package com.baudelaine.tdazlan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application Lifecycle Listener implementation class ContextListener
 *
 */
@WebListener
public class ContextListener implements ServletContextListener {

	InitialContext ic;
	Database db = null;
	SMSFactory smsFactory = new SMSFactory();
	List<Contact> contacts = new ArrayList<Contact>();

    /**
     * Default constructor. 
     */
    public ContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
       	try {
    			ic = new InitialContext();
    			arg0.getServletContext().setAttribute("ic", ic);
    			System.out.println("Context has been initialized...");
    			
    			String json = "";
    			
				json = System.getenv("VCAP_SERVICES");
				db = initDB(json);

				json = System.getenv("VCAP_SERVICES");
				smsFactory = initSMSFactory(json);
    			
    			if (smsFactory == null) {
    				// Still null so switch in Dummy mode
    				smsFactory = new SMSFactory();
    			}
    			
    			System.out.println("db=" + db);
				arg0.getServletContext().setAttribute("db", db);
				arg0.getServletContext().setAttribute("smsFactory", smsFactory);
				
				arg0.getServletContext().setAttribute("contacts", contacts);

    			
    		} catch (NamingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	arg0.getServletContext().removeAttribute("ic");
		System.out.println("Context has been destroyed...");    	
    }
    
    public SMSFactory initSMSFactory(String json){
    	
    	String serviceName = "user-provided";
    	
		ObjectMapper mapper = new ObjectMapper();
		
		String url = "";
		String account = "";
		String token = "";
		String phone = "";
		
            	
		System.out.println("json="+json);
		
		try {
		
			Map<String, Object> input = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> l0s = (List<Map<String, Object>>) input.get(serviceName);
			
			for(Map<String, Object> l0: l0s){
				for(Map.Entry<String, Object> e: l0.entrySet()){
					if(e.getKey().equalsIgnoreCase("credentials")){
						System.out.println(e.getKey() + "=" + e.getValue());
						@SuppressWarnings("unchecked")
						Map<String, Object> credential = (Map<String, Object>) e.getValue();
						url = (String) credential.get("url");
						account = (String) credential.get("accountSID");
						token = (String) credential.get("authToken");
						phone = (String) credential.get("phone");
							
					}
				}
			}
			
			if(phone != null){
				phone = phone.replaceAll("[\\s-\\.()]", "");
			}
			System.out.println("url=" + url);
			System.out.println("account=" + account);
			System.out.println("token=" + token);
			System.out.println("phone=" + phone);
			
			smsFactory = new SMSFactory(account, token);
			smsFactory.setPhone(phone);

		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return smsFactory;
    }
    
    @SuppressWarnings("unchecked")
	public Database initDB(String json){

    	String serviceName = "cloudantNoSQLDB";
    	String dbname = "contacts";
    	
		ObjectMapper mapper = new ObjectMapper();
		
		String url = "";
		String username = "";
		String password = "";
		
            	
		System.out.println("json="+json);
		
		try {
		
			Map<String, Object> input = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
			
			List<Map<String, Object>> l0s = (List<Map<String, Object>>) input.get(serviceName);
			
			for(Map<String, Object> l0: l0s){
				for(Map.Entry<String, Object> e: l0.entrySet()){
					if(e.getKey().equalsIgnoreCase("credentials")){
						System.out.println(e.getKey() + "=" + e.getValue());
						Map<String, Object> credential = (Map<String, Object>) e.getValue();
						url = (String) credential.get("url");
						username = (String) credential.get("username");
						password = (String) credential.get("password");
							
					}
				}
			}
			
			CloudantClient client = ClientBuilder.url(new URL(url))
			        .username(username)
			        .password(password)
			        .build();
		
			System.out.println("Server Version: " + client.serverVersion());
			
			// Get a Database instance to interact with, and create it if it doesn't already exist
			db = client.database(dbname, true);
			
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return db;
    	
    }
	
}
