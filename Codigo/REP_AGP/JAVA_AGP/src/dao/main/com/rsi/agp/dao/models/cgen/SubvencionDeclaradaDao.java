package com.rsi.agp.dao.models.cgen;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.SubvencionDeclarada;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;


public class SubvencionDeclaradaDao extends BaseDaoHibernate implements ISubvencionDeclaradaDao {
	
	private static final Log logger = LogFactory.getLog(SubvencionDeclaradaDao.class);
	
	@SuppressWarnings("unchecked")
	public Map<BigDecimal, SubvencionDeclarada> getSubvencionesDeclaradas(es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada[] subvenciones){
		String[] subvencionesStr = new String[subvenciones.length];
		for (int i = 0; i < subvenciones.length; i++){
			es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada s = subvenciones[i];
			subvencionesStr[i] = s.getTipo() + "";
		}		
		Map<BigDecimal, SubvencionDeclarada> subvs = new HashMap<BigDecimal, SubvencionDeclarada>();
		try {
			String sql = "select codigo, descripcion from (" +
					"select s.codtiposubvenesa as codigo, s.destiposubvenesa as descripcion from o02agpe0.tb_sc_c_tipo_subv_enesa s " +
					"union " +
					"select c.codtiposubvccaa as codigo, c.destiposubvccaa as descripcion from o02agpe0.tb_sc_c_tipo_subv_ccaa c)" +
					"where codigo in " + StringUtils.toValoresSeparadosXComas(subvencionesStr, false);
			List<Object[]> busqueda = (List<Object[]>) this.getObjectsBySQLQuery(sql);
			
			for (Object[] elemento : busqueda){
				//Para cada elemento de la lista inserto un elemento en el mapa: clave - codigo, valor - descripción
				subvs.put(new BigDecimal(elemento[0] + ""), new SubvencionDeclarada(new BigDecimal(elemento[0]+""), elemento[1]+""));
			}			
			return subvs;
		} catch (Exception e) {
			logger.error("Error al obtener las descripciones de las subvenciones", e);
		}		
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public Map<BigDecimal, SubvencionDeclarada> getSubvencionesDeclaradasGanado(es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvenciones) {
		String[] subvencionesStr = new String[subvenciones.length];
		for (int i = 0; i < subvenciones.length; i++){
			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada s = subvenciones[i];
			subvencionesStr[i] = s.getTipo() + "";
		}		
		Map<BigDecimal, SubvencionDeclarada> subvs = new HashMap<BigDecimal, SubvencionDeclarada>();
		try {
			String sql = "select codigo, descripcion from (" +
					"select s.codtiposubvenesa as codigo, s.destiposubvenesa as descripcion from o02agpe0.tb_sc_c_tipo_subv_enesa s " +
					"union " +
					"select c.codtiposubvccaa as codigo, c.destiposubvccaa as descripcion from o02agpe0.tb_sc_c_tipo_subv_ccaa c)" +
					"where codigo in " + StringUtils.toValoresSeparadosXComas(subvencionesStr, false);
			List<Object[]> busqueda = (List<Object[]>) this.getObjectsBySQLQuery(sql);
			
			for (Object[] elemento : busqueda){
				//Para cada elemento de la lista inserto un elemento en el mapa: clave - codigo, valor - descripción
				subvs.put(new BigDecimal(elemento[0] + ""), new SubvencionDeclarada(new BigDecimal(elemento[0]+""), elemento[1]+""));
			}			
			return subvs;
		} catch (Exception e) {
			logger.error("Error al obtener las descripciones de las subvenciones", e);
		}		
		return null;
	}
	
	public TipoSubvencionEnesa getSubvencionENESA(BigDecimal codtiposubvenesa) {
	
		try {
			Session sesion = obtenerSession();
			TipoSubvencionEnesa tipo = (TipoSubvencionEnesa) sesion.get(TipoSubvencionEnesa.class, codtiposubvenesa);
			return tipo;
		}catch (Exception e) {
			logger.error("Error al obtener tipo de subvención", e);
		}		
		return null;
	}
	
	public Organismo getSubvencionCCAA(Character codtipoOrganismo) {
		
		try {
			Session sesion = obtenerSession();
			Organismo tipo = (Organismo) sesion.get(Organismo.class, codtipoOrganismo);
			return tipo;
		}catch (Exception e) {
			logger.error("Error al obtener tipo de subvención", e);
		}		
		return null;
	}
}
