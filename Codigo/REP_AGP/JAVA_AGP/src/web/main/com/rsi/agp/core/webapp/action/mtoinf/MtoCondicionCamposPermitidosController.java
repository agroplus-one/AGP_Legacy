package com.rsi.agp.core.webapp.action.mtoinf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposPermitidos;

public class MtoCondicionCamposPermitidosController extends BaseMultiActionController {

	/**
	 * Realiza el alta de la condicion del campo permitido
	 * 
	 * @param request
	 * @param response
	 * @param informeBean
	 *            Objeto que encapsula la condicion del campo permitido a dar de
	 *            alta
	 * @return ModelAndView que contiene la redireccion a la pagina de
	 *         mantenimiento de condiciones de campos
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response,
			CondicionCamposPermitidos condicionCamposPermitidos) {
		return new ModelAndView("redirect:/mtoCondicionCampos.run").addObject("condicionCampos", null);
	}

	/**
	 * Realiza la modificacion de la condicion del campo permitido
	 * 
	 * @param request
	 * @param response
	 * @param informeBean
	 *            Objeto que encapsula la condicion del campo permitido a modificar
	 * @return ModelAndView que contiene la redireccion a la pagina de
	 *         mantenimiento de condiciones de campos
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response,
			CondicionCamposPermitidos condicionCamposPermitidos) {
		return new ModelAndView("redirect:/mtoCondicionCampos.run").addObject("condicionCampos", null);
	}

	/**
	 * Realiza la baja de la condicion del campo permitido
	 * 
	 * @param request
	 * @param response
	 * @param informeBean
	 *            Objeto que encapsula la condicion del campo permitido a dar de
	 *            baja
	 * @return ModelAndView que contiene la redireccion a la pagina de
	 *         mantenimiento de condiciones de campos
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response,
			CondicionCamposPermitidos condicionCamposPermitidos) {
		return new ModelAndView("redirect:/mtoCondicionCampos.run").addObject("condicionCampos", null);
	}
}