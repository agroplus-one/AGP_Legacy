package com.rsi.agp.dao.tables.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CalculoPolizaView 
{
	private BigDecimal primaComercial;
	private BigDecimal primaNeta;
	private BigDecimal costeNeto;
	private BigDecimal bonifAseg;
	private BigDecimal pctBonifAseg;
	private BigDecimal rcgoAseg;
	private BigDecimal pctRcgoAseg;
	private BigDecimal bonifMedPreventivas;
	private BigDecimal pctBonifMedPreventivas;
	private BigDecimal dtoContColectiva;
	private BigDecimal pctDtoContColectiva;
	private BigDecimal reasegConsorcio;
	private BigDecimal rcgoConsorcio;
	private ArrayList<SubvEnesaView> subvEnesa;
	private BigDecimal totalEnesa;
	private ArrayList<SubvCCAAView> subvCCAA;
	private BigDecimal totalCCAA;
	private BigDecimal impCargoTomador;
	private String admiteCompl;
	private ComparativaPolizaId identificador;
	
	public BigDecimal getPrimaComercial() {
		return primaComercial;
	}
	public void setPrimaComercial(BigDecimal primaComercial) {
		this.primaComercial = primaComercial;
	}
	public BigDecimal getPrimaNeta() {
		return primaNeta;
	}
	public void setPrimaNeta(BigDecimal primaNeta) {
		this.primaNeta = primaNeta;
	}
	public BigDecimal getCosteNeto() {
		return costeNeto;
	}
	public void setCosteNeto(BigDecimal costeNeto) {
		this.costeNeto = costeNeto;
	}
	public BigDecimal getBonifAseg() {
		return bonifAseg;
	}
	public void setBonifAseg(BigDecimal bonifAseg) {
		this.bonifAseg = bonifAseg;
	}
	public BigDecimal getPctBonifAseg() {
		return pctBonifAseg;
	}
	public void setPctBonifAseg(BigDecimal pctBonifAseg) {
		this.pctBonifAseg = pctBonifAseg;
	}
	public BigDecimal getRcgoAseg() {
		return rcgoAseg;
	}
	public void setRcgoAseg(BigDecimal rcgoAseg) {
		this.rcgoAseg = rcgoAseg;
	}
	public BigDecimal getPctRcgoAseg() {
		return pctRcgoAseg;
	}
	public void setPctRcgoAseg(BigDecimal pctRcgoAseg) {
		this.pctRcgoAseg = pctRcgoAseg;
	}
	public BigDecimal getBonifMedPreventivas() {
		return bonifMedPreventivas;
	}
	public void setBonifMedPreventivas(BigDecimal bonifMedPreventivas) {
		this.bonifMedPreventivas = bonifMedPreventivas;
	}
	public BigDecimal getPctBonifMedPreventivas() {
		return pctBonifMedPreventivas;
	}
	public void setPctBonifMedPreventivas(BigDecimal pctBonifMedPreventivas) {
		this.pctBonifMedPreventivas = pctBonifMedPreventivas;
	}
	public BigDecimal getDtoContColectiva() {
		return dtoContColectiva;
	}
	public void setDtoContColectiva(BigDecimal dtoContColectiva) {
		this.dtoContColectiva = dtoContColectiva;
	}
	public BigDecimal getPctDtoContColectiva() {
		return pctDtoContColectiva;
	}
	public void setPctDtoContColectiva(BigDecimal pctDtoContColectiva) {
		this.pctDtoContColectiva = pctDtoContColectiva;
	}
	public BigDecimal getReasegConsorcio() {
		return reasegConsorcio;
	}
	public void setReasegConsorcio(BigDecimal reasegConsorcio) {
		this.reasegConsorcio = reasegConsorcio;
	}
	public BigDecimal getRcgoConsorcio() {
		return rcgoConsorcio;
	}
	public void setRcgoConsorcio(BigDecimal rcgoConsorcio) {
		this.rcgoConsorcio = rcgoConsorcio;
	}
	public ArrayList<SubvEnesaView> getSubvEnesa() {
		return subvEnesa;
	}
	public void setSubvEnesa(ArrayList<SubvEnesaView> subvEnesa) {
		this.subvEnesa = subvEnesa;
	}
	public BigDecimal getTotalEnesa() {
		return totalEnesa;
	}
	public void setTotalEnesa(BigDecimal totalEnesa) {
		this.totalEnesa = totalEnesa;
	}
	public ArrayList<SubvCCAAView> getSubvCCAA() {
		return subvCCAA;
	}
	public void setSubvCCAA(ArrayList<SubvCCAAView> subvCCAA) {
		this.subvCCAA = subvCCAA;
	}
	public BigDecimal getTotalCCAA() {
		return totalCCAA;
	}
	public void setTotalCCAA(BigDecimal totalCCAA) {
		this.totalCCAA = totalCCAA;
	}
	public BigDecimal getImpCargoTomador() {
		return impCargoTomador;
	}
	public void setImpCargoTomador(BigDecimal impCargoTomador) {
		this.impCargoTomador = impCargoTomador;
	}
	public String getAdmiteCompl() {
		return admiteCompl;
	}
	public void setAdmiteCompl(String admiteCompl) {
		this.admiteCompl = admiteCompl;
	}
	public ComparativaPolizaId getIdentificador() {
		return identificador;
	}
	public void setIdentificador(ComparativaPolizaId identificador) {
		this.identificador = identificador;
	}
	public String dameColumnaImportes() 
	{
		String cadena ="";
		cadena += "Prima Comercial:       "+this.getPrimaComercial()+"€<br>";
		cadena += "Prima Neta Bonif/Rec:  "+this.getPrimaNeta()+"€<br>";
		cadena += "Coste Neto:            "+this.getCosteNeto()+"€<br>";
		return cadena;
	}
	
	public String dameColumnaBonifDesc()
	{
		String cadena = "";
		BigDecimal numCero = new BigDecimal(0);
		cadena += "Total Bonif/Desc:      ";
		if (this.bonifAseg != null)
		{
			cadena += "Bonif. Aseg: ("+this.pctBonifAseg+"%):    "+this.bonifAseg+"€<br>";
		}			
		else
		{
			cadena += "Recargo Aseg: ("+this.pctRcgoAseg+"%):    "+this.rcgoAseg+"€<br>";
		}
		if (this.bonifMedPreventivas == null)
			this.setBonifMedPreventivas(numCero);
		cadena += "Bonif. M. Prev:        "+this.bonifMedPreventivas+"€<br>";
		if (this.dtoContColectiva == null)
		{
			this.setDtoContColectiva(numCero);
			this.setPctDtoContColectiva(numCero);
		}		
		cadena += "Descuentos ("+this.getPctDtoContColectiva()+"%):      "+this.getDtoContColectiva()+"€<br>";
		return cadena;
	}
	
	public String dameColumnaConsorcio()
	{
		String cadena = "";
		BigDecimal numCero = new BigDecimal(0);
		if (this.getReasegConsorcio() == null)
		{
			this.setReasegConsorcio(numCero);			
		}
		if (this.getRcgoConsorcio() == null)
		{
			this.setRcgoConsorcio(numCero);
		}
		cadena += "Reaseguro:             "+this.getReasegConsorcio()+"€<br>";
		cadena += "Recargo:               "+this.getRcgoConsorcio()+"€<br>";
		return cadena;
	}
	
	public String dameColumnaEnesa ()
	{
		String cadena = "";
		if (this.getSubvEnesa() != null && this.getSubvEnesa().size() > 0)
		{
			cadena += "Total ENESA:            "+this.getTotalEnesa()+"€<br>";
			for (SubvEnesaView en: this.getSubvEnesa())
			{
				cadena += en.getDescSubvencion()+":    "+en.getImporte()+"€<br>";
			}
		}
		else
		{
			cadena += "Total ENESA:            0,00€";
		}
		return cadena;
	}
	
	public String dameColumnaCCAA ()
	{
		String cadena = ""; 
		if (this.getSubvCCAA() != null && this.getSubvCCAA().size() > 0)
		{
			cadena += "Total CCAA:             "+this.getTotalCCAA()+"€<br>";
			for (SubvCCAAView ccaa: this.getSubvCCAA())
			{
				cadena += "CCAA "+ccaa.getDescOrganismo()+":    "+ccaa.getImporte()+"€<br>";
			}
		}
		else
		{
			cadena += "Total CCAA:             0,00€";
		}
		return cadena;
	}
}
