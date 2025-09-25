package com.rsi.agp.dao.models.rc;

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
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;

public class EstadoCuponRCDao extends BaseDaoHibernate implements IEstadoCuponRCDao {
	
	private static final Log logger = LogFactory.getLog(PagoEstadosPolizaDao.class);

//	@Override
//	public List<EstadoCupon> getListaEstadoCupon() throws DAOException {
//		List<EstadoCupon> listaEstados = new ArrayList<EstadoCupon>();
//		try {
//			Session session = obtenerSession();
//			listaEstados =  session.createCriteria(EstadoCupon.class).list();
//			
//		}catch (Exception e){
//			logger.error("Error al obtener los estados del cupón", e);
//			throw new DAOException(e);
//		}
//		return listaEstados;
//	}
	public EstadoCuponRC getEstadoCupon(Long idEstado) throws DAOException {
		EstadoCuponRC estadoCupon = new EstadoCuponRC();
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(EstadoCuponRC.class);
		 
    		criteria.add(Restrictions.eq("id", idEstado));
			
    		estadoCupon = (EstadoCuponRC)criteria.uniqueResult();
			
		}catch (Exception e){
			logger.error("Error al obtener el objeto estadosCupon", e);
			throw new DAOException(e);
		}
		return estadoCupon;
	}

}
