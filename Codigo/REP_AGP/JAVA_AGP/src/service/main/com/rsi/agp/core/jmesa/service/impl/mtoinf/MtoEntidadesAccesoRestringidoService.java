package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.EntidadAccesoRestringidoFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoEntidadesAccesoRestringidoService;
import com.rsi.agp.core.jmesa.sort.EntidadAccesoRestringidoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoEntidadesAccesoRestringidoDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

@SuppressWarnings("deprecation")
public class MtoEntidadesAccesoRestringidoService implements IMtoEntidadesAccesoRestringidoService {
	
	private IMtoEntidadesAccesoRestringidoDao mtoEntidadesAccesoRestringidoDao;	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private final String ID_STR = "ID";
	private final String CODIGO = "CODIGO";
	private final String ENTIDAD = "ENTIDAD";
	private final String ACCESO_DISENADOR = "ACCESO_DISENADOR";
	private final String ACCESO_GENERADOR = "ACCESO_GENERADOR";


	@Override
	public int getEntidadAccesoRestringidoCountWithFilter(EntidadAccesoRestringidoFilter filter) {
		return mtoEntidadesAccesoRestringidoDao.getEntidadAccesoRestringidoCountWithFilter(filter);
	}

	@Override
	public Collection<EntidadAccesoRestringido> getEntidadAccesoRestringidoWithFilterAndSort(
			EntidadAccesoRestringidoFilter filter,
			EntidadAccesoRestringidoSort sort, int rowStart, int rowEnd) {
		return mtoEntidadesAccesoRestringidoDao.getEntidadAccesoRestringidoWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	@Override
	public String getTablaEntidadesAccesoRestringido(HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, entidadAccesoRestringido);

		// Configura el filtro y la ordenación, busca las entidades y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade, request);
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de entidades con acceso restringido
	 * @param request
	 * @param response
	 * @param entidadAccesoRestringido
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null) {
    		if (request.getSession().getAttribute("entidadAccesoRestringido_LIMIT") != null && request.getParameter("origenLlamada") == null){
    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
    			tableFacade.setLimit((Limit) request.getSession().getAttribute("entidadAccesoRestringido_LIMIT"));
    		} else{
    				// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    				cargarFiltrosBusqueda(columnas, entidadAccesoRestringido, tableFacade);
    		}
    	}                
        return tableFacade;
		
	}
	
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de las entidades y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade){
		
		// Obtiene el Filter para la búsqueda de entidades
		Limit limit = tableFacade.getLimit();
		Collection<EntidadAccesoRestringido> items = new ArrayList<EntidadAccesoRestringido>();
		
		// Crea el Sort para la búsqueda de entidades
        EntidadAccesoRestringidoSort entidadAccesoRestringidoSort = getEntidadAccesoRestringidoSort(limit);
		
        // Crea el Filter para la búsqueda de entidades
        EntidadAccesoRestringidoFilter entidadAccesoRestringidoFilter = getEntidadAccesoRestringidoFilter(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getEntidadAccesoRestringidoCountWithFilter(entidadAccesoRestringidoFilter);
        logger.debug("Numero de registros de entidades con acceso restringido que cumplen el filtro: " + totalRows);
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        
		// Obtiene los registros que cumplen el filtro
		items = getEntidadAccesoRestringidoWithFilterAndSort(entidadAccesoRestringidoFilter, entidadAccesoRestringidoSort, rowStart, rowEnd);
		logger.debug("Numero de registros en la lista de entidades con acceso restringido que cumplen el filtro: " + items.size());
		
			// Carga los registros obtenidos de la bd en la tabla
        tableFacade.setItems(items); 
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
        	// Columna acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(request));   
        	// Columna Acceso al diseñador
        	table.getRow().getColumn(columnas.get(ACCESO_DISENADOR)).getCellRenderer().setCellEditor(getCellEditorAccesoDisenador(request));
        	// Columna Acceso al generador
        	table.getRow().getColumn(columnas.get(ACCESO_GENERADOR)).getCellRenderer().setCellEditor(getCellEditorAccesoGenerador(request));
        }
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
    
    /**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// 1 - Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "5%");
    	// 2 - Código de entidad
    	configColumna(table, columnas.get(CODIGO), "Codigo", true, true, "10%");
    	// 3 - Nombre de entidad
    	configColumna(table, columnas.get(ENTIDAD), "Entidad", true, true, "30%");
    	// 4 - Acceso al diseñador
    	configColumna(table, columnas.get(ACCESO_DISENADOR), "Acceso al dise&ntilde;ador", true, true, "25%");
    	// 5 - Acceso al generador
    	configColumna(table, columnas.get(ACCESO_GENERADOR), "Acceso al generador", true, true, "30%");
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
			columnas.put(CODIGO, "entidad.codentidad");
			columnas.put(ENTIDAD, "entidad.nomentidad");
			columnas.put(ACCESO_DISENADOR, "accesoDisenador");
			columnas.put(ACCESO_GENERADOR, "accesoGenerador");
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(CODIGO), columnas.get(ENTIDAD), columnas.get(ACCESO_DISENADOR),
				columnas.get(ACCESO_GENERADOR)); 
        // Devuelve el mapa
        return columnas;
	}
	
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
		 
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, EntidadAccesoRestringido entidadAccesoRestringido, TableFacade tableFacade) {
		
		// Id
		if (FiltroUtils.noEstaVacio (entidadAccesoRestringido.getId()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ID_STR), entidadAccesoRestringido.getId().toString()));
		// Código de entidad
		if (FiltroUtils.noEstaVacio (entidadAccesoRestringido.getEntidad().getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODIGO), entidadAccesoRestringido.getEntidad().getCodentidad().toString()));
		// Nombre de entidad
		if (FiltroUtils.noEstaVacio (entidadAccesoRestringido.getEntidad().getNomentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ENTIDAD), entidadAccesoRestringido.getEntidad().getNomentidad()));
		// Acceso al diseñador
		if (FiltroUtils.noEstaVacio (entidadAccesoRestringido.getAccesoDisenador()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ACCESO_DISENADOR), entidadAccesoRestringido.getAccesoDisenador().toString()));
		// Acceso al generador
		if (FiltroUtils.noEstaVacio (entidadAccesoRestringido.getAccesoGenerador()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ACCESO_GENERADOR), entidadAccesoRestringido.getAccesoGenerador().toString()));
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final HttpServletRequest request) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				// Obtiene los valores de la fila
				// Id.
				Long idNum = (Long) new BasicCellEditor().getValue(item, columnas.get(ID_STR), rowcount);
            	String id = (idNum != null) ? idNum.toString() : "";
            	// Código de entidad
            	String codigo = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(CODIGO), rowcount));
            	// Nombre de entidad
            	String entidad = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(ENTIDAD), rowcount));
            	// Acceso al diseñador
            	BigDecimal accesoDsnNum = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(ACCESO_DISENADOR), rowcount);
            	String accesoDsn = (accesoDsnNum != null) ? accesoDsnNum.toString() : "";
            	// Acceso al diseñador
            	BigDecimal accesoGenNum = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(ACCESO_GENERADOR), rowcount);
            	String accesoGen = (accesoGenNum != null) ? accesoGenNum.toString() : "";
				
            	HtmlBuilder html = new HtmlBuilder();
            	// botón editar
    			html.a().href().quote().append("javascript:editar('"+id+"','"+codigo+"','"+entidad+"','"+accesoDsn+"', '"+accesoGen+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Entidad con Acceso Restringido\" title=\"Editar Entidad con Acceso Restringido\"/>");
                html.aEnd();
                html.append("&nbsp;");
                    
                // botón borrar
	            html.a().href().quote().append("javascript:borrar('"+ id +"');").quote().close();
	            html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Entidad con Acceso Restringido\" title=\"Borrar Entidad con Acceso Restringido\"/>");
	            html.aEnd();
	            html.append("&nbsp;");
			            
		         // botón visulizar
	            html.a().href().quote().append("javascript:visualizar('"+id+"','"+codigo+"','"+entidad+"','"+accesoDsn+"', '"+accesoGen+"');").quote().close();
	            html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar Entidad con Acceso Restringido\" title=\"Visualizar Entidad con Acceso Restringido\"/>");
	            html.aEnd();
	            html.append("&nbsp;");    
		            
                return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acceso al diseñador'
	 * @return
	 */
	private CellEditor getCellEditorAccesoDisenador (final HttpServletRequest request) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				// Acceso al diseñador
            	BigDecimal accesoDsnNum = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(ACCESO_DISENADOR), rowcount);
            	String accesoDsn = (accesoDsnNum != null) ? accesoDsnNum.toString() : "";
            	
            	HtmlBuilder html = new HtmlBuilder();
            	html.append(ConstantsInf.ACCESO_DENEGADO.toString().equals(accesoDsn) ? "Denegado" 
            			 : (ConstantsInf.ACCESO_PERMITIDO.toString().equals(accesoDsn)) ? "Permitido" : "");    
	            
                return html.toString();
				
			}
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acceso al generador'
	 * @return
	 */
	private CellEditor getCellEditorAccesoGenerador (final HttpServletRequest request) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				// Acceso al generador
            	BigDecimal accesoGenNum = (BigDecimal)new BasicCellEditor().getValue(item, columnas.get(ACCESO_GENERADOR), rowcount);
            	String accesoGen = (accesoGenNum != null) ? accesoGenNum.toString() : "";
            	
            	HtmlBuilder html = new HtmlBuilder();
            	html.append(ConstantsInf.ACCESO_DENEGADO.toString().equals(accesoGen) ? "Denegado" 
            			 : (ConstantsInf.ACCESO_PERMITIDO.toString().equals(accesoGen)) ? "Permitido" 
            			 : (ConstantsInf.ACCESO_PERMITIDO_USU_PER_CONCRETOS.toString().equals(accesoGen)) ? "Permitido a Usuarios y Perfiles concretos" : "");
	            
                return html.toString();
				
			}
		};
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
	 * Crea y configura el Sort para la consulta de Entidades 
	 * @param limit
	 * @return
	 */
	private EntidadAccesoRestringidoSort getEntidadAccesoRestringidoSort(Limit limit) {
		EntidadAccesoRestringidoSort consultaSort = new EntidadAccesoRestringidoSort();
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
	 * Crea y configura el Filter para la consulta de Entidades
	 * @param limit
	 * @return
	 */
	private EntidadAccesoRestringidoFilter getEntidadAccesoRestringidoFilter(Limit limit) {
		EntidadAccesoRestringidoFilter consultaFilter = new EntidadAccesoRestringidoFilter();
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
	public boolean bajaEntidadAccesoRestringido(EntidadAccesoRestringido entidadAccesoRestringido) {
		
		try {
			mtoEntidadesAccesoRestringidoDao.delete (entidadAccesoRestringido);
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al dar de baja la entidad con acceso restringido", e);
			return false;
		}

		return true;
	}
	
	@Override
	public Map<String, Object> altaEntidadAccesoRestringido(EntidadAccesoRestringido entidadAccesoRestringido) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {
			// Comprueba si el código de entidad introducido se corresponde con una entidad dada de alta
			if (!existeEntidad(entidadAccesoRestringido)) {
				logger.debug("La entidad introducida no existe");
				parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_ENT_NO_EXISTE));
				return parametros;
			}
			
			// Si la entidad no se ha creado previamente se da de alta
			if (!mtoEntidadesAccesoRestringidoDao.checkEntidadAccesoRestringido (entidadAccesoRestringido.getEntidad().getCodentidad(), null)) {
				mtoEntidadesAccesoRestringidoDao.saveOrUpdate(entidadAccesoRestringido);
				parametros.put("mensaje", bundle.getObject(ConstantsInf.MSG_ENT_ACCESO_RESTRINGIDO_ALTA_OK));
			}
			// Si ya existe, no se da de alta y se muestra el mensaje
			else {
				parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_ALTA_EXISTE_KO));
				logger.debug("La entidad con acceso restringido ya existe");
			}
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al dar de alta la entidad con acceso restringido", e);
			parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_ALTA_KO));
		}

		return parametros;
	}

	/**
	 * Devuelve un boolean indicando si la entidad introducida existe en BD o no
	 * @param entidadAccesoRestringido
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean existeEntidad(EntidadAccesoRestringido entidadAccesoRestringido) {
		List<Entidad> lista = mtoEntidadesAccesoRestringidoDao.getObjects(Entidad.class, "codentidad", entidadAccesoRestringido.getEntidad().getCodentidad());
		return (lista != null && lista.size() >= 1);
	}
	
	@Override
	public Map<String, Object> editaEntidadAccesoRestringido(EntidadAccesoRestringido entidadAccesoRestringido) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {
			// Comprueba si el código de entidad introducido se corresponde con una entidad dada de alta
			if (!existeEntidad(entidadAccesoRestringido)) {
				logger.debug("La entidad introducida no existe");
				parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_ENT_NO_EXISTE));
				return parametros;
			}
			
			// Si ya existe una entidad con el mismo código
			if (!mtoEntidadesAccesoRestringidoDao.checkEntidadAccesoRestringido (entidadAccesoRestringido.getEntidad().getCodentidad(), new Long (entidadAccesoRestringido.getId()).longValue())) {
				mtoEntidadesAccesoRestringidoDao.saveOrUpdate(entidadAccesoRestringido);
				parametros.put("mensaje", bundle.getObject(ConstantsInf.MSG_ENT_ACCESO_RESTRINGIDO_MODIF_OK));
			}
			// Si ya existe, no se da de alta y se muestra el mensaje
			else {
				parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_ALTA_EXISTE_KO));
				logger.debug("La entidad con acceso restringido ya existe");
			}
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al modificar la entidad con acceso restringido", e);
			parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_MODIF_KO));
		}

		return parametros;
	}
	
	@Override
	public EntidadAccesoRestringido getEntidadAccesoRestringido(Long idEntidad) {
		try {
			return (EntidadAccesoRestringido)mtoEntidadesAccesoRestringidoDao.get(EntidadAccesoRestringido.class, idEntidad);
		} catch (DAOException e) {
			logger.error("Ocurrio un error al obtener la entidad con acceso restringido con id: " + idEntidad, e);
			return null;
		}
	}

	/**
	 * Setter de propiedad para Spring
	 * @return
	 */
	public void setMtoEntidadesAccesoRestringidoDao(IMtoEntidadesAccesoRestringidoDao mtoEntidadesAccesoRestringidoDao) {
		this.mtoEntidadesAccesoRestringidoDao = mtoEntidadesAccesoRestringidoDao;
	}	

	/**
	 * Setter de propiedad para Spring
	 * @return
	 */
	public void setId(String id) {
		this.id = id;
	}
}
