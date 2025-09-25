package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Tomador;

/**
 * Clase para manejar las operaciones de base de datos de los tomadores.
 * 
 * Las operaciones básicas se realizan en BaseDaoHibernate. 
 * Aquí deberemos definir las operaciones específicas para los tomadores.
 * 
 * @author U028783
 *
 */
public class TomadorDao extends BaseDaoHibernate implements ITomadorDao{
	private static final Log LOGGER = LogFactory.getLog(TomadorDao.class);
	@Override
	public List<Tomador> getTomadoresGrupoEntidad(Tomador tomadorBean, List<BigDecimal> listaEnt) throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(Tomador.class);
			criteria.addOrder(Order.asc("id.codentidad"));
			criteria.createAlias("localidad", "loc", CriteriaSpecification.LEFT_JOIN);
			if (FiltroUtils.noEstaVacio(tomadorBean)) {				
				criteria.add(Restrictions.allEq(getMapaTomador(tomadorBean)));
			}
			if(tomadorBean.getRepreNombre()!= null)
				if(!tomadorBean.getRepreNombre().equals("")){
				   String nombre = tomadorBean.getRepreNombre();
				   criteria.add(Restrictions.ilike("repreNombre", "%".concat(nombre).concat("%")));
			    }
			if(tomadorBean.getRepreAp1()!= null)
				if(!tomadorBean.getRepreAp1().equals("")){
				   String ape1 = tomadorBean.getRepreAp1();
				   criteria.add(Restrictions.ilike("repreAp1", "%".concat(ape1).concat("%")));
			    }
			if(tomadorBean.getRepreAp2()!= null)
				if(!tomadorBean.getRepreAp2().equals("")){
				   String ape2 = tomadorBean.getRepreAp2();
				   criteria.add(Restrictions.ilike("repreAp2", "%".concat(ape2).concat("%")));
			    }
			LOGGER.info("---BigDecimal[] listaEnt size = " + listaEnt.size());
//			Si el perfil pertenece a algun grupo de Entidades
			if(listaEnt.size() > 0){
				if(tomadorBean.getId().getCodentidad() != null){
					LOGGER.info("Sí listaEnt > 0 Filtro por codentidad");
					criteria.add(Restrictions.eq("id.codentidad", tomadorBean.getId().getCodentidad()));
				}else{
					LOGGER.info("Sí listaEnt > 0 Filtro por grupo entidades");
					criteria.add(Restrictions.in("id.codentidad", listaEnt));
				}
			}else{
				final BigDecimal codEntidad = tomadorBean.getId().getCodentidad();
				if (FiltroUtils.noEstaVacio(codEntidad)) {
					LOGGER.info("Sí listaEnt = 0 Filtro por codentidad");
					criteria.add(Restrictions.eq("id.codentidad", codEntidad));
				}
			}
			if(tomadorBean.getId().getCodentidad() == null){
				LOGGER.info("Filtro de todos los tomadores");
			}
			return criteria.list();
			
		} catch (Exception e) {
			LOGGER.info("Error al obtener el listado de tomadores", e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	private final Map<String, Object> getMapaTomador(Tomador tomador) {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final String cif = tomador.getId().getCiftomador();
		if (FiltroUtils.noEstaVacio(cif)) {
			mapa.put("id.ciftomador", cif);
		}

		final String razonSocial = tomador.getRazonsocial();
		if (FiltroUtils.noEstaVacio(razonSocial)) {
			mapa.put("razonsocial", razonSocial);
		}

		final BigDecimal codProvincia = tomador.getLocalidad().getId().getCodprovincia();
		if (FiltroUtils.noEstaVacio(codProvincia)) {
			mapa.put("loc.id.codprovincia", codProvincia);
		}

		final String nomLocalidad = tomador.getLocalidad().getNomlocalidad();
		if (FiltroUtils.noEstaVacio(nomLocalidad)) {
			mapa.put("loc.nomlocalidad", nomLocalidad);
		}

		final BigDecimal cp = tomador.getCodpostal();
		if (FiltroUtils.noEstaVacio(cp)) {
			mapa.put("codpostal", cp);
		}

		final String telefono = tomador.getTelefono();
		if (FiltroUtils.noEstaVacio(telefono)) {
			mapa.put("telefono", telefono);
		}

		final String numVia = tomador.getNumvia();
		if (FiltroUtils.noEstaVacio(numVia)) {
			mapa.put("numvia", numVia);
		}

		final String movil = tomador.getMovil();
		if (FiltroUtils.noEstaVacio(movil)) {
			mapa.put("movil", movil);
		}

		final String email = tomador.getEmail();
		if (FiltroUtils.noEstaVacio(email)) {
			mapa.put("email", email);
		}
		
		/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Inicio */
		final String email2 = tomador.getEmail2();
		if (FiltroUtils.noEstaVacio(email2)) {
			mapa.put("email2", email2);
		}
		
		final String email3 = tomador.getEmail3();
		if (FiltroUtils.noEstaVacio(email3)) {
			mapa.put("email3", email3);
		}
		/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Fin */

		final String piso = tomador.getPiso();
		if (FiltroUtils.noEstaVacio(piso)) {
			mapa.put("piso", piso);
		}

		final String bloque = tomador.getBloque();
		if (FiltroUtils.noEstaVacio(bloque)) {
			mapa.put("bloque", bloque);
		}

		final String escalera = tomador.getEscalera();
		if (FiltroUtils.noEstaVacio(escalera)) {
			mapa.put("escalera", escalera);
		}

		final String clave = tomador.getVia().getClave();
		if (FiltroUtils.noEstaVacio(clave)) {
			mapa.put("via.clave", clave);
		}

		final String domicilio = tomador.getDomicilio();
		if (FiltroUtils.noEstaVacio(domicilio)) {
			mapa.put("domicilio", domicilio);
		}

		final BigDecimal codLocalidad = tomador.getLocalidad().getId().getCodlocalidad();
		if (FiltroUtils.noEstaVacio(codLocalidad)) {
			mapa.put("loc.id.codlocalidad", codLocalidad);
		}

		final String sublocalidad = tomador.getLocalidad().getId().getSublocalidad();
		if (FiltroUtils.noEstaVacio(sublocalidad)) {
			mapa.put("loc.id.sublocalidad", sublocalidad);
		}
		
		/*final String repreNombre = tomador.getRepreNombre();
		if (FiltroUtils.noEstaVacio(repreNombre)) {
			mapa.put("repreNombre", repreNombre);
		}
		
		final String repreAp1 = tomador.getRepreAp1();
		if (FiltroUtils.noEstaVacio(repreAp1)) {
			mapa.put("repreAp1", repreAp1);
		}
		
		final String repreAp2 = tomador.getRepreAp2();
		if (FiltroUtils.noEstaVacio(repreAp2)) {
			mapa.put("repreAp2", repreAp2);
		}*/
		
		final String repreNif = tomador.getRepreNif();
		if (FiltroUtils.noEstaVacio(repreNif)) {
			mapa.put("repreNif", repreNif);
		}
		
		final Character envioAPagos = tomador.getEnvioAPagos();
		if (FiltroUtils.noEstaVacio(envioAPagos)) {
			mapa.put("envioAPagos", envioAPagos);
		}
		
		return mapa;
	}

}
