package com.rsi.agp.core.managers.ged;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.poliza.CanalFirma;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public interface IDocumentacionGedManager {

	/**
	* P0073325 - RQ.04, RQ.05, RQ.06, RQ.10, RQ.11 y RQ.12
	*/
	public List<CanalFirma> getCanalesFirma() throws Exception;		
	
	public CanalFirma getCanalFirma(final Long codCanal) throws DAOException;		
	
	public String getDocBarcode();
	
	public String getDocBarcodeSBP();
	
	public void saveNewGedDocPoliza(final Long idPoliza, final String codUsuario) throws BusinessException;
	
	public void saveNewGedDocPolizaBatch(final Long idPoliza, final String codUsuario) throws BusinessException;
	
	public void saveNewGedDocPolizaSBP(final Long idPolizaSbp, final String codUsuario) throws BusinessException;

	/**
	* P0073325 - RQ.19
	*/
	public String uploadDocumentoPoliza(final String codUsuario, final MultipartFile file, final Poliza poliza) throws BusinessException;
	public String uploadDocumentoPoliza(final String codUsuario, final byte[] file, final Poliza poliza, final String firmaTableta, final Character docFirmada, final Long canal) throws BusinessException;
	public String uploadDocumentoPolizaSbp(final String codUsuario, final MultipartFile file, final PolizaSbp polizaPpal) throws BusinessException;
	public String uploadDocumentoPolizaSbp(final String codUsuario, final byte[] file, final PolizaSbp polizaPpal, final String firmaTableta, final Character docFirmada, final Long canal) throws BusinessException;
	public byte[] getDocumentoPoliza(final Long idPoliza, final String codEntidad, final String codOficina, final String usuario) throws BusinessException;
	public byte[] getDocumentoPolizaSbp(final Long idPolizaSbp, final String codEntidad, final String codOficina, final String usuario) throws BusinessException;
	public String getIdDocumentum(final Long idPoliza) throws BusinessException;
	public void marcarComoDiferida(final Long idPoliza) throws BusinessException;

	
}