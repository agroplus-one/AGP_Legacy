package com.rsi.agp.core.jmesa.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CargasFicherosFilter;
import com.rsi.agp.core.jmesa.service.ICargasFicherosService;
import com.rsi.agp.core.jmesa.sort.CargasFicherosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.imp.ICargasFicherosDao;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;

public class CargasFicherosService implements ICargasFicherosService {

	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private ICargasFicherosDao cargasFicherosDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_importacion");

	public int getFicherosCountWithFilter(CargasFicherosFilter filter,
			CargasFicheros cargasFicheros) {
		return cargasFicherosDao.getFicherosCountWithFilter(filter,
				cargasFicheros);
	}

	public Collection<CargasFicheros> getFicherosWithFilterAndSort(
			CargasFicherosFilter filter, CargasFicherosSort sort, int rowStart,
			int rowEnd, CargasFicheros cargasFicheros) throws BusinessException {

		return cargasFicherosDao.getFicherosWithFilterAndSort(filter, sort,
				rowStart, rowEnd, cargasFicheros);

	}

	
	public String getTablaCargasFicheros(HttpServletRequest request,
			HttpServletResponse response, CargasFicheros cargasFicheros,
			String origenLlamada) {

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				cargasFicheros, origenLlamada);

		// Configura el filtro y la ordenación, busca las cargas de ficheros y
		// las carga en el TableFacade
		setDataAndLimitVariables(tableFacade, cargasFicheros);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de cargas
	 * de Ficheros
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, CargasFicheros cargasFicheros,
			String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		// Carga las columnas a mostrar en el listado en el TableFacade

		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class),
				new DateFromFilterMatcher("d/M/yyyy"));
		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoCargasFicheros_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoCargasFicheros_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(cargasFicheros, tableFacade);
				
				// -- ORDENACIÓN POR DEFECTO --> ORDER ASC -> Fecha
				tableFacade.getLimit().getSortSet().addSort(
						new Sort(cargasFicheros.POS_FECHA,
								cargasFicheros.CAMPO_FECHA,
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
		tableFacade.setColumnProperties(CargasFicheros.CAMPO_ID,
				CargasFicheros.CAMPO_FICHERO, CargasFicheros.CAMPO_TIPO,
				CargasFicheros.CAMPO_PLAN, CargasFicheros.CAMPO_LINEA, CargasFicheros.CAMPO_FECHA);

	}

	/**
	 * Carga en el TableFacade los filtros de búsqueda introducidos en el
	 * formulario
	 * 
	 * @param CargasFicheros
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(CargasFicheros cargasFicheros,
			TableFacade tableFacade) {
		// fichero
		if (FiltroUtils.noEstaVacio(cargasFicheros.getFichero()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasFicheros.CAMPO_FICHERO, cargasFicheros.getFichero());
		// tipo
		if (FiltroUtils.noEstaVacio(cargasFicheros.getTipo()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasFicheros.CAMPO_TIPO,
					cargasFicheros.getTipo().toString());
		// plan
		if (FiltroUtils.noEstaVacio(cargasFicheros.getPlan()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasFicheros.CAMPO_PLAN,
					cargasFicheros.getPlan().toString());
		// linea
		if (FiltroUtils.noEstaVacio(cargasFicheros.getLinea()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasFicheros.CAMPO_LINEA,
					cargasFicheros.getLinea().toString());
		// fecha
		if (FiltroUtils.noEstaVacio(cargasFicheros.getFecha()))
			tableFacade.getLimit().getFilterSet().addFilter(
					CargasFicheros.CAMPO_FECHA,
					cargasFicheros.getLinea().toString());

	}

	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los
	 * datos de las cargas de Ficheros y carga el TableFacade con ellas
	 * 
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,
			CargasFicheros cargasFicheros) {

		// Obtiene el Filter para la búsqueda de ficheros
		Limit limit = tableFacade.getLimit();
		CargasFicherosFilter cargasFicherosFilter = getCargasFicherosFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicherosCountWithFilter(cargasFicherosFilter,
				cargasFicheros);
		logger
				.debug("********** Número de filas para la búsqueda de ficheros  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		// Crea el Sort para la búsqueda de cargas de ficheros
		CargasFicherosSort cargasFicherosSort = getCargasFicherosSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<CargasFicheros> items = new ArrayList<CargasFicheros>();

		try {
			items = getFicherosWithFilterAndSort(cargasFicherosFilter,
					cargasFicherosSort, rowStart, rowEnd, cargasFicheros);
			logger.debug("********** Items de la lista de ficheros  = "
					+ items.size() + " **********");

		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	/**
	 * Crea y configura el Filter para la consulta de cargas de ficheros
	 * 
	 * @param limit
	 * @return
	 */
	private CargasFicherosFilter getCargasFicherosFilter(Limit limit) {
		CargasFicherosFilter cargasFicherosFilter = new CargasFicherosFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			cargasFicherosFilter.addFilter(property, value);
		}
		return cargasFicherosFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de Cargas de ficheros
	 * 
	 * @param limit
	 * @return
	 */
	private CargasFicherosSort getCargasFicherosSort(Limit limit) {
		CargasFicherosSort cargasFicherosSort = new CargasFicherosSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			cargasFicherosSort.addSort(property, order);
		}

		return cargasFicherosSort;
	}

	/**
	 * Método para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade,String origenLlamada) {

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
			table.getRow().getColumn(CargasFicheros.CAMPO_ID).getCellRenderer()
					.setCellEditor(getCellEditorAcciones(origenLlamada));
			// Campo estado
			table.getRow().getColumn(CargasFicheros.CAMPO_TIPO)
					.getCellRenderer().setCellEditor(getCellEditorTipo());
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
		configColumna(table, CargasFicheros.CAMPO_ID, "&nbsp;&nbsp;Acciones",
				false, false, "4%");
		// 1 - fichero
		configColumna(table, CargasFicheros.CAMPO_FICHERO, "Nombre Fichero",
				true, true, "20%");
		// 2 - tipo
		configColumna(table, CargasFicheros.CAMPO_TIPO, "Tipo", true, true,
				"20%");
		// 3 - Plan
		configColumna(table, CargasFicheros.CAMPO_PLAN, "Plan", true, true,
				"20%");
		// 4 - Linea
		configColumna(table, CargasFicheros.CAMPO_LINEA, "L&iacutenea", true,
				true, "20%");
		// 5 - Fecha
		configColumnaFecha(table, CargasFicheros.CAMPO_FECHA,
				"Fecha", true, true, "20%", "dd/MM/yyyy HH:mm:ss");
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
	private CellEditor getCellEditorAcciones(final String origenLlamada) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				// Id
				String id = new BasicCellEditor().getValue(item,
						CargasFicheros.CAMPO_ID, rowcount).toString();
				// Name
				String name = new BasicCellEditor().getValue(item,
						CargasFicheros.CAMPO_FICHERO, rowcount).toString();

				HtmlBuilder html = new HtmlBuilder();
				if (origenLlamada.equals("consulta")){
					// botón consulta
					html.a().href().quote().append(
							"javascript:consultarFichero('" + id + "');").quote()
							.close();
					html
					.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}else{
					// botón editar
					html.a().href().quote().append(
							"javascript:editarFichero('" + id + "');").quote()
							.close();
					html
							.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar tablas\" title=\"Editar tablas\"/>");
					html.aEnd();
					html.append("&nbsp;");
					// botón borrar
					html.a().href().quote().append(
							"javascript:borrarFichero('" + id + "','" + name
									+ "');").quote().close();
					html
							.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Fichero\" title=\"Borrar Fichero\"/>");
					html.aEnd();
					html.append("&nbsp;");
				}

				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Tipo'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorTipo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Obtiene el código de estado de la póliza actual
				int estado = 0;
				try {
					estado = ((BigDecimal) new BasicCellEditor().getValue(item,
							CargasFicheros.CAMPO_TIPO, rowcount)).intValue();
				} catch (Exception e) {
					logger
							.error(
									"ConsultaPolizaSbpService - Ocurrió un error al obtener el tipo del fichero",
									e);
				}
				// Muestra el mensaje correspondiente al estado
				String value = "";
				value = bundle.getString("tipo."+estado+".value");

				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}
	/**
	 * Sube los ficheros por Sftp
	 * @param MultipartFile file
	 * @param HttpServletRequest request
	 */
	@Override
	public void subeFicherosFTP(MultipartFile file, HttpServletRequest request)
			throws BusinessException, Exception {
		try {
			file.transferTo(new File(bundle.getString(Constants.COND_DIR) + file.getOriginalFilename().toUpperCase()));
		} catch (Exception e) {
			logger.error("Error al subir el fichero al servidor", e);
			throw new Exception(e);
		} finally {
			// Cerramos el canal y session
		}
	}
	
	
	/**
	 * Guarda un fichero en bbdd
	 * @param CargasFicheros cargasFicherosBean
	 * @param Long idCondicionado
	 * @return Long
	 */
	@Override
	public Long saveFichero(CargasFicheros cargasFicherosBean,
			Long idCondicionado) throws BusinessException {

		try {
			CargasCondicionado cargasCondicionado = new CargasCondicionado();
			cargasCondicionado.setId(idCondicionado);
			cargasFicherosBean.setCargasCondicionado(cargasCondicionado);
			cargasFicherosBean.setFichero(cargasFicherosBean.getFile()
					.getOriginalFilename().substring(
							0,
							(cargasFicherosBean.getFile().getOriginalFilename()
									.length() - 4)));
			cargasFicherosBean.setFecha(new Date());

			cargasFicherosDao.saveOrUpdate(cargasFicherosBean);
			return cargasFicherosBean.getId();

		} catch (DAOException e) {
			logger.error("Error al guardar cargasFicherosBean en bbdd", e);
			throw new BusinessException();

		}

	}
	/**
	 * Valida los campos plan,linea y tipo para que coincidan con los del fichero
	 * @param CargasFicheros cargasFicherosBean
	 * @return List<String> errores
	 */
	@Override
	public List<String> validaCampos(CargasFicheros cargasFicherosBean) {

		InputStream input;
		List<String> errores = new ArrayList<String>();
		try {
			// Si el tipo es 2=general no validamos plan/linea
			if (cargasFicherosBean.getTipo().compareTo(new BigDecimal(2)) != 0) {
				input = cargasFicherosBean.getFile().getInputStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(input, writer);
				String text = writer.toString();
				// plan xxxx / linea xxx
				String plan = "plan " + cargasFicherosBean.getPlan();
				String plan2 = "plan " + NumberUtils.formatear(cargasFicherosBean.getPlan(), 0);
				logger.debug("Planes posibles para el fichero: " + plan + ", " + plan2);
				String linea = "linea " + cargasFicherosBean.getLinea();
				String linea2 = "linea " + cargasFicherosBean.getLinea();
				logger.debug("Lineas posibles para el fichero: " + linea + ", " + linea2);
				if ((!text.contains((CharSequence) plan)) && (!text.contains((CharSequence) plan2))) {
					errores.add((String) bundle.getObject(Constants.ALERTA_PLAN_KO));
				}
				if ((!text.contains((CharSequence) linea)) && (!text.contains((CharSequence) linea2))) {
					errores.add((String) bundle.getObject(Constants.ALERTA_LINEA_KO));
				}
			}
			// del fichero xml cogemos los numeros de las tablas
			List numsTablas = getNumsTablas(cargasFicherosBean.getFile());
			// recuperamos el tipo de las tablas del fichero
			String tipo = cargasFicherosDao.getTipoFichero(cargasFicherosBean,
					numsTablas);
			BigDecimal tipoB = converTipo(tipo);
			if (!tipoB.equals(cargasFicherosBean.getTipo())) {
				errores.add((String) bundle.getObject(Constants.ALERTA_TIPO));
			}

		} catch (IOException e) {
			logger.error("Error verificando la linea y el plan", e);
		}
		return errores;
	}
	/**
	 * Convierte el tipo en BigDecimal para su posterior comparacion
	 * @param tipo
	 * @return BigDecimal
	 */
	private BigDecimal converTipo(String tipo) {
		if (tipo.equals("ORG")) {
			return new BigDecimal(1);
		} else if (tipo.equals("GEN")) {
			return new BigDecimal(2);
		} else if (tipo.equals("CPL")) {
			return new BigDecimal(3);
		}
		return new BigDecimal(0);
	}
	/**
	 * Obtiene los numeros de las tablas del fichero que vienen en el TXT
	 * @param file
	 * @return List
	 * @throws IOException
	 */
	private List getNumsTablas(MultipartFile file) throws IOException {

		List<BigDecimal> numsTablas = new ArrayList<BigDecimal>();
		// De la cadena obtenida del txt nos quedamos con la parte entre
		// parentesis,
		// que corresponde a los numeros de las tablas
		InputStream input = file.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(input, writer);

		String text = writer.toString();
		String arrayString[] = text.split("\\(");
		String codsTablas[] = arrayString[1].split("\\)");
		String cods[] = codsTablas[0].split(",");
		for (int i = 0; i < cods.length; i++) {
			numsTablas.add(new BigDecimal(cods[i].trim()));
		}

		return numsTablas;
	}
	/**
	 * Borra un fichero de bbdd y del servidor
	 * @param
	 * @param
	 */
	@Override
	public void borrarFichero(Long idFichero, String nombreFichero)
			throws BusinessException, Exception {
		
		try {
			logger.info("CargasFicherosService - borrarFichero - init");
			logger
					.info("Borramos el fichero (txt,zip)del servidor por ftp. Nombre del Fichero: "
							+ nombreFichero);
			
			
			File ftxt = new File(bundle.getString(Constants.COND_DIR) + nombreFichero.toUpperCase()+".TXT");
			File fzip = new File(bundle.getString(Constants.COND_DIR) + nombreFichero.toUpperCase()+".ZIP");
			if (ftxt.delete() && fzip.delete()){
				logger.info("Borramos el fichero y sus tablas de bbdd. idFichero: "
					+ idFichero);
				cargasFicherosDao.delete(CargasFicheros.class, idFichero);

				logger.info("Ficheros borrados correctamente");
			}else{
				logger.error("Error al borrar el fichero");
				throw new Exception();
			}

			logger.info("CargasFicherosService - borrarFichero - end");
		} catch (DAOException e) {
			logger.error("Error al borrar el fichero", e);
			throw new BusinessException();
		} catch (Exception e) {
			logger.error("Error al borrar el fichero", e);
			throw new Exception();
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCargasFicherosDao(ICargasFicherosDao cargasFicherosDao) {
		this.cargasFicherosDao = cargasFicherosDao;
	}

}
