package it.polito.verigraph.scalability.tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoFieldModifier;
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;

public class Test_Scalability_FW_FM_DPI_NAT {

    public Checker check;
    public Context ctx;
    public PolitoEndHost client;//, server;
    public PolitoWebServer server;
    public AclFirewall[] fws = new AclFirewall[10];
    public PolitoFieldModifier[] fms = new PolitoFieldModifier[10];
    public PolitoIDS[] dpis= new PolitoIDS[10];
    public PolitoNat nat;

    public Test_Scalability_FW_FM_DPI_NAT(){

        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"client","server", "nat",
                "fw1","fw2","fw3", "fw4", "fw5", "fw6", "fw7", "fw8", "fw9", "fw10",
                "fm1","fm2","fm3", "fm4", "fm5", "fm6", "fm7", "fm8", "fm9", "fm10",
                "dpi1","dpi2","dpi3", "dpi4", "dpi5", "dpi6", "dpi7", "dpi8", "dpi9", "dpi10"},
                new String[]{"ip_client", "ip_server", "ip_nat",
                        "ip_fw1","ip_fw2","ip_fw3", "ip_fw4", "ip_fw5", "ip_fw6", "ip_fw7", "ip_fw8", "ip_fw9", "ip_fw10",
                        "ip_fm1","ip_fm2","ip_fm3", "ip_fm4", "ip_fm5", "ip_fm6", "ip_fm7", "ip_fm8", "ip_fm9", "ip_fm10",
                        "ip_dpi1","ip_dpi2","ip_dpi3", "ip_dpi4", "ip_dpi5", "ip_dpi6", "ip_dpi7", "ip_dpi8", "ip_dpi9", "ip_dpi10"});
        Network net = new Network (ctx,new Object[]{nctx});

        client = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("client"), net, nctx});
        server = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("server"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        for(int i = 0; i< fws.length; i++){
            fws[i]= new AclFirewall(ctx, new Object[]{nctx.nm.get("fw"+(i+1)), net, nctx});
        }
        for(int i = 0; i< fms.length; i++)
            fms[i]= new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get("fm"+(i+1)), net, nctx});
        for(int i = 0; i< dpis.length; i++)
            dpis[i]= new PolitoIDS(ctx, new Object[]{nctx.nm.get("dpi"+(i+1)), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<ArrayList<DatatypeExpr>> als = new ArrayList<ArrayList<DatatypeExpr>>();
        for(int i =0 ; i< (3+fws.length+fms.length+dpis.length); i++)
            als.add( new ArrayList<DatatypeExpr>());

        for(int i =0 ;i< als.size(); i++){
            if(i == 0){
                als.get(0).add(nctx.am.get("ip_client"));
                adm.add(new Tuple<>(client, als.get(0)));
            }else if (i == 1){
                als.get(1).add(nctx.am.get("ip_server"));
                adm.add(new Tuple<>(server, als.get(1)));
            }else if (i == 2){
                als.get(2).add(nctx.am.get("ip_nat"));
                adm.add(new Tuple<>(nat, als.get(2)));
            }else if(i > 2 && i< 13){ //i in [3,12]
                als.get(i).add(nctx.am.get("ip_fw"+(i-2)));
                adm.add(new Tuple<>(fws[i-3], als.get(i)));
            }else if(i > 12 && i< 23){//i in [13,22]
                als.get(i).add(nctx.am.get("ip_fm"+(i-12)));
                adm.add(new Tuple<>(fms[i-13], als.get(i)));
            }else if(i > 22 && i< 33){//i in [23,32]
                als.get(i).add(nctx.am.get("ip_dpi"+(i-22)));
                adm.add(new Tuple<>(dpis[i-23], als.get(i)));
            }
        }
        net.setAddressMappings(adm);

        ArrayList<ArrayList<Tuple<DatatypeExpr,NetworkObject>>> rtables = new ArrayList<ArrayList<Tuple<DatatypeExpr,NetworkObject>>>();
        for(int i =0 ; i< (3+fws.length+fms.length+dpis.length); i++){
            rtables.add( new ArrayList<Tuple<DatatypeExpr,NetworkObject>>());
            if(i == 0){
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fws[0]));
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), fws[0]));
                for(int j =0 ; j< (fws.length); j++){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fws[0]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fws[0]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), fws[0]));
                }
                net.routingTable(client, rtables.get(i));
            }
            else if(i == 1){
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat));
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"),nat));
                for(int j =0 ; j< (fws.length); j++){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), nat));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), nat));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), nat));
                }
                net.routingTable(server, rtables.get(i));
            }
            else if(i == 2){
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), dpis[9]));
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), server));
                for(int j =0 ; j< (fws.length); j++){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), dpis[9]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), dpis[9]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), dpis[9]));
                }
                net.routingTable(nat, rtables.get(i));
            }
            else if(i > 2 && i< 13){ //firewalls
                if(i==3){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), client));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fws[i-2]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), fws[i-2]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fws[i-2]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), fws[i-2]));
                        if(j!=0)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fws[i-2]));
                    }
                } else if(i==12){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fws[i-4]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fms[0]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), fms[0]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fms[0]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), fms[0]));
                        if(j!=9)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fws[i-4]));
                    }
                } else {
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fws[i-4]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fws[i-2]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), fws[i-2]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fws[i-2]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), fws[i-2]));
                        if(j > ((i-3)%10))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fws[i-2]));
                        if(j < ((i-3)%10))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fws[i-4]));
                    }
                }
                net.routingTable(fws[i-3], rtables.get(i));
            }
            else if(i > 12 && i< 23){ //field modifiers
                if(i==13){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fws[9]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fms[i-12]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), fms[i-12]));
                    for(int j =0 ; j< (fms.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fms[i-12]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), fms[i-12]));
                        if(j!=0)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fms[i-12]));
                    }
                } else if(i==22){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fms[i-14]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), dpis[0]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), dpis[0]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fms[i-14]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), dpis[0]));
                        if(j!=9)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fms[i-14]));
                    }
                }else {
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fms[i-14]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fms[i-12]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), fms[i-12]));
                    for(int j =0 ; j< (fms.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fms[i-14]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), fms[i-12]));
                        if(j > ((i-3)%10))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fms[i-12]));
                        if(j < ((i-3)%10))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fms[i-14]));
                    }
                }
                net.routingTable(fms[i-13], rtables.get(i));
            }
            else if(i > 22 && i< 33){ // dpis
                if(i==23){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fms[9]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), dpis[i-22]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), dpis[i-22]));
                    for(int j =0 ; j< (dpis.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), fms[9]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), fms[9]));
                        if(j!=0)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), dpis[i-22]));
                    }
                } else if(i==32){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), dpis[i-24]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), dpis[i-24]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), dpis[i-24]));
                        if(j!=9)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), dpis[i-24]));
                    }
                }else {
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), dpis[i-24]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), dpis[i-22]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), dpis[i-22]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+(j+1)), dpis[i-24]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+(j+1)), dpis[i-24]));
                        if(j > ((i-3)%10))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), dpis[i-22]));
                        if(j < ((i-3)%10))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+(j+1)), dpis[i-24]));
                    }
                }
                net.routingTable(dpis[i-23], rtables.get(i));
            }
        }

        net.attach(client, server, nat);
        for(int i=0;i<fws.length;i++){
            net.attach(fws[i],fms[i],dpis[i]);

        }

        int[] s = {} ;

        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_client"),nctx.am.get("ip_server")));

        for(int i=0;i<dpis.length; i++){
            dpis[i].installIDS(s);
            //fws[i].addAcls(acl);
            fms[i].installFieldModifier(Optional.empty());
        }

        PacketModel packet = new PacketModel();
        packet.setEmailFrom(3);
        packet.setBody(5);
        packet.setProto(nctx.HTTP_REQUEST);
        packet.setIp_dest(nctx.am.get("ip_server"));
        client.installAsWebClient(nctx.am.get("ip_server"), packet);
        //server.installAsWebServer(new PacketModel());
        ArrayList<DatatypeExpr> internalAddress = new ArrayList<DatatypeExpr>();
//        for(int i = 0; i<(fws.length); i++){
//            internalAddress.add(nctx.am.get("ip_fw"+(i+1)));
//            internalAddress.add(nctx.am.get("ip_dpi"+(i+1)));
//            internalAddress.add(nctx.am.get("ip_fm"+(i+1)));
//        }
//        internalAddress.add(nctx.am.get("ip_client"));
        nat.natModel(nctx.am.get("ip_nat"));
        //nat.setInternalAddress(internalAddress);
        
        check = new Checker(ctx,nctx,net);
    }


    public void resetZ3() throws Z3Exception{
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        ctx = new Context(cfg);
    }

    public void printVector (Object[] array){
        int i=0;
        System.out.println( "*** Printing vector ***");
        for (Object a : array){
            i+=1;
            System.out.println( "#"+i);
            System.out.println(a);
            System.out.println(  "*** "+ i+ " elements printed! ***");
        }
    }

    public void printModel (Model model) throws Z3Exception{
        for (FuncDecl d : model.getFuncDecls()){
            System.out.println(d.getName() +" = "+ d.toString());
            System.out.println("");
        }
    }


    public static void main(String[] args) throws Z3Exception
    {
        System.out.println("Verification Start");
        Test_Scalability_FW_FM_DPI_NAT model = new Test_Scalability_FW_FM_DPI_NAT();
        model.resetZ3();
        Long time_reachability=(long) 0;
        Date start_time_reachability = Calendar.getInstance().getTime();
        IsolationResult ret =model.check.checkIsolationProperty(model.client,model.server);
        time_reachability = time_reachability +(Calendar.getInstance().getTime().getTime() - start_time_reachability.getTime());
        //model.printVector(ret.assertions);
        if (ret.result == Status.UNSATISFIABLE){
            System.out.println("UNSAT"); // Nodes a and b are isolated
        } else if (ret.result == Status.SATISFIABLE){
            System.out.println("SAT ");
            System.out.println(ret.model);
            //            System.out.print( "Model -> ");model.printModel(ret.model);
            //          System.out.println( "Violating packet -> " +ret.violating_packet);
            //          System.out.println("Last hop -> " +ret.last_hop);
            //          System.out.println("Last send_time -> " +ret.last_send_time);
            //          System.out.println( "Last recv_time -> " +ret.last_recv_time);
        }else{System.out.println("UNKNOWN");}
        System.out.print("Verification time: "+ time_reachability + "ms -> " + (time_reachability/1000) + "s");
    }
}
