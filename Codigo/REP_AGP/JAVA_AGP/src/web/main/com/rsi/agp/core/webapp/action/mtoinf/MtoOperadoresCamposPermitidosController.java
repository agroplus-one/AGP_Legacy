package com.rsi.agp.core.webapp.action.mtoinf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadoresCamposPermitidosService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;

public class MtoOperadoresCamposPermitidosController extends BaseMultiActionController {
	
	private IMtoOperadoresCamposPermitidosService mtoOperadoresCamposPermitidosService;
	private Log logger = LogFactory.getLog(MtoOperadoresCamposPermitidosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/**
	 * Realiza el alta del operador de campos permitidos
	 * @param request
	 * @param response
	 * @param operadorCamposPermitidosBean Objeto que encapsula el operador de campos permitidos a dar de alta
	 * @return 
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, OperadorCamposPermitido opCamPerBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			String idOperador = StringUtils.nullToString(request.getParameter("idOperador"));
			String idVistaCampo = StringUtils.nullToString(request.getParameter("idVistaC"));
			
			//recoger filtro
			String tablaOrigenFiltro = StringUtils.nullToString(request.getParameter("tablaOrigenFiltroP"));
			String campoFiltro = StringUtils.nullToString(request.getParameter("campoFiltroP"));
			String operadorFiltro = StringUtils.nullToString(request.getParameter("operadorFiltroP"));
			
			if (!idOperador.equals("")){
				opCamPerBean.setIdoperador(new BigDecimal(idOperador));
			}
			parameters = mtoOperadoresCamposPermitidosService.altaOperadorCamposPermitidos(opCamPerBean, idVistaCampo);
			// pasamos el filtro por parámetros
			parameters.put("tablaOrigen", tablaOrigenFiltro);
			parameters.put("campo", campoFiltro);
			parameters.put("operador", operadorFiltro);
			parameters.put("idVistaC", idVistaCampo);
			parameters.put("isCalcOPermMain3", "2");
			if (opCamPerBean.getId() != null){
				parameters.put("idOpCampoPermitido", opCamPerBean.getId());
			}
			parameters.put("origenLlamada", "menuGeneral");
			return new ModelAndView("redirect:/mtoOperadoresCampos.run").addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_ALTA_KO));
			parameters.put("origenLlamada", "menuGeneral");
			return new ModelAndView("redirect:/mtoOperadoresCampos.run").addAllObjects(parameters);
		}
	}
	
	/**
	 * Realiza la modificación del operador de campos permitidos
	 * @param request
	 * @param response
	 * @param operadorCamposPermitidosBean Objeto que encapsula el operador de campos permitidos a modificar
	 * @return 
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, OperadorCamposPermitido operadorCamposPermitidosBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String idOperador = StringUtils.nullToString(request.getParameter("idOperador"));
		String idOpCampoPermitido = StringUtils.nullToString(request.getParameter("idOpCamposPermitido"));
		String idVistaCampo = StringUtils.nullToString(request.getParameter("idVistaC"));
		
		//recoger filtro
		String tablaOrigenFiltro = StringUtils.nullToString(request.getParameter("tablaOrigenFiltroP"));
		String campoFiltro = StringUtils.nullToString(request.getParameter("campoFiltroP"));
		String operadorFiltro = StringUtils.nullToString(request.getParameter("operadorFiltroP"));
		
		try {
			if (!StringUtils.nullToString(request.getParameter("idOpCamposPermitido")).equals("")){
				parameters = mtoOperadoresCamposPermitidosService.updateOperadorCamposPermitidos(idOpCampoPermitido, idVistaCampo, idOperador);
			} else{
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_MODIF_KO));
			}
			// pasamos el filtro por parámetros
			parameters.put("tablaOrigen", tablaOrigenFiltro);
			parameters.put("campo", campoFiltro);
			parameters.put("operador", operadorFiltro);
			parameters.put("idVistaC", idVistaCampo);
			parameters.put("isCalcOPermMain3", "2");
			parameters.put("idOpCampoPermitido", idOpCampoPermitido);
			parameters.put("origenLlamada", "menuGeneral");
			return new ModelAndView("redirect:/mtoOperadoresCampos.run").addAllObjects(parameters);
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al editar el Operador Campo Permitido: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_MODIF_KO));
			parameters.put("origenLlamada", "menuGeneral");
			return new ModelAndView("redirect:/mtoOperadoresCampos.run").addAllObjects(parameters);
		}
	}
	
	/**
	 * Realiza la baja del operador de campos permitidos
	 * @param request
	 * @param response
	 * @param operadorCamposPermitidosBean Objeto que encapsula el operador de campos permitidos a dar de baja
	 * @return 
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, OperadorCamposPermitido operadorCamposPermitidosBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			String idOpCampoPermitido = StringUtils.nullToString(request.getParameter("idOpCamposPermitido"));
			//recoger filtro
			String tablaOrigenFiltro = StringUtils.nullToString(request.getParameter("tablaOrigenFiltroP"));
			String campoFiltro = StringUtils.nullToString(request.getParameter("campoFiltroP"));
			String operadorFiltro = StringUtils.nullToString(request.getParameter("operadorFiltroP"));
			
			operadorCamposPermitidosBean = mtoOperadoresCamposPermitidosService.getOperadorCamposPermitidos(Long.parseLong(idOpCampoPermitido));
			if (operadorCamposPermitidosBean != null){
				parameters = mtoOperadoresCamposPermitidosService.bajaOperadorCamposPermitidos(operadorCamposPermitidosBean);
			}else{
				logger.error("El operadorCamposPermitido con id: " + idOpCampoPermitido + " no existe en BBDD");
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_BAJA_KO));
			}
			// pasamos el filtro por parámetros
			parameters.put("tablaOrigen", tablaOrigenFiltro);
			parameters.put("campo", campoFiltro);
			parameters.put("operador", operadorFiltro);
			return new ModelAndView("redirect://mtoOperadoresCampos.run").addAllObjects(parameters);
		}catch (Exception e) {
			logger.error("Se ha producido un error al dar de baja el Campo permitido. " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_BAJA_KO));		
			return new ModelAndView("redirect://mtoOperadoresCampos.run").addAllObjects(parameters);
		}
	}

	/**
	 * Setter del Service para Spring
	 * @param mtoOperadoresCamposPermitidosService
	 */
	public void setMtoOperadoresCamposPermitidosService(IMtoOperadoresCamposPermitidosService mtoOperadoresCamposPermitidosService) {
		this.mtoOperadoresCamposPermitidosService = mtoOperadoresCamposPermitidosService;
	}
	
}
