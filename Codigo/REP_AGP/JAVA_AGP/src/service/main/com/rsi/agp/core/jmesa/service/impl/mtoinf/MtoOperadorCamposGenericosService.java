package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
import com.rsi.agp.core.jmesa.filter.OperadorCampoGenericoFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadorCamposGenericosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoVistasService;
import com.rsi.agp.core.jmesa.sort.OperadorCampoGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.OperadorInforme;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoOperadorCamposGenericosDao;
import com.rsi.agp.dao.tables.mtoinf.OperadorCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Vista;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfOperadores;

@SuppressWarnings("deprecation")
public class MtoOperadorCamposGenericosService implements IMtoOperadorCamposGenericosService {
	
	
	private IMtoVistasService mtoVistasService;
	private IMtoOperadorCamposGenericosDao mtoOperadorCamposGenericosDao;
	
	// Constantes para los nombres de las columnas del listado
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private final String ID_STR = "ID";
	private final String TABLAORIGEN = "TABLAORIGEN";
	private final String CAMPO = "CAMPO";
	private final String OPERADOR = "OPERADOR";
	
	// Constantes para los nombres de los campos del formulario
	public static final String CAMPO_ID = "idOpGen";
	public static final String CAMPO_IDVISTA = "id.idvista";
	public static final String CAMPO_TABLAORIGEN = "id.vistanombre";
	public static final String CAMPO_CAMPO = "id.nombrecampo";
	public static final String CAMPO_OPERADOR = "id.operador";
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	
	@Override
	public String getTablaOperadorCampos(HttpServletRequest request, HttpServletResponse response, 
			OperadorCampoGenerico operadorCampoGenerico, String origenLlamada) {
	
			// Crea el TableFacade
			TableFacade tableFacade = crearTableFacade(request, response, operadorCampoGenerico, origenLlamada); 

			// Configura el filtro y la ordenación, busca las pólizas y las carga en el TableFacade
			setDataAndLimitVariables(tableFacade);
			
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());

			// Genera el html de la tabla y lo devuelve	
			return html (tableFacade, request);
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de Operadores Genericos
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, OperadorCampoGenerico operadorCampoGenerico, String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
		columnas = cargarColumnas(tableFacade);
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("opGenerico_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("opGenerico_LIMIT"));
	    		}
    		}
    	}                
        return tableFacade;
	}
	
	/**
     * Metodo para construir el html de la tabla a mostrar
     * @param tableFacade
     * @return
     */
    private String html(TableFacade tableFacade, HttpServletRequest request){	
    	HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        // Configuración de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.hasExport()) {
            tableFacade.render(); 
            return null; 
        } else {
        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo ACCIONES
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(request));
        	// campo OPERADOR
        	table.getRow().getColumn(columnas.get(OPERADOR)).getCellRenderer().setCellEditor(getCellEditorOperador());
        }
    	// Devuelve el html de la tabla
    	return tableFacade.render();
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
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "6%");
    	// 1 - TABLAORIGEN
    	configColumna(table, columnas.get(TABLAORIGEN), "Tabla Origen", true, true, "8%");
    	// 3 - CAMPO
    	configColumna(table, columnas.get(CAMPO), "Campo", true, true, "8%");
    	// 4 - OPERADOR
    	configColumna(table, columnas.get(OPERADOR), "Operador", true, true, "8%");
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
			columnas.put(ID_STR, CAMPO_ID);
			columnas.put(TABLAORIGEN, CAMPO_TABLAORIGEN);
			columnas.put(CAMPO, CAMPO_CAMPO);
			columnas.put(OPERADOR, CAMPO_OPERADOR);
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(TABLAORIGEN), columnas.get(CAMPO), columnas.get(OPERADOR));
		
        // Devuelve el mapa
        return columnas;
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final HttpServletRequest request) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				//Long idOgenericoLong = (Long) new BasicCellEditor().getValue(item, columnas.get(ID), rowcount);
            	String idOpGenrico = null;
            	String tablaOrigen = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(TABLAORIGEN), rowcount));
				BigDecimal idOperador = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(OPERADOR), rowcount);
				BigDecimal idVista = (BigDecimal)new BasicCellEditor().getValue(item, "id.idvista", rowcount);
				BigDecimal idOperadorCalcOPerm = (BigDecimal)new BasicCellEditor().getValue(item, "id.idoperadores", rowcount);
				BigDecimal isOpCalcOPerm = (BigDecimal)new BasicCellEditor().getValue(item, "id.permitidocalculado", rowcount);
				BigDecimal idCampo = (BigDecimal)new BasicCellEditor().getValue(item, "id.idcampo", rowcount);
				String nombreCampo = (String) new BasicCellEditor().getValue(item, "id.nombrecampo", rowcount);
            	HtmlBuilder html = new HtmlBuilder();
            // botón editar
    			html.a().href().quote().append("javascript:editar('"+idOpGenrico+"','"+isOpCalcOPerm+"','"+idOperadorCalcOPerm+"','"+idCampo+"','"+nombreCampo+"','"+idVista+"','"+tablaOrigen+"','"+idOperador+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Operador\" title=\"Editar Operador\"/>");
                html.aEnd();
                html.append("&nbsp;");
            // botón borrar
	            html.a().href().quote().append("javascript:borrar('"+ idOpGenrico +"','"+isOpCalcOPerm+"','"+idOperadorCalcOPerm+"');").quote().close();
	            html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Operador\" title=\"Borrar Operador\"/>");
	            html.aEnd();
	            html.append("&nbsp;");
		    // botón visulizar
	            html.a().href().quote().append("javascript:visualizar('"+idOpGenrico+"','"+isOpCalcOPerm+"','"+idOperadorCalcOPerm+"','"+idCampo+"','"+nombreCampo+"','"+idVista+"','"+tablaOrigen+"','"+idOperador+"');").quote().close();
                html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar Operador\" title=\"Visualizar Operador\"/>");
                html.aEnd();
                html.append("&nbsp;");    
                return html.toString();
            }
		};
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de los campos permitidos y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade){
		
		// Obtiene el Filter para la búsqueda
		Limit limit = tableFacade.getLimit();
		Collection<VistaMtoinfOperadores> items = new ArrayList<VistaMtoinfOperadores>();
		// Crea el Sort para la búsqueda
        OperadorCampoGenericoSort opGenericoSort = getOpGenericoSort(limit);
		OperadorCampoGenericoFilter opGenericoFilter = getOpGenericoFilter(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getOpGenericoCountWithFilter(opGenericoFilter);
        logger.debug("********** count filas de Operadores Genericos = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        
		// Obtiene los registros que cumplen el filtro
        try {
			items = getOpGenericoWithFilterAndSort(opGenericoFilter, opGenericoSort, rowStart, rowEnd);
			logger.debug("********** list items de Operadores Genericos = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos de la bd en la tabla
        tableFacade.setItems(items); 
    }
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'TotalPorGrupo'
	 * @return
	 */
	private CellEditor getCellEditorOperador() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
		    	int operador = 0;
				try {
					operador = ((BigDecimal)new BasicCellEditor().getValue(item, columnas.get(OPERADOR), rowcount)).intValue();
				} catch (Exception e) {
					logger.error("MtoInformeService - Ocurrió un error al obtener el operdor el OperadorGenerico", e);
				}
				String value = "&nbsp;";	    
		    	switch (operador) {
		    		case ConstantsInf.COD_OPERADOR_BD_IGUAL: value = ConstantsInf.OPERADOR_BD_IGUAL; break;
		    		case ConstantsInf.COD_OPERADOR_BD_MAYOR_QUE: value = ConstantsInf.OPERADOR_BD_MAYOR_QUE; break;	
					case ConstantsInf.COD_OPERADOR_BD_MAYOR_IGUAL_QUE: value = ConstantsInf.OPERADOR_BD_MAYOR_IGUAL_QUE; break;
					case ConstantsInf.COD_OPERADOR_BD_MENOR_QUE: value = ConstantsInf.OPERADOR_BD_MENOR_QUE; break;	
					case ConstantsInf.COD_OPERADOR_BD_MENOR_IGUAL_QUE: value = ConstantsInf.OPERADOR_BD_MENOR_IGUAL_QUE; break;
					case ConstantsInf.COD_OPERADOR_BD_ENTRE: value = ConstantsInf.OPERADOR_BD_ENTRE; break;	
					case ConstantsInf.COD_OPERADOR_BD_CONTENIDO_EN: value = ConstantsInf.OPERADOR_BD_CONTENIDO_EN; break;
					case ConstantsInf.COD_OPERADOR_BD_CAD_EMPIEZAN_POR: value = ConstantsInf.OPERADOR_BD_CAD_EMPIEZAN_POR; break;	
					case ConstantsInf.COD_OPERADOR_BD_CAD_TERMINAN_POR: value = ConstantsInf.OPERADOR_BD_CAD_TERMINAN_POR; break;
					case ConstantsInf.COD_OPERADOR_CAD_CONTIENEN: value = ConstantsInf.OPERADOR_BD_CAD_CONTIENEN; break;	
					default: value = "&nbsp;"; break;
				}
		        HtmlBuilder html = new HtmlBuilder();
		        html.append(value);
		        return html.toString();
		    }
		};
	}
	
	/**
	 * Crea y configura el Filter para la consulta
	 * @param limit
	 * @return
	 */
	private OperadorCampoGenericoFilter getOpGenericoFilter(Limit limit) {
		OperadorCampoGenericoFilter consultaFilter = new OperadorCampoGenericoFilter();
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
	private OperadorCampoGenericoSort getOpGenericoSort(Limit limit) {
		OperadorCampoGenericoSort consultaSort = new OperadorCampoGenericoSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public int getOpGenericoCountWithFilter(OperadorCampoGenericoFilter filter) {
		return mtoOperadorCamposGenericosDao.getOpGenericoCountWithFilter(filter);
	}
	
	public Collection<VistaMtoinfOperadores> getOpGenericoWithFilterAndSort(
			OperadorCampoGenericoFilter filter, OperadorCampoGenericoSort sort, int rowStart, int rowEnd) throws BusinessException {
		
		return mtoOperadorCamposGenericosDao.getOpGenericoWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	public List<Vista> getListadoVistas() {
		List<Vista> lstVistas = mtoVistasService.getListadoVistas();
		return lstVistas;
	}
	
	/**
	 * Devuelve el listado de operados para cargar el combo correspondiente
	 * @return
	 */
	public List<OperadorInforme> getListaOperadores() {
		
		List<OperadorInforme> lista = new ArrayList<OperadorInforme>();
		
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_CAD_CONTIENEN).toString(), ConstantsInf.OPERADOR_BD_CAD_CONTIENEN, false));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_CAD_EMPIEZAN_POR).toString(), ConstantsInf.OPERADOR_BD_CAD_EMPIEZAN_POR, false));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_CAD_TERMINAN_POR).toString(), ConstantsInf.OPERADOR_BD_CAD_TERMINAN_POR, false));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_CONTENIDO_EN).toString(), ConstantsInf.OPERADOR_BD_CONTENIDO_EN, true));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_ENTRE).toString(), ConstantsInf.OPERADOR_BD_ENTRE, true));	
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_IGUAL).toString(), ConstantsInf.OPERADOR_BD_IGUAL, true));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_MAYOR_IGUAL_QUE).toString(), ConstantsInf.OPERADOR_BD_MAYOR_IGUAL_QUE, true));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_MAYOR_QUE).toString(), ConstantsInf.OPERADOR_BD_MAYOR_QUE, true));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_MENOR_IGUAL_QUE).toString(), ConstantsInf.OPERADOR_BD_MENOR_IGUAL_QUE, true));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.COD_OPERADOR_BD_MENOR_QUE).toString(), ConstantsInf.OPERADOR_BD_MENOR_QUE, true));

		
		return lista;
	}
	
	/**
	 * Setter de propiedad
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public void setMtoVistasService(IMtoVistasService mtoVistasService) {
		this.mtoVistasService = mtoVistasService;
	}

	public void setMtoOperadorCamposGenericosDao(
			IMtoOperadorCamposGenericosDao mtoOperadorCamposGenericosDao) {
		this.mtoOperadorCamposGenericosDao = mtoOperadorCamposGenericosDao;
	}

}
