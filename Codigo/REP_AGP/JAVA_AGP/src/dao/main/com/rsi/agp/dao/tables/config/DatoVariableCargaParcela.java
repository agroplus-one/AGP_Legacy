package com.rsi.agp.dao.tables.config;

import java.math.BigDecimal;

public class DatoVariableCargaParcela  implements java.io.Serializable {
	
     
    private BigDecimal codconcepto;
    private String valor;

    public DatoVariableCargaParcela(BigDecimal codconcepto, String valor) {
    	this.codconcepto = codconcepto;
    	this.valor = valor;       
    }
    
    public BigDecimal getCodconcepto() {
        return this.codconcepto;
    }
    
    public void setCodconcepto(BigDecimal codconcepto) {
        this.codconcepto = codconcepto;
    }
    
    public String getValor() {
        return this.valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public String toString () {
    	return (codconcepto == null ? "" : codconcepto.toString()) + "#" + valor;
    }

}