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
import org.hibernate.exception.ConstraintViolationException;
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
import com.rsi.agp.core.jmesa.filter.CamposCalculadosFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCamposCalculadosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadoresCamposCalculadosService;
import com.rsi.agp.core.jmesa.sort.CamposCalculadosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.InformeUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposCalculadosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposPermitidosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoOperadorCamposCalculadosDao;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;

@SuppressWarnings("deprecation")
public class MtoCamposCalculadosService implements IMtoCamposCalculadosService {
	
	private IMtoOperadoresCamposCalculadosService mtoOperadoresCamposCalculadosService;
	private IMtoCamposCalculadosDao mtoCamposCalculadosDao;
	private IMtoCamposPermitidosDao mtoCamposPermitidosDao;
	private IMtoOperadorCamposCalculadosDao mtoOperadorCamposCalculadosDao;
	
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();
		
		private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
		
		private String id;
		
		private Log logger = LogFactory.getLog(getClass());
		
		// Constantes para los nombres de las columnas del listado
		private final static String ID_STR = "ID";
		private final static String NOMBRE = "NOMBRE";
		private final static String OPERANDO1 = "OPERANDO1";
		private final static String IDOPERADOR = "IDOPERADOR";
		private final static String OPERANDO2 = "OPERANDO2";

		@Override
		public int getConsultaCamposCalculadosCountWithFilter(CamposCalculadosFilter filter) {
			
			return mtoCamposCalculadosDao.getCamposCalculadosCountWithFilter(filter);
		}

		@Override
		public Collection<CamposCalculados> getCamposCalculadosWithFilterAndSort(
				CamposCalculadosFilter filter, CamposCalculadosSort sort, int rowStart,
				int rowEnd) throws BusinessException {
			
			Collection<CamposCalculados> colCamposCalculados = null;
			try{
			colCamposCalculados =  mtoCamposCalculadosDao.getCamposCalculadosWithFilterAndSort(filter, sort, rowStart, rowEnd);
			}catch(Exception ex){
				logger.error("getCamposCalculadosWithFilterAndSort error. " + ex);
				throw new BusinessException(
						"Se ha producido al obtener getCamposCalculadosWithFilterAndSort:",
							ex);
				}
			
			return colCamposCalculados;
			}
		
		public String getTablaCamposCalculados (HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados, String origenLlamada){
			
			// Crea el TableFacade
			TableFacade tableFacade = crearTableFacade(request, response, camposCalculados, origenLlamada);
			// Configura el filtro y la ordenación, busca los datos y las carga en el TableFacade
			setDataAndLimitVariables(tableFacade,request,origenLlamada);

			
			if (request.getSession().getAttribute("pageSession") != null && origenLlamada != null && origenLlamada.equals("borrar")){
				
				int pageSession = (Integer)(request.getSession().getAttribute("pageSession"));
				tableFacade.getLimit().getRowSelect().setPage(pageSession);
			
			}
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());

			request.getSession().setAttribute("pageSession", tableFacade.getLimit().getRowSelect().getPage());
	        request.getSession().setAttribute("rowStart", tableFacade.getLimit().getRowSelect().getRowStart());
	        request.getSession().setAttribute("rowEnd", tableFacade.getLimit().getRowSelect().getRowEnd());
			
			
			
			return html (tableFacade);
			
		}
		/**
		 * Crea y configura el objeto TableFacade que encapsulará la tabla de campos calculados
		 * @param request
		 * @param response
		 * @return
		 */
		private TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados,String origenLlamada) {
			
		
			TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);

			// Carga las columnas a mostrar en el listado en el TableFacade y
			cargarColumnas(tableFacade);
						
			tableFacade.setStateAttr("restore");

			// Si no es una llamada a través de ajax
			if (request.getParameter("ajax") == null) {
				if (origenLlamada == null || origenLlamada.equals("") || origenLlamada.equals("borrar")) {
					if (request.getSession().getAttribute(
							"consultaCamposCalculados_LIMIT") != null) {
						// Si venimos por aquí es que ya hemos pasado por el filtro
						// en algun momento
						tableFacade.setLimit((Limit) request.getSession()
								.getAttribute("consultaCamposCalculados_LIMIT"));
					}
				
					//NOMBRE
					if (camposCalculados.getNombre() !=null && !camposCalculados.getNombre().equals("")){
	        			Filter filterNombre = new Filter("nombre", camposCalculados.getNombre().toString());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterNombre);
	        		}
	        		// OPERANDO1
	        		if (camposCalculados.getCamposPermitidosByIdoperando1().getId() !=null){
	        			Filter filterIdOperando1 = new Filter("camposPermitidosByIdoperando1.id", camposCalculados.getCamposPermitidosByIdoperando1().getId().toString());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterIdOperando1);
	        		}	
	        		// OPERANDO2
	        		if (camposCalculados.getCamposPermitidosByIdoperando2().getId() !=null){
	        			Filter filterIdOperando2 = new Filter("camposPermitidosByIdoperando2.id", camposCalculados.getCamposPermitidosByIdoperando2().getId().toString());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterIdOperando2);
	        		}
	        		// IDOPERADO
	        		if (camposCalculados.getIdoperador() !=null){
	        			Filter filterIdoperador = new Filter("idoperador", camposCalculados.getIdoperador().toString());
	        			tableFacade.getLimit().getFilterSet().addFilter(filterIdoperador);
	        		}
	    			
				}
				
			}
				
			return tableFacade;

	    	
	    	
		}

		
		@SuppressWarnings("all")
		private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
			
			// Crea el Map con las columnas del listado y los campos del filtro de búsqueda si no se ha hecho anteriormente
			if (columnas.size() == 0) {
				columnas.put(ID_STR, "id");
				columnas.put(NOMBRE, "nombre");
				columnas.put(OPERANDO1, "camposPermitidosByIdoperando1.vistaCampo.nombre");
				columnas.put(IDOPERADOR, "idoperador");
				columnas.put(OPERANDO2, "camposPermitidosByIdoperando2.vistaCampo.nombre");
				
			}	
			tableFacade.setColumnProperties(columnas.get(ID_STR),columnas.get(NOMBRE), columnas.get(OPERANDO1),
					columnas.get(IDOPERADOR),columnas.get(OPERANDO2)); 
	        
	        return columnas;
		}
		
		/**
		 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos de Campos Calculados y carga el TableFacade con ellas
		 * @param tableFacade
		 */
		private void setDataAndLimitVariables(TableFacade tableFacade,HttpServletRequest request,String origenLlamada){
	        
			
			Limit limit = tableFacade.getLimit();
			
			CamposCalculados ccBean = new CamposCalculados ();
			CamposCalculadosFilter consultaFilter = getConsultaCamposCalculadosFilter(limit, ccBean);
			
	    	// Se guarda en sesión el objeto que contiene el filtro de búsqueda
	    	request.getSession().setAttribute("filtroCampoCalculados", ccBean);
			
	        // Obtiene el número de filas que cumplen el filtro        
	        int totalRows = getConsultaCamposCalculadosCountWithFilter(consultaFilter);
	        logger.debug("********** count filas para campos calculados = "+totalRows+" **********");
	        
	        // y lo establecemos al tableFacade antes de obtener la fila de inicio y la de fin
	        tableFacade.setTotalRows(totalRows);

	        // Crea el Sort para la búsqueda campos calculados
	        CamposCalculadosSort consultaSort = getConsultaCamposCalculadosSort(limit);
	        int rowStart = 0;
	        int rowEnd = 0; 
	        if (origenLlamada != null && origenLlamada.equals("borrar")){
	     	   rowStart = (Integer)request.getSession().getAttribute("rowStart");
	          rowEnd = (Integer)request.getSession().getAttribute("rowEnd");
	        }else{
	     	   rowStart = limit.getRowSelect().getRowStart();
	           rowEnd = limit.getRowSelect().getRowEnd();
	        }
	        Collection<CamposCalculados> items = new ArrayList<CamposCalculados>();
			// Obtiene los registros que cumplen el filtro
	        try {
				items = getCamposCalculadosWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
				logger.debug("********** list items para campos calculados = "+items.size()+" **********");
			} catch (BusinessException e) {
				
				logger.error("setDataAndLimitVariables error. " + e);
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
	    	
	       
	        	// Configuración de los datos de las columnas que requieren un tratamiento para mostrarse
	        	// campo acciones
	        	
	        	table.getRow().getColumn(columnas.get(NOMBRE)).getCellRenderer().setCellEditor(getCellEditorNombre());
	        	table.getRow().getColumn(columnas.get(OPERANDO1)).getCellRenderer().setCellEditor(getCellEditorOperando1());
	        	table.getRow().getColumn(columnas.get(IDOPERADOR)).getCellRenderer().setCellEditor(getCellEditorOperador());
	        	table.getRow().getColumn(columnas.get(OPERANDO2)).getCellRenderer().setCellEditor(getCellEditorOperando2());
	        	
	        	configurarColumnas(table);
	        	
	        	Limit limit = tableFacade.getLimit();
	    		if (limit.isExported()) {
	    			tableFacade.render(); 
	    								
	    			return null; 
	    							
	    		} else {
	    			// Configuración de los datos de las columnas que requieren un
	    			// tratamiento para mostrarse
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
					HtmlBuilder html = new HtmlBuilder();
					Long id = (Long)new BasicCellEditor().getValue(item, "id", rowcount);
					String nombre = (String) new BasicCellEditor().getValue(item, "nombre", rowcount); 
					Long operando1 = (Long) new BasicCellEditor().getValue(item, "camposPermitidosByIdoperando1.id", rowcount);
					Long operando2 = (Long) new BasicCellEditor().getValue(item, "camposPermitidosByIdoperando2.id", rowcount);
					BigDecimal operador = (BigDecimal) new BasicCellEditor().getValue(item, "idoperador", rowcount);
	            	
	            	// botón editar
	            	html.a().href().quote().append("javascript:editar('"+id+"','"+nombre+"','"+operando1+"','"+operando2+"','"+operador+"');").quote().close();
	            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Campo Calculado\" title=\"Editar Campo Calculado\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
	            			
	                // botón borrar 
	            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
	                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Campo Calculado\" title=\"Borrar Campo Calculado\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
	                
	                // botón visualizar 
	                html.a().href().quote().append("javascript:visualizar('"+id+"','"+nombre+"','"+operando1+"','"+operando2+"','"+operador+"');").quote().close();
	                html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar Campo Calculado\" title=\"Visualizar Campo Calculado\"/>");
	                html.aEnd();
	                html.append("&nbsp;");
	            	
		            return html.toString();
	            }
			};
		}
		
		/**
		 * Devuelve el objeto que muestra la columna 'nombre'
		 * @return
		 */
		private CellEditor getCellEditorNombre() {
			return new CellEditor() {
				
				public Object getValue(Object item, String property, int rowcount) {
	            	String nombre = null;
					try {
						nombre = (String) new BasicCellEditor().getValue(item, "nombre", rowcount);
					} catch (Exception e) {
						logger.error("nombre nulo");
					}
	            	
	            	HtmlBuilder html = new HtmlBuilder();
	            	
	            	html.append(StringUtils.nullToString(nombre) + "&nbsp;");
	            			
	            	return html.toString();
	            }
			};
		}
		
		/**
		 * Devuelve el objeto que muestra la columna 'Operando1'
		 * @return
		 */
		private CellEditor getCellEditorOperando1() {
			return new CellEditor() {
				
				public Object getValue(Object item, String property, int rowcount) {
	            	String operando1 = null;
					try {
						operando1 = (String) new BasicCellEditor().getValue(
								item, "camposPermitidosByIdoperando1.vistaCampo.nombre", rowcount);
					} catch (Exception e) {
						logger.error("camposPermitidosByIdoperando1.abreviado nulo");
					}
	            	
	            	HtmlBuilder html = new HtmlBuilder();
	        
	            	html.append(StringUtils.nullToString(operando1) + "&nbsp;");
	            			
	            	return html.toString();
	            }
			};
		}
		
		/**
		 * Devuelve el objeto que muestra la columna 'Operando2'
		 * @return
		 */
		private CellEditor getCellEditorOperando2() {
			return new CellEditor() {
				
				public Object getValue(Object item, String property, int rowcount) {
	            	String operando2 = null;
					try {
						operando2 = (String) new BasicCellEditor().getValue(item, "camposPermitidosByIdoperando2.vistaCampo.nombre", rowcount);
					} catch (Exception e) {
						logger.error("camposPermitidosByIdoperando2.abreviado nulo");
					}
	            	
	            	HtmlBuilder html = new HtmlBuilder();
	            	
	            	html.append(StringUtils.nullToString(operando2) + "&nbsp;");
	            			
	            	return html.toString();
	            }
			};
		}
		
		/**
		 * Devuelve el objeto que muestra la columna 'Operador'
		 * @return
		 */
		private CellEditor getCellEditorOperador() {
			return new CellEditor() {
				
				public Object getValue(Object item, String property, int rowcount) {
	            	BigDecimal idoperador = null;
					try {
						idoperador = (BigDecimal) new BasicCellEditor().getValue(item, "idoperador", rowcount);
					} catch (Exception e) {
						logger.error("idoperador nulo");
					}
	            	
	            	HtmlBuilder html = new HtmlBuilder();
	            
	            	if (idoperador != null)
	            		html.append(StringUtils.nullToString(InformeUtils.getOperador(idoperador.intValue())) + "&nbsp;");
	            			
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
	    	configColumna(table, columnas.get(NOMBRE), "Nombre", true, true, "24%");
	    	configColumna(table, columnas.get(OPERANDO1), "Operando 1", true, true, "30%");
	      	configColumna(table, columnas.get(IDOPERADOR), "Operador", true, true, "10%");
	     	configColumna(table, columnas.get(OPERANDO2), "Operando 2", true, true, "30%");
	      	 
	    	
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
		 * Crea y configura el Filter para la consulta de campos calculados
		 * @param limit
		 * @return
		 */
		private CamposCalculadosFilter getConsultaCamposCalculadosFilter(Limit limit, CamposCalculados ccBean) {
			CamposCalculadosFilter consultaFilter = new CamposCalculadosFilter();
	        FilterSet filterSet = limit.getFilterSet();
	        Collection<Filter> filters = filterSet.getFilters();
	        
	        for (Filter filter : filters) {
	            String property = filter.getProperty();
	            String value = filter.getValue();
	            consultaFilter.addFilter(property, value);
	            
	            // Carga del bean de campos calculados
	            if ("nombre".equals(property)) ccBean.setNombre(filter.getValue());
	            if ("idoperador".equals(property)) ccBean.setIdoperador(new BigDecimal (filter.getValue()));
	        }
	        
	        return consultaFilter;
		}
		
		/**
		 * Crea y configura el Sort para la consulta de campos calculados
		 * @param limit
		 * @return
		 */
		private CamposCalculadosSort getConsultaCamposCalculadosSort(Limit limit) {
			
			CamposCalculadosSort consultaSort = new CamposCalculadosSort();
			SortSet sortSet = limit.getSortSet();
	        Collection<Sort> sorts = sortSet.getSorts();
	        for (Sort sort : sorts) {
	            String property = sort.getProperty();
	            String order = sort.getOrder().toParam();
	            consultaSort.addSort(property, order);
	        }

	        return consultaSort;
		}
		

		
		public Map<String, Object> bajaCamposCalculados(CamposCalculados camposCalculados) throws BusinessException {
			Map<String, Object> parameters = new HashMap<String, Object>();
			try {
				boolean existeCondicionCamCalc = false;
				// recojo la lista de operadores para ese campo calculado
				List<OperadorCamposCalculados> lstOpCalc = mtoOperadorCamposCalculadosDao.getListaOperadores(new BigDecimal(camposCalculados.getId()));
				boolean tieneCondiciones = false;
				if (lstOpCalc != null && lstOpCalc.size() >0){
					for (OperadorCamposCalculados opCalc:lstOpCalc){
						// reviso si hay condicion para ese campo calculado
						existeCondicionCamCalc = mtoOperadorCamposCalculadosDao.existeCondicionCamCalc(opCalc.getId().toString());
						if (existeCondicionCamCalc){
							tieneCondiciones = true;
						}
					}
				}
				if (!tieneCondiciones){
					// compruebo que no se usa en ningun informe.
					boolean existeEnInforme = mtoOperadorCamposCalculadosDao.existeCamCalcEnInforme(camposCalculados.getId().toString());
					if (existeEnInforme){
						logger.debug("bajaCamposCalculados : no se puede borrar porque existe para un informe");
						parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_BAJA_KO_EXISTE_INFORME));
					}else{ // borramos operadores y el campo calculado
						if (lstOpCalc != null && lstOpCalc.size() >0){
							for (OperadorCamposCalculados opCalc:lstOpCalc){
								// borramos cada uno de los operadores
								mtoOperadorCamposCalculadosDao.delete(opCalc);
								logger.debug("operadorCamposCalculado borrado = " + opCalc.getId());
							}
						}
						// y por ultimo el campo calculado
						mtoCamposCalculadosDao.delete(CamposCalculados.class, camposCalculados.getId());
						parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CAMPOCALCULADO_BAJA_OK));
						logger.debug("camposCalculados borrado  = " + camposCalculados.getId());
					}
				}else{
					logger.debug("bajaCamposCalculados : no se puede borrar porque existe para un informe");
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_BAJA_KO_EXISTE_INFORME));
				}
					
			}
			catch (ConstraintViolationException ex) {
				logger.error("bajaCamposCalculados : no se puede borrar porque existe para un informe",ex);
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_BAJA_KO_EXISTE_INFORME));	
			
			}
			
			catch (DataIntegrityViolationException ex) {
				logger.error("bajaCamposCalculados : no se puede borrar porque existe para un informe",ex);
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_BAJA_KO_EXISTE_INFORME));	
				
			}
			
			 catch (Exception ex) {
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_BAJA_KO));	
				logger.error("bajaCamposCalculados : error a borrar un campo calculado", ex); 
				
			}
			return parameters;
		}
	
		public void setCamposCalculadosDao(IMtoCamposCalculadosDao mtoCamposCalculadosDao) {
			this.mtoCamposCalculadosDao = mtoCamposCalculadosDao;
		}

		public void setCamposPermitidosDao(IMtoCamposPermitidosDao mtoCamposPermitidosDao) {
			this.mtoCamposPermitidosDao = mtoCamposPermitidosDao;
		}
		
		public void setId(String id) {
			this.id = id;
		}

	
	/**
	 * Setter del Dao para Spring
	 * @param mtoCamposCalculadosDao
	 */
	public void setMtoCamposCalculadosDao(IMtoCamposCalculadosDao mtoCamposCalculadosDao) {
		this.mtoCamposCalculadosDao = mtoCamposCalculadosDao;
	}

	public IMtoCamposPermitidosDao getMtoCamposPermitidosDao() {
		return mtoCamposPermitidosDao;
	}

	public void setMtoCamposPermitidosDao(
			IMtoCamposPermitidosDao mtoCamposPermitidosDao) {
		this.mtoCamposPermitidosDao = mtoCamposPermitidosDao;
	}

	public IMtoCamposCalculadosDao getMtoCamposCalculadosDao() {
		return mtoCamposCalculadosDao;
	}

	
	
	
	public List<CamposPermitidos> getListCamposPermitidos() throws  BusinessException{
		List<CamposPermitidos> listCamposPermitidos = null;
		try{
		
		listCamposPermitidos= mtoCamposPermitidosDao.getListaCamposPermitidosParaOperador();
	
		} catch (Exception ex) {
				logger.error("getListCamposPermitidos ; Error al recuperar la lista de campos permitidos", ex);
		}
		
		return listCamposPermitidos;
		}

	public String getId() {
		return id;
	}
	
	/**
	 * Realiza el alta del campo calculado del informe pasado como parámetro
	 * @param un campo calculado
	 */
	public Map<String, Object> altaCamposCalculados (CamposCalculados camposCalculados) throws BusinessException{
		Map<String, Object> parameters = new HashMap<String, Object>();	
		try
		{
			if(mtoCamposCalculadosDao.existeDatosCamposCalculados(camposCalculados)){
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_EXISTE_ALTA_KO));	
			}
			else{
				mtoCamposCalculadosDao.saveOrUpdate(camposCalculados);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CAMPOCALCULADO_ALTA_OK));
				
				// dar de alta operadores por defecto para ese campo calculado.
				mtoOperadoresCamposCalculadosService.altaOperadoresPorDefectoByCamCalc(camposCalculados.getId());
				mtoCamposCalculadosDao.evict(camposCalculados);
			}
			
		
		}
		 catch (Exception e) {
			 parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_ALTA_KO));
			 logger.error("altaCamposCalculados : Error a dar de alta un campo calculado  " + e);
				}
			return parameters;
	}



/**
	 * Realiza la modificacion del campo calculado del informe pasado como parámetro
	 * @param un campo calculado
	 */
	
	
	public Map<String, Object> modificarCamposCalculados(CamposCalculados camposCalculados) throws BusinessException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try {
			
			if(mtoCamposCalculadosDao.existeDatosCamposCalculados(camposCalculados)){
				
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_EXISTE_ALTA_KO));	
			
			}
			else{
				
				mtoCamposCalculadosDao.saveOrUpdate(camposCalculados);	
				mtoCamposCalculadosDao.evict(camposCalculados);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CAMPOCALCULADO_MODIF_OK));
			}
			

				
		} catch (Exception ex) {
			logger.error("modificarCamposCalculados : Error a modificar un campo calculado ", ex);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_MODIF_KO));
			
		}
		return parameters;
	}
	
	public void setMtoOperadoresCamposCalculadosService(
			IMtoOperadoresCamposCalculadosService mtoOperadoresCamposCalculadosService) {
		this.mtoOperadoresCamposCalculadosService = mtoOperadoresCamposCalculadosService;
	}
	
	public void setMtoOperadorCamposCalculadosDao(
			IMtoOperadorCamposCalculadosDao mtoOperadorCamposCalculadosDao) {
		this.mtoOperadorCamposCalculadosDao = mtoOperadorCamposCalculadosDao;
	}
	
}
