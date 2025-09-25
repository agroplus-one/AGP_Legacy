package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;

public class DatoVariableFiltro implements Filter {
	
	private BigDecimal codConcepto;
	private Long idCapitalAsegurado;
	private Long codconceptoppalmod;
    private Long codriesgocubierto;
	
	public DatoVariableFiltro (BigDecimal codConcepto, Long idCapitalAsegurado) {
		this.codConcepto = codConcepto;
		this.idCapitalAsegurado = idCapitalAsegurado;
	}
	
	public DatoVariableFiltro (BigDecimal codConcepto, Long idCapitalAsegurado, Long codconceptoppalmod, Long codriesgocubierto) {
		this.codConcepto = codConcepto;
		this.idCapitalAsegurado = idCapitalAsegurado;
		this.codconceptoppalmod = codconceptoppalmod;
		this.codriesgocubierto = codriesgocubierto;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		
		Criteria criteria = sesion.createCriteria(CapitalDTSVariable.class );
		
		criteria.add(Restrictions.eq("codconcepto", this.codConcepto));
		criteria.add(Restrictions.eq("capitalAsegurado.id", this.idCapitalAsegurado));
		
		if (this.codconceptoppalmod != null) criteria.add(Restrictions.eq("codconceptoppalmod", this.codconceptoppalmod));
		if (this.codriesgocubierto != null) criteria.add(Restrictions.eq("codriesgocubierto", this.codriesgocubierto));

		return criteria;
	}

}
