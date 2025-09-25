package com.rsi.agp.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.batch.comunicacionesExternas.ErrorComunicacionExterna;

/**
 * Clase para el envo de plizas de corredurias externas a Agroseguro. Pasos a
 * seguir:
 * 
 * 1. Leemos de la carpeta a la que acceden las corredurias los ficheros
 * depositados. 2. Por cada fichero ejecutamos los siguientes pasos: -
 * Comprobamos si es un acuse o si ya lo hemos procesado previamente (tabla
 * TB_COMUNICACIONES_EXTERNAS) - Si ya lo hemos procesado o es un acuse, pasamos
 * al siguiente. - Si NO lo hemos procesado, lo enviamos a Agroseguro e
 * insertamos el registro correspondiente.
 * 
 * @author U028783
 *
 */
public class EnvioPolizasExternas {

	private static final int DEFAULT_HTTPS_PORT = 443;
	private static final int DEFAULT_PROXY_PORT = 8080;
	private static final String CONT_BATCH_ENVIO_POL_EXTERNAS = "CONT_BATCH_ENVIO_POL_EXTERNAS";

	private String ruta;
	private String rutaTmpValidacion;
	private Long contadorPolizas;
	ValidaPolizasExternas validacion = null;

	// Atributos para el envio a Agroseguro
	private String address;
	private String verbose;
	private String userAgro;
	private String passwordAgro;
	private static SessionFactory factory = getSessionFactory();

	private static final Logger logger = Logger.getLogger(EnvioPolizasExternas.class);

	public EnvioPolizasExternas() {
		// Inicializamos los atributos necesarios para las conexiones con BBDD y
		// Agroseguro
		ResourceBundle bundle = ResourceBundle.getBundle("agp_plz_externas");
		ruta = bundle.getString("ruta.origen.envio");
		rutaTmpValidacion = bundle.getString("ruta.origen.validacion.tmp");

		address = bundle.getString("address");
		verbose = bundle.getString("verbose");
		userAgro = bundle.getString("userAgro");
		passwordAgro = bundle.getString("passwordAgro");
	}

	public static void main(String[] args) {

		EnvioPolizasExternas envio = new EnvioPolizasExternas();
		try {
			logger.info("Inicio de proceso Batch de envio de polizas externas a Agroseguro");
			envio.procesarEnvio();
			logger.info("Proceso de envio de polizas de corredurias externas finalizado correctamente");
			System.exit(0);

		} catch (Exception e) {
			logger.error("Error durante el envio de polizas de corredurias externas ", e);
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Metodo para enviar los ficheros depositados por las corredurias externas a
	 * Agroseguro
	 * 
	 * @throws Exception
	 */
	private void procesarEnvio(com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna correduria)
			throws Exception {

		// 1. Leer los ficheros TXT de la carpeta de origen:
		String pathFichero = ruta + File.separator + correduria.getRutaFtp();
		File dir = new File(pathFichero);
		String[] ficherosTxt = dir.list(new TXTFileFilter());

		logger.info("Número de ficheros encontrados = " + ((ficherosTxt != null) ? ficherosTxt.length : "0"));

		logger.info("Inicio del proceso de envio de ficheros.");
		for (int z = 0; ficherosTxt != null && z < ficherosTxt.length; z++) {
			File fich = new File(pathFichero + File.separator + ficherosTxt[z]);
			String nombreFichero = fich.getName().substring(0, fich.getName().lastIndexOf("."));
			logger.info("Procesando el fichero " + nombreFichero);
			String resultadoEnvio = "";
			// 2. Por cada fichero...
			// Si el nombre del fichero existe en TB_COMUNICACIONES_EXTERNAS en el campo
			// FICHERO_RECIBO_F
			// o en el campo FICHERO_ENVIO => no lo procesamos. En caso contrario,
			// intentamos enviar a Agroseguro
			// el fichero ZIP y el TXT.
			if (!this.ficheroProcesado(nombreFichero)) {
				logger.info("Fichero no procesado. Leemos informacion del .txt");
				InformacionFileTxt infTxt = this.cargaFicheroTxt(fich.getPath(), nombreFichero);
				logger.info("Polizas del plan " + infTxt.getPlan().toString());
				if (infTxt.getPlan() >= Constants.PLAN_2015.longValue()) {
					logger.info("Validacion de ficheros XML");

					boolean isvalid = validacion.isValidProceso(pathFichero, rutaTmpValidacion, correduria, infTxt);
					if (isvalid) {// sI ES VALIDO SE ENVIA A aGROSEGURO
						logger.info("Fichero OK");
						resultadoEnvio = envioAgroseguro(nombreFichero, pathFichero);
					} else {
						logger.info("Fichero KO");
						resultadoEnvio = "KO";
					}
					List<ErrorComunicacionExterna> resultadoValidacion = validacion.getResultadoValidaciones();
					// Registrar resultado en TB_CONFIG_AGP --- OJO solo hay un registro WARNING
					saveOrUpdate(resultadoValidacion);

				} else {
					resultadoEnvio = envioAgroseguro(nombreFichero, pathFichero);
				}
				logger.info(resultadoEnvio);
				// insertar el registro correspondiente en la tabla TB_COMUNICACIONES_EXTERNAS
				this.insertarRegistroFichero(nombreFichero, resultadoEnvio, correduria.getId());
			}
		}
	}

	private void saveOrUpdate(List<ErrorComunicacionExterna> lista) throws HibernateException {

		Session session = null;
		Transaction trans = null;
		try {
			session = factory.openSession();
			trans = session.beginTransaction();
			for (ErrorComunicacionExterna errorComunicacionExterna : lista) {
				session.persist(errorComunicacionExterna);
			}
			trans.commit();
		} catch (Exception ex) {
			if (null != trans) {
				trans.rollback();
			}
			throw new HibernateException("Se ha producido un error durante el guardado de una de las entidades", ex);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private String envioAgroseguro(String nombreFichero, String pathFichero) {
		// Realizar el envio a Agroseguro
		logger.info("Enviando el fichero....");
		String resultadoEnvio = "";
		try {
			logger.info("Llamada al servicio");
			FileUploader.doWork(new URL(address), nombreFichero, pathFichero, userAgro, passwordAgro,
					EnvioPolizasExternas.DEFAULT_HTTPS_PORT, "", EnvioPolizasExternas.DEFAULT_PROXY_PORT, "", "", "",
					"", verbose);
			resultadoEnvio = "OK";
			logger.info("Fichero enviado!");
			logger.info("Fichero enviado!");
		} catch (MalformedURLException e) {
			logger.error("Error al procesar el fichero. ", e);
			resultadoEnvio = "KO";

		} catch (Exception e) {
			logger.error("Error indefinido al procesar el fichero. ", e);
			resultadoEnvio = "KO";
		}
		return resultadoEnvio;
	}

	private void procesarEnvio() throws Exception {
		try {
			validacion = new ValidaPolizasExternas();
			contadorPolizas = Long.valueOf(0);
			// Borrar tabla de errores TB_ERRORES_ENVIO_PLZ_EXTERNAS
			borraTablaErroresEnvio();
			iniciaContadorPolizas(contadorPolizas.toString());
			// Seleccionamos las corredurias externas
			List<com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna> corredurias = getCorreduriasExternas();
			if (null != corredurias && corredurias.size() > 0) {
				logger.info("Proceso de envio de " + corredurias.size() + " corredurias externas.");
				for (com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna correduria : corredurias) {
					logger.info("Proceso para la aseguradora " + correduria.getNombre() + ". Directorio: "
							+ correduria.getRutaFtp());
					procesarEnvio(correduria);
				}
			} else {
				logger.info("No existen registros de corredurias externas. Proceso terminado");
			}
		} catch (Exception e) {
			logger.error("Error durante el proceso de envio de pÃ³lizas de corredurias externas. ", e);
			throw (e);
		} finally {
			contadorPolizas = validacion.getContadorPolizas();
			iniciaContadorPolizas(contadorPolizas.toString());
			validacion.setContadorPolizas(Long.valueOf(0));
		}
	}

	private void borraTablaErroresEnvio() throws Exception {
		Session session = null;
		try {
			session = factory.openSession();
			session.createSQLQuery("DELETE FROM o02agpe0.TB_ERRORES_ENVIO_PLZ_EXTERNAS").executeUpdate();

		} catch (Exception e) {
			logger.error("Error durante el borrado de la tabla de errores. ", e);
			throw (e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private void iniciaContadorPolizas(String valor) throws Exception {
		Session session = null;
		try {

			session = factory.openSession();
			session.createSQLQuery("UPDATE O02AGPE0.TB_CONFIG_AGP set AGP_VALOR='" + valor + "' WHERE  AGP_NEMO='"
					+ CONT_BATCH_ENVIO_POL_EXTERNAS + "'").executeUpdate();
		} catch (Exception e) {
			logger.error("Error durante el borrado de la tabla de errores. ", e);
			throw (e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna> getCorreduriasExternas()
			throws Exception {
		List<com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna> corredurias = new ArrayList<com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna>();
		Session session = null;
		try {
			session = factory.openSession();
			Criteria crit = session
					.createCriteria(com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna.class);
			corredurias = (List<com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna>) crit.list();
			logger.info("Seleccionamos las corredurias externas. ");
		} catch (Exception e) {
			logger.error("Error al seleccionar las corredurias externas ", e);
			throw (e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return corredurias;
	}

	/**
	 * Metodo que comprueba si un fichero de envio ya ha sido procesado o se trata
	 * de un acuse de recibo.
	 * 
	 * @param nombreFichero
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean ficheroProcesado(String nombreFichero) {

		Session session = null;
		try {

			String sql = "SELECT count(*) FROM o02agpe0.TB_COMUNICACIONES_EXTERNAS c WHERE (c.RESULTADO_ENVIO='OK' AND c.FICHERO_ENVIO = '"
					+ nombreFichero + "')";
			sql += " OR c.FICHERO_RECIBO_F = '" + nombreFichero + "'";

			session = factory.openSession();

			logger.info("Comprobando si el fichero " + nombreFichero + " se ha procesado");
			List<Object> resultado = session.createSQLQuery(sql).list();

			BigDecimal numero = ((BigDecimal) resultado.get(0));
			logger.info("Fichero ya procesado = " + (numero.intValue() > 0));
			if (numero.intValue() > 0)
				return true;
		} catch (Exception e) {
			logger.error("Error en ficheroProcesado ", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return false;
	}

	/**
	 * Metodo para insertar los datos de un fichero enviado a Agroseguro en la tabla
	 * de comunicaciones de corredurias externas
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero enviado a Agroseguro
	 * @param resultadoEnvio
	 *            Resultado del envio del fichero a Agroseguro
	 */
	private void insertarRegistroFichero(String nombreFichero, String resultadoEnvio, BigDecimal idCorreduria) {
		Session session = null;

		String sql = "INSERT INTO o02agpe0.TB_COMUNICACIONES_EXTERNAS VALUES (o02agpe0.SQ_COMUNICACIONES_EXTERNAS.nextval, "
				+ "sysdate, '" + nombreFichero + "', '" + resultadoEnvio + "', null, null, null, null, null,"
				+ idCorreduria + ")";
		logger.info(sql);
		try {
			session = factory.openSession();
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception e) {
			logger.error("Error en insertarRegistroFichero. " + e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private class TXTFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".txt") || name.endsWith(".TXT"));
		}
	}

	private InformacionFileTxt cargaFicheroTxt(String pathFichero, String nombreFichero) throws Exception {
		InformacionFileTxt infTxt = null;
		try (FileReader f = new FileReader(pathFichero);
				BufferedReader b = new BufferedReader(f)) {			
			String textoFichero = b.readLine();
			infTxt = new InformacionFileTxt(textoFichero);
			infTxt.setNombreFichero(nombreFichero);
		} catch (FileNotFoundException e) {
			logger.error("Error en la lectura del fichero de texto " + pathFichero + ". ", e);
			throw (e);
		}
		return infTxt;
	}

	private static SessionFactory getSessionFactory() {
		try {
			Configuration cfg = new Configuration();
			cfg.configure();
			factory = cfg.buildSessionFactory();
		} catch (Exception ex) {
			logger.error("# Error al crear el objeto SessionFactory." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		return factory;
	}

	class InformacionFileTxt {
		private String identificadorRGA;
		private String tipoPolizas;
		private String encoding;
		private Long plan;
		private Long numeroPolizas;
		private String nombreFichero;

		InformacionFileTxt() {
		}

		InformacionFileTxt(String linea) throws Exception {
			try {
				this.setIdentificadorRGA(linea.substring(0, 5));
				this.setTipoPolizas(linea.substring(5, 7));
				this.setEncoding(linea.substring(7, 8));
				this.setPlan(Long.parseLong(linea.substring(8, 12)));
				this.setNumeroPolizas(Long.parseLong(linea.substring(12, 21)));
			} catch (Exception e) {
				throw new Exception(e);
			}
		}

		public String getIdentificadorRGA() {
			return identificadorRGA;
		}

		public void setIdentificadorRGA(String identificadorRGA) {
			this.identificadorRGA = identificadorRGA;
		}

		public String getTipoPolizas() {
			return tipoPolizas;
		}

		public void setTipoPolizas(String tipoPolizas) {
			this.tipoPolizas = tipoPolizas;
		}

		public String getEncoding() {
			return encoding;
		}

		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		public Long getPlan() {
			return plan;
		}

		public void setPlan(Long plan) {
			this.plan = plan;
		}

		public Long getNumeroPolizas() {
			return numeroPolizas;
		}

		public void setNumeroPolizas(Long numeroPolizas) {
			this.numeroPolizas = numeroPolizas;
		}

		public String getNombreFichero() {
			return nombreFichero;
		}

		public void setNombreFichero(String nombreFichero) {
			this.nombreFichero = nombreFichero;
		}

	}
}