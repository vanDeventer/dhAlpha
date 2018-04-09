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
public class operation {
    private int id;
    private operation_type operation_type_id;
    private String name;
    private String description;
     
	public operation(){}
	
	public operation(operation_type operation_type_id, String name ,String description) {
		super();
		this.name = name;
		this.description = description;
                this.operation_type_id=operation_type_id;

	}

    public void setId(int id) {
        this.id = id;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public operation_type getOperation_type_id() {
        return operation_type_id;
    }

    public void setOperation_type_id(operation_type operation_type_id) {
        this.operation_type_id = operation_type_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
