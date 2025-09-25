package com.rsi.agp.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider; 

import sun.security.jca.ProviderList;
import sun.security.jca.Providers;

/* P79439 RQ.01 Muchas modificaciones */
public class FTPDownloader {

	private static final Log logger = LogFactory.getLog(FTPDownloader.class);
	
	public final String FTPSSLPROTOCOLS = "ftpsslprotocols";
	public final String FTPS_FLAG = "ftps_flag";

	private FTPClient client;
	private boolean logged;
	
	private static ResourceBundle bundle = ResourceBundle.getBundle("agp_coms_auto");
	
	public FTPDownloader() {
		this(null);
	}
	
	public FTPDownloader(Boolean ftps_flag) {
		logger.debug("FTPDownloader() - init");
		if (ftps_flag == null) {
			ftps_flag = Boolean.valueOf(bundle.getString(FTPS_FLAG)); 
		}		
		if (ftps_flag) {
			ProviderList list = Providers.getFullProviderList();
			list = ProviderList.insertAt(list, new BouncyCastleProvider(), 0);
			list = ProviderList.insertAt(list, new BouncyCastleJsseProvider(), 1);
			Providers.setProviderList(list);
			Security.addProvider(new BouncyCastleProvider());
			Security.addProvider(new BouncyCastleJsseProvider());
			try {
				this.client = new FTPSClient();
				String ftpsslprotocols = bundle.getString(FTPSSLPROTOCOLS);
				((FTPSClient) this.client).setEnabledProtocols(new String[] { ftpsslprotocols });
			} catch (NoSuchAlgorithmException e) {
				logger.error(e);
			}
		} else {
			this.client = new FTPClient();
		}
		this.client.setConnectTimeout(100000);
		logger.debug("FTPDownloader() - end");
	}

	/**
	 * 
	 * @param pattern
	 * @return
	 * @throws IOException 
	 */
	public String getLastFileNameByPattern(final String pattern, final String urlFtp) throws IOException {

		String rutaCompleta = "";
		logger.debug("FTPDownloader - getLastFileNameByPattern - init");
		try {
			String dirToLook = bundle.getString("directoriosRemotos");
			String rutaDirTemp = bundle.getString("directorioLocal");
				// Conectamos con el buzon de PRE
				connect(new URL(urlFtp), false);
				org.apache.commons.net.ftp.FTPFile[] files = this.client.listFiles(dirToLook);
				if (null != files && files.length != 0) {
					// Ordenamos por timestamp desc
					Arrays.sort(files, CMP);

					// Creamos una carpeta temporal para descargar los ficheros que necesitemos
					crearCarpetaTemporalParaDescarga(rutaDirTemp);

					// Recorremos la lista de ficheros
					for (int j = 0; j < files.length; j++) {
						org.apache.commons.net.ftp.FTPFile file = files[j];
						int type = file.getType();
						logger.debug(file.getName());
						if (type == org.apache.commons.net.ftp.FTPFile.FILE_TYPE
								&& (file.getName().endsWith(".txt") || file.getName().endsWith(".TXT"))) {

							rutaCompleta = rutaDirTemp + file.getName();
							logger.debug("descargando fichero :" + rutaCompleta);
							try (FileOutputStream fos = new FileOutputStream(rutaCompleta)) {
								// Descargamos el fichero
								this.client.retrieveFile(dirToLook + file.getName(), fos);
							}
							
							File tempFile = new File(rutaCompleta);
							// Si se ha descargado ok
							if (tempFile.exists()) {
								logger.debug(file.getName() + " descargado");
								// Si el contenido del fichero cumple el patron pattern
								if (comprobarPatron(rutaCompleta, pattern)) {
									return dirToLook + file.getName();
								}
								// Borramos el fichero temporal y continuamos con el recorrido
								Files.delete(Paths.get(rutaCompleta));
							}
						}
					}
				}
		} finally {
			disconnect();
		}

		return "";
	}

	public void download(String urlFtp, String remoteUrl, String localFile) throws Exception {
		logger.debug("FTPDownloader - download - init");
		connect(new URL(urlFtp), false);		
		try (FileOutputStream out = new FileOutputStream(localFile)) {
			logger.debug("descargando fichero :" + localFile);
			this.client.retrieveFile(remoteUrl, out);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la descarga del fichero " + localFile, e);
			throw e;
		} finally {
			disconnect();
		}
		logger.debug("FTPDownloader - download - end");
	}

	private void connect(URL url, boolean useProxy) throws IOException {

		logger.debug("FTPDownloader - connect con URL - init");

		// Login credentials
		String user = null;
		String password = null;
		String userInfo = url.getUserInfo();
		if (userInfo != null) {
			int separator = userInfo.indexOf(':');
			if (separator != -1) {
				user = userInfo.substring(0, separator);
				password = userInfo.substring(separator + 1);
			} else {
				user = userInfo;
			}
		}
		connect(url.getHost(), url.getPort(), user, password, useProxy);

		logger.debug("FTPDownloader - connect con URL - end");

	}

	private void connect(String host, int port, String user, String password, boolean useProxy)
			throws IOException {

		logger.debug("FTPDownloader - connect - init");

		// Validate FTP host
		if (host == null) {
			throw new IOException("No encuentra el host");
		}
		// Connect
		try {
			conecta(host, port, user, password);
			this.client.enterLocalPassiveMode();
		} catch (IOException e) {
			disconnect();
			throw e;
		}
		logger.debug("FTPDownloader - connect - end");
	}

	private void disconnect() {

		logger.debug("FTPDownloader - disconnect  - init");

		if (this.logged) {
			try {
				this.client.logout();
			} catch (Exception ignore) {
			}
			this.logged = false;
		}
		if (this.client.isConnected()) {
			try {
				this.client.disconnect();
			} catch (Exception ignore) {
			}
		}

		logger.debug("FTPDownloader - disconnect - end");

	}

	private void conecta(String host, int port, String user, String password) throws IOException {

		logger.debug("FTPDownloader - conectaSinProxy  - init");
		logger.debug("host: " + host + ":" + port);
		logger.debug("user: " + user);
		
		// Connect to Host
		int reply = -1;
		if (port != -1) {
			this.client.connect(host, port);
		} else {
			this.client.connect(host);
		}

		this.client.setFileType(FTP.BINARY_FILE_TYPE);
		reply = this.client.getReplyCode();
		if (isError(reply)) {
			throw new IOException("Imposible conectar a  " + host + ". Respuesta: " + reply);
		}
		// Login
		if (user != null) {
			boolean success = this.client.login(user, password);
			if (!success) {
				throw new IOException("Usuario o password no valido. Respuesa: " + reply);
			}
		}
		this.logged = true;

		logger.debug("FTPDownloader - conectaSinProxy  - end");
	}

	private boolean isError(int reply) {
		return (!(FTPReply.isPositiveCompletion(reply) || FTPReply.isPositiveIntermediate(reply)));
	}
	
	/**
	 * Metodo que crea un directorio para realizar la descarga de ficheros
	 * 
	 * @param rutaDirTemp
	 *            ruta temporal para la descarga
	 */
	private void crearCarpetaTemporalParaDescarga(String rutaDirTemp) {

		logger.debug("FTPDownloader - crearCarpetaTemporalParaDescarga  - init");

		File file = new File(rutaDirTemp);
		if (!file.exists()) {
			file.mkdirs();
		}

		logger.debug("FTPDownloader - crearCarpetaTemporalParaDescarga  - end");
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean comprobarPatron(String rutaFichero, String pattern) throws IOException {

		BufferedReader bd = null;
		final StringBuilder contents = new StringBuilder();

		// Miramos si dentro se cumple el patron
		bd = new BufferedReader(new FileReader(rutaFichero));
		while (bd.ready()) {
			contents.append(bd.readLine());
		}
		bd.close();

		final String stringContents = contents.toString();

		logger.debug("CONTENIDO: " + stringContents + "\n");
		
		boolean cumplePatron = stringContents.matches(pattern);
		logger.debug("cumplePatron: " + cumplePatron);		

		return cumplePatron;
	}
	
	public List<String> getListFiles(String remoteUrl, String dir)throws IOException {
		logger.info("getListFiles");
		ArrayList<String> fileNames = new ArrayList<String>();
		connect(new URL(remoteUrl), false); // PROXY FALSE
		//ftp.connect(new URL(remoteUrl), true); // PROXY TRUE
		try {
			org.apache.commons.net.ftp.FTPFile[] files = (dir == null || dir.length() == 0) ? this.client.listFiles(): this.client.listFiles(dir);
			if (files == null || files.length == 0) {
				return null;
			} else {
				for (int i = 0; i < files.length; i++) {
					org.apache.commons.net.ftp.FTPFile file = files[i];
					int type = file.getType();
					if (type == org.apache.commons.net.ftp.FTPFile.FILE_TYPE) {
						fileNames.add(file.getName());
					}
				}
			}
		} finally {
			disconnect();
		}
		return fileNames;
	}

	private final Comparator<org.apache.commons.net.ftp.FTPFile> CMP = new Comparator<org.apache.commons.net.ftp.FTPFile>() {
		
		@Override
		public int compare(final org.apache.commons.net.ftp.FTPFile f1, final org.apache.commons.net.ftp.FTPFile f2) {
			return f1.getTimestamp().compareTo(f2.getTimestamp());
		}
	};
}