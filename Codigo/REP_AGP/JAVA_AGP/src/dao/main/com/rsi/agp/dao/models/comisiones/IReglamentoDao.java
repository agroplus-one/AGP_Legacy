package com.rsi.agp.dao.models.comisiones;

import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ReglamentoFilter;
import com.rsi.agp.core.jmesa.filter.ReglamentoSitFilter;
import com.rsi.agp.core.jmesa.sort.ReglamentoSitSort;
import com.rsi.agp.core.jmesa.sort.ReglamentoSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.comisiones.Reglamento;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;

public interface IReglamentoDao extends GenericDao{

	public List<Reglamento> listReglamentos(Reglamento reglamentoBean) throws DAOException;
	public Entidad getEntidad(Entidad entidad) throws DAOException;
	public Integer existeRegistro(Reglamento reglamentoBean) throws DAOException;
	public boolean existePlan(String planDestino) throws DAOException;
	
	int getFicheroReglamentoCountWithFilter(ReglamentoFilter filter,
			ReglamentoProduccionEmitida reglamentoProduccionEmitida);
	
	Collection<ReglamentoProduccionEmitida> getFicheroReglamentoWithFilterAndSort(
			ReglamentoFilter filter, ReglamentoSort sort, int rowStart,
			int rowEnd, ReglamentoProduccionEmitida reglamentoProduccionEmitida)
			throws BusinessException;
	
	public int getFicheroReglamentoSitCountWithFilter(ReglamentoSitFilter filter,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit);
	
	public Collection<ReglamentoProduccionEmitidaSituacion> getFicheroReglamentoSitWithFilterAndSort(
			ReglamentoSitFilter filter, ReglamentoSitSort sort, int rowStart,
			int rowEnd,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) throws BusinessException;

}
