package com.baudelaine.tdazlan;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SMS{
	
	String sid;
	Date when;
	String hWhen;
	String to;
	String from;
	String body;
	String status;
	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL);

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Date getWhen() {
		return when;
	}
	public void setWhen(Date when) {
		this.when = when;
		this.hWhen = String.format("%s", df.format(when));
	}
	public String getHWhen(){
		return this.hWhen;
	}
	
	public String toString(){
		String result = "SMS from:" + this.from + 
				" to:" + this.to + 
				" about:" + this.body;
		result += " at: " + this.hWhen;
		result += " with sid:" + this.sid;
		result += " and status:" + this.status;
		
		return result;
	}
	
}
