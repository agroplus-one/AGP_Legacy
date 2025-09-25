package com.rsi.agp.batch.sobreprecio;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.managers.impl.HistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.sbp.SimulacionSbpManager;
import com.rsi.agp.dao.models.anexo.DeclaracionModificacionPolizaDao;
import com.rsi.agp.dao.models.commons.HistoricoEstadosDao;
import com.rsi.agp.dao.models.ged.DocumentacionGedDao;
import com.rsi.agp.dao.models.poliza.PolizaComplementariaDao;
import com.rsi.agp.dao.models.poliza.SeleccionPolizaDao;
import com.rsi.agp.dao.models.sbp.SimulacionSbpDao;
import com.rsi.agp.dao.models.sbp.SobrePrecioDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;


public class GeneraSuplementos {
	
	private static final Logger logger = Logger.getLogger(GeneraSuplementos.class);
	
	private static SimulacionSbpManager simulacionSbpManager ;
	private static SeleccionPolizaManager seleccionPolizaManager;
	private static SimulacionSbpDao simulacionSbpDao;
	private static SeleccionPolizaDao seleccionPolizaDao;
	private static PolizaComplementariaDao polizaComplementariaDao;
	private static org.hibernate.SessionFactory sessionFactory ;
	private static HistoricoEstadosManager historicoEstadosManager;
	private static HistoricoEstadosDao historicoEstadosDao ;
	private static DeclaracionModificacionPolizaDao declaracionModificacionPolizaDao;
	private static SobrePrecioDao sobrePrecioDao;
	private static DocumentacionGedDao documentacionGedDao;

	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.info("INICIO batch Genera suplementos sobreprecios");
			doWorkDiario();
			logger.info("FIN Batch Genera suplementos sobreprecios");			
			System.exit(0);
		} catch (Exception e) {
			logger.error(
					"Error en el proceso batch de Generar suplementos sobreprecios",
					e);
			System.exit(1);
		} catch (Throwable e) {
			logger.error(
					"Error en el proceso batch de Generar suplementos sobreprecios",
					e);
			System.exit(1);
		}
	}

	/**
	 * 1. comprobamos todos los A.M por cupon en estado 6 ó 7 con revisar_sp = s
	 * 2. recorremos todas las polizas de sobreprecio marcadas con "GEN_SPL_CPL" ='S'
	 *	  y comprobamos si hay que generar el suplemento
	 * 18/03/2014 U029769
	 */
	private static void doWorkDiario()throws Throwable {
		
		SessionFactory factory;
		Session session = null;
		Transaction trans = null;
		Usuario usuario = new Usuario();
		boolean haGeneradoSuplemento = false;
		List<Long> lstIdsPolSbp = new ArrayList<Long>();
		ArrayList<Long> lstIdsAnexos = new ArrayList<Long>();
		try {
			logger.info("*********** COMPROBACION DIARIA ***************");
			logger.info(" Cargamos la configuracion necesaria ");
			factory = getConfiguracion();
			session = factory.openSession();
			
			
			String realPath = ConfigSobreprecio.getProperty(ConfigSobreprecio.RUTA_WSDL);
			usuario.setCodusuario(ConfigSobreprecio.getProperty(ConfigSobreprecio.COD_USUARIO));
			
			logger.info("Cogemos A.M por cupon en estado 6 ó 7 con revisar_sp = s");
			lstIdsAnexos = simulacionSbpManager.checkAnexosCuponParaSbp(session);
			trans = session.beginTransaction();
			logger.info("buscamos polizas de sbp con el campo GEN_SPL_CPL ='S'");
			List<PolizaSbp> sbpList = simulacionSbpManager.getPolizasSbpParaSuplementos();
			if (sbpList.size() > 0) {
				for (int i=0; i<sbpList.size();i++) {
					PolizaSbp polSbp = sbpList.get(i);
					haGeneradoSuplemento = false;
					logger.info("--------------------------------------------------------");
					logger.info("-- Registro " + i + " de " + sbpList.size() + " / " + polSbp.getReferencia());
					logger.info("--------------------------------------------------------");
					try {
                        haGeneradoSuplemento = simulacionSbpManager.validaPolizaParaSuplemento(
                                polSbp.getPolizaPpal().getIdpoliza(), polSbp.getFechaEnvioSbp(), usuario, realPath, null,
                                false, session, true);
                        if (haGeneradoSuplemento){
                            logger.info("guardamos idPolSbp: "+ polSbp.getId());
                            lstIdsPolSbp.add(polSbp.getId());
                        }
                    } catch (Exception e) {
                        logger.error("Se ha producido una excepcion en la validacion de la poliza para suplemento. Referencia " + polSbp.getReferencia());
                    }
				}
			}else{
				logger.info("No hay polizas de sobreprecio con  GEN_SPL_CPL ='S'");
			}
			trans.commit();
			
			// actualizamos flag de la poliza sbp de los sobreprecios que hayan creado suplemento
			if (lstIdsPolSbp.size()>0)
				simulacionSbpManager.updateFlagbyIdPolSbp(lstIdsPolSbp);
			
			logger.info("Total anexos a actualizar: "+  lstIdsAnexos.size());
			if (lstIdsAnexos.size()>0)
				simulacionSbpManager.actualizaRevisar(lstIdsAnexos);
			
			generateAndSendMail (session, true,lstIdsPolSbp.size());
			
			session.close();
			
			logger.info("*************** FIN COMPROBACION DIARIA  *****************");
			
			
			
		} catch (Exception e) {
			logger.error("Error en doWorkDiario" + e);
			
			generateAndSendMail (session, false,0);
			
			if (session.isOpen()) {
				session.close();
			}
			
		} catch (Throwable e) {
			logger.error("Error en doWorkDiario" + e);
			
			generateAndSendMail (session, false,0);
			
			if (session.isOpen()) {
				session.close();
			}
			
		}finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}
	
	private static SessionFactory getConfiguracion() throws Throwable {
		
		try {
			//Configuracion hibernate
			Configuration cfg = new Configuration();
			//cargamos la sesion
			sessionFactory =  cfg.configure().buildSessionFactory();
			
			//Creamos los managers y DAOS necesarios
			simulacionSbpManager = new SimulacionSbpManager();
			seleccionPolizaManager = new SeleccionPolizaManager();
			simulacionSbpDao = new SimulacionSbpDao();
			seleccionPolizaDao = new SeleccionPolizaDao();
			polizaComplementariaDao = new PolizaComplementariaDao();
			historicoEstadosManager = new HistoricoEstadosManager();
			historicoEstadosDao = new HistoricoEstadosDao();
			declaracionModificacionPolizaDao = new DeclaracionModificacionPolizaDao();
			sobrePrecioDao = new SobrePrecioDao();
			
	    	//abrimos la sesion
	    	sessionFactory.getCurrentSession().beginTransaction();
	    	
	    	//Asignamos la session de hibernate a los Daos y managers
	    	declaracionModificacionPolizaDao.setSessionFactory(sessionFactory);
	    	simulacionSbpDao.setSessionFactory(sessionFactory);
	    	polizaComplementariaDao.setSessionFactory(sessionFactory);
	    	historicoEstadosDao.setSessionFactory(sessionFactory);
	    	sobrePrecioDao.setSessionFactory(sessionFactory);
	    	documentacionGedDao.setSessionFactory(sessionFactory);
	    	
	    	simulacionSbpDao.setDeclaracionModificacionPolizaDao(declaracionModificacionPolizaDao);
	    	simulacionSbpDao.setDocumentacionGedDao(documentacionGedDao);
	    	
	    	simulacionSbpManager.setPolizaComplementariaDao(polizaComplementariaDao);
	    	simulacionSbpManager.setSimulacionSbpDao(simulacionSbpDao);
	    	historicoEstadosManager.setHistoricoEstadosDao(historicoEstadosDao);
	    	simulacionSbpManager.setHistoricoEstadosManager(historicoEstadosManager);
	    	
	    	seleccionPolizaDao.setSessionFactory(sessionFactory);
	    	seleccionPolizaManager.setSeleccionPolizaDao(seleccionPolizaDao);
	    	simulacionSbpManager.setSeleccionPolizaManager(seleccionPolizaManager);
	    	simulacionSbpManager.setSobrePrecioDao(sobrePrecioDao);
		}catch (Throwable e) {
			logger.error("Error al cargar la configuracion " + e);
			throw e ;
		}
		return sessionFactory;
	}
	
	private static void generateAndSendMail(final Session session, boolean correcto, int contSuplementos) {
		
		Query query = session
				.createSQLQuery(
						"CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreo(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", "3")
				.setParameter("asunto", "Alerta Agroplus: Resumen de generacion de suplementos - " + (correcto ? "OK" : "ERROR"))
				.setParameter("mensaje", "Suplementos creados: "+contSuplementos+ System.getProperty("line.separator"));

		query.executeUpdate();
		
	}
}
