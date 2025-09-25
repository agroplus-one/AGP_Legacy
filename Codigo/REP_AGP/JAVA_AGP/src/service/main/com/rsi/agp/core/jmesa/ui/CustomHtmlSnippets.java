package com.rsi.agp.core.jmesa.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jmesa.core.CoreContext;
import org.jmesa.view.component.Column;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlConstants;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.HtmlUtils;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.view.html.toolbar.Toolbar;

//public class CustomHtmlSnippets extends HtmlSnippetsImpl implements HtmlSnippets {
public class CustomHtmlSnippets extends HtmlSnippets {

	public CustomHtmlSnippets(HtmlTable table, Toolbar toolbar,
			CoreContext coreContext) {
		super(table, toolbar, coreContext);
	}
	
	public String header(String... columsToRemove ) {
        HtmlBuilder html = new HtmlBuilder();
        String headerClass = getCoreContext().getPreference(HtmlConstants.HEADER_CLASS);
        html.tr(1).styleClass(headerClass).close();

        HtmlRow row = getHtmlTable().getRow();
        List<Column> columns = row.getColumns();

        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            HtmlColumn column = (HtmlColumn) iter.next();
            boolean pintar = true;
            for (String columnName: columsToRemove){
            	if (columnName.equalsIgnoreCase(column.getProperty())){
            		pintar = false;
            		break;
            	}
            }
            if (pintar) {
        		html.append(column.getHeaderRenderer().render());
            }
        }

        html.trEnd(1);
        return html.toString();
    }

	public String body(String... columsToRemove) {
        HtmlBuilder html = new HtmlBuilder();

        int rowcount = HtmlUtils.startingRowcount(getCoreContext());

        Collection<?> items = getCoreContext().getPageItems();
        for (Object item : items) {
            rowcount++;

            HtmlRow row = getHtmlTable().getRow();
            List<Column> columns = row.getColumns();

            html.append(row.getRowRenderer().render(item, rowcount));

            for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
                HtmlColumn column = (HtmlColumn) iter.next();
                boolean pintar = true;
                for (String columnName: columsToRemove){
                	if (columnName.equalsIgnoreCase(column.getProperty())){
                		pintar = false;
                		break;
                	}
                }
                if (pintar) {
            		html.append(column.getCellRenderer().render(item, rowcount));
                }
            }

            html.trEnd(1);
        }
        return html.toString();
    }
}

