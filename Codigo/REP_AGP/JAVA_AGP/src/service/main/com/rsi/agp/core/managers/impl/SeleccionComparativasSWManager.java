package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ISeleccionComparativasSWManager;
import com.rsi.agp.core.managers.impl.ganado.ContratacionAyudasHelper;
import com.rsi.agp.core.util.ComparativaPolizaComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.DatoVariableComparator;
import com.rsi.agp.core.util.ModuloFilaViewComparator;
import com.rsi.agp.core.util.ModuloPolizaComparator;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.cpl.ConceptoCubiertoModuloFiltro;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ganado.ISeleccionComparativaSWDao;
import com.rsi.agp.dao.models.poliza.ganado.SeleccionComparativaSWDao;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaVinculacionView;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaSimple;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaCoberturaSW;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.modulosYCoberturas.Cobertura;
import es.agroseguro.modulosYCoberturas.DatoVariable;
import es.agroseguro.modulosYCoberturas.Explotacion;
import es.agroseguro.modulosYCoberturas.Fila;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturasDocument;
import es.agroseguro.modulosYCoberturas.Parcela;
import es.agroseguro.modulosYCoberturas.TipoCapitalAgricola;
import es.agroseguro.modulosYCoberturas.Valor;
import es.agroseguro.modulosYCoberturas.VinculacionCelda;
import es.agroseguro.modulosYCoberturas.VinculacionFila;
import es.agroseguro.serviciosweb.contratacionayudas.AgrException;
import es.agroseguro.serviciosweb.contratacionayudas.CoberturasContratadasResponse;
import es.agroseguro.serviciosweb.contratacionayudas.ModulosCoberturasResponse;

public class SeleccionComparativasSWManager implements ISeleccionComparativasSWManager {

	protected ModuloManager moduloManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private IPolizaDao polizaDao;
	private ISeleccionComparativaSWDao seleccionComparativaSWDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	
	private static final Log logger = LogFactory.getLog(SeleccionComparativasSWManager.class);
	
	private final String ELEGIBLE = "E";
	private final String BASICA = "B";
	
	@Override 
	public Map<String, Object> generarListaComparativas(long idpoliza, boolean backImportesOLectura, String realPath, Usuario usuario) {

		logger.debug("generarListaComparativas [INIT]");
		
		// Mapa de objetos que se devuelven al controlador
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		// Carga la poza y obtiene el listado de modulos asociados
		Poliza p = seleccionPolizaManager.getPolizaById(idpoliza);
		
		List<ModuloView> lstComparativasModulo = new ArrayList<ModuloView>();
		
		// Obtiene el max num de comparativas por modulo
		BigDecimal maxNumComparativas = null;
		try {
			maxNumComparativas = seleccionComparativaSWDao.getMaxNumComparativas();
		} catch (DAOException e) {
			logger.error("Error al recoger el max num Comparativas de BBDD", e);
			
		}
		StringBuilder lstModulos = new StringBuilder();
		StringBuilder compVisiblesBBDD = new StringBuilder();
		try {
			// Por cada modulo asociado se obtiene el objeto 'ModulosYCoberturas' asociado y se crea el listado de coberturas a pintar en pantalla
			List <Long> lstMods = new ArrayList<Long>();
			
			ModulosYCoberturas myc =null;
			int i=0;
			ModuloPoliza mpPorDefecto=null; // Uno cualquiera, para construir los no existentes hasta el maximo de comparativas posibles
			List<ModuloPoliza> mFinal = new ArrayList<ModuloPoliza>();
			mFinal.addAll(p.getModuloPolizas());
			Collections.sort(mFinal, new ModuloPolizaComparator());
			Map<String, ModulosYCoberturas> mycSW = new HashMap<String, ModulosYCoberturas>();
			for (ModuloPoliza mp : mFinal) {
				Long keyMod = mp.getId().getNumComparativa();
				if (null==mpPorDefecto) {
					mpPorDefecto = mp;
				}
				if (!lstMods.contains(keyMod)){
					lstMods.add(keyMod);
					if (backImportesOLectura) {
						myc = getModulosYCoberturasBBDD(mp, p, realPath);
					} else {
						if (mycSW.keySet().contains(mp.getId().getCodmodulo())) {
							myc = mycSW.get(mp.getId().getCodmodulo());	
						} else {
							myc = getModulosYCoberturasSW(mp, p, realPath, usuario);
							mycSW.put(mp.getId().getCodmodulo(), myc);
						}
					}	
					lstModulos.append(mp.getId().getCodmodulo());
					lstModulos.append(",");	
					if (myc != null){				
						i+=1;
						lstComparativasModulo.add(getModuloViewFromModulosYCoberturas(myc, mp, i, mp.getId().getCodmodulo(), true));
					}
					compVisiblesBBDD.append(mp.getId().getCodmodulo());
					compVisiblesBBDD.append("_");
					compVisiblesBBDD.append(i);
					compVisiblesBBDD.append(":");
				}			
			}
			
			for (int j = i; j < maxNumComparativas.intValue(); j++) {	
				int numComp = j + 1;
				lstComparativasModulo.add(getModuloViewFromModulosYCoberturas(myc, mpPorDefecto, numComp, mpPorDefecto.getId().getCodmodulo(), false));
			}
			lstModulos.substring(0, lstModulos.length() - 1);
			compVisiblesBBDD.substring(0, compVisiblesBBDD.length() - 1);
		} 
		catch (Exception e) {
			logger.error("Error al obtener las comparativas de la póliza", e);
			
			// Si es un error controlado del SW se muestra la descripcion del mensaje en la pantalla
			if (e instanceof AgrException) {
				mapa.put("alerta", WSUtils.debugAgrException(e));
			}
			// Si no, se devuelve un error generico
			else {
				mapa.put("alerta", "Error al obtener las comparativas de la póliza");
			}
		}
		
		List<ComparativaPoliza> compFinal = new ArrayList<ComparativaPoliza>();		
		if (null != p.getComparativaPolizas() && p.getComparativaPolizas().size() > 0){
			compFinal.addAll(p.getComparativaPolizas());			
			Collections.sort(compFinal, new ComparativaPolizaComparator());	
			long numComparativa = 1;
			Map<Long, Long> mapaIds = new HashMap<Long, Long>();
			for (ComparativaPoliza cp : compFinal) {
				if (!mapaIds.containsKey(cp.getId().getIdComparativa())) {
					mapaIds.put(cp.getId().getIdComparativa(), numComparativa++);
				}
				cp.getId().setIdComparativa(mapaIds.get(cp.getId().getIdComparativa()));
			}
		}
		
		// Se devuelve un mapa de objetos que contiene la poliza cargada de BD y la lista comparativas por modulo
		for (ModuloView mv : lstComparativasModulo) {
			logger.debug("[ESC-26336] ModuloView: " + mv);
		}
		mapa.put("listaMod", lstComparativasModulo);
		mapa.put("poliza", p);
		mapa.put("listaCoberturasElegidas", compFinal);
		mapa.put("modulos", lstModulos.toString());
		mapa.put("compVisiblesBBDD", compVisiblesBBDD.toString());
		
		// Obtiene el listado de tipos de asegurado de ganado si aplica para el plan/linea
		mapa.put("listaTipoAseguradoGanado", generarListaTipoAseguradoGanado(p.getLinea().getLineaseguroid()));
		mapa.put("maxComparativas", maxNumComparativas);
		
		logger.debug("generarListaComparativas [END]");
		
		return mapa;
	}
	
	@Override
	public Map<String, Object> guardarComparativas(long idpoliza, long lineaseguroid, String[] infoModulos, String[] infoCoberturas) {
		
		// Mapa de objetos que se devuelven al controlador
		Map<String, Object> mapa = new HashMap<String, Object>();
		Map<String, Long> mapaSecuencias = new HashMap<String, Long>();
		
		// Actualiza el indicador de 'Renovable' de cada modulo asociado a la poliza con la informacion obtenida de la pantalla
		// y el campo 'Tipo de asegurado de ganado' si viene informado
		try {
			
			seleccionComparativaSWDao.borrarComparativasNoElegidas(idpoliza, lineaseguroid, infoModulos);
			
			for (String infoMod : infoModulos) {
				String[] info = infoMod.split("#");
				String codModulo = info[0];
				int numComparativa = Integer.valueOf(info[1]);
				int renovable = Integer.valueOf(info[2]);
				Integer tipoAsegGanado = info.length > 3 ? Integer.valueOf(info[3]) : null;
				Long secuencia = seleccionComparativaSWDao.actualizaModuloRenovable(idpoliza, lineaseguroid, codModulo, numComparativa, renovable, tipoAsegGanado);
				
				mapaSecuencias.put(codModulo + "_" + numComparativa, secuencia);
			}
		} catch (Exception e) {
			logger.error("Error al actualizar el indicador de renovable de la poliza " + idpoliza, e);
			mapa.put("alerta", "Error al guardar la información del módulo");
			return mapa;
		}
		
		// Genera la lista de comparativas de poliza a partir de la informacion obtenida de la pantalla y la guarda en BBDD
		try {
			List<ComparativaPolizaSimple> listCp = new ArrayList<ComparativaPolizaSimple>();
			Map<String, String> clavesProcesadas = new HashMap<String, String>();
			
			
			for (String infoCob : infoCoberturas) {
				
				if (!StringUtils.isNullOrEmpty(infoCob)) {
					String[] infoTemp = infoCob.split("_");
					
					// MPM - 22/09/2016. (Por incidencia urgente en produccion) Genera una clave para identificar al registro de comparativa y 
					// la guarda, para evitar guardar en la lista comparativas identicas y que falle el guardado por restriccion de PK
					String claveComparativa = getClaveComparativa (infoTemp, mapaSecuencias);
					
					if (!clavesProcesadas.containsKey(claveComparativa)) {
						
						ComparativaPolizaSimple cp = creaComparativaPolizaSimple(idpoliza, lineaseguroid,
								infoCob.split("_"), mapaSecuencias.get(infoTemp[0] + "_" + infoTemp[1]));
						if (cp != null)	{
							listCp.add(cp);
							clavesProcesadas.put(claveComparativa, "");
						}
					}
				}
			}
			
			// Si no hay coberturas elegibles que guardar vuelve al controlador
			if (listCp.isEmpty()) return mapa;
			
			seleccionComparativaSWDao.guardaListaComparativasPoliza(idpoliza, listCp);
		}
		catch (Exception e) {
			logger.error("Error al guardar las comparativas elegidas de la poliza " + idpoliza, e);
			mapa.put("alerta", "Error al guardar las comparativas");
			return mapa;
		}
		
		return mapa;
	}
	
	private String getClaveComparativa (String[] infoCob, Map<String, Long> mapaSecuencias) {
		
		String str = infoCob[1] + "_" + infoCob[2] + "_" + infoCob[3] + "_" + infoCob[4] + "_" + infoCob[5] + "_" +
					 infoCob[6] + "_" + ((infoCob.length == 8) ? (infoCob[7] + "_") : "") +  mapaSecuencias.get(infoCob[0]+"_"+infoCob[1]);
		
		return str;
	}
	
	
	/**
	 * Comprueba si aplica el dato variable de cabecera 'Tipo asegurado ganado' para el plan/linea de la poliza accediendo al organizador
	 * y en caso afirmativo obtiene los valores correspondientes del buzon general 
	 * @param lineaseguroid
	 * @return
	 */
	private List<DatosBuzonGeneral> generarListaTipoAseguradoGanado(long lineaseguroid) {
		
		List<DatosBuzonGeneral> listaTipoAseguradoGanado = new ArrayList<DatosBuzonGeneral>();
		
		try {
			// Si aplica el dato variable de cabecera 'Tipo asegurado ganado' para el plan/linea
			if (seleccionComparativaSWDao.aplicaTipoAseguradoGanado(lineaseguroid)) {
				
				// Obtiene los valores correspondientes del buzon general
				listaTipoAseguradoGanado = seleccionComparativaSWDao.obtenerListaTipoAseguradoGanado();
			}
		} 
		catch (Exception e) {
			logger.error("Error al obtener el listado de tipos de asegurado de ganado al lineaseguroid " + lineaseguroid, e);
		}
		return listaTipoAseguradoGanado;
	}

	

	/**
	 * @param idpoliza
	 * @param lineaseguroid
	 * @param info
	 */
	private ComparativaPolizaSimple creaComparativaPolizaSimple(long idpoliza, long lineaseguroid,	String[] info, Long numComparativa) {
		
		// MPM - 22/09/2016. (Por incidencia urgente en produccion) 
		// Si el identificador de la comparativa es nulo no se puede crear el registro de comparativa correspondiente en BD 
		if (numComparativa == null) return null;
		
		try {
			ComparativaPolizaSimple cp = new ComparativaPolizaSimple();
			cp.setIdpoliza(idpoliza);
			cp.setLineaseguroid(lineaseguroid);
			cp.setCodmodulo(info[0]);
			cp.setFilamodulo(new Integer (info[2]));
			cp.setCpm(new Integer (info[3]));
			cp.setRc(new Integer (info[4]));
			cp.setConcepto(new Integer(info[5]));
			cp.setFilacomparativa(new Integer (info[6]));
			// MPM - 22/09/2016. (Por incidencia urgente en produccion)
			// Si no viene informado el valor (sin elegibles) se pone un -2
			cp.setValor((info.length >= 8) ? new Integer (info[7]) : -2);
			// Si viene informada la descripcion del valor
			if (info.length >= 9) cp.setDescValor(info[8]);
			cp.setNumComparativa(numComparativa);
			
			return cp;
		} catch (Exception e) {
			logger.error("Error al crear el objeto que encapsula la informacion de la comparativa simple", e);
			return null;
		}
		
	}
	
	/**
	 * Procesa el objeto 'ModulosYCoberturas' recibido como parametro y genera un objeto 'ModuloView', que encapsula la informacion
	 * que se mostrara en pantalla de las comparativas de un modulo
	 * @param myc
	 * @param mp
	 * @return
	 */
	public ModuloView getModuloViewFromModulosYCoberturas (final ModulosYCoberturas myc, final ModuloPoliza mp, 
			int numComparativa, String codMod, boolean llenaRenovable) {
		
		logger.debug("getModuloViewFromModulosYCoberturas [INIT]");
		
		ModuloView mv = new ModuloView();
	
		// Obtiene el objeto modulo correspondiente al plan/linea y modulo indicados
		Modulo modulo = moduloManager.getModulo(mp.getId().getCodmodulo(), mp.getId().getLineaseguroid());
		
		// Establece el codigo, la descripcion, el indicador de renovable del modulo y el tipo de asegurado
		mv.setCodModulo(codMod);
		mv.setDescripcionModulo(modulo != null ? modulo.getDesmodulo() : "");
		if(llenaRenovable){
			mv.setRenovable(mp.getRenovable());
			mv.setIdModulo(mp.getId().getNumComparativa());
		}
		mv.setTipoAsegGanado(mp.getTipoAsegGanado());
		mv.setNumComparativa(numComparativa);
		
		// Si no existe el elemento 'Modulo' en el xml no continua el proceso
		if (myc == null || myc.getModuloArray() == null || myc.getModuloArray().length == 0) return mv; 		
		
		// Recorre la lista de coberturas
		Cobertura[] coberturaArray = myc.getModuloArray(0).getCoberturaArray();	
		logger.debug("SeleccionComparativasSWManager- obtenemos coberturaArray");		
		
		List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>();
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<Integer> codCptos = new ArrayList<Integer>();
		List<String> listCabs = new ArrayList<String>();
		// buscamos max cabeceras de cada cobertura
		for (Cobertura cobertura : coberturaArray) {
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {
				if (!codCptos.contains(dv.getCodigoConcepto())){
					lstCabVariables.add(dv);
					codCptos.add(dv.getCodigoConcepto());
				}
			}
		}
		if (mv.getListaCabeceras().isEmpty())
			mv.setListaCabeceras(listCabs);		
		
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			if (!listCabs.contains(dvCab.getNombre())) {
				listCabs.add(dvCab.getNombre());
			}
		}
		
		for (Cobertura cobertura : coberturaArray) {
			// Compone el objeto 'ModuloFilaView' que encapsula la informacion de una fila del cuadro de comparativas
			ModuloFilaView mfv = getModuloFilaView(cobertura, listCabs, false);
			if (mfv != null) {
				// Compone la lista de celdas variables asociadas a la fila del cuadro de comparativas 
				mfv.setCeldas(getListaCeldas(cobertura, listCabs));			
				// Compone las listas de vinculaciones al objeto de la fila del cuadro de comparativas
				mfv.setListVinculaciones(getListaVinculaciones(cobertura));			
				listaFilas.add (mfv);
			}
		}
		Collections.sort(listaFilas, new ModuloFilaViewComparator());
		mv.setListaFilas(listaFilas);
		logger.debug("getModuloViewFromModulosYCoberturas [END]");
		
		return mv;
	}
	
	
	

	/** Pet. 63485 ** MODIF TAM (17/07/2020) ** Inicio */
	/**
	 * Procesa el objeto 'ModulosYCoberturas' recibido como parametro y genera un objeto 'ModuloView', que encapsula la informacion
	 * que se mostrara en pantalla de las comparativas de un modulo para las Polizas AGRiCOLAS
	 * @param myc
	 * @param mp
	 * @return
	 */
	
	public ModuloView getModuloViewFromModulosYCobertAgricolas(final ModulosYCoberturas myc, final ModuloPoliza mp, 
			int numComparativa, String codMod) {
		
		logger.debug ("getModuloViewFromModulosYCobertAgricolas [INIT]");
		
		ModuloView mv = new ModuloView();
		
		// Obtiene el objeto modulo correspondiente al plan/linea y modulo indicados
		Modulo modulo = moduloManager.getModulo(mp.getId().getCodmodulo(), mp.getId().getLineaseguroid());
		
		// Establece el codigo, la descripcion, el indicador de renovable del modulo y el tipo de asegurado
		mv.setCodModulo(codMod);
		mv.setDescripcionModulo(modulo != null ? modulo.getDesmodulo() : "");
		
		mv.setIdModulo(mp.getId().getNumComparativa());
		mv.setTipoAsegGanado(mp.getTipoAsegGanado());
		mv.setNumComparativa(numComparativa);
		
		// Si no existe el elemento 'Modulo' en el xml no continua el proceso
		if (myc.getModuloArray().length == 0) return mv; 		
		
		// Recorre la lista de coberturas
		Cobertura[] coberturaArray = myc.getModuloArray(0).getCoberturaArray();				
		
		List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>();
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<Integer> codCptos = new ArrayList<Integer>();

		// buscamos max cabeceras de cada cobertura
		logger.debug("Recorremos bucle Coberturas");		
		
		// CABECERAS DE LAS COBERTURAS 
		// --------------------------------------
		for (Cobertura cobertura : coberturaArray) {
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {
				if (!codCptos.contains(dv.getCodigoConcepto())){
					lstCabVariables.add(dv);
					codCptos.add(dv.getCodigoConcepto());
				}
			}
		}
		if (mv.getListaCabeceras().isEmpty()) {
			mv.setListaCabeceras(new ArrayList<String>());		
		}
		
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			if (!mv.getListaCabeceras().contains(dvCab.getNombre())) {
				mv.getListaCabeceras().add(dvCab.getNombre());
			}
		}
		
		// DETALLE DE LAS COBERTURAS 
		// --------------------------------------
		for (Cobertura cobertura : coberturaArray) {
			
			// Compone el objeto 'ModuloFilaView' que encapsula la informacion de una fila del cuadro de comparativas
			ModuloFilaView mfv = getModuloFilaView(cobertura, mv.getListaCabeceras(), false);
			if (mfv != null) {
				// Compone la lista de celdas variables asociadas a la fila del cuadro de comparativas 
				mfv.setCeldas(getListaCeldas(cobertura, mv.getListaCabeceras()));			
				listaFilas.add (mfv);
			}
		}
		
		
		Collections.sort(listaFilas, new ModuloFilaViewComparator());
		mv.setListaFilas(listaFilas);
		
		logger.debug ("getModuloViewFromModulosYCobertAgricolas [END]");
		
		return mv;
	}
	
	/** Pet. 63485 ** MODIF TAM (17/07/2020) ** Fin */
	
	/* Pet. 57622 - REQ.01 * INICIO */
	/**
	 * Procesa el objeto 'ModulosYCoberturas' recibido como parametro y genera un objeto 'ModuloView', que encapsula la informacion
	 * que se mostrara en pantalla de las coberturas de las explotaciones 
	 * @param myc
	 * @param mp
	 * @return
	 */
	public ModuloView getExplotacionesViewFromModulosYCoberturas (final ModulosYCoberturas myc, int numExplotacion) {
		
		ModuloView mv = new ModuloView();
		
		// Si no existe el elemento 'Modulo' en el xml no continua el proceso
		if (myc.getExplotaciones().getExplotacionArray().length == 0) {
			return mv;
		}else {
			
		}
		
		// Recorre la lista de coberturas
		Explotacion[] explotacionArray = myc.getExplotaciones().getExplotacionArray();			
		
		
		List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>();
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		List<Integer> codCptos = new ArrayList<Integer>();
		
		// buscamos max cabeceras de cada cobertura de cada explotacion.
		for (Explotacion explotacion : explotacionArray) {
			if (numExplotacion == explotacion.getNumero()) {
				for (Cobertura cobertura : explotacion.getCoberturaArray()) {
					for (DatoVariable dv : cobertura.getDatoVariableArray()) {
						if (!codCptos.contains(dv.getCodigoConcepto())){
							lstCabVariables.add(dv);
							codCptos.add(dv.getCodigoConcepto());
						}
					}
				}
				break;
			}
		}
		
		if (mv.getListaCabeceras().isEmpty()) {
			mv.setListaCabeceras(new ArrayList<String>());		
		}
		
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			mv.getListaCabeceras().add(dvCab.getNombre());
		}
		
		for (Explotacion explotacionB : explotacionArray) {
			if (numExplotacion == explotacionB.getNumero()) { 
				for (Cobertura cobertExpl : explotacionB.getCoberturaArray()) {				
					// Compone el objeto 'ModuloFilaView' que encapsula la informacion de una fila del cuadro de comparativas de la explotacion
					ModuloFilaView mfv = getModuloFilaView(cobertExpl, mv.getListaCabeceras(), true);				
					if (mfv != null) {
						// Compone la lista de celdas variables asociadas a la fila del cuadro de comparativas 
						mfv.setCeldas(getListaCeldas(cobertExpl, mv.getListaCabeceras()));				
						// Compone las listas de vinculaciones al objeto de la fila del cuadro de comparativas
						mfv.setListVinculaciones(getListaVinculaciones(cobertExpl));				
						listaFilas.add (mfv);
					}
				}
				break;
			}
		}
		
		mv.setListaFilas(listaFilas);
		return mv;
	}
	
	
	/**
	 * @param cobertura
	 * @return
	 */
	private List<ModuloFilaVinculacionView> getListaVinculaciones(Cobertura cobertura) {
		int grupoVinc = 0;
		// Compone la lista de vinculaciones de la fila
		List<ModuloFilaVinculacionView> listVinculaciones = new ArrayList<ModuloFilaVinculacionView>();
		
		// Recorre las vinculaciones de la fila y rellena las listas de vinculados
		for (VinculacionFila vf : cobertura.getVinculacionFilaArray()) {
			grupoVinc++;
	
			// Indica si la vinculacion aplica cuando el riesgo se ha elegido o cuando no 
			boolean elegida = "S".equals(vf.getElegida().toString());
			
			for (Fila fila : vf.getFilaArray()) {
				ModuloFilaVinculacionView mfvv = new ModuloFilaVinculacionView();
				mfvv.setGrupoVinculacion(grupoVinc);
				mfvv.setElegida(elegida);
				mfvv.setVincFila(fila.getFila());
				mfvv.setVincElegida("S".equals(fila.getElegida().toString()));
				listVinculaciones.add(mfvv);
			}
			
			
		}
		return listVinculaciones;
	}
	
	/* Pet. 57622 - REQ.01 * FIN */

	/**
	 * @param cobertura
	 * @param mfv
	 */
	private ModuloFilaView getModuloFilaView(final Cobertura cobertura, final List<String> lstCabVariables, final boolean esObjetoAseg) {
		
		ModuloFilaView mfv = null;
		boolean createMfv = false;
		
		if (esObjetoAseg) {
			if (Constants.CHARACTER_S.equals(cobertura.getElegible().toString().charAt(0))) {
				createMfv = true;
			} else {
				for (DatoVariable dv : cobertura.getDatoVariableArray()) {
					if (ELEGIBLE.equals(dv.getTipoValor()) && lstCabVariables.contains(dv.getNombre())) {
						createMfv = true;
						break;
					}
				}
			}
		} else {
			createMfv = true;
		}
		
		if (createMfv) {
			mfv = new ModuloFilaView();
			
			// Compone la lista de celdas fijas asociadas a la fila del cuadro de comparativas (CPM y RC)
			mfv.setConceptoPrincipalModulo(cobertura.getDescripcionCPM());
			mfv.setRiesgoCubierto(cobertura.getDescripcionRC());
			mfv.setCodConceptoPrincipalModulo(new BigDecimal (cobertura.getConceptoPrincipalModulo()));
			mfv.setCodRiesgoCubierto(new BigDecimal (cobertura.getRiesgoCubierto()));
			mfv.setRcElegible(Constants.CHARACTER_S.equals(cobertura.getElegible().toString().charAt(0)));
			mfv.setBasica(BASICA.equals(cobertura.getTipoCobertura()));		
			mfv.setFilamodulo(new BigDecimal (cobertura.getFila()));
			mfv.setCodCptoRCE(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
			mfv.setFilaComparativa(new BigDecimal (1));
		}
		return mfv;
	}


	/**
	 * @param cobertura
	 * @return
	 */
	private List<ModuloCeldaView> getListaCeldas(Cobertura cobertura, List<String> lstCabVariables) {
		List<ModuloCeldaView> celdas = new ArrayList<ModuloCeldaView>();
		boolean existe = false;
		for (String dvCab : lstCabVariables) {
			existe = false;
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {
				if (dvCab.equals(dv.getNombre())){
					existe = true;
					ModuloCeldaView mcv = new ModuloCeldaView();
					
					// Codigo de concepto
					mcv.setCodconcepto(new BigDecimal(dv.getCodigoConcepto()));

					// Este valor se corresponde con la fila comparativa de la tabla 
					mcv.setColumna(new BigDecimal(dv.getColumna())); 					
					
					// Indica si la celda tiene valor/es elegible/s (E - Si, otro valor - No)
					mcv.setElegible(ELEGIBLE.equals(dv.getTipoValor()));
					
					// Valor/es
					List<ModuloValorCeldaView> valores = new ArrayList<ModuloValorCeldaView>();
					for (Valor valor : dv.getValorArray()) {
						ModuloValorCeldaView mvcv = new ModuloValorCeldaView(valor.getValor(), valor.getDescripcion());
						/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Inicio */
						/* Anadimos las Vinculaciones de las celdas en caso de que las tenga */
						VinculacionCelda vc = valor.getVinculacionCelda();						
						if (vc != null) {
							/* El codigo de la columna Madre de la que esta vinculada */
							BigDecimal columnaVinculada = new BigDecimal(valor.getVinculacionCelda().getColumnaMadre());
							mvcv.setColumnaVinculada(columnaVinculada);
							
							/* El codigo de la fila madre de la que esta vinculada */
							BigDecimal filaVinculada = new BigDecimal(valor.getVinculacionCelda().getFilaMadre());
							mvcv.setFilaVinculada(filaVinculada);
							
							/* El valor de la Fila Madre al que esta vinculado*/
							BigDecimal filaSelec = new BigDecimal (valor.getVinculacionCelda().getValorMadre());
							mvcv.setFila(filaSelec);
							
						} 
						/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Fin */
						
						valores.add(mvcv);
					}
					Collections.sort(valores, new Comparator<ModuloValorCeldaView>() {
						@Override
						public int compare(ModuloValorCeldaView arg0, ModuloValorCeldaView arg1) {
							return arg0.getDescripcion().compareToIgnoreCase(arg1.getDescripcion());
						}
					});
					mcv.setValores(valores);
					
					// Observaciones
					if (!"".equals(StringUtils.nullToString(dv.getObservaciones()))) {
						mcv.setObservaciones(dv.getObservaciones());
					}
					celdas.add(mcv);
				}
			}
			if (!existe){ // pintar una celda en blanco
				logger.debug("celda en blanco");
				ModuloCeldaView mcv = new ModuloCeldaView();
				mcv.setObservaciones("");
				celdas.add(mcv);
			}
		}
		return celdas;
	}

	/**
	 * @param cobertura
	 * @return
	 */
	private List<String> getListaVinculacionesAgri(final ModuloPoliza mp, Cobertura[] coberturaArray, int numComparativa) {
		String codModulo = mp.getId().getCodmodulo();
		
		logger.debug("SeleccionComparativasSWManager - getListaVinculacionesAgri");
				
		List<String> listVinculacionesAgri = new ArrayList<String>();
		
		for (Cobertura cobertura : coberturaArray) {
			
			String codConceptoPpalMod = String.valueOf(cobertura.getConceptoPrincipalModulo());
			String riesgoCubierto = String.valueOf(cobertura.getRiesgoCubierto());
			
			// Recorre las vinculaciones de la fila y rellena las listas de vinculados
			for (DatoVariable dv : cobertura.getDatoVariableArray()) {				
				String campoVinculado = codModulo + "_" + String.valueOf(numComparativa) + "_" + cobertura.getFila() + "_" + codConceptoPpalMod + "_" + riesgoCubierto + "_" + dv.getCodigoConcepto() + "_" + dv.getColumna();
				String valores = "";
				for (Valor val :dv.getValorArray()) {					
					VinculacionCelda vc = val.getVinculacionCelda();				
					if (vc != null) {						
						String valorMadre = vc.getValorMadre();
						String valorSeleccion = val.getValor();
						valores += valorMadre + ":" + valorSeleccion + "|";
					}
				}	
				
				if (valores.length() > 0) {
					valores = valores.substring(0, valores.lastIndexOf("|"));
				}
				
				/* Quitamos los posibles espacios en blanco */
				campoVinculado.trim();
				valores.trim();
				
				if (!StringUtils.isNullOrEmpty(valores)) {
					listVinculacionesAgri.add(campoVinculado + "#" + valores);
				}
			}	
		}		
			
		return listVinculacionesAgri;
	}
	
	private Map<String, List<String>> getListaCamposBloqueadosporVinc(final ModuloPoliza mp, Cobertura[] coberturaArray,
			int numComparativa) {
		String codModulo = mp.getId().getCodmodulo();
		logger.debug("SeleccionComparativasSWManager - getListaCamposBloqueadosporVinc, Modulo: " + codModulo);
		Map<String, List<String>> listCamposVinculados = new HashMap<String, List<String>>();
		for (Cobertura cobertura : coberturaArray) {
			DatoVariable[] dvArr = cobertura.getDatoVariableArray();
			for (DatoVariable dv : dvArr) {
				Valor[] valArr = dv.getValorArray();
				valFor: for (Valor val : valArr) {
					VinculacionCelda vc = val.getVinculacionCelda();
					if (vc != null) {
						String campoPadre = "";
						// Localizamos el padre por Fila/Columna
						cobPadreFor: for (Cobertura cobPadre : coberturaArray) {
							if (cobPadre.getFila() == vc.getFilaMadre()) {
								DatoVariable[] dvPadreArr = cobPadre.getDatoVariableArray();
								for (DatoVariable dvPadre : dvPadreArr) {
									if (vc.getColumnaMadre() == dvPadre.getColumna()) {
										campoPadre = codModulo + "_" + numComparativa + "_" + cobPadre.getFila() + "_"
												+ cobPadre.getConceptoPrincipalModulo() + "_"
												+ cobPadre.getRiesgoCubierto() + "_" + dvPadre.getCodigoConcepto() + "_"
												+ dvPadre.getColumna();
										break cobPadreFor;
									}
								}
							}
						}
						if (!StringUtils.isNullOrEmpty(campoPadre)) {
							if (!listCamposVinculados.containsKey(campoPadre)) {
								listCamposVinculados.put(campoPadre, new ArrayList<String>());
							}
							String campoVinculado = codModulo + "_" + numComparativa + "_" + cobertura.getFila() + "_"
									+ cobertura.getConceptoPrincipalModulo() + "_" + cobertura.getRiesgoCubierto() + "_"
									+ dv.getCodigoConcepto() + "_" + dv.getColumna();
							listCamposVinculados.get(campoPadre).add(campoVinculado);
							break valFor;
						}
					}
				}
			}
		}
		return listCamposVinculados;
	}
	
	
	/**
	 * Procesa el objeto 'Cobertura' recibido como parametro y devuelve una lista de valores que se corresponden con las cabeceras del
	 * cuadro de comparativas
	 * @param c
	 * @return
	 */
	public List<String> getCabecerasModulo (Cobertura c) {
		
		List<String> lstCabeceras = new ArrayList<String>();
		
		// Proces las cabeceras variables del objeto 'Cobertura'
		for (DatoVariable dv : c.getDatoVariableArray()) {
			if (!lstCabeceras.contains(dv.getNombre())) {
			lstCabeceras.add(dv.getNombre());
			}
		}
		
		return lstCabeceras;
	}
	
	
	/**
	 * Obtiene de BBDD la respuesta del SW de ModulosyCoberturas almacenados para la clave indicada por el parametro 'mp'
	 * @param mp Objeto que encapsula la clave correspondiente al idpoliza, plan/linea y modulo
	 * @return Objeto 'ModulosYCoberturas' que encapsula la respuesta del SW almacenado en BBDD
	 * @throws Exception 
	 */
	public ModulosYCoberturas getModulosYCoberturasBBDD (ModuloPoliza mp,Poliza p, String realPath) throws Exception {
		
		logger.debug("Obteniendo el XML de ModulosYCoberturas de BBDD para el modulo " + mp.getId().getCodmodulo() + " [INIT]");
		
		// Obtiene el xml recibido del SW de ayudas a la contratacion en la ultima llamada
		Clob respuesta = null;
		// Esto (mp.getModuloPolizaCoberturaSW()) siempre va a ser null siempre porque el campo Idmodulo ya no coincide con 
		// el campo idmodulo de la tabla tb_modulos_polizas ya que esta cada vez que 
		// se pasa por ahi se borra y se vuelve a crear por lo que este campo se queda 
		// descolgado. De momento como apanho nos traemos el ultimo xml por idpoliza y fecha

		respuesta = seleccionComparativaSWDao.getRespuestaModulosPolizaCoberturaSW(
				p.getIdpoliza(), mp.getId().getCodmodulo(), SeleccionComparativaSWDao.MODULOS_Y_COBERTURAS);
		
		if (respuesta != null){
			
			logger.debug(" Se obtiene respuesta de la consulta a BBDD");
		
			String xml = WSUtils.convertClob2String(respuesta);
			logger.debug(xml);
		
			// Convierte el xml recibido en un objeto ModulosYCoberturas
			return getMyCFromXml(xml);
		}else{
			
			logger.debug("No se ha obtenido respuesta");
			logger.debug("Valor de estado de la póliza:"+p.getEstadoPoliza().getIdestado());
			logger.debug("Valor de Línea de Ganado:"+p.getLinea().isLineaGanado());
			try {
				/* P0063482 ** MODIF TAM (23/08/2021) ** Inicio (Defecto 32) */
				if (p.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) && !p.getLinea().isLineaGanado()) {
					logger.debug("Entramos en el if");
					return getMyCPolizaAgricola(mp, p, realPath);
				/* P0063482 ** MODIF TAM (23/08/2021) ** Fin (Defecto 32) */					
				}else {
					return getMyCPoliza(mp,p, realPath);
				}
					
					
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * Obtiene el objeto ModulosYCoberturas que devuelve el SW de ayudas a la contratacion para la poliza asociada
	 * al anexo de modificacion en cuestion
	 * @param ModuloPoliza mp,Poliza p, String realPath
	 * @return
	 * @throws Exception 
	 */
	private ModulosYCoberturas getMyCPoliza(ModuloPoliza mp,Poliza p, String realPath) throws Exception {
		
		// Obtiene el xml asociado a la poliza
		String xml = null;
		try {
			/* ESC-15142 ** MODIF TAM (15.09.2021) Inicio */
			/* Al recuperar las coberturas contratadas, no se están recuperando todas por que en el xml de envío no se envían 
			 * los datos variables de la cobertura de la póliza. Nos creamos un nuevo método que genere el xml con esos datos.
			 */
			if (p.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) && p.getLinea().isLineaGanado()) {
				logger.debug("Dentro de poliza de Ganado y Definitiva");
				xml = WSUtils.generateXMLPolizaCoberturasContratadas(p, null,null,null, mp.getId().getCodmodulo(), polizaDao, cpmTipoCapitalDao);
			}else {
			/* ESC-15142 ** MODIF TAM (15.09.2021) Fin */				
				xml = WSUtils.generateXMLPolizaModulosCoberturas(p, null,null,null, mp.getId().getCodmodulo(), polizaDao);
			}

		
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el xml asociado a la poliza", e);
			throw e;
		}
		logger.debug("getMyCPoliza:xml de envio al SW Ayudas contratacion doCoberturasContratadas : "+ xml);
		//Llama al metodo de 'coberturasContratadas' del SW de Ayudas a la contratacion con el xml del anexo
		CoberturasContratadasResponse response = null;
		try {
			response = new ContratacionAyudasHelper().doCoberturasContratadas(xml, realPath);
		} catch (Exception e) {
			logger.error("Error al obtener las coberturas contratadas de la poliza a través de SW", e);
			throw e;
		}
		
		// Procesa la respuesta del servicio y genera el objeto utilizado para mostrar las comparativas para devolverlo
		String respuesta = null;
		try {
			respuesta = WSUtils.getStringResponse(response.getCoberturasContratadas());
			logger.debug("getMyCPoliza:xml de respuesta del SW Ayudas contratacion doCoberturasContratadas : "+respuesta);
		} catch (Exception e) {
			logger.error("Error al obtener el xml de respuesta del servicio", e);
			throw e;
		}
		return getMyCFromXml(respuesta);
	}
	
	/**
	 * Genera el xml asociado a la poliza, llama al metodo 'modulosCoberturas' del SW de Ayudas a la contratacion y almacena
	 * la comunicacion con el servicio en la BBDD
	 * @param mp Objeto 'ModuloPoliza' utilizado para obtener el modulo que se esta tratando y para almacenar la comunicacion con el SW en BBDD
	 * @param p Poliza de la cual se quieren obtener las comparativas
	 * @param realPath Ruta a los wsdl para las llamadas al SW
	 * @param usuario Usuario que inicia la accion
	 * @return Objeto 'ModulosYCoberturas' que encapsula la respuesta del SW 
	 * @throws Exception 
	 */
	public ModulosYCoberturas getModulosYCoberturasSW (ModuloPoliza mp, Poliza p, String realPath, Usuario usuario) throws Exception {
		
		logger.debug("Obteniendo el XML de ModulosYCoberturas del SW para el modulo " + mp.getId().getCodmodulo());
		
		// Obtiene el xml asociado a la poliza
		String xmlEnvio = null;
		try {
			xmlEnvio = WSUtils.generateXMLPolizaModulosCoberturas(p, null,null,null, mp.getId().getCodmodulo(), polizaDao);		
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el xml asociado a la poliza", e);
			throw e;
		}
		
		// Llama al SW de Ayudas a la contratacion para obtener el xml de ModulosYCoberturas
		String xmlRespuesta = null;
		try {
			ModulosCoberturasResponse response = new ContratacionAyudasHelper().doModulosCoberturas(xmlEnvio, realPath);
			xmlRespuesta = WSUtils.getStringResponse(response.getModulosCoberturas());
		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		}
		
		// Registra la comunicacion con el SW en BBDD
		try {
			registrarComunicacionSW(mp, usuario, xmlEnvio, xmlRespuesta, SeleccionComparativaSWDao.MODULOS_Y_COBERTURAS);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al registrar la comunicacion con el SW en BBDD", e);
			throw e;
		}
		
		// Convierte el xml recibido en un objeto ModulosYCoberturas
		return getMyCFromXml(xmlRespuesta);
	}

/**
 * Obtiene la ultima respuesta obtenida por el servicio de una poliza y modulo dado 
 * @param idpoliza
 * @return
 * @throws Exception
 */
	public ModulosYCoberturas getModulosYCoberturasBBDD (long idPoliza, String codModulo) throws Exception {
		
		logger.debug("Obteniendo el XML de ModulosYCoberturas de BBDD para el modulo " + codModulo);
		
		// Obtiene el xml recibido del SW de ayudas a la contratacion en la ultima llamada
		Clob respuesta = null;
		respuesta=seleccionComparativaSWDao.getRespuestaModulosPolizaCoberturaSW(idPoliza, codModulo, SeleccionComparativaSWDao.MODULOS_Y_COBERTURAS);
		ModulosYCoberturas myc=null;
		if (respuesta != null){			
			String xml = WSUtils.convertClob2String(respuesta);
			logger.debug(xml);		
			// Convierte el xml recibido en un objeto ModulosYCoberturas
			myc= getMyCFromXml(xml);
		}
		return myc;
	}

	public void registrarComunicacionSW(ModuloPoliza mp, Usuario usuario, String xmlEnvio, String xmlRespuesta,
			Integer operacion) throws DAOException {
		registrarComunicacionSW(mp.getId().getIdpoliza(), mp.getId().getLineaseguroid(), mp.getId().getCodmodulo(),
				mp.getId().getNumComparativa(), usuario, xmlEnvio, xmlRespuesta, operacion);
	}
	
	/**
	 * Crea el objeto ModuloPolizaCoberturaSW a partir de la clave recibida en el parametro 'mp' y la crear/actualiza en BBDD
	 * junto a los xml de envio y respuesta del SW
	 * @param mp Objeto que encapsula la clave correspondiente al idpoliza, plan/linea y modulo
	 * @param usuario Usuario que realiza la accion
	 * @param xmlEnvio XML que se envia al SW
	 * @param xmlRespuesta XML que se recibe del SW 
	 * @throws DAOException 
	 */
	public void registrarComunicacionSW(Long idpoliza, Long lineaseguroid, String codmodulo, Long numComparativa,
			Usuario usuario, String xmlEnvio, String xmlRespuesta, Integer operacion) throws DAOException {

		// Registrar la llamada al SW en la tabla TB_MODULOS_POLIZA_COBERTURA_SW
		ModuloPolizaCoberturaSW mpcsw;
		ModuloPolizaId id = new ModuloPolizaId();
		id.setCodmodulo(codmodulo);
		id.setIdpoliza(idpoliza);
		id.setLineaseguroid(lineaseguroid);
		id.setNumComparativa(numComparativa);
		mpcsw = (ModuloPolizaCoberturaSW) this.seleccionComparativaSWDao.get(ModuloPolizaCoberturaSW.class, id);
		// Si no hay registro en la tabla para la clave actual, se da de alta
		if (mpcsw == null) {
			mpcsw = new ModuloPolizaCoberturaSW();
			mpcsw.setId(id);
		}
		mpcsw.setEnvio(Hibernate.createClob(xmlEnvio));
		mpcsw.setRespuesta(Hibernate.createClob(xmlRespuesta));
		mpcsw.setUsuario(usuario.getCodusuario());
		mpcsw.setFecha(new Date());
		mpcsw.setOperacion(operacion);

		// Guarda el registro en BBDD
		polizaDao.saveOrUpdate(mpcsw);
	}
	
	/**
	 * Actualiza el estado de la poliza
	 * @author U029114 14/09/2017
	 * @param idPoliza
	 * @param idestado
	 * @throws DAOException
	 */
	public void actualizaEstadoPoliza(Long idpoliza, BigDecimal idestado) throws DAOException {

		try {
			polizaDao.actualizaEstadoPoliza(idpoliza, idestado);

		}catch (Exception ex) {
			logger.info("Se ha producido un error actualizando el campo idestado de la poliza: " + ex.getMessage());
			throw new DAOException("Se ha producido un error actualizando el campo idestado de la poliza", ex);
		}

	}
	
	
	/**
	 * Convierte el xml recibido en un objeto ModulosYCoberturas
	 * @param xml
	 * @return
	 */
	public ModulosYCoberturas getMyCFromXml(String xml) {
		
		ModulosYCoberturas myc = null;
		
		// Convierte el xml recibido en un objeto ModulosYCoberturas
		try {
			myc = ModulosYCoberturasDocument.Factory.parse(xml).getModulosYCoberturas();
		} 
		catch (Exception e) {
			logger.error("Ha ocurrido un error al parsar el xml a un objeto ModulosYCoberturas", e);
		}
		
		return myc;
	}
	
	/* Pet. 63485 ** MODIF TAM (15.07.2020) ** Inicio */
	/* Damos de alta los metodos necesarios para lanzar llamada al S.W de modulos y coberturas por REST
	 * para las agricolas 
	 */
	@Override 
	public Map<String, Object> generarListaComparativasAgri(long idpoliza, boolean backImportesOLectura, String realPath, Usuario usuario) {
		
		logger.debug("generarListaComparativasAgri [INIT]");

		// Mapa de objetos que se devuelven al controlador
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		// Carga la poliza y obtiene el listado de modulos asociados
		Poliza p = seleccionPolizaManager.getPolizaById(idpoliza);
		
		List<ModuloView> lstComparativasModulo = new ArrayList<ModuloView>();
		
		// Obtiene el max num de comparativas por modulo
		BigDecimal maxNumComparativas = null;
		try {
			maxNumComparativas = seleccionComparativaSWDao.getMaxNumComparativas();
		} catch (DAOException e) {
			logger.error("Error al recoger el max num Comparativas de BBDD", e);
			
		}
		
		String lstModulos="" ;
		
		try {
			// Por cada modulo asociado se obtiene el objeto 'ModulosYCoberturas' asociado y se crea el listado de coberturas a pintar en pantalla
			List <String> lstMods = new ArrayList<String>();

			ModulosYCoberturas myc = null;

			List<ModuloPoliza> mFinal = new ArrayList<ModuloPoliza>();
			mFinal.addAll(p.getModuloPolizas());
			
			Collections.sort(mFinal, new ModuloPolizaComparator());
			
			List<String> listaFinalcamposBloqVincAgri = new ArrayList<String>();
			List<String> listaFinalVinculacionesAgri = new ArrayList<String>();
			
			for (ModuloPoliza mp : mFinal) {
				
				String keyMod = mp.getId().getCodmodulo();
				
				if (!lstMods.contains(keyMod)) {
					
					lstMods.add(keyMod);
					
					if (backImportesOLectura) {
						myc = getModulosYCoberturasBBDD(mp, p, realPath);
					} else {
						myc = getModulosYCoberturasAgriSW(mp, p, realPath, usuario);
					}

					if (myc != null) {
						
						for (int i = 1; i <= maxNumComparativas.intValue(); i++) {
							
							lstComparativasModulo.add(getModuloViewFromModulosYCobertAgricolas(myc, mp, i, keyMod));
							
							/* Pet. 63485 ** MODIF TAM (20.07.2020) ** Inicio */
							Cobertura[] coberturaArray = myc.getModuloArray(0).getCoberturaArray();
							/* Obtenemos la lista de Vinculaciones para las agricolas y las pasamos por parametro */							
							List<String> listaVinculacionesAgri = getListaVinculacionesAgri(mp, coberturaArray, i);
							
							/* Obtenemos una lista con los campos que deben aparecer inicialmente bloqueados por tener vinculacion con otros campos */
							Map<String, List<String>> camposBloqVincAgri = getListaCamposBloqueadosporVinc(mp, coberturaArray,i);
							
							listaFinalVinculacionesAgri.addAll(listaVinculacionesAgri); 
							Set<String> keys = camposBloqVincAgri.keySet();
							for (String key : keys) {
								String bloqVincStr = key + "#";
								for (String aux : camposBloqVincAgri.get(key)) {
									bloqVincStr += aux + ":";
								}
								bloqVincStr = bloqVincStr.substring(0, bloqVincStr.lastIndexOf(":"));
								listaFinalcamposBloqVincAgri.add(bloqVincStr);
							}							
							/* Pet. 63485 ** MODIF TAM (20.07.2020) ** Fin */
						}
					}
					
					lstModulos = lstModulos + mp.getId().getCodmodulo() + ",";					
				}
			}
			
			mapa.put("listaVinculacionesAgri", listaFinalVinculacionesAgri);
			mapa.put("camposBloqVincAgri", listaFinalcamposBloqVincAgri);
			
			lstModulos = lstModulos.substring(0,lstModulos.length()-1);
		} 
		catch (Exception e) {
			logger.error("Error al obtener las comparativas de la poliza", e);
			
			// Si es un error controlado del SW se muestra la descripcion del mensaje en la pantalla
			if (e instanceof AgrException) {
				mapa.put("alerta", WSUtils.debugAgrException(e));
			}
			// Si no, se devuelve un error generico
			else {
				mapa.put("alerta", "Error al obtener las comparativas de la póliza");
			}
		}
		
		// inicializamos el idComparativa de BBDD
		List<String> lstcompVisibles = new ArrayList<String>();
		String compVisiblesBBDD ="";
		List<ComparativaPoliza> compFinal = new ArrayList<ComparativaPoliza>();
		
		if (null != p.getComparativaPolizas() && p.getComparativaPolizas().size()>0){
			Map<Long, Long> mapaIds = new HashMap<Long, Long>();
			compFinal.addAll(p.getComparativaPolizas());
			Collections.sort(compFinal, new ComparativaPolizaComparator());
			long numComparativa = 0;
			String codModuloAux = "";
			for (ComparativaPoliza cp : compFinal) {
				if (!codModuloAux.equals(cp.getId().getCodmodulo())) {
					numComparativa = 1;
					codModuloAux = cp.getId().getCodmodulo();
				}
				Long idCompTemp = cp.getId().getIdComparativa();
				if (!mapaIds.containsKey(idCompTemp)){
					mapaIds.put(idCompTemp, numComparativa++);
					String idKey = cp.getId().getCodmodulo() + "_" + mapaIds.get(idCompTemp);
					if (!lstcompVisibles.contains(idKey)){
						lstcompVisibles.add(idKey);
						compVisiblesBBDD = compVisiblesBBDD + idKey + ":";
					}					
				}
				cp.getId().setIdComparativa(mapaIds.get(idCompTemp));
			}
			compVisiblesBBDD = compVisiblesBBDD.substring(0,compVisiblesBBDD.length()-1);
		}
		// Se devuelve un mapa de objetos que contiene la poliza cargada de BD y la lista comparativas por modulo
		for (ModuloView mv : lstComparativasModulo) {
			logger.debug("[ESC-26336] ModuloView: " + mv);
		}
		mapa.put("listaMod", lstComparativasModulo);
		mapa.put("poliza", p);
		mapa.put("listaCoberturasElegidas", compFinal);
		mapa.put("modulos",lstModulos);
		mapa.put("compVisiblesBBDD", compVisiblesBBDD);
		
		
		// Obtiene el listado de tipos de asegurado de ganado si aplica para el plan/linea
		mapa.put("listaTipoAseguradoGanado", generarListaTipoAseguradoGanado(p.getLinea().getLineaseguroid()));
		mapa.put("maxComparativas", maxNumComparativas);
		
		logger.debug("generarListaComparativasAgri [END]");
		
		return mapa;
	}		
	
		
	/**
	 * Genera el xml asociado a la poliza, llama al metodo 'modulosCoberturas' del SW de Ayudas a la contratacion por REST y almacena
	 * la comunicacion con el servicio en la BBDD (tb_envios_sw_mod_y_coberturas)
	 * @param mp Objeto 'ModuloPoliza' utilizado para obtener el modulo que se esta tratando y para almacenar la comunicacion con el SW en BBDD
	 * @param p Poliza de la cual se quieren obtener las comparativas
	 * @param realPath Ruta a los wsdl para las llamadas al SW
	 * @param usuario Usuario que inicia la accion
	 * @return Objeto 'ModulosYCoberturas' que encapsula la respuesta del SW 
	 * @throws Exception 
	 */
	public ModulosYCoberturas getModulosYCoberturasAgriSW (ModuloPoliza mp, Poliza p, String realPath, Usuario usuario) throws Exception {
		
		logger.debug("Obteniendo el XML de ModulosYCoberturas del SW para el modulo " + mp.getId().getCodmodulo());
		
		// Obtiene el xml asociado a la poliza
		String xmlPoliza = null;
		try {
			xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturas(p, null,null,null, mp.getId().getCodmodulo(), polizaDao);		
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el xml asociado a la poliza", e);
			throw e;
		}
		
		// Llama al SW de Ayudas a la contratacion para obtener el xml de ModulosYCoberturas
		String xmlRespuesta = null;
		try {
		
			es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils.getModulosCoberturas(xmlPoliza);
			xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();
			
		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			xmlRespuesta = e.getMessage();
			throw e;
		} finally {
			// Registra la comunicacion con el SW en BBDD
			try {
				registrarComunicacionSW(mp, usuario, xmlPoliza, xmlRespuesta, SeleccionComparativaSWDao.MODULOS_Y_COBERTURAS);
			} catch (Exception e) {
				logger.error("Ha ocurrido un error al registrar la comunicacion con el SW en BBDD", e);
			}
		}
		
		// Convierte el xml recibido en un objeto ModulosYCoberturas
		return getMyCFromXml(xmlRespuesta);
	}
	
	/**
	 * Obtiene el objeto ModulosYCoberturas que devuelve el SW de ayudas a la contratacion para la poliza asociada
	 * al anexo de modificacion en cuestion
	 * @param ModuloPoliza mp,Poliza p, String realPath
	 * @return
	 * @throws Exception 
	 */
	public ModulosYCoberturas getMyCPolizaAgricola(ModuloPoliza mp,Poliza p, String realPath) throws Exception {
		
		logger.debug("SeleccionComparativasSWManager - getMyCPolizaAgricola");
		logger.debug("Lanzamos llamada por REST para recuperar los Modulos y Coberturas Contratadas ");
		
		// Obtiene el xml asociado a la poliza
		String xmlPoliza = null;
		try {
			/* ESC-12909 ** MODIF TAM (20.04.2021) Inicio*/
			/* Al recuperar las coberturas contratadas, no se están recuperando todas por que en el xml de envío no se envían 
			 * los datos variables de la cobertura de la póliza. Nos creamos un nuevo método que genere el xml con esos datos.
			 */
			xmlPoliza = WSUtils.generateXMLPolizaCoberturasContratadas(p, null,null,null, mp.getId().getCodmodulo(), polizaDao, cpmTipoCapitalDao);
			/* ESC-12909 ** MODIF TAM (20.04.2021) Fin */
			
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el xml asociado a la poliza", e);
			throw e;
		}
		
		/*** TATY ****/
		// Llama al SW de Ayudas a la contratacion por REST para obtener el xml de coberturascontratadas
		String xmlRespuesta = null;
		try {
			
			es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils.getCoberturasContratadas(xmlPoliza);
			xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();
			logger.debug("getMyCPoliza:xml de respuesta del SW Ayudas contratacion doCoberturasContratadas : "+xmlRespuesta);	
		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		}
		
		
		// Convierte el xml recibido en un objeto ModulosYCoberturas
		return getMyCFromXml(xmlRespuesta);

	}
	
	/**
	 * Metodo que devuelve un objeto FicheroMultContenido
	 * @param idfichero
	 * @return
	 * @throws BusinessException
	 */
	public Clob getxmlSWModyCobert(Long idpoliza, String codModulo) throws BusinessException{
		logger.debug("SeleccionComparativasSWManager - getxmlSWModyCobert [INIT]");
		
		Clob fichero = null;
		try {
			fichero = seleccionComparativaSWDao.getRespuestaModulosPolizaCoberturaSW(idpoliza, codModulo, SeleccionComparativaSWDao.MODULOS_Y_COBERTURAS);
			
		} catch (DAOException dao) {
			logger.debug("Se ha producido un error al recuperar el fichero  :" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el fichero ", dao);
		}
		logger.debug("SeleccionComparativasSWManager - getxmlSWModyCobert [END]");
		return fichero;
	}
	/* Pet. 63485 ** MODIF TAM (15.07.2020) ** Fin */
	
	
	
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (05.10.2020) ** Inicio */
	/**
	 * Procesa el objeto 'ModulosYCoberturas' recibido como parametro y genera un objeto 'ModuloView', que encapsula la informacion
	 * que se mostrara en pantalla de las coberturas de las explotaciones 
	 * @param myc
	 * @param mp
	 * @return
	 */
	public ModuloView getParcelasViewFromModulosYCoberturas (Poliza poliza, final ModulosYCoberturas myc) {
		
		logger.debug("** SeleccionComparativasSWManager - getParcelasViewFromModulosYCoberturas[INIT]");
		ModuloView mv = new ModuloView();
		
		// Si no existe el elemento 'Modulo' en el xml no continua el proceso
		if (myc.getParcelas() == null) {
			return mv;
		}
		
		Parcela[] ParcelaArray = myc.getParcelas().getParcelaArray();		
		// Recorre la lista de coberturas de la parcela		
		
		List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>();
		List<DatoVariable> lstCabVariables = new ArrayList<DatoVariable>();
		
		ConceptoCubiertoModuloFiltro filtroConcCbrto = new ConceptoCubiertoModuloFiltro(poliza.getLinea().getLineaseguroid(), poliza.getCodmodulo());
		@SuppressWarnings("unchecked")
		List<ConceptoCubiertoModulo> lstConcCbrtoMod = polizaDao.getObjects(filtroConcCbrto);		
		
		// buscamos max cabeceras de cada cobertura de cada Parcela.
		for (Parcela parcela : ParcelaArray) {
			for (TipoCapitalAgricola tipoCapital : parcela.getTipoCapitalArray()) {
				for (Cobertura cobertura : tipoCapital.getCoberturaArray()) {
					// recogemos los datosvariables con tipovalor a 'E'
					// o aquellos con tipovalor 'F' si la cobertura es elegible
					// y que esten en la lista de Conceptos Cubiertos.
					for (DatoVariable dv : cobertura.getDatoVariableArray()) {
						String codConceptoStr = String.valueOf(dv.getCodigoConcepto());
						Boolean conceptoVal = existInListConcepto(lstConcCbrtoMod, codConceptoStr);
						if ((ELEGIBLE.equals(dv.getTipoValor())
								|| (Constants.CHARACTER_S.equals(cobertura.getElegible().toString().charAt(0))
										&& "F".equals(dv.getTipoValor()))) && conceptoVal) {
							lstCabVariables.add(dv);
						}	
					}					
				}
			}
		}
		
		if (mv.getListaCabeceras().isEmpty()) {
			mv.setListaCabeceras(new ArrayList<String>());		
		}
		
		Collections.sort(lstCabVariables, new DatoVariableComparator());
		for (DatoVariable dvCab : lstCabVariables) {
			if (!mv.getListaCabeceras().contains(dvCab.getNombre())) {
				mv.getListaCabeceras().add(dvCab.getNombre());
			}
		}
		
		// buscamos max cabeceras de cada cobertura de cada Parcela.
		for (Parcela parcelaB : ParcelaArray) {
			for (TipoCapitalAgricola tipoCapitalB : parcelaB.getTipoCapitalArray()) {
				for (Cobertura coberParc : tipoCapitalB.getCoberturaArray()) {
					// Compone el objeto 'ModuloFilaView' que encapsula la informacion de una fila del cuadro de comparativas de la parcela
					ModuloFilaView mfv = getModuloFilaView(coberParc, mv.getListaCabeceras(), true);
					if (mfv != null) {
						// Compone la lista de celdas variables asociadas a la fila del cuadro de comparativas 
						mfv.setCeldas(getListaCeldas(coberParc, mv.getListaCabeceras()));			
						// Compone las listas de vinculaciones al objeto de la fila del cuadro de comparativas
						mfv.setListVinculaciones(getListaVinculaciones(coberParc));			
						listaFilas.add (mfv);
					}
				}	
			}
		}
		
		logger.debug("** SeleccionComparativasSWManager - getParcelasViewFromModulosYCoberturas[END]");
		mv.setListaFilas(listaFilas);
		return mv;
	}
	
	public boolean existInListConcepto(final List<ConceptoCubiertoModulo> listConceptosCubiertos, String codConcepto) {
		boolean result = false;
		if (listConceptosCubiertos.size() > 0 && codConcepto != null) {
			for (ConceptoCubiertoModulo conceptoCubierto : listConceptosCubiertos) {
				String codCto = conceptoCubierto.getDiccionarioDatos().getCodconcepto().toString();
				if (codCto.equals(codConcepto))
					result = true;
			}
		}
		return result;
	}
	/* Pet.50776_63485-Fase II ** MODIF TAM (05.10.2020) ** Fin */

	/**
	 * Setter para Spring
	 * @param seleccionPolizaManager
	 */
	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}


	/**
	 * Setter para Spring
	 * @param moduloManager
	 */
	public void setModuloManager(ModuloManager moduloManager) { 
		this.moduloManager = moduloManager; 
	}
	
	
	/**
	 * Setter para Spring 
	 * @param polizaDao
	 */
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	/**
	 * Setter para Spring
	 * @param seleccionComparativaSWDao
	 */
	public void setSeleccionComparativaSWDao(ISeleccionComparativaSWDao seleccionComparativaSWDao) {
		this.seleccionComparativaSWDao = seleccionComparativaSWDao;
	}

	public IPolizaDao getPolizaDao() {
		return polizaDao;
	}
	
	/* ESC-12909 ** MODIF TAM (19.04.2021) ** Inicio */
	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}
	/* ESC-12909 ** MODIF TAM (19.04.2021) ** Inicio */

	
}
