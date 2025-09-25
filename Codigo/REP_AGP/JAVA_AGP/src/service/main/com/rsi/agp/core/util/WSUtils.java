package com.rsi.agp.core.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.xml.sax.InputSource;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.RegistrarColectivoException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.exception.ValidacionServiceException;
import com.rsi.agp.core.exception.ValidacionSiniestroException;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.security.SecurityHandler;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.ws.ResultadoWS;
import com.rsi.agp.dao.filters.commons.ErrorWsFiltro;
import com.rsi.agp.dao.filters.cpl.ReduccionRendimientosAmbitosFiltro;
import com.rsi.agp.dao.filters.poliza.EnvioAgroseguroFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.acuseRecibo.Error;
import es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable;
import es.agroseguro.seguroAgrario.modificacion.PolizaDocument;
import es.agroseguro.seguroAgrario.siniestros.SiniestroDocument;
import es.agroseguro.serviciosweb.contratacionscrendimientos.CalcularRendimientosRequest;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.FinanciarRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.Fraccionamiento;
import es.agroseguro.tipos.AjustarProducciones;

/**
 * Utilidades para los servicios Web
 * 
 * @author T-Systems
 */
public class WSUtils {

	private static final String TD = "</td>";
	private static final String TD_CLASS_LITERALBORDE_STYLE_WIDTH = "<td class='literalborde' style='width:";
	private static final String CODIGO = "Codigo: ";
	private static final String MENSAJE = " - Mensaje: ";
	private static final String STRING = "-------------------------------------- ";
	private static final String ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB = "Errores devueltos por el Servicio Web: ";
	private static final String ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN = "Error al convertir el XML a XML Bean";
	private static final String HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO = "http://www.agroseguro.es/SeguroAgrario/CalculoSeguroAgrario";
	private static final String TIEMPO_DE_GENERACION_DEL_XML = "Tiempo de generacion del xml: ";
	private static final String MILISEGUNDOS = " milisegundos";
	private static final String XML_GENERADO = "XML generado: ";
	private static final String XML_FRAGMENT2 = "</xml-fragment>";
	private static final String PKS_POLIZA = "</pks:Poliza>";
	private static final String PKS_POLIZA_XMLNS_PKS = "<pks:Poliza xmlns:pks=\"";
	private static final String XML_FRAGMENT = "<xml-fragment";
	private static final String XML_VERSION_1_0_ENCODING_UTF_8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	// Fichero de Propiedades
	private static final ResourceBundle bundle = ResourceBundle.getBundle("webservices");
	
	// Logger
	private static final Log logger = LogFactory.getLog(WSUtils.class);
	
	// Indica si ya se establecio el proxy
	private static boolean proxyFixed = false;

	/**
	 * Metodo para anadir la cabecera de seguridad al "port" de acceso a los
	 * servicios web
	 * 
	 * @param servicio
	 *            Servicio para el que es necesaria la cabecera
	 */
	@SuppressWarnings("rawtypes")
	public static void addSecurityHeader(Object servicio) {
		List<Handler> securityHandlerChain = new ArrayList<Handler>();
		securityHandlerChain.add(new SecurityHandler());
		((BindingProvider) servicio).getBinding().setHandlerChain(securityHandlerChain);
	}

	/**
	 * Metodo para establecer los valores de configuracion del proxy (si es
	 * necesario) Recupera los valores del fichero webservices.properties
	 */
	public static void setProxy() {

		if ("true".equals(bundle.getString("proxy.on"))) {
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			
			System.setProperty("https.proxyHost", bundle.getString("proxy.host"));
			System.setProperty("https.proxyPort", bundle.getString("proxy.port"));
			System.setProperty("http.proxyHost", bundle.getString("proxy.host"));
			System.setProperty("http.proxyPort", bundle.getString("proxy.port"));
			if (!"".equals(StringUtils.nullToString(bundle.getString("proxy.user")))) {
				Authenticator.setDefault(new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(bundle.getString("proxy.user"),
								bundle.getString("proxy.password").toCharArray());
					}
				});
			}
			WSUtils.setProxyFixed(true);
		}
	}

	/**
	 * Comprueba si un String es numerico
	 * 
	 * @param i
	 * @return
	 */
	public static boolean isParsableToInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * Convierte un Clob a String
	 * 
	 * @param clobInData
	 * @return
	 */
	public static String convertClob2String(java.sql.Clob clobInData) {
		String stringClob = null;
		try {
			long i = 1;
			int clobLength = (int) clobInData.length();
			stringClob = clobInData.getSubString(i, clobLength);
		} catch (Exception e) {
			logger.error("Error convirtiendo objeto Clob a String", e);
		}
		return stringClob;
	}

	public static String generateXMLPoliza(final Poliza poliza, final ComparativaPoliza cp,
			final String webServiceToCall, final IPolizaDao polizaDao, final List<BigDecimal> listaCPM,
			final Usuario usuario, final boolean aplicaDtoRec, final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws ValidacionPolizaException, DAOException, BusinessException {

		return generateXMLPoliza(poliza, cp, webServiceToCall, polizaDao, listaCPM, usuario, false, null, aplicaDtoRec,
				comsPctCalculado);
	}

	public static String generateXMLPoliza(final Poliza poliza, final ComparativaPoliza cp,
			final String webServiceToCall, final IPolizaDao polizaDao, final List<BigDecimal> listaCPM,
			final Usuario usuario, final ImporteFraccionamiento ifr,
			final com.rsi.agp.dao.tables.poliza.PagoPoliza ppol, final boolean aplicaDtoRec,
			final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws ValidacionPolizaException, DAOException, BusinessException {

		return generateXMLPoliza(poliza, cp, webServiceToCall, polizaDao, listaCPM, usuario, ifr, ppol, false, null,
				aplicaDtoRec, comsPctCalculado);
	}

	/**
	 * Genera el XML de una poliza
	 * 
	 * @param poliza
	 *            Poliza
	 * @param cp
	 *            Comparativa
	 * @param webServiceToCall
	 *            Servicio web para el que queremos el XML
	 * @return XML de envio.
	 * @throws DAOException
	 * @throws BusinessException
	 */
	public static String generateXMLPoliza(final Poliza poliza, final ComparativaPoliza cp, final String webServiceToCall,
			final IPolizaDao polizaDao, final List<BigDecimal> listaCPM, final Usuario usuario, final boolean esEnvioWSRecalculo,
			final Set<Long> colIdParcelasFiltro, final boolean aplicaDtoRec, final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws ValidacionPolizaException, DAOException, BusinessException {

		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace;
		// DAA 19/06/13
		if (webServiceToCall.equals(Constants.WS_VALIDACION) || webServiceToCall.equals(Constants.WS_PASAR_DEFINITIVA)
				|| webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
			// namespace = "http://www.agroseguro.es/SeguroAgrario/Contratacion";
			namespace = "http://www.agroseguro.es/Contratacion";
		} else if (webServiceToCall.equals(Constants.WS_CARACT_EXPLOTACION)) {
			// namespace = "http://www.agroseguro.es/SeguroAgrario/CalculoSeguroAgrario";
			namespace = "http://www.agroseguro.es/PresupuestoContratacion";
		} else {
			namespace = "http://www.agroseguro.es/PresupuestoContratacion";
		}
		GregorianCalendar fechaI = new GregorianCalendar();

		// TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		// Parcela Datos Variables
		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
		codsConceptos = polizaDao.getCodsConceptoOrganizador(poliza.getLinea().getLineaseguroid());

		// 1. Consulta de los datos necesarios para el calculo de los datos
		// variables que dependen del concepto principal
		// del modulo y, en caso de ser la linea 301, calculamos tambien las
		// coberturas de parcela.
		
		Map<BigDecimal, List<String>> lstDatVar =  polizaDao.getDatosVariablesParcelaRiesgoJavaImpl(poliza, cp,
			webServiceToCall);

		// 2. Comprobar si para lineaseguroid y codmodulo existen codigos de
		// reduccion de rendimiento
		ReduccionRendimientosAmbitosFiltro filtro = new ReduccionRendimientosAmbitosFiltro();
		filtro.setLineaSeguroId(cp.getId().getLineaseguroid());
		filtro.setCodmodulo(cp.getId().getCodmodulo());

		boolean aplicaReduccionRdto = polizaDao.getNumObjects(filtro).intValue() > 0;

		// 3. Transformacion de la poliza de BD para llamar al servicio web
		String xmlPoliza = null;

		if (esEnvioWSRecalculo) {// Es una version simple para el calculo de
									// rendimientos

			logger.debug("listaCPM -> " + listaCPM);
			xmlPoliza = PolizaUnificadaTransformer.transformarParaEnvioWSRecalculo(poliza, cp, lstDatVar,
					aplicaReduccionRdto, listaCPM, codsConceptos, usuario, colIdParcelasFiltro, webServiceToCall).toString();

		} else {

			Map<Long, DatosVariables> mapDvEspecialesExplotacion = new HashMap<Long, DatosVariables>();
			List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();
			Boolean esGanado = false;

			if (poliza.getLinea().isLineaGanado()) {

				esGanado = true;

				gruposNegocio = polizaDao.getGruposNegocio(poliza.getIdpoliza());

				for (Explotacion explot : poliza.getExplotacions()) {
					DatosVariables dvEsp = getDatosVariablesEspecialesExplotacion(explot.getId(), polizaDao);
					if (dvEsp != null)
						mapDvEspecialesExplotacion.put(explot.getId(), dvEsp);
				}
			}

			xmlPoliza = PolizaUnificadaTransformer.transformar(poliza, null, cp, lstDatVar, aplicaReduccionRdto,
					listaCPM, codsConceptos, polizaDao, gruposNegocio, mapDvEspecialesExplotacion, esGanado,
					webServiceToCall, aplicaDtoRec, comsPctCalculado).toString();

		}

		String envio = cabecera + xmlPoliza.replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
				.replace(XML_FRAGMENT2, PKS_POLIZA);

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		return envio;
	}

	private static String generateXMLPoliza(final Poliza poliza, final ComparativaPoliza cp,
			final String webServiceToCall, final IPolizaDao polizaDao, final List<BigDecimal> listaCPM,
			final Usuario usuario, final ImporteFraccionamiento ifr,
			final com.rsi.agp.dao.tables.poliza.PagoPoliza ppol, final boolean esEnvioWSRecalculo,
			final Set<Long> colIdParcelasFiltro, final boolean aplicaDtoRec,
			final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws ValidacionPolizaException, DAOException, BusinessException {

		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace;
		// DAA 19/06/13
		if (webServiceToCall.equals(Constants.WS_VALIDACION) || webServiceToCall.equals(Constants.WS_PASAR_DEFINITIVA)
				|| webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
			namespace = "http://www.agroseguro.es/SeguroAgrario/Contratacion";
		} else if (webServiceToCall.equals(Constants.WS_CARACT_EXPLOTACION)) {
			namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;
		} else {
			namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;
		}
		GregorianCalendar fechaI = new GregorianCalendar();

		// TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		// Parcela Datos Variables
		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
		codsConceptos = polizaDao.getCodsConceptoOrganizador(poliza.getLinea().getLineaseguroid());

		// 1. Consulta de los datos necesarios para el calculo de los datos
		// variables que dependen del concepto principal
		// del modulo y, en caso de ser la linea 301, calculamos tambien las
		// coberturas de parcela.

		/* MODIF TAM (23.11.2020) * Resolucion de Incidencias RGA Pet. 63485- Fase II */
		 Map<BigDecimal, List<String>> lstDatVar = polizaDao.getDatosVariablesParcelaRiesgoJavaImpl(poliza, cp,
				webServiceToCall);

		// 2. Comprobar si para lineaseguroid y codmodulo existen codigos de
		// reduccion de rendimiento
		ReduccionRendimientosAmbitosFiltro filtro = new ReduccionRendimientosAmbitosFiltro();
		filtro.setLineaSeguroId(cp.getId().getLineaseguroid());
		filtro.setCodmodulo(cp.getId().getCodmodulo());

		boolean aplicaReduccionRdto = polizaDao.getNumObjects(filtro).intValue() > 0;

		// 3. Transformacion de la poliza de BD para llamar al servicio web
		String xmlPoliza = null;

		if (esEnvioWSRecalculo) {// Es una version simple para el calculo de
									// rendimientos
			/* Pet. 57626 ** MODIF TAM (07/05/2020) ** Inicio */
			/*
			 * xmlPoliza = PolizaTransformer.transformarParaEnvioWSRecalculo( poliza, cp,
			 * lstDatVar, lstCoberturas, aplicaReduccionRdto, listaCPM, codsConceptos,
			 * usuario, colIdParcelasFiltro) .toString();
			 */

			xmlPoliza = PolizaUnificadaTransformer.transformarParaEnvioWSRecalculo(poliza, cp, lstDatVar,
					aplicaReduccionRdto, listaCPM, codsConceptos, usuario, colIdParcelasFiltro, webServiceToCall).toString();
			/* Pet. 57626 ** MODIF TAM (07/05/2020) ** Fin */

		} else {
			Map<Long, DatosVariables> mapDvEspecialesExplotacion = new HashMap<Long, DatosVariables>();
			List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();

			Boolean esGanado = false;

			if (poliza.getLinea().isLineaGanado()) {
				
				gruposNegocio = polizaDao.getGruposNegocio(poliza.getIdpoliza());

				List<Explotacion>  explotaciones = polizaDao.getExplotacionesPoliza(poliza.getIdpoliza());
				for (Explotacion explot : explotaciones) {
					DatosVariables dvEsp = getDatosVariablesEspecialesExplotacion(explot.getId(), polizaDao);
					if (dvEsp != null)
						mapDvEspecialesExplotacion.put(explot.getId(), dvEsp);
				}
				esGanado = true;
			} else {
				esGanado = false;
			}

			xmlPoliza = PolizaUnificadaTransformer.transformar(poliza, null, cp, lstDatVar, aplicaReduccionRdto,
					listaCPM, codsConceptos, polizaDao, gruposNegocio, mapDvEspecialesExplotacion, esGanado,
					webServiceToCall, aplicaDtoRec, comsPctCalculado).toString();
		}
		String envio = cabecera + xmlPoliza.replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
				.replace(XML_FRAGMENT2, PKS_POLIZA);

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		return envio;
	}

	/**
	 * Genera el XML de una poliza complementaria
	 * 
	 * @param poliza
	 * @param webServiceToCall
	 * @param polizaDao
	 * @return
	 * @throws Exception
	 */
	public static String generateXMLPolizaCpl(final Poliza poliza, final Poliza polizaPpl, final ComparativaPoliza cp,
			final String webServiceToCall, final IPolizaDao polizaDao, final List<BigDecimal> listaCPM,
			final Usuario usuario, final Boolean saeca, final boolean aplicaDtoRec,
			final Map<Character, ComsPctCalculado> comsPctCalculado, final String realPath)
			throws ValidacionPolizaException, DAOException, BusinessException {

		logger.debug("WSUtils - generateXMLPolizaCpl");

		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace = "";
		String envio = "";

		String xmlPolizaCpl = null;

		// TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		// Parcela Datos Variables
		List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();

		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();

		codsConceptos = polizaDao.getCodsConceptoOrganizador(poliza.getLinea().getLineaseguroid());		

		// 1. Consulta de los datos necesarios para el calculo de los datos
		// variables que dependen del concepto principal
		// del modulo y, en caso de ser la linea 301, calculamos tambien las
		// coberturas de parcela.
		Map<BigDecimal, List<String>> lstDatVar = polizaDao.getDatosVariablesParcelaRiesgoCPL(poliza,
				polizaPpl.getCodmodulo());
		Map<Long, DatosVariables> mapDvEspecialesExplotacion = new HashMap<Long, DatosVariables>();
		
		// 2. Comprobar si para lineaseguroid y codmodulo existen codigos de
		// reduccion de rendimiento
		ReduccionRendimientosAmbitosFiltro filtro = new ReduccionRendimientosAmbitosFiltro();
		filtro.setLineaSeguroId(cp.getId().getLineaseguroid());
		filtro.setCodmodulo(cp.getId().getCodmodulo());

		boolean aplicaReduccionRdto = polizaDao.getNumObjects(filtro).intValue() > 0;

		boolean esGanado = false;

		// DAA 19/06/13
		if (webServiceToCall.equals(Constants.WS_VALIDACION) || webServiceToCall.equals(Constants.WS_PASAR_DEFINITIVA)
				|| webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
			namespace = "http://www.agroseguro.es/SeguroAgrario/Contratacion/Complementario";
		} else if (webServiceToCall.equals(Constants.WS_CARACT_EXPLOTACION)) {
			namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;
		} else {
			namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;
		}
		GregorianCalendar fechaI = new GregorianCalendar();

		// Transformacion de la poliza de BD para llamar al servicio web
		if (webServiceToCall.equals(Constants.WS_VALIDACION) || webServiceToCall.equals(Constants.WS_PASAR_DEFINITIVA)
				|| webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
			
			xmlPolizaCpl = PolizaUnificadaTransformer.transformarCplValidar(poliza, cp, lstDatVar, aplicaReduccionRdto,
					listaCPM, codsConceptos, polizaDao, gruposNegocio, mapDvEspecialesExplotacion, esGanado,
					webServiceToCall, aplicaDtoRec, comsPctCalculado).toString();

			envio = cabecera + xmlPolizaCpl.replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
					.replace(XML_FRAGMENT2, PKS_POLIZA);
		} else {		

			xmlPolizaCpl = PolizaUnificadaTransformer.transformar(poliza, polizaPpl, cp, lstDatVar, aplicaReduccionRdto,
					listaCPM, codsConceptos, polizaDao, gruposNegocio, mapDvEspecialesExplotacion, esGanado,
					webServiceToCall, aplicaDtoRec, comsPctCalculado).toString();

			envio = cabecera + xmlPolizaCpl.replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
					.replace(XML_FRAGMENT2, PKS_POLIZA);
			
			if (webServiceToCall.equals(Constants.WS_CALCULO)
					&& Constants.ESTADO_POLIZA_DEFINITIVA.equals(polizaPpl.getEstadoPoliza().getIdestado())) {
				//P0076499 - FORZAMOS EL VALOR DE CIERTOS ELEMENTOS
				try {
					PolizaActualizadaResponse respuesta = new SWAnexoModificacionHelper().getPolizaActualizada(
							polizaPpl.getReferencia(), polizaPpl.getLinea().getCodplan(), realPath);
					es.agroseguro.contratacion.PolizaDocument sitAct = respuesta.getPolizaPrincipalUnif();
					if (sitAct != null) {
						es.agroseguro.contratacion.PolizaDocument polDoc = es.agroseguro.contratacion.PolizaDocument.Factory
								.parse(envio);
						DatosVariables datosVariables = sitAct.getPoliza().getCobertura().getDatosVariables();
						if (datosVariables != null) {
							polDoc.getPoliza().getCobertura().setDatosVariables(datosVariables);
						}
						es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvencionesDeclaradas = sitAct
								.getPoliza().getSubvencionesDeclaradas();
						if (subvencionesDeclaradas != null) {
							polDoc.getPoliza().setSubvencionesDeclaradas(subvencionesDeclaradas);
						}
						es.agroseguro.contratacion.declaracionSubvenciones.RelacionSocios relacionSocios = sitAct
								.getPoliza().getRelacionSocios();
						if (relacionSocios != null) {
							polDoc.getPoliza().setRelacionSocios(relacionSocios);
						}
						es.agroseguro.seguroAgrario.contratacion.op.relacionSocios.RelacionSociosOP relacionSociosOP = sitAct
								.getPoliza().getRelacionSociosOP();
						if (relacionSociosOP != null) {
							polDoc.getPoliza().setRelacionSociosOP(relacionSociosOP);
						}
						envio = polDoc.toString();
					}
				} catch (Exception e) {
					logger.error("Error al obtener la situacion actualizada", e);
				}				
			}
		}

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		return envio;
	}

	/**
	 * Devuelve una cadena en formato XML de la request que se hace al WS de
	 * Zonificacion SIGPAC
	 * 
	 * @param sigpacRequest
	 * @return
	 */
	public static String generateXMLLlamadaZonificacionSIGPAC(SIGPACRequest sigpacRequest) {

		StringBuilder sb = new StringBuilder();
		sb.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosWeb/ContratacionSCSIGPACZonificacion/\">\n");
		sb.append("<soapenv:Body>\n");
		sb.append("\t<con:SIGPAC_Request>\n");
		sb.append("\t\t<con:provinciaSIGPAC>").append(sigpacRequest.getProvinciaSIGPAC())
				.append("</con:provinciaSIGPAC>\n");
		sb.append("\t\t<con:terminoSIGPAC>").append(sigpacRequest.getTerminoSIGPAC())
				.append("</con:terminoSIGPAC>\n");
		sb.append("\t\t<con:agregadoSIGPAC>").append(sigpacRequest.getAgregadoSIGPAC())
				.append("</con:agregadoSIGPAC>\n");
		sb.append("\t\t<con:zonaSIGPAC>").append(sigpacRequest.getZonaSIGPAC()).append("</con:zonaSIGPAC>\n");
		sb.append("\t\t<con:poligonoSIGPAC>").append(sigpacRequest.getPoligonoSIGPAC())
				.append("</con:poligonoSIGPAC>\n");
		sb.append("\t\t<con:parcelaSIGPAC>").append(sigpacRequest.getParcelaSIGPAC())
				.append("</con:parcelaSIGPAC>\n");
		sb.append("\t\t<con:plan>").append(sigpacRequest.getPlan().getValue()).append("</con:plan>\n");
		sb.append("\t\t<con:linea>").append(sigpacRequest.getLinea().getValue()).append("</con:linea>\n");
		sb.append("\t\t<con:cultivo>").append(sigpacRequest.getCultivo().getValue()).append("</con:cultivo>\n");
		sb.append("\t</con:SIGPAC_Request>\n");
		sb.append("</soapenv:Body>\n");
		sb.append("</soapenv:Envelope>\n");
		return sb.toString();
	}

	public static String generateXMLLlamadaCalculoRendimientos(CalcularRendimientosRequest rendimientosRequest,
			String xmlPoliza) {

		StringBuilder sb = new StringBuilder();

		sb.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosWeb/ContratacionSCRendimientos/\" xmlns:xm=\"http://www.w3.org/2005/05/xmlmime\">\n");
		sb.append("<soapenv:Body>\n");
		sb.append("\t<con:calcularRendimientosRequest>\n");
		sb.append("\t\t<con:poliza xm:contentType=\"application/?\">\n\n");

		sb.append(xmlPoliza);

		sb.append("\n");
		sb.append("\t\t</con:poliza>\n\n");

		if (rendimientosRequest.isAjuste()) {
			sb.append("\t\t<con:ajuste>true</con:ajuste>\n");
			JAXBElement<AjustarProducciones> jaxElement = rendimientosRequest.getAjustarProducciones();
			String valor = jaxElement.getValue().toString();
			sb.append("\t\t<con:ajustarProducciones>").append(valor).append("</con:ajustarProducciones>\n");
		} else {
			sb.append("\t\t<con:ajuste>false</con:ajuste>\n");
		}

		sb.append("\t</con:calcularRendimientosRequest>\n");
		sb.append("</soapenv:Body>\n");
		sb.append("</soapenv:Envelope>\n");

		return sb.toString();
	}

	public static String generateXMLLlamadaFinanciar(FinanciarRequest financiarRequest) {

		StringBuilder sb = new StringBuilder();

		sb.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosWeb/ContratacionSCUtilidades/\">\n");

		sb.append("<soapenv:Body>\n");
		sb.append("\t<con:financiarRequest>\n");
		sb.append("\t\t<con:costeTomador>").append(financiarRequest.getCosteTomador())
				.append("</con:costeTomador>\n");

		sb.append("\t\t<con:plan>").append(financiarRequest.getPlan()).append("</con:plan>\n");
		sb.append("\t\t<con:linea>").append(financiarRequest.getLinea()).append("</con:linea>\n");

		if (financiarRequest.getModulo() != null) {
			JAXBElement<String> jaxElementModulo = financiarRequest.getModulo();
			sb.append("\t\t<con:modulo>").append(jaxElementModulo.getValue()).append("</con:modulo>\n");
		}

		if (financiarRequest.getPeriodo() != null) {
			JAXBElement<Integer> jaxElementPeriodo = financiarRequest.getPeriodo();
			sb.append("\t\t<con:periodo>").append(jaxElementPeriodo.getValue()).append("</con:periodo>\n");
		}

		if (financiarRequest.getFraccionamiento1() != null) {
			JAXBElement<Fraccionamiento> jaxElementFracc = financiarRequest.getFraccionamiento1();
			Fraccionamiento fracc = jaxElementFracc.getValue();
			sb.append("\t\t<con:Fraccionamiento1>\n");

			if (fracc.getImporte() != null) {
				sb.append("\t\t<con:importe>").append(fracc.getImporte()).append("</con:importe>\n");
			}

			if (fracc.getPctCosteTomador() != null) {
				sb.append("\t\t<con:pctCosteTomador>").append(fracc.getPctCosteTomador())
						.append("</con:pctCosteTomador>\n");
			}
			sb.append("\t\t</con:Fraccionamiento1>\n");
		}

		if (financiarRequest.getImporteAval() != null) {
			sb.append("\t\t<con:importeAval>").append(financiarRequest.getImporteAval().getValue())
					.append("</con:importeAval>\n");
		}

		sb.append("\t</con:financiarRequest>\n");
		sb.append("</soapenv:Body>\n");
		sb.append("</soapenv:Envelope>\n");

		return sb.toString();
	}

	/**
	 * Obtiene el XML de calculo o de la poliza, segun se indique, de la tabla
	 * TB_ENVIOS_AGROSEGURO
	 * 
	 * @param idPoliza
	 * @param codModulo
	 * @param filaComparativa
	 * @param polizaDao
	 * @param polizaOCalculo
	 *            (true - poliza, false - calculo)
	 * @return
	 */
	private static String obtenXML(Long idEnvio, IPolizaDao polizaDao, boolean polizaOCalculo) {
		if (polizaDao == null)
			return null;
		EnvioAgroseguroFiltro filtro = new EnvioAgroseguroFiltro();
		filtro.setIdEnvio(idEnvio);
		@SuppressWarnings("unchecked")
		List<EnvioAgroseguro> objetoEnvio = polizaDao.getObjects(filtro);
		logger.debug("Numero de elementos del listado de Envios a Agroseguro: " + objetoEnvio.size());
		if (objetoEnvio.size() > 0) {
			if (polizaOCalculo)
				return WSUtils.convertClob2String(objetoEnvio.get(0).getXml());
			else
				return WSUtils.convertClob2String(objetoEnvio.get(0).getCalculo());
		}

		return null;
	}

	/**
	 * Obtiene la poliza con el ID indicado
	 * 
	 * @param id
	 * @return Clob con el XML de la poliza
	 */
	public static String obtenXMLPoliza(Long idEnvio, IPolizaDao polizaDao) {
		return obtenXML(idEnvio, polizaDao, true);
	}

	/**
	 * 
	 * @param idPoliza
	 * @param codModulo
	 * @param tipoEnvio
	 * @param polizaDao
	 * @return
	 */
	public static String obtenXMLPolizaCpl(Long idEnvio, IPolizaDao polizaDao) {
		return obtenXML(idEnvio, polizaDao, true);
	}

	public static String obtenXMLCalculo(Long idEnvio, IPolizaDao polizaDao) {
		logger.debug("Obteniendo el xml de distribucion de costes con los siguientes parametros: " + "idEnvio="
				+ idEnvio.toString());
		// "idPoliza=" + idPoliza + ", codModulo=" + codModulo +
		// "filaComparativa=" + filaComparativa);
		return obtenXML(idEnvio, polizaDao, false);
	}

	/**
	 * Valida un String XML de Calculo
	 * 
	 * @param xml
	 * @return polizaDocument
	 * @throws CalculoServiceException
	 */
	public static es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument getXMLCalculo(String xml)
			throws CalculoServiceException {
		// Se valida el XML
		es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument polizaDocument = null;
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			polizaDocument = es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument.Factory.parse(xml);
		} catch (XmlException e1) {
			logger.error(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN, e1);
			throw new CalculoServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema CalculoSeguroAgrario.xsd ");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new CalculoServiceException(
					"XML invalido, no cumple el esquema CalculoSeguroAgrario.xsd: " + cadError);
		}
		return polizaDocument;
	}

	/**
	 * Valida el xml de calculo con el esquema unificado
	 * 
	 * @param xml
	 * @return
	 */
	public static es.agroseguro.presupuestoContratacion.PolizaDocument getXMLCalculoUnificado(String xml) {
		es.agroseguro.presupuestoContratacion.PolizaDocument polizaDocument = null;
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			polizaDocument = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory.parse(xml);
		} catch (XmlException e1) {
			logger.error("Error al convertir el XML en un objeto es.agroseguro.presupuestoContratacion.PolizaDocument",
					e1);
			throw new CalculoServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema PresupuestoContratacion.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new CalculoServiceException(
					"XML invalido, no cumple el esquema PresupuestoContratacion.xsd: " + cadError);
		}

		return polizaDocument;
	}

	/* Pet. 57626 ** MODIF TAM (13.05.2020) ** Inicio */

	/**
	 * Valida un String XML de Calculo
	 * 
	 * @param xml
	 * @return polizaDocument
	 * @throws CalculoServiceException
	 */
	public static es.agroseguro.contratacion.PolizaDocument getXMLValidar(String xml)
			throws CalculoServiceException {
		// Se valida el XML
		es.agroseguro.contratacion.PolizaDocument polizaDocument = null;

		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			polizaDocument = es.agroseguro.contratacion.PolizaDocument.Factory.parse(xml);
		} catch (XmlException e1) {
			logger.error(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN, e1);
			throw new CalculoServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error(" XML invalido, no cumple el esquema CalculoSeguroAgrario.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new CalculoServiceException(
					"XML invalido, no cumple el esquema CalculoSeguroAgrario.xsd: " + cadError);
		}
		return polizaDocument;
	}

	/* Pet. 57626 ** MODIF TAM (13.05.2020) ** Inicio */
	/*
	 * Se realiza la modificacion correspondientes para modificar la validacion
	 * del xml de calculo para las complementarias ya que ahora ya va todo con
	 * formato Unificado y se debera validar contra dicho esquema.
	 */
	/**
	 * Valida un String XML de Calculo de poliza complementaria
	 * 
	 * @param xml
	 * @return
	 * @throws CalculoServiceException
	 */
	/*
	 * public static es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument
	 * getXMLCalculoCpl(
	 */
	public static es.agroseguro.contratacion.PolizaDocument getXMLCalculoCpl(String xml)
			throws CalculoServiceException {

		// Se valida el XML
		/*
		 * es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument
		 * polizaDocument = null;
		 */
		es.agroseguro.contratacion.PolizaDocument polizaDocument = null;

		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			/*
			 * polizaDocument =
			 * es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument.Factory
			 * .parse(xml);
			 */
			polizaDocument = es.agroseguro.contratacion.PolizaDocument.Factory.parse(xml);
		} catch (XmlException e1) {
			throw new CalculoServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN, e1);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema CalculoSeguroAgrario.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new CalculoServiceException(
					"XML invalido, no cumple el esquema CalculoSeguroAgrario.xsd: " + cadError);
		}
		return polizaDocument;
	}

	/**
	 * Valida un String XML de DistribucionDeCoste
	 * 
	 * @param xml
	 * @return polizaDocument
	 * @throws CalculoServiceException
	 */
	public static es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument getXMLDistribCostes(
			String xml) throws CalculoServiceException {
		// Se valida el XML
		es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument polizaDocument = null;
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			polizaDocument = es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument.Factory
					.parse(xml);
		} catch (XmlException e1) {
			logger.error("Error al obtener el xml de la distribucion de costes", e1);
			throw new CalculoServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema DistribucionCostesSeguroAgrario.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new CalculoServiceException(
					"XML invalido, no cumple el esquema DistribucionCostesSeguroAgrario.xsd: " + cadError);
		}
		return polizaDocument;
	}

	public static es.agroseguro.distribucionCostesSeguro.PolizaDocument getXMLDistribCostesSeguro(String xml)
			throws CalculoServiceException {
		// Se valida el XML
		es.agroseguro.distribucionCostesSeguro.PolizaDocument polizaDocument = null;
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			polizaDocument = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory.parse(xml);
		} catch (XmlException e1) {
			logger.error("Error al obtener el xml de la distribucion de costes", e1);
			throw new CalculoServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema DistribucionCostesSeguro.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new CalculoServiceException(
					"XML invalido, no cumple el esquema DistribucionCostesSeguro.xsd: " + cadError);
		}
		return polizaDocument;
	}

	/**
	 * Valida un String XML de Poliza
	 * 
	 * @param xml
	 * @return polizaDocument
	 * @throws ValidacionServiceException
	 */
	public static XmlObject getXMLPoliza(final String xml) throws ValidacionServiceException {

		// Se valida el XML antes de llamar al Servicio Web
		XmlObject polizaDocument = null;
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			polizaDocument = XmlObject.Factory.parse(xml);
		} catch (XmlException e1) {
			logger.error("Error al obtener el xml de la poliza", e1);
			throw new ValidacionServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema ContratacionSeguroAgrario.xsd");
			StringBuffer cadError = new StringBuffer();
			for (int i = 0; i < validationErrors.size(); i++) {
				XmlError err = validationErrors.get(i);
				logger.error(">> " + err.getMessage());
				cadError.append(err.getMessage()).append(" ");
			}
			throw new ValidacionServiceException(
					"XML invalido, no cumple el esquema ContratacionSeguroAgrario.xsd: " + cadError.toString());
		}
		return polizaDocument;
	}

	/**
	 * DAA 08/05/12 Valida un String XML de Anexo Modificacion
	 * 
	 * @param xml
	 * @return anexoDocument
	 * @throws ValidacionAnexoMocificacionException
	 */
	public static PolizaDocument getXMLAnexoModificacion(String xml) throws ValidacionAnexoModificacionException {

		// Se valida el XML antes de llamar al Servicio Web
		es.agroseguro.seguroAgrario.modificacion.PolizaDocument anexoDocument = null;
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			anexoDocument = es.agroseguro.seguroAgrario.modificacion.PolizaDocument.Factory.parse(xml);

		} catch (XmlException e1) {
			throw new ValidacionAnexoModificacionException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN, e1);
		}

		boolean bValidation = anexoDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema ModificacionSeguroAgrario.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			StringBuffer cadError = new StringBuffer();
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError.append(err + err.getMessage());
			}

			throw new ValidacionAnexoModificacionException(cadError.toString());

		}
		return anexoDocument;
	}

	/**
	 * Valida un String XML de Poliza complementaria
	 * 
	 * @param xml
	 * @return
	 * @throws ValidacionServiceException
	 */
	// Pet. 57626 ** MODIF TAM (28/05/2020) ** Inicio //
	// Utilizamos el esquema del Formato Unificado //
	/*
	 * public static
	 * es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument
	 * getXMLPolizaCpl(
	 */
	public static es.agroseguro.contratacion.PolizaDocument getXMLPolizaCpl(String xml)
			throws ValidacionServiceException {
		// es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument
		// polizaDocument = null;
		es.agroseguro.contratacion.PolizaDocument polizaDocument = null;

		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			// polizaDocument =
			// es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument.Factory
			polizaDocument = es.agroseguro.contratacion.PolizaDocument.Factory.parse(xml);

		} catch (XmlException e1) {
			logger.error("Error al obtener el xml de la poliza", e1);
			throw new ValidacionServiceException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN);
		}

		boolean bValidation = polizaDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema Contratacion.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			String cadError = "";
			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError += err.getMessage() + "";
			}

			throw new ValidacionServiceException(
					"XML invalido, no cumple el esquema ContratacionComplementario.xsd: " + cadError);
		}
		return polizaDocument;

	}

	/**
	 * TMR 03/06/13 Valida un String XML de Siniestros
	 * 
	 * @param xml
	 * @return siniestroDocument
	 * @throws ValidacionSiniestroException
	 */
	public static SiniestroDocument getXMLSiniestros(String xml) throws ValidacionSiniestroException {

		// Se valida el XML antes de llamar al Servicio Web
		es.agroseguro.seguroAgrario.siniestros.SiniestroDocument siniestroDocument = null;

		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			siniestroDocument = es.agroseguro.seguroAgrario.siniestros.SiniestroDocument.Factory.parse(xml);

		} catch (XmlException e1) {
			throw new ValidacionSiniestroException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN, e1);
		}

		boolean bValidation = siniestroDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema DeclaracionSiniestros.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			StringBuffer cadError = new StringBuffer();

			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError.append(err.getMessage());

			}
			throw new ValidacionSiniestroException(cadError.toString());
		}
		return siniestroDocument;
	}

	/**
	 * 
	 * Devuelve la propiedad indicada del fichero de propiedades
	 * webservices.properties
	 * 
	 * @param String
	 *            La propiedad
	 */
	public static String getBundleProp(String theProp) {
		return bundle.getString(theProp);
	}

	/**
	 * Indica si el proxy esta establecido
	 * 
	 * @return the proxyFixed
	 */
	public static boolean isProxyFixed() {
		return WSUtils.proxyFixed;
	}

	/**
	 * Establece si el proxy ya se fijo
	 * 
	 * @param proxyFixed
	 *            the proxyFixed to set
	 */
	public static void setProxyFixed(boolean proxyFixed) {
		WSUtils.proxyFixed = proxyFixed;
	}

	/**
	 * Devuelve el valor indicado de un ParameterMap
	 * 
	 * @param parameterMap
	 * @param paramName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getParameterMapValue(Map parameterMap, String paramName) {
		Iterator iter = parameterMap.entrySet().iterator();
		String arrValues[] = null;
		while (iter.hasNext()) {
			Map.Entry n = (Map.Entry) iter.next();
			String key = n.getKey().toString();
			if (key.equals(paramName)) {
				arrValues = (String[]) n.getValue();
				// Se supone que tendra siempre un valor.
				// Si hay un parametro repetido, no funciona, devuelve solo el
				// primero!!!
				return arrValues[0];
			}
		}
		return null;
	}

	/**
	 * Realiza un Debug de la exception devuelta por el servicio Web
	 * 
	 * @param e
	 */
	public static String debugAgrException(Exception e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException confException = null;
		es.agroseguro.serviciosweb.contratacionscutilidades.AgrException utilException = null;
		es.agroseguro.serviciosweb.impresionscpoliza.AgrException imprException = null;
		es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException segException = null;
		es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException consultaContratacionExcepcion = null;
		es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException conRenException = null;
		es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException inDocException = null;

		if (e instanceof es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException) {
			confException = (es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException) e;
			mensaje += WSUtils.printException(confException);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionscutilidades.AgrException) {
			utilException = (es.agroseguro.serviciosweb.contratacionscutilidades.AgrException) e;
			mensaje += WSUtils.printException(utilException);
		}
		if (e instanceof es.agroseguro.serviciosweb.impresionscpoliza.AgrException) {
			imprException = (es.agroseguro.serviciosweb.impresionscpoliza.AgrException) e;
			mensaje += WSUtils.printException(imprException);
		}
		if (e instanceof es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException) {
			segException = (es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException) e;
			mensaje += WSUtils.printException(segException);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException) {
			consultaContratacionExcepcion = (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException) e;
			mensaje += WSUtils.printException(consultaContratacionExcepcion);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException) {
			es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException exc = (es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException) e;
			mensaje += WSUtils.printException(exc);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException) {
			es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException exc = (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException) e;
			mensaje += WSUtils.printException(exc);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionayudas.AgrException) {
			es.agroseguro.serviciosweb.contratacionayudas.AgrException exc = (es.agroseguro.serviciosweb.contratacionayudas.AgrException) e;
			mensaje += WSUtils.printException(exc);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException) {
			conRenException = (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException) e;
			mensaje += WSUtils.printException(conRenException);
		}
		if (e instanceof es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException) {
			inDocException = (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException) e;
			mensaje += printException(inDocException);
		}
		return mensaje;
	}

	private static String printException(
			es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException exc) {

		String mensaje = "";
		List<es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrFallo fallo = exc.getFaultInfo();
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);

			for (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error error : errores) {
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	private static String printException(es.agroseguro.serviciosweb.contratacionayudas.AgrException exc) {

		String mensaje = "";
		List<es.agroseguro.serviciosweb.contratacionayudas.Error> errores = null;

		es.agroseguro.serviciosweb.contratacionayudas.AgrFallo fallo = exc.getFaultInfo();

		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);

			for (es.agroseguro.serviciosweb.contratacionayudas.Error error : errores) {
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}

		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de confirmacion
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrFallo fallo = ((es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.contratacionscconfirmacion.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionscconfirmacion.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.contratacionscconfirmacion.Error> it = errores.iterator(); it
					.hasNext();) {
				error = (es.agroseguro.serviciosweb.contratacionscconfirmacion.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	private static String printException(
			es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException e) {

		String mensaje = "";

		es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrFallo fallo = (es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrFallo) e
				.getFaultInfo();

		List<es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.Error error = null;

		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.Error> it = errores
					.iterator(); it.hasNext();) {
				error = (es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}

		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de contratacion
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.contratacionscutilidades.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.contratacionscutilidades.AgrFallo fallo = ((es.agroseguro.serviciosweb.contratacionscutilidades.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.contratacionscutilidades.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionscutilidades.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.contratacionscutilidades.Error> it = errores.iterator(); it
					.hasNext();) {
				error = (es.agroseguro.serviciosweb.contratacionscutilidades.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de impresion de la poliza
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.impresionscpoliza.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.impresionscpoliza.AgrFallo fallo = ((es.agroseguro.serviciosweb.impresionscpoliza.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.impresionscpoliza.Error> errores = null;
		es.agroseguro.serviciosweb.impresionscpoliza.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.impresionscpoliza.Error> it = errores.iterator(); it.hasNext();) {
				error = (es.agroseguro.serviciosweb.impresionscpoliza.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de seguimientos de poliza
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.seguimientoscpoliza.AgrFallo fallo = ((es.agroseguro.serviciosweb.seguimientoscpoliza.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.seguimientoscpoliza.Error> errores = null;
		es.agroseguro.serviciosweb.seguimientoscpoliza.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.seguimientoscpoliza.Error> it = errores.iterator(); it
					.hasNext();) {
				error = (es.agroseguro.serviciosweb.seguimientoscpoliza.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += " Codigo: " + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de consulta de pÃ³liza
	 * actualizada
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.contratacionscmodificacion.AgrFallo fallo = ((es.agroseguro.serviciosweb.contratacionscmodificacion.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.contratacionscmodificacion.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionscmodificacion.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.contratacionscmodificacion.Error> it = errores.iterator(); it
					.hasNext();) {
				error = (es.agroseguro.serviciosweb.contratacionscmodificacion.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += " Codigo: " + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de consulta de pÃ³liza
	 * actualizada
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.contratacionrenovaciones.AgrFallo fallo = ((es.agroseguro.serviciosweb.contratacionrenovaciones.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionrenovaciones.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> it = errores.iterator(); it
					.hasNext();) {
				error = (es.agroseguro.serviciosweb.contratacionrenovaciones.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += " Codigo: " + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	/**
	 * Imprime los errores de una exception del servicio de confirmacion
	 * 
	 * @param e
	 */
	private static String printException(es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.contratacionscrendimientos.AgrFallo fallo = ((es.agroseguro.serviciosweb.contratacionscrendimientos.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.contratacionscrendimientos.Error> errores = null;
		es.agroseguro.serviciosweb.contratacionscrendimientos.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error(ERRORES_DEVUELTOS_POR_EL_SERVICIO_WEB);
			logger.error(STRING);
			for (Iterator<es.agroseguro.serviciosweb.contratacionscrendimientos.Error> it = errores.iterator(); it
					.hasNext();) {
				error = (es.agroseguro.serviciosweb.contratacionscrendimientos.Error) it.next();
				logger.error(CODIGO + error.getCodigo() + MENSAJE + error.getMensaje());
				mensaje += CODIGO + error.getCodigo() + MENSAJE + error.getMensaje();
			}
		}
		return mensaje;
	}

	public static String getComparativaHeader(String idcomparativa,
			List<com.rsi.agp.dao.tables.poliza.ComparativaPoliza> listaComparativas) {
		return getComparativaHeader(idcomparativa, listaComparativas, false);
	}

	public static String getComparativaHeader(String idcomparativa,
			List<com.rsi.agp.dao.tables.poliza.ComparativaPoliza> listaComparativas, boolean isVersionReducida) {

		// Descripciones de los codigos de concepto que aplican a coberturas
		Map<String, String> descConceptos = new HashMap<String, String>();
		descConceptos.put("120", "% FRANQUICIA");
		descConceptos.put("121", "% M&Iacute;NIMO INDEMNIZABLE");
		descConceptos.put("170", "TIPO FRANQUICIA");
		descConceptos.put("174", "C&Aacute;LCULO INDEMNIZACI&Oacute;N");
		descConceptos.put("175", "GARANTIZADO");
		descConceptos.put("362", "% CAPITAL ASEGURADO");

		// Descripciones de las cobertuas. Se monta aqui para evitar problemas
		// cuando hay varias coberturas para una misma
		// combinacion de concepto principal del modulo y de riesgo cubierto
		Map<String, String> descCoberturas = new HashMap<String, String>();
		Map<String, String> valoresCoberturas = new HashMap<String, String>();
		for (ComparativaPoliza cp : listaComparativas) {

			String cpId = "" + cp.getId().getIdpoliza() + cp.getId().getLineaseguroid() + cp.getId().getCodmodulo()
					+ cp.getId().getIdComparativa();

			if (cpId.equals(idcomparativa) && !cp.getId().getCodconcepto().equals(new BigDecimal(106))) {
				String idcobertura = cp.getId().getCodconceptoppalmod() + "," + cp.getId().getCodriesgocubierto();
				String cob = descCoberturas.get(idcobertura);
				String val = valoresCoberturas.get(idcobertura);
				if (cob != null && cob.length() > 0) {
					cob += "<BR/>";
					val += "<BR/>";
				} else {
					cob = "";
					val = "";
				}
				cob += StringUtils.nullToString(descConceptos.get(cp.getId().getCodconcepto() + ""));
				val += StringUtils.nullToString(cp.getDescvalor());
				descCoberturas.put(idcobertura, cob);
				valoresCoberturas.put(idcobertura, val);
			}
		}

		String formattedReturn = "";
		String descripcionModulo = null;

		boolean todosElementosComparativaMenosDos = true;

		List<String> listaFilas = new ArrayList<String>();

		for (ComparativaPoliza cp : listaComparativas) {

			String cpId = "" + cp.getId().getIdpoliza() + cp.getId().getLineaseguroid() + cp.getId().getCodmodulo()
					+ cp.getId().getIdComparativa();

			if (cpId.equals(idcomparativa)) {
				if (descripcionModulo == null) {
					if (cp.getPoliza().getLinea().isLineaGanado()) {
						descripcionModulo = cp.getRiesgoCubiertoModuloGanado().getModulo().getId().getCodmodulo()
								+ " - " + cp.getRiesgoCubiertoModuloGanado().getModulo().getDesmodulo();
					} else {
						descripcionModulo = cp.getRiesgoCubiertoModulo().getModulo().getId().getCodmodulo() + " - "
								+ cp.getRiesgoCubiertoModulo().getModulo().getDesmodulo();
					}

					if (!isVersionReducida)
						formattedReturn += "<table width='100%'><tr>"
								+ "<td class='literalbordeCabecera' align='center' width='20%'>Garant&iacute;a</td>"
								+ "<td class='literalbordeCabecera' align='center' width='25%'>Riesgo Cubierto</td>"
								+ "<td class='literalbordeCabecera' align='center' width='20%'>Cobertura</td>"
								+ "<td class='literalbordeCabecera' align='center' width='20%'>Valor</td>"
								+ "</tr><tr class='literalborde'>";
				}
				if (cp.getId().getCodvalor().intValue() != -2
						&& !cp.getId().getCodconcepto().equals(new BigDecimal(106)) && !listaFilas.contains(
								cp.getId().getCodconceptoppalmod() + "," + cp.getId().getCodriesgocubierto())) {

					todosElementosComparativaMenosDos = false;
					listaFilas.add(cp.getId().getCodconceptoppalmod() + "," + cp.getId().getCodriesgocubierto());

					formattedReturn += "<tr>";
					formattedReturn += "<td class='literalborde' width='20%'>"
							+ cp.getConceptoPpalModulo().getDesconceptoppalmod() + "&nbsp;</td>";
					formattedReturn += "<td class='literalborde' width='26%'>"
							+ cp.getRiesgoCubierto().getDesriesgocubierto() + "&nbsp;</td>";
					formattedReturn += "<td class='literalborde' width='20%'>" + StringUtils.nullToString(descCoberturas
							.get(cp.getId().getCodconceptoppalmod() + "," + cp.getId().getCodriesgocubierto()));

					formattedReturn += "&nbsp;</td><td class='literalborde' width='20%'>"
							+ StringUtils.nullToString(valoresCoberturas
									.get(cp.getId().getCodconceptoppalmod() + "," + cp.getId().getCodriesgocubierto()))
							+ "&nbsp;</td>";

				}
			}
		}
		if (todosElementosComparativaMenosDos) {

			formattedReturn += "<tr>";
			formattedReturn += "<td class='literalborde' colspan='4'>Sin riesgos cubiertos elegibles</td>";

		}

		if (!isVersionReducida)
			formattedReturn += "</tr></tr></table>";

		return formattedReturn;
	}

	/**
	 * Metodo que devuelve el mensaje de validacion que se va a mostrar, los botones
	 * de calculo o corregir, y el listado de errores para el displaytag
	 * 
	 * @param documentoArray
	 * @return
	 */
	public static Map<String, Object> getMensajesYBotones(Documento documentoArray) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		boolean errorTramite = false;
		boolean errorRechazo = false;
		String mensaje = "";

		if (documentoArray.getErrorArray() != null && documentoArray.getErrorArray().length > 0) {
			for (Error error : documentoArray.getErrorArray()) {
				if (error.getTipo() == 1) {
					errorRechazo = true;
					break;
				} else if (error.getTipo() == 2) {
					errorTramite = true;
				}
			}
			parametros.put("errores", Arrays.asList(documentoArray.getErrorArray()));
			parametros.put("errLength", documentoArray.getErrorArray().length);
		}

		if ((errorRechazo && errorTramite) || errorRechazo && !errorTramite) {
			parametros.put("botonCalcular", false);
			parametros.put("botonCorregir", true);
			mensaje = "Los datos enviados NO son correctos, con los siguientes errores:";
		} else if (!errorRechazo && errorTramite) {
			parametros.put("botonCalcular", true);
			parametros.put("botonCorregir", true);
			mensaje = "Los datos enviados contienen errores de tr&aacute;mite, corrija o contin&uacute;e:";
		} else {
			parametros.put("botonCalcular", true);
			parametros.put("botonCorregir", false);
			parametros.put("datosCorrectos", true);
			mensaje = "Los datos enviados son correctos";
		}

		parametros.put("mensaje", mensaje);
		return parametros;
	}

	/**
	 * DAA 26/04/12 Elige que condiciones debe de mandar al jsp.
	 * 
	 * @param acusePolizaHolder
	 * @param cabeceraComparativaHTML
	 * @return condiciones
	 */
	public static Map<String, Object> getCondicionesErroresValidacion(
			Map<ComparativaPolizaId, ResultadoWS> acusePolizaHolder, List<ErrorWsAccion> errorWsAccionList) {
		int dimensionArrays = acusePolizaHolder == null ? 0 : acusePolizaHolder.size();
		boolean[] arrBotonCalculo = new boolean[dimensionArrays];
		boolean[] arrBotonCorregir = new boolean[dimensionArrays];
		boolean calcular = false;
		String[] sErrorHeader = new String[dimensionArrays];
		boolean bBotonCalculo = false;
		boolean bBotonCorregir = false;
		int indice = 0;

		if (acusePolizaHolder != null) {
			for (Map.Entry<ComparativaPolizaId, ResultadoWS> acusePolizaHolderEntrada : acusePolizaHolder.entrySet()) {
				arrBotonCalculo[indice] = false;
				arrBotonCorregir[indice] = false;
				AcuseRecibo acuse = acusePolizaHolderEntrada.getValue().getAcuseRecibo();
				boolean acuseNoNulo = acuse != null;
				logger.info(new StringBuilder("El acuse de recibo es ").append(acuseNoNulo ? "NO " : "").append("nulo")
						.toString());
				if (acuseNoNulo) {
					boolean forzarErroresPorPerfil = comprobarErroresPorPerfil(acuse, errorWsAccionList);
					arrBotonCalculo[indice] = acuse.getDocumentosRecibidos() <= 0 ? true : false;
					calcular = forzarErroresPorPerfil ? true : false;
					arrBotonCalculo[indice] = forzarErroresPorPerfil ? true : false;
					arrBotonCorregir[indice] = forzarErroresPorPerfil ? false : true;
				}
				indice++;
			}
		}
		// Se comprueban todas las comparativas
		for (int i = 0; i < indice; i++) {
			bBotonCalculo = arrBotonCalculo[i] ? true : false;
			bBotonCorregir = arrBotonCorregir[i] ? false : true;
			if (arrBotonCalculo[i] && !arrBotonCorregir[i]) {
				logger.debug("Datos enviados correctamente");
				sErrorHeader[i] = "Los datos enviados son correctos";
			} else {
				logger.debug("Datos enviados contienen errores");
				sErrorHeader[i] = "Los datos enviados NO son correctos, con los siguientes errores:";
			}
		}

		Map<String, Object> condiciones = new HashMap<String, Object>();
		condiciones.put("bBotonCalculo", bBotonCalculo);
		condiciones.put("bBotonCorregir", bBotonCorregir);
		condiciones.put("arrBotonCorregir", arrBotonCorregir);
		condiciones.put("arrBotonCalculo", arrBotonCalculo);
		condiciones.put("sErrorHeader", sErrorHeader);
		condiciones.put("keys", acusePolizaHolder == null ? Collections.EMPTY_SET : acusePolizaHolder.keySet());
		condiciones.put("acusePolizaHolder", acusePolizaHolder);
		condiciones.put("calcular", calcular);
		return condiciones;
	}

	/**
	 * Funcion que comprueba si un usuario puede forzar todo los errores que
	 * retorna un servicio web
	 * 
	 * @param acuseRecibo
	 * @param errorWsAccionList
	 * @return boolean
	 */
	// P0021873 2.6 CAMBIAMOS LA VISIBILIDAD PARA PODER USAR ESTE METODO EN EL
	// PASO A DEFINITIVA
	public static boolean comprobarErroresPorPerfil(AcuseRecibo acuseRecibo, List<ErrorWsAccion> errorWsAccionList) {
		logger.debug("Accediendo a la comprobacion de que errores puede forzar el usuario");
		List<BigDecimal> listaErroresDesdeWS = retrieveErrorsFromWs(acuseRecibo);
		if (listaErroresDesdeWS.isEmpty()) {
			logger.debug("El acuse de recibo no tiene errores.");
			return true;
		}
		if (CollectionsAndMapsUtil.isEmpty(errorWsAccionList)) {
			logger.debug("El usuario no puede forzar ningun error");
			return false;
		}
		List<BigDecimal> listaErroresDesdeBD = retrieveErrorsFromDb(errorWsAccionList);
		return compareErrors(listaErroresDesdeBD, listaErroresDesdeWS);
	}

	/**
	 * Metodo que compara los errores de las dos listas. Retorna true si en ambas
	 * listas estan todos los errores.
	 * 
	 * @param listaErroresDesdeBD
	 * @param listaErroresDesdeWS
	 * @return boolean
	 */
	private static boolean compareErrors(List<BigDecimal> listaErroresDesdeBD, List<BigDecimal> listaErroresDesdeWS) {
		boolean userCanForce = true;
		for (BigDecimal errorCodeFromWs : listaErroresDesdeWS) {
			if (!listaErroresDesdeBD.contains(errorCodeFromWs)) {
				userCanForce = false;
				break;
			}
		}
		logger.debug(new StringBuilder("El usuario ").append(userCanForce ? "SI" : "NO")
				.append(" puede forzar todos los errores").toString());
		return userCanForce;
	}

	/**
	 * Metodo que extrae los errores provenientes del acuse de recibo del servicio
	 * web
	 * 
	 * @param acuseRecibo
	 * @return List<BigDecimal>
	 */
	private static List<BigDecimal> retrieveErrorsFromWs(AcuseRecibo acuseRecibo) {
		List<BigDecimal> errorsFromWsList = new ArrayList<BigDecimal>();
		logger.debug("Accediendo al acuse de recibo del servicio web");
		for (Documento doc : acuseRecibo.getDocumentoArray()) {
			logger.debug("Buscando si existen errores en el acuse de recibo");
			Error[] errorsArray = doc.getErrorArray();
			for (Error error : errorsArray) {
				logger.debug("Error en respuesta del servicio web encontrado");
				errorsFromWsList.add(new BigDecimal(error.getCodigo()));
			}
		}
		String finalLogMsg = new StringBuilder("La respuesta del servicio web ")
				.append(errorsFromWsList.isEmpty() ? "NO" : "SI").append(" contiene errores").toString();
		logger.debug(finalLogMsg);
		return errorsFromWsList;
	}

	/**
	 * Metodo que extrae los errreos provenientes de la base de datos
	 * 
	 * @param errorWsAccionList
	 * @return
	 */
	private static List<BigDecimal> retrieveErrorsFromDb(List<ErrorWsAccion> errorWsAccionList) {
		List<BigDecimal> errorsFromDbList = new ArrayList<BigDecimal>();
		logger.debug("Buscando los errores que puede forzar el usuario");
		for (ErrorWsAccion errorWsAction : errorWsAccionList) {
			errorsFromDbList.add(errorWsAction.getErrorWs().getId().getCoderror());
		}
		return errorsFromDbList;
	}

	/**
	 * DAA 04/06/2012 Metodo para eliminar de los acuses de recibo aquellos errores
	 * que no se desea mostrar. Se consultaran las tablas TB_COD_ERROR* para saber
	 * que errores se pueden omitir al mostrar la pantalla de errores de las
	 * llamadas a los servicios
	 * 
	 * @param acuseRecibo
	 *            acuse de recibo.
	 * @param webServiceToCall
	 *            Indica si llamamos al servicio de validacion o de calculo
	 * @param genericDao
	 */
	@SuppressWarnings("rawtypes")
	public static void limpiaErroresWs(AcuseRecibo acuseRecibo, String webServiceToCall, Parametro parametro,
			GenericDao genericDao, BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntida, String servicio) {
		logger.debug("WSUtils - limpiaErroresWs [INIT]");
		logger.debug("Obtenemos la lista de Errores para el Servicio:"+servicio);
		List<String> listaCodError = getListaCodErrores(parametro, genericDao, codPlan, codLinea, codEntida, servicio);
		// recorrer cada acuse de recibo para ver si hay que quitar algun error
		for (int i = 0; i < acuseRecibo.getDocumentosRecibidos(); i++) {
			Documento documento = acuseRecibo.getDocumentoArray(i);
			int j = 0;
			while (j < documento.getErrorArray().length) {
				es.agroseguro.acuseRecibo.Error error = documento.getErrorArray(j);
				if (listaCodError.contains(error.getCodigo())) {
					documento.removeError(j);
				} else {
					j++;
				}
			}
		}
		logger.debug("WSUtils - limpiaErroresWs [END]");
	}

	/**
	 * DAA 04/06/2012 Metodo para eliminar de los acuses de recibo aquellos errores
	 * que no se desea mostrar. Se consultaran las tablas TB_COD_ERROR* para saber
	 * que errores se pueden omitir al mostrar la pantalla de errores de las
	 * llamadas a los servicios
	 * 
	 * @param acusePolizaHolder
	 *            Mapa con los acuses de recibo.
	 * @param webServiceToCall
	 *            Indica si llamamos al servicio de validacion o de calculo
	 */
	@SuppressWarnings("rawtypes")
	public static void limpiaErroresWs(Map<ComparativaPolizaId, ResultadoWS> acusePolizaHolder, Parametro parametro,
			GenericDao genericDao, BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntidad, String servicio) {

		List<String> listaCodError = getListaCodErrores(parametro, genericDao, codPlan, codLinea, codEntidad, servicio);

		// recorrer cada acuse de recibo para ver si hay que quitar algun error
		for (Map.Entry<ComparativaPolizaId, ResultadoWS> entry : acusePolizaHolder.entrySet()) {
			Documento[] documentos = entry.getValue().getAcuseRecibo().getDocumentoArray();
			for (Documento doc : documentos) {
				int j = 0;
				while (j < doc.getErrorArray().length) {
					Error error = doc.getErrorArray(j);
					if (listaCodError.contains(error.getCodigo())) {
						doc.removeError(j);
					} else {
						j++;
					}
				}
			}
		}
	}

	/**
	 * DAA 04/06/2012 Metodo para obtener la lista de Codigos de errores.
	 * 
	 * @param webServiceToCall
	 * @param parametro
	 * @param genericDao
	 * @return listaCodError
	 */
	@SuppressWarnings("rawtypes")
	private static List<String> getListaCodErrores(Parametro parametro, GenericDao genericDao, BigDecimal codPlan,
			BigDecimal codLinea, BigDecimal codEntidad, String servicio) {
		// Obtenemos la lista de errores que se pueden omitir
		List<ErrorWsAccion> listaErrores = getListaErroresOmitir(genericDao, codPlan, codLinea, codEntidad, servicio);
		// Nos quedamos con los codigos de error a omitir
		List<String> listaCodError = new ArrayList<String>();
		for (ErrorWsAccion error : listaErrores) {
			listaCodError.add(error.getErrorWs().getId().getCoderror().toString());
		}
		return listaCodError;
	}

	/**
	 * Metodo para obtener la lista de errores que se pueden omitir.
	 * 
	 * @param servicio
	 *            Servicio para el cual se desean filtrar los errores.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<ErrorWsAccion> getListaErroresOmitir(GenericDao genericDao, BigDecimal codPlan,
			BigDecimal codLinea, BigDecimal codEntidad, String servicio) {
		List<ErrorWsAccion> result = new ArrayList<ErrorWsAccion>();
		ErrorWsFiltro filtroOmitir = new ErrorWsFiltro(codPlan, codLinea, codEntidad, servicio);
		List<ErrorWsAccion> erroresOmitir = genericDao.getObjects(filtroOmitir);
		ErrorWsFiltro filtroNoOmitir = new ErrorWsFiltro(codPlan, codLinea, codEntidad, null, servicio);
		List<ErrorWsAccion> erroresNoOmitir = genericDao.getObjects(filtroNoOmitir);
		for (ErrorWsAccion errorOmitir : erroresOmitir) {
			boolean omitir = Boolean.TRUE;
			for (ErrorWsAccion errorNoOmitir : erroresNoOmitir) {
				if (errorOmitir.getErrorWs().getId().getCoderror().equals(errorNoOmitir.getErrorWs().getId().getCoderror())) {
					// NOS QUEDAMOS CON EL MAS RESTRICTIVO
					if (errorNoOmitir.getNivel() > errorOmitir.getNivel()) {
						omitir = Boolean.FALSE;
						break;
					}
				}
			}
			if (omitir) {
				result.add(errorOmitir);
			}
		}
		return result;
	}

	public static String getStringResponse(Base64Binary response) throws Exception {
		byte[] byteArray = response.getValue();
		String xmlString = new String(byteArray, Constants.DEFAULT_ENCODING);
		return xmlString;
	}

	public static String generateXMLPolizaModulosCoberturas(Poliza poliza, Explotacion exp, ExplotacionAnexo expAnexo,
			Set<ExplotacionAnexo> listaExpAnexo, String codModulo, IPolizaDao polizaDao)
			throws ValidacionPolizaException, DAOException, BusinessException {

		logger.debug("WSUtils - generateXMLPolizaModulosCoberturas [INIT]");

		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;

		GregorianCalendar fechaI = new GregorianCalendar();

		// TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();

		Map<Long, DatosVariables> mapDvEspecialesExplotacion = new HashMap<Long, DatosVariables>();
		List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();

		/* Pet. 63845 ** MODIF TAM (16.07.2020) ** Inicio */

		es.agroseguro.presupuestoContratacion.PolizaDocument xmlPoliza = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();

		if (poliza.getLinea().isLineaGanado()) {

			gruposNegocio = polizaDao.getGruposNegocio(poliza.getIdpoliza());
			if (gruposNegocio == null || gruposNegocio.size() == 0) {
				if (exp != null) {
					for (GrupoRaza gr : exp.getGrupoRazas()) {
						List<GruposNegocio> grNeg = polizaDao.getGruposNegocio(poliza.getLinea().getLineaseguroid(),
								gr.getCodgruporaza(), gr.getCodtipocapital().longValue());
						if (grNeg != null && grNeg.size() > 0)
							gruposNegocio.addAll(grNeg);
					}
				}
			}

			for (Explotacion explot : poliza.getExplotacions()) {
				DatosVariables dvEsp = getDatosVariablesEspecialesExplotacion(explot.getId(), polizaDao);
				if (dvEsp != null)
					mapDvEspecialesExplotacion.put(explot.getId(), dvEsp);
			}
		} else {
			logger.debug("Antes de obtener los codsConceptos");
			Long lineaSeguroId = poliza.getLinea().getLineaseguroid();

			codsConceptos = polizaDao.getCodsConceptoOrganizador(lineaSeguroId);
		}

		/* Llamamos al metodo tanto para Ganado como para las polizas Agricolas */
		logger.debug("Antes de montar el xmlPoliza");
		xmlPoliza = PolizaUnificadaTransformer.transformarPolizaModulosYCoberturas(poliza, codsConceptos, polizaDao,
				exp, expAnexo, listaExpAnexo, codModulo, gruposNegocio, mapDvEspecialesExplotacion);

		String envio = cabecera
				+ xmlPoliza.toString().replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
						.replace(XML_FRAGMENT2, PKS_POLIZA);

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		return envio;

	}

	/* ESC-12909 ** MODIF TAM (19.04.2021) ** Inicio */
	/*
	 * Para obtener todas las coberturas en el S.W de Coberturas Contratadas hay que
	 * enviar los datos variables del Modulo
	 */
	public static String generateXMLPolizaCoberturasContratadas(Poliza poliza, Explotacion exp,
			ExplotacionAnexo expAnexo, Set<ExplotacionAnexo> listaExpAnexo, String codModulo, IPolizaDao polizaDao,
			ICPMTipoCapitalDAO cpmTipoCapitalDao) throws ValidacionPolizaException, DAOException, BusinessException {

		logger.debug("WSUtils - generateXMLPolizaCoberturasContratadas [INIT] (v1.2)");
		// .generateXMLPolizaModulosCoberturas(plz,exp,modP);
		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;

		// 1. Obtenemos las comparativas de la poliza
		
		List<ComparativaPoliza> listComparativasPoliza = poliza.getComparativaPolizas() != null
				? Arrays.asList(poliza.getComparativaPolizas().toArray(new ComparativaPoliza[] {}))
				: new ArrayList<ComparativaPoliza>();
		Collections.sort(listComparativasPoliza, new ComparativaPolizaComparator());

		List<ModuloPoliza> modulosPoliza = polizaDao.getLstModulosPoliza(poliza.getIdpoliza());
		Collections.sort(modulosPoliza, new ModuloPolizaComparator());
		ComparativaPoliza cp = new ComparativaPoliza(); 

		for (ModuloPoliza mp : modulosPoliza) {
			/* P0063482 ** MODIF TAM (23/08/2021) ** Defecto 31 */
			/* Creamos una comparativa ficticia en caso de no tenerla */
			if (listComparativasPoliza.size() <= 0) {
				logger.debug("Entramos a crear la comparativa ficticia por que no recupera nada");
				if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
					List<DistribucionCoste2015>listDc = new ArrayList<DistribucionCoste2015>();		
					listDc.addAll(poliza.getDistribucionCoste2015s());		
					
					DistribucionCoste2015 dc2015 = listDc.get(0);
					
					ComparativaPolizaId cpId = new ComparativaPolizaId(); 
					cpId.setIdpoliza(poliza.getIdpoliza());
					cpId.setLineaseguroid(poliza.getLinea().getLineaseguroid()); 
					cpId.setCodmodulo(poliza.getCodmodulo()); 
					cpId.setFilacomparativa(dc2015.getFilacomparativa()); 
					cpId.setIdComparativa(dc2015.getIdcomparativa() != null ? dc2015.getIdcomparativa().longValue() : null); 
					
					cp.setId(cpId);
					cp.setEsFinanciada(dc2015.getImportePagoFracc() != null); 
					
				} 
			}else {
				logger.debug("Se recupera lista de comparativas para la póliza");
				for (ComparativaPoliza cpAux : listComparativasPoliza) {
					if (mp.getId().getNumComparativa().equals(cpAux.getId().getIdComparativa())) {
						cp = cpAux;
						break;
					}
				}
			}	
			logger.debug("3-Obtenemos la lista de riesgos cubiertos para la línea y el modulo");
			logger.debug("Valor de linea:"+poliza.getLinea().getLineaseguroid());
			logger.debug("Valor de CodModulo:"+mp.getId().getCodmodulo());
			List<RiesgoCubiertoModulo> lstrcm = polizaDao.getRiesgoCubiertosModulo(poliza.getLinea().getLineaseguroid(),
					mp.getId().getCodmodulo());
			logger.debug("Despues de obtener la lista de riesgos");
			logger.debug("Valor de size de la lista:"+lstrcm.size());
			
			if (lstrcm.size() > 0 && cp != null) {
				if (lstrcm != null && !lstrcm.isEmpty()) {
					RiesgoCubiertoModulo rcm = lstrcm.get(0);
					cp.setRiesgoCubiertoModulo(rcm);
				}
			}
		}
		
		

		// 2. Comprobar si para lineaseguroid y codmodulo existen codigos de
		// reduccion de rendimiento
		ReduccionRendimientosAmbitosFiltro filtro = new ReduccionRendimientosAmbitosFiltro();
		
		logger.debug("4-Antes de setear el valor de Lineaseguroid:"+cp.getId().getLineaseguroid());
		filtro.setLineaSeguroId(cp.getId().getLineaseguroid());
		logger.debug("5-Antes de setear el valor de codmodulo:"+cp.getId().getCodmodulo());
		filtro.setCodmodulo(cp.getId().getCodmodulo());

		boolean aplicaReduccionRdto = polizaDao.getNumObjects(filtro).intValue() > 0;

		// Calculo de CPM permitidos
		logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: "
				+ cp.getId().getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(),
				cp.getId().getCodmodulo());

		GregorianCalendar fechaI = new GregorianCalendar();

		// TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		// Parcela Datos Variables
		// List<BigDecimal> codsConceptos = null;
		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();

		Map<Long, DatosVariables> mapDvEspecialesExplotacion = new HashMap<Long, DatosVariables>();
		List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();

		/* Pet. 63845 ** MODIF TAM (16.07.2020) ** Inicio */

		es.agroseguro.presupuestoContratacion.PolizaDocument xmlPoliza = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();
		
		if (poliza.getLinea().isLineaGanado()) {
			logger.debug("Entramos por el if - ES GANADO");

			gruposNegocio = polizaDao.getGruposNegocio(poliza.getIdpoliza());
			if (gruposNegocio == null || gruposNegocio.size() == 0) {
				for (GrupoRaza gr : exp.getGrupoRazas()) {
					List<GruposNegocio> grNeg = polizaDao.getGruposNegocio(poliza.getLinea().getLineaseguroid(),
							gr.getCodgruporaza(), gr.getCodtipocapital().longValue());
					if (grNeg != null && grNeg.size() > 0)
						gruposNegocio.addAll(grNeg);
				}
			}

			for (Explotacion explot : poliza.getExplotacions()) {
				DatosVariables dvEsp = getDatosVariablesEspecialesExplotacion(explot.getId(), polizaDao);
				if (dvEsp != null)
					mapDvEspecialesExplotacion.put(explot.getId(), dvEsp);
			}
		} else {
			
			logger.debug("Entramos por el ELSE - ES AGRÍCOLA");
			Long lineaSeguroId = poliza.getLinea().getLineaseguroid();

			codsConceptos = polizaDao.getCodsConceptoOrganizador(lineaSeguroId);
		}

		/* Llamamos al metodo tanto para Ganado como para las polizas Agricolas */
		logger.debug("Antes de montar el xmlPoliza");
		xmlPoliza = PolizaUnificadaTransformer.transformarPolizaCoberturasContratadas(poliza, codsConceptos, polizaDao,
				exp, expAnexo, listaExpAnexo, codModulo, gruposNegocio, mapDvEspecialesExplotacion, cp,
				aplicaReduccionRdto, listaCPM);

		String envio = cabecera
				+ xmlPoliza.toString().replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
						.replace(XML_FRAGMENT2, PKS_POLIZA);

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		logger.debug("WSUtils - generateXMLPolizaCoberturasContratadas [END] (v1.2)");

		return envio;

	}

	// ########### Datos Variables Especiales de la explotacion
	// #############################################################

	private static DatosVariables getDatosVariablesEspecialesExplotacion(Long idExplotacion, IPolizaDao polizaDao)
			throws DAOException {
		DatosVariables dv = null;
		List<PorcentajeMinimoIndemnizable> lstPmi = getDatVarPctMinIndemnizable(idExplotacion, polizaDao);
		List<CalculoIndemnizacion> lstCi = getDatVarCalculoIndemnizacion(idExplotacion, polizaDao);

		if (lstPmi != null && lstPmi.size() > 0) {
			dv = DatosVariables.Factory.newInstance();
			dv.addNewMinIndem();
			dv.setMinIndemArray(lstPmi.toArray(new PorcentajeMinimoIndemnizable[lstPmi.size()]));
		}

		if (lstCi != null && lstCi.size() > 0) {
			if (dv == null)
				dv = DatosVariables.Factory.newInstance();
			dv.addNewCalcIndem();
			dv.setCalcIndemArray(lstCi.toArray(new CalculoIndemnizacion[lstCi.size()]));
		}

		return dv;
	}

	@SuppressWarnings("rawtypes")
	private static List<PorcentajeMinimoIndemnizable> getDatVarPctMinIndemnizable(Long idExplotacion,
			IPolizaDao polizaDao) throws DAOException {

		DatosVariables dv = null;
		List registros = null;
		List<PorcentajeMinimoIndemnizable> lstMI = null;
		ArrayList<PorcentajeMinimoIndemnizable> lstMIA = null;
		registros = getDatosVariablesEspecialesExplotacion(idExplotacion, 121, polizaDao);
		if (null != registros && registros.size() > 0) {
			dv = DatosVariables.Factory.newInstance();
			lstMI = Arrays.asList(dv.getMinIndemArray());
			lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);
			lstMIA.clear();
			for (int i = 0; i < registros.size(); i++) {
				PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
				Object[] registro = null;
				registro = (Object[]) registros.get(i);
				m.setCodRCub(((BigDecimal) registro[2]).intValue());
				m.setCPMod(((BigDecimal) registro[1]).intValue());
				m.setValor(new Integer(registro[4].toString()));
				lstMIA.add(m);
			}
		}

		// datvar.setMinIndemArray(lstMIA.toArray(new
		// PorcentajeMinimoIndemnizable[lstMIA.size()]));
		// result.put(codUbicacion, datvar);
		return lstMIA;
	}

	@SuppressWarnings("rawtypes")
	private static List<CalculoIndemnizacion> getDatVarCalculoIndemnizacion(Long idExplotacion, IPolizaDao polizaDao)
			throws DAOException {

		DatosVariables dv = null;
		List registros = null;
		List<CalculoIndemnizacion> lstMI = null;
		ArrayList<CalculoIndemnizacion> lstMIA = null;
		registros = getDatosVariablesEspecialesExplotacion(idExplotacion, 174, polizaDao);
		if (null != registros && registros.size() > 0) {
			dv = DatosVariables.Factory.newInstance();
			lstMI = Arrays.asList(dv.getCalcIndemArray());
			lstMIA = new ArrayList<CalculoIndemnizacion>(lstMI);
			lstMIA.clear();
			for (int i = 0; i < registros.size(); i++) {
				CalculoIndemnizacion m = CalculoIndemnizacion.Factory.newInstance();
				Object[] registro = null;
				registro = (Object[]) registros.get(i);
				m.setCodRCub(((BigDecimal) registro[2]).intValue());
				m.setCPMod(((BigDecimal) registro[1]).intValue());
				m.setValor(new Integer(registro[4].toString()));
				lstMIA.add(m);
			}
		}
		return lstMIA;
	}

	@SuppressWarnings("rawtypes")
	private static List getDatosVariablesEspecialesExplotacion(Long idExplotacion, long codConcepto,
			IPolizaDao polizaDao) throws DAOException {
		List registros = polizaDao.getDatosVariablesEspecialesExplotacion(idExplotacion, codConcepto);
		return registros;

	}

	private static String getFilasCoberturasGanado(ModuloView mv,
			List<com.rsi.agp.dao.tables.poliza.ComparativaPoliza> listaComparativas, Long idModulo) {
		List<String> listaFilas = new ArrayList<String>();
		String res = new String();
		Collections.sort(listaComparativas, new ComparativaPolizaComparator());
		List<String> listaAuxCadenas = new ArrayList<String>();

		logger.debug("WSUtils - getFilasCoberturasGanado [INIT]");

		for (ComparativaPoliza cp : listaComparativas) {
			if (cp.getId().getIdComparativa().compareTo(idModulo) == 0
					&& !new BigDecimal(Constants.RIESGO_ELEGIDO_NO).equals(cp.getId().getCodvalor())) {
				ModuloFilaView mfv = getModuloFilaView(mv, cp, listaAuxCadenas);
				String fila = "";
				if (mfv != null) {
					fila = getHtmlFilaCoberturas(cp, mfv, mv, listaComparativas);
				}
				listaFilas.add(fila);
			}
		}
		if (listaFilas.size() == 0) {
			String fila = getFilaPorDefecto(mv.getListaCabeceras().size());
			listaFilas.add(fila);
		}

		for (String fila : listaFilas) {
			res += fila;
		}

		logger.debug("WSUtils - getFilasCoberturasGanado [END]");

		return res;
	}

	/**
	 * Comparamos la Comparativa con las filas de las coberturas para saber cual
	 * es su correspondencia
	 * 
	 * @param mv
	 * @param cp
	 * @return
	 */
	private static ModuloFilaView getModuloFilaView(ModuloView mv, ComparativaPoliza cp, List<String> comparativasAux) {
		ModuloFilaView res = null;

		logger.debug("WSUtils - getModuloFilaView");

		for (ModuloFilaView mfv : mv.getListaFilas()) {

			// logger.debug("Valor de mvf: "+mfv.toString());
			// logger.debug("Comparamos Valor de mfv.Filamodulo: "+mfv.getFilamodulo() + " y
			// cp.getFilaModulo: "+cp.getId().getFilamodulo() );
			if (mfv.getFilamodulo().compareTo(cp.getId().getFilamodulo()) == 0) {
				// logger.debug("Entramos en el primer if");

				// logger.debug("Comparamos Valor de mfv.getCodConceptoPrincipalModulo:
				// "+mfv.getCodConceptoPrincipalModulo() +
				// " y cp.getConceptoPpalModulo():
				// "+cp.getConceptoPpalModulo().getCodconceptoppalmod() );

				if (mfv.getCodConceptoPrincipalModulo()
						.compareTo(cp.getConceptoPpalModulo().getCodconceptoppalmod()) == 0) {
					// logger.debug("Entramos en el segundo if");

					// logger.debug("Comparamos Valor de mfv.getCodRiesgoCubierto:
					// "+mfv.getCodRiesgoCubierto() +
					// " y cp.getCodriesgocubierto():
					// "+cp.getRiesgoCubierto().getId().getCodriesgocubierto() );

					if (mfv.getCodRiesgoCubierto()
							.compareTo(cp.getRiesgoCubierto().getId().getCodriesgocubierto()) == 0) {
						// logger.debug("Entramos en el tercer if ");

						// logger.debug("Comparamos Valor de mfv.getFilaComparativa(): "+
						// mfv.getFilaComparativa() +
						// " y cp.getId().getFilacomparativa(): "+cp.getId().getFilacomparativa());

						if (mfv.getFilaComparativa().compareTo(cp.getId().getFilacomparativa()) == 0) {
							if (!comparativasAux.contains(cp.getId().getFilamodulo() + "_"
									+ cp.getConceptoPpalModulo().getCodconceptoppalmod() + "_"
									+ cp.getRiesgoCubierto().getId().getCodriesgocubierto() + "_"
									+ cp.getId().getFilacomparativa())) {
								comparativasAux.add(cp.getId().getFilamodulo() + "_"
										+ cp.getConceptoPpalModulo().getCodconceptoppalmod() + "_"
										+ cp.getRiesgoCubierto().getId().getCodriesgocubierto() + "_"
										+ cp.getId().getFilacomparativa());
								res = mfv;
								break;
							}
						}
					}
				}
			}
		}
		return res;
	}

	private static String getHtmlFilaCoberturas(ComparativaPoliza cp, ModuloFilaView mfv, ModuloView mv,
			List<com.rsi.agp.dao.tables.poliza.ComparativaPoliza> listaComparativas) {

		boolean filaConUnaCeldaElegible = false;
		boolean valorCheckFila = false;
		
		if (null != listaComparativas) {
			for (ComparativaPoliza compActual : listaComparativas) {
				if (mv.getCodModulo().equals(compActual.getId().getCodmodulo())
						&& mfv.getFilamodulo().equals(compActual.getId().getFilamodulo())
						&& mv.getIdModulo().equals(compActual.getId().getIdComparativa())) {
					String valorCheck = compActual.getId().getCodvalor().toString();
					if (valorCheck.equals("-1")) { // seleccionado, si esta en -2 es no seleccionado
						valorCheckFila = true;
					}
				}
			}
		}

		int numCab = mv.getListaCabeceras().size();
		int anchoCab = 100 / numCab;
		String res = "<tr>";
		res += TD_CLASS_LITERALBORDE_STYLE_WIDTH + anchoCab + "%;'>" + mfv.getConceptoPrincipalModulo()
				+ TD;
		if (mfv.isRcElegible() && valorCheckFila) {
			res += "<td class='literalbordeAzul' style='width:" + anchoCab + "%;'>" + mfv.getRiesgoCubierto() + TD;
			filaConUnaCeldaElegible = true;
		} else {
			res += TD_CLASS_LITERALBORDE_STYLE_WIDTH + anchoCab + "%;'>" + mfv.getRiesgoCubierto() + TD;
		}

		for (ModuloCeldaView celda : mfv.getCeldas()) {
			if (null != celda.getValores() && celda.getValores().size() > 0) {
				if (!celda.isElegible()) {
					res += TD_CLASS_LITERALBORDE_STYLE_WIDTH + anchoCab + "%;'>";
					for (ModuloValorCeldaView v : celda.getValores()) {
						res += v.getDescripcion() + "<br>";
					}
					res += TD;
				} else {
					// si la celda es elegible, primero tengo que saber si tiene rcelegible y esta
					// check
					// si la celda es elegible y rcelegible es falso
					filaConUnaCeldaElegible = !mfv.isRcElegible() || (mfv.isRcElegible() && valorCheckFila);

					String valor = "";
					if (mfv.isRcElegible()) {
						valor = getValorComparativaGemela(cp, listaComparativas, celda.getCodconcepto());
					} else {
						if (celda.getValores().size() == 1) {
							for (ModuloValorCeldaView v : celda.getValores()) {
								valor += v.getDescripcion() + "<br/>";
							}
						} else if (celda.getValores().size() > 1) {
							valor = getValorComparativaGemela(cp, listaComparativas, celda.getCodconcepto());
						}
					}
					if (null != valor) {
						res += "<td class='literalbordeAzul' style='width:" + anchoCab + "%;'>" + valor + TD;
					} else {
						res += TD_CLASS_LITERALBORDE_STYLE_WIDTH + anchoCab + "%;'>";
						for (ModuloValorCeldaView v : celda.getValores()) {
							res += v.getDescripcion() + "<br/>";
						}
						res += TD;
					}
				}
			} else {
				if (celda.getObservaciones() == null
						|| (celda.getObservaciones() != null && "".equals(celda.getObservaciones().trim())))
					res += TD_CLASS_LITERALBORDE_STYLE_WIDTH + anchoCab + "%;'>&nbsp;</td>";
				else
					res += TD_CLASS_LITERALBORDE_STYLE_WIDTH + anchoCab + "%;'>" + celda.getObservaciones()
							+ TD;
			}
		}
		res += "</tr>";

		if (!filaConUnaCeldaElegible)
			res = "";

		return res;
	}

	private static String getValorComparativaGemela(ComparativaPoliza cp, List<ComparativaPoliza> listaComparativas,
			BigDecimal codConceptoElegibleSinRiesCub) {
		String res = null;
		for (ComparativaPoliza cg : listaComparativas) {
			if (cp.getId().getIdComparativa().compareTo(cg.getId().getIdComparativa()) == 0) {
				if (cp.getId().getCodmodulo().compareTo(cg.getId().getCodmodulo()) == 0) {					
					if (cp.getConceptoPpalModulo().getCodconceptoppalmod()
							.compareTo(cg.getConceptoPpalModulo().getCodconceptoppalmod()) == 0) {
						if (cp.getRiesgoCubierto().getId().getCodriesgocubierto()
								.compareTo(cg.getRiesgoCubierto().getId().getCodriesgocubierto()) == 0) {
							if (cg.getId().getCodconcepto().compareTo(codConceptoElegibleSinRiesCub) == 0) {
								if (null != cg.getDescvalor() && !cg.getDescvalor().trim().isEmpty()) {
									res = cg.getDescvalor().trim();
									break;
								}
							}
						}
					}
				}
			}
		}
		return res;
	}

	private static String getFilaPorDefecto(int columnas) {
		StringBuffer res = new StringBuffer("<tr>");
		res.append("<td class=\"literalborde\" colspan=\"");
		res.append(columnas + 2); // Sumamos dos para incluir las columnas de "garantia" y "riesgos cubiertos"
		res.append("\">Sin riesgos cubiertos elegibles</td></tr>");
		return res.toString();
	}

	/* Pet. 63485 ** MODIF TAM (23/07/2020) ** Inicio */
	/*
	 * Declaramos todas las funciones necesarias para montar la tabla de coberturas
	 * de Agricolas
	 */

	// Tabla de coberturas de Agricolas elegidas para la pagina de errores y de
	// importes #################
	public static String getComparativaHeader(List<com.rsi.agp.dao.tables.poliza.ComparativaPoliza> listaComparativas,
			ModuloView mv, boolean esGanado) {
		
		logger.debug("WSUtils - getComparativaHeader [INIT] V1.2");

		StringBuffer html = new StringBuffer("");
		html.append("<table width=\"100%\">");
		html.append(getTitulosColumnasCoberturas(mv.getListaCabeceras()));
		html.append(esGanado ? getFilasCoberturasGanado(mv, listaComparativas, new Long(mv.getNumComparativa()))
				: getFilasCoberturasAgricolas(mv, listaComparativas, new Long(mv.getNumComparativa())));
		html.append("</table>");
		
		logger.debug("WSUtils - getComparativaHeader [INIT]");
		logger.debug("Valor de html:"+html.toString());
		return html.toString();
	}

	private static String getTitulosColumnasCoberturas(List<String> cabeceras) {
		String tr = "<tr>";
		tr += "<td class='literalbordeCabecera' align='center' valign='' style='width:17%;'>GARANT&Iacute;A</td>";
		tr += "<td class='literalbordeCabecera' align='center' valign='' style='width:17%;'>RIESGOS CUBIERTOS</td>";
		int anchoCabecera3 = 66 / cabeceras.size();
		for (String cab : cabeceras) {
			tr += "<td class='literalbordeCabecera' align='center' valign='' style='width:" + anchoCabecera3 + "%;'>"
					+ cab + TD;
		}
		tr += "</tr>";
		return tr;
	}

	private static String getFilasCoberturasAgricolas(ModuloView mv,
			List<com.rsi.agp.dao.tables.poliza.ComparativaPoliza> listaComparativas, Long idModulo) {

		List<String> listaFilas = new ArrayList<String>();
		String res = new String();
		
		logger.debug("WSUtils - getFilasCoberturasAgricola [INIT]");
		
		logger.debug(" Valor de mv.getListaFilas.size: "+mv.getListaFilas().size());

		for (ModuloFilaView mfv : mv.getListaFilas()) {
			logger.debug("Entramos el primer for");
			ComparativaPoliza cp = null;
			
			logger.debug(" Valor de listaComparativas.size: "+listaComparativas.size());
			for (ComparativaPoliza cpAux : listaComparativas) {
				logger.debug("Entramos el SegundoFor for");
				if (mv.getCodModulo().equals(cpAux.getId().getCodmodulo())
						&& mv.getNumComparativa().equals(cpAux.getId().getIdComparativa().intValue())
						&& mfv.getCodConceptoPrincipalModulo().equals(cpAux.getId().getCodconceptoppalmod())
						&& mfv.getCodRiesgoCubierto().equals(cpAux.getId().getCodriesgocubierto())) {
					cp = cpAux;
					break;
				}
			}
			String fila = getHtmlFilaCoberturas(cp, mfv, mv, listaComparativas);
			if (!"".equals(fila))
				listaFilas.add(fila);
		}

		if (listaFilas.size() == 0) {
			String fila = getFilaPorDefecto(mv.getListaCabeceras().size());
			listaFilas.add(fila);
		}

		for (String fila : listaFilas) {
			res += fila;
		}
		return res;
	}
	/* Pet. 63485 ** MODIF TAM (23/07/20209 ** Fin */

	/* Pet.50776_63485-Fase II ** MODIF TAM (20.10.2020) ** Inicio */
	public static String generateXMLPolizaModulosCoberturasAgri(Poliza poliza, Parcela parc, String codModulo,
			IPolizaDao polizaDao, List<BigDecimal> codsConceptos)
			throws ValidacionPolizaException, DAOException, BusinessException {

		logger.debug("WSUtils - generateXMLPolizaModulosCoberturasAgri [INIT]");

		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;

		GregorianCalendar fechaI = new GregorianCalendar();

		// No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		// Parcela Datos Variables
		

		Map<Long, DatosVariables> mapDvEspecialesExplotacion = new HashMap<Long, DatosVariables>();
		List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();

		es.agroseguro.presupuestoContratacion.PolizaDocument xmlPoliza = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();

		/* Llamamos al metodo tanto para Ganado como para las polizas Agricolas */
		logger.debug("Antes de montar el xmlPoliza");
		
		xmlPoliza = PolizaUnificadaTransformer.transformarPolizaModulosYCoberturasAgri(poliza, codsConceptos, polizaDao,
				parc, codModulo, gruposNegocio, mapDvEspecialesExplotacion);

		String envio = cabecera
				+ xmlPoliza.toString().replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
						.replace(XML_FRAGMENT2, PKS_POLIZA);

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		return envio;

	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (20.10.2020) ** Inicio */
	public static String generateXMLPolizaModulosCoberturasAgriAnx(AnexoModificacion anexo, Poliza poliza, Parcela parc,
			com.rsi.agp.dao.tables.anexo.Parcela parcAnexo, Set<com.rsi.agp.dao.tables.anexo.Parcela> listaParcAnexo,
			String codModulo, IPolizaDao polizaDao, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException, DAOException, BusinessException {

		logger.debug("WSUtils - generateXMLPolizaModulosCoberturasAgriAnx [INIT]");

		String cabecera = XML_VERSION_1_0_ENCODING_UTF_8;
		String namespace = HTTP_WWW_AGROSEGURO_ES_SEGURO_AGRARIO_CALCULO_SEGURO_AGRARIO;

		GregorianCalendar fechaI = new GregorianCalendar();

		// No hay que mandar campos en el XML que no vengan
		// indicados en el organizador para el uso 31- Poliza y ubicacion 16-
		// Parcela Datos Variables
		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
		
		List<GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();

		es.agroseguro.presupuestoContratacion.PolizaDocument xmlPoliza = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();

		logger.debug("Antes de obtener los codsConceptos");
		Long lineaSeguroId = poliza.getLinea().getLineaseguroid();

		codsConceptos = polizaDao.getCodsConceptoOrganizador(lineaSeguroId);
		logger.debug("Despues de obtener los codsConceptos");

		/* Llamamos al metodo tanto para Ganado como para las polizas Agricolas */
		logger.debug("Antes de montar el xmlPoliza");
	
		xmlPoliza = PolizaUnificadaTransformer.transformarPolizaModulosYCoberturasAgriAnx(poliza, codsConceptos, polizaDao,
				parc, parcAnexo, listaParcAnexo, codModulo, gruposNegocio, listaDatosVariables);

		String envio = cabecera
				+ xmlPoliza.toString().replace(XML_FRAGMENT, PKS_POLIZA_XMLNS_PKS + namespace + "\"")
						.replace(XML_FRAGMENT2, PKS_POLIZA);

		logger.debug(XML_GENERADO + envio);

		GregorianCalendar fechaF = new GregorianCalendar();
		Long tiempo = fechaF.getTimeInMillis() - fechaI.getTimeInMillis();
		logger.debug(TIEMPO_DE_GENERACION_DEL_XML + tiempo + MILISEGUNDOS);

		return envio;

	}

	/**
	 * 
	 * @param colectivoXml
	 * @throws RegistrarColectivoException 
	 */
	public static es.agroseguro.colectivo.ColectivoDocument getXMLColectivo(String colectivoXml) throws RegistrarColectivoException {
		
		logger.debug("XmlTransformerUtil - getXMLColectivo - init");

		// Se valida el XML antes de llamar al Servicio Web
		es.agroseguro.colectivo.ColectivoDocument colectivoDocument = null;

		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);

		try {
			colectivoDocument = es.agroseguro.colectivo.ColectivoDocument.Factory.parse(colectivoXml);

		} catch (XmlException e1) {
			throw new RegistrarColectivoException(ERROR_AL_CONVERTIR_EL_XML_A_XML_BEAN, e1);
		}

		boolean bValidation = colectivoDocument.validate(validationOptions);

		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema Colectivo.xsd");
			Iterator<XmlError> iter = validationErrors.iterator();
			StringBuffer cadError = new StringBuffer();

			while (iter.hasNext()) {
				XmlError err = iter.next();
				logger.error(">> " + err.getMessage());
				cadError.append(err.getMessage());

			}

			throw new RegistrarColectivoException(cadError.toString());
		}
		
		logger.debug("XmlTransformerUtil - getXMLColectivo - end");
		
		return colectivoDocument;
		
	}

	//P0079361
	public static Date parseoFechaRangos(String fechaString) {
		Date fecha = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		try {
            fecha = formato.parse(fechaString);
            System.out.println("Fecha: " + fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return fecha;
	}
	
	public static Date configHoraInit(Date fechaInit) {
        Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(fechaInit);
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calInicio.set(Calendar.SECOND, 0);
            calInicio.set(Calendar.MILLISECOND, 0);
            
        return calInicio.getTime();
    }
    
    public static Date configHoraFin(Date fechaFin) {
        Calendar calFin = Calendar.getInstance();
            calFin.setTime(fechaFin);
            calFin.set(Calendar.HOUR_OF_DAY, 23);
            calFin.set(Calendar.MINUTE, 59);
            calFin.set(Calendar.SECOND, 59);
            calFin.set(Calendar.MILLISECOND, 999);
            
        return calFin.getTime();
    }
	
	public static boolean logicaObtenerNulosFechas(String fechaInit, String fechaFin) {
		boolean obtenerNulos = false;
		
		Date fechaActual = Calendar.getInstance().getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String fechaFinToCompare = dateFormat.format(fechaActual);
		
		if(Constants.STR_FECHA_INI.equals(fechaInit) && fechaFinToCompare.equals(fechaFin)) {
			obtenerNulos = true;
		}
		
		return obtenerNulos;
		
	}
	
	public static String obtenerCodEstadoCuponByNumber(String id) {
		int number = Integer.parseInt(id);
		String estadoCupon = Constants.STR_EMPTY;
		
		switch (number) {
		case 1:
			estadoCupon = Constants.AM_CUPON_ESTADO_ABIERTO_S;
			break;
		case 2:
			estadoCupon = Constants.AM_CUPON_ESTADO_CADUCADO_S;
			break;
		case 3:
			estadoCupon = Constants.AM_CUPON_ESTADO_ERROR_S;		
			break;
		case 4:
			estadoCupon = Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO_S;
			break;
		case 5:
			estadoCupon = Constants.AM_CUPON_ESTADO_ERROR_TRAMITE_S;
			break;
		case 6:
			estadoCupon = Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE_S;
			break;
		case 7:
			estadoCupon = Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO_S;
			break;
		}
		
		return estadoCupon;
	}
	
	/*public static boolean estadoCuponRCisNotEmpty(String estado) {
		String boolean = Constants.STR_EMPTY;
		
		switch (number) {
		case 1:
			estadoCupon = Constants.AM_CUPON_ESTADO_ABIERTO_S;
			break;
		case 2:
			estadoCupon = Constants.AM_CUPON_ESTADO_CADUCADO_S;
			break;
		case 3:
			estadoCupon = Constants.AM_CUPON_ESTADO_ERROR_S;		
			break;
		case 4:
			estadoCupon = Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO_S;
			break;
		case 5:
			estadoCupon = Constants.AM_CUPON_ESTADO_ERROR_TRAMITE_S;
			break;
		case 6:
			estadoCupon = Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE_S;
			break;
		case 7:
			estadoCupon = Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO_S;
			break;
		}
		
		return estadoCupon;
	}*/
	//P0079361	
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (20.10.2020) ** Fin */
	
	/**
	 * Entrada: ObjectFactory del tipo del servicio y un String con el xml que
	 * devuelve el servicio.
	 * 
	 * Devuelve un objecto con la informacion introducida
	 * 
	 * @param object
	 * @param response
	 * @return
	 */
	public static Object getUnMarshaller(Object object, String response) {
		JAXBContext jc;
		JAXBElement<Object> jbets = null;
		try {
			jc = JAXBContext.newInstance(object.getClass().getPackage().getName());
			
			Unmarshaller ums = jc.createUnmarshaller();
			jbets = (JAXBElement<Object>)ums.unmarshal(new InputSource(new StringReader(response)));
			
			return jbets.getValue();
		} catch (JAXBException e) {
			return null;
		}
	}
}