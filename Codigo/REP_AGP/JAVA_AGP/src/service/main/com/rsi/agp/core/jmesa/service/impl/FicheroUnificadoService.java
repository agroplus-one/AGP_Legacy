package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.IFicheroUnificadoService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;

public class FicheroUnificadoService extends GetTablaService implements IFicheroUnificadoService {
	//Constantes para las columnas de la tabla
	private final static String ID= "id";
	private final static String NOMBREFICHERO= "nombreFichero";
	private final static String TIPOFICHERO= "tipoFichero";
	private final static String ESTADO= "estado";
	private final static String F_CARGA= "fechaCarga";
	private final static String F_ACEPTACION= "fechaAceptacion";
	private final static String F_CIERRE= "fechaCierre"; 
    //Falta Fecha de emisión para la deuda aplazada 
    
	IGenericoFilter ficheroUnificadoFilter ;
	IGenericoSort ficheroUnificadoSort;
	

	
	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable ficheroUnificadoBean, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao) {
		
		FicheroUnificado ficheroUnificado=(FicheroUnificado)ficheroUnificadoBean;
			
		cargarColumnas();

		
		TableFacade tableFacade= this.crearTableFacade(request, response, origenLlamada, columnas);

		
		
			
		//tableFacade.getLimit().getSortSet().addSort(new Sort (1, F_CARGA, Order.DESC));
		//tableFacade.setExportTypes(response);
		
			tableFacade.autoFilterAndSort(false);
		setColumnasVisibles(tableFacade, ficheroUnificado.getTipoFichero());
		
		if (origenLlamada != null) {
			ficheroUnificadoFilter.clear();
			
			tableFacade.getLimit().getSortSet().addSort(new Sort (1, F_CARGA, Order.DESC));
			tableFacade.setExportTypes(response);
			if(null!=tableFacade.getLimit().getFilterSet().getFilters()) {
				tableFacade.getLimit().getFilterSet().getFilters().clear();
			}			
			cargarFiltrosBusqueda(ficheroUnificado, tableFacade);
			
			
			
		}
		
		ficheroUnificadoSort.clear();		
		this.setDataAndLimitVariables(tableFacade, ficheroUnificadoFilter, genericoDao, ficheroUnificadoSort, ficheroUnificadoSort);
		
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)){
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}


		return html(tableFacade);//+ script;

	}


private void cargarColumnas() {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, ID);			
			columnas.put(NOMBREFICHERO, NOMBREFICHERO);
			columnas.put(TIPOFICHERO, TIPOFICHERO);
			columnas.put(ESTADO, ESTADO);
			columnas.put(F_CARGA, F_CARGA);
			columnas.put(F_ACEPTACION, F_ACEPTACION);
			columnas.put(F_CIERRE, F_CIERRE);			 
		}
	}	
	
private void setColumnasVisibles(TableFacade tableFacade, Character tipoFichero){
		
	if(tipoFichero!=null && !tipoFichero.equals(new Character('D'))){	
			tableFacade.setColumnProperties(columnas.get(ID),
					columnas.get(NOMBREFICHERO),columnas.get(ESTADO),columnas.get(F_CARGA),
					columnas.get(F_ACEPTACION),columnas.get(F_CIERRE));
		}else{
			tableFacade.setColumnProperties(columnas.get(ID),
					//FALTA PONERLE LA COLUMNA FECHAEMISION que de momento no sabemos de donde sacar
					columnas.get(NOMBREFICHERO),columnas.get(ESTADO),columnas.get(F_CARGA),
					columnas.get(F_ACEPTACION),columnas.get(F_CIERRE));
		}
	}
	
private void cargarFiltrosBusqueda(FicheroUnificado fichero, TableFacade tableFacade) {

	if(null!=fichero.getId())
		this.addColumnaFiltro(tableFacade, ID, fichero.getId(),ficheroUnificadoFilter);
	if(null!=fichero.getNombreFichero())
			this.addColumnaFiltro(tableFacade, NOMBREFICHERO, fichero.getNombreFichero(),ficheroUnificadoFilter);
	if(null!=fichero.getTipoFichero())
			this.addColumnaFiltro(tableFacade, TIPOFICHERO, fichero.getTipoFichero(),ficheroUnificadoFilter);
	if(null!=fichero.getEstado())
			this.addColumnaFiltro(tableFacade, ESTADO,fichero.getEstado(),ficheroUnificadoFilter);
	if(null!=fichero.getFechaCarga())
			this.addColumnaFiltro(tableFacade, F_CARGA, fichero.getFechaCarga(), ficheroUnificadoFilter);
	if(null!=fichero.getFechaAceptacion())
			this.addColumnaFiltro(tableFacade, F_ACEPTACION, fichero.getFechaAceptacion(), ficheroUnificadoFilter);
	if(null!=fichero.getFechaCierre())
			this.addColumnaFiltro(tableFacade, F_CIERRE, fichero.getFechaCierre() , ficheroUnificadoFilter);
	//FALTA FECHA DE EMISIÓN DE LOS FICHEROS DE DEUDA APLAZADA
	
}

@Override
public FicheroUnificado getBeanFromLimit(Limit consulta_LIMIT) throws ParseException {
	FicheroUnificado bean = new FicheroUnificado();
	DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	

	
	if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(NOMBREFICHERO))){
		bean.setNombreFichero(consulta_LIMIT.getFilterSet().getFilter(columnas.get(NOMBREFICHERO)).getValue());
	}
	if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(TIPOFICHERO))){
		bean.setTipoFichero(consulta_LIMIT.getFilterSet().getFilter(columnas.get(TIPOFICHERO)).getValue().charAt(0));
	}
	if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(ESTADO))){
		bean.setEstado(consulta_LIMIT.getFilterSet().getFilter(columnas.get(ESTADO)).getValue().charAt(0));
	}
	if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(F_CARGA))){
		Date date = format.parse(consulta_LIMIT.getFilterSet().getFilter(columnas.get(F_CARGA)).getValue());
		bean.setFechaCarga(date);
	}
	if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(F_ACEPTACION))){
		Date date = format.parse(consulta_LIMIT.getFilterSet().getFilter(columnas.get(F_ACEPTACION)).getValue());
		bean.setFechaAceptacion(date);
	}
	if(null != consulta_LIMIT.getFilterSet().getFilter(columnas.get(F_CIERRE))){
		Date date = format.parse(consulta_LIMIT.getFilterSet().getFilter(columnas.get(F_CIERRE)).getValue());
		bean.setFechaCierre(date);
	}
	return bean;
		
} 

@SuppressWarnings("deprecation")
private String html(TableFacade tableFacade) {

	HtmlTable table = (HtmlTable) tableFacade.getTable();
	table.getRow().setUniqueProperty("id");

	configurarColumnas(table);
	
	table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
	table.getRow().getColumn(columnas.get(ESTADO)).getCellRenderer().setCellEditor(getCellEditorEstado());
	
	return tableFacade.render();
}

private CellEditor getCellEditorAcciones() {
	return new CellEditor() {
		public Object getValue(Object item, String property, int rowcount) {
			HtmlBuilder html = new HtmlBuilder();
			Long id = (Long) new BasicCellEditor().getValue(item, property,
					rowcount);
			
			html.a().href().quote().append("javascript:descargar('"+id+"');").quote().close();			
			html.append("<img src=\"jsp/img/displaytag/download.png\" alt=\"Descargar\" title=\"Descargar\"/>");        	
            html.aEnd();
            html.append("&nbsp;");
            
            html.a().href().quote().append("javascript:revisar('"+id+"');").quote().close();			
			html.append("<img src=\"jsp/img/folderopen.gif\" alt=\"Revisar\" title=\"Revisar\"/>");        	
            html.aEnd();
            html.append("&nbsp;");
            
        	

            Date fechaCierre = (Date) new BasicCellEditor().getValue(item, F_CIERRE, rowcount);
            if(null==fechaCierre) {
            	html.a().href().quote().append("javascript:borrar('"+id+"');").quote().close();
                html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/>");
                html.aEnd();
            }else {
            	 html.append("<img src=\"jsp/img/displaytag/transparente.gif\"  width='16' height='16'/>");
                 html.aEnd();
            }
            /*if(ffc.getFase().getCierre() == null)
				acciones +="<a href=\"javascript:borrar('"+ffc.getId()+"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" /></a>&nbsp;";
			else
				acciones +="<img src=\"jsp/img/displaytag/transparente.gif\" width='16' height='16'/>";		*/
            
			return html.toString();
		}
	};
}
private void configurarColumnas(HtmlTable table) {
	
	this.configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false, false, "10%");
	this.configColumna(table, columnas.get(NOMBREFICHERO),"Fichero",true,true, "20%");
	//this.configColumna(table, columnas.get(TIPOFICHERO),"",,, "");
	this.configColumna(table, columnas.get(ESTADO),"Estado",true,true, "10%");
	this.configColumnaFecha(table, columnas.get(F_CARGA), "Fec. Carga", true, true, "20%", "dd/MM/yyyy");
	
	//Aquí falta el campo de fecha de emisión de Deuda aplaada
	this.configColumnaFecha(table, columnas.get(F_ACEPTACION),"Fec. Acept",true,true, "20%", "dd/MM/yyyy");
	this.configColumnaFecha(table, columnas.get(F_CIERRE),"Fec. Cierre",true,true, "20%", "dd/MM/yyyy");
	
}


private CellEditor getCellEditorEstado() {
	return new CellEditor() {
	    public Object getValue(Object item, String property, int rowcount) {
	    	// Obtiene el codigo de estado de la poliza actual
	    	Character estado = null;
			
			estado = ((Character)new BasicCellEditor().getValue(item, columnas.get(ESTADO), rowcount));
			
	    	// Muestra el mensaje correspondiente al estado
	    	String value = "";	
	    	if(estado!=null) {
	    		
	    		if(estado.toString().compareTo("X")==0) value="Cargado";
	    		if(estado.toString().compareTo("A")==0) value="Aviso";
	    		if(estado.toString().compareTo("E")==0) value="Erróneo";
	    		if(estado.toString().compareTo("C")==0) value="Correcto";	    		
	    	}
	    	
	    			    	
	        HtmlBuilder html = new HtmlBuilder();
	        html.append(value);
	        return html.toString();
	    }
	};
}


	

	public void setFicheroUnificadoFilter(
			IGenericoFilter ficheroUnificadoFilter) {
		this.ficheroUnificadoFilter = ficheroUnificadoFilter;
	}
	public void setFicheroUnificadoSort(
			IGenericoSort ficheroUnificadoSort) {
		this.ficheroUnificadoSort = ficheroUnificadoSort;
	}



}
