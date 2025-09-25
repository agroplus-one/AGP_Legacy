package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.IncidenciasComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;

@RequestMapping("/incidencias.html")
public class IncidenciasComisionesController extends BaseMultiActionController{
	
	private static final String INCIDENCIAS_COMISIONES = "incidenciasComisiones";
	private static final String FICHERO_MULT_INCIDENCIA_BEAN = "ficheroMultIncidenciaBean";
	private static final String MENSAJE_ERROR_GENERAL = "mensaje.error.general";
	private static final String ALERTA = "alerta";
	private static final String SE_HA_PRODUCIDO_UN_ERROR = "Se ha producido un error: ";
	private static final String ESTADO_FICHERO = "estadoFichero";
	private static final String LIST_INCIDENCIAS = "listIncidencias";
	private static final String MODULO_COMISIONES_INCIDENCIAS = "moduloComisiones/incidencias";
	private static final String FICHERO_INCIDENCIA_BEAN = "ficheroIncidenciaBean";
	private static final String RECUPERAMOS_EL_LISTADO_DE_INCIDENCIAS_DEL_FICHERO_Y_SU_ESTADO = "recuperamos el listado de incidencias del fichero y su estado";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String ID_FICHERO = "idFichero";
	private static final Log logger = LogFactory.getLog(IncidenciasComisionesController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IncidenciasComisionesManager incidenciasComisionesManager;
	private static final BigDecimal INCIDENCIA_SIN_DESGLOSE_GCE = new BigDecimal(1);
	private static final BigDecimal INCIDENCIA_SIN_DESGLOSE_CULTIVO = new BigDecimal(2);
		
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, FicheroIncidencia ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doConsulta");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<FicheroIncidencia> listIncidencias = null;
		String idFichero = "";
		Fichero fichero = null;
		BigDecimal plan = null;
		try {
			// recuperamos el tipo ya que es necesario para filtrar por el mismo en la siguiente pantalla
			if (request.getParameter("tipo") != null && !request.getParameter("tipo").equals("")){
				Character tipo=  request.getParameter("tipo").charAt(0);
				if (ficheroIncidenciaBean.getFichero() != null)
					ficheroIncidenciaBean.getFichero().setTipofichero(tipo);
			}
			
			if (request.getParameter(ID_FICHERO) != null){
				idFichero = StringUtils.nullToString(request.getParameter(ID_FICHERO));
			}
			
			if (!"".equals(idFichero)){
				fichero = incidenciasComisionesManager.getFicheroIncidencias(idFichero);
				if (fichero != null){
					ficheroIncidenciaBean.setFichero(fichero);
				}	
			} 
			else {
				if ((ficheroIncidenciaBean != null) && (ficheroIncidenciaBean.getFichero() != null)){
					DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
					//Obtenemos la fecha de carga del fichero desde el formulario
					if (ficheroIncidenciaBean.getFichero().getFechacarga() == null){
						String fechacarga = request.getParameter("fechacarga");
						if (!StringUtils.nullToString(fechacarga).equals("")) {
							ficheroIncidenciaBean.getFichero().setFechacarga(df.parse(fechacarga));
						}
					}
					//Obtenemos la fecha de emisión de la fase desde el formulario
					if (ficheroIncidenciaBean.getFichero().getFase() != null 
							&& ficheroIncidenciaBean.getFichero().getFase().getFechaemision() == null){
						String fechaemision = request.getParameter("fechaemision");
						if (!StringUtils.nullToString(fechaemision).equals("")) {
							ficheroIncidenciaBean.getFichero().getFase().setFechaemision(df.parse(fechaemision));
						}
					}
					//Obtenemos el plan si viene informado
					if(null!=ficheroIncidenciaBean.getLinea()&& null!=ficheroIncidenciaBean.getLinea().getCodplan()) {
						parametros.put("plan", ficheroIncidenciaBean.getLinea().getCodplan());
					}
				}
			}				
			
			logger.debug(RECUPERAMOS_EL_LISTADO_DE_INCIDENCIAS_DEL_FICHERO_Y_SU_ESTADO);
			
			if (ficheroIncidenciaBean != null && ficheroIncidenciaBean.getOficina() == null){
				ficheroIncidenciaBean.setOficina("");
			}
			//si lo tenemos en session es que hemos pulsado el boton volver y mantenemos el filtro
			if (request.getSession().getAttribute(FICHERO_INCIDENCIA_BEAN)!=null){
				FicheroIncidencia ficheroSesion = (FicheroIncidencia)request.getSession().getAttribute(FICHERO_INCIDENCIA_BEAN);
				listIncidencias = incidenciasComisionesManager.getListFicherosIncidencias(ficheroSesion);
				mv = new ModelAndView(MODULO_COMISIONES_INCIDENCIAS,FICHERO_INCIDENCIA_BEAN, ficheroSesion);
				//eliminamos el filtro de la sesión
				request.getSession().removeAttribute(FICHERO_INCIDENCIA_BEAN);
			}else{
				listIncidencias = incidenciasComisionesManager.getListFicherosIncidencias(ficheroIncidenciaBean);
				mv = new ModelAndView(MODULO_COMISIONES_INCIDENCIAS,FICHERO_INCIDENCIA_BEAN,ficheroIncidenciaBean);
			}
			
			logger.debug("listIncidencias tamano: " + listIncidencias.size());			
			
			parametros.put(LIST_INCIDENCIAS, listIncidencias);
			parametros.put("totalListSize", listIncidencias.size());
			if (!StringUtils.nullToString(request.getParameter(ESTADO_FICHERO)).equals(""))
				parametros.put(ESTADO_FICHERO, StringUtils.nullToString(request.getParameter(ESTADO_FICHERO)));
			else if (fichero != null) 
				parametros.put(ESTADO_FICHERO, this.incidenciasComisionesManager.getEstadoFichero(fichero));

			
			plan = ficheroIncidenciaBean.getFichero().getFase().getPlan();
			if (plan != null) 
				{
				parametros.put("plan", plan);
				}
			else
			{
				if( request.getParameter("codplan")!= null){
					parametros.put("plan", request.getParameter("codplan"));
				}else {
					parametros.put("plan", 0);
				}
				
			}
									
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, new FicheroIncidencia());
		}
		logger.debug("fin - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doConsultaDeuda(HttpServletRequest request, HttpServletResponse response, FicheroMultIncidencias ficheroMultIncidenciasBean) throws Exception{
		logger.debug("init - doConsultaDeuda");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<FicheroMultIncidencias> listIncidencias = null;
		String idFichero = "";
		FicheroMult fichero = null;
		try {
			// recuperamos el tipo ya que es necesario para filtrar por el mismo en la siguiente pantalla
			if (request.getParameter("tipo") != null && !request.getParameter("tipo").equals("")){
				Character tipo=  request.getParameter("tipo").charAt(0);
				if (ficheroMultIncidenciasBean.getFicheroMult() != null)
					ficheroMultIncidenciasBean.getFicheroMult().setTipoFichero(tipo);
			}
			
			if (request.getParameter(ID_FICHERO) != null){
				idFichero = StringUtils.nullToString(request.getParameter(ID_FICHERO));
			}
			
			if (!"".equals(idFichero)){
				
				fichero = incidenciasComisionesManager.getFicheroMultIncidencias(idFichero);
				if (fichero != null){
					ficheroMultIncidenciasBean.setFicheroMult(fichero);
				}	
			} 
			else {
				if ((ficheroMultIncidenciasBean != null) && (ficheroMultIncidenciasBean.getFicheroMult() != null)){
					DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
					//Obtenemos la fecha de carga del fichero desde el formulario
					if (ficheroMultIncidenciasBean.getFicheroMult().getFechaCarga() == null){
						String fechacarga = request.getParameter("fechaCarga");
						if (!StringUtils.nullToString(fechacarga).equals("")) {
							ficheroMultIncidenciasBean.getFicheroMult().setFechaCarga(df.parse(fechacarga));
						}
					}
				}
			}				
			
			logger.debug(RECUPERAMOS_EL_LISTADO_DE_INCIDENCIAS_DEL_FICHERO_Y_SU_ESTADO);
			
			if (ficheroMultIncidenciasBean != null && ficheroMultIncidenciasBean.getOficina() == null){
				ficheroMultIncidenciasBean.setOficina("");
			}
			//si lo tenemos en session es que hemos pulsado el boton volver y mantenemos el filtro
			if (request.getSession().getAttribute(FICHERO_MULT_INCIDENCIA_BEAN)!=null){
				FicheroMultIncidencias ficheroSesion = (FicheroMultIncidencias)request.getSession().getAttribute(FICHERO_MULT_INCIDENCIA_BEAN);
				listIncidencias = incidenciasComisionesManager.getListFicherosMultIncidencias(ficheroSesion);
				
				mv = new ModelAndView("moduloComisiones/incidenciasMult",FICHERO_MULT_INCIDENCIA_BEAN, ficheroSesion);
				//eliminamos el filtro de la sesion
				request.getSession().removeAttribute(FICHERO_MULT_INCIDENCIA_BEAN);
			}else{
				listIncidencias = incidenciasComisionesManager.getListFicherosMultIncidencias(ficheroMultIncidenciasBean);
				mv = new ModelAndView("moduloComisiones/incidenciasMult",FICHERO_MULT_INCIDENCIA_BEAN,ficheroMultIncidenciasBean);
			}
			
			logger.debug("listIncidencias tamano: " + listIncidencias.size());			
			
			parametros.put(LIST_INCIDENCIAS, listIncidencias);
			parametros.put("totalListSize", listIncidencias.size());
			if (!StringUtils.nullToString(request.getParameter(ESTADO_FICHERO)).equals(""))
				parametros.put(ESTADO_FICHERO, StringUtils.nullToString(request.getParameter(ESTADO_FICHERO)));
			else if (fichero != null) 
				parametros.put(ESTADO_FICHERO, this.incidenciasComisionesManager.getEstadoFicheroMult(fichero));

			
			/*plan = ficheroMultIncidenciasBean.getFicheroMult().getFase().getPlan();
			if (plan != null) 
				{
				parametros.put("plan", plan);
				}
			else
			{
				if( request.getParameter("codplan")!= null){
					parametros.put("plan", request.getParameter("codplan"));
				}else {
					parametros.put("plan", 0);
				}
				
			}*/
									
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsultaDeuda(request, response, new FicheroMultIncidencias());
		}
		logger.debug("fin - doConsultaDeuda");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doRedirigir(HttpServletRequest request, HttpServletResponse response, FicheroIncidencia ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doRedirigir");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();			
		Integer numPagina = new Integer(0);
		
		try {			
			
			if (request.getParameter("pagina") != null){
				numPagina = Integer.valueOf(request.getParameter("pagina"));
			}	
			//guardamos el filtro en sesion para que al volver no se pierda
			request.getSession().setAttribute(FICHERO_INCIDENCIA_BEAN, ficheroIncidenciaBean);
			logger.debug("en cada caso se filtra con el tipo de incidencia correspondiente si es necesario");
			switch (numPagina){			
				case 1:{
					ficheroIncidenciaBean.setTipoincidencia(INCIDENCIA_SIN_DESGLOSE_GCE);
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);			
					mv = new ModelAndView("redirect:/gge.html",parametros);		
					break;
				}
				case 2:{
					ficheroIncidenciaBean.setTipoincidencia(INCIDENCIA_SIN_DESGLOSE_CULTIVO);
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);	
					mv = new ModelAndView("redirect:/comisionesCultivos.html",parametros);
					break;
				}
				case 3:{	
					ficheroIncidenciaBean.setTipoincidencia(INCIDENCIA_SIN_DESGLOSE_GCE);
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);	
					mv = new ModelAndView("redirect:/reglamento.html",parametros);
					break;
				}
				case 4:{	
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);	
					parametros.put("procedencia", INCIDENCIAS_COMISIONES);
					mv = new ModelAndView("redirect:/subentidadMediadora.html",parametros);
					break;
				}
				case 5:{
					parametros.put("idColectivoComisiones", StringUtils.nullToString(ficheroIncidenciaBean.getIdcolectivo()));
					parametros.put("idFicheroComisiones", ficheroIncidenciaBean.getFichero().getId());
					parametros.put("tipoFicheroComisiones", ficheroIncidenciaBean.getFichero().getTipofichero());
					if (null!= ficheroIncidenciaBean.getLinea())
						if (null!=ficheroIncidenciaBean.getLinea().getCodlinea())
							parametros.put("codLineaCom", ficheroIncidenciaBean.getLinea().getCodlinea());
					if (null != ficheroIncidenciaBean.getFichero())
						if (null != ficheroIncidenciaBean.getFichero().getFase())
							if (null!= ficheroIncidenciaBean.getFichero().getFase().getPlan())
								parametros.put("planLineaCom", ficheroIncidenciaBean.getFichero().getFase().getPlan());
					parametros.put("vengoDComisiones","true");
					mv = new ModelAndView("redirect:/colectivo.html",parametros);
					break;
				}
				case 6: case 7:{;
												
					
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);	
					parametros.put("procedencia", INCIDENCIAS_COMISIONES);
					if (null!= ficheroIncidenciaBean.getLinea())
						if (null!=ficheroIncidenciaBean.getLinea().getCodlinea())
							parametros.put("codLineaCom", ficheroIncidenciaBean.getLinea().getCodlinea());
					if (null != ficheroIncidenciaBean.getFichero())
						if (null != ficheroIncidenciaBean.getFichero().getFase())
							if (null!= ficheroIncidenciaBean.getFichero().getFase().getPlan())
								parametros.put("planLineaCom", ficheroIncidenciaBean.getFichero().getFase().getPlan());
					
					if (numPagina == 6) {
						//-	Param Grales.: accederᠡl mantenimiento de par᭥tros generales de comisiones.		
						mv = new ModelAndView("redirect:/comisionesCultivos.html?method=doConsultaParam",parametros);
					}
					else if (numPagina == 7) {
						//-	Coms E-S Med.: accederᠡl mantenimiento de comisiones por E-S Mediadora.
						mv = new ModelAndView("redirect:/comisionesCultivos.html",parametros);
					}
					break;
				}
				/*case 7:{
					
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);	
					parametros.put("procedencia", "incidenciasComisiones");
					if (null!= ficheroIncidenciaBean.getLinea())
						if (null!=ficheroIncidenciaBean.getLinea().getCodlinea())
							parametros.put("codLineaCom", ficheroIncidenciaBean.getLinea().getCodlinea());
					if (null != ficheroIncidenciaBean.getFichero())
						if (null != ficheroIncidenciaBean.getFichero().getFase())
							if (null!= ficheroIncidenciaBean.getFichero().getFase().getPlan())
								parametros.put("planLineaCom", ficheroIncidenciaBean.getFichero().getFase().getPlan());
					mv = new ModelAndView("redirect:/comisionesCultivos.html",parametros);
					break;
										
				}*/
				case 8:{
					//-	Descuentos: accederᠡl mantenimiento de descuentos.
					parametros = incidenciasComisionesManager.inicializarParametros(ficheroIncidenciaBean);	
					parametros.put("origenLlamada", INCIDENCIAS_COMISIONES);
					
					if (null!= ficheroIncidenciaBean.getSubentidad())
						parametros.put("subentidad", ficheroIncidenciaBean.getSubentidad());
					
					if (null!= ficheroIncidenciaBean.getOficina())
						parametros.put("codOficina", ficheroIncidenciaBean.getOficina());
					
					if (null!= ficheroIncidenciaBean.getLinea())
						if (null!=ficheroIncidenciaBean.getLinea().getCodlinea())
							parametros.put("codLineaCom", ficheroIncidenciaBean.getLinea().getCodlinea());
					if (null != ficheroIncidenciaBean.getFichero())
						if (null != ficheroIncidenciaBean.getFichero().getFase())
							if (null!= ficheroIncidenciaBean.getFichero().getFase().getPlan())
								parametros.put("planLineaCom", ficheroIncidenciaBean.getFichero().getFase().getPlan());
					     
					mv = new ModelAndView("redirect:/mtoDescuentos.run").addAllObjects(parametros);
					//moduloComisiones/mtoDescuentos
					break;
				}
				default:	
					mv = new ModelAndView(MODULO_COMISIONES_INCIDENCIAS,FICHERO_INCIDENCIA_BEAN,ficheroIncidenciaBean).addAllObjects(parametros);
			}			
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		}
		logger.debug("end - doRedirigir");
		return mv;
	}
	
	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, FicheroIncidencia ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doCargar");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		boolean ficheroCargado = false;		
		Fichero fichero = new Fichero();
		
		final SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
		
		String fechaAceptacionFicheroStr = request.getParameter("fechaAceptacionFichero");
		Date fechaAceptacionFichero = sdf.parse(fechaAceptacionFicheroStr);
		
		try {	
			logger.debug("se comprueba si el fichero no es nulo");
			if ((ficheroIncidenciaBean.getFichero() != null) || (ficheroIncidenciaBean.getFichero().getId() != null)){
				
				fichero = incidenciasComisionesManager.getFicheroIncidencias(ficheroIncidenciaBean.getFichero().getId().toString());
				
				if (fichero != null){
						
					logger.debug("se acepta el fichero siempre que no tenga ninguna incidencia de tipo erroneo");
					ficheroCargado = incidenciasComisionesManager.cargarFichero(fichero, fechaAceptacionFichero);
					if (ficheroCargado){
						parametros.put("mensaje", bundle.getString("mensaje.comisiones.incidencia.cargar.OK"));
					} else {
						parametros.put(ALERTA, bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
					}
	
				} else {
					parametros.put(ALERTA, bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
				}					
						
			} else {
				parametros.put(ALERTA, bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
			}			
			
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		} catch (BusinessException be) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = doConsulta(request, response, new FicheroIncidencia());
		}
			
		logger.debug("end - doCargar");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doVerificar(HttpServletRequest request, HttpServletResponse response, FicheroIncidencia ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doVerificar");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		Fichero fichero = new Fichero(); 
			
		try {	
			if ((ficheroIncidenciaBean.getFichero() != null) || (ficheroIncidenciaBean.getFichero().getId() != null)){				
				
				fichero = incidenciasComisionesManager.getFicheroIncidencias(ficheroIncidenciaBean.getFichero().getId().toString());
				
				if (fichero != null){
					incidenciasComisionesManager.verificarTodos(fichero);
					parametros.put("mensaje", bundle.getString("mensaje.comisiones.incidencia.cargar.Verificado"));
				} else {
					parametros.put(ALERTA, bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
				}
			} else {
				parametros.put(ALERTA, bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
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
	
	public ModelAndView doImprimir(HttpServletRequest request, HttpServletResponse response, FicheroIncidencia ficheroIncidenciaBean) throws Exception{
		List<FicheroIncidencia> listIncidencias = null;
		
		logger.debug(RECUPERAMOS_EL_LISTADO_DE_INCIDENCIAS_DEL_FICHERO_Y_SU_ESTADO);		
		listIncidencias = incidenciasComisionesManager.getListFicherosIncidencias(ficheroIncidenciaBean);
		logger.debug("listIncidencias size: " + listIncidencias.size());			
		
		request.setAttribute(LIST_INCIDENCIAS, listIncidencias);
		
		return new ModelAndView("forward:/informes.html?method=doInformeIncidencias");
	}
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. 
     * En MultiActionController no se hace este bind automáticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
    		// True indica que se aceptan fechas vacías
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }

	public void setIncidenciasComisionesManager(IncidenciasComisionesManager incidenciasComisionesManager) {
		this.incidenciasComisionesManager = incidenciasComisionesManager;
	}
}