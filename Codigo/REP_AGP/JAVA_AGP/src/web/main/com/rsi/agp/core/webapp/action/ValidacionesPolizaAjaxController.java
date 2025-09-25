package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IValidacionesUtilidadesManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;

/**
 * Clase para la comprobación de precondiciones de diversas operaciones sobre la póliza
 * @author U029823
 *
 */
public class ValidacionesPolizaAjaxController extends BaseMultiActionController {

	private static final Log logger = LogFactory.getLog(ValidacionesPolizaAjaxController.class);
	private static final String CHAR_SEPARADOR_IDS = ";";
	private final String VACIO = "";
	
	private PolizaManager polizaManager;
	private ExplotacionesManager explotacionesManager;
	private IValidacionesUtilidadesManager validacionesUtilidadesManager;
	

	/**
	 * Valida que la póliza tenga un estado en el que se permita un cambio de titularidad
	 * No está adaptada para cambios masivos
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void validaPolCorrectaCambioTitular(
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		List<String> listaIds = null;
		String cambioOK = "false";
		logger.debug("Init - validaPolCorrectaCambioTitular");
		try {
			JSONObject objeto = new JSONObject();
			String listIdPolizas = StringUtils.nullToString(request.getParameter("idsRowsChecked"));
			listaIds = validacionesUtilidadesManager.limpiarVacios(Arrays.asList(listIdPolizas.split(CHAR_SEPARADOR_IDS)));
			
			if(listaIds.size() > 0) {
				// EN LA P63483 SE SOLICITA ELIMINAR LA VALIDACION POR ESTADO QUE IBA AQUI
				// AHORA ESTE METODO REALMENTE NO HACE NADA, PERO SE MANTIENE POR SI
				// EN UN FUTURO HUBIERA QUE IMPLEMENTAR MAS VALIDACIONES EN ESTE FLUJO
				cambioOK = "true";
			}
			
			objeto.put("cambioTitularValido", cambioOK);
			objeto.put("listaIdsPlz", StringUtils.toValoresSeparadosXComas(listaIds, false, false));
			getWriterJSON(response, objeto);

		} catch (Exception excepcion) {
			logger.error("Error al comprobar si las pólizas seleccionadas estan en estado Enviada Correcta", excepcion);
			throw new Exception("Error al comprobar si las pólizas seleccionadas estan en estado Enviada Correcta", excepcion);

		}
		logger.debug("End - validaPolCorrectaCambioTitular" + cambioOK);
	}


	/**
	 * Valida que la póliza tenga un estado en el que se permita un cambio de IBAN
	 * No está adaptada para cambios masivos
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void validaPolCorrectaCambioIBAN(
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		List<String> listaIds = null;
		String cambioOK = "false";
		logger.debug("Init - validaPolCorrectaCambioIBAN");
		try {
			JSONObject objeto = new JSONObject();
			String listIdPolizas = StringUtils.nullToString(request.getParameter("idsRowsChecked"));
			listaIds = validacionesUtilidadesManager.limpiarVacios(Arrays.asList(listIdPolizas.split(CHAR_SEPARADOR_IDS)));
			
			if(listaIds.size()>0){
				String idPoliza = listaIds.get(0);
				Poliza poliza = polizaManager.getPoliza(new Long(idPoliza));
				EstadoPoliza estado = poliza.getEstadoPoliza();
				
				//Estados en el que se permite un cambio de IBAN
				if(Constants.ESTADO_POLIZA_DEFINITIVA.compareTo(estado.getIdestado())==0){
					cambioOK = "true";
				}
				
				if(Constants.ESTADO_POLIZA_EMITIDA.compareTo(estado.getIdestado()) == 0){
					Boolean isGanado = polizaManager.esPolizaGanadoByIdPoliza(new Long(idPoliza));
					if(isGanado){
						cambioOK = "true";
					}
				}
			}
			
			objeto.put("cambioIBANValido", cambioOK);
			objeto.put("listaIdsPlz", StringUtils.toValoresSeparadosXComas(listaIds, false, false));
			getWriterJSON(response, objeto);

		} catch (Exception excepcion) {
			logger.error("Error al comprobar si las pólizas seleccionadas estan en estado Enviada Correcta", excepcion); 
			throw new Exception("Error al comprobar si las pólizas seleccionadas estan en estado Enviada Correcta", excepcion);

		}
		logger.debug("End - validaPolCorrectaCambioIBAN" + cambioOK);
	}
	
	
	/**
	 * Valida si los datos variables guardados en los diferentes registros de GrupoRaza de cada explotación tienen los mismos valores para un
	 * mismo nivel, refiriéndonos a nivel como la ubicación del concepto que se guarda (explotación/grupo raza/tipo capital).
	 * @param request
	 * @param response
	 */
	public void doValidarDatosVariables(final HttpServletRequest request, final HttpServletResponse response) {
		
		boolean esValido = true;
		boolean esValidoExplotacion = true;
		StringBuffer mensaje = new StringBuffer();
		JSONObject params = new JSONObject();
		
		try{
			Map<Integer, BigDecimal> mapaCodConceptoVsUbicacion = null;
			Map<Integer, String> mapaConceptoNombre = null;
			
			Map<String, String> mapaExplotacion = new HashMap<String, String>();//Clave = codConcepto
			Map<String, String> mapaGrupoRaza = new HashMap<String, String>();//Clave = codGrupoRaza|codConcepto
			Map<String, String> mapaTipoCapital = new HashMap<String, String>();//Clave = codGrupoRaza|codTipoCapital|codConcepto
			
			//colProcesados va almacenando qué explotación, grupo raza y tipo capital van siendo procesados
			//para poder distinguir los casos en los que no se ha guardado un dato pero porque no estaba relleno
			Set<String> colProcesados = new HashSet<String>();
			
			//INI
			String idPoliza = request.getParameter("idPoliza");
			List<Long> listaIdsExplotaciones = explotacionesManager.obtenerExplotacionesConVariosGruposRaza(new Long(idPoliza));
			
			//Si no hay con varios grupos raza, nos lo saltamos
			if(listaIdsExplotaciones.size()>0){
			
				Poliza poliza = polizaManager.getPoliza(new Long(idPoliza));
				Long lineaSeguroId = poliza.getLinea().getLineaseguroid();
				mapaCodConceptoVsUbicacion = new HashMap<Integer, BigDecimal>();
				mapaConceptoNombre = new HashMap<Integer, String>();
				polizaManager.obtenerMapaCodConceptoVsUbicacion(lineaSeguroId, mapaCodConceptoVsUbicacion, mapaConceptoNombre);
				
				Iterator<Long> itListaExplotaciones = listaIdsExplotaciones.iterator();
				
				while(itListaExplotaciones.hasNext()){
					
					esValidoExplotacion = true;
					Long idExplotacion = itListaExplotaciones.next();
					Explotacion exp = explotacionesManager.obtenerExplotacionById(idExplotacion);

					Set<GrupoRaza> colGrupoRaza = exp.getGrupoRazas();
					Iterator<GrupoRaza> itColGrupoRaza =  colGrupoRaza.iterator();

					
					while(itColGrupoRaza.hasNext()){
						GrupoRaza gr = itColGrupoRaza.next();
						
						Long codGrupoRaza = gr.getCodgruporaza();
						BigDecimal codTipoCapital = gr.getCodtipocapital();
						
						Set<DatosVariable> colDatosVariables = gr.getDatosVariables();
						Iterator<DatosVariable> itColDatosVariable = colDatosVariables.iterator();
						
						while(itColDatosVariable.hasNext()){
							DatosVariable dv = itColDatosVariable.next();
							Integer dvCodConcepto = dv.getCodconcepto();
							String dvValor = dv.getValor();
							
							BigDecimal ubicacion = mapaCodConceptoVsUbicacion.get(dvCodConcepto);
							
							//Ignoramos si es null. Ocurre cuando se borran campos de la configuración de pantalla sin eliminar los registros
							//de datos variables con ese concepto 
							if(ubicacion!=null){
							
								String nombreCampo = mapaConceptoNombre.get(dvCodConcepto);
								//Variables de explotación
								if(OrganizadorInfoConstants.UBICACION_EXPLOTACION.compareTo(ubicacion)==0){
									if(!colProcesados.contains("EXP|"+idExplotacion)){
										//Se incluye en el mapa
										mapaExplotacion.put(VACIO + dvCodConcepto, dvValor);
									}else{
										//Se comparan valores
										String valorAnterior = mapaExplotacion.get(VACIO + dvCodConcepto);
										if(!dvValor.equals(valorAnterior)){
											esValidoExplotacion = false;
											logger.debug("Dato variable [" + nombreCampo +"] grabado con distintos valores para la explotación " + idExplotacion + "<br/>");
										}
									}
									
								//Variables de grupo raza
								}else if(OrganizadorInfoConstants.UBICACION_GRUPO_RAZA.compareTo(ubicacion)==0){
									if(!colProcesados.contains("GR|"+idExplotacion+"|"+codGrupoRaza)){
										//Se incluye en el mapa
										mapaGrupoRaza.put(VACIO + codGrupoRaza + "|" + dvCodConcepto, dvValor);
									}else{
										//Se comparan valores
										String valorAnterior  = mapaGrupoRaza.get(VACIO + codGrupoRaza + "|" + dvCodConcepto);
										if(!dvValor.equals(valorAnterior)){
											esValidoExplotacion = false;
											logger.debug("Dato variable [" + nombreCampo +"] grabado con distintos valores para la explotación " + idExplotacion + ", grupo raza " + codGrupoRaza + "<br/>");
										}
									}
									
								//Variables de tipo capital
								}else if(OrganizadorInfoConstants.UBICACION_CAP_ASEG.compareTo(ubicacion)==0){
									if(!colProcesados.contains("TC|"+idExplotacion+"|"+codGrupoRaza+"|"+codTipoCapital)){
										mapaTipoCapital.put(VACIO + codGrupoRaza + "|" + codTipoCapital + "|" + dvCodConcepto, dvValor);
									}else{
										//Se comparan valores
										String valorAnterior  = mapaTipoCapital.get(VACIO + codGrupoRaza + "|" + codTipoCapital + "|" + dvCodConcepto);
										if(!dvValor.equals(valorAnterior)){
											esValidoExplotacion = false;
											logger.debug("Dato variable [" + nombreCampo +"] grabado con distintos valores para la explotación " + idExplotacion + ", grupo raza " + codGrupoRaza + ", tipo capital " + codTipoCapital + "<br/>");
										}
									}
								}
							}
						}

						colProcesados.add("EXP|"+idExplotacion);//Para ver si se han procesado los de explotación
						colProcesados.add("GR|"+idExplotacion+"|"+codGrupoRaza);//Para ver si se han procesado los de grupo raza
						colProcesados.add("TC|"+idExplotacion+"|"+codGrupoRaza+"|"+codTipoCapital);//Para ver si se han procesado los de tipo capital
					}
					
					//vaciamos los mapas y procesados porque de una explotación a otra no interfiere
					mapaExplotacion.clear();
					mapaGrupoRaza.clear();
					mapaTipoCapital.clear();
					colProcesados.clear();
					
					esValido = esValidoExplotacion && esValido;
					
					if(!esValidoExplotacion){
						construirMensajeErrorExplotacion(mensaje, exp);
					}
				}
			}
			//FIN
			if (esValido){
				params.put("datosVariablesValidos", "true");
			}
			else{
				params.put("datosVariablesValidos", "false");
				params.put("mensaje",mensaje.toString());
			}
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);

		}
		catch(Exception e){
			logger.error("doValidarDatosVariables - Ocurrio un error inesperado.", e);
			try {
				params.put("datosVariablesValidos", "false");
				params.put("mensaje",mensaje.toString());
			} catch (JSONException e1) {}
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);
    	}
	}

	private void construirMensajeErrorExplotacion(StringBuffer mensaje, Explotacion explotacion){
		mensaje.append("Los datos variables de la explotación ");
		
		if(!StringUtils.isNullOrEmpty(explotacion.getRega())){
			mensaje.append("con Rega ").append(explotacion.getRega());
		
		}else if(!StringUtils.isNullOrEmpty(explotacion.getSigla())){
			mensaje.append("con Sigla ").append(explotacion.getSigla());
		}
		
		mensaje.append(" son incongruentes<br/>");
	}

	//SETTERs
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setValidacionesUtilidadesManager(IValidacionesUtilidadesManager validacionesUtilidadesManager) {
		this.validacionesUtilidadesManager = validacionesUtilidadesManager;
	}

	public void setExplotacionesManager(ExplotacionesManager explotacionesManager) {
		this.explotacionesManager = explotacionesManager;
	}
}