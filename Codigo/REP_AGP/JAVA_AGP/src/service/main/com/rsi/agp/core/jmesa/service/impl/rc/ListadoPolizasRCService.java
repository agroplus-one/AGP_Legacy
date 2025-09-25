package com.rsi.agp.core.jmesa.service.impl.rc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Filter;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
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
import com.rsi.agp.core.jmesa.filter.gan.VistaPolizasRCFilter;
import com.rsi.agp.core.jmesa.service.IListadoPolizasRCService;
import com.rsi.agp.core.jmesa.sort.VistaPolizasRCSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.models.rc.IListadoPolizasRCDao;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.rc.ErroresRC;
import com.rsi.agp.dao.tables.rc.EstadosRC;
import com.rsi.agp.dao.tables.rc.PermisosPerfilRC;
import com.rsi.agp.dao.tables.rc.PolizasHistEstadosRC;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.rc.VistaPolizasRC;

@SuppressWarnings("deprecation")
public class ListadoPolizasRCService implements IListadoPolizasRCService {

	private static final String PUNTOS_SUSPENSIVOS = "...";
	private static final String ESPACIO_BLANCO_ESCAPADO = "&nbsp;";
	private static final String STRING_VACIO = "";
	private static final DateCellEditor DATE_CELL_EDITOR = new DateCellEditor("dd/MM/yyyy");
	private static final Log LOGGER = LogFactory.getLog(ListadoPolizasRCService.class);
	
	private HashMap<String, String> columnas = new HashMap<String, String>();
	
	private static final BigDecimal POLIZARC_ESTADO_ANULADA = new BigDecimal("6");
	private static final BigDecimal POLIZARC_ESTADO_DEFINITIVA = new BigDecimal("2");
	private static final BigDecimal POLIZARC_ESTADO_GRABACION_PROVISIONAL = new BigDecimal("1");
	private static final BigDecimal POLIZARC_ESTADO_ENVIADA_ERRONEA = new BigDecimal("4");

	private static final String ACCIONES_HEADER = "&nbsp;&nbsp;Acciones";
	private static final String ENTIDAD_HEADER = "Entidad";
	private static final String OFICINA_HEADER = "Oficina";
	private static final String USUARIO_HEADER = "Usuario";
	private static final String PLAN_HEADER = "Plan";
	private static final String LINEA_HEADER = "L&iacute;nea";
	private static final String COLECTIVO_HEADER = "Colectivo";
	private static final String POLIZA_HEADER = "P&oacute;liza";
	private static final String NIFCIF_HEADER = "NIF/CIF";
	private static final String CLASE_HEADER = "CL";
	private static final String ESTADO_POLIZA_HEADER = "E.P.";
	private static final String FECHA_ENVIO_RC_HEADER = "F. Env&iacute;o RC";
	private static final String SUMA_ASEGURDA_HEADER = "Sum. Aseg.";
	private static final String IMPORTE_HEADER = "Importe";
	private static final String ESTADO_RC_HEADER = "Estado RC";
	private static final String DETALLE_HEADER = "Detalle";
	private static final String OMEGA_HEADER = "Ref. OMEGA";
	private static final String N_SOLICITUD_HEADER = "N&deg; Solicitud";

	private IListadoPolizasRCDao listadoPolizasRCDao;
	private String id;
	
	public void setListadoPolizasRCDao(IListadoPolizasRCDao listadoPolizasRCDao) {
		this.listadoPolizasRCDao = listadoPolizasRCDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getTablaPolizasRC(HttpServletRequest req,
			HttpServletResponse res, final VistaPolizasRC vistaPolizasRC, final BigDecimal perfilUsuario)
			throws BusinessException {
		try {
			Boolean excel = Boolean.valueOf(req.getParameter("excel"));
			
			TableFacade tableFacade = this.crearTableFacade(req, res, vistaPolizasRC);
			VistaPolizasRCFilter filter = this.setDataAndLimitVariables(tableFacade);
			if(tableFacade.getLimit().hasExport() && excel){
				this.pintarExcel(tableFacade);
				return null;
			}
			StringBuilder script = this.getIdsTodos(filter);
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
			PermisosPerfilRC permisosRC = this.listadoPolizasRCDao.getPermisosRC(perfilUsuario);
			return new StringBuilder(this.pintarHtml(tableFacade, permisosRC)).append(script).toString();
		} catch (Exception e) {
			LOGGER.error(e);
			throw new BusinessException("Se ha producido al obtener getTablaImpuestos: ", e);
		}
	}

	private StringBuilder getIdsTodos(VistaPolizasRCFilter filter) throws DAOException {
		String listaIdsTodos = this.getlistaIdsTodos(filter);
		return new StringBuilder("<script>$(\"#listaIdsTodos\").val(\"").append(listaIdsTodos).append("\");</script>");
	}

	private TableFacade crearTableFacade(final HttpServletRequest req, HttpServletResponse res,
			final VistaPolizasRC vistaPolizasRC) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(this.id, req);
		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		Map<String, String> columnas = this.cargarColumnas(tableFacade);

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
		// No sé lo que hace, pero sin esta linea, no filtra por fechas
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.setExportTypes(res, ExportType.EXCEL);
		// Si no es una llamada a traves de ajax
		if (req.getParameter("ajax") == null) {
			if (req.getParameter("origenLlamada") == null) {
				if (req.getSession().getAttribute("listaPolizasRC_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) req.getSession().getAttribute("listaPolizasRC_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				this.cargarFiltrosBusqueda(columnas, vistaPolizasRC, tableFacade);
			}
		}
		return tableFacade;
	}

	private Map<String, String> cargarColumnas(final TableFacade tableFacade) {
		if (this.columnas.isEmpty()) {
			this.columnas.put(ConstantsRC.ID_POLIZA_KEY,ConstantsRC.ID_POLIZA_VAL);
			this.columnas.put(ConstantsRC.ENTIDAD_KEY, ConstantsRC.ENTIDAD_VAL);
			this.columnas.put(ConstantsRC.OFICINA_KEY, ConstantsRC.OFICINA_VAL);
			this.columnas.put(ConstantsRC.USUARIO_KEY, ConstantsRC.USUARIO_VAL);
			this.columnas.put(ConstantsRC.PLAN_KEY, ConstantsRC.PLAN_POLIZA_VAL);
			this.columnas.put(ConstantsRC.LINEA_KEY, ConstantsRC.LINEA_VAL);
			this.columnas.put(ConstantsRC.REF_COL_KEY, ConstantsRC.REF_COL_VAL);
			this.columnas.put(ConstantsRC.REF_POLIZA_KEY, ConstantsRC.REF_POLIZA_VAL);
			this.columnas.put(ConstantsRC.NIFCIF_KEY, ConstantsRC.NIFCIF_VAL);
			this.columnas.put(ConstantsRC.CLASE_KEY, ConstantsRC.CLASE_VAL);
			this.columnas.put(ConstantsRC.ESTADO_POL_KEY,ConstantsRC.ESTADO_POL_VAL);
			this.columnas.put(ConstantsRC.FEC_ENVIO_RC_KEY,ConstantsRC.FEC_ENVIO_RC_VAL);
			this.columnas.put(ConstantsRC.SUMA_ASEGURADA_KEY, ConstantsRC.SUMA_ASEGURADA_VAL);
			this.columnas.put(ConstantsRC.IMPORTE_KEY, ConstantsRC.IMPORTE_VAL);
			this.columnas.put(ConstantsRC.ESTADO_RC_KEY, ConstantsRC.ESTADO_RC_VAL);
			this.columnas.put(ConstantsRC.ERROR_RC_KEY, ConstantsRC.ERROR_RC_VAL);
			this.columnas.put(ConstantsRC.REF_OMEGA_KEY, ConstantsRC.REF_OMEGA_VAL);
			//this.columnas.put("prueba", "nsolicitud");
			this.columnas.put(ConstantsRC.N_SOLICITUD_KEY, ConstantsRC.N_SOLICITUD_VAL);
		}
		tableFacade.setColumnProperties(this.columnas.get(ConstantsRC.ID_KEY),
				this.columnas.get(ConstantsRC.ID_POLIZA_KEY),
				this.columnas.get(ConstantsRC.ENTIDAD_KEY),
				this.columnas.get(ConstantsRC.OFICINA_KEY),
				this.columnas.get(ConstantsRC.USUARIO_KEY),
				this.columnas.get(ConstantsRC.PLAN_KEY),
				this.columnas.get(ConstantsRC.LINEA_KEY),
				this.columnas.get(ConstantsRC.REF_COL_KEY),
				this.columnas.get(ConstantsRC.REF_POLIZA_KEY),
				this.columnas.get(ConstantsRC.NIFCIF_KEY),
				this.columnas.get(ConstantsRC.CLASE_KEY),
				this.columnas.get(ConstantsRC.ESTADO_POL_KEY),
				this.columnas.get(ConstantsRC.FEC_ENVIO_RC_KEY),
				this.columnas.get(ConstantsRC.SUMA_ASEGURADA_KEY),
				this.columnas.get(ConstantsRC.IMPORTE_KEY),
				this.columnas.get(ConstantsRC.ESTADO_RC_KEY),
				this.columnas.get(ConstantsRC.ERROR_RC_KEY),
				this.columnas.get(ConstantsRC.REF_OMEGA_KEY),
				//this.columnas.get("prueba")
				this.columnas.get(ConstantsRC.N_SOLICITUD_KEY)
		);
		return this.columnas;
	}

	private void cargarFiltrosBusqueda(Map<String, String> columnas, VistaPolizasRC vistaPolizasRC, TableFacade tableFacade) {
		if(vistaPolizasRC.getClase() != null) {
			 this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.CLASE_KEY), vistaPolizasRC.getClase().toString());
		}
		String codespecierc = vistaPolizasRC.getCodespecierc();
		if(codespecierc != null && !codespecierc.equals("")){
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.COD_ESPECIE_RC_KEY), vistaPolizasRC.getCodespecierc());
		}
		String desestadopol = vistaPolizasRC.getDesestadopol();
		if(desestadopol != null && !desestadopol.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.DES_ESTADO_POL_KEY), vistaPolizasRC.getDesestadopol());
		}
		String desestadorc = vistaPolizasRC.getDesestadorc();
		if(desestadorc != null && !desestadorc.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.DES_ESTADO_RC_KEY), vistaPolizasRC.getDesestadorc());
		}
		String detalle = vistaPolizasRC.getDetalle();
		if(detalle != null && !detalle.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.DETALLE_KEY), vistaPolizasRC.getDetalle());
		}
		if(vistaPolizasRC.getEntidad() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.ENTIDAD_KEY), vistaPolizasRC.getEntidad().toString());
		}
		String errorrc = vistaPolizasRC.getErrorrc();
		if(errorrc != null && !errorrc.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.ERROR_RC_KEY), vistaPolizasRC.getErrorrc());
		}
		if(vistaPolizasRC.getEstadopol() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.ESTADO_POL_KEY), vistaPolizasRC.getEstadopol().toString());
		}
		if(vistaPolizasRC.getEstadorc() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.ESTADO_RC_KEY), vistaPolizasRC.getEstadorc().toString());
		}
		if(vistaPolizasRC.getFecenviorc() != null) {
			String fecha = new SimpleDateFormat("dd/MM/yyyy").format(vistaPolizasRC.getFecenviorc());
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.FEC_ENVIO_RC_KEY), fecha);
		}
		if(vistaPolizasRC.getLinea() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.LINEA_KEY), vistaPolizasRC.getLinea().toString());
		}
		String modulo = vistaPolizasRC.getModulo();
		if(modulo != null && !modulo.equals("")) {
			this.crearFiltro(tableFacade, ConstantsRC.MODULO_VAL, vistaPolizasRC.getModulo());
		}
		String oficina = vistaPolizasRC.getOficina();
		if(oficina != null && !oficina.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.OFICINA_KEY), vistaPolizasRC.getOficina());
		}
		if(vistaPolizasRC.getPlan() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.PLAN_KEY), vistaPolizasRC.getPlan().toString());
		}
		String refcolectivo = vistaPolizasRC.getRefcolectivo();
		if(refcolectivo != null && !refcolectivo.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.REF_COL_KEY), vistaPolizasRC.getRefcolectivo());
		}
		if(vistaPolizasRC.getRefomega() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.REF_OMEGA_KEY), vistaPolizasRC.getRefomega().toString());
		}
		if(vistaPolizasRC.getIdpoliza() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.ID_POLIZA_KEY), vistaPolizasRC.getIdpoliza().toString());
		}
		String refpoliza = vistaPolizasRC.getRefpoliza();
		if(refpoliza != null && !refpoliza.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.REF_POLIZA_KEY), vistaPolizasRC.getRefpoliza());
		}
		if(vistaPolizasRC.getSumaasegurada() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.SUMA_ASEGURADA_KEY), vistaPolizasRC.getSumaasegurada().toString());
		}
		String usuario = vistaPolizasRC.getUsuario();
		if(usuario != null && !usuario.equals("")) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.USUARIO_KEY), vistaPolizasRC.getUsuario());
		}
		String nifcif = vistaPolizasRC.getNifcif();
		if(StringUtils.isNotBlank(nifcif)){
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.NIFCIF_KEY), vistaPolizasRC.getNifcif());
		}
		if(vistaPolizasRC.getIdpoliza() != null) {
			this.crearFiltro(tableFacade, this.columnas.get(ConstantsRC.N_SOLICITUD_KEY), vistaPolizasRC.getIdpoliza().toString());
		}
	}
	
	private void crearFiltro(TableFacade tableFacade, String property, String value){
		tableFacade.getLimit().getFilterSet().addFilter(new Filter(property, value)); 
	}

	private VistaPolizasRCFilter setDataAndLimitVariables(final TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit(); 
		
		VistaPolizasRCFilter filter = this.getConsultaVistaPolizasRCFilter(limit);
		
		try {
			int totalRows = this.getVistaPolizasRCCountWithFilter(filter);
			LOGGER.debug(new StringBuilder(
					"********** count filas para PolizasRC = ")
					.append(totalRows).append(" **********").toString());

			tableFacade.setTotalRows(totalRows);

			VistaPolizasRCSort sort = this.getConsultaVistaRCPolizasRCSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			List<VistaPolizasRC> items = this.getVistaPolizasRCWithFilterAndSort(filter, sort,
					rowStart, rowEnd);
			LOGGER.debug(new StringBuilder(
					"********** list items para PolizasRC = ")
					.append(items.size()).append(" **********").toString());
			// Carga los registros obtenidos del bd en la tabla
			tableFacade.setItems(items);
		} catch (BusinessException e) {
			LOGGER.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		return filter;
	}

	private VistaPolizasRCFilter getConsultaVistaPolizasRCFilter(
			final Limit limit) {
		VistaPolizasRCFilter vistaPolizaRCFilter = new VistaPolizasRCFilter();
		Collection<Filter> filters = limit.getFilterSet().getFilters();
		for (Filter filter : filters) {
			vistaPolizaRCFilter.addFilter(filter.getProperty(), filter.getValue());	
		}
		return vistaPolizaRCFilter;
	}

	private VistaPolizasRCSort getConsultaVistaRCPolizasRCSort(final Limit limit) {
		VistaPolizasRCSort consultaSort = new VistaPolizasRCSort();
		Collection<Sort> sorts = limit.getSortSet().getSorts();
		for (Sort sort : sorts) {
			consultaSort.addSort(sort.getProperty(), sort.getOrder().toParam());
		}
		return consultaSort;
	}
	
	private void pintarExcel(TableFacade tableFacade){
		this.eliminarColumnaAccionesExcel(tableFacade);
		this.editarContenidoColumnasExcel(tableFacade);
		this.cabecerasColumnasExcel(tableFacade);
		tableFacade.render();
	}

	private String pintarHtml(final TableFacade tableFacade, final PermisosPerfilRC permisosRC) {
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");
		this.cabecerasColumnasHtml(table);
		this.editarContenidoCeldasHtml(permisosRC, table);
		return tableFacade.render();
	}
	
	private void cabecerasColumnasExcel(TableFacade tableFacade){
		Table table = tableFacade.getTable();
		table.setCaption("LISTADO PÓLIZAS RC DE GANADO");
		Row row = table.getRow();
		this.configCabecera(row, ConstantsRC.ENTIDAD_VAL, ENTIDAD_HEADER);
		this.configCabecera(row, ConstantsRC.OFICINA_VAL, OFICINA_HEADER);
		this.configCabecera(row, ConstantsRC.USUARIO_VAL, USUARIO_HEADER);
		this.configCabecera(row, ConstantsRC.PLAN_POLIZA_VAL, PLAN_HEADER);
		this.configCabecera(row, ConstantsRC.LINEA_VAL, "Línea");
		this.configCabecera(row, ConstantsRC.REF_COL_VAL, COLECTIVO_HEADER);
		this.configCabecera(row, ConstantsRC.REF_POLIZA_VAL, "Póliza");
		this.configCabecera(row, ConstantsRC.NIFCIF_VAL, NIFCIF_HEADER);
		this.configCabecera(row, ConstantsRC.CLASE_VAL, CLASE_HEADER);
		this.configCabecera(row, ConstantsRC.REF_COL_VAL, COLECTIVO_HEADER);
		this.configCabecera(row, ConstantsRC.ESTADO_POL_VAL, ESTADO_POLIZA_HEADER);
		this.configCabecera(row, ConstantsRC.FEC_ENVIO_RC_VAL, "F. Envío RC");
		this.configCabecera(row, ConstantsRC.SUMA_ASEGURADA_VAL, SUMA_ASEGURDA_HEADER);
		this.configCabecera(row, ConstantsRC.IMPORTE_VAL, IMPORTE_HEADER);
		this.configCabecera(row, ConstantsRC.ESTADO_RC_VAL, ESTADO_RC_HEADER);
		this.configCabecera(row, ConstantsRC.ERROR_RC_VAL, DETALLE_HEADER);
		this.configCabecera(row, ConstantsRC.REF_OMEGA_VAL, OMEGA_HEADER);
		this.configCabecera(row, ConstantsRC.N_SOLICITUD_VAL, N_SOLICITUD_HEADER);
		tableFacade.setTable(table);
	}
	
	private void configCabecera(Row row, String columnName, String title){
		Column column = row.getColumn(columnName);
		column.setTitle(title);
		if(columnName.equals(ConstantsRC.FEC_ENVIO_RC_VAL)){
			column.getCellRenderer().setCellEditor(DATE_CELL_EDITOR);
		}
	}

	private void editarContenidoColumnasExcel(TableFacade tableFacade){
		Table table = tableFacade.getTable();
		this.editarContenidoCelda(table, this.colectivoCellEditor(), ConstantsRC.REF_COL_VAL);
		this.editarContenidoCelda(table, this.polizaCellEditorExcel(), ConstantsRC.REF_POLIZA_VAL);
		this.editarContenidoCelda(table, DATE_CELL_EDITOR, ConstantsRC.FEC_ENVIO_RC_VAL);
		this.editarContenidoCelda(table, this.estadoPolizaCellEditor(), ConstantsRC.ESTADO_POL_VAL);
		this.editarContenidoCelda(table, this.abreviaturaEstadoRCCellEditor(), ConstantsRC.ESTADO_RC_VAL);
		this.editarContenidoCelda(table, this.detalleErrorRCCellEditorExcel(), ConstantsRC.ERROR_RC_VAL);
		this.editarContenidoCelda(table, this.detalleNSolicitudCellEditorHtml(), ConstantsRC.N_SOLICITUD_VAL);
		tableFacade.setTable(table);
	}
	
	private void editarContenidoCeldasHtml(final PermisosPerfilRC permisosRC, HtmlTable table) {
		this.editarContenidoCelda(table, this.accionesCellEditor(permisosRC), ConstantsRC.ID_POLIZA_VAL);
		this.editarContenidoCelda(table, this.colectivoCellEditor(), ConstantsRC.REF_COL_VAL);
		this.editarContenidoCelda(table, this.polizaCellEditorHtml(), ConstantsRC.REF_POLIZA_VAL);
		this.editarContenidoCelda(table, DATE_CELL_EDITOR, ConstantsRC.FEC_ENVIO_RC_VAL);
		this.editarContenidoCelda(table, this.estadoPolizaCellEditor(), ConstantsRC.ESTADO_POL_VAL);
		this.editarContenidoCelda(table, this.abreviaturaEstadoRCCellEditor(), ConstantsRC.ESTADO_RC_VAL);
		this.editarContenidoCelda(table, this.detalleErrorRCCellEditorHtml(), ConstantsRC.ERROR_RC_VAL);
		this.editarContenidoCelda(table, this.detalleNSolicitudCellEditorHtml(), ConstantsRC.N_SOLICITUD_VAL);
	}
	
	private void editarContenidoCelda(Table table, CellEditor cellEditor, String nombreColumna){
		table.getRow().getColumn(nombreColumna).getCellRenderer().setCellEditor(cellEditor);
	}

	private void eliminarColumnaAccionesExcel(final TableFacade tableFacade) {
		Table table = tableFacade.getTable();
		List<Column> columns = table.getRow().getColumns();
		Row finalRow = new Row();
		for(Column column : columns){
			String property = column.getProperty();
			if(property != null && !property.equals(ConstantsRC.ID_POLIZA_VAL)){
				finalRow.addColumn(column);
			}
		}
		table.setRow(finalRow);
		tableFacade.setTable(table);
	}
	
	private CellEditor accionesCellEditor(final PermisosPerfilRC permisosRC){
		return new CellEditor() {
			public Object getValue(Object item, final String property, final int rowCount) {

				BigDecimal polizaId = (BigDecimal)new BasicCellEditor().getValue(item, ConstantsRC.ID_POLIZA_VAL, rowCount);
				String especieRC = (String)new BasicCellEditor().getValue(item, ConstantsRC.COD_ESPECIE_RC_VAL, rowCount);
				String codUsuario = (String)new BasicCellEditor().getValue(item, ConstantsRC.USUARIO_VAL, rowCount);
				BigDecimal estadoRC = (BigDecimal)new BasicCellEditor().getValue(item, ConstantsRC.ESTADO_RC_VAL, rowCount);
				BigDecimal plan = (BigDecimal)new BasicCellEditor().getValue(item, ConstantsRC.PLAN_POLIZA_VAL, rowCount);
				
				HtmlBuilder html = new HtmlBuilder();
				
				if(this.mostrarBotonBorrar(estadoRC)){
					html.append(this.botonBorrar(polizaId));
				}
				
				html.append(this.botonImprimirSimulacion(polizaId));
				html.append(this.botonImprimirCondiciones(plan, especieRC));

				if (this.mostrarBotonDefinitiva(permisosRC, estadoRC)) {
					html.append(this.botonPasarDfinitiva(polizaId, codUsuario));
				}
				
				if(this.mostrarBotonAnular(permisosRC, estadoRC)){
					html.append(this.botonAnular(polizaId, codUsuario));
				}
								
				return html.toString();
			}
			
			private boolean mostrarBotonBorrar(BigDecimal estadoRC){
				boolean estadoCorrecto = estadoRC.equals(POLIZARC_ESTADO_GRABACION_PROVISIONAL) && estadoRC.equals(POLIZARC_ESTADO_GRABACION_PROVISIONAL);
				return estadoCorrecto;
			}
			
			private boolean mostrarBotonAnular(final PermisosPerfilRC permisosRC, BigDecimal estadoRC) {
				boolean tienePermiso = permisosRC.getPermisoAnular() == Constants.CHARACTER_S;
				return tienePermiso && estadoRC.equals(ConstantsRC.ESTADO_RC_ENV_CORRECTA);
			}
			
			private boolean mostrarBotonDefinitiva(final PermisosPerfilRC permisosRC, BigDecimal estadoRC) {
				boolean tienePermiso = permisosRC.getPermisoAnular() == Constants.CHARACTER_S;
				boolean grabacionProvisional = estadoRC.equals(POLIZARC_ESTADO_GRABACION_PROVISIONAL);
				boolean enviadaErronea = estadoRC.equals(POLIZARC_ESTADO_ENVIADA_ERRONEA);
				boolean anulada = estadoRC.equals(POLIZARC_ESTADO_ANULADA);
				return tienePermiso && (grabacionProvisional || enviadaErronea) && !anulada;
			}

			private HtmlBuilder botonAnular(BigDecimal polizaId, String codUsuario) {
				HtmlBuilder html = new HtmlBuilder();
				String anular = String.format("javascript:anular(%s,'%s')", polizaId.toString(), codUsuario);
				html.a().href().quote().append(anular).quote().close();
				html.append("<img src=\"jsp/img/displaytag/cancel.png\" alt=\"Anular Poliza RC\" title=\"Anular Poliza RC\"/>");
				html.aEnd();
				html.append(ESPACIO_BLANCO_ESCAPADO);
				return html;
			}

			private HtmlBuilder botonPasarDfinitiva(BigDecimal polizaId, String codUsuario) {
				HtmlBuilder html = new HtmlBuilder();
				String.format("javascript:definitiva(%s,'%s')", polizaId.toString(), codUsuario);
				String definitiva = String.format("javascript:definitiva(%s,'%s')", polizaId.toString(), codUsuario);
				html.a().href().quote().append(definitiva).quote().close();
				html.append("<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar Poliza RC a definitiva\" title=\"Pasar Poliza RC a definitiva\"/>");
				html.aEnd();
				html.append(ESPACIO_BLANCO_ESCAPADO);
				return html;
			}

			private HtmlBuilder botonImprimirCondiciones(BigDecimal plan, String especieRC) {
				HtmlBuilder html = new HtmlBuilder();
				String imprimir = new StringBuilder("javascript:imprimirCondiciones(")
						.append(plan).append(",'")
						.append(especieRC).append("')").toString();
				html.a().href().quote().append(imprimir).quote().close();
				html.append("<img src=\"jsp/img/displaytag/imprimir_condiciones.png\" alt=\"Imprimir Poliza RC\" title=\"Imprimir Poliza RC\"/>");
				html.aEnd();
				html.append(ESPACIO_BLANCO_ESCAPADO);
				return html;
			}

			private HtmlBuilder botonImprimirSimulacion(BigDecimal polizaId) {
				HtmlBuilder html = new HtmlBuilder();
				String simular = new StringBuilder("javascript:simulacion(").append(polizaId).append(")").toString();
				html.a().href().quote().append(simular).quote().close();
				html.append("<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir Simulación Poliza RC\" title=\"Imprimir Simulación Poliza RC\"/>");
				html.aEnd();
				html.append(ESPACIO_BLANCO_ESCAPADO);
				return html;
			}

			private HtmlBuilder botonBorrar(BigDecimal polizaId) {
				HtmlBuilder html = new HtmlBuilder();
				String borrar = new StringBuilder("javascript:borrar(").append(polizaId).append(")").toString();
				html.a().href().quote().append(borrar).quote().close();
				html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Poliza RC\" title=\"Borrar Poliza RC\"/>");
				html.aEnd();
				html.append(ESPACIO_BLANCO_ESCAPADO);
				return html;
			}
		};
	}
	

	
	private CellEditor colectivoCellEditor(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				String refColectivo = (String)new BasicCellEditor().getValue(item, columnas.get(ConstantsRC.REF_COL_KEY), rowCount);
				String dcColectivo = (String)new BasicCellEditor().getValue(item, ConstantsRC.DC_COL_VAL, rowCount);
				return new StringBuilder(refColectivo).append("-").append(dcColectivo).toString();
			}
		};
	}
	
	private CellEditor polizaCellEditorHtml(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				String refPoliza = (String)new BasicCellEditor().getValue(item, columnas.get(ConstantsRC.REF_POLIZA_KEY), rowCount);
				BigDecimal dcPoliza = (BigDecimal)new BasicCellEditor().getValue(item, ConstantsRC.DC_POLIZA_VAL, rowCount);
				return refPoliza == null ? ESPACIO_BLANCO_ESCAPADO : String.format("%s-%s", refPoliza, dcPoliza.toString());
			}
		};
	}
	
	private CellEditor polizaCellEditorExcel(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				String refPoliza = (String)new BasicCellEditor().getValue(item, columnas.get(ConstantsRC.REF_POLIZA_KEY), rowCount);
				BigDecimal dcPoliza = (BigDecimal)new BasicCellEditor().getValue(item, ConstantsRC.DC_POLIZA_VAL, rowCount);
				return refPoliza == null ? STRING_VACIO : String.format("%s-%s", refPoliza, dcPoliza.toString());
			}
		};
	}
	
	private CellEditor estadoPolizaCellEditor(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				return (String)new BasicCellEditor().getValue(item, ConstantsRC.ABR_ESTADO_POL_VAL, rowCount);
			}
		};
	}
	
	private CellEditor abreviaturaEstadoRCCellEditor(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				String texto = (String)new BasicCellEditor().getValue(item, ConstantsRC.DES_ESTADO_RC_VAL, rowCount);
				String textoSinAbreviar = (String)new BasicCellEditor().getValue(item, "desestadorc", rowCount);
				return "<label title='"+ textoSinAbreviar +"'>" + texto + "</label>";
			}
		};
	}
	
	private CellEditor detalleErrorRCCellEditorExcel(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				String error = (String)new BasicCellEditor().getValue(item, ConstantsRC.DETALLE_VAL, rowCount);
				if(error == null){
					error = STRING_VACIO;
				}
				return error;
			}
		};
	}
	
	private CellEditor detalleErrorRCCellEditorHtml(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				String errorCorto = ESPACIO_BLANCO_ESCAPADO;
				String error = (String)new BasicCellEditor().getValue(item, ConstantsRC.DETALLE_VAL, rowCount);
				String resultado = ESPACIO_BLANCO_ESCAPADO;
				if(error != null && error.length() > 15 ){
					errorCorto = error.substring(0, 12).concat(PUNTOS_SUSPENSIVOS);
				}
				if(error != null && !errorCorto.isEmpty()){
					resultado = String.format("<span title=\"%s\">%s</span>", error, errorCorto);
				} else {
					resultado = errorCorto;
				}
				return resultado;
			}
		};
	}

	private CellEditor detalleNSolicitudCellEditorHtml(){
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowCount) {
				
				BigDecimal nsolicitud = (BigDecimal) new BasicCellEditor().getValue(item, ConstantsRC.ID_POLIZA_VAL, rowCount);
				return nsolicitud;
			}
		};
	}
	
	
	private void cabecerasColumnasHtml(final HtmlTable table) {
		configCabecera(table, this.columnas.get(ConstantsRC.ID_POLIZA_KEY), ACCIONES_HEADER, false, false, "9%");
		configCabecera(table, this.columnas.get(ConstantsRC.ENTIDAD_KEY), ENTIDAD_HEADER, true, true, "4%");
		configCabecera(table, this.columnas.get(ConstantsRC.OFICINA_KEY), OFICINA_HEADER, true, true, "4%");
		configCabecera(table, this.columnas.get(ConstantsRC.USUARIO_KEY), USUARIO_HEADER, true, true, "6%");
		configCabecera(table, this.columnas.get(ConstantsRC.PLAN_KEY), PLAN_HEADER, true, true, "5%");
		configCabecera(table, this.columnas.get(ConstantsRC.LINEA_KEY), LINEA_HEADER, true, true, "4%");
		configCabecera(table, this.columnas.get(ConstantsRC.REF_COL_KEY), COLECTIVO_HEADER, true, true, "9%");
		configCabecera(table, this.columnas.get(ConstantsRC.REF_POLIZA_KEY), POLIZA_HEADER, true, true, "8%");
		configCabecera(table, this.columnas.get(ConstantsRC.NIFCIF_KEY), NIFCIF_HEADER, true, true, "6%");
		configCabecera(table, this.columnas.get(ConstantsRC.CLASE_KEY), CLASE_HEADER, true, true, "3%");
		configCabecera(table, this.columnas.get(ConstantsRC.ESTADO_POL_KEY), ESTADO_POLIZA_HEADER, true, true, "3%");
		configCabecera(table, this.columnas.get(ConstantsRC.FEC_ENVIO_RC_KEY), FECHA_ENVIO_RC_HEADER, true, true, "6%");
		configCabecera(table, this.columnas.get(ConstantsRC.SUMA_ASEGURADA_KEY), SUMA_ASEGURDA_HEADER, true, true, "4%");
		configCabecera(table, this.columnas.get(ConstantsRC.IMPORTE_KEY), IMPORTE_HEADER, true, true, "5%");
		configCabecera(table, this.columnas.get(ConstantsRC.ESTADO_RC_KEY), ESTADO_RC_HEADER, true, true, "6%");
		configCabecera(table, this.columnas.get(ConstantsRC.ERROR_RC_KEY), DETALLE_HEADER, true, true, "12%");
		configCabecera(table, this.columnas.get(ConstantsRC.REF_OMEGA_KEY), OMEGA_HEADER, true, true, "6%");
		//configCabecera(table, this.columnas.get("prueba"), "N&deg; Solicitud", true, true, "6%");
		configCabecera(table, this.columnas.get(ConstantsRC.N_SOLICITUD_KEY), N_SOLICITUD_HEADER , true, true, "6%");
	}

	private void configCabecera(final HtmlTable table, final String idCol,
			final String title, final boolean filterable,
			final boolean sortable, final String width) {

			table.getRow().getColumn(idCol).setTitle(title);
			table.getRow().getColumn(idCol).setFilterable(filterable);
			table.getRow().getColumn(idCol).setSortable(sortable);
			table.getRow().getColumn(idCol).setWidth(width);
	}

	private String getlistaIdsTodos(final VistaPolizasRCFilter filter) throws DAOException {
		return this.listadoPolizasRCDao.getlistaIdsTodos(filter);
	}

	@Override
	public List<VistaPolizasRC> getVistaPolizasRCWithFilterAndSort(
			VistaPolizasRCFilter filter, VistaPolizasRCSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		try {
			return this.listadoPolizasRCDao.getVistaPolizasRCWithFilterAndSort(filter, sort, rowStart, rowEnd);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	@Override
	public int getVistaPolizasRCCountWithFilter(VistaPolizasRCFilter filter)
			throws BusinessException {
		try {
			return this.listadoPolizasRCDao.getVistaPolizasRCCountWithFilter(filter);
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error al obtener el numero de registros", e);
		}
	}
	
	@Override
	public List<EstadosRC> getEstadosRC() throws BusinessException{
		try {
			List<EstadosRC> estadosRC = new ArrayList<EstadosRC>();
			List<EstadosRC> estadosRCMayusculas = this.listadoPolizasRCDao.getEstadosRC();
			for(EstadosRC estado : estadosRCMayusculas){
				EstadosRC estadoRC = new EstadosRC();
				estadoRC.setId(estado.getId());
				estadosRC.add(estadoRC);
				String descripcion = WordUtils.capitalizeFully(estado.getDescripcion());
				estadoRC.setDescripcion(descripcion);
				estadoRC.setAbreviatura(estado.getAbreviatura());
			}
			return estadosRC;
		} catch (DAOException e) {
			throw new BusinessException("Se ha producido al obtener Estados RC: ", e);
		}
	}

	@Override
	public List<EstadoPoliza> getEstadoPoliza() throws BusinessException{
		try {
			return this.listadoPolizasRCDao.getEstadoPoliza();
		} catch (DAOException e) {
			throw new BusinessException("Se ha producido al obtener Estados Poliza: ", e);
		}
	}

	@Override
	public List<ErroresRC> getErroresRC() throws BusinessException{
		try {
			return this.listadoPolizasRCDao.getErroresRC();
		} catch (DAOException e) {
			throw new BusinessException("Se ha producido al obtener Errores RC: ", e);
		}
	}
	
	@Override
	public void borrarPoliza(final Long polizaId) throws BusinessException {
		this.listadoPolizasRCDao.removeObject(PolizasRC.class, polizaId);
	}

	@Override
	public void pasarDefinitiva(final BigDecimal polizaId, final String codUsuario) throws BusinessException {
		try {
			this.modificarPolizaRC(polizaId, codUsuario, POLIZARC_ESTADO_DEFINITIVA);
		} catch (DAOException e) {
			throw new BusinessException("Error al intentar pasar a definitiva la Poliza RC", e);
		}
	}

	@Override
	public void anular(final BigDecimal polizaId, final String codUsuario) throws BusinessException {
		try {
			this.modificarPolizaRC(polizaId, codUsuario, POLIZARC_ESTADO_ANULADA);
		} catch (DAOException e) {
			throw new BusinessException("Error al intentar anular la poliza RC", e);
		}
	}

	private void modificarPolizaRC(final BigDecimal polizaId,
			final String codUsuario, BigDecimal idEstadoRC) throws DAOException {	
		
		EstadosRC estadoRC = (EstadosRC) this.listadoPolizasRCDao.getObject(EstadosRC.class, idEstadoRC);
		PolizasRC poliza = (PolizasRC) this.listadoPolizasRCDao.getObject(PolizasRC.class, polizaId.longValue());
		
		poliza.setEstadosRC(estadoRC);
		
		PolizasHistEstadosRC historico = new PolizasHistEstadosRC();
		historico.setCodusuario(codUsuario);
		historico.setFecha(Calendar.getInstance().getTime());
		historico.setEstadosRC(estadoRC);
		historico.setPolizasRC(poliza);
		
		estadoRC.getPolizasHistEstadosRCs().add(historico);
		estadoRC.getPolizasRCs().add(poliza);
		poliza.getPolizasHistEstadosRCs().add(historico);
		
		this.listadoPolizasRCDao.saveOrUpdate(poliza);
		this.listadoPolizasRCDao.saveOrUpdate(historico);
		this.listadoPolizasRCDao.saveOrUpdate(estadoRC);
	}

	@Override
	public String cargarNombreEntidad(BigDecimal codEntidad) throws BusinessException {
		try {
			return this.listadoPolizasRCDao.getNombreEntidad(codEntidad);
		} catch (Exception e) {
			throw new BusinessException("Error al intentar obtener el nombre de la Entidad", e);
		}
	}

	@Override
	public String cargarNombreOficina(BigDecimal codOficina, BigDecimal codEntidad)	throws BusinessException {
		try {
			return this.listadoPolizasRCDao.getNombreOficina(codOficina, codEntidad);
		} catch (Exception e) {
			throw new BusinessException("Error al obtener el nombre de Oficina", e);
		}
	}
}
