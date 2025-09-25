package com.rsi.agp.dao.models.poliza;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cgen.CalculoIndemnizacion;
import com.rsi.agp.dao.tables.cgen.CapitalAseguradoElegible;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.MinimoIndemnizableElegible;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoFranquicia;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.pagos.EstadosPago;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;
import com.rsi.agp.dao.tables.pagos.HistoricoPoliza;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.HistoricoEstados;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

import es.agroseguro.iTipos.Ambito;

public class ImportacionPolizasExtDao extends BaseDaoHibernate implements IImportacionPolizasExtDao {

	private static Log logger = LogFactory.getLog(ImportacionPolizasExtDao.class);

	public void saveImportacionPolizaExt(ImportacionPolizasExt envio) throws DAOException {
		Session session = obtenerSession();
		try {

			session.saveOrUpdate(envio);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad", ex);
		} finally {
		}
	}

	public void updateIdEnvio(ImportacionPolizasExt envio) throws DAOException {

		logger.debug("Init: updateIdEnvio - ImportacionPolizasExtDao");
		try {
			StringBuffer updatePar = new StringBuffer("UPDATE TB_IMPORTACION_PLZ_EXT SET IDENVIO = ");
			Session session = obtenerSession();
			updatePar.append(envio.getIdEnvio()).append(" WHERE ID = ").append(envio.getId());

			session.createSQLQuery(updatePar.toString()).executeUpdate();

			logger.debug("Fin: updateIdEnvio - ImportacionPolizasExtDao");
		} catch (Exception e) {
			logger.error("Se ha producido un error al actualizar el id de env�o en TB_IMPORTACION_PLZ_EXT: ", e);
			throw new DAOException();
		}
	}

	public Object saveOrUpdateEntity(Object entity, Session session, Boolean isBatch) throws DAOException {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		try {

			sessionUse.saveOrUpdate(entity);
			return entity;
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error durante el guardado de la entidad", ex);
		} finally {
		}
	}

	public void deleteEntity(Object entity, Session session, Boolean isBatch) throws DAOException {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		try {

			sessionUse.delete(entity);
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error durante el borrado de la entidad", ex);
		} finally {
		}
	}

	// USUARIO
	public Usuario getUsuarioPolizaBBDD(final Session session, final Boolean isBatch, final String usuario,
			final BigDecimal codentidad, final BigDecimal codsubentidad) throws DAOException {

		Usuario usuarioHbm = null;
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		if (isBatch) {
			if(sessionUse == null) {
				sessionUse = this.obtenerSession();
			}
			// OBTENEMOS EL USUARIO DE LA E-S MED (EL PARAMETRO VENDRA VACIO)
			// POR DEFINICION FUNCIONAL OBTENEMOS EL PRIMERO DE PERFIL 1
			Criteria crit = sessionUse.createCriteria(Usuario.class);
			crit.createAlias("subentidadMediadora", "subentidadMediadora");
			crit.add(Restrictions.eq("subentidadMediadora.id.codentidad", codentidad));
			crit.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", codsubentidad));
			crit.add(Restrictions.eq("tipousuario", Constants.PERFIL_1));
			crit.setFirstResult(0);
			crit.setMaxResults(1);
			usuarioHbm = (Usuario) crit.uniqueResult();
		} else {
			// OBTENEMOS EL USUARIO DE BBDD EN FUNCION DEL PARAMETRO
			usuarioHbm = (Usuario) sessionUse.get(Usuario.class, usuario);
		}

		if (usuarioHbm == null) {
			throw new DAOException("No se encuentra el usuario. Revise los datos: codusuario: >>" + usuario
					+ "<<, codentidad: >>" + codentidad + "<<, codsubentidad: >>" + codsubentidad + "<<");
		}

		return usuarioHbm;
	}	

	// LINEA
	public Linea getLineaSeguroBBDD(final BigDecimal codlinea, final BigDecimal codplan, final Session session,
			final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		Linea lineaHbm;

		Criteria crit = sessionUse.createCriteria(Linea.class).add(Restrictions.eq("codlinea", codlinea))
				.add(Restrictions.eq("codplan", codplan)).add(Restrictions.eq("activo", "SI"));

		lineaHbm = (Linea) crit.uniqueResult();
		if (lineaHbm == null) {
			throw new Exception("No se encuentra la linea de seguro. Revise los datos: codlinea " + codlinea
					+ ", codplan " + codplan);
		}
		return lineaHbm;
	}

	// COLECTIVOS
	public Colectivo getColectivoBBDD(final es.agroseguro.contratacion.Colectivo colectivo, final Session session,
			final SubentidadMediadora sm, final Boolean isBatch) throws Exception {
		logger.debug("Dentro de getColectivoBBDD");

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Colectivo colectivoHbm;
		String referencia;
		String dc;
		String msgError = null;

		referencia = colectivo.getReferencia();
		dc = String.valueOf(colectivo.getDigitoControl());

		Criteria crit = sessionUse.createCriteria(Colectivo.class).add(Restrictions.eq("idcolectivo", referencia))
				.add(Restrictions.eq("dc", dc));

		if (sm != null) {
			crit.add(Restrictions.eq("subentidadMediadora", sm));
		}
		colectivoHbm = (Colectivo) crit.uniqueResult();
		if (colectivoHbm == null) {
			msgError = "No se encuentra el colectivo. Revise los datos: idcolectivo: " + referencia + ", dc " + dc;
			if (sm != null && null != sm.getId() && null != sm.getId().getCodentidad()) {
				msgError = msgError + ", entMed: " + sm.getId().getCodentidad().toString();
			}
			if (sm != null && null != sm.getId() && null != sm.getId().getCodsubentidad()) {
				msgError = msgError + ", subEntMed: " + sm.getId().getCodsubentidad().toString();
			}
			throw new Exception(msgError);
		}
		return colectivoHbm;
	}

	// COLECTIVOS
	public Colectivo getColectivoBBDDonline(final es.agroseguro.contratacion.Colectivo colectivo, final Session session,
			final BigDecimal plan, final Boolean isBatch) throws Exception {
		logger.debug("Dentro de getColectivoBBDD -  online");

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Colectivo colectivoHbm;
		String referencia;
		String dc;
		String msgError = null;

		referencia = colectivo.getReferencia();
		dc = String.valueOf(colectivo.getDigitoControl());

		Criteria crit = sessionUse.createCriteria(Colectivo.class).createAlias("linea", "linea")
				.add(Restrictions.eq("idcolectivo", referencia)).add(Restrictions.eq("dc", dc))
				.add(Restrictions.eq("linea.codplan", plan));

		colectivoHbm = (Colectivo) crit.uniqueResult();
		if (colectivoHbm == null) {
			msgError = "No se encuentra el colectivo. Revise los datos: idcolectivo: " + referencia + ", dc: " + dc
					+ ", plan: " + plan;

			throw new Exception(msgError);
		}
		return colectivoHbm;
	}

	/* Tatiana (15.06.2021) ** Inicio */
	public Colectivo getColectivoBBDDGan(final String referencia, final int dc, final Session session)
			throws Exception {

		Colectivo colectivoHbm;

		Criteria crit = session.createCriteria(Colectivo.class).add(Restrictions.eq("idcolectivo", referencia))
				.add(Restrictions.eq("dc", Integer.toString(dc)));

		colectivoHbm = (Colectivo) crit.uniqueResult();

		if (colectivoHbm == null) {
			throw new Exception(
					"No se encuentra el colectivo. Revise los datos: idcolectivo " + referencia + ", dc " + dc);
		}

		return colectivoHbm;
	}

	// ASEGURADOS
	public Asegurado getAseguradoBBDD(final es.agroseguro.contratacion.Asegurado asegurado, final BigDecimal codentidad,
			final Session session, final SubentidadMediadora sm, final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Asegurado aseguradoHbm;
		String nif;
		String msgError;

		nif = String.format("%9s", asegurado.getNif()).replace(' ', '0');
		Criteria crit = sessionUse.createCriteria(Asegurado.class).createAlias("entidad", "entidad")
				.add(Restrictions.eq("nifcif", nif)).add(Restrictions.eq("entidad.codentidad", codentidad));

		// Si se ha informado la ES Mediadora
		if (sm != null) {
			crit.createAlias("usuario", "usuario");
			crit.createAlias("usuario.subentidadMediadora", "usuario.subentidadMediadora");
			crit.add(Restrictions.eq("usuario.subentidadMediadora.id.codentidad", sm.getId().getCodentidad()));
			crit.add(Restrictions.eq("usuario.subentidadMediadora.id.codsubentidad", sm.getId().getCodsubentidad()));
		}

		aseguradoHbm = (Asegurado) crit.uniqueResult();
		if (aseguradoHbm == null) {
			msgError = "No se encuentra el asegurado. Revise los datos: nifcif " + nif + ", codentidad " + codentidad;

			if (sm != null && null != sm.getId() && null != sm.getId().getCodentidad()) {
				msgError = msgError + ", entMed " + sm.getId().getCodentidad().toString();
			}
			if (sm != null && null != sm.getId() && null != sm.getId().getCodsubentidad()) {
				msgError = msgError + ", subEntMed " + sm.getId().getCodsubentidad().toString();
			}
			throw new Exception(msgError);
		}
		return aseguradoHbm;
	}

	// SUBVENCIONES
	public SubvencionEnesa getSubvEnesaBBDD(final BigDecimal tipoSubv, final Long lineaseguroid, final String codModulo,
			final Session session, final Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		try {
			// MPM - Se obtiene el listado de subvenciones y se devuelve la primera para
			// evitar errores al utilizar 'uniqueResult',
			// ya que da igual el registro de TB_SC_C_SUBVS_ENESA que se asocie a la poliza
			// con tal de que sea correcto el
			// codigo de tipo de subvencion
			@SuppressWarnings("unchecked")
			List<SubvencionEnesa> subvEnesaHbm = (List<SubvencionEnesa>) sessionUse
					.createCriteria(SubvencionEnesa.class).createAlias("tipoSubvencionEnesa", "tipoSubvencionEnesa")
					.createAlias("modulo", "modulo")
					.add(Restrictions.eq("tipoSubvencionEnesa.codtiposubvenesa", tipoSubv))
					.add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
					.add(Restrictions.eq("modulo.id.codmodulo", codModulo)).list();

			if (subvEnesaHbm != null && !subvEnesaHbm.isEmpty())
				return subvEnesaHbm.get(0);
		} catch (Exception e) {
			logger.error("Error al obtener las subvenciones de enesa", e);
		}

		return null;
	}

	public SubvencionCCAA getSubvCCAABBDD(final BigDecimal tipoSubv, final Long lineaseguroid, final String codModulo,
			final Session session, Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		try {
			@SuppressWarnings("unchecked")
			List<SubvencionCCAA> subvCCAAHbm = (List<SubvencionCCAA>) sessionUse.createCriteria(SubvencionCCAA.class)
					.createAlias("tipoSubvencionCCAA", "tipoSubvencionCCAA").createAlias("modulo", "modulo")
					.add(Restrictions.eq("tipoSubvencionCCAA.codtiposubvccaa", tipoSubv))
					.add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
					.add(Restrictions.eq("modulo.id.codmodulo", codModulo)).list();

			if (subvCCAAHbm != null && !subvCCAAHbm.isEmpty())
				return subvCCAAHbm.get(0);
		} catch (Exception e) {
			logger.error("Error al obtener las subvenciones de ccaa", e);
		}

		return null;
	}

	// ESTADO POLIZA
	public EstadoPoliza getEstadoPolizaBBDD(final Session session, final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		EstadoPoliza estadoHbm;

		estadoHbm = (EstadoPoliza) sessionUse.get(EstadoPoliza.class, Constants.ESTADO_POLIZA_DEFINITIVA);

		if (estadoHbm == null) {
			throw new Exception("No se encuentra el estado de la poliza. Revise los datos: idEstado "
					+ Constants.ESTADO_POLIZA_DEFINITIVA);
		}
		return estadoHbm;
	}

	// ESTADO POLIZA GANADO
	@SuppressWarnings("rawtypes")
	public EstadoPoliza getEstadoPolizaBBDDGanado(Session session, Boolean isBatch, String refPoliza) throws Exception {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		EstadoPoliza estadoHbm;

		logger.debug("ImportacionPolizaExtDao - getEstadoPolizaBBDDGanado [INIT]");

		BigDecimal estadoRen = new BigDecimal(0);

		/*
		 * Primero buscamos si la p�liza es renovable, es decir si tiene registro en la
		 * tabla de polizas renovables para planes anteriores y obtenemos el estado de
		 * la poliza del plan anterior
		 */
		String sql = "select ren.estado_agroseguro from o02agpe0.tb_polizas_renovables ren"
				+ " where ren.referencia = '" + refPoliza + "' order by ren.plan desc";

		logger.debug("Valor de sql:" + sql);

		List lista = sessionUse.createSQLQuery(sql).list();

		if (lista != null && lista.size() > 0) {
			estadoRen = ((BigDecimal) lista.get(0));

			if (estadoRen.compareTo(new BigDecimal(0)) > 0) {
				/* Obtenemos el estado correspondiente de la p�liza */
				String sqlEst = "select rel.idestado_pol from o02agpe0.tb_relacion_estados rel"
						+ " where rel.idestado_renov = " + estadoRen;

				logger.debug("Valor de sqlEst:" + sqlEst);

				BigDecimal estadoPol = (BigDecimal) sessionUse.createSQLQuery(sqlEst).uniqueResult();
				logger.debug("Valor de estadoPoliza:" + estadoPol);

				estadoHbm = (EstadoPoliza) sessionUse.get(EstadoPoliza.class, estadoPol);

			} else {
				/* Obtenemos el estado de Enviada correcta */
				estadoHbm = (EstadoPoliza) sessionUse.get(EstadoPoliza.class, Constants.ESTADO_POLIZA_DEFINITIVA);
			}

		} else {
			/* Obtenemos el estado de Enviada correcta */
			estadoHbm = (EstadoPoliza) sessionUse.get(EstadoPoliza.class, Constants.ESTADO_POLIZA_DEFINITIVA);
		}

		if (estadoHbm == null) {
			throw new Exception("No se encuentra el estado de la poliza. Revise los datos: idEstado "
					+ Constants.ESTADO_POLIZA_DEFINITIVA);
		}
		return estadoHbm;
	}

	// ESTADO PAGO
	public EstadoPagoAgp getEstadoPagoBBDD(final Session session, final Boolean isBatch) throws Exception {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		EstadoPagoAgp estadoPagoHbm;

		estadoPagoHbm = (EstadoPagoAgp) sessionUse.get(EstadoPagoAgp.class, Constants.POLIZA_PAGADA);
		if (estadoPagoHbm == null) {
			throw new Exception(
					"No se encuentra el estado del pago. Revise los datos: idPagoAgp " + Constants.POLIZA_PAGADA);
		}
		return estadoPagoHbm;
	}

	// COMPARATIVAS
	public Long getSecuenciaComparativa(final Session session, final Boolean isBatch) throws DAOException {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		try {
			String sql = "select o02agpe0.SQ_MODULOS_POLIZA.nextval from dual";
			BigDecimal secuencia = (BigDecimal) sessionUse.createSQLQuery(sql).uniqueResult();
			return secuencia.longValue();
		} catch (Exception e) {
			throw new DAOException("Error al crear la secuencia de la comparativa ", e);
		}
	}

	public String getDescValorCodGarantizado(final Session session, int valor, Map<String, String> colDescripciones,
			Boolean isBatch) {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String clave = "CG" + valor; // CG de codigo garantizaedo
		String res = null;

		if (colDescripciones.containsKey(clave)) {
			res = colDescripciones.get(clave);
		} else {
			res = ((Garantizado) sessionUse.createCriteria(Garantizado.class)
					.add(Restrictions.eq("codgarantizado", BigDecimal.valueOf(valor))).uniqueResult())
							.getDesgarantizado();
			colDescripciones.put(clave, res);
		}
		return res;
	}

	public BigDecimal getFilaModulo(final Long lineaseguroid, final String codmodulo,
			final BigDecimal codconceptoppalmod, final BigDecimal codriesgocubierto, final Session session,
			Map<String, BigDecimal> colFilaModulo, Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		BigDecimal res = null;
		String clave = null;
		if (null != lineaseguroid && null != codmodulo && null != codconceptoppalmod && null != codriesgocubierto) {
			clave = "L" + lineaseguroid.toString() + "M" + codmodulo.toString() + "C" + codconceptoppalmod.toString()
					+ "R" + codriesgocubierto.toString();
		} else {
			clave = "Nulo";
		}

		if (colFilaModulo.containsKey(clave)) {
			res = colFilaModulo.get(clave);
		} else {
			String sql = "SELECT ri.FILAMODULO FROM o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD ri " + "WHERE ri.LINEASEGUROID= "
					+ lineaseguroid + " and ri.CODMODULO='" + codmodulo + "' and ri.CODCONCEPTOPPALMOD= "
					+ codconceptoppalmod + " and ri.CODRIESGOCUBIERTO= " + codriesgocubierto;
			res = (BigDecimal) sessionUse.createSQLQuery(sql).uniqueResult();
			colFilaModulo.put(clave, res);

		}
		return res;
	}

	public String getDescValorCalculoIndemnizacion(final Session session, int valor,
			Map<String, String> colDescripciones, Boolean isBatch) {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String clave = "CI" + valor; // CG de codigo de calculo de indeminizacion
		String res = null;
		if (colDescripciones.containsKey(clave)) {
			res = colDescripciones.get(clave);
		} else {

			res = ((CalculoIndemnizacion) sessionUse.createCriteria(CalculoIndemnizacion.class)
					.add(Restrictions.eq("codcalculo", BigDecimal.valueOf(valor))).uniqueResult()).getDescalculo();
			colDescripciones.put(clave, res);
		}
		return res;
	}

	public String getDescValorPctFranquiciaElegible(final Session session, int valor,
			Map<String, String> colDescripciones, Boolean isBatch) {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String clave = "PFE" + valor; // CG de codigo de Porcentaje de Franquicia Elegible
		String res = null;
		if (colDescripciones.containsKey(clave)) {
			res = colDescripciones.get(clave);
		} else {
			res = ((PctFranquiciaElegible) sessionUse.createCriteria(PctFranquiciaElegible.class)
					.add(Restrictions.eq("codpctfranquiciaeleg", BigDecimal.valueOf(valor))).uniqueResult())
							.getDespctfranquiciaeleg();

			colDescripciones.put(clave, res);
		}
		return res;
	}

	public String getDescValorMinimoIndemnizableElegible(final Session session, int valor,
			Map<String, String> colDescripciones, Boolean isBatch) {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String clave = "MIE" + valor; // CG de codigo de Minimo de Indemnizable Elegible
		String res = null;
		if (colDescripciones.containsKey(clave)) {
			res = colDescripciones.get(clave);
		} else {
			res = ((MinimoIndemnizableElegible) sessionUse.createCriteria(MinimoIndemnizableElegible.class)
					.add(Restrictions.eq("pctminindem", BigDecimal.valueOf(valor))).uniqueResult()).getDesminindem();
			colDescripciones.put(clave, res);
		}
		return res;
	}

	public String getDescValoTipoFranquicia(final Session session, String valor, Map<String, String> colDescripciones,
			Boolean isBatch) {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String clave = null;
		if (null != valor) {
			clave = "TF" + valor; // CG de codigo de Tipo de Franquicia
		} else {
			clave = "TFNulo";// CG de codigo de Tipo de Franquicia
		}

		String res = null;
		if (colDescripciones.containsKey(clave)) {
			res = colDescripciones.get(clave);
		} else {
			res = ((TipoFranquicia) sessionUse.createCriteria(TipoFranquicia.class)
					.add(Restrictions.eq("codtipofranquicia", valor)).uniqueResult()).getDestipofranquicia();
			colDescripciones.put(clave, res);
		}
		return res;
	}

	public String getDescValoCapitalAseguradoElegible(final Session session, int valor,
			Map<String, String> colDescripciones, Boolean isBatch) {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String clave = "CAE" + valor; // CG de codigo de Minimo de Indemnizable Elegible
		String res = null;
		if (colDescripciones.containsKey(clave)) {
			res = colDescripciones.get(clave);
		} else {
			res = ((CapitalAseguradoElegible) sessionUse.createCriteria(CapitalAseguradoElegible.class)
					.add(Restrictions.eq("pctcapitalaseg", BigDecimal.valueOf(valor))).uniqueResult())
							.getDescapitalaseg();
			colDescripciones.put(clave, res);
		}
		return res;
	}

	public List<RiesgoCubiertoModulo> getListaRiesgoCubiertoModulo(final Poliza polizaHbm, final Session session,
			final Boolean isBatch) {

		logger.debug("ImportacionPolizasExtDao - getListaRiesgoCubiertoModulo - init");
		
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		logger.debug("ID.LINEASEGUROID: " + polizaHbm.getLinea().getLineaseguroid().toString());
		logger.debug("ID.CODMODULO: " + polizaHbm.getCodmodulo().toString());

		@SuppressWarnings("unchecked")
		List<RiesgoCubiertoModulo> rCubMobList = (List<RiesgoCubiertoModulo>) sessionUse
				.createCriteria(RiesgoCubiertoModulo.class)
				.add(Restrictions.eq("id.lineaseguroid", polizaHbm.getLinea().getLineaseguroid()))
				.add(Restrictions.eq("id.codmodulo", polizaHbm.getCodmodulo()))
				.add(Restrictions.eq("elegible", Constants.CHARACTER_S)).add(Restrictions.eq("niveleccion", 'C'))
				.list();
		
		logger.debug("ImportacionPolizasExtDao - getListaRiesgoCubiertoModulo - end");

		return rCubMobList;
	}

	// PARCELAS
	public List<DiccionarioDatos> getDiccionarioDatosVariablesParcela(final Long lineaseguroid, final Session session,
			final Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		// Buscamos los datos variables del diccionario de datos con valor
		// en la columna EtiquetaXML que seran los susceptibles de venir
		// poblados en el bean de datos variables de la situacion actual
		@SuppressWarnings("unchecked")
		List<DiccionarioDatos> dicDatosHbmArr = (List<DiccionarioDatos>) sessionUse
				.createCriteria(DiccionarioDatos.class).add(Restrictions.isNotNull("etiquetaxml"))
				.setFetchMode("organizadorInformacions", FetchMode.JOIN)
				.createAlias("organizadorInformacions.ubicacion", "ubicacion")
				.createAlias("organizadorInformacions.uso", "uso").createAlias("organizadorInformacions.linea", "linea")
				.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid))
				.add(Restrictions.eq("ubicacion.codubicacion", OrganizadorInfoConstants.UBICACION_PARCELA_DV))
				.add(Restrictions.eq("uso.coduso", Constants.USO_POLIZA)).list();
		return dicDatosHbmArr;
	}

	// COMISIONES
	@SuppressWarnings("deprecation")
	public CultivosSubentidades getComisionesSubentidades(int plan, int linea, Calendar fechaEfecto, BigDecimal entMed,
			BigDecimal subEntMed, Session session, Boolean isBatch) throws Exception {
		logger.info("Seleccionamos las comisiones en la tabla de comisiones de subentidades por fecha de efecto. ");
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		CultivosSubentidades res = null;
		// Conexion c = new Conexion();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String fecEfecto = df.format(fechaEfecto == null ? new Date() : fechaEfecto.getTime());
		try {
			String sql = "select coms.PCTENTIDAD, coms.PCTMEDIADOR "
					+ "from o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES coms "
					+ "inner join O02AGPE0.TB_COMS_CULTIVOS_SUBS_HIST sh ON coms.id=sh.IDCOMISIONESSUBENT "
					+ "inner join O02AGPE0.TB_LINEAS l on coms.LINEASEGUROID=l.LINEASEGUROID " + "where l.CODPLAN="
					+ plan + " and l.CODLINEA=" + linea + " and coms.CODENTIDAD=" + entMed + " and coms.CODSUBENTIDAD="
					+ subEntMed + " and TO_DATE (sh.FEC_EFECTO,'DD/MM/YYYY') <= TO_DATE ('" + fecEfecto
					+ "','DD/MM/YYYY') "
					+ "and ((coms.fec_baja is null or to_date(current_date,'dd/mm/yyy') < to_date(coms.fec_baja,'dd/mm/yyy')) and to_date(current_date,'dd/mm/yyy') >= trunc(sh.fec_efecto)) "
					+ "order by sh.fec_efecto desc ,sh.fechamodificacion desc";

			logger.debug("plan: " + plan + " - linea: " + linea + " - fecEfecto: " + fecEfecto + " - entMed: " + entMed
					+ " - subEntMed: " + subEntMed);
			logger.debug(sql);

			List<Object> resultado = new ArrayList<Object>();
			PreparedStatement ps = sessionUse.connection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Object[] registro = new Object[2];
				// Recorro los argumentos de la select y los a�ado al array de object que
				// representa el registro
				for (int i = 0; i < 2; i++) {
					registro[i] = rs.getObject(i + 1);
				}
				// Anado el registro al resultado
				resultado.add(registro);
			}
			rs.close();

			// List<Object> resultado = c.ejecutaQuery(sql, 2);

			if (resultado != null && resultado.size() > 0) {
				BigDecimal pctent = (BigDecimal) ((Object[]) resultado.get(0))[0];
				BigDecimal pctmed = (BigDecimal) ((Object[]) resultado.get(0))[1];

				res = new CultivosSubentidades();
				res.setPctentidad(pctent);
				res.setPctmediador(pctmed);
			} else {
				String sql2 = "select coms.PCTENTIDAD, coms.PCTMEDIADOR "
						+ "from o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES coms "
						+ "inner join O02AGPE0.TB_COMS_CULTIVOS_SUBS_HIST sh ON coms.id=sh.IDCOMISIONESSUBENT "
						+ "inner join O02AGPE0.TB_LINEAS l on coms.LINEASEGUROID=l.LINEASEGUROID " + "where l.CODPLAN="
						+ plan + " and l.CODLINEA=999" + " and coms.CODENTIDAD=" + entMed + " and coms.CODSUBENTIDAD="
						+ subEntMed + " and TO_DATE (sh.FEC_EFECTO,'DD/MM/YYYY') <= TO_DATE ('" + fecEfecto
						+ "','DD/MM/YYYY') "
						+ "and ((coms.fec_baja is null or to_date(current_date,'dd/mm/yyy') < to_date(coms.fec_baja,'dd/mm/yyy')) and to_date(current_date,'dd/mm/yyy') >= trunc(sh.fec_efecto)) "
						+ "order by sh.fec_efecto desc ,sh.fechamodificacion desc";
				logger.debug("plan: " + plan + " - linea: 999 - fecEfecto: " + fecEfecto + " - entMed: " + entMed
						+ " - subEntMed: " + subEntMed);
				logger.debug("busqueda por linea generica. - " + sql2);

				List<Object> resultado2 = new ArrayList<Object>();
				PreparedStatement ps2 = sessionUse.connection().prepareStatement(sql2);
				ResultSet rs2 = ps2.executeQuery();
				while (rs2 != null && rs2.next()) {
					Object[] registro2 = new Object[2];
					// Recorro los argumentos de la select y los a�ado al array de object que
					// representa el registro
					for (int i = 0; i < 2; i++) {
						registro2[i] = rs2.getObject(i + 1);
					}
					// Anado el registro al resultado
					resultado2.add(registro2);
				}
				rs.close();
				// List<Object> resultado2 = c.ejecutaQuery(sql2, 2);

				if (resultado2 != null && resultado2.size() > 0) {
					BigDecimal pctent = (BigDecimal) ((Object[]) resultado2.get(0))[0];
					BigDecimal pctmed = (BigDecimal) ((Object[]) resultado2.get(0))[1];

					res = new CultivosSubentidades();
					res.setPctentidad(pctent);
					res.setPctmediador(pctmed);
				}
			}

		} catch (Exception e) {
			logger.error("Error seleccionando las comisiones de subentidades por fecha de efecto.", e);
			throw (e);
		}
		return res;
	}

	// HISTORICO DE ESTADOS
	public void actualizaHistEstado(final Poliza polizaHbm, final Date fechaPago, final Session session,
			final String usuario, final Boolean isBatch) {

		logger.debug("Dentro de actualizaHistEstado [INIT]");
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		HistoricoEstados histEstadoHbm;
		HistoricoPoliza histPolizaHbm;

		histEstadoHbm = new HistoricoEstados();

		if (isBatch) {
			histEstadoHbm.setCodusuario("BATCH");
		} else {
			histEstadoHbm.setCodusuario(usuario);
		}

		histEstadoHbm.setEstado(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR);
		histEstadoHbm.setFecha(fechaPago);
		histEstadoHbm.setId((BigDecimal) sessionUse
				.createSQLQuery("SELECT o02agpe0.sq_polizas_historico_estados.NEXTVAL FROM DUAL").uniqueResult());
		histEstadoHbm.setPoliza(polizaHbm);

		sessionUse.saveOrUpdate(histEstadoHbm);

		histEstadoHbm = new HistoricoEstados();

		histEstadoHbm.setCodusuario(usuario);
		histEstadoHbm.setEstado(Constants.ESTADO_POLIZA_DEFINITIVA);
		histEstadoHbm.setFecha(fechaPago);
		histEstadoHbm.setId((BigDecimal) sessionUse
				.createSQLQuery("SELECT o02agpe0.sq_polizas_historico_estados.NEXTVAL FROM DUAL").uniqueResult());
		histEstadoHbm.setPoliza(polizaHbm);

		sessionUse.saveOrUpdate(histEstadoHbm);

		histPolizaHbm = new HistoricoPoliza();

		histPolizaHbm.setId((BigDecimal) sessionUse
				.createSQLQuery("SELECT o02agpe0.sq_pago_historico_plz.NEXTVAL FROM DUAL").uniqueResult());
		histPolizaHbm.setCodusuario(usuario);
		histPolizaHbm.setEstadosPago((EstadosPago) sessionUse.get(EstadosPago.class,
				BigDecimal.valueOf(Constants.PAGO_CORRECTO.longValue())));
		histPolizaHbm.setEstadosPoliza((EstadosPoliza) sessionUse.get(EstadosPoliza.class,
				BigDecimal.valueOf(Constants.POLIZA_PAGADA.longValue())));
		histPolizaHbm.setFecha(fechaPago);
		histPolizaHbm.setPoliza(polizaHbm);

		sessionUse.saveOrUpdate(histPolizaHbm);

		logger.debug("Dentro de actualizaHistEstado [END]");
	}

	public void guardaSituacionActual(final Poliza polizaHbm, final String xmlText, final Session session) {

		Clob clob;
		Reader reader = null;

		try {

			clob = Hibernate.createClob(xmlText);
			reader = clob.getCharacterStream();
			polizaHbm.setXmlacusecontratacion(Hibernate.createClob(reader, (int) clob.length()));

		} catch (SQLException e) {
			logger.error("Error al guardar el XML de la situacion actual.", e);
		} finally {
			try {
			} catch (Exception ex) {
			}
		}
	}

	public Variedad getVariedad(es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela,
			final Poliza polizaHbm, final Session session, final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Variedad variedadHbm = (Variedad) sessionUse.createCriteria(Variedad.class)
				.add(Restrictions.eq("id.codvariedad", BigDecimal.valueOf(parcela.getCosecha().getVariedad())))
				.add(Restrictions.eq("id.codcultivo", BigDecimal.valueOf(parcela.getCosecha().getCultivo())))
				.add(Restrictions.eq("id.lineaseguroid", polizaHbm.getLinea().getLineaseguroid())).uniqueResult();
		if (variedadHbm == null) {
			throw new Exception("No se encuentra la variedad. Revise los datos: codvariedad "
					+ parcela.getCosecha().getVariedad() + ", codcultivo " + parcela.getCosecha().getCultivo()
					+ ", lineaseguroid " + polizaHbm.getLinea().getLineaseguroid());
		}
		return variedadHbm;
	}

	public Termino getTermino(es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela,
			final Poliza polizaHbm, final Session session, final Boolean isBatch) throws Exception {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Termino terminoHbm = (Termino) sessionUse.createCriteria(Termino.class)
				.add(Restrictions.eq("id.codprovincia", BigDecimal.valueOf(parcela.getUbicacion().getProvincia())))
				.add(Restrictions.eq("id.codcomarca", BigDecimal.valueOf(parcela.getUbicacion().getComarca())))
				.add(Restrictions.eq("id.codtermino", BigDecimal.valueOf(parcela.getUbicacion().getTermino())))
				.add(Restrictions.eq("id.subtermino", "".equals(parcela.getUbicacion().getSubtermino()) ? ' '
						: parcela.getUbicacion().getSubtermino()))
				.uniqueResult();
		if (terminoHbm == null) {
			throw new Exception("No se encuentra el termino. Revise los datos: codprovincia "
					+ parcela.getUbicacion().getProvincia() + ", codcomarca " + parcela.getUbicacion().getComarca()
					+ ", codtermino " + parcela.getUbicacion().getTermino() + ", subtermino "
					+ parcela.getUbicacion().getSubtermino());
		}
		return terminoHbm;
	}

	public TipoCapital getTipoCapital(es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado,
			final Session session, final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		TipoCapital tipoCapitalHbm = (TipoCapital) sessionUse.get(TipoCapital.class,
				BigDecimal.valueOf(capitalAsegurado.getTipo()));
		if (tipoCapitalHbm == null) {
			throw new Exception("No se encuentra el tipo de capital. Revise los datos: codtipocapital "
					+ capitalAsegurado.getTipo());
		}
		return tipoCapitalHbm;
	}

	public Socio getSocio(es.agroseguro.contratacion.declaracionSubvenciones.Socio socio, final Long idAsegurado,
			final Session session, final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		Socio socioHbm = (Socio) sessionUse.createCriteria(Socio.class).add(Restrictions.eq("id.nif", socio.getNif()))
				.add(Restrictions.eq("asegurado.id", idAsegurado)).uniqueResult();

		return socioHbm;
	}

	// Para recuperar la poliza principal al poblar de datos las polizas
	// complementarias
	public Poliza getPolizaPpalBBDD(final BigDecimal plan, final BigDecimal linea, final String referencia,
			final String moduloComp, final Session session, final Boolean isBatch) throws Exception {

		Criteria crit;
		Poliza polizaHbm;
		String moduloPpal;

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		crit = sessionUse.createCriteria(Modulo.class).createAlias("linea", "linea")
				.add(Restrictions.eq("linea.codplan", plan)).add(Restrictions.eq("linea.codlinea", linea))
				.add(Restrictions.eq("id.codmodulo", moduloComp));

		moduloPpal = ((Modulo) crit.uniqueResult()).getCodmoduloasoc();

		if (moduloPpal == null) {
			throw new Exception("No se encuentra el modulo asociado. Revise los datos: plan " + plan + ", linea "
					+ linea + ", modulo complementario " + moduloComp);
		}

		crit = sessionUse.createCriteria(Poliza.class).createAlias("linea", "linea")
				.add(Restrictions.eq("linea.codplan", plan)).add(Restrictions.eq("linea.codlinea", linea))
				.add(Restrictions.eq("referencia", referencia)).add(Restrictions.eq("codmodulo", moduloPpal.trim()));

		polizaHbm = (Poliza) crit.uniqueResult();

		return polizaHbm;
	}

	public void actualizaSbp(final Long idPoliza, final Session session, final Boolean isBatch) throws DAOException {
		Long idSbp = null;
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		try {
			logger.debug("init - hayPolizaSbp: idpolizaPpal= " + idPoliza);
			PolizaSbp polizaSbp = (PolizaSbp) sessionUse.createCriteria(PolizaSbp.class)
					.createAlias("polizaPpal", "polizaPpal").createAlias("estadoPlzSbp", "estadoPlzSbp")
					.createAlias("tipoEnvio", "tipoEnvio").add(Restrictions.eq("tipoEnvio.id", BigDecimal.ONE))
					.add(Restrictions.eq("polizaPpal.idpoliza", idPoliza))
					.add(Restrictions.eq("estadoPlzSbp.idestado", ConstantsSbp.ESTADO_ENVIADA_CORRECTA)).uniqueResult();
			if (polizaSbp != null) {
				idSbp = polizaSbp.getId();
				if (idSbp != null) {
					Query sql = sessionUse
							.createSQLQuery("UPDATE o02agpe0.TB_SBP_POLIZAS SET GEN_SPL_CPL = 'S' WHERE ID = " + idSbp);
					sql.executeUpdate();
				}
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
	}

	public Asegurado getAseguradoBBDDGanado(final String nif, final BigDecimal codentidad, final Session session,
			final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Asegurado aseguradoHbm;
		String nifAseg = nif;

		nifAseg = String.format("%9s", nif.replace(' ', '0'));

		Criteria crit = sessionUse.createCriteria(Asegurado.class).createAlias("entidad", "entidad")
				.add(Restrictions.eq("nifcif", nifAseg)).add(Restrictions.eq("entidad.codentidad", codentidad));

		aseguradoHbm = (Asegurado) crit.uniqueResult();

		if (aseguradoHbm == null) {
			throw new Exception(
					"No se encuentra el asegurado. Revise los datos: nifcif " + nifAseg + ", codentidad " + codentidad);
		}

		return aseguradoHbm;
	}

	@SuppressWarnings("unchecked")
	public Asegurado getAseguradoBBDDGanadoOnline(final String nif, final BigDecimal codentidad, final Session session,
			final Boolean isBatch) throws Exception {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String nifAseg = nif;

		List<Asegurado> lstAsegurado = new ArrayList<Asegurado>();

		nifAseg = String.format("%9s", nif.replace(' ', '0'));

		Criteria crit = sessionUse.createCriteria(Asegurado.class).createAlias("entidad", "entidad")
				.add(Restrictions.eq("nifcif", nifAseg)).add(Restrictions.eq("entidad.codentidad", codentidad));

		lstAsegurado = crit.list();

		if (lstAsegurado.size() > 0) {
			return (Asegurado) lstAsegurado.get(0);
		} else {
			throw new Exception(
					"No se encuentra el asegurado. Revise los datos: nifcif " + nifAseg + ", codentidad " + codentidad);

		}

	}

	/*
	 * Si viene del batch la session tendra valor, si viene del online la session
	 * esta a null
	 */
	public Session getSesssionFromBatchOnline(Boolean isBatch, Session session) {

		Session sessionUse = null;
		if (isBatch) {
			sessionUse = session;
		} else {
			sessionUse = obtenerSession();
		}
		return sessionUse;
	}

	public Termino obtenerTermino(final Session session,
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion, final Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		Termino terminoHbm = new Termino();
		Criteria criteria = sessionUse.createCriteria(Termino.class);
		criteria.add(Restrictions.eq("id.codprovincia", new BigDecimal(explotacion.getUbicacion().getProvincia())));
		criteria.add(Restrictions.eq("id.codcomarca", new BigDecimal(explotacion.getUbicacion().getComarca())));
		criteria.add(Restrictions.eq("id.codtermino", new BigDecimal(explotacion.getUbicacion().getTermino())));
		if (explotacion.getUbicacion().getSubtermino() != null
				&& !explotacion.getUbicacion().getSubtermino().trim().equals("")) {
			criteria.add(Restrictions.eq("id.subtermino",
					new Character(explotacion.getUbicacion().getSubtermino().charAt(0))));
		} else if (explotacion.getUbicacion().getSubtermino() != null) {
			criteria.add(Restrictions.eq("id.subtermino", ' '));
		}
		Ambito ubicacion = explotacion.getUbicacion();
		logger.debug(new StringBuilder("Provincia Explotacion: ").append(ubicacion.getProvincia())
				.append(" | Comarca Explotacion: ").append(ubicacion.getComarca()).append(" | Termito Explotacion: ")
				.append(ubicacion.getTermino()).append(" | Subtermino Explotacion: ")
				.append(ubicacion.getSubtermino()));
		terminoHbm = (Termino) criteria.uniqueResult();
		return terminoHbm;
	}

	public List<Object> getCodConceptoEtiquetaTablaExplotacionesBBDD(final Long lineaseguroid, final Session session,
			final Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String sql = "select distinct o.codconcepto, dd.nomconcepto, dd.etiquetaxml, dd.numtabla "
				+ "from o02agpe0.tb_sc_oi_org_info o, o02agpe0.tb_sc_dd_dic_datos dd "
				+ "where o.codconcepto = dd.codconcepto and " + "o.codubicacion in ("
				+ OrganizadorInfoConstants.UBICACION_EXPLOTACION + ", " + OrganizadorInfoConstants.UBICACION_GRUPO_RAZA
				+ ", " + OrganizadorInfoConstants.UBICACION_CAP_ASEG + ", "
				+ OrganizadorInfoConstants.UBICACION_ANIMALES + ") and o.coduso = "
				+ OrganizadorInfoConstants.USO_POLIZA + "  and o.lineaseguroid = " + lineaseguroid;
		@SuppressWarnings("unchecked")
		List<Object> busqueda = (List<Object>) sessionUse.createSQLQuery(sql).list();

		return busqueda;
	}

	public void actualizarHistoricoPoliza(final Poliza polizaHbm, final Session session, final Boolean isBatch) {

		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);
		String strHis = "";
		if (isBatch) {
			strHis = "insert into o02agpe0.tb_POLIZAS_HISTORICO_ESTADOS values"
					+ "(o02agpe0.sq_POLIZAS_HISTORICO_ESTADOS.nextval," + polizaHbm.getIdpoliza() + ",'BATCH',sysdate,"
					+ polizaHbm.getEstadoPoliza().getIdestado() + ",null,null,null,null,null)";
		} else {
			strHis = "insert into o02agpe0.tb_POLIZAS_HISTORICO_ESTADOS values"
					+ "(o02agpe0.sq_POLIZAS_HISTORICO_ESTADOS.nextval," + polizaHbm.getIdpoliza() + ", '"
					+ polizaHbm.getUsuario().getCodusuario() + "',sysdate," + polizaHbm.getEstadoPoliza().getIdestado()
					+ ",null,null,null,null,null)";
		}
		Query qHis = sessionUse.createSQLQuery(strHis);
		qHis.executeUpdate();
	}

	public void guardarAuditoriaImportacionPoliza(Boolean isBatch, BigDecimal codPlan, String refPoliza, String xml,
			String usuario) throws DAOException {

		logger.info("ImportacionPolRenovableDao - grabaAuditoriaSWPolRenovable [INIT]");

		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
			String fechahoy = sdf.format(new Date());

			Query queryInsert = session.createSQLQuery(
					"insert into o02agpe0.TB_SW_CONS_CONTRATACION values (o02agpe0.SQ_SW_CONS_CONTRATACION.nextval, "
							+ ":CODPLAN, :REFERENCIA, :RESPUESTA, :USUARIO, to_date(:FECHAHOY, 'dd/MM/yyyy HH24:MI:ss') )")
					.setBigDecimal("CODPLAN", codPlan).setString("REFERENCIA", refPoliza).setString("RESPUESTA", xml)
					.setString("USUARIO", usuario).setString("FECHAHOY", fechahoy);
			queryInsert.executeUpdate();

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw new DAOException("Error al actualizar la tabla de auditoria de TB_CONS_CONTRATACION ", e);
		} finally {
			if (session != null)
				session.close();
		}

		logger.info("ParcelasModificacionManager - guardarXmlEnvio [END]");

	}

	/* Tatiana (24.05.2021) ** Inicio */
	public boolean existePolizaHbm(int codplan, String referencia, Character tipoRef) {

		Session session = obtenerSession();

		Poliza polizaHbm = null;
		Criteria crit = session.createCriteria(Poliza.class).createAlias("linea", "linea")
				.add(Restrictions.eq("linea.codplan", BigDecimal.valueOf(codplan)))
				.add(Restrictions.eq("referencia", referencia)).add(Restrictions.eq("tipoReferencia", tipoRef));

		logger.debug("plan: " + codplan + " - referencia: " + referencia + " - tipoRef: " + tipoRef);

		polizaHbm = (Poliza) crit.uniqueResult();

		return (null != polizaHbm);
	}
	/* Tatiana (24.05.2021) */

	/* P0063482 ** MODIF TAM (14.06.2021) Resoluci�n Incidencia importes Ganado */
	/**
	 * Devuelve la FilaModulo asociada a la linea, modulo y concepto cubierto
	 * elegible indicado en los parametros
	 */
	@SuppressWarnings("rawtypes")
	public BigDecimal getFilaModuloGanado(Long lineaseguroid, String codModulo, BigDecimal codConceptopplaMod,
			BigDecimal codRiesgoCub) {
		try {
			Session session = obtenerSession();
			String sql = "select cc.filamodulo " + " from " + " o02agpe0.tb_sc_c_caract_modulo      cc, "
					+ " o02agpe0.tb_sc_c_concepto_cbrto_mod cm, " + " o02agpe0.tb_sc_c_riesgo_cbrto_mod_g rcm "
					+ " where cc.lineaseguroid = " + lineaseguroid + " " + " and cc.codmodulo = '" + codModulo + "' "
					+ " and cc.tipovalor = 'E' " + " and cc.lineaseguroid = cm.lineaseguroid "
					+ " and cc.codmodulo = cm.codmodulo " + " and cc.columnamodulo = cm.columnamodulo "
					+ " and rcm.lineaseguroid = cc.lineaseguroid " + " and rcm.filamodulo = cc.filamodulo "
					+ " and rcm.codmodulo = cc.codmodulo " + " and rcm.codconceptoppalmod = " + codConceptopplaMod
					+ " and rcm.codriesgocubierto = " + codRiesgoCub
					+ " and cm.codconceptocbrtomod  in ( select cm.codconceptocbrtomod"
					+ " from o02agpe0.tb_sc_c_concepto_cbrto_mod cm where cm.lineaseguroid=" + lineaseguroid + " "
					+ " and cm.codmodulo= '" + codModulo + "')";
			List lista = session.createSQLQuery(sql).list();
			if (lista != null && lista.size() > 0) {
				return ((BigDecimal) session.createSQLQuery(sql).list().get(0));
			}
			logger.debug(
					"no se ha encontrado filaModulo para lineaseguroid = " + lineaseguroid + " codmodulo =" + codModulo
							+ " codconceptoppalmod = " + codConceptopplaMod + " codriesgocubierto = " + codRiesgoCub);
		} catch (Exception e) {
			logger.error("Error al obtener la filamodulo", e);
		}

		return new BigDecimal(-1);
	}

	public BigDecimal getfilaRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal cPmodulo,
			BigDecimal codRiesgoCub) throws Exception {
		logger.debug("init - [RiesgoCubiertoModuloDao] getfilaRiesgoCubModulo");
		Session session = obtenerSession();
		try {
			String sql = "select c.filamodulo from tb_sc_c_riesgo_cbrto_mod_g c where c.LINEASEGUROID = "
					+ lineaseguroid + " and c.CODMODULO = " + codmodulo + " and c.CODCONCEPTOPPALMOD = " + cPmodulo
					+ " and c.CODRIESGOCUBIERTO = " + codRiesgoCub;
			SQLQuery query = session.createSQLQuery(sql);

			return ((BigDecimal) query.uniqueResult());
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del Riesgo cubierto elegido: getRiesgoCubiertosModulo()", e);
			throw e;
		}
	}

	/**
	 * Devuelve la descripcion del Garantizado asociado al codigo indicado por
	 * parametro
	 */
	@SuppressWarnings("unchecked")
	public String getDesGarantizado(BigDecimal codGarantizado) {

		List<Garantizado> objects = this.getObjects(Garantizado.class, "codgarantizado", codGarantizado);

		if (objects != null && objects.size() > 0) {
			return objects.get(0).getDesgarantizado();
		}

		return "";
	}

	/**
	 * Devuelve la descripcion del CalculoIndemnizacion asociado al codigo
	 * indicado por parametro
	 */
	@SuppressWarnings("unchecked")
	public String getDesCalcIndem(BigDecimal codcalculo) {

		List<CalculoIndemnizacion> objects = this.getObjects(CalculoIndemnizacion.class, "codcalculo", codcalculo);

		if (objects != null && objects.size() > 0) {
			return objects.get(0).getDescalculo();
		}

		return "";
	}

	/**
	 * Devuelve la descripcion del PctFranquiciaElegible asociado al codigo
	 * indicado por parametro
	 */
	@SuppressWarnings("unchecked")
	public String getDesPctFranquicia(BigDecimal codpctfranquiciaeleg) {

		List<PctFranquiciaElegible> objects = this.getObjects(PctFranquiciaElegible.class, "codpctfranquiciaeleg",
				codpctfranquiciaeleg);

		if (objects != null && objects.size() > 0) {
			return objects.get(0).getDespctfranquiciaeleg();
		}

		return "";
	}

	/**
	 * Devuelve la descripcion del MinimoIndemnizableElegible asociado al codigo
	 * indicado por parametro
	 */
	@SuppressWarnings("unchecked")
	public String getDesMinIndem(BigDecimal pctminindem) {

		List<MinimoIndemnizableElegible> objects = this.getObjects(MinimoIndemnizableElegible.class, "pctminindem",
				pctminindem);

		if (objects != null && objects.size() > 0) {
			return objects.get(0).getDesminindem();
		}

		return "";
	}

	/**
	 * Devuelve la descripcion del TipoFranquicia asociado al codigo indicado por
	 * parametro
	 */
	@SuppressWarnings("unchecked")
	public String getDesTipoFranqIndem(String codtipofranquicia) {

		List<TipoFranquicia> objects = this.getObjects(TipoFranquicia.class, "codtipofranquicia", codtipofranquicia);

		if (objects != null && objects.size() > 0) {
			return objects.get(0).getDestipofranquicia();
		}

		return "";
	}

	/**
	 * Devuelve la descripcion del CapitalAseguradoElegible asociado al codigo
	 * indicado por parametro
	 */
	@SuppressWarnings("unchecked")
	public String getDesCapitalAseg(BigDecimal pctcapitalaseg) {

		List<CapitalAseguradoElegible> objects = this.getObjects(CapitalAseguradoElegible.class, "pctcapitalaseg",
				pctcapitalaseg);

		if (objects != null && objects.size() > 0) {
			return objects.get(0).getDescapitalaseg();
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	public Object[] obtenerSubEntColectivo(Colectivo col, int codPlan, Session session, Boolean isBatch)
			throws Exception {
		Session sessionUse = getSesssionFromBatchOnline(isBatch, session);

		String dc = col.getDc();
		String idCol = col.getIdcolectivo();
		try {
			String sql = "Select col.entmediadora, col.subentmediadora " + " from o02agpe0.tb_colectivos col"
					+ " inner join o02agpe0.tb_lineas lin on lin.lineaseguroid = col.lineaseguroid "
					+ " where col.idcolectivo = " + idCol + "and col.dc = '" + dc + "' and lin.codplan = " + codPlan;
			logger.debug("Buscamos Ent-Subent Mediadora del colectivo para el idColectivo: " + idCol + " - dc: " + dc
					+ " y plan: " + codPlan);

			logger.debug(sql);

			List<Object[]> busqueda = sessionUse.createSQLQuery(sql).list();
			return busqueda.get(0);
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del Riesgo cubierto elegido: getRiesgoCubiertosModulo()", e);
			throw e;
		}

	}

	/* ESC-15182 ** MODIF TAM (20.09.2021) ** Inicio */
	/**
	 * M�todo que obtiene un mapa cuya clave es el c�digo de concepto de los datos
	 * variables de explotaciones y el valor es un objeto que contiene la etiqueta y
	 * la tabla asociadas al concepto.
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/l�nea
	 * @return Mapa con la informaci�n asociada a cada c�digo de concepto de los
	 *         datos variables de explotaciones
	 */
	public Map<BigDecimal, com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(
			final Long lineaseguroid) {
		String sql = "select distinct o.codconcepto, dd.nomconcepto, dd.etiquetaxml, dd.numtabla "
				+ "from o02agpe0.tb_sc_oi_org_info o, o02agpe0.tb_sc_dd_dic_datos dd "
				+ "where o.codconcepto = dd.codconcepto and " + "o.codubicacion in ("
				+ OrganizadorInfoConstants.UBICACION_EXPLOTACION + ", " + OrganizadorInfoConstants.UBICACION_GRUPO_RAZA
				+ ", " + OrganizadorInfoConstants.UBICACION_CAP_ASEG + ", "
				+ OrganizadorInfoConstants.UBICACION_ANIMALES + ") and o.coduso = "
				+ OrganizadorInfoConstants.USO_POLIZA + "  and o.lineaseguroid = " + lineaseguroid;
		@SuppressWarnings("unchecked")
		List<Object> busqueda = (List<Object>) this.getObjectsBySQLQuery(sql);
		// Recorro la lista y voy rellenando el mapa
		Map<BigDecimal, com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla> resultado = new HashMap<BigDecimal, com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla>();
		for (Object elem : busqueda) {
			Object[] elemento = (Object[]) elem;
			com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla ret = new com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService.RelacionEtiquetaTabla(
					StringUtils.nullToString(elemento[2]), StringUtils.nullToString(elemento[3]),
					StringUtils.nullToString(elemento[1]));
			resultado.put(new BigDecimal(elemento[0] + ""), ret);
		}
		return resultado;
	}

	public short getFilaExplotacionCobertura(Long lineaSeguroId, String modulo, int conceptoPpalMod, int riesgoCubierto)
			throws DAOException {

		String sql = null;
		BigDecimal resBigD = null;
		short res = 0;
		try {
			sql = "select rc.FILAMODULO from TB_SC_C_RIESGO_CBRTO_MOD_G rc "
					+ "inner join TB_LINEAS lin ON rc.LINEASEGUROID = lin.LINEASEGUROID " + "WHERE lin.LINEASEGUROID = "
					+ lineaSeguroId + " AND rc.CODMODULO = '" + modulo + "' AND rc.CODCONCEPTOPPALMOD = "
					+ conceptoPpalMod + " AND rc.CODRIESGOCUBIERTO = " + riesgoCubierto + "  AND rc.NIVELECCION='D'";

			logger.debug(sql);
			Session session = obtenerSession();

			resBigD = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			if (null != resBigD)
				res = resBigD.shortValue();
			return res;
		} catch (Exception ex) {
			logger.error("ImportacionPolizasExtDao.getFilaExplotacionCobertura. - ", ex);
			throw new DAOException("ImportacionPolizasExtDao.getFilaExplotacionCobertura. - ", ex);
		}

	}

	public String getDescripcionConceptoPpalMod(int conceptoPpalMod) throws DAOException {
		String sql = null;
		String res = null;
		try {
			sql = "SELECT DESCONCEPTOPPALMOD from TB_SC_C_CONCEPTO_PPAL_MOD " + "WHERE CODCONCEPTOPPALMOD = "
					+ conceptoPpalMod;

			logger.debug(sql);
			Session session = obtenerSession();

			res = (String) session.createSQLQuery(sql).uniqueResult();

			return res;
		} catch (Exception ex) {
			logger.error("ImportacionPolizasExtDao.getDescripcionConceptoPpalMod. - ", ex);
			throw new DAOException("ImportacionPolizasExtDao.getDescripcionConceptoPpalMod. - ", ex);
		}

	}

	public String getDescripcionRiesgoCubierto(Long lineaSeguroId, String modulo, int riesgoCubierto)
			throws DAOException {
		String sql = null;
		String res = null;
		try {
			sql = "SELECT rc.DESRIESGOCUBIERTO FROM TB_SC_C_RIESGOS_CUBIERTOS  rc "
					+ "INNER JOIN TB_LINEAS lin on rc.LINEASEGUROID = lin.LINEASEGUROID " + "WHERE lin.LINEASEGUROID = "
					+ lineaSeguroId + " AND rc.CODMODULO = '" + modulo + "' AND rc.CODRIESGOCUBIERTO = "
					+ riesgoCubierto;

			logger.debug(sql);
			Session session = obtenerSession();

			res = (String) session.createSQLQuery(sql).uniqueResult();

			return res;
		} catch (Exception ex) {
			logger.error("CargaExplotacionesDao.getDescripcionRiesgoCubierto. - ", ex);
			throw new DAOException("CargaExplotacionesDao.getDescripcionRiesgoCubierto. - ", ex);
		}

	}
	/* ESC-15182 ** MODIF TAM (20.09.2021) ** Fin */

	@SuppressWarnings("unchecked")
	public List<OrganizadorInformacion> obtenerlistOrgInformacion(Filter filter, Boolean isBatch, Session session) {

		List<OrganizadorInformacion> ioList = (List<OrganizadorInformacion>) getOListOrg(filter, isBatch, session);
		return ioList;
	}

	@SuppressWarnings("rawtypes")
	public List getOListOrg(Filter filter, boolean isBatch, Session sessionUse) {

		Session session = getSesssionFromBatchOnline(isBatch, sessionUse);
		return filter.getCriteria(session).list();
	}

	/*
	 * P0063482 ** MODIF TAM (14.06.2021) Resoluci�n Incidencia importes Ganado *
	 * Fin
	 */

}
