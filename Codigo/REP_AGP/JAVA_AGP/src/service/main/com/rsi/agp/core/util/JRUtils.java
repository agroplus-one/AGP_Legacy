package com.rsi.agp.core.util;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rga.documentacion.srvmaestro.beans.FirmaXmpBaseBean;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.report.layout.BeanTablaCoberturas;
import com.rsi.agp.core.report.layout.DisenoTablaCobertExplotaciones;
import com.rsi.agp.core.report.layout.DisenoTablaCobertParcelas;
import com.rsi.agp.core.report.layout.DisenoTablaCoberturas;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class JRUtils {

	private static final String INFORME_JASPER_DECLARACION_SEGURO_COMPLEMENTARIO_TABLA_RESUMEN_COBERTURAS = "informeJasper.declaracionSeguroComplementario_tabla_resumen_coberturas";
	private static final String PATH_SUBREPORT_CONTRATO = "pathSubreport.contrato";
	private static final String INICIO_DE_LA_COMPILACION_DEL_INFORME = "Inicio de la compilacion del informe ";
	private static final String SHOW_ENT_SUBENT_MED = "SHOW_ENT_SUBENT_MED";
	private static final String CCAA2015 = "CCAA2015";
	private static final String ENESA2015 = "ENESA2015";
	private static final String FECHA = "FECHA";
	private static final String RELLENAMOS_EL_INFORME = "Rellenamos el informe ";
	private static final String INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_PARC = "informeJasper.declaracionSeguro_tabla_resumen_cobert_parc";
	private static final String JASPER = ".jasper";
	private static final String INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_EXPLO = "informeJasper.declaracionSeguro_tabla_resumen_cobert_explo";
	private static final String CABECERA = "cabecera";
	private static final String JRXML = ".jrxml";
	private static final String INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERTURAS = "informeJasper.declaracionSeguro_tabla_resumen_coberturas";
	private static final String LISTA = "lista";

	private static final Log logger = LogFactory.getLog(JRUtils.class);

	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private static final ResourceBundle bundle_siniestros = ResourceBundle.getBundle("agp_cobro_siniestros");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JasperPrint getInformePoliza(final String pathInformes, final Poliza poliza, final BigDecimal entidad,
			final BigDecimal subEntidad, final Boolean esGanado, final List<Socio> lstSocios,
			final HashMap<String, Object> lstCompNoElegidas, final String strImprimirReducida,
			final HashMap<String, List> infoCoberturas, final HashMap<Long, HashMap<String, List>> infoNumExpCobert,
			final Map<String, String> coberturasElegidasParcela, final Map<String, String> mapSub,
			final Object fechaFinContratacion, final String tipoPago, final boolean isPagoFraccionado,
			final Boolean polizaTieneSubvCaractAseguradoPersonaJuridica, final Boolean lineaSup2021,
			final Map<String, String> mapNotas, final Connection connection, IDocumentacionGedManager documentacionGedManager, final Boolean aseguradoVulnerable) throws Exception {


		JasperPrint jp;

		final HashMap<Long, HashMap<String, List>> infoNumExpCobertExplotaciones;
		final HashMap<Long, HashMap<String, List>> infoNumExpCobertParcelas;

		infoNumExpCobertExplotaciones = infoNumExpCobert;
		/* Pet. 63485-Fase II ** MODIF TAM (29.09.2020) ** Inicio */
		infoNumExpCobertParcelas = infoNumExpCobert;

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String pathInformeFuente = null;
		String pathInformeCompilado = null;

		DisenoTablaCoberturas tablaCoberturas = new DisenoTablaCoberturas();

		/* Pet. 57622 ** MODIF TAM (03.06.2019) ** INICIO */
		String pathInformeExplFuente = null;

		DisenoTablaCobertExplotaciones tablaCobertExplotaciones = new DisenoTablaCobertExplotaciones();

		/* Fin 57622 * FIN */

		/* Pet. 63485-Fase II ** MODIF TAM (30.09.2020) ** Inicio */
		String pathInformeParcFuente = null;
		DisenoTablaCobertParcelas tablaCobertParcelas = new DisenoTablaCobertParcelas();

		JRBeanCollectionDataSource socios = new JRBeanCollectionDataSource(lstSocios);

		// Para pintar las No elegibles en el informe
		parametros.put("lstCompNoElegidas", lstCompNoElegidas);

		// Parametro para elegir el tipo de impresion: false=normal. true=reducida
		boolean imprimirReducida = false;
		if ("true".equals(strImprimirReducida))
			imprimirReducida = true;
		parametros.put("imprimirReducida", imprimirReducida);

		// DAA 19/07/2012 idAnexo
		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(infoCoberturas.get(LISTA));

		parametros.put("COB_PARCELAS", coberturasElegidasParcela);

		// SubReport Dinamico Generado desde Java
		pathInformeFuente = pathInformes + bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERTURAS)
				+ JRXML;
		logger.debug("pathInformeFuente:" + pathInformeFuente);

		// Obtenemos las columnas de coberturas de nuestra poliza
		List<String> cabecera = infoCoberturas.get(CABECERA);
		tablaCoberturas.getLayoutTablaCoberturas(pathInformeFuente, cabecera);

		/* Pet. 57622 ** MODIF TAM (03.06.2019) ** Inicio */
		/*
		 * Hacemos un bucle que recorra cada una de las explotaciones y el nombre del
		 * subreport, le pasamos el nº de la explotación de esta forma intentaremos
		 * pasarle tantos subreport como explotaciones tenga y en el jasper le pasamos
		 * el nº de explotación tambien por parametro
		 * 
		 * Subreport Dinamico generado desde java para las coberturas de las
		 * explotaciones.
		 */

		/* Parametros para las explotaciones de Ganado */
		HashMap<Long, JRBeanCollectionDataSource> dataSourceExpl = new HashMap<Long, JRBeanCollectionDataSource>();
		HashMap<Long, JasperReport> tablaSubreportExp = new HashMap<Long, JasperReport>();

		/* Parametros para las Parcelas de Agrícolas */
		HashMap<Long, JRBeanCollectionDataSource> dataSourceParc = new HashMap<Long, JRBeanCollectionDataSource>();
		HashMap<Long, JasperReport> tablaSubreportParc = new HashMap<Long, JasperReport>();

		if (esGanado) {


			pathInformeExplFuente = pathInformes
					+ bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_EXPLO) + JRXML;

			if (infoNumExpCobertExplotaciones != null) {

				Set<Long> explotaciones = infoNumExpCobertExplotaciones.keySet();

				for (Long num_explo : explotaciones) {

					HashMap<String, List> infoCobertExplotaciones = infoNumExpCobertExplotaciones.get(num_explo);

					JRBeanCollectionDataSource sourceExpl = null;

					if (infoCobertExplotaciones.get(LISTA).size() > 0) {
						sourceExpl = new JRBeanCollectionDataSource(infoCobertExplotaciones.get(LISTA));
						dataSourceExpl.put(num_explo, sourceExpl);
					}

					List<String> cabeceraExpl = null;
					cabeceraExpl = infoCobertExplotaciones.get(CABECERA);
					if (cabeceraExpl != null) {

						tablaCobertExplotaciones.getLayoutTablaCobertExplo(pathInformeExplFuente, cabeceraExpl);

						/* (07.08.2019) * Nos creamos el fichero jasper */
						String IdentifInformeExpl = bundle
								.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_EXPLO);

						pathInformeFuente = pathInformes + IdentifInformeExpl + JRXML;
						pathInformeCompilado = pathInformes + IdentifInformeExpl + JASPER;

						JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
						logger.debug(RELLENAMOS_EL_INFORME + IdentifInformeExpl);
						JasperFillManager.fillReport(pathInformeCompilado, parametros, connection);

						File sub2File = new File(pathInformes
								+ bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_EXPLO)
								+ JASPER);
						JasperReport subReportExpl = (JasperReport) JRLoader.loadObject(sub2File);

						/* Informamos los hashmap */
						tablaSubreportExp.put(num_explo, subReportExpl);
					}
				}
			}
		} else {

			/* Pet. 63485-Fase II ** MODIF TAM (29.09.2020) ** Inicio */
			pathInformeParcFuente = pathInformes
					+ bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_PARC) + JRXML;
			logger.debug("pathInformeParcFuente:" + pathInformeParcFuente);

			if (infoNumExpCobertParcelas != null) {

				Set<Long> Parcelas = infoNumExpCobertParcelas.keySet();

				for (Long num_Parc : Parcelas) {

					HashMap<String, List> infoCobertParcelas = infoNumExpCobertParcelas.get(num_Parc);

					JRBeanCollectionDataSource sourceParc = null;

					if (infoCobertParcelas.get(LISTA).size() > 0) {
						sourceParc = new JRBeanCollectionDataSource(infoCobertParcelas.get(LISTA));
						dataSourceParc.put(num_Parc, sourceParc);
					}

					List<String> cabeceraParc = null;
					cabeceraParc = infoCobertParcelas.get(CABECERA);
					if (cabeceraParc != null) {

						tablaCobertParcelas.getLayoutTablaCobertParc(pathInformeParcFuente, cabeceraParc);

						String IdentifInformeParc = bundle
								.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_PARC);

						pathInformeFuente = pathInformes + IdentifInformeParc + JRXML;
						pathInformeCompilado = pathInformes + IdentifInformeParc + JASPER;

						JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
						logger.debug("Rellenamos el informe de Agricola" + IdentifInformeParc);
						JasperFillManager.fillReport(pathInformeCompilado, parametros, connection);

						File sub2File = new File(pathInformes
								+ bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERT_PARC)
								+ JASPER);
						JasperReport subReportParc = (JasperReport) JRLoader.loadObject(sub2File);

						/* Informamos los hashmap */
						tablaSubreportParc.put(num_Parc, subReportParc);
					}
				}
			}

		}

		/*
		 * Mejora modificar fecha firma. 14/02/2012. Tamara Si la poliza tiene estado
		 * definitiva la fecha sera fechaDefinitiva en otro caso sera la fecha de hoy
		 */
		// IGT 08/04/2019 P57624 - PCT-5729 - RQ.03
		// Fecha de envío para pólizas en estado superior a grabacion definitiva
		int compare = poliza.getEstadoPoliza().getIdestado().compareTo(new BigDecimal(3));
		switch (compare) {
		case 0: // grabacion definitiva
			parametros.put(FECHA, poliza.getFechadefinitiva());
			break;
		case 1: // enviada
			parametros.put(FECHA, poliza.getFechaenvio());
			break;
		case -1: // borradores
		default:
			parametros.put(FECHA, new Date());
			break;
		}
		// Fin Mejora

		parametros.put("fechaFinContratacion", fechaFinContratacion);

		// porcentaje Subvenciones
		if (mapSub != null) {

			parametros.put("%ENESA2015", "(" + mapSub.get(ENESA2015) + ")");
			parametros.put("%CCAA2015", "(" + mapSub.get(CCAA2015) + ")");
		}

		parametros.put("isPagoFraccionado", isPagoFraccionado);

		parametros.put("polizaTieneSubvCaractAseguradoPersonaJuridica", polizaTieneSubvCaractAseguradoPersonaJuridica);

		parametros.put("lineaSup2021", lineaSup2021);
		if (lineaSup2021) {
			parametros.put("exentoCuentaCobroSiniestros",
					exentoCuentaCobroSiniestros(poliza.getLinea().getCodlinea(), poliza.getCodmodulo()));
		} else {
			parametros.put("exentoCuentaCobroSiniestros", Boolean.TRUE);
		}

		if (mapNotas != null) {

			parametros.putAll(mapNotas);
		}

		parametros.put("TIPO_POLIZA", "P");

		/* SUBIDA A PRODUCCION 2011/11/30 */
		String iban = "", cuenta1 = "", cuenta2 = "", cuenta3 = "", cuenta4 = "", cuenta5 = "";
		String cuentaDatosAseg = AseguradoUtil.obtenerCcc(poliza, true);
		if (!StringUtils.isNullOrEmpty(cuentaDatosAseg)) {
			iban = cuentaDatosAseg.substring(0, 4);
			cuenta1 = cuentaDatosAseg.substring(4, 8);
			cuenta2 = cuentaDatosAseg.substring(8, 12);
			cuenta3 = cuentaDatosAseg.substring(12, 16);
			cuenta4 = cuentaDatosAseg.substring(16, 20);
			cuenta5 = cuentaDatosAseg.substring(20, 24);
		}
		parametros.put("IBAN_ASEG", iban);
		parametros.put("CUENTA_1", cuenta1);
		parametros.put("CUENTA_2", cuenta2);
		parametros.put("CUENTA_3", cuenta3);
		parametros.put("CUENTA_4", cuenta4);
		parametros.put("CUENTA_5", cuenta5);
		
		parametros.put("aseguradoVulnerable", aseguradoVulnerable);

		/* Pet. 57622 ** MODIF TAM (03.06.2019) */
		/*
		 * lstIdentificadoresInformes.add(bundle.getString(
		 * "informeJasper.declaracionSeguro_tabla_resumen_cobert_explo"));
		 */
		/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Inicio */
		String subreportDir = pathInformes + bundle.getString(PATH_SUBREPORT_CONTRATO);
		logger.debug("pathInformeParcFuente:" + subreportDir);
		parametros.put("SUBREPORT_DIR", subreportDir);
		/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 FIn */
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_cabecera"));
		lstIdentificadoresInformes.add(bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERTURAS));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosAsegurado"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosTomador"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion_AV"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion3"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion5"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion5_AV"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcela_cosecha_detalle"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_detalleDatosCosecha"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_resumenSuperficies"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_totalInstalaciones"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_totalMetrosCuadrados"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_totalUnidades"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_totalNoPlanton"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_totalPlanton"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_distCosteParcela"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas_distCosteParcela2015"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_hojaParcelas"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_ExplotacionesGanadoDatosVariables"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_explotaciones_riesgosCubiertos"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_explotaciones_gruposRaza"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_ExplotacionesGanado"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosPago"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosPago2021"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subCCAA"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subvEnesa"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subvEnesa2015"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subvCCAA2015"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_bonifRecar2015"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro2015"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro2015_B"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_informePago"));
		lstIdentificadoresInformes.add(bundle.getString(
				"informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_resumenCoberturas_caract_explotacion"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_subvencionDeclarable_tipo"));
		lstIdentificadoresInformes.add(bundle.getString(
				"informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_subvencionDeclarable_tipo_gan"));
		lstIdentificadoresInformes.add(
				bundle.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_resumenCoberturas"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_subvencionDeclarable"));
		lstIdentificadoresInformes.add(
				bundle.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_riesgosNoElegidos"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_CoberturasGarantias"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_decPersonasJuridicas"));
		lstIdentificadoresInformes.add("/jasper/declaracionSeguro/declaracionSeguro_ipid");
		lstIdentificadoresInformes.add("/jasper/declaracionSeguro/declaracionSeguro_ipid2");
		lstIdentificadoresInformes.add("/jasper/declaracionSeguro/declaracionSeguro_ipid2");
		lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2");
		lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2_AV");
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro"));
		

		parametros.put("ID", poliza.getIdpoliza().intValue());
		parametros.put("SUBREPORT_COMUN_DIR", pathInformes + "/jasper/comun/");

		parametros.put("entMed", String.valueOf(entidad));
		parametros.put("subEntMed", String.valueOf(subEntidad));
		parametros.put("caja", String.valueOf(entidad));
		parametros.put("oficina", poliza.getOficina());

		final Pattern entidadPattern = Pattern.compile("[3][0-9][0-9][0-9]"); // 3xxx-x
		if (entidadPattern.matcher(String.valueOf(entidad)).matches() && BigDecimal.ZERO.equals(subEntidad)) {
			parametros.put(SHOW_ENT_SUBENT_MED, Boolean.FALSE);
		} else {
			parametros.put(SHOW_ENT_SUBENT_MED, Boolean.TRUE);
		}

		parametros.put("RUTA_IPID",
				ResourceBundle.getBundle("agp_informes_jasper").getString("informeJasper.ipid.ruta"));
		parametros.put("IMAGEN_DIR", pathInformes + "/jasper/declaracionSeguro/images/");
		parametros.put("codLinea", poliza.getLinea().getCodlinea().toString());
		/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Inicio */
		logger.debug("Obteniendo codigo de barras");
		String barcode = poliza.getGedDocPoliza() != null ? poliza.getGedDocPoliza().getCodBarras() : "";
		logger.debug("barcode:" + barcode);
		parametros.put("P_BARCODE", barcode);
		parametros.put("P_BARCODE2", barcode);
		/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Fin */

		// Compilamos todos los reports y los rellenamos
		for (String identificadorInforme : lstIdentificadoresInformes) {
			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			pathInformeCompilado = pathInformes + identificadorInforme + JASPER;
			logger.debug(INICIO_DE_LA_COMPILACION_DEL_INFORME + identificadorInforme);

			JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
			logger.debug(RELLENAMOS_EL_INFORME + identificadorInforme);
			JasperFillManager.fillReport(pathInformeCompilado, parametros, connection);
		}

		addGrupoNegocio(poliza.getDistribucionCoste2015s(), parametros);

		// Recuperamos el report principal y el report dinamico, le pasamos como
		// parametro el report dinamico al principal
		File masterFile = new File(pathInformes + bundle.getString("informeJasper.declaracionSeguro") + JASPER);
		File sub1File = new File(
				pathInformes + bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_TABLA_RESUMEN_COBERTURAS) + JASPER);

		JasperReport subReport = (JasperReport) JRLoader.loadObject(sub1File);
		JasperReport masterReport = (JasperReport) JRLoader.loadObject(masterFile);

		/* pasamos los hashmap */
		if (esGanado) {
			parametros.put("datasourcExpl", dataSourceExpl);
			parametros.put("tablaSubreportExp", tablaSubreportExp);
		} else {
			parametros.put("datasourcParc", dataSourceParc);
			parametros.put("tablaSubreportParc", tablaSubreportParc);
		}

		/* Pet. 57622 ** MODIF TAM (03.06.2019) ** Fin */

		parametros.put("lstSocios", socios);
		parametros.put("datasource", source);
		parametros.put("tablaSubreport", subReport);

		parametros.put("CODENTIDAD", poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad());
		parametros.put("ENTMEDIADORA",
				poliza.getColectivo().getSubentidadMediadora().getEntidadMediadora().getCodentidad());
		parametros.put("pintarDC2015", poliza.isPlanMayorIgual2015());
		parametros.put("esGanado", esGanado);
		parametros.put("plan", poliza.getLinea().getCodplan().toString());
		parametros.put("linea", poliza.getLinea().getCodlinea() + " - " + poliza.getLinea().getNomlinea());
		parametros.put("esRenovable", new Character('S').equals(poliza.getRenovableSn()) ? Constants.ES_RENOVABLE
				: Constants.NO_ES_RENOVABLE);
		parametros.put("estado", poliza.getEstadoPoliza().getDescEstado());
		parametros.put("IDPOLIZA", poliza.getIdpoliza().intValue());
		Locale locale = new Locale("es", "ES");
		parametros.put(JRParameter.REPORT_LOCALE, locale);
		parametros.put("tipoPago", tipoPago);

		jp = JasperFillManager.fillReport(masterReport, parametros, connection);

		// compilacion socios
		lstIdentificadoresInformes.clear();
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_decPersonasJuridicas_listado"));
		for (String identificadorInforme : lstIdentificadoresInformes) {

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

			logger.debug(INICIO_DE_LA_COMPILACION_DEL_INFORME + identificadorInforme);
			JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
			logger.debug(RELLENAMOS_EL_INFORME + identificadorInforme);
			JasperFillManager.fillReport(pathInformeCompilado, parametros, socios);
		}
		logger.debug("getInformePoliza [END]");
		return jp;
	}

	public static JasperPrint getInformePolizaComplementaria(final String pathInformes, final Poliza poliza,
			final BigDecimal entidad, final BigDecimal subEntidad, final List<BeanTablaCoberturas> infoCoberturas,
			final Map<String, String> mapSub, final Date fechaFinContratacion, final String tipoPago,
			final boolean isPagoFraccionado, final Boolean lineaSup2021, final Map<String, String> mapNotas,
			final Connection connection, final Boolean aseguradoVulnerable) throws Exception {

		JasperPrint jp;

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		List<String> lstIdentificadoresInformes = new ArrayList<String>();
		String pathInformeFuente = null;
		String pathInformeCompilado = null;

		DisenoTablaCoberturas tablaCoberturas = new DisenoTablaCoberturas();

		// FechaFinContratacion
		parametros.put("fechaFinContratacion", fechaFinContratacion);

		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(infoCoberturas);

		// SubReport Dinamico Generado desde Java
		pathInformeFuente = pathInformes
				+ bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_COMPLEMENTARIO_TABLA_RESUMEN_COBERTURAS) + JRXML;
		tablaCoberturas.getLayoutTablaCoberturasComplementaria(pathInformeFuente);
		/*
		 * Mejora modificar fecha firma. 14/02/2012. Tamara Si la poliza tiene estado
		 * definitiva la fecha sera fechaDefinitiva en otro caso sera la fecha de hoy
		 */
		// IGT 08/04/2019 P57624 - PCT-5729 - RQ.03
		// Fecha de envío para pólizas en estado superior a grabacion definitiva
		int compare = poliza.getEstadoPoliza().getIdestado().compareTo(new BigDecimal(3));
		switch (compare) {
		case 0: // grabacion definitiva
			parametros.put(FECHA, poliza.getFechadefinitiva());
			break;
		case 1: // enviada
			parametros.put(FECHA, poliza.getFechaenvio());
			break;
		case -1: // borradores
		default:
			parametros.put(FECHA, new Date());
			break;
		}
		// Fin Mejora

		// porcentaje Subvenciones
		if (mapSub != null) {

			parametros.put(ENESA2015, "(" + mapSub.get(ENESA2015) + ")");
			parametros.put(CCAA2015, "(" + mapSub.get(CCAA2015) + ")");
		}

		parametros.put("isPagoFraccionado", isPagoFraccionado);

		parametros.put("lineaSup2021", lineaSup2021);
		if (lineaSup2021) {
			parametros.put("exentoCuentaCobroSiniestros",
					exentoCuentaCobroSiniestros(poliza.getLinea().getCodlinea(), poliza.getCodmodulo()));
		} else {
			parametros.put("exentoCuentaCobroSiniestros", Boolean.TRUE);
		}

		// Genero la nota informativa a mostrar
		if (mapNotas != null) {

			parametros.putAll(mapNotas);
		}

		parametros.put("TIPO_POLIZA", "C");

		String iban = "", cuenta1 = "", cuenta2 = "", cuenta3 = "", cuenta4 = "", cuenta5 = "";
		String cuentaDatosAseg = AseguradoUtil.obtenerCcc(poliza, true);
		if (!StringUtils.isNullOrEmpty(cuentaDatosAseg)) {
			iban = cuentaDatosAseg.substring(0, 4);
			cuenta1 = cuentaDatosAseg.substring(4, 8);
			cuenta2 = cuentaDatosAseg.substring(8, 12);
			cuenta3 = cuentaDatosAseg.substring(12, 16);
			cuenta4 = cuentaDatosAseg.substring(16, 20);
			cuenta5 = cuentaDatosAseg.substring(20, 24);
		}
		parametros.put("IBAN_ASEG", iban);
		parametros.put("CUENTA_1", cuenta1);
		parametros.put("CUENTA_2", cuenta2);
		parametros.put("CUENTA_3", cuenta3);
		parametros.put("CUENTA_4", cuenta4);
		parametros.put("CUENTA_5", cuenta5);
		
		parametros.put("aseguradoVulnerable", aseguradoVulnerable);

		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_cabecera"));
		lstIdentificadoresInformes
				.add(bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_COMPLEMENTARIO_TABLA_RESUMEN_COBERTURAS));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosAsegurado"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosTomador"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion_AV"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion3"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion5"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_condicion5_AV"));

		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcela_cosecha_detalle"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguroComplementario_detalleDatosCosecha"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguroComplementario_resumenSuperficies"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcelas_totalNoPlanton"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcelas_distCosteParcela"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguroComplementario_hojaParcelas"));

		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosPago"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_datosPago2021"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subvEnesa2015"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subvCCAA2015"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_bonifRecar2015"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro2015"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro2015_B"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subCCAA"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro_subvEnesa"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_distribucionSeguro"));

		lstIdentificadoresInformes.add(bundle.getString(
				"informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_resumenCoberturas_caract_explotacion"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_subvencionDeclarable_tipo"));
		lstIdentificadoresInformes.add(
				bundle.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_resumenCoberturas"));
		lstIdentificadoresInformes.add(bundle
				.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_subvencionDeclarable"));
		lstIdentificadoresInformes.add(
				bundle.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura_riesgosNoElegidos"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_CoberturasGarantias"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_datosTomadorAseguradoCobertura"));
		lstIdentificadoresInformes
				.add(bundle.getString("informeJasper.declaracionSeguro_decPersonasJuridicas_listado"));
		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguro_decPersonasJuridicas"));

		/* MODIF INCIDENCIA */
		lstIdentificadoresInformes.add("/jasper/declaracionSeguro/declaracionSeguro_ipid");
		lstIdentificadoresInformes.add("/jasper/declaracionSeguro/declaracionSeguro_ipid2");
		/* MODIF INCIDENCIA FIN */
		
		lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2");
		lstIdentificadoresInformes.add("/jasper/comun/Anexo_RGPD_2_AV");

		lstIdentificadoresInformes.add(bundle.getString("informeJasper.declaracionSeguroComplementario"));

		parametros.put("SUBREPORT_DIR_NOTA", pathInformes + bundle.getString(PATH_SUBREPORT_CONTRATO));

		parametros.put("SUBREPORT_COMUN_DIR", pathInformes + "/jasper/comun/");

		parametros.put("entMed", String.valueOf(entidad));
		parametros.put("subEntMed", String.valueOf(subEntidad));
		parametros.put("caja", String.valueOf(entidad));
		parametros.put("oficina", poliza.getOficina());

		final Pattern entidadPattern = Pattern.compile("[3][0-9][0-9][0-9]"); // 3xxx-x
		if (entidadPattern.matcher(String.valueOf(entidad)).matches() && BigDecimal.ZERO.equals(subEntidad)) {
			parametros.put(SHOW_ENT_SUBENT_MED, Boolean.FALSE);
		} else {
			parametros.put(SHOW_ENT_SUBENT_MED, Boolean.TRUE);
		}

		parametros.put("RUTA_IPID",
				ResourceBundle.getBundle("agp_informes_jasper").getString("informeJasper.ipid.ruta"));
		parametros.put("IMAGEN_DIR", pathInformes + "/jasper/declaracionSeguro/images/");
		parametros.put("codLinea", poliza.getLinea().getCodlinea().toString());
		/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Inicio */
		parametros.put("SUBREPORT_DIR", pathInformes + bundle.getString(PATH_SUBREPORT_CONTRATO));
		String barcode = poliza.getGedDocPoliza() != null ? poliza.getGedDocPoliza().getCodBarras() : "";
		parametros.put("P_BARCODE", barcode);
		parametros.put("P_BARCODE2", barcode);
		/* P0073325 - RQ.07, RQ.13, RQ.23 y RQ.24 Fin */

		// Compilamos todos los reports y los rellenamos
		for (String identificadorInforme : lstIdentificadoresInformes) {

			pathInformeFuente = pathInformes + identificadorInforme + JRXML;
			pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

			logger.debug(INICIO_DE_LA_COMPILACION_DEL_INFORME + identificadorInforme);
			JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
			logger.debug("Rellenamos el informe");
			JasperFillManager.fillReport(pathInformeCompilado, parametros, connection);
		}
		// Recuperamos el report principal y el report dinamico, le pasamos como
		// parametro el report dinamico al principal
		File masterFile = new File(
				pathInformes + bundle.getString("informeJasper.declaracionSeguroComplementario") + JASPER);
		File sub1File = new File(pathInformes
				+ bundle.getString(INFORME_JASPER_DECLARACION_SEGURO_COMPLEMENTARIO_TABLA_RESUMEN_COBERTURAS) + JASPER);

		JasperReport subReport = (JasperReport) JRLoader.loadObject(sub1File);
		JasperReport masterReport = (JasperReport) JRLoader.loadObject(masterFile);

		addGrupoNegocio(poliza.getDistribucionCoste2015s(), parametros);

		parametros.put("ID", poliza.getIdpoliza().intValue());
		parametros.put("datasource", source);
		parametros.put("tablaSubreport", subReport);
		parametros.put("plan", poliza.getLinea().getCodplan().toString());
		parametros.put("linea", poliza.getLinea().getCodlinea() + " - " + poliza.getLinea().getNomlinea());
		parametros.put("esRenovable", new Character('S').equals(poliza.getRenovableSn()) ? Constants.ES_RENOVABLE
				: Constants.NO_ES_RENOVABLE);
		parametros.put("estado", poliza.getEstadoPoliza().getDescEstado());
		parametros.put("tipoPago", tipoPago);

		Locale locale = new Locale("es", "ES");
		parametros.put(JRParameter.REPORT_LOCALE, locale);
		parametros.put("CODENTIDAD", poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad());
		parametros.put("ENTMEDIADORA",
				poliza.getColectivo().getSubentidadMediadora().getEntidadMediadora().getCodentidad());
		parametros.put("pintarDC2015", poliza.isPlanMayorIgual2015());
		parametros.put("esGanado", false);

		jp = JasperFillManager.fillReport(masterReport, parametros, connection);

		return jp;
	}

	@SuppressWarnings("rawtypes")
	private static void addGrupoNegocio(final Set<DistribucionCoste2015> distribucionCoste2015s,
			final HashMap<String, Object> parametros) {

		List<String> c = new ArrayList<String>();
		Iterator<DistribucionCoste2015> iter = distribucionCoste2015s.iterator();
		int i = 1;
		while (iter.hasNext()) {
			DistribucionCoste2015 dc = (DistribucionCoste2015) iter.next();
			c.add(dc.getGrupoNegocio().toString());
			i++;
		}
		Collections.sort(c);
		int j = 1;
		Iterator iter2 = c.iterator();
		while (iter2.hasNext()) {
			String gn = (String) iter2.next();
			parametros.put("grupoNegocio" + j, new BigDecimal(gn));
			j++;
		}

		if (i == 1) {
			parametros.put("grupoNegocio2", new BigDecimal(0));
		}
	}

	private static Boolean exentoCuentaCobroSiniestros(final BigDecimal codLinea, final String codModulo) {
		boolean exentoCuentaCobroSiniestros = Boolean.FALSE;
		String[] auxArr = bundle_siniestros.getString("mods.exentos.envio").split(",");
		String auxStr = codLinea.toString() + "|" + codModulo;
		logger.debug("Verificando si la poliza esta exenta envio siniestros: " + auxStr);
		for (String aux : auxArr) {
			logger.debug("Verificando contra linea exenta envio siniestros: " + aux);
			if (auxStr.toUpperCase().equals(aux.trim().toUpperCase())) {
				logger.debug("Poliza exenta!!!!");
				exentoCuentaCobroSiniestros = Boolean.TRUE;
				break;
			}
		}
		return exentoCuentaCobroSiniestros;
	}

	public static List<FirmaXmpBaseBean> getFirmasDocPoliza(final Asegurado asegurado, final boolean hasNotaInformativa,
			final String entidadAltaPersona, final String idPersonaInterno, final int numPagParcelas, boolean aseguradoVulnerable) {
		logger.debug("getFirmasDocPoliza [INIT]");
		int pagFirma = (hasNotaInformativa ? 8 : 7) + numPagParcelas;
		
		if (aseguradoVulnerable) {
			pagFirma += 3;
		}
		
		FirmaXmpBaseBean xmpBaseBean1 = new FirmaXmpBaseBean();
		xmpBaseBean1.setPosicionX(240);
		xmpBaseBean1.setPosicionY(-100);
		xmpBaseBean1.setPosicionYV2(Float.valueOf("550.0"));
		xmpBaseBean1.setPagina(pagFirma);
		xmpBaseBean1.setAlto(140);
		xmpBaseBean1.setAncho(220);
		xmpBaseBean1.setNombre(asegurado.getNombreCompleto());
		xmpBaseBean1.setNif(asegurado.getNifcif());
		xmpBaseBean1.setCodigoPersona("CIF".equals(asegurado.getTipoidentificacion()) ? "J" : "F");
		xmpBaseBean1.setEntidadAltaPersona(entidadAltaPersona);
		xmpBaseBean1.setCodRlPersPe("01");
		xmpBaseBean1.setNumRlOrden("01");
		xmpBaseBean1.setCodTpDe("01");
		xmpBaseBean1.setIdPersonaInterno(idPersonaInterno);
		List<FirmaXmpBaseBean> firmas = new ArrayList<FirmaXmpBaseBean>(
				Arrays.asList(new FirmaXmpBaseBean[] { xmpBaseBean1 }));
		logger.debug("getFirmasDocPoliza [END]");
		return firmas;
	}

	public static List<FirmaXmpBaseBean> getFirmasDocPolizaComp(final Asegurado asegurado,
			final boolean hasNotaInformativa, final String entidadAltaPersona, final String idPersonaInterno,
			final int numPagParcelas, boolean aseguradoVulnerable) {
		logger.debug("getFirmasDocPolizaComp [INIT]");
		int pagFirma = (hasNotaInformativa ? 8 : 7) + numPagParcelas;
		
		if (aseguradoVulnerable) {
			pagFirma += 3;
		}
		
		FirmaXmpBaseBean xmpBaseBean1 = new FirmaXmpBaseBean();
		xmpBaseBean1.setPosicionX(240);
		xmpBaseBean1.setPosicionY(-100);
		xmpBaseBean1.setPosicionYV2(Float.valueOf("550.0"));
		xmpBaseBean1.setPagina(pagFirma);
		xmpBaseBean1.setAlto(140);
		xmpBaseBean1.setAncho(220);
		xmpBaseBean1.setNombre(asegurado.getNombreCompleto());
		xmpBaseBean1.setNif(asegurado.getNifcif());
		xmpBaseBean1.setCodigoPersona("CIF".equals(asegurado.getTipoidentificacion()) ? "J" : "F");
		xmpBaseBean1.setEntidadAltaPersona(entidadAltaPersona);
		xmpBaseBean1.setCodRlPersPe("01");
		xmpBaseBean1.setNumRlOrden("01");
		xmpBaseBean1.setCodTpDe("01");
		xmpBaseBean1.setIdPersonaInterno(idPersonaInterno);
		List<FirmaXmpBaseBean> firmas = new ArrayList<FirmaXmpBaseBean>(
				Arrays.asList(new FirmaXmpBaseBean[] { xmpBaseBean1 }));
		logger.debug("getFirmasDocPolizaComp [END]");
		return firmas;
	}

	public static List<FirmaXmpBaseBean> getFirmasDocPolizaGan(final Asegurado asegurado,
			final boolean hasNotaInformativa, final String entidadAltaPersona, final String idPersonaInterno,
			final int numPagExplotaciones) {
		logger.debug("getFirmasDocPolizaGan [INIT]");
		int pagFirma = (hasNotaInformativa ? 8 : 7) + numPagExplotaciones;
		FirmaXmpBaseBean xmpBaseBean1 = new FirmaXmpBaseBean();
		xmpBaseBean1.setPosicionX(240);
		xmpBaseBean1.setPosicionY(-100);
		xmpBaseBean1.setPosicionYV2(Float.valueOf("550.0"));
		xmpBaseBean1.setPagina(pagFirma);
		xmpBaseBean1.setAlto(140);
		xmpBaseBean1.setAncho(220);
		xmpBaseBean1.setNombre(asegurado.getNombreCompleto());
		xmpBaseBean1.setNif(asegurado.getNifcif());
		xmpBaseBean1.setCodigoPersona("CIF".equals(asegurado.getTipoidentificacion()) ? "J" : "F");
		xmpBaseBean1.setEntidadAltaPersona(entidadAltaPersona);
		xmpBaseBean1.setCodRlPersPe("01");
		xmpBaseBean1.setNumRlOrden("01");
		xmpBaseBean1.setCodTpDe("01");
		xmpBaseBean1.setIdPersonaInterno(idPersonaInterno);
		List<FirmaXmpBaseBean> firmas = new ArrayList<FirmaXmpBaseBean>(
				Arrays.asList(new FirmaXmpBaseBean[] { xmpBaseBean1 }));
		logger.debug("getFirmasDocPolizaGan [END]");
		return firmas;
	}
	
	public static int getNumPagsParcelas(final Set<Parcela> parcelas) {
		logger.debug("JRUtils - getNumPagsParcelas INIT");
		
		HashMap<Integer, Integer> mapaHojas = new HashMap<>();
		
		
		int total = 0;
		
		for (Parcela p : parcelas) {
			if (mapaHojas.containsKey(p.getHoja())) {
				Integer valor = mapaHojas.get(p.getHoja()) + 1;
				mapaHojas.remove(p.getHoja());
				mapaHojas.put(p.getHoja(), valor);
				logger.debug("hoja:" + p.getHoja());
				logger.debug("valor: " + valor);
			}
			else {
				mapaHojas.put(p.getHoja(), 1);
			}
		}
		
		
		
		for (Integer i : mapaHojas.values()) {
			total += (int) Math.ceil((float) i / 7);
		}
		
		logger.debug("TOTAL: " + total);
		logger.debug("JRUtils - getNumPagsParcelas INIT");
		
		return total;

	}
	
	public static int getNumPagsParcelasCompl(final Set<Parcela> parcelas) {
		//return getNumPagsObjAseg(parcelas, false);
		logger.debug("JRUtils - getNumPagsParcelas INIT");
		
		HashMap<Integer, Integer> mapaHojas = new HashMap<>();
		
		
		int total = 0;
		
		for (Parcela p : parcelas) {
			if (Constants.CHARACTER_S.equals(p.getAltaencomplementario())) {
				if (mapaHojas.containsKey(p.getHoja())) {
					Integer valor = mapaHojas.get(p.getHoja()) + 1;
					mapaHojas.remove(p.getHoja());
					mapaHojas.put(p.getHoja(), valor);
					logger.debug("hoja:" + p.getHoja());
					logger.debug("valor: " + valor);
				}
				else {
					mapaHojas.put(p.getHoja(), 1);
				}
			}
		}
		
		
		
		for (Integer i : mapaHojas.values()) {
			total += (int) Math.ceil((float) i / 7);
		}
		
		logger.debug("TOTAL: " + total);
		logger.debug("JRUtils - getNumPagsParcelas INIT");
		
		return total;
	}
	
	public static int getNumPagsExplotaciones(final Set<Explotacion> explotaciones) {
		return getNumPagsObjAseg(explotaciones, true);
	}
	
	private static int getNumPagsObjAseg(final Set<?> objAsegSet, final boolean esGanado) {
		int numero = 0;
		for (Object obj : objAsegSet) {
			if (obj != null) {
				numero++;
			}
		}
		return (int) Math.ceil((float) numero / (esGanado ? 2 : 7));
	}
}