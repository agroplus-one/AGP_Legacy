package com.rsi.agp.batch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.rsi.agp.batch.bbdd.Conexion;
import com.rsi.agp.batch.common.DatosEnvio;
import com.rsi.agp.batch.common.ImportacionConstants;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;

import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;

public class RecepcionAgroseguro {

	private static final int DEFAULT_HTTPS_PORT = 443;
	private static final int DEFAULT_PROXY_PORT = 8080;

	// Atributos para la conexion con Agroseguro
	private String address;
	private String verbose;
	private String userAgro;
	private String passwordAgro;

	private String proxyHost = "";
	private String domainName = "";
	private String machineName = "";
	private String proxyUser = "";
	private String proxyPassword = "";
	private boolean disableOverwrite = false;

	// Directorio donde se dejaran los ficheros descargados de Agroseguro para
	// Agroplus
	private String destDir;
	// Directorio donde se dejaran los ficheros descargados de Agroseguro para
	// corredurias externas
	private String destDirExt;
	// Lista de id de envio asociados a envios de polizas
	private List<String> listaIdsEnvioPlz = new ArrayList<String>();
	// Logger
	private static final Logger logger = Logger.getLogger(RecepcionAgroseguro.class);

	public RecepcionAgroseguro() {
		// Inicializamos los atributos necesarios para las conexiones con BBDD y
		// Agroseguro
		ResourceBundle bundle = ResourceBundle.getBundle("agp_recepcion");

		address = getValueFromBundle(bundle, "address");
		verbose = getValueFromBundle(bundle, "verbose");
		userAgro = getValueFromBundle(bundle, "userAgro");
		passwordAgro = getValueFromBundle(bundle, "passwordAgro");
		destDir = getValueFromBundle(bundle, "destDir");
		destDirExt = getValueFromBundle(bundle, "destDirExt");
		proxyHost = getValueFromBundle(bundle, "proxyHost");
		proxyUser = getValueFromBundle(bundle, "proxyUser");
		proxyPassword = getValueFromBundle(bundle, "proxyPassword");

		logProperties();
	}

	public static void main(String[] args) {

		BasicConfigurator.configure();
		logger.info("-- ------------------------------------------- --");
		logger.info("-- INICIO - Recepcion de polizas de Agroseguro --");
		logger.info("-- ------------------------------------------- --");

		RecepcionAgroseguro ra = new RecepcionAgroseguro();
		try {
			ra.procesaFicherosAgroseguro();

		} catch (MalformedURLException e) {
			logger.debug("Error al procesar los acuses", e);
			System.exit(1);
		} catch (Exception e) {
			logger.debug("Error al procesar los acuses", e);
			System.exit(2);
		}

		logger.info("-- FIN - Recepcion de polizas de Agroseguro --");
		System.exit(0);
	}

	/**
	 * Metodo para procesar los acuses de recibo de Agroseguro. Pasos a seguir: 1.
	 * Obtener la lista de ficheros de Agroseguro 2. Obtiene la lista de ficheros ya
	 * tratados en anteriores ejecuciones a partir de la tabla de comunicaciones 3.
	 * Se elimina de la lista de ficheros de Agroseguro los ya procesados en
	 * ejecuciones anteriores 4. Recorrer la lista y procesa los ficheros 4.1. Si es
	 * un fichero de Agroplus => lo tratamos 4.2. Si no es un fichero de Agroplus
	 * 4.2.1. Si es de correduria externa => lo tratamos 4.2.2. Si no => Se inserta
	 * en la tabla de comunicaciones como tratado y se pasa al siguiente
	 * 
	 * @throws Exception
	 */
	public void procesaFicherosAgroseguro() throws Exception {

		// 1. Obtener la lista de ficheros de Agroseguro
		// Se obtiene un listado de todos los TXT, para luego ir comprobando cuales son
		// los que hay que descargar y tratar.
		logger.info("Obtiene el listado completo de ficheros TXT en el buzon de Agroseguro");
		List<String> listFiles = getNombresFicherosAgroseguro();
		logger.info("**@@** Valores de la lista de ficheros obtenida:" + listFiles.size());

		// 2. Obtiene la lista de ficheros ya tratados en anteriores ejecuciones a
		// partir de la tabla de comunicaciones
		logger.info("Obtiene el listado completo de ficheros ya procesados en comunicaciones");
		ArrayList<String> listFilesProcesados = getNombresFicherosProcesadosComunicaciones();
		logger.info("**@@** Valores de la lista de ficheros obtenida:" + listFilesProcesados.size());

		// 3. Se elimina de la lista de ficheros de Agroseguro los ya procesados en
		// ejecuciones anteriores
		logger.info("Elimina de la lista de ficheros de Agroseguro los ya procesados en ejecuciones anteriores");
		if (listFiles.removeAll(listFilesProcesados)) {
			logFicherosAProcesar(listFiles);
		} else {
			logger.info(
					"No se ha podido eliminar de la lista de ficheros de Agroseguro los ya procesados en ejecuciones anteriores");
		}
		// 4. Recorrer la lista y procesa los ficheros
		logger.info("Comienza el procesamiento de ficheros");
		for (String nombreFichero : listFiles)
			procesarFicheroAgroseguro(nombreFichero);
		logger.info("Finaliza el procesamiento de ficheros");

		// Llamada al pl para:
		// 1. Comprobar los anexos en estado definitivo del anio menor al actual
		// 2. Cambio de estado del anexo a estado recibido error (4)
		logger.info("Llamada al procedimiento de actualizacion de anexos");
		this.actualizaAnexosDefNoEnviados();

		// Si estamos en fecha de contratacion de sobreprecio
		if (enPeriodoSobreprecio()) {
			// Se marcan los sobreprecios contratados asociados a las complementarias
			// contratadas en los envios identificados
			// en la lista listaIdsEnvioPlz
			logger.info("Actualiza los sobreprecios contratados asociados a las complementarias contratadas");
			actualizaSbpComplementarias();
		}
	}

	/**
	 * Metodo para procesar un fichero del buzon de Agroseguro
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero a procesar
	 * @throws Exception
	 */
	private void procesarFicheroAgroseguro(String nombreFichero) throws Exception {

		logger.info("Descarga del fichero " + nombreFichero);
		// Descargamos el TXT
		int resultado = FileDownloader.doWork(new URL(address), proxyHost, DEFAULT_PROXY_PORT, destDir, nombreFichero,
				userAgro, passwordAgro, DEFAULT_HTTPS_PORT, domainName, machineName, proxyUser, proxyPassword,
				disableOverwrite, verbose);

		logger.info("Descarga del fichero " + nombreFichero + " finalizada OK");
		if (resultado == 0) {
			// Leero el txt y comprobar si se trata de un fichero de Agroplus
			try (BufferedReader bf = new BufferedReader(new FileReader(destDir + File.separator + nombreFichero))) {
				String textoFichero = bf.readLine();

				if (textoFichero.indexOf("RCONDICXML") < 0 && textoFichero.indexOf("ACONDICXML") < 0
						&& textoFichero.indexOf("RCONGENXML") < 0 && textoFichero.indexOf("RDICDATCXML") < 0
						&& textoFichero.indexOf("RCONORGXML") < 0 && textoFichero.indexOf("RORGINFXML") < 0
						&& textoFichero.indexOf("RRECIBOXML") < 0 && textoFichero.indexOf("REGLAMXML") < 0
						&& textoFichero.indexOf("RCOMISXML") < 0 && textoFichero.indexOf("RCOMIMPXML") < 0
						&& textoFichero.indexOf("RCOPYXML") < 0) {

					// Buscamos en la tabla de comunicaciones si tenemos algun envio pendiente de
					// procesar
					// y cuyo nombre del envio coincida con lo recibido en el txt
					String ficheroEnviado = textoFichero.substring(0, 8);
					String codigoRespuesta = textoFichero.substring(8).trim();
					String ficheroZip = nombreFichero.substring(0, nombreFichero.lastIndexOf(".")) + ".ZIP";

					if (ficheroEnviadoAndNotProcesado(ficheroEnviado)) {
						logger.info("Procesando el fichero " + nombreFichero + "(" + ficheroEnviado + ")");
						// Descargo el zip

						FileDownloader.doWork(new URL(address), proxyHost, DEFAULT_PROXY_PORT, destDir, ficheroZip,
								userAgro, passwordAgro, DEFAULT_HTTPS_PORT, domainName, machineName, proxyUser,
								proxyPassword, disableOverwrite, verbose);

						// Obtenemos el tipo de fichero y el idenvio
						DatosEnvio datos = this.getDatosEnvio(ficheroEnviado);
						// Proceso el acuse: actualizar el estado y el acuse en TB_COMUNICACIONES
						// Actualizar el estado de los objetos en la tabla correspondiente segun el
						// idenvio y el tipo

						if ("G".equals(datos.getTipoEnvio()))
							this.procesarAcuseReciboCuentasRenovables(codigoRespuesta, destDir, ficheroZip,
									datos.getIdEnvio(), datos.getTipoEnvio());
						else
							this.procesarAcuseRecibo(codigoRespuesta, destDir, ficheroZip, datos.getIdEnvio(),
									datos.getTipoEnvio());
					} else {
						// Comprobamos si es un fichero de Correduria externa
						if (ficheroEnviadoAndNotProcesadoCorreduriaExterna(ficheroEnviado)) {
							logger.info("Procesando el fichero de corredurias externas " + nombreFichero + "("
									+ ficheroEnviado + ")");
							// Obtenemos el idenvio
							DatosEnvio datos = this.getDatosEnvioCorreduriaExterna(ficheroEnviado);
							String pathCorreduria = destDirExt + datos.getDirectorioCorreduria();
							// Descargo el fichero en la ruta para la correduria
							logger.info("**@@** Antes de ejecutar el FileDownloader");
							logger.info("**@@** Valor de pathCorreduria:" + pathCorreduria);
							logger.info("**@@** Valor de ficheroZip:" + ficheroZip);
							FileDownloader.doWork(new URL(address), proxyHost, DEFAULT_PROXY_PORT, pathCorreduria,
									ficheroZip, userAgro, passwordAgro, DEFAULT_HTTPS_PORT, domainName, machineName,
									proxyUser, proxyPassword, disableOverwrite, verbose);
							logger.info("**@@** Antes de procesarAcuseReciboCorreduriaExterna");
							logger.info("**@@** Valor de codigoRespuesta: " + codigoRespuesta);
							logger.info("**@@** Valor de destDir: " + destDir);
							logger.info("**@@** Valor de pathCorreduria: " + pathCorreduria);
							logger.info("**@@** Valor de ficheroZip: " + ficheroZip);
							logger.info("**@@** Valor de datos.getIdEnvio(): " + datos.getIdEnvio());
							logger.info("**@@** Valor de ficheroEnviado: " + ficheroEnviado);
							// Actualizo el estado del envio y copio el txt a la ruta de la correduria (el
							// zip ya esta del paso anterior)
							this.procesarAcuseReciboCorreduriaExterna(codigoRespuesta, destDir, pathCorreduria,
									ficheroZip, datos.getIdEnvio(), ficheroEnviado);
						} else {
							// El fichero no es de Agroplus!! => pasamos al siguiente
							// Se inserta el registro en la tabla de comunicaciones para que no se vuelva a
							// procesar en futuras ejecuciones
							insertaRegComunicaciones(nombreFichero);
						}
					}
				} else {
					// Este tipo de fichero no se trata
					// Se inserta el registro en la tabla de comunicaciones para que no se vuelva a
					// procesar en futuras ejecuciones
					insertaRegComunicaciones(nombreFichero);
				}
			}
		} else {
			// Se ha producido un error al descargar el fichero TXT
			logger.info("No se pudo descargar el fichero " + nombreFichero);
		}
	}

	/**
	 * Metodo para obtener un listado con los nombres de los ficheros depositados en
	 * el buzon de Agroseguro.
	 * 
	 * @return Listado de nombres de ficheros
	 * @throws Exception
	 * @throws MalformedURLException
	 */
	private List<String> getNombresFicherosAgroseguro() throws Exception {
		StringBuilder buffer = RecepcionFinder.doWork(new URL(address), proxyHost, DEFAULT_PROXY_PORT,
				DEFAULT_HTTPS_PORT, domainName, machineName, proxyUser, proxyPassword, userAgro, passwordAgro, "DIR",
				"*.TXT", verbose);
		if (buffer == null)
			throw new Exception("Ocurrio un error al ejecutar el comando DIR.");

		// Se obtiene la lista de ficheros
		List<String> listFiles;
		try {
			listFiles = RecepcionFinder.getArrayOfFileNames(buffer, verbose);
			return listFiles;
		} catch (Exception e) {
			logger.error("Error al procesar los ficheros de agroseguro ", e);
		}
		return new ArrayList<String>();
	}

	/**
	 * Metodo para obtener el listado con los nombres de los ficheros ya procesados
	 * en las tablas de comunicaciones
	 * 
	 * @return
	 */
	private static ArrayList<String> getNombresFicherosProcesadosComunicaciones() {

		String sql = "SELECT CONCAT (FICHERO_RECIBO, '.TXT') FROM o02agpe0.TB_COMUNICACIONES WHERE FICHERO_RECIBO IS NOT NULL "
				+ "UNION "
				+ "SELECT CONCAT (FICHERO_RECIBO, '.TXT') FROM o02agpe0.TB_COMUNICACIONES_EXTERNAS WHERE FICHERO_RECIBO IS NOT NULL";
		try {
			return (ArrayList<String>) new Conexion().ejecutaQueryString(sql);

		} catch (Exception e) {
			logger.info("Error al obtener los ficheros ya procesados de comunicaciones ", e);
		}

		return new ArrayList<String>();
	}

	/**
	 * Metodo para comprobar si un fichero propio de Agroplus ha sido enviado a
	 * Agroseguro y no ha sido procesado
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero
	 * @return true si se ha enviado
	 */
	private boolean ficheroEnviadoAndNotProcesado(String nombreFichero) {
		Conexion c = new Conexion();

		String sql = "SELECT count(*) FROM o02agpe0.TB_COMUNICACIONES c WHERE UPPER (c.FICHERO_ENVIO) = UPPER ('"
				+ nombreFichero + "')" + " AND FICHERO_RECIBO IS NULL";

		try {
			List<Object> resultado = c.ejecutaQuery(sql, 1);
			int numero = Integer.parseInt(((Object[]) resultado.get(0))[0] + "");
			if (numero > 0)
				return true;
		} catch (Exception e) {
			logger.info("Ocurrio un error al comprobar si el fichero " + nombreFichero
					+ " ha sido enviado desde Agroplus a Agroseguro y no ha sido procesado", e);
		}

		return false;
	}

	/**
	 * Metodo para comprobar si un fichero de correduria externa ha sido enviado a
	 * Agroseguro y no ha sido procesado
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero
	 * @return true si se ha enviado
	 */
	private boolean ficheroEnviadoAndNotProcesadoCorreduriaExterna(String nombreFichero) {
		Conexion c = new Conexion();

		String sql = "SELECT count(*) FROM o02agpe0.TB_COMUNICACIONES_EXTERNAS c WHERE UPPER (c.FICHERO_ENVIO) = UPPER ('"
				+ nombreFichero + "')" + " AND FICHERO_RECIBO IS NULL";

		try {
			List<Object> resultado = c.ejecutaQuery(sql, 1);
			int numero = Integer.parseInt(((Object[]) resultado.get(0))[0] + "");
			if (numero > 0)
				return true;
		} catch (Exception e) {
			logger.info("Ocurrio un error al comprobar si el fichero " + nombreFichero
					+ " ha sido enviado desde Correduria externa a Agroseguro y no ha sido procesado", e);
		}

		return false;
	}

	/**
	 * Metodo para obtener los datos del envio de un fichero de Agroplus (tipo de
	 * envio e identificador)
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero
	 * @return true si se ha enviado
	 */
	private DatosEnvio getDatosEnvio(String nombreFichero) {
		Conexion c = new Conexion();

		String sql = "SELECT IDENVIO, FICHERO_TIPO FROM o02agpe0.TB_COMUNICACIONES c WHERE UPPER (c.FICHERO_ENVIO) = UPPER ('"
				+ nombreFichero + "')";

		try {
			List<Object> resultado = c.ejecutaQuery(sql, 2);
			String idenvio = ((Object[]) resultado.get(0))[0] + "";
			String tipoEnvio = ((Object[]) resultado.get(0))[1] + "";
			return new DatosEnvio(tipoEnvio, idenvio);
		} catch (Exception e) {
			logger.info(
					"Ocurrio un error al obtener los datos del envio de un fichero de Agroplus (" + nombreFichero + ")",
					e);
		}

		return null;
	}

	/**
	 * Metodo para obtener los datos del envio de un fichero de Correduria externa
	 * (identificador)
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero
	 * @return true si se ha enviado
	 */
	private DatosEnvio getDatosEnvioCorreduriaExterna(String nombreFichero) {
		Conexion c = new Conexion();

		String sql = "SELECT c.ID, ex.RUTA_FTP FROM o02agpe0.TB_COMUNICACIONES_EXTERNAS c "
				+ "INNER JOIN o02agpe0.TB_CORREDURIAS_EXTERNAS ex ON c.IDCORREDURIA = ex.ID "
				+ "WHERE UPPER (c.FICHERO_ENVIO) = UPPER ('" + nombreFichero + "')";

		try {
			List<Object> resultado = c.ejecutaQuery(sql, 2);
			String idenvio = ((Object[]) resultado.get(0))[0] + "";
			String directorioCorreduria = ((Object[]) resultado.get(0))[1] + "";
			return new DatosEnvio("", idenvio, directorioCorreduria);
		} catch (Exception e) {
			logger.info("Ocurrio un error al obtener los datos del envio de un fichero de Correduria externa ("
					+ nombreFichero + ")", e);
		}
		return null;
	}

	/**
	 * Metodo para procesar y actualizar los estados de los objetos en base al acuse
	 * de recibo proporcionado por Agroseguro
	 * 
	 * @param codigoRespuesta
	 *            Codigo de respuesta del envio (A, R o X)
	 * @param rutaDestino
	 *            Ruta donde se encuentra el fichero zip
	 * @param nombreFichero
	 *            Nombre del fichero zip (incluye extension)
	 * @param idEnvio
	 *            Identificador del envio a Agroseguro en TB_COMUNICACIONES
	 * @param tipoEnvio
	 *            Tipo de envioo: G - Cuentas polizas renovables
	 * @throws Exception
	 */
	public void procesarAcuseReciboCuentasRenovables(String codigoRespuesta, String rutaDestino, String nombreFichero,
			String idEnvio, String tipoEnvio) throws Exception {
		try (ZipFile zipFile = new ZipFile(rutaDestino + File.separator + nombreFichero)) {
			
			ZipEntry zipEntry = zipFile.getEntry("AcuseRecibo.xml");

			if (zipEntry == null)
				throw new IllegalArgumentException("No se ha encontrado el fichero AcuseRecibo");

			try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
				AcuseReciboDocument acuseReciboDocument = AcuseReciboDocument.Factory.parse(bis);

				String resultado = "CORRECTO";
				String sql = "";

				Conexion conexion = new Conexion();

				logger.info("Respuesta:'" + codigoRespuesta + "', Tipo envio: '" + tipoEnvio);
				if (codigoRespuesta.equals("X")) {
					// Ha habido algun elemento del envio rechazado por agroseguro. Hay que
					// tratarlos uno a uno
					this.generaConsultaActualizacionRegistroCuentasRenovables(acuseReciboDocument, conexion);
				} else {
					// En caso de que todos los elementos se hayan aceptado o rechazado, los
					// tratamos todos a la vez
					if (codigoRespuesta.equals("R")) {
						resultado = "ERROR";
					}

					sql = generaConsultaActualizacionEnvioCuentasRenovables(codigoRespuesta, idEnvio);

					logger.info("Estado: 'A o R'");

					conexion.ejecutaUpdate(sql); // Actualizamos los estados.
				}

				// Guardamos el Acuse de recibo en el envio
				if (acuseReciboDocument != null && acuseReciboDocument.getAcuseRecibo() != null
						&& acuseReciboDocument.getAcuseRecibo().xmlText() != null
						&& !acuseReciboDocument.getAcuseRecibo().xmlText().equals("")) {

					conexion.actualizaAcuse(acuseReciboDocument.getAcuseRecibo().xmlText(), idEnvio, resultado,
							nombreFichero);
				} else {
					conexion.actualizaAcuse(null, idEnvio, resultado, nombreFichero);
				}
				
				logger.info("OK - Estados actualizados correctamente");

				// Actualizamos el historico de estados
				conexion.actualizaHistoricoEstados(idEnvio, tipoEnvio);
			}			
		}
	}

	/**
	 * Metodo para procesar y actualizar los estados de los objetos en base al acuse
	 * de recibo proporcionado por Agroseguro
	 * 
	 * @param codigoRespuesta
	 *            Codigo de respuesta del envio (A, R o X)
	 * @param rutaDestino
	 *            Ruta donde se encuentra el fichero zip
	 * @param nombreFichero
	 *            Nombre del fichero zip (incluye extension)
	 * @param idEnvio
	 *            Identificador del envio a Agroseguro en TB_COMUNICACIONES
	 * @param tipoEnvio
	 *            Tipo de envio: P-POLIZA, R-ANEXO REDUCCION, S-SINIESTRO, M-ANEXO
	 *            MODIFICACION, B-SOBREPRECIO
	 * @throws Exception
	 */
	public void procesarAcuseRecibo(String codigoRespuesta, String rutaDestino, String nombreFichero, String idEnvio,
			String tipoEnvio) throws Exception {
		try (ZipFile zipFile = new ZipFile(rutaDestino + File.separator + nombreFichero)) {
			
			ZipEntry zipEntry = zipFile.getEntry("AcuseRecibo.xml");

			if (zipEntry == null)
				throw new IllegalArgumentException("No se ha encontrado AcuseRecibo.xml");

			try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
				AcuseReciboDocument acuseReciboDocument = AcuseReciboDocument.Factory.parse(bis);

				/*
				 * Estados posibles de la Poliza ## 1 Pendiente Validacion ## 2 Grabacion
				 * Provisional ## 3 Grabacion Definitiva ## 4 Anulada ## 5 Enviada Pendiente de
				 * Confirmar ## 6 Enviada Correcta ## 7 Enviada Erronea ## 8 Definitiva
				 * 
				 * Estados posibles de los anexos y siniestros ## 1 En borrador ## 2 Enviado ##
				 * 3 Recibido correcto ## 4 Recibido error
				 */

				String resultado = "CORRECTO";
				String sql = "";

				Conexion conexion = new Conexion();

				if (codigoRespuesta.equals("X")) {
					// Ha habido algun elemento del envio rechazado por agroseguro. Hay que
					// tratarlos uno a uno
					for (int i = 0; i < acuseReciboDocument.getAcuseRecibo().getDocumentoArray().length; i++) {
						Documento documento = acuseReciboDocument.getAcuseRecibo().getDocumentoArray(i);

						// MPM - Si el tipo de envio es S (siniestros) no hay que obtener la referencia
						// porque no se usa y en su lugar viene el
						// numero interno de envio, lo cual puede producir errores
						String referencia = "S".equals(tipoEnvio) ? "" : documento.getId().substring(0, 7);

						sql = this.generaConsultaActualizacionRegistro(tipoEnvio, idEnvio, referencia,
								documento.getId(), documento.getEstado());

						logger.info("Respuesta: '" + codigoRespuesta + "', Tipo envio: '" + tipoEnvio + "', Estado: '"
								+ documento.getEstado() + "'");

						conexion.ejecutaUpdate(sql); // Ejecutamos la sentencia.

					} // fin for
				} else {
					// En caso de que todos los elementos se hayan aceptado o rechazado, los
					// tratamos todos a la vez
					if (codigoRespuesta.equals("R")) {
						resultado = "ERROR";
					}

					sql = generaConsultaActualizacionEnvio(codigoRespuesta, idEnvio, tipoEnvio);

					logger.info(
							"Respuesta: '" + codigoRespuesta + "', Tipo envio: '" + tipoEnvio + "', Estado: 'A o R'");

					conexion.ejecutaUpdate(sql); // Actualizamos los estados.
				}

				// Guardamos el Acuse de recibo en el envio
				if (acuseReciboDocument != null && acuseReciboDocument.getAcuseRecibo() != null
						&& acuseReciboDocument.getAcuseRecibo().xmlText() != null
						&& !acuseReciboDocument.getAcuseRecibo().xmlText().equals("")) {

					conexion.actualizaAcuse(acuseReciboDocument.getAcuseRecibo().xmlText(), idEnvio, resultado,
							nombreFichero);
				} else {
					conexion.actualizaAcuse(null, idEnvio, resultado, nombreFichero);
				}

				logger.info("OK - Estados de las polizas recibidas actualizados correctamente");

				// Actualizamos el historico de estados
				conexion.actualizaHistoricoEstados(idEnvio, tipoEnvio);
			}
			// Si el fichero es de polizas y se ha aceptado (total o parcialmente) se
			// incluye el id de envio en la lista
			if (("A".equals(codigoRespuesta) || "X".equals(codigoRespuesta)) && (("P").equals(tipoEnvio))) {
				setIdEnvio(idEnvio);
			}
		}
	}

	/**
	 * Metodo para actualizar los datos de recepcion de un fichero de correduria
	 * externa.
	 * 
	 * @param codigoRespuesta
	 *            Respuesta incluida en el TXT.
	 * @param rutaDestino
	 *            Ruta donde se dejara el acuse.
	 * @param nombreFichero
	 *            Nombre del fichero de acuse.
	 * @param idEnvio
	 *            Identificador del envio en la tabla de comunicaciones de
	 *            correduria externa
	 * @param ficheroEnviado
	 *            Nombre del fichero enviado a Agroseguro
	 * @throws Exception
	 */
	public void procesarAcuseReciboCorreduriaExterna(String codigoRespuesta, String rutaDestinoTxt, String rutaDestino,
			String nombreFichero, String idEnvio, String ficheroEnviado) throws Exception {
		logger.info("**@@** Dentro de procesarAcuseReciboCorreduriaExterna");

		try (ZipFile zipFile = new ZipFile(rutaDestino + File.separator + nombreFichero)) {
			
			ZipEntry zipEntry = zipFile.getEntry("AcuseRecibo.xml");

			if (zipEntry == null)
				throw new IllegalArgumentException("No se ha encontrado AcuseRecibo.xml");

			try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
				AcuseReciboDocument acuseReciboDocument = AcuseReciboDocument.Factory.parse(bis);

				String resultado = "OK";

				if (codigoRespuesta.equals("R")) {
					resultado = "KO";
				}

				Conexion conexion = new Conexion();

				// Guardamos el Acuse de recibo en el envio
				if (acuseReciboDocument != null && acuseReciboDocument.getAcuseRecibo() != null
						&& acuseReciboDocument.getAcuseRecibo().xmlText() != null
						&& !acuseReciboDocument.getAcuseRecibo().xmlText().equals("")) {

					logger.info("**@@** Antes de actualizarAcuseCorreduriaExterna en BBDD");
					conexion.actualizaAcuseCorreduriaExterna(acuseReciboDocument.getAcuseRecibo().xmlText(), idEnvio,
							resultado, nombreFichero, ficheroEnviado);
				} else {
					conexion.actualizaAcuseCorreduriaExterna(null, idEnvio, resultado, nombreFichero, ficheroEnviado);
				}

				// SI AL MENOS HAY UN DOCUMENTO ACEPTADO, HAY QUE PREPARARLO PARA
				// IMPORTACION
				if (acuseReciboDocument != null && acuseReciboDocument.getAcuseRecibo() != null
						&& acuseReciboDocument.getAcuseRecibo().getDocumentosAceptados() > 0) {
					String tipoDocumento = acuseReciboDocument.getAcuseRecibo().getTipoDocumento();
					Documento[] documentos = acuseReciboDocument.getAcuseRecibo().getDocumentoArray();
					String[] codDocs = new String[documentos.length];
					// RECORREMOS LOS DOCUMENTOS DEL ACUSE DE RECIBO
					for (int i = 0; i < documentos.length; i++) {
						// Y GUARDAMOS PARA IMPORTACION AQUELLOS QUE HAN SIDO ACEPTADOS
						if (documentos[i].getEstado() == ImportacionConstants.AGROSEGURO_DOC_ACEPTADO) {
							codDocs[i] = documentos[i].getId();
						}
					}
					preparaPolizasImportacion(codDocs, idEnvio, conexion, tipoDocumento);
				}
			}
			// Renombrar el acuse de recibo con el nombre del fichero enviado concatenado
			// con "_AR"
			// ZIP
			logger.info("Correduria externa - Copiando " + rutaDestino + File.separator + nombreFichero + " a " + rutaDestino + File.separator
					+ ficheroEnviado + "_AR.ZIP");
			File origen = new File(rutaDestino + File.separator + nombreFichero);
			File destino = new File(rutaDestino + File.separator + ficheroEnviado + "_AR.ZIP");
			boolean renorigen = origen.renameTo(destino);
			logger.info("**@@** Despues del rename del destino, valor de ren_origen:" + renorigen);
			// TXT
			logger.info("Correduria externa - Copiando " + rutaDestinoTxt + File.separator
					+ nombreFichero.substring(0, nombreFichero.lastIndexOf(".")) + ".TXT" + " a " + rutaDestino + File.separator
					+ ficheroEnviado + "_AR.TXT");
			File origenTxt = new File(
					rutaDestinoTxt + File.separator + nombreFichero.substring(0, nombreFichero.lastIndexOf(".")) + ".TXT");
			File destinoTxt = new File(rutaDestino + File.separator + ficheroEnviado + "_AR.TXT");

			boolean renorigen2 = origenTxt.renameTo(destinoTxt);
			logger.info("**@@** Despues del rename del origen2, valor de ren_origen2:" + renorigen2);

			logger.info("OK - Estados de las polizas recibidas actualizados correctamente");
		} 
	}

	private void preparaPolizasImportacion(final String[] codDocs, final String idEnvio, final Conexion conexion,
			final String tipoDocumento) throws Exception {
		String codPlan;
		String codLinea;
		String referencia;
		String tipoRef = null;
		if (tipoDocumento.compareTo("SD") == 0) {
			tipoRef = "C";
		} else {
			tipoRef = "P";
		}
		for (String codDoc : codDocs) {
			if (codDoc != null) {
				try {
					// EL CODIGO DE DOCUMENTO ESTA EN FORMATO:
					// REFERENCIA+LINEA+PLAN
					referencia = codDoc.substring(0, 7);
					codPlan = codDoc.substring(7, 11);
					codLinea = codDoc.substring(11);
					// GUARDAMOS LA POLIZA COMO PDTE DE IMPORTACION
					conexion.guardaPolizaExtParaImportacion(codPlan, codLinea, referencia, idEnvio, tipoRef);
					logger.info("Poliza " + referencia + " preparada para su importacion");
				} catch (Exception e) {
					logger.info("Error al guardar la poliza para su importacion", e);
				}
			}
		}
	}

	/**
	 * Metodo para generar la consulta de actualizacion de los registros segun el
	 * acuse
	 * 
	 * @param tipoEnvio
	 *            Tipo de envio: polizas, anexos, siniestros...
	 * @param idEnvio
	 *            Identificador del envio
	 * @param referencia
	 *            Referencia de la poliza
	 * @param id
	 *            Identificador del acuse de recibo (para siniestros)
	 * @param estado
	 *            Estado del registro en el acuse de recibo
	 * @return Consulta a ejecutar para actualizar el estado del objeto
	 *         correspondiente
	 */
	private void generaConsultaActualizacionRegistroCuentasRenovables(AcuseReciboDocument acuseReciboDocument,
			Conexion conexion) {
		// String tipoEnvio, String idEnvio, String referencia, Documento doc
		String sql = "";

		// 1 - Recorrer doc y generar lista de referencias correctas y erroneas.
		// Para las correctas ejecutar un update para actualizar el envio IBAN a
		// 4-correcto
		// y destinatario domiciliacion a 'A' de todas las polizas renovables.
		// Para las erroneas, ejecutar un update para acutalizar el IBAN A 5- Erroneo.
		List<String> lstCorrectas = new ArrayList<String>();
		List<String> lstErroneas = new ArrayList<String>();
		String plan = "";
		for (int i = 0; i < acuseReciboDocument.getAcuseRecibo().getDocumentoArray().length; i++) {
			Documento doc = acuseReciboDocument.getAcuseRecibo().getDocumentoArray(i);
			String referencia = doc.getId().substring(0, 7);
			plan = doc.getId().substring(7, 11);
			logger.info("Referencia: '" + referencia + "', plan: " + plan + ", Estado: '" + doc.getEstado() + "'");
			if (doc.getEstado() == 1)
				lstCorrectas.add(referencia);
			if (doc.getEstado() == 2)
				lstErroneas.add(referencia);

		} // fin for
		if (!lstCorrectas.isEmpty()) {
			sql = updatePolizasRenCorrectas(lstCorrectas, plan);
			try {
				conexion.ejecutaUpdate(sql);
			} catch (Exception e) {
				logger.info("Error al actualizar las polizas renovables correctas ", e);
			}
		}
		if (!lstErroneas.isEmpty()) {
			sql = updatePolizasRenErroneas(lstErroneas, plan);
			try {
				conexion.ejecutaUpdate(sql);
			} catch (Exception e) {
				logger.info("Error al actualizar las polizas renovables erroneas ", e);
			}
		}
	}

	private String updatePolizasRenCorrectas(List<String> lstCorrectas, String plan) {
		List<String> lstCadReferencias = getListasParaIN(lstCorrectas);
		boolean primera = true;
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(
				"UPDATE o02agpe0.TB_POLIZAS_RENOVABLES ren SET ESTADO_ENVIO_IBAN_AGRO = 4, DESTINO_DOMICILIACION = 'A' WHERE 1=1");
		if (!lstCadReferencias.isEmpty()) {
			primera = true;
			for (String ref : lstCadReferencias) {
				if (primera) {
					stringQuery.append(" and (ren.referencia in (" + ref + ")");
					primera = false;
				} else {
					stringQuery.append(" or ren.referencia in (" + ref + ")");
				}
			}
			stringQuery.append(")");
			stringQuery.append(" and plan = " + plan);
		}
		return stringQuery.toString();
	}

	private String updatePolizasRenErroneas(List<String> lstCorrectas, String plan) {
		List<String> lstCadReferencias = getListasParaIN(lstCorrectas);
		boolean primera = true;
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append("UPDATE o02agpe0.TB_POLIZAS_RENOVABLES ren SET ESTADO_ENVIO_IBAN_AGRO = 5 WHERE 1=1");
		if (!lstCadReferencias.isEmpty()) {
			primera = true;
			for (String ref : lstCadReferencias) {
				if (primera) {
					stringQuery.append(" and (ren.referencia in (" + ref + ")");
					primera = false;
				} else {
					stringQuery.append(" or ren.referencia in (" + ref + ")");
				}
			}
			stringQuery.append(")");
			stringQuery.append(" and plan = " + plan);
		}
		return stringQuery.toString();
	}

	/**
	 * Metodo para generar la consulta de actualizacion de los registros segun el
	 * acuse
	 * 
	 * @param tipoEnvio
	 *            Tipo de envio: polizas, anexos, siniestros...
	 * @param idEnvio
	 *            Identificador del envio
	 * @param referencia
	 *            Referencia de la poliza
	 * @param id
	 *            Identificador del acuse de recibo (para siniestros)
	 * @param estado
	 *            Estado del registro en el acuse de recibo
	 * @return Consulta a ejecutar para actualizar el estado del objeto
	 *         correspondiente
	 */
	private String generaConsultaActualizacionRegistro(String tipoEnvio, String idEnvio, String referencia, String id,
			int estado) {
		String sql = "";
		int estadoObjeto = 0;
		if (tipoEnvio.equals("P")) {
			// Los acuses pertenecen a polizas. Si el estado del acuse es 1, se cambia por 8
			// y si es 2, se cambia por 7
			if (estado == 1) {
				estadoObjeto = 8;
			} else {
				estadoObjeto = 7;
			}
			sql = "UPDATE o02agpe0.TB_POLIZAS SET IDESTADO = " + estadoObjeto + " WHERE REFERENCIA = '" + referencia
					+ "'" + " AND IDENVIO=" + idEnvio;
		} else {
			// Para el resto de objetos los estados coinciden. Calculamos el estado.
			if (estado == 1) {
				estadoObjeto = 3;
			} else {
				estadoObjeto = 4;
			}

			// En funcion del tipo, asignamos la tabla para generar la sentencia de
			// actualizacion
			if (tipoEnvio.equals("M")) {
				// Los acuses pertenecen a anexos de modificacion
				sql = "UPDATE o02agpe0.TB_ANEXO_MOD SET ESTADO = " + estadoObjeto + " WHERE IDPOLIZA IN "
						+ "(SELECT IDPOLIZA FROM o02agpe0.TB_POLIZAS WHERE REFERENCIA = '" + referencia + "')"
						+ " AND IDENVIO= " + idEnvio;
			} else if (tipoEnvio.equals("R")) {
				// Los acuses pertenecen a anexos de reducciones de capital
				sql = "UPDATE o02agpe0.TB_ANEXO_RED SET IDESTADO = " + estadoObjeto + " WHERE IDPOLIZA IN "
						+ "(SELECT IDPOLIZA FROM o02agpe0.TB_POLIZAS WHERE REFERENCIA = '" + referencia + "')"
						+ " AND IDENVIO =" + idEnvio;
			} else {
				// tipoEnvio = 'S'. Los acuses pertenecen a siniestros
				sql = "UPDATE o02agpe0.TB_SINIESTROS SET ESTADO = " + estadoObjeto + " WHERE NUMINTERNOENVIO = " + id
						+ " AND IDENVIO = " + idEnvio;
			}
		}
		return sql;
	}

	/**
	 * Metodo para generar la cosulta de actualizacion de un envio completo
	 * 
	 * @param codigoRespuesta
	 *            Codigo devuelto por agroseguro. Sera A (aceptado) o R (rechazado)
	 * @param idEnvio
	 *            Identificador del envio en la tabla TB_COMUNICACIONES
	 * @param tipoEnvio
	 *            Tipo de envio: polizas, anexos,...
	 * @return Consulta de actualizacion de los objetos
	 */
	private String generaConsultaActualizacionEnvioCuentasRenovables(String codigoRespuesta, String idEnvio) {
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append("UPDATE o02agpe0.TB_POLIZAS_RENOVABLES ");
		if (codigoRespuesta.equals("R"))
			stringQuery.append(" SET ESTADO_ENVIO_IBAN_AGRO = 5");
		if (codigoRespuesta.equals("A"))
			stringQuery.append(" SET ESTADO_ENVIO_IBAN_AGRO = 4, DESTINO_DOMICILIACION = 'A' ");
		stringQuery.append(" WHERE  IDENVIO =" + idEnvio);

		return stringQuery.toString();
	}

	/**
	 * Metodo para generar la cosulta de actualizacion de un envio completo
	 * 
	 * @param codigoRespuesta
	 *            Codigo devuelto por agroseguro. Sera A (aceptado) o R (rechazado)
	 * @param idEnvio
	 *            Identificador del envio en la tabla TB_COMUNICACIONES
	 * @param tipoEnvio
	 *            Tipo de envio: polizas, anexos,...
	 * @return Consulta de actualizacion de los objetos
	 */
	private String generaConsultaActualizacionEnvio(String codigoRespuesta, String idEnvio, String tipoEnvio) {
		String sql = "";
		String estado = "";

		if (codigoRespuesta.equals("A")) {
			if (tipoEnvio.equals("P"))
				estado = "8";
			else
				estado = "3";
		} else if (codigoRespuesta.equals("R")) {
			if (tipoEnvio.equals("P"))
				estado = "7";
			else
				estado = "4";
		}

		if (tipoEnvio.equals("P")) {
			// Actualizar las polizas
			sql = "UPDATE o02agpe0.TB_POLIZAS SET IDESTADO = " + estado + " WHERE IDENVIO=" + idEnvio;
		} else if (tipoEnvio.equals("R")) {
			// Actualizar los anexos de reduccion de capital
			sql = "UPDATE o02agpe0.TB_ANEXO_RED SET IDESTADO = " + estado + " WHERE IDENVIO= " + idEnvio;
		} else if (tipoEnvio.equals("M")) {
			// Actualizar los anexos de modificacion
			sql = "UPDATE o02agpe0.TB_ANEXO_MOD SET ESTADO = " + estado + " WHERE IDENVIO =" + idEnvio;
		} else if (tipoEnvio.equals("S")) {
			// Actualizar los siniestros
			sql = "UPDATE o02agpe0.TB_SINIESTROS SET ESTADO = " + estado + " WHERE IDENVIO = " + idEnvio;
		}
		return sql;
	}

	/**
	 * Lanza la consulta necesaria para saber si estamos en fechas de contratacion
	 * de sobreprecio para el ultimo plan dado de alta en el mantenimiento de fechas
	 * de contratacion
	 * 
	 * @return Boolean
	 */
	private boolean enPeriodoSobreprecio() {

		logger.info("Se comprueba si estamos en periodo de contratacion de sobreprecio");

		int numero = 0;
		Conexion c = new Conexion();
		String sql = "SELECT COUNT(*) " + "FROM (SELECT MIN(FECHAINICIO) FINI, MAX(FECHAFIN)+1 FFIN "
				+ "FROM o02agpe0.TB_SBP_FECHAS_CONTRATACION FC, o02agpe0.TB_LINEAS L "
				+ "WHERE FC.LINEASEGUROID = L.LINEASEGUROID " + "AND L.CODPLAN IN " + "(SELECT MAX(L.CODPLAN) "
				+ "FROM o02agpe0.TB_SBP_FECHAS_CONTRATACION FC, o02agpe0.TB_LINEAS L "
				+ "WHERE FC.LINEASEGUROID = L.LINEASEGUROID)) " + "WHERE FINI <= TO_DATE(SYSDATE, 'DD/MM/YY') "
				+ "AND FFIN >= TO_DATE(SYSDATE, 'DD/MM/YY')";

		try {
			List<Object> resultado = c.ejecutaQuery(sql, 1);
			numero = Integer.parseInt(((Object[]) resultado.get(0))[0] + "");
		} catch (Exception e) {
			logger.info("Ocurrio un error al comprobar si la fecha actual esta dentro de las fechas de contratacion"
					+ " de sobreprecio para el ultimo plan dado de alta en el mantenimiento de fechas de contratacion",
					e);
		}

		logger.info("En fechas de sbp = " + (numero == 1));
		return (numero == 1);
	}

	/**
	 * Lanza la consulta necesaria para actualizar los sobreprecios contratados que
	 * no incluyan la complementaria asociados a polizas complementarias contratadas
	 * correspondientes a los envios indicados en la lista de ids de envio de
	 * polizas
	 * 
	 * @param listaIdsEnvio
	 */
	private void actualizaSbpComplementarias() {

		if (this.listaIdsEnvioPlz == null || this.listaIdsEnvioPlz.isEmpty()) {
			logger.info("La lista de ids de envio esta vacia, no se actualizara ninguna poliza.");
			return;
		}

		logger.info(
				"Se actualizan para generar suplementos los sobreprecios asociados a las complementarias contratadas en los envios con id = "
						+ StringUtils.toValoresSeparadosXComas(this.listaIdsEnvioPlz, false));

		Conexion c = new Conexion();
		String sql = "UPDATE O02AGPE0.TB_SBP_POLIZAS SP " + "SET SP.GEN_SPL_CPL = 'S' "
				+ "WHERE SP.ID IN (SELECT SP.ID " + " FROM O02AGPE0.TB_POLIZAS P, O02AGPE0.TB_SBP_POLIZAS SP"
				+ " WHERE P.IDPOLIZA_PPAL = SP.IDPOLIZA" + " AND SP.IDESTADO = " + ConstantsSbp.ESTADO_ENVIADA_CORRECTA
				+ " AND SP.INC_SBP_COMP = 'N'" + " AND P.IDESTADO = " + Constants.ESTADO_POLIZA_DEFINITIVA
				+ " AND P.TIPOREF = 'C'" + " AND P.IDENVIO IN "
				+ StringUtils.toValoresSeparadosXComas(this.listaIdsEnvioPlz, false) + ")";

		try {
			c.ejecutaUpdate(sql);
		} catch (Exception e) {
			logger.debug("Ocurrio un error al actualizar los sobreprecios", e);
		}
	}

	/**
	 * Incluye el idEnvio indicado en el parametro en la lista
	 * 
	 * @param idEnvio
	 */
	private void setIdEnvio(String idEnvio) {
		// Comprueba que la lista y el id de envio no sean nulos
		if (this.listaIdsEnvioPlz != null && idEnvio != null) {
			try {
				// Comprueba que el id de envia sea numerico
				Integer.parseInt(idEnvio);

				this.listaIdsEnvioPlz.add(idEnvio);
			} catch (NumberFormatException e) {
				logger.info("El id de envio '" + idEnvio + "' no es correcto", e);
			}
		}
	}

	/**
	 * Devuelve el valor correspondiente a la clave presente en el fichero de
	 * propiedades
	 * 
	 * @param bundle
	 *            Fichero de propiedades
	 * @param key
	 *            Clave
	 * @return String
	 */
	private String getValueFromBundle(ResourceBundle bundle, String key) {
		try {
			return bundle.getString(key);
		} catch (Exception e) {
			logger.info("No se pudo cargar el valor de '" + key + "' del fichero de propiedades");
			return "";
		}
	}

	/**
	 * Inserta el nombre del fichero (sin extension TXT) en la tabla de
	 * comunicaciones
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero a insertar
	 */
	private void insertaRegComunicaciones(String nombreFichero) {
		// Se inserta el registro en la tabla de comunicaciones para que no se vuelva a
		// procesar en futuras ejecuciones
		try {
			new Conexion().insertaRegComunicaciones(nombreFichero.replaceAll(".TXT", ""));
		} catch (Exception e) {
			logger.info("Ocurrio un error al insertar el registro en la tabla de comunicaciones", e);
		}

		logger.info("Fichero " + nombreFichero + " insertado correctamente en la tabla de comunicaciones");
	}

	/**
	 * Realiza la llamada al procedimiento que actualiza los anexos de modificacion
	 */
	private void actualizaAnexosDefNoEnviados() {
		Conexion conexion = new Conexion();
		conexion.actualizaAnexosDefNoEnviados();
	}

	/**
	 * Escribe en el log los valores cargados del fichero de propiedades
	 */
	private void logProperties() {
		logger.info("Valor obtenido para 'address': " + (this.address == null ? "" : this.address));
		logger.info("Valor obtenido para 'verbose': " + (this.verbose == null ? "" : this.verbose));
		logger.info("Valor obtenido para 'userAgro': " + (this.userAgro == null ? "" : this.userAgro));
		logger.info("Valor obtenido para 'passwordAgro': " + (this.passwordAgro == null ? "" : this.passwordAgro));
		logger.info("Valor obtenido para 'destDir': " + (this.destDir == null ? "" : this.destDir));
		logger.info("Valor obtenido para 'destDirExt': " + (this.destDirExt == null ? "" : this.destDirExt));
		logger.info("Valor obtenido para 'proxyHost': " + (this.proxyHost == null ? "" : this.proxyHost));
		logger.info("Valor obtenido para 'proxyUser': " + (this.proxyUser == null ? "" : this.proxyUser));
		logger.info("Valor obtenido para 'proxyPassword': " + (this.proxyPassword == null ? "" : this.proxyPassword));
	}

	/**
	 * Escribe en el log los ficheros que se van a procesar
	 * 
	 * @param lista
	 */
	private void logFicherosAProcesar(List<String> lista) {
		if (lista != null && !lista.isEmpty()) {
			logger.info("Se procesaran los siguientes " + lista.size() + " archivos: "
					+ StringUtils.toValoresSeparadosXComas(lista, true, false));
		} else {
			logger.info("No hay ficheros nuevos que procesar");
		}
	}

	public List<String> getListasParaIN(List<String> lstReferencias) {
		List<String> lstCadenasRef = new ArrayList<String>();
		int contador = 0;
		String cadena = "";
		boolean primera = true;
		for (String ref : lstReferencias) {
			if (contador < 1000) {
				if (!primera)
					cadena = cadena + ",";
				else
					primera = false;
				cadena = cadena + "'" + ref.trim() + "'";
				contador++;
			} else {
				if (cadena.length() > 0)
					lstCadenasRef.add(cadena);
				cadena = "'" + ref + "'";
				contador = 1;
			}
		}
		lstCadenasRef.add(cadena);
		logger.debug(
				"Numero total de elementos: " + lstReferencias.size() + ". Listas partidas: " + lstCadenasRef.size());
		return lstCadenasRef;
	}

}
