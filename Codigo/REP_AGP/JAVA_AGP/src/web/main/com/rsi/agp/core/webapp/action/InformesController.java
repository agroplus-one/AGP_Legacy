					   

package com.rsi.agp.core.webapp.action;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;

import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.ISbpTxtManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.InformesManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.RecibosPolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.SocioSubvencionManager;
import com.rsi.agp.core.report.BeanParcela;
import com.rsi.agp.core.report.anexoMod.BeanParcelaAnexo;
import com.rsi.agp.core.report.layout.BeanTablaCobertExplotaciones;
import com.rsi.agp.core.report.layout.BeanTablaCoberturas;
import com.rsi.agp.core.report.layout.DisenoTablaCoberturas;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.JRUtils;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper;
import com.rsi.agp.core.webapp.util.FirmaTabletaXmlHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.IReduccionCapitalDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeComisionesUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeDeudaAplazadaUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.DistribucionCoste;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
/* Pe.t 57622 ** MODIF TAM (28.06.2019) ** Inicio */
import com.rsi.agp.dao.tables.poliza.explotaciones.Informes.InformeAnexModExplotacion;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.siniestro.SiniestrosAnteriores;															 

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
													   
																	
																  

								
public class InformesController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(InformesController.class);

	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	

	private static final String USUARIO = "usuario";
	private static final String PLANTILLAS = "plantillas";
	private static final String JRXML = ".jrxml";
	private static final String JASPER = ".jasper";
	private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String FILENAME = "filename=";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String ID_POLIZA = "idPoliza";
	private static final String ID_RECIBO_POLIZA = "idReciboPoliza";
	private static final String LINEA_SEGURO_ID = "LINEASEGUROID";
	private static final String FORMATO = "formato";
	private static final String APP_EXCEL = "application/vnd.ms-excel";
	private static final String RGA_AGROPLUS = "RGA Agroplus";
	private static final String WEB_INF = "/WEB-INF/";
	
	private static final String LOGGER_INFORME_PDF = "Exportamos el informe a PDF";
	private static final String LOGGER_INFORME_EXPORTADO = "Informe exportado correctamente";
	private static final String LOGGER_PL_FACTURACION = "llamamos al PL de facturacion";
	private static final String LOGGER_ANEXO_EXPLOTACIONES = "Error durante la generacion del informe del anexo de modificacion de explotaciones";
	
	private static final String EXCEPTION_GENERACION_POLIZA = "Error durante la generacion de la poliza: ";
	
	private String pathInformes;
	private InformesManager informesManager;
	private PolizaManager polizaManager;
	private RecibosPolizaManager recibosPolizaManager;
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private ISimulacionSbpManager simulacionSbpManager;
	private ISbpTxtManager sbpTxtManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private PagoPolizaManager pagoPolizaManager;
	private SessionFactory sessionFactory;
	private SocioSubvencionManager socioSubvencionManager;
	private IDocumentacionGedManager documentacionGedManager;
	private IReduccionCapitalDao reduccionCapitalDao;

	/* Pet. 63485-Fase II ** MODIF TAM (25.09.2020) ** Inicio */
	/* Se incluyen cambios en la impresión de coberturas de parcelas */

	public void doInformePolizaSbp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String nombreInforme = "PolizaSobreprecio.pdf";
		String pathInformeFuente = null;
		String pathSubInformeFuente = null;
		String pathSubInformeCompilado = null;
		PolizaSbp polizaSbp = null;
		String identificadorInforme = bundle.getString("informeJasper.sobreprecio");
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		List<ParcelaSbp> listaParcelasSbp;

		String codTerminal = "";
		
		if (request.getSession().getAttribute("codTerminal") != null)
			codTerminal = request.getSession().getAttribute("codTerminal").toString();
		
												  

		ServletOutputStream out = null;
		try {
			String idPolizaSbp = StringUtils.nullToString(request.getParameter("idPolizaSbp"));
			if (!idPolizaSbp.equals("")) {
				polizaSbp = simulacionSbpManager.getPolizaSbp(Long.valueOf(idPolizaSbp));
			} else {
				polizaSbp = (PolizaSbp) request.getSession().getAttribute("polizaSbp");
			}
						
   
			byte[] docGED = null;

			if (ArrayUtils.contains(
					new BigDecimal[] { ConstantsSbp.ESTADO_PENDIENTE_ACEPTACION,
							ConstantsSbp.ESTADO_ENVIADA_ERRONEA, ConstantsSbp.ESTADO_ENVIADA_CORRECTA },
					polizaSbp.getEstadoPlzSbp().getIdestado())) {

				docGED = documentacionGedManager.getDocumentoPolizaSbp(polizaSbp.getId(),
						polizaSbp.getPolizaPpal().getColectivo().getTomador().getId().getCodentidad().toString(),
						polizaSbp.getPolizaPpal().getOficina(), usuario.getCodusuario());
			}												 
   
			if (docGED == null) {
				
				List<PolizaSbp> listaPolizasSbp = new ArrayList<PolizaSbp>();
				listaPolizasSbp.add(polizaSbp);
				JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaPolizasSbp, false);
	
				List<String> lstIdentificadoresInformes = new ArrayList<String>();
				// Subinformes
				lstIdentificadoresInformes.add(bundle.getString("informeJasper.sobreprecio_parcelas"));
				lstIdentificadoresInformes.add("/jasper/sobreprecio/polizaSobreprecio_notaInformativa");
				lstIdentificadoresInformes.add("/jasper/sobreprecio/polizaSobreprecio_notaInformativa_AV");
				lstIdentificadoresInformes.add("/jasper/sobreprecio/polizaSobreprecio_ipid");
				lstIdentificadoresInformes.add("/jasper/sobreprecio/polizaSobreprecio_ipid2");
				lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2");
				lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2_AV");
	

				// Compilamos todos los reports y los rellenamos
				for (String idSubInforme : lstIdentificadoresInformes) {
	
					pathSubInformeFuente = pathInformes + idSubInforme + JRXML;
					pathSubInformeCompilado = pathInformes + idSubInforme + JASPER;
	
					LOGGER.debug("Inicio de la compilacion del informe " + idSubInforme);
	
					JasperCompileManager.compileReportToFile(pathSubInformeFuente, pathSubInformeCompilado);
				}
	
				// Compilamos el informe principal
				pathInformeFuente = pathInformes + identificadorInforme + JRXML;
				JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);
	
				listaParcelasSbp = simulacionSbpManager.getParcelasSimulacion(polizaSbp);
	
				JRBeanCollectionDataSource sourceParcelas = new JRBeanCollectionDataSource(listaParcelasSbp, false);
	
				// calculamos el total suma asegurada
				BigDecimal totalSumAsegurada = new BigDecimal(0);
				for (ParcelaSbp par : polizaSbp.getParcelaSbps()) {
					totalSumAsegurada = totalSumAsegurada.add(StringUtils.nullToZero(par.getSobreprecio())
							.multiply(StringUtils.nullToZero(par.getTotalProduccion())));
				}
	
				// DAA 06/05/2013 calculamos los txt a mostrar
				parametros = sbpTxtManager.getTxtInformePolizaSbp(polizaSbp.getPolizaPpal().getLinea().getCodplan());
	
				parametros.put("totalSumAsegurada", totalSumAsegurada);
				parametros.put("sourceParcelas", sourceParcelas);
				parametros.put(SUBREPORT_DIR,
						getServletContext().getRealPath(PLANTILLAS) + bundle.getString("pathSubreport.sobreprecio"));
				parametros.put("SUBREPORT_COMUN_DIR", pathInformes + "/jasper/comun/");
	
				BigDecimal entidad = polizaSbp.getPolizaPpal().getColectivo().getSubentidadMediadora().getId()
						.getCodentidad();
				BigDecimal subEntidad = polizaSbp.getPolizaPpal().getColectivo().getSubentidadMediadora().getId()
						.getCodsubentidad();
	
				String tipoIdentificador = polizaSbp.getPolizaPpal().getAsegurado().getTipoidentificacion();
				Boolean aseguradoVulnerable = Boolean.FALSE;
				
				if ("NIF".equals(tipoIdentificador)) {
					String key = "";
					if (StringUtils.isNullOrEmpty(codTerminal)) {
						key = bundle.getString("aseguradoVulnerable.secret.NoTF");
					}else {
						key = bundle.getString("aseguradoVulnerable.secret.TF");
					}
					AseguradoIrisHelper helper = new AseguradoIrisHelper();
					
					LOGGER.debug("Inicio de busqueda de asegurado vulnerable.");
					aseguradoVulnerable = helper.isAseguradoVulnerable(polizaSbp.getPolizaPpal().getAsegurado().getEntidad().getCodentidad().toString(), polizaSbp.getPolizaPpal().getAsegurado().getNifcif(), "F", key, codTerminal, usuario.getCodusuario());
					LOGGER.debug("Fin de busqueda de asegurado vulnerable: " + aseguradoVulnerable);
					
				}
				
				parametros.put("aseguradoVulnerable", aseguradoVulnerable);
				
				// Genero la nota informativa a mostrar
				Map<String, String> mapNotas = null;
				
				if (aseguradoVulnerable) {
					mapNotas = informesManager.getNotaInformativa(entidad,
						polizaSbp.getPolizaPpal().getIdpoliza(), ConstantsInf.NOTA_INF_SBP_AV);
				}
				else {
					mapNotas = informesManager.getNotaInformativa(entidad,
							polizaSbp.getPolizaPpal().getIdpoliza(), ConstantsInf.NOTA_INF_SBP);
				}
				if (mapNotas != null) {
					parametros.putAll(mapNotas);
				}
	 
				parametros.put("TIPO_POLIZA", "P");
	
				parametros.put("entMed", String.valueOf(entidad));
				parametros.put("subEntMed", String.valueOf(subEntidad));
				parametros.put("caja", String.valueOf(entidad));
				parametros.put("oficina", polizaSbp.getPolizaPpal().getOficina());
	
				final Pattern entidadPattern = Pattern.compile("[3][0-9][0-9][0-9]"); // 3xxx-x
				if (entidadPattern.matcher(String.valueOf(entidad)).matches() && BigDecimal.ZERO.equals(subEntidad)) {
					parametros.put("SHOW_ENT_SUBENT_MED", Boolean.FALSE);
				} else {
					parametros.put("SHOW_ENT_SUBENT_MED", Boolean.TRUE);
				}
	
				parametros.put("RUTA_IPID",
						ResourceBundle.getBundle("agp_informes_jasper").getString("informeJasper.ipid.ruta"));
				parametros.put("PLAN", polizaSbp.getPolizaPpal().getLinea().getCodplan().toString());
				// TIPO ENVIO:
				// 1.- Principal
				// 2.- Suplemento
	
				/* DNF 08/04/2020 P0063109.FIII punto 3.1.13.12 */
				logger.debug("*polizaSbp : " + polizaSbp);
				logger.debug("*polizaSbp.getTipoEnvio() : " + polizaSbp.getTipoEnvio());
				logger.debug("*polizaSbp.getTipoEnvio().getId() : " + polizaSbp.getTipoEnvio().getId());
				logger.debug("**polizaSbp.getId() : " + polizaSbp.getId());
				logger.debug("**polizaSbp.getId().toString() : " + polizaSbp.getId().toString());
				logger.debug("***polizaSbp.getRefPlzOmega() : " + polizaSbp.getRefPlzOmega());
				/* FIN DNF 08/04/2020 P0063109.FIII punto 3.1.13.12 */
	
				parametros.put("numSolicitud",
						BigDecimal.ONE.equals(polizaSbp.getTipoEnvio().getId()) ? polizaSbp.getId().toString() :
						/* DNF 08/04/2020 P0063109.FIII punto 3.1.13.12 */
						/* controlo que al hacer el toString() no explote si viene a null */
								(polizaSbp.getRefPlzOmega() == null ? polizaSbp.getRefPlzOmega()
										: polizaSbp.getRefPlzOmega().toString())
				/* FIN DNF 08/04/2020 P0063109.FIII punto 3.1.13.12 */
				);
				
				parametros.put("P_BARCODE", polizaSbp.getGedDocPolizaSbp().getCodBarras());
	
	   
				JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);
				
				response.setContentType(APPLICATION_PDF);
				response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
				out = response.getOutputStream();
				
				SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

				configuration.setMetadataAuthor(RGA_AGROPLUS);
				configuration.setMetadataCreator(RGA_AGROPLUS);

				LOGGER.debug(LOGGER_INFORME_PDF);
																						

				JRPdfExporter exporter = new JRPdfExporter();
																				  
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setConfiguration(configuration);
																   
				try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
					SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(bos);
					exporter.setExporterOutput(exporterOutput);
					exporter.exportReport();
					byte[] bosArr = bos.toByteArray();
					try {
						if (ArrayUtils.contains(
								new BigDecimal[] { ConstantsSbp.ESTADO_PENDIENTE_ACEPTACION,
										ConstantsSbp.ESTADO_ENVIADA_ERRONEA, ConstantsSbp.ESTADO_ENVIADA_CORRECTA },
								polizaSbp.getEstadoPlzSbp())) {
							this.documentacionGedManager.uploadDocumentoPolizaSbp(usuario.getCodusuario(), bosArr,
									polizaSbp, Constants.STRING_N, Constants.CHARACTER_N,
									Constants.CANAL_FIRMA_PDTE);
						}
					} catch (BusinessException e) {
						LOGGER.error("[doInformePoliza] Error al realizar upload a GED.", e);
					}
					LOGGER.debug(LOGGER_INFORME_EXPORTADO);
					ByteArrayInputStream docStream = new ByteArrayInputStream(bosArr);
					IOUtils.copy(docStream, out);
				} catch (Exception e) {
					LOGGER.error("[doInformePolizaSbp] Error al generar el report con metadatos.", e);
				}																				 
																	   
	  
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
										
				// TMR FActuracion.Al imprimir informes facturamos
				LOGGER.debug(LOGGER_PL_FACTURACION);
				seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
				
				
			}
			else {
				
				LOGGER.debug("[doInformePolizaSbp] Mostramos el documento obtenido de GED");
				ByteArrayInputStream docStream = new ByteArrayInputStream(docGED);
				IOUtils.copy(docStream, out);
			}

			return;

		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de Poliza de Sobreprecio: ", e);
			throw new Exception("Error durante la generacion del informe Poliza de Sobreprecio", e);
		} finally {
			if (out != null)
				out.close();
		}
		
	}

	/**
	 * Metodo de impresion del report de una Poliza
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public void doInformePoliza(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.debug("InformesController - doInformesPoliza[INIT]");

		String idPoliza = request.getAttribute("idPolizaGED") != null ? request.getAttribute("idPolizaGED").toString()
				: request.getParameter(ID_POLIZA);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		String strImprimirReducida = StringUtils.nullToString(request.getParameter("StrImprimirReducida"));
		boolean imprimirReducida = "true".equals(strImprimirReducida);

		Poliza poliza = null;
		Boolean esGanado = false;
  
		String codTerminal = "";
		
		if (request.getSession().getAttribute("codTerminal") != null)
			codTerminal = request.getSession().getAttribute("codTerminal").toString();		  
  										 

		try (ServletOutputStream out = response.getOutputStream()) {

			if (idPoliza != null) {
				poliza = (Poliza) polizaManager.getPoliza(Long.valueOf(idPoliza));
				esGanado = (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0);
			} else {
				String idReciboPoliza = request.getParameter(ID_RECIBO_POLIZA);
				ReciboPoliza reciboPoliza = recibosPolizaManager.buscarRecibo(Long.valueOf(idReciboPoliza));
				poliza = (Poliza) polizaManager.getPolizaByReferencia(reciboPoliza.getRefpoliza(),
						reciboPoliza.getTiporef());
				esGanado = (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0);
			}

			String nombreInforme = "DeclaracionSeguro_" + poliza.getIdpoliza() + ".pdf";

			byte[] docGED = null;

			if (!imprimirReducida && ArrayUtils.contains(
					new BigDecimal[] { Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,
							Constants.ESTADO_POLIZA_ENVIADA_ERRONEA, Constants.ESTADO_POLIZA_DEFINITIVA },
					poliza.getEstadoPoliza().getIdestado())) {

				docGED = documentacionGedManager.getDocumentoPoliza(poliza.getIdpoliza(),
						poliza.getColectivo().getTomador().getId().getCodentidad().toString(), poliza.getOficina(),
						usuario.getCodusuario());
			}

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);

			if (docGED == null) {

				pathInformes = getServletContext().getRealPath(PLANTILLAS);

				/*if (ArrayUtils.contains(
						new BigDecimal[] { Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,
								Constants.ESTADO_POLIZA_ENVIADA_ERRONEA, Constants.ESTADO_POLIZA_DEFINITIVA },
						poliza.getEstadoPoliza().getIdestado())) {

					documentacionGedManager.createGedAudit(poliza.getIdpoliza(), Constants.EST_GED_GENERANDO,
							nombreInforme, null, usuario.getCodusuario());
				}*/

				// listaSocios
				List<Socio> lstSocios = socioSubvencionManager.getInformeSociosPoliza(poliza.getIdpoliza(), esGanado);

				HashMap<String, Object> lstCompNoElegidas = polizaManager.getMapaCompNoElegidas(poliza);

				HashMap<String, List> infoCoberturas = informesManager.getDatosCoberturasGarantias(poliza, null, null,
						false, null, null, this.getServletContext().getRealPath("/WEB-INF/"));

				// Comentamos por incidencia 20.11.2020
													  
				//Map<String, String> coberturasElegidasParcela = informesManager.getRiesgosCoberturaParcela(poliza);
														  
				Map<String, String> coberturasElegidasParcela = new HashMap<String, String>();

				// porcentaje Subvenciones
				Map<String, String> mapSub = null;
				if (poliza.isPlanMayorIgual2015()) {

					mapSub = informesManager.getPorcentajesDistSub(poliza);
				}

				HashMap<Long, HashMap<String, List>> infoNumExpCobertExplotaciones = null;

				HashMap<Long, HashMap<String, List>> infoNumExpCobertParcelas = null;
				// FechaFinContratacion
				Object fechaFinContratacion;
				if (esGanado) {

					infoNumExpCobertExplotaciones = new HashMap<Long, HashMap<String, List>>();

					for (Explotacion explotacion : poliza.getExplotacions()) {

						Long num_explo = new Long(explotacion.getNumero());

						/* Obtenemos los datos de la explotación X */
						HashMap<String, List> infoCobertExplotaciones = informesManager.getDatosCobertExplotaciones(
								poliza, explotacion.getNumero(), explotacion.getId(), false, null);

						infoNumExpCobertExplotaciones.put(num_explo, infoCobertExplotaciones);
					}

					fechaFinContratacion = informesManager.getFechaContratacionGan(poliza,poliza.getCodmodulo(),"fecContratFin");
				} else {

					/* Pet. 63485-Fase II ** MODIF TAM (24.09.2020) ** Inicio */
					infoNumExpCobertParcelas = new HashMap<Long, HashMap<String, List>>();
					logger.debug("** Parte nueva - obtener coberturas de Parcelas");

					for (Parcela parcela : poliza.getParcelas()) {

						Long idParcela = parcela.getIdparcela();
						logger.debug("** @@ **  Valor de idParcela: " + idParcela);

						/* Obtenemos los datos de las parcelas X */
						HashMap<String, List> infoCobertParcelas = informesManager.getDatosCobertParcelas(poliza,
								parcela.getNumero(), parcela.getHoja(), idParcela, null, false, null);

						/* En num_parc pasamos la hoja y el numero de la parcela */
						String num_parcStr = parcela.getHoja().toString() + parcela.getNumero().toString();
						Long numParc = new Long(num_parcStr);

						infoNumExpCobertParcelas.put(numParc, infoCobertParcelas);
						logger.debug("despues de insertar la lista");
					}
					/* Pet. 63485-Fase II ** MODIF TAM (24.09.2020) ** Fin */

					fechaFinContratacion = informesManager.getFechaContratacion(poliza, poliza.getCodmodulo(),"fecfincontrata");
				}

				String tipoPago = informesManager.getFormaPago(poliza);

				boolean isPagoFraccionado = pagoPolizaManager.compruebaPagoFraccionado(poliza);

				Boolean polizaTieneSubvCaractAseguradoPersonaJuridica = informesManager
						.polizaTieneSubvCaractAseguradoPersonaJuridica(poliza);

				BigDecimal entidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
				BigDecimal subEntidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();

				// Genero la nota informativa a mostrar
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
					LOGGER.debug("Inicio de busqueda de asegurado vulnerable.");
					aseguradoVulnerable = helper.isAseguradoVulnerable(poliza.getAsegurado().getEntidad().getCodentidad().toString(), poliza.getAsegurado().getNifcif(), "F", key, codTerminal, usuario.getCodusuario());
					LOGGER.debug("Fin de busqueda de asegurado vulnerable: " + aseguradoVulnerable);

				}
				
				// Genero la nota informativa a mostrar
				Map<String, String> mapNotas = null;
				
				if (aseguradoVulnerable) {
					 mapNotas = informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(),
							esGanado ? ConstantsInf.NOTA_INF_GAN_AV : ConstantsInf.NOTA_INF_AGRO_AV);
				}
				else {
					mapNotas = informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(),
							esGanado ? ConstantsInf.NOTA_INF_GAN : ConstantsInf.NOTA_INF_AGRO);
				}

				HashMap<Long, HashMap<String, List>> infoNumCobert = null;
				if (esGanado) {
					infoNumCobert = infoNumExpCobertExplotaciones;
				} else {
					infoNumCobert = infoNumExpCobertParcelas;
				}

				boolean lineaSup2021 = pagoPolizaManager.lineaContratacion2021(poliza.getLinea().getCodplan(),
						poliza.getLinea().getCodlinea(), esGanado);

				JasperPrint jp = JRUtils.getInformePoliza(pathInformes, poliza, entidad, subEntidad, esGanado,
						lstSocios, lstCompNoElegidas, strImprimirReducida, infoCoberturas, infoNumCobert,
						coberturasElegidasParcela, mapSub, fechaFinContratacion, tipoPago, isPagoFraccionado,
						polizaTieneSubvCaractAseguradoPersonaJuridica, lineaSup2021, mapNotas,
														 
						/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Inicio */
						sessionFactory.getCurrentSession().connection(), documentacionGedManager, aseguradoVulnerable);
						/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Fin */
													  

				SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

				configuration.setMetadataAuthor(RGA_AGROPLUS);
				configuration.setMetadataCreator(RGA_AGROPLUS);

				JRPdfExporter exporter = new JRPdfExporter();
				// exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setConfiguration(configuration);
				
				int numPagObjAseg = esGanado ? JRUtils.getNumPagsExplotaciones(poliza.getExplotacions())
						: JRUtils.getNumPagsParcelas(poliza.getParcelas());
				String xmlFirma = FirmaTabletaXmlHelper.getXmlFirma(esGanado
						? JRUtils.getFirmasDocPolizaGan(poliza.getAsegurado(), mapNotas != null, entidad.toString(), "",
								numPagObjAseg)
						: JRUtils.getFirmasDocPoliza(poliza.getAsegurado(), mapNotas != null, entidad.toString(), "",
								numPagObjAseg, aseguradoVulnerable),
						nombreInforme);
				
				PdfReader pdfReader = null;
				PdfStamper pdfStamper = null;
												

				try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ByteArrayOutputStream bos2 = new ByteArrayOutputStream()) {
					SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(bos);
					exporter.setExporterOutput(exporterOutput);
					exporter.exportReport();
					pdfReader = new PdfReader(bos.toByteArray());
					pdfStamper = new PdfStamper(pdfReader, bos2);
					pdfStamper.setXmpMetadata(xmlFirma.getBytes());
					pdfStamper.close();
					try {
						if (!imprimirReducida && ArrayUtils.contains(
								new BigDecimal[] { Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,
										Constants.ESTADO_POLIZA_ENVIADA_ERRONEA, Constants.ESTADO_POLIZA_DEFINITIVA },
								poliza.getEstadoPoliza().getIdestado())) {
							this.documentacionGedManager.uploadDocumentoPoliza(usuario.getCodusuario(),
									bos2.toByteArray(), poliza, Constants.STRING_N,
									Constants.CHARACTER_N, Constants.CANAL_FIRMA_PDTE);
						}
					} catch (BusinessException e) {
						LOGGER.error("[doInformePoliza] Error al realizar upload a GED.", e);
					}
					LOGGER.debug(LOGGER_INFORME_EXPORTADO);
					ByteArrayInputStream docStream = new ByteArrayInputStream(bos2.toByteArray());
					IOUtils.copy(docStream, out);
				} catch (Exception e) {
					LOGGER.error("[doInformePoliza] Error al generar el report con metadatos.", e);
				} finally {
					if (pdfReader != null)
						pdfReader.close();
				}
				
													

				// TMR FActuracion.Al imprimir informes facturamos
				LOGGER.debug(LOGGER_PL_FACTURACION);
				seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

			} else {

				LOGGER.debug("[doInformePoliza] Mostramos el documento obtenido de GED");
				ByteArrayInputStream docStream = new ByteArrayInputStream(docGED);
				IOUtils.copy(docStream, out);
								 
			}
		} catch (net.sf.jasperreports.governors.TimeoutGovernorException ex) {

			LOGGER.error("Error durante la generacion de la poliza por timeout: ", ex);
			throw new Exception(EXCEPTION_GENERACION_POLIZA, ex);

		} catch (net.sf.jasperreports.governors.MaxPagesGovernorException ex) {

			LOGGER.error("Error durante la generacion de la poliza por exceso de páginas(loop infinite): ", ex);
			throw new Exception(EXCEPTION_GENERACION_POLIZA, ex);
			// Propiedades metidas en el report para provocar este error cuando el
			// fillreport provoca un bucle infinito
			// en tiempo de ejecución
			// <property name="net.sf.jasperreports.governor.max.pages.enabled"
			// value="true"/>
			// <property name="net.sf.jasperreports.governor.max.pages" value="40"/>
		} catch (Exception e) {

			LOGGER.error(EXCEPTION_GENERACION_POLIZA, e);
			throw new Exception(EXCEPTION_GENERACION_POLIZA, e);
		} 

		logger.debug("InformesController - doInformesPoliza[END]");
		
		return;
	}

	/**
	 * Metodo de impresion del report de una Poliza
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void doInformePolizaComplementaria(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String idPoliza = request.getParameter(ID_POLIZA);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		ServletOutputStream out = null;
		Poliza poliza = null;			
		
		String codTerminal = "";
		
		if (request.getSession().getAttribute("codTerminal") != null)
			codTerminal = request.getSession().getAttribute("codTerminal").toString();

		try {

			if (idPoliza != null) {

				poliza = (Poliza) polizaManager.getPoliza(Long.valueOf(idPoliza));
			} else {

				String idReciboPoliza = request.getParameter(ID_RECIBO_POLIZA);
				ReciboPoliza reciboPoliza = recibosPolizaManager.buscarRecibo(Long.valueOf(idReciboPoliza));
				poliza = (Poliza) polizaManager.getPolizaByReferencia(reciboPoliza.getRefpoliza(),
						reciboPoliza.getTiporef());
			}

			String nombreInforme = "DeclaracionSeguro_" + poliza.getIdpoliza() + ".pdf";
			
			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();

			byte[] docGED = null;

			if (ArrayUtils.contains(
					new BigDecimal[] { Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,
							Constants.ESTADO_POLIZA_ENVIADA_ERRONEA, Constants.ESTADO_POLIZA_DEFINITIVA },
					poliza.getEstadoPoliza().getIdestado())) {

				docGED = documentacionGedManager.getDocumentoPoliza(poliza.getIdpoliza(),
						poliza.getColectivo().getTomador().getId().getCodentidad().toString(), poliza.getOficina(),
						usuario.getCodusuario());
			}

			if (docGED == null) {

				pathInformes = getServletContext().getRealPath(PLANTILLAS);

				List<BeanTablaCoberturas> infoCoberturas = informesManager
						.getDatosCoberturasGarantiasComplementaria(poliza);

				Map<String, String> mapSub = null;
				if (poliza.isPlanMayorIgual2015()) {

					mapSub = informesManager.getPorcentajesDistSub(poliza);
				}

				Date fechaFinContratacion = informesManager.getFechaContratacion(poliza,poliza.getCodmodulo(),"fecfincontrata");

				String tipoPago = informesManager.getFormaPago(poliza);

				boolean isPagoFraccionado = pagoPolizaManager.compruebaPagoFraccionado(poliza);

				BigDecimal entidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
				BigDecimal subEntidad = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();

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
					LOGGER.debug("Inicio de busqueda de asegurado vulnerable.");
					aseguradoVulnerable = helper.isAseguradoVulnerable(poliza.getAsegurado().getEntidad().getCodentidad().toString(), poliza.getAsegurado().getNifcif(), "F", key, codTerminal, usuario.getCodusuario());
					LOGGER.debug("Fin de busqueda de asegurado vulnerable: " + aseguradoVulnerable);
				}

				Map<String, String> mapNotas =  null;
				
				if (aseguradoVulnerable) {
					mapNotas = informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(),
							ConstantsInf.NOTA_INF_AGRO_AV);
				}
				else {
					mapNotas = informesManager.getNotaInformativa(entidad, poliza.getIdpoliza(),
							ConstantsInf.NOTA_INF_AGRO);
				}
	 

				boolean lineaSup2021 = pagoPolizaManager.lineaContratacion2021(poliza.getLinea().getCodplan(),
						poliza.getLinea().getCodlinea(), false);
				
				JasperPrint jp = JRUtils.getInformePolizaComplementaria(pathInformes, poliza, entidad, subEntidad,
						infoCoberturas, mapSub, fechaFinContratacion, tipoPago, isPagoFraccionado, lineaSup2021,
						mapNotas, sessionFactory.getCurrentSession().connection(), aseguradoVulnerable);

				SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

				configuration.setMetadataAuthor(RGA_AGROPLUS);
				configuration.setMetadataCreator(RGA_AGROPLUS);

				JRPdfExporter exporter = new JRPdfExporter();
				// exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setConfiguration(configuration);
				
				String xmlFirma = FirmaTabletaXmlHelper
						.getXmlFirma(
								JRUtils.getFirmasDocPolizaComp(poliza.getAsegurado(), mapNotas != null,
										entidad.toString(), "", JRUtils.getNumPagsParcelasCompl(poliza.getParcelas()), aseguradoVulnerable),
								nombreInforme);
				
				PdfReader pdfReader = null;
				PdfStamper pdfStamper = null;

				try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ByteArrayOutputStream bos2 = new ByteArrayOutputStream()) {
					SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(bos);
					exporter.setExporterOutput(exporterOutput);
					exporter.exportReport();
					pdfReader = new PdfReader(bos.toByteArray());
					pdfStamper = new PdfStamper(pdfReader, bos2);
					pdfStamper.setXmpMetadata(xmlFirma.getBytes());
					pdfStamper.close();
										  

						String realPath = this.getServletContext().getRealPath("/WEB-INF/");
						try {
							if (ArrayUtils.contains(
									new BigDecimal[] { Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,
											Constants.ESTADO_POLIZA_ENVIADA_ERRONEA, Constants.ESTADO_POLIZA_DEFINITIVA },
									poliza.getEstadoPoliza().getIdestado())) {
								this.documentacionGedManager.uploadDocumentoPoliza(usuario.getCodusuario(),
										bos2.toByteArray(), poliza, Constants.STRING_N,
										Constants.CHARACTER_N, Constants.CANAL_FIRMA_PDTE);
							}
						} catch (BusinessException e) {
							LOGGER.error("[doInformePoliza] Error al realizar upload a GED.", e);
						}
						LOGGER.debug(LOGGER_INFORME_EXPORTADO);
						ByteArrayInputStream docStream = new ByteArrayInputStream(bos2.toByteArray());
						IOUtils.copy(docStream, out);
					} catch (Exception e) {
						// No queremos que se propague el error
						// Dependiendo del fallo, o se subirá por batch o la próxima vez que se genere
						// el documento online
						LOGGER.error("Error durante la subida de la documentación a GED: ", e);
																	   
					}finally {
						if (pdfReader != null)
							pdfReader.close();
					}
							
				
		

				// TMR FActuracion.Al imprimir informes facturamos
				LOGGER.debug(LOGGER_PL_FACTURACION);
				seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

			} else {

				LOGGER.debug("[doInformePolizaComplementaria] Mostramos el documento obtenido de GED");
				ByteArrayInputStream docStream = new ByteArrayInputStream(docGED);
				IOUtils.copy(docStream, out);
			}
		} catch (Exception e) {

			LOGGER.error(EXCEPTION_GENERACION_POLIZA, e);
			throw new Exception(EXCEPTION_GENERACION_POLIZA, e);
		} finally {

			if (out != null) {

				out.close();
			}
		}

		return;
	}

	/**
	 * Metodo de impresion del report de las comparativas de una poliza
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public void doInformeComparativas(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String pathInformeFuente = null;
		String pathInformeCompilado = null;
		String pathReport = getServletContext().getRealPath(PLANTILLAS)
				+ bundle.getString("pathSubreport.comparativa");
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		String nombreInforme = "InformeComparativas.pdf";
		String idPoliza = request.getParameter("idpoliza");
		int numcomparativas = Integer.parseInt(request.getParameter("numcomparativas"));
		Connection conexionBBDD = null;
		DisenoTablaCoberturas tablaCoberturas = new DisenoTablaCoberturas();
		Poliza poliza = null;
		List<byte[]> informes = new ArrayList<byte[]>();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		Boolean esGanado = false;
		String descripcionMod = null;
		String tipoAsegurado = null;
		
		Boolean financiacionAgroseguro = (request.getParameter("esAgr").equals("true"));
		ImporteFraccionamiento importeFraccionamiento = null;

		try {

			if (idPoliza != null) {
				poliza = (Poliza) polizaManager.getPoliza(Long.valueOf(idPoliza));
			}

			esGanado = (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0);

			for (int cont = 1; cont <= numcomparativas; cont++) {
				HashMap<String, Object> parametros = new HashMap<String, Object>();


// en strModulo viene la Comparativa. En la primara posicion viene el codModulo
				String strModulo = request.getParameter("modElegido" + cont);
				String[] seleccionados = strModulo.split("\\|");
				String codModulo = seleccionados[1];
				BigDecimal idComparativa = new BigDecimal(seleccionados[0]);
				descripcionMod = informesManager.getDescripcionModulo(request, cont);
				if (BigDecimal.valueOf(406).equals(poliza.getLinea().getCodlinea())) {
					tipoAsegurado = informesManager.getTipoAsegurado(idComparativa);

/*
pathInformeFuente = pathInformes + identificadorInforme + JRXML;
				pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

				LOGGER.debug("Inicio de la compilacion del informe  " + identificadorInforme);
				JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
				
			}
						
			ArrayList<BeanLiteralesComparativa> cabeceras = new ArrayList();
			logger.debug("Entro en distribucionCostes");
			Long polizaId = poliza.getIdpoliza();
			logger.debug("getPolizaID = "+polizaId);
			logger.debug("ConsultaDetallesPolizaManager"+ (null != consultaDetallePolizaManager ? " no es null" : " es null"));
			ArrayList arrayDistCost2015 = new ArrayList(consultaDetallePolizaManager.getDistribucionCoste2015ByIdPoliza(polizaId));
			logger.debug("ArrayDistCost2015 size = "+(arrayDistCost2015.size()));
			ArrayList<DistribucionCoste2015> distribucionCostes = sortedListByCodModulo(arrayDistCost2015);
			logger.debug("ArrayDistCost2015 Ordenado size = "+distribucionCostes.size());
			ArrayList<ListaBeanLiteralesComparativa> listaImportesDistCostesGN1 = new ArrayList();
			ArrayList<ListaBeanLiteralesComparativa> listaImportesDistCostesGN2 = new ArrayList();
			ArrayList<ListaBeanLiteralesComparativa> listaLiteralesDistCostesGN1 = new ArrayList();
			ArrayList<ListaBeanLiteralesComparativa> listaLiteralesDistCostesGN2 = new ArrayList();
			ArrayList<BeanDatosInformativosComparativa> datosInformativosComparativa = new ArrayList<>();
			LinkedHashSet<ListaTipoCapitalComparativa> tipoCapitalComparativa = new LinkedHashSet<ListaTipoCapitalComparativa>();
			ArrayList<BeanTablaCoberturasComparativa> coberturaModuloComparativa = new ArrayList<>();
			
			boolean aplicaCaractExpl = caracteristicaExplotacionDao.aplicaCaractExplotacion(poliza.getLinea().getLineaseguroid());
			
			if (aplicaCaractExpl) {
			
				//sacar caracteristica explotacion
				BigDecimal caractExlp = caracteristicaExplotacionManager.calcularCaractExplotacion(poliza, realPath);
				CaracteristicaExplotacion caracteristicaExplotacion = caracteristicaExplotacionDao.getCaracteristicaExplotacion(caractExlp.intValue());
				caractExplotacion = org.apache.commons.lang.StringUtils.leftPad(String.valueOf(caracteristicaExplotacion.getCodcaractexplotacion()),3, '0') 
						+ " " + caracteristicaExplotacion.getDescaractexplotacion();
			
			}
			
			LinkedHashSet<BigDecimal> subvenciones = new LinkedHashSet();
			Boolean hayFinanciacion = false;
			
			for (int i = 0; i < distribucionCostes.size(); i++) {
				DistribucionCoste2015 temp = distribucionCostes.get(i);
				

				if(temp.getRecargoaval() != null && temp.getRecargofraccionamiento() != null  && temp.getTotalcostetomador() != null) {
					hayFinanciacion = true;
*/
				}
				BigDecimal pctFracc = null;
				if (financiacionAgroseguro) {
					String netoTomadorFinanciado = StringUtils
							.nullToString(request.getParameter("netoTomadorFinanciadoAgr" + cont));
					if (!netoTomadorFinanciado.trim().isEmpty()) {
						if (null == importeFraccionamiento) {
							importeFraccionamiento = informesManager.getImporteFraccionamiento(
									poliza.getLinea().getLineaseguroid(),
									poliza.getColectivo().getSubentidadMediadora());
						}
						pctFracc = importeFraccionamiento.getPctRecargo();
					}
				}

				HashMap<String, List> infoCoberturas = informesManager.getDatosCoberturasGarantias(poliza, null, null, false, null, idComparativa.longValue(), this.getServletContext().getRealPath("/WEB-INF/"));
																							 
				JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(infoCoberturas.get("lista"));

				// SubReport Dinamico Generado desde Java
				pathInformeFuente = pathInformes
						+ bundle.getString("informeJasper.declaracionComparativa_tabla_resumen_coberturas") + JRXML;

				// Obtenemos las columnas de coberturas de nuestra poliza
				List<String> cabecera = infoCoberturas.get("cabecera");
				tablaCoberturas.getLayoutTablaCoberturas(pathInformeFuente, cabecera);

				lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionComparativa_distSeguro2015"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_distSeguro2015_B"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_distribucionSeguro_bon2015"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_distribucionSeguro_subvENESA2015"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_distribucionSeguro_subvCCAA2015"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_distribucionSeguroGN"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_tabla_resumen_coberturas"));
				lstIdentificadoresInformes
						.add(bundle.getString("informeJasper.declaracionComparativa_distribucionSeguro_subCCAA"));
				lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionComparativa"));
																								 

				Locale locale = new Locale("es", "ES");
				parametros.put(JRParameter.REPORT_LOCALE, locale);

				// Compilamos todos los reports y los rellenamos
				for (String identificadorInforme : lstIdentificadoresInformes) {

					pathInformeFuente = pathInformes + identificadorInforme + JRXML;
					pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

					LOGGER.debug("Inicio de la compilacion del informe  " + identificadorInforme);
					JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
				}

				// Recuperamos el report principal y el report dinamico, le pasamos como
				// parametro el report dinamico al principal
				File masterFile = new File(
						pathInformes + bundle.getString("informeJasper.declaracionComparativa") + JASPER);
				File sub1File = new File(
						pathInformes + bundle.getString("informeJasper.declaracionComparativa_tabla_resumen_coberturas")
								+ JASPER);

					JasperReport subReport = (JasperReport) JRLoader.loadObject(sub1File);
					/*tablaCoberturas.setCodModulo(codModulo + " " + descripcionMod);
					tablaCoberturas.setCaractExplotacion((caractExplotacion != null) ? caractExplotacion : " ");
					tablaCoberturas.setTitulos(titulosCob(infoCoberturas.get("cabecera")));
					tablaCoberturas.setCoberturas(listas(infoCoberturas.get("lista")));
					LOGGER.debug("infoCobCab length "+infoCoberturas.get("cabecera").size());
					LOGGER.debug("infoCobList length "+infoCoberturas.get("lista").size());
				
					if (!(existeGN2 && !coberturaModuloComparativa.isEmpty() && isEqual(coberturaModuloComparativa.get(coberturaModuloComparativa.size() - 1), tablaCoberturas))) {
					    coberturaModuloComparativa.add(tablaCoberturas);
					}
					*/
				parametros.put("esGanado", esGanado);

				parametros.put("descripcionMod", descripcionMod);
				parametros.put("tipoAsegurado", tipoAsegurado);
				parametros.put("codModulo", codModulo);
				parametros.put("ID", poliza.getIdpoliza().intValue());
				parametros.put("pctRecargo", pctFracc);
				parametros.put("datasource", source);
				parametros.put("tablaSubreport", subReport);

				parametros.put("codplan", poliza.getLinea().getCodplan());
				parametros.put("codlinea", poliza.getLinea().getCodlinea());
				parametros.put("nomlinea", poliza.getLinea().getNomlinea());

				parametros.put(SUBREPORT_DIR, pathReport);
				if (poliza.isPlanMayorIgual2015()) {
					Boolean esFinanciada = (pagoPolizaManager.compruebaPagoFraccionado(poliza) || pctFracc != null);
					parametros.put("isPagoFraccionado", esFinanciada);

				}

				parametros.put("idComparativa", idComparativa);
				addGruposNegocioPorIdModuloComparativa(idComparativa, poliza, parametros);
				conexionBBDD = sessionFactory.getCurrentSession().connection();
				byte[] content = JasperRunManager.runReportToPdf(masterFile.getPath(), parametros, conexionBBDD);
				informes.add(content);
			}
			/*
			logger.debug("<<< PAC >>> Inicio getTiposCapitalComparativa");
			LinkedHashSet<ListaTipoCapitalComparativa> tiposCapitalComparativa = getTiposCapitalComparativa(poliza.getIdpoliza(), esGanado, distribucionCostes);
            logger.debug("<<< PAC >>> Fin getTiposCapitalComparativa");
            
			bdc.setLiteralesDistCostesGN1(listaLiteralesDistCostesGN1);
			bdc.setLiteralesDistCostesGN2(listaLiteralesDistCostesGN2);
			bdc.setListaImportesGN1(listaImportesDistCostesGN1);
			bdc.setListaImportesGN2(listaImportesDistCostesGN2);
			bdc.setDatosInformativos(datosInformativosComparativa);
			bdc.setTiposCapital(tiposCapitalComparativa);
			bdc.setTablaCoberturas(coberturaModuloComparativa);
			logger.debug("--- Tabla coberturas length "+bdc.getTablaCoberturas().size());					
			//DataSources
			logger.debug("<<< PAC >>> DataSources");
			JRBeanCollectionDataSource sourceImportesDistribucionCostesGN1 = new JRBeanCollectionDataSource(bdc.getListaImportesGN1());
			JRBeanCollectionDataSource sourceImportesDistribucionCostesGN2 = new JRBeanCollectionDataSource(bdc.getListaImportesGN2());
			JRBeanCollectionDataSource sourceLiteralesDistribucionCostesGN1 = new JRBeanCollectionDataSource(bdc.getLiteralesDistCostesGN1());
			JRBeanCollectionDataSource sourceLiteralesDistribucionCostesGN2 = new JRBeanCollectionDataSource(bdc.getLiteralesDistCostesGN2());
			JRBeanCollectionDataSource sourceDatosInformativos = new JRBeanCollectionDataSource(bdc.getDatosInformativos());
			JRBeanCollectionDataSource sourceTiposCapital = new JRBeanCollectionDataSource(bdc.getTiposCapital());
			JRBeanCollectionDataSource sourceTablaCoberturas = new JRBeanCollectionDataSource(bdc.getTablaCoberturas());
		
			logger.debug("<<< PAC >>> Recuperamos el report principal y el report dinamico");
			
			// Recuperamos el report principal y el report dinamico, le pasamos como
			// parametro el report dinamico al principal
			masterFile = new File(pathInformes + bundle.getString("informeJasper.declaracionComparativa2024") + JASPER);
			
			parametros.put(JRParameter.REPORT_LOCALE, locale);
			parametros.put("ID", poliza.getIdpoliza().intValue());
			parametros.put("codplan", poliza.getLinea().getCodplan());
			parametros.put("codlinea", poliza.getLinea().getCodlinea());
			parametros.put("nomlinea", poliza.getLinea().getNomlinea());
			parametros.put(SUBREPORT_DIR, pathReport);
			parametros.put("esGanado", esGanado);
			parametros.put("tipoAsegurado", tipoAsegurado);
			
			parametros.put("sourceImportesDistribucionCostesGN1", sourceImportesDistribucionCostesGN1);
			parametros.put("sourceImportesDistribucionCostesGN2", sourceImportesDistribucionCostesGN2);
			parametros.put("sourceLiteralesDistribucionCostesGN1", sourceLiteralesDistribucionCostesGN1);
			parametros.put("sourceLiteralesDistribucionCostesGN2", sourceLiteralesDistribucionCostesGN2);
			parametros.put("sourceDatosInformativos", sourceDatosInformativos);	
			parametros.put("sourceTiposCapital", sourceTiposCapital);	
			parametros.put("sourceTablaCoberturas", sourceTablaCoberturas);
			
			/*Integer tamBanda = tamaño * 15;
			
			String fuente;
			String compilado;
			
			fuente = pathInformes + bundle.getString("informeJasper.literalesDistribucionCosteComparativa2024") + JRXML;
			compilado = pathInformes + bundle.getString("informeJasper.literalesDistribucionCosteComparativa2024") + JASPER;
			
			JasperDesign jasperLiterales = JRXmlLoader.load(new File(fuente));
			JRDesignBand bandaLiterales = (JRDesignBand) ((Object) jasperLiterales).getDetail();
			bandaLiterales.setHeight(tamBanda); //YOU CAN SET THE SIZE THAT YOU WANT 
			JasperCompileManager.compileReport(jasperLiterales);
			JasperCompileManager.compileReportToFile(fuente, compilado);
			
			fuente = pathInformes + bundle.getString("informeJasper.importesDistribucionCosteComparativa2024") + JRXML;
			compilado = pathInformes + bundle.getString("informeJasper.importesDistribucionCosteComparativa2024") + JASPER;
			
			JasperDesign jasperImportes = JRXmlLoader.load(new File(pathInformes + bundle.getString("informeJasper.importesDistribucionCosteComparativa2024") + JRXML));
			JRDesignBand bandaImportes = (JRDesignBand) ((Object) jasperImportes).getDetail();
			bandaImportes.setHeight(tamBanda); //YOU CAN SET THE SIZE THAT YOU WANT              
			JasperCompileManager.compileReport(jasperImportes);
			JasperCompileManager.compileReportToFile(fuente, compilado);
			
			fuente = pathInformes + bundle.getString("informeJasper.importesDistribucionCosteComparativa2024") + JRXML;
			compilado = pathInformes + bundle.getString("informeJasper.importesDistribucionCosteComparativa2024") + JASPER;
			
			JasperDesign jasperDeclaracion = JRXmlLoader.load(new File(pathInformes + bundle.getString("informeJasper.declaracionComparativa2024") + JRXML));         
			JasperCompileManager.compileReport(jasperDeclaracion);
			JasperCompileManager.compileReportToFile(fuente, compilado);
			
			logger.debug("<<< PAC >>> Inicio runReportToPdf");
            conexionBBDD = sessionFactory.getCurrentSession().connection();
			outputStream.write( JasperRunManager.runReportToPdf(masterFile.getPath(), parametros, conexionBBDD));
			content = outputStream.toByteArray();
			logger.debug("<<< PAC >>> Fin runReportToPdf");
			informes.add(content);
			*/
			// Se concatenan los reports generados en un pdf
			PdfCopyFields copy = new PdfCopyFields(new FileOutputStream(pathReport + nombreInforme));
			for (byte[] report : informes) {
				copy.addDocument(new PdfReader(report));
			}
			copy.close();			
			
			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			response.setHeader("Cache-Control", "cache, must-revalidate");
			response.setHeader("Pragma", "public");

			try (InputStream archivo = new FileInputStream(pathReport + nombreInforme);
					ServletOutputStream out = response.getOutputStream()) {
				int bit = 256;
				while ((bit) >= 0) {
					bit = archivo.read();
					out.write(bit);
				}
				out.flush();
			}

			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_GENERACION_POLIZA, e);
			throw new Exception(EXCEPTION_GENERACION_POLIZA, e);
		}
		return;
	}

	private void addGruposNegocioPorIdModuloComparativa(BigDecimal idComparativa, Poliza poliza,
			HashMap<String, Object> parametros) {
		List<String> gruposNegPorIdModulo = informesManager.getGruposNegocioComparativaPorIdModulo(idComparativa,
				poliza);
		String grupoNegPrimero = "";
		String grupoNegSegundo = "";
		if (gruposNegPorIdModulo.size() > 0) {
			if (gruposNegPorIdModulo.size() == 1) {
				grupoNegPrimero = gruposNegPorIdModulo.get(0);
			} else if (gruposNegPorIdModulo.size() == 2) {
				// para ordenarlos
				Integer gn1 = new Integer(gruposNegPorIdModulo.get(0));
				Integer gn2 = new Integer(gruposNegPorIdModulo.get(1));
				if (gn1 < gn2) {
					grupoNegPrimero = gn1.toString();
					grupoNegSegundo = gn2.toString();
				} else {
					grupoNegPrimero = gn2.toString();
					grupoNegSegundo = gn1.toString();
				}
			}
		}
		parametros.put("grupoNegPrimero", grupoNegPrimero);
		parametros.put("grupoNegSegundo", grupoNegSegundo);
	}

	/**
	 * Metodo de impresion del report de una Poliza
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void doInformePolizaParcelasComplementaria(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String pathInformeFuente = null;
		String pathInformeCompilado = null;
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		String nombreInforme = "DeclaracionSeguroParcelasComplementario.pdf";
		String idPoliza = request.getParameter(ID_POLIZA);
		Connection conexionBBDD = null;
		ServletOutputStream out = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		try {
			Poliza poliza = (Poliza) polizaManager.getPoliza(Long.valueOf(idPoliza));
			String barcode = poliza.getGedDocPoliza() != null ? poliza.getGedDocPoliza().getCodBarras() : "";

			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcelas_totalNoPlanton"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcelas_totalPlanton"));
			
			/* Incidencia RGA 11.12.2020 */
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSeguroComplementario_resumenSuperficies"));
			lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_cabecera"));
			/* Incidencia RGA 11.12.2020 Fin */
			
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcelas"));
			lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguroParcelasComplementario"));
			

			// conexionBBDD = dataSource.getConnection();
			conexionBBDD = sessionFactory.getCurrentSession().connection();

			// Compilamos todos los reports y los rellenamos
			for (String identificadorInforme : lstIdentificadoresInformes) {

				pathInformeFuente = pathInformes + identificadorInforme + JRXML;
				pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

				LOGGER.debug(" Inicio de la compilacion del informe " + identificadorInforme);
				JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
				LOGGER.debug("Rellenamos el informe");
				JasperFillManager.fillReport(pathInformeCompilado, parametros, conexionBBDD);
			}
			
			// Recuperamos el report
			File masterFile = new File(pathInformes
					+ bundle.getString("informeJasper.declaracionSeguroParcelasComplementario") + JASPER);
			JasperReport masterReport = (JasperReport) JRLoader.loadObject(masterFile);

			parametros.put("ID", poliza.getIdpoliza().intValue());
			parametros.put(SUBREPORT_DIR,
					getServletContext().getRealPath(PLANTILLAS) + bundle.getString("pathSubreport.contrato"));
			parametros.put("P_BARCODE", barcode);
			parametros.put("P_BARCODE2", barcode);

			JasperPrint jp = JasperFillManager.fillReport(masterReport, parametros, conexionBBDD);

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();
			
			LOGGER.debug(LOGGER_INFORME_PDF);
			JRPdfExporter exporter = new JRPdfExporter();
			/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Inicio */
			SimpleOutputStreamExporterOutput exporterOutput;
			exporterOutput = new SimpleOutputStreamExporterOutput(out);
			exporter.setExporterInput(new SimpleExporterInput(jp));
			exporter.setExporterOutput(exporterOutput);
			/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Fin */ 
			exporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_GENERACION_POLIZA, e);
			throw new Exception(EXCEPTION_GENERACION_POLIZA, e);
		} finally {
			if (out != null)
				out.close();
		}
		return;
	}

							   
	public void doInformeSiniestro(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String nombreInforme = "DeclaracionSiniestro.pdf";
		List<SiniestrosAnteriores>  riesgosSiniestrosAnteriores = new ArrayList<SiniestrosAnteriores>();


		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSiniestro_datosAsegurado"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSiniestro_cabecera"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSiniestro__DatVarPar"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSiniestro_ParcelasSiniestro"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSiniestro_listaSiniestros"));																									  
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSiniestro"));

		Integer idSiniestro = new Integer(request.getParameter("idSiniestro"));
		// DAA Buscamos si existe un siniestro anterior al seleccionado para pasarle el
		// riesgo al informe.
		riesgosSiniestrosAnteriores = informesManager.getRiesgoSiniestrosAnteriores(idSiniestro);
		if ((riesgosSiniestrosAnteriores !=null) && (riesgosSiniestrosAnteriores.size() == 0)) {
			parametros.put("SINIESTROANTERIOR", "No");
		} else {
			parametros.put("SINIESTROANTERIOR", "Si");
		}
		parametros.put("RIESGOSANTERIORES", riesgosSiniestrosAnteriores);
		parametros.put("ID", idSiniestro);
		parametros.put(SUBREPORT_DIR,
				getServletContext().getRealPath(PLANTILLAS) + bundle.getString("pathSubreport.siniestro"));

		generarInforme(request, response, lstIdentificadoresInformes, nombreInforme, parametros);

		return;
	}

	public void doInformeReduccionCapital(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String nombreInforme = "DeclaracionReduccionCapital.pdf";
		//Integer idReduccionCapital = new Integer(request.getParameter("idReduccionCapital"));
		Long idReduccionCapitalLong = new Long(request.getParameter("idReduccionCapital"));
		ReduccionCapital reduccionCapital = reduccionCapitalDao.getReduccionCapital(idReduccionCapitalLong);
		Long estadoCupon = 0L;
		try {
			estadoCupon = reduccionCapital.getCupon().getEstadoCupon().getId();		
		}catch(Exception e){
			logger.debug("ReduccionCapital no posee Cupon");
			estadoCupon = 0L;
		}

		lstIdentificadoresInformes.add(bundle.getString("informeJasper.reduccionCapital_ParcelasAgp"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.reduccionCapitalAgp"));

		parametros.put("ID", String.valueOf(idReduccionCapitalLong));
		parametros.put(SUBREPORT_DIR,
				getServletContext().getRealPath(PLANTILLAS) + bundle.getString("pathSubreport.reduccionCapital"));

		generarInforme(request, response, lstIdentificadoresInformes, nombreInforme, parametros);

		return;
	}
	public void doInformeAnexoModificacion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Poliza poliza = null;

		String idPoliza = request.getParameter(ID_POLIZA);
		if (idPoliza != null) {
			poliza = (Poliza) polizaManager.getPoliza(Long.valueOf(idPoliza));
		} else {
			String idReciboPoliza = request.getParameter(ID_RECIBO_POLIZA);
			ReciboPoliza reciboPoliza = recibosPolizaManager.buscarRecibo(Long.valueOf(idReciboPoliza));
			poliza = (Poliza) polizaManager.getPolizaByReferencia(reciboPoliza.getRefpoliza(),
					reciboPoliza.getTiporef());
		}

		if (poliza.getLinea().isLineaGanado()) {
			String idAnexo = request.getParameter("idAnexo");
			informeAnexoModificacionGanado(request, response, poliza, idAnexo);
		} else {
			informeAnexoModificacionAgro(request, response, poliza);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private void informeAnexoModificacionAgro(HttpServletRequest request, HttpServletResponse response, Poliza poliza)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String nombreInforme = "DeclaracionAnexoModificacion.pdf";

		String pathInformeFuente = null;
		String pathInformeCompilado = null;
		AnexoModificacion anexo = null;
		pathInformes = getServletContext().getRealPath(PLANTILLAS);

		BigDecimal idAnexo = new BigDecimal(request.getParameter("idAnexo"));
		anexo = declaracionesModificacionPolizaManager.getAnexoModifById(Long.parseLong(idAnexo.toString()));
		Connection conexionBBDD = null;
		DisenoTablaCoberturas tablaCoberturas = new DisenoTablaCoberturas();
		Poliza nPoliza = new Poliza();

		if (anexo.getCodmodulo() != null && anexo.getCoberturas() != null && !anexo.getCoberturas().isEmpty()) {

			logger.debug("Inicio de la generacion del PDF de Anexo Mod. con el modulo " + anexo.getCodmodulo());

			if (poliza.getUsuario() != null)
				nPoliza.getUsuario().setCodusuario(poliza.getUsuario().getCodusuario());
			if (poliza.getAsegurado() != null)
				nPoliza.setAsegurado(poliza.getAsegurado());
			if (poliza.getClase() != null)
				nPoliza.setClase(poliza.getClase());
			if (anexo.getCodmodulo() != null)
				nPoliza.setCodmodulo(anexo.getCodmodulo());
			if (poliza.getLinea() != null)
				nPoliza.setLinea(poliza.getLinea());
			if (poliza.getTipoReferencia() != null)
				nPoliza.setTipoReferencia(poliza.getTipoReferencia());
			// DAA 16/07/2012
			if (poliza.getIdpoliza() != null)
				nPoliza.setIdpoliza(poliza.getIdpoliza());

			/// transformar las coberturas del anexo en comparativas y ponerlas en la poliza
			/// quitando las que ya tenia.
			nPoliza = informesManager.transformarCoberturasAComparativas(anexo, nPoliza, poliza);
			// DAA 19/07/2012 idAnexo
			HashMap<String, List> infoCoberturas = informesManager.getDatosCoberturasGarantias(nPoliza,
					anexo.getCodmodulo(), anexo.getId(), true, null, null, this.getServletContext().getRealPath("/WEB-INF/"));
													
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(infoCoberturas.get("lista"));

			// SubReport Dinamico Generado desde Java
			pathInformeFuente = pathInformes
					+ bundle.getString("informeJasper.declaracionSeguro_tabla_resumen_coberturas") + JRXML;

			// Obtenemos las columnas de coberturas de nuestra poliza
			List<String> cabecera = infoCoberturas.get("cabecera");
			tablaCoberturas.getLayoutTablaCoberturas(pathInformeFuente, cabecera);

			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_cabecera"));
			// el nuevo informe de coberturas para el anexo
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSeguro_tabla_resumen_coberturas"));
			// lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_coberturas"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_colectivo"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_entidad_mediador"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_identificacionSeguro"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_asunto"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_pago"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_capital"));
			lstIdentificadoresInformes.add(bundle
					.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_datosParcela_copy"));
			lstIdentificadoresInformes.add(bundle
					.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_datosParcela_poliza"));
			lstIdentificadoresInformes.add(
					bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_datosParcela"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_var_detalle"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_var"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_subv"));
			lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro"));

			parametros.put("ID", idAnexo);
								
			parametros.put(SUBREPORT_DIR, getServletContext().getRealPath(PLANTILLAS)
					+ bundle.getString("pathSubreport.anexoModificacion"));

			conexionBBDD = sessionFactory.getCurrentSession().connection();

			for (String identificadorInforme : lstIdentificadoresInformes) {

				pathInformeFuente = pathInformes + identificadorInforme + JRXML;
				pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

				LOGGER.debug("  Inicio de la compilacion del informe " + identificadorInforme);
				JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
				LOGGER.debug("Rellenamos el informe " + pathInformeFuente);
				JasperFillManager.fillReport(pathInformeCompilado, parametros, conexionBBDD);
			}

			// Recuperamos el report principal y el report dinamico, le pasamos como
			// parametro el report dinamico al principal
			File masterFile = new File(pathInformes
					+ bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro") + JASPER);
			File sub1File = new File(pathInformes
					+ bundle.getString("informeJasper.declaracionSeguro_tabla_resumen_coberturas") + JASPER);

			JasperReport subReport = (JasperReport) JRLoader.loadObject(sub1File);
			JRLoader.loadObject(masterFile);
			parametros.put("datasource", source);
			parametros.put("tablaSubreport", subReport);

			if (poliza.getLinea() != null)
				parametros.put(LINEA_SEGURO_ID, poliza.getLinea().getLineaseguroid());
			else
				parametros.put(LINEA_SEGURO_ID, null);
			
			byte[] content = JasperRunManager.runReportToPdf(pathInformeCompilado, parametros, conexionBBDD);
			logger.debug("Contenido del PDF generado correctamente - Escribimos el contenido en el response");			
			response.setContentType(APPLICATION_PDF);
			response.setContentLength(content.length);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			response.setHeader("Cache-Control", "cache, must-revalidate");
			response.setHeader("Pragma", "public");
			try (ServletOutputStream out = response.getOutputStream();			
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {	
				bufferedOutputStream.write(content);
			}
			logger.debug("Terminada la generacion del PDF de anexo de modificacion");
			return;
		} else {
			logger.debug("Generacion de PDF de anexo modificacion sin modulo");
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_cabecera"));
			// lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_coberturas"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_colectivo"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_entidad_mediador"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_identificacionSeguro"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_asunto"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_pago"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_capital"));
			lstIdentificadoresInformes.add(bundle
					.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_datosParcela_copy"));
			lstIdentificadoresInformes.add(bundle
					.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_datosParcela_poliza"));
			lstIdentificadoresInformes.add(
					bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_datosParcela"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_var_detalle"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas_var"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_parcelas"));
			lstIdentificadoresInformes
					.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro_subv"));
			lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguro"));

			parametros.put("ID", idAnexo);
			if (poliza != null)
				parametros.put(LINEA_SEGURO_ID, poliza.getLinea().getLineaseguroid());
			else
				parametros.put(LINEA_SEGURO_ID, null);

								
			parametros.put(SUBREPORT_DIR, getServletContext().getRealPath(PLANTILLAS)
					+ bundle.getString("pathSubreport.anexoModificacion"));

			generarInforme(request, response, lstIdentificadoresInformes, nombreInforme, parametros);
			logger.debug("Terminada la generacion del PDF de anexo de modificacion sin modulo");
			return;
		}
	}

	@SuppressWarnings("deprecation")
	private void informeAnexoModificacionGanado(HttpServletRequest request, HttpServletResponse response, Poliza poliza,
			String idAnexo) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		HashMap<String, Object> paramSource = new HashMap<String, Object>();
		;
		BufferedOutputStream bufferedOutputStream = null;

		pathInformes = getServletContext().getRealPath(PLANTILLAS);

		parametros.put("IDPOLIZA", new BigDecimal(poliza.getIdpoliza()));
		parametros.put("IDANEXO", new BigDecimal(idAnexo));
		paramSource.put("IDPOLIZA", new BigDecimal(poliza.getIdpoliza()));
		List<InformeAnexModExplotacion> listaModExp = informesManager
				.getModificacionAnexosExplotacion(poliza.getIdpoliza(), new Long(idAnexo));
		JRBeanCollectionDataSource modExp = new JRBeanCollectionDataSource(listaModExp);
		parametros.put("modExp", modExp);
		String subreportDir = new StringBuilder(pathInformes)
				.append(bundle.getString("pathSubreport.anexoModificacionExplotaciones")).toString();
		parametros.put(SUBREPORT_DIR, subreportDir);
		paramSource.put(SUBREPORT_DIR, subreportDir);
		try {
			Connection conexionBBDD = sessionFactory.getCurrentSession().connection();
			LOGGER.debug("Inicio de la compilacion de los informes y sus subinformes.");

			// Cabecera
			rellenarInformeAnexoModificacionGanado(parametros, conexionBBDD,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_Cabecera");

			// Datos variables
			rellenarInformeAnexoModificacionGanado(parametros, conexionBBDD,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_DatosVariables");

			// Alta
			rellenarInformeAnexoModificacionGanado(parametros, conexionBBDD,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_Alta");

			// Baja
			rellenarInformeAnexoModificacionGanado(parametros, conexionBBDD,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_Baja");

			// Cambio IBAN
			rellenarInformeAnexoModificacionGanado(parametros, conexionBBDD,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_CambioIBAN");

			// Master
			JasperPrint jp = rellenarInformeAnexoModificacionGanado(parametros, conexionBBDD,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_Explotaciones");

			// Modificacion
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! OJO
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// Si no compilamos este fichero en Ãºltimo lugar no carga las explotaciones
			// modificadas
			// Como se compila despues que el master IMPLICA que el .jaspwer de este
			// report ya debe de estar, por
			// lo que desplegamos el jasper de este subreport
			this.rellenarInformeAnexoModificacionGanado(parametros, modExp,
					"informeJasper.declaracionSolicitudModificacionSeguroGanado_Modificacion");

			Date fechaActual = new Date();
			DateFormat sdf = new SimpleDateFormat(ConstantsInf.FORMATO_FECHA_YYYYMMDD_HHMMSS);
			String nombreInformeOut = new StringBuilder("PdfAnexoMod_").append(sdf.format(fechaActual)).toString();

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInformeOut + ".pdf");
			ServletOutputStream out = response.getOutputStream();
			
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInformeOut + ".pdf");
			exporter.exportReport();

			LOGGER.debug("FIN Informe");
		} catch (JRException e) {
			LOGGER.error(
					"JRException - Error durante la generacion del informe del anexo de modificacion de explotaciones",
					e);
			throw new Exception(
					"JRException - Error durante la generacion del informe del anexo de modificacion de explotaciones",
					e);
		} catch (Exception e) {
			LOGGER.error(LOGGER_ANEXO_EXPLOTACIONES, e);
			throw new Exception(LOGGER_ANEXO_EXPLOTACIONES,
					e);
		} catch (Throwable e) {
			LOGGER.error(LOGGER_ANEXO_EXPLOTACIONES, e);
			throw new Exception(LOGGER_ANEXO_EXPLOTACIONES,
					e);

		} finally {
			if (bufferedOutputStream != null)
				bufferedOutputStream.close();
		}

		LOGGER.debug("Terminada la generacion del PDF de la solicitud de modificaciïon de explotaciones de ganado.");
	}

	private JasperPrint rellenarInformeAnexoModificacionGanado(HashMap<String, Object> parametros, Object datasource,
			String informeParaRellenar) throws JRException {
		StringBuilder mensajeLog = new StringBuilder("Compilando de informe ")
				.append(informeParaRellenar.split("_")[1]/* nombreInforme.toUpperCase() */);
		LOGGER.debug(mensajeLog.toString());
		String pathRelativo = bundle.getString(informeParaRellenar);
		String pathInformeCompilado = new StringBuilder(pathInformes).append(pathRelativo).append(JASPER).toString();
		JasperPrint jp = null;
		if (datasource instanceof Connection) {
			jp = JasperFillManager.fillReport(pathInformeCompilado, parametros, (Connection) datasource);
		} else {
			jp = JasperFillManager.fillReport(pathInformeCompilado, parametros,
					(JRBeanCollectionDataSource) datasource);
		}
		LOGGER.debug(mensajeLog.append(" FINALIZADO").toString());
		return jp;
	}

	@SuppressWarnings("deprecation")
	public void doInformeAnexoModificacionComplementario(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String nombreInforme = "DeclaracionAnexoModificacion.pdf";
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		String pathInformeFuente = null;
		String pathInformeCompilado = null;
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		String idPoliza = request.getParameter(ID_POLIZA);
		Connection conexionBBDD = null;
		DisenoTablaCoberturas tablaCoberturas = new DisenoTablaCoberturas();
		ServletOutputStream out = null;
		Poliza poliza = null;

		// Mejora 17/02/2012 Angel - Visualizar en el informe de anexo las coberturas
		// como en el informe de Poliza

		if (idPoliza != null) {
			poliza = (Poliza) polizaManager.getPoliza(Long.valueOf(idPoliza));
		} else {
			String idReciboPoliza = request.getParameter(ID_RECIBO_POLIZA);
			ReciboPoliza reciboPoliza = recibosPolizaManager.buscarRecibo(Long.valueOf(idReciboPoliza));
			poliza = (Poliza) polizaManager.getPolizaByReferencia(reciboPoliza.getRefpoliza(),
					reciboPoliza.getTiporef());
		}
		List<BeanTablaCoberturas> infoCoberturas = informesManager.getDatosCoberturasGarantiasComplementaria(poliza);
		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(infoCoberturas);

		// SubReport Dinamico Generado desde Java
		pathInformeFuente = pathInformes
				+ bundle.getString("informeJasper.declaracionSeguroComplementario_tabla_resumen_coberturas") + JRXML;
		tablaCoberturas.getLayoutTablaCoberturasComplementaria(pathInformeFuente);

		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_cabecera"));
		// el nuevo informe de coberturas para el anexo
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguroComplementario_tabla_resumen_coberturas"));
		// lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_coberturas"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_colectivo"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_entidad_mediador"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_identificacionSeguro"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_asunto"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_pago"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas_capital"));
		lstIdentificadoresInformes.add(bundle.getString(
				"informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas_datosParcela_copy"));
		lstIdentificadoresInformes.add(bundle.getString(
				"informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas_datosParcela_poliza"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas_datosParcela"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas_var_detalle"));
		lstIdentificadoresInformes.add(
				bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas_var"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_parcelas"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario_subv"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario"));

		conexionBBDD = sessionFactory.getCurrentSession().connection();

		// Compilamos todos los reports y los rellenamos
		for (String identificadorInforme : lstIdentificadoresInformes) {

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

			LOGGER.debug("Inicio de la compilacion del  informe " + identificadorInforme);
			JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
			LOGGER.debug("Rellenamos el informe");
			JasperFillManager.fillReport(pathInformeCompilado, parametros, conexionBBDD);
		}

		// Recuperamos el report principal y el report dinamico, le pasamos como
		// parametro el report dinamico al principal
		File masterFile = new File(pathInformes
				+ bundle.getString("informeJasper.declaracionSolicitudModificacionSeguroComplementario") + JASPER);
		File sub1File = new File(pathInformes
				+ bundle.getString("informeJasper.declaracionSeguroComplementario_tabla_resumen_coberturas")
				+ JASPER);

		JasperReport subReport = (JasperReport) JRLoader.loadObject(sub1File);
		JasperReport masterReport = (JasperReport) JRLoader.loadObject(masterFile);

		parametros.put("datasource", source);
		parametros.put("tablaSubreport", subReport);

		String idAnexo = "";
		BigDecimal idAnexoB = new BigDecimal(0);

		idAnexo = request.getParameter("idAnexo");

		if (idAnexo != "" && idAnexo != null) {
			idAnexoB = new BigDecimal(idAnexo);
		} else {
			String idAnexoCpl = request.getParameter("idAnexoCompl");
			idAnexoB = new BigDecimal(idAnexoCpl);
		}

		parametros.put("ID", idAnexoB);
		parametros.put(SUBREPORT_DIR, getServletContext().getRealPath(PLANTILLAS)
				+ bundle.getString("pathSubreport.anexoModificacionComplementario"));

		JasperPrint jp = JasperFillManager.fillReport(masterReport, parametros, conexionBBDD);

		response.setContentType(APPLICATION_PDF);
		response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
		out = response.getOutputStream();
		
		LOGGER.debug(LOGGER_INFORME_PDF);
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
		exporter.exportReport();
		LOGGER.debug(LOGGER_INFORME_EXPORTADO);
		// TMR FActuracion.Al imprimir informes facturamos
		LOGGER.debug(LOGGER_PL_FACTURACION);
		seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		return;
	}

	@SuppressWarnings("unchecked")
	public void doInformeAsegurados(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String pathInformeFuente = null;
		String identificadorInforme = "";
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		String formato = StringUtils.nullToString(request.getParameter(FORMATO));
		String nombreInforme = "ListadoAsegurados." + formato;
		ServletOutputStream out = null;
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			List<DatoAsegurado> listaAsegurados = (ArrayList<DatoAsegurado>) request.getAttribute("listaAseg");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaAsegurados);

			if (formato.equals("pdf")) {
				identificadorInforme = bundle.getString("informeJasper.listadoAseguradosPDF");
			} else if (formato.equals("xls")) {
				identificadorInforme = bundle.getString("informeJasper.listadoAseguradosEXCEL");
			}
			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);

			if (formato.equals("xls")) {
				
				response.setContentType(APP_EXCEL);	
				out = response.getOutputStream();
				
				LOGGER.debug("Exportamos el informe a xls ");
				JRXlsExporter xlsExporter = new JRXlsExporter();

				xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);				
				xlsExporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			} else if (formato.equals("pdf")) {
				
				response.setContentType(APPLICATION_PDF);	
				out = response.getOutputStream();
				
				LOGGER.debug(LOGGER_INFORME_PDF);
				JRPdfExporter exporter = new JRPdfExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);

				exporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			}

			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de asegurados: ", e);
			throw new Exception("Error durante la generacion del informe de asegurados", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	@SuppressWarnings("unchecked")
	public void doInformeSocios(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String nombreInforme = "ListadoSocios.pdf";
		String pathInformeFuente = null;
		String identificadorInforme = bundle.getString("informeJasper.listadoSocios");
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			List<Socio> listaSocios = (ArrayList<Socio>) request.getAttribute("listaSocios");
			// DAA 17/07/2013
			String nombreAsegurado = (String) request.getAttribute("nombreAsegurado");
			parametros.put("NOMBREASEGURADO", nombreAsegurado);
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaSocios);

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();
			
			LOGGER.debug(LOGGER_INFORME_PDF);
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
			exporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de asegurados: ", e);
			throw new Exception("Error durante la generacion del informe de asegurados", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	@SuppressWarnings("unchecked")
	public void doInformeTomadores(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String nombreInforme = "ListadoTomadores.pdf";
		String pathInformeFuente = null;
		String identificadorInforme = bundle.getString("informeJasper.listadoTomadores");
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			List<Tomador> listaTomadores = (ArrayList<Tomador>) request.getAttribute("listaTom");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaTomadores);

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();
			
			LOGGER.debug(LOGGER_INFORME_PDF);
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
			exporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de tomadores: ", e);
			throw new Exception("Error durante la generacion del informe de tomadores", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	@SuppressWarnings("unchecked")
	public void doInformeColectivos(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String nombreInforme = "ListadoColectivos.pdf";

		String pathInformeFuente = null;
		String identificadorInforme = bundle.getString("informeJasper.listadoColectivos");
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			List<Colectivo> listaColectivos = (ArrayList<Colectivo>) request.getAttribute("listaCol");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaColectivos);

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();
			
			LOGGER.debug(LOGGER_INFORME_PDF);
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
			exporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de colectivos: ", e);
			throw new Exception("Error durante la generacion del informe de colectivos", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	public void doInformeColectivoAlta(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String id = StringUtils.nullToString(request.getParameter("id"));

		String nombreInforme = "ColectivoAlta.pdf";
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionColectivo_cabecera"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionColectivo"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionColectivo_RGPD"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionColectivo_general"));

		parametros.put("ID", new BigDecimal(id));
		parametros.put(SUBREPORT_DIR,
				getServletContext().getRealPath(PLANTILLAS) + bundle.getString("pathSubreport.colectivo"));
		parametros.put("FECHA", new Date());
		// Carga los nombres de las copias del informe en el mapa de parï¿½metros
		cargarCopiasInformeColectivoAlta(parametros);

		generarInforme(request, response, lstIdentificadoresInformes, nombreInforme, parametros);

		return;
	}

	/**
	 * Introduce los nombres de las copias de este informe dinï¿½micamente
	 * dependiendo de los valores introducidos en el properties
	 * 
	 * @param parametros
	 */
	private void cargarCopiasInformeColectivoAlta(HashMap<String, Object> parametros) {
		int i = 0;
		String valor = null;
		final String keyCopia = "informeJasper.declaracionColectivo_copia";
		do {
			// Obtiene el valor de la propiedad
			try {
				valor = bundle.getString(keyCopia + (++i));
			} catch (MissingResourceException e) {
				logger.debug("cargarCopiasInformeColectivoAlta. La propiedad " + (keyCopia + i)
						+ " no existe en el properties");
				valor = null;
			}
			// Si no es nula, se incluye como parï¿½metro del informe
			if (valor != null)
				parametros.put("COPIA_" + i, valor);
		} while (valor != null);
	}

	@SuppressWarnings("unchecked")
	public void doInformeUtilidades(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String formato = StringUtils.nullToString(request.getParameter(FORMATO));
		String nombreInforme = "ListadoUtilidades." + formato;
		String pathInformeFuente = null;
		String identificadorInforme = null;
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		Map<Long, String> mapRc = new HashMap<Long, String>();
		Map<Long, String> mapStr = new HashMap<Long, String>();
		Map<Long, String> mapMp = new HashMap<Long, String>();
		Map<Long, String> mapFinanciada = new HashMap<Long, String>();
		Map<Long, String> mapRyD = new HashMap<Long, String>();
		Map<Long, BigDecimal> mapPneta = new HashMap<Long, BigDecimal>();
		Map<Long, BigDecimal> mapCosteNeto = new HashMap<Long, BigDecimal>();
		Map<Long, BigDecimal> mapCostNetoTomador = new HashMap<Long, BigDecimal>();
		Map<Long, BigDecimal> mapTotSup = new HashMap<Long, BigDecimal>();
		Map<Long, String> mapCCC = new HashMap<Long, String>();
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */ 
		/* Incluimos la nueva cuenta en el informe de Utilidades Póliza */
		Map<Long, String> mapCCC2 = new HashMap<Long, String>();
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */
		Map<Long, String> mapEnvioIban = new HashMap<Long, String>();
		Map<Long, String> mapTitular = new HashMap<Long, String>();
		Map<Long, String> mapDestDom = new HashMap<Long, String>();
		/* P73325 - RQ.04, RQ.05 y RQ.06 */
		Map<Long, String> mapCanal = new HashMap<Long, String>();
		Map<Long, String> mapUsuarioFirma = new HashMap<Long, String>();
		Map<Long, Date> mapFechaFirma = new HashMap<Long, Date>();
		Map<Long, Character> mapDocFirmada = new HashMap<Long, Character>();	
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		try {
			List<Poliza> listaPolizas = (ArrayList<Poliza>) request.getAttribute("listaPol");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaPolizas);

			if (formato.equals("pdf")) {
				identificadorInforme = bundle.getString("informeJasper.listadoUtilidadesPdf");
			} else if (formato.equals("xls")) {
				identificadorInforme = bundle.getString("informeJasper.listadoUtilidadesExcel");
			}

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);
			// mapas para prima neta bonif/recargo, coste neto, coste neto tomador, total
			// superficie y ccc
			for (Poliza po : listaPolizas) {
				// po.getRenovableSn();//iteramos por esta propiedad para que le asigne valor
				// porque es lazy=true
				// po.getFecharenovacion(); //Ídem
				Iterator<PagoPoliza> pagoP = po.getPagoPolizas().iterator();
				while (pagoP.hasNext()) {
					PagoPoliza pago = pagoP.next();
					
					/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
					/* IBAN Pago Prima */
					if (pago.getCccbanco() != null) {
						
						/* Enviamos también el IBAN */
						String ibanPagoPrima = pago.getIban() + pago.getCccbanco();
						mapCCC.put(po.getIdpoliza(), ibanPagoPrima);
					}
					/* IBAN Cobro Siniestros */
					if (pago.getCccbanco2() != null) {
						
						/* Enviamos también el IBAN */
						String ibanCobroSiniestro = pago.getIban2() + pago.getCccbanco2();
						mapCCC2.put(po.getIdpoliza(), ibanCobroSiniestro);
					}
					/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */
					
					if (pago.getEnvioIbanAgro() != null) {
						mapEnvioIban.put(po.getIdpoliza(), pago.getEnvioIbanAgro().toString());
					}
					if (pago.getTitularCuenta() != null) {
						mapTitular.put(po.getIdpoliza(), pago.getTitularCuenta());
					}
					if (pago.getDestinatarioDomiciliacion() != null) {
						mapDestDom.put(po.getIdpoliza(), pago.getDestinatarioDomiciliacion().toString());
					}
				}
				if (po.getTotalsuperficie() != null)
					mapTotSup.put(po.getIdpoliza(), po.getTotalsuperficie());
				Set<DistribucionCoste> distribucionesCostes = po.getDistribucionCostes();
				Set<DistribucionCoste2015> distribucionesCostes2015 = po.getDistribucionCoste2015s();

				// Diferenciamos si es una poliza de 2015 en adelante o no para mostrar la prima
				// comercial neta o la prima neta respectivamente en el campo PBC
				if (po.getLinea().getCodplan().intValue() >= 2015) {
					if (distribucionesCostes2015 != null && !distribucionesCostes2015.isEmpty()) {
						DistribucionCoste2015 distribCostes2015 = distribucionesCostes2015
								.toArray(new DistribucionCoste2015[] {})[0];
						if (distribCostes2015.getPrimacomercialneta() != null) {
							mapPneta.put(po.getIdpoliza(), distribCostes2015.getPrimacomercialneta());
						}
						// Las columnas C.Neto y C.Neto Tomador deben ir vacias (No existen esos campos
						// en la nueva distribucion de costes)
					}
				} else {
					if (distribucionesCostes != null && !distribucionesCostes.isEmpty()) {
						DistribucionCoste distribCostes = distribucionesCostes.toArray(new DistribucionCoste[] {})[0];
						if (distribCostes.getPrimaneta() != null) {
							mapPneta.put(po.getIdpoliza(), distribCostes.getPrimaneta());
						}
						if (distribCostes.getCosteneto() != null) {
							mapCosteNeto.put(po.getIdpoliza(), distribCostes.getCosteneto());
						}
						if (distribCostes.getCargotomador() != null) {
							mapCostNetoTomador.put(po.getIdpoliza(), distribCostes.getCargotomador());
						}
					}
				}
			}

			for (Poliza po : listaPolizas) {
				// anexosRc
				String res = "N";
				if (po.getTieneanexorc().equals('S'))
					res = "S";
				mapRc.put(po.getIdpoliza(), res);

				// siniestros
				res = "N";
				if (po.getTienesiniestros().equals('S'))
					res = "S";
				mapStr.put(po.getIdpoliza(), res);

				// anexosMp
				res = "N";
				if (po.getTieneanexomp().equals('S'))
					res = "S";
				mapMp.put(po.getIdpoliza(), res);

				// financiada
				res = "N";
				if (po.getEsFinanciada().equals('S'))
					res = "S";
				mapFinanciada.put(po.getIdpoliza(), res);

				// RyD
				res = "N";
				if (po.getEsRyD().equals('S'))
					res = "S";
				mapRyD.put(po.getIdpoliza(), res);

				/* P0073325 - RQ.04, RQ.05 y RQ.06 Inicio */
				if ((po.getGedDocPoliza() != null) && (po.getGedDocPoliza().getCanalFirma() != null)) {
					if (po.getGedDocPoliza().getCanalFirma().getIdCanal() != null) {
						mapCanal.put(po.getIdpoliza(), po.getGedDocPoliza().getCanalFirma().getNombreCanal());
					}
					

				}

				if ((po.getGedDocPoliza() != null)
						&& !StringUtils.nullToString(po.getGedDocPoliza().getDocFirmada()).equals("")) {
					Character docFirmada =  po.getGedDocPoliza().getDocFirmada();
					mapDocFirmada.put(po.getIdpoliza(), docFirmada);
					if (Constants.CHARACTER_S.equals(docFirmada)) {
						if (po.getGedDocPoliza().getCodUsuario() != null) {
							mapUsuarioFirma.put(po.getIdpoliza(), po.getGedDocPoliza().getCodUsuario());
						}
						if (po.getGedDocPoliza().getFechaFirma() != null) {
							mapFechaFirma.put(po.getIdpoliza(), po.getGedDocPoliza().getFechaFirma());
						}
					}
				}
				/* P0073325 - RQ.04, RQ.05 y RQ.06 Fin */
			}

			parametros.put("RC", mapRc);
			parametros.put("STR", mapStr);
			parametros.put("MP", mapMp);
			parametros.put("FINANCIADA", mapFinanciada);
			parametros.put("RYD", mapRyD);
			parametros.put("PNETA", mapPneta);
			// parametros.put("BONREC", mapBonRecargo);
			parametros.put("COSNETO", mapCosteNeto);
			parametros.put("COSNETOTOMADOR", mapCostNetoTomador);
			parametros.put("TOTSUPERFICIE", mapTotSup);
			parametros.put("CCC", mapCCC);
			/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
			parametros.put("CCC2", mapCCC2);
			/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */
			parametros.put("IBAN", mapEnvioIban);
			parametros.put("TITULAR", mapTitular);
			parametros.put("DESTDOM", mapDestDom);
			/* P0073325 - RQ.04, RQ.05 y RQ.06 Inicio */
			parametros.put("CANAL", mapCanal);
			parametros.put("DOCFIRMADA", mapDocFirmada);
			parametros.put("USUARIOFIRMA", mapUsuarioFirma);
			parametros.put("FECHAFIRMA", mapFechaFirma);
			/* P0073325 - RQ.04, RQ.05 y RQ.06 Fin */
			// JRBeanCollectionDataSource source = new
			// JRBeanCollectionDataSource(listaPolizas);
			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);

			if (formato.equals("xls")) {
				
				response.setContentType(APP_EXCEL);
				out = response.getOutputStream();
				
				LOGGER.debug("Exportamos el informe a  xls");
				JRXlsExporter xlsExporter = new JRXlsExporter();

				xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
				
				xlsExporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			} else if (formato.equals("pdf")) {
				
				response.setContentType(APPLICATION_PDF);
				out = response.getOutputStream();
				
				LOGGER.debug(LOGGER_INFORME_PDF);
				JRPdfExporter exporter = new JRPdfExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);

				exporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			}
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de utilidades de polizas: ", e);
			throw new Exception("Error durante la generacion del informe de utilidades de polizas", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	@SuppressWarnings("unchecked")
	public void doInformeIncidencias(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String nombreInforme = "ListadoIncidencias.pdf";
		String pathInformeFuente = null;
		String identificadorInforme = bundle.getString("informeJasper.listadoIncidencias");
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			List<FicheroIncidencia> listaIncidencias = (ArrayList<FicheroIncidencia>) request
					.getAttribute("listIncidencias");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaIncidencias);

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();
			
			LOGGER.debug(LOGGER_INFORME_PDF);
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
			exporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de incidencias: ", e);
			throw new Exception("Error durante la generacion del informe de incidencias", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}


	@SuppressWarnings("unchecked")
	public void doInformeIncidenciasUnificado(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String nombreInforme = "ListadoIncidenciasUnificado.pdf";
		String pathInformeFuente = null;
		String identificadorInforme = bundle.getString("informeJasper.listadoIncidencias");
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			List<FicheroIncidenciasUnificado> listaIncidencias = (ArrayList<FicheroIncidenciasUnificado>) request
					.getAttribute("listIncidencias");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaIncidencias);

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

			response.setContentType(APPLICATION_PDF);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();
			
			LOGGER.debug(LOGGER_INFORME_PDF);
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
			exporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);

			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de incidencias de fichero de comisiones unificado: ",
					e);
			throw new Exception(
					"Error durante la generacion del informe de incidencias de fichero de comisiones unificado", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	/**
	 * Para imprimir el listado de parcelas de póliza
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doInformeListadoParcelasPoliza(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String formato = StringUtils.nullToString(request.getParameter(FORMATO));
		String nombreInforme = "ListadoParcelas." + formato;
		String pathInformeFuente = null;
		String identificadorInforme = null;
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		try {
			List<BeanParcela> listaParcelas = (ArrayList<BeanParcela>) request.getAttribute("listaParcelasPoliza");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaParcelas);
			parametros.put("IS_PRINCIPAL", request.getAttribute("esPrincipal"));
			
			if (formato.equals("pdf")) {
				identificadorInforme = bundle.getString("informeJasper.listadoParcelasPoliza.pdf");
			} else if (formato.equals("xls")) {
				identificadorInforme = bundle.getString("informeJasper.listadoParcelasPoliza.excel");
			}

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);
			// mapas para prima neta bonif/recargo, coste neto, coste neto tomador, total
			// superficie y ccc

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);
			
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);

			if (formato.equals("xls")) {
				
				response.setContentType(APP_EXCEL);
				out = response.getOutputStream();
				
				LOGGER.debug(" Exportamos el informe a xls");
				JRXlsExporter xlsExporter = new JRXlsExporter();

				xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
				response.setContentType(APP_EXCEL);
				xlsExporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			} else if (formato.equals("pdf")) {
				
				response.setContentType(APPLICATION_PDF);
				out = response.getOutputStream();
				
				LOGGER.debug(LOGGER_INFORME_PDF);
				JRPdfExporter exporter = new JRPdfExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);

				exporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			}
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de utilidades de polizas: ", e);
			throw new Exception("Error durante la generacion del informe de utilidades de polizas", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	/**
	 * Para imprimir el listado de parcelas de póliza
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void doInformeConsultaDeudaAplazada(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String formato = "";
		String nombreInforme = "ListadoDeudaAplazada." + formato;
		String pathInformeFuente = null;
		String identificadorInforme = null;
		String perfil = "";
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;
		TableFacade tableFacade = new TableFacade("consultaDeudaAplazada", request);
		Limit limit = tableFacade.getLimit();
		formato = limit.getExportType().toString();
		Boolean esExterno = false;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		perfil = usuario.getPerfil().substring(4);
		esExterno = usuario.isUsuarioExterno();
		parametros.put("perfil", perfil);
		parametros.put("esExterno", esExterno);
		try {

			List<InformeDeudaAplazadaUnificado> lstItems = (ArrayList<InformeDeudaAplazadaUnificado>) request
					.getAttribute("lstItems");
			// List<BeanParcela> listaParcelas =
			// (ArrayList<BeanParcela>)request.getAttribute("listaParcelasPoliza");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(lstItems);

			// if (formato.equals("pdf")){
			// identificadorInforme =
			// bundle.getString("informeJasper.listadoDeudaAplazada.pdf");
			// } else if (formato.equals("xls")){
			// identificadorInforme =
			// bundle.getString("informeJasper.listadoDeudaAplazada.excel");
			// }
			if ((Constants.PERFIL_0).toString().equals(perfil)) {
				identificadorInforme = bundle.getString("informeJasper.listadoDeudaAplazadaP0.excel");
			} else if ((Constants.PERFIL_1).toString().equals(perfil) && (esExterno)) {
				identificadorInforme = bundle.getString("informeJasper.listadoDeudaAplazadaP01Externo.excel");

			} else {
				identificadorInforme = bundle.getString("informeJasper.listadoDeudaAplazadaPComun.excel");

			}
			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);
			// mapas para prima neta bonif/recargo, coste neto, coste neto tomador, total
			// superficie y ccc
			parametros.put("isExterno", limit.isExported());
			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);
			
			response.setContentType(APP_EXCEL);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			out = response.getOutputStream();

			// if (formato.equals("xls")){
			LOGGER.debug("Exportamos el  informe a xls");
			JRXlsExporter xlsExporter = new JRXlsExporter();

			xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
			xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);			
			xlsExporter.exportReport();
			LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de comisiones de Deuda aplazada: ", e);
			throw new Exception("Error durante la generacion del informe de comisiones de Deuda aplazada", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}

	/**
	 * Para imprimir el listado de parcelas de anexo
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doInformeListadoParcelasAnexo(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		String formato = StringUtils.nullToString(request.getParameter(FORMATO));
		String nombreInforme = "ListadoParcelasAnexo." + formato;
		String pathInformeFuente = null;
		String identificadorInforme = null;
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		ServletOutputStream out = null;

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		try {
			List<BeanParcelaAnexo> listaParcelasAnexo = (ArrayList<BeanParcelaAnexo>) request
					.getAttribute("listaParcelasAnexo");
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listaParcelasAnexo);
			parametros.put("IS_PRINCIPAL", request.getAttribute("esPrincipal"));

			if (formato.equals("pdf")) {
				identificadorInforme = bundle.getString("informeJasper.listadoParcelasAnexo.pdf");
			} else if (formato.equals("xls")) {
				identificadorInforme = bundle.getString("informeJasper.listadoParcelasAnexo.excel");
			}

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);
			// mapas para prima neta bonif/recargo, coste neto, coste neto tomador, total
			// superficie y ccc

			JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);
			
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			
			if (formato.equals("xls")) {
				
				response.setContentType(APP_EXCEL);
				out = response.getOutputStream();
				
				LOGGER.debug("Exportamos el informe a xls");
				JRXlsExporter xlsExporter = new JRXlsExporter();

				xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
				response.setContentType(APP_EXCEL);
				xlsExporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			} else if (formato.equals("pdf")) {
				
				response.setContentType(APPLICATION_PDF);
				out = response.getOutputStream();
				
				LOGGER.debug(LOGGER_INFORME_PDF);
				JRPdfExporter exporter = new JRPdfExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);

				exporter.exportReport();
				LOGGER.debug(LOGGER_INFORME_EXPORTADO);
			}					
			
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del informe de utilidades de polizas: ", e);
			throw new Exception("Error durante la generacion del informe de utilidades de polizas", e);
		} finally {
			if (out != null)
				out.close();
		}

		return;
	}
 
	
	@SuppressWarnings("deprecation")
																	
	private void generarInforme(HttpServletRequest request, HttpServletResponse response,
			List<String> lstIdentificadoresInformes, String nombreInforme, HashMap<String, Object> parametros)
			throws Exception {
		pathInformes = getServletContext().getRealPath(PLANTILLAS);
		String pathInformeFuente = null;
		String pathInformeCompilado = null;
		Connection conexionBBDD = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		try {
			conexionBBDD = sessionFactory.getCurrentSession().connection();

			for (String identificadorInforme : lstIdentificadoresInformes) {

				pathInformeFuente = pathInformes + identificadorInforme + JRXML;
				pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

				LOGGER.debug("Inicio de la compilacion del informe '" + identificadorInforme + "'");
				JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
				LOGGER.debug("Rellenamos el informe '" + identificadorInforme + "'");
				JasperFillManager.fillReport(pathInformeCompilado, parametros, conexionBBDD);
			}

			byte[] content = JasperRunManager.runReportToPdf(pathInformeCompilado, parametros, conexionBBDD);
			
			response.setContentType(APPLICATION_PDF);
			response.setContentLength(content.length);
			response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);
			response.setHeader("Cache-Control", "cache, must-revalidate");
			response.setHeader("Pragma", "public");
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
			// TMR FActuracion.Al imprimir informes facturamos
			LOGGER.debug(LOGGER_PL_FACTURACION);
			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_IMPRESION);

		} catch (Exception e) {
			LOGGER.error("Error durante la generacion del contrato", e);
			throw new Exception("Error durante la generacion del contrato ", e);

		} catch (Throwable e) {
			LOGGER.error("Error durante la generacion del  contrato", e);
			throw new Exception("Error durante la generacion  del contrato", e);

		}
	}
	
	/**
	 * Genera un informe generico en formato Excel o PDF a partir de un listado de objetos proporcionado.
	 * El metodo recibe los atributos necesarios a traves de la request.
	 * 
	 * @param request  La solicitud HttpServletRequest que contiene los atributos necesarios:
	 *                 - formato: El formato del informe, puede ser "xls" o "pdf".
	 *                 - nombreInforme: El nombre del archivo de salida sin extension.
	 *                 - jasperPath: La ruta del archivo Jasper en el archivo de recursos.
	 *                 - listado: El atributo de la request que contiene el listado de objetos a incluir en el informe.
	 * @param response La respuesta HttpServletResponse donde se escribe el informe generado.
	 * @throws Exception Si ocurre algun error durante la generacion del informe.
	 */
	public void doInformeListado(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    HashMap<String, Object> parametros = new HashMap<String, Object>();
	    String formato = StringUtils.nullToString(request.getParameter(FORMATO));
	    formato = "xls"; // Se fuerza a Excel mientras no se pida que se exporte a PDF
	    String nombreInforme = request.getAttribute("nombreInforme") + "." + formato;
	    String jasperPath = (String) request.getAttribute("jasperPath");
	    String pathInformeFuente = null;
	    String identificadorInforme = null;
	    pathInformes = getServletContext().getRealPath(PLANTILLAS);
	    ServletOutputStream out = null;

	    try {
	        List<?> listado = (List<?>) request.getAttribute("listado");
	        Map<?, ?> totProds = (Map<?, ?>) request.getAttribute("totProds");
	        Map<?, ?> sumAseg = (Map<?, ?>) request.getAttribute("sumAseg");
	        
	        if (totProds!= null)
	        	parametros.put("PRODUCCIONES", totProds);
	        
	        if (sumAseg != null) {
	        	parametros.put("SUMASASEGURADAS", sumAseg);
	        }
	        
	        JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listado);

	        if (formato.equals("pdf")) {
	            identificadorInforme = bundle.getString(jasperPath + ".pdf");
	        } else if (formato.equals("xls")) {
	            identificadorInforme = bundle.getString(jasperPath + ".excel");
	        }

	        pathInformeFuente = pathInformes + identificadorInforme + JRXML;
	        JasperReport jasper = JasperCompileManager.compileReport(pathInformeFuente);

	        JasperPrint jp = JasperFillManager.fillReport(jasper, parametros, source);

	        response.setHeader(CONTENT_DISPOSITION, FILENAME + nombreInforme);

	        if (formato.equals("xls")) {
	            response.setContentType(APP_EXCEL);
	            out = response.getOutputStream();

	            LOGGER.debug("Exportamos el informe a xls");
	            JRXlsExporter xlsExporter = new JRXlsExporter();

	            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	            xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
	            xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
	            xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
	            xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
	            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
	            response.setContentType(APP_EXCEL);
	            xlsExporter.exportReport();
	            LOGGER.debug(LOGGER_INFORME_EXPORTADO);
	        } else if (formato.equals("pdf")) {
	            response.setContentType(APPLICATION_PDF);
	            out = response.getOutputStream();

	            LOGGER.debug(LOGGER_INFORME_PDF);
	            JRPdfExporter exporter = new JRPdfExporter();

	            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
	            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);

	            exporter.exportReport();
	            LOGGER.debug(LOGGER_INFORME_EXPORTADO);
	        }
	    } catch (Exception e) {
	        LOGGER.error("Error durante la generacion del informe: ", e);
	        throw new Exception("Error durante la generacion del informe", e);
	    } finally {
	        if (out != null)
	            out.close();
	    }

	    return;
	}
	
	public void doInformeListadoComisiones(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    HashMap<String, Object> parametros = new HashMap<String, Object>();
	    String formato = StringUtils.nullToString(request.getParameter(FORMATO));
	    formato = "xls"; // Se fuerza a Excel mientras no se pida que se exporte a PDF
	    String nombreInforme = request.getAttribute("nombreInforme") + "." + formato;
	    String jasperPath = (String) request.getAttribute("jasperPath");
	    String pathInformeFuente = null;
	    String identificadorInforme = null;
	    pathInformes = getServletContext().getRealPath(PLANTILLAS);
	    ServletOutputStream out = null;
	    List<InformeComisionesUnificado> listado = (List<InformeComisionesUnificado>) request.getAttribute("listado");
	    String perfil = "";
	    if (request.getAttribute("PERFIL") != null) {
	    	perfil = request.getAttribute("PERFIL").toString();
	    }
	    
	    Boolean externo = Boolean.FALSE;
	    if (request.getAttribute("EXTERNO") != null) {
	    	externo = (Boolean) request.getAttribute("EXTERNO");
	    }
		try {
		    // Crear un nuevo diseño de reporte
			JasperDesign jasperDesign = new JasperDesign();
			
            jasperDesign.setName("dynamic_report");
            jasperDesign.setPageWidth(595);
            jasperDesign.setPageHeight(842);
            jasperDesign.setColumnWidth(555);
            jasperDesign.setColumnSpacing(0);
            jasperDesign.setLeftMargin(20);
            jasperDesign.setRightMargin(20);
            jasperDesign.setTopMargin(20);
            jasperDesign.setBottomMargin(20);

            // Crear el título del informe
            JRDesignBand titleBand = new JRDesignBand();
            titleBand.setHeight(50);
            JRDesignStaticText title = new JRDesignStaticText();
            title.setX(0);
            title.setY(0);
            title.setWidth(555);
            title.setHeight(30);
            title.setText("LISTADO DE COMISIONES 2015+");
            title.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
            title.setFontSize(22);
            titleBand.addElement(title);
            jasperDesign.setTitle(titleBand);

            
            List<String> columns = new ArrayList<>(Arrays.asList("E Med", "S Med", "Oficina", "Plan", "Linea", "Poliza",
            		"F. Vigor", "CIF Tomador", "Colectivo", "NIF Aseg", "Asegurado", "Fase", "F. Emision", "G.N", "PCN"));
            List<String> properties = new ArrayList<>(Arrays.asList("entmediadora", "subentmediadora", "oficina", "plan",
            		"linea", "referencia", "fechaVigor", "ciftomador", "idcolectivo", "nifcif", "nombreAsegurado", "fase", "fechaEmisionRecibo",
            		"grupoNegocio", "primaComercialNeta"));
            List<Class<?>> classList = devuelveListaClases(perfil, externo);
            
            
            if ("0".equals(perfil)) {
            	columns.addAll(columns.size(), Arrays.asList("Dev Adm", "Dev Adq", "Dev Ent", "Dev Med", "Abon Adm", "Abon Adq", "Abon Ent",
            			"Abon Med", "Pte Adm", "Pte Adq", "Pte Ent", "Pte Med"));
            	properties.addAll(properties.size(), Arrays.asList("gdAdmin", "gdAdq", "gdCommedEntidad", "gdCommedEsmed", 
            			"gaAdmin", "gaAdq", "gaCommedEntidad", "gaCommedEsmed", "gpAdmin", "gpAdq", "gpCommedEntidad", "gpCommedEsmed"));
            }
            else if (("1".equals(perfil) && !externo)  || "5".equals(perfil)) {
            	columns.addAll(columns.size(), Arrays.asList("Dev Ent", "Dev Med", "Abon Ent", "Abon Med", "Pte Ent", "Pte Med"));
            	properties.addAll(properties.size(), Arrays.asList( "gdCommedEntidad", "gdCommedEsmed", 
            			"gaCommedEntidad", "gaCommedEsmed", "gpCommedEntidad", "gpCommedEsmed"));
            }
            else if ("1".equals(perfil) && externo) {
            	columns.addAll(columns.size(), Arrays.asList("Dev Med", "Abon Med", "Pte Med"));
            	properties.addAll(properties.size(), Arrays.asList( "gdCommedEsmed", 
            			 "gaCommedEsmed",  "gpCommedEsmed"));
            }

            // Crear el encabezado de la tabla
            JRDesignBand columnHeader = new JRDesignBand();
            columnHeader.setHeight(20);
            for (int i = 0; i < columns.size(); i++) {
                JRDesignStaticText columnHeaderText = new JRDesignStaticText();
                columnHeaderText.setX(i * 100);
                columnHeaderText.setY(0);
                columnHeaderText.setWidth(100);
                columnHeaderText.setHeight(20);
                columnHeaderText.setText(columns.get(i));
                columnHeaderText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
                columnHeader.addElement(columnHeaderText);
                
             // Establecer bordes para el encabezado de columna
                JRLineBox lineBox = columnHeaderText.getLineBox();
                lineBox.getTopPen().setLineWidth(1f);
                lineBox.getBottomPen().setLineWidth(1f);
                lineBox.getLeftPen().setLineWidth(1f);
                lineBox.getRightPen().setLineWidth(1f);
            }
            
            jasperDesign.setColumnHeader(columnHeader);

            // Crear el detalle de la tabla
            JRDesignBand detailBand = new JRDesignBand();
            detailBand.setHeight(30);
            for (int i = 0; i < properties.size(); i++) {
                JRDesignField field = new JRDesignField();
                field.setName(properties.get(i));
                field.setValueClass(classList.get(i));
                jasperDesign.addField(field);

                JRDesignTextField textField = new JRDesignTextField();
                textField.setX(i * 100);
                textField.setY(0);
                textField.setWidth(100);
                textField.setHeight(20);
                textField.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
                if (classList.get(i) == Date.class) {
                	textField.setExpression(new JRDesignExpression( "new java.text.SimpleDateFormat(\"dd/MM/yyyy\").format($F{" + properties.get(i) + "})"));
                }
                else {
                	textField.setExpression(new JRDesignExpression("$F{" + properties.get(i) + "}"));
                }
                
                detailBand.addElement(textField);
                
                JRLineBox lineBox = textField.getLineBox();
                lineBox.getTopPen().setLineWidth(1f);
                lineBox.getBottomPen().setLineWidth(1f);
                lineBox.getLeftPen().setLineWidth(1f);
                lineBox.getRightPen().setLineWidth(1f);
            }
            JRDesignSection detailSection = (JRDesignSection) jasperDesign.getDetailSection();
            detailSection.addBand(detailBand);

            jasperDesign.setPageHeight(Integer.MAX_VALUE); 
            // Compilar el diseño
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            // Llenar el informe con datos
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listado);

            // Llenar el reporte con datos
            JasperPrint jp = JasperFillManager.fillReport(jasperReport, new HashMap<String, Object>(), dataSource);
            
            response.setContentType(APP_EXCEL);
            out = response.getOutputStream();

            LOGGER.debug("Exportamos el informe a xls");
            JRXlsExporter xlsExporter = new JRXlsExporter();
            
            /*SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
            configuration.setOnePagePerSheet(true); // Evita la paginación creando una sola hoja
            xlsExporter.setConfiguration(configuration);*/

            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
            xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, nombreInforme);
            response.setContentType(APP_EXCEL);
            xlsExporter.exportReport();
            

		    
	    } catch (Exception e) {
	        LOGGER.error("Error durante la generacion del informe: ", e);
	        throw new Exception("Error durante la generacion del informe", e);
	    } finally {
	        if (out != null)
	            out.close();
	    }

	    return;
	}
 
	private List<Class<?>> devuelveListaClases(String perfil, boolean externo){
		 
		List<Class<?>> classList = new ArrayList<>();
        classList.add(Integer.class);
        classList.add(Integer.class);
        classList.add(String.class);
        classList.add(Integer.class);
        classList.add(Integer.class);
        classList.add(String.class);
        classList.add(Date.class);
        classList.add(String.class);
        classList.add(String.class);
        classList.add(String.class);
        classList.add(String.class);
        classList.add(Integer.class);
        classList.add(Date.class);
        classList.add(Character.class);
        classList.add(BigDecimal.class);
        
        if ("0".equals(perfil)) {
        	for (int i=0; i<12; i++) {
        		classList.add(BigDecimal.class);
        	}
        }
        else if (("1".equals(perfil) && !externo)  || "5".equals(perfil)) {
        	for (int i=0; i<6; i++) {
        		classList.add(BigDecimal.class);
        	}
        }
        else if ("1".equals(perfil) && externo) {
        	for (int i=0; i<3; i++) {
        		classList.add(BigDecimal.class);
        	}
        }
        
        
        
        return classList;
	}
	
	public void setInformesManager(InformesManager informesManager) {
		this.informesManager = informesManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setRecibosPolizaManager(RecibosPolizaManager recibosPolizaManager) {
		this.recibosPolizaManager = recibosPolizaManager;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

	public void setSbpTxtManager(ISbpTxtManager sbpTxtManager) {
		this.sbpTxtManager = sbpTxtManager;
	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}

	public void setSocioSubvencionManager(SocioSubvencionManager socioSubvencionManager) {
		this.socioSubvencionManager = socioSubvencionManager;
	}

	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}

	public void setReduccionCapitalDao(IReduccionCapitalDao reduccionCapitalDao) {
		this.reduccionCapitalDao = reduccionCapitalDao;
	}

}