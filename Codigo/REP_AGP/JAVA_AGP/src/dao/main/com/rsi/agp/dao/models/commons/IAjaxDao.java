package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IAjaxDao extends GenericDao {

	public List getData(String objeto, Integer posicion, String[] filtro,
			String[] campoFiltro, String[] tipoFiltro, String[] valorDepende,
			String[] campoBeanDepende, String[] tipoBeanDepende,
			String campoOrden, String tipoOrden, String[] campoListado,
			String[] campoBeanDistinct, String[] campoBeanIsNull,
			String[] campoLeftJoin, String[] campoRestrictions,
			String[] valorRestrictions, String[] OperadorRestrictions,String[] tipoValorRestrictions);
	
	public List getDataAcumularResultados(String objeto, Integer posicion, String[] filtro,
			String[] campoFiltro, String[] tipoFiltro, String[] valorDepende,
			String[] campoBeanDepende, String[] tipoBeanDepende,
			String campoOrden, String tipoOrden, String[] campoListado,
			String[] campoBeanDistinct, String[] campoBeanIsNull,
			String[] campoLeftJoin, String[] campoRestrictions,
			String[] valorRestrictions, String[] OperadorRestrictions,String[] tipoValorRestrictions, String[] valorDependeGenericos);
	
	public int getCountData(String objeto, String[] filtro,
			String[] campoFiltro, String[] tipoFiltro, String[] valorDepende,
			String[] campoBeanDepende, String[] tipoBeanDepende,
			String[] campoListado, String[] campoBeanDistinct,
			String[] campoBeanIsNull,String[] campoLeftJoin, 
			String[] campoRestrictions,String[] valorRestrictions, 
			String[] OperadorRestrictions,String[] tipoValorRestrictions);

	/**
	 * DAA 29/01/2013 Obtiene la lista de tablas que faltan para completar el
	 * proceso de Activacion de la linea
	 * 
	 * @param lineaSeguroId
	 * @return List
	 * @throws DAOException
	 */
	public List getTablasPendientes(BigDecimal lineaSeguroId)
			throws DAOException;

}
