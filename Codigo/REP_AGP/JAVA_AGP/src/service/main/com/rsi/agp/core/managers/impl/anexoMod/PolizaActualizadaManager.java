package com.rsi.agp.core.managers.impl.anexoMod;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.managers.impl.InformesManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.anexoMod.util.AnexoModificacionUtils;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaCplTranformer;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaGanadoTranformer;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaTranformer;
import com.rsi.agp.core.report.anexoMod.BeanExplotacion;
import com.rsi.agp.core.report.anexoMod.BeanExplotacion.BeanGrupoRaza;
import com.rsi.agp.core.report.anexoMod.BeanExplotacion.BeanGrupoRaza.BeanTipoCapital;
import com.rsi.agp.core.report.anexoMod.BeanExplotacion.BeanGrupoRaza.BeanTipoCapital.BeanTipoAnimal;
import com.rsi.agp.core.report.anexoMod.BeanExplotacion.BeanRiesgoCubiertoElegido;
import com.rsi.agp.core.report.anexoMod.BeanParcelaCapitalAsegurado;
import com.rsi.agp.core.report.anexoMod.DatoVariable;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.report.anexoMod.ResumenCosechaAsegurada;
import com.rsi.agp.core.report.anexoMod.ResumenValorAsegurable;
import com.rsi.agp.core.report.layout.BeanTablaCoberturas;
import com.rsi.agp.core.report.layout.DisenoTablaCobertParcelas;
import com.rsi.agp.core.report.layout.DisenoTablaCoberturas;
import com.rsi.agp.core.util.BeanRCbrtoElegidoComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.models.anexo.IDeclaracionModificacionPolizaDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.cgen.ISubvencionDeclaradaDao;
import com.rsi.agp.dao.models.commons.ITerminoDao;
import com.rsi.agp.dao.models.config.IDatoVariableDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.cpl.IModulosDao;
import com.rsi.agp.dao.models.cpl.IVariedadDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.ICaracteristicaExplotacionDao;
import com.rsi.agp.dao.models.poliza.IComparativaSitActDao;
import com.rsi.agp.dao.models.poliza.IConceptoPpalDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IRiesgoCubiertoModuloDao;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.CaracteristicaExplotacion;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.SubvencionDeclarada;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.EspecieId;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazasId;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejoId;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanadoId;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.explotacion.Animales;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.contratacion.explotacion.GrupoRaza;
import es.agroseguro.iTipos.IdentificacionCatastral;
import es.agroseguro.iTipos.SIGPAC;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class PolizaActualizadaManager implements IPolizaActualizadaManager {

	private static final Log logger = LogFactory.getLog(PolizaActualizadaManager.class);

	private static final String LISTA = "lista";
	private static final String JRXML = ".jrxml";
	private static final String POLIZA_TABLA_COBERT = "polizaActualizada.tabla_resumen_cobert_parc";
	private static final String JASPER = ".jasper";
	private static final String LOGGER_ERROR = "Error ";
	private static final String GET_VALOR = "getValor";
	private static final String FECHA_DESC = "yyyy-MM-dd";
	private static final String FECHA_ASC = "dd/MM/yyyy";
	
	private InformesManager informesManager;
	private IPolizaDao polizaDao;
	private IDiccionarioDatosDao diccionarioDatosDao;
	private ITerminoDao terminoDao;
	private IVariedadDao variedadDao;
	private IDatoVariableDao datoVariableDao;
	private IModulosDao modulosDao;
	private ISubvencionDeclaradaDao subvencionDeclaradaDao;
	private ICaracteristicaExplotacionDao caracteristicaExplotacionDao;
	private IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao;
	private ISolicitudModificacionManager solicitudModificacionManager;
	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private IComparativaSitActDao comparativaSitActDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	protected IAseguradoDao aseguradoDao;
	private IRiesgoCubiertoModuloDao riesgoCubiertoModuloDao;
	private IConceptoPpalDao conceptoPpalDao;
	private IAnexoModificacionDao anexoModificacionDao;
	/* Pet. 57626 ** MODIF TAM (27.04.2020) ** Inicio */
	private WebServicesManager webServicesManager;
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;


	/*
	 * Metodo para llamar al servicio de consulta de contratacion y mostrar un PDF
	 * con los datos actualizados de una poliza. Devuelve un objeto de tipo
	 * JasperPrint que sera el que se use en el controller para pintar el informe.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JasperPrint verPolizaActualizada(final String referencia, final BigDecimal plan, final String realPath,
			final String pathInformes, boolean imprimirAnexoWs, AnexoModificacion am, final boolean anexotieneCpl)
			throws SWConsultaContratacionException, AgrException, Exception {

		logger.debug("PolizaActualizadaManager - verPolizaActualizada");

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("agp_informes_jasper");
			PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
			String tituloInforme;
			Map<BigDecimal, List<String>> listaDatosVariables = new HashMap<BigDecimal, List<String>>();
			List<BigDecimal> listaCPM = new ArrayList<BigDecimal>();

			HashMap<Long, HashMap<String, List>> infoNumExpCobertParcelas = null;

			boolean esPolizaGanado = polizaDao.esPolizaGanado(referencia, plan);

			if (am != null && am.getId() != null) {
				if (!esPolizaGanado) {
					try {
						//listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(am);
						listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcela(am);
					} catch (BusinessException e1) {
						logger.error("Error al obtener la lista de datos variables dependientes del riesgo y cpm", e1);
					}
					try {
						logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: "
								+ am.getPoliza().getIdpoliza() + ", idAnexo: " + am.getId() + ", codModulo: "
								+ am.getPoliza().getCodmodulo());
						listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(am.getPoliza().getIdpoliza(), am.getId(),
								am.getPoliza().getCodmodulo());
					} catch (Exception e1) {
						logger.error("Error al obtener la lista de CPM permitidos", e1);
					}
				}
				// cargamos el anexo
				am = declaracionModificacionPolizaDao.getAnexoModifById(new Long(am.getId()));
			} else {
				imprimirAnexoWs = false;
			}

			// imprimir borrador A.M poliza principal
			if (imprimirAnexoWs) {
				logger.debug("Entramos en imprimirAnexoWs de la poliza Principal");
				if (esPolizaGanado) {
					es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
							.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon())).getPoliza();
					// Modifica la situacion actualizada de la poliza con los cambios del anexo de
					// modificacion
					PolizaActualizadaGanadoTranformer.generarPolizaSituacionFinalCompleta(poliza, am,
							listaDatosVariables, listaCPM, anexoModificacionDao, false);
					es.agroseguro.contratacion.PolizaDocument polizaDocument = es.agroseguro.contratacion.PolizaDocument.Factory
							.newInstance();
					polizaDocument.setPoliza(poliza);
					respuesta.setPolizaGanado(polizaDocument);
				} else {
					/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
					/*
					 * Por los desarrollos de esta peticion tanto las polizas agricolas como las de
					 * ganado iran por el mismo end-point y con formato Unificado
					 */
					XmlObject polizaDoc = this.solicitudModificacionManager
							.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon());

					/* Anexos de modificacion de contratacion con Formato Unificado (Nuevas) */
					if (polizaDoc instanceof es.agroseguro.contratacion.PolizaDocument) {
						es.agroseguro.contratacion.Poliza polizaUnif = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc)
								.getPoliza();
						PolizaActualizadaTranformer.generarPolizaSituacionFinalCompletaAgri(polizaUnif, am,
								listaDatosVariables, listaCPM, false);
						es.agroseguro.contratacion.PolizaDocument polizaDocument = es.agroseguro.contratacion.PolizaDocument.Factory
								.newInstance();
						polizaDocument.setPoliza(polizaUnif);
						respuesta.setPolizaPrincipalUnif(polizaDocument);

						/* Pet. 57626 ** MODIF TAM (09.07.2020) ** Inicio */
						/*
						 * Por los desarrollos de esta peticion tanto las polizas agricolas como las de
						 * ganado iran por el mismo end-point y con formato Unificado
						 */
						es.agroseguro.seguroAgrario.contratacion.Poliza polizaPrincipal = es.agroseguro.seguroAgrario.contratacion.Poliza.Factory
								.newInstance();
						PolizaActualizadaTranformer.generarPolizaSitFinalCompletaAgricAnt(polizaPrincipal, am,
								listaDatosVariables, listaCPM);

						es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaPpalDocument = es.agroseguro.seguroAgrario.contratacion.PolizaDocument.Factory
								.newInstance();

						/* Datos a pasar a la estructura de Poliza Principal */

						/* Plan y Referencia */
						polizaPrincipal.setPlan(respuesta.getPolizaPrincipalUnif().getPoliza().getPlan());
						polizaPrincipal.setReferencia(respuesta.getPolizaPrincipalUnif().getPoliza().getReferencia());

						/* Datos Cobertura */
						es.agroseguro.seguroAgrario.contratacion.Cobertura coberturaPpal = es.agroseguro.seguroAgrario.contratacion.Cobertura.Factory
								.newInstance();

						String modulo = respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo();
						coberturaPpal.setModulo(modulo);

						polizaPrincipal.setCobertura(coberturaPpal);
						
						/* Datos Colectivo */
						es.agroseguro.seguroAgrario.contratacion.Colectivo colectivoPpal = es.agroseguro.seguroAgrario.contratacion.Colectivo.Factory
								.newInstance();

						int dcCol = respuesta.getPolizaPrincipalUnif().getPoliza().getColectivo().getDigitoControl();
						String referenciaCol = respuesta.getPolizaPrincipalUnif().getPoliza().getColectivo()
								.getReferencia();
						colectivoPpal.setDigitoControl(dcCol);
						colectivoPpal.setReferencia(referenciaCol);

						polizaPrincipal.setColectivo(colectivoPpal);

						polizaPpalDocument.setPoliza(polizaPrincipal);

						respuesta.setPolizaPrincipal(polizaPpalDocument);

					} else {
						/* Anexos de Modificacion de contratacion SIN Formato Unificado (Antiguas) */
						es.agroseguro.seguroAgrario.contratacion.Poliza polizaAnt = ((es.agroseguro.seguroAgrario.contratacion.PolizaDocument) polizaDoc)
								.getPoliza();
						PolizaActualizadaTranformer.generarPolizaSitFinalCompletaAgricAnt(polizaAnt, am,
								listaDatosVariables, listaCPM);
						es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaDocument = es.agroseguro.seguroAgrario.contratacion.PolizaDocument.Factory
								.newInstance();
						polizaDocument.setPoliza(polizaAnt);
						respuesta.setPolizaPrincipal(polizaDocument);
					}
				}
				tituloInforme = Constants.TITULO_ANEXO_MODIFICACION_PPL;
				// Si tiene cpl
				if (anexotieneCpl) {

					logger.debug("Entramos en anexo tiene complementaria");

					XmlObject polizaDoc = this.solicitudModificacionManager
							.getPolizaActualizadaCplFromCupon(am.getCupon().getIdcupon());
					if (polizaDoc instanceof es.agroseguro.contratacion.PolizaDocument) {
						logger.debug("Entramos en Agricola con Formato Unificado (Nuevas)");
						es.agroseguro.contratacion.Poliza polizaCplUnif = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc)
								.getPoliza();
						PolizaActualizadaCplTranformer.generarPolizaSituacionFinalCompleta(polizaCplUnif, am);
						es.agroseguro.contratacion.PolizaDocument polizaDocumentCpl = es.agroseguro.contratacion.PolizaDocument.Factory
								.newInstance();
						polizaDocumentCpl.setPoliza(polizaCplUnif);
						respuesta.setPolizaComplementariaUnif(polizaDocumentCpl);
					} else {
						logger.debug("Entramos en Agricola con Formato NO Unificado (Antiguas)");
						es.agroseguro.seguroAgrario.contratacion.complementario.Poliza polizaCpl = ((es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument) polizaDoc)
								.getPoliza();
						PolizaActualizadaCplTranformer.generarPolizaSituacionFinalCompletaAnt(polizaCpl, am);
						es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument polizaDocumentCpl = es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument.Factory
								.newInstance();
						polizaDocumentCpl.setPoliza(polizaCpl);
						respuesta.setPolizaComplementaria(polizaDocumentCpl);
					}
					tituloInforme = Constants.TITULO_ANEXO_MODIFICACION_CPL;
				}
				parametros.put("idCupon", am.getCupon().getIdcupon());
				parametros.put("imprimirAnexoWsPPl", true);
				parametros.put("printFooter", "true");
				// Situacion actual de la poliza
			} else {

				logger.debug("Entramos por el else dd imprimirAnexoWs de la poliza Principal");
				/*
				 * Con los datos que llegan como parametros, montamos un objeto que seria el que
				 * se envie al servicio para obtener la informacion de la poliza y rellenar el
				 * informe
				 */
				/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
				/*
				 * Por los desarrollos de esta peticion, tatno los anexos de ganado como
				 * agricolas van por el mismo end-point y los dos por formato Unificado.
				 */
				if (esPolizaGanado) {
					logger.debug("Entramos por poliza de Ganado");
					respuesta = new SWAnexoModificacionHelper().getPolizaActualizadaUnificado(referencia, plan,
							realPath, true);
				} else {

					logger.debug(
							"Entramos por poliza de Agricola. Antes de obtener la Poliza Actualizada con Formato Unificado");
					respuesta = new SWAnexoModificacionHelper().getPolizaActualizada(referencia, plan, realPath);

					es.agroseguro.seguroAgrario.contratacion.Poliza polizaPrincipal = es.agroseguro.seguroAgrario.contratacion.Poliza.Factory
							.newInstance();

					PolizaActualizadaTranformer.generarPolizaSitFinalCompletaAgricAnt(polizaPrincipal, am,
							listaDatosVariables, listaCPM);
					es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaPpalDocument = es.agroseguro.seguroAgrario.contratacion.PolizaDocument.Factory
							.newInstance();

					/* Pet. 57626 ** MODIF TAM (09/07/2020) ** Inicio */
					es.agroseguro.contratacion.Poliza polizaUnif = respuesta.getPolizaPrincipalUnif().getPoliza();

					PolizaActualizadaTranformer.generarPolizaSituacionFinalCompletaAgri(polizaUnif, am,
							listaDatosVariables, listaCPM, false);
					es.agroseguro.contratacion.PolizaDocument polizaDocument = es.agroseguro.contratacion.PolizaDocument.Factory
							.newInstance();
					polizaDocument.setPoliza(polizaUnif);
					respuesta.setPolizaPrincipalUnif(polizaDocument);

					polizaPrincipal.setPlan(respuesta.getPolizaPrincipalUnif().getPoliza().getPlan());
					polizaPrincipal.setReferencia(respuesta.getPolizaPrincipalUnif().getPoliza().getReferencia());
					es.agroseguro.seguroAgrario.contratacion.Cobertura coberturaPpal = es.agroseguro.seguroAgrario.contratacion.Cobertura.Factory
							.newInstance();

					String modulo = respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo();
					coberturaPpal.setModulo(modulo);
					polizaPrincipal.setCobertura(coberturaPpal);

					/* Datos Colectivo */
					es.agroseguro.seguroAgrario.contratacion.Colectivo colectivoPpal = es.agroseguro.seguroAgrario.contratacion.Colectivo.Factory
							.newInstance();

					int dcCol = respuesta.getPolizaPrincipalUnif().getPoliza().getColectivo().getDigitoControl();
					String referenciaCol = respuesta.getPolizaPrincipalUnif().getPoliza().getColectivo()
							.getReferencia();
					colectivoPpal.setDigitoControl(dcCol);
					colectivoPpal.setReferencia(referenciaCol);

					polizaPrincipal.setColectivo(colectivoPpal);

					/* Pet. 57626 ** MODIF TAM (09/07/2020) ** Inicio */

					polizaPpalDocument.setPoliza(polizaPrincipal);

					respuesta.setPolizaPrincipal(polizaPpalDocument);

				}
				parametros.put("imprimirAnexoWsPPl", false);
				// tituloInforme = Constants.TITULO_SITUACION_ACTUAL_POLIZA;
				tituloInforme = bundle.getString("titulo.situacion.actual.poliza");
				parametros.put("printFooter", "false");

			}

			// Obtengo los datos necesarios de la poliza
			Poliza polizaPpal = this.polizaDao.getPolizaByReferencia(referencia, Constants.MODULO_POLIZA_PRINCIPAL);

			logger.debug("Antes de obtener la lista de Parcelas/Explotaciones");
			// Obtengo la lista de parcelas/capitales o explotaciones necesarios para el
			// subinforme
			if (esPolizaGanado) {
				List<BeanExplotacion> lstExplotaciones = this.getListaExplotaciones(respuesta,
						polizaPpal.getLinea().getLineaseguroid(), polizaPpal);

				respuesta.setExplotaciones(lstExplotaciones);
			} else {
				logger.debug("Entramos por Agricolas");

				/* Cargamos parcelas de Anexos de Modificacion Antiguos */
				if (respuesta.getPolizaPrincipalUnif() != null) {
					logger.debug("Entramos por poliza con F. Unificado");
					List<BeanParcelaCapitalAsegurado> lstParcelas = this.getListaParcelasCapitales(respuesta,
							polizaPpal.getLinea().getLineaseguroid());
					respuesta.setParcelas(lstParcelas);

				} else {
					logger.debug("Entramos por poliza con F. No Unificado");
					List<BeanParcelaCapitalAsegurado> lstParcelas = this.getListaParcelasCapitalesAnt(respuesta,
							polizaPpal.getLinea().getLineaseguroid());
					respuesta.setParcelas(lstParcelas);
				}

				/* Pet. 63485-Fase II ** MODIF TAM (01.10.2020) ** Inicio */
				if (!esPolizaGanado) {

					infoNumExpCobertParcelas = new HashMap<Long, HashMap<String, List>>();

					for (com.rsi.agp.dao.tables.anexo.Parcela parcela : am.getParcelas()) {

						String num_parcStr = parcela.getHoja().toString() + parcela.getNumero().toString();
						Long numParc = new Long(num_parcStr);

						Integer numeroParc = parcela.getNumero().intValue();
						Integer hojaParc = parcela.getHoja().intValue();

						// Long numero = parcela.getNumero().longValue();

						Long idParcela = parcela.getId();
						logger.debug("** @@ **  Valor de idParcela: " + idParcela);

						/* Obtenemos los datos de las parcelas X */
						HashMap<String, List> infoCobertParcelas = informesManager.getDatosCobertParcelasAnexo(am,
								numeroParc, hojaParc, parcela, polizaPpal, false, null);

						infoNumExpCobertParcelas.put(numParc, infoCobertParcelas);
						// infoNumExpCobertParcelas.put(numero, infoCobertParcelas);
					}
				}
				/* Pet. 63485-Fase II ** MODIF TAM (01.10.2020) ** Fin */

			}

			// Relleno el mapa de los modulos
			Map<String, String> mapaModulos = getModulos(respuesta, polizaPpal.getLinea().getLineaseguroid());
			respuesta.setMapaModulos(mapaModulos);

			// Mapa para para las descripciones de las subvenciones
			Map<BigDecimal, SubvencionDeclarada> subvenciones;
			if (esPolizaGanado) {
				if (respuesta.getPolizaGanado() != null
						&& respuesta.getPolizaGanado().getPoliza().getSubvencionesDeclaradas() != null) {
					subvenciones = this.subvencionDeclaradaDao.getSubvencionesDeclaradasGanado(respuesta
							.getPolizaGanado().getPoliza().getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
					respuesta.setSubvenciones(subvenciones);
				}
			} else {
				if (respuesta.getPolizaPrincipalUnif() != null
						&& respuesta.getPolizaPrincipalUnif().getPoliza().getSubvencionesDeclaradas() != null) {
					subvenciones = this.subvencionDeclaradaDao
							.getSubvencionesDeclaradasGanado(respuesta.getPolizaPrincipalUnif().getPoliza()
									.getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
					respuesta.setSubvenciones(subvenciones);
				} else {
					if (respuesta.getPolizaPrincipal() != null
							&& respuesta.getPolizaPrincipal().getPoliza().getSubvencionesDeclaradas() != null) {
						subvenciones = this.subvencionDeclaradaDao
								.getSubvencionesDeclaradas(respuesta.getPolizaPrincipal().getPoliza()
										.getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
						respuesta.setSubvenciones(subvenciones);
					}
				}
			}
			// Datos del tomador y de la linea
			respuesta.setTomador(polizaPpal.getColectivo().getTomador());
			respuesta.setLinea(polizaPpal.getLinea());

			// Caracteristica de la explotacion
			if (respuesta.getPolizaPrincipalUnif() != null) {
				if (respuesta.getPolizaPrincipalUnif() != null
						&& respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura() != null
						&& respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getDatosVariables() != null
						&& respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getDatosVariables()
								.getCarExpl() != null) {
					CaracteristicaExplotacion caracteristicaExplotacion = this
							.getCaracteristicaExplotacion(respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura()
									.getDatosVariables().getCarExpl().getValor());
					respuesta.setCaracteristicaExplotacion(caracteristicaExplotacion);
				}
			} else {
				if (respuesta.getPolizaPrincipal() != null
						&& respuesta.getPolizaPrincipal().getPoliza().getCobertura() != null
						&& respuesta.getPolizaPrincipal().getPoliza().getCobertura().getDatosVariables() != null
						&& respuesta.getPolizaPrincipal().getPoliza().getCobertura().getDatosVariables()
								.getCarExpl() != null) {
					CaracteristicaExplotacion caracteristicaExplotacion = this
							.getCaracteristicaExplotacion(respuesta.getPolizaPrincipal().getPoliza().getCobertura()
									.getDatosVariables().getCarExpl().getValor());
					respuesta.setCaracteristicaExplotacion(caracteristicaExplotacion);
				}
			}			
			
			// PET-78691: Mejoras en anexos de modificación
			PagoPoliza pagoPoliza = new PagoPoliza();
			if (!polizaPpal.getPagoPolizas().isEmpty()) {
				pagoPoliza = polizaPpal.getPagoPolizas().iterator().next();
			}
			obtenerParametrosAnexosModificacion(pagoPoliza, am, parametros);
			
			// Creamos el datasource con los datos obtenidos del servicio
			List<PolizaActualizadaResponse> lstSource = new ArrayList<PolizaActualizadaResponse>();
			lstSource.add(respuesta);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lstSource, true);

			// Obtengo los datos para el cuadro de coberturas de la situacion actualizada de
			// la poliza
			Set<ComparativaPoliza> listaComparativas = getComparativasFromSituacionActualizada(respuesta,
					polizaPpal.getLinea().getLineaseguroid(), am.getCodmodulo(), esPolizaGanado);			
			
			/*DNF incidencia tabla coberturas de la poliza PET.63485.FIII 16/02/2021*/			
			HashMap<String, List> infoCoberturas = informesManager.getDatosCoberturasGarantias(polizaPpal, am.getCodmodulo(), am.getId(),
					true, listaComparativas, null, realPath);
			/*fin DNF 18/02/2021*/
			
			JRBeanCollectionDataSource sourceCoberturas = new JRBeanCollectionDataSource(infoCoberturas.get(LISTA));
			DisenoTablaCoberturas tablaCoberturas = new DisenoTablaCoberturas();
			String pathInformeCoberturas = pathInformes + bundle.getString("polizaActualizada.tabla_resumen_coberturas")
					+ JRXML;
			tablaCoberturas.getLayoutTablaCoberturas(pathInformeCoberturas, infoCoberturas.get("cabecera"));
			parametros.put("dsCoberturasPpal", sourceCoberturas);

			/* Pet. 63485-Fase II ** MODIF TAM (01.10.2020) ** Inicio */
			/* Se obtienen los datos para los cuadros de coberturas de las parcelas */
			String pathInformeParcFuente = null;
			DisenoTablaCobertParcelas tablaCobertParcelas = new DisenoTablaCobertParcelas();

			/* Parametros para las Parcelas de Agricolas */
			HashMap<Long, JRBeanCollectionDataSource> dataSourceParc = new HashMap<Long, JRBeanCollectionDataSource>();
			HashMap<Long, JasperReport> tablaSubreportParc = new HashMap<Long, JasperReport>();
			
			

			// Con el resultado de la llamada al servicio, creamos el informe PDF
			List<String> listaJrxml = new ArrayList<String>();
			listaJrxml.add(bundle.getString("polizaActualizada.asegurado"));
			listaJrxml.add(bundle.getString("polizaActualizada.aseguradoGanado"));
			listaJrxml.add(bundle.getString("polizaActualizada.cabecera"));
			listaJrxml.add(bundle.getString("polizaActualizada.cabeceraGanado"));
			listaJrxml.add(bundle.getString("polizaActualizada.explotaciones"));
			listaJrxml.add(bundle.getString("polizaActualizada.explotaciones_gruposRaza"));
			listaJrxml.add(bundle.getString("polizaActualizada.explotaciones_gruposRaza_tiposCapital"));
			listaJrxml.add(bundle.getString("polizaActualizada.explotaciones_gruposRaza_tiposCapital_animales"));
			listaJrxml.add(bundle.getString("polizaActualizada.explotaciones_riesgosCubiertos"));
			listaJrxml.add(bundle.getString("polizaActualizada.explotaciones_datosVariables"));
			listaJrxml.add(bundle.getString("polizaActualizada.subvencionesAsegurado"));
			listaJrxml.add(bundle.getString("polizaActualizada.subvencionesAseguradoGanado"));
			listaJrxml.add(bundle.getString("polizaActualizada.coberturasPrincipal_caractExpl"));
			listaJrxml.add(bundle.getString("polizaActualizada.coberturasPrincipal"));
			listaJrxml.add(bundle.getString("polizaActualizada.coberturasComplementaria"));
			listaJrxml.add(bundle.getString("polizaActualizada.decPersonasJuridicas_listado"));
			
			if (!esPolizaGanado) {
				
				pathInformeParcFuente = pathInformes + bundle.getString(POLIZA_TABLA_COBERT)
				+ JRXML;


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
						cabeceraParc = infoCobertParcelas.get("cabecera");
						if (cabeceraParc != null) {
							tablaCobertParcelas.getLayoutTablaCobertParc(pathInformeParcFuente, cabeceraParc);
							
							/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Inicio */
							listaJrxml.add(bundle.getString(POLIZA_TABLA_COBERT));
							/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Fin */
						}
					}
					
					/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Inicio */
					//listaJrxml.add(bundle.getString(POLIZA_TABLA_COBERT));
					/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Fin */
				}

			}
			/* Pet. 63485-Fase II ** MODIF TAM (01.10.2020) ** Fin */

			if (respuesta.getPolizaComplementariaUnif() != null || respuesta.getPolizaComplementaria() != null) {
				// Cuadro de coberturas de la poliza complementaria
				listaJrxml.add(bundle.getString("polizaActualizada.coberturasComplementaria"));
				listaJrxml.add(bundle.getString("polizaActualizada.tabla_resumen_coberturas_cpl"));

				Poliza polizaCpl = this.polizaDao.getPolizaByReferencia(referencia,
						Constants.MODULO_POLIZA_COMPLEMENTARIO);
				List<BeanTablaCoberturas> infoCoberturasCpl = informesManager
						.getDatosCoberturasGarantiasComplementaria(polizaCpl);
				JRBeanCollectionDataSource sourceCoberturasCpl = new JRBeanCollectionDataSource(infoCoberturasCpl);
				DisenoTablaCoberturas tablaCoberturasCpl = new DisenoTablaCoberturas();
				String pathInformeCoberturasCpl = pathInformes
						+ bundle.getString("polizaActualizada.tabla_resumen_coberturas_cpl") + JRXML;
				tablaCoberturasCpl.getLayoutTablaCoberturasComplementaria(pathInformeCoberturasCpl);

				parametros.put("dsCoberturasCpl", sourceCoberturasCpl);
			}
			listaJrxml.add(bundle.getString("polizaActualizada.tabla_resumen_coberturas"));
			listaJrxml.add(bundle.getString("polizaActualizada.tomadorAseguradoSubvencion"));

			listaJrxml.add(bundle.getString("polizaActualizada.parcelas_resumenCosechasAseguradas"));
			listaJrxml.add(bundle.getString("polizaActualizada.parcelas_resumenValorAsegurable"));
			listaJrxml.add(bundle.getString("polizaActualizada.parcelas_datosVariables"));

			listaJrxml.add(bundle.getString("polizaActualizada.parcelas"));

			/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Inicio */
			/*listaJrxml.add(bundle.getString(POLIZA_TABLA_COBERT));*/
			/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Fin */
			listaJrxml.add(bundle.getString("polizaActualizada.principal"));

			// Compilamos todos los reports
			for (String identificadorInforme : listaJrxml) {
				String pathInformeFuente = pathInformes + identificadorInforme + JRXML;
				String pathInformeCompilado = pathInformes + identificadorInforme + JASPER;

				logger.debug("Inicio de la compilacion del informe " + identificadorInforme);
				JasperCompileManager.compileReportToFile(pathInformeFuente, pathInformeCompilado);
			}
			// obtenemos la descripcion de la provincia
			String provinciaAseg = "";
			BigDecimal prov = null;
			String loc = null;

			if (esPolizaGanado) {
				if (!StringUtils
						.nullToString(
								respuesta.getPolizaGanado().getPoliza().getAsegurado().getDireccion().getProvincia())
						.equals("")) {

					prov = new BigDecimal(
							respuesta.getPolizaGanado().getPoliza().getAsegurado().getDireccion().getProvincia());
					loc = respuesta.getPolizaGanado().getPoliza().getAsegurado().getDireccion().getLocalidad();
				}
			} else {
				/* AGRICOLA CON FORMATO ANTIGUO */
				if (respuesta.getPolizaPrincipalUnif() != null) {

					if (!StringUtils.nullToString(
							respuesta.getPolizaPrincipalUnif().getPoliza().getAsegurado().getDireccion().getProvincia())
							.equals("")) {

						prov = new BigDecimal(respuesta.getPolizaPrincipalUnif().getPoliza().getAsegurado()
								.getDireccion().getProvincia());
						loc = respuesta.getPolizaPrincipalUnif().getPoliza().getAsegurado().getDireccion()
								.getLocalidad();

					}
				} else {
					if (!StringUtils.nullToString(
							respuesta.getPolizaPrincipal().getPoliza().getAsegurado().getDireccion().getProvincia())
							.equals("")) {

						prov = new BigDecimal(respuesta.getPolizaPrincipal().getPoliza().getAsegurado().getDireccion()
								.getProvincia());
						loc = respuesta.getPolizaPrincipal().getPoliza().getAsegurado().getDireccion().getLocalidad();

					}
				}

			}
			if (prov != null && !loc.equals(null)) {
				logger.debug("PROVINCIA: " + prov + " LOCALIDAD: " + loc);
				Object[] datos = aseguradoDao.getDatosProvincia(prov, loc);
				if (datos != null) {
					provinciaAseg = (String) datos[2];
				}
			}

			// Relenamos la lista de parametros
			parametros.put("esGanado", esPolizaGanado);
			parametros.put("SUB_DATA_SOURCE", lstSource);
			parametros.put("ID", polizaPpal.getIdpoliza());
			if (imprimirAnexoWs)
				parametros.put("estadoPol", "");
			else
				parametros.put("estadoPol", polizaPpal.getEstadoPoliza().getDescEstado());
			parametros.put("esRenovable",
					new Character('S').equals(polizaPpal.getRenovableSn()) ? Constants.ES_RENOVABLE
							: Constants.NO_ES_RENOVABLE);
			parametros.put("SUBREPORT_DIR", pathInformes + "/jasper/polizaActualizada/");
			parametros.put("tituloInforme", tituloInforme);
			Locale locale = new Locale("es", "ES");
			parametros.put(JRParameter.REPORT_LOCALE, locale);
			parametros.put("provinciaAseg", provinciaAseg);
			if (!anexotieneCpl) {
				// Subreport para las coberturas de la poliza principal
				File subreportCoberturasPpal = new File(
						pathInformes + bundle.getString("polizaActualizada.tabla_resumen_coberturas") + JASPER);
				JasperReport subReport = (JasperReport) JRLoader.loadObject(subreportCoberturasPpal);
				parametros.put("tablaCoberturasPpal", subReport);
			}

			/* Pet. 63485-Fase II ** MODIF TAM (05.10.2020) ** Inicio */
			if (!esPolizaGanado) {
				pathInformeParcFuente = pathInformes + bundle.getString(POLIZA_TABLA_COBERT)
						+ JRXML;

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
						cabeceraParc = infoCobertParcelas.get("cabecera");
						if (cabeceraParc != null) {

							tablaCobertParcelas.getLayoutTablaCobertParc(pathInformeParcFuente, cabeceraParc);

							File sub2File = new File(pathInformes
									+ bundle.getString(POLIZA_TABLA_COBERT) + JASPER);
							JasperReport subReportParc = (JasperReport) JRLoader.loadObject(sub2File);

							/* Informamos los hashmap */
							tablaSubreportParc.put(num_Parc, subReportParc);
						}
					}

					parametros.put("datasourcParc", dataSourceParc);
					parametros.put("tablaSubreportParc", tablaSubreportParc);
				}
			}

			// Subreport para las coberturas de la complementaria
			if (respuesta.getPolizaComplementariaUnif() != null || respuesta.getPolizaComplementaria() != null) {
				// Subreport para las coberturas de la poliza complementaria
				File subreportCoberturasCpl = new File(
						pathInformes + bundle.getString("polizaActualizada.tabla_resumen_coberturas_cpl") + JASPER);
				JasperReport subReportCpl = (JasperReport) JRLoader.loadObject(subreportCoberturasCpl);

				parametros.put("tablaCoberturasCpl", subReportCpl);
			}

			// P0057622 INICIO
			Boolean polizaTieneSubvCaractAseguradoPersonaJuridica = Boolean.FALSE;
			List<Socio> lstSocios = new ArrayList<Socio>();
			Object[] subDecArr;
			Object[] socArr;
			logger.debug("PolizaActualizadaManager - Antes de validar las subvenciones");
			if (esPolizaGanado) {
				logger.debug("PolizaActualizadaManager - Entramos en Ganado");
				subDecArr = respuesta.getPolizaGanado().getPoliza().getSubvencionesDeclaradas() == null
						? new Object[] {}
						: respuesta.getPolizaGanado().getPoliza().getSubvencionesDeclaradas()
								.getSubvencionDeclaradaArray();
			} else {
				logger.debug("PolizaActualizadaManager - Entramos en Agricolas");
				/* AGRICOLA CON FORMATO UNIFICADO */
				if (respuesta.getPolizaPrincipalUnif() != null) {
					logger.debug("PolizaActualizadaManager - Entramos en Agricola-Formato Unificado (Nuevas)");
					subDecArr = respuesta.getPolizaPrincipalUnif().getPoliza().getSubvencionesDeclaradas() == null
							? new Object[] {}
							: respuesta.getPolizaPrincipalUnif().getPoliza().getSubvencionesDeclaradas()
									.getSubvencionDeclaradaArray();

					/* AGRICOLA SIN FORMATO UNIFICADO (ANTIGUAS) */
				} else {
					logger.debug("PolizaActualizadaManager - Entramos en Agricola-Formato No Unificado (Antiguas)");
					subDecArr = respuesta.getPolizaPrincipal().getPoliza().getSubvencionesDeclaradas() == null
							? new Object[] {}
							: respuesta.getPolizaPrincipal().getPoliza().getSubvencionesDeclaradas()
									.getSubvencionDeclaradaArray();

				}
			}

			/*****/
			for (Object subDec : subDecArr) {
				logger.debug("Entramos en el for");
				int tiposubv = 0;
				if (esPolizaGanado) {
					logger.debug("Entramos en Ganado");
					tiposubv = ((es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada) subDec)
							.getTipo();
				} else {
					logger.debug("Entramos en Agricola");
					/* AGRICOLA CON FORMATO UNIFICADO */
					if (respuesta.getPolizaPrincipalUnif() != null) {
						logger.debug("Entramos por Agricola Formato Unificado (nuevas)");
						tiposubv = ((es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada) subDec)
								.getTipo();
						logger.debug("PolizaActualizadaManager - Valor de tiposubv:" + tiposubv);
					} else {
						logger.debug("Entramos por Agricola Formato Unificado (antiguas)");
						tiposubv = ((es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada) subDec).getTipo();
						logger.debug("PolizaActualizadaManager - Valor de tiposubv:" + tiposubv);
					}
				}
				if (Constants.CARACT_ASEGURADO_PERSONA_JURIDICA.intValue() == tiposubv) {
					polizaTieneSubvCaractAseguradoPersonaJuridica = Boolean.TRUE;
					break;
				}
			}
			/*****/
			parametros.put("polizaTieneSubvCaractAseguradoPersonaJuridica",
					polizaTieneSubvCaractAseguradoPersonaJuridica);

			if (polizaTieneSubvCaractAseguradoPersonaJuridica) {
				logger.debug("PolizaActualizadaManager - Entramos en el if de PersonaJuridica");
				if (esPolizaGanado) {
					socArr = respuesta.getPolizaGanado().getPoliza().getRelacionSocios().getSocioArray();
				} else {
					logger.debug("PolizaActualizadaManager - Entramos en polizas de Agricocolas");
					/* AGRICOLA CON FORMATO UNIFICADO */
					if (respuesta.getPolizaPrincipalUnif() != null) {
						logger.debug("PolizaActualizadaManager - Entramos en polizas de Agricolas con F. Unificado");
						socArr = respuesta.getPolizaPrincipalUnif().getPoliza().getRelacionSocios().getSocioArray();
						/* AGRICOLA SIN FORMATO UNIFICADO (ANTIGUAS) */
					} else {
						socArr = respuesta.getPolizaPrincipal().getPoliza().getRelacionSocios().getSocioArray();
						logger.debug(
								"PolizaActualizadaManager - Entramos en polizas de Agricolas con F. no Unificado");
					}
				}
				for (Object soc : socArr) {

					logger.debug("Entrmos en el for de socios");
					es.agroseguro.contratacion.declaracionSubvenciones.Socio socGan = null;
					es.agroseguro.contratacion.declaracionSubvenciones.Socio socAgr = null;
					es.agroseguro.seguroAgrario.contratacion.Socio socAgrAnt = null;

					/* es.agroseguro.seguroAgrario.contratacion.Socio socAgr = null; */
					int orden = 0;
					String nif;
					String numSS = "";
					BigDecimal regSS = BigDecimal.ZERO;
					String tipoIdent;
					String nombre;
					String apellido1;
					String apellido2;
					String razonSocial;
					Map<BigDecimal, SubvencionDeclarada> subvsDeclaradas = new HashMap<BigDecimal, SubvencionDeclarada>();

					/* GANADO */
					if (esPolizaGanado) {
						socGan = (es.agroseguro.contratacion.declaracionSubvenciones.Socio) soc;
						orden = socGan.getNumero();
						nif = socGan.getNif();
						tipoIdent = StringUtils.validaNifCif(Constants.TIPO_IDENTIFICACION_CIF, nif)
								? Constants.TIPO_IDENTIFICACION_CIF
								: Constants.TIPO_IDENTIFICACION_NIF;
						if (Constants.TIPO_IDENTIFICACION_NIF.equals(tipoIdent)) {
							nombre = socGan.getNombreApellidos().getNombre();
							apellido1 = socGan.getNombreApellidos().getApellido1();
							apellido2 = socGan.getNombreApellidos().getApellido2();
							razonSocial = "";
						} else {
							nombre = "";
							apellido1 = "";
							apellido2 = "";
							razonSocial = socGan.getRazonSocial().getRazonSocial();
						}
						if (socGan.getSubvencionesDeclaradas() != null) {
							if (socGan.getSubvencionesDeclaradas().getSubvencionDeclaradaArray() != null) {
								subvsDeclaradas = this.subvencionDeclaradaDao.getSubvencionesDeclaradasGanado(
										socGan.getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
							}
							if (socGan.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
								numSS = String
										.valueOf(socGan.getSubvencionesDeclaradas().getSeguridadSocial().getNumero());
								regSS = BigDecimal
										.valueOf(socGan.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen());
							}
						}
						/** AGRICOLAS **/
					} else {
						/** AGRICOLAS CON FORMATO UNIFICADO **/

						logger.debug("Polizas Agricolas");
						if (respuesta.getPolizaPrincipalUnif() != null) {
							logger.debug("Polizas Agricolas con F. Unificado");
							/* Pet. 57626 ** MODIF TAM (26.06.2020) ** Inicio */
							/* socAgr = (es.agroseguro.seguroAgrario.contratacion.Socio) soc; */

							socAgr = (es.agroseguro.contratacion.declaracionSubvenciones.Socio) soc;
							orden = socAgr.getNumero();
							nif = socAgr.getNif();
							tipoIdent = StringUtils.validaNifCif(Constants.TIPO_IDENTIFICACION_CIF, nif)
									? Constants.TIPO_IDENTIFICACION_CIF
									: Constants.TIPO_IDENTIFICACION_NIF;
							if (Constants.TIPO_IDENTIFICACION_NIF.equals(tipoIdent)) {
								nombre = socAgr.getNombreApellidos().getNombre();
								apellido1 = socAgr.getNombreApellidos().getApellido1();
								apellido2 = socAgr.getNombreApellidos().getApellido2();
								razonSocial = "";
							} else {
								nombre = "";
								apellido1 = "";
								apellido2 = "";
								razonSocial = socAgr.getRazonSocial().getRazonSocial();
							}
							if (socAgr.getSubvencionesDeclaradas() != null) {
								if (socAgr.getSubvencionesDeclaradas().getSubvencionDeclaradaArray() != null) {
									/*
									 * subvsDeclaradas = this.subvencionDeclaradaDao.getSubvencionesDeclaradas(
									 * socAgr.getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
									 */
									subvsDeclaradas = this.subvencionDeclaradaDao.getSubvencionesDeclaradasGanado(
											socAgr.getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
								}

								if (socAgr.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
									numSS = String.valueOf(
											socAgr.getSubvencionesDeclaradas().getSeguridadSocial().getNumero());
									regSS = BigDecimal.valueOf(
											socAgr.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen());
								}
							}
							/* AGRICOLA SIN FORMATO UNIFICADO (ANTIGUAS) */
						} else {
							logger.debug("Polizas Agricolas con F. NO Unificado (Antiguas)");
							socAgrAnt = (es.agroseguro.seguroAgrario.contratacion.Socio) soc;
							orden = socAgrAnt.getNumero();
							nif = socAgrAnt.getNif();
							tipoIdent = StringUtils.validaNifCif(Constants.TIPO_IDENTIFICACION_CIF, nif)
									? Constants.TIPO_IDENTIFICACION_CIF
									: Constants.TIPO_IDENTIFICACION_NIF;
							if (Constants.TIPO_IDENTIFICACION_NIF.equals(tipoIdent)) {
								nombre = socAgrAnt.getNombreApellidos().getNombre();
								apellido1 = socAgrAnt.getNombreApellidos().getApellido1();
								apellido2 = socAgrAnt.getNombreApellidos().getApellido2();
								razonSocial = "";
							} else {
								nombre = "";
								apellido1 = "";
								apellido2 = "";
								razonSocial = socAgrAnt.getRazonSocial().getRazonSocial();
							}
							if (socAgrAnt.getSubvencionesDeclaradas() != null) {
								if (socAgrAnt.getSubvencionesDeclaradas().getSubvencionDeclaradaArray() != null) {
									subvsDeclaradas = this.subvencionDeclaradaDao.getSubvencionesDeclaradas(
											socAgrAnt.getSubvencionesDeclaradas().getSubvencionDeclaradaArray());
								}

								if (socAgrAnt.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
									numSS = String.valueOf(
											socAgrAnt.getSubvencionesDeclaradas().getSeguridadSocial().getNumero());
									regSS = BigDecimal.valueOf(
											socAgrAnt.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen());
								}
							}
						}
					}
					if (subvsDeclaradas.isEmpty()) {
						lstSocios.add(buildSocio(orden, nif, polizaPpal.getAsegurado().getId(), numSS, regSS, tipoIdent,
								nombre, apellido1, apellido2, razonSocial, ""));
					} else {
						BigDecimal tipoSubv;
						Iterator<BigDecimal> it = subvsDeclaradas.keySet().iterator();
						while (it.hasNext()) {
							tipoSubv = it.next();
							String descSubv = tipoSubv + " - " + subvsDeclaradas.get(tipoSubv).getDesSubvencion();
							lstSocios.add(buildSocio(orden, nif, polizaPpal.getAsegurado().getId(), numSS, regSS,
									tipoIdent, nombre, apellido1, apellido2, razonSocial, descSubv));
						}
					}
				}
			}

			logger.debug("Salimos");

			JRBeanCollectionDataSource socios = new JRBeanCollectionDataSource(lstSocios);
			parametros.put("lstSocios", socios);
			parametros.put("SOCIOS", new BigDecimal(lstSocios.size()));

			parametros.put("ASEGURADO_NOMBRE",
					(Constants.TIPO_IDENTIFICACION_CIF.equals(polizaPpal.getAsegurado().getTipoidentificacion())
							? polizaPpal.getAsegurado().getRazonsocial()
							: polizaPpal.getAsegurado().getNombre() + " " + polizaPpal.getAsegurado().getApellido1()
									+ " " + polizaPpal.getAsegurado().getApellido2()).toUpperCase());
			parametros.put("REFERENCIA", polizaPpal.getReferencia());
			parametros.put("REFCOLECTIVO",
					polizaPpal.getColectivo().getIdcolectivo() + "-" + polizaPpal.getColectivo().getDc());

			// P0057622 FIN

			// Cargamos el informe principal y lo rellenamos
			File masterFile = new File(pathInformes + bundle.getString("polizaActualizada.principal") + JASPER);
			JasperReport masterReport = (JasperReport) JRLoader.loadObject(masterFile);
			JasperPrint jp = JasperFillManager.fillReport(masterReport, parametros, dataSource);

			return jp;
		} catch (Exception e) {
			logger.debug(LOGGER_ERROR + e.getMessage());
			throw e;
		}
	}

	/*
	 * 
	 */
	private void obtenerParametrosAnexosModificacion(PagoPoliza pagoPoliza, AnexoModificacion am, HashMap<String, Object> parametros) {
		
		String banco = obtenerValor(pagoPoliza.getBanco());
		String formaPago = obtenerValor(pagoPoliza.getFormapago());
		String importe = obtenerValor(pagoPoliza.getImporte());
		String destinatario = obtenerValor(pagoPoliza.getDestinatarioDomiciliacion());
		String domiciliado = obtenerValor(pagoPoliza.getDomiciliado());
		String fecha = "";
		if (pagoPoliza.getFecha()!=null) 
			fecha = new SimpleDateFormat("dd-MM-yyyy").format(pagoPoliza.getFecha());
		
		logger.debug("BANCO: "+banco);
		logger.debug("FORMA PAGO: "+formaPago);
		logger.debug("IMPORTE: "+importe+"€");
		logger.debug("FECHA: "+fecha);
		logger.debug("DEST: "+destinatario);
		logger.debug("DOMI: "+domiciliado);
		logger.debug("IBAN ASEG. ORIGINAL: "+am.getIbanAsegOriginal());
		logger.debug("IBAN ASEG. MOD: "+am.getIbanAsegModificado());
		logger.debug("IBAN2 ASEG. ORI: "+am.getIban2AsegOriginal());
		logger.debug("IBAN2 ASEG. MOD: "+am.getIban2AsegModificado());

		parametros.put("banco", banco);
		parametros.put("formaPago", formaPago);
		parametros.put("importe", importe+"€");
		parametros.put("fecha", fecha);
		parametros.put("destinatario", destinatario);
		parametros.put("domiciliado", domiciliado);
		parametros.put("ibanOriginal", formatearIBAN(am.getIbanAsegOriginal()));
		parametros.put("ibanModificado", formatearIBAN(am.getIbanAsegModificado()));
		parametros.put("iban2Original", formatearIBAN(am.getIban2AsegOriginal()));
		parametros.put("iban2Modificado", formatearIBAN(am.getIban2AsegModificado()));
		parametros.put("esIbanModificado",am.getEsIbanAsegModificado());
		parametros.put("esIban2Modificado",am.getEsIban2AsegModificado());
		
	}
	
	private String obtenerValor(Object campo) {
		
		if (campo!=null) {
			return campo.toString();
		}
		
		return "";
	}

	private Object formatearIBAN(String ibanAsegOri) {
		
		if (ibanAsegOri!=null)
			return "ES**-****-****-**-*****"+ibanAsegOri.substring(19);
		
		return null;
	}

	// P0057622 INICIO
	private Socio buildSocio(int orden, String nif, Long idAsegurado, String numSS, BigDecimal regSS, String tipoIdent,
			String nombre, String apellido1, String apellido2, String razonSocial, String tipoSubv) {
		Socio socio = new Socio();
		socio.setOrden(orden);
		socio.setId(new com.rsi.agp.dao.tables.admin.SocioId(nif, idAsegurado));
		socio.setNumsegsocial(numSS);
		socio.setRegimensegsocial(regSS);
		socio.setTipoidentificacion(tipoIdent);
		socio.setNombre(nombre);
		socio.setApellido1(apellido1);
		socio.setApellido2(apellido2);
		socio.setRazonsocial(razonSocial);
		socio.setDescripcionSubvencion(tipoSubv);
		return socio;
	}
	// P0057622 FIN

	/**
	 * Metodo para imprimir el borrador del anexo de modificacion
	 * 
	 * @param idAnexoModificacion
	 *            Identificador interno del anexo de modificacion
	 * @param pathInformes
	 *            Ruta fisica de los informes en el servidor
	 * @return Devuelve un objeto de tipo JasperPrint que sera el que se use en el
	 *         controller para pintar el informe.
	 * @throws DAOException
	 */
	@SuppressWarnings("unused")
	public JasperPrint imprimirAnexoPpal(Long idAnexoModificacion, String pathInformes) throws DAOException {
		ResourceBundle bundle = ResourceBundle.getBundle("agp_informes_jasper");

		// Obtengo los datos del anexo de modificacion
		AnexoModificacion anexo = this.declaracionModificacionPolizaDao.getAnexoModifById(idAnexoModificacion);

		// Obtengo los datos de la poliza
		Poliza polizaPpal = this.polizaDao.getPolizaById(anexo.getPoliza().getIdpoliza());

		// Relleno el objeto PolizaActualizadaResponse con los datos del anexo
		PolizaActualizadaResponse respuesta = AnexoModificacionUtils.anexoPpalToPolizaActualizadaResponse(anexo,
				polizaPpal);

		// Obtengo la lista de parcelas/capitales necesarios para el subinforme
		List<BeanParcelaCapitalAsegurado> lstParcelas = this.getListaParcelasCapitalesAnexoPpal(anexo.getParcelas(),
				polizaPpal.getLinea().getLineaseguroid());

		return null;
	}

	private List<BeanParcelaCapitalAsegurado> getListaParcelasCapitalesAnexoPpal(
			Set<com.rsi.agp.dao.tables.anexo.Parcela> parcelas, Long lineaseguroid) {

		// Por cada capital asegurado creo un objeto BeanParcelaCapitalAsegurado y lo
		// relleno con los datos necesarios
		for (com.rsi.agp.dao.tables.anexo.Parcela par : parcelas) {
			for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado capAseg : par.getCapitalAsegurados()) {
				// solo anado las parcelas que estan marcadas como modificadas
				if (par.getTipomodificacion() != null && capAseg.getTipomodificacion() != null) {
					BeanParcelaCapitalAsegurado elemento = new BeanParcelaCapitalAsegurado();

					elemento.setNombre(par.getNomparcela());
					elemento.setHoja(par.getHoja().intValue());
					elemento.setNumero(par.getNumero().intValue());

					// Identificacion catastral: no hace falta!
					IdentificacionCatastral idCatastral = IdentificacionCatastral.Factory.newInstance();
					idCatastral.setParcela(par.getParcela_1());
					idCatastral.setPoligono(par.getPoligono());
					elemento.setIdCatastral(idCatastral);

					// Identificacion SIGPAC
					SIGPAC sigpac = SIGPAC.Factory.newInstance();
					sigpac.setProvincia(par.getCodprovsigpac().intValue());
					sigpac.setTermino(par.getCodtermsigpac().intValue());
					sigpac.setAgregado(par.getAgrsigpac().intValue());
					sigpac.setZona(par.getZonasigpac().intValue());
					sigpac.setPoligono(par.getPoligonosigpac().intValue());
					sigpac.setParcela(par.getParcelasigpac().intValue());
					sigpac.setRecinto(par.getRecintosigpac().intValue());
					elemento.setIdSigpac(sigpac);

					// TTerminormino
					elemento.setUbicacion(this.getUbicacion(par.getCodprovincia().intValue(),
							par.getCodcomarca().intValue(), par.getCodtermino().intValue(), par.getSubtermino() + ""));

					// Cultivo y variedad
					elemento.setVariedad(this.getVariedad(lineaseguroid, par.getCodcultivo().intValue(),
							par.getCodvariedad().intValue()));

					// Tipo de capital
					elemento.setTipoCapital(
							this.getTipoCapital(capAseg.getTipoCapital().getCodtipocapital().intValue()));

					// Superficie, precio y produccion
					elemento.setSuperficie(capAseg.getSuperficie());
					elemento.setPrecio(capAseg.getPrecio());
					elemento.setProduccion(new Integer(capAseg.getProduccion().intValue()));
				}
			}
		}
		return null;
	}

	/**
	 * Metodo que rellena una lista de objetos "explotacion" partiendo de la
	 * informacion obtenida del servicio web de poliza actualizada.
	 * 
	 * @param respuesta
	 *            Objeto con la respuesta del servicio
	 * @return Lista con un elemento por cada explotacion.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private List<BeanExplotacion> getListaExplotaciones(final PolizaActualizadaResponse respuesta,
			final Long lineaseguroid, final Poliza pol) throws Exception {
		// Mapa auxiliar con los codigos de concepto de los datos variables y
		// sus etiquetas y tablas asociadas.
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
				.getCodConceptoEtiquetaTablaExplotaciones(lineaseguroid);
		HashMap mapAux = new HashMap();
		List<BeanExplotacion> explotaciones = new ArrayList<BeanExplotacion>();
		String modulo = "";

		try {
			if (respuesta.getPolizaGanado().getPoliza().getCobertura() != null) {
				modulo = respuesta.getPolizaGanado().getPoliza().getCobertura().getModulo();
			}
			Node currNode = respuesta.getPolizaGanado().getPoliza().getObjetosAsegurados().getDomNode().getFirstChild();
			while (currNode != null) {
				if (currNode.getNodeType() == Node.ELEMENT_NODE) {
					ExplotacionDocument xmlExplotacion = null;
					try {
						xmlExplotacion = ExplotacionDocument.Factory.parse(currNode);
					} catch (XmlException e) {
						logger.error("Error al parsear una explotacion.", e);
					}

					if (xmlExplotacion != null && xmlExplotacion.getExplotacion() != null) {
						mapAux.clear();
						List<BeanRiesgoCubiertoElegido> listRiesgosCubiertos = new ArrayList<BeanRiesgoCubiertoElegido>();
						BeanExplotacion explotacion = builExplotacion(xmlExplotacion, lineaseguroid, auxEtiquetaTabla,
								mapAux);

						if (xmlExplotacion.getExplotacion().getDatosVariables() != null && xmlExplotacion
								.getExplotacion().getDatosVariables().getRiesgCbtoElegArray() != null) {

							listRiesgosCubiertos = buildRiesgosCubiertosElegidos(
									xmlExplotacion.getExplotacion().getDatosVariables().getRiesgCbtoElegArray(),
									lineaseguroid, explotacion, modulo, pol);
							// explotacion.setRiesgosCubiertos(listRiesgosCubiertos);
						}

						// riesgos cubiertos elegibles especiales
						if (xmlExplotacion.getExplotacion().getDatosVariables() != null) {

							listRiesgosCubiertos = buildRiesgosCubEspeciales(lineaseguroid, explotacion, modulo,
									listRiesgosCubiertos, xmlExplotacion.getExplotacion().getDatosVariables(), pol);

						}
						Collections.sort(listRiesgosCubiertos, new BeanRCbrtoElegidoComparator());
						explotacion.setRiesgosCubiertos(listRiesgosCubiertos);
						explotaciones.add(explotacion);
					}
				}
				currNode = currNode.getNextSibling();
			}
		} catch (Exception e) {
			logger.error("Error al guardar los datos de la explotacion", e);
			throw e;
		}

		return explotaciones;
	}

	private List<BeanRiesgoCubiertoElegido> buildRiesgosCubiertosElegidos(
			es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] riesgCbtoElegArray, Long lineaseguroid,
			BeanExplotacion explotacion, String modulo, Poliza pol) throws Exception {

		List<BeanRiesgoCubiertoElegido> listRiesgosCubiertos = new ArrayList<BeanRiesgoCubiertoElegido>();
		try {
			BeanRiesgoCubiertoElegido beanRiesgoCubiertoElegido = null;
			for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido xmlRiesCub : riesgCbtoElegArray) {
				int cPMod = xmlRiesCub.getCPMod();
				int codRcub = xmlRiesCub.getCodRCub();
				String sFila = riesgoCubiertoModuloDao.getfilaRiesgoCubModulo(lineaseguroid, modulo.trim(),
						new BigDecimal(cPMod), new BigDecimal(codRcub));
				beanRiesgoCubiertoElegido = explotacion.new BeanRiesgoCubiertoElegido();

				beanRiesgoCubiertoElegido.setNombreRiesgoCub(riesgoCubiertoModuloDao
						.getDescRiesgoCubModulo(lineaseguroid, modulo.trim(), new BigDecimal(codRcub)));

				ConceptoPpalModulo conceptoPplaHbm = conceptoPpalDao.getConceptoPpal(new BigDecimal(cPMod));
				beanRiesgoCubiertoElegido.setNombreCptoPpal(conceptoPplaHbm.getDesconceptoppalmod());
				beanRiesgoCubiertoElegido.setNombreCpto("RIESGO CUBIERTO ELEGIDO");
				beanRiesgoCubiertoElegido.setValor("SI");

				if (!sFila.equals(""))
					beanRiesgoCubiertoElegido.setFila(Integer.parseInt(sFila));
				beanRiesgoCubiertoElegido.setColumna(new Integer(1));

				listRiesgosCubiertos.add(beanRiesgoCubiertoElegido);

			}
		} catch (Exception e) {
			logger.error("Error al los riegos cubiertos elegidos de la explotacion", e);
			throw e;
		}
		return listRiesgosCubiertos;
	}

	private List<BeanRiesgoCubiertoElegido> buildRiesgosCubEspeciales(Long lineaseguroid, BeanExplotacion explotacion,
			String modulo, List<BeanRiesgoCubiertoElegido> listRiesgosCubiertos, DatosVariables datV, Poliza pol)
			throws Exception {

		if (datV.getCalcIndemArray() != null) {
			try {
				CalculoIndemnizacion[] calcIndemArray = datV.getCalcIndemArray();
				BeanRiesgoCubiertoElegido beanRiesgoCubiertoElegido = null;
				for (CalculoIndemnizacion xmlCalcIndem : calcIndemArray) {
					int cPMod = xmlCalcIndem.getCPMod();
					int codRcub = xmlCalcIndem.getCodRCub();
					int valor = xmlCalcIndem.getValor();
					String sFila = riesgoCubiertoModuloDao.getfilaRiesgoCubModulo(lineaseguroid, modulo.trim(),
							new BigDecimal(cPMod), new BigDecimal(codRcub));
					beanRiesgoCubiertoElegido = explotacion.new BeanRiesgoCubiertoElegido();
					beanRiesgoCubiertoElegido.setNombreRiesgoCub(riesgoCubiertoModuloDao
							.getDescRiesgoCubModulo(lineaseguroid, modulo.trim(), new BigDecimal(codRcub)));
					ConceptoPpalModulo conceptoPplaHbm = conceptoPpalDao.getConceptoPpal(new BigDecimal(cPMod));
					beanRiesgoCubiertoElegido.setNombreCptoPpal(conceptoPplaHbm.getDesconceptoppalmod());

					if (!sFila.equals(""))
						beanRiesgoCubiertoElegido.setFila(Integer.parseInt(sFila));
					String descDatVar = riesgoCubiertoModuloDao.getDescDatoVarRiesgo(new BigDecimal(174));
					beanRiesgoCubiertoElegido.setNombreCpto(descDatVar);
					String DescValor = riesgoCubiertoModuloDao.getDescCalcIndem(valor);
					beanRiesgoCubiertoElegido.setValor(DescValor);
					beanRiesgoCubiertoElegido.setColumna(new Integer(3));
					listRiesgosCubiertos.add(beanRiesgoCubiertoElegido);
				}
			} catch (Exception e) {
				logger.error(
						"Error al recoger el CalculoIndemnizacion de los riegos cubiertos elegidos de la explotacion",
						e);
				throw e;
			}
		}

		if (datV.getMinIndemArray() != null) {
			try {
				es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[] minIndemArray = datV
						.getMinIndemArray();
				BeanRiesgoCubiertoElegido beanRiesgoCubiertoElegido = null;
				for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable xmlminIndem : minIndemArray) {
					int cPMod = xmlminIndem.getCPMod();
					int codRcub = xmlminIndem.getCodRCub();
					int valor = xmlminIndem.getValor();
					String sFila = riesgoCubiertoModuloDao.getfilaRiesgoCubModulo(lineaseguroid, modulo.trim(),
							new BigDecimal(cPMod), new BigDecimal(codRcub));
					beanRiesgoCubiertoElegido = explotacion.new BeanRiesgoCubiertoElegido();
					beanRiesgoCubiertoElegido.setNombreRiesgoCub(riesgoCubiertoModuloDao
							.getDescRiesgoCubModulo(lineaseguroid, modulo.trim(), new BigDecimal(codRcub)));

					ConceptoPpalModulo conceptoPplaHbm = conceptoPpalDao.getConceptoPpal(new BigDecimal(cPMod));
					beanRiesgoCubiertoElegido.setNombreCptoPpal(conceptoPplaHbm.getDesconceptoppalmod());

					if (!sFila.equals(""))
						beanRiesgoCubiertoElegido.setFila(Integer.parseInt(sFila));
					String descDatVar = riesgoCubiertoModuloDao.getDescDatoVarRiesgo(new BigDecimal(121));
					beanRiesgoCubiertoElegido.setNombreCpto(descDatVar);
					String DescValor = riesgoCubiertoModuloDao.getDescCalcIndem(valor);
					beanRiesgoCubiertoElegido.setValor(DescValor);
					beanRiesgoCubiertoElegido.setColumna(new Integer(4));
					listRiesgosCubiertos.add(beanRiesgoCubiertoElegido);
				}
			} catch (Exception e) {
				logger.error(
						"Error al recoger el PorcentajeMinimoIndemnizable de los riegos cubiertos elegidos de la explotacion",
						e);
				throw e;
			}
		}

		return listRiesgosCubiertos;
	}

	@SuppressWarnings("rawtypes")
	private BeanExplotacion builExplotacion(ExplotacionDocument xmlExplotacion, Long lineaseguroid,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla, HashMap mapAux) throws Exception {
		BeanExplotacion explotacion = new BeanExplotacion();

		try {
			// DATOS DE LA EXPLOTACION
			// Ubicacion
			BigDecimal codprovincia = BigDecimal.valueOf(xmlExplotacion.getExplotacion().getUbicacion().getProvincia());
			BigDecimal codcomarca = BigDecimal.valueOf(xmlExplotacion.getExplotacion().getUbicacion().getComarca());
			BigDecimal codtermino = BigDecimal.valueOf(xmlExplotacion.getExplotacion().getUbicacion().getTermino());
			char subtermino = "".equals(xmlExplotacion.getExplotacion().getUbicacion().getSubtermino()) ? ' '
					: xmlExplotacion.getExplotacion().getUbicacion().getSubtermino().charAt(0);
			Termino terminoHbm = terminoDao.getTermino(codprovincia, codcomarca, codtermino, subtermino);
			explotacion.setNumExplotacion(xmlExplotacion.getExplotacion().getNumero());
			explotacion.setCodProvincia(codprovincia.toString());
			explotacion.setNomProvincia(terminoHbm.getComarca().getProvincia().getNomprovincia());
			explotacion.setCodComarca(codcomarca.toString());
			explotacion.setNomComarca(terminoHbm.getComarca().getNomcomarca());
			explotacion.setCodTermino(codtermino.toString());
			
			// Se recupera una instancia específica de la entidad "Linea" a través del DAO a partir del lineaseguroid
			com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(lineaseguroid.toString());
			
			
			// Obtenemos la fecha de fin de contratación.
			Date fechaInicioContratacion = linea.getFechaInicioContratacion();
			
			// Utiliza el método getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
			// Esta versión ahora tiene en cuenta la fecha de inicio de contratación y si la línea es de ganado para determinar el nombre correcto del termino
			explotacion.setNomTermino(terminoHbm.getNomTerminoByFecha(fechaInicioContratacion, true));
			
			explotacion.setSubtermino(String.valueOf(subtermino));
			// Especie-regimen-rega-sigla-subexplotacion
			Especie especieHbm = (Especie) polizaDao.get(Especie.class,
					new EspecieId(lineaseguroid, Long.valueOf(xmlExplotacion.getExplotacion().getEspecie())));
			explotacion.setCodEspecie(especieHbm.getId().getCodespecie().toString());
			explotacion.setNomEspecie(especieHbm.getDescripcion());
			RegimenManejo regimenHbm = (RegimenManejo) polizaDao.get(RegimenManejo.class,
					new RegimenManejoId(lineaseguroid, Long.valueOf(xmlExplotacion.getExplotacion().getRegimen())));
			explotacion.setCodRegimen(regimenHbm.getId().getCodRegimen().toString());
			explotacion.setNomRegimen(regimenHbm.getDescripcion());
			explotacion.setRega(xmlExplotacion.getExplotacion().getRega());
			explotacion.setSigla(xmlExplotacion.getExplotacion().getSigla());
			explotacion.setSubexplotacion("" + xmlExplotacion.getExplotacion().getSubexplotacion());
			// Coordenadas
			if (xmlExplotacion.getExplotacion().getCoordenadas() != null) {
				explotacion.setLatitud("" + xmlExplotacion.getExplotacion().getCoordenadas().getLatitud());
				explotacion.setLongitud("" + xmlExplotacion.getExplotacion().getCoordenadas().getLongitud());
			}

			if (xmlExplotacion.getExplotacion().getGrupoRazaArray() != null
					&& xmlExplotacion.getExplotacion().getGrupoRazaArray().length != 0) {
				// guardamos los datos variables
				List<DatoVariable> datosVariables = new ArrayList<DatoVariable>();
				addDatosVariables(datosVariables, xmlExplotacion.getExplotacion().getDatosVariables(), auxEtiquetaTabla,
						mapAux);

				List<BeanGrupoRaza> listGruposRaza = buildGruposRaza(xmlExplotacion, explotacion, lineaseguroid,
						auxEtiquetaTabla, datosVariables, mapAux);

				explotacion.setGruposRaza(listGruposRaza);
			}

		} catch (Exception e) {
			logger.error("Error al guardar los datos de la explotacion", e);
			throw e;
		}
		return explotacion;
	}

	@SuppressWarnings("rawtypes")
	private List<BeanGrupoRaza> buildGruposRaza(ExplotacionDocument xmlExplotacion, BeanExplotacion explotacion,
			Long lineaseguroid, Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla,
			List<DatoVariable> datosVariables, HashMap mapAux) throws Exception {

		List<BeanGrupoRaza> gruposRaza = new ArrayList<BeanGrupoRaza>();

		try {
			// Recorremos el array de Grupos de Raza
			for (GrupoRaza xmlGrupoRaza : xmlExplotacion.getExplotacion().getGrupoRazaArray()) {

				BeanGrupoRaza beanGrupoRaza = explotacion.new BeanGrupoRaza();
				GruposRazas grupoRazaHbm = (GruposRazas) polizaDao.get(GruposRazas.class,
						new GruposRazasId(lineaseguroid, Long.valueOf(xmlGrupoRaza.getGrupoRaza())));

				beanGrupoRaza.setCodGrupoRaza(grupoRazaHbm.getId().getCodGrupoRaza().toString());
				beanGrupoRaza.setNomGrupoRaza(grupoRazaHbm.getDescripcion());

				addDatosVariables(datosVariables, xmlGrupoRaza.getDatosVariables(), auxEtiquetaTabla, mapAux);

				if (xmlGrupoRaza.getCapitalAseguradoArray() != null
						&& xmlGrupoRaza.getCapitalAseguradoArray().length != 0) {

					List<BeanTipoCapital> listTipoCap = buidCapitalAsegurado(xmlGrupoRaza, auxEtiquetaTabla,
							datosVariables, beanGrupoRaza, lineaseguroid, mapAux, gruposRaza);

					beanGrupoRaza.setBeanTipoCapital(listTipoCap);
				}
				gruposRaza.add(beanGrupoRaza);
			}

		} catch (Exception e) {
			logger.error("Error al guardar los grupos de raza", e);
			throw e;
		}
		return gruposRaza;
	}

	@SuppressWarnings("rawtypes")
	private List<BeanTipoCapital> buidCapitalAsegurado(GrupoRaza xmlGrupoRaza,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla, List<DatoVariable> datosVariables,
			BeanGrupoRaza beanGrupoRaza, Long lineaseguroid, HashMap mapAux, List<BeanGrupoRaza> grupoRazas)
			throws Exception {

		List<BeanTipoCapital> listTiposCapitales = new ArrayList<BeanTipoCapital>();

		try {
			// Por cada grupo de raza recorremos los capitales asegurados
			for (es.agroseguro.contratacion.explotacion.CapitalAsegurado xmlCapAseg : xmlGrupoRaza
					.getCapitalAseguradoArray()) {

				TipoCapital tipoCapitalHbm = (TipoCapital) polizaDao.get(TipoCapital.class,
						BigDecimal.valueOf(xmlCapAseg.getTipo()));
				BeanTipoCapital beanTipoCapital = beanGrupoRaza.new BeanTipoCapital();
				beanTipoCapital.setCodTipoCapital(tipoCapitalHbm.getCodtipocapital().toString());
				beanTipoCapital.setNomTipoCapital(tipoCapitalHbm.getDestipocapital());

				if (grupoRazas.isEmpty()) {
					addDatosVariables(datosVariables, xmlCapAseg.getDatosVariables(), auxEtiquetaTabla, mapAux);
				} else {
					for (BeanGrupoRaza grRaza : grupoRazas) {
						for (BeanTipoCapital tipCap : grRaza.getBeanTipoCapital()) {
							if (!grRaza.getGrupoRaza().startsWith(String.valueOf(xmlGrupoRaza.getGrupoRaza()))
									&& !tipCap.getTipoCapital().startsWith(
											String.valueOf(tipoCapitalHbm.getCodtipocapital().intValue()))) {
								addDatosVariables(datosVariables, xmlCapAseg.getDatosVariables(), auxEtiquetaTabla,
										mapAux);
							}
						}
					}
				}

				if (xmlCapAseg.getAnimalesArray() != null && xmlCapAseg.getAnimalesArray().length != 0) {

					List<BeanTipoAnimal> listAnimales = buildTipoAnimales(xmlCapAseg, lineaseguroid, auxEtiquetaTabla,
							beanTipoCapital, datosVariables, mapAux);
					beanTipoCapital.setBeanTipoAnimal(listAnimales);

				}
				beanTipoCapital.setDatosVariable(datosVariables);
				datosVariables = new ArrayList<DatoVariable>();
				listTiposCapitales.add(beanTipoCapital);
			}
		} catch (Exception e) {
			logger.error("Error al guardar los tipos de capital", e);
			throw e;
		}
		return listTiposCapitales;
	}

	@SuppressWarnings("rawtypes")
	private List<BeanTipoAnimal> buildTipoAnimales(es.agroseguro.contratacion.explotacion.CapitalAsegurado xmlCapAseg,
			Long lineaseguroid, Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla,
			BeanTipoCapital beanTipoCapital, List<DatoVariable> datosVariables, HashMap mapAux) throws Exception {

		List<BeanTipoAnimal> listAnimales = new ArrayList<BeanTipoAnimal>();

		try {
			// Por cada capital asegurado guardamos los tipos animales
			for (Animales xmlAnimales : xmlCapAseg.getAnimalesArray()) {

				TiposAnimalGanado tipoAnimalHbm = (TiposAnimalGanado) polizaDao.get(TiposAnimalGanado.class,
						new TiposAnimalGanadoId(lineaseguroid, Long.valueOf(xmlAnimales.getTipo())));

				BeanTipoAnimal beanTipoAnimal = beanTipoCapital.new BeanTipoAnimal();

				beanTipoAnimal.setCodTipoAnimal(tipoAnimalHbm.getId().getCodTipoAnimal().toString());
				beanTipoAnimal.setNomTipoAnimal(tipoAnimalHbm.getDescripcion());
				beanTipoAnimal.setPrecio(xmlAnimales.getPrecio());

				addDatosVariables(datosVariables, xmlAnimales.getDatosVariables(), auxEtiquetaTabla, mapAux);

				// 1065 - capacidad productiva
				// 1071 - BIOMASA MEDIA
				// 1072 - NUMERO MEDIO DE ANIMALES
				// 1076 - num colmenas
				if (mapAux.containsKey(new BigDecimal(1065)) || mapAux.containsKey(new BigDecimal(1071))
						|| mapAux.containsKey(new BigDecimal(1072)) || mapAux.containsKey(new BigDecimal(1076))) {
					String valorMap = "";
					if (mapAux.containsKey(new BigDecimal(1065))) {
						valorMap = (String) mapAux.get(new BigDecimal(1065));
					} else if (mapAux.containsKey(new BigDecimal(1071))) {
						valorMap = (String) mapAux.get(new BigDecimal(1071));
					} else if (mapAux.containsKey(new BigDecimal(1072))) {
						valorMap = (String) mapAux.get(new BigDecimal(1072));
					} else if (mapAux.containsKey(new BigDecimal(1076))) {
						valorMap = (String) mapAux.get(new BigDecimal(1076));
					}
					String a[] = valorMap.split("#");
					beanTipoAnimal.setEtiquetaConcepto(a[0]);
					beanTipoAnimal.setNumAnimales(new String(a[1]));

				} else { // es numero animales
					beanTipoAnimal.setEtiquetaConcepto("NUMERO ANIMALES");
					beanTipoAnimal.setNumAnimales("" + xmlAnimales.getNumero());
				}

				listAnimales.add(beanTipoAnimal);
			}
		} catch (Exception e) {
			logger.error("Error al guardar los tipos de animales", e);
			throw e;
		}
		return listAnimales;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addDatosVariables(final List<DatoVariable> datosVariables, final DatosVariables datosVariables2,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta, HashMap mapAux) {
		if (datosVariables2 != null && dvCodConceptoEtiqueta != null && !dvCodConceptoEtiqueta.isEmpty()) {
			String desc = "";
			// 1. Recorrer las claves de auxEtiquetaTabla
			for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
				try {
					// 2. Buscar en los datos variables el valor correspondiente
					// Obtengo el objeto que representa al dato variable
					Class<?> clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
					Method method = clase.getMethod("get" + dvCodConceptoEtiqueta.get(codconcepto).getEtiqueta());
					Object objeto = method.invoke(datosVariables2);
					if (objeto != null) {
						// Obtengo el valor que tiene el objeto en el dato
						// variable.
						Class<?> claseValor = objeto.getClass();
						Method methodValor = claseValor.getMethod(GET_VALOR);
						Object valor = methodValor.invoke(objeto);
						// 3. asigno el valor al dato variable
						if (!StringUtils.nullToString(valor).equals("")) {
							DatoVariable datoVariable = new DatoVariable();
							datoVariable.setNombreConcepto(dvCodConceptoEtiqueta.get(codconcepto).getNombreConcepto());

							if (valor instanceof XmlCalendar) {
								SimpleDateFormat sdf = new SimpleDateFormat(FECHA_DESC);
								SimpleDateFormat sdf2 = new SimpleDateFormat(FECHA_ASC);
								Date d = new Date();
								String fecha = "";
								try {
									d = sdf.parse(valor.toString());
									fecha = sdf2.format(d);
								} catch (ParseException e) {
									logger.error("Error al parsear la fecha en los datos variables ", e);
								}
								datoVariable.setValor(fecha);
							} else {
								datoVariable.setValor(StringUtils.nullToString(valor));
							}
							mapAux.put(codconcepto, datoVariable.getNombreConcepto() + "#" + datoVariable.getValor());
							// 4. Anado la descripcion del dato variable
							try {
								desc = datoVariableDao.getDescDatoVariableGanado(codconcepto,
										StringUtils.nullToString(valor));
							} catch (DAOException e) {
								logger.error(
										"Error al recuperar la descripcion del codConcepto del dato variable codConcepto: "
												+ codconcepto + " valor: " + valor.toString());
							}
							if (desc != null && !desc.equals("")) {
								datoVariable.setDescripcion(desc);
							}

							if (datosVariables != null && !mapAux.containsKey(new BigDecimal(1065))
									&& !mapAux.containsKey(new BigDecimal(1071))
									&& !mapAux.containsKey(new BigDecimal(1072))
									&& !mapAux.containsKey(new BigDecimal(1076))) {
								datosVariables.add(datoVariable);
							}
						}
					}
				} catch (SecurityException e) {
					logger.debug("Error de  seguridad " + e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.debug("El metodo no existe  para esta clase " + e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.debug("El metodo  acepta los argumentos " + e.getMessage());
				} catch (IllegalAccessException e) {
					logger.debug(LOGGER_ERROR + e.getMessage());
				} catch (InvocationTargetException e) {
					logger.debug(LOGGER_ERROR + e.getMessage());
				}
			}
		}
	}

	/**
	 * Metodo que rellena una lista de objetos "parcela + capital asegurado"
	 * partiendo de la informacion obtenida del servicio web de poliza actualizada.
	 * 
	 * @param respuesta
	 *            Objeto con la respuesta del servicio
	 * @return Lista con un elemento por cada capital asegurado, pero tambien con
	 *         los datos de la parcela asociada.
	 */
	private List<BeanParcelaCapitalAsegurado> getListaParcelasCapitales(PolizaActualizadaResponse respuesta,
			Long lineaseguroid) {

		logger.debug("PolizaActualizadaManager - getListaParcelasCapitales");
		// Recorrer las parcelas de la poliza principal y rellenar los datos del objeto

		// Mapa auxiliar con los codigos de concepto de los datos variables y sus
		// etiquetas y tablas asociadas.
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
				.getCodConceptoEtiquetaTablaParcelas(lineaseguroid);

		// Mapa para almacenar los resÃºmenes del valor asegurable por hoja.
		Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas = new HashMap<Integer, Map<String, ResumenValorAsegurable>>();
		Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones = new HashMap<Integer, Map<BigDecimal, ResumenValorAsegurable>>();

		// Mapa auxiliar para el resumen de cosechas aseguradas
		Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada = new HashMap<String, ResumenCosechaAsegurada>();

		// Lista definitiva de parcelas
		Map<String, BeanParcelaCapitalAsegurado> mapaDefinitivo = new HashMap<String, BeanParcelaCapitalAsegurado>();

		// Mapa que contiene las descripciones de todos los tipos de capital de la tabla
		// TB_SC_C_TIPO_CAPITAL indexado por su ccodigodigo
		Map<Integer, String> mapaTiposCapital = getMapaTiposCapital();

		// Por cada capital asegurado creo un objeto BeanParcelaCapitalAsegurado y lo
		// relleno con los datos necesarios
		// es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados =
		// respuesta.getPolizaGanado().getPoliza().getObjetosAsegurados();
		es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados = respuesta.getPolizaPrincipalUnif().getPoliza()
				.getObjetosAsegurados();

		Node node = objetosAsegurados.getDomNode().getFirstChild();

		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				es.agroseguro.contratacion.parcela.ParcelaDocument par = null;
				try {
					par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);
				} catch (XmlException e) {
					logger.error("Error al parsear una Parcela.", e);

				}

				// for (Parcela par :
				// respuesta.getPolizaPrincipal().getPoliza().getObjetosAsegurados()..getParcelaArray()){
				for (es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg : par.getParcela().getCosecha()
						.getCapitalesAsegurados().getCapitalAseguradoArray()) {
					BeanParcelaCapitalAsegurado elemento = new BeanParcelaCapitalAsegurado();

					elemento.setNombre(par.getParcela().getNombre());
					elemento.setHoja(par.getParcela().getHoja());
					elemento.setNumero(par.getParcela().getNumero());
					// elemento.setIdCatastral(par.getIdentificacionCatastral());
					elemento.setIdSigpac(par.getParcela().getSIGPAC());

					// Termino
					elemento.setUbicacion(this.getUbicacion(par.getParcela().getUbicacion().getProvincia(),
							par.getParcela().getUbicacion().getComarca(), par.getParcela().getUbicacion().getTermino(),
							par.getParcela().getUbicacion().getSubtermino()));

					// Cultivo y variedad
					elemento.setVariedad(this.getVariedad(lineaseguroid, par.getParcela().getCosecha().getCultivo(),
							par.getParcela().getCosecha().getVariedad()));

					// Tipo de capital
					elemento.setTipoCapital(this.getTipoCapital(capAseg.getTipo()));

					// Superficie, precio y produccion
					elemento.setSuperficie(capAseg.getSuperficie());
					elemento.setPrecio(capAseg.getPrecio());
					elemento.setProduccion(capAseg.getProduccion());

					// Datos para el resumen del valor asegurable
					// Mapa con clave "descripcion" y como valor tiene las sumas de los valores a
					// mostrar en el resumen de parcelas

					Map<String, ResumenValorAsegurable> lstResumenValorAsegurable = mapaValorAsegurableParcelas
							.get(par.getParcela().getHoja());
					if (lstResumenValorAsegurable == null) {
						lstResumenValorAsegurable = new HashMap<String, ResumenValorAsegurable>();
					}

					// Mapa con clave CODTIPOCAPITAL y como valor tiene las sumas de los valores a
					// mostrar en el resumen de instalaciones
					Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones = mapaValorAsegurableInstalaciones
							.get(par.getParcela().getHoja());
					if (lstResumenValorAsegurableInstalaciones == null) {
						lstResumenValorAsegurableInstalaciones = new HashMap<BigDecimal, ResumenValorAsegurable>();
					}

					// Se procesan los tipos de capital cuyos datos estan almacenados a nivel de
					// capital asegurado (produccion y superficie)
					// CAPITALES PRODUCCION
					if (elemento.getTipoCapital().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PRODUCCION))) {
						// Actualizamos el elemento del indice 1
						ResumenValorAsegurable rva = lstResumenValorAsegurable
								.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
						if (rva == null)
							rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
						rva.setValor(rva.getValor().add(new BigDecimal(capAseg.getProduccion())));
						rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
						rva.setValorAsegurable(rva.getValorAsegurable()
								.add(capAseg.getPrecio().multiply(new BigDecimal(capAseg.getProduccion()))));
						rva.setNumParcelas(rva.getNumParcelas() + 1);
						rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION_KG);
						rva.setIsNegrita(true);

						lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION, rva);

						// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
						String descTipoCapAseg = String.format("%03d", capAseg.getTipo()) + " - "
								+ mapaTiposCapital.get(capAseg.getTipo());
						ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
								.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION + "#" + descTipoCapAseg);
						if (rvaPorTC == null)
							rvaPorTC = new ResumenValorAsegurable(
									Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE + descTipoCapAseg);
						rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(capAseg.getProduccion())));
						rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
						rvaPorTC.setValorAsegurable(rvaPorTC.getValorAsegurable()
								.add(capAseg.getPrecio().multiply(new BigDecimal(capAseg.getProduccion()))));
						rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
						rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION_KG);
						rvaPorTC.setIsNegrita(false);

						lstResumenValorAsegurable.put(
								Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION + "#" + descTipoCapAseg,
								rvaPorTC);

						mapaValorAsegurableParcelas.put(par.getParcela().getHoja(), lstResumenValorAsegurable);
					}
					// CAPITALES SUPERFICIE
					else if (elemento.getTipoCapital().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SUPERFICIE))) {

						// Actualizamos el elemento del indice 1
						ResumenValorAsegurable rva = lstResumenValorAsegurable
								.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE);
						if (rva == null)
							rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE);

						rva.setValor(rva.getValor().add(capAseg.getSuperficie()));
						rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
						rva.setValorAsegurable(
								rva.getValorAsegurable().add(capAseg.getPrecio().multiply(capAseg.getSuperficie())));
						rva.setNumParcelas(rva.getNumParcelas() + 1);
						rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE_HA);
						rva.setIsNegrita(true);

						lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE, rva);

						// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
						String descTipoCapAseg = String.format("%03d", capAseg.getTipo()) + " - "
								+ mapaTiposCapital.get(capAseg.getTipo());
						ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
								.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE + "#" + descTipoCapAseg);
						if (rvaPorTC == null)
							rvaPorTC = new ResumenValorAsegurable(
									Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE + descTipoCapAseg);
						rvaPorTC.setValor(rvaPorTC.getValor().add(capAseg.getSuperficie()));
						rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
						rvaPorTC.setValorAsegurable(rvaPorTC.getValorAsegurable()
								.add(capAseg.getPrecio().multiply(capAseg.getSuperficie())));
						rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
						rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE_HA);
						rvaPorTC.setIsNegrita(false);

						lstResumenValorAsegurable.put(
								Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE + "#" + descTipoCapAseg,
								rvaPorTC);

						mapaValorAsegurableParcelas.put(par.getParcela().getHoja(), lstResumenValorAsegurable);
					}
					// Fin datos resumen valor asegurable

					// Anadimos el elemento actual al resumen de cosechas aseguradas (solo cuento
					// los capitales que no son instalaciones)
					calculaDatosResumenCosechaAsegurada(mapaCosechaAsegurada, par, capAseg, elemento.getTipoCapital());

					// Datos variables:
					List<DatoVariable> datosVariables = new ArrayList<DatoVariable>();
					if (respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura() != null && respuesta
							.getPolizaPrincipalUnif().getPoliza().getCobertura().getDatosVariables() != null) {
						datosVariables = getDatosVariablesParcela(lineaseguroid, auxEtiquetaTabla,
								mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg,
								elemento.getTipoCapital(), lstResumenValorAsegurable,
								lstResumenValorAsegurableInstalaciones,
								respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getDatosVariables(),
								respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo());
						elemento.setDatosVariables(datosVariables);
					} else {
						datosVariables = getDatosVariablesParcela(lineaseguroid, auxEtiquetaTabla,
								mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg,
								elemento.getTipoCapital(), lstResumenValorAsegurable,
								lstResumenValorAsegurableInstalaciones, null,
								respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo());
					}
					// listaDefinitiva.add(elemento);
					mapaDefinitivo.put(
							par.getParcela().getHoja() + "#" + par.getParcela().getNumero() + "#" + capAseg.getTipo(),
							elemento);
				}
			}
			node = node.getNextSibling();
		}

		logger.debug("Ante de validar la produccion de la poliza Complementaria");

		// Incluyo la produccion del complementario de FORMATO ANTIGUO
		if (respuesta.getPolizaComplementaria() != null) {
			logger.debug("Entramos por Complementaria con Formato No Unificado");
			for (es.agroseguro.seguroAgrario.contratacion.complementario.Parcela par : respuesta
					.getPolizaComplementaria().getPoliza().getObjetosAsegurados().getParcelaArray()) {

				for (es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado capAseg : par.getCosecha()
						.getCapitalesAsegurados().getCapitalAseguradoArray()) {

					// Resumen del valor asegurable
					Map<String, ResumenValorAsegurable> lstResumenValorAsegurable = mapaValorAsegurableParcelas
							.get(par.getHoja());
					ResumenValorAsegurable rva = lstResumenValorAsegurable
							.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
					rva.setValor(rva.getValor().add(new BigDecimal(capAseg.getProduccion())));

					lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION, rva);
					mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);

					// Produccion de la complementaria
					BeanParcelaCapitalAsegurado elemento = mapaDefinitivo
							.get(par.getHoja() + "#" + par.getNumero() + "#" + capAseg.getTipo());
					elemento.setProduccionComplementaria(capAseg.getProduccion());
					mapaDefinitivo.put(par.getHoja() + "#" + par.getNumero() + "#" + capAseg.getTipo(), elemento);

					// Resumen de cosechas aseguradas
					calculaDatosResumenCosechaAseguradaComplementariaAnt(mapaCosechaAsegurada, par, capAseg,
							elemento.getTipoCapital());

				}

			}
		} else {

			if (respuesta.getPolizaComplementariaUnif() != null) {

				logger.debug("Entramos por Complementaria con Formato Unificado (Nuevo)");
				logger.debug(
						"respueta.getPolizaComplementariaUnif: " + respuesta.getPolizaComplementariaUnif().toString());

				/* Taty - Inicio */
				es.agroseguro.contratacion.ObjetosAsegurados objetosAseguradosCpl = respuesta
						.getPolizaComplementariaUnif().getPoliza().getObjetosAsegurados();
				logger.debug("objetosAseguradosCpl: " + objetosAseguradosCpl.toString());

				Node nodeCpl = objetosAseguradosCpl.getDomNode().getFirstChild();

				while (nodeCpl != null) {
					if (nodeCpl.getNodeType() == Node.ELEMENT_NODE) {

						es.agroseguro.contratacion.parcela.ParcelaDocument par = null;
						try {
							par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(nodeCpl);
						} catch (XmlException e) {
							logger.error("Error al parsear una parcela", e);

						}

						// for (Parcela par :
						// respuesta.getPolizaPrincipal().getPoliza().getObjetosAsegurados()..getParcelaArray()){
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg : par.getParcela().getCosecha()
								.getCapitalesAsegurados().getCapitalAseguradoArray()) {

							logger.debug("Dentro del form de parcelas - capAseg: " + capAseg.toString());

							// Resumen del valor asegurable
							logger.debug("Antes de obtener el valor Asegurable de la Parcela para la hoja: "
									+ par.getParcela().getHoja());

							Map<String, ResumenValorAsegurable> lstResumenValorAsegurable = mapaValorAsegurableParcelas
									.get(par.getParcela().getHoja());
							if (lstResumenValorAsegurable == null) {
								lstResumenValorAsegurable = new HashMap<String, ResumenValorAsegurable>();
							}

							ResumenValorAsegurable rva = lstResumenValorAsegurable
									.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
							rva.setValor(rva.getValor().add(new BigDecimal(capAseg.getProduccion())));

							lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION, rva);
							mapaValorAsegurableParcelas.put(par.getParcela().getHoja(), lstResumenValorAsegurable);

							logger.debug("Antes de obtener la produccion de la complementaria");
							// Produccion de la complementaria
							BeanParcelaCapitalAsegurado elemento = mapaDefinitivo.get(par.getParcela().getHoja() + "#"
									+ par.getParcela().getNumero() + "#" + capAseg.getTipo());
							elemento.setProduccionComplementaria(capAseg.getProduccion());
							mapaDefinitivo.put(par.getParcela().getHoja() + "#" + par.getParcela().getNumero() + "#"
									+ capAseg.getTipo(), elemento);

							// Resumen de cosechas aseguradas
							logger.debug("Antes de calculaor Datos ResumenCosecha Asegurada");
							calculaDatosResumenCosechaAseguradaComplementaria(mapaCosechaAsegurada, par, capAseg,
									elemento.getTipoCapital());
						} /* Fin del for */
					} /* Fin del if */
					nodeCpl = nodeCpl.getNextSibling();
				} /* Fin del While */

			} /* Fin del if formato nuevo */

		}

		// Convierto el mapa a lista para usarlo como origen de datos en el subinforme
		// de parcelas
		List<BeanParcelaCapitalAsegurado> listaDefinitiva = getListaDefinitivaParcelas(mapaDefinitivo);

		// ordenar las parcelas
		Collections.sort(listaDefinitiva);

		// Anado el mapa con el resumen del valor asegurable de Parcelas
		Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableParcelasDef = getValoresAsegurablesParcelas(
				mapaValorAsegurableParcelas);
		respuesta.setMapaValorAsegurableParcelas(mapaValorAsegurableParcelasDef);

		// Anado el mapa con el resumen del valor asegurable de Instalaciones
		Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableInstalacionesDef = getValoresAsegurablesInstalaciones(
				mapaValorAsegurableInstalaciones);
		respuesta.setMapaValorAsegurableInstalaciones(mapaValorAsegurableInstalacionesDef);

		// Anado la lista con el resumen de cosechas aseguradas
		List<ResumenCosechaAsegurada> lstRca = getCosechasAseguradas(mapaCosechaAsegurada);
		respuesta.setListaCosechasAseguradas(lstRca);

		return listaDefinitiva;
	}

	/* Pet. 57626 ** MODIF TAM (09.07.2020) ** Inicio */
	/**
	 * Metodo que rellena una lista de objetos "parcela + capital asegurado"
	 * partiendo de la informacion obtenida del servicio web de poliza actualizada.
	 * 
	 * @param respuesta
	 *            Objeto con la respuesta del servicio
	 * @return Lista con un elemento por cada capital asegurado, pero tambien con
	 *         los datos de la parcela asociada.
	 */
	private List<BeanParcelaCapitalAsegurado> getListaParcelasCapitalesAnt(PolizaActualizadaResponse respuesta,
			Long lineaseguroid) {
		// Recorrer las parcelas de la poliza principal y rellenar los datos del objeto

		// Mapa auxiliar con los codigos de concepto de los datos variables y sus
		// etiquetas y tablas asociadas.
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
				.getCodConceptoEtiquetaTablaParcelas(lineaseguroid);

		// Mapa para almacenar los resÃºmenes del valor asegurable por hoja.
		Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas = new HashMap<Integer, Map<String, ResumenValorAsegurable>>();
		Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones = new HashMap<Integer, Map<BigDecimal, ResumenValorAsegurable>>();

		// Mapa auxiliar para el resumen de cosechas aseguradas
		Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada = new HashMap<String, ResumenCosechaAsegurada>();

		// Lista definitiva de parcelas
		Map<String, BeanParcelaCapitalAsegurado> mapaDefinitivo = new HashMap<String, BeanParcelaCapitalAsegurado>();

		// Mapa que contiene las descripciones de todos los tipos de capital de la tabla
		// TB_SC_C_TIPO_CAPITAL indexado por su codigo
		Map<Integer, String> mapaTiposCapital = getMapaTiposCapital();

		// Por cada capital asegurado creo un objeto BeanParcelaCapitalAsegurado y lo
		// relleno con los datos necesarios
		for (es.agroseguro.seguroAgrario.contratacion.Parcela par : respuesta.getPolizaPrincipal().getPoliza()
				.getObjetosAsegurados().getParcelaArray()) {
			for (es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado capAseg : par.getCosecha()
					.getCapitalesAsegurados().getCapitalAseguradoArray()) {
				BeanParcelaCapitalAsegurado elemento = new BeanParcelaCapitalAsegurado();

				elemento.setNombre(par.getNombre());
				elemento.setHoja(par.getHoja());
				elemento.setNumero(par.getNumero());
				// elemento.setIdCatastral(par.getIdentificacionCatastral());
				elemento.setIdSigpac(par.getSIGPAC());

				// Termino
				elemento.setUbicacion(
						this.getUbicacion(par.getUbicacion().getProvincia(), par.getUbicacion().getComarca(),
								par.getUbicacion().getTermino(), par.getUbicacion().getSubtermino()));

				// Cultivo y variedad
				elemento.setVariedad(
						this.getVariedad(lineaseguroid, par.getCosecha().getCultivo(), par.getCosecha().getVariedad()));

				// Tipo de capital
				elemento.setTipoCapital(this.getTipoCapital(capAseg.getTipo()));

				// Superficie, precio y produccion
				elemento.setSuperficie(capAseg.getSuperficie());
				elemento.setPrecio(capAseg.getPrecio());
				elemento.setProduccion(capAseg.getProduccion());

				// Datos para el resumen del valor asegurable
				// Mapa con clave "descripcion" y como valor tiene las sumas de los valores a
				// mostrar en el resumen de parcelas
				Map<String, ResumenValorAsegurable> lstResumenValorAsegurable = mapaValorAsegurableParcelas
						.get(par.getHoja());
				if (lstResumenValorAsegurable == null) {
					lstResumenValorAsegurable = new HashMap<String, ResumenValorAsegurable>();
				}

				// Mapa con clave CODTIPOCAPITAL y como valor tiene las sumas de los valores a
				// mostrar en el resumen de instalaciones
				Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones = mapaValorAsegurableInstalaciones
						.get(par.getHoja());
				if (lstResumenValorAsegurableInstalaciones == null) {
					lstResumenValorAsegurableInstalaciones = new HashMap<BigDecimal, ResumenValorAsegurable>();
				}

				// Se procesan los tipos de capital cuyos datos estan almacenados a nivel de
				// capital asegurado (produccion y superficie)
				// CAPITALES PRODUCCION
				if (elemento.getTipoCapital().getCodconcepto()
						.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PRODUCCION))) {
					// Actualizamos el elemento del indice 1
					ResumenValorAsegurable rva = lstResumenValorAsegurable
							.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
					if (rva == null)
						rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
					rva.setValor(rva.getValor().add(new BigDecimal(capAseg.getProduccion())));
					rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
					rva.setValorAsegurable(rva.getValorAsegurable()
							.add(capAseg.getPrecio().multiply(new BigDecimal(capAseg.getProduccion()))));
					rva.setNumParcelas(rva.getNumParcelas() + 1);
					rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION_KG);
					rva.setIsNegrita(true);

					lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION, rva);

					// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
					String descTipoCapAseg = String.format("%03d", capAseg.getTipo()) + " - "
							+ mapaTiposCapital.get(capAseg.getTipo());
					ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
							.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION + "#" + descTipoCapAseg);
					if (rvaPorTC == null)
						rvaPorTC = new ResumenValorAsegurable(
								Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE + descTipoCapAseg);
					rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(capAseg.getProduccion())));
					rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
					rvaPorTC.setValorAsegurable(rvaPorTC.getValorAsegurable()
							.add(capAseg.getPrecio().multiply(new BigDecimal(capAseg.getProduccion()))));
					rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
					rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION_KG);
					rvaPorTC.setIsNegrita(false);

					lstResumenValorAsegurable.put(
							Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION + "#" + descTipoCapAseg, rvaPorTC);

					mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);
				}
				// CAPITALES SUPERFICIE
				else if (elemento.getTipoCapital().getCodconcepto()
						.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SUPERFICIE))) {

					// Actualizamos el elemento del indice 1
					ResumenValorAsegurable rva = lstResumenValorAsegurable
							.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE);
					if (rva == null)
						rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE);

					rva.setValor(rva.getValor().add(capAseg.getSuperficie()));
					rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
					rva.setValorAsegurable(
							rva.getValorAsegurable().add(capAseg.getPrecio().multiply(capAseg.getSuperficie())));
					rva.setNumParcelas(rva.getNumParcelas() + 1);
					rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE_HA);
					rva.setIsNegrita(true);

					lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE, rva);

					// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
					String descTipoCapAseg = String.format("%03d", capAseg.getTipo()) + " - "
							+ mapaTiposCapital.get(capAseg.getTipo());
					ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
							.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE + "#" + descTipoCapAseg);
					if (rvaPorTC == null)
						rvaPorTC = new ResumenValorAsegurable(
								Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE + descTipoCapAseg);
					rvaPorTC.setValor(rvaPorTC.getValor().add(capAseg.getSuperficie()));
					rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
					rvaPorTC.setValorAsegurable(
							rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(capAseg.getSuperficie())));
					rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
					rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE_HA);
					rvaPorTC.setIsNegrita(false);

					lstResumenValorAsegurable.put(
							Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE + "#" + descTipoCapAseg, rvaPorTC);

					mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);
				}
				// Fin datos resumen valor asegurable

				// Anadimos el elemento actual al resumen de cosechas aseguradas (solo cuento
				// los capitales que no son instalaciones)
				calculaDatosResumenCosechaAseguradaAnt(mapaCosechaAsegurada, par, capAseg, elemento.getTipoCapital());

				// Datos variables:
				List<DatoVariable> datosVariables = new ArrayList<DatoVariable>();
				if (respuesta.getPolizaPrincipal().getPoliza().getCobertura() != null
						&& respuesta.getPolizaPrincipal().getPoliza().getCobertura().getDatosVariables() != null) {
					datosVariables = getDatosVariablesParcelaAnt(lineaseguroid, auxEtiquetaTabla,
							mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg,
							elemento.getTipoCapital(), lstResumenValorAsegurable,
							lstResumenValorAsegurableInstalaciones,
							respuesta.getPolizaPrincipal().getPoliza().getCobertura().getDatosVariables(),
							respuesta.getPolizaPrincipal().getPoliza().getCobertura().getModulo());
					elemento.setDatosVariables(datosVariables);
				} else {
					datosVariables = getDatosVariablesParcelaAnt(lineaseguroid, auxEtiquetaTabla,
							mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg,
							elemento.getTipoCapital(), lstResumenValorAsegurable,
							lstResumenValorAsegurableInstalaciones, null,
							respuesta.getPolizaPrincipal().getPoliza().getCobertura().getModulo());
				}
				// listaDefinitiva.add(elemento);
				mapaDefinitivo.put(par.getHoja() + "#" + par.getNumero() + "#" + capAseg.getTipo(), elemento);
			}
		}

		// Incluyo la produccion del complementario
		if (respuesta.getPolizaComplementaria() != null) {
			for (es.agroseguro.seguroAgrario.contratacion.complementario.Parcela par : respuesta
					.getPolizaComplementaria().getPoliza().getObjetosAsegurados().getParcelaArray()) {

				for (es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado capAseg : par.getCosecha()
						.getCapitalesAsegurados().getCapitalAseguradoArray()) {

					// Resumen del valor asegurable
					Map<String, ResumenValorAsegurable> lstResumenValorAsegurable = mapaValorAsegurableParcelas
							.get(par.getHoja());
					ResumenValorAsegurable rva = lstResumenValorAsegurable
							.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION);
					rva.setValor(rva.getValor().add(new BigDecimal(capAseg.getProduccion())));

					lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION, rva);
					mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);

					// Produccion de la complementaria
					BeanParcelaCapitalAsegurado elemento = mapaDefinitivo
							.get(par.getHoja() + "#" + par.getNumero() + "#" + capAseg.getTipo());
					elemento.setProduccionComplementaria(capAseg.getProduccion());
					mapaDefinitivo.put(par.getHoja() + "#" + par.getNumero() + "#" + capAseg.getTipo(), elemento);

					// Resumen de cosechas aseguradas
					calculaDatosResumenCosechaAseguradaComplementariaAnt(mapaCosechaAsegurada, par, capAseg,
							elemento.getTipoCapital());

				}

			}
		}

		// Convierto el mapa a lista para usarlo como origen de datos en el subinforme
		// de parcelas
		List<BeanParcelaCapitalAsegurado> listaDefinitiva = getListaDefinitivaParcelas(mapaDefinitivo);

		// ordenar las parcelas
		Collections.sort(listaDefinitiva);

		// Anado el mapa con el resumen del valor asegurable de Parcelas
		Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableParcelasDef = getValoresAsegurablesParcelas(
				mapaValorAsegurableParcelas);
		respuesta.setMapaValorAsegurableParcelas(mapaValorAsegurableParcelasDef);

		// Anado el mapa con el resumen del valor asegurable de Instalaciones
		Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableInstalacionesDef = getValoresAsegurablesInstalaciones(
				mapaValorAsegurableInstalaciones);
		respuesta.setMapaValorAsegurableInstalaciones(mapaValorAsegurableInstalacionesDef);

		// Anado la lista con el resumen de cosechas aseguradas
		List<ResumenCosechaAsegurada> lstRca = getCosechasAseguradas(mapaCosechaAsegurada);
		respuesta.setListaCosechasAseguradas(lstRca);

		return listaDefinitiva;
	}

	/**
	 * Metodo para calcular los datos a mostrar en el apartado del resumen de la
	 * cosecha asegurada para las parcelas del complementario.
	 * 
	 * @param mapaCosechaAsegurada
	 * @param par
	 * @param capAseg
	 * @param tipoCapital
	 */
	private void calculaDatosResumenCosechaAseguradaAnt(Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada,
			es.agroseguro.seguroAgrario.contratacion.Parcela par,
			es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado capAseg, TipoCapital tipoCapital) {
		if (tipoCapital.getCodtipocapital().compareTo(Constants.TIPOCAPITAL_INSTALACIONES_MINIMO) < 0) {
			String identificador = "" + par.getUbicacion().getProvincia() + par.getUbicacion().getComarca()
					+ par.getUbicacion().getTermino() + par.getCosecha().getCultivo() + par.getCosecha().getVariedad();
			ResumenCosechaAsegurada rca = mapaCosechaAsegurada.get(identificador);
			if (rca == null) {
				rca = new ResumenCosechaAsegurada(par.getUbicacion().getProvincia(), par.getUbicacion().getComarca(),
						par.getUbicacion().getTermino(), par.getCosecha().getCultivo(), par.getCosecha().getVariedad(),
						capAseg.getSuperficie(), new BigDecimal(capAseg.getProduccion()));
			} else {
				rca.setSuperficie(rca.getSuperficie().add(capAseg.getSuperficie()));
				rca.setProduccion(rca.getProduccion().add(new BigDecimal(capAseg.getProduccion())));
			}
			mapaCosechaAsegurada.put(identificador, rca);
		}
	}

	/**
	 * Metodo para calcular los datos a mostrar en el apartado del resumen de la
	 * cosecha asegurada.
	 * 
	 * @param mapaCosechaAsegurada
	 * @param par
	 * @param capAseg
	 * @param tipoCapital
	 */
	private void calculaDatosResumenCosechaAseguradaComplementariaAnt(
			Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada,
			es.agroseguro.seguroAgrario.contratacion.complementario.Parcela par,
			es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado capAseg, TipoCapital tipoCapital) {

		if (tipoCapital.getCodtipocapital().compareTo(Constants.TIPOCAPITAL_INSTALACIONES_MINIMO) < 0) {
			String identificador = "" + par.getUbicacion().getProvincia() + par.getUbicacion().getComarca()
					+ par.getUbicacion().getTermino() + par.getCosecha().getCultivo() + par.getCosecha().getVariedad();

			ResumenCosechaAsegurada rca = mapaCosechaAsegurada.get(identificador);
			rca.setProduccion(rca.getProduccion().add(new BigDecimal(capAseg.getProduccion())));

			mapaCosechaAsegurada.put(identificador, rca);
		}
	}

	/**
	 * Metodo para cargar la lista de datos variables del capital asegurado a partir
	 * de los datos que vienen del serivicio de poliza Actualizada
	 * 
	 * @param lineaseguroid
	 * @param auxEtiquetaTabla
	 * @param mapaValorAsegurableParcelas
	 * @param mapaValorAsegurableInstalaciones
	 * @param par
	 * @param capAseg
	 * @param elemento
	 * @param lstResumenValorAsegurable
	 * @param lstResumenValorAsegurableInstalaciones
	 * @param datosVarCob
	 * @param modulo
	 * @return
	 */
	private List<DatoVariable> getDatosVariablesParcelaAnt(Long lineaseguroid,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla,
			Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas,
			Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones,
			es.agroseguro.seguroAgrario.contratacion.Parcela par,
			es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado capAseg, TipoCapital tipoCapital,
			Map<String, ResumenValorAsegurable> lstResumenValorAsegurable,
			Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones,
			es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables datosVarCob, String modulo) {

		List<DatoVariable> datosVariables = new ArrayList<DatoVariable>();
		DatoVariable dv = new DatoVariable();
		// 1. Recorrer las claves de auxEtiquetaTabla
		for (BigDecimal codconcepto : auxEtiquetaTabla.keySet()) {
			try {
				// 2. Buscar en los datos variables del capital asegurado el valor
				// correspondiente

				// primero obtengo el objeto que representa al dato variable
				Method method = es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables.class
						.getMethod("get" + auxEtiquetaTabla.get(codconcepto).getEtiqueta());
				Object objeto = method.invoke(capAseg.getDatosVariables());
				if (objeto != null) {
					dv = setDatoVariableToParcelaAnt(objeto, auxEtiquetaTabla, lineaseguroid, codconcepto,
							mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg, tipoCapital,
							lstResumenValorAsegurable, lstResumenValorAsegurableInstalaciones);
					datosVariables.add(dv);
				}
				if (datosVarCob != null) {
					Object objeto2 = method.invoke(datosVarCob);
					if (objeto2 != null) {
						dv = setDatoVariableToParcelaAnt(objeto2, auxEtiquetaTabla, lineaseguroid, codconcepto,
								mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg,
								tipoCapital, lstResumenValorAsegurable, lstResumenValorAsegurableInstalaciones);
						datosVariables.add(dv);
					}
				}
			} catch (SecurityException e) {
				logger.debug("Error  de seguridad " + e.getMessage());
			} catch (NoSuchMethodException e) {
				logger.debug("El metodo no  existe para esta clase " + e.getMessage());
			} catch (IllegalArgumentException e) {
				logger.debug("El  metodo acepta los argumentos " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.debug(LOGGER_ERROR + e.getMessage());
			} catch (InvocationTargetException e) {
				logger.debug(LOGGER_ERROR + e.getMessage());
			}
		}

		// Recorremos el array de fechas fin garantia, y si tiene anadimos el valor
		// a la lista como otro dato variable mas
		for (es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaLimiteGarantias ffg : capAseg
				.getDatosVariables().getFecFGarantArray()) {

			if (ffg.getValor() != null) {

				SimpleDateFormat sdf = new SimpleDateFormat(FECHA_DESC);
				SimpleDateFormat sdf2 = new SimpleDateFormat(FECHA_ASC);
				Date d = new Date();
				String fecha = "";
				try {
					d = sdf.parse(ffg.getValor().toString());
					fecha = sdf2.format(d);
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha en los datos  variables", e);
				}
				dv = new DatoVariable();
				dv.setNombreConcepto(Constants.TXT_FECFGARANT);
				dv.setValor(fecha);
				datosVariables.add(dv);
				break;
			}
		}
		// recorremos el array de riesgos cubiertos elegidos
		for (es.agroseguro.seguroAgrario.contratacion.datosVariables.RiesgoCubiertoElegido rce : capAseg
				.getDatosVariables().getRiesgCbtoElegArray()) {
			if (rce.getValor() != null) {
				RiesgoCubierto rc = this.diccionarioDatosDao.getRiesgosElegidos(lineaseguroid, modulo,
						rce.getCodRCub());
				if (rc != null) {
					dv = new DatoVariable();
					dv.setNombreConcepto(Constants.TXT_RIESGOCUBELEG + " - " + rc.getDesriesgocubierto());
					dv.setValor(rce.getValor());
					datosVariables.add(dv);

				}
			}
		}

		return datosVariables;
	}

	private DatoVariable setDatoVariableToParcelaAnt(Object objeto,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla, Long lineaseguroid, BigDecimal codconcepto,
			Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas,
			Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones,
			es.agroseguro.seguroAgrario.contratacion.Parcela par,
			es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado capAseg, TipoCapital tipoCapital,
			Map<String, ResumenValorAsegurable> lstResumenValorAsegurable,
			Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones) {

		DatoVariable dv = null;
		try {
			// despues obtengo el valor que tiene el objeto en el dato variable.
			Method methodValor = objeto.getClass().getMethod(GET_VALOR);
			Object valor = methodValor.invoke(objeto);

			// 3. Si es necesario, obtener la descripcion para el valor (con origen de datos
			// en la pantalla de parcelas).
			if (!StringUtils.nullToString(valor).equals("")) {
				// Si dispongo del valor...
				String descripcion = "";
				if (!StringUtils.nullToString(auxEtiquetaTabla.get(codconcepto).getTabla()).equals("0")) {
					// Busco la descripcion (vw_datos_variables_parcela)
					try {
						descripcion = " - " + this.datoVariableDao.getDescripcionDatoVariable(lineaseguroid,
								codconcepto, valor + "");
					} catch (DAOException e) {
						descripcion = "";
					}
				}
				// Creo un objeto "DatoVariable" y lo anado a la lista con sus datos
				dv = new DatoVariable();
				dv.setNombreConcepto(auxEtiquetaTabla.get(codconcepto).getNombreConcepto());
				if (valor instanceof XmlCalendar) {
					SimpleDateFormat sdf = new SimpleDateFormat(FECHA_DESC);
					SimpleDateFormat sdf2 = new SimpleDateFormat(FECHA_ASC);
					Date d = new Date();
					String fecha = "";
					try {
						d = sdf.parse(valor.toString());
						fecha = sdf2.format(d);
					} catch (ParseException e) {
						logger.error("Error al parsear la fecha en los  datos variables", e);
					}

					dv.setValor(fecha);
				} else {
					dv.setValor(StringUtils.nullToString(valor) + descripcion);
				}

				// Datos para el resumen del valor asegurable
				calculaDatosResumenValorAsegurableAnt(mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones,
						par, capAseg, tipoCapital, lstResumenValorAsegurable, lstResumenValorAsegurableInstalaciones,
						codconcepto, valor);
			}
		} catch (SecurityException e) {
			logger.debug(" Error de seguridad " + e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("El metodo  no existe para esta clase " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.debug("El metodo acepta los argumentos  " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.debug(LOGGER_ERROR + e.getMessage());
		} catch (InvocationTargetException e) {
			logger.debug(LOGGER_ERROR + e.getMessage());
		}
		return dv;

	}

	/**
	 * Metodo para ir calculando el resumen del valor asegurable por cada
	 * parcela/capital asegurado
	 * 
	 * @param mapaValorAsegurableParcelas
	 * @param mapaValorAsegurableInstalaciones
	 * @param par
	 * @param capAseg
	 * @param elemento
	 * @param lstResumenValorAsegurable
	 * @param lstResumenValorAsegurableInstalaciones
	 * @param codconcepto
	 * @param valor
	 */
	private void calculaDatosResumenValorAsegurableAnt(
			Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas,
			Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones,
			es.agroseguro.seguroAgrario.contratacion.Parcela par,
			es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado capAseg, TipoCapital tipoCapital,
			Map<String, ResumenValorAsegurable> lstResumenValorAsegurable,
			Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones, BigDecimal codconcepto,
			Object valor) {

		// Se procesan los tipos de capital cuyos datos estan almacenados a nivel de
		// datos variables (unidades, metros cuadrados y lineales)

		// CAPITALES UNIDADES
		// Si el tipo de capital y el dato variable pertenecen al concepto de unidades
		if (tipoCapital.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_UNIDADES))
				&& codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_UNIDADES))) {

			ResumenValorAsegurable rva = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES);
			if (rva == null)
				rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES);
			rva.setValor(rva.getValor().add(new BigDecimal(valor.toString())));
			rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
			rva.setValorAsegurable(
					rva.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rva.setNumParcelas(rva.getNumParcelas() + 1);
			rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES_UDS);
			rva.setIsNegrita(true);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES, rva);

			// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
			String descTipoCapAseg = Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE
					+ String.format("%03d", tipoCapital.getCodtipocapital().intValue()) + " - "
					+ tipoCapital.getDestipocapital();
			ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES + "#" + descTipoCapAseg);
			if (rvaPorTC == null)
				rvaPorTC = new ResumenValorAsegurable(descTipoCapAseg);
			rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(valor.toString())));
			rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
			rvaPorTC.setValorAsegurable(
					rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
			rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES_UDS);
			rvaPorTC.setIsNegrita(false);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES + "#" + descTipoCapAseg,
					rvaPorTC);

			mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);
		}
		// CAPITALES METROS CUADRADOS
		// Si el tipo de capital y el dato variable pertenecen al concepto de metros
		// cuadrados
		else if (tipoCapital.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))
				&& codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))) {

			ResumenValorAsegurable rva = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS);
			if (rva == null)
				rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS);
			rva.setValor(rva.getValor().add(new BigDecimal(valor.toString())));
			rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
			rva.setValorAsegurable(
					rva.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rva.setNumParcelas(rva.getNumParcelas() + 1);
			rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS_M2);
			rva.setIsNegrita(true);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS, rva);

			// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
			String descTipoCapAseg = Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE
					+ String.format("%03d", tipoCapital.getCodtipocapital().intValue()) + " - "
					+ tipoCapital.getDestipocapital();
			ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS + "#" + descTipoCapAseg);
			if (rvaPorTC == null)
				rvaPorTC = new ResumenValorAsegurable(descTipoCapAseg);
			rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(valor.toString())));
			rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
			rvaPorTC.setValorAsegurable(
					rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
			rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS_M2);
			rvaPorTC.setIsNegrita(false);

			lstResumenValorAsegurable
					.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS + "#" + descTipoCapAseg, rvaPorTC);

			mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);
		}
		// CAPITALES METROS LINEALES
		// Si el tipo de capital y el dato variable pertenecen al concepto de metros
		// lineales
		else if (tipoCapital.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))
				&& codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))) {

			ResumenValorAsegurable rva = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES);
			if (rva == null)
				rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES);
			rva.setValor(rva.getValor().add(new BigDecimal(valor.toString())));
			rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
			rva.setValorAsegurable(
					rva.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rva.setNumParcelas(rva.getNumParcelas() + 1);
			rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES_ML);
			rva.setIsNegrita(true);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES, rva);

			// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
			String descTipoCapAseg = Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE
					+ String.format("%03d", tipoCapital.getCodtipocapital().intValue()) + " - "
					+ tipoCapital.getDestipocapital();
			ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES + "#" + descTipoCapAseg);
			if (rvaPorTC == null)
				rvaPorTC = new ResumenValorAsegurable(descTipoCapAseg);
			rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(valor.toString())));
			rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
			rvaPorTC.setValorAsegurable(
					rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
			rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES_ML);
			rvaPorTC.setIsNegrita(false);

			lstResumenValorAsegurable
					.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES + "#" + descTipoCapAseg, rvaPorTC);

			mapaValorAsegurableParcelas.put(par.getHoja(), lstResumenValorAsegurable);
		}

	}

	/* Pet. 57626 ** MODIF TAM (09.07.2020) ** Fin */

	/**
	 * @return Mapa que contiene las descripciones de todos los tipos de capital de
	 *         la tabla TB_SC_C_TIPO_CAPITAL indexado por su codigo
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> getMapaTiposCapital() {
		Map<Integer, String> mapaTiposCapital = new HashMap<Integer, String>();
		List<TipoCapital> listaTC = polizaDao.getObjects(TipoCapital.class, null, null);
		for (TipoCapital tipoCapital : listaTC) {
			mapaTiposCapital.put(tipoCapital.getCodtipocapital().intValue(), tipoCapital.getDestipocapital());
		}
		return mapaTiposCapital;
	}

	/**
	 * Metodo para cargar la lista de datos variables del capital asegurado a partir
	 * de los datos que vienen del serivicio de poliza Actualizada
	 * 
	 * @param lineaseguroid
	 * @param auxEtiquetaTabla
	 * @param mapaValorAsegurableParcelas
	 * @param mapaValorAsegurableInstalaciones
	 * @param par
	 * @param capAseg
	 * @param elemento
	 * @param lstResumenValorAsegurable
	 * @param lstResumenValorAsegurableInstalaciones
	 * @param datosVarCob
	 * @param modulo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<DatoVariable> getDatosVariablesParcela(Long lineaseguroid,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla,
			Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas,
			Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones,
			es.agroseguro.contratacion.parcela.ParcelaDocument par,
			es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg, TipoCapital tipoCapital,
			Map<String, ResumenValorAsegurable> lstResumenValorAsegurable,
			Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVarCob, String modulo) {

		logger.debug("PolizaActualizadaManager - Dentro de getDatosVariablesParcela");

		List<DatoVariable> datosVariables = new ArrayList<DatoVariable>();
		DatoVariable dv = new DatoVariable();
		// 1. Recorrer las claves de auxEtiquetaTabla
		for (BigDecimal codconcepto : auxEtiquetaTabla.keySet()) {
			try {
				// 2. Buscar en los datos variables del capital asegurado el valor
				// correspondiente

				// primero obtengo el objeto que representa al dato variable

				// Class clase =
				// es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables.class;
				Class clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
				Method method = clase.getMethod("get" + auxEtiquetaTabla.get(codconcepto).getEtiqueta());

				Object objeto = method.invoke(capAseg.getDatosVariables());
				if (objeto != null) {
					dv = setDatoVariableToParcela(objeto, auxEtiquetaTabla, lineaseguroid, codconcepto,
							mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg, tipoCapital,
							lstResumenValorAsegurable, lstResumenValorAsegurableInstalaciones);
					datosVariables.add(dv);
				}
				if (datosVarCob != null) {
					Object objeto2 = method.invoke(datosVarCob);
					if (objeto2 != null) {
						dv = setDatoVariableToParcela(objeto2, auxEtiquetaTabla, lineaseguroid, codconcepto,
								mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par, capAseg,
								tipoCapital, lstResumenValorAsegurable, lstResumenValorAsegurableInstalaciones);
						datosVariables.add(dv);
					}
				}
			} catch (SecurityException e) {
				logger.debug("Error de seguridad " + e.getMessage());
			} catch (NoSuchMethodException e) {
				logger.debug("El metodo no existe para esta clase  " + e.getMessage());
			} catch (IllegalArgumentException e) {
				logger.debug("El metodo acepta los  argumentos " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.debug(LOGGER_ERROR + e.getMessage());
			} catch (InvocationTargetException e) {
				logger.debug(LOGGER_ERROR + e.getMessage());
			}
		}

		// Recorremos el array de fechas fin garantia, y si tiene anadimos el valor
		// a la lista como otro dato variable mas
		for (es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias ffg : capAseg.getDatosVariables()
				.getFecFGarantArray()) {

			if (ffg.getValor() != null) {

				SimpleDateFormat sdf = new SimpleDateFormat(FECHA_DESC);
				SimpleDateFormat sdf2 = new SimpleDateFormat(FECHA_ASC);
				Date d = new Date();
				String fecha = "";
				try {
					d = sdf.parse(ffg.getValor().toString());
					fecha = sdf2.format(d);
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha en  los datos variables", e);
				}
				dv = new DatoVariable();
				dv.setNombreConcepto(Constants.TXT_FECFGARANT);
				dv.setValor(fecha);
				datosVariables.add(dv);
				break;
			}
		}
		// recorremos el array de riesgos cubiertos elegidos
		
		/* Incidencia RGA (10/12/2020) ** Inicio ** Pet. 63485-FII */
		/*for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : capAseg.getDatosVariables()
				.getRiesgCbtoElegArray()) {
			if (rce.getValor() != null) {
				RiesgoCubierto rc = this.diccionarioDatosDao.getRiesgosElegidos(lineaseguroid, modulo,
						rce.getCodRCub());
				if (rc != null) {
					dv = new DatoVariable();
					dv.setNombreConcepto(Constants.TXT_RIESGOCUBELEG + " - " + rc.getDesriesgocubierto());
					dv.setValor(rce.getValor());
					datosVariables.add(dv);

				}
			}
		}*/
		/* Incidencia RGA (10/12/2020) ** Fin ** Pet. 63485-FII */

		return datosVariables;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DatoVariable setDatoVariableToParcela(Object objeto,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla, Long lineaseguroid, BigDecimal codconcepto,
			Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas,
			Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones,
			es.agroseguro.contratacion.parcela.ParcelaDocument par,
			es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg, TipoCapital tipoCapital,
			Map<String, ResumenValorAsegurable> lstResumenValorAsegurable,
			Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones) {

		logger.debug("PolizaActualizadaManager - setDatoVariableToParcela");
		logger.debug("Valor de codconcepto:" + codconcepto);
		DatoVariable dv = null;
		try {
			// despues obtengo el valor que tiene el objeto en el dato variable.
			Class claseValor = objeto.getClass();
			Method methodValor = claseValor.getMethod(GET_VALOR);
			Object valor = methodValor.invoke(objeto);

			// 3. Si es necesario, obtener la descripcion para el valor (con origen de datos
			// en la pantalla de parcelas).
			if (!StringUtils.nullToString(valor).equals("")) {
				// Si dispongo del valor...
				String descripcion = "";
				if (!StringUtils.nullToString(auxEtiquetaTabla.get(codconcepto).getTabla()).equals("0")) {
					// Busco la descripcion (vw_datos_variables_parcela)
					try {
						descripcion = " - " + this.datoVariableDao.getDescripcionDatoVariable(lineaseguroid,
								codconcepto, valor + "");
					} catch (DAOException e) {
						descripcion = "";
					}
				}
				// Creo un objeto "DatoVariable" y lo anado a la lista con sus datos
				dv = new DatoVariable();
				dv.setNombreConcepto(auxEtiquetaTabla.get(codconcepto).getNombreConcepto());
				if (valor instanceof XmlCalendar) {
					SimpleDateFormat sdf = new SimpleDateFormat(FECHA_DESC);
					SimpleDateFormat sdf2 = new SimpleDateFormat(FECHA_ASC);
					Date d = new Date();
					String fecha = "";
					try {
						d = sdf.parse(valor.toString());
						fecha = sdf2.format(d);
					} catch (ParseException e) {
						logger.error("Error al parsear la fecha en los datos variables", e);
					}

					dv.setValor(fecha);
				} else {
					dv.setValor(StringUtils.nullToString(valor) + descripcion);
				}

				// Datos para el resumen del valor asegurable
				calculaDatosResumenValorAsegurable(mapaValorAsegurableParcelas, mapaValorAsegurableInstalaciones, par,
						capAseg, tipoCapital, lstResumenValorAsegurable, lstResumenValorAsegurableInstalaciones,
						codconcepto, valor);
			}
		} catch (SecurityException e) {
			logger.debug("Error de seguridad  " + e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("El metodo no existe para esta  clase " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.debug("El metodo acepta  los argumentos " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.debug(LOGGER_ERROR + e.getMessage());
		} catch (InvocationTargetException e) {
			logger.debug(LOGGER_ERROR + e.getMessage());
		}
		return dv;

	}

	/**
	 * Metodo para ir calculando el resumen del valor asegurable por cada
	 * parcela/capital asegurado
	 * 
	 * @param mapaValorAsegurableParcelas
	 * @param mapaValorAsegurableInstalaciones
	 * @param par
	 * @param capAseg
	 * @param elemento
	 * @param lstResumenValorAsegurable
	 * @param lstResumenValorAsegurableInstalaciones
	 * @param codconcepto
	 * @param valor
	 */
	private void calculaDatosResumenValorAsegurable(
			Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurableParcelas,
			Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurableInstalaciones,
			es.agroseguro.contratacion.parcela.ParcelaDocument par,
			es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg, TipoCapital tipoCapital,
			Map<String, ResumenValorAsegurable> lstResumenValorAsegurable,
			Map<BigDecimal, ResumenValorAsegurable> lstResumenValorAsegurableInstalaciones, BigDecimal codconcepto,
			Object valor) {

		// Se procesan los tipos de capital cuyos datos estan almacenados a nivel de
		// datos variables (unidades, metros cuadrados y lineales)

		// CAPITALES UNIDADES
		// Si el tipo de capital y el dato variable pertenecen al concepto de unidades
		if (tipoCapital.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_UNIDADES))
				&& codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_UNIDADES))) {

			ResumenValorAsegurable rva = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES);
			if (rva == null)
				rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES);
			rva.setValor(rva.getValor().add(new BigDecimal(valor.toString())));
			rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
			rva.setValorAsegurable(
					rva.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rva.setNumParcelas(rva.getNumParcelas() + 1);
			rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES_UDS);
			rva.setIsNegrita(true);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES, rva);

			// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
			String descTipoCapAseg = Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE
					+ String.format("%03d", tipoCapital.getCodtipocapital().intValue()) + " - "
					+ tipoCapital.getDestipocapital();
			ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES + "#" + descTipoCapAseg);
			if (rvaPorTC == null)
				rvaPorTC = new ResumenValorAsegurable(descTipoCapAseg);
			rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(valor.toString())));
			rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
			rvaPorTC.setValorAsegurable(
					rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
			rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES_UDS);
			rvaPorTC.setIsNegrita(false);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES + "#" + descTipoCapAseg,
					rvaPorTC);

			mapaValorAsegurableParcelas.put(par.getParcela().getHoja(), lstResumenValorAsegurable);
		}
		// CAPITALES METROS CUADRADOS
		// Si el tipo de capital y el dato variable pertenecen al concepto de metros
		// cuadrados
		else if (tipoCapital.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))
				&& codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))) {

			ResumenValorAsegurable rva = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS);
			if (rva == null)
				rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS);
			rva.setValor(rva.getValor().add(new BigDecimal(valor.toString())));
			rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
			rva.setValorAsegurable(
					rva.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rva.setNumParcelas(rva.getNumParcelas() + 1);
			rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS_M2);
			rva.setIsNegrita(true);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS, rva);

			// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
			String descTipoCapAseg = Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE
					+ String.format("%03d", tipoCapital.getCodtipocapital().intValue()) + " - "
					+ tipoCapital.getDestipocapital();
			ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS + "#" + descTipoCapAseg);
			if (rvaPorTC == null)
				rvaPorTC = new ResumenValorAsegurable(descTipoCapAseg);
			rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(valor.toString())));
			rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
			rvaPorTC.setValorAsegurable(
					rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
			rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS_M2);
			rvaPorTC.setIsNegrita(false);

			lstResumenValorAsegurable
					.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS + "#" + descTipoCapAseg, rvaPorTC);

			mapaValorAsegurableParcelas.put(par.getParcela().getHoja(), lstResumenValorAsegurable);
		}
		// CAPITALES METROS LINEALES
		// Si el tipo de capital y el dato variable pertenecen al concepto de metros
		// lineales
		else if (tipoCapital.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))
				&& codconcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_LINEALES))) {

			ResumenValorAsegurable rva = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES);
			if (rva == null)
				rva = new ResumenValorAsegurable(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES);
			rva.setValor(rva.getValor().add(new BigDecimal(valor.toString())));
			rva.setSuperficie(rva.getSuperficie().add(capAseg.getSuperficie()));
			rva.setValorAsegurable(
					rva.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rva.setNumParcelas(rva.getNumParcelas() + 1);
			rva.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES_ML);
			rva.setIsNegrita(true);

			lstResumenValorAsegurable.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES, rva);

			// MPM - Se rellena el mapa de Resumen del Valor Asegurable por tipo de capital
			String descTipoCapAseg = Constants.RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE
					+ String.format("%03d", tipoCapital.getCodtipocapital().intValue()) + " - "
					+ tipoCapital.getDestipocapital();
			ResumenValorAsegurable rvaPorTC = lstResumenValorAsegurable
					.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES + "#" + descTipoCapAseg);
			if (rvaPorTC == null)
				rvaPorTC = new ResumenValorAsegurable(descTipoCapAseg);
			rvaPorTC.setValor(rvaPorTC.getValor().add(new BigDecimal(valor.toString())));
			rvaPorTC.setSuperficie(rvaPorTC.getSuperficie().add(capAseg.getSuperficie()));
			rvaPorTC.setValorAsegurable(
					rvaPorTC.getValorAsegurable().add(capAseg.getPrecio().multiply(new BigDecimal(valor.toString()))));
			rvaPorTC.setNumParcelas(rvaPorTC.getNumParcelas() + 1);
			rvaPorTC.setUnidadMedida(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES_ML);
			rvaPorTC.setIsNegrita(false);

			lstResumenValorAsegurable
					.put(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES + "#" + descTipoCapAseg, rvaPorTC);

			mapaValorAsegurableParcelas.put(par.getParcela().getHoja(), lstResumenValorAsegurable);
		}

	}

	/**
	 * Metodo para calcular los datos a mostrar en el apartado del resumen de la
	 * cosecha asegurada.
	 * 
	 * @param mapaCosechaAsegurada
	 * @param par
	 * @param capAseg
	 * @param tipoCapital
	 */
	private void calculaDatosResumenCosechaAseguradaComplementaria(
			Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada,
			es.agroseguro.contratacion.parcela.ParcelaDocument par,
			es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg, TipoCapital tipoCapital) {

		if (tipoCapital.getCodtipocapital().compareTo(Constants.TIPOCAPITAL_INSTALACIONES_MINIMO) < 0) {
			String identificador = "" + par.getParcela().getUbicacion().getProvincia()
					+ par.getParcela().getUbicacion().getComarca() + par.getParcela().getUbicacion().getTermino()
					+ par.getParcela().getCosecha().getCultivo() + par.getParcela().getCosecha().getVariedad();

			ResumenCosechaAsegurada rca = mapaCosechaAsegurada.get(identificador);
			rca.setProduccion(rca.getProduccion().add(new BigDecimal(capAseg.getProduccion())));

			mapaCosechaAsegurada.put(identificador, rca);
		}
	}

	/**
	 * Metodo para calcular los datos a mostrar en el apartado del resumen de la
	 * cosecha asegurada para las parcelas del complementario.
	 * 
	 * @param mapaCosechaAsegurada
	 * @param par
	 * @param capAseg
	 * @param tipoCapital
	 */
	private void calculaDatosResumenCosechaAsegurada(Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada,
			es.agroseguro.contratacion.parcela.ParcelaDocument par,
			es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg, TipoCapital tipoCapital) {
		if (tipoCapital.getCodtipocapital().compareTo(Constants.TIPOCAPITAL_INSTALACIONES_MINIMO) < 0) {
			String identificador = "" + par.getParcela().getUbicacion().getProvincia()
					+ par.getParcela().getUbicacion().getComarca() + par.getParcela().getUbicacion().getTermino()
					+ par.getParcela().getCosecha().getCultivo() + par.getParcela().getCosecha().getVariedad();
			ResumenCosechaAsegurada rca = mapaCosechaAsegurada.get(identificador);
			if (rca == null) {
				rca = new ResumenCosechaAsegurada(par.getParcela().getUbicacion().getProvincia(),
						par.getParcela().getUbicacion().getComarca(), par.getParcela().getUbicacion().getTermino(),
						par.getParcela().getCosecha().getCultivo(), par.getParcela().getCosecha().getVariedad(),
						capAseg.getSuperficie(), new BigDecimal(capAseg.getProduccion()));
			} else {
				rca.setSuperficie(rca.getSuperficie().add(capAseg.getSuperficie()));
				rca.setProduccion(rca.getProduccion().add(new BigDecimal(capAseg.getProduccion())));
			}
			mapaCosechaAsegurada.put(identificador, rca);
		}
	}

	/**
	 * Metodo que elimina los registros que no tienen valores para que no se
	 * muestren en el informe
	 * 
	 * @param mapaValorAsegurable
	 *            Mapa con los registros para el cuadro del resumen del valor
	 *            asegurable de PARCELAS por hoja
	 */
	private Map<Integer, List<ResumenValorAsegurable>> getValoresAsegurablesParcelas(
			final Map<Integer, Map<String, ResumenValorAsegurable>> mapaValorAsegurable) {
		Map<Integer, List<ResumenValorAsegurable>> mapa = new HashMap<Integer, List<ResumenValorAsegurable>>();
		// Para cada hoja
		for (Integer clave : mapaValorAsegurable.keySet()) {

			// Creamos una lista de ResumenValorAsegurable
			List<ResumenValorAsegurable> lstDefPar = new ArrayList<ResumenValorAsegurable>();
			Map<String, ResumenValorAsegurable> rvaMap = mapaValorAsegurable.get(clave);

			// Preguntamos por cada propiedad para anadirla en el orden adecuado
			// CAPITALES PRODUCCION
			if (rvaMap.containsKey(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION)) {

				// Se anade a la lista el Resumen del Valor Asegurable total para capitales de
				// produccion
				lstDefPar.add(rvaMap.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION));

				// Se busca en el mapa los desgloses por tipo de capital para los capitales de
				// produccion y se anaden a la lista
				for (String claveTC : rvaMap.keySet()) {
					if (claveTC.contains(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION + "#")) {
						lstDefPar.add(rvaMap.get(claveTC));
					}
				}
			}
			// CAPITALES UNIDADES
			if (rvaMap.containsKey(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES)) {
				lstDefPar.add(rvaMap.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES));

				// Se busca en el mapa los desgloses por tipo de capital para los capitales de
				// unidades y se anaden a la lista
				for (String claveTC : rvaMap.keySet()) {
					if (claveTC.contains(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES + "#")) {
						lstDefPar.add(rvaMap.get(claveTC));
					}
				}
			}

			// CAPITALES METROS CUADRADOS
			if (rvaMap.containsKey(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS)) {
				lstDefPar.add(rvaMap.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS));

				// Se busca en el mapa los desgloses por tipo de capital para los capitales de
				// metros cuadrados y se anaden a la lista
				for (String claveTC : rvaMap.keySet()) {
					if (claveTC.contains(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS + "#")) {
						lstDefPar.add(rvaMap.get(claveTC));
					}
				}
			}

			// CAPITALES METROS LINEALES
			if (rvaMap.containsKey(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES)) {
				lstDefPar.add(rvaMap.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES));

				// Se busca en el mapa los desgloses por tipo de capital para los capitales de
				// metros lineales y se anaden a la lista
				for (String claveTC : rvaMap.keySet()) {
					if (claveTC.contains(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES + "#")) {
						lstDefPar.add(rvaMap.get(claveTC));
					}
				}
			}

			// CAPITALES SUPERFICIE
			if (rvaMap.containsKey(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE)) {
				lstDefPar.add(rvaMap.get(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE));

				// Se busca en el mapa los desgloses por tipo de capital para los capitales de
				// superficie y se anaden a la lista
				for (String claveTC : rvaMap.keySet()) {
					if (claveTC.contains(Constants.RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE + "#")) {
						lstDefPar.add(rvaMap.get(claveTC));
					}
				}
			}

			mapa.put(clave, lstDefPar);
		}
		return mapa;
	}

	/**
	 * Metodo que elimina los registros que no tienen valores para que no se
	 * muestren en el informe
	 * 
	 * @param mapaValorAsegurable
	 *            Mapa con los registros para el cuadro del resumen del valor
	 *            asegurable de INSTALACIONES por hoja
	 */
	private Map<Integer, List<ResumenValorAsegurable>> getValoresAsegurablesInstalaciones(
			final Map<Integer, Map<BigDecimal, ResumenValorAsegurable>> mapaValorAsegurable) {
		Map<Integer, List<ResumenValorAsegurable>> mapa = new HashMap<Integer, List<ResumenValorAsegurable>>();
		// Para cada hoja
		for (Integer clave : mapaValorAsegurable.keySet()) {

			// Creamos una lista de ResumenValorAsegurable
			List<ResumenValorAsegurable> lstDefInst = new ArrayList<ResumenValorAsegurable>();

			Map<BigDecimal, ResumenValorAsegurable> rvaMap = mapaValorAsegurable.get(clave);
			for (BigDecimal tipoCapital : rvaMap.keySet()) {
				lstDefInst.add(rvaMap.get(tipoCapital));
			}

			mapa.put(clave, lstDefInst);
		}
		return mapa;
	}

	/**
	 * Metodo para rellenar la lista con los valores para el resumen de las cosechas
	 * aseguradas
	 * 
	 * @param mapaCosechaAsegurada
	 *            Mapa con los datos por ubicacion y cosecha
	 * @return Lista con los datos para el resumen de cosechas aseguradas
	 */
	private List<ResumenCosechaAsegurada> getCosechasAseguradas(
			Map<String, ResumenCosechaAsegurada> mapaCosechaAsegurada) {
		List<ResumenCosechaAsegurada> lstRca = new ArrayList<ResumenCosechaAsegurada>();

		for (String key : mapaCosechaAsegurada.keySet()) {
			lstRca.add(mapaCosechaAsegurada.get(key));
		}

		return lstRca;
	}

	/**
	 * Metodo para rellenar la lista de parcelas para el informe
	 * 
	 * @param mapaDefinitivo
	 *            Mapa con las parcelas de la(s) poliza(s)
	 * @return
	 */
	private List<BeanParcelaCapitalAsegurado> getListaDefinitivaParcelas(
			Map<String, BeanParcelaCapitalAsegurado> mapaDefinitivo) {
		List<BeanParcelaCapitalAsegurado> listaDefinitiva = new ArrayList<BeanParcelaCapitalAsegurado>();

		for (String key : mapaDefinitivo.keySet()) {
			listaDefinitiva.add(mapaDefinitivo.get(key));
		}

		return listaDefinitiva;
	}

	private Termino getUbicacion(int provincia, int comarca, int termino, String subtermino) {
		Termino term = null;
		try {
			String subterm = subtermino;
			if (StringUtils.nullToString(subtermino).equals(""))
				subterm = " ";
			term = terminoDao.getTermino(new BigDecimal(provincia), new BigDecimal(comarca), new BigDecimal(termino),
					subterm.charAt(0));
		} catch (DAOException e) {
		}
		return term;
	}

	private Variedad getVariedad(Long lineaseguroid, int cultivo, int variedad) {
		Variedad var = null;
		try {
			var = variedadDao.getVariedad(lineaseguroid, new BigDecimal(cultivo), new BigDecimal(variedad));
		} catch (DAOException e) {
		}
		return var;
	}

	private TipoCapital getTipoCapital(int tipo) {
		TipoCapital tc = null;
		try {
			tc = (TipoCapital) polizaDao.get(TipoCapital.class, new BigDecimal(tipo));
		} catch (DAOException e) {

		}
		return tc;
	}

	private Map<String, String> getModulos(final PolizaActualizadaResponse respuesta, final Long lineaseguroid) {
		Map<String, String> mapaModulos = new HashMap<String, String>();

		/* ANEXO AGRICOLA PRINCIPAL FORMATO NUEVO */
		if (respuesta.getPolizaPrincipalUnif() != null) {
			Modulo modPpal = this.modulosDao.getModulo(lineaseguroid,
					respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo().trim());
			mapaModulos.put(modPpal.getId().getCodmodulo(), modPpal.getDesmodulo());

			/* ANEXO AGRICOLA PRINCIPAL FORMATO ANTIGUO */
		} else {
			if (respuesta.getPolizaPrincipal() != null) {
				Modulo modPpal = this.modulosDao.getModulo(lineaseguroid,
						respuesta.getPolizaPrincipal().getPoliza().getCobertura().getModulo().trim());
				mapaModulos.put(modPpal.getId().getCodmodulo(), modPpal.getDesmodulo());
			}
		}

		/* ANEXO AGRICOLA COMPLEMENTARIA FORMATO NUEVO */
		if (respuesta.getPolizaComplementariaUnif() != null) {
			// Anado al mapa el modulo de la complementaria si tiene.
			Modulo modCpl = this.modulosDao.getModulo(lineaseguroid,
					respuesta.getPolizaComplementariaUnif().getPoliza().getCobertura().getModulo().trim());
			mapaModulos.put(modCpl.getId().getCodmodulo(), modCpl.getDesmodulo());

			/* ANEXO AGRICOLA COMPLEMENTARIA FORMATO ANTIGUO */
		} else {

			if (respuesta.getPolizaComplementaria() != null) {
				// do al mapa el modulo de la complementaria si tiene.
				Modulo modCpl = this.modulosDao.getModulo(lineaseguroid,
						respuesta.getPolizaComplementaria().getPoliza().getCobertura().getModulo().trim());
				mapaModulos.put(modCpl.getId().getCodmodulo(), modCpl.getDesmodulo());
			}
		}

		if (respuesta.getPolizaGanado() != null) {
			Modulo modGanado = this.modulosDao.getModulo(lineaseguroid,
					respuesta.getPolizaGanado().getPoliza().getCobertura().getModulo().trim());
			mapaModulos.put(modGanado.getId().getCodmodulo(), modGanado.getDesmodulo());
		}
		return mapaModulos;
	}

	/**
	 * Metodo para obtener la caracteristica de la explotacion
	 * 
	 * @param codCaracteristicaExplotacion
	 *            codigo de la caracteristica proporcionado por Agroseguro
	 * @return Datos de la caracteristica de la explotacion
	 */
	private CaracteristicaExplotacion getCaracteristicaExplotacion(int codCaracteristicaExplotacion) {

		CaracteristicaExplotacion caracteristicaExplotacion = null;

		try {
			caracteristicaExplotacion = this.caracteristicaExplotacionDao
					.getCaracteristicaExplotacion(codCaracteristicaExplotacion);
		} catch (DAOException e) {

		}

		return caracteristicaExplotacion;
	}

	/**
	 * Devuelve el set de ComparativaPoliza correspondiente a las coberturas
	 * recibidas en la situacion actualizada de la poliza
	 * 
	 * @param sitAct
	 * @param lineaseguroid
	 * @param codModulo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Set<ComparativaPoliza> getComparativasFromSituacionActualizada(final PolizaActualizadaResponse sitAct,
			final Long lineaseguroid, final String codModulo, final boolean esPolizaGanado) {
		// MPM - 14/03/2014
		Set<ComparativaPoliza> listaComparativas = new HashSet<ComparativaPoliza>();
		// Obtiene una lista ComparativaPoliza a partir de la situacion actualizada de
		// la poliza

		/**** GANADO ****/
		if (esPolizaGanado) {
			// es.agroseguro.seguroAgrario.contratacion.PolizaDocument
			// es.agroseguro.contratacion.impl.PolizaDocumentImpl
			es.agroseguro.contratacion.Poliza plzAct = sitAct.getPolizaGanado().getPoliza();
			if (plzAct.getCobertura() != null && plzAct.getCobertura().getDatosVariables() != null) {
				// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA (31) Y A
				// LA UBICACION DE COBERTURAS (18)
				Filter oiFilter = new Filter() {
					@Override
					public Criteria getCriteria(final Session sesion) {
						Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class);
						criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
						criteria.add(Restrictions.eq("id.coduso", OrganizadorInfoConstants.USO_POLIZA));
						criteria.add(Restrictions.in("id.codubicacion",
								new Object[] { OrganizadorInfoConstants.UBICACION_COBERTURA_DV }));
						return criteria;
					}
				};
				List<OrganizadorInformacion> oiList = (List<OrganizadorInformacion>) polizaDao.getObjects(oiFilter);
				es.agroseguro.contratacion.datosVariables.DatosVariables dvs = plzAct.getCobertura()
						.getDatosVariables();

				// RIESGO CUBIERTO ELEGIDO
				try {
					if (dvs.getRiesgCbtoElegArray() != null && dvs.getRiesgCbtoElegArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : dvs
								.getRiesgCbtoElegArray()) {
							ComparativaPoliza comp = generarComparativaPoliza(new BigDecimal(rce.getCPMod()),
									new BigDecimal(rce.getCodRCub()),
									ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO,
									comparativaSitActDao.getFilaModuloGanado(lineaseguroid, codModulo,
											new BigDecimal(rce.getCPMod()), new BigDecimal(rce.getCodRCub())),
									rce.getValor(),
									"S".equals(rce.getValor()) ? new BigDecimal(Constants.RIESGO_ELEGIDO_SI)
											: new BigDecimal(Constants.RIESGO_ELEGIDO_NO));

							listaComparativas.add(comp);
						}
					}
				} catch (Exception e) {
					logger.error("Error al obtener los riesgos cubiertos elegibles de la cobertura.", e);
				}

				try {

					for (OrganizadorInformacion oi : oiList) {
						Method method = dvs.getClass()
								.getMethod("get" + oi.getDiccionarioDatos().getEtiquetaxml() + "Array");
						Class<?> dvClass = dvs.getClass()
								.getMethod("addNew" + oi.getDiccionarioDatos().getEtiquetaxml()).getReturnType();
						Object[] result = (Object[]) method.invoke(dvs);
						for (Object obj : result) {
							BigDecimal valor = new BigDecimal("" + dvClass.getMethod(GET_VALOR).invoke(obj));
							BigDecimal cpMod = new BigDecimal("" + dvClass.getMethod("getCPMod").invoke(obj));
							BigDecimal rCub = new BigDecimal("" + dvClass.getMethod("getCodRCub").invoke(obj));
							String des = "";
							// GARANTIZADO
							if (oi.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO))) {
								des = comparativaSitActDao.getDesGarantizado(valor);
							} // CALCULO INDEMNIZACION
							else if (oi.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION))) {
								des = comparativaSitActDao.getDesCalcIndem(valor);
							} // % FRANQUICIA
							else if (oi.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA))) {
								des = comparativaSitActDao.getDesPctFranquicia(valor);
							} // MINIMO INDEMNIZABLE
							else if (oi.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE))) {
								des = comparativaSitActDao.getDesMinIndem(valor);
							} // TIPO FRANQUICIA
							else if (oi.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA))) {
								des = comparativaSitActDao.getDesTipoFranqIndem(valor.toString());
							} // % CAPITAL ASEGURADO
							else if (oi.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO))) {
								des = comparativaSitActDao.getDesCapitalAseg(valor);
							}

							listaComparativas.add(generarComparativaPoliza(cpMod, rCub,
									oi.getId().getCodconcepto().intValue(),
									comparativaSitActDao.getFilaModuloGanado(lineaseguroid, codModulo, cpMod, rCub),
									des, valor));
						}
					}
				} catch (Exception e) {
					logger.error("Error al obtener los datos variables de la cobertura.", e);
				}
			}
			/**** AGRICOLAS ****/
		} else {

			/* ANEXOS DE AGRICOLAS DE FORMATO UNIFICADO */
			if (sitAct.getPolizaPrincipalUnif() != null) {

				es.agroseguro.contratacion.Poliza plzAct = sitAct.getPolizaPrincipalUnif().getPoliza();

				if (plzAct.getCobertura() != null && plzAct.getCobertura().getDatosVariables() != null) {
					es.agroseguro.contratacion.datosVariables.DatosVariables dv = plzAct.getCobertura()
							.getDatosVariables();
					// GARANTIZADO
					if (dv.getGarantArray() != null && dv.getGarantArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.Garantizado g : dv.getGarantArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(g.getCPMod()),
									new BigDecimal(g.getCodRCub()), ConstantsConceptos.CODCPTO_GARANTIZADO,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_GARANTIZADO, new BigDecimal(g.getCPMod()),
											new BigDecimal(g.getCodRCub())),
									comparativaSitActDao.getDesGarantizado(new BigDecimal(g.getValor())),
									new BigDecimal(g.getValor())));
						}
					}
					// CALCULO INDEMNIZACION
					if (dv.getCalcIndemArray() != null && dv.getCalcIndemArray().length > 0) {
						for (CalculoIndemnizacion c : dv.getCalcIndemArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(c.getCPMod()),
									new BigDecimal(c.getCodRCub()), ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION,
											new BigDecimal(c.getCPMod()), new BigDecimal(c.getCodRCub())),
									comparativaSitActDao.getDesCalcIndem(new BigDecimal(c.getValor())),
									new BigDecimal(c.getValor())));
						}
					}
					// % FRANQUICIA
					if (dv.getFranqArray() != null && dv.getFranqArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia pf : dv.getFranqArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(pf.getCPMod()),
									new BigDecimal(pf.getCodRCub()), ConstantsConceptos.CODCPTO_PCT_FRANQUICIA,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_PCT_FRANQUICIA, new BigDecimal(pf.getCPMod()),
											new BigDecimal(pf.getCodRCub())),
									comparativaSitActDao.getDesPctFranquicia(new BigDecimal(pf.getValor())),
									new BigDecimal(pf.getValor())));
						}
					}
					// MINIMO INDEMNIZABLE
					if (dv.getMinIndemArray() != null && dv.getMinIndemArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable pmi : dv
								.getMinIndemArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(pmi.getCPMod()),
									new BigDecimal(pmi.getCodRCub()), ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE,
											new BigDecimal(pmi.getCPMod()), new BigDecimal(pmi.getCodRCub())),
									comparativaSitActDao.getDesMinIndem(new BigDecimal(pmi.getValor())),
									new BigDecimal(pmi.getValor())));
						}
					}
					// RIESGO CUBIERTO ELEGIDO
					if (dv.getRiesgCbtoElegArray() != null && dv.getRiesgCbtoElegArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : dv
								.getRiesgCbtoElegArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(rce.getCPMod()),
									new BigDecimal(rce.getCodRCub()),
									ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO,
											new BigDecimal(rce.getCPMod()), new BigDecimal(rce.getCodRCub())),
									rce.getValor(),
									"S".equals(rce.getValor()) ? new BigDecimal(Constants.RIESGO_ELEGIDO_SI)
											: new BigDecimal(Constants.RIESGO_ELEGIDO_NO)));
						}
					}
					// TIPO FRANQUICIA
					if (dv.getTipFranqArray() != null && dv.getTipFranqArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tf : dv.getTipFranqArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(tf.getCPMod()),
									new BigDecimal(tf.getCodRCub()), ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA, new BigDecimal(tf.getCPMod()),
											new BigDecimal(tf.getCodRCub())),
									comparativaSitActDao.getDesTipoFranqIndem(tf.getValor()),
									new BigDecimal(tf.getValor())));
						}
					}
					// % CAPITAL ASEGURADO
					if (dv.getCapAsegArray() != null && dv.getCapAsegArray().length > 0) {
						for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado pca : dv
								.getCapAsegArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(pca.getCPMod()),
									new BigDecimal(pca.getCodRCub()), ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO,
											new BigDecimal(pca.getCPMod()), new BigDecimal(pca.getCodRCub())),
									comparativaSitActDao.getDesCapitalAseg(new BigDecimal(pca.getValor())),
									new BigDecimal(pca.getValor())));
						}
					}
				}
			}
			/* ANEXOS DE AGRICOLAS DE FORMATO ANTIGUO */
			else {

				es.agroseguro.seguroAgrario.contratacion.Poliza plzAct = sitAct.getPolizaPrincipal().getPoliza();
				if (plzAct.getCobertura() != null && plzAct.getCobertura().getDatosVariables() != null) {
					es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables dv = plzAct.getCobertura()
							.getDatosVariables();
					// GARANTIZADO
					if (dv.getGarantArray() != null && dv.getGarantArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.Garantizado g : dv
								.getGarantArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(g.getCPMod()),
									new BigDecimal(g.getCodRCub()), ConstantsConceptos.CODCPTO_GARANTIZADO,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_GARANTIZADO, new BigDecimal(g.getCPMod()),
											new BigDecimal(g.getCodRCub())),
									comparativaSitActDao.getDesGarantizado(new BigDecimal(g.getValor())),
									new BigDecimal(g.getValor())));
						}
					}
					// CALCULO INDEMNIZACION
					if (dv.getCalcIndemArray() != null && dv.getCalcIndemArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.CalculoIndemnizacion c : dv
								.getCalcIndemArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(c.getCPMod()),
									new BigDecimal(c.getCodRCub()), ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION,
											new BigDecimal(c.getCPMod()), new BigDecimal(c.getCodRCub())),
									comparativaSitActDao.getDesCalcIndem(new BigDecimal(c.getValor())),
									new BigDecimal(c.getValor())));
						}
					}
					// % FRANQUICIA
					if (dv.getFranqArray() != null && dv.getFranqArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeFranquicia pf : dv
								.getFranqArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(pf.getCPMod()),
									new BigDecimal(pf.getCodRCub()), ConstantsConceptos.CODCPTO_PCT_FRANQUICIA,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_PCT_FRANQUICIA, new BigDecimal(pf.getCPMod()),
											new BigDecimal(pf.getCodRCub())),
									comparativaSitActDao.getDesPctFranquicia(new BigDecimal(pf.getValor())),
									new BigDecimal(pf.getValor())));
						}
					}
					// MINIMO INDEMNIZABLE
					if (dv.getMinIndemArray() != null && dv.getMinIndemArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeMinimoIndemnizable pmi : dv
								.getMinIndemArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(pmi.getCPMod()),
									new BigDecimal(pmi.getCodRCub()), ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE,
											new BigDecimal(pmi.getCPMod()), new BigDecimal(pmi.getCodRCub())),
									comparativaSitActDao.getDesMinIndem(new BigDecimal(pmi.getValor())),
									new BigDecimal(pmi.getValor())));
						}
					}
					// RIESGO CUBIERTO ELEGIDO
					if (dv.getRiesgCbtoElegArray() != null && dv.getRiesgCbtoElegArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.RiesgoCubiertoElegido rce : dv
								.getRiesgCbtoElegArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(rce.getCPMod()),
									new BigDecimal(rce.getCodRCub()),
									ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO,
											new BigDecimal(rce.getCPMod()), new BigDecimal(rce.getCodRCub())),
									rce.getValor(),
									"S".equals(rce.getValor()) ? new BigDecimal(Constants.RIESGO_ELEGIDO_SI)
											: new BigDecimal(Constants.RIESGO_ELEGIDO_NO)));
						}
					}
					// TIPO FRANQUICIA
					if (dv.getTipFranqArray() != null && dv.getTipFranqArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoFranquicia tf : dv
								.getTipFranqArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(tf.getCPMod()),
									new BigDecimal(tf.getCodRCub()), ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA, new BigDecimal(tf.getCPMod()),
											new BigDecimal(tf.getCodRCub())),
									comparativaSitActDao.getDesTipoFranqIndem(tf.getValor()),
									new BigDecimal(tf.getValor())));
						}
					}
					// % CAPITAL ASEGURADO
					if (dv.getCapAsegArray() != null && dv.getCapAsegArray().length > 0) {
						for (es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeCapitalAsegurado pca : dv
								.getCapAsegArray()) {
							listaComparativas.add(generarComparativaPoliza(new BigDecimal(pca.getCPMod()),
									new BigDecimal(pca.getCodRCub()), ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO,
									comparativaSitActDao.getFilaModulo(lineaseguroid, codModulo,
											ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO,
											new BigDecimal(pca.getCPMod()), new BigDecimal(pca.getCodRCub())),
									comparativaSitActDao.getDesCapitalAseg(new BigDecimal(pca.getValor())),
									new BigDecimal(pca.getValor())));
						}
					}
				}
					
			} 
			
		}
		return listaComparativas;

	}

	/*** Pet. 57626 ** MODIF TAM (23/04/2020) ** Inicio ***/

	public Base64Binary obtenerPDFSituacionActual(final String referencia, final BigDecimal plan, String tipoRef,
			final String realPath) throws BusinessException {
		try {

			Base64Binary res = webServicesManager.consultarSituacionActualPol(plan, referencia, tipoRef, realPath);

			return res;

		} catch (Exception e) {

			throw new BusinessException("Se ha producido un error obteniendo el PDF de la pÃ³liza", e);

		}

	}

	/*** Pet. 57626 ** MODIF TAM (23/04/2020) ** Fin ***/

	/**
	 * Crear un objeto ComparativaPoliza con los datos indicados por parametro
	 * 
	 * @param cpm
	 * @param rCub
	 * @param codConcepto
	 * @param filaModulo
	 * @param valor
	 * @return
	 */
	private ComparativaPoliza generarComparativaPoliza(final BigDecimal cpm, final BigDecimal rCub,
			final int codConcepto, final BigDecimal filaModulo, final String descvalor, final BigDecimal codValor) {
		ComparativaPoliza cp = new ComparativaPoliza();
		ComparativaPolizaId id = new ComparativaPolizaId();
		id.setFilamodulo(filaModulo);
		id.setCodconcepto(BigDecimal.valueOf(codConcepto));
		id.setCodconceptoppalmod(cpm);
		id.setCodriesgocubierto(rCub);
		id.setCodvalor(codValor);
		cp.setDescvalor(descvalor);
		cp.setId(id);
		return cp;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setInformesManager(InformesManager informesManager) {
		this.informesManager = informesManager;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setTerminoDao(ITerminoDao terminoDao) {
		this.terminoDao = terminoDao;
	}

	public void setVariedadDao(IVariedadDao variedadDao) {
		this.variedadDao = variedadDao;
	}

	public void setDatoVariableDao(IDatoVariableDao datoVariableDao) {
		this.datoVariableDao = datoVariableDao;
	}

	public void setModulosDao(IModulosDao modulosDao) {
		this.modulosDao = modulosDao;
	}

	public void setSubvencionDeclaradaDao(ISubvencionDeclaradaDao subvencionDeclaradaDao) {
		this.subvencionDeclaradaDao = subvencionDeclaradaDao;
	}

	public void setCaracteristicaExplotacionDao(ICaracteristicaExplotacionDao caracteristicaExplotacionDao) {
		this.caracteristicaExplotacionDao = caracteristicaExplotacionDao;
	}

	public void setDeclaracionModificacionPolizaDao(
			IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao) {
		this.declaracionModificacionPolizaDao = declaracionModificacionPolizaDao;
	}

	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setComparativaSitActDao(IComparativaSitActDao comparativaSitActDao) {
		this.comparativaSitActDao = comparativaSitActDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setAseguradoDao(IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}

	public void setRiesgoCubiertoModuloDao(IRiesgoCubiertoModuloDao riesgoCubiertoModuloDao) {
		this.riesgoCubiertoModuloDao = riesgoCubiertoModuloDao;
	}

	public void setConceptoPpalDao(IConceptoPpalDao conceptoPpalDao) {
		this.conceptoPpalDao = conceptoPpalDao;
	}

	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	/* Pet. 57626 ** MODIF TAM (27.04.2020) ** Inicio */
	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
	/* Pet. 57626 ** MODIF TAM (27.04.2020) ** Fin */

}