package com.rsi.agp.core.jmesa.ui;


import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;

/**
 * @author Jeff Johnston
 */
public class CustomView  extends AbstractHtmlView {
    public Object render() {
        CustomHtmlSnippets snippets = new CustomHtmlSnippets(getTable(), getToolbar(), getCoreContext());

        HtmlBuilder html = new HtmlBuilder();
        
        html.append(snippets.themeStart());

        html.append(snippets.tableStart());

        html.append(snippets.theadStart());

        html.append(snippets.toolbar());

        html.append(snippets.header("creationDateTo", "endDateTo"));

        html.append(snippets.theadEnd());

        html.append(snippets.tbodyStart());

        html.append(snippets.body("creationDateTo", "endDateTo"));
        
        //Resumen de los resultados mostrados
        html.tr(1).close().td(2).colspan("18").align("center").style("font-family: tahoma, verdana, arial;color: #626262;font-size:11px;font-weight:bold;").close();
        html.append(snippets.statusBarText());
        html.tdEnd().trEnd(1);

        html.append(snippets.tbodyEnd());

        html.append(snippets.footer());

        html.append(snippets.tableEnd());
        
        html.append(snippets.themeEnd());

        html.append(snippets.initJavascriptLimit());
        
        html.append("<script>document.getElementById('adviceFilter').innerHTML='';$.ajaxSettings.cache = false;</script>");

        return html.toString();
    }
}
