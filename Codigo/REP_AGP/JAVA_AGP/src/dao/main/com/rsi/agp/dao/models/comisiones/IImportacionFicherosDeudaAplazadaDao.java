package com.rsi.agp.dao.models.comisiones;

import javax.servlet.http.HttpServletRequest;

import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Usuario;


@SuppressWarnings("rawtypes")
public interface IImportacionFicherosDeudaAplazadaDao extends GenericDao{

	public Long importarYValidarFicheroDeudaAplazada(XmlObject xmlObject, Usuario usuario,Character tipo, String nombre, HttpServletRequest request) throws DAOException;
	
}