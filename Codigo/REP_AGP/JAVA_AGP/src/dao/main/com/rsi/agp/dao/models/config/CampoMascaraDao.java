package com.rsi.agp.dao.models.config;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.config.CampoMascaraFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.masc.CampoMascara;

public class CampoMascaraDao extends BaseDaoHibernate implements ICampoMascaraDao {

	public boolean existeCampoMascara(CampoMascara campoMascara){
		CampoMascaraFiltro campoMascaraFiltro = new CampoMascaraFiltro();
		campoMascaraFiltro.setCampoMascara(campoMascara);
		
		Criteria criteria = campoMascaraFiltro.getCriteria(this.obtenerSession());
		if (campoMascara.getId() != null)
			criteria.add(Restrictions.ne("id", campoMascara.getId()));
		
		return criteria.list().size() != 0;
	}
}
