package com.rsi.agp.core.jmesa.ui;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.jmesa.limit.Filter;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Column;
import org.jmesa.view.editor.AbstractFilterEditor;
import org.jmesa.view.html.HtmlBuilder;


/**
 * @author ASF
 */
public class SelectFilterEditor extends AbstractFilterEditor {
	
	public SelectFilterEditor(){
		super();
	}

    @SuppressWarnings("rawtypes")
	public Object getValue() {
        Collection options = new HashSet();

        Limit limit = getCoreContext().getLimit();
        Column column = getColumn();

        Filter filter = limit.getFilterSet().getFilter(column.getProperty());
        String selected = getWebContext().getParameter(limit.getId() + "_f_" + column.getProperty());
        if (filter != null) {
            selected = filter.getValue();
        }

        // build the select box
        
        HtmlBuilder html = new HtmlBuilder();

        html.select().name(column.getProperty()).onchange(
                "jQuery.jmesa.addFilterToLimit('" + limit.getId() + "','" + column.getProperty() + "', this.options[this.selectedIndex].value);onInvokeAction('"
                        + limit.getId() + "', 'filter')").close();
        
        html.option().close().optionEnd();
        
        for (Iterator iterator = options.iterator(); iterator.hasNext();) {
            String proc = (String) iterator.next();
            html.option().value(proc);
            if (selected != null && selected.equals(proc)) {
                html.selected();
            }
            html.close();
            html.append(proc).optionEnd();
        }

        html.selectEnd();

        return html.toString();
    }
}
