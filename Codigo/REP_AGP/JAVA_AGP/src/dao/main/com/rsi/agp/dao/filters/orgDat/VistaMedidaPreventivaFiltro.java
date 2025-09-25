package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.orgDat.VistaMedidaPreventiva;

public class VistaMedidaPreventivaFiltro implements Filter 
{
	private Long lineaSeguroId;	
	private String codmodulo;
    private BigDecimal codriesgocubierto;
    private Character elegible;
    
    public VistaMedidaPreventivaFiltro() {}
    
	public VistaMedidaPreventivaFiltro(Long lineaSeguroId, String codmodulo,
			BigDecimal codriesgocubierto,Character elegible) {
		super();
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codriesgocubierto = codriesgocubierto;
		this.elegible = elegible;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaMedidaPreventiva.class);
		
		criteria.add(getMedidaPreventiva());

		criteria.add(Restrictions.eq("id.elegible", this.elegible));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.addOrder(Order.asc("id.codmedidapreventiva"));
		
		return criteria;
	}	

	private final Conjunction getMedidaPreventiva() 
	{
		Conjunction c = Restrictions.conjunction();
		
		/* PROPIEDADES DE MODULO */
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) {
			c.add(Restrictions.eq("id.lineaseguroid", this.lineaSeguroId));
		}
	
		final String codmodulo = this.codmodulo + ";99999";
		if (FiltroUtils.noEstaVacio(codmodulo)) {
			String[] codsModulos = codmodulo.split(";");
			c.add(Restrictions.in("id.codmodulo", codsModulos));
		}
		
		if (FiltroUtils.noEstaVacio(this.codriesgocubierto))
		{
			c.add(Restrictions.disjunction()
			.add(Restrictions.eq("id.codriesgocubierto", this.codriesgocubierto))
			.add(Restrictions.eq("id.codriesgocubierto", new BigDecimal("999"))));
		}		
		
		return c;
	}
	public Long getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(Long lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	public String getCodmodulo() {
		return codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}

	public BigDecimal getCodriesgocubierto() {
		return codriesgocubierto;
	}

	public void setCodriesgocubierto(BigDecimal codriesgocubierto) {
		this.codriesgocubierto = codriesgocubierto;
	}

	public Character getElegible() {
		return elegible;
	}

	public void setElegible(Character elegible) {
		this.elegible = elegible;
	}

}
