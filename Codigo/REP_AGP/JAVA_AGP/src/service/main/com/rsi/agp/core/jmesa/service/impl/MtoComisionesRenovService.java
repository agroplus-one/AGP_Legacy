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
import org.jmesa.limit.ExportType;
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
import com.rsi.agp.core.jmesa.dao.IMtoComisionesRenovDao;
import com.rsi.agp.core.jmesa.dao.IPolizasRenovablesDao;
import com.rsi.agp.core.jmesa.filter.MtoComisionesRenovFilter;
import com.rsi.agp.core.jmesa.service.IMtoComisionesRenovService;
import com.rsi.agp.core.jmesa.sort.MtoComisionesRenovSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.ComisionesRenov;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculadoId;
import com.rsi.agp.dao.tables.poliza.Linea;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;

/**
 * @author U028975 (Tatiana, T-Systems) Petición: 57624 (Mantenimiento de
 *         Comisioens en Renovables por E-S Mediadora) Fecha:
 *         (Enero/Febrero.2019)
 */
@SuppressWarnings("deprecation")
public class MtoComisionesRenovService implements IMtoComisionesRenovService {

	private static final String MENSAJE_MTO_COMISIONES_RENOV_DAO_BORRAR_KO = "mensaje.mtoComisionesRenovDao.borrar.KO";
	private static final String ALERTA = "alerta";
	private static final String NBSP = "&nbsp;";
	private static final String REFIMPORTE2 = "refimporte";
	private static final String COMISION2 = "comision";
	private static final String CODMODULO2 = "codmodulo";
	private static final String IDGRUPO2 = "idgrupo";
	private static final String CODSUBMED = "codsubmed";
	private static final String CODENTMED = "codentmed";
	private static final String CODENTIDAD = "codentidad";
	private static final String CODLINEA2 = "codlinea";
	private static final String CODPLAN2 = "codplan";

	private final static String STYLE_R = "text-align: right";
	private final static String STYLE_C = "text-align: center";

	// Sin Datos de Entidad. Ponemos el valor -1 para no incumplir las restricciones
	// de base de datos
	public static final BigDecimal SIN_ENTIDAD = new BigDecimal(-1);

	private IMtoComisionesRenovDao mtoComisionesRenovDao;
	private IPolizasRenovablesDao polizasRenovablesDao;
	private ILineaDao lineaDao;

	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private Log logger = LogFactory.getLog(getClass());
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String idComi;

	// Constantes para los nombres de las columnas del listado
	private final static String ID = "ID";
	private final static String CODPLAN = "CODPLAN";
	private final static String CODLINEA = "CODLINEA";
	private final static String ENTIDAD = "ENTIDAD";
	private final static String ESMEDIADORA = "ESMEDIADORA";
	private final static String SUBENTMEDIADORA = "SUBENTMEDIADORA";
	private final static String IDGRUPO = "IDGRUPO";
	private final static String REFIMPORTE = "REFIMPORTE";
	private final static String IMP_DESDE = "impDesde";
	private final static String IMP_HASTA = "impHasta";
	private final static String COMISION = "COMISION";
	private final static String CODMODULO = "CODMODULO";

	// Titulos columnas del informe excel
	private static final String LISTADO_MANTENIMIENTO_COMISIONES = "LISTADO MANTENIMIENTO COMISIONES E_S MEDIADORA";
	private static final String TITL_COL_PLAN = "Plan";
	private static final String TITL_COL_LINEA = "Línea";
	private static final String TITL_COL_ES_MED = "E-S Med.";
	private static final String TITL_COL_GN = "G.N";
	private static final String TITL_COL_MODULO = "Módulo";
	private static final String TITL_COL_IMP_REFERENCIA = "Imp. Referencia";
	private static final String TITL_COL_DESDE = "Desde";
	private static final String TITL_COL_HASTA = "Hasta";
	private static final String TITL_COL_COMISION = "Comisión";

	@Override
	public String getTablaComisionesRenov(HttpServletRequest request, HttpServletResponse response,
			ComisionesRenov comisRenovBean, String origenLlamada, List<GruposNegocio> gruposNegocio) {

		Map<Character, String> mapGruposNegocio = new HashMap<Character, String>();
		for (GruposNegocio gr : gruposNegocio) {
			mapGruposNegocio.put(gr.getGrupoNegocio(), gr.getDescripcion());
		}

		TableFacade tableFacade = crearTableFacade(request, response, comisRenovBean, origenLlamada);

		Limit limit = tableFacade.getLimit();

		MtoComisionesRenovFilter consultaFilter = getConsultaComisRenovFilter(limit);

		setDataAndLimitVariables(tableFacade, consultaFilter, limit);

		// tableFacade.setToolbar(new CustomToolbarMarcarTodos());
		String ajax = request.getParameter("ajax");

		if (!"false".equals(ajax) && request.getParameter("export") == null) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}

		return html(request, tableFacade, gruposNegocio);

	}

	@Override
	public Collection<ComisionesRenov> getComisRenovWithFilterAndSort(MtoComisionesRenovFilter filter,
			MtoComisionesRenovSort sort, int rowStart, int rowEnd) throws BusinessException {

		return mtoComisionesRenovDao.getComisRenovWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla CREACIÓN:
	 * 24.01.2019 ** U028975 PETICIÓN: 57624 (Mantenimiento de Comisiones de
	 * Renovables por E-S Mediadora).
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request, HttpServletResponse response,
			ComisionesRenov comisRenov, String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(idComi, request);

		// Carga las columnas a mostrar en el listado en el TableFacade y devuelve un
		// Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setExportTypes(response, ExportType.EXCEL);

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("consultaComisRenov_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaComisRenov_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario
				cargarFiltrosBusqueda(columnas, comisRenov, tableFacade);
			}
		}

		return tableFacade;
	}

	/**
	 * CREACIÓN: 24.01.2019 ** U028975 PETICIÓN: 57624 (Mantenimiento de Comisiones
	 * de Renovables por E-S Mediadora).
	 * 
	 * @param tableFacade
	 * @return
	 */
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda
		// si no se ha hecho anteriormente

		/* Columnas de la lista */
		// busqueda si no se ha hecho anteriormente

		if (columnas.size() == 0) {
			columnas.put(ID, "id");
			columnas.put(CODPLAN, CODPLAN2);
			columnas.put(CODLINEA, CODLINEA2);
			columnas.put(ENTIDAD, CODENTIDAD);
			columnas.put(ESMEDIADORA, CODENTMED);
			columnas.put(SUBENTMEDIADORA, CODSUBMED);
			columnas.put(IDGRUPO, IDGRUPO2);
			columnas.put(CODMODULO, CODMODULO2);
			columnas.put(REFIMPORTE, REFIMPORTE2);
			columnas.put(IMP_DESDE, IMP_DESDE);
			columnas.put(IMP_HASTA, IMP_HASTA);
			columnas.put(COMISION, COMISION2);
		}
		/* columnas del filtro de búsqueda */
		tableFacade.setColumnProperties(columnas.get(ID), columnas.get(CODPLAN), columnas.get(CODLINEA),
				columnas.get(ESMEDIADORA), columnas.get(IDGRUPO), columnas.get(CODMODULO), columnas.get(REFIMPORTE),
				columnas.get(IMP_DESDE), columnas.get(IMP_HASTA), columnas.get(COMISION));

		return columnas;
	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * CREACIÓN: 24.01.2019 ** U028975 PETICIÓN: 57624 (Mantenimiento de Comisiones
	 * de Renovables por E-S Mediadora).
	 * 
	 * @param columnas2
	 * @param comisionesRenov
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas2, ComisionesRenov comisRenov,
			TableFacade tableFacade) {
		// CODPLAN
		if (FiltroUtils.noEstaVacio(comisRenov.getCodplan()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODPLAN), comisRenov.getCodplan().toString()));
		// CODLINEA
		if (FiltroUtils.noEstaVacio(comisRenov.getCodlinea()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODLINEA), comisRenov.getCodlinea().toString()));

		// CODENTIDAD
		if (FiltroUtils.noEstaVacio(comisRenov.getCodentidad()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(ENTIDAD), comisRenov.getCodentidad().toString()));

		// ENTIDAD MEDIADORA
		if (FiltroUtils.noEstaVacio(comisRenov.getCodentmed()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(ESMEDIADORA), comisRenov.getCodentmed().toString()));

		// SUBENTIDAD MEDIADORA
		if (FiltroUtils.noEstaVacio(comisRenov.getCodsubmed()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(SUBENTMEDIADORA), comisRenov.getCodsubmed().toString()));

		// GRUPO NEGOCIO
		if (FiltroUtils.noEstaVacio(comisRenov.getIdgrupo()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(IDGRUPO), comisRenov.getIdgrupo().toString()));

		// MODULO
		if (FiltroUtils.noEstaVacio(comisRenov.getCodmodulo()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODMODULO), comisRenov.getCodmodulo()));

		// REFERENCIA IMPORTE
		if (FiltroUtils.noEstaVacio(comisRenov.getRefimporte()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(REFIMPORTE), comisRenov.getRefimporte().toString()));

		// IMPORTE DESDE
		if (FiltroUtils.noEstaVacio(comisRenov.getimpDesde()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(IMP_DESDE), comisRenov.getimpDesde().toString()));

		// IMPORTE HASTA
		if (FiltroUtils.noEstaVacio(comisRenov.getimpHasta()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(IMP_HASTA), comisRenov.getimpHasta().toString()));

		// REFERENCIA IMPORTE
		if (FiltroUtils.noEstaVacio(comisRenov.getComision()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(COMISION), comisRenov.getComision().toString()));
	}

	/**
	 * Metodo para construir el html de la tabla a mostrar 06/05/2014 U029769
	 * 
	 * @param tableFacade
	 * @return
	 */

	private String html(HttpServletRequest request, TableFacade tableFacade, List<GruposNegocio> gruposNegocio) {

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {

			Table exportTable = tableFacade.getTable();
			// eliminamos la columna "Id"
			eliminarColumnaId(tableFacade, exportTable);
			
			// renombramos las cabeceras
			configurarCabecerasColumnasExport(exportTable);
						
			// configuramos cada columna
			configurarColumnasExport(request, exportTable);

			tableFacade.render();
			
			return null;

		} else {

			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");
			configurarColumnas(table);

			// Configuracion de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
			table.getRow().getColumn(columnas.get(ESMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorESMed(request));
			table.getRow().getColumn(columnas.get(COMISION)).getCellRenderer()
					.setCellEditor(getCellEditorPctComision());
			table.getRow().getColumn(columnas.get(REFIMPORTE)).getCellRenderer()
					.setCellEditor(getCellEditorRefImporte());
			table.getRow().getColumn(columnas.get(IMP_DESDE)).getCellRenderer()
					.setCellEditor(getCellEditorImporteDesde());
			table.getRow().getColumn(columnas.get(IMP_HASTA)).getCellRenderer()
					.setCellEditor(getCellEditorImporteHasta());
			table.getRow().getColumn(columnas.get(IDGRUPO)).getCellRenderer()
					.setCellEditor(getCellEditorGrupoNegocio(gruposNegocio));

		}

		return tableFacade.render();
	}

	private void configurarCabecerasColumnasExport(Table table) {

		table.setCaption(LISTADO_MANTENIMIENTO_COMISIONES);
		Row row = table.getRow();

		Column colPlan = row.getColumn(columnas.get(CODPLAN));
		colPlan.setTitle(TITL_COL_PLAN);

		Column colLinea = row.getColumn(columnas.get(CODLINEA));
		colLinea.setTitle(TITL_COL_LINEA);

		Column colEsMed = row.getColumn(columnas.get(ESMEDIADORA));
		colEsMed.setTitle(TITL_COL_ES_MED);

		Column colGN = row.getColumn(columnas.get(IDGRUPO));
		colGN.setTitle(TITL_COL_GN);

		Column colModulo = row.getColumn(columnas.get(CODMODULO));
		colModulo.setTitle(TITL_COL_MODULO);

		Column colImpReferencia = row.getColumn(columnas.get(REFIMPORTE));
		colImpReferencia.setTitle(TITL_COL_IMP_REFERENCIA);

		Column colDesde = row.getColumn(columnas.get(IMP_DESDE));
		colDesde.setTitle(TITL_COL_DESDE);

		Column colHasta = row.getColumn(columnas.get(IMP_HASTA));
		colHasta.setTitle(TITL_COL_HASTA);

		Column colComision = row.getColumn(columnas.get(COMISION));
		colComision.setTitle(TITL_COL_COMISION);
	}

	private void configurarColumnasExport(HttpServletRequest request, Table table) {

		// columna es/med
		table.getRow().getColumn(columnas.get(ESMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorESMed(request));
		
		// columna grupo
		table.getRow().getColumn(columnas.get(IDGRUPO)).getCellRenderer().setCellEditor(getCellEditorGrupoNegocio(this.polizasRenovablesDao.getGruposNegocio(false)));
		
		// columna importe referencia
		table.getRow().getColumn(columnas.get(REFIMPORTE)).getCellRenderer().setCellEditor(getCellEditorRefImporte());
		
		// columna importe desde
		table.getRow().getColumn(columnas.get(IMP_DESDE)).getCellRenderer().setCellEditor(getCellEditorImporteDesde());
		
		// columna importe hasta
		table.getRow().getColumn(columnas.get(IMP_HASTA)).getCellRenderer().setCellEditor(getCellEditorImporteHasta());
		
		// columna importe hasta
		table.getRow().getColumn(columnas.get(COMISION)).getCellRenderer().setCellEditor(getCellEditorPctComision());
	}

	/**
	 * metodo para eliminar la columna Id en los informes
	 * 
	 * @param tableFacade
	 * @param table
	 */
	private void eliminarColumnaId(TableFacade tableFacade, Table table) {
		Row row = table.getRow();
		Row rowFinal = new Row();
		List<Column> lstColumns = row.getColumns();
		for (Column col : lstColumns) {
			if (!col.getProperty().equals("id")) {
				rowFinal.addColumn(col);
			}
		}
		table.setRow(rowFinal);
		tableFacade.setTable(table);
	}

	private CellEditor getCellEditorAcciones() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();

				Long id = (Long) new BasicCellEditor().getValue(item, "id", rowcount);

				BigDecimal codPlan = (BigDecimal) new BasicCellEditor().getValue(item, CODPLAN2, rowcount);

				BigDecimal codLinea = (BigDecimal) new BasicCellEditor().getValue(item, CODLINEA2, rowcount);

				BigDecimal codEntidad = (BigDecimal) new BasicCellEditor().getValue(item, CODENTIDAD, rowcount);

				BigDecimal entMedia = (BigDecimal) new BasicCellEditor().getValue(item, CODENTMED, rowcount);

				BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor().getValue(item, CODSUBMED, rowcount);

				Character grupoNeg = (Character) new BasicCellEditor().getValue(item, IDGRUPO2, rowcount);

				String codModulo = (String) new BasicCellEditor().getValue(item, CODMODULO2, rowcount);

				Character refImporte = (Character) new BasicCellEditor().getValue(item, REFIMPORTE2, rowcount);

				BigDecimal impDesde = (BigDecimal) new BasicCellEditor().getValue(item, "impDesde", rowcount);

				BigDecimal impHasta = (BigDecimal) new BasicCellEditor().getValue(item, "impHasta", rowcount);

				BigDecimal porcComision = (BigDecimal) new BasicCellEditor().getValue(item, COMISION2, rowcount);

				String nomEntidad = getNombEntidad(codEntidad);

				String desc_linea = getDescLinea(codPlan, codLinea);

				StringBuilder funcion = new StringBuilder();
				funcion.append(id).append(",").append(codPlan != null ? codPlan : "''").append(",")
						.append(codLinea != null ? codLinea : "''").append(",")
						.append(codEntidad != null ? codEntidad : "''").append(",")
						.append(entMedia != null ? entMedia : "''").append(",")
						.append(subEntMedia != null ? subEntMedia : "''").append(",")
						.append(grupoNeg != null ? grupoNeg : "''").append(",")
						.append("'" + StringUtils.nullToString(codModulo) + "'").append(",")
						.append("'" + StringUtils.nullToString(refImporte) + "'").append(",")
						.append(impDesde != null ? impDesde : "''").append(",")
						.append(impHasta != null ? impHasta : "''").append(")");

				StringBuilder funcion_modif = new StringBuilder();
				funcion_modif.append(id).append(",").append(codPlan != null ? codPlan : "''").append(",")
						.append(codLinea != null ? codLinea : "''")
						.append(",'" + StringUtils.nullToString(desc_linea) + "'").append(",")
						.append(codEntidad != null ? codEntidad : "''")
						.append(",'" + StringUtils.nullToString(nomEntidad) + "'").append(",")
						.append(entMedia != null ? entMedia : "''").append(",")
						.append(subEntMedia != null ? subEntMedia : "''").append(",")
						.append(grupoNeg != null ? grupoNeg : "''").append(",")
						.append("'" + StringUtils.nullToString(codModulo) + "'").append(",")
						.append("'" + StringUtils.nullToString(refImporte) + "'").append(",")
						.append(impDesde != null ? impDesde : "''").append(",")
						.append(impHasta != null ? impHasta : "''").append(",")
						.append(porcComision != null ? porcComision : "''").append(")");

				StringBuilder funcionEdita = new StringBuilder();
				funcionEdita.append("javascript:modificar( ").append(funcion_modif);
				html.a().href().quote().append(funcionEdita).quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Comisi&oacute;n Renovaci&oacute;n\" title=\"Editar Comisi&oacute;n Renovaci&oacute;n\"/>");
				html.aEnd();
				html.append(NBSP);

				// boton borrar
				StringBuilder funcionBorra = new StringBuilder();
				funcionBorra.append("javascript:borrar( ").append(funcion);
				html.a().href().quote().append(funcionBorra).quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Comisi&oacute;n Renovaci&oacute;n\" title=\"Borrar Comisi&oacute;n Renovaci&oacute;n\"/>");
				html.aEnd();
				html.append(NBSP);

				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorESMed(final HttpServletRequest request) {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				/***/
				BigDecimal entMedia = (BigDecimal) new BasicCellEditor().getValue(item, CODENTMED, rowcount);
				BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor().getValue(item, CODSUBMED, rowcount);

				HtmlBuilder html = new HtmlBuilder();
				if (entMedia != null && subEntMedia != null) {
					html.append(StringUtils.nullToString(entMedia) + "-" + StringUtils.nullToString(subEntMedia));
				} else {
					html.append(request.getParameter("export") == null ? NBSP : "");
				}
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorRefImporte() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				Character refImporte = (Character) new BasicCellEditor().getValue(item, REFIMPORTE2, rowcount);
				String refImporteAux = "";
				if (refImporte == 'C') {
					refImporteAux = "Coste Tomador";
				} else if (refImporte == 'P') {
					refImporteAux = "Prima Comercial";
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(refImporteAux);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorPctComision() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal pctComision = (BigDecimal) new BasicCellEditor().getValue(item, COMISION2, rowcount);

				String pct;

				if (pctComision.scale() == 0) {
					pct = pctComision.setScale(0, BigDecimal.ROUND_CEILING) + "%";
				} else {
					pct = pctComision.setScale(2, BigDecimal.ROUND_CEILING) + "%";
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(pct);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorImporteDesde() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal pctImporte = (BigDecimal) new BasicCellEditor().getValue(item, "impDesde", rowcount);
				
				String imp = pctImporte.setScale(0, BigDecimal.ROUND_DOWN) + " ";
				HtmlBuilder html = new HtmlBuilder();
				html.append(imp);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorImporteHasta() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal pctImporte = (BigDecimal) new BasicCellEditor().getValue(item, "impHasta", rowcount);

				String imp = pctImporte.setScale(0, BigDecimal.ROUND_DOWN) + " ";
				HtmlBuilder html = new HtmlBuilder();
				html.append(imp);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'GRUPONEGOCIO'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorGrupoNegocio(final List<GruposNegocio> gruposNegocio) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				Character idGrupo = (Character) new BasicCellEditor().getValue(item, IDGRUPO2, rowcount);

				String descr_Grupo = "";

				for (GruposNegocio grupoNeg : gruposNegocio) {
					if (grupoNeg.getGrupoNegocio().equals(idGrupo)) {
						descr_Grupo = grupoNeg.getDescripcion();
					}
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(descr_Grupo);
				return html.toString();
			}
		};
	}

	/**
	 * Configuracion de las columnas de la tabla 08/05/2014 U029769
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false, "10%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(CODPLAN), "Plan", false, false, "5%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(CODLINEA), "L&iacute;nea", true, true, "10%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(ESMEDIADORA), "E-S Med.", true, true, "10%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(IDGRUPO), "G.N", true, true, "10%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(CODMODULO), "M&oacute;dulo", true, true, "10%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(REFIMPORTE), "Imp. Referencia", true, true, "15%", STYLE_C, STYLE_C);
		configColumna(table, columnas.get(IMP_DESDE), "Desde", true, true, "10%", STYLE_R, STYLE_C);
		configColumna(table, columnas.get(IMP_HASTA), "Hasta", true, true, "10%", STYLE_R, STYLE_C);
		configColumna(table, columnas.get(COMISION), "Comisi&oacute;n", true, true, "10%", STYLE_R, STYLE_C);
	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla CREACIÓN: 25.01.2019 --
	 * USUARIO: U028975 (TAM)
	 * 
	 * @param table
	 * @param idCol
	 * @param title
	 * @param filterable
	 * @param sortable
	 * @param width
	 */
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width, String style, String headerStyle) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
		table.getRow().getColumn(idCol).setStyle(style);
		table.getRow().getColumn(idCol).setHeaderStyle(headerStyle);

	}

	@Override
	public List<GruposNegocio> getGruposNegocio() {
		return this.polizasRenovablesDao.getGruposNegocio(false);
	}

	@Override
	public String getDescLinea(BigDecimal codplan, BigDecimal codlinea) {
		return this.mtoComisionesRenovDao.getDescLinea(codlinea, codplan);
	}

	@Override
	public String getNombEntidad(BigDecimal codEntidad) {
		return this.mtoComisionesRenovDao.getNombEntidad(codEntidad);
	}

	@Override
	public Map<String, Object> validaAltaModificacion(ComisionesRenov comisRenovBean) throws Exception {
		logger.debug("ValidaAltaModificacion (init)- MtoComisionesRenovService");

		boolean existeComis = false;

		Map<String, Object> errores = new HashMap<String, Object>();
		try {
			// comprobamos si ya existe el registro en Comisiones de Renovables por E-S
			// Mediadora
			boolean valEntidad = comisRenovBean.getCodentidad() != null;

			existeComis = mtoComisionesRenovDao.existeComisionesRenov(comisRenovBean, valEntidad);

			if (existeComis) {
				errores.put(ALERTA, bundle.getString("mensaje.mtoComisionesRenovDao.existeRegistro"));
			} else {
				if (mtoComisionesRenovDao.validarRangoImporte(comisRenovBean, valEntidad)) {
					errores.put(ALERTA, bundle.getString("mensaje.mtoComisionesRenovDao.RangoImporteKO"));
				}

			}
		} catch (Exception e) {
			logger.error("Ocurrio un error al validar el registro " + e);
			throw e;
		}
		return errores;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<Integer> guardaComisRenov(ComisionesRenov comisRenovBean, Usuario usuario, int altaModif)
			throws Exception {

		ArrayList<Integer> error = null;

		try {

			error = comprobarDatosGrabacion(comisRenovBean, usuario, altaModif);
			if (error.size() == 0) {
				LineasFiltro filtroLinea = new LineasFiltro(comisRenovBean.getCodplan(), comisRenovBean.getCodlinea());
				List lineas = mtoComisionesRenovDao.getObjects(filtroLinea);

				Linea linea = (Linea) lineas.get(0);
				comisRenovBean.setCodlinea(linea.getCodlinea());
				comisRenovBean.setCodplan(linea.getCodplan());
				//
				comisRenovBean.setFechaModif(new Date());

				mtoComisionesRenovDao.saveOrUpdate(comisRenovBean);
				error.add(new Integer(0));
			}
		} catch (Exception e) {

			if (error != null)
				error.add(new Integer(9));
			logger.error("Se ha producido un error durante el guardado de la Comisión ", e);

			logger.error("Ocurrio un error al guardar el Registro de Comisión " + e);
			throw e;
		}
		return error;
	}

	@SuppressWarnings("finally")
	private ArrayList<Integer> comprobarDatosGrabacion(final ComisionesRenov comisRenovBean, Usuario usuario,
			int altaModif) {
		logger.debug("**@@** MtoComisionesRenovService - ComprobarDatosGrabacion");
		ArrayList<Integer> error = new ArrayList<Integer>();

		try {

			// Comprobamos la entidad
			if (!usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR)
					&& !usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)) {
				if (usuario.getOficina().getEntidad().getCodentidad().intValue() != comisRenovBean.getCodentidad()
						.intValue()) {
					error.add(new Integer(2));
				}
			}
			int count;
			// comprobamos que la subentidad exista y que no este dada de baja, en el caso
			// de que vaya informada.
			if (comisRenovBean.getCodentmed() != null && comisRenovBean.getCodentidad() != null) {
				count = mtoComisionesRenovDao.validarEntidad(comisRenovBean.getCodentmed(),
						comisRenovBean.getCodsubmed(), comisRenovBean.getCodentidad());
				if (count == 0) {
					error.add(new Integer(13));
				}
			}

			// Validamos el plan
			Linea lin = mtoComisionesRenovDao.getLinea(comisRenovBean.getCodlinea(), comisRenovBean.getCodplan());
			// El plan-linea no existiria
			if (null == lin) {
				error.add(new Integer(4));
			} else {
				// Comprobamos si el plan linea esta activo
				// Solo se comprueba si estamos dando de alta
				if (altaModif == 0) {
					if (lin.getActivo().equalsIgnoreCase("NO")) {
						error.add(new Integer(5));
					}
				}
			}

			// Validamos el Grupo de Negocio
			if (!validagrupoNegocio(comisRenovBean)) {
				error.add(new Integer(9));
			}

			// Validamos el Modulo
			if (!validaModulo(comisRenovBean)) {
				error.add(new Integer(10));
			}
		} catch (DAOException e) {
			logger.error("Error al acceder a bbdd en  comprobarDatosGrabacion () - MtoComisionesRenovService", e);
			error.add(new Integer(20));
		} catch (Exception ex) {
			logger.error("Error genérico al comprobarDatosGrabacion () - MtoComisionesRenovService", ex);
			error.add(new Integer(20));
		} finally {
			return error;
		}

	}

	private Boolean validaModulo(ComisionesRenov comisRenovBean) throws BusinessException {
		// las validaciones de obligatoriedad del campo se realizan en la jsp
		Boolean res = false;
		try {
			logger.debug("Validación del modulo del registro. ");
			Linea linea = lineaDao.getLinea(comisRenovBean.getCodlinea(), comisRenovBean.getCodplan());
			if (linea != null) {
				ModuloId id = new ModuloId(linea.getLineaseguroid(), comisRenovBean.getCodmodulo());
				Modulo modulo = (Modulo) this.lineaDao.get(Modulo.class, id);
				res = modulo != null;
			}
		} catch (DAOException e) {
			logger.error(
					"Error al acceder a bbdd en  comprobarDatosGrabacion (validaModulo) - MtoComisionesRenovService",
					e);
		} catch (Exception e) {
			logger.debug("Error validando el grupo de negocio - MtoComisionesRenovService.validaModulo", e);
		}
		return res;
	}

	private Boolean validagrupoNegocio(ComisionesRenov comisRenovBean) throws BusinessException {
		// las validaciones de obligatoriedad del campo se realizan en la jsp
		Boolean res = false;
		try {
			logger.debug("Validación del grupo de negocio del registro. ");
			Linea linea = lineaDao.getLinea(comisRenovBean.getCodlinea(), comisRenovBean.getCodplan());
			if (linea != null) {
				if (linea.isLineaGanado() || Constants.GRUPO_NEGOCIO_VIDA.equals(comisRenovBean.getIdgrupo())) {
					res = true;
				}
			}
		} catch (DAOException e) {
			logger.error(
					"Error al acceder a bbdd en  comprobarDatosGrabacion (validagrupoNegocio) - MtoComisionesRenovService",
					e);

		} catch (Exception e) {
			logger.debug("Error validando el grupo de negocio - MtoComisionesRenovService.validagrupoNegocio", e);
		}
		return res;
	}

	/**
	 * Crea y configura el Filter para la consulta de usuarios 25-01-2019 - U028975
	 * 
	 * @param limit
	 * @return
	 */
	private MtoComisionesRenovFilter getConsultaComisRenovFilter(Limit limit) {
		MtoComisionesRenovFilter consultaFilter = new MtoComisionesRenovFilter();
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
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos
	 * de usuarios y carga el TableFacade con ellas 25-01-2019 - U028975
	 * 
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, MtoComisionesRenovFilter consultaFilter,
			Limit limit) {

		Collection<ComisionesRenov> items = new ArrayList<ComisionesRenov>();

		try {
			int totalRows = getComisionesRenovCountWithFilter(consultaFilter);
			logger.debug("********** count filas  = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			MtoComisionesRenovSort consultaSort = getConsultaComisRenovSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			if (tableFacade.getLimit().getSortSet().getSort(CODPLAN2) == null) {
				consultaSort.addSort(CODPLAN2, "DESC");
			}
			if (tableFacade.getLimit().getSortSet().getSort(CODLINEA2) == null) {
				consultaSort.addSort(CODLINEA2, "DESC");
			}

			/*
			 * Si tenemos filtro de orden por entidad mediadora, añadimos tambien el filtro
			 * para subentidad mediadora
			 */
			if (tableFacade.getLimit().getSortSet().getSort(CODENTMED) != null) {
				limit.getSortSet().addSort(tableFacade.getLimit().getSortSet().getSort(CODENTMED).getPosition() + 1,
						CODSUBMED, tableFacade.getLimit().getSortSet().getSort(CODENTMED).getOrder());

			}

			items = getComisRenovWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}

		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	/**
	 * Crea y configura el Sort para la consulta de usuarios 06/05/2014 U029769
	 * 
	 * @param limit
	 * @return
	 */
	private MtoComisionesRenovSort getConsultaComisRenovSort(Limit limit) {
		MtoComisionesRenovSort consultaSort = new MtoComisionesRenovSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}

		return consultaSort;
	}

	@Override
	public int getComisionesRenovCountWithFilter(MtoComisionesRenovFilter filter) throws BusinessException {

		return mtoComisionesRenovDao.getComisionesRenovCountWithFilter(filter);
	}

	@Override
	public Map<String, Object> borraComisionRenov(ComisionesRenov comisRenovBean) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {

			// Borramos la comisión correspondiente
			mtoComisionesRenovDao.delete(comisRenovBean);
			parameters.put("mensaje", bundle.getString("mensaje.mtoComisionesRenovDao.borrar.OK"));

			if (parameters.get(ALERTA) != null) {
				parameters.put(ALERTA,
						bundle.getString(MENSAJE_MTO_COMISIONES_RENOV_DAO_BORRAR_KO) + " " + parameters.get(ALERTA));
			}

		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al borrar la Comisión ", ex);
			parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_COMISIONES_RENOV_DAO_BORRAR_KO));
		} catch (Exception ex) {
			logger.debug("Error al borrar la Comisión", ex);
			parameters.put(ALERTA, bundle.getString(MENSAJE_MTO_COMISIONES_RENOV_DAO_BORRAR_KO));
		}
		return parameters;
	}

	public ComisionesRenov cargarFiltroBusqueda(HttpServletRequest request, ComisionesRenov comisRenovBean) {

		ComisionesRenov comisRenov = new ComisionesRenov();

		String plan_filtro = request.getParameter("codPlanBorrar");
		String linea_filtro = request.getParameter("codLineaBorrar");
		String entidad_filtro = request.getParameter("entidadBorrar");
		String entMed_filtro = request.getParameter("entMedBorrar");
		String subEntMed_filtro = request.getParameter("subEntMedBorrar");
		String idGrupo_filtro = request.getParameter("idGrupoBorrar");
		String refImporte_filtro = request.getParameter("refImporteBorrar");
		String impDesde_filtro = request.getParameter("impDesdeBorrar");
		String impHasta_filtro = request.getParameter("impHastaBorrar");
		String comision_filtro = request.getParameter("comisionBorrar");

		if (plan_filtro != null && !plan_filtro.equals(""))
			comisRenov.setCodplan(new BigDecimal(plan_filtro));

		if (linea_filtro != null && !linea_filtro.equals(""))
			comisRenov.setCodlinea(new BigDecimal(linea_filtro));

		if (entidad_filtro != null && !entidad_filtro.equals(""))
			comisRenov.setCodentidad(new BigDecimal(entidad_filtro));

		if (entMed_filtro != null && !entMed_filtro.equals(""))
			comisRenov.setCodentmed(new BigDecimal(entMed_filtro));

		if (subEntMed_filtro != null && !subEntMed_filtro.equals(""))
			comisRenov.setCodsubmed(new BigDecimal(subEntMed_filtro));

		if (idGrupo_filtro != null && !idGrupo_filtro.equals(""))
			comisRenov.setIdgrupo((idGrupo_filtro.charAt(0)));

		if (refImporte_filtro != null && !refImporte_filtro.equals(""))
			comisRenov.setRefimporte((refImporte_filtro.charAt(0)));

		if (impDesde_filtro != null && !impDesde_filtro.equals(""))
			comisRenov.setimpDesde(new BigDecimal(impDesde_filtro));

		if (impHasta_filtro != null && !impHasta_filtro.equals(""))
			comisRenov.setimpHasta(new BigDecimal(impHasta_filtro));

		if (comision_filtro != null && !comision_filtro.equals(""))
			comisRenov.setComision(new BigDecimal(comision_filtro));

		return comisRenov;
	}

	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest,
			BigDecimal lineaDest, String codUsuario) throws BusinessException {

		Map<String, Object> parameters = new HashMap<String, Object>();

		// Obtiene el lineaseguroid de los plan/linea origen y destino
		try {
			// Validación del plan/linea origen
			Long lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(lineaOrig, planOrig);
			if (lineaSeguroIdOrigen == null) {
				logger.debug("El plan/línea origen no existe, no se continua con la réplica");
				parameters.put(ALERTA, bundle.getString("mensaje.mtoComisionesRenov.replica.planlinea.origen.KO"));
				return parameters;
			}

			// Validación del plan/linea destino
			Long lineaSeguroIdDestino = lineaDao.getLineaSeguroId(lineaDest, planDest);
			if (lineaSeguroIdDestino == null) {
				logger.debug("El plan/línea destino no existe, no se continua con la réplica");
				parameters.put(ALERTA, bundle.getString("mensaje.mtoComisionesRenov.replica.planlinea.destino.KO"));
				return parameters;
			}

			// Valida que el plan/linea destino no tenga comisiones dadas de alta
			// previamente
			MtoComisionesRenovFilter filter = new MtoComisionesRenovFilter();
			filter.addFilter(CODPLAN2, planDest);
			filter.addFilter(CODLINEA2, lineaDest);

			if (getComisionesRenovCountWithFilter(filter) != 0) {
				logger.debug(
						"El plan/línea destino de la entidad seleccionada tiene descuentos dados de alta, no se continua con la réplica");
				parameters.put(ALERTA, bundle.getString("mensaje.mtoComisionesRenov.replica.planlinea.KO"));
				return parameters;
			}

			// Llamada al mÃ©todo del DAO que realiza la rÃ©plica
			mtoComisionesRenovDao.replicarComisRenov(planOrig, lineaOrig, planDest, lineaDest, codUsuario);

			// Comprobamos si despues de la replica se ha realizado la misma y se han dado
			// de alta los registros nuevos
			// puede haber no realizado la replica por que el Grupo de Negocio para el
			// Plan/Linea destino no pasa la validación
			// correspondiente.
			if (getComisionesRenovCountWithFilter(filter) == 0) {
				logger.debug(
						"No se ha realizado la replica correctamente, ya que no se ha pasado la Validación del Grupo de Negocio");
				return parameters;
			}
		} catch (DAOException e) {
			logger.error("Ocurrió un error al replicar los descuentos", e);
			parameters.put(ALERTA, bundle.getString("mensaje.mtoComisionesRenov.replica.KO"));
			return parameters;
		}

		// Si llega hasta aquÃ­, el proceso de rÃ©plica ha finalizado correctamente
		logger.debug("El proceso de réplica ha finalizado correctamente");
		parameters.put("mensaje", bundle.getString("mensaje.mtoComisionesRenov.replica.OK"));
		return parameters;
	}

	@Override
	public Map<Character, ComsPctCalculado> getComisRenovParaCalculo(final BigDecimal codplan,
			final BigDecimal codlinea, final String codModulo, final Long idComparativa, final BigDecimal codEntidad,
			final BigDecimal codEntMed, final BigDecimal codSubEntMed, final CosteGrupoNegocio[] cgnArr)
			throws BusinessException {
		Map<Character, ComsPctCalculado> comsPctCalculo = new HashMap<Character, ComsPctCalculado>(cgnArr.length);
		logger.debug("[INIT] getComisRenovParaCalculo");
		try {
			logger.debug("Obteniendo comisiones por E-S Med con...");
			logger.debug("codplan:       " + codplan);
			logger.debug("codlinea:      " + codlinea);
			logger.debug("codModulo:     " + codModulo);
			logger.debug("idComparativa: " + idComparativa);
			logger.debug("codEntidad:    " + codEntidad);
			logger.debug("codEntMed:     " + codEntMed);
			logger.debug("codSubEntMed:  " + codSubEntMed);
			for (CosteGrupoNegocio cgn : cgnArr) {
				Character gn = cgn.getGrupoNegocio().charAt(0);
				logger.debug("Obteniendo comision para el grupo " + gn);
				// Obtenemos los parámetros de comisión por E-S Med
				ComisionesRenov predicate = new ComisionesRenov();
				predicate.setCodplan(codplan);
				predicate.setCodlinea(codlinea);
				predicate.setCodmodulo(codModulo);
				predicate.setIdgrupo(gn);
				predicate.setCodentidad(codEntidad);
				predicate.setCodentmed(codEntMed);
				predicate.setCodsubmed(codSubEntMed);
				// OBTENEMOS EL % CALCULADO ANTERIOR (SI LO HUBIERA)
				ComsPctCalculadoId comisCalcId = new ComsPctCalculadoId();
				comisCalcId.setIdGrupo(gn);
				comisCalcId.setIdComparativa(idComparativa);
				ComsPctCalculado comisCalc = (ComsPctCalculado) this.mtoComisionesRenovDao.get(ComsPctCalculado.class,
						comisCalcId);
				// SI EXISTIERA
				if (comisCalc != null) {
					// LO BORRAMOS... SE RECREA MAS ADELANTE SI APLICA
					this.mtoComisionesRenovDao.delete(comisCalc);
				}
				// OBTENEMOS EL % PARAMETRIZADO
				List<ComisionesRenov> comisParamLst = this.mtoComisionesRenovDao.getComisRenovParaCalculo(predicate);
				if (comisParamLst != null && !comisParamLst.isEmpty()) {
					for (ComisionesRenov comisParam : comisParamLst) {
						if (aplicaTramo(cgn, comisParam.getRefimporte(), comisParam.getimpDesde(),
								comisParam.getimpHasta())) {
							logger.debug("Encontrada comision del  " + comisParam.getComision() + "%");
							comisCalc = new ComsPctCalculado();
							comisCalc.setId(comisCalcId);
							// ACTUALIZAMOS EL VALOR Y GUARDAMOS
							comisCalc.setPctCalculado(comisParam.getComision());
							this.mtoComisionesRenovDao.saveOrUpdate(comisCalc);
							comsPctCalculo.put(gn, comisCalc);
							break;
						}
					}
				}
			}
		} catch (DAOException e) {
			logger.error("Ocurrió un error al obtener la comision", e);
			throw new BusinessException(e);
		}
		logger.debug("[END] getComisRenovParaCalculo");
		return comsPctCalculo;
	}

	private boolean aplicaTramo(final CosteGrupoNegocio cgn, final Character refImporte, final BigDecimal impDesde,
			final BigDecimal impHasta) {
		boolean result = false;
		BigDecimal importeReferencia;
		switch (refImporte) {
		case 'C':
			importeReferencia = cgn.getCosteTomador();
			logger.debug("Verificando tramo de comision sobre Coste Tomador");
			break;
		case 'P':
			importeReferencia = cgn.getPrimaComercial();
			logger.debug("Verificando tramo de comision sobre Prima Comercial");
			break;
		default:
			importeReferencia = null;
			logger.debug("No se encuentra el importe de referencia");
			break;
		}
		if (importeReferencia != null) {
			logger.debug("Verificando importe " + importeReferencia.toString() + " entre valores " + impDesde.toString()
					+ " y " + impHasta.toString());
			result = (importeReferencia.compareTo(impDesde) >= 0 && importeReferencia.compareTo(impHasta) <= 0);
		}
		return result;
	}
	public List<ComisionesRenov> getComisionesRenovList(Limit limit) {

		List<ComisionesRenov> items = new ArrayList<ComisionesRenov>();

		try {
	    	MtoComisionesRenovFilter consultaFilter = getConsultaComisRenovFilter(limit);
			int totalRows = getComisionesRenovCountWithFilter(consultaFilter);
			logger.debug("********** count filas  = " + totalRows + " **********");

			
			MtoComisionesRenovSort consultaSort = getConsultaComisRenovSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getTotalRows();

			if (limit.getSortSet().getSort(CODPLAN2) == null) {
				consultaSort.addSort(CODPLAN2, "DESC");
			}
			if (limit.getSortSet().getSort(CODLINEA2) == null) {
				consultaSort.addSort(CODLINEA2, "DESC");
			}

			items = (List<ComisionesRenov>) getComisRenovWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			
			//Rellenamos descripcion grupo de negocio -INIT
			List<GruposNegocio> gruposNegocio = getGruposNegocio();
			
	        // Convertir a Map<Character, String>
	        Map<Character, String> mapGruposNegocio = new HashMap<>();
	        for (GruposNegocio grupo : gruposNegocio) {
	        	mapGruposNegocio.put(grupo.getGrupoNegocio(), grupo.getDescripcion());
	        }
	        
	        
			for (ComisionesRenov comisionesRenov : items) {
				comisionesRenov.setNombreEntidad(mapGruposNegocio.get(comisionesRenov.getIdgrupo()));
			}
			//Rellenamos descripcion grupo de negocio -END
			logger.debug("********** list items = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}

		// Carga los registros obtenidos del bd en la tabla
		return items;
	}

	public void setMtoComisionesRenovDao(IMtoComisionesRenovDao mtoComisionesRenovDao) {
		this.mtoComisionesRenovDao = mtoComisionesRenovDao;
	}

	public void setId(String id) {
		this.idComi = id;
	}

	public void setPolizasRenovablesDao(IPolizasRenovablesDao polizasRenovablesDao) {
		this.polizasRenovablesDao = polizasRenovablesDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}