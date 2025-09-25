package com.rsi.agp.dao.models.cpl;

import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModuloId;

@SuppressWarnings("rawtypes")
public interface IConceptoCubiertoModuloDao extends GenericDao {

	public List<ConceptoCubiertoModulo> getConceptosCubiertosModulo(ConceptoCubiertoModuloId id);
}
