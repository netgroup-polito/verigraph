/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Test {
    private List<Node> nodes= new ArrayList<Node>();
    private String result;
    private String comment = "";
    @JsonIgnore
    private String textPath;
    @JsonIgnore
    private Node blockingNode;
    public Test() {

    }

    public Test(List<Node> paths, int result) {
        switch (result) {
        case 0:
            this.result = "SAT";
            break;
        case -1:
            this.result = "UNSAT";
            break;
        case -2:
            this.result = "UNKNOWN";
            break;
        default:
            this.result = "UNKNWON";
            break;
        }
        this.nodes = paths;
        
        textPath = "";
        for(Node n : paths) {
        	if(textPath.equals("")) {
        		textPath = n.getName();
        	} else {
        		textPath += " " + n.getName();
        	}
        }
    }

    public Test(List<Node> paths, String result) {
        this.nodes = paths;
        this.result = result;
        
        textPath = "";
        for(Node n : paths) {
        	if(textPath.equals("")) {
        		textPath = n.getName();
        	} else {
        		textPath += " " + n.getName();
        	}
        }
    }

    public List<Node> getPath() {
        return nodes;
    }
    
    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setBlockingNode(Node blockingNode) {
        this.blockingNode = blockingNode;
    }
}
