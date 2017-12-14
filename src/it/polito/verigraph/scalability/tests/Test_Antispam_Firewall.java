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
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;

public class Test_Antispam_Firewall {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a;
    public PolitoMailServer b;
    public PolitoAntispam antispam1;
    public AclFirewall fw;

    public  Test_Antispam_Firewall(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "antispam1", "fw"},
                                                new String[]{"ip_a", "ip_b", "ip_antispam1", "ip_fw"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoMailServer(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        antispam1 = new PolitoAntispam(ctx, new Object[]{nctx.nm.get("antispam1"), net, nctx});
        fw = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_antispam1"));
        al4.add(nctx.am.get("ip_fw"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(antispam1, al3));
        adm.add(new Tuple<>(fw, al4));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam1"), antispam1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), antispam1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"), antispam1));

        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), fw));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam1"), fw));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"), fw));

        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),fw));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw"),fw));

        net.routingTable(antispam1, rt3);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),antispam1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam1"),antispam1));

        net.routingTable(fw, rt4);

        net.attach(a, b, antispam1,fw);

        //Configuring middleboxes
        int[] s = { 1,2,3} ;
        antispam1.addBlackList(s);

        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_b")));
        fw.addAcls(acl);

        PacketModel packet = new PacketModel();
        packet.setEmailFrom(3);
        packet.setProto(nctx.POP3_REQUEST);
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
        Test_Antispam_Firewall model = new Test_Antispam_Firewall();
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