package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.Variedad;

public class PrecioFiltro implements Filter{

	private Precio precio;
	// Para filtrar por los nueves
	private boolean allcultivos    = false;
	private boolean allvariedades  = false;
	private boolean allprovincias  = false;
	private boolean allterminos    = false;
	private boolean allsubterminos = false;
	private boolean allcomarcas    = false;
	
	public PrecioFiltro() {
		super();
		precio         = new Precio();
		allcultivos    = false;
		allvariedades  = false;
		allprovincias  = false;
		allterminos    = false;
		allsubterminos = false;
		allcomarcas    = false;
	}
	
	public PrecioFiltro(final Modulo modulo, final Variedad variedad, final Termino termino){
		super();
		
		precio = new Precio();
		precio.setVariedad(variedad);
		precio.setModulo(modulo);
		precio.setCodprovincia(termino.getId().getCodprovincia());
		precio.setCodcomarca(termino.getId().getCodcomarca());
		precio.setCodtermino(termino.getId().getCodtermino());
		precio.setSubtermino(termino.getId().getSubtermino());
		allcultivos    = false;
		allvariedades  = false;
		allprovincias  = false;
		allterminos    = false;
		allsubterminos = false;
		allcomarcas    = false;
	}
	
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Precio.class);
		
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
		BigDecimal coddenomorigenigp;
		BigDecimal coddestino;
		BigDecimal codtipoplantacion;
		BigDecimal codpracticacultural;
		BigDecimal codsistemacultivo;
		BigDecimal codsistemaproteccion;
		BigDecimal codsistemaproduccion;
		BigDecimal codtipocapital;
		BigDecimal codciclocultivo;
		BigDecimal codmaterialcubierta;
		BigDecimal materialestructura;
		BigDecimal tipoinstalacion;
	    BigDecimal preciodesde;
	    BigDecimal preciohasta;
	    BigDecimal preciofijo;
	    BigDecimal densidaddesde;
	    BigDecimal densidadhasta;
	    BigDecimal edaddesde;
	    BigDecimal edadhasta;
	    Date frecoldesde;
	    Date frecolhasta;
	    
		if (precio != null && precio.getId() != null && precio.getModulo() != null && 
			precio.getModulo().getId() != null && precio.getVariedad() != null && 
			precio.getVariedad().getId() != null) {
					
			// Obtenemos los Datos Fijos de la parcela
			lineaseguroid = precio.getId().getLineaseguroid();
			codmodulo	  = precio.getModulo().getId().getCodmodulo();
			codcultivo	  = precio.getVariedad().getId().getCodcultivo();
			codvariedad	  = precio.getVariedad().getId().getCodvariedad();
			codprovincia  = precio.getCodprovincia();
			codtermino	  = precio.getCodtermino();
			subtermino	  = precio.getSubtermino(); 
			codcomarca	  = precio.getCodcomarca();
			
			// Filtramos por los Datos Variables asociados a la mascara
			coddenomorigenigp 		= precio.getCodigoDenominacionOrigen().getId().getCoddenomorigen();
			coddestino 				= precio.getDestino().getCoddestino();
			codtipoplantacion 		= precio.getTipoPlantacion().getCodtipoplantacion();
			codpracticacultural 	= precio.getPracticaCultural().getCodpracticacultural();
			codsistemacultivo 		= precio.getSistemaCultivo().getCodsistemacultivo();
			codsistemaproteccion 	= precio.getSistemaProteccion().getCodsistemaproteccion();
			codsistemaproduccion	= precio.getSistemaProduccion().getCodsistemaproduccion();
			codtipocapital 			= precio.getTipoCapital().getCodtipocapital();
			codciclocultivo			= precio.getCicloCultivo().getCodciclocultivo();
			codmaterialcubierta     = precio.getMaterialCubierta().getCodmaterialcubierta();
			materialestructura      = precio.getMaterialEstructura().getCodmaterialestructura();
			tipoinstalacion         = precio.getTipoInstalacion().getCodtipoinstalacion();
			preciodesde 			= precio.getPreciodesde();
		    preciohasta 			= precio.getPreciohasta();
		    preciofijo 				= precio.getPreciofijo();
		    densidaddesde 			= precio.getDensidaddesde();
		    densidadhasta 			= precio.getDensidadhasta();
		    edaddesde 				= precio.getEdaddesde();
			edadhasta 				= precio.getEdaddesde();
			frecoldesde 			= precio.getFrecoldesde();
		    frecolhasta 			= precio.getFrecolhasta();
		     
		    // Datos Fijos de la Parcela
		    // Linea de Seguro
			if (FiltroUtils.noEstaVacio(lineaseguroid)) {
				Criterion crit = Restrictions.eq("id.lineaseguroid",
						lineaseguroid);
				criteria.add(crit);
			}
			// Modulo
			if (FiltroUtils.noEstaVacio(codmodulo)) {
				Criterion crit = Restrictions.eq("modulo.id.codmodulo",
						codmodulo);
				criteria.add(crit);
			}
			// Cultivo
			if (FiltroUtils.noEstaVacio(codcultivo) || isAllcultivos()) {
				if (isAllcultivos()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codcultivo);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("variedad.id.codcultivo", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("variedad.id.codcultivo",
							codcultivo);
					criteria.add(crit);
				}
			}
			// Variedad
			//if (FiltroUtils.noEstaVacio(codvariedad) || isAllvariedades()) {
				if (isAllvariedades()) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codvariedad);
					coll.add(new BigDecimal("999"));
					Criterion crit = Restrictions.in("variedad.id.codvariedad", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("variedad.id.codvariedad",
							codvariedad);
					criteria.add(crit);
				}
			//}
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
			// Tipo Plantacion
			if (codtipoplantacion != null) {
				if(codtipoplantacion.longValue() == 0) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codtipoplantacion);
					coll.add(null);
					Criterion crit = Restrictions.in("tipoPlantacion.codtipoplantacion", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("tipoPlantacion.codtipoplantacion", codtipoplantacion);
					criteria.add(crit);
				}
			}
			// Practica Cultural
			if (codpracticacultural != null) {
				if(codpracticacultural.longValue() == 0) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codpracticacultural);
					coll.add(null);
					Criterion crit = Restrictions.in("practicaCultural.codpracticacultural", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("practicaCultural.codpracticacultural", codpracticacultural);
					criteria.add(crit);
				}
			}
			// Sistema Cultivo
			if (codsistemacultivo != null) {
				Criterion crit = Restrictions.eq("sistemaCultivo.codsistemacultivo", codsistemacultivo);
				criteria.add(crit);
			}
			// Sistema Proteccion
			if (codsistemaproteccion != null) {
				Criterion crit = Restrictions.eq("sistemaProteccion.codsistemaproteccion", codsistemaproteccion);
				criteria.add(crit);
			}
			// Sistema Produccion
			if (codsistemaproduccion != null) {
				if(codsistemaproduccion.longValue() == 0) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codsistemaproduccion);
					coll.add(null);
					Criterion crit = Restrictions.in("sistemaProduccion.codsistemaproduccion", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("sistemaProduccion.codsistemaproduccion", codsistemaproduccion);
					criteria.add(crit);
				}
			}
			// Tipo Capital
			if (codtipocapital != null) {
				if(codtipocapital.longValue() == 0) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codtipocapital);
					coll.add(null);
					Criterion crit = Restrictions.in("tipoCapital.codtipocapital", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("tipoCapital.codtipocapital", codtipocapital);
					criteria.add(crit);
				}
			}
			// Ciclo cultivo
			if (codciclocultivo != null){
				Criterion crit = null;
				if (codciclocultivo.equals(new BigDecimal(0))){
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codciclocultivo);
					coll.add(null);
					crit = Restrictions.in("cicloCultivo.codciclocultivo", coll);
				} else {
					crit = Restrictions.eq("cicloCultivo.codciclocultivo", codciclocultivo);
				}
				criteria.add(crit);
			}
			// Material cubierta
			if (codmaterialcubierta != null){
				Criterion crit = null;
				if (codmaterialcubierta.equals(new BigDecimal(0))){
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(codmaterialcubierta);
					coll.add(null);
					crit = Restrictions.in("materialCubierta.codmaterialcubierta", coll);
				} else {
					crit = Restrictions.eq("materialCubierta.codmaterialcubierta", codmaterialcubierta);
				}
				criteria.add(crit);
			}
			// Material estructura
			if (materialestructura != null){
				Criterion crit = null;
				if (materialestructura.equals(new BigDecimal(0))){
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(materialestructura);
					coll.add(null);
					crit = Restrictions.in("materialEstructura.codmaterialestructura", coll);
				} else {
					crit = Restrictions.eq("materialEstructura.codmaterialestructura", materialestructura);
				}
				criteria.add(crit);
			}
			// Tipo instalaciÃ³n
			if (tipoinstalacion != null){
				Criterion crit = null;
				if (tipoinstalacion.equals(new BigDecimal(0))){
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(tipoinstalacion);
					coll.add(null);                   
					crit = Restrictions.in("tipoInstalacion.codtipoinstalacion", coll);
				} else {
					crit = Restrictions.eq("tipoInstalacion.codtipoinstalacion", tipoinstalacion);
				}
				criteria.add(crit);
			}
			
			// Precio Desde
			if (preciodesde != null) {
				Criterion crit = Restrictions.le("preciodesde", preciodesde);
				criteria.add(crit);
			} 
			// Precio Hasta
			if (preciohasta != null) {
				Criterion crit = Restrictions.ge("preciohasta", preciohasta);
				criteria.add(crit);
			} 
			// Precio Fijo
			if (preciofijo != null) {
				Criterion crit = Restrictions.eq("preciofijo", preciofijo);
				criteria.add(crit);
			}
			// Denominacion de Origen
			if (coddenomorigenigp != null ) {
				Criterion crit = Restrictions.eq("codigoDenominacionOrigen.id.coddenomorigen", coddenomorigenigp);
				criteria.add(crit);
			}
			// Destino
			if (coddestino != null) {
				if(coddestino.longValue() == 0) {
					Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
					coll.add(coddestino);
					coll.add(null);
					Criterion crit = Restrictions.in("destino.coddestino", coll);
					criteria.add(crit);
				} else {
					Criterion crit = Restrictions.eq("destino.coddestino", coddestino);
					criteria.add(crit);
				}
			}		
			// Densidad Desde
			if (densidaddesde != null) {
				Criterion crit = Restrictions.le("densidaddesde", densidaddesde);
				criteria.add(crit);
			}
			// Densidad Hasta
			if (densidadhasta != null) {
				Criterion crit = Restrictions.ge("densidadhasta", densidadhasta);
				criteria.add(crit);
			}
			// Edad Desde
			if (edaddesde != null) {
				Criterion crit = Restrictions.le("edaddesde", edaddesde);
				criteria.add(crit);
			}
			// Edad Hasta
			if (edadhasta != null) {
				Criterion crit = Restrictions.ge("edadhasta", edadhasta);
				criteria.add(crit);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha1900;
			try {
				fecha1900 = sdf.parse("01/01/1900");
			} catch (ParseException e) {
				fecha1900 = new Date();
			}
			Date fecha0001;
			try {
				fecha0001 = sdf.parse("01/01/0001");
			} catch (ParseException e) {
				fecha0001 = new Date();
			}
			// Fecha Recoleccion Desde
			if (FiltroUtils.noEstaVacio(frecoldesde)) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.le("frecoldesde", frecoldesde))
					.add(Restrictions.isNull("frecoldesde"))
					.add(Restrictions.eq("frecoldesde",fecha1900))
					.add(Restrictions.eq("frecoldesde",fecha0001))
				);
				criteria.add(conjunction);
			}
			// Fecha Recoleccion Hasta
			if (FiltroUtils.noEstaVacio(frecolhasta)) {
				Conjunction conjunction = Restrictions.conjunction();
				conjunction.add(Restrictions.disjunction()
					.add(Restrictions.ge("frecolhasta", frecolhasta))
					.add(Restrictions.isNull("frecolhasta"))
					.add(Restrictions.eq("frecolhasta",fecha1900))
					.add(Restrictions.eq("frecolhasta",fecha0001))
				);
				criteria.add(conjunction);
			}
		}
		
		return criteria;
	}

	public Precio getPrecio() {
		return precio;
	}

	public void setPrecio(Precio precio) {
		this.precio = precio;
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
