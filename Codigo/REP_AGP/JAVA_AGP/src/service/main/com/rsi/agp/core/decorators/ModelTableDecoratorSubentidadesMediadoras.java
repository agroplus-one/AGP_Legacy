package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;

public class ModelTableDecoratorSubentidadesMediadoras extends TableDecorator{
		
	public String getSubentSelec(){
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		String codEntidad = "";
		String nomEntidad= "";
		String codEntidadMediadora = "";
		String nomEntidadMediadora = "";
		String codSubentidadMediadora = "";
		String descTipoMediador = "";
		String tipoIdentificacion = "";
		String nifCif = "";
		String pagodirecto = "";
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String nomSubentidadMediadora = "";
		String codigoPostal = "";
		String cargoCuenta ="";
		String iban ="";
		String forzarRevisionAM = "";
		String calcularRcGanado = "";
		String swConfirmacion = "";
		String indGastosAdq = "";
		String email = "";
		String email2 = "";
		String firmaTableta = "";
		
		if ((sm.getEntidad() != null) && (sm.getEntidad().getCodentidad() != null)){
			codEntidad = StringUtils.forHTML(sm.getEntidad().getCodentidad().toString());
		}
		
		if (sm.getEntidad() != null){
			nomEntidad = StringUtils.forHTML(sm.getEntidad().getNomentidad());
		}
		
		if ((sm.getId() != null) && (sm.getId().getCodentidad() != null)){
			codEntidadMediadora = StringUtils.forHTML(sm.getId().getCodentidad().toString());
		}
		
		if ((sm.getEntidadMediadora() != null) && (sm.getEntidadMediadora().getNomentidad() != null)){
			nomEntidadMediadora = StringUtils.forHTML(sm.getEntidadMediadora().getNomentidad());
		}
		
		if ((sm.getId() != null) && (sm.getId().getCodsubentidad() != null)){
			codSubentidadMediadora = StringUtils.forHTML(sm.getId().getCodsubentidad().toString());
		}
		
		
		descTipoMediador = getSubentTipoMediador();
	
		
		if (sm.getTipoidentificacion() != null){
			tipoIdentificacion = sm.getTipoidentificacion();
		}
		
		if (sm.getPagodirecto() != null){
			pagodirecto = sm.getPagodirecto().toString();
		}
		
		if (sm.getNifcif() != null){
			nifCif = sm.getNifcif();
		}
		
		if (sm.getNombre() != null){
			nombre = sm.getNombre();
		}
		
		if (sm.getApellido1() != null){
			apellido1 = sm.getApellido1();
		}
		
		if (sm.getApellido2() != null){
			apellido2 = sm.getApellido2();
		}
		
		if (sm.getNomSubentidadCompleto() != null){
			nomSubentidadMediadora = sm.getNomSubentidadCompleto();
		}
		
		if (sm.getCodpostal() != null){
			codigoPostal = sm.getCodpostal().toString();
		}	
		if (sm.getCargoCuenta() != null) {
			cargoCuenta = sm.getCargoCuenta().toString();
		}
		if (sm.getIban() != null) {
			iban = sm.getIban();
		}
		if(sm.getForzarRevisionAM() != null){
			forzarRevisionAM = sm.getForzarRevisionAM().toString();
		}
		if(sm.getCalcularRcGanado() != null){
			calcularRcGanado = sm.getCalcularRcGanado().toString();
		}
		if(sm.getSwConfirmacion() !=null){
			swConfirmacion = sm.getSwConfirmacion().toString();
		}
		
		if (sm.getIndGastosAdq() != null) {
			indGastosAdq = sm.getIndGastosAdq().toString();
		}

		if (sm.getEmail() != null) {
			email = sm.getEmail();
		}
		
		if (sm.getEmail2() != null) {
			email2 = sm.getEmail2();
		}
		
		if (sm.getFirmaTableta() != null) {
			firmaTableta = sm.getFirmaTableta().toString();
		}
		
		
		String modif = "<a href=\"javascript:modificar('"+codEntidad+"','"+nomEntidad+"','"+codEntidadMediadora+"',"+
						"'"+nomEntidadMediadora+"','"+codSubentidadMediadora+"','"+descTipoMediador+"','"+tipoIdentificacion+
						"','"+nifCif+"','"+pagodirecto+"','"+nombre+"','"+apellido1+"','"+apellido2+"','"+nomSubentidadMediadora+"','"+
						codigoPostal+"','"+cargoCuenta+"','"+iban+"','"+forzarRevisionAM+"','"+calcularRcGanado+"','"+swConfirmacion+"','"+indGastosAdq+"','"+email+"','"+email2+"'"
						+",'"+firmaTableta+ "')\">"+"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";
		
		String baja = "<a href=\"javascript:ajaxCheckBaja('"+codEntidadMediadora+"','"+codSubentidadMediadora+"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>";
		String deshacerBaja ="<a href=\"javascript:deshacerBaja('"+codEntidadMediadora+"','"+codSubentidadMediadora+"')\"><img src=\"jsp/img/displaytag/reciclaje.gif\" alt=\"Deshacer baja\" title=\"Deshacer baja\"/></a>";
		// Si la subentidad mediadora tiene fecha de baja no se permite ninguna acción sobre ella
		return sm.getFechabaja() != null ? deshacerBaja : modif + baja;
	}
	
	public String getEntidad (){
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		String resultado = "";
		if(sm.getEntidad() != null)
			resultado = StringUtils.nullToString(sm.getEntidad().getCodentidad());
		return resultado;
	}
	
	public String getSubentEntMed (){
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		return StringUtils.nullToString(sm.getId().getCodentidad().toString()) + " - " + StringUtils.nullToString(sm.getId().getCodsubentidad().toString());
	}
	
	public String getSubentNombreRazonSocial (){
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		if (StringUtils.nullToString(sm.getNomSubentidadCompleto()).equals("")) {
			if (sm.getTipoidentificacion().equals(Constants.TIPO_IDENTIFICACION_NIF)){
				return StringUtils.nullToString(sm.getNombre()+" "+sm.getApellido1()+" "+sm.getApellido2());
			}else {
				return StringUtils.nullToString(sm.getNomsubentidad());
			}
		}else {
			return sm.getNomSubentidadCompleto();
		}
	}
	
	public String getSubentTipoMediador (){
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		
		if (sm.getTipoMediador() != null && sm.getTipoMediador().getDescripcion() != null) {
			return sm.getTipoMediador().getDescripcion();
		}
		
		return "";
	}
	
	public String getCargoCuenta() {
		String cargoCuenta="";	
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		
		if (sm.getCargoCuenta().compareTo(Constants.VALOR_0)==0) {
			cargoCuenta = Constants.VALOR_NO;
		}else if (sm.getCargoCuenta().compareTo(Constants.VALOR_1)==0){
			cargoCuenta = Constants.VALOR_SI;
		}
		return cargoCuenta;
	}
	
	public Date getFechaBaja() {
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		Date date = null;
		if (sm.getFechabaja() != null){

			date = sm.getFechabaja();
		}
		return date;
	}
	
	
	public String getForzarRevisionAM(){
		String forzarRevisionAM = "";
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		if(sm.getForzarRevisionAM() != null){
			if(sm.getForzarRevisionAM().compareTo(Constants.VALOR_0) == 0){
				forzarRevisionAM = Constants.VALOR_NO;
			} else if (sm.getForzarRevisionAM().compareTo(Constants.VALOR_1) == 0){
				forzarRevisionAM = Constants.VALOR_SI;
			}
		}
		return forzarRevisionAM;
	}
	
	public String getCalcularRcGanado(){
		String calcularRcGanado = "";
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		if(sm.getCalcularRcGanado() != null){
			if(sm.getCalcularRcGanado().compareTo(Constants.VALOR_0) == 0){
				calcularRcGanado = Constants.VALOR_NO;
			} else if (sm.getCalcularRcGanado().compareTo(Constants.VALOR_1) == 0){
				calcularRcGanado = Constants.VALOR_SI;
			}
		}
		return calcularRcGanado;
	}
	
	public String getSwConfirmacion(){
		String swConfirmacion ="";
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		if (sm.getSwConfirmacion() != null){
			if(sm.getSwConfirmacion().compareTo(Constants.VALOR_0) == 0){
				swConfirmacion = Constants.VALOR_NO;
			} else if (sm.getSwConfirmacion().compareTo(Constants.VALOR_1) == 0){
				swConfirmacion = Constants.VALOR_SI;
			}
			
		}
		return swConfirmacion;
	}
	
	public String getIndGastosAdq() {
		String str ="";
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		if (sm.getIndGastosAdq() != null){
			if(sm.getIndGastosAdq().compareTo(Constants.VALOR_0) == 0){
				str = Constants.VALOR_NO;
			} else if (sm.getIndGastosAdq().compareTo(Constants.VALOR_1) == 0){
				str = Constants.VALOR_SI;
			}
			
		}
		return str;
	}
	
	public String getFirmaTableta() {
		String str ="";
		SubentidadMediadora sm = (SubentidadMediadora)getCurrentRowObject();
		if (sm.getFirmaTableta() != null){
			if(sm.getFirmaTableta().compareTo(Constants.VALOR_0) == 0){
				str = Constants.VALOR_NO;
			} else if (sm.getFirmaTableta().compareTo(Constants.VALOR_1) == 0){
				str = Constants.VALOR_SI;
			}
			
		}
		return str;
	}
	
}
