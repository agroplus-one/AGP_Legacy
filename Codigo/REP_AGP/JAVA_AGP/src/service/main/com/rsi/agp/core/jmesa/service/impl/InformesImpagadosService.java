package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.tables.comisiones.impagados.InformeImpagados;


public class InformesImpagadosService extends GetTablaService {
	
	//Constantes para las columnas de la tabla
	private final static String ID="id";
	private final static String CODENTIDAD= "codentidad";
	private final static String NOMENTIDAD= "nomentidad";
	private final static String ENTMEDIADORA= "entmediadora";
	private final static String SUBENTMEDIADORA= "subentmediadora";
	private final static String CODPLAN= "codplan";
	private final static String CODLINEA= "codlinea";
	private final static String NOMLINEA= "nomlinea";
	private final static String IDCOLECTIVO= "idcolectivo";
	private final static String RECIBO= "recibo";
	private final static String FASE= "fase";
	private final static String FECHAEMISION= "fechaemision"; 
	private final static String FECHAACEPTACION= "fechaaceptacion"; 
	private final static String FECHACIERRE= "fechacierre";
	private final static String CIFTOMADOR= "ciftomador";
	private final static String NOMBRETOMADOR= "nombretomador";
	private final static String SALDOPENDIENTE= "saldopendiente";
	private final static String COBRORECIBIDO= "cobrorecibido"; 
	private final static String TOTALGASTOS= "totalgastos";
	private final static String ESMAYORIGUAL2015= "esMayorIgual2015";
	private final static String STYLE = "text-align: right";
	
	public final static String CAMPO_LISTADOGRUPOENT = "grupoEntidades";
		
	IGenericoFilter informesImpagadosFilter ;
	IGenericoSort informesImpagadosSort; 	
	
	public void setInformesImpagadosSort(IGenericoSort informesImpagadosSort) {
		this.informesImpagadosSort = informesImpagadosSort;
	}

	public void setInformesImpagadosFilter(IGenericoFilter informesImpagadosFilter) {
		this.informesImpagadosFilter = informesImpagadosFilter;
	}

	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable informeImpagadosBean, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao) {
		
		InformeImpagados informeImpagados=(InformeImpagados)informeImpagadosBean;
			
		cargarColumnas();
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
		//tableFacade.setExportTypes(response, ExportType.EXCEL,ExportType.PDF);
	//	tableFacade.setExportTypes(response, ExportType.EXCEL);
		
		setColumnasVisibles(tableFacade);
		
		if (origenLlamada != null) {
			informesImpagadosFilter.clear();
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}			
			cargarFiltrosBusqueda(informeImpagados, tableFacade);
		}
			
		if(null==informeImpagados.getCodentidad()) {
			this.addListaEntidadesFilter(listaGrupoEntidades, informesImpagadosFilter, CAMPO_LISTADOGRUPOENT);
		}
		informesImpagadosSort.clear();		
		this.setDataAndLimitVariables(tableFacade, informesImpagadosFilter, genericoDao, informesImpagadosSort, informesImpagadosSort);
		
		//String listaIdsTodos = getlistaIdsTodos(consultaFilter);
		//String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}


		return html(tableFacade);//+ script;

	}
	
	private String html(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();
				
		if (limit.isExported()) {
			if (limit.getExportType() == ExportType.EXCEL) {
				Table table = tableFacade.getTable();
	        	// Quita la columna Id del informe
	        	//eliminarColumnaId(tableFacade, table);
	        	// renombramos las cabeceras
	            configurarCabecerasColumnasExport(table);
	            // Escribe los datos generados en el response
	            tableFacade.render(); 
	            // Devuelve nulo para que el controller no haga nada
	            return null; 			
			}else if(limit.getExportType() == ExportType.PDF){
				Table table = tableFacade.getTable();
	        	configurarCabecerasColumnasExport(table);
	          
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
			// Configuracion de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// campo acciones
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");

			configurarColumnas(table);
			
			table.getRow().getColumn(columnas.get(ENTMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorESMed());
			table.getRow().getColumn(columnas.get(CODLINEA)).getCellRenderer().setCellEditor(getCellEditorLinea());			
		}

		return tableFacade.render();
	}
	
	  private void configurarCabecerasColumnasExport(Table table) {
			table.setCaption("Comisiones de impagados");	    	
			Row row = table.getRow();
	    	row.getColumn(ENTMEDIADORA).setTitle("E Med");
	    	row.getColumn(SUBENTMEDIADORA).setTitle("S Med");
			row.getColumn(IDCOLECTIVO).setTitle("Colectivo");		
			row.getColumn(CIFTOMADOR).setTitle("CIF Tomador");
			row.getColumn(NOMBRETOMADOR).setTitle("Nombre Tomador");
			row.getColumn(CODPLAN).setTitle("Plan");
			row.getColumn(CODLINEA).setTitle("Línea");
			row.getColumn(FASE).setTitle("Fase");
			row.getColumn(RECIBO).setTitle("Recibo");
			row.getColumn(SALDOPENDIENTE).setTitle("Saldo pendiente");
			row.getColumn(COBRORECIBIDO).setTitle("Cobro recibido");
			row.getColumn(TOTALGASTOS).setTitle("Total gastos");

	  }
	
	private CellEditor getCellEditorESMed() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				Long entMedia = (Long) new BasicCellEditor().getValue(item, ENTMEDIADORA,rowcount);
				Long subEntMedia = (Long) new BasicCellEditor().getValue(item, SUBENTMEDIADORA,rowcount);

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

				Long codlinea = (Long) new BasicCellEditor().getValue(item, CODLINEA,rowcount);
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
	
	private void configurarColumnas(HtmlTable table) {
		this.configColumna(table, columnas.get(ENTMEDIADORA), "E-S Med.", true, true, "6%");
		this.configColumna(table, columnas.get(IDCOLECTIVO), "Colectivo", true, true, "6%");		
		this.configColumna(table, columnas.get(CIFTOMADOR), "CIF Tomador", false, true, "7%");
		this.configColumna(table, columnas.get(NOMBRETOMADOR), "Nombre Tomador", false, true, "16%");
		this.configColumna(table, columnas.get(CODPLAN), "Plan", true, true, "5%");
		this.configColumna(table, columnas.get(CODLINEA), "Línea", true, true, "22.5%");
		this.configColumna(table, columnas.get(FASE), "Fase", true, true, "4%");
		this.configColumna(table, columnas.get(RECIBO), "Recibo", true, true, "5%");
		this.configColumna(table, columnas.get(SALDOPENDIENTE), "Saldo pendiente", false, true, "7.5%",STYLE);
		this.configColumna(table, columnas.get(COBRORECIBIDO), "Cobro recibido", false, true, "7.5%",STYLE);
		this.configColumna(table, columnas.get(TOTALGASTOS), "Total gastos", false, true, "7.5%",STYLE);
	}
	
	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);
			columnas.put(ENTMEDIADORA, ENTMEDIADORA);
			columnas.put(SUBENTMEDIADORA,SUBENTMEDIADORA);
			columnas.put(IDCOLECTIVO, IDCOLECTIVO);
			columnas.put(CIFTOMADOR, CIFTOMADOR);
			columnas.put(NOMBRETOMADOR, NOMBRETOMADOR);
			columnas.put(CODPLAN, CODPLAN);
			columnas.put(CODLINEA, CODLINEA);
			columnas.put(NOMLINEA, NOMLINEA);
			columnas.put(FASE, FASE);
			columnas.put(RECIBO, RECIBO);
			columnas.put(SALDOPENDIENTE, SALDOPENDIENTE);
			columnas.put(COBRORECIBIDO, COBRORECIBIDO); 
			columnas.put(TOTALGASTOS, TOTALGASTOS);			
			columnas.put(CODENTIDAD, CODENTIDAD);
			columnas.put(NOMENTIDAD, NOMENTIDAD);			
			columnas.put(FECHAEMISION, FECHAEMISION); 
			columnas.put(FECHAACEPTACION, FECHAACEPTACION); 
			columnas.put(FECHACIERRE, FECHACIERRE);
			columnas.put(ESMAYORIGUAL2015, ESMAYORIGUAL2015);
		}
		
	}
	
	private void setColumnasVisibles(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();	
		if (!limit.isExported()){
			setColumnasVisiblesGrid(tableFacade);
		}else{
			setColumnasVisiblesExport(tableFacade);
		}		
	}
		
	private void setColumnasVisiblesGrid(TableFacade tableFacade){
		tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),
				columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOMBRETOMADOR),
				columnas.get(CODPLAN),columnas.get(CODLINEA),
				columnas.get(FASE),columnas.get(RECIBO),
				columnas.get(SALDOPENDIENTE),columnas.get(COBRORECIBIDO), 
				columnas.get(TOTALGASTOS));
	}
	
	private void setColumnasVisiblesExport(TableFacade tableFacade){
		tableFacade.setColumnProperties(columnas.get(ENTMEDIADORA),columnas.get(SUBENTMEDIADORA),
				columnas.get(IDCOLECTIVO),columnas.get(CIFTOMADOR),columnas.get(NOMBRETOMADOR),
				columnas.get(CODPLAN),columnas.get(CODLINEA),
				columnas.get(FASE),columnas.get(RECIBO),
				columnas.get(SALDOPENDIENTE),columnas.get(COBRORECIBIDO), 
				columnas.get(TOTALGASTOS));
	}
	
	private void cargarFiltrosBusqueda(InformeImpagados infImp, TableFacade tableFacade) {
		if(null!=infImp.getCodentidad()) 
			this.addColumnaFiltro(tableFacade, CODENTIDAD, infImp.getCodentidad(), informesImpagadosFilter);		
		
		if(null!=infImp.getCodplan()) 
			this.addColumnaFiltro(tableFacade, CODPLAN, infImp.getCodplan(), informesImpagadosFilter);
		
		if(null!=infImp.getCodlinea()) 
			this.addColumnaFiltro(tableFacade, CODLINEA, infImp.getCodlinea(), informesImpagadosFilter);
		
		if(null!=infImp.getEntmediadora()) 
			this.addColumnaFiltro(tableFacade, ENTMEDIADORA, infImp.getEntmediadora(), informesImpagadosFilter);
		
		if(null!=infImp.getSubentmediadora()) 
			this.addColumnaFiltro(tableFacade, SUBENTMEDIADORA, infImp.getSubentmediadora(), informesImpagadosFilter);		

		if(null!=infImp.getIdcolectivo() && !infImp.getIdcolectivo().isEmpty()) 
			this.addColumnaFiltro(tableFacade, IDCOLECTIVO, infImp.getIdcolectivo(), informesImpagadosFilter);
		
		if(null!=infImp.getRecibo())
			this.addColumnaFiltro(tableFacade, RECIBO, infImp.getRecibo(), informesImpagadosFilter);
					
		if(null!=infImp.getFase()&& !infImp.getFase().isEmpty()) 
			this.addColumnaFiltro(tableFacade, FASE, infImp.getFase(), informesImpagadosFilter);
		
		if(null!=infImp.getFechaemision()) 
			this.addColumnaFiltro(tableFacade, FECHAEMISION, infImp.getFechaemision(), informesImpagadosFilter); 
		//tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(FECHAINICIO), new SimpleDateFormat("dd/MM/yyyy").format(fechaContratacionSbp.getFechainicio())));
			//this.addColumnaFiltro(tableFacade, FECHAEMISION,  new SimpleDateFormat("dd/MM/yyyy").format(infImp.getFechaemision()), informesImpagadosFilter);
		if(null!=infImp.getFechaaceptacion()) 
			this.addColumnaFiltro(tableFacade, FECHAACEPTACION, infImp.getFechaaceptacion(), informesImpagadosFilter); 
				
		if(null!=infImp.getFechacierre()) 
			this.addColumnaFiltro(tableFacade, FECHACIERRE, infImp.getFechacierre(), informesImpagadosFilter);
	
		if(null!=infImp.getEsMayorIgual2015()) 
			this.addColumnaFiltro(tableFacade, ESMAYORIGUAL2015, infImp.getEsMayorIgual2015(), informesImpagadosFilter);
		
	}	
	
	public List<Serializable> getAllFilteredAndSorted(IGenericoDao genericoDao) throws BusinessException {

		Collection<Serializable> allResults = null;
		allResults = genericoDao.getWithFilterAndSort(informesImpagadosFilter,informesImpagadosSort, -1, -1);
		return (List<Serializable>) allResults;
	}
}
