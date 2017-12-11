package it.polito.verigraph.random;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.random.PolicyGen.PolicyType;

class GraphGen extends Graph {

    private HashMap<String,PolicyGen> policies;

    private Node clientNodes[];
    private Node serverNodes[];
    private Node middleNodes[];

    private FunctionalTypes clientTypes[]={FunctionalTypes.ENDHOST};
    private FunctionalTypes serverTypes[]={FunctionalTypes.MAILSERVER,FunctionalTypes.WEBSERVER};
    private FunctionalTypes middleTypes[]={FunctionalTypes.ANTISPAM,FunctionalTypes.CACHE,FunctionalTypes.DPI, FunctionalTypes.FIELDMODIFIER, FunctionalTypes.FIREWALL,FunctionalTypes.NAT,FunctionalTypes.VPNACCESS,FunctionalTypes.VPNEXIT};

    private PolicyGen.PolicyType types[]={PolicyType.REACHABILITY, PolicyType.ISOLATION,PolicyType.TRAVERSAL};

    private static int count=0;

    public static void resetCounter() {
        count=0;
    }

    GraphGen(Random random, long seed) {
        super(count);
        count++;

        // create policies hash map
        policies = new HashMap<String,PolicyGen>();

        // create nodes
        clientNodes = createNodeSubset(clientTypes, random, 10);
        serverNodes = createNodeSubset(serverTypes, random, 10);
        middleNodes = createNodeSubset(middleTypes, random, 20);

        // create links
        createLinks(random);

        // create policies for this nffg
        createPolicies(random);

    }

    private void createLinks(Random random) {
        int m ;
        //HashMap<Long, LinkGen> possibleLinks = new HashMap<Long,LinkGen>();

        HashMap<Long, Neighbour> neighbourfromnode;
        HashMap<Long, Neighbour> neighbourfromneighbour;
        Neighbour node;
        // create links for each client
        for (Node client: clientNodes) {
            neighbourfromnode= new HashMap<Long,Neighbour>();
            neighbourfromneighbour= new HashMap<Long,Neighbour>();

            // connect the client via a bidirectional link to a randomly chosen middlebox
            m = random.nextInt(middleNodes.length);
            node = new Neighbour(middleNodes[m].getId(), middleNodes[m].getName());
            neighbourfromnode.put(node.getId(), node);

            neighbourfromneighbour.put(client.getId(), new Neighbour(client.getId(), client.getName()));
            middleNodes.clone()[m].setNeighbours(neighbourfromneighbour);

            // create the other possible links for this client
            int maxNumLinks = middleNodes.length/3;
            for (int i=0; i<maxNumLinks; i++) {
                int j = random.nextInt(maxNumLinks);
                if (j != m) {
                    node = new Neighbour(middleNodes[j].getId(), middleNodes[j].getName());
                    neighbourfromnode.put(node.getId(), node);
                    neighbourfromneighbour= new HashMap<Long,Neighbour>();
                    neighbourfromneighbour.put(client.getId(), new Neighbour(client.getId(), client.getName()));
                    middleNodes.clone()[j].setNeighbours(neighbourfromneighbour);
                }
            }
            nodes.get(client.getId()).setNeighbours(neighbourfromnode);

        }

        //create links for each server
        for (Node server: serverNodes) {
            neighbourfromnode= new HashMap<Long,Neighbour>();
            neighbourfromneighbour= new HashMap<Long,Neighbour>();
            // connect the server via a bidirectional link to a randomly chosen middlebox
            m = random.nextInt(middleNodes.length);
            node = new Neighbour(middleNodes[m].getId(), middleNodes[m].getName());
            neighbourfromnode.put(middleNodes[m].getId(), node);

            neighbourfromneighbour.put(server.getId(), new Neighbour(server.getId(), server.getName()));
            middleNodes.clone()[m].setNeighbours(neighbourfromneighbour);

            // create the other possible links for this server
            int maxNumLinks = middleNodes.length/3;
            for (int i=0; i<maxNumLinks; i++) {
                int j = random.nextInt(maxNumLinks);
                if (j != m) {
                    node = new Neighbour(middleNodes[j].getId(), middleNodes[j].getName());
                    neighbourfromnode.put(node.getId(), node);
                    neighbourfromneighbour= new HashMap<Long,Neighbour>();
                    neighbourfromneighbour.put(server.getId(), new Neighbour(server.getId(), server.getName()));
                    middleNodes.clone()[j].setNeighbours(neighbourfromneighbour);
                }
            }
            nodes.get(server.getId()).setNeighbours(neighbourfromnode);
        }

        // create possible links for middleboxes
        for (int i=0; i<middleNodes.length; i++) {
            neighbourfromnode= new HashMap<Long,Neighbour>();
            for (int j=0; j<middleNodes.length; j++) {
                if (i != j){
                    node = new Neighbour(middleNodes[j].getId(), middleNodes[j].getName());
                    neighbourfromnode.put(middleNodes[j].getId(), node);
                }
                middleNodes.clone()[i].setNeighbours(neighbourfromnode);
            }
        }

        return ;
    }

    private void createPolicies(Random random) {
        PolicyGen policy;

        // create policies with client as source
        for (int i=0; i<clientNodes.length; i++) {
            // create vector of possible destinations
            Vector<Node> possibleDestinations = new Vector<Node>(Arrays.asList(serverNodes));
            // choose number of destinations
            int m = random.nextInt(serverNodes.length)+1;
            // for each destination to be used
            for (int j=0; j<m; j++) {
                // choose index of destination to be selected
                int serverIndex = random.nextInt(possibleDestinations.size());
                // TODO choose type of policy
                // create reachability policy
                PolicyType type = types[random.nextInt(types.length)];
                policy = new PolicyGen(this, random, clientNodes[i], serverNodes[serverIndex],type);
                // add created policy to Hashmap
                policies.put(policy.getName(), policy);
                // remove selected destination from Vector
                possibleDestinations.remove(serverIndex);
            } // for each destination
        } // for each client
    }

    HashMap<String, PolicyGen> getPolicies() {
        return policies;
    }

    /**
     * Creates a random set of new nodes with types taken from a specified list of types
     * The new nodes are added to the instance nodes set
     * The nodes set is assumed to be already existing (not null)
     * @param types	the types to be used for the new nodes
     * @param random	the random generator to be used for initializing the new nodes
     * @param maxNum	the maximum number of new nodes to be created
     * @return	an array including all the new nodes created
     */
    private Node[] createNodeSubset(FunctionalTypes types[], Random random, int maxNum) {

        if (types == null || types.length == 0)
            return new Node[0];

        int numNodes = random.nextInt(maxNum)+1; // at least 1 node
        int existingnode = nodes.size();
        Node nodeSubset[] = new Node[numNodes];
        for (int i=0; i<numNodes; i++) {
            // create and store single node
            FunctionalTypes type;
            if(types.length==1)
                type= types[0];
            else type = chooseType(types,random);
            Configuration conf = createConfiguration("",type.toString(), random.nextBoolean()); //TODO
            nodeSubset[i] = new Node(existingnode+i,(type.toString().replace("_", ""))+i,type.toString().toLowerCase(), conf); //No configuration yet
            nodes.put(nodeSubset[i].getId(), nodeSubset[i]);
        }
        return nodeSubset;
    }

    private FunctionalTypes chooseType(FunctionalTypes[] array, Random random) {
        // choose node type randomly
        return array[random.nextInt(array.length)];
    }

    public Node getNode(String name) {
        if (name == null || nodes == null)
            return null;
        else
            return nodes.get(name);
    }

    private Configuration createConfiguration (String nodename, String functype, boolean nextBoolean) { //TODO complete configurations

        Configuration conf;
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode conf_node = nodeFactory.objectNode();
        ArrayNode conf_array = nodeFactory.arrayNode();
        switch(functype){
        case "ENDHOST":
            if(!nextBoolean){
                conf_node.put("url", "www.facebook.com");
                conf_node.put("body", "weapons");
                conf_node.put("email_from", "spam@polito.it");
            } else{
                conf_node.put("url", "www.deepweb.com");
                conf_node.put("body", "cats");
                conf_node.put("email_from", "verigraph@polito.it");
            }
            conf_array.add(conf_node);
            conf = new Configuration(nodename, "Endhost configuration", conf_array);
            break;
        case "MAILSERVER":
            conf = new Configuration(nodename, "Mail Server configuration", conf_array);
            break;
        case "WEBSERVER":
            conf = new Configuration(nodename, "Web Sever configuration", conf_array);
            break;
        case "ANTISPAM":
            if(!nextBoolean){
                conf_array.add("spam@polito.it");
            } else{
                conf_array.add("verigraph@polito.it");
            }
            conf = new Configuration(nodename, "Antispam configuration", conf_array);
            break;
        case "CACHE":
            conf = new Configuration(nodename, "Web Cache configuration", conf_array);
            break;
        case "DPI":
            conf_array.add("weapons");
            conf = new Configuration(nodename, "DPI configuration", conf_array);
            break;
        case "FIELDMODIFIER":
            conf = new Configuration(nodename, "FieldModifier configuration", conf_array);
            break;
        case "FIREWALL":
            conf = new Configuration(nodename, "Firewall configuration", conf_array);
            break;
        case "NAT" :
            conf = new Configuration(nodename, "NAT configuration", conf_array);
            break;
        case "VPNACCESS":
            conf = new Configuration(nodename, "VPN Access gateway configuration", conf_array);
            break;
        case "VPNEXIT":
            conf = new Configuration(nodename, "VPN Exit gateway configuration", conf_array);
            break;
        default:
            return null;
        }

        return conf;
    }
}
