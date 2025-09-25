package com.rsi.agp.batch;

import java.io.BufferedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;

public class ComprobadorEstadoPoliza {
	
	private static final String REFPOLIZA_STR = "refPoliza";
	private static final String DIRACUSE_STR = "dirAcuseRecibo";
	private static final String NOMACUSE_STR = "nomAcuseRecibo";
	
	private static final Log logger = LogFactory.getLog(ComprobadorEstadoPoliza.class);

	public static void main(String[] args) throws Exception {

		String refPoliza = null;
		String dirAcuseRecibo = null;
		String nomAcuseRecibo = null;

		BufferedInputStream bis = null;
		CommandLineParser parser = null;
		CommandLine cmdLine = null;
		
		try (ZipFile zipFile = new ZipFile(dirAcuseRecibo + "/" + nomAcuseRecibo)) {
			Options options = new Options();
			options.addOption(REFPOLIZA_STR, true, "Referencia de la poliza a tratar (obligatorio)");
			options.addOption(DIRACUSE_STR, true,
					"Directorio en el que se ha descargado el ZIP con el acuse de recibo de la poliza (obligatorio)");
			options.addOption(NOMACUSE_STR, true,
					"Nombre del ZIP que contiene el acuse de recibo de la poliza (obligatorio)");

			parser = new BasicParser();
			cmdLine = parser.parse(options, args);

			if (cmdLine.hasOption(REFPOLIZA_STR))
				refPoliza = cmdLine.getOptionValue(REFPOLIZA_STR);

			if (cmdLine.hasOption(DIRACUSE_STR))
				dirAcuseRecibo = cmdLine.getOptionValue(DIRACUSE_STR);

			if (cmdLine.hasOption(NOMACUSE_STR))
				nomAcuseRecibo = cmdLine.getOptionValue(NOMACUSE_STR);

			if (refPoliza == null)
				throw new org.apache.commons.cli.ParseException(
						"La referencia de la poliza a tratar (-refPoliza <arg>) es requerida");

			if (dirAcuseRecibo == null)
				throw new org.apache.commons.cli.ParseException(
						"El directorio con el acuse de recibo (-dirAcuseRecibo <arg>) es requerido");

			if (nomAcuseRecibo == null)
				throw new org.apache.commons.cli.ParseException(
						"El nombre del ZIP con el acuse de recibo (-nomAcuseRecibo <arg>) es requerido");
			
			ZipEntry zipEntry = zipFile.getEntry("AcuseRecibo.xml");

			if (zipEntry == null)
				throw new IllegalArgumentException("No se ha encontrado AcuseRecibo.xml");

			bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));

			AcuseReciboDocument acuseReciboDocument = AcuseReciboDocument.Factory.parse(bis);

			for (int i = 0; i < acuseReciboDocument.getAcuseRecibo().getDocumentoArray().length; i++) {
				Documento documento = acuseReciboDocument.getAcuseRecibo().getDocumentoArray(i);

				String refPolizaXML = documento.getId().substring(0, 7);

				if (refPoliza.equals(refPolizaXML)) {
					logger.info(documento.getEstado());
					return;
				}

			}

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Ha ocurrido un error durante el proceso de la poliza con referencia " + refPoliza);
		} finally {
			if (bis != null) bis.close();
		}
	}
}
