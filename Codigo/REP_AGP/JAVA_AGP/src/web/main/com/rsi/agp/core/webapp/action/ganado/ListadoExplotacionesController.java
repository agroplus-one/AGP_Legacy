package com.rsi.agp.core.webapp.action.ganado;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.impl.ganado.IExplotacionesService;
import com.rsi.agp.core.managers.IBaseManager;
import com.rsi.agp.core.managers.IDatosExplotacionesManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesManager;
import com.rsi.agp.core.managers.impl.ganado.InformacionRega;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;


public class ListadoExplotacionesController extends BaseMultiActionController {

	private static final String REGIMEN = "regimen";
	private static final String ESPECIE = "especie";
	private static final String SUBEXPLOTACION = "subexplotacion";
	private static final String SIGLA = "sigla";
	private static final String LONGITUD = "longitud";
	private static final String LATITUD = "latitud";
	private static final String SUBTERMINO = "subtermino";
	private static final String TERMINO = "termino";
	private static final String COMARCA = "comarca";
	private static final String PROVINCIA = "provincia";
	private static final String PREDICATE = "predicate";
	private static final String ALERTA = "alerta";
	private static final String MENSAJE = "mensaje";
	private static final String MODO_LECTURA = "modoLectura";
	
	private static final Log logger = LogFactory.getLog(ListadoExplotacionesController.class);
	private IExplotacionesService explotacionesService;
	private IDatosExplotacionesManager datosExplotacionesManager;
	private IBaseManager baseManager;
	private ExplotacionesManager explotacionesManager;
	private ClaseManager claseManager;
	
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private final String VACIO = "";

	/**
	 * Redirige a la pantalla de listado de explotaciones y realiza la primera
	 * busqueda
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doPantallaListaExplotaciones(HttpServletRequest request, HttpServletResponse response,Poliza polizaBean) {
		
		String tablaExplotaciones;		
		String modoLectura = StringUtils.nullToString(request.getParameter(ListadoExplotacionesController.MODO_LECTURA));		
		Boolean esModoLectura=(modoLectura.compareTo(ListadoExplotacionesController.MODO_LECTURA)==0);
		
		String vieneDeUtilidades=StringUtils.nullToString(request.getParameter("vieneDeUtilidades"));
		
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Si la poliza no viene cargada se obtiene de BD
		if (polizaBean.getLinea().getLineaseguroid() == null) {
			if(polizaBean.getIdpoliza() != null){
				polizaBean =  datosExplotacionesManager.getPoliza(polizaBean.getIdpoliza());
			}
			else{
				polizaBean =  datosExplotacionesManager.getPoliza(new Long(request.getParameter("idPoliza")));
			}
		}
		
		if(null==polizaBean || null==polizaBean.getIdpoliza()|| null==polizaBean.getClase()|| null==polizaBean.getLinea() || null!=polizaBean.getLinea().getLineaseguroid()){	
			Poliza plz=datosExplotacionesManager.getPoliza(polizaBean.getIdpoliza());
			Long idClase= getIdClase(plz.getClase(), plz.getLinea().getLineaseguroid());
			datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parameters);
			polizaBean.setLinea(plz.getLinea());
		}		
		
		
		logger.debug("doPantallaListaExplotaciones - Redireccion a pantalla de listado de explotaciones");
		ModelAndView mv = new ModelAndView("moduloExplotaciones/explotaciones/listadoExplotaciones", "polizaBean", polizaBean);
		
		try {
			String origenLlamada = request.getParameter("origenllamada");
			String operacion = request.getParameter("operacion");
			if (operacion != null && !VACIO.equals(operacion)) {
				String explotacionId = request.getParameter("explotacionId");
				if ("borrarExplotacion".equals(operacion)) {
					if (explotacionId != null && !VACIO.equals(explotacionId)) {
						try {
							explotacionesManager.borrarExplotacion(Long.valueOf(explotacionId));
							parameters.put(ListadoExplotacionesController.MENSAJE, bundle.getString("mensaje.listadoExplotaciones.borrar.OK"));
						} catch (Exception e) {
							parameters.put(ListadoExplotacionesController.ALERTA, bundle.getString("mensaje.listadoExplotaciones.borrar.KO"));
						}
					} else {
						throw new Exception(
								"No se ha recibido el identificador de la explotacion sobre la que realizar la accion.");
					}
				} else if ("duplicarExplotacion".equals(operacion)) {
					if (explotacionId != null && !VACIO.equals(explotacionId)) {
						try {
							Explotacion exp = explotacionesManager.duplicarExplotacion(Long.valueOf(explotacionId),null);
							parameters.put(ListadoExplotacionesController.MENSAJE, bundle.getString("mensaje.listadoExplotaciones.duplicar.OK"));
							parameters.put("idExplotacion", exp.getId());
							parameters.put("method", "doEditar");
							mv = new ModelAndView("redirect:/datosExplotaciones.html").addAllObjects(parameters); 
						} catch (Exception e) {
							parameters.put(ListadoExplotacionesController.ALERTA, bundle.getString("mensaje.listadoExplotaciones.duplicar.KO"));
						}
					} else {
						throw new Exception("No se ha recibido el identificador de la explotacion sobre la que realizar la accion.");
					}
				} else if ("recalcularPrecios".equals(operacion)) {
					// TODO: OPERACION SIN DESARROLLAR
					parameters.put(ListadoExplotacionesController.ALERTA, "Operacion pendiente de desarrollo.");
				}
			}
			
		
			Explotacion predicate = null;
			
			if(origenLlamada==null)request.getSession().removeAttribute(ListadoExplotacionesController.PREDICATE);
			
			if(null!=origenLlamada && origenLlamada.compareTo("listaExplotaciones")==0){			
				predicate = new Explotacion();
				predicate.setPoliza(polizaBean);
				putFilterParametersInPredicate(request, predicate);
				request.getSession().setAttribute(ListadoExplotacionesController.PREDICATE, predicate);
			}else if(null!=origenLlamada && origenLlamada.compareTo("datosExplotaciones")==0){
				predicate=(Explotacion) request.getSession().getAttribute(ListadoExplotacionesController.PREDICATE);
				request.getSession().removeAttribute(ListadoExplotacionesController.PREDICATE);	
				if(null==predicate){
					predicate = new Explotacion();
					predicate.setPoliza(polizaBean);
				}
			}else{
				predicate = new Explotacion();
				predicate.setPoliza(polizaBean);
				putFilterParametersInPredicate(request, predicate);
			}
			
			tablaExplotaciones = explotacionesService.getTabla(request,
					response, predicate, origenLlamada, null, null, esModoLectura);
			if (tablaExplotaciones == null) {
				return null;
			} else {
				String ajax = request.getParameter("ajax");
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = tablaExplotaciones.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					// Pasa a la jsp el codigo de la tabla a traves de este
					// atributo
					request.setAttribute("consultaExplotaciones",
							tablaExplotaciones);
			}			
			
			if(null!=origenLlamada && origenLlamada.compareTo("datosExplotaciones")==0){
				putFilterParametersInResult(predicate, parameters);
			}else{
				putFilterParametersInResult(request, parameters);
			}
			
			parameters.put("idpoliza", polizaBean.getIdpoliza());
			parameters.put(ListadoExplotacionesController.MODO_LECTURA, request.getParameter(ListadoExplotacionesController.MODO_LECTURA));
			parameters.put("vieneDeUtilidades",	request.getParameter("vieneDeUtilidades"));
			
			// MPM - Para mostrar los mensajes si vuelve de la pantalla de datos de la explotacion
			String msg = request.getParameter(ListadoExplotacionesController.MENSAJE);
			if (!StringUtils.isNullOrEmpty(msg)) parameters.put(ListadoExplotacionesController.MENSAJE, msg);
			String alerta = request.getParameter(ListadoExplotacionesController.ALERTA);
			if (!StringUtils.isNullOrEmpty(alerta)) parameters.put(ListadoExplotacionesController.ALERTA, alerta);
			
			mv.addAllObjects(parameters);
			
			if (esModoLectura && vieneDeUtilidades.compareTo("true")==0){
				//aunque polizaBean en un principio esta completa, cuando viene de Volver no. Por eso pasamos el id
				baseManager.cargaCabecera(polizaBean.getIdpoliza(), request);
			}
		} catch (Exception e) {
			logger.error("Error en doPantallaListaExplotaciones de ListadoExplotacionesController", e);
		}
		return mv;
	}
	
	
	/**
	 * Devuelve la llamada a un WebService "Información Rega"
	 * Used in datosExplotaciones.jsp
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void doInfoRega (HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JSONObject result = new JSONObject();
		String[] errorMsgs;
		logger.debug("ListadoExplotacionesController - doInfoRega - init");
		
		String codigoRega = request.getParameter("codigoRega");
		String plan = request.getParameter("plan");
		String linea = request.getParameter("linea");
			
		if (StringUtils.isNullOrEmpty(codigoRega) || StringUtils.isNullOrEmpty(plan) || StringUtils.isNullOrEmpty(linea)) {
			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };			
		} else {
			InformacionRega informacionRega = datosExplotacionesManager.getInfoRega(codigoRega, plan, linea);
			result.put("informacionRega", new JSONObject(informacionRega));
			errorMsgs = new String[] {};
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		getWriterJSON(response, result);
		logger.debug("ListadoExplotacionesController - doInfoRega - end");
	}
	
	private Long getIdClase(BigDecimal clase, Long lineaSeguroId){
		Long idClase=null;		
		if (null!=clase && null!=lineaSeguroId)
			idClase= claseManager.getClase(lineaSeguroId, clase);
		return idClase;
	}
	
	private void putFilterParametersInPredicate(
			final HttpServletRequest request, final Explotacion explotacionBean) {
		String provincia = request.getParameter(ListadoExplotacionesController.PROVINCIA);
		if (provincia != null && !VACIO.equals(provincia)) {
			explotacionBean.getTermino().getId()
					.setCodprovincia(new BigDecimal(provincia));
		}
		String comarca = request.getParameter(ListadoExplotacionesController.COMARCA);
		if (comarca != null && !VACIO.equals(comarca)) {
			explotacionBean.getTermino().getId()
					.setCodcomarca(new BigDecimal(comarca));
		}
		String termino = request.getParameter(ListadoExplotacionesController.TERMINO);
		if (termino != null && !VACIO.equals(termino)) {
			explotacionBean.getTermino().getId()
					.setCodtermino(new BigDecimal(termino));
		}
		String subtermino = request.getParameter(ListadoExplotacionesController.SUBTERMINO);
		if (subtermino != null && !VACIO.equals(subtermino)) {
			explotacionBean.getTermino().getId()
					.setSubtermino(subtermino.charAt(0));
		}
		String latitud = request.getParameter(ListadoExplotacionesController.LATITUD);
		if (latitud != null && !VACIO.equals(latitud)) {
			explotacionBean.setLatitud(Integer.valueOf(latitud));
		}
		String longitud = request.getParameter(ListadoExplotacionesController.LONGITUD);
		if (longitud != null && !VACIO.equals(longitud)) {
			explotacionBean.setLongitud(Integer.valueOf(longitud));
		}
		String rega = request.getParameter("rega");
		if (rega != null && !VACIO.equals(rega)) {
			explotacionBean.setRega(rega);
		}
		String sigla = request.getParameter(ListadoExplotacionesController.SIGLA);
		if (sigla != null && !VACIO.equals(sigla)) {
			explotacionBean.setSigla(sigla);
		}
		String subexplotacion = request.getParameter(ListadoExplotacionesController.SUBEXPLOTACION);
		if (subexplotacion != null && !VACIO.equals(subexplotacion)) {
			explotacionBean.setSubexplotacion(Integer.valueOf(subexplotacion));
		}
		String especie = request.getParameter(ListadoExplotacionesController.ESPECIE);
		if (especie != null && !VACIO.equals(especie)) {
			explotacionBean.setEspecie(Long.valueOf(especie));
		}
		String regimen = request.getParameter(ListadoExplotacionesController.REGIMEN);
		if (regimen != null && !VACIO.equals(regimen)) {
			explotacionBean.setRegimen(Long.valueOf(regimen));
		}
	}

	private void putFilterParametersInResult(final HttpServletRequest request,
			final Map<String, Object> parameters) {
		parameters.put(ListadoExplotacionesController.PROVINCIA, request.getParameter(ListadoExplotacionesController.PROVINCIA));
		parameters.put("desc_provincia", request.getParameter("desc_provincia"));
		parameters.put(ListadoExplotacionesController.COMARCA, request.getParameter(ListadoExplotacionesController.COMARCA));
		parameters.put("desc_comarca", request.getParameter("desc_comarca"));
		parameters.put(ListadoExplotacionesController.TERMINO, request.getParameter(ListadoExplotacionesController.TERMINO));
		parameters.put("desc_termino", request.getParameter("desc_termino"));
		parameters.put(ListadoExplotacionesController.SUBTERMINO, request.getParameter(ListadoExplotacionesController.SUBTERMINO));
		parameters.put(ListadoExplotacionesController.LATITUD, request.getParameter(ListadoExplotacionesController.LATITUD));
		parameters.put(ListadoExplotacionesController.LONGITUD, request.getParameter(ListadoExplotacionesController.LONGITUD));
		parameters.put("rega", request.getParameter("rega"));
		parameters.put(ListadoExplotacionesController.SIGLA, request.getParameter(ListadoExplotacionesController.SIGLA));
		parameters.put(ListadoExplotacionesController.SUBEXPLOTACION, request.getParameter(ListadoExplotacionesController.SUBEXPLOTACION));
		parameters.put(ListadoExplotacionesController.ESPECIE, request.getParameter(ListadoExplotacionesController.ESPECIE));
		parameters.put("desc_especie", request.getParameter("desc_especie"));
		parameters.put(ListadoExplotacionesController.REGIMEN, request.getParameter(ListadoExplotacionesController.REGIMEN));
		parameters.put("desc_regimen", request.getParameter("desc_regimen"));
	}

	private void putFilterParametersInResult(final Explotacion predicate,
			final Map<String, Object> parameters) {
		if(null!=predicate){
			if(null!=predicate.getTermino() && null!=predicate.getTermino().getId()){
				if(null!=predicate.getTermino().getId().getCodprovincia())parameters.put(ListadoExplotacionesController.PROVINCIA, predicate.getTermino().getId().getCodprovincia());
				if(null!=predicate.getTermino().getId().getCodcomarca())parameters.put(ListadoExplotacionesController.COMARCA, predicate.getTermino().getId().getCodcomarca());
				if(null!=predicate.getTermino().getId().getCodtermino())parameters.put(ListadoExplotacionesController.TERMINO, predicate.getTermino().getId().getCodtermino());
				if(null!=predicate.getTermino().getId().getSubtermino())parameters.put(ListadoExplotacionesController.SUBTERMINO, predicate.getTermino().getId().getSubtermino());
			
				if(null!=predicate.getTermino().getProvincia() && null!=predicate.getTermino().getProvincia().getNomprovincia()){
					parameters.put("desc_provincia", predicate.getTermino().getProvincia().getNomprovincia());
				}
			
				if(null!=predicate.getTermino().getComarca() && null!=predicate.getTermino().getComarca().getNomcomarca()){
					parameters.put("desc_comarca", predicate.getTermino().getComarca().getNomcomarca());
				}
				
				if(null!= predicate.getTermino().getNomtermino()){
					parameters.put("desc_termino", predicate.getTermino().getNomtermino());
				}
			}
			
			if(null!=predicate.getLatitud())parameters.put(ListadoExplotacionesController.LATITUD, predicate.getLatitud());
			if(null!=predicate.getLongitud())parameters.put(ListadoExplotacionesController.LONGITUD, predicate.getLongitud());
			if(null!=predicate.getRega())parameters.put("rega", predicate.getRega());
			if(null!=predicate.getSigla())parameters.put(ListadoExplotacionesController.SIGLA, predicate.getSigla());
			if(null!=predicate.getSubexplotacion())parameters.put(ListadoExplotacionesController.SUBEXPLOTACION, predicate.getSubexplotacion());
			if(null!=predicate.getEspecie())parameters.put(ListadoExplotacionesController.ESPECIE, predicate.getEspecie());
			if(null!=predicate.getNomespecie())parameters.put("desc_especie",predicate.getNomespecie());
			if(null!=predicate.getRegimen())parameters.put(ListadoExplotacionesController.REGIMEN, predicate.getRegimen());
			if(null!=predicate.getNomregimen())parameters.put("desc_regimen",predicate.getNomregimen());
		}
		
	}
	


	public void setDatosExplotacionesManager(
			IDatosExplotacionesManager datosExplotacionesManager) {
		this.datosExplotacionesManager = datosExplotacionesManager;
	}

	public void setExplotacionesManager(ExplotacionesManager explotacionesManager) {
		this.explotacionesManager = explotacionesManager;
	}

	public void setExplotacionesService(IExplotacionesService explotacionesService) {
		this.explotacionesService = explotacionesService;
	}

	public void setBaseManager(IBaseManager baseManager) {
		this.baseManager = baseManager;
	}


	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}  
}