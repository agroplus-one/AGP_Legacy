package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.core.filter.MatcherKey;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.model.PageItems;
import org.jmesa.model.TableModel;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.view.html.editor.HtmlCellEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.rsi.agp.core.managers.GenericTableViewerService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.CharacterFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.LongFilterMatcher;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;

/**
 * @author T-Systems
 */
public class GenericTableViewerController extends AbstractController {
	
	private ResourceBundle rb = ResourceBundle.getBundle("tableData");
	
	private final static String TIPO_DATE = "date"; 
	private final static String TIPO_CHAR = "char"; 
	private final static String TIPO_NUMBER = "number";
	private final static String TIPO_STRING = "string";

    private GenericTableViewerService tableService;
    private String successView;
    private String id; // The unique table id.
    private TableDataFilter filtroJmesa;
	
	/**
	 * Pasos a seguir para mostrar los datos de una tabla:
	 * 		1. Obtener del fichero de propiedades los datos necesarios para consultar y pintar la tabla
	 * 		2. Consultar los datos de la tabla deseada
	 * 		3. Renderizar los datos
	 */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)throws Exception {
    	 ModelAndView mv = new ModelAndView(successView);
    	 HashMap<String, Object> filtros = new HashMap<String, Object>();
    	
//    	 Parametros de la JSP
    	String numTabla = StringUtils.nullToString(request.getParameter("tabla"));
    	String idHistorico = StringUtils.nullToString(request.getParameter("idhistorico"));
    	String comeFrom = StringUtils.nullToString(request.getParameter("comeFrom"));
    	String ROW = StringUtils.nullToString(request.getParameter("ROW"));
    	String lineaSeguroId = StringUtils.nullToString(request.getParameter("lineaSeguroId"));
    	String codPlan = StringUtils.nullToString(request.getParameter("codPlan"));
    	String codLinea = StringUtils.nullToString(request.getParameter("codLinea"));
    	
//    	Parametros del jmesa.properties
    	String caption = rb.getString(numTabla + ".caption");
    	String object = rb.getString(numTabla + ".object");
    	String objectItem = rb.getString(numTabla + ".objectItem");
    	String fields = rb.getString(numTabla + ".fields");
    	String titles = rb.getString(numTabla + ".titles");
    	String types = rb.getString(numTabla + ".types");
    	String filter = rb.getString(numTabla + ".filter");
    	
//    	Rellenamos la hashMap con los filtros
    	filtros.put("lineaSeguroId", lineaSeguroId);
    	filtros.put("codPlan", codPlan);
    	filtros.put("codLinea", codLinea);
    	filtros.put("criterio", filter);
    	filtros.put("clase",object.trim());
    	filtros.put("fields",fields);
    	filtros.put("types", types);
    	filtros.put("claseItem", objectItem.trim());
        
//    	Filtro
        filtroJmesa = new TableDataFilter();
        filtroJmesa.setFiltros(filtros);
        
        TableModel tableModel = new TableModel(id, request, response);
        
//		Definimos los tipos para los filtros
        tableModel.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
        tableModel.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
        tableModel.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
        tableModel.addFilterMatcher(new MatcherKey(Character.class), new CharacterFilterMatcher());
        tableModel.addFilterMatcher(new MatcherKey(String.class), new StringFilterMatcher());
        
        tableModel.setStateAttr("restore");
        
        /*
         * We are only returning one page of data. To do this we must first find the total rows. 
         * The total rows can only be figured out after filtering out the data. The sorting does
         * not effect the total row count but is needed to return the correct set of sorted rows.
         */
              
        tableModel.setItems(new PageItems(){
            public int getTotalRows(Limit limit) {
            	return tableService.getTableDataCountWithFilter(filtroJmesa,limit.getFilterSet());
            }

            public Collection<Object> getItems(Limit limit) {
                int rowStart = limit.getRowSelect().getRowStart();
                int rowEnd = limit.getRowSelect().getRowEnd();
                return tableService.getTableDataWithFilterAndSort(filtroJmesa, limit.getFilterSet(),getSort(limit), rowStart, rowEnd);
            }
        });
        

        tableModel.setTable(getHtmlTable(caption, fields.split("\\|"), titles.split("\\|"), types.split("\\|")));
        		
        	  String view = tableModel.render();
        	  request.setAttribute("tableData", view);
      
            
       


        return mv.addObject("tabla", numTabla)
        		 .addObject("idhistorico", idHistorico)
        		 .addObject("comeFrom", comeFrom)
        		 .addObject("ROW", ROW)
			     .addObject("lineaSeguroId", lineaSeguroId)
			     .addObject("codPlan", codPlan)
			     .addObject("codLinea", codLinea);
    }
    
    /**
     * Para filtrar los elementos. Se necesita el límite y el filtro para filtrar las filas.
     *
     * @param limit 
     */
    protected TableDataFilter getFilter(Limit limit) {
    	TableDataFilter filtro = new TableDataFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            filtro.addFilter(property, value);
        }

        return filtro;
    }

    /**
     * Para ordenar los elementos. Se necesita información  del límite y el orden.
     *
     * @ limit 
    */
   protected TableDataSort getSort(Limit limit) {
	   TableDataSort sort = new TableDataSort();
       SortSet sortSet = limit.getSortSet();
       Collection<Sort> sorts = sortSet.getSorts();
       for (Sort s : sorts) {
           String property = s.getProperty();
           String order = s.getOrder().toParam();
           sort.addSort(property, order);
       }

       return sort;
   }

    private Table getHtmlTable(String caption, String[] fields, String[] titles, String[] types) {
        HtmlTable htmlTable = new HtmlTable().caption(caption);

        HtmlRow htmlRow = new HtmlRow();
        htmlTable.setRow(htmlRow);
        
        for (int i = 0; i < fields.length; i++){
        	String field = fields[i];
        	String title = titles[i];
        	
        	HtmlColumn columna = new HtmlColumn(field).title(title);
        	//Formateos especiales de los datos: fechas
        	if (types[i].equals(GenericTableViewerController.TIPO_DATE))
        		columna.setCellEditor(new DateCellEditor("dd/MM/yyyy"));
        	
        	//Alineación especial de campos
        	if (types[i].equals(GenericTableViewerController.TIPO_DATE) || types[i].equals(GenericTableViewerController.TIPO_CHAR)
        		|| types[i].equals(GenericTableViewerController.TIPO_NUMBER)){
        		columna.setStyle("text-align: center;");
        	}
        	if (types[i].equals(GenericTableViewerController.TIPO_STRING)){
        		columna.setCellEditor(new CellEditor() { 
                    public Object getValue(Object item, String property, int rowcount) { 
                        Object value = new HtmlCellEditor().getValue(item, property, rowcount); 
                        String valor = StringUtils.nullToString(value); 
                        HtmlBuilder html = new HtmlBuilder(); 
                        html.append(valor); 
                        return html.toString(); 
                    } 
                });
        	}
        	if (types[i].equals(GenericTableViewerController.TIPO_CHAR)){
        		columna.setCellEditor(new CellEditor() {
                    public Object getValue(Object item, String property, int rowcount) {
                        Object value = new HtmlCellEditor().getValue(item, property, rowcount);
                        String valor = StringUtils.nullToString(value);
                        HtmlBuilder html = new HtmlBuilder();
                        if (StringUtils.nullToString(valor).equals(" ") || StringUtils.nullToString(valor).equals("")){
                        	html.append("&nbsp;");
                        }
                        else{
                        	html.append(valor);
                        }
                        return html.toString();
                    } 
                });
        	}
        	
        	htmlRow.addColumn(columna);
        }
        
        return htmlTable;
    }

    public void setTableService(GenericTableViewerService tableService) {
        this.tableService = tableService;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setId(String id) {
        this.id = id;
    }
}
