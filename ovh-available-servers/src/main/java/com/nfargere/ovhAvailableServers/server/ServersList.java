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

	public ArrayList<Server> getServers() {
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}
}
