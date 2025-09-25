package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.poliza.Poliza;

public interface IRiesgosDao extends GenericDao{

	List<BigDecimal> getRiesgosCubModulo(Long lineaseguroid, String codmodulo, Character elegible) throws DAOException;
	List<Riesgo> getRiesgos(List<BigDecimal> listadoRiesgos, String modulo, Long lineaSeguroid, BigDecimal codLinea)throws DAOException;
	List<Object[]> getRiesgosReduccionCapital(Long lineaseguroid)throws DAOException;
	List<BigDecimal> getRiesgosElegidosFiltrados(Poliza poliza)throws DAOException;
	List<Riesgo> getRiesgosConTasables() throws DAOException;
}
