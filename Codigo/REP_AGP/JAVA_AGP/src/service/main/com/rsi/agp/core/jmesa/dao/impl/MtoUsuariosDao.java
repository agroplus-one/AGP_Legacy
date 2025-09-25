package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoUsuariosDao;
import com.rsi.agp.core.jmesa.filter.MtoUsuariosFilter;
import com.rsi.agp.core.jmesa.sort.MtoUsuariosSort;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

/**
 * @author U029769
 *
 */
public class MtoUsuariosDao extends BaseDaoHibernate implements IMtoUsuariosDao {
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Usuario> getUsuariosWithFilterAndSort(
			final MtoUsuariosFilter filter, final MtoUsuariosSort sort, final int rowStart,
			final int rowEnd) throws BusinessException{
		try {
			logger.debug("init - [MtoUsuariosDao] getUsuariosWithFilterAndSort");
			List<Usuario> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(Usuario.class);
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
			logger.debug("end - [MtoUsuariosDao] getUsuariosWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getUsuariosCountWithFilter(final MtoUsuariosFilter filter) {
		logger.debug("init - [MtoUsuariosDao] getUsuariosCountWithFilter");
		
		logger.debug("SQLWHERE: " + filter.getSqlWhere());
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(Usuario.class);
						criteria = filter.execute(criteria);
						
						return criteria.setProjection(Projections.rowCount()).uniqueResult();
					}
				});
		logger.debug("end - [MtoUsuariosDao] getUsuariosCountWithFilter");
		return count.intValue();
	}

	@Override
	public boolean exiteEntidadMediadora(BigDecimal codentidad) throws DAOException {
		Integer count=0;
		try {
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(EntidadMediadora.class);
			criteria.add(Restrictions.eq("codentidad", codentidad));
			
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en exiteEntidadMediadora", e);
			throw new DAOException("Se ha producido un error en exiteEntidadMediadora", e);
		}
	}

	@Override
	public boolean esUsuarioConPolizas(String codusuario) throws DAOException {
		Integer count=0;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.add(Restrictions.eq("usuario.codusuario", codusuario));
			
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en esUsuarioConPolizas", e);
			throw new DAOException("Se ha producido un error en esUsuarioConPolizas", e);
		}
	}

	@Override
	public boolean esUsuarioConAsegurados(String codusuario)
			throws DAOException {
		Integer count=0;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Asegurado.class);
			criteria.add(Restrictions.eq("usuario.codusuario", codusuario));
			
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en esUsuarioConAsegurados", e);
			throw new DAOException("Se ha producido un error en esUsuarioConAsegurados", e);
		}
	}

	@Override
	public boolean existeUsuario(Usuario usuarioBean) throws DAOException {
		Integer count=0;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Usuario.class);
			//criteria.createAlias("oficina", "oficina");
			//criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			if (!StringUtils.nullToString(usuarioBean.getCodusuario()).equals("")) {
				criteria.add(Restrictions.eq("codusuario", usuarioBean.getCodusuario()));
			}/*else if (!StringUtils.nullToString(usuarioBean.getTipousuario()).equals("")) {
				criteria.add(Restrictions.eq("tipousuario", usuarioBean.getTipousuario()));
			}else if (!StringUtils.nullToString(usuarioBean.getOficina().getId().getCodentidad()).equals("")) {
				criteria.add(Restrictions.eq("oficina.id.codentidad", usuarioBean.getOficina().getId().getCodentidad()));
			}else if (!StringUtils.nullToString(usuarioBean.getOficina().getId().getCodoficina()).equals("")) {
				criteria.add(Restrictions.eq("oficina.id.codoficina", usuarioBean.getOficina().getId().getCodoficina()));
			}else if (!StringUtils.nullToString(usuarioBean.getSubentidadMediadora().getId().getCodentidad()).equals("")) {
				criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", usuarioBean.getSubentidadMediadora().getId().getCodentidad()));
			}else if (!StringUtils.nullToString(usuarioBean.getSubentidadMediadora().getId().getCodsubentidad()).equals("")) {
				criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", usuarioBean.getSubentidadMediadora().getId().getCodsubentidad()));
			}else if (!StringUtils.nullToString(usuarioBean.getDelegacion()).equals("")) {
				criteria.add(Restrictions.eq("delegacion", usuarioBean.getDelegacion()));
			}*/
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en esUsuarioConAsegurados", e);
			throw new DAOException("Se ha producido un error en esUsuarioConAsegurados", e);
		}
	}

	/** Método para recuperar un String con todos los Ids de usurios segun el filtro
	 * 
	 * @param claseDetalleBusqueda
	 * @return listaids
	 */
	public String getlistaIdsTodos(MtoUsuariosFilter consultaFilter) {
		String listaids="";
		Session session = obtenerSession();
		String sql = "SELECT U.CODUSUARIO FROM TB_USUARIOS U " +consultaFilter.getSqlWhere();
		List lista = session.createSQLQuery(sql).list();
		
		for(int i=0;i<lista.size();i++){
			listaids += lista.get(i)+",";
		}
		return listaids;
	}
	
	@Override
	public void cambioMasivo(String listaIds,Usuario usuarioBean) throws Exception{
		
		try {
		Session session = obtenerSession();
	
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			boolean entro = false;
		
			List lstUsuarios = Arrays.asList(ids);
			// Update para usuarios Externos -> update de todos los campos
			StringBuilder sql = new StringBuilder();
			sql.append("update tb_usuarios set ");
			if (usuarioBean.getTipousuario() !=null){
				if (entro)
					sql.append(",");
				sql.append(" tipousuario = ").append(usuarioBean.getTipousuario().toString());
				entro = true;
			}
			if (usuarioBean.getOficina().getId().getCodentidad() !=null){
				if (entro)
					sql.append(",");
				sql.append(" codentidad = ").append(usuarioBean.getOficina().getId().getCodentidad());
				entro = true;
			}
			if (usuarioBean.getOficina().getId().getCodoficina() !=null){
				if (entro)
					sql.append(",");
				sql.append(" codoficina = ").append(usuarioBean.getOficina().getId().getCodoficina());
				entro = true;
			}
			if (usuarioBean.getSubentidadMediadora().getId().getCodentidad() !=null){
				if (entro)
					sql.append(",");
				sql.append(" entmediadora = ").append(usuarioBean.getSubentidadMediadora().getId().getCodentidad());
				entro = true;
			}
			if (usuarioBean.getSubentidadMediadora().getId().getCodsubentidad() !=null){
				if (entro)
					sql.append(",");
				sql.append(" subentmediadora = ").append(usuarioBean.getSubentidadMediadora().getId().getCodsubentidad());
				entro = true;
			}
			if (usuarioBean.getDelegacion() !=null){
				if (entro)
					sql.append(",");
				sql.append(" delegacion = ").append(usuarioBean.getDelegacion().toString());
				entro = true;
			}
			if (usuarioBean.getCargaPac() !=null){
				if (entro)
					sql.append(",");
				sql.append(" CARGA_PAC = ").append(usuarioBean.getCargaPac().toString());
				entro = true;
			}
			if (usuarioBean.getFinanciar() != null) {
				if (entro)
					sql.append(",");
				sql.append(" FINANCIAR = ").append(usuarioBean.getFinanciar().toString());
				entro = true;
			}
			if (usuarioBean.getImpMinFinanciacion() != null) {
				if(entro)
					sql.append(",");
				sql.append(" IMP_MIN_FINANCIACION = ").append(usuarioBean.getImpMinFinanciacion().toString());
				entro = true;
			}
			if (usuarioBean.getImpMaxFinanciacion() != null) {
				if(entro)
					sql.append(",");
				sql.append(" IMP_MAX_FINANCIACION = ").append(usuarioBean.getImpMaxFinanciacion().toString());
				entro = true;
			}
			if (usuarioBean.getFechaLimite() != null) {
				if(entro)
					sql.append(",");
				sql.append(" FECHA_LIMITE = ").append("TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(usuarioBean.getFechaLimite()) + "', 'dd/MM/yyyy')");				
				entro = true;
			}
			
			
			sql.append(" where ");
			StringBuilder strWhere = CriteriaUtils.splitSql("codusuario",lstUsuarios);
		
			sql.append(strWhere.toString());
			sql.append(" and externo = 1 ");
			//logger.debug("update Externos: "+ sql.toString());
			session.createSQLQuery(sql.toString()).executeUpdate();
			
			// Update para usuarios Internos -> solo update de E-S mediadora y delegación
			if (usuarioBean.getSubentidadMediadora().getId().getCodentidad() !=null || usuarioBean.getSubentidadMediadora().getId().getCodsubentidad() !=null ||
				usuarioBean.getDelegacion() !=null || usuarioBean.getCargaPac() !=null || usuarioBean.getFinanciar() != null || 
				usuarioBean.getImpMinFinanciacion() != null || usuarioBean.getImpMaxFinanciacion() != null || usuarioBean.getFechaLimite() != null) {	
				entro = false;
				StringBuilder sqlInterno = new StringBuilder();
				sqlInterno.append("update tb_usuarios set ");
				if (usuarioBean.getSubentidadMediadora().getId().getCodentidad() !=null){
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" entmediadora = ").append(usuarioBean.getSubentidadMediadora().getId().getCodentidad());
					entro = true;
				}
				if (usuarioBean.getSubentidadMediadora().getId().getCodsubentidad() !=null){
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" subentmediadora = ").append(usuarioBean.getSubentidadMediadora().getId().getCodsubentidad());
					entro = true;
				}
				if (usuarioBean.getDelegacion() !=null){
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" delegacion = ").append(usuarioBean.getDelegacion().toString());
					entro = true;
				}
				if (usuarioBean.getCargaPac() !=null){
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" CARGA_PAC = ").append(usuarioBean.getCargaPac().toString());
					entro = true;
				}
				if (usuarioBean.getFinanciar() != null) {
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" FINANCIAR = ").append(usuarioBean.getFinanciar().toString());
					entro = true;
				}
				if (usuarioBean.getImpMinFinanciacion() != null) {
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" IMP_MIN_FINANCIACION = ").append(usuarioBean.getImpMinFinanciacion().toString());
					entro = true;
				}
				if (usuarioBean.getImpMaxFinanciacion() != null) {
					if (entro)
						sqlInterno.append(",");
					sqlInterno.append(" IMP_MAX_FINANCIACION = ").append(usuarioBean.getImpMaxFinanciacion().toString());
					entro = true;
				}
				if (usuarioBean.getFechaLimite() != null) {
					if(entro)
						sqlInterno.append(",");					
					sqlInterno.append(" FECHA_LIMITE = ").append("TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(usuarioBean.getFechaLimite()) + "', 'dd/MM/yyyy')");
					entro = true;
				}
				sqlInterno.append(" where ");
				sqlInterno.append(strWhere.toString());
 				sqlInterno.append(" and externo = 0 ");
				//logger.debug("update Internos: "+ sqlInterno.toString());
				session.createSQLQuery(sqlInterno.toString()).executeUpdate();
			}
		}catch (Exception e ) {
			logger.error("Error en cambioMasivo: " +e);
			throw e;
		}
	}
	
	@Override
	public void incrementarFecha(String listaIds, Usuario usuarioBean) throws Exception {
		
		try {
				Session session = obtenerSession();
				String[] ids = listaIds.split(",");
				List lstUsuarios = Arrays.asList(ids);
				StringBuilder sql = new StringBuilder();
				sql.append("update tb_usuarios set fecha_limite = add_months(fecha_limite, 12) where ");
				StringBuilder strWhere = CriteriaUtils.splitSql("codusuario",lstUsuarios);			
				sql.append(strWhere.toString());
				sql.append("and fecha_limite is not null");
				session.createSQLQuery(sql.toString()).executeUpdate();

		}catch (Exception e ) {
			logger.error("Error en incrementoFecha: " +e);
		}
	}

}
