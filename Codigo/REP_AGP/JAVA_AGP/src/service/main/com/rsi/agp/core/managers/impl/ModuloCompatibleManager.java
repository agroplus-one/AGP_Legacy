package com.rsi.agp.core.managers.impl;


import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.cesp.RiesgoCubiertoFiltro;
import com.rsi.agp.dao.filters.cesp.impl.ModuloCompatibleFiltro;
import com.rsi.agp.dao.models.cesp.IModuloCompatibleDao;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.tables.cesp.ModuloCompatibleCe;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;

public class ModuloCompatibleManager implements IManager{
	
	private IModuloCompatibleDao moduloCompatibleDao;
	private ICommonDao commonDao;
	protected final Log logger = LogFactory.getLog(getClass());
	
	public boolean existeModuloCompatible(ModuloCompatibleCe moduloCompatibleCe){
		return listModulosCompatibles(moduloCompatibleCe).size() != 0;
	}
	
	public ModuloCompatibleCe getModuloCompatible(Long idModuloCompatible){
		return (ModuloCompatibleCe)moduloCompatibleDao.getObject(ModuloCompatibleCe.class, idModuloCompatible);
	}
	public void deleteModuloCompatible(ModuloCompatibleCe moduloCompatibleCe){
    	moduloCompatibleDao.removeObject(ModuloCompatibleCe.class, moduloCompatibleCe.getId());
	}
	
	public void saveModuloCompatible(ModuloCompatibleCe moduloCompatibleCe) throws DAOException{
		moduloCompatibleDao.saveOrUpdate(moduloCompatibleCe);
	}
	
	public List<ModuloCompatibleCe> listModulosCompatibles(ModuloCompatibleCe moduloCompatibleCe) {
		ModuloCompatibleFiltro moduloCompatibleFiltro = new ModuloCompatibleFiltro();
		moduloCompatibleFiltro.setModuloCompatibleCe(moduloCompatibleCe);
		
		return moduloCompatibleDao.getObjects(moduloCompatibleFiltro);
		
	}

	public List getPlanes(){

		List listPlanes = null;
		
		try {
			listPlanes = commonDao.getPlanes();
		}
		catch(Exception excepcion){
			logger.error("Excepcion : ModuloCompatibleManager - getPlanes", excepcion);
		}
		return listPlanes;
	}
	
	public List getLineas(BigDecimal codPlan){
		List listLineas = null;
		try {
			listLineas = commonDao.getLineas(codPlan);
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : ModuloCompatibleManager - getLineas", excepcion);
		}
		return listLineas;
	}
	
	public List<Modulo> getModulos(Long idLinea) {
		
		List listModulo = null;
		try{
			listModulo = moduloCompatibleDao.getObjects(Modulo.class, "id.lineaseguroid", idLinea);
	    }
	    catch(Exception exception) {
	    	logger.error("Excepcion : ModuloCompatibleManager - getModulos", exception);
	    }
	    return listModulo;
	}
	
	public List<RiesgoCubierto> getRiesgos(String codmodulo, Long lineaseguroid) {
		List listRiesgo = null;
		RiesgoCubiertoFiltro riesgoCubiertoFiltro = new RiesgoCubiertoFiltro(lineaseguroid, codmodulo); 
		try
		{
			listRiesgo = moduloCompatibleDao.getObjects(riesgoCubiertoFiltro);
		}
	    catch(Exception exception) {
	    	logger.error("Excepcion : ModuloCompatibleManager - getRiesgos", exception);
	    }
	    return listRiesgo;
	}
	
	public void setModuloCompatibleDao(IModuloCompatibleDao moduloCompatibleDao) {
		this.moduloCompatibleDao = moduloCompatibleDao;
	}

	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}

}


