package com.rsi.agp.dao.models.familias;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.familias.Familia;
import com.rsi.agp.dao.tables.familias.GrupoFamilia;
import com.rsi.agp.dao.tables.familias.LineaFamilia;
import com.rsi.agp.dao.tables.familias.LineaFamiliaId;

@SuppressWarnings("rawtypes")
public interface IMtoFamiliasDao extends GenericDao {

	public List<LineaFamilia> listLineaGrupoNegocios(LineaFamilia familia) throws DAOException;
	
	List<GruposNegocio> getGruposNegocio() throws DAOException;
	
	public LineaFamilia getLineaFamilia(LineaFamiliaId lineaFamiliaId);

	public Familia getFamilia(Long codFamilia);

	public List<GrupoFamilia> getGrupos() throws DAOException;

	public void updateLineaFamilia(LineaFamilia familiaInicial, LineaFamilia familiaModificada) throws DAOException;

	public void alta(LineaFamilia lineaFamilia)  throws DAOException;
	
}
