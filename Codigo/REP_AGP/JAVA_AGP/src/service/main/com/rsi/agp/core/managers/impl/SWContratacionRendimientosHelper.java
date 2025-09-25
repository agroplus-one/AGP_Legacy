package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

import es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException;
import es.agroseguro.serviciosweb.contratacionscrendimientos.CalcularRendimientosRequest;
import es.agroseguro.serviciosweb.contratacionscrendimientos.CalcularRendimientosResponse;
import es.agroseguro.serviciosweb.contratacionscrendimientos.ContratacionSCRendimientos;
import es.agroseguro.serviciosweb.contratacionscrendimientos.ContratacionSCRendimientos_Service;
import es.agroseguro.tipos.AjustarProducciones;

public class SWContratacionRendimientosHelper {

	/*** SONAR Q ** MODIF TAM (24.11.2021) ***/
	/**
	 * - Se ha eliminado todo el código comentado - Se crean metodos nuevos para
	 * descargar de ifs/fors - Se crean constantes locales nuevas
	 **/

	/** CONSTANTES SONAR Q ** MODIF TAM (24.11.2021) ** Inicio **/
	private final static String LITERAL = "Se esperaba un XML en formato ";
	/** CONSTANTES SONAR Q ** MODIF TAM (24.11.2021) ** Inicio **/

	private static final Log logger = LogFactory.getLog(SWContratacionRendimientosHelper.class);

	public com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse getProduccionRendimientos(
			CalcularRendimientosRequest params, String realPath, String codUsuario) throws AgrException, Exception {

		logger.debug("[ESC-28227] getProduccionRendimientos [BEGIN]");
		CalcularRendimientosResponse respuesta = null;
		ContratacionSCRendimientos srvContratacionSCRendimientos = getSrvUbicacionRendimientos(realPath);
		com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse respuestaTransformada = null;

		try {
			// Llamada al SW
			respuesta = srvContratacionSCRendimientos.calcularRendimientos(params);

			if (respuesta != null) {
				respuestaTransformada = transformarRespuesta(respuesta);
				logger.debug("[ESC-28227] respuesta: " + respuestaTransformada.getRendimientoPolizaDocument().xmlText());
			}

		} catch (AgrException e) {
			// El servicio ha devuelto una excepcion => tratar el error e informar al usuario
			throw e;

		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			throw e;

		} catch (Exception ex) {
			logger.error(
					"Error inesperado al llamar al servicio web de Rendimientos de Producción - getProduccionRendimientos",
					ex);
			throw ex;
		}
		logger.debug("[ESC-28227] getProduccionRendimientos [END]");
		return respuestaTransformada;
	}

	private ContratacionSCRendimientos getSrvUbicacionRendimientos(String realPath) {
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		URL wsdlLocation = null;
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("rendimientos.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException(
					"Imposible recuperar el WSDL de Rendimientos de Producción. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("rendimientos.location");
		String wsPort = WSUtils.getBundleProp("rendimientos.port");
		String wsService = WSUtils.getBundleProp("rendimientos.service");

		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		// Crea el envoltorio para la llamada al servicio web

		ContratacionSCRendimientos_Service srv = new ContratacionSCRendimientos_Service(wsdlLocation, serviceName);

		ContratacionSCRendimientos srvContratacionSCRendimientos = srv.getPort(portName,
				ContratacionSCRendimientos.class);
		logger.debug(srvContratacionSCRendimientos.toString());

		// Añade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvContratacionSCRendimientos);

		return srvContratacionSCRendimientos;
	}

	private com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse transformarRespuesta(
			CalcularRendimientosResponse response) throws XmlException, IOException {

		com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse respuesta = new com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse();

		Base64Binary relacionIncidencias = response.getCalcularRendimientos();
		byte[] byteArray = relacionIncidencias.getValue();
		if (byteArray != null && byteArray.length > 0) {
			String xmlData = new String(byteArray, Constants.DEFAULT_ENCODING);

			es.agroseguro.seguroAgrario.rendimientosCalculo.PolizaDocument eqRendimientos = es.agroseguro.seguroAgrario.rendimientosCalculo.PolizaDocument.Factory
					.parse(new StringReader(xmlData));

			respuesta.setRendimientoPolizaDocument(eqRendimientos);
		}
		return respuesta;
	}

	/**
	 * Obtiene el objeto request a partir de una póliza y una parcela. Si la parcela
	 * es distinto de null, sólo incluiremos dicha parcela.
	 * 
	 * @param poliza
	 * @param parcelaVO
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 * @throws ValidacionPolizaException
	 */
	public static CalcularRendimientosRequest obtenerRendimientosRequest(String xmlPoliza, boolean conAjuste,
			AjustarProducciones ajusteProduccion) throws ValidacionPolizaException, DAOException, BusinessException {

		// Parametros a enviar al SW
		CalcularRendimientosRequest params = new CalcularRendimientosRequest();

		byte[] array = null;
		Base64Binary base64BinaryPoliza = new Base64Binary();
		base64BinaryPoliza.setContentType("text/xml");
		try {
			array = xmlPoliza.getBytes(Constants.DEFAULT_ENCODING);
			base64BinaryPoliza.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error(LITERAL + Constants.DEFAULT_ENCODING, e2);
			throw new CalculoServiceException(LITERAL + Constants.DEFAULT_ENCODING, e2);
		}

		params.setPoliza(base64BinaryPoliza);
		params.setAjuste(conAjuste);

		if (conAjuste) {
			es.agroseguro.serviciosweb.contratacionscrendimientos.ObjectFactory o = new es.agroseguro.serviciosweb.contratacionscrendimientos.ObjectFactory();
			AjustarProducciones ajustarProducciones = ajusteProduccion;
			JAXBElement<AjustarProducciones> ajustarProduccionesJaxb = o
					.createCalcularRendimientosRequestAjustarProducciones(ajustarProducciones);
			params.setAjustarProducciones(ajustarProduccionesJaxb);
		}
		return params;
	}

	/**
	 * Obtiene el objeto request a partir de una póliza y una parcela. Si la parcela
	 * es distinto de null, sólo incluiremos dicha parcela.
	 * 
	 * @param poliza
	 * @param parcelaVO
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 * @throws ValidacionPolizaException
	 */
	public static CalcularRendimientosRequest obtenerRendimientosRequest(String xmlPoliza, boolean conAjuste,
			AjustarProducciones ajusteProduccion, boolean ajustarHistorico)
			throws ValidacionPolizaException, DAOException, BusinessException {

		// Parametros a enviar al SW
		CalcularRendimientosRequest params = new CalcularRendimientosRequest();

		/* Pet. 57626 ** MODIF TAM ** (25/05/2020) ** Inicio */
		// Cogemos el XML de la BBDD, el SW de Rendimiento debe ir con el esquema
		// PresupuestoContratación */
		logger.debug("Obtenemos en un Clob el XML generado");

		// No se recupero el XML...
		if (xmlPoliza == null)
			throw new CalculoServiceException("No se ha podido obtener el XML de la Póliza");

		xmlPoliza = xmlPoliza.replaceAll("xmlns:pks=\"http://www.agroseguro.es/SeguroAgrario/CalculoSeguroAgrario\"",
				"xmlns:pks=\"http://www.agroseguro.es/PresupuestoContratacion\"");

		WSUtils.getXMLCalculoUnificado(xmlPoliza);

		logger.debug("Llamando a Servicio de Rendimientos: RENDIMIENTOS>> " + xmlPoliza);
		/* Pet. 57626 ** MODIF TAM ** (25/05/2020) ** Fin */

		byte[] array = null;
		Base64Binary base64BinaryPoliza = new Base64Binary();
		base64BinaryPoliza.setContentType("text/xml");
		try {
			array = xmlPoliza.getBytes(Constants.DEFAULT_ENCODING);
			base64BinaryPoliza.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error(LITERAL + Constants.DEFAULT_ENCODING, e2);
			throw new CalculoServiceException(LITERAL + Constants.DEFAULT_ENCODING, e2);
		}

		params.setPoliza(base64BinaryPoliza);
		params.setAjuste(true);

		es.agroseguro.serviciosweb.contratacionscrendimientos.ObjectFactory o = new es.agroseguro.serviciosweb.contratacionscrendimientos.ObjectFactory();
		if (conAjuste) {
			AjustarProducciones ajustarProducciones = ajusteProduccion;
			JAXBElement<AjustarProducciones> ajustarProduccionesJaxb = o
					.createCalcularRendimientosRequestAjustarProducciones(ajustarProducciones);
			params.setAjustarProducciones(ajustarProduccionesJaxb);
		}
		JAXBElement<Boolean> a = o.createCalcularRendimientosRequestAjustarHistoricos(Boolean.TRUE);
		params.setAjustarHistoricos(a);
		return params;
	}

	public static String polizaRendimientosToXml(Long codtipoCapital, com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ModuloPoliza mp, ICPMTipoCapitalDAO cpmTipoCapitalDao, IPolizaDao polizaDao, Set<Long> colIdParcelasFiltro)
			throws DAOException, ValidacionPolizaException, BusinessException {

		String xmlPoliza = null;
		List<BigDecimal> listaCPM = new ArrayList<BigDecimal>();
		ComparativaPoliza cp = null;
		List<ComparativaPoliza> lstComparativas = polizaDao.getLstCompPolizas(poliza.getIdpoliza(),
				mp.getId().getCodmodulo());
		if (lstComparativas != null && !lstComparativas.isEmpty()) {
			for (ComparativaPoliza cpAux : lstComparativas) {
				if (cpAux.getId().getIdComparativa().equals(mp.getId().getNumComparativa())) {
					cp = cpAux;
					logger.debug("El xml se montará con una comparativa de póliza");
					break;
				}
			}
		}
		if (cp == null) {
			logger.debug("El xml se montará con una comparativa de póliza");
			cp = new ComparativaPoliza();
			cp.setId(new ComparativaPolizaId());
			cp.getId().setCodmodulo(mp.getId().getCodmodulo());
			cp.getId().setFilacomparativa(BigDecimal.ONE);
			cp.getId().setIdComparativa(mp.getId().getNumComparativa());
			cp.getId().setIdpoliza(poliza.getIdpoliza());
			cp.getId().setLineaseguroid(poliza.getLinea().getLineaseguroid());
		}

		listaCPM = cpmTipoCapitalDao.getCPMDePoliza(codtipoCapital, poliza.getIdpoliza(), mp.getId().getCodmodulo());
		logger.debug("listaCPM -> " + listaCPM);
		xmlPoliza = WSUtils.generateXMLPoliza(poliza, cp, "rendimiento", polizaDao, listaCPM, poliza.getUsuario(), true,
				colIdParcelasFiltro, false, null);

		return xmlPoliza;
	}

	public static String polizaRendimientosAnexoToXml(AnexoModificacion anexoMod, Set<Long> colIdParcelasParaRecalculo,
			Usuario usuario, IXmlAnexoModificacionDao xmlAnexoModDao, ICPMTipoCapitalDAO cpmTipoCapitalDao,
			IPolizaCopyDao polizaCopyDao, boolean calcRendOriHist) throws Exception {

		String xmlPoliza = null;

		Map<BigDecimal, List<String>> listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(anexoMod);
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(anexoMod.getPoliza().getIdpoliza(),
				anexoMod.getId(), anexoMod.getCodmodulo());
		List<BigDecimal> codsConceptos = polizaCopyDao
				.getCodsConceptoOrganizador(anexoMod.getPoliza().getLinea().getLineaseguroid());

		xmlPoliza = XmlTransformerUtil.generateXMLAnexoModWSRecalculo(anexoMod, null, listaDatosVariables, listaCPM,
				codsConceptos, usuario, colIdParcelasParaRecalculo, calcRendOriHist);

		return xmlPoliza;

	}
}