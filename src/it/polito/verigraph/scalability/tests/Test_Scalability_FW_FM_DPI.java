package it.polito.verigraph.scalability.tests;
//CLIENT - FW1 - ......- FM1 - .......- DPI1 - ...... - SERVER
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
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;

public class Test_Scalability_FW_FM_DPI {

    public Checker check;
    public Context ctx;
    public PolitoEndHost client;//, server;
    public PolitoWebServer server;
    public AclFirewall[] fws; 
    public PolitoFieldModifier[] fms;
    public PolitoIDS[] dpis;

    public Test_Scalability_FW_FM_DPI(int num){
        HashMap<String, String> options = new HashMap<>();
        options.put("proof", "true");
        options.put("model", "true");
        options.put("unsat_core", "true");
        ctx = new Context(options);

        fws = new AclFirewall[num];
        fms = new PolitoFieldModifier[num];
        dpis = new PolitoIDS[num];
        //ctx = new Context();

        String[] name = new String[(num*3)+2];
        String[] address = new String[(num*3)+2];
        for(int i =0; i< num; i++){
            name[i] = "fw"+i;
            name[i+num] = "fm"+i;
            name[i+(num*2)] ="dpi"+i;
            address[i] = "ip_fw"+i;
            address[i+num] = "ip_fm"+i;
            address[i+(num*2)] ="ip_dpi"+i;
        }
        name[(num*3)]="client";
        name[(num*3)+1]="server";
        address[(num*3)]="ip_client";
        address[(num*3)+1]="ip_server";

        NetContext nctx = new NetContext (ctx, name,address);
        Network net = new Network (ctx,new Object[]{nctx});

        client = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("client"), net, nctx});
        server = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("server"), net, nctx});
        for(int i = 0; i< num; i++){
            fws[i]= new AclFirewall(ctx, new Object[]{nctx.nm.get("fw"+i), net, nctx});
            fms[i]= new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get("fm"+i), net, nctx});
            dpis[i]= new PolitoIDS(ctx, new Object[]{nctx.nm.get("dpi"+i), net, nctx});
        }

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<ArrayList<DatatypeExpr>> als = new ArrayList<ArrayList<DatatypeExpr>>();
        for(int i =0 ; i< (2+(num*3)); i++)
            als.add( new ArrayList<DatatypeExpr>());

        for(int i =0 ;i< als.size(); i++){
            if(i >= 0 && i< num){ //i in [0,num-1]
                als.get(i).add(nctx.am.get("ip_fw"+i));
                adm.add(new Tuple<>(fws[i], als.get(i)));
            }else if(i >= num && i< 2*num){//i in [num,2*num -1]
                als.get(i).add(nctx.am.get("ip_fm"+(i-num)));
                adm.add(new Tuple<>(fms[i-num], als.get(i)));
            }else if(i >=2*num && i< 3*num){//i in [2*num,3*num -1]
                als.get(i).add(nctx.am.get("ip_dpi"+(i-2*num)));
                adm.add(new Tuple<>(dpis[i-2*num], als.get(i)));
            }else if (i == als.size() -2){ // 3*num
                als.get(i).add(nctx.am.get("ip_client"));
                adm.add(new Tuple<>(client, als.get(i)));
            }else if (i == als.size() -1){ // 3*num+1
                als.get(1).add(nctx.am.get("ip_server"));
                adm.add(new Tuple<>(server, als.get(1)));
            }
        }
        net.setAddressMappings(adm);

        ArrayList<ArrayList<Tuple<DatatypeExpr,NetworkObject>>> rtables = new ArrayList<ArrayList<Tuple<DatatypeExpr,NetworkObject>>>();
        for(int i =0 ; i< (3*num + 2); i++){
            rtables.add( new ArrayList<Tuple<DatatypeExpr,NetworkObject>>());

            if(i >=0  && i< num){ //firewalls
                if(i==0){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), client));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fws[i+1]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fws[i+1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), fws[i+1]));
                        if(j!=0)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fws[i+1]));
                    }
                } else if(i==num-1){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fws[i-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fms[0]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fms[0]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), fms[0]));
                        if(j!=num-1)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fws[i-1]));
                    }
                } else {
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fws[i-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fws[i+1]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fws[i+1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), fws[i+1]));
                        if(j > (i%num))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fws[i+1]));
                        if(j < (i%num))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fws[i-1]));
                    }
                }
                net.routingTable(fws[i], rtables.get(i));
            }else if(i >= num && i< 2*num){ //field modifiers
                if(i==num){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fws[num-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fms[(i%num)+1]));
                    for(int j =0 ; j< (fms.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fws[num-1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), fms[(i%num)+1]));
                        if(j!=0)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fms[(i%num)+1]));
                    }
                } else if(i==2*num-1){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fms[(i%num)-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), dpis[0]));
                    for(int j =0 ; j< (fws.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fms[(i%num)-1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), dpis[0]));
                        if(j!=num-1)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fms[(i%num)-1]));
                    }
                }else {
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fms[(i%num)-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fms[(i%num)+1]));
                    for(int j =0 ; j< (fms.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fms[(i%num)-1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), fms[(i%num)+1]));
                        if(j > (i%num))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fms[(i%num)+1]));
                        if(j < (i%num))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fms[(i%num)-1]));
                    }
                }
                net.routingTable(fms[(i%num)], rtables.get(i));
            }else if(i >=2*num && i< 3*num){ // dpis
                if(i==2*num){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), fms[num-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), dpis[(i%num)+1]));
                    for(int j =0 ; j< (dpis.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fms[num-1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fms[num-1]));
                        if(j!=0)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), dpis[(i%num)+1]));
                    }
                } else if(i==(3*num)-1){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), dpis[(i%num)-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), server));
                    for(int j =0 ; j< (dpis.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), dpis[(i%num)-1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), dpis[(i%num)-1]));
                        if(j!=num-1)
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), dpis[(i%num)-1]));
                    }
                }else {
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), dpis[(i%num)-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), dpis[(i%num)+1]));
                    for(int j =0 ; j< (dpis.length); j++){
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), dpis[(i%num)-1]));
                        rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), dpis[(i%num)-1]));
                        if(j > (i%num))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), dpis[(i%num)+1]));
                        if(j < (i%num))
                            rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), dpis[(i%num)-1]));
                    }
                }
                net.routingTable(dpis[i%num], rtables.get(i));
            } else if(i == 3*num){
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), fws[0]));
                for(int j =0 ; j< num; j++){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), fws[0]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), fws[0]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), fws[0]));
                }
                net.routingTable(client, rtables.get(i));
            } else if(i == 3*num+1){
                rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), dpis[num-1]));
                for(int j =0 ; j< num; j++){
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"+j), dpis[num-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fm"+j), dpis[num-1]));
                    rtables.get(i).add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"+j), dpis[num-1]));
                }
                net.routingTable(server, rtables.get(i));
            }
        }

        net.attach(client, server);
        for(int i=0;i<num;i++){
            net.attach(fws[i],fms[i],dpis[i]);

        }

        int[] s = {1} ;
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_fw1"),nctx.am.get("ip_server")));

                for(int i=0;i<dpis.length; i++){
                    dpis[i].installIDS(s);
                   // fws[i].addAcls(acl);
                    fms[i].installFieldModifier(Optional.empty());
                }

//        dpis[0].installIDS(s);
//        fws[0].addAcls(acl);
//        fms[0].installFieldModifier(Optional.empty());

        PacketModel packet = new PacketModel();
        packet.setEmailFrom(3);
        packet.setBody(5);
        packet.setProto(nctx.HTTP_REQUEST);
        packet.setIp_dest(nctx.am.get("ip_server"));
        client.installAsWebClient(nctx.am.get("ip_server"), packet);
//        server.installAsWebServer(new PacketModel());

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
        Test_Scalability_FW_FM_DPI model = new Test_Scalability_FW_FM_DPI(2);
        model.resetZ3();
        Long time_reachability=(long) 0;
        Date start_time_reachability = Calendar.getInstance().getTime();
        IsolationResult ret =model.check.checkIsolationProperty(model.client,model.server);
        time_reachability = time_reachability +(Calendar.getInstance().getTime().getTime() - start_time_reachability.getTime());
        model.printVector(ret.assertions);
        if (ret.result == Status.UNSATISFIABLE){
            System.out.println("UNSAT"); // Nodes a and b are isolated
           model.printVector(ret.unsat_core);
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
