package com.rsi.agp.core.jmesa.service.impl.rc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
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
import com.rsi.agp.core.jmesa.filter.gan.LineasRCFilter;
import com.rsi.agp.core.jmesa.service.ILineasRCService;
import com.rsi.agp.core.jmesa.sort.LineasRCSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.rc.ILineasRCDao;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.EspecieId;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejoId;
import com.rsi.agp.dao.tables.orgDat.VistaPorFactores;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.LineasRC;

@SuppressWarnings("deprecation")
public class LineasRCService implements ILineasRCService {

	private Log logger = LogFactory.getLog(getClass());

	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id;

	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "ID";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String DES_LINEA = "DES_LINEA";
	private final static String COD_ESPECIE = "COD_ESPECIE";
	private final static String DES_ESPECIE = "DES_ESPECIE";
	private final static String COD_REGIMEN = "COD_REGIMEN";
	private final static String DES_REGIMEN = "DES_REGIMEN";
	private final static String COD_TIPOCAPITAL = "COD_TIPOCAPITAL";
	private final static String DES_TIPOCAPITAL = "DES_TIPOCAPITAL";
	private final static String COD_ESPECIE_RC = "COD_ESPECIE_RC";
	private final static String DES_ESPECIE_RC = "DES_ESPECIE_RC";
	private final static BigDecimal GENERICO_999 = new BigDecimal("999");

	private ILineasRCDao lineasRCDao;
	private ILineaDao lineaDao;

	@Override
	public Collection<EspeciesRC> getEspeciesRC() throws BusinessException {

		Collection<EspeciesRC> result = null;

		try {

			result = this.lineasRCDao.getEspeciesRC();

		} catch (Exception ex) {

			logger.error("getEspeciesRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getEspeciesRC:", ex);
		}

		return result;
	}

	@Override
	public String validateLineaRC(final LineasRC lineasRC)
			throws BusinessException {

		try {

			Linea linea = this.lineaDao.getLinea(lineasRC.getLinea()
					.getCodlinea(), lineasRC.getLinea().getCodplan());

			if (linea == null) {

				return "El plan/línea introducido no existe.";

			} else {

				EspecieId especieId = new EspecieId(linea.getLineaseguroid(),
						lineasRC.getCodespecie().longValue());
				Especie especie = (Especie)this.lineasRCDao.get(Especie.class, especieId);
				if (especie == null && !GENERICO_999.equals(lineasRC.getCodespecie())) {
					return "La especie introducida no existe.";
				} else {
					lineasRC.setDescespecie(especie.getDescripcion());
				}
				
				RegimenManejoId regimenId = new RegimenManejoId(
						linea.getLineaseguroid(), lineasRC.getCodregimen()
								.longValue());
				RegimenManejo regimen = (RegimenManejo)this.lineasRCDao.get(RegimenManejo.class, regimenId);
				if (regimen == null && !GENERICO_999.equals(lineasRC.getCodregimen())) {
					return "El régimen introducido no existe.";
				} else {
					lineasRC.setDescregimen(regimen.getDescripcion());
				}
				
				if (!GENERICO_999.equals(lineasRC.getCodtipocapital())) {

					final class VistaPorFactoresFiltro implements com.rsi.agp.dao.filters.Filter {
						
						private BigDecimal lineaseguroid;
						private BigDecimal codvalor;
						
						@Override
						public Criteria getCriteria(final Session sesion) {
							Criteria criteria = sesion.createCriteria(VistaPorFactores.class);
							criteria.add(Restrictions.eq("id.lineaseguroid", this.lineaseguroid));
							criteria.add(Restrictions.eq("id.codconcepto", BigDecimal.valueOf(126)));
							criteria.add(Restrictions.eq("id.codvalor", this.codvalor));
							return criteria;
						}

						public void setLineaseguroid(final Long lineaseguroid) {
							this.lineaseguroid = BigDecimal.valueOf(lineaseguroid);
						}
						
						public void setCodvalor(final BigDecimal codvalor) {
							this.codvalor = codvalor;
						}
					}
					
					VistaPorFactoresFiltro filter = new VistaPorFactoresFiltro();
					filter.setLineaseguroid(linea.getLineaseguroid());
					filter.setCodvalor(lineasRC.getCodtipocapital());
					@SuppressWarnings("unchecked")
					List<VistaPorFactores> result = this.lineasRCDao.getObjects(filter);
					
					if (result == null || (result != null && result.isEmpty())) {
						return "El tipo de capital introducido no existe.";
					} else {						
						for (VistaPorFactores tc : result) {							
							if (lineasRC.getCodtipocapital().equals(tc.getId().getCodvalor())) {
								lineasRC.setDesctipocapital(tc.getId().getDescripcion());
								break;
							}
						}
					}
				} else {
					lineasRC.setDesctipocapital("TODOS");
				}

				LineasRCFilter filter = new LineasRCFilter();
				filter.addFilter("linea.codplan", lineasRC.getLinea()
						.getCodplan().toString());
				filter.addFilter("linea.codlinea", lineasRC.getLinea()
						.getCodlinea().toString());
				filter.addFilter("codespecie", lineasRC.getCodespecie()
						.toString());
				filter.addFilter("codregimen", lineasRC.getCodregimen()
						.toString());
				filter.addFilter("codtipocapital", lineasRC
						.getCodtipocapital().toString());
				
				int numRegs = this.lineasRCDao
						.getLineasRCCountWithFilter(filter);

				boolean existeLineaRC = Boolean.FALSE;
				// ALTA
				if (lineasRC.getId() == null && numRegs > 0) {
					
					//if (numRegs > 0) {

						existeLineaRC = Boolean.TRUE;
					//} 
				} else {
					// MODIFICACION
					if (numRegs > 0) {
						
						// VERIFICAMOS QUE NO SEA EL MISMO REGISTRO QUE
						// ESTAMOS MODIFICANDO
						Collection<LineasRC> listaLineasRC = this.lineasRCDao
								.getLineasRCWithFilterAndSort(filter,
										new LineasRCSort(), 0, numRegs);
						
						if (listaLineasRC != null
								&& !listaLineasRC.isEmpty()) {

							if (listaLineasRC.size() == 1) {

								if (!lineasRC
										.getId()
										.equals(listaLineasRC
												.toArray(new LineasRC[] {})[0]
												.getId())) {

									existeLineaRC = Boolean.TRUE;
								}
							} else if (listaLineasRC.size() > 1) {

								existeLineaRC = Boolean.TRUE;
							}
						}
					}
				}
				if (existeLineaRC) {
					
					return "Ya existe una línea RC para ese plan/línea/especie/régimen/tipo de capital.";
				}
				
			}
		} catch (Exception ex) {

			logger.error("validateLineaRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar validateLineaRC:", ex);
		}

		return null;
	}

	@Override
	public String validateReplicaLineaRC(final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException {
		String errorMsg = null;

		try {

			Linea linea = this.lineaDao.getLinea(lineaDest, planDest);

			if (linea == null) {

				errorMsg = "El plan/línea introducido no existe.";

			} else {

				LineasRCFilter filter = new LineasRCFilter();
				filter.addFilter("linea.codplan", planDest.toString());
				filter.addFilter("linea.codlinea", lineaDest.toString());
				
				int numRegs = this.lineasRCDao.getLineasRCCountWithFilter(filter);
				
				if (numRegs > 0) {
				
					errorMsg = "El plan/línea destino de la réplica ya tiene Líneas para RC.";
				}
			}
		} catch (Exception ex) {

			logger.error("validateReplicaLineaRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar validateReplicaLineaRC:", ex);
		}

		return errorMsg;
	}

	@Override
	public LineasRC grabarLineaRC(final LineasRC lineasRC)
			throws BusinessException {

		LineasRC newRecord = new LineasRC();

		try {

			Linea linea = this.lineaDao.getLinea(lineasRC.getLinea()
					.getCodlinea(), lineasRC.getLinea().getCodplan());
			newRecord.setLinea(linea);

			EspeciesRC especiesRC = (EspeciesRC) this.lineasRCDao.get(
					EspeciesRC.class, lineasRC.getEspeciesRC().getCodespecie());
			newRecord.setEspeciesRC(especiesRC);

			newRecord.setCodespecie(lineasRC.getCodespecie());
			newRecord.setCodregimen(lineasRC.getCodregimen());
			newRecord.setCodtipocapital(lineasRC.getCodtipocapital());

			newRecord = (LineasRC) this.lineasRCDao.saveOrUpdate(newRecord);
			
			newRecord.setDescespecie(lineasRC.getDescespecie());
			newRecord.setDescregimen(lineasRC.getDescregimen());
			newRecord.setDesctipocapital(lineasRC.getDesctipocapital());

		} catch (Exception ex) {

			logger.error("grabarLineaRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar grabarLineaRC:", ex);
		}

		return newRecord;
	}
	
	@Override
	public LineasRC modificarLineaRC(final LineasRC lineasRC)
			throws BusinessException {

		LineasRC modifLineaRC = null;

		try {

			modifLineaRC = (LineasRC) this.lineasRCDao.get(LineasRC.class, lineasRC.getId());
			
			Linea linea = this.lineaDao.getLinea(lineasRC.getLinea()
					.getCodlinea(), lineasRC.getLinea().getCodplan());
			modifLineaRC.setLinea(linea);

			EspeciesRC especiesRC = (EspeciesRC) this.lineasRCDao.get(
					EspeciesRC.class, lineasRC.getEspeciesRC().getCodespecie());
			modifLineaRC.setEspeciesRC(especiesRC);
			
			modifLineaRC.setCodespecie(lineasRC.getCodespecie());
			modifLineaRC.setCodregimen(lineasRC.getCodregimen());
			modifLineaRC.setCodtipocapital(lineasRC.getCodtipocapital());

			modifLineaRC = (LineasRC) this.lineasRCDao.saveOrUpdate(modifLineaRC);
			
			modifLineaRC.setDescespecie(lineasRC.getDescespecie());
			modifLineaRC.setDescregimen(lineasRC.getDescregimen());
			modifLineaRC.setDesctipocapital(lineasRC.getDesctipocapital());

		} catch (Exception ex) {

			logger.error("modificarLineaRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar modificarLineaRC:", ex);
		}

		return modifLineaRC;
	}

	@Override
	public void borrarLineaRC(final Long idLineaRC) throws BusinessException {

		try {

			LineasRC lineaRC = (LineasRC) this.lineasRCDao.get(LineasRC.class,
					idLineaRC);

			this.lineasRCDao.delete(lineaRC);

		} catch (Exception ex) {

			logger.error("borrarLineaRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar borrarLineaRC:", ex);
		}
	}
	
	@Override
	public void replicaLineaRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException {

		try {

			this.lineasRCDao.replicaLineaRC(planOrig, lineaOrig, planDest,
					lineaDest);

		} catch (Exception ex) {

			logger.error("replicaLineaRC error. " + ex);
			throw new BusinessException(
					"Se ha producido al ejecutar replicaLineaRC:", ex);
		}
	}

	@Override
	public String getTablaLineasRC(final HttpServletRequest request,
			final HttpServletResponse response, final LineasRC lineasRC,
			final String origenLlamada) throws BusinessException {

		try {

			TableFacade tableFacade = crearTableFacade(request, response,
					lineasRC, origenLlamada);

			Limit limit = tableFacade.getLimit();

			LineasRCFilter consultaFilter = getConsultaLineasRCFilter(limit);

			setDataAndLimitVariables(tableFacade, consultaFilter, limit);

			String listaIdsTodos = getlistaIdsTodos(consultaFilter);
			String script = "<script>$(\"#listaIdsTodos\").val(\""
					+ listaIdsTodos + "\");</script>";

			tableFacade.setToolbar(new CustomToolbar());

			tableFacade.setView(new CustomView());

			return html(tableFacade) + script;

		} catch (Exception ex) {

			logger.error("getTablaDocumentos error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getTablaDocumentos:", ex);
		}
	}

	private TableFacade crearTableFacade(final HttpServletRequest request,
			final HttpServletResponse response, final LineasRC lineasRC,
			final String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(this.id,
				request);

		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("listaLineasRC_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listaLineasRC_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(columnas, lineasRC, tableFacade);
			}
		}

		return tableFacade;
	}

	private HashMap<String, String> cargarColumnas(final TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (this.columnas.size() == 0) {
			this.columnas.put(ID_STR, "id");
			this.columnas.put(PLAN, "linea.codplan");
			this.columnas.put(LINEA, "linea.codlinea");
			this.columnas.put(DES_LINEA, "linea.nomlinea");
			this.columnas.put(COD_ESPECIE, "codespecie");
			this.columnas.put(DES_ESPECIE, "descespecie");
			this.columnas.put(COD_REGIMEN, "codregimen");
			this.columnas.put(DES_REGIMEN, "descregimen");
			this.columnas.put(COD_TIPOCAPITAL, "codtipocapital");
			this.columnas.put(DES_TIPOCAPITAL, "desctipocapital");
			this.columnas.put(COD_ESPECIE_RC, "especiesRC.codespecie");
			this.columnas.put(DES_ESPECIE_RC, "especiesRC.descripcion");
		}
		tableFacade.setColumnProperties(this.columnas.get(ID_STR),
				this.columnas.get(PLAN), this.columnas.get(LINEA),
				this.columnas.get(COD_ESPECIE), this.columnas.get(DES_ESPECIE),
				this.columnas.get(COD_REGIMEN), this.columnas.get(DES_REGIMEN),
				this.columnas.get(COD_TIPOCAPITAL),
				this.columnas.get(DES_TIPOCAPITAL),
				this.columnas.get(DES_ESPECIE_RC));

		return this.columnas;
	}

	private void cargarFiltrosBusqueda(final HashMap<String, String> columnas,
			final LineasRC lineasRC, final TableFacade tableFacade) {
		// PLAN
		if (lineasRC.getLinea() != null
				&& FiltroUtils.noEstaVacio(lineasRC.getLinea().getCodplan()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(PLAN), lineasRC.getLinea()
									.getCodplan().toString()));
		// LINEA
		if (lineasRC.getLinea() != null
				&& FiltroUtils.noEstaVacio(lineasRC.getLinea().getCodlinea()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(LINEA), lineasRC.getLinea()
									.getCodlinea().toString()));
		// ESPECIE
		if (FiltroUtils.noEstaVacio(lineasRC.getCodespecie()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_ESPECIE), lineasRC
									.getCodespecie().toString()));
		// REGIMEN
		if (FiltroUtils.noEstaVacio(lineasRC.getCodregimen()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_REGIMEN), lineasRC
									.getCodregimen().toString()));
		// TIPO CAPITAL
		if (FiltroUtils.noEstaVacio(lineasRC.getCodtipocapital()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_TIPOCAPITAL), lineasRC
									.getCodtipocapital().toString()));
		// ESPECIE PARA RC
		if (lineasRC.getEspeciesRC() != null
				&& FiltroUtils.noEstaVacio(lineasRC.getEspeciesRC()
						.getCodespecie()))
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(COD_ESPECIE_RC), lineasRC
									.getEspeciesRC().getCodespecie()));
	}

	private LineasRCFilter getConsultaLineasRCFilter(final Limit limit) {
		LineasRCFilter consultaFilter = new LineasRCFilter();
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
			final LineasRCFilter consultaFilter, final Limit limit) {

		Collection<LineasRC> items = new ArrayList<LineasRC>();
		try {

			int totalRows = getLineasRCCountWithFilter(consultaFilter);
			logger.debug("********** count filas para LineasRC = " + totalRows
					+ " **********");

			tableFacade.setTotalRows(totalRows);

			LineasRCSort consultaSort = getConsultaLineasRCSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getLineasRCWithFilterAndSort(consultaFilter, consultaSort,
					rowStart, rowEnd);
			logger.debug("********** list items para LineasRC = "
					+ items.size() + " **********");

		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	private LineasRCSort getConsultaLineasRCSort(final Limit limit) {
		LineasRCSort consultaSort = new LineasRCSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}
		return consultaSort;
	}

	public String getlistaIdsTodos(final LineasRCFilter consultaFilter)
			throws DAOException {

		String listaIdsTodos = this.lineasRCDao
				.getlistaIdsTodos(consultaFilter);
		return listaIdsTodos;
	}

	@Override
	public Collection<LineasRC> getLineasRCWithFilterAndSort(
			final LineasRCFilter filter, final LineasRCSort sort,
			final int rowStart, final int rowEnd) throws BusinessException {

		try {

			return this.lineasRCDao.getLineasRCWithFilterAndSort(filter, sort,
					rowStart, rowEnd);

		} catch (Exception ex) {

			logger.error("getDocsAgroseguroWithFilterAndSort error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getDocsAgroseguroWithFilterAndSort:",
					ex);
		}
	}

	@Override
	public int getLineasRCCountWithFilter(final LineasRCFilter filter)
			throws BusinessException {

		try {

			return this.lineasRCDao.getLineasRCCountWithFilter(filter);

		} catch (Exception ex) {

			logger.error("getLineasRCCountWithFilter error. " + ex);
			throw new BusinessException(
					"Se ha producido al obtener getLineasRCCountWithFilter:",
					ex);
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
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer()
					.setCellEditor(new CellEditor() {

						public Object getValue(final Object item,
								final String property, final int rowcount) {

							HtmlBuilder html = new HtmlBuilder();

							Long id = (Long) new BasicCellEditor().getValue(
									item, "id", rowcount);
							BigDecimal plan = (BigDecimal) new BasicCellEditor()
									.getValue(item, "linea.codplan", rowcount);
							BigDecimal linea = (BigDecimal) new BasicCellEditor()
									.getValue(item, "linea.codlinea", rowcount);
							String desclinea = (String) new BasicCellEditor()
									.getValue(item, "linea.nomlinea", rowcount);
							BigDecimal codespecie = (BigDecimal) new BasicCellEditor()
									.getValue(item, "codespecie", rowcount);
							String descespecie = (String) new BasicCellEditor()
									.getValue(item, "descespecie", rowcount);
							BigDecimal codregimen = (BigDecimal) new BasicCellEditor()
									.getValue(item, "codregimen", rowcount);
							String descregimen = (String) new BasicCellEditor()
									.getValue(item, "descregimen", rowcount);
							BigDecimal codtipocapital = (BigDecimal) new BasicCellEditor()
									.getValue(item, "codtipocapital", rowcount);
							String desctipocapital = (String) new BasicCellEditor()
									.getValue(item, "desctipocapital", rowcount);
							String especiesRC = (String) new BasicCellEditor()
									.getValue(item, "especiesRC.codespecie",
											rowcount);

							// boton editar
							html.a()
									.href()
									.quote()
									.append("javascript:editar(" + id + ", "
											+ plan + ", " + linea + ", '"
											+ desclinea + "', " + codespecie
											+ ", '" + descespecie + "', "
											+ codregimen + ", '" + descregimen
											+ "', " + codtipocapital + ", '"
											+ desctipocapital + "', '"
											+ especiesRC + "');").quote()
									.close();
							html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Línea para RC\" title=\"Editar Línea para RC\"/>");
							html.aEnd();
							html.append("&nbsp;");

							// boton borrar
							html.a().href().quote()
									.append("javascript:borrar(" + id + ");")
									.quote().close();
							html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Línea para RC\" title=\"Borrar Línea para RC\"/>");
							html.aEnd();
							html.append("&nbsp;");

							return html.toString();
						}
					});
		}

		return tableFacade.render();
	}

	private void configurarColumnas(final HtmlTable table) {

		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false,
				false, "10%");
		configColumna(table, columnas.get(PLAN), "Plan", true, true, "7%");
		configColumna(table, columnas.get(LINEA), "Línea", true, true, "7%");
		configColumna(table, columnas.get(COD_ESPECIE), "Cód. Especie", true,
				true, "8%");
		configColumna(table, columnas.get(DES_ESPECIE), "Especie", false, true,
				"13%");
		configColumna(table, columnas.get(COD_REGIMEN), "Cód. Régimen", true,
				true, "8%");
		configColumna(table, columnas.get(DES_REGIMEN), "Régimen", false, true,
				"13%");
		configColumna(table, columnas.get(COD_TIPOCAPITAL), "Cód. TC", true,
				true, "7%");
		configColumna(table, columnas.get(DES_TIPOCAPITAL), "Tipo Capital",
				false, true, "13%");
		configColumna(table, columnas.get(DES_ESPECIE_RC), "Especie RC", false,
				true, "14%");
	}

	private void configColumna(final HtmlTable table, final String idCol,
			final String title, final boolean filterable,
			final boolean sortable, final String width) {

		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setLineasRCDao(final ILineasRCDao lineasRCDao) {
		this.lineasRCDao = lineasRCDao;
	}

	public void setLineaDao(final ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}		
}