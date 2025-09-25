/**
 * 
 */
package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IPagoEstadosPolizaManager;
import com.rsi.agp.dao.models.poliza.IPagoEstadosPolizaDao;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;

/**
 * @author U029769
 *
 */
public class PagoEstadosPolizaManager implements IPagoEstadosPolizaManager {

	private IPagoEstadosPolizaDao pagoEstadosPolizaDao; 
	private List<EstadosPoliza> estadosPago;
	private static final Log LOG = LogFactory.getLog(PagoEstadosPolizaManager.class);
	
	
	/**
	 * Devuelve todos los estados del pago de la poliza
	 * 02/08/2013 U029769
	 * @return List<EstadosPoliza>
	 * @throws BusinessException 
	 */
	public List<EstadosPoliza> getEstadosPagoPoliza () throws BusinessException{
		
		LOG.info("Obtenemos los estados de pago de la poliza");
		try {
			if (estadosPago == null || estadosPago.isEmpty()){
				estadosPago = pagoEstadosPolizaDao.getEstadosPagoPoliza();
			}
		
		} catch (DAOException e) {
			LOG.error("error al obtener los estados de pago de la poliza", e);
			throw new BusinessException(e);
		}
		return estadosPago;
		
	}

	public void setEstadosPago(List<EstadosPoliza> estadosPago) {
		this.estadosPago = estadosPago;
	}

	public void setPagoEstadosPolizaDao(IPagoEstadosPolizaDao pagoEstadosPolizaDao) {
		this.pagoEstadosPolizaDao = pagoEstadosPolizaDao;
	}

}
