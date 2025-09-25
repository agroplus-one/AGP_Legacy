package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;

import com.rsi.agp.dao.tables.orgDat.VistaCodigoReduccionRendimiento;

public class VistaCodigoReduccionRendimientoFiltro implements Filter{

	private Long lineaSeguroId;
	private String codmodulo;
    private BigDecimal codprovincia;
    private BigDecimal codcomarca;
    private BigDecimal codtermino;
    private Character subtermino;
    private List<BigDecimal> listProvincias;
    private List<BigDecimal> listComarcas;
    private List<BigDecimal> listTerminos;
    private List<Character> listSubterminos;
   
    
    
	public VistaCodigoReduccionRendimientoFiltro() {		
	}

	public VistaCodigoReduccionRendimientoFiltro(Long lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino, BigDecimal codtipocapital) {
		
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		
	}
	
	public void setListas(List<BigDecimal> listProvincias, List<BigDecimal> listComarcas, List<BigDecimal> listTerminos, List<Character> listSubterminos){
		this.listProvincias = listProvincias;
		this.listComarcas = listComarcas;
		this.listTerminos = listTerminos;
		this.listSubterminos = listSubterminos;
	}



	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaCodigoReduccionRendimiento.class);
		criteria.add(getTipoPlantacion());
		
		return criteria;
	}
	
	private final Conjunction getTipoPlantacion() {
		Conjunction c = Restrictions.conjunction();
		
		/* PROPIEDADES DE MODULO */
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) {
			c.add(Restrictions.eq("id.lineaseguroid", new BigDecimal(this.lineaSeguroId)));
		}
	
		if (FiltroUtils.noEstaVacio(this.codmodulo)) {
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
			
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens())
		    { 
		    	dd.add(Restrictions.eq("id.codmodulo",new String(tokens.nextToken())));
		    }
		    c.add(dd);
		}		
		
		if(listProvincias != null && !listProvincias.contains(new BigDecimal("99"))){
			if(listProvincias.size() > 0){
				listProvincias.add(new BigDecimal("99"));
				c.add(Restrictions.in("id.codprovincia", listProvincias));	
			}
		}
		if(FiltroUtils.noEstaVacio(this.codprovincia)){
			c.add (				
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codprovincia", this.codprovincia))
					.add(Restrictions.eq("id.codprovincia", new BigDecimal("99")))
			);
		}
     
		
		if(listComarcas != null && !listComarcas.contains(new BigDecimal("99"))){
			if(listComarcas.size() > 0){
				listComarcas.add(new BigDecimal("99"));
				c.add(Restrictions.in("id.codcomarca", listComarcas));
			}
		}
		if(FiltroUtils.noEstaVacio(this.codcomarca)){
			c.add (
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codcomarca", this.codcomarca))
					.add(Restrictions.eq("id.codcomarca", new BigDecimal("99")))
			);
		}
		
		if(listTerminos != null && !listTerminos.contains(new BigDecimal("999"))){
			if(listTerminos.size() > 0){
				listTerminos.add(new BigDecimal("999"));
				c.add(Restrictions.in("id.codtermino", listTerminos));
			}
		}
		if(FiltroUtils.noEstaVacio(this.codtermino)){
			c.add (
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codtermino", this.codtermino))
					.add(Restrictions.eq("id.codtermino", new BigDecimal("999")))
			);
		}
		
		if(listSubterminos != null && !listSubterminos.contains(new Character('9'))){
			if(listSubterminos.size() > 0){
				listSubterminos.add(new Character('9'));
				c.add(Restrictions.in("id.subtermino", listSubterminos));
			}
		}
		if(FiltroUtils.noEstaVacio(this.subtermino)){
			c.add (
					Restrictions.disjunction()
					.add(Restrictions.eq("id.subtermino", this.subtermino))
					.add(Restrictions.eq("id.subtermino", new Character('9')))
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

		
}
