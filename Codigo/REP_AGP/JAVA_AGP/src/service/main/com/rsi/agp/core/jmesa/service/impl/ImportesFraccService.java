package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.dao.impl.ImportesFraccDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;

public class ImportesFraccService extends GetTablaService{
	
	//Constantes para las columnas de la tabla
	public final static String ID = "id";
	public final static String CODPLAN = "codplan";
	public final static String CODLINEA= "codlinea";
	public final static String NOMLINEA= "nomlinea";
	public final static String IMPORTE = "importe";
	public final static String CODENTMED= "codentidad";
	public final static String CODSUBENTMED = "codsubentidad"; 
	public final static String TIPO = "tipo";
	public final static String PCTRECARGO = "pctRecargo";

	IGenericoFilter importesFraccFilter ;
	IGenericoSort importesFraccSort;
	
	public void setImportesFraccFilter(IGenericoFilter importesFraccFilter) {
		this.importesFraccFilter = importesFraccFilter;
	}
	public void setImportesFraccSort(IGenericoSort importesFraccSort) {
		this.importesFraccSort = importesFraccSort;
	}
	
	private ImportesFraccDao importeFraccDao;
	 	
	public ImportesFraccDao getImporteFraccDao() {
		return importeFraccDao;
	}
	public void setImporteFraccDao(ImportesFraccDao importeFraccDao) {
		this.importeFraccDao = importeFraccDao;
	}
	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable importeFraccBean, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao) {
		
		ImporteFraccionamiento importeFraccionamiento = (ImporteFraccionamiento)importeFraccBean;
			
		cargarColumnas();
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);
		
		setColumnasVisibles(tableFacade);
		
		importesFraccFilter.clear();
		
		if (origenLlamada.compareTo(Constants.ORIGEN_LLAMADA_PAGINACION)!=0) {
			importesFraccFilter.clear();
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}
			cargarFiltrosBusqueda(importeFraccionamiento, tableFacade);
			importesFraccSort.clear();		
			this.setDataAndLimitVariables(tableFacade, importesFraccFilter, genericoDao, importesFraccSort, importesFraccSort);
		}else {
			importesFraccFilter.clear();
			
			ImporteFraccionamiento importeFraccionamientoLimit = this.getBeanFromLimit(tableFacade.getLimit());
			cargarFiltrosBusqueda(importeFraccionamientoLimit, tableFacade);
			importesFraccSort.clear();		
			this.setDataAndLimitVariables(tableFacade, importesFraccFilter, genericoDao, importesFraccSort, importesFraccSort);
		}
		
		
		
		/*if (null!=origenLlamada) {*/
//			cargarFiltrosBusqueda(importeFraccionamiento, tableFacade);
//			importesFraccSort.clear();		
//			this.setDataAndLimitVariables(tableFacade, importesFraccFilter, genericoDao, importesFraccSort, importesFraccSort);
		/*}else {
			importesFraccFilter.clear();
			ImporteFraccionamiento importeFraccionamientoLimit = this.getBeanFromLimit(tableFacade.getLimit());
			cargarFiltrosBusqueda(importeFraccionamientoLimit, tableFacade);
			importesFraccSort.clear();			
			this.setDataAndLimitVariables(tableFacade, importesFraccFilter, genericoDao, importesFraccSort, importesFraccSort);
		}*/
			
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}


		return html(tableFacade);//+ script;

	}
	
	private String html(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();
		
		// Configuracion de los datos de las columnas que requieren un tratamiento para mostrarse
			
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");

		configurarColumnas(table);
			
		table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
		table.getRow().getColumn(columnas.get(CODLINEA)).getCellRenderer().setCellEditor(getCellEditorLinea());
		table.getRow().getColumn(columnas.get(TIPO)).getCellRenderer().setCellEditor(getCellEditorTipo());
	//	table.getRow().getColumn(columnas.get(CODENTMED)).getCellRenderer().setCellEditor(getCellEditorEntidad());

		return tableFacade.render();
	}
	
	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				Long id = (Long)new BasicCellEditor().getValue(item, "id", rowcount);
				BigDecimal codPlan = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codplan", rowcount);
            	BigDecimal codLinea = (BigDecimal)new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
            	String descLinea = (String)new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);
            	BigDecimal codEntidad=(BigDecimal) new BasicCellEditor().getValue(item,"subentidadMediadora.id.codentidad", rowcount);
            	BigDecimal codSubEntidad=(BigDecimal) new BasicCellEditor().getValue(item,"subentidadMediadora.id.codsubentidad", rowcount);
            	BigDecimal importeMinimo = (BigDecimal)new BasicCellEditor().getValue(item, "importe",rowcount);
            	Integer tipoFracc = (Integer)new BasicCellEditor().getValue(item, "tipo",rowcount);
            	BigDecimal perRecargo = (BigDecimal)new BasicCellEditor().getValue(item, "pctRecargo",rowcount);
            	
            	HtmlBuilder html = new HtmlBuilder();
            	
            	//boton editar
            	html.a().href().quote().append("javascript:modificar("+id+","+codPlan+","+codLinea+",'"+descLinea+"',"+codEntidad+","+codSubEntidad+","+importeMinimo+","+tipoFracc+"," +
            			perRecargo+");").quote().close();
            	
            	html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar ImporteFraccionamiento\" title=\"Editar ImporteFraccionamiento\"/>");
                html.aEnd();
                html.append("&nbsp;");
            			
                // boton borrar 
            	html.a().href().quote().append("javascript:baja("+id+","+codPlan+","+codLinea+",'"+descLinea+"',"+importeMinimo+","+tipoFracc+"," + perRecargo+");").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar ImporteFraccionamiento\" title=\"Borrar ImporteFraccionamiento\"/>");
                html.aEnd();
                html.append("&nbsp;");
	                	
            	return html.toString();
            }
		};
	}
	
	private CellEditor getCellEditorLinea() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal codlinea = (BigDecimal) new BasicCellEditor().getValue(item, "linea.codlinea", rowcount);
				String nomLinea = (String) new BasicCellEditor().getValue(item, "linea.nomlinea", rowcount);

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
	
	private CellEditor getCellEditorEntidad() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal codentidad = (BigDecimal) new BasicCellEditor().getValue(item, "subentidadMediadora.id.codentidad", rowcount);
				BigDecimal codsubentidad = (BigDecimal) new BasicCellEditor().getValue(item, "subentidadMediadora.id.codsubentidad", rowcount);

				String entidad="";
				if (null!=codentidad) {
					entidad = codentidad  + "-" + codsubentidad;
				}else {
					entidad="&nbsp;";
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(entidad);
				return html.toString();
			}
		};
	}
	
	private CellEditor getCellEditorTipo() {
		
		return new CellEditor() {
			
			public Object getValue(Object item, String property, int rowcount) {
				
				Integer tipo = (Integer) new BasicCellEditor().getValue(item, "tipo", rowcount);
				
            	HtmlBuilder html = new HtmlBuilder();
            	
            	if (tipo.compareTo(Constants.FINANCIACION_SAECA) == 0) {
            		html.append("SAECA");
            	}else if (tipo.compareTo(Constants.FINANCIACION_AGROSEGURO) == 0) {
            		html.append("AGROSEGURO");
            	}
            	return html.toString();
            }
		};
	}
	
	private void configurarColumnas(HtmlTable table) {
		this.configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false, "4%");
		this.configColumna(table, columnas.get(CODPLAN), "Plan", true, true, "4%");
		this.configColumna(table, columnas.get(CODLINEA), "Línea", true, true, "22.5%");
		this.configColumna(table, columnas.get("entidad"), "E-S Med.", true, true, "5%");
		this.configColumna(table, columnas.get(IMPORTE), "Importe", true, true, "4%");
		this.configColumna(table, columnas.get(TIPO), "Tipo", true, true, "5%");
		this.configColumna(table, columnas.get(PCTRECARGO), "% Recargo", true, true, "4%");
	}
	
	private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, "id");
			columnas.put(CODPLAN, "linea.codplan");
			columnas.put(CODLINEA, "linea.codlinea");
			columnas.put(NOMLINEA, "linea.nomlinea");
			columnas.put("entidad", "entidad");
			columnas.put(CODENTMED,"subentidadMediadora.id.codentidad");
			columnas.put(CODSUBENTMED,"subentidadMediadora.id.codsubentidad"); 
			columnas.put(IMPORTE, "importe");
			columnas.put(TIPO, "tipo");
			columnas.put(PCTRECARGO, "pctRecargo");
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private void setColumnasVisibles(TableFacade tableFacade) {
		tableFacade.setColumnProperties(
				columnas.get(ID),
				columnas.get(CODPLAN),
				columnas.get(CODLINEA),
				columnas.get("entidad"),
				columnas.get(IMPORTE),
				columnas.get(TIPO),
				columnas.get(PCTRECARGO));
	}
		
	private void cargarFiltrosBusqueda(ImporteFraccionamiento impFracc, TableFacade tableFacade) {
		
//		if(0l !=impFracc.getId()) 
//			this.addColumnaFiltro(tableFacade, ID, impFracc.getId(), importesFraccFilter);
//		
		if(null != impFracc.getLinea().getCodplan()) 
			this.addColumnaFiltro(tableFacade, CODPLAN, impFracc.getLinea().getCodplan(), importesFraccFilter);
		
		if(null != impFracc.getLinea().getCodlinea()) 
			this.addColumnaFiltro(tableFacade, CODLINEA, impFracc.getLinea().getCodlinea(), importesFraccFilter);
		if(null!= impFracc.getSubentidadMediadora().getId().getCodentidad())
			this.addColumnaFiltro(tableFacade, CODENTMED, impFracc.getSubentidadMediadora().getId().getCodentidad(), importesFraccFilter);
		if(null!= impFracc.getSubentidadMediadora().getId().getCodsubentidad() )	
			this.addColumnaFiltro(tableFacade, CODSUBENTMED, impFracc.getSubentidadMediadora().getId().getCodsubentidad(), importesFraccFilter);
		if(null != impFracc.getImporte()) 
			this.addColumnaFiltro(tableFacade, IMPORTE, impFracc.getImporte(), importesFraccFilter);
		
		/*if(null != Integer.toString(impFracc.getTipo()) ) 
			this.addColumnaFiltro(tableFacade, TIPO, impFracc.getTipo(), importesFraccFilter);*/
		if(FiltroUtils.noEstaVacio (impFracc.getTipo()))
			this.addColumnaFiltro(tableFacade, TIPO, impFracc.getTipo(), importesFraccFilter);
	
		if(null!=impFracc.getPctRecargo()) 
			this.addColumnaFiltro(tableFacade, PCTRECARGO, impFracc.getPctRecargo(), importesFraccFilter);
		
	}
	
	public ImporteFraccionamiento getBeanFromLimit(Limit consulta_LIMIT) {
		ImporteFraccionamiento bean = new ImporteFraccionamiento();
		
		     if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(ImportesFraccService.ID))){
		    	 bean.setId(new Long(consulta_LIMIT.getFilterSet().getFilter(columnas.get(ImportesFraccService.ID)).getValue()).longValue());
		     }
		     
		     if(null != consulta_LIMIT.getFilterSet().getFilter("linea.codplan")){
		    	 bean.getLinea().setCodplan(new BigDecimal(consulta_LIMIT.getFilterSet().getFilter("linea.codplan").getValue()));
		     }
		     
		     if(null != consulta_LIMIT.getFilterSet().getFilter("linea.codlinea")){
		    	 bean.getLinea().setCodlinea(new BigDecimal(consulta_LIMIT.getFilterSet().getFilter("linea.codlinea").getValue()));
		     }
		     if(null != consulta_LIMIT.getFilterSet().getFilter("subentidadMediadora.id.codentidad")){
		    	 bean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal(consulta_LIMIT.getFilterSet().getFilter("subentidadMediadora.id.codentidad").getValue()));
		     }
		     if(null != consulta_LIMIT.getFilterSet().getFilter("subentidadMediadora.id.codsubentidad")){
		    	 bean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(consulta_LIMIT.getFilterSet().getFilter("subentidadMediadora.id.codsubentidad").getValue()));
		     }
		     if(null != consulta_LIMIT.getFilterSet().getFilter("entidad")){
		    	 bean.setEntidad(consulta_LIMIT.getFilterSet().getFilter("entidad").getValue());
		     }
		     if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(this.IMPORTE))){
		    	 bean.setImporte(new BigDecimal(consulta_LIMIT.getFilterSet().getFilter(columnas.get(ImportesFraccService.IMPORTE)).getValue()));
		     }
		     
		     if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(this.PCTRECARGO))){
		    	 bean.setPctRecargo(new BigDecimal(consulta_LIMIT.getFilterSet().getFilter(columnas.get(ImportesFraccService.PCTRECARGO)).getValue()));
		     }
	
		     if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(this.TIPO))){
		    	 bean.setTipo(new Integer(consulta_LIMIT.getFilterSet().getFilter(columnas.get(this.TIPO)).getValue()));
		     }
		return bean;
	}	
	

}
