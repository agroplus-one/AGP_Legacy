package com.rsi.agp.core.managers.impl;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.familias.IMtoFamiliasDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.familias.Familia;
import com.rsi.agp.dao.tables.familias.GrupoFamilia;
import com.rsi.agp.dao.tables.familias.LineaFamilia;
import com.rsi.agp.dao.tables.familias.LineaFamiliaId;

public class MtoFamiliasManager implements IManager {
	
	private static final Log logger = LogFactory.getLog(MtoFamiliasManager.class);

	private IMtoFamiliasDao familiasDao;	
	


	public final List<LineaFamilia> listLineaGrupoNegocioFamilia(final LineaFamilia lineaFamilia) throws BusinessException {
		logger.debug("listLineaGrupoNegocioFamilia");
		try {
			return familiasDao.listLineaGrupoNegocios(lineaFamilia);	
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de lineaFamilia: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el listado de lineaFamilia",dao);
		}
	}
	
	
	public boolean existLineaFamilia(LineaFamiliaId id) {
		return familiasDao.getLineaFamilia(id) != null;
	}
	
	

	public void altaFamilia(LineaFamilia lineaFamilia) throws BusinessException {
		logger.debug("init - guardarFamilia");
		
		try {

			// Actualizar/Crear familia
			this.actualizarFamilia(lineaFamilia);
			
			
			logger.debug("lineaFamilia:" + lineaFamilia.getId().getCodFamilia() + " - " + lineaFamilia.getId().getGrupoNegocio() + " - " + lineaFamilia.getId().getCodLinea());

			// Crear familia solo en caso de que no exista
			if (!existLineaFamilia(lineaFamilia.getId())) {
				familiasDao.alta(lineaFamilia);
			} else {
				logger.error("La linea de familia ya existe y no puede darse de alta");
				throw new BusinessException("El registro ya existe.");
			}
			


		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar la familia: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al guardar la familia: " + dao.getMessage());
		}
		
		logger.debug("end - guardarFamilia");

	}
	


	public void actualizarLineaFamilia(LineaFamilia lineaFamiliaInicial, LineaFamilia lineaFamiliaModificada, HttpServletRequest request) throws BusinessException {

		logger.debug("actualizarLineaFamilia [INIT]");
		
		try {
			// Actualizar/Crear familia
			this.actualizarFamilia(lineaFamiliaModificada);			

			// Actualizar linea de familia solo en caso de que exista ya.
			if (existLineaFamilia(lineaFamiliaInicial.getId())) {
				familiasDao.updateLineaFamilia(lineaFamiliaInicial, lineaFamiliaModificada);
			} else {
				logger.error("La linea de familia no existe");
				throw new BusinessException("La linea de familia no existe");
			}
			
			
		}catch(DAOException dao) {
			logger.error("Se ha producido un error al validarFamilia : " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al validarFamilia",dao);
		}
		
		
	}
	
	

	private void actualizarFamilia(LineaFamilia lineaGrupoNegocio) throws BusinessException {
		
		logger.debug("init - actualizarFamilia");
		

		try {	
		
		// Si no existe la familia, entonces se crea
		Familia familia = familiasDao.getFamilia(lineaGrupoNegocio.getId().getCodFamilia());
		if (familia == null) {
			
			logger.debug("La Familia NO Existe, procediendo a crearla.");

			
			Familia fam = new Familia();
			fam.setCodFamilia(lineaGrupoNegocio.getId().getCodFamilia());
			fam.setNomFamilia( lineaGrupoNegocio.getFamilia().getNomFamilia());
			familiasDao.saveOrUpdate(fam);

			lineaGrupoNegocio.setFamilia(fam);
		} else {
			
			logger.debug("La Familia SÍ Existe, procediendo a modificar el nombre.");

			familia.setNomFamilia( lineaGrupoNegocio.getFamilia().getNomFamilia());
			familiasDao.saveOrUpdate(familia);
			logger.debug("Actualizar nombre de la familia");
		}
		

		
		}catch(DAOException dao) {
			logger.error("Se ha producido un error al validarFamilia : " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al validarFamilia",dao);
		}
		
	}
	
	public void borrarLineaFamilia(LineaFamilia lineaFamilia) throws BusinessException {
		
		logger.debug("borrarLineaFamilia - init");

		try {

			if (existLineaFamilia(lineaFamilia.getId())) {
				familiasDao.delete(lineaFamilia);
			} else {
				logger.error("La linea de familia no existe");
				throw new BusinessException("La linea de familia no existe");
			}

		
		}catch(DAOException dao) {
			logger.error("Se ha producido un error al validarFamilia : " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al validarFamilia",dao);
		}
	}
	
	
	public LineaFamilia getLineaFamilia(LineaFamiliaId lineaFamiliaId) {
		return familiasDao.getLineaFamilia(lineaFamiliaId);
				
	}
	
	
	public void setFamiliasDao(IMtoFamiliasDao familiasDao) {
		this.familiasDao = familiasDao;
	}
	

	public List<GrupoFamilia> getGrupos() throws DAOException {
		return this.familiasDao.getGrupos();
	}
	
	
	public List<GruposNegocio> getGruposNegocio() throws DAOException {
		return this.familiasDao.getGruposNegocio();
	}




	
}
