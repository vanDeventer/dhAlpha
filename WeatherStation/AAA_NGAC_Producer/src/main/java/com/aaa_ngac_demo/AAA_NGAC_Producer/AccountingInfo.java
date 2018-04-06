package com.aaa_ngac_demo.AAA_NGAC_Producer;

public class AccountingInfo {
	private int SessionID;
    private static int InRequestCounter = 0;
    private static int OutResponseCounter = 0;
    private static int MinRequestSize = 0;
    private static int MaxRequestSize = 0;
    private static int IPaddressChange = 0;
    private int TerminationCause;
    
    public void setSessionID(int SessionID)
    {
    	this.SessionID = SessionID;
    }
    
    public void setInRequestCounter(int InRequestCounter)
    {
    	this.InRequestCounter = InRequestCounter;
    }
    public void setOutResponseCounter(int OutResponseCounter)
    {
    	this.OutResponseCounter = OutResponseCounter;
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
    
    public int getSessionID()
    {
    	return this.SessionID;
    }
    
   public int getInRequestCounter()
    {
    	return this.InRequestCounter;
    }
    public int getOutResponseCounter()
    {
    	return this.OutResponseCounter;
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
    public void incrementInRequestCounter()
    {
    	this.InRequestCounter++;
    }
    public void incrementOutResponseCounter()
    {
    	this.OutResponseCounter++;
    }
}
