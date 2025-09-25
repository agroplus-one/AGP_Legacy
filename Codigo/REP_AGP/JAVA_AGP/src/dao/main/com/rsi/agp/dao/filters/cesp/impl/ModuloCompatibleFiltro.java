package com.rsi.agp.dao.filters.cesp.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cesp.ModuloCompatibleCe;

public class ModuloCompatibleFiltro implements Filter {
	
	private ModuloCompatibleCe moduloCompatibleCe;
    
	public Criteria getCriteria(Session sesion) {  
		final Criteria criteria = sesion.createCriteria(ModuloCompatibleCe.class);

		BigDecimal codplan = moduloCompatibleCe.getLinea().getCodplan();
		if (FiltroUtils.noEstaVacio(codplan)) {
			criteria.add(Restrictions.eq("linea.codplan",codplan));
			criteria.createAlias("linea", "linea");
		}
		
	    Long lineaseguroid = moduloCompatibleCe.getLinea().getLineaseguroid();
	    
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			criteria.add(Restrictions.eq("linea.lineaseguroid",lineaseguroid));
		}
		
		String codmoduloPrincipal = moduloCompatibleCe.getModuloPrincipal().getId().getCodmodulo();
		
		if (FiltroUtils.noEstaVacio(codmoduloPrincipal)) {
			criteria.add(Restrictions.eq("moduloPrincipal.id.codmodulo",codmoduloPrincipal));
		}

		String codmoduloComplementario = moduloCompatibleCe.getModuloComplementario().getId().getCodmodulo();
		
		if (FiltroUtils.noEstaVacio(codmoduloComplementario)) {
			criteria.add(Restrictions.eq("moduloComplementario.id.codmodulo",codmoduloComplementario));
		}
		
		BigDecimal codriesgocubierto = moduloCompatibleCe.getRiesgoCubierto().getId().getCodriesgocubierto();
		
		if (FiltroUtils.noEstaVacio(codriesgocubierto)) {
			criteria.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto",codriesgocubierto));
		}

		return criteria;
	}

	public ModuloCompatibleCe getModuloCompatibleCe() {
		return moduloCompatibleCe;
	}

	public void setModuloCompatibleCe(ModuloCompatibleCe moduloCompatibleCe) {
		this.moduloCompatibleCe = moduloCompatibleCe;
	}
}