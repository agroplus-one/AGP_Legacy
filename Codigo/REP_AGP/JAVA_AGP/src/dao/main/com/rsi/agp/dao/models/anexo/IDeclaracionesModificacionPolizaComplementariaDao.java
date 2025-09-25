package com.rsi.agp.dao.models.anexo;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;

@SuppressWarnings("rawtypes")
public interface IDeclaracionesModificacionPolizaComplementariaDao extends GenericDao{

	public List<CapitalAsegurado> getCapitalesAsegPolCpl(CapitalAsegurado capitalAseguradoBean) throws DAOException;
	
	/* Pet. 78691 ** MODIF TAM (22/12/2021) */
	public String getdescSistCultivo(String sistCult) throws DAOException;

}
