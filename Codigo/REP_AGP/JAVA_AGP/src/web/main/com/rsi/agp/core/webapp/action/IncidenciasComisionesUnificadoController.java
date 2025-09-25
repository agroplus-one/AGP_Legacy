package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.impl.IIncidenciasComisionesUnificadoDao;
import com.rsi.agp.core.jmesa.service.IIncidenciasComisionesUnificadoService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;


public class IncidenciasComisionesUnificadoController extends
		BaseMultiActionController {

	// Constantes
	private static final String MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO = "mensaje.comisiones.incidencia.cargar.Erroneo";
	private static final String MENSAJE = "mensaje";
	private static final String SE_HA_PRODUCIDO_UN_ERROR = "Se ha producido un error: ";
	private static final String PLAN_LINEA_COM = "planLineaCom";
	private static final String COD_LINEA_COM = "codLineaCom";
	private static final String INCIDENCIAS_COMISIONES_UNIFICADAS = "incidenciasComisionesUnificadas";
	private static final String PROCEDENCIA = "procedencia";
	private static final String MENSAJE_ERROR_GENERAL = "mensaje.error.general";
	private static final String ALERTA = "alerta";
	private static final String FICHERO_INCIDENCIA_UNIF_BEAN = "ficheroIncidenciaUnifBean";
	private static final String ID_FICHERO_UNIFICADO = "idFicheroUnificado";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	
	
	private static final Log logger = LogFactory.getLog(IncidenciasComisionesController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IIncidenciasComisionesUnificadoService incidenciasComisionesUnificadoService;
	private IIncidenciasComisionesUnificadoDao incidenciasComisionesUnificadoDao;
	
	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, FicheroIncidenciasUnificado fichero)
			throws Exception {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		FicheroUnificado ficheroUnificado = null;
		Long idFicheroUnificado = null;
		
		try {		
			String origenLlamada = request.getParameter(ORIGEN_LLAMADA);
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			if (null != request.getParameter(ID_FICHERO_UNIFICADO))
				idFicheroUnificado = new Long(request.getParameter(ID_FICHERO_UNIFICADO));
			
			//si lo tenemos en session es que hemos pulsado el boton volver y mantenemos el filtro
			if (request.getSession().getAttribute(FICHERO_INCIDENCIA_UNIF_BEAN)!=null){
				FicheroIncidenciasUnificado ficheroSesion = (FicheroIncidenciasUnificado)request.getSession().getAttribute(FICHERO_INCIDENCIA_UNIF_BEAN);
				request.getSession().removeAttribute(FICHERO_INCIDENCIA_UNIF_BEAN);
				fichero = ficheroSesion;
			}
			
			
			if(idFicheroUnificado!=null) {
				ficheroUnificado = (FicheroUnificado) incidenciasComisionesUnificadoDao.get(FicheroUnificado.class, idFicheroUnificado);
				fichero.setFicheroUnificado(ficheroUnificado);
			}else {
				idFicheroUnificado=fichero.getFicheroUnificado().getId();
			}
			
			asignaNulosEmpty(fichero);
			String descripcionEstadoFichero =this.getDescripcionEstadoFichero(fichero.getFicheroUnificado().getEstado());
			
			parametros.put(ORIGEN_LLAMADA, origenLlamada);
			parametros.put(ID_FICHERO_UNIFICADO, idFicheroUnificado);
			parametros.put("estadoFichero", descripcionEstadoFichero);
			
						
			String tablaHTML = getTablaHtml(request, response, fichero, usuario, origenLlamada);

			if (tablaHTML == null) {
				return null;
			} else {
				String ajax = request.getParameter("ajax");
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = tablaHTML.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					// Pasa a la jsp el codigo de la tabla a traves de este
					// atributo
					request.setAttribute("consultaIncidenciasUnificado", tablaHTML);
			}
			mv = new ModelAndView("moduloComisionesUnificado/incidenciasUnificado", "ficheroIncidenciasUnificadoBean", fichero);
		
		} catch (Exception be) {
			logger.error("Se ha producido un error", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
		}
		return mv.addAllObjects(parametros);
	}
	
	private String getTablaHtml(HttpServletRequest request,
			HttpServletResponse response,
			FicheroIncidenciasUnificado ficheroIncidenciasUnificado, Usuario usuario,
			String origenLlamada) {

		List<BigDecimal> listaGrupoEntidades = usuario
				.getListaCodEntidadesGrupo();
		
		String tabla = incidenciasComisionesUnificadoService.getTabla(request, response,
				ficheroIncidenciasUnificado, origenLlamada, listaGrupoEntidades,
				incidenciasComisionesUnificadoDao);
		return tabla;
	}
	
	private String getDescripcionEstadoFichero(Character estado){
		String res="";
		if(estado!=null){
			if (estado.toString().equals("C")){
				res="Correcto";
			}
			if (estado.toString().equals("A")){
				res="Aviso";
			}
			if (estado.toString().equals("X")){
				res="Cargado";
			}
			if (estado.toString().equals("E")){
				res="Erróneo";
			}
		}	
		return res;
	}

	private void asignaNulosEmpty(FicheroIncidenciasUnificado fichero) {
		if(fichero.getEsMedColectivo()!=null && fichero.getEsMedColectivo().isEmpty())
			fichero.setEsMedColectivo(null);
		
		if(fichero.getIdcolectivo()!=null && fichero.getIdcolectivo().isEmpty())
			fichero.setIdcolectivo(null);
		
		if(fichero.getMensaje()!=null && fichero.getMensaje().isEmpty())
			fichero.setMensaje(null);
			
		if(fichero.getOficina()!=null && fichero.getOficina().isEmpty())
			fichero.setOficina(null);
			
		if(fichero.getRefpoliza()!=null && fichero.getRefpoliza().isEmpty())
			fichero.setRefpoliza(null);
			
		if(fichero.getSubentidad()!=null && fichero.getSubentidad().isEmpty())
			fichero.setSubentidad(null);
	}

	public ModelAndView doRedirigir(HttpServletRequest request, HttpServletResponse response, FicheroIncidenciasUnificado ficheroIncidenciaUnifBean) throws Exception{
		logger.debug("init - doRedirigir");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();			
		Integer numPagina = new Integer(0);
		
		try {			
			
			if (request.getParameter("pagina") != null){
				numPagina = Integer.valueOf(request.getParameter("pagina"));
			}	
			//guardamos el filtro en sesion para que al volver no se pierda
			request.getSession().setAttribute(FICHERO_INCIDENCIA_UNIF_BEAN, ficheroIncidenciaUnifBean);
			logger.debug("en cada caso se filtra con el tipo de incidencia correspondiente si es necesario");
			
			switch (numPagina){			
				case 4:{	
					parametros = inicializarParametros(ficheroIncidenciaUnifBean);	
					parametros.put(PROCEDENCIA, INCIDENCIAS_COMISIONES_UNIFICADAS);
					mv = new ModelAndView("redirect:/subentidadMediadora.html",parametros);
					break;
				}
				case 5:{
					parametros.put("idColectivoComisiones", StringUtils.nullToString(ficheroIncidenciaUnifBean.getIdcolectivo()));
					parametros.put("idFicheroComisiones", ficheroIncidenciaUnifBean.getFicheroUnificado().getId());
					parametros.put("tipoFicheroComisiones", ficheroIncidenciaUnifBean.getFicheroUnificado().getTipoFichero());
					if (null!= ficheroIncidenciaUnifBean.getLinea()) {
						if (null!=ficheroIncidenciaUnifBean.getLinea().getCodlinea())
							parametros.put(COD_LINEA_COM, ficheroIncidenciaUnifBean.getLinea().getCodlinea());
						if (null!=ficheroIncidenciaUnifBean.getLinea().getCodplan())
							parametros.put(PLAN_LINEA_COM, ficheroIncidenciaUnifBean.getLinea().getCodplan());
					}
					
					parametros.put("vengoDComisiones","true");
					parametros.put(PROCEDENCIA,INCIDENCIAS_COMISIONES_UNIFICADAS);
					mv = new ModelAndView("redirect:/colectivo.html",parametros);
					break;
				}
				case 6: case 7:{;
												
					
					parametros = inicializarParametros(ficheroIncidenciaUnifBean);	
					parametros.put(PROCEDENCIA, INCIDENCIAS_COMISIONES_UNIFICADAS);
					
					if (null!= ficheroIncidenciaUnifBean.getLinea()) {
						if (null!=ficheroIncidenciaUnifBean.getLinea().getCodlinea())
							parametros.put(COD_LINEA_COM, ficheroIncidenciaUnifBean.getLinea().getCodlinea());
						if (null!= ficheroIncidenciaUnifBean.getLinea().getCodplan())
							parametros.put(PLAN_LINEA_COM, ficheroIncidenciaUnifBean.getLinea().getCodplan());
				
					}
					
					if (numPagina == 6) {
						//-	Param Grales.: accederá al mantenimiento de parámetros generales de comisiones.		
						mv = new ModelAndView("redirect:/comisionesCultivos.html?method=doConsultaParam",parametros);
					}
					else if (numPagina == 7) {
						//-	Coms E-S Med.: accederá al mantenimiento de comisiones por E-S Mediadora.
						mv = new ModelAndView("redirect:/comisionesCultivos.html",parametros);
					}
					break;
				}
			
				case 8:{
					//-	Descuentos: accederá al mantenimiento de descuentos.
					parametros = inicializarParametros(ficheroIncidenciaUnifBean);	
					parametros.put(ORIGEN_LLAMADA, INCIDENCIAS_COMISIONES_UNIFICADAS);
					
					if (null!= ficheroIncidenciaUnifBean.getSubentidad())
						parametros.put("subentidad", ficheroIncidenciaUnifBean.getSubentidad());
					
					if (null!= ficheroIncidenciaUnifBean.getOficina())
						parametros.put("codOficina", ficheroIncidenciaUnifBean.getOficina());
					
					if (null!= ficheroIncidenciaUnifBean.getLinea()) {
						if (null!=ficheroIncidenciaUnifBean.getLinea().getCodlinea())
							parametros.put(COD_LINEA_COM, ficheroIncidenciaUnifBean.getLinea().getCodlinea());
						if (null!=ficheroIncidenciaUnifBean.getLinea().getCodplan())
							parametros.put(PLAN_LINEA_COM, ficheroIncidenciaUnifBean.getLinea().getCodplan());
					}
					     
					mv = new ModelAndView("redirect:/mtoDescuentos.run").addAllObjects(parametros);
					break;
				}
				default:	
					mv = new ModelAndView("moduloComisiones/incidencias","ficheroIncidenciaBean",ficheroIncidenciaUnifBean).addAllObjects(parametros);
			}			
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, ficheroIncidenciaUnifBean);
		}
		logger.debug("end - doRedirigir");
		return mv;
	}
		
	private Map<String, Object> inicializarParametros(FicheroIncidenciasUnificado ficheroIncidenciaUnifBean) throws BusinessException {
		logger.debug("init - getFichero");		
		Map<String, Object> parametros = new HashMap<String, Object>();	
		String entidad = null;
		String subentidad = null;
		
		try {
			//Mejora boton gge 1-02-2011 Tamara
			//ASF - Sigpe 5969: Cogemos la E-S Med del colectivo.
			if (!ficheroIncidenciaUnifBean.getEsMedColectivo().equals("")){				
				String[] entidadSubentidad = ficheroIncidenciaUnifBean.getEsMedColectivo().split("-"); 
				if (entidadSubentidad.length>0){
					entidad = entidadSubentidad[0];
					parametros.put("entidadmediadora",entidad);
				}
				
				if (entidadSubentidad.length>1){
					subentidad = entidadSubentidad[1];
					parametros.put("subentidad",subentidad);
				}
			}
		
			
			if (null!= ficheroIncidenciaUnifBean.getLinea() && 
					null!=ficheroIncidenciaUnifBean.getLinea().getCodplan()){				
				parametros.put("planIncidencias", ficheroIncidenciaUnifBean.getLinea().getCodplan().longValue());
			}
			
			parametros.put("tipoFichero", ficheroIncidenciaUnifBean.getFicheroUnificado().getTipoFichero());	
			parametros.put("idFichero",ficheroIncidenciaUnifBean.getFicheroUnificado().getId() );
						
		} catch (Exception e) {
			logger.error("Se ha producido un error al redirigir a GCE: " + e.getMessage());
			throw new BusinessException("Se ha producido un error al redirigir a GCE", e);
		}		
		
		logger.debug("end - getFichero");
		return parametros;
	}
	
	public ModelAndView doImprimir(HttpServletRequest request, HttpServletResponse response, FicheroIncidenciasUnificado ficheroIncidenciasBean) throws Exception{
		List<FicheroIncidenciasUnificado>  listIncidencias = null;
		
		logger.debug("recuperamos el listado de incidencias del fichero y su estado");	
		asignaNulosEmpty(ficheroIncidenciasBean);
		listIncidencias = incidenciasComisionesUnificadoService.gestListaIncidencias(ficheroIncidenciasBean,incidenciasComisionesUnificadoDao);
		
		logger.debug("listIncidencias size: " + listIncidencias.size());			
		
		request.setAttribute("listIncidencias", listIncidencias);
		
		return new ModelAndView("forward:/informes.html?method=doInformeIncidenciasUnificado");
	}

	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, FicheroIncidenciasUnificado ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doCargar");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		boolean ficheroCargado = false;		
		FicheroUnificado fichero = null;
		
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String fechaAceptacionFicheroStr = request.getParameter("fechaAceptacionFichero");
		Date fechaAceptacionFichero = sdf.parse(fechaAceptacionFicheroStr);
		
		try {	
			logger.debug("se comprueba si el fichero no es nulo");
			if ((ficheroIncidenciaBean.getFicheroUnificado() != null) || (ficheroIncidenciaBean.getFicheroUnificado().getId() != null)){
				
				fichero = (FicheroUnificado) incidenciasComisionesUnificadoDao.get(FicheroUnificado.class, ficheroIncidenciaBean.getFicheroUnificado().getId());
				
				if (fichero != null){
					logger.debug("se acepta el fichero siempre que no tenga ninguna incidencia de tipo erroneo");
					ficheroCargado = incidenciasComisionesUnificadoService.cargarFichero(
							fichero, fechaAceptacionFichero, incidenciasComisionesUnificadoDao);
					if (ficheroCargado){
						parametros.put(MENSAJE, bundle.getString("mensaje.comisiones.incidencia.cargar.OK"));
					} else {
						parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
					}
	
				} else {
					parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
				}					
						
			} else {
				parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
			}			
			
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, new FicheroIncidenciasUnificado());
		}
			
		logger.debug("end - doCargar");
		return mv.addAllObjects(parametros);
	}
	
	
	public ModelAndView doVerificar(HttpServletRequest request, HttpServletResponse response, FicheroIncidenciasUnificado ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doVerificar");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		FicheroUnificado fichero = new FicheroUnificado(); 
		
		try {	
			if ((ficheroIncidenciaBean.getFicheroUnificado() != null) || (ficheroIncidenciaBean.getFicheroUnificado().getId() != null)){				
				
				fichero = (FicheroUnificado) incidenciasComisionesUnificadoDao.get(FicheroUnificado.class, ficheroIncidenciaBean.getFicheroUnificado().getId());
				if (fichero != null){
					incidenciasComisionesUnificadoService.verificarTodos(fichero,incidenciasComisionesUnificadoDao);
					parametros.put(MENSAJE, bundle.getString("mensaje.comisiones.incidencia.cargar.Verificado"));
				} else {
					parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
				}
			} else {
				parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
			}
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		}
			
		logger.debug("end - doVerificar");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doRecargar(HttpServletRequest request, HttpServletResponse response, FicheroIncidenciasUnificado ficheroIncidenciaBean) throws Exception{

		logger.debug("IncidenciasComisionesUnificadoController - doRecargar - init");
				
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		FicheroUnificado fichero = new FicheroUnificado(); 
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		try {	
			if ((ficheroIncidenciaBean.getFicheroUnificado() != null) || (ficheroIncidenciaBean.getFicheroUnificado().getId() != null)){				
				
				fichero = (FicheroUnificado) incidenciasComisionesUnificadoDao.get(FicheroUnificado.class, ficheroIncidenciaBean.getFicheroUnificado().getId());
				if (fichero != null){
					incidenciasComisionesUnificadoService.recargarFichero(fichero,usuario, request, incidenciasComisionesUnificadoDao);
					parametros.put(MENSAJE, bundle.getString("mensaje.comisiones.incidencia.cargar.Verificado"));
				} else {
					parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
				}
			} else {
				parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_INCIDENCIA_CARGAR_ERRONEO));
			}
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		}
			
		logger.debug("IncidenciasComisionesUnificadoController - doRecargar - init");

		return mv.addAllObjects(parametros);
	}


	public void setIncidenciasComisionesUnificadoDao(
		IIncidenciasComisionesUnificadoDao incidenciasComisionesUnificadoDao) {
		this.incidenciasComisionesUnificadoDao = incidenciasComisionesUnificadoDao;
}
	
	public void setIncidenciasComisionesUnificadoService(
			IIncidenciasComisionesUnificadoService incidenciasComisionesUnificadoService) {
		this.incidenciasComisionesUnificadoService = incidenciasComisionesUnificadoService;
	}
	
	public ModelAndView doRevisarIncidencia(HttpServletRequest request,
			HttpServletResponse response, FicheroIncidenciasUnificado fichero)
			throws Exception {

		logger.debug("init - doRevisarIncidencia");

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			String idIncidencia = request.getParameter("idIncidencia");

			if (!StringUtils.isNullOrEmpty(idIncidencia)) {

				incidenciasComisionesUnificadoService.revisarIncidencia(
						Long.valueOf(idIncidencia), 'R',
						incidenciasComisionesUnificadoDao);
			} else {

				throw new BusinessException(
						"No se han recibido los parámetros necesarios.");
			}

			parametros.put(MENSAJE, "Incidencia revisada correctamente.");

			mv = doConsulta(request, response, fichero);

		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, fichero);
		}

		logger.debug("end - doRevisarIncidencia");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doRevisarMultiple(HttpServletRequest request,
			HttpServletResponse response, FicheroIncidenciasUnificado fichero)
			throws Exception {

		logger.debug("init - doRevisarMultiple");

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			String listaIdsMarcados = request.getParameter("listaIdsMarcados");
			String estado = request.getParameter("idEstadoRevision");

			if (!StringUtils.isNullOrEmpty(listaIdsMarcados)
					&& !StringUtils.isNullOrEmpty(estado)) {

				String[] listaIdsMarcadosArr = listaIdsMarcados.split(",");

				for (String idIncidencia : listaIdsMarcadosArr) {

					incidenciasComisionesUnificadoService.revisarIncidencia(
							Long.valueOf(idIncidencia), estado.charAt(0),
							incidenciasComisionesUnificadoDao);
				}
			} else {

				throw new BusinessException(
						"No se han recibido los parámetros necesarios.");
			}

			parametros.put(MENSAJE, "Acción realizada correctamente.");

			mv = doConsulta(request, response, fichero);

		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, fichero);
		}

		logger.debug("end - doRevisarMultiple");
		return mv.addAllObjects(parametros);
	}
}
