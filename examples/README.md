The JSON files contained in this folder are meant to be used in the body of a POST to the address http://localhost:8080/verigraph/api/graphs.
They are respectively labeled SAT and UNSAT because a GET request to the address
http://localhost:8080/verigraph/api/graphs/2/policy?source=user1&destination=webserver&type=reachability should a JSON object with either "SAT" or "UNSAT" as a result.