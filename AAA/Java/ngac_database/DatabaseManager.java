/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database;


import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models.*;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.*;
import org.hibernate.Query;
import com.aaa_ngac_demo.AAA_NGAC_Server.exceptions.*;
/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author cripan-local
 */
public class DatabaseManager {

      private static SessionFactory sessionFactory;
   
    public DatabaseManager(){
        if (sessionFactory == null){
            sessionFactory = new Configuration().configure().buildSessionFactory();
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
 
    @SuppressWarnings("unchecked")
	public List<arrowheadcloud> getClouds(String operator){
    	List<arrowheadcloud> list;
    	
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		transaction = session.beginTransaction();
                  Query query= session.createQuery("SELECT c FROM arrowheadcloud c WHERE operator='"+operator+"'");
                  list = query.list();
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    	
    	return list;
    }
       public arrowheadsystem getSystemByName(String systemName){
    	arrowheadsystem arrowheadSystem;
    	List<arrowheadsystem> list;
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		 transaction = session.beginTransaction();
                 Query query= session.createQuery("SELECT s FROM arrowheadsystem s WHERE systemName='"+systemName+"'");
                  list = query.list();
                  arrowheadSystem=list.get(0);
             transaction.commit();
         }
         catch (Exception e) {
             if (transaction!=null) transaction.rollback();
             throw e;
         }
         finally {
             session.close();
         }
    	
    	return arrowheadSystem;
    }
    
       public arrowheadservice getServiceByName(String serviceName){
    	arrowheadservice arrowheadService;
    	List<arrowheadservice> list;
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		 transaction = session.beginTransaction();
                 Query query= session.createQuery("SELECT s FROM arrowheadservice s WHERE serviceName='"+serviceName+"'");
                  list = query.list();
                  arrowheadService=list.get(0);
             transaction.commit();
         }
         catch (Exception e) {
             if (transaction!=null) transaction.rollback();
             throw e;
         }
         finally {
             session.close();
         }
    	
    	return arrowheadService;
    }
    public arrowheadcloud getCloudByName(String operator, String cloudName){
    	arrowheadcloud arrowheadCloud;
    	List<arrowheadcloud> list;
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		 transaction = session.beginTransaction();
                Query query= session.createQuery("SELECT c FROM arrowheadcloud c WHERE operator='"+operator+"' AND cloudName='"+cloudName+"'");
                list = query.list();
                arrowheadCloud=list.get(0);
             if(arrowheadCloud == null){
            	 throw new DataNotFoundException("The consumer Cloud is not in the authorized database.");
             }
             transaction.commit();
         }
         catch (Exception e) {
             if (transaction!=null) transaction.rollback();
             throw e;
         }
         finally {
             session.close();
         }
    	
    	return arrowheadCloud;
    }

    public arrowheadcloud addCloudToAuthorized(arrowheadcloud arrowheadCloud){
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		transaction = session.beginTransaction();
    		session.save(arrowheadCloud);

            transaction.commit();
        }
    	catch(ConstraintViolationException e){
    		if (transaction!=null) transaction.rollback();
    		throw new DuplicateEntryException("There is already an entry in the database with these parameters.");
    	}
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    	
    	return arrowheadCloud;
    }
    
    public void deleteCloudFromAuthorized(String operator, String cloudName){
    	arrowheadcloud arrowheadCloud = getCloudByName(operator, cloudName);
    	if(arrowheadCloud == null)
    		throw new DataNotFoundException("Cloud not found in the database.");
    	
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		transaction = session.beginTransaction();
    		session.delete(arrowheadCloud);

            transaction.commit();
        }
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    }
    
    public void updateAuthorizedCloud(arrowheadcloud arrowheadCloud){
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		transaction = session.beginTransaction();
    		session.update(arrowheadCloud);

            transaction.commit();
        }
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    }
   
 
    
    public association getAssociation(arrowheadsystem consumer, arrowheadsystem provider, arrowheadservice service){
    	association ss = new association();
    	List<association> list;
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
            transaction = session.beginTransaction();
            node node_ua =consumer.getNode_id();
            int ua_id=node_ua.getId();
            node node_oa =provider.getNode_id();
            int oa_id=node_oa.getId();
            operation_set op_set=service.getSet_id();
            operation op=op_set.getOperation_id();
            
            Query query= session.createQuery("SELECT a FROM association a WHERE ua_id="+ua_id+"AND oa_id="+oa_id+"AND operation_id="+op);
            list = query.list();
            ss=list.get(0);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    	
   	return ss;
    }
    
    public List<association> getRelations(arrowheadsystem consumer){
    	List<association> ssList;
    	
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
            transaction = session.beginTransaction();
                        
                  node node =consumer.getNode_id();
                  int ua_id=node.getId();
                  Query query= session.createQuery("SELECT a FROM association a WHERE ua_id="+ua_id);
                  ssList = query.list();
                        
                        
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    	
   	return ssList;
    }
    
    public <T> T save(T object){
		Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		transaction = session.beginTransaction();
    		session.saveOrUpdate(object);
            transaction.commit();
        }
    	catch(ConstraintViolationException e){
    		if (transaction!=null) transaction.rollback();
    		throw new DuplicateEntryException("There is already an entry in the database with these parameters.");
    	}
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    	
    	return object;	
	}
	
	public <T> void delete(T object){
		Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		transaction = session.beginTransaction();
    		session.delete(object);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction!=null) transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }	
	}
	
        public node getNodeBySet(int opset_id){
           node Node;
    	List<node> list;
    	Session session = getSessionFactory().openSession();
    	Transaction transaction = null;
    	
    	try {
    		 transaction = session.beginTransaction();
                 Query query= session.createQuery("SELECT node_id FROM operation_set  WHERE set_id="+opset_id);
                  list = query.list();
                  Node=list.get(0);
             transaction.commit();
         }
         catch (Exception e) {
             if (transaction!=null) transaction.rollback();
             throw e;
         }
         finally {
             session.close();
         }
    	
    	return Node;
        }
    
}   
