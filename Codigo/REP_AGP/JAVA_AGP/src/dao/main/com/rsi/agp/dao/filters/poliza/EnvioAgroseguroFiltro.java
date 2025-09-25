package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;

public class EnvioAgroseguroFiltro implements Filter {
	
	private Long idEnvio;
	private Long idPoliza;
    private String codModulo;
    private BigDecimal filaComparativa;
    private String tipoEnvio;
    
    public EnvioAgroseguroFiltro() {
    	super();
    }
    
	public EnvioAgroseguroFiltro(Long idEnvio, Long idPoliza, 
								 String codModulo,
								 BigDecimal filaComparativa, String tipoEnvio) {
		super();
		this.idEnvio = idEnvio;
		this.idPoliza = idPoliza;
		this.codModulo = codModulo;
		this.filaComparativa = filaComparativa;
		this.tipoEnvio = tipoEnvio;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {  //Le pasamos los criterios para filtrar en el controller
		final Criteria criteria = sesion.createCriteria(EnvioAgroseguro.class);
		
		if (FiltroUtils.noEstaVacio(idEnvio)) {
			criteria.add(Restrictions.eq("id",idEnvio));
		}
		/*if (FiltroUtils.noEstaVacio(idPoliza)) {
			criteria.add(Restrictions.eq("poliza.idpoliza",idPoliza));
		}
		if (FiltroUtils.noEstaVacio(codModulo)) {
			criteria.add(Restrictions.eq("codmodulo",codModulo));
		}
		if (FiltroUtils.noEstaVacio(filaComparativa)) {
			criteria.add(Restrictions.eq("filacomparativa",filaComparativa));
		}
		if (FiltroUtils.noEstaVacio(tipoEnvio)) {
			criteria.add(Restrictions.eq("tipoenvio",tipoEnvio));
		}*/
		
		return criteria.addOrder(Order.desc("fechaEnvio"));
	}

	/**
	 * @return the idPoliza
	 */
	public Long getIdPoliza() {
		return idPoliza;
	}

	/**
	 * @param idPoliza the idPoliza to set
	 */
	public void setIdPoliza(Long idPoliza) {
		this.idPoliza = idPoliza;
	}

	/**
	 * @return the codModulo
	 */
	public String getCodModulo() {
		return codModulo;
	}

	/**
	 * @param codModulo the codModulo to set
	 */
	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	/**
	 * @return the filaComparativa
	 */
	public BigDecimal getFilaComparativa() {
		return filaComparativa;
	}

	/**
	 * @param filaComparativa the filaComparativa to set
	 */
	public void setFilaComparativa(BigDecimal filaComparativa) {
		this.filaComparativa = filaComparativa;
	}

	public String getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(String tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public Long getIdEnvio() {
		return idEnvio;
	}

	public void setIdEnvio(Long idEnvio) {
		this.idEnvio = idEnvio;
	}

	
}
