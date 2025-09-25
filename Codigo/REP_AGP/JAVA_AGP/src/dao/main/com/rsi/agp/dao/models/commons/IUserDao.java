package com.rsi.agp.dao.models.commons;

import java.sql.SQLException;
import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.model.user.User;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Medida;

public interface IUserDao extends GenericDao {
	public User login(User userFinal) throws BusinessException, HibernateException, SQLException;
	public String encodeUser(String user) throws BusinessException;
	public String decodeUser(final String user) throws BusinessException;
	public void load(User userFinal) throws BusinessException, HibernateException, SQLException;
	public boolean existeOficina(Oficina oficina) throws DAOException;
	public Medida getMedida(Long lineaseguroid, String nifcif)throws DAOException;
	public HashMap<String, Object> checkPermisosLogin();
	public Usuario getUsuarioExterno (String codUsuario);
	public Usuario getUsuarionNewLogin (String codUsuario);
	public String getNombreUsuario();
	
}
