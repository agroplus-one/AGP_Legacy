package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoDescuentosDao;
import com.rsi.agp.core.jmesa.filter.MtoDescuentosFilter;
import com.rsi.agp.core.jmesa.sort.MtoDescuentosSort;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;

public class MtoDescuentosDao extends BaseDaoHibernate implements IMtoDescuentosDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Descuentos> getDescuentosWithFilterAndSort(
			final MtoDescuentosFilter filter, final MtoDescuentosSort sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [MtoDescuentosDao] getDescuentosWithFilterAndSort");
			List<Descuentos> applications = (List<Descuentos>) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(Descuentos.class);
							criteria.createAlias("subentidadMediadora", "subentidadMediadora");
							criteria.createAlias("subentidadMediadora.entidad", "entidad");
							criteria.createAlias("subentidadMediadora.id", "id");
							criteria.createAlias("linea", "linea");
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<Usuario> lista = criteria.list();
							return lista;
						}
					});
			logger.debug("end - [MtoDescuentosDao] getDescuentosWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	

	@Override
	public int getDescuentosCountWithFilter(final MtoDescuentosFilter filter) {
		logger.debug("init - [MtoDescuentosDao] getDescuentosCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(Descuentos.class);
						criteria.createAlias("subentidadMediadora", "subentidadMediadora");
						criteria.createAlias("subentidadMediadora.entidad", "entidad");
						criteria.createAlias("subentidadMediadora.id", "id");
						criteria.createAlias("linea", "linea");
						criteria = filter.execute(criteria);
						
						return criteria.setProjection(Projections.rowCount()).uniqueResult();
					}
				});
		logger.debug("end - [MtoDescuentosDao] getDescuentosCountWithFilter");
		return count.intValue();
	}


	@Override
	public boolean existeRegistro(BigDecimal codentidad, BigDecimal entMed,BigDecimal subMed,
			BigDecimal codoficina,BigDecimal delegacion,Long id, BigDecimal codplan, BigDecimal codlinea) throws Exception{
		Integer count=0;
		try {
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(Descuentos.class);
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("subentidadMediadora.entidad", "entidad");
			criteria.createAlias("subentidadMediadora.id", "id");
			criteria.createAlias("linea", "linea");
			// si el id viene a null es un alta y si viene relleno estamos editando
			// y comprobamos que el registro que vamos a editar no exista sin
			// tener en cuenta el id que le estamos pasando
			if (id!=null) {
				criteria.add(Restrictions.ne("id", id));
			}
			criteria.add(Restrictions.eq("subentidadMediadora.entidad.codentidad", codentidad));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", entMed));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", subMed));
			criteria.add(Restrictions.eq("oficina.id.codoficina", codoficina));
			criteria.add(Restrictions.eq("linea.codlinea",codlinea));
			criteria.add(Restrictions.eq("linea.codplan",codplan));
			 
			if (delegacion != null)
				criteria.add(Restrictions.eq("delegacion", delegacion));
			else
				criteria.add(Restrictions.isNull("delegacion"));
			criteria.add(Restrictions.isNull("fechaBaja"));
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en existeRegistro", e);
			throw new DAOException("Se ha producido un error en existeRegistro", e);
		}
	}


	@Override
	public ArrayList<DescuentosHistorico> consultaHistorico(Long id)
			throws Exception {
		logger.debug("init - consultaHistorico");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(DescuentosHistorico.class);
			criteria.createAlias("descuentos", "descuentos");
			criteria.addOrder(Order.asc("fecha"));				
			
			criteria.add(Restrictions.eq("descuentos.id",  id));
			
			return (ArrayList<DescuentosHistorico>) criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public void replicar(BigDecimal origen, BigDecimal destino, String codUsuario, BigDecimal entidadReplica) throws DAOException {
		
		try {
			// Procedimiento de réplica
			String procedimiento = "PQ_REPLICAR.replicarDescuentos (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, CODENT_REPLICA IN NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("LINEASEGUROID_DESTINO", destino);
			parametros.put("LINEASEGUROID_ORIGEN", origen);
			parametros.put("CODENT_REPLICA",entidadReplica);
		
			databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			
			//Procedimiento de guaradado de réplica en Histórico
			procedimiento="PQ_REPLICAR.replicarDescuentosHistorico (LINEASEGUROID_DESTINO IN NUMBER, USUARIO IN VARCHAR2, CODENT_REPLICA IN NUMBER)";
			parametros.clear();
			parametros.put("LINEASEGUROID_DESTINO", destino);
			parametros.put("USUARIO", codUsuario);
			parametros.put("CODENT_REPLICA",entidadReplica);
			
			databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			
		} catch (Exception e) {
			logger.error("Error al replicar descuentos",e);
			throw new DAOException("Error al replicar descuentos", e);
		}        
		
	}
	
	@Override
	public void cambioMasivo(String listaIds,Descuentos descuentosBean) throws Exception{
		try {
			Session session = obtenerSession();
			
			String[] ids = listaIds.split(",");
			boolean campoAnterior = false;
		
			List lstDescuentos = Arrays.asList(ids);
			
			StringBuilder sql = new StringBuilder();
			sql.append("update TB_COMS_DESCUENTOS set ");
			if(null!=descuentosBean.getPctDescMax()) {
				sql.append(" PCT_DESC_MAX = ").append(descuentosBean.getPctDescMax().toString());
				campoAnterior = true;
			}
			if(null!= descuentosBean.getPermitirRecargo()) {
				if(campoAnterior)sql.append(",");
				sql.append(" PERMITIR_RECARGO = ").append(descuentosBean.getPermitirRecargo().toString());
				campoAnterior = true;
			}
			if(null!= descuentosBean.getVerComisiones()) {
				if(campoAnterior)sql.append(",");
				sql.append(" VER_COMISIONES = ").append(descuentosBean.getVerComisiones().toString());
			}
			
			sql.append(" where ");
			StringBuilder strWhere = CriteriaUtils.splitSql("ID",lstDescuentos);
			sql.append(strWhere.toString());
			session.createSQLQuery(sql.toString()).executeUpdate();
			
		}catch (Exception e ) {
			logger.error("Error en cambioMasivo: " +e);
			throw e;
		}
	}
	
	@Override
	public void cambioMasivoHistorico(String listaIds,Descuentos descuentosBean, Usuario usuario) throws Exception{
		try {
			Session session = obtenerSession();
			
			String[] ids = listaIds.split(",");
			
		
			List lstDescuentos = Arrays.asList(ids);
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO TB_COMS_DESCUENTOS_HIST ");
			sql.append("SELECT sq_coms_descuentos_hist.nextval, id, codent, codentmed, codsubentmed, codoficina, delegacion, pct_desc_max, ");     
			sql.append("1, sysdate, '");
			sql.append(usuario.getCodusuario());
			sql.append("', permitir_recargo, ver_comisiones, lineaseguroid from TB_COMS_DESCUENTOS ");   
			sql.append("WHERE ");
			StringBuilder strWhere = CriteriaUtils.splitSql("ID",lstDescuentos);			
			sql.append(strWhere.toString());			
			session.createSQLQuery(sql.toString()).executeUpdate();
			
		}catch (Exception e ) {
			logger.error("Error en cambioMasivoHistorico: " +e);
			throw e;
		}
	}
	
	public String getlistaIdsTodos(MtoDescuentosFilter consultaFilter) {
		String listaids="";
		Session session = obtenerSession();
		/*String sql = "SELECT D.ID FROM TB_COMS_DESCUENTOS D , TB_SUBENTIDADES_MEDIADORAS E, TB_OFICINAS O, TB_LINEAS L " + consultaFilter.getSqlWhere();*/
		String sql = "SELECT D.ID FROM TB_COMS_DESCUENTOS D , TB_SUBENTIDADES_MEDIADORAS S, TB_ENTIDADES E, TB_LINEAS L " + consultaFilter.getSqlWhere();
		 
		List lista = session.createSQLQuery(sql).list();
		
		for(int i=0;i<lista.size();i++){
			listaids += lista.get(i)+",";
		}
		logger.debug("MtoDescuentosDao getlistaIdsTodos: " + sql);
		return listaids;
	}
	
}
