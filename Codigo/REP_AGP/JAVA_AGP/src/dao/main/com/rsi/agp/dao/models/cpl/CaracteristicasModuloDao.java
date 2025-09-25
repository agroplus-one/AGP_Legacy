package com.rsi.agp.dao.models.cpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;

public class CaracteristicasModuloDao extends BaseDaoHibernate implements ICaracteristicasModuloDao{
	
	private enum TipoTablaRCM {
	       TABLA_RCM_AGRO ("riesgoCubiertoModulo"), TABLA_RCM_GANADO ("riesgoCubiertoModuloGanado");
	       
	       private String alias;
	       
	       TipoTablaRCM (String alias) {
	    	   this.alias = alias;
	       }
	       
	       public String toString () {
	    	   return this.alias;
	       }
	};

	
	@Override
	public List<CaracteristicaModulo> getCaracteristicasModulo(RiesgoCubiertoModulo rcm){
		return getCaracteristicasModuloGenerico (TipoTablaRCM.TABLA_RCM_AGRO, rcm.getId().getLineaseguroid(), rcm.getId().getCodmodulo(),
				 rcm.getId().getFilamodulo());
	}

	@Override
	public List<CaracteristicaModulo> getCaracteristicasModulo(RiesgoCubiertoModuloGanado rcm) {
		
		return getCaracteristicasModuloGenerico (TipoTablaRCM.TABLA_RCM_GANADO, rcm.getId().getLineaseguroid(), rcm.getId().getCodmodulo(),
												 rcm.getId().getFilamodulo());
	} 
	
	/**
	 * Devuelve el listado de características del módulo asociadas al plan/línea, módulo y fila correspondientes al riesgo cubierto del módulo
	 * @param alias Referencia a la tabla de riesgo cubierto del módulo correspondiente (Agrario o Ganado)
	 * @param linea Plan/línea asociado al RCM
	 * @param modulo Módulo asociado al RCM
	 * @param filaModulo Fila del módulo asociado al RCM
	 * @return Lista de objetos CaracteristicaModulo
	 */
	@SuppressWarnings("unchecked")
	private List<CaracteristicaModulo> getCaracteristicasModuloGenerico (TipoTablaRCM tablaRCM, Long linea, String modulo, Number filaModulo) {
		Session session = obtenerSession();
		List<CaracteristicaModulo> lista = new ArrayList<CaracteristicaModulo>();
		try{
			
			Criteria criteria =	session.createCriteria(CaracteristicaModulo.class);
			criteria.createAlias(tablaRCM.toString(), "rcm");
			criteria.add(Restrictions.eq("rcm.id.lineaseguroid", linea));
			criteria.add(Restrictions.eq("rcm.id.codmodulo", modulo));
			criteria.add(Restrictions.eq("rcm.id.filamodulo", filaModulo));
			criteria.addOrder(Order.asc("id.filamodulo"));
			criteria.addOrder(Order.asc("id.columnamodulo"));
			lista = criteria.list();
		} 
		catch (Exception e) {
			logger.fatal("Error al obtener el listado de características del módulo", e);
		}
		
		return lista;
	} 
}
