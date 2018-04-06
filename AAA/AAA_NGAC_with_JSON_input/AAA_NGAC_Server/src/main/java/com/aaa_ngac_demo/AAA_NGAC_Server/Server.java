package com.aaa_ngac_demo.AAA_NGAC_Server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.*;
import com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.*;
import com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models.*;
import org.hibernate.*;
import org.json.*;

import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.*;

/**
 * Hello world!
 *
 */
public class Server 
{
    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    public static void main( String[] args )
    {
    	//LOG.info("Inside log Server started");

    	createNodes();
    	Properties prop = new Properties();
    	    InputStream input = null;

    	    try {

    	        input = new FileInputStream("resources/config.properties");
    	        // load a properties file
    	        prop.load(input);
    	        TOKEN_EXPIRY_TIME_IN_MIN = Integer.valueOf(prop.getProperty("TOKEN_EXPIRY_TIME_IN_MIN"));
                AAA_SERVER_COAP_PORT= Integer.valueOf(prop.getProperty("AAA_SERVER_COAP_PORT"));
               // NGAC_DB_FILE= prop.getProperty("NGAC_DB_FILE");
                //System.out.println("db file is " + NGAC_DB_FILE);
    	    }
    	    catch(Exception e)
    	    {
    	    	e.printStackTrace();
    	    }
    	        CoapServ server = new CoapServ();
      //  server.addEndpoints();
    	        System.out.println("----AAA server started----");
        server.start();

    }
    
    public static void createNodes()
    {
        Declarations d = new Declarations();
        Procedures pr= new Procedures();
        d.add_nodeTypes();
        d.add_OperationTypes();
        try{
        	File f = new File("resources/ngac_db.json");
        	FileReader fr = new FileReader(f);
        	BufferedReader reader = new BufferedReader(fr); 
        	StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            String ngac_db_string = sb.toString();
			  JSONObject jsonObject = new JSONObject(ngac_db_string);	  
		  JSONObject jo = (JSONObject) jsonObject.get("ArrowheadCloud");
		  Set<String> s = (Set<String>)jo.keySet();
		  arrowheadcloud cloud= new arrowheadcloud((String)jo.get("Operator"),(String)jo.get("CloudName"),(String)jo.get("AuthenticationInfo"));
		  JSONArray json_arrowheadsystem_array = jo.getJSONArray("ArrowheadSystem");
		  JSONArray json_arrowheadservice_array = jo.getJSONArray("ArrowheadService");
		  JSONArray json_assignment_array = jo.getJSONArray("Assignment");
		  JSONArray json_association_array = jo.getJSONArray("Association");
		  ArrayList<node> nodearray = new ArrayList<node>();
		  ArrayList<arrowheadsystem> ah_sys_array = new ArrayList<arrowheadsystem>();
		  ArrayList<operation> operation_array = new ArrayList<operation>();
		  ArrayList<operation_set> operation_set_array= new ArrayList<operation_set>();
		  ArrayList<arrowheadservice> ah_service_array= new ArrayList<arrowheadservice>();
		  Iterator i = json_arrowheadsystem_array.iterator();
		  node n = null;
		  while(i.hasNext())
		  {
			  JSONObject obj = (JSONObject) i.next(); 
			  if(obj.get("NodeType").equals("User"))
			  {
				  n  = new node(d.User, "Node"+obj.get("ID"), (String)obj.get("Desc"));
				  ah_sys_array.add(new arrowheadsystem((String)obj.get("IP"),(String) obj.get("Port"),(String) obj.get("AuthenticationInfo"), (String) obj.get("Name"), n));
			  }
			  else if(obj.get("NodeType").equals("Obj"))
			  {
				  n  = new node(d.Obj, "Node"+obj.get("ID"), (String)obj.get("Desc"));
				  ah_sys_array.add(new arrowheadsystem((String)obj.get("IP"),(String) obj.get("Port"),(String) obj.get("AuthenticationInfo"), (String) obj.get("Name"), n));
			  }
			  else if(obj.get("NodeType").equals("U_attr"))
			  {
				  n  = new node(d.U_attr, "Node"+obj.get("ID"), (String)obj.get("Desc"));
			  }
			  else if(obj.get("NodeType").equals("O_attr"))
			  {
				  n  = new node(d.U_attr, "Node"+obj.get("ID"), (String)obj.get("Desc"));
			  }

			  nodearray.add(n);
		  }
		  i = json_arrowheadservice_array.iterator();
		  operation op = null;
		  operation_set ops = null;
		  while(i.hasNext())
		  {
			  JSONObject obj = (JSONObject) i.next(); 
				  n  = new node(d.Oper, (String)obj.get("Name"), (String)obj.get("Desc"));
				  op = new operation(d.resource,(String)obj.get("Name"), (String)obj.get("Desc"));
				  operation_array.add(op);
				  ops = new operation_set(op,n);
				  operation_set_array.add(ops);
				  ah_service_array.add(new arrowheadservice((String)obj.get("Metadata"),(String)obj.get("Name"),ops));
			  nodearray.add(n);
		  }
        
        DatabaseManager databaseManager=new DatabaseManager();
        Session session=databaseManager.getSessionFactory().openSession();
        session.beginTransaction();  
        i = nodearray.iterator();
        while(i.hasNext())
        {
        	session.save(i.next());
        }
        i = operation_array.iterator();
        while(i.hasNext())
        {
        	session.save(i.next());
        }
        i = operation_set_array.iterator();
        while(i.hasNext())
        {
        	session.save(i.next());
        }
        i = ah_sys_array.iterator();
        while(i.hasNext())
        {
        	session.save(i.next());
        }
        i = ah_service_array.iterator();
        while(i.hasNext())
        {
        	session.save(i.next());
        }
       
        session.save(cloud);
        session.getTransaction().commit();
        session.close();
		  i = json_assignment_array.iterator();
		  while(i.hasNext())
		  {
			  JSONObject obj = (JSONObject) i.next(); 
			  String nodeid1 = "Node"+obj.get("node1");
			  String nodeid2 = "Node"+obj.get("node2");
			  node n1 =null;
			  node n2 = null;
			  for(node node_iterator : nodearray)
			  {
				  if(node_iterator.getName().equals(nodeid1))
				  {
					  n1 = node_iterator;
				  }
				  else if(node_iterator.getName().equals(nodeid2))
				  {
					  n2 = node_iterator;
				  }
			  }
			  if(n1.equals(null) || n2.equals(null))
			  {
				  System.out.println("An error occured while ngac db insertion");
				  System.exit(0);
			  }
			 pr.createAssignment(n1, n2);
		  }
		  
		  i = json_association_array.iterator();
		  while(i.hasNext())
		  {
			  JSONObject obj = (JSONObject) i.next(); 
			  String nodeid1 = "Node"+obj.get("node1");
			  String nodeid2 = "Node"+obj.get("node2");
			  String serviceName = (String)obj.get("ServiceName");
			  node n1=null;
			  node n2= null;
			  operation o1 = null;
			  for(node node_iterator : nodearray)
			  {
				  if(node_iterator.getName().equals(nodeid1))
				  {
					  n1 = node_iterator;
				  }
				  else if(node_iterator.getName().equals(nodeid2))
				  {
					  n2 = node_iterator;
				  }
				  
			  }
			  for(operation op_iterator : operation_array)
			  {
				  if(op_iterator.getName().equals(serviceName))
				  {
					  o1 = op_iterator;
					 
				  }
				  
			  }
			  if(n1.equals(null) || n2.equals(null) ||  o1.equals(null))
				  {
					  System.out.println("An error occured while ngac db insertion");
					  System.exit(0);
				  }
			 pr.createAssociation(n1, n2, serviceName, o1);
		  }
        

        System.out.println("Successfully inserted NGAC database");
        
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
}
