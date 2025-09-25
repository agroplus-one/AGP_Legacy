package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IValidaLupasCambioMasivoManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class ValidaLupasCambioMasivoController extends BaseMultiActionController{
	
	private static final Log logger = LogFactory.getLog(ValidaLupasCambioMasivoController.class);
	private IValidaLupasCambioMasivoManager validaLupasCambioMasivoManager;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public void doValidar(HttpServletRequest request, HttpServletResponse response)throws Exception{
		logger.debug("init - doValidar - ValidaLupasCambioMasivoController");
		
		String cultivo = "";
		String variedad = "";
		String lineaSeguroId = "";
		String destino = "";
		String tipoPlantacion = "";
		String sisCultivo = "";
		String tipoMarcoPlan = "";
		String practicaCultural = "";
		String provincia = "";
		String comarca = "";
		String termino = "";
		String subtermino = "";
		String sistProduccion = "";
		String listaModulos = "";
		
		JSONObject resultado = new JSONObject();
		ArrayList<String> erroresLupas = new ArrayList<String>();
		ArrayList<String> listCodModulos =null;
		try{
			logger.debug ("INIT - DoValidar - Validando campos lupas Cambio Masivo Ajax");
			lineaSeguroId = StringUtils.nullToString(request.getParameter("lineaseguroId"));
			
			cultivo = StringUtils.nullToString(request.getParameter("cultivo"));
			variedad = StringUtils.nullToString(request.getParameter("variedad"));
			destino  = StringUtils.nullToString(request.getParameter("destino"));
			tipoPlantacion = StringUtils.nullToString(request.getParameter("tipoPlantacion"));
			sisCultivo = StringUtils.nullToString(request.getParameter("sisCultivo"));
			tipoMarcoPlan = StringUtils.nullToString(request.getParameter("tipoMarcoPlan"));
			practicaCultural	= StringUtils.nullToString(request.getParameter("practicaCultural"));
			
			provincia	= StringUtils.nullToString(request.getParameter("provincia"));
			comarca  	= StringUtils.nullToString(request.getParameter("comarca"));
			termino 	= StringUtils.nullToString(request.getParameter("termino"));
			subtermino	= StringUtils.nullToString(request.getParameter("subtermino"));
			
			sistProduccion =StringUtils.nullToString(request.getParameter("sistProd")); 
			
			if (!provincia.equals("") || !comarca.equals("") || !termino.equals("") || !subtermino.equals("")){
				validaLupasCambioMasivoManager.validaUbicacion(provincia,comarca,termino,subtermino,erroresLupas);
			}
			if (!cultivo.equals("") && !variedad.equals("")){
				validaLupasCambioMasivoManager.validaCultivoVariedad (cultivo,variedad,lineaSeguroId,erroresLupas);
			}
			if (!destino.equals("")){
				validaLupasCambioMasivoManager.validaDestino (destino,erroresLupas);
			}
			if (!tipoPlantacion.equals("")){
				validaLupasCambioMasivoManager.validaTipoPlantacion (tipoPlantacion,erroresLupas);
			}
			if (!sisCultivo.equals("")){
				validaLupasCambioMasivoManager.validaSisCultivo (sisCultivo,erroresLupas);
			}
			if (!tipoMarcoPlan.equals("")){
				validaLupasCambioMasivoManager.validaMarcoPlan(tipoMarcoPlan,erroresLupas);
			}
			if (!practicaCultural.equals("")){
				validaLupasCambioMasivoManager.validaPracticaCultural(practicaCultural,erroresLupas);
			}	
			if(!sistProduccion.equals("")){
				validaLupasCambioMasivoManager.validaSistemaProduccion(sistProduccion, erroresLupas);
			}
			resultado.put("errores", erroresLupas);	
			logger.debug ("END - DoValidar - Validando campos lupas Cambio Masivo Ajax");
			
			getWriterJSON(response, resultado);
		
		}catch (Exception e) {
			logger.debug ("ERROR al validar campos lupas cambio Masivo Ajax");
			erroresLupas.add(bundle.getString("mensaje.cambioMasivo.KO"));
			
		}
		
	}

	public void setValidaLupasCambioMasivoManager(
			IValidaLupasCambioMasivoManager validaLupasCambioMasivoManager) {
		this.validaLupasCambioMasivoManager = validaLupasCambioMasivoManager;
	}
		

}
