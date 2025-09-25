/**
 * 
 */
package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;

/**
 * @author U029769
 *
 */
public class ModelTableDecoratorMtoDescuentosHistorico extends TableDecorator{
	
	private final static String VACIO = "";
	
	public String getEntidad(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		
		return StringUtils.nullToString(cs.getEntidad().getCodentidad());
	}
	
	public String getPlan() {
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getDescuentos().getLinea().getCodplan());
	}
	
	public String getLinea() {
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getDescuentos().getLinea().getCodlinea());
	}

	public String getPermitirRecargo() {
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		String res;
		if(cs.getPermitirRecargo()==Constants.PERMITIR_RECARGO_NO) {
			res = Constants.PERMITIR_RECARGO_NO_TXT ;
		}else {
			res =Constants.PERMITIR_RECARGO_SI_TXT;
		}
			return res;		
	}

	public String getVerComisiones() {
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		String res="";
		switch (cs.getVerComisiones()) {
			case Constants.VER_COMISIONES_NO:
				res= Constants.VER_COMISIONES_NO_TXT;
				break;
			case Constants.VER_COMISIONES_ENTIDAD:
				res= Constants.VER_COMISIONES_ENTIDAD_TXT;
				break;
			case Constants.VER_COMISIONES_ENTIDAD_MEDIADORA:
				res= Constants.VER_COMISIONES_ENTIDAD_MEDIADORA_TXT;
				break;
			case Constants.VER_COMISIONES_TODAS:
				res= Constants.VER_COMISIONES_TODAS_TXT;
				break;
			default:
				break;
		}
		return res;
	}
	
	public String getOficina(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		String codOficina;
		BigDecimal oficina = (BigDecimal) cs.getOficina().getId().getCodoficina();
		if (StringUtils.nullToString(cs.getOficina().getId().getCodoficina()).equals("")|| oficina.intValue() == Constants.SIN_OFICINA.intValue()) {
			codOficina = "&nbsp;";
		}else {
			codOficina = cs.getOficina().getId().getCodoficina().toString();
			
			if (codOficina.length()<4) {
				while (codOficina.length()<4) {
					codOficina= "0" + codOficina;
				}
			}
		}
		return codOficina;
	}
	
	public String getEsMed(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		
		return StringUtils.nullToString(cs.getCodentmed()+"-"+cs.getCodsubentmed());
	}
	
	public String getDelegacion(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		
		return !VACIO.equals(StringUtils.nullToString(cs.getDelegacion()))?cs.getDelegacion().toString():"Todas";
	}
	
	public String getPctMaximo(){
		DescuentosHistorico dh = (DescuentosHistorico) getCurrentRowObject();

		
		if(dh.getPctDescMax() != null){
			return StringUtils.nullToString(dh.getPctDescMax().setScale(2, BigDecimal.ROUND_DOWN)) + "%";
		}	
		return "";
	}
	
	public String getTipoMov(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		if (cs.getOperacion().equals(Constants.ALTA_DESCUENTO)) {
			return ComisionesConstantes.AccionesHistComisionCte.ALTA;
		}else if (cs.getOperacion().equals(Constants.MOD_DESCUENTO)) {
			return ComisionesConstantes.AccionesHistComisionCte.MODIFICACION;
		}else if (cs.getOperacion().equals(Constants.BAJA_DESCUENTO)) {
			return ComisionesConstantes.AccionesHistComisionCte.BAJA;
		}
		return "";
	}
	
	public Date getFechaMov(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		return cs.getFecha();
	}
	
	public String getUsuario(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getUsuario());
	}
	
	public String getNomEntidad(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getEntidad().getNomentidad());
	}
	
	public String getNomOficina(){
		DescuentosHistorico cs = (DescuentosHistorico) getCurrentRowObject();
		BigDecimal oficina = (BigDecimal) cs.getOficina().getId().getCodoficina();
		if (StringUtils.nullToString(cs.getOficina().getId().getCodoficina()).equals("")|| oficina.intValue() == Constants.SIN_OFICINA.intValue()) {
			return Constants.SIN_OFICINA_NOMBRE;
		}else {
			return StringUtils.nullToString(cs.getOficina().getNomoficina());
		}
		
	}	
}