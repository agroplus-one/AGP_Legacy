package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ClaseMtoFilter;
import com.rsi.agp.core.jmesa.service.IClaseMtoService;
import com.rsi.agp.core.jmesa.sort.ClaseMtoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.config.IClaseMtoDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("deprecation")
public class ClaseMtoService implements IClaseMtoService {
	
	private IClaseMtoDao claseMtoDao;
	private ILineaDao lineaDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private String id;
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String CLASE = "CLASE";
	private final static String DESCRIPCION = "DESCRIPCION";
	private final static String MAXPOLIZAS = "MAXPOLIZAS";
	private final static String COMPROBARAAC = "COMPROBARAAC";
	private final static String RDTOHISTORICO="RDTOHISTORICO";
	private final static String COMPROBARRCE="COMPROBARRCE";
	
	private final static String CAMPO_CLASE = "clase";
	
	
	public Collection<Clase> getClaseMtoWithFilterAndSort(ClaseMtoFilter filter, ClaseMtoSort sort, int rowStart, int rowEnd,String descripcion) throws BusinessException {
		
		return claseMtoDao.getClaseMtoWithFilterAndSort(filter, sort, rowStart, rowEnd,descripcion);
	}
	
	public int getConsultaClaseMtoCountWithFilter(ClaseMtoFilter filter,String descripcion) {
		
		return claseMtoDao.getConsultaClaseMtoCountWithFilter(filter,descripcion);
	}
	
	public String getTablaClaseMto (HttpServletRequest request, HttpServletResponse response, Clase clase, String origenLlamada,String descripcion){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, clase, origenLlamada);

		// Configura el filtro y la ordenación, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade,descripcion);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());
		// DAA 25/01/2013    
		return html (tableFacade, origenLlamada);
		
	}
			
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, Clase clase, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade,origenLlamada);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaClaseMto_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaClaseMto_LIMIT"));
	    		}
    		}
    		else {
    			tableFacade.getLimit().getSortSet().addSort(new Sort (1, CAMPO_CLASE, Order.ASC));
    		}
    		
			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
			cargarFiltrosBusqueda(columnas, clase, tableFacade);
    	}                
    	request.getSession().setAttribute("consultaClaseMto_LIMIT",tableFacade.getLimit());
    	
        return tableFacade;
	}
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade,String origenLlamada) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(PLAN, "linea.codplan");
			columnas.put(LINEA, "linea.codlinea");
			columnas.put(CLASE, "clase");
			columnas.put(DESCRIPCION, "descripcion");
			columnas.put(MAXPOLIZAS, "maxpolizas");
			columnas.put(COMPROBARAAC, "comprobarAac");
			columnas.put(RDTOHISTORICO, "rdtoHistorico");
			columnas.put(COMPROBARRCE, "comprobarRce");
					
		}	
		if (!("cicloPoliza").equals(origenLlamada)){
		tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(PLAN), columnas.get(LINEA), 
				columnas.get(CLASE),columnas.get(DESCRIPCION),columnas.get(MAXPOLIZAS),
				columnas.get(COMPROBARAAC),columnas.get(RDTOHISTORICO),columnas.get(COMPROBARRCE));
		}else{
			tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(PLAN), columnas.get(LINEA), 
					columnas.get(CLASE),columnas.get(DESCRIPCION),columnas.get(MAXPOLIZAS),
					columnas.get(COMPROBARAAC), columnas.get(COMPROBARRCE));
		}
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param clase
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, Clase clase, TableFacade tableFacade) {
		// DAA 25/01/2013    
		// ID
		if (clase.getId() != null && FiltroUtils.noEstaVacio(clase.getId()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ID_STR), clase.getId().toString()));
		// PLAN
		if (clase.getLinea() != null && FiltroUtils.noEstaVacio (clase.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), clase.getLinea().getCodplan().toString()));
		// LINEA
		if (clase.getLinea() != null && FiltroUtils.noEstaVacio (clase.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), clase.getLinea().getCodlinea().toString()));
		// CLASE
		if (FiltroUtils.noEstaVacio (clase.getClase()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CLASE), clase.getClase().toString()));
		// DESCRIPCION
		if (FiltroUtils.noEstaVacio (clase.getDescripcion()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(DESCRIPCION), clase.getDescripcion()));
		// MAXPOLIZAS
		if (FiltroUtils.noEstaVacio (clase.getMaxpolizas()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(MAXPOLIZAS), clase.getMaxpolizas().toString()));
		// COMPROBAR AAC
		if (FiltroUtils.noEstaVacio (clase.getComprobarAac()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COMPROBARAAC), clase.getComprobarAac().toString()));
		//RDTOHISTORICO
		if (FiltroUtils.noEstaVacio (clase.getRdtoHistorico()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(RDTOHISTORICO), clase.getRdtoHistorico().toString()));
		// COMPROBAR RCE
		if (FiltroUtils.noEstaVacio (clase.getComprobarRce()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COMPROBARRCE), clase.getComprobarRce().toString()));
				
	}
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de la clase y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,String descripcion){
        
		// Obtiene el Filter para la búsqueda de errores
		Limit limit = tableFacade.getLimit();
		ClaseMtoFilter consultaFilter = getConsultaClaseMtoFilter(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getConsultaClaseMtoCountWithFilter(consultaFilter,descripcion);
        logger.debug("********** count filas para ClaseMto = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la búsqueda ClaseMto
        ClaseMtoSort consultaSort = getConsultaClaseMtoSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<Clase> items = new ArrayList<Clase>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getClaseMtoWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd,descripcion);
			logger.debug("********** list items para ClaseMto = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
        
    }
	
	/**
     * Método para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
	private String html(TableFacade tableFacade, String origenLlamada){
    	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuración de las columnas de la tabla    
    	configurarColumnas(table,origenLlamada);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        }else{
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	// DAA 25/01/2013    
        	table.getRow().getColumn(columnas.get(ClaseMtoService.ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(origenLlamada));
        	if (!("cicloPoliza").equals(origenLlamada))
        		table.getRow().getColumn(columnas.get(RDTOHISTORICO)).getCellRenderer().setCellEditor(getCellEditorRdtoHist());
        }
        
    	return tableFacade.render();
    }
    

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final String origenLlamada) {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount ) {
				Long id = (Long)new BasicCellEditor().getValue(item, "id", rowcount);
            	BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
            	BigDecimal codplan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	String nomlinea = (String)new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);
            	BigDecimal clase = (BigDecimal)new BasicCellEditor().getValue(item, "clase", rowcount);
            	String descripcion = (String)new BasicCellEditor().getValue(item, "descripcion", rowcount);
            	BigDecimal maxpolizas = (BigDecimal)new BasicCellEditor().getValue(item, "maxpolizas", rowcount);
            	Long lineaseguroid =(Long) new BasicCellEditor().getValue(item, "linea.lineaseguroid", rowcount);
            	Character comprobarAac = (Character)new BasicCellEditor().getValue(item, "comprobarAac", rowcount);
            	Long rdtoHistorico=null;
            	if (!("cicloPoliza").equals(origenLlamada)){
            		rdtoHistorico=(Long) new BasicCellEditor().getValue(item,"rdtoHistorico",rowcount);
            	}
            	Character comprobarRce = (Character)new BasicCellEditor().getValue(item, "comprobarRce", rowcount);
            	Long esLineaGanado = (Long) new BasicCellEditor().getValue(item, "linea.esLineaGanadoCount", rowcount);
            	
            	Date fechaInicioContratacion = (Date) new BasicCellEditor().getValue(item, "linea.fechaInicioContratacion", rowcount);
               
            	HtmlBuilder html = new HtmlBuilder();
            	// botón editar
            	if (!("cicloPoliza").equals(origenLlamada)){
            		html.a().href().quote().append("javascript:editar('"+id+"','"+codLinea+"','"+nomlinea+"','"+codplan+"','"+clase+"'," +
                			"'"+descripcion+"','"+maxpolizas+"','"+StringUtils.nullToString(comprobarAac)+"','"+StringUtils.nullToString(rdtoHistorico)+"','"+StringUtils.nullToString(comprobarRce)+"');").quote().close();
            	}else{
            			html.a().href().quote().append("javascript:editar('"+id+"','"+codLinea+"','"+nomlinea+"','"+codplan+"','"+clase+"'," +
                    			"'"+descripcion+"','"+maxpolizas+"','"+StringUtils.nullToString(comprobarAac)+"','"+StringUtils.nullToString(comprobarRce)+"');").quote().close();
               
            	}
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Clase\" title=\"Editar Clase\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
            	// DAA 25/01/2013    
            	if (("cicloPoliza").equals(origenLlamada)){
            		//boton cargar
            		html.a().href().quote().append("javascript:cargarClase('"+id+"');").quote().close();
	                html.append("<img src=\"jsp/img/displaytag/load.png\" alt=\"Cargar clase\" title=\"Cargar clase\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
            		
            	}else {
	                // botón borrar 
	            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
	                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Clase\" title=\"Borrar Clase\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
            	}    
            	
				// botón detalle 
            	html.a().href().quote().append("javascript:detalle('"+id+"','"+codLinea+"','"+codplan+"','"+lineaseguroid+"','"+clase+"','"+descripcion +"','"+esLineaGanado +"','"+fechaInicioContratacion +"');").quote().close();
           		html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Detalle Clase\" title=\"Detalle Clase\"/>");
                html.aEnd();
                html.append("&nbsp;");

                return html.toString();
            }
		};
	}
	
	
    /**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table,String origenLlamada) {
		
		// Acciones10
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "10%");

    	// 1 - Plan6
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "6%");
    	// 2- Línea8
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "8%");
    	// 3 - Clase6
    	configColumna(table, columnas.get(CLASE), "Clase", true, true, "8%");
    	// 4 - Descripcion60
    	configColumna(table, columnas.get(DESCRIPCION), "Descripcion", true, true, "32%");
    	// 5 - Maxpolizas12
    	configColumna(table, columnas.get(MAXPOLIZAS), "Max.P&oacutelizas", true, true, "8%");
    	// 6 - ComprobarAac10
    	configColumna(table, columnas.get(COMPROBARAAC), "<span id='imp' title=\"Comprobar Autorizaci&oacute;n a la Contrataci&oacute;n\">Comprobar Autorizaci&oacute;n</span>", true, true, "8%");
    	if (!("cicloPoliza").equals(origenLlamada)){
    		// 6 - rdto historico10
        	configColumna(table, columnas.get(RDTOHISTORICO), "Rdto. Hist.", true, true, "8%");
    	}
    	configColumna(table, columnas.get(COMPROBARRCE), "<span id='imp' title=\"Incluir Riesgo Cubierto Elegido\">Incl. R.C.E.</span>", true, true, "8%");
	}
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parámetro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
	 * @param filterable Indica si se podrá buscar por esa columna
	 * @param sortable Indica si se podrá ordenar por esa columna
	 * @param width Ancho de la columna
	 */
	private void configColumna (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
	   table.getRow().getColumn(idCol).setTitle(title);
       table.getRow().getColumn(idCol).setFilterable(filterable);
       table.getRow().getColumn(idCol).setSortable(sortable);
       table.getRow().getColumn(idCol).setWidth(width);
	}
    
	/**
	 * Crea y configura el Filter para la consulta de clases
	 * @param limit
	 * @return
	 */
	private ClaseMtoFilter getConsultaClaseMtoFilter(Limit limit) {
		ClaseMtoFilter consultaFilter = new ClaseMtoFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            consultaFilter.addFilter(property, value);
        }
        return consultaFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta 
	 * @param limit
	 * @return
	 */
	private ClaseMtoSort getConsultaClaseMtoSort(Limit limit) {

		ClaseMtoSort consultaSort = new ClaseMtoSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public Clase getClase(Long idClase)
	throws BusinessException {
		try {
			return (Clase) claseMtoDao.get(Clase.class, idClase);
		
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener la clase: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener la clase:",
					dao);
		}
	}
	
	public Map<String, Object> bajaClaseMto(Clase clase) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		try {
			//Validamos que la clase no este cargada por ningun usuario
			boolean isCargada =claseMtoDao.isCargadaClase(clase.getId());
			if (isCargada){
				parameters.put("alerta", bundle.getString("mensaje.clase.cargada.usuario.KO"));
				return parameters;
			}
			//borramos la clase y sus detalles
			claseMtoDao.delete(Clase.class, clase.getId());
			parameters.put("mensaje", bundle.getString("mensaje.clase.borrado.OK"));
			
			logger.debug("Clase borrada  = " + clase.getId());

		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar la Clase",ex);
		}
		return parameters;
	}
	
	/**
	 * Realiza las validaciones previas y modifica la clase pasada como parámetro
	 * @param Clase a modificar
	 * @return Mapa con los mensajes de alerta que se hayan producido
	 */
	public Map<String, Object> editaClaseMto(Clase clase) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			// Comprueba si existe el plan/línea indicado
			Linea linea = lineaDao.getLinea(clase.getLinea().getCodlinea(), clase.getLinea().getCodplan());
			if (linea == null) {
				// El plan/línea introducido es erróneo, muestra el mensaje de error y cancela el proceso
				parameters.put("alerta", bundle.getString("mensaje.clase.alta.lineaseguroid.KO"));	
				return parameters;
			} 
			
			// Comprueba si ya existe un registro con ese plan/línea y clase que no sea el propio registro a modificar
			if (existeClase(clase, true)){
				parameters.put("alerta", bundle.getString("mensaje.clase.existente.alta.KO"));
				return parameters;
			}
				
			clase.setLinea(linea);
			claseMtoDao.saveOrUpdate(clase);
			claseMtoDao.evict(clase);	
			logger.debug("Clase modificada con  id = " + clase.getId());
			
		} catch (Exception ex) {
			logger.debug("Error al editar la Clase", ex);
			parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
		}
		

		return parameters;
	}

	/**
	 * Realiza las validaciones previas y da de alta la clase pasada como parámetro
	 * @param Clase a dar de alta
	 * @return Mapa con los mensajes de alerta que se hayan producido
	 */
	public Map<String, Object> altaClaseMto(Clase clase) {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			// Comprueba si existe el plan/línea indicado
			Linea linea = lineaDao.getLinea(clase.getLinea().getCodlinea(), clase.getLinea().getCodplan());
			if (linea == null) {
				// El plan/línea introducido es erróneo, muestra el mensaje de error y cancela el proceso
				parameters.put("alerta", bundle.getString("mensaje.clase.alta.lineaseguroid.KO"));	
				return parameters;
			} 
			
			// Comprueba si ya existe un registro con ese plan/línea y clase
			if (existeClase(clase, false)){
				parameters.put("alerta", bundle.getString("mensaje.clase.existente.alta.KO"));
				return parameters;
			}
			
			// Incluye los datos necesarios en el objeto y lo inserta
			if (clase.getId() != null){
				clase.setId(null);				
			}
			clase.setLinea(linea);	
			claseMtoDao.saveOrUpdate(clase);
			claseMtoDao.evict(clase);
		} 
		catch (Exception ex) {
			logger.debug("Error al dar de alta la Clase", ex);
			parameters.put("alerta", bundle.getString("mensaje.clase.alta.KO"));	
		}
		
		return parameters;
	}

	
	/**
	 * Devuelve un boolean indicando si ya existe un registro con ese plan/línea y clase
	 * @param clase
	 * @return
	 */
	private boolean existeClase(final Clase clase, final boolean incluirId) {
		
		
		try {
			ClaseMtoFilter filtro = new ClaseMtoFilter(true);
			filtro.addFilter("linea.codlinea", clase.getLinea().getCodlinea());
			filtro.addFilter("linea.codplan", clase.getLinea().getCodplan());
			filtro.addFilter("clase", clase.getClase());
			// Si se indica que se incluya el id de la clase y éste no es nulo, se añade al filtro
			if (incluirId && clase.getId() != null) {
				filtro.addFilter("id", clase.getId());
			}
			
			return (claseMtoDao.getConsultaClaseMtoCountWithFilter(filtro,null) > 0);
		}
		catch (Exception e) {
			logger.debug("Error al comprobar si existe un registro de clase con ese código y plan/linea", e);
		}
		
		return false;
	}
	

	/**
	 * Devuelve un booleano indicando si el plan/línea indicado en el parámetro de tipo Clase existe. 
	 * @param clase 
	 * @return boolean
	 */
	public boolean existeLineaseguroid(Clase clase) throws BusinessException {
		
		boolean existe = false;
		// obtener el lineaseguroid asociado al plan y la linea para comprobar si existe
		try {
			Long lineaSeguroId = lineaDao.getLineaSeguroId(clase.getLinea().getCodlinea(), clase.getLinea().getCodplan());
			if(lineaSeguroId != null){
				existe = true;
			}
		} catch (Exception ex) {
			logger.debug("Error al Comprobar si existe el lineaSeguroId", ex);	
		}
		return existe;
	}


	public Map<String, Object> replicaPlanLineaClaseMto(Clase clase, String planDestinoReplica, String lineaDestinoReplica) {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			BigDecimal linea = new BigDecimal(lineaDestinoReplica);
			BigDecimal plan = new BigDecimal(planDestinoReplica);
			// obtener el lineaseguroid al que vamos a replicar
			Long lineaSeguroIdDestino = lineaDao.getLineaSeguroId(linea,plan );
			Long lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(clase.getLinea().getCodlinea(),clase.getLinea().getCodplan() );
			
			//DAA 17/12/2012 Si claseorigen=null replicaré todas las clases para ese plan linea
			BigDecimal claseOrigen = clase.getClase();
			
			if(lineaSeguroIdDestino != null && lineaSeguroIdOrigen != null){
				//comprobamos que no existan antes registros para el lineaSeguroIdDestino
				boolean existe = claseMtoDao.existeClaseReplica(lineaSeguroIdDestino, claseOrigen);
				if(existe){
					parameters.put("alerta", bundle.getString("mensaje.clase.lineaseguroid.replica.tieneregistros.KO"));
				}else{
					claseMtoDao.replicaPlanLinea(lineaSeguroIdDestino,lineaSeguroIdOrigen,claseOrigen);
					parameters.put("mensaje", bundle.getString("mensaje.clase.lineaseguroid.replica.OK"));
				}
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.lineaseguroid.replica.KO"));
			}
		
		} catch (Exception ex) {
			logger.debug("Error al replicar las Clases", ex);
			parameters.put("alerta", bundle.getString("mensaje.clase.lineaseguroid.replica.KO"));	
		}
		
		return parameters;
	}
	

	public void setId(String id) {
		this.id = id;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setClaseMtoDao(IClaseMtoDao claseMtoDao) {
		this.claseMtoDao = claseMtoDao;
	}
	
	/**
	 * Devuelve el objeto que muestra la columna 'Formato'
	 * @return
	 */
	private CellEditor getCellEditorRdtoHist() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				Long rdtoHist = null;
            	
				try {
					
					rdtoHist =(Long) new BasicCellEditor().getValue(item,"rdtoHistorico",rowcount);
				}catch (Exception e) {
					logger.debug("formato nulo");
				}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	
            	if(rdtoHist.equals(1L)){
            	html.append(StringUtils.nullToString("SI") + "&nbsp;");
            	}else{
            	html.append(StringUtils.nullToString("NO")+ "&nbsp;");	
            	}
            	return html.toString();
            }
		};
	}

}
