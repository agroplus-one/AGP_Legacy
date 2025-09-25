package com.rsi.agp.dao.models.familias;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.familias.Familia;
import com.rsi.agp.dao.tables.familias.GrupoFamilia;
import com.rsi.agp.dao.tables.familias.LineaFamilia;
import com.rsi.agp.dao.tables.familias.LineaFamiliaId;

public class MtoFamiliasDao extends BaseDaoHibernate implements IMtoFamiliasDao {
 
	private static final Log logger = LogFactory.getLog(MtoFamiliasDao.class);
	private static final String ERROR_ACCESO_BBDD = "Se ha producido un error en el acceso a la BBDD";

	@SuppressWarnings("unchecked")
	@Override
	public List<LineaFamilia> listLineaGrupoNegocios(LineaFamilia lineaFamilia) throws DAOException {
		
		logger.debug("MtoFamiliasDao - listLineaGrupoNegocios");

		
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(LineaFamilia.class);
			

			criteria.createAlias("familia", "f");
			criteria.createAlias("linea", "l");
			
			if (FiltroUtils.noEstaVacio(lineaFamilia.getId().getCodFamilia())) {
				criteria.add(Restrictions.eq("id.codFamilia", lineaFamilia.getId().getCodFamilia()));
			}
			
			
			if (FiltroUtils.noEstaVacio(lineaFamilia.getId().getGrupoNegocio())) {
				criteria.add(Restrictions.eq("id.grupoNegocio", lineaFamilia.getId().getGrupoNegocio()));
			}
			
			
			if (FiltroUtils.noEstaVacio(lineaFamilia.getId().getCodLinea())) {
				criteria.add(Restrictions.eq("id.codLinea", lineaFamilia.getId().getCodLinea()));
			}
			
			if (FiltroUtils.noEstaVacio(lineaFamilia.getLinea().getDeslinea())) {
				criteria.add(Restrictions.like("l.deslinea","%"+  lineaFamilia.getLinea().getDeslinea() + "%"));

			}
			
			
			if (FiltroUtils.noEstaVacio(lineaFamilia.getId().getCodGrupoFamilia())) {
				criteria.add(Restrictions.eq("id.codGrupoFamilia", lineaFamilia.getId().getCodGrupoFamilia()));
			}
			
			
			if (FiltroUtils.noEstaVacio(lineaFamilia.getFamilia().getNomFamilia())) {
				criteria.add(Restrictions.like("f.nomFamilia","%"+ lineaFamilia.getFamilia().getNomFamilia() + "%"));
			}


			return criteria.list();
			
		} catch (Exception e) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + e.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}		
		

	}
	
	
	@SuppressWarnings("unchecked")
	public List<GruposNegocio> getGruposNegocio() throws DAOException {
		
		logger.debug("MtoFamiliasDao - getGruposNegocio");

		
		Session session = obtenerSession();
		List<GruposNegocio> aux = new ArrayList<GruposNegocio>();
		try {
			Criteria criteria = session.createCriteria(GruposNegocio.class);
			
			criteria.add(Restrictions.ne("grupoNegocio", Constants.GRUPO_NEGOCIO_GENERICO));		
			aux = criteria.list();

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		return aux;
		
	}
	
	
	/**
	 * Devuelve la linea de familia pasandole el plan y linea
	 */
	@Override
	public LineaFamilia getLineaFamilia(LineaFamiliaId lineaFamiliaId) {
		
		logger.debug("MtoFamiliasDao - getLineaFamilia");

		
		Session sesion = obtenerSession();
		try {			

			Criteria criteria = sesion.createCriteria(LineaFamilia.class);
			criteria.add(Restrictions.eq("id.codFamilia", lineaFamiliaId.getCodFamilia()));
			criteria.add(Restrictions.eq("id.grupoNegocio", lineaFamiliaId.getGrupoNegocio()));
			criteria.add(Restrictions.eq("id.codLinea", lineaFamiliaId.getCodLinea()));
			criteria.add(Restrictions.eq("id.codGrupoFamilia", lineaFamiliaId.getCodGrupoFamilia()));

			
	
			
			LineaFamilia res =  (LineaFamilia) criteria.uniqueResult();
			
		
			sesion.clear();
			
			return res;
			
		} catch (Exception ex) {
			logger.error("Error al obtener la linea de familia", ex);
		}
		

		return null;
	}


	@Override
	public Familia getFamilia(Long codFamilia) {
		
		logger.debug("MtoFamiliasDao - getFamilia");

		Session sesion = obtenerSession();
		try {			
			logger.debug("getFamilia");


			Criteria criteria = sesion.createCriteria(Familia.class);
			criteria.add(Restrictions.eq("codFamilia", codFamilia));
			
			return (Familia) criteria.uniqueResult();
			
			
		} catch (Exception ex) {
			logger.error("Error al obtener la familia indicadas", ex);
		}
		
		// No se ha encontrado registros para los parametros indicados
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<GrupoFamilia> getGrupos() throws DAOException {
		
		logger.debug("MtoFamiliasDao - getGrupos");

		
		Session session = obtenerSession();

		try {
			Criteria criteria = session.createCriteria(GrupoFamilia.class);
			
			return criteria.list();

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		
	}

	
	
	
	public void updateLineaFamilia(LineaFamilia familiaInicial, LineaFamilia familiaModificada) throws DAOException {
		
		logger.debug("MtoFamiliasDao - updateLineaFamilia");

		
		String sql = String.format("update o02agpe0.LIN_FAMILIA_SEGURO set cod_familia_seg = %s, cod_grupo_familia_seg = %s,codlinea = %s,grupo_negocio = %s where cod_familia_seg = %s and cod_grupo_familia_seg = %s and codlinea = %s and grupo_negocio = %s", 
				familiaModificada.getId().getCodFamilia(),
				familiaModificada.getId().getCodGrupoFamilia(),
				familiaModificada.getId().getCodLinea(),
				familiaModificada.getId().getGrupoNegocio(),

				familiaInicial.getId().getCodFamilia(),
				familiaInicial.getId().getCodGrupoFamilia(),
				familiaInicial.getId().getCodLinea(),
				familiaInicial.getId().getGrupoNegocio()
				);
		
		logger.debug("SQL -> " + sql);

		
		try {
			Session session = obtenerSession();

			Query update = session
					.createSQLQuery(sql);
			update.executeUpdate();
			

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}		
		

	}
	
	public void alta(LineaFamilia lineaFamilia)  throws DAOException {
		
		logger.debug("MtoFamiliasDao - alta");
		Session session = obtenerSession();

		try {
			session.save(lineaFamilia);
			session.flush();
			session.refresh(lineaFamilia);
			
		} catch(ConstraintViolationException e) {
			throw new DAOException("La línea no existe.");

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}		
		
	
	}
	
	
	
	
}