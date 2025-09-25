package com.rsi.agp.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author U028893   T-systems
 * 
 * Clase con utilidaes basicas para trabajar con ficheros zip.
 * 
 **/

public class ZipUtil {
	
	private static final Log LOGGER = LogFactory.getLog(ZipUtil.class); 
	
	public static File getFirstFileInZip(File zipFile) throws Exception{
		return getFirstFileInZip(zipFile,null);
	}
	
	public static File getFirstFileInZip(File zipFile, String rutaNuevoFichero) throws Exception {
		LOGGER.debug(
				"Entrando a getFirsFileInZip con zipFile: " + zipFile.getName() + " nuevoFichero:" + rutaNuevoFichero);
		File temp = null;
		try (ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile))) {
			// Nos movemos a la primera entrada
			in.getNextEntry();
			LOGGER.debug("Estamos en la primera entrada del zip");
			// Creamos un temporal
			if (rutaNuevoFichero == null || rutaNuevoFichero.equals("")) {
				temp = File.createTempFile("ficheroTemporalZip", "tmp");
			} else {
				temp = new File(rutaNuevoFichero);
			}
			LOGGER.debug("Creado el fichero");
			try (OutputStream out = new FileOutputStream(temp)) {
				// Copiamos el zip al fichero
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
		} catch (ZipException e) {
			LOGGER.error("getFirsFileInZip" + e.getMessage());
			throw new Exception("Se ha producido un error al descomprimir el fichero zip", e);
		} catch (IOException e) {
			LOGGER.error("getFirsFileInZip" + e.getMessage());
			throw new Exception("Se ha producido un error al procesar el fichero zip", e);
		}
		LOGGER.debug("Saliendo");
		return temp;
	}

	public static File getFileFromZip(File zipFile, String nameToExtrac) throws Exception {
		File outFile = null;
		try (ZipFile myZip = new ZipFile(zipFile)) {
			ZipEntry entry = myZip.getEntry(nameToExtrac);			
			try (InputStream in = myZip.getInputStream(entry); OutputStream out = new FileOutputStream(outFile)) {
				outFile = new File(nameToExtrac);
				// Copiamos el zip al fichero
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
		} catch (ZipException e) {
			LOGGER.error("getFileFromZip" + e.getMessage());
			throw new Exception("Se ha producido un error al descomprimir el fichero zip", e);
		} catch (IOException e2) {
			LOGGER.error("getFileFromZip" + e2.getMessage());
			throw new Exception("Se ha producido un error al procesar el fichero zip", e2);
		}
		return outFile;
	}
	
	/**
	 * Descomprime el fichero ZIP indicado
	 *
	 * @param zipname Fichero a descomprimir
	 * @param ruta Ruta donde se encuentra el fichero
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void uncompressFile(String zipname, String ruta) throws Exception {
		try (ZipFile zipFile = new ZipFile(ruta + "/" + zipname)) {
			Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				LOGGER.debug("Unzipping: " + zipEntry.getName());
				int size;
				byte[] buffer = new byte[2048];
				try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(ruta + "/" + zipEntry.getName()), buffer.length)) {
					while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
						bos.write(buffer, 0, size);
					}
					bos.flush();
				}
			}
		} catch (Exception e) {
			LOGGER.fatal("Error al descomprimir el fichero " + zipname, e);
			throw e;
		}
	}
  
	/**
	 * Descomprime el "fichero" indicado del fichero "zipname"
	 *
	 * @param zipname Nombre del fichero zip
	 * @param ruta Ruta donde se encuentra el zip
	 * @param fichero Fichero contenido en el zip a descomprimir
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void uncompressFile(String zipname, String ruta, String fichero) throws Exception {
		try (ZipFile zipFile = new ZipFile(ruta + "/" + zipname)) {
			Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				if (fichero.trim().equals(zipEntry.getName())) {
					LOGGER.debug("Unzipping: " + zipEntry.getName());
					int size;
					byte[] buffer = new byte[2048];
					try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
							BufferedOutputStream bos = new BufferedOutputStream(
									new FileOutputStream(ruta + "/" + zipEntry.getName()), buffer.length)) {
						while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
							bos.write(buffer, 0, size);
						}
						bos.flush();
					}
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.fatal("Error al descomprimir el fichero " + fichero, e);
			throw e;
		}
	}	
}