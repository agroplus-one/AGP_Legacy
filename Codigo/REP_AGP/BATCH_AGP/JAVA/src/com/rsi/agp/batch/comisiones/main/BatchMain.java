package com.rsi.agp.batch.comisiones.main;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class BatchMain {
	private static final Logger logger = Logger.getLogger(BatchMain.class);

	public static void main(String[] args) {

		ImportacionFicherosComisionesBatch ifcb = new ImportacionFicherosComisionesBatch();

		try {
			// Descargamos los ficheros
			BasicConfigurator.configure();
			logger.debug("##--------------------------------##");
			logger.debug("      INICIO Batch COMISIONES");
			logger.debug("##--------------------------------##");
			String fechaPlanif = "";
            if (args.length != 0) {
                fechaPlanif = args[0];
            }
            logger.info((Object)("CON FECHA DE PLANIFICACION: " + fechaPlanif));
			logger.info("COMENZANDO LA DESCARGA DE FICHEROS");
			ifcb.doImportacion(fechaPlanif);
			logger.info("FIN");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error en el proceso batch de comisiones", e);
			System.exit(1);
		}
	}
}