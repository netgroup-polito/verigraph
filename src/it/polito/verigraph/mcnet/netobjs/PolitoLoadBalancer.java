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

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;

import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;

/** Represents a Load balancer with the associated Access Control List
 *
 */
public class PolitoLoadBalancer extends NetworkObject{

    List<BoolExpr> constraints; 
    Context ctx;
    DatatypeExpr lb;
    Network net;
    NetContext nctx;
    FuncDecl acl_func;


    public PolitoLoadBalancer(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        lb = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        net.saneSend(this);
        balancerSendRules();
    }

   


    @Override
    public DatatypeExpr getZ3Node() {
        return lb;
    }


    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    private void balancerSendRules (){
        Expr p_0 = ctx.mkConst(lb+"_balancer_send_p_0", nctx.packet);
        Expr n_0 = ctx.mkConst(lb+"_balancer_send_n_0", nctx.node);
        Expr n_1 = ctx.mkConst(lb+"_balancer_send_n_1", nctx.node);
        //IntExpr t_0 = ctx.mkIntConst(lb+"_balancer_send_t_0");
        //IntExpr t_1 = ctx.mkIntConst(lb+"_balancer_send_t_1");

        //Constraint1		send(lb, n_0, p, t_0)  -> (exist n_1,t_1 : (recv(n_1, lb, p, t_1) && 
        //    				t_1 < t_0 && !acl_func(p.src,p.dest))
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0}, 
                        ctx.mkImplies(
                                (BoolExpr)nctx.send.apply(new Expr[]{ lb, n_0, p_0}),
                                ctx.mkAnd(
                                        ctx.mkExists(new Expr[]{n_1}, 
                                                nctx.recv.apply(n_1, lb, p_0),1,null,null,null,null)
                                        )
                                ),1,null,null,null,null));

    }

}