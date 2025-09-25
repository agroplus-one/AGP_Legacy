package com.rsi.agp.dao.models.poliza;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;



public class PagoEstadosPolizaDao extends BaseDaoHibernate implements IPagoEstadosPolizaDao{

	
	private static final Log LOG = LogFactory.getLog(PagoEstadosPolizaDao.class);
	
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Obtiene todos los estados de pago de poliza
	 * 05/08/2013 U029769
	 * @return
	 * @throws DAOException
	 */
	public List<EstadosPoliza> getEstadosPagoPoliza() throws DAOException{
		
		List<EstadosPoliza> listaEstados = new ArrayList<EstadosPoliza>();
		try {
			Session session = obtenerSession();
			listaEstados =  session.createCriteria(EstadosPoliza.class).list();
			
		}catch (Exception e){
			LOG.error("error al obtener los estados de pago de la poliza");
			throw new DAOException(e.getMessage());
		}
		return listaEstados;
		
	}

}
