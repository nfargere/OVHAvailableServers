package com.nfargere.ovhAvailableServers.server;

import java.util.ArrayList;
import java.util.List;

public class Server {
	private String id;
	private String url;
	private ArrayList<ServerZone> zones;
	
	public Server() {
		zones = new ArrayList<ServerZone>();
	}
	
	public void addZone(String zoneName) {
		ServerZone zone = new ServerZone();
		zone.setName(zoneName);
		zone.setIsAvailable(false);
		
		getZones().add(zone);
	}
	
	public Boolean isAvailable() {
		return !getAvailableZones().isEmpty();
	}
	
	public List<ServerZone> getAvailableZones() {
		ArrayList<ServerZone> zones = new ArrayList<ServerZone>();
		
		for(ServerZone zone : getZones()) {
			if(zone.getIsAvailable() == true) {
				zones.add(zone);
			}
		}
		
		return zones;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<ServerZone> getZones() {
		return zones;
	}

	public void setZones(ArrayList<ServerZone> zones) {
		this.zones = zones;
	}
}
