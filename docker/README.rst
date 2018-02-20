.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. role:: raw-latex(raw)
   :format: latex
..

The **Verigraph** services can be run on **Docker** containers by using the Dockerfiles bundled in the project.

If it's the first time that you use **Docker**, please follow `this guide <https://docs.docker.com/get-started/>`_ first.
You can follow these instructions to build separate Docker images for its REST service and gRPC service:

**REST**

- Build the WAR containing the Verigraph REST service as detailed `here <https://github.com/netgroup-polito/verigraph/blob/master/README.rst>`_
- Run the ``docker-rest-build`` target contained into the ``build.xml`` Ant script
- With a terminal navigate to the folder containing the REST Dockerfile ``/verigraph/docker/REST``
- After having started the Docker daemon, run the following command: ``docker build -t verigraph .``
- Now that the image is built, you can run it from anywhere. For example for running the image in a local container: ``docker run -p 8080:8080 verigraph``

**gRPC**

- Build Verigraph from your IDE
- Generate a runnable JAR starting from the ``it.polito.verigraph.grpc.server.Service`` class with the required libraries in a sub-folder next to the JAR. You can do that with the provided Ant script or manually:

 - Run the ``docker-grpc-build`` target contained into the ``build.xml`` Ant script
 - Check that the folder ``service_lib`` containing all the required JARs, the folder ``jsonschema`` and the files ``server.properties`` and ``service.jar`` are included into the ``/verigraph/docker/gRPC`` folder

 **OR**

 - With Eclipse:

  - Create a launch configuration for the gRPC service ``it.polito.verigraph.grpc.server.Service``
  - File > Export... > Runnable JAR file > select the previously created launch configuration > select as export destination the folder containing the gRPC Dockerfile ``/verigraph/docker/gRPC`` > select copy required libraries into a sub-folder. Check that the created folder containing the JARs is called ``service_lib``
  - Copy the folder ``/verigraph/jsonschema`` and the file ``/verigraph/server.properties`` to ``/verigraph/docker/gRPC``

- With a terminal navigate to the folder containing the gRPC Dockerfile ``/verigraph/docker/gRPC``
- After having started the Docker daemon, run the following command: ``docker build -t verigraph_grpc .``
- Now that the image is built, you can run it from anywhere. For example for running the image in a local container: ``docker run -p 50051:50051 verigraph_grpc``

----

**Changing the versions of Ubuntu, Tomcat, Z3**

If you want to change the Ubuntu, Tomcat and Z3 versions bundled in the images, you can change the following in the Dockerfiles (both for gRPC and REST):

- ``FROM ubuntu:version_tag``: replace *version_tag* with your chosen Ubuntu version
- ``ENV UBUNTU_VERSION x.x``: replace *x.x* with your chosen Ubuntu version
- ``ENV TOMCAT_VERSION x.x.x``: replace *x.x.x* with your chosen Tomcat version
- ``ENV Z3_VERSION x.x.x``: replace *x.x.x* with your chosen Z3 version

Please notice that by default Verigraph uses Z3 4.5.0 which is already compiled and tested with Ubuntu 14.04.
