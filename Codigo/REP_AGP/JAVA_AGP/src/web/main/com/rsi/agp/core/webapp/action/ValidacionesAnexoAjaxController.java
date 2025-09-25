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

import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesAnexoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;

/**
 * Clase para la comprobación de precondiciones de diversas operaciones sobre el anexo
 * @author U029823
 *
 */
public class ValidacionesAnexoAjaxController extends BaseMultiActionController {

	private static final Log logger = LogFactory.getLog(ValidacionesAnexoAjaxController.class);
	private final String VACIO = "";
	
	private PolizaManager polizaManager;
	private AnexoModificacionManager anexoModificacionManager;
	private ExplotacionesAnexoManager explotacionesAnexoManager;
	private SolicitudModificacionManager solicitudModificacionManager;
	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	/**
	 * Validaciones previas:
	 * Si es una póliza de Ganado: si el anexo se ha modificado (IBAN o explotaciones), y si los datos variables guardados en los diferentes registros
	 * de GrupoRazaAnexo de cada explotación tienen los mismos valores para un mismo nivel, refiriéndonos a nivel como la ubicación
	 * del concepto que se guarda (explotación/grupo raza/tipo capital).
	 * Si es una póliza de Agrícola: si las parcelas del anexo se han modificado o se ha realizado alta o baja.
	 * @param request
	 * @param response
	 */
	public void doValidacionesPreviasEnvio(final HttpServletRequest request, final HttpServletResponse response) {
		
		boolean esValido = true;
		boolean esValidoExplotacion = true;
		StringBuffer mensaje = new StringBuffer();
		JSONObject params = new JSONObject();
		
		try{
			String idAnexoStr = request.getParameter("idAnexo");
			Long idAnexo = new Long(idAnexoStr);

			AnexoModificacion anexo = anexoModificacionManager.obtenerAnexoModificacionById(idAnexo);
						
			// Comprobamos si es Ganado la póliza
			boolean esPolizaGanado = polizaManager.esPolizaGanadoByIdPoliza(anexo.getPoliza().getIdpoliza());
			if (esPolizaGanado) {
				boolean tieneModificacionesIban = anexo.getEsIbanAsegModificado()!=null && anexo.getEsIbanAsegModificado().compareTo(1)==0;
				boolean tieneModificacionesIban2 = anexo.getEsIban2AsegModificado()!= null && anexo.getEsIban2AsegModificado().compareTo(1)==0;
				boolean tieneModificacionesExplotaciones = anexoModificacionManager.isAnexoExplotacionesConModificaciones(idAnexo);
				boolean tieneModificacionesCoberturas = anexoModificacionManager.isAnexoCoberturasConModificaciones(idAnexo);
				boolean tieneModificacionesSubvenciones = anexoModificacionManager.isAnexoSubvencionesConModificaciones(idAnexo);
				
				if(tieneModificacionesExplotaciones){
					
					Map<Integer, BigDecimal> mapaCodConceptoVsUbicacion = null;
					Map<Integer, String> mapaConceptoNombre = null;
					
					Map<String, String> mapaExplotacion = new HashMap<String, String>();//Clave = codConcepto
					Map<String, String> mapaGrupoRaza = new HashMap<String, String>();//Clave = codGrupoRaza|codConcepto
					Map<String, String> mapaTipoCapital = new HashMap<String, String>();//Clave = codGrupoRaza|codTipoCapital|codConcepto
					
					//colProcesados va almacenando qué explotación, grupo raza y tipo capital van siendo procesados
					//para poder distinguir los casos en los que no se ha guardado un dato pero porque no estaba relleno
					Set<String> colProcesados = new HashSet<String>();
					
					//INI
					List<Long> listaIdsExplotaciones = explotacionesAnexoManager.obtenerIdsExplotacionesAnexoConVariosGruposRaza(idAnexo);
					
					//Si no hay con varios grupos raza, nos lo saltamos
					if(listaIdsExplotaciones.size()>0){
					
						Long lineaSeguroId = anexo.getPoliza().getLinea().getLineaseguroid();
						mapaCodConceptoVsUbicacion = new HashMap<Integer, BigDecimal>();
						mapaConceptoNombre = new HashMap<Integer, String>();
						polizaManager.obtenerMapaCodConceptoVsUbicacion(lineaSeguroId, mapaCodConceptoVsUbicacion, mapaConceptoNombre);
						
						Iterator<Long> itListaExplotaciones = listaIdsExplotaciones.iterator();
						
						while(itListaExplotaciones.hasNext()){
							
							esValidoExplotacion = true;
							Long idExplotacion = itListaExplotaciones.next();
							ExplotacionAnexo expAnexo = explotacionesAnexoManager.getExplotacionAnexo(idExplotacion);
		
							Set<GrupoRazaAnexo> colGrupoRaza = expAnexo.getGrupoRazaAnexos();
							Iterator<GrupoRazaAnexo> itColGrupoRaza =  colGrupoRaza.iterator();
		
							
							while(itColGrupoRaza.hasNext()){
								GrupoRazaAnexo gr = itColGrupoRaza.next();
								
								Long codGrupoRaza = gr.getCodgruporaza();
								BigDecimal codTipoCapital = gr.getCodtipocapital();
								
								Set<DatosVarExplotacionAnexo> colDatosVariables = gr.getDatosVarExplotacionAnexos();
								Iterator<DatosVarExplotacionAnexo> itColDatosVariable = colDatosVariables.iterator();
								
								while(itColDatosVariable.hasNext()){
									DatosVarExplotacionAnexo dv = itColDatosVariable.next();
									Integer dvCodConcepto = dv.getCodconcepto();
									String dvValor = dv.getValor();
		
									//No debería ser nulo, pero en la tabla de datos variables se está colando alguno
									if(dvValor!=null){
									
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
								construirMensajeErrorExplotacionAnexo(mensaje, expAnexo);
							}
						}
					}
				}else if (tieneModificacionesCoberturas) {
					esValido = true;
				}else if (tieneModificacionesSubvenciones) {
					esValido = true;
				}else if(!tieneModificacionesExplotaciones && !tieneModificacionesCoberturas && (tieneModificacionesIban || tieneModificacionesIban2)){
					esValido = true;
				}else{
					esValido = false;
					mensaje.append(bundle.getString("alerta.anexo.NoModificaciones"));
				}
			}else { // NO es ganado
				boolean tieneModificacionesIban_Agri = anexo.getEsIbanAsegModificado() != null
						&& anexo.getEsIbanAsegModificado().compareTo(1) == 0;
				boolean tieneModificacionesIban2_Agri = anexo.getEsIban2AsegModificado() != null
						&& anexo.getEsIban2AsegModificado().compareTo(1) == 0;
				
				XmlObject polizaDoc = this.solicitudModificacionManager
						.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon());
				es.agroseguro.contratacion.Poliza sitAct = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc)
						.getPoliza();
				
				esValido = anexoModificacionManager.tieneModificacionesAnexo(anexo, sitAct,
						Constants.MODULO_POLIZA_PRINCIPAL);
				
				if (esValido){
					esValido = true;
				}else {
					if (tieneModificacionesIban_Agri || tieneModificacionesIban2_Agri) {
						esValido = true;
					}else {
						esValido = false;	
					}
				}
				if (!esValido ){
					mensaje.append(bundle.getString("alerta.anexo.NoModificaciones"));
				}
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
			
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);
		}
		catch(Exception e){
			logger.error("doValidacionesPreviasEnvio - Ocurrio un error inesperado.", e);
			try {
				params.put("validacionesPreviasEnvio", "false");
				params.put("mensaje",mensaje.toString());
			} catch (JSONException e1) {}
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);
    	}
	}
	
	private void construirMensajeErrorExplotacionAnexo(StringBuffer mensaje, ExplotacionAnexo explotacionAnexo){
		mensaje.append("Los datos variables de la explotación ");
		
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

	public void setSolicitudModificacionManager(SolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}
}