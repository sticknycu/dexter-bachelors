import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.64.0)",
    comments = "Source: image_generator.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ImageGeneratorGrpc {

  private ImageGeneratorGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ImageGenerator";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ImageGeneratorOuterClass.ImageRequest,
      ImageGeneratorOuterClass.ImageResponse> getGenerateImageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateImage",
      requestType = ImageGeneratorOuterClass.ImageRequest.class,
      responseType = ImageGeneratorOuterClass.ImageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ImageGeneratorOuterClass.ImageRequest,
      ImageGeneratorOuterClass.ImageResponse> getGenerateImageMethod() {
    io.grpc.MethodDescriptor<ImageGeneratorOuterClass.ImageRequest, ImageGeneratorOuterClass.ImageResponse> getGenerateImageMethod;
    if ((getGenerateImageMethod = ImageGeneratorGrpc.getGenerateImageMethod) == null) {
      synchronized (ImageGeneratorGrpc.class) {
        if ((getGenerateImageMethod = ImageGeneratorGrpc.getGenerateImageMethod) == null) {
          ImageGeneratorGrpc.getGenerateImageMethod = getGenerateImageMethod =
              io.grpc.MethodDescriptor.<ImageGeneratorOuterClass.ImageRequest, ImageGeneratorOuterClass.ImageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateImage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ImageGeneratorOuterClass.ImageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ImageGeneratorOuterClass.ImageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ImageGeneratorMethodDescriptorSupplier("GenerateImage"))
              .build();
        }
      }
    }
    return getGenerateImageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ImageGeneratorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ImageGeneratorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ImageGeneratorStub>() {
        @java.lang.Override
        public ImageGeneratorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ImageGeneratorStub(channel, callOptions);
        }
      };
    return ImageGeneratorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ImageGeneratorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ImageGeneratorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ImageGeneratorBlockingStub>() {
        @java.lang.Override
        public ImageGeneratorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ImageGeneratorBlockingStub(channel, callOptions);
        }
      };
    return ImageGeneratorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ImageGeneratorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ImageGeneratorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ImageGeneratorFutureStub>() {
        @java.lang.Override
        public ImageGeneratorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ImageGeneratorFutureStub(channel, callOptions);
        }
      };
    return ImageGeneratorFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void generateImage(ImageGeneratorOuterClass.ImageRequest request,
        io.grpc.stub.StreamObserver<ImageGeneratorOuterClass.ImageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateImageMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ImageGenerator.
   */
  public static abstract class ImageGeneratorImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ImageGeneratorGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ImageGenerator.
   */
  public static final class ImageGeneratorStub
      extends io.grpc.stub.AbstractAsyncStub<ImageGeneratorStub> {
    private ImageGeneratorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImageGeneratorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ImageGeneratorStub(channel, callOptions);
    }

    /**
     */
    public void generateImage(ImageGeneratorOuterClass.ImageRequest request,
        io.grpc.stub.StreamObserver<ImageGeneratorOuterClass.ImageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateImageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ImageGenerator.
   */
  public static final class ImageGeneratorBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ImageGeneratorBlockingStub> {
    private ImageGeneratorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImageGeneratorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ImageGeneratorBlockingStub(channel, callOptions);
    }

    /**
     */
    public ImageGeneratorOuterClass.ImageResponse generateImage(ImageGeneratorOuterClass.ImageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateImageMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ImageGenerator.
   */
  public static final class ImageGeneratorFutureStub
      extends io.grpc.stub.AbstractFutureStub<ImageGeneratorFutureStub> {
    private ImageGeneratorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImageGeneratorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ImageGeneratorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ImageGeneratorOuterClass.ImageResponse> generateImage(
        ImageGeneratorOuterClass.ImageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateImageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_IMAGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_IMAGE:
          serviceImpl.generateImage((ImageGeneratorOuterClass.ImageRequest) request,
              (io.grpc.stub.StreamObserver<ImageGeneratorOuterClass.ImageResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGenerateImageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ImageGeneratorOuterClass.ImageRequest,
              ImageGeneratorOuterClass.ImageResponse>(
                service, METHODID_GENERATE_IMAGE)))
        .build();
  }

  private static abstract class ImageGeneratorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ImageGeneratorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ImageGeneratorOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ImageGenerator");
    }
  }

  private static final class ImageGeneratorFileDescriptorSupplier
      extends ImageGeneratorBaseDescriptorSupplier {
    ImageGeneratorFileDescriptorSupplier() {}
  }

  private static final class ImageGeneratorMethodDescriptorSupplier
      extends ImageGeneratorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ImageGeneratorMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ImageGeneratorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ImageGeneratorFileDescriptorSupplier())
              .addMethod(getGenerateImageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
