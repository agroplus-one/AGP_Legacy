package com.rsi.agp.core.report.rcganado;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;

public class InformeGanadoRC {
	
	private static final Log LOGGER = LogFactory.getLog(InformeGanadoRC.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private final static String EXTENSION_JRXML=".jrxml";
	private final static String EXTENSION_JASPER=".jasper";
	private final static String FICHERO="declaracionRCGanado";
	private final static Locale locale = new Locale( "es", "ES");
	
	public static byte[] getInforme(String ruta, Map<String,Object> parametros) throws Exception{
		
		String rutaJasper = bundle.getString("informeJasper.ganadoRC");
		String rutaComun = bundle.getString("informeJasper.ganadoRC.rutaComun");
		
		String rutaFicheros = ruta + rutaJasper;
		String rutaFicherosComun = ruta + rutaComun;
		String ficheroFuente = rutaFicheros + FICHERO + EXTENSION_JRXML;
		String ficheroCompilado = rutaFicheros + FICHERO + EXTENSION_JASPER;

		parametros.put("IMAGEN_DIR", rutaFicheros);
		parametros.put("SUBREPORT_DIR", rutaFicheros);
		parametros.put("SUBREPORT_COMUN_DIR", rutaFicherosComun);

		verParametros(parametros);
		
		//Locale locale = new Locale( "es", "ES");
		parametros.put(JRParameter.REPORT_LOCALE, locale); 
		
		LOGGER.debug("Compilamos el informe");
		JasperCompileManager.compileReportToFile(ficheroFuente, ficheroCompilado);
		
		LOGGER.debug("Rellenamos el informe");
		JasperFillManager.fillReport(ficheroCompilado , parametros,  new JREmptyDataSource());

		LOGGER.debug("Convertimos el informe en un array de bytes");
		return JasperRunManager.runReportToPdf(ficheroCompilado, parametros,  new JREmptyDataSource());
	}

	private static void verParametros(Map<String, Object> parameters) {
		for (Map.Entry<String, Object> entry : parameters.entrySet()){
			LOGGER.debug("KEY -> " + entry.getKey() + " || VALUE -> " + entry.getValue());
		}
	}
}
