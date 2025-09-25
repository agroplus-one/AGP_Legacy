package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.gan.DatosRCFilter;
import com.rsi.agp.core.jmesa.sort.DatosRCSort;
import com.rsi.agp.dao.tables.rc.DatosRC;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;
import com.rsi.agp.dao.tables.rc.SumaAseguradaRC;

public interface IDatosRCService {

	public Collection<EspeciesRC> getEspeciesRC() throws BusinessException;

	public Collection<RegimenRC> getRegimenesRC() throws BusinessException;

	public Collection<SumaAseguradaRC> getSumasAseguradasRC()
			throws BusinessException;

	public String getTablaDatosRC(final HttpServletRequest request,
			final HttpServletResponse response, final DatosRC datosRC,
			final String origenLlamada) throws BusinessException;

	public Collection<DatosRC> getDatosRCWithFilterAndSort(
			final DatosRCFilter filter, final DatosRCSort sort,
			final int rowStart, final int rowEnd) throws BusinessException;

	public int getDatosRCCountWithFilter(final DatosRCFilter filter)
			throws BusinessException;

	public String validateDatosRC(final DatosRC datosRC)
			throws BusinessException;

	public DatosRC grabarDatosRC(final DatosRC datosRC)
			throws BusinessException;

	public DatosRC modificarDatosRC(final DatosRC lineasRC)
			throws BusinessException;

	public void borrarDatosRC(final Long idDatosRC) throws BusinessException;

	public String validateReplicaDatosRC(final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException;

	public void replicaDatosRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException;
	
	public void cambioMasivoDatosRC(final String[] idsMarcadosStrArr,
			final BigDecimal tasaCM, final BigDecimal franquiciaCM,
			final BigDecimal primaMinimaCM) throws BusinessException;
}