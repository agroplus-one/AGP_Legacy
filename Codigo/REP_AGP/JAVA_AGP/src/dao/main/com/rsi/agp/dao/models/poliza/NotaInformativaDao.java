/**
 * 
 */
package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.NotaInformativa;

/**
 * @author T-Systems
 *
 */
public class NotaInformativaDao extends BaseDaoHibernate implements INotaInformativaDao{
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getNotaInformativa(BigDecimal codEntidad, BigDecimal producto) {
		Map<String, Object> mapNotas = new HashMap<String, Object>();
		List<NotaInformativa> lstNotas = new ArrayList<NotaInformativa>();
		final Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(NotaInformativa.class);
			criteria.add(Restrictions.eq ("id.codentidad", codEntidad));
			criteria.add(Restrictions.eq ("id.producto", producto));
			criteria.addOrder(Order.asc("id.orden"));
			lstNotas = criteria.list();
			mapNotas.put("lstNotas",lstNotas);
			return mapNotas;
		} 
		catch (Exception e) {
			logger.error("Error al recoger la nota inoformativa para la entidad " + codEntidad, e);
		} 
			
		return mapNotas;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getDatosParaNotaInfoGenerica(Long idPoliza) throws DAOException {
		logger.debug("init - [NotaInformativaDao] getDatosParaNotaInfoGenerica");
		List<Object> datos = null;	
		try{
			final Session session = obtenerSession();
			String sql = "select NVL(TRIM(SM.NOMSUBENTIDAD), SM.NOMBRE||' '||SM.Apellido1||' '||SM.Apellido2) as NOMSUBENTIDAD, v.nombre as nombrevia, tm.domicilio, tm.numvia, sm.codpostal,pr.nomprovincia, co.codentidad, sm.nifcif" + 
					" from o02agpe0.tb_subentidades_mediadoras sm, o02agpe0.tb_polizas po,o02agpe0.tb_colectivos co," +
					" o02agpe0.tb_tomadores tm, o02agpe0.tb_vias v, o02agpe0.tb_provincias pr where  co.id = po.idcolectivo" +
					" and tm.codprovincia = pr.codprovincia and sm.codentidad = co.entmediadora and sm.codsubentidad = co.subentmediadora " +
					" and po.idcolectivo = co.id and co.ciftomador = tm.ciftomador and co.codentidad = tm.codentidad " +
					" and tm.clavevia = v.clave and po.idpoliza = " + idPoliza;						
			datos = session.createSQLQuery(sql).list();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos para la nota info generica ",ex);
		}
		return datos;
	}
	
	


}
