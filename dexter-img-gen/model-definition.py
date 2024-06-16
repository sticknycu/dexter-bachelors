import cv2
import os
import tensorflow as tf
import numpy as np
from matplotlib import pyplot as plt
from IPython import display
import time
import datetime


save = True

# Directories
train_image_dir = "./art_gallery/Abstract_gallery_2/Abstract_gallery_2/"
test_image_dir = "./art_gallery/Abstract_gallery/Abstract_gallery/"

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
    edges = tf.py_function(canny_edge_detector, [image], tf.uint8)
    edges = tf.reshape(edges, [IMG_HEIGHT, IMG_WIDTH, 1])
    edges = tf.cast(edges, tf.float32)  # Cast to float32
    edges = tf.image.grayscale_to_rgb(edges)
    return edges

def load_image_train(image_file):
    input_image = load(image_file)
    edge_image = generate_canny_edges(input_image)
    edge_image, input_image = random_jitter(edge_image, input_image)
    edge_image, input_image = normalize(edge_image, input_image)
    return edge_image, input_image

def load_image_test(image_file):
    input_image = load(image_file)
    edge_image = generate_canny_edges(input_image)
    edge_image, input_image = resize(edge_image, input_image, IMG_HEIGHT, IMG_WIDTH)
    edge_image, input_image = normalize(edge_image, input_image)
    return edge_image, input_image

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

train_image_files = [os.path.join(train_image_dir, f) for f in os.listdir(train_image_dir) if f.endswith('.jpg') or f.endswith('.png')]

train_dataset = tf.data.Dataset.from_tensor_slices(train_image_files)
train_dataset = train_dataset.map(load_image_train, num_parallel_calls=tf.data.AUTOTUNE)
train_dataset = train_dataset.shuffle(BUFFER_SIZE)
train_dataset = train_dataset.batch(BATCH_SIZE)

test_image_files = [os.path.join(test_image_dir, f) for f in os.listdir(test_image_dir) if f.endswith('.jpg') or f.endswith('.png')]
test_image_files = test_image_files[:50]  # Limit to 50 images

test_dataset = tf.data.Dataset.from_tensor_slices(test_image_files)
test_dataset = test_dataset.map(load_image_test)
test_dataset = test_dataset.batch(BATCH_SIZE)

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
tf.keras.utils.plot_model(generator, show_shapes=True, dpi=64)

inp = load(train_image_files[0])
inp_resized = tf.image.resize(inp, [IMG_HEIGHT, IMG_WIDTH])  # Resize the input image to 256x256
edge = generate_canny_edges(inp_resized)
gen_output = generator(edge[tf.newaxis, ...], training=False)

plt.figure(figsize=(12, 6))
plt.subplot(1, 2, 1)
plt.title('Canny Edge Image')
plt.imshow(edge / 255.0)  # Plot the Canny edge image
plt.subplot(1, 2, 2)
plt.title('Generated Image')
plt.imshow((gen_output[0] + 1) / 2)  # Plot the generated image
plt.show()

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
tf.keras.utils.plot_model(discriminator, show_shapes=True, dpi=64)
disc_out = discriminator([edge[tf.newaxis, ...], gen_output], training=False)
plt.imshow(disc_out[0, ..., -1], vmin=-20, vmax=20, cmap='RdBu_r')
plt.colorbar()

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

if save:
    # Restore the latest checkpoint
    status = checkpoint.restore(tf.train.latest_checkpoint(checkpoint_dir))

    # Wait for the restore to finish (if needed)
    status.assert_existing_objects_matched()

    # Define the export directory for the SavedModel
    export_dir = './saved_model/'

    # Save the generator model as a SavedModel
    tf.saved_model.save(generator, export_dir)

    print(f"Model saved to {export_dir}")

    exit(0)

def save_model_config(model, filename):
    model_json = model.to_json()
    with open(filename, 'w') as json_file:
        json_file.write(model_json)

# Save generator model configuration
save_model_config(generator, os.path.join(checkpoint_dir, "generatorConfig.json"))

# Save discriminator model configuration
save_model_config(discriminator, os.path.join(checkpoint_dir, "discriminatorConfig.json"))

def generate_images(model, test_input, tar):
    prediction = model(test_input, training=True)
    plt.figure(figsize=(15, 15))
    display_list = [test_input[0], tar[0], prediction[0]]
    title = ['Canny Edge Image', 'Ground Truth', 'Predicted Image']
    for i in range(3):
        plt.subplot(1, 3, i+1)
        plt.title(title[i])
        plt.imshow(display_list[i] * 0.5 + 0.5)
        plt.axis('off')
    plt.show()

# Test the model on 50 images from the specified directory
for edge_image, input_image in test_dataset.take(50):
    generate_images(generator, edge_image, input_image)

log_dir="logs/"
summary_writer = tf.compat.v1.summary.create_file_writer(log_dir + "fit/" + datetime.datetime.now().strftime("%Y%m%d-%H%M%S"))

@tf.function
def train_step(input_image, target, step):
    with tf.GradientTape() as gen_tape, tf.GradientTape() as disc_tape:
        gen_output = generator(input_image, training=True)
        disc_real_output = discriminator([input_image, target], training=True)
        disc_generated_output = discriminator([input_image, gen_output], training=True)
        gen_total_loss, gen_gan_loss, gen_l1_loss = generator_loss(disc_generated_output, gen_output, target)
        disc_loss = discriminator_loss(disc_real_output, disc_generated_output)
    generator_gradients = gen_tape.gradient(gen_total_loss, generator.trainable_variables)
    discriminator_gradients = disc_tape.gradient(disc_loss, discriminator.trainable_variables)
    generator_optimizer.apply_gradients(zip(generator_gradients, generator.trainable_variables))
    discriminator_optimizer.apply_gradients(zip(discriminator_gradients, discriminator.trainable_variables))
    with summary_writer.as_default():
        tf.summary.scalar('gen_total_loss', gen_total_loss, step=step//1000)
        tf.summary.scalar('gen_gan_loss', gen_gan_loss, step=step//1000)
        tf.summary.scalar('gen_l1_loss', gen_l1_loss, step=step//1000)
        tf.summary.scalar('disc_loss', disc_loss, step=step//1000)

def fit(train_ds, test_ds, steps):
    example_input = next(iter(test_ds.take(1)))
    example_input, example_target = example_input
    start = time.time()
    for step, (input_image, target) in train_ds.repeat().take(steps).enumerate():
        if (step) % 1000 == 0:
            display.clear_output(wait=True)
            if step != 0:
                print(f'Time taken for 1000 steps: {time.time()-start:.2f} sec\n')
            start = time.time()
            generate_images(generator, example_input, example_target)
            print(f"Step: {step//1000}k")
        train_step(input_image, target, step)
        if (step+1) % 10 == 0:
            print('.', end='', flush=True)
        if (step + 1) % 5000 == 0:
            checkpoint.save(file_prefix=checkpoint_prefix)

fit(train_dataset, test_dataset, steps=40000)
