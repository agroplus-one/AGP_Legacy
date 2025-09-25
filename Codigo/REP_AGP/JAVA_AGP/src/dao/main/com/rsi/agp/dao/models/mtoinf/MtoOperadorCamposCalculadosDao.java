package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;

public class MtoOperadorCamposCalculadosDao extends BaseDaoHibernate implements IMtoOperadorCamposCalculadosDao {

	@SuppressWarnings("unchecked")
	public List<OperadorCamposCalculados> getListaOperadores(BigDecimal idCampoCalculado) throws DAOException {

		List<OperadorCamposCalculados> listaOperadorCamposCalculados = null;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(OperadorCamposCalculados.class);
			criteria.add(Restrictions.eq("camposCalculados.id", idCampoCalculado.longValue()));
			listaOperadorCamposCalculados = criteria.list();

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error", ex);

		}
		return listaOperadorCamposCalculados;
	}

	/**
	 * Comprueba que no exista ya la relación campo-operador en la tabla
	 * OperadorCamposCalculados
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String checkCampo_OperadorExists(String idCampoCalc, BigDecimal idOperador, String idOpCalculado)
			throws DAOException {
		List<OperadorCamposCalculados> lstOpCamposCalc = new ArrayList<OperadorCamposCalculados>();
		Session session = obtenerSession();
		String idCampEncontrado = "";

		try {
			Criteria criteria = session.createCriteria(OperadorCamposCalculados.class);
			criteria.createAlias("camposCalculados", "campCalc");
			criteria.add(Restrictions.eq("campCalc.id", Long.parseLong(idCampoCalc)));
			criteria.add(Restrictions.eq("idoperador", idOperador));
			if (null != idOpCalculado) {
				criteria.add(Restrictions.ne("id", Long.parseLong(idOpCalculado)));
			}
			lstOpCamposCalc = criteria.list();
			if (!lstOpCamposCalc.isEmpty()) {
				OperadorCamposCalculados camCalc = lstOpCamposCalc.get(0);
				if (camCalc.getId() != null) {
					idCampEncontrado = camCalc.getId().toString();
				}
			}

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return idCampEncontrado;
	}

	/**
	 * Comprueba que no exista un OperadorCampoCalculado en la tabla
	 * CondicionCamposCalculados
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean existeCondicionCamCalc(String idOpCalculado) throws DAOException {
		List<CondicionCamposCalculados> lstCondicionCamCalc = new ArrayList<CondicionCamposCalculados>();
		Session session = obtenerSession();
		boolean condicionCamCalcExiste = false;

		try {
			Criteria criteria = session.createCriteria(CondicionCamposCalculados.class);
			criteria.createAlias("operadorCamposCalculados", "opCamCalc");
			criteria.add(Restrictions.eq("opCamCalc.id", Long.parseLong(idOpCalculado)));
			lstCondicionCamCalc = criteria.list();

			if (!lstCondicionCamCalc.isEmpty()) {
				CondicionCamposCalculados conCalc = lstCondicionCamCalc.get(0);
				if (conCalc.getId() != null) {
					condicionCamCalcExiste = true;
				}
			}

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}

		return condicionCamCalcExiste;
	}

	/**
	 * Comprueba que no exista el campo calculado en ningun informe
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean existeCamCalcEnInforme(String idCampoCalc) throws DAOException {
		List<DatoInformes> lstDatInf = new ArrayList<DatoInformes>();
		Session session = obtenerSession();
		boolean existeEnInforme = false;

		try {
			Criteria criteria = session.createCriteria(DatoInformes.class);
			criteria.createAlias("camposCalculados", "camCalc");
			criteria.add(Restrictions.eq("camCalc.id", Long.parseLong(idCampoCalc)));
			lstDatInf = criteria.list();

			if (!lstDatInf.isEmpty()) {
				DatoInformes datInf = lstDatInf.get(0);
				if (datInf.getId() != null) {
					existeEnInforme = true;
				}
			}

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}

		return existeEnInforme;
	}

}
