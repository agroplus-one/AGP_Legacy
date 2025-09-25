package com.rsi.agp.core.managers.impl.anexoMod;

import java.math.BigDecimal;

import org.w3._2005._05.xmlmime.Base64Binary;

import net.sf.jasperreports.engine.JasperPrint;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;

import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public interface IPolizaActualizadaManager {
	
	public JasperPrint verPolizaActualizada(String referencia, BigDecimal plan, String realPath, String pathInformes,boolean imprimirAnexoWs,
			AnexoModificacion am,boolean tieneCpl) 
			throws SWConsultaContratacionException, AgrException, Exception;
	
	public JasperPrint imprimirAnexoPpal(Long idAnexoModificacion, String pathInformes) throws DAOException;
	
	public Base64Binary obtenerPDFSituacionActual (final String referencia, final BigDecimal plan, String tipoRef, final String realPath) throws BusinessException;

}
