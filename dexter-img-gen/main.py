import os
import cv2
import grpc
import numpy as np
import tensorflow as tf
import logging
from concurrent import futures
import matplotlib.pyplot as plt

from protobuf.image_generator_pb2 import ImageResponse
from protobuf.image_generator_pb2_grpc import add_ImageGeneratorServicer_to_server, ImageGeneratorServicer

# Set up logging
logging.basicConfig(level=logging.DEBUG)

BUFFER_SIZE = 400
BATCH_SIZE = 1
IMG_WIDTH = 256
IMG_HEIGHT = 256
OUTPUT_CHANNELS = 3
LAMBDA = 100

def load(image_file):
    image = tf.io.read_file(image_file)
    image = tf.io.decode_jpeg(image)
    input_image = tf.cast(image, tf.float32)
    return input_image


def generate_canny_edges(image):
    def canny_edge_detector(image_np):
        image_np = np.array(image_np)
        edges = cv2.Canny(image_np, 100, 200)
        return edges

    image = tf.image.rgb_to_grayscale(image)
    image = tf.image.resize(image, [IMG_HEIGHT, IMG_WIDTH])
    image = tf.image.convert_image_dtype(image, tf.uint8)

    # Ensure the image tensor has the correct shape before passing it to py_function
    image = tf.reshape(image, [IMG_HEIGHT, IMG_WIDTH])

    edges = tf.py_function(canny_edge_detector, [image], tf.uint8)

    # Manually set the shape of the tensor
    edges.set_shape([IMG_HEIGHT, IMG_WIDTH])

    # Debug statement for edges shape
    logging.debug(f'Edges shape before reshaping: {edges.shape}')

    edges = tf.reshape(edges, [IMG_HEIGHT, IMG_WIDTH, 1])

    # Debug statement for edges shape after reshaping
    logging.debug(f'Edges shape after reshaping: {edges.shape}')

    edges = tf.cast(edges, tf.float32)  # Cast to float32
    edges = tf.image.grayscale_to_rgb(edges)

    return edges


def load_image_train(image_file):
    input_image = load(image_file)
    edge_image = generate_canny_edges(input_image)
    edge_image, input_image = random_jitter(edge_image, input_image)
    edge_image, input_image = normalize(edge_image, input_image)
    return edge_image, input_image


def load_image_test(image_tensor):
    edge_image = generate_canny_edges(image_tensor)

    # Debug statement for image shapes
    logging.debug(f'Image tensor shape: {image_tensor.shape}')
    logging.debug(f'Edge image shape: {edge_image.shape}')

    edge_image, image_tensor = resize(edge_image, image_tensor, IMG_HEIGHT, IMG_WIDTH)
    edge_image, image_tensor = normalize(edge_image, image_tensor)
    return edge_image, image_tensor

def resize(edge_image, input_image, height, width):
    edge_image = tf.image.resize(edge_image, [height, width], method=tf.image.ResizeMethod.NEAREST_NEIGHBOR)
    input_image = tf.image.resize(input_image, [height, width], method=tf.image.ResizeMethod.NEAREST_NEIGHBOR)
    return edge_image, input_image

def random_crop(edge_image, input_image):
    stacked_image = tf.stack([edge_image, input_image], axis=0)
    cropped_image = tf.image.random_crop(stacked_image, size=[2, IMG_HEIGHT, IMG_WIDTH, 3])
    return cropped_image[0], cropped_image[1]

def normalize(edge_image, input_image):
    edge_image = (edge_image / 127.5) - 1
    input_image = (input_image / 127.5) - 1
    return edge_image, input_image

@tf.function
def random_jitter(edge_image, input_image):
    edge_image, input_image = resize(edge_image, input_image, 286, 286)
    edge_image, input_image = random_crop(edge_image, input_image)
    if tf.random.uniform(()) > 0.5:
        edge_image = tf.image.flip_left_right(edge_image)
        input_image = tf.image.flip_left_right(input_image)
    return edge_image, input_image

def downsample(filters, size, apply_batchnorm=True):
    initializer = tf.random_normal_initializer(0., 0.02)
    result = tf.keras.Sequential()
    result.add(
        tf.keras.layers.Conv2D(filters, size, strides=2, padding='same', kernel_initializer=initializer, use_bias=False))
    if apply_batchnorm:
        result.add(tf.keras.layers.BatchNormalization())
    result.add(tf.keras.layers.LeakyReLU())
    return result

def upsample(filters, size, apply_dropout=False):
    initializer = tf.random_normal_initializer(0., 0.02)
    result = tf.keras.Sequential()
    result.add(
        tf.keras.layers.Conv2DTranspose(filters, size, strides=2, padding='same', kernel_initializer=initializer, use_bias=False))
    result.add(tf.keras.layers.BatchNormalization())
    if apply_dropout:
        result.add(tf.keras.layers.Dropout(0.5))
    result.add(tf.keras.layers.ReLU())
    return result

def Generator():
    inputs = tf.keras.layers.Input(shape=[256, 256, 3])
    down_stack = [
        downsample(64, 4, apply_batchnorm=False),
        downsample(128, 4),
        downsample(256, 4),
        downsample(512, 4),
        downsample(512, 4),
        downsample(512, 4),
        downsample(512, 4),
        downsample(512, 4),
    ]
    up_stack = [
        upsample(512, 4, apply_dropout=True),
        upsample(512, 4, apply_dropout=True),
        upsample(512, 4, apply_dropout=True),
        upsample(512, 4),
        upsample(256, 4),
        upsample(128, 4),
        upsample(64, 4),
    ]
    initializer = tf.random_normal_initializer(0., 0.02)
    last = tf.keras.layers.Conv2DTranspose(OUTPUT_CHANNELS, 4, strides=2, padding='same', kernel_initializer=initializer, activation='tanh')
    x = inputs
    skips = []
    for down in down_stack:
        x = down(x)
        skips.append(x)
    skips = reversed(skips[:-1])
    for up, skip in zip(up_stack, skips):
        x = up(x)
        x = tf.keras.layers.Concatenate()([x, skip])
    x = last(x)
    return tf.keras.Model(inputs=inputs, outputs=x)

generator = Generator()
loss_object = tf.keras.losses.BinaryCrossentropy(from_logits=True)

def generator_loss(disc_generated_output, gen_output, target):
    gan_loss = loss_object(tf.ones_like(disc_generated_output), disc_generated_output)
    l1_loss = tf.reduce_mean(tf.abs(target - gen_output))
    total_gen_loss = gan_loss + (LAMBDA * l1_loss)
    return total_gen_loss, gan_loss, l1_loss

def Discriminator():
    initializer = tf.random_normal_initializer(0., 0.02)
    inp = tf.keras.layers.Input(shape=[256, 256, 3], name='input_image')
    tar = tf.keras.layers.Input(shape=[256, 256, 3], name='target_image')
    x = tf.keras.layers.concatenate([inp, tar])
    down1 = downsample(64, 4, False)(x)
    down2 = downsample(128, 4)(down1)
    down3 = downsample(256, 4)(down2)
    zero_pad1 = tf.keras.layers.ZeroPadding2D()(down3)
    conv = tf.keras.layers.Conv2D(512, 4, strides=1, kernel_initializer=initializer, use_bias=False)(zero_pad1)
    batchnorm1 = tf.keras.layers.BatchNormalization()(conv)
    leaky_relu = tf.keras.layers.LeakyReLU()(batchnorm1)
    zero_pad2 = tf.keras.layers.ZeroPadding2D()(leaky_relu)
    last = tf.keras.layers.Conv2D(1, 4, strides=1, kernel_initializer=initializer)(zero_pad2)
    return tf.keras.Model(inputs=[inp, tar], outputs=last)

discriminator = Discriminator()

def discriminator_loss(disc_real_output, disc_generated_output):
    real_loss = loss_object(tf.ones_like(disc_real_output), disc_real_output)
    generated_loss = loss_object(tf.zeros_like(disc_generated_output), disc_generated_output)
    total_disc_loss = real_loss + generated_loss
    return total_disc_loss

generator_optimizer = tf.keras.optimizers.legacy.Adam(2e-4, beta_1=0.5)
discriminator_optimizer = tf.keras.optimizers.legacy.Adam(2e-4, beta_1=0.5)
checkpoint_dir = './training_checkpoints'
checkpoint_prefix = os.path.join(checkpoint_dir, "ckpt")
checkpoint = tf.train.Checkpoint(generator_optimizer=generator_optimizer,
                                 discriminator_optimizer=discriminator_optimizer,
                                 generator=generator,
                                 discriminator=discriminator)

# Restore latest checkpoint
checkpoint.restore(tf.train.latest_checkpoint(checkpoint_dir))

def generate_images(model, test_input, tar):
    prediction = model(test_input, training=True)
    plt.figure(figsize=(15, 15))

    display_list = [test_input[0], tar[0], prediction[0]]

    title = ['Input Image', 'Ground Truth', 'Predicted Image']

    for i in range(len(display_list)):
        plt.subplot(1, 3, i + 1)
        plt.title(title[i])
        # Remove the batch dimension for displaying
        plt.imshow(tf.squeeze(display_list[i]) * 0.5 + 0.5)
        plt.axis('off')
    plt.show()
    return display_list


# log_dir="logs/"
# summary_writer = tf.compat.v1.summary.create_file_writer(log_dir + "fit/" + datetime.datetime.now().strftime("%Y%m%d-%H%M%S"))


import io
from PIL import Image as ImgPIL
import base64

class ImageGeneratorServicerImpl(ImageGeneratorServicer):
    def __init__(self, model):
        self.model = model

    def GenerateImage(self, request, context):
        logging.debug("Received GenerateImage request")
        image_data = base64.b64decode(request.image_data)
        image_data = np.frombuffer(image_data, dtype=np.uint8)
        # Decode image data into a NumPy array
        try:
            btes = io.BytesIO(image_data)
            btes.seek(0)
            image_np = ImgPIL.open(btes)
            image_np = np.array(image_np)
        except Exception as e:
            logging.error(f"Failed to open image: {e}")
            return None

        # Check if the image is single-channel and convert to RGB if necessary
        if len(image_np.shape) == 2:  # Grayscale image
            image_pil = ImgPIL.fromarray(image_np).convert('RGB')
            image_np = np.array(image_pil)
        elif len(image_np.shape) == 3 and image_np.shape[2] == 1:  # Single-channel image
            image_pil = ImgPIL.fromarray(image_np.squeeze(), mode='L').convert('RGB')
            image_np = np.array(image_pil)


        # Convert to tensor and normalize
        image = tf.convert_to_tensor(image_np, dtype=tf.float32)  # Convert to tensor
        image = image / 255.0

        # Debug statement for original image shape
        # logging.debug(f'Original image shape: {image.shape}')

        # Resize image to the required dimensions
        image = tf.image.resize(image, [IMG_HEIGHT, IMG_WIDTH])

        # Debug statement for resized image shape
        # logging.debug(f'Resized image shape: {image.shape}')

        # Expand dimensions to create a batch of size 1
        image = tf.expand_dims(image, axis=0)

        # Debug statement for batch image shape
        # logging.debug(f'Batch image shape: {image.shape}')

        # Process the image tensor directly
        edge_image, input_image = load_image_test(image)

        # Ensure edge_image and input_image have a batch dimension
        edge_image = tf.expand_dims(edge_image, axis=0)
        input_image = tf.expand_dims(input_image, axis=0)

        # Debug statement for batched image shapes
        # logging.debug(f'Batched edge image shape: {edge_image.shape}')
        # logging.debug(f'Batched input image shape: {input_image.shape}')

        # Generate images
        display_list = generate_images(generator, edge_image, input_image)
        generated_edge_image = display_list[0]
        generated_output_image = display_list[2]

        # Convert tensors to byte strings
        canny_edge = cv2.imencode('.jpg', (tf.squeeze(generated_edge_image).numpy() * 0.5 + 0.5) * 255)[1].tobytes()
        generated_image = cv2.imencode('.jpg', (tf.squeeze(generated_output_image).numpy() * 0.5 + 0.5) * 255)[1].tobytes()

        logging.debug("Processed GenerateImage request")
        return ImageResponse(canny_edge=canny_edge, generated_image=generated_image)



def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    add_ImageGeneratorServicer_to_server(ImageGeneratorServicerImpl(generator), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    logging.info("Server started, listening on port 50051")
    server.wait_for_termination()


if __name__ == '__main__':
    # generator.summary()
    #
    # discriminator.summary()
    #
    # # Salvează diagrama arhitecturii modelului generator
    # tf.keras.utils.plot_model(generator, to_file='./generator_architecture.png', show_shapes=True, dpi=64)
    #
    # # Salvează diagrama arhitecturii modelului discriminator
    # tf.keras.utils.plot_model(discriminator, to_file='./discriminator_architecture.png', show_shapes=True, dpi=64)
    #
    # # Afișează diagramele cu matplotlib
    # generator_img = plt.imread('./generator_architecture.png')
    # discriminator_img = plt.imread('./discriminator_architecture.png')
    #
    # plt.figure(figsize=(10, 10))
    #
    # plt.subplot(1, 2, 1)
    # plt.title('Generator Architecture')
    # plt.imshow(generator_img)
    # plt.axis('off')
    #
    # plt.subplot(1, 2, 2)
    # plt.title('Discriminator Architecture')
    # plt.imshow(discriminator_img)
    # plt.axis('off')


    # plt.show()
    # breakpoint()
    serve()
