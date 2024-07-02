# Use the official Python image as a parent image
FROM python:3.8-slim

# Set the working directory inside the container
WORKDIR /app

# Install system dependencies required by OpenCV
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libglib2.0-0

# Copy the requirements.txt file and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the protobuf definition file
COPY protobuf/ protobuf/
COPY saved_model/ saved_model/
COPY training_checkpoints/ training_checkpoints/

# Generate the protobuf files
RUN python -m grpc_tools.protoc -Iprotobuf --python_out=. --grpc_python_out=. protobuf/image_generator.proto

# Copy the main application file
COPY main.py .
COPY requirements.txt .

# Expose the port the app runs on
EXPOSE 50051

# Run the application
CMD ["python", "main.py"]
