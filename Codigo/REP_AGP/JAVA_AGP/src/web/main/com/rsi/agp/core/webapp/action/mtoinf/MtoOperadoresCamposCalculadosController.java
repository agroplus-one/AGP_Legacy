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
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadoresCamposCalculadosService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;

public class MtoOperadoresCamposCalculadosController extends BaseMultiActionController {
	
	private IMtoOperadoresCamposCalculadosService mtoOperadoresCamposCalculadosService;
	private Log logger = LogFactory.getLog(MtoOperadoresCamposCalculadosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/**
	 * Realiza el alta del operador de campos calculados
	 * @param request
	 * @param response
	 * @param operadorCamposCalculadosBean Objeto que encapsula el operador de campos calculados a dar de alta
	 * @return 
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, OperadorCamposCalculados opCamCalcBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			String idOperador = StringUtils.nullToString(request.getParameter("idOperador"));
			String idcampoCalc = StringUtils.nullToString(request.getParameter("idcampoCalc"));
			
			//recoger filtro
			String tablaOrigenFiltro = StringUtils.nullToString(request.getParameter("tablaOrigenFiltroC"));
			String campoFiltro = StringUtils.nullToString(request.getParameter("campoFiltroC"));
			String operadorFiltro = StringUtils.nullToString(request.getParameter("operadorFiltroC"));
			
			if (!idOperador.equals("")){
				opCamCalcBean.setIdoperador(new BigDecimal(idOperador));
			}
			parameters = mtoOperadoresCamposCalculadosService.altaOperadorCamposCalculados(opCamCalcBean, idcampoCalc);
			// pasamos el filtro por parámetros
			parameters.put("tablaOrigen", tablaOrigenFiltro);
			parameters.put("campo", campoFiltro);
			parameters.put("operador", operadorFiltro);
			parameters.put("idVistaC", idcampoCalc);
			parameters.put("isCalcOPermMain3", "1");
			parameters.put("idcampoCalc", idcampoCalc);
			if (opCamCalcBean.getId() != null){
				parameters.put("idOpCalculado", opCamCalcBean.getId());
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
	 * Realiza la modificación del operador de campos calculados
	 * @param request
	 * @param response
	 * @param operadorCamposCalculadosBean Objeto que encapsula el operador de campos calculados a modificar
	 * @return 
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, OperadorCamposCalculados operadorCamposCalculadosBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String idOperador = StringUtils.nullToString(request.getParameter("idOperador"));
		String idOpCalculado = StringUtils.nullToString(request.getParameter("idOpCalculado"));
		String idcampoCalc = StringUtils.nullToString(request.getParameter("idcampoCalc"));
		
		//recoger filtro
		String tablaOrigenFiltro = StringUtils.nullToString(request.getParameter("tablaOrigenFiltroC"));
		String campoFiltro = StringUtils.nullToString(request.getParameter("campoFiltroC"));
		String operadorFiltro = StringUtils.nullToString(request.getParameter("operadorFiltroC"));
		
		try {
			if (!idOpCalculado.equals("") && !idOperador.equals("") && !idcampoCalc.equals("")){
				parameters = mtoOperadoresCamposCalculadosService.updateOperadorCampoCalculado(idOpCalculado, idcampoCalc, idOperador);
			} else{
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_MODIF_KO));
			}
			// pasamos el filtro por parámetros
			parameters.put("tablaOrigen", tablaOrigenFiltro);
			parameters.put("campo", campoFiltro);
			parameters.put("operador", operadorFiltro);
			parameters.put("idVistaC", idcampoCalc);
			parameters.put("isCalcOPermMain3", "1");
			parameters.put("idcampoCalc", idcampoCalc);
			parameters.put("idOpCalculado", idOpCalculado);
			parameters.put("origenLlamada", "menuGeneral");
			return new ModelAndView("redirect:/mtoOperadoresCampos.run").addAllObjects(parameters);
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al editar el Operador Campo Calculado: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_MODIF_KO));
			parameters.put("origenLlamada", "menuGeneral");
			return new ModelAndView("redirect:/mtoOperadoresCampos.run").addAllObjects(parameters);
		}
	}
	
	/**
	 * Realiza la baja del operador de campos calculados
	 * @param request
	 * @param response
	 * @param operadorCamposCalculadosBean Objeto que encapsula el operador de campos calculados a dar de baja
	 * @return 
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, OperadorCamposCalculados operadorCamposCalculadosBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			String idOpCalculado = StringUtils.nullToString(request.getParameter("idOpCalculado"));
			//recoger filtro
			String tablaOrigenFiltro = StringUtils.nullToString(request.getParameter("tablaOrigenFiltroC"));
			String campoFiltro = StringUtils.nullToString(request.getParameter("campoFiltroC"));
			String operadorFiltro = StringUtils.nullToString(request.getParameter("operadorFiltroC"));
			
			operadorCamposCalculadosBean = mtoOperadoresCamposCalculadosService.getOperadorCampoCalculado(Long.parseLong(idOpCalculado));
			if (operadorCamposCalculadosBean != null){
				parameters = mtoOperadoresCamposCalculadosService.bajaOperadorCampoCalculado(operadorCamposCalculadosBean);
			}else{
				logger.error("El operadorCampoCalculado con id: " + idOpCalculado + " no existe en BBDD");
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_BAJA_KO));
			}
			// pasamos el filtro por parámetros
			parameters.put("tablaOrigen", tablaOrigenFiltro);
			parameters.put("campo", campoFiltro);
			parameters.put("operador", operadorFiltro);
			return new ModelAndView("redirect://mtoOperadoresCampos.run").addAllObjects(parameters);
		}catch (Exception e) {
			logger.error("Se ha producido un error al dar de baja el Operador CampoCalculado. " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_BAJA_KO));		
			return new ModelAndView("redirect://mtoOperadoresCampos.run").addAllObjects(parameters);
		}
	}
	
	
	/**
	 * Setter del Service para Spring
	 * @param mtoOperadoresCamposCalculadosService
	 */
	public void setMtoOperadoresCamposCalculadosService(IMtoOperadoresCamposCalculadosService mtoOperadoresCamposCalculadosService) {
		this.mtoOperadoresCamposCalculadosService = mtoOperadoresCamposCalculadosService;
	}

}
