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
 * @author ASF
 * Clase para redefinir la barra de herramientas del componente.
 */
public class CustomToolbar extends HtmlToolbar{
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
        
        html.table(2).border("0").cellpadding("0").cellspacing("2")
        	.align("right").style("font-family: tahoma, verdana, arial;color: #626262;font-size:11px;").close();

        html.tr(3).styleClass("menu").close();
        
        html.td(4).close().span().id("adviceFilter").close().spanEnd().tdEnd();
        
        for (ToolbarItem item : getToolbarItems()) {
            html.td(4).close();
            html.append(item.getToolbarItemRenderer().render());
            html.tdEnd();
        }

        html.trEnd(3);

        html.tableEnd(2);
        html.newline();
        html.tabs(2);

                
        String result = html.toString();
        
        return result;
    }
        
}

