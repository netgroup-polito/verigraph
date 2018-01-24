FROM ubuntu:latest

RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install openjdk-8-jdk wget
RUN apt-get -y install libgomp1
RUN apt-get -y install unzip

RUN mkdir /usr/local/tomcat
RUN wget http://www-us.apache.org/dist/tomcat/tomcat-8/v8.5.24/bin/apache-tomcat-8.5.24.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz
RUN cp -Rv /tmp/apache-tomcat-8.5.24/* /usr/local/tomcat/

RUN mkdir /usr/local/tomcat/shared
RUN wget https://github.com/Z3Prover/z3/releases/download/z3-4.5.0/z3-4.5.0-x64-ubuntu-14.04.zip -O /tmp/z3-4.5.0-x64-ubuntu-14.04.zip
RUN cd /tmp && unzip z3-4.5.0-x64-ubuntu-14.04.zip
RUN cp -Rv /tmp/z3-4.5.0-x64-ubuntu-14.04/bin/* /usr/local/tomcat/shared

RUN touch /usr/local/tomcat/bin/setenv.sh
RUN echo "#!/bin/sh\n" >> /usr/local/tomcat/bin/setenv.sh
RUN echo export LD\_LIBRARY\_PATH\=\$LD\_LIBRARY\_PATH\:\$CATALINA\_HOME\/shared\; export CATALINA\_OPTS\=\"\$CATALINA\_OPTS \-Djava\.library\.path\=\$LD\_LIBRARY\_PATH\" >> /usr/local/tomcat/bin/setenv.sh

ADD verigraph.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD /usr/local/tomcat/bin/catalina.sh run