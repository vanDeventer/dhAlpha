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
public class node_type {
    private int id;
    private String name;
    private String description;
     
	public node_type(){}
	
	public node_type(String name ,String description) {
		super();
		this.name = name;
		this.description = description;

	}

	public int getId() {
		return id;
	}

	public void setId(int nodetype_id) {
		this.id = nodetype_id;
	}

	public String getName() {
		return name;
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

