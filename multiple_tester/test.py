#!/usr/bin/python
#
#  Copyright 2016 Politecnico di Torino
#  Authors:
#  Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
#  
#  This file is part of Verigraph.
#  
#  Verigraph is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Affero General Public License as
#  published by the Free Software Foundation, either version 3 of
#  the License, or (at your option) any later version.
#  
#  Verigraph is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Affero General Public License for more details.
#  
#  You should have received a copy of the GNU Affero General Public
#  License along with Verigraph.  If not, see
#  <http://www.gnu.org/licenses/>.
#
from __future__ import print_function
from jsonschema import validate
from pprint import pprint
import sys
import requests
from requests.exceptions import *
from jsonschema.exceptions import *
import json
import getopt
import os
import subprocess
import csv
import datetime
import time
import re

# Constants (change them, if appropriate)
VERIGRAPH_PORT = "8080"
TEST_CASES_DIR = "testcases"
BASE_URL = "http://localhost:"+VERIGRAPH_PORT+"/verigraph/api/graphs/"
SCHEMA_FILE = "testcase_schema.json"

# Variables
success = 0
run = 0
performed = 0

# Utils
def eprint(toPrint):
    sys.stdout.flush()
    print(toPrint, file=sys.stderr)
    sys.stderr.flush()
    
# Print PYTHON ver
print("PYTHON " + sys.version)

# Loading schema file
try:
    schema = json.load(open(SCHEMA_FILE))
except ValueError:
    eprint("Invalid json schema (check your "+SCHEMA_FILE+")!\nExiting.")
    exit(-1)

with open('result.csv', 'wb') as file:
    writer=csv.writer(file, delimiter=',', quoting=csv.QUOTE_MINIMAL, lineterminator='\n')
    # Iterate over .json files contained in the TEST_CASES_DIR
    for i in os.listdir(TEST_CASES_DIR):
        if i.endswith(".json"): 
            with open(TEST_CASES_DIR+os.path.sep+i) as data_file:
                try:
                    # Load json file (raise exception if malformed)
                    data = json.load(data_file)
                    print("caricato")
                    # Validate input json against schema (raise exception if invalid)
                    validate(data, schema)
                    
                    run += 1
                    print("Test case ID: "+str(data["id"]))
                    print("\tFILE NAME: "+i)
                    print("\tTEST NAME: "+data["name"])
                    print("\tTEST DESCRIPTION: "+data["description"])
                     
                    requested=data["policy_url_parameters"]                                      
                    
                    # POST the graph
                    r = requests.post(BASE_URL, json=data["graph"])
                    if r.status_code == 201: 
                         
                        success+=1
                        graph_id = r.json()["id"]
                        print("\tCreated Graph has ID " + str(graph_id) + " on VeriGraph")
                        
                        for i in range(len(requested)):
                            output=[]
                            total_time=[]
                            result=[]
                            temp=requested[i]
                            temp=re.split(r'[&=]', temp)
                            output.append(temp[3])
                            output.append(temp[5])
                            for n in range(0, 10):  
                                
                                start_time=datetime.datetime.now()
                                #GET the policy verification result
                                policy = requests.get(BASE_URL+str(graph_id)+"/policy"+data["policy_url_parameters"][i])
                                total_time.append(int((datetime.datetime.now()-start_time).total_seconds() * 1000))
                                # Check the response
                                if policy.status_code == 200:
                                    
                                    print("\tVerification result is " + policy.json()["result"])
                                    if n==0:
                                          result.append(policy.json()["result"])
                                          
                                    else:                                      
                                        if policy.json()["result"]==result[n-1]:
                                            result.append(policy.json()["result"])
                                            if(n==9):
                                                performed+=1
                                        else:
                                            # FAIL
                                            eprint("\t[ERROR] The result of previous test was " + result[n-1] + " but this test returned " + policy.json()["result"])
                                      
                                else:
                                    print("\tVeriGraph returned an unexpected response -> " + str(policy.status_code), policy.reason)
                                    print("\t--- Test failed ---")
                            output.append(graph_id)
                            output.append(data["id"])
                            output.append(result[0])
                            for j in range(len(total_time)):
                                output.append(total_time[j])
                            writer.writerow(output) 
                    print()
                except ValueError:
                    print("Malformed json!\nSkipping "+i+" file")
                    print("\t--- Test failed ---")
                except ValidationError:
                    print("Invalid json (see Schema file)!\nSkipping "+i+" file")
                    print("\t--- Test failed ---")
                except ConnectionError:
                    print("Connection refused!")
                    print("\t--- Test failed ---")
                except HTTPError:
                    print("HTTP error!")
                    print("\t--- Test failed ---")

# Final output
print("\nTest run = "+str(run))
print("Test succeded = "+str(success))
if run != 0:
    if run != success:
        print("\n --- Some tests failed. See the output. ---")
    else:
        print("\n +++ All tests passed +++")
        print("\nRequests performed:" + str(performed))
else:
    print("\n\n +++ 0 tests executed +++")
    