package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.config.TablaCondicionadoTiposCamposLimitesFiltro;
import com.rsi.agp.dao.models.admin.ITablaCondicionadoDao;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;

public class TablaCondicionadoManager implements IManager {

	private static final Log LOGGER = LogFactory.getLog(TablaCondicionadoManager.class);
	protected ITablaCondicionadoDao tablaCondicionadoDao;

	@SuppressWarnings("unchecked")
	public final List<TablaCondicionado> listTiposCamposLimites() {
		final TablaCondicionadoTiposCamposLimitesFiltro filter = new TablaCondicionadoTiposCamposLimitesFiltro();
		return tablaCondicionadoDao.getObjects(filter);
	}

	public void setTablaCondicionadoDao(ITablaCondicionadoDao tablaCondicionadoDao) {
		this.tablaCondicionadoDao = tablaCondicionadoDao;
	}
	
	
}
