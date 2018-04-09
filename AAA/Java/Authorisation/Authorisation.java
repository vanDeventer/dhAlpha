/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaa_ngac_demo.AAA_NGAC_Server.Authorisation;

import com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models.arrowheadservice;
import com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models.arrowheadsystem;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.*;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;
//import javax.ws.rs.PathParam;






/**
 *
 * @author cripan-local
 */
public class Authorisation {

    public Authorisation() {
    }
    
    public  boolean  isAllowed(node ua,node oa,String name_op){
        DatabaseManager databaseManager=new DatabaseManager();
        
        int ua_id=ua.getId();
        int oa_id=oa.getId();
        int operation_id=0;
        boolean allow=false;
        
        Session session=databaseManager.getSessionFactory().openSession();
        session.beginTransaction(); 
       
       Query query= session.createQuery("SELECT o FROM operation o"); 
       List<operation> list = query.list();
       operation op;
       String aux;
       if(list.isEmpty()){
           allow=false;
       }else{
       for (int i = 0; i < list.size(); i++){
           op=list.get(i);
           aux=op.getName();
            if(aux.equalsIgnoreCase(name_op)){
                operation_id=op.getId();
            }
         }
         
       
             if(operation_id==0){
                     allow=false;
              }else{
                      Query query2= session.createQuery("SELECT id FROM association WHERE ua_id="+ua_id+"AND operation_id="+operation_id+"AND oa_id="+oa_id); 
                       List<Integer> list2 = query2.list();
                       if(list2.size()!=0){
                           allow=true; 
                       }else {
                           
                           Query query3= session.createQuery("SELECT a FROM association a WHERE operation_id="+operation_id+"AND oa_id="+oa_id); 
                           List<association> listua = query3.list();


                           association asso;
                           int node=0;
                           if(!listua.isEmpty()){
                             
                                 for (int i = 0; i < listua.size(); i++){
                                        asso=listua.get(i);
                                        node=asso.getUa_id();
                                        Query query11= session.createQuery("SELECT id FROM assignment  WHERE start_node_id="+ua_id+"AND end_node_id="+node); 
                                        List<Integer> list11 = query11.list(); 
                                        if(!list11.isEmpty()){
                                            Query query5= session.createQuery("SELECT a FROM association a WHERE operation_id="+operation_id+"AND ua_id="+node+"AND oa_id="+oa_id); 
                                            List<association> list5 = query5.list();
                                            if(!list5.isEmpty()){
                                                allow=true;
                                                i=listua.size()+1;
                                            }
                                        }
                                 }
                           }
                           
                           if( allow==false){
                           Query query4= session.createQuery("SELECT a FROM association a WHERE operation_id="+operation_id+"AND ua_id="+ua_id); 
                           List<association> listoa = query4.list();
                           if(!listoa.isEmpty()){
                             
                                 for (int i = 0; i < listoa.size(); i++){
                                        asso=listoa.get(i);
                                        node=asso.getOa_id();
                                        Query query12= session.createQuery("SELECT id FROM assignment  WHERE start_node_id="+oa_id+"AND end_node_id="+node); 
                                        List<Integer> list12 = query12.list(); 
                                        if(!list12.isEmpty()){
                                            Query query6= session.createQuery("SELECT a FROM association a WHERE operation_id="+operation_id+"AND ua_id="+ua_id+"AND oa_id="+node); 
                                            List<association> list6 = query6.list();
                                            if(!list6.isEmpty()){
                                                allow=true;
                                                i=listoa.size()+1;
                                            }
                                        }
                                 }
                           }
                           
                           }
                           if(allow==false){
                             Query query13= session.createQuery("SELECT end_node_id FROM assignment  WHERE start_node_id="+ua_id); 
                             List<Integer> UA = query13.list(); 
                             Query query14= session.createQuery("SELECT end_node_id FROM assignment  WHERE start_node_id="+oa_id); 
                             List<Integer> OA = query14.list(); 
                           
                           if(OA.isEmpty() || UA.isEmpty()){
                                   
                               allow=false;
                           }else {
                               for (int i = 0; i < UA.size(); i++){
                                   int u=UA.get(i);
                                    //   System.out.println(u);
                                   for (int j = 0; j < OA.size(); j++){
                                        int o=OA.get(j);
                                     //   System.out.println(o);
                                        Query query7= session.createQuery("SELECT a FROM association a WHERE operation_id="+operation_id+"AND ua_id="+u+"AND oa_id="+o); 
                                            List<association> list7 = query7.list();
                                            if(list7.isEmpty()){
                                                allow=false;
                                                
                                            }else{
                                              allow=true;
                                              i=UA.size()+1;
                                              j=OA.size()+1;
                                            }
                                        
                                        
                                   }
                               }
                            }
                        }
                       }
                               
                             
                   }
                           
                                     
             
       }       
       
       
           if(allow){
            System.out.println("Consume request is Allowed"); 
            }else System.out.println("Consume request is Not Allowed");
        session.getTransaction().commit();
        session.close();
      return allow;
    }
    public String isSystemAuthorized_test(String systemName, String service_name, String provider_name) {
            
           DatabaseManager d=new DatabaseManager();
        
        boolean allow=false;
          
            node ua; 
            node oa;
            node op;
            operation_set op_set;
                arrowheadsystem consumer =d.getSystemByName(systemName);
                arrowheadsystem provider =d.getSystemByName(provider_name);
                arrowheadservice service =d.getServiceByName(service_name);
            
                // Association
               
                ua=consumer.getNode_id();
                oa=provider.getNode_id();
                op_set=service.getSet_id();
                op=d.getNodeBySet(op_set.getId());
                allow=isAllowed(ua,oa,op.getName());

                return "funciona";
	}
}
