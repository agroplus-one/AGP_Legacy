package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.SolicitudReduccionCapManager;
import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.ParcelasReduccionCapitalManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesAnexoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.action.rc.ConfirmacionRCController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

/**
 * Clase para la comprobaci�n de precondiciones de diversas operaciones sobre el anexo
 * @author U029823
 *
 */
public class ValidacionesRCAjaxController extends BaseMultiActionController {

	private static final Log logger = LogFactory.getLog(ValidacionesRCAjaxController.class);
	private final String VACIO = "";
	
	private PolizaManager polizaManager;
	private ParcelasReduccionCapitalManager parcelasReduccionCapitalManager;
	private ExplotacionesAnexoManager explotacionesAnexoManager;
	private SolicitudReduccionCapManager solicitudRedCapManager;
	private AnexoModificacionManager anexoModificacionManager;
	private ConfirmacionRCController confirmacionRCController;

	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	/**
	 * Validaciones previas:
	 * Si es una p�liza de Ganado: si el anexo se ha modificado (IBAN o explotaciones), y si los datos variables guardados en los diferentes registros
	 * de GrupoRazaAnexo de cada explotaci�n tienen los mismos valores para un mismo nivel, refiri�ndonos a nivel como la ubicaci�n
	 * del concepto que se guarda (explotaci�n/grupo raza/tipo capital).
	 * Si es una p�liza de Agr�cola: si las parcelas del anexo se han modificado o se ha realizado alta o baja.
	 * @param request
	 * @param response
	 */
	public ModelAndView doValidacionesPreviasEnvio(final HttpServletRequest request, final HttpServletResponse response) {
		
		boolean esValido = true;
		boolean esValidoExplotacion = true;
		StringBuffer mensaje = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		try{
			String idRedCapStr = request.getParameter("parcela.reduccionCapital.id");
			Long idRedCap = new Long(idRedCapStr);
			//buscarReduccionesCapital DeclaracionesRedCapMan
			ReduccionCapital reduccionCapital = parcelasReduccionCapitalManager.getReduccionCapitalById(idRedCap);
//			AnexoModificacion anexo = anexoModificacionManager.obtenerAnexoModificacionById(idAnexo);
//				
//				XmlObject polizaDoc = this.solicitudRedCapManager
//						.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon());
//				es.agroseguro.contratacion.Poliza sitAct = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc)
//						.getPoliza();
//				
				//esValido = anexoModificacionManager.tieneModificacionesAnexo(anexo, sitAct, Constants.MODULO_POLIZA_PRINCIPAL);
				esValido = true;
				
				if (!esValido ){
					mensaje.append(bundle.getString("alerta.anexo.NoModificaciones"));
				}
			
			
			//FIN
			if (esValido){
				params.put("validacionesPreviasEnvio", "true");
			}
			else{
				params.put("validacionesPreviasEnvio", "false");
				params.put("mensaje",mensaje.toString());
			}
			
			params.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
			params.put("idReduccionCapital", idRedCap);
			params.put("vieneDeListadoRC", false);
			params.put("redireccion", "parcelas");
			
			return confirmacionRCController.doValidarRC(request, response).addAllObjects(params);
		}
		catch(Exception e){
			logger.error("doValidacionesPreviasEnvio - Ocurrio un error inesperado.", e);
			try {
				params.put("validacionesPreviasEnvio", "false");
				params.put("mensaje",mensaje.toString());
			} catch (Exception e1) {}
		    response.setCharacterEncoding("UTF-8");
		    logger.error(e);
    	}
		
		return null;
	}
	
	private void construirMensajeErrorExplotacionAnexo(StringBuffer mensaje, ExplotacionAnexo explotacionAnexo){
		mensaje.append("Los datos variables de la explotaci�n ");
		
		if(!StringUtils.isNullOrEmpty(explotacionAnexo.getRega())){
			mensaje.append("con Rega ").append(explotacionAnexo.getRega());
		
		}else if(!StringUtils.isNullOrEmpty(explotacionAnexo.getSigla())){
			mensaje.append("con Sigla ").append(explotacionAnexo.getSigla());
		}
		
		mensaje.append(" son incongruentes<br/>");
	}

	//SETTERs
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setExplotacionesAnexoManager(ExplotacionesAnexoManager explotacionesAnexoManager) {
		this.explotacionesAnexoManager = explotacionesAnexoManager;
	}

	public void setAnexoModificacionManager(AnexoModificacionManager anexoModificacionManager) {
		this.anexoModificacionManager = anexoModificacionManager;
	}

	public void setParcelasReduccionCapitalManager(ParcelasReduccionCapitalManager parcelasReduccionCapitalManager) {
		this.parcelasReduccionCapitalManager = parcelasReduccionCapitalManager;
	}

	public void setConfirmacionRCController(ConfirmacionRCController confirmacionRCController) {
		this.confirmacionRCController = confirmacionRCController;
	}

	
}