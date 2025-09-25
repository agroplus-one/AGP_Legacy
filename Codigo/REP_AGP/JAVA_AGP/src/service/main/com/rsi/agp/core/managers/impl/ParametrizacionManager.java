package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.param.ParametrizacionFiltro;
import com.rsi.agp.dao.models.param.IParametrizacionDao;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;

public class ParametrizacionManager implements IManager {

	private IParametrizacionDao parametrizacionDao;
	private static final Log LOGGER = LogFactory.getLog(ParametrizacionManager.class);
	
	public final void actualizaParametro(final Parametro parametro) throws BusinessException {
		try {
			parametrizacionDao.saveOrUpdate(parametro);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al actualizar la parametrizacion", e);
			throw new BusinessException(e);
		}
	}

	/**	
	 * @return primera linea de la tabla TB_PARAMETROS. Si es la primera vez que se accede, retorna un parametro nuevo.
	 */
	@SuppressWarnings("unchecked")
	public final Parametro getParametro() {
		Parametro resultado = new Parametro();
		final ParametrizacionFiltro filter = new ParametrizacionFiltro();
		final List<Parametro> listaParametros = parametrizacionDao.getObjects(filter);
		if (null != listaParametros && !listaParametros.isEmpty()) {
			resultado = listaParametros.get(0);
		}
		return resultado;
	}

	public final void setParametrizacionDao(final IParametrizacionDao parametrizacionDao) {
		this.parametrizacionDao = parametrizacionDao;
	}

	@SuppressWarnings("unchecked")
	public List<EstadoRenovacionAgroseguro>getEstadosRenovacionAgroseguro(){
		List<EstadoRenovacionAgroseguro> res=null;
		res = this.parametrizacionDao.getObjects(EstadoRenovacionAgroseguro.class, null, null);
		return res;
	} 
	
	@SuppressWarnings("unchecked")
	public List<ConfigAgp> getNemosConfigAgp() {
		List<ConfigAgp> res=null;
		res = this.parametrizacionDao.getObjects(ConfigAgp.class, null, null);
		return res;
	}
	
	public String getConfigAgpValor(final String agpNemo) {
		ConfigAgp configAgp = (ConfigAgp) this.parametrizacionDao.getObject(ConfigAgp.class, "agpNemo", agpNemo);
		return configAgp == null ? "" : configAgp.getAgpValor();
	}
	
	public void updateConfigAgpValor(final String agpNemo, final String agpValor) throws BusinessException {
		ConfigAgp configAgp = (ConfigAgp) this.parametrizacionDao.getObject(ConfigAgp.class, "agpNemo", agpNemo);
		if (configAgp != null) {
			configAgp.setAgpValor(agpValor);
			try {
				this.parametrizacionDao.saveOrUpdate(configAgp);
			} catch (DAOException e) {
				throw new BusinessException(e.getMessage());
			}
		} else {
			throw new BusinessException("Configuracion de Agroplus " + agpNemo + " no encontrada");
		}
	}
}
