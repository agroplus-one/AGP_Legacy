package com.rsi.agp.dao.models.mtoinf;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;
import com.rsi.agp.dao.tables.mtoinf.Vista;

public class MtoVistasDao extends BaseDaoHibernate implements IMtoVistasDao{

	@Override
	public List<Vista> getListadoVistas() {
		
		Criteria criteria = obtenerSession().createCriteria(Vista.class);
		// Sólo se devuelven las vistas configuradas como visibles
		criteria.add(Restrictions.eq("visible", ConstantsInf.VISIBLE_SI));
		
		return criteria.list();
	}
	
	@Override
	public List<RelVistaCampos> getRelVistaCampos() {
		
		Criteria criteria = obtenerSession().createCriteria(RelVistaCampos.class);
		// Sólo se devuelven las vistas configuradas como visibles
		criteria.createAlias("vistaByIdvista1", "vista1");
		criteria.createAlias("vistaByIdvista2", "vista2");
		criteria.add(Restrictions.eq("vista1.visible", ConstantsInf.VISIBLE_SI));
		criteria.add(Restrictions.eq("vista2.visible", ConstantsInf.VISIBLE_SI));
		
		return criteria.list();
	}

}
