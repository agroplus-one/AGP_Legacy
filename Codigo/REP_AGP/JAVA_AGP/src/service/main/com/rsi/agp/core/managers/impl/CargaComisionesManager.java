package com.rsi.agp.core.managers.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.impl.IImportacionComisionesUnificadoDao;
import com.rsi.agp.core.managers.ICargaComisionesManager;
import com.rsi.agp.core.managers.impl.ComisionesUnificadas.IImportacionComisionesUnificadoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.FTPDownloader;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.ConfigAgp;

public class CargaComisionesManager implements ICargaComisionesManager {

	private static final Log logger = LogFactory.getLog(CargaComisionesManager.class);

	private IImportacionComisionesUnificadoManager importacionComisionesUnificadoManager;
	private IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao;

	private ResourceBundle bundle = ResourceBundle.getBundle("agp_coms_auto");
	
	private static final String RGASTOSENT_S_S$ = "^RGASTOSENT.*C616.*%s.*%s$";
		
	/**
	 * 
	 * @return el nombre del fichero de comisiones de entidad mas reciente
	 * @throws BusinessException 
	 */
	public int cargaFichero(HttpServletRequest request, Usuario usuario) throws BusinessException {

		actualizarBarraProgreso(request, "UPLOADING", 0);
		
		logger.info("CargaComisionesManager - cargaFichero  - init");

		actualizarBarraProgreso(request, "UPLOADING", 3);
		
		String rutaFichero = obtenerNombreFichero();
		
		actualizarBarraProgreso(request, "UPLOADING", 5);

		// Si no se encuentra el fichero de comisiones devolvemos not found
		if ("".equals(rutaFichero)) {			
			
			actualizarBarraProgreso(request, Constants.ESTADO_AJAX_DONE, 100);
			
			logger.info("CargaComisionesManager - cargaFichero  - end");

			return FICHERO_NOT_FOUND;
		} else {
			rutaFichero = rutaFichero.toUpperCase().replace(".TXT", ".ZIP");
			String[] parts = rutaFichero.split("/");
			String nombreFichero = parts[parts.length-1];
			
			// Si el fichero ya se encuentra cargado
			if (importacionComisionesUnificadoManager.esFicheroYaImportado(nombreFichero)) {
				actualizarBarraProgreso(request, Constants.ESTADO_AJAX_DONE, 100);
				
				logger.info("CargaComisionesManager - cargaFichero  - end");

				return FICHERO_CARGADO;
			}

			actualizarBarraProgreso(request, "UPLOADING", 7);
			
			String baseUrl = "/" + FilenameUtils.getPath(rutaFichero);
			String fileName = FilenameUtils.getBaseName(rutaFichero) + "." + FilenameUtils.getExtension(rutaFichero);
			
			// Obtenemos el fichero
			File file = obtenerFichero(fileName, baseUrl);
			
			actualizarBarraProgreso(request, "UPLOADING", 9);
			
			if (null!=file) {
				Map<String, Long> mapaIdAndError;				
				// Procesamos el fichero
				try {
					mapaIdAndError = importacionComisionesUnificadoManager.procesaFichero(file, nombreFichero, null, 'U', usuario, request);
					
				} catch (Exception e) {
					logger.error(" Se ha producido un error al procesar el fichero");
					return ERROR;
				} finally {
					try {
						Files.delete(Paths.get(file.getPath()));
					} catch (IOException e) {
						logger.error(" Se ha producido un error al procesar el fichero");
					}
				}
				
				actualizarBarraProgreso(request, "UPLOADING", 90);				
				logger.info("CargaComisionesManager - cargaFichero  - end");

				return mapaIdAndError.get(Constants.CLAVE_ID_FICHERO).intValue();
			}
		}		
		return ERROR;
	}

	@Override
	public String obtenerNombreFichero() throws BusinessException {

		logger.info("CargaComisionesManager - obtenerNombreFichero  - init");

		String nombreFichero = "";
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		
		// Recorremos los ficheros obtenidos y buscamos en su contenido la etiqueta
		// 'RGASTOSENT' y mes/anho ejecucion segun la regexp
		String MES[] = { "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre" };
		
		String pattern = String.format(RGASTOSENT_S_S$, MES[month == 0 ? 11 : month - 1], month == 0 ? year - 1 : year);
		
		String passBuzonInfovia = this.importacionComisionesUnificadoDao.getPasswordBuzonInfovia();
		String urlFtp = bundle.getString("urlFtpInicio") + passBuzonInfovia + bundle.getString("urlFtpFin");
		logger.info("CargaComisionesManager - obtenerNombreFichero  - urlFtp" + urlFtp); 

		try {
			Boolean ftps_flag = Boolean.valueOf(
					((ConfigAgp) this.importacionComisionesUnificadoDao.get(ConfigAgp.class, "FTPS_ONLINE_FLAG"))
							.getAgpValor());
			FTPDownloader ftpDownloader = new FTPDownloader(ftps_flag);
			nombreFichero = ftpDownloader.getLastFileNameByPattern(pattern, urlFtp);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BusinessException(e);
		}
		
		logger.info("CargaComisionesManager - obtenerNombreFichero  - end");

		return nombreFichero;
	}


	/**
	 * 
	 * @return fichero zip correspondiente al fichero de nombre nombreFichero
	 */
	public File obtenerFichero(String nombreFichero, String directorio) {
		
		logger.info("CargaComisionesManager - obtenerFichero  - init");

		String rutaDirTemp = bundle.getString("directorioLocal");
		
		String passBuzonInfovia = this.importacionComisionesUnificadoDao.getPasswordBuzonInfovia();
		String urlFtp = bundle.getString("urlFtpInicio") + passBuzonInfovia + bundle.getString("urlFtpFin");
				
		if (descarga(nombreFichero, rutaDirTemp, directorio, urlFtp)) {
			return new File(rutaDirTemp + nombreFichero);
		}
		
		logger.info("CargaComisionesManager - obtenerFichero  - end");

		return null;
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
	 * @throws FTPException
	 * @throws FTPIllegalReplyException
	 * @throws IllegalStateException
	 * 
	 */
	private boolean descarga(String filename, String rutaDirTemp, String rutaOrigen, String urlFtp) {		
		logger.info("CargaComisionesManager - descarga  - init");
		try {
			Boolean ftps_flag = Boolean.valueOf(
					((ConfigAgp) this.importacionComisionesUnificadoDao.get(ConfigAgp.class, "FTPS_ONLINE_FLAG"))
							.getAgpValor());
			FTPDownloader ftpDownloader = new FTPDownloader(ftps_flag);
			ftpDownloader.download(urlFtp, rutaOrigen.concat(filename), rutaDirTemp + filename);
			logger.info(filename + " descargado");			
		} catch (Exception e) {			
			logger.error(e.getMessage());
			return false;
		}
		logger.info("CargaComisionesManager - descarga  - end");
		return true;
	}

	/**
	 * 
	 * @param request
	 * @param estado
	 * @param valor
	 */
	public void actualizarBarraProgreso(HttpServletRequest request, String estado, int valor) {
		
		// Solo si venimos del online
		if (null!=request) {
			request.getSession().setAttribute("progressStatus", estado);
			request.getSession().setAttribute("progress", valor);
		}
	}

	public IImportacionComisionesUnificadoManager getImportacionComisionesUnificadoManager() {
		return importacionComisionesUnificadoManager;
	}

	public void setImportacionComisionesUnificadoManager(
			IImportacionComisionesUnificadoManager importacionComisionesUnificadoManager) {
		this.importacionComisionesUnificadoManager = importacionComisionesUnificadoManager;
	}

	public IImportacionComisionesUnificadoDao getImportacionComisionesUnificadoDao() {
		return importacionComisionesUnificadoDao;
	}

	public void setImportacionComisionesUnificadoDao(IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao) {
		this.importacionComisionesUnificadoDao = importacionComisionesUnificadoDao;
	}
}