package com.rsi.agp.batch.comisiones.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.batch.bbdd.Conexion;
import com.rsi.agp.batch.comisiones.util.ConfigBuzonInfovia;
import com.rsi.agp.core.util.FTPDownloader;
import com.rsi.agp.batch.comisiones.util.ImportacionConnectionPool;

public class ImportacionFicherosComisionesBatch {
	
	private static final Logger logger = Logger.getLogger(ImportacionFicherosComisionesBatch.class);
	private static final int NUM_REINT = 2;
	private int intento = 0;
	ImportacionConnectionPool icp = null;

	private static final String LINESEPARATOR_STR = "line.separator";

	/**
	 * Realiza la importacion de ficheros desde el buzon de infovia. Clasifica los
	 * ficheros importados y realiza la importacion de los que corresponda.
	 * @throws ParseException 
	 * 
	 * @throws IllegalStateException
	 */
	public void doImportacion(final String fechaPlanif) throws ParseException {
		BasicConfigurator.configure();
		logger.info("**************INICIANDO PROCESO DESCARGA FICHEROS**************************");
		doWork(fechaPlanif);
		logger.info("FIN DESCARGA DE FICHEROS");
	}

	/**
	 * Metodo principal que inicia las tareas 1. Obtener listado de archivos del FTP
	 * 2. Crear directorio temporal para descargas 3. Descargar ficheros del FTP 4.
	 * Clasificar los ficheros en carpetas 5. Borrar la carpeta temporal
	 * @throws ParseException 
	 * 
	 */
	private void doWork(final String fechaPlanif) throws ParseException {
		SessionFactory factory;
		Session session = null;
		factory = getSessionFactory();
		session = factory.openSession();
		icp = new ImportacionConnectionPool();
		logger.info("Reintentos: " + NUM_REINT);
		String rutaDirTemp = ConfigBuzonInfovia.getProperty("directorioLocal")
				+ ConfigBuzonInfovia.getProperty("temporal");
		Date fecha = new Date();
        if (fechaPlanif != null && !"".equals(fechaPlanif)) {
            fecha = new SimpleDateFormat("yyyyMMdd").parse(fechaPlanif);
        }
		logger.info("rutaDirTemp: " + rutaDirTemp);
		String directorios = ConfigBuzonInfovia.getProperty(ConfigBuzonInfovia.DIR_REMOTO_GENERAL);
		String[] dirToLook = directorios.split("#");
		crearCarpetaTemporalParaDescarga(rutaDirTemp); // la creamos solo una vez
		List<String> lstFich = new ArrayList<String>();
		Map<String, String[]> mapEtiquetas = new HashMap<String, String[]>();

		try {
			mapEtiquetas = ImportacionFicherosComisionesBatch.getListEtiquetasBd();
		} catch (SQLException e) {
			logger.error("Error al obtener las etiquetas de cada fichero", e);
		}
		String passBuzonInfovia = getPasswordBuzonInfovia(session);
		if (null == passBuzonInfovia) {
			logger.error(" Salida batch.");
			System.exit(5);
		}
		for (int i = 0; i < dirToLook.length; i++) {
			intento = 0;
			logger.info("Obteniendo ficheros TXT de " + dirToLook[i] + " para dejarlos en " + rutaDirTemp);
			String urlFtp = ConfigBuzonInfovia.getProperty("urlFtpInicio") + passBuzonInfovia
					+ ConfigBuzonInfovia.getProperty("urlFtpFin");
			List<String> listFilenames = obtenerListado(urlFtp, dirToLook[i]);
			int ficherosDescargados = 0;
			if (listFilenames != null && !listFilenames.isEmpty()) {
				for (int j = 0; j < listFilenames.size(); j++) {
					String filename = listFilenames.get(j);
					if (!existeEnHistorico(filename)) { // VER SI EXISTE EN tb_buzon_agro_ficheros_movidos
						descarga(filename, rutaDirTemp, dirToLook[i], urlFtp);
						intento = 0;
						ficherosDescargados++;
					}
				}

				logger.info("Descarga de " + dirToLook[i] + " finalizada. Total Ficheros descargados: "
						+ ficherosDescargados);
				logger.info("Clasificando ficheros..");
				try {
					clasificarFicherosDescargados(rutaDirTemp, dirToLook[i], mapEtiquetas, urlFtp, lstFich, fecha);
				} catch (SQLException e) {
					logger.error("Error en la clasificacion de ficheros..", e);
				}

				borrarFicherosTemporales(rutaDirTemp);
			} else {// sin ficheros descargados
				logger.info(" Sin ficheros descargados");
			}
		}
		logger.info(" TOTAL FICHEROS DESCARGADOS DE AMBOS BUZONES: " + lstFich.size());
		generateAndSendMail(lstFich, fecha, session);
	}

	/**
	 * Metodo que crea un directorio para realizar la descarga de ficheros
	 * 
	 * @param rutaDirTemp
	 *            ruta temporal para la descarga
	 */
	private void crearCarpetaTemporalParaDescarga(String rutaDirTemp) {
		File file = new File(rutaDirTemp);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * Descarga txt
	 * 
	 * @param filename
	 * @param rutaDirTemp
	 * 
	 *            AgpFtpClient.download: - Ruta del fichero del FTP que queremos
	 *            descargar - Ruta en la que queremos descargar el fichero - Si
	 *            queremos utilizar el proxy - Tipo de fichero (TXT: ASCII, ZIP:
	 *            Binary)
	 * 
	 */
	private boolean descarga(String filename, String rutaDirTemp, String rutaOrigen, String urlFtp) {
		try {			
			FTPDownloader ftpDownloader = new FTPDownloader();
			ftpDownloader.download(urlFtp, rutaOrigen.concat(filename), rutaDirTemp + filename);
			logger.info(filename + " descargado");
			return true;
		} catch (Exception e) {
			while (intento < NUM_REINT) {
				logger.error("Error al descargar el fichero: " + rutaOrigen.concat(filename)
						+ ". Reintentando la descarga por " + intento + " vez");
				intento++;
				if (descarga(filename, rutaDirTemp, rutaOrigen, urlFtp)) {
					intento = NUM_REINT;
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Comprueba si el fichero existe en la tabla TB_BUZON_AGRO_FICHEROS_MOVIDOS
	 * 
	 * @param filename
	 * @return
	 */
	private boolean existeEnHistorico(String filename) {
		int longitudEtiqueta = filename.length() - 4;
		String etiquetaFilename = filename.substring(0, longitudEtiqueta);
		boolean existe = icp.existeEnMovidos(etiquetaFilename);
		logger.info("existeEnHistorico " + filename + " " + existe);
		return existe;
	}

	/**
	 * Clasificamos los ficheros dependiendo del tipo
	 * 
	 * @param dirTemp
	 * @throws IllegalStateException
	 */
	private void clasificarFicherosDescargados(final String dirTemp, final String dirToLook,
			final Map<String, String[]> mapEtiquetas, final String urlFtp, final List<String> lstFich, final Date fecha)
			throws SQLException {
		File file = new File(dirTemp);
		File[] ficherosDir = file.listFiles();
		int temp = 0;
		try {
			BufferedReader bd = null;
			List<String> listEtiquetas = new ArrayList<String>(mapEtiquetas.keySet());
			String[] etiquetasArr = new String[listEtiquetas.size()];
			etiquetasArr = listEtiquetas.toArray(etiquetasArr);
			// Leemos los ficheros del directorio temporal
			for (Integer j = 0; j < ficherosDir.length; j++) {
				String fileetiqueta = "";
				temp = temp + 1;
				File ficheroTemp = ficherosDir[j];
				logger.info(
						"Clasificando fichero " + temp + " / " + ficherosDir.length + " : " + ficheroTemp.getName());
				bd = new BufferedReader(new FileReader(ficheroTemp));
				String primeraLinea = bd.readLine();
				bd.close();
				logger.info(primeraLinea);
				int posicionEtiqueta = StringUtils.indexOfAny(primeraLinea, etiquetasArr);
				if (primeraLinea != null && posicionEtiqueta != -1) { // fIchero con etiqueta en BBDD
					// buscamos la etiqueta
					for (String etiq : listEtiquetas) {
						if (primeraLinea.indexOf(etiq) != -1) {
							fileetiqueta = etiq;
							break;
						}
					}
					// Comprobamos si es de alguno de los tipos que tratamos y, en tal caso, lo
					// movemos a la carpeta pertinente.
					String etiquetaFilename = ficheroTemp.getName().substring(0, ficheroTemp.getName().length() - 4);
					logger.info("   " + etiquetaFilename + ": " + primeraLinea);

					String[] strIdRuta = mapEtiquetas.get(fileetiqueta);
					if (strIdRuta[1] != null) {
						intento = 0;
						moverFicheros(ficheroTemp, strIdRuta[1], strIdRuta[0], dirToLook, primeraLinea, urlFtp, fecha);
					}
					lstFich.add("   " + etiquetaFilename + ": " + primeraLinea);
				} else {
					logger.info(" Etiqueta no encontrada en BBDD");
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Error durante la clasificacion de ficheros descargados", e);
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error durante la clasificacion de ficheros descargados", e);
			System.exit(4);
		}
	}

	/**
	 * Movemos los txt y los zip
	 * 
	 * @param fichero
	 * @param ruta
	 * @param idTipoFichero
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	private void moverFicheros(final File fichero, final String ruta, final String idTipoFichero, final String dirToLook, final String primeraLinea,
			final String urlFtp, final Date fecha) throws IOException {
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd/");
		final String auxRuta = String.valueOf(File.separator) + s.format(fecha);
		logger.info("Ruta destino: " + auxRuta);
		File file = new File(ruta.concat(auxRuta));
		if (!file.exists()) {
            file.mkdirs();
        }
		try (InputStream in = new FileInputStream(fichero);
				FileOutputStream fos = new FileOutputStream(
						ruta.concat(File.separator + s.format(fecha)).concat(fichero.getName()));
				OutputStream out = new BufferedOutputStream(fos)) {
			logger.info("Moviendo " + fichero.getName() + " a " + ruta.concat(File.separator + s.format(fecha)));
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			// Descargamos el ZIP
			String zipName = obtenerZipName(fichero.getName());
			logger.info("Descargando " + zipName);
			boolean descargaCorrecta = descarga(zipName, ruta.concat(File.separator + s.format(fecha)),
					 dirToLook, urlFtp);
			// boolean descargaCorrecta = true; // para pruebas
			if (descargaCorrecta) {
				logger.info("Insertando en historico " + fichero.getName());
				insertarEnHistorico(fichero.getName(), "true", idTipoFichero, true, primeraLinea);
			} else {
				logger.info("Insertando Descarga ZIP Erronea en historico " + fichero.getName());
				insertarEnHistorico(fichero.getName(), "false", idTipoFichero, false, "");
			}
		} catch (IOException e1) {
			insertarEnHistorico(fichero.getName(), "false", idTipoFichero, false, "");
			logger.info(fichero.getName() + " NO descargado");
		}
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	private String obtenerZipName(String filename) {
		return filename.replace(".txt", ".ZIP").replace(".TXT", ".ZIP");
	}

	private List<String> obtenerListado(String urlFTP, String dirRemoto) {
		List<String> b = new ArrayList<String>();
		List<String> aux = new ArrayList<String>();
		try {

			FTPDownloader ftpDownloader = new FTPDownloader();
			b = ftpDownloader.getListFiles(urlFTP, dirRemoto);

			// tenemos los ficheros pero duplicados, ya que hay nombreFichero.txt y
			// nombreFichero.zip
		} catch (Exception e) {
			while (intento < NUM_REINT) {
				logger.error("Error al recuperar el listado del directorio en '" + urlFTP
						+ "' con el directorio remoto " + dirRemoto + "Reintentando por " + intento + " vez", e);
				intento++;
				if (intento >= NUM_REINT) {
					logger.error(" Salida batch, reintentos : " + intento);
					System.exit(5);
				}
				b = obtenerListado(urlFTP, dirRemoto);
				if (!b.isEmpty()) {
					intento = NUM_REINT;
				}
			}
		}
		for (int i = 0; i < b.size(); i++) {
			if (b.get(i) != null && (b.get(i).indexOf(".txt") != -1 || b.get(i).indexOf(".TXT") != -1)) {
				aux.add(b.get(i));
			}
		}
		logger.info(" dirRemoto: " + dirRemoto + " Total ficheros: " + aux.size());
		return aux;
	}

	/**
	 * Insertamos en historico
	 * 
	 * @param filename
	 * @param correcto
	 * @param idTipoFichero
	 */
	private void insertarEnHistorico(String filename, String correcto, String idTipoFichero, boolean movido,
			String primeraLinea) {
		String etiquetaFilename = filename.substring(0, filename.length() - 4);
		if (existe(etiquetaFilename)) {
			updateEnHistorico(etiquetaFilename, correcto);
		} else {
			String qr = "INSERT INTO o02agpe0.TB_COMS_HIST_FICHS_IMPORTS VALUES('" + etiquetaFilename + "', SYSDATE,"
					+ "'" + correcto + "'" + ")";
			try {
				Conexion c = new Conexion();
				logger.debug(qr);
				c.ejecutaUpdate(qr);
				logger.info("Historico actualizado.");
			} catch (Exception e) {
				logger.error("## ERROR en INSERT TB_COMS_HIST_FICHS_IMPORTS VALUES  ", e);
			}
		}
		if (movido) {
			if (existeEnMovidosBuzon(etiquetaFilename)) {
				updateEnMovidosBuzon(etiquetaFilename, idTipoFichero, primeraLinea);
			} else {
				String sqlQuery = "INSERT INTO o02agpe0.tb_buzon_agro_ficheros_movidos VALUES(" + getMaxId() + ","
						+ idTipoFichero + ", '" + etiquetaFilename + "', SYSDATE,'" + primeraLinea + "')";

				try {
					Conexion c = new Conexion();
					logger.debug(sqlQuery);
					c.ejecutaUpdate(sqlQuery);
					logger.info("tabla ficheros_movidos actualizado.");
				} catch (Exception e) {
					logger.error("## ERROR en INSERT tb_buzon_agro_ficheros_movidos VALUES  ", e);
				}
			}
		}
	}

	private boolean existe(String filename) {
		return icp.existeEnHistorico(filename);
	}

	private boolean existeEnMovidosBuzon(String filename) {
		return icp.existeEnMovidos(filename);
	}

	private long getMaxId() {
		long idMaximo = 0;
		String query = "SELECT MAX(ID) FROM o02agpe0.tb_buzon_agro_ficheros_movidos";
		List<Object> lista;
		try {
			Conexion c = new Conexion();
			lista = c.ejecutaQuery(query, 1);
			if (lista != null && !lista.isEmpty()) {
				Object[] registro = (Object[]) lista.get(0);
				BigDecimal idMaximB = (BigDecimal) registro[0];
				if (idMaximB != null)
					idMaximo = idMaximB.longValue();
			}
		} catch (Exception e) {
			logger.error("## ERROR en getMaxId", e);
		}
		idMaximo++;
		return idMaximo;
	}

	private void updateEnMovidosBuzon(String filename, String idTipoFichero, String primeraLinea) {
		String query = "update o02agpe0.tb_buzon_agro_ficheros_movidos set idtipofichero=" + idTipoFichero
				+ ", descripcion = '" + primeraLinea + "',fecha=SYSDATE where fichero='" + filename + "'";
		try {
			Conexion c = new Conexion();
			logger.debug(query);
			c.ejecutaUpdate(query);
		} catch (Exception e) {
			logger.error("## ERROR en updateEnMovidosBuzon  ", e);
		}
	}

	private void updateEnHistorico(String filename, String correcto) {
		String qr = "update o02agpe0.tb_coms_hist_fichs_imports set correcto='" + correcto
				+ "' , fecimportacion=SYSDATE where nombrefichero='" + filename + "'";
		try {
			Conexion c = new Conexion();
			logger.debug(qr);
			c.ejecutaUpdate(qr);
		} catch (Exception e) {
			logger.error("## ERROR en UPDATE  ", e);
		}
	}

	/**
	 * metodo que saca el listado de todas las etiquetas en un mapa con clave la
	 * etiqueta y valores el id y su ruta
	 */
	private static Map<String, String[]> getListEtiquetasBd() throws SQLException {
		Map<String, String[]> mapaEtiquetas = new HashMap<String, String[]>();
		Conexion c = new Conexion();

		String sqlQuery = "select etiqueta,id, directorio from o02agpe0.tb_buzon_agro_tipos_ficheros";
		logger.info(" ## sql: " + sqlQuery);
		List<Object> lista;
		try {
			lista = c.ejecutaQuery(sqlQuery, 3);
			if (lista != null && !lista.isEmpty()) {
				for (int i = 0; i < lista.size(); i++) {
					Object[] registro = (Object[]) lista.get(i);
					String etiqueta = (String) registro[0];
					BigDecimal id = (BigDecimal) registro[1];
					String ruta = (String) registro[2];
					String[] strArr = new String[2];
					strArr[0] = id.toString();
					strArr[1] = ruta;
					mapaEtiquetas.put(etiqueta, strArr);
					logger.info(" obj: " + etiqueta + " " + id + " " + ruta);
				}
			}
		} catch (Exception e) {
			logger.error("## ERROR en getListEtiquetasBd  ", e);
		}
		return mapaEtiquetas;
	}

	/**
	 * Metodo encargado de borrar los ficheros temporales
	 * 
	 * @param directorio
	 */
	private void borrarFicherosTemporales(String directorio) {
		logger.info("Borrando ficheros innecesarios del temporal: " + directorio);
		File dir = new File(directorio);
		try {
			FileUtils.cleanDirectory(dir);
			File[] archivos = dir.listFiles();
			if (archivos.length > 0) {
				logger.info("Borrado de los " + archivos.length + " ficheros del directorio: " + directorio);
				for (int i = 0; i < archivos.length; i++) {
					boolean result = archivos[i].delete();
					logger.debug("Resultado borrado: " + result);
				}
			}
			if (archivos.length > 0) {
				logger.info("NO SE HA PODIDO BORRAR EL CONTENIDO DEL DIRECTORIO TEMPORAL");
				System.exit(6);
			}
			logger.info("Fin Borrado ficheros innecesarios del temporal: " + directorio);
		} catch (IOException e) {
			logger.info("Error en el borrado del directorio: " + directorio, e);
		}
	}

	/**
	 * Metodo que realiza el Envio correo
	 * 
	 * @param lstFich
	 *            lista de ficheros procesadas
	 */
	private static void generateAndSendMail(final List<String> lstFich, final Date fecha, final Session session) {
		String grupo;
		String asunto;
		StringBuilder msg;
		grupo = ConfigBuzonInfovia.getProperty("mail.grupo");
		asunto = ConfigBuzonInfovia.getProperty("mail.asunto") + " "
				+ new SimpleDateFormat("dd/MM/yyyy").format(fecha);
		msg = new StringBuilder();
		msg.append(ConfigBuzonInfovia.getProperty("mail.totalFicheros") + " " + lstFich.size());
		msg.append(System.getProperty(LINESEPARATOR_STR));
		msg.append(System.getProperty(LINESEPARATOR_STR));
		if (!lstFich.isEmpty()) {
			for (String fich : lstFich) {
				msg.append(fich);
				msg.append(System.getProperty(LINESEPARATOR_STR));
			}
		}
		msg.append(System.getProperty(LINESEPARATOR_STR));
		msg.append(System.getProperty(LINESEPARATOR_STR));
		msg.append(ConfigBuzonInfovia.getProperty("mail.footer"));
		msg.append(System.getProperty(LINESEPARATOR_STR));
		logger.debug("Mensaje a mandar: " + asunto);
		logger.debug(msg.toString());
		Query query = session.createSQLQuery("CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreo(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", grupo).setParameter("asunto", asunto).setParameter("mensaje", msg.toString());

		query.executeUpdate();
	}

	private static SessionFactory getSessionFactory() {
		SessionFactory factory;
		try {
			Configuration cfg = new Configuration();
			cfg.configure();

			factory = cfg.buildSessionFactory();

		} catch (Exception ex) {

			logger.error("# Error al crear el objeto SessionFactory.", ex);
			throw new ExceptionInInitializerError(ex);
		}
		return factory;
	}

	// Metodo que devuelve el password del buzon Infovia
	public static String getPasswordBuzonInfovia(final Session session) {
		String pass = null;
		try {
			String query = "select pass_buzon_infovia from o02agpe0.tb_parametros";
			pass = (String) session.createSQLQuery(query).uniqueResult();
			return pass;
		} catch (Exception ex) {
			logger.error(" Error al recoger el password del buzon Infovia en BBDD : ", ex);
			logger.error(" Salida batch.");
			System.exit(5);
			return null;
		}
	}
}