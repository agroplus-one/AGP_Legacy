package com.rsi.agp.dao.models.comisiones;

import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public interface IImportacionFicherosEmitidosDao extends GenericDao {

	public Long importarYValidarFicheroEmitidos(XmlObject xmlObject, Usuario usuario, Character tipo, String nombre,
			Boolean saveXML) throws DAOException;

	public void actualizarInformesFicheroEmitidos(Long id, int plan) throws DAOException;

	boolean existeFicheroCargado(final String nomFichero, final Character tipofichero);
}