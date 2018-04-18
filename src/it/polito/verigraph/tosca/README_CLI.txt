** Verigraph Verification Service - Command Line Interface **

The CLI allows to interact with the Verigraph Verification Service using both the RESTful and gRPC interface.
The CLI allows CRUD operations on graphs and three kind of verification.


-- Available commands --
> CONFIGURE -use <Interface> -format <contentFormat> -port <servicePort> -host <hostname>
> HELP
> EXIT
> GETALL
> GET <graphId>
> CREATE <validFilePath>
> UPDATE <graphId> <validFilePath>
> DELETE <graphId>
> VERIFY <graphId> <verificationType> <sourceNode> <destinationNode> [ <middleboxNode> ]


-- CLI commands --
> CONFIGURE -use <Interface> -format <contentFormat>
Allows to configure connection parameters (host and port ), the interface (REST or gRPC ) and the data format
(JSON, XML or YAML ) to be used to communicate with the verification service, XML and YAML formats exploit an extension
of TOSCA specification. At program start the default configuration uses the REST interface with XML data format. 
Note that the JSON format is not supported by the grpc interface.

> HELP
Prints on screen the CLI documentation.

> EXIT
Closes REST/gRPC client and exits.


-- CRUD on graphs --
> GETALL
Performs a request without specifying a particular graph id. The service will return
a list of graph templates in the currently selected format and will print them on screen. 

> GET <graphId>
Performs a request for a specific graph whose id MUST be specified as a long integer
value. If present the graph will be returned and printed on screen in the currently selected format.

> CREATE <validFilePath>
Performs a create graph request providing a graph template as a file.
The <validFilePath> must point to an existing file  whose filenme must be coherent with the currently selected 
data format. The server can accept or not the provided graph upon its validation against Tosca Verigraph specification
for XML/YAML or against Verigraph JSON schema. For further info see Verigraph Service documentation at [...].

> UPDATE <graphId> <validFilePath>
Performs an update request for a specific graph providing a graph template as a file.
The provided filename must be coherent with the currently selected data format and the <LongId> must be a long integer 
corresponding to one of the graphs previously created. The server can accept or not the provided graph upon its 
validation against Tosca Verigraph specification for XML/YAML or against Verigraph JSON schema.
For further info see Verigraph Service documentation at [...].

> DELETE <graphId>
Performs a delete request for a specific graph.
The provided id must be a long integer corresponding to a previously created graph.


-- Verification --
> VERIFY <graphId> <verificationType> <sourceNode> <destinationNode> [ <middleboxNode> ]
Performs a verification request for a specific graph.
The <graphId> must be the long integer id of a previously created graph. Three types of verification services are
available: reachability, isolation and traversal. The source, destination and middlebox node parameters 
must be provided as string and must correspond to the name of a Node in the specified graph. The middlebox parameter
must not be provided in case of reachability verification.