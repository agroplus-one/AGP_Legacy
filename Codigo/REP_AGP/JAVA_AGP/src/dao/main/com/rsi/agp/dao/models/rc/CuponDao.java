package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.HistoricoCuponRC;

public class CuponDao extends BaseDaoHibernate implements ICuponDao {

	@Override
	public CuponRC saveCupon(final CuponRC cupon) throws DAOException {
		try {
			return (CuponRC) this.saveOrUpdate(cupon);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al guardar el cupon", e);
			throw new DAOException("Ha ocurrido un error al guardar el cupon");
		}
	}

	@Override
	public HistoricoCuponRC saveHistoricoCupon(final HistoricoCuponRC historico)
			throws DAOException {
		try {
			this.saveOrUpdate(historico);
			return historico;
		} catch (DAOException exc) {
			logger.error(
					"Ha ocurrido un error al guardar el historico del cupon",
					exc);
			throw new DAOException(
					"Ha ocurrido un error al guardar el historico del cupon");
		}
	}

	@Override
	public void borrarCupon(final String idCupon) throws DAOException {
		try {
			@SuppressWarnings("unchecked")
			List<CuponRC> lista = (List<CuponRC>) this.getObjects(CuponRC.class,
					"idcupon", idCupon);
			List<HistoricoCuponRC> listahistorico = null; 
			
			for (CuponRC cupon : lista) {
				listahistorico = this.getObjects(HistoricoCuponRC.class, "cuponRC", cupon);
				for (HistoricoCuponRC historicoCupon : listahistorico) {
					this.delete(historicoCupon);
				}
				this.delete(cupon);
			}
			
			
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al borrar el cupon con id "
					+ idCupon, e);
			throw new DAOException(
					"Ha ocurrido un error al borrar el cupon");
		}
	}

	@Override
	public CuponRC obtenerCupon(String idCupon) throws DAOException {
		// TODO Auto-generated method stub
		try {
			CuponRC cupon = (CuponRC) this.getObject(CuponRC.class, "idcupon", idCupon);
			return cupon;
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el cupon con id "
					+ idCupon, e);
			throw new DAOException(
					"Ha ocurrido un error al obtener el cupon");
		}
	}

}
