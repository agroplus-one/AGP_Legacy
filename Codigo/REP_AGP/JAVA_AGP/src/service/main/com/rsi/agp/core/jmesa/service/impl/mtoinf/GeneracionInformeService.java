package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import static com.rsi.agp.core.util.Constants.PERFIL_USUARIO_ADMINISTRADOR;
import static com.rsi.agp.core.util.Constants.PERFIL_USUARIO_SEMIADMINISTRADOR;
import static com.rsi.agp.core.util.ConstantsInf.ACCESO_PERMITIDO_USU_PER_CONCRETOS;
import static com.rsi.agp.core.util.ConstantsInf.COD_VISIBILIDAD_TODOS;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.EntidadAccesoRestringidoFilter;
import com.rsi.agp.core.jmesa.filter.InformeFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IGeneracionInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoVistasService;
import com.rsi.agp.core.jmesa.sort.InformeSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoEntidadesAccesoRestringidoDao;
import com.rsi.agp.dao.models.mtoinf.IMtoInformeDao;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones2015;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos2015;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;
import com.rsi.agp.dao.tables.mtoinf.Informe;
import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;

@SuppressWarnings("deprecation")
public class GeneracionInformeService implements IGeneracionInformeService {
	
	private IMtoInformeService mtoInformeService;
	private IMtoVistasService mtoVistasService;
	private IMtoInformeDao mtoInformeDao;
	private IMtoEntidadesAccesoRestringidoDao mtoEntidadesAccesoRestringidoDao;
	
	// Constantes para los nombres de las columnas del listado
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private final String ID_STR = "ID";
	private final String NOMBRE = "NOMBRE";
	private final String TITULO1 = "TITULO1";
	private final String TITULO2 = "TITULO2";
	private final String TITULO3 = "TITULO3";
	private final String USUARIO_STR = "USUARIO";
	
	private final String usuario = "usuario.codusuario";
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	
	/**
	 * Setter de propiedad
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getTablaInformes(HttpServletRequest request,
			HttpServletResponse response, Informe informe, String origenLlamada, final Usuario usuario) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, informe, origenLlamada);

		// Configura el filtro y la ordenacion, busca las polizas y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, usuario);
		
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve	
		return html (tableFacade, request);
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla de informes
	 * @param request
	 * @param response
	 * @return
	 */	
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, Informe informe, String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null || origenLlamada.equals("sesion")){
	    		if (request.getSession().getAttribute("generacionInformes_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("generacionInformes_LIMIT"));
	    		}
    		}else{
    			// filtro por defecto
    			if (!"menuGeneral".equals(origenLlamada)){
    				// Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    				cargarFiltrosBusqueda(columnas, informe, tableFacade);
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
    	
        // Configuracion de las columnas de la tabla    
    	configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.hasExport()) {
            tableFacade.render(); 
            return null; 
        } else {
        	// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
        	
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones(request));
        }
    	// Devuelve el html de la tabla
    	return tableFacade.render();
    }
	
    /**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final HttpServletRequest request) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Obtiene el id del informe
				Long idInformeLong = (Long) new BasicCellEditor().getValue(item, columnas.get(ID_STR), rowcount);
            	String idInforme = (idInformeLong != null) ? idInformeLong.toString() : "";
            	
            	HtmlBuilder html = new HtmlBuilder();
            	// boton generar
            	html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            	html.a().href().quote().append("javascript:ajaxCheckInforme("+idInforme+ ");").quote().close();
                html.append("<img src=\"jsp/img/displaytag/generar.png\" alt=\"Generar Informe\" title=\"Generar Informe\"/>");
                html.aEnd();
                html.append("&nbsp;");
                
                return html.toString();
            }
		};
	}
    
	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos de las polizas y carga el TableFacade con ellas
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, final Usuario usuario){
		
		Limit limit = tableFacade.getLimit();
		Collection<Informe> items = new ArrayList<Informe>();
		// Crea el Sort para la búsqueda de informes
        InformeSort informeSort = getInformeSort(limit);
        // Obtiene el Filter para la búsqueda de informes		
		InformeFilter informeFilter = getInformeFilter(limit);
		// Comprueba si la entidad a la que pertenece el usuario tiene restringido el acceso al generador a perfiles y usuarios concretos
		// y lo introduce en el objeto del filtro
		informeFilter.setEntidadRestringida(isEntidadAccesoRestringidoUsuariosPerfiles(usuario));
		// Guarda en el filtro si el usuario pertenece al perfil 0
		informeFilter.setPerfil0(PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil()));
		// Si el usuario pertenece al perfil 5, se carga la lista de entidades del grupo en el objeto del filtro
		if (PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
			informeFilter.setListaEntidadesUsuario(usuario.getListaCodEntidadesGrupo());
		}
		//DAA 28/10/2013 Siempre mostramos los informes visibles 
		informeFilter.addFilter("oculto", COD_VISIBILIDAD_TODOS);
		
        // Obtiene el número de filas que cumplen el filtro    
		int totalRows = 0;
		List<? extends Number> listCount = getInformeGenCountWithFilter(informeFilter, usuario);
        // Si el usuario es perfil 0, el listado contiene un elemento con el count de todos los informes dados de alta en la aplicacion
		if (informeFilter.isPerfil0()) {
			totalRows = (listCount != null && listCount.size() == 1) ? ((BigDecimal)listCount.get(0)).intValue() : 0;
		}
		// Si el usuario no es perfil 0, el listado contiene los id de informe que se ajustan al filtro de búsqueda introducido
		else {
			totalRows = (listCount != null) ? listCount.size() : 0;
		}
        
        logger.debug("********** count filas de informes = "+totalRows+" **********");
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);
        
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        
		// Obtiene los registros que cumplen el filtro
        try {
        	// Si el perfil del usuario no es 0 se le pasa el listado de ids de informe que hay que mostrar en el listado
			items = getInformeGenWithFilterAndSort(informeSort, rowStart, rowEnd, informeFilter.isPerfil0() ? null : toListLong (listCount),informeFilter);
			logger.debug("********** list items de informes = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos de la bd en la tabla
        tableFacade.setItems(items); 
    }

	/**
	 * Devuelve un boolean indicando si la entidad del usuario pasado como parametro tiene restringida el acceso al generador a perfiles 
	 * y usuarios concretos
	 * @param usuario
	 * @return
	 */
	private boolean isEntidadAccesoRestringidoUsuariosPerfiles(final Usuario usuario) {
		
		// Crea el filtro para buscar la entidad del usuario conectado y el tipo de acceso restringido
		EntidadAccesoRestringidoFilter earFilter = new EntidadAccesoRestringidoFilter ();
		earFilter.addFilter(EntidadAccesoRestringido.CAMPO_CODENTIDAD, usuario.getOficina().getEntidad().getCodentidad());
		earFilter.addFilter(EntidadAccesoRestringido.CAMPO_ACCESO_GENERADOR, ACCESO_PERMITIDO_USU_PER_CONCRETOS);
		
		return (mtoEntidadesAccesoRestringidoDao.getEntidadAccesoRestringidoCountWithFilter(earFilter) > 0);
	}
	
	/**
	 * Configuracion de las columnas de la tabla
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// 1 - Acciones
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "1%");
    	// 2 - Nombre
    	configColumna(table, columnas.get(NOMBRE), "Nombre", true, true, "18%");
    	// 3 - Titulo1
    	configColumna(table, columnas.get(TITULO1), "Titulo 1", true, true, "14%");
    	// 4 - Titulo2
    	configColumna(table, columnas.get(TITULO2), "Titulo 2", true, true, "14%");
    	// 5 - Titulo3
    	configColumna(table, columnas.get(TITULO3), "Titulo 3", true, true, "14%");
    	// 5 - usuario
    	configColumna(table, columnas.get(USUARIO_STR), "Usuario", true, true, "14%");
    	
	}
	
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parametro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
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
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(NOMBRE, "nombre");
			columnas.put(TITULO1, "titulo1");
			columnas.put(TITULO2, "titulo2");
			columnas.put(TITULO3, "titulo3");
			columnas.put(USUARIO_STR, "usuario.codusuario");
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(NOMBRE), columnas.get(TITULO1), columnas.get(TITULO2),
				columnas.get(TITULO3),columnas.get(USUARIO_STR)); 
        // Devuelve el mapa
        return columnas;
	}
	
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
		 
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, Informe informe, TableFacade tableFacade) {
		
		// Nombre
		if (FiltroUtils.noEstaVacio (informe.getNombre()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMBRE), informe.getNombre()));
		// Titulo1
		if (FiltroUtils.noEstaVacio (informe.getTitulo1()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TITULO1), informe.getTitulo1()));
		// Titulo2
		if (FiltroUtils.noEstaVacio (informe.getTitulo2()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TITULO2), informe.getTitulo2()));
		// Titulo3
		if (FiltroUtils.noEstaVacio (informe.getTitulo3()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TITULO3), informe.getTitulo3()));
		// codusuario
		if (FiltroUtils.noEstaVacio (informe.getUsuario().getCodusuario()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(usuario), informe.getUsuario().getCodusuario()));
	}
	
	public Map<String, Object> generarInforme(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos informeRecibos) throws BusinessException {
		try{
			Map<String, Object> map = mtoInformeService.generarConsultaInforme(informe, usuario, consultaYaGenerada, informeRecibos);
			String sql = (String)map.get("sql");
			List<?> listadoInforme = mtoInformeDao.getConsulta(sql);
			map.put("listadoInforme", listadoInforme);
			return map;
		}catch (Exception e) {
			logger.error("Se ha producido un error al crear la consulta para el informe", e);
			throw new BusinessException("Se ha producido un error al crear la consulta para el informe", e);
		}
	}
	

	public Map<String, Object> generarInforme2015(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos2015 informeRecibos) throws BusinessException {
		try{
			Map<String, Object> map = mtoInformeService.generarConsultaInforme2015(informe, usuario, consultaYaGenerada, informeRecibos);
			String sql = (String)map.get("sql");
			List<?> listadoInforme = mtoInformeDao.getConsulta(sql);
			map.put("listadoInforme", listadoInforme);
			return map;
		}catch (Exception e) {
			logger.error("Se ha producido un error al crear la consulta para el informe", e);
			throw new BusinessException("Se ha producido un error al crear la consulta para el informe", e);
		}
	}
	
	public Map<String, Object> generarInformeComisiones(Informe informe,
			Usuario usuario, String consultaYaGenerada,
			InformeComisiones informeComisionesBean) throws BusinessException {
		try{
			Map<String, Object> map = mtoInformeService.generarConsultaInformeComisiones(informe, usuario, consultaYaGenerada, informeComisionesBean);
			String sql = (String)map.get("sql");
			List<?> listadoInforme = mtoInformeDao.getConsulta(sql);
			map.put("listadoInforme", listadoInforme);
			@SuppressWarnings("unchecked")
			List<BigDecimal> lstFormato = (List<BigDecimal>) map.get("formato");
			List<BigDecimal> lstFormatoNuevo = new ArrayList<BigDecimal>();
			logger.debug("## GeneracionInformeService - formatoAntiguo : "+lstFormato.toString());
			for (BigDecimal formato:lstFormato) {
				if (formato != null && formato.compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_NUM_N_NNN_DD))==0) {
					formato = new BigDecimal(ConstantsInf.COD_FORMATO_NUM_N_NNN_DD_RIGHT);
				}
				if (formato != null && formato.compareTo(new BigDecimal(ConstantsInf.COD_FORMATO_NUM_NNNN_DD))==0) {
					formato = new BigDecimal(ConstantsInf.COD_FORMATO_NUM_NNNN_DD_RIGHT);
				}
				lstFormatoNuevo.add(formato);
			}
			logger.debug("## GeneracionInformeService - formatoNuevo : "+lstFormatoNuevo.toString());
			map.put("formato", lstFormatoNuevo);
			return map;
		}catch (Exception e) {
			logger.error("Se ha producido un error al crear la consulta para el informe", e);
			throw new BusinessException("Se ha producido un error al crear la consulta para el informe", e);
		}
	}
	
	public Map<String, Object> generarInformeComisiones2015(Informe informe,
			Usuario usuario, String consultaYaGenerada,
			InformeComisiones2015 informeComisionesBean) throws BusinessException {
		try{
			Map<String, Object> map = mtoInformeService.generarConsultaInformeComisiones2015(informe, usuario, consultaYaGenerada, informeComisionesBean);
			String sql = (String)map.get("sql");
			List<?> listadoInforme = mtoInformeDao.getConsulta(sql);
			map.put("listadoInforme", listadoInforme);
			return map;
		}catch (Exception e) {
			logger.error("Se ha producido un error al crear la consulta para el informe", e);
			throw new BusinessException("Se ha producido un error al crear la consulta para el informe", e);
		}
	}
	
	public List<RelVistaCampos> getRelVistaCampos() {
		List<RelVistaCampos> lstRelVistaCampos = mtoVistasService.getRelVistaCampos();
		return lstRelVistaCampos;
	}
	
	/**
	 * Crea y configura el Filter para la consulta de informes
	 * @param limit
	 * @return
	 */
	private InformeFilter getInformeFilter(Limit limit) {
		InformeFilter consultaFilter = new InformeFilter();
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
	 * Crea y configura el Sort para la consulta de Informes
	 * @param limit
	 * @return
	 */
	private InformeSort getInformeSort(Limit limit) {
		InformeSort consultaSort = new InformeSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	

	public List<Long> getInformeGenCountWithFilter(InformeFilter filter, final Usuario usuario) {
		return mtoInformeDao.getInformesGenCountWithFilter(filter, usuario);
	}
	
	
	public Collection<Informe> getInformeGenWithFilterAndSort(
			InformeSort sort, int rowStart,	int rowEnd, List<Long> lstIdsInforme,InformeFilter informeFilter) throws BusinessException {
		
		return mtoInformeDao.getInformesGenWithFilterAndSort(sort, rowStart, rowEnd, lstIdsInforme, informeFilter);
	}
	
	/**
	 * Convierte la lista de BigDecimal en lista de Long
	 * @param lista
	 * @return
	 */
	private List<Long> toListLong (List<? extends Number> lista) {
		List<Long> lstLong = new ArrayList<Long>();
		
		for (Number num : lista) {
			Long l = new Long (num.longValue());
			lstLong.add(l);
		}
		
		return lstLong;
	}

	/** DAA 21/02/13 Obtiene el objeto que controlara el maximo de registros permitidos para el informe
	 * 
	 * @param numRegistros
	 * @param maxReg
	 * @param formatoInforme
	 * @return
	 */
	public JSONObject getControlErrorMaxReg(int numRegistros, int maxReg, int formatoInforme) {
		JSONObject objeto = new JSONObject();
		
		/*
		- Si el número de registros es menor o igual que el valor configurado en el properties, se genera el informe normalmente.
		- Si el número de registros es mayor que el valor configurado en el properties:
			- Si el formato elegido para la generacion es diferente a excel, se muestra un mensaje de pidiendo confirmacion para generar el informe, indicando que contendra 'n' registros.
			- Si el formato elegido para la generacion es excel:
				- Si el número de registros es menor o igual que el maximo de registros para excel configurado en la clase de constantes (65535), se muestra el mismo mensaje de confirmacion que en el caso anterior.
				- Si el número de registros es mayor que el maximo, se muestra un mensaje informando de esto y no se permite la generacion.
		*/
		try{
			if(numRegistros <= maxReg){
				objeto.put("datos", "0");	//se genera el informe normalmente
			}else{
				if(formatoInforme != ConstantsInf.COD_FORMATO_XLS){
					objeto.put("datos", "1");
					objeto.put("mensaje",MessageFormat.format(bundle.getString("mensaje.numRegistros.informe"),numRegistros));
					//se muestra un mensaje de pidiendo confirmacion para generar el informe, indicando que contendra 'n' registros.
				}else{
					if(numRegistros <= ConstantsInf.MAX_REG_EXCEL){
						objeto.put("datos", "1");
						objeto.put("mensaje",MessageFormat.format(bundle.getString("mensaje.numRegistros.informe"),numRegistros));
					}else{
						objeto.put("datos", "2");
						objeto.put("mensaje", bundle.getString("mensaje.maximoPermitido.informe"));
					}	
				}
			}
		} catch (JSONException e) {
			logger.error("Excepcion : GeneracionInformeService - getControlErrorMaxReg", e);
		}
		return objeto;
	}
	
	/** DAA 16/10/2013
	 *  Metodo para establecer un registro mas al listado de informe que correspondera al sumatorio del campo que venga indicado
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List setSumatorioToInforme(List totaliza, List listadoInforme) {
		BigDecimal[] registroSumatorio = new BigDecimal[totaliza.size()];
		
		for(int i=0; i<totaliza.size(); i++){
			
			if((Boolean) totaliza.get(i)){
				BigDecimal suma = new BigDecimal(0);
				for(Object dato : listadoInforme){
					if (dato instanceof Object[]) {
						Object[] registro = (Object[]) dato;
						suma = suma.add((BigDecimal) registro[i]);
					}
					else if (dato instanceof BigDecimal) {
						suma = suma.add((BigDecimal)dato);
					}
				}
				registroSumatorio[i] = suma;
			}
		}
		listadoInforme.add(Arrays.asList(registroSumatorio));	
		return listadoInforme;
	}
	
	/**
	 * Setter del service para Spring
	 * @param mtoInformeService
	 */
	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}		
	
	public void setMtoVistasService(IMtoVistasService mtoVistasService) {
		this.mtoVistasService = mtoVistasService;
	}

	public void setMtoInformeDao(IMtoInformeDao mtoInformeDao) {
		this.mtoInformeDao = mtoInformeDao;
	}

	public void setMtoEntidadesAccesoRestringidoDao(
			IMtoEntidadesAccesoRestringidoDao mtoEntidadesAccesoRestringidoDao) {
		this.mtoEntidadesAccesoRestringidoDao = mtoEntidadesAccesoRestringidoDao;
	}
}