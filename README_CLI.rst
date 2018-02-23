.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. role:: raw-latex(raw)
   :format: latex
..

The command line interface allows to interact with **Verigraph** using both the **REST** and **gRPC** interfaces. The CLI allows CRUD operations on graphs and verification of multiple types of policies.

You can generate a JAR for executing the CLI with the following steps:

- Run the ``build-cli-jar`` target contained into the ``build.xml`` Ant script
- With a terminal navigate to the folder containing generated JAR file ``/verigraph/VerigraphCLI/``
- Execute the command ``java -jar VerigraphCLI.jar`` for running the Commnad Line Interface

**Available commands**

- ``CONFIGURE -use <Interface> -format <contentFormat> -port <servicePort> -host <hostname>``
- ``HELP``
- ``EXIT``
- ``GETALL``
- ``GET <graphId>``
- ``CREATE <validFilePath>``
- ``UPDATE <graphId> <validFilePath>``
- ``DELETE <graphId>``
- ``VERIFY <graphId> <verificationType> <sourceNode> <destinationNode> <middleboxNode>``


**CLI commands**

``CONFIGURE -use <Interface> -format <contentFormat> -port <servicePort> -host <hostname>``

	Allows to configure connection parameters (host and port ), the interface (REST or gRPC ) and the data format (JSON, YAML or XML) to be used to communicate with the verification service, XML and YAML formats exploit an extension of **TOSCA** specification. At program start the default configuration uses the REST interface with XML data format. *Note that the JSON format is not supported by the gRPC interface*.

``HELP``

	Prints on screen the CLI documentation.

``EXIT``

	Closes REST/gRPC client and exits.

**CRUD on graphs**

``GETALL``

	Performs a request without specifying a particular graph id. The service will return
	a list of graph templates in the currently selected format and will print them on screen. 

``GET <graphId>``

	Performs a request for a specific graph whose id MUST be specified as a long integer value. If present the graph will be returned and printed on screen in the currently selected format.

``CREATE <validFilePath>``

	Performs a create graph request providing a graph template as a file. The ``<validFilePath>`` must point to an existing file  whose filename must be coherent with the currently selected 
	data format. The server can accept or not the provided graph upon its validation against TOSCA Verigraph specification for YAML/XML or against Verigraph JSON schema. For further infos see the Verigraph `service documentation <https://github.com/netgroup-polito/verigraph/blob/tosca-support/README.rst>`__ and the Verigraph `TOSCA documentation <https://github.com/netgroup-polito/verigraph/blob/tosca-support/README.rst>`_.

``UPDATE <graphId> <validFilePath>``

	Performs an update request for a specific graph providing a graph template as a file.
	The provided filename must be coherent with the currently selected data format and the ``<LongId>`` must be a long integer corresponding to one of the *Graphs* previously created. The server can accept or not the provided graph upon its validation against TOSCA Verigraph specification for YAML/XML or against Verigraph JSON schema. For further infos see the Verigraph `service documentation <https://github.com/netgroup-polito/verigraph/blob/tosca-support/README.rst>`__ and the Verigraph `TOSCA documentation <https://github.com/netgroup-polito/verigraph/blob/tosca-support/README.rst>`_.

``DELETE <graphId>``

	Performs a delete request for a specific graph.
	The provided id must be a ``long integer`` corresponding to a previously created graph.

**Verification**

``VERIFY <graphId> <verificationType> <sourceNode> <destinationNode> <middleboxNode>``

	Performs a verification request for a specific graph.
	The ``<graphId>`` must be the long integer id of a previously created graph. Three types of verification services are available: ``reachability``, ``isolation`` and ``traversal``. The source, destination and middlebox node parameters must be provided as string and must correspond to the name of a ``Node`` in the specified graph. The middlebox parameter must not be provided in case of reachability verification.