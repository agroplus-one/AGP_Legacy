package com.rsi.agp.core.jmesa.service.impl.utilidades;

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
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
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
import com.rsi.agp.core.jmesa.filter.LongFilterMatcher;
import com.rsi.agp.core.jmesa.filter.SiniestrosFilter;
import com.rsi.agp.core.jmesa.service.utilidades.ISiniestrosUtilidadesService;
import com.rsi.agp.core.jmesa.sort.SiniestrosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.poliza.IRiesgosDao;
import com.rsi.agp.dao.models.poliza.ISiniestroDao;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.siniestro.SiniestrosUtilidades;

public class SiniestrosUtilidadesService implements ISiniestrosUtilidadesService {

	/*** SONAR Q ** MODIF TAM(01.12.2021) ***/
	/**
	 * - Se ha eliminado todo el código comentado - Se crean metodos nuevos para
	 * descargar de ifs/fors - Se crean constantes locales nuevas
	 **/
	private final static String NBSP = "&nbsp;";
	private final static String FMT_DATE = "dd/MM/yyyy";
	private final static String VAR = "setDataAndLimitVariables";

	private ISiniestroDao siniestroDao;
	private IRiesgosDao riesgosDao;
	private String id = "listadoSiniestros";
	private Log logger = LogFactory.getLog(getClass());

	// Nombres de los campos de la tabla
	public static final String CAMPO_ID = "id";
	public static final String CAMPO_IDPOLIZA = "idpoliza";
	public static final String CAMPO_ENTIDAD = "codentidad";
	public static final String CAMPO_OFICINA = "oficina";
	public static final String CAMPO_USUARIO = "codusuario";
	public static final String CAMPO_PLAN = "codplan";
	public static final String CAMPO_LINEA = "codlinea";
	public static final String CAMPO_POLIZA = "referencia";
	public static final String CAMPO_DC = "dc";
	public static final String CAMPO_NIF = "nifcif";
	public static final String CAMPO_NOMBRE = "nombre";
	public static final String CAMPO_APELLIDO1 = "apellido1";
	public static final String CAMPO_APELLIDO2 = "apellido2";
	public static final String CAMPO_FEC_ENVIO_POLIZA = "fenvpol";
	public static final String CAMPO_ORDEN = "numsiniestro";
	public static final String CAMPO_FEC_FIRMA = "ffirma";
	public static final String CAMPO_FEC_OCURRENCIA = "focurr";
	public static final String CAMPO_ESTADO = "descestado";
	public static final String CAMPO_IDESTADO = "idestado";
	public static final String CAMPO_FEC_ENVIO = "fenv";
	public static final String CAMPO_CODRIESGO = "codriesgo";
	public static final String CAMPO_RIESGO = "desriesgo";
	public static final String CAMPO_LISTADOGRUPOENT = "listaGrupoEntidades";
	public static final String CAMPO_LISTADOGRUPOOFI = "listaGrupoOficinas";
	public static final String CAMPO_ENTMEDIADORA = "entmediadora";
	public static final String CAMPO_SUBENTMEDIADORA = "subentmediadora";
	public static final String CAMPO_DELEGACION = "delegacion";
	public static final String CAMPO_NUMEROSINIESTRO = "numerosiniestro";
	public static final String CAMPO_SERIE = "serie";

	/* Pet.63473 ** MODIF TAM (30.11.2021) ** Inicio */
	public static final String CAMPO_FEC_BAJA = "fbaja";

	// Posicion de los campos de la tabla
	public static final int POS_ENTIDAD = 1;
	public static final int POS_OFICINA = 2;
	public static final int POS_PLAN = 3;
	public static final int POS_LINEA = 4;
	public static final int POS_POLIZA = 5;
	public static final int POS_NIF = 6;
	public static final int POS_ASEGURADO = 7;
	public static final int POS_FEC_ENVIO_POLIZA = 8;
	public static final int POS_ORDEN = 9;
	public static final int POS_FEC_OCURRENCIA = 10;
	public static final int POS_FEC_FIRMA = 11;
	public static final int POS_ESTADO = 12;
	public static final int POS_FEC_ENVIO = 13;
	
	SiniestrosFilter siniestrosFilter;
	SiniestrosSort siniestrosSort;

	@Override
	public int getSiniestrosCountWithFilter(SiniestrosFilter filter) throws BusinessException {
		return siniestroDao.getSiniestrosCountWithFilter(filter);
	}

	@Override
	public Collection<SiniestrosUtilidades> getSiniestrosWithFilterAndSort(SiniestrosFilter filter, SiniestrosSort sort,
			int rowStart, int rowEnd) throws BusinessException {
		return siniestroDao.getSiniestrosWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}

	@Override
	public List<Riesgo> getRiesgos() {
		try {
			return riesgosDao.getRiesgosConTasables();
		} catch (DAOException e) {
			log("getRiesgos", "Ocurrio un error al obtener el listado de riesgos", e);
		}

		return new ArrayList<Riesgo>();
	}

	public String getTablaSiniestros(HttpServletRequest request, HttpServletResponse response,
			SiniestrosUtilidades siniestro, String primeraBusqueda, List<BigDecimal> listaGrupoEntidades,
			List<BigDecimal> listaGrupoOficinas) {

		/* P0063473 ** MODIF TAM (30.11.2021) ** Inicio */
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil();

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, siniestro, primeraBusqueda);

		// Configura el filtro y la ordenacion, busca los siniestros y los carga en el
		// TableFacade
		setDataAndLimitVariables(tableFacade, listaGrupoEntidades, listaGrupoOficinas);

		// Si se esta generando un informe no se establecen los custom
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade, perfil);
	}

	/**
	 * Metodo para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade, String perfil) {

		Limit limit = tableFacade.getLimit();

		// Si se va a exportar a un informe el listado
		if (limit.isExported()) {
			Table table = tableFacade.getTable();
			// Quita la columna Id del informe
			eliminarColumnaId(tableFacade, table);
			// renombramos las cabeceras
			configurarCabecerasColumnasExport(table);
			// Escribe los datos generados en el response
			tableFacade.render();
			// Devuelve nulo para que el controller no haga nada
			return null;
		}
		// Si se muestra el listado en la pantalla
		else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			// Establece el id
			table.getRow().setUniqueProperty("id");
			// Configuracion de las columnas de la tabla
			configurarColumnas(table);
			// Configuracion de los datos de las columnas que requieren un tratamiento para
			// mostrarse
			// Acciones
			table.getRow().getColumn(CAMPO_ID).getCellRenderer().setCellEditor(getCellEditorAcciones(perfil));
			// Referencia
			table.getRow().getColumn(CAMPO_POLIZA).getCellRenderer().setCellEditor(getCellEditorPoliza());
			// Riesgo del siniestro
			table.getRow().getColumn(CAMPO_RIESGO).getCellRenderer().setCellEditor(getCellEditorRiesgo());
			// Num Aviso
			table.getRow().getColumn(CAMPO_NUMEROSINIESTRO).getCellRenderer().setCellEditor(getCellNumeroSiniestro());
		}

		// Devuelve el html de la tabla
		return tableFacade.render();
	}

	/**
	 * Metodo que configura los nombres de las columnas para los informes
	 * 
	 * @param table
	 */
	private void configurarCabecerasColumnasExport(Table table) {
		table.setCaption("Listado de siniestros");

		Row row = table.getRow();
		row.getColumn(CAMPO_ENTIDAD).setTitle("Entidad");
		row.getColumn(CAMPO_OFICINA).setTitle("Oficina");
		row.getColumn(CAMPO_PLAN).setTitle("Plan");
		row.getColumn(CAMPO_LINEA).setTitle("Linea");
		row.getColumn(CAMPO_POLIZA).setTitle("Poliza");
		row.getColumn(CAMPO_NIF).setTitle("NIF/CIF");
		row.getColumn(CAMPO_NOMBRE).setTitle("Asegurado");
		row.getColumn(CAMPO_FEC_ENVIO_POLIZA).setTitle("Fecha de envio de la poliza");
		row.getColumn(CAMPO_ORDEN).setTitle("Orden");
		row.getColumn(CAMPO_RIESGO).setTitle("Riesgo");
		row.getColumn(CAMPO_NUMEROSINIESTRO).setTitle("Num. Aviso");
		row.getColumn(CAMPO_FEC_OCURRENCIA).setTitle("Fecha de ocurrencia del siniestro");
		row.getColumn(CAMPO_FEC_FIRMA).setTitle("Fecha de firma del siniestro");
		row.getColumn(CAMPO_ESTADO).setTitle("Estado");
		row.getColumn(CAMPO_FEC_ENVIO).setTitle("Fecha de envio del siniestro");
		row.getColumn(CAMPO_FEC_BAJA).setTitle("Fecha de Baja del siniestro");
	}

	/**
	 * Metodo que formatea los datos que se muestran en las celdas de la columna
	 * 'Poliza'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorPoliza() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				Object objref = new BasicCellEditor().getValue(item, CAMPO_POLIZA, rowcount);
				Object objdc = new BasicCellEditor().getValue(item, CAMPO_DC, rowcount);

				// Control de la referencia de poliza del siniestro
				String ref = objref != null ? objref.toString() : "";
				String dc = objdc != null ? objdc.toString() : "";

				HtmlBuilder html = new HtmlBuilder();

				html.append("".equals(dc) ? ref : ref + "-" + dc);

				return html.toString();
			}
		};
	}

	/**
	 * Metodo que formatea los datos que se muestran en las celdas de la columna
	 * 'Riesgo Siniestro'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorRiesgo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String codRiesgo = "", riesgo = "";
				boolean error = false;

				// Control riesgo del siniestro
				try {
					codRiesgo = StringUtils
							.nullToString(new BasicCellEditor().getValue(item, CAMPO_CODRIESGO, rowcount)).toString();
					riesgo = StringUtils.nullToString(new BasicCellEditor().getValue(item, CAMPO_RIESGO, rowcount))
							.toString();
				} catch (Exception e) {
					log("getCellEditorRiesgo", "Ocurrio un error al obtener el riesgo del siniestro", e);
					error = true;
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(error ? NBSP : codRiesgo + " - " + riesgo);

				return html.toString();
			}
		};
	}

	/**
	 * Metodo que formatea los datos que se muestran en las celdas de la columna
	 * 'Num. Aviso'
	 * 
	 * @return
	 */
	private CellEditor getCellNumeroSiniestro() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal numAviso = null;

				// Control Num. Aviso
				try {
					numAviso = (BigDecimal) (new BasicCellEditor().getValue(item, CAMPO_NUMEROSINIESTRO, rowcount));

				} catch (Exception e) {
					log("getCellNumeroSiniestro", "Ocurrio un error al obtener el num Aviso", e);

				}

				HtmlBuilder html = new HtmlBuilder();

				logger.debug("NUM AVISO: " + numAviso);
				if (numAviso != null) {
					if (numAviso.compareTo(new BigDecimal(-1)) == 0) {
						logger.debug("Provisional: Le ponemos un espacio");
						html.append(NBSP);
					} else {
						logger.debug("Alta: Le ponemos el num aviso");
						html.append(numAviso);
					}
				} else {
					logger.debug("Va por FTP");
					html.append(" FTP ");
				}
				return html.toString();
			}
		};
	}

	/**
	 * Metodo que formatea los datos que se muestran en las celdas de la columna
	 * 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final String perfil) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				// Estado del siniestro
				Short estado = new Short(new BasicCellEditor().getValue(item, CAMPO_IDESTADO, rowcount).toString());
				// Id del siniestro
				String idSiniestro = new BasicCellEditor().getValue(item, CAMPO_ID, rowcount).toString();
				// Id de la poliza asociada al siniestro
				String idPoliza = new BasicCellEditor().getValue(item, CAMPO_IDPOLIZA, rowcount).toString();
				// Fecha de envio a Agroseguro del siniestro
				Date fenv = (Date) new BasicCellEditor().getValue(item, CAMPO_FEC_ENVIO, rowcount);

				BigDecimal numAviso = (BigDecimal) (new BasicCellEditor().getValue(item, CAMPO_NUMEROSINIESTRO,
						rowcount));

				Integer serie = (Integer) (new BasicCellEditor().getValue(item, CAMPO_SERIE, rowcount));

				int numIconos = 0;

				HtmlBuilder html = new HtmlBuilder();

				// EDITAR
				if (!Constants.SINIESTRO_ESTADO_ENVIADO_PDT_ACEPTACION.equals(estado)
						&& !Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO.equals(estado)) {
					numIconos++;
					html.a().href().quote()
							.append("javascript:editar(" + idSiniestro + "," + idPoliza + "," + estado + ");").quote()
							.close();
					html.append(
							"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar siniestro\" title=\"Editar siniestro\"/>");
					html.aEnd();
					html.append(NBSP);
				}

				// ELIMINAR
				if (!Constants.SINIESTRO_ESTADO_ENVIADO_PDT_ACEPTACION.equals(estado)
						&& !Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO.equals(estado)) {
					numIconos++;
					html.a().href().quote().append("javascript:borrar(" + idSiniestro + "," + idPoliza + ");").quote()
							.close();
					html.append(
							"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar siniestro\" title=\"Borrar siniestro\"/>");
					html.aEnd();
					html.append(NBSP);
				}

				// INFORMACION
				numIconos++;
				html.a().href().quote().append("javascript:informacion(" + idSiniestro + "," + idPoliza + ");").quote()
						.close();
				html.append(
						"<img src=\"jsp/img/displaytag/information.png\" alt=\"Informaci&oacute;n del siniestro\" title=\"Informaci&oacute;n del siniestro\"/>");
				html.aEnd();
				html.append(NBSP);
				if (numIconos == 3) {
					html.append("<br>");
					numIconos = 0;
				}

				/* SONAR Q */
				HashMap<String, Object> datos = new HashMap<String, Object>();
				String opcion = "accionImp";
				datos = getAcciones(numAviso, html, numIconos, estado, idSiniestro, idPoliza, serie, opcion, perfil);

				html = (HtmlBuilder) datos.get("html");
				numIconos = (Integer) datos.get("numIconos");
				/* SONAR Q FIN */

				// IMPRIMIR
				numIconos++;
				html.a().href().quote().append("javascript:imprimir(" + idSiniestro + ");").quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir siniestro\" title=\"Imprimir siniestro\"/>");
				html.aEnd();
				html.append(NBSP);
				if (numIconos == 3) {
					html.append("<br>");
					numIconos = 0;
				}

				// PASAR A DEFINITIVA
				if (Constants.SINIESTRO_ESTADO_PROVISIONAL.equals(estado)) {
					numIconos++;
					html.a().href().quote().append("javascript:pasarADefinitiva(" + idSiniestro + "," + idPoliza + ");")
							.quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar a definitiva el siniestro\" title=\"Pasar a definitiva el siniestro\"/>");
					html.aEnd();
					html.append(NBSP);
					if (numIconos == 3) {
						html.append("<br>");
						numIconos = 0;
					}
				}

				// VER ERRORES
				// Se muestra el icono de acuse de recibo si el siniestro no esta en estado
				// 'Enviado correcto' y tiene fecha de envio
				if (!Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO.equals(estado) && fenv != null) {
					numIconos++;
					html.a().href().quote().append("javascript:verErrores(" + idSiniestro + ");").quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/acuserecibo.png\" alt=\"Consultar Acuse de Recibo\" title=\"Consultar Acuse de Recibo\"/>");
					html.aEnd();
					html.append(NBSP);
					if (numIconos == 3) {
						html.append("<br>");
						numIconos = 0;
					}
				}

				/* SONAR Q */
				opcion = "accionBaja";
				datos = new HashMap<String, Object>();
				datos = getAcciones(numAviso, html, numIconos, estado, idSiniestro, idPoliza, serie, opcion, perfil);

				html = (HtmlBuilder) datos.get("html");
				numIconos = (Integer) datos.get("numIconos");
				/* SONAR Q FIN */

				return html.toString();
			}
		};
	}

	/**
	 * Configuracion de las columnas de la tabla
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
		configColumna(table, CAMPO_ID, "&nbsp;&nbsp;Acciones", false, false, "3%");
		// 1 - Entidad
		configColumna(table, CAMPO_ENTIDAD, "Ent.</br>", true, true, "3%");
		// 2 - Oficina
		configColumna(table, CAMPO_OFICINA, "Ofi.</br>", true, true, "3%");
		// 3 - Plan
		configColumna(table, CAMPO_PLAN, "Plan</br>", true, true, "3%");
		// 4 - Linea
		configColumna(table, CAMPO_LINEA, "L&iacute;nea</br>", true, true, "3%");
		// 5 - Poliza
		configColumna(table, CAMPO_POLIZA, "P&oacute;liza</br>", true, true, "7%");
		// 6 - NIF/CIF
		configColumna(table, CAMPO_NIF, "NIF/CIF</br>", true, true, "7%");
		// 7 - Asegurado
		configColumna(table, CAMPO_NOMBRE, "Asg. Nombre</br>", true, true, "10%");
		// 8 - Fecha de envio de la poliza
		configColumnaFecha(table, CAMPO_FEC_ENVIO_POLIZA, "Fec.Env.Pol</br>", true, true, "7%", FMT_DATE);
		// 9 - Orden del siniestro dentro de la poliza
		configColumna(table, CAMPO_ORDEN, "Orden</br>", true, true, "2%");
		// 10 - Riesgo del siniestro
		configColumna(table, CAMPO_RIESGO, "Riesgo Siniestro</br>", true, true, "15%");
		// 11 - Num Aviso
		configColumna(table, CAMPO_NUMEROSINIESTRO, "Num. Aviso", true, false, "15%");
		// 12 - Fecha de ocurrencia
		configColumnaFecha(table, CAMPO_FEC_OCURRENCIA, "Fec.Ocur</br>", true, true, "7%", FMT_DATE);
		// 13 - Fecha de firma
		configColumnaFecha(table, CAMPO_FEC_FIRMA, "Fec.Firma</br>", true, true, "7%", FMT_DATE);
		// 14 - Estado
		configColumna(table, CAMPO_ESTADO, "Estado</br>", true, true, "10%");
		// 15 - Fecha de envio
		configColumnaFecha(table, CAMPO_FEC_ENVIO, "Fec.Env</br>", true, true, "7%", FMT_DATE);
		configColumnaFecha(table, CAMPO_FEC_BAJA, "Fec.Baja</br>", false, false, "7%", FMT_DATE);
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
	 */
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width) {
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
	@SuppressWarnings("deprecation")
	private void configColumnaFecha(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Anhade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error("Ocurrio un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla de siniestros
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request, HttpServletResponse response,
			SiniestrosUtilidades siniestro, String primeraBusqueda) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = new TableFacade(id, request);

		// Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());

		cargarColumnas(tableFacade);

		//tableFacade.setExportTypes(response, ExportType.EXCEL);

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.

		// Defino los tipos para los filtros. Habra que redefinir en el filter la forma
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (primeraBusqueda == null) {
				if (request.getSession().getAttribute("listadoSiniestros_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("listadoSiniestros_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario
				cargarFiltrosBusqueda(siniestro, tableFacade);

				// -- ORDENACION POR DEFECTO --> Entidad asc, Plan desc, Linea asc, Poliza asc,
				// orden asc
				tableFacade.getLimit().getSortSet().addSort(new Sort(POS_ENTIDAD, CAMPO_ENTIDAD, Order.ASC));
				tableFacade.getLimit().getSortSet().addSort(new Sort(POS_PLAN, CAMPO_PLAN, Order.DESC));
				tableFacade.getLimit().getSortSet().addSort(new Sort(POS_LINEA, CAMPO_LINEA, Order.ASC));
				tableFacade.getLimit().getSortSet().addSort(new Sort(POS_POLIZA, CAMPO_POLIZA, Order.ASC));
				tableFacade.getLimit().getSortSet().addSort(new Sort(POS_ORDEN, CAMPO_ORDEN, Order.ASC));
			}
		}

		return tableFacade;
	}

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos
	 * de los siniestros y carga el TableFacade con ellos
	 * 
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, List<BigDecimal> listaGrupoEntidades,
			List<BigDecimal> listaGrupoOficinas) {

		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();
		siniestrosFilter = getSiniestrosFilter(limit, listaGrupoEntidades, listaGrupoOficinas);

		// Obtiene el numero de filas que cumplen el filtro
		int totalRows = 0;
		try {
			totalRows = getSiniestrosCountWithFilter(siniestrosFilter);
			log(VAR, "Numero de siniestros obtenidos = " + totalRows);
		} catch (BusinessException e1) {
			log(VAR, "Error al obtener el numero de siniestros", e1);
		}

		tableFacade.setTotalRows(totalRows);

		// Crea el Sort para la busqueda de siniestros
		siniestrosSort = getSiniestrosSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<SiniestrosUtilidades> items = new ArrayList<SiniestrosUtilidades>();
		try {
			items = getSiniestrosWithFilterAndSort(siniestrosFilter, siniestrosSort, rowStart, rowEnd);
			log(VAR, "Registros en la lista de siniestros = " + items.size());
		} catch (BusinessException e) {
			log(VAR, "Error al obtener el listado de siniestros", e);
		}

		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);

	}

	/**
	 * Crea y configura el Filter para la consulta de siniestros
	 * 
	 * @param limit
	 * @return
	 */
	private SiniestrosFilter getSiniestrosFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,
			List<BigDecimal> listaGrupoOficinas) {
		SiniestrosFilter siniestrosFilter = new SiniestrosFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			log("getSiniestrosFilter", "Anhade al filtro - property: " + property + " - value: " + value);

			siniestrosFilter.addFilter(property, value);
		}

		// Si la lista de grupos de entidades no esta vacia se incluye en el filtro de
		// busqueda
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			siniestrosFilter.addFilter(CAMPO_LISTADOGRUPOENT, listaGrupoEntidades);
		}
		// Si la lista de grupos de oficinas no esta vacia se incluye en el filtro de
		// busqueda
		if (listaGrupoOficinas != null && listaGrupoOficinas.size() > 0) {
			siniestrosFilter.addFilter(CAMPO_LISTADOGRUPOOFI, listaGrupoOficinas);
		}

		return siniestrosFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de siniestros
	 * 
	 * @param limit
	 * @return
	 */
	private SiniestrosSort getSiniestrosSort(Limit limit) {
		SiniestrosSort siniestrosSort = new SiniestrosSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			siniestrosSort.addSort(property, order);

			log("getSiniestrosSort", "Anhade la ordenacion - property: " + property + " - order: " + order);
		}

		return siniestrosSort;
	}

	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * 
	 * @param tableFacade
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {

		// Configura el TableFacade con las columnas que se quieren mostrar
		tableFacade.setColumnProperties(CAMPO_ID, CAMPO_ENTIDAD, CAMPO_OFICINA, CAMPO_PLAN, CAMPO_LINEA, CAMPO_POLIZA,
				CAMPO_NIF, CAMPO_NOMBRE, CAMPO_FEC_ENVIO_POLIZA, CAMPO_ORDEN, CAMPO_RIESGO, CAMPO_NUMEROSINIESTRO,
				CAMPO_FEC_OCURRENCIA, CAMPO_FEC_FIRMA, CAMPO_ESTADO, CAMPO_FEC_ENVIO, CAMPO_FEC_BAJA);

	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 
	 * @param siniestro
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(SiniestrosUtilidades siniestro, TableFacade tableFacade) {

		// Entidad
		if (FiltroUtils.noEstaVacio(siniestro.getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ENTIDAD, siniestro.getCodentidad().toString());
		// Oficina
		if (FiltroUtils.noEstaVacio(siniestro.getOficina()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_OFICINA, siniestro.getOficina());
		// Usuario
		if (FiltroUtils.noEstaVacio(siniestro.getCodusuario()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_USUARIO, siniestro.getCodusuario());
		// Plan
		if (FiltroUtils.noEstaVacio(siniestro.getCodplan()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_PLAN, siniestro.getCodplan().toString());
		// Linea
		if (FiltroUtils.noEstaVacio(siniestro.getCodlinea()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_LINEA, siniestro.getCodlinea().toString());
		// Poliza
		if (FiltroUtils.noEstaVacio(siniestro.getReferencia()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_POLIZA, siniestro.getReferencia());
		// NIF/CIF
		if (FiltroUtils.noEstaVacio(siniestro.getNifcif()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NIF, siniestro.getNifcif());
		// Asegurado
		if (FiltroUtils.noEstaVacio(siniestro.getNombre()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NOMBRE, siniestro.getNombre());
		// Riesgo
		if (FiltroUtils.noEstaVacio(siniestro.getCodriesgo())) {
			String[] aux = siniestro.getCodriesgo().split(";");
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_CODRIESGO, aux[0]);
			//ESC-31621
			//tableFacade.getLimit().getFilterSet().addFilter("codgruposeguro", aux[1]);
			//ESC-31621
		}

		/* SONAR Q */
		tableFacade = anadirFechasTableFacade(tableFacade, siniestro);
		/* FIN SONAR Q */

		// Estado
		if (FiltroUtils.noEstaVacio(siniestro.getIdestado()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_IDESTADO, siniestro.getIdestado().toString());
		// delegacion
		if (FiltroUtils.noEstaVacio(siniestro.getDelegacion()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_DELEGACION, siniestro.getDelegacion().toString());
		// entidad mediadora
		if (FiltroUtils.noEstaVacio(siniestro.getEntmediadora()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_ENTMEDIADORA, siniestro.getEntmediadora().toString());
		// subentidad mediadora
		if (FiltroUtils.noEstaVacio(siniestro.getSubentmediadora()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_SUBENTMEDIADORA,
					siniestro.getSubentmediadora().toString());
		if (FiltroUtils.noEstaVacio(siniestro.getNumerosiniestro()))
			tableFacade.getLimit().getFilterSet().addFilter(CAMPO_NUMEROSINIESTRO,
					siniestro.getNumerosiniestro().toString());

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
	 * Escribe en el log indicando la clase y el metodo.
	 * 
	 * @param method
	 * @param msg
	 */
	private void log(String method, String msg) {
		logger.debug("SiniestrosUtilidadesService." + method + " - " + msg);
	}

	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * 
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log(String method, String msg, Throwable e) {
		logger.error("SiniestrosUtilidadesService." + method + " - " + msg, e);
	}

	/**
	 * Setter del Dao para Spring
	 * 
	 * @param riesgosDao
	 */
	public void setRiesgosDao(IRiesgosDao riesgosDao) {
		this.riesgosDao = riesgosDao;
	}

	/**
	 * Setter del Dao para Spring
	 * 
	 * @param siniestroDao
	 */
	public void setSiniestroDao(ISiniestroDao siniestroDao) {
		this.siniestroDao = siniestroDao;
	}

	/* MODIF TAM (01.12.2021) ** SONAR Q ** Inicio */
	/*
	 * Creamos nuevas funciones, para descargar las funciones principales de
	 * ifs/fors
	 */
	private HashMap<String, Object> getAcciones(BigDecimal numAviso, HtmlBuilder html, int numIconos, Short estado,
			String idSiniestro, String idPoliza, Integer serie, String opcion, String perfil) {

		HashMap<String, Object> datos = new HashMap<String, Object>();

		if (opcion.equals("accionImp")) {
			// Enviado correcto y enviado por servicio web (para solicitar parte del
			// siniestro - Pdf del SW)
			if (null != numAviso && !numAviso.equals(new BigDecimal(-1))
					&& Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO.equals(estado)) {

				html.a().href().quote().append("javascript:verDetalleLineaSiniestro(" + serie + ", " + numAviso + ","
						+ idSiniestro + "," + idPoliza + ")").quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/imprimir_poliza_modificada.png\" alt=\"Pdf - Parte del siniestro\" title=\"Pdf - Parte del siniestro\"/>");
				html.aEnd();
				html.append(NBSP);
				if (numIconos == 3) {
					html.append("<br>");
					numIconos = 0;
				}
			}
		} else {
			/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
			/*
			 * Inclcuir una nueva acción para dar de baja (lógica) el siniestro,
			 * independientemente del estado del siniestro
			 */
			if (opcion.equals("accionBaja")) {
				if ((Constants.PERFIL_USUARIO_ADMINISTRADOR).equals(perfil)) {
					numIconos++;
					html.a().href().quote().append("javascript:bajaSiniestro(" + idSiniestro + ");").quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/cancel.png\" alt=\"Baja Siniestro\" title=\"Baja Siniestro\"/>");
					html.aEnd();
					html.append(NBSP);
					if (numIconos == 3) {
						html.append("<br>");
						numIconos = 0;
					}
				}
			}
			/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */
		}
		datos.put("html", html);
		datos.put("numIconos", numIconos);

		return datos;
	}

	private TableFacade anadirFechasTableFacade(TableFacade tableFacade, SiniestrosUtilidades siniestro) {
		// Fecha de ocurrencia
		if (FiltroUtils.noEstaVacio(siniestro.getFocurr()))
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(CAMPO_FEC_OCURRENCIA, new SimpleDateFormat(FMT_DATE).format(siniestro.getFocurr())));

		// Fecha de firma
		if (FiltroUtils.noEstaVacio(siniestro.getFfirma()))
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(CAMPO_FEC_FIRMA, new SimpleDateFormat(FMT_DATE).format(siniestro.getFfirma())));

		// Fecha de envio del siniestro
		if (FiltroUtils.noEstaVacio(siniestro.getFenv()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(CAMPO_FEC_ENVIO, new SimpleDateFormat(FMT_DATE).format(siniestro.getFenv())));

		// Fecha de envio de la poliza
		if (FiltroUtils.noEstaVacio(siniestro.getFenvpol()))
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(CAMPO_FEC_ENVIO_POLIZA, new SimpleDateFormat(FMT_DATE).format(siniestro.getFenvpol())));

		/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Inicio */
		// Fecha de baja del Siniestro
		if (FiltroUtils.noEstaVacio(siniestro.getFbaja()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(CAMPO_FEC_BAJA, new SimpleDateFormat(FMT_DATE).format(siniestro.getFbaja())));
		/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Fin */

		return tableFacade;
	}

	/* MODIF TAM (01.12.2021) ** SONAR Q ** Fin */
	
	@Override
	public List<SiniestrosUtilidades> getAllFilteredAndSorted() throws BusinessException {
		Collection<SiniestrosUtilidades> allResults = null;
		allResults = siniestroDao.getSiniestrosWithFilterAndSort(siniestrosFilter,siniestrosSort, -1, -1);
		return (List<SiniestrosUtilidades>) allResults;
	}


}
