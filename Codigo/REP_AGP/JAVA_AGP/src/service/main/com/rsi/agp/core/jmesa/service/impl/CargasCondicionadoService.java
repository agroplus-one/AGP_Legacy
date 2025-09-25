package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.limit.state.State;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CondicionadoFilter;
import com.rsi.agp.core.jmesa.service.ICargasCondicionadoService;
import com.rsi.agp.core.jmesa.service.ICargasFicherosService;
import com.rsi.agp.core.jmesa.sort.CondicionadoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.imp.ICargasCondicionadoDao;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;

public class CargasCondicionadoService implements ICargasCondicionadoService {

	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private ICargasCondicionadoDao cargasCondicionadoDao;
	private ICargasFicherosService cargasFicherosService;

	public int getCondicionadosCountWithFilter(CondicionadoFilter filter) {
		return cargasCondicionadoDao.getCondicionadosCountWithFilter(filter);
	}

	public Collection<CargasCondicionado> getCondicionadosWithFilterAndSort(
			CondicionadoFilter filter, CondicionadoSort sort, int rowStart,
			int rowEnd) throws BusinessException {

		return cargasCondicionadoDao.getCondicionadosWithFilterAndSort(filter,
				sort, rowStart, rowEnd);

	}

	/**
	 * Busca las cargas de condicionado y genera la tabla para presentarlas
	 * 
	 * @param request
	 * @param response
	 * @param Objeto
	 *            que encapsula el filtro de la búsqueda
	 * @return Código de la tabla de presentación de las cargas de condicionado
	 */
	public String getTablaCargasCondicionado(HttpServletRequest request,
			HttpServletResponse response,
			CargasCondicionado cargasCondicionado, String origenLlamada) {

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				cargasCondicionado, origenLlamada);

		// Configura el filtro y la ordenación, busca las cargas de condicionado
		// y las carga en el TableFacade
		setDataAndLimitVariables(tableFacade);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade);
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de cargas
	 * de condicionado
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			CargasCondicionado cargasCondicionado, String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		// Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoCargasCondicionado_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoCargasCondicionado_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(cargasCondicionado, tableFacade);

				// -- ORDENACIÓN POR DEFECTO --> ORDER ASC -> FechaCreacion
				tableFacade.getLimit().getSortSet().addSort(
						new Sort(CargasCondicionado.POS_FECHA_CREACION,
								CargasCondicionado.CAMPO_FECHA_CREACION,
								Order.DESC));
			}
		}

		return tableFacade;
	}

	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * 
	 * @param tableFacade
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {

		// Configura el TableFacade con las columnas que se quieren mostrar
		tableFacade.setColumnProperties(CargasCondicionado.CAMPO_ID,
				CargasCondicionado.CAMPO_FECHA_CREACION,
				CargasCondicionado.CAMPO_ESTADO,
				CargasCondicionado.CAMPO_FECHA_CARGA);

	}

	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el
	 * formulario
	 * 
	 * @param CargasCondicionado
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(CargasCondicionado cargasCondicionado,
			TableFacade tableFacade) {
		// FechaCreacion
		if (FiltroUtils.noEstaVacio(cargasCondicionado.getFechaCreacion()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasCondicionado.CAMPO_FECHA_CREACION,
					new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
							.format(cargasCondicionado.getFechaCreacion()
									.toString()));
		// estado
		if (FiltroUtils.noEstaVacio(cargasCondicionado.getEstado()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasCondicionado.CAMPO_ESTADO,
					cargasCondicionado.getEstado().toString());
		// fechaCarga
		if (FiltroUtils.noEstaVacio(cargasCondicionado.getFechaCarga()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasCondicionado.CAMPO_FECHA_CARGA,
					new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
							.format(cargasCondicionado.getFechaCarga()));

	}

	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los
	 * datos de las cargas de condicionado y carga el TableFacade con ellas
	 * 
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade) {

		// Obtiene el Filter para la búsqueda de condicionados
		Limit limit = tableFacade.getLimit();
		CondicionadoFilter condicionadoFilter = getCargasCondicionadoFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getCondicionadosCountWithFilter(condicionadoFilter);
		logger
				.debug("********** Número de filas para la búsqueda de condicionados  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		// Crea el Sort para la búsqueda de cargas de condicionado
		CondicionadoSort condicionadoSort = getCondicionadoSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<CargasCondicionado> items = new ArrayList<CargasCondicionado>();

		try {
			items = getCondicionadosWithFilterAndSort(condicionadoFilter,
					condicionadoSort, rowStart, rowEnd);
			logger.debug("********** Items de la lista de condicionados  = "
					+ items.size() + " **********");

		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	/**
	 * Crea y configura el Filter para la consulta de cargas de condicionado
	 * 
	 * @param limit
	 * @return
	 */
	private CondicionadoFilter getCargasCondicionadoFilter(Limit limit) {
		CondicionadoFilter condicionadoFilter = new CondicionadoFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			condicionadoFilter.addFilter(property, value);
		}
		return condicionadoFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de Cargas de Condicionado
	 * 
	 * @param limit
	 * @return
	 */
	private CondicionadoSort getCondicionadoSort(Limit limit) {
		CondicionadoSort condicionadoSort = new CondicionadoSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			condicionadoSort.addSort(property, order);
		}

		return condicionadoSort;
	}

	/**
	 * Método para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");

		// Configuración de las columnas de la tabla
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
			// Acciones
			table.getRow().getColumn(CargasCondicionado.CAMPO_ID)
					.getCellRenderer().setCellEditor(getCellEditorAcciones());
			// Campo estado
			table.getRow().getColumn(CargasCondicionado.CAMPO_ESTADO)
					.getCellRenderer().setCellEditor(getCellEditorEstado());
		}

		// Devuelve el html de la tabla
		return tableFacade.render();
	}

	/**
	 * Configuración de las columnas de la tabla
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
		configColumna(table, CargasCondicionado.CAMPO_ID,
				"&nbsp;&nbsp;Acciones", false, false, "8%");
		// 1 - Fecha de Creacion
		configColumnaFecha(table, CargasCondicionado.CAMPO_FECHA_CREACION,
				"Fecha de creaci&oacuten", true, true, "20%", "dd/MM/yyyy HH:mm:ss");
		// 2 - Estado
		configColumna(table, CargasCondicionado.CAMPO_ESTADO, "Estado", true,
				true, "20%");
		// 3 - Fecha de Carga
		configColumnaFecha(table, CargasCondicionado.CAMPO_FECHA_CARGA,
				"Fecha de carga", true, true, "20%", "dd/MM/yyyy HH:mm:ss");

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
	 * @param fFecha
	 *            Formato de fecha con la que se mostrarán los datos de esta
	 *            columna
	 */
	private void configColumnaFecha(HtmlTable table, String idCol,
			String title, boolean filterable, boolean sortable, String width,
			String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(
					new DateCellEditor("dd/MM/yyyy HH:mm:ss"));
		} catch (Exception e) {
			logger.error(
					"Ocurrió un error al configurar el formato de fecha de la columna "
							+ idCol, e);
		}
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
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				// Id de la carga
				String idCarga = new BasicCellEditor().getValue(item,
						CargasCondicionado.CAMPO_ID, rowcount).toString();
				BigDecimal estado = (BigDecimal) new BasicCellEditor()
						.getValue(item, CargasCondicionado.CAMPO_ESTADO,
								rowcount);
				HtmlBuilder html = new HtmlBuilder();
				
				//Si el estado es cargada, es que el batch ha ido ok y solo se puede consultar
				if (estado.compareTo(Constants.ESTADO_CARGA_CARGADA) == 0){
					// botón consulta
					html.a().href().quote().append(
							"javascript:consultar('" + idCarga + "');").quote()
							.close();
					html
							.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/>");
					html.aEnd();
					html.append("&nbsp;");
				//Si el estado es abierta,cerrada o error mostramos editar y borrar
				}else{
					// botón editar
					html.a().href().quote().append(
							"javascript:editarCarga('" + idCarga + "');").quote()
							.close();
					html
							.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Carga\" title=\"Editar Carga\"/>");
					html.aEnd();
					html.append("&nbsp;");
					// botón borrar
					html.a().href().quote().append(
							"javascript:borrarCarga('" + idCarga + "');").quote()
							.close();
					html
							.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Carga\" title=\"Borrar Carga\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}
				//Si el estado es abierta mostramos boton cerrar carga
				if (estado.compareTo(Constants.ESTADO_CARGA_ABIERTA) == 0){
					// botón Cerrar Carga
					html.a().href().quote().append(
							"javascript:cerrarCarga('" + idCarga + "');")
							.quote().close();
					html
							.append("<img src=\"jsp/img/displaytag/accept.png\" alt=\"Cerrar Carga\" title=\"Cerrar Carga\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}

				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Estado'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorEstado() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Obtiene el código de estado de la póliza actual
				int estado = 0;
				try {
					estado = ((BigDecimal) new BasicCellEditor().getValue(item,
							CargasCondicionado.CAMPO_ESTADO, rowcount))
							.intValue();
				} catch (Exception e) {
					logger
							.error(
									"ConsultaPolizaSbpService - Ocurrió un error al obtener el estado de la carga del condicionado",
									e);
				}
				// Muestra el mensaje correspondiente al estado
				String value = "";
				switch (estado) {
				case 1:
					value = "Cargado";
					break;
				case 2:
					value = "Abierto";
					break;
				case 3:
					value = "Cerrado";
					break;
				case 4:
					value = "Erroneo";
					break;
				default:
					break;
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}

	/**
	 * Guarda un condicionado en bbdd.
	 * 
	 * @return Objeto CargasCondicionado
	 */
	public CargasCondicionado saveCondicionado() throws BusinessException {
		CargasCondicionado cc = new CargasCondicionado();
		cc.setEstado(new BigDecimal(2));
		cc.setFechaCreacion(new Date());
		try {
			cargasCondicionadoDao.saveOrUpdate(cc);
		} catch (DAOException e) {
			logger.error("Error al acceder a bbdd", e);
			throw new BusinessException();

		}
		return cc;
	}

	/**
	 * Borra un condicionado a partir de su id
	 * 
	 * @param Long
	 *            idcondicionado
	 */
	public void borraCondicionado(Long idCondicionado)
			throws BusinessException, Exception {

		logger.info("CargasCondicionadoService - borraCondicionado");
		try {
			// recuperamos los ficheros asociados a ese condicionado
			List<CargasFicheros> ficheros = cargasCondicionadoDao
					.getFicherosCondicionado(idCondicionado);

			logger
					.info("borramos todos los ficheros del servidor por ftp del condidionado:"
							+ idCondicionado);

			for (int i = 0; i < ficheros.size(); i++) {
				CargasFicheros cf = new CargasFicheros();
				cf = ficheros.get(i);
				cargasFicherosService
						.borrarFichero(cf.getId(), cf.getFichero());
			}
			// Una vez borrado los ficheros de ftp y de bbdd y sus tablas,
			// borramos el condicionado
			cargasCondicionadoDao.delete(CargasCondicionado.class,
					idCondicionado);

		} catch (BusinessException e) {
			logger.error("Error al acceder a bbdd", e);
			throw new BusinessException();
		} catch (Exception e) {
			logger.error("Error", e);
			throw new Exception();
		}
	}

	/**
	 * Metodo que actualiza el estado de la carga del condicionado
	 * 
	 * @param Long
	 *            id (id del condicionado a actualizar) BigDecimal estado
	 *            (1=Cargado,2=Abierto,3= Cerrado)
	 */
	public void cambiaEstadoCarga(Long id, BigDecimal idEstado)
			throws BusinessException {

		try {
			CargasCondicionado cc = (CargasCondicionado) cargasCondicionadoDao
					.get(CargasCondicionado.class, id);
			cc.setEstado(idEstado);
			cc.setFechaCarga(null);
			cargasCondicionadoDao.saveOrUpdate(cc);
		} catch (DAOException e) {
			logger.error("error al cerrar la carga del idcondicionado: " + id,
					e);
			throw new BusinessException();
		}

	}

	/* Inyeccion de Spring */
	public void setCargasCondicionadoDao(
			ICargasCondicionadoDao cargasCondicionadoDao) {
		this.cargasCondicionadoDao = cargasCondicionadoDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCargasFicherosService(
			ICargasFicherosService cargasFicherosService) {
		this.cargasFicherosService = cargasFicherosService;
	}

}
