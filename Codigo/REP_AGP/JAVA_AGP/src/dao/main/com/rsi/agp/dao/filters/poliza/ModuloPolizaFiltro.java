package com.rsi.agp.dao.filters.poliza;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;

public class ModuloPolizaFiltro implements Filter{

	private Long idpoliza;
	private Long lineaseguroid;
	private String codmodulo;
	
	public ModuloPolizaFiltro(final Long idpoliza, final Long lineaseguroid, final String codmodulo) {
		this.idpoliza = idpoliza;
		this.lineaseguroid = lineaseguroid;
		this.codmodulo = codmodulo;
	}
	
	public Criteria getCriteria(Session sesion){
		Criteria criteria = sesion.createCriteria(ModuloPoliza.class);
		criteria.add(Restrictions.allEq(getIds()));
		
		return criteria;
	}

	private final Map<String, Object> getIds(){
		
		final Map<String, Object> map = new HashMap<String, Object>();
		
		final Long idpoliza = this.idpoliza;
		if (FiltroUtils.noEstaVacio(idpoliza))
			map.put("id.idpoliza", idpoliza);
		
		final Long lineaseguroid = this.lineaseguroid;
		if (FiltroUtils.noEstaVacio(lineaseguroid))
			map.put("id.lineaseguroid", lineaseguroid);
		
		final String codmodulo = this.codmodulo;
		if (FiltroUtils.noEstaVacio(codmodulo))
			map.put("id.codmodulo", codmodulo);
		
		return map;
	}
	
}
