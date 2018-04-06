package com.aaa_ngac_demo.AAA_NGAC_Server.Accounting;

public class accounting_table {

	private int ID;
	private int SessionID;
    private String Consumer;
    private String Producer;
    private String ServiceName;
    private String RequestingEntity;
    private int InboundRequests;
    private int OutResponses;
    private long SessionStartTime;
    private long SessionEndTime;
    private int MinRequestSize;
    private int MaxRequestSize;
    private int IPaddressChange;
    private int TerminationCause;
    
    public void setID(int ID)
    {
    	this.ID = ID;
    }
    public void setSessionID(int SessionID)
    {
    	this.SessionID = SessionID;
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
    public void setRequestingEntity(String RequestingEntity)
    {
    	this.RequestingEntity = RequestingEntity;
    }
    public void setInboundRequests(int InboundRequests)
    {
    	this.InboundRequests = InboundRequests;
    }
    public void setOutResponses(int OutResponses)
    {
    	this.OutResponses = OutResponses;
    }
    public void setSessionStartTime(long SessionStartTime)
    {
    	this.SessionStartTime = SessionStartTime;
    }
    public void setSessionEndTime(long SessionEndTime)
    {
    	this.SessionEndTime = SessionEndTime;
    }
    public void setMinRequestSize(int MinRequestSize)
    {
    	this.MinRequestSize = MinRequestSize;
    }
    public void setMaxRequestSize(int MaxRequestSize)
    {
    	this.MaxRequestSize = MaxRequestSize;
    }
    public void setIPaddressChange(int IPaddressChange)
    {
    	this.IPaddressChange = IPaddressChange;
    }
    public void setTerminationCause(int TerminationCause)
    {
    	this.TerminationCause = TerminationCause;
    }
    
    public int getID()
    {
    	return this.ID;
    }
    public int getSessionID()
    {
    	return this.SessionID;
    }
    
    public String getConsumer()
    {
    	return this.Consumer;
    }
    public String getProducer()
    {
    	return this.Producer;
    }
    public String getServiceName()
    {
    	return this.ServiceName;
    }
    public String getRequestingEntity()
    {
    	return this.RequestingEntity;
    }
    public int getInboundRequests()
    {
    	return this.InboundRequests;
    }
    public int getOutResponses()
    {
    	return this.OutResponses;
    }
    public long getSessionStartTime()
    {
    	return this.SessionStartTime;
    }
    public long getSessionEndTime()
    {
    	return this.SessionEndTime;
    }
    public int getMinRequestSize()
    {
    	return this.MinRequestSize;
    }
    public int getMaxRequestSize()
    {
    	return this.MaxRequestSize;
    }
    public int getIPaddressChange()
    {
    	return this.IPaddressChange;
    }
    public int getTerminationCause()
    {
    	return this.TerminationCause;
    }
}
