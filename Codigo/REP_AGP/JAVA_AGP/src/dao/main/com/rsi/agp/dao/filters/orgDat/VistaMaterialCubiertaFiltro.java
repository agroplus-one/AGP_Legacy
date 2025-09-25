package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.orgDat.VistaMaterialCubierta;


public class VistaMaterialCubiertaFiltro implements Filter{
	
	private Long       lineaSeguroId;
	private String     codmodulo;
    private BigDecimal codcultivo;
    private BigDecimal codvariedad;
    private BigDecimal codprovincia;
    private BigDecimal codcomarca;
    private BigDecimal codtermino;
    private Character  subtermino;
    private BigDecimal codtipocapital;
    private BigDecimal codmaterialcubierta;
    private BigDecimal codsistemaproteccion;
    private List<BigDecimal> listCultivos;
    private List<BigDecimal> listVariedades;
    
    
    
    public VistaMaterialCubiertaFiltro() {}
    
    
    public VistaMaterialCubiertaFiltro(Long lineaSeguroId, String codmodulo, BigDecimal codcultivo,
    		BigDecimal codvariedad, BigDecimal codprovincia, BigDecimal codcomarca,
    		BigDecimal codtermino, Character subtermino, BigDecimal codtipocapital,
    		BigDecimal codmaterialcubierta){
    	
    	this.lineaSeguroId        = lineaSeguroId;
    	this.codmodulo            = codmodulo;
    	this.codcultivo           = codcultivo;
    	this.codvariedad          = codvariedad;
    	this.codprovincia         = codprovincia;
    	this.codcomarca           = codcomarca;
    	this.codtermino           = codtermino;
    	this.subtermino           = subtermino;
    	this.codtipocapital       = codtipocapital;
    	this.codmaterialcubierta  = codmaterialcubierta;
    	
    	
    }
    
    public void setListas(List<BigDecimal> listCultivos, List<BigDecimal> listVariedades){
		this.listCultivos = listCultivos;
		this.listVariedades = listVariedades;
	}
    
    @Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaMaterialCubierta.class);
		criteria.add(getMaterialCubierta());
		
		criteria.addOrder(Order.asc("id.codmaterialcubierta"));
		
		return criteria;		
	}
	
	private final Conjunction getMaterialCubierta() 
	{
	
		Conjunction c = Restrictions.conjunction();
		
		// linea seguro id
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) 
		{
			c.add(Restrictions.eq("id.lineaseguroid", this.lineaSeguroId));
		}
		
		if(FiltroUtils.noEstaVacio(this.codsistemaproteccion)){
			c.add(Restrictions.eq("id.codsistemaproteccion", this.codsistemaproteccion));
		}
		
		
		// código módulo
		if (FiltroUtils.noEstaVacio(this.codmodulo)) 
		{
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
		
			Disjunction dd = Restrictions.disjunction();
			
			while(tokens.hasMoreTokens())
			{ 
				dd.add(
				Restrictions.disjunction()
						.add(Restrictions.eq("id.codmodulo",new String(tokens.nextToken())))
						.add(Restrictions.eq("id.codmodulo", new String("99999")))
				);
				c.add(dd);
			}
		}
		
		
		if (this.codtipocapital != null)
		{
			c.add(
		    Restrictions.disjunction()		
				.add(Restrictions.eq("codtipocapital", this.codtipocapital))
				.add(Restrictions.isNull("codtipocapital"))
			);
		}
		
		
		
		
		if(listCultivos != null && !listCultivos.contains(new BigDecimal("999"))){
			if(listCultivos.size() > 0){
				listCultivos.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("id.codcultivo", listCultivos));
		}
		if(FiltroUtils.noEstaVacio(this.codcultivo)){
			c.add(				
				Restrictions.disjunction()
				.add(Restrictions.eq("id.codcultivo", this.codcultivo))
				.add(Restrictions.eq("id.codcultivo", new BigDecimal("999")))
			);
		}

		if(listVariedades != null && !listVariedades.contains(new BigDecimal("999"))){
			if(listVariedades.size() > 0){
				listVariedades.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("id.codvariedad", listVariedades));
		}
			
		//COMENTADO POR ASF: LA VARIEDAD SI PUEDE SER '0' Y EL METOD DE FILTROUTILS NO LO ACEPTA!!!
		//if(FiltroUtils.noEstaVacio(this.codvariedad)){
		if(!StringUtils.nullToString(this.codvariedad).equals("")){
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("id.codvariedad", this.codvariedad))
				.add(Restrictions.eq("id.codvariedad", new BigDecimal("999")))
					
			);
		}
		
		
		// NOTA: solo se filtra por cultivo y variedad
		// el resto sobraría
		
		
		if(FiltroUtils.noEstaVacio(this.codprovincia))
		{
			c.add (				
			Restrictions.disjunction()
			.add(Restrictions.eq("codprovincia", this.codprovincia))
			.add(Restrictions.isNull("codprovincia"))
			
			);
		}
		if(FiltroUtils.noEstaVacio(this.codcomarca))
		{
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("codcomarca", this.codcomarca))
				.add(Restrictions.isNull("codcomarca"))
			);
		}
		if(FiltroUtils.noEstaVacio(this.codtermino))
		{
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("codtermino", this.codtermino))
				.add(Restrictions.isNull("codtermino"))
			
			);
		}
		if(FiltroUtils.noEstaVacio(this.subtermino))
		{
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("subtermino", this.subtermino))
				.add(Restrictions.isNull("subtermino"))
			
			);
		}
		
		
			return c;
	}
    
    
	public Long getLineaSeguroId() {
		return lineaSeguroId;
	}
	public void setLineaSeguroId(Long lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
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
	public BigDecimal getCodmaterialcubierta() {
		return codmaterialcubierta;
	}
	public void setCodmaterialcubierta(BigDecimal codmaterialcubierta) {
		this.codmaterialcubierta = codmaterialcubierta;
	}
	public BigDecimal getCodsistemaproteccion() {
		return codsistemaproteccion;
	}
	public void setCodsistemaproteccion(BigDecimal codsistemaproteccion) {
		this.codsistemaproteccion = codsistemaproteccion;
	}
	
}