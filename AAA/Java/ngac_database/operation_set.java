/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database;

import org.hibernate.annotations.Proxy;

/**
 *
 * @author cripan-local
 */

public class operation_set {
    private int id; 
    private operation operation_id; 
    private node node_id; 

    public operation_set() {
    }


    public operation_set(operation operation_id, node node_id) {
        this.operation_id = operation_id;
        this.node_id = node_id;
    }
        public int getId() {
        return id;
    }

    public operation getOperation_id() {
        return operation_id;
    }

    public node getNode_id() {
        return node_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOperation_id(operation operation_id) {
        this.operation_id = operation_id;
    }

    public void setNode_id(node node_id) {
        this.node_id = node_id;
    }

    
}
