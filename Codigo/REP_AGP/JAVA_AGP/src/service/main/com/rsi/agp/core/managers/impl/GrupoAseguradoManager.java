package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.cesp.impl.GrupoAseguradoFiltro;
import com.rsi.agp.dao.models.cesp.IGrupoAseguradoDao;
import com.rsi.agp.dao.tables.cesp.GrupoAseguradoCe;

public class GrupoAseguradoManager implements IManager {

	protected IGrupoAseguradoDao grupoAseguradoDao;
	private static final Log LOGGER = LogFactory.getLog(GrupoAseguradoManager.class); 
	
	public void setGrupoAseguradoDao(IGrupoAseguradoDao grupoAseguradoDao) {
		this.grupoAseguradoDao = grupoAseguradoDao;
	}
	
	
	/*Le pasamos los datos del filtro (en caso de que se aplique) y construimos un objeto del tipo grupoAseguradosFiltro para obtener los datos 
	  con Hibenate y devolverlos*/	
	@SuppressWarnings("unchecked")
	public final List<GrupoAseguradoCe> getListAsegurados(final GrupoAseguradoCe grupoAseguradoBean) {

		GrupoAseguradoFiltro filtroAsegurado = null; // creamos un objeto que contiene los filtros de hibernate, para la tabla aseguradosCe
		if(grupoAseguradoBean!=null){
			filtroAsegurado = new GrupoAseguradoFiltro(grupoAseguradoBean);
		}else{
			filtroAsegurado = new GrupoAseguradoFiltro();
		} 				// le preguntamos si el Bean viene vacio (no han introducido datos en la web). 
		
		List<GrupoAseguradoCe> resultado = grupoAseguradoDao.getObjects(filtroAsegurado); //el metodo nos va a devolver una lista del tipo GrupoAseguradoCe
		
		return resultado;
	}
	
	
	public void saveCodGrupo(final GrupoAseguradoCe grupoAseguradoBean){
			try {
				grupoAseguradoDao.saveOrUpdate(grupoAseguradoBean);
			} catch (DAOException e) {
				LOGGER.error("Se ha producido un error al guardar cod. grupo",e);
			}
		}
	
	public GrupoAseguradoCe getObjet(final GrupoAseguradoCe grupoAseguradoBean){
		GrupoAseguradoCe grupocomprobar = (GrupoAseguradoCe)grupoAseguradoDao.getObject(GrupoAseguradoCe.class, grupoAseguradoBean.getCodgrupoaseg());
		return grupocomprobar;
	}
	
	public void dropObjet(final String grupoAseguradoId){
		grupoAseguradoDao.removeObject(GrupoAseguradoCe.class, grupoAseguradoId);
	}
}
