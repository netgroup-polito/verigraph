package it.polito.verigraph.test;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | FW | <------> | FW | <------> | FW |<------> | B |
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
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoLoadBalancer;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.PacketModel;

public class Test_Panda_Comparison_Single {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a;
    public PolitoEndHost b;
    public AclFirewall fw1, fw2;
    public PolitoLoadBalancer lb1, lb2, lb3, lb4;
    public PolitoIDS ids1, ids2;
    
    

    public  Test_Panda_Comparison_Single(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "fw1","fw2","lb1","lb2","ids1","ids2"},
                                                new String[]{"ip_a", "ip_b", "ip_fw1","ip_fw2","ip_lb1","ip_lb2","ip_ids1","ip_ids2"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        fw1 = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw1"), net, nctx});
        fw2 = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw2"), net, nctx});
        lb1 = new PolitoLoadBalancer(ctx, new Object[]{nctx.nm.get("lb1"), net, nctx});
        lb2 = new PolitoLoadBalancer(ctx, new Object[]{nctx.nm.get("lb2"), net, nctx});
        ids1 = new PolitoIDS(ctx, new Object[]{nctx.nm.get("ids1"), net, nctx});
        ids2 = new PolitoIDS(ctx, new Object[]{nctx.nm.get("ids2"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al7 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al8 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_fw1"));
        al4.add(nctx.am.get("ip_fw2"));
        al5.add(nctx.am.get("ip_lb1"));
        al6.add(nctx.am.get("ip_lb2"));
        al7.add(nctx.am.get("ip_ids1"));
        al8.add(nctx.am.get("ip_ids2"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(fw1, al3));
        adm.add(new Tuple<>(fw2, al4));
        adm.add(new Tuple<>(lb1, al5));
        adm.add(new Tuple<>(lb2, al6));
        adm.add(new Tuple<>(ids1, al7));
        adm.add(new Tuple<>(ids2, al8));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rta = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rta.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), lb1));
        net.routingTable(a, rta);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtlb1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtlb1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), a));
        rtlb1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), fw1));
        net.routingTable(lb1, rtlb1);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtfw1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtfw1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), lb1));
        rtfw1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), ids1));
        net.routingTable(fw1, rtfw1);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtids1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtids1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), fw1));
        rtids1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), ids2));
        net.routingTable(ids1, rtids1);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtids2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtids2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), ids1));
        rtids2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), fw2));
        net.routingTable(ids2, rtids2);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtfw2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtfw2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), ids2));
        rtfw2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), lb2));
        net.routingTable(fw2, rtfw2);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtlb2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtlb2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), fw2));
        rtlb2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), b));
        net.routingTable(lb2, rtlb2);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtb = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtb.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), lb2));
        net.routingTable(b, rtb);

      //Configuring middleboxes
        int[] s1= {1};
        ids1.installIDS(s1);
        int[] s2= {2};
        ids2.installIDS(s2);
        
        net.attach(a, b, lb1, lb2, fw1,fw2,ids1,ids2);

        //Configuring middleboxes
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_fw1"),nctx.am.get("ip_a")));
      
        PacketModel packet = new PacketModel();
        packet.setBody(4);
        packet.setProto(nctx.HTTP_REQUEST);
        packet.setIp_dest(nctx.am.get("ip_b"));
        a.installAsWebClient(nctx.am.get("ip_b"),packet);
       
        fw1.addAcls(acl);
        fw2.addAcls(acl);
        b.installAsWebServer(packet);
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
        Test_Panda_Comparison_Single model = new Test_Panda_Comparison_Single();
        model.resetZ3();
        long startTime = System.currentTimeMillis();
        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time: "+elapsedTime);
        //model.printVector(ret.assertions);
        if (ret.result == Status.UNSATISFIABLE){
           System.out.println("UNSAT"); // Nodes a and b are isolated
        }else{
            System.out.println("**************************************************");
            System.out.println("SAT ");
           // System.out.println(ret.model);
//            System.out.print( "Model -> ");model.printModel(ret.model);
//          System.out.println( "Violating packet -> " +ret.violating_packet);
//          System.out.println("Last hop -> " +ret.last_hop);
//          System.out.println("Last send_time -> " +ret.last_send_time);
//          System.out.println( "Last recv_time -> " +ret.last_recv_time);
        }
    }
}