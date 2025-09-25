package com.rsi.agp.dao.models.imp;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cargas.CargasTablas;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;

@SuppressWarnings("rawtypes")
public interface ICargasTablasDao extends GenericDao{

	TablaCondicionado getTabla(String codTabla) throws DAOException;

	List<CargasTablas> getTablasbyId(Long valueOf);

	void deletebyIdFichero(Long idFichero);

	CargasTablas getTablaAmodificar(CargasTablas cargasTablas, Long idFichero);

}
