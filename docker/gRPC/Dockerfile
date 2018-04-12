# Ubuntu 14.04 for compatibility with Z3 4.5.0
FROM ubuntu:14.04

# Change these environment variables to select other versions
ENV UBUNTU_VERSION 14.04
ENV Z3_VERSION 4.5.0

RUN apt-get -y update && apt-get -y upgrade

# Not needed for Ubuntu version greater than 16.04 (OpenJDK already included in default repositories)
RUN apt-get -y install software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa && apt-get -y update

# Install OpenJDK, wget, libgomp1 (needed by Z3) and unzip
RUN apt-get -y install openjdk-8-jdk wget libgomp1 unzip

# Install Z3
RUN mkdir /z3
RUN wget https://github.com/Z3Prover/z3/releases/download/z3-$Z3_VERSION/z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION.zip -O /tmp/z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION.zip
RUN cd /tmp && unzip z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION.zip
RUN cp -Rv /tmp/z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION/bin/* /z3

# Add files needed to run gRPC Verigraph server to container
RUN mkdir /verigraph
ADD ./service_lib /verigraph/service_lib
ADD ./jsonschema /verigraph/jsonschema
ADD service.jar /verigraph/
ADD server.properties /verigraph

# Set environment variable for loading Z3 native library
ENV LD_LIBRARY_PATH="/z3:${LD_LIBRARY_PATH}"

WORKDIR /verigraph

# Make port 50051 (used by Verigraph gRPC server) available to the world outside this container
EXPOSE 50051

# Start Verigraph gRPC server process
CMD java -cp /verigraph/service_lib/*:. -jar ./service.jar