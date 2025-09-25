package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class RefPolizaDao extends BaseDaoHibernate implements IRefPolizaDao {

	@Override
	public List getReferenciasAgricolas() {
		
		return this.getObjectsBySQLQuery("select * from tb_referencias_agricolas where fechaenvio is not null");
		 
	}

	
	
	public Poliza getPoliza(String refPoliza,Character tipo, BigDecimal codPlan) {
		logger.debug("init -  getPolizaByReferencia");
		Session session = obtenerSession();
		List listaPolizas=null;
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("linea", "linea");
			criteria.createAlias("estadoPoliza", "estadoPoliza");
			criteria.add(Restrictions.eq("referencia", refPoliza));
			criteria.add(Restrictions.eq("tipoReferencia", tipo));
			criteria.add(Restrictions.eq("linea.codplan", codPlan));
			criteria.add(Restrictions.eq("estadoPoliza.idestado", new BigDecimal(8)));
			listaPolizas= criteria.list();
		} catch (Exception e) {
			logger.error(e);
			
		}
		logger.debug("end - getPolizaByReferencia");
		return  ((listaPolizas == null || listaPolizas.isEmpty()) ? null : (Poliza) listaPolizas.get(0));
		
	}
	
	/**
	 * Método para enviar un correo al grupo 2 indicando quien ha lanzado el proceso de comprobación de referencias
	 * @param mensaje
	 */
	public void sendMail(String mensaje){
		String procedure = "PQ_ENVIO_CORREOS.enviarCorreo(P_CODGRUPO IN VARCHAR2,P_ASUNTO IN VARCHAR2, P_MENSAJE IN VARCHAR2)";			
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {	
			parametros.put("P_CODGRUPO", "2");
			parametros.put("P_ASUNTO", "Se ejecutó una llamada al servicio de comprobación de referencias");
			parametros.put("P_MENSAJE", mensaje);
			logger.debug("llamada al PL: PQ_ENVIO_CORREOS.enviarCorreo('2'," +
					"'Se ejecutó una llamada al servicio de comprobación de referencias','" + mensaje + "')");
		
			databaseManager.executeStoreProc(procedure, parametros);
		} catch (Exception e) {
			logger.error("error en la llamada al PL llamada al PL: PQ_ENVIO_CORREOS.enviarCorreo()",e);
		}
	}
}
