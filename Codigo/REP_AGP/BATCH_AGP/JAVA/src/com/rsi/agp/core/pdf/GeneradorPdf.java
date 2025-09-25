package com.rsi.agp.core.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.rsi.agp.core.webapp.util.StringUtils;

public class GeneradorPdf {

	private static boolean keepPath = true;
	private static boolean debug = false;

	private static String ORIGEN_STR = "origen";
	private static String DESTINO_STR = "destino";

	private static List<String> extensiones = new ArrayList<String>(Arrays.asList("java", "jsp", "js", "css", "xml",
			"properties", "mxml", "as", "ttf", "jrxml", "xsd", "wsdl", "tld", "MF"));

	/**
	 * Metodo principal de la clase generadora de pdfs. EJEMPLO DE LLAMADA: java
	 * -jar GeneradorPdf.jar -origen "D:/temp/AGP_PRODUCCION/REP_AGP" -destino
	 * "D:/temp/AGP_PRODUCCION/PDF"
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLine cmdLine = null;
		CommandLineParser parser = null;

		String rutaOrigen = "";
		String rutaDestino = "";

		Options options = new Options();
		options.addOption(ORIGEN_STR, true, "Ruta de origen de los ficheros a convertir");
		options.addOption(DESTINO_STR, true, "Ruta destino donde se dejarán los PDFs");

		try {
			GregorianCalendar fechaInicio = new GregorianCalendar();
			parser = new BasicParser();
			cmdLine = parser.parse(options, args);

			if (cmdLine.hasOption(ORIGEN_STR)) {
				rutaOrigen = cmdLine.getOptionValue(ORIGEN_STR);
			}

			if (cmdLine.hasOption(DESTINO_STR)) {
				rutaDestino = cmdLine.getOptionValue(DESTINO_STR);
			}

			if (!StringUtils.nullToString(rutaOrigen).equals("") && !StringUtils.nullToString(rutaDestino).equals("")) {
				GeneradorPdf.doWork(rutaOrigen, rutaOrigen, rutaDestino);
			} else {
				System.out.println("No se recibieron los parametros necesarios");
			}

			GregorianCalendar fechaFin = new GregorianCalendar();
			Long tiempo = fechaFin.getTimeInMillis() - fechaInicio.getTimeInMillis();
			System.out.println("Duracion de la generacion: " + (tiempo / 1000) + " segundos.");
		} catch (org.apache.commons.cli.ParseException ex) {
			ex.printStackTrace();
			new HelpFormatter().printHelp(GeneradorPdf.class.getName(), options); // Error, imprimimos la ayuda
			System.exit(1);
		} catch (Exception ex) {
			ex.printStackTrace();
			new HelpFormatter().printHelp(GeneradorPdf.class.getName(), options); // Error, imprimimos la ayuda
			System.exit(1);
		}

	}

	/**
	 * Metodo para leer los ficheros de rutaOrigen, convertirlos y dejarlos en
	 * rutaDestino
	 * 
	 * @param rutaOrigenFicheros
	 *            Carpeta origen de los ficheros
	 * @param rutaOrigen
	 *            Carpeta origen del proceso
	 * @param rutaDestino
	 *            Carpeta destino de los ficheros
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static void doWork(String rutaOrigenFicheros, String rutaOrigen, String rutaDestino) throws Exception {
		File dir = new File(rutaOrigenFicheros);
		File[] ficheros = dir.listFiles();

		for (final File fichero : ficheros) {
			// Obtenemos la ruta de destino
			String rutaNueva = "";
			if (keepPath) {
				rutaNueva = fichero.getAbsolutePath();
				rutaNueva = rutaNueva.replace("\\", File.separator).replaceFirst(rutaOrigen, rutaDestino);
				// Si se trata de un fichero, quitamos el nombre del fichero de la ruta.
				if (fichero.isFile())
					rutaNueva = rutaNueva.substring(0, rutaNueva.lastIndexOf(File.separator));
				if (debug)
					System.out.println("Nueva ruta: " + rutaNueva);
			} else {
				rutaNueva = rutaDestino;
			}

			if (fichero.isDirectory()) {
				// Si es un directorio, creamos el directorio destino nuevo y llamamos al
				// proceso recursivamente
				File directorioDestino = new File(rutaNueva);
				directorioDestino.mkdir();
				doWork(fichero.getAbsolutePath().replace("\\", File.separator), rutaOrigen, rutaDestino);
			} else {
				if (debug)
					System.out.println("Fichero en la ruta " + rutaOrigenFicheros + ": " + fichero.getName());
				// Si es un fichero y es una de las extensiones permitidas, generamos el pdf en
				// el directorio de destino
				String nombreFichero = fichero.getName();
				if (nombreFichero.lastIndexOf(".") > 0
						&& extensiones.contains(nombreFichero.substring(nombreFichero.lastIndexOf(".") + 1))) {
					try {
						convertirPdf(fichero, new File(rutaNueva + File.separator + fichero.getName() + ".pdf"));
					} catch (Exception e) {
						System.out.println("Error al convertir el fichero " + nombreFichero + ". " + e.getMessage());
						throw e;
					}
				}
			}
		}
		if (debug)
			System.out.println("Proceso finalizado");
	}

	private static void convertirPdf(File ficheroOrigen, File ficheroDestino) throws DocumentException, IOException {
		OutputStream file = null;
		if (debug)
			System.out.println("Convirtiendo " + ficheroOrigen.getAbsolutePath() + " al fichero "
					+ ficheroDestino.getAbsolutePath());
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(ficheroOrigen))) {			
			// Leemos el contenido del fichero origen y lo volcamos en una variable de tipo String
			String linea = "";
			StringBuilder total = new StringBuilder();
			while ((linea = bufferedReader.readLine()) != null) {
				total.append(linea);
				total.append("\n");
			}
			// Escribimos el contenido de la cadena en el fichero de destino
			file = new FileOutputStream(ficheroDestino);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			document.add(new Paragraph(total.toString()));
			document.close();
		} finally {
			file.close();
		}
	}

}
