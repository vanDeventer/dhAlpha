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
public class node {
    private int id;
    public int nodetype_id;
    private String name;
    private String description;
     
	public node(){}
	
	public node(int nodetype_id,String name ,String description) {
		super();
                this.nodetype_id=nodetype_id;
		this.name = name;
		this.description = description;

	}
        
	public int getId() {
		return id;
	}

	public void setId(int node_id) {
		this.id = node_id;
	}

	public String getName() {
		return name;
	}

         public void setNodetype_id(int nodetype_id) {
                 this.nodetype_id = nodetype_id;
        }
         
         public int getNodetype_id() {
                 return nodetype_id;
        }

	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
