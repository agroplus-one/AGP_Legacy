package com.rsi.agp.core.webapp.action.coberturas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.ICuadroCoberturasGanadoManager;
import com.rsi.agp.core.managers.ICuadroCoberturasManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class CoberturasController extends BaseMultiActionController {
	
	private ICuadroCoberturasManager cuadroCoberturasManager;
	private ICuadroCoberturasGanadoManager cuadroCoberturasGanadoManager;
	private PolizaManager polizaManager;

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Object bean) throws Exception {
		return null;
	}
	
	public ModelAndView doGetModulos(HttpServletRequest request, HttpServletResponse response, Object bean) throws Exception {
		String codmodulo = request.getParameter("codmodulo");
		String idlinea = request.getParameter("idlinea");
		String idtabla = request.getParameter("idtabla");
		String ganadoc = request.getParameter("ganadoc").toString();
		
		if (!ganadoc.equals("0")) {
			ModuloView modulo = cuadroCoberturasGanadoManager.getCoberturasModulo(codmodulo, new Long(idlinea), false);
			JSONObject moduloJSON = this.cuadroCoberturasGanadoManager.getCoberturasJSON(modulo, idtabla);
			getWriterJSON(response, moduloJSON);
		}
		else{
			ModuloView modulo = cuadroCoberturasManager.getCoberturasModulo(codmodulo, idtabla, new Long(idlinea),false);
			JSONObject moduloJSON = this.cuadroCoberturasManager.getCoberturasJSON(modulo, idtabla);
			getWriterJSON(response, moduloJSON);
		}
		
		
		return null;
	}
	
	public ModelAndView doGetModulosPoliza(HttpServletRequest request, HttpServletResponse response, Object bean) throws Exception {
		String codmodulo = request.getParameter("codmodulo");
		String idpoliza = request.getParameter("idpoliza");
		String idtabla = request.getParameter("idtabla");
		
		Poliza p = polizaManager.getPoliza(Long.valueOf(idpoliza));
		
		ModuloView modulo = null;

		// Si la línea es de Ganado
		if (p.getLinea().isLineaGanado()) {
			modulo = cuadroCoberturasGanadoManager.getCoberturasModulo(codmodulo, p.getLinea().getLineaseguroid(), false);
		}
		// Si la línea es de Agrarios
		else {
			modulo = cuadroCoberturasManager.getCoberturasModulo(codmodulo, p,false);
		}
		
		JSONObject moduloJSON = this.cuadroCoberturasManager.getCoberturasJSON(modulo, idtabla);
		getWriterJSON(response, moduloJSON);
		
		return null;
	}
	
	public ModelAndView doDetalleModulo(HttpServletRequest request, HttpServletResponse response,CapitalAsegurado capitalAseguradoBean ) throws Exception{
		
		logger.debug("init - doDetalleModulo");
		String idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		JSONObject resultado =  new JSONObject();
		
		String modoLectura = StringUtils.nullToString(request.getParameter("modoLectura"));
		String esAnexo = StringUtils.nullToString(request.getParameter("esAnexo"));
		String idAnexo = StringUtils.nullToString(request.getParameter("idAnexo"));
		
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		try {
			Poliza poliza = polizaManager.getPoliza(new Long(idPoliza));
			logger.debug("id poliza: " + idPoliza);
			
			if (StringUtils.isNullOrEmpty(esAnexo) || "false".equalsIgnoreCase(esAnexo)) {				
				if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {					
					// Si estamos en modo lectura
					if (!StringUtils.isNullOrEmpty(modoLectura) && ("true".equalsIgnoreCase(modoLectura) || "modoLectura".equalsIgnoreCase(modoLectura))) {
						logger.debug("Es poliza enviada correcta en lectura");
						resultado = cuadroCoberturasManager.getCoberturasLectura(poliza);
					}
					// Modo edicion
					else {
						logger.debug("Es poliza enviada correcta en edicion");
						resultado = cuadroCoberturasManager.getCoberturasPpalCpl(poliza, null, realPath);						
					}
				} else {
					logger.debug("Es poliza no enviada correcta");
					resultado = cuadroCoberturasManager.getCoberturas(poliza);
				}
			}
			else {
				logger.debug("Es anexo");
				if (Constants.MODULO_POLIZA_PRINCIPAL.equals(poliza.getTipoReferencia())) {
					logger.debug("Es cuadro principal");
					AnexoModificacion anexoModificacion = cuadroCoberturasManager.getAnexo(idAnexo); 
					resultado = cuadroCoberturasManager.getCoberturasPpalCpl(poliza, anexoModificacion.getCupon().getIdcupon(), realPath);
				} else {
					logger.debug("Es cuadro complementaria");
					resultado = cuadroCoberturasManager.getCoberturas(poliza);
				}
			}
			
			logger.debug("SALIDA: " + resultado.toString());			
			
			getWriterJSON(response,resultado);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al calcular las coberturas de la poliza complementaria: " + be.getMessage());
		}
		return null;
	}

	public void setCuadroCoberturasManager(
			ICuadroCoberturasManager cuadroCoberturasManager) {
		this.cuadroCoberturasManager = cuadroCoberturasManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setCuadroCoberturasGanadoManager(
			ICuadroCoberturasGanadoManager cuadroCoberturasGanadoManager) {
		this.cuadroCoberturasGanadoManager = cuadroCoberturasGanadoManager;
	}
	
	
	
}
