package com.rsi.agp.core.managers.impl; 
 
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ModuloComparator;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.dao.filters.cpl.ModuloFiltro;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.CoberturaSeleccionada;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloFilaView;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.ModuloValorCeldaView;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo; 
 
public class ModuloManager implements IManager{ 
	 
	private static final Log logger = LogFactory.getLog(ModuloManager.class); 
	 
	private CoberturasModificacionPolizaManager coberturasModificacionPolizaManager; 
	 
	private IPolizaDao polizaDao; 
	 
	/** 
	 * Metodo común que crea un tabla HTML con los datos de las coberturas de una poliza o anexo 
	 * @param moduloView 
	 * @return 
	 */ 
	public String crearCabeceraTabla(ModuloView moduloView, String idtabla, int numeroColumnas, int dimensionColumnaStr, boolean esGanado){ 
 
		String resultado = ""; 
		 
		 
		resultado = "<table id='" + idtabla + "'><tr>" + 
					"<td class='literalbordeCabecera' align='center' width='15%'>CONCEPTO PRINCIPAL DEL MODULO</td>" +  
					"<td class='literalbordeCabecera' align='center' width='15%'>RIESGO CUBIERTO</td>" + 
					"<td class='literalbordeCabecera' width='70%'>" + 
					"<table width='100%'>" + 
					"<tr><td colspan='"+numeroColumnas+"' class='literalbordeCabecera' align='center'>CONDICIONES COBERTURAS</td></tr>" + 
					"<tr>"; 
			 
		for(String  cabeceras : moduloView.getListaCabeceras()){ 
			resultado += "<td class='literalbordeCabecera' align='center' width='"+dimensionColumnaStr+"%'>" + cabeceras +"</td>"; 
		} 
		resultado +="</tr></table></td></tr>"; 
		 
		return resultado; 
	} 
	 
	/** 
	 * Metodo común que crea un tabla HTML con los datos de las coberturas de una poliza o anexo 
	 * @param moduloView 
	 * @return 
	 */ 
	public String crearCabeceraTablaAgri(ModuloView moduloView, String idtabla, int numeroColumnas, int dimensionColumnaStr, boolean esGanado, Long lineaseguroid){ 
 
		String resultado = "";
		 
		/*  Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Inicio  
		   Anadimos un nuevo combo con todos los modulos principales que permite el condicionado para la linea de la poliza  */ 
		resultado = "<table id='" + idtabla + "'>";
		
		resultado+=  "<tr>" + 
					"<td class='literalbordeCabecera' align='center' width='15%'>CONCEPTO PRINCIPAL DEL MODULO</td>" +  
					"<td class='literalbordeCabecera' align='center' width='15%'>RIESGO CUBIERTO</td>" + 
					"<td class='literalbordeCabecera' width='70%'>" + 
					"<table width='100%'>" + 
					"<tr><td colspan='"+numeroColumnas+"' class='literalbordeCabecera' align='center'>CONDICIONES COBERTURAS</td></tr>" + 
					"<tr>"; 
		 
		for(String  cabeceras : moduloView.getListaCabeceras()){ 
			resultado += "<td class='literalbordeCabecera' align='center' width='"+dimensionColumnaStr+"%'>" + cabeceras +"</td>"; 
		} 
		resultado +="</tr></table></td></tr>";
		 
		return resultado; 
	} 
	 
	/** 
	 * Metodo que crea un tabla HTML con los datos de las coberturas de una poliza 
	 * @param moduloView 
	 * @return 
	 */ 
	public String crearTablaPoliza(ModuloView moduloView, String idtabla){ 
		 
		String resultado = ""; 
		 
		//En funcion del numero de columnas a pintar hallamos los parametros de maquetacion de las celdas 
		int numeroColumnas = moduloView.getListaCabeceras().size(); 
		int dimensionColumna; 
		 
		if(numeroColumnas!=0){ 
		    dimensionColumna = 100/numeroColumnas; 
		}else{ 
			dimensionColumna = 0; 
		} 
		 
		// pintamos la cabecera		 
		resultado = crearCabeceraTabla(moduloView,idtabla,numeroColumnas,dimensionColumna, true); 
	 
		for(ModuloFilaView fila:moduloView.getListaFilas()){ 
			resultado += "<tr>" + 
						"<td class='literalborde' align='center' width='15%'>" + fila.getConceptoPrincipalModulo() + "</td>" + 
						"<td class='literalborde' align='center' width='15%'>" + fila.getRiesgoCubierto() + "</td>" + 
						"<td class='literalborde' align='center' width='70%'>" + 
						"<table width='100%'>" +  
						"<tr>"; 
			 
			//Recorremos las celdas 
			for (ModuloCeldaView mcv : fila.getCeldas()){ 
				int contLineas = 0; 
				resultado += "<td class='literalborde' align='center' width='"+dimensionColumna+"%'>"; 
				if(mcv.isElegible()){ 
					resultado +="<font color='red'>ELEGIBLE</font><br/>"; 
				} 
				for(ModuloValorCeldaView valor : mcv.getValores()){ 
					if(valor.isTachar()){ 
						resultado +="<strike>" + valor.getDescripcion() +"</strike>"; 
					}else{ 
						resultado += valor.getDescripcion(); 
					} 
					if (contLineas < mcv.getValores().size() - 1){ 
						resultado += "<br/>"; 
					} 
					contLineas ++; 
				} 
				if(mcv.getObservaciones()!= null){ 
					resultado += "<br/>" + mcv.getObservaciones(); 
				} 
				resultado+="&nbsp;</td>"; 
			} 
			 
			resultado+="</tr></table></td>";							 
		} 
		resultado+="</tr>" + "</table>"; 
		 
		return resultado; 
	} 
 
	/** 
	 * Método para obtener los módulos y sus coberturas para mostrarlas en la pantalla de elección de módulos 
	 * @param idpoliza Póliza 
	 * @param lineaseguroid 
	 * @param tipoReferencia 
	 * @param codmodulo 
	 * @param isPoliza => true si es póliza, false en caso contrario 
	 * @param informes 
	 * @return Listado de Módulos 
	 */ 
	@SuppressWarnings("unchecked") 
	public List<Modulo> dameModulosDisponibles(Long idpoliza, Long lineaseguroid,   
			Character tipoReferencia, String codmodulo, boolean isPoliza, boolean informes) { 
		 
		logger.debug("Filtro de modulos: lineaseguroid=" + lineaseguroid + ", tipoReferencia=" + tipoReferencia); 
		 
		ModuloFiltro filtro = new ModuloFiltro(lineaseguroid, tipoReferencia); 
		 
		if (informes && isPoliza && !StringUtils.nullToString(tipoReferencia).equals(Constants.MODULO_POLIZA_COMPLEMENTARIO+"")){ 
			List<String> lstModulosElegidos = polizaDao.getLstModulosElegidos(idpoliza); 
			filtro.setLstModulosClase(lstModulosElegidos); 
		} 
		else{ 
			ModuloId moduloId = new ModuloId(lineaseguroid, codmodulo); 
			filtro.setModuloId(moduloId); 
		} 
		 
		List<Modulo> modulos = polizaDao.getObjects(filtro); 
		 
		Collections.sort(modulos, new ModuloComparator()); 
		return modulos; 
	} 
	 
	 
	/** 
	 * Obtiene el objeto Modulo asociado al c?digo y plan/l?nea indicados como par?metro 
	 * @param codModulo 
	 * @param lineaseguroid 
	 * @return 
	 */ 
	@SuppressWarnings("unchecked") 
	public Modulo getModulo (String codModulo, Long lineaseguroid) { 
		 
		ModuloFiltro filtro = new ModuloFiltro(lineaseguroid, Constants.MODULO_POLIZA_PRINCIPAL); 
		 
		ModuloId moduloId = new ModuloId(lineaseguroid, codModulo); 
		filtro.setModuloId(moduloId); 
		 
		List<Modulo> modulos = polizaDao.getObjects(filtro); 
		 
		if (modulos != null && !modulos.isEmpty()) return modulos.get(0); 
		 
		return null; 
	} 
	 
	 
	/** 
	 * Tabla de coberturas para el nuevo modulo seleccionado 
	 * @param moduloView 
	 * @param object  
	 * @return 
	 * @throws JSONException  
	 * @throws   
	 */ 
	public String crearTablaModuloAnexo(ModuloView moduloView, List<CoberturaSeleccionada> lstCob, 
			List<VinculacionValoresModulo> lstVincValMod,boolean activarCombo, boolean esGanado, Long lineaseguroid, AnexoModificacion anexoM) throws JSONException{ 
		StringBuilder resultado = new StringBuilder(); 
		String activarComboStr =""; 
		StringBuilder scriptSb = new StringBuilder(); 
		String funcion = ""; 
		StringBuilder cobElegiblesPoliza = new StringBuilder(); 
		StringBuilder cobElegiblesAnexo = new StringBuilder(); 
		String codvalor = "-2"; 
		if (activarCombo){			 
			funcion = coberturasModificacionPolizaManager.creaFuncionesJSCombos(lstVincValMod,moduloView); 
			scriptSb.append("<script>").append(funcion).append("</script>"); 
		}else{ 
			activarComboStr="disabled='disabled'"; 
		} 
		 
		//En funcion del numero de columnas a pintar hallamos los parametros de maquetacion de las celdas 
		int numeroColumnas = moduloView.getListaCabeceras().size(); 
		int dimensionColumna = numeroColumnas != 0 ? 100/numeroColumnas : 0; 
		 
		/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Inicio */ 
		if (esGanado) { 
			resultado.append(crearCabeceraTabla(moduloView, "tablaPpal", numeroColumnas, dimensionColumna, esGanado)); 
		}else { 
			resultado.append(crearCabeceraTablaAgri(moduloView, "tablaPpal", numeroColumnas, dimensionColumna, esGanado, lineaseguroid)); 
		} 
		 
		for(ModuloFilaView fila:moduloView.getListaFilas()){ 
			resultado.append("<tr> <td class='literalborde' align='center' width='15%'>") 
				.append(fila.getConceptoPrincipalModulo()).append("</td> <td class='literalborde' align='center' width='15%'>"); 
			/*ESC-13514 DNF 23/04/2021 ***/
			//if(fila.getRiesgoCubierto().indexOf("ELEGIBLE") != -1){ 
			if(fila.isRcElegible() == true) {
			/* FIN ESC-13514 DNF 23/04/2021 ***/					
				resultado.append(fila.getRiesgoCubierto() + "<br/><input type='checkbox' " + activarComboStr); 
				
				
				if(activarCombo){ //anexo
					
					if(moduloView.getCodModulo().equals(anexoM.getCodmodulo())) { //si no se cambia el modulo 
						for(Cobertura coberturaAnexo : anexoM.getCoberturas()) { 
							 
							if ((fila.getCodConceptoPrincipalModulo() !=null &&   
									fila.getCodConceptoPrincipalModulo().equals(coberturaAnexo.getCodconceptoppalmod())) &&  
									(fila.getCodRiesgoCubierto( )!=null && fila.getCodRiesgoCubierto().equals(coberturaAnexo.getCodriesgocubierto()))   
									) {  
								// Si el concepto no es 363 ni 106 se marca el check  
								if(coberturaAnexo.getCodconcepto().equals(new BigDecimal(363))) {  
									 
									//anexo 
									if("-1".equals(coberturaAnexo.getCodvalor()) ) { 
										resultado.append("checked='checked'");  
										codvalor = "-1";  
										resultado.append(" value='1'"); 
									}else { 
										codvalor = "-2";  
										resultado.append(" value='0'");		 
									}		 
								} 
							}	 
						} 
					}else { //se cambia el modulo 
						for(Cobertura coberturaAnexo : anexoM.getCoberturas()) { 
							 
							if(coberturaAnexo.getCodconcepto().equals(new BigDecimal(363))) {  
								codvalor = "-2";  
								resultado.append(" value='0'");			 
							} 
								 
						} 
					} 	 
					
				}else {
					for(CoberturaSeleccionada cob:lstCob){ 
						// Si la cobertura coincide con la de la fila actual 
						if ((fila.getCodConceptoPrincipalModulo() !=null &&  
								fila.getCodConceptoPrincipalModulo().equals(cob.getCodconceptoppalmod())) && 
								(fila.getCodRiesgoCubierto( )!=null && fila.getCodRiesgoCubierto().equals(cob.getCodriesgocubierto()))  
								) { 
							// Si el concepto no es 363 ni 106 se marca el check 
							if(cob.getCodconcepto().equals(new BigDecimal(363))) { 
								
								//poliza
								if("-1".equals(cob.getCodvalor()) || "S".equals(cob.getCodvalor()) ) {
									resultado.append("checked='checked'"); 
									codvalor = "-1"; 
									resultado.append(" value='1'");
								}else {
									codvalor = "-2"; 
									resultado.append(" value='0'");
								}
								
							}
						} 
					}
				}
				
				
				
				
				
				
				
				
				
				
				
				
				if(activarCombo){ 
					resultado.append(" id='checkAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()).append("_") 
						.append(fila.getCodRiesgoCubierto().toString()).append("_363' name='checkAnexo_") 
						.append(fila.getCodConceptoPrincipalModulo().toString()).append("_").append(fila.getCodRiesgoCubierto().toString()) 
						.append("_363' onclick='cambiarValorCombo(this.id)'>"); 
					resultado.append("<input type='hidden' value='0' name='checkAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
						.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_363'>"); 
					//ASF 25/07/2013 A?ado a la mejora desarrollada por David los elegibles tipo "check" 
					cobElegiblesAnexo.append(fila.getCodConceptoPrincipalModulo().toString()).append("_").append(fila.getCodRiesgoCubierto().toString()).append("_363#").append(codvalor).append(";"); 
					//cobElegiblesAnexo += fila.getCodConceptoPrincipalModulo().toString() + "_" + fila.getCodRiesgoCubierto().toString()+"_363#"+codvalor+";"; 
					
					/*** 01/20/2021 DNF PET.63485.FIII AÑADO EL CAMPO OCULTO DEL RC ELEGIBLE PARA LA VALIDACION*/
					resultado.append("<label id='campoObligatoriocheckAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
					.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_363'") 
					.append(" name='campoObligatoriocheckAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
					.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_363'") 
					.append(" class='campoObligatorio' title='Campo obligatorio' > *</label>");
					/*** FIN 01/20/2021 DNF PET.63485.FIII */
					
				} else { 
					resultado.append(" id='checkPoliza_").append(fila.getCodConceptoPrincipalModulo().toString()) 
						.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_363' name='checkPoliza_") 
						.append(fila.getCodConceptoPrincipalModulo().toString()).append("_") 
						.append(fila.getCodRiesgoCubierto().toString()).append("_363'>"); 
					//ASF 25/07/2013 A?ado a la mejora desarrollada por David los elegibles tipo "check" 
					cobElegiblesPoliza.append(fila.getCodConceptoPrincipalModulo().toString()).append("_").append(fila.getCodRiesgoCubierto().toString()).append("_363#").append(codvalor).append(";"); 
				} 
				codvalor = "-2"; 	
			} else { 
				resultado.append(fila.getRiesgoCubierto()); 
			} 
			resultado.append("</td> <td class='literalborde' align='center' width='70%'> <table width='100%'> <tr>"); 
				
			for (ModuloCeldaView mcv : fila.getCeldas()){ 
				resultado.append("<td class='literalborde' align='center' width='").append(dimensionColumna).append("%'>"); 
				if(mcv.isElegible()){ 
					resultado.append("<font color='red'>ELEGIBLE</font><br/>"); 
					if(activarCombo){							 
						resultado.append("<select ").append(activarComboStr).append(" onChange='cambiarComboAJAX(\"selectAnexo_") 
						//resultado.append("<select ").append(activarComboStr).append(" onChange='cambiarComboAJAX(").append(moduloView).append(",").append("\"selectAnexo_")
							.append(fila.getCodConceptoPrincipalModulo().toString()).append("_") 
							.append(fila.getCodRiesgoCubierto().toString()).append("_").append(mcv.getCodconcepto()) 
							//.append("\");' class='dato' id='selectAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
							.append("\"").append(" , ").append("this.value").append(");' class='dato' id='selectAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
							.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_").append(mcv.getCodconcepto()) 
							//.append("' name='comboElegible'>");
							.append("' name='selectAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
							.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_") 
							.append(mcv.getCodconcepto()).append("'>"); 
						//DAA 05/07/2013 String para montar array de coberturas elegibles del anexo 
						cobElegiblesAnexo.append(fila.getCodConceptoPrincipalModulo().toString()).append("_").append(fila.getCodRiesgoCubierto().toString()).append("_").append(mcv.getCodconcepto()).append("#"); 
						//cobElegiblesAnexo += fila.getCodConceptoPrincipalModulo().toString() + "_" + fila.getCodRiesgoCubierto().toString()+"_" + mcv.getCodconcepto() + "#"; 
					} else { 
						resultado.append("<select ").append(activarComboStr).append(" class='dato'id='selectPoliza_") 
							.append(fila.getCodConceptoPrincipalModulo().toString()).append("_") 
							.append(fila.getCodRiesgoCubierto().toString()).append("_").append(mcv.getCodconcepto()) 
							.append("' name='selectPoliza_").append(fila.getCodConceptoPrincipalModulo().toString()) 
							.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_").append(mcv.getCodconcepto()).append("'>"); 
						//DAA 05/07/2013 String para montar array de coberturas elegibles del apoliza 
						cobElegiblesPoliza.append(fila.getCodConceptoPrincipalModulo().toString()).append("_").append(fila.getCodRiesgoCubierto().toString()).append("_").append(mcv.getCodconcepto()).append("#");  
					} 
					
					for(ModuloValorCeldaView celdaF:mcv.getValores()){ 
			
						if(!celdaF.isTachar()){ 
							
							resultado.append("<option value='").append(celdaF.getCodigo()).append("' "); 
							for(CoberturaSeleccionada cob:lstCob){ 
								if(!cob.getCodconcepto().equals(new BigDecimal(363)) && !cob.getCodconcepto().equals(new BigDecimal(106))){ 
									//DAA 05/07/2013 
									if(cob.getCodconcepto().equals(mcv.getCodconcepto()) /* && cob.getCodvalor().equals(celdaF.getCodigo()) */
											&& cob.getCodriesgocubierto().compareTo(fila.getCodRiesgoCubierto())==0 ){ 
										
										
										if(cob.getCodvalor().equals(celdaF.getCodigo()) && fila.isRcElegible() == false) {
										
											resultado.append("selected"); 
											if(cobElegiblesPoliza.length() > 0){ 
												cobElegiblesPoliza.append(celdaF.getCodigo()).append(";"); 
											}else{ 
												cobElegiblesAnexo.append(celdaF.getCodigo()).append(";"); 
											}
										} 
										
										if(fila.isRcElegible() == true) {
											
											for(ModuloFilaView filas: moduloView.getListaFilas()){
												
												if (filas.getFilamodulo() != null && celdaF.getFilaVinculada() != null
														&& filas.getFilamodulo()
																.compareTo(celdaF.getFilaVinculada()) == 0) {
													
													for (ModuloCeldaView moduloCV : filas.getCeldas()){
														
														if(moduloCV.getColumna().compareTo(celdaF.getColumnaVinculada()) == 0) {
															
															for(ModuloValorCeldaView mvcv : moduloCV.getValores()) {
														
																if(mvcv.getDescripcion().equals(celdaF.getDescripcion()) //&& mvcv.getCodigo().equals(cob.getCodvalor())		
																		) {
																	 
																	if(cobElegiblesPoliza.length() > 0){ 
																		resultado.append("selected");
																		cobElegiblesPoliza.append(celdaF.getCodigo()).append(";"); 
																	}else{ 
																		
																		
																		for(Cobertura cobertura : anexoM.getCoberturas()) {
																			
																			if ((cobertura
																					.getCodconceptoppalmod() != null
																					&& filas.getCodConceptoPrincipalModulo() != null
																					&& cobertura.getCodconceptoppalmod()
																							.compareTo(filas
																									.getCodConceptoPrincipalModulo()) == 0)
																					&& (cobertura
																							.getCodriesgocubierto() != null
																							&& filas.getCodRiesgoCubierto() != null
																							&& cobertura
																									.getCodriesgocubierto()
																									.compareTo(filas
																											.getCodRiesgoCubierto()) == 0)
																					&& (cobertura
																							.getCodconcepto() != null
																							&& moduloCV
																									.getCodconcepto() != null
																							&& cobertura
																									.getCodconcepto()
																									.compareTo(moduloCV
																											.getCodconcepto()) == 0)
																					&& cobertura.getCodvalor()
																							.equals(mvcv.getCodigo())) {
																				resultado.append("selected");
																				cobElegiblesAnexo.append(cobertura.getCodvalor()).append(";");
																			}																			
																		}
																	}
																}	
															}
														}			
													}
												}	
											}
											
											
										}
									} 
								} 
							} 
							resultado.append(">").append(celdaF.getDescripcion()).append("</option>"); 
						}
	
					} 
					
					
					/*** PET.63485FIII DNF 28/01/2021 en caso de combo de anexo elegible le añado campoobligatorio para validacion*/
					//resultado.append("</select>");
					if(activarCombo) {
						resultado.append("</select>")
						.append("<label id='campoObligatorioselectAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
						.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_") 
						.append(mcv.getCodconcepto()).append("' name='campoObligatorioselectAnexo_").append(fila.getCodConceptoPrincipalModulo().toString()) 
						.append("_").append(fila.getCodRiesgoCubierto().toString()).append("_") 
						.append(mcv.getCodconcepto()).append("' class='campoObligatorio' title='Campo obligatorio' > *</label>");
					}else {
						resultado.append("</select>");
					}
					/*** fin PET.63485FIII DNF 28/01/2021 */
				} else { 
					for(ModuloValorCeldaView valor : mcv.getValores()){ 
						if(valor.isTachar()){ 
							resultado.append("<strike>").append(valor.getDescripcion()).append("</strike>"); 
						}else{ 
							resultado.append(valor.getDescripcion()); 
						} 
					} 
				} 
				if(mcv.getObservaciones()!= null){ 
					resultado.append("<br/>").append(mcv.getObservaciones()); 
				} 
				resultado.append("&nbsp;</td>"); 
			} 
			resultado.append("</tr></table></td>"); 
		} 
		resultado.append("</tr> </table>"); 
		//DAA 05/07/2013 Guardamos un array con las coberturas elegibles de la poliza 
		resultado.append(scriptSb.toString()); 
		resultado.append("<script language='javascript'>montarArrayCoberturasElegibles('").append(cobElegiblesPoliza.toString()).append("','").append(cobElegiblesAnexo.toString()).append("');</script>"); 
		return resultado.toString(); 
	} 
	 
	public void setCoberturasModificacionPolizaManager( 
			CoberturasModificacionPolizaManager coberturasModificacionPolizaManager) { 
		this.coberturasModificacionPolizaManager = coberturasModificacionPolizaManager; 
	} 
 
	public void setPolizaDao(IPolizaDao polizaDao) { 
		this.polizaDao = polizaDao; 
	} 
	 
} 
