package com.rsi.agp.core.managers.impl.anexoMod.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;

import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.CoberturaSeleccionada;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.seguroAgrario.contratacion.Asegurado;
import es.agroseguro.seguroAgrario.contratacion.ObjetosAsegurados;

public class AnexoModificacionUtils {
	
	private static final Log logger = LogFactory.getLog(AnexoModificacionUtils.class);
	
	/**
	 * Metodo para obtener el objeto necesario para al impresion del borrador de un anexo de modificacion de una poliza principal
	 * @param anexo Anexo de modificacion a imprimir
	 * @param poliza poliza asociada al anexo de modificacion
	 * @return Objeto PolizaActualizadaResponse con los datos necesarios para imprimir el borrador
	 */
	public static PolizaActualizadaResponse anexoPpalToPolizaActualizadaResponse(AnexoModificacion anexo, Poliza poliza){
		PolizaActualizadaResponse resultado = new PolizaActualizadaResponse();
		
		//Rellenamos los datos de la poliza principal
		es.agroseguro.contratacion.PolizaDocument polizaPpal = es.agroseguro.contratacion.PolizaDocument.Factory.newInstance();
		
		es.agroseguro.contratacion.Poliza polizaAux = es.agroseguro.contratacion.Poliza.Factory.newInstance();
		
		//Datos del asegurado
		Asegurado asegurado = getAsegurado(poliza.getAsegurado());
		polizaAux.setAsegurado((es.agroseguro.contratacion.Asegurado) asegurado);
		polizaAux.setAsegurado((es.agroseguro.contratacion.Asegurado) asegurado);
		
		//Datos de parcelas ??
		polizaAux.setObjetosAsegurados((es.agroseguro.contratacion.ObjetosAsegurados) getParcelas(anexo));
		
		polizaPpal.setPoliza(polizaAux);
		
		resultado.setPolizaGanado(polizaPpal);
		
		
		return resultado;
	}

	private static ObjetosAsegurados getParcelas(AnexoModificacion anexo) {
		ObjetosAsegurados parcelas = ObjetosAsegurados.Factory.newInstance();
		
		return parcelas;
	}

	/**
	 * Metodo para obtener un objeto Asegurado de Agroseguro a partir de los datos del asegurado de una poliza de bbdd.
	 * @param asegurado Asegurado de la poliza
	 * @return Asegurado para enviar a Agroseguro.
	 */
	private static Asegurado getAsegurado(com.rsi.agp.dao.tables.admin.Asegurado asegurado) {
		Asegurado aseguradoDef = Asegurado.Factory.newInstance();
		aseguradoDef.setNif(asegurado.getNifcif());
		if (asegurado.getTipoidentificacion().equals(Constants.TIPO_IDENTIFICACION_CIF)){
			RazonSocial razonSocial = RazonSocial.Factory.newInstance();
			razonSocial.setRazonSocial(asegurado.getRazonsocial());
			aseguradoDef.setRazonSocial(razonSocial);
		}
		else{
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(asegurado.getNombre());
			nom.setApellido1(asegurado.getApellido1());
			nom.setApellido2(asegurado.getApellido2());
			aseguradoDef.setNombreApellidos(nom);
		}
		return aseguradoDef;
	}
	
	/**
	 * Metodo para obtener una lista de parcelas de anexo a partir de los datos de la situacion actualizada de agroseguro.
	 * @param poliza Poliza actualizada de Agroseguro
	 * @return Listado de parcelas para un anexo
	 * @throws XmlException 
	 */
	public static List<Parcela> getParcelasAnexoFromPolizaActualizada(es.agroseguro.contratacion.Poliza poliza, 
			Long idAnexo, Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta) throws XmlException{
		
		
		/* TAM (12.06.2020 */
		List<Parcela> parcelasAnexo = new ArrayList<Parcela>(); 
		AnexoModificacion anexo = new AnexoModificacion(idAnexo);

		//Recorremos las explotaciones de la situacion actualizada y vamos creando las explotaciones del anexo
		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
		
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				
				es.agroseguro.contratacion.parcela.ParcelaDocument  xmlParcela = null;
				xmlParcela = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode);

				if (xmlParcela != null){
					Parcela parcela = buildParcelaAnexo(dvCodConceptoEtiqueta, anexo, xmlParcela, poliza.getCobertura().getDatosVariables());
	
					//anhado la parcela a la lista
					parcelasAnexo.add(parcela);
				}
			}
			currNode = currNode.getNextSibling();
		}
		
		return parcelasAnexo;
	}
	
	/**
	 * Metodo para obtener los datos de una parcela de la situacion actualizada de Agroseguro
	 * @param poliza Situacion actualizada de agroseguro
	 * @param idAnexo Identificador del anexo de modificacion
	 * @param dvCodConceptoEtiqueta Datos variables que aplican para la línea del anexo
	 * @param hoja Hoja de la parcela
	 * @param numero Numero de la parcela
	 * @return Parcela del anexo con los datos de la situacion actualizada
	 * @throws XmlException 
	 */
	public static Parcela getParcelaAnexoFromPolizaActualizada(es.agroseguro.contratacion.Poliza poliza, 
			Long idAnexo, Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta, BigDecimal hoja, BigDecimal numero) throws XmlException{
		
		logger.debug("Entramos en getParcelaAnexoFromPolizaAnexoActualizada");
		
		/* TAM (12.06.2020 */
		AnexoModificacion anexo = new AnexoModificacion(idAnexo);

		//Recorremos las explotaciones de la situacion actualizada y vamos creando las explotaciones del anexo
		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
		
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				
				es.agroseguro.contratacion.parcela.ParcelaDocument xmlParcela = null;
				xmlParcela = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode);

				if (xmlParcela != null){
					
					if (hoja.equals(new BigDecimal(xmlParcela.getParcela().getHoja())) && numero.equals(new BigDecimal(xmlParcela.getParcela().getNumero()))){
						Parcela parcela = buildParcelaAnexo(dvCodConceptoEtiqueta, anexo, xmlParcela,poliza.getCobertura().getDatosVariables());
						return parcela;
					}
				}
			}
			currNode = currNode.getNextSibling();
		}
		
		return null;
		/* TAM FIN (12.06.2020) */
		
	}
	
	/**
	 * Metodo para obtener una lista de parcelas de anexo complementario a partir de los datos de la situacion actualizada de agroseguro.
	 * @param poliza Poliza actualizada Complementaria de Agroseguro
	 * @return Listado de parcelas para un anexo
	 */
	public static List<Parcela> getParcelasAnexoCplFromPolizaActualizada(es.agroseguro.contratacion.Poliza poliza, Long idAnexo) throws XmlException{
		
		/* Pet. 57626 ** MODIF TAM (18.06.2020) ** Inicio */
		/* Por el desarrollo de esta peticion tanto polizas Agricolas (Principales y Complementarias) como de Ganado van por formato Unificado */
		logger.debug("Entramos en getParcelaAnexoCplFromPolizaAnexoActualizada");
		
		List<Parcela> parcelasAnexo = new ArrayList<Parcela>(); 
		AnexoModificacion anexo = new AnexoModificacion(idAnexo);

		//Recorremos las explotaciones de la situacion actualizada y vamos creando las explotaciones del anexo
		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
		
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				
				es.agroseguro.contratacion.parcela.ParcelaDocument par = null;
				par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode);

				if (par != null){
					
						Parcela parcela = buildParcelaAnexoCpl(anexo, par);
						//return parcela;
						parcelasAnexo.add(parcela);
				}
			}
			currNode = currNode.getNextSibling();
		}
		
		return parcelasAnexo;
	}

	/**
	 * Metodo para obtener un objeto parcela de anexo a partir de los datos de la parcela en la situacion actualizada 
	 * @param dvCodConceptoEtiqueta Datos variables que aplican para esta línea
	 * @param anexo Anexo asociado a la parcela
	 * @param par Parcela de la situacion actualizada
	 * @param datosVariables 
	 * @return Parcela del anexo con los datos de la situacion actualizada
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Parcela buildParcelaAnexo(Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			AnexoModificacion anexo, es.agroseguro.contratacion.parcela.ParcelaDocument xmlParcelaAnexo, 
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariablesCob) {
		
		Parcela parcela = new Parcela();
		
		if (xmlParcelaAnexo != null && xmlParcelaAnexo.getParcela() != null) {
			
			es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela par = xmlParcelaAnexo.getParcela();
			
			
			parcela.setAnexoModificacion(anexo);
			parcela.setParcela(null);
		
			parcela.setHoja(new BigDecimal(par.getHoja()));
			parcela.setNumero(new BigDecimal(par.getNumero()));
			parcela.setNomparcela(par.getNombre());
			parcela.setCodprovincia(new BigDecimal(par.getUbicacion().getProvincia()));
			parcela.setCodcomarca(new BigDecimal(par.getUbicacion().getComarca()));
			parcela.setCodtermino(new BigDecimal(par.getUbicacion().getTermino()));
			if (!StringUtils.nullToString(par.getUbicacion().getSubtermino()).equals("")){
				parcela.setSubtermino(par.getUbicacion().getSubtermino().charAt(0));
			}
			else{
				parcela.setSubtermino(' ');
			}
			parcela.setCodcultivo(new BigDecimal(par.getCosecha().getCultivo()));
			parcela.setCodvariedad(new BigDecimal(par.getCosecha().getVariedad()));
			parcela.setCodprovsigpac(new BigDecimal(par.getSIGPAC().getProvincia()));
			parcela.setCodtermsigpac(new BigDecimal(par.getSIGPAC().getTermino()));
			parcela.setAgrsigpac(new BigDecimal(par.getSIGPAC().getAgregado()));
			parcela.setZonasigpac(new BigDecimal(par.getSIGPAC().getZona()));
			parcela.setPoligonosigpac(new BigDecimal(par.getSIGPAC().getPoligono()));
			parcela.setParcelasigpac(new BigDecimal(par.getSIGPAC().getParcela()));
			parcela.setRecintosigpac(new BigDecimal(par.getSIGPAC().getRecinto()));
		
			parcela.setTipoparcela(Constants.TIPO_PARCELA_PARCELA); // Ponemos todo como parcela y luego se recalculan las instalaciones
			parcela.setAltaencomplementario('N');
		
			//Rellenamos los capitales asegurados
			
			for (es.agroseguro.contratacion.parcela.CapitalAsegurado ca : par.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray()){
				
				com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado = new com.rsi.agp.dao.tables.anexo.CapitalAsegurado();
				capitalAsegurado.setParcela(parcela);
			
				capitalAsegurado.setTipoCapital(new TipoCapital(new BigDecimal(ca.getTipo()), null));
				capitalAsegurado.setSuperficie(ca.getSuperficie());
				capitalAsegurado.setPrecio(ca.getPrecio());
				capitalAsegurado.setProduccion(new BigDecimal(ca.getProduccion()));
				
				//Rellenamos los datos variables
				// 1. Recorrer las claves de dvCodConceptoEtiqueta
				for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()){
					try {
						
						if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA).equals(codconcepto)
								|| BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE).equals(codconcepto)
								|| BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO).equals(codconcepto)) {
							
							if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA).equals(codconcepto)) {
								/* ESC-11548 ** MODIF TAM (30.11.2020) ** Inicio */
								/* Incluimos una validacion, por si no existen Datos variables */
								if (ca.getDatosVariables() != null) {
									for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia franq : ca.getDatosVariables().getFranqArray()) {
										if (franq.getValor() > 0) {
											String valor = String.valueOf(franq.getValor());
											
											CapitalDTSVariable dv = new CapitalDTSVariable();
											dv.setCapitalAsegurado(capitalAsegurado);
											dv.setValor(StringUtils.nullToString(valor));
											dv.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA));
											dv.setCodconceptoppalmod(Long.valueOf(franq.getCPMod()));
											dv.setCodriesgocubierto(Long.valueOf(franq.getCodRCub()));
											capitalAsegurado.getCapitalDTSVariables().add(dv);
										}
									}	
								}	
							} else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE).equals(codconcepto)) {
								/* ESC-11548 ** MODIF TAM (30.11.2020) ** Inicio */
								/* Incluimos una validacion, por si no existen Datos variables */
								if (ca.getDatosVariables() != null) {
									for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable minIndem : ca.getDatosVariables().getMinIndemArray()) {
										if (minIndem.getValor() > 0) {
											String valor = String.valueOf(minIndem.getValor());
											CapitalDTSVariable dv = new CapitalDTSVariable();
											dv.setCapitalAsegurado(capitalAsegurado);
											dv.setValor(StringUtils.nullToString(valor));
											dv.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE));
											dv.setCodconceptoppalmod(Long.valueOf(minIndem.getCPMod()));
											dv.setCodriesgocubierto(Long.valueOf(minIndem.getCodRCub()));
											capitalAsegurado.getCapitalDTSVariables().add(dv);
										}
									}
								}	
							} else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO).equals(codconcepto)) {
								/* ESC-11548 ** MODIF TAM (30.11.2020) ** Inicio */
								/* Incluimos una validacion, por si no existen Datos variables */
								if (ca.getDatosVariables() != null) {
									for (es.agroseguro.contratacion.datosVariables.Garantizado garant : ca.getDatosVariables().getGarantArray()) {
										if (garant.getValor() > 0) {
											String valor = String.valueOf(garant.getValor());
											CapitalDTSVariable dv = new CapitalDTSVariable();
											dv.setCapitalAsegurado(capitalAsegurado);
											dv.setValor(StringUtils.nullToString(valor));
											dv.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO));
											dv.setCodconceptoppalmod(Long.valueOf(garant.getCPMod()));
											dv.setCodriesgocubierto(Long.valueOf(garant.getCodRCub()));
											capitalAsegurado.getCapitalDTSVariables().add(dv);
										}
									}
								}	
							}
						}else {
							
							/* ESC-11548 ** MODIF TAM (30.11.2020) ** Inicio */
							/* Incluimos una validacion, por si no existen Datos variables */
							if (ca.getDatosVariables() != null) {
								
								// 2. Buscar en los datos variables del capital asegurado el valor correspondiente
						
								//primero obtengo el objeto que representa al dato variable
								Class clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
								
								Method method = clase.getMethod("get" + dvCodConceptoEtiqueta.get(codconcepto).getEtiqueta());
								
								Object objeto = method.invoke(ca.getDatosVariables());
								
								if (objeto != null){
									capitalAsegurado = setDatoVariableToPArcela (objeto,capitalAsegurado,codconcepto);
								}
								// compruebo si esta en datos variables de coberturas
								if (datosVariablesCob != null) {
									Object objeto2 = method.invoke(datosVariablesCob);
									if (objeto2 != null){
										capitalAsegurado = setDatoVariableToPArcela (objeto2,capitalAsegurado,codconcepto);
									}
								}
							}	
						}	
				
					} catch (SecurityException e) {
						logger.debug("Error de seguridad " + e.getMessage());
					} catch (NoSuchMethodException e) {
						logger.debug("El metodo no existe para esta clase " + e.getMessage());
					} catch (IllegalArgumentException e) {
						logger.debug("El metodo acepta los argumentos " + e.getMessage());
					} catch (IllegalAccessException e) {
						logger.debug("Error " + e.getMessage());
					} catch (InvocationTargetException e) {
						logger.debug("Error " + e.getMessage());
					}
				}/* Fin del for de codconcepto */
			
			
				// Recorremos el array de fechas fin garantia, y si tiene anhadimos el valor
				// a la lista como otro dato variable mas
				/* ESC-11548 ** MODIF TAM (30.11.2020) ** Inicio */
				/* Incluimos una validacion, por si no existen Datos variables */
				if (ca.getDatosVariables() != null) {
					for (es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias ffg : ca.getDatosVariables().getFecFGarantArray()) {
						
						if (ffg.getValor()!= null) {
							CapitalDTSVariable dv = null;
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
							Date d = new Date();
							String fecha="";
							try {
								d = sdf.parse(ffg.getValor().toString());
								fecha = sdf2.format(d);
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha en los datos variables");
							}
							dv = new CapitalDTSVariable();
							dv.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FEC_FIN_GARANT));
							dv.setValor(fecha);
							dv.setCapitalAsegurado(capitalAsegurado);
							capitalAsegurado.getCapitalDTSVariables().add(dv);
							break;
						}
					} /* Fin del for de fechas*/
				
					// MPM - 0614
					for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : ca.getDatosVariables().getRiesgCbtoElegArray()) {
						CapitalDTSVariable dv = new CapitalDTSVariable();
						
						dv.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
						dv.setCodconceptoppalmod(new Long (rce.getCPMod()));
						dv.setCodriesgocubierto(new Long (rce.getCodRCub()));
						dv.setValor("S".equals(rce.getValor()) ? Constants.RIESGO_ELEGIDO_SI : Constants.RIESGO_ELEGIDO_NO);
						dv.setCapitalAsegurado(capitalAsegurado);
						
						capitalAsegurado.getCapitalDTSVariables().add(dv);
					}
				}	
			
			
				//asigno el capital asegurado a la parcela
				parcela.getCapitalAsegurados().add(capitalAsegurado);
			
			}
		} /* Fin del if */
		return parcela;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static com.rsi.agp.dao.tables.anexo.CapitalAsegurado setDatoVariableToPArcela(
			Object objeto, com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado, BigDecimal codconcepto) {
	
		try {
			//despues obtengo el valor que tiene el objeto en el dato variable.
			Class claseValor = objeto.getClass();
			Method methodValor = claseValor.getMethod("getValor");
			Object valor = methodValor.invoke(objeto);
			
			// 3. asigno el valor al dato variable
			if (!StringUtils.nullToString(valor).equals("")){
				CapitalDTSVariable datoVariable = new CapitalDTSVariable();
				datoVariable.setCodconcepto(codconcepto);
				if (valor instanceof XmlCalendar) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
					Date d = new Date();
					String fecha="";
					try {
						d = sdf.parse(valor.toString());
						fecha = sdf2.format(d);
					} catch (ParseException e) {
						logger.error("Error al parsear la fecha en los datos variables", e);
					}
					
					datoVariable.setValor(fecha);
				}else {
					datoVariable.setValor(StringUtils.nullToString(valor));
				}
				datoVariable.setCapitalAsegurado(capitalAsegurado);
				
				capitalAsegurado.getCapitalDTSVariables().add(datoVariable);
			}
		} catch (SecurityException e) {
			logger.debug("Error de seguridad " + e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("El metodo no existe para esta clase " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.debug("El metodo acepta los argumentos " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.debug("Error " + e.getMessage());
		} catch (InvocationTargetException e) {
			logger.debug("Error " + e.getMessage());
		}
		return capitalAsegurado;
	}

	/**
	 * Metodo para obtener un objeto parcela de anexo a partir de los datos de la parcela en la situacion actualizada 
	 * @param dvCodConceptoEtiqueta Datos variables que aplican para esta línea
	 * @param anexo Anexo asociado a la parcela
	 * @param par Parcela de la situacion actualizada
	 * @return Parcela del anexo con los datos de la situacion actualizada
	 */
	private static Parcela buildParcelaAnexoCpl(AnexoModificacion anexo, es.agroseguro.contratacion.parcela.ParcelaDocument parcelaAnx) {
		
		
		Parcela parcela = new Parcela();
		
		if (parcelaAnx != null && parcelaAnx.getParcela() != null) {
			
			es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela par = parcelaAnx.getParcela();
			
			parcela.setAnexoModificacion(anexo);
			parcela.setParcela(null);
			
			parcela.setHoja(new BigDecimal(par.getHoja()));
			parcela.setNumero(new BigDecimal(par.getNumero()));
			//parcela.setNomparcela(par.get);
			parcela.setCodprovincia(new BigDecimal(par.getUbicacion().getProvincia()));
			parcela.setCodcomarca(new BigDecimal(par.getUbicacion().getComarca()));
			parcela.setCodtermino(new BigDecimal(par.getUbicacion().getTermino()));
			if (!StringUtils.nullToString(par.getUbicacion().getSubtermino()).equals("")){
				parcela.setSubtermino(par.getUbicacion().getSubtermino().charAt(0));
			}
			else{
				parcela.setSubtermino(' ');
			}
			parcela.setCodcultivo(new BigDecimal(par.getCosecha().getCultivo()));
			parcela.setCodvariedad(new BigDecimal(par.getCosecha().getVariedad()));
			parcela.setCodprovsigpac(new BigDecimal(par.getSIGPAC().getProvincia()));
			parcela.setCodtermsigpac(new BigDecimal(par.getSIGPAC().getTermino()));
			parcela.setAgrsigpac(new BigDecimal(par.getSIGPAC().getAgregado()));
			parcela.setZonasigpac(new BigDecimal(par.getSIGPAC().getZona()));
			parcela.setPoligonosigpac(new BigDecimal(par.getSIGPAC().getPoligono()));
			parcela.setParcelasigpac(new BigDecimal(par.getSIGPAC().getParcela()));
			parcela.setRecintosigpac(new BigDecimal(par.getSIGPAC().getRecinto()));
			
			parcela.setTipoparcela(Constants.TIPO_PARCELA_PARCELA); // Ponemos todo como parcela y luego se recalculan las instalaciones
			parcela.setAltaencomplementario('N');
			
			//Rellenamos los capitales asegurados
			
			for (es.agroseguro.contratacion.parcela.CapitalAsegurado ca : par.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray()){
				
				com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado = new com.rsi.agp.dao.tables.anexo.CapitalAsegurado();
				
				capitalAsegurado.setParcela(parcela);
				
				capitalAsegurado.setTipoCapital(new TipoCapital(new BigDecimal(ca.getTipo()), null));
				capitalAsegurado.setSuperficie(ca.getSuperficie());
				//capitalAsegurado.setPrecio(ca.getPrecio());
				capitalAsegurado.setProduccion(new BigDecimal(ca.getProduccion()));
				
				//asigno el capital asegurado a la parcela
				parcela.getCapitalAsegurados().add(capitalAsegurado);
			}
		}	
		return parcela;
	}

	/**
	 * Metodo para obtener las coberturas de la poliza actualizada
	 * @param datosVariables
	 * @return
	 */
	public static List<CoberturaSeleccionada> getCoberturasFromPolizaActualizada(
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables) {
		
		List<CoberturaSeleccionada> lstCoberturas = new ArrayList<CoberturaSeleccionada>();
		if (datosVariables != null){
			//CALCULO INDEMNIZACION
			if (null != datosVariables.getCalcIndemArray() && datosVariables.getCalcIndemArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion calcIndem : datosVariables.getCalcIndemArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION));
					cobertura.setCodconceptoppalmod(new BigDecimal(calcIndem.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(calcIndem.getCodRCub()));
					cobertura.setCodvalor(calcIndem.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//GARANTIZADO
			if (null != datosVariables.getGarantArray() && datosVariables.getGarantArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.Garantizado garant : datosVariables.getGarantArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO));
					cobertura.setCodconceptoppalmod(new BigDecimal(garant.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(garant.getCodRCub()));
					cobertura.setCodvalor(garant.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//% FRANQUICIA
			if (null != datosVariables.getFranqArray() && datosVariables.getFranqArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia franq : datosVariables.getFranqArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA));
					cobertura.setCodconceptoppalmod(new BigDecimal(franq.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(franq.getCodRCub()));
					cobertura.setCodvalor(franq.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//% MINIMO INDEMNIZABLE
			if (null != datosVariables.getMinIndemArray() && datosVariables.getMinIndemArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable minIndem : datosVariables.getMinIndemArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE));
					cobertura.setCodconceptoppalmod(new BigDecimal(minIndem.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(minIndem.getCodRCub()));
					cobertura.setCodvalor(minIndem.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//RIESGO CUBIERTO ELEGIDO
			if (null != datosVariables.getRiesgCbtoElegArray() && datosVariables.getRiesgCbtoElegArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCub : datosVariables.getRiesgCbtoElegArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
					cobertura.setCodconceptoppalmod(new BigDecimal(rCub.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(rCub.getCodRCub()));
					cobertura.setCodvalor(rCub.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//TIPO FRANQUICIA
			if (null != datosVariables.getTipFranqArray() && datosVariables.getTipFranqArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tFranq : datosVariables.getTipFranqArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA));
					cobertura.setCodconceptoppalmod(new BigDecimal(tFranq.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(tFranq.getCodRCub()));
					cobertura.setCodvalor(tFranq.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//% CAPITAL ASEGURADO
			if (null != datosVariables.getCapAsegArray() && datosVariables.getCapAsegArray().length > 0){
				for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado capAseg : datosVariables.getCapAsegArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO));
					cobertura.setCodconceptoppalmod(new BigDecimal(capAseg.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(capAseg.getCodRCub()));
					cobertura.setCodvalor(capAseg.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//CARACT. EXPLOTACION
			if (null != datosVariables.getCarExpl()){
				CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
				
				cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION));
				cobertura.setCodvalor(datosVariables.getCarExpl().getValor() + "");
				
				lstCoberturas.add(cobertura);
			}
		}	
		return lstCoberturas;
	}
	
	public static List<CoberturaSeleccionada> getCoberturasFromPolizaActualizadaAnt(
			es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables datosVariables) {
		
		List<CoberturaSeleccionada> lstCoberturas = new ArrayList<CoberturaSeleccionada>();
		if (datosVariables != null){
			//CALCULO INDEMNIZACION
			if (null != datosVariables.getCalcIndemArray() && datosVariables.getCalcIndemArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.CalculoIndemnizacion calcIndem : datosVariables.getCalcIndemArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION));
					cobertura.setCodconceptoppalmod(new BigDecimal(calcIndem.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(calcIndem.getCodRCub()));
					cobertura.setCodvalor(calcIndem.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//GARANTIZADO
			if (null != datosVariables.getGarantArray() && datosVariables.getGarantArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.Garantizado garant : datosVariables.getGarantArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO));
					cobertura.setCodconceptoppalmod(new BigDecimal(garant.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(garant.getCodRCub()));
					cobertura.setCodvalor(garant.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//% FRANQUICIA
			if (null != datosVariables.getFranqArray() && datosVariables.getFranqArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeFranquicia franq : datosVariables.getFranqArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA));
					cobertura.setCodconceptoppalmod(new BigDecimal(franq.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(franq.getCodRCub()));
					cobertura.setCodvalor(franq.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//% MINIMO INDEMNIZABLE
			if (null != datosVariables.getMinIndemArray() && datosVariables.getMinIndemArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeMinimoIndemnizable minIndem : datosVariables.getMinIndemArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE));
					cobertura.setCodconceptoppalmod(new BigDecimal(minIndem.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(minIndem.getCodRCub()));
					cobertura.setCodvalor(minIndem.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//RIESGO CUBIERTO ELEGIDO
			if (null != datosVariables.getRiesgCbtoElegArray() && datosVariables.getRiesgCbtoElegArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.RiesgoCubiertoElegido rCub : datosVariables.getRiesgCbtoElegArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
					cobertura.setCodconceptoppalmod(new BigDecimal(rCub.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(rCub.getCodRCub()));
					cobertura.setCodvalor(rCub.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//TIPO FRANQUICIA
			if (null != datosVariables.getTipFranqArray() && datosVariables.getTipFranqArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoFranquicia tFranq : datosVariables.getTipFranqArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA));
					cobertura.setCodconceptoppalmod(new BigDecimal(tFranq.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(tFranq.getCodRCub()));
					cobertura.setCodvalor(tFranq.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//% CAPITAL ASEGURADO
			if (null != datosVariables.getCapAsegArray() && datosVariables.getCapAsegArray().length > 0){
				for (es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeCapitalAsegurado capAseg : datosVariables.getCapAsegArray()){
					CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
					
					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO));
					cobertura.setCodconceptoppalmod(new BigDecimal(capAseg.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(capAseg.getCodRCub()));
					cobertura.setCodvalor(capAseg.getValor() + "");
					
					lstCoberturas.add(cobertura);
				}
			}
			
			//CARACT. EXPLOTACION
			if (null != datosVariables.getCarExpl()){
				CoberturaSeleccionada cobertura = new CoberturaSeleccionada();
				
				cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION));
				cobertura.setCodvalor(datosVariables.getCarExpl().getValor() + "");
				
				lstCoberturas.add(cobertura);
			}
		}	
		return lstCoberturas;
	}

	/**
	 * Metodo para realizar la "fusion" de las parcelas de la poliza principal con las de la complementaria
	 * @param parcelasPpl Parcelas de la poliza principal
	 * @param parcelasCpl Parcelas de la poliza complementaria
	 * @return Lista definitiva con las parcelas y sus incrementos
	 */
	public static List<Parcela> getParcelasAnexoCpl(final List<Parcela> parcelasPpl, final List<Parcela> parcelasCpl) {
		
		List<Parcela> parcelas = new ArrayList<Parcela>();
		
		if (parcelasCpl.size() > 0){
			//Recorremos la lista de parcelas de la poliza principal y las vamos actualizando con los datos de la complementaria
			for (Parcela parPpl : parcelasPpl){
				//Busco en las complementarias si esta para anhadirle el incremento de la produccion
				for (Parcela parCpl : parcelasCpl){
					if (parPpl.getHoja().equals(parCpl.getHoja()) && parPpl.getNumero().equals(parCpl.getNumero())){
						//Si hay correspondencia entre la hoja y el numero, anhado el dato "incremento" al capital asegurado
						for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado caPpl : parPpl.getCapitalAsegurados()){
							for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado caCpl : parCpl.getCapitalAsegurados()){
								if (caPpl.getTipoCapital().getCodtipocapital().equals(caCpl.getTipoCapital().getCodtipocapital())){
									caPpl.setIncrementoproduccionanterior(caCpl.getProduccion());
									break;
								}
							}
						}
						break;
					}
				}
				
				//Anhado la parcela a la lista definitiva
				parcelas.add(parPpl);
			}
		}
		else{
			parcelas = new ArrayList<Parcela>(parcelasPpl);
		}
		
		return parcelas;
	}

	public static boolean tieneCambiosCoberturas(final AnexoModificacion anexo,
			final es.agroseguro.contratacion.Poliza sitActual) {
		Set<Cobertura> cobsAnexo = anexo.getCoberturas();
		Set<ComparativaPoliza> cobsPoliza = anexo.getPoliza().getComparativaPolizas();
		List<CoberturaSeleccionada> cobsSitAct = sitActual == null ? null
				: getCoberturasFromPolizaActualizada(sitActual.getCobertura().getDatosVariables());
		String codModuloAnt = sitActual == null ? anexo.getPoliza().getCodmodulo()
				: sitActual.getCobertura().getModulo();
		if ((!anexo.getCodmodulo().equals(codModuloAnt))
				|| (cobsAnexo.size() != (sitActual == null ? cobsPoliza.size() : cobsSitAct.size()))) {
			// SI EL NUMERO DE COBERTURAS ES DIFERENTE O EL MODULO ES DIFERENTE
			return true;
		} else {
			for (Cobertura cobAnx : cobsAnexo) {
				logger.debug("ANEXO coAn: " + cobAnx.getCodconceptoppalmod() + "_" + cobAnx.getCodriesgocubierto() + "_"
						+ cobAnx.getCodconcepto() + "-- Valor: " + cobAnx.getCodvalor());
				if (sitActual == null) {
					for (ComparativaPoliza cobPol : cobsPoliza) {
						logger.debug("POLIZA: " + cobPol.getId().getCodconceptoppalmod() + "_"
								+ cobPol.getId().getCodriesgocubierto() + "_" + cobPol.getId().getCodconcepto()
								+ "-- Valor: " + cobPol.getId().getCodvalor().toString());
						if (cobPol.getId().getCodconceptoppalmod().equals(cobAnx.getCodconceptoppalmod())
								&& cobPol.getId().getCodriesgocubierto().equals(cobAnx.getCodriesgocubierto())
								&& cobPol.getId().getCodconcepto().equals(cobAnx.getCodconcepto())) {
							if (!cobPol.getId().getCodvalor().toString().equals(cobAnx.getCodvalor())) {
								logger.debug("...distintos");
								return true;
							}
						}
					}
				} else {
					for (CoberturaSeleccionada cobSit : cobsSitAct) {
						logger.debug("SITUACION ACTUAL: " + cobSit.getCodconceptoppalmod() + "_"
								+ cobSit.getCodriesgocubierto() + "_" + cobSit.getCodconcepto() + "-- Valor: "
								+ cobSit.getCodvalor());
						if (cobSit.getCodconceptoppalmod().equals(cobAnx.getCodconceptoppalmod())
								&& cobSit.getCodriesgocubierto().equals(cobAnx.getCodriesgocubierto())
								&& cobSit.getCodconcepto().equals(cobAnx.getCodconcepto())) {
							if (!cobSit.getCodvalor().toString().equals(cobAnx.getCodvalor())) {
								logger.debug("...distintos");
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}
}
