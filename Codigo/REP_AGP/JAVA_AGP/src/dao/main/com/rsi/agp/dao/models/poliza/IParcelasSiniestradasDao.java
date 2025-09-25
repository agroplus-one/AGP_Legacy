package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestradoDV;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;

@SuppressWarnings("rawtypes")
public interface IParcelasSiniestradasDao extends GenericDao {

	public List<ParcelaSiniestro> listSiniestroParcelas (CapAsegSiniestradoDV capAsegSiniestradoDV) throws DAOException;	
	public List<CapAsegSiniestro> list(Long idCapital) throws DAOException;
	
	public List<CapAsegSiniestro> getCapitalesAseguradosSiniestro(List<Long> idsCapitalesAseguradosSiniestro) throws DAOException;
	public List<ParcelaSiniestro> getParcelasSiniestradas(List<Long> idsCapitalesAseguradosSiniestro) throws DAOException;
	
}
