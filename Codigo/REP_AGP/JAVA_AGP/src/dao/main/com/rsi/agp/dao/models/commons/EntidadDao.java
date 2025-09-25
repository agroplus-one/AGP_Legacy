package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.poliza.OficinaFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinaId;
import com.rsi.agp.dao.tables.commons.OficinasZona;

public class EntidadDao extends BaseDaoHibernate implements IEntidadDao{
	
	/**
	 * Método que devuelve un String con la lista de entidades del grupo CRM
	 * @param 
	 * @return listaEnt
	 */
	public String getEntidadesGrupoCRM() throws DAOException{
		String idGrupoCRM = "";
		String listaEnt = "";
		try{	
			Session session = obtenerSession();
			String sql = "select d.id from tb_grupo_entidades d where d.descripcion like '%CRM%'";
			List list = session.createSQLQuery(sql).list();
			if(list.size()>0)
				idGrupoCRM = list.get(0).toString();
			
			listaEnt = StringUtils.toValoresSeparadosXComas(getListaEntidadesGrupo(idGrupoCRM), false, false);
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return listaEnt;
	}
	
	
	/**
	 * Método que devuelve la lista de entidades del idgrupo que le paso por parametro
	 * @param idGrupo
	 * @return listaEnt
	 */
	@Override
	public List<BigDecimal> getListaEntidadesGrupo(String idGrupo) throws DAOException {
	
		List<BigDecimal> lista = new ArrayList<BigDecimal>();
		try {			
			for (Entidad entidad : (List<Entidad>)getObjects(Entidad.class, "grupoEntidades.id", new BigDecimal(idGrupo))) {
				lista.add(entidad.getCodentidad());
			}
			return lista;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/**
	 * Método que comprueba si una entidad pertenece al grupo CRM
	 * @param codentidad Entidad a comprobar.
	 * @return true en caso de que la entidad pertenezca al grupo CRM
	 */
	public boolean isCRM(BigDecimal codentidad){
		try {		

			BigDecimal resultado;
			Session session = obtenerSession();
			
	 		String sql = "select count(*) from tb_entidades c, tb_grupo_entidades d " +
	 				"where c.idgrupo = d.id and d.descripcion like '%CRM%' and c.codentidad = " + codentidad;
			
	 		List list = session.createSQLQuery(sql).list();
			resultado = (BigDecimal) list.get(0);
			
			if ( resultado.intValue() ==0) 
				return false;
			else
				return true;
		
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Método que comprueba si una entidad pertenece al grupo CR Almendralejo
	 * @param codentidad Entidad a comprobar.
	 * @return true en caso de que la entidad pertenezca al grupo CR Almendralejo
	 */
	public boolean isCRAlmendralejo(BigDecimal codentidad){
		try {		

			BigDecimal resultado;
			Session session = obtenerSession();
			
	 		String sql = "select count(*) from tb_entidades c, tb_grupo_entidades d " +
	 				"where c.idgrupo = d.id and d.descripcion like '%ALMENDRALEJO%' and c.codentidad = " + codentidad;
			
	 		List list = session.createSQLQuery(sql).list();
			resultado = (BigDecimal) list.get(0);
			
			if ( resultado.intValue() ==0) 
				return false;
			else
				return true;
		
		} catch (Exception e) {
			return false;
		}
	}
	
	/**Metodo que devuelve el idGrupo entidad a partir del codigo de entidad del usuario
	 * 
	 * @param codEntidadUsuario
	 * @return
	 * @throws DAOException
	 */
	@Override
	public String getIdGrupoEntidad(BigDecimal codEntidadUsuario) throws DAOException {
		logger.debug("init - getIdGrupoEntidad");
		Session session = obtenerSession();
		List<Entidad> ent = new ArrayList<Entidad>();
		String idGrupo= "";
		try {
			Criteria c = session.createCriteria(Entidad.class);
			c.add(Restrictions.eq("codentidad", codEntidadUsuario));
			ent = c.list();
			if (ent.size()>0)
				idGrupo=ent.get(0).getGrupoEntidades().getId().toString();
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		logger.debug("end - getIdGrupoEntidad");
		return idGrupo;
	}
	
	public boolean existeEntidad (BigDecimal codentidad)throws DAOException {
		logger.debug("init - existeEntidad");
		Session session = obtenerSession();
		Integer count=0;
		try {
			Criteria c = session.createCriteria(Entidad.class);
			c.add(Restrictions.eq("codentidad", codentidad));
			count = (Integer) c.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
	}
	@Override
	public boolean existeEntidadOficina(BigDecimal codentidad,
			BigDecimal codoficina) throws DAOException {
		Integer count=0;
		try {
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(Oficina.class);
			criteria.add(Restrictions.eq("id.codentidad", codentidad));
			criteria.add(Restrictions.eq("id.codoficina", codoficina));
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
	
	@Override
	public Oficina getOficina(BigDecimal codentidad, BigDecimal codoficina) throws DAOException {
		Oficina oficina = null;
		try {			
			
			OficinaId id = new OficinaId(codentidad, codoficina);
			
			oficina = (Oficina) this.get(Oficina.class, id);
			
			return oficina;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en getOficina de EntidadDao", e);
			throw new DAOException("Se ha producido un error en getOficina", e);
		}
	}


	@Override
	public List<Entidad> obtenerListaEntidadesByArrayCodEntidad(List<BigDecimal> listaCodEntidad) throws DAOException {
/*
		Session session = obtenerSession();
		List<Entidad> listaEntidades = null;
		try {
			Query query = session.createQuery("from Entidad ent where ent.codentidad = :listaCodEntidad ");
			query.setParameterList("listaCodEntidad", listaCodEntidad);
			listaEntidades = query.list();
			
			return listaEntidades;
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	*/	
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Entidad.class);
			criteria.add(Restrictions.in("codentidad", listaCodEntidad));
			return criteria.list();
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		} 
		
	}


	@Override
	public String getIdGrupoOficina(BigDecimal codEntidad, BigDecimal codOficina) throws DAOException {
		logger.debug("init - getIdGrupoOficina");
		Session session = obtenerSession();
		List<Oficina> ent = new ArrayList<Oficina>();
		String idGrupo= "";
		try {
			Criteria c = session.createCriteria(Oficina.class);
			c.add(Restrictions.eq("id.codentidad", codEntidad));
			c.add(Restrictions.eq("id.codoficina", codOficina));
			ent = c.list();
			if (ent.size()>0)
				if(null!=ent.get(0).getIdgrupo())
					idGrupo=ent.get(0).getIdgrupo().toString();
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		logger.debug("end - getIdGrupoOficina");
		return idGrupo;
	}
	
	public List<BigDecimal> getListaOficinasGrupo(BigDecimal codzona, BigDecimal codentidad) throws DAOException{
		Session session = obtenerSession();
		
		List<OficinasZona> listOfi = new ArrayList<OficinasZona>();
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		try {	
			if(codzona != null && codentidad != null) {
				
				Criteria c = session.createCriteria(OficinasZona.class);
				c.add(Restrictions.eq("id.codentidad", codentidad));
				c.add(Restrictions.eq("id.codzona", codzona));
				listOfi = c.list();
				
				for (int i = 0; i < listOfi.size(); i++){
					OficinasZona oficZona = (OficinasZona) listOfi.get(i);
					
					BigDecimal codoficina = oficZona.getId().getCodoficina();
					
					list.add(codoficina);
				}
			}
			return list;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	
}