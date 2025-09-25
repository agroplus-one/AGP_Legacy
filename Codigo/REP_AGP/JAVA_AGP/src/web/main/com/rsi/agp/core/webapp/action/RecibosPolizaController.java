package com.rsi.agp.core.webapp.action;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.RecibosPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;


public class RecibosPolizaController extends BaseMultiActionController {
	
	private static final String MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO = "/moduloUtilidades/recibos/errorRecibo";
	private static final String PUBLIC = "public";
	private static final String PRAGMA = "Pragma";
	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String CACHE_MUST_REVALIDATE = "cache, must-revalidate";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String FILENAME_POLIZA = "filename=Poliza_";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String MENSAJE_ERROR_GENERAL = "mensaje.error.general";
	private static final String ALERTA = "alerta";
	private static final String ID_POLIZA = "idPoliza";
	private static final String FILTRO_RECIBO_POLIZA = "filtroReciboPoliza";
	private static final String WEB_INF = "/WEB-INF/";
	
	private static final Log LOGGER = LogFactory.getLog(RecibosPolizaController.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private RecibosPolizaManager recibosPolizaManager;
	private PolizaManager polizaManager;
	
	String realPath = "";
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, ReciboPoliza reciboPoliza){
		String idPoliza = null;
		String refPoliza = null;
		Poliza poliza = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		// Path real para luego buscar el WSDL de los servicios Web
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Character tipoRefPoliza=null;
		try{
			if (request.getSession().getAttribute(FILTRO_RECIBO_POLIZA) != null) {
				reciboPoliza = (ReciboPoliza) request.getSession().getAttribute(FILTRO_RECIBO_POLIZA);
				request.getSession().removeAttribute(FILTRO_RECIBO_POLIZA);
			}
			idPoliza = request.getParameter(ID_POLIZA);
			if(idPoliza != null){				 
				
				parametros.put(ID_POLIZA, idPoliza);
				poliza = polizaManager.getPoliza(Long.parseLong(idPoliza));
				refPoliza = poliza.getReferencia();				
				reciboPoliza.setRefpoliza(refPoliza);
				tipoRefPoliza = poliza.getTipoReferencia();
				reciboPoliza.setTiporef(poliza.getTipoReferencia());
			}else{
				refPoliza = request.getParameter("refPoliza");
				if(refPoliza != null){
					reciboPoliza.setRefpoliza(refPoliza);
					reciboPoliza.setTiporef('P');
					tipoRefPoliza = 'P';
				}
			}

//			primero actualizar bbdd con llamada a WS
			List<Long> idrecibos = new ArrayList<Long>();
			if(null!=poliza && null!=poliza.getReferencia()&& null!=poliza.getTipoReferencia()){
				idrecibos = recibosPolizaManager.descargarRecibosPolizaWS(poliza, realPath);
			}else if (tipoRefPoliza !=null && refPoliza !=null){
				idrecibos = recibosPolizaManager.descargarRecibosPolizaWS(tipoRefPoliza,refPoliza,realPath);
			}else{
				logger.info("hay nulos: tipoRefPoliza="+tipoRefPoliza+" refPoliza="+refPoliza);
			}
			if(idrecibos.isEmpty()){
				LOGGER.info("-----------------------------------------------");
				LOGGER.info("--!! No hay recibos emitidos para la Poliza!!--");
				LOGGER.info("-----------------------------------------------");
			}
			
			parametros.put("listTipRecibos", recibosPolizaManager.getListTipificacionRecibos());
			
			reciboPoliza.getRecibo().setCodplan(poliza.getLinea().getCodplan().toString());
			
			List<ReciboPoliza> listaRecibosPoliza = recibosPolizaManager.buscarRecibos(reciboPoliza);
			
			
			reciboPoliza.getRecibo().setCodlinea(Integer.valueOf(poliza.getLinea().getCodlinea().toString()));
			reciboPoliza.getRecibo().setRefcolectivo(poliza.getColectivo().getIdcolectivo());
			reciboPoliza.getRecibo().setDccolectivo(Integer.valueOf(poliza.getColectivo().getDc()));
			reciboPoliza.setNombreaseg(poliza.getAsegurado().getNombreCompleto());
			reciboPoliza.setNifaseg(poliza.getAsegurado().getNifcif());
			
			parametros.put("listaRecibosPoliza", listaRecibosPoliza);
		}catch(Exception e){
			LOGGER.error("Se ha producido un error durante la consulta de recibos", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
		}

		return new ModelAndView("/moduloUtilidades/recibos/consultaRecibosPoliza","reciboPolizaBean", reciboPoliza).addAllObjects(parametros);
	
	}

	public ModelAndView doVerDetalle(HttpServletRequest request, HttpServletResponse response, ReciboPoliza reciboPoliza){
		Map<String, Object> parametros = new HashMap<String, Object>();
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		String idPoliza = null;
		idPoliza = request.getParameter(ID_POLIZA);
		parametros.put(ID_POLIZA, idPoliza);
		try{
			request.getSession().setAttribute(FILTRO_RECIBO_POLIZA, reciboPoliza);
			reciboPoliza = recibosPolizaManager.buscarRecibo(reciboPoliza.getId());
			parametros.put("tipoReciboAux",  StringUtils.nullToString(reciboPoliza.getTiporecibo()).equals("N")?"Nuevo":"Regularizado");
			parametros.put("tipoPolizaAux", StringUtils.nullToString(reciboPoliza.getTiporef()).equals("P")?"Principal":"Complemetaria");
			parametros.put("fechaEmisionAux", sdf2.format(reciboPoliza.getRecibo().getFecemisionrecibo()));
			
			if (new BigDecimal(reciboPoliza.getRecibo().getCodplan()).compareTo(Constants.PLAN_2015) == -1) {
				parametros.put("pintarPlan2015", false);
			}else {
				parametros.put("pintarPlan2015", true);
			}
			
		}catch(BusinessException be){
			LOGGER.error("Se ha producido un error durante la consulta de detalle del recibo ", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
		}

		return new ModelAndView("/moduloUtilidades/recibos/consultaDetalleReciboPoliza","reciboPolizaBean", reciboPoliza).addAllObjects(parametros);	
	}
	
	public ModelAndView doVerPDFPoliza(HttpServletRequest request, HttpServletResponse response, ReciboPoliza reciboPoliza){
		Map<String, Object> parametros = new HashMap<String, Object>();
		Base64Binary pdf = null;
//		Path real para luego buscar el WSDL de los servicios Web
		setRealPath(this.getServletContext().getRealPath(WEB_INF));
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try{
			pdf = recibosPolizaManager.obtenerPDFPoliza(reciboPoliza.getId(),realPath,usuario);
			byte[] content = pdf.getValue();
			response.setContentType(APPLICATION_PDF);
			response.setContentLength(content.length);
			response.setHeader(CONTENT_DISPOSITION, FILENAME_POLIZA + reciboPoliza.getId() + ".pdf");
			response.setHeader(CACHE_CONTROL, CACHE_MUST_REVALIDATE);
			response.setHeader(PRAGMA, PUBLIC);
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
		}catch(BusinessException be){
			LOGGER.error("Se ha producido un error durante la consulta de detalle del  recibo", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		} catch (IOException e) {
			LOGGER.error("Se ha producido un error durante la impresion del detalle del recibo ", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		}
		return null;
	}
	
	public ModelAndView doVerPDFPolizaCopy(HttpServletRequest request, HttpServletResponse response, ReciboPoliza reciboPoliza){
		Map<String, Object> parametros = new HashMap<String, Object>();
		Base64Binary pdf = null;
//		Path real para luego buscar el WSDL de los servicios Web
		setRealPath(this.getServletContext().getRealPath(WEB_INF));
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try{
			String tipoRef = request.getParameter("tipoRefPoliza");
			String CodPlan = request.getParameter("plan2");
			String RefPoliza = request.getParameter("poliza");
			pdf = recibosPolizaManager.obtenerPDFPolizaCopy(CodPlan, RefPoliza, tipoRef,realPath,usuario);		    
			byte[] content = pdf.getValue();
			response.setContentType(APPLICATION_PDF);
			response.setContentLength(content.length);
			response.setHeader(CONTENT_DISPOSITION, FILENAME_POLIZA + RefPoliza + "_" + CodPlan + ".pdf");
			response.setHeader(CACHE_CONTROL, CACHE_MUST_REVALIDATE);
			response.setHeader(PRAGMA, PUBLIC);
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
		}catch(BusinessException be){
			LOGGER.error("Se ha producido un error durante la consulta de detalle  del recibo", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		} catch (IOException e) {
			LOGGER.error("Se ha producido un error durante la impresion del detalle del  recibo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		}
		return null;
	}
	
	public ModelAndView doVerPDFPolizaTradCopy(HttpServletRequest request, HttpServletResponse response, ReciboPoliza reciboPoliza){
		Map<String, Object> parametros = new HashMap<String, Object>();
		Base64Binary pdf = null;
//		Path real para luego buscar el WSDL de los servicios Web
		setRealPath(this.getServletContext().getRealPath(WEB_INF));
		try{
			String tipoRef = request.getParameter("tipoRefPoliza");
			String CodPlan = request.getParameter("plan2");
			String RefPoliza = request.getParameter("poliza");
			pdf = recibosPolizaManager.obtenerPDFTradPolizaCopy(CodPlan, RefPoliza, tipoRef,realPath);
			byte[] content = pdf.getValue();
			response.setContentType(APPLICATION_PDF);
			response.setContentLength(content.length);
			response.setHeader(CONTENT_DISPOSITION, FILENAME_POLIZA + RefPoliza + "_" + CodPlan + ".pdf");
			response.setHeader(CACHE_CONTROL, CACHE_MUST_REVALIDATE);
			response.setHeader(PRAGMA, PUBLIC);
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
		}catch(BusinessException be){
			LOGGER.error("Se ha producido un error durante la consulta de  detalle del recibo", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		} catch (IOException e) {
			LOGGER.error("Se ha producido un error durante la impresion del detalle  del recibo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		}
		return null;
	}
	
	public ModelAndView doVerPDFPolizaOrigen (HttpServletRequest request, HttpServletResponse response, ReciboPoliza reciboPoliza){
		Map<String, Object> parametros = new HashMap<String, Object>();
		Base64Binary pdf = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String idPoliza= (request.getParameter(ID_POLIZA));
		LOGGER.debug("doVerPDFPolizaOrigen -> idPoliza="+idPoliza);
		setRealPath(this.getServletContext().getRealPath(WEB_INF));
		try{			
			pdf = recibosPolizaManager.obtenerPDFPolizaOrigen(idPoliza,realPath,usuario);		    
			byte[] content = pdf.getValue();			
			response.setContentType(APPLICATION_PDF);
			response.setContentLength(content.length);
			response.setHeader(CONTENT_DISPOSITION, FILENAME_POLIZA + idPoliza + ".pdf");
			response.setHeader(CACHE_CONTROL, CACHE_MUST_REVALIDATE);
			response.setHeader(PRAGMA, PUBLIC);
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
		}catch(BusinessException be){
			LOGGER.error("Se ha producido un error durante la consulta de detalle del recibo", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		} catch (IOException e) {
			LOGGER.error("Se ha producido un error durante la impresion del detalle del recibo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			return new ModelAndView(MODULO_UTILIDADES_RECIBOS_ERROR_RECIBO);
		}
		return null;
	}
	
	public void setRecibosPolizaManager(RecibosPolizaManager recibosPolizaManager) {
		this.recibosPolizaManager = recibosPolizaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	
	
}
