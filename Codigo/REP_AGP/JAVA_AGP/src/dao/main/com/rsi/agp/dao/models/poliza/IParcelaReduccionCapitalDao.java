package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Comarca;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado;
import com.rsi.agp.dao.tables.reduccionCap.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

public interface IParcelaReduccionCapitalDao extends GenericDao {

	public List<CapitalAsegurado> listReduccionCapitalParcelas (CapitalAsegurado capitalAsegurado) throws DAOException;	
	public List<CapitalAsegurado> list(Long idCapitalAsegurado) throws DAOException;
	public void saveCapitalesAsegurados(List <CapitalAsegurado> listaCapitalesAsegurados) throws DAOException;
	public void saveReduccionCapital(ReduccionCapital reduccionCapital) throws DAOException;
	public ReduccionCapital getReduccionCapitalById (Long id)throws DAOException;
}
