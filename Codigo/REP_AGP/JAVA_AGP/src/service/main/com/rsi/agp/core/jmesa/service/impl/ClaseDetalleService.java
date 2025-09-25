package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClaseDetalleFilter;
import com.rsi.agp.core.jmesa.service.IClaseDetalleService;
import com.rsi.agp.core.jmesa.sort.ClaseDetalleSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.admin.IComarcaDao;
import com.rsi.agp.dao.models.admin.IProvinciaDao;
import com.rsi.agp.dao.models.config.IClaseDetalleDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("deprecation")
public class ClaseDetalleService implements IClaseDetalleService {
	
	private IClaseDetalleDao claseDetalleDao;
	private IProvinciaDao provinciaDao;
	private IComarcaDao comarcaDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	
	private String id;
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String MODULO = "MODULO";
	private final static String CICLO_CULTIVO = "CICLO_CULTIVO";
	private final static String SISTEMA_CULTIVO = "SISTEMA_CULTIVO";
	private final static String CULTIVO = "CULTIVO";
	private final static String VARIEDAD = "VARIEDAD";
	private final static String PROVINCIA = "PROVINCIA";
	private final static String COMARCA = "COMARCA";
	private final static String TERMINO = "TERMINO";
	private final static String SUBTERMINO = "SUBTERMINO";
	
	private final static String TCAPITAL = "TCAPITAL";
	private final static String DES_T_CAPITAL = "tipoCapital.destipocapital";
	private final static String TPLANTACION = "TPLANTACION";
	private final static String DES_T_PLANTACION = "tipoPlantacion.destipoplantacion";
	
	
	@Override
	public Collection<ClaseDetalle> getClaseDetalleWithFilterAndSort(ClaseDetalleFilter filter, ClaseDetalleSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		
		return claseDetalleDao.getClaseDetalleWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	@Override
	public int getConsultaClaseDetalleCountWithFilter(ClaseDetalleFilter filter) {
		
		return claseDetalleDao.getConsultaClaseDetalleCountWithFilter(filter);
	}
	

	public String getTablaClaseDetalle (HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalle, String origenLlamada){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, claseDetalle,origenLlamada);

		Limit limit = tableFacade.getLimit();
		ClaseDetalleFilter consultaFilter = null;
		
		//DAA 11/02/2013
		if(!("true").equals(StringUtils.nullToString(request.getParameter("vieneDeConsultar"))) && 
				request.getSession().getAttribute("claseDetalleBusqueda") != null){
			
			consultaFilter = (ClaseDetalleFilter) request.getSession().getAttribute("claseDetalleBusqueda");
			
		}else{
			// Obtiene el Filter para la búsqueda de clasesDetalle y lo guarda en sesion
			consultaFilter = getConsultaClaseDetalleFilter(limit);
			request.getSession().setAttribute("claseDetalleBusqueda", consultaFilter);
		}

		// Configura el filtro y la ordenación, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, consultaFilter, limit);
		
		String listaIdsTodos = getlistaIdsTodos(consultaFilter);
		String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		
		if (!"cargaClases".equals(origenLlamada)){
			tableFacade.setToolbar(new CustomToolbarMarcarTodos());
			tableFacade.setView(new CustomView());
		}else{
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}

		return html (tableFacade,origenLlamada) + script;
		
	}
			
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalle,String origenLlamada) {
		Filter filterId = null;
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);	
		
		
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade,origenLlamada);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        
        if(claseDetalle.getClase().getId()!= null){
        	filterId = new Filter("clase.id", claseDetalle.getClase().getId()+"");
        }
        else{
        	filterId = new Filter("clase.id", request.getParameter("detalleid"));
        }
 
        
		tableFacade.getLimit().getFilterSet().addFilter(filterId);
        
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		//if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaClaseDetalle_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaClaseDetalle_LIMIT"));
	    		}
    		//}
    		//else{
    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			
    		//}
    	} 
    	cargarFiltrosBusqueda(columnas, claseDetalle, tableFacade);
        return tableFacade;
	}
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade,String origenLlamada) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(MODULO, "codmodulo");
			columnas.put(CICLO_CULTIVO, "cicloCultivo.codciclocultivo");
			columnas.put(SISTEMA_CULTIVO, "sistemaCultivo.codsistemacultivo");
			columnas.put(CULTIVO, "cultivo.id.codcultivo");
			columnas.put(VARIEDAD, "variedad.id.codvariedad");
			columnas.put(PROVINCIA, "codprovincia");
			columnas.put(COMARCA, "codcomarca");
			columnas.put(TERMINO, "codtermino");
			columnas.put(SUBTERMINO, "subtermino");
			
			columnas.put(TCAPITAL, "tipoCapital.codtipocapital");
			columnas.put(TPLANTACION, "tipoPlantacion.codtipoplantacion");
		}
		if (!origenLlamada.equals("cargaClases")){
			tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(MODULO), columnas.get(CICLO_CULTIVO), 
					columnas.get(SISTEMA_CULTIVO),columnas.get(CULTIVO),columnas.get(VARIEDAD),columnas.get(PROVINCIA),
					columnas.get(COMARCA),columnas.get(TERMINO),columnas.get(SUBTERMINO),columnas.get(TCAPITAL),
					columnas.get(TPLANTACION)); 
		}else{
			tableFacade.setColumnProperties(columnas.get(MODULO), columnas.get(CICLO_CULTIVO), 
					columnas.get(SISTEMA_CULTIVO),columnas.get(CULTIVO),columnas.get(VARIEDAD),columnas.get(PROVINCIA),
					columnas.get(COMARCA),columnas.get(TERMINO),columnas.get(SUBTERMINO),columnas.get(TCAPITAL),
					columnas.get(TPLANTACION)); 
		}
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param clase
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, ClaseDetalle claseDetalle, TableFacade tableFacade) {
		
		// MODULO
		if (!StringUtils.nullToString(claseDetalle.getCodmodulo()).equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(MODULO), claseDetalle.getCodmodulo()));
		// CICLO_CULTIVO
		if (claseDetalle.getCicloCultivo() != null && FiltroUtils.noEstaVacio (claseDetalle.getCicloCultivo().getCodciclocultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CICLO_CULTIVO), claseDetalle.getCicloCultivo().getCodciclocultivo().toString()));
		// SISTEMA_CULTIVO
		if (claseDetalle.getSistemaCultivo() != null && FiltroUtils.noEstaVacio (claseDetalle.getSistemaCultivo().getCodsistemacultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(SISTEMA_CULTIVO), claseDetalle.getSistemaCultivo().getCodsistemacultivo().toString()));
		// CULTIVO
		if (claseDetalle.getCultivo() != null && FiltroUtils.noEstaVacio (claseDetalle.getCultivo().getId().getCodcultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CULTIVO), claseDetalle.getCultivo().getId().getCodcultivo().toString()));
		// VARIEDAD
		if (claseDetalle.getVariedad() != null && FiltroUtils.noEstaVacio (claseDetalle.getVariedad().getId().getCodvariedad()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(VARIEDAD), claseDetalle.getVariedad().getId().getCodvariedad().toString()));
		// PROVINCIA
		if (FiltroUtils.noEstaVacio (claseDetalle.getCodprovincia()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PROVINCIA), claseDetalle.getCodprovincia().toString()));
		// COMARCA
		if (FiltroUtils.noEstaVacio (claseDetalle.getCodcomarca()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COMARCA), claseDetalle.getCodcomarca().toString()));
		// TERMINO
		if (FiltroUtils.noEstaVacio (claseDetalle.getCodtermino()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TERMINO), claseDetalle.getCodtermino().toString()));
		// SUBTERMINO
		if (FiltroUtils.noEstaVacio (claseDetalle.getSubtermino()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(SUBTERMINO), claseDetalle.getSubtermino().toString()));
		// TIPO CAPITAL
		if (claseDetalle.getTipoCapital() != null && FiltroUtils.noEstaVacio (claseDetalle.getTipoCapital().getCodtipocapital()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TCAPITAL), claseDetalle.getTipoCapital().getCodtipocapital().toString()));
		// TIPO PLANTACIÃ“N
		if (claseDetalle.getTipoPlantacion() != null && FiltroUtils.noEstaVacio (claseDetalle.getTipoPlantacion().getCodtipoplantacion()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TPLANTACION), claseDetalle.getTipoPlantacion().getCodtipoplantacion().toString()));
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de la clase y carga el TableFacade con ellas
	 * @param tableFacade
	 * @param consultaFilter 
	 * @param limit 
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, ClaseDetalleFilter consultaFilter, Limit limit){
        
		// Obtiene el número de filas que cumplen el filtro        
        int totalRows = getConsultaClaseDetalleCountWithFilter(consultaFilter);
        logger.debug("********** count filas para ClaseDetalle = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la búsqueda ClaseDetalle
        ClaseDetalleSort consultaSort = getConsultaClaseDetalleSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<ClaseDetalle> items = new ArrayList<ClaseDetalle>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getClaseDetalleWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para ClaseDetalle = "+items.size()+" **********");
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
	private String html(TableFacade tableFacade,String origenLlamada){
    	
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
        	if (!origenLlamada.equals("cargaClases")){
	        	// campo acciones
	        	table.getRow().getColumn(columnas.get(ClaseDetalleService.ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	}
        	// Subtermino
        	table.getRow().getColumn(columnas.get(ClaseDetalleService.SUBTERMINO)).getCellRenderer().setCellEditor(getCellEditorSubtermino());
        	
        }
        
    	return tableFacade.render();
    }
    

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
				Long id = (Long)new BasicCellEditor().getValue(item, "id", rowcount);
				String codModulo = (String)new BasicCellEditor().getValue(item, "codmodulo", rowcount);
				BigDecimal codCicloCultivo = (BigDecimal)new BasicCellEditor().getValue(item, "cicloCultivo.codciclocultivo", rowcount);
				String desCicloCultivo = (String)new BasicCellEditor().getValue(item, "cicloCultivo.desciclocultivo", rowcount);
				BigDecimal codSistCult = (BigDecimal)new BasicCellEditor().getValue(item, "sistemaCultivo.codsistemacultivo", rowcount);
				String desSistCult = (String)new BasicCellEditor().getValue(item, "sistemaCultivo.dessistemacultivo", rowcount);
				BigDecimal codCultivo = (BigDecimal)new BasicCellEditor().getValue(item, "cultivo.id.codcultivo", rowcount);
				String desCultivo = (String)new BasicCellEditor().getValue(item, "cultivo.descultivo", rowcount);
				BigDecimal codVariedad = (BigDecimal)new BasicCellEditor().getValue(item, "variedad.id.codvariedad", rowcount);
				String desVariedad = (String)new BasicCellEditor().getValue(item, "variedad.desvariedad", rowcount);
				BigDecimal codProvincia = (BigDecimal)new BasicCellEditor().getValue(item, "codprovincia", rowcount);
				BigDecimal codComarca = (BigDecimal)new BasicCellEditor().getValue(item, "codcomarca", rowcount);
				BigDecimal codTermino = (BigDecimal)new BasicCellEditor().getValue(item, "codtermino", rowcount);
				Character codSubtermino = (Character)new BasicCellEditor().getValue(item, "subtermino", rowcount);
            	
				BigDecimal codTipoCapital = (BigDecimal)new BasicCellEditor().getValue(item, "tipoCapital.codtipocapital", rowcount);
				String desTipoCapital = (String)new BasicCellEditor().getValue(item, ClaseDetalleService.DES_T_CAPITAL, rowcount);
				BigDecimal codTipoPlantacion = (BigDecimal)new BasicCellEditor().getValue(item, "tipoPlantacion.codtipoplantacion", rowcount);
				String desTipoPlantacion = (String)new BasicCellEditor().getValue(item, ClaseDetalleService.DES_T_PLANTACION, rowcount);
				
            	HtmlBuilder html = new HtmlBuilder();
            	
            	//DAA 05/02/2013 checkbox
            	html.append("<input type=\"checkbox\" id=\"check_" + id + "\"  name=\"check_" + id + "\" onClick =\"listaCheckId(\'" + id + "')\" class=\"dato\"/>");
                html.append("&nbsp;");
            	
            	// botón editar
            	html.a().href().quote().append("javascript:editar('"+id+"','"+codModulo+"','"+StringUtils.nullToString(codCicloCultivo)+"','"+StringUtils.nullToString(codSistCult)+"'," +
            			"'"+codCultivo+"','"+codVariedad+"','"+codProvincia+"','"+codComarca+"','"+codTermino+"','"+codSubtermino+"'" +
            					",'"+StringUtils.nullToString(desCicloCultivo)+"','"+StringUtils.nullToString(desSistCult)+"','"+StringUtils.nullToString(desCultivo)+"'" +
            					",'"+StringUtils.nullToString(desVariedad)+"','"+StringUtils.nullToString(codTipoCapital)+"','"+StringUtils.nullToString(desTipoCapital)+"'" +
            					",'"+StringUtils.nullToString(codTipoPlantacion)+"','"+StringUtils.nullToString(desTipoPlantacion)+"');").quote().close();
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Detalle\" title=\"Editar Detalle\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // botón borrar 
            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Detalle\" title=\"Borrar Detalle\"/>");
                html.aEnd();
                html.append("&nbsp;");
                	                	
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Subtérmino'
	 * @return
	 */
	private CellEditor getCellEditorSubtermino() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				Character codSubtermino = (Character)new BasicCellEditor().getValue(item, "subtermino", rowcount);
            	
            	HtmlBuilder html = new HtmlBuilder();
              	html.append((codSubtermino != null && (!new Character (' ').equals(codSubtermino))) ? codSubtermino : "&nbsp;");
            			
                	                	
            	return html.toString();
            }
		};
	}
	
	
    /**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table,String origenLlamada) {
		
		if (!origenLlamada.equals("cargaClases")){
			// Acciones
	    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "10%");
		}
    	// 1 - MODULO
    	configColumna(table, columnas.get(MODULO), "M&oacute;dulo", true, true, "8%");
    	// 2 - CICLO_CULTIVO
    	configColumna(table, columnas.get(CICLO_CULTIVO), "Ciclo Cult.", true, true, "8%");
    	// 3 - SISTEMA_CULTIVO
    	configColumna(table, columnas.get(SISTEMA_CULTIVO), "Sist. Cultivo", true, true, "8%");
    	// 4 - CULTIVO
    	configColumna(table, columnas.get(CULTIVO), "Cultivo", true, true, "8%");
    	// 5 - VARIEDAD
    	configColumna(table, columnas.get(VARIEDAD), "Variedad", true, true, "8%");
    	// 6 - PROVINCIA
    	configColumna(table, columnas.get(PROVINCIA), "Provincia", true, true, "8%");
    	// 7 - COMARCA
    	configColumna(table, columnas.get(COMARCA), "Comarca", true, true, "8%");
    	// 8 - TERMINO
    	configColumna(table, columnas.get(TERMINO), "T&eacute;rmino", true, true, "8%");
    	// 9 - SUBTERMINO
    	configColumna(table, columnas.get(SUBTERMINO), "Subtermino", true, true, "9%");
    	// 10 - TCAPITAL
    	configColumna(table, columnas.get(TCAPITAL), "Tipo Capital", true, true, "7%");
    	// 11 - TPLANTACION
    	configColumna(table, columnas.get(TPLANTACION), "Tipo Plantaci&oacute;n", true, true, "10%");
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
	private ClaseDetalleFilter getConsultaClaseDetalleFilter(Limit limit) {
		ClaseDetalleFilter consultaFilter = new ClaseDetalleFilter();
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
	private ClaseDetalleSort getConsultaClaseDetalleSort(Limit limit) {

		ClaseDetalleSort consultaSort = new ClaseDetalleSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public ClaseDetalle getClaseDetalle(Long idClaseDetalle)
	throws BusinessException {
		try {
			return (ClaseDetalle) claseDetalleDao.get(ClaseDetalle.class, idClaseDetalle);
		
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener la clase: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener la clase:",
					dao);
		}
	}
	
	public void bajaClaseDetalle(ClaseDetalle claseDetalle) throws BusinessException {
		try {
			claseDetalleDao.delete(ClaseDetalle.class, claseDetalle.getId());
			logger.debug("ClaseDetalle borrada  = " + claseDetalle.getId());
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar la ClaseDetalle",ex);
		}
	}
	
	public Map<String, Object> insertOrUpdateClaseDetalle(ClaseDetalle claseDetalle, Long lineaseguroid) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {

			// Validación de modulo
			Modulo mod = claseDetalleDao.getModulo(lineaseguroid, claseDetalle.getCodmodulo());
			if (mod == null) {
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.modulo.KO"));
				return parameters;
			}
			claseDetalle.setCodmodulo(mod.getId().getCodmodulo());
			
			//validacion de cultivo
			Cultivo cul = claseDetalleDao.getCultivo(lineaseguroid, claseDetalle.getCultivo().getId().getCodcultivo());
			if (cul == null) {
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.cultivo.KO"));
				return parameters;
			}
			claseDetalle.setCultivo(cul);
			
			// Validación de variedad
			Variedad var = claseDetalleDao.getVariedad(lineaseguroid, claseDetalle.getCultivo().getId().getCodcultivo(),claseDetalle.getVariedad().getId().getCodvariedad());
			if (var == null) {
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.variedad.KO"));
				return parameters;
			}
			claseDetalle.setVariedad(var);	
			
			// Validación del ciclo de cultivo
			// Solo se valida cuando el usuario a metido un valor, si no, no es obligatorio
			if (null != claseDetalle.getCicloCultivo().getCodciclocultivo()){
				CicloCultivo ccul = claseDetalleDao.getCicloCultivo(claseDetalle.getCicloCultivo().getCodciclocultivo());
				if (ccul == null) {
					parameters.put("alerta", bundle.getString("mensaje.clase.detalle.ciclocultivo.KO"));
					return parameters;
				}
				claseDetalle.setCicloCultivo(ccul);
			}
			else{
				claseDetalle.setCicloCultivo(null);
			}
			
			// Validación del sistema de cultivo
			// Solo se valida cuando el usuario a metido un valor, si no, no es obligatorio
			if (claseDetalle.getSistemaCultivo() != null && claseDetalle.getSistemaCultivo().getCodsistemacultivo() != null){
				SistemaCultivo scul = claseDetalleDao.getSistemaCultivo(claseDetalle.getSistemaCultivo().getCodsistemacultivo());
				if (scul == null) {
					parameters.put("alerta", bundle.getString("mensaje.clase.detalle.sistemacultivo.KO"));
					return parameters;
				}
				claseDetalle.setSistemaCultivo(scul);
			}
			else{
				claseDetalle.setSistemaCultivo(null);
			}
			
			// Validación del tipo de Capital
			// Solo se valida cuando el usuario a metido un valor, si no, no es obligatorio
			if (claseDetalle.getTipoCapital() != null && claseDetalle.getTipoCapital().getCodtipocapital() != null){
				 TipoCapital tCap = claseDetalleDao.getTipoCapital(claseDetalle.getTipoCapital().getCodtipocapital());
				if (tCap == null) {
					parameters.put("alerta", bundle.getString("mensaje.clase.detalle.tipocapital.KO"));
					return parameters;
				}
				claseDetalle.setTipoCapital(tCap);
			}
			else{
				claseDetalle.setTipoCapital(null);
			}
			
			// Validación de la provincia
			if (!isProvinciaValida(claseDetalle.getCodprovincia())) {
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.provincia.KO"));
				return parameters;
			}
			
			// Validación de la comarca
			if (!isComarcaValida(claseDetalle.getCodprovincia(), claseDetalle.getCodcomarca())) {
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.comarca.KO"));
				return parameters;
			}
			
			// Validación del tipo de Plantación
			// Solo se valida cuando el usuario a metido un valor, si no, no es obligatorio
			if (claseDetalle.getTipoPlantacion() != null && claseDetalle.getTipoPlantacion().getCodtipoplantacion() != null){
				TipoPlantacion tPlant = claseDetalleDao.getTipoPlantacion(claseDetalle.getTipoPlantacion().getCodtipoplantacion());
				if (tPlant == null) {
					parameters.put("alerta", bundle.getString("mensaje.clase.detalle.tipoplantacion.KO"));
					return parameters;
				}
				claseDetalle.setTipoPlantacion(tPlant);
			}
			else{
				claseDetalle.setTipoPlantacion(null);
			}
			
			// MPM - Introduce el lineaseguroid en el objeto claseDetalle para la validación de duplicados
			if (claseDetalle.getClase().getLinea() == null) {
				claseDetalle.getClase().setLinea(new Linea());
			}
			claseDetalle.getClase().getLinea().setLineaseguroid(lineaseguroid);

			if (claseDetalleDao.existeClaseDetalle(claseDetalle)){
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.existe.KO"));
			}else{
				claseDetalleDao.saveOrUpdate(claseDetalle);
				claseDetalleDao.evict(claseDetalle);
			}
		} catch (Exception ex) {
			logger.debug("Error al dar de alta la Clase", ex);
		} finally {
			if (claseDetalle.getSistemaCultivo() == null) claseDetalle.setSistemaCultivo(new SistemaCultivo());
			if (claseDetalle.getCicloCultivo() == null)	claseDetalle.setCicloCultivo(new CicloCultivo());
			if (claseDetalle.getTipoCapital() == null)	claseDetalle.setTipoCapital(new TipoCapital());
			if (claseDetalle.getTipoPlantacion() == null)	claseDetalle.setTipoPlantacion(new TipoPlantacion());
		}
		
		return parameters;
	}

	/** DAA 06/02/2013  Metodo para recuperar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param consultaFilter
	 * @return listaIdsTodos
	 */
	public String getlistaIdsTodos(ClaseDetalleFilter consultaFilter) {
		String listaIdsTodos =claseDetalleDao.getlistaIdsTodos(consultaFilter);
		return listaIdsTodos;
		
	}

	/** DAA 11/02/2013  Metodo para actualizar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param claseDetalleBean
	 */
	public Map<String, String>  cambioMasivo(String listaIdsMarcados_cm, ClaseDetalle claseDetalleBean,
			String cicloCultivoCheck,String sistemaCultivoCheck,String tipoCapitalCheck,String tipoPlantacionCheck ) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		boolean actualizado = false;
		boolean noActualizado = false;
		try {
			String listaIds = listaIdsMarcados_cm.substring(0,listaIdsMarcados_cm.length()-1);
			
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			ClaseDetalle cd = new ClaseDetalle();
			for (int i=0; i<ids.length;i++){
				cd = (ClaseDetalle) claseDetalleDao.getObject(ClaseDetalle.class, new Long(ids[i]));
				claseDetalleDao.evict(cd);
				
				if (!StringUtils.nullToString(claseDetalleBean.getCodmodulo()).equals("")){
					cd.setCodmodulo(claseDetalleBean.getCodmodulo());
				}
				if (claseDetalleBean.getCultivo().getId().getCodcultivo()!= null){
					cd.setCultivo(claseDetalleBean.getCultivo());
				}
				if (claseDetalleBean.getSistemaCultivo().getCodsistemacultivo()!= null){
					cd.setSistemaCultivo(claseDetalleBean.getSistemaCultivo());
				}
				if (sistemaCultivoCheck.equals("sistemaCultivoCheck")){ //Es que ha pinchado el check y hay que actualizar a null
					cd.setSistemaCultivo(null);
				}
				if (claseDetalleBean.getCicloCultivo().getCodciclocultivo()!= null){
					cd.setCicloCultivo(claseDetalleBean.getCicloCultivo());
				}
				if (cicloCultivoCheck.equals("cicloCultivoCheck")){ //Es que ha pinchado el check y hay que actualizar a null
					cd.setCicloCultivo(null);
				}
				if (claseDetalleBean.getVariedad().getId().getCodvariedad()!= null){
					cd.setVariedad(claseDetalleBean.getVariedad());
				}
				if (claseDetalleBean.getTipoCapital().getCodtipocapital()!= null){
					cd.setTipoCapital(claseDetalleBean.getTipoCapital());
				}
				if (tipoCapitalCheck.equals("tipoCapitalCheck")){ //Es que ha pinchado el check y hay que actualizar a null
					cd.setTipoCapital(null);
				}
				if (claseDetalleBean.getCodprovincia()!= null){
					cd.setCodprovincia(claseDetalleBean.getCodprovincia());
				}
				if (claseDetalleBean.getCodcomarca()!= null){
					cd.setCodcomarca(claseDetalleBean.getCodcomarca());
				}
				if (claseDetalleBean.getCodtermino() != null){
					cd.setCodtermino(claseDetalleBean.getCodtermino());
				}
				if (claseDetalleBean.getSubtermino()!= null){
					cd.setSubtermino(claseDetalleBean.getSubtermino());
				}
				if (claseDetalleBean.getTipoPlantacion().getCodtipoplantacion()!= null){
					cd.setTipoPlantacion(claseDetalleBean.getTipoPlantacion());
				}
				if (tipoPlantacionCheck.equals("tipoPlantacionCheck")){ //Es que ha pinchado el check y hay que actualizar a null
					cd.setTipoPlantacion(null);
				}
				if (claseDetalleDao.existeClaseDetalle(cd)){
					noActualizado = true;
				}else{
					claseDetalleDao.saveOrUpdate(cd);
					actualizado = true;
				}
			}
			if (noActualizado){
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
			}
			if (actualizado){
				parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.edicion.OK"));
			}
			
		} catch (DAOException e) {
			logger.debug("Error al ejecutar el Cambio Masivo ", e);
		}
		return parameters;
	}
	
	/** TMR 05/03/2013  Recupera el limit de sesion para pasarlo al bean de cambio Masivo.
	 * @param Limit
	 * @return ErrorWsAccion
	 */
	public ClaseDetalle getCambioMasivoBeanFromLimit(Limit consultaClaseDetalle_LIMIT) {
		ClaseDetalle cambioMasivoClaseDetalleBean = new ClaseDetalle();
		
		//CLASEID
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter("clase.id")){
			cambioMasivoClaseDetalleBean.getClase().setId(new Long(consultaClaseDetalle_LIMIT.getFilterSet().getFilter("clase.id").getValue()));
			Clase c = (Clase) claseDetalleDao.getObject(Clase.class, cambioMasivoClaseDetalleBean.getClase().getId());
			cambioMasivoClaseDetalleBean.setClase(c);
		}
		// MODULO
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(MODULO))){
			cambioMasivoClaseDetalleBean.setCodmodulo(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(MODULO)).getValue());
		}
		// CICLO_CULTIVO
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(CICLO_CULTIVO))){
			cambioMasivoClaseDetalleBean.getCicloCultivo().setCodciclocultivo(
					new BigDecimal (consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(CICLO_CULTIVO)).getValue()));
		}
		// CODERROR
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(SISTEMA_CULTIVO))){
			cambioMasivoClaseDetalleBean.getSistemaCultivo().setCodsistemacultivo(
					new BigDecimal (consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(SISTEMA_CULTIVO)).getValue()));
		}
		// CULTIVO
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(CULTIVO))){
			cambioMasivoClaseDetalleBean.getCultivo().getId().setCodcultivo(
					new BigDecimal (consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(CULTIVO)).getValue()));
		}
		//VARIEDAD
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(VARIEDAD))){
			cambioMasivoClaseDetalleBean.getVariedad().getId().setCodvariedad(
					new BigDecimal (consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(VARIEDAD)).getValue()));
		}
		// PROVINCIA
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(PROVINCIA))){
			cambioMasivoClaseDetalleBean.setCodprovincia(
					new BigDecimal(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(PROVINCIA)).getValue()));
		}
		// COMARCA
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(COMARCA))){
			cambioMasivoClaseDetalleBean.setCodcomarca(
					new BigDecimal(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(COMARCA)).getValue()));
		}
		// TERMINO
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(TERMINO))){
			cambioMasivoClaseDetalleBean.setCodtermino(
					new BigDecimal(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(TERMINO)).getValue()));
		}
		// SUBTERMINO
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(SUBTERMINO))){
			cambioMasivoClaseDetalleBean.setSubtermino(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(SUBTERMINO)).getValue().charAt(0));
		}
		// TCAPITAL
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(TCAPITAL))){
			cambioMasivoClaseDetalleBean.getTipoCapital().setCodtipocapital(
					new BigDecimal(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(TCAPITAL)).getValue().charAt(0)));
		}
		//TPLANTACION
		if(null != consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(TPLANTACION))){
			cambioMasivoClaseDetalleBean.getTipoPlantacion().setCodtipoplantacion(
					new BigDecimal(consultaClaseDetalle_LIMIT.getFilterSet().getFilter(columnas.get(TPLANTACION)).getValue().charAt(0)));
		}
				
		return cambioMasivoClaseDetalleBean;
	}	
	
	/**
	 * Devuelve un booleano indicando si la provincia introducida es correcta
	 * @param codProvincia
	 * @return
	 */
	private boolean isProvinciaValida (BigDecimal codProvincia) {
		
		try {
			return provinciaDao.checkProvinciaExists(codProvincia);
		}
		catch (DAOException e) {
			logger.debug("Ocurrió un error al validar la provincia", e);
		}
		
		return false;
	}
	
	/**
	 * Devuelve un booleano indicando si la comarca es correcta para la provincia indicada
	 * @param codProvincia
	 * @param codComarca
	 * @return
	 */
	private boolean isComarcaValida (BigDecimal codProvincia, BigDecimal codComarca) {
		
		try {
			return comarcaDao.checkComarcaExists(codComarca, codProvincia);
		}
		catch (DAOException e) {
			logger.debug("Ocurrió un error al validar la comarca", e);
		}
		
		return false;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setClaseDetalleDao(IClaseDetalleDao claseDetalleDao) {
		this.claseDetalleDao = claseDetalleDao;
	}

	public void setProvinciaDao(IProvinciaDao provinciaDao) {
		this.provinciaDao = provinciaDao;
	}

	public void setComarcaDao(IComarcaDao comarcaDao) {
		this.comarcaDao = comarcaDao;
	}
}
