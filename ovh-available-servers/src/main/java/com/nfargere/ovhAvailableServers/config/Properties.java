package com.nfargere.ovhAvailableServers.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.aeonbits.owner.ConfigFactory;
import org.apache.log4j.Logger;

public class Properties {
	private Properties(){}
	private AppConfig appConfig;
	private static Logger logger = Logger.getLogger(Properties.class);
 
	private static Properties INSTANCE = null;
	
	public static final String PROPERTIES_FILE_NAME = "app.properties";
 
	private static synchronized Properties getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Properties();
			java.util.Properties props = new java.util.Properties();
			
	        try {
	        	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	        	InputStream resourceStream = loader.getResourceAsStream(PROPERTIES_FILE_NAME);
	        	if(resourceStream == null) {
	        		logger.error(PROPERTIES_FILE_NAME + " file not found.");
	        	}
				props.load(resourceStream);
			}
	        catch (IOException e) {
				logger.error("Error when reading app.properties file");
				e.printStackTrace();
			}
	        INSTANCE.appConfig = ConfigFactory.create(AppConfig.class, props);
		}
		return INSTANCE;
	}
	
	public static AppConfig getValues() {
		return getInstance().getAppConfig();
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
}
