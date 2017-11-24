package it.polito.verigraph.grpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import it.polito.verigraph.tosca.classes.*;

import java.io.FileInputStream;
import java.io.IOException;
import it.polito.verigraph.tosca.classes.*;

import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc.ToscaVerigraphBlockingStub;
import it.polito.verigraph.grpc.tosca.*;



public class toscaClient {
	
    private final ManagedChannel channel;
    private final ToscaVerigraphBlockingStub blockingStub;
    private static final Logger logger = Logger.getLogger(toscaClient.class.getName());
    private static FileHandler fh;
    
    
    
    public toscaClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }
	
    /** Construct client for accessing toscaVerigraph server using the existing channel. */
    public toscaClient(ManagedChannelBuilder<?> channelBuilder) {
    	  channel = channelBuilder.build();
    	  blockingStub = ToscaVerigraphGrpc.newBlockingStub(channel);
    	}
    
    /** Close the channel */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
      }
    
    
    public List<TopologyTemplateGrpc> getTopologyTemplates(){
    	List<TopologyTemplateGrpc> templates = new ArrayList<TopologyTemplateGrpc>();
    	GetRequest request = GetRequest.newBuilder().build();
    	
    	/** Iterates on received topology templates, prints on log file in case of errors*/
    	Iterator<TopologyTemplateGrpc> recTempls;
    	try {
    		recTempls = blockingStub.getTopologyTemplates(request);
    		System.out.println("[toscaClient] Receiving Topology Templates...");
    		while(recTempls.hasNext()) {
    			TopologyTemplateGrpc nxtTempl = recTempls.next();
    			if(nxtTempl.getErrorMessage().equals("")) {
    				System.out.println("[toscaClient] Received Template: id - " + nxtTempl.getId());
    				templates.add(nxtTempl);
    			}else {
    				System.out.println("[toscaClient] Error receiving TopologyTemplates: " + nxtTempl.getErrorMessage());
    				return templates;
    			}	
    		}
 
    	}catch(StatusRuntimeException ex) {
    		System.err.println("[toscaClient] RPC failed : " + ex.getMessage());
    		warning("RPC failed - on getTopologyTemplates", ex.getStatus());
    	}
    	
    	return templates;
    	
    }
    
    
    /** Get Topology Template by ID */
    public TopologyTemplateGrpc getTopologyTemplate(long id) {
        RequestID request = RequestID.newBuilder().setIdTopologyTemplate(id).build();
        TopologyTemplateGrpc response;
        try {
            response = blockingStub.getTopologyTemplate(request);
            if(response.getErrorMessage().equals("")){
                System.out.println("[toscaClient] ReceivedTemplate : id - " + response.getId());
            }else{
                System.err.println("[toscaClient] Error : " + response.getErrorMessage());
                return null;
            }
        } catch (StatusRuntimeException ex) {
            System.err.println("[toscaClient] RPC failed: " + ex.getStatus());
            return null;
        }
        return response;
    }
    

    /** Creates a new TopologyTemplate, takes in input a tosca compliant filename */
    public void createTopologyTemplate(String toscaFile) {
    	TTopologyTemplate jaxbTempl = parseToscaFile(toscaFile);
    	
    	
    	
    }
    
    
    
    
    /** Method for parsing a Tosca xml file into Tosca objects.
     *  Additional functionalities for yaml support must be defined*/
    
    public TTopologyTemplate parseToscaFile(String file) {
        try {
            // create a JAXBContext capable of handling the generated classes
            JAXBContext jc = JAXBContext.newInstance( "it.polito.verigraph.tosca.classes" );
            
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            
            // unmarshal a document into a tree of Java content objects
            Object jaxbElement = u.unmarshal( new FileInputStream(file) );
            JAXBElement<TDefinitions> jaxbRoot = (JAXBElement<TDefinitions>)jaxbElement;
            TDefinitions root = (TDefinitions)jaxbRoot.getValue();

        } catch( JAXBException je ) {
        	System.out.println("Error while unmarshalling or marshalling");
            je.printStackTrace();
            System.exit(1);
        } catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit(1);
        } catch( ClassCastException cce) {
        	System.out.println("Wrong data type found in XML document");
        	cce.printStackTrace();
            System.exit(1);
        }
    	
    	
    }
    
    
    
    /** The clients prints logs on File - to be defined, two levels of log*/
    private void setUpLogger(){
    	try {  
    		// This block configure the logger with handler and formatter  
    		fh = new FileHandler("C:\\Users\\mikx_\\git\\verigraph\\tosca_support\\toscaGrpc\\toscaClientLog.log");  
    		logger.addHandler(fh);
    		SimpleFormatter formatter = new SimpleFormatter();  
    		fh.setFormatter(formatter);  
    	} catch (SecurityException e) {  
    		e.printStackTrace();  
    	} catch (IOException e) {  
    		e.printStackTrace();  
    	}  
    }
    
    private void info(String msg,  Object... params) {
        logger.log(Level.INFO, msg, params);    
    }

    private void warning(String msg, Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
    
}
