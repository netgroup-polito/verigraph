.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. role:: raw-latex(raw)
   :format: latex
..

The **YAML** and **XML** files contained in this folder are meant to be used in the body of a **POST** to the address 
``http://localhost:8080/verigraph/api/graphs`` with the ``Content-Type`` of the request set, respectively, to 
``application/xml`` or ``application/x-yaml``. Every response sent by the server will be of the same format of the 
request or in a specified one, according to the ``Accept`` header value. Some of the examples (like the 
`JSON ones <https://github.com/netgroup-polito/verigraph/tree/tosca-support/examples>`_) are respectively labeled 
**SAT** and **UNSAT** because a GET request to the address 
``http://localhost:8080/verigraph/api/graphs/GRAPH_ID/policy?source=user1&destination=webserver&type=reachability`` 
should return *"SAT"* or *"UNSAT"* as a result (where ``GRAPH_ID`` is the graph ID assigned by Verigraph).
