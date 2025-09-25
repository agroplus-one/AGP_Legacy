package com.rsi.agp.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileUtil {

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		try (FileInputStream fis = new FileInputStream(sourceFile);
				FileChannel origen = fis.getChannel();
				FileOutputStream fos = new FileOutputStream(destFile);
				FileChannel destino = fos.getChannel()) {
			long count = 0;
			long size = origen.size();
			while ((count += destino.transferFrom(origen, count, size - count)) < size);
		}
	}

	/**
	 * Borra los ficheros que contiene un directorio
	 * 
	 * @param folder
	 * @throws IOException
	 */
	public static void deleteFiles(File folder) throws IOException {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (!f.isDirectory()) {
					if (!f.delete()) {
						throw new FileNotFoundException("Failed to delete file: " + f);
					}
				}
			}
		}
	}

}
