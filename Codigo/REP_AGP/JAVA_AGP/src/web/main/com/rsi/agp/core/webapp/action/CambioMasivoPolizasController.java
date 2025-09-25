package com.rsi.agp.core.webapp.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.managers.ICambioMasivoPolizasManager;
import com.rsi.agp.core.managers.IValidacionesUtilidadesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;

public class CambioMasivoPolizasController extends BaseMultiActionController{
	
	ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private static final Log logger = LogFactory.getLog(CambioMasivoPolizasController.class);
	private final String CHAR_SEPARADOR_IDS = ";";
	private IValidacionesUtilidadesManager validacionesUtilidadesManager;
	private ICambioMasivoPolizasManager cambioMasivoPolizasManager;

	
	/** DAA 29/05/2013
	 * Realiza el proceso de pago masivo y devuelve a la pantalla de utilidades
	 * @param request
	 * @param response
	 * @return mv
	 */
	@SuppressWarnings("all")
	public ModelAndView doPagoMasivo (HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("Init - doPagoMasivo");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			String fechapago = StringUtils.nullToString(request.getParameter("fechapago"));
			String listaIds = StringUtils.nullToString(request.getParameter("listaIds"));
			String marcar_desmarcar = StringUtils.nullToString(request.getParameter("pagoMasivo"));
			cambioMasivoPolizasManager.pagoMasivo(fechapago, listaIds, marcar_desmarcar,usuario.getCodusuario());
			parameters.put("mensaje",bundle.getString("mensaje.pago.masivo.OK"));
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al efectuar el pago masivo: " , e);
			parameters.put("alerta",bundle.getString("mensaje.pago.masivo.KO"));
		}
		//parametros para la redireccion
		parameters.put("recogerPolizaSesion","true");
		parameters.put("operacion","consultar");
		
		mv = new ModelAndView(new RedirectView("utilidadesPoliza.html"));
		mv.addAllObjects(parameters);
		logger.debug("End - doPagoMasivo");
			
		return mv;
	}
	
	
	
	/** DAA 28/05/2013 
	 *  Convierte a un JSON la comprobacion previa al PagoMasivo
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	public void compruebaPolizasDefinitivasPagoMasivo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		List<String> listaIds = null;
		String pagoMasivoValido = "false";
		logger.debug("Init - compruebaPolizasDefinitivasPagoMasivo");
		try{
			JSONObject objeto = new JSONObject();
			String listIdPolizas = StringUtils.nullToString(request.getParameter("idsRowsChecked"));
			listaIds = validacionesUtilidadesManager.limpiarVacios (Arrays.asList (listIdPolizas.split(CHAR_SEPARADOR_IDS)));
			
			if (listaIds.size() > 0) {
				pagoMasivoValido = "true";
			}
			objeto.put("pagoMasivoValido",pagoMasivoValido);
			objeto.put("listaIds",StringUtils.toValoresSeparadosXComas(listaIds,false,false));
			getWriterJSON(response, objeto);
			
    	}
    	catch(Exception excepcion){
    		logger.error("Error al comprobar si las polizas seleccionadas estan en estado Definitiva",excepcion);
    		throw new Exception("Error al comprobar si las polizas seleccionadas estan en estado Definitiva", excepcion);
			
    	}
    	logger.debug("End - compruebaPolizasDefinitivasPagoMasivo - pagoMasivoValido="+pagoMasivoValido);
	}

	public void setValidacionesUtilidadesManager(
			IValidacionesUtilidadesManager validacionesUtilidadesManager) {
		this.validacionesUtilidadesManager = validacionesUtilidadesManager;
	}

	public void setCambioMasivoPolizasManager(
			ICambioMasivoPolizasManager cambioMasivoPolizasManager) {
		this.cambioMasivoPolizasManager = cambioMasivoPolizasManager;
	}

}
