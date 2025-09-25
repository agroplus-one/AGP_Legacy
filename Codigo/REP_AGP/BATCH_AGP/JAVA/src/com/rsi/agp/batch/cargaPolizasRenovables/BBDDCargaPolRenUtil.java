package com.rsi.agp.batch.cargaPolizasRenovables;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.service.impl.PolizaRenBean;
import com.rsi.agp.core.jmesa.service.impl.utilidades.AltaPolizaRenovableService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.LineaGrupoNegocio;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableHistoricoEstados;

import es.agroseguro.estadoRenovacion.Renovacion;

public final class BBDDCargaPolRenUtil {

	private BBDDCargaPolRenUtil() {
	}

	private static final Logger logger = Logger.getLogger(CargaPolizasRenovables.class);

	// Metodo principal de importacion invocado desde otras clases
	/*
	 * ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S
	 * Mediadora * Inicio
	 */
	/*
	 * protected static void importaPolizaRen(List<PolizaRenBean> lstRes , final
	 * Renovacion polizaRen,final Session session, final Map<String, BigDecimal[]>
	 * mapPctComs,final int cont, final StringBuilder polKO, final StringBuilder
	 * polOK, final PolizasPctComisionesDao polizasPctComisionesDao) throws
	 * Exception {
	 */

	public static void importaPolizaRen(List<PolizaRenBean> lstRes, final Renovacion polizaRen, final Session session,
			final int cont, final StringBuilder polKO, final StringBuilder polOK,
			final PolizasPctComisionesDao polizasPctComisionesDao, final Long plan, final AltaPolizaRenovableService altaPolizaRenovableService) throws Exception {
		PolizaRenovable polizaHbm;
		
		try {
			// Buscamos que no exista ya en BBDD
			Criteria crit = session.createCriteria(PolizaRenovable.class)
					.add(Restrictions.eq("plan", Long.valueOf(polizaRen.getPlan())))
					.add(Restrictions.eq("linea", Long.valueOf(polizaRen.getLinea())))
					.add(Restrictions.eq("referencia", polizaRen.getReferencia()));
			polizaHbm = (PolizaRenovable) crit.uniqueResult();
			if (polizaHbm == null) {
				logger.debug("## -----------" + polizaRen.getReferencia() + "----------- ##");
				polizaHbm = new PolizaRenovable();

				/****
				 * Pet. 63482 ** Por los desarrollos de esta petici�n se ha creado una funci�n
				 * de alta com�n para on-line y batch
				 *****/
				boolean batch = true;
				ColectivosRenovacion colRen = altaPolizaRenovableService.ValidatePolizaRenColectivo(polizaRen, batch, session);

				if (colRen != null) {

					polizaHbm.setColectivoRenovacion(colRen);
					String codUsuario = "BATCH";
					
					boolean polizaOK = altaPolizaRenovableService.populateAndValidatePolizaRen(lstRes, polizaHbm,
							polizaRen, session, polOK, polizasPctComisionesDao, batch, codUsuario);
					logger.debug("Poliza dada de alta: " + polizaOK);
				}
				/*
				 * ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S
				 * Mediadora * Fin
				 */
			} else {
				polKO.append(polizaRen.getReferencia() + ",");
			}
		} catch (Exception ex) {
			logger.debug(" Error al comprobar si existe la pol. " + polizaRen.getReferencia() + " ## " + cont + " ## ",
					ex);
		}
	}



	
	// Metodo que devuelve la lineas activas con grupo seguro G01
	public static List<BigDecimal> getLineasGanado(final Session session, final int planActual) {
		String str = "select distinct(linn.codlinea) from o02agpe0.tb_lineas lin ,o02agpe0.tb_sc_c_lineas linn where lin.codlinea = linn.codlinea and lin.codplan="
				+ planActual + " and linn.codgruposeguro='G01'";

		@SuppressWarnings("unchecked")
		List<BigDecimal> lstLineas = session.createSQLQuery(str).list();

		logger.debug("## LINEAS ACTIVAS CON G01: " + lstLineas.toString() + " ##");

		return lstLineas;
	}

	// Metodo que devuelve una lista de lineasGrupoNegocio
	public static List<LineaGrupoNegocio> getGruposNegocioPorLinea(final Session session, final int anio) {
		Criteria crit = session.createCriteria(LineaGrupoNegocio.class);
		crit.createAlias("linea", "lin");
		crit.add(Restrictions.eq("lin.codplan", new BigDecimal(anio)));
		crit.addOrder(Order.asc("lin.lineaseguroid"));
		@SuppressWarnings("unchecked")
		List<LineaGrupoNegocio> lstLineasGrNg = (List<LineaGrupoNegocio>) crit.list();
		return lstLineasGrNg;
	}

	// Metodo que devuelve la fecha desde
	public static String getFechaDesdePolRenovables(final Session session) {
		String fec = "";
		try {
			String queryFec = "select agp_valor from o02agpe0.Tb_Config_Agp where agp_nemo='FEC_DESDE_POL_REN'";
			fec = (String) session.createSQLQuery(queryFec).uniqueResult();
			logger.debug(" ## FECHA DESDE: " + fec + " ## ");
			if (fec != null)
				return fec;
			else
				return null;
		} catch (Exception ex) {
			logger.debug(" Error al recoger la fecha desde : ", ex);
			return null;
		}
	}

	// Metodo que devuelve la fecha hasta
	public static String getFechaHastaPolRenovables(final Session session) {
		String fec = "";
		try {
			String queryFec = "select agp_valor from o02agpe0.Tb_Config_Agp where agp_nemo='FEC_HASTA_POL_REN'";
			fec = (String) session.createSQLQuery(queryFec).uniqueResult();
			logger.debug(" ## FECHA HASTA: " + fec + " ## ");
			if (fec != null)
				return fec;
			else
				return null;
		} catch (Exception ex) {
			logger.debug(" Error al recoger la fecha hasta : ", ex);
			return null;
		}
	}

	// Metodo que devuelve los estados a tratar de las polizas renovables
	public static List<String> getEstadosPolRenovables(final Session session) {
		List<String> lstEstados = new ArrayList<String>();
		String estados = null;
		try {
			String queryFec = "select agp_valor from o02agpe0.Tb_Config_Agp where agp_nemo='ESTADOS_POL_REN'";
			estados = (String) session.createSQLQuery(queryFec).uniqueResult();
			logger.debug(" ## ESTADOS AGROSEGURO A PROCESAR: " + estados + " ## ");
			if (!StringUtils.isNullOrEmpty(estados)) {
				String[] items = estados.split(",");
				lstEstados = Arrays.asList(items);
				if (lstEstados != null)
					return lstEstados;
				else
					return null;
			} else {
				logger.debug(" No se encuentra ninguna parametrizacion para los estados en BBDD");
				return null;
			}
		} catch (Exception ex) {
			logger.error(" Error al recoger los estados a tratar de BBDD : ", ex);
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CultivosEntidades> getParamsGen(final Session session, BigDecimal codLinea, int codPlan)
			throws Exception {
		logger.debug("init - getParamsGen - BBDDCargaPolRenUtil");
		List registros = new ArrayList();
		List resultado = new ArrayList();
		List<CultivosEntidades> lstParam = new ArrayList<CultivosEntidades>();
		boolean comprobarConNulos = true;

		try {
			if (comprobarConNulos) {
				String sql = " SELECT h.pctrga,h.pctadquisicion,h.pctgeneralentidad, e.GRUPO_NEGOCIO "
						+ " FROM o02agpe0.tb_coms_cultivos_entidades e "
						+ " INNER JOIN o02agpe0.tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "
						+ " INNER JOIN o02agpe0.tb_lineas lin  ON  lin.lineaseguroid = e.lineaseguroid "
						+ " WHERE e.ENTMEDIADORA is null and e.SUBENTMEDIADORA is null "
						+ " and ((e.fec_baja is null or to_date(sysdate,'dd/mm/yy') "
						+ " < to_date(e.fec_baja,'dd/mm/yy')) " + " and to_date(sysdate,'dd/mm/yy') >= h.fecha_efecto) "
						+ " and lin.codlinea = " + codLinea + " and lin.codPlan = " + codPlan
						+ " order by h.fecha_efecto  desc ,h.fechamodificacion desc";
				logger.info("sql con nulos: " + sql);
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
						lstParam.add(param);
					}
					if (gnActual.equals(Constants.GRUPO_NEGOCIO_GENERICO.toString())) {
						break;
					}
				}
			}

			logger.debug("fin - getParamsGen");
			return lstParam;

		} catch (Exception e) {
			logger.error("Error al acceder a bbdd - getParamsGen - BBDDCargaPolRenUtil");
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, BigDecimal[]> getParamsComis(final Session session, BigDecimal codLinea, Long codPlan,
			Long entMed, Long subEntMed, Date fechaRenov) throws Exception {
		logger.debug("************************************************");
		logger.debug("BBDDCargaPolRenUtil- getParamsComis(new), INIT");

		List registros = new ArrayList();
		List resultado = new ArrayList();

		List<CultivosEntidades> lstParam = new ArrayList<CultivosEntidades>();
		boolean comprobarConNulos = true;

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
			logger.debug("BBDDCargaPolRenUtil- getParamsComis(new), Buscampos por entMed y subEntMed primero");

			/*
			 * Primero comprobamos si hay comisiones espec�ficas por Entidad Mediadora y
			 * Subentidad Mediadora
			 */
			String sql = "SELECT h.pctrga,h.pctadquisicion,h.pctgeneralentidad, e.GRUPO_NEGOCIO "
					+ "FROM o02agpe0.tb_coms_cultivos_entidades e "
					+ "INNER JOIN o02agpe0.tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "
					+ "INNER JOIN o02agpe0.tb_lineas lin  ON  lin.lineaseguroid = e.lineaseguroid "
					+ "WHERE e.ENTMEDIADORA = " + entMed + " and e.SUBENTMEDIADORA = " + subEntMed
					+ " and ((e.fec_baja is null or to_date(" + fechaParaComparar
					+ ",'dd/mm/yy') < to_date(e.fec_baja,'dd/mm/yy')) " + " and to_date(" + fechaParaComparar
					+ ",'dd/mm/yy') >= h.fecha_efecto) " + " and lin.codlinea = " + codLinea + " and lin.codPlan = "
					+ codPlan + " order by e.GRUPO_NEGOCIO DESC, h.fecha_efecto  desc ,h.fechamodificacion desc";
			logger.info("BBDDCargaPolRenUtil- getParamsComis(new), sql(1): " + sql);

			registros.addAll(session.createSQLQuery(sql).list());

			// SI LA SENTENCIA DEVUELVE EL GRUPO DE NEGOCIO GENERICO O TANTOS GRUPOS COMO
			// TIENE LA LINEA
			// NO ES NECESARIO SEGUIR BUSCANDO. SI NO, DEBEMOS BUSCAR AQUELLOS GRUPOS QUE NO
			// SE HAYAN OBTENIDO YA.
			String gnNotIn = "";
			if (registros.size() > 0) {
				logger.info(
						"BBDDCargaPolRenUtil- getParamsComis(new), Se encuentran comisiones especificas por entMed y SubeEntMed");
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

				logger.debug("BBDDCargaPolRenUtil- getParamsComis(new), Buscamos por generico");

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
				logger.info("BBDDCargaPolRenUtil- getParamsComis(new), sql(2): " + sql);

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

						logger.info("BBDDCargaPolRenUtil- getParamsComis(new), Valor asignado Pctadminitracion: "
								+ param.getPctadministracion().toString());
						logger.info("BBDDCargaPolRenUtil- getParamsComis(new), Valor asignado Pctadquisicion: "
								+ param.getPctadquisicion().toString());
						lstParam.add(param);
					}
					if (gnActual.equals(Constants.GRUPO_NEGOCIO_GENERICO.toString())) {
						break;
					}
				}
			}

			// 3.1 Montamos un mapa con las comisiones para la l�nea de la p�liza grupo de
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
					logger.debug("BBDDCargaPolRenUtil- getParamsComis(new), Valor de mapLineaGrPcts: "
							+ mapLineaGrPcts.toString());
				}
			}

			// 3.2 Compruebo si existe el grupo generico para esa linea en nuestro
			// mantenimiento de parametros generales
			for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param : lstParam) {
				if (param.getLinea().getCodlinea().compareTo(codLinea) == 0) {// misma linea
					if (param.getGrupoNegocio().getGrupoNegocio().equals('9')) {
						logger.debug("## linea " + codLinea + " con grupoNegocio 9 ##");
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
					logger.debug("## metemos todos los grupos " + lstGr.toString() + " para linea " + codLinea
							+ " plan " + codPlan + " ##");
					for (Character gr : lstGr) {
						if (!gr.equals('9')) {
							BigDecimal[] pctComs;
							pctComs = new BigDecimal[2];
							pctComs[0] = pctAdmTemp;
							pctComs[1] = pctAdqTemp;
							mapLineaGrPctsFinal.put(codLinea.toString() + "_" + gr, pctComs);
							logger.debug(
									"BBDDCargaPolRenUtil- getParamsComis(new), Asignamos datos al mapLineasGrcPcts(1)");
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
								logger.debug("## la linea " + codLinea + " plan: " + codPlan
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
								logger.debug(
										"BBDDCargaPolRenUtil- getParamsComis(new), Asignamos datos al mapLineasGrcPcts(2)");
								// indexo en el mapa las comisiones como key: linea_GrupoNegocio
								mapLineaGrPctsFinal.put(param.getLinea().getCodlinea().toString() + "_"
										+ param.getGrupoNegocio().getGrupoNegocio(), pctComs);
							}
						}
					} // fin if informado
				}
			}
			/* Taty 06.09.2019 Fin */

			logger.debug("BBDDCargaPolRenUtil- getParamsComis(new), FIN");
			return mapLineaGrPctsFinal;

		} catch (Exception e) {
			logger.error("Error al acceder a bbdd - getParamsGen - BBDDCargaPolRenUtil");
			throw e;
		}

	}

	public static Long getLineaseguroIdfromPlanLinea(final Session session, final Long codPlan, final Long codLinea) {
		String str = "select lineaseguroid from o02agpe0.tb_lineas lin where lin.codlinea =   " + codLinea
				+ " and lin.codplan = " + codPlan;

		BigDecimal linId = (BigDecimal) session.createSQLQuery(str).uniqueResult();

		logger.debug("## plan: " + codPlan + " linea: " + codLinea + " lineaseguroid: " + linId + " ##");

		return linId.longValue();
	}

	/**
	 * Metodo para obtener la Entidad Mediadora y la subEntidadMediadora del
	 * colectivo correspondiente
	 * 
	 * @param id
	 *            Identificador de colectivo
	 * @param dc
	 *            Digito de control del colectivo
	 */
	@SuppressWarnings("unchecked")
	public static List<Colectivo> getEntMedSubEntMed(final Session session, Long id) {

		try {

			Criteria criteria = session.createCriteria(Colectivo.class);
			criteria.add(Restrictions.eq("id", id));

			return criteria.list();

		} catch (Exception ex) {
			logger.info("Se ha producido un error al obtener los datos del colectivos con id = " + id, ex);
		}
		return null;
	}

	// Metodo que actualiza el historico
	public static void actualizarHistorico(final PolizaRenovable polizaHbm, EstadoRenovacionAgroseguro estadoAgroSeguro,
			EstadoRenovacionAgroplus estadoAgroplus, List<Character> listGN, final Session session) {

		if (listGN == null || listGN.isEmpty()) {
			logger.debug("--actualizarHistorico: La lista de GN es vacia");
			return;
		}

		// Insertamos registro en el historico de estados
		Set<PolizaRenovableHistoricoEstados> historico = new HashSet<PolizaRenovableHistoricoEstados>();

		for (Character gn : listGN) {
			PolizaRenovableHistoricoEstados hist = new PolizaRenovableHistoricoEstados();
			hist.setEstadoRenovacionAgroplus(estadoAgroplus);
			hist.setEstadoRenovacionAgroseguro(estadoAgroSeguro);
			hist.setFecha(new Date());
			hist.setUsuario("BATCH");
			hist.setPolizaRenovable(polizaHbm);
			hist.setGrupoNegocio(gn);
			historico.add(hist);

			logger.debug("## Historico de la poliza " + polizaHbm.getReferencia() + " con GN: " + gn + " insertado ##");
		}

		polizaHbm.setPolizaRenovableHistoricoEstadoses(historico);
	}

}
