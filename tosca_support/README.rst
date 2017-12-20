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
            <source>webeserver1</source>
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

TODO

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
