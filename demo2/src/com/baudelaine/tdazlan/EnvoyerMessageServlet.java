package com.baudelaine.tdazlan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.AllDocsRequest;
import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.twilio.sdk.TwilioRestException;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "EnvoyerMessage", urlPatterns = { "/EnvoyerMessage" })
public class EnvoyerMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EnvoyerMessageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		
		try {
			
			Database db = (Database) request.getServletContext().getAttribute("db");
	    	System.out.println("db=" + db);
	    	
			SMSFactory smsFactory = (SMSFactory) request.getServletContext().getAttribute("smsFactory");
			
	        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        String json = "";
	        ObjectMapper mapper = new ObjectMapper();
	        List<SMS> smss = new ArrayList<SMS>();
	        
	        if(br != null){
	        	json = br.readLine();
	        }

            System.out.println("json="+json);

            Map<String, Object> messages = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
            
            for(Map.Entry<String, Object> e: messages.entrySet()){
            	System.out.println(e.getKey() + " -> " + e.getValue());
            	
            	Map<String, String> message = (Map<String, String>) e.getValue();
            	SMS sms = new SMS();
            	for(Map.Entry<String, String> f: message.entrySet()){
            		
                	if(f.getKey().equalsIgnoreCase("message")) sms.setBody(f.getValue());;
                	if(f.getKey().equalsIgnoreCase("telephone")) sms.setTo(f.getValue());;
                	
            	}
            	smss.add(sms);
            }
            
            if(smss.size() > 0){
            	for(SMS sms: smss){
            		smsFactory.sendSMS(sms);
            	}
            }
     
            System.out.println("smss=" + smss);
            
	        response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(smss));
		}
		
		catch (TwilioRestException e) {
			System.err.println(e.getErrorCode() + "\r\n" + e.getErrorMessage());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			Map<String, String> error = new HashMap<String, String>();
			error.put("errorCode", String.valueOf(e.getErrorCode()));
			error.put("errorMessage", e.getErrorMessage());
			response.getWriter().write(Tools.toJSON(error));
			return;
		}
		
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
