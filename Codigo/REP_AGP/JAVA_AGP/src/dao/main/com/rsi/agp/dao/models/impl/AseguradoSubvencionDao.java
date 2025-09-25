package com.rsi.agp.dao.models.impl;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.IAseguradoSubvencionDao;
import com.rsi.agp.dao.tables.cpl.AsegSubvAdicionalEnesa;
import com.rsi.agp.dao.tables.cpl.gan.AseguradosSubvAdicionalRenov;
import com.rsi.agp.dao.tables.poliza.Linea;

public class AseguradoSubvencionDao extends BaseDaoHibernate implements IAseguradoSubvencionDao {

	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings("rawtypes")
	public boolean existeAuditAsegDatMed(Long idpoliza) {
		boolean existeAuditAseg = false;
		try {

			logger.debug("AseguradoSubvencionDao - esisteAuditAsegDatMed[INIT]");
			logger.debug("Valor de idPoliza:" + idpoliza);

			BigDecimal resultado;
			Session session = obtenerSession();

			String sql = "select count(*) from o02agpe0.TB_SW_ASEG_DATOS_MEDIDAS  AsegDat "
					+ "where AsegDat.Idpoliza = " + idpoliza;

			List list = session.createSQLQuery(sql).list();
			resultado = (BigDecimal) list.get(0);

			if (resultado.intValue() == 0)
				existeAuditAseg = true;
			else
				existeAuditAseg = false;

		} catch (Exception e) {
			return false;
		}
		logger.debug("AseguradoSubvencionDao - esisteAuditAsegDatMed[INIT]");
		logger.debug("return:" + existeAuditAseg);
		return existeAuditAseg;
	}

	@SuppressWarnings("rawtypes")
	public void guardaAuditAsegDatosMedida(Long idPoliza, String codUsuario, String xml) throws DAOException {
		
		logger.debug("AseguradoSubvencionDao - guardarAuditAsegDatosMedida[INIT]");
		logger.debug("Valor de idPoliza:" + idPoliza);

		Session session = obtenerSession();

		try {

			List theList = session.createSQLQuery("select SQ_TB_SW_ASEG_DATOS_MEDIDAS_ID.nextval as num from dual")
					.addScalar("num", Hibernate.INTEGER).list();

			int id = (Integer) theList.get(0);

			Date date = new Date();

			String queryString = "INSERT INTO o02agpe0.TB_SW_ASEG_DATOS_MEDIDAS (id,idpoliza,fecha, codusuario, xml) values(:id, :idPoliza, :fecha, :codUsuario, :xml)";
			SQLQuery query = session.createSQLQuery(queryString);
			query.setParameter("id", id);
			query.setParameter("idPoliza", idPoliza);
			query.setParameter("fecha", date);
			query.setParameter("codUsuario", codUsuario);
			query.setParameter("xml", xml);
			query.executeUpdate();

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al dar de alta en la tabla de Auditoria AsegDatosMedida",
					ex);
		}
		logger.debug("AseguradoSubvencionDao - guardarAuditAsegDatosMedida[END]");
	}

	public void guardaAuditCambioSubvs(Long idPoliza, String codUsuario, String detalle) throws DAOException {

		logger.debug("AseguradoSubvencionDao - guardaAuditCambioSubvs[INIT]");

		try {

			Session session = obtenerSession();

			String sql = "INSERT INTO o02agpe0.TB_AUDIT_CAMBIO_SUBVS " + " (id,fecha, idpoliza, codusuario, detalle) "
						+ " values(SQ_TB_AUDIT_CAMBIO_SUBVS_ID.nextval, sysdate ," + idPoliza + ",'" + codUsuario
						+ "' ,'" + (StringUtils.isNullOrEmpty(detalle) ? "No se quitan subvenciones" : detalle) + "')";

			// Insertamos las zonas asignadas a la oficina
			session.createSQLQuery(sql).executeUpdate();

		} catch (Exception ex) {
			throw new DAOException(
					"Se ha producido un error al dar de alta/actualizar en Auditoria de cambios subvenciones", ex);
		}
		logger.debug("AseguradoSubvencionDao - guardarAuditAsegDatosMedida[END]");

	}

	@SuppressWarnings("deprecation")
	public String getXmlAsegDatosMedida(Long idPoliza) throws DAOException {

		String xml = null;
		Session session = obtenerSession();

		try {

			Clob respuesta = null;
			String sql = "select swDat.Xml\r\n" + "  from o02agpe0.tb_sw_aseg_datos_medidas swDat\r\n"
					+ "where swDat.Idpoliza = " + idPoliza;

			logger.info(sql);

			Statement stmt = session.connection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (null != rs) {
				while (rs.next()) {
					respuesta = (Clob) rs.getClob(1);
					xml = WSUtils.convertClob2String(respuesta);
				}
				rs.close();
				stmt.close();

				return xml;
			}
			return xml;
		} catch (Exception e) {
			throw new DAOException(
					"Error al recuperar el xml de Asegurados Datos y Medidas del SW de la póliza: " + idPoliza, e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getControlSubvenciones(List<String> nifsCifs, BigDecimal codPlan, BigDecimal codLinea)
			throws DAOException {
		List<String> result = new ArrayList<String>();
		Criteria crit;
		try {
			Session session = obtenerSession();
			crit = session.createCriteria(Linea.class);
			crit.add(Restrictions.eq("codlinea", codLinea));
			crit.add(Restrictions.eq("codplan", codPlan));
			Linea linea = (Linea) crit.uniqueResult();
			
			if (null!=linea) {
				List<?> subvs = null;
				crit = session.createCriteria(
						linea.isLineaGanado() ? AseguradosSubvAdicionalRenov.class : AsegSubvAdicionalEnesa.class);
				crit.add(Restrictions.in(linea.isLineaGanado() ? "id.nifAsegurado" : "id.nifasegurado", nifsCifs));
				crit.add(Restrictions.eq("id.lineaseguroid", linea.getLineaseguroid()));

				ProjectionList projectionList = Projections.projectionList();
				projectionList.add(Projections.property(linea.isLineaGanado() ? "id.nifAsegurado" : "id.nifasegurado"));

				crit.setProjection(projectionList);
				
				subvs = crit.list();

				if (subvs != null) {
					result = (List<String>) subvs;
					return result;
				}
			}
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
		return null;
	}
}