package com.aaa_ngac_demo.AAA_NGAC_Producer;

import java.net.InetAddress;

import org.eclipse.californium.scandium.dtls.SessionId;

public class AcceptedTokens {
	private String Consumer;
	private String Producer;
	private String ServiceName;
	private InetAddress IPAddress;
	private int Port;
	private SessionId DtlsSID;
	private long ExpirationTime;
	private int SessionID;
	
	
	public AcceptedTokens()
	{
		this.Consumer = null;
		this.Producer = null;
		this.ServiceName = null;
		this.ExpirationTime = 0;
		this.IPAddress = null;
		this.Port = 0;
		this.DtlsSID = null;
		this.SessionID=0;
	}
	public AcceptedTokens(String Consumer, String Producer, String ServiceName, long ExpirationTime, InetAddress IPAddress, int Port, SessionId DtlsSID, int SessionID)
	{
		this.Consumer = Consumer;
		this.Producer = Producer;
		this.ServiceName = ServiceName;
		this.SessionID = SessionID;
		this.ExpirationTime = ExpirationTime;
		this.IPAddress = IPAddress;
		this.Port = Port;
		this.DtlsSID = DtlsSID;
		
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
	public SessionId getDtlsSID()
	{
		return this.DtlsSID;
	}
	public long getExpirationTime()
	{
		return this.ExpirationTime;
	}
	public int getSessionID()
	{
		return this.SessionID;
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
	public void setDtlsSID(SessionId DtlsSID)
	{
		this.DtlsSID = DtlsSID;
	}
	public void setExpirationTime(long ExpirationTime)
	{
		this.ExpirationTime = ExpirationTime;
	}
	public void setSessionID(int SessionID)
	{
		this.SessionID = SessionID;
	}

}
