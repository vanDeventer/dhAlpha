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
public class assignment_path {
    private int id;
    private node node_id;
  
     
	public assignment_path(){}
	
	public assignment_path(node assignment_node_id) {
		super();
                this.node_id=assignment_node_id;
		
	}

    public int getId() {
        return id;
    }

    public node getNode_id() {
        return node_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNode_id(node assignment_node_id) {
        this.node_id = assignment_node_id;
    }
        
}
