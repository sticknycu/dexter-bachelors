### Generate protobuf:

```bash
python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. protobuf/image_generator.proto
```