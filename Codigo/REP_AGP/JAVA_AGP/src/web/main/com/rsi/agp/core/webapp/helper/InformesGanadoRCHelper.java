package com.rsi.agp.core.webapp.helper;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.webapp.util.AseguradoIrisHelper;
import com.rsi.agp.dao.models.param.ParametrizacionDao;
import com.rsi.agp.dao.tables.config.ConfigAgp;

public class InformesGanadoRCHelper {
	
	private static final Log LOGGER = LogFactory.getLog(InformesGanadoRCHelper.class);
	
	private final static ResourceBundle bundle = ResourceBundle.getBundle("agp_informes_jasper");
	
	private final static String NOMBRE_FICHERO = bundle.getString("ganadoRC.nombreFichero");
	private final static String NOMBRE_INFORME = bundle.getString("ganadoRC.nombreInforme");
	private final static String RUTA_JASPER = bundle.getString("ganadoRC.ruta");
	private final static String RUTA_COMUN = bundle.getString("informeJasper.ganadoRC.rutaComun");
	private final static String RUTA_IPID = bundle.getString("informeJasper.ipid.ruta");
	
	private final static Locale locale = new Locale("es", "ES");

	public void generarInforme(HttpServletResponse res, Map<String,Object> datosInforme, String rutaInformes, Boolean aseguradoVulnerable) throws Exception{
		LOGGER.debug("");
		try (ServletOutputStream out = res.getOutputStream();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
			
			byte[] content = this.getInforme(rutaInformes, datosInforme, aseguradoVulnerable);
			
			res.setContentType("application/pdf");
			res.setContentLength(content.length);
			res.setHeader("Content-Disposition", "filename="+NOMBRE_INFORME);
			res.setHeader("Cache-Control", "cache, must-revalidate");
			res.setHeader("Pragma", "public");
			bufferedOutputStream.write(content);
				
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe", e);
			throw new Exception("Error durante la generacion del informe", e);
			
		} catch (Throwable e) {
			LOGGER.error("Error durante la generacion del informe", e);
			throw new Exception("Error durante la generacion del informe", e);
			
		}					
	}
	
	private byte[] getInforme(String rutaInformes, Map<String,Object> datosInforme, Boolean aseguradoVulnerable) throws Exception{	
		
		ParametrizacionDao parametrizacionDao;
		Configuration cfg = new Configuration();
		//cargamos la sesion
		//SessionFactory sessionFactory =  cfg.configure().buildSessionFactory();
		parametrizacionDao = new ParametrizacionDao();
		//parametrizacionDao.setSessionFactory(sessionFactory);
		
		String pathSubInformeFuente = null;
		String pathSubInformeCompilado = null;
		
		String rutaFicheros = rutaInformes + RUTA_JASPER;
		String ficheroFuente = rutaFicheros + NOMBRE_FICHERO;
		String rutaComun = rutaInformes + RUTA_COMUN;

		datosInforme.put("IMAGEN_DIR", rutaFicheros + "/images/");
		datosInforme.put("SUBREPORT_DIR", rutaFicheros);
		datosInforme.put("SUBREPORT_COMUN_DIR", rutaComun);
		datosInforme.put("RUTA_IPID", RUTA_IPID);
		
		
		datosInforme.put("aseguradoVulnerable", aseguradoVulnerable);
		                                                                                                
		verParametros(datosInforme);

		datosInforme.put(JRParameter.REPORT_LOCALE, locale); 
		
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		//Subinformes
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_cabecera");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_ccpp");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_datosPersonales");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_ipid");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_ipid2");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_notaInformativa");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_notaInformativa_AV");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_recibo_detalle");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_recibo");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_riesgo_detalle");
		lstIdentificadoresInformes.add("/jasper/ganadoRC/declaracionRCGanado_riesgo");
		lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2");
		lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2_AV");
		
		// Compilamos todos los reports y los rellenamos
		for (String idSubInforme : lstIdentificadoresInformes) {		
			
			pathSubInformeFuente = rutaInformes + idSubInforme + ".jrxml";
			pathSubInformeCompilado = rutaInformes + idSubInforme + ".jasper";

			LOGGER.debug("Inicio de la compilacion del informe " + idSubInforme);
		
			JasperCompileManager.compileReportToFile(pathSubInformeFuente, pathSubInformeCompilado);
		}
		
		LOGGER.debug("Compilamos el informe");
		JasperReport report = JasperCompileManager.compileReport(ficheroFuente);
		
		LOGGER.debug("Rellenamos el informe");
		JasperPrint print = JasperFillManager.fillReport(report , datosInforme,  new JREmptyDataSource());

		LOGGER.debug("Convertimos el informe en un array de bytes");
		byte[] exportReportToPdf = JasperExportManager.exportReportToPdf(print);
				
		return exportReportToPdf;
	}

	private static void verParametros(Map<String, Object> parameters) {
		for (Map.Entry<String, Object> entry : parameters.entrySet()){
			LOGGER.debug("KEY -> " + entry.getKey() + " || VALUE -> " + entry.getValue());
		}
	} 
}
