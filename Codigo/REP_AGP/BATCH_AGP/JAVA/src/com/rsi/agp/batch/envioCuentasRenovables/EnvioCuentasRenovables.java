package com.rsi.agp.batch.envioCuentasRenovables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import es.agroseguro.contratacion.seguroRenovable.gastos.Cuenta;
import es.agroseguro.contratacion.seguroRenovable.gastos.GastosSeguroRenovable;
import es.agroseguro.contratacion.seguroRenovable.gastos.GastosSeguroRenovableDocument;

public class EnvioCuentasRenovables {
	private static final Logger logger = Logger.getLogger(EnvioCuentasRenovables.class);
	// Atributos de la clase para manejar el zip
	static ResourceBundle bundle = ResourceBundle.getBundle("envioCuentasRenovables");
	static String rutaOrigenFich = bundle.getString("ruta.origen.envio");
	static String rutaDestinoFich = bundle.getString("ruta.destino.envio");

	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("##-----------------------------------------------##");
			logger.debug("INICIO Batch ENVIO CUENTAS DE POLIZAS RENOVABLES");
			logger.debug("##-----------------------------------------------##");
			doWork();
			logger.debug("FIN Batch ENVIO CUENTAS DE POLIZAS RENOVABLES");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error en el proceso de Envio Cuentas de Polizas Renovables", e);
			System.exit(1);
		}
	}

	@SuppressWarnings("unchecked")
	private static void doWork() throws Exception {
		List<com.rsi.agp.dao.tables.renovables.PolizaRenovable> renovables;
		String archivo = "";
		SessionFactory factory;
		Session session = null;
		List<String> lstFicherosEnvio = new ArrayList<String>();
		List<String> lstArchivos = new ArrayList<String>();
		// RQ.04
		List<Long> lstIdsPoliza = new ArrayList<Long>();
		
		// 1 - Buscamos el plan actual
		Calendar c2 = new GregorianCalendar();
		int planActual = c2.get(Calendar.YEAR);
		factory = getSessionFactory();
		session = factory.openSession();
		
		try {
			
			for(int anio=planActual;anio>planActual-2;anio--) {
				lstArchivos.clear();
				logger.debug("## Año " + anio + " ##");

				List<String> lstPlzRenov = new ArrayList<String>();

				// 2 - Cargamos las pólizas renovables en estado envío IBAN A 2 - Preparado
				Long estadoIBAN = Long.valueOf(EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_PREPARADO);
				Criteria crit = session.createCriteria(com.rsi.agp.dao.tables.renovables.PolizaRenovable.class);
				crit.createAlias("polizaRenovableEstadoEnvioIBAN", "polizaRenovableEstadoEnvioIBAN");
				crit.add(Restrictions.eq("polizaRenovableEstadoEnvioIBAN.codigo", estadoIBAN));
				crit.add(Restrictions.eq("plan", Long.valueOf(anio)));
				renovables = (List<com.rsi.agp.dao.tables.renovables.PolizaRenovable>) crit.list();
				if (renovables.size() == 0) {
					logger.info("### NO HAY POLIZAS RENOVABLES EN BBDD DEL PLAN " + anio
							+ " CON ESTADO IBAN '2 -PREPARADO' " + " ###");
				} else {
					logger.info("### TOTAL POLIZAS " + anio + " PARA ENVIAR :" + renovables.size() + " ###");
					for (com.rsi.agp.dao.tables.renovables.PolizaRenovable polRen : renovables) {
						logger.debug("#-----------------------------------------#");
						logger.debug("TRATANDO POLIZA: ID: " + polRen.getId() + " REF: " + polRen.getReferencia());

						// 3 - Actualizamos las polizas renovables con estado IBAN 2-Preprado a
						// 3-Enviado
						BBDDEnvioCuentasRenovables.actualizaEstadoEnvioIBAN(session, anio,
								EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_ENVIADO);

						// RQ.04
						lstIdsPoliza.add(polRen.getId());

						// 4 - Insertamos en el histórico para cada póliza renovable
						BBDDEnvioCuentasRenovables.actualizarHistorico(polRen.getId(),
								EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_ENVIADO, session);
						logger.debug("Histórico envio Iban de pol. renovable con id: " + polRen.getId() + " ref: "
								+ polRen.getReferencia() + " insertado");

						// 5 - Creamos el xml de la poliza
						String xml = crearXml(session, polRen);

						// 6- Creamos el fichero C:\Documents and Settings\U028982\Escritorio\POLREN
						archivo = polRen.getReferencia() + polRen.getPlan() + polRen.getLinea();
						crearFicheroXML(archivo, xml);
						lstArchivos.add(archivo + ".xml");
						lstPlzRenov.add(polRen.getId().toString());
					}
					if (!lstArchivos.isEmpty()) {
						String nombreFichero = BBDDEnvioCuentasRenovables.getNombreFichero(Integer.toString(anio),
								session);

						/*
						 * -----------------------------------------------------------------------------
						 * ----------------------------------------------
						 * -----------------------------------------------------------------------------
						 * ---------------------------------------------- -- El nombre de los ficheros
						 * sería de 8 caracteres, entre los rangos 0..9 y A..Z, -- excluyendo aquellos
						 * que se consideran especiales -- Ejemplo:
						 * GS60216A.TXT -- GS => Prefijo usado para gastos -- 6 => Año -- 02 => Mes --
						 * 16 => Dia -- 1 => Primer envío. Este numero lo vamos cambiando en función de
						 * si tenemos que realizar reenvios -- -- El nombre del fichero TXT seguirá la
						 * nomenclatura utilizada para el envíos por lotes de -- los ficheros de
						 * contratación de pólizas, utilizando el prefijo "GS" -- El contenido del
						 * fichero TXT se regirá por la siguiente máscara: -- RURALGSUPPPPNNNNNNNNN --
						 * RURAL: Código asignado por Agroseguro a RGA. -- GS: Indicador para los gastos
						 * de seguros renovables. -- U: Formato del texto: XML encoding UTF-8. -- PPPP:
						 * Campañía. En este caso se corresponde con el plan. -- NNNNNNNNN: Número de
						 * pólizas (o ficheros XML) incluidas en el envío formateado a 9 dígitos,
						 * rellenando por la izquierda con ceros.
						 */

						// 7- Creamos el txt fichero cabecera C:\Documents and
						// Settings\U028982\Escritorio\POLREN
						crearFicheroCabecera(anio, lstArchivos.size(), nombreFichero);
						lstFicherosEnvio.add(nombreFichero + ".TXT");

						// 8 - Comprimimos todos los xml en un .zip
						FileUtil.compressMultipleFiles(lstArchivos, rutaOrigenFich, nombreFichero);
						lstFicherosEnvio.add(nombreFichero + ".ZIP");

						// 9 - Envio a Agroseguro del TXT y ZIP generado
						EnvioCuentasRenAgroseguro.main(session, lstPlzRenov);

						// 10 - Insertar en tb_comunicaciones un registro del envio

						// 11 - Se copia el fichero .zip a la ruta
						// /aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH
						File dirOrigen = new File(rutaOrigenFich);
						FileUtil.copyFile(rutaOrigenFich, rutaDestinoFich, lstFicherosEnvio);
						logger.debug("## Archivos zip y txt copiados a " + rutaDestinoFich);

						// 12 - Eliminar ficheros.xml del directorio temporal
						FileUtil.deleteFiles(dirOrigen, lstArchivos);
						logger.debug("## Archivos xml borrados del temporal " + dirOrigen);

					} else {
						logger.debug("No hay archivos para mandar a Agroseguro");
					}
				}
			}

		} catch (Throwable ex) {

			// RQ.04
			for (Long idPoliza : lstIdsPoliza) {
				logger.debug("Número de polizas para hacer rollback: " + lstIdsPoliza.size());
				//BBDDEnvioCuentasRenovables.actualizaEstadoById(idPoliza, EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_PREPARADO, ex.getMessage(), session);
				logger.debug("Realizando rollback...");
				logger.debug("Cambiando estado poliza renovable a PREPARADO 2...");
				BBDDEnvioCuentasRenovables.rollbackEstadoEnvioIBAN(session, planActual, EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_PREPARADO);
			}

			throw new Exception();

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private static SessionFactory getSessionFactory() {
		SessionFactory factory;
		try {
			Configuration cfg = new Configuration();
			cfg.configure();

			factory = cfg.buildSessionFactory();

		} catch (Throwable ex) {

			logger.error("# Error al crear el objeto SessionFactory.", ex);
			throw new ExceptionInInitializerError(ex);
		}
		return factory;
	}

	/*
	 * Metodo que realiza el xml de Envio Cuenta para Agroseguro
	 * 
	 * @param polRen poliza renovable
	 */
	private static String crearXml(Session session, com.rsi.agp.dao.tables.renovables.PolizaRenovable polRen) {
		GastosSeguroRenovableDocument gasDoc = GastosSeguroRenovableDocument.Factory.newInstance();
		GastosSeguroRenovable gasSeguro = GastosSeguroRenovable.Factory.newInstance();
		gasDoc.setGastosSeguroRenovable(gasSeguro);
		try {
			gasSeguro.setReferencia(polRen.getReferencia());
			gasSeguro.setDigitoControl(Integer.parseInt(polRen.getDc().toString()));
			gasSeguro.setPlan(polRen.getPlan().intValue());

			String iban = BBDDEnvioCuentasRenovables.recogerCuentaAsegurado(session, polRen.getId().toString(),
					polRen.getLinea().toString());
			if (iban.equals("")) {
				iban = BBDDEnvioCuentasRenovables.recogerCuentaAsegurado(session, polRen.getId().toString(), "999");
			}

			if (!iban.equals("")) {
				Cuenta cuenta = Cuenta.Factory.newInstance();
				cuenta.setIban(iban);

				logger.debug("Recuperamos los datos del Destinatario");
				Cuenta cuentaAux = BBDDEnvioCuentasRenovables.recogerDestinatarioAseg(session,
						polRen.getId().toString(), polRen.getLinea().toString());

				if (cuentaAux.getDestinatario() == null) {
					cuentaAux = BBDDEnvioCuentasRenovables.recogerDestinatarioAseg(session, polRen.getId().toString(),
							"999");
				}

				cuenta.setDestinatario(cuentaAux.getDestinatario());
				logger.debug("Destinatario Recuperado: " + cuenta.getDestinatario());

				String titularAux = cuentaAux.getTitular();

				if (cuentaAux.getTitular() != null) {

					// Convertimos a formato "UTF-8" el titular de la cuenta por si viene con
					// algún caracter extraño como la "nh".

					String titular = new String(titularAux.getBytes("UTF-8"));
					cuenta.setTitular(titular);
					logger.debug("**@@** Valor de cuenta.getTitular: " + cuenta.getTitular());
				} else {
					cuenta.setTitular(cuentaAux.getTitular());
				}

				logger.debug("Titular Recuperado: " + cuenta.getTitular());
				// titularCuenta no lo meto al no estar en la póliza renovable
				gasSeguro.setCuenta(cuenta);
			}
			gasDoc.setGastosSeguroRenovable(gasSeguro);
		} catch (Exception ex) {

			logger.error("# Error al crear el xml de la póliza " + polRen.getReferencia(), ex);
			throw new ExceptionInInitializerError(ex);
		}
		logger.debug("## ---------------------- ##");
		logger.debug("## Xml de poliza con id: " + polRen.getId() + " ref: " + polRen.getReferencia() + ": "
				+ gasDoc.toString());
		return gasDoc.toString();
	}

	private static void crearFicheroXML(String archivo, String xml) throws IOException {
		String ruta = rutaOrigenFich.trim() + archivo + ".xml"; // por properties pasar la ruta
		File file = new File(ruta);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(xml);
		}
	}

	private static void crearFicheroCabecera(int codPlan, int totalPolizas, String nombreFichero) throws IOException {
		String contenido = "RURALGSU" + codPlan + String.format("%09d", totalPolizas);
		String ruta = rutaOrigenFich + nombreFichero + ".TXT";
		File file = new File(ruta);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(contenido);
		}
	}
}