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
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.PrimaMinimaSbpFilter;
import com.rsi.agp.core.jmesa.service.IPrimaMinimaSbpService;
import com.rsi.agp.core.jmesa.sort.PrimaMinimaSbpSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.sbp.IPrimaMinimaSbpDao;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;

@SuppressWarnings("deprecation")
public class PrimaMinimaSbpService implements IPrimaMinimaSbpService {
	
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	private IPrimaMinimaSbpDao primaMinimaSbpDao;
	private ILineaDao lineaDao;
	
	
	// Constantes para los nombres de las columnas del listado
	private final String ID_STR = "ID";		
	private final String PLAN = "PLAN";
	private final String LINEA = "LINEA";
	private final String PRIMAMINIMA = "PRIMAMINIMA";
		
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();

	@Override
	public Collection<PrimaMinimaSbp> getPrimaMinimaSbpWithFilterAndSort(
			PrimaMinimaSbpFilter filter, PrimaMinimaSbpSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		
		return primaMinimaSbpDao.getPrimaMinimaSbpWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}

	
	/**
	 * Setter de propiedad
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTablaPrimaMinimaParaSbp (HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp, String origenLlamada) {
	
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, primaMinimaSbp, origenLlamada);

		// Configura el filtro y la ordenación, busca las pólizas y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve	
		//return html (tableFacade, lineas);
		return html (tableFacade);
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de pólizas
	 * @param request
	 * @param response
	 * @return
	 */	
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp, String origenLlamada) {
		
	
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	        
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
         
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaPrimaMinimaSbp_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaPrimaMinimaSbp_LIMIT"));
	    		}
    		}
    		
    		else{
    		
    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			cargarFiltrosBusqueda(columnas, primaMinimaSbp, tableFacade);
    		}
    	}                
        
        return tableFacade;
		
	}
	
	/**
     * Metodo para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
    private String html(TableFacade tableFacade){	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuración de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        } else {
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        }
           	
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
    
	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			// columnas.put(USUARIO, "usuario.codusuario");
			columnas.put(PLAN, "linea.codplan");
			columnas.put(LINEA, "linea.codlinea");
			columnas.put(PRIMAMINIMA, "primaMinima");		
		}
				
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(PLAN), columnas.get(LINEA), columnas.get(PRIMAMINIMA)); 
        
        // Devuelve el mapa
        return columnas;
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de las pólizas y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade){
        
		// Obtiene el Filter para la búsqueda de pólizas
		Limit limit = tableFacade.getLimit();
		PrimaMinimaSbpFilter consultaFilter = getConsultaPrimaMinimaSbpFilter(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getPrimaMinimaSbpCountWithFilter(consultaFilter);
        logger.debug("********** count filas para Sbp = "+totalRows+" **********");
        //logger.debug("Encontradas " + totalRows + " pólizas para sobreprecio.");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la búsqueda de pólizas
        PrimaMinimaSbpSort consultaSort = getConsultaPrimaMinimaSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<PrimaMinimaSbp> items = new ArrayList<PrimaMinimaSbp>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getPrimaMinimaSbpWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para Sbp = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
    }
	
	/**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		
		// 1 - Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "1%");
    	// 2 - Plan
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "1%");
    	// 3 - Línea
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "1%");
    	// 4 - Prima Minima
    	configColumna(table, columnas.get(PRIMAMINIMA), "Prima M&iacute;nima", true, true, "1%");
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
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
		 
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, PrimaMinimaSbp primaMinimaSbp, TableFacade tableFacade) {
		
		// Plan
		if (FiltroUtils.noEstaVacio (primaMinimaSbp.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), primaMinimaSbp.getLinea().getCodplan().toString()));
		// Linea
		if (FiltroUtils.noEstaVacio (primaMinimaSbp.getLinea().getLineaseguroid()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), primaMinimaSbp.getLinea().getCodlinea().toString()));
		// Prima Mínima
		if (FiltroUtils.noEstaVacio (primaMinimaSbp.getPrimaMinima()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PRIMAMINIMA), primaMinimaSbp.getPrimaMinima().toString()));
	}
	
	 /**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Long idPrimaMinimaBigDec = (Long) new BasicCellEditor().getValue(item, "id", rowcount);
            	String idPrimaMinima = idPrimaMinimaBigDec.toString();
				BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
            	BigDecimal codplan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	String nomLinea = (String)new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);
            	BigDecimal primaMinima = (BigDecimal)new BasicCellEditor().getValue(item, "primaMinima", rowcount);
            	
            	            	
            	HtmlBuilder html = new HtmlBuilder();
            	
            	// botón editar
					logger.info("setPrimaMinima: "+ idPrimaMinima);
                    html.a().href().quote().append("javascript:editar('"+idPrimaMinima+"','"+codLinea+"','"+codplan+"','"+nomLinea+"', '"+primaMinima+"');").quote().close();
                    html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Prima M&iacute;nima Sbp\" title=\"Editar Prima M&iacute;nima Sbp\"/>");
                    html.aEnd();
                    html.append("&nbsp;");

                // botón borrar póliza Sbp
		            html.a().href().quote().append("javascript:borrar('"+ idPrimaMinima +"');").quote().close();
		            html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Prima M&iacute;nima Sbp\" title=\"Borrar Prima M&iacute;nima Sbp\"/>");
		            html.aEnd();
		            html.append("&nbsp;");
				
                return html.toString();
            }
		};
	}
	
	/**
	 * Crea y configura el Filter para la consulta de pólizas
	 * @param limit
	 * @return
	 */
	private PrimaMinimaSbpFilter getConsultaPrimaMinimaSbpFilter(Limit limit) {
		PrimaMinimaSbpFilter consultaFilter = new PrimaMinimaSbpFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            consultaFilter.addFilter(property, value);
        }
        return consultaFilter;
	}

	@Override
	public int getPrimaMinimaSbpCountWithFilter(PrimaMinimaSbpFilter filter) {
		return primaMinimaSbpDao.getPrimaMinimaSbpCountWithFilter(filter);
	}
	
	/**
	 * Crea y configura el Sort para la consulta de Prima Mínima
	 * @param limit
	 * @return
	 */
	private PrimaMinimaSbpSort getConsultaPrimaMinimaSort(Limit limit) {
		PrimaMinimaSbpSort consultaSort = new PrimaMinimaSbpSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public PrimaMinimaSbp getPrimaMinimaSbp(Long idPrimaMinimaSbp)
			throws BusinessException {
		try {
		
			return (PrimaMinimaSbp) primaMinimaSbpDao.getObject(
					PrimaMinimaSbp.class, idPrimaMinimaSbp);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener la Prima Minima de sobreprecio", dao);
			throw new BusinessException("Se ha producido al obtener la Prima Minima de sobreprecio:", dao);
		}
	}
				
	public Map<String, Object> altaPrimaMinimaSbp(PrimaMinimaSbp primaMinimaSbp) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		try {
			Long lineaSeguroId = lineaDao.getLineaSeguroId(primaMinimaSbp.getLinea().getCodlinea(), 
					primaMinimaSbp.getLinea().getCodplan());
				
			if (StringUtils.nullToString(lineaSeguroId).equals("")) {
				// mensaje de error, línea no existe
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_GRAB_PRIMA_MIN_LINEASEG_KO));	

			}
			else if (primaMinimaSbpDao.checkPrimaMinimaSbpExists(lineaSeguroId)) {
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_GRAB_PRIMA_MIN_PRIMA_EXISTE_KO));
			} 
			else{
				primaMinimaSbp.getLinea().setLineaseguroid(lineaSeguroId);
				primaMinimaSbpDao.saveOrUpdate(primaMinimaSbp);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_GRAB_PRIMA_MINIMA_OK));
			}					
		} catch (Exception ex) {
			throw new BusinessException("Error al dar de alta la Prima Mínima de Sobreprecio", ex);
		}
		
		return parameters;
	}
	
	public void bajaPrimaMinimaSbp(PrimaMinimaSbp primaMinimaSbp) throws BusinessException {
	
		try {
			primaMinimaSbpDao.delete(primaMinimaSbp);
			logger.debug("idPrimaMinimaSbp borrada = " + primaMinimaSbp.getId());
		} catch (Exception ex) {
			logger.error("Error al eliminar la Prima Mínima de Sobreprecio", ex);
			throw new BusinessException("Error al eliminar la Prima Minima de Sobreprecio", ex);
		}
	}

	public Map<String, Object> editarPrimaMinimaSbp(PrimaMinimaSbp primaMinimaSbpEdit) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			Long lineaSeguroId = lineaDao.getLineaSeguroId(primaMinimaSbpEdit
					.getLinea().getCodlinea(), primaMinimaSbpEdit.getLinea()
					.getCodplan());
			primaMinimaSbpEdit.getLinea().setLineaseguroid(lineaSeguroId);
			
			if (lineaSeguroId == null) {
				// mensaje de error, línea facilitada no existe
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_GRAB_PRIMA_MIN_LINEASEG_KO));		
			
			} else{
				primaMinimaSbpEdit.getLinea().setLineaseguroid(lineaSeguroId);
				primaMinimaSbpDao.saveOrUpdate(primaMinimaSbpEdit);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_EDITAR_PRIMA_MINIMA_OK));
				logger.debug("Prima Mínima de Sobreprecio editada con lineaseguroid = "
					+ primaMinimaSbpEdit.getLinea().getLineaseguroid());
			}

		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar la prima mínima de sobrepreci", ex);
			throw new BusinessException("Error al editar la Prima Mínima de Sobreprecio", ex);
		} catch (Exception ex) {
			logger.debug("Error al editar la prima mínima de sobreprecio", ex);
			throw new BusinessException("Error al editar la Prima Mínima de Sobreprecio", ex);
		}
		
		return parameters;
	}

	
	public IPrimaMinimaSbpDao getPrimaMinimaSbpDao() {
		return primaMinimaSbpDao;
	}


	public void setPrimaMinimaSbpDao(IPrimaMinimaSbpDao primaMinimaSbpDao) {
		this.primaMinimaSbpDao = primaMinimaSbpDao;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}
