package com.rsi.agp.dao.models.ged;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.ged.GedDocPoliza;
import com.rsi.agp.dao.tables.ged.GedDocPolizaSbp;
import com.rsi.agp.dao.tables.poliza.CanalFirma;


public class DocumentacionGedDao extends BaseDaoHibernate implements IDocumentacionGedDao {

	private static final Log logger = LogFactory.getLog(DocumentacionGedDao.class);
	
	
	@Override
	public void updateGedDocPoliza(final Long idPoliza, final String idDocumentum) throws DAOException {

		Session session = obtenerSession();

		try {

			logger.debug("init - [DocumentacionGedDao] createGedDocPoliza");
			
			GedDocPoliza gedDocPoliza;
			
			gedDocPoliza = (GedDocPoliza) session.createCriteria(GedDocPoliza.class)
					/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
					.add(Restrictions.eq("poliza.idpoliza", idPoliza)).uniqueResult();
					/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
			
			gedDocPoliza.setIdDocumentum(idDocumentum);
			session.saveOrUpdate(gedDocPoliza);

			logger.debug("end - [DocumentacionGedDao] createGedDocPoliza");
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	/**
	 * P0073325 - RQ. 19
	 */
	@Override
	public void updateGedDocPoliza(final Long idPoliza, final Long idCanal, Character docFirmada, String codUsuario, final Date fechaFirma, final String idDocumentum) throws DAOException {

		Session session = obtenerSession();

		try {

			logger.debug("DocumentacionGedDao - updateGedDocPolizaAlta - init");
			
			GedDocPoliza gedDocPoliza;
			
			gedDocPoliza = (GedDocPoliza) session.createCriteria(GedDocPoliza.class)
					/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
					.add(Restrictions.eq("poliza.idpoliza", idPoliza)).uniqueResult();
					/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
			
			gedDocPoliza.setIdDocumentum(idDocumentum);
			gedDocPoliza.setCanalFirma(new CanalFirma());
			gedDocPoliza.getCanalFirma().setIdCanal(idCanal);
			gedDocPoliza.setDocFirmada(docFirmada);
			gedDocPoliza.setCodUsuario(codUsuario);
			gedDocPoliza.setFechaFirma(new Date());
			
			session.saveOrUpdate(gedDocPoliza);

			logger.debug("DocumentacionGedDao - updateGedDocPolizaAlta - end");
			
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/**
	 * P0073325 - RQ. 19
	 */
	@Override
	public void updateGedDocPolizaSbp(final Long idPoliza, final Long idCanal, Character docFirmada, String codUsuario, final Date fechaFirma, final String idDocumentum) throws DAOException {

		Session session = obtenerSession();

		try {

			logger.debug("DocumentacionGedDao - updateGedDocPolizaSbpAlta - init");
			
			GedDocPolizaSbp gedDocPolizaSbp;
			
			gedDocPolizaSbp = (GedDocPolizaSbp) session.createCriteria(GedDocPolizaSbp.class).add(Restrictions.eq("polizaSbp.id", idPoliza)).uniqueResult();
			
			gedDocPolizaSbp.setIdDocumentum(idDocumentum);
			gedDocPolizaSbp.setCanalFirma(new CanalFirma());
			gedDocPolizaSbp.getCanalFirma().setIdCanal(idCanal);
			gedDocPolizaSbp.setDocFirmada(docFirmada);
			gedDocPolizaSbp.setCodUsuario(codUsuario);
			gedDocPolizaSbp.setFechaFirma(new Date());
			
			session.saveOrUpdate(gedDocPolizaSbp);

			logger.debug("DocumentacionGedDao - updateGedDocPolizaSbpAlta - end");
			
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/**
	 * P0073325 - RQ. 19
	 */
	@Override
	public void updateGedDocPoliza(Long idPoliza, Long idCanal, String codUsuario, Date fechaFirma) throws DAOException {
	
		Session session = obtenerSession();

		try {

			logger.debug("DocumentacionGedDao - updateGedDocPolizaModif - init");
			
			GedDocPoliza gedDocPoliza;
			
			gedDocPoliza = (GedDocPoliza) session.createCriteria(GedDocPoliza.class)
					/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
					.add(Restrictions.eq("poliza.idpoliza", idPoliza)).uniqueResult();
					/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
			
			gedDocPoliza.setCanalFirma(new CanalFirma());
			gedDocPoliza.getCanalFirma().setIdCanal(idCanal);
			gedDocPoliza.setCodUsuario(codUsuario);
			gedDocPoliza.setFechaFirma(new Date());
			
			session.saveOrUpdate(gedDocPoliza);

			logger.debug("DocumentacionGedDao - updateGedDocPolizaModif - end");
			
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
	}

	/**
	 * P0073325 - RQ. 19
	 */
	@Override
	public void updateGedDocPolizaSbp(Long idPoliza, Long idCanal, String codUsuario, Date fechaFirma) throws DAOException {
		
		Session session = obtenerSession();

		try {

			logger.debug("DocumentacionGedDao - updateGedDocPolizaSbpAlta - init");
			
			GedDocPolizaSbp gedDocPolizaSbp;
			
			gedDocPolizaSbp = (GedDocPolizaSbp) session.createCriteria(GedDocPolizaSbp.class).add(Restrictions.eq("polizaSbp.id", idPoliza)).uniqueResult();
			
			gedDocPolizaSbp.setCanalFirma(new CanalFirma());
			gedDocPolizaSbp.getCanalFirma().setIdCanal(idCanal);
			gedDocPolizaSbp.setCodUsuario(codUsuario);
			gedDocPolizaSbp.setFechaFirma(new Date());
			
			session.saveOrUpdate(gedDocPolizaSbp);

			logger.debug("DocumentacionGedDao - updateGedDocPolizaSbpAlta - end");
			
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
	}


	@Override
	public String getIdDocumentum(final Long idPoliza) throws DAOException {

		String idDocumentum = null;
		
		Session session = obtenerSession();

		try {

			logger.debug("init - [DocumentacionGedDao] getIdDocumentum");

			Criteria crit = session.createCriteria(GedDocPoliza.class);
			crit.add(Restrictions.eq("idPoliza", idPoliza));
			crit.setProjection(Projections.distinct(Projections.property("idDocumentum")));
			
			idDocumentum = (String) crit.uniqueResult();

			logger.debug("end - [DocumentacionGedDao] getIdDocumentum");
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		return idDocumentum;
	}	
	
	@Override
	public String getIdDocumentumSbp(final Long idPolizaSbp) throws DAOException {

		String idDocumentum = null;
		
		Session session = obtenerSession();

		try {

			logger.debug("init - [DocumentacionGedDao] getIdDocumentumSbp");

			Criteria crit = session.createCriteria(GedDocPolizaSbp.class);			
			crit.add(Restrictions.eq("idPolizaSbp", idPolizaSbp));
			crit.setProjection(Projections.distinct(Projections.property("idDocumentum")));
			
			idDocumentum = (String) crit.uniqueResult();

			logger.debug("end - [DocumentacionGedDao] getIdDocumentumSbp");
		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		return idDocumentum;
	}

	/**
	* P0073325 - RQ.04, RQ.05, RQ.06, RQ.10, RQ.11 y RQ.12
	*/
	@SuppressWarnings("unchecked")
	public List<CanalFirma> getCanalesFirma() throws DAOException {
		logger.debug("DocumentacionGedDao - getCanalesFirma() - init");
		Session session = obtenerSession();
		List<CanalFirma> lista = new ArrayList<CanalFirma>();
		try {
			Criteria criteria = session.createCriteria(CanalFirma.class);
			lista = criteria.list();
		} catch (Exception e) {
			throw new DAOException("DocumentacionGedDao - getCanalesFirma() error lectura BD", e);
		}
		logger.debug("DocumentacionGedDao - getCanalesFirma() - end");
		return lista;
	}

	@Override
	public void saveNewGedDocPoliza(final Long idPoliza, final String codUsuario, final String barCode) throws DAOException {
		Session session = obtenerSession();
		logger.debug("DocumentacionGedDao - saveNewGedDocPoliza() - init");
		
		try {
			Query query = session.createSQLQuery("INSERT INTO O02AGPE0.TB_GED_DOC_POLIZA VALUES (:idPoliza, 'N/A', 1, 'N', SYSDATE, :codUsuario, :barCode)");
			query.setParameter("idPoliza", idPoliza);
			query.setParameter("codUsuario", codUsuario);
			query.setParameter("barCode", barCode);
			query.executeUpdate();
		} catch (Exception e) {
			throw new DAOException("DocumentacionGedDao - saveNewGedDocPoliza() error escritura BD", e);
		}	
		logger.debug("DocumentacionGedDao - saveNewGedDocPoliza() - end");
	}
	
	@Override
	public void saveNewGedDocPolizaBatch(final Long idPoliza, final String codUsuario, final String barCode) throws DAOException {
		Session session = obtenerSession();
		logger.debug("DocumentacionGedDao - saveNewGedDocPoliza() - init");
		
		logger.debug("DocumentacionGedDao - saveNewGedDocPoliza() - inicio");
		
		Transaction trans = session.beginTransaction();
		
		try {
			logger.debug("DocumentacionGedDao - saveNewGedDocPoliza() - ESTA ENTRANDO");
			Query query = session.createSQLQuery("INSERT INTO O02AGPE0.TB_GED_DOC_POLIZA VALUES (:idPoliza, 'N/A', 1, 'N', SYSDATE, :codUsuario, :barCode)");
			query.setParameter("idPoliza", idPoliza);
			query.setParameter("codUsuario", codUsuario);
			query.setParameter("barCode", barCode);
			logger.debug("INSERT: " + query.getQueryString());
			logger.debug(idPoliza + " " + codUsuario + " " + barCode);
			logger.debug(query.executeUpdate());
			trans.commit();
		} catch (Exception e) {
			throw new DAOException("DocumentacionGedDao - saveNewGedDocPoliza() error escritura BD", e);
		}	
		logger.debug("DocumentacionGedDao - saveNewGedDocPoliza() - end");
	}
	
	

	@Override
	public void saveNewGedDocPolizaSBP(final Long idPolizaSbp, final String codUsuario, String barCode) throws DAOException {
		Session session = obtenerSession();
		logger.debug("DocumentacionGedDao - saveNewGedDocPolizaSBP() - init");
		try {
			if (StringUtils.isNullOrEmpty(barCode)) {
				logger.debug("Suplemento - Se obtiene el barcode de la poliza principal de sbp");
				// SI barCode ES NULO ES UN SUPLEMENTO DE SBP
				// OBTENEMOS EL CODIGO DE BARRAS DE LA POLIZA DE SBP
				Query query1 = session.createSQLQuery("SELECT CAST(doc.cod_barras AS varchar2(9)) FROM o02agpe0.TB_GED_DOC_POLIZA_SBP doc INNER JOIN o02agpe0.TB_SBP_POLIZAS p ON doc.idpoliza_sbp = p.id AND p.tipoenvio = 1 INNER JOIN o02agpe0.TB_SBP_POLIZAS sup ON sup.id = :idSuplemento AND sup.idpoliza = p.idpoliza");
				query1.setParameter("idSuplemento", idPolizaSbp);
				barCode = (String) query1.uniqueResult();
				logger.debug("barCode: " + barCode);
			}
			Query query = session.createSQLQuery("INSERT INTO O02AGPE0.TB_GED_DOC_POLIZA_SBP VALUES (:idPolizaSbp, 'N/A', 1, 'N', SYSDATE, :codUsuario, :barCode)");
			query.setParameter("idPolizaSbp", idPolizaSbp);
			query.setParameter("codUsuario", codUsuario);
			query.setParameter("barCode", barCode);
			query.executeUpdate();
		} catch (Exception e) {
			throw new DAOException("DocumentacionGedDao - saveNewGedDocPolizaSBP() error escritura BD", e);
		}
		logger.debug("DocumentacionGedDao - saveNewGedDocPolizaSBP() - end");
	}

	@Override
	public void marcarComoDiferida(final Long idPoliza) throws DAOException {
		logger.debug("DocumentacionGedDao - marcarComoDiferida() - init");
		Session session = obtenerSession();
		
		try {
			Query query = session.createSQLQuery("UPDATE O02AGPE0.TB_GED_DOC_POLIZA SET IND_DOC_FIRMADA = '" + Constants.CHARACTER_N + "', COD_CANAL_FIRMA = " + Constants.CANAL_FIRMA_DIFERIDA + " WHERE idpoliza = :idPoliza");
			query.setParameter("idPoliza", idPoliza);
			query.executeUpdate();
		} catch (Exception e) {
			throw new DAOException("DocumentacionGedDao - getCanalesFirma() error lectura BD", e);
		}
		logger.debug("DocumentacionGedDao - marcarComoDiferida() - end");
	}
}