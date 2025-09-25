package com.rsi.agp.core.webapp.action.mtoinf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposCalculados;

public class MtoCondicionCamposCalculadosController extends	BaseMultiActionController {
	
	/**
	 * Realiza el alta de la condicion del campo calculado
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula la condicion del campo calculado a dar de alta
	 * @return ModelAndView que contiene la redirección a la página de mantenimiento de condiciones de campos
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, CondicionCamposCalculados condicionCamposCalculados) {		
		return new ModelAndView("redirect:/mtoCondicionCampos.run").addObject("condicionCampos", null);
	}
	
	/**
	 * Realiza la modificación de la condicion del campo calculado
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula la condicion del campo calculado a modificar
	 * @return ModelAndView que contiene la redirección a la página de mantenimiento de condiciones de campos
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, CondicionCamposCalculados condicionCamposCalculados) {
		return new ModelAndView("redirect:/mtoCondicionCampos.run").addObject("condicionCampos", null);
	}
	
	/**
	 * Realiza la baja de la condicion del campo calculado
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula la condicion del campo calculado a dar de baja
	 * @return ModelAndView que contiene la redireccion a la página de mantenimiento de condiciones de campos
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, CondicionCamposCalculados condicionCamposCalculados) {
		return new ModelAndView("redirect:/mtoCondicionCampos.run").addObject("condicionCampos", null);
	}
}