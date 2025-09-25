package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;


public class AseguradoFiltro implements Filter {

	private Asegurado asegurado;
	private Integer posicion;
	private String filtro;
	private BigDecimal codEntidad;
	private String nifcif;
	private String discriminante;
	private String perfil;
	private BigDecimal codEntMed;
	private BigDecimal codSubentMed;
	private Long idAsegurado;

	public AseguradoFiltro(final Asegurado asegurado) {
		this.asegurado = asegurado;
	}

	public AseguradoFiltro(final String filtro, final BigDecimal codEntidad) {
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	public AseguradoFiltro(final Integer posicion, final String filtro, final BigDecimal codEntidad) {
		this.posicion = posicion;
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	public AseguradoFiltro(final BigDecimal codentidad,
			final BigDecimal codEntMed, final BigDecimal codSubentMed,
			final String nifcif, final String discriminante,  final Long idAsegurado) {
		this.codEntidad = codentidad;
		this.codEntMed = codEntMed;
		this.codSubentMed = codSubentMed;
		this.nifcif = nifcif;
		this.discriminante = discriminante;
		this.idAsegurado = idAsegurado; 
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Asegurado.class);
		criteria.createAlias("entidad", "ent");

		if (FiltroUtils.noEstaVacio(asegurado)) {
			criteria.add(Restrictions.allEq(getMapaAsegurado()));
		}
		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("id.ciftomador"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("id.ciftomador", "%" + filtro + "%"));
		}
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			criteria.add(Restrictions.eq("ent.codentidad", codEntidad));
		}
		if (FiltroUtils.noEstaVacio(nifcif)) {
			criteria.add(Restrictions.eq("nifcif", nifcif));
		}
		if (FiltroUtils.noEstaVacio(discriminante)) {
			criteria.add(Restrictions.eq("discriminante", discriminante));
		}
		if (FiltroUtils.noEstaVacio(codEntMed) || FiltroUtils.noEstaVacio(codSubentMed)) {
			criteria.createAlias("usuario", "usu");
			criteria.createAlias("usu.subentidadMediadora", "esMed");
			if (FiltroUtils.noEstaVacio(codEntMed)) {
				criteria.add(Restrictions.eq("esMed.id.codentidad", codEntMed));
			}
			//el noestavacio lo compara con 0. esto genera errores porque existen codsubent que son 0
			//if (FiltroUtils.noEstaVacio(codSubentMed)) {
			//	criteria.add(Restrictions.eq("esMed.id.codsubentidad", codSubentMed));
			//}
			if(codSubentMed!=null){
				criteria.add(Restrictions.eq("esMed.id.codsubentidad", codSubentMed));
			}
		}
		if (FiltroUtils.noEstaVacio(idAsegurado)) {
			criteria.add(Restrictions.ne("id", idAsegurado));
		}
		
		return criteria;
	}

	private final Map<String, Object> getMapaAsegurado() {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final BigDecimal codEntidad = asegurado.getEntidad().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			mapa.put("ent.codentidad", codEntidad);
		}

		final String nifCif = asegurado.getNifcif();
		if (FiltroUtils.noEstaVacio(nifCif)) {
			mapa.put("nifcif", nifCif);
		}

		final String discriminante = asegurado.getDiscriminante();
		if (FiltroUtils.noEstaVacio(discriminante)) {
			mapa.put("discriminante", discriminante);
		}

		final String nombre = asegurado.getNombre();
		if (FiltroUtils.noEstaVacio(nombre)) {
			mapa.put("nombre", nombre);
		}

		final String apellido1 = asegurado.getApellido1();
		if (FiltroUtils.noEstaVacio(apellido1)) {
			mapa.put("apellido1", apellido1);
		}

		final String apellido2 = asegurado.getApellido2();
		if (FiltroUtils.noEstaVacio(apellido2)) {
			mapa.put("apellido2", apellido2);
		}

		final String razonSocial = asegurado.getRazonsocial();
		if (FiltroUtils.noEstaVacio(razonSocial)) {
			mapa.put("razonsocial", razonSocial);
		}

		final BigDecimal codProvincia = asegurado.getLocalidad().getId().getCodprovincia();
		if (FiltroUtils.noEstaVacio(codProvincia)) {
			mapa.put("localidad.id.codprovincia", codProvincia);
		}

		final String via = asegurado.getVia().getClave();
		if (FiltroUtils.noEstaVacio(via)) {
			mapa.put("via.clave", via);
		}

		final String direccion = asegurado.getDireccion();
		if (FiltroUtils.noEstaVacio(direccion)) {
			mapa.put("direccion", direccion);
		}

		final String piso = asegurado.getPiso();
		if (FiltroUtils.noEstaVacio(piso)) {
			mapa.put("piso", piso);
		}

		final String bloque = asegurado.getBloque();
		if (FiltroUtils.noEstaVacio(bloque)) {
			mapa.put("bloque", bloque);
		}

		final String escalera = asegurado.getEscalera();
		if (FiltroUtils.noEstaVacio(escalera)) {
			mapa.put("escalera", escalera);
		}

		final BigDecimal provincia = asegurado.getLocalidad().getId().getCodprovincia();
		if (FiltroUtils.noEstaVacio(provincia)) {
			mapa.put("localidad.id.codprovincia", provincia);
		}

		final BigDecimal localidad = asegurado.getLocalidad().getId().getCodlocalidad();
		if (FiltroUtils.noEstaVacio(localidad)) {
			mapa.put("localidad.id.codlocalidad", localidad);
		}

		final BigDecimal cp = asegurado.getCodpostal();
		if (FiltroUtils.noEstaVacio(cp)) {
			mapa.put("codpostal", cp);
		}

		final String telefono = asegurado.getTelefono();
		if (FiltroUtils.noEstaVacio(telefono)) {
			mapa.put("telefono", telefono);
		}

		final String movil = asegurado.getMovil();
		if (FiltroUtils.noEstaVacio(movil)) {
			mapa.put("movil", movil);
		}

		final String eMail = asegurado.getEmail();
		if (FiltroUtils.noEstaVacio(eMail)) {
			mapa.put("email", eMail);
		}

		final String atp = asegurado.getAtp();
		if (FiltroUtils.noEstaVacio(atp)) {
			mapa.put("atp", atp);
		}

		final Character joven = asegurado.getJovenagricultor();
		if (FiltroUtils.noEstaVacio(joven)) {
			mapa.put("jovenagricultor", joven);
		}

		return mapa;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public Long getIdAsegurado() {
		return idAsegurado;
	}

	public void setIdAsegurado(Long idAsegurado) {
		this.idAsegurado = idAsegurado;
	}
}
