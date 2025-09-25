package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DatosInformeFilter;
import com.rsi.agp.core.jmesa.sort.DatosInformeSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfDatosInformes;

public interface IMtoDatosInformeDao extends GenericDao {
	
	public List<CampoInforme> getListaCampos(final BigDecimal informeId) throws DAOException;
	public List<DatoInformes> getListaDatoInformes(Long informeId) throws DAOException ;
	public boolean existeDatosInforme(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws  DAOException ;
	public Collection<VistaMtoinfDatosInformes> getDatosInformeWithFilterAndSort(
				final DatosInformeFilter filter, final DatosInformeSort sort,final BigDecimal informeId, final int rowStart,
				final int rowEnd) throws  DAOException ;
	public int getDatosInformeCountWithFilter(final DatosInformeFilter filter,final BigDecimal informeId)  throws  DAOException;
	public int getOrden(VistaMtoinfDatosInformes vistaMtoinfDatosInformes)
	throws  DAOException ;
		
}
