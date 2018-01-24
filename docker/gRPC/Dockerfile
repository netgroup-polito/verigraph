FROM ubuntu:latest

RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install openjdk-8-jdk wget
RUN apt-get -y install libgomp1
RUN apt-get -y install unzip

RUN mkdir /usr/src/verigraph/

RUN mkdir /usr/src/verigraph/z3
RUN wget https://github.com/Z3Prover/z3/releases/download/z3-4.5.0/z3-4.5.0-x64-ubuntu-14.04.zip -O /tmp/z3-4.5.0-x64-ubuntu-14.04.zip
RUN cd /tmp && unzip z3-4.5.0-x64-ubuntu-14.04.zip
RUN cp -Rv /tmp/z3-4.5.0-x64-ubuntu-14.04/bin/* /usr/src/verigraph/z3

ADD ./service_lib /usr/src/verigraph/service_lib
ADD ./jsonschema /usr/src/verigraph/jsonschema
ADD service.jar /usr/src/verigraph/
ADD server.properties /usr/src/verigraph

ENV LD_LIBRARY_PATH="/usr/src/verigraph/z3:${LD_LIBRARY_PATH}"

WORKDIR /usr/src/verigraph/

EXPOSE 50051

CMD java -cp /usr/src/verigraph/service_lib/*:. -jar ./service.jar