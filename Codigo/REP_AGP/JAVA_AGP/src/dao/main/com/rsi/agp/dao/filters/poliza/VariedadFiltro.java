package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.cpl.Variedad;


public class VariedadFiltro implements Filter{
	
	private Long codLinea;
	private Long codPlan;
	private Long lineaseguroid;
	private Integer posicion;
	private String filtro;
	private boolean soloNumero;
	private BigDecimal codCultivo;
	private List listClaseVariedad; 

	
	public VariedadFiltro() {
		super();
	}
	
	public VariedadFiltro(BigDecimal codCultivo,Long lineaseguroid){
		this.codCultivo = codCultivo;
		this.lineaseguroid = lineaseguroid;
	}
	
	public VariedadFiltro(BigDecimal codCultivo,Long lineaseguroid, List listClaseVariedad){
		this.codCultivo = codCultivo;
		this.lineaseguroid = lineaseguroid;
		this.listClaseVariedad = listClaseVariedad;
	}
	
	public VariedadFiltro(BigDecimal codCultivo){
		this.codCultivo = codCultivo;
	}
	
	public VariedadFiltro(Integer posicion, String filtro,
			Long codLinea, Long codPlan, BigDecimal codCultivo){
		this.codCultivo = codCultivo;
		this.codLinea=codLinea;
		this.codPlan=codPlan;
		this.posicion=posicion;
		this.filtro=filtro;
	}
	
	public VariedadFiltro(String filtro, Long codLinea,
		    Long codPlan,BigDecimal codCultivo,boolean soloNumero){
		this.codCultivo = codCultivo;
		this.codLinea=codLinea;
		this.codPlan=codPlan;
		this.soloNumero=soloNumero;
		this.filtro=filtro;
	}
	
	@Override
	public Criteria getCriteria(Session sesion){
		Criteria criteria = sesion.createCriteria(Variedad.class);

		criteria.add(Restrictions.ne("id.codvariedad", new BigDecimal("999")));
		criteria.addOrder(Order.asc("id.codcultivo"));
        if(FiltroUtils.noEstaVacio(filtro)){		
			criteria.add(Restrictions.ilike("desvariedad", "%" + filtro + "%"));			
		}
		if (!soloNumero && FiltroUtils.noEstaVacio(posicion)){
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if(FiltroUtils.noEstaVacio(lineaseguroid)){
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		}		
        if(FiltroUtils.noEstaVacio(codCultivo) && !codCultivo.equals(new BigDecimal("0"))){
        	criteria.add(Restrictions.eq("id.codcultivo",this.codCultivo));
        }
        if(FiltroUtils.noEstaVacio(codLinea)){
        	criteria.createAlias("linea", "lin");
        	criteria.add(Restrictions.eq("lin.codlinea",new BigDecimal(codLinea)));
        }
        
        
        if(listClaseVariedad != null && !listClaseVariedad.contains(new BigDecimal("999"))){
			if(listClaseVariedad.size() > 0){
				listClaseVariedad.add(new BigDecimal("999"));
			}
			criteria.add(Restrictions.in("id.codvariedad", listClaseVariedad));
		}

        

        return criteria;
	}

	public Long getCodLinea() {
		return codLinea;
	}

	public void setCodLinea(Long codLinea) {
		this.codLinea = codLinea;
	}

	public Long getCodPlan() {
		return codPlan;
	}

	public void setCodPlan(Long codPlan) {
		this.codPlan = codPlan;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public Integer getPosicion() {
		return posicion;
	}

	public void setPosicion(Integer posicion) {
		this.posicion = posicion;
	}

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public boolean isSoloNumero() {
		return soloNumero;
	}

	public void setSoloNumero(boolean soloNumero) {
		this.soloNumero = soloNumero;
	}

	public BigDecimal getCodCultivo() {
		return codCultivo;
	}

	public void setCodCultivo(BigDecimal codCultivo) {
		this.codCultivo = codCultivo;
	}

	public List getListClaseVariedad() {
		return listClaseVariedad;
	}

	public void setListClaseVariedad(List listClaseVariedad) {
		this.listClaseVariedad = listClaseVariedad;
	}
}