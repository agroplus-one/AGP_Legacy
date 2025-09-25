package com.rsi.agp.dao.models.comisiones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.EntidadesOperadoresInforme;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.comisiones.InformeMediadoresMeses;
import com.rsi.agp.dao.tables.comisiones.RgaComisiones;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;
import com.rsi.agp.dao.tables.comisiones.unificado.RgaUnifMediadores;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeColaboradores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFacturacion;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFamLinEnt;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsImpagados2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsRGA2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeCorredores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeDetMediador2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidades2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidadesOperadores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeTotMediador2015;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformesExcelDao extends BaseDaoHibernate implements IInformesExcelDao {
	
	private static final Log logger = LogFactory.getLog(InformesExcelDao.class);

	private BigDecimal codEntidad4milDesde = new BigDecimal(4000);
	private BigDecimal codEntidad7milHasta = new BigDecimal(6999);

	// informes cierre 2015+
	private Integer codEnt4milDesde = new Integer(4000);
	private Integer codEnt7milHasta = new Integer(6999);
	private Integer codEnt3milDesde = new Integer(3000);
	private Integer codEnt4milHasta = new Integer(3999);

	@SuppressWarnings("unchecked")
	public List<InformeMediadores> listMediadores(final Date fechaCierre) throws DAOException {
		logger.debug("init - listMediadoresMeses");
		Session session = obtenerSession();

		List<InformeMediadores> informesMediadores = new ArrayList<InformeMediadores>();
		List<BigDecimal> idsInformesMediadores = new ArrayList<BigDecimal>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			BigDecimal anyo = new BigDecimal(sdf.format(fechaCierre));

			Criteria criteria = session.createCriteria(InformeMediadoresMeses.class);
			criteria.createAlias("informeMediadores", "informeMediadores");
			criteria.add(Restrictions.eq("anyo", anyo));
			criteria.add(Restrictions.between("informeMediadores.subentidadMediadora.id.codentidad",
					codEntidad4milDesde, codEntidad7milHasta));
			criteria.addOrder(Order.asc("informeMediadores.id"));
			criteria.setProjection(Projections
					.distinct(Projections.projectionList().add(Projections.property("informeMediadores.id"))));
			idsInformesMediadores = criteria.list();
			if (idsInformesMediadores.size() > 0) {

				informesMediadores = getListInformeMediadores(idsInformesMediadores);
			}

			logger.debug("end - listMediadoresMeses");
			return informesMediadores;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<RgaUnifMediadores> listMediadores2015(final Date fechaCierre, boolean segGen) throws DAOException {
		logger.debug("init - listMediadoresSegGenerales2015");
		Session session = obtenerSession();

		List<RgaUnifMediadores> informesMediadores2015 = new ArrayList<RgaUnifMediadores>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			BigDecimal anyo = new BigDecimal(sdf.format(fechaCierre));

			Criteria criteria = session.createCriteria(RgaUnifMediadores.class);
			criteria.add(Restrictions.eq("anyo", anyo.intValue()));
			criteria.addOrder(Order.asc("entidad"));
			criteria.addOrder(Order.asc("subentidad"));
			criteria.addOrder(Order.asc("mes"));

			if (segGen) {
				criteria.add(Restrictions.between("entidad", codEnt4milDesde, codEnt7milHasta));
			} else {
				criteria.add(Restrictions.between("entidad", codEnt3milDesde, codEnt4milHasta));
				criteria.add(Restrictions.ne("subentidad", new Integer(0)));
			}

			informesMediadores2015 = criteria.list();
			logger.debug("end - listMediadoresSegGenerales2015");
			return informesMediadores2015;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	private List<InformeMediadores> getListInformeMediadores(final List<BigDecimal> idsInformesMediadores)
			throws DAOException {
		Session session = obtenerSession();
		List<InformeMediadores> listInformeMediadores = new ArrayList<InformeMediadores>();

		try {
			Criteria criteria = session.createCriteria(InformeMediadores.class);
			Disjunction disjunction = Restrictions.disjunction();
			for (int i = 0; i < idsInformesMediadores.size(); i++) {
				disjunction.add(Restrictions.eq("id", idsInformesMediadores.get(i)));
			}

			criteria.add(disjunction);
			criteria.addOrder(Order.asc("id"));

			listInformeMediadores = criteria.list();

			return listInformeMediadores;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public BigDecimal getNumeroMaximoMes(final Date fechaCierre) throws DAOException {
		Session session = obtenerSession();
		BigDecimal numMes = new BigDecimal(0);
		List listAux = new ArrayList();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			BigDecimal anyo = new BigDecimal(sdf.format(fechaCierre));

			Criteria criteria = session.createCriteria(InformeMediadoresMeses.class);
			criteria.add(Restrictions.eq("anyo", anyo));
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.max("mes"));
			criteria.setProjection(projectionList);

			listAux = criteria.list();

			if (listAux.get(0) != null) {
				numMes = (BigDecimal) listAux.get(0);
			}
			return numMes;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public BigDecimal getNumeroMaximoMes2015(final Date fechaCierre) throws DAOException {
		Session session = obtenerSession();
		BigDecimal numMes = new BigDecimal(0);
		List listAux = new ArrayList();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			BigDecimal anyo = new BigDecimal(sdf.format(fechaCierre));

			Criteria criteria = session.createCriteria(RgaUnifMediadores.class);
			criteria.add(Restrictions.eq("anyo", anyo.intValue()));
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.max("mes"));
			criteria.setProjection(projectionList);

			listAux = criteria.list();

			if (listAux.get(0) != null) {

				numMes = new BigDecimal(listAux.get(0).toString());
			}
			return numMes;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<RgaComisiones> listComisionesEntidades(final Long idCierre) throws DAOException {
		logger.debug("init - listComisionesEntidades");
		Session session = obtenerSession();
		List resultado = new ArrayList();
		try {
			String sql = " select sum(rc.PRISUM) ,sum(rc.GASENTSUM) ,sum(rc.GASSUBSUM) ,sum(rc.COMENTSUM) ,sum(rc.COMSUBSUM), "
					+ " sum(rc.IMPTRA),sum(rc.IMPCAL) ,rc.NUMFAS ,rc.CODENTMED ,rc.CODSUBMED ,rc.CODCOL ,rc.CODPLN ,rc.CODLIN ,"
					+ " rp.gaspen from o02agpe0.tb_rga_comisiones_pendientes rp,o02agpe0.tb_rga_comisiones rc,o02agpe0.TB_COMS_CIERRE c"
					+ " where (rc.CODENTMED between 3000 and 3999 or rc.CODENTMED between 8000 and 8999) and rp.codent(+) = rc.codentmed"
					+ " and rp.codlin (+)= rc.codlin and rp.codpln (+)= rc.codpln and (SUBSTR(rp.codcol(+),0,7))=rc.codcol and rp.fase(+) = rc.numfas"
					+ " and c.id =" + idCierre + " and c.id = rc.idcierre and rp.idcierre(+) = rc.idcierre "
					+ " group by rc.NUMFAS, rc.CODENTMED, rc.CODSUBMED, rc.CODCOL, rc.CODPLN, rc.CODLIN ,rp.gaspen"
					+ " order by rc.CODENTMED asc, rc.CODSUBMED asc";
			logger.info("sql " + sql);
			List<RgaComisiones> listComisionesEntidades = new ArrayList<RgaComisiones>();

			resultado = session.createSQLQuery(sql).list();

			if (resultado.size() > 0) {
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					RgaComisiones aux = new RgaComisiones();
					if (registro[0] != null) {
						aux.setPrisum((BigDecimal) registro[0]);
					}
					if (registro[1] != null) {
						aux.setGasentsum((BigDecimal) registro[1]);
					}
					if (registro[2] != null) {
						aux.setGassubsum((BigDecimal) registro[2]);
					}
					if (registro[3] != null) {
						aux.setComentsum((BigDecimal) registro[3]);
					}
					if (registro[4] != null) {
						aux.setComsubsum((BigDecimal) registro[4]);
					}
					if (registro[5] != null) {
						aux.setImptra((BigDecimal) registro[5]);
					}
					if (registro[6] != null) {
						aux.setImpcal((BigDecimal) registro[6]);
					}
					if (registro[7] != null) {
						aux.setNumfas((String) registro[7]);
					}
					if (registro[8] != null) {
						aux.setCodentmed((BigDecimal) registro[8]);
					}
					if (registro[9] != null) {
						aux.setCodsubmed((BigDecimal) registro[9]);
					}
					if (registro[10] != null) {
						aux.setCodcol((String) registro[10]);
					}
					if (registro[11] != null) {
						aux.setCodpln((BigDecimal) registro[11]);
					}
					if (registro[12] != null) {
						aux.setCodlin((BigDecimal) registro[12]);
					}
					if (registro[13] != null) {
						aux.setGaspen((BigDecimal) registro[13]);
					}
					listComisionesEntidades.add(aux);
				}
			}
			return listComisionesEntidades;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InformeEntidades2015> listComisionesEntidades2015() throws DAOException {
		logger.debug("init - listComisionesEntidades2015");
		Session session = obtenerSession();
		try {

			List<InformeEntidades2015> listComisionesEntidades2015 = new ArrayList<InformeEntidades2015>();

			Criteria criteria = session.createCriteria(InformeEntidades2015.class);
			listComisionesEntidades2015 = criteria.list();

			logger.debug("InformesExcelDao - listColaboradores2015 - end");

			return listComisionesEntidades2015;
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listComisionesEntidades2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InformeDetMediador2015> listDetalleMediador2015() throws DAOException {
		logger.debug("init - listDetalleMediador2015");
		Session session = obtenerSession();
		try {
			
			List<InformeDetMediador2015> listDetMediadores2015 = new ArrayList<InformeDetMediador2015>();

			Criteria criteria = session.createCriteria(InformeDetMediador2015.class);
			listDetMediadores2015 = criteria.list();
			logger.debug("InformesExcelDao - listDetalleMediador2015 - end");
			return listDetMediadores2015;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listComisionesEntidades2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<RgaComisiones> listDetalleMediador(final Long idCierre) throws DAOException {
		logger.debug("init - listDetalleMediador");
		Session session = obtenerSession();
		List resultado = new ArrayList();
		List<RgaComisiones> listaComisiones = new ArrayList<RgaComisiones>();
		try {
			String sql = " select sum(rc.comentsum),sum(rc.comsubsum),sum(rc.gassubsum),rp.gaspen,rc.numfas,rc.codentmed,"
					+ " rc.codsubmed,rc.codcol,rc.codpln,rc.CODLIN  "
					+ " from  o02agpe0.tb_rga_comisiones_pendientes rp,o02agpe0.tb_rga_comisiones rc,o02agpe0.TB_COMS_CIERRE c"
					+ " where  (codentmed  between 4000 and 6999 or codentmed  between 3000 and 3999 and codsubmed !=0) and"
					+ " rp.codlin (+)= rc.codlin and rp.codpln (+)= rc.codpln and (SUBSTR(rp.codcol(+),0,7))=rc.codcol"
					+ " and c.id =" + idCierre + " and c.id = rc.idcierre and rp.idcierre(+) = rc.idcierre"
					+ " and rp.fase(+) = rc.numfas "
					+ " group by rc.NUMFAS, rc.CODENTMED, rc.CODSUBMED, rc.CODCOL, rc.CODPLN, rc.CODLIN ,rp.gaspen"
					+ " order by rc.numfas,rc.codentmed,rc.codsubmed,rc.codcol asc";

			logger.info("sql " + sql);
			resultado = session.createSQLQuery(sql).list();

			if (resultado.size() > 0) {
				// Se copian los resultados obtenidos a una lista de RgaComisiones
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					RgaComisiones aux = new RgaComisiones();

					if (registro[0] != null) {
						aux.setComentsum((BigDecimal) registro[0]);
					}

					if (registro[1] != null) {
						aux.setComsubsum((BigDecimal) registro[1]);
					}

					if (registro[2] != null) {
						aux.setGassubsum((BigDecimal) registro[2]);
					}

					if (registro[3] != null) {
						aux.setGaspen((BigDecimal) registro[3]);
					}

					if (registro[4] != null) {
						aux.setNumfas((String) registro[4]);
					}

					if (registro[5] != null) {
						aux.setCodentmed((BigDecimal) registro[5]);
					}

					if (registro[6] != null) {
						aux.setCodsubmed((BigDecimal) registro[6]);
					}

					if (registro[7] != null) {
						aux.setCodcol((String) registro[7]);
					}

					if (registro[8] != null) {
						aux.setCodpln((BigDecimal) registro[8]);
					}

					if (registro[9] != null) {
						aux.setCodlin((BigDecimal) registro[9]);
					}
					listaComisiones.add(aux);
				}
			}

			logger.debug("end - listDetalleMediador");
			return listaComisiones;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	private Colectivo getColectivoByRef(final String colectivoref) throws DAOException {
		logger.debug("init - getCierreByFecha");
		Session session = obtenerSession();
		List<Colectivo> listAux = new ArrayList<Colectivo>();
		Colectivo colectivo = null;
		try {

			Criteria criteria = session.createCriteria(Colectivo.class);
			criteria.add(Restrictions.eq("idcolectivo", colectivoref));
			listAux = criteria.list();

			if (listAux.size() > 0) {
				colectivo = listAux.get(0);
			}
			logger.debug("init - getCierreByFecha");

			return colectivo;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}

	}

	@SuppressWarnings("rawtypes")
	public List<ReciboImpagado> listImpagados(final Cierre cierre) throws DAOException {
		Set<Fase> setFases = cierre.getFases();
		List<ReciboImpagado> listImpagados = new ArrayList<ReciboImpagado>();
		List resultado = new ArrayList();
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ReciboImpagado.class);
			criteria.createAlias("fichero", "fichero");
			criteria.createAlias("fichero.fase", "fasefichero");

			// Impagados de todas las fases del cierre actual
			Disjunction dd = Restrictions.disjunction();
			for (Fase fase : setFases) {
				dd.add(Restrictions.eq("fasefichero.id", fase.getId()));
			}

			criteria.add(dd);

			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.sum("caTotalgastos"));
			projectionList.add(Projections.groupProperty("colectivoreferencia"));
			projectionList.add(Projections.groupProperty("recibo"));

			criteria.setProjection(projectionList);
			criteria.addOrder(Order.asc("colectivoreferencia"));
			criteria.addOrder(Order.asc("recibo"));

			resultado = criteria.list();

			if (resultado.size() > 0) {

				// Se copian los resultados obtenidos a una lista de
				// RgaComisiones
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					ReciboImpagado aux = new ReciboImpagado();

					// Se obtiene el colectivo para obtener los datos de
					// entidad, tomador, etc
					if (registro[1] != null) {
						String refColectivo = (String) registro[1];
						Colectivo colectivo = getColectivoByRef(refColectivo);

						if (colectivo != null) {
							SubentidadMediadora subEnt = colectivo.getSubentidadMediadora();

							if (registro[0] != null) {
								aux.setPaGastoscomisiones((BigDecimal) registro[0]);
							}

							if (registro[2] != null) {
								aux.setRecibo((BigDecimal) registro[2]);
							}

							if ((colectivo.getTomador() != null) && (colectivo.getTomador().getRazonsocial() != null)) {
								aux.setRazonsocial(colectivo.getTomador().getRazonsocial());
							}

							// Se guarda la entidad en individualcodinterno
							// porque es para el mismo tipo de datos
							// Esto se hace para no tener que obtener de
							// nuevo el codentidad cuando se genere el excel
							aux.setColectivocodinterno(
									subEnt.getId().getCodentidad() + "-" + subEnt.getId().getCodsubentidad());
							aux.setColectivoreferencia(refColectivo);
							listImpagados.add(aux);
						}
					}
				}
			}
			return listImpagados;
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InformeComsImpagados2015> listImpagados2015(final Long idCierre) throws DAOException {
		logger.debug("init - listImpagados2015");
		Session session = obtenerSession();
		List<InformeComsImpagados2015> listDetMediadores2015 = new ArrayList<InformeComsImpagados2015>();
		try {
			Criteria criteria = session.createCriteria(InformeComsImpagados2015.class);
			criteria.add(Restrictions.eq("idcierre", idCierre.intValue()));
			listDetMediadores2015 = criteria.list();
			return listDetMediadores2015;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listImpagados2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InformeComsRGA2015> listComsRGA2015() throws DAOException {
		logger.debug("init - listComsRGA2015");
		Session session = obtenerSession();
		List<InformeComsRGA2015> listComsRGA2015 = new ArrayList<InformeComsRGA2015>();
		try {
			Criteria criteria = session.createCriteria(InformeComsRGA2015.class);
			listComsRGA2015 = criteria.list();
			return listComsRGA2015;
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listImpagados2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<RgaComisiones> listTotalesMediador(final Long idCierre) throws DAOException {
		logger.debug("init - listTotalesMediador");
		List<RgaComisiones> listTotalesMediador = new ArrayList<RgaComisiones>();
		List resultado = new ArrayList();
		Session session = obtenerSession();
		try {

			String sql = " select this_.codentmed ,this_.codsubmed ,this_.NUMFAS ,this_.CODPLN ,this_.CODLIN ,this_.CODCOL ,this_.REFPLZ ,this_.NIFASG ,"
					+ " this_.NOMASG ,this_.PRISUM ,sum(this_.GASSUBSUM) ,sum(this_.COMSUBSUM)"
					+ " from o02agpe0.TB_RGA_COMISIONES this_ inner join o02agpe0.TB_COMS_CIERRE cierre1_ on this_.IDCIERRE=cierre1_.ID "
					+ " where cierre1_.ID=" + idCierre
					+ " and (codentmed <3000 or codentmed >3999) and (codentmed <8000 or codentmed >8999)"
					+ " group by this_.codentmed ,this_.codsubmed, this_.NUMFAS, this_.CODPLN, this_.CODLIN, this_.CODCOL, this_.REFPLZ, this_.NIFASG, "
					+ " this_.NOMASG, this_.PRISUM order by this_.NUMFAS asc,this_.codentmed asc ";

			logger.info("sql " + sql);
			resultado = session.createSQLQuery(sql).list();

			if (resultado.size() > 0) {
				// Se copian los resultados obtenidos a una lista de RgaComisiones
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					RgaComisiones aux = new RgaComisiones();

					if (registro[0] != null) {
						aux.setCodentmed((BigDecimal) registro[0]);
					}
					if (registro[1] != null) {
						aux.setCodsubmed((BigDecimal) registro[1]);
					}

					if (registro[2] != null) {
						aux.setNumfas((String) registro[2]);
					}

					if (registro[3] != null) {
						aux.setCodpln((BigDecimal) registro[3]);
					}

					if (registro[4] != null) {
						aux.setCodlin((BigDecimal) registro[4]);
					}

					if (registro[5] != null) {
						aux.setCodcol((String) registro[5]);
					}

					if (registro[6] != null) {
						aux.setRefplz((String) registro[6]);
					}

					if (registro[7] != null) {
						aux.setNifasg((String) registro[7]);
					}

					if (registro[8] != null) {
						aux.setNomasg((String) registro[8]);
					}

					if (registro[9] != null) {
						aux.setPrisum((BigDecimal) registro[9]);
					}
					if (registro[10] != null) {
						aux.setGassubsum((BigDecimal) registro[10]);
					}
					if (registro[11] != null) {
						aux.setComsubsum((BigDecimal) registro[11]);
					}
					listTotalesMediador.add(aux);
				}
			}

			logger.debug("end - listTotalesMediador");
			return listTotalesMediador;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InformeTotMediador2015> listTotalesMediador2015() throws DAOException {
		logger.debug("init - listTotalesMediador2015");
		Session session = obtenerSession();
		List<InformeTotMediador2015> listTotalesMediador2015 = new ArrayList<InformeTotMediador2015>();
		try {
			Criteria criteria = session.createCriteria(InformeTotMediador2015.class);
			listTotalesMediador2015 = criteria.list();
			return listTotalesMediador2015;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listComisionesEntidades2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InformeEntidadesOperadores2015> listEntidadesOperadores2015() throws DAOException {
		logger.debug("init - listEntidadesOperadores2015");
		Session session = obtenerSession();
		List<InformeEntidadesOperadores2015> listEntidadesOperadores2015 = new ArrayList<InformeEntidadesOperadores2015>();
		try {
			Criteria criteria = session.createCriteria(InformeEntidadesOperadores2015.class);
			listEntidadesOperadores2015 = criteria.list();
			return listEntidadesOperadores2015;
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listEntidadesOperadores2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<EntidadesOperadoresInforme> listEntidadesOperadores(final Long idCierre) throws DAOException {
		logger.debug("init - listTotalesMediador");
		List<EntidadesOperadoresInforme> listEntidadesOperadores = new ArrayList<EntidadesOperadoresInforme>();
		List resultado = new ArrayList();
		Session session = obtenerSession();
		try {

			String sql = " select codentmed,codsubmed,sum(gasentsum)as gasentsum,sum(comentsum)as comentsum,sum(imptra) as imptra,sum(impcal) as impcal,"
					+ " sum(gassubsum) as gassubsum,sum(comsubsum) as comsubsum,sum(gastoentidad) as gastoentidad,sum(gastoscomisiones) as gastoscomisiones,"
					+ " pagodirecto,sum(gaspen) from (select r.codentmed,r.codsubmed,sum(r.gasentsum) as gasentsum,sum(r.comentsum) as comentsum,"
					+ " sum(r.imptra) as imptra,sum(r.impcal)as impcal,sum(r.gassubsum)as gassubsum,sum(r.comsubsum) as comsubsum,"
					+ " sum(r.gastosentidad) as gastoentidad,sum(r.gastoscomisiones) as gastoscomisiones,s.pagodirecto,rp.gaspen"
					+ " from o02agpe0.tb_rga_comisiones r,o02agpe0.tb_coms_cierre c,o02agpe0.tb_subentidades_mediadoras s,o02agpe0.tb_rga_comisiones_pendientes rp"
					+ " where c.id = " + idCierre
					+ " and c.id = r.idcierre and r.codentmed = s.codentidad and r.codsubmed = s.codsubentidad"
					+ " and rp.codent(+)= r.codent and rp.codlin (+)= r.codlin and rp.codpln (+)= r.codpln and (SUBSTR(rp.codcol(+),0,7))=r.codcol"
					+ " and rp.idcierre(+) = r.idcierre and rp.fase(+) = r.numfas and (r.codentmed >= 3000 and r.codentmed <= 3999 or"
					+ " r.codentmed >= 8000 and r.codentmed <= 8999) group by r.codentmed, r.codsubmed, s.pagodirecto,rp.gaspen"
					+ " order by r.codentmed, codsubmed)group by codentmed, codsubmed, pagodirecto order by codentmed, codsubmed";

			resultado = session.createSQLQuery(sql).list();
			if (resultado.size() > 0) {
				// Se copian los resultados obtenidos a una lista de EntidadesOperadoresInforme
				for (int i = 0; i < resultado.size(); i++) {
					Object[] registro = (Object[]) resultado.get(i);
					EntidadesOperadoresInforme aux = new EntidadesOperadoresInforme();
					// CODENTIDAD
					if (registro[0] != null) {
						aux.setCodEntMed((BigDecimal) registro[0]);
					}
					// CODSUBENTIDAD
					if (registro[1] != null) {
						aux.setCodSubMed((BigDecimal) registro[1]);
					}
					BigDecimal ggeEntidad = StringUtils.nullToZero((BigDecimal) registro[2]); // gasentsum
					BigDecimal ccEntidad = StringUtils.nullToZero((BigDecimal) registro[3]); // comentsum
					BigDecimal imptra = StringUtils.nullToZero((BigDecimal) registro[4]); // imptra
					BigDecimal impcal = StringUtils.nullToZero((BigDecimal) registro[5]); // impcal
					BigDecimal ggeMediador = StringUtils.nullToZero((BigDecimal) registro[6]); // gassubsum
					BigDecimal ccMediador = StringUtils.nullToZero((BigDecimal) registro[7]); // comsubsum
					BigDecimal gasPen = StringUtils.nullToZero((BigDecimal) registro[11]); // gasPen ->
																							// rga_comisiones_pendientes
					// FASES
					if (aux.getCodEntMed() != null) {
						if (aux.getCodEntMed().compareTo(new BigDecimal(3000)) >= 0
								&& aux.getCodEntMed().compareTo(new BigDecimal(3999)) <= 0) {

							aux.setFases((ggeEntidad.add(ccEntidad).add(imptra).add(impcal)).subtract(gasPen));

						} else if (aux.getCodEntMed().compareTo(new BigDecimal(8000)) >= 0
								&& aux.getCodEntMed().compareTo(new BigDecimal(8999)) <= 0) {
							aux.setFases(ggeMediador.add(ccMediador).add(imptra).add(impcal));
						}
					}
					// COOPERATIVAS
					// aux.setCooperativas((ggeMediador.add(ccMediador)).subtract(gasPen));
					aux.setCooperativas((ggeMediador.add(ccMediador)).add(ggeMediador));
					// IMPAGADOS
					BigDecimal gastosentidad = StringUtils.nullToZero((BigDecimal) registro[8]); // gastosentidad
					BigDecimal gastoscomisiones = StringUtils.nullToZero((BigDecimal) registro[9]); // gastoscomisiones
					aux.setImpagados(gastosentidad.add(gastoscomisiones));

					// PAGO DIRECTO
					if (registro[10] != null) {
						aux.setPagoDirecto((String) registro[10]);
					}

					listEntidadesOperadores.add(aux);
				}
			}

			logger.debug("end - listTotalesMediador");
			return listEntidadesOperadores;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void saveFicheroExcelCierre(final String nombreFicheroExcel, final String rutaFicheroExcel, final Long idCierre,
			final Usuario usuario) throws DAOException, IOException {
		Session session = obtenerSession();
		Connection con = session.connection();
		try {
			if (idCierre != null) {
				logger.debug("saveFicheroExcelCierre: " + idCierre);
				logger.debug("nombreFicheroExcel: " + nombreFicheroExcel);
				logger.debug("rutaFicheroExcel: " + rutaFicheroExcel);
				File file = new File(rutaFicheroExcel);
				if (file.exists()) {
					logger.debug("Fichero grabado en filesyste. Persistimos en BBDD.");
					String sql = "INSERT INTO o02agpe0.TB_COMS_REPORT_FICHEROS_CIERRE"
							+ " (id,idcierre,nom_fich_cierre,contenido) " + " values(SQ_COMS_FICHEROS_CIERRE.nextval,"
							+ idCierre + ",'" + nombreFicheroExcel + "',?)";					
					con.setAutoCommit(false);
					try (FileInputStream fis = new FileInputStream(file);
							PreparedStatement ps = con.prepareStatement(sql)) {
						ps.setBinaryStream(1, fis, (int) file.length());
						ps.executeUpdate();
					}
					con.commit();
					con.close();
					// TMR. Facturacion
					callFacturacion(usuario, Constants.FACTURA_IMPRESION);
				} else {
					logger.debug("Fichero NO grabado en filesystem.");
				}
			}
		} catch (SQLException e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InformeColaboradores2015> listColaboradores2015() throws DAOException {
		
		logger.debug("InformesExcelDao - listColaboradores2015 - init");
		
		Session session = obtenerSession();
		List<InformeColaboradores2015> listColaboradores2015 = new ArrayList<InformeColaboradores2015>();
		try {
			Criteria criteria = session.createCriteria(InformeColaboradores2015.class);
			listColaboradores2015 = criteria.list();

			logger.debug("InformesExcelDao - listColaboradores2015 - end");

			return listColaboradores2015;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listColaboradores2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InformeCorredores2015> listCorredores2015() throws DAOException {
		
		logger.debug("InformesExcelDao - listCorredores2015 - init");
		
		Session session = obtenerSession();
		List<InformeCorredores2015> listCorredores2015 = new ArrayList<InformeCorredores2015>();
		
		try {
			
			Criteria criteria = session.createCriteria(InformeCorredores2015.class);
			listCorredores2015 = criteria.list();
			logger.debug("InformesExcelDao - listCorredores2015 - end");

			return listCorredores2015;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listCorredores2015:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public List<InformeComsFamLinEnt> listComsFamLinEnt() throws DAOException {
			
		logger.debug("InformesExcelDao - listComsFamLinEnt - init");
		
		Session session = obtenerSession();
		List<InformeComsFamLinEnt> listComsFamLinEnt = new ArrayList<InformeComsFamLinEnt>();
		
		try {
			Criteria criteria = session.createCriteria(InformeComsFamLinEnt.class);
			listComsFamLinEnt = criteria.list();

			logger.debug("InformesExcelDao - listComsFamLinEnt - end");

			return listComsFamLinEnt;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listComsFamLinEnt:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InformeComsFacturacion> listComsFacturacion() throws DAOException {
		
		logger.debug("InformesExcelDao - listComsFacturacion - init");
		
		Session session = obtenerSession();
		List<InformeComsFacturacion> listComsFacturacion = new ArrayList<InformeComsFacturacion>();
		
		try {
			Criteria criteria = session.createCriteria(InformeComsFacturacion.class);
			listComsFacturacion = criteria.list();

			logger.debug("InformesExcelDao - listComsFacturacion - end");

			return listComsFacturacion;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD - listComsFacturacion:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}
}