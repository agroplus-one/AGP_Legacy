package com.rsi.agp.core.jmesa.dao.impl;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.LineaGrupoNegocio;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableHistoricoEstados;

public class ImportacionPolRenovableDao extends BaseDaoHibernate implements IImportacionPolRenovableDao {

	private static final Log logger = LogFactory.getLog(ImportacionPolRenovableDao.class);
	public static final int EST_AGPLUS_PEND_ASIGNAR_GASTOS = 1;

	public boolean existePolRenovable(Long codPlan, Long codLinea, String refPolizaRen) throws Exception {

		Session session = obtenerSession();
		PolizaRenovable polizaHbm;

		// Buscamos que no exista ya en BBDD
		Criteria crit = session.createCriteria(PolizaRenovable.class)
				.add(Restrictions.eq("plan", Long.valueOf(codPlan)))
				.add(Restrictions.eq("linea", Long.valueOf(codLinea))).add(Restrictions.eq("referencia", refPolizaRen));

		polizaHbm = (PolizaRenovable) crit.uniqueResult();

		if (polizaHbm == null) {
			logger.info("## -----------" + refPolizaRen + ": NO EXISTE----------- ##");
			return false;
		} else {
			logger.info("## -----------" + refPolizaRen + ": EXISTE----------- ##");
			return true;
		}
	}

	@Override
	public void grabaAuditoriaSWPolRenovable(Long codPlan, Long codLinea, String referencia, String codUsuario,
			String xml) throws DAOException {

		logger.info("ImportacionPolRenovableDao - grabaAuditoriaSWPolRenovable [INIT]");

		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
			String fechahoy = sdf.format(new Date());

			Query queryInsert = session
					.createSQLQuery(
							"INSERT INTO o02agpe0.TB_SW_LISTA_POL_RENOVABLE VALUES (SQ_SW_LISTA_POL_RENOVABLE.NEXTVAL,"
									+ ":CODLINEA, :CODPLAN, :REFERENCIA, :RESPUESTA, :USUARIO, to_date(:FECHAHOY, 'dd/MM/yyyy HH24:MI:ss') )")
					.setLong("CODLINEA", codLinea).setLong("CODPLAN", codPlan).setString("REFERENCIA", referencia)
					.setString("RESPUESTA", xml).setString("USUARIO", codUsuario).setString("FECHAHOY", fechahoy);
			queryInsert.executeUpdate();

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw new DAOException("Error al actualizar la tabla de auditoria de TB_SW_LISTA_POL_RENOVABLE ", e);
		} finally {
			if (session != null)
				session.close();
		}

		logger.info("ParcelasModificacionManager - guardarXmlEnvio [END]");
	}

	@Override
	public ColectivosRenovacion obtenerColectivoPlanAnt(final String refColectivo, BigDecimal codPlan, BigDecimal codLinea)
			throws DAOException {

		ColectivosRenovacion colRen = new ColectivosRenovacion();
		Session session = obtenerSession();

		String sql = "select col.codentidad, col.entmediadora, col.subentmediadora, col.dc, lin.codlinea "
				+ "  from o02agpe0.tb_colectivos col "
				+ "            inner join o02agpe0.tb_lineas lin on lin.lineaseguroid = col.lineaseguroid "
				+ " where col.idcolectivo = " + refColectivo + " and lin.codplan < " + codPlan
				+ " and lin.codlinea = " + codLinea 
				+ " order by lin.codplan desc";

		@SuppressWarnings("rawtypes")
		List resultado = session.createSQLQuery(sql).list();

		if (!resultado.isEmpty()) {
			for (Object obj : resultado) {
				Object[] registro = (Object[]) obj;
				
				BigDecimal entidad = (BigDecimal) registro[0];
				BigDecimal entMed = (BigDecimal) registro[1];
				BigDecimal subEnt = (BigDecimal) registro[2];
				String dc = (String) registro[3];
				BigDecimal linea = (BigDecimal) registro[4];
				
				colRen.setCodentidad(entidad.longValue());
				colRen.setCodentidadmed(entMed.longValue());
				colRen.setCodlinea(linea.longValue());
				colRen.setCodsubentmed(subEnt.longValue());
				colRen.setDc(dc.charAt(0));
				colRen.setReferencia(refColectivo);
				break;
			}
		} else {
			return null;
		}

		return colRen;
	}

	@Override
	public ColectivosRenovacion obtenerColectivoRen(final String refColectivo) throws DAOException {
		Session session = obtenerSession();
		ColectivosRenovacion colHbm = new ColectivosRenovacion();

		Criteria critCol = session.createCriteria(ColectivosRenovacion.class);
		critCol.add(Restrictions.eq("referencia", refColectivo));
		colHbm = (ColectivosRenovacion) critCol.uniqueResult();

		return colHbm;

	}

	@Override
	public boolean guardarColectivoRen(final ColectivosRenovacion colHbmRen) throws DAOException {
		Session session = obtenerSession();
		boolean isGrabado = false;

		try {

			session.saveOrUpdate(colHbmRen);
			isGrabado = true;

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al guardar el colectivo renovable", ex);
		} finally {
		}
		logger.info("Fin guardar Colectivo Renovable()");
		return isGrabado;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, BigDecimal[]> getParamsComis(final Session sessionAux, BigDecimal codLinea, Long codPlan,
			Long entMed, Long subEntMed, Date fechaRenov, boolean batch) throws Exception {
		logger.info("************************************************");
		logger.info("ImportacionPolRenovableDao - getParamsComis(new), INIT");

		List registros = new ArrayList();
		List resultado = new ArrayList();

		List<CultivosEntidades> lstParam = new ArrayList<CultivosEntidades>();
		boolean comprobarConNulos = true;

		Session session = null;

		if (!batch) {
			session = obtenerSession();
		} else {
			session = sessionAux;
		}

		BigDecimal pctAdmTemp = null;
		BigDecimal pctAdqTemp = null;
		boolean tieneGrGenerico = false;
		boolean informado = true;
		Map<String, List<Character>> mapGrLineas = new HashMap<String, List<Character>>();
		Map<String, BigDecimal[]> mapLineaGrPctsFinal = new HashMap<String, BigDecimal[]>();

		Map<String, BigDecimal[]> mapLineaGrPcts = new HashMap<String, BigDecimal[]>();

		String fechaParaComparar = "current_date";

		int anio = codPlan.intValue();

		List<LineaGrupoNegocio> lstLineasGrNg = getGruposNegocioPorLinea(session, anio);

		for (LineaGrupoNegocio linGr : lstLineasGrNg) {
			if (!mapGrLineas.containsKey(linGr.getLinea().getCodlinea() + "_" + anio)) {
				List<Character> lstGruposN = new ArrayList<Character>();
				mapGrLineas.put(linGr.getLinea().getCodlinea() + "_" + anio, lstGruposN);
				lstGruposN.add(linGr.getId().getGrupoNegocio());

			} else {
				List<Character> lstGr = mapGrLineas.get(linGr.getLinea().getCodlinea() + "_" + anio);
				if (!lstGr.contains(linGr.getId().getGrupoNegocio())) {
					lstGr.add(linGr.getId().getGrupoNegocio());
				}
			}
		}

		if (fechaRenov != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yy");
			fechaParaComparar = "'" + df.format(fechaRenov) + "'".trim();
		}

		try {
			logger.info("ImportacionPolRenovableDao- getParamsComis(new), Buscampos por entMed y subEntMed primero");

			/*
			 * Primero comprobamos si hay comisiones específicas por Entidad Mediadora y
			 * Subentidad Mediadora
			 */
			String sql = " SELECT h.pctrga,h.pctadquisicion,h.pctgeneralentidad, e.GRUPO_NEGOCIO "
					+ "FROM o02agpe0.tb_coms_cultivos_entidades e "
					+ "INNER JOIN o02agpe0.tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "
					+ "INNER JOIN o02agpe0.tb_lineas lin  ON  lin.lineaseguroid = e.lineaseguroid "
					+ "WHERE e.ENTMEDIADORA = " + entMed + " and e.SUBENTMEDIADORA = " + subEntMed
					+ " and ((e.fec_baja is null or to_date(" + fechaParaComparar
					+ ",'dd/mm/yy') < to_date(e.fec_baja,'dd/mm/yy')) " + " and to_date(" + fechaParaComparar
					+ ",'dd/mm/yy') >= h.fecha_efecto) " + " and lin.codlinea = " + codLinea + " and lin.codPlan = "
					+ codPlan + " order by e.GRUPO_NEGOCIO DESC, h.fecha_efecto  desc ,h.fechamodificacion desc";
			logger.info("ImportacionPolRenovableDao- getParamsComis(new), sql(1): " + sql);

			registros.addAll(session.createSQLQuery(sql).list());

			// SI LA SENTENCIA DEVUELVE EL GRUPO DE NEGOCIO GENERICO O TANTOS GRUPOS COMO
			// TIENE LA LINEA
			// NO ES NECESARIO SEGUIR BUSCANDO. SI NO, DEBEMOS BUSCAR AQUELLOS GRUPOS QUE NO
			// SE HAYAN OBTENIDO YA.
			String gnNotIn = "";
			if (registros.size() > 0) {
				logger.info(
						"ImportacionPolRenovableDao- getParamsComis(new), Se encuentran comisiones especificas por entMed y SubeEntMed");
				String gnPrimero;
				Object[] paramsGen = null;
				paramsGen = (Object[]) registros.get(0);
				gnPrimero = ((String) paramsGen[3]);
				if (gnPrimero.equals(Constants.GRUPO_NEGOCIO_GENERICO.toString())
						|| registros.size() == lstLineasGrNg.size()) {
					comprobarConNulos = false;
				} else {
					for (int k = 0; k < registros.size(); k++) {
						gnNotIn += (String) ((Object[]) registros.get(k))[3];
						if (k < registros.size() - 1)
							gnNotIn += ",";
					}
				}
			}

			if (comprobarConNulos) {

				logger.info("ImportacionPolRenovableDao- getParamsComis(new), Buscamos por generico");

				sql = " SELECT h.pctrga,h.pctadquisicion,h.pctgeneralentidad, e.GRUPO_NEGOCIO "
						+ " FROM o02agpe0.tb_coms_cultivos_entidades e "
						+ " INNER JOIN o02agpe0.tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "
						+ " INNER JOIN o02agpe0.tb_lineas lin  ON  lin.lineaseguroid = e.lineaseguroid "
						+ " WHERE e.ENTMEDIADORA is null and e.SUBENTMEDIADORA is null "
						+ " and ((e.fec_baja is null or to_date(sysdate,'dd/mm/yy')  < to_date(e.fec_baja,'dd/mm/yy'))"
						+ " and to_date(sysdate,'dd/mm/yy') >= h.fecha_efecto) " + " and lin.codlinea = " + codLinea
						+ " and lin.codPlan = " + codPlan
						+ (StringUtils.isNullOrEmpty(gnNotIn) ? "" : " and e.GRUPO_NEGOCIO not in(" + gnNotIn + ")")
						+ " order by h.fecha_efecto  desc ,h.fechamodificacion desc";
				logger.info("ImportacionPolRenovableDao- getParamsComis(new), sql(2): " + sql);

				registros.addAll(session.createSQLQuery(sql).list());
			}

			// Nos quedamos con el primer registro de cada grupo de negocio
			List gn = new ArrayList<String>();
			String gnActual;

			if (registros.size() > 0) {
				for (int i = 0; i < registros.size(); i++) {

					Object[] paramsGen = null;
					paramsGen = (Object[]) registros.get(i);
					gnActual = ((String) paramsGen[3]);

					if (!gn.contains(gnActual)) {
						resultado.add(registros.get(i));
						gn.add(gnActual);

						// inserto nuevo cultivoEntidad en la lista
						CultivosEntidades param = new CultivosEntidades();

						GruposNegocio grupN = new GruposNegocio();
						grupN.setGrupoNegocio(gnActual.charAt(0));
						param.setGrupoNegocio(grupN);

						Linea lin = new Linea();
						lin.setCodlinea(codLinea);
						lin.setCodplan(new BigDecimal(codPlan));
						param.setLinea(lin);

						param.setPctadministracion((BigDecimal) paramsGen[0]);
						param.setPctadquisicion((BigDecimal) paramsGen[1]);

						logger.info("ImportacionPolRenovableDao- getParamsComis(new), Valor asignado Pctadminitracion: "
								+ param.getPctadministracion().toString());
						logger.info("ImportacionPolRenovableDao- getParamsComis(new), Valor asignado Pctadquisicion: "
								+ param.getPctadquisicion().toString());
						lstParam.add(param);
					}
					if (gnActual.equals(Constants.GRUPO_NEGOCIO_GENERICO.toString())) {
						break;
					}
				}
			}

			// 3.1 Montamos un mapa con las comisiones para la línea de la póliza grupo de
			// negocio
			for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param : lstParam) {
				if (param.getLinea() != null && param.getLinea().getCodlinea() != null) {
					// Guardamos por cada linea las comisiones en un mapa
					BigDecimal[] pctComs;
					pctComs = new BigDecimal[2];
					pctComs[0] = param.getPctadministracion();
					pctComs[1] = param.getPctadquisicion();
					// indexo tambien el grupo negocio en la key del mapa: linea + grupoNegocio
					mapLineaGrPcts.put(
							param.getLinea().getCodlinea().toString() + "_" + param.getGrupoNegocio().getGrupoNegocio(),
							pctComs);
					logger.info("ImportacionPolRenovableDao- getParamsComis(new), Valor de mapLineaGrPcts: "
							+ mapLineaGrPcts.toString());
				}
			}

			// 3.2 Compruebo si existe el grupo generico para esa linea en nuestro
			// mantenimiento de parametros generales
			for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param : lstParam) {
				if (param.getLinea().getCodlinea().compareTo(codLinea) == 0) {// misma linea
					if (param.getGrupoNegocio().getGrupoNegocio().equals('9')) {
						logger.info("## linea " + codLinea + " con grupoNegocio 9 ##");
						pctAdmTemp = param.getPctadministracion();
						pctAdqTemp = param.getPctadquisicion();
						tieneGrGenerico = true;
					}
				}
			}

			// 3.3 recogemos los grupos de esa linea
			List<Character> lstGr = mapGrLineas.get(codLinea + "_" + codPlan);

			if (lstGr != null) {
				if (tieneGrGenerico) {
					// 3.4.1 SI GENERICO: accedemos al mapa de grupos por linea y creamos las
					// comisiones para todos los grupos excepto el generico
					logger.info("## metemos todos los grupos " + lstGr.toString() + " para linea " + codLinea
							+ " plan " + codPlan + " ##");
					for (Character gr : lstGr) {
						if (!gr.equals('9')) {
							BigDecimal[] pctComs;
							pctComs = new BigDecimal[2];
							pctComs[0] = pctAdmTemp;
							pctComs[1] = pctAdqTemp;
							mapLineaGrPctsFinal.put(codLinea.toString() + "_" + gr, pctComs);
							logger.info(
									"ImportacionPolRenovableDao- getParamsComis(new), Asignamos datos al mapLineasGrcPcts(1)");
						}
					}

				} else {// no es generico
						// 3.4.2 NO GENERICO:

					// compruebo que todos los grupos de la linea tengon informados datos de
					// comisiones
					List<Character> lstGrNoEncontrados = new ArrayList<Character>();
					for (Character gr : lstGr) {
						if (!gr.equals('9')) {
							BigDecimal[] pctComsEncontrados = mapLineaGrPcts.get(codLinea.toString() + "_" + gr);
							if (null == pctComsEncontrados) {
								logger.info("## la linea " + codLinea + " plan: " + codPlan
										+ " NO tiene parametros para el grupoNegocio " + gr
										+ " en el mantenimiento de parametros generales ##");
								lstGrNoEncontrados.add(gr);
								informado = false;
							}
						}
					}

					if (informado) {
						for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param : lstParam) {
							if (param.getLinea().getCodlinea().compareTo(codLinea) == 0) {// misma linea
								BigDecimal[] pctComs;
								pctComs = new BigDecimal[2];
								pctComs[0] = param.getPctadministracion();
								pctComs[1] = param.getPctadquisicion();
								logger.info(
										"ImportacionPolRenovableDao- getParamsComis(new), Asignamos datos al mapLineasGrcPcts(2)");
								// indexo en el mapa las comisiones como key: linea_GrupoNegocio
								mapLineaGrPctsFinal.put(param.getLinea().getCodlinea().toString() + "_"
										+ param.getGrupoNegocio().getGrupoNegocio(), pctComs);
							}
						}
					} // fin if informado
				}
			}
			/* Taty 06.09.2019 Fin */

			logger.info("ImportacionPolRenovableDao- getParamsComis(new), FIN");
			return mapLineaGrPctsFinal;

		} catch (Exception e) {
			logger.error("Error al acceder a bbdd - getParamsGen - BBDDCargaPolRenUtil");
			throw e;
		}

	}

	public List<LineaGrupoNegocio> getGruposNegocioPorLinea(final Session session, final int anio) {

		Criteria crit = session.createCriteria(LineaGrupoNegocio.class);
		crit.createAlias("linea", "lin");
		crit.add(Restrictions.eq("lin.codplan", new BigDecimal(anio)));
		crit.addOrder(Order.asc("lin.lineaseguroid"));
		@SuppressWarnings("unchecked")
		List<LineaGrupoNegocio> lstLineasGrNg = (List<LineaGrupoNegocio>) crit.list();
		return lstLineasGrNg;
	}

	// Metodo que valida que el estado de la poliza asociado a la misma por
	// configuracion
	// este presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	public EstadoRenovacionAgroplus getEstadoPolRenAgroplus(final Session sessionAux, final boolean batch)
			throws Exception {

		Session session = null;

		if (!batch) {
			session = obtenerSession();
		} else {
			session = sessionAux;
		}

		// insertamos el estado a la poliza renovable
		Integer estadoRenAgroplus = EST_AGPLUS_PEND_ASIGNAR_GASTOS;
		EstadoRenovacionAgroplus estadoHbm;
		Criteria crit = session.createCriteria(EstadoRenovacionAgroplus.class)
				.add(Restrictions.eq("codigo", estadoRenAgroplus.longValue()));
		estadoHbm = (EstadoRenovacionAgroplus) crit.uniqueResult();
		if (estadoHbm == null) {
			throw new Exception("No se encuentra el estado de la poliza renovable. Revise los datos: idEstado "
					+ EST_AGPLUS_PEND_ASIGNAR_GASTOS);
		}

		return estadoHbm;
	}

	// Metodo que valida que el estado de la poliza asociado a la misma por
	// configuracion
	// este presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	public EstadoRenovacionAgroseguro getEstadoPolRenAgroseguro(final Long estAgroseguro, final Session sessionAux,
			final boolean batch) throws Exception {

		Session session = null;

		if (!batch) {
			session = obtenerSession();
		} else {
			session = sessionAux;
		}
		EstadoRenovacionAgroseguro estadoHbm;
		Criteria crit = session.createCriteria(EstadoRenovacionAgroseguro.class)
				.add(Restrictions.eq("codigo", estAgroseguro));

		estadoHbm = (EstadoRenovacionAgroseguro) crit.uniqueResult();

		if (estadoHbm == null) {
			throw new Exception(
					"No se encuentra el estado de la poliza renovable. Revise los datos: idEstado " + estAgroseguro);
		}
		return estadoHbm;
	}

	public Long getLineaseguroIdfromPlanLinea(final Session sessionAux, final Long codPlan, final Long codLinea,
			final boolean batch) {

		Session session = null;

		if (!batch) {
			session = obtenerSession();
		} else {
			session = sessionAux;
		}
		String str = "select lineaseguroid from o02agpe0.tb_lineas lin where lin.codlinea =   " + codLinea
				+ " and lin.codplan = " + codPlan;

		BigDecimal linId = (BigDecimal) session.createSQLQuery(str).uniqueResult();

		logger.info("## plan: " + codPlan + " linea: " + codLinea + " lineaseguroid: " + linId + " ##");

		return linId.longValue();
	}

	public void guardaXml(final PolizaRenovable polizaHbm, final String xmlText, final Session sessionAux,
			final boolean batch) {
		
		logger.info("ImportacionPolRenovableDao(guardaXml) "+xmlText);

		Clob clob;
		Reader reader = null;

		try {

			clob = Hibernate.createClob(xmlText);
			reader = clob.getCharacterStream();
			polizaHbm.setXml(Hibernate.createClob(reader, (int) clob.length()));

		} catch (SQLException e) {

			logger.error("# Error al guardar el XML de la Poliza Renovable.", e);

		} finally {
			try {
				// if (reader != null)
				// reader.close();
			} catch (Exception ex) {
				// Exception free code
			}
		}
	}

	// Metodo que actualiza el historico
	public void actualizarHistorico(final PolizaRenovable polizaHbm, EstadoRenovacionAgroseguro estadoAgroSeguro,
			EstadoRenovacionAgroplus estadoAgroplus, List<Character> listGN, final Session sessionAux,
			final boolean batch, final String usuario) {

		if (listGN == null || listGN.isEmpty()) {
			logger.info("--actualizarHistorico: La lista de GN es vacia");
			return;
		}

		// Insertamos registro en el historico de estados
		Set<PolizaRenovableHistoricoEstados> historico = new HashSet<PolizaRenovableHistoricoEstados>();

		for (Character gn : listGN) {
			PolizaRenovableHistoricoEstados hist = new PolizaRenovableHistoricoEstados();
			hist.setEstadoRenovacionAgroplus(estadoAgroplus);
			hist.setEstadoRenovacionAgroseguro(estadoAgroSeguro);
			hist.setFecha(new Date());
			if (batch) {
				hist.setUsuario("BATCH");
			} else {
				hist.setUsuario(usuario);
			}
			hist.setPolizaRenovable(polizaHbm);
			hist.setGrupoNegocio(gn);
			historico.add(hist);

			logger.info("## Historico de la poliza " + polizaHbm.getReferencia() + " con GN: " + gn + " insertado ##");
		}

		polizaHbm.setPolizaRenovableHistoricoEstadoses(historico);
	}

	@Override
	public boolean guardarPolizaRen(final PolizaRenovable polHbmRen, final Session sessionAux, final boolean batch)
			throws DAOException {
		
		logger.info("## ImportacionPolRenovableDao (GuardarPolizaRen) " + polHbmRen.getReferencia() + " del Plan: " + polHbmRen.getPlan());

		Session session = null;

		if (!batch) {
			session = obtenerSession();
		} else {
			session = sessionAux;
		}

		boolean isGrabado = false;

		try {

			session.saveOrUpdate(polHbmRen);
			isGrabado = true;
			

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al guardar la póliza Renovable", ex);
		} finally {
		}
		logger.info("Fin guardar Poliza Renovable()");
		return isGrabado;
	}
	
	/* Cambio de Alcance Nº2 ** P0063482 ** MODIF TAM (08.06.2021) ** Inicio */
	@Override
	public ColectivosRenovacion obtenerColectivoRenovPlanAnt(final String refPoliza, BigDecimal codPlan, BigDecimal codLinea)
			throws DAOException {

		ColectivosRenovacion colRen = new ColectivosRenovacion();
		Session session = obtenerSession();
		
		try {
		
			logger.info("## ImportacionPolRenovableDao (obtenerColectivoRenovPlanAnt). Referencia Poliza " + refPoliza + " del Plan: " + codPlan + "y Linea:"+codLinea);
			
			String sql = "Select  col.referencia, col.codentidad, col.codentidadmed, col.codsubentmed, col.dc, col.codlinea" + 
						 " from o02agpe0.tb_polizas_renovables polRen" +
						 " inner join o02agpe0.tb_colectivos_renovacion col on polRen.Idcolectivo = col.id " +
						 " where polRen.Referencia = '" + refPoliza +"' and polRen.Plan < "+ codPlan + 
						 " and polRen.Linea = " + codLinea + 
						 " order by polRen.Plan desc";
	
			logger.info("Valor de sql:"+sql);
	
			@SuppressWarnings("rawtypes")
			List resultado = session.createSQLQuery(sql).list();
	
			if (!resultado.isEmpty()) {
				for (Object obj : resultado) {
					Object[] registro = (Object[]) obj;
					
					BigDecimal entidad = (BigDecimal) registro[1];
					BigDecimal entMed = (BigDecimal) registro[2];
					BigDecimal subEnt = (BigDecimal) registro[3];
					String dc = (String) registro[4];
					BigDecimal linea = (BigDecimal) registro[5];
					
					colRen.setCodentidad(entidad.longValue());
					colRen.setCodentidadmed(entMed.longValue());
					colRen.setCodlinea(linea.longValue());
					colRen.setCodsubentmed(subEnt.longValue());
					colRen.setDc(dc.charAt(0));
					colRen.setReferencia(registro[0].toString());
					break;
				}
			}else {
				return null;
			}
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al obtener los colectivos de la poliza Renovable de Planes anteriores", ex);
		} finally {
		}	

		return colRen;
	}
	
	@Override
	public ColectivosRenovacion obtenerColectivoPolPpal(final String refPoliza, BigDecimal codPlan, BigDecimal codLinea)
			throws DAOException {

		ColectivosRenovacion colRen = new ColectivosRenovacion();
		Session session = obtenerSession();
		
		logger.info("## ImportacionPolRenovableDao (obtenerColectivoPolPpal). Referencia Poliza " + refPoliza + " del Plan: " + codPlan + "y Linea:"+codLinea);
		
		String sql = "select col.idcolectivo, col.codentidad, col.entmediadora, col.subentmediadora, col.dc, lin.codlinea " + 
				     "  from o02agpe0.tb_colectivos col " + 
				     " inner join o02agpe0.tb_lineas lin on lin.lineaseguroid = col.lineaseguroid " + 
				     " inner join o02agpe0.tb_polizas po on po.idcolectivo = col.id and po.lineaseguroid = lin.lineaseguroid " + 
				     " where po.referencia = '"+refPoliza +"' and lin.codlinea = " + codLinea + 
				     " and lin.codplan < "+ codPlan +" and po.idestado <> 0 " + 
				     " order by lin.codplan desc ";

		logger.info("Valor de sql:"+sql);
		
		@SuppressWarnings("rawtypes")
		List resultado = session.createSQLQuery(sql).list();

		if (!resultado.isEmpty()) {
			for (Object obj : resultado) {
				Object[] registro = (Object[]) obj;
				
				BigDecimal entidad = (BigDecimal) registro[1];
				BigDecimal entMed = (BigDecimal) registro[2];
				BigDecimal subEnt = (BigDecimal) registro[3];
				String dc = (String) registro[4];
				BigDecimal linea = (BigDecimal) registro[5];
				
				colRen.setCodentidad(entidad.longValue());
				colRen.setCodentidadmed(entMed.longValue());
				colRen.setCodlinea(linea.longValue());
				colRen.setCodsubentmed(subEnt.longValue());
				colRen.setDc(dc.charAt(0));
				colRen.setReferencia(registro[0].toString());
				break;
			}
		}else {
			return null;
		}

		return colRen;
	}
	/* Cambio de Alcance Nº2 ** P0063482 ** MODIF TAM (08.06.2021) ** Fin */

}