
package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.web.servlet.ModelAndView;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.DistribucionCostesException;
import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.jmesa.dao.IImportesFraccDao;
import com.rsi.agp.core.jmesa.service.IMtoComisionesRenovService;
import com.rsi.agp.core.managers.ICuadroCoberturasManager;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.ISeleccionComparativasSWManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.ComparativaPolizaComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ModuloPolizaComparator;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.core.ws.ResultadoWS;
import com.rsi.agp.dao.filters.cesp.impl.ModuloCompatibleFiltro;
import com.rsi.agp.dao.filters.commons.ErrorWsFiltro;
import com.rsi.agp.dao.filters.cpl.ModuloFiltro;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.models.config.IErrorWsAccionDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.ICaracteristicaExplotacionDao;
import com.rsi.agp.dao.models.poliza.IComparativaDao;
import com.rsi.agp.dao.models.poliza.ICondicionesFraccionamientoDao;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cesp.ModuloCompatibleCe;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamientoId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.contratacion.costePoliza.BonificacionRecargo;
import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.distribucionCostesSeguro.CalculoAlternativoFinanciacion;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.BonificacionAsegurado;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Consorcio;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DatosCalculo;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DistribucionCoste;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.RecargoAsegurado;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionCCAA;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionEnesa;
import es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument;
import es.agroseguro.seguroAgrario.listaRecibos.ListaRecibosDocument;
import es.agroseguro.seguroAgrario.recibos.FaseDocument;
import es.agroseguro.serviciosweb.contratacionscutilidades.FinanciarRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.ObjectFactory;

/**
 * Manager para las llamadas a Servicios Web
 * 
 * @author T-Systems
 * 
 */
public class WebServicesManager implements IManager {
	
	private static final Log logger = LogFactory.getLog(WebServicesManager.class);
	private static final String ERROR_SERVICIO = "Servicio de ";
	private static final String CALCULO = "calculo";
	private static final String FINANCIACION = "financiacion";
	private static final String S_ERROR_HEADER = "sErrorHeader";
	private static final String B_BOTON_CALCULO = "bBotonCalculo";
	private static final String B_BOTON_CORREGIR = "bBotonCorregir";
	private static final String ARR_BOTON_CALCULO = "arrBotonCalculo";
	private static final String ARR_BOTON_CORREGIR = "arrBotonCorregir";
	private static final String ALERTA = "alerta";
	private static final String TD_LITERAL_BORDE = "<td class='literalborde' width='20%'>";
	private static final String NBSP_TD = "&nbsp;</td>";
	private static final String ERROR_CONSULTA_POLIZA = "Se ha producido un error al consultar el estado actual de la poliza";
	private static final String MILISEGUNDOS = " milisegundos";
	private static final String MUESTRA_BOTON_DESCUENTOS = "muestraBotonDescuentos";

	// Helpers para la llamadas a los servicios Web
	private ServicioValidarHelper servicioValidarHelper = new ServicioValidarHelper();
	private ServicioConfirmarHelper servicioConfirmarHelper = new ServicioConfirmarHelper();
	private ServicioCalcularHelper servicioCalcularHelper = new ServicioCalcularHelper();
	private ServicioFinanciarHelper ServicioFinanciarHelper = new ServicioFinanciarHelper();
	private ServicioPolizaActualPDFHelper servicioPolizaActualPDFHelper = new ServicioPolizaActualPDFHelper();
	private ServicioConsultaEstado servicioConsultaEstado = new ServicioConsultaEstado();
	private ServicioListarRecibosPoliza servicioListarRecibosPoliza = new ServicioListarRecibosPoliza();
	private ServicioConsultaDetalleRecibo servicioConsultaDetalleRecibo = new ServicioConsultaDetalleRecibo();
	private ServicioConsultaCostesRecibo servicioConsultaCostesRecibo = new ServicioConsultaCostesRecibo();
	private ServicioCaractExplotacionHelper servicioCaractExplotacionHelper = new ServicioCaractExplotacionHelper();
	private ServicioPolizaOrigenPDFHelper servicioPolizaOrigenPDFHelper = new ServicioPolizaOrigenPDFHelper();
	private ServicioPolizaTradActualPDFHelper servicioPolizaTradActualPDFHelper = new ServicioPolizaTradActualPDFHelper();
	private ServicioSeguimientoPolizaHelper servicioSeguimientoPolizaHelper = new ServicioSeguimientoPolizaHelper();
	private ServicioSituacionActualPDFHelper servicioSituacionActualPDFHelper = new ServicioSituacionActualPDFHelper();

	private ICuadroCoberturasManager cuadroCoberturasManager;
	private CaracteristicaExplotacionManager caracteristicaExplotacionManager;
	private PagoPolizaManager pagoPolizaManager;
	private IPolizasPctComisionesManager polizasPctComisionesManager;
	private HistoricoWSManager historicoWSManager;
	private ISeleccionComparativasSWManager seleccionComparativasSWManager;
	private IHistoricoEstadosManager historicoEstadosManager;
	
	private IPolizaDao polizaDao;
	private ICaracteristicaExplotacionDao caracteristicaExplotacionDao;
	private IComparativaDao comparativaDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private IPolizasPctComisionesDao polizasPctComisionesDao;
	private IDistribucionCosteDAO distribucionCosteDAO;
	private ICondicionesFraccionamientoDao condicionesFraccionamientoDao;
	private IImportesFraccDao importesFraccDao;
	private IErrorWsAccionDao errorWsAccionDao;
	private ISeleccionPolizaDao seleccionPolizaDao;
	
	private IMtoComisionesRenovService mtoComisionesRenovService;
	
	/**
	 * Llamadas a los servicios Web
	 * 
	 * @param poliza
	 * @param webServiceToCall
	 * @param origenllamada
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public ModelAndView callWebService(Poliza poliza, String webServiceToCall,
			String origenllamada, String modoLectura, String realPath,
			Parametro parametro, Map<String, Object> parameters,
			HttpServletRequest request, boolean actualizaDistribucionCostes) {
		ModelAndView mv = null;
		Long idEnvio = null;
		GregorianCalendar fechaInicio = new GregorianCalendar();
		Usuario usuario = (Usuario) request.getSession()
				.getAttribute("usuario");
		
		logger.debug("WebServicesManager - callWebService [INIT] ");
		if (poliza != null) {
			// Objeto que encapsula el resultado del calculo para polizas de
			// Agrarios con el esquema antiguo
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo = null;
			// Objeto que encapsula el resultado del calculo para polizas de
			// Ganado y Agrario con el esquema unificado
			es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado = null;

			AcuseRecibo acuseRecibo = null;
			AcuseRecibo acuseRecibo_swConfirmacion = null;

			// Aqui se guarda el restultado de las llamadas a los WS por cada
			// comparativa Poliza
			SortedMap<ComparativaPolizaId, ResultadoWS> acusePolizaHolder = new TreeMap<ComparativaPolizaId, ResultadoWS>();
			// Aqui se guarda la representacion HTML de las comparativas
			// seleccionadas para mostrarlas en el jsp
			Map<String, String> cabeceraComparativaHTML = new LinkedHashMap<String, String>();
			// Aqui se guardan los objetos que ayudaran a pintar la pantalla de
			// Importes
			Set<VistaImportes> fluxCondensatorHolder = new LinkedHashSet<VistaImportes>();
			ResultadoWS resultadoWS = null;
			es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument financiacion = null;
			Map<String, Object> respuestaServFinanciacion = null;

			try {
				
				Boolean esGanado = poliza.getLinea().isLineaGanado();
				
				ArrayList<Long> calledComparativas = new ArrayList<Long>();
						
				/* Pet. 57626 ** MODIF TAM (06.07.2020) ** Inicio */
				/* Solo se llama al calculo de Caracteristicas de la Explotacion  al validar
				 * en el ciclo de contratacion
				 */
				boolean aplicaCaractExpl = false;						
				if (!esGanado && webServiceToCall.equals(Constants.WS_VALIDACION)) {
					aplicaCaractExpl = caracteristicaExplotacionDao.aplicaYBorraCaractExplocion(poliza);
				}		

				String financiacionSeleccionada = null;
				String[] finanSelec = null;
				String finanIdModSelec = null;
				String finanFilaSelec = null;
				String finanCodModSelec = null;
				if (null != parameters) {

					if (null != parameters.get("financiacionSeleccionada")) {
						financiacionSeleccionada = ((String[]) parameters
								.get("financiacionSeleccionada"))[0];
						if (null != financiacionSeleccionada) {
							financiacionSeleccionada = ((String[]) parameters
									.get("financiacionSeleccionada"))[0];
							finanSelec = financiacionSeleccionada.trim().split(
									"\\|");
							finanIdModSelec = finanSelec[0];
							finanCodModSelec = finanSelec[1];
							finanFilaSelec = finanSelec[7];
						}
					}
				}
				/** Tamara 29/02/2012 fin */
				
				List<ComparativaPoliza> listComparativasPoliza = this.polizaDao.getLstCompPolizasByIdPol(poliza.getIdpoliza()); 
				if (listComparativasPoliza == null) {
					listComparativasPoliza = new ArrayList<ComparativaPoliza>(); 
				}
				Collections.sort(listComparativasPoliza, new ComparativaPolizaComparator());
				
				List<ModuloPoliza> modulosPoliza = polizaDao.getLstModulosPoliza(poliza.getIdpoliza());
				Collections.sort(modulosPoliza, new ModuloPolizaComparator());
				for (ModuloPoliza mp : modulosPoliza) {

					if (calledComparativas.indexOf(mp.getId().getNumComparativa()) == -1) {
						
						ComparativaPoliza cp = null;
						for (ComparativaPoliza cpAux : listComparativasPoliza) {
							if (mp.getId().getNumComparativa().equals(cpAux.getId().getIdComparativa())) {
								cp = cpAux;
								break;
							}
						}
						if (cp == null) {							
							cp = new ComparativaPoliza();
							ComparativaPolizaId cpId = new ComparativaPolizaId();
							cpId.setIdpoliza(poliza.getIdpoliza());
							cpId.setLineaseguroid(poliza.getLinea().getLineaseguroid());
							cpId.setCodmodulo(mp.getId().getCodmodulo());
							cpId.setIdComparativa(mp.getId().getNumComparativa());							
							if (esGanado) {
								List<RiesgoCubiertoModuloGanado> lstrcm = polizaDao.getRiesgoCubiertosModuloGanado(
										poliza.getLinea().getLineaseguroid(), mp.getId().getCodmodulo());
								if (lstrcm != null && !lstrcm.isEmpty()) {
									RiesgoCubiertoModuloGanado rcm = lstrcm.get(0);  
									cp.setRiesgoCubiertoModuloGanado(rcm);		
									cpId.setFilamodulo(BigDecimal.valueOf(rcm.getId().getFilamodulo()));
									cpId.setCodconceptoppalmod(rcm.getConceptoPpalModulo().getCodconceptoppalmod());
									cpId.setCodriesgocubierto(rcm.getRiesgoCubierto().getId().getCodriesgocubierto());
								} else {
									cp.setRiesgoCubiertoModuloGanado(new RiesgoCubiertoModuloGanado());
									cpId.setFilamodulo(new BigDecimal(999));
									cpId.setCodconceptoppalmod(new BigDecimal(999));
									cpId.setCodriesgocubierto(new BigDecimal(999));
								}
								cpId.setCodconcepto(new BigDecimal(999));
								cpId.setFilacomparativa(new BigDecimal(99));
								cpId.setCodvalor(new BigDecimal(-2));
							} else {
								List<RiesgoCubiertoModulo> lstrcm = polizaDao.getRiesgoCubiertosModulo(
										poliza.getLinea().getLineaseguroid(), mp.getId().getCodmodulo());
								if (lstrcm != null && !lstrcm.isEmpty()) {
									RiesgoCubiertoModulo rcm = lstrcm.get(0);  
									cp.setRiesgoCubiertoModulo(rcm);		
									cpId.setFilamodulo(rcm.getId().getFilamodulo());
									cpId.setCodconceptoppalmod(rcm.getConceptoPpalModulo().getCodconceptoppalmod());
									cpId.setCodriesgocubierto(rcm.getRiesgoCubierto().getId().getCodriesgocubierto());
								} else {
									cp.setRiesgoCubiertoModulo(new RiesgoCubiertoModulo());
									cpId.setFilamodulo(new BigDecimal(999));
									cpId.setCodconceptoppalmod(new BigDecimal(999));
									cpId.setCodriesgocubierto(new BigDecimal(999));
								}
								cpId.setCodconcepto(new BigDecimal(999));
								cpId.setFilacomparativa(new BigDecimal(99));
								cpId.setCodvalor(new BigDecimal(-2));
							}
							cp.setId(cpId);
						}
						
						if (aplicaCaractExpl
								&& !poliza
										.getEstadoPoliza()
										.getIdestado()
										.equals(Constants.ESTADO_POLIZA_DEFINITIVA)
								&& !poliza
										.getEstadoPoliza()
										.getIdestado()
										.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)) {
							// DAA 21/03/13
						
							logger.debug("Es Validacion, calculamos Caracteristica de la Explotacion");
								
							BigDecimal caractExlp = caracteristicaExplotacionManager
									.calcularCaractExplotacion(poliza, realPath);	
	
							ComparativaPoliza compP = comparativaDao
									.guardarComparatCaracExplot(cp, poliza,
											caractExlp);
							poliza.getComparativaPolizas().add(compP);
						}

						boolean esCpFinanciada = esComparativaFinanciada(cp,
								finanCodModSelec, finanFilaSelec,
								finanIdModSelec);
						cp.setEsFinanciada(esCpFinanciada);
						// Se llama a la generacion del XML de la Poliza
						// y se almacena en resultado
						if (webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
							idEnvio = generateAndSaveXMLPoliza(poliza, cp, webServiceToCall, true,
									getComsPctCalculadoComp(cp.getId().getIdComparativa()));
						} else {
							idEnvio = generateAndSaveXMLPoliza(poliza, cp, webServiceToCall, false, null);
						}
						
						logger.debug("Resultado de la llamada al pl: "
								+ idEnvio);
						// Se comprueba el resultado de la llamada al PLSQL
						if (idEnvio == null) {
							throw new WebServiceException(
									"Error en la llamada a los Servicios Web. Imposible Obtener la Poliza de Base de Datos...");
						}

						// *********************
						// VALIDACION
						// *********************
						GregorianCalendar fechaFin = new GregorianCalendar();
						Long tiempo = fechaFin.getTimeInMillis()
								- fechaInicio.getTimeInMillis();
						logger.debug("Tiempo desde que se inicia la generacion del xml hasta que se envia a Agroseguro: "
								+ tiempo + " milisegundos.");

						// Llamamos al servicio Web de validacion
						if (webServiceToCall.equals(Constants.WS_VALIDACION)) {
							fechaInicio = new GregorianCalendar();

							try {
								// throw new Exception("sdsfs");
								acuseRecibo = this.validar(idEnvio,
										poliza.getIdpoliza(), realPath,
										parametro.getValidacion(), cp);
							} catch (Exception e) {
								logger.error(
										"Ha ocurrido un error inesperado al llamar al SW de validacion",
										e);
								mv = HTMLUtils.errorMessage(ERROR_SERVICIO
										+ webServiceToCall,
										"Ocurrio un error inesperado al llamar a los servicios Web: "
												+ e.getMessage());
								return mv;
							}

							fechaFin = new GregorianCalendar();
							tiempo = fechaFin.getTimeInMillis()
									- fechaInicio.getTimeInMillis();
							logger.debug("Tiempo que dura la peticion al servicio de validacion de Agroseguro: "
									+ tiempo + " milisegundos.");
							logger.debug("Llamada al servicio de validacion REALIZADA");
							String cpId = "" + cp.getId().getIdpoliza() + cp.getId().getLineaseguroid() + cp.getId().getCodmodulo()
									+ cp.getId().getIdComparativa();
							VistaImportes vistaImportes = setDatosComparativa(cp, esGanado);
							String coberturasHtml;
							if (esGanado) {								
								coberturasHtml = getTablaComparativaGanadoSeleccionadas(
										cp, listComparativasPoliza, poliza,
										realPath, usuario);
							} else {
								/** Pet. 63485 ** MODIF TAM (23.07.2020) ** Inicio **/
								/** Montamos la tabla de coberturas de Agricolas de la misma forma que para
								 * Ganado */
								coberturasHtml = getTablaComparativaAgriSeleccionadas(
										cp, listComparativasPoliza, poliza,
										realPath, usuario);
								// UNIFICACION VALIDACION/CALCULO/PASO DEFINTIVA
								// PARA MOSTRAR LAS COMPARATIVAS
								acusePolizaHolder.put(cp.getId(), resultadoWS);
								
								generarComparativa(idEnvio, poliza, cp, vistaImportes, realPath, usuario);
							}
							
							logger.debug(coberturasHtml);
							cabeceraComparativaHTML.put(cpId, coberturasHtml);							
							cabeceraComparativaHTML.put(cpId + "modulo", vistaImportes.getIdModulo());
							cabeceraComparativaHTML.put(cpId + "descModulo", vistaImportes.getDescModulo());
						}
						// *********************
						// CALCULO
						// *********************
						else if (webServiceToCall.equals(Constants.WS_CALCULO)) {
							// Llamamos al servicio Web de Calculo si la
							// comparativa poliza es calculable
							// Devuelve dos XML, el Acuse de Recibo y el
							// Resultado del calculo
							String calculables = ((String[]) parameters
									.get("validComps"))[0];
							String arrCalculables[] = null;
							// En el JSP errorValidaciones, por cada comparativa
							// se forma un hidden con el nombre
							// calculable + idPoliza + codModulo +
							// filaComparativa para las comparativas en las que
							// se permite.
							// Para gandao se anhade al id el idmodulo para
							// diferenciar las comparatica
							// llamar a calculo
							String cpId = "" + cp.getId().getIdpoliza() + cp.getId().getLineaseguroid() + cp.getId().getCodmodulo()
									+ cp.getId().getIdComparativa();
							String currentCalculable = "calculable" + cpId;	
							if (calculables != null) {
								arrCalculables = calculables.split("\\|");
								for (int i = 0; i < arrCalculables.length; i++) {
									if (currentCalculable
											.equals(arrCalculables[i])) {
										Map<String, Object> wsreturn = callWSCalculo(
												idEnvio, poliza, cp, realPath);
										acuseRecibo = (es.agroseguro.acuseRecibo.AcuseRecibo) wsreturn.get("acuse");
										
										Map<Character, ComsPctCalculado> comsPctCalculado = null;
										// MPM - Se comprueba si la respuesta del calculo es del esquema unificado o no
										if (wsreturn.get(CALCULO) instanceof es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) {
											logger.debug("Respuesta de calculo del esquema antiguo");
											polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) wsreturn
													.get(CALCULO);
											logger.debug("Resultado del calculo: "
													+ polizaCalculo.toString());											
										} else if (wsreturn.get(CALCULO) instanceof es.agroseguro.distribucionCostesSeguro.Poliza) {
											logger.debug("Respuesta de calculo del esquema unificado");
											plzCalculoUnificado = (es.agroseguro.distribucionCostesSeguro.Poliza) wsreturn
													.get(CALCULO);
											logger.debug("Resultado del calculo: "
													+ plzCalculoUnificado
															.toString());
											com.rsi.agp.dao.tables.admin.Colectivo colectivo;
											try {
												colectivo = (com.rsi.agp.dao.tables.admin.Colectivo) seleccionPolizaDao
														.get(com.rsi.agp.dao.tables.admin.Colectivo.class, poliza.getColectivo().getId());
											} catch (DAOException e1) {
												throw new ValidacionPolizaException(e1);
											}
											logger.debug("Entidad colectivo: " + colectivo.getTomador().getEntidad().getCodentidad());
											logger.debug("Entidad: " + colectivo.getSubentidadMediadora().getId().getCodentidad());
											logger.debug("Subentidad: " + colectivo.getSubentidadMediadora().getId().getCodsubentidad());
											logger.debug("CosteGrupoNegocioArray: " + plzCalculoUnificado.getDatosCalculo().getCostePoliza().getCosteGrupoNegocioArray());
											comsPctCalculado = this.mtoComisionesRenovService.getComisRenovParaCalculo(
													poliza.getLinea().getCodplan(), poliza.getLinea().getCodlinea(),
													cp.getId().getCodmodulo(), cp.getId().getIdComparativa(),
													colectivo.getTomador().getEntidad().getCodentidad(),
													colectivo.getSubentidadMediadora().getId().getCodentidad(),
													colectivo.getSubentidadMediadora().getId().getCodsubentidad(),
													plzCalculoUnificado.getDatosCalculo().getCostePoliza().getCosteGrupoNegocioArray());
										}
										
										// REGENERAMOS EL XML PARA APLICAR EL POSIBLE DESCUENTO/RECARGO Y EL % DE COMISION POR E-S MED
										idEnvio = generateAndSaveXMLPoliza(poliza, cp, webServiceToCall, true, comsPctCalculado);
										
										wsreturn = callWSCalculo(idEnvio, poliza, cp, realPath);
										acuseRecibo = (es.agroseguro.acuseRecibo.AcuseRecibo) wsreturn.get("acuse");
										if (wsreturn.get(CALCULO) instanceof es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) {
											logger.debug("Respuesta de calculo del esquema antiguo");
											polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) wsreturn
													.get(CALCULO);
											logger.debug("Resultado del calculo: "
													+ polizaCalculo.toString());											
										} else if (wsreturn.get(CALCULO) instanceof es.agroseguro.distribucionCostesSeguro.Poliza) {
											logger.debug("Respuesta de calculo del esquema unificado");
											plzCalculoUnificado = (es.agroseguro.distribucionCostesSeguro.Poliza) wsreturn
													.get(CALCULO);
											logger.debug("Resultado del calculo: "
													+ plzCalculoUnificado
															.toString());
										}
										
										if (FINANCIACION
												.equals(origenllamada)) {
											if (cp.getId()
													.getIdComparativa()
													.compareTo(
															new Long(
																	finanIdModSelec)) == 0
													&& cp.getId()
															.getFilacomparativa()
															.compareTo(
																	new BigDecimal(
																			finanFilaSelec)) == 0) {
												respuestaServFinanciacion = this
														.callWSFinanciacion(
																realPath,
																parameters,
																poliza,
																finanCodModSelec,
																new BigDecimal(
																		finanFilaSelec),
																usuario.getCodusuario());
												if (respuestaServFinanciacion != null
														&& respuestaServFinanciacion
																.containsKey(FINANCIACION)) {
													financiacion = (es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument) respuestaServFinanciacion
															.get(FINANCIACION);
													this.actualizaDistribCostesConFinanciacion(
															idEnvio,
															poliza,
															cp.getId()
																	.getCodmodulo(),
															cp.getId()
																	.getFilacomparativa(),
															financiacion,
															parameters,
															cp.getId()
																	.getIdComparativa());
												}
											} 
										} else {

											/* Pet. 57626 ** MODIF TAM (25.05.2020 */
											/* Con los desarrollos de esta peticion tanto las polizas
											 * de Ganado como las agricolas van con Formato Unificado
											 */
											this.actualizaDistribCostesUnificado(
													plzCalculoUnificado,
													poliza,
													cp.getId()
															.getCodmodulo(),
													cp.getId()
															.getFilacomparativa(),
													cp.getId()
															.getIdComparativa());
										}
									}
								}
							}
						}
						// *********************
						// CONFIRMACION
						// *********************
						else if (webServiceToCall
								.equals(Constants.WS_CONFIRMACION)) { // Llamamos
																		// al
																		// servicio
																		// Web
																		// de
																		// Confirmacion
							// Pet. 22208 ** MODIF TAM (05.03.2018) ** Inicio //
							// Hay que incluir el tratamiento de la recepcion de
							// la confirmacion

							try {
								acuseRecibo = this.confirmar(idEnvio,
										poliza.getIdpoliza(), realPath);
								acuseRecibo.getDocumentoArray(0).getEstado();

							} catch (Exception e) {

								// **** Actualizamos el estado de la poliza con
								// error ****.
								EstadoPoliza estado;
								Poliza p = (Poliza) seleccionPolizaDao.get(
										Poliza.class, poliza.getIdpoliza());

								// RECHAZADA POR AGROSEGUROS
								// Actualizamos el estado de la poliza a estado
								// ESTADO_POLIZA_ENVIADA_ERRONEA
								estado = new EstadoPoliza(
										Constants.ESTADO_POLIZA_ENVIADA_ERRONEA);
								p.setEstadoPoliza(estado); // set estado poliza
								seleccionPolizaDao.evict(p);
								
								seleccionPolizaDao.saveOrUpdate(p);

								// INSERTAMOS NUEVO REGISTRO EN LA TABLA DE
								// HISTORICO.
								Set<PagoPoliza> pagos = p.getPagoPolizas();

								BigDecimal tipoPago = null;
								Date fechaPrimerPago = null;
								Date fechaSegundoPago = null;
								BigDecimal pctPrimerPago = null;
								BigDecimal pctSegundoPago = null;
								for (PagoPoliza pago : pagos) {
									if (pago.getTipoPago() != null)
										tipoPago = pago.getTipoPago();
									if (pago.getFecha() != null)
										fechaPrimerPago = pago.getFecha();
									if (pago.getPctprimerpago() != null)
										pctPrimerPago = pago.getPctprimerpago();
									if (pago.getFechasegundopago() != null)
										fechaSegundoPago = pago
												.getFechasegundopago();
									if (pago.getPctsegundopago() != null)
										pctSegundoPago = pago
												.getPctsegundopago();
								}

								historicoEstadosManager
										.insertaEstadoPoliza(
												p.getIdpoliza(),
												usuario.getCodusuario(),
												Constants.ESTADO_POLIZA_ENVIADA_ERRONEA,
												tipoPago, fechaPrimerPago,
												pctPrimerPago,
												fechaSegundoPago,
												pctSegundoPago);

								logger.error(
										"Ha ocurrido un error inesperado al llamar al SW de validacion",
										e);
								mv = HTMLUtils.errorMessage(ERROR_SERVICIO
										+ webServiceToCall,
										"Ocurrio un error inesperado al llamar a los servicios Web: "
												+ e.getMessage());
								return mv;
							}

							acuseRecibo_swConfirmacion = acuseRecibo;
							// Pet. 22208 ** MODIF TAM (05.03.2018) ** Fin //
						}

						if (acuseRecibo != null) {
							logger.debug("Acuse recibido: "
									+ acuseRecibo.toString());
							// Si el documento del acuse de recibo tiene estado
							// 2 (rechazado), no se permite llamar al servicio
							// de calculo
							// Hacemos el Array de Documentos, aunque segun la
							// documentacion, siempre viene solo 1
							for (int i = 0; i < acuseRecibo
									.getDocumentosRecibidos(); i++) {
								Documento documentoRecibido = acuseRecibo
										.getDocumentoArray(i);

								// Si el Acuse de Recibo es de una validacion, y
								// no tiene errores de tramite (el estado es 1)
								// se llama al servicio de calculo, se actualiza
								// la distribucion de costes, y se pasa a
								// grabacion provisional. Esto se puede hacer
								// asi porque se supone que la primera vez que
								// se
								// pase por aqui, siempre va a haber errores de
								// tramite (precio, etc..)
								// PASAREMOS POR AQUI CUANDO VENGAMOS POR LA
								// PANTALLA DE PAGO.
								if (webServiceToCall
										.equals(Constants.WS_VALIDACION)
										&& documentoRecibido.getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO
										&& "pago".equals(origenllamada)) {

									Map<String, Object> wsreturn = callWSCalculo(
											idEnvio, poliza, cp, realPath);
									acuseRecibo = (es.agroseguro.acuseRecibo.AcuseRecibo) wsreturn
											.get("acuse");
									polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) wsreturn
											.get(CALCULO);
									webServiceToCall = Constants.WS_CALCULO;
								} else {
									resultadoWS = new ResultadoWS();
									// No se permite calcular solo si el estado
									// es RECHAZADO
									resultadoWS
											.setCalculable((documentoRecibido
													.getEstado() != Constants.ACUSE_RECIBO_ESTADO_RECHAZADO));
									resultadoWS.setAcuseRecibo(acuseRecibo);

									// Por cada comparativaPoliza, se guarda su
									// acuse de recibo y si es llamada a
									// Calculo, su polizaCalculo
									acusePolizaHolder.put(cp.getId(),
											resultadoWS);
									// final Usuario usuario = (Usuario)
									// request.getSession().getAttribute("usuario");
									if (webServiceToCall
											.equals(Constants.WS_CALCULO)
											&& (polizaCalculo != null || plzCalculoUnificado != null)) {
										resultadoWS
												.setPolizaCalculo(polizaCalculo);
										resultadoWS
												.setPlzCalculoUnificado(plzCalculoUnificado);

										fluxCondensatorHolder
												.add(generateDataForImportes(
														idEnvio,
														acusePolizaHolder,
														poliza, cp, usuario,
														financiacion, realPath));
										// Aunque el origen llamada sea pago, el
										// estado del documento no es correcto
										// por lo que se vacia el origenllamada
										// para que redirija a importes en lugar
										// de a grabacionPoliza
										financiacion = null;
										if ("pago".equals(origenllamada))
											origenllamada = "pago";
									}
								}
							}
						}
						calledComparativas.add(cp.getId().getIdComparativa());
					}
					acuseRecibo = null;					
				} // End For ComparativaPoliza

				BigDecimal codPlan = poliza.getLinea().getCodplan();
				BigDecimal codLinea = poliza.getLinea().getCodlinea();
				BigDecimal codEntidad = poliza.getAsegurado().getEntidad()
						.getCodentidad();

				// Recorremos el objeto acusePolizaHolder para eliminar los
				// errores que no queremos que se muestren
				String validacion = webServiceToCall
						.equals(Constants.WS_VALIDACION) ? Constants.VALIDACION
						: "";
				WSUtils.limpiaErroresWs(acusePolizaHolder, parametro,
						polizaDao, codPlan, codLinea, codEntidad, validacion);

				if (webServiceToCall.equals(Constants.WS_VALIDACION)) {
					/* DAA 26/04/12 */
					BigDecimal tipoUsuario = usuario.getTipousuario();
					tipoUsuario = usuario.getExterno().equals(
							Constants.USUARIO_EXTERNO) ? tipoUsuario
							.add(Constants.NUMERO_DIEZ) : tipoUsuario;
					boolean usuarioPerfilCero = tipoUsuario
							.equals(Constants.PERFIL_0);
					List<ErrorWsAccion> errorWsAccionList = this
							.getErroresWsAccion(codPlan, codLinea, codEntidad,
									tipoUsuario, Constants.VALIDACION);
					Map<String, Object> condiciones = WSUtils
							.getCondicionesErroresValidacion(acusePolizaHolder,
									errorWsAccionList);
					mv = new ModelAndView(
							"moduloPolizas/webservices/erroresValidacion",
							"resultado", acusePolizaHolder);
					mv.addObject("cabeceras", cabeceraComparativaHTML);
					mv.addObject("origenllamada", origenllamada);
					mv.addObject("usuarioPerfilCero", usuarioPerfilCero);
					if (isCalculable(acusePolizaHolder)) {
						mv.addObject("operacion", "calcular");
						mv.addObject(S_ERROR_HEADER,
								condiciones.get(S_ERROR_HEADER));
						mv.addObject(B_BOTON_CALCULO,
								condiciones.get(B_BOTON_CALCULO));
						mv.addObject(B_BOTON_CORREGIR,
								condiciones.get(B_BOTON_CORREGIR));
						mv.addObject(ARR_BOTON_CALCULO,
								condiciones.get(ARR_BOTON_CALCULO));
						mv.addObject(ARR_BOTON_CORREGIR,
								condiciones.get(ARR_BOTON_CORREGIR));
						mv.addObject("keys", condiciones.get("keys"));
						mv.addObject("calcular", true);
					} else {
						mv.addObject(S_ERROR_HEADER,
								condiciones.get(S_ERROR_HEADER));
						mv.addObject(B_BOTON_CALCULO,
								condiciones.get(B_BOTON_CALCULO));
						mv.addObject(B_BOTON_CORREGIR,
								condiciones.get(B_BOTON_CORREGIR));
						mv.addObject(ARR_BOTON_CALCULO,
								condiciones.get(ARR_BOTON_CALCULO));
						mv.addObject(ARR_BOTON_CORREGIR,
								condiciones.get(ARR_BOTON_CORREGIR));
						mv.addObject("keys", condiciones.get("keys"));
						mv.addObject("calcular", false);
					}
				} else if (webServiceToCall.equals(Constants.WS_CALCULO)) {
					// final Usuario usuario = (Usuario)
					// request.getSession().getAttribute("usuario");
					Asegurado aseguradoSesion = usuario.getAsegurado();
					Long idAseg = aseguradoSesion.getId();
					mv = calcular(idEnvio, origenllamada, null, modoLectura,
							cabeceraComparativaHTML, poliza, polizaCalculo,
							plzCalculoUnificado, acusePolizaHolder,
							fluxCondensatorHolder, idAseg, aseguradoSesion,
							actualizaDistribucionCostes);
					request.getSession().setAttribute("distCoste",
							fluxCondensatorHolder);
					// guardamos las comparativas para tenerlas en la jsp de
					// importes y poder llamar de nuevo
					// al webservice de calcular

					if (respuestaServFinanciacion != null
							&& respuestaServFinanciacion.containsKey(ALERTA)) {
						// mv.addObject(respuestaServFinanciacion);
						mv.addObject(ALERTA,
								respuestaServFinanciacion.get(ALERTA));
					}

					mv.addObject("validComps",
							((String[]) parameters.get("validComps"))[0]);

					// Guardamos los valores de la primera llamada a calcular
					if (poliza.getDistribucionCoste2015s() != null
							&& poliza.getDistribucionCoste2015s().size() > 0) {
						DistribucionCoste2015 dc = poliza
								.getDistribucionCoste2015s().iterator().next();
						mv.addObject("dc", dc);
					}
				}
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
				// anhadimos el nuevo caso del SW CONFIRMACION
				else if (webServiceToCall.equals(Constants.WS_CONFIRMACION)) {

					// ***** ACTUALIZAMOS EL ESTADO DE LA POLIZA *****//
					EstadoPoliza estado;
					Poliza p = (Poliza) seleccionPolizaDao.get(Poliza.class,
							poliza.getIdpoliza());

					// ACEPTADA POR AGROSEGUROS
					if (acuseRecibo_swConfirmacion.getDocumentoArray(0)
							.getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
						// Actualizamos el estado de la poliza a
						// ESTADO_POLIZA_DEFINITIVA
						estado = new EstadoPoliza(
								Constants.ESTADO_POLIZA_DEFINITIVA);
					} else {
						// RECHAZADA POR AGROSEGUROS
						// Actualizamos el estado de la poliza a estado
						// ESTADO_POLIZA_ENVIADA_ERRONEA
						estado = new EstadoPoliza(
								Constants.ESTADO_POLIZA_ENVIADA_ERRONEA);
					}
					p.setEstadoPoliza(estado); // set estado poliza

					seleccionPolizaDao.saveOrUpdate(p);

					// INSERTAMOS NUEVO REGISTRO EN LA TABLA DE HISTORICO.
					Set<PagoPoliza> pagos = p.getPagoPolizas();

					BigDecimal tipoPago = null;
					Date fechaPrimerPago = null;
					Date fechaSegundoPago = null;
					BigDecimal pctPrimerPago = null;
					BigDecimal pctSegundoPago = null;
					for (PagoPoliza pago : pagos) {
						if (pago.getTipoPago() != null)
							tipoPago = pago.getTipoPago();
						if (pago.getFecha() != null)
							fechaPrimerPago = pago.getFecha();
						if (pago.getPctprimerpago() != null)
							pctPrimerPago = pago.getPctprimerpago();
						if (pago.getFechasegundopago() != null)
							fechaSegundoPago = pago.getFechasegundopago();
						if (pago.getPctsegundopago() != null)
							pctSegundoPago = pago.getPctsegundopago();
					}

					// ACEPTADA POR AGROSEGUROS
					if (acuseRecibo_swConfirmacion.getDocumentoArray(0)
							.getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
						historicoEstadosManager.insertaEstadoPoliza(
								p.getIdpoliza(), usuario.getCodusuario(),
								Constants.ESTADO_POLIZA_DEFINITIVA, tipoPago,
								fechaPrimerPago, pctPrimerPago,
								fechaSegundoPago, pctSegundoPago);
					} else {
						historicoEstadosManager.insertaEstadoPoliza(
								p.getIdpoliza(), usuario.getCodusuario(),
								Constants.ESTADO_POLIZA_ENVIADA_ERRONEA,
								tipoPago, fechaPrimerPago, pctPrimerPago,
								fechaSegundoPago, pctSegundoPago);

					}

					/** MODIF **/
					/*
					 * PRIMERO TENDREMOS QUE COMPROBAR QUE SE HA SELECCIONADO LA
					 * OPCION DOMICILICACION AGROSEGURO EN FORMA DE PAGO
					 */
					/*
					 * SI SE HA CONFIRMADO CORRECTAMENTE EN AGROSEGURO,
					 * ACTUALIZAMOS EL ESTADO DE LA POLIZA COMO 'PAGADA'
					 */
					if (acuseRecibo_swConfirmacion.getDocumentoArray(0).getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
						if (p.getPagoPolizas().iterator().next().getTipoPago().compareTo(new BigDecimal(2)) == 0) {
							polizaDao.actualizarPolizaPagada(p.getIdpoliza(), p
									.getEnvioAgroseguros().iterator().next()
									.getFechaEnvio());
						}
					}
					/** MODIF **/

					seleccionPolizaDao.evict(p);

					BigDecimal tipoUsuario = usuario.getTipousuario();
					tipoUsuario = usuario.getExterno().equals(
							Constants.USUARIO_EXTERNO) ? tipoUsuario
							.add(Constants.NUMERO_DIEZ) : tipoUsuario;
					boolean usuarioPerfilCero = tipoUsuario
							.equals(Constants.PERFIL_0);

					List<ErrorWsAccion> errorWsAccionList = this
							.getErroresWsAccion(codPlan, codLinea, codEntidad,
									tipoUsuario, Constants.VALIDACION);
					Map<String, Object> condiciones = WSUtils
							.getCondicionesErroresValidacion(acusePolizaHolder,
									errorWsAccionList);
					mv = new ModelAndView(
							"moduloPolizas/webservices/erroresValidacion",
							"resultado", acusePolizaHolder);

					mv.addObject("cabeceras", cabeceraComparativaHTML);
					mv.addObject("origenllamada", origenllamada);
					mv.addObject("usuarioPerfilCero", usuarioPerfilCero);
					mv.addObject("swConfirmacion", true);
					mv.addObject(S_ERROR_HEADER,
							condiciones.get(S_ERROR_HEADER));
					mv.addObject("keys", condiciones.get("keys"));
					mv.addObject("idpoliza", poliza.getIdpoliza());

					String mensaje = null;
					String alerta = null;
					if (acuseRecibo_swConfirmacion.getDocumentoArray(0)
							.getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
						mensaje = "Poliza confirmada con Agroseguro";
					} else {
						alerta = "Poliza rechazada con Agroseguro";
					}

					if (alerta != null) {
						mv.addObject(ALERTA, alerta);
					}
					if (mensaje != null) {
						mv.addObject("mensaje", mensaje);
					}
					// Pet. 22208 ** MODIF TAM (02.03.2018) ** Fin //

				}

			} catch (CalculoServiceException e) {
				logger.error("Excepcion del tipo CalculoServiceException", e);
				String[] splitedMsg = e.getMessage().split("####");
				String code = splitedMsg[0];
				String msg = splitedMsg[1];
				mv = HTMLUtils.errorMessage("Servicio Web de Calculo", msg,
						code);
			} catch (Exception e) {
				logger.error(
						"Ocurrio un error inesperado al llamar a los servicios Web.",
						e);
				mv = HTMLUtils.errorMessage(ERROR_SERVICIO + webServiceToCall,
						"Ocurrio un error inesperado al llamar a los servicios Web: "
								+ e.getMessage());
			}
		} // End If poliza
		else {
			logger.info("La BBDD devuelve una poliza nula");
			mv = HTMLUtils
					.errorMessage(ERROR_SERVICIO + webServiceToCall,
							"Error al intentar llamar a los Servicios Web: La Poliza es requerida.");
		}
		// Comprobamos el tipo de poliza de cara a su posible financiacion
		if (poliza.getTipoReferencia().equals('P')) {
			mv.addObject("esFinanciacionCpl", "false");
		} else {
			mv.addObject("esFinanciacionCpl", "true");
		}

		return mv;
	}

	@SuppressWarnings("unchecked")
	public List<ErrorWsAccion> getErroresWsAccion(BigDecimal codPlan,
			BigDecimal codLinea, BigDecimal codEntidad, BigDecimal tipoUsuario,
			String servicio) {
		ErrorWsFiltro errorWsFiltro = new ErrorWsFiltro(codPlan, codLinea,
				codEntidad, tipoUsuario, servicio);
		return errorWsAccionDao.getObjects(errorWsFiltro);
	}

	/**
	 * Realiza una llamada al servicio Web de Calculo
	 * 
	 * @param poliza
	 * @param cp
	 * @param realPath
	 * @param tipoWS
	 *            nos indica si llamar al WS_local o WS_desarrollo
	 * @return El Acuse de Recibo
	 * @throws Exception
	 */
	public Map<String, Object> callWSCalculo(Long idEnvio, Poliza poliza,
			ComparativaPoliza cp, String realPath)
			throws Exception {
		
		com.rsi.agp.dao.tables.admin.Colectivo colectivo;
		try {
			colectivo = (com.rsi.agp.dao.tables.admin.Colectivo) seleccionPolizaDao
					.get(com.rsi.agp.dao.tables.admin.Colectivo.class, poliza.getColectivo().getId());
		} catch (DAOException e1) {
			throw new ValidacionPolizaException(e1);
		}
		Map<String, Object> wsreturn = this.calculo(idEnvio, poliza.getIdpoliza(), colectivo.getPctdescuentocol(),
				realPath);

		if (wsreturn != null && wsreturn.containsKey(CALCULO)) {

			es.agroseguro.distribucionCostesSeguro.Poliza polizaCalculo = (es.agroseguro.distribucionCostesSeguro.Poliza) wsreturn
					.get(CALCULO);
			es.agroseguro.distribucionCostesSeguro.PolizaDocument polizaDocument = null;
			try {
				polizaDocument = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory.parse(polizaCalculo.getDomNode());
				
				this.grabaPolizaCalculo(polizaDocument.toString(), poliza, idEnvio);
				
			} catch (Exception e) {
				logger.error("Error al grabar el resultado del calculo del formato unificado",e);
				throw new WebServiceException("Error al Grabar el resultado de Calculo!", e);
			}
		}
		return wsreturn;
	}

	public Map<String, Object> callWSFinanciacion(String realPath,
			Map<String, Object> parameters, Poliza poliza,
			String codmodulo, BigDecimal filaSelec, String codUsuario)
			throws Exception {

		FinanciarRequest fr = this.getFinanciarRequest(parameters, poliza,
				codmodulo);
		Map<String, Object> wsreturn = this.financiar(realPath, fr);
		es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument financiacion = null;

		if (wsreturn != null && wsreturn.containsKey(FINANCIACION)) {
			financiacion = (es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument) wsreturn
					.get(FINANCIACION);

			try {
				financiacion = es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument.Factory
						.parse(financiacion.getDomNode());
				wsreturn.put(FINANCIACION, financiacion);
				wsreturn.put("financiarRequest", fr);
				historicoWSManager.grabarLlamadaFinanciacion(fr, financiacion,
						poliza, codmodulo,
						filaSelec != null ? filaSelec.intValue() : 1,
						codUsuario, null);
			} catch (Exception e) {
				logger.error("Error al grabar el resultado de la financiacion",
						e);
				throw new WebServiceException(
						"Error al grabar el resultado de la financiacion", e);
			}
		} else if (wsreturn != null && !wsreturn.containsKey(FINANCIACION)) {
			// Si el WS devuelve un error
			historicoWSManager.grabarLlamadaFinanciacion(fr, null, poliza,
					codmodulo, filaSelec != null ? filaSelec.intValue() : 1,
					codUsuario, (String) wsreturn.get(Constants.KEY_ALERTA));
		}
		return wsreturn;
	}

	private FinanciarRequest getFinanciarRequest(
			Map<String, Object> parameters, Poliza poliza, String codmodulo) {

		// Valores necesarios para pasarle al servicio
		ObjectFactory fact = new ObjectFactory();
		FinanciarRequest fr = new FinanciarRequest();
		int codlinea = poliza.getLinea().getCodlinea().intValue();// (int)
																	// parameters.get("codlinea_cf");
		int codplan = poliza.getLinea().getCodplan().intValue();
		fr.setLinea(codlinea);
		fr.setPlan(codplan);

		// Modulo
		String codModulo = null;
		if (null != codmodulo) {// formateamos el codigo del modulo segun la
								// especificacion del servicio
			codModulo = String.format("%-5s", codmodulo);
		}
		fr.setModulo(fact.createFinanciarRequestModulo(codModulo));
		// -----------------------------------------------------------------------

		String condfrac = ((String[]) parameters
				.get("condicionesFraccionamiento"))[0];

		fr.setPeriodo(fact.createFinanciarRequestPeriodo(new Integer(condfrac)));
		// String calculables = ((String[])parameters.get("validComps"))[0];
		String costeTomadorStr = ((String[]) parameters.get("costeTomador_cf"))[0];
		costeTomadorStr = costeTomadorStr.replace(".", "");
		costeTomadorStr = costeTomadorStr.replace(",", ".");
		BigDecimal costeTomador = new BigDecimal(costeTomadorStr);
		fr.setCosteTomador(costeTomador);

		String importeAvalStr = null;
		if (null != parameters.get("importeAval_txt")) {
			importeAvalStr = ((String[]) parameters.get("importeAval_txt"))[0];
		}

		if (null != importeAvalStr && !importeAvalStr.equals("")) {
			// importeAvalStr = importeAvalStr.replace(".", "");
			// importeAvalStr = importeAvalStr.replace(",", ".");
			fr.setImporteAval(fact
					.createFinanciarRequestImporteAval(new BigDecimal(
							importeAvalStr)));
		} else {
			// es.agroseguro.serviciosweb.contratacionscutilidades.Fraccionamiento
			// es.agroseguro.seguroAgrario.financiacion.impl.FraccionamientoImpl
			es.agroseguro.serviciosweb.contratacionscutilidades.Fraccionamiento frac = fact
					.createFraccionamiento();

			String impFinanStr = null;
			if (null != parameters.get("importeFinanciar_txt"))
				impFinanStr = ((String[]) parameters
						.get("importeFinanciar_txt"))[0];

			if (null != impFinanStr && !impFinanStr.equals("")) {
				// impFinanStr = impFinanStr.replace(".", "");
				// impFinanStr = impFinanStr.replace(",", ".");
				BigDecimal impFinan = new BigDecimal(impFinanStr);
				// frac.addNewFraccionamiento1().setImporte(impFinan);
				frac.setImporte(impFinan);

			}

			String pctcosteStr = null;
			if (null != parameters.get("porcentajeCosteTomador_txt"))
				pctcosteStr = ((String[]) parameters
						.get("porcentajeCosteTomador_txt"))[0];

			if (null != pctcosteStr && !pctcosteStr.equals("")) {
				// pctcosteStr = pctcosteStr.replace(".", "");
				// pctcosteStr = pctcosteStr.replace(",", ".");
				BigDecimal pctcoste = new BigDecimal(pctcosteStr);
				// frac.addNewFraccionamiento1().setPctCosteTomador(pctcoste);
				frac.setPctCosteTomador(pctcoste);

			}
			fr.setFraccionamiento1(fact
					.createFinanciarRequestFraccionamiento1(frac));

		}
		return fr;
	}

	private ModelAndView calcular(
			Long idEnvio,
			String origenllamada,
			String vezPaso,
			String modoLectura,
			Map<String, String> cabeceraComparativaHTML,
			Poliza poliza,
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo,
			es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado,
			Map<ComparativaPolizaId, ResultadoWS> acusePolizaHolder,
			Set<VistaImportes> fluxCondensatorHolder, Long idAseg,
			Asegurado aseguradoSesion, boolean actualizaDistribucionCostes)
			throws WebServiceException {

		String codModulo;
		BigDecimal filaComparativa;
		ModelAndView mv = null;
		Boolean tieneSubvenciones = false;
		Map<String, Object> params = new HashMap<String, Object>();
		Long idComparativa;
		params.put("idEnvio", idEnvio);
		try {

			if ("pasarDefinitiva".equals(origenllamada)) {
				BigDecimal importeTomadorCalculo = polizaCalculo
						.getDatosCalculo().getDistribucionCoste()
						.getCargoTomador();
				BigDecimal importePoliza = poliza.getImporte();
				tieneSubvenciones = tieneSubvenciones(fluxCondensatorHolder);

				// guardamos el resultado obtenido del servicio web
				grabaPolizaCalculo(
						es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument.Factory
								.parse(polizaCalculo.getDomNode()).toString(),
						poliza, idEnvio);
				// Al pasar a definitiva solo queda una comparativa... obtenemos
				// la fila/modulo del primer elemento del set
				ComparativaPoliza comparativa = poliza.getComparativaPolizas() != null
						&& !poliza.getComparativaPolizas().isEmpty() ? poliza
						.getComparativaPolizas().toArray(
								new ComparativaPoliza[] {})[0] : null;
				filaComparativa = comparativa != null ? comparativa.getId()
						.getFilacomparativa() : BigDecimal.valueOf(-1);
				codModulo = comparativa != null ? comparativa.getId()
						.getCodmodulo() : "";
				idComparativa = comparativa != null ? comparativa.getId()
						.getIdComparativa() : new Long(-1);
				// y volvemos a grabar la distribucion de costes por si ha
				// cambiado
				this.actualizaDistribCostes(idEnvio, poliza, codModulo,
						filaComparativa, idComparativa);

				mv = new ModelAndView("redirect:/utilidadesPoliza.html");

				mv.addObject("polizaOperacion", poliza.getIdpoliza());
				mv.addObject("resultadoValidacion", importeTomadorCalculo
						.compareTo(importePoliza) == 0 ? true : false);
				mv.addObject("operacion", "pasarDefinitivaPostValidaciones");
				mv.addObject("tieneSubvenciones", tieneSubvenciones);
				mv.addObject("idEnvio", idEnvio);
			} else if ("pago".equals(origenllamada)) {
				tieneSubvenciones = tieneSubvenciones(fluxCondensatorHolder);

				params.put("tieneSubvenciones", tieneSubvenciones);
				params.put("idpoliza", poliza.getIdpoliza());
				params.put("grProvisional", "true");
				mv = new ModelAndView("redirect:/grabacionPoliza.html")
						.addAllObjects(params);
			} else {
				tieneSubvenciones = tieneSubvenciones(fluxCondensatorHolder);
				if (Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL.toString()
						.equals(poliza.getEstadoPoliza().getIdestado()
								.toString())) {
					params.put("grProvisionalOK", "true");
				}
				if (Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.toString()
						.equals(poliza.getEstadoPoliza().getIdestado()
								.toString())
						|| Constants.ESTADO_POLIZA_ANULADA.toString().equals(
								poliza.getEstadoPoliza().getIdestado()
										.toString())
						|| Constants.ESTADO_POLIZA_DEFINITIVA.toString()
								.equals(poliza.getEstadoPoliza().getIdestado()
										.toString())) {
					params.put("grDefinitivaOK", "true");
				}

				int countImportes = fluxCondensatorHolder.size();
				params.put("countImportes", countImportes);
				params.put("tieneSubvenciones", tieneSubvenciones.toString());
				params.put("modoLectura", modoLectura);
				params.put("idEnvio", idEnvio);
				params.put("plan", poliza.getLinea().getCodplan());
				params.put("estadoPoliza", poliza.getEstadoPoliza()
						.getIdestado());
				// DAA 03/05/12
				params.put("numeroCuenta",
						AseguradoUtil.getFormattedBankAccount(poliza, true));
				params.put("numeroCuenta2",
						AseguradoUtil.getFormattedBankAccount(poliza, false));

				/* Pet. 57626 ** MODIF TAM (25.05.2020 ** Inicio **/
				/* Con los desarrollos de esta peticion tanto las polizas
				 * de Ganado como las agricolas van con Formato Unificado
				 */ 
				// Si la poliza es de ganado
				String xml = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory
							.parse(plzCalculoUnificado.getDomNode()).toString();				
				/* Pet. 57626 ** MODIF TAM (25.05.2020 ** Fin **/	

				// Guardamos el resultado obtenido del servicio web
				grabaPolizaCalculo(xml, poliza, idEnvio);

				if (actualizaDistribucionCostes) {
					// Al pasar a definitiva solo queda una comparativa...
					// obtenemos la fila/modulo del primer elemento del set
					ComparativaPoliza comparativa = poliza
							.getComparativaPolizas() != null
							&& !poliza.getComparativaPolizas().isEmpty() ? poliza
							.getComparativaPolizas().toArray(
									new ComparativaPoliza[] {})[0] : null;
					filaComparativa = comparativa != null ? comparativa.getId()
							.getFilacomparativa() : BigDecimal.valueOf(-1);
					codModulo = comparativa != null ? comparativa.getId()
							.getCodmodulo() : "";
					idComparativa = comparativa != null ? comparativa.getId()
							.getIdComparativa() : new Long(-1);
					// y volvemos a grabar la distribucion de costes por si ha
					// cambiado
					this.actualizaDistribCostes(idEnvio, poliza, codModulo,
							filaComparativa, idComparativa);
				}
				// MPM - Paso a definitiva
				Poliza polizaDefinitiva = new Poliza();
				polizaDefinitiva.setIdpoliza(poliza.getIdpoliza());
				params.put("polizaDefinitiva", polizaDefinitiva);
				// ---
				// se comprueba si la poliza tiene pago fraccionado
				params.put("isPagoFraccionado",
						pagoPolizaManager.compruebaPagoFraccionado(poliza));

				// Si la poliza pertenece a un plan >= 2015 se comprueba si se
				// muestra el boton de financiar
				if (poliza.isPlanMayorIgual2015()) {
					// Asignamos si debe mostrar el boton de financiar
					ImporteFraccionamiento impFrac = this
							.getImporteFraccionamiento(poliza.getLinea()
									.getLineaseguroid(), poliza.getColectivo()
									.getSubentidadMediadora());
					// Si hay configurado un importe de fraccionamiento para el
					// plan/linea de la poliza
					if (impFrac != null) {
						muestraFinanciar(fluxCondensatorHolder, impFrac);
					}
				}
				params.put("isLineaGanado", poliza.getLinea().isLineaGanado());
				if (vezPaso != null) {
					params.put("muestraBotonFin", "nomostrar");
				}

				params.put("dataCodlinea", poliza.getLinea().getCodlinea());
				params.put("dataCodplan", poliza.getLinea().getCodplan());
				params.put("dataNifcif", poliza.getAsegurado().getNifcif());

				mv = new ModelAndView(
						"moduloPolizas/polizas/importes/importes", "resultado",
						fluxCondensatorHolder).addAllObjects(params);
			}
		} catch (Exception e) {

			logger.error("Ocurrio un error al calcular", e);
			throw new WebServiceException("Error al calcular");

		}

		return mv;
	}

	public void muestraFinanciar(Set<VistaImportes> fluxCondensatorHolder,
			ImporteFraccionamiento impFrac) {

		for (VistaImportes fco : fluxCondensatorHolder) {

			if (fco.getImporteTomador() != null) {
				String impTomStr = fco.getImporteTomador().replace(".", "");
				impTomStr = impTomStr.replace(",", ".");
				BigDecimal impTom = new BigDecimal(impTomStr);

				// Si el importe de la poliza supera el minimo para financiar
				if (impFrac != null) {
					if (impTom.compareTo(impFrac.getImporte()) != -1) {
						fco.setMuestraBotonFinanciar(true);
						fco.setEsFraccAgr(false);
					}
				} else {
					fco.setMuestraBotonFinanciar(true);
					fco.setEsFraccAgr(false);
				}
			}
		}
	}

	public ImporteFraccionamiento getImporteFraccionamiento(Long lineaSeguroId,
			SubentidadMediadora sm) {// SubentidadMediadora
		ImporteFraccionamiento impFrac = null;
		impFrac = importesFraccDao.obtenerImporteFraccionamiento(lineaSeguroId,
				sm);
		return impFrac;
	}

	public Boolean tieneSubvenciones(Set<VistaImportes> fluxCondensatorHolder) {
		Boolean result = false;
		for (VistaImportes fco : fluxCondensatorHolder) {
			for (VistaImportesPorGrupoNegocio vign : fco
					.getVistaImportesPorGrupoNegocio()) {
				if (vign.getSubvCCAA() != null && vign.getSubvCCAA().size() > 0) {
					result = true;
				}
				if (vign.getSubvEnesa() != null
						&& vign.getSubvEnesa().size() > 0) {
					result = true;
				}
			}

		}

		return result;
	}

	/**
	 * Genera los datos a mostrar en la pantalla de importes
	 * 
	 * @param resultadoComparativas
	 * @param poliza
	 * @param ComparativaPolzia
	 * @return
	 */
	public VistaImportes generateDataForImportes(Long idEnvio,
			Map<ComparativaPolizaId, ResultadoWS> resultadoComparativas,
			com.rsi.agp.dao.tables.poliza.Poliza poliza,
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp,
			Usuario usuario, String realPath, FinanciacionDocument financiacion) {

		/**
		 * Se muestran los siguientes campos del servicio web de calculo. -Prima
		 * Comercial -Prima Neta Bonificacion/Recargo -Coste Neto -Bonificacion
		 * del Asegurado (o Recargo del asegurado): solo se mostrara la que
		 * proceda. Se mostrara el porcentaje y el importe. -Bonificacion Medida
		 * Preventiva: Se mostrara el porcentaje y el importe. -Descuento por
		 * contratacion colectiva: Se mostrara el porcentaje y el importe.
		 * -Reaseguro Consorcio -Recargo Consorcio -Subvenciones ENESA: se
		 * mostraran desglosados todos los tipos y sus importes. Y un importe
		 * total. -Subvenciones CCAA: se mostrara la descripcion de cada
		 * organismo que venga informado y los importes asociados. -Importe a
		 * cargo del Tomador: se mostrara en el pie del detalle. Este dato se
		 * muestra siempre, a modo de resumen para la comparativa.
		 * 
		 * Ademas, en cada una de las comparativas apareceran los siguientes
		 * datos: -Admite complementario: SI/NO. Este dato se obtendra de la
		 * tabla Modulos Compatibles. -Elegir modulo: marcador para indicar la
		 * opcion de contratacion que desea seleccionarse. Solamente podra
		 * marcarse una opcion.
		 */
		if (resultadoComparativas == null)
			throw new NullPointerException(
					"No se ha podido recuperar el resultado de la llamada al servicio Web de Calculo");

		// ##No unificado##
		es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo = null;
		// ##Unificado##
		es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado = null;

		// Se recupera la poliza actual
		if (poliza == null)
			throw new NullPointerException(
					"No se ha podido recuperar la poliza actual en la sesion.");
		if (poliza == null || poliza.getComparativaPolizas() == null)
			throw new NullPointerException(
					"No se ha podido recuperar la poliza actual o no tiene comparativas!");

		ResultadoWS resultadoPorComparativa = null;
		// ##No unificado##
		DatosCalculo datosCalculo = null;

		DistribucionCoste distribCostes = null;
		es.agroseguro.seguroAgrario.distribucionCoste.DistribucionCoste distribCostes1 = null;

		// MPM - Variables para el formato unificado
		es.agroseguro.distribucionCostesSeguro.DatosCalculo datosCalculoUnificado = null;
		CostePoliza costePoliza = null;
		boolean isFmtUnif = false;

		resultadoPorComparativa = resultadoComparativas.get(cp.getId());
		if (resultadoPorComparativa == null
				|| (resultadoPorComparativa.getPolizaCalculo() == null && resultadoPorComparativa
						.getPlzCalculoUnificado() == null))
			throw new NullPointerException(
					"No se ha podido recuperar el resultado de la llamada al servicio Web de Calculo");

		// Se recupera el objeto devuelto por el servicio Web
		polizaCalculo = resultadoPorComparativa.getPolizaCalculo();
		plzCalculoUnificado = resultadoPorComparativa.getPlzCalculoUnificado();

		// ##No unificado##
		if (polizaCalculo != null)
			datosCalculo = polizaCalculo.getDatosCalculo();

		// MPM - Formato unificado
		if (plzCalculoUnificado != null) {
			datosCalculoUnificado = plzCalculoUnificado.getDatosCalculo();
			isFmtUnif = true;
		}
		
		boolean esGanado = poliza.getLinea().isLineaGanado();
		
		VistaImportes vistaImportes = setDatosComparativa(cp, esGanado);

		// TMR Formulacion 29-10-14 - INICIO
		// ##No unificado##
		if (datosCalculo != null || datosCalculoUnificado != null) {
			if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == -1) {
				distribCostes = datosCalculo.getDistribucionCoste();
			} else {
				// MPM - Formato unificado
				if (isFmtUnif) {
					costePoliza = datosCalculoUnificado.getCostePoliza();
				} else {
					distribCostes1 = datosCalculo.getDistribucionCoste1();
				}
			}

		}
		// if (datosCalculoUnificado != null) {
		// costePoliza = datosCalculoUnificado.getCostePoliza();
		// //******************************************************************************************
		// MPM - Formato unificado
		// Para Polizas con linea => 2015 con el formato unificado
		if (costePoliza != null) {
			CalculoAlternativoFinanciacion caf = plzCalculoUnificado
					.getCalculoAlternativoFinanciacion();
			dc2015FmtUnificado(vistaImportes, costePoliza, caf, financiacion);
		}

		// ##No unificado##
		// Para Polizas con linea => 2015 con el formato antiguo
		if (distribCostes1 != null) {
			dc2015FmtAntiguo(vistaImportes, distribCostes1);
		}
		// Para Polizas con linea < 2015 distribCostes
		if (distribCostes != null) {
			dc2014FmtAntiguo(vistaImportes, distribCostes);
			if (datosCalculo != null) {
				vistaImportes
						.getVistaImportesPorGrupoNegocio()
						.get(0)
						.setPctDescContColectiva(
								StringUtils.formatPercent(datosCalculo
										.getPctDescuentoColectivo()));
			}
		}
		// *************************************************////
		generarComparativa(idEnvio, poliza, cp, vistaImportes, realPath, usuario);

		// Si la poliza es de agrarios se obtiene el total de produccion
		if (!esGanado) {
			String totalProduccion = "";
			totalProduccion = polizaDao.getTotalProdComparativa(cp
					.getRiesgoCubiertoModulo().getModulo().getId()
					.getCodmodulo(), poliza);
			vistaImportes.setTotalProduccion(totalProduccion);
		}

		// Si la poliza es de plan >= 2015 se cargan los datos de comisiones
		if (poliza.isPlanMayorIgual2015()) {
			vistaImportes = polizasPctComisionesManager.dameComisiones(
					vistaImportes, poliza, usuario);
			
			/*/ ACTUALIZAMOS LOS IMPORTES DE COMISION
			try {
				BigDecimal idComparativa = new BigDecimal(vistaImportes.getComparativaSeleccionada().split("\\|")[0]);
				
				this.distribucionCosteDAO.updateComsDistCoste2015(poliza.getDistribucionCoste2015s(),
						vistaImportes.getVistaImportesPorGrupoNegocio(), vistaImportes.getIdModulo(), idComparativa);
			} catch (DAOException e) {
				logger.error("Error al actualizar los importes de comision en las distribuciones de coste.");
			}*/
		}		

		return vistaImportes;

	}

	public VistaImportes generateDataForImportesByDC(
			List<DistribucionCoste2015> listDc,
			com.rsi.agp.dao.tables.poliza.Poliza poliza,
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp,
			Usuario usuario, String realPath) {

		if (listDc == null)
			throw new NullPointerException(
					"No se ha podido recuperar la distribucion de costes de la poliza");

		// Se recupera la poliza actual
		if (poliza == null)
			throw new NullPointerException(
					"No se ha podido recuperar la poliza actual en la sesion.");
		if (poliza == null || poliza.getComparativaPolizas() == null)
			throw new NullPointerException(
					"No se ha podido recuperar la poliza actual o no tiene comparativas!");

		boolean esGanado = poliza.getLinea().isLineaGanado();
		
		VistaImportes vistaImportes = setDatosComparativa(cp, esGanado);
		
		// distribucionCostes
		dc2015(vistaImportes, listDc, poliza.getLinea().isLineaGanado(),
				cp.getEsFinanciada());

		BigDecimal pctMinimoFinanciacion;
		try {
			pctMinimoFinanciacion = pagoPolizaManager.getPctMinimoFinanciacion(
					poliza.getLinea().getCodplan(), poliza.getLinea()
							.getCodlinea(), cp.getId().getCodmodulo());
			if (null != pctMinimoFinanciacion) {
				vistaImportes
						.setPctMinFinanSobreCosteTomador(pctMinimoFinanciacion
								.toString());
			}
		} catch (Exception e) {

		}

		// *************************************************////
		generarComparativa(new Long(0), poliza, cp, vistaImportes, realPath, usuario);

		// Si la poliza es de agrarios se obtiene el total de produccion
		if (!esGanado) {
			String totalProduccion = "";
			/* Defecto 30 de la P0063482 ** MODIF TAM (29.07.2021) */
			if (cp.getRiesgoCubiertoModulo() != null) {
			/* Defecto 30 de la P0063482 ** MODIF TAM (29.07.2021) Fin*/	
				totalProduccion = polizaDao.getTotalProdComparativa(cp
						.getRiesgoCubiertoModulo().getModulo().getId()
						.getCodmodulo(), poliza);
				vistaImportes.setTotalProduccion(totalProduccion);
				
			}
			
		}

		// Si la poliza es de plan >= 2015 se cargan los datos de comisiones
		if (poliza.isPlanMayorIgual2015()) {
			vistaImportes = polizasPctComisionesManager.dameComisiones(
					vistaImportes, poliza, usuario);
		}

		return vistaImportes;

	}

	private void dc2015(VistaImportes vistaImportes,
			List<DistribucionCoste2015> listDc, boolean esGanado,
			boolean esFinanciada) {
		logger.debug ("WebServicesManager - dc2015[INIT]");
		

		if (listDc != null) {
			BigDecimal ImpTomador = new BigDecimal(0);
			BigDecimal totImp = new BigDecimal(0);
			BigDecimal totalCosteTomadorAFinanciar = new BigDecimal(0);
			Boolean recargosAnteriores = false;// Los recargos de val y
												// fraccionamiento solo se deben
												// de pintar en uno de los
												// gripos de negocio de la
												// comparativa
			// Con esta viariable controlamos si ya se han asignado
			// conanterioridad.
			for (int i = 0; i < listDc.size(); i++) {
				DistribucionCoste2015 dcAux = listDc.get(i);

				VistaImportesPorGrupoNegocio gn = new VistaImportesPorGrupoNegocio();
				gn.setCodGrupoNeg(dcAux.getGrupoNegocio().toString());
				String descGn = getDesGruponegocio(gn.getCodGrupoNeg()
						.charAt(0));
				if (null != descGn)
					gn.setDescGrupNeg(descGn);

				gn.setPrimaComercial(NumberUtils.formatear(
						dcAux.getPrimacomercial(), 2));
				gn.setPrimaNeta(NumberUtils.formatear(
						dcAux.getPrimacomercialneta(), 2));
				gn.setPrimaNetaB(dcAux.getPrimacomercialneta());
				gn.setRecargoConsorcio(NumberUtils.formatear(
						dcAux.getRecargoconsorcio(), 2));
				gn.setReciboPrima(NumberUtils.formatear(dcAux.getReciboprima(),
						2));
				gn.setCosteTomador(NumberUtils.formatear(
						dcAux.getCostetomador(), 2));

				for (DistCosteSubvencion2015 distCosteSubvencion : dcAux
						.getDistCosteSubvencion2015s()) {
					if (distCosteSubvencion.getCodorganismo().equals('0')) {
						gn.addSubEnesa(
								this.getDescripcionEnesa(distCosteSubvencion
										.getCodtiposubv()),
								NumberUtils.formatear(
										distCosteSubvencion.getImportesubv(), 2));

					} else {
						gn.addSubCCAA(this.getCCAA(distCosteSubvencion
								.getCodorganismo()), NumberUtils.formatear(
								distCosteSubvencion.getImportesubv(), 2));

					}
				}

				Set<BonificacionRecargo2015> boniRecargo1 = dcAux
						.getBonificacionRecargo2015s();

				if (boniRecargo1 != null) {
					for (BonificacionRecargo2015 b : boniRecargo1) {
						gn.addBoniRecargo1(
								this.getDescBoniRecar(b.getCodigo().intValue()),
								NumberUtils.formatear(b.getImporte(), 2));
					}
				}

				if (!recargosAnteriores) {
					if (dcAux.getRecargoaval() != null) {
						gn.setRecargoAval(NumberUtils.formatear(
								dcAux.getRecargoaval(), 2));
					}
					if (dcAux.getRecargofraccionamiento() != null) {
						gn.setRecargoFraccionamiento(NumberUtils.formatear(
								dcAux.getRecargofraccionamiento(), 2));
					}
					recargosAnteriores = true;
				}
				vistaImportes.getVistaImportesPorGrupoNegocio().add(gn);

				if (esFinanciada) {
					if (esGanado) {
						ImpTomador = dcAux.getTotalcostetomador(); // Si no lo
																	// tre el
																	// servicio
																	// habria
																	// que
																	// sumarles
																	// el
																	// recargo
																	// aval y el
																	// de
																	// fraccionamiento
						totImp = (dcAux.getTotalcostetomador().subtract(dcAux
								.getRecargoaval())).subtract(dcAux
								.getRecargofraccionamiento());
					} else {
						ImpTomador = dcAux.getTotalcostetomador();
						totImp = dcAux.getCostetomador();
					}
				} else {
					if (esGanado) {
						totImp = dcAux.getTotalcostetomador();
					} else {
						totImp = dcAux.getCostetomador();
					}
				}

				if (esGanado) {
					if (null != dcAux.getTotalcostetomadorafinanciar()) {
						totalCosteTomadorAFinanciar = dcAux
								.getTotalcostetomadorafinanciar();
					} else {
						if (esFinanciada) {
							totalCosteTomadorAFinanciar = totImp;
						} else {
							totalCosteTomadorAFinanciar = dcAux
									.getTotalcostetomador();
						}
					}
				} else {
					
					/* ESC-13542 ** MODIF TAM (17.05.2021) ** Inicio */
					/* Si el valor de totalCosteTomadorAfinanciar est� informado se env�a */
					
					if (dcAux.getTotalcostetomadorafinanciar() != null) {
						totalCosteTomadorAFinanciar = dcAux.getTotalcostetomadorafinanciar();
						logger.debug ("WebServicesManager - Valor de totalCosteTomadorAFinanciar(1):"+totalCosteTomadorAFinanciar);
					}else {
						totalCosteTomadorAFinanciar = dcAux.getTotalcostetomador();
						logger.debug ("WebServicesManager - Valor de totalCosteTomadorAFinanciar(2):"+totalCosteTomadorAFinanciar);
					}
					/* ESC-13542 ** MODIF TAM (17.05.2021) ** Fin */	
				}

				// Primera fraccion del importe
				if (dcAux.getImportePagoFraccAgr() != null) {
					vistaImportes.setImportePagoFraccAgr(NumberUtils.formatear(
							dcAux.getImportePagoFraccAgr(), 2));
				}

				if (dcAux.getImportePagoFracc() != null) {
					vistaImportes.setImportePagoFracc(NumberUtils.formatear(
							dcAux.getImportePagoFracc(), 2));
				}

				if (dcAux.getPeriodoFracc() != null) {
					vistaImportes.setPeriodoFracc(dcAux.getPeriodoFracc()
							.toString());
				}

			}// fin de for

			vistaImportes.setImporteTomador(NumberUtils.formatear(totImp, 2));
			vistaImportes.setTotalCosteTomador(NumberUtils.formatear(
					ImpTomador, 2));
			vistaImportes.setTotalCosteTomadorAFinanciar(NumberUtils.formatear(
					totalCosteTomadorAFinanciar, 2));
			
			logger.debug("totImp " + totImp);
			logger.debug("ImpTomador " + ImpTomador);
		}
	}

	protected VistaImportes setDatosComparativa(com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp, boolean esGanado) {
		VistaImportes vistaImportes = new VistaImportes();
		Modulo modulo = null;
		ConceptoPpalModulo conceptoPpalModulo = null;
		if (esGanado) {
			RiesgoCubiertoModuloGanado riesgoCubiertoModuloGanado = cp.getRiesgoCubiertoModuloGanado();
			if (riesgoCubiertoModuloGanado != null) {
				conceptoPpalModulo = riesgoCubiertoModuloGanado.getConceptoPpalModulo();
				modulo = riesgoCubiertoModuloGanado.getModulo();
			}
		} else {
			RiesgoCubiertoModulo riesgoCubiertoModulo = cp.getRiesgoCubiertoModulo();
			if (riesgoCubiertoModulo != null) {
				conceptoPpalModulo = riesgoCubiertoModulo.getConceptoPpalModulo();
				modulo = riesgoCubiertoModulo.getModulo();
			}
		}
		if (conceptoPpalModulo != null) {
			vistaImportes.setConceptoPpalMod(StringUtils.nullToString(conceptoPpalModulo.getDesconceptoppalmod()));
		}
		if (modulo != null) {
			vistaImportes.setDescModulo(StringUtils.nullToString(modulo.getDesmodulo()));
			vistaImportes.setIdModulo(StringUtils.nullToString(modulo.getId().getCodmodulo()));
			vistaImportes.setAdmiteComplementario(
					modulo.getTotcomplementarios() != null && modulo.getTotcomplementarios() > 0 ? "S&iacute;" : "No");
		}
		/* ESC-14626 ** MODIF TAM (20/07/2021) ** Inicio */
		if (modulo == null) {
			// Obtiene el objeto modulo correspondiente al plan/linea y modulo indicados
			Modulo mod = getModulo(StringUtils.nullToString(cp.getId().getCodmodulo()), cp.getId().getLineaseguroid());
			
			vistaImportes.setDescModulo(StringUtils.nullToString(mod.getDesmodulo().toString()));
			vistaImportes.setIdModulo(StringUtils.nullToString(mod.getId().getCodmodulo().toString()));
		}

		/* ESC-14626 ** MODIF TAM (20/07/2021) ** Fin */
		return vistaImportes;
	}	
	
	/* ESC-14626 ** MODIF TAM (20/07/2021) ** Inicio */
	/** 
	 * Obtiene el objeto Modulo asociado al codigo y plan/linea indicados como parametro 
	 * @param codModulo 
	 * @param lineaseguroid 
	 * @return 
	 */ 
	@SuppressWarnings("unchecked") 
	public Modulo getModulo (String codModulo, Long lineaseguroid) { 
		 
		ModuloFiltro filtro = new ModuloFiltro(lineaseguroid, Constants.MODULO_POLIZA_PRINCIPAL); 
		 
		ModuloId moduloId = new ModuloId(lineaseguroid, codModulo); 
		filtro.setModuloId(moduloId); 
		 
		List<Modulo> modulos = polizaDao.getObjects(filtro); 
		 
		if (modulos != null && !modulos.isEmpty()) return modulos.get(0); 
		 
		return null; 
	} 
	/* ESC-14626 ** MODIF TAM (20/07/2021) ** Inicio */

	protected void generarComparativa(Long idEnvio, com.rsi.agp.dao.tables.poliza.Poliza poliza,
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp, VistaImportes vistaImportes, String realPath,
			Usuario usuario) {
		
		logger.debug("WebServicesManager - generarComparativa [INIT] ");

		Map<String, Object> comparativas = null;

		if (poliza.getLinea().isLineaGanado()) {

			// Obtiene el listado de comparativas de la poliza
			Set<ComparativaPoliza> comparativaPolizas = poliza.getComparativaPolizas();
			List<ComparativaPoliza> listComparativasPoliza = comparativaPolizas != null
					? Arrays.asList(comparativaPolizas.toArray(new ComparativaPoliza[] {}))
					: new ArrayList<ComparativaPoliza>();

			// Genera el html de la tabla de coberturas elegidas en la
			// comparativa
			vistaImportes.setComparativaCompleta(getTablaComparativaGanadoSeleccionadas(cp, listComparativasPoliza,
					poliza, realPath, usuario));
			// Indica el identificador de la comparativa - codigo de modulo y
			// fila de la comparativa, separados por los '|' necesarios
			// para la integracion con la pantalla de improtes
			vistaImportes.setComparativaSeleccionada(
					cp.getId().getIdComparativa() + "|" + cp.getId().getCodmodulo() + "||||||"+cp.getId().getFilacomparativa());
			vistaImportes.setIdEnvioComp(idEnvio.toString());
		} else {
			/* Pet. 63485 ** MODIF TAM (24.07.2020) ***/
			/*
			 * Si es poliza Agricola hay que buscar las comparativas devueltas por el S.web
			 */
			// Obtiene el listado de comparativas de la poliza
			Set<ComparativaPoliza> comparativaPolizas = poliza.getComparativaPolizas();
			List<ComparativaPoliza> listComparativasPoliza = comparativaPolizas != null
					? Arrays.asList(comparativaPolizas.toArray(new ComparativaPoliza[] {}))
					: new ArrayList<ComparativaPoliza>();

			// Genera el html de la tabla de coberturas elegidas en la
			// comparativa
			vistaImportes.setComparativaCompleta(
					getTablaComparativaAgriSeleccionadas(cp, listComparativasPoliza, poliza, realPath, usuario));
			// Indica el identificador de la comparativa - codigo de modulo y
			// fila de la comparativa, separados por los '|' necesarios
			// para la integracion con la pantalla de improtes
			
			/* ESC-12588 ** MODIF TAM (15/03/2021) ** Inicio */
			/* Al generar el string de comparativa seleccionado, hay que pasarle la filacomparativa con la que se ha dado de alta la 
			 * distribucion de coste y que se pasa por parametro, sino la financiacion no se genera correctamente.*/
			/* Se le pasa el valor de filacomparativa, en vez de el valor fijo "1" */
			 
			vistaImportes.setComparativaSeleccionada(
			cp.getId().getIdComparativa() + "|" + cp.getId().getCodmodulo() + "||||||"+cp.getId().getFilacomparativa());
			/* ESC-12588 ** MODIF TAM (15/03/2021) ** Fin */
			
			vistaImportes.setIdEnvioComp(idEnvio.toString());
		}

		creaComparativasUnificado(vistaImportes, cp, comparativas, poliza, idEnvio);
		logger.debug("WebServicesManager - generarComparativa [END] ");
	}

	/**
	 * Metodo unificado para pintar las comparativas de Agricola en validacion,
	 * calculo, paso a provisional y definitiva
	 * 
	 * @param vistaImportes
	 * @param cp
	 * @param comparativas
	 * @param poliza
	 * @param idEnvio
	 * @param modulo
	 */
	@SuppressWarnings("unchecked")
	public void creaComparativasUnificado(VistaImportes vistaImportes,
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp,
			Map<String, Object> comparativas,
			com.rsi.agp.dao.tables.poliza.Poliza poliza, Long idEnvio) {

		HashMap<String, List<List<VistaComparativas>>> compMod = null;
		if (comparativas != null)
			compMod = (HashMap<String, List<List<VistaComparativas>>>) comparativas
					.get("comparativa");

		if (compMod != null) {

			String radioValue = "";
			String formattedReturn = "";

			BigDecimal filaComparativa = cp.getId().getFilacomparativa();

			String descripcionModulo = compMod.keySet().iterator().next();
			List<VistaComparativas> comparativa = compMod
					.get(descripcionModulo).get(filaComparativa.intValue() - 1);

			boolean todosElementosComparativaMenosDos = true;
			/*
			 * AMG logger.debug("# VISTA COMPARATIVAS SIN ORDENAR #"); for
			 * (VistaComparativas comp:comparativa){
			 * logger.debug("CPM: "+comp.getId().getDesconceptoppalmod()
			 * +" RCE: "+ comp.getId().getDesriesgocubierto()); }
			 * Collections.sort(comparativa, new VistaComparativaComparator());
			 * logger.debug("# VISTA COMPARATIVAS ORDENADAS #"); for
			 * (VistaComparativas comp:comparativa){
			 * logger.debug("CPM: "+comp.getId().getDesconceptoppalmod()
			 * +" RCE: "+ comp.getId().getDesriesgocubierto()); }
			 */
			for (int i = 0; i < comparativa.size(); i++) {

				VistaComparativas vistaComparativa = comparativa.get(i);
				if (poliza.getLinea().isLineaGanado()) {
					radioValue = radioValue
							+ vistaComparativa.getId().getIdComparativa() + "|";
				} else {
					radioValue = radioValue + cp.getId().getIdComparativa()
							+ "|";
				}
				radioValue = radioValue + cp.getId().getCodmodulo() + "|"
						+ vistaComparativa.getId().getFilamodulo() + "|"
						+ vistaComparativa.getId().getCodconceptoppalmod()
						+ "|" + vistaComparativa.getId().getCodriesgocubierto()
						+ "|" + vistaComparativa.getId().getCodconcepto() + "|"
						+ vistaComparativa.getId().getCodvalor() + "|"
						+ filaComparativa + "|"
						+ vistaComparativa.getId().getDesvalor() + ";";
				// logger.debug("codvalor: "+vistaComparativa.getId().getCodvalor().intValue()+
				// " valor: "+vistaComparativa.getId().getDesvalor());
				if (vistaComparativa.getId().getCodvalor().intValue() != -2) {

					todosElementosComparativaMenosDos = false;

					formattedReturn += "<tr>";
					formattedReturn += TD_LITERAL_BORDE
							+ vistaComparativa.getId().getDesconceptoppalmod()
							+ NBSP_TD;
					formattedReturn += "<td class='literalborde' width='26%'>"
							+ vistaComparativa.getId().getDesriesgocubierto()
							+ NBSP_TD;
					formattedReturn += TD_LITERAL_BORDE
							+ StringUtils.nullToString(vistaComparativa.getId()
									.getNomconcepto());

					Iterator<VistaComparativas> iterator = vistaComparativa
							.getFilasVinculadas().iterator();

					while (iterator.hasNext()) {

						VistaComparativas filaVinculada = iterator.next();
						formattedReturn += "</br>"
								+ StringUtils.nullToString(filaVinculada
										.getId().getNomconcepto());

					}

					formattedReturn += NBSP_TD;
					formattedReturn += TD_LITERAL_BORDE
							+ StringUtils.nullToString(vistaComparativa.getId()
									.getDesvalor());
					// ini sacamos la descripcion del valor de los vinculados
					Iterator<VistaComparativas> iterator2 = vistaComparativa
							.getFilasVinculadas().iterator();

					while (iterator2.hasNext()) {

						VistaComparativas filaVinculada = iterator2.next();
						formattedReturn += "&nbsp;</br>"
								+ StringUtils.nullToString(filaVinculada
										.getId().getDesvalor());

					}

					formattedReturn += NBSP_TD;
					// fin
				}
			}// for

			if (todosElementosComparativaMenosDos) {

				formattedReturn += "<tr>";
				formattedReturn += "<td class='literalborde' colspan='4'>Sin riesgos cubiertos elegibles</td>";

			}

			vistaImportes.setComparativaCompleta(formattedReturn);
			vistaImportes.setComparativaSeleccionada(radioValue);
			if (idEnvio != null)
				vistaImportes.setIdEnvioComp(idEnvio.toString());
		}

	}

	private void dc2014FmtAntiguo(VistaImportes vistaImportes,
			DistribucionCoste distribCostes) {
		BonificacionAsegurado bonifAseg;
		RecargoAsegurado recargoAseg;
		Consorcio consorcio;
		SubvencionCCAA[] subCCAA;
		SubvencionEnesa[] subEnesa;
		VistaImportesPorGrupoNegocio viGn = new VistaImportesPorGrupoNegocio();
		viGn.setCodGrupoNeg("1");
		String descGn = getDesGruponegocio('1');
		if (null != descGn)
			viGn.setDescGrupNeg(descGn);

		viGn.setPrimaComercial(NumberUtils.formatear(
				distribCostes.getPrimaComercial(), 2));
		viGn.setPrimaNeta(NumberUtils.formatear(distribCostes.getPrimaNeta(), 2));
		viGn.setCosteNeto(NumberUtils.formatear(distribCostes.getCosteNeto(), 2));
		viGn.setBonifMedidaPreventiva(NumberUtils.formatear(distribCostes
				.getBonificacionMedidasPreventivas().getBonifMedPreventivas(),
				2));
		viGn.setDescuentoContColectiva(NumberUtils.formatear(distribCostes
				.getDescuento().getDescuentoColectivo(), 2));

		bonifAseg = distribCostes.getBonificacionAsegurado();
		recargoAseg = distribCostes.getRecargoAsegurado();
		consorcio = distribCostes.getConsorcio();
		subCCAA = distribCostes.getSubvencionCCAAArray();
		subEnesa = distribCostes.getSubvencionEnesaArray();
		if (bonifAseg != null) {
			viGn.setBonifAsegurado(NumberUtils.formatear(
					bonifAseg.getBonifAsegurado(), 2));
			viGn.setPctBonifAsegurado(StringUtils.formatPercent(bonifAseg
					.getPctBonifAsegurado()));
		}
		if (recargoAseg != null) {
			viGn.setRecargoAsegurado(NumberUtils.formatear(
					recargoAseg.getRecargoAsegurado(), 2));
			viGn.setPctRecargoAsegurado(NumberUtils.formatear(
					recargoAseg.getPctRecargoAsegurado(), 2));
		}
		if (consorcio != null) {
			viGn.setConsorcioReaseguro(NumberUtils.formatear(
					consorcio.getReaseguro(), 2));
			viGn.setConsorcioRecargo(NumberUtils.formatear(
					consorcio.getRecargo(), 2));
		}
		if (subCCAA != null && subCCAA.length > 0) {
			Map<String, BigDecimal> ccaa = new HashMap<String, BigDecimal>();
			for (int i = 0; i < subCCAA.length; i++) {
				ccaa.put(subCCAA[i].getCodigoOrganismo(), new BigDecimal(0));
			}
			for (int i = 0; i < subCCAA.length; i++) {
				ccaa.put(
						subCCAA[i].getCodigoOrganismo(),
						ccaa.get(subCCAA[i].getCodigoOrganismo()).add(
								subCCAA[i].getSubvencionCA()));
			}
			Iterator<Map.Entry<String, BigDecimal>> it = ccaa.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, BigDecimal> e = (Map.Entry<String, BigDecimal>) it
						.next();
				String key = e.getKey();
				BigDecimal valor = e.getValue();

				viGn.addSubCCAA(this.getCCAA(key.charAt(0)),
						NumberUtils.formatear(valor, 2));
			}

		}
		if (subEnesa != null && subEnesa.length > 0) {
			for (int i = 0; i < subEnesa.length; i++) {
				viGn.addSubEnesa(this.getDescripcionEnesa(new BigDecimal(
						subEnesa[i].getTipo())), NumberUtils.formatear(
						subEnesa[i].getSubvencionEnesa(), 2));

			}
		}
		vistaImportes.setImporteTomador(NumberUtils.formatear(
				distribCostes.getCargoTomador(), 2));
		vistaImportes.setTotalCosteTomador(NumberUtils.formatear(
				distribCostes.getCargoTomador(), 2));
		vistaImportes.getVistaImportesPorGrupoNegocio().add(viGn);

	}

	/**
	 * Rellena el objeto utilizado para mostrar el resultado del calculo en la
	 * pantalla a partir del objeto devuelto para polizas con el formato
	 * unificado
	 * 
	 * @param fluxCondensatorObject
	 * @param costePoliza
	 */
	private void dc2015FmtUnificado(VistaImportes vistaImportes,
			CostePoliza costePoliza, CalculoAlternativoFinanciacion caf,
			FinanciacionDocument financiacionDoc) {

		CosteGrupoNegocio costeGrupoNegocio = null;
		// es.agroseguro.contratacion.costePoliza.Financiacion financiacion =
		// null;

		if (costePoliza != null) {
			CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza
					.getCosteGrupoNegocioArray();
			if (costeGrupoNegocioArray != null) {
				int j = 0;
				for (CosteGrupoNegocio costeGrupoNegocioFor : costeGrupoNegocioArray) {
					j++;
					costeGrupoNegocio = costeGrupoNegocioFor;
					VistaImportesPorGrupoNegocio gn = new VistaImportesPorGrupoNegocio();
					gn.setCodGrupoNeg(costeGrupoNegocio.getGrupoNegocio());
					String descGn = getDesGruponegocio(gn.getCodGrupoNeg()
							.charAt(0));
					if (null != descGn)
						gn.setDescGrupNeg(descGn);

					gn.setPrimaComercial(NumberUtils.formatear(
							costeGrupoNegocio.getPrimaComercial(), 2));
					gn.setPrimaNeta(NumberUtils.formatear(
							costeGrupoNegocio.getPrimaComercialNeta(), 2));
					gn.setPrimaNetaB(costeGrupoNegocio.getPrimaComercialNeta());
					gn.setRecargoConsorcio(NumberUtils.formatear(
							costeGrupoNegocio.getRecargoConsorcio(), 2));
					gn.setReciboPrima(NumberUtils.formatear(
							costeGrupoNegocio.getReciboPrima(), 2));
					gn.setCosteTomador(NumberUtils.formatear(
							costeGrupoNegocio.getCosteTomador(), 2));

					// Subvenciones de Enesa
					for (es.agroseguro.contratacion.costePoliza.SubvencionEnesa subvencionEnesa : costeGrupoNegocio
							.getSubvencionEnesaArray()) {
						gn.addSubEnesa(this.getDescripcionEnesa(new BigDecimal(
								subvencionEnesa.getTipo())), NumberUtils
								.formatear(subvencionEnesa.getImporte(), 2));
					}

					// Subvenciones de CCAA
					es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subvencionCCAAArray = costeGrupoNegocio
							.getSubvencionCCAAArray();

					if (subvencionCCAAArray != null
							&& subvencionCCAAArray.length > 0) {
						Map<String, BigDecimal> ccaa = new HashMap<String, BigDecimal>();
						for (int i = 0; i < subvencionCCAAArray.length; i++) {
							ccaa.put(
									subvencionCCAAArray[i].getCodigoOrganismo(),
									new BigDecimal(0));
						}
						for (int i = 0; i < subvencionCCAAArray.length; i++) {
							ccaa.put(
									subvencionCCAAArray[i].getCodigoOrganismo(),
									ccaa.get(
											subvencionCCAAArray[i]
													.getCodigoOrganismo())
											.add(subvencionCCAAArray[i]
													.getImporte()));
						}
						Iterator<Map.Entry<String, BigDecimal>> it = ccaa
								.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, BigDecimal> e = (Map.Entry<String, BigDecimal>) it
									.next();
							String key = e.getKey();
							BigDecimal valor = e.getValue();

							gn.addSubCCAA(this.getCCAA(key.charAt(0)),
									NumberUtils.formatear(valor, 2));
						}

					}

					// Bonificaciones y recargos
					for (BonificacionRecargo bonificacionRecargo : costeGrupoNegocio
							.getBonificacionRecargoArray()) {
						gn.addBoniRecargo1(this
								.getDescBoniRecar(bonificacionRecargo
										.getCodigo()), NumberUtils.formatear(
								bonificacionRecargo.getImporte(), 2));
					}

					if (null != financiacionDoc) {
						if (j == 1) {
							gn.setRecargoAval(financiacionDoc.getFinanciacion()
									.getPeriodoArray(0).getDistribucionCoste()
									.getRecargoAval().toString());
							gn.setRecargoFraccionamiento(financiacionDoc
									.getFinanciacion().getPeriodoArray(0)
									.getDistribucionCoste()
									.getRecargoFraccionamiento().toString());
							// gn.setCosteTomador(financiacionDoc.getFinanciacion().getCosteTomador().toString());
						}
					}

					vistaImportes.getVistaImportesPorGrupoNegocio().add(gn);
				}
			}
		}
		if (null != caf) {
			BigDecimal totalCosteTomadorAFinanciar = caf
					.getTotalCosteTomadorAFinanciar();
			vistaImportes.setTotalCosteTomador(NumberUtils.formatear(
					totalCosteTomadorAFinanciar, 2));
			vistaImportes.setTotalCosteTomadorAFinanciar(NumberUtils.formatear(
					totalCosteTomadorAFinanciar, 2));
		} else if (costePoliza != null) {
			vistaImportes.setTotalCosteTomador(NumberUtils.formatear(
					costePoliza.getTotalCosteTomador(), 2));
			vistaImportes.setTotalCosteTomadorAFinanciar(NumberUtils.formatear(
					costePoliza.getTotalCosteTomador(), 2));
		}
		if (costePoliza != null)
			vistaImportes.setImporteTomador(NumberUtils.formatear(
					costePoliza.getTotalCosteTomador(), 2));

		if (null != financiacionDoc) {
			vistaImportes.setTotalCosteTomador(NumberUtils.formatear(
					financiacionDoc.getFinanciacion().getPeriodoArray(0)
							.getDistribucionCoste().getTotalCosteTomador(), 2));
			vistaImportes.setTotalCosteTomadorAFinanciar(NumberUtils.formatear(
					financiacionDoc.getFinanciacion().getCosteTomador(), 2));
		}

	}

	private void dc2015FmtAntiguo(
			VistaImportes vistaImportes,
			es.agroseguro.seguroAgrario.distribucionCoste.DistribucionCoste distribCostes1) {

		es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo[] boniRecargo1;
		es.agroseguro.seguroAgrario.distribucionCoste.SubvencionCCAA[] subCCAA1;
		es.agroseguro.seguroAgrario.distribucionCoste.SubvencionEnesa[] subEnesa1;
		VistaImportesPorGrupoNegocio viGn = new VistaImportesPorGrupoNegocio();
		viGn.setCodGrupoNeg("1");
		String descGn = getDesGruponegocio('1');
		if (null != descGn)
			viGn.setDescGrupNeg(descGn);

		viGn.setPrimaComercial(NumberUtils.formatear(
				distribCostes1.getPrimaComercial(), 2));
		viGn.setPrimaNeta(NumberUtils.formatear(
				distribCostes1.getPrimaComercialNeta(), 2));
		viGn.setPrimaNetaB(distribCostes1.getPrimaComercialNeta());
		viGn.setRecargoConsorcio(NumberUtils.formatear(
				distribCostes1.getRecargoConsorcio(), 2));
		viGn.setReciboPrima(NumberUtils.formatear(
				distribCostes1.getReciboPrima(), 2));
		viGn.setCosteTomador(NumberUtils.formatear(
				distribCostes1.getCosteTomador(), 2));
		vistaImportes.setImporteTomador(NumberUtils.formatear(
				distribCostes1.getCosteTomador(), 2));
		vistaImportes.setTotalCosteTomador(NumberUtils.formatear(
				distribCostes1.getTotalCosteTomador(), 2));

		vistaImportes.setTotalCosteTomadorAFinanciar(NumberUtils.formatear(
				distribCostes1.getTotalCosteTomador(), 2));

		if (distribCostes1.getRecargoAval() != null) {
			viGn.setRecargoAval(NumberUtils.formatear(
					distribCostes1.getRecargoAval(), 2));
		}
		if (distribCostes1.getRecargoFraccionamiento() != null) {
			viGn.setRecargoFraccionamiento(NumberUtils.formatear(
					distribCostes1.getRecargoFraccionamiento(), 2));
		}

		subCCAA1 = distribCostes1.getSubvencionCCAAArray();
		subEnesa1 = distribCostes1.getSubvencionEnesaArray();
		boniRecargo1 = distribCostes1.getBonificacionRecargoArray();

		if (boniRecargo1 != null) {

			for (int i = 0; i < boniRecargo1.length; i++) {
				viGn.addBoniRecargo1(
						this.getDescBoniRecar(boniRecargo1[i].getCodigo()),
						NumberUtils.formatear(boniRecargo1[i].getImporte(), 2));
			}
		}

		if (subCCAA1 != null && subCCAA1.length > 0) {
			Map<String, BigDecimal> ccaa = new HashMap<String, BigDecimal>();
			for (int i = 0; i < subCCAA1.length; i++) {
				ccaa.put(subCCAA1[i].getCodigoOrganismo(), new BigDecimal(0));
			}
			for (int i = 0; i < subCCAA1.length; i++) {
				ccaa.put(
						subCCAA1[i].getCodigoOrganismo(),
						ccaa.get(subCCAA1[i].getCodigoOrganismo()).add(
								subCCAA1[i].getImporte()));
			}
			Iterator<Map.Entry<String, BigDecimal>> it = ccaa.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, BigDecimal> e = (Map.Entry<String, BigDecimal>) it
						.next();
				String key = e.getKey();
				BigDecimal valor = e.getValue();

				viGn.addSubCCAA(this.getCCAA(key.charAt(0)),
						NumberUtils.formatear(valor, 2));
			}

		}
		if (subEnesa1 != null && subEnesa1.length > 0) {
			for (int i = 0; i < subEnesa1.length; i++) {
				viGn.addSubEnesa(this.getDescripcionEnesa(new BigDecimal(
						subEnesa1[i].getTipo())), NumberUtils.formatear(
						subEnesa1[i].getImporte(), 2));

			}
		}
		vistaImportes.getVistaImportesPorGrupoNegocio().add(viGn);
	}

	public VistaImportes generateDataForImportes(Long idEnvio,
			Map<ComparativaPolizaId, ResultadoWS> resultadoComparativas,
			com.rsi.agp.dao.tables.poliza.Poliza poliza,
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp,
			Usuario usuario, FinanciacionDocument financiacion, String realPath) {
		if (null != financiacion) {
			if (null != resultadoComparativas.get(cp.getId())
					.getPolizaCalculo()) { // solo para agro. En ganado lo
											// resolvemos en
											// generateDataForImportes -->
											// dc2015FmtUnificado
				resultadoComparativas
						.get(cp.getId())
						.getPolizaCalculo()
						.getDatosCalculo()
						.getDistribucionCoste1()
						.setRecargoAval(
								financiacion.getFinanciacion()
										.getPeriodoArray(0)
										.getDistribucionCoste()
										.getRecargoAval());
				resultadoComparativas
						.get(cp.getId())
						.getPolizaCalculo()
						.getDatosCalculo()
						.getDistribucionCoste1()
						.setRecargoFraccionamiento(
								financiacion.getFinanciacion()
										.getPeriodoArray(0)
										.getDistribucionCoste()
										.getRecargoFraccionamiento());
				resultadoComparativas
						.get(cp.getId())
						.getPolizaCalculo()
						.getDatosCalculo()
						.getDistribucionCoste1()
						.setCosteTomador(
								financiacion.getFinanciacion()
										.getCosteTomador());
				resultadoComparativas
						.get(cp.getId())
						.getPolizaCalculo()
						.getDatosCalculo()
						.getDistribucionCoste1()
						.setTotalCosteTomador(
								financiacion.getFinanciacion()
										.getPeriodoArray(0)
										.getDistribucionCoste()
										.getTotalCosteTomador());
			}
		}

		VistaImportes vistaImportes = this.generateDataForImportes(idEnvio,
				resultadoComparativas, poliza, cp, usuario, realPath,
				financiacion);

		BigDecimal idComparativa = new BigDecimal(-1);

		String seleccionada = vistaImportes.getComparativaSeleccionada();
		String[] cadena = seleccionada.split("\\|");
		String codModulo = cadena[1];
		if (null != cadena[0] && !"null".equals(cadena[0])) {
			idComparativa = new BigDecimal(cadena[0]);
		}
		if (poliza.getLinea().getCodplan().compareTo(new BigDecimal("2015")) != -1) {

			Iterator<DistribucionCoste2015> it = poliza
					.getDistribucionCoste2015s().iterator();
			boolean encontrado = false;

			while (it.hasNext() && !encontrado) {
				DistribucionCoste2015 distCoste = it.next();

				if (idComparativa.compareTo(new BigDecimal(-1)) == 0) {
					if (distCoste.getCodmodulo().equals(codModulo)
							&& distCoste.getIdcomparativa().equals(idComparativa)) {
						if (financiacion == null) {
							for (VistaImportesPorGrupoNegocio vistaImportesPorGrupoNegocio : vistaImportes
									.getVistaImportesPorGrupoNegocio()) {
								if (distCoste
										.getGrupoNegocio()
										.toString()
										.equals(vistaImportesPorGrupoNegocio
												.getCodGrupoNeg())) {
									if (distCoste.getRecargoaval() != null) {
										vistaImportesPorGrupoNegocio
												.setRecargoAval(NumberUtils.formatear(
														distCoste
																.getRecargoaval(),
														2));
									}
									if (distCoste.getRecargofraccionamiento() != null) {
										vistaImportesPorGrupoNegocio
												.setRecargoFraccionamiento(NumberUtils.formatear(
														distCoste
																.getRecargofraccionamiento(),
														2));
									}
								}
							}

							if (distCoste.getImportePagoFracc() != null) {
								vistaImportes
										.setImportePagoFracc(NumberUtils
												.formatear(distCoste
														.getImportePagoFracc(),
														2));
							}
							if (distCoste.getPeriodoFracc() != null) {
								vistaImportes.setPeriodoFracc(distCoste
										.getPeriodoFracc().toString());
							}
							vistaImportes.setOpcionFracc(distCoste
									.getOpcionFracc());
							vistaImportes.setValorOpcionFracc(distCoste
									.getValorOpcionFracc());

						} else {
							// Necesitamos al menos poner el periodo de
							// fraccionamiento (u otro que solo se rellene
							// cuando esta financiado)
							// ya que lo usaremos de flag para ver si hay que
							// pintar o no el boton de financiar
							vistaImportes.setPeriodoFracc(distCoste
									.getPeriodoFracc().toString());
						}
						encontrado = true;
					}
				} else {
					if (distCoste.getCodmodulo().equals(codModulo)
							&& distCoste.getIdcomparativa().equals(
									idComparativa)) {
						if (financiacion == null) {
							for (VistaImportesPorGrupoNegocio vistaImportesPorGrupoNegocio : vistaImportes
									.getVistaImportesPorGrupoNegocio()) {
								if (distCoste
										.getGrupoNegocio()
										.toString()
										.equals(vistaImportesPorGrupoNegocio
												.getCodGrupoNeg())) {
									if (distCoste.getRecargoaval() != null) {
										vistaImportesPorGrupoNegocio
												.setRecargoAval(NumberUtils.formatear(
														distCoste
																.getRecargoaval(),
														2));
									}
									if (distCoste.getRecargofraccionamiento() != null) {
										vistaImportesPorGrupoNegocio
												.setRecargoFraccionamiento(NumberUtils.formatear(
														distCoste
																.getRecargofraccionamiento(),
														2));
									}
								}
							}

							if (distCoste.getImportePagoFracc() != null) {
								vistaImportes
										.setImportePagoFracc(NumberUtils
												.formatear(distCoste
														.getImportePagoFracc(),
														2));
							}
							if (distCoste.getPeriodoFracc() != null) {
								vistaImportes.setPeriodoFracc(distCoste
										.getPeriodoFracc().toString());
							}
							vistaImportes.setOpcionFracc(distCoste
									.getOpcionFracc());
							vistaImportes.setValorOpcionFracc(distCoste
									.getValorOpcionFracc());

						} else {
							// Necesitamos al menos poner el periodo de
							// fraccionamiento (u otro que solo se rellene
							// cuando esta financiado)
							// ya que lo usaremos de flag para ver si hay que
							// pintar o no el boton de financiar
							vistaImportes.setPeriodoFracc(distCoste
									.getPeriodoFracc().toString());
						}

						if (null != distCoste.getImportePagoFracc())
							vistaImportes.setImportePagoFracc(NumberUtils
									.formatear(distCoste.getImportePagoFracc(),
											2));

						encontrado = true;
					}
				}
			}

		}

		try {
			BigDecimal pctMinimoFinanciacion = getPctMinimoFinanciacion(
					cp.getId(), poliza);
			if (null != pctMinimoFinanciacion) {
				vistaImportes
						.setPctMinFinanSobreCosteTomador(pctMinimoFinanciacion
								.toString());
			}

		} catch (Exception e) {
			logger.error(
					"Error al obtener el porcentaje minimo de fraccionamiento",
					e);
		}

		return vistaImportes;
	}

	private BigDecimal getPctMinimoFinanciacion(
			ComparativaPolizaId comparativa, Poliza poliza) throws Exception {
		BigDecimal resultado = null;
		BigDecimal codPlan = null;
		BigDecimal codLinea = null;
		String codModulo = null;

		if (null != poliza && null != poliza.getLinea()
				&& null != poliza.getLinea().getCodplan()
				&& null != poliza.getLinea().getCodlinea()
				&& comparativa != null) {
			codPlan = poliza.getLinea().getCodplan();
			codLinea = poliza.getLinea().getCodlinea();
			codModulo = comparativa.getCodmodulo();

			// Busqueda de porcentajes de financiacon
			resultado = pagoPolizaManager.getPctMinimoFinanciacion(codPlan,
					codLinea, codModulo);
		}

		return resultado;
	}

	public boolean isCalculable(
			Map<ComparativaPolizaId, ResultadoWS> acusePolizaHolder) {

		// ResultadoWS resultado = null;
		// Set<ComparativaPolizaId> keys = acusePolizaHolder.keySet();
		int size = acusePolizaHolder.keySet().size();
		boolean botonCalculo[] = new boolean[size];
		boolean botonCorregir[] = new boolean[size];
		boolean bBotonCalculo = false;
		int iComparativa = 0;

		for (Map.Entry<ComparativaPolizaId, ResultadoWS> entry : acusePolizaHolder
				.entrySet()) {
			botonCalculo[iComparativa] = false;
			botonCorregir[iComparativa] = false;
			ResultadoWS resultado = entry.getValue();
			AcuseRecibo acuse = entry.getValue().getAcuseRecibo();
			if (acuse != null) {
				if (acuse.getDocumentosRecibidos() <= 0) {
					botonCalculo[iComparativa] = true;
				}
				for (Documento doc : acuse.getDocumentoArray()) {
					botonCalculo[iComparativa] = resultado.isCalculable();
					if (doc.getEstado() == 3 || doc.getEstado() == 2) {
						if (ArrayUtils.isEmpty(doc.getErrorArray())) {
							botonCalculo[iComparativa] = true;
							botonCorregir[iComparativa] = false;
						} else {
							botonCorregir[iComparativa] = true;
							botonCalculo[iComparativa] = false;
						}
					}

				}
			}
			iComparativa++;
		}

		// Se comprueban todas las comparativas
		for (Boolean boton : botonCalculo) {
			bBotonCalculo = boton ? true : false;
		}
		return bBotonCalculo;

	}

	public Long generateAndSaveXMLPoliza(final Poliza poliza, final ComparativaPoliza cp, final String webServiceToCall,
			final boolean aplicaDtoRec, final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws DAOException, ValidacionPolizaException, BusinessException {

		String tipoEnvio;
		ImporteFraccionamiento ifr = null;
		PagoPoliza ppol = null;
		if (webServiceToCall.equals(Constants.WS_VALIDACION)) {
			tipoEnvio = "VL";
			if (poliza.getPagoPolizas() != null
					&& poliza.getPagoPolizas().size() > 0) {
				PagoPoliza pago = poliza.getPagoPolizas().iterator().next();
				if (null != pago && pago.getFormapago().equals('F')) {
					pago.setFormapago('C');
				}
			}
		} else {
			// DAA 19/06/13
			if (webServiceToCall.equals(Constants.WS_PASAR_DEFINITIVA)) {
				tipoEnvio = "PD";
				// Obtenemos el pago y el importe de fraccionamiento de la
				// poliza por si va financiada
				if (poliza.getIdpoliza() != null) {
					ppol = pagoPolizaManager.getPagoPolizaByPolizaId(poliza
							.getIdpoliza());
					ifr = this.getImporteFraccionamiento(ppol.getPoliza()
							.getLinea().getLineaseguroid(), poliza
							.getColectivo().getSubentidadMediadora());
				}

			} else {
				// Pet. 22208 ** MODIF TAM (13.03.2018) ** Inicio //
				// Enviar el nuevo tipo de envio para cuando la llamada sea
				// del SW de Confirmacion
				// tipoEnvio = "CL";
				if (webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
					tipoEnvio = "CO";
					if (poliza.getIdpoliza() != null) {
						ppol = pagoPolizaManager.getPagoPolizaByPolizaId(poliza
								.getIdpoliza());
						ifr = this.getImporteFraccionamiento(ppol.getPoliza()
								.getLinea().getLineaseguroid(), poliza
								.getColectivo().getSubentidadMediadora());
					}
				} else {
					tipoEnvio = "CL";
				}
				// Pet. 22208 ** MODIF TAM (13.03.2018) ** Fin //

			}
		}

		// Calculo de CPM permitidos
		logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: "
				+ poliza.getIdpoliza() + ", codModulo: "
				+ cp.getId().getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null,
				poliza.getIdpoliza(), cp.getId().getCodmodulo());

		// Genero el xml
		Usuario usuario = poliza.getUsuario();
		String envio = WSUtils.generateXMLPoliza(poliza, cp, webServiceToCall, polizaDao, listaCPM, usuario, ifr, ppol,
				aplicaDtoRec, comsPctCalculado);

		// 3. Insertar el fichero xml generado en la tabla de envios a
		// agroseguro
		// y devolver el identificador de la insercion
		BigDecimal filaComparativa = null;
		if (poliza.getLinea().getEsLineaGanadoCount() > 0) {
			filaComparativa = new BigDecimal(cp.getId().getIdComparativa());
		} else {
			filaComparativa = cp.getId().getFilacomparativa();
		}
		
		/* Pet. 57626 ** MODIF TAM (21/07/2020) ** Inicio */
		/* Antes de lanzar llamada al S.W de Calculo se cambia en el xml el nombre del esquema
		 * pero al grabar el envio se graba con el esquema incorrecto. (ServicioCalcularHelper.java (doWork)*/
		
		if (webServiceToCall.equals(Constants.WS_CALCULO)) {
			envio = envio.replace("xmlns=\"http://www.agroseguro.es/Contratacion\"", "xmlns=\"http://www.agroseguro.es/PresupuestoContratacion\"");
		}
		
		return this.guardarXmlEnvio(poliza, envio, tipoEnvio, cp.getId()
				.getCodmodulo(), filaComparativa);
	}

	/**
	 * Actualiza la distribucion de costes a partir del resultado del calculo
	 * con el formato unificado
	 * 
	 * @param idModulo
	 * @return
	 * @throws Exception
	 */
	public void actualizaDistribCostesUnificado(
			es.agroseguro.distribucionCostesSeguro.Poliza polizaUnificada,
			Poliza poliza, String codModulo, BigDecimal filaComparativa,
			Long idComparativa) throws Exception {

		try {
			distribucionCosteDAO.deleteDistribucionCoste2015(
					poliza.getIdpoliza(), codModulo, idComparativa);
			Boolean esGanado = poliza.getLinea().isLineaGanado();
			Set<DistribucionCoste2015> distribCoste1 = distribucionCosteDAO
					.saveDistribucionCoste2015Unificado(polizaUnificada,
							poliza.getIdpoliza(), codModulo, filaComparativa,
							idComparativa, null, 0, 0, null, esGanado);
			poliza.getDistribucionCoste2015s().addAll(distribCoste1);

			// Actualiza el registro de pagos poliza con el importe recibido
			if (poliza.getPagoPolizas() != null
					&& poliza.getPagoPolizas().size() > 0) {
				Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
				if (it.hasNext()) {
					PagoPoliza pp = it.next();
					if (pp.getPctprimerpago() != null) {
						pp.setImporte(poliza.getImporte());
					}
				}
			}
		} catch (Exception ex) {
			throw new DistribucionCostesException(
					"Ocurrio un error al guardar la distribucion de costes de la poliza",
					ex);
		}
	}

	/**
	 * Actualiza la distribucion de costes guardada
	 * 
	 * @param poliza
	 */
	public es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument actualizaDistribCostes(
			Long idEnvio, Poliza poliza, String codModulo,
			BigDecimal filaComparativa, Long idComparativa)
			throws DistribucionCostesException, Exception {

		// Se recupera la comparativa de la poliza (en este punto, solo deberia
		// haber 1)
		String xml = WSUtils.obtenXMLCalculo(idEnvio, polizaDao);

		// No se recupero el XML...
		if (xml == null)
			throw new Exception("No se ha podido obtener el XML de Calculo");

		try {
			// Se valida el XML y se obtiene el document
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument polizaDocument = WSUtils
					.getXMLDistribCostes(xml);

			if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == -1) {
				// SE ELIMINA EL CLEAR YA QUE AL SET PERSISTENT SET BORRA EN
				// BBDD
				// poliza.getDistribucionCostes().clear();
				distribucionCosteDAO.deleteDistribucionCoste(
						poliza.getIdpoliza(), codModulo, filaComparativa);
				com.rsi.agp.dao.tables.poliza.DistribucionCoste distribCoste = distribucionCosteDAO
						.saveDistribucionCoste(polizaDocument.getPoliza(),
								poliza.getIdpoliza(), codModulo,
								filaComparativa);
				poliza.getDistribucionCostes().add(distribCoste);
			} else {
				distribucionCosteDAO.deleteDistribucionCoste2015(
						poliza.getIdpoliza(), codModulo, idComparativa);
				DistribucionCoste2015 distribCoste1 = distribucionCosteDAO
						.saveDistribucionCoste2015(polizaDocument.getPoliza(),
								poliza.getIdpoliza(), codModulo,
								filaComparativa, idComparativa);
				poliza.getDistribucionCoste2015s().add(distribCoste1);
			}

			// / Angel. Mejora 113 - 24/02/2012 Actualizacion del importe de la
			// poliza en pagos Poliza si existen.
			if (poliza.getPagoPolizas() != null
					&& poliza.getPagoPolizas().size() > 0) {
				Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
				if (it.hasNext()) {
					PagoPoliza pp = it.next();
					if (pp.getPctprimerpago() != null) {
						// pp.setImporte(distribCoste.getCargotomador());
						pp.setImporte(poliza.getImporte());
					}
				}
			}
			// / Fin mejora 113
			
			return polizaDocument;
		} catch (Exception e) {
			throw new DistribucionCostesException(
					"Ocurrio un error al guardar la distribucion de costes de la poliza",
					e);
		}
	}

	public void actualizaDistribCostesConFinanciacion(Long idEnvio,
			Poliza poliza, String codModulo, BigDecimal filaComparativa,
			FinanciacionDocument financiacion, Map<String, Object> parameters,
			Long idComparativa) throws DistribucionCostesException, Exception {

		es.agroseguro.distribucionCostesSeguro.PolizaDocument polizaDocumentSeg = null;

		String xml = WSUtils.obtenXMLCalculo(idEnvio, polizaDao);

		// No se recupero el XML...
		if (xml == null)
			throw new Exception("No se ha podido obtener el XML de Calculo");
		try {
			
			polizaDocumentSeg = WSUtils.getXMLDistribCostesSeguro(xml);
			
			Integer opcionFracc = new Integer(
					((String[]) parameters.get("opcion_cf"))[0]);
			Integer condfrac = new Integer(
					((String[]) parameters.get("condicionesFraccionamiento"))[0]);

			String valorOpcionFraccStr = null;

			switch (opcionFracc) {
			case 0:
				valorOpcionFraccStr = ((String[]) parameters
						.get("porcentajeCosteTomador_txt"))[0];
				break;
			case 1:
				valorOpcionFraccStr = ((String[]) parameters
						.get("importeFinanciar_txt"))[0];
				break;
			case 2:
				valorOpcionFraccStr = ((String[]) parameters
						.get("importeAval_txt"))[0];
				break;
			default:
				break;
			}
			valorOpcionFraccStr = valorOpcionFraccStr.replace(".", "");
			valorOpcionFraccStr = valorOpcionFraccStr.replace(",", ".");
			BigDecimal valorOpcionFracc = new BigDecimal(valorOpcionFraccStr);
			Set<DistribucionCoste2015> setDistCoste = null;
			distribucionCosteDAO.deleteDistribucionCoste2015(
					poliza.getIdpoliza(), codModulo, idComparativa);
			
			Boolean esGanado = poliza.getLinea().isLineaGanado();
			setDistCoste = distribucionCosteDAO
					.saveDistribucionCoste2015Unificado(
							polizaDocumentSeg.getPoliza(),
							poliza.getIdpoliza(), codModulo,
							filaComparativa, idComparativa, financiacion,
							condfrac, opcionFracc, valorOpcionFracc, esGanado);
			poliza.getDistribucionCoste2015s().addAll(setDistCoste);
			poliza.setImporte(setDistCoste.iterator().next()
					.getCostetomador());

			if (poliza.getPagoPolizas() != null
					&& poliza.getPagoPolizas().size() > 0) {
				Iterator<PagoPoliza> it = poliza.getPagoPolizas()
						.iterator();
				if (it.hasNext()) {
					PagoPoliza pp = it.next();
					pp.setImporte(setDistCoste.iterator().next()
							.getImportePagoFracc());
				}
			}
		} catch (Exception e) {
			throw new DistribucionCostesException(
					" Ocurrio un error al guardar la distribucion de costes de la poliza",
					e);
		}
	}

	/**
	 * Devuelve una representacion de la descripcion de la comparativa para
	 * mostrarla por pantalla en el JSP erroresValidacion
	 * 
	 * @param cp
	 * @return HTML formatted ComparativaPoliza header
	 */
	@SuppressWarnings("unchecked")
	public String getComparativaHeader(
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp) {
		String[] arrModulos = new String[] { cp.getId().getCodmodulo() };
		Map<String, Object> comparativas = cuadroCoberturasManager
				.crearComparativas(
						cp.getPoliza().getLinea().getLineaseguroid(), cp
								.getPoliza().getLinea().getCodlinea()
								+ "", cp.getPoliza().getClase(), cp.getPoliza()
								.getAsegurado().getNifcif(), arrModulos);
		HashMap<String, List<List<VistaComparativas>>> compMod = null;
		if (comparativas != null)
			compMod = (HashMap<String, List<List<VistaComparativas>>>) comparativas
					.get("comparativa");

		String formattedReturn = "";

		if (compMod != null) {
			BigDecimal filaComparativa = cp.getId().getFilacomparativa();

			String descripcionModulo = compMod.keySet().iterator().next();
			List<VistaComparativas> comparativa = compMod
					.get(descripcionModulo).get(filaComparativa.intValue() - 1);

			formattedReturn += "<table width='100%'><tr>"
					+ "<td class='literalbordeCabecera' align='center' width='20%'>Garant&iacute;a</td>"
					+ "<td class='literalbordeCabecera' align='center' width='25%'>Riesgo Cubierto</td>"
					+ "<td class='literalbordeCabecera' align='center' width='20%'>Cobertura</td>"
					+ "<td class='literalbordeCabecera' align='center' width='20%'>Valor</td>"
					+ "</tr><tr class='literalborde'>";

			boolean todosElementosComparativaMenosDos = true;

			for (int i = 0; i < comparativa.size(); i++) {

				VistaComparativas vistaComparativa = comparativa.get(i);

				if (vistaComparativa.getId().getCodvalor().intValue() != -2) {

					todosElementosComparativaMenosDos = false;

					formattedReturn += "<tr>";
					formattedReturn += TD_LITERAL_BORDE
							+ vistaComparativa.getId().getDesconceptoppalmod()
							+ NBSP_TD;
					formattedReturn += "<td class='literalborde' width='26%'>"
							+ vistaComparativa.getId().getDesriesgocubierto()
							+ NBSP_TD;
					formattedReturn += TD_LITERAL_BORDE
							+ vistaComparativa.getId().getNomconcepto();

					Iterator<VistaComparativas> iterator = vistaComparativa
							.getFilasVinculadas().iterator();

					while (iterator.hasNext()) {

						VistaComparativas filaVinculada = iterator.next();
						formattedReturn += "</br>"
								+ filaVinculada.getId().getNomconcepto();

					}

					formattedReturn += NBSP_TD;
					formattedReturn += TD_LITERAL_BORDE
							+ StringUtils.nullToString(vistaComparativa.getId()
									.getDesvalor());
					// ini sacamos la descripcion del valor de los vinculados
					Iterator<VistaComparativas> iterator2 = vistaComparativa
							.getFilasVinculadas().iterator();

					while (iterator2.hasNext()) {

						VistaComparativas filaVinculada = iterator2.next();
						formattedReturn += "&nbsp;</br>"
								+ StringUtils.nullToString(filaVinculada
										.getId().getDesvalor());

					}

					formattedReturn += NBSP_TD;
					// fin
				}

			}

			if (todosElementosComparativaMenosDos) {

				formattedReturn += "<tr>";
				formattedReturn += "<td class='literalborde' colspan='4'>Sin riesgos cubiertos elegibles</td>";

			}

			formattedReturn += "</tr></tr></table>";

		}
		return formattedReturn;
	}

	/**
	 * Consulta la situacion de la poliza a una fecha
	 * 
	 * @param recibo
	 *            de la poliza
	 * @return Stream out
	 * @throws Exception
	 */
	public Base64Binary consultarPolizaActual(ReciboPoliza reciboPoliza,
			String realPath) throws BusinessException {
		try {
			return servicioPolizaActualPDFHelper.doWork(reciboPoliza, realPath);
		} catch (Exception e) {
			throw new BusinessException(
					ERROR_CONSULTA_POLIZA,
					e);
		}

	}

	/**
	 * Consulta la situacion de la poliza desde el menu de duplicados
	 * informaticos
	 * 
	 */
	public Base64Binary consultarPolizaActualCopy(String CodPlan,
			String RefPoliza, String tipoRef, String realPath)
			throws BusinessException {
		try {
			return servicioPolizaActualPDFHelper.doWorkCopy(CodPlan, RefPoliza,
					tipoRef, realPath);
		} catch (Exception e) {
			throw new BusinessException(
					ERROR_CONSULTA_POLIZA,
					e);
		}

	}

	/**
	 * Consulta la situacion de la poliza Tradicional desde el menu de
	 * duplicados informaticos
	 * 
	 * @param CodPlan
	 * @param RefPoliza
	 * @param tipoRef
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public Base64Binary consultarPolizaTradActualCopy(String CodPlan,
			String RefPoliza, String tipoRef, String realPath) throws Exception {
		return servicioPolizaTradActualPDFHelper.doWorkTradCopy(CodPlan,
				RefPoliza, tipoRef, realPath);
	}

	/**
	 * Consulta la situacion de la poliza original
	 * 
	 * @param recibo
	 *            de la poliza
	 * @return Stream out
	 * @throws Exception
	 */
	public Base64Binary consultarPolizaOrigen(Poliza poliza, String realPath)
			throws BusinessException {
		try {
			return servicioPolizaOrigenPDFHelper.doWork(poliza, realPath);
		} catch (Exception e) {
			throw new BusinessException(
					ERROR_CONSULTA_POLIZA,
					e);
		}

	}
	
	/*** Pet. 57627 ** MODIF TAM (27.04.2020) ** Inicio ***/
	/**
	 * Consulta la Situacion Actual con calculo de coste
	 * 
	 * @param CodPlan
	 * @param RefPoliza
	 * @param tipoRef
	 * @param realPath
	 * @return Stream out
	 * @throws Exception
	 */
	public Base64Binary consultarSituacionActualPol(final BigDecimal codPlan, final String referencia, final String tipoRef, String realPath) throws BusinessException {
		try {
			return servicioSituacionActualPDFHelper.doWork(codPlan, referencia, tipoRef, realPath);
		} catch (Exception e) {
			throw new BusinessException(
					ERROR_CONSULTA_POLIZA,
					e);
		}

	}
	/*** Pet. 57627 ** MODIF TAM (27.04.2020) ** Fin ***/

	public BigDecimal calcularCaractExplotacion(String xml, String realPath) {
		GregorianCalendar gcIni = new GregorianCalendar();
		
		/** Pet. 57626 ** MODIF TAM (18/05/2020) ** Inicio **/
		//BigDecimal carExpl = servicioCaractExplotacionHelper.doWork(xml,
		//		realPath, tipoWS);
		
		Long idEnvio = new Long(0);
		Map<String, Object> retorno = servicioCaractExplotacionHelper.doWork(xml, realPath, idEnvio, polizaDao);
		
		BigDecimal carExpl = (BigDecimal) retorno.get("carExpl");
		/** Pet. 57626 ** MODIF TAM (18/05/2020) ** Fin **/
		
		GregorianCalendar gcFin = new GregorianCalendar();

		Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
		logger.debug("Tiempo de la llamada al servicio calculo de la caracteristica de la explotacion: "
				+ tiempo + MILISEGUNDOS);

		return carExpl;
	}

	/**
	 * Valida la poliza indicada contra el servicio Web
	 * 
	 * @param id
	 *            de la poliza
	 * @param Path
	 *            real donde buscar el WSDL del servicio
	 * @param tipoWS
	 *            nos indica si llamar al WS_local o WS_desarrollo
	 * @return AcuseRecibo
	 * @throws DAOException
	 */
	public AcuseRecibo validar(Long idEnvio, Long idPoliza, String realPath,
			Character tipoWS, ComparativaPoliza cp) throws DAOException {
		GregorianCalendar gcIni = new GregorianCalendar();

		AcuseRecibo acuse = servicioValidarHelper.doWork(idEnvio, idPoliza,
				realPath, polizaDao, tipoWS);

		GregorianCalendar gcFin = new GregorianCalendar();
		Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
		logger.debug("Tiempo de la llamada al servicio de validacion: "
				+ tiempo + MILISEGUNDOS);

		if (acuse != null) {
			logger.debug("Acuse de la llamada al servicio de validacion recibido correctamente");
			// guardamos el acuse de recibo
			logger.debug("Obtenemos la poliza para actualizar el acuse en BD: "
					+ idPoliza);
			Poliza poliza = polizaDao.getPolizaById(idPoliza);
			grabaAcuseRecibo(acuse, poliza, idEnvio);
		} else {
			logger.debug("El acuse de la llamada al servicio de validacion es NULO");
		}

		return acuse;
	}

	/**
	 * Realiza la confirmacion de la Poliza contra el servicio Web
	 * 
	 * @param id
	 *            de la poliza
	 * @param Path
	 *            real donde buscar el WSDL del servicio
	 * @return AcuseRecibo
	 */
	public AcuseRecibo confirmar(Long idEnvio, Long idPoliza, String realPath)
			throws DAOException {
		
		logger.debug("WebservicesManager-confirmar");
		
		GregorianCalendar gcIni = new GregorianCalendar();
		logger.debug("WebServicesManager-Antes de llamar al dowork");
		AcuseRecibo acuse = servicioConfirmarHelper.doWork(idEnvio, idPoliza,
				realPath, polizaDao);
		logger.debug("WebServicesManager-Despues de llamar al dowork");
		GregorianCalendar gcFin = new GregorianCalendar();

		// Pet. 22208 ** MODIF TAM (14.03.2018) ** Inicio //
		// Incluimos la validacion del acuse de recibo he insertamos registro de
		// recepcion
		if (acuse != null) {
			logger.debug("Acuse de la llamada al servicio de SW Confirmacion recibido correctamente");

			// guardamos el acuse de recibo
			logger.debug("Obtenemos la poliza para actualizar el acuse en BD: "
					+ idPoliza);
			Poliza poliza = polizaDao.getPolizaById(idPoliza);

			grabaAcuseRecibo(acuse, poliza, idEnvio);
		} else {
			logger.debug("El acuse de la llamada al servicio de SW Confirmacion es NULO");
		}

		Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
		logger.debug("Tiempo de la llamada al servicio de confirmacion: "
				+ tiempo + MILISEGUNDOS);

		return acuse;
	}

	/**
	 * Realiza el calculo de la poliza contra el servicio Web
	 * 
	 * @param id
	 *            de la poliza
	 * @param Path
	 *            real donde buscar el WSDL del servicio
	 * @param tipoWS
	 *            nos indica si llamar al WS_local o WS_desarrollo
	 * @return Map con el Acuse de Recibo y el Documento del Calculo
	 */
	public Map<String, Object> calculo(Long idEnvio, Long idPoliza,
			BigDecimal descuentoColectivo, String realPath) {
		
		GregorianCalendar gcIni = new GregorianCalendar();
		
		Map<String, Object> acuse = servicioCalcularHelper.doWork(idEnvio,idPoliza, descuentoColectivo, realPath, polizaDao);
		GregorianCalendar gcFin = new GregorianCalendar();

		Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
		logger.debug("Tiempo de la llamada al servicio de calculo: " + tiempo
				+ MILISEGUNDOS);

		return acuse;
	}

	public Map<String, Object> financiar(String realPath, FinanciarRequest fr) {
		GregorianCalendar gcIni = new GregorianCalendar();
		Map<String, Object> financiacion = ServicioFinanciarHelper.doWork(
				realPath, fr);
		GregorianCalendar gcFin = new GregorianCalendar();

		Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
		logger.debug("XML financiacion obtenido:" + financiacion);
		logger.debug("Tiempo de la llamada al servicio de calculo: " + tiempo
				+ MILISEGUNDOS);

		return financiacion;
	}

	/**
	 * SeguimientoPoliza contra el servicio Web
	 * 
	 * @param id
	 *            de la poliza
	 * @param Path
	 *            real donde buscar el WSDL del servicio
	 * @param tipoWS
	 *            nos indica si llamar al WS_local o WS_desarrollo
	 * @return Map con del objeto SeguimientoPoliza
	 */
	public Map<String, Object> getSeguimientoPoliza(String referencia,
			BigDecimal plan, Date fechaCambioEstDesde, Date fechaCambioEstHasta, String realPath, String metodo) {
		
		return servicioSeguimientoPolizaHelper.doWork(referencia, plan, fechaCambioEstDesde, fechaCambioEstHasta, realPath, metodo);
		
	}

	public FaseDocument consultaEstado(String tipoReferencia,
			BigDecimal codPlan, String refPoliza, BigDecimal codRecibo,
			Date fechaEmisionRecibo, String realPath, Character tipoWS)
			throws SeguimientoServiceException {
		return servicioConsultaEstado.doWork(tipoReferencia, codPlan,
				refPoliza, codRecibo, fechaEmisionRecibo, realPath);
	}

	public ListaRecibosDocument listarRecibosEmitidos(String tipoReferencia,
			BigDecimal codPlan, String refPoliza, String realPath)
			throws SeguimientoServiceException {
		return servicioListarRecibosPoliza.doWork(tipoReferencia, codPlan,
				refPoliza, realPath);
	}

	public es.agroseguro.recibos.emitidos.FaseDocument consultaReciboEmitido(
			String tipoReferencia, BigDecimal codPlan, String refPoliza,
			BigDecimal codRecibo, Date fechaEmisionRecibo, String realPath)
			throws SeguimientoServiceException {
		return servicioConsultaDetalleRecibo.doWork(tipoReferencia, codPlan,
				refPoliza, codRecibo, fechaEmisionRecibo, realPath);
	}
	
	public es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza consultaCostesRecibo(
			String tipoReferencia, BigDecimal codPlan, String refPoliza,
			BigDecimal codRecibo, Date fechaEmisionRecibo, String realPath)
			throws SeguimientoServiceException {
		return servicioConsultaCostesRecibo.doWork(tipoReferencia, codPlan,
				refPoliza, codRecibo, fechaEmisionRecibo, realPath);
	}

	/**
	 * Obtiene la descripcion del Codigo de Subvencion Enesa indicado
	 * 
	 * @param tipo
	 * @return
	 */
	public String getDescripcionEnesa(BigDecimal tipo) {
		String dev = "";
		try {
			TipoSubvencionEnesa tipoEnesa = (TipoSubvencionEnesa) polizaDao
					.getObject(TipoSubvencionEnesa.class, tipo);
			if (tipoEnesa != null)
				dev = tipoEnesa.getDestiposubvenesa();
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion ENESA "
					+ tipo, e);
		}
		return dev;
	}

	/**
	 * Obtiene la descripcion del Codigo de Subvencion CCAA indicado
	 * 
	 * @param tipo
	 * @return
	 */
	public String getDescripcionCCAA(BigDecimal tipo) {
		String dev = "";
		try {
			TipoSubvencionCCAA tipoEnesa = (TipoSubvencionCCAA) polizaDao
					.getObject(TipoSubvencionCCAA.class, tipo);
			if (tipoEnesa != null)
				dev = tipoEnesa.getDestiposubvccaa();
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion CCAA "
					+ tipo, e);
		}
		return dev;
	}

	/**
	 * Obtiene la descripcion del Organismo indicado
	 * 
	 * @param codigoOrganismo
	 * @return
	 */
	public String getCCAA(Character codOrganismo) {
		String desc = "";
		try {

			Organismo organismo = (Organismo) polizaDao.getObject(
					Organismo.class, codOrganismo);
			if (organismo != null) {
				desc = organismo.getDesorganismo();
			}
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion CCAA "
					, e);
		}
		return desc;
	}

	/**
	 * Obtiene la descripcion de BonificacionRecargo
	 * 
	 * @param codigoOrganismo
	 * @return
	 */
	public String getDescBoniRecar(int i) {

		String desc = "";
		try {
			com.rsi.agp.dao.tables.cpl.BonificacionRecargo br = (com.rsi.agp.dao.tables.cpl.BonificacionRecargo) polizaDao
					.getObject(
							com.rsi.agp.dao.tables.cpl.BonificacionRecargo.class,
							Long.valueOf(i));
			if (br != null)
				desc = br.getDescripcion();
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de BonificacionRecargo", e);
		}
		return desc;
	}

	public String getDesGruponegocio(Character codGrupoNegocio) {
		String desc = "";
		try {

			com.rsi.agp.dao.tables.cgen.GruposNegocio gn = (com.rsi.agp.dao.tables.cgen.GruposNegocio) polizaDao
					.getObject(com.rsi.agp.dao.tables.cgen.GruposNegocio.class,
							codGrupoNegocio);
			if (gn != null) {
				desc = gn.getDescripcion();
			}
		} catch (Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion CCAA "
					, e);
		}
		return desc;
	}

	public String dimeSiAdmiteComplementario(ComparativaPoliza cp) {
		String dev = "NO";
		ModuloCompatibleFiltro filtro = new ModuloCompatibleFiltro();
		ModuloCompatibleCe moduloCompatibleCe = new ModuloCompatibleCe();
		moduloCompatibleCe.getLinea().setLineaseguroid(
				cp.getId().getLineaseguroid());
		moduloCompatibleCe.getModuloComplementario().getId()
				.setCodmodulo(cp.getId().getCodmodulo());
		moduloCompatibleCe
				.getRiesgoCubierto()
				.getId()
				.setCodriesgocubierto(
						cp.getRiesgoCubierto().getId().getCodriesgocubierto());
		filtro.setModuloCompatibleCe(moduloCompatibleCe);

		Integer valores = polizaDao.getNumObjects(filtro);
		if (valores.intValue() > 0)
			dev = "SI";
		return dev;
	}

	/**
	 * Actualiza el Numero de Referencia de la Poliza
	 * 
	 * @param poliza
	 * @param numReferencia
	 * @return
	 */
	public boolean actualizaNumRefPoliza(Poliza poliza, String numReferencia) {
		boolean ok = true;
		poliza.setReferencia(numReferencia);
		try {
			polizaDao.saveOrUpdate(poliza);
		} catch (Exception e) {
			logger.info("Error al actualizar la poliza para insertar su numero de referencia "
					, e);
			ok = false;
		}
		return ok;
	}

	private void grabaPolizaCalculo(final String polizaCalculo,
			final Poliza poliza, Long idEnvio) throws DAOException {
		if (polizaCalculo == null || poliza == null)
			return;
		EnvioAgroseguro envioAgroseguro = this.polizaDao
				.getEnvioAgroseguro(idEnvio);
		this.polizaDao.actualizaXmlEnvio(envioAgroseguro.getId(), null,
				polizaCalculo);
		this.polizaDao.evictEnvio(envioAgroseguro);
	}

	private void grabaAcuseRecibo(AcuseRecibo acuse, Poliza poliza, Long idEnvio)
			throws DAOException {
		// ASF - 10/2/2014 - correccion para que grabe correctamente los acuses
		// en las llamadas a validacion.

		logger.debug("WebServicesManager - Dentro de grabaAcuseRecibo");
		this.polizaDao.actualizaXmlEnvio(idEnvio, null, acuse.toString());
	}

	/**
	 * Metodo para guardar el XML de envio a agroseguro
	 * 
	 * @param poliza
	 *            Identificador de la poliza asociada
	 * @param envio
	 *            XML de envio a Agroseguro
	 * @param webServiceToCall
	 *            Servicio web a llamar
	 * @param codmodulo
	 *            Modulo asociado al envio
	 * @param filacomparativa
	 *            Comparativa asociada al envio
	 * @throws DAOException
	 *             Error al guardar el envio en la base de datos
	 */
	public Long guardarXmlEnvio(Poliza poliza, String envio, String tipoEnvio,
			String codmodulo, BigDecimal filacomparativa) throws DAOException {
		EnvioAgroseguro envioAgroseguro = new EnvioAgroseguro();
		envioAgroseguro.setPoliza(poliza);
		envioAgroseguro.setFechaEnvio((new GregorianCalendar()).getTime());

		envioAgroseguro.setXml(Hibernate.createClob(" "));

		envioAgroseguro.setTipoenvio(tipoEnvio);
		envioAgroseguro.setCodmodulo(codmodulo);
		envioAgroseguro.setFilacomparativa(filacomparativa);

		EnvioAgroseguro newEnvio = (EnvioAgroseguro) this.polizaDao
				.saveEnvioAgroseguro(envioAgroseguro);
		this.polizaDao.actualizaXmlEnvio(newEnvio.getId(), envio, null);
		this.polizaDao.evictEnvio(newEnvio);
		// para actualizar el envio en la poliza vuelvo a traerla de base de
		// datos.
		poliza = this.polizaDao.getPolizaById(poliza.getIdpoliza());
		return newEnvio.getId();
	}

	/**
	 * Para polizas <2015 no se muestra el boton Descuentos ni Recargos, Para
	 * polizas =>2015 se muestra en determinadas ocasiones 31/10/2014 U029769
	 * 
	 * @param poliza
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> muestraBotonDescuento(Poliza poliza,
			Usuario usuario) throws Exception {

		Map<String, Object> params = new HashMap<String, Object>();
		Set<PolizaPctComisiones> polizaPctComisiones = poliza
				.getSetPolizaPctComisiones();
		try {
			if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == -1) {
				params.put(MUESTRA_BOTON_DESCUENTOS, false);
				params.put("muestraBotonRecargos", false);
			} else {

				// Perfil 0,5 o 1 interno
				if (usuario.getPerfil().equals(
						Constants.PERFIL_USUARIO_ADMINISTRADOR)
						|| // perfil 0
						usuario.getPerfil().equals(
								Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)
						|| // perfil 5
						(!usuario.isUsuarioExterno() && usuario
								.getPerfil()
								.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))) {// perfil
																							// 1
																							// interno
					params.put(MUESTRA_BOTON_DESCUENTOS, true);
					params.put("muestraBotonRecargos", true);
					params.put("validarRango", false);
					// perfil 3,4 o externo (1 o 3)
				} else if (usuario.getPerfil().equals(
						Constants.PERFIL_USUARIO_JEFE_ZONA)
						|| // Perfil 2
						usuario.getPerfil().equals(
								Constants.PERFIL_USUARIO_OFICINA)
						|| // Perfil 3 int/ext
						usuario.getPerfil().equals(
								Constants.PERFIL_USUARIO_OTROS)
						|| // perfil 4
						(usuario.isUsuarioExterno() && usuario
								.getPerfil()
								.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))) { // perfil
																							// 1
																							// ext

					// comprobar boton Recargos
					/* recogemos los datos de mto de descuentos */
					List<BigDecimal> codOficinas = new ArrayList<BigDecimal>();
					if (usuario.getPerfil().equals(
							Constants.PERFIL_USUARIO_JEFE_ZONA)) {
						codOficinas.addAll(usuario.getListaCodOficinasGrupo());
					} else {
						codOficinas.add(usuario.getOficina().getId()
								.getCodoficina());
					}
					Descuentos descuentos = polizasPctComisionesDao
							.getDescuentos(poliza.getColectivo().getTomador()
									.getId().getCodentidad(), codOficinas,
									poliza.getColectivo()
											.getSubentidadMediadora().getId()
											.getCodentidad(), poliza
											.getColectivo()
											.getSubentidadMediadora().getId()
											.getCodsubentidad(), usuario
											.getDelegacion(), poliza.getLinea()
											.getCodplan(), poliza.getLinea()
											.getCodlinea());
					if (null == descuentos) {
						params.put("muestraBotonRecargos", false);
					} else {
						if (descuentos.getPermitirRecargo().compareTo(
								new Integer(1)) == 0) {
							params.put("muestraBotonRecargos", true);
						}
					}

					if (null == polizaPctComisiones
							|| polizaPctComisiones.isEmpty()
							|| polizaPctComisiones.size() == 0) {
						params.put(MUESTRA_BOTON_DESCUENTOS, false);
					} else {
						BigDecimal descuento = new BigDecimal(0);
						for (PolizaPctComisiones polizaPctComision : polizaPctComisiones) {
							if ((null != polizaPctComision.getPctdescmax())
									&& polizaPctComision.getPctdescmax()
											.compareTo(new BigDecimal(0)) > 0) {
								logger.debug("el descuento es mayor de 0");
								descuento = polizaPctComision.getPctdescmax();
								params.put(MUESTRA_BOTON_DESCUENTOS, true);
								params.put("validaRango", true);
								break;
							}
						}
						if (descuento.compareTo(new BigDecimal(0)) < 1) {
							logger.debug("el descuento NO es mayor de 0");
							params.put(MUESTRA_BOTON_DESCUENTOS, false);
							params.put("validaRango", false);
						}
					}
				}
				// recogemos los porcentajes de comision y buscamos la
				// descripcion del grupo de negocio
				BigDecimal dtoMax = new BigDecimal(0);
				boolean encontrado = false;
				for (PolizaPctComisiones pctCom : polizaPctComisiones) {
					String descGrNeg = null;
					descGrNeg = polizasPctComisionesDao
							.getDescripcionGrupoNegocio(pctCom
									.getGrupoNegocio());
					pctCom.setDescGrupoNegocio(descGrNeg);
					if (null != pctCom.getPctdescmax()
							&& dtoMax.compareTo(pctCom.getPctdescmax()) == (-1)) {
						dtoMax = pctCom.getPctdescmax();
						encontrado = true;
					}
				}
				if (!encontrado) {
					dtoMax = new BigDecimal(100);
				}
				params.put("pctdescmax", dtoMax);
				params.put("pctComisiones",
						getPolizasPctComisionesPodDistCostes(poliza));
			}
		} catch (Exception e) {
			logger.error("Error comprobar muestraBotonDescuento-Recargo", e);
			throw e;
		}
		return params;
	}

	@Deprecated
	public void updateDescuento(Long idpoliza, String descuento)
			throws Exception {

		try {
			polizasPctComisionesDao.updateDescuento(idpoliza, new BigDecimal(
					descuento));

		} catch (Exception e) {
			logger.error("Error al actualizar el descuento de la poliza - updateDescuento");
			throw e;
		}
	}

	public void updateDescuento(BigDecimal descuento, Long idPolizaPctComision)
			throws Exception {
		try {
			polizasPctComisionesDao.updateDescuento(descuento,
					idPolizaPctComision);

		} catch (Exception e) {
			logger.error("Error al actualizar el descuento de la poliza - updateDescuento");
			throw e;
		}
	}

	@Deprecated
	public void updateRecargo(Long idpoliza, String recargo) throws Exception {

		try {
			polizasPctComisionesDao.updateRecargo(idpoliza, new BigDecimal(
					recargo));

		} catch (Exception e) {
			logger.error("Error al actualizar el recargo de la poliza - updateRecargo");
			throw e;
		}
	}

	public void updateRecargo(BigDecimal recargo, Long idPolizaPctComision)
			throws Exception {

		try {
			polizasPctComisionesDao.updateRecargo(recargo, idPolizaPctComision);

		} catch (Exception e) {
			logger.error("Error al actualizar el recargo de la poliza - updateRecargo");
			throw e;
		}
	}

	public void insertHistoricoDescuento(Poliza poliza, String descuento,
			Usuario usu) throws Exception {

		try {
			polizasPctComisionesDao.insertHistoricoDescuento(poliza,
					new BigDecimal(descuento), usu);

		} catch (Exception e) {
			logger.error("Error al insertar en insertHistoricoDescuento");
			throw e;
		}
	}

	public void insertHistoricoDescuento(Poliza poliza, BigDecimal descuento,
			Usuario usu) throws Exception {

		try {
			polizasPctComisionesDao.insertHistoricoDescuento(poliza, descuento,
					usu);

		} catch (Exception e) {
			logger.error("Error al insertar en insertHistoricoDescuento");
			throw e;
		}
	}

	public List<CondicionesFraccionamiento> getCondicionesFraccionamiento(
			Long lineaseguroid, String codmodulo) throws DAOException {

		List<CondicionesFraccionamiento> lista = null;
		CondicionesFraccionamiento condicionesFraccionamiento = new CondicionesFraccionamiento();
		condicionesFraccionamiento.setId(new CondicionesFraccionamientoId());
		condicionesFraccionamiento.getId().setLineaseguroid(lineaseguroid);
		condicionesFraccionamiento.setModulo(new Modulo());
		condicionesFraccionamiento.getModulo().setId(new ModuloId());
		condicionesFraccionamiento.getModulo().getId().setCodmodulo(codmodulo);
		try {
			lista = condicionesFraccionamientoDao
					.listCondicionesFraccionamiento(condicionesFraccionamiento);
			if (null == lista || lista.size() < 1) {
				condicionesFraccionamiento.getModulo().getId()
						.setCodmodulo(Constants.TODOS_MODULOS);
				lista = condicionesFraccionamientoDao
						.listCondicionesFraccionamiento(condicionesFraccionamiento);
			}
		} catch (Exception e) {
			logger.error("Error buscando las condiciones de fraccionamiento");
			throw new DAOException(
					"Se ha producido un error buscando las condiciones de fraccionamiento. ",
					e);
		}
		return lista;

	}

	public List<CondicionesFraccionamiento> getCondicionesFraccionamiento(
			Long lineaseguroid) throws DAOException {

		List<CondicionesFraccionamiento> lista = null;
		CondicionesFraccionamiento condicionesFraccionamiento = new CondicionesFraccionamiento();
		condicionesFraccionamiento.setId(new CondicionesFraccionamientoId());
		condicionesFraccionamiento.getId().setLineaseguroid(lineaseguroid);

		try {
			lista = condicionesFraccionamientoDao
					.listCondicionesFraccionamiento(condicionesFraccionamiento);
		} catch (Exception e) {
			logger.error("Error buscando las condiciones de fraccionamiento");
			throw new DAOException(
					"Se ha producido un error buscando las condiciones de fraccionamiento. ",
					e);
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<PolizaPctComisiones> getPolizasPctComisiones(Long idPoliza) {
		List<PolizaPctComisiones> pctComisiones = null;
		pctComisiones = polizasPctComisionesDao.getObjects(
				PolizaPctComisiones.class, "poliza.idpoliza", idPoliza);
		return pctComisiones;
	}

	private List<PolizaPctComisiones> getPolizasPctComisionesPodDistCostes(
			Poliza poliza) {
		// Al dar de alta una poliza guaradamos todos los porcentajes de
		// comisiones posibles
		// por cada grupo de negocio que permite la linea. Que lo permita la
		// linea no significa
		// que la poliza vaya a tener los dos grupos de negocio
		List<PolizaPctComisiones> pctComisiones = new ArrayList<PolizaPctComisiones>();
		for (PolizaPctComisiones pctComis : poliza.getSetPolizaPctComisiones()) {
			for (DistribucionCoste2015 dstCos : poliza
					.getDistribucionCoste2015s()) {
				if (pctComis.getGrupoNegocio().equals(dstCos.getGrupoNegocio())) {
					String descGrNeg = "";
					try {
						descGrNeg = polizasPctComisionesDao
								.getDescripcionGrupoNegocio(pctComis
										.getGrupoNegocio());
					} catch (DAOException e) {
						logger.error(
								"error al obtener la descripcion del grupo de negocio",
								e);
					}
					pctComis.setDescGrupoNegocio(descGrNeg);
					pctComisiones.add(pctComis);
					break;
				}
			}
		}
		return pctComisiones;
	}

	// #####################DESCUENTOS Y RECARGOS
	// ##########################################
	@SuppressWarnings("rawtypes")
	public void actualizaPolizaPctComisionesDescuento(
			final String[] varRequest, final Poliza poliza,
			final Usuario usuario) throws Exception {
		// dctoPctComisiones contiene unidades de cuatro valores, es decir, cada
		// cuatro valores pertenecen a un grupo de negocio;
		// Nos interesan el primer valor(porcentaje decuento elegido) y el
		// segundo(id de PolizaPctComisiones):
		// Los datos provienen del popup de descuentos
		// pctelegido idPctComisiones pctMax grupoNegocio pctrecelegido
		// 12, 5406, , 1
		// 35, 5405, 2.35, 2
		Map<Long, BigDecimal> valoresPctComis = getValoresPolizaPctComisionesRequest(
				varRequest, 2, 1, 5);
		Iterator it = valoresPctComis.keySet().iterator();
		while (it.hasNext()) {
			Long key = (Long) it.next();
			BigDecimal valor = valoresPctComis.get(key);
			updateDescuento(valor, key);

			if (valor == null)
				valor = new BigDecimal(0);
			insertHistoricoDescuento(poliza, valor, usuario);

			for (PolizaPctComisiones ppc : poliza.getSetPolizaPctComisiones()) {
				if (ppc.getId().compareTo(key) == 0) {
					ppc.setPctdescelegido(valor);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void actualizaPolizaPctComisionesRecargo(final String[] varRequest,
			final Poliza poliza, final Usuario usuario) throws Exception {
		// dctoPctComisiones contiene unidades de cuatro valores, es decir, cada
		// cuatro valores pertenecen a un grupo de negocio;
		// Nos interesan el primer valor(porcentaje decuento elegido) y el
		// segundo(id de PolizaPctComisiones):
		// Los datos provienen del popup de descuentos
		// pctelegido idPctComisiones pctMax grupoNegocio
		// 12, 5406, , 1
		// 35, 5405, 2.35, 2
		Map<Long, BigDecimal> valoresPctComis = getValoresPolizaPctComisionesRequest(
				varRequest, 2, 1, 4);
		Iterator it = valoresPctComis.keySet().iterator();
		while (it.hasNext()) {
			Long key = (Long) it.next();
			BigDecimal valor = valoresPctComis.get(key);
			updateRecargo(valor, key);

			for (PolizaPctComisiones ppc : poliza.getSetPolizaPctComisiones()) {
				if (ppc.getId().compareTo(key) == 0) {
					ppc.setPctrecarelegido(valor);
				}
			}
		}
	}

	private Map<Long, BigDecimal> getValoresPolizaPctComisionesRequest(
			String[] varRequest, int indId, int indValor, int indGrupoValores) {
		Map<Long, BigDecimal> res = new HashMap<Long, BigDecimal>();
		// 5.resto = numero1%numero2;
		int ii = 1;
		Long idPctComisiones = null;
		BigDecimal valorPctComisiones = null;

		for (int i = 0; i < varRequest.length; i++) {
			if (ii == indId) {
				idPctComisiones = new Long(varRequest[i]);
			}
			if (ii == indValor) {
				if (varRequest[i].isEmpty()
						|| varRequest[i].equals(new String("0"))) {
					valorPctComisiones = null;
				} else {
					valorPctComisiones = new BigDecimal(varRequest[i]);
				}
			}
			if (ii % indGrupoValores == 0) {
				res.put(idPctComisiones, valorPctComisiones);
				idPctComisiones = null;
				valorPctComisiones = null;
				ii = 0;
			}
			ii += 1;
		}

		return res;
	}

	// ############# FIN DESCUENTOS Y RECARGOS
	// #######################################

	public String getTablaComparativaGanadoSeleccionadas(ComparativaPoliza cp,
			List<ComparativaPoliza> listComparativas, Poliza p,
			String realPath, Usuario usuario) {
		
		logger.debug("WebServicesManager - getTablaComparativaGanadoSeleccionadas [INIT]");
		String res = null;
		ModuloView mv = getModulosYCoberturasBBDD(cp, p, realPath, usuario);
		/* P0063842** MODIF TAM */
		
		if (listComparativas.size() > 0) {
			logger.debug("Entramos por el if ");
			res = WSUtils.getComparativaHeader(listComparativas, mv, true);
		/* Pet. 63482 ** MODIF TAM (29.06.2021) ** Defecto 14 */
		/* Para las p�lizas que se crean manualmente y que no tienen comparativas seleccionadas */	
		}else {
			logger.debug("Entramos por el else ");
			res = WSUtils.getComparativaHeader(listComparativas, mv, true);
		}
		/* Pet. 63482 ** MODIF TAM (29.06.2021) ** Defecto 14 Fin */
		logger.debug("WebServicesManager - getTablaComparativaGanadoSeleccionadas [INIT]");
		return res;
	}

	private ModuloView getModulosYCoberturasBBDD(ComparativaPoliza cp,
			Poliza p, String realPath, Usuario usuario) {
		ModulosYCoberturas myc = null;
		ModuloPoliza mp = null;
		ModuloView mv = null;
		logger.debug("WebServicesManager - getModulosYCoberturasBBDD [INIT]");
		try {
			logger.debug("WebServicesManager - antes del if");
			if (p.getModuloPolizas() != null) {
				logger.debug ("Entramos en el primer if");
				for (ModuloPoliza m : p.getModuloPolizas()) {
					logger.debug ("Dentro del for");
					if (cp != null) {
						logger.debug ("Dentro del if, cp not null");
						logger.debug ("Comparamos idpoliza(m) :"+m.getId().getIdpoliza() + " y el valor de cp:"+cp.getId().getIdpoliza());
						logger.debug ("Comparamos LineaseguroId(m) :" +m.getId().getLineaseguroid() + " y el valor de cp:"+cp.getId().getLineaseguroid());
						logger.debug ("Comparamos codmodulo(m) :" +m.getId().getCodmodulo() + " y el valor de cp:"+ cp.getId().getCodmodulo());
						logger.debug ("Comparamos numcomparativa(m) :" +m.getId().getNumComparativa() + " y el valor de cp:"+ cp.getId().getIdComparativa());
						if (m.getId().getIdpoliza().compareTo(cp.getId().getIdpoliza()) == 0
								&& m.getId().getLineaseguroid()
								.compareTo(cp.getId().getLineaseguroid()) == 0
								&& m.getId().getCodmodulo()
								.compareTo(cp.getId().getCodmodulo()) == 0
								&& m.getId().getNumComparativa()
								.compareTo(cp.getId().getIdComparativa()) == 0) {
							mp = m;
							break;
						}
					}	
				}
			}
			
			myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(
					p.getIdpoliza(), mp.getId().getCodmodulo());
			if (null == myc) {
				myc = seleccionComparativasSWManager.getModulosYCoberturasSW(
						mp, p, realPath, usuario);
			}
			mv = seleccionComparativasSWManager
					.getModuloViewFromModulosYCoberturas(myc, mp, mp.getId()
							.getNumComparativa().intValue(), mp.getId()
							.getCodmodulo(), true);

		} catch (Exception e) {
			logger.error("Error buscando Modulos y coberturas de la Base de Datos");
		}
		return mv;
	}
	
	
	/* Pet. 63485 ** MODIF TAM (23/07/2020) ** Inicio */
	public String getTablaComparativaAgriSeleccionadas(ComparativaPoliza cp,
			List<ComparativaPoliza> listComparativas, Poliza p,
			String realPath, Usuario usuario) {
		
		logger.debug("WebServicesManager - getTablaComparativaAgriSeleccionadas");
		String res = null;
		ModuloView mv = new ModuloView();
		
		if (p.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
			try{
				logger.debug("Poliza Enviada Correcta");
				/* ESC-12909 ** MODIF TAM (08/04/2021) ** Inicio */
				boolean isPostSep2020 = true;
				if (p.getFechaenvio() != null) {
					Date fechaEnvio = p.getFechaenvio();
					isPostSep2020 = isFechaEnvioPosteriorSep2020(fechaEnvio);
					
				}
				if (isPostSep2020) {
					mv = getModulosYCobertContratadasAgriSW(cp, p, realPath, usuario);		
				}else {
					mv = getModulosYCobertAgriBBDD(cp, p, realPath, usuario);
				}
			}catch (Exception e) {
				logger.error("Error al obtener las cobertura contratadas"  + e.getMessage(), e);
			}
			/* ESC-12909 ** MODIF TAM (08/04/2021) ** Fin */
					
		}else {
			logger.debug("Poliza No Confirmadas");			
			mv = getModulosYCobertAgriBBDD(cp, p, realPath, usuario);
		}
		
		res = WSUtils.getComparativaHeader(listComparativas, mv, false);
			
		return res;
	}
	
	
	/* ESC-12909 ** MODIF TAM (08/04/2021) ** Inicio */
	/* Para aquellas polizas cuya contratacion sea inferior a la implantacion de la peticion 63485 habra que 
	 * consultar los Modulos y Coberturas normales y no las contratadas.
	 */
	public boolean isFechaEnvioPosteriorSep2020(Date fechaEnvio) throws ParseException {
		
		boolean valor = false;
		String fecStr = "2020-09-16";
		Date fecEfecto;
		
		fecEfecto = new SimpleDateFormat("yyyy-MM-dd").parse(fecStr);
		
		if(fechaEnvio.compareTo(fecEfecto) > 0) { //si la fechaEnvio es posterior a fecEfecto (2020-09-16)
			valor = true;
		}
		
		return valor;
	}
	
	/* Pet. 63485 ** MODIF TAM (23/07/2020) ** Inicio */
	public String consultaTablaComparativaAgriSeleccionadas(ComparativaPoliza cp,
			List<ComparativaPoliza> listComparativas, Poliza p,
			String realPath, Usuario usuario) {
		
		logger.debug("WebServicesManager - getTablaComparativaAgriSeleccionadas");
		String res = null;
		
		if (p.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
			logger.debug("Poliza Enviada Correcta");
			
			ModuloView mv = getModulosYCobertContratadasAgriSW(cp, p, realPath, usuario);

			res = WSUtils.getComparativaHeader(listComparativas, mv, false);
			
		}else {
			logger.debug("Poliza No Confirmadas, consultamos datos en BBDD");
			
			ModulosYCoberturas myc = null;
			ModuloPoliza mp = null;
			ModuloView mv = null;
			try {
				for (ModuloPoliza m : p.getModuloPolizas()) {
					if (m.getId().getIdpoliza().compareTo(cp.getId().getIdpoliza()) == 0
							&& m.getId().getLineaseguroid()
									.compareTo(cp.getId().getLineaseguroid()) == 0
							&& m.getId().getCodmodulo()
									.compareTo(cp.getId().getCodmodulo()) == 0
							&& m.getId().getNumComparativa()
									.compareTo(cp.getId().getIdComparativa()) == 0) {
						mp = m;
						break;
					}
				}
				myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(
						p.getIdpoliza(), mp.getId().getCodmodulo());
				
				if (myc != null) {
					mv = seleccionComparativasSWManager.getModuloViewFromModulosYCobertAgricolas(myc, mp, mp.getId()
							.getNumComparativa().intValue(), mp.getId().getCodmodulo());
					
					res = WSUtils.getComparativaHeader(listComparativas, mv, false);

					
				}

			} catch (Exception e) {
				logger.error(" Error buscando Modulos y coberturas de la Base de Datos de Agricolas");
			}


			
		}
			
		return res;
	}
	
	private ModuloView getModulosYCobertAgriBBDD(ComparativaPoliza cp,
			Poliza p, String realPath, Usuario usuario) {
		ModulosYCoberturas myc = null;
		ModuloPoliza mp = null;
		ModuloView mv = null;
		try {
			for (ModuloPoliza m : p.getModuloPolizas()) {
				if (m.getId().getIdpoliza().compareTo(cp.getId().getIdpoliza()) == 0
						&& m.getId().getLineaseguroid()
								.compareTo(cp.getId().getLineaseguroid()) == 0
						&& m.getId().getCodmodulo()
								.compareTo(cp.getId().getCodmodulo()) == 0
						&& m.getId().getNumComparativa()
								.compareTo(cp.getId().getIdComparativa()) == 0) {
					mp = m;
					break;
				}
			}
			myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(
					p.getIdpoliza(), mp.getId().getCodmodulo());
			if (null == myc) {
				myc = seleccionComparativasSWManager.getModulosYCoberturasAgriSW(
						mp, p, realPath, usuario);
			}
			mv = seleccionComparativasSWManager.getModuloViewFromModulosYCobertAgricolas(myc, mp, mp.getId()
							.getNumComparativa().intValue(), mp.getId().getCodmodulo());

		} catch (Exception e) {
			logger.error("Error  buscando Modulos y coberturas de la Base de Datos de Agricolas");
		}
		return mv;
	}
	
	private ModuloView getModulosYCobertContratadasAgriSW(ComparativaPoliza cp,
			Poliza p, String realPath, Usuario usuario) {
		
		logger.debug("WebServicesManager - getModulosYCobertContratadasAgriSW [INIT]");
		
		ModulosYCoberturas myc = null;
		ModuloPoliza mp = null;
		ModuloView mv = null;
		try {
			for (ModuloPoliza m : p.getModuloPolizas()) {
				if (m.getId().getIdpoliza().compareTo(cp.getId().getIdpoliza()) == 0
						&& m.getId().getLineaseguroid()
								.compareTo(cp.getId().getLineaseguroid()) == 0
						&& m.getId().getCodmodulo()
								.compareTo(cp.getId().getCodmodulo()) == 0
						&& m.getId().getNumComparativa()
								.compareTo(cp.getId().getIdComparativa()) == 0) {
					mp = m;
					break;
				}
			}
			logger.debug(" Antes de getMyCPolizaAgricola");
			myc = seleccionComparativasSWManager.getMyCPolizaAgricola(mp, p, realPath);
			
			if (myc != null){
				logger.debug("El valor de myc es != nulo");
				mv = seleccionComparativasSWManager.getModuloViewFromModulosYCobertAgricolas(myc, mp, mp.getId()
						.getNumComparativa().intValue(), mp.getId().getCodmodulo());				
			}

		} catch (Exception e) {
			logger.error("Error buscando  Modulos y coberturas de la Base de Datos de Agricolas");
		}
		return mv;
	}
	
	public VistaImportes consultaDataForImportesByDCAgri(
			List<DistribucionCoste2015> listDc,
			com.rsi.agp.dao.tables.poliza.Poliza poliza,
			com.rsi.agp.dao.tables.poliza.ComparativaPoliza cp,
			Usuario usuario, String realPath) {

		if (listDc == null)
			throw new NullPointerException(
					"No se ha podido recuperar la distribucion de costes de la poliza");

		// Se recupera la poliza actual
		if (poliza == null)
			throw new NullPointerException(
					"No se ha podido recuperar la poliza actual en la sesion.");
		if (poliza == null || poliza.getComparativaPolizas() == null)
			throw new NullPointerException(
					"No se ha podido recuperar la poliza actual o no tiene comparativas!");
		
		boolean esGanado = poliza.getLinea().isLineaGanado();

		VistaImportes vistaImportes = setDatosComparativa(cp, esGanado);
		
		// distribucionCostes
		dc2015(vistaImportes, listDc, poliza.getLinea().isLineaGanado(),
				cp.getEsFinanciada());

		BigDecimal pctMinimoFinanciacion;
		try {
			pctMinimoFinanciacion = pagoPolizaManager.getPctMinimoFinanciacion(
					poliza.getLinea().getCodplan(), poliza.getLinea()
							.getCodlinea(), cp.getId().getCodmodulo());
			if (null != pctMinimoFinanciacion) {
				vistaImportes
						.setPctMinFinanSobreCosteTomador(pctMinimoFinanciacion
								.toString());
			}
		} catch (Exception e) {

		}

		// *************************************************////
		// Se genera el ID de la comparativa y se pone como valor del Radio
		// Button
			ModulosYCoberturas myc = null;
			ModuloPoliza mp = null;
			
			/* Antes de generarComparativa, comprobamos si tiene datos del xml de Modulos y Coberturas en BBDD
			 * ya que en caso de no tener, no se muestran las comparativas 
			 */
			try {
				for (ModuloPoliza m : poliza.getModuloPolizas()) {
					if (m.getId().getIdpoliza().compareTo(cp.getId().getIdpoliza()) == 0
							&& m.getId().getLineaseguroid()
									.compareTo(cp.getId().getLineaseguroid()) == 0
							&& m.getId().getCodmodulo()
									.compareTo(cp.getId().getCodmodulo()) == 0
							&& m.getId().getNumComparativa()
									.compareTo(cp.getId().getIdComparativa()) == 0) {
						mp = m;
						break;
					}
				}
				myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(
						poliza.getIdpoliza(), mp.getId().getCodmodulo());
				
				if (null != myc) {
					generarComparativa(new Long(0), poliza, cp, vistaImportes, realPath, usuario);
				}


			} catch (Exception e) {
				logger.error("Error buscando Modulos y coberturas de la Base de Datos de Agricolas");
			}

		// Si la poliza es de agrarios se obtiene el total de produccion
		if (!esGanado) {
			String totalProduccion = "";
			totalProduccion = polizaDao.getTotalProdComparativa(cp
					.getRiesgoCubiertoModulo().getModulo().getId()
					.getCodmodulo(), poliza);
			vistaImportes.setTotalProduccion(totalProduccion);
		}

		// Si la poliza es de plan >= 2015 se cargan los datos de comisiones
		if (poliza.isPlanMayorIgual2015()) {
			vistaImportes = polizasPctComisionesManager.dameComisiones(
					vistaImportes, poliza, usuario);
		}

		return vistaImportes;

	}
	/* Pet. 63485 ** MODIF TAM (23/07/2020) ** Fin */

	/**
	 * @param polizaDao
	 *            the polizaDao to set
	 */
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setCaracteristicaExplotacionDao(
			ICaracteristicaExplotacionDao caracteristicaExplotacionDao) {
		this.caracteristicaExplotacionDao = caracteristicaExplotacionDao;
	}

	public void setComparativaDao(IComparativaDao comparativaDao) {
		this.comparativaDao = comparativaDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setCaracteristicaExplotacionManager(
			CaracteristicaExplotacionManager caracteristicaExplotacionManager) {
		this.caracteristicaExplotacionManager = caracteristicaExplotacionManager;
	}

	public void setCuadroCoberturasManager(
			ICuadroCoberturasManager cuadroCoberturasManager) {
		this.cuadroCoberturasManager = cuadroCoberturasManager;
	}

	public void setDistribucionCosteDAO(
			IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}

	public void setPolizasPctComisionesDao(
			IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}

	public void setPolizasPctComisionesManager(
			IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}

	public void setHistoricoWSManager(HistoricoWSManager historicoWSManager) {
		this.historicoWSManager = historicoWSManager;
	}

	public void setSeleccionComparativasSWManager(
			ISeleccionComparativasSWManager seleccionComparativasSWManager) {
		this.seleccionComparativasSWManager = seleccionComparativasSWManager;
	}

	public void setCondicionesFraccionamientoDao(
			ICondicionesFraccionamientoDao condicionesFraccionamientoDao) {
		this.condicionesFraccionamientoDao = condicionesFraccionamientoDao;

	}

	public void setErrorWsAccionDao(IErrorWsAccionDao errorWsAccionDao) {
		this.errorWsAccionDao = errorWsAccionDao;
	}

	// Pet. 22208 ** MODIF TAM (06.03.2018) ** Inicio //
	// creamos el set de las nuevas instancias creadas //
	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setSeleccionPolizaDao(ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}
	// Pet. 22208 ** MODIF TAM (06.03.2018) ** Fin //
	
	public void setImportesFraccDao(IImportesFraccDao importesFraccDao) {
		this.importesFraccDao = importesFraccDao;
	}
	
	private boolean esComparativaFinanciada(ComparativaPoliza cp, String moduloFinanciado, String filaFinanciada,
			String idModulo) {
		return ((null != moduloFinanciado && null != filaFinanciada && null == idModulo)
				&& (cp.getId().getCodmodulo().trim().compareTo(moduloFinanciado.trim()) == 0
						&& cp.getId().getFilacomparativa().compareTo(new BigDecimal(filaFinanciada)) == 0
						&& cp.getId().getIdComparativa().compareTo(new Long(idModulo)) == 0));
	}

	public Map<Character, ComsPctCalculado> getComsPctCalculadoComp(final Long idComparativa) {
		@SuppressWarnings("unchecked")
		List<ComsPctCalculado> aux = this.polizasPctComisionesDao.getObjects(ComsPctCalculado.class, "id.idComparativa", idComparativa);
		Map<Character, ComsPctCalculado> comsPctCalculado = new HashMap<Character, ComsPctCalculado>(aux.size());
		for (ComsPctCalculado obj : aux) {
			logger.debug("Encontrado % calculado " + obj.getPctCalculado() + " para el grupo " + obj.getId().getIdGrupo());
			comsPctCalculado.put(obj.getId().getIdGrupo(), obj);
		}
		return comsPctCalculado;
	}

	public void setMtoComisionesRenovService(IMtoComisionesRenovService mtoComisionesRenovService) {
		this.mtoComisionesRenovService = mtoComisionesRenovService;
	}
}