package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.rsi.agp.core.jmesa.filter.MtoImpuestoSbpFilter;
import com.rsi.agp.core.jmesa.service.IMtoImpuestoSbpService;
import com.rsi.agp.core.jmesa.sort.MtoImpuestoSbpSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.sbp.IMtoImpuestoSbpDao;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;



@SuppressWarnings("deprecation")
public class MtoImpuestoSbpService implements IMtoImpuestoSbpService {
	
	private IMtoImpuestoSbpDao mtoImpuestoSbpDao;
	private ILineaDao lineaDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	
	private String id;
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private Log logger = LogFactory.getLog(getClass());
	
	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String CODIMPUESTO = "CODIMPUESTO";
	private final static String NOMIMPUESTO = "NOMIMPUESTO";
	private final static String VALOR = "VALOR";
	private final static String BASE = "BASE";
	// &nbsp;
	private final String NBSP = "&nbsp;";
	
	@Override
	public Collection<MtoImpuestoSbp> getMtoImpuestoSbpWithFilterAndSort(MtoImpuestoSbpFilter filter, MtoImpuestoSbpSort sort, int rowStart, int rowEnd) throws BusinessException {
		
		return mtoImpuestoSbpDao.getMtoImpuestoSbpWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}
	
	@Override
	public int getConsultaMtoImpuestoSbpCountWithFilter(MtoImpuestoSbpFilter filter) {
		
		return mtoImpuestoSbpDao.getConsultaMtoImpuestoSbpCountWithFilter(filter);
	}
	

	public String getTablaMtoImpuestoSbp (HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbp, String origenLlamada){
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, mtoImpuestoSbp, origenLlamada);
		
		// Obtiene el Filter para la búsqueda de mtoImpuestoSbp
		Limit limit = tableFacade.getLimit();
		MtoImpuestoSbpFilter consultaFilter = getConsultaMtoImpuestoSbpFilter(limit);
		
		// Configura el filtro y la ordenación, busca los datos y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, consultaFilter, limit);
				
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		return html (tableFacade);
		
	}
			
	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbp, String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);				
    	
		//Carga las columnas a mostrar en el listado en el TableFacade y devuelve un Map con ellas
		HashMap<String,String> columnas = cargarColumnas(tableFacade);
        
        tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
        tableFacade.addFilterMatcher(new MatcherKey(String.class),new StringFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        // Si no es una llamada a través de ajax        
    	if (request.getParameter("ajax") == null){
    		if (origenLlamada == null){
	    		if (request.getSession().getAttribute("consultaMtoImpuestoSbp_LIMIT") != null){
	    			//Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
	    			tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaMtoImpuestoSbp_LIMIT"));
	    		}
    		}else {
    			//Carga en el TableFacade los filtros de búsqueda introducidos en el formulario 
    			cargarFiltrosBusqueda(columnas, mtoImpuestoSbp, tableFacade);
    		}
    	}                
    	//request.getSession().setAttribute("consultaMtoImpuestoSbp_LIMIT",tableFacade.getLimit());

    	return tableFacade;
	}
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		
		// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(PLAN, "codplan");
			columnas.put(CODIMPUESTO, "impuestoSbp.codigo");
			columnas.put(NOMIMPUESTO, "impuestoSbp.descripcion");
			columnas.put(VALOR,"valor");
			columnas.put(BASE,"baseSbp.base");
		}	
		tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(PLAN),columnas.get(CODIMPUESTO),
				columnas.get(NOMIMPUESTO),columnas.get(VALOR), columnas.get(BASE));
        
        return columnas;
	}
	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el formulario
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String,String> columnas, MtoImpuestoSbp mtoImpuestoSbp, TableFacade tableFacade) {
		
		// PLAN
		if (mtoImpuestoSbp.getCodplan() != null)
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(PLAN), mtoImpuestoSbp.getCodplan().toString()));
		// CODIMPUESTO
		if (mtoImpuestoSbp.getImpuestoSbp() != null && FiltroUtils.noEstaVacio (mtoImpuestoSbp.getImpuestoSbp().getCodigo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(CODIMPUESTO), mtoImpuestoSbp.getImpuestoSbp().getCodigo()));
		// NOMIMPUESTO
		if (mtoImpuestoSbp.getImpuestoSbp() != null && FiltroUtils.noEstaVacio (mtoImpuestoSbp.getImpuestoSbp().getDescripcion()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMIMPUESTO), mtoImpuestoSbp.getImpuestoSbp().getDescripcion()));
		// VALOR
		if (mtoImpuestoSbp.getValor() != null )
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(VALOR), mtoImpuestoSbp.getValor().toString()));
		// BASE
		if (mtoImpuestoSbp.getBaseSbp() != null && FiltroUtils.noEstaVacio (mtoImpuestoSbp.getBaseSbp().getBase()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(BASE), mtoImpuestoSbp.getBaseSbp().getBase()));
	
	}
	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de mtoImpuestoSbp y carga el TableFacade con ellas
	 * @param tableFacade
	 * @param limit 
	 * @param consultaFilter 
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, MtoImpuestoSbpFilter consultaFilter, Limit limit ){

        // Obtiene el número de filas que cumplen el filtro        
        int totalRows = getConsultaMtoImpuestoSbpCountWithFilter(consultaFilter);
        logger.debug("********** count filas para MtoImpuestoSbp = "+totalRows+" **********");
        
        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
        tableFacade.setTotalRows(totalRows);

        // Crea el Sort para la búsqueda MtoImpuestoSbp
        MtoImpuestoSbpSort consultaSort = getConsultaMtoImpuestoSbpSort(limit);
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<MtoImpuestoSbp> items = new ArrayList<MtoImpuestoSbp>();
		// Obtiene los registros que cumplen el filtro
        try {
			items = getMtoImpuestoSbpWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para MtoImpuestoSbp = "+items.size()+" **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
        tableFacade.setItems(items); 
        
    }
	
	/**
     * Método para construir el html de la tabla a mostrar
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
        	table.getRow().getColumn(columnas.get(MtoImpuestoSbpService.ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
        	// campo valor
        	table.getRow().getColumn(columnas.get(MtoImpuestoSbpService.VALOR)).getCellRenderer().setCellEditor(getCellEditorValor());
        	
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
				Long id = (Long) new BasicCellEditor().getValue(item, "id", rowcount);
				BigDecimal codPlan = (BigDecimal) new BasicCellEditor().getValue(item, "codplan", rowcount);
				String codImpuesto = (String) new BasicCellEditor().getValue(item, "impuestoSbp.codigo", rowcount);
				String nomImpuesto = (String) new BasicCellEditor().getValue(item, "impuestoSbp.descripcion", rowcount);
				String nomBase = (String) new BasicCellEditor().getValue(item, "baseSbp.base", rowcount);
				BigDecimal valor = (BigDecimal) new BasicCellEditor().getValue(item, "valor", rowcount);

            	HtmlBuilder html = new HtmlBuilder();
            	
            	//botón editar
            	html.a().href().quote().append("javascript:editar("+id+","+codPlan+",'"+codImpuesto+"','"+nomImpuesto+"','"+nomBase+"',"+valor+");").quote().close();
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar MtoImpuestoSbp\" title=\"Editar MtoImpuestoSbp\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // botón borrar 
            	html.a().href().quote().append("javascript:borrar("+id+");").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar MtoImpuestoSbp\" title=\"Borrar MtoImpuestoSbp\"/>");
                html.aEnd();
                html.append("&nbsp;");
	                	
            	return html.toString();
            }
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Valor'
	 * @return
	 */
	private CellEditor getCellEditorValor() {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	// Muestra el valor
		    	String value = "";		    		    		    	
				try {
					
					BigDecimal valor = (BigDecimal) new BasicCellEditor().getValue(item, "valor", rowcount);
					value = valor + NBSP + "%";
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
    	configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "10%");
    	configColumna(table, columnas.get(PLAN), "Plan", false, false, "10%");
    	configColumna(table, columnas.get(CODIMPUESTO), "Cod.Impuesto", true, true, "10%");
    	configColumna(table, columnas.get(NOMIMPUESTO), "Nom.Impuesto", true, true, "30%");
    	configColumna(table, columnas.get(VALOR), "Valor", true, true, "10%");
    	configColumna(table, columnas.get(BASE), "Base", true, true, "30%");
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
	 * Crea y configura el Filter para la consulta de mtoImpuestoSbp
	 * @param limit
	 * @return
	 */
	private MtoImpuestoSbpFilter getConsultaMtoImpuestoSbpFilter(Limit limit) {
		MtoImpuestoSbpFilter consultaFilter = new MtoImpuestoSbpFilter();
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
	private MtoImpuestoSbpSort getConsultaMtoImpuestoSbpSort(Limit limit) {
		
		MtoImpuestoSbpSort consultaSort = new MtoImpuestoSbpSort();
		SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            consultaSort.addSort(property, order);
        }

        return consultaSort;
	}
	
	public MtoImpuestoSbp getMtoImpuestoSbp(Long idMtoImpuestoSbp)
	throws BusinessException {
		try {
			return (MtoImpuestoSbp) mtoImpuestoSbpDao.get(MtoImpuestoSbp.class, idMtoImpuestoSbp);
		
		} catch (Exception dao) {
			logger.error("Se ha producido error al obtener el mtoImpuestoSbp: "
						+ dao.getMessage());
		throw new BusinessException(
				"Se ha producido al obtener el mtoImpuestoSbp:",
					dao);
		}
	}
	
	public void bajaMtoImpuestoSbp(Long id) throws BusinessException {
		try {
			mtoImpuestoSbpDao.delete(MtoImpuestoSbp.class, id);
			logger.debug("MtoImpuestoSbp borrado  = " + id);
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al borrar el MtoImpuestoSbp",ex);
		}
	}
	
	public Map<String, Object> editaMtoImpuestoSbp(MtoImpuestoSbp mtoImpuestoSbp) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			
			// comprobamos si es valido el plan
			if(!lineaDao.existePlan(mtoImpuestoSbp.getCodplan())){
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.lineaseguroid.KO"));
			}else{
				//comprobamos si es valido el impuesto
				if(!existeImpuestoSbp(mtoImpuestoSbp)){
					parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.impuesto.KO"));
				}else{
					//comprobamos si es valida la base
					if(!existeBaseSbp(mtoImpuestoSbp)){
						parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.base.KO"));
					}	
					else{
						//comprobamos que no existan previamente registros iguales para todos los campos excepto el valor
						if(mtoImpuestoSbpDao.numRegistrosIguales(mtoImpuestoSbp)>0){
							parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.existente.alta.KO"));
						}else{
							mtoImpuestoSbpDao.saveOrUpdate(mtoImpuestoSbp);
							mtoImpuestoSbpDao.evict(mtoImpuestoSbp);
							parameters.put("mensaje", bundle.getString("mensaje.mtoImpuestoSbp.edicion.OK"));
						}
					}
				}
			}	
			
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar el MtoImpuestoSbp", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.edicion.KO"));
		} catch (Exception ex) {
			logger.debug("Error al editar el MtoImpuestoSbp", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.edicion.KO"));
		}
		

		return parameters;
	}

	
	

	public Map<String, Object> altaMtoImpuestoSbp(MtoImpuestoSbp mtoImpuestoSbp) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();	
		
		try {
			mtoImpuestoSbp.setId(null);
			
			// comprobamos si es valido el plan
			if(!lineaDao.existePlan(mtoImpuestoSbp.getCodplan())){
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.lineaseguroid.KO"));
			}else{
				//comprobamos si es valido el impuesto
				if(!existeImpuestoSbp(mtoImpuestoSbp)){
					parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.impuesto.KO"));
				}else{
					//comprobamos si es valida la base
					if(!existeBaseSbp(mtoImpuestoSbp)){
						parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.base.KO"));
					}	
					else{
						//comprobamos que no existan previamente registros iguales para todos los campos excepto el valor
						if(mtoImpuestoSbpDao.numRegistrosIguales(mtoImpuestoSbp)>0){
							parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.existente.alta.KO"));
						}else{
							//en el caso del alta establezco el id a nulo por si viene relleno.
							mtoImpuestoSbp.setId(null);
							mtoImpuestoSbpDao.saveOrUpdate(mtoImpuestoSbp);
							mtoImpuestoSbpDao.evict(mtoImpuestoSbp);
							parameters.put("mensaje", bundle.getString("mensaje.mtoImpuestoSbp.alta.OK"));
						}
					}
				}
			}	
			
		}catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta el MtoImpuestoSbp", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.existente.alta.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta el MtoImpuestoSbp", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.KO"));
		}
		
		return parameters;
	}
	/**
	 * Comprueba existe el ImpuestoSbp
	 * @param mtoImpuestoSbp
	 * @return
	 */
	private boolean existeImpuestoSbp(MtoImpuestoSbp mtoImpuestoSbp) {

		List<?> impuestos =mtoImpuestoSbpDao.getImpuestoSbpWithFilter(mtoImpuestoSbp);
        if (impuestos != null && impuestos.size() == 1){
        	Object[] impuesto = (Object[]) impuestos.get(0);
        	Long idImpuesto = new Long(impuesto[0].toString());
        	mtoImpuestoSbp.getImpuestoSbp().setId(idImpuesto);
        	return true;
        }
        else
        	return false;
		
	}
	
	/**
	 * Comprueba existe la BaseSbp
	 * @param mtoImpuestoSbp
	 * @return
	 */
	private boolean existeBaseSbp(MtoImpuestoSbp mtoImpuestoSbp) {

        List<?> bases =mtoImpuestoSbpDao.getBaseSbpWithFilter(mtoImpuestoSbp);
        if (bases != null && bases.size() == 1){
        	Object[] impuesto = (Object[]) bases.get(0);
        	Long idBase = new Long(impuesto[0].toString());
        	mtoImpuestoSbp.getBaseSbp().setId(idBase);
        	return true;
        }
        else
        	return false;
		
	}
	
	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal planDest) throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
				
		try {
			// Validación del plan origen
			if (planOrig == null) {
				logger.debug("El plan origen no existe, no se continúa con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.replica.plan.origen.KO"));
				return parameters;
			}
			
			// Validación del plan destino
			if (planDest == null || !lineaDao.existePlan(planDest)) {
				logger.debug("El plan destino no existe, no se continúa con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.replica.plan.destino.KO "));
				return parameters;
			}
			
			// Valida que el plan destino no tenga mtoImpuestoSbp dados de alta previamente
			MtoImpuestoSbpFilter filter = new MtoImpuestoSbpFilter ();
			filter.addFilter("codplan", planDest);
			
			if (getConsultaMtoImpuestoSbpCountWithFilter(filter) != 0) {
				logger.debug("El plan destino tiene mtoImpuestoSbp dados de alta, no se continúa con la réplica");
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.replica.existen.registros.KO"));
				return parameters;
			}
			
			// Llamada al método del DAO que realiza la réplica 
			String numregCopiados = mtoImpuestoSbpDao.replicar(planOrig, planDest);
			logger.debug("El proceso de réplica ha finalizado correctamente. Numero de registros Copiados = " + numregCopiados);
			
			if (numregCopiados.equals("0")){
				parameters.put("mensaje", bundle.getString("mensaje.mtoImpuestoSbp.replica.sin.registros.copiados.OK"));
				return parameters;
			}else{
				parameters.put("mensaje", bundle.getString("mensaje.mtoImpuestoSbp.replica.OK"));
				return parameters;
			}	
			
		} 
		catch (DAOException e) {
			logger.error("Ocurrió un error al replicar los mtoImpuestoSbp ", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.replica.KO"));
			return parameters;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMtoImpuestoSbpDao(IMtoImpuestoSbpDao mtoImpuestoSbpDao) {
		this.mtoImpuestoSbpDao = mtoImpuestoSbpDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
	
}
