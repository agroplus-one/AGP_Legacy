package com.rsi.agp.core.webapp.action;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.impl.rc.InformesGanadoRCService;
import com.rsi.agp.core.managers.IPasarADefinitivaPlzManager;
import com.rsi.agp.core.managers.ged.impl.DocumentacionGedManager;
import com.rsi.agp.core.managers.impl.InformesManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.managers.impl.PasarADefinitivaPlzManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.SocioSubvencionManager;
// Pet. 22208 ** MODIF TAM (22.03.2018) ** Inicio //
import com.rsi.agp.core.managers.impl.WebServicesCplManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.report.layout.BeanTablaCoberturas;
//Pet. 22208 ** MODIF TAM (22.03.2018) ** Fin //
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.util.JRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.helper.InformesGanadoRCHelper;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper;
import com.rsi.agp.core.webapp.util.FirmaTabletaXmlHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.ws.ResultadoWS;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.pagination.PaginatedListImpl;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.Documento;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class PasarADefinitivaPlzController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(PasarADefinitivaPlzController.class);
	private IPasarADefinitivaPlzManager pasarADefinitivaPlzManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private PolizaManager polizaManager;
	private WebServicesManager webServicesManager;
	private WebServicesCplManager webServicesCplManager;
	private InformesGanadoRCHelper informesGanadoRCHelper;
	private InformesGanadoRCService informesGanadoRCService;
	private ParametrizacionManager parametrizacionManager;
	private DocumentacionGedManager documentacionGedManager;
	private InformesManager informesManager;
	private PagoPolizaManager pagoPolizaManager;
	private SocioSubvencionManager socioSubvencionManager;
	
	private SessionFactory sessionFactory;
	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza el proceso de paso a definitiva de una poliza
	 * 
	 * @param request
	 * @param response
	 * @param polizaDefinitiva
	 *            Bean que encapsula el id de poliza necesario para las operaciones
	 * @return Redireccion a jsp correspondiente
	 * @throws DAOException
	 */
	@SuppressWarnings("all")
	public ModelAndView doPasarADefinitiva(final HttpServletRequest request, final HttpServletResponse response,
			final Poliza polizaDefinitiva) throws Exception {

		log("doPasarADefinitiva", "inicio");
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String idInternoPe = request.getParameter("idInternoPe");
		String firmaDiferida = request.getParameter("firmaDiferida");
		String docFirmada = request.getParameter("docFirmada");

		// Se recupera el id de poliza que hay que pasar a definitiva
		final Long idPolizaDefinitiva = polizaDefinitiva.getIdpoliza();
		log("doPasarADefinitiva", "Paso a definitiva de la poliza con id = " + idPolizaDefinitiva);

		/*
		 * Antes de pasar a definitiva, guardamos los valores de los checks de
		 * confirmacion (Nota previa, IPID, RGPD) en BBDD
		 */
		Poliza poliza = polizaManager.getPoliza(new Long(idPolizaDefinitiva));

		log("**@@** doPasarADefinitiva",
				"Paso a definitiva, estado de la poliza :" + poliza.getEstadoPoliza().getIdestado());

		if (StringUtils.nullToString(request.getParameter("notaPreviaInput")).equals("true")) {
			poliza.setNotaPrevia(1);
		} else if (StringUtils.nullToString(request.getParameter("notaPreviaInput")).equals("false")) {
			poliza.setNotaPrevia(0);
		}

		if (StringUtils.nullToString(request.getParameter("IPIDInput")).equals("true")) {
			poliza.setIPID(1);
		} else if (StringUtils.nullToString(request.getParameter("IPIDInput")).equals("false")) {
			poliza.setIPID(0);
		}

		if (StringUtils.nullToString(request.getParameter("RGPDInput")).equals("true")) {
			poliza.setRGPD(1);
		} else if (StringUtils.nullToString(request.getParameter("RGPDInput")).equals("false")) {
			poliza.setRGPD(0);
		}

		polizaManager.savePoliza(poliza);
		

		// Carga los parametros enviados desde la jsp o que se necesiten en el manager
		log("doPasarADefinitiva", "Carga de parametros desde la jsp");
		Map<String, Object> parametros = cargaParametros(request);

		/* Comprobacion estado Enviada Correcta [ESC - 5876] */
		Map<String, Object> errores = new HashMap<String, Object>();
		log("doPasarADefinitiva",
				"Comprobamos si el estado no es Enviada correcta para poder contiuar con el paso a definitiva");

		BigDecimal idEstadoPoliza = poliza.getEstadoPoliza().getIdestado();
		log("**@@**doPasarADefinitiva", "Comprobamos valor de idEstadoPoliza: " + idEstadoPoliza);

		if (idEstadoPoliza != null && ConstantsSbp.ENVIADA_CORRECTA.equals(idEstadoPoliza)) {
			
				log("doPasarADefinitiva", "El estado ya esta en Enviada correcta");

				// ModelAndView mv = generarRedireccion(parametros, request, errores,
				// polizaDefinitiva);
				// return mv;
				errores.put("alerta",
						"La poliza ya se habia enviado antes a Agroseguro. No se puede pasar a definitiva");

				String modoLecturaAux = "modoLectura";
				parametros.put("modoLectura", modoLecturaAux);
				return redirigirAlertaPolizaEnviada(parametros, errores, polizaDefinitiva.getIdpoliza(),
						StringUtils.nullToString(parametros.get("cicloPoliza")));
		}
		
		

		// Llamada al manager para el paso a definitiva
		errores = pasarADefinitivaPlzManager.doPasarADefinitiva(idPolizaDefinitiva, parametros, request);
		
		log("generarRedireccion", "errores.size(): " + errores.size());
		log("generarRedireccion", "errores.containsKey(\"autoCompFirma\"): " + errores.containsKey("autoCompFirma"));

		
		AcuseRecibo acuseRecibo = null;
		acuseRecibo = (AcuseRecibo) errores.get("acusePpal");
		
		boolean swConfirm = (Boolean) parametros.get("swConfirmacion");
		boolean esCplAux = Boolean.parseBoolean(request.getParameter("esCpl"));

		if (null == acuseRecibo)
			acuseRecibo = (AcuseRecibo) errores.get("resultado");

		// de errores sacar el acuse (si no viene, meterlo anteriormente como se hace
		// con la complementaria) y asi en muestraBotonPasoDef le pasas el acuse ya
		// limpio de
		// errores y no hay q recoger el acuse y limpiar de nuevo
		// Habilitamos o deshabilitamos el boton forzar paso a definitiva en funcion del
		// perfil de los errores
		if (acuseRecibo != null) {
			boolean mostrarForzaDef = pasarADefinitivaPlzManager.muestraBotonPasoDef(idPolizaDefinitiva, acuseRecibo,
					usuario);
			parametros.put("mostrarForzaDef", mostrarForzaDef);
		}

		// Pet. 22208 ** MODIF TAM (01.03.2018) ** Inicio //
		// Incluimos validacion para saber si venimos de Paso a Definitiva normal
		// o por SW Confirmacion
		ModelAndView mv = null;

		// Pet. 22208 ** MODIF TAM (04.04.2018) - Resolucion Incidencias //
		boolean pasoADefinitivaForzadoSwConfirm = !"".equals((String) parametros.get("grFueraContratacion"));
		

		// Redireccion y envio de parametros a jsp
		log("doPasarADefinitiva", "@@**@@ VERSION 1.0");
		log("doPasarADefinitiva", "@@**@@-Antes de validar si entramos por via normal o Sw confirmacion");
		log("doPasarADefinitiva", "@@**@@-Valor de swConfirm:" + swConfirm);
		log("doPasarADefinitiva", "@@**@@-Valor de pasoADefinitivaForzadoSwConfirm:" + pasoADefinitivaForzadoSwConfirm);
		// MODIF TAM (06.03.2018) ** Si se retornan errores
		// en el WS de validacion habria que parar la contratacion y que se fuerze el
		// pase a Definitiva.
		

		if (polizaDefinitiva.getTipoReferencia() == null) {
			if (esCplAux == true) {
				polizaDefinitiva.setTipoReferencia(Constants.MODULO_POLIZA_COMPLEMENTARIO);
			}
		}
		
		
		if (swConfirm == true && ((acuseRecibo == null && errores.size() == 0) || pasoADefinitivaForzadoSwConfirm)) {

			log("doPasarADefinitiva", "@@**@@-Entramos por SWConfirmacion");

			// Si es True -> Venimos del boton Sw confirmacion
			String operacion = StringUtils.nullToString(parametros.get("origenllamada"));
			// deberia llegar el valor "confirmar"

			String origenllamada = StringUtils.nullToString(parametros.get("origenllamada"));
			String modoLectura = StringUtils.nullToString(parametros.get("modoLectura"));
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			boolean actualizaDistribucionCostes = false;
			Parametro parameters = null;

			// MODIF TAM (10.04.2018) ** Resolucion de Incidencias //
			// si venimos de forzar el paso a definitiva
			if (pasoADefinitivaForzadoSwConfirm == true) {
				if (esCplAux == true) {
					polizaDefinitiva.setTipoReferencia(Constants.MODULO_POLIZA_COMPLEMENTARIO);
				}
			}

			Poliza p = seleccionPolizaManager.getPolizaById(new Long(idPolizaDefinitiva));
			
			// MODIF TAM (22.03.2018) ** Inicio
			// Si se trata de una poliza complementaria lanzaremos la llamada al WebService
			// desde otro WebServicesCplManager
			final boolean esComplementario = Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(p.getTipoReferencia());
			if (esComplementario) {

				log("doPasarADefinitiva", "@@**@@-Lanzamos llamada al WS Confirmacion de poliza Cpl");

				try {
					// Inserta en la tabla de envios a Agroseguro el xml generado correspondiente a
					// la poliza y retorna el idEnvio
					Long idEnvio = null;
					try {
						// MODIF TAM (10.04.2018) ** Resolucion Incidencias //
						Long idComparativa = p.getModuloPolizas().iterator().next().getId().getNumComparativa();
						Map<Character, ComsPctCalculado> comsPctCalculado = this.webServicesManager
								.getComsPctCalculadoComp(idComparativa);
						idEnvio = webServicesCplManager.generateAndSaveXMLPolizaCpl(p, Constants.WS_CONFIRMACION,
								comsPctCalculado, null);
					} catch (Exception e) {
						logger.error(
								"Error al generar el xml de la Confirmacion de poliza complementaria e insertar en la tabla de envios",
								e);
					}

					acuseRecibo = webServicesCplManager.ConfirmarPolizaCpl(idEnvio, polizaDefinitiva.getIdpoliza(),
							polizaDefinitiva, realPath, request);

					String cicloPoliza = StringUtils.nullToString(parametros.get("cicloPoliza"));
					String vieneDeUtilidades = StringUtils.nullToString(parametros.get("vieneDeUtilidades"));

					log("doPasarADefinitiva", "@@**@@-Valor de cicloPoliza(Complementaria):" + cicloPoliza);
					log("doPasarADefinitiva", "@@**@@-Valor de vieneDeUtilidades(Complementaria):" + vieneDeUtilidades);
				} catch (DAOException e) {
					logger.error("Error al confirmar por SW la poliza Complementaria", e);
				} catch (BusinessException e) {
					logger.error("Error al confirmar por SW la poliza Complementaria", e);
				} catch (Exception e) {
					logger.error("Error al confirmar por SW la poliza Complementaria", e);
				}

				if (acuseRecibo != null) {

					Parametro parametro = parametrizacionManager.getParametro();

					// Poliza complementaria
					errores.put("erroresAcuseReciboCpl", true);
					errores.put("resultado", acuseRecibo);

					Documento documentoArray = ((AcuseRecibo) errores.get("resultado")).getDocumentoArray(0);
					if (documentoArray.getErrorArray() != null && documentoArray.getErrorArray().length > 0) {
						parametros.put("errores", Arrays.asList(documentoArray.getErrorArray()));
						parametros.put("errLength", documentoArray.getErrorArray().length);
					}

					String mensaje = null;
					String alerta = null;
					if (acuseRecibo.getDocumentoArray(0).getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
						
						// SI NO TIENE DOCUMENTACION LA GENERAMOS
						String idDocumentum = this.documentacionGedManager.getIdDocumentum(poliza.getIdpoliza());
						
						if (StringUtils.isNullOrEmpty(idDocumentum) || Constants.STRING_NA.equals(idDocumentum)) {
							
							BigDecimal entidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
							BigDecimal subEntidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();
							
							Object fechaFinContratacion = this.informesManager.getFechaContratacion(poliza,poliza.getCodmodulo(),"fecfincontrata");
							
							List<BeanTablaCoberturas> infoCoberturasComp = this.informesManager.getDatosCoberturasGarantiasComplementaria(poliza);
							
							String tipoPago = this.informesManager.getFormaPago(poliza);
							boolean isPagoFraccionado = this.pagoPolizaManager.compruebaPagoFraccionado(poliza);						
							Map<String, String> mapSub = this.informesManager.getPorcentajesDistSub(poliza);
							Map<String, String> mapNotas = this.informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(), ConstantsInf.NOTA_INF_AGRO);
							
							String codTerminal = "";
							
							if (request.getSession().getAttribute("codTerminal") != null)
								codTerminal = request.getSession().getAttribute("codTerminal").toString();
							
							String tipoIdentificador = poliza.getAsegurado().getTipoidentificacion();
							Boolean aseguradoVulnerable = Boolean.FALSE;
							
							if ("NIF".equals(tipoIdentificador)) {
								String key = "";
								if (codTerminal.isEmpty()) {
									key = bundle.getString("aseguradoVulnerable.secret.NoTF");
								}else {
									key = bundle.getString("aseguradoVulnerable.secret.TF");
								}
								AseguradoIrisHelper helper = new AseguradoIrisHelper();
								logger.debug("Inicio de busqueda de asegurado vulnerable.");
								aseguradoVulnerable = helper.isAseguradoVulnerable(poliza.getAsegurado().getEntidad().getCodentidad().toString(), poliza.getAsegurado().getNifcif(), "F", key, codTerminal, usuario.getCodusuario());
								logger.debug("Fin de busqueda de asegurado vulnerable: " + aseguradoVulnerable);
							}
							
							
							
							JasperPrint jp = JRUtils.getInformePolizaComplementaria(
									this.getServletContext().getRealPath("plantillas"), poliza, entidad,
									subEntidad, infoCoberturasComp, mapSub, (Date) fechaFinContratacion, tipoPago,
									isPagoFraccionado, true, mapNotas,
									sessionFactory.getCurrentSession().connection(),aseguradoVulnerable);
							
							SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
	
							configuration.setMetadataAuthor("RGA Agroplus");
							configuration.setMetadataCreator("RGA Agroplus");
	
							String nombreInforme = "DeclaracionSeguro_" + poliza.getIdpoliza() + ".pdf";
							JRPdfExporter exporter = new JRPdfExporter();
							// exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
							exporter.setExporterInput(new SimpleExporterInput(jp));
							exporter.setConfiguration(configuration);
	
							SimpleOutputStreamExporterOutput exporterOutput;
	
							int numPagObjAseg = JRUtils.getNumPagsParcelasCompl(poliza.getParcelas());
							String xmlFirma = FirmaTabletaXmlHelper.getXmlFirma(JRUtils.getFirmasDocPolizaComp(poliza.getAsegurado(), mapNotas != null,
									entidad.toString(), idInternoPe, numPagObjAseg, aseguradoVulnerable),
									nombreInforme);
	
							PdfReader pdfReader = null;
							PdfStamper pdfStamper = null;
	
							try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
									ByteArrayOutputStream bos2 = new ByteArrayOutputStream()) {
								exporterOutput = new SimpleOutputStreamExporterOutput(bos);
								exporter.setExporterOutput(exporterOutput);
								exporter.exportReport();
								pdfReader = new PdfReader(bos.toByteArray());
								pdfStamper = new PdfStamper(pdfReader, bos2);
								pdfStamper.setXmpMetadata(xmlFirma.getBytes());
								pdfStamper.close();
								idDocumentum = this.documentacionGedManager.uploadDocumentoPoliza(
										usuario.getCodusuario(), bos2.toByteArray(), poliza,
										Constants.STRING_N, Constants.CHARACTER_N, Constants.CANAL_FIRMA_PDTE);
								parametros.put("idDocumentum", idDocumentum);
							} catch (Exception e) {
								logger.error("Error al generar el report con metadatos y/o subir el documento a GED.", e);
								throw new BusinessException(e);
							} finally {
								if (pdfReader != null)
									pdfReader.close();
							}
						}
						
						if (Constants.STRING_S.equals(firmaDiferida)) {
							this.documentacionGedManager.marcarComoDiferida(idPolizaDefinitiva);
						}
						
						parametros.put("mensaje", "Poliza confirmada con agroseguro");
					} else {
						
						parametros.put("alerta", "Poliza rechazada con agroseguro");
					}

					mv = redirigirErroresValidacionCpl(parametros, errores, polizaDefinitiva,
							StringUtils.nullToString(parametros.get("cicloPoliza")), usuario);
				}

				log("doPasarADefinitiva", "@@**@@-IdEstado resultado confirmacion:-"
						+ polizaDefinitiva.getEstadoPoliza().getIdestado() + "-");
			} else {

				// Pet. 22208 ** MODIF TAM (05.04.2018) - Resolucion de Incidencias //
				// Si estamos forzando la llamada al Webservice (Ejecutamos boton Forzar Paso a
				// Definitiva, despues de haber saltado errores de validacion
				// al haber ejecutado el boton Sw Confirmacion

				// LANZAMOS LLAMADA AL WEBSERVICE PARA LAS POLIZAS PRINCIPALES
				p.getComparativaPolizas();
				logger.debug("Obtenidas comparativas");
				
				log("doPasarADefinitiva", "@@**@@-Lanzamos llamada al WS Confirmacion de poliza Ppal");
				mv = webServicesManager.callWebService(p, Constants.WS_CONFIRMACION, origenllamada, modoLectura,
						realPath, parameters, null, request, actualizaDistribucionCostes);

				String cicloPoliza = StringUtils.nullToString(parametros.get("cicloPoliza"));
				String vieneDeUtilidades = StringUtils.nullToString(parametros.get("vieneDeUtilidades"));

				log("doPasarADefinitiva", "@@**@@-Valor de cicloPoliza(Ppal):-" + cicloPoliza + "-");
				log("doPasarADefinitiva", "@@**@@-Valor de vieneDeUtilidades(Ppal):-" + vieneDeUtilidades + "-");

				BigDecimal idEstado = p.getEstadoPoliza().getIdestado();
				
				if (Constants.ESTADO_POLIZA_DEFINITIVA.equals(idEstado)) {
					
					// SI NO TIENE DOCUMENTACION LA GENERAMOS
					String idDocumentum = this.documentacionGedManager.getIdDocumentum(poliza.getIdpoliza());
					
					if (StringUtils.isNullOrEmpty(idDocumentum) || Constants.STRING_NA.equals(idDocumentum)) {
						
						boolean esGanado = (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0);
						
						BigDecimal entidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
						BigDecimal subEntidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();
						
						Object fechaFinContratacion = this.informesManager.getFechaContratacion(poliza,poliza.getCodmodulo(),"fecfincontrata");
						
						List<Socio> lstSocios = this.socioSubvencionManager.getInformeSociosPoliza(poliza.getIdpoliza(),
								esGanado);
						HashMap<String, Object> lstCompNoElegidas = this.polizaManager.getMapaCompNoElegidas(poliza);
						
						HashMap<String, List> infoCoberturas = this.informesManager.getDatosCoberturasGarantias(poliza, null,
								null, false, null, null, this.getServletContext().getRealPath("/WEB-INF/"));
						
						HashMap<Long, HashMap<String, List>> infoNumCobert = null;
						HashMap<Long, HashMap<String, List>> infoNumExpCobertExplotaciones = null;
						HashMap<Long, HashMap<String, List>> infoNumExpCobertParcelas = null;
						Map<String, String> coberturasElegidasParcela = new HashMap<String, String>();
						if (esGanado) {
							fechaFinContratacion = this.informesManager.getFechaContratacionGan(poliza,poliza.getCodmodulo(),"fecContratFin");
							infoNumExpCobertExplotaciones = new HashMap<Long, HashMap<String, List>>();
							for (Explotacion explotacion : poliza.getExplotacions()) {
								Long num_explo = new Long(explotacion.getNumero());
								HashMap<String, List> infoCobertExplotaciones = this.informesManager
										.getDatosCobertExplotaciones(poliza, explotacion.getNumero(), null, false, null);
								infoNumExpCobertExplotaciones.put(num_explo, infoCobertExplotaciones);
							}
							infoNumCobert = infoNumExpCobertExplotaciones;
						} else {
							fechaFinContratacion = this.informesManager.getFechaContratacion(poliza,poliza.getCodmodulo(),"fecfincontrata");
							infoNumExpCobertParcelas = new HashMap<Long, HashMap<String, List>>();
							for (Parcela parcela : poliza.getParcelas()) {
	
								Long idParcela = parcela.getIdparcela();
								HashMap<String, List> infoCobertParcelas = this.informesManager.getDatosCobertParcelas(poliza,
										parcela.getNumero(), parcela.getHoja(), idParcela, null, false, null);
								String num_parcStr = parcela.getHoja().toString() + parcela.getNumero().toString();
								Long numParc = new Long(num_parcStr);
								infoNumExpCobertParcelas.put(numParc, infoCobertParcelas);
							}
							infoNumCobert = infoNumExpCobertParcelas;
						}
						
						String tipoPago = this.informesManager.getFormaPago(poliza);
						boolean isPagoFraccionado = this.pagoPolizaManager.compruebaPagoFraccionado(poliza);						
						Map<String, String> mapSub = this.informesManager.getPorcentajesDistSub(poliza);
						Map<String, String> mapNotas = this.informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(), ConstantsInf.NOTA_INF_AGRO);
						
						Boolean polizaTieneSubvCaractAseguradoPersonaJuridica = this.informesManager.polizaTieneSubvCaractAseguradoPersonaJuridica(poliza);
						
						String codTerminal = "";
						
						if (request.getSession().getAttribute("codTerminal") != null)
							codTerminal = request.getSession().getAttribute("codTerminal").toString();
						
						String tipoIdentificador = poliza.getAsegurado().getTipoidentificacion();
						Boolean aseguradoVulnerable = Boolean.FALSE;
						
						if ("NIF".equals(tipoIdentificador)) {
							String key = "";
							if (codTerminal.isEmpty()) {
								key = bundle.getString("aseguradoVulnerable.secret.NoTF");
							}else {
								key = bundle.getString("aseguradoVulnerable.secret.TF");
							}
							AseguradoIrisHelper helper = new AseguradoIrisHelper();
							logger.debug("Inicio de busqueda de asegurado vulnerable.");
							aseguradoVulnerable = helper.isAseguradoVulnerable(poliza.getAsegurado().getEntidad().getCodentidad().toString(), poliza.getAsegurado().getNifcif(), "F", key, codTerminal, usuario.getCodusuario());
							logger.debug("Fin de busqueda de asegurado vulnerable: " + aseguradoVulnerable);
							
						}
						
						JasperPrint jp = JRUtils.getInformePoliza(this.getServletContext().getRealPath("plantillas"),
								poliza, entidad, subEntidad, esGanado, lstSocios, lstCompNoElegidas, "false",
								infoCoberturas, infoNumCobert, coberturasElegidasParcela, mapSub, fechaFinContratacion,
								tipoPago, isPagoFraccionado, polizaTieneSubvCaractAseguradoPersonaJuridica, true, mapNotas,
								sessionFactory.getCurrentSession().connection(), this.documentacionGedManager, aseguradoVulnerable);
						
						SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
	
						configuration.setMetadataAuthor("RGA Agroplus");
						configuration.setMetadataCreator("RGA Agroplus");
	
						String nombreInforme = "DeclaracionSeguro_" + poliza.getIdpoliza() + ".pdf";
						JRPdfExporter exporter = new JRPdfExporter();
						// exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
						exporter.setExporterInput(new SimpleExporterInput(jp));
						exporter.setConfiguration(configuration);
	
						SimpleOutputStreamExporterOutput exporterOutput;
	
						int numPagObjAseg = esGanado ? JRUtils.getNumPagsExplotaciones(poliza.getExplotacions())
								: JRUtils.getNumPagsParcelas(poliza.getParcelas());
						String xmlFirma = FirmaTabletaXmlHelper.getXmlFirma(esGanado
								? JRUtils.getFirmasDocPolizaGan(poliza.getAsegurado(), mapNotas != null, entidad.toString(),
										idInternoPe, numPagObjAseg)
								: JRUtils.getFirmasDocPoliza(poliza.getAsegurado(), mapNotas != null, entidad.toString(),
										idInternoPe, numPagObjAseg, aseguradoVulnerable),
								nombreInforme);
	
						PdfReader pdfReader = null;
						PdfStamper pdfStamper = null;
	
						try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
								ByteArrayOutputStream bos2 = new ByteArrayOutputStream()) {
							exporterOutput = new SimpleOutputStreamExporterOutput(bos);
							exporter.setExporterOutput(exporterOutput);
							exporter.exportReport();
							pdfReader = new PdfReader(bos.toByteArray());
							pdfStamper = new PdfStamper(pdfReader, bos2);
							pdfStamper.setXmpMetadata(xmlFirma.getBytes());
							pdfStamper.close();
							idDocumentum = this.documentacionGedManager.uploadDocumentoPoliza(
									usuario.getCodusuario(), bos2.toByteArray(), poliza, Constants.STRING_N,
									Constants.CHARACTER_N, Constants.CANAL_FIRMA_PDTE);
							parametros.put("idDocumentum", idDocumentum);
						} catch (Exception e) {
							logger.error("Error al generar el report con metadatos y/o subir el documento a GED.", e);
							throw new BusinessException(e);
						} finally {
							if (pdfReader != null)
								pdfReader.close();
						}
					}
				}
				
				if (Constants.STRING_S.equals(firmaDiferida)) {
					this.documentacionGedManager.marcarComoDiferida(idPolizaDefinitiva);
				}
				
				log("doPasarADefinitiva", "@@**@@-IdEstado resultado confirmacion:-" + idEstado + "-");				
				log("doPasarADefinitiva", "@@**@@-Referencia:-" + p.getReferencia() + "-");
				log("doPasarADefinitiva", "@@**@@-Tipo Referencia:-" + p.getTipoReferencia() + "-");				
				log("doPasarADefinitiva", "@@**@@-Plan:-" + p.getLinea().getCodplan() + "-");
				
				mv.addObject("cicloPoliza", cicloPoliza);
				mv.addObject("vieneDeUtilidades", vieneDeUtilidades);
				mv.addObject("estadoPoliza", idEstado);
				mv.addObject("docFirmada", docFirmada);
				mv.addObject("referencia", p.getReferencia());
				mv.addObject("plan", p.getLinea().getCodplan());
				mv.addObject("tipoPoliza", p.getTipoReferencia());
			}			
		} else {
			// Pet. 22208 ** MODIF TAM (26.03.2018) ** Inicio //
			// Si ha fallado la validacion de las complementarias, activamos el
			// sw_confirmacion a false
			if (Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(polizaDefinitiva.getTipoReferencia())) {
				log("doPasarADefinitiva.swConfirmacion1", swConfirm+"");
				if (swConfirm == true && acuseRecibo != null) {
					log("doPasarADefinitiva.swConfirmacion2", swConfirm+"");
					// Pet. 22208 ** MODIF TAM (10.04.2018) - Resolucion Incidencias *I* //
					parametros.put("ForzarswConfirmacion", parametros.get("swConfirmacion"));
					parametros.put("swConfirmacion", false);
				}
			}
			log("doPasarADefinitiva", "@@**@@-Entramos por Pasar a Definitiva");
			// PARAMETROS PARA LA FIRMA EN TABLETA
			parametros.put("idInternoPe", idInternoPe);
			// Si no es true...hacemos lo que estaba haciendo hasta ahora.
			mv = generarRedireccion(parametros, request, errores, polizaDefinitiva);
		}

		log("doPasarADefinitiva", "fin");

		return mv;
	}

	/**
	 * Realiza el proceso de paso a definitiva multiple
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView doPasarADefinitivaMultiple(HttpServletRequest request, HttpServletResponse response) {

		log("doPasarADefinitivaMultiple", "inicio");
		// Carga los parametros enviados desde la jsp o que se necesiten en el manager
		log("doPasarADefinitivaMultiple", "Carga de parametros desde la jsp");
		Map<String, Object> parametros = cargaParametros(request);

		// Llamada al manager para el proceso de paso a definitiva multiple
		Map<String, Object> errores = pasarADefinitivaPlzManager.doPasarADefinitivaMultiple(parametros, request);

		// Redireccion y envio de parametros a jsp
		ModelAndView mv = generarRedireccionMultiple(request, parametros, errores);

		log("doPasarADefinitivaMultiple", "fin");

		return mv;
	}

	/**
	 * @param request
	 * @param parametros
	 * @param errores
	 * @return
	 */
	private ModelAndView generarRedireccionMultiple(HttpServletRequest request, Map<String, Object> parametros,
			Map<String, Object> errores) {
		ModelAndView mv = new ModelAndView();

		if (errores.containsKey("resultadoGrabMult") && "true".equals(errores.get("resultadoGrabMult"))) {

			errores.put("actualizarSbp", parametros.get("actualizarSbp"));
			return new ModelAndView("/moduloUtilidades/resultadoMultiGrabDefPoliza", "polizaBean", null)
					.addAllObjects(errores);
		} else {
			mv = redirigirListadoUtilidades(parametros, request, errores);
		}

		return mv;
	}

	/**
	 * Gestiona la redireccion despues de pasar a definitiva la poliza
	 * 
	 * @param parametros
	 * @param polizaBusqueda
	 * @param request
	 * @param errores
	 * @return
	 */
	private ModelAndView generarRedireccion(Map<String, Object> parametros, HttpServletRequest request,
			Map<String, Object> errores, Poliza polizaDefinitiva) {

		log("generarRedireccion", "inicio");
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		log("generarRedireccion", "errores.size(): " + errores.size());
		log("generarRedireccion", "errores.containsKey(\"autoCompFirma\"): " + errores.containsKey("autoCompFirma"));
		
		for (String value : errores.keySet()) {
			log("generarRedireccion", "VALOR DE ERROR: " + value);
		}
		
		log("generarRedireccion", "TIPO DE REFERENCIA: " + polizaDefinitiva.getTipoReferencia());
		// Comprobacion del objeto de errores para redirecciones especiales
		if (errores.size() > 0) {
			// Si ha habido errores en la validacion del acuse de recibo de la poliza
			// principal
			if (errores.containsKey("erroresAcuseRecibo") || (errores.containsKey("autoCompFirma") && !Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(polizaDefinitiva.getTipoReferencia()))) {
				log("generarRedireccion", "Redireccion a la pagina de errores de validacion de poliza principal");
				parametros.put("autoCompFirma", errores.containsKey("autoCompFirma"));
				return redirigirErroresValidacion(parametros, errores, polizaDefinitiva, polizaDefinitiva.getIdpoliza(),
						StringUtils.nullToString(parametros.get("cicloPoliza")), usuario);			}
			// Si ha habido errores en la validacion del acuse de recibo de la poliza
			// complementaria
			else if (errores.containsKey("erroresAcuseReciboCpl") || (errores.containsKey("autoCompFirma") && Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(polizaDefinitiva.getTipoReferencia()))) {
				log("generarRedireccion", "Redireccion a la pagina de errores de validacion de poliza complementaria");
				parametros.put("autoCompFirma", errores.containsKey("autoCompFirma"));
				return redirigirErroresValidacionCpl(parametros, errores, polizaDefinitiva,
						StringUtils.nullToString(parametros.get("cicloPoliza")), usuario);
			}
			// DAA 13/05/2013
			// Si no hay errores de validacion pero existe alerta de poliza incompatible y
			// el perfil es 0
			else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)
					&& StringUtils.nullToString(errores.get("alerta"))
							.equals(bundle.getString("mensaje.alta.definitiva.IncompatibilidadClases"))) {
				log("generarRedireccion", "Redireccion a la pagina de alerta poliza incompatible perfil 0");
				return redirigirAlertaPolizaIncompatible(parametros, errores, polizaDefinitiva.getIdpoliza(),
						StringUtils.nullToString(parametros.get("cicloPoliza")));
			}
		}
		
		String cicloPoliza = StringUtils.nullToString(parametros.get("cicloPoliza"));
		String vieneDeUtilidades = StringUtils.nullToString(parametros.get("vieneDeUtilidades"));
		log("generarRedireccion", "cicloPoliza: " + cicloPoliza);
		log("generarRedireccion", "vieneDeUtilidades: " + vieneDeUtilidades);
		if (cicloPoliza.equals("cicloPoliza") || (cicloPoliza.equals("") && vieneDeUtilidades.equals(""))) {
			// Si se ha pasado a definitiva desde el ciclo de poliza
			log("generarRedireccion", "Redireccion por ciclo de poliza");
			return redirigirCicloPoliza(request, errores, polizaDefinitiva.getIdpoliza(), parametros);
		} else {
			// Si se ha pasado a definitiva desde la pantalla de utilidades
			log("generarRedireccion", "Redireccion por utilidades");
			return redirigirListadoUtilidades(parametros, request, errores);
		}
		
	}

	/**
	 * @param parametros
	 * @param request
	 * @param errores
	 * @return
	 */
	private ModelAndView redirigirListadoUtilidades(Map<String, Object> parametros, HttpServletRequest request,
			Map<String, Object> errores) {
		// Se carga el perfil del usuario conectado
		Usuario usuario = (Usuario) parametros.get("usuario");
		String perfil = "";
		perfil = usuario.getPerfil().substring(4);
		String tipoPago = StringUtils.nullToString(request.getParameter("fpago"));
		// Se carga la poliza de busqueda de sesion
		log("generarRedireccion", "Cargar poliza de sesion");
		Poliza polizaBusqueda;
		if (request.getSession().getAttribute("polizaBusqueda") != null) {
			polizaBusqueda = (Poliza) request.getSession().getAttribute("polizaBusqueda");
		} else {
			log("generarRedireccion", "No se ha encontrado poliza en sesion. Se listan todas las polizas.");
			polizaBusqueda = new Poliza();
			cargarEntidad(usuario, perfil, polizaBusqueda);
		}

		// ---------------------------------------------
		// PARA TODAS LAS OPERACIONES
		// ---------------------------------------------
		if (polizaBusqueda != null) {
			polizaBusqueda.getLinea()
					.setLineaseguroid(seleccionPolizaManager.getLineaseguroId(
							polizaBusqueda.getColectivo().getLinea().getCodplan(),
							polizaBusqueda.getColectivo().getLinea().getCodlinea()));
		}

		Long numPageRequest = new Long("0");

		if (request.getParameter("page") == null)
			numPageRequest = Long.parseLong("1");
		else
			numPageRequest = Long.parseLong(request.getParameter("page"));

		// ------------------Parametros de ordenacion

		String sort = StringUtils.nullToString(request.getParameter("sort"));
		String dir = StringUtils.nullToString(request.getParameter("dir"));

		PaginatedListImpl<Poliza> listaPolizas;

		final List<EstadoPoliza> estadosPoliza = seleccionPolizaManager.getEstadosPoliza(new BigDecimal[] {});

		Poliza polizaBusquedaSession = polizaBusqueda;

		if (polizaBusquedaSession != null) {
			listaPolizas = seleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt(polizaBusquedaSession,
					new BigDecimal[] { Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION, Constants.ESTADO_POLIZA_BAJA },
					usuario, numPageRequest.intValue(), sort, dir, tipoPago);

			polizaBusqueda = polizaBusquedaSession;
		} else {
			listaPolizas = seleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt(polizaBusqueda,
					new BigDecimal[] { Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION, Constants.ESTADO_POLIZA_BAJA },
					usuario, numPageRequest.intValue(), sort, dir, tipoPago);
		}

		// si la accion esta rellena vaciamos los checks seleccionados
		// if (!accion.equals("")){
		errores.put("idsRowsChecked", "");
		// }

		// Fase 4.habilitamos o no el boton en funcion de su lineaSeguroid
		PolizaSbp polizaSbp = new PolizaSbp();
		errores.put("polizaSbp", polizaSbp);
		// Fin Fase 4

		errores.put("totalListSize", listaPolizas.getFullListSize());
		logger.debug("Establecemos totalListSize");
		errores.put("listaPolizas", listaPolizas);
		logger.debug("Establecemos totalListSize");
		errores.put("estados", estadosPoliza);
		logger.debug("Establecemos estados de la Poliza");
		errores.put("perfil", perfil);
		logger.debug("Establecemos perfil");
		errores.put("grupoEntidades",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
		logger.debug("Establecemos grupo de Entidades");
		// revisar
		errores.put("opcionSTR", polizaBusqueda != null ? polizaBusqueda.getTienesiniestros() : "");
		errores.put("opcionRC", polizaBusqueda != null ? polizaBusqueda.getTieneanexorc() : "");
		errores.put("opcionMOD", polizaBusqueda != null ? polizaBusqueda.getTieneanexomp() : "");
		errores.put("opcionPago", tipoPago);
		errores.put("externo", usuario.getExterno());

		// mensajes Suplemento
		if (parametros.get("mensaje") != null) {
			errores.put("mensaje3", parametros.get("mensaje"));
		}
		if (parametros.get("alerta") != null) {
			errores.put("alerta2", parametros.get("alerta"));
		}

		// Para el binding del formulario de paso a definitiva
		Poliza polizaDefinitiva = new Poliza();
		polizaDefinitiva.setIdpoliza(new Long(0));
		errores.put("polizaDefinitiva", polizaDefinitiva);

		if (polizaBusqueda != null) {
			polizaBusqueda.setFechaenvio(polizaBusquedaSession.getFechaenvio());
		}
		ModelAndView mv = new ModelAndView("moduloUtilidades/cambiopolizasdefinitivas", "polizaBean", polizaBusqueda);
		mv.addAllObjects(errores);
		return mv;
	}

	/**
	 * Realiza la redireccion cuando se pasa a definitiva desde el ciclo de poliza
	 * 
	 * @param request
	 * @param errores
	 * @param idPolizaDefinitiva
	 * @return
	 */
	private ModelAndView redirigirCicloPoliza(HttpServletRequest request, Map<String, Object> errores,
			Long idPolizaDefinitiva, Map<String, Object> parametros) {
		// Si ha habido algun error
		if (hayErrores(errores)) {

			// Se comprueba si en los errores esta la tabla de ambitos de contratacion
			if (errores.containsKey("tableInfoNoDefinitiva")) {
				request.getSession().setAttribute("tableInfoNoDefinitiva", errores.get("tableInfoNoDefinitiva"));
				errores.remove("tableInfoNoDefinitiva");
				errores.put("grProvisional", "true");
				errores.put("operacion", "importes");
				errores.put("popUpAmbiCont", "true");
				errores.put("pintarTablaError", "S");
				errores.put("idpoliza", idPolizaDefinitiva);
			}

			ModelAndView resultado = new ModelAndView("redirect:/seleccionPoliza.html");
			resultado.addAllObjects(errores);
			return resultado;
		}

		// Si el paso a definitiva ha sido correcto
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("mostrarBtnDef", "NO");
		parameters.put("titulo", bundle.getString("mensaje.alta.definitiva.OK"));
		parameters.put("mensajeCentral", bundle.getString("mensaje.alta.definitiva.OK"));
		parameters.put("idPolizaDefinitiva", idPolizaDefinitiva);

		boolean avisoMan = DateUtil.horaActualMayor(PasarADefinitivaPlzManager.HORA, PasarADefinitivaPlzManager.MINUTO);
		String mensajeGrabacion = bundle.getString("mensaje.poliza.change.state.definitiva") + ".<br/>"
				+ bundle.getString("mensaje.poliza.change.state.definitiva.envio." + (avisoMan ? "man" : "hoy"));
		parameters.put("mensajeGrabacion", mensajeGrabacion);

		// mensajes de suplemento
		if (parametros.get("mensaje") != null) {
			parameters.put("mensajeSuplemento", parametros.get("mensaje"));
		}
		if (parametros.get("alerta") != null) {
			parameters.put("alerta2", parametros.get("alerta"));
		}

		// Objeto poliza para el formulario
		Poliza polizaDefinitiva = new Poliza();
		polizaDefinitiva.setIdpoliza(idPolizaDefinitiva);
		// ---

		ModelAndView resultado = new ModelAndView("moduloPolizas/polizas/grabacion/grabaciondef", "grabDef", parameters)
				.addObject("polizaDefinitiva", polizaDefinitiva);
		return resultado;
	}

	/**
	 * Comprueba si en el mapa pasado como parametro se han registrado errores
	 * 
	 * @param errores
	 * @return
	 */
	private boolean hayErrores(Map<String, Object> errores) {

		// Si el mapa es nulo no hay errores
		if (errores == null)
			return false;

		if (errores.size() > 0) {
			// Si el mapa contiene mensajes no hay errores
			return !(errores.containsKey("mensaje") || errores.containsKey("mensaje1"));
		} else {
			// Si el mapa esta vacio no hay errores
			return false;
		}

	}

	/**
	 * Realiza la redireccion cuando hay errores de validacion para una poliza
	 * principal
	 * 
	 * @param errores
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView redirigirErroresValidacion(Map<String, Object> parametros, Map<String, Object> errores,
			Poliza poliza, Long idPoliza, String cicloPoliza, Usuario usuario) {
		/* DAA 26/04/12 */
		
		logger.debug("redirigirErroresValidacion - INICIO");
		Map<String, Object> condiciones = new HashMap<String, Object>();
		try {
			BigDecimal codPlan = poliza.getLinea().getCodplan();
			BigDecimal codLinea = poliza.getLinea().getCodlinea();
			BigDecimal codEntidad = poliza.getAsegurado().getEntidad().getCodentidad();
			BigDecimal tipoUsuario = usuario.getTipousuario();
			tipoUsuario = usuario.getExterno().equals(Constants.USUARIO_EXTERNO)
					? tipoUsuario.add(Constants.NUMERO_DIEZ)
					: tipoUsuario;
			List<ErrorWsAccion> erroresWssAccionList = this.webServicesManager.getErroresWsAccion(codPlan, codLinea,
					codEntidad, tipoUsuario, Constants.VALIDACION);
			condiciones = WSUtils.getCondicionesErroresValidacion(
					(Map<ComparativaPolizaId, ResultadoWS>) errores.get("resultado"), erroresWssAccionList);
		} catch (Exception e) {
			logger.error("Ocurrio algun error al obtener las condiciones de errores de validacion", e);
		}

		ModelAndView mv = new ModelAndView("moduloPolizas/webservices/erroresValidacion", "resultado",
				(Map<ComparativaPolizaId, ResultadoWS>) errores.get("resultado"));
		mv.addObject("cabeceras", (Map<String, String>) errores.get("cabeceras"));
		mv.addObject("origenllamada", "");
		// Pet. 22208 ** MODIF TAM (04.04.2018) ** Resolucion Incidencias //
		mv.addObject("ForzarswConfirmacion", parametros.get("swConfirmacion"));
		// Pet. 22208 ** MODIF TAM (04.04.2018) ** Resolucion Incidencias //
		mv.addObject("sErrorHeader", condiciones.get("sErrorHeader"));
		mv.addObject("bBotonCalculo", false);
		mv.addObject("bBotonCorregir", condiciones.get("bBotonCorregir"));
		mv.addObject("arrBotonCalculo", condiciones.get("arrBotonCalculo"));
		mv.addObject("arrBotonCorregir", condiciones.get("arrBotonCorregir"));
		mv.addObject("keys", condiciones.get("keys"));
		mv.addObject("calcular", false);
		mv.addObject("idpoliza", idPoliza);
		// TMR 13-08-2012
		mv.addObject("perfil", usuario.getPerfil());
		mv.addObject("codUsuario", usuario.getCodusuario());
		mv.addObject("autoCompFirma", parametros.get("autoCompFirma"));
		logger.debug("autoCompFirma - " + parametros.get("autoCompFirma"));
		if (!"".equals(StringUtils.nullToString(cicloPoliza))) {
			mv.addObject("cicloPoliza", "cicloPoliza");
		}

		if (!"".equals(StringUtils.nullToString(parametros.get("vieneDeUtilidades")))) {
			mv.addObject("vieneDeUtilidades", true);
		}

		// TMR anhadimos variable que muestra o no el boton forzar paso a definitiva
		mv.addObject("mostrarForzaDef", StringUtils.nullToString(parametros.get("mostrarForzaDef")));
		// Indica con un parametro que viene desde el paso a definitiva
		mv.addObject("fromPasoADefinitiva", true);
		Poliza p = polizaManager.getPoliza(idPoliza);
		boolean isLineaGanado = p.getLinea().isLineaGanado();
		mv.addObject("esLineaGanado", isLineaGanado);
		if (isLineaGanado) {
			mv.addObject("columnaNumero", "Explotaci&oacuten");
		} else {
			mv.addObject("columnaNumero", "N&uacutemero de Parcela");
		}
		mv.addObject("idInternoPe", StringUtils.nullToString(parametros.get("idInternoPe")));
		logger.debug("redirigirErroresValidacion - FIN");
		return mv;
	}

	/**
	 * Realiza la redireccion cuando hay errores de validacion para una poliza
	 * complementaria
	 * 
	 * @param parametros
	 * @param errores
	 * @param polizaDefinitiva
	 * @return
	 */
	private ModelAndView redirigirErroresValidacionCpl(Map<String, Object> parametros, Map<String, Object> errores,
			Poliza polizaDefinitiva, String cicloPoliza, Usuario usuario) {

		logger.debug("redirigirErroresValidacionCpl [INIT]");
		String mostrarForzaDef = StringUtils.nullToString(parametros.get("mostrarForzaDef"));

		AcuseRecibo acuse = (AcuseRecibo) errores.get("resultado");
		if (acuse != null && acuse.getDocumentoArray(0) != null && acuse.getDocumentoArray(0).getErrorArray() != null
				&& acuse.getDocumentoArray(0).getErrorArray().length > 0) {
			parametros.put("errores", Arrays.asList(acuse.getDocumentoArray(0).getErrorArray()));
			parametros.put("errLength", acuse.getDocumentoArray(0).getErrorArray().length);
		} else {
			parametros.put("errores", new Error[] {});
			parametros.put("errLength", 0);
		}
		parametros.put("botonCalcular", false);
		parametros.put("botonCorregir", true);
		parametros.put("origenllamada", "");
		parametros.put("idpolizaCpl", polizaDefinitiva.getIdpoliza());
		parametros.put("WS", Constants.WS_VALIDACION);
		parametros.put("estado", Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
		if (!"".equals(StringUtils.nullToString(cicloPoliza))) {
			parametros.put("cicloPoliza", "cicloPoliza");
		}

		// Anhadimos variable que muestra o no el boton forzar paso a definitiva
		parametros.put("mostrarForzaDef", mostrarForzaDef);

		// Indica con un parametro que viene desde el paso a definitiva
		parametros.put("fromPasoADefinitiva", true);

		boolean swConfirm = (Boolean) parametros.get("swConfirmacion");
		if (swConfirm == true) {
			// Anhadimos variable que muestra o no el boton forzar paso a definitiva
			parametros.put("mostrarForzaDef", false);
		} else {
			parametros.put("mensaje", "Los datos enviados contienen errores");
		}
		
		parametros.put("perfil", usuario.getPerfil().substring(4));
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("idPoliza", polizaDefinitiva.getIdpoliza());		
		parametros.put("referencia", polizaDefinitiva.getReferencia());		
		parametros.put("tipoPoliza", polizaDefinitiva.getTipoReferencia());
		parametros.put("plan", polizaDefinitiva.getLinea().getCodplan());
		parametros.put("estadoPoliza", polizaDefinitiva.getEstadoPoliza().getIdestado());	
		
		if (!parametros.containsKey("ForzarswConfirmacion")) {
			parametros.put("ForzarswConfirmacion", parametros.get("swConfirmacion"));
		}
		
		ModelAndView mv = new ModelAndView("moduloPolizas/webservices/erroresValidacionCpl", "resultado", acuse).addAllObjects(parametros);
		logger.debug("redirigirErroresValidacionCpl [END]");
		return mv;
	}

	/**
	 * DAA 16/05/2013 Realiza la redireccion cuando la poliza es incompatible y el
	 * perfil es 0
	 * 
	 * @param parametros,
	 *            errores, idPoliza, cicloPoliza
	 * @return mv
	 */
	private ModelAndView redirigirAlertaPolizaIncompatible(Map<String, Object> parametros, Map<String, Object> errores,
			Long idPoliza, String cicloPoliza) {

		ModelAndView mv = new ModelAndView("moduloPolizas/webservices/alertaIncompatible");
		mv.addObject("alerta", errores.get("alerta"));
		mv.addObject("idpoliza", idPoliza);
		if (!"".equals(StringUtils.nullToString(cicloPoliza))) {
			mv.addObject("cicloPoliza", "cicloPoliza");
		}
		return mv;
	}

	private ModelAndView redirigirAlertaPolizaEnviada(Map<String, Object> parametros, Map<String, Object> errores,
			Long idPoliza, String cicloPoliza) {

		ModelAndView mv = new ModelAndView("moduloPolizas/webservices/alertaPolizaEnviada");
		mv.addObject("alerta", errores.get("alerta"));
		mv.addObject("idpoliza", idPoliza);
		if (!"".equals(StringUtils.nullToString(cicloPoliza))) {
			mv.addObject("cicloPoliza", "cicloPoliza");
		}
		return mv;
	}

	/**
	 * @param usuario
	 * @param perfil
	 * @param polizaBusqueda
	 */
	private void cargarEntidad(Usuario usuario, String perfil, Poliza polizaBusqueda) {
		if (null != perfil && !"".equalsIgnoreCase(perfil)) {
			switch (new Integer(perfil).intValue()) {
			case 1:
				polizaBusqueda.getColectivo().getTomador().getId()
						.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
				break;
			case 3:
				polizaBusqueda.getColectivo().getTomador().getId()
						.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
				polizaBusqueda.setOficina(usuario.getOficina().getNomoficina());
				break;
			case 4:
				Usuario us = new Usuario();
				us.setCodusuario(usuario.getCodusuario());
				polizaBusqueda.setUsuario(usuario);
				break;
			case 0:
			case 5:
				polizaBusqueda.getColectivo().getTomador().getId()
						.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
				polizaBusqueda.getColectivo().getTomador().getEntidad()
						.setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Carga los parametros enviados desde la jsp o que se necesiten en el manager
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, Object> cargaParametros(HttpServletRequest request) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		// Usuario
		parametros.put("usuario", (Usuario) request.getSession().getAttribute("usuario"));
		// En esta variable se indica si se ha mostrado el pop-up de grabacion fuera de
		// fechas de contratacion y se ha continuado con el paso a definitiva
		parametros.put("grFueraContratacion", StringUtils.nullToString(request.getParameter("grabFueraContratacion")));
		// La póliza está firmada
		parametros.put("docFirmada", StringUtils.nullToString(request.getParameter("docFirmada")));
		// Indica si el resultado de la validacion ha sido correcto
		parametros.put("resultadoValidacion", Boolean.parseBoolean(request.getParameter("resultadoValidacion")));
		// Indica si hay que actualizar las polizas de sobreprecio
		parametros.put("actualizarSbp", StringUtils.nullToString(request.getParameter("actualizarSbp")));
		// Indica la ruta a 'WEB-INF'
		parametros.put("realPath", this.getServletContext().getRealPath("/WEB-INF/"));
		// Indica si se pasa a definitiva desde el ciclo de poliza
		parametros.put("cicloPoliza", StringUtils.nullToString(request.getParameter("cicloPoliza")));
		// Indica si la poliza es complementaria
		parametros.put("esCpl", StringUtils.nullToString(request.getParameter("esCpl")));
		// Carga los ids de poliza en el caso que sea paso a definitiva multiple
		parametros.put("idsPoliza", StringUtils.nullToString(request.getParameter("idsRowsChecked")));
		// Path real para luego buscar el WSDL de los servicios Web
		parametros.put("realPath", this.getServletContext().getRealPath("/WEB-INF/"));
		if (request.getParameter("esSaeca") != null && !request.getParameter("esSaeca").equals("")) {
			parametros.put("esSaecaUnica", StringUtils.nullToString(request.getParameter("esSaeca")));
		}
		String vieneDeUtilidades = StringUtils.nullToString(request.getParameter("vieneDeUtilidades"));
		parametros.put("vieneDeUtilidades", vieneDeUtilidades);
		// Pet.22208 ** MODIF TAM (28.02.2018) ** Inicio //
		// recibimos el nuevo parametro del request //
		parametros.put("swConfirmacion", Boolean.parseBoolean(request.getParameter("swConfirmacion")));

		return parametros;
	}

	public ModelAndView doSalir(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("init - operacion salir");

		final String idpoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		Poliza poliza = polizaManager.getPoliza(new Long(idpoliza));

		// Debloqueamos la poliza al pasarla a provisional
		poliza.setBloqueadopor(null);
		poliza.setFechabloqueo(null);
		// Actualizamos el estado de la Poliza
		polizaManager.savePoliza(poliza);

		logger.debug("end - operacion salir");

		return new ModelAndView("redirect:/seleccionPoliza.html");

	}

	public ModelAndView doImprimirPoliza(HttpServletRequest request, HttpServletResponse response) {

		logger.debug("init - operacion imprimir");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView resultado = null;

		String StrImprimirReducida = StringUtils.nullToString(request.getParameter("imprimirReducida"));
		final String idpoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		Poliza poliza = polizaManager.getPoliza(new Long(idpoliza));

		parameters.put("StrImprimirReducida", StrImprimirReducida);

		if (poliza.getTipoReferencia() == 'C') {
			resultado = new ModelAndView("redirect:/informes.html").addObject("idPoliza", poliza.getIdpoliza())
					.addObject("method", "doInformePolizaComplementaria");
		} else {
			resultado = new ModelAndView("redirect:/informes.html").addObject("idPoliza", poliza.getIdpoliza())
					.addObject("method", "doInformePoliza");
		}
		logger.debug("end - operacion imprimir");

		return resultado.addAllObjects(parameters);
	}

	public void doImprimirPolizaRC(HttpServletRequest req, HttpServletResponse res) {
		try {
			Long idPolizaRC = Long.parseLong(req.getParameter("idPolizaRC"));
			Map<String, Object> datosInforme = this.informesGanadoRCService.getRellenarInformacion(idPolizaRC);
			String rutaInformes = this.getServletContext().getRealPath("plantillas");
			Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
			
			String codTerminal = "";
			
			if (req.getSession().getAttribute("codTerminal") != null)
				codTerminal = req.getSession().getAttribute("codTerminal").toString();
			
			String tipoIdentificador = datosInforme.get("tipoIdentificacion").toString();
			Boolean aseguradoVulnerable = Boolean.FALSE;
			
			if ("NIF".equals(tipoIdentificador)) {
				String key = "";
				if (codTerminal.isEmpty()) {
					key = bundle.getString("aseguradoVulnerable.secret.NoTF");
				}else {
					key = bundle.getString("aseguradoVulnerable.secret.TF");
				}
				
				AseguradoIrisHelper helper = new AseguradoIrisHelper();				
				
				aseguradoVulnerable = helper.isAseguradoVulnerable(datosInforme.get("entMed").toString(), 
				datosInforme.get("asegurado_identificador").toString(), "F", key, codTerminal, usuario.getCodusuario());
				logger.debug("Fin aseguradoVulnerable: " + aseguradoVulnerable);
			}
			
			this.informesGanadoRCHelper.generarInforme(res, datosInforme, rutaInformes,Boolean.FALSE);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * 
	 * @param method
	 * @param msg
	 */
	private void log(String method, String msg) {
		logger.debug("PasarADefinitivaPlzController." + method + " - " + msg);
	}

	/**
	 * Setter para el manager de pasar a definitiva
	 * 
	 * @param pasarADefinitivaPlzManager
	 */
	public void setPasarADefinitivaPlzManager(IPasarADefinitivaPlzManager pasarADefinitivaPlzManager) {
		this.pasarADefinitivaPlzManager = pasarADefinitivaPlzManager;
	}

	/**
	 * Se registra un editor para hacer el bind de las propiedades tipo Date que
	 * vengan de la jsp. En MultiActionController no se hace este bind
	 * automaticamente
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		// True indica que se aceptan fechas vacias
		CustomDateEditor editor = new CustomDateEditor(df, true);
		binder.registerCustomEditor(Date.class, editor);
	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setInformesGanadoRCHelper(InformesGanadoRCHelper informesGanadoRCHelper) {
		this.informesGanadoRCHelper = informesGanadoRCHelper;
	}

	public void setInformesGanadoRCService(InformesGanadoRCService informesGanadoRCService) {
		this.informesGanadoRCService = informesGanadoRCService;
	}

	// Pet. 22208 ** MODIF TAM (23.03.2018) ** Inicio //
	public void setWebServicesCplManager(WebServicesCplManager webServicesCplManager) {
		this.webServicesCplManager = webServicesCplManager;
	}

	public void setParametrizacionManager(ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
	// Pet. 22208 ** MODIF TAM (23.03.2018) ** Fin //
	
	public void setDocumentacionGedManager(DocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
	
	public void setInformesManager(InformesManager informesManager) {
		this.informesManager = informesManager;
	}
	
	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}
	
	public void setSocioSubvencionManager(SocioSubvencionManager socioSubvencionManager) {
		this.socioSubvencionManager = socioSubvencionManager;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}