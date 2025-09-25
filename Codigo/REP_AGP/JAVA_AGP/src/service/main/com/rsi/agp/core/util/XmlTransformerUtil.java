package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.exception.ValidacionSiniestroException;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.PolizaReduccionCapital;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.poliza.IReduccionCapitalDao;
import com.rsi.agp.dao.models.poliza.ReduccionCapitalDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.copy.Poliza;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;


public class XmlTransformerUtil {

	private static Log logger = LogFactory.getLog(XmlTransformerUtil.class);
	
	/*** SONAR Q ** MODIF TAM(14.11.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/
	
	/** CONSTANTES SONAR Q ** MODIF TAM (11.11.2021) ** Inicio **/
	private final static String CAB_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private final static String FRAGMENT = "<xml-fragment";
	private final static String POL_XML = "<pks:Poliza xmlns:pks=\"";
	private final static String END_FRAGMENT = "</xml-fragment>";
	private final static String END_POL_XML = "</pks:Poliza>";
	/** CONSTANTES SONAR Q ** MODIF TAM (11.11.2021) ** Fin **/
	
	public static void updateXMLAnexoMod(
			IXmlAnexoModificacionDao xmlAnexoModDao,
			IPolizaCopyDao polizaCopyDao, AnexoModificacion anexo,
			boolean modAseg, List<BigDecimal> listaCPM,
			boolean validarEstructuraXml) throws ValidacionAnexoModificacionException,DAOException,BusinessException{

		String xml = "";

		try {
			/*
			 * TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
			 * indicados en el organizador para el uso 31 Poliza y ubicacion
			 * 16- Parcela Datos Variables
			 */
			List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
			codsConceptos = polizaCopyDao.getCodsConceptoOrganizador(anexo.getPoliza().getLinea().getLineaseguroid());

			// Actualizamos el xml que se enviara a Agroseguro
			Map<BigDecimal, List<String>> listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(anexo);

			// Guardo el anexo  para que se pueda generar
			// correctamente el informe en PDF.
			xmlAnexoModDao.saveOrUpdate(anexo);

			Poliza copy = null;
			if (anexo.getIdcopy() != null) {
				copy = polizaCopyDao.getPolizaCopyById(anexo.getIdcopy());
			}

			xml = XmlTransformerUtil.generateXMLAnexoMod(anexo, copy, listaDatosVariables, listaCPM, codsConceptos,anexo.getPoliza().getUsuario());
		} catch (ValidacionAnexoModificacionException e1) {
			logger.error("Error al validar el xml de anexo de modificacion", e1);
			throw new ValidacionAnexoModificacionException(e1.getMessage());
		} catch (Exception e) {
			logger.error("Error al generar el xml de anexo de modificacion", e);
			throw new BusinessException("Error al generar el xml de anexo de modificacion: " + e.getMessage());
		}
		// DAA 08/05/12 validar estructura xml
		if (validarEstructuraXml) {
			try {

				WSUtils.getXMLAnexoModificacion(xml);
				xmlAnexoModDao.saveXmlAnexoModificacion(anexo.getId(), xml);

			} catch (ValidacionAnexoModificacionException e1) {
				logger.error("Error al validar el xml de anexo de modificacion", e1);
				throw new ValidacionAnexoModificacionException(e1.getMessage());
			} catch (DAOException e){
				logger.error("Error al guardar el xml de anexo de modificacion", e);
				throw new DAOException(e.getMessage());
			}
		}
		logger.debug("updateXMLAnexoMod: "+ xml);
		// Actualiza el anexo con el nuevo xml para que cuando se grabe el objeto no se pierdan los cambios
		anexo.setXml(Hibernate.createClob(xml));
	}
	public static void updateXMLRedCap(
			ReduccionCapital redCap, IReduccionCapitalDao reduccionCapitalDao) throws ValidacionAnexoModificacionException,DAOException,BusinessException{
		
		String xml = "";
		
		try {
			// Guardo el anexo  para que se pueda generar
			// correctamente el informe en PDF.			
			xml = XmlTransformerUtil.generateXMLRedCap(redCap);
		} catch (ValidacionAnexoModificacionException e1) {
			logger.error("Error al validar el xml de anexo de modificacion", e1);
			throw new ValidacionAnexoModificacionException(e1.getMessage());
		} catch (Exception e) {
			logger.error("Error al generar el xml de anexo de modificacion", e);
			throw new BusinessException("Error al generar el xml de anexo de modificacion: " + e.getMessage());
		}
		
		logger.debug("updateXMLRedCap: "+ xml);
		// Actualiza el anexo con el nuevo xml para que cuando se grabe el objeto no se pierdan los cambios
		redCap.setXml(Hibernate.createClob(xml));
		reduccionCapitalDao.saveOrUpdate(redCap);
	}

	/**
	 * Metodo para generar el asunto del anexo de modificacion
	 * 
	 * @param anexo Anexo de modificacion.
	 * @param modAseg indica si se ha modificado el asegurado o no.
	 * @return Cadena de texto con los codigos de asunto separados por ';'
	 */
	public static String generarAsuntoAnexo(AnexoModificacion anexo,
			boolean modAseg) {
		String asuntos = "";
		if (modAseg)
			asuntos = "DOMICM;";

		/* SONAR Q */
		asuntos = obtenerAsunto(anexo, asuntos);
		/* SONAR Q - Fin */
		
		boolean alta = false;
		boolean modificacion = false;
		boolean baja = false;
		
		for (SubvDeclarada subvencion : anexo.getSubvDeclaradas()) {
			if (!alta && subvencion.getTipomodificacion() != null
					&& subvencion.getTipomodificacion().equals('A')) {
				alta = true;
				asuntos += "SUBVEA;";
			} else if (!modificacion
					&& subvencion.getTipomodificacion() != null
					&& subvencion.getTipomodificacion().equals('M')) {
				modificacion = true;
				asuntos += "SUBVEM;";
			} else if (!baja && subvencion.getTipomodificacion() != null
					&& subvencion.getTipomodificacion().equals('B')) {
				baja = true;
				asuntos += "SUBVEB;";
			}
		}

		if (anexo.getCoberturas().size() > 0) {
			asuntos += "OTROS;";
		}

		return asuntos;
	}

	/**
	 * Genera el XML del anexo de modificacion de una poliza principal
	 * 
	 * @param anexo Anexo de modificacion
	 * @param copy Copy asociada al anexo, si la tiene.
	 * @param listaDatosVariables
	 *            Lista con los datos variables de parcela que dependen del
	 *            conepto principal del modulo y del riesgo cubierto.
	 * @return Identificador del envio.
	 */
	public static String generateXMLAnexoMod(AnexoModificacion anexo,
			Poliza copy, Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos, Usuario usuario)
			throws Exception {
		
		logger.info("init - generateXMLAnexoMod");

		String cabecera = CAB_XML;
		String namespace = "http://www.agroseguro.es/SeguroAgrario/Modificacion";
		
		es.agroseguro.seguroAgrario.modificacion.Poliza p = null;
		// 1. Transformacion del anexo de BD para llamar al servicio web
		p = AnexoModificacionTransformer.transformar(anexo, copy, listaDatosVariables, listaCPM, codsConceptos, usuario);

		String resultado = cabecera + p.toString().replaceAll(FRAGMENT, POL_XML + namespace + "\"")
						.replaceAll(END_FRAGMENT, END_POL_XML);

		logger.info("Xml de Anexo de modificacion: " + resultado);
		logger.info("end - generateXMLAnexoMod");
		
		return resultado;
	}
	/**
	 * Genera el XML de Reducicones de capital de una polizaReduccion
	 * 
	 * @param redCap Anexo Reduccion de Capital
	 * @return Identificador del envio.
	 */
	public static String generateXMLRedCap(ReduccionCapital redCap)
					throws Exception {
		
		logger.info("init - generateXMLAnexoMod");
		
		String cabecera = CAB_XML;
		String namespace = "http://www.agroseguro.es/SeguroAgrario/Modificacion";
		
		//PolizaReduccionCapital polizaRedCap = null;
		PolizaReduccionCapital polizaRedCap = null;
		// 1. Transformacion del anexo de BD para llamar al servicio web
		polizaRedCap = ReduccionCapitalTransformer.transformar(redCap);
		
		String resultado = cabecera + polizaRedCap.toString().replaceAll(FRAGMENT, POL_XML + namespace + "\"")
				.replaceAll(END_FRAGMENT, END_POL_XML);
		
		logger.info("Xml de Anexo de modificacion: " + resultado);
		logger.info("end - generateXMLAnexoMod");
		
		return resultado;
	}


	/**
	 * Para el envío del XML de anexo d
	 * @param anexo
	 * @param copy
	 * @param listaDatosVariables
	 * @param listaCPM
	 * @param codsConceptos
	 * @param usuario
	 * @param colIdParcelasFiltro
	 * @return
	 * @throws Exception
	 */
	public static String generateXMLAnexoModWSRecalculo(AnexoModificacion anexo,
			Poliza copy, Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos, Usuario usuario, Set<Long> colIdParcelasFiltro,
			boolean calcRendOriHist)
			throws Exception {
		
		logger.info("init - generateXMLAnexoModWSRecalculo");

		
		String cabecera = CAB_XML;
		String namespace = "http://www.agroseguro.es/PresupuestoContratacion";
		
		es.agroseguro.contratacion.Poliza p = null;

		// 1. Transformacion del anexo de BD para llamar al servicio web
		p = AnexoModificacionTransformer.transformarParaEnvioWSRecalculo(anexo, copy, listaDatosVariables, listaCPM, codsConceptos, usuario, colIdParcelasFiltro, calcRendOriHist);

		String resultado = cabecera + p.toString().replaceAll(FRAGMENT, POL_XML + namespace + "\"")
						.replaceAll(END_FRAGMENT, END_POL_XML);

		logger.info("Xml de Anexo de modificacion (Rendimiento): " + resultado);
		logger.info("end - generateXMLAnexoModWSRecalculo");
		
		return resultado;
	}
	
	/**
	 * Genera el XML del anexo de modificacion de una poliza complementaria
	 * 
	 * @param anexo Anexo de modificacion
	 * @param copy Copy asociada al anexo, si la tiene.
	 * @param listaDatosVariables
	 *            Lista con los datos variables de parcela que dependen del
	 *            conepto principal del modulo y del riesgo cubierto.
	 * @return Identificador del envio.
	 * @throws ValidacionAnexoModificacionException 
	 */
	private static String generateXMLAnexoModCpl(AnexoModificacion anexo,
			Poliza copy, Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos,Usuario usuario) throws ValidacionAnexoModificacionException {
		logger.debug("init - generateXMLAnexoModCpl");

		String cabecera = CAB_XML;
		String namespace = "http://www.agroseguro.es/SeguroAgrario/Modificacion";

		// 1. Transformacion del anexo de BD para llamar al servicio web
		es.agroseguro.seguroAgrario.modificacion.Poliza p = AnexoModificacionTransformer
				.transformarCPL(anexo, copy, listaDatosVariables, listaCPM,
						codsConceptos,usuario);
		String resultado = cabecera
				+ p.toString().replaceAll(FRAGMENT,
						POL_XML + namespace + "\"")
						.replaceAll(END_FRAGMENT, END_POL_XML);

		logger.debug("Xml de Anexo de modificacion: " + resultado);

		logger.debug("end - generateXMLAnexoModCpl");
		return resultado;
	}

	public static void updateXMLAnexoModCpl(
			IXmlAnexoModificacionDao xmlAnexoModDao,
			IPolizaCopyDao polizaCopyDao, AnexoModificacion anexo,
			String moduloPPal, boolean modAseg, List<BigDecimal> listaCPM, boolean validarEstructura)
			throws BusinessException, DAOException,
			ValidacionAnexoModificacionException {
		logger.debug("init - updateXMLAnexoModCpl");
		String xml = "";

		try {
			/*
			 * TMR 10/12/2012 No hay que mandar campos en el XML que no vengan
			 * indicados en el organizador para el uso 31 â€“ Poliza y ubicacion
			 * 16- Parcela Datos Variables
			 */
			List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
			codsConceptos = polizaCopyDao.getCodsConceptoOrganizador(anexo.getPoliza().getLinea().getLineaseguroid());

			// Actualizamos el xml que se enviara a Agroseguro
			Map<BigDecimal, List<String>> listaDatosVariables = xmlAnexoModDao
					.getDatosVariablesParcelaRiesgoAnexoCPL(anexo.getId(), moduloPPal);

			// 1. Generar el asunto del anexo de modificacion
			anexo.setAsunto(generarAsuntoAnexo(anexo, modAseg));
			
			Poliza copy = null;
			if (anexo.getIdcopy() != null) {
				copy = polizaCopyDao.getPolizaCopyById(anexo.getIdcopy());
			}
			Usuario usuario = anexo.getPoliza().getUsuario();
			xml = XmlTransformerUtil.generateXMLAnexoModCpl(anexo, copy, listaDatosVariables, listaCPM, codsConceptos,usuario);

		} catch (ValidacionAnexoModificacionException e1) {
			logger.error("Error al validar el xml de anexo de modificacion CPL", e1);
			throw new ValidacionAnexoModificacionException(e1.getMessage());
		} catch (Exception e) {
			logger.error("Error al generar el xml de anexo de modificacion", e);
			throw new BusinessException("Error al generar el xml de anexo de modificacion: " + e.getMessage());
		}

		// DAA 08/05/12 validar estructura xml
		if (validarEstructura){
			try {
				WSUtils.getXMLAnexoModificacion(xml);
				// 2. Guardo el anexo con asunto para que se pueda generar
				// correctamente el informe en PDF.
				xmlAnexoModDao.saveOrUpdate(anexo);
	
			} catch (ValidacionAnexoModificacionException e1) {
				logger.error("Error al validar el xml de anexo de modificacion CPL", e1);
				throw new ValidacionAnexoModificacionException(e1.getMessage());
			} catch (Exception e ){
				logger.error("Error generico al validar el xml de anexo de modificacion CPL", e);
				throw new DAOException(e.getMessage());
			}

		}

		xmlAnexoModDao.saveXmlAnexoModificacion(anexo.getId(), xml);
		
		// Actualiza el anexo con el nuevo xml para que cuando se grabe el objeto no se pierdan los cambios
		anexo.setXml(Hibernate.createClob(xml));

		logger.debug("end - updateXMLAnexoModCpl");
	}
	
	/** 3-06-13 TMR
	 * Genera el XML de un siniestro de una poliza principal
	 * 
	 * @param siniestro Siniestro
	 * @return Identificador del envio.
	 */
	public static String generateXMLSiniestro(Siniestro siniestro, Asegurado asegurado)
			throws Exception {
		logger.debug("init - generateXMLSiniestro");
		
		es.agroseguro.seguroAgrario.siniestros.Siniestro s = SiniestroTransformer.transformar(siniestro, asegurado);
		String inicioPks = new StringBuilder("<pks:Siniestro xmlns:pks=\"http://www.agroseguro.es/SeguroAgrario/Siniestros\"").toString();
		String siniestroString = s.toString().replaceAll(FRAGMENT, inicioPks).replaceAll(END_FRAGMENT, "</pks:Siniestro>");
		String siniestroXml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(siniestroString).toString();
		
		logger.debug(new StringBuilder("Xml de Siniestro:\n").append(siniestroXml).toString());
		
		try {
			WSUtils.getXMLSiniestros(siniestroXml);		
		} catch (ValidacionSiniestroException e1) {
			logger.error("Error al validar el xml de siniestros", e1);
			throw new ValidacionSiniestroException(e1.getMessage());
		}catch (Exception e) {
			logger.error("Error inesperado al generar el XML de siniestros", e);
			throw new Exception(e.getMessage());
		}
		
		logger.debug("end - generateXMLSiniestro");
		return siniestroXml;
	}
	
	
	/** SONAR Q ** MODIF TAM(10.11.2021) ** Inicio **/
	private static String obtenerAsunto(AnexoModificacion anexo, String asuntos) {
		
		boolean alta = false;
		boolean modificacion = false;
		boolean baja = false;
		
		for (Parcela parcela : anexo.getParcelas()) {
			if (!alta && parcela.getTipomodificacion() != null
					&& parcela.getTipomodificacion().equals('A')) {
				alta = true;
				asuntos += "PARCEA;";
			} else if (!modificacion && parcela.getTipomodificacion() != null
					&& parcela.getTipomodificacion().equals('M')) {
				modificacion = true;
				asuntos += "PARCEM;";
			} else if (!baja && parcela.getTipomodificacion() != null
					&& parcela.getTipomodificacion().equals('B')) {
				baja = true;
				asuntos += "PARCEB;";
			}
		}
		return asuntos;
	}
	/** SONAR Q ** MODIF TAM(10.11.2021) ** Fin **/

	
	/**
	 * 
	 * @param colectivo
	 * @return
	 * @throws Exception 
	 */
	public static String generateXMLColectivo(Colectivo colectivo) throws Exception {
		
		logger.debug("XmlTransformerUtil - generateXMLColectivo - init");
		
		es.agroseguro.colectivo.Colectivo c = ColectivoTransformer.transformar(colectivo);
		String inicioPks = new StringBuilder("<pks:Colectivo xmlns:pks=\"http://www.agroseguro.es/Colectivo\"").toString();
		String colectivoStr = c.toString().replaceAll(FRAGMENT, inicioPks).replaceAll(END_FRAGMENT, "</pks:Colectivo>");
		String colectivoXml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(colectivoStr).toString();
		
		logger.debug(new StringBuilder("Xml de Colectivo:\n").append(colectivoXml).toString());
		
		try {
			WSUtils.getXMLColectivo(colectivoXml);		
		} catch (Exception e) {
			logger.error("Error inesperado al generar el XML de colectivo", e);
			throw new Exception(e.getMessage());
		}
		
		logger.debug("XmlTransformerUtil - generateXMLColectivo - end");
		
		return colectivoXml;
				
	}

}