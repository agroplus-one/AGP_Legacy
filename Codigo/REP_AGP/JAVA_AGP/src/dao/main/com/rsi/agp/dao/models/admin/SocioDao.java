package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;

public class SocioDao extends BaseDaoHibernate implements ISocioDao {
	private static final Log logger = LogFactory.getLog(SocioDao.class);
			
	@Override
	public List<PolizaSocio> getPolizasByIdSocio(SocioId socioId) throws DAOException {
		logger.debug("init - getPolizasByIdAsegurado");
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(PolizaSocio.class);
			criteria.createAlias("socio", "socio");
			
			criteria.add(Restrictions.eq("socio.id.nif", socioId.getNif()));
			criteria.add(Restrictions.eq("socio.id.idasegurado", socioId.getIdasegurado()));
			
			logger.debug("end - getPolizasByIdAsegurado");
			return criteria.list();			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al acceder a base de datos ", e);
		}	
		
	}

	@Override
	public Set<Socio> getSociosByAseguradoPoliza(Asegurado asegurado, Poliza poliza) throws DAOException {
		logger.debug("init - getSociosByAseguradoPoliza");
		Session session = obtenerSession();
		Set<Socio> sociosAsegurado = new HashSet<Socio>();
		
		try {
			Criteria criteria = session.createCriteria(PolizaSocio.class);
			criteria.createAlias("socio", "socio");
			criteria.createAlias("poliza", "poliza");
			
			criteria.add(Restrictions.eq("socio.id.idasegurado", asegurado.getId()));
			criteria.add(Restrictions.eq("poliza.idpoliza", poliza.getIdpoliza()));
			
			if (criteria.list().size() > 0){
				List<PolizaSocio> resultado = criteria.list();
				
				for (int i = 0; i < resultado.size(); i++) {
					Socio sc = resultado.get(i).getSocio();
					sc.getSubvencionSocios();
					sociosAsegurado.add(sc);
				}
			}
			
			logger.debug("end - getSociosByAseguradoPoliza");
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al acceder a base de datos ", e);
		}	
		
		return sociosAsegurado;
	}

	@Override
	public List<Socio> getSociosActivosByAsegurado(Asegurado asegurado) throws DAOException {
		logger.debug("init - getSociosActivosByAsegurado");
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(Socio.class);
			criteria.createAlias("asegurado", "asegurado");
			
			criteria.add(Restrictions.eq("asegurado.id", asegurado.getId()));
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.not(Restrictions.eq("baja", 'S')))
					.add(Restrictions.isNull("baja")));	
					
			logger.debug("end - getSociosByAseguradoPoliza");
			return criteria.list();	
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al acceder a base de datos ", e);
		}	
		
	}

	@Override
	public List<Poliza> getPolizasSinGrabarByIdAsegurado(Long id) throws DAOException {
		logger.debug("init - getPolizasSinGrabarByIdAsegurado");
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("asegurado", "asegurado");
			criteria.createAlias("estadoPoliza", "estado");
			
			criteria.add(Restrictions.eq("asegurado.id", id));
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("estado.idestado", new BigDecimal(1)))
					.add(Restrictions.eq("estado.idestado", new BigDecimal(2))));	
					
			logger.debug("end - getPolizasSinGrabarByIdAsegurado");
			return criteria.list();	
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al acceder a base de datos ", e);
		}	
	}

	@Override
	public Set<Socio> getSociosByPolizaConSubvenciones(Poliza poliza) throws DAOException {
		logger.debug("init - getSociosByAseguradoPoliza");
		Session session = obtenerSession();
		Set<Socio> sociosAsegurado = new HashSet<Socio>();
		
		try {
			Criteria criteria = session.createCriteria(SubvencionSocio.class);
			criteria.createAlias("poliza", "poliza");
			
			//criteria.add(Restrictions.eq("socio.id.idasegurado", asegurado.getId()));
			criteria.add(Restrictions.eq("poliza.idpoliza", poliza.getIdpoliza()));
			
			if (criteria.list().size() > 0){
				List<SubvencionSocio> resultado = criteria.list();
				
				for (int i = 0; i < resultado.size(); i++) {
					sociosAsegurado.add(resultado.get(i).getSocio());
				}
			}
			
			logger.debug("end - getSociosByAseguradoPoliza");
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al acceder a base de datos ", e);
		}	
		
		return sociosAsegurado;	
	}

	@Override
	public BigDecimal getOrdenPolizaSocio(Long idpoliza) throws DAOException {
		
		Session sesion = obtenerSession();
		BigDecimal resultado = new BigDecimal(0);
		try {
			String sqlQuery = "select nvl(max(orden)+1, 1) from tb_poliza_socios where idpoliza=" + idpoliza;
		
			// Lanza la consulta y devuelve el valor del count
			resultado = ((BigDecimal)sesion.createSQLQuery (sqlQuery).list().get(0));
			if (StringUtils.nullToString(resultado).equals("")){
				resultado = new BigDecimal(1);
			}
			
			return resultado;
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de datos: ", ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	@Override
	public void actualizaOrdenPolizaSocio(Long idpoliza)	throws DAOException {
		try{
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("idPolizaParam", idpoliza);
			
			databaseManager.executeStoreProc("PQ_SOCIOS.actualizarOrden (idPolizaParam IN NUMBER)", parametros);        // ejecutamos PL
		}
		catch(Exception e) {
			logger.error("Ocurrió un error al actualizar el orden de los socios de la póliza " + idpoliza, e);
			throw new DAOException("Ocurrió un error al actualizar el orden de los socios de la póliza " + idpoliza, e);
		}
	}	


}
