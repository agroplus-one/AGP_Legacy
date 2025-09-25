package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
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
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoUsuariosDao;
import com.rsi.agp.core.jmesa.filter.MtoUsuariosFilter;
import com.rsi.agp.core.jmesa.service.IMtoUsuariosService;
import com.rsi.agp.core.jmesa.sort.MtoUsuariosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Usuario;

/**
 * @author U029769
 *
 */
@SuppressWarnings("deprecation")
public class MtoUsuariosService implements IMtoUsuariosService{
	
	private IMtoUsuariosDao mtoUsuariosDao;
	private IEntidadDao entidadDao;
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private Log logger = LogFactory.getLog(getClass());
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id;
	
	// excepciones para el cambio masivo
	private static String excep_oficina = "FK_USUARIO_OFICINA";
	private static String excep_entMed = "FK_USUARIO_ENTSUBENT";
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR ="ID";
	private final static String CODUSUARIO ="CODUSUARIO";
	private final static String NOMBREUSU ="NOMBREUSU";
	private final static String TIPOUSUARIO ="TIPOUSUARIO";
	private final static String ENTIDAD ="ENTIDAD";
	private final static String OFICINA ="OFICINA";
	private final static String ESMEDIADORA ="ESMEDIADORA";
	private final static String SUBENTMEDIADORA = "SUBENTMEDIADORA";
	private final static String DELEGACION ="DELEGACION";
	private final static String EXTERNO ="externo";
	private final static String CARGAPAC ="cargaPac";
	private final static String EMAIL ="email";
	private final static String FINANCIAR = "financiar";
	private final static String IMPORTE_MINIMO = "impMinFinanciacion";
	private final static String IMPORTE_MAXIMO = "impMaxFinanciacion";
	private final static String FECHA_LIMITE = "fechaLimite";
	private final static String NBSP = "&nbsp;";
	private static final String ALERTA = "alerta";
	private static final String ERROR_EN_ALGUNO_DE_LAS_CAMBIOS_MASIVOS = "Error en alguno de las cambios masivos: ";
	private static final String MENSAJE_MTO_USUARIO_EDICION_KO = "mensaje.mtoUsuario.edicion.KO";
	private static final String MENSAJE = "mensaje";
	private static final String MENSAJE_MTO_USUARIO_ES_SUB_ENT_VALIDA_PARA_ENT_MED = "mensaje.mtoUsuario.esSubEntValidaParaEntMed";
	private static final String MENSAJE_MTO_USUARIO_ES_ENTIDAD_OFICINA_VALIDA = "mensaje.mtoUsuario.esEntidadOficinaValida";
	
	@Override
	public Collection<Usuario> getUsuariosWithFilterAndSort(
			MtoUsuariosFilter filter, MtoUsuariosSort sort,
			int rowStart, int rowEnd)throws BusinessException {
		
		return mtoUsuariosDao.getUsuariosWithFilterAndSort(filter, sort, rowStart, rowEnd);
		
	}
	@Override
	public int getUsuariosCountWithFilter(
			MtoUsuariosFilter filter)throws BusinessException {
		
		return mtoUsuariosDao.getUsuariosCountWithFilter(filter);
	}
	
	@Override
	public String getTablaUsuarios(HttpServletRequest request,
			HttpServletResponse response, Usuario usuario,
			String origenLlamada) {
		
		TableFacade tableFacade = crearTableFacade(request, response, usuario, origenLlamada);
		
		Limit limit = tableFacade.getLimit();
		MtoUsuariosFilter consultaFilter = getConsultaUsuariosFilter(limit);
		
		setDataAndLimitVariables(tableFacade, consultaFilter, limit);
		
		String listaIdsTodos = getlistaIdsTodos(consultaFilter);
		String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		
		tableFacade.setToolbar(new CustomToolbarMarcarTodos());
		tableFacade.setView(new CustomView());

		return html (tableFacade) + script;
	}
	
	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, Usuario usuario, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        tableFacade.addFilterMatcher(new MatcherKey(String.class),new StringLikeFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        // Si no es una llamada a traves de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaUsuariosPagoManual_LIMIT") != null){
	    			//Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaUsuarios_LIMIT"));
	    		}
    		}else {
    			//Carga en el TableFacade los filtros de busqueda introducidos en el formulario 
    			cargarFiltrosBusqueda(columnas, usuario, tableFacade);
    		}
    	}                
    	
    	return tableFacade;
	}
	/**
	 * 08/05/2014 U029769
	 * @param tableFacade
	 * @return
	 */
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(CODUSUARIO, "codusuario");
			columnas.put(NOMBREUSU, "nombreusu");
			columnas.put(TIPOUSUARIO, "tipousuario");
			columnas.put(ENTIDAD, "oficina.id.codentidad");
			columnas.put(OFICINA, "oficina.id.codoficina");
			columnas.put(ESMEDIADORA, "subentidadMediadora.id.codentidad");
			columnas.put(SUBENTMEDIADORA, "subentidadMediadora.id.codsubentidad");
			columnas.put(DELEGACION, "delegacion");
			columnas.put(EMAIL, "email");
			
		}	
		tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(CODUSUARIO),columnas.get(NOMBREUSU),
				columnas.get(TIPOUSUARIO),columnas.get(ENTIDAD),columnas.get(OFICINA),columnas.get(ESMEDIADORA),
				columnas.get(DELEGACION),columnas.get(EMAIL));
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 06/05/2014 U029769
	 * @param columnas2
	 * @param usuario
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas2,
			Usuario usuario, TableFacade tableFacade) {
		// CODUSUARIO
		if (usuario.getCodusuario() != null && !usuario.getCodusuario().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODUSUARIO), usuario.getCodusuario()));
		// NOMBRE USUARIO
		if (usuario.getNombreusu() != null && !usuario.getNombreusu().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMBREUSU), usuario.getNombreusu()));
		// TIPOUSUARIO
		if (usuario.getTipousuario()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(TIPOUSUARIO), usuario.getTipousuario().toString()));
		//ENTIDAD
		if (usuario.getOficina().getId().getCodentidad()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ENTIDAD), usuario.getOficina().getId().getCodentidad().toString()));
		//OFICINA
		if (usuario.getOficina().getId().getCodoficina()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(OFICINA), usuario.getOficina().getId().getCodoficina().toString()));
		//ESMEDIADORA
		if (usuario.getSubentidadMediadora().getId().getCodentidad()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ESMEDIADORA), usuario.getSubentidadMediadora().getId().getCodentidad().toString()));
		//SUBENTIDADMEDIADORA
		if (usuario.getSubentidadMediadora().getId().getCodsubentidad()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(SUBENTMEDIADORA), usuario.getSubentidadMediadora().getId().getCodsubentidad().toString()));
		//DELEGACION
		if (usuario.getDelegacion()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(DELEGACION), usuario.getDelegacion().toString()));
		//EXTERNO
		if (usuario.getExterno()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(EXTERNO, usuario.getExterno().toString()));
		// CARGA PAC
		if (usuario.getCargaPac()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(CARGAPAC, usuario.getCargaPac().toString()));
		// E-MAIL
		if (usuario.getEmail()!= null && !usuario.getEmail().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(EMAIL), usuario.getEmail()));
		// FINANCIAR
		if (usuario.getFinanciar() != null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(FINANCIAR, usuario.getFinanciar().toString()));
		// IMPORTE MINIMO
		if (usuario.getImpMinFinanciacion() != null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(IMPORTE_MINIMO, usuario.getImpMinFinanciacion().toString()));
		// IMPORTE MAXIMO
		if (usuario.getImpMaxFinanciacion() != null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(IMPORTE_MAXIMO, usuario.getImpMaxFinanciacion().toString()));
		// FECHA LIMITE
		if (usuario.getFechaLimite() != null) {						
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(FECHA_LIMITE, new SimpleDateFormat("dd/MM/yyyy").format(usuario.getFechaLimite())));
			
		}
	}
	
	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos de usuarios y carga el TableFacade con ellas
	 * 06/05/2014 U029769
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,
			MtoUsuariosFilter consultaFilter, Limit limit) {
		
		Collection<Usuario> items = new ArrayList<Usuario>();
		try {        
	        int totalRows = getUsuariosCountWithFilter(consultaFilter);
	        logger.debug("********** count filas para Usuarios  = "+totalRows+" **********");
	        
	        tableFacade.setTotalRows(totalRows);
	
	        MtoUsuariosSort consultaSort = getConsultaUsuariosSort(limit);
	        int rowStart = limit.getRowSelect().getRowStart();
	        int rowEnd = limit.getRowSelect().getRowEnd();
	        
	        items = getUsuariosWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para Usuarios  = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
	}
	
	/**
	 * Crea y configura el Filter para la consulta de usuarios 
	 * 06/05/2014 U029769
	 * @param limit
	 * @return
	 */
	private MtoUsuariosFilter getConsultaUsuariosFilter(Limit limit) {
		MtoUsuariosFilter consultaFilter = new MtoUsuariosFilter();
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
	 * Crea y configura el Sort para la consulta de usuarios 
	 * 06/05/2014 U029769
	 * @param limit
	 * @return
	 */
	private MtoUsuariosSort getConsultaUsuariosSort(Limit limit) {
		MtoUsuariosSort consultaSort = new MtoUsuariosSort();
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
	 * 06/05/2014 U029769
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade) {
		
		HtmlTable table = (HtmlTable) tableFacade.getTable();
    	table.getRow().setUniqueProperty("id");
    	
        configurarColumnas(table);    
    	
    	Limit limit = tableFacade.getLimit();
        if (limit.isExported()) {
            tableFacade.render(); // Will write the export data out to the response.
            return null; // In Spring return null tells the controller not to do anything.
        }else{
        	// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
        	// campo acciones
        	table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	table.getRow().getColumn(columnas.get(OFICINA)).getCellRenderer().setCellEditor(getCellEditorOficina());
        	table.getRow().getColumn(columnas.get(ESMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorEsMediadora());
        	table.getRow().getColumn(columnas.get(NOMBREUSU)).getCellRenderer().setCellEditor(getCellEditorNombreUsu());
        }
        
    	return tableFacade.render();
	}
	
	
	/**
	 * Configuracion de las columnas de la tabla
	 * 08/05/2014 U029769
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "10%");
    	configColumna(table, columnas.get(CODUSUARIO), "Cod. Usuario", true, true, "15%");
    	configColumna(table, columnas.get(NOMBREUSU), "Nombre", true, true, "25%");
    	configColumna(table, columnas.get(TIPOUSUARIO), "Perfil", true, true, "10%");
    	configColumna(table, columnas.get(ENTIDAD), "Entidad", true, true, "15%");
    	configColumna(table, columnas.get(OFICINA), "Oficina", true, true, "10%");
    	configColumna(table, columnas.get(ESMEDIADORA), "E-S Mediadora", true, true, "30%");
    	configColumna(table, columnas.get(DELEGACION), "Delegacion", true, true, "30%");
    	configColumna(table, columnas.get(EMAIL), "E-mail", true, true, "25%");
    	
	}
	
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parametro y los incluye en la tabla
	 * 08/05/2014 U029769
	 * @param table
	 * @param idCol
	 * @param title
	 * @param filterable
	 * @param sortable
	 * @param width
	 */
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
	       table.getRow().getColumn(idCol).setFilterable(filterable);
	       table.getRow().getColumn(idCol).setSortable(sortable);
	       table.getRow().getColumn(idCol).setWidth(width);
		
	}
	
	
	private CellEditor getCellEditorNombreUsu() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				String value = "";
				String     nombreusu = (String) new BasicCellEditor().getValue(item, "nombreusu", rowcount);
				
				if (nombreusu != null) {
					if (nombreusu.trim().equals("")) {
						value = NBSP;
					}else {
						value = nombreusu;
					}
				}else {
					value = NBSP;
				}
				html.append(value);
				
				return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * 06/05/2014 U029769
	 * @return CellEditor
	 */
	private CellEditor getCellEditorAcciones() {
		
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				
				String codusuario = (String) new BasicCellEditor().getValue(item, "codusuario", rowcount);
				BigDecimal tipousuario = (BigDecimal) new BasicCellEditor().getValue(item, "tipousuario", rowcount);
				BigDecimal codentidad = (BigDecimal) new BasicCellEditor().getValue(item, "oficina.id.codentidad", rowcount);
				BigDecimal codoficina = (BigDecimal) new BasicCellEditor().getValue(item, "oficina.id.codoficina", rowcount);
				BigDecimal entMedia = (BigDecimal) new BasicCellEditor().getValue(item, "subentidadMediadora.id.codentidad", rowcount);
				BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor().getValue(item, "subentidadMediadora.id.codsubentidad", rowcount);
				BigDecimal delegacion = (BigDecimal) new BasicCellEditor().getValue(item, "delegacion", rowcount);
				String nombreusu = (String) new BasicCellEditor().getValue(item, "nombreusu", rowcount);
				String nomEntidad = (String)new BasicCellEditor().getValue(item, "oficina.entidad.nomentidad", rowcount);
				String nomOfi = (String)new BasicCellEditor().getValue(item, "oficina.nomoficina", rowcount);
				String nomSubEntMed = (String)new BasicCellEditor().getValue(item, "subentidadMediadora.nomsubentidad", rowcount);
				BigDecimal externo = (BigDecimal)new BasicCellEditor().getValue(item, EXTERNO, rowcount);
				BigDecimal cargaPac = (BigDecimal)new BasicCellEditor().getValue(item, CARGAPAC, rowcount);
				String email = (String) new BasicCellEditor().getValue(item, "email", rowcount);
				BigDecimal financiar = (BigDecimal) new BasicCellEditor().getValue(item, FINANCIAR, rowcount);
				BigDecimal importeMinimo = (BigDecimal) new BasicCellEditor().getValue(item, CARGAPAC, rowcount);
				BigDecimal importeMaximo = (BigDecimal) new BasicCellEditor().getValue(item, IMPORTE_MAXIMO, rowcount);
				Date fechaLimite = (Date) new BasicCellEditor().getValue(item, FECHA_LIMITE, rowcount);				
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
								
				// boton consultar
				StringBuilder funcion = new StringBuilder();
				funcion.append("'"+codusuario+"',").append(tipousuario!=null?tipousuario:"''").append(",")
				.append(codentidad!=null?codentidad:"''").append(",")
				.append(codoficina!=null?codoficina:"''").append(",")
				.append(entMedia!=null?entMedia:"''").append(",")
				.append(subEntMedia!=null?subEntMedia:"''").append(",")
				.append(delegacion!=null?delegacion:"''").append(",'"+StringUtils.nullToString(nombreusu)+"'").append(",'"+StringUtils.nullToString(nomEntidad)+"'").append(",'"+StringUtils.nullToString(nomOfi)+"'").append(",'"+StringUtils.nullToString(nomSubEntMed)+"'").append(",")
				.append(externo!=null?externo:"''").append(",")
				.append(cargaPac!=null ? cargaPac : "''").append(",")
				.append(email!=null ? "\'" + email + "\'" : "''").append(",")
				.append(financiar != null ? financiar : "''").append(",")
				.append(importeMinimo != null ? importeMinimo : "''").append(",")
				.append(importeMaximo != null ? importeMaximo : "''").append(",")
				.append(fechaLimite != null ? "'" + df.format(fechaLimite) + "'" : "''").append(");");
				
				//checkbox
            	html.append("<input type=\"checkbox\" id=\"check_" + codusuario + "\"  name=\"check_" + codusuario + "\" onClick =\"listaCheckId(\'" + codusuario + "')\" class=\"dato\"/>");
                html.append(NBSP);
				
				//consultar
				StringBuilder funcionSube = new StringBuilder();
				funcionSube.append("javascript:subirRegistro( ").append(funcion);
				html.a().href().quote().append(funcionSube).quote().close();
				html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/>");
				html.aEnd();
                html.append(NBSP);   	
				
                //boton editar
                StringBuilder funcionEdita = new StringBuilder();
                funcionEdita.append("javascript:editar( ").append(funcion);
				html.a().href().quote().append(funcionEdita).quote().close();
				html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Usuario\" title=\"Editar Usuario\"/>");
				html.aEnd();
                html.append(NBSP);
                
                // boton borrar
                StringBuilder funcionBorra = new StringBuilder();
                funcionBorra.append("javascript:borrar( ").append(funcion);
                //externo 0->no 1-> si
                // Si es externo se puede borrar
                if (externo.compareTo(new BigDecimal(1))==0) {
                	html.a().href().quote().append(funcionBorra).quote().close();
                	html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Usuario\" title=\"Borrar Usuario\"/>");
                    html.aEnd();
                    html.append(NBSP);
                }
                
            	return html.toString();
            }
		};
	}
	
	
	private CellEditor getCellEditorOficina() {
		
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
				BigDecimal oficina = (BigDecimal) new BasicCellEditor().getValue(item, "oficina.id.codoficina", rowcount);
				
				String codOficina;
				if (StringUtils.nullToString(oficina).equals("")) {
					codOficina = NBSP;
				}else {
					codOficina = oficina.toString();
	            	
	            	
	            	if (codOficina.length()<4) {
	    				while (codOficina.length()<4) {
	    					codOficina= "0" + codOficina;
	    				}
	    			}
				}
				HtmlBuilder html = new HtmlBuilder();
            	html.append(codOficina);
            	return html.toString();
            }
		};
	}
	
	private CellEditor getCellEditorEsMediadora() {
		
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
				BigDecimal entidadMed = (BigDecimal) new BasicCellEditor().getValue(item, "subentidadMediadora.id.codentidad", rowcount);
				BigDecimal subEntidadMed = (BigDecimal) new BasicCellEditor().getValue(item, "subentidadMediadora.id.codsubentidad", rowcount);
				
            	HtmlBuilder html = new HtmlBuilder();
            	if (entidadMed!= null && subEntidadMed != null) {
            		html.append(StringUtils.nullToString(entidadMed)+"-"+StringUtils.nullToString(subEntidadMed));
            	}else {
            		html.append(NBSP);
            	}
            	return html.toString();
            }
		};
	}
	
	@Override
	public Map<String, Object> editaUsuario(Usuario usuarioBean,HttpServletRequest request)throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			//se valida que la entidad exista
			if (!entidadDao.existeEntidad(usuarioBean.getOficina().getId().getCodentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.EntidadNoExiste"));
			
			//se valida que la oficina pertenece a la entidad
			}else if (!entidadDao.existeEntidadOficina(usuarioBean.getOficina().getId().getCodentidad(),
				usuarioBean.getOficina().getId().getCodoficina())) {
				parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_ES_ENTIDAD_OFICINA_VALIDA));
			
			// se valida que la entidad mediadora exista en tb_entidades_mediadoras				
			}else if (!mtoUsuariosDao.exiteEntidadMediadora(usuarioBean.getSubentidadMediadora().getId().
					getCodentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.exiteEntidadMediadora"));
				
			// se valida que la entidad medidora corresponde con la entidad
			}else if (!esEntMedValidaParaEntidad(usuarioBean.getOficina().getId().getCodentidad(),
					usuarioBean.getSubentidadMediadora().getId().getCodentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.esEntMedValidaParaEntidad"));
			
			//se valida que la subentidad mediadora corresponda con la entidad mediadora
			}else if (!subentidadMediadoraDao.esSubEntValidaParaEntMed(usuarioBean.getSubentidadMediadora().getId().getCodentidad(),
					usuarioBean.getSubentidadMediadora().getId().getCodsubentidad())) {
				parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_ES_SUB_ENT_VALIDA_PARA_ENT_MED));
			
			// se valida que la subentidad mediadora no este dada de baja
			}else if (!subentidadMediadoraDao.isSubentidadMedBaja(usuarioBean.getSubentidadMediadora().getId().getCodentidad(),
					usuarioBean.getSubentidadMediadora().getId().getCodsubentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.isSubentidadMedBaja"));
			//Si todo es correcto se actualiza el usuario
			}else {
				if (!StringUtils.nullToString(usuarioBean.getOficina().getNomoficina()).equals("")){
					Oficina ofi = (Oficina) mtoUsuariosDao.getObject(Oficina.class, usuarioBean.getOficina().getId());
					usuarioBean.setOficina(ofi);
				}
				mtoUsuariosDao.saveOrUpdate(usuarioBean);
							
				Usuario usuarioSession =  (Usuario) request.getSession().getAttribute("usuario");
				if (usuarioSession.getCodusuario().equals(usuarioBean.getCodusuario())){
					usuarioSession.setImpMinFinanciacion(usuarioBean.getImpMinFinanciacion());
					usuarioSession.setImpMaxFinanciacion(usuarioBean.getImpMaxFinanciacion());
					usuarioSession.setFechaLimite(usuarioBean.getFechaLimite());
					usuarioSession.setFinanciar(usuarioBean.getFinanciar());
					request.getSession().setAttribute("usuario", usuarioSession);
				}
				
				
				parameters.put(MENSAJE, bundle.getString("mensaje.mtoUsuario.edicion.OK"));
			}
			if (parameters.get(ALERTA)!= null) {
				parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_EDICION_KO)+" "+
						parameters.get(ALERTA));
			}
		}catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar el usuario", ex);
			parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_EDICION_KO));
		} catch (Exception ex) {
			logger.debug("Error al editar el usuario", ex);
			parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_EDICION_KO));
		}
		return parameters;
	}
	
	@Override
	public Map<String, Object> altaUsuario(Usuario usuarioBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// Se valida que el usuario existe
			if (mtoUsuariosDao.existeUsuario(usuarioBean)) {
				parameters.put(ALERTA, bundle.getString("mensaje.cambioUsuario.externo.existe.KO"));
			//se valida que la entidad exista
			}else if (!entidadDao.existeEntidad(usuarioBean.getOficina().getId().getCodentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.EntidadNoExiste"));
			
			//se valida que la oficina pertenece a la entidad
			}else if (!entidadDao.existeEntidadOficina(usuarioBean.getOficina().getId().getCodentidad(),
				usuarioBean.getOficina().getId().getCodoficina())) {
				parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_ES_ENTIDAD_OFICINA_VALIDA));
			
			// se valida que la entidad mediadora exista en tb_entidades_mediadoras				
			}else if (!mtoUsuariosDao.exiteEntidadMediadora(usuarioBean.getSubentidadMediadora().getId().
					getCodentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.exiteEntidadMediadora"));
				
			// se valida que la entidad medidora corresponde con la entidad
			}else if (!esEntMedValidaParaEntidad(usuarioBean.getOficina().getId().getCodentidad(),
					usuarioBean.getSubentidadMediadora().getId().getCodentidad())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.esEntMedValidaParaEntidad"));
			
			//se valida que la subentidad mediadora corresponda con la entidad mediadora
			}else if (!subentidadMediadoraDao.esSubEntValidaParaEntMed(usuarioBean.getSubentidadMediadora().getId().getCodentidad(),
					usuarioBean.getSubentidadMediadora().getId().getCodsubentidad())) {
				parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_ES_SUB_ENT_VALIDA_PARA_ENT_MED));
				
			//Si todo es correcto se da de alta el usuario
			}else {
				if (StringUtils.nullToString(usuarioBean.getOficina().getNomoficina()).equals("")){
					Oficina ofi = (Oficina) mtoUsuariosDao.getObject(Oficina.class, usuarioBean.getOficina().getId());
					usuarioBean.setOficina(ofi);
				}
				usuarioBean.setExterno(Constants.USUARIO_EXTERNO);
				mtoUsuariosDao.saveOrUpdate(usuarioBean);
				parameters.put(MENSAJE, bundle.getString("mensaje.alta.OK"));
			}
			if (parameters.get(ALERTA)!= null) {
				parameters.put(ALERTA, bundle.getString("mensaje.alta.generico.KO")+" "+
						parameters.get(ALERTA));
			}
		}catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta el usuario ", ex);
			parameters.put(ALERTA, bundle.getString("mensaje.alta.generico.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta el  usuario", ex);
			parameters.put(ALERTA, bundle.getString("mensaje.alta.generico.KO"));
		}	
		return parameters;
	}
	
	@Override
	public Map<String, Object> borraUsuario(Usuario usuarioBean)
			throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// comprobamos que el usuarario a borrar no tenga polizas asociadas
			if (mtoUsuariosDao.esUsuarioConPolizas(usuarioBean.getCodusuario())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.borrar.esUsuarioConPolizas"));
			
			// comprobamos que el usuario a borrar no tenga asegurados asociados
			}else if (mtoUsuariosDao.esUsuarioConAsegurados(usuarioBean.getCodusuario())) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.borrar.esUsuarioConAsegurados"));
			
			//Si todo es correcto se borra el usuario	
			}else {
				usuarioBean.setExterno(Constants.USUARIO_EXTERNO);
				usuarioBean.setCargaPac(new BigDecimal(0));
				usuarioBean.setFinanciar(new BigDecimal(0));
				
				mtoUsuariosDao.delete(usuarioBean);
				parameters.put(MENSAJE, bundle.getString("mensaje.mtoUsuario.borrar.OK"));
			}
			if (parameters.get(ALERTA)!= null) {
				parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.borrar.KO")+" "+
						parameters.get(ALERTA));
			}
			
		}catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta  el usuario", ex);
			parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.borrar.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de  alta el usuario", ex);
			parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.borrar.KO"));
		}	
		return parameters;
	}
	
	
	/** Metodo para actualizar un String con todos los Ids de usuarios segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param errorWsAccionBean
	 */
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, Usuario usuarioBean) throws DAOException {
		
		Map<String, String> parameters = new HashMap<String, String>();		
		try {
			String listaIds = listaIdsMarcados_cm.substring(0,listaIdsMarcados_cm.length()-1);
			
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			Usuario errorWs = new Usuario();
			
			for (int i=0; i<ids.length;i++){
				errorWs = (Usuario) mtoUsuariosDao.getObject(Usuario.class, ids[i]);
				mtoUsuariosDao.evict(errorWs);
				
				//se valida que la entidad exista
				if (usuarioBean.getOficina().getId().getCodentidad() !=null && !entidadDao.existeEntidad(usuarioBean.getOficina().getId().getCodentidad())) {
					parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.EntidadNoExiste"));
				
				//se valida que la oficina pertenece a la entidad
				}else if (usuarioBean.getOficina().getId().getCodentidad() != null && usuarioBean.getOficina().getId().getCodoficina() != null &&
						!entidadDao.existeEntidadOficina(usuarioBean.getOficina().getId().getCodentidad(),
					usuarioBean.getOficina().getId().getCodoficina())) {
					parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_ES_ENTIDAD_OFICINA_VALIDA));
				
				// se valida que la entidad mediadora exista en tb_entidades_mediadoras				
				}else if (usuarioBean.getSubentidadMediadora().getId().
						getCodentidad() != null && !mtoUsuariosDao.exiteEntidadMediadora(usuarioBean.getSubentidadMediadora().getId().
						getCodentidad())) {
					parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.exiteEntidadMediadora"));
					
				// se valida que la entidad medidora corresponde con la entidad
				}else if (usuarioBean.getOficina().getId().getCodentidad() != null && usuarioBean.getSubentidadMediadora().getId().getCodentidad() != null && !esEntMedValidaParaEntidad(usuarioBean.getOficina().getId().getCodentidad(),
						usuarioBean.getSubentidadMediadora().getId().getCodentidad())) {
					parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.esEntMedValidaParaEntidad"));
				
				//se valida que la subentidad mediadora corresponda con la entidad mediadora
				}else if (usuarioBean.getSubentidadMediadora().getId().getCodentidad() != null && usuarioBean.getSubentidadMediadora().getId().getCodsubentidad() != null && !subentidadMediadoraDao.esSubEntValidaParaEntMed(usuarioBean.getSubentidadMediadora().getId().getCodentidad(),
						usuarioBean.getSubentidadMediadora().getId().getCodsubentidad())) {
					parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_ES_SUB_ENT_VALIDA_PARA_ENT_MED));
				
				// se valida que la subentidad mediadora no este dada de baja
				}else if (usuarioBean.getSubentidadMediadora().getId().getCodentidad() != null && usuarioBean.getSubentidadMediadora().getId().getCodsubentidad() != null && !subentidadMediadoraDao.isSubentidadMedBaja(usuarioBean.getSubentidadMediadora().getId().getCodentidad(),
						usuarioBean.getSubentidadMediadora().getId().getCodsubentidad())) {
					parameters.put(ALERTA, bundle.getString("mensaje.mtoUsuario.isSubentidadMedBaja"));
				//Si todo es correcto se actualiza el usuario
				}else {
					if (!StringUtils.nullToString(usuarioBean.getOficina().getNomoficina()).equals("")){
						Oficina ofi = (Oficina) mtoUsuariosDao.getObject(Oficina.class, usuarioBean.getOficina().getId());
						usuarioBean.setOficina(ofi);
					}
					
					try {
						// update de todos los usuarios
						mtoUsuariosDao.cambioMasivo (listaIds,usuarioBean);
						parameters.put(MENSAJE, bundle.getString("mensaje.mtoUsuario.edicion.OK"));
	
					}catch (Exception e){
						logger.debug("Error al ejecutar el Cambio Masivo de Usuarios", e);
						String error = e.getCause().toString();
						 if (error.contains(excep_oficina)){						 
							 parameters.put(ALERTA, ERROR_EN_ALGUNO_DE_LAS_CAMBIOS_MASIVOS + bundle.getString(MENSAJE_MTO_USUARIO_ES_ENTIDAD_OFICINA_VALIDA));
						 }else if (error.contains(excep_entMed)){					 
							 parameters.put(ALERTA, ERROR_EN_ALGUNO_DE_LAS_CAMBIOS_MASIVOS + bundle.getString(MENSAJE_MTO_USUARIO_ES_SUB_ENT_VALIDA_PARA_ENT_MED));
						 }else{
							 parameters.put(ALERTA, ERROR_EN_ALGUNO_DE_LAS_CAMBIOS_MASIVOS + bundle.getString(MENSAJE_MTO_USUARIO_EDICION_KO));
						 }
					     return parameters;
					}
					
				}
				if (parameters.get(ALERTA)!= null) {
					parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_USUARIO_EDICION_KO)+" "+
							parameters.get(ALERTA));
				}
			}	// fin for
				
		
		if (parameters.get(ALERTA)!= null) {
			parameters.put(ALERTA, ERROR_EN_ALGUNO_DE_LAS_CAMBIOS_MASIVOS + bundle.getString(MENSAJE_MTO_USUARIO_EDICION_KO)+" "+
					parameters.get(ALERTA));
		}
		return parameters;
		
		} catch (DAOException e) {
			logger.debug("Error al ejecutar el Cambio Masivo ", e);
			throw e;
		}
	}
	
	
	/** Metodo para actualizar un String con todos los Ids de usuarios segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param errorWsAccionBean
	 */
	public Map<String, String> incrementarFecha(String listaIdsMarcados_ifecha, Usuario usuarioBean) throws DAOException {
		
		Map<String, String> parameters = new HashMap<String, String>();
		try {
			String listaIds = listaIdsMarcados_ifecha.substring(0,listaIdsMarcados_ifecha.length()-1);
			
			String[] ids = listaIds.split(",");
			Usuario errorWs = new Usuario();

				errorWs = (Usuario) mtoUsuariosDao.getObject(Usuario.class, ids);
				mtoUsuariosDao.evict(errorWs);
					try {
						// update de todos los usuarios
						mtoUsuariosDao.incrementarFecha(listaIds, usuarioBean);
						parameters.put(MENSAJE, bundle.getString("alerta.incrementoFechaLimite.usuarios.OK"));

					}catch (Exception e) {
						logger.debug("Error al ejecutar el Incremento de fecha de Usuarios", e);
						parameters.put(ALERTA, bundle.getString("alerta.incrementoFechaLimite.usuarios.KO"));

					    return parameters;
					}
		}
		catch (Exception e) {
			logger.debug("Error al ejecutar el Incremento de Fecha ", e);
		}
		return parameters;
	}
	
	private boolean esEntMedValidaParaEntidad(BigDecimal codentidad,
			BigDecimal entMediadora) {
		if (codentidad.toString().length()==4) {
			String auxCodEntidad = codentidad.toString().substring(1, 4);
			String auxEntMediadora = entMediadora.toString().substring(1, 4);
			if (auxCodEntidad.equals(auxEntMediadora)) {
				return true;
			}
			return false;
		}else {
			return true;
		}
	}
	
	
	/** Recupera el limit de sesion para pasarlo al bean de cambio Masivo.
	 * @param Limit
	 * @return UsuarioBean
	 */
	public Usuario getCambioMasivoBeanFromLimit(Limit consultaUsuarios_LIMIT) {
		Usuario usuarioBean = new Usuario();

		// CODUSUARIO
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(CODUSUARIO))){
			usuarioBean.setCodusuario(consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(CODUSUARIO)).getValue().toString());
		}
		// NOMBRE USUARIO
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(NOMBREUSU))){
			usuarioBean.setNombreusu(consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(NOMBREUSU)).getValue().toString());
		}
		// TIPOUSUARIO
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(TIPOUSUARIO))){
			usuarioBean.setTipousuario(new BigDecimal (consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(TIPOUSUARIO)).getValue()));
		}
		//ENTIDAD
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD))){
			usuarioBean.getOficina().getId().setCodentidad(new BigDecimal (consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD)).getValue()));
		}		
		//OFICINA
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(OFICINA))){
			usuarioBean.getOficina().getId().setCodoficina(new BigDecimal (consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(OFICINA)).getValue()));
		}
		//ESMEDIADORA
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(ESMEDIADORA))){
			usuarioBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal (consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(ESMEDIADORA)).getValue()));
		}
		//SUBENTIDADMEDIADORA
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(SUBENTMEDIADORA))){
			usuarioBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(SUBENTMEDIADORA)).getValue()));
		}
		//DELEGACION
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(DELEGACION))){
			usuarioBean.setDelegacion(new BigDecimal(consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(DELEGACION)).getValue()));
		}
		//E-MAIL
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(EMAIL))){
			usuarioBean.setEmail(consultaUsuarios_LIMIT.getFilterSet().getFilter(columnas.get(EMAIL)).getValue().toString());
		}
		//EXTERNO
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(EXTERNO)){
			usuarioBean.setExterno(new BigDecimal(consultaUsuarios_LIMIT.getFilterSet().getFilter(EXTERNO).getValue()));
		}
		//FINANCIAR
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(FINANCIAR)) {
			usuarioBean.setFinanciar(new BigDecimal(consultaUsuarios_LIMIT.getFilterSet().getFilter(FINANCIAR).getValue()));
		}
		//IMPORTE MINIMO
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(IMPORTE_MINIMO)) {
			usuarioBean.setImpMinFinanciacion(new BigDecimal(consultaUsuarios_LIMIT.getFilterSet().getFilter(IMPORTE_MINIMO).getValue()));
		}
		//IMPORTE MAXIMO
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(IMPORTE_MAXIMO)) {
			usuarioBean.setImpMaxFinanciacion(new BigDecimal(consultaUsuarios_LIMIT.getFilterSet().getFilter(IMPORTE_MAXIMO).getValue()));
		}
		//FECHA LIMITE
		if(null != consultaUsuarios_LIMIT.getFilterSet().getFilter(FECHA_LIMITE)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaLimite = null;
			try {
				fechaLimite = df.parse(consultaUsuarios_LIMIT.getFilterSet().getFilter(FECHA_LIMITE).getValue());
			} catch (ParseException e) {
				logger.error("Excepcion : MtoUsuariosService - getCambioMasivoBeanFromLimit", e);
			}
			usuarioBean.setFechaLimite(fechaLimite);
		}
		return usuarioBean;
	}
	
	
	
	/**  Metodo para recuperar un String con todos los Ids de usuarios segun el filtro
	 * 
	 * @param consultaFilter
	 * @return listaIdsTodos
	 */
	public String getlistaIdsTodos(MtoUsuariosFilter consultaFilter) {
		String listaIdsTodos =mtoUsuariosDao.getlistaIdsTodos(consultaFilter);
		return listaIdsTodos;
		
	}
	
	
	public void setMtoUsuariosDao(IMtoUsuariosDao mtoUsuariosDao) {
		this.mtoUsuariosDao = mtoUsuariosDao;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setEntidadDao(IEntidadDao entidadDao) {
		this.entidadDao = entidadDao;
	}
	public void setSubentidadMediadoraDao(
			ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}
	
	
	
	
}
