package tests.examples.cachenatfw;
import java.util.ArrayList;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import mcnet.components.Checker;
import mcnet.components.NetContext;
import mcnet.components.Network;
import mcnet.components.NetworkObject;
import mcnet.components.Tuple;
import mcnet.netobjs.PolitoWebServer;
import mcnet.netobjs.PolitoWebClient;
import mcnet.netobjs.PolitoNat;
import mcnet.netobjs.AclFirewall;
import mcnet.netobjs.PolitoCache;

public class Scenario_1{
    public Checker check;
    public PolitoWebServer hostC;
    public PolitoWebServer hostB;
    public PolitoWebClient hostA;
    public PolitoNat politoNat;
    public AclFirewall politoFw;
    public PolitoCache politoCache;
    public Scenario_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"hostC", "hostB", "hostA", "politoNat", "politoFw", "politoCache"}, new String[]{"ip_hostC", "ip_hostB", "ip_hostA", "ip_politoNat", "ip_politoFw", "ip_politoCache"});
        Network net = new Network (ctx,new Object[]{nctx});
        hostC = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("hostC"), net, nctx});
        hostB = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("hostB"), net, nctx});
        hostA = new PolitoWebClient(ctx, new Object[]{nctx.nm.get("hostA"), net, nctx, nctx.am.get("ip_hostB")});
        politoNat = new PolitoNat(ctx, new Object[]{nctx.nm.get("politoNat"), net, nctx});
        politoFw = new AclFirewall(ctx, new Object[]{nctx.nm.get("politoFw"), net, nctx});
        politoCache = new PolitoCache(ctx, new Object[]{nctx.nm.get("politoCache"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_hostC"));
        adm.add(new Tuple<>(hostC, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_hostB"));
        adm.add(new Tuple<>(hostB, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_hostA"));
        adm.add(new Tuple<>(hostA, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_politoNat"));
        adm.add(new Tuple<>(politoNat, al3));
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        al4.add(nctx.am.get("ip_politoFw"));
        adm.add(new Tuple<>(politoFw, al4));
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        al5.add(nctx.am.get("ip_politoCache"));
        adm.add(new Tuple<>(politoCache, al5));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_hostC = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_hostC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), hostA));
        rt_hostC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), hostA));
        rt_hostC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), hostA));
        rt_hostC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), hostA));
        rt_hostC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), hostA));
        net.routingTable(hostC, rt_hostC);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_hostB = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_hostB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoFw));
        rt_hostB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoFw));
        rt_hostB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoFw));
        rt_hostB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoFw));
        rt_hostB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoFw));
        net.routingTable(hostB, rt_hostB);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_hostA = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_hostA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoCache));
        rt_hostA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoCache));
        rt_hostA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoCache));
        rt_hostA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoCache));
        rt_hostA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), hostC));
        net.routingTable(hostA, rt_hostA);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_politoNat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_politoNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoCache));
        rt_politoNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoFw));
        rt_politoNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoCache));
        rt_politoNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoFw));
        rt_politoNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoCache));
        net.routingTable(politoNat, rt_politoNat);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_politoFw = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_politoFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoNat));
        rt_politoFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoNat));
        rt_politoFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoNat));
        rt_politoFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), hostB));
        rt_politoFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoNat));
        net.routingTable(politoFw, rt_politoFw);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_politoCache = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_politoCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoNat));
        rt_politoCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoNat));
        rt_politoCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), hostA));
        rt_politoCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoNat));
        rt_politoCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), hostA));
        net.routingTable(politoCache, rt_politoCache);
        net.attach(hostC, hostB, hostA, politoNat, politoFw, politoCache);
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
        ia.add(nctx.am.get("ip_hostA"));
        ia.add(nctx.am.get("ip_hostC"));
        politoNat.setInternalAddress(ia);
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_politoNat"),nctx.am.get("ip_hostB")));
        politoCache.installCache(new NetworkObject[]{hostA});
        check = new Checker(ctx,nctx,net);
    }
}

