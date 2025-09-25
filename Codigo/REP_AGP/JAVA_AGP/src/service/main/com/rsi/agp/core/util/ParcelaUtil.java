package com.rsi.agp.core.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.poliza.IDatosParcelaDao;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.SubvParcelaCCAA;
import com.rsi.agp.dao.tables.poliza.SubvParcelaENESA;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.DatoVariableParcelaVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.ProduccionVO;
import com.rsi.agp.vo.RiesgoVO;

public class ParcelaUtil {
	
	private static final Log logger = LogFactory.getLog(ParcelaUtil.class); 
	
	/**
	 * Obtiene una parcela y genera su parcelaVO. 
	 */
	public static ParcelaVO getParcela(Long codParcela, IDatosParcelaDao datosParcelaDao) {
		ParcelaVO parcelaVO = null;
		Parcela parcela;		
		try {
			parcela  =  (Parcela) datosParcelaDao.getObject(Parcela.class,codParcela);
			if(parcela != null) {
		        parcelaVO  = ParcelaUtil.getParcelaVO(parcela, datosParcelaDao);
			}
		} catch(Exception excepcion) {
			logger.error("Se ha producido un error al recuperar la Parcela.", excepcion);
		}		
		return parcelaVO;
	}

	/**
	 * Transforma un objeto parcela en un objeto parcelaVO que sera pasado 
	 * a la pantalla de parcelas
	 */
	@SuppressWarnings("unchecked")
	public static ParcelaVO getParcelaVO(final Parcela parcela, final IDatosParcelaDao datosParcelaDao) throws BusinessException {
		ParcelaVO parcelaVO = new ParcelaVO();
		String metrosCuadrados = "";
		try {
			logger.debug("**@@** DENTRO getParcelaVO");
			parcelaVO.setCodPoliza(parcela.getPoliza().getIdpoliza().toString());
			parcelaVO.setTipoParcela(String.valueOf(parcela.getTipoparcela()));
			parcelaVO.setCodParcela(parcela.getIdparcela().toString());
			// ----------------------------
			// --- DATOS PARCELA ---
			// ----------------------------
			parcelaVO.setIdparcelaanxestructura(
					parcela.getIdparcelaestructura() == null ? null : parcela.getIdparcelaestructura().toString());
			// --- Ubicacion ---
			if (parcela.getTermino() != null) {
				parcelaVO.setCodProvincia(parcela.getTermino().getProvincia().getCodprovincia().toString());
				parcelaVO.setDesProvincia(parcela.getTermino().getProvincia().getNomprovincia());
				parcelaVO.setCodComarca(parcela.getTermino().getComarca().getId().getCodcomarca().toString());
				parcelaVO.setDesComarca(parcela.getTermino().getComarca().getNomcomarca());
				parcelaVO.setCodTermino(parcela.getTermino().getId().getCodtermino().toString());
				
				Date fechaInicioContratacion = parcela.getPoliza().getLinea().getFechaInicioContratacion();
				// Utiliza el metodo getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
				// Esta version ahora tiene en cuenta la fecha de inicio de contratacion y si la linea es de ganado para determinar el nombre correcto del termino
				parcelaVO.setDesTermino(parcela.getTermino().getNomTerminoByFecha(fechaInicioContratacion, false));
				parcelaVO.setCodSubTermino(parcela.getTermino().getId().getSubtermino().toString());
			}			
			// --- Sigpac ---
			if (parcela.getCodprovsigpac() != null)
				parcelaVO.setProvinciaSigpac(parcela.getCodprovsigpac().toString());
			if (parcela.getCodtermsigpac() != null)
				parcelaVO.setTerminoSigpac(parcela.getCodtermsigpac().toString());
			if (parcela.getAgrsigpac() != null)
				parcelaVO.setAgregadoSigpac(parcela.getAgrsigpac().toString());
			if (parcela.getZonasigpac() != null)
				parcelaVO.setZonaSigpac(parcela.getZonasigpac().toString());
			if (parcela.getPoligonosigpac() != null)
				parcelaVO.setPoligonoSigpac(parcela.getPoligonosigpac().toString());
			if (parcela.getParcelasigpac() != null)
				parcelaVO.setParcelaSigpac(parcela.getParcelasigpac().toString());
			if (parcela.getRecintosigpac() != null)
				parcelaVO.setRecintoSigpac(parcela.getRecintosigpac().toString());
			// Otros
			if (parcela.getNomparcela() != null)
				parcelaVO.setNombreParcela(parcela.getNomparcela());
			Cultivo cultivo = null;
			try {
				CultivoId cultivoId = new CultivoId(parcela.getPoliza().getLinea().getLineaseguroid(),
						parcela.getCodcultivo());
				cultivo = (Cultivo) datosParcelaDao.getObject(Cultivo.class, cultivoId);
			} catch (Exception e) {
				logger.debug("No existe el cultivo");
				throw e;
			}
			Variedad variedad = null;
			try {
				VariedadId variedadId = new VariedadId(parcela.getPoliza().getLinea().getLineaseguroid(),
						parcela.getCodcultivo(), parcela.getCodvariedad());
				variedad = (Variedad) datosParcelaDao.getObject(Variedad.class, variedadId);
			} catch (Exception e) {
				logger.debug("No existe la variedad");
				throw e;
			}
			if (parcela.getCodcultivo() != null) {
				if (cultivo != null) {
					parcelaVO.setCultivo(parcela.getCodcultivo().toString());
					parcelaVO.setDesCultivo(cultivo.getDescultivo());
				} else {
					parcelaVO.setCultivo(null);
					parcelaVO.setDesCultivo(null);
				}
			}
			if (parcela.getCodvariedad() != null) {
				if (variedad != null) {
					parcelaVO.setVariedad(parcela.getCodvariedad().toString());
					parcelaVO.setDesVariedad(variedad.getDesvariedad());
				} else {
					// La variedad no existe
					parcelaVO.setVariedad(null);
					parcelaVO.setDesVariedad(null);
				}
			}
			// ----------------------------
			// --- CAPITALES ASEGURADOS ---
			// ----------------------------
			List<CapitalAsegurado> lstCapAseg = datosParcelaDao.getCapitalesAseguradoParcela(parcela.getIdparcela());
			if (lstCapAseg != null && lstCapAseg.size() > 0) {
				for (CapitalAsegurado capitalAsegurado : lstCapAseg) {
					if (capitalAsegurado != null) {
						CapitalAseguradoVO capitalAseguradoVO = new CapitalAseguradoVO();
						if (capitalAsegurado.getTipoCapital() != null
								&& capitalAsegurado.getTipoCapital().getCodtipocapital() != null) {
							// PRECIO/PRODUCCION
							List<CapAsegRelModulo> lstCapAsegRelMod = datosParcelaDao.getObjects(CapAsegRelModulo.class,
									"capitalAsegurado.idcapitalasegurado", capitalAsegurado.getIdcapitalasegurado());
							if (lstCapAsegRelMod != null && lstCapAsegRelMod.size() > 0) {
								BigDecimal precioMax = new BigDecimal(0);
								BigDecimal produccionMax = new BigDecimal(0);
								// Acumulador de precios y acumulador de producciones
								ArrayList<PrecioVO> listPrecios = new ArrayList<PrecioVO>();
								ArrayList<ProduccionVO> listProducciones = new ArrayList<ProduccionVO>();
								for (CapAsegRelModulo capAsegRelModulo : lstCapAsegRelMod) {
									// Es precio
									if (capAsegRelModulo.getPrecio() != null) {
										if (precioMax.compareTo(capAsegRelModulo.getPrecio()) == -1) {
											precioMax = capAsegRelModulo.getPrecio();
										}
										// Add to list precios
										PrecioVO precioVO = new PrecioVO();
										precioVO.setCodModulo(capAsegRelModulo.getCodmodulo());
										precioVO.setLimMax(capAsegRelModulo.getPrecio().toString());
										precioVO.setLimMin("0");
										listPrecios.add(precioVO);
									}
									// Es produccion
									if (capAsegRelModulo.getProduccion() != null) {
										if (produccionMax.compareTo(capAsegRelModulo.getProduccion()) == -1) {
											produccionMax = capAsegRelModulo.getProduccion();
										}
										// Add to list producciones
										ProduccionVO ProduccionVO = new ProduccionVO();
										ProduccionVO.setCodModulo(capAsegRelModulo.getCodmodulo());
										ProduccionVO.setLimMax(capAsegRelModulo.getProduccion().toString());
										ProduccionVO.setLimMin("1");
										listProducciones.add(ProduccionVO);
									}
								} // for
								capitalAseguradoVO.setPrecio(precioMax.toString());
								capitalAseguradoVO.setProduccion(produccionMax.toString());
								capitalAseguradoVO.setListPrecios(listPrecios);
								capitalAseguradoVO.setListProducciones(listProducciones);

							}
							if (capitalAsegurado.getTipoCapital() != null) {
								capitalAseguradoVO.setCodtipoCapital(
										capitalAsegurado.getTipoCapital().getCodtipocapital().toString());
								capitalAseguradoVO
										.setDesTipoCapital(capitalAsegurado.getTipoCapital().getDestipocapital());
							}
							if (capitalAsegurado.getIdcapitalasegurado() != null)
								capitalAseguradoVO.setId(capitalAsegurado.getIdcapitalasegurado().toString());
							if (capitalAsegurado.getSuperficie() != null)
								capitalAseguradoVO.setSuperficie(capitalAsegurado.getSuperficie().toString());
						}

						ArrayList<DatoVariableParcelaVO> datosVariables = new ArrayList<DatoVariableParcelaVO>();
						// ----------------------------
						// --- DATOS VARIABLES ---
						// ----------------------------
						List<DatoVariableParcela> lstDatVarParc = datosParcelaDao.getObjects(DatoVariableParcela.class,
								"capitalAsegurado.idcapitalasegurado", capitalAsegurado.getIdcapitalasegurado());
						if (lstDatVarParc != null && !lstDatVarParc.isEmpty()) {
							for (DatoVariableParcela datoVariableParcela : lstDatVarParc) {
								BigDecimal codConcepto = datoVariableParcela.getDiccionarioDatos().getCodconcepto();
								String valor = datoVariableParcela.getValor();
								datosVariables.add(new DatoVariableParcelaVO(codConcepto.intValue(), valor,
										datoVariableParcela.getIddatovariable()));
							}
							capitalAseguradoVO.setDatosVariablesParcela(datosVariables);
						}
						if (!StringUtils.nullToString(metrosCuadrados).equals("")) {
							capitalAseguradoVO.setMetrosCuadrados(metrosCuadrados);
						} else {
							capitalAseguradoVO.setMetrosCuadrados(capitalAsegurado.getSuperficie().toString());
						}
						capitalAseguradoVO.setDatosVariablesParcela(datosVariables);
						capitalAseguradoVO.setSuperficie(capitalAsegurado.getSuperficie().toString());
						parcelaVO.getCapitalesAsegurados().add(capitalAseguradoVO);
					}
				}
				parcelaVO.setCapitalAsegurado(parcelaVO.getCapitalesAsegurados().get(0));
			}
			// ----------------------------
			// --- SUBVENCIONES ---
			// ----------------------------
			// CCAA
			ArrayList<Integer> subCCAA = new ArrayList<Integer>();
			List<SubvParcelaCCAA> lstSubvCCAA = datosParcelaDao.getObjects(SubvParcelaCCAA.class, "parcela.idparcela",
					parcela.getIdparcela());
			Iterator<SubvParcelaCCAA> it1 = lstSubvCCAA.iterator();

			while (it1.hasNext()) {
				SubvParcelaCCAA aux = it1.next();
				subCCAA.add(
						new Integer(aux.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa().intValue()));
			}
			parcelaVO.setSubvencionesCCAA(subCCAA);
			// ENESA
			ArrayList<Integer> subENESA = new ArrayList<Integer>();
			List<SubvParcelaENESA> lstSubvEnesa = datosParcelaDao.getObjects(SubvParcelaENESA.class,
					"parcela.idparcela", parcela.getIdparcela());
			Iterator<SubvParcelaENESA> it2 = lstSubvEnesa.iterator();
			while (it2.hasNext()) {
				SubvParcelaENESA aux = it2.next();
				subENESA.add(new Integer(
						aux.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().intValue()));
			}
			parcelaVO.setSubvencionesENESA(subENESA);
			// ----------------------------
			// --- COBERTURAS ---
			// ----------------------------
			List<ParcelaCobertura> lstCoberturas = datosParcelaDao.getObjects(ParcelaCobertura.class,
					"parcela.idparcela", parcela.getIdparcela());
			for (ParcelaCobertura parcelaCobertura : lstCoberturas) {
				RiesgoVO riesgoVO = new RiesgoVO();

				riesgoVO.setCodConcepto(parcelaCobertura.getDiccionarioDatos().getCodconcepto().toString());
				riesgoVO.setCodConceptoPpalMod(
						parcelaCobertura.getConceptoPpalModulo().getCodconceptoppalmod().toString());
				riesgoVO.setCodModulo(parcelaCobertura.getRiesgoCubierto().getModulo().getId().getCodmodulo());
				riesgoVO.setCodRiesgoCubierto(
						parcelaCobertura.getRiesgoCubierto().getId().getCodriesgocubierto().toString());
				riesgoVO.setCodValor(parcelaCobertura.getCodvalor().toString());

				parcelaVO.getRiesgosSeleccionados().add(riesgoVO); // ADD COBERTURA
			}
		} catch (Exception exception) {
			logger.error("No se pudo cargar los datos de la parcela.", exception);
			throw (new BusinessException("No se pudo cargar los datos de la parcela.", exception));
		}
		return parcelaVO;
	}
	
	/**
	 * Transforma un objeto json en un objeto parcelaVO 
	 */
	public static ParcelaVO getParcelaVO(final JSONObject json) throws BusinessException {
		logger.debug("ParcelaUtil - getParcelaVO[INIT]");
		ParcelaVO parcelaVO = new ParcelaVO();
		try {
			parcelaVO.setCodPoliza(json.getString("idpoliza"));
			parcelaVO.setIdAnexoModificacion(json.getString("idanexo"));
			parcelaVO.setCodParcela(json.getString("codParcela"));
			parcelaVO.setTipoParcela(json.getString("tipoParcela"));
			// DATOS IDENTIFICATIVOS
			JSONObject datIdent = json.getJSONObject("datIdent");
			try {
				parcelaVO.setNombreParcela(URLDecoder.decode(datIdent.getString("nomParcela"), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				parcelaVO.setNombreParcela(datIdent.getString("nomParcela"));
			}
			parcelaVO.setCultivo(datIdent.getString("cultivo"));
			parcelaVO.setDesCultivo(datIdent.getString("desc_cultivo"));
			parcelaVO.setVariedad(datIdent.getString("variedad"));
			parcelaVO.setDesVariedad(datIdent.getString("desc_variedad"));
			parcelaVO.setProvinciaSigpac(datIdent.getString("provinciaSigpac"));
			parcelaVO.setTerminoSigpac(datIdent.getString("terminoSigpac"));
			parcelaVO.setAgregadoSigpac(datIdent.getString("agregadoSigpac"));
			parcelaVO.setZonaSigpac(datIdent.getString("zonaSigpac"));
			parcelaVO.setPoligonoSigpac(datIdent.getString("poligonoSigpac"));
			parcelaVO.setParcelaSigpac(datIdent.getString("parcelaSigpac"));
			parcelaVO.setRecintoSigpac(datIdent.getString("recintoSigpac"));
			parcelaVO.setCodProvincia(datIdent.getString("provincia"));
			parcelaVO.setDesProvincia(datIdent.getString("desc_provincia"));
			parcelaVO.setCodComarca(datIdent.getString("comarca"));
			parcelaVO.setDesComarca(datIdent.getString("desc_comarca"));
			parcelaVO.setCodTermino(datIdent.getString("termino"));
			parcelaVO.setDesTermino(datIdent.getString("desc_termino"));
			parcelaVO.setCodSubTermino(datIdent.getString("subtermino"));
			// DATOS CAPITAL ASEGURADO
			CapitalAseguradoVO capitalAsegurado = parcelaVO.getCapitalAsegurado();
			JSONObject capAseg = json.getJSONObject("capitalAsegurado");
			capitalAsegurado.setId(capAseg.getString("id"));
			capitalAsegurado.setCodtipoCapital(capAseg.getString("tipoCapital"));
			capitalAsegurado.setSuperficie(capAseg.getString("superficie"));
			capitalAsegurado.setPrecio(capAseg.getString("precio"));
			capitalAsegurado.setProduccion(capAseg.getString("produccion"));
			JSONObject dvs = capAseg.getJSONObject("dvs");
			String[] names = JSONObject.getNames(dvs);
			
			List<DatoVariableParcelaVO> datosVariables = new ArrayList<DatoVariableParcelaVO>(
					names == null ? 0 : names.length);
			if (names != null) {
				for (String name : names) {
					Integer codconcepto = Integer.valueOf(name.replace("cod_cpto_", ""));
					datosVariables.add(new DatoVariableParcelaVO(codconcepto, dvs.getString(name), null));
				}
			}
			capitalAsegurado.setDatosVariablesParcela(datosVariables);
			
			/* Pet.50776_63485-Fase II ** MODIF TAM (26.10.2020) ** Inicio */
			
			JSONObject riesgoCubierto = json.getJSONObject("riesgoCub");
			RiesgoVO riesgoCub = new RiesgoVO();
			names = JSONObject.getNames(riesgoCubierto);
			String valor ="";
			
			if (names != null) {
				String [] nombre = null;
				List<RiesgoVO> listriesgCubierto = new ArrayList<RiesgoVO>(names.length);
				List<RiesgoVO> listriesgCubDatVar = new ArrayList<RiesgoVO>(names.length);
				// Primero recorremos los riesgos
				for (String name : names) {
					valor = riesgoCubierto.getString(name);
					nombre = name.split("\\_");
					
					if (nombre[0].equals("riesgo")) {
						logger.debug("Encontrado riesgo");
						riesgoCub = new RiesgoVO();
						
						logger.debug("Valor de riesgo_valor:"+valor);
						String[] seleccionados = valor.split("\\|");
						
						riesgoCub.setCodConceptoPpalMod(seleccionados[6]);
						riesgoCub.setCodConcepto(seleccionados[0]);
						riesgoCub.setCodModulo(seleccionados[4]);
						riesgoCub.setCodRiesgoCubierto(seleccionados[8]);
						riesgoCub.setCodValor(seleccionados[1]);
						riesgoCub.setLineaSeguroId(seleccionados[2]);
						
						listriesgCubierto.add(riesgoCub);
						
					}
				} /* Fin del for de names */
				
				/*****************************************/
				/**	Luego recorremos los datos variables */ 
				/*****************************************/
				for (String name2 : names) {
						valor = riesgoCubierto.getString(name2);
						nombre = name2.split("\\_");
						
						if (nombre[0].equals("datVariable")) {
							logger.debug("Encontrado datoVariable");
					
							logger.debug("Valor de datoVariable:"+valor);
							logger.debug("Valor de riesgo_valor:"+valor);
							String[] datVar = valor.split("\\#");
							
							String[] datVarGenerales = datVar[0].split("\\|");
							
							riesgoCub = new RiesgoVO();
							
							String conceptoPPalMod = datVarGenerales[5];
							String riesCub = datVarGenerales[7];
							String codModulo = datVarGenerales[3];
							String lineaSeguroid = datVarGenerales[1];
							String selecDatVar = datVarGenerales[0];
								
							if (selecDatVar.equals("-1")) {
								logger.debug("Riesgo seleccionado");
								String[] datVariables = datVar[1].split("\\|");
								
								riesgoCub.setCodConceptoPpalMod(conceptoPPalMod);
								riesgoCub.setCodConcepto(datVariables[0]);
								riesgoCub.setCodModulo(codModulo);
								riesgoCub.setCodRiesgoCubierto(riesCub);
								riesgoCub.setCodValor(datVariables[2]);
								riesgoCub.setLineaSeguroId(lineaSeguroid);
								
								listriesgCubDatVar.add(riesgoCub);
							} else {
								logger.debug("Riesgo no seleccionado");
							}
						} /* Fin del id dato variable*/
					} /* Fin del for de names*/
					
					for (RiesgoVO riesgoDat: listriesgCubDatVar) {
						listriesgCubierto.add(riesgoDat);	
					}
					
					logger.debug("Asignamos Riesgos Cubiertos a la parcela");
					parcelaVO.setRiesgosSeleccionados(listriesgCubierto);
				}
			/* Pet.50776_63485-Fase II ** MODIF TAM (26.10.2020) ** Fin */
			
			parcelaVO.setCapitalAsegurado(capitalAsegurado);
			parcelaVO.getCapitalesAsegurados().add(capitalAsegurado);
		} catch (JSONException exception) {
			throw new BusinessException("Error al transformar el JSON en ParcelaVO.", exception);
		}
		logger.debug("ParcelaUtil - getParcelaVO[END]");
		return parcelaVO;
	}
	
	/**
	 * Transforma un objeto CapitalAsegurado en un objeto CapitalAseguradoVO 
	 */
	public static CapitalAseguradoVO getCapitalAseguradoVO(final CapitalAsegurado obj) throws BusinessException {
		CapitalAseguradoVO capasegVO = new CapitalAseguradoVO();
		capasegVO.setId(obj.getIdcapitalasegurado().toString());
		capasegVO.setCodtipoCapital(obj.getTipoCapital().getCodtipocapital().toString());
		capasegVO.setDesTipoCapital(obj.getTipoCapital().getDestipocapital());
		capasegVO.setSuperficie(obj.getSuperficie().toString());
		Set<DatoVariableParcela> lstDatVarParc = obj.getDatoVariableParcelas();
		if (lstDatVarParc != null && !lstDatVarParc.isEmpty()) {
			List<DatoVariableParcelaVO> datosVariables = new ArrayList<DatoVariableParcelaVO>(lstDatVarParc.size());
			for (DatoVariableParcela datoVariableParcela : lstDatVarParc) {
				BigDecimal codConcepto = datoVariableParcela.getDiccionarioDatos().getCodconcepto();
				String valor = datoVariableParcela.getValor();
				datosVariables.add(new DatoVariableParcelaVO(codConcepto.intValue(), valor,
						datoVariableParcela.getIddatovariable()));
			}
			capasegVO.setDatosVariablesParcela(datosVariables);
		}
		Set<CapAsegRelModulo> lstCapAsegRelMod = obj.getCapAsegRelModulos();
		if (lstCapAsegRelMod != null && lstCapAsegRelMod.size() > 0) {
			BigDecimal precioMax = new BigDecimal(0);
			BigDecimal produccionMax = new BigDecimal(0);
			// Acumulador de precios y acumulador de producciones
			ArrayList<PrecioVO> listPrecios = new ArrayList<PrecioVO>();
			ArrayList<ProduccionVO> listProducciones = new ArrayList<ProduccionVO>();
			for (CapAsegRelModulo capAsegRelModulo : lstCapAsegRelMod) {
				// Es precio
				if (capAsegRelModulo.getPrecio() != null) {
					if (precioMax.compareTo(capAsegRelModulo.getPrecio()) == -1) {
						precioMax = capAsegRelModulo.getPrecio();
					}
					// Add to list precios
					PrecioVO precioVO = new PrecioVO();
					precioVO.setCodModulo(capAsegRelModulo.getCodmodulo());
					precioVO.setLimMax(capAsegRelModulo.getPrecio().toString());
					precioVO.setLimMin("0");
					listPrecios.add(precioVO);
				}
				// Es produccion
				if (capAsegRelModulo.getProduccion() != null) {
					if (produccionMax.compareTo(capAsegRelModulo.getProduccion()) == -1) {
						produccionMax = capAsegRelModulo.getProduccion();
					}
					// Add to list producciones
					ProduccionVO ProduccionVO = new ProduccionVO();
					ProduccionVO.setCodModulo(capAsegRelModulo.getCodmodulo());
					ProduccionVO.setLimMax(capAsegRelModulo.getProduccion().toString());
					ProduccionVO.setLimMin("1");
					listProducciones.add(ProduccionVO);
				}
			}
			capasegVO.setPrecio(precioMax.toString());
			capasegVO.setProduccion(produccionMax.toString());
			capasegVO.setListPrecios(listPrecios);
			capasegVO.setListProducciones(listProducciones);
		}		
		return capasegVO;
	}
}