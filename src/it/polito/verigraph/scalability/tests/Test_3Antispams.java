package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | spam | <------> | spam | <------> | spam |<------> | B |
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
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;

public class Test_3Antispams {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a;
    public PolitoEndHost b;
    public PolitoAntispam spam1, spam2, spam3;

    public  Test_3Antispams(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "spam1","spam2","spam3"},
                new String[]{"ip_a", "ip_b", "ip_spam1","ip_spam2","ip_spam3"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        spam1 = new PolitoAntispam(ctx, new Object[]{nctx.nm.get("spam1"), net, nctx});
        spam2 = new PolitoAntispam(ctx, new Object[]{nctx.nm.get("spam2"), net, nctx});
        spam3 = new PolitoAntispam(ctx, new Object[]{nctx.nm.get("spam3"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_spam1"));
        al4.add(nctx.am.get("ip_spam2"));
        al5.add(nctx.am.get("ip_spam3"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(spam1, al3));
        adm.add(new Tuple<>(spam2, al4));
        adm.add(new Tuple<>(spam3, al5));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam1"), spam1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam2"), spam1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam3"), spam1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), spam1));

        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), spam3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam1"), spam3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam2"), spam3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam3"), spam3));

        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam2"),spam2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam3"),spam2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),spam2));

        net.routingTable(spam1, rt3);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),spam1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam1"),spam1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam3"),spam3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),spam3));

        net.routingTable(spam2, rt4);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt5 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),spam2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam1"),spam2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_spam2"),spam2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));

        net.routingTable(spam3, rt5);

        net.attach(a, b, spam1, spam2, spam3);

        //Configuring middleboxes
        int[] s1= {1};
        spam1.addBlackList(s1);
        int[] s2= {2};
        spam2.addBlackList(s2);
        int[] s3= {3};
        spam3.addBlackList(s3);
        
        
        PacketModel packet = new PacketModel();
        packet.setEmailFrom(4);
        //packet.setIp_dest(nctx.am.get("ip_b"));
        a.installAsPOP3MailClient(nctx.am.get("ip_b"),packet);
        b.installAsPOP3MailServer(new PacketModel());

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
        Test_3Antispams model = new Test_3Antispams();
        model.resetZ3();

        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
        model.printVector(ret.assertions);
        if (ret.result == Status.UNSATISFIABLE){
            System.out.println("UNSAT"); // Nodes a and b are isolated
        }else{
            System.out.println("**************************************************");
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