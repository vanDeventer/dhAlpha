package com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer;

import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.RESOURCE_ACCOUNTING_NAME;
import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.RESOURCE_ACCOUNTING_TITLE;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.APPLICATION_JSON;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;

import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.aaa_ngac_demo.AAA_NGAC_Server.Accounting.AccountingDatabaseManager;
import com.aaa_ngac_demo.AAA_NGAC_Server.Accounting.accounting_table;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.DatabaseManager;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.node;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.operation_set;

public class AccountingResource extends ConcurrentCoapResource{
	private static final Logger LOG = Logger.getLogger(AccountingResource.class.getName());
	public AccountingResource() 
	{
		super(RESOURCE_ACCOUNTING_NAME);
		getAttributes().setTitle(RESOURCE_ACCOUNTING_TITLE);
	}
	public AccountingResource(String name, int threads) {
		super(name, threads);
	}
public void handlePOST(CoapExchange exchange) {
	if (exchange.getRequestOptions().getContentFormat() == APPLICATION_JSON)
	 {
		 JSONParser parser = new JSONParser();
		  String Consumer = null;
		  String Producer = null;
		  String ServiceName = null;
		  int accSessionID = -1;
		  String RequestingEntity = null;
         node ua; 
         node oa;
         node op;
         operation_set op_set;
		   try{
			  JSONObject jsonObject = (JSONObject) parser.parse(exchange.getRequestText());
		      if(jsonObject.toString().indexOf("StartAccountingRequest")> -1)
		      {
			  JSONObject jo = (JSONObject) jsonObject.get("StartAccountingRequest");
			  Set<String> s = (Set<String>)jo.keySet();
			  Consumer = (String)jo.get("Consumer");
			  Producer = (String) jo.get("Producer");
			  ServiceName = (String)jo.get("ServiceName");
			  accSessionID = Integer.valueOf((String)jo.get("AccountingID"));
			  RequestingEntity = (String)jo.get("RequestingEntity");
		      System.out.println("start accounting request from " + RequestingEntity);
			  AccountingDatabaseManager adm = new AccountingDatabaseManager();
			  accounting_table ate = new accounting_table();
			  ate.setSessionID(accSessionID);
			  ate.setConsumer(Consumer);
			  ate.setProducer(Producer);
			  ate.setServiceName(ServiceName);
			  ate.setRequestingEntity(RequestingEntity);
			  ate.setSessionStartTime(System.currentTimeMillis());
			  boolean insert_success = false;
			  insert_success = adm.insertAccountingInfo(ate);
			  if(insert_success == true)
			  {
				  exchange.respond(CONTENT, "ok,"+accSessionID);
			  }
			  else
				  exchange.respond(CONTENT, "failed to make an entry in the accounting table");
		  	}
		   else if(jsonObject.toString().indexOf("StopAccountingRequest") > -1)
		   {
				  JSONObject jo = (JSONObject) jsonObject.get("StopAccountingRequest");
				  Set<String> s = (Set<String>)jo.keySet();
				  System.out.println(jo.get("SessionID"));
				  long SessionID =   Long.parseLong((String)jo.get("SessionID"));
				  RequestingEntity = (String)jo.get("RequestingEntity");
				  System.out.println("stop accounting request from "+ RequestingEntity);
				  long InRequestCounter =  Long.parseLong((String)jo.get("InRequestCounter"));
				  long OutResponseCounter =  Long.parseLong((String)jo.get("OutResponseCounter"));
				  long MinRequestSize =  Long.parseLong((String)jo.get("MinRequestSize"));
				  long MaxRequestSize =   Long.parseLong((String)jo.get("MaxRequestSize"));
				  long TerminationCause =  Long.parseLong((String)jo.get("TerminationCause"));
				  System.out.println("Reason for termination" + TerminationCause);
				  AccountingDatabaseManager adm = new AccountingDatabaseManager();
				  accounting_table accTableEntry = new accounting_table();
             	 accTableEntry.setSessionID((int)SessionID);
             	 accTableEntry.setRequestingEntity(RequestingEntity);
             	 accTableEntry.setInboundRequests((int)InRequestCounter);
             	 accTableEntry.setOutResponses((int)OutResponseCounter);
             	 accTableEntry.setMinRequestSize((int)MinRequestSize);
             	 accTableEntry.setMaxRequestSize((int)MaxRequestSize);
             	 accTableEntry.setSessionEndTime(System.currentTimeMillis());
             	 accTableEntry.setTerminationCause((int) TerminationCause);
             	 System.out.println("sesion end time " + System.currentTimeMillis());
             	 adm.updateAccountingTable(accTableEntry);
			   exchange.respond(CONTENT, "received accounting stop request");
		   }
		   else
			   exchange.respond(CONTENT, "received request is not accounting request");
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
}
}
}
