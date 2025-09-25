package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.Date;
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
import com.rsi.agp.dao.tables.orgDat.VistaCicloCultivo;

public class VistaCicloCultivoFiltro implements Filter{

	private Long lineaSeguroId;
	private String codmodulo;
	private BigDecimal codcultivo;
    private BigDecimal codvariedad;
    private BigDecimal codprovincia;
    private BigDecimal codcomarca;
    private BigDecimal codtermino;
    private Date fecha;
    private List<BigDecimal> listCultivos;
    private List<BigDecimal> listVariedades;
    private List<BigDecimal> listCicloCultivo;
    
	public VistaCicloCultivoFiltro() {}

	public VistaCicloCultivoFiltro(Long lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino,Date fecha) {
		
		super();
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.fecha = fecha;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaCicloCultivo.class);		
		criteria.add(getCC());	
		criteria.addOrder(Order.asc("id.codciclocultivo"));
		criteria.add(Restrictions.le("id.feciniciocontrata", fecha));
		criteria.add(Restrictions.ge("id.fecfincontrata", fecha));
		return criteria;
	}
	
	public void setListas(List<BigDecimal> listCultivos, List<BigDecimal> listVariedades, List<BigDecimal> listCicloCultivo){
		this.listCultivos = listCultivos;
		this.listVariedades = listVariedades;
		this.listCicloCultivo = listCicloCultivo;
	}
	
	private final Conjunction getCC() {
		Conjunction c = Restrictions.conjunction();
		
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) {
			c.add(Restrictions.eq("id.lineaseguroid", this.lineaSeguroId));
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
		
		if(FiltroUtils.noEstaVacio(this.codcultivo)){
			//Filtramos por los datos de la parcela
			c.add(				
				Restrictions.disjunction()
				.add(Restrictions.eq("id.codcultivo", this.codcultivo))
				.add(Restrictions.eq("id.codcultivo", new BigDecimal("999")))
			);
		}
		else{
			//Filtramos por los datos de la clase
			if(listCultivos != null){
				if(listCultivos.size() > 0 && !listCultivos.contains(new BigDecimal("999"))){
					listCultivos.add(new BigDecimal("999"));
				}
				c.add(Restrictions.in("id.codcultivo", listCultivos));
			}
		}
		//COMENTADO POR ASF: LA VARIEDAD SI PUEDE SER '0' Y EL METOD DE FILTROUTILS NO LO ACEPTA!!!
		//if(FiltroUtils.noEstaVacio(this.codvariedad)){
		if(!StringUtils.nullToString(this.codvariedad).equals("")){
			//Filtramos por los datos de la parcela
			c.add (
				Restrictions.disjunction()
				.add(Restrictions.eq("id.codvariedad", this.codvariedad))
				.add(Restrictions.eq("id.codvariedad", new BigDecimal("999")))
			);
		}
		else{
			//Filtramos por los datos de la clase
			if(listVariedades != null){
				if(listCultivos.size() > 0 && !listVariedades.contains(new BigDecimal("999"))){
					listVariedades.add(new BigDecimal("999"));
				}
				c.add(Restrictions.in("id.codvariedad", listVariedades));
			}
		}
		
		if(listCicloCultivo != null && listCicloCultivo.size() > 0){
			c.add(Restrictions.in("id.codciclocultivo", listCicloCultivo));
		}		
		
		// los siguientes campos valen siempre 999,99(vale cualquiera)
		if(FiltroUtils.noEstaVacio(this.codprovincia)){
			c.add (				
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codprovincia", this.codprovincia))
					.add(Restrictions.eq("id.codprovincia", new BigDecimal("99")))
			);
		}
		if(FiltroUtils.noEstaVacio(this.codcomarca)){
			c.add (
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codcomarca", this.codcomarca))
					.add(Restrictions.eq("id.codcomarca", new BigDecimal("99")))
			);
		}
		if(FiltroUtils.noEstaVacio(this.codtermino)){
			c.add (
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codtermino", this.codtermino))
					.add(Restrictions.eq("id.codtermino", new BigDecimal("999")))
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	
}
