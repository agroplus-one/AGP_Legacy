package com.rsi.agp.dao.models.coberturas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.coberturas.CuadroCobertura;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;

public class CuadroCoberturasDao extends BaseDaoHibernate implements ICuadroCoberturasDao {

	@SuppressWarnings("unchecked")
	public List<CuadroCobertura> getCoberturas(CaracteristicaModulo caracteristicaModulo){
		Session session = obtenerSession();
		List<CuadroCobertura> lista = new ArrayList<CuadroCobertura>();
		
		try{
			
			Criteria criteria =	session.createCriteria(CuadroCobertura.class);
			criteria.add(Restrictions.eq("lineaseguroid", caracteristicaModulo.getId().getLineaseguroid()));
			criteria.add(Restrictions.eq("codmodulo", caracteristicaModulo.getId().getCodmodulo()));
			criteria.add(Restrictions.eq("filamodulo", caracteristicaModulo.getId().getFilamodulo()));
			criteria.add(Restrictions.eq("columnamodulo", caracteristicaModulo.getId().getColumnamodulo()));
			criteria.addOrder(Order.asc("filamodulo"));
			criteria.addOrder(Order.asc("columnamodulo"));
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("Error al obtener el listado de coberturas para motar el cuadro", e);
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<CuadroCobertura> getCoberturas(Long lineaseguroid, String codmodulo, 
			BigDecimal filamodulo, BigDecimal columnamodulo, BigDecimal codconcepto, BigDecimal valor){
		Session session = obtenerSession();
		List<CuadroCobertura> lista = new ArrayList<CuadroCobertura>();
		
		try{
			
			Criteria criteria =	session.createCriteria(CuadroCobertura.class);
			criteria.add(Restrictions.eq("lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("codmodulo", codmodulo));
			criteria.add(Restrictions.eq("filamodulo", filamodulo));
			criteria.add(Restrictions.eq("columnamodulo", columnamodulo));
			criteria.add(Restrictions.eq("codconcepto", codconcepto));
			criteria.add(Restrictions.eq("codigo", valor+""));
			criteria.addOrder(Order.asc("filamodulo"));
			criteria.addOrder(Order.asc("columnamodulo"));
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("Error al obtener el listado de coberturas para motar el cuadro", e);
		}
		
		return lista;
	}

	@Override
	public AnexoModificacion getAnexo(String idAnexo) {
		AnexoModificacion anexoModificacion = (AnexoModificacion) this.getObject(AnexoModificacion.class, Long.parseLong(idAnexo));
		return anexoModificacion;
	}
	
	

}
