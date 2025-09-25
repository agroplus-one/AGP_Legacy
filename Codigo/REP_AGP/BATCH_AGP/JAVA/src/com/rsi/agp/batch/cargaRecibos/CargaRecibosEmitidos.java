package com.rsi.agp.batch.cargaRecibos;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.util.XmlComisionesValidationUtil;
import com.rsi.agp.core.util.ZipUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.comisiones.FaseDao;
import com.rsi.agp.dao.models.comisiones.ImportacionFicherosEmitidosDao;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.tables.commons.Usuario;

public class CargaRecibosEmitidos {

	private static final Logger logger = Logger.getLogger(CargaRecibosEmitidos.class);

	static ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_ficheros");

	private static org.hibernate.SessionFactory sessionFactory;

	private static DatabaseManager databaseManager;

	private static ImportacionFicherosEmitidosDao importacionFicherosEmitidosDao;
	private static FaseDao faseDao;

	public static void main(String[] args) {

		try {

			BasicConfigurator.configure();
			logger.info("INICIO batch Carga Recibos Emitidos");
			String fechaPlanif = "";
			if (args.length!=0)
				fechaPlanif = args[0];
			doWork(fechaPlanif);
			logger.info("FIN batch Carga Recibos Emitidos");
			System.exit(0);
		} catch (Exception e) {

			logger.error("Error en el proceso batch de Carga Recibos Emitidos", e);
			System.exit(1);
		} catch (Throwable e) {

			logger.error("Error en el proceso batch de Carga Recibos Emitidos", e);
			System.exit(1);
		}
	}

	private static void doWork(String fechaPlanif) throws Throwable {

		try {

			logger.info("Cargamos la configuracion necesaria.");
			getConfiguracion();

			sessionFactory.getCurrentSession().beginTransaction();

			cleanConfigAgpParams();

			Usuario usuario = (Usuario) importacionFicherosEmitidosDao.get(Usuario.class,
					bundle.getString("usuario.carga.recibos"));

			sessionFactory.getCurrentSession().getTransaction().commit();
			
			logger.info("Fecha recibida como parametro: " + fechaPlanif);
			
			String ruta = bundle.getString("ruta.recibos.emitidos");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			if (StringUtils.isNullOrEmpty(fechaPlanif)) 
				ruta += sdf.format(new Date());
			else
				ruta += sdf.format(new SimpleDateFormat("yyyyMMdd").parse(fechaPlanif));
			logger.info("Obtenemos los ficheros de: " + ruta);

			int numFicheros = 0;
			String errorLst = "";
			String dupLst = "";
			
			File folder = new File(ruta);
			if (folder.exists() && folder.canRead()) {

				for (final File fileEntry : folder.listFiles()) {

					if (!fileEntry.isDirectory()) {

						String fileName = fileEntry.getName().toUpperCase();

						if (fileName.endsWith(".ZIP")) {

							logger.debug("Procesando: " + fileName);
							numFicheros++;

							try {

								File temp = ZipUtil.getFirstFileInZip(fileEntry);

								es.agroseguro.recibos.emitidos.FaseDocument fase = (es.agroseguro.recibos.emitidos.FaseDocument) XmlComisionesValidationUtil
										.getXMLBeanValidado(temp, 4);

								if (importacionFicherosEmitidosDao.existeFicheroCargado(fileEntry.getName(), 'G')) {
									
									// SE GUARDA EL NOMBRE DEL FICHERO PARA QUE LUEGO LO RECUPERE EL PL DE ENVIO CORREO
									dupLst += fileName.replace(".ZIP", "") + ", ";
									
								} else {
								
									sessionFactory.getCurrentSession().beginTransaction();
									
									Long id = importacionFicherosEmitidosDao.importarYValidarFicheroEmitidos(fase, usuario,
											'G', fileEntry.getName(), Boolean.FALSE);
	
									logger.debug("Fichero importado con identificador: " + id);
									
									sessionFactory.getCurrentSession().getTransaction().commit();
									
									sessionFactory.getCurrentSession().beginTransaction();
	
									logger.debug("Se procede a la validacion del fichero");
									validarFicheroEmitidos(id);
	
									logger.debug("Generando informes del plan " + fase.getFase().getPlan()
											+ " para el fichero " + fileName);
									importacionFicherosEmitidosDao.actualizarInformesFicheroEmitidos(id,
											fase.getFase().getPlan());
								}

							} catch (Exception e) {

								logger.error("Se ha producido un error al tratar el fichero " + fileName + " :"
										+ e.getMessage());
								errorLst += fileName.replace(".ZIP", "") + ", ";
							}
						}
					}
				}

				sessionFactory.getCurrentSession().beginTransaction();
				updateConfigAgpParam(bundle.getString("param.rec.ok"), String.valueOf(numFicheros));
				if (!StringUtils.isNullOrEmpty(errorLst)) {

					errorLst = errorLst.substring(0, errorLst.lastIndexOf(','));
					updateConfigAgpParam(bundle.getString("param.rec.errorMsg"), errorLst);
				}
				if (!StringUtils.isNullOrEmpty(dupLst)) {

					dupLst = dupLst.substring(0, dupLst.lastIndexOf(','));
					updateConfigAgpParam(bundle.getString("param.rec.dupMsg"), dupLst);
				}				
				sessionFactory.getCurrentSession().getTransaction().commit();
			}
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

	private static void cleanConfigAgpParams() {

		updateConfigAgpParam(bundle.getString("param.rec.ok"), "0");
		updateConfigAgpParam(bundle.getString("param.rec.errorMsg"), " ");
	}

	private static void updateConfigAgpParam(final String param, final String value) {

		SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("UPDATE O02AGPE0.TB_CONFIG_AGP SET AGP_VALOR = :valor WHERE AGP_NEMO = :nemo");
		query.setParameter("nemo", param);
		query.setParameter("valor", value);
		query.executeUpdate();
	}

	private static void validarFicheroEmitidos(final Long id) throws BusinessException {

		Map<String, Object> parametros = new HashMap<String, Object>();
		String procedure = "O02AGPE0.PQ_VALIDAR_RECIBOS_EMITIDOS.doValidarFicherosEmitidos(IDFICHERO IN NUMBER)";
		parametros.put("IDFICHERO", id);
		databaseManager.executeStoreProc(procedure, parametros);
	}

	private static void getConfiguracion() throws Throwable {
		try {

			// Configuracion hibernate
			Configuration cfg = new Configuration();
			// cargamos la sesion
			sessionFactory = cfg.configure().buildSessionFactory();
			faseDao = new FaseDao();
			faseDao.setSessionFactory(sessionFactory);
			databaseManager = new DatabaseManager();
			databaseManager.setSessionFactory(sessionFactory);
			importacionFicherosEmitidosDao = new ImportacionFicherosEmitidosDao();
			importacionFicherosEmitidosDao.setSessionFactory(sessionFactory);
			importacionFicherosEmitidosDao.setFaseDao(faseDao);
			importacionFicherosEmitidosDao.setDatabaseManager(databaseManager);
		} catch (Throwable e) {

			logger.error("Error al cargar la configuracion " + e);
			throw e;
		}
	}
}
