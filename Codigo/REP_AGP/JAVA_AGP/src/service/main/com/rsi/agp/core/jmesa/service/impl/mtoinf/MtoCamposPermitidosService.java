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
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CamposPermitidosFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCamposPermitidosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadoresCamposPermitidosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoVistasService;
import com.rsi.agp.core.jmesa.sort.CamposPermitidosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.OperadorInforme;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposPermitidosDao;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.Vista;
import com.rsi.agp.dao.tables.mtoinf.VistaCampo;

@SuppressWarnings("deprecation")
public class MtoCamposPermitidosService implements IMtoCamposPermitidosService {
	
	private IMtoCamposPermitidosDao mtoCamposPermitidosDao;
	private IMtoVistasService mtoVistasService;
	private IMtoOperadoresCamposPermitidosService mtoOperadoresCamposPermitidosService;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	// Constantes para los nombres de las columnas del listado
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private final String ID_STR = "ID";
	private final String DESCRIPCION = "DESCRIPCION";
	private final String TABLAORIGEN = "TABLAORIGEN";
	private final String CAMPO = "CAMPO";
	private final String TIPO = "TIPO";
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	
	/**
	 * Setter de propiedad
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public void setMtoOperadoresCamposPermitidosService(
			IMtoOperadoresCamposPermitidosService mtoOperadoresCamposPermitidosService) {
		this.mtoOperadoresCamposPermitidosService = mtoOperadoresCamposPermitidosService;
	}

	public Map<String, Object> altaCampoPermitido(CamposPermitidos camposPermitidos) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		try {
			if (mtoCamposPermitidosDao.checkCampPermExists(camposPermitidos.getVistaCampo().getId())) {
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_ALTA_EXISTE_KO));
			}else{
				mtoCamposPermitidosDao.saveOrUpdate(camposPermitidos);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CAMPOPERMITIDO_ALTA_OK));
				// dar de alta operadores por defecto según el tipo, para ese campo permitido.
				mtoOperadoresCamposPermitidosService.altaOperadoresPorDefectoByCamPer(camposPermitidos.getVistaCampo().getId(),
						camposPermitidos.getVistaCampo().getVistaCampoTipo().getIdtipo());
			}					
		} catch (Exception ex) {
			throw new BusinessException("MtoCamposPermitidosService - altaCampoPermitido - Error al dar de alta el campo permitido", ex);
		}
		
		return parameters;
	}

	@Override
	public boolean bajaCampoPermitido(CamposPermitidos camposPermitidos) throws BusinessException {
		try {
			mtoCamposPermitidosDao.delete(camposPermitidos);
			logger.debug("camposPermitido borrado = " + camposPermitidos.getId());
		}catch (DataIntegrityViolationException ex1){
			logger.debug("camposPermitido con id: " + camposPermitidos.getId() +" condependencias en al menos un informe");
			throw new BusinessException("Dependencias", ex1);
		}catch (Exception ex) {
			logger.error("Error al eliminar el camposPermitido", ex);
			throw new BusinessException("Error al eliminar el camposPermitido", ex);
		} 
		return false;
	}

	@Override
	public CamposPermitidos getCampoPermitido(Long id) throws BusinessException {
		try {
			return (CamposPermitidos) mtoCamposPermitidosDao.getObject(CamposPermitidos.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el CamposPermitido", dao);
			throw new BusinessException("Se ha producido al obtener el CamposPermitido:", dao);
		}
	}

	public String getTablaCamposPermitidos(HttpServletRequest request,
			HttpServletResponse response, CamposPermitidos camposPermitidos,String origenLlamada,
			String tablaOrigen, List<Vista> lstVistas, String descripcion) {
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, camposPermitidos, origenLlamada);

		// Configura el filtro y la ordenación, busca las pólizas y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, tablaOrigen, lstVistas, descripcion, request,origenLlamada);
		
		if (request.getSession().getAttribute("pageSession") != null && "borrar".equals(origenLlamada)){
			int pageSession = (Integer)(request.getSession().getAttribute("pageSession"));
			tableFacade.getLimit().getRowSelect().setPage(pageSession);
		}
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());
		
		request.getSession().setAttribute("pageSession", tableFacade.getLimit().getRowSelect().getPage());
        request.getSession().setAttribute("rowStart", tableFacade.getLimit().getRowSelect().getRowStart());
        request.getSession().setAttribute("rowEnd", tableFacade.getLimit().getRowSelect().getRowEnd());
        
        // Genera el html de la tabla y lo devuelve
		return html (tableFacade, request);
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de CamposPermitidos
	 * @param request
	 * @param response
	 * @return
	 */	
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, CamposPermitidos camposPermitidos,
			String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null || origenLlamada.equals("sesion")){
	    		if (request.getSession().getAttribute("mtoCamposPermitidos_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("mtoCamposPermitidos_LIMIT"));
	    		}
    		}else if (origenLlamada == null || origenLlamada.equals("borrar")){
    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
    			tableFacade.setLimit((Limit) request.getSession().getAttribute("mtoCamposPermitidos_LIMIT"));
    			if (!"menuGeneral".equals(origenLlamada)){
	    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	    			// CAMPO
	    			if (FiltroUtils.noEstaVacio (camposPermitidos.getVistaCampo().getNombre())){
	    				Filter filterCampo = new Filter(columnas.get(CAMPO), camposPermitidos.getVistaCampo().getNombre());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterCampo);
	    			}
	    			// TIPO
	    			if (FiltroUtils.noEstaVacio (camposPermitidos.getVistaCampo().getVistaCampoTipo().getIdtipo())){
	    				Filter filterTipo = new Filter(columnas.get(TIPO), camposPermitidos.getVistaCampo().getVistaCampoTipo().getIdtipo().toString());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterTipo);
	    			}
    			}
    			
    		}else{
    			// filtro por defecto
    			if (!"menuGeneral".equals(origenLlamada)){
	    			// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	    			// CAMPO
	    			if (FiltroUtils.noEstaVacio (camposPermitidos.getVistaCampo().getNombre())){
	    				Filter filterCampo = new Filter(columnas.get(CAMPO), camposPermitidos.getVistaCampo().getNombre());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterCampo);
	    			}
	    			// TIPO
	    			if (null != camposPermitidos.getVistaCampo().getVistaCampoTipo().getIdtipo()){
	    				Filter filterTipo = new Filter(columnas.get(TIPO), camposPermitidos.getVistaCampo().getVistaCampoTipo().getIdtipo().toString());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterTipo);
	    			}
    			}
    		}
    	}
    	request.getSession().setAttribute("mtoCamposPermitidos_LIMIT",tableFacade.getLimit());
        return tableFacade;
	}
	
	/**
     * Método para construir el html de la tabla a mostrar
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
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(request));
        }
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
    
	
    /**
	 * Configuración de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "6%");
    	// 1 - TABLAORIGEN
    	configColumna(table, columnas.get(TABLAORIGEN), "Tabla Origen", true, true, "25%");
    	// 2 - CAMPO
    	configColumna(table, columnas.get(CAMPO), "Campo", true, true, "25%");
    	// 3 - TIPO
    	configColumna(table, columnas.get(TIPO), "Tipo", true, true, "14%");
    	// 4 - DESCRIPCIÓN
    	configColumna(table, columnas.get(DESCRIPCION), "Descripci&oacute;n", true, true, "30%");
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
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final HttpServletRequest request) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Vista vista = new Vista();
				String tipo = "";
				Long idCampCLong = (Long) new BasicCellEditor().getValue(item, columnas.get(ID_STR), rowcount);
            	String idCampCalc = idCampCLong.toString();
            	String descripcion = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(DESCRIPCION), rowcount));
            	String tablaOrigen = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(TABLAORIGEN), rowcount));
            	String campo = StringUtils.nullToString(new BasicCellEditor().getValue(item, columnas.get(CAMPO), rowcount));
				VistaCampo vistaCampo = (VistaCampo) new BasicCellEditor().getValue(item, "vistaCampo", rowcount);
				if (vistaCampo != null){
					vista = vistaCampo.getVista();
					tipo = vistaCampo.getVistaCampoTipo().getIdtipo().toString();
				}
				
            	HtmlBuilder html = new HtmlBuilder();
            	// botón editar
            			html.a().href().quote().append("javascript:editar('"+idCampCalc+"','"+descripcion+"','"+ ((vistaCampo != null) ? vistaCampo.getId() : "")+"','"+((vista != null) ? vista.getId() : "")+"','"+tablaOrigen+"','"+tipo+"','"+campo+"');").quote().close();
                        html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Campo Permitido\" title=\"Editar Campo Permitido\"/>");
                        html.aEnd();
                        html.append("&nbsp;");
                // botón borrar
			            html.a().href().quote().append("javascript:borrar('"+ idCampCalc +"');").quote().close();
			            html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Campo Permitido\" title=\"Borrar Campo Permitido\"/>");
			            html.aEnd();
			            html.append("&nbsp;");
		         // botón visulizar
			            html.a().href().quote().append("javascript:visualizar('"+idCampCalc+"','"+descripcion+"','"+((vistaCampo != null) ? vistaCampo.getId() : "")+"','"+((vista != null) ? vista.getId() : "")+"','"+tablaOrigen+"','"+tipo+"','"+campo+"');").quote().close();
                        html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar Campo Permitido\" title=\"Visualizar Campo Permitido\"/>");
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
	private void setDataAndLimitVariables(TableFacade tableFacade, String tablaOrigen, List<Vista> lstVistas, 
			String descripcion, HttpServletRequest request,String origenLlamada){
		
		// Obtiene el Filter para la búsqueda y compone el objeto que almacenará en sesión dicho filtro
		Limit limit = tableFacade.getLimit();	
		CamposPermitidos cpBean = new CamposPermitidos ();
		CamposPermitidosFilter campPermFilter = getCamposPermitidosFilter(limit, cpBean, tablaOrigen, descripcion);
		
    	// Se guarda en sesión el objeto que contiene el filtro de búsqueda
    	request.getSession().setAttribute("filtroCampoPermitidos", cpBean);
		
		// Crea el Sort para la búsqueda
        CamposPermitidosSort campPermSort = getCamposPermitidosSort(limit);

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getCalcPermCountWithFilter(campPermFilter, tablaOrigen, descripcion);
        logger.debug("********** count filas de CamposPermitidos = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        int rowStart = 0;
        int rowEnd = 0;
        if (origenLlamada != null && origenLlamada.equals("borrar")){
        	rowStart = (Integer)request.getSession().getAttribute("rowStart");
        	rowEnd = (Integer)request.getSession().getAttribute("rowEnd");
        }else{
        	rowStart = limit.getRowSelect().getRowStart();
        	rowEnd = limit.getRowSelect().getRowEnd();
        }
        
		// Obtiene los registros que cumplen el filtro
        Collection<CamposPermitidos> items = new ArrayList<CamposPermitidos>();
        try {
			items = getCalcPermWithFilterAndSort(campPermFilter, campPermSort, rowStart, rowEnd, tablaOrigen, descripcion);
			for ( CamposPermitidos cp:items){
				if (cp.getVistaCampo().getVista().getNombre() == null){
					for (Vista vis:lstVistas){
						if (vis.getId().compareTo(cp.getVistaCampo().getVista().getId()) == 0){
							cp.getVistaCampo().getVista().setNombre(vis.getNombre());
							break;
						}
					}
				}
			}
			logger.debug("********** list items de CamposPermitidos = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.error("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos de la bd en la tabla
        tableFacade.setItems(items); 
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
			columnas.put(DESCRIPCION, "descripcion");
			columnas.put(TABLAORIGEN, "vistaCampo.vista.nombre");
			columnas.put(CAMPO, "vistaCampo.nombre");
			columnas.put(TIPO, "vistaCampo.vistaCampoTipo.nombreTipo");
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(TABLAORIGEN), columnas.get(CAMPO),
				columnas.get(TIPO), columnas.get(DESCRIPCION)); 
        // Devuelve el mapa
        return columnas;
	}
	
	@Override
	public Map<String, Object> updateCampoPermitido(CamposPermitidos camposPermitidos) throws BusinessException {
			
		Map<String, Object> parameters = new HashMap<String, Object>();	
		try {
			mtoCamposPermitidosDao.saveOrUpdate(camposPermitidos);
			parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CAMPOPERMITIDO_MODIF_OK));
			logger.debug("CampoPermitido editado con id= "+camposPermitidos.getId());
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar el CampoPermitido", ex);
			throw new BusinessException("Error al editar el CampoPermitido", ex);
		} catch (Exception ex) {
			logger.debug("Error al editar el CampoPermitido", ex);
			throw new BusinessException("Error al editar el CampoPermitido", ex);
		}
		return parameters;
	}
	
	/**
	 * Crea y configura el Filter para la consulta
	 * @param limit
	 * @return
	 */
	private CamposPermitidosFilter getCamposPermitidosFilter(Limit limit, CamposPermitidos cpBean, String tablaOrigen, String descripcion) {
		CamposPermitidosFilter consultaFilter = new CamposPermitidosFilter();
		//A�adimos siempre como condici�n que el campo sea visible
		consultaFilter.addFilter("vistaCampo.visible", new BigDecimal(1));
		
		
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            consultaFilter.addFilter(property, value);
            
            // Carga del bean de campos permitidos
            if ("vistaCampo.nombre".equals(property)) cpBean.getVistaCampo().setNombre(filter.getValue());
            if ("vistaCampo.vistaCampoTipo.idtipo".equals(property)) cpBean.getVistaCampo().getVistaCampoTipo().setIdtipo(new BigDecimal (filter.getValue()));
        }
        
        // Carga en el bean la descripcion
        if (descripcion != null) cpBean.setDescripcion(descripcion);
        // Carga en el bean la tabla origen
        if (!StringUtils.isNullOrEmpty(tablaOrigen)) cpBean.getVistaCampo().getVista().setId(new BigDecimal (tablaOrigen));
        
        return consultaFilter;
	}
	
	/**
	 * Crea y configura el Sort para la consulta
	 * @param limit
	 * @return
	 */
	private CamposPermitidosSort getCamposPermitidosSort(Limit limit) {
		CamposPermitidosSort consultaSort = new CamposPermitidosSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public int getCalcPermCountWithFilter(CamposPermitidosFilter filter, String tablaOrigen, String descripcion) {
		return mtoCamposPermitidosDao.getCalcPermCountWithFilter(filter, tablaOrigen, descripcion);
	}
	
	public Collection<CamposPermitidos> getCalcPermWithFilterAndSort(
			CamposPermitidosFilter filter, CamposPermitidosSort sort, int rowStart,
			int rowEnd, String tablaOrigen, String descripcion) throws BusinessException {
		
		return mtoCamposPermitidosDao.getCalcPermWithFilterAndSort(filter, sort, rowStart, rowEnd, tablaOrigen, descripcion);
	}
	
	public List<Vista> getListadoVistas() {
		List<Vista> lstVistas = mtoVistasService.getListadoVistas();
		return lstVistas;
	}
	
	public VistaCampo getVistaCampo(Long idVistaCampo){
			try {

				return (VistaCampo) mtoCamposPermitidosDao.getObject(VistaCampo.class,new BigDecimal(idVistaCampo));
			} catch (Exception dao) {
				logger
						.error("Se ha producido al obtener la VistaCampo : " + dao.getMessage());
			}
			return null;
	}
	
	
	/**
	 * mapa de formatos para el combo de la jsp
	 */
	public Map<String, String> getMapFormatos() {
		Map<String, String> mapFormatos= new HashMap<String, String>();
		String formatosNum = "";
		String codFormatosNum = "";
		String formatosFec = "";
		String codFormatosFec = "";
		
		//1
		codFormatosFec += ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY+"#";
		formatosFec += ConstantsInf.FORMATO_FECHA_DDMMYYYY+"#";
    	//2
		codFormatosFec += ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD;
		formatosFec += ConstantsInf.FORMATO_FECHA_YYYYMMDD;
    	//3
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_NNNN+"#";
		formatosNum += ConstantsInf.FORMATO_NUM_NNNN+"#";
    	//4
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_N_NNN+"#";
		formatosNum += ConstantsInf.FORMATO_NUM_N_NNN+"#";
    	//5
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_NNNN_DD+"#";
		formatosNum += ConstantsInf.FORMATO_NUM_NNNN_DD+"#";
    	//6
		codFormatosNum += ConstantsInf.COD_FORMATO_NUM_N_NNN_DD;
		formatosNum += ConstantsInf.FORMATO_NUM_N_NNN_DD;
		
		mapFormatos.put("codFormatosFec", codFormatosFec);
		mapFormatos.put("formatosFec", formatosFec);
		mapFormatos.put("codFormatosNum", codFormatosNum);
		mapFormatos.put("formatosNum", formatosNum);
		
		return mapFormatos;
	}
	
	/**
	 * Devuelve el listado de tipos de campo disponibles
	 * @return
	 */
	public List<OperadorInforme> getListaTiposCampo() {
		
		List<OperadorInforme> lista = new ArrayList<OperadorInforme>();
		
		lista.add(new OperadorInforme (new Integer (ConstantsInf.CAMPO_TIPO_TEXTO).toString(), ConstantsInf.CAMPO_TIPO_TEXTO_STR, false));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.CAMPO_TIPO_FECHA).toString(), ConstantsInf.CAMPO_TIPO_FECHA_STR, false));
		lista.add(new OperadorInforme (new Integer (ConstantsInf.CAMPO_TIPO_NUMERICO).toString(), ConstantsInf.CAMPO_TIPO_NUMERICO_STR, false));

		return lista;
	}
	
	/**
	 * Setter del Dao para Spring
	 * @param mtoCamposPermitidosDao
	 */
	public void setMtoCamposPermitidosDao(IMtoCamposPermitidosDao mtoCamposPermitidosDao) {
		this.mtoCamposPermitidosDao = mtoCamposPermitidosDao;
	}

	public void setMtoVistasService(IMtoVistasService mtoVistasService) {
		this.mtoVistasService = mtoVistasService;
	}
	
}
