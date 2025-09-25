package com.rsi.agp.core.managers.impl;

import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.poliza.IPolizasSWFinanciacionDao;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizasSWFinanciacion;

import es.agroseguro.serviciosweb.contratacionscutilidades.FinanciarRequest;

/**
 * Clase manager para centralizar todas las operaciones relacionadas con los históricos de llamadas a WS
 * @author U029823
 *
 */
public class HistoricoWSManager implements IManager {
	
	private static final Log logger = LogFactory.getLog(HistoricoWSManager.class);
	
	private IPolizasSWFinanciacionDao polizasSWFinanciacionDao;
	
	public void grabarLlamadaFinanciacion(FinanciarRequest financiarRequest, es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument financiacionDocument, Poliza poliza, String codModulo, Integer filaCompartiva, String codUsuario, String error){

		PolizasSWFinanciacion historico = new PolizasSWFinanciacion();
		historico.setPoliza(poliza);
		historico.setCodmodulo(codModulo);
		historico.setFilacompartiva(filaCompartiva);
		historico.setUsuario(codUsuario);
		historico.setFecha(new GregorianCalendar().getTime());
		historico.setXmlEnvio(Hibernate.createClob(WSUtils.generateXMLLlamadaFinanciar(financiarRequest)));
		
		if(financiacionDocument!=null){
			historico.setXmlRespuesta(Hibernate.createClob(financiacionDocument.toString()));
		}else{
			if(error!=null){
				historico.setXmlRespuesta(Hibernate.createClob(error));
			}else{
				historico.setXmlRespuesta(Hibernate.createClob("Error inesperado"));
			}
		}
		
		try {
			polizasSWFinanciacionDao.saveOrUpdate(historico);
		} catch (DAOException e) {
			logger.error("Error al guardar la llamada al WS de financiacion", e);
		}
	}

	public void setPolizasSWFinanciacionDao(IPolizasSWFinanciacionDao polizasSWFinanciacionDao) {
		this.polizasSWFinanciacionDao = polizasSWFinanciacionDao;
	}
}