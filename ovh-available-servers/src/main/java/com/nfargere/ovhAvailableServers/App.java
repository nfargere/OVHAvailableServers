package com.nfargere.ovhAvailableServers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.nfargere.ovhAvailableServers.checker.AvailabilityChecker;
import com.nfargere.ovhAvailableServers.config.AppConfig;
import com.nfargere.ovhAvailableServers.config.Properties;
import com.nfargere.ovhAvailableServers.google.GmailService;
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
			
			for(Server server : servers.getServers()) {
				checker.checkServerAvailability(server);
			}
		}
		
		if(!servers.getAvailableServers().isEmpty()) {
			String subject;
			StringBuilder bldr = new StringBuilder();
			
			bldr.append("Hi!<br /><br />");
			
			if(servers.getAvailableServers().size() > 1) {
				subject = "Many OVH servers available !!!";
				bldr.append("The following servers are available:<br />");
			}
			else {
				subject = "One OVH server available !!!";
				bldr.append("The following server is available:<br />");
			}
			
			bldr.append("<ul>");
			
			for(Server server : servers.getAvailableServers()) {
				bldr.append("<li>");
				bldr.append("server "+ server.getId() + ": "+ server.getUrl());
				bldr.append("</li>");
			}
			
			bldr.append("</ul>");
			
			bldr.append("<br /><br />");
			bldr.append("Good luck!");
						
			GmailService.getInstance().sendEmail(cfg.emailTo(), cfg.emailFrom(), subject, bldr.toString());
		}
		else {
			logger.info("No available server found");
		}
	}
}
