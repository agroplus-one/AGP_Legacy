package com.rsi.agp.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtils {

	private static final Log logger = LogFactory.getLog(SFTPUtils.class);

	// For FTP server
	private String hostName;
	private String hostPort;
	private String userName;
	private String passWord;
	private String destinationDir;

	// For sFTP server
	private ChannelSftp channelSftp = null;
	private Session session = null;
	private Channel channel = null;

	private int userGroupId = 0;

	public SFTPUtils() {
		// Empty Method
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getDestinationDir() {
		return destinationDir;
	}

	public void setDestinationDir(String destinationDir) {
		this.destinationDir = destinationDir;
	}

	public int getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	private void initChannelSftp() {
		channelSftp = null;
		session = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(userName, hostName, Integer.valueOf(hostPort));
			session.setPassword(passWord);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public List<String> listFiles() {
		List<String> result = null;
		initChannelSftp();
		try {
			if (!session.isConnected())
				session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			@SuppressWarnings("unchecked")
			Vector<LsEntry> v = channelSftp.ls(destinationDir);	
			result = new ArrayList<String>(v.size());
			for (LsEntry obj : v) {
				result.add(obj.getFilename());
			}
		} catch (Exception ex) {
			logger.error(ex);
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		return result;
	}

	/*
	 * Upload file to ftp server that has configuration on sysConfig.properties
	 * filename: name of file that will be stored on ftp fis: input stream of file
	 * that will be stored on ftp enableLog: enable log return value: URN
	 */
	public String uploadFileToFTP(String filename, InputStream fis) {
		String result = "";
		initChannelSftp();
		try {
			if (!session.isConnected())
				session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			try {
				channelSftp.cd(destinationDir);
			} catch (SftpException e) {
				channelSftp.mkdir(destinationDir);
				channelSftp.cd(destinationDir);
			}
			channelSftp.put(fis, filename);
			logger.info("Upload successful portfolio file name:" + filename);
			result = String.format("sftp://%s/%s/%s", hostName, destinationDir, filename);
			channelSftp.exit();
		} catch (Exception ex) {
			logger.error(ex);
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		return result;
	}

	public String uploadFileToFTP(String desFileName, String srcFilePath) throws IOException {
		String result = "";
		try (InputStream fis = new FileInputStream(srcFilePath)) {
			result = uploadFileToFTP(desFileName, fis);
		} catch (Exception ex) {
			logger.error(ex);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public boolean checkExist(String fileName) {
		boolean existed = false;
		initChannelSftp();
		try {
			if (!session.isConnected())
				session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			try {
				channelSftp.cd(destinationDir);
			} catch (SftpException e) {
				channelSftp.mkdir(destinationDir);
				channelSftp.cd(destinationDir);
			}
			Vector ls = channelSftp.ls(destinationDir);
			if (ls != null) {
				// Iterate listing.
				logger.info(fileName);
				for (int i = 0; i < ls.size(); i++) {
					LsEntry entry = (LsEntry) ls.elementAt(i);
					String file_name = entry.getFilename();
					if (!entry.getAttrs().isDir()) {
						if (fileName.toLowerCase().startsWith(file_name)) {
							existed = true;
						}
					}
				}
			}

			channelSftp.exit();
		} catch (Exception ex) {
			existed = false;
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		return existed;
	}

	public void deleteFile(String fileName) {
		initChannelSftp();
		try {
			if (!session.isConnected())
				session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			try {
				channelSftp.cd(destinationDir);
			} catch (SftpException e) {
				channelSftp.mkdir(destinationDir);
				channelSftp.cd(destinationDir);
			}
			channelSftp.rm(fileName);
			channelSftp.exit();
		} catch (Exception ex) {
			logger.info(ex.getMessage());
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
	}
}