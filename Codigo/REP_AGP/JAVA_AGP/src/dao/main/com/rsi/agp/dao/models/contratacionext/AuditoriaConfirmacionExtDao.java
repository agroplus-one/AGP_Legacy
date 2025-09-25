package com.rsi.agp.dao.models.contratacionext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.importacion.AuditoriaAnexosExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaCalcAnxExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaConfirmacionExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaSiniestrosExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaValidarExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaCalculoExt;

public class AuditoriaConfirmacionExtDao extends BaseDaoHibernate implements
		IAuditoriaConfirmacionExtDao {

private Log logger = LogFactory.getLog(AuditoriaConfirmacionExtDao.class);
	
	public void saveAuditoriaConfirmacion(AuditoriaConfirmacionExt auditoriaConfirmacionExt)
			throws DAOException {
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(auditoriaConfirmacionExt);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el guardado de la entidad",
					ex);
		}
	}
	
	public void saveAuditoriaAnexo(AuditoriaAnexosExt auditoriaAnexosExt)
			throws DAOException {
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(auditoriaAnexosExt);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el guardado de la entidad",
					ex);
		}
	}
	
	public void saveAuditoriaSiniestro(AuditoriaSiniestrosExt auditoriaSiniestrosExt)
			throws DAOException {
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(auditoriaSiniestrosExt);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el guardado de la entidad",
					ex);
		}
	}
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	public void saveAuditoriaCalculo(AuditoriaCalculoExt auditoriaCalculoExt)
			throws DAOException {
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(auditoriaCalculoExt);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(	"Se ha producido un error al guardar Auditoria de Calculo en Pólizas Externas", ex);
		}
	}
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */
	
	
	public void saveAuditoriaCalculoAnexo(AuditoriaCalcAnxExt auditoriaCalcAnxExt) throws DAOException {
		
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(auditoriaCalcAnxExt);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(	"Se ha producido un error al guardar Auditoria de Calculo Anexo", ex);
		}
	}
	
	public void saveAuditoriaValidar(AuditoriaValidarExt auditoriaValidarExt)
			throws DAOException {
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(auditoriaValidarExt);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(	"Se ha producido un error al guardar Auditoria de Validar en Pólizas Externas", ex);
		}
	}
}
