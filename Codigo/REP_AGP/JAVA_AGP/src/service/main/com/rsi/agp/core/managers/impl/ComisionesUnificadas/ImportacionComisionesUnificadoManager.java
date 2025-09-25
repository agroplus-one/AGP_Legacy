package com.rsi.agp.core.managers.impl.ComisionesUnificadas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.XMLValidationException;
import com.rsi.agp.core.jmesa.dao.impl.IImportacionComisionesUnificadoDao;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.XmlComisionesValidationUtil;
import com.rsi.agp.core.util.ZipUtil;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroContenidoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ImportacionComisionesUnificadoManager implements IImportacionComisionesUnificadoManager {

	private static final Log LOGGER = LogFactory.getLog(ImportacionComisionesUnificadoManager.class);

	private IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao;
	private FicheroUnificadoGastosEntidadManager ficheroUnificadoGastosEntidadManager;

	public boolean esFicheroYaImportado(String nombreFichero) throws BusinessException {
		LOGGER.debug("init - ficheroImportado");
		boolean importado = false;

		try {
			importado = importacionComisionesUnificadoDao.existeFicheroImportado(nombreFichero);
		} catch (DAOException dao) {
			LOGGER.debug("Se ha producido un error al importar, fichero duplicado:" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al importar, fichero duplicado", dao);
		}
		LOGGER.debug("end - ficheroImportado");
		return importado;
	}

	public Map<String, Long> procesaFichero(MultipartFile file, Character tipoFichero, Usuario usuario,
			HttpServletRequest request) throws Exception {
		LOGGER.debug("init - procesaFichero");
		File newfile = null;
		String nombreFichero = null;
		nombreFichero = file.getOriginalFilename();
		LOGGER.debug("creamos un FILE temporal");
		newfile = File.createTempFile("xml", "temp");

		LOGGER.debug("transformamos nuestro multipartfile a file");
		file.transferTo(newfile);
		
		// En la carga automatica no hace falta idfichero por tanto a null
		return procesaFichero(newfile, nombreFichero, null, tipoFichero, usuario, request);
	}
	
	public Map<String, Long> procesaFichero(Blob blob,  String nombreFichero, Long idFichero, Character tipoFichero, Usuario usuario,
			HttpServletRequest request) throws Exception {
		LOGGER.debug("init - procesaFichero");
		
	    
		File file = File.createTempFile("xml", "temp");
		LOGGER.debug("Fichero creado en: " + file.getAbsolutePath());


		InputStream in = blob.getBinaryStream();
		try (OutputStream out = new FileOutputStream(file)) {
			out.write("<?xml version=\"1.0\" encoding=\"cp1252\"?>".getBytes());
			byte[] buff = new byte[4096];  // how much of the blob to read/write at a time
			int len = 0;
	
			while ((len = in.read(buff)) != -1) {
			    out.write(buff, 0, len);
			}			
	
			in.close();
			out.close();
			
			// Convertir a zip
			File zipFile = File.createTempFile("zip", "temp");	
			FileOutputStream fos = null;
	        ZipOutputStream zipOut = null;
			try (FileInputStream fis = new FileInputStream(file)) {
		        fos = new FileOutputStream(zipFile);
		        zipOut = new ZipOutputStream(fos);
		        ;
		        ZipEntry zipEntry = new ZipEntry(file.getName());
		        zipOut.putNextEntry(zipEntry);
		        byte[] bytes = new byte[1024];
		        int length;
		        while((length = fis.read(bytes)) >= 0) {
		            zipOut.write(bytes, 0, length);
		        }
			} finally {
				if (zipOut != null) zipOut.close();
				if (fos != null) fos.close();
			}	        
	        
	        return procesaFichero(zipFile, nombreFichero, idFichero, tipoFichero, usuario, request);			
		}
	}
	
	public Map<String, Long> procesaFichero(File file, String nombreFichero, Long idFichero, Character tipoFichero, Usuario usuario,

			HttpServletRequest request) throws Exception {

		LOGGER.debug("ImportacionComisionesUnificadoManager - procesaFichero - init");

		FicheroUnificado fichero = null;
		Map<String, Long> mapaIdAndError = new HashMap<String, Long>();
		XmlObject xml = null;
		try {

			actualizarBarraProgreso(request, "UPLOADING", 10);

			// Validamos esquema y nos traemos el xml
			LOGGER.debug("validación de esquema.");

			try {
				xml = this.getXmlObject(file, tipoFichero);
			} catch (XMLValidationException e) {
				if (tipoFichero.equals(new Character('C'))) {
					tipoFichero = new Character('U');
					xml = this.getXmlObject(file, tipoFichero);
				}
			}

			actualizarBarraProgreso(request, "UPLOADING", 55);


			// Creamos el campo Blob para asignarselo al FicheroUnificado
			Clob clob = Hibernate.createClob(xml.xmlText());
			Long lg = clob.length();
			Blob blob = Hibernate.createBlob(clob.getAsciiStream(), lg.intValue());

			LOGGER.debug("[procesaFichero] Comienza el procesamiento de fichero de tipo " + tipoFichero);

			// Importación del fichero - Carga de datos
			if (tipoFichero.equals(new Character('C'))) {
				fichero = this.importarGastosRecibosEmitidos(xml, usuario, tipoFichero, nombreFichero, blob);
			} else if (tipoFichero.equals(new Character('I'))) {
				fichero = this.importarGastosRecibosImpagados(xml, usuario, tipoFichero, nombreFichero, blob);
			} else if (tipoFichero.equals(new Character('D'))) {
				fichero = this.importarGastosCobroDeudaAplazada(xml, usuario, tipoFichero, nombreFichero, blob);
			} else if (tipoFichero.equals(new Character('U'))) {
				fichero = this.importarGastosEntidadUnificado(xml, usuario, tipoFichero, nombreFichero, idFichero, blob);
			}

			actualizarBarraProgreso(request, "UPLOADING", 85);
			
			if (fichero != null && null != fichero.getId()) {
				mapaIdAndError.put(Constants.CLAVE_ID_FICHERO, fichero.getId());
				// Facturación
				if (usuario != null) importacionComisionesUnificadoDao.saveOrUpdateFacturacion(fichero, usuario);
				// ValidarFichero
				if (null!=request) {
					importacionComisionesUnificadoDao.validarFicheroComisiones(fichero.getId(), tipoFichero);
				}
			}

		} catch (IOException ex) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al crear el fichero temporal :" + ex.getMessage());
			throw new BusinessException("Se ha producido un error al crear el fichero temporal", ex);
		} catch (DAOException dao) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al importar el fichero :" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al importar el fichero", dao);
		} catch (BusinessException be) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al importar el fichero :" + be.getMessage());
			throw new BusinessException("Se ha producido un error al importar el fichero", be);
		} catch (Exception e) {
			request.getSession().setAttribute("progressStatus", "FAILED");
			LOGGER.debug("Se ha producido un error al crear el fichero temporal :" + e.getMessage());
			throw new Exception("Se ha producido un error al crear el fichero temporal", e);
		}

		LOGGER.debug("ImportacionComisionesUnificadoManager - procesaFichero - end");

		return mapaIdAndError;
	}

	private XmlObject getXmlObject(File fichero, Character tipoFichero)
			throws BusinessException, XMLValidationException {
		LOGGER.debug("init - realizarValidacion");
		XmlObject xmlobj = null;
		try {
			File temp = ZipUtil.getFirstFileInZip(fichero);

			if (tipoFichero.equals(new Character('C'))) {
				xmlobj = XmlComisionesValidationUtil.getXMLBeanValidado(temp,
						Constants.FICHERO_UNIFICADO_GASTOS_RECIBOS_EMITIDOS);
			} else if (tipoFichero.equals(new Character('I'))) {
				xmlobj = XmlComisionesValidationUtil.getXMLBeanValidado(temp,
						Constants.FICHERO_UNIFICADO_GASTOS_RECIBOS_IMPAGADOS);
			} else if (tipoFichero.equals(new Character('D'))) {
				xmlobj = XmlComisionesValidationUtil.getXMLBeanValidado(temp,
						Constants.FICHERO_UNIFICADO_GASTOS_RECIBOS_DEUDA_APLAZADA);
			} else if (tipoFichero.equals(new Character('U'))) {
				xmlobj = XmlComisionesValidationUtil.getXMLBeanValidado(temp,
						Constants.FICHERO_UNIFICADO_GASTOS_ENTIDAD_UNIFICADO);
			}

		} catch (XMLValidationException xmle) {
			if (tipoFichero.equals(new Character('C'))) {
				throw xmle;
			} else {
				LOGGER.error("Se ha producido un error en validacion del fichero: " + xmle.getMessage());
				throw new BusinessException("Se ha producido un error en validacion del fichero.", xmle);
			}
		} catch (Exception be) {
			LOGGER.error("Se ha producido un error al tratar el zip : " + be.getMessage());
			throw new BusinessException("Se ha producido un error al tratar el zip", be);
		}

		LOGGER.debug("init - realizarValidacion");
		return xmlobj;
	}

	private FicheroUnificado importarGastosRecibosEmitidos(XmlObject xml, Usuario usuario, Character tipo,
			String nombre, Blob blob) throws Exception {
		FicheroUnificado f = null;
		FicheroUnificadoGastosRecibosEmtidosManager mg = null;

		try {
			mg = new FicheroUnificadoGastosRecibosEmtidosManager();
			f = mg.getFicheroUnificado(xml, usuario, tipo, nombre, blob);
			LOGGER.info("[importarGastosRecibosEmitidos] Objetos creados correctamente a partir del fichero.");
			// Guardar en BD
			importacionComisionesUnificadoDao.saveFicheroUnificado(f);
			return f;
		} catch (Exception e) {
			LOGGER.error(
					"Se ha producido un error al covertir el xml  de gastos de recibos emitidos en FicheroUnificado : "
							+ e.getMessage());
			throw new Exception("Error cargando el fichero de gastos de recibos emitidos de comisiones unificadas. ",
					e);
		}
	}

	private FicheroUnificado importarGastosRecibosImpagados(XmlObject xml, Usuario usuario, Character tipo,
			String nombre, Blob blob) throws Exception {

		FicheroUnificado f = null;
		FicheroUnificadoGastosRecibosImpagadosManager mg = null;
		try {
			mg = new FicheroUnificadoGastosRecibosImpagadosManager();
			f = mg.getFicheroUnificado(xml, usuario, tipo, nombre, blob);
			LOGGER.info("[importarGastosRecibosImpagados] Objetos creados correctamente a partir del fichero.");
			// Guardar en BD
			importacionComisionesUnificadoDao.saveFicheroUnificado(f);
			return f;
		} catch (Exception e) {
			LOGGER.error(
					"Se ha producido un error al covertir el xml de gastos de recibos impagados en FicheroUnificado : "
							+ e.getMessage());
			throw new Exception("Error cargando el fichero de gastos de recibos impagados de comisiones unificadas. ",
					e);
		}
	}

	private FicheroUnificado importarGastosCobroDeudaAplazada(XmlObject xml, Usuario usuario, Character tipo,
			String nombre, Blob blob) throws Exception {
		FicheroUnificado f = null;
		FicheroUnificadoGastosDeudaAplazadaManager mg = null;

		try {
			mg = new FicheroUnificadoGastosDeudaAplazadaManager();
			f = mg.getFicheroUnificado(xml, usuario, tipo, nombre, blob);
			LOGGER.info("[importarGastosCobroDeudaAplazada] Objetos creados correctamente a partir del fichero.");
			// Guardar en BD
			importacionComisionesUnificadoDao.saveFicheroUnificado(f);

			return f;
		} catch (Exception e) {
			LOGGER.error(
					"Se ha producido un error al covertir el xml  de gastos de deuda aplazada en FicheroUnificado : "
							+ e.getMessage());
			throw new Exception(
					"Error cargando el fichero de gastos de cobro de deuda aplazada de comisiones unificadas. ", e);
		}
	}

	private FicheroUnificado importarGastosEntidadUnificado(XmlObject xml, Usuario usuario, Character tipo,
			String nombre, Long idFichero, Blob blob) throws Exception {
		FicheroUnificado f = null;
		
		try {

			f = ficheroUnificadoGastosEntidadManager.getFicheroUnificado(xml, usuario, tipo, idFichero, nombre, blob);
			
			LOGGER.info("[importarGastosEntidadUnificado] Objetos creados correctamente a partir del fichero.");
			// Guardar en BD
			importacionComisionesUnificadoDao.saveFicheroUnificado(f);
						
			return f;
		} catch (Exception e) {
			LOGGER.error(
					"Se ha producido un error al covertir el xml de gastos de entidad unificado emitidos en FicheroUnificado : "
							+ e.getMessage());
			throw new Exception("Error cargando el fichero de gastos de entidad unificado. ", e);
		} finally {
			LOGGER.info("Fin proceso de xml  de gastos de entidad unificado en fichero >>" + nombre + "<< de tipo >>"
					+ tipo + "<<");
		}
	}

	public FicheroUnificado getFichero(Long idfichero) throws BusinessException {
		LOGGER.debug("init - ImportacionComisionesUnificadoManager.getFichero");
		FicheroUnificado fichero = null;
		try {
			fichero = (FicheroUnificado) importacionComisionesUnificadoDao.getObject(FicheroUnificado.class, idfichero);

		} catch (Exception ex) {
			LOGGER.debug("Se ha producido un error al recuperar el fichero importado :" + ex.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el fichero importado", ex);
		}
		LOGGER.debug("end - ImportacionComisionesUnificadoManager.getFichero");
		return fichero;
	}

	public void borrarFichero(FicheroUnificado fichero, Usuario usuario) throws BusinessException {
		LOGGER.debug("init - ImportacionComisionesUnificadoManager.borrarFichero");
		try {
			// Método genérico que borra el objeto y registra el usuario
			importacionComisionesUnificadoDao.deleteFacturacion(fichero, usuario);
		} catch (DAOException dao) {
			LOGGER.debug("Se ha producido un error al borrar el fichero seleccionado :" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al borrar el fichero seleccionado", dao);
		}
		LOGGER.debug("end - ImportacionComisionesUnificadoManager.borrarFichero");
	}

	public FicheroContenidoUnificado getFicheroContenido(Long idfichero) throws Exception {
		LOGGER.debug("init - ImportacionComisionesUnificadoManager.getFicheroContenido");
		FicheroContenidoUnificado fichero = null;
		try {
			fichero = (FicheroContenidoUnificado) importacionComisionesUnificadoDao
					.getObject(FicheroContenidoUnificado.class, idfichero);

		} catch (Exception ex) {
			LOGGER.debug("Se ha producido un error al recuperar el fichero importado :" + ex.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el fichero importado", ex);
		}
		LOGGER.debug("end - ImportacionComisionesUnificadoManager.getFicheroContenido");
		return fichero;
	}

	@Override
	public void actualizaEstadoFichero(Long idFichero, Character estado) throws BusinessException {
		LOGGER.debug("init - ImportacionComisionesUnificadoManager.actualizaEstadoFichero");
		try {

			FicheroUnificado fichero = this.getFichero(idFichero);
			fichero.setEstado(estado);
			importacionComisionesUnificadoDao.saveOrUpdate(fichero);

		} catch (DAOException dao) {
			LOGGER.debug("Se ha producido un error al actualizar el estado del fichero :" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al actualizar el estado del fichero " + "", dao);
		}
		LOGGER.debug("end - ImportacionComisionesUnificadoManager.borrarFichero");

	}

	public void setImportacionComisionesUnificadoDao(
			IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao) {
		this.importacionComisionesUnificadoDao = importacionComisionesUnificadoDao;
	}
	
	public void setFicheroUnificadoGastosEntidadManager(
			FicheroUnificadoGastosEntidadManager ficheroUnificadoGastosEntidadManager) {
		this.ficheroUnificadoGastosEntidadManager = ficheroUnificadoGastosEntidadManager;
	}
	
	
	/**
	 * 
	 * @param request
	 * @param estado
	 * @param valor
	 */
	public void actualizarBarraProgreso(HttpServletRequest request, String estado, int valor) {

		if (null!=request) {
			request.getSession().setAttribute("progressStatus", estado);
			request.getSession().setAttribute("progress", valor);
		}
	}
}