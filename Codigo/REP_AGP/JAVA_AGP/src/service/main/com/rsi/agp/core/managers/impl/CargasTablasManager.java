package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.imp.ICargasTablasDao;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;
import com.rsi.agp.dao.tables.cargas.CargasTablas;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;

public class CargasTablasManager implements IManager{
	
	
	private Log logger = LogFactory.getLog(getClass());
	private ICargasTablasDao cargasTablasDao;
	
	public List<CargasTablas> getTablasAndSave(MultipartFile file,Long idFichero) throws BusinessException,Exception {
		
		CargasTablas cargasTablas = null;
		List<CargasTablas> listTablas = new ArrayList<CargasTablas>();
		
		try {
			logger.info("CargasTablasManager - getTablas");
			logger.info("Recuperamos el contenido del fichero txt");
			InputStream input = file.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(input, writer);
			String text = writer.toString();
			
			//De la cadena obtenida del txt nos quedamos con la parte entre parentesis,
			//que corresponde a los numeros de las tablas
			logger.info("Contenido del TXT: '" + text + "'");
			String arrayString[] = text.split("\\(");
			String codsTablas[] = arrayString[1].split("\\)");
			logger.info("Contenido de tablas del TXT: '" + codsTablas[0] + "'");
			String cods[] = codsTablas[0].split(",");
			
			logger.info("Guardamos en bbdd todas las tablas asociadas al idfichero: " + idFichero+ " con alta = N");
			
			for (int i = 0; i < cods.length; i++) {
				TablaCondicionado tablaCondicionado = cargasTablasDao.getTabla(cods[i].trim());
				if (tablaCondicionado != null && tablaCondicionado.getCodtablacondicionado() != null){
					cargasTablas = new CargasTablas();
					cargasTablas.setNumtabla(tablaCondicionado.getCodtablacondicionado());
					cargasTablas.setFicheroxml(tablaCondicionado.getDestablacondicionado());
					cargasTablas.setAlta(new Character('N'));
					CargasFicheros cf = new CargasFicheros();
					cf.setId(idFichero);
					cargasTablas.setCargasFicheros(cf);
					
					cargasTablasDao.saveOrUpdate(cargasTablas);
					
					listTablas.add(cargasTablas);
				}
			}
			
		} catch (IOException e) {
			logger.error("error al leer el fichero txt",e);
			throw e;
		} catch (DAOException e) {
			logger.error("error al acceder a la bbdd",e);
			throw e;
		} catch (Exception e) {
			logger.error("error indefinido",e);
			throw e;
		}
		return listTablas;
	}

	

	public void saveTablasSeleccionadas(String[] tablasSel, Long idFichero, String[] arrayTablas) throws BusinessException {
		
		logger.info("CargasTablasManager - saveTablas");
		CargasTablas cargasTablas = null;
		HashMap<BigDecimal, String> auxTablas = new HashMap<BigDecimal, String>();
		CargasFicheros cargasFicheros = new CargasFicheros();
		List <CargasTablas> listaFinal = new ArrayList<CargasTablas>();
		
		
		try {
			//Borramos todas las tablas para volver a insertar las que el usuario a marcado
			cargasTablasDao.deletebyIdFichero(idFichero);
			//Guardamos en un hashmap las tablas seleccionadas para poder acceder mas rapidamente a ellas
			auxTablas = copiaTablas (tablasSel);
			// con el array de tablas recogido de la jsp generamos una lista con todas las tablas del fichero
			listaFinal = generaListaTablas(arrayTablas);
			//Asignamos el idFichero 
			cargasFicheros.setId(idFichero);
			
			for (int i = 0; i < listaFinal.size(); i++) {
				
				CargasTablas aux = new CargasTablas();
				aux = (CargasTablas) listaFinal.get(i);
				
				cargasTablas = new CargasTablas();
				cargasTablas.setCargasFicheros(cargasFicheros);
				cargasTablas.setNumtabla(aux.getNumtabla());
				cargasTablas.setFicheroxml(aux.getFicheroxml());
				if (auxTablas.containsKey(aux.getNumtabla()))
					cargasTablas.setAlta(new Character('S'));
				else
					cargasTablas.setAlta(new Character('N'));
				
				cargasTablasDao.saveOrUpdate(cargasTablas);
			}
			
		} catch (DAOException e) {
			logger.error("error al guardar las tablas en bbdd",e);
			throw new BusinessException();
		} catch (Exception ex){
			logger.error("error generico en bbdd",ex);
			throw new BusinessException();
		}
		
	}

	private HashMap<BigDecimal, String> copiaTablas(String[] tablasSel) {
		
		HashMap<BigDecimal, String> auxTablas = new HashMap<BigDecimal, String>();
		
		for (int i = 0; i < tablasSel.length; i++) {
			String[] codName = tablasSel[i].split("-");
			auxTablas.put(new BigDecimal(codName[0]), codName[1].trim());
		}
		return auxTablas;
	}
	
	private List<CargasTablas> generaListaTablas(String[] arrayTablas){
		
		List <CargasTablas> listaFinal = new ArrayList<CargasTablas>();
		
		CargasTablas c = null;
		
		String valor[] =arrayTablas[0].split(";");
		for (int i =0; i<valor.length;i++){   // num-nombre;num-nombre;....
			if (valor[i]!= null){
				String aux[] = valor[i].split("-");
				if (aux!= null){ 
					c = new CargasTablas();
					c.setNumtabla(new BigDecimal(aux[0]));
					c.setFicheroxml(aux[1]);
					
					listaFinal.add(c);
				}
			}
		}
		return listaFinal;
	}


	public List<CargasTablas> getTablasBBDD(String idFichero) {
		
		List <CargasTablas>listTablas = new ArrayList<CargasTablas>();
		listTablas = cargasTablasDao.getTablasbyId(Long.parseLong(idFichero));
		
		return listTablas;
	}

	
	public String getNombreFichero(String idFichero) {
		CargasFicheros cargasFicheros = null;
		cargasFicheros = (CargasFicheros) cargasTablasDao.getObject(CargasFicheros.class, Long.parseLong(idFichero));
		return cargasFicheros.getFichero();
	}
	
	public void setCargasTablasDao(ICargasTablasDao cargasTablasDao) {
		this.cargasTablasDao = cargasTablasDao;
	}




}
