package com.baudelaine.tdazlan;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Tools {

	public final static String toJSON(Object o) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		String jsonResult = null;
		mapper.writeValue(sw, o);
		sw.flush();
		jsonResult = sw.toString();
		sw.close();
		return jsonResult;
	}
	
}
