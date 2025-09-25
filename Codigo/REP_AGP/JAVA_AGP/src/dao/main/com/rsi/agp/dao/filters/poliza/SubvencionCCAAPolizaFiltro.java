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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;

/**
 * Clase para obtener las subvenciones enesa declarables por socios.
 * @author U028783
 *
 */
public class SubvencionCCAAPolizaFiltro implements Filter {

	private Long lineaseguroid;
	private String nivelDeclaracion;
	//Sirve para indicar si el socio es persona física o jurídica ("NIF" o "CIF" respectivamente)
	private String tipoidentificacion;
	private String codsmodulo;
	// Sirve para hacer el filtro por provincia, comarca, termino, subtermino, cultivo, variedad y datos variables
	private Set<Parcela> parcelas;
	private String codSubvenciones;
	private List codsOrganismos; 
	
	public SubvencionCCAAPolizaFiltro(){
		//nivel de declaración POLIZA
		this.nivelDeclaracion = "1";
	}
	private List listaCodTipos;
	/* P79408 RQ.02 Inicio */
	private Date fechaEjecucion;
	/* P79408 RQ.02 Fin */
	
	public SubvencionCCAAPolizaFiltro(Long lineaseguroid,
			String nivelDeclaracion, String tipoidentificacion, String codsmodulo, Set<Parcela> parcelas,List codsOrganismos, List<BigDecimal> listaCodTipos, 
			/* P79408 RQ.02 Inicio */
			Date fechaEjecucion) {
			/* P79408 RQ.02 Fin */
		this.lineaseguroid = lineaseguroid;
		this.nivelDeclaracion = nivelDeclaracion;
		this.tipoidentificacion = tipoidentificacion;
		this.codsmodulo = codsmodulo;
		this.parcelas = parcelas;
		this.codsOrganismos=codsOrganismos;
		this.listaCodTipos = listaCodTipos;
		/* P79408 RQ.02 Inicio */
		this.fechaEjecucion = fechaEjecucion;
		/* P79408 RQ.02 Fin */
	
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(SubvencionCCAA.class);
		criteria.createAlias("tipoSubvencionCCAA", "tse");
		
		criteria.add(Restrictions.eq("tse.declarable", new Character('S')));
		
		//DAA añadimos la lista de los codtiposdeSubvencionesEnesa
		if (this.listaCodTipos != null && !this.listaCodTipos.isEmpty()){
			criteria.add(Restrictions.not(Restrictions.in("tse.codtiposubvccaa", this.listaCodTipos)));
		}
		
		if (this.lineaseguroid != null){
			criteria.add(Restrictions.eq("id.lineaseguroid", this.lineaseguroid));
		}

		if (this.nivelDeclaracion != null){
			criteria.add(Restrictions.eq("tse.niveldeclaracion", new BigDecimal(this.nivelDeclaracion)));
		}
		
		if (tipoidentificacion != null) {
			if("NIF".equals(tipoidentificacion)) {
				criteria.add(
						Restrictions.disjunction().add(
								Restrictions.eq("tse.niveldependencia", new Character('S')))
								.add(Restrictions.isNull("tse.niveldependencia"))
				);
					
			} else if("CIF".equals(tipoidentificacion)) {
				criteria.add(Restrictions.disjunction().add(
						Restrictions.eq("tse.niveldependencia", new Character('J')))
						.add(Restrictions.isNull("tse.niveldependencia"))
				);
			}
		}
		
		if (this.codsmodulo != null){
			StringTokenizer tokens = new StringTokenizer(this.codsmodulo,";");
			
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens())
		    { 
		    	dd.add(Restrictions.eq("modulo.id.codmodulo",new String(tokens.nextToken())));
		    }
		    criteria.add(dd);
		}
		
		if (this.parcelas != null){
			//INICIO DEL FILTRO POR LOS DATOS DE LA PARCELA
			//Añado el filtro por los datos de las parcelas: será una OR por parcela
			Disjunction dj = Restrictions.disjunction();
			for (Parcela parcela: this.parcelas){
				Conjunction c = Restrictions.conjunction();
				c.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("codprovincia", parcela.getTermino().getId().getCodprovincia()))
							.add(Restrictions.eq("codprovincia", new BigDecimal("99")))
				)
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("codcomarca", parcela.getTermino().getId().getCodcomarca()))
							.add(Restrictions.eq("codcomarca", new BigDecimal("99")))
				)
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("codtermino", parcela.getTermino().getId().getCodtermino()))
							.add(Restrictions.eq("codtermino", new BigDecimal("999")))
				)
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("subtermino", parcela.getTermino().getId().getSubtermino()))	
							.add(Restrictions.eq("subtermino", new Character('9')))			
				)				
			    .add(
						Restrictions.disjunction() //OR
							.add(Restrictions.eq("variedad.id.codcultivo", parcela.getCodcultivo()))
							.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal("999")))
				)//AND
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("variedad.id.codvariedad", parcela.getCodvariedad()))
							.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal("999")))
				);
				
				//Añadimos los criterios de Sistema de Produccion e IGP
				//Recorremos los datos variables para seguir añadiendo criterios
				Disjunction djdv = Restrictions.disjunction();
				for (CapitalAsegurado capAseg: parcela.getCapitalAsegurados()){
					for (DatoVariableParcela dv: capAseg.getDatoVariableParcelas()){
						if (dv.getDiccionarioDatos().getCodconcepto().equals(new BigDecimal("616")) ||
								dv.getDiccionarioDatos().getCodconcepto().equals(new BigDecimal("765"))){
							Conjunction cdv = Restrictions.conjunction();
							cdv
								.add(Restrictions.eq("diccionarioDatos.codconcepto", dv.getDiccionarioDatos().getCodconcepto()))
								.add(Restrictions.eq("valorconcepto", new BigDecimal(dv.getValor())));
							
							djdv.add(cdv);
						}
					}
				}
				Criterion cdvnull = Restrictions.conjunction()
					.add(Restrictions.isNull("diccionarioDatos.codconcepto"))
					.add(Restrictions.isNull("valorconcepto"));
				djdv.add(cdvnull);
				
				//añadimos la and de ors de datos variables de la parcela
				c.add(djdv);
				
				dj.add(c);
			}
			criteria.add(dj);
			//FIN DEL FILTRO POR LOS DATOS DE LA PARCELA
		}
		
		if (this.getCodSubvenciones() != null){
			StringTokenizer tokens = new StringTokenizer(this.codSubvenciones, ",");
			
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens())
		    { 
		    	dd.add(Restrictions.eq("tipoSubvencionCCAA.codtiposubvccaa",new BigDecimal(tokens.nextToken())));
		    }
		    criteria.add(dd);
		}
		if (this.getCodsOrganismos() != null && this.getCodsOrganismos().size() > 0){
			criteria.createAlias("organismo", "organismo");
			criteria.add(Restrictions.in("organismo.codorganismo", this.getCodsOrganismos()));
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
		
		criteria.addOrder(Order.desc("pctsubvindividual"));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return criteria;
	}

	public String getNivelDeclaracion() {
		return nivelDeclaracion;
	}

	public void setNivelDeclaracion(String nivelDeclaracion) {
		this.nivelDeclaracion = nivelDeclaracion;
	}

	public String getTipoidentificacion() {
		return tipoidentificacion;
	}

	public void setTipoidentificacion(String tipoidentificacion) {
		this.tipoidentificacion = tipoidentificacion;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public String getCodsmodulo() {
		return codsmodulo;
	}

	public void setCodsmodulo(String codsmodulo) {
		this.codsmodulo = codsmodulo;
	}

	public String getCodSubvenciones() {
		return codSubvenciones;
	}

	public void setCodSubvenciones(String codSubvenciones) {
		this.codSubvenciones = codSubvenciones;
	}

	public Set<Parcela> getParcelas() {
		return parcelas;
	}

	public void setParcelas(Set<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	public List getCodsOrganismos() {
		return codsOrganismos;
	}

	public void setCodsOrganismos(List codsOrganismos) {
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
