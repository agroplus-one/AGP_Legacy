package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.webapp.action.PolizaActualizadaController;





public class PolizaActualizadaSort  implements CriteriaCommand{

// Constantes	
private static final String CLASE = "clase";
private static final String FECHAENVIO = "fechaenvio";
private static final String ESTADO_POLIZA_IDESTADO = "estadoPoliza.idestado";
private static final String ASEGURADO_NOMBRE = "asegurado.nombre";
private static final String ASEGURADO_NIFCIF = "asegurado.nifcif";
private static final String CODMODULO = "codmodulo";
private static final String IMPORTE = "importe";
private static final String REFERENCIA = "referencia";
private static final String COLECTIVO_IDCOLECTIVO = "colectivo.idcolectivo";
private static final String LINEA_CODLINEA = "linea.codlinea";
private static final String LINEA_CODPLAN = "linea.codplan";
private static final String USUARIO_CODUSUARIO = "usuario.codusuario";
private static final String OFICINA = "oficina";
private static final String COLECTIVO_TOMADOR_ID_CODENTIDAD = "colectivo.tomador.id.codentidad";


List<Sort> sorts = new ArrayList<Sort>();
private Log logger = LogFactory.getLog(PolizaActualizadaController.class);
	
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
	
	public void clearSorts() {		
			sorts.clear();		
	}
	
	private void buildCriteria(Criteria criteria, String property, String order) {
		// Variable que indica si la ordenacion es ascendente o descendente
				boolean isAsc = Sort.ASC.equals(order);
				// Anade el orden al Criteria dependiendo del campo y del sentido de la ordenacion
				try {
					
				
				
				if (property.equals(COLECTIVO_TOMADOR_ID_CODENTIDAD)){
					criteria.addOrder(isAsc ? Order.asc(COLECTIVO_TOMADOR_ID_CODENTIDAD) : Order.desc(COLECTIVO_TOMADOR_ID_CODENTIDAD));	
				}
				if (property.equals(OFICINA)){
					criteria.addOrder(isAsc ? Order.asc(OFICINA) : Order.desc(OFICINA));
				}
				if (property.equals(USUARIO_CODUSUARIO)){
					criteria.addOrder(isAsc ? Order.asc(USUARIO_CODUSUARIO) : Order.desc(USUARIO_CODUSUARIO));
				}
				if (property.equals(LINEA_CODPLAN)){
					criteria.addOrder(isAsc ? Order.asc(LINEA_CODPLAN) : Order.desc(LINEA_CODPLAN));
				}
				if (property.equals(LINEA_CODLINEA)){
					criteria.addOrder(isAsc ? Order.asc(LINEA_CODLINEA) : Order.desc(LINEA_CODLINEA));
				}
				if (property.equals(COLECTIVO_IDCOLECTIVO)){
					criteria.addOrder(isAsc ? Order.asc(COLECTIVO_IDCOLECTIVO) : Order.desc(COLECTIVO_IDCOLECTIVO));
				}
				if (property.equals(REFERENCIA)){
					criteria.addOrder(isAsc ? Order.asc(REFERENCIA) : Order.desc(REFERENCIA));
				}
				if (property.equals(IMPORTE)){
					criteria.addOrder(isAsc ? Order.asc(IMPORTE) : Order.desc(IMPORTE));
				}
				if (property.equals(CODMODULO)){
					criteria.addOrder(isAsc ? Order.asc(CODMODULO) : Order.desc(CODMODULO));
				}
				if (property.equals(ASEGURADO_NIFCIF)){
					criteria.addOrder(isAsc ? Order.asc(ASEGURADO_NIFCIF) : Order.desc(ASEGURADO_NIFCIF));
				}
				if (property.equals(ASEGURADO_NOMBRE)){
					criteria.addOrder(isAsc ? Order.asc(ASEGURADO_NOMBRE) : Order.desc(ASEGURADO_NOMBRE));
				}
				if (property.equals(ESTADO_POLIZA_IDESTADO)){
					criteria.addOrder(isAsc ? Order.asc(ESTADO_POLIZA_IDESTADO) : Order.desc(ESTADO_POLIZA_IDESTADO));
				}
				if (property.equals(FECHAENVIO)){
					criteria.addOrder(isAsc ? Order.asc(FECHAENVIO) : Order.desc(FECHAENVIO));
				}
				if (property.equals(CLASE)){
					criteria.addOrder(isAsc ? Order.asc(CLASE) : Order.desc(CLASE));
				}
			}catch (Exception e) {
				logger.debug("end - PolizaActualizadaController");
			}
				
	}
		



	private static class Sort {
	    public final static String ASC = "asc";
	    
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


