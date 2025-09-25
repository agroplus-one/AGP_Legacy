package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Tomador;

@SuppressWarnings("unchecked")
public interface ITomadorDao extends GenericDao {
	List<Tomador> getTomadoresGrupoEntidad(Tomador tomadorBean, List<BigDecimal> listaEnt) throws DAOException;
}
