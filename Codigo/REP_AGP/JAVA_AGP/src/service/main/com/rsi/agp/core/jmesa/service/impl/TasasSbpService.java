package com.rsi.agp.core.jmesa.service.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.jmesa.core.filter.MatcherKey;
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
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.LongFilterMatcher;
import com.rsi.agp.core.jmesa.filter.TasasSbpFilter;
import com.rsi.agp.core.jmesa.service.ITasasSbpService;
import com.rsi.agp.core.jmesa.sort.TasasSbpSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.admin.IProvinciaDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.sbp.ISobrePrecioDao;
import com.rsi.agp.dao.models.sbp.ITasasSbpDao;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

@SuppressWarnings("deprecation")
public class TasasSbpService implements ITasasSbpService {
	
	private ITasasSbpDao tasasSbpDao;
	private ILineaDao lineaDao;
	private IProvinciaDao provinciaDao;
	private String id;
	private ISobrePrecioDao sobrePrecioDao;
	
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");

	@Override
	public int getTasasSbpCountWithFilter(TasasSbpFilter filter) {
		return tasasSbpDao.getTasasSbpCountWithFilter(filter);
	}

	@Override
	public Collection<TasasSbp> getTasasSbpWithFilterAndSort(TasasSbpFilter filter, TasasSbpSort sort, int rowStart, int rowEnd) throws BusinessException {
		// Obtiene el listado de tasas de sobreprecio de la bd, calcula el campo 'Tasa total' de cada una y devuelve el listado
		return calculaTasaTotal (tasasSbpDao.getTasasSbpWithFilterAndSort(filter, sort, rowStart, rowEnd));
		
		
	}
	
	@Override
	public Map<String, String> updateTasaSbp (TasasSbp tasaSbp) {
		
		// Mapa que contiene los errores producidos en el proceso de alta
		Map<String, String> errores = new HashMap<String, String>();
		
		try {
			tasaSbp.getCultivo().getId().setLineaseguroid(tasaSbp.getLinea().getLineaseguroid());
			
			// Carga el objeto tasa desde bd
			TasasSbp tasa = getTasaSbp(tasaSbp.getId());
			tasasSbpDao.evict(tasa);
			
			// Si el plan/linea y provincia del objeto no van a cambiar, no se hacen validaciones y se ejecuta el actualización directamente		
			if (isPKTasasIguales(tasaSbp, tasa)) {
				logger.debug("Se actualiza la tasa");				
				tasasSbpDao.saveOrUpdate(tasaSbp);
			}
			// Si alguna PK ha cambiado, se realizan las validaciones antes de actualizar
			else {
				validarSaveOrUpdate(tasaSbp, errores);
			}
		
		} catch (Exception e) {
			logger.error("Ocurrió un error al borrar la tasa con id " + tasaSbp.getId(), e);	
			errores.put(ConstantsSbp.ERROR_GENERAL, "true");
			return errores;
		}
		
		return errores;
	}

	/**
	 * Comprueba si los campos lineaseguroid y codprovincia de las dos tasas son iguales
	 * @param tasaSbp
	 * @param tasa
	 * @return
	 */
	private boolean isPKTasasIguales(TasasSbp tasaSbp, TasasSbp tasa) {
		return tasaSbp.getLinea().getCodlinea().equals(tasa.getLinea().getCodlinea()) &&
			tasaSbp.getLinea().getCodplan().equals(tasa.getLinea().getCodplan()) &&
			tasaSbp.getComarca().getId().getCodprovincia().equals(tasa.getComarca().getId().getCodprovincia());
	}
	
	@Override
	public Map<String, String> altaTasaSbp (TasasSbp tasaSbp) {
		
		// Mapa que contiene los errores producidos en el proceso de alta
		Map<String, String> errores = new HashMap<String, String>();
		BigDecimal codProvincia = tasaSbp.getComarca().getId().getCodprovincia();
		
		try {	
				/* 
				 * cÓ“DIGO ORIGINAL
				 * Se borra el id de tasa del objeto por si viniera cargado del formulario
				tasaSbp.setId(null);
				// Realiza las validaciones de lineaseguroid y provincia e inserta la tasa
				validarSaveOrUpdate(tasaSbp, errores);
				*/	
				
			
				// check provincia existe
				if (provinciaDao.checkProvinciaExists(codProvincia)){
							// Se borra el id de tasa del objeto por si viniera cargado del formulario
							tasaSbp.setId(null);
							// Realiza las validaciones de lineaseguroid y provincia e inserta la tasa
							validarSaveOrUpdate(tasaSbp, errores);			
				} else {			
							errores.put(ConstantsSbp.ERROR_PROVINCIA_NO_EXISTE, "true");							
				}
				
		} catch (DAOException e) {
			logger.error("Ocurrió un error al intentar guardar la tasa con id " + tasaSbp.getId(), e);	
			errores.put(ConstantsSbp.ERROR_GENERAL, "true");
			return errores;
		}
		
		return errores;
	}

	/**
	 * Realiza las validaciones de lineaseguroid,  provincia 
	 *  y comprueba si esta repetido e inserta la tasa
	 * @param tasaSbp
	 * @param errores
	 * @throws DAOException
	 */
	private void validarSaveOrUpdate(TasasSbp tasaSbp,	Map<String, String> errores) throws DAOException {
		// Obtiene el código de lineaseguroid correspondiente al plan y línea indicado 
		Long lineaSeguroId = lineaDao.getLineaSeguroId(tasaSbp.getLinea().getCodlinea(), tasaSbp.getLinea().getCodplan());
		
		tasaSbp.getLinea().setLineaseguroid(lineaSeguroId);
		tasaSbp.getCultivo().getId().setLineaseguroid(lineaSeguroId);
		
		// Se comprueba si el plan/linea indicado existe
		if (lineaSeguroId == null) {
			logger.debug("El plan/línea indicado no existe");
			errores.put(ConstantsSbp.ERROR_LINEASEGUROID_NO_EXISTE, "true");
			return;
		}
		
		// check si provincia existe
		if (!provinciaDao.checkProvinciaExists(tasaSbp.getComarca().getId().getCodprovincia())) {
			logger.debug("La Provincia indicada no es válida");
			errores.put(ConstantsSbp.ERROR_PROVINCIA_NO_EXISTE, "true");
			return;
		}
		
		// comprobamos si el cultivo es valido
		if (!sobrePrecioDao.existeCultivo(tasaSbp.getLinea().getLineaseguroid(),tasaSbp.getCultivo().getId().getCodcultivo())){
			// mensaje de error, Cultivo no existe
			errores.put(ConstantsSbp.ERROR_SOBREPRECIO_CULTIVO_KO, "true");					
			return;
		}
		
		// Se comprueba si ya existe una tasa mínima dada de alta para el plan/linea y comarca y cultivo indicado			
		if (getTasasSbpCountWithFilter(getFilterPrimaRepetida(tasaSbp))>0) {
			logger.debug("Ya existe una tasa mínima para el plan/línea y la provincia indicados");
			errores.put(ConstantsSbp.ERROR_TASA_YA_EXISTE , "true");
			return;
		}
					
		// Se hace el saveOrUpdate de la tasa
		logger.debug("Se hace el saveOrUpdate de la tasa");
		
		tasasSbpDao.saveOrUpdate(tasaSbp);
	}
	
	@Override
	public TasasSbp getTasaSbp (Long id) {
		
		TasasSbp tasa = null;
		
		try {
			tasa = (TasasSbp) tasasSbpDao.getObject(TasasSbp.class, id);
		}
		catch (Exception e) {
			logger.error("Ocurrió un error al cargar la tasa con id " +id, e);
		}				
		
		return tasa;
	}
		
	@Override
	public boolean bajaTasaSbp (TasasSbp tasaSbp) {
		try {			
			// Carga el objeto tasa
			TasasSbp tasa = getTasaSbp(tasaSbp.getId());
			
			// Si es nulo, se lanza el error
			if (tasa == null) throw new DAOException ("El objeto tasa no se ha podido cargar");
			
			// Borra el objeto
			tasasSbpDao.delete(tasa);
			
		} catch (DAOException e) {
			logger.error("Ocurrió un error al borra la tasa con id " + tasaSbp.getId(), e);
			return false;
		}
		
		return true;
	}

	
	/**
	 * Busca las tasas de sobreprecio que se ajusten al filtro y genera la tabla para presentarlas
	 * @param request
	 * @param response
	 * @param Objeto que encapsula el filtro de la búsqueda
	 * @return Código de la tabla de presentación de las tasas de sobreprecio
	 */
	public String getTablaTasasSbp (HttpServletRequest request, HttpServletResponse response, TasasSbp tasasSbp, String origenLlamada) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, tasasSbp, origenLlamada);
		
		// Configura el filtro y la ordenación, busca las tasas y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade);		
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla de tasas
	 * @param request
	 * @param response
	 * @return
	 */	
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, TasasSbp tasasSbp, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringFilterMatcher());
		cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("tasasSbp_LIMIT") != null){
	    			//Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("tasasSbp_LIMIT"));
	    		}
    		}
    		else{    			
    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			cargarFiltrosBusqueda(tasasSbp, tableFacade);
    			
    			 // -- ORDENACIÓN POR DEFECTO --> ORDER ASC -> Plan, línea y provincia
    			tableFacade.getLimit().getSortSet().addSort(new Sort (TasasSbp.POS_PLAN, TasasSbp.CAMPO_PLAN, Order.DESC));
    			tableFacade.getLimit().getSortSet().addSort(new Sort (TasasSbp.POS_LINEA, TasasSbp.CAMPO_LINEA, Order.ASC));    			    		
    		}
    	}                
        
        return tableFacade;
		
	}
	
	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * @param tableFacade 
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {		
				
    	// Configura el TableFacade con las columnas que se quieren mostrar
        tableFacade.setColumnProperties(TasasSbp.CAMPO_ID, TasasSbp.CAMPO_PLAN, TasasSbp.CAMPO_LINEA,TasasSbp.CAMPO_CODPROVINCIA, TasasSbp.CAMPO_PROVINCIA,
			      TasasSbp.CAMPO_CODCOMARCA, TasasSbp.CAMPO_COMARCA, TasasSbp.CAMPO_CODCULTIVO,TasasSbp.CAMPO_CULTIVO, TasasSbp.CAMPO_TASA_INCENDIO, TasasSbp.CAMPO_TASA_PEDRISCO, TasasSbp.CAMPO_TASA_TOTAL);
				        
	}
	
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * @param tasasSbp
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(TasasSbp tasasSbp, TableFacade tableFacade) {		
		// Plan
		if (FiltroUtils.noEstaVacio (tasasSbp.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_PLAN, tasasSbp.getLinea().getCodplan().toString());
		// Linea
		if (FiltroUtils.noEstaVacio (tasasSbp.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_LINEA, tasasSbp.getLinea().getCodlinea().toString());
		// Codigo de provincia
		if (FiltroUtils.noEstaVacio (tasasSbp.getComarca().getId().getCodprovincia()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_CODPROVINCIA, tasasSbp.getComarca().getId().getCodprovincia().toString());
		// Nombre de provincia
		if (FiltroUtils.noEstaVacio (tasasSbp.getComarca().getProvincia().getNomprovincia()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_PROVINCIA, tasasSbp.getComarca().getProvincia().getNomprovincia().toString());
		// Codigo de comarca
		if (FiltroUtils.noEstaVacio (tasasSbp.getComarca().getId().getCodcomarca()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_CODCOMARCA, tasasSbp.getComarca().getId().getCodcomarca().toString());
		// Nombre de comarca
		if (FiltroUtils.noEstaVacio (tasasSbp.getComarca().getNomcomarca()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_COMARCA, tasasSbp.getComarca().getNomcomarca().toString());
		// Codigo de cultivo
		if (FiltroUtils.noEstaVacio (tasasSbp.getCultivo().getId().getCodcultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_CODCULTIVO, tasasSbp.getCultivo().getId().getCodcultivo().toString());
		// Nombre de cultivo
		if (FiltroUtils.noEstaVacio (tasasSbp.getCultivo().getDescultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_CULTIVO, tasasSbp.getCultivo().getDescultivo().toString());
		// Tasa de incendio
		if (FiltroUtils.noEstaVacio (tasasSbp.getTasaIncendio()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_TASA_INCENDIO, tasasSbp.getTasaIncendio().toString());
		// Tasa de pedrisco
		if (FiltroUtils.noEstaVacio (tasasSbp.getTasaPedrisco()))
			tableFacade.getLimit().getFilterSet().addFilter(TasasSbp.CAMPO_TASA_PEDRISCO, tasasSbp.getTasaPedrisco().toString());
	}
	
	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos de las tasas y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade){
        
		// Obtiene el Filter para la búsqueda de polizas
		Limit limit = tableFacade.getLimit();
        TasasSbpFilter tasasFilter = getTasasSbpFilter(limit); 

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getTasasSbpCountWithFilter(tasasFilter);        
        logger.debug("********** Número de filas para la búsqueda de tasas de sbp = "+totalRows+" **********");
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la busqueda de tasas
        TasasSbpSort tasasSort = getTasasSbpSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        
        // Obtiene los registros que cumplen el filtro
        Collection<TasasSbp> items = new ArrayList<TasasSbp>();		
        try {
			items = getTasasSbpWithFilterAndSort(tasasFilter, tasasSort, rowStart, rowEnd);
			logger.debug("********** Items de la lista de tasas de sbp = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
        
        //tableFacade.setToolbar(new CustomToolbar());
        //tableFacade.setView(new CustomView());
    }
	
	/**
	 * Crea y configura el Filter para la consulta de tasas
	 * @param limit
	 * @return
	 */
	private TasasSbpFilter getTasasSbpFilter(Limit limit) {
		TasasSbpFilter tasasFilter = new TasasSbpFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            
            logger.debug("property:" + property);
            logger.debug("value:" + value);
            
            tasasFilter.addFilter(property, value);
        }
        return tasasFilter;
	}
	
	/**
	 * Crea el objeto Filter para la busqueda de tasas por plan/linea y provincia
	 * @return
	 */
	private TasasSbpFilter getFilterPrimaRepetida(TasasSbp tasasSbp) {
		TasasSbpFilter tasasFilter = new TasasSbpFilter();
                
        tasasFilter.addFilter(TasasSbp.CAMPO_PLAN, tasasSbp.getLinea().getCodplan());
        tasasFilter.addFilter(TasasSbp.CAMPO_LINEA, tasasSbp.getLinea().getCodlinea());
        tasasFilter.addFilter(TasasSbp.CAMPO_CODPROVINCIA, tasasSbp.getComarca().getId().getCodprovincia());
        tasasFilter.addFilter(TasasSbp.CAMPO_CODCOMARCA, tasasSbp.getComarca().getId().getCodcomarca());
        tasasFilter.addFilter(TasasSbp.CAMPO_CODCULTIVO, tasasSbp.getCultivo().getId().getCodcultivo());
                
        return tasasFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta de tasas
	 * @param limit
	 * @return
	 */
	private TasasSbpSort getTasasSbpSort(Limit limit) {
		TasasSbpSort tasaSort = new TasasSbpSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            tasaSort.addSort(property, order);
        }

        return tasaSort;
	}
	
	/**
     * Metodo para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
	private String html(TableFacade tableFacade){
    	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuracion de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        } else {
        	// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
        	// Acciones
        	table.getRow().getColumn(TasasSbp.CAMPO_ID).getCellRenderer().setCellEditor(getCellEditorAcciones());        	
        }
    	
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
    
    /**
	 * Configuracion de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
    	configColumna(table, TasasSbp.CAMPO_ID, "&nbsp;&nbsp;Acciones", false, false, "6%");
    	// 1 - Plan
    	configColumna(table, TasasSbp.CAMPO_PLAN, "Plan", true, true, "4%");
    	// 2 - Linea
    	configColumna(table, TasasSbp.CAMPO_LINEA, "L&iacute;nea", true, true, "5%");  //6
    	// 3 - Codigo de Provincia
    	configColumna(table, TasasSbp.CAMPO_CODPROVINCIA, "Cod. Provincia", true, true, "9%"); //7
    	// 4 - Provincia
    	configColumna(table, TasasSbp.CAMPO_PROVINCIA, "Provincia", true, true, "8%"); //10
    	// 5 - Codigo de Comarca
    	configColumna(table, TasasSbp.CAMPO_CODCOMARCA, "Cod. Comarca", true, true, "9%"); //7
    	// 6 - Comarca
    	configColumna(table, TasasSbp.CAMPO_COMARCA, "Comarca", true, true, "10%");
    	// 7 - Codigo de Cultivo
    	configColumna(table, TasasSbp.CAMPO_CODCULTIVO, "Cod. Cultivo", true, true, "8%"); //7
    	// 8 - Cultivo
    	configColumna(table, TasasSbp.CAMPO_CULTIVO, "Cultivo", true, true, "10%");
    	// 9 - Tasa de incendio
    	configColumna(table, TasasSbp.CAMPO_TASA_INCENDIO, "Tasa de incendio", true, true, "12%"); //8
    	// 10 - Tasa de pedrisco
    	configColumna(table, TasasSbp.CAMPO_TASA_PEDRISCO, "Tasa de pedrisco", true, true, "11%"); //8
    	// 11 - Tasa de pedrisco
    	configColumna(table, TasasSbp.CAMPO_TASA_TOTAL, "Tasa total", true, true, "8%");
	}
	
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parametro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Titulo de la columna
	 * @param filterable Indica si se podra buscar por esa columna
	 * @param sortable Indica si se podra ordenar por esa columna
	 * @param width Ancho de la columna
	 */
	private void configColumna (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
        table.getRow().getColumn(idCol).setFilterable(filterable);
        table.getRow().getColumn(idCol).setSortable(sortable);
        table.getRow().getColumn(idCol).setWidth(width);
	}
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Tasa total'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) { 
				
				// Id de tasa
				String idTasa = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_ID, rowcount).toString();
				// Plan
				String plan = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_PLAN, rowcount).toString();
				// Linea
				String linea = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_LINEA, rowcount).toString();				
				// Nombre de linea
				String nomlinea = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_NOMLINEA, rowcount).toString();
				// Lineaseguroid
				String lineaseguroid = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_LINEASEGUROID, rowcount).toString();
				// CÃ³digo de provincia
				String codprovincia = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_CODPROVINCIA, rowcount).toString();
				// Nombre de la provincia
				//String nombreProvincia = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_PROVINCIA, rowcount).toString();
				String nombreProvincia = null;
				try {
					nombreProvincia = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_PROVINCIA, rowcount).toString();
				} catch (Exception e) {
					logger.debug("nomProvincia null");
				}
				// Codigo de comarca
				String codcomarca = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_CODCOMARCA, rowcount).toString();
				// Nombre de comarca
				//String nombreComarca = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_COMARCA, rowcount).toString();
				String nombreComarca = null;
				try {
					nombreComarca = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_COMARCA, rowcount).toString();
				} catch (Exception e) {
					logger.debug("nombreComarca null");
				}
				// Codigo de cultivo
				String codcultivo = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_CODCULTIVO, rowcount).toString();
				// Nombre de cultivo
				//String nombreCultivo = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_CULTIVO, rowcount).toString();
				String nombreCultivo = null;
				try {
					nombreCultivo = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_CULTIVO, rowcount).toString();
				} catch (Exception e) {
					logger.debug("nombreCultivo null");
				}
				// Tasa de incendio
				String tasaIncendio = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_TASA_INCENDIO, rowcount).toString();
				// Tasa de pedrisco
				String tasaPedrisco = new BasicCellEditor().getValue(item, TasasSbp.CAMPO_TASA_PEDRISCO, rowcount).toString();

            	
				
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// botÃ³n editar            	
    			html.a().href().quote().append("javascript:editar('"+idTasa + "','" + plan + "','" 
    																+linea + "','" + codprovincia + "','"
    																+tasaIncendio + "','" + tasaPedrisco + "','"
    																+lineaseguroid + "','" + nomlinea + "','"
    																+ StringUtils.nullToString(nombreProvincia) + "','" + codcomarca + "','"
    																+ StringUtils.nullToString(nombreComarca) + "','" + codcultivo + "','"
    																+ StringUtils.nullToString(nombreCultivo) + "');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar tasa\" title=\"Editar tasa\"/>");
                html.aEnd();
                html.append("&nbsp;");
                
                // botón borrar
                html.a().href().quote().append("javascript:borrar('"+idTasa+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar tasa\" title=\"Borrar tasa\"/>");
                html.aEnd();
                html.append("&nbsp;&nbsp;");                
                        	
                return html.toString();
            }
		};
	}
	
	/**
	 * Calcula la tasa total de cada tasa de sobreprecio y la añade al listado
	 * @param listaSinTasaTotal
	 * @return
	 */
	private Collection<TasasSbp> calculaTasaTotal (Collection<TasasSbp> lista) {
		
		for (TasasSbp tasasSbp : lista) {
			tasasSbp.setTasaTotal(tasasSbp.getTasaIncendio().add(tasasSbp.getTasaPedrisco()));
		}
		
		return lista;
	}
	
	
	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long lineaSeguroIdOrigen = null;
		Long lineaSeguroIdDestino = null;
		
		// Obtiene el lineaseguroid de los plan/linea origen y destino
		try {
			// ValidaciÃ³n del plan/linea origen
			lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(lineaOrig, planOrig);
			if (lineaSeguroIdOrigen == null) {
				logger.debug("El plan/línea origen no existe, no se continua con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.replicarTasa.planlinea.origen.KO"));
				return parameters;
			}
			
			// ValidaciÃ³n del plan/linea destino
			lineaSeguroIdDestino = lineaDao.getLineaSeguroId(lineaDest, planDest);
			if (lineaSeguroIdDestino == null) {
				logger.debug("El plan/línea destino no existe, no se continua con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.replicarTasa.planlinea.destino.KO"));
				return parameters;
			}
			
			// Valida que el plan/linea destino no tenga TasasSbp dados de alta previamente
			TasasSbpFilter filter = new TasasSbpFilter ();
			filter.addFilter("linea.codplan", planDest);
			filter.addFilter("linea.codlinea", lineaDest);
			
			if (getTasasSbpCountWithFilter(filter) != 0) {
				logger.debug("El plan/línea destino tiene TasasSbp dados de alta, no se continua con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.replicarTasa.planlinea.KO"));
				return parameters;
			}
			
			// Llamada al metodo del DAO que realiza la replica 
			String numregCopiados = tasasSbpDao.replicar(new BigDecimal(lineaSeguroIdOrigen), new BigDecimal(lineaSeguroIdDestino));
			logger.debug("El proceso de réplica ha finalizado correctamente. Numero de registros Copiados = " + numregCopiados);
			if (numregCopiados.equals("0")){
				parameters.put("mensaje", bundle.getString("mensaje.replicarTasa.sin.registros.copiados.OK"));
				return parameters;
			}
		} 
		catch (DAOException e) {
			logger.error("Ocurrió un error al replicar las tasas Sbp ", e);
			parameters.put("alerta", bundle.getString("mensaje.replicarTasa.KO"));
			return parameters;
		}
		
		// Comprobamos si se han copiado todos los registros
		if(tasasSbpDao.numRegDestinoIgualNumRegOrigen (lineaSeguroIdDestino,lineaSeguroIdOrigen)){
			parameters.put("mensaje", bundle.getString("mensaje.replicarTasa.OK"));
		}else{
			parameters.put("mensaje", bundle.getString("mensaje.replicarTasa.registros.pendientes.OK"));
		}
		
		return parameters;
	}

	/** DAA 27/04/2013 Carga las tasas en bbdd a partir de un fichero
	 * 
	 */
	public void subeFicheroTasas(MultipartFile file, HttpServletRequest request) throws BusinessException, IOException{
		
		String str= "";
		int numReg = 0;
		try {
			// Se obtiene del fichero de propiedades los caracteres separadores de decimales de entrada y salida (el que viene
			// indicando en el fichero y al que se quiere convertir)
			String caracEntrada = getValue("importacion.tasas.caracter.entrada");
			String caracSalida = getValue("importacion.tasas.caracter.salida");
						
			try (DataInputStream input = new DataInputStream(file.getInputStream());
					FileOutputStream fos2 = new FileOutputStream(
							new File(bundle.getString("directorio.importacion.tasas")
									+ bundle.getString("nombre.importacion.tasas")));
					DataOutputStream output = new DataOutputStream(fos2)) {

				// recorro el fichero de entrada linea a linea hasta que termine,y añado un ";"
				// y un salto de linea
				// despues pinto en el de salida
				while (null != ((str = input.readLine()))) {
					// para cambiar las comas de los decimales por puntos
					str = str.replaceAll(caracEntrada, caracSalida);

					str = str + ";\n";
					output.writeBytes(str);
					numReg++;
				}
			}	
			logger.debug("Archivo copiado en: "+ bundle.getString("directorio.importacion.tasas") + 
					bundle.getString("nombre.importacion.tasas") + " Registros:" + numReg);
			
			//una vez creado el archivo para la tabla externa tbx_sbp_tasas insertamos en la tabla TB_SBP_TASAS
			tasasSbpDao.volcarTasasSbpFromFichero();

		}catch (IOException ioe) {
			logger.error("Error al tratar el archivo entrada/salida - tasasSbpService.subeFicheroTasas ", ioe);
			throw new IOException(ioe);	
		
		}catch (BusinessException be) {
			logger.error("Error al volcar el archivo en la BBDD - tasasSbpDao.volcarTasasSbpFromFichero ", be);
			throw new BusinessException(be);	
			
		} finally {
			//y despues siempre borramos el fichero creado
			try (FileOutputStream fos = new FileOutputStream(new File(bundle.getString("directorio.importacion.tasas")+ bundle.getString("nombre.importacion.tasas")))) {
				//EMPTY BLOCK
			}
			
		}
	}
	
	/**
	 * Obtiene el valor asociada a la clave indicada del fichero de propiedades
	 * @param key
	 * @return
	 */
	private String getValue (String key) throws IOException {
		
		try {
			return bundle.getString(key);
		}
		catch (Exception e) {
			logger.error("Ocurrió un error al obtener la clave '" + key + "' del fichero de propiedades", e);
			throw (new IOException ());
		}
		
	}
	

	public void setTasasSbpDao(ITasasSbpDao tasasSbpDao) {
		this.tasasSbpDao = tasasSbpDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setProvinciaDao(IProvinciaDao provinciaDao) {
		this.provinciaDao = provinciaDao;
	}

	public void setSobrePrecioDao(ISobrePrecioDao sobrePrecioDao) {
		this.sobrePrecioDao = sobrePrecioDao;
	}

}
