package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.tables.admin.Entidad;


public class EntidadManager implements IManager {

	private IEntidadDao entidadDao;
	private static final Log LOGGER = LogFactory.getLog(EntidadManager.class); 
	
	public final String getEntidadesGrupo(String idGrupo){
		String resultado = "";
		try {
			resultado = StringUtils.toValoresSeparadosXComas(entidadDao.getListaEntidadesGrupo(idGrupo), false, false);
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al recuperar el grupo de entidades ",e);
		}
		return resultado;
	}
	
	public final List<BigDecimal> getListaEntidadesGrupo(String idGrupo){
		List<BigDecimal> resultado = null;
		try {
			resultado = entidadDao.getListaEntidadesGrupo(idGrupo);
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al recuperar el grupo de entidades ",e);
		}
		return resultado;
	}
	
	public String getEntidadesGrupoCRM (){
		String listaEntCRM = "";
		try{
			listaEntCRM = entidadDao.getEntidadesGrupoCRM();
		}catch(Exception ex){
			LOGGER.fatal("(getIdGrupoCRM)Error lectura BD", ex);
		}
		return listaEntCRM;
	}
	
	
	public String getIdGrupo(BigDecimal codEntidadUsuario) throws BusinessException{
		String idGrupo = "";
		try{
		
			idGrupo = entidadDao.getIdGrupoEntidad(codEntidadUsuario);
		
		}catch(Exception ex){
			LOGGER.fatal("(getIdGrupo)Error lectura BD", ex);
		}
		return idGrupo;
	}

	public Entidad getEntidad(BigDecimal codEntidad) throws BusinessException{
		
		Entidad entidad = null;
		try{
			if(codEntidad!=null)
				entidad = (Entidad)entidadDao.get(Entidad.class, codEntidad);
		
		}catch(Exception ex){
			LOGGER.fatal("(getIdGrupo)Error lectura BD", ex);
		}
		return entidad;
	}
	
	public List<Entidad> obtenerListaEntidadesByArrayCodEntidad(List<BigDecimal> listaCodEntidad) throws BusinessException{
		
		List<Entidad> listaEntidades = null;
		
		try{
			listaEntidades = entidadDao.obtenerListaEntidadesByArrayCodEntidad(listaCodEntidad);
		}catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al recuperar el listado de entidades: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el listado de entidades",dao);
		}
		return listaEntidades;		
	}
	
	public boolean isCRM(BigDecimal codentidad){
		return this.entidadDao.isCRM(codentidad);
	}
	
	public boolean isCRAlmendralejo(BigDecimal codentidad){
		return this.entidadDao.isCRAlmendralejo(codentidad);
	}
	
	public void setEntidadDao(IEntidadDao entidadDao) {
		this.entidadDao = entidadDao;
	}
	

	public String getIdGrupoOficina(BigDecimal codEntidad,BigDecimal codOficina) throws BusinessException{
		String idGrupo = "";
		try{
		
			idGrupo = entidadDao.getIdGrupoOficina(codEntidad,codOficina);
		
		}catch(Exception ex){
			LOGGER.fatal("(getIdGrupo)Error lectura BD", ex);
		}
		return idGrupo;
	}
	
	public List<BigDecimal> getListaOficinasGrupo(BigDecimal codzona, BigDecimal entidad){
		 List<BigDecimal>  resultado=null;
		try {
			resultado = entidadDao.getListaOficinasGrupo(codzona, entidad);
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al recuperar el grupo de entidades ",e);
		}
		return resultado;
	}
	
	
}
