package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;

@SuppressWarnings("unchecked")
public class PolizasRCDao extends BaseDaoHibernate implements IPolizasRCDao {

	private static final String QUERY_REGIMENES = "select distinct r.codregimen, r.descripcion from tb_rc_regimen r inner join tb_rc_datos d on r.codregimen = d.codregimen_rc where d.lineaseguroid = :lineaseguroid and d.codent_med = :entidad and d.codsubent_med = :subentidad and d.codespecie_rc = :especie";
	private static final String QUERY_REGIMENES_NULOS = "select distinct r.codregimen, r.descripcion from tb_rc_regimen r inner join tb_rc_datos d on r.codregimen = d.codregimen_rc where d.lineaseguroid = :lineaseguroid and d.codent_med is null and d.codsubent_med is null and d.codespecie_rc = :especie";
	private static final String QUERY_ESPECIES = "select distinct e.codespecie, e.descripcion from tb_rc_especies e inner join tb_rc_lineas l on e.codespecie = l.codespecie_rc where l.lineaseguroid = :lineaseguroid and (l.codespecie = :codespecie or l.codespecie = 999) and (l.codregimen = :codregimen or l.codregimen = 999) and l.codtipocapital = :codtipocapital";
	private static final String QUERY_REGIMENES_INFORME = "select count(*) from tb_rc_lineas rcl where rcl.lineaseguroid = :lineaseguroid and (rcl.codregimen = :codregimen or  rcl.codregimen = 999) and (rcl.codespecie = :codespecie or rcl.codespecie = 999) and (rcl.codtipocapital = :codtipocapital or rcl.codtipocapital = 999)";
	
	@Override
	public PolizasRC obtenerPolizaRC(Long polizaId) throws DAOException{
		try {
			return (PolizasRC) this.obtenerSession().createCriteria(PolizasRC.class)
					.createAlias("poliza", "poliza")
					.add(Restrictions.eq("poliza.idpoliza", polizaId))
					.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public List<EspeciesRC> obtenerEspecieRC(Long lineaSeguroId, BigDecimal codEspecie, BigDecimal regimen, 
			BigDecimal codTipoCapital) throws DAOException {
		try {
			Session session = this.obtenerSession();
			return this.queryEspeciesRC(session, lineaSeguroId, codEspecie, regimen, codTipoCapital).list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	private SQLQuery queryEspeciesRC(Session session, Long lineaSeguroId, BigDecimal codEspecie, BigDecimal regimen, 
			BigDecimal codTipoCapital){
		SQLQuery query = session.createSQLQuery(QUERY_ESPECIES);
		query.setLong("lineaseguroid", lineaSeguroId);
		query.setBigDecimal("codespecie", codEspecie);
		query.setBigDecimal("codregimen", regimen);
		query.setBigDecimal("codtipocapital", codTipoCapital);
		query.addEntity(EspeciesRC.class);
		return query;
	}

	@Override
	public List<RegimenRC> obtenerRegimenRC(Long lineaSeguroId, BigDecimal codSubentidad, BigDecimal codEntidad, 
			String codEspecieRC) throws DAOException {
		try {
			Session session = this.obtenerSession();
			List<RegimenRC> regimenes = this.queryRegimenes(session, lineaSeguroId, codSubentidad, codEntidad, codEspecieRC).list();
			if(regimenes.isEmpty()){			
				regimenes = this.queryRegimenes(session, lineaSeguroId, codEspecieRC).list();
			}
			return regimenes;
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}
	
	private SQLQuery queryRegimenes(Session session, Long lineaSeguroId, BigDecimal codSubentidad, BigDecimal codEntidad, 
			String codEspecieRC){
		SQLQuery query = session.createSQLQuery(QUERY_REGIMENES);
		query.setLong("lineaseguroid", lineaSeguroId);
		query.setBigDecimal("entidad", codEntidad);
		query.setBigDecimal("subentidad", codSubentidad);
		query.setString("especie", codEspecieRC);
		query.addEntity(RegimenRC.class);
		return query;
	}
	
	private SQLQuery queryRegimenes(Session session, Long lineaSeguroId, String codEspecieRC){
		SQLQuery query = session.createSQLQuery(QUERY_REGIMENES_NULOS);
		query.setLong("lineaseguroid", lineaSeguroId);
		query.setString("especie", codEspecieRC);
		query.addEntity(RegimenRC.class);
		return query;
	}

	@Override
	public Boolean grupoRazaTieneRegimen(Long lineaSeguroId, Long codEspecie, 
			Long codRegimen, BigDecimal codTipoCapital) throws DAOException {
		Boolean flag = false;
		BigDecimal count = (BigDecimal)this.queryRegimenesInforme(lineaSeguroId, codEspecie, codRegimen, codTipoCapital).uniqueResult();
		if(count.intValue() == 1){
			flag = true;
		}
		return flag;
	}
	
	private SQLQuery queryRegimenesInforme(Long lineaSeguroId, Long codEspecie, 
			Long codRegimen, BigDecimal codTipoCapital){
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_REGIMENES_INFORME);
		query.setLong("codespecie", codEspecie);
		query.setBigDecimal("codtipocapital", codTipoCapital);
		query.setLong("codregimen", codRegimen);
		query.setLong("lineaseguroid", lineaSeguroId);
		return query;
	}
}
