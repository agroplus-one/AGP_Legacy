package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.IInformesImpagadosUnificadoService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeImpagadosUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformesImpagadosUnificadoService extends GetTablaService implements IInformesImpagadosUnificadoService {

	private final Log logger = LogFactory.getLog(InformesImpagadosUnificadoService.class);

	public final static String LISTA_ENTIDADES_USUARIO = "grupoEntidades";
	private final static String ID = "id";
	private final static String CODENTIDAD = "codentidad";
	// private final static String NOMENTIDAD= "nomentidad";
	private final static String ENTMEDIADORA = "entmediadora";
	private final static String SUBENTMEDIADORA = "subentmediadora";
	private final static String IDCOLECTIVO = "idcolectivo";
	private final static String CIFTOMADOR = "ciftomador";
	private final static String NOM_TOMADOR = "nomTomador";
	private final static String PLAN = "plan";
	private final static String LINEA = "linea";
	// private final static String NOMLINEA = "nomlinea";
	private final static String FASE = "fase";
	private final static String RECIBO = "recibo";
	private final static String REFERENCIA = "referencia";
	// private final static String FECHA_CARGA="fechaCarga" ;
	private final static String FECHA_ACEPTACION = "fechaAceptacion";
	private final static String FECHA_CIERRE = "fechaCierre";
	private final static String FECHA_EMISION_RECIBO = "fechaEmisionRecibo";
	private final static String IMPORTE_SALDO_PDTE = "importeSaldoPdte";
	private final static String IMPORTE_COBRO_RECIBIDO = "importeCobroRecibido";
	private final static String GA_ADMIN = "gaAdmin";
	private final static String GA_ADQ = "gaAdq";
	private final static String GA_COMISION_MEDIADOR = "gaComisionMediador";
	private final static String GA_COMMED_ENTIDAD = "gaCommedEntidad";
	private final static String GA_COMMED_ESMED = "gaCommedEsmed";
	private final static String GRUPO_NEGOCIO = "grupoNegocio";
	private final static String STYLE = "text-align: right";
	IGenericoFilter informesImpagadosUnificadoFilter;
	IGenericoSort informesImpagadosUnificadoSort;

	@SuppressWarnings("deprecation")
	@Override
	public String getTabla(HttpServletRequest request, HttpServletResponse response,
			Serializable informeImpagadosUnificado, String origenLlamada, List<BigDecimal> listaGrupoEntidades,
			IGenericoDao genericoDao, Usuario usuario) {

		InformeImpagadosUnificado informe = (InformeImpagadosUnificado) informeImpagadosUnificado;

		cargarColumnas();
		TableFacade tableFacade = this.crearTableFacade(request, response, origenLlamada, columnas);
		// tableFacade.setExportTypes(response);
		tableFacade.autoFilterAndSort(false);
		// tableFacade.setExportTypes(response, ExportType.EXCEL);

		setColumnasVisibles(tableFacade, usuario);

		if (origenLlamada != null) {// nulo viene de paginaci�n
			informesImpagadosUnificadoFilter.clear();
			if (null != tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}
			try {
				cargarFiltrosBusqueda(informe, tableFacade);
			} catch (ParseException e) {
				logger.error("Excepcion : InformesImpagadosUnificadoService - getTabla", e);
			}
		}

		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			this.addListaEntidadesFilter(listaGrupoEntidades, informesImpagadosUnificadoFilter,
					LISTA_ENTIDADES_USUARIO);
		}

		informesImpagadosUnificadoSort.clear();
		this.setDataAndLimitVariables(tableFacade, informesImpagadosUnificadoFilter, genericoDao,
				informesImpagadosUnificadoSort, informesImpagadosUnificadoSort);

		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}
		return html(tableFacade, usuario);// + script;
	}

	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);
			columnas.put(CODENTIDAD, CODENTIDAD);
			columnas.put(ENTMEDIADORA, ENTMEDIADORA);
			columnas.put(SUBENTMEDIADORA, SUBENTMEDIADORA);
			columnas.put(PLAN, PLAN);
			columnas.put(LINEA, LINEA);
			columnas.put(REFERENCIA, REFERENCIA);
			columnas.put(IDCOLECTIVO, IDCOLECTIVO);
			columnas.put(CIFTOMADOR, CIFTOMADOR);
			columnas.put(NOM_TOMADOR, NOM_TOMADOR);
			columnas.put(RECIBO, RECIBO);
			columnas.put(FASE, FASE);
			// columnas.put(FECHA_CARGA,FECHA_CARGA);
			columnas.put(FECHA_EMISION_RECIBO, FECHA_EMISION_RECIBO);
			columnas.put(FECHA_ACEPTACION, FECHA_ACEPTACION);
			columnas.put(FECHA_CIERRE, FECHA_CIERRE);
			columnas.put(IMPORTE_SALDO_PDTE, IMPORTE_SALDO_PDTE);
			columnas.put(IMPORTE_COBRO_RECIBIDO, IMPORTE_COBRO_RECIBIDO);
			columnas.put(GRUPO_NEGOCIO, GRUPO_NEGOCIO);
			columnas.put(GA_ADMIN, GA_ADMIN);
			columnas.put(GA_ADQ, GA_ADQ);
			columnas.put(GA_COMISION_MEDIADOR, GA_COMISION_MEDIADOR);
			columnas.put(GA_COMMED_ENTIDAD, GA_COMMED_ENTIDAD);
			columnas.put(GA_COMMED_ESMED, GA_COMMED_ESMED);
			columnas.put(LISTA_ENTIDADES_USUARIO, LISTA_ENTIDADES_USUARIO);
		}
	}

	@SuppressWarnings("deprecation")
	private void setColumnasVisibles(TableFacade tableFacade, Usuario usuario) {
		Limit limit = tableFacade.getLimit();

		if (!limit.isExported()) {
			setColumnasVisiblesGrid(tableFacade, usuario);
		} else {
			setColumnasVisiblesExport(tableFacade, usuario);
		}
	}

	@SuppressWarnings("deprecation")
	private void setColumnasVisiblesGrid(TableFacade tableFacade, Usuario usuario) {
		String perfil = usuario.getPerfil().substring(4);

		if (perfil.compareTo("0") == 0) {
			tableFacade.setColumnProperties(ENTMEDIADORA, IDCOLECTIVO, CIFTOMADOR, NOM_TOMADOR, PLAN, LINEA, FASE,
					RECIBO, REFERENCIA, IMPORTE_SALDO_PDTE, IMPORTE_COBRO_RECIBIDO, GRUPO_NEGOCIO, GA_ADMIN, GA_ADQ,
					GA_COMMED_ENTIDAD, GA_COMMED_ESMED);
		}

		if (perfil.compareTo("5") == 0 || (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == false)) {
			tableFacade.setColumnProperties(ENTMEDIADORA, IDCOLECTIVO, CIFTOMADOR, NOM_TOMADOR, PLAN, LINEA, FASE,
					RECIBO, REFERENCIA, IMPORTE_SALDO_PDTE, IMPORTE_COBRO_RECIBIDO, GRUPO_NEGOCIO, GA_COMMED_ENTIDAD,
					GA_COMMED_ESMED);
		}

		if (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == true) {
			tableFacade.setColumnProperties(ENTMEDIADORA, IDCOLECTIVO, CIFTOMADOR, NOM_TOMADOR, PLAN, LINEA, FASE,
					RECIBO, REFERENCIA, IMPORTE_SALDO_PDTE, IMPORTE_COBRO_RECIBIDO, GRUPO_NEGOCIO, GA_COMMED_ESMED);
		}
	}

	@SuppressWarnings("deprecation")
	private void setColumnasVisiblesExport(TableFacade tableFacade, Usuario usuario) {
		String perfil = usuario.getPerfil().substring(4);

		if (perfil.compareTo("0") == 0) {
			tableFacade.setColumnProperties(ENTMEDIADORA, SUBENTMEDIADORA, IDCOLECTIVO, CIFTOMADOR, NOM_TOMADOR, PLAN,
					LINEA, FASE, RECIBO, REFERENCIA, IMPORTE_SALDO_PDTE, IMPORTE_COBRO_RECIBIDO, GRUPO_NEGOCIO,
					GA_ADMIN, GA_ADQ, GA_COMMED_ENTIDAD, GA_COMMED_ESMED);
		}

		if (perfil.compareTo("5") == 0 || (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == false)) {
			tableFacade.setColumnProperties(ENTMEDIADORA, SUBENTMEDIADORA, IDCOLECTIVO, CIFTOMADOR, NOM_TOMADOR, PLAN,
					LINEA, FASE, RECIBO, REFERENCIA, IMPORTE_SALDO_PDTE, IMPORTE_COBRO_RECIBIDO, GRUPO_NEGOCIO,
					GA_COMMED_ENTIDAD, GA_COMMED_ESMED);
		}

		if (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == true) {
			tableFacade.setColumnProperties(ENTMEDIADORA, SUBENTMEDIADORA, IDCOLECTIVO, CIFTOMADOR, NOM_TOMADOR, PLAN,
					LINEA, FASE, RECIBO, REFERENCIA, IMPORTE_SALDO_PDTE, IMPORTE_COBRO_RECIBIDO, GRUPO_NEGOCIO,
					GA_COMMED_ESMED);
		}
	}

	private void cargarFiltrosBusqueda(InformeImpagadosUnificado informe, TableFacade tableFacade)
			throws ParseException {

		if (null != informe.getCodentidad())
			this.addColumnaFiltro(tableFacade, CODENTIDAD, informe.getCodentidad(), informesImpagadosUnificadoFilter);

		if (null != informe.getEntmediadora())
			this.addColumnaFiltro(tableFacade, ENTMEDIADORA, informe.getEntmediadora(),
					informesImpagadosUnificadoFilter);

		if (null != informe.getSubentmediadora())
			this.addColumnaFiltro(tableFacade, SUBENTMEDIADORA, informe.getSubentmediadora(),
					informesImpagadosUnificadoFilter);

		if (null != informe.getPlan())
			this.addColumnaFiltro(tableFacade, PLAN, informe.getPlan(), informesImpagadosUnificadoFilter);

		if (null != informe.getLinea())
			this.addColumnaFiltro(tableFacade, LINEA, informe.getLinea(), informesImpagadosUnificadoFilter);

		if (null != informe.getIdcolectivo() && !informe.getIdcolectivo().isEmpty())
			this.addColumnaFiltro(tableFacade, IDCOLECTIVO, informe.getIdcolectivo(), informesImpagadosUnificadoFilter);

		if (null != informe.getFase())
			this.addColumnaFiltro(tableFacade, FASE, informe.getFase(), informesImpagadosUnificadoFilter);

		if (null != informe.getFechaEmisionRecibo())
			this.addColumnaFiltro(tableFacade, FECHA_EMISION_RECIBO, informe.getFechaEmisionRecibo(),
					informesImpagadosUnificadoFilter);

		if (null != informe.getRecibo())
			this.addColumnaFiltro(tableFacade, RECIBO, informe.getRecibo(), informesImpagadosUnificadoFilter);

		if (null != informe.getFechaAceptacion())
			this.addColumnaFiltro(tableFacade, FECHA_ACEPTACION, informe.getFechaAceptacion(),
					informesImpagadosUnificadoFilter);

		if (null != informe.getFechaCierre())
			this.addColumnaFiltro(tableFacade, FECHA_CIERRE, informe.getFechaCierre(),
					informesImpagadosUnificadoFilter);

	}

	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade, Usuario usuario) {
		Limit limit = tableFacade.getLimit();

		if (limit.isExported()) {
			if (limit.getExportType() == ExportType.EXCEL) {
				Table table = tableFacade.getTable();
				// renombramos las cabeceras
				configurarCabecerasColumnasExport(table, usuario);
				tableFacade.render();
				// Devuelve nulo para que el controller no haga nada
				return null;
			}
		} else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			// int totalListSize = this.getListadoItems().size();
			// Agrega el valor de 'totalListSize' a la leyenda de la tabla.
			// table.setCaption("<!-- totalListSize: " + totalListSize + " -->");
			table.getRow().setUniqueProperty(ID);

			configurarColumnas(table, usuario);
			table.getRow().getColumn(columnas.get(ENTMEDIADORA)).getCellRenderer()
					.setCellEditor(getCellEditorEsMediadora());

		}
		return tableFacade.render();
	}

	private void configurarCabecerasColumnasExport(Table table, Usuario usuario) {
		String perfil = usuario.getPerfil().substring(4);

		table.setCaption("Impagados ");
		Row row = table.getRow();
		row.getColumn(ENTMEDIADORA).setTitle("E Med");
		row.getColumn(SUBENTMEDIADORA).setTitle("S Med");
		row.getColumn(IDCOLECTIVO).setTitle("Colectivo");
		row.getColumn(CIFTOMADOR).setTitle("CIF Tomador");
		row.getColumn(NOM_TOMADOR).setTitle("Nombre Tomador");
		row.getColumn(PLAN).setTitle("Plan");
		row.getColumn(LINEA).setTitle("L�nea");
		row.getColumn(FASE).setTitle("Fase");
		row.getColumn(RECIBO).setTitle("Recibo");
		row.getColumn(REFERENCIA).setTitle("P�liza");
		row.getColumn(IMPORTE_SALDO_PDTE).setTitle("Saldo Pendiente");
		row.getColumn(IMPORTE_COBRO_RECIBIDO).setTitle("Cobro Recibido");
		row.getColumn(GRUPO_NEGOCIO).setTitle("G.N.");
		row.getColumn(GA_COMMED_ESMED).setTitle("");
		if (perfil.compareTo("0") == 0) {
			row.getColumn(GA_ADMIN).setTitle("Total gastos admin.");
			row.getColumn(GA_ADQ).setTitle("Total gastos Adq.");
			row.getColumn(GA_COMMED_ENTIDAD).setTitle("Total gastos ent.");
			row.getColumn(GA_COMMED_ESMED).setTitle("Total gastos ES-Med");
		}

		if (perfil.compareTo("5") == 0 || (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == false)) {
			row.getColumn(GA_COMMED_ENTIDAD).setTitle("Total gastos ent.");
			row.getColumn(GA_COMMED_ESMED).setTitle("Total gastos ES-Med");
		}

		if (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == true) {
			row.getColumn(GA_COMMED_ESMED).setTitle("Total gastos ES-Med");
		}

	}

	private void configurarColumnas(HtmlTable table, Usuario usuario) {
		// Campos comunes a todos los perfiles de usuario
		String perfil = usuario.getPerfil().substring(4);
		this.configColumna(table, columnas.get(ENTMEDIADORA), "E-S Med", true, true, "");
		this.configColumna(table, columnas.get(IDCOLECTIVO), "Colectivo", true, true, "");
		this.configColumna(table, columnas.get(CIFTOMADOR), "CIF Tomador", true, true, "");
		this.configColumna(table, columnas.get(NOM_TOMADOR), "Nombre Tomador", true, true, "");
		this.configColumna(table, columnas.get(PLAN), "Plan", true, true, "");
		this.configColumna(table, columnas.get(LINEA), "L�nea", true, true, "");
		this.configColumna(table, columnas.get(FASE), "Fase", true, true, "");
		this.configColumna(table, columnas.get(RECIBO), "Recibo", true, true, "");
		this.configColumna(table, columnas.get(REFERENCIA), "P�liza", true, true, "");
		this.configColumna(table, columnas.get(IMPORTE_SALDO_PDTE), "Saldo pendiente", true, true, "", STYLE);
		this.configColumna(table, columnas.get(IMPORTE_COBRO_RECIBIDO), "Cobro recibido", true, true, "", STYLE);
		this.configColumna(table, columnas.get(GRUPO_NEGOCIO), "G.N.", false, true, "", "text-align:center");
		if (perfil.compareTo("0") == 0) {
			this.configColumna(table, columnas.get(GA_ADMIN), "Total gastos admin.", true, true, "", STYLE);
			this.configColumna(table, columnas.get(GA_ADQ), "Total gastos Adq.", true, true, "", STYLE);
			this.configColumna(table, columnas.get(GA_COMMED_ENTIDAD), "Total gastos ent.", true, true, "", STYLE);
			this.configColumna(table, columnas.get(GA_COMMED_ESMED), "Total gastos ES-Med", true, true, "", STYLE);
		}

		if (perfil.compareTo("5") == 0 || (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == false)) {
			this.configColumna(table, columnas.get(GA_COMMED_ENTIDAD), "Total gastos ent.", true, true, "", STYLE);
			this.configColumna(table, columnas.get(GA_COMMED_ESMED), "Total gastos ES-Med", true, true, "", STYLE);
		}

		if (perfil.compareTo("1") == 0 && usuario.isUsuarioExterno() == true) {
			this.configColumna(table, columnas.get(GA_COMMED_ESMED), "Total gastos ES-Med", true, true, "", STYLE);
		}

	}

	private CellEditor getCellEditorEsMediadora() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				Integer entidadMed = (Integer) new BasicCellEditor().getValue(item, ENTMEDIADORA, rowcount);
				Integer subEntidadMed = (Integer) new BasicCellEditor().getValue(item, SUBENTMEDIADORA, rowcount);

				HtmlBuilder html = new HtmlBuilder();
				if (entidadMed != null && subEntidadMed != null) {
					html.append(StringUtils.nullToString(entidadMed) + "-" + StringUtils.nullToString(subEntidadMed));
				} else {
					html.append("&nbsp;");
				}
				return html.toString();
			}
		};
	}

	public void setInformesImpagadosUnificadoFilter(IGenericoFilter informesImpagadosUnificadoFilter) {
		this.informesImpagadosUnificadoFilter = informesImpagadosUnificadoFilter;
	}

	public void setInformesImpagadosUnificadoSort(IGenericoSort informesImpagadosUnificadoSort) {
		this.informesImpagadosUnificadoSort = informesImpagadosUnificadoSort;
	}

	public List<Serializable> getAllFilteredAndSorted(IGenericoDao genericoDao) throws BusinessException {

		Collection<Serializable> allResults = null;
		allResults = genericoDao.getWithFilterAndSort(informesImpagadosUnificadoFilter,
				informesImpagadosUnificadoSort, -1, -1);
		return (List<Serializable>) allResults;
	}

}
