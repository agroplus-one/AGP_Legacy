package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinaId;
import com.rsi.agp.dao.tables.commons.Usuario;

public class UsuarioDao extends BaseDaoHibernate implements IUsuarioDao {

	@Override
	public void desasociarColectivo(Long idColectivo) throws DAOException {
		
		try {
			Session session = obtenerSession();		
			session.createSQLQuery ("UPDATE TB_USUARIOS SET IDCOLECTIVO=NULL WHERE IDCOLECTIVO=" + idColectivo).executeUpdate();
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al borrar el colectivo con id=" + idColectivo + " de los usuarios", e);
			throw new DAOException("Se ha producido un error al borrar el colectivo con id=" + idColectivo + " de los usuarios", e);
		}

	}
	@SuppressWarnings("unchecked")
	/**
	 * Obtiene el usuario de una poliza
	 * @author U029769 19/07/2013
	 * @param idpoliza
	 * @return Usuario
	 * @throws DAOException
	 */
	public Usuario getUsuarioPoliza(String codUsuario) throws DAOException {
		
		List<Object> list = new ArrayList<Object>();
		Usuario usuario = new Usuario();
		try{
			Session session = obtenerSession();
			
			list =session.createSQLQuery("select codentidad,codoficina,nombreusu from tb_usuarios u where  " +
												" u.codusuario = '" + codUsuario +"'").list();
		if (list.size()>0){
			Object[] o = (Object[]) list.get(0);
			
			usuario.setOficina(new Oficina());
			usuario.getOficina().setId(new OficinaId());
			usuario.getOficina().getId().setCodentidad(new BigDecimal(o[0].toString()));
			usuario.getOficina().getId().setCodoficina(new BigDecimal(o[1].toString()));
			usuario.setNombreusu(o[2].toString());
		}
		return usuario;
		}catch (Exception e) {
			logger.error("Se ha producido un error al obtener los datos del usuario de una poliza", e);
			throw new DAOException("Se ha producido un error al obtener los datos del usuario de una poliza", e);
		}
	}
	
	public boolean isOficinaConUsuarios (Oficina oficina) throws DAOException{
		
		Integer count=0;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Usuario.class);
			criteria.add(Restrictions.eq("oficina.id.codentidad", oficina.getId().getCodentidad()));
			criteria.add(Restrictions.eq("oficina.id.codoficina", oficina.getId().getCodoficina()));
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en isOficinaConUsuarios", e);
			throw new DAOException("Se ha producido un error en isOficinaConUsuarios", e);
		}
	}

}
