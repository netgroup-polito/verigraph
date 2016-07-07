/*
Copyright 2016 Politecnico di Torino
Authors:
Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)

This file is part of Verigraph.

    Verigraph is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    Verigraph is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public
    License along with Verigraph.  If not, see
    <http://www.gnu.org/licenses/>. 
*/
package mcnet.netobjs;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;

import mcnet.components.NetContext;
import mcnet.components.Network;
import mcnet.components.NetworkObject;

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
	
	public void installFieldModifier (){
		Expr x = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_x", nctx.node);
    	Expr y = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_y", nctx.node);
    	
    	Expr p_0 = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_p_0", nctx.packet);
    	Expr p_1 = ctx.mkConst("politoFieldModifier_"+politoFieldModifier+"_p_1", nctx.packet);
    	
    	IntExpr t_0 = ctx.mkIntConst("politoFieldModifier_"+politoFieldModifier+"_t_0");
    	IntExpr t_1 = ctx.mkIntConst("politoFieldModifier_"+politoFieldModifier+"_t_1");
    	
    	constraints.add(
    			ctx.mkForall(new Expr[]{t_0, p_0, x},
    				ctx.mkImplies((BoolExpr)nctx.send.apply(politoFieldModifier,x,p_0,t_0), 
    							  ctx.mkExists(new Expr[]{y, p_1, t_1}, 
  	    							 ctx.mkAnd(ctx.mkLt(t_1, t_0),
  	    									   (BoolExpr)nctx.recv.apply(y, politoFieldModifier, p_1, t_1),
  	    									   ctx.mkEq(nctx.pf.get("encrypted").apply(p_0), nctx.pf.get("encrypted").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("src").apply(p_0), nctx.pf.get("src").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("dest").apply(p_0), nctx.pf.get("dest").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("inner_src").apply(p_0), nctx.pf.get("inner_src").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("inner_dest").apply(p_0), nctx.pf.get("inner_dest").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("origin").apply(p_0), nctx.pf.get("origin").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("orig_body").apply(p_0), nctx.pf.get("orig_body").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("body").apply(p_0), nctx.pf.get("body").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("seq").apply(p_0), nctx.pf.get("seq").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("proto").apply(p_0), nctx.pf.get("proto").apply(p_1)),
  	    									   ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0), nctx.pf.get("emailFrom").apply(p_1)),
  	    									   ctx.mkNot(ctx.mkEq(nctx.pf.get("url").apply(p_0), nctx.pf.get("url").apply(p_1))),
  	    									   ctx.mkEq(nctx.pf.get("options").apply(p_0), nctx.pf.get("options").apply(p_1))),1,null,null,null,null)),
    			1,null,null,null,null));
	}

}
