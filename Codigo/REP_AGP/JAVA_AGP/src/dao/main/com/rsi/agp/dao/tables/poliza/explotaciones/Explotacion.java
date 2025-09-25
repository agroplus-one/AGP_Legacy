package com.rsi.agp.dao.tables.poliza.explotaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubvExplotacionCCAA;
import com.rsi.agp.dao.tables.poliza.SubvExplotacionENESA;

public class Explotacion implements java.io.Serializable, Comparable<Explotacion> {

	private static final long serialVersionUID = -1097552150401430845L;

	private Long id;
	private Termino termino;
	private Poliza poliza;
	private Integer latitud;
	private Integer longitud;
	private Integer numero;
	private String rega;
	private String sigla;
	private Integer subexplotacion;
	private Long especie;
	private String nomespecie;
	private Long regimen;
	private String nomregimen;
	private Set<SubvExplotacionCCAA> subvExplotacionCCAAs = new HashSet<SubvExplotacionCCAA>(0);
	private Set<SubvExplotacionENESA> subvExplotacionENESAs = new HashSet<SubvExplotacionENESA>(0);
	private Set<GrupoRaza> grupoRazas = new HashSet<GrupoRaza>(0);
	private Set<ExplotacionCobertura> explotacionCoberturas = new HashSet<ExplotacionCobertura>();

	public static final String GR_RAZA = "gruporaza";
	public static final String GR_TIPOCAPITAL = "tipocapital";
	public static final String GR_TIPOANIMAL = "tipoanimal";
	public static final String GR_NUMERO = "numero";
	public static final String GR_PRECIO = "precio";
	private HashMap<String, List<String>> grupoRazasCols;

	public Explotacion() {
		this.grupoRazas.add(new GrupoRaza(null, this, null, null, null, null, null));
		this.termino = new Termino();
		this.poliza = new Poliza();
	}

	public Explotacion(Explotacion obj) {
		this();
		this.termino = obj.getTermino();
		this.poliza = obj.getPoliza();
		this.latitud = obj.getLatitud();
		this.longitud = obj.getLongitud();
		this.numero = obj.getNumero();
		this.rega = obj.getRega();
		this.sigla = obj.getSigla();
		this.subexplotacion = obj.getSubexplotacion();
		this.especie = obj.getEspecie();
		this.regimen = obj.getRegimen();
		this.nomregimen = obj.getNomregimen();
		this.subvExplotacionCCAAs.addAll(obj.getSubvExplotacionCCAAs());
		this.subvExplotacionENESAs.addAll(obj.getSubvExplotacionENESAs());
		this.grupoRazas.clear();
		Set<GrupoRaza> grs = obj.getGrupoRazas();
		for (GrupoRaza gr : grs) {
			this.grupoRazas.add(new GrupoRaza(gr));
		}
		this.explotacionCoberturas.clear();
		Set<ExplotacionCobertura> ecs = obj.getExplotacionCoberturas();
		for (ExplotacionCobertura ec : ecs) {
			ExplotacionCobertura ecNew = new ExplotacionCobertura(ec);
			ecNew.setExplotacion(obj);
			this.explotacionCoberturas.add(ecNew);
		}
	}

	public Explotacion(Poliza p) {

		Set<ModuloPoliza> sMP = p.getModuloPolizas();

		for (ModuloPoliza mp : sMP) {
			this.grupoRazas.add(new GrupoRaza(null, this, null, null, null, null, mp.getId().getCodmodulo()));
		}

		this.termino = new Termino();
		this.poliza = p;
	}

	public Explotacion(Long id, Termino termino, Poliza poliza, Long especie, Long regimen) {
		this.id = id;
		this.termino = termino;
		this.poliza = poliza;
		this.especie = especie;
		this.regimen = regimen;
	}

	public Explotacion(Long id, Termino termino, Poliza poliza, Integer latitud, Integer longitud, Integer numero,
			String rega, String sigla, Integer subexplotacion, Long especie, Long regimen,
			Set<SubvExplotacionCCAA> subvExplotacionCCAAs, Set<SubvExplotacionENESA> subvExplotacionENESAs) {
		this.id = id;
		this.termino = termino;
		this.poliza = poliza;
		this.latitud = latitud;
		this.longitud = longitud;
		this.numero = numero;
		this.rega = rega;
		this.sigla = sigla;
		this.subexplotacion = subexplotacion;
		this.especie = especie;
		this.regimen = regimen;
		this.subvExplotacionCCAAs = subvExplotacionCCAAs;
		this.subvExplotacionENESAs = subvExplotacionENESAs;
	}

	public Explotacion(Long id, Termino termino, Poliza poliza, Integer latitud, Integer longitud, Integer numero,
			String rega, String sigla, Integer subexplotacion, Long especie, Long regimen,
			Set<SubvExplotacionCCAA> subvExplotacionCCAAs, Set<SubvExplotacionENESA> subvExplotacionENESAs,
			Set<GrupoRaza> grupoRazas, Set<ExplotacionCobertura> explotacionCoberturas) {
		this.id = id;
		this.termino = termino;
		this.poliza = poliza;
		this.latitud = latitud;
		this.longitud = longitud;
		this.numero = numero;
		this.rega = rega;
		this.sigla = sigla;
		this.subexplotacion = subexplotacion;
		this.especie = especie;
		this.regimen = regimen;
		this.subvExplotacionCCAAs = subvExplotacionCCAAs;
		this.subvExplotacionENESAs = subvExplotacionENESAs;
		this.grupoRazas = grupoRazas;
		this.explotacionCoberturas = explotacionCoberturas;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Poliza getPoliza() {
		return this.poliza;
	}

	public void setPoliza(Poliza poliza) {
		this.poliza = poliza;
	}

	public Integer getLatitud() {
		return this.latitud;
	}

	public void setLatitud(Integer latitud) {
		this.latitud = latitud;
	}

	public Integer getLongitud() {
		return this.longitud;
	}

	public void setLongitud(Integer longitud) {
		this.longitud = longitud;
	}

	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getRega() {
		return this.rega;
	}

	public void setRega(String rega) {
		this.rega = rega;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getSubexplotacion() {
		return this.subexplotacion;
	}

	public void setSubexplotacion(Integer subexplotacion) {
		this.subexplotacion = subexplotacion;
	}

	public Long getEspecie() {
		return this.especie;
	}

	public void setEspecie(Long especie) {
		this.especie = especie;
	}

	public Long getRegimen() {
		return this.regimen;
	}

	public void setRegimen(Long regimen) {
		this.regimen = regimen;
	}

	public Set<GrupoRaza> getGrupoRazas() {
		return this.grupoRazas;
	}

	public void setGrupoRazas(Set<GrupoRaza> grupoRazas) {
		this.grupoRazas = grupoRazas;
	}

	/*
	 * public GrupoRaza getGrupoRazaEdicion() { return grupoRazaEdicion; }
	 * 
	 * 
	 * public void setGrupoRazaEdicion(GrupoRaza grupoRazaEdicion) {
	 * this.grupoRazaEdicion = grupoRazaEdicion; }
	 */

	public Termino getTermino() {
		return termino;
	}

	public void setTermino(Termino termino) {
		this.termino = termino;
	}

	public String getNomespecie() {
		return nomespecie;
	}

	public void setNomespecie(String nomespecie) {
		this.nomespecie = nomespecie;
	}

	public String getNomregimen() {
		return nomregimen;
	}

	public void setNomregimen(String nomregimen) {
		this.nomregimen = nomregimen;
	}

	public HashMap<String, List<String>> getGrupoRazasCols() {
		if (grupoRazasCols == null) {
			grupoRazasCols = new HashMap<String, List<String>>();
			grupoRazasCols.put(Explotacion.GR_RAZA, new ArrayList<String>(10));
			grupoRazasCols.put(Explotacion.GR_TIPOCAPITAL, new ArrayList<String>(10));
			grupoRazasCols.put(Explotacion.GR_TIPOANIMAL, new ArrayList<String>(10));
			grupoRazasCols.put(Explotacion.GR_NUMERO, new ArrayList<String>(10));
			grupoRazasCols.put(Explotacion.GR_PRECIO, new ArrayList<String>(10));
		}
		return grupoRazasCols;
	}

	public void setGrupoRazasCols(HashMap<String, List<String>> grupoRazasCols) {
		this.grupoRazasCols = grupoRazasCols;
	}

	public Set<SubvExplotacionCCAA> getSubvExplotacionCCAAs() {
		return this.subvExplotacionCCAAs;
	}

	public void setSubvExplotacionCCAAs(Set<SubvExplotacionCCAA> subvExplotacionCCAAs) {
		this.subvExplotacionCCAAs = subvExplotacionCCAAs;
	}

	public Set<SubvExplotacionENESA> getSubvExplotacionENESAs() {
		return this.subvExplotacionENESAs;
	}

	public void setSubvExplotacionENESAs(Set<SubvExplotacionENESA> subvExplotacionENESAs) {
		this.subvExplotacionENESAs = subvExplotacionENESAs;
	}

	public Set<ExplotacionCobertura> getExplotacionCoberturas() {
		return this.explotacionCoberturas;
	}

	public void setExplotacionCoberturas(Set<ExplotacionCobertura> explotacionCoberturas) {
		this.explotacionCoberturas = explotacionCoberturas;
	}

	@Override
	public int compareTo(Explotacion o) {
		if (id < o.id) {
			return -1;
		}
		if (id > o.id) {
			return 1;
		}
		return 0;
	}
}