package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.filters.poliza.CultivoFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.Cultivo;

public class CultivoDao extends BaseDaoHibernate implements ICultivoDao {
 
	@SuppressWarnings("unchecked")
	@Override
	public Cultivo getCultivo (BigDecimal codPlan, BigDecimal codLinea, BigDecimal codCultivo) {
		
		
		try {
			CultivoFiltro filtro = new CultivoFiltro(codLinea, codPlan, codCultivo);
			List<Cultivo> lista = this.getObjects(filtro);
			
			if (lista != null && lista.size()>0) {
				return lista.get(0);
			}
		} catch (Exception e) {
			logger.error("Ocurrio un error al obtener el cultivo", e);
		}		
		return null;		
	}
}