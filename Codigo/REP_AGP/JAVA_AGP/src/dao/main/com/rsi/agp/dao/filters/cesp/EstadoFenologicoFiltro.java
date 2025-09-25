package com.rsi.agp.dao.filters.cesp;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.EstadoFenologico;

public class EstadoFenologicoFiltro implements Filter {

	private EstadoFenologico estadoFenologico;
	private boolean distintosPorCultivo;
	
	public Criteria getCriteria(Session sesion) {
		
		Criteria criteria = sesion.createCriteria(EstadoFenologico.class);
		
		if(distintosPorCultivo){
			criteria.add(Restrictions.eq("id.codcultivo", estadoFenologico.getId().getCodcultivo()));
			criteria.setProjection(Projections.distinct(
					Projections.projectionList().add(Projections.property("id.codcultivo"), "id.codcultivo")
		       	)).setResultTransformer(Transformers.aliasToBean(EstadoFenologico.class));

		}
			
			
			
		return criteria;
		
		
	}

	public EstadoFenologico getEstadoFenologico() {
		return estadoFenologico;
	}

	public void setEstadoFenologico(EstadoFenologico estadoFenologico) {
		this.estadoFenologico = estadoFenologico;
	}

	public boolean isDistintosPorCultivo() {
		return distintosPorCultivo;
	}

	public void setDistintosPorCultivo(boolean distintosPorCultivo) {
		this.distintosPorCultivo = distintosPorCultivo;
	}

	
}
