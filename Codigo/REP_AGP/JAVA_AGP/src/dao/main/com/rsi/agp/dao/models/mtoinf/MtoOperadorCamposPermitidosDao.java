package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.InformeUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.CondicionCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.Operador;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;

public class MtoOperadorCamposPermitidosDao extends BaseDaoHibernate implements IMtoOperadorCamposPermitidosDao {

	/**
	 * Comprueba que no exista ya la relación campo-operador en la tabla
	 * OperadorCamposPermitido
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String checkCampo_OperadorExists(String idVistaCampo, BigDecimal idOperador, String idOpCampoPermitido)
			throws DAOException {
		List<OperadorCamposPermitido> lstOpCamposPermitidos = new ArrayList<OperadorCamposPermitido>();
		Session session = obtenerSession();
		String idCampEncontrado = "";

		try {
			Criteria criteria = session.createCriteria(OperadorCamposPermitido.class);
			criteria.createAlias("vistaCampo", "visC");
			criteria.add(Restrictions.eq("visC.id", new BigDecimal(idVistaCampo)));
			criteria.add(Restrictions.eq("idoperador", idOperador));
			if (null != idOpCampoPermitido) {
				criteria.add(Restrictions.ne("id", Long.parseLong(idOpCampoPermitido)));
			}
			lstOpCamposPermitidos = criteria.list();

			if (!lstOpCamposPermitidos.isEmpty()) {
				OperadorCamposPermitido camPer = lstOpCamposPermitidos.get(0);
				if (camPer.getId() != null) {
					idCampEncontrado = camPer.getId().toString();
				}
			}

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return idCampEncontrado;
	}

	/**
	 * Método para obtener una lista de operadores de campos permitidos
	 */
	@SuppressWarnings("unchecked")
	public List<Operador> getListaOperadores(Long idCampoPermitido) throws DAOException {

		List<Operador> listaOperadores = null;
		try {
			Session session = obtenerSession();

			Criteria criteria = session.createCriteria(OperadorCamposPermitido.class);
			criteria.createAlias("vistaCampo", "vc");
			criteria.createAlias("vc.camposPermitidoses", "cp");

			criteria.add(Restrictions.eq("cp.id", idCampoPermitido));
			List<OperadorCamposPermitido> lista = criteria.list();

			if (lista.size() > 0) {
				listaOperadores = new ArrayList<Operador>();
				for (OperadorCamposPermitido ocp : lista) {
					Operador op = new Operador(ocp.getId().intValue(), ocp.getIdoperador().intValue(),
							InformeUtils.getValueOperador((ocp.getIdoperador()).intValue()));
					listaOperadores.add(op);
				}
			}
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al obtener la lista de operadores", ex);
		}
		return listaOperadores;
	}

	/**
	 * Comprueba que no exista un OperadorCamposPermitido en la tabla
	 * CondicionCamposPermitidos
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean existeCondicionCamPerm(String idOpCamPer) throws DAOException {
		List<CondicionCamposPermitidos> lstCondicionCamPer = new ArrayList<CondicionCamposPermitidos>();
		Session session = obtenerSession();
		boolean condicionCamPerExists = false;

		try {
			Criteria criteria = session.createCriteria(CondicionCamposPermitidos.class);
			criteria.createAlias("operadorCamposPermitido", "opCamPer");
			criteria.add(Restrictions.eq("opCamPer.id", Long.parseLong(idOpCamPer)));
			lstCondicionCamPer = criteria.list();

			if (!lstCondicionCamPer.isEmpty()) {
				CondicionCamposPermitidos camPer = lstCondicionCamPer.get(0);
				if (camPer.getId() != null) {
					condicionCamPerExists = true;
				}
			}

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}

		return condicionCamPerExists;
	}

}
