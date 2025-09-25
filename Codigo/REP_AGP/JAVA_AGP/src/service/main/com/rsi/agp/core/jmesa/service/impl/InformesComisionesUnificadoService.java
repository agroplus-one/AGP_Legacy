package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.IInformesComisionesUnificadoService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeComisionesUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformesComisionesUnificadoService extends GetTablaService implements IInformesComisionesUnificadoService {
	
	private final static String ID = "id";
	private final static String CODENTIDAD = "codentidad";
	public final static String LISTA_ENTIDADES_USUARIO = "grupoEntidades";
	private final static String OFICINA = "oficina";
	private final static String ENTMEDIADORA = "entmediadora";
	private final static String SUBENTMEDIADORA = "subentmediadora";
	private final static String PLAN = "plan";
	private final static String LINEA = "linea";
	private final static String REFERENCIA = "referencia";
	private final static String IDCOLECTIVO = "idcolectivo";
	private final static String CIFTOMADOR = "ciftomador";
	private final static String NIFCIF = "nifcif";
	private final static String NOMBRE_ASEGURADO = "nombreAsegurado";
	private final static String RECIBO = "recibo";
	private final static String FASE = "fase";
	public final static String FECHA_CARGA = "fechaCarga";
	public final static String FECHA_EMISION_RECIBO = "fechaEmisionRecibo";
	public final static String FECHA_ACEPTACION = "fechaAceptacion";
	public final static String FECHA_CIERRE = "fechaCierre";
	public final static String FECHA_VIGOR = "fechaVigor";
	private final static String GRUPO_NEGOCIO = "grupoNegocio";
	private final static String PRIMA_COMERCIAL_NETA = "primaComercialNeta";
	private final static String GD_ADMIN = "gdAdmin";
	private final static String GD_ADQ = "gdAdq";
	private final static String GD_COMISION_MEDIADOR = "gdComisionMediador";
	private final static String GD_COMMED_ENTIDAD = "gdCommedEntidad";
	private final static String GD_COMMED_ESMED = "gdCommedEsmed";
	private final static String GA_ADMIN = "gaAdmin";
	private final static String GA_ADQ = "gaAdq";
	private final static String GA_COMISION_MEDIADOR = "gaComisionMediador";
	private final static String GA_COMMED_ENTIDAD = "gaCommedEntidad";
	private final static String GA_COMMED_ESMED = "gaCommedEsmed";
	private final static String GP_ADMIN = "gpAdmin";
	private final static String GP_ADQ = "gpAdq";
	private final static String GP_COMISION_MEDIADOR = "gpComisionMediador";
	private final static String GP_COMMED_ENTIDAD = "gpCommedEntidad";
	private final static String GP_COMMED_ESMED = "gpCommedEsmed";
	
	// Constantes para los filtros igual y entre
	public final static String FECHA_CARGA_ENTRE = "entreFechaCarga";
	public final static String FECHA_EMISION_RECIBO_ENTRE = "entreFechaEmisionRecibo";
	public final static String FECHA_ACEPTACION_ENTRE = "entreFechaAceptacion";
	public final static String FECHA_CIERRE_ENTRE = "entreFechaCierre";
	public final static String FECHA_VIGOR_ENTRE = "entreFechaVigor";
	
	private final static String GD_COMMED_ENTIDAD_TIT = "Dev Ent";
	private final static String GA_COMMED_ENTIDAD_TIT = "Abon Ent";
	private final static String GP_COMMED_ENTIDAD_TIT = "Pte Ent";
	private final static String GD_COMMED_ESMED_TIT = "Dev Med";
	private final static String GA_COMMED_ESMED_TIT = "Abon Med";
	private final static String GP_COMMED_ESMED_TIT = "Pte Med";
	
	private final static String WHITESPACE_HTML = "&nbsp;";

	private Log logger = LogFactory.getLog(getClass());

	private final static String STYLE = "text-align: right";
	
	IGenericoFilter informesComisionesUnificadoFilter;
	IGenericoSort	informesComisionesUnificadoSort;	
	
	@SuppressWarnings("deprecation")
	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable informeComisionesUnificado, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao,
			Usuario usuario) {
		InformeComisionesUnificado informe=(InformeComisionesUnificado)informeComisionesUnificado;
		
		cargarColumnas();
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
		//tableFacade.setExportTypes(response);
		tableFacade.autoFilterAndSort(false);
//		tableFacade.setExportTypes(response, ExportType.EXCEL);		
		
		setColumnasVisibles(tableFacade, usuario);
		
		if (origenLlamada != null) {
			informesComisionesUnificadoFilter.clear();
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}			
			try {
				cargarFiltrosBusqueda(informe, tableFacade);
			} catch (ParseException e) {
				logger.error(e);
			}
		}
		
		if(listaGrupoEntidades!=null && listaGrupoEntidades.size()>0) {
			this.addListaEntidadesFilter(listaGrupoEntidades, informesComisionesUnificadoFilter, LISTA_ENTIDADES_USUARIO);			
		}		
		
		informesComisionesUnificadoSort.clear();
		
		this.setDataAndLimitVariables(tableFacade, informesComisionesUnificadoFilter, genericoDao, 
				informesComisionesUnificadoSort, informesComisionesUnificadoSort);
		
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}
	
		return html(tableFacade, usuario);//+ script;
	}
	
	public void setDataAndLimitVariables(TableFacade tableFacade, 
			CriteriaCommand filtro, IGenericoDao genericoDao,
			IGenericoSort genericoSort, CriteriaCommand sort) {

		Collection<java.io.Serializable> items = new ArrayList<java.io.Serializable>();
		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();		

		try {
			int totalRows = genericoDao.getCountWithFilter(filtro);
			logger.debug("********** count filas  = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);
			informesComisionesUnificadoSort.getConsultaSort(limit);			
			
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items=genericoDao.getWithFilterAndSort(filtro, sort, rowStart, rowEnd);
			tableFacade.setItems(items);
			logger.debug("********** lista de items   = "+ items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
	}
	
	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);
			columnas.put(CODENTIDAD, CODENTIDAD);
			columnas.put(OFICINA, OFICINA);
			columnas.put(ENTMEDIADORA, ENTMEDIADORA);
			columnas.put(SUBENTMEDIADORA, SUBENTMEDIADORA);
			columnas.put(PLAN, PLAN);
			columnas.put(LINEA, LINEA);
			columnas.put(REFERENCIA, REFERENCIA);
			columnas.put(IDCOLECTIVO, IDCOLECTIVO);
			columnas.put(CIFTOMADOR, CIFTOMADOR);
			columnas.put(NIFCIF, NIFCIF);
			columnas.put(NOMBRE_ASEGURADO, NOMBRE_ASEGURADO);
			columnas.put(RECIBO, RECIBO);
			columnas.put(FASE, FASE);
			columnas.put(FECHA_CARGA, FECHA_CARGA);
			columnas.put(FECHA_EMISION_RECIBO, FECHA_EMISION_RECIBO);
			columnas.put(FECHA_ACEPTACION, FECHA_ACEPTACION);
			columnas.put(FECHA_CIERRE, FECHA_CIERRE);
			columnas.put(FECHA_VIGOR, FECHA_VIGOR);
			columnas.put(GRUPO_NEGOCIO, GRUPO_NEGOCIO);
			columnas.put(PRIMA_COMERCIAL_NETA, PRIMA_COMERCIAL_NETA);
			columnas.put(GD_ADMIN, GD_ADMIN);
			columnas.put(GD_ADQ, GD_ADQ);
			columnas.put(GD_COMISION_MEDIADOR, GD_COMISION_MEDIADOR);
			columnas.put(GD_COMMED_ENTIDAD, GD_COMMED_ENTIDAD);
			columnas.put(GD_COMMED_ESMED, GD_COMMED_ESMED);
			columnas.put(GA_ADMIN, GA_ADMIN);
			columnas.put(GA_ADQ, GA_ADQ);
			columnas.put(GA_COMISION_MEDIADOR, GA_COMISION_MEDIADOR);
			columnas.put(GA_COMMED_ENTIDAD, GA_COMMED_ENTIDAD);
			columnas.put(GA_COMMED_ESMED, GA_COMMED_ESMED);
			columnas.put(GP_ADMIN, GP_ADMIN);
			columnas.put(GP_ADQ, GP_ADQ);
			columnas.put(GP_COMISION_MEDIADOR, GP_COMISION_MEDIADOR);
			columnas.put(GP_COMMED_ENTIDAD, GP_COMMED_ENTIDAD);
			columnas.put(GP_COMMED_ESMED, GP_COMMED_ESMED);
			columnas.put(LISTA_ENTIDADES_USUARIO, LISTA_ENTIDADES_USUARIO);
			columnas.put(FECHA_CARGA_ENTRE, FECHA_CARGA_ENTRE);
			columnas.put(FECHA_EMISION_RECIBO_ENTRE, FECHA_EMISION_RECIBO_ENTRE);
			columnas.put(FECHA_ACEPTACION_ENTRE, FECHA_ACEPTACION_ENTRE);
			columnas.put(FECHA_CIERRE_ENTRE, FECHA_CIERRE_ENTRE);
			columnas.put(FECHA_VIGOR_ENTRE, FECHA_VIGOR_ENTRE);
		}
	}	
	
	@SuppressWarnings("deprecation")
	private void setColumnasVisibles(TableFacade tableFacade, Usuario usuario) {
		Limit limit = tableFacade.getLimit();
		if (!limit.isExported()){
			setColumnasVisiblesGrid(tableFacade,  usuario);
		}else{
			setColumnasVisiblesExport(tableFacade,  usuario);
		}			
	}

	@SuppressWarnings("deprecation")
	private void setColumnasVisiblesGrid(TableFacade tableFacade, Usuario usuario){
		String perfil= usuario.getPerfil().substring(4);
		if(Constants.PERFIL_0.toString().equals(perfil)) {
			tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA), columnas.get(OFICINA), columnas.get(PLAN),
					columnas.get(LINEA), columnas.get(REFERENCIA), columnas.get(FECHA_VIGOR), columnas.get(CIFTOMADOR),
					columnas.get(IDCOLECTIVO), columnas.get(NIFCIF), columnas.get(NOMBRE_ASEGURADO), columnas.get(FASE),
					columnas.get(GRUPO_NEGOCIO), columnas.get(PRIMA_COMERCIAL_NETA), columnas.get(GD_ADMIN),
					columnas.get(GD_ADQ), columnas.get(GD_COMMED_ENTIDAD), columnas.get(GD_COMMED_ESMED),
					columnas.get(GA_ADMIN), columnas.get(GA_ADQ), columnas.get(GA_COMMED_ENTIDAD),
					columnas.get(GA_COMMED_ESMED), columnas.get(GP_ADMIN), columnas.get(GP_ADQ),
					columnas.get(GP_COMMED_ENTIDAD), columnas.get(GP_COMMED_ESMED));
		}		
		if(Constants.PERFIL_5.toString().equals(perfil) || (Constants.PERFIL_1.toString().equals(perfil)) && !usuario.isUsuarioExterno()){
			tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA), columnas.get(OFICINA), columnas.get(PLAN),
					columnas.get(LINEA), columnas.get(REFERENCIA), columnas.get(FECHA_VIGOR), columnas.get(CIFTOMADOR),
					columnas.get(IDCOLECTIVO), columnas.get(NIFCIF), columnas.get(NOMBRE_ASEGURADO), columnas.get(FASE),
					columnas.get(GRUPO_NEGOCIO), columnas.get(PRIMA_COMERCIAL_NETA), columnas.get(GD_COMMED_ENTIDAD),
					columnas.get(GD_COMMED_ESMED), columnas.get(GA_COMMED_ENTIDAD), columnas.get(GA_COMMED_ESMED),
					columnas.get(GP_COMMED_ENTIDAD), columnas.get(GP_COMMED_ESMED));
		}		
		if(Constants.PERFIL_1.toString().equals(perfil) && usuario.isUsuarioExterno()){
			tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA), columnas.get(OFICINA), columnas.get(PLAN),
					columnas.get(LINEA), columnas.get(REFERENCIA), columnas.get(FECHA_VIGOR), columnas.get(CIFTOMADOR),
					columnas.get(IDCOLECTIVO), columnas.get(NIFCIF), columnas.get(NOMBRE_ASEGURADO), columnas.get(FASE),
					columnas.get(GRUPO_NEGOCIO), columnas.get(PRIMA_COMERCIAL_NETA), columnas.get(GD_COMMED_ESMED),
					columnas.get(GA_COMMED_ESMED), columnas.get(GP_COMMED_ESMED));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void setColumnasVisiblesExport(TableFacade tableFacade, Usuario usuario){
		String perfil= usuario.getPerfil().substring(4);		
		if(Constants.PERFIL_0.toString().equals(perfil)) {
			tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA), columnas.get(SUBENTMEDIADORA),
					columnas.get(OFICINA), columnas.get(PLAN), columnas.get(LINEA), columnas.get(REFERENCIA),
					columnas.get(FECHA_VIGOR), columnas.get(CIFTOMADOR), columnas.get(IDCOLECTIVO),
					columnas.get(NIFCIF), columnas.get(NOMBRE_ASEGURADO), columnas.get(FASE),
					columnas.get(FECHA_EMISION_RECIBO), columnas.get(GRUPO_NEGOCIO), columnas.get(PRIMA_COMERCIAL_NETA),
					columnas.get(GD_ADMIN), columnas.get(GD_ADQ), columnas.get(GD_COMMED_ENTIDAD),
					columnas.get(GD_COMMED_ESMED), columnas.get(GA_ADMIN), columnas.get(GA_ADQ),
					columnas.get(GA_COMMED_ENTIDAD), columnas.get(GA_COMMED_ESMED), columnas.get(GP_ADMIN),
					columnas.get(GP_ADQ), columnas.get(GP_COMMED_ENTIDAD), columnas.get(GP_COMMED_ESMED));
		}		
		if(Constants.PERFIL_5.toString().equals(perfil) || (Constants.PERFIL_1.toString().equals(perfil)) && !usuario.isUsuarioExterno()){
			tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA), columnas.get(SUBENTMEDIADORA),
					columnas.get(OFICINA), columnas.get(PLAN), columnas.get(LINEA), columnas.get(REFERENCIA),
					columnas.get(FECHA_VIGOR), columnas.get(CIFTOMADOR), columnas.get(IDCOLECTIVO),
					columnas.get(NIFCIF), columnas.get(NOMBRE_ASEGURADO), columnas.get(FASE),
					columnas.get(FECHA_EMISION_RECIBO), columnas.get(GRUPO_NEGOCIO), columnas.get(PRIMA_COMERCIAL_NETA),
					columnas.get(GD_COMMED_ENTIDAD), columnas.get(GD_COMMED_ESMED), columnas.get(GA_COMMED_ENTIDAD),
					columnas.get(GA_COMMED_ESMED), columnas.get(GP_COMMED_ENTIDAD), columnas.get(GP_COMMED_ESMED));
		}		
		if(Constants.PERFIL_1.toString().equals(perfil) && usuario.isUsuarioExterno()){
			tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA), columnas.get(SUBENTMEDIADORA),
					columnas.get(OFICINA), columnas.get(PLAN), columnas.get(LINEA), columnas.get(REFERENCIA),
					columnas.get(FECHA_VIGOR), columnas.get(CIFTOMADOR), columnas.get(IDCOLECTIVO),
					columnas.get(NIFCIF), columnas.get(NOMBRE_ASEGURADO), columnas.get(FASE),
					columnas.get(FECHA_EMISION_RECIBO), columnas.get(GRUPO_NEGOCIO), columnas.get(PRIMA_COMERCIAL_NETA),
					columnas.get(GD_COMMED_ESMED), columnas.get(GA_COMMED_ESMED), columnas.get(GP_COMMED_ESMED));
		}		
	}
	
	 private void configurarCabecerasColumnasExport(Table table,Usuario usuario){
			String perfil= usuario.getPerfil().substring(4);
			
			table.setCaption("Comisiones ");
	    	
			Row row = table.getRow();
			
			row.getColumn(OFICINA).setTitle("Oficina");
			row.getColumn(ENTMEDIADORA).setTitle("E Med");
			row.getColumn(SUBENTMEDIADORA).setTitle("S Med");
			row.getColumn(PLAN).setTitle("Plan");
			row.getColumn(LINEA).setTitle("Línea");
			row.getColumn(REFERENCIA).setTitle("Póliza");
			row.getColumn(FECHA_VIGOR).setTitle("F. Vigor");
			/* GDLD-78692 ** MODIF TAM (28.12.2021) * Resolución Defecto Nº1 */
			row.getColumn(FECHA_VIGOR).getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));
			row.getColumn(CIFTOMADOR).setTitle("CIF Tomador");			
			row.getColumn(IDCOLECTIVO).setTitle("Colectivo");
			row.getColumn(NIFCIF).setTitle("NIF Aseg.");
			row.getColumn(NOMBRE_ASEGURADO).setTitle("Asegurado");
			row.getColumn(FASE).setTitle("Fase");
			row.getColumn(FECHA_EMISION_RECIBO).setTitle("F. Emisión");
			/* GDLD-78692 ** MODIF TAM (28.12.2021) * Resolución Defecto Nº1 */
			row.getColumn(FECHA_EMISION_RECIBO).getCellRenderer().setCellEditor(new DateCellEditor("dd/MM/yyyy"));
			row.getColumn(GRUPO_NEGOCIO).setTitle("G.N.");			
			row.getColumn(PRIMA_COMERCIAL_NETA).setTitle("PCN");
			
			if(perfil.compareTo("0")==0) {
				row.getColumn(GD_ADMIN).setTitle("Dev Adm");	//3.33	
				row.getColumn(GD_ADQ).setTitle("Dev Adq");
				row.getColumn(GD_COMMED_ENTIDAD).setTitle(GD_COMMED_ENTIDAD_TIT);
				row.getColumn(GD_COMMED_ESMED).setTitle(GD_COMMED_ESMED_TIT);			
				row.getColumn(GA_ADMIN).setTitle("Abon Adm");
				row.getColumn(GA_ADQ).setTitle("Abon Adq");
				row.getColumn(GA_COMMED_ENTIDAD).setTitle(GA_COMMED_ENTIDAD_TIT);				
				row.getColumn(GA_COMMED_ESMED).setTitle(GA_COMMED_ESMED_TIT);
				row.getColumn(GP_ADMIN).setTitle("Pte Adm");
				row.getColumn(GP_ADQ).setTitle("Pte Adq");				
				row.getColumn(GP_COMMED_ENTIDAD).setTitle(GP_COMMED_ENTIDAD_TIT);
				row.getColumn(GP_COMMED_ESMED).setTitle(GP_COMMED_ESMED_TIT);	
				
			}
			if(perfil.compareTo("5")==0 ||(perfil.compareTo("1")==0 && usuario.isUsuarioExterno()==false)){
				row.getColumn(GD_COMMED_ENTIDAD).setTitle(GD_COMMED_ENTIDAD_TIT);
				row.getColumn(GD_COMMED_ESMED ).setTitle(GD_COMMED_ESMED_TIT);			
				row.getColumn(GA_COMMED_ENTIDAD).setTitle(GA_COMMED_ENTIDAD_TIT);				
				row.getColumn(GA_COMMED_ESMED).setTitle(GA_COMMED_ESMED_TIT);
				row.getColumn(GP_COMMED_ENTIDAD).setTitle(GP_COMMED_ENTIDAD_TIT);
				row.getColumn(GP_COMMED_ESMED).setTitle(GP_COMMED_ESMED_TIT);			
			}
			
			if(perfil.compareTo("1")==0  && usuario.isUsuarioExterno()==true){
				row.getColumn(GD_COMMED_ESMED ).setTitle(GD_COMMED_ESMED_TIT);			
				row.getColumn(GA_COMMED_ESMED).setTitle(GA_COMMED_ESMED_TIT);
				row.getColumn(GP_COMMED_ESMED).setTitle(GP_COMMED_ESMED_TIT);
			}
			
		}

	
	
	private void cargarFiltrosBusqueda(InformeComisionesUnificado informe, TableFacade tableFacade) throws ParseException {
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		if(null!=informe.getCodentidad() )
			this.addColumnaFiltro(tableFacade, CODENTIDAD, informe.getCodentidad(), informesComisionesUnificadoFilter);
		if(null!=informe.getOficina() && !informe.getOficina().isEmpty())
			this.addColumnaFiltro(tableFacade, OFICINA,informe.getOficina(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getEntmediadora())
			this.addColumnaFiltro(tableFacade, ENTMEDIADORA,informe.getEntmediadora(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getSubentmediadora())
			this.addColumnaFiltro(tableFacade, SUBENTMEDIADORA,informe.getSubentmediadora(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getPlan())
			this.addColumnaFiltro(tableFacade, PLAN,informe.getPlan(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getLinea())
			this.addColumnaFiltro(tableFacade, LINEA,informe.getLinea(),informesComisionesUnificadoFilter);		
	
		if(null!=informe.getReferencia() && !informe.getReferencia().isEmpty() )
			this.addColumnaFiltro(tableFacade, REFERENCIA,informe.getReferencia(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getCiftomador() && !informe.getCiftomador().isEmpty())
			this.addColumnaFiltro(tableFacade, CIFTOMADOR,informe.getCiftomador(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getIdcolectivo() && !informe.getIdcolectivo().isEmpty())
			this.addColumnaFiltro(tableFacade, IDCOLECTIVO,informe.getIdcolectivo(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getNifcif() && !informe.getNifcif().isEmpty())
			this.addColumnaFiltro(tableFacade, NIFCIF ,informe.getNifcif(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getFase())
			this.addColumnaFiltro(tableFacade, FASE,informe.getFase(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getFechaCarga())
			this.addColumnaFiltro(tableFacade, FECHA_CARGA,informe.getFechaCarga(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGrupoNegocio())
			this.addColumnaFiltro(tableFacade, GRUPO_NEGOCIO ,informe.getGrupoNegocio(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getPrimaComercialNeta())
			this.addColumnaFiltro(tableFacade, PRIMA_COMERCIAL_NETA ,informe.getPrimaComercialNeta(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGdAdmin())
			this.addColumnaFiltro(tableFacade, GD_ADMIN ,informe.getGdAdmin(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGdAdq())
			this.addColumnaFiltro(tableFacade, GD_ADQ ,informe.getGdAdq(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGdCommedEntidad())
			this.addColumnaFiltro(tableFacade, GD_COMMED_ENTIDAD,informe.getGdCommedEntidad(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGdComisionMediador())
			this.addColumnaFiltro(tableFacade, GD_COMISION_MEDIADOR ,informe.getGdComisionMediador(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGaAdmin())
			this.addColumnaFiltro(tableFacade, GA_ADMIN ,informe.getGaAdmin(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGaAdq())
			this.addColumnaFiltro(tableFacade, GA_ADQ,informe.getGaAdq(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGaCommedEntidad())
			this.addColumnaFiltro(tableFacade, GA_COMMED_ENTIDAD,informe.getGaCommedEntidad(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGaComisionMediador())
			this.addColumnaFiltro(tableFacade, GA_COMISION_MEDIADOR,informe.getGaComisionMediador(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGpAdmin())
			this.addColumnaFiltro(tableFacade, GP_ADMIN,informe.getGpAdmin(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGpAdq())
			this.addColumnaFiltro(tableFacade, GP_ADQ ,informe.getGpAdq(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGpCommedEntidad())
			this.addColumnaFiltro(tableFacade, GP_COMMED_ENTIDAD,informe.getGpCommedEntidad(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGpComisionMediador())
			this.addColumnaFiltro(tableFacade, GP_COMISION_MEDIADOR,informe.getGpComisionMediador(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getRecibo())
			this.addColumnaFiltro(tableFacade, RECIBO,informe.getRecibo(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getFechaEmisionRecibo())
			this.addColumnaFiltro(tableFacade, FECHA_EMISION_RECIBO,informe.getFechaEmisionRecibo(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getFechaAceptacion())
			this.addColumnaFiltro(tableFacade, FECHA_ACEPTACION,informe.getFechaAceptacion(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getFechaCierre())
			this.addColumnaFiltro(tableFacade, FECHA_CIERRE ,informe.getFechaCierre(),informesComisionesUnificadoFilter);//
		
		if(null!=informe.getFechaVigor())
			this.addColumnaFiltro(tableFacade, FECHA_VIGOR , informe.getFechaVigor(), informesComisionesUnificadoFilter);//
		
		if(null!=informe.getGdCommedEsmed())
			this.addColumnaFiltro(tableFacade, GD_COMMED_ESMED,informe.getGdCommedEsmed(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGaCommedEsmed())
			this.addColumnaFiltro(tableFacade, GA_COMMED_ESMED ,informe.getGaCommedEsmed(),informesComisionesUnificadoFilter);
		
		if(null!=informe.getGpCommedEsmed())
			this.addColumnaFiltro(tableFacade, GP_COMMED_ESMED ,informe.getGpCommedEsmed(),informesComisionesUnificadoFilter);
		
		//********************************************
		if(null!=informe.getOpcionfechaCarga()&& !informe.getOpcionfechaCarga().isEmpty()) {
			if(informe.getOpcionfechaCarga().compareTo("eq")==0) {				
				this.addColumnaFiltro(tableFacade, FECHA_CARGA,df.parse(informe.getEntreFechaCarga()),informesComisionesUnificadoFilter);
			}else {
				this.addColumnaFiltro(tableFacade, FECHA_CARGA_ENTRE,informe.getEntreFechaCarga(),informesComisionesUnificadoFilter);
			}				
		}
		
		if(null!=informe.getOpcionfechaAceptacion()&& !informe.getOpcionfechaAceptacion().isEmpty()) {			
			if(informe.getOpcionfechaAceptacion().compareTo("eq")==0) {
				this.addColumnaFiltro(tableFacade, FECHA_ACEPTACION,df.parse(informe.getEntreFechaAceptacion()),informesComisionesUnificadoFilter);
			}else {
				this.addColumnaFiltro(tableFacade, FECHA_ACEPTACION_ENTRE,informe.getEntreFechaAceptacion(),informesComisionesUnificadoFilter);
			}				
		}
		
		if(null!=informe.getOpcionfechaCierre()&& !informe.getOpcionfechaCierre().isEmpty()) {			
			if(informe.getOpcionfechaCierre().compareTo("eq")==0) {
				this.addColumnaFiltro(tableFacade, FECHA_CIERRE,df.parse(informe.getEntreFechaCierre()),informesComisionesUnificadoFilter);
			}else {
				this.addColumnaFiltro(tableFacade, FECHA_CIERRE_ENTRE,informe.getEntreFechaCierre(),informesComisionesUnificadoFilter);
			}				
		}
		
		if(null!=informe.getOpcionfechaVigor()&& !informe.getOpcionfechaVigor().isEmpty()) {			
			if(informe.getOpcionfechaVigor().compareTo("eq")==0) {
				this.addColumnaFiltro(tableFacade, FECHA_VIGOR, df.parse(informe.getEntreFechaVigor()), informesComisionesUnificadoFilter);
			}else {
				this.addColumnaFiltro(tableFacade, FECHA_VIGOR_ENTRE, informe.getEntreFechaVigor(),informesComisionesUnificadoFilter);
			}				
		}
		
		if(null!=informe.getOpcionfechaEmisionRecibo()&& !informe.getOpcionfechaEmisionRecibo().isEmpty()) {
			if(informe.getOpcionfechaEmisionRecibo().compareTo("eq")==0) {
				this.addColumnaFiltro(tableFacade, FECHA_EMISION_RECIBO,df.parse(informe.getEntreFechaEmisionRecibo()),informesComisionesUnificadoFilter);
			}else {
				this.addColumnaFiltro(tableFacade, FECHA_EMISION_RECIBO_ENTRE,informe.getEntreFechaEmisionRecibo(),informesComisionesUnificadoFilter);
			}				
		}		
	}

	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade, Usuario usuario) {
		Limit limit = tableFacade.getLimit();
		
		if (limit.isExported()) {
			if (limit.getExportType() == ExportType.EXCEL) {
				Table table = tableFacade.getTable();
	        	// Quita la columna Id del informe
	        	//eliminarColumnaId(tableFacade, table);
	        	// renombramos las cabeceras
	            configurarCabecerasColumnasExport(table, usuario);
	            // Escribe los datos generados en el response
	            tableFacade.render(); 
	            // Devuelve nulo para que el controller no haga nada
	            return null; 			
			}
		}else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty(ID);

			configurarColumnas(table, usuario);
			table.getRow().getColumn(columnas.get(ENTMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorEsMediadora());
			table.getRow().getColumn(columnas.get(NOMBRE_ASEGURADO)).getCellRenderer().setCellEditor(getCellEditorNombreAsegurado());
			table.getRow().getColumn(columnas.get(FECHA_VIGOR)).getCellRenderer().setCellEditor(getCellEditorFechaVigor());
		}
		return tableFacade.render();
	}

	
	
	private CellEditor getCellEditorEsMediadora() {
		
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
							
				Integer entidadMed = (Integer) new BasicCellEditor().getValue(item,ENTMEDIADORA, rowcount);
				Integer subEntidadMed = (Integer) new BasicCellEditor().getValue(item, SUBENTMEDIADORA, rowcount);
				
            	HtmlBuilder html = new HtmlBuilder();
            	if (entidadMed!= null && subEntidadMed != null) {
            		html.append(StringUtils.nullToString(entidadMed)+"-"+StringUtils.nullToString(subEntidadMed));
            	}else {
            		html.append(WHITESPACE_HTML);
            	}
            	return html.toString();
            }
		};
	}
	
	private CellEditor getCellEditorNombreAsegurado() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				String nombreAsegurado = (String) new BasicCellEditor()
						.getValue(item, NOMBRE_ASEGURADO, rowcount);
				HtmlBuilder html = new HtmlBuilder();
				if (nombreAsegurado == null || nombreAsegurado.trim().isEmpty()) {
					html.append(WHITESPACE_HTML);
				} else {
					nombreAsegurado = nombreAsegurado.replaceAll("^\\s+", "");
					html.append(nombreAsegurado);
				}

				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorFechaVigor() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				Date fechaVigor = (Date) new BasicCellEditor()
						.getValue(item, FECHA_VIGOR, rowcount);
				HtmlBuilder html = new HtmlBuilder();
				if (fechaVigor == null) {
					html.append(WHITESPACE_HTML);
				} else {
					html.append(new SimpleDateFormat("dd/MM/yyyy").format(fechaVigor));
				}
				return html.toString();
			}
		};
	}
	
	private void configurarColumnas(HtmlTable table, Usuario usuario) {
				
		if(!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
			
			this.configColumna(table, columnas.get(OFICINA),"Oficina",true,true,"");	   //3		
			this.configColumna(table, columnas.get(ENTMEDIADORA),"E-S Med",true,true,"");//6
			this.configColumna(table, columnas.get(PLAN),	"Plan",true,true,"");			//4	
			this.configColumna(table, columnas.get(LINEA),"Línea",true,true,"");			//5
			this.configColumna(table, columnas.get(REFERENCIA),	"Póliza",true,true,"");	//5		
			this.configColumna(table, columnas.get(FECHA_VIGOR), "F.Vigor", true, true, ""); // 5
			this.configColumna(table, columnas.get(CIFTOMADOR),	"CIF Tomador",true,true,"");//5	
			this.configColumna(table, columnas.get(IDCOLECTIVO),"Colectivo",true,true,"");//5		
			this.configColumna(table, columnas.get(NIFCIF ),	"NIF Aseg.",true,true,"");//5							
			this.configColumna(table, columnas.get(NOMBRE_ASEGURADO),	"Asegurado",true,true,"");//6
			this.configColumna(table, columnas.get(FASE),"Fase",true,true,"");				//3
			this.configColumna(table, columnas.get(GRUPO_NEGOCIO ),	"G.N.",true,true,""); //2
			this.configColumna(table, columnas.get(PRIMA_COMERCIAL_NETA ),"PCN",true,true,"",STYLE);	//2
		}
		
		if(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
			
			this.configColumna(table, columnas.get(OFICINA),"Oficina</br>",true,true,"");
			this.configColumna(table, columnas.get(ENTMEDIADORA),"E-S Med</br>",true,true,"42px");
			this.configColumna(table, columnas.get(PLAN),	"Plan</br>",true,true,"");
			this.configColumna(table, columnas.get(LINEA),"Línea</br>",true,true,"");
			this.configColumna(table, columnas.get(REFERENCIA),	"Póliza</br>",true,true,"");	
			this.configColumna(table, columnas.get(FECHA_VIGOR), "F.Vigor</br>", true, true, "");
			this.configColumna(table, columnas.get(CIFTOMADOR),	"CIF Tomador</br>",true,true,"");	
			this.configColumna(table, columnas.get(IDCOLECTIVO),"Colectivo</br>",true,true,"");	
			this.configColumna(table, columnas.get(NIFCIF ),	"NIF Aseg.</br>",true,true,"");						
			this.configColumna(table, columnas.get(NOMBRE_ASEGURADO),	"Asegurado</br>",true,true,"");
			this.configColumna(table, columnas.get(FASE),"Fase</br>",true,true,"");
			this.configColumna(table, columnas.get(GRUPO_NEGOCIO ),	"G.N.</br>",true,true,"");
			this.configColumna(table, columnas.get(PRIMA_COMERCIAL_NETA ),"PCN</br>",true,true,"",STYLE);			
			
			this.configColumna(table, columnas.get(GD_ADMIN ), "Dev Adm</br>",true,true,"",STYLE);
			this.configColumna(table, columnas.get(GD_ADQ ), "Dev Adq</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GD_COMMED_ENTIDAD),"Dev Ent</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GD_COMMED_ESMED ),	"Dev Med</br>",true,true,"",STYLE);				
			this.configColumna(table, columnas.get(GA_ADMIN ), "Abon Adm</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GA_ADQ),"Abon Adq</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GA_COMMED_ENTIDAD), "Abon Ent</br>",true,true,"",STYLE);					
			this.configColumna(table, columnas.get(GA_COMMED_ESMED),		"Abon Med</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GP_ADMIN),"Pte Adm</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GP_ADQ ), "Pte Adq</br>",true,true,"",STYLE);				
			this.configColumna(table, columnas.get(GP_COMMED_ENTIDAD),"Pte Ent</br>",true,true,"",STYLE);	
			this.configColumna(table, columnas.get(GP_COMMED_ESMED),"Pte Med</br>",true,true,"",STYLE);	
		}
		
		if(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)
				||(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES) && usuario.isUsuarioExterno()==false)){
			this.configColumna(table, columnas.get(GD_COMMED_ENTIDAD),"Dev. Ent",true,true,"");
			this.configColumna(table, columnas.get(GD_COMMED_ESMED ),	"Dev. Med",true,true,"");			
			this.configColumna(table, columnas.get(GA_COMMED_ENTIDAD), "Abon. Ent",true,true,"");				
			this.configColumna(table, columnas.get(GA_COMMED_ESMED),		"Abon. Med",true,true,"");
			this.configColumna(table, columnas.get(GP_COMMED_ENTIDAD),"Pte. Ent",true,true,"");
			this.configColumna(table, columnas.get(GP_COMMED_ESMED),"Pte. Med",true,true,"");	
		}
		
		if(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES) && usuario.isUsuarioExterno()==true){
			this.configColumna(table, columnas.get(GD_COMMED_ESMED ),	"Dev. Med",true,true,"");			
			this.configColumna(table, columnas.get(GA_COMMED_ESMED),		"Abon. Med",true,true,"");
			this.configColumna(table, columnas.get(GP_COMMED_ESMED),"Pte. Med",true,true,"");
		}
		
	}

	public void setInformesComisionesUnificadoFilter(
			IGenericoFilter informesComisionesUnificadoFilter) {
		this.informesComisionesUnificadoFilter = informesComisionesUnificadoFilter;
	}

	public void setInformesComisionesUnificadoSort(
			IGenericoSort informesComisionesUnificadoSort) {
		this.informesComisionesUnificadoSort = informesComisionesUnificadoSort;
	}	
	
	public List<Serializable> getAllFilteredAndSorted(IGenericoDao genericoDao) throws BusinessException {

		Collection<Serializable> allResults = null;
		allResults = genericoDao.getWithFilterAndSort(informesComisionesUnificadoFilter,informesComisionesUnificadoSort, -1, -1);
		return (List<Serializable>) allResults;
	}
}
