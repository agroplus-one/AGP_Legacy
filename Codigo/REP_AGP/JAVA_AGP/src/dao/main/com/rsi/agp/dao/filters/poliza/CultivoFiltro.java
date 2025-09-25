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
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;


public class CultivoFiltro implements Filter{
	
	private BigDecimal codLinea;
	private BigDecimal codPlan;
	private BigDecimal codCultivo;
	private Integer posicion;
	private String filtro;
	private boolean soloNumero;
	List listClaseCultivo; 
	
	public CultivoFiltro() {
		super();
	}
	
	public CultivoFiltro(BigDecimal codLinea, BigDecimal codPlan){
		this.codLinea = codLinea;
		this.codPlan = codPlan;
		this.filtro = "";
		this.soloNumero = true;
	}
	
	public CultivoFiltro(BigDecimal codLinea, BigDecimal codPlan, BigDecimal codCultivo){
		this.codLinea = codLinea;
		this.codPlan = codPlan;
		this.codCultivo = codCultivo;
		this.filtro = "";
		this.soloNumero = true;
	}
	
	public CultivoFiltro(BigDecimal codLinea, BigDecimal codPlan, List<CultivoId> listClaseCultivo){
		this.codLinea = codLinea;
		this.codPlan = codPlan;
		this.filtro = "";
		this.soloNumero = true;
		this.listClaseCultivo = listClaseCultivo;		
	}
	
	public CultivoFiltro(Integer posicion, String filtro, BigDecimal codLinea, BigDecimal codPlan){
		this.codLinea=codLinea;
		this.codPlan=codPlan;
		this.posicion=posicion;
		this.filtro=filtro;
	}
	public CultivoFiltro(String filtro, BigDecimal codLinea, BigDecimal codPlan,boolean soloNumero){
		this.codLinea=codLinea;
		this.codPlan=codPlan;
		this.filtro=filtro;
		this.soloNumero= soloNumero;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Cultivo.class);
		criteria.createAlias("linea", "lin");
		Criterion crit1 = Restrictions.eq("lin.codlinea",this.codLinea);
        criteria.add(crit1);
        Criterion crit2 = Restrictions.eq("lin.codplan",this.codPlan);
        criteria.add(crit2);

        
        
        if(listClaseCultivo != null && !listClaseCultivo.contains(new BigDecimal("999"))){
			if(listClaseCultivo.size() > 0){
				listClaseCultivo.add(new BigDecimal("999"));
			}
			criteria.add(Restrictions.in("id.codcultivo", listClaseCultivo));
		}

        // Si no se ha especificado el código de cultivo        
        criteria.add((codCultivo == null) ? Restrictions.ne("id.codcultivo", new BigDecimal("999")) 
        			: Restrictions.eq("id.codcultivo", this.codCultivo));
        
        criteria.addOrder(Order.asc("descultivo"));
        
        
        if(!filtro.equals("")){		
			criteria.add(Restrictions.ilike("descultivo", "%" + filtro + "%"));			
		}
		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
        
        return criteria;
	}
}
