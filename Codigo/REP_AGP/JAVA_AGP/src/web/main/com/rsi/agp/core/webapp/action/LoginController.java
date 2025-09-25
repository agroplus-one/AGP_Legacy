package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.CargaAseguradoManager;
import com.rsi.agp.core.managers.impl.CheckStatusManager;
import com.rsi.agp.core.managers.impl.EntidadManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.UserManager;
import com.rsi.agp.core.model.user.User;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.SecureRequest;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Medida;


public class LoginController extends BaseSimpleController implements Controller
{
	private UserManager userManager;
	private CargaAseguradoManager cargaAseguradoManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private EntidadManager entidadManager;
	private CheckStatusManager checkStatusManager;
	
	private static final String STATUS_CHECK_URL = "/checkStatus.html";

	public LoginController ()
	{
		setCommandClass(String.class);
		setCommandName("string");
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception 
	{
		String OP = SecureRequest.secureString(request.getParameter("OP"), false, true, 50);
		try{
			if (OP.equals("login")) {
				return login(request, response, command, errors);
			} else if (OP.equals("logout")) {
				return logout(request, response);
			} else if (OP.equals("taller")) {
				return menuTaller(request, response);
			} else if (OP.equals("ppal")) {
				return menuPrincipal(request, response);
			} else if (OP.equals("ajax_getEncodedUser")){ 
				ajax_getEncodedUser(request, response);
				return null;
			} else {
				boolean isDbCheck = request.getRequestURI().toString().contains(STATUS_CHECK_URL);
				return isDbCheck ? checkStatus() : HTMLUtils.errorMessage("loginController",HTMLUtils.ERR_OP_MSG);
			}
			
		}catch (Exception e){
			logger.error("Excepcion : LoginController - handle", e);
			return HTMLUtils.errorMessage("loginController",e.getMessage());
		}
	}
	
	private ModelAndView checkStatus(){
		return new ModelAndView("checkStatus", "status", checkStatusManager.checkStatusBBDD());
	}
	
	private ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> myModel = new HashMap<String, Object>();	
		myModel.put("textoMensaje", "SESION FINALIZADA DE FORMA MANUAL");
		return new ModelAndView("sessionOut", "result", myModel);
	}
	
	private ModelAndView login(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws BusinessException, NumberFormatException, HibernateException, SQLException {
		Map<String, Object> myModel = new HashMap<String, Object>();	
		Medida medida = new Medida();
		User user = new User();
		user.setIpUsuario(request.getRemoteAddr());
		Usuario usuario = new Usuario();
		
		//Obtenemos el usuario del contexto de Spring
		SecurityContextImpl context = (SecurityContextImpl) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		org.springframework.security.userdetails.User ctxUser = 
			(org.springframework.security.userdetails.User) context.getAuthentication().getPrincipal();
		// Si el codigo del usuario del contexto de Spring es vacio, se comprueba si en la cabecera de la peticion HTTP viene el usuario
		// (acceso identificado) o es un error de login
		if (ctxUser.getUsername().trim().equals("")){
			return gestionAccesoIdentificado(request, response);
		} 
		// Si viene relleno, se continua con el login
		else {
			user.setIdUsuario(ctxUser.getUsername());
			usuario = this.userManager.login(user,false);
		}	
		
		
		//comprueba si la aplicacion esta abierta, en caso de que no lo esté, se comprueba si el
		//usuario introducido tiene privilegios de login.
		boolean permisoLogin = this.userManager.checkPermisosLogin(usuario);
		if (!permisoLogin){
			myModel.put("textoMensaje", "REALIZANDO TAREAS DE MANTENIMIENTO, DISCULPEN LAS MOLESTIAS");
			return new ModelAndView("error", "result", myModel);
		}
		
		// Si el usuario no es perfil 0 se cargan sus permisos para el acceso al diseñador y al generador de informes
		if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil())) userManager.cargaPermisosPerfiles(usuario);
		
		// Si el usuario tiene perfil 5 se carga el listado de codigos de entidades pertenecientes a su grupo
		
		List <BigDecimal> grupoEntidades = null;
		if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
			String idGrupo = entidadManager.getIdGrupo(usuario.getOficina().getEntidad().getCodentidad());
			grupoEntidades = entidadManager.getListaEntidadesGrupo(idGrupo);
			usuario.setListaCodEntidadesGrupo(grupoEntidades);
		}
		
		List <BigDecimal> listaCodOficinasGrupo = null;
		if(Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil()) ){
			
			/* Pet. 63701 ** MODIF TAM (03/06/2021) ** Inicio */
			/* Habra que incluir una validacion para comprobar que la zona del usuario recuperada */
			// 1� Hemos recuperado la zona del usuario del modulo de seguridad
			BigDecimal codzona = usuario.getCodzona();
			BigDecimal codentidad = usuario.getOficina().getEntidad().getCodentidad();
			if(codzona == null){
				logger.info("ATENCION: La oficina de este usuario no esta asignada a ningun grupo");
			}else {
				//	Nos traemos de la tabla tb_oficinas_zonas todas las oficinas que pertenezcan a esa zona y esa entidad.
				listaCodOficinasGrupo=entidadManager.getListaOficinasGrupo(codzona, codentidad);				
			}
			
			//	Asignar los valores devueltos al campo ListaCodOficinasGrupo del usuario.
			usuario.setListaCodOficinasGrupo(listaCodOficinasGrupo);
			//	Metemos el usuario en sesion. (Esto ya se hace).
		}

		request.getSession().setAttribute("usuario", usuario);
		
		if(usuario.getColectivo() != null){
			Long lineaseguroid = usuario.getColectivo().getLinea().getLineaseguroid();
			if(usuario.getAsegurado() != null){
				String nifcif = usuario.getAsegurado().getNifcif();
				medida = this.userManager.getMedida(lineaseguroid, nifcif);
				if (medida == null)
					medida = new Medida();
				
				//compruebo si el asegurado es nuevo ( no se encuentra en AAC)
				Integer countAseg;
				if (usuario.getColectivo().getLinea().isLineaGanado()) {
					countAseg = cargaAseguradoManager.getCountAseguradoAutorizadoG(nifcif, lineaseguroid);
				} else {
					countAseg = cargaAseguradoManager.getCountAseguradoAutorizado(nifcif, lineaseguroid);
				}
				String aseguradoNuevo = ((countAseg != null && countAseg > 0) ? "" : "Nuevo Asegurado");
				request.getSession().setAttribute("aseguradoNuevo", aseguradoNuevo);
				
				// 09/10/2012 AMG //calculamos el intervalo de CoefReduccRdto
				if (usuario.getClase() != null){
					String intervaloCoefReduccionRdtoStr = seleccionPolizaManager.calcularIntervaloCoefReduccionRdtoPoliza(usuario,usuario.getClase().getId());
					request.getSession().setAttribute("intervaloCoefReduccionRdto", intervaloCoefReduccionRdtoStr);
 				}
				// FIN 09/10/2012 AMG
			}
		}
		request.getSession().setAttribute("medida", medida);
		return new ModelAndView("menuPrincipal", "result", myModel);
		
	}

	/**
	 * Comprueba si el usuario viene en la cabecera de la peticion HTTP (acceso identificado) para continuar con el login o es un error
	 * de acceso
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView gestionAccesoIdentificado(HttpServletRequest request,	HttpServletResponse response) {
		
		Map<String, Object> myModel = new HashMap<String, Object>();
		String nombreUsuario;
		
		// Recogemos el nombre que se usara para recoger el usuario
		nombreUsuario = userManager.getNombreUsuario();
		logger.debug("## NOMBRE_USUARIO para buscar en la cabecera HTTP: " + nombreUsuario + " ## ");
		// Recogemos el usuario de la cabecera HTTP
		String userNew = StringUtils.nullToString(request.getHeader(nombreUsuario));
		logger.debug("## usuario HTTP: " + userNew + " ## ");

		// El usuario no viene en la cabecera HTTP -> Error de login
		if (userNew.equals("")){ 
			// Redirigimos a pantalla de No acceso
			logger.debug("Redirigiendo a pantalla de No acceso a Agroplus");
			myModel.put("textoMensaje", "El Usuario no tiene acceso a Agroplus");
			return new ModelAndView("error", "result", myModel);
		} 
		// El usuario si viene en la cabecera HTTP -> Se continua con el login
		else { 
			// Se redirige de nuevo al modulo de acceso de Spring con el usuario recibido en la cabecera de la peticion
			// para realizar el login normal
			try {
				response.sendRedirect("j_spring_security_check?j_username=" + userNew + "&op=login");
			} catch (IOException e) {
				logger.debug("Error en la redireccion al acceso identificado de Spring", e);
				
				// Redirigimos a pantalla de No acceso
				logger.debug("Redirigiendo a pantalla de No acceso a Agroplus");
				myModel.put("textoMensaje", "El Usuario no tiene acceso a Agroplus");
				return new ModelAndView("error", "result", myModel);
			}
			return null;
			
		}
	}
	
	public ModelAndView menuTaller(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> myModel = new HashMap<String, Object>();

		return new ModelAndView("moduloTaller/menuTaller", "result", myModel);
	}
	
	public ModelAndView menuPrincipal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> myModel = new HashMap<String, Object>();

		if (!checkLogged(request)) {
			return new ModelAndView("error");
		}

		return new ModelAndView("menuPrincipal", "result", myModel);
	}
	
	private void ajax_getEncodedUser(HttpServletRequest request, HttpServletResponse response){
    	try{
    		String encodedUser = this.userManager.encodeUser(request.getParameter("p_usuario"));
    		response.getWriter().write(encodedUser);
    	}
    	catch(Exception excepcion){
    		logger.error(excepcion);
    	}
    }

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public void setCargaAseguradoManager(CargaAseguradoManager cargaAseguradoManager) {
		this.cargaAseguradoManager = cargaAseguradoManager;
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setEntidadManager(EntidadManager entidadManager) {
		this.entidadManager = entidadManager;
	}
	
	public void setCheckStatusManager(CheckStatusManager checkStatusManager) {
		this.checkStatusManager = checkStatusManager;
	}
}

