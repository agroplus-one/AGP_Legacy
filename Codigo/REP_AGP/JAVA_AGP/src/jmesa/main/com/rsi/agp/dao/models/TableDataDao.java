package com.rsi.agp.dao.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmesa.limit.FilterSet;

import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;

/**
 * @author T-Systems
 */
public interface TableDataDao {
    public List<Object> getTableData(String objeto, HashMap<String, Object> filtros);

    public int getTableDataCountWithFilter(TableDataFilter filter, final FilterSet filtroColumna);

    public List<Object> getTableDataWithFilterAndSort(TableDataFilter filter, FilterSet filtroColumna, TableDataSort sort, int rowStart, int rowEnd);
    
    public Map<String, Object> getTableDataByUniqueIds(String property, List<String> uniqueIds);
}
