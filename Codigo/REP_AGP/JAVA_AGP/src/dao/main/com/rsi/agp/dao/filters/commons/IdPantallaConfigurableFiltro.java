

package com.rsi.agp.dao.filters.commons;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class IdPantallaConfigurableFiltro implements Filter {

	private Long idPantallaConfigurable;

	public Long getIdPantallaConfigurable() {
		return idPantallaConfigurable;
	}

	public void setIdPantallaConfigurable(Long idPantallaConfigurable) {
		this.idPantallaConfigurable = idPantallaConfigurable;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(PantallaConfigurable.class);
        criteria.add(Restrictions.eq("linea.idpantallaconfigurable",this.idPantallaConfigurable));

		return criteria;
	}
}