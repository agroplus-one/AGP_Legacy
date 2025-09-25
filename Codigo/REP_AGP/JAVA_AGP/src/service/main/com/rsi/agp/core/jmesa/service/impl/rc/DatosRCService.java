package com.rsi.agp.core.jmesa.service.impl.rc;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import com.rsi.agp.core.jmesa.filter.gan.DatosRCFilter;
import com.rsi.agp.core.jmesa.service.IDatosRCService;
import com.rsi.agp.core.jmesa.sort.DatosRCSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.rc.IDatosRCDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.rc.DatosRC;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;
import com.rsi.agp.dao.tables.rc.SumaAseguradaRC;

@SuppressWarnings("deprecation")
public class DatosRCService implements IDatosRCService {

	// Constantes
	private static final String TEXT_ALIGN_RIGHT = "text-align:right";
	private static final String EURO = " &euro;";
	private static final String NBSP = "&nbsp;";
	private static final String PRIMA_MINIMA2 = "primaMinima";
	private static final String FRANQUICIA2 = "franquicia";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD = "subentidadMediadora.id.codsubentidad";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODENTIDAD = "subentidadMediadora.id.codentidad";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	private static final String LINEA_CODPLAN = "linea.codplan";

	private Log logger = LogFactory.getLog(getClass());

	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String idRC;

	// Constantes para los nombres de las columnas del listado
	private final static String ID = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String DES_LINEA = "DES_LINEA";
	private final static String ESMEDIADORA = "ENTIDAD";
	private final static String SUBENTMEDIADORA = "SUBENTIDAD";
	private final static String COD_ESPECIE_RC = "COD_ESPECIE_RC";
	private final static String DES_ESPECIE_RC = "DES_ESPECIE_RC";
	private final static String COD_REGIMEN_RC = "COD_REGIMEN_RC";
	private final static String DES_REGIMEN_RC = "DES_REGIMEN_RC";
	private final static String COD_SUMA_ASEGURADA_RC = "COD_SUMA_ASEGURADA_RC";
	private final static String DES_SUMA_ASEGURADA_RC = "DES_SUMA_ASEGURADA_RC";
	private final static String TASA = "TASA";
	private final static String FRANQUICIA = "FRANQUICIA";
	private final static String PRIMA_MINIMA = "PRIMA_MINIMA";

	private IDatosRCDao datosRCDao;
	private ILineaDao lineaDao;

	@Override
	public Collection<EspeciesRC> getEspeciesRC() throws BusinessException {

		Collection<EspeciesRC> result = null;

		try {

			result = this.datosRCDao.getEspeciesRC();

		} catch (Exception ex) {

			logger.error("getEspeciesRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar getEspeciesRC:", ex);
		}

		return result;
	}

	@Override
	public Collection<RegimenRC> getRegimenesRC() throws BusinessException {

		Collection<RegimenRC> result = null;

		try {

			result = this.datosRCDao.getRegimenesRC();

		} catch (Exception ex) {

			logger.error("getRegimenesRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar getRegimenesRC:", ex);
		}

		return result;
	}

	@Override
	public Collection<SumaAseguradaRC> getSumasAseguradasRC()
			throws BusinessException {

		Collection<SumaAseguradaRC> result = null;

		try {

			result = this.datosRCDao.getSumasAseguradasRC();

		} catch (Exception ex) {

			logger.error("getSumasAseguradasRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar getSumasAseguradasRC:", ex);
		}

		return result;
	}

	@Override
	public String validateDatosRC(final DatosRC datosRC)
			throws BusinessException {

		String errorMsg = null;

		try {

			SubentidadMediadoraId id = new SubentidadMediadoraId();
			id.setCodentidad(datosRC.getSubentidadMediadora().getId()
					.getCodentidad());
			id.setCodsubentidad(datosRC.getSubentidadMediadora().getId()
					.getCodsubentidad());
			
			Linea linea = this.lineaDao.getLinea(datosRC.getLinea()
					.getCodlinea(), datosRC.getLinea().getCodplan());

			if (linea == null) {

				errorMsg = "El plan/línea introducido no existe.";

			} else if (datosRC.getSubentidadMediadora().getId().getCodentidad() != null
					&& datosRC.getSubentidadMediadora().getId().getCodsubentidad() != null
					&& this.datosRCDao.get(SubentidadMediadora.class, id) == null) {
				
				errorMsg = "La E-S Mediadora introducida no existe.";
				
			} else if (this.datosRCDao.existeDatosRC(datosRC)) {

				errorMsg = "Ya existen datos para RC para ese plan/línea/E-S Med/especie/régimen/suma asegurada.";
			}
		} catch (Exception ex) {

			logger.error("validateDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar validateDatosRC:", ex);
		}

		return errorMsg;
	}

	@Override
	public String validateReplicaDatosRC(final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException {
		String errorMsg = null;

		try {

			Linea linea = this.lineaDao.getLinea(lineaDest, planDest);

			if (linea == null) {

				errorMsg = "El plan/línea introducido no existe.";

			} else {

				DatosRCFilter filter = new DatosRCFilter();
				filter.addFilter(LINEA_CODPLAN, planDest.toString());
				filter.addFilter(LINEA_CODLINEA, lineaDest.toString());

				int numRegs = this.datosRCDao.getDatosRCCountWithFilter(filter);

				if (numRegs > 0) {

					errorMsg = "El plan/línea destino de la réplica ya tiene Datos para RC.";
				}
			}
		} catch (Exception ex) {

			logger.error("validateReplicaDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar validateReplicaDatosRC:", ex);
		}

		return errorMsg;
	}

	@Override
	public DatosRC grabarDatosRC(final DatosRC datosRC)
			throws BusinessException {

		DatosRC datoRC = new DatosRC();

		try {

			Linea linea = this.lineaDao.getLinea(datosRC.getLinea()
					.getCodlinea(), datosRC.getLinea().getCodplan());
			datoRC.setLinea(linea);

			if (datosRC.getSubentidadMediadora().getId().getCodentidad() != null
					&& datosRC.getSubentidadMediadora().getId().getCodsubentidad() != null) {
				
				SubentidadMediadoraId id = new SubentidadMediadoraId();
				id.setCodentidad(datosRC.getSubentidadMediadora().getId()
						.getCodentidad());
				id.setCodsubentidad(datosRC.getSubentidadMediadora().getId()
						.getCodsubentidad());
				SubentidadMediadora esMed = (SubentidadMediadora) this.datosRCDao
						.get(SubentidadMediadora.class, id);
				datoRC.setSubentidadMediadora(esMed);
			}
			
			EspeciesRC especiesRC = (EspeciesRC) this.datosRCDao.get(
					EspeciesRC.class, datosRC.getEspeciesRC().getCodespecie());
			datoRC.setEspeciesRC(especiesRC);

			RegimenRC regimenRC = (RegimenRC) this.datosRCDao.get(
					RegimenRC.class, datosRC.getRegimenRC().getCodregimen());
			datoRC.setRegimenRC(regimenRC);

			SumaAseguradaRC sumaAseguradaRC = (SumaAseguradaRC) this.datosRCDao
					.get(SumaAseguradaRC.class, datosRC.getSumaAseguradaRC()
							.getCodsuma());
			datoRC.setSumaAseguradaRC(sumaAseguradaRC);

			datoRC.setTasa(datosRC.getTasa());
			datoRC.setFranquicia(datosRC.getFranquicia());
			datoRC.setPrimaMinima(datosRC.getPrimaMinima());

			this.datosRCDao.saveOrUpdate(datoRC);

		} catch (Exception ex) {

			logger.error("grabarDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar grabarDatosRC:", ex);
		}

		return datoRC;
	}

	@Override
	public DatosRC modificarDatosRC(final DatosRC datosRC)
			throws BusinessException {

		DatosRC modifDatosRC = null;

		try {

			modifDatosRC = (DatosRC) this.datosRCDao.get(DatosRC.class,
					datosRC.getId());

			Linea linea = this.lineaDao.getLinea(datosRC.getLinea()
					.getCodlinea(), datosRC.getLinea().getCodplan());
			modifDatosRC.setLinea(linea);
			
			if (datosRC.getSubentidadMediadora().getId().getCodentidad() != null
					&& datosRC.getSubentidadMediadora().getId().getCodsubentidad() != null) {
			
				SubentidadMediadoraId id = new SubentidadMediadoraId();
				id.setCodentidad(datosRC.getSubentidadMediadora().getId()
						.getCodentidad());
				id.setCodsubentidad(datosRC.getSubentidadMediadora().getId()
						.getCodsubentidad());
				SubentidadMediadora esMed = (SubentidadMediadora) this.datosRCDao
						.get(SubentidadMediadora.class, id);
				modifDatosRC.setSubentidadMediadora(esMed);
				
			} else {
				
				modifDatosRC.setSubentidadMediadora(new SubentidadMediadora());
			}

			EspeciesRC especiesRC = (EspeciesRC) this.datosRCDao.get(
					EspeciesRC.class, datosRC.getEspeciesRC().getCodespecie());
			modifDatosRC.setEspeciesRC(especiesRC);

			RegimenRC regimenRC = (RegimenRC) this.datosRCDao.get(
					RegimenRC.class, datosRC.getRegimenRC().getCodregimen());
			modifDatosRC.setRegimenRC(regimenRC);

			SumaAseguradaRC sumaAseguradaRC = (SumaAseguradaRC) this.datosRCDao
					.get(SumaAseguradaRC.class, datosRC.getSumaAseguradaRC()
							.getCodsuma());
			modifDatosRC.setSumaAseguradaRC(sumaAseguradaRC);

			modifDatosRC.setTasa(datosRC.getTasa());
			modifDatosRC.setFranquicia(datosRC.getFranquicia());
			modifDatosRC.setPrimaMinima(datosRC.getPrimaMinima());

			this.datosRCDao.saveOrUpdate(modifDatosRC);

		} catch (Exception ex) {

			logger.error("modificarDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar modificarDatosRC:", ex);
		}

		return modifDatosRC;
	}

	@Override
	public void borrarDatosRC(final Long idDatosRC) throws BusinessException {

		try {

			DatosRC datosRC = (DatosRC) this.datosRCDao.get(DatosRC.class,
					idDatosRC);

			this.datosRCDao.delete(datosRC);

		} catch (Exception ex) {

			logger.error("borrarDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar borrarDatosRC:", ex);
		}
	}

	@Override
	public void replicaDatosRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException {

		try {

			this.datosRCDao.replicaDatosRC(planOrig, lineaOrig, planDest,
					lineaDest);

		} catch (Exception ex) {

			logger.error("replicaDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar replicaDatosRC:", ex);
		}
	}

	@Override
	public String getTablaDatosRC(final HttpServletRequest request,
			final HttpServletResponse response, final DatosRC datosRC,
			final String origenLlamada) throws BusinessException {

		try {

			TableFacade tableFacade = crearTableFacade(request, response,
					datosRC, origenLlamada);

			Limit limit = tableFacade.getLimit();

			DatosRCFilter consultaFilter = getConsultaDatosRCFilter(limit);

			setDataAndLimitVariables(tableFacade, consultaFilter, limit);

			String listaIdsTodos = getlistaIdsTodos(consultaFilter);
			String script = "<script>$(\"#listaIdsTodos\").val(\""
					+ listaIdsTodos + "\");</script>";

			tableFacade.setToolbar(new CustomToolbarMarcarTodos());

			tableFacade.setView(new CustomView());

			return html(tableFacade) + script;

		} catch (Exception ex) {

			logger.error("getTablaDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getTablaDatosRC:", ex);
		}
	}

	private TableFacade crearTableFacade(final HttpServletRequest request,
			final HttpServletResponse response, final DatosRC datosRC,
			final String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(this.idRC,
				request);

		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("listaDatosRC_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listaDatosRC_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(columnas, datosRC, tableFacade);
			}
		}

		return tableFacade;
	}

	private HashMap<String, String> cargarColumnas(final TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (this.columnas.size() == 0) {
			this.columnas.put(ID, "id");
			this.columnas.put(PLAN, LINEA_CODPLAN);
			this.columnas.put(LINEA, LINEA_CODLINEA);
			this.columnas.put(DES_LINEA, "linea.nomlinea");
			this.columnas.put(ESMEDIADORA, SUBENTIDAD_MEDIADORA_ID_CODENTIDAD);
			this.columnas.put(SUBENTMEDIADORA,
					SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD);
			this.columnas.put(COD_ESPECIE_RC, "especiesRC.codespecie");
			this.columnas.put(DES_ESPECIE_RC, "especiesRC.descripcion");
			this.columnas.put(COD_REGIMEN_RC, "regimenRC.codregimen");
			this.columnas.put(DES_REGIMEN_RC, "regimenRC.descripcion");
			this.columnas.put(COD_SUMA_ASEGURADA_RC, "sumaAseguradaRC.codsuma");
			this.columnas.put(DES_SUMA_ASEGURADA_RC,
					"sumaAseguradaRC.descripccion");
			this.columnas.put(TASA, "tasa");
			this.columnas.put(FRANQUICIA, FRANQUICIA2);
			this.columnas.put(PRIMA_MINIMA, PRIMA_MINIMA2);
		}
		tableFacade.setColumnProperties(this.columnas.get(ID),
				this.columnas.get(PLAN), this.columnas.get(LINEA),
				this.columnas.get(ESMEDIADORA),
				this.columnas.get(DES_ESPECIE_RC),
				this.columnas.get(DES_REGIMEN_RC),
				this.columnas.get(DES_SUMA_ASEGURADA_RC),
				this.columnas.get(TASA), this.columnas.get(FRANQUICIA),
				this.columnas.get(PRIMA_MINIMA));

		return this.columnas;
	}

	private void cargarFiltrosBusqueda(final HashMap<String, String> columnas,
			final DatosRC datosRC, final TableFacade tableFacade) {
		// PLAN
		if (datosRC.getLinea() != null
				&& FiltroUtils.noEstaVacio(datosRC.getLinea().getCodplan()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(PLAN), datosRC.getLinea()
									.getCodplan().toString()));
		// LINEA
		if (datosRC.getLinea() != null
				&& FiltroUtils.noEstaVacio(datosRC.getLinea().getCodlinea()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(LINEA), datosRC.getLinea()
									.getCodlinea().toString()));
		// ESMEDIADORA
		if (datosRC.getSubentidadMediadora().getId().getCodentidad() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(ESMEDIADORA), datosRC
									.getSubentidadMediadora().getId()
									.getCodentidad().toString()));
		// SUBENTIDADMEDIADORA
		if (datosRC.getSubentidadMediadora().getId().getCodsubentidad() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(SUBENTMEDIADORA), datosRC
									.getSubentidadMediadora().getId()
									.getCodsubentidad().toString()));
		// ESPECIE PARA RC
		if (datosRC.getEspeciesRC() != null
				&& FiltroUtils.noEstaVacio(datosRC.getEspeciesRC()
						.getCodespecie()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_ESPECIE_RC), datosRC
									.getEspeciesRC().getCodespecie()));
		// REGIMEN PARA RC
		if (datosRC.getRegimenRC() != null
				&& FiltroUtils.noEstaVacio(datosRC.getRegimenRC()
						.getCodregimen()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_REGIMEN_RC), datosRC
									.getRegimenRC().getCodregimen().toString()));
		// SUMA ASEGURADA
		if (datosRC.getSumaAseguradaRC() != null
				&& FiltroUtils.noEstaVacio(datosRC.getSumaAseguradaRC()
						.getCodsuma()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_SUMA_ASEGURADA_RC),
									datosRC.getSumaAseguradaRC().getCodsuma()
											.toString()));
		// TASA
		if (FiltroUtils.noEstaVacio(datosRC.getTasa()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(TASA), datosRC.getTasa()
									.toString()));
		// FRANQUICIA
		if (FiltroUtils.noEstaVacio(datosRC.getFranquicia()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(FRANQUICIA), datosRC
									.getFranquicia().toString()));
		// PRIMA MINIMA
		if (FiltroUtils.noEstaVacio(datosRC.getPrimaMinima()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(PRIMA_MINIMA), datosRC
									.getPrimaMinima().toString()));
	}

	private DatosRCFilter getConsultaDatosRCFilter(final Limit limit) {
		DatosRCFilter consultaFilter = new DatosRCFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			consultaFilter.addFilter(property, value);
		}
		return consultaFilter;
	}

	private void setDataAndLimitVariables(final TableFacade tableFacade,
			final DatosRCFilter consultaFilter, final Limit limit) {

		Collection<DatosRC> items = new ArrayList<DatosRC>();
		try {

			int totalRows = getDatosRCCountWithFilter(consultaFilter);
			logger.debug("********** count filas para DatosRC = " + totalRows
					+ " **********");

			tableFacade.setTotalRows(totalRows);

			DatosRCSort consultaSort = getConsultaDatosRCSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getDatosRCWithFilterAndSort(consultaFilter, consultaSort,
					rowStart, rowEnd);
			logger.debug("********** list items para DatosRC = " + items.size()
					+ " **********");

		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	private DatosRCSort getConsultaDatosRCSort(final Limit limit) {
		DatosRCSort consultaSort = new DatosRCSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}
		return consultaSort;
	}

	public String getlistaIdsTodos(final DatosRCFilter consultaFilter)
			throws DAOException {

		String listaIdsTodos = this.datosRCDao.getlistaIdsTodos(consultaFilter);
		return listaIdsTodos;
	}

	@Override
	public Collection<DatosRC> getDatosRCWithFilterAndSort(
			final DatosRCFilter filter, final DatosRCSort sort,
			final int rowStart, final int rowEnd) throws BusinessException {

		try {

			return this.datosRCDao.getDatosRCWithFilterAndSort(filter, sort,
					rowStart, rowEnd);

		} catch (Exception ex) {

			logger.error("getDatosRCWithFilterAndSort error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getDatosRCWithFilterAndSort:",
					ex);
		}
	}

	@Override
	public int getDatosRCCountWithFilter(final DatosRCFilter filter)
			throws BusinessException {
		try {

			return this.datosRCDao.getDatosRCCountWithFilter(filter);

		} catch (Exception ex) {

			logger.error("getDatosRCCountWithFilter error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getDatosRCCountWithFilter:", ex);
		}
	}

	private String html(final TableFacade tableFacade) {

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
			table.getRow().getColumn(columnas.get(ID)).getCellRenderer()
					.setCellEditor(new CellEditor() {

						public Object getValue(final Object item,
								final String property, final int rowcount) {

							HtmlBuilder html = new HtmlBuilder();

							Long id = (Long) new BasicCellEditor().getValue(
									item, "id", rowcount);
							BigDecimal plan = (BigDecimal) new BasicCellEditor()
									.getValue(item, LINEA_CODPLAN, rowcount);
							BigDecimal linea = (BigDecimal) new BasicCellEditor()
									.getValue(item, LINEA_CODLINEA, rowcount);
							String desclinea = (String) new BasicCellEditor()
									.getValue(item, "linea.nomlinea", rowcount);
							BigDecimal entmediadora = (BigDecimal) new BasicCellEditor()
									.getValue(
											item,
											SUBENTIDAD_MEDIADORA_ID_CODENTIDAD,
											rowcount);
							BigDecimal subentmediadora = (BigDecimal) new BasicCellEditor()
									.getValue(
											item,
											SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD,
											rowcount);
							String especiesRC = (String) new BasicCellEditor()
									.getValue(item, "especiesRC.codespecie",
											rowcount);
							BigDecimal regimenesRC = (BigDecimal) new BasicCellEditor()
									.getValue(item, "regimenRC.codregimen",
											rowcount);
							BigDecimal sumaAsegurada = (BigDecimal) new BasicCellEditor()
									.getValue(item, "sumaAseguradaRC.codsuma",
											rowcount);
							BigDecimal tasa = (BigDecimal) new BasicCellEditor()
									.getValue(item, "tasa", rowcount);
							BigDecimal franquicia = (BigDecimal) new BasicCellEditor()
									.getValue(item, FRANQUICIA2, rowcount);
							BigDecimal primaMinima = (BigDecimal) new BasicCellEditor()
									.getValue(item, PRIMA_MINIMA2, rowcount);

							// checkbox
							html.append("<input type=\"checkbox\" id=\"check_"
									+ id + "\"  name=\"check_" + id
									+ "\" onClick =\"listaCheckId(\'" + id
									+ "')\" class=\"dato\"/>");
							html.append(NBSP);
							
							// boton editar
							
							if(entmediadora != null){
								html.a()
									.href()
									.quote()
									.append("javascript:editar(" + id + ", "
											+ plan + ", " + linea + ", '"
											+ desclinea + "', " + entmediadora 
											+ ", " + subentmediadora + ", '"
											+ especiesRC + "', " + regimenesRC
											+ ", " + sumaAsegurada + ", '"
											+ tasa + "', " + franquicia + ", "
											+ primaMinima + ");").quote()
										.close();
							} else {
								html.a()
									.href()
									.quote()
									.append("javascript:editarSinEntidad(" + id + ", "
											+ plan + ", " + linea + ", '"
											+ desclinea + "', '"
											+ especiesRC + "', " + regimenesRC
											+ ", " + sumaAsegurada + ", '"
											+ tasa + "', " + franquicia + ", "
											+ primaMinima + ");").quote()
									.close();
							}
							html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Datos para RC\" title=\"Editar Datos para RC\"/>");
							html.aEnd();
							html.append(NBSP);

							// boton borrar
							html.a().href().quote()
									.append("javascript:borrar(" + id + ");")
									.quote().close();
							html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Datos para RC\" title=\"Borrar Datos para RC\"/>");
							html.aEnd();
							html.append(NBSP);

							return html.toString();
						}
					});

			table.getRow().getColumn(columnas.get(ESMEDIADORA))
					.getCellRenderer().setCellEditor(new CellEditor() {

						public Object getValue(final Object item,
								final String property, final int rowcount) {

							HtmlBuilder html = new HtmlBuilder();

							BigDecimal entMedia = (BigDecimal) new BasicCellEditor()
									.getValue(
											item,
											SUBENTIDAD_MEDIADORA_ID_CODENTIDAD,
											rowcount);
							BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor()
									.getValue(
											item,
											SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD,
											rowcount);
							
							if(entMedia == null) {
								return NBSP;
							}
							
							html.append(entMedia + "-" + subEntMedia);

							return html.toString();
						}
					});

			table.getRow().getColumn(columnas.get(TASA)).getCellRenderer()
					.setCellEditor(new CellEditor() {

						private final NumberFormat nf = NumberFormat
								.getInstance(new Locale("es", "ES"));

						public Object getValue(final Object item,
								final String property, final int rowcount) {

							HtmlBuilder html = new HtmlBuilder();

							BigDecimal tasa = (BigDecimal) new BasicCellEditor()
									.getValue(item, "tasa", rowcount);

							html.append(this.nf.format(tasa));

							return html.toString();
						}
					});

			table.getRow().getColumn(columnas.get(FRANQUICIA))
					.getCellRenderer().setCellEditor(new CellEditor() {
						
						private final NumberFormat nf = NumberFormat
								.getInstance(new Locale("es", "ES"));

						public Object getValue(final Object item,
								final String property, final int rowcount) {
							
							HtmlBuilder html = new HtmlBuilder();

							BigDecimal franquicia = (BigDecimal) new BasicCellEditor()
									.getValue(item, FRANQUICIA2, rowcount);
							
							html.append(this.nf.format(franquicia));
							html.append(EURO);

							return html.toString();
						}
					});
		}

		table.getRow().getColumn(columnas.get(PRIMA_MINIMA)).getCellRenderer()
				.setCellEditor(new CellEditor() {

					private final NumberFormat nf = NumberFormat
							.getInstance(new Locale("es", "ES"));

					public Object getValue(final Object item,
							final String property, final int rowcount) {
						
						HtmlBuilder html = new HtmlBuilder();

						BigDecimal primaMinima = (BigDecimal) new BasicCellEditor()
								.getValue(item, PRIMA_MINIMA2, rowcount);
						
						html.append(this.nf.format(primaMinima));
						html.append(EURO);

						return html.toString();
					}
				});
		
		table.getRow().getColumn(columnas.get(DES_SUMA_ASEGURADA_RC)).getCellRenderer()
				.setCellEditor(new CellEditor() {
					
					@Override
					public Object getValue(Object item, String property, int rowCount) {
						
						String sumaAseg = (String)new BasicCellEditor()
									.getValue(item, "sumaAseguradaRC.descripccion", rowCount);
						
						StringBuilder sb = new StringBuilder(sumaAseg).append(EURO);
						
						return sb.toString();
					}
				});
		
		return tableFacade.render();
	}

	private void configurarColumnas(final HtmlTable table) {

		configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false,
				false, "8%");
		configColumna(table, columnas.get(PLAN), "Plan", true, true, "6%");
		configColumna(table, columnas.get(LINEA), "Línea", true, true, "6%");
		configColumna(table, columnas.get(ESMEDIADORA), "E-S Med.", true, true,
				"8%");
		configColumna(table, columnas.get(DES_ESPECIE_RC), "Especie RC", false,
				true, "12%");
		configColumna(table, columnas.get(DES_REGIMEN_RC), "Régimen", false,
				true, "24%");
		configColumna(table, columnas.get(DES_SUMA_ASEGURADA_RC),
				"Suma Asegurada", false, true, "10%", TEXT_ALIGN_RIGHT);
		configColumna(table, columnas.get(TASA), "Tasa", true, true, "8%",
				TEXT_ALIGN_RIGHT);
		configColumna(table, columnas.get(FRANQUICIA), "Franquicia", true,
				true, "10%", TEXT_ALIGN_RIGHT);
		configColumna(table, columnas.get(PRIMA_MINIMA), "Prima mínima", true,
				true, "8%", TEXT_ALIGN_RIGHT);
	}
	
	private void configColumna(final HtmlTable table, final String idCol,
			final String title, final boolean filterable,
			final boolean sortable, final String width) {

		configColumna(table, idCol, title, filterable, sortable, width, null);
	}

	private void configColumna(final HtmlTable table, final String idCol,
			final String title, final boolean filterable,
			final boolean sortable, final String width, final String style) {

		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
		if (style != null) {
			table.getRow().getColumn(idCol).setStyle(style);
		}
	}

	public void setId(final String id) {
		this.idRC = id;
	}

	public void setDatosRCDao(final IDatosRCDao datosRCDao) {
		this.datosRCDao = datosRCDao;
	}

	public void setLineaDao(final ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	@Override
	public void cambioMasivoDatosRC(final String[] idsMarcadosStrArr,
			final BigDecimal tasaCM, final BigDecimal franquiciaCM,
			final BigDecimal primaMinimaCM) throws BusinessException {

		try {

			List<DatosRC> listaItemsCM = new ArrayList<DatosRC>(
					idsMarcadosStrArr.length);

			DatosRC datosRC;
			for (String idDatosRC : idsMarcadosStrArr) {

				datosRC = (DatosRC) this.datosRCDao.get(DatosRC.class,
						Long.valueOf(idDatosRC));

				if (tasaCM != null) {
					datosRC.setTasa(tasaCM);
				}
				if (franquiciaCM != null) {
					datosRC.setFranquicia(franquiciaCM);
				}
				if (primaMinimaCM != null) {
					datosRC.setPrimaMinima(primaMinimaCM);
				}

				listaItemsCM.add(datosRC);
			}

			this.datosRCDao.saveOrUpdateList(listaItemsCM);

		} catch (Exception ex) {

			logger.error("cambioMasivoDatosRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar cambioMasivoDatosRC:", ex);
		}
	}
}