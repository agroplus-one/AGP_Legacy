package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;



public class ConsultaPolizaSbpSort implements CriteriaCommand {
	List<Sort> sorts = new ArrayList<Sort>();
	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
            buildCriteria(criteria, sort.getProperty(), sort.getOrder());
        }
		return criteria;
	}

	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));
		
	}
	
	private void buildCriteria(Criteria criteria, String property, String order) {
        if (order.equals(Sort.ASC)) {// ***** ASC *****
        	// Entidad
    		if (property.equals("colectivo.tomador.id.codentidad")){
   				criteria.addOrder(Order.asc("col.tomador.id.codentidad"));
    		}
    		// Pet. 17094 ** MODIF TAM (15.03.2022) ** Inicio */
    		// Incluimos los campos de Ent. Mediador y SubEnt. Mediadora en la ordenaci髇 */
    		else if (property.equals("colectivo.subentidadMediadora.id.codentidad")){
    			criteria.addOrder(Order.asc("col.subentidadMediadora.id.codentidad"));
    		}else if(property.equals("colectivo.subentidadMediadora.id.codsubentidad")){
    			criteria.addOrder(Order.asc("col.subentidadMediadora.id.codsubentidad"));
    		}
    		// Oficina
    		else if (property.equals("oficina")){
    				criteria.addOrder(Order.asc("oficina"));
    		}
    		// Usuario
    		else if (property.equals("usuario.codusuario")){
    			criteria.addOrder(Order.asc("usu.codusuario"));
    		}
    		// Plan
    		else if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.asc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.asc("lin.codlinea"));
    		}
    		// Clase
    		else if (property.equals("clase")){
    			criteria.addOrder(Order.asc("clase"));
    		}
    		// Referencia de p贸liza
    		else if (property.equals("referencia")){
   				criteria.addOrder(Order.asc("referencia"));
    		}
    		// Referencia colectivo
    		else if (property.equals("colectivo.idcolectivo")){	        			
    			criteria.addOrder(Order.asc("col.idcolectivo"));
    		}
    		// DC colectivo
    		else if (property.equals("colectivo.dccolectivo")){	        			
    			criteria.addOrder(Order.asc("col.dc"));
    		}
    		// M贸dulo
    		else if (property.equals("codmodulo")){	
   				criteria.addOrder(Order.asc("codmodulo"));
    		}
    		// CIF/NIF asegurado
    		else if (property.equals("asegurado.nifcif")){	        			
    			criteria.addOrder(Order.asc("ase.nifcif"));
    		}
    		// Nombre asegurado
    		else if (property.equals("asegurado.nombre")){	        			
    			criteria.addOrder(Order.asc("ase.nombre"));
    		}
    		// Estado de la poliza
    		else if (property.equals("estadopoliza.idestado")){	        			
    			criteria.addOrder(Order.asc("est.idestado"));
    		}
    		else if (property.equals("estadopoliza.idestado")){	        			
    			criteria.addOrder(Order.asc("est.idestado"));
    		}
    		

        } else if (order.equals(Sort.DESC)) {// ***** DESC *****
        	// Entidad
    		if (property.equals("colectivo.tomador.id.codentidad")){
   				criteria.addOrder(Order.desc("col.tomador.id.codentidad"));
    		}
    		// Pet. 17094 ** MODIF TAM (15.03.2022) ** Inicio */
    		// Incluimos los campos de Ent. Mediador y SubEnt. Mediadora en la ordenaci髇 */
    		// Entidad Mediadora
    		else if (property.equals("colectivo.subentidadMediadora.id.codentidad")){
    			criteria.addOrder(Order.desc("col.subentidadMediadora.id.codentidad"));
       		// SubEntidad Mediadora    			
    		}else if(property.equals("colectivo.subentidadMediadora.id.codsubentidad")){
    			criteria.addOrder(Order.desc("col.subentidadMediadora.id.codsubentidad"));
    		}
    		// Oficina
    		else if (property.equals("oficina")){
   				criteria.addOrder(Order.desc("oficina"));
    		}
    		// Usuario
    		else if (property.equals("usuario.codusuario")){
    			criteria.addOrder(Order.desc("usu.codusuario"));
    		}
    		// Plan
    		else if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.desc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.desc("lin.codlinea"));
    		}
    		// Clase
    		else if (property.equals("clase")){
    			criteria.addOrder(Order.desc("clase"));
    		}
    		// Referencia de p贸liza
    		else if (property.equals("referencia")){
   				criteria.addOrder(Order.desc("referencia"));
    		}
    		// Referencia colectivo
    		else if (property.equals("colectivo.idcolectivo")){	        			
    			criteria.addOrder(Order.desc("col.idcolectivo"));
    		}
    		// DC colectivo
    		else if (property.equals("colectivo.dccolectivo")){	        			
    			criteria.addOrder(Order.desc("col.dc"));
    		}
    		// M贸dulo
    		else if (property.equals("codmodulo")){	    
   				criteria.addOrder(Order.desc("codmodulo"));
    		}
    		// CIF/NIF asegurado
    		else if (property.equals("asegurado.nifcif")){	        			
    			criteria.addOrder(Order.desc("ase.nifcif"));
    		}
    		// Nombre asegurado
    		else if (property.equals("asegurado.nombre")){	        			
    			criteria.addOrder(Order.desc("ase.nombre"));
    		}
    		// Estado de la poliza
    		else if (property.equals("estadopoliza.idestado")){	        			
    			criteria.addOrder(Order.desc("est.idestado"));
    		}
    		
        }
    }
	
	private static class Sort {
        public final static String ASC = "asc";
        public final static String DESC = "desc";

        private final String property;
        private final String order;

        public Sort(String property, String order) {
            this.property = property;
            this.order = order;
        }

        public String getProperty() {
            return property;
        }

        public String getOrder() {
            return order;
        }
    }
}

