package com.rsi.agp.dao.models.contratacionext;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna;
import com.rsi.agp.dao.tables.importacion.CuponExt;
import com.rsi.agp.dao.tables.importacion.CuponExtId;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class CorreduriaExternaDao extends BaseDaoHibernate implements ICorreduriaExternaDao {

	@Override
	public CorreduriaExterna getCorreduria(final String codInterno) throws DAOException {
		logger.debug("init - getCorreduria");
		Session session = obtenerSession();
		CorreduriaExterna correduria = null;
		try {
			Criteria criteria = session.createCriteria(CorreduriaExterna.class);
			criteria.add(Restrictions.eq("codInterno", codInterno));
			correduria = (CorreduriaExterna) criteria.uniqueResult();
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD TB_CORREDURIAS_EXTERNAS", e);
		}
		logger.debug("end - getCorreduria");
		return correduria;
	}

	@Override
	public void guardarCupon(final BigDecimal plan, final String referencia, final String idCupon) throws DAOException {
		logger.debug("init - guardarCupon");
		Session session = obtenerSession();
		Poliza poliza = null;
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("linea", "linea");
			criteria.add(Restrictions.eq("referencia", referencia));
			criteria.add(Restrictions.eq("linea.codplan", plan));
			criteria.add(Restrictions.eq("tipoReferencia", 'P'));
			poliza = (Poliza) criteria.uniqueResult();
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD TB_POLIZAS", e);
		}
		if (poliza != null) {
			CuponExt cupon = new CuponExt();
			try {
				CuponExtId id = new CuponExtId();
				id.setIdpoliza(poliza.getIdpoliza());
				id.setIdcupon(idCupon);
				cupon.setId(id);
				session.save(cupon);
			} catch (Exception e) {
				logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
				throw new DAOException("Se ha producido un error en la BBDD TB_CUPON_EXT", e);
			}
		}
		logger.debug("end - guardarCupon");
	}

	@Override
	public void anularCupon(final String idCupon) throws DAOException {
		logger.debug("init - anularCupon");
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(CuponExt.class);
			criteria.add(Restrictions.eq("id.idcupon", idCupon));
			CuponExt cupon = (CuponExt) criteria.uniqueResult();
			session.delete(cupon);
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD TB_CUPON_EXT", e);
		}
		logger.debug("end - anularCupon");
	}

	@Override
	public String getCorreduriaPoliza(final BigDecimal plan, final String referencia) throws DAOException {
		logger.debug("init - getCorreduriaPoliza");
		String correduria = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("linea", "linea");
			criteria.add(Restrictions.eq("referencia", referencia));
			criteria.add(Restrictions.eq("linea.codplan", plan));
			criteria.add(Restrictions.eq("tipoReferencia", 'P'));
			Poliza poliza = (Poliza) criteria.uniqueResult();
			if (poliza != null) {
				correduria = org.apache.commons.lang.StringUtils.leftPad(
						poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad().toString(), 4, '0')
						+ org.apache.commons.lang.StringUtils.leftPad(
								poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad().toString(), 4,
								'0');
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD TB_POLIZAS", e);
		}
		logger.debug("end - getCorreduriaPoliza");
		return correduria;
	}

	@Override
	public String getCorreduriaCupon(final String idCupon) throws DAOException {
		logger.debug("init - getCorreduriaCupon");
		String correduria = null;
		CuponExt cupon = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(CuponExt.class);
			criteria.add(Restrictions.eq("id.idcupon", idCupon));
			cupon = (CuponExt) criteria.uniqueResult();
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD TB_CUPON_EXT", e);
		}
		if (cupon != null) {
			try {
				Poliza poliza = (Poliza) session.get(Poliza.class, cupon.getId().getIdpoliza());
				if (poliza != null) {
					correduria = org.apache.commons.lang.StringUtils.leftPad(
							poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad().toString(), 4, '0')
							+ org.apache.commons.lang.StringUtils.leftPad(poliza.getColectivo().getSubentidadMediadora()
									.getId().getCodsubentidad().toString(), 4, '0');
				}
			} catch (Exception e) {
				logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
				throw new DAOException("Se ha producido un error en la BBDD TB_POLIZAS", e);
			}
		}
		logger.debug("end - getCorreduriaCupon");
		return correduria;
	}
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	@Override
	public List<Colectivo> getColectivo(final String referencia, final int dc) throws DAOException {
		logger.debug("init - getColectivo");
		Session sesion = obtenerSession();
		try {
				
			Criteria criteria = sesion.createCriteria(Colectivo.class);		
			criteria.add(Restrictions.eq("idcolectivo", referencia));
			criteria.add(Restrictions.eq("dc", Integer.toString(dc)));
				
			return criteria.list();
				
		} catch (Exception ex) {
				logger.info("Se ha producido un error al obtener los colectivos referencia = " + referencia + "-" + dc, ex);
		}
		return null;
	}
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */
}