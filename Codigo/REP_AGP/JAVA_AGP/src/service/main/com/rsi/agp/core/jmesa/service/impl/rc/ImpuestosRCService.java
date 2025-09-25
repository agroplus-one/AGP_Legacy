package com.rsi.agp.core.jmesa.service.impl.rc;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.ImpuestosRCFilter;
import com.rsi.agp.core.jmesa.service.IImpuestosRCService;
import com.rsi.agp.core.jmesa.sort.ImpuestosRCSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.dao.models.rc.IImpuestosRCDao;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;
import com.rsi.agp.dao.tables.sbp.BaseSbp;
import com.rsi.agp.dao.tables.sbp.ImpuestoSbp;

@SuppressWarnings("deprecation")
public class ImpuestosRCService implements IImpuestosRCService {
	
	private static final Log LOGGER = LogFactory.getLog(ImpuestosRCService.class);
	
	private static final String BASE_HEADER = "Base";
	private static final String VALOR_HEADER = "Valor";
	private static final String NOM_IMPUESTO_HEADER = "Nom. Impuesto";
	private static final String COD_IMPUESTO_HEADER = "Cod. Impuesto";
	private static final String ID_HEADER = "&nbsp;&nbsp;Acciones";
	private static final String PLAN_HEADER = "Plan";

	private HashMap<String, String> columnas = new HashMap<String, String>();

	private IImpuestosRCDao impuestosRCDao;
	private String id;
	
	@Override
	public String getTablaImpuestos(HttpServletRequest req,
			ImpuestosRC impuestosVista, String origenLlamada)
			throws BusinessException {
		try {
			TableFacade tableFacade = this.crearTableFacade(req, impuestosVista, origenLlamada);
			Limit limit = tableFacade.getLimit();
			ImpuestosRCFilter filter = this.getConsultaImpuestosRCFilter(limit);
			this.setDataAndLimitVariables(tableFacade, filter, limit);
			StringBuilder script = this.getlistaIdsTodos(filter);
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
			return new StringBuilder(html(tableFacade)).append(script).toString();
		} catch (Exception e) {
			LOGGER.error("getTablaDatosRC error. " + e);
			throw new BusinessException("Se ha producido al obtener getTablaImpuestos: ", e);
		}
	}

	private TableFacade crearTableFacade(final HttpServletRequest req, final ImpuestosRC impuestosRC, final String origenLlamada) {
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(this.id, req);
		Map<String, String> columnas = cargarColumnas(tableFacade);
		tableFacade.setStateAttr("restore");

		if (req.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (req.getSession().getAttribute("listaImpuestosRC_LIMIT") != null) {
					tableFacade.setLimit((Limit) req.getSession().getAttribute("listaImpuestosRC_LIMIT"));
				}
			} else {
				cargarFiltrosBusqueda(columnas, impuestosRC, tableFacade);
			}
		}
		return tableFacade;
	}

	private Map<String, String> cargarColumnas(final TableFacade tableFacade) {
		if (this.columnas.isEmpty()) {
			this.columnas.put(ConstantsRC.ID_KEY, ConstantsRC.ID_VALUE);
			this.columnas.put(ConstantsRC.PLAN_KEY, ConstantsRC.PLAN_VALUE);
			this.columnas.put(ConstantsRC.COD_IMPUESTO_KEY, ConstantsRC.COD_IMPUESTO_VALUE);
			this.columnas.put(ConstantsRC.NOM_IMPUESTO_KEY, ConstantsRC.NOM_IMPUESTO_VALUE);
			this.columnas.put(ConstantsRC.VALOR_KEY, ConstantsRC.VALOR_VALUE);
			this.columnas.put(ConstantsRC.BASE_KEY, ConstantsRC.BASE_VALUE);
		}
		tableFacade.setColumnProperties(this.columnas.get(ConstantsRC.ID_KEY),
				this.columnas.get(ConstantsRC.PLAN_KEY),
				this.columnas.get(ConstantsRC.COD_IMPUESTO_KEY),
				this.columnas.get(ConstantsRC.NOM_IMPUESTO_KEY),
				this.columnas.get(ConstantsRC.VALOR_KEY), this.columnas.get(ConstantsRC.BASE_KEY));
		return this.columnas;
	}

	private void cargarFiltrosBusqueda(final Map<String, String> columnas,
			final ImpuestosRC impuestosRC, final TableFacade tableFacade) {
		if(impuestosRC.getCodPlan() != null) {
			this.crearFiltro(tableFacade, ConstantsRC.PLAN_VALUE, impuestosRC.getCodPlan().toString());
		}
		if(impuestosRC.getBaseSbp() != null && impuestosRC.getBaseSbp().getBase() != null) {
			this.crearFiltro(tableFacade, ConstantsRC.BASE_VALUE, impuestosRC.getBaseSbp().getBase());
		}
		if(impuestosRC.getImpuestoSbp() != null) {
			ImpuestoSbp impuestoSbp = impuestosRC.getImpuestoSbp();
			if(impuestoSbp.getCodigo() != null) {
				this.crearFiltro(tableFacade, ConstantsRC.COD_IMPUESTO_VALUE, impuestoSbp.getCodigo());
			}
			if(impuestoSbp.getDescripcion() != null) {
				this.crearFiltro(tableFacade, ConstantsRC.NOM_IMPUESTO_VALUE, impuestoSbp.getDescripcion());
			}
		}
		if(impuestosRC.getValor() != null) {
			this.crearFiltro(tableFacade, ConstantsRC.VALOR_VALUE, impuestosRC.getValor().toString());
		}
	}
	
	private void crearFiltro(TableFacade tableFacade, String property, String value){
		tableFacade.getLimit().getFilterSet().addFilter(new Filter(property, value));
	}

	private ImpuestosRCFilter getConsultaImpuestosRCFilter(final Limit limit) {
		ImpuestosRCFilter impuestosFilter = new ImpuestosRCFilter();
		Collection<Filter> filters = limit.getFilterSet().getFilters();
		for (Filter filter : filters) {
			impuestosFilter.addFilter(filter.getProperty(), filter.getValue());
		}
		return impuestosFilter;
	}

	private void setDataAndLimitVariables(final TableFacade tableFacade,
			final ImpuestosRCFilter filter, final Limit limit) {

		List<ImpuestosRC> items = new ArrayList<ImpuestosRC>();
		try {
			int totalRows = this.getImpuestoRCCountWithFilter(filter);
			LOGGER.debug(new StringBuilder(
					"********** count filas para DatosRC = ").append(totalRows)
					.append(" **********").toString());

			tableFacade.setTotalRows(totalRows);

			ImpuestosRCSort sort = this.getConsultaImpuestosRCSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = this.getImpuestosRCWithFilterAndSort(filter, sort,
					rowStart, rowEnd);
			LOGGER.debug(new StringBuilder(
					"********** list items para DatosRC = ")
					.append(items.size()).append(" **********").toString());

		} catch (BusinessException e) {
			LOGGER.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	private ImpuestosRCSort getConsultaImpuestosRCSort(final Limit limit) {
		ImpuestosRCSort consultaSort = new ImpuestosRCSort();
		Collection<Sort> sorts = limit.getSortSet().getSorts();
		for (Sort sort : sorts) {
			consultaSort.addSort(sort.getProperty(), sort.getOrder().toParam());
		}
		return consultaSort;
	}

	private String html(final TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render();
			return null;
		} else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");
			this.configurarColumnas(table);
			this.editarContenidoColumnas(table);
			return tableFacade.render();
		}
	}

	private void editarContenidoColumnas(HtmlTable table) {
		this.editarContenidoCelda(table, this.valorCellEditor(), columnas.get(ConstantsRC.ID_KEY));
		this.editarContenidoCelda(table, this.accionesCellEditor(), columnas.get(ConstantsRC.ID_KEY));
	}
	
	private void editarContenidoCelda(Table table, CellEditor cellEditor, String nombreColumna){
		table.getRow().getColumn(nombreColumna).getCellRenderer().setCellEditor(cellEditor);
	}
	
	private CellEditor accionesCellEditor(){
		return new CellEditor(){
			public Object getValue(Object item, String property, int rowcount) {
				Long id = (Long) new BasicCellEditor().getValue(item, ConstantsRC.ID_VALUE, rowcount);
				BigDecimal plan = (BigDecimal) new BasicCellEditor().getValue(item, ConstantsRC.PLAN_VALUE, rowcount);
				String codImpuesto = (String) new BasicCellEditor().getValue(item, ConstantsRC.COD_IMPUESTO_VALUE, rowcount);
				Object nomImpuesto = new BasicCellEditor().getValue(item, ConstantsRC.NOM_IMPUESTO_VALUE, rowcount);
				BigDecimal valor = (BigDecimal) new BasicCellEditor().getValue(item, ConstantsRC.VALOR_VALUE, rowcount);
				String base = (String) new BasicCellEditor().getValue(item, ConstantsRC.BASE_VALUE, rowcount);

				HtmlBuilder html = this.botonEditar(id, plan, codImpuesto, nomImpuesto, valor, base);
				html.append(this.botonBorrar(id));

				return html.toString();
			}

			private HtmlBuilder botonBorrar(Long id) {
				String borrar = new StringBuilder("javascript:borrar(").append(id).append(");").toString();
				HtmlBuilder html = new HtmlBuilder();
				html.a().href().quote().append(borrar).quote().close();
				html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Datos para RC\" title=\"Borrar Datos para RC\"/>");
				html.aEnd();
				html.append("&nbsp;");
				return html;
			}

			private HtmlBuilder botonEditar(Long id, BigDecimal plan,
					String codImpuesto, Object nomImpuesto, BigDecimal valor,
					String base) {
				HtmlBuilder html = new HtmlBuilder();
				String editar = new StringBuilder("javascript:editar(").append(id)
						.append(", ").append(plan).append(", '")
						.append(codImpuesto).append("', '")
						.append(nomImpuesto).append("', ")
						.append(valor).append(", '").append(base)
						.append("')").toString();
				html.a().href().quote().append(editar).quote().close();
				html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Datos para RC\" title=\"Editar Datos para RC\"/>");
				html.aEnd();
				html.append("&nbsp;");
				return html;
			}
			
		};
	}
	
	private CellEditor valorCellEditor(){
		return new CellEditor() {
			private final NumberFormat nf = NumberFormat.getInstance(new Locale("es", "ES"));
			public Object getValue(Object item,	final String property, final int rowcount) {
				BigDecimal valor = (BigDecimal) new BasicCellEditor().getValue(item, ConstantsRC.VALOR_VALUE, rowcount);
				return new StringBuilder(this.nf.format(valor)).append(" %").toString();
			}
		};
	}

	private void configurarColumnas(final HtmlTable table) {
		configColumna(table, columnas.get(ConstantsRC.ID_KEY), ID_HEADER, false, false, "16%");
		configColumna(table, columnas.get(ConstantsRC.PLAN_KEY), PLAN_HEADER, true, true, "16%");
		configColumna(table, columnas.get(ConstantsRC.COD_IMPUESTO_KEY), COD_IMPUESTO_HEADER, true, true, "16%");
		configColumna(table, columnas.get(ConstantsRC.NOM_IMPUESTO_KEY), NOM_IMPUESTO_HEADER, true, true, "16%");
		configColumna(table, columnas.get(ConstantsRC.VALOR_KEY), VALOR_HEADER, true, true, "16%");
		configColumna(table, columnas.get(ConstantsRC.BASE_KEY), BASE_HEADER, true, true, "16%");
	}

	private void configColumna(final HtmlTable table, final String idCol,
			final String title, final boolean filterable,
			final boolean sortable, final String width) {

		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}

	private StringBuilder getlistaIdsTodos(final ImpuestosRCFilter filter) throws DAOException {
		StringBuilder listaIdsTodos = this.impuestosRCDao.getlistaIdsTodos(filter);
		return new StringBuilder("<script>$(\"#listaIdsTodos\").val(\"").append(listaIdsTodos).append("\");</script>");
	}

	@Override
	public ImpuestosRC guardarImpuesto(ImpuestosRC impuestosVista) throws BusinessException {
		try {
			String codigoImpuestoSbp = impuestosVista.getImpuestoSbp().getCodigo();
			ImpuestoSbp impuestoSbp = (ImpuestoSbp) this.impuestosRCDao.getObject(ImpuestoSbp.class, "codigo", codigoImpuestoSbp);
			String baseBaseSbp = impuestosVista.getBaseSbp().getBase();
			BaseSbp baseSbp = (BaseSbp) this.impuestosRCDao.getObject(BaseSbp.class, "base", baseBaseSbp);
			ImpuestosRC impuestosBD = new ImpuestosRC();
			impuestosBD.setBaseSbp(baseSbp);
			impuestosBD.setImpuestoSbp(impuestoSbp);
			impuestosBD.setValor(impuestosVista.getValor());
			impuestosBD.setCodPlan(impuestosVista.getCodPlan());
			
			this.impuestosRCDao.saveOrUpdate(impuestosBD);
			return impuestosBD;
		} catch (DAOException e) {
			throw new BusinessException("Ha habido un error al intentar guardar el nuevo impuesto",	e);
		}
	}

	@Override
	public void borrarImpuesto(Long impuestoRCId) throws BusinessException {
		this.impuestosRCDao.removeObject(ImpuestosRC.class, impuestoRCId);
	}

	@Override
	public ImpuestosRC modificarImpuesto(ImpuestosRC impuestosVista)
			throws BusinessException {
		try {
			ImpuestosRC impuestoBD = (ImpuestosRC)this.impuestosRCDao.get(ImpuestosRC.class, impuestosVista.getId());
			String codigoImpuestoSbp = impuestosVista.getImpuestoSbp().getCodigo();
			ImpuestoSbp impuestoSbp = (ImpuestoSbp) this.impuestosRCDao.getObject(ImpuestoSbp.class, "codigo", codigoImpuestoSbp);
			String baseBaseSbp = impuestosVista.getBaseSbp().getBase();
			BaseSbp baseSbp = (BaseSbp) this.impuestosRCDao.getObject(BaseSbp.class, "base", baseBaseSbp);
			impuestoBD.setBaseSbp(baseSbp);
			impuestoBD.setImpuestoSbp(impuestoSbp);
			impuestoBD.setValor(impuestosVista.getValor());
			impuestoBD.setCodPlan(impuestosVista.getCodPlan());
			
			this.impuestosRCDao.saveOrUpdate(impuestoBD);
			return impuestoBD; 
		} catch (DAOException e) {
			throw new BusinessException("Ha habido un error al intentar guardar el impuesto actualizado", e);
		}
	}

	@Override
	public List<ImpuestosRC> getImpuestosRCWithFilterAndSort(
			ImpuestosRCFilter filter, ImpuestosRCSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		try {
			return this.impuestosRCDao.getImpuestosRCWithFilterAndSort(filter,
					sort, rowStart, rowEnd);
		} catch (DAOException e) {
			throw new BusinessException("Ha habido un error al intentar guardar el nuevo impuesto",	e);
		}
	}

	@Override
	public String validarImpuestosRC(final ImpuestosRC impuestosVista)
			throws BusinessException {
		try {
			ImpuestoSbp impuestoSbp = (ImpuestoSbp)this.impuestosRCDao.getObject(ImpuestoSbp.class, "codigo", impuestosVista.getImpuestoSbp().getCodigo());
			if(impuestoSbp == null){
				return "No existe el impuesto";
			}
			BaseSbp baseSbp = (BaseSbp) this.impuestosRCDao.getObject(BaseSbp.class, "base", impuestosVista.getBaseSbp().getBase());
			if(baseSbp == null){
				return "No existe la base";
			}
			ImpuestosRCFilter filter = new ImpuestosRCFilter();
			filter.addFilter(ConstantsRC.PLAN_VALUE, impuestosVista.getCodPlan());
			filter.addFilter(ConstantsRC.COD_IMPUESTO_VALUE, impuestosVista.getImpuestoSbp().getCodigo());
			int numRegistros = this.impuestosRCDao.getImpuestosRCCountWithFilter(filter);
			if(numRegistros > 0){
				if(impuestosVista.getId() == null){
					// Alta de impuesto
					return "Ya existen datos para este impuesto RC.";
				} else {
					// Modificación de impuesto
					List<ImpuestosRC> listaImpuestos = this.impuestosRCDao
							.getImpuestosRCWithFilterAndSort(filter, new ImpuestosRCSort(), 0, numRegistros);
					for (ImpuestosRC impuesto : listaImpuestos) {
						
						if (!impuestosVista.getId().equals(impuesto.getId())) {
							
							return "Ya existen datos para este impuesto RC.";
						}
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessException("Se ha producido al ejecutar validarImpuestosRC: ", e);
		}
	
		return "";
	}

	@Override
	public void replicarImpuestosRC(final BigDecimal planOrig, final BigDecimal planDest)
			throws BusinessException {
		try {
			this.impuestosRCDao.replicarImpuestosRC(planOrig, planDest);
		} catch (DAOException e) {
			LOGGER.error("replicaDatosRC error. " + e);
			throw new BusinessException("Se ha producido al ejecutar replicarImpuestosRC: ", e);
		}
	}

	@Override
	public int getImpuestoRCCountWithFilter(final ImpuestosRCFilter filter)
			throws BusinessException {
		try {
			return this.impuestosRCDao.getImpuestosRCCountWithFilter(filter);
		} catch (DAOException e) {
			throw new BusinessException("Ha habido un error al intentar guardar el nuevo impuesto", e);
		}
	}
	
	@Override
	public String validarImpuestosRCReplica(BigDecimal planDest) throws BusinessException {
		
		String errorMsg = "";

		try {
			ImpuestosRCFilter filter = new ImpuestosRCFilter();
			filter.addFilter(ConstantsRC.PLAN_VALUE, planDest);
			int numRegs = this.impuestosRCDao.getImpuestosRCCountWithFilter(filter);
			if (numRegs > 0) {
				errorMsg = "El plan destino de la réplica ya tiene impuestos para RC.";
			}
		} catch (Exception e) {
			LOGGER.error("validateReplicaDatosRC error. " + e);
			throw new BusinessException("Se ha producido al ejecutar validarReplicaImpuestosRC:", e);
		}
		return errorMsg;
	}

	public void setImpuestosRCDao(IImpuestosRCDao impuestosRCDao) {
		this.impuestosRCDao = impuestosRCDao;
	}

	public void setId(String id) {
		this.id = id;
	}
}
