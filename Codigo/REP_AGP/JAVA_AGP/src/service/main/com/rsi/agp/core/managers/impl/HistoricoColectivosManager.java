package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.admin.IHistoricoColectivosDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;
import com.rsi.agp.dao.tables.commons.Usuario;

public class HistoricoColectivosManager implements IManager {

	protected IHistoricoColectivosDao historicoColectivosDao;
	private static final Log logger = LogFactory.getLog(HistoricoColectivosManager.class);

	/**
	 * 
	 * @param colectivoBean
	 * @param usuario
	 * @param fechaEfectoHist
	 * @return
	 */
	public final ArrayList<Integer> saveHistoricoColectivo(final Colectivo colectivoBean, Usuario usuario,
			String tipoOperacion, Date fechaEfectoHist, boolean activarCol) {
		ArrayList<Integer> error = new ArrayList<>();
		try {

			historicoColectivosDao.saveHistoricoColectivo(colectivoBean, usuario, tipoOperacion, fechaEfectoHist,
					activarCol);
			error.add(0);
		} catch (Exception e) {
			error.add(20);
			logger.error("Se ha producido un error durante el guardado del colectivo ", e);
		} 
		return error;
	}

	public List<HistoricoColectivos> getListHistoricoColectivos(HistoricoColectivos historicoColectivo)
			throws BusinessException {
		List<HistoricoColectivos> listHistoricoColectivos = null;
		try {
			listHistoricoColectivos = historicoColectivosDao.listHistoricoColectivos(historicoColectivo);

		} catch (DAOException e) {
			logger.error("Se ha producido un error al obtener el historio de colectivos: " + e.getMessage());
			throw new BusinessException("Se ha producido un error al obtener el historio de colectivos", e);
		}
		return listHistoricoColectivos;
	}

	public HistoricoColectivos getUltColectivoHistorico(Long id) throws BusinessException {
		try {
			return historicoColectivosDao.getUltColectivoHistorico(id);
		} catch (DAOException e) {
			logger.error("Se ha producido un error al obtener el historio de un colectivo: " + e.getMessage());
			throw new BusinessException("Se ha producido un error al obtener el historio de un colectivo", e);
		}
	}

	public IHistoricoColectivosDao getHistoricoColectivosDao() {
		return historicoColectivosDao;
	}

	public void setHistoricoColectivosDao(IHistoricoColectivosDao historicoColectivosDao) {
		this.historicoColectivosDao = historicoColectivosDao;
	}

	public void borrarHistoricoColectivo(Long idHistorico) throws DAOException {
		try {

			historicoColectivosDao.borrarHistoricoColectivo(idHistorico);
		} catch (DAOException e) {
			logger.error("Se ha producido un error durante el borrado del historico colectivo ", e);
			throw new DAOException("Se ha producido un error durante el borrado del historico colectivo", e);
		}
	}

}
