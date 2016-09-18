package com.nfargere.ovhAvailableServers.server;

public class ServerZone {
	private String name;
	private Boolean isAvailable = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
}
