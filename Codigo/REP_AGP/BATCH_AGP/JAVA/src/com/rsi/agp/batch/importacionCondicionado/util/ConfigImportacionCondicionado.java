package com.rsi.agp.batch.importacionCondicionado.util;

import java.io.IOException;
import java.util.Properties;

public class ConfigImportacionCondicionado {
	
	private static Properties props;
	
	public static final String RUTA_CARGA="ruta_carga";
	public static final String RUTA_ORIGEN="ruta_origen";
	public static final String CLASSPATH="classpath";
	public static final String URL_BBDD="urlJdbc";
	public static final String DRIVER_BBDD="driver";
	public static final String BBDD_USER="urlJdbcUser";
	public static final String BBDD_PASS="urlJdbcPass";
	public static final String SEPARADOR = "importacion.separador";
	
	static{
		loadProperties();	
	}	

	private static void loadProperties() {
		try {
			props=new Properties();
			props.load(ConfigImportacionCondicionado.class.getClassLoader().getResourceAsStream("agp_importacion.properties"));
		} catch(IOException e) {
			//fail silently...
		}
	}
	
	public static String getProperty(String name){
		return props.getProperty(name);
	}

	public static void mostrarPropiedades() {
		props.list(System.err);
	}
	

}
