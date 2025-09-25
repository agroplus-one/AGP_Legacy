package com.rsi.agp.core.webapp.action;

import java.util.List;

import net.sf.jasperreports.engine.JRScriptletException;

public class ColumnVisibilityScriptlet extends net.sf.jasperreports.engine.JRDefaultScriptlet {
    public String isColumnVisible(int index, List<String> columnHeaders) throws JRScriptletException {
        if (columnHeaders != null && columnHeaders.size() > index) {
            return columnHeaders.get(index);
        }
        return "-";
    }
}