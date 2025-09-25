package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;
import com.rsi.agp.dao.tables.poliza.Linea;

public class IncidenciasComisionesDao extends BaseDaoHibernate implements IIncidenciasComisionesDao{
	
	private static final Log logger= LogFactory.getLog(IncidenciasComisionesDao.class);

	public List<FicheroIncidencia> getListFicherosIncidencias(FicheroIncidencia ficheroIncidenciaBean) throws DAOException {
		logger.debug("init - getFicherosIncidenciasListado");
		Session session = obtenerSession();
		try {						
			StringBuffer filtroSql = new StringBuffer();		
			
			if (FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getIdcolectivo())){
				filtroSql.append(" and fi.idcolectivo = '" + ficheroIncidenciaBean.getIdcolectivo()+"'");
			}
			if ((FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getLinea())) &&
				(FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getLinea().getCodlinea()))){
				filtroSql.append(" and l.codlinea = " + ficheroIncidenciaBean.getLinea().getCodlinea());
			}
			if (FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getSubentidad())){
				String[] subEntidad =ficheroIncidenciaBean.getSubentidad().split("-"); 
				//Si solo busca por entidad
				String aux = " and fi.subentidad like '%"+subEntidad[0]+"%'";
				//Si busca por entidad - subentidad
				if (subEntidad.length>1)
					aux =  " and fi.subentidad = '"+subEntidad[0]+"-"+subEntidad[1]+"'";
				filtroSql.append(aux);
			}
			if (FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getOficina())){
				filtroSql.append(" and fi.oficina = '" + ficheroIncidenciaBean.getOficina()+"'");
			}
			if (FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getMensaje())){
				filtroSql.append(" and fi.mensaje like '%" + ficheroIncidenciaBean.getMensaje()+"%'");
			}
			if (FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getEstado())){
				filtroSql.append(" and fi.estado = '" + ficheroIncidenciaBean.getEstado()+"'");
			}	
			if (FiltroUtils.noEstaVacio(ficheroIncidenciaBean.getRefpoliza())){
				filtroSql.append(" and fi.refpoliza = '" + ficheroIncidenciaBean.getRefpoliza()+"'");
			}
			String sql =" select l.codplan,l.codlinea, fi.idcolectivo,fi.subentidad,fi.refpoliza,fi.oficina,fi.estado,fi.mensaje," +
			 "  fi.ES_MED_COLECTIVO from TB_COMS_FICHEROS_INCIDENCIAS fi,TB_LINEAS l" +
			 " where  l.lineaseguroid = fi.lineaseguroid and fi.idfichero ="+ ficheroIncidenciaBean.getFichero().getId()+" "
			   + filtroSql.toString();
			
			logger.info("sql " + sql);
			List<FicheroIncidencia> listaIncidencias = new ArrayList<FicheroIncidencia>();
			
			List resultado = session.createSQLQuery(sql).list();
			if (resultado.size()>0)
			{
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					FicheroIncidencia aux = new FicheroIncidencia();
					Linea l = new Linea();
					
					if (registro[0] != null && registro[1] != null) {
						l.setCodplan((BigDecimal) registro[0]);
						l.setCodlinea((BigDecimal) registro[1]);
						aux.setLinea(l);
					}
					if (registro[2] != null) {
						aux.setIdcolectivo((String) registro[2]);
					}
					if (registro[3] != null) {
						aux.setSubentidad((String) registro[3]);
					}
					if (registro[4] != null) {
						aux.setRefpoliza((String) registro[4]);
					}
					if (registro[5] != null) {
						aux.setOficina((String) registro[5]);
					}
					if (registro[6] != null) {
						aux.setEstado((String) registro[6]);
					}
					if (registro[7] != null) {
						aux.setMensaje((String) registro[7]);
					}
					if (registro[8] != null) {
						aux.setEsMedColectivo((String)registro[8]);
					}
					
					listaIncidencias.add(aux);
				}
			}
			return listaIncidencias;

		
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a BBDD",ex);
		}
	}
	
	public List<FicheroMultIncidencias> getListFicherosMultIncidencias(FicheroMultIncidencias ficheroMultIncidenciaBean) throws DAOException {
		logger.debug("init - getFicherosMultIncidenciasListado");
		Session session = obtenerSession();
		try {						
			StringBuffer filtroSql = new StringBuffer();		
			
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getIdcolectivo())){
				filtroSql.append(" and fi.idcolectivo = '" + ficheroMultIncidenciaBean.getIdcolectivo()+"'");
			}
			if ((FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getLinea())) &&
				(FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getLinea().getCodlinea()))){
				filtroSql.append(" and l.codlinea = " + ficheroMultIncidenciaBean.getLinea().getCodlinea());
			}
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getSubentidad())){
				String[] subEntidad =ficheroMultIncidenciaBean.getSubentidad().split("-"); 
				//Si solo busca por entidad
				String aux = " and fi.subentidad like '%"+subEntidad[0]+"%'";
				//Si busca por entidad - subentidad
				if (subEntidad.length>1)
					aux =  " and fi.subentidad = '"+subEntidad[0]+"-"+subEntidad[1]+"'";
				filtroSql.append(aux);
			}
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getEsMedColectivo())){
				filtroSql.append(" and fi.es_med_colectivo = '" + ficheroMultIncidenciaBean.getEsMedColectivo()+"'");
			}
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getOficina())){
				filtroSql.append(" and fi.oficina = '" + ficheroMultIncidenciaBean.getOficina()+"'");
			}
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getMensaje())){
				filtroSql.append(" and fi.mensaje like '%" + ficheroMultIncidenciaBean.getMensaje()+"%'");
			}
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getEstado())){
				filtroSql.append(" and fi.estado = '" + ficheroMultIncidenciaBean.getEstado()+"'");
			}	
			if (FiltroUtils.noEstaVacio(ficheroMultIncidenciaBean.getRefpoliza())){
				filtroSql.append(" and fi.refpoliza = '" + ficheroMultIncidenciaBean.getRefpoliza()+"'");
			}
			String sql =" select l.codplan,l.codlinea, fi.idcolectivo,fi.subentidad,fi.refpoliza,fi.oficina,fi.estado,fi.mensaje," +
			 "  fi.ES_MED_COLECTIVO from TB_COMS_FICHERO_MULT_INCIDENC fi,TB_LINEAS l" +
			 " where  l.lineaseguroid = fi.lineaseguroid and fi.idficheromult ="+ ficheroMultIncidenciaBean.getFicheroMult().getId()+" "
			   + filtroSql.toString();
			
			logger.info("sql " + sql);
			List<FicheroMultIncidencias> listaIncidencias = new ArrayList<FicheroMultIncidencias>();
			
			List resultado = session.createSQLQuery(sql).list();
			if (resultado.size()>0)
			{
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					FicheroMultIncidencias aux = new FicheroMultIncidencias();
					Linea l = new Linea();
					
					if (registro[0] != null && registro[1] != null) {
						l.setCodplan((BigDecimal) registro[0]);
						l.setCodlinea((BigDecimal) registro[1]);
						aux.setLinea(l);
					}
					if (registro[2] != null) {
						aux.setIdcolectivo((String) registro[2]);
					}
					if (registro[3] != null) {
						aux.setSubentidad((String) registro[3]);
					}
					if (registro[4] != null) {
						aux.setRefpoliza((String) registro[4]);
					}
					if (registro[5] != null) {
						aux.setOficina((String) registro[5]);
					}
					if (registro[6] != null) {
						aux.setEstado((String) registro[6]);
					}
					if (registro[7] != null) {
						aux.setMensaje((String) registro[7]);
					}
					if (registro[8] != null) {
						aux.setEsMedColectivo((String)registro[8]);
					}
					
					listaIncidencias.add(aux);
				}
			}
			return listaIncidencias;

		
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a BBDD",ex);
		}
	}

	public SubentidadMediadora getSubEntByColectivo(String idcolectivo,String entmediadora,String subentmediadora) throws DAOException{
		logger.debug("init - getSubEntByColectivo");
		Session session = obtenerSession();
		List<Colectivo> list = null;
		SubentidadMediadora subEnt = null;
		try {
			Criteria criteria = session.createCriteria(Colectivo.class);
			criteria.add(Restrictions.eq("idcolectivo", idcolectivo));
			
			list = criteria.list();		
			if(list.size() > 0)
				subEnt = (SubentidadMediadora) list.get(0).getSubentidadMediadora();	
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
		logger.debug("end - getSubEntByColectivo");
		return subEnt;
		
	}
	public void deleteFichero(Long idFichero) throws DAOException{
		
		logger.debug("init - deleteFichero");
		Session session = obtenerSession();
		
		try {
			String hql = "delete from TB_COMS_FICHEROS_INCIDENCIAS fi where fi.idfichero = :idFichero";
			SQLQuery query = session.createSQLQuery(hql);
			query.setParameter("idFichero", idFichero);
            int row = query.executeUpdate();

			if(row == 0)
				logger.debug(" --> No se borro ninguna fila.");
			else
				logger.debug(" --> Se borraron " +  row + " filas.");
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
		logger.debug("end - deleteFichero");
	}
	
	public void deleteFicheroMult(Long idFichero) throws DAOException{
		
		logger.debug("init - deleteFichero");
		Session session = obtenerSession();
		
		try {
			String hql = "delete from TB_COMS_FICHERO_MULT_INCIDENC fi where fi.idfichero = :idFichero";
			SQLQuery query = session.createSQLQuery(hql);
			query.setParameter("idFichero", idFichero);
            int row = query.executeUpdate();

			if(row == 0)
				logger.debug(" --> No se borro ninguna fila.");
			else
				logger.debug(" --> Se borraron " +  row + " filas.");
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
		logger.debug("end - deleteFichero");
	}
		
}
