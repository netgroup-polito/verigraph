package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | CACHE |<------> | B |
 */

import java.util.ArrayList;
import java.util.HashMap;

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
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoVpnAccess;
import it.polito.verigraph.mcnet.netobjs.PolitoVpnExit;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;
import it.polito.verigraph.mcnet.netobjs.PacketModel;

public class Test_Vpn {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a;
    public PolitoWebServer b;
    public PolitoVpnAccess access;
    public PolitoVpnExit exit;

    public  Test_Vpn(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "access", "exit"},
                                                new String[]{"ip_a", "ip_b", "ip_access", "ip_exit"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        access = new PolitoVpnAccess(ctx, new Object[]{nctx.nm.get("access"), net, nctx});
        exit = new PolitoVpnExit(ctx, new Object[]{nctx.nm.get("exit"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_access"));
        al4.add(nctx.am.get("ip_exit"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(access, al3));
        adm.add(new Tuple<>(exit, al4));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_access"), access));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), access));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_exit"), access));

        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), exit));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_access"), exit));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_exit"), exit));

        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),exit));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_exit"),exit));

        net.routingTable(access, rt3);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),access));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_access"),access));

        net.routingTable(exit, rt4);

        net.attach(a, b, access,exit);

        //Configuring middleboxes

        access.vpnAccessModel(nctx.am.get("ip_access"),nctx.am.get("ip_exit")); 
        
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>());
        exit.vpnExitModel(nctx.am.get("ip_access"),nctx.am.get("ip_exit"));
        
        PacketModel packet = new PacketModel();
        packet.setEmailFrom(4);
        packet.setProto(nctx.HTTP_REQUEST);
        packet.setIp_dest(nctx.am.get("ip_b"));
        a.installEndHost(packet);

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
        Test_Vpn model = new Test_Vpn();
        model.resetZ3();
        
        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
        model.printVector(ret.assertions);
        System.out.println("--------------------------------------------");
        if (ret.result == Status.UNSATISFIABLE){
           System.out.println("UNSAT"); // Nodes a and b are isolated
           
           
           /*solver.getProof().simplify());
           System.out.println("core: ");
           for (Expr c : solver.getUnsatCore())
           {
               System.out.println(c);
           }*/
        }else{
            System.out.println("SAT ");
            System.out.println(ret.model);
//            System.out.print( "Model -> ");model.printModel(ret.model);
//          System.out.println( "Violating packet -> " +ret.violating_packet);
//          System.out.println("Last hop -> " +ret.last_hop);
//          System.out.println("Last send_time -> " +ret.last_send_time);
//          System.out.println( "Last recv_time -> " +ret.last_recv_time);
        }
    }
}