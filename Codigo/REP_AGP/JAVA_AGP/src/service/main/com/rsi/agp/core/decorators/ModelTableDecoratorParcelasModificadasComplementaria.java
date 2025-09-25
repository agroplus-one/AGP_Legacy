package com.rsi.agp.core.decorators;

import java.math.BigDecimal;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Parcela;

public class ModelTableDecoratorParcelasModificadasComplementaria extends TableDecorator{
	
	public String getAdmActionsAnexo() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String editar = "<a href=\"javascript:abrirIncremento('"+capAseg.getId()+"')\">" +
				"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
		String deshacer = "<a href=\"#\" onclick=\"javascript:deshacerCambios("+capAseg.getId()+")\"><img src='jsp/img/displaytag/deshacer.png' alt='Deshacer' title='Deshacer'/></a>";
		String deshacerSw = "<a href=\"#\" onclick=\"javascript:deshacerCambiosSw("+capAseg.getId()+")\"><img src='jsp/img/displaytag/deshacer.png' alt='Deshacer' title='Deshacer'/></a>";
		String transparente = "<img src='jsp/img/displaytag/transparente.gif'  style='width:16px;height:16px' />";
		String acciones = "";
		
		boolean edito = true;
		BigDecimal estadoAnexo = capAseg.getParcela().getAnexoModificacion().getEstado().getIdestado();
		Long estadoCupon = null;
		if (capAseg.getParcela().getAnexoModificacion().getCupon()!=null){
			estadoCupon = capAseg.getParcela().getAnexoModificacion().getCupon().getEstadoCupon().getId();
		}
		
		// Solo se permite edicion en el estado borrador y error
		if(!Constants.ANEXO_MODIF_ESTADO_ENVIADO.equals(estadoAnexo) && !Constants.ANEXO_MODIF_ESTADO_CORRECTO.equals(estadoAnexo)) {
			// Si el cupón está caducado tampoco se permite modificar
			if (estadoCupon!=null){
				if (estadoCupon.equals(Constants.AM_CUPON_ESTADO_CADUCADO))
					edito = false;		
			}
			
			if (edito){
				if(capAseg.getTipomodificacion() == null){
					acciones = editar + transparente;
				}else{
					acciones = editar;
					if (capAseg.getParcela().getAnexoModificacion().getCupon() != null &&
							!StringUtils.nullToString(capAseg.getParcela().getAnexoModificacion().getCupon().getId()).equals(""))
						acciones += deshacerSw;
					else
						acciones += deshacer;
				}
			}
		}
		
		acciones  += "<input type='hidden' name='idRow' " +
				"value=\""+capAseg.getId()+ "#" + capAseg.getSuperficie().toString() +"#" + capAseg.getIncrementoproduccionanterior() +"\"/>";
		return acciones;
	}
	
	public String getEstado(){
		String resultado = "";
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		
		if(capitalAsegurado.getTipomodificacion() != null){
			if(capitalAsegurado.getTipomodificacion().equals(new Character('A'))){
				resultado = "Alta";
			}else if (capitalAsegurado.getTipomodificacion().equals(new Character('B'))) {
				resultado = "Baja";
			}else if (capitalAsegurado.getTipomodificacion().equals(new Character('M'))) {
				resultado = "Modif";
			}
		}
		
		return resultado;
	}
	
	public String getChecks(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		return "<input type=\"checkbox\" "+
				"value=\"check_"+capitalAsegurado.getId()+ "#" + capitalAsegurado.getSuperficie().toString() +"#" + capitalAsegurado.getIncrementoproduccionanterior() +"\""+
				"onclick=\"onClickInCheckAnexo('"+capitalAsegurado.getId()+"');numero_check_seleccionados();\" " +
				"id=\"check_"+capitalAsegurado.getId()+"\" name=\"check_"+capitalAsegurado.getId()+"\" class=\"dato\">";
	}
	
	
	
	public String getCapital(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getTipoCapital().getCodtipocapital());
	}
	
	public String getNombre() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getNomparcela());
				
	}
	
	public String getCodtermino() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getCodtermino());
	}

	public String getCodsubtermino() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getSubtermino());
	}
	
	public String getIdCat() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if (capAseg.getParcela().getPoligono() != null && capAseg.getParcela().getParcela() != null) {
			return StringUtils.nullToString(capAseg.getParcela().getPoligono()) +" - "+ StringUtils.nullToString(capAseg.getParcela().getParcela_1());
		} else {
			return getSigPac(capAseg.getParcela());
		}
	}

	public String getSigPac(Parcela pa) {
		String sigPac = "";
		if(pa.getCapitalAsegurados().size() > 0) {
				CapitalAsegurado ca = (CapitalAsegurado) pa.getCapitalAsegurados().toArray()[0];
			
				sigPac = StringUtils.nullToString(ca.getParcela().getCodprovsigpac());
				sigPac += "-"+ StringUtils.nullToString(ca.getParcela().getCodtermsigpac());
				sigPac += "-"+ StringUtils.nullToString(ca.getParcela().getAgrsigpac());
				sigPac += "-"+ StringUtils.nullToString(ca.getParcela().getZonasigpac());
				sigPac += "-"+ StringUtils.nullToString(ca.getParcela().getPoligonosigpac());
				sigPac += "-"+ StringUtils.nullToString(ca.getParcela().getParcelasigpac());
				sigPac += "-"+ StringUtils.nullToString(ca.getParcela().getRecintosigpac());		
		}
		return sigPac;
	}
	public String getCodprovincia() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getCodprovincia());
	}
	public String getCodcomarca(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getCodcomarca());
	}
		
	public String getPrecio(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getPrecio());
	}
	public String getProduccion(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getProduccion());
	}
	public String getSuperf(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getSuperficie());
	}
	
	public String getNomPar(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getNomparcela());
		
	}
	public String getNumero(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getHoja()) + "-"+ StringUtils.nullToString(capAseg.getParcela().getNumero());
	}
	
	public String getCodVariedad(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getCodvariedad());
	}
	
	public String getCodCultivo(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getCodcultivo());
	}
	
	public String getIncremento(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String res = "";
		if(capAseg.getIncrementoproduccionanterior() != null)
			res = capAseg.getIncrementoproduccionanterior().intValue() + "";
		return res;
	}
	public String getkilosAseg(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String res = "";
		
		//Si los dos son distintos de null,sumamos
		if(capAseg.getIncrementoproduccion() != null && capAseg.getProduccion()!= null){
			res = capAseg.getProduccion().add(capAseg.getIncrementoproduccion())+"";
		
		}
		else if(capAseg.getIncrementoproduccion() == null && capAseg.getProduccion()!= null){
			//Si el incremento es null pero la produccion no
			if (capAseg.getIncrementoproduccionanterior() != null) {
				// Si el incremento anterior está informado se suma a la producción
				res = capAseg.getProduccion().add(capAseg.getIncrementoproduccionanterior()) +"";
			}
			else {
				// Si el incremento anterior es nulo, se muestra la producción
				res = capAseg.getProduccion() +"";
			}
		}
		//Si el incremento NO es null y la produccion si, sacamos el incremento
		else if (capAseg.getIncrementoproduccion() != null && capAseg.getProduccion()== null){
			res = capAseg.getIncrementoproduccion() +"";
		}
		return res;
	}
	public String getIncrModif(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		String res = "";
		
		if (capitalAsegurado.getIncrementoproduccion() != null){
			res = capitalAsegurado.getIncrementoproduccion().floatValue()+"";
		}
		return res;
	}
	public String getKilosIncrementar(){
		String res = "";
		String tipo = "";
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		
		if (capAseg.getTipoincremento()!= null){
			if (capAseg.getTipoincremento().equalsIgnoreCase("kha")){
				tipo = "Kg tot./Ha";
			}else if (capAseg.getTipoincremento().equalsIgnoreCase("ha")){
				tipo = "Kg/Ha";
			}else if (capAseg.getTipoincremento().equalsIgnoreCase("pa")){
				tipo = "Kg/Pa";
			}
		}
		if (capAseg.getValorincremento() != null){
			res = capAseg.getValorincremento()+" "+tipo;
		}
		return res;
	}
}
