package com.rsi.agp.batch.cargaCondicionados;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.batch.importacionCondicionado.util.ConfigImportacionCondicionado;
import com.rsi.agp.core.managers.impl.PantallasConfigurablesManager;
import com.rsi.agp.core.plsql.PlsqlServiceImpl;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.commons.CommonDao;
import com.rsi.agp.dao.models.config.PantallasConfigurablesDao;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.log.HistImportacionesDao;
import com.rsi.agp.dao.models.plsql.PlsqlDao;
import com.rsi.agp.dao.tables.log.HistImportaciones;

public class CargaCondicionados {

	private static final Logger logger = Logger.getLogger(CargaCondicionados.class);

	static ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_ficheros");

	private static org.hibernate.SessionFactory sessionFactory;

	private static PlsqlServiceImpl plsqlExecutor;
	private static CommonDao commonDao;

	public static void main(String[] args) {

		try {

			BasicConfigurator.configure();
			logger.info("INICIO batch Carga Condicionados");
			String fechaPlanif = "";
			if (args.length!=0)
				fechaPlanif = args[0];
			doWork(fechaPlanif);
			logger.info("FIN batch Carga Condicionados");
			System.exit(0);
		} catch (Exception e) {

			logger.error("Error en el proceso batch de Carga Condicionados", e);
			System.exit(1);
		} catch (Throwable e) {

			logger.error("Error en el proceso batch de Carga Condicionados", e);
			System.exit(1);
		}
	}

	private static void doWork(String fechaPlanif) throws Throwable {

		try {

			logger.info("Cargamos la configuracion necesaria.");
			getConfiguracion();

			sessionFactory.getCurrentSession().beginTransaction();

			cleanConfigAgpParams();

			sessionFactory.getCurrentSession().getTransaction().commit();

			logger.info("Fecha recibida como parametro: " + fechaPlanif);
			
			String ruta = bundle.getString("ruta.condicionados");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			if ("".equals(fechaPlanif)) 
				ruta += sdf.format(new Date());
			else
				ruta += sdf.format(new SimpleDateFormat("yyyyMMdd").parse(fechaPlanif));
			
			logger.info("Obtenemos los ficheros de: " + ruta);

			HashMap<String, List<CargaCondBean>> condFiles = new HashMap<String, List<CargaCondBean>>();

			File folder = new File(ruta);
			if (folder.exists() && folder.canRead()) {

				for (final File fileEntry : folder.listFiles()) {

					if (!fileEntry.isDirectory()) {

						String fileName = fileEntry.getName().toUpperCase();

						if (fileName.endsWith(".TXT")) {
							try (FileReader fr = new FileReader(fileEntry);
									BufferedReader bf = new BufferedReader(fr)) {

								String fileContent = bf.readLine();
								String fileType = fileContent.substring(0, 11).trim();
								if (condFiles.get(fileType) == null) {

									condFiles.put(fileType, new ArrayList<CargaCondBean>());
								}
								String plan = null;
								String linea = null;
								String tablas = null;
								int planIndx = fileContent.toUpperCase().indexOf("PLAN");
								if (planIndx != -1) {
									plan = fileContent.substring(planIndx + 4, planIndx + 10).replace(".", "").trim();
								}
								int linIndx = fileContent.toUpperCase().indexOf("LINEA");
								if (linIndx != -1) {
									linea = fileContent.substring(linIndx + 5, linIndx + 9).trim();
								}
								int tabIndx = fileContent.toUpperCase().indexOf("REGISTROS TIPO (");
								if (tabIndx != -1) {
									tablas = fileContent.substring(tabIndx + 16, fileContent.lastIndexOf(")")).trim();
								}
								condFiles.get(fileType)
										.add(new CargaCondBean(fileName.replace(".TXT", ""), plan, linea, tablas));
							}
						}
					}
				}

				int numFicheros = 0;
				StringBuilder errorLst = new StringBuilder();

				if (!condFiles.isEmpty()) {

					String[] fileTypesArr = bundle.getString("cond.filetype.order").split(",");
					for (String fileType : fileTypesArr) {

						if (condFiles.get(fileType) != null) {

							List<CargaCondBean> fileLst = condFiles.get(fileType);
							for (CargaCondBean condBean : fileLst) {

								try {

									int tipoImportacion = Integer
											.parseInt(bundle.getString(fileType + ".tipo.importacion"));
									
									TimeUnit.SECONDS.sleep(Integer
											.parseInt(bundle.getString("conds.proceso.retardo")));
									
									logger.debug("Procesando: " + condBean.getFileName());
									numFicheros++;

									File zipFile = new File(ruta + File.separator + condBean.getFileName() + ".ZIP");
									if (zipFile.exists() && zipFile.canRead()) {

										copiarFicheroCondicionado(condBean.getFileName(), ruta);

										String tablas = "";
										String ficheros = "";

										String[] tablasArr = condBean.getTablas().split(", ");
										// No se procesan las tablas Primas Riesgo (45) ni Tarifas (17)
										for (String numTabla : tablasArr) {
											if (!"45".equals(numTabla) && !"17".equals(numTabla)) {
												tablas += numTabla + ConfigImportacionCondicionado
														.getProperty(ConfigImportacionCondicionado.SEPARADOR);
												ficheros += condBean.getFileName() + ConfigImportacionCondicionado
														.getProperty(ConfigImportacionCondicionado.SEPARADOR);
												// Si es la tabla 8: Caracteristicas del grupo de tasas, anhadimos tambien
												// la combinacion 8R
												if ("8".equals(numTabla) || "406".equals(numTabla)) {
													tablas += numTabla + "R" + ConfigImportacionCondicionado
															.getProperty(ConfigImportacionCondicionado.SEPARADOR);
													ficheros += condBean.getFileName() + ConfigImportacionCondicionado
															.getProperty(ConfigImportacionCondicionado.SEPARADOR);
												}
											}
										}

										plsqlExecutor.setTablas(tablas);
										plsqlExecutor.setClasspath(ConfigImportacionCondicionado
												.getProperty(ConfigImportacionCondicionado.CLASSPATH));
										plsqlExecutor.setFichTablas(ficheros);
										plsqlExecutor.setLinea(StringUtils.nullToString(condBean.getLinea()));
										plsqlExecutor.setPlan(StringUtils.nullToString(condBean.getPlan()));
										plsqlExecutor.setTipoImportacion(tipoImportacion);
										plsqlExecutor.doTask();

										sessionFactory.getCurrentSession().beginTransaction();
										
										Criteria crit = sessionFactory.getCurrentSession()
												.createCriteria(HistImportaciones.class);
										crit.createAlias("importacionTablas", "importacionTablas");
										crit.add(Restrictions.eq("importacionTablas.ficherozip",
												condBean.getFileName()));
										// POR SI SE HA CARGADO UN FICHERO MAS DE UNA VEZ ORDENAMOS POR LA FECHA DE
										// EJECUCION DE LA CARGA DESCENDENTE Y COGEMOS EL PRIMERO
										crit.addOrder(Order.desc("fechaimport"));
										crit.setProjection(Projections.property("estado"));
										List<?> auxLst = crit.list();
										String resultadoCarga = auxLst == null || auxLst.isEmpty() ? null
												: (String) crit.list().get(0);

										if (StringUtils.isNullOrEmpty(resultadoCarga)) {

											logger.error("No se encuentra el resultado de la carga del fichero "
													+ condBean.getFileName());
											errorLst.append(condBean.getFileName());
											errorLst.append(", ");
										} else {
											if ("ERROR".equalsIgnoreCase(resultadoCarga)) {

												logger.error("El proceso de importacion del fichero "
														+ condBean.getFileName() + " ha finalizado con error");
												errorLst.append(condBean.getFileName());
												errorLst.append(", ");
											}
										}
										
										sessionFactory.getCurrentSession().getTransaction().commit();
									} else {

										logger.error(
												"No se encuentra el fichero ZIP asociado " + condBean.getFileName());
										errorLst.append(condBean.getFileName());
										errorLst.append(", ");
									}
								} catch (Exception e) {

									logger.error("Se ha producido un error al tratar el fichero "
											+ condBean.getFileName() + " :" + e.getMessage());
									errorLst.append(condBean.getFileName());
									errorLst.append(", ");
								}
							}
						}
					}
				}

				sessionFactory.getCurrentSession().beginTransaction();

				updateConfigAgpParam(bundle.getString("param.cond.ok"), String.valueOf(numFicheros));
				String errorLstStr = errorLst.toString();
				if (!StringUtils.isNullOrEmpty(errorLstStr)) {

					errorLstStr = errorLstStr.substring(0, errorLstStr.lastIndexOf(','));
					updateConfigAgpParam(bundle.getString("param.cond.errorMsg"), errorLstStr);
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

		updateConfigAgpParam(bundle.getString("param.cond.ok"), "0");
		updateConfigAgpParam(bundle.getString("param.cond.errorMsg"), " ");
	}

	private static void updateConfigAgpParam(final String param, final String value) {

		SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("UPDATE O02AGPE0.TB_CONFIG_AGP SET AGP_VALOR = :valor WHERE AGP_NEMO = :nemo");
		query.setParameter("nemo", param);
		query.setParameter("valor", value);
		query.executeUpdate();
	}

	private static void copiarFicheroCondicionado(final String fichero, final String rutaOrigen)
			throws IOException {		
			// Copiamos el fichero de donde esta a "ruta.proceso.condicionados"
			File ficheroTxtOrigen = new File(rutaOrigen + File.separator + fichero + ".TXT");
			File ficheroZipOrigen = new File(rutaOrigen + File.separator + fichero + ".ZIP");
			String rutaDestino = bundle.getString("ruta.proceso.condicionados");
			File ficheroTxtDestino = new File(rutaDestino + File.separator + fichero + ".TXT");
			File ficheroZipDestino = new File(rutaDestino + File.separator + fichero + ".ZIP");
			writeFile(ficheroTxtOrigen, ficheroTxtDestino);
			writeFile(ficheroZipOrigen, ficheroZipDestino);
	}
	
	private static void writeFile(final File origen, final File destino) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		try (InputStream inTxt = new FileInputStream(origen); OutputStream outTxt = new FileOutputStream(destino)) {
			while ((len = inTxt.read(buf)) > 0) {
				outTxt.write(buf, 0, len);
			}
		}
	}

	private static void getConfiguracion() throws Exception {
		try {
			// Configuracion hibernate
			Configuration cfg = new Configuration();
			// cargamos la sesion
			sessionFactory = cfg.configure().buildSessionFactory();
			DatabaseManager databaseManager = new DatabaseManager();
			databaseManager.setSessionFactory(sessionFactory);
			PlsqlDao plsqlDao = new PlsqlDao();
			plsqlDao.setDatabaseManager(databaseManager);
			HistImportacionesDao histImportacionesDao = new HistImportacionesDao();
			histImportacionesDao.setSessionFactory(sessionFactory);
			commonDao = new CommonDao();
			commonDao.setSessionFactory(sessionFactory);
			PantallasConfigurablesDao pantallasConfigurablesDao = new PantallasConfigurablesDao();
			pantallasConfigurablesDao.setSessionFactory(sessionFactory);
			PantallasConfigurablesManager pantallasConfigurablesManager = new PantallasConfigurablesManager();
			pantallasConfigurablesManager.setPantallasConfigurablesDao(pantallasConfigurablesDao);
			pantallasConfigurablesManager.setCommonDao(commonDao);
			plsqlExecutor = new PlsqlServiceImpl();
			plsqlExecutor.setPlsqlDao(plsqlDao);
			plsqlExecutor.setHistImportacionesDao(histImportacionesDao);
			plsqlExecutor.setCommonDao(commonDao);
			plsqlExecutor.setPantallasConfigurablesManager(pantallasConfigurablesManager);
		} catch (Exception e) {
			logger.error("Error al cargar la configuracion " + e);
			throw e;
		}
	}

	private static class CargaCondBean {

		private String fileName;
		private String plan;
		private String linea;
		private String tablas;

		CargaCondBean(final String fileName, final String plan, final String linea, final String tablas) {

			this.fileName = fileName;
			this.plan = plan;
			this.linea = linea;
			this.tablas = tablas;
		}

		protected String getFileName() {

			return this.fileName;
		}

		protected String getPlan() {

			return this.plan;
		}

		protected String getLinea() {

			return this.linea;
		}

		protected String getTablas() {

			return this.tablas;
		}
	}
}