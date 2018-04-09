package com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.operation_set;
@XmlRootElement
public class arrowheadservice {

	private int id;
	private String metaData;
        private String serviceName;
	private operation_set set_id;
        
        
	public arrowheadservice(){
		
	}
	
	public arrowheadservice( String metaData,String serviceName, operation_set set_id) {
		super();
		this.metaData = metaData;
                this.set_id=set_id;
                this.serviceName=serviceName;
	}

         public int getId() {
             return id;
         }

        public operation_set getSet_id() {
             return set_id;
        }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

        public void setId(int id) {
                this.id = id;
        }

        public void setSet_id(operation_set set_id) {
                this.set_id = set_id;
        }

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	
	
}
