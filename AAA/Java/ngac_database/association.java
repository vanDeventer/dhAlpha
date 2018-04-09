/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database;

/**
 *
 * @author cripan-local
 */
public class association {
    private int id; 
    private int ua_id;
    private operation operation_id;
    private int oa_id;

    public association() {
    }

    public association(int ua_id, operation operation_id, int oa_id) {
        this.ua_id = ua_id;
        this.operation_id = operation_id;
        this.oa_id = oa_id;
    }

    public int getId() {
        return id;
    }

    public int getUa_id() {
        return ua_id;
    }

    public operation getOperation_id() {
        return operation_id;
    }

    public int getOa_id() {
        return oa_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUa_id(int ua_id) {
        this.ua_id = ua_id;
    }

    public void setOperation_id(operation opset_id) {
        this.operation_id = opset_id;
    }

    public void setOa_id(int oa_id) {
        this.oa_id = oa_id;
    }
    
    
}
