package com.rsi.agp.core.jmesa.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoDescuentosFilter;
import com.rsi.agp.core.jmesa.sort.MtoDescuentosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;


public interface IMtoDescuentosDao extends GenericDao{

	
	Collection<Descuentos> getDescuentosWithFilterAndSort(
		final MtoDescuentosFilter filter, final MtoDescuentosSort sort, final int rowStart,
		final int rowEnd) throws BusinessException;

	int getDescuentosCountWithFilter(final MtoDescuentosFilter filter);

	boolean existeRegistro(BigDecimal entidad, BigDecimal entMed,BigDecimal subMed,
			BigDecimal oficina,BigDecimal delegacion,Long id, BigDecimal codPlan, BigDecimal codLinea) throws Exception;

	ArrayList<DescuentosHistorico> consultaHistorico(Long id)throws Exception;
	public void replicar (BigDecimal origen, BigDecimal destino, String codUsuario, BigDecimal entidadReplica) throws DAOException;
	public void cambioMasivo(String listaIds,Descuentos descuentosBean) throws Exception;
	public void cambioMasivoHistorico(String listaIds,Descuentos descuentosBean, Usuario usuario) throws Exception;
	public String getlistaIdsTodos(MtoDescuentosFilter consultaFilter);
	
}
