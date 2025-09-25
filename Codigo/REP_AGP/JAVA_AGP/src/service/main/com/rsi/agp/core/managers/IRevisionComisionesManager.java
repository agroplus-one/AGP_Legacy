package com.rsi.agp.core.managers;

import java.util.Map;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.commons.Usuario;

/**
 * @author U028982
 */
public interface IRevisionComisionesManager {

	
	public Map<String, String> cambiaParametrosComisiones(Usuario usuario, String comMaxP, String pctadministracionP, 
			String pctEntidadP, String pctESMedP, String pctadquisicionP, String listaIds, String grupoNegocio) throws DAOException;

}