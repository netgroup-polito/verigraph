package it.polito.verigraph.grpc.server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import it.polito.verigraph.grpc.GetRequest;
import it.polito.verigraph.grpc.Status;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.resources.beans.VerificationBean;
import it.polito.verigraph.service.GraphService;
import it.polito.verigraph.service.VerificationService;
/* new import */
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.NewTopologyTemplate;
import it.polito.verigraph.grpc.tosca.ToscaRequestID;
import it.polito.verigraph.grpc.tosca.ToscaPolicy;
import it.polito.verigraph.grpc.tosca.ToscaVerificationGrpc;
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
            ToscaService server = new ToscaService(port);
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
        public void getTopologyTemplate (ToscaRequestID request, StreamObserver<TopologyTemplateGrpc> responseObserver) {
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
                 Graph newGraph = graphService.addGraph(graph);
                 response.setSuccess(true).setTopologyTemplate(GrpcUtils.obtainTopologyTemplate(newGraph)); 
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
        public void deleteTopologyTemplate (ToscaRequestID request, StreamObserver<Status> responseObserver) {
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
                 Graph newGraph = graphService.updateGraph(graph);
                 response.setSuccess(true).setTopologyTemplate(GrpcUtils.obtainTopologyTemplate(newGraph));
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
        public void verifyPolicy(ToscaPolicy request, StreamObserver<ToscaVerificationGrpc> responseObserver) {

            ToscaVerificationGrpc.Builder verification;
            try{
                //Convert request
                VerificationBean verify = new VerificationBean();
                verify.setDestination(request.getDestination());
                verify.setSource(request.getSource());
                verify.setType(request.getType().toString());
                verify.setMiddlebox(request.getMiddlebox());

                //Convert Response
                Verification ver = verificationService.verify(request.getIdTopologyTemplate(), verify);
                verification = ToscaVerificationGrpc.newBuilder(GrpcUtils.obtainToscaVerification(ver))
                        .setSuccessOfOperation(true); 
            }catch(ForbiddenException | DataNotFoundException | BadRequestException ex){
                verification = ToscaVerificationGrpc.newBuilder().setSuccessOfOperation(false)
                        .setErrorMessage(ex.getMessage());
                logger.log(Level.WARNING, ex.getMessage());
            }catch(Exception ex){
                verification = ToscaVerificationGrpc.newBuilder().setSuccessOfOperation(false)
                        .setErrorMessage(internalError);
                logger.log(Level.WARNING, ex.getMessage());
            }
            responseObserver.onNext(verification.build());
            responseObserver.onCompleted();
        }
    	
    	
    }
}

