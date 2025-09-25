package com.rsi.agp.core.webapp.action;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.managers.impl.FirmaTabletaManager;
import com.rsi.agp.core.managers.impl.InformesManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.SocioSubvencionManager;
import com.rsi.agp.core.report.layout.BeanTablaCoberturas;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.JRUtils;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper.AseguradoIrisBean;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper;
import com.rsi.agp.core.webapp.util.FirmaTabletaXmlHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class FirmaTabletaController extends BaseMultiActionController {

	private final Log logger = LogFactory.getLog(FirmaTabletaController.class);

	private FirmaTabletaManager firmaTabletaManager;
	private PolizaManager polizaManager;
	private IDocumentacionGedManager documentacionGedManager;
	private SocioSubvencionManager socioSubvencionManager;
	private InformesManager informesManager;
	private PagoPolizaManager pagoPolizaManager;

	private SessionFactory sessionFactory;
	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public ModelAndView doCheckAseguradoIris(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("FirmaTabletaController.doCheckAseguradoIris [INIT]");
		String codigoEntidad = request.getParameter("codigoEntidad");
		String idExternoPersona = request.getParameter("idExternoPersona");
		String tipoPersona = request.getParameter("tipoPersona");
		String codUsuario = request.getParameter("codUsuario");
		String codTerminal = request.getParameter("codTerminal");
		try {
			if (StringUtils.isNullOrEmpty(codigoEntidad) || StringUtils.isNullOrEmpty(idExternoPersona)
					|| StringUtils.isNullOrEmpty(tipoPersona) || StringUtils.isNullOrEmpty(codUsuario)
					|| StringUtils.isNullOrEmpty(codTerminal)) {
				errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
			} else {
				List<AseguradoIrisBean> listaAsegs = this.firmaTabletaManager.getAseguradoIris(codigoEntidad,
						idExternoPersona, tipoPersona, codUsuario, codTerminal);
				JSONArray listaAsegsArr = new JSONArray(listaAsegs);
				result.put("listaAsegs", listaAsegsArr);
				errorMsgs = new String[] {};
			}
		} catch (BusinessException e) {
			errorMsgs = new String[] { e.getMessage() };
		} catch (JSONException e) {
			logger.error(e);
			throw e;
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		logger.debug("FirmaTabletaController.doCheckAseguradoIris [END]");
		getWriterJSON(response, result);
		return null;
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public ModelAndView doUploadDocGed(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("FirmaTabletaController.doUploadDocGed [INIT]");
		String idPoliza = request.getParameter("idPoliza");
		String idInternoPe = request.getParameter("idInternoPe");
		String codUsuario = request.getParameter("codUsuario");
		String codTerminal = "";
		
////////////////////////////////////////////////////////////////	
logger.debug("FirmaTabletaController.idPoliza="+idPoliza);
logger.debug("FirmaTabletaController.idInternoPe="+idInternoPe);
logger.debug("FirmaTabletaController.codUsuario="+codUsuario);
//idPoliza = "27732040";
//idInternoPe = "1897757";
//codUsuario = "U999999";
//codTerminal = "ACg)Qmri";
////////////////////////////////////////////////////////////////			
		try {
			if (StringUtils.isNullOrEmpty(idInternoPe) || StringUtils.isNullOrEmpty(idPoliza)
					|| StringUtils.isNullOrEmpty(codUsuario)) {
				errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
			} else {

				Poliza poliza = polizaManager.getPoliza(new Long(idPoliza));
				result.put("nifCifAseg", poliza.getAsegurado().getNifcif());
				result.put("nomAseg", poliza.getAsegurado().getNombreCompleto());
				BigDecimal entidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
				BigDecimal subEntidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();
				boolean esGanado = (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0);
				result.put("prodTecnico", esGanado ? "1001" : "1002");				
				final boolean esComplementario = Constants.MODULO_POLIZA_COMPLEMENTARIO
						.equals(poliza.getTipoReferencia());
				List<Socio> lstSocios = this.socioSubvencionManager.getInformeSociosPoliza(poliza.getIdpoliza(),
						esGanado);
				HashMap<String, Object> lstCompNoElegidas = this.polizaManager.getMapaCompNoElegidas(poliza);
				List<BeanTablaCoberturas> infoCoberturasComp = null;
				HashMap<String, List> infoCoberturas = null;
				if (esComplementario) {
					infoCoberturasComp = this.informesManager.getDatosCoberturasGarantiasComplementaria(poliza);
				} else {
					infoCoberturas = this.informesManager.getDatosCoberturasGarantias(poliza, null,
							null, false, null, null, this.getServletContext().getRealPath("/WEB-INF/"));
				}
				
				Map<String, String> mapSub = this.informesManager.getPorcentajesDistSub(poliza);
				Object fechaFinContratacion;
				HashMap<Long, HashMap<String, List>> infoNumCobert = null;
				HashMap<Long, HashMap<String, List>> infoNumExpCobertExplotaciones = null;
				HashMap<Long, HashMap<String, List>> infoNumExpCobertParcelas = null;
				Map<String, String> coberturasElegidasParcela = new HashMap<String, String>();
				if (esGanado) {
					fechaFinContratacion = this.informesManager.getFechaContratacionGan(poliza, poliza.getCodmodulo(),"fecContratFin");
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
						logger.debug("** @@ **  Valor de idParcela: " + idParcela);
						HashMap<String, List> infoCobertParcelas = this.informesManager.getDatosCobertParcelas(poliza,
								parcela.getNumero(), parcela.getHoja(), idParcela, null, false, null);
						String num_parcStr = parcela.getHoja().toString() + parcela.getNumero().toString();
						Long numParc = new Long(num_parcStr);
						infoNumExpCobertParcelas.put(numParc, infoCobertParcelas);
						logger.debug("despues de insertar la lista");
					}
					infoNumCobert = infoNumExpCobertParcelas;
				}
				
				if (request.getSession().getAttribute("codTerminal") != null)
					codTerminal = request.getSession().getAttribute("codTerminal").toString();
				
////////////////////////////////////////////////////////////////
logger.debug("FirmaTabletaController.codTerminal="+codTerminal);
////////////////////////////////////////////////////////////////

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
					aseguradoVulnerable = helper.isAseguradoVulnerable(poliza.getAsegurado().getEntidad().getCodentidad().toString(), poliza.getAsegurado().getNifcif(), "F", key, codTerminal, codUsuario);
					logger.debug("Fin de busqueda de asegurado vulnerable: " + aseguradoVulnerable);
				}
				
				String tipoPago = this.informesManager.getFormaPago(poliza);
				logger.debug("tipoPago: " + tipoPago);
				boolean isPagoFraccionado = this.pagoPolizaManager.compruebaPagoFraccionado(poliza);
				logger.debug("isPagoFraccionado: " + isPagoFraccionado);
				Boolean polizaTieneSubvCaractAseguradoPersonaJuridica = this.informesManager.polizaTieneSubvCaractAseguradoPersonaJuridica(poliza);
				logger.debug("polizaTieneSubvCaractAseguradoPersonaJuridica: " + polizaTieneSubvCaractAseguradoPersonaJuridica);
				Map<String, String> mapNotas = null;
				
				if (aseguradoVulnerable) {
					 mapNotas = informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(),
							esGanado ? ConstantsInf.NOTA_INF_GAN_AV : ConstantsInf.NOTA_INF_AGRO_AV);
				}
				else {
					mapNotas = informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(),
							esGanado ? ConstantsInf.NOTA_INF_GAN : ConstantsInf.NOTA_INF_AGRO);
				}
				logger.debug("mapNotas: " + mapNotas);
				
				JasperPrint jp = null;
				
				if (esComplementario) {
					jp = JRUtils.getInformePolizaComplementaria(this.getServletContext().getRealPath("plantillas"),
							poliza, entidad, subEntidad, infoCoberturasComp, mapSub, (Date) fechaFinContratacion,
							tipoPago, isPagoFraccionado, true, mapNotas,
							sessionFactory.getCurrentSession().connection(), aseguradoVulnerable);
				} else {
					jp = JRUtils.getInformePoliza(this.getServletContext().getRealPath("plantillas"), poliza, entidad,
							subEntidad, esGanado, lstSocios, lstCompNoElegidas, "false", infoCoberturas, infoNumCobert,
							coberturasElegidasParcela, mapSub, fechaFinContratacion, tipoPago, isPagoFraccionado,
							polizaTieneSubvCaractAseguradoPersonaJuridica, true, mapNotas,
							sessionFactory.getCurrentSession().connection(), this.documentacionGedManager,
							aseguradoVulnerable);
				}

				if (jp != null) {
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
							: (esComplementario ? JRUtils.getNumPagsParcelasCompl(poliza.getParcelas()) : JRUtils.getNumPagsParcelas(poliza.getParcelas()));
					String xmlFirma = FirmaTabletaXmlHelper.getXmlFirma(esGanado
							? JRUtils.getFirmasDocPolizaGan(poliza.getAsegurado(), mapNotas != null, entidad.toString(),
									idInternoPe, numPagObjAseg)
							: (esComplementario
									? JRUtils.getFirmasDocPolizaComp(poliza.getAsegurado(), mapNotas != null,
											entidad.toString(), idInternoPe, numPagObjAseg, aseguradoVulnerable)
									: JRUtils.getFirmasDocPoliza(poliza.getAsegurado(), mapNotas != null,
											entidad.toString(), idInternoPe, numPagObjAseg, aseguradoVulnerable)),
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
						String idDocumentum = this.documentacionGedManager.uploadDocumentoPoliza(codUsuario,
								bos2.toByteArray(), poliza,
								StringUtils.isNullOrEmpty(idInternoPe) ? Constants.STRING_N : Constants.STRING_S,
								StringUtils.isNullOrEmpty(idInternoPe) ? Constants.CHARACTER_N : Constants.CHARACTER_S,
								StringUtils.isNullOrEmpty(idInternoPe) ? Constants.CANAL_FIRMA_PDTE
										: Constants.CANAL_FIRMA_TABLETA);
						result.put("idDocumentum", idDocumentum);
					} catch (Exception e) {
						logger.error("Error al generar el report con metadatos y/o subir el documento a GED.", e);
						throw new BusinessException(e);
					} finally {
						if (pdfReader != null)
							pdfReader.close();
					}
				}
				errorMsgs = new String[] {};
			}
		} catch (BusinessException e) {
			errorMsgs = new String[] { e.getMessage() };
////////////////////////////////////////////////////////////////
logger.debug("FirmaTabletaController.BusinessException="+e.getMessage());
////////////////////////////////////////////////////////////////
		} catch (JSONException e) {
			logger.error(e);
			throw e;
		}
		
		result.put("idPoliza", idPoliza);
		result.put("idInternoPe", idInternoPe);
		result.put("errorMsgs", new JSONArray(errorMsgs));
////////////////////////////////////////////////////////////////		
if(errorMsgs.length >0) {
	logger.debug("FirmaTabletaController.errorMsgs.length="+errorMsgs.length);
	for (String error : errorMsgs) {
		logger.debug("FirmaTabletaController.error="+error);
	}
}
////////////////////////////////////////////////////////////////		
		logger.debug("FirmaTabletaController.doUploadDocGed [END]");
		getWriterJSON(response, result);
		return null;
	}

	public void setFirmaTabletaManager(FirmaTabletaManager firmaTabletaManager) {
		this.firmaTabletaManager = firmaTabletaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
	
	public void setSocioSubvencionManager(SocioSubvencionManager socioSubvencionManager) {
		this.socioSubvencionManager = socioSubvencionManager;
	}

	public void setInformesManager(InformesManager informesManager) {
		this.informesManager = informesManager;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}