package com.rsi.agp.core.webapp.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.impl.ReferenciaColectivoManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.ColectivoReferencia;


public class ReferenciaColectivoController extends BaseMultiActionController  {

	private static final Log logger = LogFactory.getLog(ReferenciaColectivoController.class);
	private ReferenciaColectivoManager referenciaColectivoManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public final ModelAndView doConsulta (HttpServletRequest request, HttpServletResponse response, ColectivoReferencia colectivoReferenciaBean) throws Exception {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		final ModelAndView mv = new ModelAndView("moduloTaller/referencia/referenciaColectivo", "colectivoReferenciaBean", colectivoReferenciaBean);
		
		HashMap<String, Object> filtroConsulta = new HashMap<String, Object>();
		
		final String referenciaInicio = request.getParameter("referenciaIni");
		final String referenciaFin = request.getParameter("referenciaFin");
		
		filtroConsulta.put("referenciaIni", referenciaInicio );
		filtroConsulta.put("referenciaFin", referenciaFin );

		parametros = getRefLibreYUltima(parametros);
		
		parametros.put("filtro", filtroConsulta);
		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	
	public final ModelAndView doGenerar (HttpServletRequest request, HttpServletResponse response, ColectivoReferencia colectivoReferenciaBean) throws Exception {
		logger.debug("init - doGenerar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		final ModelAndView mv = new ModelAndView("moduloTaller/referencia/referenciaColectivo", "colectivoReferenciaBean", colectivoReferenciaBean);
		final Integer longitudRef = Integer.parseInt(bundle.getString("referencia.longitud"));
		final String referenciaIni = request.getParameter("referenciaIni");
		final String referenciaFin = request.getParameter("referenciaFin");
		Integer numRefIni = null;
		Integer numRefFin = null;
		String letraRef = "";
		
		// Comprobamos que hemos recibido ambos códigos de referencia
		if (null != referenciaIni && !"".equals(referenciaIni) && null != referenciaFin && !"".equals(referenciaFin)) {
			numRefIni = Integer.parseInt(referenciaIni.substring(1));
			numRefFin = Integer.parseInt(referenciaFin.substring(1));
			letraRef = referenciaIni.substring(0,1);
		}else{
			parametros.put("alerta",bundle.getString("mensaje.referencia.entrada.KO"));
			parametros = getRefLibreYUltima(parametros);
			return mv.addAllObjects(parametros);
		}
		if (null == numRefIni || null == numRefFin || referenciaIni.length() > longitudRef || referenciaFin.length() > longitudRef) {
			parametros.put("alerta",bundle.getString("mensaje.referencia.entrada.KO"));
			parametros = getRefLibreYUltima(parametros);
			return mv.addAllObjects(parametros);
		}

		String numRef;
		String referencia;
		ColectivoReferencia colectivoReferencia;
		final List<ColectivoReferencia> listaReferencias = new ArrayList<ColectivoReferencia>();
		for (Integer i = numRefIni; i <= numRefFin; i++) {
			numRef = i.toString();
			while (numRef.length() < longitudRef - 1) {
				numRef = "0".concat(numRef);
			}
			referencia = letraRef.concat(numRef);
			colectivoReferencia = referenciaColectivoManager.getRefColectivoByRef(referencia);
			if (null == colectivoReferencia) {
				colectivoReferencia = new ColectivoReferencia(null, referencia);
				
				//Obtenemos el digito de control calculado
				String dccalculado = StringUtils.getDigitoControl(Integer.parseInt(referencia));
				// Si el dc calculado es -1 es fallo en el properties
				if (dccalculado.equals("-1")) {
					    //Solo si no hay errores anteriores muestro mensaje de fallo properties .
					logger.debug("error en el archivo Properties");
					parametros.put("alerta","Se ha producido un error en la aplicación");
					parametros = getRefLibreYUltima(parametros);
					return mv.addAllObjects(parametros);
				}else{
					colectivoReferencia.setDc(dccalculado);
					listaReferencias.add(colectivoReferencia);
				}
			} else {
				parametros.put("alerta", MessageFormat.format(bundle.getString("mensaje.referencia.duplicada"),
						colectivoReferencia.getReferencia()));
				parametros = getRefLibreYUltima(parametros);
				return mv.addAllObjects(parametros);
			}
		}
		referenciaColectivoManager.addReferencias(listaReferencias);
		parametros = getRefLibreYUltima(parametros);
		parametros.put("mensaje", bundle.getString("mensaje.referencia.alta.OK"));
		logger.debug("end - doGenerar");
		return mv.addAllObjects(parametros);
	}
	
	private Map<String, Object> getRefLibreYUltima(Map<String, Object> parametros ){
		final Integer refLibres = referenciaColectivoManager.getNumRefLibres();
		final String ultimaRef = referenciaColectivoManager.getUltimaRef();
		parametros.put("refLibres", refLibres);
		parametros.put("ultimaRef", ultimaRef);
		
		return parametros;
	}
	
	public final void setReferenciaColectivoManager(final ReferenciaColectivoManager referenciaColectivoManager) {
		this.referenciaColectivoManager = referenciaColectivoManager;
	}

}
