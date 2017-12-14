package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | FM1 | <------> | FM2 | <------> | FM2 |<------> | B |
 */

import java.util.ArrayList;
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
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoFieldModifier;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;

public class Test_3FieldModifiers {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a;
    public PolitoWebServer b;
    public PolitoFieldModifier modifier1, modifier2, modifier3;

    public  Test_3FieldModifiers(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "modifier1","modifier2","modifier3"},
                                                new String[]{"ip_a", "ip_b", "ip_modifier1","ip_modifier2","ip_modifier3"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        modifier1 = new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get("modifier1"), net, nctx});
        modifier2 = new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get("modifier2"), net, nctx});
        modifier3 = new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get("modifier3"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_modifier1"));
        al4.add(nctx.am.get("ip_modifier2"));
        al5.add(nctx.am.get("ip_modifier3"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(modifier1, al3));
        adm.add(new Tuple<>(modifier2, al4));
        adm.add(new Tuple<>(modifier3, al5));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
//        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier1"), modifier1));
//        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier2"), modifier1));
//        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier3"), modifier1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), modifier1));

        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), modifier3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier1"), modifier3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier2"), modifier3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier3"), modifier3));

        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
//        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
//        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier2"),modifier2));
//        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier3"),modifier2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),modifier2));

        net.routingTable(modifier1, rt3);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
//        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),modifier1));
//        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier1"),modifier1));
//        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier3"),modifier3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),modifier3));

        net.routingTable(modifier2, rt4);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt5 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
//        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),modifier2));
//        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier1"),modifier2));
//        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_modifier2"),modifier2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));

        net.routingTable(modifier3, rt5);

        net.attach(a, b, modifier1, modifier2, modifier3);

        //Configuring middleboxes
        PacketModel packet = new PacketModel();
        packet.setEmailFrom(1);
        packet.setIp_dest(nctx.am.get("ip_b"));
        packet.setProto(nctx.HTTP_REQUEST);packet.setOptions(10);
        a.installEndHost(packet);

        packet.setEmailFrom(101);
        modifier1.installFieldModifier(Optional.of(packet));
        packet.setEmailFrom(102);
        modifier2.installFieldModifier(Optional.of(packet));
        packet.setEmailFrom(103);
        modifier3.installFieldModifier(Optional.of(packet));


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
        Test_3FieldModifiers model = new Test_3FieldModifiers();
        model.resetZ3();
        
        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
        model.printVector(ret.assertions);
        if (ret.result == Status.UNSATISFIABLE){
           System.out.println("UNSAT"); // Nodes a and b are isolated
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