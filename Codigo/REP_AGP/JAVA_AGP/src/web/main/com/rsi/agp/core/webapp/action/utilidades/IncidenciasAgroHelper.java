package com.rsi.agp.core.webapp.action.utilidades;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.CharUtils;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.AsuntosIncId;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;

public class IncidenciasAgroHelper {
	
	private static final BigDecimal ASUNTO_INC_ACTIVO = new BigDecimal("1");
	private static final String INC_AGRO = "incidenciasAgro";
	private static final String IMPRESION_INC = "impresionIncidencias";

	public static Map<String, Object> agregarParametrosDeVuelta(Map<String, Object> params, HttpServletRequest req){
		String origen = req.getParameter("origen");
		if(INC_AGRO.equals(origen)){
			params.put("plan", req.getParameter("plan"));
			params.put("tipoBusqueda", req.getParameter("tipoBusqueda"));
			params.put("referencia_pol", req.getParameter("referencia_pol"));
			params.put("linea", req.getParameter("linea"));
			params.put("nifcif", req.getParameter("nifcif"));
		} else if(IMPRESION_INC.equals(origen)) {
			params.put("referencia", req.getParameter("referencia"));
			params.put("linea", req.getParameter("linea"));
			params.put("fechaEnvio", req.getParameter("fechaEnvio"));
		}
		params.put("origen", origen);
		return params;
	};
	
	public static Incidencias obtenerIncidenciaVista(HttpServletRequest req){
		
		Incidencias incidencia = new Incidencias();
		String year = req.getParameter("anioDoc");
		incidencia.setAnhoincidencia(new BigDecimal(year));
		incidencia.setCodestado(Constants.ESTADO_INC_LIMBO);
		String referenciaDoc = req.getParameter("referenciaDoc");
		incidencia.setReferencia(referenciaDoc);

		String tipoPolizaReferencia = req.getParameter("tipoPolizaDoc");
		Character tipoPoliza = tipoPolizaReferencia.equals("P") ? 'P' : 'C';	
		incidencia.setTiporef(tipoPoliza);
		String fechaEstado = req.getParameter("fechaEstadoDoc");
		incidencia.setFechaestadoagro(new Date(Long.parseLong(fechaEstado)));
		incidencia.setFechaestado(new Date());
		String idEnvio = req.getParameter("idEnvioDoc");
		incidencia.setIdenvio(idEnvio);
		String numeroDocumento = req.getParameter("numDoc");
		incidencia.setNumdocumentos(new BigDecimal(numeroDocumento));
		String numeroIncidencia = req.getParameter("numIncidenciaDoc");
		incidencia.setNumincidencia(new BigDecimal(numeroIncidencia));
		
		String plan = req.getParameter("planDoc");
		incidencia.setCodplan(new BigDecimal(plan));
		return incidencia;
	}
	
	public static AsuntosInc obtenerAsuntoVista(HttpServletRequest req){
		String codAsuntoDoc = req.getParameter("codAsuntoDoc");
		String asuntoDoc = req.getParameter("asuntoDoc");
		AsuntosInc asunto = new AsuntosInc(new AsuntosIncId(codAsuntoDoc, 'P'), asuntoDoc, ASUNTO_INC_ACTIVO);
		return asunto;
	}
	
	public static DocsAfectadosInc obtenerDocAfectadosVista(HttpServletRequest req){
		String codDocAfecString = req.getParameter("codDocAfec");
		Character codDocAfectado = CharUtils.toChar(codDocAfecString);
		DocsAfectadosInc docsAfectadosInc = new DocsAfectadosInc();
		docsAfectadosInc.setCoddocafectado(codDocAfectado);
		return docsAfectadosInc;
	}
}
