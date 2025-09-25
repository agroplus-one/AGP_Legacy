package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantia308;

public class VistaFechaFinGarantiasEspecificoYGenericoFiltro308 implements Filter 
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
	private BigDecimal codpracticacultural;
	
	
	public VistaFechaFinGarantiasEspecificoYGenericoFiltro308() 
	{
		
	}

	public VistaFechaFinGarantiasEspecificoYGenericoFiltro308(String codmodulo, BigDecimal codcultivo, BigDecimal codvariedad, Long lineaSeguroId, BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino, Character  subtermino, BigDecimal codtipocapital, BigDecimal codpracticacultural) 
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
		this.codpracticacultural = codpracticacultural;
	}

	@Override
	public Criteria getCriteria(Session sesion) 
	{
		Criteria criteria = sesion.createCriteria(FechaFinGarantia308.class);

		criteria.add(getFinGarantias());

		return criteria;
	}

	private final Conjunction getFinGarantias() 
	{
     
		Conjunction c = Restrictions.conjunction();
        
		/* PROPIEDADES DE MODULO */
		
		if (this.lineaSeguroId != null) 
		{
			c.add(Restrictions.eq("id.lineaseguroid", this.lineaSeguroId));
		}

		if (this.codmodulo != null) 
		{
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
			
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens())
		    { 
		    	dd.add(Restrictions.eq("modulo.id.codmodulo",new String(tokens.nextToken())));
		    }
		    c.add(dd);
		}

		
		if (this.codcultivo != null) 
		{
			c.add(
					Restrictions.disjunction().add(Restrictions.eq("variedad.id.codcultivo", this.codcultivo))
					.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal(999)))
			);
		}
		
		if (this.codvariedad != null) 
		{
			c.add(
					Restrictions.disjunction().add(Restrictions.eq("variedad.id.codvariedad", this.codvariedad))
					.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal(999)))
			);
		}
		
		if (this.codprovincia != null){
			c.add(
					Restrictions.disjunction().add(Restrictions.eq("codprovincia", this.codprovincia))
					.add(Restrictions.eq("codprovincia", new BigDecimal(99)))
			);
		}
		
		if (this.codcomarca != null){
			c.add(
					Restrictions.disjunction().add(Restrictions.eq("codcomarca", this.codcomarca))
					.add(Restrictions.eq("codcomarca", new BigDecimal(99)))
			);
		}
		
		if (this.codtermino != null){
			c.add(
					Restrictions.disjunction().add(Restrictions.eq("codtermino", this.codtermino))
					.add(Restrictions.eq("codtermino", new BigDecimal(999)))
			);
		}
		
		if (this.subtermino != null){
			c.add(
					Restrictions.disjunction().add(Restrictions.eq("subtermino", this.subtermino))
					.add(Restrictions.eq("subtermino", new Character('9')))
			);
		}
		
		if (this.codtipocapital != null){
			c.add(Restrictions.eq("tipoCapital.codtipocapital", this.codtipocapital));
		}
		
		if (this.codpracticacultural != null){
			c.add(Restrictions.eq("practicaCultural.codpracticacultural", this.codpracticacultural));
		}
		
		return c;
	}

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

	public BigDecimal getCodpracticacultural() {
		return codpracticacultural;
	}

	public void setCodpracticacultural(BigDecimal codpracticacultural) {
		this.codpracticacultural = codpracticacultural;
	}

}
