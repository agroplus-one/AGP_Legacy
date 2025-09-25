package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClasificacionRupturaCamposGenericosFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoClasificacionRupturaCamposGenericosService;
import com.rsi.agp.core.jmesa.sort.ClasificacionRupturaCamposGenericosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoClasificacionRupturaCamposGenericosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoDatosInformeDao;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfClasificacionRuptura;

@SuppressWarnings("deprecation")
public class MtoClasificacionRupturaCamposGenericosService implements
		IMtoClasificacionRupturaCamposGenericosService {

	private IMtoClasificacionRupturaCamposGenericosDao mtoClasificacionRupturaCamposGenericosDao;
	private IMtoDatosInformeDao mtoDatosInformeDao;
	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();

	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");

	private String id;

	private Log logger = LogFactory.getLog(getClass());

	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String NOMBRE = "NOMBRE";
	private final static String SENTIDO = "SENTIDO";
	private final static String RUPTURA = "RUPTURA";

	@Override
	public int getConsultaClasificacionRupturaGenericoCountWithFilter(
			ClasificacionRupturaCamposGenericosFilter filter,
			BigDecimal informeId) {

		return mtoClasificacionRupturaCamposGenericosDao
				.getClasificacionRupturaCountWithFilter(filter,informeId);
	}

	@Override
	public Collection<VistaMtoinfClasificacionRuptura> getClasificacionRupturaGenericoWithFilterAndSort(
			ClasificacionRupturaCamposGenericosFilter filter,
			ClasificacionRupturaCamposGenericosSort sort,BigDecimal informeId,
			int rowStart, int rowEnd) throws BusinessException {

		Collection<VistaMtoinfClasificacionRuptura> colClasificacionRuptura = null;
		try {
			colClasificacionRuptura = mtoClasificacionRupturaCamposGenericosDao
					.getClasificacionRupturaWithFilterAndSort(filter, sort,informeId,
							 rowStart, rowEnd);
		} catch (Exception ex) {
			throw new BusinessException(
					"Se ha producido al obtener getClasificacionRupturaGenericoWithFilterAndSort:",
					ex);
		}

		return colClasificacionRuptura;
	}

	public String getTablaClasificacionRuptura(HttpServletRequest request,
			HttpServletResponse response,
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura,
			String origenLlamada) {

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,vistaMtoinfClasificacionRuptura, origenLlamada);

		// Configura el filtro y la ordenación, busca los datos y las carga en
		// el TableFacade
		setDataAndLimitVariables(tableFacade,request,vistaMtoinfClasificacionRuptura.getIdinforme(),origenLlamada);

		if (request.getSession().getAttribute("pageSession") != null && origenLlamada != null && origenLlamada.equals("borrar")){
			int pageSession = (Integer)(request.getSession().getAttribute("pageSession"));
			tableFacade.getLimit().getRowSelect().setPage(pageSession);
		}
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());
		
		request.getSession().setAttribute("pageSession", tableFacade.getLimit().getRowSelect().getPage());
        request.getSession().setAttribute("rowStart", tableFacade.getLimit().getRowSelect().getRowStart());
        request.getSession().setAttribute("rowEnd", tableFacade.getLimit().getRowSelect().getRowEnd());
	
		return html(tableFacade);

	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de
	 * clasificacionRuptura
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura,
			String origenLlamada) {

		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);
		tableFacade.setStateAttr("restore");// return to the table in the same
		
		// Carga las columnas a mostrar en el listado en el TableFacade y
		cargarColumnas(tableFacade);

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"mtoConsultaClasificacionRuptura_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute(
									"mtoConsultaClasificacionRuptura_LIMIT"));
				}

			}
			
			if (vistaMtoinfClasificacionRuptura.getId() != null && vistaMtoinfClasificacionRuptura.getId().getIddatoInforme() !=null ){
    			Filter filterIddatoInforme = new Filter("id.iddatoInforme", vistaMtoinfClasificacionRuptura.getId().getIddatoInforme().toString());
    			tableFacade.getLimit().getFilterSet().addFilter(filterIddatoInforme);
    		}
			
			if (vistaMtoinfClasificacionRuptura.getRuptura() !=null ){
    			Filter filterRuptura = new Filter("ruptura", vistaMtoinfClasificacionRuptura.getRuptura().toString());
    			tableFacade.getLimit().getFilterSet().addFilter(filterRuptura);
    		}
			
			if (vistaMtoinfClasificacionRuptura.getSentido()!=null ){
    			Filter filterSentido = new Filter("sentido", vistaMtoinfClasificacionRuptura.getSentido().toString());
    			tableFacade.getLimit().getFilterSet().addFilter(filterSentido);
    		}
			
		}
			
			
			
		
		return tableFacade;

	}

	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {

		// Crea el Map con las columnas del listado y los campos del filtro de
		// búsqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(NOMBRE, "nombre");
			columnas.put(SENTIDO, "sentido");
			columnas.put(RUPTURA, "ruptura");

		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(NOMBRE),
				columnas.get(SENTIDO), columnas.get(RUPTURA));

		return columnas;
	}

	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los
	 * datos de Campos Calculados y carga el TableFacade con ellas
	 * 
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,HttpServletRequest request,BigDecimal informeId,String origenLlamada) {

		Limit limit = tableFacade.getLimit();
		ClasificacionRupturaCamposGenericosFilter consultaFilter = getConsultaClasificacionRupturaGenericoFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getConsultaClasificacionRupturaGenericoCountWithFilter(
				consultaFilter,informeId);
		logger.debug("********** count filas para campos calculados = "
				+ totalRows + " **********");

		// y lo establecemos al tableFacade antes de obtener la fila de inicio y
		// la de fin
		tableFacade.setTotalRows(totalRows);

		// Crea el Sort para la búsqueda campos calculados
		ClasificacionRupturaCamposGenericosSort consultaSort = getConsultaClasificacionRupturaGenericoSort(limit);
		 int rowStart = 0;
	       int rowEnd = 0; 
	       if (origenLlamada != null && origenLlamada.equals("borrar")){
	    	   rowStart = (Integer)request.getSession().getAttribute("rowStart");
	       	   rowEnd = (Integer)request.getSession().getAttribute("rowEnd");
	       }else{
	    	   rowStart = limit.getRowSelect().getRowStart();
	       	   rowEnd = limit.getRowSelect().getRowEnd();
	       }
		Collection<VistaMtoinfClasificacionRuptura> items = new ArrayList<VistaMtoinfClasificacionRuptura>();
		// Obtiene los registros que cumplen el filtro
		try {
			items = getClasificacionRupturaGenericoWithFilterAndSort(
					consultaFilter, consultaSort,informeId,rowStart, rowEnd);
			logger.debug("********** list items para campos calculados = "
					+ items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e);
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);

	}

	/**
	 * Metodo para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");

		// Configuración de los datos de las columnas que requieren un
		// tratamiento para mostrarse
		// campo acciones
		table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer()
				.setCellEditor(getCellEditorAcciones());
		table.getRow().getColumn(columnas.get(NOMBRE)).getCellRenderer()
				.setCellEditor(getCellEditorNombre());
		table.getRow().getColumn(columnas.get(SENTIDO)).getCellRenderer()
				.setCellEditor(getCellEditorSentido());
		table.getRow().getColumn(columnas.get(RUPTURA)).getCellRenderer()
				.setCellEditor(getCellEditorRuptura());

		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render(); // Will write the export data out to the
			// response.
			return null; // In Spring return null tells the controller not to
			// do anything.
		} else {
			// Configuración de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer()
					.setCellEditor(getCellEditorAcciones());
		}

		return tableFacade.render();
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				try {
					BigDecimal iddatoInforme = (BigDecimal) new BasicCellEditor()
							.getValue(item, "id.iddatoInforme", rowcount);
				
					BigDecimal sentido = (BigDecimal) new BasicCellEditor()
							.getValue(item, "sentido", rowcount);
					BigDecimal ruptura = (BigDecimal) new BasicCellEditor()
							.getValue(item, "ruptura", rowcount);
					BigDecimal permitidoOcalculado = (BigDecimal) new BasicCellEditor()
							.getValue(item, "id.permitidocalculado", rowcount);
					BigDecimal idClasifRupt = (BigDecimal)new BasicCellEditor()
							.getValue(item, "idClasifRupt", rowcount);
					
					// botón editar
					html.a().href().quote().append(
							"javascript:editar('"+idClasifRupt+"','"+sentido+"','"+ruptura+"','"+iddatoInforme+"','"+permitidoOcalculado+"');").quote().close();
					html
							.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar una clasificaci&oacute;n ruptura\" title=\"Editar una clasificaci&oacute;n ruptura\"/>");
					html.aEnd();
					html.append("&nbsp;");

					// botón borrar
					html.a().href().quote().append(
							"javascript:borrar('"+idClasifRupt+"','"+iddatoInforme+"','"+permitidoOcalculado+"');").quote().close();
					html
							.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar una clasificaci&oacute;n ruptura\" title=\"Borrar una clasificaci&oacute;n ruptura\"/>");
					html.aEnd();
					html.append("&nbsp;");
					// botón visualizar
					html.a().href().quote().append(
							"javascript:visualizar('"+idClasifRupt+"','"+sentido+"','"+ruptura+"','"+iddatoInforme+"','"+permitidoOcalculado+"');").quote().close();
					html
							.append("<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar una clasificaci&oacute;n ruptura\" title=\"Visualizar una clasificaci&oacute;n ruptura\"/>");
					html.aEnd();
					html.append("&nbsp;");

					
				} catch (Exception ex) {
					
				}
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la columna 'Columna'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorNombre() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				String nombre = null;
				try {
					nombre = (String) new BasicCellEditor().getValue(item,
							"nombre", rowcount);
				} catch (Exception e) {
					logger.debug("nombre nulo");
				}

				HtmlBuilder html = new HtmlBuilder();
				
				html.append(StringUtils.nullToString(nombre) + "&nbsp;");

				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la columna 'Sentido'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorSentido() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				String sentido = "";
				try {

					if (((BigDecimal) new BasicCellEditor().getValue(item,
							"sentido", rowcount)).intValue() == 0)
						sentido = ConstantsInf.ORDENACION_ASC;
					else if (((BigDecimal) new BasicCellEditor().getValue(item,
							"sentido", rowcount)).intValue() == 1)
						sentido = ConstantsInf.ORDENACION_DESC;

				} catch (Exception e) {
					logger.debug("sentido nulo");
				}

				HtmlBuilder html = new HtmlBuilder();
				// botón editar
				html.append(StringUtils.nullToString(sentido) + "&nbsp;");

				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la columna 'Ruptura'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorRuptura() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				String ruptura = "";
				try {

					if (((BigDecimal) new BasicCellEditor().getValue(item,
							"ruptura", rowcount)).intValue() == 0)
						ruptura = "NO";
					else if (((BigDecimal) new BasicCellEditor().getValue(item,
							"ruptura", rowcount)).intValue() == 1)
						ruptura = "SI";

				} catch (Exception e) {
					logger.debug("sentido nulo");
				}

				HtmlBuilder html = new HtmlBuilder();

				html.append(StringUtils.nullToString(ruptura) + "&nbsp;");

				return html.toString();
			}
		};
	}

	/**
	 * Configuración de las columnas de la tabla
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {

		// Acciones
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false,
				false, "6%");

		configColumna(table, columnas.get(NOMBRE), "Abreviado", true, true,
				"10%");
		configColumna(table, columnas.get(SENTIDO), "Sentido", true, true,
				"10%");
		configColumna(table, columnas.get(RUPTURA), "Ruptura", true, true,
				"10%");

	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parámetro y los incluye en la tabla
	 * 
	 * @param table
	 *            Objeto que contiene la tabla
	 * @param idCol
	 *            Id de la columna
	 * @param title
	 *            Título de la columna
	 * @param filterable
	 *            Indica si se podrá buscar por esa columna
	 * @param sortable
	 *            Indica si se podrá ordenar por esa columna
	 * @param width
	 *            Ancho de la columna
	 */
	private void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}

	/**
	 * Crea y configura el Filter para la consulta de claseificacionRupturaCamposGenericos
	 * 
	 * @param limit
	 * @return
	 */
	private ClasificacionRupturaCamposGenericosFilter getConsultaClasificacionRupturaGenericoFilter(
			Limit limit) {
		ClasificacionRupturaCamposGenericosFilter consultaFilter = new ClasificacionRupturaCamposGenericosFilter();
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
	 * Crea y configura el Sort para la claseificacionRupturaCamposGenericos
	 * 
	 * @param limit
	 * @return
	 */
	private ClasificacionRupturaCamposGenericosSort getConsultaClasificacionRupturaGenericoSort(
			Limit limit) {

		ClasificacionRupturaCamposGenericosSort consultaSort = new ClasificacionRupturaCamposGenericosSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}

		return consultaSort;
	}

	public VistaMtoinfClasificacionRuptura getClasificacionRuptura(
			Long idCamposCalculados) throws BusinessException {
		try {
			return (VistaMtoinfClasificacionRuptura) mtoClasificacionRupturaCamposGenericosDao
					.get(VistaMtoinfClasificacionRuptura.class,
							idCamposCalculados);

		} catch (Exception dao) {
			logger
					.error("Se ha producido error al obtener el campo calculado: "
							+ dao);
			throw new BusinessException(
					"Se ha producido al obtener el campo calculado:", dao);
		}
	}

	

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/**
	 * Realiza el alta del dato del informe pasado como parámetro
	 * 
	 * @param informeId
	 * @param datosInformeBean
	 */
	public Map<String, Object> altaClasificacionRupturaGenerico(
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) throws BusinessException{
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			
			//Comprobamos si ya existe
			if (mtoClasificacionRupturaCamposGenericosDao.existeDatosClasificacionRuptura(vistaMtoinfClasificacionRuptura)){
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CLASIFRUPTURAINFORME_EXISTE_KO));
			} else {
				if (vistaMtoinfClasificacionRuptura.getId()
						.getPermitidocalculado().compareTo(ConstantsInf.CAMPO_CALCULADO) == 0) {
					
					ClasificacionRupturaCamposCalculados clasificacionRuptura = new ClasificacionRupturaCamposCalculados();
					DatoInformes datoInformes = (DatoInformes) mtoDatosInformeDao
						.get(DatoInformes.class, 
								vistaMtoinfClasificacionRuptura.getId().getIddatoInforme().longValue());
						
					clasificacionRuptura.setDatoInformes(datoInformes);
					clasificacionRuptura.setRuptura(vistaMtoinfClasificacionRuptura.getRuptura());
					clasificacionRuptura.setSentido(vistaMtoinfClasificacionRuptura.getSentido());
					clasificacionRuptura = (ClasificacionRupturaCamposCalculados)mtoClasificacionRupturaCamposGenericosDao.saveOrUpdate(clasificacionRuptura);
					vistaMtoinfClasificacionRuptura.setIdClasifRupt(new BigDecimal(clasificacionRuptura.getId()));
				
				} else if (vistaMtoinfClasificacionRuptura.getId()
						.getPermitidocalculado().compareTo(ConstantsInf.CAMPO_PERMITIDO) == 0) {
					
					ClasificacionRupturaCamposPermitidos clasificacionRuptura = new ClasificacionRupturaCamposPermitidos();
					DatoInformes datoInformes = (DatoInformes) mtoDatosInformeDao
					.get(DatoInformes.class, 
							vistaMtoinfClasificacionRuptura.getId().getIddatoInforme().longValue());
					
					clasificacionRuptura.setDatoInformes(datoInformes);
					clasificacionRuptura.setRuptura(vistaMtoinfClasificacionRuptura.getRuptura());
					clasificacionRuptura.setSentido(vistaMtoinfClasificacionRuptura.getSentido());
					clasificacionRuptura = (ClasificacionRupturaCamposPermitidos)mtoClasificacionRupturaCamposGenericosDao.saveOrUpdate(clasificacionRuptura);
					vistaMtoinfClasificacionRuptura.setIdClasifRupt(new BigDecimal(clasificacionRuptura.getId()));
				}
				parameters.put("mensaje", bundle
						.getObject(ConstantsInf.MSG_CLASIFRUPTURAINFORME_ALTA_OK));
				
			}
		} catch (DAOException e) {
			logger.debug("altaCampoInforme error. " + e);
			parameters.put("mensaje", bundle
					.getObject(ConstantsInf.ALERTA_CLASIFRUPTURAINFORME_ALTA_KO));
			throw new BusinessException("Error al dar de alta el campo de clasificacion ruptura generico",e);
		}
		return parameters;
	}

	
	/**
	 * Realiza la modificacion del dato del informe pasado como parámetro
	 * @param datoInformes
	 */
	public Map<String, Object> modificarClasificacionRupturaGenerico(
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura)
			throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		ClasificacionRupturaCamposPermitidos clasificacionRupturaPermitido = null;
		ClasificacionRupturaCamposCalculados clasificacionRupturaCalculado = null;
		
		try {
			
			if (mtoClasificacionRupturaCamposGenericosDao.existeDatosClasificacionRuptura(vistaMtoinfClasificacionRuptura)){
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CLASIFRUPTURAINFORME_EXISTE_KO));
			
			} else {
			
				if (vistaMtoinfClasificacionRuptura.getId().getPermitidocalculado().compareTo(ConstantsInf.CAMPO_CALCULADO) == 0) {
				
					clasificacionRupturaCalculado = (ClasificacionRupturaCamposCalculados) mtoClasificacionRupturaCamposGenericosDao.getObject(ClasificacionRupturaCamposCalculados.class,vistaMtoinfClasificacionRuptura.getIdClasifRupt().longValue());
					DatoInformes datoInformes = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class, 
						vistaMtoinfClasificacionRuptura.getId().getIddatoInforme().longValue());
					clasificacionRupturaCalculado.setDatoInformes(datoInformes);
					clasificacionRupturaCalculado.setRuptura(vistaMtoinfClasificacionRuptura.getRuptura());
				clasificacionRupturaCalculado.setSentido(vistaMtoinfClasificacionRuptura.getSentido());
					mtoClasificacionRupturaCamposGenericosDao.saveOrUpdate(clasificacionRupturaCalculado);
				
			} else if (vistaMtoinfClasificacionRuptura.getId()
					.getPermitidocalculado().compareTo(ConstantsInf.CAMPO_PERMITIDO) == 0) {
				
					clasificacionRupturaPermitido = (ClasificacionRupturaCamposPermitidos) mtoClasificacionRupturaCamposGenericosDao.getObject(ClasificacionRupturaCamposPermitidos.class,vistaMtoinfClasificacionRuptura.getIdClasifRupt().longValue());
					DatoInformes datoInformes = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class, 
						vistaMtoinfClasificacionRuptura.getId().getIddatoInforme().longValue());
					clasificacionRupturaPermitido.setDatoInformes(datoInformes);
					clasificacionRupturaPermitido.setRuptura(vistaMtoinfClasificacionRuptura.getRuptura());
					clasificacionRupturaPermitido.setSentido(vistaMtoinfClasificacionRuptura.getSentido());
					mtoClasificacionRupturaCamposGenericosDao.saveOrUpdate(clasificacionRupturaPermitido);
				
			}
			parameters.put("mensaje", bundle
						.getObject(ConstantsInf.MSG_CLASIFRUPTURAINFORME_MODIF_OK));
			
		}
			
			
		}	catch (Exception ex) {
			parameters.put("mensaje", bundle
					.getObject(ConstantsInf.ALERTA_CLASIFRUPTURAINFORME_MODIF_KO));
			logger.error("Error a modificar un dato de los informes", ex);
			throw new BusinessException("Error al modificar el campo calculado",
					ex);
		}
		return parameters;
	}
	
	public Map<String, Object> bajaClasificacionRuptura(
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura)
			throws BusinessException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ClasificacionRupturaCamposPermitidos clasificacionRupturaPermitido = null;
		ClasificacionRupturaCamposCalculados clasificacionRupturaCalculado = null;
		try {
			if (vistaMtoinfClasificacionRuptura.getId().getPermitidocalculado().compareTo(ConstantsInf.CAMPO_CALCULADO) == 0) {
				
				clasificacionRupturaCalculado = (ClasificacionRupturaCamposCalculados) mtoClasificacionRupturaCamposGenericosDao.getObject(ClasificacionRupturaCamposCalculados.class,vistaMtoinfClasificacionRuptura.getIdClasifRupt().longValue());
				mtoClasificacionRupturaCamposGenericosDao.delete(clasificacionRupturaCalculado);
			
			} else if (vistaMtoinfClasificacionRuptura.getId().getPermitidocalculado().compareTo(ConstantsInf.CAMPO_PERMITIDO) == 0) {
				
				clasificacionRupturaPermitido = (ClasificacionRupturaCamposPermitidos) mtoClasificacionRupturaCamposGenericosDao.getObject(ClasificacionRupturaCamposPermitidos.class,vistaMtoinfClasificacionRuptura.getIdClasifRupt().longValue());
				mtoClasificacionRupturaCamposGenericosDao.delete(clasificacionRupturaPermitido);
			}
			
			parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CLASIFRUPTURAINFORME_BAJA_OK));

		} catch (Exception ex) {
			
			parameters.put("mensaje", bundle.getObject(ConstantsInf.ALERTA_CLASIFRUPTURAINFORME_BAJA_KO));
			logger.error("Error al borrar un dato de los informes", ex);
			throw new BusinessException("Error al borrar el campos calculados",
					ex);
		}
		return parameters;
	}
	

	
	
	
	public IMtoDatosInformeDao getMtoDatosInformeDao() {
		return mtoDatosInformeDao;
	}

	public void setMtoDatosInformeDao(IMtoDatosInformeDao mtoDatosInformeDao) {
		this.mtoDatosInformeDao = mtoDatosInformeDao;
	}

	public IMtoClasificacionRupturaCamposGenericosDao getMtoClasificacionRupturaCamposGenericosDao() {
		return mtoClasificacionRupturaCamposGenericosDao;
	}

	public void setMtoClasificacionRupturaCamposGenericosDao(
			IMtoClasificacionRupturaCamposGenericosDao mtoClasificacionRupturaCamposGenericosDao) {
		this.mtoClasificacionRupturaCamposGenericosDao = mtoClasificacionRupturaCamposGenericosDao;
	}

}
