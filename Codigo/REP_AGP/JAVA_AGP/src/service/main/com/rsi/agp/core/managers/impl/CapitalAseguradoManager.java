package com.rsi.agp.core.managers.impl;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.poliza.CapitalAseguradoDao;
import com.rsi.agp.dao.models.poliza.ICapitalAseguradoDao;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;

public class CapitalAseguradoManager implements IManager {

	private ICapitalAseguradoDao capitalAseguradoDao;

	public final CapitalAsegurado getCapitalAseguradoById(
			final Long idCapitalAsegurado) {
		return (CapitalAsegurado) capitalAseguradoDao.getObject(CapitalAsegurado.class, idCapitalAsegurado);
	}
	
	public List<CapAsegRelModulo> listCapAsegRelModuloByIdCapitalAsegurado(
			final Long idCapitalAsegurado) throws BusinessException {
		try {
			return capitalAseguradoDao.listCapAsegRelModuloByIdCapAseg(idCapitalAsegurado);

		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error buscando los capitales asegurados", dao);
		}
	}
	
	public CapitalAsegurado saveCapitalAsegurado(CapitalAsegurado capAseg) throws BusinessException {		
		try{
			return (CapitalAsegurado)capitalAseguradoDao.saveOrUpdate(capAseg);			
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error guardando el capital asegurado", dao);
		}
	}
	
	public CapAsegRelModulo saveCapAsegRelMod(CapAsegRelModulo capAsegRelMod) throws BusinessException {		
		try{
			return (CapAsegRelModulo)capitalAseguradoDao.saveOrUpdate(capAsegRelMod);			
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error guardando el capital asegurado", dao);
		}
	}

	public void deleteCapAsegRelModById(Long id)  throws BusinessException {
		try {
			capitalAseguradoDao.deleteCapAsegRelModById(id);
		} catch (DAOException e) {
			throw new BusinessException ("Se ha producido un error la relacion del capital asegurado con el modulo", e);
		}
	}
	
	public void setCapitalAseguradoDao(ICapitalAseguradoDao capitalAseguradoDao) {
		this.capitalAseguradoDao = capitalAseguradoDao;
	}

}
