package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;

public class FaseDao extends BaseDaoHibernate implements IFaseDao{

	private static final Log logger = LogFactory.getLog(FaseDao.class);

	@Override
	public Fase isExistFase(String strFase, BigDecimal plan) throws DAOException {
		Session session = obtenerSession();
		Fase fase = new Fase();
		try {
			Criteria criteria = session.createCriteria(Fase.class);
			
			criteria.add(Restrictions.eq("fase",strFase));
			criteria.add(Restrictions.eq("plan",plan));
			
			if (criteria.list().size() > 0)
				fase = (Fase) criteria.list().get(0);
		}
		catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: ", ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
		return fase;
	}
	
	@Override
	public void saveFaseFichero(Fase fase)throws DAOException {
		Session session = obtenerSession();
		try {
			session.save(fase);
		} catch (Exception ex) {
			logger.error("Error al grabar la fase", ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad", ex);
		}
	}

	/**
	 * Dado un id de cierre, pone a null todas las apariciones de esa FK en la tabla de fases
	 */
	@Override
	public int borrarReferenciasCierre(Cierre cierre) throws DAOException {

		logger.debug("Init: borrarReferenciasCierre - FaseDao");
		int numRegistrosActualizados = -1;
		Set<FaseUnificado> fases = cierre.getFaseUnificados();
		List<Long> lstIdsFicheros = new ArrayList<Long>();
		String idsFicheros = "";
		for (FaseUnificado fase:fases) {
			lstIdsFicheros.add(fase.getFichero().getId());	
		}
		if (lstIdsFicheros.size()>0) {
			idsFicheros = lstIdsFicheros.toString().replace("[", "");
			idsFicheros = idsFicheros.replace("]", "");
		}
		try{			
			Session session = obtenerSession();
			String sql = "update TB_COMS_FASE fase set fase.idcierre = null where fase.idcierre = " + cierre.getId();
			
			numRegistrosActualizados = session.createSQLQuery(sql).executeUpdate();
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al borrar el idcierre en la tabla TB_COMS_FASE ", e);
			throw new DAOException();
		}	
		try{		
			Session session = obtenerSession();
			String sql = "update TB_COMS_UNIF_FASE fase set fase.idcierre = null where fase.idcierre = " + cierre.getId();
			
			numRegistrosActualizados = session.createSQLQuery(sql).executeUpdate();
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al borrar el idcierre en la tabla TB_COMS_UNIF_FASE ", e);
			throw new DAOException();
		}
		try{
			if (idsFicheros.length()>0) {	
				Session session = obtenerSession();
				String sql = "update TB_COMS_UNIF_FICHERO fich set fich.fecha_cierre = null where fich.id in("+idsFicheros+")";
				numRegistrosActualizados = session.createSQLQuery(sql).executeUpdate();
			}
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al borrar la fecha cierre de los ficheros: "+idsFicheros+" en la tabla TB_COMS_UNIF_FICHERO ", e);
			throw new DAOException();
		}
		logger.debug("Fin: borrarReferenciasCierre - FaseDao");
		return numRegistrosActualizados;
	}
	
	
	/**
	 * Dado un idFichero devuelve el plan al que pertenece
	 */
	@Override
	public BigDecimal obtenerPlanByIdFichero(Long idFichero) throws DAOException {
		
		logger.debug("Init: obtenerPlanByIdFichero - FaseDao");
		BigDecimal plan = null;
		
		try{
			Session session = obtenerSession();
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append("select plan ");
			stringQuery.append("from TB_COMS_FICHEROS fic, TB_COMS_FASE fase ");
			stringQuery.append("where fic.idfase = fase.id ");
			stringQuery.append("and fic.id = ").append(idFichero);
	
			SQLQuery query = session.createSQLQuery(stringQuery.toString());
			plan = new BigDecimal((query.uniqueResult()).toString());
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al intentar obtener el plan de un fichero: ", e);
			throw new DAOException();
		}
		
		return plan;
	}
}