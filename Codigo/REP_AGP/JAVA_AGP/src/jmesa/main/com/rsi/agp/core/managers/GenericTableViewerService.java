package com.rsi.agp.core.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmesa.limit.FilterSet;

import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;

/**
 * @author T-Systems
 */
public interface GenericTableViewerService {

    Collection<Object> getTableData(String objeto, HashMap<String, Object> filtros);

    Collection<Map> getTableDataAsMaps();

    int getTableDataCountWithFilter(TableDataFilter filter, final FilterSet filtroColumna);

    Collection<Object> getTableDataWithFilterAndSort(TableDataFilter filter, FilterSet filtroColumna,TableDataSort sort, int rowStart, int rowEnd);

    Map<String, Object> getTableDataByUniqueIds(String property, List<String> uniqueIds);

}
