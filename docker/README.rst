.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. role:: raw-latex(raw)
   :format: latex
..

**VeriGraph** can be run on Docker containers by using the Dockerfiles bundled in the project.

Follow these instructions to build separate Docker images for its REST service and gRPC service:

**REST**

- Build the WAR containing the **VeriGraph** REST service as detailed `here <https://github.com/netgroup-polito/verigraph/blob/master/README.rst>`_
- Copy the generated ``verigraph.war`` to the folder containing the REST Dockerfile ``/verigraph/docker/REST``
- With a terminal navigate to the folder containing the REST Dockerfile ``/verigraph/docker/REST``
- After having started the Docker daemon, run the following command: ``docker build -t verigraph .``
- Now that the image is built, you can run it from anywhere. For example for running the image in a local container: ``docker run -p 8080:8080 verigraph``

**gRPC**

- Build **VeriGraph** from your IDE
- Generate a runnable JAR starting from the ``it.polito.verigraph.grpc.server.Service`` class with the required libraries in a sub-folder next to the JAR. You can do that with the provided Ant script or manually:
 - In the gRPC-Docker-build.xml Ant script modify the properties ``dir.workspace`` and ``dir.m2`` respectively with the location of the Verigraph project directory and the Maven local repository directory. **Notice:** this Ant script assumes the default JAR libraries locations with the library versions specified in the pom.xml, if you change any of these library versions the script must be modified accordingly
 - Run the ``create_run_jar`` target

 **or**

 - With Eclipse:
  - Create a launch configuration for the gRPC service ``it.polito.verigraph.grpc.server.Service``
  - File > Export... > Runnable JAR file > select the previously created launch configuration > select as export destination the folder containing the gRPC Dockerfile ``/verigraph/docker/gRPC`` > select copy required libraries into a sub-folder
- With a terminal navigate to the folder containing the gRPC Dockerfile ``/verigraph/docker/gRPC``
- After having started the Docker daemon, run the following command: ``docker build -t verigraph_grpc .``
- Now that the image is built, you can run it from anywhere. For example for running the image in a local container: ``docker run -p 50051:50051 verigraph_grpc``