package com.rsi.agp.dao.models.contratacionext;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.importacion.AuditoriaAnexosExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaCalcAnxExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaConfirmacionExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaSiniestrosExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaValidarExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaCalculoExt;

public interface IAuditoriaConfirmacionExtDao {
	
	public void saveAuditoriaConfirmacion(AuditoriaConfirmacionExt auditoriaConfirmacionExt) throws DAOException;

	public void saveAuditoriaAnexo(AuditoriaAnexosExt auditoriaAnexosExt) throws DAOException;
	
	public void saveAuditoriaSiniestro(AuditoriaSiniestrosExt auditoriaSiniestrosExt) throws DAOException;
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) */
	public void saveAuditoriaCalculo(AuditoriaCalculoExt auditoriaCalculonExt) throws DAOException;
	
	public void saveAuditoriaCalculoAnexo(AuditoriaCalcAnxExt auditoriaCalcAnxExt) throws DAOException;
	
	public void saveAuditoriaValidar(AuditoriaValidarExt auditoriaValidarExt) throws DAOException;
}
