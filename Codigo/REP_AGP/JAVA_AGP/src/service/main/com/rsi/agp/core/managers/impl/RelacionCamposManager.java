/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Manager para la pantalla relacion campos
*
 **************************************************************************************************
*/
package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.config.OrganizacionInformacionFiltro;
import com.rsi.agp.dao.filters.config.UbicacionesFiltro;
import com.rsi.agp.dao.filters.config.UsosFiltro;
import com.rsi.agp.dao.models.config.IRelCamposDao;
import com.rsi.agp.dao.tables.config.GrupoFactores;
import com.rsi.agp.dao.tables.config.RelacionCampo;
import com.rsi.agp.dao.tables.config.TipoCampo;

@SuppressWarnings("rawtypes")
public class RelacionCamposManager implements IManager {

	private Log logger = LogFactory.getLog(RelacionCamposManager.class);
	
	private IRelCamposDao relDao;

	/*
	 * NOTA: Setters para el Hibernate, tantos como stributos dao declarados en la
	 * clase Modificar su manager del action-servlet y a�adir los mapeos de esos
	 * datos
	 */

	public void setRelDao(IRelCamposDao relDao) {
		this.relDao = relDao;
	}

	public boolean existeRelacionCampo(RelacionCampo relacionCampo) {
		return consultaRelacionCampos(relacionCampo).size() != 0;
	}

	public List getUbicaciones(Long linea, Long uso) {
		List listUbicaciones = null;
		try {
			OrganizacionInformacionFiltro filter = new OrganizacionInformacionFiltro(linea, uso);
			UbicacionesFiltro filterUbicacion = new UbicacionesFiltro(relDao.getObjects(filter));
			listUbicaciones = relDao.getObjects(filterUbicacion);
		} catch (Exception exception) {
			logger.error(exception);
		}
		return listUbicaciones;
	}

	public List getCampoSC(Long linea, BigDecimal uso, BigDecimal ubicacion) {
		List listCampoSC = null;
		try {
			listCampoSC = relDao.getCampoSC(linea, uso, ubicacion);
	    }
	    catch(Exception exception) {
	    	logger.error("Excepcion : RelacionCamposManager - getCampoSC", exception);
	    }
	    return listCampoSC;
	}

	public List getTipoCampo() {
		List listTipoCampos = null;
		try {
			listTipoCampos = relDao.getObjects(TipoCampo.class, null, null);
		} catch (Exception exception) {
			logger.error(exception);
		}
		return listTipoCampos;
	}

	public List getFactores() {
		List listFactores = null;
		try {
			listFactores = relDao.getObjects(GrupoFactores.class, null, null);
		} catch(Exception exception) {
	    	logger.error("Excepcion : RelacionCamposManager - getFactores", exception);
	    }
	    return listFactores;
	}

	public List getUsos(Long linea) {
		OrganizacionInformacionFiltro filter = new OrganizacionInformacionFiltro(linea);
		UsosFiltro filterUsos = new UsosFiltro(relDao.getObjects(filter));
		return relDao.getObjects(filterUsos);
		// return relDao.getObjects(Uso.class, null, null);
	}

	public List getRelacionCampos() {
		List listRelacionCampos = null;
	    try{
	    	listRelacionCampos = relDao.getRelacionesCampos();
	    }
	    catch(Exception exception) {
	    	logger.error("Excepcion : RelacionCamposManager - getRelacionCampos", exception);
	    }
	    return listRelacionCampos;
	}

	public void deleteRelacionCampos(Long idRow) {
		try {
			relDao.removeObject(RelacionCampo.class, idRow);
		} catch(Exception exception) {
			logger.error("Excepcion : RelacionCamposManager - deleteRelacionCampos", exception); 
		}
	}

	public void saveRelacionCampos(RelacionCampo rc) {
		try {
			relDao.saveOrUpdate(rc);
		} catch (Exception exception) {
			logger.error("Se ha producido un error al guardar la referencia de campos", exception);
		}
	}

	public List consultaRelacionCampos(RelacionCampo rc) {
		List resultConsulta = null;
		try {
			resultConsulta = relDao.getRelacionCamposConsulta(rc);
		} catch(Exception exception) {
			logger.error("Excepcion : RelacionCamposManager - consultaRelacionCampos", exception);
	    }
		return resultConsulta;
	}

	public RelacionCampo getRelacionCampo(Long idRow) {
		RelacionCampo rc = (RelacionCampo) relDao.getObject(RelacionCampo.class, idRow);
		return rc;
	}

	public Object getGrupoFactores(BigDecimal camposc) {
		Object grupoFactores = null;
		try {
			grupoFactores = relDao.getGruposFactores(camposc);
		} catch (BusinessException e) {
			logger.error("Excepcion : RelacionCamposManager - getGrupoFactores", e);
		}
		return grupoFactores;
	}

	public GrupoFactores getFactoresPorGrupo(BigDecimal factor) {
		GrupoFactores grupoFactores = null;
		try {
			grupoFactores = (GrupoFactores) relDao.getFactoresPorGrupo(factor);
		} catch(Exception exception) {
			logger.error("Excepcion : RelacionCamposManager - getFactoresPorGrupo", exception); 
	    }
		return grupoFactores;
	}
}
