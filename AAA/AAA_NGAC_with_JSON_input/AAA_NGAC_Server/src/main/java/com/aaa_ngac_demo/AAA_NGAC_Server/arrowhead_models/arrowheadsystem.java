package com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models;

import javax.xml.bind.annotation.XmlRootElement;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.node;
@XmlRootElement
public class arrowheadsystem {
	private int id;


	private String IPAddress;
	private String port;
        private String systemName;
	private String authenticationInfo;
	private node node_id;
	public arrowheadsystem(){
		
	}
	
	public arrowheadsystem(	String iPAddress, String port, String authenticationInfo,String systemName, node node_id) {
		super();
		this.IPAddress = iPAddress;
		this.port = port;
		this.authenticationInfo = authenticationInfo;
                this.node_id=node_id;
                this.systemName=systemName;
	}

        public int getId() {
            return id;
        }

         public void setId(int id) {
            this.id = id;
        }

        public String getSystemName() {
            return systemName;
        }

        public void setSystemName(String systemName) {
            this.systemName = systemName;
        }
        
        public node getNode_id() {
             return node_id;
        }

        public void setNode_id(node node_id) {
             this.node_id = node_id;
        }

	

	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAuthenticationInfo() {
		return authenticationInfo;
	}

	public void setAuthenticationInfo(String authenticationInfo) {
		this.authenticationInfo = authenticationInfo;
	}
	
	
}
