package com.rsi.agp.batch.seguimiento;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.managers.impl.HistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.SeguimientoPolizaBean;
import com.rsi.agp.core.managers.impl.SeguimientoPolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.dao.models.commons.HistoricoEstadosDao;
import com.rsi.agp.dao.models.poliza.PolizaDao;
import com.rsi.agp.dao.models.poliza.SeguimientoPolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

public class SeguimientoPolizas {

	private static final Logger logger = Logger
			.getLogger(SeguimientoPolizas.class);

	private static org.hibernate.SessionFactory sessionFactory;

	private static SeguimientoPolizaManager seguimientoPolizaManager;
	private static WebServicesManager webServicesManager;
	private static HistoricoEstadosManager historicoEstadosManager;

	private static SeguimientoPolizaDao seguimientoPolizaDao;
	private static PolizaDao polizaDao;
	private static HistoricoEstadosDao historicoEstadosDao;
	
	private static BigDecimal TIPO_POLIZA = BigDecimal.ONE;
	private static BigDecimal TIPO_INCIDENCIA = new BigDecimal(2);
	private static BigDecimal TIPO_POLIZA_NO_ENCONTRADA = new BigDecimal(3);
	private static BigDecimal TIPO_SUSPENSION_GARANTIAS = new BigDecimal(4);

	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.info("INICIO batch Seguimiento Pï¿½lizas");
			doWork();
			logger.info("FIN batch Seguimiento Pï¿½lizas");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error en el proceso batch de Seguimiento Pólizas", e);
			System.exit(1);
		} catch (Throwable e) {
			logger.error("Error en el proceso batch de Seguimiento Pólizas", e);
			System.exit(1);
		}
	}

	@SuppressWarnings("unchecked")
	private static void doWork() throws Throwable {
		try {
			logger.info(" Cargamos la configuracion necesaria ");
			getConfiguracion();
			SQLQuery query = sessionFactory.getCurrentSession()
					.createSQLQuery("TRUNCATE TABLE O02AGPE0.TB_TMP_BATCH_SEGUIMIENTO");
			query.executeUpdate();
			List<SeguimientoPolizaBean> polizasSeguimiento = seguimientoPolizaManager
					.getPolizasSeguimiento("@BATCH", "");
			for (SeguimientoPolizaBean spb : polizasSeguimiento) {
				logger.debug("");
				logger.debug("###########################");
				logger.debug("");
				logger.debug("Referencia: " + spb.getReferenciaPoliza() + " / " + spb.getTipoPoliza());
				Poliza poliza = polizaDao.getPolizaByReferenciaPlan(
						spb.getReferenciaPoliza(), spb.getTipoPoliza(),
						BigDecimal.valueOf(spb.getPlan()));
				if (poliza == null) {	
					// ESC-6299: si no existe la poliza, verificamos si existe la renovable...
					// en caso afirmativo, la actualizamos
					PolizaRenovable polizaRenovable = null;
					List<PolizaRenovable> aux = (List<PolizaRenovable>) polizaDao.getObjects(PolizaRenovable.class,
							"referencia", spb.getReferenciaPoliza());
					if (aux != null) {
						for (PolizaRenovable prAux : aux) {
							if (spb.getPlan() == prAux.getPlan().intValue()) {
								polizaRenovable = prAux;
								break;
							}
						}
					}
					if (polizaRenovable == null) {
						seguimientoPolizaManager.createTmpBatchSeguimiento(spb.getNifAsegurado(), spb.getNifTomador(),
								BigDecimal.valueOf(spb.getPlan()), BigDecimal.valueOf(spb.getLinea()),
								spb.getReferenciaPoliza(), spb.getTipoPoliza(), null, null, TIPO_POLIZA_NO_ENCONTRADA,
								"---", spb.getEstado().toString(), spb.getColectivo());
						logger.debug("Insertado registro tipo " + TIPO_POLIZA_NO_ENCONTRADA);
					} else {
						seguimientoPolizaManager.createTmpBatchSeguimiento(spb.getNifAsegurado(), spb.getNifTomador(),
								BigDecimal.valueOf(spb.getPlan()), BigDecimal.valueOf(spb.getLinea()),
								spb.getReferenciaPoliza(), spb.getTipoPoliza(),
								BigDecimal.valueOf(polizaRenovable.getColectivoRenovacion().getCodentidad().longValue()),
								"", // TODO: OFICINA DE RENOVABLE??? 
								TIPO_POLIZA, spb.getDesEstado(), spb.getEstado().toString(), spb.getColectivo());
						logger.debug("Insertado registro tipo " + TIPO_POLIZA + "para renovable");
						seguimientoPolizaManager.actualizarRenovable(spb, polizaRenovable.getId(), "@BATCH");
					}
				} else {
					seguimientoPolizaManager.createTmpBatchSeguimiento(spb.getNifAsegurado(), spb.getNifTomador(),
							BigDecimal.valueOf(spb.getPlan()), BigDecimal.valueOf(spb.getLinea()),
							spb.getReferenciaPoliza(), spb.getTipoPoliza(),
							poliza.getColectivo().getTomador().getEntidad().getCodentidad(),
							poliza.getOficina(),
							TIPO_POLIZA, spb.getDesEstado(), spb.getEstado().toString(), spb.getColectivo());
					logger.debug("Insertado registro tipo " + TIPO_POLIZA);
					seguimientoPolizaManager.actualizarPoliza(poliza.getIdpoliza(), spb, "@BATCH");
					for (es.agroseguro.seguimientoContratacion.InfoAdicional inf : spb.getInfoAdicional()) {
						// Codigo 1 es suspension de garantias... el resto se ignoran
						if (inf.getCodigo() == 1) {
							seguimientoPolizaManager.createTmpBatchSeguimiento(spb.getNifAsegurado(), spb.getNifTomador(),
									BigDecimal.valueOf(spb.getPlan()), BigDecimal.valueOf(spb.getLinea()),
									spb.getReferenciaPoliza(), spb.getTipoPoliza(),
									poliza.getColectivo().getTomador().getEntidad().getCodentidad(),
									poliza.getOficina(),
									TIPO_SUSPENSION_GARANTIAS, inf.getDescCausa(), "", spb.getColectivo());
							logger.debug("Insertado registro tipo " + TIPO_SUSPENSION_GARANTIAS);
						}
					}
					for (es.agroseguro.seguimientoContratacion.Incidencia inc : spb.getIncidencia()) {
						seguimientoPolizaManager.createTmpBatchSeguimiento(spb.getNifAsegurado(), spb.getNifTomador(),
								BigDecimal.valueOf(spb.getPlan()), BigDecimal.valueOf(spb.getLinea()),
								spb.getReferenciaPoliza(), spb.getTipoPoliza(),
								poliza.getColectivo().getTomador().getEntidad().getCodentidad(),
								poliza.getOficina(),
								TIPO_INCIDENCIA, inc.getDesEstado(), inc.getEstado(), spb.getColectivo());
						logger.debug("Insertado registro tipo " + TIPO_INCIDENCIA);
					}
				}
				logger.debug("");
			}
			sessionFactory.getCurrentSession().getTransaction().commit();
		} catch (Exception e) {
			logger.error("Error en doWork: " + e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error en doWork: " + e);
			throw e;
		} finally {
			if (sessionFactory.getCurrentSession().isOpen()) {
				sessionFactory.getCurrentSession().close();
			}
		}
	}

	private static void getConfiguracion() throws Throwable {
		try {
			// Configuracion hibernate
			Configuration cfg = new Configuration();
			// cargamos la sesion
			sessionFactory = cfg.configure().buildSessionFactory();
			seguimientoPolizaDao = new SeguimientoPolizaDao();
			polizaDao = new PolizaDao();
			historicoEstadosDao = new HistoricoEstadosDao();
			seguimientoPolizaManager = new SeguimientoPolizaManager();
			webServicesManager = new WebServicesManager();
			historicoEstadosManager = new HistoricoEstadosManager();
			// abrimos la sesion
			sessionFactory.getCurrentSession().beginTransaction();
			// Asignamos la session de hibernate a los Daos y managers
			historicoEstadosDao.setSessionFactory(sessionFactory);
			historicoEstadosManager.setHistoricoEstadosDao(historicoEstadosDao);
			seguimientoPolizaDao.setSessionFactory(sessionFactory);
			seguimientoPolizaDao.setHistoricoEstadosManager(historicoEstadosManager);
			polizaDao.setSessionFactory(sessionFactory);
			seguimientoPolizaManager
					.setSeguimientoPolizaDao(seguimientoPolizaDao);
			seguimientoPolizaManager.setPolizaDao(polizaDao);
			seguimientoPolizaManager.setWebServicesManager(webServicesManager);
		} catch (Throwable e) {
			logger.error("Error al cargar la configuracion " + e);
			throw e;
		}
	}
}
