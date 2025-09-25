/**
 * 
 */
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

import com.rsi.agp.core.managers.ICambioClaseMasivoManager;
import com.rsi.agp.core.managers.IValidacionesUtilidadesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

/**
 * @author U029769
 * 
 */
public class CambioClaseMasivoController extends BaseMultiActionController {

	private static final Log LOOGER = LogFactory.getLog(CambioClaseMasivoController.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private static final String CHAR_SEPARADOR_IDS = ";";
	
	private IValidacionesUtilidadesManager validacionesUtilidadesManager;
	private ICambioClaseMasivoManager cambioClaseMasivoManager;
	

	/**
	 * Valida si las polizas están en estado Enviada Correcta
	 * 
	 * @author U029769 12/06/2013
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void validaPolCorrectasCambioClaseMasivo(
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		List<String> listaIds = null;
		String cambioClaseMasivoOK = "false";
		LOOGER.debug("Init - validaPolCorrectasCambioClaseMasivo");
		try {
			JSONObject objeto = new JSONObject();
			String listIdPolizas = StringUtils.nullToString(request
					.getParameter("idsRowsChecked"));
			listaIds = validacionesUtilidadesManager.limpiarVacios(Arrays
					.asList(listIdPolizas.split(CHAR_SEPARADOR_IDS)));

			// EN LA P63483 SE SOLICITA ELIMINAR LA VALIDACION POR ESTADO QUE IBA AQUI
			// AHORA ESTE METODO REALMENTE NO HACE NADA, PERO SE MANTIENE POR SI
			// EN UN FUTURO HUBIERA QUE IMPLEMENTAR MAS VALIDACIONES EN ESTE FLUJO
			if (listaIds.size() > 0) {
				cambioClaseMasivoOK = "true";
			}
			
			objeto.put("claseMasivoValido", cambioClaseMasivoOK);
			objeto.put("listaIdsPlz", StringUtils.toValoresSeparadosXComas(
					listaIds, false, false));
			getWriterJSON(response, objeto);

		} catch (Exception excepcion) {
			LOOGER.error(
					"Error al comprobar si las pólizas seleccionadas estan " +
							"en estado Enviada Correcta",
					excepcion);
			throw new Exception(
					"Error al comprobar si las pólizas seleccionadas estan " +
							"en estado Enviada Correcta",
					excepcion);

		}
		LOOGER.debug("End - validaPolCorrectasCambioClaseMasivo"
				+ cambioClaseMasivoOK);
	}
	/**
	 * Cambia la clase masivamente
	 * @author U029769 12/06/2013
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	public ModelAndView doCambioClaseMasivo(HttpServletRequest request,
			HttpServletResponse response) {
		
		LOOGER.debug("Init - doCambioClaseMasivo");
		
		ModelAndView mv = null;
		Map<String, String> parameters = new HashMap<String, String>();
		
		try {
		
			String clase = StringUtils.nullToString(request.getParameter("claseCM"));
			String listaIds = StringUtils.nullToString(request.getParameter("listaIdsPlz"));
			parameters = cambioClaseMasivoManager.cambiaClase(clase,listaIds);
		
		} catch (Exception e) {
			LOOGER.info("Se ha producido un error al cambiar la clase masivamente: " + e.getMessage());
			parameters.put("alerta",bundle.getString("mensaje.clase.masivo.KO"));
		}
		//parametros para la redireccion
		parameters.put("recogerPolizaSesion","true");
		parameters.put("operacion","consultar");
		
		mv = new ModelAndView(new RedirectView("utilidadesPoliza.html"));
		mv.addAllObjects(parameters);
		LOOGER.debug("End - doCambioClaseMasivo");
			
		return mv;
	}

	/**
	 * @author U029769
	 * @param validacionesUtilidadesManager
	 */
	public void setValidacionesUtilidadesManager(
			IValidacionesUtilidadesManager validacionesUtilidadesManager) {
		this.validacionesUtilidadesManager = validacionesUtilidadesManager;
	}

	/**
	 * @author U029769
	 * @param cambioClaseMasivoManager
	 */
	public void setCambioClaseMasivoManager(
			ICambioClaseMasivoManager cambioClaseMasivoManager) {
		this.cambioClaseMasivoManager = cambioClaseMasivoManager;
	}
}
