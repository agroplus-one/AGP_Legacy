package com.rsi.agp.core.webapp.action.anexoMod;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.managers.impl.anexoMod.IPolizaActualizadaManager;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;

import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class PolizaActualizadaController extends BaseMultiActionController {
	
	private static final Log logger = LogFactory.getLog(PolizaActualizadaController.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IPolizaActualizadaManager polizaActualizadaManager;
	
	String realPath = "";
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception {
		return null;
	}
	
	/**
	 * Metodo para ver el borrador con el estado actual de la poliza en Agroseguro
	 * @param request
	 * @param response
	 * @param anexoModificacion
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doVerPolizaActualizada(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) {
		
		logger.debug("PolizaActualizadaController - doVerPolizaActualizada");
		try {
			mostrarInformePolActualizada(response,anexoModificacion.getPoliza().getReferencia(),anexoModificacion.getPoliza().getLinea().getCodplan().toString(),
					anexoModificacion.getPoliza().getTipoReferencia().toString(), anexoModificacion);
		} catch (SWConsultaContratacionException e) {
			logger.error("Error al crear los objetos para llamar al servicio de consulta de contratacion", e);
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion")
				.addObject("error", "Error al crear los objetos para llamar al servicio de consulta de contratacion");
		} catch (AgrException e) {
			String mensaje = WSUtils.debugAgrException(e);
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion").addObject("error", mensaje);
		} catch (Exception e) {
			logger.error("Error indefinido durante la llamada al servicio de consulta de contratacion", e);
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion")
				.addObject("error", "Error indefinido durante la llamada al servicio de consulta de contratacion");
		}
		return null;
		
		
	}
	
	/**
	 * Metodo para ver el borrador con el estado actual de la poliza en Agroseguro
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doVerPolActualizada(HttpServletRequest request, HttpServletResponse response) {		
		String planPol = request.getParameter("planPol");
		String referenciaPol = request.getParameter("referenciaPol");
		String tipoRef = request.getParameter("tipoRefPoliza");
		AnexoModificacion am = new AnexoModificacion();
		try {
			mostrarInformePolActualizada(response,referenciaPol,planPol,tipoRef, am);	
		} catch (SWConsultaContratacionException e) {
			logger.error("Error al crear los objetos para llamar al servicio de consulta de contratacion", e);
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion")
				.addObject("error", "Error al crear los objetos para llamar al servicio de consulta de contratacion");
		} catch (AgrException e) {
			String mensaje = WSUtils.debugAgrException(e);
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion").addObject("error", mensaje);
		} catch (Exception e) {
			logger.error("Error indefinido durante la llamada al servicio de consulta de contratacion", e);
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion")
				.addObject("error", "Se ha producido un error al consultar el estado actual de la poliza");
		}
		return null;
		
	}
	
	/**
	 * Metodo para mostrar el borrador con el estado actual de la poliza en Agroseguro
	 * @param request
	 * @param response
	 * @param referenciaPol,
	 * @param planPol
	 * @param anexoModificacion
	 * @return 
	 * @return
	 * @throws Exception
	 */
	private ModelAndView mostrarInformePolActualizada(HttpServletResponse response, String referenciaPol, String planPol, String tipoRef, AnexoModificacion am) throws Exception {
		
		logger.debug("PolizaActualizadaController - mostrarInformePolActualizada");
		try {
			// Path real para luego buscar el WSDL de los servicios Web
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			boolean esAnexo = false;
			boolean anexoTieneCpl = false;
			
			if (am != null && am.getId() != null) {
				logger.debug("Entramos en el if de anexos");
			   JasperPrint jp = this.polizaActualizadaManager.verPolizaActualizada(referenciaPol, 
				   	new BigDecimal(planPol), realPath, getServletContext().getRealPath("plantillas"),esAnexo,
					am,anexoTieneCpl);
			
			   //Con los datos obtenidos, pintamos el informe en el out
			   ServletOutputStream out = response.getOutputStream();
			   logger.debug("Exportamos el informe a PDF");
			   JRPdfExporter exporter = new JRPdfExporter();
			   exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			   exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			   exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "SituacionActualizada_" + referenciaPol + "_" + planPol + ".pdf");
			   exporter.exportReport();
			   out.close();
			   logger.debug("Informe exportado correctamente");
			}else{
				return doVerPDFSituacionActual (new BigDecimal(planPol), referenciaPol, tipoRef ,response);
				
			}
			
		} catch (SWConsultaContratacionException e) {
			logger.error("Error al crear los objetos para llamar al servicio de consulta de contratacion", e);
			throw e;
		} catch (AgrException e) {
			
			throw e;
		} catch (Exception e) {
			logger.error("Error indefinido durante la llamada al servicio de consulta de contratacion", e);
			throw e;
		}
		return null;
		
	}
	
	
	public void doImprimirAnexoPpal(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean) {
		
		//Con los datos obtenidos, pintamos el informe en el out
		ServletOutputStream out;
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		boolean anexoTieneCpl = false;
		boolean esAnexo = true;
		try {
			String refPoliza = StringUtils.nullToString(request.getParameter("refPoliza"));
			String planPoliza = StringUtils.nullToString(request.getParameter("planPoliza"));
			capitalAseguradoBean = rellenaDatos(request,capitalAseguradoBean);
			
			JasperPrint jp = this.polizaActualizadaManager
					.verPolizaActualizada(refPoliza,
							"".equals(planPoliza) ? null : new BigDecimal(
									planPoliza), realPath, getServletContext()
									.getRealPath("plantillas"), esAnexo,
							capitalAseguradoBean.getParcela()
									.getAnexoModificacion(), anexoTieneCpl);

			String nombreInforme = "SituacionActualizada_" + refPoliza + "_" + planPoliza+ ".pdf";
			
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "filename=" + nombreInforme);
			out = response.getOutputStream();
			
			logger.debug("Exportamos el informe a PDF");
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
			exporter.exportReport();
			out.close();
			logger.debug("Informe exportado correctamente");
		} catch (IOException e) {
			logger.error("Error al obtener el output stream", e);
		} catch (JRException e) {
			logger.error("Error al exportar el informe a PDF", e);
		} catch (IllegalStateException e) {
			logger.error("Error al exportar el informe a PDF", e);
		} catch (DAOException e) {
			logger.error("Error al obtener los datos necesarios para generar el informe", e);
		} catch (SWConsultaContratacionException e) {
			logger.error("Error al crear los objetos para llamar al servicio de consulta de contratacion", e);
		} catch (AgrException e) {
			logger.error("Excepcion : PolizaActualizadaController - doImprimirAnexoPpal", e);
		} catch (Exception e) {
			logger.error("Excepcion : PolizaActualizadaController - doImprimirAnexoPpal", e);
		}
	}
	
	
	
	public void doImprimirAnexoCpl(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean) {
		
		//Con los datos obtenidos, pintamos el informe en el out
		ServletOutputStream out;
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		boolean anexoTieneCpl = true;
		boolean esAnexo = true;
		try {
			String refPoliza = StringUtils.nullToString(request.getParameter("refPoliza"));
			capitalAseguradoBean = rellenaDatos(request,capitalAseguradoBean);
			JasperPrint jp = this.polizaActualizadaManager.verPolizaActualizada(
					refPoliza, 
					null,
					realPath, getServletContext().getRealPath("plantillas"),esAnexo,
					capitalAseguradoBean.getParcela().getAnexoModificacion(),anexoTieneCpl);

			out = response.getOutputStream();
			logger.debug("Exportamos el informe a PDF");
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "SituacionActualizada_" + refPoliza + ".pdf");
			exporter.exportReport();
			out.close();
			logger.debug("Informe exportado correctamente");
		} catch (IOException e) {
			logger.error("Error al obtener el output stream", e);
		} catch (JRException e) {
			logger.error("Error al exportar el informe a PDF", e);
		} catch (IllegalStateException e) {
			logger.error("Error al exportar el informe a PDF", e);
		} catch (DAOException e) {
			logger.error("Error al obtener los datos necesarios para generar el informe", e);
		} catch (SWConsultaContratacionException e) {
			logger.error("Error al crear los objetos para llamar al servicio de consulta de contratacion", e);
		} catch (AgrException e) {
			logger.error("Excepcion : PolizaActualizadaController - doImprimirAnexoCpl", e);
		} catch (Exception e) {
			logger.error("Excepcion : PolizaActualizadaController - doImprimirAnexoCpl", e);
		}
	}
	
	private CapitalAsegurado rellenaDatos(HttpServletRequest request,CapitalAsegurado capitalAseguradoBean) {
		if (capitalAseguradoBean.getParcela().getAnexoModificacion().getCupon().getIdcupon()==null) {
			capitalAseguradoBean.getParcela().getAnexoModificacion().getCupon().
			setIdcupon(StringUtils.nullToString(request.getParameter("idCuponImprimir")));
		}
		if (capitalAseguradoBean.getParcela().getAnexoModificacion().getId()==null) {
			capitalAseguradoBean.getParcela().getAnexoModificacion().setId(new Long
					(StringUtils.nullToString(request.getParameter("idImprimir"))));
		}
		return capitalAseguradoBean;
	}
	
	/* Pet. 57626 ** MODIF TAM (27/04/2020) ** Inicio */
	public ModelAndView doVerPDFSituacionActual(final BigDecimal codPlan, final String referencia, final String tipoRef, HttpServletResponse response) throws BusinessException{		
		logger.debug("PolizaActualizadaController-doVerPDFSituacionActual");
		logger.debug("Valor de codPlan: "+codPlan);
		logger.debug("Valor de referencia: "+referencia);
		logger.debug("Valor de tipoRef: "+tipoRef);		
		Map<String, Object> parametros = new HashMap<String, Object>();
		Base64Binary pdf = null;		
//		Path real para luego buscar el WSDL de los servicios Web
		setRealPath(this.getServletContext().getRealPath("/WEB-INF/"));		
		try{
			pdf = polizaActualizadaManager.obtenerPDFSituacionActual(referencia, codPlan, tipoRef, realPath);
			byte[] content = pdf.getValue();
			response.setContentType("application/pdf");
			response.setContentLength(content.length);
			response.setHeader("Content-Disposition", "filename=Poliza_" + referencia + "_" + codPlan + ".pdf");
			response.setHeader("Cache-Control", "cache, must-revalidate");
			response.setHeader("Pragma", "public");
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
		}catch(BusinessException be){
			logger.error("Error indefinido durante la llamada al servicio de consulta del PDF de la Situacion Actualizada", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			throw new BusinessException ("Se ha producido un error obteniendo el PDF de la poliza", be);
		} catch (IOException e) {
			logger.error("Error indefinido durante la llamada al servicio de consulta del PDF de la Situacion Actualizada", e);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			return new ModelAndView("moduloUtilidades/modificacionesPoliza/errorConsultaContratacion")
				.addObject("error", "Error indefinido durante la llamada al servicio de consulta del PDF de la Situacion Actualizada");
		}
		return null;
	}
	/* Pet. 57626 ** MODIF TAM (27/04/2020) ** Fin */
	
	public void setPolizaActualizadaManager(
			IPolizaActualizadaManager polizaActualizadaManager) {
		this.polizaActualizadaManager = polizaActualizadaManager;
	}
	
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	
	
	/* Pet. 57626 ** MODIF TAM (27/04/2020) ** Fin */
	
}
