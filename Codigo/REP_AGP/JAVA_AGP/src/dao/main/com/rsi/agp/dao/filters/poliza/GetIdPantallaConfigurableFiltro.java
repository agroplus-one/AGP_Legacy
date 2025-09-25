package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class GetIdPantallaConfigurableFiltro implements Filter{
	
    private BigDecimal idPantalla;
    private BigDecimal codLinea;
    private BigDecimal codPlan;
    private Long lineaseguroid;
    
    
	public GetIdPantallaConfigurableFiltro() {
		super();
	}
	
	public GetIdPantallaConfigurableFiltro(BigDecimal codLinea,BigDecimal codPlan,BigDecimal idPantalla){
		this.idPantalla = idPantalla;
		this.codLinea   = codLinea;
		this.codPlan    = codPlan;
	}
	
	public GetIdPantallaConfigurableFiltro(Long lineseguroid,BigDecimal idPantalla){
		this.idPantalla = idPantalla;
		this.lineaseguroid = lineseguroid;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(PantallaConfigurable.class);
		criteria.createAlias("pantalla", "pan");
        criteria.add(Restrictions.eq("pan.idpantalla",this.idPantalla.longValueExact()));
        criteria.createAlias("linea", "lin");
        if (this.lineaseguroid == null) {
	        criteria.add(Restrictions.eq("lin.codlinea",this.codLinea)).list();
	        criteria.add(Restrictions.eq("lin.codplan",this.codPlan)).list();
        }
        else {
        	criteria.add(Restrictions.eq("lin.lineaseguroid",this.lineaseguroid));
        }
        
        return criteria;
	}    
}

