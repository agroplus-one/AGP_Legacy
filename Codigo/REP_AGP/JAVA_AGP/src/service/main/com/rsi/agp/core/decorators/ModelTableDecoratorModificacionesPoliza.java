package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;

public class ModelTableDecoratorModificacionesPoliza extends TableDecorator {

	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public String getAcciones() {
		String acciones = "";
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		//Datos del usuario conectado
		HttpServletRequest request = (HttpServletRequest) this.getPageContext().getRequest();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		//Acciones para polizas principales
		String editarPpal = "<a href=\"#\" onclick=\"javascript:editar("+ anexo.getId() +","+ anexo.getEstado().getIdestado()+")\">" +
				"<img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>&nbsp;"; 
		String editarCaducado = "<a href=\"#\" onclick=\"javascript:editarAMCuponCaducado(" 
				+ anexo.getId() + "," + anexo.getPoliza().getIdpoliza() + ",'" + anexo.getPoliza().getReferencia() 
				+ "'," + anexo.getPoliza().getLinea().getCodplan() + ")\">" +
				"<img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>&nbsp;";
		String pasarDefPpal = "<a href=\"javascript:pasarDefinitivo("+ anexo.getId()+ ")\">" +
				"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar a definitiva\" title=\"Pasar a definitiva\"/></a>&nbsp;";
		String pasarDefPpalCupon = "<a href=\"javascript:validarAMCupon('"+ ((anexo.getCupon() != null) ? anexo.getCupon().getId() : "")  +"'"+ ","+ anexo.getId()+")\">" +
				"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Confirmar el Anexo\" title=\"Confirmar el Anexo\"/></a>&nbsp;";
		String consultaPpal = "<a href=\"#\" onclick=\"javascript:ver('"+ anexo.getId() +"')\"/>" +
				"<img src=\"jsp/img/displaytag/information.png\" alt=\"Ver informacion\" title=\"Ver informacion\"/></a>&nbsp;";
		String imprimirPpalFTP ="";
		String imprimirPpalSW ="";
		
		
		
		if (anexo.getCupon() != null) {
			if (anexo.getCupon().getId() != null){
				if (anexo.getCupon().getEstadoCupon().getId().equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO)) {
					imprimirPpalFTP = "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimirSwPDFIncidencia('"+anexo.getCupon().getIdcupon()+"')\">" +
							"<img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Anexo' title='Imprimir Anexo'/></a>&nbsp;";
				}else {
					imprimirPpalFTP = "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimir("+anexo.getId()+")\">" +
							"<img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Anexo' title='Imprimir Anexo'/></a>&nbsp;";
				}
			}
		}else {
			//ANEXO DE MODIFICACION DE TIPO "FTP"
			imprimirPpalFTP = "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimir("+anexo.getId()+")\">" +
					"<img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Anexo' title='Imprimir Anexo'/></a>&nbsp;";
		}
		
		
		//ANEXO DE MODIFICACION DE TIPO "SW"
		if (anexo.getCupon() != null) {
			if (anexo.getCupon().getId() != null){
				imprimirPpalSW = "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimirSw('"+anexo.getCupon().getIdcupon()+"',"+anexo.getId()+",'"+anexo.getPoliza().getReferencia()+"')\">" +
					"<img src='jsp/img/displaytag/imprimir_poliza_modificada.png' alt='Imprimir poliza Modificada' title='Poliza Modificada'/></a>&nbsp;";
			}
		}
			//Acciones para polizas complementarias
		String editarCpl = "<a href=\"#\" onclick=\"javascript:editarCpl("+ anexo.getId() +","+ anexo.getEstado().getIdestado()+")\">" +
				"<img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>&nbsp;";
		String pasarDefCpl = "<a href=\"javascript:pasarDefinitivoCpl("+ anexo.getId()+ ")\">" +
				"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Pasar a definitiva\" title=\"Pasar a definitiva\"/></a>";
		String pasarDefCplCupon = "<a href=\"javascript:validarAMCupon('"+ ((anexo.getCupon() != null) ? anexo.getCupon().getId() :  "")  +"'"+ ","+ anexo.getId()+")\">" +
				"<img src=\"jsp/img/displaytag/accept.png\" alt=\"Confirmar el Anexo\" title=\"Confirmar el Anexo\"/></a>&nbsp;";
		String consultaCpl = "<a href=\"#\" onclick=\"javascript:verCpl('"+ anexo.getId() +"')\"/>" +
				"<img src=\"jsp/img/displaytag/information.png\" alt=\"Ver informacion\" title=\"Ver informacion\"/></a>&nbsp;";
		String imprimirCplFTP ="";
		String imprimirCplSW ="";
		//ANEXO DE MODIFICACION DE TIPO "FTP"
		imprimirCplFTP = "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimirCpl("+anexo.getId()+")\">" +
					"<img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Anexo' title='Imprimir Anexo'/></a>";
		
		//ANEXO DE MODIFICACION DE TIPO "SW"
		if (anexo.getCupon() != null) {
			if (anexo.getCupon().getId() != null){
				imprimirCplSW = "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimirCplSw('"+anexo.getCupon().getIdcupon()+"',"+anexo.getId()+",'"+anexo.getPoliza().getReferencia()+"')\">" +
					"<img src='jsp/img/displaytag/imprimir_poliza_modificada.png' alt='Imprimir poliza Modificada' title='Imprimir Poliza Modificada'/></a>";
			}
		}
		
		//Acciones comunes
		String eliminar = "<a href=\"#\" onclick=\"javascript:eliminar("+ anexo.getId() +")\">" +
				"<img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>&nbsp;";
		String verAcuse = "<a href=\"#\" onclick=\"javascript:verAcuseRecibo(" + anexo.getId() + ")\">" +
				"<img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo'/></a>&nbsp;";
		String verAcuseCupon = "<a href=\"#\" onclick=\"javascript:verAcuseConfirmacion(" + anexo.getId() +","+ anexo.getPoliza().getIdpoliza()+","+((anexo.getCupon() != null) ? anexo.getCupon().getId() : "") +");\">" +
				"<img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo'/></a>&nbsp;";
		String vacio = "<img src=\"jsp/img/displaytag/transparente.gif\" width='16' height='16'/>&nbsp;";
		
		//Lista con los estados del anexo que permiten la edicion del mismo.
		List<BigDecimal> lstEstadosAnexoEdicion = new ArrayList<BigDecimal>();
		lstEstadosAnexoEdicion.add(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
		lstEstadosAnexoEdicion.add(Constants.ANEXO_MODIF_ESTADO_ERROR);
		lstEstadosAnexoEdicion.add(Constants.ANEXO_MODIF_ESTADO_DEFINITIVO);
		
		//Lista con los estados de los cupones que permiten la ediciï¿½n del anexo
		List<Long> lstEstadosCuponEdicion = new ArrayList<Long>();
		lstEstadosCuponEdicion.add(Constants.AM_CUPON_ESTADO_ABIERTO);
		lstEstadosCuponEdicion.add(Constants.AM_CUPON_ESTADO_ERROR);
		lstEstadosCuponEdicion.add(Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO);
		lstEstadosCuponEdicion.add(Constants.AM_CUPON_ESTADO_ERROR_TRAMITE);
		
		
		// Lista de estados de am por cupon que permite ver el acuse de recibo
		List<Long> lstEstadosCuponAcuse = new ArrayList<Long>();
		lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO);
		lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_ERROR_TRAMITE);
		lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE);
		lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO);
		lstEstadosCuponAcuse.add(Constants.AM_CUPON_ESTADO_ERROR);
		
		boolean permiteEditar = false;
		boolean isAnexoCupon = false;
		boolean permiteEditarCaducado = false;
		if (anexo.getCupon() == null || anexo.getCupon().getId() == null){
			//ANEXO DE MODIFICACION DE TIPO "FTP"
			permiteEditar = anexo.getEstado() != null && lstEstadosAnexoEdicion.contains(anexo.getEstado().getIdestado());
		}
		else{
			//ANEXO DE MODIFICACION DE TIPO "SW"
			isAnexoCupon = true;
			permiteEditar = anexo.getEstado() != null && lstEstadosAnexoEdicion.contains(anexo.getEstado().getIdestado())
					&& lstEstadosCuponEdicion.contains(anexo.getCupon().getEstadoCupon().getId());
			
			permiteEditarCaducado = (anexo.getCupon().getEstadoCupon().getId().equals(Constants.AM_CUPON_ESTADO_CADUCADO));
					
		}
		
		if(anexo.getPoliza().getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
			if (permiteEditar || permiteEditarCaducado) {
				
				if (permiteEditar) {
					//EDICIï¿½N -- estados 1 (borrador), estado 3 (definitivo) y estado 4 (error)
					acciones += editarPpal + eliminar;
					
					// Si el anexo esta en estado borrador
					if(anexo.getEstado().getIdestado().equals(Constants.ANEXO_MODIF_ESTADO_BORRADOR)){
						// Pasar a definitiva anexos de principal por FTP
						if (anexo.getCupon() == null || anexo.getCupon().getId() == null){
							acciones += pasarDefPpal; 
						}
						// Confirmar anexos de principal por SW
						else {
							acciones += pasarDefPpalCupon;
						}					
				    }
					// Si no esta en borrador se comprueba si es anexo por cupon y esta en estado "Error-Tramite"
					else {
						if (anexo.getCupon() != null && anexo.getCupon().getId() != null && 
							Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(anexo.getCupon().getEstadoCupon().getId())){
							acciones += pasarDefPpalCupon;
						}
					}
				}else if (permiteEditarCaducado) {
					acciones += editarCaducado ;
				}
				
				
			}
			else{
				//VISUALIZACIï¿½N estados 2 (enviado) y 3 (enviado correcto)
				acciones = consultaPpal + vacio;
				if (usuario.getTipousuario().equals(Constants.PERFIL_0)) {
					if (anexo.getCupon() != null && anexo.getCupon().getId() != null && 
						Constants.AM_CUPON_ESTADO_CADUCADO.equals(anexo.getCupon().getEstadoCupon().getId())){
						acciones += eliminar;
					}
				}
			}
			//Modificado el 14/07/2011 durante la puesta en producciï¿½n por peticiï¿½n explicita de Judit Sï¿½nchez.
			acciones += imprimirPpalFTP;
			acciones += imprimirPpalSW;
			
		}
		else if (anexo.getPoliza().getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO)) {
			if (permiteEditar || permiteEditarCaducado) {
				acciones +=  eliminar;
				if(anexo.getEstado().getIdestado().equals(Constants.ANEXO_MODIF_ESTADO_BORRADOR)){
					// Pasar a definitiva anexos de principal por FTP
					if (anexo.getCupon() == null || anexo.getCupon().getId() == null){
						acciones += pasarDefCpl; 
					}
					// Pasar a definitiva anexos de principal por SW
					else {
						acciones += pasarDefCplCupon;
					}
			    }
				// Si no esta en borrador se comprueba si es anexo por cupon y esta en estado "Error-Tramite"
				else {
					if (anexo.getCupon() != null && anexo.getCupon().getId() != null && 
						Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(anexo.getCupon().getEstadoCupon().getId())){
						acciones += pasarDefCplCupon;
					}
				}
			}else{
				acciones = consultaCpl + vacio;
			}
			
			if(permiteEditar)
				acciones += editarCpl;
			
			if (permiteEditarCaducado) {
				acciones += editarCaducado ;
			}else {
				acciones += imprimirCplFTP;
				acciones += imprimirCplSW;
			}
		}
		
		// VISUALIZACIï¿½N DE ACUSE DE RECIBO
		// ANEXOS POR FTP
		if (!isAnexoCupon) {
			if (anexo.getEstado().getIdestado().equals(Constants.ANEXO_MODIF_ESTADO_ERROR)){
				acciones += verAcuse;
			}
		}
		// ANEXOS SW
		else {
			// Se puede visualizar el acuse de recibo si el cupon tiene estado 'Error-Rechazado', 'Error-Tramite', 'Confirmado-Tramite'
			// o 'Confirmado-Aplicado' o error
			if (lstEstadosCuponAcuse.contains(anexo.getCupon().getEstadoCupon().getId())) {
				acciones += verAcuseCupon;
			}
		}
		
		return acciones;
	}
	

	public String getAnexModifPoliza() {
		StringBuffer modificaciones = new StringBuffer("");
		boolean anadir= false;
		boolean anadirExp= false;
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		
		if (anexo.getAsunto()!=null){
			List<String> lstAsunto = Arrays.asList(anexo.getAsunto().split(";"));
			//if(hayCambiosAsegurado(anexo)) {
			if (lstAsunto.get(0).toString().equals("DOMICM")){ 	
				modificaciones.append("Asegurado, ");
			}
		}
			//}
		/* ESC-14312 ** MODIF TAM (22.06.2021) ** Inicio */
		if (!anexo.getPoliza().getLinea().isLineaGanado()) {
			
			if(anexo.getCoberturas() != null && !anexo.getCoberturas().isEmpty()) {
				
				Boolean cobModificadas = false;
				for (Cobertura cob: anexo.getCoberturas()) {
					if (cob.getTipomodificacion() != null) {
						Character Modif = cob.getTipomodificacion();
						if (Modif.equals('M')){
							cobModificadas = true;
						}
					}
				
					
				}
				if (cobModificadas) {
					modificaciones.append("Coberturas, ");
				}	
			}
		}else {
			if(anexo.getCoberturas() != null && !anexo.getCoberturas().isEmpty()) {
				/* ESC-14671 ** MODIF TAM (29.07.2021) ** Inicio */
				/* Solo en caso de que aparezcan como modificadas*/
				Boolean cobModificadas = false;
				for (Cobertura cob: anexo.getCoberturas()) {
					if (cob.getTipomodificacion() != null) {
						Character Modif = cob.getTipomodificacion();
						if (Modif.equals('M')){
							cobModificadas = true;
						}
					}
				
					
				}
				if (cobModificadas) {
					modificaciones.append("Coberturas, ");
				}	
				/* ESC-14671 ** MODIF TAM (29.07.2021) ** Fin */
			}
		}
		
		/* ESC-14671 ** MODIF TAM (29.07.2021) */
		if(anexo.getExplotacionAnexos() != null && !anexo.getExplotacionAnexos().isEmpty()) {
			for (ExplotacionAnexo explAnx : anexo.getExplotacionAnexos()) {
				if (explAnx.getTipoModificacion()!= null){
					anadirExp = true;
					break;
				}
			}
			if (anadirExp) {
				modificaciones.append("Explotaciones, ");
			}
		}
		/* ESC-14312 ** MOIF TAM (22.06.2021) ** Fin */	

		//TMR mejora 230. Añadimos parcelas, solo cuando estas tengan algun tipo de modificacion
		if(anexo.getParcelas() != null && !anexo.getParcelas().isEmpty()) {
			for (com.rsi.agp.dao.tables.anexo.Parcela p: anexo.getParcelas()){
				if (p.getTipomodificacion()!= null){
					anadir = true;
					break;
				}
			}
			if (anadir)
				modificaciones.append("Parcelas, ");
		}
		if(anexo.getSubvDeclaradas() != null && !anexo.getSubvDeclaradas().isEmpty()) {
			anadir = false;
			for (SubvDeclarada s: anexo.getSubvDeclaradas()){
				if (s.getTipomodificacion()!= null){
					anadir = true;
					break;
				}
			}
			if (anadir)
				modificaciones.append("Subvenciones, ");
		}
		if(!modificaciones.toString().equals(""))
			modificaciones.deleteCharAt(modificaciones.lastIndexOf(", "));
		
		return modificaciones.toString();
	}
	
	public String getEstadoAnexModifPoliza() {
		AnexoModificacion anexo = (AnexoModificacion) getCurrentRowObject();

		if (anexo.getEstado() == null)
			return "";
		else
			return StringUtils.nullToString(anexo.getEstado().getDescestado());
	}
	
	public String getFecEnvioAnexModifPoliza() {
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		//ANEXO DE MODIFICACION DE TIPO "FTP"
		if (anexo.getCupon() == null || anexo.getCupon().getId() == null){
			
			if (anexo.getComunicaciones() == null
					|| anexo.getComunicaciones().getFechaEnvio() == null)
				return "";
			else
				return df.format(anexo.getComunicaciones().getFechaEnvio());
		}
		//ANEXO DE MODIFICACION DE TIPO "SW"
		else{
			Date fEnvio = anexo.getCupon().getFechaEnvio();
			return (fEnvio == null ? "" : df.format(fEnvio));
		}	
	}
	
	/**
	 * Mï¿½todo para obtener el tipo de envï¿½o de un anexo y pintarlo en la pantalla de anexos de una poliza
	 * @return Cadena de texto para el tipo de envï¿½o
	 */
	public String getTipoEnvio(){
		AnexoModificacion anexo = (AnexoModificacion) getCurrentRowObject();
		//Si es envï¿½o por FTP ponemos el literal "FTP" y si no, ponemos el nï¿½mero de cupon.
		if (anexo.getTipoEnvio().equals(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP)){
			return anexo.getTipoEnvio();
		}
		else{
			return anexo.getCupon().getIdcupon();
		}
	}
	
	/**
	 * Mï¿½todo para obtener la fecha de alta de un anexo y pintarlo en la pantalla de anexos de una poliza.
	 * @return
	 */
	public String getFechaAltaAM() {
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		if (anexo.getFechaAlta() != null)
			return df.format(anexo.getFechaAlta());
		
		return "";
	}
	
	
	
	
	/**
	 * Mï¿½todo para obtener el estado de un cupon y pintarlo en la pantalla de anexos de una poliza
	 * @return
	 */
	public String getEstadoCupon() {
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		if (anexo.getCupon() != null)
			return anexo.getCupon().getEstadoCupon().getEstado();
		
		return "";
	}
	
	
	/**
	 * Mï¿½todo para obtener el importe final de la poliza de un AM con cupon en estado Confirmado-Aplicado y pintarlo en la pantalla de AM de poliza
	 * @return Importe si el AM esta en el estado indicado, vacio en cualquier otro caso
	 */
	public String getImportePlzFinal () {
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		if (mostrarImporte(anexo)) 	{
			BigDecimal importePlzFinal = anexo.getImportePlzFinal();
			return (importePlzFinal != null) ? importePlzFinal.setScale(2).toString() : "";
		}
		
		return "";
	}
	
	/**
	 * Mï¿½todo para obtener el importe de la MODIFICACION de un AM con cupon en estado Confirmado-Aplicado y pintarlo en la pantalla de AM de poliza
	 * @return Importe si el AM esta en el estado indicado, vacio en cualquier otro caso
	 */
	public String getImporteModificacion () {
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		if (mostrarImporte(anexo)) {
			BigDecimal importeModificacion = anexo.getImporteModificacion();
			return (importeModificacion != null) ? importeModificacion.setScale(2).toString() : "";
		}
		
		return "";
	}
	
	/**
	 * Devuelve un booleano indicando si el anexo actual esta asociado a un cupon en estado 'Confirmado-Aplicado' o 'Confirmado-Tramite'
	 * @return
	 */
	private boolean mostrarImporte (AnexoModificacion anexo) {
		
		if (anexo.getCupon() != null && anexo.getCupon().getEstadoCupon() != null && 
				anexo.getCupon().getEstadoCupon().getId() != null &&
				(anexo.getCupon().getEstadoCupon().getId().equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO) || 
				 anexo.getCupon().getEstadoCupon().getId().equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE))) {
			
			return true;
		}
		
		return false;
	}
	
	public String getEstRenovacionAgroseguro(){
		AnexoModificacion anexo = (AnexoModificacion)getCurrentRowObject();
		
		/*
		if (anexo.getPoliza().getEstadoAgroseguro() != null)
			return anexo.getPoliza().getEstadoAgroseguro().getDescEstado();
		*/
		if (anexo == null || anexo.getEstadoAgroseguro() == null || anexo.getEstadoAgroseguro().getDescripcion() == null)
			return "";
		
		return anexo.getEstadoAgroseguro().getDescripcion();
		
	}
}
