package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionCCAAGanado;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;

/**
 * Clase para obtener las subvenciones enesa declarables por socios.
 * 
 * @author U028783
 * 
 */
public class SubvencionCCAAGanPolizaFiltro implements Filter {

	private Long lineaseguroid;
	private String nivelDeclaracion;
	// Sirve para indicar si el socio es persona física o jurídica ("NIF" o
	// "CIF" respectivamente)
	private String tipoidentificacion;
	private String codsmodulo;
	// Sirve para hacer el filtro por provincia, comarca, termino, subtermino,
	// cultivo, variedad y datos variables
	private Set<Explotacion> explotaciones;
	private String codSubvenciones;
	private List<BigDecimal> codsOrganismos;
	private List<Long>codigosTipoAnimal;
	/* P79408 RQ.02 Inicio */
	private Date fechaEjecucion;
	/* P79408 RQ.02 Fin */

	public SubvencionCCAAGanPolizaFiltro() {
		// nivel de declaración POLIZA
		this.nivelDeclaracion = "1";
	}

	private List<BigDecimal> listaCodTipos;

	public SubvencionCCAAGanPolizaFiltro(final Long lineaseguroid,
			final String nivelDeclaracion, final String tipoidentificacion,
			final String codsmodulo, final Set<Explotacion> explotaciones,
			final List<BigDecimal> codsOrganismos,
			/* P79408 RQ.02 Inicio */
			final List<BigDecimal> listaCodTipos,final List<Long>codigosTipoAnimal, Date fechaEjecucion) {
			/* P79408 RQ.02 Fin */
		this.lineaseguroid = lineaseguroid;
		this.nivelDeclaracion = nivelDeclaracion;
		this.tipoidentificacion = tipoidentificacion;
		this.codsmodulo = codsmodulo;
		this.explotaciones = explotaciones;
		this.codsOrganismos = codsOrganismos;
		this.listaCodTipos = listaCodTipos;
		this.codigosTipoAnimal=codigosTipoAnimal;
		/* P79408 RQ.02 Inicio */
		this.fechaEjecucion=fechaEjecucion;
		/* P79408 RQ.02 Fin */
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(SubvencionCCAAGanado.class);
		criteria.createAlias("tipoSubvencionCCAA", "tsc");

		criteria.add(Restrictions.eq("tsc.declarable", new Character('S')));

		// DAA añadimos la lista de los codtiposdeSubvencionesEnesa
		if (this.listaCodTipos != null && !this.listaCodTipos.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in(
					"tsc.codtiposubvccaa", this.listaCodTipos)));
		}

		if (this.lineaseguroid != null) {
			criteria.add(Restrictions
					.eq("id.lineaseguroid", this.lineaseguroid));
		}

		if (this.nivelDeclaracion != null) {
			criteria.add(Restrictions.eq("tsc.niveldeclaracion",
					new BigDecimal(this.nivelDeclaracion)));
		}

		if (tipoidentificacion != null) {
			if ("NIF".equals(tipoidentificacion)) {
				criteria.add(Restrictions
						.disjunction()
						.add(Restrictions.eq("tsc.niveldependencia",
								new Character('S')))
						.add(Restrictions.isNull("tsc.niveldependencia")));

			} else if ("CIF".equals(tipoidentificacion)) {
				criteria.add(Restrictions
						.disjunction()
						.add(Restrictions.eq("tsc.niveldependencia",
								new Character('J')))
						.add(Restrictions.isNull("tsc.niveldependencia")));
			}
		}

		if (this.codsmodulo != null) {
			StringTokenizer tokens = new StringTokenizer(this.codsmodulo, ";");

			Disjunction dd = Restrictions.disjunction();

			while (tokens.hasMoreTokens()) {
				dd.add(Restrictions.eq("modulo.id.codmodulo",
						new String(tokens.nextToken())));
			}
			criteria.add(dd);
		}

		if (this.explotaciones != null) {
			// INICIO DEL FILTRO POR LOS DATOS DE LA EXPLOTACION
			// Añado el filtro por los datos de las explotaciones: será una OR
			// por
			// explotación
			Disjunction dj = Restrictions.disjunction();
			for (Explotacion explotacion : this.explotaciones) {
				Conjunction c = Restrictions.conjunction();
				c.add(Restrictions
						.disjunction()
						.add(Restrictions.eq("termino.id.codprovincia",
								explotacion.getTermino().getId()
										.getCodprovincia()))
						.add(Restrictions.eq("termino.id.codprovincia",
								new BigDecimal("99"))))
						.add(Restrictions
								.disjunction()
								.add(Restrictions.eq("termino.id.codcomarca",
										explotacion.getTermino().getId()
												.getCodcomarca()))
								.add(Restrictions.eq("termino.id.codcomarca",
										new BigDecimal("99"))))
						.add(Restrictions
								.disjunction()
								.add(Restrictions.eq("termino.id.codtermino",
										explotacion.getTermino().getId()
												.getCodtermino()))
								.add(Restrictions.eq("termino.id.codtermino",
										new BigDecimal("999"))))
						.add(Restrictions
								.disjunction()
								.add(Restrictions.eq("termino.id.subtermino",
										explotacion.getTermino().getId()
												.getSubtermino()))
								.add(Restrictions.eq("termino.id.subtermino",
										new Character('9'))))
						.add(Restrictions
								.disjunction()
								.add(Restrictions.eq("especie.id.codespecie",
										explotacion.getEspecie()))
								.add(Restrictions.eq("especie.id.codespecie",
										new Long("999"))))
						.add(Restrictions
								.disjunction()
								.add(Restrictions.eq(
										"regimenManejo.id.codRegimen",
										explotacion.getRegimen()))
								.add(Restrictions.eq(
										"regimenManejo.id.codRegimen",
										new Long("999"))));
				Disjunction d = Restrictions.disjunction();
				for (GrupoRaza grupoRaza : explotacion.getGrupoRazas()) {
					d.add(Restrictions
							.conjunction()
							.add(Restrictions
									.disjunction()
									.add(Restrictions.eq(
											"gruposRazas.id.CodGrupoRaza",
											grupoRaza.getCodgruporaza()))
									.add(Restrictions.eq(
											"gruposRazas.id.CodGrupoRaza",
											new Long("999"))))
							.add(Restrictions
									.disjunction()
									.add(Restrictions.eq(
											"tipoCapital.codtipocapital",
											grupoRaza.getCodtipocapital().longValue()))
									.add(Restrictions.eq(
											"tipoCapital.codtipocapital",
											new Long("999")))));
				}
				c.add(d);
				dj.add(c);
			}
			criteria.add(dj);
			// FIN DEL FILTRO POR LOS DATOS DE LA PARCELA
		}

		if (this.getCodSubvenciones() != null) {
			StringTokenizer tokens = new StringTokenizer(this.codSubvenciones,
					",");
			Disjunction dd = Restrictions.disjunction();
			while (tokens.hasMoreTokens()) {
				dd.add(Restrictions.eq("tipoSubvencionCCAA.codtiposubvccaa",
						new BigDecimal(tokens.nextToken())));
			}
			criteria.add(dd);
		}
		if (this.getCodsOrganismos() != null
				&& this.getCodsOrganismos().size() > 0) {
			criteria.createAlias("organismo", "organismo");
			criteria.add(Restrictions.in("organismo.codorganismo",
					this.getCodsOrganismos()));
		}
		
		if(codigosTipoAnimal!=null && codigosTipoAnimal.size()>0){
			Disjunction dd = Restrictions.disjunction();
			dd.add(Restrictions.in("tipoAnimal.id.codTipoAnimal", codigosTipoAnimal));
			dd.add(Restrictions.eq("tipoAnimal.id.codTipoAnimal", new Long(999)));		
			criteria.add(dd);
			
		}
		
		/* P79408 RQ.02 Inicio */
		if (this.getFechaEjecucion()!=null) {			
			Disjunction dd = Restrictions.disjunction();
			dd.add(Restrictions.isNull("fechaDesde")); 
			dd.add(Restrictions.isNull("fechaHasta"));		
			Conjunction objConjunction = Restrictions.conjunction();
			objConjunction.add(Restrictions.le("fechaDesde", this.getFechaEjecucion()));			
			objConjunction.add(Restrictions.ge("fechaHasta", this.getFechaEjecucion()));
			dd.add(objConjunction);
			criteria.add(dd);
		}
		/* P79408 RQ.02 Fin */
		
		criteria.addOrder(Order.desc("porcSubvSeguroInd"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	public String getNivelDeclaracion() {
		return this.nivelDeclaracion;
	}

	public void setNivelDeclaracion(final String nivelDeclaracion) {
		this.nivelDeclaracion = nivelDeclaracion;
	}

	public String getTipoidentificacion() {
		return this.tipoidentificacion;
	}

	public void setTipoidentificacion(final String tipoidentificacion) {
		this.tipoidentificacion = tipoidentificacion;
	}

	public Long getLineaseguroid() {
		return this.lineaseguroid;
	}

	public void setLineaseguroid(final Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public String getCodsmodulo() {
		return this.codsmodulo;
	}

	public void setCodsmodulo(final String codsmodulo) {
		this.codsmodulo = codsmodulo;
	}

	public String getCodSubvenciones() {
		return this.codSubvenciones;
	}

	public void setCodSubvenciones(final String codSubvenciones) {
		this.codSubvenciones = codSubvenciones;
	}

	public Set<Explotacion> getExplotaciones() {
		return this.explotaciones;
	}

	public void setExplotaciones(final Set<Explotacion> explotaciones) {
		this.explotaciones = explotaciones;
	}

	public List<BigDecimal> getCodsOrganismos() {
		return this.codsOrganismos;
	}

	public void setCodsOrganismos(final List<BigDecimal> codsOrganismos) {
		this.codsOrganismos = codsOrganismos;
	}
	
	/* P79408 RQ.02 Inicio */
	public Date getFechaEjecucion() {
		return fechaEjecucion;
	}

	public void setFechaEjecucion(Date fechaEjecucion) {
		this.fechaEjecucion = fechaEjecucion;
	}
	/* P79408 RQ.02 Fin */

}