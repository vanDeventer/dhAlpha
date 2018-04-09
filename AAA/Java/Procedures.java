/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaa_ngac_demo.AAA_NGAC_Server;
import java.util.*;


import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.Query;
import org.hibernate.Session;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.*;
import com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models.*;

/**
 *
 * @author cripan-local
 */
public class Procedures {
    
    public Procedures(){}
    
    public void createAssignment(node start_node, node end_node){
               DatabaseManager databaseManager=new DatabaseManager();
               
        int node_type_id=0;
        int path_id=0;
        int start_id=start_node.getId();
        int end_id=end_node.getId();
        if(start_id!=end_id){
            if(nodeExists(start_id)){
               
                if(nodeExists(end_id)){
                      
                     if(start_node.getNodetype_id() ==7 || end_node.getNodetype_id()==7){
                        Session session=databaseManager.getSessionFactory().openSession();
                        session.beginTransaction(); 
                        assignment assig =new assignment(start_id,end_id,1,0);
                        session.save(assig);
                        session.getTransaction().commit();
                        session.close();  
                     }else{
                     
                            Session session=databaseManager.getSessionFactory().openSession();
                            session.beginTransaction();
                            assignment_path path=new assignment_path(end_node);
                            session.save(path); 
                             Query query1= session.createQuery("SELECT id FROM assignment_path ");
                            List<Integer> listaResultados = query1.list();
                             path_id=Collections.max(listaResultados);         
                             assignment assig1 =new assignment(start_id,end_id,1,path_id);
                             session.save(assig1);
                
                            Query query= session.createQuery("SELECT DISTINCT a FROM assignment a where end_node_id="+ start_id + "AND assignment_path_id>0 AND depth >0");
                            List<assignment> list_start= query.list();
                            assignment aux ;
                            if(list_start.size()!=0){
                    
                                 assignment [] assig =new assignment[list_start.size()];
                                  for (int i = 0; i < list_start.size(); i++){
                                        aux=list_start.get(i);
                                        assig[i]=new assignment(aux.getStart_node_id(),end_id,aux.getDepth()+1,path_id);
                                        session.save(assig[i]);
                             
                                    }
                   
                             }
       
                            session.getTransaction().commit();
                            session.close();  
                     }
        
                }
            }
        }
    }       
    
    public boolean nodeExists(int nodeid){
        DatabaseManager databaseManager=new DatabaseManager();
        
        Session session=databaseManager.getSessionFactory().openSession();
        session.beginTransaction();
                        Query query= session.createQuery("SELECT id FROM node where id="+nodeid);
                        List<Integer> list = query.list();
                        if(list.size()!=0){
                               session.close();  
                                return true;
        
                        }else {
                            
                            session.close();  
                               return false;
                        }

     }
     
    public void createAssociation(node ua, node oa, String op_name,operation op){
       DatabaseManager databaseManager=new DatabaseManager();
       
       int opset_id=op.getId();
       int ua_id= ua.getId();
       int oa_id= oa.getId();
       int node_id=0;
       String name=null;
       String aux;
       
       Session session=databaseManager.getSessionFactory().openSession();
       session.beginTransaction(); 
       Query query= session.createQuery("SELECT n FROM node n"); 
       List<node> list = query.list();
       node [] nodes =new node[list.size()];
       node nod;
       for (int i = 0; i < list.size(); i++){
           nod=list.get(i);
           aux=nod.getName();
            if(aux.equalsIgnoreCase(op_name)){
                name=aux;
                node_id=nod.getId();
            }
         }
       
        if(name==null){
        
            node operation = new node(7,op_name, "--");
            operation_set set = new operation_set(op,operation);
           session.save(operation);
           session.save(set);
           node_id=operation.getId();
                        
        }
        association asso= new association(ua_id,op,oa_id);
        assignment a = new assignment (oa_id,node_id,1,0);
        assignment b = new assignment (node_id,ua_id,1,0);
        session.save(asso);
        session.save(a);
        session.save(b);
        
        session.getTransaction().commit();
        session.close();
        
    }
    

}