package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IImportesFraccDao;
import com.rsi.agp.core.managers.ICuadroCoberturasGanadoManager;
import com.rsi.agp.core.managers.ICuadroCoberturasManager;
import com.rsi.agp.core.managers.IDatosExplotacionesManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.ISeleccionComparativasSWManager;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
/* Pet. 57622 ** MODIF TAM (14.06.2019) INICIO */
import com.rsi.agp.core.report.layout.BeanTablaCobertExplotaciones;
import com.rsi.agp.core.report.layout.BeanTablaCobertParcelas;
import com.rsi.agp.core.report.layout.BeanTablaCoberturas;
import com.rsi.agp.core.util.CollectionsAndMapsUtil;
import com.rsi.agp.core.util.ComparativaPolizaComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.util.DatoVariableComparator;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.cpl.ConceptoCubiertoModuloFiltro;
import com.rsi.agp.dao.filters.poliza.TerminoFiltro1;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.IDatosCobertParcelasDao;
import com.rsi.agp.dao.models.poliza.IFechaContratacionDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.INotaInformativaDao;
import com.rsi.agp.dao.models.poliza.ISiniestroDao;
import com.rsi.agp.dao.models.poliza.ganado.IDatosExplotacionAnexoDao;
import com.rsi.agp.dao.models.poliza.ganado.IDatosExplotacionDao;
import com.rsi.agp.dao.models.poliza.ganado.IExplotacionAnexoDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.cgen.AdaptacionRiesgo;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.MascaraFechaContrataAgricola;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.SubvencionInforme;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.NotaInformativa;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.Informes.InformeAnexModExplotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.Informes.InformeDatosVariables;
import com.rsi.agp.dao.tables.siniestro.SiniestrosAnteriores;

import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.explotacion.Animales;
import es.agroseguro.contratacion.explotacion.CapitalAsegurado;
import es.agroseguro.contratacion.explotacion.GrupoRaza;
import es.agroseguro.modulosYCoberturas.DatoVariable;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturasDocument;
import es.agroseguro.modulosYCoberturas.Parcela;
import es.agroseguro.modulosYCoberturas.TipoCapitalAgricola;

public class InformesManager implements IManager {

	private PolizaManager polizaManager;
	private ICuadroCoberturasManager cuadroCoberturasManager;
	private DatabaseManager databaseManager;
	private ModuloManager moduloManager;
	private ISiniestroDao siniestroDao;
	private IFechaContratacionDao fechaContratacionDao;

	private INotaInformativaDao notaInformativaDao;

	private IAnexoModificacionDao anexoModificacionDao;
	private IExplotacionAnexoDao explotacionAnexoDao;
	private ICuadroCoberturasGanadoManager cuadroCoberturasGanadoManager;
	private IDiccionarioDatosDao diccionarioDatosDao;
	private IDatosExplotacionAnexoDao datosExplotacionAnexoDao;
	public final String FINANCIADA = "FINANCIADA";
	public final String CARGO_CUENTA = "CARGO EN CUENTA";
	public final String CONTADO = "CONTADO";
	private IImportesFraccDao importesFraccDao;
	private ISeleccionComparativasSWManager seleccionComparativasSWManager;

	/* Pet. 57622 ** MODIF TAM (14.06.2019) */
	private IDatosExplotacionDao datosExplotacionDao;
	@SuppressWarnings("unused")
	private IDatosExplotacionesManager datosExplotacionesManager;

	/* Pet. 63485-Fase II ** MODIF TAM (25.09.2020) ** Inicio */
	/* Se incluyen cambios en la impresion de coberturas de parcelas */
	private IDatosCobertParcelasDao datosCobertParcelasDao;

	/* Pet. 63485-Fase II ** MODIF TAM (25.09.2020) ** Fin */
	
	private static final String CABECERA = "cabecera";
	private static final String LISTA = "lista";
	private static final String LOG_VISTA_NULL = " La lista viene NULL";
	private static final String TAB_ELEGIBLE = "<font color=\"red\">ELEGIBLE</font><br/>";

	private static final Log logger = LogFactory.getLog(InformesManager.class);
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;

	@SuppressWarnings("rawtypes")
	public HashMap<String, List> getDatosCoberturasGarantias(Poliza poliza, String codModAnexo, Long idAnexo,
			Boolean usarCodModAnexo, Set<ComparativaPoliza> listaComparativasSitAct, Long idComparativa, String realpath) throws Exception {
		logger.debug("***INIT getDatosCoberturasGarantias ...");
		List<BeanTablaCoberturas> lista = new ArrayList<BeanTablaCoberturas>();
		List<String> listaCabeceras = new ArrayList<String>();
		List<ModuloView> vistaModulos = new ArrayList<ModuloView>();
		HashMap<String, List> datosCoberturas = new HashMap<String, List>();
		List<Modulo> modulos = moduloManager.dameModulosDisponibles(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(),
				poliza.getTipoReferencia(), usarCodModAnexo ? codModAnexo : poliza.getCodmodulo(), !usarCodModAnexo, true);
		if (modulos.size() > 0) {
			// Tenemos los modulos elegibles, procedemos a montar los datos de
			// coberturas para la vista...
			if (poliza.getLinea().isLineaGanado()) {
				for (int i = 0; i < modulos.size(); i++) {
					ModuloView vistaGanado = cuadroCoberturasGanadoManager.getCoberturasModulo(
							modulos.get(i).getId().getCodmodulo(), poliza.getLinea().getLineaseguroid(), true);
					if (null != vistaGanado)
						vistaModulos.add(vistaGanado);
				}
			} else {
				vistaModulos = cuadroCoberturasManager.generaCondicionesModulos(poliza.getIdpoliza(),
						poliza.getLinea().getLineaseguroid(), poliza.getLinea().getCodlinea() + "", poliza.getClase(),
						poliza.getAsegurado().getNifcif(), idAnexo, modulos, true);
			}
		}
		List<ModuloFilaView> filasModulo;
		Set<ComparativaPoliza> comparativasElegidas = ((listaComparativasSitAct == null) ? poliza.getComparativaPolizas() : listaComparativasSitAct);
		Iterator<ModuloView> iterModulos = vistaModulos.iterator();
		if (usarCodModAnexo) {
			while (iterModulos.hasNext()) {
				ModuloView modulo = (ModuloView) iterModulos.next();
				datosCoberturas.put(CABECERA, modulo.getListaCabeceras());
				filasModulo = modulo.getListaFilas();
				for (int i = 0; i < filasModulo.size(); i++) {
					ModuloFilaView fila = filasModulo.get(i);
					rellenaListaBeanTablaCoberturas(lista, comparativasElegidas, fila, null);
				}
			}
		} else {
			
			for (ModuloPoliza modPol : poliza.getModuloPolizas()) {
				if (idComparativa == null || modPol.getId().getNumComparativa().equals(idComparativa)) {
					ModulosYCoberturas myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(poliza.getIdpoliza(), modPol.getId().getCodmodulo());
					if (myc == null) {
						myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(modPol, poliza, realpath);
					}
					ModuloView modV = seleccionComparativasSWManager.getModuloViewFromModulosYCoberturas(myc, modPol, modPol.getId().getNumComparativa().intValue(), modPol.getId().getCodmodulo(), true);
					filasModulo = modV.getListaFilas();
					listaCabeceras = recogeCabecerasDelClob(myc);
					datosCoberturas.put(CABECERA, listaCabeceras);
					for (int i = 0; i < filasModulo.size(); i++) {
						ModuloFilaView fila = filasModulo.get(i);
						rellenaListaBeanTablaCoberturas(lista, comparativasElegidas, fila, modPol.getId().getNumComparativa().intValue());
					}
				}
			}
		}
		
		logger.debug("***FIN getDatosCoberturasGarantias ...");
		// Introducimos los registros encontrados en el tipo de objeto que vamos a usar
		// para montar
		datosCoberturas.put(LISTA, lista);
		datosCoberturas.put(CABECERA, listaCabeceras);
		return datosCoberturas;
	}

	/* Pet. 57622 ** MODIF TAM (03.06.2019) ** Inicio */
	@SuppressWarnings({ "rawtypes", "unused" })
	public HashMap<String, List> getDatosCobertExplotaciones(Poliza poliza, Integer numeroExpl, Long idExplotacion,
			Boolean usarCodModAnexo, Set<ComparativaPoliza> listaComparativasSitAct) throws Exception {

		List<BeanTablaCobertExplotaciones> listaExpl = new ArrayList<BeanTablaCobertExplotaciones>();

		HashMap<String, List> datosCobertExplotacion = new HashMap<String, List>();
		ModulosYCoberturas myc = null;

		logger.debug("Obteniendo el XML de ModulosYCoberturas de BBDD para la explotacion: " + numeroExpl
				+ " y poliza: " + poliza.getIdpoliza());

		List<ModuloFilaView> filasExplotacion = new ArrayList<ModuloFilaView>();

		logger.debug("Es poliza de ganado: " + poliza.getLinea().isLineaGanado());
		if (poliza.getLinea().isLineaGanado()) {

			// Obtiene el xml recibido del SW de ayudas a la contratacion en la ultima
			// llamada
			Clob respuesta = null;
			String xml = datosExplotacionDao.getCobExplotacion(poliza.getCodmodulo(), idExplotacion);

			logger.debug("xml:  " + xml);

			if (xml != null) {
				logger.debug(xml);
				// Convierte el xml recibido en un objeto ModulosYCoberturas
				myc = getMyCFromXml(xml);
			}

			logger.debug("myc:  " + myc);

			// si el objeto myc es nulo continuamos como ya se hacia, si no es nulo usamos
			// dicho objeto para montar las filas de la explotacion
			if (myc != null) {
				ModuloView modV = seleccionComparativasSWManager.getExplotacionesViewFromModulosYCoberturas(myc,
						numeroExpl);

				logger.debug("modV:  " + modV);

				filasExplotacion = modV.getListaFilas();
				logger.debug("filasExplotacion: " + filasExplotacion);

				// recogemos las cabeceras del clob
				List<String> listaCabeceras = recogeCabExplDelClob(myc);
				datosCobertExplotacion.put(CABECERA, listaCabeceras);
			} else {

				myc = seleccionComparativasSWManager.getModulosYCoberturasBBDD(poliza.getIdpoliza(),
						poliza.getCodmodulo());
				if (myc != null) {
					ModuloView modV = seleccionComparativasSWManager.getExplotacionesViewFromModulosYCoberturas(myc,
							numeroExpl);
					filasExplotacion = modV.getListaFilas();
					List<String> listaCabeceras = recogeCabExplDelClob(myc);
					datosCobertExplotacion.put(CABECERA, listaCabeceras);
				}
			}

			logger.debug("ANTES DEL FOR ...... PARA RELLENAR LA LISTA");
			logger.debug("filasExplotacion: " + filasExplotacion != null ? filasExplotacion.size() : " ES NULL");
			for (int i = 0; i < filasExplotacion.size(); i++) {
				ModuloFilaView fila = filasExplotacion.get(i);
				logger.debug(" fila " + i + " --- y su valor es " + fila);
				logger.debug("listaExpl -- valor es  " + listaExpl);
				logger.debug("poliza -- valor es " + poliza);
				logger.debug("numeroExpl -- valor es " + numeroExpl);

				rellenaListaBeanTablaExplotaciones(listaExpl, poliza, numeroExpl, fila);
			}
		}

		// Introducimos los registros encontrados en el tipo de objeto que vamos a usar
		// para montar*/
		datosCobertExplotacion.put(LISTA, listaExpl);

		logger.debug(" La lista final antes del return es " + listaExpl);
		logger.debug("listaExpl " + listaExpl != null ? listaExpl.size() : " OJO ES NULA ");

		return datosCobertExplotacion;
	}

	private void rellenaListaBeanTablaExplotaciones(List<BeanTablaCobertExplotaciones> lista, Poliza poliza,
			Integer numeroExpl, ModuloFilaView fila) {

		logger.debug("INIT---rellenaListaBeanTablaExplotaciones");

		BeanTablaCobertExplotaciones data = new BeanTablaCobertExplotaciones();

		String tempData;
		// Si la fila actual no es elegible procedemos normalmente
		if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") == -1 && fila.isRcElegible() == false) {
			logger.debug("fila actual NO ELEGIBLE ");
			tempData = "";

			// Columnas fijas
			data.setExplotacion(fila.getConceptoPrincipalModulo());
			data.setRiesgosCubiertos(fila.getRiesgoCubierto());

			for (ModuloCeldaView mcv : fila.getCeldas()) {

				if (!mcv.isElegible()) {

					if (mcv.getValores().size() > 0)
						tempData = mcv.getValores().get(0).getDescripcion() + " ";
					if (mcv.getObservaciones() != null) {
						tempData += mcv.getObservaciones();
					}

					data.getCeldas().add(tempData);
					tempData = "";
				} else {
					try {
						// Obtenemos el valor para la celda de la tabla de explotaciones
						tempData = datosExplotacionDao.obtenerExploDescvalorElegido(mcv.getCodconcepto(),
								fila.getFilamodulo(), poliza.getIdpoliza(), numeroExpl);

						data.getCeldas().add(tempData);
						tempData = "";

					} catch (DAOException e) {
						logger.error("Excepcion : InformesManager - rellenaListaBeanTablaExplotaciones", e);
					}

				}
			}
			logger.debug("  NO ELEGIBLE -- data: " + data);
			logger.debug(" NO ELEGIBLE -- lista: " + lista != null ? lista.size() : LOG_VISTA_NULL);
			lista.add(data);
		} else {
			logger.debug("fila actual SI ELEGIBLE ");
			// Si es elegible la fila, comprobamos si fue elegida. si lo fue, la pintamos y
			// si no, no
			tempData = "";
			boolean elegida = false;

			try {
				/*
				 * comprobamos que la fila esta elegida, en caso de no estar elegida no se
				 * imprime nada de esta fila
				 */
				elegida = datosExplotacionDao.obtenerExploElegida(fila.getCodConceptoPrincipalModulo(),
						fila.getFilamodulo(), poliza.getIdpoliza(), numeroExpl);

				if (elegida) {
					// Columnas fijas
					data.setExplotacion(fila.getConceptoPrincipalModulo());
					data.setRiesgosCubiertos(fila.getRiesgoCubierto());

					if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") != -1) {
						data.setRiesgosCubiertos(fila.getRiesgoCubierto()
								.substring(TAB_ELEGIBLE.length()));
					} else {
						for (ModuloCeldaView mcv : fila.getCeldas()) {
							if (mcv.isElegible()) {
								tempData = datosExplotacionDao.obtenerExploDescvalorElegido(mcv.getCodconcepto(),
										fila.getFilamodulo(), poliza.getIdpoliza(), numeroExpl);
							} else {
								if (mcv.getValores().size() > 0)

									tempData = mcv.getValores().get(0).getDescripcion() + " ";

								if (mcv.getObservaciones() != null) {
									tempData += mcv.getObservaciones();
								}

							}
							data.getCeldas().add(tempData);
							tempData = "";
						}
					}
					
					logger.debug(" SI ELEGIBLE -- data: " + data);
					logger.debug(" SI ELEGIBLE -- lista: " + lista != null ? lista.size() : LOG_VISTA_NULL);

					lista.add(data);
				}
			} catch (DAOException e) {
				logger.error("Excepcion : InformesManager - rellenaListaBeanTablaExplotaciones", e);
			}
		}
	}

	/*
	 * Recoge las cabeceras de la lista de coberturas guardadas en BBDD desde el
	 * clob de la llamada al WS.
	 */
	private List<String> recogeCabExplDelClob(ModulosYCoberturas myc) {
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<String> listaCabecerasExpl = new ArrayList<String>();

		es.agroseguro.modulosYCoberturas.Explotacion[] explotacionArray = myc.getExplotaciones().getExplotacionArray();
		for (es.agroseguro.modulosYCoberturas.Explotacion explotacion : explotacionArray) {
			for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : explotacion.getCoberturaArray()) {
				// buscamos max cabeceras de cada cobertura
				for (DatoVariable dv : cobertura.getDatoVariableArray()) {
					lstCabVariables.add(dv);
				}
			}
		}
		// ordenamos siempre las cabeceras por columna
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			if (!listaCabecerasExpl.contains(dvCab.getNombre())) {
				listaCabecerasExpl.add(dvCab.getNombre());
			}
		}
		return listaCabecerasExpl;
	}

	/**
	 * Convierte el xml recibido en un objeto ModulosYCoberturas
	 * 
	 * @param xml
	 * @return
	 */
	protected ModulosYCoberturas getMyCFromXml(String xml) {

		ModulosYCoberturas myc = null;

		// Convierte el xml recibido en un objeto ModulosYCoberturas
		try {
			myc = ModulosYCoberturasDocument.Factory.parse(xml).getModulosYCoberturas();
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al parsar el xml a un objeto ModulosYCoberturas (explotaciones)", e);
		}

		return myc;
	}

	/* Pet. 57622 ** MODIF TAM (03.06.2019) ** Fin */

	/*
	 * Recoge las cabeceras de la lista de coberturas guardadas en BBDD desde el
	 * clob de la llamada al WS.
	 */
	private List<String> recogeCabecerasDelClob(ModulosYCoberturas myc) {
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<String> listaCabeceras = new ArrayList<String>();
		es.agroseguro.modulosYCoberturas.Cobertura[] coberturaArray;
		if (myc == null || myc.getModuloArray() == null || myc.getModuloArray().length == 0) {
			coberturaArray = new es.agroseguro.modulosYCoberturas.Cobertura[] {};
		} else {
			coberturaArray = myc.getModuloArray(0).getCoberturaArray();
		}

		// buscamos max cabeceras de cada cobertura
		for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : coberturaArray) {
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {
				lstCabVariables.add(dv);
			}
		}
		// ordenamos siempre las cabeceras por columna
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			if (!listaCabeceras.contains(dvCab.getNombre())) {
				listaCabeceras.add(dvCab.getNombre());
			}
		}
		return listaCabeceras;
	}

	private void rellenaListaBeanTablaCoberturas(List<BeanTablaCoberturas> lista,
			Collection<ComparativaPoliza> comparativasElegidas, ModuloFilaView fila, Integer numComparativa) {
		BeanTablaCoberturas data = new BeanTablaCoberturas();
		String tempData;
		// Si la fila actual no es elegible procedemos normalmente
		if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") == -1 && fila.isRcElegible() == false) {
			tempData = "";
			// Columnas fijas
			data.setGarantia(fila.getConceptoPrincipalModulo());
			data.setRiesgosCubiertos(fila.getRiesgoCubierto());

			for (ModuloCeldaView mcv : fila.getCeldas()) {
				if (!mcv.isElegible()) {
					if (mcv.getValores().size() > 0)
						tempData = mcv.getValores().get(0).getDescripcion() + " ";
					if (mcv.getObservaciones() != null) {
						tempData += mcv.getObservaciones();
					}

					data.getCeldas().add(tempData);
					tempData = "";
				} else {
					for (ComparativaPoliza cp : comparativasElegidas) {
						if (cp.getId().getFilamodulo().equals(fila.getFilamodulo())
								&& cp.getId().getCodconcepto().equals(mcv.getCodconcepto()) 
								&& (numComparativa == null || numComparativa.equals(cp.getId().getIdComparativa().intValue()))) {
							tempData = cp.getDescvalor();
						}
					}
					data.getCeldas().add(tempData);
					tempData = "";
				}
			}
			lista.add(data);
		} else {
			// Si es elegible la fila, comprobamos si fue elegida. si lo fue, la pintamos y
			// si no, no
			tempData = "";
			boolean elegida = false;
			for (ComparativaPoliza cp : comparativasElegidas) {
				if (cp.getId().getCodconceptoppalmod().equals(fila.getCodConceptoPrincipalModulo())
						&& cp.getId().getCodriesgocubierto().equals(fila.getCodRiesgoCubierto())
						&& (numComparativa == null || numComparativa.equals(cp.getId().getIdComparativa().intValue()))) {
					if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO).equals(cp.getId().getCodconcepto())) {
						if (cp.getId().getCodvalor().compareTo(new BigDecimal(-1)) == 0) {
							elegida = true;
						}
					}
				}
			}
			if (elegida) {
				// Columnas fijas
				data.setGarantia(fila.getConceptoPrincipalModulo());

				if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") != -1)
					data.setRiesgosCubiertos(
							fila.getRiesgoCubierto().substring(TAB_ELEGIBLE.length()));
				else
					data.setRiesgosCubiertos(fila.getRiesgoCubierto());

				for (ModuloCeldaView mcv : fila.getCeldas()) {
					if (!mcv.isElegible()) {
						if (mcv.getValores().size() > 0)
							tempData = mcv.getValores().get(0).getDescripcion() + " ";
						if (mcv.getObservaciones() != null) {
							tempData += mcv.getObservaciones();

						}
						data.getCeldas().add(tempData);
						tempData = "";
					} else {
						for (ComparativaPoliza cp : comparativasElegidas) {
							if (cp.getId().getFilamodulo().equals(fila.getFilamodulo())
									&& cp.getId().getCodconcepto().equals(mcv.getCodconcepto())
									&& (numComparativa == null
											|| numComparativa.equals(cp.getId().getIdComparativa().intValue()))) {
								tempData = cp.getDescvalor();
							}
						}
						data.getCeldas().add(tempData);
						tempData = "";
					}
				}
				lista.add(data);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public HashMap<String, List> getDatosCoberturasGarantias(Poliza poliza, String strModulo) throws Exception {

		List<BeanTablaCoberturas> lista = new ArrayList<BeanTablaCoberturas>();
		HashMap<String, Object> detCoberturas = new HashMap<String, Object>();
		List<Modulo> modulos = moduloManager.dameModulosDisponibles(poliza.getIdpoliza(),
				poliza.getLinea().getLineaseguroid(), poliza.getTipoReferencia(), poliza.getCodmodulo(), true, true);
		List<ModuloView> vistaModulos = new ArrayList<ModuloView>();

		if (modulos.size() > 0) {

			if (!poliza.getLinea().isLineaGanado()) {
				// Tenemos los modulos elegibles, procedemos a montar los datos de
				// coberturas para la vista...
				vistaModulos = cuadroCoberturasManager.generaCondicionesModulos(poliza.getIdpoliza(),
						poliza.getLinea().getLineaseguroid(), poliza.getLinea().getCodlinea() + "", poliza.getClase(),
						poliza.getAsegurado().getNifcif(), null, modulos, true);
			} else {
				for (int i = 0; i < modulos.size(); i++) {
					ModuloView vistaGanado = cuadroCoberturasGanadoManager.getCoberturasModulo(
							modulos.get(i).getId().getCodmodulo(), poliza.getLinea().getLineaseguroid(), true);
					if (null != vistaGanado)
						vistaModulos.add(vistaGanado);
				}
			}
		}
		// Comprobamos si es Asegurado autorizado
		boolean isSubvencionable = polizaManager.isUsuarioAutorizado(poliza.getLinea().getLineaseguroid(),
				poliza.getAsegurado().getNifcif());
		detCoberturas.put("autorizado", isSubvencionable);

		String codModulo = new String();
		String idComparativa = new String();

		String[] seleccionados = strModulo.split("\\|");

		codModulo = seleccionados[1];
		idComparativa = seleccionados[0];

		// Se escogen solo las comparativas del modulo y fila correspondiente
		Iterator<ModuloView> iterModulos = vistaModulos.iterator();
		List<ComparativaPoliza> comparativasElegidas = new ArrayList<ComparativaPoliza>();
		Set<ComparativaPoliza> lstComp = poliza.getComparativaPolizas();
		for (ComparativaPoliza cp : lstComp) {
			if (cp.getId().getCodmodulo().equals(codModulo)
					&& cp.getId().getIdComparativa().toString().equals(idComparativa)) {
				comparativasElegidas.add(cp);
			}
		}

		Collections.sort(comparativasElegidas, new ComparativaPolizaComparator());

		HashMap<String, List> datosCoberturas = new HashMap<String, List>();
		while (iterModulos.hasNext()) {
			ModuloView modulo = (ModuloView) iterModulos.next();
			if (modulo.getCodModulo().equals(codModulo)) {
				datosCoberturas.put(CABECERA, modulo.getListaCabeceras());
				List<ModuloFilaView> filasModulo = modulo.getListaFilas();
				for (int i = 0; i < filasModulo.size(); i++) {
					ModuloFilaView fila = filasModulo.get(i);
					this.rellenaListaBeanTablaCoberturas(lista, comparativasElegidas, fila,
							Integer.parseInt(idComparativa));
				}
			}
		}
		// Introducimos los registros encontrados en el tipo de objeto que vamos a usar
		// para montar
		datosCoberturas.put(LISTA, lista);
		return datosCoberturas;
	}

	public String getDescripcionModulo(HttpServletRequest request, int cont) {
		String res = null;
		String modulo = getValorRequestInformeComparativa(request, "idModulo" + cont);

		String descModulo = getValorRequestInformeComparativa(request, "descModulo" + cont);
		if (null != modulo)
			res = modulo + " ";
		if (null != descModulo)
			res += descModulo;
		return res;
	}

	public List<String> getGruposNegocioComparativaPorIdModulo(BigDecimal idComparativa, Poliza poliza) {
		List<String> res = new ArrayList<String>();
		for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
			if (dc.getIdcomparativa().compareTo(idComparativa) == 0) {
				if (!res.contains(dc.getGrupoNegocio().toString()))
					res.add(dc.getGrupoNegocio().toString());

			}
		}
		return res;
	}

	@SuppressWarnings("rawtypes")
	private String getValorRequestInformeComparativa(final HttpServletRequest request, String prefijoParametro) {
		String res = null;

		Iterator it = request.getParameterMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Entry) it.next();
			String clave = (String) e.getKey();
			logger.debug("InformesManager - getValorRequestInformeComparativa - clave" + clave);
			if (clave.indexOf(prefijoParametro) != -1) {
				Object valor = e.getValue();
				String[] stringValues = (String[]) valor;
				res = stringValues[0];
				break;
			}
		}
		return res;
	}

	public List<SubvencionInforme> getSubvencionesCCAA(Map<String, String> subvCCAA) {
		List<SubvencionInforme> resultado = new ArrayList<SubvencionInforme>();
		for (Entry<String, String> subvencion : subvCCAA.entrySet()) {
			SubvencionInforme obj = new SubvencionInforme();
			obj.setOrganismo(subvencion.getKey());
			obj.setImporte(subvencion.getValue());
			resultado.add(obj);
		}
		return resultado;
	}

	public List<SubvencionInforme> getSBonificaciones2015(Map<String, String> subvCCAA) {
		List<SubvencionInforme> resultado = new ArrayList<SubvencionInforme>();
		for (Entry<String, String> subvencion : subvCCAA.entrySet()) {
			SubvencionInforme obj = new SubvencionInforme();
			obj.setOrganismo(subvencion.getKey());
			String cad = subvencion.getValue();
			BigDecimal n = NumberUtils.formatToNumber(cad, 2);
			obj.setImporte2(n);
			resultado.add(obj);
		}
		return resultado;
	}

	public List<BeanTablaCoberturas> getDatosCoberturasGarantiasComplementaria(Poliza poliza) throws Exception {

		List<BeanTablaCoberturas> lista = new ArrayList<BeanTablaCoberturas>();
		List<ModuloView> vistaModulos = new ArrayList<ModuloView>();
		List<Modulo> modulos = moduloManager.dameModulosDisponibles(poliza.getIdpoliza(),
				poliza.getLinea().getLineaseguroid(), poliza.getTipoReferencia(), poliza.getCodmodulo(), true, true);

		if (modulos.size() > 0) {
			// Tenemos los modulos elegibles, procedemos a montar los datos de
			// coberturas para la vista...
			vistaModulos = cuadroCoberturasManager.generaCondicionesModulos(poliza.getIdpoliza(),
					poliza.getLinea().getLineaseguroid(), poliza.getLinea().getCodlinea() + "", poliza.getClase(),
					poliza.getAsegurado().getNifcif(), null, modulos, true);
		}

		Iterator<ModuloView> iterModulos = vistaModulos.iterator();
		Set<ComparativaPoliza> comparativasElegidas = poliza.getComparativaPolizas();
		while (iterModulos.hasNext()) {
			ModuloView modulo = (ModuloView) iterModulos.next();
			if (modulo.getCodModulo().equals(poliza.getCodmodulo())) {

				List<ModuloFilaView> filasModulo = modulo.getListaFilas();
				for (int i = 0; i < filasModulo.size(); i++) {
					ModuloFilaView fila = filasModulo.get(i);
					// Si la fila actual no es elegible procedemos normalmente
					if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") == -1) {
						BeanTablaCoberturas data = new BeanTablaCoberturas();
						// Columnas fijas
						data.setGarantia(fila.getConceptoPrincipalModulo());
						data.setRiesgosCubiertos(fila.getRiesgoCubierto());

						lista.add(data);
					} else {
						// Si es elegible la fila, comprobamos si fue elegida. si lo fue, la pintamos y
						// si no, no
						boolean elegida = false;
						for (ComparativaPoliza cp : comparativasElegidas) {
							if (cp.getId().getFilamodulo().compareTo(new BigDecimal(i + 2)) == 0) {
								if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO).equals(cp.getId().getCodconcepto())) {
									if (cp.getId().getCodvalor().compareTo(new BigDecimal(-1)) == 0) {
										elegida = true;
									}
								}
							}
						}
						if (elegida) {
							BeanTablaCoberturas data = new BeanTablaCoberturas();
							// Columnas fijas
							data.setGarantia(fila.getConceptoPrincipalModulo());
							data.setRiesgosCubiertos(fila.getRiesgoCubierto()
									.substring(TAB_ELEGIBLE.length()));
							lista.add(data);
						}
					}
				}
			}
		}
		// Introducimos los registros encontrados en el tipo de objeto que vamos a usar
		// para montar
		return lista;
	}

	public Map<String, String> getRiesgosCoberturaParcela(Poliza poliza) throws BusinessException {
		Map<String, Object> resultado = new HashMap<String, Object>();
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, String> resultadoParseo = new HashMap<String, String>();

		// llamamos al PL que nos da las coberturas parcela
		String procedure = "o02agpe0.PQ_DATOS_VARIABLES_RIESGO.getDatVarCobParcelaReport(IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2";
		// Establecemos los par?metros para llamar al PL.
		param.put("IDPOLIZAPARAM", poliza.getIdpoliza());
		param.put("CODMODULOPARAM", poliza.getCodmodulo());

		try {
			resultado = databaseManager.executeStoreProc(procedure, param);
			String strDatVar = (String) resultado.get("RESULT");
			if (!StringUtils.nullToString(strDatVar).equals("")) {
				for (String cap : strDatVar.split("\\|")) {
					String[] cad_capital = cap.split(":");
					String[] cadena = cad_capital[1].split("#");

					String riesgo = cadena[0] + " " + (cadena[1].equals("-1") ? "Si" : "No");

					if (cad_capital.length == 2) {
						if (resultadoParseo.containsKey(cad_capital[0])) {
							String valor = resultadoParseo.get(cad_capital[0]);
							valor += "  " + riesgo;
							resultadoParseo.put(cad_capital[0], valor);
						} else {
							resultadoParseo.put(cad_capital[0], riesgo);
						}
					}
				}
			}

		} catch (BusinessException be) {
			throw new BusinessException("Erroral recuperar las riesgos coberturas de la poliza: ", be);
		}
		return resultadoParseo;
	}

	/**
	 * MEJORA 5 ANGEL 26/01/2012
	 * 
	 * @param comparativasElegidas
	 * @param anexo
	 * @return lstCob
	 */
	@SuppressWarnings("unchecked")
	public Poliza transformarCoberturasAComparativas(AnexoModificacion anexo, Poliza nPoliza, Poliza poliza) {
		Set<ComparativaPoliza> setComp = new HashSet<ComparativaPoliza>();

		HashMap<String, Object> comparativas = null;
		String[] modSelec = new String[1];
		modSelec[0] = new String(nPoliza.getCodmodulo());

		List<VistaComparativas> lstVistaCompTemporal = new ArrayList<VistaComparativas>();

		List<VistaComparativas> lstVistaCompDefinitiva = new ArrayList<VistaComparativas>();
		comparativas = this.cuadroCoberturasManager.crearComparativas(poliza.getLinea().getLineaseguroid(),
				poliza.getLinea().getCodlinea() + "", poliza.getClase(), poliza.getAsegurado().getNifcif(), modSelec);
		LinkedHashMap<String, Object> comparativasModulo = (LinkedHashMap<String, Object>) comparativas
				.get("comparativa");
		List<List<VistaComparativas>> combinacionesElementosComparativa = null;

		if (comparativasModulo.size() > 0) {
			for (String clave : comparativasModulo.keySet()) {
				combinacionesElementosComparativa = (List<List<VistaComparativas>>) comparativasModulo.get(clave);
				for (List<VistaComparativas> lstComb : combinacionesElementosComparativa) {
					for (VistaComparativas comb : lstComb) {
						logger.debug(comb.getId().getCodconceptoppalmod() + "," + comb.getId().getCodriesgocubierto()
								+ "," + comb.getId().getCodconcepto() + "," + comb.getId().getCodvalor() + ","
								+ comb.getId().getFilamodulo());
						logger.debug("anexo.getCoberturas() --> " + anexo.getCoberturas());						
						for (Cobertura cobAnexo : anexo.getCoberturas()) {
							logger.debug("cobAnexo --> " + cobAnexo);
							logger.debug("#########################################");
							logger.debug("comb.getId().getCodconceptoppalmod() --> " + comb.getId().getCodconceptoppalmod());
							logger.debug("cobAnexo.getCodconceptoppalmod() --> " + cobAnexo.getCodconceptoppalmod());
							logger.debug("comb.getId().getCodriesgocubierto() --> " + comb.getId().getCodriesgocubierto());
							logger.debug("cobAnexo.getCodriesgocubierto() --> " + cobAnexo.getCodriesgocubierto());
							logger.debug("comb.getId().getCodconcepto() --> " + comb.getId().getCodconcepto());
							logger.debug("comb.getId().getCodvalor() --> " + comb.getId().getCodvalor());
							logger.debug("cobAnexo.getCodvalor() --> " + cobAnexo.getCodvalor());
							logger.debug("cobAnexo.getTipomodificacion() --> " + cobAnexo.getTipomodificacion());
							logger.debug("cobAnexo.getCodconcepto() --> " + cobAnexo.getCodconcepto());
							if (comb.getId().getCodconceptoppalmod().equals(cobAnexo.getCodconceptoppalmod())
									&& comb.getId().getCodriesgocubierto().equals(cobAnexo.getCodriesgocubierto())
									&& comb.getId().getCodconcepto().equals(cobAnexo.getCodconcepto())
									&& StringUtils.nullToString(comb.getId().getCodvalor()).equals(StringUtils.nullToString(cobAnexo.getCodvalor()))
									&& !(new Character('B').equals(cobAnexo.getTipomodificacion())
											&& !new BigDecimal(363).equals(cobAnexo.getCodconcepto()))) {
								logger.debug("comb --> " + comb);
								if (!lstVistaCompTemporal.contains(comb))
									lstVistaCompTemporal.add(comb);
							}
							logger.debug("#########################################");
						}
						logger.debug("comb.getFilasVinculadas() --> " + comb.getFilasVinculadas());
						for (VistaComparativas combVinc : comb.getFilasVinculadas()) {
							logger.debug("vinculadas=" + combVinc.getId().getCodconceptoppalmod() + ","
									+ combVinc.getId().getCodriesgocubierto() + "," + combVinc.getId().getCodconcepto()
									+ "," + combVinc.getId().getCodvalor() + "," + combVinc.getId().getCodconcepto()
									+ "," + combVinc.getId().getFilamodulo());
							for (Cobertura cobAnexo : anexo.getCoberturas()) {
								logger.debug("NUEVO LOG: ");
								logger.debug("combVinc.getId().getCodconceptoppalmod()" + combVinc.getId().getCodconceptoppalmod());
								logger.debug("cobAnexo.getCodconceptoppalmod()" + cobAnexo.getCodconceptoppalmod());
								logger.debug("cobAnexo.getCodriesgocubierto()" + cobAnexo.getCodriesgocubierto());
								logger.debug("combVinc.getId().getCodriesgocubierto()" + combVinc.getId().getCodriesgocubierto());
								logger.debug("combVinc.getId().getCodconcepto()" + combVinc.getId().getCodconcepto());
								logger.debug("cobAnexo.getCodconcepto()" + cobAnexo.getCodconcepto());
								if (combVinc.getId().getCodconceptoppalmod()
										.compareTo(cobAnexo.getCodconceptoppalmod()) == 0
										&& combVinc.getId().getCodriesgocubierto()
												.compareTo(cobAnexo.getCodriesgocubierto()) == 0
										&& combVinc.getId().getCodconcepto().compareTo(cobAnexo.getCodconcepto()) == 0
										&& combVinc.getId().getCodvalor().toString().equals(cobAnexo.getCodvalor())
										&& !(Character.valueOf('B').equals(cobAnexo.getTipomodificacion())
												&& !(new BigDecimal(363).equals(cobAnexo.getCodconcepto())))) {
									if (!lstVistaCompTemporal.contains(combVinc))
										lstVistaCompTemporal.add(combVinc);
								}
								logger.debug("ACABA");
							}
						}
					}
				}

			}
		}
		boolean repetida = false;
		for (VistaComparativas comb : lstVistaCompTemporal) {
			repetida = false;
			for (VistaComparativas combTemp : lstVistaCompDefinitiva) {

				if (comb.getId().getCodconceptoppalmod().compareTo(combTemp.getId().getCodconceptoppalmod()) == 0
						&& comb.getId().getCodriesgocubierto().compareTo(combTemp.getId().getCodriesgocubierto()) == 0
						&& comb.getId().getCodconcepto().compareTo(combTemp.getId().getCodconcepto()) == 0
						&& comb.getId().getCodvalor().compareTo(combTemp.getId().getCodvalor()) == 0) {
					repetida = true;
				}
			}
			if (!repetida) {
				lstVistaCompDefinitiva.add(comb);
			}

		}
		for (VistaComparativas comb : lstVistaCompDefinitiva) {
			ComparativaPoliza comp = new ComparativaPoliza();
			ConceptoPpalModulo concPpal = new ConceptoPpalModulo();
			concPpal.setCodconceptoppalmod(comb.getId().getCodconceptoppalmod());
			comp.setConceptoPpalModulo(concPpal);
			RiesgoCubierto riesgoCub = new RiesgoCubierto();
			riesgoCub.getId().setCodriesgocubierto(comb.getId().getCodriesgocubierto());
			comp.setRiesgoCubierto(riesgoCub);
			ComparativaPolizaId compId = new ComparativaPolizaId();
			compId.setCodconcepto(comb.getId().getCodconcepto());
			if (comb.getId().getCodvalor() != null)
				compId.setCodvalor(comb.getId().getCodvalor());
			compId.setCodconceptoppalmod(comb.getId().getCodconceptoppalmod());
			compId.setCodriesgocubierto(comb.getId().getCodriesgocubierto());

			compId.setFilamodulo(comb.getId().getFilamodulo());
			compId.setIdComparativa(comb.getId().getIdComparativa());
			comp.setDescvalor(comb.getId().getDesvalor());
			comp.setId(compId);
			setComp.add(comp);
		}
		nPoliza.setComparativaPolizas(setComp);

		return nPoliza;
	}

	public Map<String, String> getPorcentajesDistSub(Poliza poliza) {
		Map<String, String> mapNotas = new HashMap<String, String>();
		BigDecimal totalEnesa = new BigDecimal(0);
		BigDecimal totalCCAA = new BigDecimal(0);
		BigDecimal ptcEnesa = new BigDecimal(0);
		BigDecimal ptcCCAA = new BigDecimal(0);
		BigDecimal totalSub = new BigDecimal(0);
		for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
			for (DistCosteSubvencion2015 dcs : dc.getDistCosteSubvencion2015s()) {
				if (dcs.getCodorganismo().equals(new Character('0'))) {// ENESA
					totalEnesa = totalEnesa.add(dcs.getImportesubv());
				} else {// CCAA
					totalCCAA = totalCCAA.add(dcs.getImportesubv());
				}
			}
			totalSub = totalEnesa.add(totalCCAA);
			if (totalSub.compareTo(new BigDecimal(0)) == 0) {
				ptcEnesa = new BigDecimal(0);
			} else {
				ptcEnesa = totalEnesa.divide(totalSub, 2, RoundingMode.HALF_UP);
				ptcEnesa = ptcEnesa.multiply(new BigDecimal(100));
			}
			logger.debug("pctEnesa: " + ptcEnesa.toString());
			mapNotas.put("ENESA2015", ptcEnesa.toString());

			ptcCCAA = (new BigDecimal(100).subtract(ptcEnesa));
			logger.debug("pctCCAA: " + ptcCCAA.toString());
			mapNotas.put("CCAA2015", ptcCCAA.toString());
			break;
		}
		return mapNotas;
	}

	/**
	 * Obtiene la nota informativa a mostrar dependiendo de la entidad a la que
	 * pertenezca la poliza
	 * 
	 * @param poliza
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getNotaInformativa(BigDecimal codEntidad, Long idPoliza, BigDecimal producto) {

		logger.debug("codEntidad para la notaInfo: " + codEntidad);

		Map<String, Object> mapNotaInfo = notaInformativaDao.getNotaInformativa(codEntidad, producto);

		String notaTitulo = "";
		String notaSubtitulo = "";
		String notaTexto = "";
		String notaVertical = "";
		String rutaFoto = "";
		String ubicacionFoto = "";

		StringBuffer sb = new StringBuffer(notaTexto);

		Map<String, String> mapNotas = new HashMap<String, String>();
		List<NotaInformativa> lstNotas = (List<NotaInformativa>) mapNotaInfo.get("lstNotas");
		logger.debug("total registros notaInfo: " + lstNotas.size());
		if (lstNotas != null && lstNotas.size() > 0) {
			mapNotas.put("HAS_NOTA_INFORMATIVA", "TRUE");
			for (NotaInformativa not : lstNotas) {
				switch (not.getId().getTipo().intValue()) {
				case Constants.TIPO_NOTA_INFO_TITULO: // titulo
					notaTitulo = formateaNotaInfo(not);
					break;
				case Constants.TIPO_NOTA_INFO_SUBTITULO: // subtitulo
					notaSubtitulo = formateaNotaInfo(not);
					break;
				case Constants.TIPO_NOTA_INFO_TEXTO: // texto
					sb.append(formateaNotaInfo(not));
					sb.append("<br><br>");
					break;
				case Constants.TIPO_NOTA_INFO_IMAGEN_SUP_IZQ:
				case Constants.TIPO_NOTA_INFO_IMAGEN_SUP_DER: // logo de la Entidad
				case Constants.TIPO_NOTA_INFO_IMAGEN_INF_IZQ:
				case Constants.TIPO_NOTA_INFO_IMAGEN_INF_DER:
					rutaFoto = not.getTexto();
					ubicacionFoto = not.getId().getTipo().toString();
					break;
				case Constants.TIPO_NOTA_INFO_TEXTO_LATERAL_IZQ: // linea vertical
					notaVertical = formateaNotaInfo(not);
					break;
				default:
					break;
				}
			}
		} else {
			mapNotas.put("HAS_NOTA_INFORMATIVA", "FALSE");
		}

		mapNotas.put("NOTA_TITULO", notaTitulo);
		logger.debug("NOTA_TITULO: " + notaTitulo);
		mapNotas.put("NOTA_SUBTITULO", notaSubtitulo);
		logger.debug("NOTA_SUBTITULO: " + notaSubtitulo);
		mapNotas.put("NOTA_RUTA_FOTO", rutaFoto);
		logger.debug("NOTA_RUTA_FOTO: " + rutaFoto);
		mapNotas.put("NOTA_UBICACION_FOTO", ubicacionFoto);
		logger.debug("NOTA_UBICACION_FOTO: " + ubicacionFoto);
		mapNotas.put("NOTA_VERTICAL", notaVertical);
		logger.debug("NOTA_VERTICAL: " + notaVertical);

		notaTexto = sb.toString();

		mapNotas.put("NOTA_TEXTO", notaTexto);
		logger.debug("NOTA_TEXTO: " + notaTexto);

		return mapNotas;
	}

	public String formateaNotaInfo(NotaInformativa not) {
		String notaFormateada = "";
		String inicio = "";
		String fin = "";
		if (not != null) {
			if (not.getNegrita() != null && not.getNegrita().compareTo(new BigDecimal(0)) == 1) {
				inicio = inicio + "<b>";
				fin = fin + "</b>";
			}
			if (not.getCursiva() != null && not.getCursiva().compareTo(new BigDecimal(0)) == 1) {
				inicio = inicio + "<i>";
				fin = fin + "</i>";
			}
			if (not.getSubrayado() != null && not.getSubrayado().compareTo(new BigDecimal(0)) == 1) {
				inicio = inicio + "<u>";
				fin = fin + "</u>";
			}
			if (not.getTexto() != null)
				notaFormateada = inicio + not.getTexto() + fin;
		}
		logger.debug("notaFormateada: " + notaFormateada);
		return notaFormateada;
	}

	private InformeAnexModExplotacion getRegistroInformeAnexoModExplotacion(Object[] explotacionAnexo,
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion xmlExplotacion, BigDecimal idAnexo,
			BigDecimal lineaSeguroId, BigDecimal idPoliza) throws XmlException, IOException {

		InformeAnexModExplotacion anexoModExplotacion = new InformeAnexModExplotacion();
		Long grupoRazaM = new Long(((BigDecimal) explotacionAnexo[14]).longValue());
		Long tipoCapitalM = new Long(((BigDecimal) explotacionAnexo[15]).longValue());
		Long tipoAnimalM = new Long(((BigDecimal) explotacionAnexo[16]).longValue());
		BigDecimal precioM = (BigDecimal) explotacionAnexo[19];
		Long numAnimalesM = new Long(((BigDecimal) explotacionAnexo[18]).longValue());

		llenaDatosOriginales(xmlExplotacion, anexoModExplotacion, idPoliza, lineaSeguroId, grupoRazaM, tipoCapitalM,
				tipoAnimalM, precioM, numAnimalesM);
		llenaDatosModificados(explotacionAnexo, anexoModExplotacion);
		boolean encuentraGr = false;
		boolean encuentraTc = false;
		boolean encuentraTa = false;
		GruposRazas grupo = null;

		com.rsi.agp.dao.tables.cgen.TipoCapital tipoCapital = null;

		TiposAnimalGanado tipoAnimal = null;
		Animales animalSel = null;
		// buscamos en el XML de la explotacion el "grupo de raza" que nos llega; En
		// principio no sabemos si es alta o modificacion
		for (GrupoRaza gr : xmlExplotacion.getGrupoRazaArray()) {
			if (new Long(gr.getGrupoRaza()).equals(grupoRazaM)) {
				grupo = this.getGrupoRaza(((Integer) gr.getGrupoRaza()).longValue(), lineaSeguroId.longValue());

				if (null != grupo) {
					encuentraGr = true;
				}
				for (CapitalAsegurado ca : gr.getCapitalAseguradoArray()) {
					if (new Long(ca.getTipo()).equals(tipoCapitalM)) {
						tipoCapital = this.getTipoCapital(new BigDecimal(tipoCapitalM));

						if (null != tipoCapital) {
							encuentraTc = true;
						}
						for (Animales animal : ca.getAnimalesArray()) {
							if (new Long(animal.getTipo()).equals(tipoAnimalM)) {
								tipoAnimal = (TiposAnimalGanado) this.getTipoAnimal(new Long(animal.getTipo()),
										new Long(lineaSeguroId.longValue()));
								if (null != tipoAnimal) {
									encuentraTa = true;

									animalSel = animal;
								}
							}
						}

					}
				}

			}

		}
		if (encuentraGr) {// se trata de una modificacion del Tc
			anexoModExplotacion
					.setGrupoRaza(grupo.getId().getCodGrupoRaza().toString() + " - " + grupo.getDescripcion());
			// anexoModExplotacion.setGrupoRazaM(null);
		} else { // han cambiado el Gr
			grupo = this.getGrupoRaza(grupoRazaM, lineaSeguroId.longValue());
			anexoModExplotacion
					.setGrupoRazaM(grupo.getId().getCodGrupoRaza().toString() + " - " + grupo.getDescripcion());
			anexoModExplotacion.setGrupoRaza("");
		}
		if (encuentraTc) {// se trata de una modificacion del Ta
			anexoModExplotacion.setTipoCapital(
					tipoCapital.getCodtipocapital().toString() + " - " + tipoCapital.getDestipocapital());
			// anexoModExplotacion.setTipoCapitalM(null);
		} else {// han cambiado el Tc (puede ser un alta de TC dentro del gr) al cambiar una de
				// "las claves" tiene que salir todo en azul
			grupo = this.getGrupoRaza(grupoRazaM, lineaSeguroId.longValue());
			anexoModExplotacion
					.setGrupoRazaM(grupo.getId().getCodGrupoRaza().toString() + " - " + grupo.getDescripcion());
			anexoModExplotacion.setGrupoRaza("");
			tipoCapital = this.getTipoCapital(new BigDecimal(tipoCapitalM));
			anexoModExplotacion.setTipoCapitalM(
					tipoCapital.getCodtipocapital().toString() + " - " + tipoCapital.getDestipocapital());
			anexoModExplotacion.setTipoCapital("");
			tipoAnimal = (TiposAnimalGanado) this.getTipoAnimal(tipoAnimalM, new Long(lineaSeguroId.longValue()));
			anexoModExplotacion.setTipoAnimalM(
					tipoAnimal.getId().getCodTipoAnimal().toString() + " - " + tipoAnimal.getDescripcion());
			anexoModExplotacion.setTipoAnimal("");

		}
		if (encuentraTa) {// se trata de una modificacion del numero o precio del Ta
			anexoModExplotacion.setTipoAnimal(
					tipoAnimal.getId().getCodTipoAnimal().toString() + " - " + tipoAnimal.getDescripcion());
		} else {// han cambiado el Ta; al cambiar una de "las claves" tiene que salir todo en
				// azul
			grupo = this.getGrupoRaza(grupoRazaM, lineaSeguroId.longValue());
			anexoModExplotacion
					.setGrupoRazaM(grupo.getId().getCodGrupoRaza().toString() + " - " + grupo.getDescripcion());
			anexoModExplotacion.setGrupoRaza("");
			tipoCapital = this.getTipoCapital(new BigDecimal(tipoCapitalM));
			anexoModExplotacion.setTipoCapitalM(
					tipoCapital.getCodtipocapital().toString() + " - " + tipoCapital.getDestipocapital());
			anexoModExplotacion.setTipoCapital("");
			tipoAnimal = (TiposAnimalGanado) this.getTipoAnimal(tipoAnimalM, new Long(lineaSeguroId.longValue()));
			anexoModExplotacion.setTipoAnimalM(
					tipoAnimal.getId().getCodTipoAnimal().toString() + " - " + tipoAnimal.getDescripcion());
			anexoModExplotacion.setTipoAnimal("");
			tipoAnimal = (TiposAnimalGanado) this.getTipoAnimal(tipoAnimalM, new Long(lineaSeguroId.longValue()));
			anexoModExplotacion.setTipoAnimalM(
					tipoAnimal.getId().getCodTipoAnimal().toString() + " - " + tipoAnimal.getDescripcion());
			anexoModExplotacion.setTipoAnimal("");

		}

		if (null != animalSel) {
			if (numAnimalesM.intValue() == (animalSel.getNumero())) {
				anexoModExplotacion.setNumanimales(new BigDecimal(animalSel.getNumero()));
			} else {
				anexoModExplotacion.setNumanimales(new BigDecimal(animalSel.getNumero()));
				anexoModExplotacion.setNumAnimalesM(new BigDecimal(numAnimalesM));
			}

			if (null != precioM) {
				if ((animalSel.getPrecio().setScale(2, BigDecimal.ROUND_DOWN))
						.equals(precioM.setScale(2, BigDecimal.ROUND_DOWN))) {
					anexoModExplotacion.setPrecio(animalSel.getPrecio().setScale(2, BigDecimal.ROUND_DOWN));

				} else {
					anexoModExplotacion.setPrecio(animalSel.getPrecio().setScale(2, BigDecimal.ROUND_DOWN));
					anexoModExplotacion.setPrecioM(precioM.setScale(2, BigDecimal.ROUND_DOWN));
				}
			}
		} else {// si se ha cambiado el tipo de animal no hay manera de encontrar su precio y
				// numero originales
			anexoModExplotacion.setNumAnimalesM(new BigDecimal(numAnimalesM));
			if (null != precioM)
				anexoModExplotacion.setPrecioM(precioM.setScale(2, BigDecimal.ROUND_DOWN));
		}

		return anexoModExplotacion;
	}

	private void llenaDatosOriginales(
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion xmlExplotacion,
			InformeAnexModExplotacion anexoModExplotacion, BigDecimal idPoliza, BigDecimal lineaSeguroId,
			Long grupoRazaM, Long tipoCapitalM, Long tipoAnimalM, BigDecimal precioM, Long numAnimalesM) {

		// Datos de ubicacion
		Termino termino = this.getTermino(xmlExplotacion.getUbicacion());
		if (null != termino) {
			anexoModExplotacion.setProvincia(termino.getProvincia().getCodprovincia().toString() + " - "
					+ termino.getProvincia().getNomprovincia());

			anexoModExplotacion.setComarca(termino.getComarca().getId().getCodcomarca().toString() + " - "
					+ termino.getComarca().getNomcomarca());
			
			// Se recupera una instancia espec�fica de la entidad "Linea" a trav�s del DAO a partir del lineaseguroid
			com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(lineaSeguroId.toString());
			
			// Obtenemos la fecha de fin de contrataci�n.
			Date fechaInicioContratacion = linea.getFechaInicioContratacion();
			
			// Utiliza el m�todo getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
			// Esta versi�n ahora tiene en cuenta la fecha de inicio de contrataci�n y si la l�nea es de ganado para determinar el nombre correcto del termino
			anexoModExplotacion
					.setTermino(termino.getId().getCodtermino().toString() + " - " + termino.getNomTerminoByFecha(fechaInicioContratacion, true));
		}
		// -----------------------------------------------------------------------------------

		anexoModExplotacion.setCosteTomador(new BigDecimal(0)); // de momento no lo alimentamos

		Especie especie = this.getEspecie(new Long(((Integer) xmlExplotacion.getEspecie()).longValue()),
				lineaSeguroId.longValue());
		if (null != especie)
			anexoModExplotacion.setEspecie(especie.getId().getCodespecie() + " - " + especie.getDescripcion());

		RegimenManejo regimen = this.getRegimen(((Integer) xmlExplotacion.getRegimen()).longValue(),
				lineaSeguroId.longValue());
		if (null != regimen)
			anexoModExplotacion
					.setRegimen(regimen.getId().getCodRegimen().toString() + " - " + regimen.getDescripcion());

		// anexoModExplotacion.setId(id); //Campo que no diponemos en el estado original
		// anexoModExplotacion.setIdGrupoRaza(idgruporaza);// campo que no disponemos en
		// el estado original
		anexoModExplotacion.setIdpoliza(idPoliza);
		if (null != xmlExplotacion.getCoordenadas()) {
			anexoModExplotacion.setLatitud(new BigDecimal(xmlExplotacion.getCoordenadas().getLatitud()));
			anexoModExplotacion.setLongitud(new BigDecimal(xmlExplotacion.getCoordenadas().getLongitud()));
		}

		anexoModExplotacion.setNumero(new BigDecimal(xmlExplotacion.getNumero()));
		anexoModExplotacion.setSubexplotacion(((Integer) xmlExplotacion.getSubexplotacion()).toString());
		anexoModExplotacion.setTasaComercial(new BigDecimal(0));// de momento es un dato que no tenemos

		if (null != xmlExplotacion.getSigla())
			anexoModExplotacion.setSigla(xmlExplotacion.getSigla());

		if (null != xmlExplotacion.getRega())
			anexoModExplotacion.setRega(xmlExplotacion.getRega());

		anexoModExplotacion.setTipoModificacion("M");

		// De momento solo hay un grupo de raza

		// for (GrupoRaza grupoRaza : xmlExplotacion.getGrupoRazaArray()){
		// GruposRazas grupo =this.getGrupoRaza(((Integer)
		// grupoRaza.getGrupoRaza()).longValue(), lineaSeguroId.longValue());
		// if(null!=grupo && grupo.getId().getCodGrupoRaza().equals(grupoRazaM)) {
		// anexoModExplotacion.setGrupoRaza(grupo.getId().getCodGrupoRaza().toString() +
		// " - " + grupo.getDescripcion());
		// //De momento solo hay un capital asegurado
		// for (CapitalAsegurado capitalAsegurado :
		// grupoRaza.getCapitalAseguradoArray()){
		// com.rsi.agp.dao.tables.cgen.TipoCapital tipoCapital=this.getTipoCapital( new
		// BigDecimal(capitalAsegurado.getTipo()));
		// if (null!= tipoCapital && new
		// Long(tipoCapital.getCodtipocapital().longValue()).equals(tipoCapitalM)){
		// anexoModExplotacion.setTipoCapital(tipoCapital.getCodtipocapital().toString()
		// + " - " + tipoCapital.getDestipocapital());
		// // y un grupo de animales
		// for (Animales animales : capitalAsegurado.getAnimalesArray()){
		// //cod y Linea
		// TiposAnimalGanado tipoAnimal =(TiposAnimalGanado) this.getTipoAnimal(new
		// Long(animales.getTipo()),new Long(lineaSeguroId.longValue()));
		// if(null!= tipoAnimal &&
		// tipoAnimal.getId().getCodTipoAnimal().equals(tipoAnimalM)){
		// anexoModExplotacion.setTipoAnimal(tipoAnimal.getId().getCodTipoAnimal().toString()
		// + " - " + tipoAnimal.getDescripcion() );
		//
		// anexoModExplotacion.setNumanimales(new BigDecimal(animales.getNumero()));
		// if (null!= animales.getPrecio()) {
		// anexoModExplotacion.setPrecio(animales.getPrecio().setScale(2,BigDecimal.ROUND_DOWN));
		// }
		//
		// }
		//
		// }
		// }
		//
		// //capitalAsegurado.getDatosVariables()){
		//
		// }
		// }
		// }
		// De momento los datos variables vienen en el nivel explotacion
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
				.getCodConceptoEtiquetaTablaExplotaciones(lineaSeguroId.longValue());
		List<InformeDatosVariables> listaDv = this.getDatosVariablesOrg(xmlExplotacion, auxEtiquetaTabla, grupoRazaM,
				lineaSeguroId, tipoCapitalM, tipoAnimalM);
		// Solo nos devuelve el valor de lo que necesitamos y el nombre del concepto
		// pero no tenemos la descripcion
		for (InformeDatosVariables inf : listaDv) {
			String descripcion = this.getDescripcionDatosVariables(inf.getCodConcepto(), inf.getValor());
			if (null != descripcion) {
				inf.setDescripcion(descripcion);
			}
		}
		anexoModExplotacion.setDatosVariables(listaDv);

	}

	private void llenaDatosModificados(Object[] explotacionAnexo, InformeAnexModExplotacion anexoModExplotacion) {
		/*
		 * 0 id, 1 idpoliza, 2 TIPO_MODIFICACION, 3 NUMERO, 4 Provincia, 5 Comarca, 6
		 * Termino, 7 rega, 8 sigla, 9 subexplotacion, 10 latitud, 11 longitud, 12
		 * especie, 13 regimen, 14 GrupoRaza 15 TipoCapital, 16 TipoAnimal, 17
		 * grupoListado, 18 numanimales, 19 precio, 20 TasaComercial, 21 CosteTomador,
		 * 22 idgruporaza, 23 ID_ANEXO, 24 lineaseguroid,
		 */
		Long lineaSeguroId = null;
		if (null != explotacionAnexo[24])
			lineaSeguroId = ((BigDecimal) explotacionAnexo[24]).longValue();
		String provinciaM = null;
		String comarcaM = null;
		String terminoM = null;

		// Datos de ubicacion
		if (null != explotacionAnexo[4])
			provinciaM = StringUtils.nullToString((BigDecimal) explotacionAnexo[4]);

		if (null != explotacionAnexo[5])
			comarcaM = StringUtils.nullToString((BigDecimal) explotacionAnexo[5]);

		if (null != explotacionAnexo[6])
			terminoM = StringUtils.nullToString((BigDecimal) explotacionAnexo[6]);

		if (null != provinciaM || null != comarcaM || null != terminoM) {
			// Tenemos que buscar las descripciones
			Termino termino = this.getTermino(new BigDecimal(provinciaM), new BigDecimal(comarcaM),
					new BigDecimal(terminoM), null);
			if (null != termino) {
				
				// Se recupera una instancia espec�fica de la entidad "Linea" a trav�s del DAO a partir del lineaseguroid
				com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(lineaSeguroId.toString());
				
				// Obtenemos la fecha de fin de contrataci�n.
				Date fechaInicioContratacion = linea.getFechaInicioContratacion();
				
				// Utiliza el m�todo getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
				// Esta versi�n ahora tiene en cuenta la fecha de inicio de contrataci�n y si la l�nea es de ganado para determinar el nombre correcto del termino
				terminoM += " - " + termino.getNomTerminoByFecha(fechaInicioContratacion, true);
				
				provinciaM += " - " + termino.getProvincia().getNomprovincia();
				comarcaM += " - " + termino.getComarca().getNomcomarca();
				if (null != anexoModExplotacion.getProvincia()
						&& anexoModExplotacion.getProvincia().compareTo(provinciaM) != 0)
					anexoModExplotacion.setProvinciaM(provinciaM);
				if (null != anexoModExplotacion.getComarca()
						&& anexoModExplotacion.getComarca().compareTo(comarcaM) != 0)
					anexoModExplotacion.setComarcaM(comarcaM);
				if (null != anexoModExplotacion.getTermino()
						&& anexoModExplotacion.getTermino().compareTo(terminoM) != 0)
					anexoModExplotacion.setTerminoM(terminoM);
			}
		}

		BigDecimal latitudM = (BigDecimal) explotacionAnexo[10];
		if (null != latitudM && anexoModExplotacion.getLatitud() == null || (null != anexoModExplotacion.getLatitud()
				&& anexoModExplotacion.getLatitud().compareTo(latitudM) != 0)) {
			anexoModExplotacion.setLatitudM(latitudM);
		}

		BigDecimal longitudM = (BigDecimal) explotacionAnexo[11];
		if (null != longitudM && anexoModExplotacion.getLongitud() == null || (null != anexoModExplotacion.getLongitud()
				&& anexoModExplotacion.getLongitud().compareTo(longitudM) != 0)) {
			anexoModExplotacion.setLongitudM(longitudM);
		}

		// -----------------------------------------------------------------------------------------------------------------------

		if (null != explotacionAnexo[12]) {
			if (null != anexoModExplotacion.getEspecie()) {
				String especieM = StringUtils.nullToString((BigDecimal) explotacionAnexo[12]);
				Especie especie = this.getEspecie((new Integer(especieM)).longValue(), lineaSeguroId);
				if (null != especie)
					especieM += " - " + especie.getDescripcion();
				if (anexoModExplotacion.getEspecie().compareTo(especieM) != 0)
					anexoModExplotacion.setEspecieM(especieM);
			}
		}
		
		BigDecimal numeroM = (BigDecimal) explotacionAnexo[3];
		if (null != anexoModExplotacion.getNumero() && null != numeroM
				&& anexoModExplotacion.getNumero().compareTo(numeroM) != 0)
			anexoModExplotacion.setNumeroM(numeroM);

		String regaM = (String) explotacionAnexo[7];
		if (null != anexoModExplotacion.getRega() && null != regaM
				&& anexoModExplotacion.getRega().compareTo(regaM) != 0)
			anexoModExplotacion.setRegaM(regaM);

		if (null != explotacionAnexo[13]) {
			if (null != anexoModExplotacion.getRegimen()) {
				String regimenM = StringUtils.nullToString((BigDecimal) explotacionAnexo[13]);
				RegimenManejo reg = this.getRegimen((new Integer(regimenM)).longValue(), lineaSeguroId);
				if (null != reg)
					regimenM += " - " + reg.getDescripcion();
				if (anexoModExplotacion.getRegimen().compareTo(regimenM) != 0)
					anexoModExplotacion.setRegimenM(regimenM);
			}
		}

		String siglaM = (String) explotacionAnexo[8];
		if (null != anexoModExplotacion.getSigla() && anexoModExplotacion.getSigla().compareTo(siglaM) != 0)
			anexoModExplotacion.setSiglaM(siglaM);
		String subExplotacionM = StringUtils.nullToString((BigDecimal) explotacionAnexo[9]);
		if (!subExplotacionM.trim().equals("")) {
			if (null != anexoModExplotacion.getSubexplotacion()
					&& anexoModExplotacion.getSubexplotacion().compareTo(subExplotacionM) != 0)
				anexoModExplotacion.setSubexplotacionM(subExplotacionM.toString());
		}

		String grupoListadoM = (String) explotacionAnexo[17];
		if (anexoModExplotacion.getGrupoListado() != null
				&& anexoModExplotacion.getGrupoListado().compareTo(grupoListadoM) != 0)
			anexoModExplotacion.setGrupoListadoM(grupoListadoM);

		anexoModExplotacion.setTipoModificacionM("M");

		// Datos Variables
		BigDecimal idExplotacionAnexo = (BigDecimal) explotacionAnexo[0];
		BigDecimal idGrupoRaza = (BigDecimal) explotacionAnexo[22];
		if (null != idExplotacionAnexo && null != idGrupoRaza)
			this.getDatosVariablesModificados(idExplotacionAnexo, anexoModExplotacion, idGrupoRaza);

	}

	@SuppressWarnings("unchecked")
	public List<InformeAnexModExplotacion> getModificacionAnexosExplotacion(Long idPoliza, Long idAnexo) {
		// List <BeanExplotacion> explotaciones = new ArrayList<BeanExplotacion>();
		Clob xmlEnvio = null;

		List<InformeAnexModExplotacion> anexosModExplotacion = new ArrayList<InformeAnexModExplotacion>();
		try {

			List<Object> listaExpl = explotacionAnexoDao.getExplotaciones(new Long(idPoliza), "M", idAnexo);

			BigDecimal lineaSeguroId = null;
			InformeAnexModExplotacion anexoMod = null;
			xmlEnvio = anexoModificacionDao.getXMLSituacionActualizada((long) idAnexo);
			es.agroseguro.contratacion.PolizaDocument poliza = es.agroseguro.contratacion.PolizaDocument.Factory
					.newInstance();
			String cad = WSUtils.convertClob2String(xmlEnvio);
			poliza = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(cad));

			for (int i = 0; i < listaExpl.size(); i++) {
				anexoMod = null;
				Object[] expl = (Object[]) listaExpl.get(i);
				lineaSeguroId = (BigDecimal) expl[24];
				es.agroseguro.contratacion.explotacion.ExplotacionDocument xmlExplotacion = null;
				Node currNode = poliza.getPoliza().getObjetosAsegurados().getDomNode().getFirstChild();
				inner: while (null != currNode) {
					if (currNode.getNodeType() == Node.ELEMENT_NODE) {
						xmlExplotacion = es.agroseguro.contratacion.explotacion.ExplotacionDocument.Factory
								.parse(currNode);
						if (new BigDecimal(xmlExplotacion.getExplotacion().getNumero())
								.compareTo((BigDecimal) expl[3]) == 0) {
							anexoMod = getRegistroInformeAnexoModExplotacion(expl, xmlExplotacion.getExplotacion(),
									new BigDecimal(idAnexo), lineaSeguroId, new BigDecimal(idPoliza));
							break inner;
						}
					}
					currNode = currNode.getNextSibling();
				}
				if (null != anexoMod) {
					anexosModExplotacion.add(anexoMod);
				}
			}
			// buscamos las bajas; vamos a comparar el xml enviado con los grupos-raza
			// actuales.
			Node currNode = poliza.getPoliza().getObjetosAsegurados().getDomNode().getFirstChild();
			while (null != currNode) {
				if (currNode.getNodeType() == Node.ELEMENT_NODE) {
					es.agroseguro.contratacion.explotacion.ExplotacionDocument xmlExplotacion = es.agroseguro.contratacion.explotacion.ExplotacionDocument.Factory
							.parse(currNode);
					for (GrupoRaza gr : xmlExplotacion.getExplotacion().getGrupoRazaArray()) {
						for (CapitalAsegurado ca : gr.getCapitalAseguradoArray()) {
							for (Animales animal : ca.getAnimalesArray()) {
								Long grupoRaza = new Long(gr.getGrupoRaza());
								Long tipoCapital = new Long(ca.getTipo());
								Long tipoAnimal = new Long(animal.getTipo());
								logger.debug("busca: explotacion " + xmlExplotacion.getExplotacion().getNumero()
										+ " grupoRaza " + grupoRaza + " tipoCapital " + tipoCapital + " tipoAnimal "
										+ tipoAnimal);
								boolean encontrado = false;
								boolean explotacionCorrecta = false;
								for (int i = 0; i < listaExpl.size(); i++) {
									explotacionCorrecta = false;
									Object[] expl = (Object[]) listaExpl.get(i);
									lineaSeguroId = (BigDecimal) expl[24];
									Long grupoRazaM = new Long(((BigDecimal) expl[14]).longValue());
									Long tipoCapitalM = new Long(((BigDecimal) expl[15]).longValue());
									Long tipoAnimalM = new Long(((BigDecimal) expl[16]).longValue());
									logger.debug("encuentra: explotacion :" + expl[3] + "grupoRazaM " + grupoRazaM
											+ " tipoCapitalM " + tipoCapitalM + " tipoAnimalM " + tipoAnimalM);
									anexoMod = null;

									if (new BigDecimal(xmlExplotacion.getExplotacion().getNumero())
											.compareTo((BigDecimal) expl[3]) == 0) {
										explotacionCorrecta = true;
										if ((grupoRaza.equals(grupoRazaM) && tipoCapital.equals(tipoCapitalM)
												&& tipoAnimal.equals(tipoAnimalM))) {
											encontrado = true;
											break;
										}
									}
								}
								if (explotacionCorrecta && (!encontrado)) {
									logger.debug("no encontrado : explotacion "
											+ xmlExplotacion.getExplotacion().getNumero() + " +grupoRaza " + grupoRaza
											+ " tipoCapital " + tipoCapital + " tipoAnimal " + tipoAnimal);
									anexoMod = getRegistroInformeAnexoBajaExplotacion(grupoRaza, tipoCapital, animal,
											lineaSeguroId, new BigDecimal(idPoliza), xmlExplotacion.getExplotacion());
									if (null != anexoMod) {
										anexosModExplotacion.add(anexoMod);
									}
								}
							}
						}
					}
				}
				currNode = currNode.getNextSibling();
			}

			return anexosModExplotacion;
		} catch (IOException e) {
			logger.error("Excepcion  : InformesManager - getModificacionAnexosExplotacion", e);
		} catch (XmlException e) {
			logger.error("Excepcion : InformesManager  - getModificacionAnexosExplotacion", e);
		} catch (DAOException e) {
			logger.error("Excepcion : InformesManager -  getModificacionAnexosExplotacion", e);
		} catch (BusinessException e) {
			logger.error("Excepcion : InformesManager - getModificacionAnexosExplotacion", e);
		}
		return null;
	}

	private InformeAnexModExplotacion getRegistroInformeAnexoBajaExplotacion(Long gr, Long tc, Animales animal,
			BigDecimal lineaSeguroId, BigDecimal idPoliza,
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion xmlExplotacion) {
		InformeAnexModExplotacion anexoModExplotacion = new InformeAnexModExplotacion();

		llenaDatosOriginales(xmlExplotacion, anexoModExplotacion, idPoliza, lineaSeguroId, gr, tc,
				new Long(animal.getTipo()), animal.getPrecio(), new Long(animal.getNumero()));

		anexoModExplotacion.setTipoModificacion("M");
		anexoModExplotacion.setTipoModificacionM("M");
		GruposRazas grupo = this.getGrupoRaza(gr, lineaSeguroId.longValue());
		anexoModExplotacion.setGrupoRaza(grupo.getId().getCodGrupoRaza().toString() + " - " + grupo.getDescripcion());
		anexoModExplotacion.setGrupoRazaM(" ");
		com.rsi.agp.dao.tables.cgen.TipoCapital tipoCapital = this.getTipoCapital(new BigDecimal(tc));
		anexoModExplotacion
				.setTipoCapital(tipoCapital.getCodtipocapital().toString() + " - " + tipoCapital.getDestipocapital());
		anexoModExplotacion.setTipoCapitalM(" ");
		TiposAnimalGanado tipoAnimal = (TiposAnimalGanado) this.getTipoAnimal(new Long(animal.getTipo()),
				new Long(lineaSeguroId.longValue()));
		anexoModExplotacion
				.setTipoAnimal(tipoAnimal.getId().getCodTipoAnimal().toString() + " - " + tipoAnimal.getDescripcion());
		anexoModExplotacion.setTipoAnimalM(" ");
		anexoModExplotacion.setNumanimales(new BigDecimal(animal.getNumero()));

		anexoModExplotacion.setNumAnimalesM(new BigDecimal(1).negate());

		anexoModExplotacion.setPrecio(animal.getPrecio().setScale(2, BigDecimal.ROUND_DOWN));
		anexoModExplotacion.setPrecioM(new BigDecimal(1).negate());
		return anexoModExplotacion;
	}

	private List<InformeDatosVariables> getDatosVariablesOrg(
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion xmlExplotacion,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta, Long grupoRazaM,
			BigDecimal lineaSeguroId, Long tipoCapitalM, Long TipoAnimalM) {

		List<InformeDatosVariables> listaInformeDatosVariables = new ArrayList<InformeDatosVariables>();

		if (!CollectionsAndMapsUtil.isEmpty(dvCodConceptoEtiqueta)) {
			try {
				// 1. Recorrer las claves de auxEtiquetaTabla
				Class<?> clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
				for (Map.Entry<BigDecimal, RelacionEtiquetaTabla> entry : dvCodConceptoEtiqueta.entrySet()) {
					Method method = null;
					String nombreEtiqueta = entry.getValue().getEtiqueta();
					BigDecimal codigoConcepto = entry.getKey();
					try {
						logger.debug("buscando...." + nombreEtiqueta);
						method = clase.getMethod("get" + nombreEtiqueta);
					} catch (Exception e) {
						logger.debug("El metodo no existe para esta clase " + e.getMessage());
					}
					if (method != null) {
						for (GrupoRaza grupoRaza : xmlExplotacion.getGrupoRazaArray()) {
							if (new Long(grupoRaza.getGrupoRaza()).equals(grupoRazaM)) {
								for (CapitalAsegurado capitalAsegurado : grupoRaza.getCapitalAseguradoArray()) {
									if (new Long(capitalAsegurado.getTipo()).equals(tipoCapitalM)) {
										DatosVariables datosVariables = capitalAsegurado.getDatosVariables();
										if (datosVariables != null) {
											Object objeto = method.invoke(datosVariables);
											if (objeto != null) {
												setDatosVariables(listaInformeDatosVariables, objeto, codigoConcepto,
														dvCodConceptoEtiqueta);
											} else {
												logger.debug("no encontrado");
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				logger.debug("El metodo acepta los argumentos " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.debug(" Error " + e.getMessage());
			} catch (InvocationTargetException e) {
				logger.debug("Error  " + e.getMessage());
			} catch (Exception e) {
				logger.debug("Error " + e.getMessage());
			}
		}
		return listaInformeDatosVariables;
	}

	private void setDatosVariables(List<InformeDatosVariables> listaInformeDatosVariables, Object objeto,
			BigDecimal codconcepto, final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta)
			throws Exception {

		logger.debug("encontrado");
		// despues obtengo el valor que tiene el objeto en el dato variable.
		Class<?> claseValor = objeto.getClass();
		Method methodValor = null;
		Object valor = null;
		try {
			methodValor = claseValor.getMethod("getValor");
			valor = methodValor.invoke(objeto);
		} catch (NoSuchMethodException e) {
			logger.debug("El metodo no existe para esta clase " + e.getMessage());
		}
		// 3. asigno el valor al dato variable
		if (!StringUtils.nullToString(valor).equals("")) {
			InformeDatosVariables inf = new InformeDatosVariables();
			inf.setNombreConcepto(dvCodConceptoEtiqueta.get(codconcepto).getNombreConcepto());
			inf.setCodConcepto(codconcepto.longValue());
			if (valor instanceof XmlCalendar) {
				SimpleDateFormat sdfToParse = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdfToFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date parsedDate = new Date();
				String formattedDate = "";
				try {
					parsedDate = sdfToParse.parse(valor.toString());
					formattedDate = sdfToFormat.format(parsedDate);
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha en los datos variables", e);
				}
				inf.setValor(formattedDate);
			} else {
				inf.setValor(StringUtils.nullToString(valor));
			}
			listaInformeDatosVariables.add(inf);
		}
	}

	private HashMap<Long, InformeDatosVariables> getMapDatosVariablesOriginales(
			List<InformeDatosVariables> datosVariablesOrg) {
		HashMap<Long, InformeDatosVariables> mapa = new HashMap<Long, InformeDatosVariables>();
		for (InformeDatosVariables dv : datosVariablesOrg) {
			Long codConceptoOrg = dv.getCodConcepto();
			mapa.put(codConceptoOrg, dv);
		}

		return mapa;
	}

	private void getDatosVariablesModificados(BigDecimal idExplotacionAnexo,
			InformeAnexModExplotacion anexoModExplotacion, BigDecimal idGrupoRaza) {
		try {

			@SuppressWarnings("unchecked")
			List<Object> listaDatosVariablesModificados = explotacionAnexoDao.getDatosVariables(idExplotacionAnexo,
					idGrupoRaza);
			// 0-idgruporaza || 1-idexplotacion || 2-codgruporaza || 3-codconcepto ||
			// 4-valor || 5_nomconcepto || 6-descripcion
			HashMap<Long, InformeDatosVariables> mapaDatosVariablesOriginales = this
					.getMapDatosVariablesOriginales(anexoModExplotacion.getDatosVariables());
			List<InformeDatosVariables> infnuevos = new ArrayList<InformeDatosVariables>();
			if (!CollectionsAndMapsUtil.isEmpty(mapaDatosVariablesOriginales)) {
				logger.info("EXISTE DATOS VARIABLES EN ORIGEN");
				if (!CollectionsAndMapsUtil.isEmpty(listaDatosVariablesModificados)) {
					logger.info("HAY DATOS VARIABLES MODOFICADOS");
					for (int i = 0; i < listaDatosVariablesModificados.size(); i++) {
						Object[] arrayDatooVariablesModificados = (Object[]) listaDatosVariablesModificados.get(i);
						Long codConceptoMod = ((BigDecimal) (arrayDatooVariablesModificados[3])).longValue();
						if (mapaDatosVariablesOriginales.containsKey(codConceptoMod)) {
							InformeDatosVariables infDatosOrg = mapaDatosVariablesOriginales.get(codConceptoMod);
							StringBuilder logMsgBeforeTest = new StringBuilder("Comprobamos si ")
									.append((String) (arrayDatooVariablesModificados[5])).append(" ha sido modificado");
							logger.info(logMsgBeforeTest.toString());
							if (infDatosOrg.getValor().compareTo((String) arrayDatooVariablesModificados[4]) != 0) {
								StringBuilder logMsg = new StringBuilder((String) (arrayDatooVariablesModificados[5]))
										.append(" SI ha sido modificado");
								logger.info(logMsg.toString());
								infDatosOrg.setCodConceptoM(
										((BigDecimal) (arrayDatooVariablesModificados[3])).longValue());
								infDatosOrg.setDescripcionM((String) arrayDatooVariablesModificados[6]);
								infDatosOrg.setNombreConceptoM((String) arrayDatooVariablesModificados[5]);
								infDatosOrg.setValorM((String) arrayDatooVariablesModificados[4]);
							}
							StringBuilder logMsgAfterTest = new StringBuilder(
									(String) (arrayDatooVariablesModificados[5])).append(" NO ha sido modificado");
							logger.info(logMsgAfterTest.toString());
						} else {
							StringBuilder logMsgNoModification = new StringBuilder("El concepto: ")
									.append((String) (arrayDatooVariablesModificados[5]))
									.append(" es nuevo, se agrega");
							logger.info(logMsgNoModification.toString());
							InformeDatosVariables infNuevoMod = new InformeDatosVariables();
							infNuevoMod.setCodConceptoM(((BigDecimal) (arrayDatooVariablesModificados[3])).longValue());
							infNuevoMod.setDescripcionM((String) arrayDatooVariablesModificados[6]);
							infNuevoMod.setNombreConceptoM((String) arrayDatooVariablesModificados[5]);
							infNuevoMod.setValorM((String) arrayDatooVariablesModificados[4]);
							infnuevos.add(infNuevoMod);
						}
					}
				} else {
					logger.info("LOS DATOS VARIABLES NO HAN SIDO MODIFICADOS");
					for (Map.Entry<Long, InformeDatosVariables> entry : mapaDatosVariablesOriginales.entrySet()) {
						InformeDatosVariables infDatosOrg = entry.getValue();
						infDatosOrg.setCodConceptoM(null);
						infDatosOrg.setDescripcionM("");
						infDatosOrg.setNombreConceptoM("");
						infDatosOrg.setValorM("");
					}
				}
				List<InformeDatosVariables> informeDatosVariables = new ArrayList<InformeDatosVariables>(
						mapaDatosVariablesOriginales.values());
				informeDatosVariables.addAll(infnuevos);
				anexoModExplotacion.setDatosVariables(informeDatosVariables);
			} else {
				logger.info("NO HAY DATOS VARIABLES ORIGINALES, LOS ANHADIMOS");
				List<InformeDatosVariables> nuevaLista = new ArrayList<InformeDatosVariables>();
				for (int i = 0; i < listaDatosVariablesModificados.size(); i++) {
					Object[] obj = (Object[]) listaDatosVariablesModificados.get(i);
					InformeDatosVariables inf = new InformeDatosVariables();
					inf.setCodConceptoM(((BigDecimal) (obj[3])).longValue());
					inf.setDescripcionM((String) obj[6]);
					inf.setNombreConceptoM((String) obj[5]);
					inf.setValorM((String) obj[4]);
					nuevaLista.add(inf);
				}
				anexoModExplotacion.setDatosVariables(nuevaLista);
			}
		} catch (BusinessException e) {
			logger.error("Excepcion : InformesManager - getDatosVariablesModificados", e);
		}
	}

	// Metodos auxiliares
	private String getDescripcionDatosVariables(Long codConcepto, String valor) {
		AdaptacionRiesgo adp = null;
		DatosBuzonGeneral dbg = null;
		String resultado = null;
		adp = (AdaptacionRiesgo) this.getObject(AdaptacionRiesgo.class, new String[] { "id.codcpto", "id.valorCpto" },
				new Object[] { new BigDecimal(codConcepto), new BigDecimal(valor) });
		if (null == adp) {
			dbg = (DatosBuzonGeneral) this.getObject(DatosBuzonGeneral.class,
					new String[] { "id.codcpto", "id.valorCpto" },
					new Object[] { new BigDecimal(codConcepto), new BigDecimal(valor) });
		}
		if (null != adp)
			resultado = adp.getDescripcion();
		if (null != dbg)
			resultado = dbg.getDescripcion();

		return resultado;
	}

	private Especie getEspecie(Long codEspecie, Long lineaSeguroId) {
		Especie especie = null;

		especie = (Especie) this.getObject(Especie.class, new String[] { "id.codespecie", "linea.lineaseguroid" },
				new Object[] { codEspecie, lineaSeguroId });
		return especie;
	}

	private RegimenManejo getRegimen(Long codRegimen, Long lineaSeguroId) {
		RegimenManejo regimen = null;
		regimen = (RegimenManejo) this.getObject(RegimenManejo.class,
				new String[] { "id.codRegimen", "linea.lineaseguroid" }, new Object[] { codRegimen, lineaSeguroId });
		return regimen;
	}

	private GruposRazas getGrupoRaza(Long codGrupo, Long lineaSeguroId) {
		GruposRazas grupo = null;
		grupo = (GruposRazas) this.getObject(GruposRazas.class, new String[] { "id.CodGrupoRaza", "id.lineaseguroid" },
				new Object[] { codGrupo, lineaSeguroId });

		return grupo;
	}

	private com.rsi.agp.dao.tables.cgen.TipoCapital getTipoCapital(BigDecimal codTipoCapital) {
		com.rsi.agp.dao.tables.cgen.TipoCapital tipoCapital = null;
		try {
			tipoCapital = (com.rsi.agp.dao.tables.cgen.TipoCapital) datosExplotacionAnexoDao
					.getObject(com.rsi.agp.dao.tables.cgen.TipoCapital.class, "codtipocapital", codTipoCapital);

		} catch (Exception e) {
			logger.error("ERROR traza sigpe 8471 en getTipoCapital" + e.getMessage());
		}
		return tipoCapital;
	}

	@SuppressWarnings("unchecked")
	private Termino getTermino(es.agroseguro.iTipos.Ambito ubicacion) {
		List<Termino> listaTerminos = null;
		Termino termino = null;
		BigDecimal p = new BigDecimal(ubicacion.getProvincia());
		BigDecimal c = new BigDecimal(ubicacion.getComarca());
		BigDecimal t = new BigDecimal(ubicacion.getTermino());
		String s = (String) ubicacion.getSubtermino();
		if (s.isEmpty())
			s = null;
		TerminoFiltro1 filtro = new TerminoFiltro1(p, c, t, s);

		listaTerminos = anexoModificacionDao.getObjects(filtro);
		if (null != listaTerminos && listaTerminos.size() > 0)
			termino = listaTerminos.get(0);
		return termino;
	}

	@SuppressWarnings("unchecked")
	private Termino getTermino(BigDecimal provincia, BigDecimal comarca, BigDecimal termino, String subtermino) {
		List<Termino> listaTerminos = null;
		Termino term = null;

		if (null != subtermino && subtermino.isEmpty())
			subtermino = null;
		TerminoFiltro1 filtro = new TerminoFiltro1(provincia, comarca, termino, subtermino);

		listaTerminos = anexoModificacionDao.getObjects(filtro);
		if (null != listaTerminos && listaTerminos.size() > 0)
			term = listaTerminos.get(0);
		return term;
	}

	@SuppressWarnings("unchecked")
	private TiposAnimalGanado getTipoAnimal(Long codTipoAnimal, Long lineaSeguroId) {
		List<Object> objetos = null;
		TiposAnimalGanado animal = null;
		try {
			objetos = anexoModificacionDao.findFiltered(TiposAnimalGanado.class,
					new String[] { "id.codTipoAnimal", "id.lineaseguroid" },
					new Object[] { codTipoAnimal, lineaSeguroId }, null);
			if (null != objetos && objetos.size() > 0)
				animal = (TiposAnimalGanado) objetos.get(0);

		} catch (DAOException e) {
			logger.error("ERROR traza sigpe 8471 en el dao de getTipoAnimal  " + e.getMessage());

		} catch (Exception ex) {
			logger.error("ERROR traza sigpe 8471 en getTipoAnimal " + ex.getMessage());

		}

		return animal;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getObject(Class clase, String[] parametros, Object[] valores) {
		List<Object> objetos = null;
		Object objeto = null;
		try {
			objetos = datosExplotacionAnexoDao.findFiltered(clase, parametros, valores, null);
			if (null != objetos && objetos.size() > 0)
				objeto = objetos.get(0);

			return objeto;
		} catch (DAOException e) {
			logger.error("Excepcion : InformesManager - getObject", e);
		}
		return objeto;
	}

	public Date getFechaContratacion(Poliza poliza,String codmodulo, String campo) throws Exception {
		logger.debug("INICIO getFechaContratacion");
		Object[] cp = null;
		// obtenemos los valores fijos de la poliza
		List<BigDecimal> listCultivos = new ArrayList<BigDecimal>();
		List<BigDecimal> listVariedad = new ArrayList<BigDecimal>();
		List<BigDecimal> listProvs = new ArrayList<BigDecimal>();
		List<BigDecimal> listCmc = new ArrayList<BigDecimal>();
		List<BigDecimal> listTerminos = new ArrayList<BigDecimal>();
		List<Character> listSubTerminos = new ArrayList<Character>();
		List<BigDecimal> listCicloCultivo = new ArrayList<BigDecimal>();
		List<BigDecimal> listSisCultivo = new ArrayList<BigDecimal>();
		List<BigDecimal> listtipoPlan = new ArrayList<BigDecimal>();
		List<BigDecimal> listsistProt = new ArrayList<BigDecimal>();
		List<BigDecimal> listTipoCapital = new ArrayList<BigDecimal>();
		try {
			Long lineaseguroid = poliza.getLinea().getLineaseguroid();

			for (com.rsi.agp.dao.tables.poliza.Parcela par : poliza.getParcelas()) {

				if (!listCultivos.contains(par.getCodcultivo())) {
					listCultivos.add(par.getCodcultivo());
				}
				if (!listVariedad.contains(par.getCodvariedad())) {
					listVariedad.add(par.getCodvariedad());
				}

				if (par.getTermino().getId().getCodprovincia() != null) {
					if (!listProvs.contains(par.getTermino().getId().getCodprovincia()))
						listProvs.add(par.getTermino().getId().getCodprovincia());
				}
				if (par.getTermino().getId().getCodcomarca() != null) {
					if (!listCmc.contains(par.getTermino().getId().getCodcomarca()))
						listCmc.add(par.getTermino().getId().getCodcomarca());
				}
				if (par.getTermino().getId().getCodtermino() != null) {
					if (!listTerminos.contains(par.getTermino().getId().getCodtermino()))
						listTerminos.add(par.getTermino().getId().getCodtermino());
				}
				if (StringUtils.nullToString(par.getTermino().getId().getSubtermino()) != null) {
					if (!listSubTerminos.contains(par.getTermino().getId().getSubtermino()))
						listSubTerminos.add(par.getTermino().getId().getSubtermino());
				}

				for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado c : par.getCapitalAsegurados()) {
					for (DatoVariableParcela dv : c.getDatoVariableParcelas()) {
						if (dv.getDiccionarioDatos().getCodconcepto()
								.intValue() == ConstantsConceptos.CODCPTO_CICLOCULTIVO
								&& FiltroUtils.noEstaVacio(new BigDecimal(dv.getValor()))) {
							if (!listCicloCultivo.contains(new BigDecimal(dv.getValor())))
								listCicloCultivo.add(new BigDecimal(dv.getValor()));
						}
					}
				}
			}
			if (!listCultivos.contains(new BigDecimal(999)))
				listCultivos.add(new BigDecimal(999));
			if (!listVariedad.contains(new BigDecimal(999)))
				listVariedad.add(new BigDecimal(999));
			if (!listProvs.contains(new BigDecimal(99)))
				listProvs.add(new BigDecimal(99));
			if (!listCmc.contains(new BigDecimal(99)))
				listCmc.add(new BigDecimal(99));
			if (!listTerminos.contains(new BigDecimal(999)))
				listTerminos.add(new BigDecimal(999));
			if (!listSubTerminos.contains('9'))
				listSubTerminos.add('9');

			// miramos en la tabla mascaras fechas de contratacion para ver si necesitamos
			// incluir algun dato variable
			List<MascaraFechaContrataAgricola> conceptos = fechaContratacionDao.getConceptosMascaras(listCultivos,
					listVariedad, listProvs, listCmc, listTerminos, listSubTerminos, lineaseguroid, codmodulo);
			for (int i = 0; i < conceptos.size(); i++) {
				MascaraFechaContrataAgricola mfc = conceptos.get(i);
				BigDecimal codConcepto = mfc.getId().getCodconcepto();
				if (codConcepto.intValue() == ConstantsConceptos.CODCPTO_CICLOCULTIVO
						|| codConcepto.intValue() == ConstantsConceptos.CODCPTO_SISTCULTIVO
						|| codConcepto.intValue() == ConstantsConceptos.CODCPTO_TIPO_PLANTACION
						|| codConcepto.intValue() == ConstantsConceptos.CODCPTO_SISTEMA_PROTECCION) {
					for (com.rsi.agp.dao.tables.poliza.Parcela par : poliza.getParcelas()) {
						for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado c : par.getCapitalAsegurados()) {
							for (DatoVariableParcela dv : c.getDatoVariableParcelas()) {

								if (dv.getDiccionarioDatos().getCodconcepto()
										.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CICLOCULTIVO))
										&& codConcepto
												.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CICLOCULTIVO))) {
									if (!listCicloCultivo.contains(new BigDecimal(dv.getValor())))
										listCicloCultivo.add(new BigDecimal(dv.getValor()));

								} else if (dv.getDiccionarioDatos().getCodconcepto()
										.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))
										&& codConcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
									if (!listSisCultivo.contains(new BigDecimal(dv.getValor())))
										listSisCultivo.add(new BigDecimal(dv.getValor()));

								} else if (dv.getDiccionarioDatos().getCodconcepto()
										.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_PLANTACION))
										&& codConcepto
												.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_PLANTACION))) {
									if (!listtipoPlan.contains(new BigDecimal(dv.getValor())))
										listtipoPlan.add(new BigDecimal(dv.getValor()));

								} else if (dv.getDiccionarioDatos().getCodconcepto()
										.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTEMA_PROTECCION))
										&& codConcepto.equals(
												BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTEMA_PROTECCION))) {
									if (!listsistProt.contains(new BigDecimal(dv.getValor())))
										listsistProt.add(new BigDecimal(dv.getValor()));
								}
							}
						}
					}
				} else if (codConcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPOCAPITAL))) { // 126
					for (com.rsi.agp.dao.tables.poliza.Parcela par : poliza.getParcelas()) {
						for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado c : par.getCapitalAsegurados()) {

							if (!listTipoCapital.contains(c.getTipoCapital().getCodtipocapital()))
								listTipoCapital.add(c.getTipoCapital().getCodtipocapital());

						}
					}
				} else if (codConcepto.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO))) {
					// obtenemos las coberturas elegidas (solo riesgo cubierto)
					cp = fechaContratacionDao.getDatosComparativas(poliza.getIdpoliza());
				}
			}

			return fechaContratacionDao.getFechaContratacion(listCultivos, listVariedad, listProvs, listCmc,
					listTerminos, listSubTerminos, codmodulo, lineaseguroid, cp, listCicloCultivo, listSisCultivo,
					listtipoPlan, listsistProt, listTipoCapital,campo);

		} catch (Exception e) {
			logger.debug("error al obtener la fecha de contratacion", e);
			throw e;
		}

	}

	public Object getFechaContratacionGan(Poliza poliza, String codmodulo, String campo) throws Exception {

		try {
			List<Long> listTipoCapital = new ArrayList<Long>();
			List<Long> listGrupoRaza = new ArrayList<Long>();
			List<Long> listTipoAnimal = new ArrayList<Long>();
			List<Long> listEspecies = new ArrayList<Long>();
			List<Long> listRegimenes = new ArrayList<Long>();
			List<Long> listProvs = new ArrayList<Long>();
			List<Long> listCmc = new ArrayList<Long>();
			List<Long> listTerminos = new ArrayList<Long>();
			List<Character> listSubTerminos = new ArrayList<Character>();

			Long lineaseguroid = poliza.getLinea().getLineaseguroid();

			for (Explotacion exp : poliza.getExplotacions()) {

				if (!listEspecies.contains(exp.getEspecie())) {
					listEspecies.add(exp.getEspecie());
				}
				if (!listRegimenes.contains(exp.getRegimen())) {
					listRegimenes.add(exp.getRegimen());
				}
				if (!listCmc.contains(exp.getTermino().getId().getCodcomarca().longValue())) {
					listCmc.add(exp.getTermino().getId().getCodcomarca().longValue());
				}
				if (!listProvs.contains(exp.getTermino().getId().getCodprovincia().longValue())) {
					listProvs.add(exp.getTermino().getId().getCodprovincia().longValue());
				}
				if (!listTerminos.contains(exp.getTermino().getId().getCodtermino().longValue())) {
					listTerminos.add(exp.getTermino().getId().getCodtermino().longValue());
				}
				if (!listSubTerminos.contains(exp.getTermino().getId().getSubtermino())) {
					listSubTerminos.add(exp.getTermino().getId().getSubtermino());
				}
				for (com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza r : exp.getGrupoRazas()) {

					if (!listTipoCapital.contains(r.getCodtipocapital().longValue())) {
						listTipoCapital.add(r.getCodtipocapital().longValue());
					}
					if (!listGrupoRaza.contains(r.getCodgruporaza())) {
						listGrupoRaza.add(r.getCodgruporaza());
					}
					if (!listTipoAnimal.contains(r.getCodtipoanimal())) {
						listTipoAnimal.add(r.getCodtipoanimal());
					}
				}
			}
			return fechaContratacionDao.getFechaContratacionGan(listEspecies, listRegimenes, listTipoCapital,
					listGrupoRaza, listTipoAnimal, listProvs, listCmc, listTerminos, listSubTerminos, codmodulo,
					lineaseguroid,campo);

		} catch (Exception e) {
			logger.debug("error al obtener la fecha de contratacion de ganado", e);
			throw e;
		}

	}

	public String getFormaPago(Poliza poliza) throws Exception {

		Date fechaSegundoPago = null;
		Date fechaEnvioPol = null;
		Date fechaPagoPol = null;
		BigDecimal tipoPago = null;

		if (poliza.getFechaenvio() != null) {
			fechaEnvioPol = poliza.getFechaenvio();
		} else {
			fechaEnvioPol = new Date();
		}
		if (poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0) {
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()) {
				PagoPoliza pp = it.next();
				if (pp.getFechasegundopago() != null) {
					fechaSegundoPago = pp.getFechasegundopago();
				}
				if (pp.getTipoPago() != null) {
					tipoPago = pp.getTipoPago();
				}
				if (pp.getFecha() != null) {
					fechaPagoPol = pp.getFecha();
				}
			}
		}
		if (tipoPago != null) {
			if (tipoPago.compareTo(Constants.CARGO_EN_CUENTA) == 0) { // Si es cargo en cuenta miramos si es financiada

				if (fechaSegundoPago != null) {
					return this.FINANCIADA;
				} else if (fechaEnvioPol != null && fechaPagoPol != null) {
					int dias = DateUtil.obtener_dis_entre_2_fechas(fechaEnvioPol, fechaPagoPol);
					if (dias >= Constants.DIAS_FINANCIADA)
						return this.FINANCIADA;
					else
						return this.CARGO_CUENTA;
				} else {
					return this.CARGO_CUENTA;
				}
			} else {
				return this.CONTADO;
			}
		}
		return null;
	}

	/**
	 * DAA Busca un siniestro anterior en estado enviado correcto de esa misma
	 * poliza
	 * 
	 * @param idSiniestro
	 * @throws BusinessException
	 */
	public String getRiesgoSiniestroAnterior(Integer idSiniestro) throws Exception {
		return siniestroDao.getRiesgoSiniestroAnterior(idSiniestro);
	}
	
	/**
	 * DAA Busca un siniestros anteriores en estado enviado correcto de esa misma
	 * poliza
	 * 
	 * @param idSiniestro
	 * @throws BusinessException
	 */
	public List<SiniestrosAnteriores> getRiesgoSiniestrosAnteriores(Integer idSiniestro) throws Exception {
		return siniestroDao.getRiesgoSiniestrosAnteriores(idSiniestro);
	}

	public ImporteFraccionamiento getImporteFraccionamiento(Long lineaSeguroId, SubentidadMediadora sm) {
		ImporteFraccionamiento res = null;
		res = importesFraccDao.obtenerImporteFraccionamiento(lineaSeguroId, sm);
		return res;
	}

	/**
	 * @author srojo Metodo privado que recorre las subvenciones ENESA y CCAA
	 *         comprobando si la poliza tiene la subvencion Caracteristica
	 *         Asegurado Persona Juridica. En el mapa de paramatros se anhade la
	 *         clave "existeSubCaractAsegPersonaJuridica" y el valor booleano en
	 *         funcion de si la subvencion esta presente, o no.
	 * @param poliza
	 */
	public Boolean polizaTieneSubvCaractAseguradoPersonaJuridica(Poliza poliza) {
		Boolean existeCaractAseguradoPersonaJuridica = Boolean.FALSE;
		BigDecimal codigoSubvencion = null;
		if (poliza.getLinea().isLineaGanado()) {
			/* Primero miramos que la subvencion esta en las otorgadas por ENESA */
			Set<SubAseguradoENESAGanado> conjuntoSubAseguradoENESA = poliza.getSubAseguradoENESAGanados();
			for (SubAseguradoENESAGanado subv : conjuntoSubAseguradoENESA) {
				codigoSubvencion = subv.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa();
				if (codigoSubvencion.compareTo(Constants.CARACT_ASEGURADO_PERSONA_JURIDICA) == 0) {
					// Como lo hemos encontrado, reasignamos el valor a la variable de retorno
					existeCaractAseguradoPersonaJuridica = Boolean.TRUE;
					// y nos salimos del bucle
					break;
				}
			}
			/*
			 * Si la subvencion no existe en las otorgadas por ENESA, lo buscamos en las
			 * otorgadas por las CCAA
			 */
			if (!existeCaractAseguradoPersonaJuridica) {
				Set<SubAseguradoCCAAGanado> conjuntoSubAseguradoCCAA = poliza.getSubAseguradoCCAAGanados();
				for (SubAseguradoCCAAGanado subv : conjuntoSubAseguradoCCAA) {
					codigoSubvencion = subv.getSubvencionCCAAGanado().getTipoSubvencionCCAA().getCodtiposubvccaa();
					if (codigoSubvencion.compareTo(Constants.CARACT_ASEGURADO_PERSONA_JURIDICA) == 0) {
						existeCaractAseguradoPersonaJuridica = Boolean.TRUE;
						break;
					}
				}
			}
		} else {
			/* Primero miramos que la subvencion esta en las otorgadas por ENESA */
			Set<SubAseguradoENESA> conjuntoSubAseguradoENESA = poliza.getSubAseguradoENESAs();
			for (SubAseguradoENESA subv : conjuntoSubAseguradoENESA) {
				codigoSubvencion = subv.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa();
				if (codigoSubvencion.compareTo(Constants.CARACT_ASEGURADO_PERSONA_JURIDICA) == 0) {
					// Como lo hemos encontrado, reasignamos el valor a la variable de retorno
					existeCaractAseguradoPersonaJuridica = Boolean.TRUE;
					// y nos salimos del bucle
					break;
				}
			}
			/*
			 * Si la subvencion no existe en las otorgadas por ENESA, lo buscamos en las
			 * otorgadas por las CCAA
			 */
			if (!existeCaractAseguradoPersonaJuridica) {
				Set<SubAseguradoCCAA> conjuntoSubAseguradoCCAA = poliza.getSubAseguradoCCAAs();
				for (SubAseguradoCCAA subv : conjuntoSubAseguradoCCAA) {
					codigoSubvencion = subv.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa();
					if (codigoSubvencion.compareTo(Constants.CARACT_ASEGURADO_PERSONA_JURIDICA) == 0) {
						existeCaractAseguradoPersonaJuridica = Boolean.TRUE;
						break;
					}
				}
			}
		}
		StringBuilder mensajeLog = new StringBuilder().append("La poliza con id: ").append(poliza.getIdpoliza())
				.append(", ").append(existeCaractAseguradoPersonaJuridica ? "si" : "no")
				.append(" tiene la subvencion Caract. Asegurado Persona Juridica");
		logger.debug(mensajeLog.toString());
		return existeCaractAseguradoPersonaJuridica;
	}

	/* Pet. 63485-Fase II ** MODIF TAM (24.09.2020) ** Inicio */
	
	/* METODOS CONTRATACION DE AGRICOLAS */
	
	@SuppressWarnings({ "rawtypes" })
	public HashMap<String, List> getDatosCobertParcelas(Poliza poliza, Integer numeroParc, Integer hojaParc,
			Long idParcela, Long idAnexo, Boolean usarCodModAnexo, Set<ComparativaPoliza> listaComparativasSitAct)
			throws Exception {
		
		logger.debug("getDatosCobertParcelas[INIT]");

		List<BeanTablaCobertParcelas> listaParc = new ArrayList<BeanTablaCobertParcelas>();

		HashMap<String, List> datosCobertParcela = new HashMap<String, List>();
		ModulosYCoberturas myc = null;

		logger.debug("Obteniendo el XML de ModulosYCoberturas de BBDD para la Parcelas: " + numeroParc + " y poliza: "
				+ poliza.getIdpoliza());

		List<ModuloFilaView> filasParcela = new ArrayList<ModuloFilaView>();

		logger.debug("Es poliza Agricola: " + !poliza.getLinea().isLineaGanado());
		
		// Comprobamos si la parcela tiene riesgos cubiertos seleccionados, en caso de
		// no tenerlos no se muestra la tabla
		Set<com.rsi.agp.dao.tables.poliza.Parcela> ListParcelas = poliza.getParcelas();
		Boolean tieneCoberParcelas = false;

		for (com.rsi.agp.dao.tables.poliza.Parcela parc : ListParcelas) {
			/*
			 * comprobamos si la parcela que estamos tratando tiene coberturas de PArcela
			 * elegidas
			 */
			if (parc.getIdparcela() == idParcela) {
				Set<ParcelaCobertura> coberturas = parc.getCoberturasParcela();

				for (ParcelaCobertura cob : coberturas) {    
					if (!new BigDecimal(Constants.RIESGO_ELEGIDO_NO).equals(cob.getCodvalor())) {
						tieneCoberParcelas = true;
						break;
					}
				}
			}
		}
		
		logger.debug("tieneCoberParcelas: " + tieneCoberParcelas);

		if (tieneCoberParcelas) {
			// Obtiene el xml recibido del SW de ayudas a la contratacion en la ultima
			// llamada
			String xml = datosCobertParcelasDao.getCobParcelas(poliza.getCodmodulo(), poliza.getIdpoliza(), numeroParc,
					hojaParc);
	
			logger.debug("xml : " + xml);
	
			if (xml != null) {
				// Convierte el xml recibido en un objeto ModulosYCoberturas
				myc = getMyCFromXml(xml);
			}
		}		

		// si el objeto myc es nulo continuamos como ya se hacia, si no es nulo usamos
		// dicho objeto para montar las filas de la parcela
		if (myc != null) {

			ModuloView modV = seleccionComparativasSWManager.getParcelasViewFromModulosYCoberturas(poliza, myc);
			filasParcela = modV.getListaFilas();
			logger.debug("filasParcela: " + filasParcela);

			// recogemos las cabeceras del clob
			List<String> listaCabeceras = recogeCabParcelaDelClob(poliza, myc);
			datosCobertParcela.put(CABECERA, listaCabeceras);
		} 

		logger.debug("ANTES DEL FOR ....... PARA RELLENAR LA LISTA");
		for (int i = 0; i < filasParcela.size(); i++) {
			ModuloFilaView fila = filasParcela.get(i);
			logger.debug("fila " + i + ": " + fila);

			rellenaListaBeanTablaParcelas(listaParc, fila, poliza.getCodmodulo(), idParcela, myc);
		}

		// Introducimos los registros encontrados en el tipo de objeto que vamos a usar
		// para montar*/
		datosCobertParcela.put(LISTA, listaParc);

		logger.debug("La lista final antes del return es " + listaParc);		
		logger.debug("getDatosCobertParcelas[END]");
		return datosCobertParcela;
	}

	/*
	 * Recoge las cabeceras de la lista de coberturas guardadas en BBDD desde el
	 * clob de la llamada al WS.
	 */
	private List<String> recogeCabParcelaDelClob(Poliza poliza, ModulosYCoberturas myc) {
		List<Integer> codCptos = new ArrayList<Integer>();
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<String> listaCabecerasParc = new ArrayList<String>();
		
		ConceptoCubiertoModuloFiltro filtroConcCbrto = new ConceptoCubiertoModuloFiltro(poliza.getLinea().getLineaseguroid(), poliza.getCodmodulo());
		@SuppressWarnings("unchecked")
		List<ConceptoCubiertoModulo> lstConcCbrtoMod = (List<ConceptoCubiertoModulo>) anexoModificacionDao.getObjects(filtroConcCbrto);

		
		if (myc.getParcelas() != null) {
			Parcela[] parcelaArray = myc.getParcelas().getParcelaArray();
			for (es.agroseguro.modulosYCoberturas.Parcela parcela : parcelaArray) {
				for (TipoCapitalAgricola tipoCapital : parcela.getTipoCapitalArray()) {
					for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : tipoCapital.getCoberturaArray()) {
						// buscamos max cabeceras de cada cobertura
						for (DatoVariable dv : cobertura.getDatoVariableArray()) {
							String codConceptoStr = String.valueOf(dv.getCodigoConcepto());
							Boolean conceptoVal = seleccionComparativasSWManager.existInListConcepto(lstConcCbrtoMod, codConceptoStr);
							
							if (("E".equals(dv.getTipoValor())
									|| (Constants.CHARACTER_S.equals(cobertura.getElegible().toString().charAt(0))
											&& "F".equals(dv.getTipoValor())))
									&& conceptoVal) {
								lstCabVariables.add(dv);
								codCptos.add(dv.getCodigoConcepto());
							}
						}
					}
				}
			}
		}	
		
		// ordenamos siempre las cabeceras por columna
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			if (!listaCabecerasParc.contains(dvCab.getNombre())) {
				listaCabecerasParc.add(dvCab.getNombre());
			}
		}
		return listaCabecerasParc;
	}
	
	private void rellenaListaBeanTablaParcAnexo(List<BeanTablaCobertParcelas> lista,
			ModuloFilaView fila, String codModulo, Long idParcela, ModulosYCoberturas myc) {

		logger.debug("rellenaListaBeanTablaParcAnexo [INIT]");

		BeanTablaCobertParcelas data = new BeanTablaCobertParcelas();

		String tempData;
		// Si la fila actual no es elegible procedemos normalmente
		if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") == -1 && fila.isRcElegible() == false) {
			logger.debug("fila actual NO ELEGIBLE");
			tempData = "";

			// Columnas fijas
			data.setParcela(fila.getConceptoPrincipalModulo());
			data.setRiesgosCubiertos(fila.getRiesgoCubierto());

			for (ModuloCeldaView mcv : fila.getCeldas()) {				
				if (mcv.isElegible()) {
					try {
						// Obtenemos el valor para la celda de la tabla de Parcelas
						tempData = obtenerParcDescvalorElegido(mcv, this.datosCobertParcelasDao
								.obtenerParcValorElegidoAnexo(mcv.getCodconcepto(), idParcela));
						data.getCeldas().add(tempData);
						tempData = "";
					} catch (Exception e) {
						logger.error(e.getMessage());
					}					
				} else {
					if (mcv.getValores().size() > 0)
						tempData = mcv.getValores().get(0).getDescripcion() + " ";
					if (mcv.getObservaciones() != null) {
						tempData += mcv.getObservaciones();
					}
					data.getCeldas().add(tempData);
					tempData = "";
				}
			}
			lista.add(data);
		} else {
			logger.debug("fila actual SI ELEGIBLE");
			// Si es elegible la fila, comprobamos si fue elegida. si lo fue, la pintamos y
			// si no, no
			tempData = "";
			boolean elegida = false;

			try {
				/*
				 * comprobamos que la fila esta elegida, en caso de no estar elegida no se
				 * imprime nada de esta fila
				 */
				logger.debug("Valor de CodConceptoPrincipalModulo: " + fila.getCodConceptoPrincipalModulo());
				logger.debug("Valor de CodRiesgoCubierto: " + fila.getCodRiesgoCubierto());

				elegida = datosCobertParcelasDao.obtenerParcelaElegidaAnexo(fila.getCodConceptoPrincipalModulo(),
						idParcela, codModulo, fila.getCodRiesgoCubierto());

				if (elegida) {
					// Columnas fijas
					data.setParcela(fila.getConceptoPrincipalModulo());
					data.setRiesgosCubiertos(fila.getRiesgoCubierto());

					if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") != -1) {
						data.setRiesgosCubiertos(fila.getRiesgoCubierto()
								.substring(TAB_ELEGIBLE.length()));
					} else {

						for (ModuloCeldaView mcv : fila.getCeldas()) {

							if (mcv.isElegible()) {
								tempData = obtenerParcDescvalorElegido(mcv, this.datosCobertParcelasDao
										.obtenerParcValorElegidoAnexo(mcv.getCodconcepto(), idParcela));							
								data.getCeldas().add(tempData);
								tempData = "";								
							} else {
								if (mcv.getValores().size() > 0) {
									/* comprobamos primero que el valor esta elegido */
									tempData = mcv.getValores().get(0).getDescripcion() + " ";
								}
								if (mcv.getObservaciones() != null) {
									tempData += mcv.getObservaciones();
								}
								data.getCeldas().add(tempData);
								tempData = "";								
							}
						}
					}
					lista.add(data);					
				}
			} catch (DAOException e) {
				logger.error(e.getMessage());
			}
		}
		logger.debug("rellenaListaBeanTablaParcAnexo [END]");
	}
	
	private void rellenaListaBeanTablaParcelas(List<BeanTablaCobertParcelas> lista,
			ModuloFilaView fila, String codModulo, Long idParcela, ModulosYCoberturas myc) {

		logger.debug("rellenaListaBeanTablaParcelas [INIT]");

		BeanTablaCobertParcelas data = new BeanTablaCobertParcelas();

		String tempData;
		// Si la fila actual no es elegible procedemos normalmente
		if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") == -1 && fila.isRcElegible() == false) {
			logger.debug("fila actual NO  ELEGIBLE");
			tempData = "";

			// Columnas fijas
			data.setParcela(fila.getConceptoPrincipalModulo());
			data.setRiesgosCubiertos(fila.getRiesgoCubierto());

			for (ModuloCeldaView mcv : fila.getCeldas()) {
				if (mcv.isElegible()) {
					try {
						// Obtenemos el valor para la celda de la tabla de Parcelas
						tempData = obtenerParcDescvalorElegido(mcv, this.datosCobertParcelasDao
								.obtenerParcValorElegido(mcv.getCodconcepto(), idParcela, codModulo));
					} catch (Exception e) {
						logger.error(e.getMessage());
					}					
				} else {
					if (mcv.getValores().size() > 0)
						tempData = mcv.getValores().get(0).getDescripcion() + " ";
					if (mcv.getObservaciones() != null) {
						tempData += mcv.getObservaciones();
					}
					
				}
				data.getCeldas().add(tempData);
				tempData = "";
			}
			lista.add(data);
		} else {
			logger.debug("fila actual SI  ELEGIBLE");
			// Si es elegible la fila, comprobamos si fue elegida. si lo fue, la pintamos y
			// si no, no
			tempData = "";
			boolean elegida = false;

			try {
				/*
				 * comprobamos que la fila esta elegida, en caso de no estar elegida no se
				 * imprime nada de esta fila
				 */
				logger.debug("Valor de CodConceptoPrincipalModulo: " + fila.getCodConceptoPrincipalModulo());
				logger.debug("Valor de CodRiesgoCubierto: " + fila.getCodRiesgoCubierto());

				elegida = datosCobertParcelasDao.obtenerParcelaElegida(fila.getCodConceptoPrincipalModulo(), idParcela,
						codModulo, fila.getCodRiesgoCubierto());

				if (elegida) {
					// Columnas fijas
					data.setParcela(fila.getConceptoPrincipalModulo());
					data.setRiesgosCubiertos(fila.getRiesgoCubierto());

					if (fila.getRiesgoCubierto().indexOf("ELEGIBLE") != -1) {
						data.setRiesgosCubiertos(fila.getRiesgoCubierto()
								.substring(TAB_ELEGIBLE.length()));
					} else {
						for (ModuloCeldaView mcv : fila.getCeldas()) {
							if (mcv.isElegible()) {
								tempData = obtenerParcDescvalorElegido(mcv, this.datosCobertParcelasDao
										.obtenerParcValorElegido(mcv.getCodconcepto(), idParcela, codModulo));
								data.getCeldas().add(tempData);
								tempData = "";								
							} else {
								if (mcv.getValores().size() > 0)
									/* comprobamos primero que el valor esta elegido */
									tempData = mcv.getValores().get(0).getDescripcion() + " ";

								if (mcv.getObservaciones() != null) {
									tempData += mcv.getObservaciones();
								}
								data.getCeldas().add(tempData);
								tempData = "";
							}
						}
					}
					lista.add(data);
				}
			} catch (DAOException e) {
				logger.error(e.getMessage());
			}
		}
		logger.debug("rellenaListaBeanTablaParcelas [END]");
	}
	
	/******* METODOS DE ANEXOS DE AGRICOLAS *******/
	@SuppressWarnings({ "rawtypes", "unused" })
	public HashMap<String, List> getDatosCobertParcelasAnexo(AnexoModificacion am, Integer numeroParc, Integer hojaParc,
			com.rsi.agp.dao.tables.anexo.Parcela parcela, Poliza poliza, Boolean usarCodModAnexo,
			Set<ComparativaPoliza> listaComparativasSitAct) throws Exception {

		List<BeanTablaCobertParcelas> listaParc = new ArrayList<BeanTablaCobertParcelas>();

		logger.debug("getDatosCobertParcelasAnexo [INIT]");

		HashMap<String, List> datosCobertParcela = new HashMap<String, List>();
		ModulosYCoberturas myc = null;

		logger.debug("Obteniendo el XML de ModulosYCoberturas de BBDD para la Parcelas: " + numeroParc + " y anexo: "
				+ am.getId());

		List<ModuloFilaView> filasParcela = new ArrayList<ModuloFilaView>();

		// Obtiene el xml recibido del SW de ayudas a la contratacion en la ultima
		// llamada
		Clob respuesta = null;

		String xml = datosCobertParcelasDao.getCobParcelasAnexo(am.getCodmodulo(), am.getId(), parcela.getId(),
				numeroParc, hojaParc);

		if (xml != null) {
			// Convierte el xml recibido en un objeto ModulosYCoberturas
			myc = getMyCFromXml(xml);
		}

		logger.debug("myc: " + myc);

		// si el objeto myc es nulo continuamos como ya se hacia, si no es nulo usamos
		// dicho objeto para montar las filas de la parcela
		if (myc != null) {

			ModuloView modV = seleccionComparativasSWManager.getParcelasViewFromModulosYCoberturas(poliza, myc);
			logger.debug("modV: " + modV);

			filasParcela = modV.getListaFilas();
			logger.debug("filasParcela: " + filasParcela);

			// recogemos las cabeceras del clob
			List<String> listaCabeceras = recogeCabParcelaDelClob(poliza, myc);
			datosCobertParcela.put(CABECERA, listaCabeceras);

			logger.debug("listaCabeceras : " + listaCabeceras);
		} 

		for (int i = 0; i < filasParcela.size(); i++) {
			ModuloFilaView fila = filasParcela.get(i);
			/* De momento comentamos esto */
			rellenaListaBeanTablaParcAnexo(listaParc, fila, am.getCodmodulo(), parcela.getId(), myc);
		}

		// Introducimos los registros encontrados en el tipo de objeto que vamos a usar
		// para montar*/
		datosCobertParcela.put(LISTA, listaParc);
		
		logger.debug("La lista final antes del return es " + listaParc);		
		logger.debug("getDatosCobertParcelasAnexo [END]");
		return datosCobertParcela;
	}
	
	/* Obtenemos la descripci�n del valor elegido desde el xml */
	private String obtenerParcDescvalorElegido(final ModuloCeldaView mcv, final String valor) {
		String result = "";
		for (ModuloValorCeldaView mvcv : mcv.getValores()){
			if (mvcv.getCodigo().equals(valor)){
				result = mvcv.getDescripcion();
				break;
			}
		}
		return result;
	}	
	/* Pet. 63485-Fase II ** MODIF TAM (24.09.2020) ** Fin */
	// -------------------------------------------------------------------------------------

	public String getTipoAsegurado(final BigDecimal idComparativa) {
		return diccionarioDatosDao.getTipoAsegurado(idComparativa);
	}
	
	// Propiedades SET -------------------------------------------------------

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public void setModuloManager(ModuloManager moduloManager) {
		this.moduloManager = moduloManager;
	}

	public void setSiniestroDao(ISiniestroDao siniestroDao) {
		this.siniestroDao = siniestroDao;
	}

	public void setCuadroCoberturasManager(ICuadroCoberturasManager cuadroCoberturasManager) {
		this.cuadroCoberturasManager = cuadroCoberturasManager;
	}

	public void setNotaInformativaDao(INotaInformativaDao notaInformativaDao) {
		this.notaInformativaDao = notaInformativaDao;
	}

	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}

	public void setCuadroCoberturasGanadoManager(ICuadroCoberturasGanadoManager cuadroCoberturasGanadoManager) {
		this.cuadroCoberturasGanadoManager = cuadroCoberturasGanadoManager;
	}

	public void setExplotacionAnexoDao(IExplotacionAnexoDao explotacionAnexoDao) {
		this.explotacionAnexoDao = explotacionAnexoDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setDatosExplotacionAnexoDao(IDatosExplotacionAnexoDao datosExplotacionAnexoDao) {
		this.datosExplotacionAnexoDao = datosExplotacionAnexoDao;
	}

	public void setFechaContratacionDao(IFechaContratacionDao fechaContratacionDao) {
		this.fechaContratacionDao = fechaContratacionDao;
	}

	/* Pet. 57622 ** MODIF TAM (14.06.2019) */
	public void setDatosExplotacionDao(IDatosExplotacionDao datosExplotacionDao) {
		this.datosExplotacionDao = datosExplotacionDao;
	}

	public void setDatosExplotacionesManager(IDatosExplotacionesManager datosExplotacionesManager) {
		this.datosExplotacionesManager = datosExplotacionesManager;
	}
	// ---------------------------------------------------------------------------------

	public void setImportesFraccDao(IImportesFraccDao importesFraccDao) {
		this.importesFraccDao = importesFraccDao;
	}

	public void setSeleccionComparativasSWManager(ISeleccionComparativasSWManager seleccionComparativasSWManager) {
		this.seleccionComparativasSWManager = seleccionComparativasSWManager;
	}

	public void setDatosCobertParcelasDao(IDatosCobertParcelasDao datosCobertParcelasDao) {
		this.datosCobertParcelasDao = datosCobertParcelasDao;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}
