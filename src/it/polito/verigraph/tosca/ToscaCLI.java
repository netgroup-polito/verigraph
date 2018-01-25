package it.polito.verigraph.tosca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.sun.research.ws.wadl.ObjectFactory;

import it.polito.tosca.jaxb.Configuration;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TDefinitions;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaPolicy;
import it.polito.verigraph.grpc.ToscaTestGrpc;
import it.polito.verigraph.grpc.ToscaVerificationGrpc;
import it.polito.verigraph.grpc.client.ToscaClient;
import it.polito.verigraph.tosca.converter.grpc.GrpcToXml;
import it.polito.verigraph.tosca.converter.grpc.GrpcToYaml;
import it.polito.verigraph.tosca.converter.grpc.XmlToGrpc;
import it.polito.verigraph.tosca.converter.grpc.YamlToGrpc;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;


public class ToscaCLI {
	
	private static final String helper = "./tosca_support/CLIhelper.txt";
	
	//Service parameters.
	private String host;
	private int port;
	
	//New media type for yaml rest request
	private static final MediaType yamlMedia = new MediaType("application", "x-yaml");
	private static final String defaultHost = "localhost";
	private static final int defaultRestPort = 8080;
	private static final int defaultGrpcPort = 50051;
	
	//Input validation patterns
	private static final Pattern yamlSource = Pattern.compile(".*\\.yaml$");
	private static final Pattern xmlSource = Pattern.compile(".*\\.xml");
	private static final Pattern jsonSource = Pattern.compile(".*\\.json$");
	private static final Pattern configOpt = Pattern.compile("-use|-format|-port|-host", Pattern.CASE_INSENSITIVE);
	private static final Pattern useOpt = Pattern.compile("grpc|rest", Pattern.CASE_INSENSITIVE);
	private static final Pattern formatOpt = Pattern.compile("yaml|json|xml", Pattern.CASE_INSENSITIVE);
	private static final Pattern policies = Pattern.compile("reachability|isolation|traversal", Pattern.CASE_INSENSITIVE);
	
	//Configuration parameters
	private Boolean useRest;
	private String mediatype;
	private Client restClient;
	private ToscaClient grpcClient;
	
	public ToscaCLI(){
		//Variables representing the client environment
		this.useRest = true;
		this.port = defaultRestPort;
		this.host = defaultHost;
		this.mediatype = MediaType.APPLICATION_XML;	
		this.restClient = null;
		this.grpcClient = null;
	}
	
	
	public static void main(String[] args) {
		ToscaCLI myclient = new ToscaCLI();
		try {
			myclient.clientStart();
		} catch (Exception e) {
			System.out.println("-- Unexpected error, service closing.");
			e.printStackTrace();
		}
		return;
	}
	
	//Build base Uri for REST service
	private String buildBaseUri() {
		return "http://" + this.host + ":" + String.valueOf(this.port) + "/verigraph/api/graphs";
	}
	
	//Function iterating getting user commands.
	public void clientStart(){
    	System.out.println("++ Welcome to Verigraph Verification Serivice...");
    	System.out.println("++ Type HELP for instructions on client use...");
    	
    	Scanner reader = null;
    	InputStream input = System.in;
    	Scanner scan = new Scanner(System.in);
    	String commandline;

    	while(true) {
    		System.out.print("++ Please insert command : ");	
    		try{
    			
    			while(input.available()!=0) input.skip(input.available());
    			commandline = scan.nextLine();
    			reader = new Scanner(commandline);
    			
    			switch (reader.next().toUpperCase()) {
    				case "GETALL": 
    					if(useRest) this.restGetAll(reader);
    					else this.grpcGetAll(reader);
    					break;
    				case "GET":
    					if(useRest) this.restGet(reader);
    					else this.grpcGet(reader);
    					break;
    				case "CREATE":
    					if(useRest) this.restCreate(reader);
    					else this.grpcCreate(reader);
    					break;
    				case "DELETE":
    					if(useRest) this.restDelete(reader);
    					else grpcDelete(reader);
    					break;
    				case "UPDATE":
    					if(useRest) this.restUpdate(reader);
    					else this.grpcUpdate(reader);
    					break;
    				case "VERIFY":
    					if(useRest) this.restVerify(reader);
    					else this.grpcVerify(reader);
    					break;
    				case "HELP":
    					this.printHelper();
    					break;
    				case "CONFIGURE":
    					this.setConfig(reader);
    					break;
    				case "CLOSE":
    					System.out.println("++ Client closing...");
    					scan.close();
    					input.close();
    					reader.close();
    					if(grpcClient != null) this.grpcClient.shutdown();
    					if(restClient != null) this.restClient.close();
    					System.exit(0);
    					break;
    				default:
    					System.out.println("-- Unknown or bad formed command, type HELP to show commands documentation.");
    					break;
    			}
    			
    		}catch(NoSuchElementException ex) {
    			System.err.println("-- Unrecognized or incorrect command,"
    					+ " type help to know how to use the client...");
    			continue;
    		}catch(IOException ex){
    			ex.printStackTrace();
    		}catch(InterruptedException ex){
    			ex.printStackTrace();
    		}finally {
    			reader.close();
    		}
    	}
    		
    }
	
	
	public void printHelper() {
		Scanner filereader = null;
		try {
			File inputfile = new File(helper);
			filereader = new Scanner(inputfile).useDelimiter("\\Z");
			String content = filereader.next();
			if (filereader.ioException() != null) {
				throw new IOException(filereader.ioException());
			}
			if(content != null) System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(filereader != null) filereader.close();
		}
		
	}
	
	public void setConfig(Scanner reader) throws InterruptedException {
		if(!reader.hasNext()) {
			System.out.println("-- No configuration options provided.");
			return;
		}
		while(reader.hasNext(configOpt)) {
			switch(reader.next().toLowerCase()) {
			case "-use":
				if(reader.hasNext(useOpt)) {
					if(reader.next().toLowerCase().equals("rest")) {
						if(grpcClient != null) {
							grpcClient.shutdown();
							grpcClient = null;
						}
						this.port = defaultRestPort;
						restClient = ClientBuilder.newClient();
						useRest = true;
					}
					else {
						if(mediatype == MediaType.APPLICATION_JSON) {
							System.out.println("-- The JSON format is not compatible with the grpc interface, change format first.");
							return;
						}
						if(restClient != null) {
							restClient.close();
							restClient = null;
						}
						this.port = defaultGrpcPort;
						grpcClient = new ToscaClient(host, port);
						useRest = false;
					}
				}else {
					System.out.println("-- Unrecognized values for option -use, accepted values are: rest, grpc.");
				}
				break;
			case "-format":
				if(reader.hasNext(formatOpt)) {
					if(reader.next().toLowerCase().equals("json")) mediatype = MediaType.APPLICATION_JSON;
					else if(reader.next().toLowerCase().equals("xml")) mediatype = MediaType.APPLICATION_XML;
					else if(reader.next().toLowerCase().equals("yaml")) mediatype = "application/x-yaml";
				}else {
					System.out.println("-- Unrecognized values for option -format, accepted formats are: json, xml, yaml.");
				}
				break;
			case "-host":
				if(reader.hasNext()) {
					this.host = reader.next();
				}
				else {
					System.out.println("-- Provide a valid hostname.");
				}
				break;
			case "-port":
				if(reader.hasNextInt()) {
					int oldvalue = this.port;
					this.port = reader.nextInt();
					if(0 > this.port || 65535 < this.port) {
						System.out.println("-- The provided port number is not valid, port has not been modified.");
						this.port = oldvalue;
					}
				}
				else {
					System.out.println("-- Provide a port as an integer.");
				}
				break;
			default:
				System.out.println("-- Unrecognized option!");
			}
		}

	}
	
	
	// RESTful service interface CRUD and Verify functions
	public void restGetAll(Scanner reader) {
		try {
			// Build a new client if it does not exist
			if (restClient == null)
				restClient = ClientBuilder.newClient();

			// targeting the graphs resource
			WebTarget target = restClient.target(this.buildBaseUri());

			// Performing the request and reading the response
			Response res = target.request(mediatype).get();
			this.readResponseRest("GETALL", res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	
	public void restGet(Scanner reader) {

		try {
			// Build a new client if it does not exist
			if (restClient == null)
				restClient = ClientBuilder.newClient();

			if (!reader.hasNextLong()) {
				System.out.println("-- Provide the integer Id for the requested graph.");
				return;
			}

			// Targeting the specified graph resource
			WebTarget target = restClient.target(this.buildBaseUri() + "/" + String.valueOf(reader.nextLong()));

			// Performing the request and reading the response
			Response res = target.request(mediatype).get();
			this.readResponseRest("GETALL", res);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	public void restCreate(Scanner reader) {
		// TODO (?) handling of -x/j/y option to choose the mediatype

		try {
			// Getting file content
			String content = readFile(reader);
			if (content == null) {
				System.out.println("-- The required operation can't be performed.");
				return;
			}

			// Build a new client if it does not exist
			if (restClient == null)
				restClient = ClientBuilder.newClient();

			// Targeting the resource
			WebTarget target = restClient.target(this.buildBaseUri());

			// Performing the request and reading the response
			Builder mypost = target.request(mediatype);
			Response res = null;
			switch (mediatype) {
			case MediaType.APPLICATION_JSON:
				res = mypost.post(Entity.json(content));
				break;
			case MediaType.APPLICATION_XML:
				res = mypost.post(Entity.xml(content));
				break;
			case "application/x-yaml":
				res = mypost.post(Entity.entity(content, yamlMedia));
				break;
			}

			this.readResponseRest("CREATE", res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
	
	
	
	public void restDelete(Scanner reader) {
		// Build a new client if it does not exist
		if (restClient == null)
			restClient = ClientBuilder.newClient();
		
		if (!reader.hasNextLong()) {
			System.out.println("-- Provide the integer Id of the graph you want to delete.");
			return;
		}
		
		// Targeting the specified graph resource
		WebTarget target = restClient.target(this.buildBaseUri() + "/" + String.valueOf(reader.nextLong()));

		// Performing the request and reading the response
		Response res = target.request(mediatype).delete();
		this.readResponseRest("DELETE", res);

		return;	
	}
	
	
	public  void restUpdate(Scanner reader) {
		try {
			
			//Getting the target graph
			if(!reader.hasNextLong()) {
				System.out.println("-- Please provide a valid id for the graph to be update");
				return;
			}
			
			// Build a new client if it does not exist
			if (restClient == null)
				restClient = ClientBuilder.newClient();

			// Targeting the resource
			WebTarget target = restClient.target(this.buildBaseUri() + "/" + reader.next());
			
			// Getting file content
			String content = readFile(reader);
			if (content == null) {
				System.out.println("-- The required operation can't be performed.");
				return;
			}

			// Performing the request and reading the resonse
			Builder myupdate = target.request(mediatype);
			Response res = null;
			switch (mediatype) {
			case MediaType.APPLICATION_JSON:
				res = myupdate.put(Entity.json(content));
				break;
			case MediaType.APPLICATION_XML:
				res = myupdate.put(Entity.xml(content));
				break;
			case "application/x-yaml":
				res = myupdate.put(Entity.entity(content, yamlMedia));
				break;
			}

			this.readResponseRest("UPDATE", res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public  void restVerify(Scanner reader) {
		String whichpolicy = null;
		String graphId, source, destination, middlebox = null;
		
		try {
			if(!reader.hasNextLong()) {
				System.out.println("-- Provide the graph on which you want to perform verification.");
				return;
			}
			graphId = reader.next();
			
			if (!reader.hasNext(policies)) {
				System.out.println("-- Provide the requested type of verfication.");
				return;
			}
			whichpolicy = reader.next().toLowerCase();
			
			try {
				source = reader.next();
				destination = reader.next();
				if(!whichpolicy.equals("reachability")) {
					middlebox = reader.next();
				}
			}catch(NoSuchElementException ex) {
				System.out.println("-- Wrong or missing verification parameters.");
				return;
			}
			
			// Build a new client if it does not exist
			if (restClient == null)
				restClient = ClientBuilder.newClient();

			// Targeting the resource
			WebTarget target = restClient.target(this.buildBaseUri() + "/" + graphId + "/policy")
					.queryParam("source", source)
					.queryParam("destination", destination)
					.queryParam("type", whichpolicy);
			if(!whichpolicy.equals("reachability")) {
				target = target.queryParam("middlebox", middlebox);
			}
			
			Response res = target.request(mediatype).get();
			this.readResponseRest("VERIFY", res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	//gRPC service interface CRUD and Verify functions 
	public void grpcGetAll(Scanner reader) {
		try {
			if(grpcClient == null) 
				grpcClient = new ToscaClient(host, port);
			
			List<TopologyTemplateGrpc> templates; 
			templates =  grpcClient.getTopologyTemplates();
			
			if(templates.isEmpty()) {
				System.out.println("++ GET Success no graph was returned.");
				return;
			}

			switch(mediatype) {
			case MediaType.APPLICATION_XML:
				List<Definitions> receivedDefs = new ArrayList<Definitions>();
				for(TopologyTemplateGrpc curr : templates) {
					receivedDefs.add(GrpcToXml.mapGraph(curr));
				}
				this.marshallToXml(receivedDefs);
				break;
				
			case "application/x-yaml":
				List<ServiceTemplateYaml> receivedTempls = new ArrayList<ServiceTemplateYaml>();
				for(TopologyTemplateGrpc curr : templates) {
					receivedTempls.add(GrpcToYaml.mapGraphYaml(curr));
				}
				this.marshallToYaml(receivedTempls);
				break;
				
			}

		}catch (Exception ex) {
			ex.printStackTrace(); //TODO check for unhandled exceptions
		}
		
	}
	
	
	public void grpcGet(Scanner reader) {
		
		try {
			if (grpcClient == null)
				grpcClient = new ToscaClient(host, port);

			if (!reader.hasNextLong()) {
				System.out.println("-- Provide the integer Id for the requested graph.");
				return;
			}
			
			TopologyTemplateGrpc templ = grpcClient.getTopologyTemplate(reader.next());
			if(templ == null) {
				System.out.println("++ No template received, the requested template could not be present.");
				return;
			}
			
		
			switch(mediatype) {
			case MediaType.APPLICATION_XML:
				List<Definitions> receivedDefs = new ArrayList<Definitions>();
				receivedDefs.add(GrpcToXml.mapGraph(templ));
				this.marshallToXml(receivedDefs);
				break;
				
			case "application/x-yaml":
				List<ServiceTemplateYaml> receivedTempls = new ArrayList<ServiceTemplateYaml>();
				receivedTempls.add(GrpcToYaml.mapGraphYaml(templ));
				this.marshallToYaml(receivedTempls);
				break;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void grpcCreate(Scanner reader) {
		try {
			if (grpcClient == null)
				grpcClient = new ToscaClient(host, port);

			switch (mediatype) {
			case MediaType.APPLICATION_XML:
				if (reader.hasNext(xmlSource)) {
					grpcClient.createTopologyTemplate(XmlToGrpc.obtainTopologyTemplateGrpc(reader.next()));
				} else {
					System.out.println("-- The provided file is not compatible with the current configuration.");
					return;
				}
				break;

			case "application/x-yaml":
				if (reader.hasNext(yamlSource)) {
					grpcClient.createTopologyTemplate(YamlToGrpc.obtainTopologyTemplateGrpc(reader.next()));
				} else {
					System.out.println("-- The provided file is not compatible with the current configuration.");
					return;
				}
				break;
			}

		} catch (DataNotFoundException | ClassCastException | BadRequestException | IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
	
	
	public  void grpcDelete(Scanner reader) {
		
		try {
			if (grpcClient == null)
				grpcClient = new ToscaClient(host, port);
			
			if (!reader.hasNextLong()) {
				System.out.println("-- Provide the integer Id of the graph you want to delete.");
				return;
			}
			
			grpcClient.deleteTopologyTemplate(reader.next());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	
	public  void grpcUpdate(Scanner reader) {
		try {
			if (grpcClient == null)
				grpcClient = new ToscaClient(host, port);
			
			//Checking if user ha provided the id of the graph to be updated and retrieving it
			if(!reader.hasNextLong()) {
				System.out.println("-- Please provide a valid id for the graph to be update");
				return;
			}
			String id = reader.next();
			
			//Readign the file and performing the request according to current configuration
			switch (mediatype) {
			case MediaType.APPLICATION_XML:
				if (reader.hasNext(xmlSource)) {
					grpcClient.updateTopologyTemplate(XmlToGrpc.obtainTopologyTemplateGrpc(reader.next()), id);
				} else {
					System.out.println("-- The provided file is not compatible with the current configuration.");
					return;
				}
				break;

			case "application/x-yaml":
				if (reader.hasNext(yamlSource)) {
					grpcClient.updateTopologyTemplate(YamlToGrpc.obtainTopologyTemplateGrpc(reader.next()), id);
				} else {
					System.out.println("-- The provided file is not compatible with the current configuration.");
					return;
				}
				break;
			}
		} catch (DataNotFoundException | ClassCastException | BadRequestException | IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public  void grpcVerify(Scanner reader) {
		ToscaPolicy.Builder policyBuilder = ToscaPolicy.newBuilder();
		String graphId, whichPolicy, source, destination, middlebox = null;
		
			try {
				if(!reader.hasNextLong()) {
					System.out.println("-- Provide the graph on which you want to perform verification.");
					return;
				}
				graphId = reader.next();
				
				if (!reader.hasNext(policies)) {
					System.out.println("-- Provide the requested type of verfication.");
					return;
				}
				whichPolicy = reader.next().toLowerCase();
				
				try {
					source = reader.next();
					destination = reader.next();
					if(!whichPolicy.equals("reachability")) {
						middlebox = reader.next();
					}
				}catch(NoSuchElementException ex) {
					System.out.println("-- Wrong or missing verification parameters.");
					return;
				}
				
				policyBuilder.setIdTopologyTemplate(graphId);
				policyBuilder.setDestination(destination);
				policyBuilder.setSource(source);
				switch(whichPolicy) {
				case "reachability":
					policyBuilder.setType(ToscaPolicy.PolicyType.forNumber(0));
					break;
				case "isolation":
					policyBuilder.setType(ToscaPolicy.PolicyType.forNumber(1));
					policyBuilder.setMiddlebox(middlebox);
					break;
				case "traversal":
					policyBuilder.setType(ToscaPolicy.PolicyType.forNumber(2));
					policyBuilder.setMiddlebox(middlebox);
					break;
				}
				
				if (grpcClient == null)
					grpcClient = new ToscaClient(host, port);
				
				//Sending verification request
				ToscaVerificationGrpc result = grpcClient.verifyPolicy(policyBuilder.build());
				
				if(result.getErrorMessage().equals("")) {
					System.out.println("++ Verification result: " + result.getResult());
					System.out.println("++ Verification comment: " + result.getComment());
					List<ToscaTestGrpc> tests = result.getTestList();
					if(!tests.isEmpty()) {
						System.out.println("++ Followed paths: \n");
						// TODO complete this part
//					for(ToscaTestGrpc test : tests) {
//						test.getNodeTemplateList()
//					}
					}
					
				}else {
					System.out.println("-- Something went wrong: " + result.getErrorMessage());
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
	}
	
	
	
	
	
	public void readResponseRest(String responseOf, Response res) {		
		
		switch(responseOf) {
		case "GETALL":
			switch (res.getStatus()) {
			case 200:
				System.out.println("++ GET success :");
				String getresult = res.readEntity(String.class);
				if (getresult != null) {
					System.out.println(getresult);
				} else {
					System.out.println("** No graphs to be showed **");
				}
				return;
			case 500:
				System.out.println("-- GET failed : internal server error.");
				break;
			default:
				System.out.println("** Unexpected response");
				break;
			}
			break;
			
		case "GET":
			switch (res.getStatus()) {
			case 200:
				System.out.println("++ GET success :");
				String getresult = res.readEntity(String.class);
				if (getresult != null) {
					System.out.println(getresult);
				} else {
					System.out.println("** No graphs to be showed **");
				}
				return;
			case 404:
				System.out.println("-- GET failed : graph not found.");
				break;
			case 500:
				System.out.println("-- GET failed : internal server error.");
				break;
			default:
				System.out.println("** Unexpected response **");
				break;
			}
			break;
			
		case "CREATE":
			switch (res.getStatus()) {
			case 201:
				System.out.println("++ POST success : graph created.");
				break;
			case 400:
				System.out.println("-- POST failed : invalid graph.");
				break;
			case 500:
				System.out.println("-- POST failed : internal server error.");
				break;
			default:
				System.out.println("** Unexpected response **");
				break;
			}
			break;
		case "DELETE":
			switch (res.getStatus()) {
			case 204:
				System.out.println("++ DELETE success : graph deleted.");
				break;
			case 403:
				System.out.println("-- DELETE failed : invalid graph ID.");
				break;
			case 404:
				System.out.println("-- DELETE failed : invalid graph id.");
				break;
			case 500:
				System.out.println("-- DELETE failed : internal server error.");
				break;
			default:
				System.out.println("** Unexpected response **");
				break;
			}
			break;
		case "UPDATE":
			switch (res.getStatus()) {
			case 200:
				System.out.println("++ PUT success : graph correctly updated.");
				break;
			case 400:
				System.out.println("-- PUT failed : invalid graph object.");
				break;
			case 403:
				System.out.println("-- PUT failed : invalid graph ID.");
				break;
			case 404:
				System.out.println("-- PUT failed : graph not found.");
				break;
			case 500:
				System.out.println("-- PUT failed : internal server error.");
				break;
			default:
				System.out.println("** Unexpected response **");
				break;
			}
			break;
			
		default:
			
		}
		
		if(res.hasEntity()) {
			System.out.println(prettyFormat(res.readEntity(String.class)));
		}
		else {
			System.out.println("++ No content in the message body");
		}
		
	}
	
	
	public void marshallToXml(List<Definitions> defs) {
		try {
			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			for (Definitions def : defs) {
				// To be tested, in case of problems def must be converted to a JAXBElement
				m.marshal(def, System.out);
				System.out.println("\n");
			}

		} catch (JAXBException je) {
			System.out.println("-- Error while marshalling");
			je.printStackTrace();
		}

		return;
	}
	
	public void marshallToYaml(List<ServiceTemplateYaml> templates) {
		try {
			YAMLMapper mapper = new YAMLMapper();
			for (ServiceTemplateYaml templ : templates) {
				// To be tested, in case of problems def must be converted to a JAXBElement
				System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(templ));
				System.out.println("\n");
			}

		} catch (JsonProcessingException je) {
			System.out.println("-- Error while marshalling");
			je.printStackTrace();
		}
		return;
	}
	
	
	// Reads the whole file into a string and performs a minimum validation on file type
	public String readFile(Scanner reader) {
		
		String content = null;
		Scanner filereader = null;
		if ((mediatype.equals("application/x-yaml") && reader.hasNext(yamlSource))
				|| (mediatype.equals(MediaType.APPLICATION_XML) && reader.hasNext(xmlSource))
				|| (mediatype.equals(MediaType.APPLICATION_JSON) && reader.hasNext(jsonSource))) {
			try {
				File inputfile = new File(reader.next());
				filereader = new Scanner(inputfile).useDelimiter("\\Z");
				content = filereader.next();
				if (filereader.ioException() != null) {
					throw new IOException(filereader.ioException());
				} else {
					System.out.println("++ File correctly read.");
				}
			} catch (FileNotFoundException ex) {
				System.out.println("-- The provided file does not exist!");
				ex.printStackTrace();
			}catch (IOException ex) {
				System.out.println("-- An error occurred reading the input file!");
				ex.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				if(filereader != null) filereader.close();
			}
			
		} else {
			System.out.println("-- The file provided in input does not match with the current client configuration.");
		}
		
		return content;
	}
	
	
	public String prettyFormat(String input) {
		String formattedString = null;
	    try {
	    	switch(mediatype) {
	    	case MediaType.APPLICATION_XML:
	    		Source xmlInput = new StreamSource(new StringReader(input));
		        StringWriter stringWriter = new StringWriter();
		        StreamResult xmlOutput = new StreamResult(stringWriter);
		        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        transformerFactory.setAttribute("indent-number", 2);
		        Transformer transformer = transformerFactory.newTransformer(); 
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer.transform(xmlInput, xmlOutput);
		        formattedString = xmlOutput.getWriter().toString();
		        break;
	    	case MediaType.APPLICATION_JSON:
	    		ObjectMapper mapper = new ObjectMapper();
	    		Object jsonObj = mapper.readValue(input, Object.class);
	    		formattedString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
	    		break;
	    	case "application/x-yaml":
	    		break;
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return formattedString;
	}
	
	
}