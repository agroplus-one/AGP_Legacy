package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmesa.limit.FilterSet;

import com.rsi.agp.core.managers.GenericTableViewerService;
import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;
import com.rsi.agp.dao.models.TableDataDao;

/**
 * @author T-Systems
 */
public class GenericTableViewerServiceImpl implements GenericTableViewerService {

    private TableDataDao tableDataDao;

    public Collection<Object> getTableData(String objeto, HashMap<String, Object> filtros) {
        return tableDataDao.getTableData(objeto, filtros);
    }

    public Collection<Map> getTableDataAsMaps() {
    	List<Map> results = new ArrayList<Map>();
        return results;
    }

    public int getTableDataCountWithFilter(TableDataFilter filter, FilterSet filtroColumna) {
        return tableDataDao.getTableDataCountWithFilter(filter, filtroColumna);
    }

    public Collection<Object> getTableDataWithFilterAndSort(TableDataFilter filter,FilterSet filtroColumna, TableDataSort sort, int rowStart, int rowEnd) {
        return tableDataDao.getTableDataWithFilterAndSort(filter, filtroColumna, sort, rowStart, rowEnd);
    }

    public Map<String, Object> getTableDataByUniqueIds(String property, List<String> uniqueIds) {
        return tableDataDao.getTableDataByUniqueIds(property, uniqueIds);
    }

    public void setTableDataDao(TableDataDao tableDataDao) {
        this.tableDataDao = tableDataDao;
    }
}
