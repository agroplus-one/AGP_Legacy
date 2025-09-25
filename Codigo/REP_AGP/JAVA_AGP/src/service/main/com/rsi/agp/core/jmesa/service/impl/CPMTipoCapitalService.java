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
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CPMTipoCapitalFilter;
import com.rsi.agp.core.jmesa.service.ICPMTipoCapitalService;
import com.rsi.agp.core.jmesa.sort.CPMTipoCapitalSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.cpl.IModulosDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.ICicloCultivoDao;
import com.rsi.agp.dao.models.poliza.IConceptoPpalDao;
import com.rsi.agp.dao.models.poliza.ICultivoDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.ISistemaCultivoDao;
import com.rsi.agp.dao.models.poliza.ITipoCapitalDao;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapital;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("deprecation")
public class CPMTipoCapitalService implements ICPMTipoCapitalService {
	
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private ILineaDao lineaDao;
	private IModulosDao modulosDao;
	private ICultivoDao cultivoDao;
	private ISistemaCultivoDao sistemaCultivoDao;
	private ITipoCapitalDao tipoCapitalDao;
	private IConceptoPpalDao conceptoPpalDao;
	private ICicloCultivoDao cicloCultivoDao;

	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private String id;
	// Mapa con las columnas del listado y los campos del filtro de b�squeda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String CODMODULO = "CODMODULO";
	private final static String CODCONCEPTOPPALMOD = "CODCONCEPTOPPALMOD";
	private final static String CODTIPOCAPITAL = "CODTIPOCAPITAL";
	private final static String CODCULTIVO = "CODCULTIVO";
	private final static String CODSISTEMACULTIVO = "CODSISTEMACULTIVO";	
	private final static String FECHAFINGARANTIA = "FECHAFINGARANTIA";
	private final static String CODCICLOCULTIVO = "CODCICLOCULTIVO";
	
	// &nbsp;
	private final String NBSP = "&nbsp;";
	
	@Override
	public Collection<CPMTipoCapital> getCPMTipoCapitalWithFilterAndSort(CPMTipoCapitalFilter filter, CPMTipoCapitalSort sort, int rowStart, int rowEnd) throws BusinessException {
		
		return cpmTipoCapitalDao.getCPMTipoCapitalWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	@Override
	public int getConsultaCPMTipoCapitalCountWithFilter(CPMTipoCapitalFilter filter) {
		
		return cpmTipoCapitalDao.getConsultaCPMTipoCapitalCountWithFilter(filter);
	}
	

	public String getTablaCPMTipoCapital (HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapital, String origenLlamada){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, cpmTipoCapital, origenLlamada);
		
		// Obtiene el Filter para la b�squeda de cpmTipoCapital
		Limit limit = tableFacade.getLimit();
		CPMTipoCapitalFilter consultaFilter = getConsultaCPMTipoCapitalFilter(limit);
		
		// Configura el filtro y la ordenación, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, consultaFilter, limit);
				
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		return html (tableFacade);
		
	}
			
	/**
	 * Crea y configura el objeto TableFacade que encapsular� la tabla
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapital, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaCPMTipoCapital_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaCPMTipoCapital_LIMIT"));
	    		}
    		}
   			//Carga en el TableFacade los filtros de b�squeda introducidos en el formulario 
   			cargarFiltrosBusqueda(columnas, cpmTipoCapital, tableFacade);
    	}                
    	request.getSession().setAttribute("consultaCPMTipoCapital_LIMIT",tableFacade.getLimit());

    	return tableFacade;
	}
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(PLAN, "cultivo.linea.codplan");
			columnas.put(LINEA, "cultivo.linea.codlinea");
			columnas.put(CODMODULO , "modulo");
			columnas.put(CODCONCEPTOPPALMOD , "conceptoPpalModulo.codconceptoppalmod");
			columnas.put(CODTIPOCAPITAL , "tipoCapital.codtipocapital");
			columnas.put(CODCULTIVO , "cultivo.id.codcultivo");
			columnas.put(CODSISTEMACULTIVO , "sistemaCultivo.codsistemacultivo");
			columnas.put(FECHAFINGARANTIA , "fechafingarantia");
			columnas.put(CODCICLOCULTIVO, "cicloCultivo.codciclocultivo");
		}	
		tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(PLAN), columnas.get(LINEA), 
				columnas.get(CODMODULO),columnas.get(CODCONCEPTOPPALMOD),columnas.get(CODTIPOCAPITAL),columnas.get(CODCULTIVO),
				columnas.get(CODSISTEMACULTIVO),columnas.get(FECHAFINGARANTIA),columnas.get(CODCICLOCULTIVO)); 
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de b�squeda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, CPMTipoCapital cpmTipoCapital, TableFacade tableFacade) {
		
		// PLAN
		if (cpmTipoCapital.getCultivo().getLinea() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getCultivo().getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), cpmTipoCapital.getCultivo().getLinea().getCodplan().toString()));
		// LINEA
		if (cpmTipoCapital.getCultivo().getLinea() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getCultivo().getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(LINEA), cpmTipoCapital.getCultivo().getLinea().getCodlinea().toString()));
		// CODMODULO
		if (FiltroUtils.noEstaVacio (cpmTipoCapital.getModulo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODMODULO), cpmTipoCapital.getModulo()));
		// CODCONCEPTOPPALMOD
		if (cpmTipoCapital.getConceptoPpalModulo() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getConceptoPpalModulo().getCodconceptoppalmod()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODCONCEPTOPPALMOD), cpmTipoCapital.getConceptoPpalModulo().getCodconceptoppalmod().toString()));
		// CODTIPOCAPITAL
		if (cpmTipoCapital.getTipoCapital() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getTipoCapital().getCodtipocapital()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODTIPOCAPITAL), cpmTipoCapital.getTipoCapital().getCodtipocapital().toString()));
		// CODCULTIVO
		if (cpmTipoCapital.getCultivo() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getCultivo().getId().getCodcultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODCULTIVO), cpmTipoCapital.getCultivo().getId().getCodcultivo().toString()));
		// CODSISTEMACULTIVO
		if (cpmTipoCapital.getSistemaCultivo() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODSISTEMACULTIVO), cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo().toString()));
		// FECHAFINGARANTIA
		if (cpmTipoCapital.getFechafingarantia() != null )
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHAFINGARANTIA), new SimpleDateFormat("dd/MM/yyyy").format(cpmTipoCapital.getFechafingarantia())));
		// CODCICLOCULTIVO
		if (cpmTipoCapital.getCicloCultivo() != null && FiltroUtils.noEstaVacio (cpmTipoCapital.getCicloCultivo().getCodciclocultivo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODCICLOCULTIVO), cpmTipoCapital.getCicloCultivo().getCodciclocultivo().toString()));

	}
	/**
	 * Crea los objetos de filtro y ordenaci�n, llama al dao para obtener los datos de cpmTipoCapital y carga el TableFacade con ellas
	 * @param tableFacade
	 * @param limit 
	 * @param consultaFilter 
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, CPMTipoCapitalFilter consultaFilter, Limit limit ){

        // Obtiene el n�mero de filas que cumplen el filtro        
        int totalRows = getConsultaCPMTipoCapitalCountWithFilter(consultaFilter);
        logger.debug("********** count filas para CPMTipoCapital = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la b�squeda CPMTipoCapital
        CPMTipoCapitalSort consultaSort = getConsultaCPMTipoCapitalSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<CPMTipoCapital> items = new ArrayList<CPMTipoCapital>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getCPMTipoCapitalWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para CPMTipoCapital = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
        
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
        	table.getRow().getColumn(columnas.get(CPMTipoCapitalService.ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	// campo fechafingarantia
        	table.getRow().getColumn(columnas.get(CPMTipoCapitalService.FECHAFINGARANTIA)).getCellRenderer().setCellEditor(getCellEditorFechaFinG());
        	
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
            	String descLinea = (String)new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);
            	BigDecimal codPlan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	String codModulo = (String)new BasicCellEditor().getValue(item, "modulo",rowcount);
            	BigDecimal concPpalMod = (BigDecimal)new BasicCellEditor().getValue(item, "conceptoPpalModulo.codconceptoppalmod", rowcount);
            	BigDecimal codTipoCapital = (BigDecimal)new BasicCellEditor().getValue(item, "tipoCapital.codtipocapital", rowcount);
            	String desTipoCapital = (String)new BasicCellEditor().getValue(item, "tipoCapital.destipocapital", rowcount); 
            	BigDecimal codCultivo = (BigDecimal)new BasicCellEditor().getValue(item, "cultivo.id.codcultivo", rowcount);
            	String desCultivo = (String)new BasicCellEditor().getValue(item, "cultivo.descultivo", rowcount);
            	String codSistCult = StringUtils.nullToString(new BasicCellEditor().getValue(item, "sistemaCultivo.codsistemacultivo", rowcount));
            	String desSistCult = StringUtils.nullToString(new BasicCellEditor().getValue(item, "sistemaCultivo.dessistemacultivo",rowcount));
            	Date fechafingarantia = (Date)new BasicCellEditor().getValue(item, "fechafingarantia",rowcount);
            	String codCicloCultivo = StringUtils.nullToString(new BasicCellEditor().getValue(item, "cicloCultivo.codciclocultivo",rowcount));
            	String desCicloCultivo = StringUtils.nullToString(new BasicCellEditor().getValue(item, "cicloCultivo.desciclocultivo",rowcount));
            	String nulo = "";
            	
            	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            	HtmlBuilder html = new HtmlBuilder();
            	
            	//botón editar
            	html.a().href().quote().append("javascript:editar("+id+","+codLinea+",'"+descLinea+"',"+codPlan+",'"+codModulo+"',"+concPpalMod+"," +
            			codTipoCapital+",'"+desTipoCapital+"',"+codCultivo+",'"+desCultivo+"','"+codSistCult+"','"+desSistCult+"','"+ 
            			((fechafingarantia==null) ? nulo:formato.format(fechafingarantia))+"','"+codCicloCultivo+"','"+desCicloCultivo+"');").quote().close();
            	
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar CPMTipoCapital\" title=\"Editar CPMTipoCapital\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // botón borrar 
            	html.a().href().quote().append("javascript:borrar("+id+");").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar CPMTipoCapital\" title=\"Borrar CPMTipoCapital\"/>");
                html.aEnd();
                html.append("&nbsp;");
	                	
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'fechafingarantia'
	 * @return
	 */
	private CellEditor getCellEditorFechaFinG() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	// Muestra la fechafingarantia
		    	String value = "";		    		    		    	
				try {
					// Si la tiene fecha de envío se formatea y se muestra, si no la tiene no se muestra nada
					Date dateAux = (Date)new BasicCellEditor().getValue(item, columnas.get(FECHAFINGARANTIA), rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat("dd/MM/yyyy").format(dateAux);
				} catch (Exception e) {
					logger.error("ConsultaPolizaSbpService - Ocurrió un error al obtener la fecha fin garantia" , e);
				}
		    			    	
		        HtmlBuilder html = new HtmlBuilder();
		        html.append (FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
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
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "8%");
    	
    	configColumna(table, columnas.get(PLAN), "Plan", true, true, "8%");
    	configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "8%");
    	configColumna(table, columnas.get(CODMODULO), "M&oacute;dulo", true, true, "8%");
    	configColumna(table, columnas.get(CODCONCEPTOPPALMOD), "CPM", true, true, "8%");
    	configColumna(table, columnas.get(CODTIPOCAPITAL ), "Tipo Capital", true, true, "15%");
    	configColumna(table, columnas.get(CODCULTIVO ), "Cultivo", true, true, "8%");
    	configColumna(table, columnas.get(CODSISTEMACULTIVO ), "Sist. Cultivo", true, true, "15%");
     	configColumna(table, columnas.get(FECHAFINGARANTIA ), "Fin de Garant&iacute;a", true, true, "15%");
     	configColumna(table, columnas.get(CODCICLOCULTIVO ), "Ciclo Cultivo", true, true, "15%");

	}
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como par�metro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
	 * @param filterable Indica si se podr� buscar por esa columna
	 * @param sortable Indica si se podr� ordenar por esa columna
	 * @param width Ancho de la columna
	 */
	private void configColumna (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width) {
	   table.getRow().getColumn(idCol).setTitle(title);
       table.getRow().getColumn(idCol).setFilterable(filterable);
       table.getRow().getColumn(idCol).setSortable(sortable);
       table.getRow().getColumn(idCol).setWidth(width);
	}
    
	/**
	 * Crea y configura el Filter para la consulta de cpmTipoCapital
	 * @param limit
	 * @return
	 */
	private CPMTipoCapitalFilter getConsultaCPMTipoCapitalFilter(Limit limit) {
		CPMTipoCapitalFilter consultaFilter = new CPMTipoCapitalFilter();
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
	private CPMTipoCapitalSort getConsultaCPMTipoCapitalSort(Limit limit) {
		
		CPMTipoCapitalSort consultaSort = new CPMTipoCapitalSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public CPMTipoCapital getCPMTipoCapital(Long idCPMTipoCapital)
	throws BusinessException {
		try {
			return (CPMTipoCapital) cpmTipoCapitalDao.get(CPMTipoCapital.class, idCPMTipoCapital);
		
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener el cpmTipoCapital: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener el cpmTipoCapital:",
					dao);
		}
	}
	
	public void bajaCPMTipoCapital(Long id) throws BusinessException {
		try {
			cpmTipoCapitalDao.delete(CPMTipoCapital.class, id);
			logger.debug("CPMTipoCapital borrado  = " + id);
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar el CPMTipoCapital",ex);
		}
	}
	
	public Map<String, Object> editaCPMTipoCapital(CPMTipoCapital cpmTipoCapital) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			// Realiza las validaciones previas para comprobar que el registro se puede modificar
			parameters = validacionesPrevias(cpmTipoCapital);
			
			// Si el mapa tiene registros no se puede continuar con el alta
			if (parameters != null && !parameters.isEmpty()) return parameters;
			
			cpmTipoCapitalDao.saveOrUpdate(cpmTipoCapital);
			cpmTipoCapitalDao.evict(cpmTipoCapital);
			logger.debug("CPMTipoCapital modificado con  id = " + cpmTipoCapital.getId());
			parameters.put("mensaje", bundle.getString("mensaje.cpmTipoCapital.edicion.OK"));
			
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar el CPMTipoCapital", ex);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.edicion.KO"));
		} catch (Exception ex) {
			logger.debug("Error al editar el CPMTipoCapital", ex);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.edicion.KO"));
		}

		return parameters;
	}

	
	
	public Map<String, Object> altaCPMTipoCapital(CPMTipoCapital cpmTipoCapital) throws BusinessException {

		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		// Comprueba que el sistema de cultivo introducido es correcto
		boolean filtroSistCultivo = cpmTipoCapital.getSistemaCultivo() != null && cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo() != null;
		
		try {
			// Realiza las validaciones previas para comprobar que el registro se puede dar de alta
			parameters = validacionesPrevias(cpmTipoCapital);
			
			// Si el mapa tiene registros no se puede continuar con el alta
			if (parameters != null && !parameters.isEmpty()) return parameters;
				
			// Modificacion del objeto para la transaccion en Hibernate
			if (cpmTipoCapital.getId() != null) cpmTipoCapital.setId(null);
			if (!filtroSistCultivo) cpmTipoCapital.setSistemaCultivo(null);
			
			cpmTipoCapitalDao.saveOrUpdate(cpmTipoCapital);
			cpmTipoCapitalDao.evict(cpmTipoCapital);
			
			if (!filtroSistCultivo) cpmTipoCapital.setSistemaCultivo(new SistemaCultivo());
            
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta el CPMTipoCapital", ex);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.existente.alta.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta el CPMTipoCapital", ex);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.KO"));
		}
		
		return parameters;
	}

	/**
	 * Valida que la informacion indicada en el objeto es valida para dar de alta o modificar
	 * @param cpmTipoCapital
	 * @return
	 * @throws DAOException
	 */
	private Map<String, Object> validacionesPrevias(CPMTipoCapital cpmTipoCapital) throws DAOException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Comprueba que el plan/linea introducido es correcto
		Linea linea = lineaDao.getLinea(cpmTipoCapital.getCultivo().getLinea().getCodlinea(), 
				cpmTipoCapital.getCultivo().getLinea().getCodplan());
		if (linea == null || StringUtils.nullToString(linea.getLineaseguroid()).equals("")) {
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.lineaseguroid.KO"));	
			return parameters;
		}
		
		// Comprueba que el modulo introducido es correcto
		Modulo mod = modulosDao.getModulo(linea.getLineaseguroid(), cpmTipoCapital.getModulo());
		if (mod == null) {
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.modulo.KO"));
			return parameters;
		}
		cpmTipoCapital.setModulo(mod.getId().getCodmodulo());
		
		// Comprueba que el cultivo introducido es correcto
		Cultivo cult = cultivoDao.getCultivo(cpmTipoCapital.getCultivo().getLinea().getCodplan(), 
				cpmTipoCapital.getCultivo().getLinea().getCodlinea(), cpmTipoCapital.getCultivo().getId().getCodcultivo());
		if (cult == null) {
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.cultivo.KO"));
			return parameters;
		}
		else {
			cpmTipoCapital.setCultivo(cult);
		}
		
		
		// Comprueba que el sistema de cultivo introducido es correcto
		if (cpmTipoCapital.getSistemaCultivo() != null && cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo() != null) {
			if (!sistemaCultivoDao.existeSistemaCultivo(cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo())) {
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.sistCultivo.KO"));
				return parameters;
			}
		}
		else{
			//ASF - 22/01/2014 - Si el usuario no ha elegido sistema de cultivo, lo elimino del bean para que no
			// de errores el hibernate al guardar el objeto
			cpmTipoCapital.setSistemaCultivo(null);
		}
		// Comprueba que el ciclo cultivo introducido es valido
		if (cpmTipoCapital.getCicloCultivo() != null && cpmTipoCapital.getCicloCultivo().getCodciclocultivo() != null) {
			if (!cicloCultivoDao.existeCicloCultivo(cpmTipoCapital.getCicloCultivo().getCodciclocultivo())) {
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.cicloCultivo.KO"));
				return parameters;
			}
		}else {
			cpmTipoCapital.setCicloCultivo(null);
		}
		
		// Comprueba que el tipo de capital introducido es correcto
		if (cpmTipoCapital.getTipoCapital() != null && cpmTipoCapital.getTipoCapital().getCodtipocapital() != null) {
			if (!tipoCapitalDao.existeTipoCapital(cpmTipoCapital.getCultivo().getLinea().getCodplan(), 
												  cpmTipoCapital.getCultivo().getLinea().getCodlinea(), 
												  cpmTipoCapital.getModulo(),
												  cpmTipoCapital.getCultivo().getId().getCodcultivo(),
												  cpmTipoCapital.getTipoCapital().getCodtipocapital())) {
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.tipoCapital.KO"));
				return parameters;
			}
		}
		
		// Comprueba que el concepto principal del modulo introducido es correcto
		if (!conceptoPpalDao.existeConceptoPpal(cpmTipoCapital.getConceptoPpalModulo().getCodconceptoppalmod())) {
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.conceptoPpalModulo.KO"));
			return parameters;
		}
		
		// Comprueba si el registro ya existe en la tabla
		if (existeCPMTipoCapital(cpmTipoCapital)) {
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.existente.alta.KO"));
			return parameters;
		}
		
		// Si llega hasta aqui el objeto es valido para el alta o modificacion
		return parameters;
	}
	
	
	/**
	 * Comprueba si cpmTipoCapital ya existe
	 * @param cpmTipoCapital
	 * @return
	 */
	private boolean existeCPMTipoCapital(CPMTipoCapital cpmTipoCapital) {
		
		CPMTipoCapitalFilter filtro = new CPMTipoCapitalFilter(); 
        filtro.addFilter("cultivo.linea.codlinea", cpmTipoCapital.getCultivo().getLinea().getCodlinea());
        filtro.addFilter("cultivo.linea.codplan", cpmTipoCapital.getCultivo().getLinea().getCodplan());
        filtro.addFilter("modulo", cpmTipoCapital.getModulo());
        filtro.addFilter("tipoCapital.codtipocapital", cpmTipoCapital.getTipoCapital().getCodtipocapital());
        filtro.addFilter("conceptoPpalModulo.codconceptoppalmod",cpmTipoCapital.getConceptoPpalModulo().getCodconceptoppalmod());
        filtro.addFilter("cultivo.id.codcultivo", cpmTipoCapital.getCultivo().getId().getCodcultivo());
        if (cpmTipoCapital.getSistemaCultivo() != null && cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo() != null) {
        	filtro.addFilter("sistemaCultivo.codsistemacultivo",cpmTipoCapital.getSistemaCultivo().getCodsistemacultivo());
        }
        filtro.addFilter("fechafingarantia",cpmTipoCapital.getFechafingarantia());
        if (cpmTipoCapital.getCicloCultivo() != null && cpmTipoCapital.getCicloCultivo().getCodciclocultivo() != null) {
        	filtro.addFilter("cicloCultivo.codciclocultivo",cpmTipoCapital.getCicloCultivo().getCodciclocultivo());
        }
        filtro.addFilter("incluirNulos","");
       
        return cpmTipoCapitalDao.getConsultaCPMTipoCapitalCountWithFilter(filtro)>0;
		
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long lineaSeguroIdOrigen = null;
		Long lineaSeguroIdDestino = null;
		
		// Obtiene el lineaseguroid de los plan/linea origen y destino
		try {
			// Validacion del plan/linea origen
			lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(lineaOrig, planOrig);
			if (lineaSeguroIdOrigen == null) {
				logger.debug("El plan/línea origen no existe, no se continua con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.replica.planlinea.origen.KO"));
				return parameters;
			}
			
			// Validacion del plan/linea destino
			lineaSeguroIdDestino = lineaDao.getLineaSeguroId(lineaDest, planDest);
			if (lineaSeguroIdDestino == null) {
				logger.debug("El plan/línea destino no existe, no se contin�a con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.replica.planlinea.destino.KO"));
				return parameters;
			}
			
			// Valida que el plan/linea destino no tenga cpmTipoCapital dados de alta previamente
			CPMTipoCapitalFilter filter = new CPMTipoCapitalFilter ();
			filter.addFilter("cultivo.linea.codplan", planDest);
			filter.addFilter("cultivo.linea.codlinea", lineaDest);
			
			if (getConsultaCPMTipoCapitalCountWithFilter(filter) != 0) {
				logger.debug("El plan/línea destino tiene cpmTipoCapital dados de alta, no se continua con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.replica.planlinea.KO"));
				return parameters;
			}
			
			// Llamada al método del DAO que realiza la réplica 
			String numregCopiados = cpmTipoCapitalDao.replicar(new BigDecimal(lineaSeguroIdOrigen), new BigDecimal(lineaSeguroIdDestino));
			logger.debug("El proceso de réplica ha finalizado correctamente. Numero de registros Copiados = " + numregCopiados);
			if (numregCopiados.equals("0")){
				parameters.put("mensaje", bundle.getString("mensaje.cpmTipoCapital.replica.sin.registros.copiados.OK"));
				return parameters;
			}
		} 
		catch (DAOException e) {
			logger.error("Ocurrió un error al replicar los cpmTipoCapital ", e);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.replica.KO"));
			return parameters;
		}
		
		// Comprobamos si se han copiado todos los registros
		if(cpmTipoCapitalDao.numRegDestinoIgualNumRegOrigen (lineaSeguroIdDestino,lineaSeguroIdOrigen)){
			parameters.put("mensaje", bundle.getString("mensaje.cpmTipoCapital.replica.OK"));
		}else{
			parameters.put("mensaje", bundle.getString("mensaje.cpmTipoCapital.replica.registros.pendientes.OK"));
		}
		
		return parameters;
	}

	public void setModulosDao(IModulosDao modulosDao) {
		this.modulosDao = modulosDao;
	}

	public void setCultivoDao(ICultivoDao cultivoDao) {
		this.cultivoDao = cultivoDao;
	}

	public void setSistemaCultivoDao(ISistemaCultivoDao sistemaCultivoDao) {
		this.sistemaCultivoDao = sistemaCultivoDao;
	}

	public void setTipoCapitalDao(ITipoCapitalDao tipoCapitalDao) {
		this.tipoCapitalDao = tipoCapitalDao;
	}

	public void setConceptoPpalDao(IConceptoPpalDao conceptoPpalDao) {
		this.conceptoPpalDao = conceptoPpalDao;
	}

	public void setCicloCultivoDao(ICicloCultivoDao cicloCultivoDao) {
		this.cicloCultivoDao = cicloCultivoDao;
	}	
	
}
