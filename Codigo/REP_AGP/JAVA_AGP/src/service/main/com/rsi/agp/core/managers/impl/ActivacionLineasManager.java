/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  16/07/2010  Ernesto Laura     Manager asociado al controlador ActivacionController 
* 											
*
**************************************************************************************************/

package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ModuloComparator;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.cpl.ModuloFiltro;
import com.rsi.agp.dao.filters.log.ActivacionFiltro;
import com.rsi.agp.dao.filters.log.HistImportacionesFiltro;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.models.commons.IRelacionTablasDao;
import com.rsi.agp.dao.models.log.IHistImportacionesDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.log.HistImportaciones;
import com.rsi.agp.dao.tables.log.ImportacionTabla;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ActivacionLineasManager implements IManager {
	private static final Log logger = LogFactory.getLog(PolizaManager.class);
	public final static String ESTADO_IMPORTADO = "Importado";
	public final static String ESTADO_INCOMPLETO = "Incompleto";
	public final static String ESTADO_ERROR = "Error";
	
	private ICommonDao commonDao;
	private IHistImportacionesDao histImportacionesDao;
	private IRelacionTablasDao relacionTablasXmlDao;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String, Object> cargaDatosBasicosActivacion(HttpServletRequest request, boolean inicio, HistImportacionesFiltro filtroPantalla, BigDecimal tipousuario) throws Exception
	{
		HashMap<String, Object> activacion = new HashMap<String, Object>();
		HashMap<String, Object> estados = new HashMap<String, Object>();
		
		//Cargar todos los planes, lineas, tipos y estados
		//PLANES y lineas
		List planes = commonDao.getPlanes();
		activacion.put("planes", planes);

		//ESTADOS
		estados.put("IMPORTADO", ActivacionLineasManager.ESTADO_IMPORTADO);
		estados.put("INCOMPLETO", ActivacionLineasManager.ESTADO_INCOMPLETO);
		estados.put("ERROR", ActivacionLineasManager.ESTADO_ERROR);
		activacion.put("estados", estados);
		activacion.put("coberturason", "NO");
		
		//Obtenemos el listado de líneas importadas
		LineasFiltro filtroLineas = new LineasFiltro();
		if (filtroPantalla != null)
		{
			//Si tenemos campos para filtrar, los añadimos al filtro
			if (filtroPantalla.getLinea()!=null)
			{			
				if (filtroPantalla.getLinea().getCodplan() != null){
					filtroLineas.setCodPlan(filtroPantalla.getLinea().getCodplan());												
				}
				if (filtroPantalla.getLinea().getCodlinea() != null){
					filtroLineas.setCodLinea(filtroPantalla.getLinea().getCodlinea());						
				}
				if (filtroPantalla.getLinea().getLineaseguroid() != null){
					filtroLineas.setLineaSeguroId(filtroPantalla.getLinea().getLineaseguroid());
				}
			}
			
			if (filtroPantalla.getEstado() != null && !filtroPantalla.getEstado().equalsIgnoreCase("")){
				filtroLineas.setEstado(filtroPantalla.getEstado());
			}
			
			if (filtroPantalla.getActivo() != null && !filtroPantalla.getActivo().equalsIgnoreCase("")){
				filtroLineas.setActivado(filtroPantalla.getActivo());
			}
			
			if (filtroPantalla.getFechaactivacion() != null){
				filtroLineas.setFxActivacion(filtroPantalla.getFechaactivacion());
			}				
		}
		//Obtenemos las lineas importadas
		List<Linea> lineas = histImportacionesDao.getObjects(filtroLineas);
		
		activacion.put("resultados", lineas);
		
		return activacion;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> consulta (HttpServletRequest request) throws Exception
	{
		String plan 	= new String();
		String linea 	= new String();
		String estado	= new String();
		String activado = new String();
		String fxActiv  = new String();
		
		HashMap<String, Object> activacion = new HashMap<String, Object>();
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		//La variable "volver" tendra valor "true" cuando se llame al controller desde detalleTablas
		String volver = StringUtils.nullToString(request.getParameter("volver"));
		List<String> filtroVentanaActivacion=new ArrayList<String>(5);
		
    	//Recogemos los parametros desde el objeto filtro de la sesion porque volvemos de la ventana de detalle
    	if (volver.equals("1")){
			//Recogemos de sesion la lista de parametros del filtro de busqueda
			filtroVentanaActivacion = (List<String>) request.getSession().getAttribute("filtroHisCondicionado");
    		plan		= filtroVentanaActivacion.get(0);
    		linea		= filtroVentanaActivacion.get(1);
			estado		= filtroVentanaActivacion.get(2);
			activado	= filtroVentanaActivacion.get(3);
			fxActiv		= filtroVentanaActivacion.get(4);
			
			//borramos de sesion la lista de parametros del filtro de busqueda
			request.getSession().removeAttribute("filtroVentanaActivacion");
    	}
    	else{
			//Recojemos los parametros introducidos
			plan     = StringUtils.nullToString(request.getParameter("sl_planes"));
			linea    = StringUtils.nullToString(request.getParameter("sl_lineas"));
			estado   = StringUtils.nullToString(request.getParameter("sl_estado"));
			activado = StringUtils.nullToString(request.getParameter("sl_activado"));
			fxActiv  = StringUtils.nullToString(request.getParameter("fechaActiv"));
			
			filtroVentanaActivacion.add(0, plan);
			filtroVentanaActivacion.add(1, linea);
			filtroVentanaActivacion.add(2, estado);
			filtroVentanaActivacion.add(3, activado);
			filtroVentanaActivacion.add(4, fxActiv);
			
			request.getSession().setAttribute("filtroHisCondicionado", filtroVentanaActivacion);
    	}
		HistImportacionesFiltro filtro = new HistImportacionesFiltro();
		Linea datosLinea = new Linea();
		
		if (!StringUtils.nullToString(plan).equals("")){
			datosLinea = new Linea();
			datosLinea.setCodplan(new BigDecimal(plan));
		}
		if (!StringUtils.nullToString(linea).equals("") && !linea.equalsIgnoreCase("smsAviso")){
			datosLinea.setLineaseguroid(new Long(linea));
		}
		filtro.setLinea(datosLinea);
		
		if (estado != null && !estado.equalsIgnoreCase("")){
			filtro.setEstado(estado.toUpperCase());
		}
		
		if (activado != null && !activado.equalsIgnoreCase("")){
			if (!activado.equalsIgnoreCase("")){
				filtro.setActivo(activado);
			}
		}
		
		if (fxActiv != null && !fxActiv.equalsIgnoreCase("")){
			Date fActiv = sdf.parse(fxActiv);
			filtro.setFechaactivacion(fActiv);
		}

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		activacion = cargaDatosBasicosActivacion(request, false, filtro, usuario.getTipousuario());
		
		activacion.put("filtro", filtro);
		return activacion;
	}
	
	public HistImportaciones consultaFilaPL (Long idImp)
	{
		HistImportaciones h = (HistImportaciones)histImportacionesDao.getObject(HistImportaciones.class, idImp);
		return h;
	}
	
	public String bloquearPL (String idHistorico) throws Exception
	{
		//HashMap<String, Object> activacion = new HashMap<String, Object>();
		String mensaje = "P/L bloqueado con éxito";
		Linea objetoActualizar = (Linea)histImportacionesDao.getObject(Linea.class, new Long(idHistorico));
		objetoActualizar.setActivo("BL");
		histImportacionesDao.saveOrUpdate(objetoActualizar);
		
		//activacion.put("mensaje", "P/L bloqueado con éxito");
		
		return mensaje;
	}
	
	public HashMap<String, Object>activarPL (String lineaSeguro, String fechaActivacion, Boolean forzarActivar) throws Exception
	{
		HashMap<String, Object> activacion = new HashMap<String, Object>();
		Long id;
		int resultado = 0;
		if (lineaSeguro!=null && !lineaSeguro.equalsIgnoreCase(""))
		{
			id = new Long(lineaSeguro);
			resultado = histImportacionesDao.compruebaRegistrosPL(id.intValue(), forzarActivar);
		}
		//resultado -1: Si alguna de las tablas importadas no tiene estado 'Importado'
		//resultado -2: Si existe alguna tabla del condicionado para este P/L que no tiene registros
		//resultado -3: Si no estan configuradas las pantallas configurables
		//resultado -4: Si alguna de las pantallas configurables no tiene datos variables configurados

		activacion.put("error", resultado);
		return activacion;
	}	
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> dameListaModulos(long lineasegid, String coberturas, BigDecimal tipousuario) {
		logger.debug("init - [metodo] dameListaModulos");
		
		HashMap<String, Object> listaModulos = new HashMap<String, Object>();
		List<Modulo> modulos = null;
		
		ModuloFiltro filtro = new ModuloFiltro(lineasegid);
		modulos = commonDao.getObjects(filtro);
		 
		Collections.sort(modulos, new ModuloComparator());
		
		listaModulos.put("plan", modulos.get(0).getLinea().getCodplan());
		listaModulos.put("lineaDesc", modulos.get(0).getLinea().getCodlinea()+" - "+modulos.get(0).getLinea().getNomlinea());
		listaModulos.put("idlinea", modulos.get(0).getLinea().getLineaseguroid());
		
		
		if(coberturas.equals("coberturas")){
		    listaModulos.put("volver", "coberturas");
		}

		listaModulos.put("listaModulos", modulos);
		
		//Tamara 28/12/2012 
		/*if(tipousuario.compareTo(new BigDecimal(0)) == 0){
			listaModulos.put("tipoMenu", "menuTaller");
		}else{
			listaModulos.put("tipoMenu", "menuGeneral");
		}*/

		logger.debug("end - [metodo] dameListaModulos");
		return listaModulos;
	}
	
	
	/**
	 * Método para consultar todas las tablas asociadas a una línea
	 * @param idLinea
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> consultaDetalleActivacion(String idLinea) throws Exception
	{				
		HashMap<String, Object> detalleHistorico = new HashMap<String, Object>();
		
		// a�adimos el filtro para el lineaseguroid indicado
		ActivacionFiltro filtro = new ActivacionFiltro();
		filtro.setLineaseguroid(idLinea);
		
		// DAA 17/09/2013	a�adimos el filtro para las tablas del condicionado general
		if(("").equals(idLinea)){
			List <BigDecimal> lstCodtablacondicionadoGen = relacionTablasXmlDao.getLstCodtablacondicionado(Constants.COND_GENERAL);
			filtro.setLstCodtabla(lstCodtablacondicionadoGen);
		}
		// Obtenemos todas las tablas y Eliminamos las repeticiones de tablas. Viene ordenado para podernos quedar con la primera y no tratar el resto
		List<ImportacionTabla> tablas = this.histImportacionesDao.getObjects(filtro);
		Map<BigDecimal, ImportacionTabla> tablasSinRep = new HashMap<BigDecimal, ImportacionTabla>();
		for (ImportacionTabla tab: tablas){
			if (!tablasSinRep.containsKey(tab.getId().getCodtablacondicionado())){
				tablasSinRep.put(tab.getId().getCodtablacondicionado(), tab);
			}
		}
		
		//Cargamos las tablas en una lista para pasarselas a la jsp
		List<ImportacionTabla> tablasDef = new ArrayList<ImportacionTabla>();
		for (BigDecimal codtab: tablasSinRep.keySet()){
			tablasDef.add(tablasSinRep.get(codtab));
		}
		
		detalleHistorico.put("listaDetalle", tablasDef);
		
		if(!("").equals(idLinea)){
			//Obtengo los datos de la última importación del plan/línea seleccionado
			HistImportacionesFiltro histFiltro = new HistImportacionesFiltro();
			Linea l = new Linea();
			l.setLineaseguroid(new Long(idLinea));
			histFiltro.setLinea(l);
			List<HistImportaciones> hist = this.histImportacionesDao.getObjects(histFiltro);
			
			detalleHistorico.put("historico", hist.get(0));
			
			//Cargamos la línea para saber la fecha de activación
			LineasFiltro filtroLineas = new LineasFiltro();
			filtroLineas.setLineaSeguroId(new Long(idLinea));
			//Obtenemos las lineas importadas
			List<Linea> lineas = histImportacionesDao.getObjects(filtroLineas);
			
			detalleHistorico.put("lineaActivacion", lineas.get(0));
			detalleHistorico.put("tablasGenerales", false);
			
		}else{
			detalleHistorico.put("historico", null);
			detalleHistorico.put("lineaActivacion", null);
			detalleHistorico.put("tablasGenerales", true);
		}
		
		
		return detalleHistorico;
	}
	
	public boolean mostrarCoberturas(final Long lineaseguroId) throws DAOException {
		return histImportacionesDao.mostrarCoberturas(lineaseguroId);
	}
	
	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}
	
	public void setHistImportacionesDao(IHistImportacionesDao histImportacionesDao) {
		this.histImportacionesDao = histImportacionesDao;
	}

	public void setRelacionTablasXmlDao(IRelacionTablasDao relacionTablasXmlDao) {
		this.relacionTablasXmlDao = relacionTablasXmlDao;
	}

}
