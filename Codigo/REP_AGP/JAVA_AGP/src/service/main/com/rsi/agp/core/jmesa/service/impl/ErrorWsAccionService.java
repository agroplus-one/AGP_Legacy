package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ErrorWsAccionFilter;
import com.rsi.agp.core.jmesa.service.IErrorWsAccionService;
import com.rsi.agp.core.jmesa.sort.ErrorWsAccionSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.CollectionsAndMapsUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.ErrorWSSetFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.filters.commons.ErrorWsIdFiltro;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.models.config.IErrorWsAccionDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.ErrorPerfiles;
import com.rsi.agp.dao.tables.commons.ErrorWs;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.ErrorWsTipo;
import com.rsi.agp.dao.tables.commons.Perfil;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("deprecation")
public class ErrorWsAccionService implements IErrorWsAccionService {
	
	private IErrorWsAccionDao errorWsAccionDao;
	private ILineaDao lineaDao;
	private IEntidadDao entidadDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private String id;
	// Mapa con las columnas del listado y los campos del filtro de busqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private static final Log logger = LogFactory.getLog(ErrorWsAccionService.class);
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String CODERROR = "CODERROR";
	private final static String DESCRIPCION = "DESCRIPCION";
	private final static String COD_TIPO_ERROR = "COD_TIPO_ERROR";
	private final static String DESCRIPCION_TIPO = "DESCRIPCION_TIPO";
	private final static String SERVICIO = "SERVICIO";
	private final static String OCULTAR = "OCULTAR";
	private final static String COD_ENTIDAD = "COD_ENTIDAD";
	private final static String FORZAR_PERFIL = "FORZAR_PERFIL";
	
	/* Pet. 63481 ** MODIF TAM (13/05/2021) ** Inicio */
	private final static String CATALOGO_ERR = "CATALOGO";
	
	private final static String SERVICIO_PD = "PD";
	private final static String SERVICIO_VA = "VA";
	private final static String SERVICIO_AM = "AM";
	private final static String SERVICIO_SN = "SN";
	private final static String SERVICIO_RC = "RC";
	private final static String SERVICIO_PD_DESC = "Paso a definitiva";
	private final static String SERVICIO_VA_DESC = "Validaci&oacute;n";
	private final static String SERVICIO_AM_DESC = "Anexo Mod.";
	private final static String SERVICIO_SN_DESC = "Siniestro";
	private final static String SERVICIO_RC_DESC = "Anexo R.C.";
	
	private final static Character OCULTAR_SI = 'S';
	private final static Character OCULTAR_NO = 'N';
	private final static String OCULTAR_SI_DESC = "S&iacute;";
	private final static String OCULTAR_NO_DESC = "No";
	
	
	@Override
	public Collection<ErrorWsAccion> getErrorWsAccionWithFilterAndSort(ErrorWsAccionFilter filter, ErrorWsAccionSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		
		return errorWsAccionDao.getErrorWsAccionWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	@Override
	public int getConsultaErrorWsCountWithFilter(ErrorWsAccionFilter filter) {
		
		return errorWsAccionDao.getConsultaErrorWsCountWithFilter(filter,false,null);
	}
	

	public String getTablaErrorWsAccion (HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWs, String origenLlamada){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, errorWs, origenLlamada);
		
		// Obtiene el Filter para la busqueda de errores
		Limit limit = tableFacade.getLimit();
		ErrorWsAccionFilter consultaFilter = getConsultaErrorWsAccionFilter(limit);
		
		String listaIdsTodos = getlistaIdsTodos(consultaFilter);
		String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		// Configura el filtro y la ordenacion, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, consultaFilter, limit);
		
		//DAA 12/02/2013
		tableFacade.setToolbar(new CustomToolbarMarcarTodos());
		tableFacade.setView(new CustomView());

		return html (tableFacade) + script;
		
	}
			
	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla de polizas
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWs, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        
        tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Set.class), new ErrorWSSetFilterMatcher());
        
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaErrorWsAccion_LIMIT") != null){
	    			//Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaErrorWsAccion_LIMIT"));
	    		}
    		}
   			//Carga en el TableFacade los filtros de busqueda introducidos en el formulario 
   			cargarFiltrosBusqueda(columnas, errorWs, tableFacade);
    	}                
    	request.getSession().setAttribute("consultaErrorWsAccion_LIMIT",tableFacade.getLimit());

    	return tableFacade;
	}
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(PLAN, "linea.codplan");
			columnas.put(LINEA, "linea.codlinea");
			columnas.put(COD_ENTIDAD, "entidad.codentidad");
			columnas.put(CATALOGO_ERR, "errorWs.id.catalogo");
			columnas.put(CODERROR, "errorWs.id.coderror");
			columnas.put(DESCRIPCION, "errorWs.descripcion");
			columnas.put(COD_TIPO_ERROR, "errorWs.errorWsTipo.codigo");
			columnas.put(DESCRIPCION_TIPO, "errorWs.errorWsTipo.descripcion");
			columnas.put(SERVICIO, "servicio");
			columnas.put(OCULTAR, "ocultar");
			columnas.put(FORZAR_PERFIL, "codErrorPerfiles");
		}	
		tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(PLAN), columnas.get(LINEA), columnas.get(COD_ENTIDAD), 
				columnas.get(CODERROR),columnas.get(DESCRIPCION),columnas.get(DESCRIPCION_TIPO),columnas.get(SERVICIO),
				columnas.get(OCULTAR), columnas.get(FORZAR_PERFIL)); 
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, ErrorWsAccion errorWs, TableFacade tableFacade) {
		
		// PLAN
		if (errorWs.getLinea() != null && FiltroUtils.noEstaVacio (errorWs.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), errorWs.getLinea().getCodplan().toString()));
		// LINEA
		if (errorWs.getLinea() != null && FiltroUtils.noEstaVacio (errorWs.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), errorWs.getLinea().getCodlinea().toString()));
		// COD_ENTIDAD
		if (errorWs.getEntidad() != null && FiltroUtils.noEstaVacio(errorWs.getEntidad().getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COD_ENTIDAD), errorWs.getEntidad().getCodentidad().toString()));		
		
		// Pet. 63481 ** MODIF TAM (13/05/2021) ** Inicio //
		//CATaLOGO
		if (errorWs.getErrorWs() != null && FiltroUtils.noEstaVacio (errorWs.getErrorWs().getId().getCatalogo())) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CATALOGO_ERR), errorWs.getErrorWs().getId().getCatalogo().toString()));
		}
		// Pet. 63481 ** MODIF TAM (13/05/2021) ** Fin //
		
		// CODERROR
		if (errorWs.getErrorWs() != null && FiltroUtils.noEstaVacio (errorWs.getErrorWs().getId().getCoderror()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODERROR), errorWs.getErrorWs().getId().getCoderror().toString()));
		// DESCRIPCION
		if (errorWs.getErrorWs() != null && FiltroUtils.noEstaVacio (errorWs.getErrorWs().getDescripcion()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(DESCRIPCION), errorWs.getErrorWs().getDescripcion()));
		// COD_TIPO_ERROR
		if (errorWs.getErrorWs() != null && errorWs.getErrorWs().getErrorWsTipo() != null && FiltroUtils.noEstaVacio (errorWs.getErrorWs().getErrorWsTipo().getCodigo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COD_TIPO_ERROR), errorWs.getErrorWs().getErrorWsTipo().getCodigo().toString()));
		// SERVICIO
		if (FiltroUtils.noEstaVacio (errorWs.getServicio()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(SERVICIO), errorWs.getServicio().toString()));
		// OCULTAR
		if (FiltroUtils.noEstaVacio (errorWs.getOcultar()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(OCULTAR), errorWs.getOcultar().toString()));
		// LISTA_PERFILES
		if(FiltroUtils.noEstaVacio(errorWs.getListaPerfiles()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FORZAR_PERFIL), errorWs.getListaPerfiles().toString()));
	}
	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos de error y carga el TableFacade con ellas
	 * @param tableFacade
	 * @param limit 
	 * @param consultaFilter 
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, ErrorWsAccionFilter consultaFilter, Limit limit ){

        // Obtiene el numero de filas que cumplen el filtro        
        int totalRows = getConsultaErrorWsCountWithFilter(consultaFilter);
        logger.debug("********** count filas para ErrorWsAccion = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la busqueda ErrorWsAccion
        ErrorWsAccionSort consultaSort = getConsultaErrorWsAccionSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<ErrorWsAccion> items = new ArrayList<ErrorWsAccion>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getErrorWsAccionWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para ErrorWsAccion = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
        // ES NECESARIO VOLVER A OBTENER LOS PERFILES DE CADA REGISTRO
        // SI SE HA FILTRADO POR "FORZAR"
		if (consultaFilter.isFiltroForzar()) {
			for (ErrorWsAccion item : items) {
				item.setCodErrorPerfiles(new HashSet<ErrorPerfiles>(this.errorWsAccionDao.obtenerPerfiles(item)));
			}
		}
        Collection<ErrorWsAccion> itemsNuevos = new ArrayList<ErrorWsAccion>();
        if(tableFacade.getLimit().getSortSet().getSorts().size() != 0) {        	
        	for (Iterator<ErrorWsAccion> iterator = items.iterator(); iterator.hasNext();) {	        	
	        	ErrorWsAccion errorWsAcc = (ErrorWsAccion) iterator.next();
				if (errorWsAcc.getEntidad() == null) {
					errorWsAcc.setEntidad(new Entidad());					
					itemsNuevos.add(errorWsAcc);
				}
				else {
					itemsNuevos.add(errorWsAcc);
				}
			}
        }
		// Carga los registros obtenidos del bd en la tabla
        if(tableFacade.getLimit().getSortSet().getSorts().size() != 0) {
			// Carga los registros obtenidos del bd en la tabla
	        tableFacade.setItems(itemsNuevos);
        } else {
        	tableFacade.setItems(items);
        } 
        
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
        }else{
        	// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ErrorWsAccionService.ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	table.getRow().getColumn(columnas.get(ErrorWsAccionService.SERVICIO)).getCellRenderer().setCellEditor(getCellEditorServicio());
        	table.getRow().getColumn(columnas.get(ErrorWsAccionService.OCULTAR)).getCellRenderer().setCellEditor(getCellEditorOcultar());
        	table.getRow().getColumn(columnas.get(ErrorWsAccionService.FORZAR_PERFIL)).getCellRenderer().setCellEditor(getCellEditorPerfil());
        }
        
    	return tableFacade.render();
    }
    
    
    private CellEditor getCellEditorPerfil() {
    	
    	return new CellEditor() {
			
			@SuppressWarnings("unchecked")
			public Object getValue(Object item, String property, int rowcount) {
				
				StringBuilder cadenaErrorPerfiles = new StringBuilder();
				Set<ErrorPerfiles> codErrorPerfiles = (Set<ErrorPerfiles>) new BasicCellEditor()
						.getValue(item, "codErrorPerfiles", rowcount);
				String[] perfilesStrArr = new String[codErrorPerfiles.size()];
            	int i = 0;
				for(ErrorPerfiles errorPerfil : codErrorPerfiles) {
            		logger.debug("errorPerfil.getPerfil().getAbreviacion() -> " + errorPerfil.getPerfil().getAbreviacion());
            		perfilesStrArr[i++] = errorPerfil.getPerfil().getAbreviacion();
            	}
            	Arrays.sort(perfilesStrArr);
            	
            	for (String str : perfilesStrArr) {
            		cadenaErrorPerfiles.append(str);
            		cadenaErrorPerfiles.append(", ");            		
            	}
            	if (cadenaErrorPerfiles.length() > 0) {
            		cadenaErrorPerfiles.replace(cadenaErrorPerfiles.lastIndexOf(", "), cadenaErrorPerfiles.length(), "");
            	}
            	
            	HtmlBuilder html = new HtmlBuilder();            	
            	html.append(StringUtils.nullToString(cadenaErrorPerfiles).equals("") ? "&nbsp;" : cadenaErrorPerfiles);            			
            	return html.toString();
            }
		};
	}
    
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Servicio'
	 * @return
	 */
	private CellEditor getCellEditorServicio() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
            	String servCode = (String)new BasicCellEditor().getValue(item, "servicio", rowcount);
            	String servicio;
            	if (SERVICIO_PD.equals(servCode)) {
            		servicio = SERVICIO_PD_DESC;
            	} else if (SERVICIO_VA.equals(servCode)) {
            		servicio = SERVICIO_VA_DESC;
            	} else if (SERVICIO_AM.equals(servCode)) {
            		servicio = SERVICIO_AM_DESC;
            	} else if (SERVICIO_SN.equals(servCode)) {
            		servicio = SERVICIO_SN_DESC;
            	} else if (SERVICIO_RC.equals(servCode)) {
            		servicio = SERVICIO_RC_DESC;
            	} else {
            		servicio = "&nbsp;";
            	}

            	return servicio;
            }
		};
	}
	
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Ocultar'
	 * @return
	 */
	private CellEditor getCellEditorOcultar() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
				
            	Character ocultar = (Character)new BasicCellEditor().getValue(item, "ocultar", rowcount);
            	
            	HtmlBuilder html = new HtmlBuilder();
            	html.append(ocultar.equals(OCULTAR_SI) ? OCULTAR_SI_DESC : (ocultar.equals(OCULTAR_NO) ? OCULTAR_NO_DESC : "&nbsp;"));
            			
            	return html.toString();
            }
		};
	}
    

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			
			@SuppressWarnings("unchecked")
			public Object getValue(Object item, String property, int rowcount) {
				
				String cadenaErrorPerfiles = "";
				
				Long id = (Long)new BasicCellEditor().getValue(item, "id", rowcount);
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
				Character catalogo = (Character) new BasicCellEditor().getValue(item, "errorWs.id.catalogo", rowcount);
            	BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
            	String descLinea = new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount)+"";
            	BigDecimal codplan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	BigDecimal codEntidad = (BigDecimal)new BasicCellEditor().getValue(item, "entidad.codentidad", rowcount);
            	String nomEntidad = (String) new BasicCellEditor().getValue(item, "entidad.nomentidad", rowcount);
            	BigDecimal codError = (BigDecimal)new BasicCellEditor().getValue(item, "errorWs.id.coderror", rowcount);
            	String descError=  (String)new BasicCellEditor().getValue(item, "errorWs.descripcion", rowcount);
            	Character codigoTipoError =  (Character)new BasicCellEditor().getValue(item, "errorWs.errorWsTipo.codigo", rowcount);
            	String servicio = (String)new BasicCellEditor().getValue(item, "servicio", rowcount);
            	Character ocultar = (Character)new BasicCellEditor().getValue(item, "ocultar", rowcount);
            	Set<ErrorPerfiles> codErrorPerfiles = new HashSet<ErrorPerfiles>();
            	codErrorPerfiles = (Set<ErrorPerfiles>) new BasicCellEditor().getValue(item, "codErrorPerfiles", rowcount);
            	for(ErrorPerfiles errorPerfil : codErrorPerfiles) {
            		cadenaErrorPerfiles = cadenaErrorPerfiles + ((cadenaErrorPerfiles.length() > 0) ? "," : "") + errorPerfil.getPerfil().getId();            		
            	}
            	
            	HtmlBuilder html = new HtmlBuilder();
            	
            	//DAA 12/02/2013 checkbox
            	html.append("<input type=\"checkbox\" id=\"check_" + id + "\"  name=\"check_" + id + "\" onClick =\"listaCheckId(\'" + id + "')\" class=\"dato\"/>");
                html.append("&nbsp;");
            	
            	// boton editar
            	html.a().href().quote().append("javascript:editar('"+id+"','"+catalogo+"','"+codLinea+"','"+descLinea+"','"+codplan+"','"+codEntidad+"','"+nomEntidad+"','"+codError+"'," +
            			"'"+servicio+"'," + "'"+ocultar+"','"+cadenaErrorPerfiles+"','"+descError+"','"+codigoTipoError+"');").quote().close();
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Error\" title=\"Editar Error\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // boton borrar 
            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Error\" title=\"Borrar Error\"/>");
                html.aEnd();
                html.append("&nbsp;");
	                	
            	return html.toString();
            }
		};
	}
	
	
    /**
	 * Configuracion de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		
		// Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "6%");
    	 	// 1 - Plan
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "6%");
    	// 2- Linea
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "6%");
    	// 3- Linea
    	configColumna(table, columnas.get(COD_ENTIDAD), "Entidad", true, true, "6%");
    	// 4 - Cod. Error
    	configColumna(table, columnas.get(CODERROR), "Cod. Error", true, true, "10%");
    	// 5 - Descripcion
    	configColumna(table, columnas.get(DESCRIPCION), "Descripcion", true, true, "40%");
    	// 6 - Descripcion_Tipo
    	configColumna(table, columnas.get(DESCRIPCION_TIPO), "Tipo", true, true, "10%");
    	// 7 - Servicio 
    	configColumna(table, columnas.get(SERVICIO), "Servicio", true, true, "12%");
    	// 8 - Ocultar
    	configColumna(table, columnas.get(OCULTAR), "Ocultar", true, true, "4%");
    	// 9- Perfil
    	configColumna(table, columnas.get(FORZAR_PERFIL), "Forzar&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", true, false, "17%");
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
	 * Crea y configura el Filter para la consulta de errores
	 * @param limit
	 * @return
	 */
	private ErrorWsAccionFilter getConsultaErrorWsAccionFilter(Limit limit) {
		ErrorWsAccionFilter consultaFilter = new ErrorWsAccionFilter();
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
	 * Crea y configura el Sort para la consulta de polizas
	 * @param limit
	 * @return
	 */
	private ErrorWsAccionSort getConsultaErrorWsAccionSort(Limit limit) {
		
		ErrorWsAccionSort consultaSort = new ErrorWsAccionSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public ErrorWsAccion getErrorWsAccion(Long idErrorWsAccion)
	throws BusinessException {
		try {
			return (ErrorWsAccion) errorWsAccionDao.get(ErrorWsAccion.class, idErrorWsAccion);
		
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener el errorWs: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener el errorWs:",
					dao);
		}
	}
	
	public void bajaErrorWsAccion(ErrorWsAccion errorWs) throws BusinessException {
		try {
			errorWsAccionDao.delete(ErrorWsAccion.class, errorWs.getId());
			logger.debug("ErrorWsAccion borrado  = " + errorWs.getId());
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar el ErrorWsAccion",ex);
		}
	}
	
	
	/**
	 * Realiza las comprobaciones previas y la modificacion del registro de error ws
	 */
	public Map<String, Object> editaErrorWsAccion(ErrorWsAccion errorWsAccionFromView) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			// Comprueba elcodigo de error webservice. Si la validacion es erronea no continua con la modificacion
			if (!checkCodigoErrorWS(errorWsAccionFromView, parameters)){
				return parameters;
			}
		
			// Comprueba si el plan/linea introducido existe
			Linea linea = lineaDao.getLinea(errorWsAccionFromView.getLinea().getCodlinea(), errorWsAccionFromView.getLinea().getCodplan());
			
			if (linea != null && StringUtils.nullToString(linea.getLineaseguroid()).equals("")) {
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.lineaseguroid.KO"));	
			} else {
				if(!exisiteEntidad(errorWsAccionFromView)){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.edicion.entidad.KO"));
					return parameters;
				}
				
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
				if (errorWsAccionFromView.getErrorWs().getId().getCatalogo().equals('P') && errorWsAccionFromView.getServicio().equals(SERVICIO_SN)) {
					parameters.put(Constants.KEY_ALERTA, "Catalogo del Error y Servicio Incompatibles para el Alta");
					return parameters;
				}else if (errorWsAccionFromView.getErrorWs().getId().getCatalogo().equals('S') && !errorWsAccionFromView.getServicio().equals(SERVICIO_SN)) {
					parameters.put(Constants.KEY_ALERTA, "Catalogo del Error y Servicio Incompatibles para el Alta");
					return parameters;
				}
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Fin */
				
				Long id = errorWsAccionDao.getErrorWS(errorWsAccionFromView);
				errorWsAccionDao.evict(errorWsAccionFromView);
				if(id!=null && id.compareTo(errorWsAccionFromView.getId())!=0){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.existente.alta.KO"));
				}else{
					// recupero el objecto original de la base de datos
					ErrorWsAccion errorWsAccionFromDB = (ErrorWsAccion)errorWsAccionDao.getObject(ErrorWsAccion.class, errorWsAccionFromView.getId());
					Character ocultar = errorWsAccionFromView.getOcultar();
					// borro los errores perfiles existentes
					while(errorWsAccionFromDB.getCodErrorPerfiles().iterator().hasNext()){
						eliminarErrorPerfil(errorWsAccionFromDB);
					}
					if(ocultar != null) {
						errorWsAccionFromDB.setOcultar(ocultar);
					}
					// agrego los errores perfiles nuevos si ocultar es igual a no
					Iterator<String> itr = getListaPerfiles(errorWsAccionFromView).iterator();
					while(ocultar.equals(OCULTAR_NO) && itr.hasNext()){
						agregarErrorPerfil(errorWsAccionFromDB, itr);
					}
					errorWsAccionFromDB.setLinea(linea);
					
					// modifico la entidad 
					Entidad entidadView = errorWsAccionFromView.getEntidad();
					if(entidadView.getCodentidad() != null ){
						Entidad entidadDB = (Entidad) entidadDao.get(Entidad.class, entidadView.getCodentidad());
						errorWsAccionFromDB.setEntidad(entidadDB);
						entidadDao.evict(entidadDB);
					} else {
						errorWsAccionFromDB.setEntidad(null);
					}
					
					errorWsAccionFromDB.setServicio(errorWsAccionFromView.getServicio());
					/* Pet. 63481 ** MODIF TAM (13.05.2021) ** Inicio */
					errorWsAccionFromDB.getErrorWs().getId().setCatalogo(errorWsAccionFromView.getErrorWs().getId().getCatalogo());
					errorWsAccionFromDB.getErrorWs().getId().setCoderror(errorWsAccionFromView.getErrorWs().getId().getCoderror());
					/* Pet. 63481 ** MODIF TAM (13.05.2021) ** Fin */
					
					errorWsAccionFromDB.getErrorWs().setDescripcion(errorWsAccionFromView.getErrorWs().getDescripcion());
					errorWsAccionFromDB.getErrorWs().getErrorWsTipo().setCodigo(errorWsAccionFromView.getErrorWs().getErrorWsTipo().getCodigo());					
					// persisto los cambio en BBDD
					errorWsAccionDao.evict(errorWsAccionFromDB);
					errorWsAccionDao.saveOrUpdate(errorWsAccionFromDB);
					errorWsAccionDao.evict(errorWsAccionFromDB);	
					logger.debug("ErrorWsAccion modificado con  id = " + errorWsAccionFromDB.getId());
					parameters.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.errorWs.edicion.OK"));
				}
			}
				
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar el ErrorWsAccion", ex);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
		} catch (Exception ex) {
			logger.debug("Error al editar el ErrorWsAccion", ex);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
		}
		

		return parameters;
	}

	/**
	 * Funcion que elimina los errores perfiles de un ErrorWsAccion dado. Por ahora se usa en
	 * la modificacion y el cambio masivo
	 * @param errorWsAccion
	 * @throws DAOException
	 */
	private void eliminarErrorPerfil(ErrorWsAccion errorWsAccion)
			throws DAOException {
		ErrorPerfiles item = errorWsAccion.getCodErrorPerfiles().iterator().next();
		errorWsAccion.getCodErrorPerfiles().remove(item);
		errorWsAccionDao.delete(item);
		errorWsAccionDao.evict(item);
	}

	/**
	 * Funcion que agrega nuevos errores perfiles a un ErrorWsAccion dado. Por ahora se usa en
	 * la modificacion y el cambio masivo
	 * @param errorWsAccion
	 * @param itr
	 */
	private void agregarErrorPerfil(ErrorWsAccion errorWsAccion,
			Iterator<String> itr) {
		String perfilIdString = itr.next();
		Perfil perfil = new Perfil();
		perfil.setId(new BigDecimal(perfilIdString));
		
		ErrorPerfiles errorPerfiles = new ErrorPerfiles();
		
		errorPerfiles.setPerfil(perfil);
		errorPerfiles.setErrorWsAccion(errorWsAccion);
		errorWsAccion.getCodErrorPerfiles().add(errorPerfiles);
	}

	public Map<String, Object> altaErrorWs(ErrorWsAccion errorWsAccion) throws BusinessException {
		
		logger.debug("ErrorWsAccionService - altaErrorWs [INIT]");
		
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ErrorWs errorWs = errorWsAccion.getErrorWs();
		logger.debug("Valor de errorWs.getId().getCoderror(): "+errorWs.getId().getCoderror() +"-");
		logger.debug("Valor de errorWs.getId().getCatalogo(): "+errorWs.getId().getCatalogo() +"-");
		logger.debug("Valor errorWs.getDescripcion(): " + errorWs.getDescripcion() +"-");
		try {
			if(!exisiteEntidad(errorWsAccion)){
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.edicion.entidad.KO"));
				return parameters;
			}
			errorWsAccionDao.saveOrUpdate(errorWs);
			errorWsAccionDao.evict(errorWs);
		} catch (DAOException e) {
			logger.debug("Error al dar de alta el ErrorWs", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.KO"));
		}
		logger.debug("ErrorWsAccionService - altaErrorWs [INIT]");
		
		return parameters;
	}	
	
	
	/**
	 * Realiza las comprobaciones previas y el alta del registro de error ws
	 */
	public Map<String, Object> altaErrorWsAccion(ErrorWsAccion errorWs) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			// Comprueba elcodigo de error webservice. Si la validacion es erronea no continua con el alta
			//if (!checkCodigoErrorWS(errorWs, parameters)) return parameters;
			
			// Comprueba si el plan/linea introducido existe
			logger.debug("altaErrorWsAccion - plan: " + errorWs.getLinea().getCodplan() + ", linea: " + errorWs.getLinea().getCodlinea());
			Linea linea = lineaDao.getLinea(errorWs.getLinea().getCodlinea(), errorWs.getLinea().getCodplan());
			
			if (linea == null || linea.getLineaseguroid() == null) {
				// El plan/linea introducido es erroneo, muestra el mensaje de error y cancela el proceso
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.lineaseguroid.KO"));	
				return parameters;
			} else {
				if(!exisiteEntidad(errorWs)){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.edicion.entidad.KO"));
					return parameters;
				}
				 
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
				if (errorWs.getErrorWs().getId().getCatalogo().equals('P') && errorWs.getServicio().equals(SERVICIO_SN)) {
					parameters.put(Constants.KEY_ALERTA, "Catalogo del Error y Servicio Incompatibles para el Alta");
					return parameters;
				}else if (errorWs.getErrorWs().getId().getCatalogo().equals('S') && !errorWs.getServicio().equals(SERVICIO_SN)) {
					parameters.put(Constants.KEY_ALERTA, "Catalogo del Error y Servicio Incompatibles para el Alta");
					return parameters;
				}
				/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Fin */
				
				if (existeErrorWs(errorWs, false) > 0){
					parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.existente.alta.KO"));
				}
	            else {
					if (errorWs.getId() != null){
						errorWs.setId(null);
					}
					
					List<Perfil> listaPerfiles = new ArrayList<Perfil>();
					if(errorWs.getListaPerfiles() != null) {
						for(String perf : errorWs.getListaPerfiles()) {
							Perfil perfil = new Perfil();
							perfil.setId(new BigDecimal(perf));
							listaPerfiles.add(perfil);
						}
						
						for(Perfil perfil : listaPerfiles) {
							ErrorPerfiles errPerf = new ErrorPerfiles();
							errPerf.setPerfil(perfil);
							errPerf.setErrorWsAccion(errorWs);
							errorWs.getCodErrorPerfiles().add(errPerf);
						}
					}
					
					
					//ErrorWs errWs= new ErrorWs();
					//errWs.setCoderror(errorWs.getErrorWs().set)
					
					errorWs.setLinea(linea);
					if(errorWs.getEntidad().getCodentidad() == null ) {
						errorWs.setEntidad(null);
					}
					errorWsAccionDao.saveOrUpdate(errorWs);
					errorWsAccionDao.evict(errorWs);
	            }
			}
		} 
		catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta el ErrorWsAccion", ex);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.existente.alta.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta el ErrorWsAccion", ex);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.KO"));
		}
		
		return parameters;
	}

	private boolean exisiteEntidad(ErrorWsAccion errorWs) throws DAOException {
		BigDecimal codEntidad = errorWs.getEntidad().getCodentidad();
		if(codEntidad == null){
			return true;
		}
		return entidadDao.existeEntidad(codEntidad);
	}

	/**
	 * Comprueba si elcodigo de error ws introducido es valido y existe en la tabla correspondiente
	 * @param errorWs
	 * @param parameters Mapa de parametros donde se incluira el mensaje de error si elcodigo no es correcto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean checkCodigoErrorWS(ErrorWsAccion errorWs,	Map<String, Object> parameters) {
		// Comprueba si elcodigo de error existe
		ErrorWsIdFiltro errorWsidFiltro = new ErrorWsIdFiltro(null, null);
		
		List<ErrorWs> list = null;
		if (errorWs != null && errorWs.getErrorWs() != null && errorWs.getErrorWs().getId().getCoderror() != null) {
			/* Pet. 63481 ** MODIF TAM (13.05.2021) ** Inicio */
			errorWsidFiltro.setCatalogo(errorWs.getErrorWs().getId().getCatalogo());
			errorWsidFiltro.setCodError(errorWs.getErrorWs().getId().getCoderror());
			//list = errorWsAccionDao.getObjects(ErrorWs.class, "coderror", errorWs.getErrorWs().getId().getCoderror());//
			list = errorWsAccionDao.getErrores(errorWsidFiltro);
		}
		
		if (list == null || list.size() == 0) {
			// Elcodigo de error introducido es erroneo, muestra el mensaje de error y cancela el proceso
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.coderror.KO"));
			return false;
		}
		
		return true;
	}
	/**
	 * Comprueba si ya existe un registro con el mismo plan/linea,codigo de error y servicio que el del objeto pasado como parametro
	 * @param errorWs
	 * @param isCambioMasivo Indica si se esta realizando un cambio masivo
	 * @return
	 */
	private int existeErrorWs(ErrorWsAccion errorWs, boolean isCambioMasivo) {
		
		ErrorWsAccionFilter filtro = new ErrorWsAccionFilter(); 
        
		filtro.addFilter("linea.codlinea", errorWs.getLinea().getCodlinea());
        filtro.addFilter("linea.codplan", errorWs.getLinea().getCodplan());
        filtro.addFilter("errorWs.id.coderror", errorWs.getErrorWs().getId().getCoderror());
        filtro.addFilter("errorWS.id.catalogo", errorWs.getErrorWs().getId().getCatalogo());
        filtro.addFilter("servicio", errorWs.getServicio());
        if(errorWs.getEntidad() != null) {
        	filtro.addFilter("entidad.codentidad", errorWs.getEntidad().getCodentidad());
        }
        return errorWsAccionDao.getConsultaErrorWsCountWithFilter(filtro, isCambioMasivo, errorWs.getId());
		
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setErrorWsAccionDao(IErrorWsAccionDao errorWsAccionDao) {
		this.errorWsAccionDao = errorWsAccionDao;
	}
	
	public void setEntidadDao(IEntidadDao entidadDao){
		this.entidadDao = entidadDao;
	}
	
	/** Obtiene el Cod y la Desc de los errores Ws.
	 * 
	 */
	public List<ErrorWs> getTodosErrores() {
		
		List<ErrorWs> errores = errorWsAccionDao.getTodosErrores();
		
		return errores;
	}

	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest, String servicioOrig, String servicioDest) throws BusinessException {

		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Obtiene el lineaseguroid de los plan/linea origen y destino
		try {
			// Validacion del plan/linea origen
			Long lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(lineaOrig, planOrig);
			if (lineaSeguroIdOrigen == null) {
				logger.debug("El plan/linea origen no existe, no se continua con la replica");
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.replica.planlinea.origen.KO"));
				return parameters;
			}
			
			// Validacion del plan/linea destino
			Long lineaSeguroIdDestino = lineaDao.getLineaSeguroId(lineaDest, planDest);
			if (lineaSeguroIdDestino == null) {
				logger.debug("El plan/linea destino no existe, no se continua con la replica");
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.replica.planlinea.destino.KO"));
				return parameters;
			}
			
			// Valida que el plan/linea destino no tenga errores ws dados de alta previamente
			ErrorWsAccionFilter filter = new ErrorWsAccionFilter();
			filter.addFilter("linea.codplan", planDest);
			filter.addFilter("linea.codlinea", lineaDest);
			if(!servicioDest.isEmpty()) {
				filter.addFilter("servicio", servicioDest);
			}
			if (getConsultaErrorWsCountWithFilter(filter) != 0) {
				logger.debug("El plan/linea destino tiene errores ws dados de alta, no se continua con la replica");
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.replica.planlinea.KO"));
				return parameters;
			}
			
			// Llamada al metodo del DAO que realiza la replica
			errorWsAccionDao.replicar(new BigDecimal(lineaSeguroIdOrigen), new BigDecimal(lineaSeguroIdDestino), servicioOrig, servicioDest);
		} 
		catch (DAOException e) {
			logger.error("Ocurrio un error al replicar los errores webservice", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.replica.KO"));
			return parameters;
		}
		
		// Si llega hasta aqui, el proceso de replica ha finalizado correctamente
		logger.debug("El proceso de replica ha finalizado correctamente");
		parameters.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.errorWs.replica.OK"));
		return parameters;
	}
	
	/** DAA 12/02/2013  Metodo para recuperar un String con todos los Ids de ErrorWs segun el filtro
	 * 
	 * @param consultaFilter
	 * @return listaIdsTodos
	 */
	public String getlistaIdsTodos(ErrorWsAccionFilter consultaFilter) {
		String listaIdsTodos =errorWsAccionDao.getlistaIdsTodos(consultaFilter);
		return listaIdsTodos;
		
	}
	
	/** DAA 13/02/2013  Metodo para actualizar un String con todos los Ids de ErrorWs segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param errorWsAccionBean
	 */
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, ErrorWsAccion errorWsAccionBean) throws DAOException {
		
		Map<String, String> parameters = new HashMap<String, String>();
		boolean actualizado = false;
		boolean noActualizado = false;
		
		try {
			String[] ids = listaIdsMarcados_cm.substring(0, listaIdsMarcados_cm.length() - 1).split(",");
			
			ErrorWsAccion errorWs = new ErrorWsAccion();
			
			// recorro la lista de ids
			for(String id : ids){
				// cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
				errorWs = (ErrorWsAccion) errorWsAccionDao.getObject(ErrorWsAccion.class, new Long(id));
				// si hay Errores Perfiles anteriores los elimino
				while(errorWs.getCodErrorPerfiles().iterator().hasNext()){
					eliminarErrorPerfil(errorWs);
				}
				Character ocultar = errorWsAccionBean.getOcultar();
				if(ocultar != null) {
					errorWs.setOcultar(ocultar);
				}
				if(existeErrorWs(errorWs, true) > 0) {
					noActualizado = true;
				} else {
					Iterator<String> itr = getListaPerfiles(errorWsAccionBean).iterator();
					while(itr.hasNext()){
						agregarErrorPerfil(errorWs,	itr);
					}
					errorWsAccionDao.evict(errorWs);
					errorWsAccionDao.saveOrUpdate(errorWs);
					errorWsAccionDao.evict(errorWs);
					actualizado = true;
				}
			}
			if (noActualizado){
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
			}
			if (actualizado){
				parameters.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.errorWs.edicion.OK"));
			}
		} catch (DAOException e) {
			logger.debug("Error al ejecutar el Cambio Masivo ", e);
		}
		return parameters;		
	}
	
	private List<String> getListaPerfiles(ErrorWsAccion errorWsAccion){
		List<String> listaPerfiles = errorWsAccion.getListaPerfiles();
		if(CollectionsAndMapsUtil.isEmpty(listaPerfiles)){
			List<String> returnList = new ArrayList<String>();
			return returnList;
		}
		return listaPerfiles;
	}

	/** DAA 05/03/2013  Recupera el limit de sesion para pasarlo al bean de cambio Masivo.
	 * @param Limit
	 * @return ErrorWsAccion
	 */
	public ErrorWsAccion getCambioMasivoBeanFromLimit(Limit consultaErrorWsAccion_LIMIT) {
		ErrorWsAccion cambioMasivoErrorWsAccionBean = new ErrorWsAccion();
		
		// PLAN
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(PLAN))){
			cambioMasivoErrorWsAccionBean.getLinea().setCodplan(new BigDecimal (consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(PLAN)).getValue()));
		}
		// LINEA
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(LINEA))){
			cambioMasivoErrorWsAccionBean.getLinea().setCodlinea(new BigDecimal (consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(LINEA)).getValue()));
		}
		// CODERROR
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(CODERROR))){
			cambioMasivoErrorWsAccionBean.getErrorWs().getId().setCoderror(new BigDecimal (consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(CODERROR)).getValue()));
		}
		
		// Pet. 63481 ** MODIF TAM(13.05.2021) ** Inicio //
		// CATALOGO 
		// Pet. 63481 ** MODIF TAM(13.05.2021) ** Fin  //
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(CATALOGO_ERR))){
			cambioMasivoErrorWsAccionBean.getErrorWs().getId().setCatalogo(consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(CATALOGO_ERR)).getValue().charAt(0));
		}
		// Pet. 63481 ** MODIF TAM(13.05.2021) ** Fin //
		
		// DESCRIPCION
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(DESCRIPCION))){
			cambioMasivoErrorWsAccionBean.getErrorWs().setDescripcion(consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(DESCRIPCION)).getValue());
		}
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(COD_TIPO_ERROR))){
			cambioMasivoErrorWsAccionBean.getErrorWs().getErrorWsTipo().setCodigo(consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(COD_TIPO_ERROR)).getValue().charAt(0));
		}
		// SERVICIO
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(SERVICIO))){
			cambioMasivoErrorWsAccionBean.setServicio(consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(SERVICIO)).getValue());
		}
		// OCULTAR
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(OCULTAR))){
			cambioMasivoErrorWsAccionBean.setOcultar(consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(OCULTAR)).getValue().charAt(0));
		}
		// FORZAR
		if(null != consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(FORZAR_PERFIL))){
			String val = consultaErrorWsAccion_LIMIT.getFilterSet().getFilter(columnas.get(FORZAR_PERFIL)).getValue()
					.replace("[", "").replace("]", "").replace(" ", "");
			cambioMasivoErrorWsAccionBean.setListaPerfiles(Arrays.asList(val.split(",")));
		}
				
		return cambioMasivoErrorWsAccionBean;
	}

	@Override
	public List<ErrorWsTipo> obtenerListaTiposWsError() throws DAOException {
		
		List<ErrorWsTipo> listaErrorWsTipo = errorWsAccionDao.obtenerListaTiposWsError();
		return listaErrorWsTipo;
	}
	
	@Override
	public List<Perfil> obtenerListaPerfiles() throws DAOException {
		
		List<Perfil> listaPerfiles = errorWsAccionDao.obtenerListaPerfiles();
		return listaPerfiles;
	}
	
	
}