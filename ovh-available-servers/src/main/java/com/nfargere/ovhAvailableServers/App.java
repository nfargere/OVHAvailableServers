package com.nfargere.ovhAvailableServers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.nfargere.ovhAvailableServers.checker.AvailabilityChecker;
import com.nfargere.ovhAvailableServers.config.AppConfig;
import com.nfargere.ovhAvailableServers.config.Properties;
import com.nfargere.ovhAvailableServers.server.Server;
import com.nfargere.ovhAvailableServers.server.ServerZone;
import com.nfargere.ovhAvailableServers.server.ServersList;

public class App 
{
	private static Logger logger = Logger.getLogger(App.class);
	
	public static void main(String[] args) throws IOException, JSONException
	{
		AppConfig cfg = Properties.getValues();
		
		AvailabilityChecker checker = new AvailabilityChecker();		
		ServersList servers = cfg.servers();
		
		if(!servers.getServers().isEmpty()) {
			checker.loadJson(cfg.kimsufiJsonUrl());
		}
		
		for(Server server : servers.getServers()) {
		    try {
				checker.checkServerAvailability(server);
				
				if(server.isAvailable()) {
					StringBuilder availableZones = new StringBuilder();
					availableZones.append(server.getId() + " is available at the following zone(s): ");
					
					int cnt = 0;
					for(ServerZone zone : server.getAvailableZones()) {
						if(cnt > 0) {
							availableZones.append(", ");
						}
						
						availableZones.append(zone.getName());
						
						cnt++;
					}
					
					logger.info(availableZones.toString());
				}
				else {
					logger.info(server.getId() + " is not available");
				}
			}
		    catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
