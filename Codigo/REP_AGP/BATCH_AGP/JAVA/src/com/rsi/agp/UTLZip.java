package com.rsi.agp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Utilidades de Compresion
 *
 * @author T-Systems
 *
 */
public class UTLZip {

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
	 * 
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
				readZipfile(zipFile.getInputStream(zipEntry), zipEntry, ruta);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw e;
		} 
	}
	
	private static void readZipfile(InputStream is, final ZipEntry ze, final String ruta) throws IOException {
		int size;
		byte[] buffer = new byte[2048];
		try (BufferedInputStream bis = new BufferedInputStream(is);
				FileOutputStream fos = new FileOutputStream(ruta + "/" + ze.getName());
				BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				bos.write(buffer, 0, size);
			}
			bos.flush();
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
				ZipEntry zipEntry = enumeration.nextElement();
				if (fichero.equals(zipEntry.getName())) {
					readZipfile(zipFile.getInputStream(zipEntry), zipEntry, ruta);					
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw e;
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
		try {
			File fileInput = new File(infilename);
			// Si el fichero de entrada es un directorio, se llama a compressDir
			if (fileInput.isDirectory()) {
				UTLZip.compressDir(fileInput.getName(), zout);
			} else {
				ZipEntry ze = new ZipEntry(fileInput.getName());
				zout.putNextEntry(ze);
				try (FileInputStream fin = new FileInputStream(infilename)) {					
					copy(fin, zout);
				}
				zout.closeEntry();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw e;
		} 
	}

	/**
	 * Comprime un directorio entero
	 *
	 * @param dir2zip
	 * @param zos
	 * @throws IOException
	 */
	private static void compressDir(String dir2zip, ZipOutputStream zos) throws IOException {
		try {
			// create a new File object based on the directory we
			// have to zip File
			File zipDir = new File(dir2zip);
			// get a listing of the directory content
			String[] dirList = zipDir.list();
			
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
				compressFile(f, zos);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw e;
		} 
	}
	
	private static void compressFile(final File f, final ZipOutputStream zos) throws IOException {
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
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
		boolean result = dir.delete();
		System.out.println("delete result: " + result);		
	}

	public static void main(String[] args) {
		String zipname = "14GF0542.ZIP";
		String ruta = "/aplicaciones/AGP_AGROPLUS/INTERFACES";

		try {
			uncompressFile(zipname, ruta);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
