package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CondicionCamposFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.Estados;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCondicionCamposGenericosService;
import com.rsi.agp.core.jmesa.sort.CondicionCamposSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.InformeUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.poliza.EstadoPolizaFilter;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposCalculadosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoCamposPermitidosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoCondicionesCamposGenericosDao;
import com.rsi.agp.dao.models.mtoinf.IMtoDatosInformeDao;
import com.rsi.agp.dao.models.mtoinf.IMtoOperadorCamposPermitidosDao;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.Operador;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfCondiciones;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;

@SuppressWarnings("deprecation")
public class MtoCondicionCamposGenericosService implements IMtoCondicionCamposGenericosService {

	private Log logger = LogFactory.getLog(getClass());

	private IMtoCondicionesCamposGenericosDao mtoCondicionesCamposGenericosDao;
	private IMtoOperadorCamposPermitidosDao mtoOperadorCamposPermitidosDao;
	private IMtoCamposCalculadosDao mtoCamposCalculadosDao;
	private IMtoCamposPermitidosDao mtoCamposPermitidosDao;
	private IMtoDatosInformeDao mtoDatosInformeDao;

	private String id = "mtoCondicionesCampos";

	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");

	// Constantes para los nombres de las columnas del listado
	private final String ID_STR = "ID";
	private final String ABREVIADO = "ABREVIADO";
	private final String OPERADOR = "OPERADOR";
	private final String VALOR = "VALOR";

	// Mapa con las columnas del listado y los campos del filtro de búsqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();

	@Override
	public Collection<VistaMtoinfCondiciones> getCondicionInformeWithFilterAndSort(CondicionCamposFilter filter,
			CondicionCamposSort sort, BigDecimal informeId, int rowStart, int rowEnd) throws BusinessException {

		Collection<VistaMtoinfCondiciones> colectDatoInformes = null;
		try {

			colectDatoInformes = mtoCondicionesCamposGenericosDao.getCamposGenericosWithFilterSort(filter, sort,
					informeId, rowStart, rowEnd);

		} catch (Exception e) {
			logger.error("Error en getCondicionInformeWithFilterAndSort", e);
			throw new BusinessException("Error en getCondicionInformeWithFilterAndSort", e);

		}

		return colectDatoInformes;
	}

	/***************************************************************************
	 * Realiza el alta del dato del informe pasado como parámetro
	 * 
	 * @param informeId
	 * @param datosInformeBean
	 **************************************************************************/

	public Map<String, Object> altaCondicionInforme(VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			if (mtoCondicionesCamposGenericosDao.existeCondicion(vistaMtoinfCondiciones)) {
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_EXISTE_KO));
			} else {
				if (vistaMtoinfCondiciones.getId().getPermitidocalculado().intValue() == 1) {

					OperadorCamposCalculados operadorCamposCalculados = (OperadorCamposCalculados) mtoCondicionesCamposGenericosDao
							.get(OperadorCamposCalculados.class,
									vistaMtoinfCondiciones.getIdtablaoperadores().longValue());
					CondicionCamposCalculados condicionCamposCalculados = new CondicionCamposCalculados();

					DatoInformes datoInforme = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class,
							vistaMtoinfCondiciones.getDatoinformeid().longValue());

					condicionCamposCalculados.setDatoInformes(datoInforme);
					condicionCamposCalculados.setOperadorCamposCalculados(operadorCamposCalculados);
					condicionCamposCalculados.setCondicion(vistaMtoinfCondiciones.getCondicion());
					mtoCondicionesCamposGenericosDao.saveOrUpdate(condicionCamposCalculados);
				} else if (vistaMtoinfCondiciones.getId().getPermitidocalculado().intValue() == 2) {

					CondicionCamposPermitidos condicionCamposPermitidos = new CondicionCamposPermitidos();
					DatoInformes datoInforme = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class,
							vistaMtoinfCondiciones.getDatoinformeid().longValue());
					condicionCamposPermitidos.setDatoInformes(datoInforme);
					OperadorCamposPermitido operadorCamposPermitidos = (OperadorCamposPermitido) mtoOperadorCamposPermitidosDao
							.get(OperadorCamposPermitido.class,
									vistaMtoinfCondiciones.getIdtablaoperadores().longValue());
					condicionCamposPermitidos.setOperadorCamposPermitido(operadorCamposPermitidos);
					condicionCamposPermitidos.setCondicion(vistaMtoinfCondiciones.getCondicion());
					condicionCamposPermitidos = (CondicionCamposPermitidos) mtoCondicionesCamposGenericosDao
							.saveOrUpdate(condicionCamposPermitidos);

				}
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CONDICIONINFORME_ALTA_OK));
			}
		} catch (Exception ex) {
			logger.error("Error a dar de alta una condicion de dato de los informes", ex);
			throw new BusinessException("Error a dar de alta  un dato de los informes", ex);
		}
		return parameters;
	}

	/**
	 * Setter de propiedad
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * devuelve la tabla html del listado de jmesa
	 * 
	 * @param request
	 * @param response
	 * @param origenLlamada
	 * @return String
	 */
	public String getTablaCondicionInforme(HttpServletRequest request, HttpServletResponse response,
			VistaMtoinfCondiciones vistaMtoinfCondiciones, String origenLlamada) {

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, vistaMtoinfCondiciones, origenLlamada);

		// Configura el filtro y la ordenación, busca las condiciones y las carga en
		// el TableFacade
		setDataAndLimitVariables(tableFacade, request, vistaMtoinfCondiciones, origenLlamada);
		if (request.getSession().getAttribute("pageSession") != null && origenLlamada != null
				&& origenLlamada.equals("borrar")) {

			int pageSession = (Integer) (request.getSession().getAttribute("pageSession"));
			tableFacade.getLimit().getRowSelect().setPage(pageSession);

		}
		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		request.getSession().setAttribute("pageSession", tableFacade.getLimit().getRowSelect().getPage());
		request.getSession().setAttribute("rowStart", tableFacade.getLimit().getRowSelect().getRowStart());
		request.getSession().setAttribute("rowEnd", tableFacade.getLimit().getRowSelect().getRowEnd());

		// Genera el html de la tabla y lo devuelve
		// return html (tableFacade, lineas);
		return html(tableFacade);
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulará la tabla de
	 * condiciones
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public TableFacade crearTableFacade(HttpServletRequest request, HttpServletResponse response,
			VistaMtoinfCondiciones vistaMtoinfCondiciones, String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);
		tableFacade.setStateAttr("restore");

		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null || origenLlamada.equals("")) {
				if (request.getSession().getAttribute("mtoCondicionesCampos_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("mtoCondicionesCampos_LIMIT"));
				}

				if (vistaMtoinfCondiciones.getCondicion() != null
						&& !vistaMtoinfCondiciones.getCondicion().equals("")) {
					Filter filterCondicion = new Filter("condicion", vistaMtoinfCondiciones.getCondicion().toString());
					tableFacade.getLimit().getFilterSet().addFilter(filterCondicion);
				}

				if (vistaMtoinfCondiciones.getDatoinformeid() != null) {
					Filter filterDatoInformeId = new Filter("datoinformeid",
							vistaMtoinfCondiciones.getDatoinformeid().toString());
					tableFacade.getLimit().getFilterSet().addFilter(filterDatoInformeId);
				}

				if (vistaMtoinfCondiciones.getIdoperador() != null) {
					Filter filterIdOperador = new Filter("idoperador",
							vistaMtoinfCondiciones.getIdoperador().toString());
					tableFacade.getLimit().getFilterSet().addFilter(filterIdOperador);
				}
				if (vistaMtoinfCondiciones.getIdtablaoperadores() != null) {
					Filter filterIdOperador = new Filter("idtablaoperadores",
							vistaMtoinfCondiciones.getIdtablaoperadores().toString());
					tableFacade.getLimit().getFilterSet().addFilter(filterIdOperador);
				}
			}
		}
		return tableFacade;

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
				if (item != null) {
					BigDecimal id = (BigDecimal) new BasicCellEditor().getValue(item, "id.condid", rowcount);
					BigDecimal permitidOCalculado = (BigDecimal) new BasicCellEditor().getValue(item,
							"id.permitidocalculado", rowcount);
					BigDecimal datoInformesId = (BigDecimal) new BasicCellEditor().getValue(item, "datoinformeid",
							rowcount);
					BigDecimal idCampo = (BigDecimal) new BasicCellEditor().getValue(item, "idcampo", rowcount);
					BigDecimal idOperadorCondicion = (BigDecimal) new BasicCellEditor().getValue(item,
							"idtablaoperadores", rowcount);
					BigDecimal idOperador = (BigDecimal) new BasicCellEditor().getValue(item, "idoperador", rowcount);
					String condicion = (String) new BasicCellEditor().getValue(item, "condicion", rowcount);
					BigDecimal tipo = (BigDecimal) new BasicCellEditor().getValue(item, "tipo", rowcount);
					BigDecimal origendato = (BigDecimal) new BasicCellEditor().getValue(item, "origendato", rowcount);

					// botón editar
					html.a().href().quote()
							.append("javascript:editar('" + id + "','" + permitidOCalculado + "','" + idCampo + "','"
									+ datoInformesId + "','" + idOperador + "','" + idOperadorCondicion + "','"
									+ condicion + "','" + tipo + "','" + origendato + "');")
							.quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar una condici&oacute;n de informe\" title=\"Editar una condici&oacute;n de informe\"/>");

					// botón borrar
					html.a().href().quote().append("javascript:borrar('" + id + "','" + permitidOCalculado + "');")
							.quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar una condici&oacute;n de informe\" title=\"Borrar una condici&oacute;n de informe\"/>");
					html.aEnd();
					html.append("&nbsp;");
					// botón visualizar
					html.a().href().quote()
							.append("javascript:visualizar('" + id + "','" + permitidOCalculado + "','" + idCampo
									+ "','" + datoInformesId + "','" + idOperador + "','" + idOperadorCondicion + "','"
									+ condicion + "','" + tipo + "','" + origendato + "');")
							.quote().close();
					html.append(
							"<img src=\"jsp/img/magnifier.png\" alt=\"Visualizar una condici&oacute;n de informe\" title=\"Visualizar una condici&oacute;n de informe\"/>");
					html.aEnd();
					html.append("&nbsp;");
					html.aEnd();
					html.append("&nbsp;");
				}

				return html.toString();
			}
		};
	}

	private String html(TableFacade tableFacade) {
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");

		table.getRow().getColumn(columnas.get(ABREVIADO)).getCellRenderer().setCellEditor(getCellEditorAbreviado());
		table.getRow().getColumn(columnas.get(OPERADOR)).getCellRenderer().setCellEditor(getCellEditorOperador());
		table.getRow().getColumn(columnas.get(VALOR)).getCellRenderer().setCellEditor(getCellEditorValor());

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
			// campo acciones
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
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

		// 1 - Id
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "1%");
		// 2 - Orden
		configColumna(table, columnas.get(ABREVIADO), "Abreviado", true, true, "1%");
		// 3 - Abreviado
		configColumna(table, columnas.get(OPERADOR), "Operador", true, true, "1%");
		// 3 - Formato
		configColumna(table, columnas.get(VALOR), "Valor", true, true, "1%");

	}

	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un
	 * Map con ellas
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {

		// Crea el Map con las columnas del listado y los campos del filtro
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(ABREVIADO, "nombre");
			columnas.put(OPERADOR, "idoperador");
			columnas.put(VALOR, "condicion");
		}

		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(ABREVIADO), columnas.get(OPERADOR),
				columnas.get(VALOR));

		// Devuelve el mapa
		return columnas;
	}

	public int getCondicionesCountWithFilter(final BigDecimal informeId) {
		int count = 0;
		CondicionCamposFilter consultaFilter = new CondicionCamposFilter();
		try {

			count = mtoCondicionesCamposGenericosDao.getCamposGenericosCountWithFilter(consultaFilter, informeId);
		} catch (DAOException e) {
			logger.debug("getCondicionesCountWithFilter error. " + e);
		}
		return count;

	}

	/**
	 * Crea los objetos de filtro y ordenación, llama al dao para obtener los datos
	 * de las condiciones y carga el TableFacade con ellas
	 * 
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, HttpServletRequest request,
			VistaMtoinfCondiciones vistaMtoinfCondiciones, String origenLlamada) {

		// Obtiene el Filter para la búsqueda de condiciones
		Limit limit = tableFacade.getLimit();
		CondicionCamposFilter consultaFilter = getConsultaDatosInformeFilter(limit);

		// Obtiene el número de filas que cumplen el filtro

		// logger.debug("Encontradas " + totalRows + " condiciones para dato de
		// informes.");
		// y lo establecemos al tableFacade antes de obtener la fila de inicio y
		// la de fin

		Collection<VistaMtoinfCondiciones> items = new ArrayList<VistaMtoinfCondiciones>();

		try {

			int totalRows = mtoCondicionesCamposGenericosDao.getCamposGenericosCountWithFilter(consultaFilter,
					vistaMtoinfCondiciones.getIdinforme());
			logger.debug("********** count filas para Dato de informe = " + totalRows + " **********");
			// logger.debug("Encontradas " + totalRows + " condiciones para dato de
			// informes.");
			// y lo establecemos al tableFacade antes de obtener la fila de
			// inicio y la de fin
			tableFacade.setTotalRows(totalRows);
			CondicionCamposSort consultaSort = getConsultaCondicionCamposSort(limit);
			int rowStart = 0;
			int rowEnd = 0;
			if (origenLlamada != null && origenLlamada.equals("borrar")) {
				rowStart = (Integer) request.getSession().getAttribute("rowStart");
				rowEnd = (Integer) request.getSession().getAttribute("rowEnd");
			} else {
				rowStart = limit.getRowSelect().getRowStart();
				rowEnd = limit.getRowSelect().getRowEnd();
			}

			items = getCondicionInformeWithFilterAndSort(consultaFilter, consultaSort,
					vistaMtoinfCondiciones.getIdinforme(), rowStart, rowEnd);
			totalRows = items.size();
			logger.debug("********** count filas para Dato de informe = " + totalRows + " **********");
			logger.debug("********** list items  = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e);
		} catch (Exception e) {
			logger.debug("setDataAndLimitVariables error. " + e);
		}

		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);

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
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}

	/**
	 * Crea y configura el Filter para la consulta de los datos del informe
	 * 
	 * @param limit
	 * @return
	 */
	private CondicionCamposFilter getConsultaDatosInformeFilter(Limit limit) {
		CondicionCamposFilter consultaFilter = new CondicionCamposFilter();
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
	 * Crea y configura el Sort para la consulta de los datos del informe
	 * 
	 * @param limit
	 * @return
	 */
	private CondicionCamposSort getConsultaCondicionCamposSort(Limit limit) {
		CondicionCamposSort consultaSort = new CondicionCamposSort();
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
	 * Realiza la baja del dato del informe pasado como parámetro
	 * 
	 * @param datoInformes
	 */

	public Map<String, Object> bajaCondicionInforme(VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		CondicionCamposCalculados condicionCamposCalculados = null;
		CondicionCamposPermitidos condicionCamposPermitidos = null;
		try {

			if (vistaMtoinfCondiciones.getId().getPermitidocalculado().intValue() == 1) {
				condicionCamposCalculados = (CondicionCamposCalculados) mtoCondicionesCamposGenericosDao.getObject(
						CondicionCamposCalculados.class, vistaMtoinfCondiciones.getId().getCondid().longValue());
				mtoCondicionesCamposGenericosDao.delete(condicionCamposCalculados);
			} else if (vistaMtoinfCondiciones.getId().getPermitidocalculado().intValue() == 2) {
				condicionCamposPermitidos = (CondicionCamposPermitidos) mtoCondicionesCamposGenericosDao.getObject(
						CondicionCamposPermitidos.class, vistaMtoinfCondiciones.getId().getCondid().longValue());
				mtoCondicionesCamposGenericosDao.delete(condicionCamposPermitidos);

			}
			parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CONDICIONINFORME_BAJA_OK));
			logger.debug("bajaCondicionesCampos");

		} catch (Exception ex) {
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_BAJA_KO));
			logger.error("Error al eliminar la condicion del campo", ex);
			throw new BusinessException("Error al eliminar la condicion del campo", ex);
		}
		return parameters;
	}

	/**
	 * Realiza la modificacion del dato del informe pasado como parámetro
	 * 
	 * @param datoInformes
	 */
	public Map<String, Object> modificarCondicionInforme(VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			if (mtoCondicionesCamposGenericosDao.existeCondicion(vistaMtoinfCondiciones)) {
				parameters.put("alert", bundle.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_EXISTE_KO));
			} else {
				if (vistaMtoinfCondiciones.getId().getPermitidocalculado().intValue() == 1) {

					CondicionCamposCalculados condicionCamposCalculados = new CondicionCamposCalculados();
					OperadorCamposCalculados operadorCamposCalculados = (OperadorCamposCalculados) mtoCondicionesCamposGenericosDao
							.get(OperadorCamposCalculados.class,
									vistaMtoinfCondiciones.getIdtablaoperadores().longValue());
					DatoInformes datoInforme = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class,
							vistaMtoinfCondiciones.getDatoinformeid().longValue());
					condicionCamposCalculados.setId(vistaMtoinfCondiciones.getId().getCondid().longValue());
					condicionCamposCalculados.setDatoInformes(datoInforme);
					condicionCamposCalculados.setOperadorCamposCalculados(operadorCamposCalculados);
					condicionCamposCalculados.setCondicion(vistaMtoinfCondiciones.getCondicion());
					condicionCamposCalculados = (CondicionCamposCalculados) mtoCondicionesCamposGenericosDao
							.saveOrUpdate(condicionCamposCalculados);

				} else if (vistaMtoinfCondiciones.getId().getPermitidocalculado().intValue() == 2) {

					CondicionCamposPermitidos condicionCamposPermitidos = new CondicionCamposPermitidos();
					DatoInformes datoInforme = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class,
							vistaMtoinfCondiciones.getDatoinformeid().longValue());
					condicionCamposPermitidos.setDatoInformes(datoInforme);
					OperadorCamposPermitido operadorCamposPermitidos = (OperadorCamposPermitido) mtoOperadorCamposPermitidosDao
							.get(OperadorCamposPermitido.class,
									vistaMtoinfCondiciones.getIdtablaoperadores().longValue());
					condicionCamposPermitidos.setOperadorCamposPermitido(operadorCamposPermitidos);
					condicionCamposPermitidos.setCondicion(vistaMtoinfCondiciones.getCondicion());
					condicionCamposPermitidos.setId(vistaMtoinfCondiciones.getId().getCondid().longValue());
					condicionCamposPermitidos = (CondicionCamposPermitidos) mtoCondicionesCamposGenericosDao
							.saveOrUpdate(condicionCamposPermitidos);

				}

				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CONDICIONINFORME_MODIF_OK));

			}
		} catch (Exception ex) {
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_MODIF_KO));

			logger.error("Error a modificar un dato de los informes", ex);
			throw new BusinessException("Error a modificar un dato de los informes", ex);
		}
		return parameters;
	}

	/**
	 * Devuelve el objeto que muestra la columna del operador
	 * 
	 * @return
	 */
	private CellEditor getCellEditorOperador() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal operador = null;

				String strOprerator = "";
				try {
					operador = (BigDecimal) new BasicCellEditor().getValue(item, "idoperador", rowcount);

					strOprerator = InformeUtils.getValueOperador(operador.intValue());
				}

				catch (Exception e) {
					logger.debug("operador nulo");
				}

				HtmlBuilder html = new HtmlBuilder();

				html.append(StringUtils.nullToString(strOprerator) + "&nbsp;");

				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la columna 'Abreviado'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAbreviado() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				String nombre = null;
				try {

					nombre = (String) new BasicCellEditor().getValue(item, "nombre", rowcount);
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
	 * Devuelve el objeto que muestra la columna 'Valor'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorValor() {
		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				String valor = null;

				try {
					valor = (String) new BasicCellEditor().getValue(item, "condicion", rowcount);
				} catch (Exception e) {
					logger.debug("valor nulo");
				}

				HtmlBuilder html = new HtmlBuilder();

				html.append(StringUtils.nullToString(valor) + "&nbsp;");

				return html.toString();
			}
		};
	}

	public IMtoCondicionesCamposGenericosDao getMtoCondicionesCamposGenericosDao() {
		return mtoCondicionesCamposGenericosDao;
	}

	public void setMtoCondicionesCamposGenericosDao(
			IMtoCondicionesCamposGenericosDao mtoCondicionesCamposGenericosDao) {
		this.mtoCondicionesCamposGenericosDao = mtoCondicionesCamposGenericosDao;
	}

	public IMtoOperadorCamposPermitidosDao getMtoOperadorCamposPermitidosDao() {
		return mtoOperadorCamposPermitidosDao;
	}

	public void setMtoOperadorCamposPermitidosDao(IMtoOperadorCamposPermitidosDao mtoOperadorCamposPermitidosDao) {
		this.mtoOperadorCamposPermitidosDao = mtoOperadorCamposPermitidosDao;
	}

	public IMtoCamposCalculadosDao getMtoCamposCalculadosDao() {
		return mtoCamposCalculadosDao;
	}

	public void setMtoCamposCalculadosDao(IMtoCamposCalculadosDao mtoCamposCalculadosDao) {
		this.mtoCamposCalculadosDao = mtoCamposCalculadosDao;
	}

	public IMtoCamposPermitidosDao getMtoCamposPermitidosDao() {
		return mtoCamposPermitidosDao;
	}

	public void setMtoCamposPermitidosDao(IMtoCamposPermitidosDao mtoCamposPermitidosDao) {
		this.mtoCamposPermitidosDao = mtoCamposPermitidosDao;
	}

	public List<Operador> getListaOperadores(Integer permitidOCalculado, Long datoInformesId) throws BusinessException {

		List<Operador> listaOperadores = null;

		try {
			if (permitidOCalculado == 1) {

				DatoInformes datoInformes = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class, datoInformesId);
				CamposCalculados camposCalculados = (CamposCalculados) mtoCamposCalculadosDao
						.getObject(CamposCalculados.class, datoInformes.getCamposCalculados().getId());

				if (camposCalculados.getOperadorCamposCalculadoses().size() > 0) {
					listaOperadores = new ArrayList<Operador>();
					for (OperadorCamposCalculados codigoOperador : camposCalculados.getOperadorCamposCalculadoses()) {
						Operador operador = new Operador(codigoOperador.getId().intValue(),
								codigoOperador.getIdoperador().intValue(),
								InformeUtils.getValueOperador(codigoOperador.getIdoperador().intValue()));

						listaOperadores.add(operador);
					}
				}
			} else if (permitidOCalculado == 2) {
				DatoInformes datoInformes = (DatoInformes) mtoDatosInformeDao.get(DatoInformes.class, datoInformesId);
				listaOperadores = mtoOperadorCamposPermitidosDao
						.getListaOperadores(datoInformes.getCamposPermitidos().getId());
			}
		} catch (Exception ex) {
			logger.error("Error a recuperar la lista de operadores", ex);
			throw new BusinessException("Error a recuperar la lista de operadores", ex);

		}
		return listaOperadores;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Estados> getListaEstados(BigDecimal od) {

		// Dependiendo del código de origen de datos recibido, se carga una clase para
		// el acceso a datos
		Class classDao = null;
		switch (od.intValue()) {
		case ConstantsInf.OD_ESTADO_POLIZA:
			classDao = EstadoPoliza.class;
			break;
		case ConstantsInf.OD_ESTADO_SINIESTRO:
			classDao = EstadoSiniestro.class;
			break;
		case ConstantsInf.OD_ESTADO_ANEXO_MOD:
			classDao = com.rsi.agp.dao.tables.anexo.Estado.class;
			break;
		case ConstantsInf.OD_ESTADO_ANEXO_RED:
			classDao = com.rsi.agp.dao.tables.reduccionCap.Estado.class;
			break;
		// Pet. 50777 (02.04.2019) //
		case ConstantsInf.OD_ESTADO_AGRO:
			classDao = com.rsi.agp.dao.tables.poliza.EstadoAgroseguro.class;
			break;
		case ConstantsInf.OD_ESTADO_AGRO_AM:
			classDao = com.rsi.agp.dao.tables.inc.EstadosInc.class;
			break;
		default:
			break;
		}

		// Crea y carga el listado que se devolverá
		List<Estados> listEstados = new ArrayList<Estados>();
		if (classDao != null) {
			if (classDao.equals(EstadoPoliza.class)) {
				EstadoPolizaFilter estadoPolizaFilter = new EstadoPolizaFilter();
				BigDecimal[] val = new BigDecimal[1];
				Arrays.fill(val, Constants.ESTADO_POLIZA_BAJA);
				estadoPolizaFilter.setEstadosPolizaExcluir(val);
				listEstados = mtoCondicionesCamposGenericosDao.getEstadosPol(estadoPolizaFilter);

			} else {
				listEstados = mtoCondicionesCamposGenericosDao.getEstados(classDao);
			}
		}

		// Ordena la lista
		Collections.sort(listEstados);

		return listEstados;
	}

	public IMtoDatosInformeDao getMtoDatosInformeDao() {
		return mtoDatosInformeDao;
	}

	public void setMtoDatosInformeDao(IMtoDatosInformeDao mtoDatosInformeDao) {
		this.mtoDatosInformeDao = mtoDatosInformeDao;
	}

	public String getId() {
		return id;
	}

}