package com.aaa_ngac_demo.AAA_NGAC_Server.Accounting;

import java.util.logging.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;



public class AccountingDatabaseManager {
	private static final Logger LOG = Logger.getLogger(AccountingDatabaseManager.class.getName());
	      private static SessionFactory sessionFactory;
	   
	    public AccountingDatabaseManager(){
	        if (sessionFactory == null){
	            sessionFactory = new Configuration().configure("hibernate_accounting.cfg.xml").buildSessionFactory();
	        }
	    }
	   
	    public SessionFactory getSessionFactory() {
	    	if (sessionFactory != null)
	    	return sessionFactory;
	    	else {
	    		sessionFactory = new Configuration().configure().buildSessionFactory();
	    		return sessionFactory;
	    	}
	    }
	    
	    public boolean insertAccountingInfo(accounting_table accounting_table_entry)
	    {
	    	Session session = getSessionFactory().openSession();
	    	Transaction transaction = null;
	    	
	    	try {
	    		transaction = session.beginTransaction();
	    		session.save(accounting_table_entry);

	            transaction.commit();
	          //  LOG.info("successfuly inserted in accounting table");
	            return true;
	        }
	        catch (Exception e) {
	            if (transaction!=null) transaction.rollback();
	            throw e;
	        }
	        finally {
	            session.close();
	        }
	    
	    }
	    public void updateAccountingTable(accounting_table accounting_table_entry) throws Exception
	    {
	    	Session session = getSessionFactory().openSession();
	    	Transaction transaction = null;
	    	
	    	try {
	    		transaction = session.beginTransaction();
	    		Criteria criteria = session.createCriteria(accounting_table.class);
	    		System.out.println("Session ID " + accounting_table_entry.getSessionID());
	    		System.out.println("Requesting Entity " + accounting_table_entry.getRequestingEntity());
	            criteria.add(Restrictions.eq("SessionID", accounting_table_entry.getSessionID()));
	            criteria.add(Restrictions.eq("RequestingEntity", accounting_table_entry.getRequestingEntity()));
	            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
	            accounting_table ate = (accounting_table) criteria.uniqueResult();
	            System.out.println("Session ID " + ate.getSessionID());
	            System.out.println("Requesting Entity " + ate.getRequestingEntity());
	            System.out.println("ID " + ate.getID());
	            System.out.println("Consumer " + ate.getConsumer());
	            if(ate!= null)
	            {
	            System.out.println(Integer.toString(ate.getID()));
	            ate.setSessionEndTime(accounting_table_entry.getSessionEndTime());
	            ate.setInboundRequests(accounting_table_entry.getInboundRequests());
	            ate.setOutResponses(accounting_table_entry.getOutResponses());
	            ate.setMinRequestSize(accounting_table_entry.getMinRequestSize());
	            ate.setMaxRequestSize(accounting_table_entry.getMaxRequestSize());
	            ate.setTerminationCause(accounting_table_entry.getTerminationCause());
	            session.update(ate);
	    		
	            }
	            else
	            	System.out.println("cannot retrieve row from accounting database");
	            	
	            transaction.commit();

	        }
	        catch (Exception e) {
	            if (transaction!=null) transaction.rollback();
	            throw e;
	        }
	        finally {
	            session.close();
	        }
	    	System.out.println("successfuly updated in accounting table");
	    }
	 
}
