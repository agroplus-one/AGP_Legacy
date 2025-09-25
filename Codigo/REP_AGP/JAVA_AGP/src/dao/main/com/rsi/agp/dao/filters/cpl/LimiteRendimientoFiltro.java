package com.rsi.agp.dao.filters.cpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;

public class LimiteRendimientoFiltro implements Filter
{
	private LimiteRendimiento limiteRendimiento;
	// Para filtrar por los nueves
	private boolean allcultivos = false;
	private boolean allvariedades = false;
	private boolean allprovincias = false;
	private boolean allterminos = false;
	private boolean allsubterminos = false;
	private boolean allcomarcas = false;
	
	public LimiteRendimientoFiltro() {
		super();
		limiteRendimiento = new LimiteRendimiento();
		allcultivos = false;
		allvariedades = false;
		allprovincias = false;
		allterminos = false;
		allsubterminos = false;
		allcomarcas = false;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(LimiteRendimiento.class);
		
		// Datos fijos de la parcela
		Long lineaseguroid;
		String codmodulo;
		BigDecimal codcultivo;
		BigDecimal codvariedad;
		BigDecimal codprovincia;
		BigDecimal codtermino;
		Character subtermino;
		BigDecimal codcomarca;
		
		//Datos Variables de la parcela
		BigDecimal codtipomarcoplantac;
		BigDecimal edaddesde;
		BigDecimal edadhasta;
		BigDecimal limiteinfrdto;
	    BigDecimal limitesuprdto;
	    BigDecimal densidaddesde;
	    BigDecimal densidadhasta;
	    Date frecoldesde;
	    Date frecolhasta;
	    BigDecimal numudsdesde;
	    BigDecimal numudshasta;
	    BigDecimal numaniospoda;
	    BigDecimal tablaRdto;
	    
	    BigDecimal codgarantizado;
	    BigDecimal codsistemacultivo;
	    BigDecimal codsistemaproduccion;
	    BigDecimal codsistemaconduccion;
	    BigDecimal codtipoplantacion;
	    BigDecimal codpracticacultural;
	    
		if (limiteRendimiento != null && limiteRendimiento.getId() != null
				&& limiteRendimiento.getModulo() != null
				&& limiteRendimiento.getModulo().getId() != null
				&& limiteRendimiento.getVariedad() != null
				&& limiteRendimiento.getVariedad().getId() != null) {
			
			// Obtenemos los Datos Fijos de la parcela
			lineaseguroid = limiteRendimiento.getId().getLineaseguroid();
			codmodulo = limiteRendimiento.getModulo().getId().getCodmodulo();
			codcultivo = limiteRendimiento.getVariedad().getId().getCodcultivo();
			codvariedad = limiteRendimiento.getVariedad().getId().getCodvariedad();
			codprovincia = limiteRendimiento.getCodprovincia();
			codtermino = limiteRendimiento.getCodtermino();
			subtermino = limiteRendimiento.getSubtermino();
			codcomarca = limiteRendimiento.getCodcomarca();
			
			//Filtramos por los Datos Variables asociados a la mascara
			edaddesde = limiteRendimiento.getEdaddesde();
			edadhasta = limiteRendimiento.getEdaddesde();
			codtipomarcoplantac = limiteRendimiento.getCodtipomarcoplantac();
		    densidaddesde = limiteRendimiento.getDensidaddesde();
		    densidadhasta = limiteRendimiento.getDensidadhasta();
		    frecoldesde = limiteRendimiento.getFrecoldesde();
		    frecolhasta = limiteRendimiento.getFrecolhasta();
		    codgarantizado = limiteRendimiento.getGarantizado().getCodgarantizado();
		    numudsdesde = limiteRendimiento.getNumudsdesde();
		    numudshasta = limiteRendimiento.getNumudshasta();
		    
		    codsistemacultivo = limiteRendimiento.getSistemaCultivo().getCodsistemacultivo();
		    codsistemaproduccion = limiteRendimiento.getSistemaProduccion().getCodsistemaproduccion();
		    codsistemaconduccion = limiteRendimiento.getSistemaConduccion().getCodsistemaconduccion();
		    codtipoplantacion = limiteRendimiento.getTipoPlantacion().getCodtipoplantacion();
		    codpracticacultural = limiteRendimiento.getPracticaCultural().getCodpracticacultural();
		    
		    numaniospoda = limiteRendimiento.getNumaniospoda();
		    tablaRdto = limiteRendimiento.getTablardtos();
		    
		    //Esto no sabemos para que sirve, pero lo dejamos.
			limiteinfrdto = limiteRendimiento.getLimiteinfrdto();
		    limitesuprdto = limiteRendimiento.getLimitesuprdto();
		     
		    // Datos Fijos de la Parcela
		    // Linea de Seguro
			if (FiltroUtils.noEstaVacio(lineaseguroid)) {
				Criterion crit = Restrictions.eq("id.lineaseguroid",
						lineaseguroid);
				criteria.add(crit);
			}
			// Modulo
			if (FiltroUtils.noEstaVacio(codmodulo)) {
				criteria.createAlias("modulo", "m");
				Criterion crit = Restrictions.eq("m.id.codmodulo",
						codmodulo);
				criteria.add(crit);
			}
			boolean hayAliasVariedad=false;
			// Cultivo
			if (FiltroUtils.noEstaVacio(codcultivo) || isAllcultivos()) {
				criteria.createAlias("variedad", "v");
				hayAliasVariedad=true;
				
				if (isAllcultivos()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codcultivo);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("v.id.codcultivo", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("v.id.codcultivo",
							codcultivo);
					criteria.add(crit);
				}
			}
			// Variedad
			if (FiltroUtils.noEstaVacio(codvariedad) || isAllvariedades()) {
				if(!hayAliasVariedad) {
					criteria.createAlias("variedad", "v");
					hayAliasVariedad=true;
				}
				
				if (isAllvariedades()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codvariedad);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("v.id.codvariedad", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("v.id.codvariedad",
							codvariedad);
					criteria.add(crit);
				}
			}
			// Provincia
			if (FiltroUtils.noEstaVacio(codprovincia) || isAllprovincias()) {
				if (isAllprovincias()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codprovincia);
					coll.add(new BigDecimal("99"));
					Criterion crit = Restrictions.in("codprovincia", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("codprovincia", codprovincia);
					criteria.add(crit);
				}
			}
			// Termino Municipal
			if (FiltroUtils.noEstaVacio(codtermino) || isAllterminos()) {
				if (isAllterminos()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codtermino);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("codtermino", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("codtermino", codtermino);
					criteria.add(crit);
				}
			}
			// Subtermino
			if (FiltroUtils.noEstaVacio(subtermino) || isAllsubterminos()) {
				if(isAllsubterminos()){
					Collection<Character> coll = new ArrayList<Character>();
					coll.add(subtermino);
					coll.add("9".charAt(0));
					Criterion crit = Restrictions.in("subtermino", coll);
					criteria.add(crit);					
				} else {
					Criterion crit = Restrictions.eq("subtermino", subtermino);
					criteria.add(crit);
				}
			}
			// Comarca
			if (FiltroUtils.noEstaVacio(codcomarca) || isAllcomarcas()) {
				if (isAllcomarcas()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codcomarca);
					coll.add(new BigDecimal("99"));
					Criterion crit = Restrictions.in("codcomarca", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("codcomarca", codcomarca);
					criteria.add(crit);
				}
			}
			// Datos variables de la mascara
			// Tipo Marco Plantacion
			if (codtipomarcoplantac != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.eq("codtipomarcoplantac", codtipomarcoplantac))
					.add(Restrictions.isNull("codtipomarcoplantac"))
				);
				criteria.add(conjunction);
			}
			// Edad Desde
			if (edaddesde != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.le("edaddesde", edaddesde))
					.add(Restrictions.isNull("edaddesde"))
				);
				criteria.add(conjunction);
			}
			// Edad Hasta
			if (edadhasta != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.ge("edadhasta", edadhasta))
					.add(Restrictions.isNull("edadhasta"))
				);
				criteria.add(conjunction);
			}
			// Limite Inferior de Rendimiento
			if (limiteinfrdto != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.eq("limiteinfrdto", limiteinfrdto))
					.add(Restrictions.isNull("limiteinfrdto"))
				);
				criteria.add(conjunction);
			}
			// Limite Superior de Rendimiento
			if (limitesuprdto != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.eq("limitesuprdto", limitesuprdto))
					.add(Restrictions.isNull("limitesuprdto"))
				);
				criteria.add(conjunction);
			}
			// Densidad Desde
			if (densidaddesde != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.le("densidaddesde", densidaddesde))
					.add(Restrictions.isNull("densidaddesde"))
				);
				criteria.add(conjunction);
			}
			// Densidad Hasta
			if (densidadhasta != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.ge("densidadhasta", densidadhasta))
					.add(Restrictions.isNull("densidadhasta"))
				);
				criteria.add(conjunction);
			}
			// Fecha Recoleccion Desde
			if (FiltroUtils.noEstaVacio(frecoldesde)) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.le("frecoldesde", frecoldesde))
					.add(Restrictions.isNull("frecoldesde"))
					.add(Restrictions.eq("frecoldesde","01/01/1900"))
				);
				criteria.add(conjunction);
			}
			// Fecha Recoleccion Hasta
			if (FiltroUtils.noEstaVacio(frecolhasta)) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.ge("frecolhasta", frecolhasta))
					.add(Restrictions.isNull("frecolhasta"))
					.add(Restrictions.eq("frecolhasta", "01/01/1900"))
				);
				criteria.add(conjunction);
			}
			// Garantizado
			if (FiltroUtils.noEstaVacio(codgarantizado)) {
				criteria.add(Restrictions.eq("garantizado.codgarantizado", codgarantizado));
			}
			// Numero de Unidades Desde
			if (numudsdesde != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.le("numudsdesde", numudsdesde))
					.add(Restrictions.isNull("numudsdesde"))
				);
				criteria.add(conjunction);
			}
			// Numero de Unidades Hasta
			if (numudshasta != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.ge("numudshasta", numudshasta))
					.add(Restrictions.isNull("numudshasta"))
				);
				criteria.add(conjunction);
			}
			
			//Sistema cultivo
			if (FiltroUtils.noEstaVacio(codsistemacultivo)) {
				criteria.add(Restrictions.eq("sistemaCultivo.codsistemacultivo", codsistemacultivo));
			}
			//Sistema produccion
			if (FiltroUtils.noEstaVacio(codsistemaproduccion)) {
				criteria.add(Restrictions.eq("sistemaProduccion.codsistemaproduccion", codsistemaproduccion));
			}
			//Sistema conduccion
			if (FiltroUtils.noEstaVacio(codsistemaconduccion)) {
				criteria.add(Restrictions.eq("sistemaConduccion.codsistemaconduccion", codsistemaconduccion));
			}
			//Tipo Plantacion
			if (FiltroUtils.noEstaVacio(codtipoplantacion)) {
				criteria.add(Restrictions.eq("tipoPlantacion.codtipoplantacion", codtipoplantacion));
			}
			//Practica cultural
			if (FiltroUtils.noEstaVacio(codpracticacultural)) {
				criteria.add(Restrictions.eq("practicaCultural.codpracticacultural", codpracticacultural));
			}
			
			// Numero de Unidades Desde la Poda
			if (numaniospoda != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.eq("numaniospoda", numaniospoda))
					.add(Restrictions.isNull("numaniospoda"))
				);
				criteria.add(conjunction);
			}
			
			// TablaRdto
			if (tablaRdto != null) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.eq("tablardtos", tablaRdto))
					.add(Restrictions.isNull("tablardtos"))
				);
				criteria.add(conjunction);
			}
		}	
		
		return criteria;
	}

	public LimiteRendimiento getLimiteRendimiento() {
		return limiteRendimiento;
	}

	public void setLimiteRendimiento(LimiteRendimiento limiteRendimiento) {
		this.limiteRendimiento = limiteRendimiento;
	}

	public boolean isAllcultivos() {
		return allcultivos;
	}

	public void setAllcultivos(boolean allcultivos) {
		this.allcultivos = allcultivos;
	}

	public boolean isAllvariedades() {
		return allvariedades;
	}

	public void setAllvariedades(boolean allvariedades) {
		this.allvariedades = allvariedades;
	}

	public boolean isAllprovincias() {
		return allprovincias;
	}

	public void setAllprovincias(boolean allprovincias) {
		this.allprovincias = allprovincias;
	}

	public boolean isAllterminos() {
		return allterminos;
	}

	public void setAllterminos(boolean allterminos) {
		this.allterminos = allterminos;
	}

	public boolean isAllcomarcas() {
		return allcomarcas;
	}

	public void setAllcomarcas(boolean allcomarcas) {
		this.allcomarcas = allcomarcas;
	}

	public boolean isAllsubterminos() {
		return allsubterminos;
	}

	public void setAllsubterminos(boolean allsubterminos) {
		this.allsubterminos = allsubterminos;
	}

}