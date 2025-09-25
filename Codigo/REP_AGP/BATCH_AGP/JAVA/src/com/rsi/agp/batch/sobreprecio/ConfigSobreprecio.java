package com.rsi.agp.batch.sobreprecio;

import java.io.IOException;
import java.util.Properties;

import com.rsi.agp.batch.importacionCondicionado.util.ConfigImportacionCondicionado;

public class ConfigSobreprecio {
private static Properties props;
	
	public static final String RUTA_WSDL = "ruta_wsdl";
	public static final String COD_USUARIO = "cod_usuario";
	public static final String DIA_REVISION_SEMANAL = "dia_revision_semanal";
	
	
	static{
		loadProperties();	
	}	

	private static void loadProperties() {
		try {
			props=new Properties();
			props.load(ConfigImportacionCondicionado.class.getClassLoader().getResourceAsStream("agp_sbp.properties"));
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
