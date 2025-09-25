package com.rsi.agp.dao.models.comisiones;

import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;

@SuppressWarnings("rawtypes")
public interface IRgaUnifComisionesDao extends GenericDao {

	void generaDatosMediadores2015(List<FicheroUnificado> listFasesUnifCierre) throws Exception;

}
