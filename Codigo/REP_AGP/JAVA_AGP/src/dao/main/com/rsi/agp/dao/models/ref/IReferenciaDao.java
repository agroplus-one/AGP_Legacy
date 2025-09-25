package com.rsi.agp.dao.models.ref;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.ref.ReferenciaAgricola;

@SuppressWarnings("unchecked")
public interface IReferenciaDao extends GenericDao {
	
	/**
	 * Devuelve el siguiente registro de ReferenciaAgricola
	 * @return
	 */
	public ReferenciaAgricola getSiguienteReferencia () throws DAOException;
	public String getUltimaRef();

	/**
	 * Devuelve un boolean indicando si existe alguna referencia entre el rango indicado por refInicial y refFinal
	 * @param refInicial
	 * @param refFinal
	 * @return
	 * @throws DAOException
	 */
	public boolean hayRefRepetidasEnRango (String refInicial, String refFinal) throws DAOException;
}
 