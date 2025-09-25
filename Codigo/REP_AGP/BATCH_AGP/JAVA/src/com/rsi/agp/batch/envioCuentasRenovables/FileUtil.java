package com.rsi.agp.batch.envioCuentasRenovables;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class FileUtil {
	
	private static final Logger logger = Logger.getLogger(FileUtil.class);

	// Atributos de la clase para manejar el zip
	private static FileOutputStream fout;
	private static ZipOutputStream zout;

	/**
	 * Abre el ZipOutputStream
	 *
	 * @param outfilename
	 * @throws FileNotFoundException 
	 * @throws Exception
	 *             Para que la recoja Oracle
	 */
	public static void abrirZip(String outfilename) throws FileNotFoundException {
		fout = new FileOutputStream(outfilename);
		zout = new ZipOutputStream(fout);
	}

	/**
	 * Cierra el ZipOutputStream
	 * @throws IOException 
	 *
	 * @throws Exception
	 *             Para que la recoja Oracle
	 */
	public static void cerrarZip() throws IOException {
		zout.close();
	}

	/**
	 * Descomprime el fichero indicado
	 *
	 * @param zipname
	 * @param ruta
	 * @throws IOException 
	 * @throws Exception
	 *             Para que la recoja Oracle
	 */
	public static void uncompressFile(String zipname, String ruta) throws IOException {
		try (ZipFile zipFile = new ZipFile(ruta + "/" + zipname)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();
				byte[] buffer = new byte[2048];
				try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(ruta + "/" + zipEntry.getName()), buffer.length)) {
					int size;
					while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
						bos.write(buffer, 0, size);
					}
					bos.flush();
				}
			}
		}
	}

	/**
	 * Descomprime el "fichero" indicado del fichero "zipname"
	 *
	 * @param zipname
	 * @param ruta
	 * @param fichero
	 * @throws IOException 
	 * @throws Exception
	 *             Para que la recoja Oracle
	 */
	public static void uncompressFile(String zipname, String ruta, String fichero) throws IOException {
		try (ZipFile zipFile = new ZipFile(ruta + "/" + zipname)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				if (fichero.equals(zipEntry.getName())) {
					byte[] buffer = new byte[2048];
					try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
							BufferedOutputStream bos = new BufferedOutputStream(
									new FileOutputStream(ruta + "/" + zipEntry.getName()), buffer.length)) {
						int size;
						while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
							bos.write(buffer, 0, size);
						}
						bos.flush();
					}
					break;
				}
			}
		}
	}

	/**
	 * Comprime el fichero indicado
	 *
	 * @param infilename
	 * @throws IOException 
	 * @throws Exception
	 *             Para que la recoja Oracle
	 */
	public static void compressFile(String infilename) throws IOException {
		File fileInput = new File(infilename);
		ZipEntry ze = new ZipEntry(fileInput.getName());
		try (FileInputStream fin = new FileInputStream("144999P2015415.xml")) {
			zout.putNextEntry(ze);
			copy(fin, zout);
		}
		try (FileInputStream fin = new FileInputStream("244999P2015415.xml")) {
			zout.putNextEntry(ze);
			copy(fin, zout);
		}
		zout.closeEntry();		
	}

	/**
	 * Comprime un directorio entero
	 *
	 * @param dir2zip
	 * @param zos
	 */
	static void compressDir(String dir2zip, ZipOutputStream zos) throws Exception {
		// create a new File object based on the directory we
		// have to zip File
		File zipDir = new File(dir2zip);
		// get a listing of the directory content
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		// loop through dirList, and zip the files
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(zipDir, dirList[i]);
			if (f.isDirectory()) {
				// if the File object is a directory, call this
				// function again to add its content recursively
				String filePath = f.getPath();
				compressDir(filePath, zos);
				// loop again
				continue;
			}
			// if we reached here, the File object f was not
			// a directory
			// create a FileInputStream on top of f
			try (FileInputStream fis = new FileInputStream(f)) {
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(f.getPath());
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
			}
		}
	}

	/**
	 * Copia un fichero
	 *
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		while (true) {
			int bytesRead = in.read(buffer);
			if (bytesRead == -1)
				break;
			out.write(buffer, 0, bytesRead);
		}
	}

	/**
	 * Borra un fichero en la ruta indicada
	 *
	 * @param ruta
	 */
	public static void borraXMLs(String ruta) {
		File dir = new File(ruta);
		dir.delete();
	}

	public static void main(String[] args) {

		String zipname = "14GF0542.ZIP";
		String ruta = "/aplicaciones/AGP_AGROPLUS/INTERFACES";

		try {
			uncompressFile(zipname, ruta);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Comprime multiples archivos en un Zip
	 *
	 * @param dir2zip
	 * @param zos
	 * @throws IOException 
	 */
	protected static void compressMultipleFiles(List<String> lstArchivos, String rutaOrigenFich, String ficheroDestino)
			throws IOException {
		try (FileOutputStream dest = new FileOutputStream(rutaOrigenFich + ficheroDestino + ".ZIP");
				ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
			// Create a byte[] buffer that we will read data
			// from the source
			// files into and then transfer it to the zip file
			byte[] data = new byte[4096];
			// Iterate over all of the files in our list
			for (String archivo : lstArchivos) {
				// Get a BufferedInputStream that we can use to read the
				// source file
				try (FileInputStream fi = new FileInputStream(rutaOrigenFich + archivo);
						BufferedInputStream origin = new BufferedInputStream(fi, 4096)) {
					// Setup the entry in the zip file
					ZipEntry entry = new ZipEntry(archivo);
					out.putNextEntry(entry);
					// Read data from the source file and write it out to the zip file
					int count;
					while ((count = origin.read(data, 0, 4096)) != -1) {
						out.write(data, 0, count);
					}
				}
			}
		}
	}

	public static void copyFile(String sourceFile, String destFile, List<String> lstFicherosEnvio) throws IOException {
		for (String nombreArchivo : lstFicherosEnvio) {
			File dirOrigen = new File(sourceFile + nombreArchivo);
			File dirDestino = new File(destFile + nombreArchivo);
			if (!dirDestino.exists()) {
				dirDestino.createNewFile();
			}
			FileChannel origen = null;
			FileChannel destino = null;
			try (FileInputStream fis = new FileInputStream(dirOrigen);
					FileOutputStream fos = new FileOutputStream(dirDestino)) {
				try {
					origen = fis.getChannel();
					destino = fos.getChannel();
					long count = 0;
					long size = origen.size();
					while ((count += destino.transferFrom(origen, count, size - count)) < size);
				} finally {
					if (origen != null)
						origen.close();
					if (destino != null)
						destino.close();
				}
			}
		}
	}

	/**
	 * Borra los ficheros de un directorio que existan en la lista lstFicherosEnvio
	 * pasada por parametro
	 * 
	 * @param lstFicherosEnvio
	 *            lista de ficheros a borrar
	 * @throws IOException
	 */
	public static void deleteFiles(File folder, List<String> lstFicherosEnvio) throws IOException {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (!f.isDirectory()) {
					if (lstFicherosEnvio.contains(f.getName()) && !f.delete())
						throw new FileNotFoundException("Failed to delete file: " + f);
				}
			}
		}
	}
}
