package com.baudelaine.tdazlan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;

public class SMSFactory{
	
	String accountSid;
	String authToken;
	String phone;	
	TwilioRestClient client = null;
	SmsFactory fact = null;
	
	public SMSFactory(){
		this.accountSid = "Dummy";
		this.authToken = "Dummy";
		this.phone = "+33612345678";
	}

	public SMSFactory(String accountSid, String authToken) {
		// TODO Auto-generated constructor stub
		this.accountSid = accountSid;
		this.authToken = authToken;
		setSMSFactory();
	}
	
	private void setSMSFactory(){
		try{
			this.client = new TwilioRestClient(this.accountSid, this.authToken);
			this.fact = this.client.getAccount().getSmsFactory();
		}
		catch(IllegalArgumentException e){
		}
	}
	
	public SMS sendSMS(SMS sms) throws TwilioRestException{
		
		if(this.fact != null){

			sms.setFrom(this.getPhone());
			Map<String, String> params = new HashMap<String, String>();
			params.put("To", sms.getTo());
			params.put("From", sms.getFrom());
			params.put("Body", sms.getBody());
			Sms twilio = this.fact.create(params);
			sms.setSid(twilio.getSid());
			sms.setBody(twilio.getBody());
			sms.setTo(twilio.getTo());
			sms.setWhen(twilio.getDateCreated());
			sms.setStatus(twilio.getStatus());
			System.out.println("twilio.getSid()=" + twilio.getSid());
			
		}
		else{
			sms.setFrom(this.getPhone());
			sms.setWhen(new Date());
			sms.setSid("Dummy");
			sms.setStatus("Dummy");
		}
		
		return sms;
	}
	
	public TwilioRestClient getClient() {
		return client;
	}

	public void setClient(TwilioRestClient client) {
		this.client = client;
	}

	public String getaccountSid() {
		return accountSid;
	}

	public void setaccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	public String getauthToken() {
		return authToken;
	}

	public void setauthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String toString(){
		String result = "";
		result += "SMSFACTORY accountSid=" + this.accountSid;
		result += "\nSMSFACTORY authToken=" + this.authToken;
		result += "\nSMSFACTORY phones=" + this.phone;
		
		return result;
	}

}

