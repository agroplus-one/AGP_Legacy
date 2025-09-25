package com.rsi.agp.batch.importacion;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Node;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public final class BBDDParcelasUtil {

	private static final Logger logger = Logger
			.getLogger(BBDDParcelasUtil.class);

	private BBDDParcelasUtil() {
	}

	// Metodo que transforma las parcelas de la situacion actual de la poliza
	// principal en el formato esperado por el modelo de datos de Agroplus y
	// puebla el objeto Hibernate encargado de la importacion
	protected static void populateParcelasPpal(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados,
			final es.agroseguro.contratacion.Cobertura cobertura,
			final Session session) throws Exception {
		Set<Parcela> parcelasHbm;
		Set<CapitalAsegurado> capitalesAseguradosHbm;
		Parcela parcelaHbm;
		CapitalAsegurado capitalAseguradoHbm;
		TipoCapital tipoCapitalHbm;
		CapAsegRelModulo capAsegRelMod;
		Set<CapAsegRelModulo> capitalesAsegRelMod;

		parcelasHbm = new HashSet<Parcela>();
		
		Node node = objetosAsegurados.getDomNode().getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) { 
				es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocument = null;
				try { 
					parcelaDocument = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);
	            } catch (XmlException e) { 
	                logger.error("Error al parsear una parcela.", e); 
	                
	            }
				if (parcelaDocument != null) {
					es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela = parcelaDocument.getParcela();
					parcelaHbm = new Parcela();
					
					Variedad variedadHbm = (Variedad) session
							.createCriteria(Variedad.class)
							.add(Restrictions.eq("id.codvariedad", BigDecimal
									.valueOf(parcela.getCosecha().getVariedad())))
							.add(Restrictions.eq("id.codcultivo", BigDecimal
									.valueOf(parcela.getCosecha().getCultivo())))
							.add(Restrictions.eq("id.lineaseguroid", polizaHbm
									.getLinea().getLineaseguroid())).uniqueResult();
					if (variedadHbm == null) {
						throw new Exception(
								"No se encuentra la variedad. Revise los datos: codvariedad "
										+ parcela.getCosecha().getVariedad()
										+ ", codcultivo "
										+ parcela.getCosecha().getCultivo()
										+ ", lineaseguroid "
										+ polizaHbm.getLinea().getLineaseguroid());
					}
					parcelaHbm.setVariedad(variedadHbm);

					Termino terminoHbm = (Termino) session
							.createCriteria(Termino.class)
							.add(Restrictions.eq("id.codprovincia", BigDecimal
									.valueOf(parcela.getUbicacion().getProvincia())))
							.add(Restrictions.eq("id.codcomarca", BigDecimal
									.valueOf(parcela.getUbicacion().getComarca())))
							.add(Restrictions.eq("id.codtermino", BigDecimal
									.valueOf(parcela.getUbicacion().getTermino())))
							.add(Restrictions.eq("id.subtermino", "".equals(parcela
									.getUbicacion().getSubtermino()) ? ' ' : parcela
									.getUbicacion().getSubtermino())).uniqueResult();
					if (terminoHbm == null) {
						throw new Exception(
								"No se encuentra el termino. Revise los datos: codprovincia "
										+ parcela.getUbicacion().getProvincia()
										+ ", codcomarca "
										+ parcela.getUbicacion().getComarca()
										+ ", codtermino "
										+ parcela.getUbicacion().getTermino()
										+ ", subtermino "
										+ parcela.getUbicacion().getSubtermino());
					}
					parcelaHbm.setTermino(terminoHbm);

					
					if (parcela.getSIGPAC() != null) {
						parcelaHbm.setCodprovsigpac(BigDecimal.valueOf(parcela
								.getSIGPAC().getProvincia()));
						parcelaHbm.setCodtermsigpac(BigDecimal.valueOf(parcela
								.getSIGPAC().getTermino()));
						parcelaHbm.setAgrsigpac(BigDecimal.valueOf(parcela.getSIGPAC()
								.getAgregado()));
						parcelaHbm.setZonasigpac(BigDecimal.valueOf(parcela.getSIGPAC()
								.getZona()));
						parcelaHbm.setPoligonosigpac(BigDecimal.valueOf(parcela
								.getSIGPAC().getPoligono()));
						parcelaHbm.setParcelasigpac(BigDecimal.valueOf(parcela
								.getSIGPAC().getParcela()));
						parcelaHbm.setRecintosigpac(BigDecimal.valueOf(parcela
								.getSIGPAC().getRecinto()));
					}

					parcelaHbm.setNomparcela(parcela.getNombre());
					parcelaHbm.setCodcultivo(variedadHbm.getCultivo().getId()
							.getCodcultivo());
					parcelaHbm.setCodvariedad(variedadHbm.getId().getCodvariedad());
					parcelaHbm.setHoja(parcela.getHoja());
					parcelaHbm.setNumero(parcela.getNumero());
					parcelaHbm.setTipoparcela(Constants.TIPO_PARCELA_PARCELA);
					parcelaHbm.setAltaencomplementario(Constants.CHARACTER_N);

					parcelaHbm.setPoliza(polizaHbm);
					session.saveOrUpdate(parcelaHbm);

					try {

						es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitalAseguradoArr = parcela.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray();
						capitalesAseguradosHbm = new HashSet<CapitalAsegurado>(capitalAseguradoArr.length);
						
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado : capitalAseguradoArr) {

							if (capitalAsegurado.getTipo() >= 100) {
								parcelaHbm
										.setTipoparcela(Constants.TIPO_PARCELA_INSTALACION);
							}

							capitalAseguradoHbm = new CapitalAsegurado();
							capitalAseguradoHbm.setPrecio(capitalAsegurado.getPrecio());
							capitalAseguradoHbm.setProduccion(BigDecimal
									.valueOf(capitalAsegurado.getProduccion()));
							capitalAseguradoHbm.setSuperficie(capitalAsegurado
									.getSuperficie());
							tipoCapitalHbm = (TipoCapital) session.get(
									TipoCapital.class,
									BigDecimal.valueOf(capitalAsegurado.getTipo()));
							if (tipoCapitalHbm == null) {
								throw new Exception(
										"No se encuentra el tipo de capital. Revise los datos: codtipocapital "
												+ capitalAsegurado.getTipo());
							}
							capitalAseguradoHbm.setTipoCapital(tipoCapitalHbm);
							capitalAseguradoHbm
									.setAltaencomplementario(Constants.CHARACTER_N);

							capitalAseguradoHbm.setParcela(parcelaHbm);
							session.saveOrUpdate(capitalAseguradoHbm);

							try {
								populateDatosVariables(capitalAseguradoHbm,
										capitalAsegurado.getDatosVariables(), polizaHbm
												.getLinea().getLineaseguroid(), session);
								//Anhadimos los datos variables genericos que vienen en la cobertura
								populateDatosVariables(capitalAseguradoHbm,
										cobertura.getDatosVariables(), polizaHbm
												.getLinea().getLineaseguroid(), session);
								
								capitalesAsegRelMod = new HashSet<CapAsegRelModulo>();
								capAsegRelMod = new CapAsegRelModulo();

								capAsegRelMod.setCodmodulo(polizaHbm.getCodmodulo());
								capAsegRelMod.setPrecio(capitalAsegurado.getPrecio());
								capAsegRelMod.setProduccion(BigDecimal
										.valueOf(capitalAsegurado.getProduccion()));
								capAsegRelMod.setCapitalAsegurado(capitalAseguradoHbm);

								capitalesAsegRelMod.add(capAsegRelMod);
								session.saveOrUpdate(capAsegRelMod);
								capitalAseguradoHbm
										.setCapAsegRelModulos(capitalesAsegRelMod);
							} catch (Exception ex) {

								session.delete(capitalAseguradoHbm);
								session.delete(parcelaHbm);
								throw ex;
							}
							capitalesAseguradosHbm.add(capitalAseguradoHbm);
						}

						parcelaHbm.setCapitalAsegurados(capitalesAseguradosHbm);
						parcelasHbm.add(parcelaHbm);
					} catch (Exception ex) {

						session.delete(parcelaHbm);
						throw ex;
					}
				}
			}
			node = node.getNextSibling();
		}

		polizaHbm.setParcelas(parcelasHbm);
	}

	// Metodo que transforma las parcelas de la situacion actual de la poliza
	// complementaria en el formato esperado por el modelo de datos de Agroplus
	// y puebla el objeto Hibernate encargado de la importacion
	protected static void populateParcelasComp(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados,
			final Session session) throws Exception {

		Set<Parcela> parcelasHbm;
		Parcela parcelaHbm;
		Set<CapitalAsegurado> capitalesAseguradosHbm;
		CapitalAsegurado capAsegHbm;
		es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcelaComp;
		BigDecimal precioPpal;
		BigDecimal produccionPpal;
		Boolean isAltaEnComp;

		parcelasHbm = new HashSet<Parcela>();

		// Recorremos las parcelas de la poliza principal
		Set<Parcela> parcelasPolPpal = polizaHbm.getPolizaPpal().getParcelas();
		for (Parcela parcelaPpal : parcelasPolPpal) {

			isAltaEnComp = Boolean.FALSE;
			parcelaComp = null;
			precioPpal = null;
			produccionPpal = null;

			// Inicamente para parcelas NO de instalaciones
			if (Constants.TIPO_PARCELA_PARCELA.equals(parcelaPpal.getTipoparcela())) {

				Node node = objetosAsegurados.getDomNode().getFirstChild();
				while (node != null) {
					if (node.getNodeType() == Node.ELEMENT_NODE) { 
						es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocument = null;
						try { 
							parcelaDocument = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);
			            } catch (XmlException e) { 
			                logger.error("Error al parsear una parcela.", e); 
			                
			            }
						if (parcelaDocument != null) {
							es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela = parcelaDocument.getParcela();
							if (parcelaPpal.getHoja().equals(
									Integer.valueOf(parcela.getHoja()))
									&& parcelaPpal.getNumero().equals(
											Integer.valueOf(parcela.getNumero()))) {
								isAltaEnComp = Boolean.TRUE;
								parcelaComp = parcela;
							}
						}
					}
					node = node.getNextSibling();
				}

				// Recorremos los capitales asegurados
				Set<CapitalAsegurado> capitalesAsegPpal = parcelaPpal
						.getCapitalAsegurados();
				capitalesAseguradosHbm = new HashSet<CapitalAsegurado>(
						capitalesAsegPpal.size());
				for (CapitalAsegurado capAsegPpal : capitalesAsegPpal) {

					// unicamente para tipos de capital de produccion
					if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PRODUCCION)
							.equals(capAsegPpal.getTipoCapital().getCodconcepto())) {

						Set<CapAsegRelModulo> capAsegRelModulosPpal = capAsegPpal
								.getCapAsegRelModulos();
						for (CapAsegRelModulo capAsegRelMod : capAsegRelModulosPpal) {

							if (polizaHbm.getPolizaPpal().getCodmodulo()
									.equals(capAsegRelMod.getCodmodulo())) {
								precioPpal = capAsegRelMod.getPrecio();
								produccionPpal = capAsegRelMod.getProduccion();
								break;
							}
						}

						if (precioPpal == null || produccionPpal == null) {
							throw new Exception(
									"No se ha podido obtener precio y produccion de la parcela de la poliza principal.");
						}

						capAsegHbm = new CapitalAsegurado();

						capAsegHbm.setPrecio(precioPpal);
						capAsegHbm.setProduccion(produccionPpal);
						capAsegHbm.setSuperficie(capAsegPpal.getSuperficie());
						capAsegHbm.setTipoCapital(capAsegPpal.getTipoCapital());
						capAsegHbm
								.setAltaencomplementario(isAltaEnComp ? Constants.CHARACTER_S
										: Constants.CHARACTER_N);
						if (parcelaComp != null) {

							es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitalAseguradoArr = parcelaComp
									.getCosecha().getCapitalesAsegurados()
									.getCapitalAseguradoArray();
							for (es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado : capitalAseguradoArr) {

								if (capAsegPpal
										.getTipoCapital()
										.getCodtipocapital()
										.equals(BigDecimal
												.valueOf(capitalAsegurado
														.getTipo()))) {

									capAsegHbm
											.setIncrementoproduccion(BigDecimal
													.valueOf(capitalAsegurado
															.getProduccion()));
								}
							}

						}

						capitalesAseguradosHbm.add(capAsegHbm);
					}
				}

				// Se ha encontrado al menos un capital asegurado de produccion
				// en la parcela principal. Debemos crear la complementaria
				if (!capitalesAseguradosHbm.isEmpty()) {
					parcelaHbm = new Parcela();

					parcelaHbm.setVariedad(parcelaPpal.getVariedad());
					parcelaHbm.setTermino(parcelaPpal.getTermino());
					parcelaHbm.setPoligono(parcelaPpal.getPoligono());
					parcelaHbm.setParcela(parcelaPpal.getParcela());
					parcelaHbm.setCodprovsigpac(parcelaPpal.getCodprovsigpac());
					parcelaHbm.setCodtermsigpac(parcelaPpal.getCodtermsigpac());
					parcelaHbm.setAgrsigpac(parcelaPpal.getAgrsigpac());
					parcelaHbm.setZonasigpac(parcelaPpal.getZonasigpac());
					parcelaHbm.setPoligonosigpac(parcelaPpal
							.getPoligonosigpac());
					parcelaHbm.setParcelasigpac(parcelaPpal.getParcelasigpac());
					parcelaHbm.setRecintosigpac(parcelaPpal.getRecintosigpac());
					parcelaHbm.setNomparcela(parcelaPpal.getNomparcela());
					parcelaHbm.setCodcultivo(parcelaPpal.getCodcultivo());
					parcelaHbm.setCodvariedad(parcelaPpal.getCodvariedad());
					parcelaHbm.setHoja(parcelaPpal.getHoja());
					parcelaHbm.setNumero(parcelaPpal.getNumero());
					parcelaHbm.setTipoparcela(Constants.TIPO_PARCELA_PARCELA);
					parcelaHbm.setPoliza(polizaHbm);
					parcelaHbm
							.setAltaencomplementario(isAltaEnComp ? Constants.CHARACTER_S
									: Constants.CHARACTER_N);

					session.saveOrUpdate(parcelaHbm);

					for (CapitalAsegurado capAsegHbmAux : capitalesAseguradosHbm) {
						capAsegHbmAux.setParcela(parcelaHbm);
						session.saveOrUpdate(capAsegHbmAux);
					}
					parcelaHbm.setCapitalAsegurados(capitalesAseguradosHbm);
					
					parcelasHbm.add(parcelaHbm);
				}
			}
		}

		polizaHbm.setParcelas(parcelasHbm);
	}

	private static void populateDatosVariables(
			final CapitalAsegurado capitalAseguradoHbm,
			final es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables,			
			final Long lineaseguroid, final Session session) throws Exception {

		Set<DatoVariableParcela> datoVariableParcelasHbm;
		DatoVariableParcela datoVariableParcelaHbm;
		Method method;

		if (datosVariables != null) {
			
			datoVariableParcelasHbm = new HashSet<DatoVariableParcela>();
			List<DiccionarioDatos> dicDatosHbmArr = getDiccionarioDatosVariablesParcela(lineaseguroid,session);

			for (DiccionarioDatos dicDatosHbm : dicDatosHbmArr) {

				try {
					method = datosVariables.getClass().getMethod(
							"get" + dicDatosHbm.getEtiquetaxml());
					// Es de tipo simple... unicamente nos interesa el valor
					Object datoVar = method.invoke(datosVariables);

					if (datoVar != null) {
						datoVariableParcelaHbm = new DatoVariableParcela();

						datoVariableParcelaHbm.setDiccionarioDatos(dicDatosHbm);

						Object valor = datoVar.getClass().getMethod("getValor").invoke(datoVar);
						if (valor instanceof XmlCalendar) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							datoVariableParcelaHbm.setValor(sdf
									.format(((XmlCalendar) datoVar.getClass()
											.getMethod("getValor")
											.invoke(datoVar)).getTime()));
						} else {
							datoVariableParcelaHbm.setValor(datoVar.getClass()
									.getMethod("getValor").invoke(datoVar)
									.toString());
						}
						datoVariableParcelaHbm
								.setCapitalAsegurado(capitalAseguradoHbm);
						session.saveOrUpdate(datoVariableParcelaHbm);
						datoVariableParcelasHbm.add(datoVariableParcelaHbm);
					}
				} catch (NoSuchMethodException ex) {
					// Es de tipo complejo... multiples ocurrencias y nos
					// interesa el valor, el concepto principal y el riesgo
					// cubierto
					try {
						method = datosVariables.getClass().getMethod(
								"get" + dicDatosHbm.getEtiquetaxml() + "Array");
						Object[] datosVar = (Object[]) method
								.invoke(datosVariables);

						for (Object datoVar : datosVar) {
							if (datoVar != null) {
								datoVariableParcelaHbm = new DatoVariableParcela();

								datoVariableParcelaHbm
										.setCodconceptoppalmod(Integer
												.valueOf(datoVar.getClass()
														.getMethod("getCPMod")
														.invoke(datoVar)
														.toString()));
								datoVariableParcelaHbm
										.setCodriesgocubierto(Integer
												.valueOf(datoVar
														.getClass()
														.getMethod("getCodRCub")
														.invoke(datoVar)
														.toString()));
								datoVariableParcelaHbm
										.setDiccionarioDatos(dicDatosHbm);
								Object valor = datoVar.getClass()
										.getMethod("getValor").invoke(datoVar);
								if (valor instanceof XmlCalendar) {
									SimpleDateFormat sdf = new SimpleDateFormat(
											"dd/MM/yyyy");
									datoVariableParcelaHbm
											.setValor(sdf
													.format(((XmlCalendar) datoVar
															.getClass()
															.getMethod(
																	"getValor")
															.invoke(datoVar))
															.getTime()));
								} else {
									datoVariableParcelaHbm.setValor(datoVar
											.getClass().getMethod("getValor")
											.invoke(datoVar).toString());
								}
								datoVariableParcelaHbm
										.setCapitalAsegurado(capitalAseguradoHbm);
								session.saveOrUpdate(datoVariableParcelaHbm);
								datoVariableParcelasHbm
										.add(datoVariableParcelaHbm);
							}
						}
					} catch (NoSuchMethodException nsmex) {
						// El dato variable no pertenece a la parcela
						// Do nothing
						logger.debug("El campo variable "
								+ dicDatosHbm.getEtiquetaxml()
								+ " no es de ambito parcela.");
					}
				}
			}
			if(null==capitalAseguradoHbm.getDatoVariableParcelas() || capitalAseguradoHbm.getDatoVariableParcelas().size()==0){
				capitalAseguradoHbm.setDatoVariableParcelas(datoVariableParcelasHbm);
			}else{
				capitalAseguradoHbm.getDatoVariableParcelas().addAll(datoVariableParcelasHbm);
			}
			
		}
	}
	
	
	private static List<DiccionarioDatos> getDiccionarioDatosVariablesParcela(final Long lineaseguroid,final Session session){
		

		// Buscamos los datos variables del diccionario de datos con valor
		// en la columna EtiquetaXML que seran los susceptibles de venir
		// poblados en el bean de datos variables de la situacion actual
		@SuppressWarnings("unchecked")
		List<DiccionarioDatos> dicDatosHbmArr = (List<DiccionarioDatos>) session
				.createCriteria(DiccionarioDatos.class)
				.add(Restrictions.isNotNull("etiquetaxml"))
				.setFetchMode("organizadorInformacions", FetchMode.JOIN)
				.createAlias("organizadorInformacions.ubicacion",
						"ubicacion")
				.createAlias("organizadorInformacions.uso", "uso")
				.createAlias("organizadorInformacions.linea", "linea")
				.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid))
				.add(Restrictions.eq("ubicacion.codubicacion",
						OrganizadorInfoConstants.UBICACION_PARCELA_DV))
				.add(Restrictions.eq("uso.coduso", Constants.USO_POLIZA))
				.list();
		return dicDatosHbmArr;
	}

	protected static void asignarParcelasInstalaciones(
			final Set<Parcela> parcelas) {

		Iterator<Parcela> it;

		it = parcelas.iterator();
		while (it.hasNext()) {

			Parcela parcelaHbm = it.next();
			if (parcelaHbm.getTipoparcela() == Constants.TIPO_PARCELA_INSTALACION) {

				// Es parcela de instalaciones, buscamos sus parcelas
				// asociadas
				for (Parcela parcelaHbmAux : parcelas) {

					// Se compara por Ubicacion (SIGPAC o Ident. Catastral),
					// cultivo y variedad
					int comparation = new Comparator<Parcela>() {

						@Override
						public int compare(final Parcela arg0,
								final Parcela arg1) {

							int result = -1;

							// Comparamos el cultivo/variedad
							if (arg0.getCodcultivo().equals(
									arg1.getCodcultivo())
									&& arg0.getCodvariedad().equals(
											arg1.getCodvariedad())) {
								result = 0;
							}
							// Si aplica, comparamos la ubicacion
							if (result == 0) {
								boolean isSigPac = ""
										.equals(arg0.getPoligono() == null ? ""
												: arg0.getPoligono())
										&& "".equals(arg0.getParcela() == null ? ""
												: arg0.getParcela());

								if (isSigPac) {
									if (arg0.getCodprovsigpac().equals(
											arg1.getCodprovsigpac())
											&& arg0.getCodtermsigpac().equals(
													arg1.getCodtermsigpac())
											&& arg0.getAgrsigpac().equals(
													arg1.getAgrsigpac())
											&& arg0.getZonasigpac().equals(
													arg1.getZonasigpac())
											&& arg0.getPoligonosigpac().equals(
													arg1.getPoligonosigpac())
											&& arg0.getParcelasigpac().equals(
													arg1.getParcelasigpac())
											&& arg0.getRecintosigpac().equals(
													arg1.getRecintosigpac())) {
										result = 0;
									}
								} else {
									if (arg0.getPoligono().equals(
											arg1.getPoligono())
											&& arg0.getParcela().equals(
													arg1.getParcela())) {
										result = 0;
									}
								}
							}

							return result;
						}
					}.compare(parcelaHbm, parcelaHbmAux);

					// Si son la misma parcela se asigna la instalacion
					if (comparation == 0) {
						parcelaHbmAux.setIdparcelaestructura(parcelaHbm
								.getIdparcela());
					}
				}
			}
		}
	}
}
