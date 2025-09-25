package com.rsi.agp.dao.models.anexo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.poliza.PagoEstadosPolizaDao;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

public class EstadoCuponDao extends BaseDaoHibernate implements IEstadoCuponDao {
	
	private static final Log logger = LogFactory.getLog(PagoEstadosPolizaDao.class);

	@Override
	public List<EstadoCupon> getListaEstadoCupon() throws DAOException {
		List<EstadoCupon> listaEstados = new ArrayList<EstadoCupon>();
		try {
			Session session = obtenerSession();
			listaEstados =  session.createCriteria(EstadoCupon.class).list();
			
		}catch (Exception e){
			logger.error("Error al obtener los estados del cupón", e);
			throw new DAOException(e);
		}
		return listaEstados;
	}
	public EstadoCupon getEstadoCupon(Long idEstado) throws DAOException {
		EstadoCupon estadoCupon = new EstadoCupon();
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(EstadoCupon.class);
		 
    		criteria.add(Restrictions.eq("id", idEstado));
			
    		estadoCupon = (EstadoCupon)criteria.uniqueResult();
			
		}catch (Exception e){
			logger.error("Error al obtener el objeto estadosCupon", e);
			throw new DAOException(e);
		}
		return estadoCupon;
	}

}
