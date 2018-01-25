# Ubuntu 14.04 for compatibility with Z3 4.5.0
FROM ubuntu:14.04

# Change these environment variables to select other versions
ENV UBUNTU_VERSION 14.04
ENV TOMCAT_VERSION 8.5.27
ENV Z3_VERSION 4.5.0

RUN apt-get -y update && apt-get -y upgrade

# Not needed for Ubuntu version greater than 16.04 (OpenJDK already included in default repositories)
RUN apt-get -y install software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa && apt-get -y update

# Install OpenJDK, wget, libgomp1 (needed by Z3) and unzip
RUN apt-get -y install openjdk-8-jdk wget libgomp1 unzip

# Install Apache Tomcat
RUN mkdir /usr/local/tomcat
RUN wget https://archive.apache.org/dist/tomcat/tomcat-8/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz
RUN cp -Rv /tmp/apache-tomcat-$TOMCAT_VERSION/* /usr/local/tomcat/

# Install Z3
RUN mkdir /usr/local/tomcat/shared
RUN wget https://github.com/Z3Prover/z3/releases/download/z3-$Z3_VERSION/z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION.zip -O /tmp/z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION.zip
RUN cd /tmp && unzip z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION.zip
RUN cp -Rv /tmp/z3-$Z3_VERSION-x64-ubuntu-$UBUNTU_VERSION/bin/* /usr/local/tomcat/shared

# Set environment variables for loading Z3 native library
RUN touch /usr/local/tomcat/bin/setenv.sh
RUN echo "#!/bin/sh\n" >> /usr/local/tomcat/bin/setenv.sh
RUN echo export LD\_LIBRARY\_PATH\=\$LD\_LIBRARY\_PATH\:\$CATALINA\_HOME\/shared\; export CATALINA\_OPTS\=\"\$CATALINA\_OPTS \-Djava\.library\.path\=\$LD\_LIBRARY\_PATH\" >> /usr/local/tomcat/bin/setenv.sh

# Add WAR containing Verigraph to container
ADD verigraph.war /usr/local/tomcat/webapps/

# Make port 8080 (used by Tomcat) available to the world outside this container
EXPOSE 8080

# Start Tomcat server
CMD /usr/local/tomcat/bin/catalina.sh run