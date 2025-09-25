package com.rsi.agp.dao.tables.commons;

import java.math.BigDecimal;

import com.rsi.agp.dao.tables.cpl.Modulo;

public class Limites {
	private BigDecimal limiteinfrdto;
    private BigDecimal limitesuprdto;
    private Character apprdto;
    
    
	public Character getApprdto() {
		return apprdto;
	}
	public void setApprdto(Character apprdto) {
		this.apprdto = apprdto;
	}
	public BigDecimal getLimiteinfrdto() {
		return limiteinfrdto;
	}
	public void setLimiteinfrdto(BigDecimal limiteinfrdto) {
		this.limiteinfrdto = limiteinfrdto;
	}
	public BigDecimal getLimitesuprdto() {
		return limitesuprdto;
	}
	public void setLimitesuprdto(BigDecimal limitesuprdto) {
		this.limitesuprdto = limitesuprdto;
	}
    
    
    
    
    
    
}
