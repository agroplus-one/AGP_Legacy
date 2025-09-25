package com.rsi.agp.core.jmesa.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.PagoManualFilter;
import com.rsi.agp.core.jmesa.sort.PagoManualSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Zona;

@SuppressWarnings("rawtypes")
public interface IPagoManualDao extends GenericDao {

	Collection<Oficina> getOficinasPagoManualWithFilterAndSort(final PagoManualFilter filter, final PagoManualSort sort,
			final int rowStart, final int rowEnd, final String codZona) throws BusinessException;

	int getOficinasPagoManualCountWithFilter(final PagoManualFilter filter);

	String getlistaIdsTodos(PagoManualFilter consultaFilter) throws DAOException;

	void cambioMasivo(String listaIds, Oficina oficinaBean) throws DAOException;

	/* Pet. 63701 ** MODIF TAM (25.06.2021) ** Inicio */
	public List<Zona> obtenerListaZonas(BigDecimal codentidad) throws DAOException;

	public void guardarZonasOficina(Oficina oficinaBean, List<Zona> zonaListSel) throws DAOException;

	public List<String> obtenerListaNombZonasOficina(BigDecimal codentidad, BigDecimal codOficina) throws DAOException;

	public void borrarZonasOficinas(Oficina oficinaBean) throws DAOException;

	public void editaZonasOficina(Oficina oficinaBean, List<Zona> zonaListSel) throws DAOException;

	public List<Zona> obtenerListaZonasOficina(BigDecimal codentidad, BigDecimal codoficina) throws DAOException;

	void cambioMasivoZonas(String listaIds, List<Zona> zonaListSel) throws DAOException;

	void adiccionMasivaZonas(String listaIds, List<Zona> zonaListSel) throws DAOException;

}
