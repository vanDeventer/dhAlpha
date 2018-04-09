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
public class operation_type {
  private int id;
    private String name;

	public operation_type(){}
	
	public operation_type(String name) {
		super();
		this.name = name;
	

	}

	public int getId() {
		return id;
	}

	public void setId(int optype_id) {
		this.id = optype_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
