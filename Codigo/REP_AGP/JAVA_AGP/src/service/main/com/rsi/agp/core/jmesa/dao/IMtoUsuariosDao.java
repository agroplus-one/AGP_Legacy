package com.rsi.agp.core.jmesa.dao;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoUsuariosFilter;
import com.rsi.agp.core.jmesa.sort.MtoUsuariosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Usuario;


public interface IMtoUsuariosDao extends GenericDao{

	Collection<Usuario> getUsuariosWithFilterAndSort(final MtoUsuariosFilter filter,
			final MtoUsuariosSort sort, final int rowStart, final int rowEnd)throws BusinessException;

	int getUsuariosCountWithFilter(final MtoUsuariosFilter filter);

	boolean exiteEntidadMediadora(BigDecimal codentidad)throws DAOException;

	boolean esUsuarioConPolizas(String codusuario)throws DAOException;

	boolean esUsuarioConAsegurados(String codusuario)throws DAOException;

	boolean existeUsuario(Usuario usuarioBean)throws DAOException;
	
	public String getlistaIdsTodos(MtoUsuariosFilter consultaFilter);
	
	public void cambioMasivo(String listaIds,Usuario usuarioBean) throws Exception;

	public void incrementarFecha(String listaIds,Usuario usuarioBean) throws Exception;
	
}
