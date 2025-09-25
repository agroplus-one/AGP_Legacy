package com.rsi.agp.dao.filters.cpl;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.ReduccionRdtoAmbito;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class ReduccionRendimientosAmbitosFiltro implements Filter {

	private Long lineaSeguroId;
	private String codmodulo;
    private BigDecimal codreducrdto;	
    private BigDecimal codprovincia;
    private BigDecimal codcomarca;
    private BigDecimal codtermino;
    private Character subtermino;
	
    public ReduccionRendimientosAmbitosFiltro(){
    	
    }
    
	public ReduccionRendimientosAmbitosFiltro(Long lineaSeguroId,
			String codmodulo, BigDecimal codreducrdto, BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino) {
		super();
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.codreducrdto = codreducrdto;
	}

	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(ReduccionRdtoAmbito.class);
		
		if (lineaSeguroId != null){
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaSeguroId));
		}

		if (codmodulo != null){
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
		}

		if (codreducrdto != null){
			criteria.add(Restrictions.eq("id.codreducrdto", codreducrdto));
		}

		
		Conjunction c = Restrictions.conjunction();
		
		if(codprovincia != null){
		
			c.add(
					Restrictions.disjunction()
						.add(Restrictions.eq("id.codprovincia", codprovincia))
						.add(Restrictions.eq("id.codprovincia", new BigDecimal("99")))
			);

		}
		
		if(codcomarca != null){
		
			c.add(
					Restrictions.disjunction()
						.add(Restrictions.eq("id.codcomarca", codcomarca))
						.add(Restrictions.eq("id.codcomarca", new BigDecimal("99")))
			);

		}
		
		if(codtermino != null){
		
			c.add(
					Restrictions.disjunction()
						.add(Restrictions.eq("id.codtermino", codtermino))
						.add(Restrictions.eq("id.codtermino", new BigDecimal("999")))
			);
		
		}
		
		if(subtermino != null){

			c.add(
					Restrictions.disjunction()
						.add(Restrictions.eq("id.subtermino", subtermino))	
						.add(Restrictions.eq("id.subtermino", new Character('9')))			
			);				
		
		}

		criteria.add(c);
		
		return criteria;	
	
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

	public BigDecimal getCodreducrdto() {
		return codreducrdto;
	}

	public void setCodreducrdto(BigDecimal codreducrdto) {
		this.codreducrdto = codreducrdto;
	}

}
