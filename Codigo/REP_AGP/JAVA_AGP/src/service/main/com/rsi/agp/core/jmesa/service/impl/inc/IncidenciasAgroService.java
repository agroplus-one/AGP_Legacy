package com.rsi.agp.core.jmesa.service.impl.inc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.filter.IncidenciasAgroFilter;
import com.rsi.agp.core.jmesa.filter.IntArchiveTypeFilterMatcher;
import com.rsi.agp.core.jmesa.filter.LongFilterMatcher;
import com.rsi.agp.core.jmesa.service.IIncidenciasAgroService;
import com.rsi.agp.core.jmesa.sort.IncidenciasSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.models.inc.IIncidenciasAgroDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;

@SuppressWarnings("deprecation")
public class IncidenciasAgroService implements IIncidenciasAgroService {

	private Log logger = LogFactory.getLog(getClass());

	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id;
	private IIncidenciasAgroDao incidenciasAgroDao;

	// Constantes para los nombres de las columnas del listado
	private final static String _ID = "ID";
	private final static String ANHO = "ANHO";
	private final static String NUMERO = "NUMERO";
	private final static String CODASUNTO = "CODASUNTO";
	private final static String CODMOTIVO = "CODMOTIVO";
	private final static String ASUNTO = "ASUNTO";
	private final static String MOTIVO = "MOTIVO";
	private final static String CODESTADO = "CODESTADO";
	private final static String ESTADO_AGROPLUS = "ESTADODES";
	private final static String FECHA = "FECHA";
	private final static String COD_DOC_AFECTADO = "COD_DOC_AFECTADO";
	private final static String DOC_AFECTADO = "DOC_AFECTADO";
	private final static String TIPO_POLIZA = "TIPO_POLIZA";
	private final static String ID_ENVIO = "ID_ENVIO";

	private final static String ENTIDAD = "ENTIDAD";
	private final static String OFICINA = "OFICINA";
	private final static String ENT_MEDIADORA = "ENT_MEDIADORA";
	private final static String SUBENT_MEDIADORA = "SUBENT_MEDIADORA";

	private final static String DELEGACION = "DELEGACION";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String ESTADO_INC_AGRO = "CODESTADOAGRO";
	private final static String ESTADO_AGROSEGURO = "ESTADOAGRODES";
	private final static String CUPON = "CUPON";
	private final static String NIF_CIF = "NIF_CIF";
	private final static String FECHA_ENVIO_DESDE = "FECHA_ENVIO_DESDE";
	private final static String FECHA_ENVIO_HASTA = "FECHA_ENVIO_HASTA";
	private final static String REFERENCIA = "REFERENCIA";
	private final static String CODUSUARIO = "CODUSUARIO";
	private final static String FECHASEGUIMIENTO = "FECHASEGUIMIENTO";
	private final static String TIPO_INC = "TIPOINC";
	private final static String TIPO_INC_DES = "TIPOINCDES";
	private static final DateCellEditor DATE_CELL_EDITOR = new DateCellEditor("dd/MM/yyyy");
	private static final String ENTIDAD_HEADER = "Entidad";
	private static final String OFICINA_HEADER = "Oficina";
	private static final String ES_MEDIADORA_HEADER = "E-S Mediadora";
	private static final String PLAN_HEADER = "Plan";
	private static final String TIPO_REF_HEADER = "Tipo Ref.";
	private static final String NIFCIF_HEADER = "NIF/CIF";
	private static final String ANHO_HEADER = "A&ntilde;o";
	private static final String ASUNTO_HEADER = "Asunto";
	private static final String EST_AGROSEG_HEADER = "Est. Agroseguro";
	private static final String EST_AGROPLUS_HEADER = "Est. Agroplus";
	private static final String FECHA_HEADER = "Fec. Env.";
	private static final String FECHA_SEGUIMIENTO_HEADER = "Fec. Actualizacion";
	private static final String TIPO_INC_HEADER = "Tipolog&icaute;a";

	private static final String ENV_CORRECTA = "Enviada Correcta";
	private static final String INCIDENCIA = "Incidencia";
	
	IncidenciasAgroFilter incidenciasAgroFilter;
	IncidenciasSort incidenciasSort;
	
	Date fechaEnvioDesde;
	Date fechaEnvioHasta;

	/**
	 * MODIF TAM 06.09.2018 Modificaciones para solucionar el tema de las oficinas
	 * de una entidad y el filtrado de incidencias
	 **/
	/**
	 * De momento estamos haciendo pruebas e intentando igualar el funcionamiento
	 * con Sobreprecios que lo hace correctamente.
	 **/

	/**
	 * Comentamos lo que hay a dia de hoy para hacerme una funcion con el mismo
	 * nombre pero con las modificaciones que le estoy Incluyendo para solucionar la
	 * incidencia
	 */

	@Override
	public String getTablaIncidenciasAgro(final HttpServletRequest request, final HttpServletResponse response,
			final VistaIncidenciasAgro vIncidenciasAgro, final String origenLlamada) throws BusinessException {

		try {

			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

			Boolean excel = Boolean.valueOf(request.getParameter("excel"));
			TableFacade tableFacade = crearTableFacade(request, response, vIncidenciasAgro, origenLlamada, excel);

			Limit limit = tableFacade.getLimit();

			// DAA 12/08/2013
			List<BigDecimal> grupoEntidades = null;
			if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
				grupoEntidades = usuario.getListaCodEntidadesGrupo();
			}

			// MODIF TAM (13.09.2018) ** Resolucion de Incidencias //
			List<String> Oficinas = null;
			if (vIncidenciasAgro.getOficina() != null && !vIncidenciasAgro.getOficina().isEmpty()) {
				Oficinas = CriteriaUtils.getCodigosOficina(vIncidenciasAgro.getOficina().toString());
			}

			List<BigDecimal> grupoOficinas = null;
			if (Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil())) {
				grupoOficinas = usuario.getListaCodOficinasGrupo();
			}

			incidenciasAgroFilter = getConsultaIncFilter(limit, grupoEntidades, grupoOficinas, Oficinas);

			incidenciasAgroFilter.setvIncidenciasAgro(vIncidenciasAgro);

			fechaEnvioDesde = vIncidenciasAgro.getFechaEnvioDesde();
			fechaEnvioHasta = vIncidenciasAgro.getFechaEnvioHasta();

			setDataAndLimitVariables(tableFacade, incidenciasAgroFilter, limit, fechaEnvioDesde, fechaEnvioHasta);

			String listaIdsTodos = this.incidenciasAgroDao.getlistaIdsTodos(incidenciasAgroFilter, fechaEnvioDesde,
					fechaEnvioHasta);

			if (tableFacade.getLimit().hasExport() && excel) {
				this.pintarExcel(tableFacade);
				return null;
			}

			String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";

			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());

			return html(tableFacade, usuario) + script;

		} catch (Exception ex) {

			logger.error("getTablaDocumentos error. " + ex);
			throw new BusinessException("Se ha producido al obtener getTablaDocumentos:", ex);
		}
	}

	/* Pet. 50775 ** MODIF TAM (27.06.2018) ** Inicio */
	/* Exportar Excel */
	private void pintarExcel(TableFacade tableFacade) {
		this.eliminarColumnaAccionesExcel(tableFacade);
		this.editarContenidoColumnasExcel(tableFacade);
		this.cabecerasColumnasExcel(tableFacade);
		tableFacade.render();
	}

	private void eliminarColumnaAccionesExcel(final TableFacade tableFacade) {
		Table table = tableFacade.getTable();
		List<Column> columns = table.getRow().getColumns();
		Row finalRow = new Row();
		for (Column column : columns) {
			String property = column.getProperty();
			if (property != null && !property.equals("id")) {
				finalRow.addColumn(column);
			}
		}
		table.setRow(finalRow);
		tableFacade.setTable(table);
	}

	private void editarContenidoColumnasExcel(TableFacade tableFacade) {
		Table table = tableFacade.getTable();
		this.editarContenidoCelda(table, DATE_CELL_EDITOR, "fecha");
		this.editarContenidoCelda(table, this.descripcionAsuntoMotivoCellEditor(), "asunto");
		tableFacade.setTable(table);
	}

	private void editarContenidoCelda(Table table, CellEditor cellEditor, String nombreColumna) {
		table.getRow().getColumn(nombreColumna).getCellRenderer().setCellEditor(cellEditor);
	}

	private CellEditor descripcionAsuntoMotivoCellEditor() {
		return new CellEditor() {
			public Object getValue(final Object item, final String property, final int rowcount) {

				HtmlBuilder html = new HtmlBuilder();

				String tipoInc = (String) new BasicCellEditor().getValue(item, "tipoincdes", rowcount);
				String desc_Asunto = (String) new BasicCellEditor().getValue(item, "asunto", rowcount);
				String desc_Motivo = (String) new BasicCellEditor().getValue(item, "motivo", rowcount);

				if (tipoInc.equals("Incidencia")) {
					html.append(desc_Asunto);
				} else {
					html.append(desc_Motivo);
				}

				return html.toString();
			}
		};
	}

	private void cabecerasColumnasExcel(TableFacade tableFacade) {
		Table table = tableFacade.getTable();
		table.setCaption("LISTADO INCIDENCIAS");
		Row row = table.getRow();

		this.configCabecera(row, "codentidad", ENTIDAD_HEADER);
		this.configCabecera(row, "oficina", OFICINA_HEADER);
		this.configCabecera(row, "entmediadora", ES_MEDIADORA_HEADER);
		this.configCabecera(row, "codplan", PLAN_HEADER);
		this.configCabecera(row, "codlinea", "L&icaute;nea");
		this.configCabecera(row, "referencia", "P&oacute;liza");

		this.configCabecera(row, "tiporef", TIPO_REF_HEADER);
		this.configCabecera(row, "nifcif", NIFCIF_HEADER);
		this.configCabecera(row, "anho", ANHO_HEADER);
		this.configCabecera(row, "numero", "N&ucaute;mero");

		this.configCabecera(row, "idcupon", "Cup&oacute;n");
		this.configCabecera(row, "asunto", ASUNTO_HEADER);
		this.configCabecera(row, "estadoagrodes", EST_AGROSEG_HEADER);
		this.configCabecera(row, "estadodes", EST_AGROPLUS_HEADER);
		this.configCabecera(row, "fecha", FECHA_HEADER);
		this.configCabecera(row, "fechaSeguimientoStr", FECHA_SEGUIMIENTO_HEADER);
		this.configCabecera(row, "tipoincdes", TIPO_INC_HEADER);
		tableFacade.setTable(table);
	}

	private void configCabecera(Row row, String columnName, String title) {
		Column column = row.getColumn(columnName);
		column.setTitle(title);
		if (columnName.equals("fecha")) {
			column.getCellRenderer().setCellEditor(DATE_CELL_EDITOR);
		}
	}

	/* Pet. 50775 ** MODIF TAM (27.06.2018) ** Fin */

	@Override
	public int getIncidenciasAgroCountWithFilter(final IncidenciasAgroFilter filter, Date fechaEnvioDesde,
			Date fechaEnvioHasta) throws BusinessException {

		try {

			return this.incidenciasAgroDao.getIncidenciasAgroCountWithFilter(filter, fechaEnvioDesde, fechaEnvioHasta);

		} catch (Exception ex) {

			logger.error("getIncidenciasAgroCountWithFilter error. " + ex);
			throw new BusinessException("Se ha producido al obtener getIncidenciasCountWithFilter:", ex);
		}
	}

	@Override
	public Collection<EstadosInc> getEstadosInc() throws BusinessException {

		Collection<EstadosInc> result = null;

		try {

			result = this.incidenciasAgroDao.getEstadosInc();

		} catch (Exception ex) {

			logger.error("getEstadosInc error. " + ex);
			throw new BusinessException("Se ha producido al obtener getEstadosInc:", ex);
		}

		return result;
	}

	@Override
	public void borrarIncidencia(final Long incidenciaId) throws BusinessException {
		this.incidenciasAgroDao.removeObject(Incidencias.class, incidenciaId);
	}

	private TableFacade crearTableFacade(final HttpServletRequest request, final HttpServletResponse response,
			final VistaIncidenciasAgro vIncidenciasAgro, final String origenLlamada, Boolean excel) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(this.id, request);

		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade, excel);

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		// Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(int.class), new IntArchiveTypeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());

//		tableFacade.setExportTypes(response, ExportType.EXCEL);

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("listaIncidencias_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("listaIncidencias_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(columnas, vIncidenciasAgro, tableFacade);
			}
		}

		return tableFacade;
	}

	public void addListIncidenciasFilter(List<String> listaOficinas, IGenericoFilter filtro) {
		if (listaOficinas != null && listaOficinas.size() > 0) {
			filtro.addFilter("oficina", listaOficinas);
		}

	}

	private HashMap<String, String> cargarColumnas(final TableFacade tableFacade, Boolean excel) {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (this.columnas.size() == 0) {
			this.columnas.put(_ID, "id");
			this.columnas.put(ANHO, "anho");
			this.columnas.put(NUMERO, "numero");
			this.columnas.put(CODASUNTO, "codasunto");
			this.columnas.put(ASUNTO, "asunto");
			this.columnas.put(CODMOTIVO, "codmotivo");
			this.columnas.put(MOTIVO, "motivo");
			this.columnas.put(CODESTADO, "codestado");
			this.columnas.put(ESTADO_AGROPLUS, "estadodes");
			this.columnas.put(FECHA, "fecha");
			this.columnas.put(COD_DOC_AFECTADO, "coddocafectado");
			this.columnas.put(DOC_AFECTADO, "docafectado");
			this.columnas.put(TIPO_POLIZA, "tiporef");
			this.columnas.put(ID_ENVIO, "idenvio");
			this.columnas.put(NIF_CIF, "nifcif");
			this.columnas.put(CUPON, "idcupon");
			this.columnas.put(ENTIDAD, "codentidad");
			this.columnas.put(OFICINA, "oficina");
			this.columnas.put("ENT_MEDIADORA", "entmediadora");
			this.columnas.put("SUBENT_MEDIADORA", "subentmediadora");
			this.columnas.put(DELEGACION, "delegacion");
			this.columnas.put(PLAN, "codplan");
			this.columnas.put(LINEA, "codlinea");
			this.columnas.put(ESTADO_AGROSEGURO, "estadoagrodes");
			this.columnas.put(ESTADO_INC_AGRO, "codestadoagro");
			this.columnas.put(FECHA_ENVIO_DESDE, "fechaEnvioDesde");
			this.columnas.put(FECHA_ENVIO_HASTA, "fechaEnvioHasta");
			this.columnas.put(REFERENCIA, "referencia");
			this.columnas.put(CODUSUARIO, "codusuario");
			this.columnas.put(TIPO_INC, "tipoinc");
			this.columnas.put(TIPO_INC_DES, "tipoincdes");
		}
		if (excel) {
			this.columnas.put(FECHASEGUIMIENTO, "fechaSeguimientoStr");
		}
		tableFacade.setColumnProperties(this.columnas.get(_ID), this.columnas.get(ENTIDAD), this.columnas.get(OFICINA),
				this.columnas.get(ENT_MEDIADORA), this.columnas.get(PLAN), this.columnas.get(LINEA),
				this.columnas.get(REFERENCIA), this.columnas.get(TIPO_POLIZA), this.columnas.get(NIF_CIF),
				this.columnas.get(ANHO), this.columnas.get(NUMERO), this.columnas.get(CUPON), this.columnas.get(ASUNTO),
				this.columnas.get(ESTADO_AGROPLUS), this.columnas.get(ESTADO_AGROSEGURO), this.columnas.get(FECHA),
				this.columnas.get(TIPO_INC_DES));
		if (excel) {
			tableFacade.setColumnProperties(this.columnas.get(_ID), this.columnas.get(ENTIDAD),
					this.columnas.get(OFICINA), this.columnas.get(ENT_MEDIADORA), this.columnas.get(PLAN),
					this.columnas.get(LINEA), this.columnas.get(REFERENCIA), this.columnas.get(TIPO_POLIZA),
					this.columnas.get(NIF_CIF), this.columnas.get(ANHO), this.columnas.get(NUMERO),
					this.columnas.get(CUPON), this.columnas.get(ASUNTO), this.columnas.get(ESTADO_AGROPLUS),
					this.columnas.get(ESTADO_AGROSEGURO), this.columnas.get(FECHA), this.columnas.get(FECHASEGUIMIENTO),
					this.columnas.get(TIPO_INC_DES));
		}
		/*
		 * tableFacade.setColumnProperties(this.columnas.get(ID),
		 * this.columnas.get(ANHO), this.columnas.get(NUMERO),
		 * this.columnas.get(ASUNTO), this.columnas.get(ESTADO),
		 * this.columnas.get(FECHA), this.columnas.get(DOC_AFECTADO),
		 * this.columnas.get(TIPO_POLIZA), this.columnas.get(ID_ENVIO));
		 */
		return this.columnas;
	}

	private void cargarFiltrosBusqueda(final HashMap<String, String> columnas,
			final VistaIncidenciasAgro vIncidenciasAgro, final TableFacade tableFacade) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (vIncidenciasAgro.getCodentidad() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(ENTIDAD), vIncidenciasAgro.getCodentidad().toString()));

		if (vIncidenciasAgro.getOficina() != null && !vIncidenciasAgro.getOficina().isEmpty()) {
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(OFICINA), vIncidenciasAgro.getOficina()));
		}

		if (vIncidenciasAgro.getEntmediadora() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(ENT_MEDIADORA), vIncidenciasAgro.getEntmediadora().toString()));

		if (vIncidenciasAgro.getSubentmediadora() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(columnas.get(SUBENT_MEDIADORA), vIncidenciasAgro.getSubentmediadora().toString()));

		if (vIncidenciasAgro.getDelegacion() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(DELEGACION), vIncidenciasAgro.getDelegacion().toString()));

		if (vIncidenciasAgro.getCodplan() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(PLAN), vIncidenciasAgro.getCodplan().toString()));

		if (vIncidenciasAgro.getCodlinea() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(LINEA), vIncidenciasAgro.getCodlinea().toString()));

		if (vIncidenciasAgro.getCodestado() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODESTADO), vIncidenciasAgro.getCodestado().toString()));

		if (vIncidenciasAgro.getCodestadoagro() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(columnas.get(ESTADO_INC_AGRO), vIncidenciasAgro.getCodestadoagro().toString()));

		if (vIncidenciasAgro.getNifcif() != null && !vIncidenciasAgro.getNifcif().isEmpty())
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(NIF_CIF), vIncidenciasAgro.getNifcif().toString()));

		if (vIncidenciasAgro.getNumero() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(NUMERO), vIncidenciasAgro.getNumero().toString()));

		if (vIncidenciasAgro.getIdcupon() != null && !vIncidenciasAgro.getIdcupon().isEmpty())
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CUPON), vIncidenciasAgro.getIdcupon().toString()));

		if (vIncidenciasAgro.getCodasunto() != null && !vIncidenciasAgro.getCodasunto().isEmpty())
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODASUNTO), vIncidenciasAgro.getCodasunto()));

		if (vIncidenciasAgro.getCodmotivo() != null && vIncidenciasAgro.getCodmotivo() != 0)
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(columnas.get(CODMOTIVO), String.valueOf(vIncidenciasAgro.getCodmotivo())));

		if (vIncidenciasAgro.getFecha() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(FECHA), sdf.format(vIncidenciasAgro.getFecha())));

		if (vIncidenciasAgro.getTiporef() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(TIPO_POLIZA), vIncidenciasAgro.getTiporef().toString()));

		if (vIncidenciasAgro.getReferencia() != null && !vIncidenciasAgro.getReferencia().isEmpty())
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(REFERENCIA), vIncidenciasAgro.getReferencia().toString()));

		if (vIncidenciasAgro.getCodusuario() != null && !vIncidenciasAgro.getCodusuario().isEmpty())
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODUSUARIO), vIncidenciasAgro.getCodusuario().toString()));

		if (vIncidenciasAgro.getTipoinc() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(TIPO_INC), vIncidenciasAgro.getTipoinc().toString()));

	}

	private String html(TableFacade tableFacade, final Usuario usuario) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();

		table.getRow().setUniqueProperty("id");

		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {

			tableFacade.render(); // Will write the export data out to the
									// response.
			return null; // In Spring return null tells the controller not to do
							// anything.

		} else {

			// Configuracion de los datos de las columnas que requieren un
			// tratamiento para mostrarse

			////
			table.getRow().getColumn(columnas.get("ID")).getCellRenderer().setCellEditor(new CellEditor() {
				public Object getValue(final Object item, final String property, final int rowcount) {

					HtmlBuilder html = new HtmlBuilder();

					Long id = (Long) new BasicCellEditor().getValue(item, "idincidencia", rowcount);

					// PET. 50775 ** MODIF TAM (03.07.2018)
					// Si el estado de la incidencia es "Enviada correcta" las acciones de Borrar y
					// Editar no deben estar visibles
					String estado_agroplus = StringUtils
							.nullToString((String) new BasicCellEditor().getValue(item, "estadodes", rowcount));
					String tipo_incidencia = StringUtils
							.nullToString((String) new BasicCellEditor().getValue(item, "tipoincdes", rowcount));
					if (estado_agroplus.compareTo(ENV_CORRECTA) == 0) {

						if (tipo_incidencia.compareTo(INCIDENCIA) == 0) {
							// boton consultar de Incidencias
							html.a().href().quote().append("javascript:consultarAgroseguros(" + id + ");").quote()
									.close();
							html.append(
									"<img src=\"jsp/img/displaytag/information.png\" alt=\"Consulta incidencia\" title=\"Consulta incidencia\"/>");
							html.aEnd();
							html.append("&nbsp;");

							return html.toString();
						} else {
							// boton consultar de Anulacion y Rescision
							html.a().href().quote().append("javascript:consultarAnulyResc(" + id + ");").quote()
									.close();
							html.append(
									"<img src=\"jsp/img/displaytag/information.png\" alt=\"Consulta Anulaci&oacute;n/Rescisi&oacute;n\" title=\"Consulta Anulaci&oacute;n/Rescisi&oacute;n\"/>");
							html.aEnd();
							html.append("&nbsp;");

							return html.toString();
						}
					} else {
						if (tipo_incidencia.compareTo(INCIDENCIA) == 0 || (tipo_incidencia.compareTo(INCIDENCIA) != 0
								&& Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil()))) {

							// Incidencias
							if (tipo_incidencia.compareTo(INCIDENCIA) == 0) {

								// boton editar
								html.a().href().quote().append("javascript:editar(" + id + ");").quote().close();
								html.append(
										"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Incidencia\" title=\"Editar Incidencia\"/>");
								html.aEnd();
								html.append("&nbsp;");

								// boton borrar
								html.a().href().quote()
										.append("javascript:borrar(" + id + ", '" + tipo_incidencia + "');").quote()
										.close();
								html.append(
										"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar la incidencia\" title=\"Eliminar la incidencia\"/>");
								html.aEnd();
								html.append("&nbsp;");

								// boton consultar
								html.a().href().quote().append("javascript:consultarAgroseguros(" + id + ");").quote()
										.close();
								html.append(
										"<img src=\"jsp/img/displaytag/information.png\" alt=\"Consulta Incidencia\" title=\"Consulta Incidencia\"/>");
								html.aEnd();
								html.append("&nbsp;");
								// Anulacion y Rescision
							} else {
								// boton Editar (Anulacion y Rescision)
								html.a().href().quote().append("javascript:editarAnulyResc(" + id + ");").quote()
										.close();
								html.append(
										"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Anulaci&oacute;n/Rescisi&oacute;n\" title=\"Editar Anulaci&oacute;n/Rescisi&oacute;n\"/>");
								html.aEnd();
								html.append("&nbsp;");

								// boton borrar (Anulacion y Rescision)
								html.a().href().quote()
										.append("javascript:borrar(" + id + ", '" + tipo_incidencia + "');").quote()
										.close();
								html.append(
										"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar Anulaci&oacute;n/Rescisi&oacute;n\" title=\"Eliminar Anulaci&oacute;n/Rescisi&oacute;n\"/>");
								html.aEnd();
								html.append("&nbsp;");

								// boton consultar (Anulacion y Rescision)
								html.a().href().quote().append("javascript:consultarAnulyResc(" + id + ");").quote()
										.close();
								html.append(
										"<img src=\"jsp/img/displaytag/information.png\" alt=\"Consulta Anulaci&oacute;n/Rescisi&oacute;n\" title=\"Consulta Anulaci&oacute;n/Rescisi&oacute;n\"/>");
								html.aEnd();
								html.append("&nbsp;");

							}

							return html.toString();

							// Si la tipologia es "Rescision" o "Anulacion" y el perfil es <> 0, solo se
							// podra consultar.
						} else {
							// boton consultar
							html.a().href().quote().append("javascript:consultarAnulyResc(" + id + ");").quote()
									.close();
							html.append(
									"<img src=\"jsp/img/displaytag/information.png\" alt=\"Consulta Anulaci&oacute;n/Rescisi&oacute;n\" title=\"Consulta Anulaci&oacute;n/Rescisi&oacute;n\"/>");
							html.aEnd();
							html.append("&nbsp;");

							return html.toString();

						}
					}
				}
			});

			table.getRow().getColumn(columnas.get(ENT_MEDIADORA)).getCellRenderer().setCellEditor(new CellEditor() {

				public Object getValue(final Object item, final String property, final int rowcount) {

					HtmlBuilder html = new HtmlBuilder();

					BigDecimal entMedia = (BigDecimal) new BasicCellEditor().getValue(item, "entmediadora", rowcount);
					BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor().getValue(item, "subentmediadora",
							rowcount);

					if (entMedia == null) {
						return "&nbsp;";
					}

					html.append(entMedia + "-" + subEntMedia);

					return html.toString();
				}
			});

			table.getRow().getColumn(columnas.get(ASUNTO)).getCellRenderer().setCellEditor(new CellEditor() {

				public Object getValue(final Object item, final String property, final int rowcount) {

					HtmlBuilder html = new HtmlBuilder();

					String tipoInc = (String) new BasicCellEditor().getValue(item, "tipoincdes", rowcount);
					String desc_Asunto = (String) new BasicCellEditor().getValue(item, "asunto", rowcount);
					String desc_Motivo = (String) new BasicCellEditor().getValue(item, "motivo", rowcount);

					if (tipoInc.equals("Incidencia")) {
						html.append(desc_Asunto);
					} else {
						html.append(desc_Motivo);
					}

					return html.toString();
				}
			});

		}

		return tableFacade.render();
	}

	private void configurarColumnas(final HtmlTable table) {

		/* MODIF TAM (13.06.2018) - Se cambian las columnas del grid */
		/*
		 * configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false,
		 * "3%"); configColumna(table, columnas.get(ANHO), "A\u00f1o", false, true,
		 * "5%"); configColumna(table, columnas.get(NUMERO), "N&uacute;mero", true,
		 * true, "10%"); configColumna(table, columnas.get(ASUNTO), "Asunto", true,
		 * true, "12%"); configColumna(table, columnas.get(ESTADO), "Estado", true,
		 * true, "13%"); configColumnaFecha(table, columnas.get(FECHA), "Fecha", true,
		 * true, "17%", "dd/MM/yyyy"); configColumna(table, columnas.get(DOC_AFECTADO),
		 * "Doc Afectado", false, true, "18%"); configColumna(table,
		 * columnas.get(TIPO_POLIZA), "Tipo de p&oacute;liza", true, true, "9%");
		 * configColumna(table, columnas.get(ID_ENVIO), "Id env&iacute;o", false, true,
		 * "13%");
		 */

		configColumna(table, columnas.get(_ID), "&nbsp;&nbsp;Acciones", false, false, "3%");
		configColumna(table, columnas.get(ENTIDAD), "Ent.</br>", true, true, "4%");
		configColumna(table, columnas.get(OFICINA), "Ofi.</br>", true, true, "4%");
		configColumna(table, columnas.get(ENT_MEDIADORA), "E-S Med.</br>", true, true, "7%");
		configColumna(table, columnas.get(PLAN), "Plan</br>", true, true, "3%");
		configColumna(table, columnas.get(LINEA), "L&iacute;nea</br>", true, true, "4%");
		configColumna(table, columnas.get(REFERENCIA), "P&oacute;liza</br>", true, true, "7%");
		configColumna(table, columnas.get(TIPO_POLIZA), "Tipo Ref.</br>", true, true, "3%");
		configColumna(table, columnas.get(NIF_CIF), "NIF/CIF.</br>", true, true, "5%");
		configColumna(table, columnas.get(ANHO), "A\u00f1o</br>", false, true, "5%");
		configColumna(table, columnas.get(NUMERO), "N&uacute;mero</br>", true, true, "4%");
		configColumna(table, columnas.get(CUPON), "Cup&oacute;n</br>", true, true, "6%");
		configColumna(table, columnas.get(ASUNTO), "Asunto</br>", true, true, "12%");
		configColumna(table, columnas.get(ESTADO_AGROPLUS), "Est. Agroplus</br>", true, true, "8%");
		configColumna(table, columnas.get(ESTADO_AGROSEGURO), "Est. Agroseguro</br>", true, true, "8%");
		configColumnaFecha(table, columnas.get(FECHA), "Fec. Env.</br>", true, true, "8%", "dd/MM/yyyy");
		configColumna(table, columnas.get(TIPO_INC_DES), "Tipolog&iacute;a</br>", true, true, "8%");
	}

	private void configColumna(final HtmlTable table, final String idCol, final String title, final boolean filterable,
			final boolean sortable, final String width) {

		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);

	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla
	 * 
	 * @param table
	 *            Objeto que contiene la tabla
	 * @param idCol
	 *            Id de la columna
	 * @param title
	 *            Titulo de la columna
	 * @param filterable
	 *            Indica si se podra buscar por esa columna
	 * @param sortable
	 *            Indica si se podra ordenar por esa columna
	 * @param width
	 *            Ancho de la columna
	 * @param fFecha
	 *            Formato de fecha con la que se mostraran los datos de esta columna
	 */
	private void configColumnaFecha(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Anhade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error("Ocurrio un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}

	private void setDataAndLimitVariables(final TableFacade tableFacade, IncidenciasAgroFilter consultaFilter,
			final Limit limit, Date fechaEnvioDesde, Date fechaEnvioHasta) {

		Collection<VistaIncidenciasAgro> items = new ArrayList<VistaIncidenciasAgro>();
		try {

			int totalRows = getIncidenciasAgroCountWithFilter(consultaFilter, fechaEnvioDesde, fechaEnvioHasta);
			logger.debug("********** count filas para IncidenciasAgro = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			incidenciasSort = getConsultaIncidenciasAgroSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getIncidenciasAgroWithFilterAndSort(consultaFilter, incidenciasSort, rowStart, rowEnd, fechaEnvioDesde,
					fechaEnvioHasta);
			logger.debug("********** list items para IncidenciasAgro = " + items.size() + " **********");

		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos de bd en la tabla
		tableFacade.setItems(items);

	}

	private Collection<VistaIncidenciasAgro> getIncidenciasAgroWithFilterAndSort(final IncidenciasAgroFilter filter,
			final IncidenciasSort sort, final int rowStart, final int rowEnd, Date fechaEnvioDesde,
			Date fechaEnvioHasta) throws BusinessException {

		try {

			return this.incidenciasAgroDao.getIncidenciasAgroWithFilterAndSort(filter, sort, rowStart, rowEnd,
					fechaEnvioDesde, fechaEnvioHasta);

		} catch (Exception ex) {

			logger.error("getIncidenciasAgroWithFilterAndSort error. " + ex);
			throw new BusinessException("Se ha producido al obtener getIncidenciasAgroWithFilterAndSort:", ex);
		}
	}

	private IncidenciasSort getConsultaIncidenciasAgroSort(Limit limit) {
		IncidenciasSort consultaSort = new IncidenciasSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}
		return consultaSort;
	}

	private IncidenciasAgroFilter getConsultaIncFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,
			List<BigDecimal> listaGrupoOficinas, List<String> listOficinas) {
		IncidenciasAgroFilter consultaFilter = new IncidenciasAgroFilter();
		FilterSet filterSet = limit.getFilterSet();

		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			consultaFilter.addFilter(property, value);
		}

		// Si la lista de grupos de entidades no esta vacia se incluye en el filtro de
		// busqueda
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			consultaFilter.addFilter("listaGrupoEntidades", listaGrupoEntidades);
		}

		// Si la lista de oficinas no esta vacia se incluye en el filtro de busqueda
		if (listOficinas == null || listOficinas.size() == 0) {
			if (listaGrupoOficinas != null && listaGrupoOficinas.size() > 0) {
				consultaFilter.addFilter("listaGrupoOficinas", listaGrupoOficinas);
			}
		}

		return consultaFilter;
	}

	@Override
	public String cargarNombreEntidad(BigDecimal codEntidad) throws BusinessException {
		try {
			return this.incidenciasAgroDao.getNombreEntidad(codEntidad);
		} catch (Exception e) {
			throw new BusinessException("Error al intentar obtener el nombre de la Entidad", e);
		}
	}

	@Override
	public String cargarNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) throws BusinessException {
		try {
			return this.incidenciasAgroDao.getNombreOficina(codOficina, codEntidad);
		} catch (Exception e) {
			throw new BusinessException("Error al obtener el nombre de Oficina", e);
		}
	}

	@Override
	public String cargarNombreLinea(BigDecimal codLinea) throws BusinessException {
		try {
			return this.incidenciasAgroDao.getNombreLinea(codLinea);
		} catch (Exception e) {
			throw new BusinessException("Error al obtener el nombre de la Linea", e);
		}
	}

	public HashMap<String, String> getColumnas() {
		return columnas;
	}

	public void setColumnas(HashMap<String, String> columnas) {
		this.columnas = columnas;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IIncidenciasAgroDao getIncidenciasAgroDao() {
		return incidenciasAgroDao;
	}

	public void setIncidenciasAgroDao(final IIncidenciasAgroDao incidenciasAgroDao) {
		this.incidenciasAgroDao = incidenciasAgroDao;
	}
	
	public List<VistaIncidenciasAgro> getAllFilteredAndSorted() throws DAOException {
	    // Obtener todos los registros filtrados y ordenados sin límites de paginación
	    Collection<VistaIncidenciasAgro> allResults = incidenciasAgroDao.getIncidenciasAgroWithFilterAndSort(incidenciasAgroFilter, incidenciasSort, -1, -1, fechaEnvioDesde, fechaEnvioHasta);
	    return (List<VistaIncidenciasAgro>) allResults;
	}

}