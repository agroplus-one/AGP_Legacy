package com.rsi.agp.dao.models.imp;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cargas.CargasTablas;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;

public class CargasTablasDao extends BaseDaoHibernate implements ICargasTablasDao{
	
	@Override
	public TablaCondicionado getTabla(String codTabla) throws DAOException {
		try{
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(TablaCondicionado.class);
			criteria.add(Restrictions.eq("codtablacondicionado", new BigDecimal(codTabla)));
			return (TablaCondicionado)criteria.uniqueResult();
		}catch (Exception e) {
			logger.error("Error al obtener las tablas del fichero de bbdd",e);
			throw new DAOException(e);
		}
	}

	@Override
	public List<CargasTablas> getTablasbyId(Long idFichero) {
		
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(CargasTablas.class);
		criteria.createAlias("cargasFicheros", "cargasFicheros");
		
		criteria.add(Restrictions.eq("cargasFicheros.id", idFichero));
		return (List)criteria.list();
		
	}

	@Override
	public CargasTablas getTablaAmodificar(CargasTablas cargasTablas,Long idFichero) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(CargasTablas.class);
		criteria.createAlias("cargasFicheros", "cargasFicheros");
		
		criteria.add(Restrictions.eq("cargasFicheros.id", idFichero));
		criteria.add(Restrictions.eq("numtabla", cargasTablas.getNumtabla()));
		return (CargasTablas)criteria.uniqueResult();
		
	}

	@Override
	public void deletebyIdFichero(Long idFichero) {
		
		Session session = obtenerSession();
		
		Query queryDelete = session.createSQLQuery("delete from tb_cargas_tablas where id_carga_fichero = :id")
								   .setLong("id", idFichero);
		queryDelete.executeUpdate();
		
	}

}
