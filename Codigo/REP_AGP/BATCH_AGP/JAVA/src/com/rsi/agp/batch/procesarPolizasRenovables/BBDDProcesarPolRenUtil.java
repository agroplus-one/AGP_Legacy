package com.rsi.agp.batch.procesarPolizasRenovables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.batch.procesarPolizasRenovables.ProcesarPolizasRenovables.PolizaRenBean;
import com.rsi.agp.batch.procesarPolizasRenovables.ProcesarPolizasRenovablesWS.RelacionEtiquetaTabla;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableHistoricoEstados;

import es.agroseguro.estadoRenovacion.Renovacion;

public final class BBDDProcesarPolRenUtil {

	private BBDDProcesarPolRenUtil() {
	}

	private static final Logger logger = Logger.getLogger(ProcesarPolizasRenovables.class);

	// Metodo que actualiza el historico
	public static void actualizarHistorico(final PolizaRenovable polizaHbm, EstadoRenovacionAgroseguro estadoAgroSeguro,
			EstadoRenovacionAgroplus estadoAgroplus, final Session session) {
		// Insertamos registro en el historico de estados
		Set<PolizaRenovableHistoricoEstados> historico;
		historico = new HashSet<PolizaRenovableHistoricoEstados>();
		PolizaRenovableHistoricoEstados hist = new PolizaRenovableHistoricoEstados();
		hist.setEstadoRenovacionAgroplus(estadoAgroplus);
		hist.setEstadoRenovacionAgroseguro(estadoAgroSeguro);
		hist.setFecha(new Date());
		hist.setUsuario("BATCH");
		hist.setPolizaRenovable(polizaHbm);
		historico.add(hist);
		polizaHbm.setPolizaRenovableHistoricoEstadoses(historico);
		session.saveOrUpdate(hist);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Devuelve el id y la referencia de las polizas renovables que no tienen un
	 * registro asociado en porcentajes de comisiones
	 * 
	 * @param session
	 * @return
	 */
	protected static List<Object[]> getPolizasRenovablesSinPctComisiones(final Session session) {
		List<Object[]> reg = null;
		try {
			String consulta = "SELECT ren.ID, ren.FECHA_RENOVACION, ren.REFERENCIA , pol.lineaseguroid "
					+ "FROM o02agpe0.TB_POLIZAS_RENOVABLES ren "
					+ "INNER JOIN o02agpe0.TB_POLIZAS pol on ren.REFERENCIA=pol.REFERENCIA "
					+ "LEFT OUTER JOIN o02agpe0.TB_POLIZAS_PCT_COMISIONES pct on pol.IDPOLIZA=pct.IDPOLIZA "
					+ "WHERE pct.IDPOLIZA is null "
					+ "GROUP BY ren.ID, ren.FECHA_RENOVACION, ren.REFERENCIA, pol.lineaseguroid";
			reg = session.createSQLQuery(consulta).list();

		} catch (Exception e) {
			logger.error(
					"Error seleccionando las polizas renovables que no tienen registro de porcentajes de comisiones. "
							+ e.getMessage());
		}

		return reg;
	}

	protected static GastosRenovacion getGastosRenovacion(final Session session, Long id) {
		GastosRenovacion gr = null;
		try {
			logger.debug("### BBDDProcesarPolRenUtil.getGastosRenovacion - ENTRA");
			Criteria criteriaGastos = session.createCriteria(GastosRenovacion.class, "gr");
			criteriaGastos.add(Restrictions.eq("polizaRenovable.id", id));
			gr = (GastosRenovacion) criteriaGastos.uniqueResult();
		} catch (Exception e) {
			logger.error("Error seleccionando los gastos de renovacion de la poliza " + id + " - " + e.getMessage());
		} finally {
			logger.debug("### BBDDProcesarPolRenUtil.getGastosRenovacion - SALE");
		}
		return gr;
	}

	@SuppressWarnings("unchecked")
	protected static Poliza getPoliza(final Session session, String referencia, Long lineaseguroid) {
		List<Poliza> polizas = null;
		Poliza res = null;
		try {
			logger.debug("### BBDDProcesarPolRenUtil.getPoliza - ENTRA");
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("linea", "linea");
			criteria.add(Restrictions.eq("referencia", referencia));
			criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			polizas = criteria.list();
			if (null != polizas && polizas.size() > 0) {
				res = polizas.get(0);
				logger.debug("poliza " + referencia + " OK");
			} else {
				logger.debug("poliza " + referencia + " NO encontrada");
			}
		} catch (Exception e) {
			logger.error("Error al recoger la poliza de BBDD con referencia " + referencia + " lineaseguroid: "
					+ lineaseguroid + " - " + e.getMessage());
		} finally {
			logger.debug("### BBDDProcesarPolRenUtil.getPoliza - SALE");
		}
		return res;
	}

	// Metodo principal de importacion invocado desde otras clases
	protected static List<PolizaRenBean> existePoliza(List<PolizaRenBean> lstRes, final Renovacion polRen,
			final Session session, final int contpolizas) throws Exception {
		// Comprobamos que la referencia de la poliza renovable no se encuentra ya en
		// BBDD
		Poliza polHbm;
		try {
			Criteria crit = session.createCriteria(Poliza.class)
					// .add(Restrictions.eq("referencia","144996P"));
					.add(Restrictions.eq("referencia", polRen.getReferencia()));

			polHbm = (Poliza) crit.uniqueResult();
			if (polHbm == null) {
				// logger.debug("## La poliza " + polRen.getReferencia() + " No existe. La
				// creamos."+" # "+contpolizas+" ## ");

				PolizaRenBean polClonar = new PolizaRenBean();
				polClonar.setReferencia(polRen.getReferencia());
				polClonar.setLinea(Integer.toString(polRen.getLinea()));
				polClonar.setPlan(Integer.toString(polRen.getPlan()));
				polClonar.setIdColectivo(polRen.getColectivo());
				polClonar.setNifAsegurado(polRen.getNifAsegurado());

				lstRes.add(polClonar);
			} else {
				// logger.debug("## La poliza: codPlan "+ polRen.getPlan() + " codLinea " +
				// polRen.getLinea() + ", referencia "
				// + polRen.getReferencia() +" ya existe ## "+contpolizas+" # ");

			}
		} catch (Exception ex) {
			logger.error(" Error al comprobar si existe la poliza : " + polRen.getReferencia() + " plan: "
					+ polRen.getPlan() + " linea: " + polRen.getLinea(), ex);
			return lstRes;
		}
		return lstRes;
	}

	// Metodo principal de importacion invocado desde otras clases
	@SuppressWarnings("unchecked")
	protected static Colectivo comprobarColectivo(PolizaRenBean polRes, final BigDecimal codentidad,
			final BigDecimal entMed, final BigDecimal entSubMed, final Session session) throws Exception {
		// Comprobamos que el colectivo con plan y linea de la poliza exista
		Colectivo colHbm = null;
		List<Colectivo> lstColectivos = null;
		try {
			Criteria crit = session.createCriteria(Colectivo.class);

			crit.createAlias("linea", "linea");
			crit.createAlias("subentidadMediadora", "subentidadMediadora");

			crit.add(Restrictions.eq("linea.codplan", new BigDecimal(polRes.getPlan())));
			crit.add(Restrictions.eq("linea.codlinea", new BigDecimal(polRes.getLinea())));

			crit.add(Restrictions.eq("subentidadMediadora.entidad.codentidad", codentidad));
			crit.add(Restrictions.eq("subentidadMediadora.id.codentidad", entMed));
			crit.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", entSubMed));

			// crit.add(Restrictions.eq("idcolectivo","1234578"));
			crit.add(Restrictions.eq("idcolectivo", polRes.getIdColectivo()));
			lstColectivos = crit.list();
			if (lstColectivos != null && lstColectivos.size() > 0) {
				colHbm = lstColectivos.get(0);
				if (lstColectivos.size() > 1) {
					// logger.debug(" ## COLECTIVO DUPLICADO: "+ polRes.getIdColectivo());
				}
			}
		} catch (Exception ex) {
			logger.error(" Error al comprobar el colectivo: " + polRes.getIdColectivo() + " plan: " + polRes.getPlan()
					+ " linea: " + polRes.getLinea(), ex);
			return null;
		}
		if (colHbm == null) {
			// logger.debug("# El colectivo " + polRes.getIdColectivo() + " Plan: "
			// + polRes.getPlan() + " Linea: " + polRes.getLinea() +" NO EXISTE. Comprobamos
			// plan anterior #");

			// buscamos por plan anterior
			// logger.debug("referencia: "+polRes.getReferencia());
			int planAnt = Integer.parseInt(polRes.getPlan()) - 1;
			try {
				Criteria crit2 = session.createCriteria(Colectivo.class);
				crit2.createAlias("linea", "linea");
				crit2.createAlias("subentidadMediadora", "subentidadMediadora");

				crit2.add(Restrictions.eq("linea.codplan", new BigDecimal(Integer.toString(planAnt))));
				crit2.add(Restrictions.eq("linea.codlinea", new BigDecimal(polRes.getLinea())));

				crit2.add(Restrictions.eq("subentidadMediadora.entidad.codentidad", codentidad));
				crit2.add(Restrictions.eq("subentidadMediadora.id.codentidad", entMed));
				crit2.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", entSubMed));
				// crit2.add(Restrictions.eq("idcolectivo","1583415"));
				// descomentar la siguiente y comentar la anterior
				crit2.add(Restrictions.eq("idcolectivo", polRes.getIdColectivo()));

				lstColectivos = crit2.list();
				if (lstColectivos != null && lstColectivos.size() > 0) {
					colHbm = lstColectivos.get(0);
					if (lstColectivos.size() > 1) {
						logger.debug(" ## COLECTIVO DUPLICADO ANT: " + polRes.getIdColectivo());
					}
				}
			} catch (Exception ex) {
				logger.error(" Error al comprobar el colectivo: " + polRes.getIdColectivo() + " plan anterior: "
						+ planAnt + " linea: " + polRes.getLinea(), ex);
				return null;
			}

			if (colHbm == null) {
				// logger.debug("# El colectivo " + polRes.getIdColectivo() + " codPlan: "
				// + planAnt + " codLinea: " + polRes.getLinea() +" NO EXISTE. NO REPLICAMOS
				// POLIZA #");
				return null;
			} else {
				// logger.debug("# El colectivo plan anterior existe, replicamos colectivo al
				// plan actual #");
				// replicar colectivo plan anterior al actual.
				// replicando colectivo 1583415 del plan 2015 al 2014 de prueba.
				colHbm = replicarColectivo(colHbm, session, polRes.getPlan().toString());
				// polRes.setDescripcion("ColectivoOK");
			}
		} else {
			// logger.debug("# El colectivo " + polRes.getIdColectivo() +" del plan actual
			// existe #");
			return colHbm;
		}
		return colHbm;
	}

	// Metodo principal de replicar colectivo
	protected static Colectivo replicarColectivo(final Colectivo colHbm, final Session session, final String plan) {
		Transaction transCol = session.beginTransaction();
		Colectivo colectivoBean = new Colectivo();
		Linea lineaHbm = new Linea();
		try {
			colectivoBean.setActivo(colHbm.getActivo());
			colectivoBean.setDc(colHbm.getDc());

			if (colHbm.getPctdescuentocol() != null) {
				colectivoBean.setPctdescuentocol(colHbm.getPctdescuentocol());
			}

			if (colHbm.getPctprimerpago() != null) {
				colectivoBean.setPctprimerpago(colHbm.getPctprimerpago());
			}

			if (colHbm.getPctsegundopago() != null) {
				colectivoBean.setPctsegundopago(colHbm.getPctsegundopago());
			}

			if (colHbm.getFechaprimerpago() != null) {
				Date fechPrimerPago = getFechaNueva(colHbm.getFechaprimerpago());
				colectivoBean.setFechaprimerpago(fechPrimerPago);
			}
			if (colHbm.getFechasegundopago() != null) {
				Date fechSegundoPago = getFechaNueva(colHbm.getFechasegundopago());
				colectivoBean.setFechasegundopago(fechSegundoPago);
			}
			if (colHbm.getFechacambio() != null) {
				Date fechCambio = getFechaNueva(colHbm.getFechacambio());
				colectivoBean.setFechacambio(fechCambio);
			}
			if (colHbm.getFechaefecto() != null) {
				Date fechEfecto = getFechaNueva(colHbm.getFechaefecto());
				colectivoBean.setFechaefecto(fechEfecto);
			}

			if (colHbm.gettipoDescRecarg() != null) {
				colectivoBean.settipoDescRecarg(colHbm.gettipoDescRecarg());
			}

			if (colHbm.getpctDescRecarg() != null) {
				colectivoBean.setpctDescRecarg(colHbm.getpctDescRecarg());
			}

			colectivoBean.setCccEntidad(colHbm.getCccEntidad());
			colectivoBean.setCccOficina(colHbm.getCccOficina());
			colectivoBean.setCccDc(colHbm.getCccDc());
			colectivoBean.setCccCuenta(colHbm.getCccCuenta());

			colectivoBean.setTomador(colHbm.getTomador());

			Criteria critLinea = session.createCriteria(Linea.class);
			critLinea.add(Restrictions.eq("codlinea", colHbm.getLinea().getCodlinea()));
			critLinea.add(Restrictions.eq("codplan", new BigDecimal(plan)));
			lineaHbm = (Linea) critLinea.uniqueResult();
			colectivoBean.setLinea(lineaHbm);

			colectivoBean.setIban(colHbm.getIban());
			colectivoBean.setIdcolectivo(colHbm.getIdcolectivo());
			colectivoBean.setNomcolectivo(colHbm.getNomcolectivo());
			colectivoBean.setSubentidadMediadora(colHbm.getSubentidadMediadora());
			colectivoBean.setEnvioIbanAgro(colHbm.getEnvioIbanAgro());

			session.saveOrUpdate(colectivoBean);
			actualizarHistoricoCol(colectivoBean, session);
			transCol.commit();

			logger.debug("El colectivo replicado del plan anterior correctamente, " + colHbm.getIdcolectivo()
					+ " plan: " + plan + " linea: " + colHbm.getLinea().getCodlinea());

			return colectivoBean;
		} catch (Exception ex) {
			logger.error("Error al replicar el colectivo - " + colHbm.getIdcolectivo() + " plan: " + plan + " linea: "
					+ colHbm.getLinea().getCodlinea(), ex);

			return null;
		}

	}

	public static Date getFechaNueva(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		Date datt = cal.getTime();
		return datt;
	}

	// Metodo que actualiza el historico del colectivo
	public static void actualizarHistoricoCol(final Colectivo cBean, final Session session) {
		HistoricoColectivos histCol = new HistoricoColectivos();
		histCol.setSubentidadMediadora(cBean.getSubentidadMediadora());
		histCol.setColectivo(cBean);
		histCol.setActivo('1');
		histCol.setDc(cBean.getDc());
		histCol.setFechacambio(cBean.getFechacambio());
		histCol.setFechaefecto(cBean.getFechaefecto());
		histCol.setFechaoperacion(new Date());
		histCol.setFechaprimerpago(cBean.getFechaprimerpago());
		histCol.setFechasegundopago(cBean.getFechasegundopago());
		histCol.setLinea(cBean.getLinea());
		histCol.setNomcolectivo(cBean.getNomcolectivo());
		histCol.setPctdescuentocol(cBean.getPctdescuentocol());
		histCol.setPctprimerpago(cBean.getPctprimerpago());
		histCol.setPctsegundopago(cBean.getPctsegundopago());
		histCol.setTomador(cBean.getTomador());
		histCol.setCodusuario("BATCH");
		histCol.setTipooperacion('A');
		histCol.setReferencia(cBean.getIdcolectivo());
		session.saveOrUpdate(histCol);
		logger.debug("historico colectivo insertado:" + histCol.getId());
	}

	// Metodo que devuelve el plan actual
	public static int getPlanActual(final Session session) {
		// logger.debug(" BUSCANDO PLAN ACTUAL..");
		String strPlanActual = "select max(codplan) from o02agpe0.tb_lineas lin ,o02agpe0.tb_sc_c_lineas linn where lin.codlinea = linn.codlinea and linn.codgruposeguro='G01'";
		// logger.debug("SQL PLAN ACTUAL: " + strPlanActual );
		BigDecimal plan = (BigDecimal) session.createSQLQuery(strPlanActual).uniqueResult();
		logger.debug("## PLAN ACTUAL: " + plan + " ##");
		return plan.intValue();
	}

	// Metodo que devuelve contador de polizas a insertar en BBDD
	public static int getContadorPolizas(final Session session) {
		try {
			String strCont = "select agp_valor from o02agpe0.Tb_Config_Agp where agp_nemo='CONT_PROCESAR_POL'";
			String cont = (String) session.createSQLQuery(strCont).uniqueResult();
			logger.debug("## CONTADOR POLIZAS: " + cont + " ## ");
			if (cont != null)
				return Integer.parseInt(cont);
			else
				return 0;
		} catch (Exception ex) {
			logger.error(" Error al recoger el contador de Polizas en Config_Agp : ", ex);
			return 0;
		}
	}

	// Metodo que devuelve las lineas activas con grupo seguro G01
	public static List<BigDecimal> getLineasGanado(final Session session, final int planActual) {
		String str = "select distinct(linn.codlinea) from o02agpe0.tb_lineas lin ,o02agpe0.tb_sc_c_lineas linn where lin.codlinea = linn.codlinea and lin.codplan="
				+ planActual + " and linn.codgruposeguro='G01'";
		@SuppressWarnings("unchecked")
		List<BigDecimal> lstLineas = session.createSQLQuery(str).list();

		logger.debug("## LINEAS ACTIVAS CON G01: " + lstLineas.toString() + " ##");

		return lstLineas;
	}

	// Metodo que devuelve las referencias de la tabla de polizas renovables que no
	// existan en la tabla de polizas
	// filtradas por plan y ordenadas ascendentemente por fecha de renovacion y que
	// no estan rescindidas ni anuladas.
	public static List<String> getReferencias(final Session session, final Long plan, String estados) {
		String str = "select referencia from o02agpe0.tb_polizas_renovables ren where ren.plan= " + plan
				+ " and ren.referencia not in "
				+ "(select pol.referencia from o02agpe0.tb_polizas pol, o02agpe0.tb_lineas lin where referencia is not null "
				+ " and pol.lineaseguroid = lin.lineaseguroid and lin.codplan = " + plan
				+ ")  and ren.estado_agroseguro in" + "(" + estados + ") order by ren.fecha_renovacion asc";
		logger.debug("# getReferencias: " + str.toString() + " #");
		@SuppressWarnings("unchecked")
		List<String> lstReferencias = session.createSQLQuery(str).list();
		return lstReferencias;
	}

	// Metodo que devuelve la lineaseguroid
	public static Long getlineaseguroid(final Session session, final int codPlan, final int codLinea) {
		try {
			String strLineaseugoid = "select lineaseguroid from o02agpe0.tb_lineas lin where lin.codlinea=" + codLinea
					+ " and lin.codplan=" + codPlan;
			// logger.debug("SQL PLAN ACTUAL: " + strPlanActual );
			BigDecimal linSegId = (BigDecimal) session.createSQLQuery(strLineaseugoid).uniqueResult();
			// logger.debug(" LineaseguroId: " + linSegId );
			return linSegId.longValue();
		} catch (Exception ex) {
			logger.error(" Error al recuperar la lineaseguroid: ", ex);
			return null;
		}
	}

	// Metodo que devuelve el nombre del concepto
	public static Integer getCodconceptoBBDD(final String datNombre, final Session session) {
		try {
			String str = " select dic.codconcepto from o02agpe0.tb_sc_dd_dic_datos dic where dic.etiquetaxml='"
					+ datNombre + "'";
			BigDecimal codConcept = (BigDecimal) session.createSQLQuery(str).uniqueResult();
			// logger.debug(" Nombre Concepto: "+ datNombre +" codConcepto: "+codConcept);
			return codConcept.intValue();

		} catch (Exception ex) {
			logger.error(" No se encuentra el codconcepto para el Nombre : " + datNombre);
			return 0;
		}
	}

	public static String getCodconcepto(final String nombre,
			final Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla) {
		String codConcepto = "";
		RelacionEtiquetaTabla rel = auxEtiquetaTabla.get(nombre);
		if (rel != null) {
			codConcepto = rel.getcodConcepto();
		}
		return codConcepto;
	}

	// Metodo que devuelve la clase, usuario y oficina de la poliza pasando como
	// parametros la referencia y el plan
	@SuppressWarnings("rawtypes")
	public static Object[] checkClasePolizaAnterior(String referencia, int codPlan, final Session session) {
		Object[] campos = null;
		List registros = new ArrayList();
		try {
			String str = " select pol.clase,pol.codusuario,pol.oficina from o02agpe0.tb_polizas pol,o02agpe0.tb_lineas lin where pol.lineaseguroid = lin.lineaseguroid"
					+ " and lin.codplan = " + codPlan + " and pol.referencia = '" + referencia + "'"; // 146628P'
			logger.debug("# checkClasePolizaAnterior: " + str.toString() + " #");
			registros = session.createSQLQuery(str).list();
			if (registros != null && registros.size() > 0) {
				campos = (Object[]) registros.get(0);
				logger.debug("# poliza anterior encontrada #");
				return campos; // lstClasases.get(0);
			}
			return null;

		} catch (Exception ex) {
			logger.error(" No se encuentra la clase para el anho anterior de la referencia: " + referencia
					+ " , plan: " + codPlan);
			return null;
		}
	}

	protected static Usuario getUsuario(final String codUsuario, final Session session) {
		Usuario usuario = new Usuario();
		try {
			Criteria critLinea = session.createCriteria(Usuario.class);
			critLinea.add(Restrictions.eq("codusuario", codUsuario));
			usuario = (Usuario) critLinea.uniqueResult();
			return usuario;
		} catch (Exception ex) {
			logger.error("Error al recuperar el usuario " + codUsuario + " de la BBDD", ex);
			return null;
		}

	}

	// Metodo que devuelve los estados a tratar de las polizas
	public static String getEstadosPolRenovables(final Session session) {
		String estados = null;
		String estadosFinal = null;
		try {
			String query = "select ESTADO_PLZ_RENOV_CARGA from o02agpe0.tb_parametros";
			estados = (String) session.createSQLQuery(query).uniqueResult();
			// logger.debug(" ## ESTADOS AGROSEGURO A PROCESAR: " + estados + " ## ");
			if (estados != null) {
				String ultimaLetra = estados.substring(estados.length() - 1, estados.length());
				if (ultimaLetra.equals(",")) {
					estadosFinal = estados.substring(0, estados.length() - 1);
				} else {
					estadosFinal = estados;
				}
				return estadosFinal;
			} else {
				logger.debug(" No se encuentra ninguna parametrizacion para los estados en BBDD");
				return null;
			}
		} catch (Exception ex) {
			logger.error(" Error al recoger los estados a tratar de BBDD : ", ex);
			return null;
		}
	}

	public static BigDecimal getSecuenciaComparativa(final Session session) throws DAOException {
		try {
			String sql = "select o02agpe0.SQ_MODULOS_POLIZA.nextval from dual";
			BigDecimal secuencia = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			return secuencia;
		} catch (Exception e) {
			throw new DAOException("Error al crear la secuencia de la comparativa ", e);
		}
	}
	
	/** ESC-16132 ** MODIF TAM (09.12.2021) ** Guardar las coberturas de las explotaciones en caso de tenerlas contratadas ** INICIO **/
	public static short getFilaExplotacionCobertura(Long lineaSeguroId, String modulo, int conceptoPpalMod, int riesgoCubierto, final Session session)
			throws DAOException {

		String sql = null;
		BigDecimal resBigD = null;
		short res = 0;
		try {
			sql = "select rc.FILAMODULO from o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD_G rc "
					+ "inner join o02agpe0.TB_LINEAS lin ON rc.LINEASEGUROID = lin.LINEASEGUROID " + "WHERE lin.LINEASEGUROID = "
					+ lineaSeguroId + " AND rc.CODMODULO = '" + modulo + "' AND rc.CODCONCEPTOPPALMOD = "
					+ conceptoPpalMod + " AND rc.CODRIESGOCUBIERTO = " + riesgoCubierto + "  AND rc.NIVELECCION='D'";

			logger.debug(sql);

			resBigD = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			if (null != resBigD)
				res = resBigD.shortValue();
			return res;
		} catch (Exception ex) {
			logger.error("BBDDProcesarPolRenUtil.getFilaExplotacionCobertura. - ", ex);
			throw new DAOException("BBDDProcesarPolRenUtil.getFilaExplotacionCobertura. - ", ex);
		}

	}
	
	public static String getDescripcionConceptoPpalMod(int conceptoPpalMod, final Session session) throws DAOException {
		String sql = null;
		String res = null;
		try {
			sql = "SELECT DESCONCEPTOPPALMOD from o02agpe0.TB_SC_C_CONCEPTO_PPAL_MOD " + "WHERE CODCONCEPTOPPALMOD = "
					+ conceptoPpalMod;

			logger.debug(sql);
			
			res = (String) session.createSQLQuery(sql).uniqueResult();

			return res;
		} catch (Exception ex) {
			logger.error("BBDDProcesarPolRenUtil.getDescripcionConceptoPpalMod. - ", ex);
			throw new DAOException("BBDDProcesarPolRenUtil.getDescripcionConceptoPpalMod. - ", ex);
		}

	}

	public static String getDescripcionRiesgoCubierto(Long lineaSeguroId, String modulo, int riesgoCubierto, final Session session)
			throws DAOException {
		String sql = null;
		String res = null;
		try {
			sql = "SELECT rc.DESRIESGOCUBIERTO FROM o02agpe0.TB_SC_C_RIESGOS_CUBIERTOS  rc "
					+ "INNER JOIN o02agpe0.TB_LINEAS lin on rc.LINEASEGUROID = lin.LINEASEGUROID " + "WHERE lin.LINEASEGUROID = "
					+ lineaSeguroId + " AND rc.CODMODULO = '" + modulo + "' AND rc.CODRIESGOCUBIERTO = "
					+ riesgoCubierto;

			logger.debug(sql);

			res = (String) session.createSQLQuery(sql).uniqueResult();

			return res;
		} catch (Exception ex) {
			logger.error("BBDDProcesarPolRenUtil.getDescripcionRiesgoCubierto. - ", ex);
			throw new DAOException("BBDDProcesarPolRenUtil.getDescripcionRiesgoCubierto. - ", ex);
		}

	}

}
