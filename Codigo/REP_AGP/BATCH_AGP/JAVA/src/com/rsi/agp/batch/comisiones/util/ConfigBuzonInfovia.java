package com.rsi.agp.batch.comisiones.util;
import java.io.IOException;
import java.util.Properties;


public class ConfigBuzonInfovia {
	
	private static Properties props;
	public static final String URL_FTP="urlFtp";
	public static final String DIR_REMOTO_GENERAL="directoriosRemotos";
	public static final String DIR_LOCAL="directorioLocal";
	public static final String DIR_REMOTO="directorioRemoto";
	public static final String URL_BBDD="urlJdbc";
	public static final String DRIVER_BBDD="driver";
	public static final String BBDD_USER="urlJdbcUser";
	public static final String BBDD_PASS="urlJdbcPass";
	
	public final static int FICHERO_COMISIONES=1;
	public final static int FICHERO_IMPAGADOS=2;
	public final static int FICHERO_REGLAMENTO=3;
	public final static int FICHERO_EMITIDOS=4;
	
	static{
		loadProperties();	
	}	

	private static void loadProperties() {
		try {
			props=new Properties();
			props.load(ConfigBuzonInfovia.class.getClassLoader().getResourceAsStream("ImportacionBuzonInfovia.properties"));
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
