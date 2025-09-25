package com.rsi.agp.batch;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author root
 *
 */
public class FileDownloader {

	
	private static final int DEFAULT_HTTPS_PORT = 443;
	private static final int DEFAULT_PROXY_PORT = 8080;
	
	private static final String ADDRESS_STR = "address";
	private static final String DESTDIR_STR = "destdir";
	private static final String FILENAME_STR = "filename";
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
		String			  destDir          = null;
		String			  proxyHost        = null;
		int 			  proxyPort        = DEFAULT_PROXY_PORT;
		int 			  httpsPort        = DEFAULT_HTTPS_PORT;
		String            domainName       = null;
		String            machineName      = null;
		String            proxyUser        = null;
		String            proxyPassword    = null;
		String 			  filename		   = null;
		String			  userAgro		   = null;
		String			  pwdAgro	       = null;
		String 			  verbose		   = null;
		boolean			  disableOverwrite = false;
		CommandLineParser parser           = null;  
		CommandLine       cmdLine          = null;  
		
		///////////////////////////////////////////////////////////////////////  
		// Fase 1: Configuramos las opciones de validacion de entrada.  
		///////////////////////////////////////////////////////////////////////  
		               
		Options options = new Options();  
		options.addOption(ADDRESS_STR,                true, "URL del fichero a descargar (obligatorio)");  
		options.addOption(DESTDIR_STR,                true, "Directorio de destino donde almacenar el archivo descargado. Por defecto se usa el directorio actual.");  
		options.addOption(FILENAME_STR,               true, "Nombre del archivo a descargar (obligatorio)");  
		options.addOption(USERAGRO_STR,      			true, "Usuario para la conexion al servlet de envio de ficheros de AgroSeguro (obligatorio)");  
		options.addOption(PWDAGRO_STR,  			true, "Contrasenha para la conexion al servlet de envio de ficheros de AgroSeguro (obligatorio)"); 
		options.addOption(PROXYHOST_STR,              true, "IP del servidor proxy (opcional)");  
		options.addOption(PROXYPORT_STR,              true, "Puerto del servidor proxy (opcional)");  
		options.addOption(DOMAIN_STR,             true, "Nombre del dominio si la autentificacion por proxy es NTLM (opcional)");  
		options.addOption(MACHINE_STR,            true, "Nombre de la maquina si la autentificacion por proxy es NTLM (opcional)");
		options.addOption(HTTPSPORT_STR,              true, "Puerto HTTPS para la conexion (opcional, por defecto se utiliza el puerto 443)");
		options.addOption(PROXYUSER_STR,              true, "Usuario del proxy (opcional)"); 
		options.addOption(PROXYPWD_STR,          true, "Password para el usuario del proxy (opcional)");  
		options.addOption("verbose",          		true, "Muestra mensajes internos del estado del proceso (opcional)"); 
		options.addOption("do", "disableOverwrite", false, "Por defecto, si el fichero existe lo sobreescribe. Habilitando esta opcion se mantiene el fichero antiguo.");   
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
				new HelpFormatter().printHelp(FileDownloader.class.getName(), options );  
			    return;  
			}  
			
			// Se comprueba si esta la opcion de disableOverwrite  
			if (cmdLine.hasOption("do")){    // No hace falta preguntar por el parametro "disableOverwrite". Ambos son sinonimos
				disableOverwrite = true;
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
			
			// Si el usuario ha especificado el directorio de destino lo leemos          
			if (cmdLine.hasOption(DESTDIR_STR)){  
			    destDir = cmdLine.getOptionValue(DESTDIR_STR);    
			} else {  
			    destDir = "."; // Current dir 
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
			
			// Si el usuario ha especificado el nombre del fichero de destino lo leemos          
			if (cmdLine.hasOption(FILENAME_STR)){  
			    filename = cmdLine.getOptionValue(FILENAME_STR);    
			}  
			else {
				filename = null;
			}
			
			if (filename == null){  
			    throw new org.apache.commons.cli.ParseException("El fichero a descargar de AgroSeguro (-filename <arg>) es requerido");  
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
			
			// Downloading file...
			if (FileDownloader.doWork(address, 
									  proxyHost, 
									  proxyPort, 
									  destDir,
									  filename,
									  userAgro,
									  pwdAgro,
									  httpsPort,
									  domainName, 
									  machineName, 
									  proxyUser, 
									  proxyPassword,
									  disableOverwrite, 
									  verbose) == 0) {	               
				if (verbose != null) System.out.println("OK! File Succesfully Downloaded.");
				
				System.exit(0);
			}
			               
		} catch (org.apache.commons.cli.ParseException ex){ 
		    new HelpFormatter().printHelp(FileDownloader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(1);
		} catch (java.net.MalformedURLException ex){  
		    new HelpFormatter().printHelp(FileDownloader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(2);
		} catch (java.lang.NumberFormatException ex){  
		    new HelpFormatter().printHelp(FileDownloader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(3);
		} catch (Exception ex){  
		    new HelpFormatter().printHelp(FileDownloader.class.getName(), options );    // Error, imprimimos la ayuda
		    System.exit(4);
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
	 * @throws Exception
	 */
	public static int doWork(URL address, 
							  String proxyHost, 
							  int proxyPort, 
							  String destDir,
							  String filename,
							  String userAgro, 
							  String pwdAgro,
							  int httpsPort,
							  String domainName,
							  String machineName,
							  String proxyUser, 
							  String proxyPassword,
							  boolean disableOverwrite,
							  String verbose) throws Exception {
		HttpClient          httpClient = null;  // Objeto a traves del cual realizamos las peticiones  
		HttpMethodBase      request    = null;  // Objeto para realizar las peticiines HTTP GET o POST  
		int                 status     = 0;     // Codigo de la respuesta HTTP  
		String              localFile  = null;
		
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
			if (proxyUser != null && !proxyUser.equals("") && domainName == null) {
				Credentials credentials = new UsernamePasswordCredentials(proxyUser, proxyPassword); 
				AuthScope authScope = new AuthScope(proxyHost, proxyPort); 
				httpClient.getState().setProxyCredentials(authScope, credentials); 				
			}
			
			// El fichero de AgroSeguro se descarga por https
			Protocol httpsProtocol = new Protocol("https", ((ProtocolSocketFactory)new EasySSLProtocolSocketFactory()), httpsPort);
			httpClient.getHostConfiguration().setHost(address.getHost(), httpsPort, httpsProtocol);
			
		    // Si no se ha indicado el filename, se obtiene solo el nombre del fichero
			if (filename == null || filename.equals(""))
				localFile = FileDownloader.getFileNameOnly(address);
			else localFile = filename;
			if (verbose != null) System.out.println("Downloading " + localFile + " from " + address.toString() + " ...");
			
			request = new GetMethod(address.toString());  
			// Le indicamos que realize automaticamente el seguimiento de las redirecciones   
			// en caso de que existan.  
			request.setFollowRedirects(true);
			
			// Indicamos reintente 3 veces en caso de que haya errores.  
			request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,   
			                      new DefaultHttpMethodRetryHandler(3, true));
			
			// Se anhaden los parametros
			NameValuePair[] params = new NameValuePair[4];
			params[0] = new NameValuePair("user", userAgro);
			params[1] = new NameValuePair("password", pwdAgro);
			params[2] = new NameValuePair("action", "GET");
			params[3] = new NameValuePair("file", filename);
			request.setQueryString(params); 
			
			if (verbose != null) System.out.println("Llamando a AgroSeguro: action=GET, file=" + filename);
			
			// Leemos el codigo de la respuesta HTTP que nos devuelve el servidor  
			status = httpClient.executeMethod(request);  

			// Vemos si la peticion se ha realizado satisfactoriamente  
			if (status != HttpStatus.SC_OK) {  
				System.err.println("Error\t" + request.getStatusCode() + "\t" +   
			                                   request.getStatusText() + "\t" + request.getStatusLine());
				return -1;
			} else {  
			    // Leemos el contenido de la respuesta y realizamos el tratamiento de la misma,  
			    // guardando el fichero en destdir  
				try (BufferedInputStream bistream = new BufferedInputStream(request.getResponseBodyAsStream())) {
					FileDownloader.writeFile(bistream, destDir, localFile, request.getRequestCharSet(),
							disableOverwrite, verbose);
				}
			    return 0;
			}
		}
		catch (Exception e) {
			System.err.println("Error\t: " + e.getMessage());  	               
			e.printStackTrace();
			throw e;
		} finally {  
		    // Liberamos la conexion. (Tambien libera los stream asociados)  
			if (request != null)
				request.releaseConnection();  
		}  
	}
	
	/**
	 * Escribe el fichero en el directorio indicado
	 * 
	 * @param reader
	 * @param destDir
	 * @param fileName
	 * @param contentLength
	 * 
	 * @throws IOException, FileNotFoundException
	 */
	public static void writeFile(BufferedInputStream bistream, String destDir, String fileName, String charset,
			boolean disableOverwrite, String verbose) throws IOException {
		File destDirFile = new File(destDir);
		if (!destDirFile.exists()) {
			FileUtils.forceMkdir(destDirFile);
		}
		File aFile = new File(FilenameUtils.concat(destDir, fileName));
		if (verbose != null)
			System.out.println("Escribiendo el fichero " + aFile.getName());
		// Se comprueba si existe el fichero. Si existe se elimina
		if (aFile.exists()) {
			if (disableOverwrite) {
				if (verbose != null)
					System.out.println("File " + fileName + " already exists. Overwrite disabled.");
				return;
			}
			boolean result = aFile.delete();
			System.out.println("result: " + result);
		}

		// Se crea el fichero de salida
		try (FileOutputStream outputFile = new FileOutputStream(aFile, true)) {
			byte[] buffer = new byte[1024];
			int bytesRead = -1;
			int bytesReadCount = 0;
			String text = "";
			if (verbose != null)
				System.out.print("Downloaded ");
			while ((bytesRead = bistream.read(buffer)) != -1) {
				outputFile.write(buffer, 0, bytesRead);
				bytesReadCount += bytesRead;
				text = String.valueOf(bytesReadCount) + " bytes...";
				if (verbose != null)
					System.out.print(text);
				for (int i = 0; i < text.length(); i++)
					if (verbose != null)
						System.out.print("\b");
			}
		}
	}
	
	/**
	 * Dada una URL devuelve el nombre del fichero
	 * 
	 * @param address
	 * @return
	 */
	private static String getFileNameOnly(URL address) {

		String localFile = null;
	    StringTokenizer st = new StringTokenizer(address.getFile(), "/");
	    while (st.hasMoreTokens()) {
	    	localFile = st.nextToken();
	    }
		return localFile;
	}
}
