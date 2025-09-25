package com.rsi.agp.core.webapp.action.mtoinf;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.dao.tables.commons.Usuario;

public class GenericInformeMultiActionController extends BaseMultiActionController {
	
	/**
	 * Comprueba si el usuario tiene permiso para acceder al módulo de diseño
	 * @param request
	 * @return
	 */
	protected boolean checkPermisoDisenador (HttpServletRequest request) {
		
		// Obtiene el usuario de sesión
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Comprueba que el usuario tiene acceso al módulo de diseño
		if (usuario != null && usuario.getPermisosInformes()!= null) return usuario.getPermisosInformes().isAccesoDisenador();
		
		logger.debug("GenericInformeMultiActionController.checkPermisoDisenador - No se ha podido acceder a los permisos del usuario");
		logger.debug("GenericInformeMultiActionController.checkPermisoDisenador - Se permite el acceso al modulo de diseño de informes");
		
		return true;
	}
	
	/**
	 * Comprueba si el usuario tiene permiso para acceder al módulo de generación
	 * @param request
	 * @return
	 */
	protected boolean checkPermisoGenerador (HttpServletRequest request) {
		
		// Obtiene el usuario de sesión
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Comprueba que el usuario tiene acceso al módulo de diseño
		if (usuario != null && usuario.getPermisosInformes()!= null) return usuario.getPermisosInformes().isAccesoGenerador();
		
		logger.debug("GenericInformeMultiActionController.checkPermisoGenerador - No se ha podido acceder a los permisos del usuario");
		logger.debug("GenericInformeMultiActionController.checkPermisoGenerador - Se permite el acceso al modulo de generación de informes");
		
		return true;
	}
	
	/**
	 * Redirige a la pantalla de login
	 * @return
	 */
	protected ModelAndView devolverError () {

		logger.debug("El usuario no tiene permisos para acceder al modulo indicado");
		return HTMLUtils.errorMessage("GenericInformeMultiActionController","Acceso denegado");
	}

}
