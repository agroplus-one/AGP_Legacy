package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
/**
 * P0073325 - RQ.10, RQ.11 y RQ.12
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
/**
 * P0073325 - RQ.10, RQ.11 y RQ.12
 */
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
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
import org.jmesa.view.editor.NumberCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IListadoPolizaSbpDao;
import com.rsi.agp.core.jmesa.filter.ListadoPolizaSbpFilter;
import com.rsi.agp.core.jmesa.service.IListadoPolizaSbpService;
import com.rsi.agp.core.jmesa.sort.ListadoPolizaSbpSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public class ListadoPolizaSbpService implements IListadoPolizaSbpService {

	private static final String POLIZA_PPAL_COL_TOM_ID_COD_ENT = "polizaPpal.colectivo.tomador.id.codentidad";
	/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
	private static final String POLIZA_PPAL_COL_SUBENTMED_ID_COD_ENT = "polizaPpal.colectivo.subentidadMediadora.id.codentidad";
	private static final String POLIZA_PPAL_COL_SUBENTMED_ID_COD_SUBENT = "polizaPpal.colectivo.subentidadMediadora.id.codsubentidad";
	/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Fin */
	private static final String POLIZA_PPAL_OFICINA = "polizaPpal.oficina";
	private static final String USUARIO_PROVISIONAL = "usuarioProvisional";
	private static final String POLIZA_PPAL_LIN_COD_PLAN = "polizaPpal.linea.codplan";
	private static final String POLIZA_PPAL_LIN_COD_LIN = "polizaPpal.linea.codlinea";
	private static final String POLIZA_PPAL_COL_ID_COL = "polizaPpal.colectivo.idcolectivo";
	private static final String REFERENCIA = "referencia";
	private static final String POLIZA_PPAL_AS_NIF_CIF = "polizaPpal.asegurado.nifcif";
	private static final String POLIZA_PPAL_CLASE = "polizaPpal.clase";
	private static final String POLIZA_PPAL_COD_MODULO = "polizaPpal.codmodulo";
	private static final String POLIZA_PPAL_EP_ID_ESTADO = "polizaPpal.estadoPoliza.idestado";
	private static final String POLIZA_CPL_EP_ID_ESTADO = "polizaCpl.estadoPoliza.idestado";
	private static final String INC_SBP_COMP = "incSbpComp";
	private static final String TIPO_ENVIO_DESCRIPCION = "tipoEnvio.descripcion";
	private static final String FECHA_ENVIO_SBP = "fechaEnvioSbp";
	private static final String IMPORTE = "importe";
	private static final String ESTADO_PLZ_SBP_ID_ESTADO = "estadoPlzSbp.idestado";
	private static final String ERROR_SBPS = "errorSbps";
	private static final String REF_PLZ_OMEGA = "refPlzOmega";
	private static final String N_SOLICITUD = "nSolicitud";

	/**
	* P0073325 -RQ.10, RQ.11 y RQ.12
	*/
	private static final String GED_DOC_POLIZA_SBP_DOC_FIRMADA = "gedDocPolizaSbp.docFirmada";
	private static final String GED_DOC_POLIZA_SBP_CANAL_FIRMA_NOMBRE_CANAL = "gedDocPolizaSbp.canalFirma.nombreCanal";
	private static final String GED_DOC_POLIZA_SBP_COD_USUARIO = "gedDocPolizaSbp.codUsuario";
	private static final String GED_DOC_POLIZA_SBP_FECHA_FIRMA = "gedDocPolizaSbp.fechaFirma";
	private static final String S = "S";
	private static final String NO = "No";
	private static final String SI = "Sí";
	private static final String CANAL_FIRMA = "canalFirma";
	private static final String DOC_FIRMADA = "docFirmada";
	private static final String COL_CANAL_FIRMA = "Canal Firma";
	private static final String COL_DOC_FIRMADA = "Doc Firmada";
	private static final String NBSP = "&nbsp;";
	private static final String USUARIO_FIRMA = "usuarioFirma";
	private static final String COL_USUARIO_FIRMA = "Usuario Firma";
	private static final String FECHA_FIRMA = "fechaFirma";
	private static final String COL_FECHA_FIRMA = "Fecha firma";

	private IListadoPolizaSbpDao listadoPolizaSbpDao;

	private Log logger = LogFactory.getLog(getClass());
	
	private ListadoPolizaSbpFilter listadoFilter;
	private ListadoPolizaSbpSort listadoPolizasSbpSort;
	
	
	/**
	 * Metodo para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	public String html(TableFacade tableFacade, String perfil) {
		Limit limit = tableFacade.getLimit();
		if (limit.hasExport()) { // INFORMES
			Table table = tableFacade.getTable();
			// eliminamos la columna "Id"
			eliminarColumnaId(tableFacade, table);

			// configuramos cada columna
			configurarColumnasExport(table);

			// renombramos las cabeceras
			configurarCabecerasColumnasExport(table);

			tableFacade.render();
			return null;
		} else {

			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");

			// Defino los titulos de la cabecera
			configurarCabecerasColumnas(table);

			// configuramos cada columna
			configurarColumnas(table, perfil);

			return tableFacade.render(); // Return the Html.
		}
	}

	/**
	 * Metodo que calcula las filas que cumplen el filtro
	 * 
	 * @param tableFacade
	 * @param filtrarDetalle
	 *            -> recibe el valor del filtrado por detalle
	 */
	public void setDataAndLimitVariables(TableFacade tableFacade, String filtrarDetalle,
			List<BigDecimal> listaGrupoEntidades, List<BigDecimal> listaGrupoOficinas) {
		Limit limit = tableFacade.getLimit();
		listadoFilter = getListadoPolizasSbpFilter(limit, listaGrupoEntidades,
				listaGrupoOficinas);
		// Obtenemos el numero de filas que cumplen el filtro
		int totalRows = this.getListadoPolizaSbpCountWithFilter(listadoFilter, filtrarDetalle);
		logger.debug("********** count filas = " + totalRows + " **********");
		// y lo establecemos al tableFacade antes de obtener la fila de inicio y la de
		// fin
		tableFacade.setTotalRows(totalRows);

		listadoPolizasSbpSort = getListadoPolizasSbpSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();
		Collection<PolizaSbp> items = new ArrayList<PolizaSbp>();
		try {
			// Obtenemos las polizas Sbp que cumplen el filtro
			items = this.getListadoPolizasSbpWithFilterAndSort(listadoFilter, listadoPolizasSbpSort, rowStart, rowEnd,
					filtrarDetalle);
			logger.debug("********** list items  = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		tableFacade.setItems(items);
	}

	/**
	 * configura los nombres de las columnas y sus propiedades
	 * 
	 * @param table
	 */
	private void configurarCabecerasColumnas(HtmlTable table) {
		table.getRow().getColumn("id").setTitle("&nbsp;&nbsp;Acciones");
		table.getRow().getColumn("id").setFilterable(false);
		table.getRow().getColumn("id").setSortable(false);
		table.getRow().getColumn("id").setWidth("80px");// 6
		// 1
		table.getRow().getColumn(POLIZA_PPAL_COL_TOM_ID_COD_ENT).setTitle("Ent");
		table.getRow().getColumn(POLIZA_PPAL_COL_TOM_ID_COD_ENT).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_TOM_ID_COD_ENT).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_TOM_ID_COD_ENT).setWidth("45px");

		// Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
		// 2 - Entidad Mediadora
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_ENT).setTitle("Ent. Med.");
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_ENT).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_ENT).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_ENT).setWidth("45px");

		// 3 - SubEntidad Mediadora.
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_SUBENT).setTitle("SubEnt. Med.");
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_SUBENT).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_SUBENT).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_SUBENT).setWidth("45px");
		// Pet. 79014 ** MODIF TAM (17.03.2022) ** Fin */

		// 4
		table.getRow().getColumn(POLIZA_PPAL_OFICINA).setTitle("Ofi");
		table.getRow().getColumn(POLIZA_PPAL_OFICINA).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_OFICINA).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_OFICINA).setWidth("45px");
		// 5
		table.getRow().getColumn(USUARIO_PROVISIONAL).setTitle("Usuario");
		table.getRow().getColumn(USUARIO_PROVISIONAL).setFilterable(true);
		table.getRow().getColumn(USUARIO_PROVISIONAL).setSortable(true);
		table.getRow().getColumn(USUARIO_PROVISIONAL).setWidth("60px");
		// 6
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_PLAN).setTitle("Plan");
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_PLAN).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_PLAN).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_PLAN).setWidth("40px");
		// 7
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_LIN).setTitle("L&iacute;nea");
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_LIN).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_LIN).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_LIN_COD_LIN).setWidth("37px");
		// 8
		table.getRow().getColumn(POLIZA_PPAL_COL_ID_COL).setTitle("Colectivo");
		table.getRow().getColumn(POLIZA_PPAL_COL_ID_COL).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_ID_COL).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_COL_ID_COL).setWidth("80px");
		// 9
		table.getRow().getColumn(REFERENCIA).setTitle("Referencia");
		table.getRow().getColumn(REFERENCIA).setFilterable(true);
		table.getRow().getColumn(REFERENCIA).setSortable(true);
		table.getRow().getColumn(REFERENCIA).setWidth("80px");
		// 10
		table.getRow().getColumn(POLIZA_PPAL_AS_NIF_CIF).setTitle("NIF/CIF");
		table.getRow().getColumn(POLIZA_PPAL_AS_NIF_CIF).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_AS_NIF_CIF).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_AS_NIF_CIF).setWidth("80px");
		// 11
		table.getRow().getColumn(POLIZA_PPAL_CLASE).setTitle("CL");
		table.getRow().getColumn(POLIZA_PPAL_CLASE).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_CLASE).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_CLASE).setWidth("35px");
		// 12
		table.getRow().getColumn(POLIZA_PPAL_COD_MODULO).setTitle("M&oacute;d");
		table.getRow().getColumn(POLIZA_PPAL_COD_MODULO).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_COD_MODULO).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_COD_MODULO).setWidth("35px");
		// 13
		table.getRow().getColumn(POLIZA_PPAL_EP_ID_ESTADO)
				.setTitle("<span id='imp' title=\"Estado Principal\">E.P</span>");
		table.getRow().getColumn(POLIZA_PPAL_EP_ID_ESTADO).setFilterable(true);
		table.getRow().getColumn(POLIZA_PPAL_EP_ID_ESTADO).setSortable(true);
		table.getRow().getColumn(POLIZA_PPAL_EP_ID_ESTADO).setWidth("35px");
		// 14
		table.getRow().getColumn(POLIZA_CPL_EP_ID_ESTADO)
				.setTitle("<span id='imp' title=\"Estado Complementaria\">E.C</span>");
		table.getRow().getColumn(POLIZA_CPL_EP_ID_ESTADO).setFilterable(true);
		table.getRow().getColumn(POLIZA_CPL_EP_ID_ESTADO).setSortable(true);
		table.getRow().getColumn(POLIZA_CPL_EP_ID_ESTADO).setWidth("35px");
		// 15
		table.getRow().getColumn(INC_SBP_COMP)
				.setTitle("<span id='imp' title=\"Complementaria en Sobreprecio\">C.S</span>");
		table.getRow().getColumn(INC_SBP_COMP).setFilterable(true);
		table.getRow().getColumn(INC_SBP_COMP).setSortable(true);
		table.getRow().getColumn(INC_SBP_COMP).setWidth("35px");
		// 16
		table.getRow().getColumn(TIPO_ENVIO_DESCRIPCION).setTitle("Tipo Env&iacute;o");
		table.getRow().getColumn(TIPO_ENVIO_DESCRIPCION).setFilterable(true);
		table.getRow().getColumn(TIPO_ENVIO_DESCRIPCION).setSortable(true);
		table.getRow().getColumn(TIPO_ENVIO_DESCRIPCION).setWidth("50px");
		// 17
		table.getRow().getColumn(FECHA_ENVIO_SBP).setTitle("Fec. Env&iacute;o Sbp");
		table.getRow().getColumn(FECHA_ENVIO_SBP).setFilterable(true);
		table.getRow().getColumn(FECHA_ENVIO_SBP).setSortable(true);
		table.getRow().getColumn(FECHA_ENVIO_SBP).setWidth("55px");
		table.getRow().getColumn(FECHA_ENVIO_SBP).getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));
		// 18
		table.getRow().getColumn(IMPORTE).setTitle("Importe");
		table.getRow().getColumn(IMPORTE).setFilterable(true);
		table.getRow().getColumn(IMPORTE).setSortable(true);
		table.getRow().getColumn(IMPORTE).setWidth("55px");
		table.getRow().getColumn(IMPORTE).getCellRenderer().setCellEditor(new NumberCellEditor());
		// 19
		table.getRow().getColumn(ESTADO_PLZ_SBP_ID_ESTADO).setTitle("Estado Sbp");
		table.getRow().getColumn(ESTADO_PLZ_SBP_ID_ESTADO).setFilterable(true);
		table.getRow().getColumn(ESTADO_PLZ_SBP_ID_ESTADO).setSortable(true);
		table.getRow().getColumn(ESTADO_PLZ_SBP_ID_ESTADO).setWidth("40px");
		// 20
		table.getRow().getColumn(ERROR_SBPS).setTitle("Detalle");
		table.getRow().getColumn(ERROR_SBPS).setFilterable(true);
		table.getRow().getColumn(ERROR_SBPS).setSortable(false);
		table.getRow().getColumn(ERROR_SBPS).setWidth("100px");
		// 21
		table.getRow().getColumn(REF_PLZ_OMEGA).setTitle("Ref. OMEGA");
		table.getRow().getColumn(REF_PLZ_OMEGA).setFilterable(true);
		table.getRow().getColumn(REF_PLZ_OMEGA).setSortable(false);
		table.getRow().getColumn(REF_PLZ_OMEGA).setWidth("60px");
		// 22
		table.getRow().getColumn(N_SOLICITUD).setTitle("N&#176; Sol.");
		table.getRow().getColumn(N_SOLICITUD).setFilterable(true);
		table.getRow().getColumn(N_SOLICITUD).setSortable(true);
		table.getRow().getColumn(N_SOLICITUD).setWidth("50px");
		
	}

	/**
	 * Metodo que pinta las columnas
	 * 
	 * @param table
	 * @param perfil
	 */
	@SuppressWarnings("deprecation")
	private void configurarColumnas(HtmlTable table, String perfil) {
		// campo acciones
		table.getRow().getColumn("id").getCellRenderer().setCellEditor(getCellEditorAcciones(perfil));

		// campo colectivo - dc
		table.getRow().getColumn(POLIZA_PPAL_COL_ID_COL).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String idCol, dc;
				idCol = (String) new BasicCellEditor().getValue(item, POLIZA_PPAL_COL_ID_COL, rowcount);
				dc = (String) new BasicCellEditor().getValue(item, "polizaPpal.colectivo.dc", rowcount);
				String value = idCol + "-" + dc;
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		});
		// campo referencia - dc
		table.getRow().getColumn(REFERENCIA).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String ref = " ";
				BigDecimal dc;
				String value = "";
				ref = (String) new BasicCellEditor().getValue(item, REFERENCIA, rowcount);
				if (ref != null) {
					dc = (BigDecimal) new BasicCellEditor().getValue(item, "polizaPpal.dc", rowcount);
					if (dc != null) {
						value = ref + "-" + dc;
					} else {
						value = ref;
					}
				} else {
					value = NBSP;
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		});
		// campo estado poliza PPal
		table.getRow().getColumn(POLIZA_PPAL_EP_ID_ESTADO).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Poliza polizaPpal = (Poliza) new BasicCellEditor().getValue(item, "polizaPpal", rowcount);
				String value = " ";
				if (polizaPpal.getEstadoPoliza().getIdestado() != null) {
					switch (polizaPpal.getEstadoPoliza().getIdestado().intValue()) {
					case 1: // pendiente validacion
						value = "<span id='imp' title=\"Pendiente de Validaci&oacute;n\">V</span>";
						break;
					case 2:// grab prov
						value = "<span id='imp' title=\"Grabaci&oacute;n Provisional\">P</span>";
						break;
					case 3: // grab def
						value = "<span id='imp' title=\"Grabaci&oacute;n Definitiva\">D</span>";
						break;
					case 4: // anulada
						value = "<span id='imp' title=\"Anulada\">A</span>";
						break;
					case 5: // enviada pendiente confirmar
						value = "<span id='imp' title=\"Enviada pendiente de confirmar\">EP</span>";
						break;
					case 7: // enviada erronea
						value = "<span id='imp' title=\"Enviada err&oacute;nea\">EE</span>";
						break;
					case 8: // enviada correcta
						value = "<span id='imp' title=\"Enviada correcta\">E</span>";
						break;
					default: // por defecto
						value = NBSP;
						break;
					}
				} else {
					value = NBSP;
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		});
		// campo estado poliza Cpl
		table.getRow().getColumn(POLIZA_CPL_EP_ID_ESTADO).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Poliza polizaCpl = (Poliza) new BasicCellEditor().getValue(item, "polizaCpl", rowcount);
				String value = "";
				if (polizaCpl != null) {
					if (polizaCpl.getEstadoPoliza().getIdestado() != null) {
						switch (polizaCpl.getEstadoPoliza().getIdestado().intValue()) {
						case 1: // pendiente validacion
							value = "<span id='imp' title=\"Pendiente de Validaci&oacute;n\">V</span>";
							break;
						case 2:// grab prov
							value = "<span id='imp' title=\"Grabaci&oacute;n Provisional\">P</span>";
							break;
						case 3: // grab def
							value = "<span id='imp' title=\"Grabaci&oacute;n Definitiva\">D</span>";
							break;
						case 4: // anulada
							value = "<span id='imp' title=\"Anulada\">A</span>";
							break;
						case 5: // enviada pendiente confirmar
							value = "<span id='imp' title=\"Enviada pendiente de confirmar\">EP</span>";
							break;
						case 7: // enviada erronea
							value = "<span id='imp' title=\"Enviada err&oacute;nea\">EE</span>";
							break;
						case 8: // enviada correcta
							value = "<span id='imp' title=\"Enviada correcta\">E</span>";
							break;
						default: // por defecto. Sin complementaria
							value = "<span id='imp' title=\"Sin Complementaria\"> N</span>";
							break;
						}
					}
				}

				HtmlBuilder html = new HtmlBuilder();
				if (!value.equals("")) {
					html.append(value);
				} else {
					html.append("<span id='imp' title=\"Sin Complementaria\"> N</span>");
				}

				return html.toString();
			}
		});
		// campo incSbpComp
		table.getRow().getColumn(INC_SBP_COMP).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String incSbpComp = StringUtils
						.nullToString((Character) new BasicCellEditor().getValue(item, INC_SBP_COMP, rowcount));
				String value = "";
				HtmlBuilder html = new HtmlBuilder();
				if (S.equals(incSbpComp)) {
					value = "<span id='imp' title=\"Si Complementaria en Sbp\"> S</span>";
				} else if ("N".equals(incSbpComp)) {
					value = "<span id='imp' title=\"No Complementaria en Sbp\"> N</span>";
				}
				html.append(value);
				return html.toString();
			}
		});
		// campo importe
		table.getRow().getColumn(IMPORTE).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal importe = (BigDecimal) new BasicCellEditor().getValue(item, IMPORTE, rowcount);
				HtmlBuilder html = new HtmlBuilder();
				if (importe != null)
					html.append(importe);
				return html.toString();
			}
		});
		// campo estado poliza Sbp
		table.getRow().getColumn(ESTADO_PLZ_SBP_ID_ESTADO).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String acronimo = "";
				String value = "";
				String descEstadoSbp = StringUtils.nullToString(
						(String) new BasicCellEditor().getValue(item, "estadoPlzSbp.descEstado", rowcount));

				acronimo = obtenerAcronimoDesc(descEstadoSbp);

				/* Pet. 79014 ** MODIF TAM (15.03.2022) ** Inicio */
				value = "<span id='imp' title=\"" + descEstadoSbp + "\">" + acronimo + "</span>";

				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
				/* Pet. 79014 ** MODIF TAM (15.03.2022) ** Fin */
			}
		});
		// campo detalle
		table.getRow().getColumn(ERROR_SBPS).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String errorStr = (String) new BasicCellEditor().getValue(item, "errorPlzSbp.errorSbp.descError",
						rowcount);
				String miniErrorStr = NBSP;
				HtmlBuilder html = new HtmlBuilder();
				if (errorStr != null && errorStr.length() > 15) {
					miniErrorStr = errorStr.substring(0, 15);
				}
				if (errorStr != null && !errorStr.isEmpty()) {
					html.append("<span title=\"" + errorStr + "\">" + miniErrorStr + "</span>");
				} else {
					html.append(miniErrorStr);
				}
				return html.toString();
			}
		});
	}

	/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
	private String obtenerAcronimoDesc(String DescEstadoPolSbp) {
		String acronimo = "";
		if (DescEstadoPolSbp.indexOf("Provisional") > 0) {
			// Grabada Provisional (P): grabada, pero no marcada para el envío.
			acronimo = "P";
		} else if (DescEstadoPolSbp.indexOf("Definitiva") > 0) {
			// Grabada Definitiva (D): grabada y marcada para el envío.
			acronimo = "D";
		} else if (DescEstadoPolSbp.indexOf("Pendiente") > 0) {
			// Enviada Pendiente Aceptación (EP): antes de procesar Acuse de Recibo.
			acronimo = "EP";
		} else if (DescEstadoPolSbp.indexOf("Errónea") > 0) {
			// Enviada Errónea (EE): enviada a OMEGA y con Acuse de Recibo de rechazo.
			acronimo = "EE";
		} else if (DescEstadoPolSbp.indexOf("Correcta") > 0) {
			// Enviada Correcta(E)
			acronimo = "E";
		} else if (DescEstadoPolSbp.indexOf("Anulada") > 0) {
			// Anulada(A)
			acronimo = "A";
		}

		return acronimo;
	}

	/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
	/**
	 * DAA 12/06/2013 Devuelve el objeto que muestra la informacion de la columna
	 * 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final String perfil) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Long idPolizaSbpLong = (Long) new BasicCellEditor().getValue(item, "id", rowcount);
				Poliza polizaPpal = (Poliza) new BasicCellEditor().getValue(item, "polizaPpal", rowcount);
				Poliza polizaCpl = (Poliza) new BasicCellEditor().getValue(item, "polizaCpl", rowcount);
				BigDecimal idTipoEnvio = (BigDecimal) new BasicCellEditor().getValue(item, "tipoEnvio.id", rowcount);

				String estadoPpal = "";
				String estadoCpl = "";
				String idPolPpal = polizaPpal.getIdpoliza().toString();
				String idPolCpl = "";
				/**
				* P0073325 -RQ.10, RQ.11 y RQ.12
				*/
				String docFirmada = StringUtils.nullToString((Character) new BasicCellEditor().getValue(item, GED_DOC_POLIZA_SBP_DOC_FIRMADA, rowcount));

				if (polizaPpal.getEstadoPoliza().getIdestado() != null) {
					estadoPpal = polizaPpal.getEstadoPoliza().getIdestado().toString();
				}

				if (polizaCpl != null) {
					if (polizaCpl.getEstadoPoliza().getIdestado() != null) {
						estadoCpl = polizaCpl.getEstadoPoliza().getIdestado().toString();
					}
					idPolCpl = polizaCpl.getIdpoliza().toString();
				}

				String incSbpComp = StringUtils
						.nullToString((Character) new BasicCellEditor().getValue(item, INC_SBP_COMP, rowcount));
				String idPolizaSbp = idPolizaSbpLong.toString();
				BigDecimal estadoSbp = (BigDecimal) new BasicCellEditor().getValue(item, ESTADO_PLZ_SBP_ID_ESTADO,
						rowcount);

				HtmlBuilder html = new HtmlBuilder();
				// boton editar
				if (estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_DEF) || estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_PROV)
						|| estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_ERRONEA)) {
					if (idTipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_PRINCIPAL)) {
						html.a().href().quote()
								.append("javascript:editar('" + idPolizaSbp + "','" + idPolPpal + "','" + idPolCpl
										+ "','" + estadoPpal + "','" + estadoCpl + "','" + incSbpComp + "','"
										+ estadoSbp + "','" + idTipoEnvio + "');")
								.quote().close();
						html.append(
								"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar p&oacute;liza Sbp\" title=\"Editar p&oacute;liza Sbp\"/>");
						html.aEnd();
						html.append(NBSP);
					}
				}
				
				/**
				 * 79222_3
				 */
				if (estadoSbp != null) {
					
					BigDecimal codPerfil = (BigDecimal) new BasicCellEditor().getValue(item, "polizaPrincipal.usuario.tipousuario", rowcount);
					System.out.println("codperfil usu: "+ codPerfil);
					
					if (Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(perfil)) {
						if (!idTipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO)) {
							if (estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_CORRECTA)) {
	
								html.a().href().quote()
								.append("javascript:altaSuplementoSbp('" + idPolizaSbp + "');")
								.quote().close();
								html.append(
								"<span id='imp' style='font-size: 150%;text-decoration:none;font-weight:bold;color: #FF0000' title=\"Alta suplemento Sbp\">A</span>");
								html.aEnd();
								html.append("&nbsp;&nbsp;");
							}
						}
					}
				}
				
				// boton borrar poliza Sbp
				if (estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_PROV) || estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_DEF)
						|| estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_ERRONEA)) {
					if (idTipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_PRINCIPAL)) {
						html.a().href().quote().append("javascript:borrar('" + idPolizaSbp + "');").quote().close();
						html.append(
								"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar p&oacute;liza Sbp\" title=\"Borrar p&oacute;liza Sbp\"/>");
						html.aEnd();
						html.append(NBSP);
					}
				}
				// boton mostrar detalle errores
				if (estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_ERRONEA)) {
					String errorStr = (String) new BasicCellEditor().getValue(item, "errorPlzSbp.errorSbp.descError",
							rowcount);
					html.a().href().quote().append("javascript:mostrarErroresDetalle('" + errorStr + "');").quote()
							.close();
					html.append(
							"<img src=\"jsp/img/displaytag/report.png\" alt=\"Mostrar Detalle\" title=\"Mostrar Detalle\"/>");
					html.aEnd();
					html.append(NBSP);
				}
				// boton imprimir
				if (!estadoSbp.equals(ConstantsSbp.ESTADO_ANULADA)) {
					html.a().href().quote().append("javascript:imprimir('" + idPolizaSbp + "');").quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"imprimir Simulaci&oacute;n Sbp\" title=\"imprimir Simulaci&oacute;n Sbp\"/>");
					html.aEnd();
					html.append(NBSP);
				}
				
				if (!idTipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO)) {
					// boton pasar a definitiva
					// Si el sbp estÃ¡ en estado 'Grabacion provisional' o 'Enviada erronea'
					if (estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_ERRONEA)
							|| estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_PROV)) {
						// Se obtiene el estado de la poliza asociada (la complementaria si estÃ¡
						// incluida o si no la principal)
						String estadoPoliza = (ConstantsSbp.INCL_SBP_SI.equals(incSbpComp) ? estadoCpl : estadoPpal);
						// Si el estado de la poliza asociada es 'Enviada correcta', 'Grabacion
						// definitiva' o 'Enviada pendiente de confirmacion'
						// se puede pasar a definitiva el sobreprecio
						if (Constants.ESTADO_POLIZA_DEFINITIVA.toString().equals(estadoPoliza)
								|| Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.toString().equals(estadoPoliza)
								|| Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR.toString().equals(estadoPoliza)) {
							html.a().href().quote().append("javascript:pasarADefinitiva('" + idPolizaSbp + "','"
									+ estadoPpal + "','" + estadoCpl + "','" + incSbpComp + "');").quote().close();
							html.append(
									"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar a Definitiva Sbp\" title=\"Pasar a Definitiva Sbp\"/>");
							html.aEnd();
						}
					}
				}
				

				if (estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_CORRECTA)
						&& Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(perfil)) {
					html.a().href().quote()
							.append("javascript:anularSbp('" + idPolizaSbp + "','" + polizaPpal.getReferencia() + "');")
							.quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/cancel.png\" alt=\"anular Simulaci&oacute;n Sbp\" title=\"anular Simulaci&oacute;n Sbp\"/>");
					html.aEnd();
					html.append(NBSP);
				}
				/**
				 * P0073325 - RQ.10, RQ.11 y RQ.12
				 */
				
				// boton cargar documentacion
				if (estadoSbp.equals((Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR)) && idTipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_PRINCIPAL)) {
					
					html.a().href().quote()
							//.append("javascript:anularSbp('" + idPolizaSbp + "','" + polizaPpal.getReferencia() + "');")
							.append("javascript:cargaDocFirmada('"+ idPolizaSbp + "','"+ polizaPpal.getReferencia() + "','" + polizaPpal.getLinea().getCodplan() + "','" + polizaPpal.getTipoReferencia() + "','1');")
							.quote().close();
					html.append(
					"<img src=\"jsp/img/jmesa/pdf.gif\" alt=\"Cargar documentaci&oacute;n \" title=\"Cargar documentaci&oacute;n\"/>");
					html.aEnd();
					html.append(NBSP);
				}
				return html.toString();
			}
		};
	}

	/**
	 * Metodo que configura los nombres de las columnas para los informes
	 * 
	 * @param table
	 */
	private void configurarCabecerasColumnasExport(Table table) {
		table.setCaption("LISTADO POLIZAS SOBREPRECIO");
		Row row = table.getRow();

		Column colEntidad = row.getColumn(POLIZA_PPAL_COL_TOM_ID_COD_ENT);
		colEntidad.setTitle("Entidad");

		/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
		Column colEntMed = row.getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_ENT);
		colEntMed.setTitle("Ent. Mediadora");

		Column colSubEntMed = row.getColumn(POLIZA_PPAL_COL_SUBENTMED_ID_COD_SUBENT);
		colSubEntMed.setTitle("SubEnt. Mediadora");
		/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Fin */

		Column colOficina = row.getColumn(POLIZA_PPAL_OFICINA);
		colOficina.setTitle("Oficina");

		Column colUsurio = row.getColumn(USUARIO_PROVISIONAL);
		colUsurio.setTitle("Usuario");

		Column colPlan = row.getColumn(POLIZA_PPAL_LIN_COD_PLAN);
		colPlan.setTitle("Plan");

		Column colLinea = row.getColumn(POLIZA_PPAL_LIN_COD_LIN);
		colLinea.setTitle("Linea");

		Column colIdColectivo = row.getColumn(POLIZA_PPAL_COL_ID_COL);
		colIdColectivo.setTitle("Colectivo");

		Column colReferencia = row.getColumn(REFERENCIA);
		colReferencia.setTitle("Referencia");

		Column colNifCif = row.getColumn(POLIZA_PPAL_AS_NIF_CIF);
		colNifCif.setTitle("NIF/CIF");

		Column colClase = row.getColumn(POLIZA_PPAL_CLASE);
		colClase.setTitle("Clase");

		Column colModulo = row.getColumn(POLIZA_PPAL_COD_MODULO);
		colModulo.setTitle("Modulo");

		Column colEstadoPpal = row.getColumn(POLIZA_PPAL_EP_ID_ESTADO);
		colEstadoPpal.setTitle("E.Ppal ");

		Column colEstadoCpl = row.getColumn(POLIZA_CPL_EP_ID_ESTADO);
		colEstadoCpl.setTitle("E.Cpl");

		Column colIncSbpCpl = row.getColumn(INC_SBP_COMP);
		colIncSbpCpl.setTitle("C.S.");

		Column colTipoEnvio = row.getColumn(TIPO_ENVIO_DESCRIPCION);
		colTipoEnvio.setTitle("Tipo Env.");

		Column colFechaEnvioSbp = row.getColumn(FECHA_ENVIO_SBP);
		colFechaEnvioSbp.setTitle("Fecha Envio Sbp");
		colFechaEnvioSbp.getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));

		Column colImporte = row.getColumn(IMPORTE);
		colImporte.setTitle("Importe");
		colImporte.getCellRenderer().setCellEditor(new NumberCellEditor());

		Column colEstadoSbp = row.getColumn(ESTADO_PLZ_SBP_ID_ESTADO);
		colEstadoSbp.setTitle("E.Sbp");

		Column colErrorSbps = row.getColumn(ERROR_SBPS);
		colErrorSbps.setTitle("Errores Sbp");

		Column colRefPlzOmega = row.getColumn(REF_PLZ_OMEGA);
		colRefPlzOmega.setTitle(REF_PLZ_OMEGA);

		// DAA 22/01/2013 "fechaGrabacion","prodTotal","sumAsegurada"
		Column colFechaDefinitiva = row.getColumn("fechaDefinitiva");
		colFechaDefinitiva.setTitle("Fec. Grabacion");
		colFechaDefinitiva.getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));

		Column colProdTotal = row.getColumn("prodTotal");
		colProdTotal.setTitle("Prod. Total");

		Column colSumAsegurada = row.getColumn("sumAsegurada");
		colSumAsegurada.setTitle("Suma Asegurada");

		// DNF 28/11/2018 columna del excell
		Column colNSol = row.getColumn(/* "polizaPpal.idpoliza" */ N_SOLICITUD);
		colNSol.setTitle("Num. Solicitud");
		
		/**
		 * P0073325 - RQ.10, RQ.11 y RQ.12
		 */
		Column colCanalFirma = row.getColumn(CANAL_FIRMA);
		colCanalFirma.setTitle(COL_CANAL_FIRMA);
		
		Column colDocFirmada = row.getColumn(DOC_FIRMADA);
		colDocFirmada.setTitle(COL_DOC_FIRMADA);
		
		Column colUsuarioFirma = row.getColumn(USUARIO_FIRMA);
		colUsuarioFirma.setTitle(COL_USUARIO_FIRMA);
		
		Column colFechaFirma = row.getColumn(FECHA_FIRMA);
		colFechaFirma.setTitle(COL_FECHA_FIRMA);
	}

	/**
	 * metodo que pinta las columnas para los informes
	 * 
	 * @param table
	 */
	@SuppressWarnings("deprecation")
	private void configurarColumnasExport(Table table) {
		// campo colectivo - dc
		table.getRow().getColumn(POLIZA_PPAL_COL_ID_COL).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String idCol, dc;
				idCol = (String) new BasicCellEditor().getValue(item, POLIZA_PPAL_COL_ID_COL, rowcount);
				dc = (String) new BasicCellEditor().getValue(item, "polizaPpal.colectivo.dc", rowcount);
				String value = idCol + "-" + dc;
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);

				return html.toString();
			}
		});
		// campo referencia - dc
		table.getRow().getColumn(REFERENCIA).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String ref = " ";
				BigDecimal dc;
				String value = "";
				ref = (String) new BasicCellEditor().getValue(item, REFERENCIA, rowcount);
				if (ref != null) {
					dc = (BigDecimal) new BasicCellEditor().getValue(item, "polizaPpal.dc", rowcount);
					if (dc != null) {
						value = ref + "-" + dc;
					} else {
						value = ref;
					}
				} else {
					value = "";
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		});
		// campo estado poliza PPal
		table.getRow().getColumn(POLIZA_PPAL_EP_ID_ESTADO).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Poliza polizaPpal = (Poliza) new BasicCellEditor().getValue(item, "polizaPpal", rowcount);
				String value = " ";
				if (polizaPpal.getEstadoPoliza().getIdestado() != null) {
					switch (polizaPpal.getEstadoPoliza().getIdestado().intValue()) {
					case 2:// grab prov
						value = "P";
						break;
					case 3: // grab def
						value = "D";
						break;
					case 4: // anulada
						value = "A";
						break;
					case 5: // enviada pendiente confirmar
						value = "EP";
						break;
					case 7: // enviada erronea
						value = "EE";
						break;
					case 8: // enviada correcta
						value = "E";
						break;
					default: // por defecto
						value = "";
						break;
					}
				} else {
					value = "";
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		});
		// campo estado poliza Cpl
		table.getRow().getColumn(POLIZA_CPL_EP_ID_ESTADO).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Poliza polizaCpl = (Poliza) new BasicCellEditor().getValue(item, "polizaCpl", rowcount);
				String value = "";
				if (polizaCpl != null) {
					if (polizaCpl.getEstadoPoliza().getIdestado() != null) {
						switch (polizaCpl.getEstadoPoliza().getIdestado().intValue()) {
						case 1: // pendiente validacion
							value = "V";
							break;
						case 2:// grab prov
							value = "P";
							break;
						case 3: // grab def
							value = "D";
							break;
						case 4: // anulada
							value = "A";
							break;
						case 5: // enviada pendiente confirmar
							value = "EP";
							break;
						case 7: // enviada erronea
							value = "EE";
							break;
						case 8: // enviada correcta
							value = "E";
							break;
						default: // por defecto. Sin complementaria
							value = "N";
							break;
						}
					}
				}

				HtmlBuilder html = new HtmlBuilder();
				if (!value.equals("")) {
					html.append(value);
				} else {
					html.append("N");
				}

				return html.toString();
			}
		});

		// campo estado poliza Sbp
		table.getRow().getColumn(ESTADO_PLZ_SBP_ID_ESTADO).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String descEstadoSbp = StringUtils.nullToString(
						(String) new BasicCellEditor().getValue(item, "estadoPlzSbp.descEstado", rowcount));
				HtmlBuilder html = new HtmlBuilder();
				html.append(descEstadoSbp);
				return html.toString();
			}
		});
		// campo importe
		table.getRow().getColumn(IMPORTE).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal importe = (BigDecimal) new BasicCellEditor().getValue(item, IMPORTE, rowcount);
				HtmlBuilder html = new HtmlBuilder();
				if (importe != null)
					html.append(importe);
				return html.toString();
			}
		});
		// campo detalle
		table.getRow().getColumn(ERROR_SBPS).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String errorStr = (String) new BasicCellEditor().getValue(item, "errorPlzSbp.errorSbp.descError",
						rowcount);
				HtmlBuilder html = new HtmlBuilder();
				html.append(errorStr == null ? "" : errorStr);
				return html.toString();
			}
		});
		// DAA 22/01/2013 Campo "fechaGrabacion","prodTotal","sumAsegurada"
		// campo prodTotal
		table.getRow().getColumn("prodTotal").getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Set<ParcelaSbp> parcelaSbps = new HashSet<ParcelaSbp>();
				BigDecimal totProd = new BigDecimal(0);
				parcelaSbps = (Set<ParcelaSbp>) new BasicCellEditor().getValue(item, "parcelaSbps", rowcount);
				if (parcelaSbps != null) {
					for (ParcelaSbp parcela : parcelaSbps) {
						totProd = totProd.add(parcela.getTotalProduccion());
					}
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(totProd);
				return html.toString();
			}
		});
		// campo sumAsegurada
		table.getRow().getColumn("sumAsegurada").getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				Set<ParcelaSbp> parcelaSbps = new HashSet<ParcelaSbp>();
				parcelaSbps = (Set<ParcelaSbp>) new BasicCellEditor().getValue(item, "parcelaSbps", rowcount);
				BigDecimal totalSumAsegurada = new BigDecimal(0);
				if (parcelaSbps != null) {
					for (ParcelaSbp par : parcelaSbps) {
						totalSumAsegurada = totalSumAsegurada
								.add(par.getSobreprecio().multiply(par.getTotalProduccion()));
					}
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(NumberUtils.formatear(totalSumAsegurada, 2));
				return html.toString();
			}
		});
		/**
		 * P0073325 - RQ.10, RQ.11 y RQ.12
		 */
		// campo canal firma
		table.getRow().getColumn(CANAL_FIRMA).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
					String canalFirmaStr = (String) new BasicCellEditor().getValue(item, GED_DOC_POLIZA_SBP_CANAL_FIRMA_NOMBRE_CANAL,
							rowcount);
					HtmlBuilder html = new HtmlBuilder();
					html.append(canalFirmaStr == null || canalFirmaStr == "" ? "" : canalFirmaStr);
					return html.toString();
				}
		});
		// campo doc firmada
		table.getRow().getColumn(DOC_FIRMADA).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
					Character docFirmada = (Character) new BasicCellEditor().getValue(item, GED_DOC_POLIZA_SBP_DOC_FIRMADA,
								rowcount);					
					HtmlBuilder html = new HtmlBuilder();
					if (null!=docFirmada && docFirmada.toString().length()!=0) {
						html.append(docFirmada.toString().equals(S) ? SI : NO);
					}
					return html.toString();
				}
		});
		// campo usuario firma
		table.getRow().getColumn(USUARIO_FIRMA).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
					String usuarioFirmaStr = (String) new BasicCellEditor().getValue(item, GED_DOC_POLIZA_SBP_COD_USUARIO,
								rowcount);
					HtmlBuilder html = new HtmlBuilder();
					html.append(null == usuarioFirmaStr ||usuarioFirmaStr == "" ? "" : usuarioFirmaStr);
					return html.toString();
				}
		});
		// campo fecha firma
		table.getRow().getColumn(FECHA_FIRMA).getCellRenderer().setCellEditor(new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
					String value = "";		
					Date dateAux = (Date)new BasicCellEditor().getValue(item, GED_DOC_POLIZA_SBP_FECHA_FIRMA, rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat("dd/MM/yyyy").format(dateAux);
					HtmlBuilder html = new HtmlBuilder();
					html.append (null!=value && value != "" ? value : "");
					return html.toString();
				}
		});
		
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

	/**
	 * Crea y configura el Filter para la consulta de polizas de Sobreprecio
	 * 
	 * @param limit
	 * @param listaGrupoEntidades
	 * @return
	 */
	protected ListadoPolizaSbpFilter getListadoPolizasSbpFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,
			List<BigDecimal> listaGrupoOficinas) {
		ListadoPolizaSbpFilter listadoFilter = new ListadoPolizaSbpFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			listadoFilter.addFilter(property, value);
		}
		// Si la lista de grupos de entidades no esta vacia se incluye en el filtro de
		// busqueda
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			listadoFilter.addFilter("listaGrupoEntidades", listaGrupoEntidades);
		}
		// Si la lista de oficinas no esta vacia se incluye en el filtro de busqueda
		if (listaGrupoOficinas != null && listaGrupoOficinas.size() > 0) {
			listadoFilter.addFilter("listaGrupoOficinas", listaGrupoOficinas);
		}
		return listadoFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de polizas de Sobreprecio
	 * 
	 * @param limit
	 * @return
	 */
	protected ListadoPolizaSbpSort getListadoPolizasSbpSort(Limit limit) {
		ListadoPolizaSbpSort listadoPolizasSbpSort = new ListadoPolizaSbpSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			listadoPolizasSbpSort.addSort(property, order);
		}

		return listadoPolizasSbpSort;
	}

	public Collection<PolizaSbp> getListadoPolizasSbpWithFilterAndSort(ListadoPolizaSbpFilter filter,
			ListadoPolizaSbpSort sort, int rowStart, int rowEnd, String filtrarDetalle) throws BusinessException {
		return listadoPolizaSbpDao.getListadoPolizasSbpWithFilterAndSort(filter, sort, rowStart, rowEnd,
				filtrarDetalle);
	}

	public void setListadoPolizaSbpDao(IListadoPolizaSbpDao listadoPolizaSbpDao) {
		this.listadoPolizaSbpDao = listadoPolizaSbpDao;
	}

	public int getListadoPolizaSbpCountWithFilter(ListadoPolizaSbpFilter filter, String filtrarDetalle) {
		return listadoPolizaSbpDao.getListadoPolizaSbpCountWithFilter(filter, filtrarDetalle);
	}

	@Override
	public List<PolizaSbp> getAllFilteredAndSorted() throws BusinessException {
	    // Obtener todos los registros filtrados y ordenados sin límites de paginación
	    Collection<PolizaSbp> allResults = listadoPolizaSbpDao.getListadoPolizasSbpWithFilterAndSort(listadoFilter, listadoPolizasSbpSort, -1, -1, null);
	    return (List<PolizaSbp>) allResults;
	}
}