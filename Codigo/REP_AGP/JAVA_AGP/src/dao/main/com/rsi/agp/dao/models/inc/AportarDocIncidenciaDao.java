package com.rsi.agp.dao.models.inc;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.DocumentosInc;
import com.rsi.agp.dao.tables.inc.Motivos;
import com.rsi.agp.dao.tables.inc.TiposDocInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("unchecked")
public class AportarDocIncidenciaDao extends BaseDaoHibernate implements IAportarDocIncidenciaDao{
	
	private static final String QUERY_LINEA = "SELECT L.CODLINEA FROM O02AGPE0.TB_POLIZAS P INNER JOIN O02AGPE0.TB_LINEAS L ON P.LINEASEGUROID = L.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND L.CODPLAN = :plan AND P.TIPOREF = :tipoReferencia";
	private static final String QUERY_NIFCIF = "SELECT A.NIFCIF FROM O02AGPE0.TB_ASEGURADOS A INNER JOIN O02AGPE0.TB_POLIZAS P ON A.ID = P.IDASEGURADO INNER JOIN O02AGPE0.TB_LINEAS L ON P.LINEASEGUROID = L.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND P.TIPOREF = :tipoReferencia AND L.CODPLAN = :codPlan";
	private static final String QUERY_REFERENCIA = "SELECT P.REFERENCIA FROM O02AGPE0.TB_POLIZAS P INNER JOIN O02AGPE0.TB_ASEGURADOS A ON P.IDASEGURADO = A.ID INNER JOIN O02AGPE0.TB_LINEAS L ON P.LINEASEGUROID = L.LINEASEGUROID WHERE A.NIFCIF = :nifCif AND L.CODPLAN = :codPlan AND L.CODLINEA = :codLinea AND P.TIPOREF = 'P' AND P.REFERENCIA IS NOT NULL";
	private static final String QUERY_DC_POLIZA = "SELECT P.DC FROM O02AGPE0.TB_POLIZAS P INNER JOIN O02AGPE0.TB_LINEAS L ON P.LINEASEGUROID = L.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND L.CODPLAN = :codPlan AND L.CODLINEA = :codLinea AND P.TIPOREF = :tipoRef";
	private static final String QUERY_ID_POLIZA = "SELECT P.IDPOLIZA FROM O02AGPE0.TB_POLIZAS P INNER JOIN O02AGPE0.TB_LINEAS L ON P.LINEASEGUROID = L.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND L.CODPLAN = :codPlan AND L.CODLINEA = :codLinea AND P.TIPOREF = :tipoRef";
	private static final String QUERY_INC = "SELECT I.CODESTADOAGRO FROM O02AGPE0.TB_INC_INCIDENCIAS I WHERE I.IDINCIDENCIA IN (SELECT MIN(INC.IDINCIDENCIA) FROM O02AGPE0.TB_INC_INCIDENCIAS INC WHERE INC.NUMINCIDENCIA = :numIncidencia AND INC.CODPLAN = :codPlan AND INC. CODLINEA = :codLinea AND INC.ANHOINCIDENCIA = :anhoIncidencia  AND INC.CODESTADOAGRO <> :codEstadoAgro AND INC.CODESTADO = :codEstado)";

	
	private static final BigDecimal ASUNTO_ACTIVO = new BigDecimal("1");
	private static final String CATALOGO_POLIZA = "P";
	
	@Override
	public List<DocumentosInc> getDocumentos(Long idIncidencia) throws DAOException {
		try {
			return (List<DocumentosInc>)this.obtenerSession().createCriteria(DocumentosInc.class)
				.createAlias("incidencias", "incidencias")
				.add(Restrictions.eq("incidencias.idincidencia", idIncidencia))
				.list();
		} catch (HibernateException e) {
			throw new DAOException("No se pudo obtener los datos", e);
		}
	}

	@Override
	public List<TiposDocInc> getExtensionesFicherosValidas() throws DAOException {
		try {
			return (List<TiposDocInc>)this.obtenerSession().createCriteria(TiposDocInc.class).list();
		} catch (HibernateException e) {
			throw new DAOException("No se pudo obtener los datos", e);
		}
		
	}

	@Override
	public List<AsuntosInc> getAsuntos() throws DAOException {
		try {
			/* Recuperamos la lista de Asuntos de tipología 'I' -> Incidencia */
			return (List<AsuntosInc>)this.obtenerSession()
					.createCriteria(AsuntosInc.class)
					.add(Restrictions.eq("activo", ASUNTO_ACTIVO))
					.add(Restrictions.eq("id.catalogo", CATALOGO_POLIZA))
					.list();
		} catch (HibernateException e) {
			throw new DAOException("No se pudo obtener los datos", e);
		}
	}
	/* Pet. 57627 ** MODIF TAM (29/10/2019) */
	@Override
	public List<Motivos> getMotivos() throws DAOException {
		try {
			/* Recuperamos la lista de Motivos de Anulación y Rescisión */
			return (List<Motivos>)this.obtenerSession()
					.createCriteria(Motivos.class)
					.add(Restrictions.eq("activo", ASUNTO_ACTIVO))
					.list();
		} catch (HibernateException e) {
			throw new DAOException("No se pudo obtener los datos", e);
		}
	}
	
	@Override
	public VistaIncidenciasAgro getIncidenciasById(Long idIncidencia) throws DAOException {
		
		try {

			logger.debug("init - [aportarDocIncidenciaDao] getIncidenciasById");

			return (VistaIncidenciasAgro)this.obtenerSession().createCriteria(VistaIncidenciasAgro.class)
					.add(Restrictions.eq("idincidencia", idIncidencia))
					.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException("No se pudo obtener los datos", e);
		}

	}
	/* Pet. 57627 ** MODIF TAM (29/10/2019) Fin */

	@Override
	public TiposDocInc getExtension(String extension) throws DAOException {
		try {
			return (TiposDocInc)this.obtenerSession().createCriteria(TiposDocInc.class)
					.add(Restrictions.eq("extension", extension))
					.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException("No se pudo obtener los datos", e);
		}
	}

	@Override
	public BigDecimal getLineaPoliza(String referencia, BigDecimal plan, Character tipoReferencia) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_LINEA);
		query.setString("referencia", referencia);
		query.setBigDecimal("plan", plan);
		query.setCharacter("tipoReferencia", tipoReferencia);
		BigDecimal uniqueResult = (BigDecimal)query.uniqueResult();
		return uniqueResult;
	}

	@Override
	public String getNifCifPoliza(String referencia, Character tipoReferencia, BigDecimal codPlan) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_NIFCIF);
		query.setString("referencia", referencia);
		query.setCharacter("tipoReferencia", tipoReferencia);
		query.setBigDecimal("codPlan", codPlan);
		String uniqueResult = (String)query.uniqueResult();
		return uniqueResult;
	}

	@Override
	public String getReferenciaPoliza(String nifCif, BigDecimal codPlan, BigDecimal codLinea) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_REFERENCIA);
		query.setString("nifCif", nifCif);
		query.setBigDecimal("codPlan", codPlan);;
		query.setBigDecimal("codLinea", codLinea);
		String uniqueResult = (String)query.uniqueResult();
		return uniqueResult;
	}

	@Override
	public BigDecimal getDCPoliza(String referencia, Character tipoRef, BigDecimal codPlan, BigDecimal codLinea) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_DC_POLIZA);
		query.setString("referencia", referencia);
		query.setBigDecimal("codPlan", codPlan);;
		query.setBigDecimal("codLinea", codLinea);
		query.setCharacter("tipoRef", tipoRef);
		BigDecimal uniqueResult = (BigDecimal)query.uniqueResult();
		return uniqueResult;
	}
	
	@Override
	public BigDecimal getIdPoliza(String referencia, Character tipoRef, BigDecimal codPlan, BigDecimal codLinea) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_ID_POLIZA);
		query.setString("referencia", referencia);
		query.setBigDecimal("codPlan", codPlan);;
		query.setBigDecimal("codLinea", codLinea);
		query.setCharacter("tipoRef", tipoRef);
		BigDecimal uniqueResult = (BigDecimal)query.uniqueResult();
		return uniqueResult;
	}
	
	@Override
	public String getCodEstInc(BigDecimal numInc, BigDecimal codPlan, BigDecimal codLinea, BigDecimal anho) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_INC);
		query.setBigDecimal("numIncidencia", numInc);
		query.setBigDecimal("codPlan", codPlan);;
		query.setBigDecimal("codLinea", codLinea);
		query.setBigDecimal("anhoIncidencia", anho);
		query.setString("codEstadoAgro", "B");
		query.setString("codEstado", "1");

		String uniqueResult = (String)query.uniqueResult();
		return uniqueResult;
	}
	
	@Override
	public String getNombLinea(BigDecimal codLinea) throws DAOException {
		return (String)this.obtenerSession()
				.createCriteria(Linea.class)
				.add(Restrictions.eq("codlinea", codLinea))
				.setProjection(Projections.distinct(Projections.property("nomlinea"))).uniqueResult();
	}
	
	public Poliza getPolizaById(Long idPoliza) throws DAOException {
		Poliza poliza;
		try {
			poliza = (Poliza) get(Poliza.class, idPoliza);

			return poliza;

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",e);
		}
	}
	
	/*DNF ESC-8399 18/02/2020*/
	public void actualizarIncidenciaPlanRefYTipo(BigDecimal plan, String referencia, Character tipoRef, Long idIncidencia) {
		
		
		Session session = obtenerSession();
		try {
			String sql = "UPDATE O02AGPE0.TB_INC_INCIDENCIAS INC SET INC.CODPLAN = " +plan+ ", INC.TIPOREF = '" +tipoRef+"', INC.REFERENCIA = '" +referencia+ "' WHERE INC.IDINCIDENCIA = "+ idIncidencia;
			logger.debug("actualizarIncidenciaPlanRefYTipo - sql: " + sql);
			session.createSQLQuery(sql).executeUpdate();
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la incidencia con id: " +idIncidencia, e);
		}	
		return;
	}
	
	
	@SuppressWarnings("deprecation")
	public VistaIncidenciasAgro getPlanRefTipoRefFromIncidenciaById(Long idIncidencia) throws DAOException {
		
		VistaIncidenciasAgro via = new VistaIncidenciasAgro();
		Session session = obtenerSession();
		
		logger.debug("**@@** AportarDocIncidenciaDao.getPlanRefTipoRefFromIncidenciaById");
		try{
			String sql = "SELECT CODPLAN, REFERENCIA, TIPOREF FROM O02AGPE0.TB_INC_INCIDENCIAS WHERE IDINCIDENCIA=" + idIncidencia;
			
			PreparedStatement ps = session.connection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				
				BigDecimal plan = rs.getBigDecimal("CODPLAN");
				String referen     = rs.getString("REFERENCIA");
				Character tipoReferen = rs.getString("TIPOREF").charAt(0);
				
				via.setCodplan(plan);
				via.setTiporef(tipoReferen);
				via.setReferencia(referen);
			}	
			rs.close();
			
			
		}catch(Exception excepcion){
			logger.error("Error al recuperar el plan, tipoRef y referencia",excepcion);
			throw new DAOException("Error al recuperar el plan, tipoRef y referencia",excepcion);
		}
		
		return via;
	}
	
	
	
	/*DNF ESC-8399 18/02/2020*/
	
	
	
	
}
