package it.polito.verigraph.grpc.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import it.polito.verigraph.grpc.ConfigurationGrpc;
import it.polito.verigraph.grpc.GetRequest;
import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NeighbourGrpc;
import it.polito.verigraph.grpc.NewGraph;
import it.polito.verigraph.grpc.NewNeighbour;
import it.polito.verigraph.grpc.NewNode;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.resources.beans.VerificationBean;
import it.polito.verigraph.service.GraphService;
import it.polito.verigraph.service.NeighbourService;
import it.polito.verigraph.service.NodeService;
import it.polito.verigraph.service.VerificationService;
/* new import */
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc;
import it.polito.verigraph.grpc.tosca.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.tosca.NewTopologyTemplate;
import it.polito.verigraph.grpc.tosca.RequestID;
import it.polito.verigraph.grpc.tosca.Policy;
import it.polito.verigraph.grpc.tosca.Status;
import it.polito.verigraph.grpc.tosca.VerificationGrpc;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc;

public class ToscaService {
    /** Port on which the server should run. */
    private static final Logger logger = Logger.getLogger(Service.class.getName());
    private static final int port = 50051;
    private static final String internalError = "Internal Server Error";
    private Server server;
    private GraphService graphService= new GraphService();
    private VerificationService verificationService = new VerificationService();

    public ToscaService(int port) {
        this(ServerBuilder.forPort(port), port);
    }

    /** Create a ToscaService server using serverBuilder as a base and features as data. */
    public ToscaService(ServerBuilder<?> serverBuilder, int port) {
        server = serverBuilder.addService(new ToscaVerigraphImpl())
                .build();
    }

    public void start() throws IOException {
        FileHandler fileTxt = new FileHandler("grpc_server_log.txt");
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
        server.start();
        logger.info("Server started, listening on "+ port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("*** Shutting down gRPC server since JVM is shutting down");
                ToscaService.this.stop();
                logger.info("*** Server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /** Main function to launch server from cmd. */
    public static void main(String[] args) throws IOException, InterruptedException {
        try{
            Service server = new Service(port);
            server.start();
            server.blockUntilShutdown();
        }
        catch(Exception ex){
            logger.log(Level.WARNING, ex.getMessage());
        }
    }

    /**Here start method of my implementation*/
    private class ToscaVerigraphImpl extends ToscaVerigraphGrpc.ToscaVerigraphImplBase{
    	
    	/** Here start methods of TopologyTemplate (TOSCA)*/
       
    	@Override
        public void getTopologyTemplates (GetRequest request, StreamObserver<TopologyTemplateGrpc> responseObserver) {
        	try{
                for(Graph item : graphService.getAllGraphs()) {
                    TopologyTemplateGrpc topol = GrpcUtils.obtainTopologyTemplate(item);
                    responseObserver.onNext(topol);
                }
            }catch(Exception ex){
                TopologyTemplateGrpc topolErr = TopologyTemplateGrpc.newBuilder().setErrorMessage(internalError).build();
                responseObserver.onNext(topolErr);
                logger.log(Level.WARNING, ex.getMessage());
            }
            responseObserver.onCompleted();
        }
        
        @Override
        public void getTopologyTemplate (RequestID request, StreamObserver<TopologyTemplateGrpc> responseObserver) {
        	try{
                Graph graph = graphService.getGraph(request.getIdTopologyTemplate());
                TopologyTemplateGrpc topol = GrpcUtils.obtainTopologyTemplate(graph);
                responseObserver.onNext(topol);
            }catch(ForbiddenException | DataNotFoundException ex){
            	TopologyTemplateGrpc topolError = TopologyTemplateGrpc.newBuilder().setErrorMessage(ex.getMessage()).build();
                responseObserver.onNext(topolError);
                logger.log(Level.WARNING, ex.getMessage());
            }catch(Exception ex){
            	TopologyTemplateGrpc topolError = TopologyTemplateGrpc.newBuilder().setErrorMessage(internalError).build();
                responseObserver.onNext(topolError);
                logger.log(Level.WARNING, ex.getMessage());
            }
            responseObserver.onCompleted();
        }
        
        @Override
        public void createTopologyTemplate (TopologyTemplateGrpc request, StreamObserver<NewTopologyTemplate> responseObserver) {
        	 NewTopologyTemplate.Builder response = NewTopologyTemplate.newBuilder();
             try{
                 Graph graph = GrpcUtils.deriveGraph(request);
                 graphService.addGraph(graph);
                 response.setSuccess(true); //.setNewTopologyTemplate(GrpcUtils.obtainTopologyTemplate(newGraph)); TO BE ADDED IF YOU WANT TO RETURN A TOPOLOGY AS WELL
             }catch(BadRequestException ex){
                 response.setSuccess(false).setErrorMessage(ex.getMessage());
                 logger.log(Level.WARNING, ex.getClass().toString());
                 logger.log(Level.WARNING, ex.getMessage());

             }
             catch(Exception ex){
                 response.setSuccess(false).setErrorMessage(internalError);
                 logger.log(Level.WARNING, ex.getClass().toString());
                 logger.log(Level.WARNING, ex.getMessage());
             }
             responseObserver.onNext(response.build());
             responseObserver.onCompleted();
        }
        
        @Override
        public void deleteTopologyTemplate (RequestID request, StreamObserver<Status> responseObserver) {
        	Status.Builder response = Status.newBuilder();
            try{
                graphService.removeGraph(request.getIdTopologyTemplate());
                response.setSuccess(true);
            }catch(ForbiddenException ex){
                response.setSuccess(false).setErrorMessage(ex.getMessage());
                logger.log(Level.WARNING, ex.getMessage());
            }catch(Exception ex){
                response.setSuccess(false).setErrorMessage(internalError);
                logger.log(Level.WARNING, ex.getMessage());
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
        
        @Override
        public void updateTopologyTemplate (TopologyTemplateGrpc request, StreamObserver<NewTopologyTemplate> responseObserver) {
        	 NewTopologyTemplate.Builder response = NewTopologyTemplate.newBuilder();
             try{
                 Graph graph = GrpcUtils.deriveGraph(request);
                 graph.setId(request.getId());
                 graphService.updateGraph(graph);
                 response.setSuccess(true);//.setGraph(GrpcUtils.obtainGraph(newGraph)); SAME OF ABOVE
             }catch(ForbiddenException | DataNotFoundException | BadRequestException ex){
                 response.setSuccess(false).setErrorMessage(ex.getMessage());
                 logger.log(Level.WARNING, ex.getMessage());
             }catch(Exception ex){
                 response.setSuccess(false).setErrorMessage(internalError);
                 logger.log(Level.WARNING, ex.getMessage());
             }
             responseObserver.onNext(response.build());
             responseObserver.onCompleted();
        }
    	
        @Override
        public void verifyPolicy(Policy request, StreamObserver<VerificationGrpc> responseObserver) {

            VerificationGrpc.Builder verification;
            try{
                //Convert request
                VerificationBean verify = new VerificationBean();
                verify.setDestination(request.getDestination());
                verify.setSource(request.getSource());
                verify.setType(request.getType().toString());
                verify.setMiddlebox(request.getMiddlebox());

                //Convert Response
                Verification ver = verificationService.verify(request.getIdGraph(), verify);
                verification = VerificationGrpc.newBuilder(GrpcUtils.obtainVerification(ver))
                        .setSuccessOfOperation(true); //NAME CONFLICT TO BE SOLVED SOON
            }catch(ForbiddenException | DataNotFoundException | BadRequestException ex){
                verification = VerificationGrpc.newBuilder().setSuccessOfOperation(false)
                        .setErrorMessage(ex.getMessage());
                logger.log(Level.WARNING, ex.getMessage());
            }catch(Exception ex){
                verification = VerificationGrpc.newBuilder().setSuccessOfOperation(false)
                        .setErrorMessage(internalError);
                logger.log(Level.WARNING, ex.getMessage());
            }
            responseObserver.onNext(verification.build());
            responseObserver.onCompleted();
        }
    	
    	
    }
}

