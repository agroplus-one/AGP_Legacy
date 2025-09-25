package com.rsi.agp.dao.filters.log;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.log.ImportacionTabla;

public class ActivacionFiltro implements Filter 
{
	private String lineaseguroid;
	private List<BigDecimal> lstCodtabla;

	@Override
	public Criteria getCriteria(Session sesion){
		
		Criteria criteria = sesion.createCriteria(ImportacionTabla.class);
		criteria.createAlias("histImportaciones", "hi");
		
		if(!("").equals(StringUtils.nullToString(this.lineaseguroid))){
			criteria.add(Restrictions.eq("hi.linea.lineaseguroid", new Long(this.lineaseguroid)));
		}
		if(FiltroUtils.noEstaVacio(this.lstCodtabla)){
			criteria.add(Restrictions.in("id.codtablacondicionado", this.lstCodtabla));
		}
		criteria.addOrder(Order.desc("hi.fechaimport"));
		
		return criteria;
	}

	public String getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(String lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public List<BigDecimal> getLstCodtabla() {
		return lstCodtabla;
	}

	public void setLstCodtabla(List<BigDecimal> lstCodtabla) {
		this.lstCodtabla = lstCodtabla;
	}

		

}
