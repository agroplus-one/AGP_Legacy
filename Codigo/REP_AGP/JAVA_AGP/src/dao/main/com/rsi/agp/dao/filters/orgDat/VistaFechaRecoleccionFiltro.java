package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.FechaRecoleccion;

public class VistaFechaRecoleccionFiltro implements Filter 
{
	private String     codmodulo;
	private BigDecimal codcultivo;
	private BigDecimal codvariedad;
	private Long       lineaSeguroId;
	private BigDecimal codprovincia;
	private BigDecimal codcomarca;
	private BigDecimal codtermino;
	private Character  subtermino;
	private BigDecimal codtipocapital;
	private List<BigDecimal> listProvincias;
    private List<BigDecimal> listComarcas;
    private List<BigDecimal> listTerminos;
    private List<Character> listSubterminos;
    private List<BigDecimal> listCultivos;
    private List<BigDecimal> listVariedades;
	
	
	public Long getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(Long lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	public BigDecimal getCodvariedad() {
		return codvariedad;
	}

	public void setCodvariedad(BigDecimal codvariedad) {
		this.codvariedad = codvariedad;
	}

	public VistaFechaRecoleccionFiltro() 
	{
		
	}

	public VistaFechaRecoleccionFiltro(String codmodulo, BigDecimal codcultivo, BigDecimal codvariedad, Long lineaSeguroId, BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino, Character  subtermino, BigDecimal codtipocapital, BigDecimal codpracticacultural) 
	{
		this.codmodulo = codmodulo;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.lineaSeguroId = lineaSeguroId;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.codtipocapital = codtipocapital;
	}
	
	public void setListas(List<BigDecimal> listProvincias, List<BigDecimal> listComarcas, List<BigDecimal> listTerminos, List<BigDecimal> listCultivos, List<BigDecimal> listVariedades, List<Character> listSubterminos){
		this.listProvincias = listProvincias;
		this.listComarcas = listComarcas;
		this.listTerminos = listTerminos;
		this.listCultivos = listCultivos;
		this.listVariedades = listVariedades;
		this.listSubterminos = listSubterminos;
	}

	@Override
	public Criteria getCriteria(Session sesion) 
	{
		Criteria criteria = sesion.createCriteria(FechaRecoleccion.class);
		criteria.add(getFiltro());

		return criteria;
	}

	private final Conjunction getFiltro() 
	{
		Conjunction c = Restrictions.conjunction();
        
		/* PROPIEDADES DE MODULO */
		
		if (this.lineaSeguroId != null){
			c.add(Restrictions.eq("id.lineaseguroid", this.lineaSeguroId));
		}

		if (this.codmodulo != null){
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens()){ 
		    	dd.add(Restrictions.eq("modulo.id.codmodulo",new String(tokens.nextToken())));
		    }
		    c.add(dd);
		}

		
		if(listCultivos != null && !listCultivos.contains(new BigDecimal("999"))){
			if(listCultivos.size() > 0){
			    listCultivos.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("variedad.id.codcultivo", listCultivos));
		}
		
		if(FiltroUtils.noEstaVacio(this.codcultivo)){
			c.add(				
				Restrictions.disjunction()
				.add(Restrictions.eq("variedad.id.codcultivo", this.codcultivo))
				.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal("999")))
			);
		}
		
		if(listVariedades != null && !listVariedades.contains(new BigDecimal("999"))){
			if(listVariedades.size() > 0){
				listVariedades.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("variedad.id.codvariedad", listVariedades));
		}
			
		//COMENTADO POR ASF: LA VARIEDAD SI PUEDE SER '0' Y EL METOD DE FILTROUTILS NO LO ACEPTA!!!
		//if(FiltroUtils.noEstaVacio(this.codvariedad)){
		if(!StringUtils.nullToString(this.codvariedad).equals("")){
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("variedad.id.codvariedad", this.codvariedad))
				.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal("999")))
			);
		}
		
		if(listProvincias != null && !listProvincias.contains(new BigDecimal("99"))){
			if(listProvincias.size() > 0){
				listProvincias.add(new BigDecimal("99"));
			}
			c.add(Restrictions.in("codprovincia", listProvincias));	
		}
		if(FiltroUtils.noEstaVacio(this.codprovincia)){
			c.add (				
				Restrictions.disjunction()
				.add(Restrictions.eq("codprovincia", this.codprovincia))
				.add(Restrictions.eq("codprovincia", new BigDecimal("99")))
			);
		}
		
		if(listComarcas != null && !listComarcas.contains(new BigDecimal("99"))){
			 if(listComarcas.size() > 0){
				listComarcas.add(new BigDecimal("99"));
			 }
			 c.add(Restrictions.in("codcomarca", listComarcas));
		}

		if(FiltroUtils.noEstaVacio(this.codcomarca)){
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("codcomarca", this.codcomarca))
				.add(Restrictions.eq("codcomarca", new BigDecimal("99")))
			);
		}
		
		if(listTerminos != null && !listTerminos.contains(new BigDecimal("999"))){
			if(listTerminos.size() > 0){
				listTerminos.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("codtermino", listTerminos));
		}
		if(FiltroUtils.noEstaVacio(this.codtermino)){
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("codtermino", this.codtermino))
				.add(Restrictions.eq("codtermino", new BigDecimal("999")))
			);
		}
		
		if(listSubterminos != null && !listSubterminos.contains(new Character('9'))){
			if(listSubterminos.size() > 0){
				listSubterminos.add(new Character('9'));
			}
			c.add(Restrictions.in("subtermino", listSubterminos));
		}
		if(FiltroUtils.noEstaVacio(this.subtermino)){
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("subtermino", this.subtermino))
				.add(Restrictions.eq("subtermino", new Character('9')))
			);
		}
		
		
		return c;
	}

	public String getCodmodulo() {
		return codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}

	public BigDecimal getCodcultivo() {
		return codcultivo;
	}

	public void setCodcultivo(BigDecimal codcultivo) {
		this.codcultivo = codcultivo;
	}

	public BigDecimal getCodprovincia() {
		return codprovincia;
	}

	public void setCodprovincia(BigDecimal codprovincia) {
		this.codprovincia = codprovincia;
	}

	public BigDecimal getCodcomarca() {
		return codcomarca;
	}

	public void setCodcomarca(BigDecimal codcomarca) {
		this.codcomarca = codcomarca;
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

	public BigDecimal getCodtipocapital() {
		return codtipocapital;
	}

	public void setCodtipocapital(BigDecimal codtipocapital) {
		this.codtipocapital = codtipocapital;
	}

}
