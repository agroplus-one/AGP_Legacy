package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.FechasContratacionSbpFilter;
import com.rsi.agp.core.jmesa.service.IFechasContratacionSbpService;
import com.rsi.agp.core.jmesa.sort.FechasContratacionSbpSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.ICultivoDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.sbp.IFechasContratacionSbpDao;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;

/**
 * @author U029769
 *
 */
@SuppressWarnings("deprecation")
public class FechasContratacionSbpService implements IFechasContratacionSbpService {
	
	private IFechasContratacionSbpDao fechasContratacionSbpDao;
	private ILineaDao lineaDao;
	private ICultivoDao cultivoDao;
	private String id;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final String ID_STR = "ID";
	private final String PLAN = "PLAN";
	private final String LINEA = "LINEA";
	private final String CULTIVO = "COD.CULTIVO";
	private final String FECHAINICIO = "FECHAINICIO";
	private final String FECHAFIN = "FECHAFIN";
	private final String FECHAFINGARANTIA = "FECHAFINGARANTIA";
	private final String CULTIVO_DESC ="CULTIVO";
	private final String FECHAFINSUPLEMENTOS = "FECHAFINSUPLEMENTOS";

	@Override
	public Collection<FechaContratacionSbp> getFechasContratacionSbpWithFilterAndSort(
			FechasContratacionSbpFilter filter, FechasContratacionSbpSort sort,
			int rowStart, int rowEnd) throws BusinessException {
		return fechasContratacionSbpDao.getFechasContratacionSbpWithFilterAndSort(filter, sort, rowStart, rowEnd);
		
	}

	public String getTablaFechasContratacionSbp (HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp
			, String origenLlamada){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, fechaContratacionSbp, origenLlamada);

		// Configura el filtro y la ordenación, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		return html (tableFacade);
		
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de pólizas
	 * @param request
	 * @param response
	 * @param primeraBusqueda 
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp,String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.

        //Defino los tipos para los filtros. Habrá que redefinir en el filter la forma
        //de filtrar los campos que tienen un tratamiento especial (distinto de 'like %valor%')
        tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaFechasContratacion_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaFechasContratacion_LIMIT"));
	    		}
    		}
    		else{
    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			cargarFiltrosBusqueda(columnas, fechaContratacionSbp, tableFacade);
    		}
    	}                
        
        return tableFacade;
	}
	
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(PLAN, "linea.codplan");
			columnas.put(LINEA, "linea.codlinea");
			columnas.put(CULTIVO, "cultivo.id.codcultivo");
			columnas.put(CULTIVO_DESC, "cultivo.descultivo");
			columnas.put(FECHAINICIO, "fechainicio");
			columnas.put(FECHAFIN, "fechafin");
			columnas.put(FECHAFINGARANTIA, "fechaFinGarantia");
			columnas.put(FECHAFINSUPLEMENTOS, "fechaFinSuplementos");
		}	
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(PLAN), columnas.get(LINEA), columnas.get(CULTIVO),
				columnas.get(CULTIVO_DESC), columnas.get(FECHAINICIO), columnas.get(FECHAFIN), columnas.get(FECHAFINGARANTIA),
				columnas.get(FECHAFINSUPLEMENTOS)); 
        
        return columnas;
	}
	
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, FechaContratacionSbp fechaContratacionSbp, TableFacade tableFacade) {
		
		// Plan
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), fechaContratacionSbp.getLinea().getCodplan().toString()));
		// Linea
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), fechaContratacionSbp.getLinea().getCodlinea().toString()));
		// Fecha Inicio
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getFechainicio())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHAINICIO), new SimpleDateFormat("dd/MM/yyyy").format(fechaContratacionSbp.getFechainicio())));
		// Fecha Fin
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getFechafin())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHAFIN), new SimpleDateFormat("dd/MM/yyyy").format(fechaContratacionSbp.getFechafin())));
		// Fecha Fin Garantia
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getFechaFinGarantia())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHAFINGARANTIA), new SimpleDateFormat("dd/MM/yyyy").format(fechaContratacionSbp.getFechaFinGarantia())));
		// cultivo
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getCultivo().getId().getCodcultivo())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CULTIVO), fechaContratacionSbp.getCultivo().getId().getCodcultivo().toString()));
		// cultivo desc
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getCultivo().getDescultivo())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CULTIVO_DESC), fechaContratacionSbp.getCultivo().getDescultivo().toString()));
		// Fecha Fin Suplementos
		if (FiltroUtils.noEstaVacio (fechaContratacionSbp.getFechaFinSuplementos())) 			
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHAFINSUPLEMENTOS), new SimpleDateFormat("dd/MM/yyyy").format(fechaContratacionSbp.getFechaFinSuplementos())));
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de las fechas de contratacion y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade){
        
		// Obtiene el Filter para la búsqueda de fechas de contratacion
		Limit limit = tableFacade.getLimit();
		FechasContratacionSbpFilter consultaFilter = getConsultaFechasContratacionFilter(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getFechasContratacionSbpCountWithFilter(consultaFilter);
        logger.debug("********** count filas para fechas Contratacion = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la búsqueda de fechas de contratacion
        FechasContratacionSbpSort consultaSort = getConsultaFechasContatacionSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<FechaContratacionSbp> items = new ArrayList<FechaContratacionSbp>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getFechasContratacionSbpWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para FechasContratacion = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
        
    }
	
	/**
	 * Crea y configura el Filter para la consulta de fechas de contratacion
	 * @param limit
	 * @return
	 */
	private FechasContratacionSbpFilter getConsultaFechasContratacionFilter(Limit limit) {
		FechasContratacionSbpFilter consultaFilter = new FechasContratacionSbpFilter();
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
	 * Crea y configura el Sort para la consulta de pólizas
	 * @param limit
	 * @return
	 */
	private FechasContratacionSbpSort getConsultaFechasContatacionSort(Limit limit) {
		
		FechasContratacionSbpSort consultaSort = new FechasContratacionSbpSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
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
        }else{
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
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
            	BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
            	BigDecimal codplan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	String nomLinea = (String)new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);
            	Date fechainicio = (Date)new BasicCellEditor().getValue(item, "fechainicio", rowcount);
            	Date fechafin = (Date)new BasicCellEditor().getValue(item, "fechafin", rowcount);
            	Date fechafinGarantia = (Date)new BasicCellEditor().getValue(item, "fechaFinGarantia", rowcount);
            	String codcultivo = new BasicCellEditor().getValue(item, "cultivo.id.codcultivo", rowcount).toString();
            	String nombreCultivo = null;
            	Date fechaFinSuplementos = (Date)new BasicCellEditor().getValue(item, "fechaFinSuplementos", rowcount);
            	
				try {
					nombreCultivo = new BasicCellEditor().getValue(item, "cultivo.descultivo", rowcount).toString();
				} catch (Exception e) {
					logger.debug("nombreCultivo null");
				}
            	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            	HtmlBuilder html = new HtmlBuilder();
            	// botón editar
            	// codigo original
            	// html.a().href().quote().append("javascript:editar('"+id+"','"+codLinea+"','"+codplan+"','"+nomLinea+"','"+formato.format(fechainicio)+"'," +
            			// "'"+formato.format(fechafin)+"');").quote().close();
           	 	// new code
            	html.a().href().quote().append("javascript:editar('"+id+"','"+codLinea+"','"+codplan+"','"+codcultivo+"','"+StringUtils.nullToString(nombreCultivo)+"','"+nomLinea+"','"+formato.format(fechainicio)+"'," +
                     "'"+formato.format(fechafin)+"', '"+formato.format(fechafinGarantia)+"', '"+formato.format(fechaFinSuplementos)+"');").quote().close();
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar p&oacute;liza Sbp\" title=\"Editar p&oacute;liza Sbp\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // botón borrar 
            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar l&iacute;nea Sbp\" title=\"Borrar l&iacute;nea Sbp\"/>");
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
	private void configurarColumnas(HtmlTable table) {
		
		// Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "6%");

    	// 1 - Plan
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "6%");
    	// 2- Línea
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "6%");
    	// 3- codcultivo
    	configColumna(table, columnas.get(CULTIVO), "Cod.Cultivo", true, true, "8%");
    	// 4- cultivo desc
    	configColumna(table, columnas.get(CULTIVO_DESC), "Cultivo", true, true, "14%");
    	// 5 - Fecha de inicio
    	configColumnaFecha(table, columnas.get(FECHAINICIO), "Inicio de Contrataci&oacute;n", true, true, "14%", "dd/MM/yyyy");
    	// 6 - Fecha de Fin
    	configColumnaFecha(table, columnas.get(FECHAFIN), "Final de Contrataci&oacute;n", true, true, "14%", "dd/MM/yyyy");
    	// 7 - Fecha de Fin Garantia
    	configColumnaFecha(table, columnas.get(FECHAFINGARANTIA), "Fin de Garant&iacute;a", true, true, "13%", "dd/MM/yyyy");
    	// 8 - Fecha de Fin Suplementos
    	configColumnaFecha(table, columnas.get(FECHAFINSUPLEMENTOS), "Final de env&iacute;o de Suplementos", true, true, "19%", "dd/MM/yyyy");
    	
	}
	
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parámetro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
	 * @param filterable Indica si se podrá buscar por esa columna
	 * @param sortable Indica si se podrá ordenar por esa columna
	 * @param width Ancho de la columna
	 * @param fFecha Formato de fecha con la que se mostrarán los datos de esta columna
	 */
	private void configColumnaFecha (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));
		} catch (Exception e) {
			logger.error("Ocurrió un error al configurar el formato de fecha de la columna " + idCol, e);
		}
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
	
	public FechaContratacionSbp getFechaContratacionSbp(Long idFechaContratacion)
		throws BusinessException {
		try {
			return (FechaContratacionSbp) fechasContratacionSbpDao.get(
					FechaContratacionSbp.class, idFechaContratacion);
		
		} catch (Exception dao) {
			logger
					.error("Se ha producido error al obtener Fecha Contratacion de sobreprecio: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener la Fecha Contratacion de sobreprecio:",
					dao);
		}
	}
		
	public void bajaFechasContratacionSbp(
		FechaContratacionSbp fechaContratacionSbp) throws BusinessException {
		try {
			
			fechasContratacionSbpDao.delete(FechaContratacionSbp.class,fechaContratacionSbp.getId());
			logger.debug("fechaContratacionSbp borrada  = "
					+ fechaContratacionSbp.getId());
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar la fechaContratacionSbp de Sobreprecio",
					ex);
		}
	}
		
	public Map<String, Object> editaFechasContratacionSbp(FechaContratacionSbp fechaContratacionSbp) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {	
				Long lineaSeguroId = lineaDao.getLineaSeguroId(fechaContratacionSbp
						.getLinea().getCodlinea(), fechaContratacionSbp.getLinea()
						.getCodplan());
							
				/* if (fechasContratacionSbpDao.existeLineaSeguroId(lineaSeguroId)) {
					// mensaje de error, línea ya existe
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_LINEASEG_YA_EXISTE));	
					
				} else */ 
				if (lineaSeguroId == null) {
					// mensaje de error, línea no existe en tabla "tb_lineas"
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_LINEASEG_NO_EXISTE));	
				}else if (fechasContratacionSbpDao.existeLineaSeguroIdCultivo(lineaSeguroId,fechaContratacionSbp.getCultivo().getId().getCodcultivo(),
						fechaContratacionSbp.getId())) {
					// mensaje de error, línea ya existe en tabla "TB_SBP_SOBREPRECIO"
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_YA_EXISTE));	
				
				}else if (cultivoDao.getCultivo(fechaContratacionSbp.getLinea().getCodplan(),fechaContratacionSbp.getLinea()
						.getCodlinea(),fechaContratacionSbp.getCultivo().getId().getCodcultivo())==null) {
					//mensaje de error, cultivo no pertenece a esa linea
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_LINEA_CULTIVO_KO));
					
				}else {
						fechaContratacionSbp.getLinea().setLineaseguroid(lineaSeguroId);
						fechaContratacionSbp.getCultivo().getId().setLineaseguroid(lineaSeguroId);
						fechaContratacionSbp.getCultivo().getLinea().setLineaseguroid(lineaSeguroId);
						fechasContratacionSbpDao.saveOrUpdate(fechaContratacionSbp);
						// mensaje modificación ok
						parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_EDITA_FECHA_CONTRATACION_OK));
						logger.debug("fechaContratacionSbp modificado con id = "
								+ fechaContratacionSbp.getId());				
				}
				
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al editar la fechaContratacionSbp de Sobreprecio",
					ex);
		}
		
		return parameters;
	}
		
	public Map<String, Object> altaFechasContratacionSbp(FechaContratacionSbp fechaContratacionSbp) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {			
				Long lineaSeguroId = lineaDao.getLineaSeguroId(fechaContratacionSbp
					.getLinea().getCodlinea(), fechaContratacionSbp.getLinea()
					.getCodplan());
					
				
				 if (lineaSeguroId == null) {
					// mensaje de error, línea no existe en tabla "TB_LINEAS"
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_LINEASEG_NO_EXISTE));	
						
				} else if (fechasContratacionSbpDao.existeLineaSeguroIdCultivo(lineaSeguroId,fechaContratacionSbp.getCultivo().getId().getCodcultivo()
						,null)) {
					// mensaje de error, línea ya existe en tabla "TB_SBP_SOBREPRECIO"
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_YA_EXISTE));	
					
				}else if (cultivoDao.getCultivo(fechaContratacionSbp.getLinea().getCodplan(),fechaContratacionSbp.getLinea()
						.getCodlinea(),fechaContratacionSbp.getCultivo().getId().getCodcultivo())==null) {
					//mensaje de error, cultivo no pertenece a esa linea
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ERROR_FECHA_CONTRATACION_LINEA_CULTIVO_KO));
					
				}else {
					
					// set fechaContratacionSbp id to null
					fechaContratacionSbp.setId(null);
					// set Linea y guardar nueva FechaContratacionSbp
					fechaContratacionSbp.getLinea().setLineaseguroid(lineaSeguroId);
					fechaContratacionSbp.getCultivo().getId().setLineaseguroid(lineaSeguroId);
					fechaContratacionSbp.getCultivo().getLinea().setLineaseguroid(lineaSeguroId);
					fechasContratacionSbpDao.saveOrUpdate(fechaContratacionSbp);
					// mensaje grabacion ok
					 parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_ALTA_FECHA_CONTRATACION_OK));
				}

		} catch (Exception ex) {
			throw new BusinessException(
					"Error al dar de alta la fechaContratacionSbp de Sobreprecio",
					ex);
		}
		
		return parameters;
	}
	
	public FechaContratacionSbp getLineaFechaContratacionSbp(Long linea) throws BusinessException {
		
		try {
				return (FechaContratacionSbp) fechasContratacionSbpDao.get(FechaContratacionSbp.class, linea);	
				
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener Id. de Línea asociado a Fecha Contratacion de sobreprecio: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener el de Línea asociado a Fecha Contratacion de sobreprecio:",
					dao);
		}
	}
	
	
	public int getFechasContratacionSbpCountWithFilter(
			FechasContratacionSbpFilter filter) {
		return fechasContratacionSbpDao.getFechasContratacionSbpCountWithFilter(filter);
	}
	
	
	public void setFechasContratacionSbpDao(
			IFechasContratacionSbpDao fechasContratacionSbpDao) {
		this.fechasContratacionSbpDao = fechasContratacionSbpDao;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setCultivoDao(ICultivoDao cultivoDao) {
		this.cultivoDao = cultivoDao;
	}

}
