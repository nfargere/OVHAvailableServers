package com.nfargere.ovhAvailableServers.config;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import org.aeonbits.owner.Converter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nfargere.ovhAvailableServers.json.JsonReader;
import com.nfargere.ovhAvailableServers.server.Server;
import com.nfargere.ovhAvailableServers.server.ServersList;

public class ServersConverter implements Converter<ServersList>{
	private Logger logger = Logger.getLogger(ServersConverter.class);
	
	public ServersList convert(Method method, String input) {
		JSONObject json = null;
		ServersList servers = new ServersList();
		
		try {			
			URL path = Thread.currentThread().getContextClassLoader().getResource(input);
			if(path != null) {
				json = JsonReader.readJsonFromFile(path.getFile());
			}
			else {
				json = JsonReader.readJsonFromFile(input);
			}
		}
		catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
		
		if(json == null) {
			logger.error("The json file " + input + "has not been loaded.");
			return servers;
		}
		else {
			try {
				JSONArray jsonServers = json.getJSONArray("servers");
				
				for(int i = 0; i < jsonServers.length(); i++) {
			    	JSONObject jsonServer = jsonServers.getJSONObject(i);
			    	
			    	Server server = new Server();			    	
			    	
			    	server.setId(jsonServer.getString("id"));
			    	server.setUrl(jsonServer.getString("url"));
			    	
			    	JSONArray jsonZones = jsonServer.getJSONArray("zones");
			    	for(int j = 0; j < jsonZones.length(); j++) {
			    		String jsonZone = jsonZones.getString(j);
			    		server.addZone(jsonZone);		    		
			    	}
			    	
			    	servers.addServer(server);
				}
			}
			catch(Exception e) {
				logger.error("Error when reading the file " + input+ ": "+ e.getMessage());
				logger.error(e);
			}
		}
		
		return servers;
	}
}
