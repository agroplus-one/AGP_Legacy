package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;

public class MascaraLimiteRendimientoFiltro implements Filter {

	private Long lineaseguroid;
	private List<String> modulos;
	private BigDecimal codcultivo;
	private BigDecimal codvariedad;
	private BigDecimal codprovincia;
	private BigDecimal codtermino;
	private Character subtermino;
	private BigDecimal codcomarca;
	// Para filtrar por los nueves
	private boolean allcultivos = false;
	private boolean allvariedades = false;
	private boolean allprovincias = false;
	private boolean allterminos = false;
	private boolean allsubterminos = false;
	private boolean allcomarcas = false;

	public MascaraLimiteRendimientoFiltro() {
		super();
	}

	public MascaraLimiteRendimientoFiltro(Long lineaseguroid, BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino, Character subtermino,
			List<String> modulos) {
		super();
		this.lineaseguroid = lineaseguroid;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.codprovincia = codprovincia;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.codcomarca = codcomarca;
		this.modulos = modulos;
		this.allcultivos = false;
		this.allvariedades = false;
		this.allprovincias = false;
		this.allterminos = false;
		this.allsubterminos = false;
		this.allcomarcas = false;
	}
	
	public MascaraLimiteRendimientoFiltro(Long lineaseguroid, BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino, Character subtermino,
			String modulo) {
		super();
		this.lineaseguroid = lineaseguroid;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.codprovincia = codprovincia;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.codcomarca = codcomarca;
		this.modulos = Arrays.asList(new String[] { modulo });
		this.allcultivos = false;
		this.allvariedades = false;
		this.allprovincias = false;
		this.allterminos = false;
		this.allsubterminos = false;
		this.allcomarcas = false;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		
		Criteria criteria = sesion.createCriteria(MascaraLimiteRendimiento.class);

		// Lineaseguroid
		if (FiltroUtils.noEstaVacio(this.lineaseguroid)) {
			Criterion crit = Restrictions.eq("id.lineaseguroid", this.lineaseguroid);
			criteria.add(crit);
		}
		// Modulo
		if (modulos != null && modulos.size() > 0) {
			criteria.setProjection(Projections.distinct(Projections.property("id.codconcepto")));
			Criterion crit = Restrictions.in("id.codmodulo", modulos);
			criteria.add(crit);
		}
		// Cultivo
		if (FiltroUtils.noEstaVacio(this.codcultivo)) {
			if (isAllcultivos()) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(this.codcultivo);
				coll.add(new BigDecimal("999"));
				Criterion crit = Restrictions.in("id.codcultivo", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codcultivo", this.codcultivo);
				criteria.add(crit);
			}
		}
		// Variedad
		// COMENTADO POR ASF: LA VARIEDAD SI PUEDE SER '0' Y EL METOD DE FILTROUTILS NO
		// LO ACEPTA!!!
		// if(FiltroUtils.noEstaVacio(this.codvariedad)){
		if (!StringUtils.nullToString(this.codvariedad).equals("")) {
			if (isAllvariedades()) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(this.codvariedad);
				coll.add(new BigDecimal("999"));
				Criterion crit = Restrictions.in("id.codvariedad", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codvariedad", this.codvariedad);
				criteria.add(crit);
			}
		}
		// Provincia
		if (FiltroUtils.noEstaVacio(this.codprovincia)) {
			if (isAllprovincias()) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(this.codprovincia);
				coll.add(new BigDecimal("99"));
				Criterion crit = Restrictions.in("id.codprovincia", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codprovincia", this.codprovincia);
				criteria.add(crit);
			}
		}
		// Termino Municipal
		if (FiltroUtils.noEstaVacio(this.codtermino)) {
			if (isAllterminos()) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(this.codtermino);
				coll.add(new BigDecimal("999"));
				Criterion crit = Restrictions.in("id.codtermino", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codtermino", this.codtermino);
				criteria.add(crit);
			}
		}
		// Subtermino
		if (FiltroUtils.noEstaVacio(this.subtermino) || isAllsubterminos()) {
			if (isAllsubterminos()) {
				Collection<Character> coll = new ArrayList<Character>();
				coll.add(this.subtermino);
				coll.add("9".charAt(0));
				Criterion crit = Restrictions.in("id.subtermino", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.subtermino", this.subtermino);
				criteria.add(crit);
			}
		}
		// Comarca
		if (FiltroUtils.noEstaVacio(this.codcomarca)) {
			if (isAllcomarcas()) {
				Collection<BigDecimal> coll = new ArrayList<BigDecimal>();
				coll.add(this.codcomarca);
				coll.add(new BigDecimal("99"));
				Criterion crit = Restrictions.in("id.codcomarca", coll);
				criteria.add(crit);
			} else {
				Criterion crit = Restrictions.eq("id.codcomarca", this.codcomarca);
				criteria.add(crit);
			}
		}

		return criteria;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public List<String> getModulos() {
		return modulos;
	}

	public void setModulos(List<String> modulos) {
		this.modulos = modulos;
	}

	public BigDecimal getCodcultivo() {
		return codcultivo;
	}

	public void setCodcultivo(BigDecimal codcultivo) {
		this.codcultivo = codcultivo;
	}

	public BigDecimal getCodvariedad() {
		return codvariedad;
	}

	public void setCodvariedad(BigDecimal codvariedad) {
		this.codvariedad = codvariedad;
	}

	public BigDecimal getCodprovincia() {
		return codprovincia;
	}

	public void setCodprovincia(BigDecimal codprovincia) {
		this.codprovincia = codprovincia;
	}

	public BigDecimal getCodtermino() {
		return codtermino;
	}

	public void setCodtermino(BigDecimal codtermino) {
		this.codtermino = codtermino;
	}

	public Character getSubtermino() {
		return subtermino;
	}

	public void setSubtermino(Character subtermino) {
		this.subtermino = subtermino;
	}

	public BigDecimal getCodcomarca() {
		return codcomarca;
	}

	public void setCodcomarca(BigDecimal codcomarca) {
		this.codcomarca = codcomarca;
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