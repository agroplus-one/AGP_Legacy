package com.rsi.agp.core.decorators;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class ModelTableDecoratorListaParcelas extends TableDecorator {

	private static final Log logger = LogFactory.getLog(ModelTableDecoratorListaParcelas.class);
	
	public String getAdmActionsConsulta(){
		String acciones="";
		Parcela parcela = (Parcela) getCurrentRowObject();
		acciones +=  "<a href=\"javascript:visualizarDatosRegistro('";
		acciones += parcela.getTipoparcela();
		acciones +=  "','";
		acciones += parcela.getIdparcela();
		acciones += "')\"><img src=\"jsp/img/displaytag/information.png\" alt=\"Visualizar informaci&oacute;n\" title=\"Visualizar informaci&oacute;n\"/></a>";
		
		return acciones;
	}
	
	public String getAdmActions() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		String acciones = "";
		String tipoParcela;
		
		if(parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();
		else
			tipoParcela = "";
		
		// Si la poliza tiene idestado = 8 
		// solo se mostrara un 脙潞nico icono que servira para 
		// visualizar los datos de las parcelas (ni editar ni borrar)
		/// mejora 112 Angel 01/02/2012 a帽adida la opci贸n de ver la p贸liza sin opci贸n a editarla tambi茅n con estado grabaci贸n definitiva
		if(parcela.getPoliza().getEstadoPoliza().getIdestado().intValue() == 8 || parcela.getPoliza().getEstadoPoliza().getIdestado().intValue() == 3){
			acciones +=  "<a href=\"javascript:visualizarDatosRegistro('";
			acciones += parcela.getTipoparcela();
			acciones +=  "','";
			acciones += parcela.getIdparcela();
			acciones += "')\"><img src=\"jsp/img/displaytag/information.png\" alt=\"Visualizar informaci&oacute;n\" title=\"Visualizar informaci&oacute;n\"/></a>";
			acciones += "<img src='jsp/img/displaytag/transparente.gif' style='width:16;height:16' />&nbsp;";
			acciones += "<img src='jsp/img/displaytag/transparente.gif' style='width:16;height:16' />&nbsp;";
			acciones += "<img src='jsp/img/displaytag/transparente.gif' style='width:16;height:16' />";
		}else {
			acciones +=  "<a href=\"javascript:updateParcela('";
			acciones += parcela.getTipoparcela();
			acciones +=  "','";
			acciones += parcela.getIdparcela();
			acciones += "')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
			
			if(parcela.getTipoparcela().equals('P')){
				acciones += "<a href=\"javascript:altaEstructuraParcela('";
				acciones += parcela.getPoliza().getIdpoliza();
				acciones += "','";
				acciones += parcela.getIdparcela();
				acciones += "')\"><img src=\"jsp/img/displaytag/instalaciones.jpg\" alt=\"Alta instalacion\" title=\"Alta instalacion\" width=\"16\" height=\"16\" /></a>&nbsp;";
				
				acciones += "<a href=\"javascript:duplicateParcela('";
				acciones += parcela.getPoliza().getIdpoliza();
				acciones += "','";
				acciones += parcela.getIdparcela();
				acciones += "')\"><img src=\"jsp/img/displaytag/duplicar.png\" alt=\"Duplicar\" title=\"Duplicar\"/></a>&nbsp;";
			}
			else {
				acciones += "<img src='jsp/img/displaytag/transparente.gif' style='width:16;height:16' />&nbsp;";
				acciones += "<img src='jsp/img/displaytag/transparente.gif' style='width:16;height:16' />";
			}		
					
			acciones += "<a href=\"javascript:deleteParcela('";
			acciones += parcela.getPoliza().getIdpoliza();
			acciones += "','";
			acciones += parcela.getIdparcela();
			acciones += "','";
			
			if(parcela.getTipoparcela().equals('P'))
			    acciones += "true";
			else
			    acciones += "false";
			
			acciones += "')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar\" title=\"Eliminar\"/></a>";
		}
		
		if("E".equals(tipoParcela)){
			acciones += "<input type='hidden' name='tipoParcela' id='tipoParcela' value='E@E'/>";
		}else{
			acciones += "<input type='hidden' name='tipoParcela' id='tipoParcela' value='P@P'/>";
		}
		
		// localizacion: @@prov;;comarca;;termino;;subtermino@@
		String localizacion = "@@" + parcela.getTermino().getId().getCodprovincia() + ";;" + 
		                             parcela.getTermino().getComarca().getId().getCodcomarca() + ";;" + 
		                             parcela.getTermino().getId().getCodtermino() + ";;" + 
		                             getSubterminoParcela(parcela) + "@@";
		acciones += "<input type='hidden' name='localizacion_cm' id='localizacion_cm' value='"+ localizacion +"'/>";
		acciones += "<input type='hidden' name='idRow_cm' id='idRow_cm' value='" + parcela.getIdparcela() + "' />";
		
		return acciones;
	}

	public String getNombre() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		return StringUtils.nullToString(parcela.getNomparcela());
	}
	
	public String getHojaNum() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		return StringUtils.nullToString(parcela.getHoja()) + "-"
				+ StringUtils.nullToString(parcela.getNumero());
	}

	public String getCodtermino() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		if (parcela.getTermino() == null
				&& parcela.getTermino().getId() == null) {
			return "";
		} else {
			//DAA 09/01/13 Label del termino 
						
			Date fechaInicioContratacion = parcela.getPoliza().getLinea().getFechaInicioContratacion();
			// Utiliza el m閠odo getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
			// Esta versi髇 ahora tiene en cuenta la fecha de inicio de contrataci髇 y si la l韓ea es de ganado para determinar el nombre correcto del termino
			return "<div title='"+ parcela.getTermino().getNomTerminoByFecha(fechaInicioContratacion, false) + "'>" + 
			StringUtils.nullToString(parcela.getTermino().getId().getCodtermino()) 
			+ "</div>";
		}
	}

	public String getCodsubtermino() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		if (parcela.getTermino() == null
				&& parcela.getTermino().getId() == null) {
			return "";
		} else {
			return StringUtils.nullToString(parcela.getTermino().getId()
					.getSubtermino());
		}
	}
	
	private String getSubterminoParcela(Parcela parcela){
		if (parcela.getTermino() == null
				&& parcela.getTermino().getId() == null) {
			return "";
		} else {
			return StringUtils.nullToString(parcela.getTermino().getId().getSubtermino());
		}
	}
	
	public String getIdCat() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		if (parcela.getPoligono() != null && parcela.getParcela() != null) {
			return parcela.getPoligono() +" - "+parcela.getParcela();
		} else {
			return getSigPac(parcela);
		}
	}

	public String getSigPac(Parcela pa) {
		String sigPac = "";
		sigPac = StringUtils.nullToString(pa.getCodprovsigpac());
		sigPac += "-"+  StringUtils.nullToString(pa.getCodtermsigpac());
		sigPac += "-"+ StringUtils.nullToString(pa.getAgrsigpac());
		sigPac += "-"+ StringUtils.nullToString(pa.getZonasigpac());
		sigPac += "-"+ StringUtils.nullToString(pa.getPoligonosigpac());
		sigPac += "-"+ StringUtils.nullToString(pa.getParcelasigpac());
		sigPac += "-"+ StringUtils.nullToString(pa.getRecintosigpac());		
		return sigPac;
	}
	
	public String getCodprovincia() {
		Parcela parcela = (Parcela) getCurrentRowObject();
		if (parcela.getTermino() == null
				&& parcela.getTermino().getId() == null) {
			return "";
		} else {
			return StringUtils.nullToString(parcela.getTermino().getId().getCodprovincia());
		}
	}
	
	public String getCodcomarca(){
		Parcela parcela = (Parcela) getCurrentRowObject();
		if(parcela.getTermino().getComarca() .getId() == null){
			return "";
		}else{
			return parcela.getTermino().getComarca().getId().getCodcomarca().toString();
		}		
	}
	
	public String getTcapital(){
		String resultado="";
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales =parcela.getCapitalAsegurados();
		if(!capitales.isEmpty()){
			for(CapitalAsegurado capital:capitales){
				String descripcion = capital.getTipoCapital().getDestipocapital();
				// corto la cadena para que entre en la celda de la tabla y ocupe una sola fila
				if(descripcion.length() > 15)
					descripcion = descripcion.substring(0,14) + "."; 
				resultado += descripcion +"<br/>";
			}
		}
		
		return resultado;
	}
	
	public String getPrecio(){
		String resultado="";
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales =parcela.getCapitalAsegurados();
		for(CapitalAsegurado capital:capitales){
			Float precioMax = new Float(0);
			// recuperamos la relacion de capital-modulo para obtener el precio
			// maximo de todos los modulos seleccionados
			for(int i=0;i<capital.getCapAsegRelModulos().size();i++){
				CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) capital.getCapAsegRelModulos().toArray()[i];
				if(capAsegRelMod.getPrecio() != null) {
					if(capAsegRelMod.getPrecio().floatValue() > precioMax){
						precioMax = capAsegRelMod.getPrecio().floatValue();
					}
				} 
			}
			if(precioMax == null || precioMax.equals(new Float(0))){
				resultado="<br/>";
			}else{
				resultado+=precioMax.toString() +"<br/>";
			}
			
		}
		return resultado;
	}
	
	public String getProduccion(){
		String resultado="";
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales =parcela.getCapitalAsegurados();
		for(CapitalAsegurado capital:capitales){
			Long produccionMax = new Long(0);
			// recuperamos la relacion de capital-modulo para obtener el precio
			// maximo de todos los modulos seleccionados
			for(int i=0;i<capital.getCapAsegRelModulos().size();i++){
				CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) capital.getCapAsegRelModulos().toArray()[i];
				if(capAsegRelMod.getProduccion() != null) {
					if(capAsegRelMod.getProduccion().longValue() > produccionMax){
						produccionMax = capAsegRelMod.getProduccion().longValue();
					}
				}
			}
			if(produccionMax == null || produccionMax.equals(new Long(0))) {
				resultado+="<br/>";
			}else{
				resultado+=produccionMax.toString() +"<br/>";
			}		
		}
		return resultado;
	}
	
	public String getSuperf(){
			String resultado = "";
			Parcela parcela = (Parcela) getCurrentRowObject();
			Set<CapitalAsegurado> capitales =parcela.getCapitalAsegurados();
			
			// ------- METROS CUADRADOS/METROS LISOS -------
			if(parcela.getTipoparcela().equals('E')){
				for(CapitalAsegurado capital:capitales){
					// Busco entre los datos variables metros cuadrados y metros lineales
					String metros = "";
					// metros cuadrados --> cod.concepto:767
	                for(DatoVariableParcela datovar : capital.getDatoVariableParcelas()){
	                	String concepto = datovar.getDiccionarioDatos().getCodconcepto().toString();
						if(concepto.equals("767")){
							metros = datovar.getValor();
						}
					}
	                // metros lineales --> cod.concepto:766
					for(DatoVariableParcela datovar : capital.getDatoVariableParcelas()){
						if(datovar.getDiccionarioDatos().getCodconcepto().toString().equals("766")){
							metros = datovar.getValor();
						}
					}
	                
					if(metros.equals("")){
						resultado="<br/>";
					}else{
						resultado+=metros +"<br/>";
					}
				}
			}
			// ------- SUPERFICIE -------
			else{
				for(CapitalAsegurado capital:capitales){
					if(capital.getSuperficie() == null){
						resultado="<br/>";
					}else{
						resultado+=capital.getSuperficie() +"<br/>";
					}			
				}
			}
			
		return resultado;
	}
	
	public String getNomPar(){
		Parcela parcela = (Parcela) getCurrentRowObject();
		return StringUtils.nullToString(parcela.getNomparcela());
	}
	
	public String getNumero(){
		String resultado = "";
		String hoja = "";
		String numero = "";
		
		Parcela parcela = (Parcela) getCurrentRowObject();
		
		if(parcela.getNumero()!=null)
			numero = parcela.getNumero().toString();
		if(parcela.getHoja()!= null)
			hoja = parcela.getHoja().toString();
		
		resultado = hoja + " - " + numero;
		
		return resultado;
	}
	
	public String getFechaFin(){
		String resultado = "";
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales =parcela.getCapitalAsegurados();
		
		// ------- Fecha Fin Garantias  de Parcelas y Todas------- 
		if(parcela.getTipoparcela().equals('P')){
			for(CapitalAsegurado capital:capitales){
				// Busco entre los datos variables 
				String fecha = "";
				// Fecha fin garantia --> cod.concepto:134
				for(DatoVariableParcela datovar : capital.getDatoVariableParcelas()){
					if(datovar.getDiccionarioDatos().getCodconcepto().toString().equals("134")){
						fecha = datovar.getValor();
					}
				}
                              
				if(fecha.equals("")){
					resultado="<br/>";
				}else{
					resultado+=fecha +"<br/>";
				}
			}
		}
		
		return resultado;
	}
	
	public String getCheckCambioMasivo(){
		String result;
		String tipoParcela = "";
		Parcela parcela = (Parcela) getCurrentRowObject();
		
		if(parcela.getTipoparcela() != null)
			tipoParcela = parcela.getTipoparcela().toString();
		
		// si no es parcela(P) lo deshabilito
		if("P".equals(tipoParcela)){
    		result =  "<input type=\"checkbox\" id=\"checkParcela_" + parcela.getIdparcela() + "\"  name=\"checkParcela_" + parcela.getIdparcela() + "\" onClick =\"onClickInCheck2( \'checkParcela_" + parcela.getIdparcela()+ "')\" class=\"dato\"/>";
		}else{
			result =  "<input type=\"checkbox\" id=\"checkParcela_" + parcela.getIdparcela() + "\"  name=\"checkParcela_" + parcela.getIdparcela() + "\" onClick =\"onClickInCheck2( \'checkParcela_" + parcela.getIdparcela()+ "')\" class=\"dato\" disabled=\"false\"/>";
		}

		return result;
	}
	
	//ASF 28/08/2013 Ordenacion por defecto del displayTag. Faltaba de la mejora realizada por DAA
	public String getOrdenacion(){
		Parcela parcela = (Parcela) getCurrentRowObject();
		Set<CapitalAsegurado> capitales =parcela.getCapitalAsegurados();
		String codTCapital = "0";
		Character subtermino = ' ';
		
		String div_style = "display:none";
		String invisible = "<div style='"+ div_style +"'>";
		invisible += StringUtils.nullToString(parcela.getTermino().getId().getCodprovincia()) + "-" +
							StringUtils.nullToString(parcela.getTermino().getComarca().getId().getCodcomarca()) + "-" +
							StringUtils.nullToString(parcela.getTermino().getId().getCodtermino()) + "-";
		//Subtermino
		if(parcela.getTermino().getId().getSubtermino()!= null){
			subtermino = parcela.getTermino().getId().getSubtermino();
		}
		
		if(!Character.isDigit(subtermino)){	//si es una letra
			try {
				byte[] b = subtermino.toString().getBytes("US-ASCII");
				invisible += b[0]+ "-";
			} catch (UnsupportedEncodingException e) {
				logger.error("Excepcion : ModelTableDecoratorListaParcelas - getOrdenacion", e);
			}
		}else{
			invisible += subtermino + "-";	
		}
		
		invisible +=StringUtils.nullToString(parcela.getCodcultivo()) + "-" +
					StringUtils.nullToString(parcela.getCodvariedad()) + "-" +
					ModelTableDecoratorListaParcelasTodas.getSigPacFormateado(parcela) + "-";
		//T.Capital
		if(!capitales.isEmpty()){
			for(CapitalAsegurado capital:capitales){
				codTCapital = capital.getTipoCapital().getCodtipocapital().toString();
				break;
			}
		}								
		invisible+= codTCapital + "</div>";
		
		return invisible;
	}
	
}

