/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaa_ngac_demo.AAA_NGAC_Server;

import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.node_type;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.operation_type;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.DatabaseManager;
import org.hibernate.Session;

/**
 *
 * @author cripan-local
 */
public class Declarations {
    
    public  final int Conn=1;
    public  final int Pol=2;
    public  final int U_attr=3;
    public  final int User=4;
    public  final int O_attr=5;
    public  final int Obj=6;
    public  final int Oper=7;
    
    public operation_type resource;
    public operation_type admin;
    
            node_type Tc;
            node_type Tp;
            node_type Ta;
            node_type Tu;
            node_type Tb;
            node_type To;
            node_type Ts;

    public Declarations() {
    }
    
        public  void add_nodeTypes(){
            
            
                DatabaseManager databaseManager=new DatabaseManager();
        
        Session session=databaseManager.getSessionFactory().openSession();
        session.beginTransaction(); 
         Tc = new node_type("c", "Connector"); 
        session.save(Tc);
         Tp = new node_type("p", "Policy Class"); 
        session.save(Tp);
         Ta = new node_type("a", "User Attribute"); 
        session.save(Ta);
         Tu = new node_type("u", "User"); 
        session.save(Tu);
         Tb = new node_type("b", "Object Attribute"); 
        session.save(Tb);
         To = new node_type("o", "Object"); 
        session.save(To);
         Ts = new node_type("s", "Operation Set"); 
        session.save(Ts);
                
        session.getTransaction().commit();
        session.close();
         
    }

        
        
   public  void add_OperationTypes(){
         DatabaseManager databaseManager=new DatabaseManager();
        
        Session session=databaseManager.getSessionFactory().openSession();
        session.beginTransaction(); 
         resource = new operation_type("Resource operations"); 
        session.save(resource);
         admin = new operation_type("Admin operations"); 
        session.save(admin);
                  
        session.getTransaction().commit();
        session.close();
        
        
    }

}
