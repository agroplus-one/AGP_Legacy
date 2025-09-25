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
	 * Devuelve el listado de caracter�sticas del m�dulo asociadas al plan/l�nea, m�dulo y fila correspondientes al riesgo cubierto del m�dulo
	 * @param alias Referencia a la tabla de riesgo cubierto del m�dulo correspondiente (Agrario o Ganado)
	 * @param linea Plan/l�nea asociado al RCM
	 * @param modulo M�dulo asociado al RCM
	 * @param filaModulo Fila del m�dulo asociado al RCM
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
			logger.fatal("Error al obtener el listado de caracter�sticas del m�dulo", e);
		}
		
		return lista;
	} 
}
