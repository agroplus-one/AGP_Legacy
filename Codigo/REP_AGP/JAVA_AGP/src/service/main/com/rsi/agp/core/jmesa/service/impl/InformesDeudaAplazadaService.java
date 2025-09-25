package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.jmesa.view.pdf.PdfView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.dao.impl.IInformesDeudaAplazadaDao;
import com.rsi.agp.core.jmesa.filter.InformesDeudaAplazadaFilter;
import com.rsi.agp.core.jmesa.service.IInformesDeudaAplazadaService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.sort.InformesDeudaAplazadaSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeDeudaAplazadaUnificado;



public class InformesDeudaAplazadaService extends GetTablaService
implements IInformesDeudaAplazadaService {
	//Constantes para las columnas de la tabla
	public final static String GRUPO_ENT ="grupoEntidades";
	public final static String ENTIDAD= "codentidad";
	private final static String NOMENTIDAD="nomEntidad";
	private final static String ENTMEDIADORA= "entmediadora";
	private final static String SUBENTMEDIADORA= "subentmediadora";
	private final static String IDCOLECTIVO= "idcolectivo";
	private final static String CIFTOMADOR= "ciftomador";
	private final static String NOM_TOMADOR= "nomTomador";
	private final static String PLAN= "plan";
	private final static String LINEA= "linea";
	private final static String NOMLINEA= "nomLinea";
	private final static String FASE= "fase";
	private final static String RECIBO= "recibo";
	private final static String REFERENCIA= "referencia";
	private final static String GRUPO_NEGOCIO="grupoNegocio";
	private final static String FECHA_ACEPTACION= "fechaAceptacion";
	private final static String FECHA_CIERRE= "fechaCierre";
	private final static String FECHA_EMISION_RECIBO= "fechaEmisionRecibo"; 
	private final static String GA_ADMIN= "gaAdmin";
	private final static String GA_ADQ= "gaAdq";
	private final static String GA_COMMED_ENTIDAD= "gaCommedEntidad";
	private final static String GA_COMMED_ESMED= "gaCommedEsmed";
	private final static String GASTO_PAGADO= "gastoPagado";
	private final static String GD_ADMIN= "gdAdmin";
	private final static String GD_ADQ= "gdAdq";
	private final static String TOTAL_GD_ENTIDAD= "totalGdEntidad";
	private final static String GD_COMMED_ENTIDAD= "gdCommedEntidad";
	private final static String GD_COMMED_ESMED= "gdCommedEsmed";
	private final static String TOTAL_GD_MEDIADOR= "totalGdMediador";
	private final static String STYLE = "text-align: right";
	
	private static final Log logger = LogFactory.getLog(InformesDeudaAplazadaService.class);
	private InformesDeudaAplazadaFilter informesDeudaAplazadaFilter;
	private InformesDeudaAplazadaSort informesDeudaAplazadaSort;
	
	//MÉTODOS JMESA ----------------------------------------------------------------------------------------
		@Override
		public String getTabla(HttpServletRequest request,
				HttpServletResponse response,
				Serializable infDeudaBean, String origenLlamada,
				List<BigDecimal> listaGrupoEntidades, String perfil, boolean externo,IGenericoDao genericoDao) {
			boolean imprimir = false;		
			InformeDeudaAplazadaUnificado infDeuda=(InformeDeudaAplazadaUnificado)infDeudaBean;
			cargarColumnas();
			TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
		//	tableFacade.setExportTypes(response, ExportType.EXCEL);
			//tableFacade.setExportTypes(response);
			tableFacade.autoFilterAndSort(false);
			Limit limit = tableFacade.getLimit();
			if (limit.isExported()) {
				imprimir = true;
			}
			setColumnasVisibles(tableFacade,perfil,externo,imprimir);
			
			
			
			if (origenLlamada != null) {
				informesDeudaAplazadaFilter.clear();
				if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
					tableFacade.getLimit().getFilterSet().getFilters().clear();
				}			
				cargarFiltrosBusqueda(infDeuda, tableFacade);
			}
			if (listaGrupoEntidades != null && listaGrupoEntidades.size()>0) { 
				this.addListaEntidadesFilter(listaGrupoEntidades, informesDeudaAplazadaFilter, this.GRUPO_ENT);
			}
			informesDeudaAplazadaSort.clear();	
			List<InformeDeudaAplazadaUnificado> lstInfD = new ArrayList<InformeDeudaAplazadaUnificado>(); 
			if (!imprimir)
				this.setDataAndLimitVariables(tableFacade, informesDeudaAplazadaFilter, genericoDao, informesDeudaAplazadaSort, informesDeudaAplazadaSort);
			else {
				lstInfD = this.setDataAndLimitVariablesExp(tableFacade, informesDeudaAplazadaFilter, genericoDao, informesDeudaAplazadaSort, informesDeudaAplazadaSort);
			}

			String ajax = request.getParameter("ajax");
			if (!"false".equals(ajax)){
				tableFacade.setToolbar(new CustomToolbar());
				tableFacade.setView(new CustomView());
			}


			return html(tableFacade,perfil,externo);//+ script;

		}

				
		private void setColumnasVisibles(TableFacade tableFacade, String perfil, boolean externo, boolean imprimir){
			if ((Constants.PERFIL_0).toString().equals(perfil)){
				if (imprimir) {
						tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
						columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ENTIDAD),
						columnas.get(GA_COMMED_ESMED),columnas.get(GA_ADMIN),columnas.get(GA_ADQ),columnas.get(GASTO_PAGADO),
						columnas.get(GD_COMMED_ENTIDAD),columnas.get(GD_COMMED_ESMED));
				}else {
					tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
							columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ENTIDAD),
							columnas.get(GA_COMMED_ESMED),columnas.get(GA_ADMIN),columnas.get(GA_ADQ),columnas.get(GASTO_PAGADO));
				}
			}else if ((Constants.PERFIL_1).toString().equals(perfil)){
				if (externo) {
					if (imprimir) {
						tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
								columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ESMED),
								columnas.get(GD_COMMED_ESMED));
					}else {
							tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
									columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ESMED));
									
					}
				}else {
					if (imprimir) {
						tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
								columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ENTIDAD),columnas.get(GA_COMMED_ESMED),
								columnas.get(GD_COMMED_ENTIDAD),columnas.get(GD_COMMED_ESMED));
					}else {
						tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
								columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ENTIDAD),columnas.get(GA_COMMED_ESMED));
						
					}
				}
			}else {
				if (imprimir) {
					tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
							columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ENTIDAD),columnas.get(GA_COMMED_ESMED),
							columnas.get(GD_COMMED_ENTIDAD),columnas.get(GD_COMMED_ESMED));
				}else {
					tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOM_TOMADOR),
							columnas.get(PLAN),columnas.get(LINEA),columnas.get(RECIBO),columnas.get(REFERENCIA),columnas.get(GRUPO_NEGOCIO),columnas.get(GA_COMMED_ENTIDAD),columnas.get(GA_COMMED_ESMED));
				}
			}
		}	
		
	private void cargarFiltrosBusqueda(InformeDeudaAplazadaUnificado infDeuda, TableFacade tableFacade) {

		if(null!=infDeuda) {
			if(null!=infDeuda.getCodentidad())
				this.addColumnaFiltro(tableFacade, ENTIDAD, infDeuda.getCodentidad(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getEntmediadora())
				this.addColumnaFiltro(tableFacade, ENTMEDIADORA, infDeuda.getEntmediadora(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getSubentmediadora())
				this.addColumnaFiltro(tableFacade, SUBENTMEDIADORA, infDeuda.getSubentmediadora(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getIdcolectivo() && !infDeuda.getIdcolectivo().isEmpty())
				this.addColumnaFiltro(tableFacade, IDCOLECTIVO, infDeuda.getIdcolectivo(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getCiftomador())
				this.addColumnaFiltro(tableFacade, CIFTOMADOR, infDeuda.getCiftomador(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getNomTomador())
				this.addColumnaFiltro(tableFacade, NOM_TOMADOR, infDeuda.getNomTomador(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getPlan())
				this.addColumnaFiltro(tableFacade, PLAN, infDeuda.getPlan(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getLinea())
				this.addColumnaFiltro(tableFacade, LINEA, infDeuda.getLinea(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getFase())
				this.addColumnaFiltro(tableFacade, FASE, infDeuda.getFase(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getRecibo())
				this.addColumnaFiltro(tableFacade, RECIBO, infDeuda.getRecibo(),informesDeudaAplazadaFilter);			
			if(null!=infDeuda.getFechaAceptacion())
				this.addColumnaFiltro(tableFacade, FECHA_ACEPTACION, infDeuda.getFechaAceptacion(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getFechaCierre())
				this.addColumnaFiltro(tableFacade, FECHA_CIERRE, infDeuda.getFechaCierre(),informesDeudaAplazadaFilter);
			if(null!=infDeuda.getFechaEmisionRecibo())
				this.addColumnaFiltro(tableFacade, FECHA_EMISION_RECIBO, infDeuda.getFechaEmisionRecibo(),informesDeudaAplazadaFilter);
		}
	}	
	
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade, String perfil, boolean externo) {
		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			if (limit.getExportType() == ExportType.EXCEL) {
				Table table = tableFacade.getTable();
	        	// renombramos las cabeceras
	            configurarCabecerasColumnasExport(table,perfil,externo);
	            // Escribe los datos generados en el response
	            tableFacade.render(); 
	            // Devuelve nulo para que el controller no haga nada
	            return null; 			
			}else if(limit.getExportType() == ExportType.PDF){
				Table table = tableFacade.getTable();
	        	configurarCabecerasColumnasExport(table,perfil,externo);
	          
	        	PdfView pdfView = (PdfView)tableFacade.getView();
	        	pdfView.setCssLocation("/jsp/css/jmesa/jmesa-pdf.css");
	        	pdfView.setTable(table);
	        	pdfView.render();
	        	
	        	tableFacade.setView(pdfView);
	        	tableFacade.render(); 
	            // Devuelve nulo para que el controller no haga nada
	            return null; 
	            
			}
			
		} else {
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");
			configurarColumnas(table, perfil,externo);			
			table.getRow().getColumn(columnas.get(ENTMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorESMed());
			table.getRow().getColumn(columnas.get(LINEA)).getCellRenderer().setCellEditor(getCellEditorLinea());
		}
		return tableFacade.render();
	}
	
	private void configurarCabecerasColumnasExport(Table table, String perfil,boolean externo) {
		table.setCaption("Consulta Comisiones Deuda Aplazada");	    	
		Row row = table.getRow();
    	row.getColumn(ENTMEDIADORA).setTitle("E-S Med.");	    	
		row.getColumn(IDCOLECTIVO).setTitle("Colectivo");
		row.getColumn(CIFTOMADOR).setTitle("CIF Tomador");
		row.getColumn(NOM_TOMADOR).setTitle("Nombre Tomador");
		row.getColumn(PLAN).setTitle("Plan");
		row.getColumn(LINEA).setTitle("Línea");
		row.getColumn(RECIBO).setTitle("Recibo");
		row.getColumn(REFERENCIA).setTitle("Póliza");
		row.getColumn(GRUPO_NEGOCIO).setTitle("G.N.");
		if (!externo) {
			row.getColumn(GA_COMMED_ENTIDAD).setTitle("Abon Ent.");	
		}
		row.getColumn(GA_COMMED_ESMED).setTitle("Abon E-S Med");
		if ((Constants.PERFIL_0).toString().equals(perfil)){
			row.getColumn(GA_ADMIN).setTitle("Abon Adm");
			row.getColumn(GA_ADQ).setTitle("Abon Adq");
			row.getColumn(GASTO_PAGADO).setTitle("Gasto pagado");
		}
		if ((Constants.PERFIL_0).toString().equals(perfil) || ((Constants.PERFIL_1).toString().equals(perfil) && (!externo)) ||(Constants.PERFIL_5).toString().equals(perfil) ){
			row.getColumn(GD_COMMED_ENTIDAD).setTitle("Dev Med-Ent");
		}
		row.getColumn(GD_COMMED_ESMED).setTitle("Dev Med-SubEnt");
		
  }
	
	
	private CellEditor getCellEditorESMed() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				Integer entMedia = (Integer) new BasicCellEditor().getValue(item, ENTMEDIADORA,rowcount);
				Integer subEntMedia = (Integer) new BasicCellEditor().getValue(item, SUBENTMEDIADORA,rowcount);

				String esMed="";
				if (null!= entMedia) {
					esMed = entMedia.toString() + "-" + subEntMedia.toString();
				}else {
					esMed ="&nbsp;";
				}				
				HtmlBuilder html = new HtmlBuilder();
				html.append(esMed);
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorLinea() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				Integer codlinea = (Integer) new BasicCellEditor().getValue(item, LINEA,rowcount);
				
				String nomLinea = (String) new BasicCellEditor().getValue(item, NOMLINEA,rowcount);

				String linea="";
				if (null!=codlinea) {
					linea = codlinea  + "-" + nomLinea;
				}else {
					linea="&nbsp;";
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(linea);
				return html.toString();
			}
		};
	}
	
	private void configurarColumnas(HtmlTable table, String perfil, boolean externo) { // VISIBLE
		this.configColumna(table, columnas.get(ENTMEDIADORA), "E-S Med.", true, true, "6%");
		this.configColumna(table, columnas.get(IDCOLECTIVO),"Colectivo",true,true, "7%");
		this.configColumna(table, columnas.get(CIFTOMADOR),"CIF Tomador",false,true, "7%");
		this.configColumna(table, columnas.get(NOM_TOMADOR),"Nombre Tomador",false,true, "22%");
		this.configColumna(table, columnas.get(PLAN),"Plan",true,true, "4%");
		this.configColumna(table, columnas.get(LINEA),"Línea",true,true, "22.5%");
		this.configColumna(table, columnas.get(RECIBO),"Recibo",true,true, "5%");
		this.configColumna(table, columnas.get(REFERENCIA),"Póliza",false,true, "6%");
		this.configColumna(table, columnas.get(GRUPO_NEGOCIO),"G.N.",false,true, "","text-align:center" );
		if (!externo) {
			this.configColumna(table, columnas.get(GA_COMMED_ENTIDAD),"Abon Ent",true,true, "7.5%",STYLE);
		}
		this.configColumna(table, columnas.get(GA_COMMED_ESMED),"Abon E-S Med",true,true, "7.5%",STYLE);
		if ((Constants.PERFIL_0).toString().equals(perfil)){
			this.configColumna(table, columnas.get(GA_ADMIN),"Abon Adm",true,true, "7.5%",STYLE);
			this.configColumna(table, columnas.get(GA_ADQ),"Abon Adq",true,true, "7.5%",STYLE);
			this.configColumna(table, columnas.get(GASTO_PAGADO),"Gasto pagado",true,true, "7.5%",STYLE);
		}
	}

	private void cargarColumnas() { 
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ENTIDAD, ENTIDAD);
			columnas.put(ENTMEDIADORA, ENTMEDIADORA);
			columnas.put(SUBENTMEDIADORA,SUBENTMEDIADORA);	
			columnas.put(IDCOLECTIVO, IDCOLECTIVO);
			columnas.put(CIFTOMADOR, CIFTOMADOR);
			columnas.put(NOM_TOMADOR, NOM_TOMADOR);
			columnas.put(PLAN, PLAN);
			columnas.put(LINEA, LINEA);				
			columnas.put(FASE, FASE);	
			columnas.put(RECIBO, RECIBO);	
			columnas.put(REFERENCIA, REFERENCIA);	
			columnas.put(GRUPO_NEGOCIO,GRUPO_NEGOCIO);
			columnas.put(FECHA_ACEPTACION, FECHA_ACEPTACION);	
			columnas.put(FECHA_CIERRE, FECHA_CIERRE);	
			columnas.put(FECHA_EMISION_RECIBO, FECHA_EMISION_RECIBO);			
			columnas.put(GA_ADMIN, GA_ADMIN);
			columnas.put(GA_ADQ, GA_ADQ);
			columnas.put(GA_COMMED_ENTIDAD, GA_COMMED_ENTIDAD);
			columnas.put(GA_COMMED_ESMED, GA_COMMED_ESMED);
			columnas.put(GASTO_PAGADO, GASTO_PAGADO);
			columnas.put(GD_ADMIN, GD_ADMIN);
			columnas.put(GD_ADQ, GD_ADQ);
			columnas.put(TOTAL_GD_ENTIDAD, TOTAL_GD_ENTIDAD);
			columnas.put(GD_COMMED_ENTIDAD, GD_COMMED_ENTIDAD);
			columnas.put(GD_COMMED_ESMED, GD_COMMED_ESMED);
			columnas.put(TOTAL_GD_MEDIADOR, TOTAL_GD_MEDIADOR);
		}

	}
	
	public List<InformeDeudaAplazadaUnificado> setDataAndLimitVariablesExp(TableFacade tableFacade, 
			CriteriaCommand filtro, IGenericoDao genericoDao,
			IGenericoSort genericoSort, CriteriaCommand sort) {

		Collection<java.io.Serializable> items = new ArrayList<java.io.Serializable>();
		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();		

		try {
			int totalRows = genericoDao.getCountWithFilter(filtro);
			logger.debug("********** count filas  = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);
			genericoSort.getConsultaSort(limit);			
			
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items=genericoDao.getWithFilterAndSort(filtro, sort, rowStart, rowEnd);
			tableFacade.setItems(items);
			logger.debug("********** lista de items   = "+ items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		List<InformeDeudaAplazadaUnificado> lstInfD= new ArrayList<InformeDeudaAplazadaUnificado>();
		for(Object o: items) {
			InformeDeudaAplazadaUnificado infD = (InformeDeudaAplazadaUnificado)o;
			lstInfD.add(infD);
			//System.out.println(o);
		}
		return lstInfD;
	}

	public void setInformesDeudaAplazadaFilter(
			InformesDeudaAplazadaFilter informesDeudaAplazadaFilter) {
		this.informesDeudaAplazadaFilter = informesDeudaAplazadaFilter;
	}


	public void setInformesDeudaAplazadaSort(InformesDeudaAplazadaSort informesDeudaAplazadaSort) {
		this.informesDeudaAplazadaSort = informesDeudaAplazadaSort;
	}

	// métodos NO jmesa

	public String getNombreEntidad(Integer codEntidad, IInformesDeudaAplazadaDao genericoDao) {
		String nombre="";
		nombre =(String) genericoDao.getNombreEntidad(new BigDecimal(codEntidad));
		return nombre;
	}
	
	public String getNombreLinea(Integer codLinea, IInformesDeudaAplazadaDao genericoDao) {
		String nombre="";
		nombre =(String) genericoDao.getNombreLinea(new BigDecimal(codLinea));
		return nombre;
	}

	@Override
	public List<InformeDeudaAplazadaUnificado> getListado(HttpServletRequest request,
			HttpServletResponse response,
			Serializable infDeudaBean, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, String perfil, boolean externo,IGenericoDao genericoDao) {
		boolean imprimir = false;		
		InformeDeudaAplazadaUnificado infDeuda=(InformeDeudaAplazadaUnificado)infDeudaBean;
		cargarColumnas();
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
		tableFacade.setExportTypes(response, ExportType.EXCEL);
		//tableFacade.setExportTypes(response);
		tableFacade.autoFilterAndSort(false);
		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			imprimir = true;
		}
		setColumnasVisibles(tableFacade,perfil,externo,imprimir);
		
		
		
		if (origenLlamada != null) {
			informesDeudaAplazadaFilter.clear();
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}			
			cargarFiltrosBusqueda(infDeuda, tableFacade);
		}
		if (listaGrupoEntidades != null && listaGrupoEntidades.size()>0) { 
			this.addListaEntidadesFilter(listaGrupoEntidades, informesDeudaAplazadaFilter, this.GRUPO_ENT);
		}
		informesDeudaAplazadaSort.clear();	
		List<InformeDeudaAplazadaUnificado> lstItems = new ArrayList<InformeDeudaAplazadaUnificado>(); 
		
		lstItems = this.setDataAndLimitVariablesExp(tableFacade, informesDeudaAplazadaFilter, genericoDao, informesDeudaAplazadaSort, informesDeudaAplazadaSort);
	

		return lstItems;
	}
	
	public List<Serializable> getAllFilteredAndSorted(IGenericoDao genericoDao) throws BusinessException {
		
		Collection<Serializable> allResults = null;
		allResults = genericoDao.getWithFilterAndSort(informesDeudaAplazadaFilter,informesDeudaAplazadaSort, -1, -1);
		return (List<Serializable>) allResults;
	}
}
