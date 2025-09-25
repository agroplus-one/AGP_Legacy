package com.rsi.agp.core.managers.impl.poliza.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;

import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado;

public class PolizaUtils {

	private static final Log logger = LogFactory.getLog(PolizaUtils.class);

	/**
	 * Método para obtener una lista de explotaciones de anexo a partir de los
	 * datos de la situación actualizada de agroseguro.
	 * 
	 * @param poliza
	 *            Poliza actualizada de Agroseguro
	 * @return Listado de explotaciones para un anexo
	 */
	public static List<Explotacion> getExplotacionesPolizaFromPolizaActualizada(
			final es.agroseguro.contratacion.Poliza poliza,
			final Long idPoliza,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta) {
		List<Explotacion> explotaciones = new ArrayList<Explotacion>();
		Poliza polizaAgr = new Poliza(idPoliza);	
		if(poliza.getCobertura()!= null)
			polizaAgr.setCodmodulo(poliza.getCobertura().getModulo());
		Node currNode = poliza.getObjetosAsegurados().getDomNode()
				.getFirstChild();
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				ExplotacionDocument xmlExplotacion = null;
				try {
					xmlExplotacion = ExplotacionDocument.Factory
							.parse(currNode);
					Explotacion explotacion = buildExplotacionPoliza(
							dvCodConceptoEtiqueta, polizaAgr,
							xmlExplotacion.getExplotacion());
					// añado la explotacion a la lista
					explotaciones.add(explotacion);
				} catch (XmlException e) {
					logger.error("Error al parsear una explotación.", e);
				}
			}
			currNode = currNode.getNextSibling();
		}
		return explotaciones;
	}

	private static Explotacion buildExplotacionPoliza(
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			final Poliza polizaAgr,
			final es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacionXML) {
		Explotacion explotacion = new Explotacion();
		explotacion.setPoliza(polizaAgr);
		explotacion.setNumero(explotacionXML.getNumero());
		explotacion.setRega(explotacionXML.getRega());
		explotacion.setSigla(explotacionXML.getSigla());
		explotacion.setSubexplotacion(explotacionXML.getSubexplotacion());
		explotacion.setEspecie(Long.valueOf(explotacionXML.getEspecie()));
		explotacion.setRegimen(Long.valueOf(explotacionXML.getRegimen()));
		Termino termino = new Termino();
		Character subtermino;
		if (!StringUtils.nullToString(
				explotacionXML.getUbicacion().getSubtermino()).equals("")) {
			subtermino = explotacionXML.getUbicacion().getSubtermino()
					.charAt(0);
		} else {
			subtermino = ' ';
		}
		TerminoId id = new TerminoId(new BigDecimal(explotacionXML
				.getUbicacion().getProvincia()), new BigDecimal(explotacionXML
				.getUbicacion().getTermino()), subtermino, new BigDecimal(
				explotacionXML.getUbicacion().getComarca()));
		termino.setId(id);
		explotacion.setTermino(termino);
		if (explotacionXML.getCoordenadas() != null) {
			explotacion
					.setLatitud(explotacionXML.getCoordenadas().getLatitud());
			explotacion.setLongitud(explotacionXML.getCoordenadas()
					.getLongitud());
		}
		es.agroseguro.contratacion.explotacion.GrupoRaza[] grupoRazaArr = explotacionXML.getGrupoRazaArray();
		
		Set<GrupoRaza> grupoRazas = new HashSet<GrupoRaza>(grupoRazaArr.length);		
//		GrupoRaza grupoRaza = null;
//		for (es.agroseguro.contratacion.explotacion.GrupoRaza grupoRazaXML : grupoRazaArr) {
//			grupoRaza = new GrupoRaza();
//			grupoRaza
//					.setCodgruporaza(Long.valueOf(grupoRazaXML.getGrupoRaza()));
//			addDatosVariables(datosVariables, grupoRazaXML.getDatosVariables(),
//					dvCodConceptoEtiqueta, grupoRaza);
//			Set<PrecioAnimalesModulo> precioAnimalesModulos = new HashSet<PrecioAnimalesModulo>();
//			es.agroseguro.contratacion.explotacion.CapitalAsegurado[] capitalesAsegurados = grupoRazaXML
//					.getCapitalAseguradoArray();
//			for (es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAseguradoXML : capitalesAsegurados) {
//				grupoRaza.setCodtipocapital(BigDecimal
//						.valueOf(capitalAseguradoXML.getTipo()));
//				addDatosVariables(datosVariables,
//						capitalAseguradoXML.getDatosVariables(),
//						dvCodConceptoEtiqueta, grupoRaza);
//				es.agroseguro.contratacion.explotacion.Animales[] animales = capitalAseguradoXML
//						.getAnimalesArray();
//				for (es.agroseguro.contratacion.explotacion.Animales animalXML : animales) {
//					grupoRaza
//							.setCodtipoanimal(Long.valueOf(animalXML.getTipo()));
//					grupoRaza
//							.setNumanimales(Long.valueOf(animalXML.getNumero()));
//					PrecioAnimalesModulo precioAnimalesModulo = new PrecioAnimalesModulo();
//					precioAnimalesModulo.setCodmodulo(polizaAgr.getCodmodulo());
//					precioAnimalesModulo.setPrecio(animalXML.getPrecio());
//					precioAnimalesModulo.setPrecioMin(animalXML.getPrecio());
//					precioAnimalesModulo.setPrecioMax(animalXML.getPrecio());
//					precioAnimalesModulo.setGrupoRaza(grupoRaza);
//					precioAnimalesModulos.add(precioAnimalesModulo);
//					addDatosVariables(datosVariables,
//							animalXML.getDatosVariables(),
//							dvCodConceptoEtiqueta, grupoRaza);
//				}
//			}
//			grupoRaza.setPrecioAnimalesModulos(precioAnimalesModulos);
//			grupoRaza.setDatosVariables(datosVariables);
//			grupoRaza.setExplotacion(explotacion);
//			grupoRazas.add(grupoRaza);
//		}
//		addDatosVariables(datosVariables, explotacionXML.getDatosVariables(),
//				dvCodConceptoEtiqueta, grupoRaza);
		
		//Esta llamada sustituye al método anterior
		grupoRazas=buildGruposRazas(explotacionXML, dvCodConceptoEtiqueta, explotacion, polizaAgr.getCodmodulo() );
		explotacion.setGrupoRazas(grupoRazas);
		return explotacion;
	}
	
	private static Set<GrupoRaza>buildGruposRazas(
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacionXML,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta, 
			final Explotacion explotacion, String codModulo){
		
		Set<GrupoRaza> setGr = new HashSet<GrupoRaza>(0);		
		es.agroseguro.contratacion.explotacion.GrupoRaza gruposRaza[]= explotacionXML.getGrupoRazaArray();
		
		//Añadimos coberturas de la explotación
		Set<ExplotacionCobertura> coberturas= explotacionXML.getDatosVariables() != null ? getExplotacionesCoberturas(explotacionXML.getDatosVariables().getRiesgCbtoElegArray()) : null;
		if(null!=coberturas){
			//explotacion.getExplotacionCoberturas().clear();//borramos el primer elemento de la colección de inicialización
			explotacion.getExplotacionCoberturas().addAll(coberturas);
		}
		//--------------------------------------------------------------------------------
		
		
		for (es.agroseguro.contratacion.explotacion.GrupoRaza grupoRazaXML : gruposRaza) {
			es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalesAseg[]=grupoRazaXML.getCapitalAseguradoArray();
						
			for (es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAseguradoXML : capitalesAseg) {
				es.agroseguro.contratacion.explotacion.Animales precioAnimales[]=capitalAseguradoXML.getAnimalesArray();
			
				for (es.agroseguro.contratacion.explotacion.Animales animalXML : precioAnimales) {
					Set<DatosVariable> datosVariables = new HashSet<DatosVariable>();
					
					//Datos del grupo de raza
					GrupoRaza grupoRaza = new GrupoRaza();
					grupoRaza.setCodgruporaza(Long.valueOf(grupoRazaXML.getGrupoRaza()));
					addDatosVariables(datosVariables, grupoRazaXML.getDatosVariables(),dvCodConceptoEtiqueta, grupoRaza);
					//Capital asegurado
					grupoRaza.setCodtipocapital(BigDecimal.valueOf(capitalAseguradoXML.getTipo()));
					addDatosVariables(datosVariables, capitalAseguradoXML.getDatosVariables(),
							dvCodConceptoEtiqueta, grupoRaza);
					
					//Precio animales módulo
					grupoRaza.setCodtipoanimal(Long.valueOf(animalXML.getTipo()));
					grupoRaza.setNumanimales(Long.valueOf(animalXML.getNumero()));					
					PrecioAnimalesModulo precioAnimalesModulo = new PrecioAnimalesModulo();
					// MPM - Es un campo obligatorio en BD, si viene a nulo del SW se pone a 1 para que no falle 
					precioAnimalesModulo.setCodmodulo(codModulo != null ? codModulo.trim() : "1");
					precioAnimalesModulo.setPrecio(animalXML.getPrecio());
					precioAnimalesModulo.setPrecioMin(animalXML.getPrecio());
					precioAnimalesModulo.setPrecioMax(animalXML.getPrecio());
					precioAnimalesModulo.setGrupoRaza(grupoRaza);
					grupoRaza.getPrecioAnimalesModulos().clear();//para borrar el elemento de instancia
					grupoRaza.getPrecioAnimalesModulos().add(precioAnimalesModulo) ;
					//precioAnimalesModulos.add(precioAnimalesModulo);
					addDatosVariables(datosVariables, animalXML.getDatosVariables(), dvCodConceptoEtiqueta, grupoRaza);
					
					//grupoRaza.setPrecioAnimalesModulos(precioAnimalesModulos);
					//añadimos los datos variables de la explotación
					addDatosVariables(datosVariables, explotacionXML.getDatosVariables(), dvCodConceptoEtiqueta, grupoRaza);					
					grupoRaza.setDatosVariables(datosVariables);
					grupoRaza.setExplotacion(explotacion);
					setGr.add(grupoRaza);
					
				}
			}		
		}
		
		return setGr;
	}
	
	
	private static Set<ExplotacionCobertura> getExplotacionesCoberturas(
			es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido riesgos[]){		
		Set<ExplotacionCobertura> coberturas =null;
		if (null!=riesgos && riesgos.length>0){
			coberturas = new HashSet<ExplotacionCobertura>(riesgos.length-1);
			for (int i = 0; i < riesgos.length; i++) {
				ExplotacionCobertura cob = new ExplotacionCobertura();				
				cob.setRiesgoCubierto((short) riesgos[i].getCodRCub());
				cob.setCpm((short) riesgos[i].getCPMod());
				cob.setElegida(riesgos[i].getValor().toCharArray()[0]);				
				coberturas.add(cob);
			}
		}
		return coberturas;
	} 

	private static void addDatosVariables(
			final Set<DatosVariable> result,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			final GrupoRaza grupoRaza) {
		if (datosVariables != null && dvCodConceptoEtiqueta != null
				&& !dvCodConceptoEtiqueta.isEmpty()) {
			// 1. Recorrer las claves de auxEtiquetaTabla
			for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
				try {
					// 2. Buscar en los datos variables del capital asegurado el
					// valor correspondiente
					// primero obtengo el objeto que representa al dato variable
					Class<?> clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
					Method method = clase.getMethod("get"
							+ dvCodConceptoEtiqueta.get(codconcepto)
									.getEtiqueta());
					Object objeto = method.invoke(datosVariables);
					if (objeto != null) {
						// después obtengo el valor que tiene el objeto en el
						// dato variable.
						Class<?> claseValor = objeto.getClass();
						Method methodValor = claseValor.getMethod("getValor");
						Object valor = methodValor.invoke(objeto);
						// 3. asigno el valor al dato variable
						if (!StringUtils.nullToString(valor).equals("")) {
							DatosVariable datoVariable = new DatosVariable();
							datoVariable.setCodconcepto(codconcepto.intValue());
							if (valor instanceof XmlCalendar) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd");
								SimpleDateFormat sdf2 = new SimpleDateFormat(
										"dd/MM/yyyy");
								Date d = new Date();
								String fecha = "";
								try {
									d = sdf.parse(valor.toString());
									fecha = sdf2.format(d);
								} catch (ParseException e) {
									logger.error("Error al parsear la fecha en los datos variables", e);
								}
								datoVariable.setValor(fecha);
							} else {
								datoVariable.setValor(StringUtils
										.nullToString(valor));
							}
							if (grupoRaza != null) {
								datoVariable.setGrupoRaza(grupoRaza);
							}
							if (result != null) {
								result.add(datoVariable);
							}
						}
					}
				} catch (SecurityException e) {
					logger.debug("Error de seguridad " + e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.debug("El método no existe para esta clase "
							+ e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.debug("El método acepta los argumentos "
							+ e.getMessage());
				} catch (IllegalAccessException e) {
					logger.debug("Error " + e.getMessage());
				} catch (InvocationTargetException e) {
					logger.debug("Error " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Método para obtener una lista de parcelas de anexo a partir de los datos
	 * de la situación actualizada de agroseguro.
	 * 
	 * @param poliza
	 *            Poliza actualizada de Agroseguro
	 * @return Listado de parcelas para un anexo
	 * @throws XmlException 
	 */
	public static List<Parcela> getParcelasPolizaFromPolizaActualizada(
			final es.agroseguro.contratacion.Poliza poliza,
			final Long idPoliza,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta) throws XmlException {
		
		/*****/
		/* TAM (12.06.2020 */
		List<Parcela> parcelasAnexo = new ArrayList<Parcela>(); 
		Poliza polizaAgr = new Poliza(idPoliza);
		
				//Recorremos las explotaciones de la situación actualizada y vamos creando las explotaciones del anexo
		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();
		
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				
				es.agroseguro.contratacion.parcela.ParcelaDocument  xmlParcela = null;
				xmlParcela = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode);

				if (xmlParcela != null){
					Parcela parcela = buildParcelaPoliza(dvCodConceptoEtiqueta, polizaAgr, xmlParcela);
	
					//añado la parcela a la lista
					parcelasAnexo.add(parcela);
				}
			}
			currNode = currNode.getNextSibling();
		}
		
		return parcelasAnexo;
		
	}
	
	/* Pet. 57626 ** MODIF TAM (09.07.2020) ** Inicio */
	
	/**
	 * Método para obtener una lista de parcelas de anexo a partir de los datos
	 * de la situación actualizada de agroseguro, cuando se recupera el formato Antiguo
	 * 
	 * @param poliza
	 *            Poliza actualizada de Agroseguro
	 * @return Listado de parcelas para un anexo
	 */
	public static List<Parcela> getParcelasPolizaFromPolizaActualizadaAnt(
			final es.agroseguro.seguroAgrario.contratacion.Poliza poliza,
			final Long idPoliza,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta) {
		List<Parcela> parcelasAnexo = new ArrayList<Parcela>();
		Poliza polizaAgr = new Poliza(idPoliza);
		// Recorremos las parcelas de la situación actualizada y vamos creando
		// las parcelas del anexo
		for (es.agroseguro.seguroAgrario.contratacion.Parcela par : poliza
				.getObjetosAsegurados().getParcelaArray()) {
			Parcela parcela = buildParcelaPolizaAnt(dvCodConceptoEtiqueta,
					polizaAgr, par);
			// añado la parcela a la lista
			parcelasAnexo.add(parcela);
		}
		return parcelasAnexo;
	}
	
	/**
	 * Método para obtener un objeto parcela de anexo a partir de los datos de
	 * la parcela en la situación actualizada
	 * 
	 * @param dvCodConceptoEtiqueta
	 *            Datos variables que aplican para esta línea
	 * @param anexo
	 *            Anexo asociado a la parcela
	 * @param par
	 *            Parcela de la situación actualizada
	 * @return Parcela del anexo con los datos de la situación actualizada
	 */
	private static Parcela buildParcelaPolizaAnt(
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			final Poliza poliza,
			final es.agroseguro.seguroAgrario.contratacion.Parcela par) {
		Parcela parcela = new Parcela();
		parcela.setPoliza(poliza);
		parcela.setParcela(null);
		parcela.setHoja(new Integer(par.getHoja()));
		parcela.setNumero(new Integer(par.getNumero()));
		parcela.setNomparcela(par.getNombre());
		Termino termino = new Termino();
		Character subtermino;
		if (!StringUtils.nullToString(par.getUbicacion().getSubtermino())
				.equals("")) {
			subtermino = par.getUbicacion().getSubtermino().charAt(0);
		} else {
			subtermino = ' ';
		}
		TerminoId id = new TerminoId(new BigDecimal(par.getUbicacion()
				.getProvincia()), new BigDecimal(par.getUbicacion()
				.getTermino()), subtermino, new BigDecimal(par.getUbicacion()
				.getComarca()));
		termino.setId(id);
		parcela.setTermino(termino);
		parcela.setCodcultivo(new BigDecimal(par.getCosecha().getCultivo()));
		parcela.setCodvariedad(new BigDecimal(par.getCosecha().getVariedad()));
		parcela.setCodprovsigpac(new BigDecimal(par.getSIGPAC().getProvincia()));
		parcela.setCodtermsigpac(new BigDecimal(par.getSIGPAC().getTermino()));
		parcela.setAgrsigpac(new BigDecimal(par.getSIGPAC().getAgregado()));
		parcela.setZonasigpac(new BigDecimal(par.getSIGPAC().getZona()));
		parcela.setPoligonosigpac(new BigDecimal(par.getSIGPAC().getPoligono()));
		parcela.setParcelasigpac(new BigDecimal(par.getSIGPAC().getParcela()));
		parcela.setRecintosigpac(new BigDecimal(par.getSIGPAC().getRecinto()));
		parcela.setTipoparcela(Constants.TIPO_PARCELA_PARCELA); // ??????????????????
																// => ver en
																// función de
																// los tipos de
																// capital
																// cuando sean
																// instalaciones
																// hay que
																// calcular la
																// parcela
																// asociada
		// Rellenamos los capitales asegurados
		for (CapitalAsegurado ca : par.getCosecha().getCapitalesAsegurados()
				.getCapitalAseguradoArray()) {
			com.rsi.agp.dao.tables.poliza.CapitalAsegurado capitalAsegurado = new com.rsi.agp.dao.tables.poliza.CapitalAsegurado();
			capitalAsegurado.setParcela(parcela);
			capitalAsegurado.setTipoCapital(new TipoCapital(new BigDecimal(ca
					.getTipo()), null));
			capitalAsegurado.setSuperficie(ca.getSuperficie());
			capitalAsegurado.setPrecio(ca.getPrecio());
			capitalAsegurado.setProduccion(new BigDecimal(ca.getProduccion()));
			// Rellenamos los datos variables
			// 1. Recorrer las claves de auxEtiquetaTabla
			for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
				try {
					// 2. Buscar en los datos variables del capital asegurado el
					// valor correspondiente
					// primero obtengo el objeto que representa al dato variable
					Class<?> clase = es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables.class;
					Method method = clase.getMethod("get"
							+ dvCodConceptoEtiqueta.get(codconcepto)
									.getEtiqueta());
					Object objeto = method.invoke(ca.getDatosVariables());
					if (objeto != null) {
						// después obtengo el valor que tiene el objeto en el
						// dato variable.
						Class<?> claseValor = objeto.getClass();
						Method methodValor = claseValor.getMethod("getValor");
						Object valor = methodValor.invoke(objeto);
						// 3. asigno el valor al dato variable
						if (!StringUtils.nullToString(valor).equals("")) {
							DatoVariableParcela datoVariable = new DatoVariableParcela();
							datoVariable.getDiccionarioDatos().setCodconcepto(
									codconcepto);
							if (valor instanceof XmlCalendar) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd");
								SimpleDateFormat sdf2 = new SimpleDateFormat(
										"dd/MM/yyyy");
								Date d = new Date();
								String fecha = "";
								try {
									d = sdf.parse(valor.toString());
									fecha = sdf2.format(d);
								} catch (ParseException e) {
									logger.error("Error al parsear la fecha en los datos variables", e);
								}
								datoVariable.setValor(fecha);
							} else {
								datoVariable.setValor(StringUtils
										.nullToString(valor));
							}
							datoVariable.setCapitalAsegurado(capitalAsegurado);

							capitalAsegurado.getDatoVariableParcelas().add(
									datoVariable);
						}
					}
				} catch (SecurityException e) {
					logger.debug("Error de seguridad " + e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.debug("El método no existe para esta clase "
							+ e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.debug("El método acepta los argumentos "
							+ e.getMessage());
				} catch (IllegalAccessException e) {
					logger.debug("Error " + e.getMessage());
				} catch (InvocationTargetException e) {
					logger.debug("Error " + e.getMessage());
				}
			}
			// asigno el capital asegurado a la parcela
			parcela.getCapitalAsegurados().add(capitalAsegurado);
		}
		return parcela;
	}
	
	/* Pet. 57626 ** MODIF TAM (09.07.2020) ** Fin */

	/**
	 * Método para obtener un objeto parcela de anexo a partir de los datos de
	 * la parcela en la situación actualizada
	 * 
	 * @param dvCodConceptoEtiqueta
	 *            Datos variables que aplican para esta línea
	 * @param anexo
	 *            Anexo asociado a la parcela
	 * @param par
	 *            Parcela de la situación actualizada
	 * @return Parcela del anexo con los datos de la situación actualizada
	 */
	private static Parcela buildParcelaPoliza(
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			final Poliza poliza,
			final es.agroseguro.contratacion.parcela.ParcelaDocument xmlParcelaAnexo) {
		
		Parcela parcela = new Parcela();
		
		
		if (xmlParcelaAnexo != null && xmlParcelaAnexo.getParcela() != null) {
			
			es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela par = xmlParcelaAnexo.getParcela();
		
			parcela.setPoliza(poliza);
			parcela.setParcela(null);
			parcela.setHoja(new Integer(par.getHoja()));
			parcela.setNumero(new Integer(par.getNumero()));
			parcela.setNomparcela(par.getNombre());
			Termino termino = new Termino();
			Character subtermino;
			if (!StringUtils.nullToString(par.getUbicacion().getSubtermino())
					.equals("")) {
				subtermino = par.getUbicacion().getSubtermino().charAt(0);
			} else {
				subtermino = ' ';
			}
			TerminoId id = new TerminoId(new BigDecimal(par.getUbicacion()
					.getProvincia()), new BigDecimal(par.getUbicacion()
					.getTermino()), subtermino, new BigDecimal(par.getUbicacion()
					.getComarca()));
			termino.setId(id);
			parcela.setTermino(termino);
			parcela.setCodcultivo(new BigDecimal(par.getCosecha().getCultivo()));
			parcela.setCodvariedad(new BigDecimal(par.getCosecha().getVariedad()));
			parcela.setCodprovsigpac(new BigDecimal(par.getSIGPAC().getProvincia()));
			parcela.setCodtermsigpac(new BigDecimal(par.getSIGPAC().getTermino()));
			parcela.setAgrsigpac(new BigDecimal(par.getSIGPAC().getAgregado()));
			parcela.setZonasigpac(new BigDecimal(par.getSIGPAC().getZona()));
			parcela.setPoligonosigpac(new BigDecimal(par.getSIGPAC().getPoligono()));
			parcela.setParcelasigpac(new BigDecimal(par.getSIGPAC().getParcela()));
			parcela.setRecintosigpac(new BigDecimal(par.getSIGPAC().getRecinto()));
			parcela.setTipoparcela(Constants.TIPO_PARCELA_PARCELA); // ??????????????????
																	// => ver en  función de  los tipos de
																	// capital  cuando sean  instalaciones
																	// hay que  calcular la  parcela
																	// asociada
			// Rellenamos los capitales asegurados
			for (es.agroseguro.contratacion.parcela.CapitalAsegurado ca : par.getCosecha().getCapitalesAsegurados()
					.getCapitalAseguradoArray()) {
				com.rsi.agp.dao.tables.poliza.CapitalAsegurado capitalAsegurado = new com.rsi.agp.dao.tables.poliza.CapitalAsegurado();
				capitalAsegurado.setParcela(parcela);
				capitalAsegurado.setTipoCapital(new TipoCapital(new BigDecimal(ca
						.getTipo()), null));
				capitalAsegurado.setSuperficie(ca.getSuperficie());
				capitalAsegurado.setPrecio(ca.getPrecio());
				capitalAsegurado.setProduccion(new BigDecimal(ca.getProduccion()));
				// Rellenamos los datos variables
				// 1. Recorrer las claves de auxEtiquetaTabla
				for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
					try {
						// 2. Buscar en los datos variables del capital asegurado el
						// valor correspondiente
						// primero obtengo el objeto que representa al dato variable
						Class<?> clase = es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables.class;
						Method method = clase.getMethod("get"
								+ dvCodConceptoEtiqueta.get(codconcepto)
										.getEtiqueta());
						Object objeto = method.invoke(ca.getDatosVariables());
						if (objeto != null) {
							// después obtengo el valor que tiene el objeto en el
							// dato variable.
							Class<?> claseValor = objeto.getClass();
							Method methodValor = claseValor.getMethod("getValor");
							Object valor = methodValor.invoke(objeto);
							// 3. asigno el valor al dato variable
							if (!StringUtils.nullToString(valor).equals("")) {
								DatoVariableParcela datoVariable = new DatoVariableParcela();
								datoVariable.getDiccionarioDatos().setCodconcepto(
										codconcepto);
								if (valor instanceof XmlCalendar) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
									Date d = new Date();
									String fecha = "";
									try {
										d = sdf.parse(valor.toString());
										fecha = sdf2.format(d);
									} catch (ParseException e) {
										logger.error("Error al parsear la fecha en los datos variables", e);
									}
									datoVariable.setValor(fecha);
								} else {
									datoVariable.setValor(StringUtils.nullToString(valor));
								}
								datoVariable.setCapitalAsegurado(capitalAsegurado);
	
								capitalAsegurado.getDatoVariableParcelas().add(datoVariable);
							}
						}
					} catch (SecurityException e) {
						logger.debug("Error de seguridad " + e.getMessage());
					} catch (NoSuchMethodException e) {
						logger.debug("El método no existe para esta clase "
								+ e.getMessage());
					} catch (IllegalArgumentException e) {
						logger.debug("El método acepta los argumentos "
								+ e.getMessage());
					} catch (IllegalAccessException e) {
						logger.debug("Error " + e.getMessage());
					} catch (InvocationTargetException e) {
						logger.debug("Error " + e.getMessage());
					}
				}
				// asigno el capital asegurado a la parcela
				parcela.getCapitalAsegurados().add(capitalAsegurado);
			}
		}/* Fin del if */	
		return parcela;
	}
}