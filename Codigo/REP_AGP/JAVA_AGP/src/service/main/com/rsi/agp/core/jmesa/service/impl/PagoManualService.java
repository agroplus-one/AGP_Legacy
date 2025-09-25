/**
 * 
 */
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
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IPagoManualDao;
import com.rsi.agp.core.jmesa.filter.PagoManualFilter;
import com.rsi.agp.core.jmesa.service.IPagoManualService;
import com.rsi.agp.core.jmesa.sort.PagoManualSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.models.admin.IUsuarioDao;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Zona;

/**
 * @author U029769
 *
 */
@SuppressWarnings("deprecation")
public class PagoManualService implements IPagoManualService {

	private IPagoManualDao pagoManualDao;
	private IUsuarioDao usuarioDao;
	private IPolizaDao polizaDao;
	private IEntidadDao entidadDao;
	private String id;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private Log logger = LogFactory.getLog(getClass());
	private HashMap<String, String> columnas = new HashMap<String, String>();

	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String ENTIDAD = "ENTIDAD";
	private final static String DESC_ENTIDAD = "DESC_ENTIDAD";
	private final static String OFICINA = "OFICINA";
	private final static String DESC_OFICINA = "DESC_OFICINA";
	private final static String PAGOMANUAL = "PAGO MANUAL";
	private final static String IDGRUPO = "IDGRUPO";
	private final static String COD_ZONA = "CODZONA";
	
	private PagoManualFilter consultaFilter;
	private PagoManualSort consultaSort;
	private String codZona;

	@Override
	public Collection<Oficina> getOficinasPagoManualWithFilterAndSort(PagoManualFilter filter, PagoManualSort sort,
			int rowStart, int rowEnd, String codZona) throws BusinessException {

		return pagoManualDao.getOficinasPagoManualWithFilterAndSort(filter, sort, rowStart, rowEnd, codZona);

	}

	@Override
	public int getOficinasPagoManualCountWithFilter(PagoManualFilter filter) throws BusinessException {

		return pagoManualDao.getOficinasPagoManualCountWithFilter(filter);
	}

	@Override
	public String getTablaOficinasPagoManual(HttpServletRequest request, HttpServletResponse response, Oficina oficina,
			String origenLlamada) throws BusinessException {
		try {
			TableFacade tableFacade = crearTableFacade(request, response, oficina, origenLlamada);

			Limit limit = tableFacade.getLimit();
			consultaFilter = getConsultaOficinasPagoManualFilter(limit);
			codZona = request.getParameter("zonaSel");

			setDataAndLimitVariables(tableFacade, consultaFilter, limit, codZona);

			String script = "";
			String ajax = request.getParameter("ajax");
			if (!"false".equals(ajax) && request.getParameter("export") == null) {

				String listaIdsTodos = getlistaIdsTodos(consultaFilter);
				script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";

				tableFacade.setToolbar(new CustomToolbarMarcarTodos());
				tableFacade.setView(new CustomView());
				return html(tableFacade) + script;
			} else {
				return html(tableFacade);
			}

		} catch (BusinessException b) {
			logger.error("Error en getTablaOficinasPagoManual", b);
			throw new BusinessException();
		}
	}

	@Override
	public List<Zona> obtenerListaZonas(BigDecimal codEntidad) throws DAOException {

		List<Zona> listaZonas = pagoManualDao.obtenerListaZonas(codEntidad);
		return listaZonas;
	}

	@Override
	public List<String> obtenerListaNombZonasOficina(BigDecimal codEntidad, BigDecimal codoficina) throws DAOException {

		List<String> listaZonas = pagoManualDao.obtenerListaNombZonasOficina(codEntidad, codoficina);
		return listaZonas;
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request, HttpServletResponse response, Oficina oficina,
			String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);

		// Carga las columnas a mostrar en el listado en el TableFacade y devuelve un
		// Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("consultaOficinasPagoManual_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaOficinasPagoManual_LIMIT"));
				}
			} else {
				String zonasStr = request.getParameter("zonaSel");
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario
				cargarFiltrosBusqueda(columnas, oficina, zonasStr, tableFacade);
			}
		}

		return tableFacade;
	}

	/**
	 * 06/05/2014 U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda
		// si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(ENTIDAD, "id.codentidad");
			columnas.put(DESC_ENTIDAD, "entidad.nomentidad");
			columnas.put(OFICINA, "id.codoficina");
			columnas.put(DESC_OFICINA, "nomoficina");
			columnas.put(PAGOMANUAL, "pagoManual");
			columnas.put(IDGRUPO, "idgrupo");
			columnas.put(COD_ZONA, "codZona");
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(ENTIDAD), columnas.get(DESC_ENTIDAD),
				columnas.get(OFICINA), columnas.get(DESC_OFICINA), columnas.get(PAGOMANUAL), columnas.get("CODZONA"));

		return columnas;
	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 06/05/2014 U029769
	 * 
	 * @param columnas2
	 * @param oficina
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas2, Oficina oficina, String zonasStr, TableFacade tableFacade) {
		// ENTIDAD
		if (oficina.getId().getCodentidad() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(ENTIDAD), oficina.getId().getCodentidad().toString()));
		// OFICINA
		if (oficina.getId().getCodoficina() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(OFICINA), oficina.getId().getCodoficina().toString()));
		// PAGOMANUAL
		if (oficina.getPagoManual() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(PAGOMANUAL), oficina.getPagoManual().toString()));
		// NOM OFICINA
		if (oficina.getNomoficina() != null && FiltroUtils.noEstaVacio(oficina.getNomoficina()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(DESC_OFICINA), oficina.getNomoficina().toString()));
		// ZONAS
		if (zonasStr != null && FiltroUtils.noEstaVacio(zonasStr))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(COD_ZONA), zonasStr));
	}

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos
	 * de oficinas y carga el TableFacade con ellas 06/05/2014 U029769
	 * 
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, PagoManualFilter consultaFilter, Limit limit, String codZona) {

		Collection<Oficina> items = new ArrayList<Oficina>();
		try {
			int totalRows = getOficinasPagoManualCountWithFilter(consultaFilter);
			logger.debug("********** count filas para Oficinas con pago manual = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			consultaSort = getConsultaOficinasPagoManualSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getOficinasPagoManualWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd, codZona);
			logger.debug("********** list items para Oficinas con pago manual = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	/**
	 * Crea y configura el Filter para la consulta de oficinas con pago manual
	 * 06/05/2014 U029769
	 * 
	 * @param limit
	 * @return
	 */
	private PagoManualFilter getConsultaOficinasPagoManualFilter(Limit limit) {
		PagoManualFilter consultaFilter = new PagoManualFilter();
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
	 * Crea y configura el Sort para la consulta de oficinas con pago manual
	 * 06/05/2014 U029769
	 * 
	 * @param limit
	 * @return
	 */
	private PagoManualSort getConsultaOficinasPagoManualSort(Limit limit) {
		PagoManualSort consultaSort = new PagoManualSort();
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
	 * Metodo para construir el html de la tabla a mostrar 06/05/2014 U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			Table table = tableFacade.getTable();
			// Quita la columna Id del informe
			 eliminarColumnaId(tableFacade, table);
			// renombramos las cabeceras
			configurarCabecerasColumnasExport(table);

			// Configuracion de los datos de las columnas que requieren un tratamiento para
			// mostrarse
			// campo acciones

			table.getRow().getColumn(columnas.get(PAGOMANUAL)).getCellRenderer()
					.setCellEditor(getCellEditorPagoManual());
			
			table.getRow().getColumn(columnas.get(COD_ZONA)).getCellRenderer().setCellEditor(getCellEditorZonasExport());

			// Escribe los datos generados en el response

			tableFacade.render(); // Will write the export data out to the
									// response.
			return null; // In Spring return null tells the controller not to do
							// anything.
		} else {

			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");

			configurarColumnas(table);

			// Configuracion de los datos de las columnas que requieren un tratamiento para
			// mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
			table.getRow().getColumn(columnas.get(PAGOMANUAL)).getCellRenderer()
					.setCellEditor(getCellEditorPagoManual());
			table.getRow().getColumn(columnas.get(OFICINA)).getCellRenderer().setCellEditor(getCellEditorOficina());
			table.getRow().getColumn(columnas.get(COD_ZONA)).getCellRenderer().setCellEditor(getCellEditorZonas());
		}

		return tableFacade.render();
	}

	/**
	 * Metodo que configura los nombres de las columnas para los informes
	 * 
	 * @param table
	 */
	private void configurarCabecerasColumnasExport(Table table) {

		table.setCaption("Oficinas con Pago Manual");

		Row row = table.getRow();

		row.getColumn("id.codentidad").setTitle("Entidad");
		row.getColumn("entidad.nomentidad").setTitle("Nombre Entidad");
		row.getColumn("id.codoficina").setTitle("Oficina");
		row.getColumn("nomoficina").setTitle("Nombre Oficina");
		row.getColumn("pagoManual").setTitle("Pago Manual");
		row.getColumn("codZona").setTitle("Zonas");
	}

	/**
	 * Configuracion de las columnas de la tabla 06/05/2014 U029769
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "7%");
		configColumna(table, columnas.get(ENTIDAD), "Entidad", true, true, "7%");
		configColumna(table, columnas.get(DESC_ENTIDAD), "Nombre Entidad", true, false, "18%");
		configColumna(table, columnas.get(OFICINA), "Oficina", true, true, "7%");
		configColumna(table, columnas.get(DESC_OFICINA), "Nombre Oficina", true, false, "18%");
		configColumna(table, columnas.get(PAGOMANUAL), "Pago Manual", true, true, "7%");
		configColumna(table, columnas.get(COD_ZONA), "Zonas", true, false, "36%");

	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla 06/05/2014 U029769
	 * 
	 * @param table
	 * @param idCol
	 * @param title
	 * @param filterable
	 * @param sortable
	 * @param width
	 */
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);

	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * 06/05/2014 U029769
	 * 
	 * @return CellEditor
	 */
	private CellEditor getCellEditorAcciones() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal codentidad = (BigDecimal) new BasicCellEditor().getValue(item, "id.codentidad", rowcount);
				BigDecimal codoficina = (BigDecimal) new BasicCellEditor().getValue(item, "id.codoficina", rowcount);
				String nomoficina = (String) new BasicCellEditor().getValue(item, "nomoficina", rowcount);
				String nomentidad = (String) new BasicCellEditor().getValue(item, "entidad.nomentidad", rowcount);
				BigDecimal pagoManual = (BigDecimal) new BasicCellEditor().getValue(item, "pagoManual", rowcount);
				BigDecimal idGrupo = (BigDecimal) new BasicCellEditor().getValue(item, "idgrupo", rowcount);
				String zonasSelStr = "";

				/* Pet. 63701 ** MODIF TAM (25.06.2021) */
				try {
					List<Zona> zonasSel = obtenerListaZonasOficina(codentidad, codoficina);

					if (zonasSel.size() > 0) {
						for (Zona zon : zonasSel) {
							zonasSelStr = zonasSelStr + zon.getId().getCodentidad() + "-" + zon.getId().getCodzona()
									+ ";";
						}
					}
					logger.debug("Valor de zonasSelStr:-" + zonasSelStr + "-");
				} catch (Exception ex) {
					logger.debug("Error al obtener las zonas", ex);
				}
				// DNF 4/12/2018
				if (idGrupo == null) {
					idGrupo = BigDecimal.ZERO;
				}

				HtmlBuilder html = new HtmlBuilder();

				// checkbox cambio masivo
				html.append("<input type=\"checkbox\" id=\"check_" + codentidad + "_" + codoficina + "\"  name=\"check_"
						+ codentidad + "_" + codoficina + "\" onClick =\"listaCheckId(\'" + codentidad + "_"
						+ codoficina + "')\" class=\"dato\"/>");
				html.append("&nbsp;");

				// boton editar
				html.a().href().quote()
						.append("javascript:editar(" + codentidad + "," + codoficina + "," + pagoManual + ",'"
								+ nomoficina + "','" + nomentidad + "'," + idGrupo + ",'" + zonasSelStr + "');")
						.quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Oficina\" title=\"Editar Oficina\"/>");
				html.aEnd();
				html.append("&nbsp;");

				// boton borrar
				html.a().href().quote().append("javascript:borrar(" + codentidad + "," + codoficina + ",'" + nomoficina
						+ "'," + pagoManual + ",'" + nomentidad + "');").quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Oficina\" title=\"Borrar Oficina\"/>");
				html.aEnd();
				html.append("&nbsp;");

				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorPagoManual() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal pagoManual = (BigDecimal) new BasicCellEditor().getValue(item, "pagoManual", rowcount);

				HtmlBuilder html = new HtmlBuilder();

				if (pagoManual.compareTo(new BigDecimal(0)) == 0) {
					html.append("No");
				} else if (pagoManual.compareTo(new BigDecimal(1)) == 0) {
					html.append("Si");
				}
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorOficina() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal oficina = (BigDecimal) new BasicCellEditor().getValue(item, "id.codoficina", rowcount);
				String codOficina = oficina.toString();
				HtmlBuilder html = new HtmlBuilder();

				if (codOficina.length() < 4) {
					while (codOficina.length() < 4) {
						codOficina = "0" + codOficina;
					}
				}
				html.append(codOficina);
				return html.toString();
			}
		};
	}
	
	/**
     * metodo para eliminar la columna Id en los informes
     * @param tableFacade
     * @param table
     */
    private void eliminarColumnaId(TableFacade tableFacade, Table table){
    	Row row = table.getRow();
        Row rowFinal = new Row();
        List<Column> lstColumns = row.getColumns();
        for (Column col:lstColumns){
        	if (null != col.getProperty() && !col.getProperty().equals("null")){
	        	if (!col.getProperty().equals("id")){
	        		rowFinal.addColumn(col);
	        	}
        	}
        }
        table.setRow(rowFinal);
        tableFacade.setTable(table);
    }

	@Override
	public Map<String, Object> altaOficina(Oficina oficinaBean, List<Zona> zonaListSel) throws BusinessException {

		logger.debug("PagoManualService - altaOficina [INIT]");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// comprobamos que la entidad existe
			if (!entidadDao.existeEntidad(oficinaBean.getId().getCodentidad())) {
				parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.alta.EntidadNoExiste"));

				// comprobamos que no exista ya la entidad y oficina
			} else if (entidadDao.existeEntidadOficina(oficinaBean.getId().getCodentidad(),
					oficinaBean.getId().getCodoficina())) {
				parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.alta.EntOfiExiste"));

			} else {
				pagoManualDao.saveOrUpdate(oficinaBean);
				/* Pet. 63701 ** MODIF TAM (24.06.2021) */
				/* Añadimos el alta de las zonas de las oficinas marcadas por pantalla */
				if (zonaListSel != null) {
					pagoManualDao.guardarZonasOficina(oficinaBean, zonaListSel);
				}
				parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
			}

		} catch (Exception ex) {
			logger.debug("Error en el alta de oficinas pago manual", ex);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
		}

		logger.debug("PagoManualService - altaOficina [END]");
		return parameters;
	}

	@Override
	public Map<String, Object> editaOficina(Oficina oficinaBean, List<Zona> zonaListSel) throws BusinessException {

		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			// DNF 4/12/2018
			if (oficinaBean.getIdgrupo().intValueExact() == 0) {
				oficinaBean.setIdgrupo(null);
			}

			pagoManualDao.saveOrUpdate(oficinaBean);
			pagoManualDao.editaZonasOficina(oficinaBean, zonaListSel);
			parameters.put("mensaje", bundle.getString("mensaje.oficinaPagoManual.edicion.OK"));

		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar oficinas pago manual", ex);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
		} catch (Exception ex) {
			logger.debug("Error al editar oficinas pago manual", ex);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
		}
		return parameters;
	}

	@Override
	public Map<String, Object> borraOficina(Oficina oficinaBean) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// comprobamos que la oficina no tenga usuarios asociados
			if (usuarioDao.isOficinaConUsuarios(oficinaBean)) {
				parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.usuarioAsociado"));
				// comprobamos que la oficina no tenga polizas asociadas
			} else if (polizaDao.isOficinaConPolizas(oficinaBean.getId().getCodoficina().toString(),
					oficinaBean.getId().getCodentidad())) {
				parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.polizaAsociada"));

			} else {
				pagoManualDao.delete(oficinaBean);
				/* Pet. 63701 ** MODIF TAM (25.06.2021) ** Inicio */
				pagoManualDao.borrarZonasOficinas(oficinaBean);
				parameters.put("mensaje", bundle.getString("mensaje.oficinaPagoManual.borrar.OK"));
			}

		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al borrar oficinas pago manual", ex);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.KO"));
		} catch (Exception ex) {
			logger.debug("Error al borrar oficinas pago manual", ex);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.KO"));
		}
		return parameters;

	}

	/**
	 * Metodo para recuperar un String con todos los Ids de oficinas segun el filtro
	 * 
	 * @param consultaFilter
	 * @return listaIdsTodos
	 * @throws BusinessException
	 */
	public String getlistaIdsTodos(PagoManualFilter consultaFilter) throws BusinessException {
		String listaIdsTodos;
		try {
			listaIdsTodos = pagoManualDao.getlistaIdsTodos(consultaFilter);
		} catch (DAOException e) {
			logger.error("error en getlistaIdsTodos", e);
			throw new BusinessException();
		}
		return listaIdsTodos;

	}

	@Override
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, Oficina oficinaBean, List<Zona> zonaListSel) {

		Map<String, String> parameters = new HashMap<String, String>();
		try {
			String listaIds = listaIdsMarcados_cm.substring(0, listaIdsMarcados_cm.length() - 1);

			pagoManualDao.cambioMasivo(listaIds, oficinaBean);
			
			/* Resolución Defecto Nº 12 ** MODIF TAM (27.08.2021) ** Inicio */
			if (zonaListSel != null) {
				pagoManualDao.cambioMasivoZonas(listaIds, zonaListSel);
			} else {
				pagoManualDao.cambioMasivoZonas(listaIds, new ArrayList<Zona>());
			}
			/* Resolución Defecto Nº 12 ** MODIF TAM (27.08.2021) ** Fin */
			
			parameters.put("mensaje", bundle.getString("mensaje.oficinaPagoManual.edicion.OK"));

		} catch (DAOException e) {
			logger.debug("Error al ejecutar el Cambio Masivo ", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
			return parameters;
		}
		return parameters;
	}

	/* Pet. 63701 ** MODIF TAM (25.06.2021) Inicio **/
	/* Recuperamos la lista de zonas para la entidad y oficina de la fila */
	private CellEditor getCellEditorZonas() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal oficina = (BigDecimal) new BasicCellEditor().getValue(item, "id.codoficina", rowcount);
				BigDecimal entidad = (BigDecimal) new BasicCellEditor().getValue(item, "id.codentidad", rowcount);
				String listaNombZonas = "";
				HtmlBuilder html = new HtmlBuilder();

				try {

					List<String> ListZonas = pagoManualDao.obtenerListaNombZonasOficina(entidad, oficina);
					if (ListZonas.size() > 0) {
						for (String nombZona : ListZonas) {
							listaNombZonas = listaNombZonas + nombZona + ";" + "</br>";
						}

					}
					html.append(listaNombZonas);
				} catch (Exception ex) {
					logger.debug("Error al obtener las zonas", ex);
				}
				return html.toString();
			}
		};
	}

	/* Pet. 63701 ** MODIF TAM (25.06.2021) Inicio **/
	/* Recuperamos la lista de zonas para la entidad y oficina de la fila */
	private CellEditor getCellEditorZonasExport() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal oficina = (BigDecimal) new BasicCellEditor().getValue(item, "id.codoficina", rowcount);
				BigDecimal entidad = (BigDecimal) new BasicCellEditor().getValue(item, "id.codentidad", rowcount);
				String listaNombZonas = "";
				HtmlBuilder html = new HtmlBuilder();

				try {

					List<String> ListZonas = pagoManualDao.obtenerListaNombZonasOficina(entidad, oficina);
					if (ListZonas.size() > 0) {
						for (String nombZona : ListZonas) {
							listaNombZonas = listaNombZonas + nombZona + ";";
						}

					}
					html.append(listaNombZonas);
				} catch (Exception ex) {
					logger.debug("Error al obtener las zonas", ex);
				}
				return html.toString();
			}
		};
	}

	@Override
	public List<Zona> obtenerListaZonasOficina(BigDecimal codEntidad, BigDecimal codoficina) throws DAOException {

		List<Zona> listaZonas = pagoManualDao.obtenerListaZonasOficina(codEntidad, codoficina);
		return listaZonas;
	}

	@Override
	public Oficina getCambioMasivoBeanFromLimit(Limit consultaOficinasPagoManual_LIMIT) {
		Oficina cambioMasivoOficina = new Oficina();

		if (null != consultaOficinasPagoManual_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD))) {
			cambioMasivoOficina.getId().setCodentidad(new BigDecimal(
					consultaOficinasPagoManual_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD)).getValue()));
		}
		if (null != consultaOficinasPagoManual_LIMIT.getFilterSet().getFilter(columnas.get(OFICINA))) {
			cambioMasivoOficina.getId().setCodoficina(new BigDecimal(
					consultaOficinasPagoManual_LIMIT.getFilterSet().getFilter(columnas.get(OFICINA)).getValue()));
		}
		if (null != consultaOficinasPagoManual_LIMIT.getFilterSet().getFilter(columnas.get(PAGOMANUAL))) {
			cambioMasivoOficina.setPagoManual(new BigDecimal(
					consultaOficinasPagoManual_LIMIT.getFilterSet().getFilter(columnas.get(PAGOMANUAL)).getValue()));
		}

		return cambioMasivoOficina;
	}

	@Override
	public Map<String, String> adiccionMasiva(String listaIdsMarcados_cm, Oficina oficinaBean, List<Zona> zonaListSel) {

		Map<String, String> parameters = new HashMap<String, String>();
		try {
			String listaIds = listaIdsMarcados_cm.substring(0, listaIdsMarcados_cm.length() - 1);
			/* Resolución Defecto Nº 12 ** MODIF TAM (27.08.2021) ** Inicio */
			if (zonaListSel != null) {
				pagoManualDao.adiccionMasivaZonas(listaIds, zonaListSel);
			}
				
			parameters.put("mensaje", bundle.getString("mensaje.oficinaPagoManual.edicion.OK"));

		} catch (DAOException e) {
			logger.debug("Error al ejecutar el Cambio Masivo ", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
			return parameters;
		}
		return parameters;
	}
	/* Pet. 63701 ** MODIF TAM (25.06.2021) Fin **/

	public void setPagoManualDao(IPagoManualDao pagoManualDao) {
		this.pagoManualDao = pagoManualDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUsuarioDao(IUsuarioDao usuarioDao) {
		this.usuarioDao = usuarioDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setEntidadDao(IEntidadDao entidadDao) {
		this.entidadDao = entidadDao;
	}
	
	// Metodo para obtener la lista de zonas y convertirla en una cadena separada por punto y coma
		private String getZonasAsStringForOficina(Oficina oficina) {
		    try {
		        List<String> zonas = pagoManualDao.obtenerListaNombZonasOficina(oficina.getId().getCodentidad(), oficina.getId().getCodoficina());
		        StringBuilder sb = new StringBuilder();
		        boolean first = true;
		        for (String zona : zonas) {
		            if (!first) {
		                sb.append("; ");
		            }
		            sb.append(zona);
		            first = false;
		        }
		        return sb.toString();
		    } catch (DAOException e) {
		        logger.error("Error al obtener la lista de zonas para la oficina " + oficina.getId().getCodoficina() + " de la entidad " + oficina.getId().getCodentidad(), e);
		        return "";
		    }
		}
		
	@Override
	public List<Oficina> getAllFilteredAndSorted() throws BusinessException {
		
		List<Oficina> listadoOficinasPagoManual = (List<Oficina>) getOficinasPagoManualWithFilterAndSort(consultaFilter, consultaSort, -1, -1, codZona);
		for (Oficina oficina : listadoOficinasPagoManual) {
		    oficina.setZonasAsString(getZonasAsStringForOficina(oficina));
		}
		
		return (List<Oficina>) getOficinasPagoManualWithFilterAndSort(consultaFilter, consultaSort, -1, -1, codZona);
	}

}
