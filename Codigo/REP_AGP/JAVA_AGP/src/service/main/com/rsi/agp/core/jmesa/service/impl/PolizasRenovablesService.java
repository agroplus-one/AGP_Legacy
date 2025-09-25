package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IPolizasRenovablesDao;
import com.rsi.agp.core.jmesa.filter.PolizasRenovablesFilter;
import com.rsi.agp.core.jmesa.service.IPolizasRenovablesService;
import com.rsi.agp.core.jmesa.sort.PolizasRenovablesSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.managers.impl.ContratacionRenovacionesHelper;
import com.rsi.agp.core.managers.impl.PolizasPctComisionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.filters.admin.impl.TomadorFiltro;
import com.rsi.agp.dao.filters.poliza.EstadoRenAgroplusFilter;
import com.rsi.agp.dao.filters.poliza.EstadoRenAgroseguroFilter;
import com.rsi.agp.dao.filters.poliza.EstadoRenEnvioIBANFilter;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableEstadoEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableValidacionEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableValidacionEnvioIBANId;
import com.rsi.agp.dao.tables.renovables.VistaPolizaRenovable;

@SuppressWarnings("deprecation")
public class PolizasRenovablesService implements IPolizasRenovablesService {
	
	private final NumberFormat df = NumberFormat.getInstance(new Locale("es", "ES"));
	
	// Caracter por el que se separan los ids de poliza
	private final String CHAR_SEPARADOR_IDS = ",";
	private ContratacionRenovacionesHelper contratacionRenovacionesHelper   = new ContratacionRenovacionesHelper();
	private IPolizasRenovablesDao polizasRenovablesDao;
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id = "polRenovables";
	private PolizasPctComisionesManager polizasPctComisionesManager;
	private IPolizasPctComisionesDao polizasPctComisionesDao;

	// Constantes para los nombres de las columnas del listado
	private final static String CAMPO_ID = "id";
	private final static String ENTIDAD = "ENTIDAD";
	private final static String TOMADOR = "TOMADOR";
	private final static String PLAN = "PLAN";
	private final static String LINEA = "LINEA";
	private final static String COLECTIVO = "COLECTIVO";
	private final static String COLECTIVO_ENT_MED = "codentidadmed";
	private final static String COLECTIVO_SUBENT_MED = "codsubentmed";
	private final static String REFERENCIA = "REFERENCIA";
	private final static String ASEGURADO = "ASEGURADO";
	private final static String ESTADOSAGROPLUS = "ESTADOSAGROPLUS";
	private final static String ESTADOSAGROSEGURO = "ESTADOSAGROSEGURO";
	private final static String ESTADOSENVIOIBAN = "ESTADOSENVIOIBAN";
	private final static String FECHACARGA = "fechaCarga";
	private final static String FECHARENOVACION = "fechaRenovacion";
	private final static String FECHAENVIOIBAN = "fechaEnvioIban";
	private final static String PORCENTAJE_COMISION = "PORCENTAJE_COMISION";
	private final static String COMISION_ENTIDAD = "COMISION_ENTIDAD";
	private final static String COMISION_ESMED = "COMISION_ESMED";
	public  final static String CAMPO_LISTADOGRUPOENT = "listaGrupoEntidades";
	private final static String COSTE_TOMADOR = "COSTE_TOTAL_TOMADOR";
	private final static String GRUPO_NEGOCIO = "GRUPO_NEGOCIO";
	
	private final static String COMISIONAPL = "comisionApl";
	private final static String ENTIDADAPL = "entidadApl";
	private final static String ESMEDAPL = "esMedApl";
	private final static String PRIMACOMERCIALNETA = "primaComercialNeta";
	private final static String COSTETOMADORANTERIOR = "costeTomadorAnterior";
	private final static String DOMICILIADO = "domiciliado";
	private final static String DESTDOMIC = "dest_domic";
	private final static String IBAN = "iban";
	
	private static final String TEXT_ALIGN_CENTER = "text-align: center;";
	private static final String NBSP = "&nbsp;";
	private static final String ALERTA = "alerta";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	
	private PolizasRenovablesFilter consultaFilter;
	private PolizasRenovablesSort consultaSort;
	
	public Collection<VistaPolizaRenovable> getPolRenovablesWithFilterAndSort(
			final PolizasRenovablesFilter filter,
			final PolizasRenovablesSort sort, int rowStart, int rowEnd, String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
			String fecEnvioIBANIni, String fecEnvioIBANFin, String grupoNegocio, String estAgroplus) throws BusinessException {

		return polizasRenovablesDao.getPolRenovablesWithFilterAndSort(filter,sort, rowStart, rowEnd,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin,grupoNegocio,estAgroplus);

	}

	
	public int getPolRenovablesCountWithFilter(PolizasRenovablesFilter filter,String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
			String fecEnvioIBANIni, String fecEnvioIBANFin, String grupoNegocio,String estAgroplus)	throws BusinessException {

		return polizasRenovablesDao.getPolRenovablesCountWithFilter(filter,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin,grupoNegocio,estAgroplus);
	}

	public String getTablaPolRenovables(HttpServletRequest request,
			HttpServletResponse response, VistaPolizaRenovable polizaRenovableBean,
			String origenLlamada, List<Long> listaGrupoEntidades, Usuario usuario,List <GruposNegocio> gruposNegocio) {
		
		String fecCargaIni     = (String) request.getParameter("fechaCargaIni");
		String fecCargaFin     = (String) request.getParameter("fechaCargaFin");
		String fecRenoIni      = (String) request.getParameter("fechaRenoIni");
		String fecRenoFin      = (String) request.getParameter("fechaRenoFin");
		String fecEnvioIBANIni = (String) request.getParameter("fechaEnvioIBANIni");
		String fecEnvioIBANFin = (String) request.getParameter("fechaEnvioIBANFin");
		String grupoNegocio    = (String) request.getParameter("grupoNegocio");
		String estAgroplus     = (String) request.getParameter("estAgroplus");
		
		if (fecCargaIni == null || fecCargaIni.equals("")) {
			fecCargaIni   = (String) request.getAttribute("fechaCargaIni");
		}
		if (fecCargaFin == null || fecCargaFin.equals("")) {
			fecCargaFin   = (String) request.getAttribute("fechaCargaFin");
		}
		if (fecRenoIni == null || fecRenoIni.equals("")) {
			fecRenoIni    = (String) request.getAttribute("fechaRenoIni");
		}
		if (fecRenoFin == null || fecRenoFin.equals("")) {
			fecRenoFin    = (String) request.getAttribute("fechaRenoFin");
		}
		if (fecEnvioIBANIni == null || fecEnvioIBANIni.equals("")) {
			fecEnvioIBANIni    = (String) request.getAttribute("fechaEnvioIBANIni");
		}
		if (fecEnvioIBANFin == null || fecEnvioIBANFin.equals("")) {
			fecEnvioIBANFin    = (String) request.getAttribute("fechaEnvioIBANFin");
		}
		if (grupoNegocio == null || grupoNegocio.equals("")) {
			grupoNegocio   = (String) request.getAttribute("grupoNegocio");
		}
		if (estAgroplus == null || estAgroplus.equals("")) {
			estAgroplus   = (String) request.getAttribute("estAgroplus");
		}
		
		//String estAgroplus   = "";
		String estAgroseguro = "";
		String estEnvioIBAN  = "";
		if (null != origenLlamada && origenLlamada.equals("primeraBusqueda")) {
			estAgroplus   = (String) request.getParameter("estAgroplus");
			estAgroseguro = (String) request.getParameter("estAgroseguro");
			estEnvioIBAN  = (String) request.getParameter("estEnvioIBAN");
			
//			if (estAgroplus !=null && !estAgroplus.equals("")) {
//				polizaRenovableBean.getEstadoRenovacionAgroplus().setCodigo(new Long(estAgroplus));
//			}
			if (estAgroseguro !=null && !estAgroseguro.equals("")) {
				polizaRenovableBean.setEstagroseguro(new BigDecimal(estAgroseguro));
			}
			if (estEnvioIBAN !=null && !estEnvioIBAN.equals("")) {
				polizaRenovableBean.setEstadoIban(new BigDecimal(estEnvioIBAN));
			}
			
		}
		logger.debug("DATOS FECHAS: fecCargaIni: "+fecCargaIni+" fecCargaFin: "+fecCargaFin+" fecRenoIni: "+fecRenoIni+" fecRenoFin: "+fecRenoFin+
				" fecEnvioIBANIni: "+fecEnvioIBANIni+" fecEnvioIBANFin: "+fecEnvioIBANFin + " grupoNegocio: "+grupoNegocio+ " estAgroplus: "+estAgroplus);	
		
		Map<Character,String> mapGruposNegocio =  new HashMap<Character,String>();
		for (GruposNegocio gr:gruposNegocio){
			mapGruposNegocio.put(gr.getGrupoNegocio(), gr.getDescripcion());
		}
		
		TableFacade tableFacade = crearTableFacade(request, response, polizaRenovableBean, origenLlamada,usuario);

		Limit limit = tableFacade.getLimit();
		PolizasRenovablesFilter consultaFilter = getConsultaPolRenovablesFilter(limit, listaGrupoEntidades);

		setDataAndLimitVariables(tableFacade,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin, listaGrupoEntidades,grupoNegocio,estAgroplus);

		String listaIdsTodos = getlistaIdsTodos(consultaFilter,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin,grupoNegocio,estAgroplus);
		String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		
		/*tableFacade.setToolbar(new CustomToolbarMarcarTodos());
		tableFacade.setView(new CustomView());*/
		// Si se esta generando un informe no se establecen los custom
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax) && request.getParameter("export") == null){
			tableFacade.setToolbar(new CustomToolbarMarcarTodos());
			tableFacade.setView(new CustomView());
		}
		

		return html (tableFacade,usuario,mapGruposNegocio) + script;
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, VistaPolizaRenovable polizaRenovable,
			String origenLlamada, Usuario usuario) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		
		tableFacade.setStateAttr("restore");// return to the table in the same
		// state that the user left it.
		
		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade,usuario);
		
		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("consultaPolRenovables_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaPolRenovables_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(columnas, polizaRenovable, tableFacade);
			}
		}

		return tableFacade;
	}

	/**
	 * U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade, Usuario usuario) {
		String perfil= usuario.getPerfil().substring(4);
		Limit limit = tableFacade.getLimit();
		
		
		boolean isUsuP1Ex = false;
		
		if(perfil.equals("1") && usuario.isUsuarioExterno()==true)
			isUsuP1Ex = true;
		
		boolean isInforme = false; // false es exportación
		if(limit.isExported())
			isInforme = true;
		
		if (columnas.isEmpty()) crearMapaColumnas();

		if (!isUsuP1Ex && !isInforme){
			cargarColumnasListado(tableFacade);
		}
		
		if (!isUsuP1Ex && isInforme){
			cargarColumnasInforme(tableFacade);
		}
		
		if (isUsuP1Ex && !isInforme){
			cargarColumnasListadoP1Ext(tableFacade);
		}
		
		if (isUsuP1Ex && isInforme){
			cargarColumnasInformeP1Ext(tableFacade);
		}
		
		columnas.put(ENTIDAD, "codentidad");
		columnas.put(TOMADOR, "nifTomador");
		
		columnas.put(FECHAENVIOIBAN, "fechaEnvioIbanAgro");
		columnas.put(PRIMACOMERCIALNETA,PRIMACOMERCIALNETA);
		columnas.put(COSTETOMADORANTERIOR,COSTETOMADORANTERIOR);
		columnas.put(DOMICILIADO,DOMICILIADO);
		columnas.put(DESTDOMIC,DESTDOMIC);
		columnas.put(IBAN,"iban");
		
		return columnas;
	}
	
	
	/**
	 * Carga el mapa 'columnas' con todas las posibles columnas que puedan contener los listados
	 */
	private void crearMapaColumnas(){
		columnas.put(CAMPO_ID, "id");
		columnas.put(ENTIDAD, "codentidad");
		columnas.put(TOMADOR, "nifTomador");
		columnas.put(PLAN, "plan");
		columnas.put(LINEA, "linea");
		columnas.put(GRUPO_NEGOCIO, "gruponegocio");
		columnas.put(COLECTIVO, "refcol");
		columnas.put(REFERENCIA, "referencia");
		columnas.put(ASEGURADO, "nifAsegurado");
		columnas.put("ESTADOSAGROPLUS_DESC","descagroplus");
		columnas.put(ESTADOSAGROPLUS,"estagroplus");
		columnas.put("ESTADOSAGROSEGURO_DESC",	"descagroseguro");
		columnas.put(ESTADOSAGROSEGURO,	"estagroseguro");
		columnas.put(FECHARENOVACION, FECHARENOVACION);
		columnas.put(PORCENTAJE_COMISION, "pctComision");
		columnas.put(COMISION_ENTIDAD, "pctEntidad");
		columnas.put(COMISION_ESMED, "pctESMed");
		columnas.put(COMISIONAPL, COMISIONAPL);
		columnas.put(ENTIDADAPL, ENTIDADAPL);
		columnas.put(ESMEDAPL, ESMEDAPL);
		columnas.put(COLECTIVO_ENT_MED, COLECTIVO_ENT_MED);
		columnas.put(COLECTIVO_SUBENT_MED, COLECTIVO_SUBENT_MED);
		columnas.put(COSTE_TOMADOR, "costeTotalTomador");
		columnas.put(ESTADOSENVIOIBAN,"estadoIban");
		columnas.put(FECHAENVIOIBAN, "fechaEnvioIbanAgro");
		columnas.put(PRIMACOMERCIALNETA,PRIMACOMERCIALNETA);
		columnas.put(COSTETOMADORANTERIOR,COSTETOMADORANTERIOR);
		columnas.put(DOMICILIADO,DOMICILIADO);
		columnas.put(DESTDOMIC,DESTDOMIC);
		columnas.put(IBAN,"iban");
	}
	
	// no es perfil 1 externo y se está mostrando un listado
	private void cargarColumnasListado(TableFacade tableFacade){
		tableFacade.setColumnProperties(columnas.get(CAMPO_ID),
				columnas.get(COLECTIVO_ENT_MED),
				columnas.get(COLECTIVO_SUBENT_MED),
				columnas.get(PLAN),
				columnas.get(LINEA),
				columnas.get(GRUPO_NEGOCIO),
				columnas.get(COLECTIVO),
				columnas.get(REFERENCIA),
				columnas.get(ASEGURADO),
				columnas.get(ESTADOSAGROPLUS),
				columnas.get(ESTADOSAGROSEGURO),
				columnas.get(FECHARENOVACION),
				columnas.get(PORCENTAJE_COMISION),
				columnas.get(COMISION_ENTIDAD),
				columnas.get(COMISION_ESMED),
				columnas.get(COMISIONAPL),
				columnas.get(ENTIDADAPL),
				columnas.get(ESMEDAPL),
				columnas.get(COSTE_TOMADOR),
				columnas.get(ESTADOSENVIOIBAN));				
	}
	
	//no es perfil 1 externo y se está generando un informe
	private void cargarColumnasInforme(TableFacade tableFacade){
		tableFacade.setColumnProperties(columnas.get(ENTIDAD),
				columnas.get(TOMADOR),
				columnas.get(COLECTIVO_ENT_MED),
				columnas.get(COLECTIVO_SUBENT_MED),
				columnas.get(PLAN),
				columnas.get(LINEA),
				columnas.get(GRUPO_NEGOCIO),
				columnas.get(COLECTIVO),
				columnas.get(REFERENCIA),
				columnas.get(ASEGURADO),
				columnas.get(ESTADOSAGROPLUS),
				columnas.get(ESTADOSAGROSEGURO),
				//columnas.get(FECHACARGA),
				columnas.get(FECHARENOVACION),
				columnas.get(PORCENTAJE_COMISION),
				columnas.get(COMISION_ENTIDAD),
				columnas.get(COMISION_ESMED),
				columnas.get(COMISIONAPL),
				columnas.get(ENTIDADAPL),
				columnas.get(ESMEDAPL),
				columnas.get(COSTE_TOMADOR),
				columnas.get(ESTADOSENVIOIBAN),
				columnas.get(FECHAENVIOIBAN),
				columnas.get(PRIMACOMERCIALNETA),
				columnas.get(COSTETOMADORANTERIOR),
				columnas.get(DOMICILIADO),
				columnas.get(DESTDOMIC),
				columnas.get(IBAN));
	}
	
	// es perfil 1 externo y se está mostrando un listado
	private void cargarColumnasListadoP1Ext(TableFacade tableFacade){
		tableFacade.setColumnProperties(columnas.get(CAMPO_ID),
				columnas.get(COLECTIVO_ENT_MED),
				columnas.get(COLECTIVO_SUBENT_MED),
				columnas.get(PLAN),
				columnas.get(LINEA),
				columnas.get(GRUPO_NEGOCIO),
				columnas.get(COLECTIVO),
				columnas.get(REFERENCIA),
				columnas.get(ASEGURADO),
				columnas.get(ESTADOSAGROPLUS),
				columnas.get(ESTADOSAGROSEGURO),
				columnas.get(FECHARENOVACION),
				columnas.get(COMISION_ESMED),
				columnas.get(ESMEDAPL),
				columnas.get(COSTE_TOMADOR),
				columnas.get(ESTADOSENVIOIBAN));
	}
	
	// es perfil 1 externo y se está generando un informe
	private void cargarColumnasInformeP1Ext(TableFacade tableFacade){
		tableFacade.setColumnProperties(columnas.get(ENTIDAD),
				columnas.get(TOMADOR),
				columnas.get(COLECTIVO_ENT_MED),
				columnas.get(COLECTIVO_SUBENT_MED),
				columnas.get(PLAN),
				columnas.get(LINEA),
				columnas.get(GRUPO_NEGOCIO),
				columnas.get(COLECTIVO),
				columnas.get(REFERENCIA),
				columnas.get(ASEGURADO),
				columnas.get(ESTADOSAGROPLUS),
				columnas.get(ESTADOSAGROSEGURO),
				//columnas.get(FECHACARGA),
				columnas.get(FECHARENOVACION),				
				columnas.get(COMISION_ESMED),
				columnas.get(ESMEDAPL),
				columnas.get(COSTE_TOMADOR),
				columnas.get(ESTADOSENVIOIBAN),
				columnas.get(FECHAENVIOIBAN),
				columnas.get(PRIMACOMERCIALNETA),
				columnas.get(COSTETOMADORANTERIOR),
				columnas.get(DOMICILIADO),
				columnas.get(DESTDOMIC),
				columnas.get(IBAN));
	}
	
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el
	 * formulario 06/05/2014 U029769
	 * 
	 * @param columnas2
	 * @param usuario
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas2,
			VistaPolizaRenovable polizaRenovable, TableFacade tableFacade) {
		if (polizaRenovable.getCodentidad()!= null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(ENTIDAD), polizaRenovable.getCodentidad().toString()));
		
		if (polizaRenovable.getCodentidadmed() != null){
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(COLECTIVO_ENT_MED), polizaRenovable.getCodentidadmed().toString()));
		}
		
		if (polizaRenovable.getCodsubentmed() != null){
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(COLECTIVO_SUBENT_MED), polizaRenovable.getCodsubentmed().toString()));
		}
		
		if (polizaRenovable.getNifTomador() != null && !polizaRenovable.getNifTomador().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(TOMADOR), polizaRenovable.getNifTomador()));
		
		if (polizaRenovable.getPlan() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(PLAN), polizaRenovable.getPlan().toString()));
		
		if (polizaRenovable.getLinea() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(LINEA), polizaRenovable.getLinea().toString()));
		
		if (polizaRenovable.getRefcol() != null && !polizaRenovable.getRefcol().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(COLECTIVO), polizaRenovable.getRefcol()));
		
		if (polizaRenovable.getReferencia() != null && !polizaRenovable.getReferencia().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(REFERENCIA), polizaRenovable.getReferencia()));
		
		if (polizaRenovable.getNifAsegurado() != null && !polizaRenovable.getNifAsegurado().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(ASEGURADO), polizaRenovable.getNifAsegurado()));
		if (polizaRenovable.getEstagroplus() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(ESTADOSAGROPLUS),polizaRenovable.getEstagroplus().toString()));
		
		if (polizaRenovable.getEstagroseguro() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(ESTADOSAGROSEGURO), polizaRenovable.getEstagroseguro().toString()));
		
		if (polizaRenovable.getEstadoIban() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(ESTADOSENVIOIBAN), polizaRenovable.getEstadoIban().toString()));
		
		// Fecha carga
		if (polizaRenovable.getFechaCarga() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
				new Filter(columnas.get(FECHACARGA), new SimpleDateFormat(PolizasRenovablesService.DD_MM_YYYY).format(polizaRenovable.getFechaCarga())));
		
		// Fecha renovacion
		if (polizaRenovable.getFechaRenovacion() != null)
			tableFacade.getLimit().getFilterSet().addFilter( 
				new Filter(columnas.get(FECHARENOVACION), new SimpleDateFormat(PolizasRenovablesService.DD_MM_YYYY).format(polizaRenovable.getFechaRenovacion())));
		
		// Fecha envío IBAN
				if (polizaRenovable.getFechaEnvioIbanAgro() != null)
					tableFacade.getLimit().getFilterSet().addFilter( 
						new Filter(columnas.get(FECHAENVIOIBAN), new SimpleDateFormat(PolizasRenovablesService.DD_MM_YYYY).format(polizaRenovable.getFechaEnvioIbanAgro())));
	}
	
	/**
	 * MPM - 13/3/2015
	 * Realiza la llamada al dao para el cambio masivo de comisión de las pólizas renovables
	 */
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, String comisionMasiva, String usuario, String isPerfil0) throws DAOException {
		Map<String, Object> mapaGastosRen = new HashMap<String, Object>();
		Map<String, Object> mapaPolRen = new HashMap<String, Object>();
		
		Map<String, String> parameters = new HashMap<String, String>();
		boolean aplicarCM = true;
		//boolean comisionesInformadas=false;
		try {
			String listaIdsGastos = listaIdsMarcados_cm.substring(0,listaIdsMarcados_cm.length()-1);
			List<String> lstGastosRen = Arrays.asList(listaIdsGastos.split(","));
			List<String> lstGastosRenFinal = new ArrayList<String>();
			BigDecimal comEntidad = new BigDecimal(0);

			// Obtiene el objeto EstadoRenovacionAgroplus que se va a establecer en todas las pólizas
			EstadoRenovacionAgroplus estAgpGastosAsignados = polizasRenovablesDao.getEstadorenovacionAgroplus(Constants.PLZ_RENOV_AGP_GASTOS_ASIGNADOS);
			EstadoRenovacionAgroplus estAgpPendienteAsigGastos = polizasRenovablesDao.getEstadorenovacionAgroplus(Constants.PLZ_RENOV_AGP_PENDIENTE_ASIG_GASTOS);
			for (String id : lstGastosRen) {
				GastosRenovacion gasRen  = polizasRenovablesDao.getGastosRenovacionById(Long.parseLong(id));
				Long idPoliza = gasRen.getPolizaRenovable().getId();
				PolizaRenovable plz = polizasRenovablesDao.getPolizaById(idPoliza);
				Long Lineaseguroid  = polizasRenovablesDao.getLineaSeguroId(plz.getPlan(),plz.getLinea());
				BigDecimal codEntidadMed = new BigDecimal(plz.getColectivoRenovacion().getCodentidadmed().toString());
				BigDecimal codSubEntMed  = new BigDecimal(plz.getColectivoRenovacion().getCodsubentmed().toString());
				BigDecimal plan 		 = new BigDecimal(plz.getPlan().toString());
				BigDecimal linea 		 = new BigDecimal(plz.getLinea().toString());
				Date fechaRenovacion 	 = plz.getFechaRenovacion(); 
				/* recogemos los datos del mto de parametros generales*/
				@SuppressWarnings("rawtypes")
				List lstParams= polizasPctComisionesDao.getParamsGen (Lineaseguroid,codEntidadMed,codSubEntMed,plz.getFechaRenovacion());
				/*
				if(resultado!=null && resultado.size()>0){
					comisionesInformadas=polizasPctComisionesManager.comisionesPorGrupoNegocioInformadas(Lineaseguroid, resultado);	
					paramsGen = (Object[]) resultado.get(0);
				}		
				*/
				if (lstParams != null && lstParams.size()>0) {
					/* recogemos los datos del mto de comisiones por E-S Mediadora */
					Object[] comisionesESMed = polizasPctComisionesDao.getComisionesESMed (Lineaseguroid,codEntidadMed,
							codSubEntMed,linea,plan,fechaRenovacion);
					if (comisionesESMed != null) {
						//recogemos los porcentajes que necesitamos
						List<PolizaPctComisiones> lstPpc = polizasPctComisionesManager.generaPolizaComisiones( lstParams,
								comisionesESMed, null, plz.getId(), Lineaseguroid);
						
						
						if (lstPpc != null)
							logger.debug("lstPpc size: "+lstPpc.size());
						
						// Calculamos la comision Entidada y la comision E-S Mediadora
						boolean encontrado = false;
						for (PolizaPctComisiones pc:lstPpc) {
							logger.debug("isPerfil0 " +  isPerfil0 + " pc grupo negocio: "+pc.getGrupoNegocio() + "gasRen.getGrupoNegocio() "+gasRen.getGrupoNegocio());
							if (pc.getGrupoNegocio().equals(gasRen.getGrupoNegocio()) || pc.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_GENERICO)) {
								BigDecimal valorPopup = new BigDecimal(comisionMasiva);
								
								// VALOR PARA LA ENTIDAD
								if (isPerfil0.equals("true")){									
									/// #### REVISAR valorPopup x pctEntidad/100
									comEntidad = valorPopup.multiply(pc.getPctentidad().divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_FLOOR);
								}else{
									comEntidad = pc.getPctentidad().multiply(pc.getPctcommax().divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_FLOOR);
								}

								BigDecimal comEntidadFinal = StringUtils.eliminarCerosDerBigDecimal(comEntidad, 2, 0);
								gasRen.setComEntidad(comEntidadFinal);
								logger.debug("gasRen.getComEntidad "+gasRen.getComEntidad());	
								
								// VALOR PARA LA E-S MEDIADORA
								BigDecimal comESMedFinal = null;
								if (isPerfil0.equals("true")){
									/// #### REVISAR valorPopup x getPctesmediadora/100
									comESMedFinal = valorPopup.multiply(pc.getPctesmediadora().divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_FLOOR);
								}else{
									comESMedFinal = StringUtils.eliminarCerosDerBigDecimal(new BigDecimal(comisionMasiva), 2, 0);
								}
							
								gasRen.setComESMed(comESMedFinal);
								logger.debug("gasRen.getComESMed "+gasRen.getComESMed());
								
								
								// COMISIÓN A ENVIAR A AGROSEGURO
								if (isPerfil0.equals("true")){
									/// #### REVISAR valorPopup que ya est la suma de ambas comisiones y lo que se enviará
									gasRen.setComMediador(StringUtils.eliminarCerosDerBigDecimal(valorPopup, 2, 0));
								}else{
									gasRen.setComMediador((gasRen.getComEntidad().add(gasRen.getComESMed())));
								}
								logger.debug("gasRen.getComMediador "+gasRen.getComMediador());
								mapaGastosRen.put(id, gasRen);
								mapaPolRen.put(id,plz);
								lstGastosRenFinal.add(id);
								encontrado = true;
								logger.debug("encontrado: true "+pc.getGrupoNegocio());
								break;
							}
						}
						if (encontrado) {
							logger.debug(" idPoliza: "+idPoliza+" referencia: "+plz.getReferencia()+" comEntidad: "+ gasRen.getComisionEntidad()+" comESMed: "+gasRen.getComisionESMediadora()+" comisionMediador: "+gasRen.getComisionMediador());
						}else {
							logger.debug(" ## No hay parametros de mantenimiento para el grupo de negocio específico ni genérico para la póliza con id: "+idPoliza+" y referencia: "+plz.getReferencia());
							// alert sin parametros grupo negocio
							//aplicarCM = false;
							//break;
						}
					}else {
						logger.debug(" No hay parametros de comisiones ESMed para la póliza con id:"+idPoliza+" y referencia: "+plz.getReferencia());
						//parameters.put("alerta", bundle.getString("alerta.cambioMasivo.polizasrenovables.comisionesESMed.KO") +" en la poliza renovable con referencia "+plz.getReferencia());
						//aplicarCM = false;
						//break;
					}
				}else {
					logger.debug(" No hay parametros generales para la póliza con id: "+id+" referencia: "+plz.getReferencia());
					//parameters.put("alerta", bundle.getString("alerta.cambioMasivo.polizasrenovables.parametrosGenerales.KO") +" en la poliza renovable con referencia "+plz.getReferencia());
					//aplicarCM = false;
					//break;
				}

			} // fin bucle ids
			
			if (aplicarCM) {
				for (String id : lstGastosRenFinal) {
					GastosRenovacion gRen = (GastosRenovacion) mapaGastosRen.get(id);
					PolizaRenovable polRen = (PolizaRenovable) mapaPolRen.get(id);
					polizasRenovablesDao.cambioMasivo( gRen,polRen, estAgpGastosAsignados, estAgpPendienteAsigGastos, usuario);
				}
				parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
			}
		} 
		catch (Exception e) {
			logger.error("Error al ejecutar el Cambio Masivo de pólizas renovables ", e);	
			parameters.put("mensaje", bundle.getString("mensaje.modificacion.KO"));
		}
		
		return parameters;		
	}

    public Map<String, String> validarEnvioIBAN(List<String> lstCadenasIds, boolean marcar, String usuario,List<String> lstPlzRenov){ // throws DAOException
		boolean res = false;
		Map<String, String> parametros = new HashMap<String, String>();
		
		Object[] registro 	  = null;
		String nifCif      	  = "";
		Integer totalCuentas  = 0;
		BigDecimal codEntidad = null;
		BigDecimal codEntMed  = null;
		BigDecimal subEntMed  = null;
		BigDecimal codLinea   = null;
		String referencia     = "";
		String dc             = "";
		Long idAsegurado 	  = null;
		boolean noAsegurado   = false;
		boolean noCuenta      = false;
		int correctas = 0;
		int erroneas  = 0;
		boolean primerError   = true;
		BigDecimal secuencia  = null;
		try {
			//List<String> lstPlzRenov = Arrays.asList(listaIds.split(","));
			// validaciones previas
			res = polizasRenovablesDao.validacionesPreviasEnvioIBAN(lstCadenasIds, marcar, usuario,lstPlzRenov.size());
			if (marcar) { // **MARCAR**
				if (!res) {
					parametros.put(PolizasRenovablesService.ALERTA, bundle.getString("alerta.PolizaRenovable.envioIBAN.marcarParaEnviar.KO"));
				}else {
					if (lstPlzRenov.size()>0) {
						for (String idPolRen:lstPlzRenov) {
							noAsegurado = false;
							noCuenta    = false;
							try {
								// 1 - recogemos Parametros previos de la póliza renovable
								registro = polizasRenovablesDao.recogerParametrosPolizaRenovable(idPolRen);
								nifCif     = (String)registro[0];	
								codEntidad = (BigDecimal)registro[1];
								codEntMed  = (BigDecimal)registro[2];
								subEntMed  = (BigDecimal)registro[3];
								codLinea   = (BigDecimal)registro[4];
								referencia = (String)registro[5];
								dc         = (String)registro[6];	
							    logger.debug(" nifCif:"+nifCif+ " codEntidad:"+codEntidad+" codEntMed:"+codEntMed+" subEntMed:"+subEntMed+
							    		" codLinea: "+codLinea+ " referencia: "+referencia + " dc:"+dc);
							}catch (Exception e) {
								logger.error("Ocurrió un error al obtener los datos de la poliza renovable "+idPolRen,  e);
							}
							try {
							    if ((nifCif != null && !nifCif.equals("")) && codEntidad != null && codEntMed != null && subEntMed != null){
							    	PolizaRenovableValidacionEnvioIBAN pRenVal = new PolizaRenovableValidacionEnvioIBAN();
							    	// 2.1 - Comprobamos si existe el asegurado de la póliza renovable
									idAsegurado = polizasRenovablesDao.comprobarAsegurado(nifCif, codEntidad, codEntMed, subEntMed);
									logger.debug(" idAsegurado: "+idAsegurado);	
									if ((idAsegurado) != null) {
										// 2.2 - Comprobamos si el asegurado tiene cuenta en la línea específica o bien en la genérica
										totalCuentas = polizasRenovablesDao.comprobarCuentasAsegurado(idAsegurado,codLinea);
										if (totalCuentas != null && totalCuentas.compareTo(new Integer(0)) > 0) {
											correctas++;
										}else {
											noCuenta = true;
											erroneas++;
										}
									}else {
										noAsegurado = true;
										erroneas++;
									}
									// 3 - Si hay errores se guarda el resultado del error del Asegurado. TipoError 0 - No existe el asegurado 1- Asegurado sin cuenta
									if ((noAsegurado) || (noCuenta)) {									
										if (primerError) { // llamar a recoger sencuencia									
											secuencia = polizasRenovablesDao.recogerSecuenciaValidaEnvioIBAN();
											primerError = false;
										}
										guardarDatosErrorAseguradoIBAN(pRenVal,secuencia,noCuenta,noAsegurado,referencia,dc,codEntMed,subEntMed,nifCif);
									}
									parametros.put("mostrarResultadoEnvioIBAN","true");
								}
							}catch (Exception e) {
								logger.info("Ocurrió un error al obtener el idAsegurado de la poliza renovable "+idPolRen,  e);
							}
						}
					}
				}
				// 4 - Guardamos los parametros de las marcadas, correctas, erroneas y el id del error si existe
				String seleccionEnvioIBAN = (marcar)?"true":"false";
				parametros.put("seleccionEnvioIBAN",seleccionEnvioIBAN);
				parametros.put("marcadasIBAN" ,Integer.toString(lstPlzRenov.size()));
				parametros.put("correctasIBAN",Integer.toString(correctas));
				parametros.put("erroneasIBAN" ,Integer.toString(erroneas));
				if (secuencia != null)
					parametros.put("idErroresIBAN",secuencia.toString());
			}else { // **DESMARCAR**
				if (!res) {
					parametros.put(PolizasRenovablesService.ALERTA, bundle.getString("alerta.PolizaRenovable.envioIBAN.desmarcarParaEnviar.KO"));
				}else {
					// Procedemos a la actualización de los estados de las pólizas renovables seleccionadas.
					// si viene de marcar: actualizo a  2-Preparado. Desmarcar: a 1-No	
					parametros = modificarEstadoEnvioIBAN(lstCadenasIds,marcar,usuario,lstPlzRenov);
				}	
			}
			//parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
		}catch (Exception e) {
			logger.error("PolizasRenovablesService - Error al ejecutar la validación del envío IBAN de pólizas renovables ", e);	
			parametros.put(PolizasRenovablesService.ALERTA, bundle.getString("alerta.PolizaRenovable.envioIBAN.KO"));
		}
		
		return parametros;		
	}
	
	
	
    public Map<String, String> modificarEstadoEnvioIBAN(List<String> lstCadenasIds, boolean marcar, String usuario, List<String> lstPlzRenov){ // throws DAOException
		Map<String, String> parametros = new HashMap<String, String>();
		String estado = "";
		try {
			// actualiza estados: si marcar es true actualiza a 2-Preparado. Si es false actualiza a 1-No
			estado = (marcar)?Constants.ES_POL_REN_ENVIO_IBAN_PREPARADO.toString():Constants.ES_POL_REN_ENVIO_IBAN_NO.toString();
			polizasRenovablesDao.modificarEstadoEnvioIBAN(lstCadenasIds, estado, usuario);

		//Insertamos en el historico por cada póliza renovable actualizada
		for (String id:lstPlzRenov) {
			polizasRenovablesDao.updatePolRenEnvioIBANHisEstados(id,estado,usuario);
		}
		parametros.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
		}catch (Exception e) {
			logger.error("Error al ejecutar la modificación del envío IBAN de pólizas renovables ", e);	
			parametros.put(PolizasRenovablesService.ALERTA, bundle.getString("alerta.PolizaRenovable.envioIBAN.KO"));
		}
		
		return parametros;		
	}
	
    public Map<String, List<PolizaRenovableValidacionEnvioIBAN>> getPolRenValidacionEnvioIBAN(String idErroresIBAN){ // throws DAOException
		Map<String, List<PolizaRenovableValidacionEnvioIBAN>> mapaListas = new HashMap<String, List<PolizaRenovableValidacionEnvioIBAN>>();
		List<PolizaRenovableValidacionEnvioIBAN> lstErroresEnvioIBAN   = new ArrayList<PolizaRenovableValidacionEnvioIBAN>(); 
		List<PolizaRenovableValidacionEnvioIBAN> lstErroresNoAsegurado = new ArrayList<PolizaRenovableValidacionEnvioIBAN>(); 
		List<PolizaRenovableValidacionEnvioIBAN> lstErroresNoCuenta    = new ArrayList<PolizaRenovableValidacionEnvioIBAN>(); 
		try {
			lstErroresEnvioIBAN = polizasRenovablesDao.getPolRenValidacionEnvioIBAN(idErroresIBAN); 
		} catch (Exception e) {
			logger.error("PolizasRenovablesService - Error en getPolRenValidacionEnvioIBAN de pólizas renovables ", e);
		}
		for (PolizaRenovableValidacionEnvioIBAN err:lstErroresEnvioIBAN) {
			if (err.getId().isTipoError()==0) {
				lstErroresNoAsegurado.add(err);
			}else if (err.getId().isTipoError()==1) {
				lstErroresNoCuenta.add(err);
			}
		}
		mapaListas.put("lstErroresNoAsegurado", lstErroresNoAsegurado);
		mapaListas.put("lstErroresNoCuenta", lstErroresNoCuenta);
		return mapaListas;		
	}
	
	public void guardarDatosErrorAseguradoIBAN(PolizaRenovableValidacionEnvioIBAN pRenVal, BigDecimal secuencia,boolean noCuenta, boolean noAsegurado,
			String referencia, String dc, BigDecimal codEntMed, BigDecimal subEntMed, String nifCif) {
		PolizaRenovableValidacionEnvioIBANId id = new PolizaRenovableValidacionEnvioIBANId();
		id.setId(secuencia.longValue());
		id.setReferencia(referencia+"-"+dc);
		if (noAsegurado)
			id.setTipoError(0);
		if (noCuenta)
			id.setTipoError(1);
		pRenVal.setId(id);
		pRenVal.setEntidadMediadora(codEntMed.intValue());
		pRenVal.setSubentidadMediadora(subEntMed.intValue());
		pRenVal.setNifAsegurado(nifCif);

		try {
			polizasRenovablesDao.saveOrUpdate(pRenVal);
			polizasRenovablesDao.evict(pRenVal);
		} catch (DAOException e) {
			logger.error("PolizasRenovablesService - Error al guardar Datos ErrorAseguradoIBAN en pólizas renovables ", e);	
		}
	}
    
	

	/**
	 * Consulta del servicio web contatacion renovaciones  metodo impresión prorroga
	 * 
	 * @param CodPlan
	 * @param RefPoliza
	 * @param tipoRef
	 * @param realPath
	 * @return
	 * @throws Exception 
	 */
	public Base64Binary imprimirProrroga(String planWs, String referenciaWs, String valorWs,String realPath) throws Exception
	{
		return contratacionRenovacionesHelper.doWorkImprimirProrroga(planWs, referenciaWs, valorWs, realPath);
	}
	
	
	@Override
	public VistaPolizaRenovable getCambioMasivoBeanFromLimit(Limit consultaPlzRenovables_LIMIT) {
		VistaPolizaRenovable plzRenovBean = new VistaPolizaRenovable();
		
		// ID
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(CAMPO_ID))){
			plzRenovBean.setId(new BigDecimal(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(CAMPO_ID)).getValue()));
		}
		
		// ENTIDAD
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD))){
			plzRenovBean.setCodentidad(new Long (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD)).getValue()));
		}

		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_ENT_MED))){
			plzRenovBean.setCodentidadmed(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_ENT_MED)).getValue()));
		}
		
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_SUBENT_MED))){
			plzRenovBean.setCodsubentmed(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_SUBENT_MED)).getValue()));
		}
		// TOMADOR
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(TOMADOR))){
			plzRenovBean.setNifTomador(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(TOMADOR)).getValue());
		}
		
		// PLAN
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(PLAN))){
			plzRenovBean.setPlan(new BigDecimal(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(PLAN)).getValue()));
		}
		
		// LINEA
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(LINEA))){
			plzRenovBean.setLinea(new BigDecimal(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(LINEA)).getValue()));
		}
		
		// REFERENCIA DE PÓLIZA
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(REFERENCIA))){
			plzRenovBean.setReferencia(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(REFERENCIA)).getValue());
		}
		
		// REFERENCIA DE COLECTIVO
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO))){
			plzRenovBean.setRefcol(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO)).getValue());
		}
		
		// ASEGURADO
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ASEGURADO))){
			plzRenovBean.setNifAsegurado(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ASEGURADO)).getValue());
		}
		
		// ESTADO AGROPLUS
//		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROPLUS))){
//			plzRenovBean.getEstadoRenovacionAgroplus().setCodigo(new Long (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROPLUS)).getValue()));
//		}
		
		// ESTADO AGROSEGURO
		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROSEGURO))){
			plzRenovBean.setEstagroseguro(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROSEGURO)).getValue()));
		}
		
		// ESTADO ENVIO IBAN
				if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSENVIOIBAN))){
					plzRenovBean.setEstadoIban(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSENVIOIBAN)).getValue()));
				}
		
		return plzRenovBean;
	}
	
	
	/* Pet. 63482 ** MODIF TAM (29/04/2021) ** Inicio */
	@Override
	public HashMap<String, Object> getAltaRenovableBeanFromLimit(Limit consultaPlzRenovables_LIMIT) {
		VistaPolizaRenovable plzRenovBean = new VistaPolizaRenovable();
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		String conFiltro = "true";
		
		if (consultaPlzRenovables_LIMIT != null) {
			
			// ID
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(CAMPO_ID))){
				plzRenovBean.setId(new BigDecimal(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(CAMPO_ID)).getValue()));
			}
		
			// ENTIDAD
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD))){
				plzRenovBean.setCodentidad(new Long (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD)).getValue()));
			}
	
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_ENT_MED))){
				plzRenovBean.setCodentidadmed(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_ENT_MED)).getValue()));
			}
			
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_SUBENT_MED))){
				plzRenovBean.setCodsubentmed(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO_SUBENT_MED)).getValue()));
			}
			// TOMADOR
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(TOMADOR))){
				plzRenovBean.setNifTomador(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(TOMADOR)).getValue());
			}
			
			// PLAN
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(PLAN))){
				plzRenovBean.setPlan(new BigDecimal(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(PLAN)).getValue()));
			}
			
			// LINEA
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(LINEA))){
				plzRenovBean.setLinea(new BigDecimal(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(LINEA)).getValue()));
			}
			
			// REFERENCIA DE PÓLIZA
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(REFERENCIA))){
				plzRenovBean.setReferencia(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(REFERENCIA)).getValue());
			}
			
			// REFERENCIA DE COLECTIVO
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO))){
				plzRenovBean.setRefcol(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(COLECTIVO)).getValue());
			}
			
			// ASEGURADO
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ASEGURADO))){
				plzRenovBean.setNifAsegurado(consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ASEGURADO)).getValue());
			}
			
			// ESTADO AGROPLUS
	//		if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROPLUS))){
	//			plzRenovBean.getEstadoRenovacionAgroplus().setCodigo(new Long (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROPLUS)).getValue()));
	//		}
			
			// ESTADO AGROSEGURO
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROSEGURO))){
				plzRenovBean.setEstagroseguro(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSAGROSEGURO)).getValue()));
			}
			
			// ESTADO ENVIO IBAN
			if(null != consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSENVIOIBAN))){
				plzRenovBean.setEstadoIban(new BigDecimal (consultaPlzRenovables_LIMIT.getFilterSet().getFilter(columnas.get(ESTADOSENVIOIBAN)).getValue()));
			}
		}else {
			conFiltro = "false";
		}
		
		parameters.put("vistaRenov", plzRenovBean);
		parameters.put("filtro", conFiltro);
		
		return parameters;
		
	}
	/* Pet. 63482 ** MODIF TAM (29/04/2021) ** Fin */
	
	

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los
	 * datos de Polizas Renovables y carga el TableFacade con ellas U029769
	 * 
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
			String fecEnvioIBANIni,String fecEnvioIBANFin, List<Long> listaGrupoEntidades, String grupoNegocio,String estAgroplus) {

		Collection<VistaPolizaRenovable> items = new ArrayList<VistaPolizaRenovable>();
		// Obtiene el Filter para la busqueda de polizas renovables
		Limit limit = tableFacade.getLimit();
		consultaFilter = getConsultaPolRenovablesFilter(limit, listaGrupoEntidades);

		try {
			int totalRows = getPolRenovablesCountWithFilter(consultaFilter,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin,grupoNegocio,estAgroplus);
			logger.debug("********** count filas para PolizasRenovables  = "+ totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			consultaSort = getConsultaPolRenovablesSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getPolRenovablesWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin,grupoNegocio,estAgroplus);
			logger.debug("********** list items para PolizasRenovables  = "+ items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	/**
	 * Crea y configura el Filter para la consulta de usuarios U029769
	 * 
	 * @param limit
	 * @return
	 */
	private PolizasRenovablesFilter getConsultaPolRenovablesFilter(Limit limit,
			List<Long> listaGrupoEntidades) {
		PolizasRenovablesFilter consultaFilter = new PolizasRenovablesFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			consultaFilter.addFilter(property, value);
		}
		// Si la lista de grupos de entidades no esta vacia se incluye en el
		// filtro de busqueda
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			 consultaFilter.addFilter(CAMPO_LISTADOGRUPOENT, listaGrupoEntidades);
		}

		return consultaFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de usuarios U029769
	 * 
	 * @param limit
	 * @return
	 */
	private PolizasRenovablesSort getConsultaPolRenovablesSort(Limit limit) {
		PolizasRenovablesSort consultaSort = new PolizasRenovablesSort();
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
	 * Metodo para construir el html de la tabla a mostrar U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade, Usuario usuario,Map<Character,String> mapGruposNegocio) {
		String perfil= usuario.getPerfil().substring(4);
		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			Table table = tableFacade.getTable();
        	// Quita la columna Id del informe
        	//eliminarColumnaId(tableFacade, table);
        	// renombramos las cabeceras
            configurarCabecerasColumnasExport(table,usuario,true);
            // Configuracion de los datos de las columnas que requieren un
         	// tratamiento para mostrarse
            //table.getRow().getColumn("fechaCarga").getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));
            table.getRow().getColumn(FECHARENOVACION).getCellRenderer().setCellEditor(new DateCellEditor(PolizasRenovablesService.DD_MM_YYYY));
            table.getRow().getColumn("fechaEnvioIbanAgro").getCellRenderer().setCellEditor(new DateCellEditor(PolizasRenovablesService.DD_MM_YYYY));
            table.getRow().getColumn(columnas.get(GRUPO_NEGOCIO)).getCellRenderer().setCellEditor(getCellEditorGrupoNegocio(mapGruposNegocio));
            
            // Escribe los datos generados en el response
           
			tableFacade.render(); // Will write the export data out to the
									// response.
			return null; // In Spring return null tells the controller not to do
							// anything.
		} else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");
			configurarColumnas(table,usuario);
			// Configuracion de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(CAMPO_ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
			
			
			table.getRow().getColumn(columnas.get(GRUPO_NEGOCIO)).getCellRenderer().setCellEditor(getCellEditorGrupoNegocio(mapGruposNegocio));
			table.getRow().getColumn(columnas.get(COLECTIVO)).getCellRenderer().setCellEditor(getCellEditorColectivo());
			table.getRow().getColumn(columnas.get(REFERENCIA)).getCellRenderer().setCellEditor(getCellEditorReferencia());
			table.getRow().getColumn(columnas.get(ESTADOSAGROPLUS)).getCellRenderer().setCellEditor(getCellEditorEstAgroplus(false));
			table.getRow().getColumn(columnas.get(ESTADOSAGROSEGURO)).getCellRenderer().setCellEditor(getCellEditorEstAgroseguro(false));
			if (!(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==true)){
			//if(perfil.compareTo("0")==0 ||(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==false)){
				table.getRow().getColumn(columnas.get(PORCENTAJE_COMISION)).getCellRenderer().setCellEditor(getCellEditorPorcentajeComision());
				table.getRow().getColumn(columnas.get(COMISION_ENTIDAD)).getCellRenderer().setCellEditor(getCellEditorPorcentajeEntidad());
			}
			table.getRow().getColumn(columnas.get(COMISION_ESMED)).getCellRenderer().setCellEditor(getCellEditorPorcentajeESMed());
			if (!(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==true)){
				table.getRow().getColumn(columnas.get(COMISIONAPL)).getCellRenderer().setCellEditor(getCellEditorPorcentajeComisionApl());		
				table.getRow().getColumn(columnas.get(ENTIDADAPL)).getCellRenderer().setCellEditor(getCellEditorPorcentajeEntApl(true));
				table.getRow().getColumn(columnas.get(ESMEDAPL)).getCellRenderer().setCellEditor(getCellEditorPorcentajeE_SMedApl(true));
			}			
			table.getRow().getColumn(columnas.get(COSTE_TOMADOR)).getCellRenderer().setCellEditor(getCellEditorCosteTotalTomador());
			table.getRow().getColumn(columnas.get(ESTADOSENVIOIBAN)).getCellRenderer().setCellEditor(getCellEditorEstEnvioIBAN());
		}

		return tableFacade.render();
	}
    
    /**
     * Metodo que configura los nombres de las columnas para los informes
     * @param table
     */
    private void configurarCabecerasColumnasExport(Table table, Usuario usuario, boolean isInforme) {
    	String perfil= usuario.getPerfil().substring(4);
    	boolean isUsuP1Ex = false;	
		if(perfil.equals("1") && usuario.isUsuarioExterno()==true)
			isUsuP1Ex = true;
		
    	table.setCaption("Polizas Renovables");
    	
		Row row = table.getRow();
    	row.getColumn("codentidad").setTitle("Entidad");
		row.getColumn(COLECTIVO_ENT_MED).setTitle("Entidad Med");
		row.getColumn(COLECTIVO_SUBENT_MED).setTitle("Subentidad Med");
		row.getColumn("nifTomador").setTitle("Tomador");
		row.getColumn("plan").setTitle("Plan");
		row.getColumn("linea").setTitle("Linea");
		row.getColumn("refcol").setTitle("Colectivo");
		row.getColumn("gruponegocio").setTitle("G.N.");
		row.getColumn("referencia").setTitle("Poliza");
		row.getColumn("nifAsegurado").setTitle("Asegurado");
		row.getColumn(columnas.get(ESTADOSAGROPLUS)).setTitle("Estado Agroplus");
		row.getColumn(columnas.get(ESTADOSAGROPLUS)).getCellRenderer().setCellEditor(getCellEditorEstAgroplus(isInforme));
		row.getColumn(columnas.get(ESTADOSAGROSEGURO)).setTitle("Estado Agroseguro");
		row.getColumn(columnas.get(ESTADOSAGROSEGURO)).getCellRenderer().setCellEditor(getCellEditorEstAgroseguro(isInforme));			
		//row.getColumn("fechaCarga").setTitle("Fecha de carga");
		row.getColumn(FECHARENOVACION).setTitle("Fecha de Renovacion");
		if (!(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==true)){
		//if(perfil.compareTo("0")==0 ||(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==false)){
			row.getColumn("pctComision").setTitle("Comision");
			row.getColumn("pctEntidad").setTitle("% Entidad");
		}
		row.getColumn("pctESMed").setTitle("% E-S Mediadora");
		
		//3 nuevas solo los q no son externo 1
		if (!isUsuP1Ex){
			row.getColumn(columnas.get(COMISIONAPL)).setTitle("Comisión Apl.");
			row.getColumn(columnas.get(ENTIDADAPL)).setTitle("% Ent. Apl.");
			row.getColumn(columnas.get(ESMEDAPL)).setTitle("% E-S Med. Apl.");
		}
		
		row.getColumn("costeTotalTomador").setTitle("Coste Tomador");
		row.getColumn(columnas.get(ESTADOSENVIOIBAN)).setTitle("Envio IBAN");
		row.getColumn(columnas.get(ESTADOSENVIOIBAN)).getCellRenderer().setCellEditor(getCellEditorEstEnvioIBAN());
		row.getColumn(columnas.get(FECHAENVIOIBAN)).setTitle("Fec. IBAN");		
		row.getColumn(columnas.get(PRIMACOMERCIALNETA)).setTitle("Prima Comercial Neta");
		row.getColumn(columnas.get(COSTETOMADORANTERIOR)).setTitle("Coste Tomador Anterior");
		row.getColumn(columnas.get(DOMICILIADO)).setTitle("Domiciliado");
		row.getColumn(columnas.get(DESTDOMIC)).setTitle("Destinatario Domiciliacion");
		row.getColumn(columnas.get(IBAN)).setTitle("Iban");
		
	}

	private CellEditor getCellEditorAcciones() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				
			BigDecimal idGasRen = (BigDecimal) new BasicCellEditor().getValue(item,CAMPO_ID, rowcount);
			BigDecimal idPolRen = (BigDecimal) new BasicCellEditor().getValue(item,"idpol", rowcount);
				
			BigDecimal estadoAgroSeguro = (BigDecimal) new BasicCellEditor().getValue(item, "estagroseguro",	rowcount);
			BigDecimal estadoAgroplus = (BigDecimal) new BasicCellEditor().getValue(item, "estagroplus",	rowcount);
			BigDecimal plan = (BigDecimal) new BasicCellEditor().getValue(item, "plan",	rowcount);
			String referencia = (String) new BasicCellEditor().getValue(item, "referencia",	rowcount);

			HtmlBuilder html = new HtmlBuilder();
			
			// botón checkbox
			html.append("<input type=\"checkbox\" id=\"check_" + idGasRen + "\"  name=\"check_" + idGasRen + "\" onClick =\"listaCheckId(\'" + idGasRen + "')\" class=\"dato\"/>");
			html.append(PolizasRenovablesService.NBSP);	
			
			// botón imprimir: solo para estados de Agroseguro: Primera comunicación, comunicación definitiva y Emitida
			if (estadoAgroSeguro.compareTo(Constants.ES_POL_REN_AGSEGURO_PRIMERA_COMUNICACION) == 0  ||
			    estadoAgroSeguro.compareTo(Constants.ES_POL_REN_AGSEGURO_COMUNICACION_DEFINITIVA) == 0 ||
			    estadoAgroSeguro.compareTo(Constants.ES_POL_REN_AGSEGURO_EMITIDA) == 0) {
	        	html.a().href().quote().append("javascript:imprimir('"+idPolRen+"','"+estadoAgroSeguro+"','"+plan+"','"+referencia+"');").quote().close();
	        	html.append("<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"Imprimir \" title=\"Imprimir \"/>");
	            html.aEnd();
	            html.append(PolizasRenovablesService.NBSP);
			}
			
			// botón acuse: solo para estado Agroplus enviada errónea
			if (estadoAgroplus.compareTo(Constants.ES_POL_REN_AGROPLUS_ENVIADA_ERRONEA) == 0) {
	        	html.a().href().quote().append("javascript:verAcuseRecibo('"+idPolRen+"');").quote().close();
	        	html.append("<img src=\"jsp/img/displaytag/acuserecibo.png\" alt=\"Ver acuse Recibo \" title=\"Ver acuse Recibo \"/>");
	            html.aEnd();
	            html.append(PolizasRenovablesService.NBSP);
			}

			return html.toString();
			}
		};
	}
		
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Referencia'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorReferencia() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String refPoliza = (String) new BasicCellEditor().getValue(item,
						columnas.get(REFERENCIA), rowcount);
				Character dcReferencia = (Character) new BasicCellEditor().getValue(item, "dcpol", rowcount);

				String value = refPoliza + "-" + dcReferencia.toString();
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Colectivo'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorColectivo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String refColectivo = (String) new BasicCellEditor().getValue(item, columnas.get(COLECTIVO), rowcount);
				Character dcColectivo = (Character) new BasicCellEditor().getValue(item, "dccol", rowcount);
				String value = refColectivo+ "-" + dcColectivo.toString();
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna
	 * 'ESTADOSAGROPLUS'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorEstAgroplus(final boolean isInforme) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String miniValue = "";
				String descripcion = (String) new BasicCellEditor().getValue(item, "descagroplus", rowcount);
				
				
				BigDecimal estadoAgroplus = (BigDecimal) new BasicCellEditor().getValue(item, "estagroplus",	rowcount);		
				if (estadoAgroplus != null){
	            	switch (estadoAgroplus.intValue()) {
	            		case 1: // pendiente Asignar gastos
	            			miniValue = "P.A.";
		    				break;
		    			case 2:// gastos asignados
		    				miniValue = "G.A.";
		    				break;
		    			case 3: // enviada perndiente de confirmar
		    				miniValue = "E.P."; 
		    				break;
		    			case 4: // enviada correcta
		    				miniValue = "E.C.";
		    				break;
		    			case 5: // enviada errónea
		    				miniValue = "E.R."; 
		    				break;
		    			default : // por defecto
		    				break;
	            	}
        		}
				String value = descripcion.toString();
				HtmlBuilder html = new HtmlBuilder();
				if (isInforme)
					html.append(value);
				else
					html.append("<span title=\""+value+"\">"+miniValue+"</span>");
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna
	 * 'ESTADOSAGROSEGURO'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorEstAgroseguro(final boolean isInforme) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String miniValue = "";
				String descripcion = (String) new BasicCellEditor().getValue(item, "descagroseguro", rowcount);
				BigDecimal estadoAgroSeguro = (BigDecimal) new BasicCellEditor().getValue(item, "estagroseguro",	rowcount);
				
				if (estadoAgroSeguro != null){
	            	switch (estadoAgroSeguro.intValue()) {
	            		case 1: // borrador Precartera
	            			miniValue = "B.P.";
		    				break;
		    			case 2:// Primera comunicación
		    				miniValue = "P.C.";
		    				break;
		    			case 3: // Comunicación definitiva
		    				miniValue = "C.D."; 
		    				break;
		    			case 4: // Emitida
		    				miniValue = "E.";
		    				break;
		    			case 5: // Rescindida
		    				miniValue = "R."; 
		    				break;
		    			case 6: // Anulada
		    				miniValue = "A."; 
		    				break;
		    			case 7: // Con gastos sin renovación
		    				miniValue = "S.R."; 
		    				break;
		    			case 8: // Precartera precalculada
		    				miniValue = "P.P."; 
		    				break;
		    			case 9: // Precartera generada
		    				miniValue = "P.G."; 
		    				break;
		    			default : // por defecto
		    				break;
	            	}
        		}

				String value = descripcion.toString();
				HtmlBuilder html = new HtmlBuilder();
				if (isInforme)
					html.append(value);
				else
					html.append("<span title=\""+value+"\">"+miniValue+"</span>");
				return html.toString();
			}
		};
	}
	
	
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna
	 * 'ESTADOSENVIOIBAN'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorEstEnvioIBAN() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String descripcion = (String) new BasicCellEditor().getValue(item, "descEstadoIban", rowcount);
				String value = descripcion.toString();
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna
	 * 'GRUPONEGOCIO'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorGrupoNegocio(final Map<Character,String> mapGruposNegocio) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
//				String grupoN = "";
//				String value  = "";
//				Set<GastosRenovacion> setGasRen = (Set<GastosRenovacion>) new BasicCellEditor().getValue(item,"gastosRenovacions", rowcount);
//				for (GastosRenovacion gastosRenovacion : setGasRen) {
//					grupoN = gastosRenovacion.getGrupoNegocio().toString();
//					break;
//				}
//				
//				value = mapGruposNegocio.get(grupoN.charAt(0));
//				HtmlBuilder html = new HtmlBuilder();
//				html.append(value);
//				return html.toString();
				String descripcion = (String) new BasicCellEditor().getValue(item, "descGrupoNegocio", rowcount);
				String value = PolizasRenovablesService.NBSP;
				if (descripcion != null && !descripcion.equals(""))
					 value = descripcion.toString();
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorPorcentajeComision() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal GasRen = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(PORCENTAJE_COMISION), rowcount);
				//Set<GastosRenovacion> setGasRen = (Set<GastosRenovacion>) new BasicCellEditor().getValue(item,"pctComision", rowcount);
				
//				BigDecimal GasRen = null;
//				for (GastosRenovacion gastosRenovacion : setGasRen) {
//					GasRen = gastosRenovacion.getComisionMediador();
//					break;
//				}
				
				String strValue = PolizasRenovablesService.NBSP;
				HtmlBuilder html = new HtmlBuilder();
				if(GasRen!=null){

					html.append(GasRen + "%");
				}else {
					html.append(strValue);
				}
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorPorcentajeEntidad() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal GasRen = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(COMISION_ENTIDAD), rowcount);
				//Set<GastosRenovacion> setGasRen = (Set<GastosRenovacion>) new BasicCellEditor().getValue(item,"pctComision", rowcount);
				
//				BigDecimal GasRen = null;
//				for (GastosRenovacion gastosRenovacion : setGasRen) {
//					GasRen = gastosRenovacion.getComisionMediador();
//					break;
//				}
				
				String strValue = PolizasRenovablesService.NBSP;
				HtmlBuilder html = new HtmlBuilder();
				if(GasRen!=null){

					html.append(GasRen + "%");
				}else {
					html.append(strValue);
				}
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorPorcentajeESMed() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				BigDecimal GasRen = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(COMISION_ESMED), rowcount);
				//Set<GastosRenovacion> setGasRen = (Set<GastosRenovacion>) new BasicCellEditor().getValue(item,"pctComision", rowcount);
				
//				BigDecimal GasRen = null;
//				for (GastosRenovacion gastosRenovacion : setGasRen) {
//					GasRen = gastosRenovacion.getComisionMediador();
//					break;
//				}
				
				String strValue = PolizasRenovablesService.NBSP;
				HtmlBuilder html = new HtmlBuilder();
				if(GasRen!=null){

					html.append(GasRen + "%");
				}else {
					html.append(strValue);
				}
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorPorcentajeComisionApl() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal comTotalApl = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(COMISIONAPL), rowcount);
				
				df.setMinimumFractionDigits(2);
				
				String strValue = PolizasRenovablesService.NBSP;
				
				HtmlBuilder html = new HtmlBuilder();
				if(comTotalApl!=null){

					html.append(df.format(comTotalApl) + "%");
				}else {
					html.append(strValue);
				}
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorPorcentajeEntApl(final boolean pintar) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal entApl = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(ENTIDADAPL), rowcount);
				
				df.setMinimumFractionDigits(2);
				
				String strValue = PolizasRenovablesService.NBSP;
				
				HtmlBuilder html = new HtmlBuilder();
				if(entApl!=null){
					
					if (pintar)
						html.append(df.format(entApl) + "%");
					else
						html.append(df.format(entApl));
					
				}else {
					if (pintar)
						html.append(strValue);
					else
						html.append("");
				}
				return html.toString();
			}
		};
	}
	
	
	private CellEditor getCellEditorPorcentajeE_SMedApl(final boolean pintar) {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal esMedApl = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(ESMEDAPL), rowcount);
				
				df.setMinimumFractionDigits(2);
				
				String strValue = PolizasRenovablesService.NBSP;
				
				HtmlBuilder html = new HtmlBuilder();
				if(esMedApl!=null){
                    if (pintar)
                    	html.append(df.format(esMedApl) + "%");
                    else
                    	html.append(df.format(esMedApl));
				}else {
					if (pintar)
						html.append(strValue);
					else
						html.append("");
				}
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorCosteTotalTomador() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				BigDecimal numero = (BigDecimal) new BasicCellEditor().getValue(item, columnas.get(COSTE_TOMADOR), rowcount);
				String strValue = PolizasRenovablesService.NBSP;
				if(numero!=null){
					
					df.setMinimumFractionDigits(2);
					strValue = df.format(numero);
	
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(strValue);
				return html.toString();
			}
		};
	}

	/**
	 * Configuracion de las columnas de la tabla 08/05/2014 U029769
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table, Usuario usuario) {
		String perfil= usuario.getPerfil().substring(4);
		
		configColumna(table, columnas.get(CAMPO_ID), "Acciones", false, false,       				null, null);
		configColumna(table, columnas.get(COLECTIVO_ENT_MED),"Ent. Med.", true, true,      		 	"35px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
		configColumna(table, columnas.get(COLECTIVO_SUBENT_MED),"Subent. Med.", true, true,      	"40px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
		configColumna(table, columnas.get(PLAN), "Plan", true, true,                       		 	"31px", null);
		configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true,              		 	"37px", null);
		configColumna(table, columnas.get(GRUPO_NEGOCIO), "G.N.", true, true,              		 	"32px", null);
		configColumna(table, columnas.get(COLECTIVO), "Colectivo", true, true,	           			null, "white-space: nowrap;");
		configColumna(table, columnas.get(REFERENCIA), "P&oacute;liza", true,true,            		null, "white-space: nowrap;");
		configColumna(table, columnas.get(ASEGURADO), "NIF/CIF", true, true,               		 	null, null);
		configColumna(table, columnas.get(ESTADOSAGROPLUS), "Estado Agroplus",true, true,  	    	"55px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
		configColumna(table, columnas.get(ESTADOSAGROSEGURO),"Estado Agroseguro", true, true,   	"66px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
		configColumnaFecha(table, columnas.get(FECHARENOVACION),"F.Renovaci&oacute;n", true, true,	"76px", PolizasRenovablesService.DD_MM_YYYY, null);
		if (!(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==true)){
		//if(perfil.compareTo("0")==0 ||(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==false)){
			configColumna(table, columnas.get(PORCENTAJE_COMISION),"Comisi&oacute;n", true, true, 	"55px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
			configColumna(table, columnas.get(COMISION_ENTIDAD),"%Ent.", true, true,            	"43px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
		}
		configColumna(table, columnas.get(COMISION_ESMED),"%E-S Med.", true, true,              	"37px", PolizasRenovablesService.TEXT_ALIGN_CENTER);
		if (!(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==true)){
			configColumna(table, columnas.get(COMISIONAPL),"Comisión Apl", true, true,          	null, PolizasRenovablesService.TEXT_ALIGN_CENTER);
			configColumna(table, columnas.get(ENTIDADAPL),"%Ent. Apl", true, true,                	null, PolizasRenovablesService.TEXT_ALIGN_CENTER);
			configColumna(table, columnas.get(ESMEDAPL),"%E-S Med. Apl", true, true,             	null, PolizasRenovablesService.TEXT_ALIGN_CENTER);
		}		
		configColumna(table, columnas.get(COSTE_TOMADOR),"Coste Tomador", true, true,            	"55px", "text-align: right;");
		configColumna(table, columnas.get(ESTADOSENVIOIBAN), "Env&iacute;o IBAN",true, true,     	null, null);
	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla 
	 * 
	 * @param table
	 * @param idCol
	 * @param title
	 * @param filterable
	 * @param sortable
	 * @param width
	 */
	private void configColumna(HtmlTable table, String idCol, String title,	boolean filterable, boolean sortable, String width, String style) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		// Si se ha indicado ancho para la columna
		if (width != null) table.getRow().getColumn(idCol).setWidth(width);
		// Si se ha indicado estilo para la columna
		if (style != null) table.getRow().getColumn(idCol).setStyle(style);
	}

	/**
	 * Configura una columna de tipo fecha con el formateo indicado en 'fFecha'
	 * @param table
	 * @param idCol
	 * @param title
	 * @param filterable
	 * @param sortable
	 * @param width
	 * @param fFecha
	 */
	private void configColumnaFecha(HtmlTable table, String idCol,
			String title, boolean filterable, boolean sortable, String width,
			String fFecha, String style) {
		
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width, style);
		// AÃ±ade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error(
					"Ocurrio un error al configurar el formato de fecha de la columna "
							+ idCol, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EstadoRenovacionAgroplus> getEstadosRenAgroplus(BigDecimal estadoRenAgroplusExcluir[]) {
		EstadoRenAgroplusFilter estadoRenAgroplusFilter = new EstadoRenAgroplusFilter();
		estadoRenAgroplusFilter.setEstadosRenAgroplusExcluir(estadoRenAgroplusExcluir);
		return this.polizasRenovablesDao.getObjects(estadoRenAgroplusFilter);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EstadoRenovacionAgroseguro> getEstadosRenAgroseguro(BigDecimal estadoRenAgroseguroExcluir[]) {
		EstadoRenAgroseguroFilter estadoRenAgroseguroFilter = new EstadoRenAgroseguroFilter();
		estadoRenAgroseguroFilter.setEstadosRenAgroseguroExcluir(estadoRenAgroseguroExcluir);
		return this.polizasRenovablesDao.getObjects(estadoRenAgroseguroFilter);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PolizaRenovableEstadoEnvioIBAN> getEstadosRenEnvioIBAN(BigDecimal estadoRenEnvioIBANExcluir[]) {
				EstadoRenEnvioIBANFilter estadoRenEnvioIBANFilter = new EstadoRenEnvioIBANFilter();
				estadoRenEnvioIBANFilter.setEstadoRenAEnvioIBANExcluir(estadoRenEnvioIBANExcluir);
		return this.polizasRenovablesDao.getObjects(estadoRenEnvioIBANFilter);
	}
	
	@Override
	public List<GruposNegocio> getGruposNegocio() {
		return this.polizasRenovablesDao.getGruposNegocio(true);
	}

	@Override
	public String getlistaIdsTodos(PolizasRenovablesFilter consultaFilter,String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
		   String fecEnvioIBANIni,String fecEnvioIBANFin,String grupoNegocio, String estAgroplus) {
		//para cambio masivo. Cambiamos la consulta para que sean los que tengan FechaBaja == Null
		//consultaFilter.addFilter("fechaBaja", null);
		String listaIdsTodos =polizasRenovablesDao.getlistaIdsTodos(consultaFilter,fecCargaIni,fecCargaFin,fecRenoIni,fecRenoFin,fecEnvioIBANIni,fecEnvioIBANFin,grupoNegocio,estAgroplus);
		return listaIdsTodos;
		
	}
	
	public String getNombreEntidad(Long codigo) {
		String nombre="";
		Entidad ent=(Entidad) polizasRenovablesDao.getObject(Entidad.class, new BigDecimal(codigo));
		if(null!= ent && ent.getNomentidad()!=null)nombre=ent.getNomentidad();		
		return nombre;
	}
	
	public String getAcuseReciboGastos(Long idPolRen) {
		String acuseRecibo = polizasRenovablesDao.getAcuseReciboGastos(idPolRen);
	 return acuseRecibo;
	}
	
	public String getNombreTomador(Long codigoEntidad, String cifTomador) {
		String nombre="";	
		Tomador tomFiltro = new Tomador();
		tomFiltro.getId().setCodentidad(new BigDecimal(codigoEntidad));
		tomFiltro.getId().setCiftomador(cifTomador);
		tomFiltro.getEntidad().setCodentidad(new BigDecimal(codigoEntidad));
		TomadorFiltro filtro= new TomadorFiltro(tomFiltro);
		@SuppressWarnings("unchecked")
		List<Tomador> listTomador =  polizasRenovablesDao.getObjects(filtro);
		if(null!= listTomador && listTomador.size()>0 && 
				listTomador.get(0).getRazonsocial()!=null) {
			nombre=listTomador.get(0).getRazonsocial();
		}		
		return nombre;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean validarPolizasGastosMasivo(String ids) {
		
		// Se trata la cadena 'ids' para convertirla en un listado de ids
		List<String> lista = new ArrayList<String>();
		try {
			// Obtiene el listado de ids de pÃ³liza a partir de la cadena separa por ';'
			lista = limpiarVacios (Arrays.asList (ids.split(CHAR_SEPARADOR_IDS)));	
		}
		catch (Exception e) {
			logger.error("Ocurrio³ un error al obtener el listado de ids de po³liza", e);
		}
		
		// Si la lista se ha rellenado correctamente seguimos con la validaciÃ³n
		if (lista.size() > 0) {
			try {
				//Comprueba si alguna póliza seleccionada se encuentra en estados diferentes a
				//comunicacion definitiva o emitida
				return ((polizasRenovablesDao.getCountPlzGastosMasivo (lista) > 0) ? false : true);
			} catch (DAOException e) {
				logger.error("Ocurrio algun error al obtener el numero de polizas que no pueden ser borradas", e);
			}
		}
		
		// Si llega hasta aquÃ­ ha ocurrido algÃºn error, no se permite el borrado
		return false;
	}
	
	/**
	 * Elimina los elementos vacÃ­os que contenga la lista
	 * @param listaIni
	 */
	public List<String> limpiarVacios (List<String> listaIni) {
		
		List<String> listaFin = new ArrayList<String>();
		
		// Recorre los elementos de la lista y copia en la nueva lista los que no sean vacios
		for (String string : listaIni) {
			if (!"".equals(string)) listaFin.add(string);
		}	
	
		return listaFin;
	}
	
	 public List<String> getListaIdsRenovables (List<String> lstCadenasIds){
		 
		 List<String> lstPolRen = polizasRenovablesDao.getListaIdsRenovables(lstCadenasIds);
		 return lstPolRen;
	 }
	
	public void setPolizasRenovablesDao(
			IPolizasRenovablesDao polizasRenovablesDao) {
		this.polizasRenovablesDao = polizasRenovablesDao;
	}


	public void setPolizasPctComisionesDao(
			IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}


	public void setPolizasPctComisionesManager(
			PolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}


	@Override
	public List<VistaPolizaRenovable> getAllFilteredAndSorted(HttpServletRequest request) throws BusinessException {
		
		 // Obtener todos los registros filtrados y ordenados sin límites de paginación
	    Collection<VistaPolizaRenovable> allResults = polizasRenovablesDao.getPolizasRenovablesWithFilterAndSort(consultaFilter, consultaSort, -1, -1);
	    return (List<VistaPolizaRenovable>) allResults;
	}
	
	
}