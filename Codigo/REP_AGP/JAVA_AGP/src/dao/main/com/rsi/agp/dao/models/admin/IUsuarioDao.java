package com.rsi.agp.dao.models.admin;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IUsuarioDao extends GenericDao{
	
	/**
	 * Borra el colectivo indicado de todos los usuarios que lo tengan asociado
	 * @param idColectivo
	 */
	void desasociarColectivo (Long idColectivo) throws DAOException;
	
	Usuario getUsuarioPoliza(String codUsuario) throws DAOException;
	
	/**
	 * true -> La oficina tiene usuarios false-> no tiene
	 * 07/05/2014 U029769
	 * @param oficina
	 * @return
	 * @throws DAOException
	 */
	boolean isOficinaConUsuarios (Oficina oficina) throws DAOException;

}
