package com.nfargere.ovhAvailableServers.config;

import org.aeonbits.owner.Config;

import com.nfargere.ovhAvailableServers.server.ServersList;

public interface AppConfig extends Config {    
    String kimsufiJsonUrl();
	
	@Key("serversFile")
	@ConverterClass(ServersConverter.class)
	ServersList servers();
}
