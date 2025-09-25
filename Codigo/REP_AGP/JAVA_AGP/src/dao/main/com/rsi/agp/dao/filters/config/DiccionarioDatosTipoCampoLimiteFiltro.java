package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class DiccionarioDatosTipoCampoLimiteFiltro implements Filter {

	private BigDecimal idTipoCampoLimite;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	@Override
	public Criteria getCriteria(Session sesion) {
		String tablaTipoCampoLimite = tablaTipoCampoLimite();
		
		Criteria criteria = sesion.createCriteria(DiccionarioDatos.class);
		criteria.add(Restrictions.sqlRestriction("codconcepto IN ( SELECT distinct(codconcepto) FROM "+tablaTipoCampoLimite+")"));
		criteria.addOrder(Order.asc("codconcepto"));
		return criteria;
	}
	
	private String tablaTipoCampoLimite(){

		try{
		
			return bundle.getString("diccionarioDatos.campoLimite.idTipoCampoLimite."+idTipoCampoLimite);

		}catch(MissingResourceException mre){
			throw new IllegalArgumentException("No exite tabla para el Tipo de Campo Límite con id "+idTipoCampoLimite);
		}
	
	}

	public BigDecimal getIdTipoCampoLimite() {
		return idTipoCampoLimite;
	}

	public void setIdTipoCampoLimite(BigDecimal idTipoCampoLimite) {
		this.idTipoCampoLimite = idTipoCampoLimite;
	}
	
	

}
