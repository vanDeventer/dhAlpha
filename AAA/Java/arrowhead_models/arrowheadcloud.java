package com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models;

public class arrowheadcloud {
	private int id;
	private String operator;
	private String cloudName;
	//private String gatekeeperIP;
	//private String gatekeeperPort;
	//private String gatekeeperURI;
	private String authenticationInfo;
	
	public arrowheadcloud(){
		
	}
	
	public arrowheadcloud(String operator, String cloudName, String authenticationInfo) {
		this.operator = operator;
		this.cloudName = cloudName;
		//this.gatekeeperIP = gatekeeperIP;
		//this.gatekeeperPort = gatekeeperPort;
		//this.gatekeeperURI = gatekeeperURI;
		this.authenticationInfo = authenticationInfo;
	}

        public int getId() {
            return id;
        }

        public String getCloudName() {
             return cloudName;
         }

        public void setId(int id) {
            this.id = id;
        }

        public void setCloudName(String cloudName) {
            this.cloudName = cloudName;
        }
        
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

	
		
	public String getAuthenticationInfo() {
		return authenticationInfo;
	}

	public void setAuthenticationInfo(String authenticationInfo) {
		this.authenticationInfo = authenticationInfo;
	}
	

}