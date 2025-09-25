package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.DatosRCFilter;
import com.rsi.agp.core.jmesa.sort.DatosRCSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.rc.DatosRC;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;
import com.rsi.agp.dao.tables.rc.SumaAseguradaRC;

@SuppressWarnings("rawtypes")
public interface IDatosRCDao extends GenericDao {

	public Collection<EspeciesRC> getEspeciesRC() throws DAOException;

	public Collection<RegimenRC> getRegimenesRC() throws DAOException;

	public Collection<SumaAseguradaRC> getSumasAseguradasRC()
			throws DAOException;

	public String getlistaIdsTodos(final DatosRCFilter consultaFilter)
			throws DAOException;

	public int getDatosRCCountWithFilter(final DatosRCFilter filter)
			throws DAOException;

	public Collection<DatosRC> getDatosRCWithFilterAndSort(
			final DatosRCFilter filter, final DatosRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException;

	public void replicaDatosRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws DAOException;
	
	public Collection<DatosRC> getDatosRC(final BigDecimal plan,
			final BigDecimal linea, final BigDecimal codentidad,
			final BigDecimal codsubentidad, final String codespecieRC,
			final BigDecimal codregimenRC) throws DAOException;
	
	public Boolean existeDatosRC(final DatosRC datosRC) throws DAOException;
}