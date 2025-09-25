package com.rsi.agp.dao.models.ref;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.ColectivoReferencia;

@SuppressWarnings("unchecked")
public interface IReferenciaColectivoDao extends GenericDao {
	
	/**
	 * Devuelve el siguiente registro de ColectivoReferencia
	 * @return
	 */
	public ColectivoReferencia getSiguienteReferenciaColectivo () throws DAOException;
	public String getUltimaRef();

}
