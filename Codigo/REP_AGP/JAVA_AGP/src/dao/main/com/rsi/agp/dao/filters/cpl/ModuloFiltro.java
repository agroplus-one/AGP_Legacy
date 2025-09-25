package com.rsi.agp.dao.filters.cpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;

public class ModuloFiltro implements Filter{
	private ModuloId moduloId;
	private Long lineaseguroid;
	private Character ppalComplementario;
	private List<String> lstModulosClase;
	
	public ModuloFiltro() {
	}

	public ModuloFiltro(final Long idLinea) {
		this.lineaseguroid = idLinea;
	}
	
	public ModuloFiltro(final Long idLinea, List<String> lstModulosClase) {
		this.lineaseguroid = idLinea;
		this.lstModulosClase = lstModulosClase;
	}
	
	public ModuloFiltro(final Long lineaseguroid ,final Character ppalCompl) {
		this.lineaseguroid = lineaseguroid;
		this.ppalComplementario = ppalCompl;
	}
	
	public Criteria getCriteria(Session sesion){
		Criteria criteria = sesion.createCriteria(Modulo.class);
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		
		if(FiltroUtils.noEstaVacio(moduloId)){
			criteria.add(Restrictions.eq("id", moduloId));
		}
		
		//Buscamos los módulos principales o complementarios
		if(FiltroUtils.noEstaVacio(ppalComplementario)){
			criteria.add(Restrictions.eq("ppalcomplementario", ppalComplementario));
		}
		
		//Quito del listado el módulo '99999'
		criteria.add(Restrictions.ne("id.codmodulo", "99999"));
		
		//filtramos por los modulos de la clase si no contiene ningún '99999'
		if (lstModulosClase !=null){
			if ((lstModulosClase.size() > 0) && (!lstModulosClase.contains("99999"))){	
			    criteria.add(Restrictions.in("id.codmodulo", lstModulosClase));
			}
		}
		
		//Ordenamos la lista de módulos y hacemos un DISTINCT por si nos devolviera módulos repetidos.
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//distinct en lugar de un id, de todo el modulo
		
		return criteria;
	}

	public ModuloId getModuloId() {
		return moduloId;
	}

	public void setModuloId(ModuloId moduloId) {
		this.moduloId = moduloId;
	}

	public Character getPpalComplementario() {
		return ppalComplementario;
	}

	public void setPpalComplementario(Character ppalComplementario) {
		this.ppalComplementario = ppalComplementario;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public List<String> getLstModulosClase() {
		return lstModulosClase;
	}

	public void setLstModulosClase(List<String> lstModulosClase) {
		this.lstModulosClase = lstModulosClase;
	}

}
