package com.aaa_ngac_demo.AAA_NGAC_Consumer;

import java.net.InetAddress;

import org.eclipse.californium.scandium.dtls.SessionId;

public class AcceptedTokens {
	private String Consumer;
	private String Producer;
	private String ServiceName;
	private InetAddress IPAddress;
	private int Port;
	private int SessionID;
	private String Token;
	
	
	public AcceptedTokens()
	{
		this.Consumer = null;
		this.Producer = null;
		this.ServiceName = null;
		this.IPAddress = null;
		this.Port = 0;
		this.SessionID=0;
		this.Token = null;
	}
	public AcceptedTokens(String Consumer, String Producer, String ServiceName, InetAddress IPAddress, int Port, int SessionID, String Token)
	{
		this.Consumer = Consumer;
		this.Producer = Producer;
		this.ServiceName = ServiceName;
		this.SessionID = SessionID;
		this.IPAddress = IPAddress;
		this.Port = Port;
		this.Token = Token;
	}
	public String getConsumer()
	{
		return Consumer;
	}
	public String getProducer()
	{
		return Producer;
	}
	public String getServiceName()
	{
		return ServiceName;
	}
	public InetAddress getIPAddress()
	{
		return this.IPAddress;
	}
	public int getPort()
	{
		return this.Port;
	}
	public int getSessionID()
	{
		return this.SessionID;
	}
	public String getToken()
	{
		return this.Token;
	}
	public void setConsumer(String Consumer)
	{
		this.Consumer = Consumer;
	}
	public void setProducer(String Producer)
	{
		this.Producer = Producer;
	}
	public void setServiceName(String ServiceName)
	{
		this.ServiceName = ServiceName;
	}
	public void setIPAddress(InetAddress IPAddress)
	{
		this.IPAddress = IPAddress;
	}
	public void setPort(int Port)
	{
		this.Port = Port;
	}
	public void setSessionID(int SessionID)
	{
		this.SessionID = SessionID;
	}
	public void setToken(String Token)
	{
		this.Token = Token;
	}

}
