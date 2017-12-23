.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. role:: raw-latex(raw)
   :format: latex
..

**VeriGraph** has been extended for **OASIS TOSCA**-based network service descriptors.
**VeriGraph** supports both **XML** and **YAML** TOSCA descriptions provided both within its RESTful APIs and via gRPC.

**TOSCA** network service descriptors are interpreted by **VeriGraph** with the following mapping (``TOSCA -> Verigraph``):

- ``Definitions -> Graph``
- ``NodeTemplate -> Node``
- ``RelationshipTemplate -> Neighbours within each Node``

In particular, a ``RelationshipTemplate`` from a ``NodeTemplate`` X to a ``NodeTemplate`` Y is mapped into the **VeriGraph** Model as Y being a neighbour of node X.

**TOSCA** has been extended for supporting **VeriGraph**-specific functional types, and node configurations can be provided as properties within each NodeTemplate.

**Example (in YAML):**

.. code:: yaml

  node_templates:
    1:
      name : firewall
      type: verigraph.nodeTypes.Firewall
      properties:
        elements:
          "host1" : "host2"

**Example (in XML):**

.. code:: xml

  <NodeTemplate id="1" name="firewall" type="FirewallType">
    <Properties>
      <Configuration confID="10" confDescr="Firewall config">
         <firewallConfiguration>
          <elements>
            <source>webserver1</source>
            <destination>host2</destination>
          </elements>
        </firewallConfiguration>
      </Configuration>
    </Properties>
  </NodeTemplate>

Complete examples of NF-FGs (in YAML and XML) can be found `here <https://github.com/netgroup-polito/verigraph/blob/tosca-support/tosca_support/examples>`__.

By deploying the **tosca_support** branch of **VeriGraph** with these `instructions <https://github.com/netgroup-polito/verigraph/blob/tosca-support/README.rst>`__, TOSCA support is already seamlessly integrated with the **VeriGraph** provided APIs.

Below are detailed the extensions made to the RESTful APIs and gRPC:

**gRPC**

VeriGraph gRPC implementation support TOSCA YAML and XML representation at the Graph resource level.
The messages used to send/receive information from/to server/client are the `following <https://github.com/netgroup-polito/verigraph/blob/tosca-support/src/main/proto/tosca_verigraph.proto>`__:

- ``TopologyTemplateGrpc``: it represents the *Graph*.
- ``NodeTemplateGrpc``: it represents the Node without its *Neighbours*.
- ``RelationshipTemplateGrpc``: it represents the *Neighbours* within each *Node*.
- ``NewTopologyTemplate``: it contains the exact *Graph* created/updated by **VeriGraph**.
- ``ToscaRequestID``: used to identify a specific *Graph*.
- ``ToscaConfigurationGrpc``: it contains the configuration of a *Node*.
- ``ToscaPolicyGrpc``: used to perform a verification.
- ``ToscaVerificationGrpc``: used to return the result of a verification.
- ``ToscaTestGrpc``: it contains a path of *Nodes* used as result of a verification.

In order to obtain a gRPC object starting from file there can be use the following static methods:

- ``XmlToGrpc.obtainTopologyTemplateGrpc(String)`` returns a TopologyTemplateGrpc from a TOSCA compliant XML filepath.
- ``XmlParsingUtils.writeDefinitionsString(Definitions)`` returns a string that contains an XML printable format of the Definions.
- ``YamlToGrpc.obtainTopologyTemplateGrpc(String)`` returns a TopologyTemplateGrpc from a TOSCA compliant YAML filepath.
- ``YamlParsingUtils.writeServiceTemplateYamlString(ServiceTemplateYaml)`` returns a string that contains a YAML printable format of the ServiceTemplateYaml.
- Moreover, others converter utility methods can be found in `this package <https://github.com/netgroup-polito/verigraph/tree/tosca-support/src/it/polito/verigraph/tosca/converter>`__

The previous messages are used with the `following <https://github.com/netgroup-polito/verigraph/blob/tosca-support/src/main/proto/tosca_verigraph.proto>`__ gRPC to perform the CRUD operation on Graphs and to verify a specific policy:

- ``GetTopologyTemplates(GetRequest)``: returns a list that contains all the *Graphs* stored in **VeriGraph** (as *TopologyTemplateGrpc*).
- ``GetTopologyTemplate(ToscaRequestID)``: returns the *Graph* with the specific ID provided (as *TopologyTemplateGrpc*).
- ``CreateTopologyTemplate(TopologyTemplateGrpc)``: sends to **VeriGraph** a *Graph* (as *TopologyTemplateGrpc*) and returns the *Graph* (as *NewTopologyTemplate*) as it has been created by **VeriGraph**.
- ``DeleteTopologyTemplate(ToscaRequestID)``: deletes the *Graph* with the specific ID provided from **VeriGraph**.
- ``UpdateTopologyTemplate(TopologyTemplateGrpc)``: sends to **VeriGraph** an update of the *Graph* (as *TopologyTemplateGrpc*) and returns the *Graph* updated (as *NewTopologyTemplate*).
- ``VerifyPolicy(ToscaPolicy)``: send a *ToscaPolicy* to **VeriGraph** and returns a *ToscaVerificationGrpc* that contains the result.


**REST**

**VeriGraph** RESTful APIs support TOSCA YAML and XML representations at the Graph resource level:

``/graphs``
 - ``GET``: based on the request header, the server returns all the graphs stored on Neo4j represented with the default **VeriGraph** representation (``Accept: application/json``) or with a **TOSCA** representation (``Accept: application/{x-yaml, xml}``)
 - ``POST``: the server accepts a graph represented with the default **VeriGraph** representation (``Content-Type: application/json``) or with a **TOSCA** representation (``Content-type: application/{x-yaml, xml}`` and stores it on Neo4j and returns the stored graph with the same format received
 - ``PUT``:  the server accepts a graph represented with the default **VeriGraph** representation (``Content-Type: application/json``) or with a **TOSCA** representation (``Content-type: application/{x-yaml, xml}``, updates the id-specified graph, and returns the updated graph with the same format received

``/graphs/{graphId}``
 - ``GET``: based on the request header, the server returns the id-specified graph stored on Neo4j, represented with the formats specified below

``/graphs/{graphId}/paths``
 - ``GET``:

``/graphs/{graphId}/policy``
 - ``GET``:
