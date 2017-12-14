/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.netobjs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;

public class PolitoFieldModifier extends NetworkObject {

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoFieldModifier;
    Network net;
    NetContext nctx;

    public PolitoFieldModifier(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoFieldModifier;
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoFieldModifier = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    public void installFieldModifier (Optional<PacketModel> packet){
        Expr x = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_x", nctx.node);
        Expr y = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_y", nctx.node);

        Expr p_0 = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_p_0", nctx.packet);
        Expr p_1 = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_p_1", nctx.packet);
        BoolExpr predicatesOnPktFields = ctx.mkTrue();

        if(packet.isPresent() && packet.get().getIp_dest() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("dest").apply(p_0), packet.get().getIp_dest()));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("dest").apply(p_0), nctx.pf.get("dest").apply(p_1)));
        if(packet.isPresent() && packet.get().getBody() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("body").apply(p_0), ctx.mkInt(packet.get().getBody())));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("body").apply(p_0), nctx.pf.get("body").apply(p_1)));
        if(packet.isPresent() && packet.get().getEmailFrom() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0), ctx.mkInt(packet.get().getEmailFrom())));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0), nctx.pf.get("emailFrom").apply(p_1)));
        if(packet.isPresent() && packet.get().getOptions() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("options").apply(p_0), ctx.mkInt(packet.get().getOptions())));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("options").apply(p_0), nctx.pf.get("options").apply(p_1)));
        if(packet.isPresent() && packet.get().getProto() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(packet.get().getProto())));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("proto").apply(p_0), nctx.pf.get("proto").apply(p_1)));
        if(packet.isPresent() && packet.get().getSeq() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("seq").apply(p_0), ctx.mkInt(packet.get().getSeq())));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("seq").apply(p_0), nctx.pf.get("seq").apply(p_1)));
        if(packet.isPresent() && packet.get().getUrl() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("url").apply(p_0), ctx.mkInt(packet.get().getUrl())));
        else
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields,ctx.mkEq(nctx.pf.get("url").apply(p_0), nctx.pf.get("url").apply(p_1)));

        constraints.add(
                ctx.mkForall(new Expr[]{ p_0, x},
                        ctx.mkImplies((BoolExpr)nctx.send.apply(politoFieldModifier,x,p_0),
                                ctx.mkExists(new Expr[]{y, p_1},
                                        ctx.mkAnd(predicatesOnPktFields,
                                                (BoolExpr)nctx.recv.apply(y, politoFieldModifier, p_1),
                                                ctx.mkEq(nctx.pf.get("encrypted").apply(p_0), nctx.pf.get("encrypted").apply(p_1)),
                                                ctx.mkEq(nctx.pf.get("src").apply(p_0), nctx.pf.get("src").apply(p_1)),
                                                ctx.mkEq(nctx.pf.get("inner_src").apply(p_0), nctx.pf.get("inner_src").apply(p_1)),
                                                ctx.mkEq(nctx.pf.get("inner_dest").apply(p_0), nctx.pf.get("inner_dest").apply(p_1)),
                                                ctx.mkEq(nctx.pf.get("origin").apply(p_0), nctx.pf.get("origin").apply(p_1)),
                                                ctx.mkEq(nctx.pf.get("orig_body").apply(p_0), nctx.pf.get("orig_body").apply(p_1))
                                                ),1,null,null,null,null)),
                        1,null,null,null,null));
    }

}