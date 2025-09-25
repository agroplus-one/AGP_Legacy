package com.rsi.agp.batch.comisiones.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author U028893 T-systems
 * 
 *         Clase con utilidaes basicas para trabajar con ficheros zip.
 * 
 **/

public class ZipUtil {

	private static final Log logger = LogFactory.getLog(ZipUtil.class);

	private ZipUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static File getFirstFileInZip(File zipFile) throws Exception {
		return getFirstFileInZip(zipFile, null);
	}

	public static File getFirstFileInZip(File zipFile, String rutaNuevoFichero) throws Exception {
		logger.info(
				"Entrando a getFirsFileInZip con zipFile: " + zipFile.getName() + " nuevoFichero:" + rutaNuevoFichero);
		File temp = null;
		logger.info("****************" + zipFile);
		try (ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile))) {
			// Nos movemos a la primera entrada
			in.getNextEntry();
			logger.info("Estamos en la primera entrada del zip");
			// Creamos un temporal
			if (rutaNuevoFichero == null || rutaNuevoFichero.equals("")) {
				temp = File.createTempFile("ficheroTemporalZip", "tmp");
			} else {
				temp = new File(rutaNuevoFichero);
			}
			logger.info("Creado el fichero");
			writeFile(in, temp);
		} catch (ZipException e) {
			logger.error("getFirsFileInZip", e);
			throw new Exception("Se ha producido un error al descomprimir el fichero zip", e);
		} catch (IOException e) {
			logger.error("getFirsFileInZip", e);
			throw new Exception("Se ha producido un error al procesar el fichero zip", e);
		}
		logger.info("Saliendo");
		return temp;
	}
	
	private static void writeFile(final InputStream inZip, final File outFile) throws IOException {
		try (OutputStream out = new FileOutputStream(outFile)) {
			// Copiamos el zip al fichero
			byte[] buf = new byte[1024];
			int len;
			while ((len = inZip.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		}
	}

	public static File getFileFromZip(File zipFile, String nameToExtrac) throws Exception {
		File outFile = null;
		try (ZipFile myZip = new ZipFile(zipFile)) {
			ZipEntry entry = myZip.getEntry(nameToExtrac);
			try (InputStream in = myZip.getInputStream(entry)) {
				outFile = new File(nameToExtrac);
				writeFile(in, outFile);
			}
		} catch (ZipException e) {
			logger.error("getFileFromZip", e);
			throw new Exception("Se ha producido un error al descomprimir el fichero zip", e);
		} catch (IOException e2) {
			logger.error("getFileFromZip", e2);
			throw new Exception("Se ha producido un error al procesar el fichero zip", e2);
		}
		return outFile;
	}
}