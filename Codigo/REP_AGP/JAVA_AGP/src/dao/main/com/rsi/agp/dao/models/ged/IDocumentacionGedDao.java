package com.rsi.agp.dao.models.ged;

import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.CanalFirma;

@SuppressWarnings("rawtypes")
public interface IDocumentacionGedDao extends GenericDao {

	public void updateGedDocPoliza(final Long idPoliza, final String idDocumentum) throws DAOException;
	
	/**
	* P0073325 - RQ.19
	*/
	// Alta documentacion general
	public void updateGedDocPoliza(final Long idPoliza, final Long idCanal, Character docFirmada, String codUsuario, final Date fechaFirma, final String idDocumentum) throws DAOException;
	// Modificacion documentacion general
	public void updateGedDocPoliza(final Long idPoliza, final Long idCanal, String codUsuario, final Date fechaFirma) throws DAOException;

	// Alta documentacion agrarios
	public void updateGedDocPolizaSbp(final Long idPoliza, final Long idCanal, Character docFirmada, String codUsuario, final Date fechaFirma, final String idDocumentum) throws DAOException;
	// Modificacion documentacion agrarios
	public void updateGedDocPolizaSbp(final Long idPoliza, final Long idCanal, String codUsuario, final Date fechaFirma) throws DAOException;
	
	public String getIdDocumentum(final Long idPoliza) throws DAOException;
	public String getIdDocumentumSbp(final Long idPolizaSbp) throws DAOException;
	
	/**
	* P0073325 - RQ.04, RQ.05, RQ.06, RQ.10, RQ.11 y RQ.12
	*/
	public List<CanalFirma> getCanalesFirma() throws DAOException;
	
	public void saveNewGedDocPoliza(final Long idPoliza, final String codUsuario, final String barCode) throws DAOException;
	
	public void saveNewGedDocPolizaBatch(final Long idPoliza, final String codUsuario, final String barCode) throws DAOException;
	
	public void saveNewGedDocPolizaSBP(final Long idPolizaSbp, final String codUsuario, String barCode) throws DAOException;
	
	public void marcarComoDiferida(final Long idPoliza) throws DAOException;

	
}