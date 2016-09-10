package com.nfargere.ovhAvailableServers.checker;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfargere.ovhAvailableServers.json.JsonReader;
import com.nfargere.ovhAvailableServers.server.Server;
import com.nfargere.ovhAvailableServers.server.ServerZone;

public class AvailabilityChecker {
	private String url;
	private JSONObject availabilityJson;
	private Logger logger = Logger.getLogger(AvailabilityChecker.class);
	
	public void loadJson(String url) throws JSONException, IOException {
		logger.info("Load json object from " + url);
		availabilityJson = JsonReader.readJsonFromUrl(url);		
	}
	
	public void checkServerAvailability(Server server) {
		if(availabilityJson == null) {
			logger.error("The JSON object is null. Call loadJson(String url) first.");			
		}
		
		JSONArray availabilities = availabilityJson.getJSONObject("answer").getJSONArray("availability");
	    for(int i = 0; i < availabilities.length(); i++) {
	    	JSONObject availability = availabilities.getJSONObject(i);	    	
	    	
	    	if(availability.get("reference").toString().equalsIgnoreCase(server.getId())) {
	    		JSONArray metaZones = availability.getJSONArray("metaZones");
	    		
	    		for(int j = 0; j < metaZones.length(); j++) {
	    			JSONObject metaZone = metaZones.getJSONObject(j);	    			
	    			String zone = metaZone.get("zone").toString();
	    			
	    			for(ServerZone serverZone : server.getZones()) {
	    				if(serverZone.getName().equalsIgnoreCase(zone)) {
		    				if(!metaZone.get("availability").toString().equalsIgnoreCase("unavailable")) {
		    					serverZone.setIsAvailable(true);
		    				}
		    				else {
		    					serverZone.setIsAvailable(false);
		    				}
		    			}
	    			}
	    		}
	    	}
	    }
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject getAvailabilityJson() {
		return availabilityJson;
	}

	public void setAvailabilityJson(JSONObject availabilityJson) {
		availabilityJson = availabilityJson;
	}
}
