/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;

import it.polito.verigraph.mcnet.components.NetContext;

/**
 * Data structure for the response to check requests for isolation properties
 *
 */
public class IsolationResult {

    Context ctx;
    public NetContext nctx;
    public Status result;
    public Model model;
    public Expr violating_packet,last_hop;
    public BoolExpr [] assertions;
    public BoolExpr [] unsat_core;
    public String reason_unknown;

    public IsolationResult(Context ctx,Status result, Expr violating_packet, Expr last_hop, NetContext nctx, BoolExpr[] assertions, Model model, BoolExpr[] unsat, String unknown){
        this.ctx = ctx;
        this.result = result;
        this.violating_packet = violating_packet;
        this.last_hop = last_hop;
        this.model = model;
        this.assertions = assertions;
        this.unsat_core=unsat;
        this.reason_unknown=unknown;
    }
}