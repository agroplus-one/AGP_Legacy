package com.rsi.agp.core.jmesa.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.view.ViewUtils;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.HtmlToolbar;
import org.jmesa.view.html.toolbar.MaxRowsItem;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemType;

/**
 * @author DAA 06/02/2013
 * Clase para redefinir la barra de herramientas del componente incluyendo en check Marcar Todos.
 */
public class CustomToolbarMarcarTodos extends HtmlToolbar{
    @Override
    public String render() {
    	addToolbarItem(ToolbarItemType.FIRST_PAGE_ITEM);
        addToolbarItem(ToolbarItemType.PREV_PAGE_ITEM);
        addToolbarItem(ToolbarItemType.NEXT_PAGE_ITEM);
        addToolbarItem(ToolbarItemType.LAST_PAGE_ITEM);
        
        HtmlBuilder html = new HtmlBuilder();

        addToolbarItem(ToolbarItemType.SEPARATOR);
        
        MaxRowsItem maxRowsItem = (MaxRowsItem) addToolbarItem(ToolbarItemType.MAX_ROWS_ITEM);
        if (getMaxRowsIncrements() != null) {
            maxRowsItem.setIncrements(getMaxRowsIncrements());
        }
        
        boolean exportable = ViewUtils.isExportable(getExportTypes());

        if (exportable) {
            addToolbarItem(ToolbarItemType.SEPARATOR);
            addExportToolbarItems(getExportTypes());
        }
        html.table(1).border("0").cellpadding("0").cellspacing("2")
    	.style("font-family: tahoma, verdana, arial; color: #626262; font-size:11px; width:100%").close();
        	
        	html.tr(1).close();
        		html.td(1).style("text-align:left").close();
        			html.append("<input type=\"checkbox\" id=\"checkTodos\"  name=\"checkTodos\" onClick =\"marcarTodos()\" class=\"dato\"/>");
        			html.append("&nbsp;&nbsp;&nbsp;");
        			html.append("Marcar Todos");
        		html.tdEnd();
        		html.td(2).close();
        		
	        		html.table(2).border("0").cellpadding("0").cellspacing("2").style("font-family: tahoma, verdana, arial;color: #626262;font-size:11px;").close();
		            
		            	html.tr(4).styleClass("menu").close();
		               		html.td(5).close().span().id("adviceFilter").close().spanEnd().tdEnd();
		            
					            for (ToolbarItem item : getToolbarItems()) {
					                html.td(5).close();
					                html.append(item.getToolbarItemRenderer().render());
					                html.tdEnd();
					            }
		
					    html.trEnd(4);
		
		            html.tableEnd(2);
		            html.newline();
		            html.tabs(2);
        		
        		html.tdEnd();
        	html.trEnd(1);	
        html.tableEnd(1);
            
        String result = html.toString();
        
        return result;
    }
        
}

