package com.rsi.agp.core.jmesa.service;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoUsuariosFilter;
import com.rsi.agp.core.jmesa.sort.MtoUsuariosSort;
import com.rsi.agp.dao.tables.commons.Usuario;
/**
 * 
 * @author U029769
 *
 */
public interface IMtoUsuariosService {
	
	
	String getTablaUsuarios(HttpServletRequest request,
			HttpServletResponse response, Usuario usuarioBusqueda,
			String origenLlamada);

	Collection<Usuario> getUsuariosWithFilterAndSort(MtoUsuariosFilter filter,
			MtoUsuariosSort sort, int rowStart, int rowEnd)
			throws BusinessException;

	int getUsuariosCountWithFilter(MtoUsuariosFilter filter)
			throws BusinessException;

	Map<String, Object> borraUsuario(Usuario usuarioBean)throws BusinessException;

	Map<String, Object> editaUsuario(Usuario usuarioBean,HttpServletRequest request)throws BusinessException;

	Map<String, Object> altaUsuario(Usuario usuarioBean)throws BusinessException;

	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, Usuario usuarioBean) throws DAOException;
	
	public Map<String, String> incrementarFecha(String listaIdsMarcados_ifecha, Usuario usuarioBean) throws DAOException;
	
	public String getlistaIdsTodos(MtoUsuariosFilter consultaFilter);
	
	public Usuario getCambioMasivoBeanFromLimit(Limit consultaUsuarios_LIMIT);
}
