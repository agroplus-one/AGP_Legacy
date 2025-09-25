package com.rsi.agp.batch.seguimiento;

import java.io.IOException;
import java.util.Properties;

import com.rsi.agp.batch.importacionCondicionado.util.ConfigImportacionCondicionado;

public class ConfigSeguimiento {
	
	private static Properties props;

	public static final String RUTA_WSDL = "ruta_wsdl";
	public static final String COD_USUARIO = "cod_usuario";

	static {
		loadProperties();
	}

	private static void loadProperties() {
		try {
			props = new Properties();
			props.load(ConfigImportacionCondicionado.class.getClassLoader()
					.getResourceAsStream("agp_seguimiento.properties"));
		} catch (IOException e) {
			// fail silently...
		}
	}

	public static String getProperty(String name) {
		return props.getProperty(name);
	}

	public static void mostrarPropiedades() {
		props.list(System.err);
	}

}
