package com.rsi.agp.dao.models.anexo;

import java.util.Set;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;

public interface ISubvencionesDeclarablesModificacionPolizaDao extends GenericDao {
	public void actualizaSubvencionesAnexo(AnexoModificacion anexo, Set<SubvDeclarada> subvDefinitivas) throws DAOException;
	public void insertamosSubvencionesDeclaradasAnexo(SubvDeclarada subvDeclarada) throws DAOException;
	public boolean getsubvDeclarada(Long idAnexo) throws DAOException;
}
