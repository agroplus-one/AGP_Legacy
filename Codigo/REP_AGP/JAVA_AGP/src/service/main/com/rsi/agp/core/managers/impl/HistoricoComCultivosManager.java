package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.admin.IHistoricoColectivosDao;
import com.rsi.agp.dao.models.admin.IHistoricoComCultivosDao;
import com.rsi.agp.dao.models.comisiones.ICultivosEntidadesDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;

public class HistoricoComCultivosManager implements IManager {

	protected IHistoricoColectivosDao historicoColectivosDao;
	protected IHistoricoComCultivosDao historicoComCultivosDao;
	private ICultivosEntidadesDao cultivosEntidadesDao;
	private static final Log logger = LogFactory.getLog(HistoricoComCultivosManager.class);

	/**
	 * 
	 * @param colectivoBean
	 * @param usuario
	 * @return
	 */
	public final ArrayList<Integer> saveHistoricoColectivo(final Colectivo colectivoBean, Usuario usuario,
			String tipoOperacion) {
		ArrayList<Integer> error = new ArrayList<>();
		try {

			historicoColectivosDao.saveHistoricoColectivo(colectivoBean, usuario, tipoOperacion, null, false);
			error.add(0);
		} catch (Exception e) {
			error.add(20);
			logger.error("Se ha producido un error durante el guardado del colectivo ", e);
		}
		return error;
	}

	public CultivosEntidades getCultEnt(Long id) {
		CultivosEntidades CultEnt = new CultivosEntidades();

		CultEnt = (CultivosEntidades) cultivosEntidadesDao.getObject(CultivosEntidades.class, id);

		return CultEnt;
	}

	public List<CultivosEntidadesHistorico> getListHistoricoComCultivos(Long id) throws BusinessException {
		List<CultivosEntidadesHistorico> listCultEntHistorico = null;
		try {
			listCultEntHistorico = historicoComCultivosDao.listHistoricoComCultivos(id);

		} catch (DAOException e) {
			logger.error("Se ha producido un error al obtener el historio de cultivos Entidades: " + e.getMessage());
			throw new BusinessException("Se ha producido un error al obtener el historio de cultivos Entidades", e);
		}
		return listCultEntHistorico;
	}

	public IHistoricoColectivosDao getHistoricoColectivosDao() {
		return historicoColectivosDao;
	}

	public void setHistoricoColectivosDao(IHistoricoColectivosDao historicoColectivosDao) {
		this.historicoColectivosDao = historicoColectivosDao;
	}

	public void setHistoricoComCultivosDao(IHistoricoComCultivosDao historicoComCultivosDao) {
		this.historicoComCultivosDao = historicoComCultivosDao;
	}

	public void setCultivosEntidadesDao(ICultivosEntidadesDao cultivosEntidadesDao) {
		this.cultivosEntidadesDao = cultivosEntidadesDao;
	}

}
