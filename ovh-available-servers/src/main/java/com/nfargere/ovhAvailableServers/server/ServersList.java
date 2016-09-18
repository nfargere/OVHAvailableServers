package com.nfargere.ovhAvailableServers.server;

import java.util.ArrayList;

public class ServersList{
	private ArrayList<Server> servers;
	
	public ServersList() {
		servers = new ArrayList<Server>();
	}
	
	public void addServer(Server server) {
		servers.add(server);
	}
	
	public ArrayList<Server> getAvailableServers() {
		ArrayList<Server> availableServers = new ArrayList<Server>();
		
		for(Server server : getServers()) {
			if(server.isAvailable()) {
				availableServers.add(server);
			}
		}
		
		return availableServers;
	}

	public ArrayList<Server> getServers() {
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}
}
