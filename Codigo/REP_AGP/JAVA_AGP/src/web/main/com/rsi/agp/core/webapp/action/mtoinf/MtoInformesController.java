package com.rsi.agp.core.webapp.action.mtoinf;

import static com.rsi.agp.core.webapp.util.StringUtils.asListBigDecimal;
import static com.rsi.agp.core.webapp.util.StringUtils.isNullOrEmpty;
import static com.rsi.agp.core.webapp.util.StringUtils.nullToString;
import static com.rsi.agp.core.webapp.util.StringUtils.toValoresSeparadosXComas;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Informe;


public class MtoInformesController extends GenericInformeMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoInformesController.class);
	private IMtoInformeService mtoInformeService;
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/**
	 * Realiza la consulta de informes que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que contiene la redireccion a la pagina de mantenimiento de informes
	 * @throws Exception 
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Informe informeBean) throws Exception {
		logger.debug("init - MtoInformesController - doConsulta");
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Comprueba que el usuario tiene permiso para acceder a este modulo
		if (!checkPermisoDisenador(request)) return devolverError();
		
		// Obtiene el objeto Usuario asociado al usuario conectado
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
    	
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	if ("".equals(origenLlamada)){
    		origenLlamada = (String)request.getAttribute("origenLlamada");
    	}
    	
    	// Obtiene la lista de codigos (de usuarios o entidades) indicados a traves de las lupas
    	String cadenaCodigosLupas = isNullOrEmpty(request.getParameter("cadenaCodigosLupas")) ? null : request.getParameter("cadenaCodigosLupas");
    	
    	
    	String idInforme = request.getParameter("idInforme");
    	if (StringUtils.nullToString(idInforme).equals("")){ 
			idInforme = (String)request.getAttribute("idInforme");
		}
    	String  recogerInformeSesion =  StringUtils.nullToString(request.getParameter("recogerInformeSesion"));
    	// Si se ha pulsado 'Volver', se obtiene el objeto de busqueda de la sesion
		if ("true".equals(recogerInformeSesion)){
			informeBean = (Informe) request.getSession().getAttribute("informeBean");
			request.getSession().removeAttribute("informeBean");
			cadenaCodigosLupas =(String) request.getSession().getAttribute("cadenaCodigosLupasAux");
			cargarUsuariosEntidades(informeBean, cadenaCodigosLupas,usuario);
			request.getSession().removeAttribute("cadenaCodigosLupasAux");
		}
		if (informeBean.getId() != null){
			parameters.put("idInforme", informeBean.getId().toString());
			
		}
    	
		Informe informeBusqueda = (Informe) informeBean;
    	
    	// ---------------------------------------------------------------------------------
    	// -- Busqueda de informes y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
    	logger.debug("Comienza la busqueda Informes");    	
    	
    	html = mtoInformeService.getTablaInformes(request, response, informeBusqueda, origenLlamada, cadenaCodigosLupas, usuario);
        
		if (html == null) {
			return null; // an export
		} else {
			String ajax = request.getParameter("ajax");
			// Llamada desde ajax
			if (ajax != null && ajax.equals("true")) {
				byte[] contents = html.getBytes("UTF-8");
				response.getOutputStream().write(contents);
				return null;
			} else {
				// Pasa a la jsp el codigo de la tabla a traves de este atributo
				request.setAttribute("mtoInforme", html);
			}
		}
    	
    	// ----------------------------------------------------------
    	// -- Carga del mapa de parametros que se enviara a la jsp --
        // ----------------------------------------------------------
    	
    	cargarParametros(request, parameters, usuario,informeBusqueda);
    	
    	String entidadesBox="";
		for (Entidad e :informeBean.getEntidades()){
    		entidadesBox += e.getCodentidad()+"#";
    		parameters.put("entidadesBox", entidadesBox);
		}
		String usuariosBox="";
		for (Usuario u :informeBean.getUsuarios()){
    		usuariosBox += u.getCodusuario()+"#";
    		parameters.put("usuariosBox", usuariosBox);
		}
    	
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
    	informeBean.setFechaAlta(null);
       	mv = new ModelAndView(successView, "informeBean",informeBean);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - MtoInformesController - doConsulta");
    	return mv;     	    
	}

	/**
	 * Carga en el mapa los parametros que se van a enviar a la jsp
	 * @param request
	 * @param parameters
	 */
	private void cargarParametros(HttpServletRequest request, final Map<String, Object> parameters, 
			final Usuario usuario,Informe informeBean) {
		// --- carga de la lista de formatos de informe
    	List<FormatoCampoGenerico> lstFormatosInforme = mtoInformeService.getFormatosInforme();
    	parameters.put("lstFormatosInforme", lstFormatosInforme);
    	parameters.put("codFormatoPDF", ConstantsInf.COD_FORMATO_PDF);
    	parameters.put("codOrientacionV", ConstantsInf.COD_ORIENTACION_VERTICAL);
    	
    	// --- carga de la lista de orientaciones del informe
    	List<FormatoCampoGenerico> lstOrientacionesInforme = mtoInformeService.getOrientacionesInforme();
    	parameters.put("lstOrientacionesInforme", lstOrientacionesInforme);
    	
		// Carga de codigos para el campo 'Cuenta'
		parameters.put("codCuentaNo", ConstantsInf.COD_CUENTA_NO);
		parameters.put("codCuentaSi", ConstantsInf.COD_CUENTA_SI);
		
		// Carga de codigos para el campo 'Visibilidad'
		parameters.put("vsbTodos", ConstantsInf.COD_VISIBILIDAD_TODOS);
		parameters.put("vsbPerfil", ConstantsInf.COD_VISIBILIDAD_PERFIL);
		parameters.put("vsbUsuarios", ConstantsInf.COD_VISIBILIDAD_USUARIOS);
		parameters.put("vsbEntidades", ConstantsInf.COD_VISIBILIDAD_ENTIDADES_SI);
		
		// Si el usuario es perfil 5 se pasa a la jsp la lista de entidades pertenecientes a su grupo de entidades
		parameters.put("grupoEntidades", Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil()) 
											||  Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(usuario.getPerfil())
										? toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false) 
										: "");
		
		// Si el usuario es perfil 1 o 5 se pasa a la jsp la entidad para mostrarla en el campo entidad(es)
		if (Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(usuario.getPerfil())){
			parameters.put("entidadAux",usuario.getOficina().getEntidad().getCodentidad());
		}
		if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())){
			parameters.put("entidadAux",usuario.getOficina().getEntidad().getCodentidad());
		}
		// Si el usuario es perfil 1 se pasa a la jsp la entidad a la que pertenece
		parameters.put("entidad", Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(usuario.getPerfil())
										  ? usuario.getOficina().getEntidad().getCodentidad()
										  : "");
		
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
        parameters.put("perfil", usuario.getPerfil().substring(4));
        
        
        
	}
	
	/**
	 * Realiza el alta del informe
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula el informe a dar de alta
	 * @return ModelAndView que contiene la redireccion a la pagina de mantenimiento de informes
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Informe informeBean) throws Exception{
		logger.debug("init - MtoInformesController - doAlta");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String redireccion = nullToString(request.getParameter("redireccion"));
		String cadenaCodigosLupas = request.getParameter("cadenaCodigosLupas");
		try{	
			if (informeBean != null){
				// Carga en el informe la lista de entidades o usuarios si se ha seleccionado la visibilidad correspondiente
				cargarUsuariosEntidades(informeBean, cadenaCodigosLupas,usuario);
				
				// Anhado el usuario de la session al informe
				informeBean.setUsuario(usuario);
				// Se informa de la fecha de alta del informe
				informeBean.setFechaAlta(new Date());
				// Se establece el campo oculto
				informeBean.setOculto(new BigDecimal(0));
				// Se inserta el informe en BD
				parameters = mtoInformeService.altaInforme(informeBean);
				// Si tiene id, el alta ha sido correcta
				if (informeBean.getId() != null){
					parameters.put("idInforme", informeBean.getId().toString());
					parameters.put("nombre", informeBean.getNombre());
				}
				// Si informe no tiene id es que no se ha hecho el alta
				else {
					// Se borra 'redireccion' para que se vuelva al listado de informes y se muestre el aviso del fallo en el alta
					redireccion = "";
				}
				
			}
			if (!redireccion.equals("")){
				parameters.put("mensaje","");
				parameters.put("alerta","");
				parameters.put("origenLlamada","informe");
			}
			if (redireccion.equals("datosInformes")){
				request.getSession().setAttribute("informeBean", informeBean);
				request.getSession().setAttribute("cadenaCodigosLupasAux", cadenaCodigosLupas);
				mv = new ModelAndView("redirect://mtoDatosInforme.run").addAllObjects(parameters);
			}else if (redireccion.equals("condiciones")){
				request.getSession().setAttribute("informeBean", informeBean);
				request.getSession().setAttribute("cadenaCodigosLupasAux", cadenaCodigosLupas);
				mv = new ModelAndView("redirect:/mtoCondicionCampos.run").addAllObjects(parameters);
			}else if (redireccion.equals("clasifYRuptura")){
				request.getSession().setAttribute("informeBean", informeBean);
				request.getSession().setAttribute("cadenaCodigosLupasAux", cadenaCodigosLupas);
				mv = new ModelAndView("redirect:/mtoClasificacionRuptura.run").addAllObjects(parameters);
			}else {
				logger.debug("end - MtoInformesController - doAlta");
				request.setAttribute("origenLlamada", "menuGeneral");
				return doConsulta(request, response, informeBean).addAllObjects(parameters);  
			}		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_ALTA_KO));
				request.setAttribute("origenLlamada", "menuGeneral");
				mv = new ModelAndView("redirect://mtoInformes.run").addAllObjects(parameters);
		}
		logger.debug("end - MtoInformesController - doAlta");
		return mv; 		
	}

	
	/**
	 * Realiza la modificacion del informe
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula el informe a modificar
	 * @return ModelAndView que contiene la redireccion a la pagina de mantenimiento de informes
	 * @throws Exception 
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, Informe informeBean) throws Exception {
		logger.debug("init - MtoInformesController - doEdita");
		Map<String, Object> parameters = new HashMap<String, Object>();
		String redireccion = nullToString(request.getParameter("redireccion"));
		String cadenaCodigosLupas = request.getParameter("cadenaCodigosLupas");
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		ModelAndView mv= null;
		try{
			
			if (!nullToString(request.getParameter("idInforme")).equals("")){

				informeBean.setId(Long.parseLong(request.getParameter("idInforme")));
				// Carga en el informe la lista de entidades o usuarios si se ha seleccionado la visibilidad correspondiente
				cargarUsuariosEntidades(informeBean, cadenaCodigosLupas,usuario);
				// nos aseguramos de que se establece el campo oculto
				informeBean.setOculto(new BigDecimal(0));
				// anhado el usuario de la sesion al informe
				if (redireccion.equals(""))
					parameters = mtoInformeService.editarInforme(informeBean);
				parameters.put("cadenaCodigosLupas", cadenaCodigosLupas);
				parameters.put("idInforme", informeBean.getId());
				parameters.put("nombre", informeBean.getNombre());
			} else{
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_MODIF_KO));
			}
			
			if (!redireccion.equals("")){
				parameters.put("mensaje","");
				parameters.put("alerta","");
				parameters.put("origenLlamada","informe");
			}
			if (redireccion.equals("datosInformes")){
				DatoInformes datoInforme = new DatoInformes();
				datoInforme.setInforme(informeBean);
				request.getSession().setAttribute("informeBean", informeBean);
				request.getSession().setAttribute("cadenaCodigosLupasAux", cadenaCodigosLupas);
				mv = new ModelAndView(new RedirectView("mtoDatosInforme.run"),"datoInformesBean", datoInforme).addAllObjects(parameters); //al doEdita
			}else if (redireccion.equals("condiciones")){
				request.getSession().setAttribute("informeBean", informeBean);
				request.getSession().setAttribute("cadenaCodigosLupasAux", cadenaCodigosLupas);
				mv = new ModelAndView("redirect:/mtoCondicionCampos.run").addAllObjects(parameters);
			}else if (redireccion.equals("clasifYRuptura")){
				request.getSession().setAttribute("informeBean", informeBean);
				request.getSession().setAttribute("cadenaCodigosLupasAux", cadenaCodigosLupas);
				mv = new ModelAndView("redirect:/mtoClasificacionRuptura.run").addAllObjects(parameters);
			}else {
				logger.debug("end - MtoInformesController - doEdita");
				request.setAttribute("origenLlamada", "menuGeneral");
				request.setAttribute("cadenaCodigosLupas", cadenaCodigosLupas);
				return doConsulta(request, response, informeBean).addAllObjects(parameters);  
			}	
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_MODIF_KO));
			request.setAttribute("origenLlamada", "menuGeneral");
			
			mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(parameters);
		}
		logger.debug("end - MtoInformesController - doEdita");
		return mv; 	
	}
	
	/**
	 * Realiza la baja del informe
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula el informe a dar de baja
	 * @return ModelAndView que contiene la redireccion a la pagina de mantenimiento de informes
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, Informe informeBean) throws Exception{
		logger.debug("init - MtoInformesController - doBaja");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;

		try{
			String idInforme = (String) request.getParameter("idInforme");
			Long idInformeLong = Long.parseLong(idInforme);
			informeBean = mtoInformeService.getInforme(idInformeLong);
			String cadenaUsuarios = request.getParameter("cadenaUsuarios");
			parameters.put("cadenaUsuarios", cadenaUsuarios);
			if (informeBean != null){
				mtoInformeService.bajaInforme(informeBean);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_INFORME_BAJA_OK));
			}else{
				logger.error("El informe con idInforme: " + idInforme + " no existe en BBDD");
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_BAJA_KO));
			}
			parameters.put("origenLlamada", "sesion");
			mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_BAJA_KO));		
				mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(parameters);
		}
		logger.debug("end - MtoInformesController - doBaja");
		return mv;     
	}
	
	/**
	 * Carga en el informe la lista de entidades o usuarios si se ha seleccionado la visibilidad correspondiente
	 * @param informeBean
	 * @param cadenaCodigosLupas
	 */
	private void cargarUsuariosEntidades(Informe informeBean, String cadenaCodigosLupas, Usuario usuario) {
		
		
		if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())){
			informeBean.setVisibilidadEnt(new BigDecimal (ConstantsInf.COD_VISIBILIDAD_ENTIDADES_SI));
		}
		// Si se ha seleccionado visibilidad por usuarios se carga el informe con la lista correspondiente
		if (new BigDecimal (ConstantsInf.COD_VISIBILIDAD_USUARIOS).equals(informeBean.getVisibilidad()) 
				&& !isNullOrEmpty(cadenaCodigosLupas)) {
			Set<Usuario> setUsu = new HashSet<Usuario>();
			// Recorre el listado de codigos obtenidos, crea el objeto usuario correspondiente y lo inserta en el set
			for (String codigo : (Arrays.asList(cadenaCodigosLupas.split(ConstantsInf.CHR_SEPARADOR_LUPAS)))) {
				Usuario usu = new Usuario();
				usu.setCodusuario(codigo);
				setUsu.add(usu);
			}
			// Anhade el set de usuarios al informe
			informeBean.setUsuarios(setUsu);
		}
		// Si se ha seleccionado visibilidad por entidades se carga el informe con la lista correspondiente
		else if (new BigDecimal (ConstantsInf.COD_VISIBILIDAD_ENTIDADES_SI).equals(informeBean.getVisibilidadEnt())
				&& !isNullOrEmpty(cadenaCodigosLupas)) {
			Set<Entidad> setEnt = new HashSet<Entidad>();
			// Recorre el listado de codigos obtenidos, crea el objeto usuario correspondiente y lo inserta en el set
			for (BigDecimal codigo : (asListBigDecimal(cadenaCodigosLupas, ConstantsInf.CHR_SEPARADOR_LUPAS))) {
				Entidad ent = new Entidad();
				ent.setCodentidad(codigo);
				setEnt.add(ent);
			}
			// Anhade el set de usuarios al informe
			informeBean.setEntidades(setEnt);
		}
	}
	
	public ModelAndView doDuplicar(HttpServletRequest request, HttpServletResponse response, Informe informeBean) throws Exception {
		
		
		logger.debug("init - MtoInformesController - doDuplicar");
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv= null;
		Informe informeduplicado = new Informe();
		try{
			String idInforme = (String) request.getParameter("idInforme");
			Long idInformeLong = Long.parseLong(idInforme);
			String tituloInfoDuplicado = (String) request.getParameter("tituloInfoDuplicado");
			String cadenaCodigosLupas = request.getParameter("cadenaCodigosLupas");
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			informeBean = mtoInformeService.getInforme(idInformeLong);
			
			if (informeBean != null){
				// Carga en el informe la lista de entidades o usuarios si se ha seleccionado la visibilidad correspondiente
				cargarUsuariosEntidades(informeBean, cadenaCodigosLupas,usuario);
				
				params = mtoInformeService.duplicarInforme(informeBean,tituloInfoDuplicado);
				// si ya existe un informe con ese nombre error
				if (params.containsKey("alerta")){
					return doConsulta(request, response, new Informe()).addAllObjects(params);  
				}else{
					
					informeduplicado = (Informe) params.get("informeduplicado");
					params.put("idInforme", informeduplicado.getId());
					params.put("mensaje", bundle.getObject(ConstantsInf.ALERTA_INFORME_DUPLICAR_OK));
					request.setAttribute("origenLlamada", "menuGeneral");
					return doConsulta(request, response, informeduplicado).addAllObjects(params);  
				}
				
			}else{
				logger.error("El informe con idInforme: " + idInforme + " no existe en BBDD");
				params.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_DUPLICAR_KO));
			}
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			params.put("alerta", bundle.getObject(ConstantsInf.ALERTA_INFORME_DUPLICAR_KO));		
				mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(params);
		}
		logger.debug("end - MtoInformesController - doDuplicar");
		return mv;     
	}
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automaticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacías
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }
	
	/**
	 * Set de propiedad para Spring
	 * @param mtoInformeService
	 */
	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}

	/**
	 * Set de propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
}
