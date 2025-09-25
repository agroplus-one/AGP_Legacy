package com.rsi.agp.batch;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * @author root
 *
 */
public class AcuseReciboFinder {

	
	private static final int DEFAULT_HTTPS_PORT = 443;
	private static final int DEFAULT_PROXY_PORT = 8080;
	
	private static final String ADDRESS_STR = "address";
	private static final String FILEID_STR = "fileID";
	private static final String PROXYHOST_STR = "proxyHost";
	private static final String PROXYPORT_STR = "proxyPort";
	private static final String USERAGRO_STR = "userAgro";
	private static final String PWDAGRO_STR = "passwordAgro";
	private static final String DOMAIN_STR = "domainName";
	private static final String MACHINE_STR = "machineName";
	private static final String HTTPSPORT_STR = "httpsPort";
	private static final String PROXYUSER_STR = "proxyUser";
	private static final String PROXYPWD_STR = "proxyPassword";


	/**
	 * Descarga el fichero indicado y lo almacena en el directorio indicado
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		URL               address          = null;
		String			  proxyHost        = null;
		int 			  proxyPort        = DEFAULT_PROXY_PORT;
		int 			  httpsPort        = DEFAULT_HTTPS_PORT;
		String            domainName       = null;
		String            machineName      = null;
		String            proxyUser        = null;
		String            proxyPassword    = null;
		String 			  fileID		   = null;
		String			  verbose		   = null;
		CommandLineParser parser           = null;  
		CommandLine       cmdLine          = null;  
		String		 	  userAgro		   = null;
		String			  pwdAgro		   = null;
		
		
		///////////////////////////////////////////////////////////////////////  
		// Fase 1: Configuramos las opciones de validacion de entrada.  
		///////////////////////////////////////////////////////////////////////  
		               
		Options options = new Options();  
		options.addOption(ADDRESS_STR,              true, "URL del fichero a descargar (obligatorio)");  
		options.addOption(FILEID_STR,               true, "Nombre del archivo que se envia a AgroSeguro (obligatorio)");  
		options.addOption(PROXYHOST_STR,            true, "IP del servidor proxy (opcional)");  
		options.addOption(PROXYPORT_STR,            true, "Puerto del servidor proxy (opcional)");  
		options.addOption(USERAGRO_STR,      		true, "Usuario para la conexion al servlet de envio de ficheros de AgroSeguro (obligatorio)");  
		options.addOption(PWDAGRO_STR,  			true, "Contrasenha para la conexion al servlet de envio de ficheros de AgroSeguro (obligatorio)"); 		
		options.addOption(DOMAIN_STR,             	true, "Nombre del dominio si la autentificacion por proxy es NTLM (opcional)");  
		options.addOption(MACHINE_STR,            	true, "Nombre de la maquina si la autentificacion por proxy es NTLM (opcional)");
		options.addOption(HTTPSPORT_STR,            true, "Puerto HTTPS para la conexion (opcional, por defecto se utiliza el puerto 443)");
		options.addOption(PROXYUSER_STR,            true, "Usuario del proxy (opcional)"); 
		options.addOption("verbose",          		true, "Muestra mensajes internos del estado del proceso (opcional)"); 
		options.addOption(PROXYPWD_STR,        		true, "Password para el usuario del proxy (opcional)");  
		options.addOption("h", "help",              false, "Imprime el mensaje de ayuda");   
		           
		try {  	       
		    ///////////////////////////////////////////////////////////////////////  
			// Fase 2: Parseamos la entrada con la configuracion establecida  
			///////////////////////////////////////////////////////////////////////  
			         
			parser  = new BasicParser();  
			cmdLine = parser.parse(options, args);  
			               
			///////////////////////////////////////////////////////////////////////  
			// Fase 3: Analizamos los resultados y realizamos las tareas pertinentes  
			///////////////////////////////////////////////////////////////////////  
			              
			// Si esta la opcion de ayuda, la imprimimos y salimos.  
			if (cmdLine.hasOption("h")){    // No hace falta preguntar por el parametro "help". Ambos son sinonimos  
				new HelpFormatter().printHelp(AcuseReciboFinder.class.getName(), options );  
			    return;  
			}  
					               
			// Si el usuario ha especificado la direccion la leemos          
			if (cmdLine.hasOption(ADDRESS_STR)){  
			    address = new URL(cmdLine.getOptionValue(ADDRESS_STR));    
			} else {  
			    address = null;  
			}  
			// Sino existe generamos un error pues es un parametro requerido.  
			if (address == null){  
			    throw new org.apache.commons.cli.ParseException("La direccion URL del archivo (-address <arg>) es requerida");  
			}  
						
			// Nombre del fichero que se envio a Agroseguro          
			if (cmdLine.hasOption(FILEID_STR)){  
			    fileID = cmdLine.getOptionValue(FILEID_STR);    
			}  			
			
			// Si el usuario ha especificado el puerto https lo leemos          
			if (cmdLine.hasOption(HTTPSPORT_STR)){  
			    httpsPort = Integer.parseInt(cmdLine.getOptionValue(HTTPSPORT_STR));    
			} 

			// Si el usuario ha especificado el parametro verbose lo leemos          
			if (cmdLine.hasOption("verbose")){  
			    verbose = "fakeValue";    
			} 			
			
			// Si el usuario ha especificado el puerto del proxy lo leemos          
			if (cmdLine.hasOption(DOMAIN_STR)){  
			    domainName = cmdLine.getOptionValue(DOMAIN_STR);    
			} 

			// Si el usuario ha especificado el nombre de la maquina lo leemos          
			if (cmdLine.hasOption(MACHINE_STR)){  
			    machineName = cmdLine.getOptionValue(MACHINE_STR);    
			} 

			// Si el usuario ha especificado el usuario del proxy lo leemos          
			if (cmdLine.hasOption(PROXYUSER_STR)){  
			    proxyUser = cmdLine.getOptionValue(PROXYUSER_STR);    
			} 

			// Si el usuario ha especificado la password del usuario del proxy lo leemos          
			if (cmdLine.hasOption(PROXYPWD_STR)){  
			    proxyPassword = cmdLine.getOptionValue(PROXYPWD_STR);    
			} 
			
			// Si el usuario ha especificado la direccion del proxy lo leemos          
			if (cmdLine.hasOption(PROXYHOST_STR)){  
			    proxyHost = cmdLine.getOptionValue(PROXYHOST_STR);    
			} else {  
			    proxyHost = "";  
			} 			              

			// Si el usuario ha especificado el puerto del proxy lo leemos          
			if (cmdLine.hasOption(PROXYPORT_STR)){  
			    proxyPort = Integer.parseInt(cmdLine.getOptionValue(PROXYPORT_STR));    
			} 
			
			// Usuario para la conexion al servlet de envio de ficheros de AgroSeguro          
			if (cmdLine.hasOption(USERAGRO_STR)){  
			    userAgro = cmdLine.getOptionValue(USERAGRO_STR);
			} else {  
				userAgro = null; 			    
			}
			// Sino existe generamos un error pues es un parametro requerido.  
			if (userAgro == null){  
			    throw new org.apache.commons.cli.ParseException("El usuario de conexion a AgroSeguro (-userAgro <arg>) es requerido");  
			} 			

			// Password para la conexion al servlet de envio de ficheros de AgroSeguro          
			if (cmdLine.hasOption(PWDAGRO_STR)){  
			    pwdAgro = cmdLine.getOptionValue(PWDAGRO_STR);   
			} else {  
				pwdAgro = null; 			    
			}	
			// Sino existe generamos un error pues es un parametro requerido.  
			if (pwdAgro == null){  
			    throw new org.apache.commons.cli.ParseException("La contrasenha de conexion a AgroSeguro (-passwordAgro <arg>) es requerido");  
			}			
			
			// Se obtiene un listado de todos los TXT, para luego ir comprobando cual es el que
			StringBuilder buffer = AcuseReciboFinder.doWork(address, 
									     				   proxyHost, 
									     				   proxyPort, 
									     				   httpsPort,
									     				   domainName, 
									     				   machineName, 
									     				   proxyUser, 
									     				   proxyPassword,
									     				   userAgro,
									     				   pwdAgro,
									     				   "DIR",
									     				   "*.TXT",
									     				   verbose
									     				   );		
			
			if (buffer == null) throw new Exception("Ocurrio un error al ejecutar el comando indicado.");

			// Se obtiene la lista de ficheros
			ArrayList<String> listFiles = getArrayOfFileNames(buffer, verbose);

			// Se descarga cada fichero y se comprueba si su contenido coincide con nuestro fileID
			String file = null;
			for (Iterator<String> iter = listFiles.iterator(); iter.hasNext();) {
				file = iter.next();
				buffer = AcuseReciboFinder.doWork(address, 
	     				   						  proxyHost, 
	     				   						  proxyPort, 
	     				   						  httpsPort,
	     				   						  domainName, 
	     				   						  machineName, 
	     				   						  proxyUser, 
	     				   						  proxyPassword,
	     				   						  userAgro,
	     				   						  pwdAgro,
	     				   						  "GET",
	     				   						  file,
	     				   						  verbose
	     				   						  );
				if (buffer == null) throw new Exception("Ocurrio un error al descargar el fichero " + file);
				
				// Se comprueba si el contenido coincide con nuestro fileID
				if (verbose != null) System.out.println("Buscando " + fileID + " en los ficheros...");
				if (buffer.indexOf(fileID) != -1) {
					// Se envia por la salida estandar el nombre del fichero, para poder recuperarlo en el shell script
					if (verbose != null) System.out.print("Encontrado " + fileID + " en el fichero " + file);
					System.out.print(file);
					return;
				}
					
			}
			               
		} catch (org.apache.commons.cli.ParseException ex){ 
		    new HelpFormatter().printHelp(AcuseReciboFinder.class.getName(), options );    // Error, imprimimos la ayuda  
		} catch (java.net.MalformedURLException ex){  
		    new HelpFormatter().printHelp(AcuseReciboFinder.class.getName(), options );    // Error, imprimimos la ayuda  
		} catch (java.lang.NumberFormatException ex){  
		    new HelpFormatter().printHelp(AcuseReciboFinder.class.getName(), options );    // Error, imprimimos la ayuda  
		} catch (Exception ex){  
		    new HelpFormatter().printHelp(AcuseReciboFinder.class.getName(), options );    // Error, imprimimos la ayuda  
		} 		
	}
	
	/**
	 * Descarga el fichero indicado en address.
	 * Si proxyHost es distinto de vacio, configura el proxy
	 * Guarda el fichero descargado en destdir
	 * 
	 * @param address
	 * @param proxyHost
	 * @param proxyPort
	 * @param destDir
	 * @param domainName
	 * @param machineName
	 * @param proxyUser
	 * @param proxyPassword
	 * @param disableOverwrite
	 * 
	 * @return El contenido de la descarga, ya sea para DIR o GET
	 * 
	 * @throws Exception
	 */
	public static StringBuilder doWork(URL address, 
							  		  String proxyHost, 
							  		  int proxyPort, 
							  		  int httpsPort,
							  		  String domainName,
							  		  String machineName,
							  		  String proxyUser, 
							  		  String proxyPassword,
							  		  String userAgro,
							  		  String pwdAgro,
							  		  String action, 
							  		  String fileOrPatternName,
							  		  String verbose) throws Exception {
		HttpClient          httpClient = null;  // Objeto a traves del cual realizamos las peticiones  
		HttpMethodBase      request    = null;  // Objeto para realizar las peticiines HTTP GET o POST  
		int                 status     = 0;     // Codigo de la respuesta HTTP  
		
		// Instanciamos el objeto  
		httpClient = new HttpClient();  
		  
		try {
			// Especificamos que salimos a traves de un Proxy. Auth NTLM   
			if (!proxyHost.equals("")){  
				httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort); 
				
				if (domainName != null && !domainName.equals("")){
					httpClient.getState().setProxyCredentials(
							new AuthScope(proxyHost, proxyPort, domainName),
							new NTCredentials(proxyUser, proxyPassword, machineName, domainName)
					);			
				}
			} 
			// Basic AUTH
			if (proxyUser != null && !proxyUser.equals("")) {
				Credentials credentials = new UsernamePasswordCredentials(proxyUser, proxyPassword); 
				AuthScope authScope = new AuthScope(proxyHost, proxyPort); 
				httpClient.getState().setProxyCredentials(authScope, credentials); 				
			}
			
			// El fichero de AgroSeguro se descarga por https
			Protocol httpsProtocol = new Protocol("https", ((ProtocolSocketFactory)new EasySSLProtocolSocketFactory()), httpsPort);
			httpClient.getHostConfiguration().setHost(address.getHost(), httpsPort, httpsProtocol);
			
			request = new GetMethod(address.toString());  
			// Le indicamos que realize automaticamente el seguimiento de las redirecciones   
			// en caso de que existan.  
			request.setFollowRedirects(true);
			
			// Se anhaden los parametros
			NameValuePair[] params = new NameValuePair[4];
			params[0] = new NameValuePair("user", userAgro);
			params[1] = new NameValuePair("password", pwdAgro);
			params[2] = new NameValuePair("action", action);
			params[3] = new NameValuePair("file", fileOrPatternName);
			request.setQueryString(params); 
			
			if (verbose != null) System.out.println("Llamando a AgroSeguro: action=" + action + " file=" + fileOrPatternName);
			
			// Indicamos reintente 3 veces en caso de que haya errores.  
			request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,   
			                      new DefaultHttpMethodRetryHandler(3, true));
			
			// Leemos el codigo de la respuesta HTTP que nos devuelve el servidor  
			status = httpClient.executeMethod(request);  

			// Vemos si la peticion se ha realizado satisfactoriamente  
			if (status != HttpStatus.SC_OK) {  
				System.err.println("Error\t" + request.getStatusCode() + "\t" +   
			                                   request.getStatusText() + "\t" + request.getStatusLine());
				return null;
			} else {  
			    // Leemos el contenido de la respuesta y realizamos el tratamiento de la misma,  
			    // guardando el fichero en destdir. Al haber hecho una operacion DIR, lo que se obtiene 
				// es una lista de ficheros, que se tendran que procesar uno a uno para saber cual es
				// el acuse de recibo de nuestro envio
				try (BufferedInputStream bistream = new BufferedInputStream(request.getResponseBodyAsStream())) {
					return readContents(bistream, verbose);
				}
			}
		} catch (Exception e) {
			System.err.println("Error\t: " + e.getMessage()); 
			throw e;
		} finally {
			if (request != null)
				request.releaseConnection();
		}  
	}
	
	private static StringBuilder readContents(final BufferedInputStream bistream, final String verbose)
			throws IOException {
		// Se lee la respuesta y se almacena en un stringbuffer
		byte[] contents = new byte[1024];
		int bytesRead = 0;
		StringBuilder strFileContents = new StringBuilder();
		while ((bytesRead = bistream.read(contents)) != -1) {
			strFileContents.append(new String(contents, 0, bytesRead));
		}
		if (verbose != null)
			System.out.println(strFileContents.toString());
		return strFileContents;
	}
	
	/**
	 * Separa el contenido del string, primero por lineas y luego cada linea
	 * por espacios, y busca ".TXT" en cada token para encontrar los nombres 
	 * de los ficheros. La estructura del stringbuffer es:
	 *  07/10/2010 04:45:54 *FICHERO                   80 096F4528.TXT
	 *  07/10/2010 04:06:03 *FICHERO                   80 096F0401.TXT
	 *  03/10/2010 11:14:05 *FICHERO                   80 09CA1326.TXT
	 *	27/09/2010 10:24:34 *FICHERO                   80 08Q92353.TXT
	 *	22/09/2010 12:54:07 *FICHERO                   80 08LB5328.TXT
     *  22/09/2010 12:31:55 *FICHERO                   80 08LB3132.TXT
	 * 	13/10/2010 04:20:24 *FICHERO                   80 09CF2002.TXT
     *	22/09/2010 12:20:20 *FICHERO                   80 08LB1845.TXT
	 * @param buffer
	 * @return
	 */
	private static ArrayList<String> getArrayOfFileNames(StringBuilder buffer, String verbose) {
		ArrayList<String> listFiles = new ArrayList<String>();
		StringTokenizer stByLineFeed = new StringTokenizer(buffer.toString(), "\n");
		StringTokenizer stByName = null;
		String tokenByLineFeed = null;
		String tokenByName = null;

		while (stByLineFeed.hasMoreTokens()){
			tokenByLineFeed = stByLineFeed.nextToken();
			stByName = new StringTokenizer(tokenByLineFeed, " ");
			while (stByName.hasMoreTokens()) {
				tokenByName = stByName.nextToken();
				if (tokenByName.indexOf(".TXT") != -1) {
					listFiles.add(tokenByName);
					if (verbose != null) System.out.println("Fichero encolado: " + tokenByName);
				}
			}
		}
		return listFiles;
	}
}
