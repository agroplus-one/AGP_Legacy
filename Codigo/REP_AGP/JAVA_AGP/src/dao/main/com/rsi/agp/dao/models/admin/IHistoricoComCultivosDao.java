package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public interface IHistoricoComCultivosDao extends GenericDao {

	public void saveHistoricoColectivo(Colectivo colectivoBean, Usuario usuario, String modificacionHistoricoColectivo) throws DAOException;

	public List<CultivosEntidadesHistorico> listHistoricoComCultivos(Long id) throws DAOException;
	
	public boolean existeComision(int plan, int linea, 
			Date fechaEfecto, BigDecimal gastosAdmon, BigDecimal gastosAdq) throws DAOException;
	
	
}
