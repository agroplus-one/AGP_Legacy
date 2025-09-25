package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IDatoVariableDao extends GenericDao {
	
	/** DAA 10/09/2013
	 *  Devuelve una lista de codConceptos de Datos variables de las parcelas de la poliza
	 * @param idpoliza
	 * @return
	 */
	public List<BigDecimal> getDatosVariableParcelas(Long idPoliza) throws DAOException;
	
	public String getDescripcionDatoVariable (Long lineaseguroid, BigDecimal codconcepto, String codigo) throws DAOException;
	
	public String getDescDatoVariableGanado (BigDecimal idgruporaza, String idexplotacion) throws DAOException;
}
