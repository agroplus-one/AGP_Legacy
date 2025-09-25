package com.rsi.agp.batch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.CLOB;

@SuppressWarnings("deprecation")
public class GeneradorSqlEstadoPoliza {
	
	private static final String KO_ERROR_AL_ACUTALIZAR_EL_ACUSE = "KO - Error al acutalizar el acuse: ";
	private static final String WHERE_IDENVIO = " WHERE IDENVIO = ";
	private static final String SELECT_IDPOLIZA_FROM_O02AGPE0_TB_POLIZAS_WHERE_REFERENCIA = "(SELECT IDPOLIZA FROM o02agpe0.TB_POLIZAS WHERE REFERENCIA = '";
	private static final String AND_IDENVIO = " AND IDENVIO = ";
	
	private static final String CONNECTION_URL = "jdbc:oracle:oci:@OA9P";     // Produccion
	
	private static final String TIPOFICHERO_STR = "tipofichero";
	private static final String DIRACUSE_STR = "dirAcuseRecibo";
	private static final String IDENVIO_STR = "idenvio";
	private static final String NOMACUSE_STR = "nomAcuseRecibo";
	private static final String CODRESPUESTA_STR = "codigoRespuesta";

	public static void main(String[] args) throws Exception{
 
		String tipofichero = null;
		String dirAcuseRecibo = null; 
		String idenvio = null;
		String nomAcuseRecibo = null;
		String codigoRespuesta = null;
		
		CommandLineParser parser = null;  
		CommandLine cmdLine = null;

		Options options = new Options();  
		options.addOption(TIPOFICHERO_STR, true, "Tipo de fichero a tratar: poliza(P)/anexo(R o M)/siniestro(S) (obligatorio)");  
		options.addOption(DIRACUSE_STR, true, "Directorio en el que se ha descargado el ZIP con el acuse de recibo de la poliza (obligatorio)");  
		options.addOption(IDENVIO_STR, true, "Identificador del envio (obligatorio)");
		options.addOption(NOMACUSE_STR, true, "Nombre del ZIP que contiene el acuse de recibo de la poliza (obligatorio)");  
		options.addOption(CODRESPUESTA_STR, true, "Codigo de respuesta de Agroseguro: A - Aceptado, R - Rechazado, X - Ambos");
		
		try{
			parser  = new BasicParser();  
			cmdLine = parser.parse(options, args);
			
			if (cmdLine.hasOption(TIPOFICHERO_STR))
				tipofichero = cmdLine.getOptionValue(TIPOFICHERO_STR);

			if (cmdLine.hasOption(DIRACUSE_STR))
				dirAcuseRecibo = cmdLine.getOptionValue(DIRACUSE_STR);

			if (cmdLine.hasOption(IDENVIO_STR))  
				idenvio = cmdLine.getOptionValue(IDENVIO_STR);    

			if (cmdLine.hasOption(NOMACUSE_STR))
				nomAcuseRecibo = cmdLine.getOptionValue(NOMACUSE_STR);
			
			if (cmdLine.hasOption(CODRESPUESTA_STR))
				codigoRespuesta = cmdLine.getOptionValue(CODRESPUESTA_STR);
			
			if (tipofichero == null)  
			    throw new org.apache.commons.cli.ParseException("El tipo de fichero a tratar (-tipofichero <arg>) es requerido");  
			
			if (dirAcuseRecibo == null)  
			    throw new org.apache.commons.cli.ParseException("El directorio con el acuse de recibo (-dirAcuseRecibo <arg>) es requerido");  

			if (nomAcuseRecibo == null)  
			    throw new org.apache.commons.cli.ParseException("El nombre del ZIP con el acuse de recibo (-nomAcuseRecibo <arg>) es requerido");  
			
			if (idenvio == null)  
			    throw new org.apache.commons.cli.ParseException("El identificador del envio (-idenvio <arg>) es requerido");
			
			if (codigoRespuesta == null)  
			    throw new org.apache.commons.cli.ParseException("El codigo de respuesta de Agroseguro (-codigoRespuesta <arg>) es requerido");

			try (ZipFile zipFile = new ZipFile(dirAcuseRecibo + File.separator + nomAcuseRecibo)) {
				ZipEntry zipEntry = zipFile.getEntry("AcuseRecibo.xml");

				if (zipEntry == null)
					throw new IllegalArgumentException("No se ha encontrado AcuseRecibo.xml");

				try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
					AcuseReciboDocument acuseReciboDocument = AcuseReciboDocument.Factory.parse(bis);

					/*
					 * Estados posibles de la Poliza ## 1 Pendiente Validacion ## 2 Grabacion
					 * Provisional ## 3 Grabacion Definitiva ## 4 Anulada ## 5 Enviada Pendiente de
					 * Confirmar ## 6 Enviada Correcta ## 7 Enviada Erronea ## 8 Definitiva
					 * 
					 * Estados posibles de los anexos y siniestros ## 1 En borrador ## 2 Enviado ##
					 * 3 Recibido correcto ## 4 Recibido error
					 */

					System.out.println("Java - java.library.path=" + System.getProperty("java.library.path"));

					String resultado = "RESULTADO";
					String estado = "ESTADO";

					String sql = "";
					if (codigoRespuesta.equals("X")) {
						// Ha habido algun elemento del envio rechazado por agroseguro. Hay que
						// tratarlos uno a uno
						resultado = "CORRECTO";
						for (int i = 0; i < acuseReciboDocument.getAcuseRecibo().getDocumentoArray().length; i++) {
							Documento documento = acuseReciboDocument.getAcuseRecibo().getDocumentoArray(i);

							if (tipofichero.equals("P")) {
								// Los acuses pertenecen a polizas. Si el estado del acuse es 1, se cambia por 8
								// y si es 2, se cambia por 7
								if (documento.getEstado() == 1) {
									sql = "UPDATE o02agpe0.TB_POLIZAS SET IDESTADO = 8 WHERE REFERENCIA = '"
											+ documento.getId().substring(0, 7) + "'" + AND_IDENVIO + idenvio;
								} else {
									sql = "UPDATE o02agpe0.TB_POLIZAS SET IDESTADO = 7 WHERE REFERENCIA = '"
											+ documento.getId().substring(0, 7) + "'" + AND_IDENVIO + idenvio;
								}
							} else if (tipofichero.equals("M")) {
								// Los acuses pertenecen a anexos de modificacion de polizas
								// Si el estado del acuse es 1, se cambia por 3 y si es 2, se cambia por 4
								if (documento.getEstado() == 1) {
									sql = "UPDATE o02agpe0.TB_ANEXO_MOD SET ESTADO = 3 WHERE IDPOLIZA IN "
											+ SELECT_IDPOLIZA_FROM_O02AGPE0_TB_POLIZAS_WHERE_REFERENCIA
											+ documento.getId().substring(0, 7) + "')" + AND_IDENVIO + idenvio;
								} else {
									sql = "UPDATE o02agpe0.TB_ANEXO_MOD SET ESTADO = 4 WHERE IDPOLIZA IN "
											+ SELECT_IDPOLIZA_FROM_O02AGPE0_TB_POLIZAS_WHERE_REFERENCIA
											+ documento.getId().substring(0, 7) + "')" + AND_IDENVIO + idenvio;
								}
							} else if (tipofichero.equals("R")) {
								// Los acuses pertenecen a anexos de reducciones de capital
								if (documento.getEstado() == 1) {
									sql = "UPDATE o02agpe0.TB_ANEXO_RED SET IDESTADO = 3 WHERE IDPOLIZA IN "
											+ SELECT_IDPOLIZA_FROM_O02AGPE0_TB_POLIZAS_WHERE_REFERENCIA
											+ documento.getId().substring(0, 7) + "')" + AND_IDENVIO + idenvio;
								} else {
									sql = "UPDATE o02agpe0.TB_ANEXO_RED SET IDESTADO = 4 WHERE IDPOLIZA IN "
											+ SELECT_IDPOLIZA_FROM_O02AGPE0_TB_POLIZAS_WHERE_REFERENCIA
											+ documento.getId().substring(0, 7) + "')" + AND_IDENVIO + idenvio;
								}
							} else {
								// tipofichero sera 'S'. Los acuses pertenecen a siniestros
								if (documento.getEstado() == 1) {
									sql = "UPDATE o02agpe0.TB_SINIESTROS SET ESTADO = 3 WHERE NUMINTERNOENVIO = "
											+ documento.getId() + AND_IDENVIO + idenvio;
								} else {
									sql = "UPDATE o02agpe0.TB_SINIESTROS SET ESTADO = 4 WHERE NUMINTERNOENVIO = "
											+ documento.getId() + AND_IDENVIO + idenvio;
								}
							}

							System.out.println(" *******************************************");
							System.out.println("codigo respuesta: " + codigoRespuesta);
							System.out.println("tipo fichero: " + tipofichero);
							System.out.println("estado: " + documento.getEstado());
							System.out.println("query: ");
							System.out.println(sql);
							System.out.println("******************************************* ");

							ejecutaSentencia(sql); // Ejecutamos la sentencia.

						} // fin for
					} else {
						// En caso de que todos los elementos se hayan aceptado o rechazado, los
						// tratamos todos a la vez
						if (codigoRespuesta.equals("A")) {
							resultado = "CORRECTO";
							if (tipofichero.equals("P"))
								estado = "8";
							else
								estado = "3";
						} else if (codigoRespuesta.equals("R")) {
							resultado = "ERROR";
							if (tipofichero.equals("P"))
								estado = "7";
							else
								estado = "4";
						}
						if (tipofichero.equals("P")) {
							// Actualizar las polizas
							sql = "UPDATE o02agpe0.TB_POLIZAS SET IDESTADO = " + estado + WHERE_IDENVIO + idenvio;
						} else if (tipofichero.equals("R")) {
							// Actualizar los anexos de reduccion de capital
							sql = "UPDATE o02agpe0.TB_ANEXO_RED SET IDESTADO = " + estado + WHERE_IDENVIO + idenvio;
						} else if (tipofichero.equals("M")) {
							// Actualizar los anexos de modificacion
							sql = "UPDATE o02agpe0.TB_ANEXO_MOD SET ESTADO = " + estado + WHERE_IDENVIO + idenvio;
						} else if (tipofichero.equals("S")) {
							// Actualizar los siniestros
							sql = "UPDATE o02agpe0.TB_SINIESTROS SET ESTADO = " + estado + WHERE_IDENVIO + idenvio;
						}

						System.out.println("  *******************************************");
						System.out.println("codigo respuesta: " + codigoRespuesta);
						System.out.println("tipo fichero: " + tipofichero);
						System.out.println("estado: no es necesario si codigo respuesta no es X");
						System.out.println("query: ");
						System.out.println(sql);
						System.out.println("*******************************************  ");

						ejecutaSentencia(sql); // Actualizamos los estados.
					}
					// Guardamos el Acuse de recibo en el envio
					if (acuseReciboDocument != null && acuseReciboDocument.getAcuseRecibo() != null
							&& acuseReciboDocument.getAcuseRecibo().xmlText() != null
							&& !acuseReciboDocument.getAcuseRecibo().xmlText().equals("")) {

						actualizaAcuse(acuseReciboDocument.getAcuseRecibo().xmlText(), idenvio, resultado,
								nomAcuseRecibo);
					} else {
						actualizaAcuse(null, idenvio, resultado, nomAcuseRecibo);
					}
				}
			}
			System.out.println("OK - Estados de las polizas recibidas acutalizados correctamente");
			System.exit(0);
		
		}catch (Exception e){
			System.out.println("KO - Error al generar las consultas de actualizacion: " + stack2string(e));
			System.exit(1);
		}	
	}

	/**
	 * Metodo para actualizar el acuse de recibo obtenido de agroseguro para el envio indicado
	 * @param xml Contenido del acuse
	 * @param idenvio Identificador del envio
	 */
	private static void actualizaAcuse(String xml, String idenvio, String resultado, String nomAcuseRecibo) {
		Connection conexion = null;
		String sql = "";
		
		Statement stmt = null;
		ResultSet rs = null;

		CLOB clob = null;
		
		try {			
			conexion = DriverManager.getConnection(CONNECTION_URL, new Properties()); // Test & Produccion
			
			//Desactivo el autocommit
			conexion.setAutoCommit(false);
			
			sql = "UPDATE o02agpe0.TB_COMUNICACIONES SET RESULTADO = '" + resultado + "', FICHERO_RECIBO = '" + 
			nomAcuseRecibo.substring(0, nomAcuseRecibo.indexOf(".ZIP")) + 
			"', FECHA_RECEPCION = SYSDATE, TIPO_MOV='RECEPCION', FICHERO_CONTENIDO = EMPTY_CLOB() WHERE IDENVIO = " + idenvio;
		
			//Actualizamos la tabla de comunicaciones
			ejecutaSentencia(sql, conexion);
			
			if(xml != null){
				sql = "SELECT FICHERO_CONTENIDO FROM o02agpe0.TB_COMUNICACIONES WHERE IDENVIO = " + idenvio + " FOR UPDATE";
			
				stmt = conexion.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()){
					clob = (CLOB) rs.getClob("FICHERO_CONTENIDO");
					
					OutputStream os = clob.setAsciiStream(1);
	
					byte[] b = xml.getBytes("ASCII");
	
					os.write(b);
					os.flush();
					os.close();
	
				}
			}
			//hago commit activo el autocommit de la conexion (por si fuera necesario)
			conexion.commit();
			conexion.setAutoCommit(true);
		} catch (ClassNotFoundException e) {
			System.out.println(KO_ERROR_AL_ACUTALIZAR_EL_ACUSE + stack2string(e));
			System.exit(-1);
		} catch (SQLException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println(KO_ERROR_AL_ACUTALIZAR_EL_ACUSE + sql);
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println(KO_ERROR_AL_ACUTALIZAR_EL_ACUSE + sql);
			System.exit(-1);
		} catch (IOException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println(KO_ERROR_AL_ACUTALIZAR_EL_ACUSE + sql);
			System.exit(-1);
		} catch (Exception e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error inesperado al acutalizar el acuse: " + sql);
			System.exit(-1);
		} finally{
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar el Statement en actualizaAcuse " + stack2string(e));
			}
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar el ResultSet " + stack2string(e));
			}
			try {
				if (conexion != null)
					conexion.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar la conexion " + stack2string(e));
			}
		}
	}

	private static void ejecutaSentencia(String sql) throws Exception {
		try (Connection conexion = DriverManager.getConnection(CONNECTION_URL, new Properties()); OraclePreparedStatement stmt = (OraclePreparedStatement)conexion.prepareStatement(sql)) {
			//Desactivo el autocommit
			conexion.setAutoCommit(false);			
			stmt.executeUpdate();			
			conexion.commit();
			conexion.setAutoCommit(true);	
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.out.println(sql);
			e.printStackTrace();
			throw e;
		}
	}
	
	private static void ejecutaSentencia(String sql, Connection conexion) throws Exception {
		try (OraclePreparedStatement stmt = (OraclePreparedStatement)conexion.prepareStatement(sql)) {
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.out.println(sql);
			e.printStackTrace();
			throw e;
		}
	}
	
	private static String stack2string(Exception excepcion){
		try{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			excepcion.printStackTrace(pw);
			return "---- \r\n" + sw.toString() + " ---- \r\n";
		} catch (Exception e){
			return "No se pudo convertir la excepcion a cadena de texto";
		}
	}
}
