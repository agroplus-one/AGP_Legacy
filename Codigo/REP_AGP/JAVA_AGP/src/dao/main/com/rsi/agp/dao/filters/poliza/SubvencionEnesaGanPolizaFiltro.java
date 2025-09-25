package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;

/**
 * Clase para obtener las subvenciones enesa declarables para socios/asegurados.
 * 
 * @author U028783
 * 
 */
public class SubvencionEnesaGanPolizaFiltro implements Filter {

	private Long lineaseguroid;
	private String nivelDeclaracion;
	// Sirve para indicar si el socio es persona física o jurídica ("NIF" o
	// "CIF" respectivamente)
	private String tipoidentificacion;
	// códigos de módulos seleccionados por el usuario (incluído el 99999)
	private String codsmodulo;
	// Sirve para diferenciar cuando busquemos subvenciones de asegurado (true)
	// o de socio (false)
	private boolean asegurado;
	// Lista de parcelas de la póliza para las que queremos las subvenciones.

	// Atributo para hacer el filtro de subvenciones a insertar en la base de
	// datos.
	private String codSubvenciones;

	private List<Long>codigosTipoAnimal;
	/* P79408 RQ.02 Inicio */
	private Date fechaEjecucion;
	/* P79408 RQ.02 Fin */
	
	public SubvencionEnesaGanPolizaFiltro() {
		// nivel de declaración POLIZA
		this.nivelDeclaracion = "1";
	}

	public SubvencionEnesaGanPolizaFiltro(final Long lineaseguroid,
			final String nivelDeclaracion, final String tipoidentificacion,
			final String codsmodulo, final boolean asegurado, 
			final List<Long>codigosTipoAnimal, Date fechaEjecucion) {
		this.lineaseguroid = lineaseguroid;
		this.nivelDeclaracion = nivelDeclaracion;
		this.tipoidentificacion = tipoidentificacion;
		this.codsmodulo = codsmodulo;
		this.asegurado = asegurado;
		this.codigosTipoAnimal = codigosTipoAnimal;
		this.fechaEjecucion = fechaEjecucion;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(SubvencionEnesaGanado.class);
		criteria.createAlias("tipoSubvencionEnesa", "tse");

		criteria.add(Restrictions.eq("tse.declarable", new Character('S')));

		if (this.lineaseguroid != null) {
			criteria.add(Restrictions.eq("id.lineaseguroid", this.lineaseguroid));
		}

		if (this.nivelDeclaracion != null) {
			criteria.add(Restrictions.eq("tse.niveldeclaracion", new BigDecimal(this.nivelDeclaracion)));
		}

		if (tipoidentificacion != null) {
			if ("NIF".equals(tipoidentificacion)) {
				criteria.add(Restrictions
						.disjunction()
						.add(Restrictions.eq("tse.niveldependencia",
								new Character('S')))
						.add(Restrictions.isNull("tse.niveldependencia")));

			} else if ("CIF".equals(tipoidentificacion) && !asegurado) {
				criteria.add(Restrictions.eq("tse.niveldependencia",
						new Character('J')));
			} else if ("CIF".equals(tipoidentificacion) && asegurado) {
				criteria.add(Restrictions
						.disjunction()
						.add(Restrictions.eq("tse.niveldependencia",
								new Character('J')))
						.add(Restrictions.isNull("tse.niveldependencia")));
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

		if (this.getCodSubvenciones() != null) {
			StringTokenizer tokens = new StringTokenizer(this.codSubvenciones,
					",");

			Disjunction dd = Restrictions.disjunction();

			while (tokens.hasMoreTokens()) {
				dd.add(Restrictions.eq("tipoSubvencionEnesa.codtiposubvenesa",
						new BigDecimal(tokens.nextToken())));
			}
			criteria.add(dd);
		}
		
		if(codigosTipoAnimal!=null && codigosTipoAnimal.size()>0){
			Disjunction dd = Restrictions.disjunction();
			dd.add(Restrictions.in("tipoAnimal.id.codTipoAnimal", codigosTipoAnimal));
			dd.add(Restrictions.eq("tipoAnimal.id.codTipoAnimal", new Long(999)));		
			criteria.add(dd);
			
		}
		
		/* P79408 RQ.02 Inicio */
		if (this.getFechaEjecucion()!=null){	
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
		return nivelDeclaracion;
	}

	public void setNivelDeclaracion(final String nivelDeclaracion) {
		this.nivelDeclaracion = nivelDeclaracion;
	}

	public String getTipoidentificacion() {
		return tipoidentificacion;
	}

	public void setTipoidentificacion(final String tipoidentificacion) {
		this.tipoidentificacion = tipoidentificacion;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(final Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public String getCodsmodulo() {
		return codsmodulo;
	}

	public void setCodsmodulo(final String codsmodulo) {
		this.codsmodulo = codsmodulo;
	}

	public String getCodSubvenciones() {
		return codSubvenciones;
	}

	public void setCodSubvenciones(final String codSubvenciones) {
		this.codSubvenciones = codSubvenciones;
	}

	public boolean isAsegurado() {
		return asegurado;
	}

	public void setAsegurado(final boolean asegurado) {
		this.asegurado = asegurado;
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