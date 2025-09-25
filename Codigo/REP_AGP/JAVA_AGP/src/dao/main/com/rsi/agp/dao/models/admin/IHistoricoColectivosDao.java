package com.rsi.agp.dao.models.admin;

import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public interface IHistoricoColectivosDao extends GenericDao {

	public void saveHistoricoColectivo(Colectivo colectivoBean, Usuario usuario, 
			String modificacionHistoricoColectivo, Date fechaEfectoHist,boolean activarCol) throws DAOException;

	public List<HistoricoColectivos> listHistoricoColectivos(HistoricoColectivos historicoColectivo) throws DAOException;

	public HistoricoColectivos getUltColectivoHistorico(Long id)throws DAOException;

	public void borrarHistoricoColectivo(Long idHistorico) throws DAOException;

	void borrarFechaBajaColectivo(HistoricoColectivos hc) throws DAOException;
	
}
