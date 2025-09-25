package com.rsi.agp.batch;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * @author root
 *
 */
public class FileUploader {
	
	private static final int DEFAULT_HTTPS_PORT = 443;
	private static final int DEFAULT_PROXY_PORT = 8080;
	private static final String ETIQUETA_EXTENSION = ".TXT";
	private static final String DATOS_EXTENSION = ".ZIP";

	private static final Logger logger = Logger.getLogger(FileUploader.class);
	/*DNF 12/03/2021 PET.73141 nueva variable*/
	private static final int NUMERO_INTENTOS = Integer.parseInt(ResourceBundle.getBundle("agp").getString("ftp.upload.numreintentos"));
	/*FIN DNF 12/03/2021 PET.73141 */
	private static final int SLEEP_TIME = Integer.parseInt(ResourceBundle.getBundle("agp").getString("ftp.upload.sleeptime_ms"));
	/**
	 * Realiza un POST del fichero indicado en la URL indicada
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		URL               address          = null;
		String			  fileToUpload     = null;
		String			  folder           = null;
		int				  httpsPort		   = DEFAULT_HTTPS_PORT;
		String			  proxyHost        = null;
		int 			  proxyPort        = DEFAULT_PROXY_PORT;
		String            domainName       = null;
		String            machineName      = null;
		String            proxyUser        = null;
		String            proxyPassword    = null;
		String		 	  userAgro		   = null;
		String			  pwdAgro		   = null;
		String			  verbose		   = null;
		CommandLineParser parser           = null;  
		CommandLine       cmdLine          = null;  
		
		///////////////////////////////////////////////////////////////////////  
		// Fase 1: Configuramos las opciones de validación de entrada.  
		///////////////////////////////////////////////////////////////////////  
		               
		Options options = new Options();  
		options.addOption("address",       true, "URL donde se quiere enviar el fichero (obligatorio)");  
		options.addOption("file",          true, "Nombre del fichero sin extension que se quiere enviar (obligatorio, mismo nombre para .TXT y .ZIP)");  
		options.addOption("folder",        true, "Directorio donde se encuentran los ficheros que se quieren enviar (opcional, por defecto, directorio actual)");  
		options.addOption("userAgro",      true, "Usuario para la conexión al servlet de envío de ficheros de AgroSeguro (obligatorio)");  
		options.addOption("passwordAgro",  true, "Contraseña para la conexión al servlet de envío de ficheros de AgroSeguro (obligatorio)"); 
		options.addOption("httpsPort",     true, "Puerto HTTPS para la conexión (opcional, por defecto se utiliza el puerto 443)");
		options.addOption("proxyHost",     true, "IP del servidor proxy (opcional)");  
		options.addOption("proxyPort",     true, "Puerto del servidor proxy (opcional, por defecto se utiliza el puerto 8080)");  
		options.addOption("verbose",       true, "Muestra mensajes internos del estado del proceso (opcional)"); 
		options.addOption("domainName",    true, "Nombre del dominio si la autentificación por proxy es NTLM (opcional)");  
		options.addOption("machineName",   true, "Nombre de la máquina si la autentificación por proxy es NTLM (opcional)");  
		options.addOption("proxyUser",     true, "Usuario del proxy (opcional)"); 
		options.addOption("proxyPassword", true, "Password para el usuario del proxy (opcional)");  
		options.addOption("h", "help",     false, "Imprime el mensaje de ayuda");   
		           
		try {  	       
		    ///////////////////////////////////////////////////////////////////////  
			// Fase 2: Parseamos la entrada con la configuración establecida  
			///////////////////////////////////////////////////////////////////////  
			         
			parser  = new BasicParser();  
			cmdLine = parser.parse(options, args);  
			               
			///////////////////////////////////////////////////////////////////////  
			// Fase 3: Analizamos los resultados y realizamos las tareas pertinentes  
			///////////////////////////////////////////////////////////////////////  
			              
			// Si está la opcion de ayuda, la imprimimos y salimos.  
			if (cmdLine.hasOption("h")){    // No hace falta preguntar por el parámetro "help". Ambos son sinónimos  
				new HelpFormatter().printHelp(FileUploader.class.getName(), options );  
			    return;  
			}  
					               
			// Si el usuario ha especificado la direccion la leemos          
			if (cmdLine.hasOption("address")){  
			    address = new URL(cmdLine.getOptionValue("address"));    
			} else {  
			    address = null;  
			}  
			// Sino existe generamos un error pues es un parámetro requerido.  
			if (address == null){  
			    throw new org.apache.commons.cli.ParseException("La direccion URL del archivo (-address <arg>) es requerida");  
			}  
			
			// Nombre/Ruta del fichero a enviar          
			if (cmdLine.hasOption("file")){  
			    fileToUpload = cmdLine.getOptionValue("file");    
			} else {  
				fileToUpload = null; 			    
			}
			// Sino existe generamos un error pues es un parámetro requerido.  
			if (fileToUpload == null){  
			    throw new org.apache.commons.cli.ParseException("El fichero a enviar (-file <arg>) es requerido");  
			} 			
			
			// Ruta donde reside el fichero a enviar          
			if (cmdLine.hasOption("folder")){  
			    folder = cmdLine.getOptionValue("folder");  
			}
			else {
				folder = "."; // Si no se indica directorio actual
			}
			
			// Usuario para la conexión al servlet de envío de ficheros de AgroSeguro          
			if (cmdLine.hasOption("userAgro")){  
			    userAgro = cmdLine.getOptionValue("userAgro");
			} else {  
				userAgro = null; 			    
			}
			// Sino existe generamos un error pues es un parámetro requerido.  
			if (userAgro == null){  
			    throw new org.apache.commons.cli.ParseException("El usuario de conexión a AgroSeguro (-userAgro <arg>) es requerido");  
			} 			

			// Password para la conexión al servlet de envío de ficheros de AgroSeguro          
			if (cmdLine.hasOption("passwordAgro")){  
			    pwdAgro = cmdLine.getOptionValue("passwordAgro");   
			} else {  
				pwdAgro = null; 			    
			}	
			// Sino existe generamos un error pues es un parámetro requerido.  
			if (pwdAgro == null){  
			    throw new org.apache.commons.cli.ParseException("La contraseña de conexión a AgroSeguro (-passwordAgro <arg>) es requerido");  
			} 			
			
			// Si el usuario ha especificado el puerto https lo leemos          
			if (cmdLine.hasOption("httpsPort")){  
			    httpsPort = Integer.parseInt(cmdLine.getOptionValue("httpsPort"));    
			} 
			
			// Si el usuario ha especificado la direccion ip del proxy lo leemos          
			if (cmdLine.hasOption("proxyHost")){  
			    proxyHost = cmdLine.getOptionValue("proxyHost");    
			} else {  
			    proxyHost = "";  
			} 			              

			// Si el usuario ha especificado el parametro verbose lo leemos          
			if (cmdLine.hasOption("verbose")){  
			    verbose = "fakeValue";    
			} 
			
			// Si el usuario ha especificado el puerto del proxy lo leemos          
			if (cmdLine.hasOption("domainName")){  
			    domainName = cmdLine.getOptionValue("domainName");    
			} 

			// Si el usuario ha especificado el nombre de la maquina lo leemos          
			if (cmdLine.hasOption("machineName")){  
			    machineName = cmdLine.getOptionValue("machineName");    
			} 

			// Si el usuario ha especificado el usuario del proxy lo leemos          
			if (cmdLine.hasOption("proxyUser")){  
			    proxyUser = cmdLine.getOptionValue("proxyUser");    
			} 

			// Si el usuario ha especificado la password del usuario del proxy lo leemos          
			if (cmdLine.hasOption("proxyPassword")){  
			    proxyPassword = cmdLine.getOptionValue("proxyPassword");    
			} 
			
			// Si el usuario ha especificado la direccion del proxy lo leemos          
			if (cmdLine.hasOption("proxyHost")){  
			    proxyHost = cmdLine.getOptionValue("proxyHost");    
			} else {  
			    proxyHost = "";  
			} 			              

			// Si el usuario ha especificado el puerto del proxy lo leemos          
			if (cmdLine.hasOption("proxyPort")){  
			    proxyPort = Integer.parseInt(cmdLine.getOptionValue("proxyPort"));    
			} 
			
			// Uploading file...
			FileUploader.doWork(address,
								fileToUpload,
								folder,
								userAgro,
								pwdAgro,
								httpsPort,
								proxyHost, 
								proxyPort, 
								domainName, 
								machineName, 
								proxyUser, 
								proxyPassword, 
								verbose);		               
			if (verbose != null) System.out.println("OK! Files Succesfully Uploaded.");  
			
			System.exit(0);
			               
		} catch (org.apache.commons.cli.ParseException ex){ 
			logger.error("Error en FileUploader: " + ex.getMessage());
		    new HelpFormatter().printHelp(FileUploader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(1);
		} catch (java.net.MalformedURLException ex){  
			logger.error("Error en FileUploader: " + ex.getMessage());
		    new HelpFormatter().printHelp(FileUploader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(2);
		} catch (java.lang.NumberFormatException ex){  
			logger.error("Error en FileUploader: " + ex.getMessage());
		    new HelpFormatter().printHelp(FileUploader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(3);
		} catch (Exception ex){  
			logger.error("Error en FileUploader: " + ex.getMessage());
		    new HelpFormatter().printHelp(FileUploader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(4);
		} 		
	}
	
	/**
	 * Envia el fichero indicado a la URL de AgroSeguro indicada
	 * 
	 * @param address
	 * @param fileToUpload
	 * @param folder
	 * @param userAgro
	 * @param pwdAgro
	 * @param httpsPort
	 * @param proxyHost
	 * @param proxyPort
	 * @param domainName
	 * @param machineName
	 * @param proxyUser
	 * @param proxyPassword
	 * 
	 * @throws Exception
	 */
	public static void doWork(URL address, 
							  String fileToUpload,
							  String folder,
							  String userAgro,
							  String pwdAgro,
							  int httpsPort,
							  String proxyHost, 
							  int proxyPort, 
							  String domainName, 
							  String machineName, 
							  String proxyUser, 
							  String proxyPassword, 
							  String verbose) throws Exception {
		HttpClient          httpClient = null;  // Objeto a través del cual realizamos las peticiones  
		PostMethod          postMethod = null;  // Objeto para realizar las peticiines HTTP GET o POST  
		
		// Instanciamos el objeto  
		httpClient = new HttpClient();  
		  
		try {
			
			logger.debug("INIT doWork ...");
			// Especificamos que salimos a través de un Proxy. Auth NTLM   
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
			logger.debug("verbose: " + verbose);
			if (verbose != null) logger.debug("Uploading " + fileToUpload + " to " + address.toString() + " ...");
			postMethod = new PostMethod(address.toString());

			// El fichero se sube por https
			Protocol httpsProtocol = new Protocol("https", ((ProtocolSocketFactory)new EasySSLProtocolSocketFactory()), httpsPort);
			httpClient.getHostConfiguration().setHost(address.getHost(), httpsPort, httpsProtocol);
			
			postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, Boolean.TRUE.booleanValue());
			
			File fileEtiqueta = new File(FilenameUtils.concat(folder, fileToUpload + ETIQUETA_EXTENSION));
			File fileDatos = new File(FilenameUtils.concat(folder, fileToUpload + DATOS_EXTENSION));
			
			// El servlet de Agroseguro requiere usuario y contraseña
			Part[] parts = {
					new StringPart("user", userAgro),
					new StringPart("password", pwdAgro),
                    new FilePart("etiqueta", fileEtiqueta.getName(), fileEtiqueta),
                    new FilePart("datos", fileDatos.getName(), fileDatos)
                };
                
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
	        
			logger.debug("postMethod: " + postMethod);
			
			byte[] response = null;
			try{
				
				
				int statusCode = 0;
				int controlIntentos = 0;
				boolean valor = false;
				do {
					controlIntentos = controlIntentos + 1;
					logger.debug("Iniciando el intento numero " + controlIntentos);
					
					Thread.sleep(SLEEP_TIME);
					
					statusCode = httpClient.executeMethod(postMethod);
					
					logger.debug("El resultado de la ejecucion de la transferencia es : " + statusCode);
					
					if (statusCode != HttpStatus.SC_OK) {
						logger.error("Error en FileUploader. Error code : " + statusCode);
						if(controlIntentos == NUMERO_INTENTOS) throw new Exception("Error code : " + statusCode);
					}
					
					response = postMethod.getResponseBody();
					if (new String(response).toUpperCase().indexOf("ERROR") != -1) {
						
						valor = true;
						
						logger.error("Error en FileUploader returned by Server: " + new String(response));
						if(controlIntentos == NUMERO_INTENTOS) throw new Exception("Error returned by Server: " + new String(response));		
					}
					
				} while (statusCode == HttpStatus.SC_OK && controlIntentos <= NUMERO_INTENTOS && valor);
				
				
			}catch(Exception e){
				logger.error("Error en FileUploader: " + e.getMessage());
				if (verbose != null) System.out.println("Error al conectar con Agroseguro para subir ficheros: " + e.getMessage());
				throw e;
			}
			if (verbose != null) System.out.println(new String(response));
		}
		catch (Exception e) {
			logger.error("Error en FileUploader: " + e.getMessage());
			System.err.println("Error\t: " + e.getMessage());  	               
			//e.printStackTrace();
			throw e;
		}  
	}
}
