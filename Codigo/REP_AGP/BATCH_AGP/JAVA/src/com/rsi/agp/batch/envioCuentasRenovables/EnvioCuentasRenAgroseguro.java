package com.rsi.agp.batch.envioCuentasRenovables;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rsi.agp.batch.FileUploader;
import com.rsi.agp.core.util.Constants;

/**
 * Clase para el envio de Cuentas de polizas renoables a Agroseguro. Pasos a seguir:
 * 
 * 1. Leemos de la carpeta a donde se encuentran los ficheros.
 * 2. Por cada fichero ejecutamos los siguientes pasos:
 *     - Comprobamos si es un acuse o si ya lo hemos procesado previamente (tabla TB_COMUNICACIONES)
 *     - Si ya lo hemos procesado o es un acuse, pasamos al siguiente.
 *     - Si NO lo hemos procesado, lo enviamos a Agroseguro e insertamos el registro correspondiente. 
 * 
 * @author T-Systems
 *
 */

public class EnvioCuentasRenAgroseguro {
	private static final Logger logger = Logger.getLogger(EnvioCuentasRenAgroseguro.class);
	private static final int DEFAULT_HTTPS_PORT = 443;
	private static final int DEFAULT_PROXY_PORT = 8080;

	private String ruta;
	
	//Atributos para el envio a Agroseguro
	private String address;
	private String verbose;
	private String userAgro;
	private String passwordAgro;
	
	public EnvioCuentasRenAgroseguro() {
		//Inicializamos los atributos necesarios para las conexiones con BBDD y Agroseguro
		ResourceBundle bundle = ResourceBundle.getBundle("envioCuentasRenovables");
		
		ruta     = bundle.getString("ruta.origen.envio");
		address  = bundle.getString("address");
		verbose  = bundle.getString("verbose");
		userAgro = bundle.getString("userAgro");
		passwordAgro = bundle.getString("passwordAgro");
	}

	public static void main(Session session, List<String> lstReferencias) {
		
		EnvioCuentasRenAgroseguro envio = new EnvioCuentasRenAgroseguro();
		try {
			envio.procesarEnvio(session,lstReferencias);
			logger.debug("Proceso de envio de cuentas de polizas renovables finalizado correctamente");
			

		} catch (Exception e) {
			logger.debug("Error durante el envio de cuentas de polizas renovables " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/**
	 * Metodo para enviar los ficheros a Agroseguro
	 */
	private void procesarEnvio(Session session, List<String> lstPlzRenov){

		//1. Leer los ficheros TXT de la carpeta de origen:
		File dir = new File(ruta);
		String[] ficheros = dir.list(new TXTFileFilter());
		logger.debug("## Inicio del proceso de envio de ficheros.");
		for (int z=0; ficheros != null && z<ficheros.length; z++){
			File fich = new File(ruta + "/" + ficheros[z]);
			String nombreFichero = fich.getName().substring(0, fich.getName().lastIndexOf("."));
			logger.debug("Procesando el fichero " + nombreFichero);
			
			//2. Por cada fichero...
			//     Si el nombre del fichero existe en TB_COMUNICACIONES en el campo FICHERO_RECIBO_F 
			//     o en el campo FICHERO_ENVIO => no lo procesamos. En caso contrario, intentamos enviar a Agroseguro
			//     el fichero ZIP y el TXT.
			if (!this.ficheroProcesado(nombreFichero,session)){
				//Realizar el envio a Agroseguro
				logger.debug("Enviando el fichero...");
				String resultadoEnvio = "OK";
				
				try {
					FileUploader.doWork(new URL(address), nombreFichero, ruta, userAgro, passwordAgro, 
							EnvioCuentasRenAgroseguro.DEFAULT_HTTPS_PORT, "", EnvioCuentasRenAgroseguro.DEFAULT_PROXY_PORT, 
							"", "", "", "", verbose);
					resultadoEnvio = "OK";
					logger.debug("Fichero enviado!");
				} catch (MalformedURLException e) {
					logger.debug("Error al procesar el fichero. " + e.getMessage());
					resultadoEnvio = "KO";
				} catch (Exception e) {
					logger.debug("Error indefinido al procesar el fichero. " + e.getMessage());
					resultadoEnvio = "KO";
				}
				
				//Insertar el registro correspondiente en la tabla TB_COMUNICACIONES
				BigDecimal idEnvio = this.insertarRegistroFichero(nombreFichero, resultadoEnvio, session);
				
				//Actualizar polizas renovables con el idEnvio
				this.actualizarPolizasRenovables(idEnvio, lstPlzRenov, "OK".equals(resultadoEnvio), session);
			}
		}
	}
	
	/**
	 * Metodo que comprueba si un fichero de envio ya ha sido procesado o se trata de un acuse de recibo.
	 * @param nombreFichero
	 * @return
	 */
	private boolean ficheroProcesado(String nombreFichero, Session session){		
		logger.debug("INICIO ficheroProcesado");		
		try {
			String sql = "SELECT count(*) FROM o02agpe0.TB_COMUNICACIONES c WHERE c.FICHERO_ENVIO = '" + nombreFichero + "'";
			//sql += " OR c.FICHERO_RECIBO_F = '" + nombreFichero + "'";
			logger.debug("Comprobando si el fichero " + nombreFichero + " se ha procesado");
			Query qHis = session.createSQLQuery(sql);
			qHis.executeUpdate();
			BigDecimal resultado = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			logger.debug("Elementos obtenidos de la consulta: " + resultado);
			
			int numero = Integer.parseInt(resultado.toString());
			logger.debug("Numero de elementos obtenidos de la tabla: " + numero);
			if (numero > 0)
				return true;
		} catch (Exception e) {
			logger.debug("Error en ficheroProcesado: " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Metodo para insertar los datos de un fichero enviado a Agroseguro en la tabla de comunicaciones
	 * @param nombreFichero Nombre del fichero enviado a Agroseguro
	 * @param resultadoEnvio Resultado del envio del fichero a Agroseguro
	 */

	private BigDecimal insertarRegistroFichero(String nombreFichero, String resultadoEnvio, Session session){
		Transaction trans   = null;
		trans = session.beginTransaction();
		String sql = "select o02agpe0.sq_comunicaciones.nextval from dual";
		BigDecimal secuencia = (BigDecimal)session.createSQLQuery(sql).uniqueResult();
		sql = "INSERT INTO o02agpe0.TB_COMUNICACIONES VALUES ("+secuencia+",sysdate, '" + nombreFichero + "','ENVIO', '" + resultadoEnvio + "', null, null,'G', null)";
		logger.debug("ACTUALIZACION DE ESTADO " + sql );
		Query query = session.createSQLQuery(sql);
		query.executeUpdate();
		trans.commit();
		return secuencia;
	}
	
	private void actualizarPolizasRenovables(BigDecimal idEnvio, List<String> lstPlzRenov, boolean envioOK,
			Session session) {
		List<String> lstCadenasIds = getListasParaIN(lstPlzRenov);
		boolean primera = true;
		try {
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append("update o02agpe0.tb_Polizas_Renovables ren set idEnvio = " + idEnvio);
			// SI EL ENVIO ES KO ES NECESARIO REVERTIR EL ESTADO (NO HAY EXCEPCION, NO SE
			// PROCESA EN LA GESTION DE ERRORES DE LA CLASE LLAMANTE
			if (!envioOK) {
				stringQuery.append(", estado_envio_iban_agro = " + Constants.ES_POL_REN_ENVIO_IBAN_PREPARADO);
			}
			stringQuery.append(" where 1=1 ");
			if (lstCadenasIds.size() > 0) {
				primera = true;
				for (String cadenaIds : lstCadenasIds) {
					if (primera) {
						stringQuery.append(" and (ren.id in (" + cadenaIds + ")");
						primera = false;
					} else {
						stringQuery.append(" or ren.id in (" + cadenaIds + ")");
					}
				}
				stringQuery.append(")");
			}
			Query query = session.createSQLQuery(stringQuery.toString());
			query.executeUpdate();
			logger.debug("ACTUALIZACION DE POLIZAS RENOVABLES OK");
		} catch (Exception e) {
			logger.error("Error en la actualizacion del idEnvio de las polizas renovables ", e);
		}
	}
	
	public List<String> getListasParaIN(List<String> lstPlzRenov){
		List<String> lstCadenasIds = new ArrayList<String>();
		int contador = 0;
		String cadena = "";
		boolean primera = true;
		for (String id : lstPlzRenov) {
			if (contador<1000) {
				if (!primera)
					cadena = cadena + ",";
				else
					primera = false;
				cadena = cadena +id;
				contador++;			
			}else {
				if (cadena.length()>0)
					lstCadenasIds.add(cadena);
				cadena = id;
				contador = 1;
			}
		}
		lstCadenasIds.add(cadena);
		logger.debug("Numero total de elementos: " + lstPlzRenov.size() + ". Listas partidas: " + lstCadenasIds.size());		
		return lstCadenasIds;
	}
	
	private class TXTFileFilter implements FilenameFilter{
		public boolean accept(File dir, String name) {
	        return (name.endsWith(".txt") || name.endsWith(".TXT"));
	    }
	}
	
}

