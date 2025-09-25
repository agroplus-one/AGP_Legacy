package com.rsi.agp.core.managers.impl.anexoMod.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.util.CollectionsAndMapsUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.util.PolizaUnificadaTransformer;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.anexo.AnexoModBonifRecargos;
import com.rsi.agp.dao.tables.anexo.AnexoModDistribucionCostes;
import com.rsi.agp.dao.tables.anexo.AnexoModDistribucionCostesId;
import com.rsi.agp.dao.tables.anexo.AnexoModSubvCCAA;
import com.rsi.agp.dao.tables.anexo.AnexoModSubvEnesa;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;

import es.agroseguro.contratacion.Cuenta;
import es.agroseguro.contratacion.CuentaCobroSiniestros;
import es.agroseguro.contratacion.ObjetosAsegurados;
import es.agroseguro.contratacion.Pago;
import es.agroseguro.contratacion.Poliza;
import es.agroseguro.contratacion.costePoliza.BonificacionRecargo;
import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.costePoliza.SubvencionCCAA;
import es.agroseguro.contratacion.costePoliza.SubvencionEnesa;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido;
import es.agroseguro.contratacion.datosVariables.TipoAseguradoGanado;
import es.agroseguro.contratacion.declaracionSubvenciones.SeguridadSocial;
import es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada;
import es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas;
import es.agroseguro.contratacion.explotacion.Animales;
import es.agroseguro.contratacion.explotacion.CapitalAsegurado;
import es.agroseguro.contratacion.explotacion.Coordenadas;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion;
import es.agroseguro.contratacion.explotacion.GrupoRaza;
import es.agroseguro.iTipos.Ambito;
import es.agroseguro.iTipos.Gastos;

@SuppressWarnings("rawtypes")
public class PolizaActualizadaGanadoTranformer {
	
	private static final Log logger = LogFactory.getLog(PolizaActualizadaGanadoTranformer.class);
	/**
	 * Modifica el objeto Poliza con los datos incluidos en el anexo de modificación
	 * @param poliza
	 * @param anexo
	 * @throws ValidacionPolizaException 
	 */
	public static void generarPolizaSituacionFinalCompleta(final Poliza poliza, final AnexoModificacion anexo, final Map<BigDecimal,
			List<String>> listaDatosVariables, final List<BigDecimal> listaCPM, final GenericDao genericDao, final boolean hayCambiosDatosAsegurado) throws ValidacionPolizaException {
		
		comprobarModificacionesGN(poliza, anexo, genericDao);
		
		// OBJETOS ASEGURADOS
		if (!CollectionsAndMapsUtil.isEmpty(anexo.getExplotacionAnexos())){
			poliza.setObjetosAsegurados(getObjetosAsegurados(anexo, listaDatosVariables, genericDao));
		}
		// SUBVENCIONES
		if (!CollectionsAndMapsUtil.isEmpty(anexo.getSubvDeclaradas())){
			//poliza.setSubvencionesDeclaradas(getSubvencionesDeclaradas(anexo, poliza));
			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvsDeclaradas = getSubvencionesDeclaradas(anexo, poliza);
			logger.debug(subvsDeclaradas);
			if (subvsDeclaradas == null || subvsDeclaradas.getSubvencionDeclaradaArray() == null
					|| subvsDeclaradas.getSubvencionDeclaradaArray().length == 0) {
				logger.debug("unset de subvenciones");
				if (poliza.getSubvencionesDeclaradas() != null) 
					poliza.unsetSubvencionesDeclaradas();
			} else {
				logger.debug("asignación de subvenciones");
				poliza.setSubvencionesDeclaradas(subvsDeclaradas);
			}
		}
		// COBERTURAS
		// Guardamos el posible Tipo Asegurado Ganado antes de modificar las coberturas
		TipoAseguradoGanado tAseGan = null;
		
		
		if (poliza.getCobertura() != null && poliza.getCobertura().getDatosVariables() != null) {
			tAseGan = poliza.getCobertura().getDatosVariables().getTAseGan();	
		}			
		
		for (Cobertura c: anexo.getCoberturas()) {
			if (c.getCodconcepto().intValue() == ConstantsConceptos.CODCPTO_TIPO_ASEG_GAN) {
				tAseGan.setValor(Integer.parseInt(c.getCodvalor()));
				break;
			}
		}
		
		boolean hayCoberturasAnex = !CollectionsAndMapsUtil.isEmpty(anexo.getCoberturas());
		boolean moduloCambiado = !anexo.getCodmodulo().trim()
				.equalsIgnoreCase(poliza.getCobertura().getModulo().trim());
		boolean coberturasDiferentes = AnexoModificacionUtils.tieneCambiosCoberturas(anexo,
				poliza);
		logger.debug("hayCoberturasAnex: " + hayCoberturasAnex);
		logger.debug("moduloCambiado: " + moduloCambiado);
		logger.debug("coberturasDiferentes: " + coberturasDiferentes);
		if (hayCoberturasAnex || moduloCambiado || coberturasDiferentes) {
			poliza.setCobertura(PolizaUnificadaTransformer.getCoberturasAnexo(anexo, listaCPM, tAseGan));
		}
		//PAGOS: Incluir si se ha modificado el IBAN
		if(ObjectUtils.equals(anexo.getEsIbanAsegModificado(),1)){
			Pago pago = poliza.getPago();
			Cuenta cuenta = Cuenta.Factory.newInstance();
			//cuenta.setIban(am.getIbanAseg());
			cuenta.setIban(anexo.getIbanAsegModificado());
			cuenta.setDestinatario("A");
			pago.setCuenta(cuenta);
			poliza.setPago(pago);
		}
		
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (26/02/2021) * Inicio */ 
		// PAGOS: Incluir si se ha modificado el IBAN de Cuenta Cobro siniestros si se ha modificado
		if(ObjectUtils.equals(anexo.getEsIban2AsegModificado(),1)){
			//ESC-31952
			//CuentaCobroSiniestros cuentaCobroSin = poliza.getCuentaCobroSiniestros();
			CuentaCobroSiniestros cuentaCobroSin = CuentaCobroSiniestros.Factory.newInstance();
			//ESC-31952
			cuentaCobroSin.setIban(anexo.getIban2AsegModificado());
			poliza.setCuentaCobroSiniestros(cuentaCobroSin);
		}
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (26/02/2021) * Fin */ 
		
		if (hayCambiosDatosAsegurado) {
			logger.debug("Hay cambios en los datos del asegurado respecto a la situacion actualizada, se modifica el XML de envio al SW");
			actualizarDatosAseguradoSiHayCambios(anexo, poliza);
		}
	}
	
	/**
	 * Si hay diferencias entre los datos actuales del asegurado en agroplus y los datos de la situación actualizada,
	 * los datos del asegurado en la situación actualizada se modifican por los de Agroplus para su envío al SW.
	 *
	 * @param anexo El objeto Anexo que contiene la información actual del asegurado en Agroplus.
	 * @param poliza El objeto Poliza que contiene la información del asegurado en la situación actualizada.
	 */
	public static void actualizarDatosAseguradoSiHayCambios(AnexoModificacion anexo, Poliza poliza) {

	        Asegurado aseguradoAnexo = anexo.getPoliza().getAsegurado();

	        if (aseguradoAnexo != null) {
	            poliza.getAsegurado().setNif(aseguradoAnexo.getNifcif() != null ? aseguradoAnexo.getNifcif() : "");
	            poliza.getAsegurado().getNombreApellidos().setNombre(aseguradoAnexo.getNombre() != null ? aseguradoAnexo.getNombre() : "");
	            poliza.getAsegurado().getNombreApellidos().setApellido1(aseguradoAnexo.getApellido1() != null ? aseguradoAnexo.getApellido1() : "");
	            poliza.getAsegurado().getNombreApellidos().setApellido2(aseguradoAnexo.getApellido2() != null ? aseguradoAnexo.getApellido2() : "");
	            
	            String via = aseguradoAnexo.getVia() != null && aseguradoAnexo.getVia().getAbreviatura() != null && aseguradoAnexo.getDireccion() != null ? aseguradoAnexo.getVia().getAbreviatura() + " " + aseguradoAnexo.getDireccion() : "";
	            poliza.getAsegurado().getDireccion().setVia(via);

	            poliza.getAsegurado().getDireccion().setNumero(aseguradoAnexo.getNumvia() != null ? aseguradoAnexo.getNumvia() : "");
	            poliza.getAsegurado().getDireccion().setPiso(aseguradoAnexo.getPiso() != null ? aseguradoAnexo.getPiso() : "");
	            poliza.getAsegurado().getDireccion().setBloque(aseguradoAnexo.getBloque() != null ? aseguradoAnexo.getBloque() : "");
	            poliza.getAsegurado().getDireccion().setEscalera(aseguradoAnexo.getEscalera() != null ? aseguradoAnexo.getEscalera() : "");

	            if (aseguradoAnexo.getLocalidad() != null && aseguradoAnexo.getLocalidad().getProvincia() != null && aseguradoAnexo.getLocalidad().getProvincia().getCodprovincia() != null) {
	                poliza.getAsegurado().getDireccion().setProvincia(aseguradoAnexo.getLocalidad().getProvincia().getCodprovincia().intValue());
	            } else {
	                poliza.getAsegurado().getDireccion().setProvincia(0);
	            }

	            poliza.getAsegurado().getDireccion().setLocalidad(aseguradoAnexo.getLocalidad() != null && aseguradoAnexo.getLocalidad().getNomlocalidad() != null ? aseguradoAnexo.getLocalidad().getNomlocalidad() : "");

	            if (aseguradoAnexo.getTelefono() != null) {
	                try {
	                    poliza.getAsegurado().getDatosContacto().setTelefonoFijo(Integer.parseInt(aseguradoAnexo.getTelefono()));
	                } catch (NumberFormatException e) {
	                    logger.error("Error al convertir el número de teléfono del asegurado a un entero. Se utilizará el valor predeterminado 0. Valor original: ");
	                    poliza.getAsegurado().getDatosContacto().setTelefonoFijo(0);
	                }
	            } else {
	                poliza.getAsegurado().getDatosContacto().setTelefonoFijo(0);
	            }

	            if (aseguradoAnexo.getMovil() != null) {
	                try {
	                    poliza.getAsegurado().getDatosContacto().setTelefonoMovil(Integer.parseInt(aseguradoAnexo.getMovil()));
	                } catch (NumberFormatException e) {
	                    logger.error("Error al convertir el número de teléfono del asegurado a un entero. Se utilizará el valor predeterminado 0. Valor original: ");
	                    poliza.getAsegurado().getDatosContacto().setTelefonoMovil(0);
	                }
	            } else {
	                poliza.getAsegurado().getDatosContacto().setTelefonoMovil(0);
	            }
	        }
	}
	
	private static void comprobarModificacionesGN(Poliza poliza, AnexoModificacion anexo, GenericDao genericDao) {
		logger.debug("Comprobando si ha habido cambios en los grupos de negocio de la poliza");
		logger.debug(new StringBuilder("Poliza\n").append(poliza.toString()));
		
		//rellenamos los de la poliza
		List<String> gruposNegocioPoliza = gruposNegocioGastosPoliza(poliza.getEntidad().getGastosArray());
		//rellenamos los del anexo
		List<String> gruposNegocioAnexo = gruposNegocioAnexo(anexo.getExplotacionAnexos(), genericDao);
		List<String> gruposNegocioAnexoHelper = new ArrayList<String>(gruposNegocioAnexo); 
		//compobamos si hay cambios
		
		boolean anexoContienePoliza = gruposNegocioAnexo.containsAll(gruposNegocioPoliza);
		boolean polizaContieneAnexo = gruposNegocioPoliza.containsAll(gruposNegocioAnexo);
		boolean polizaSinGastos = gruposNegocioPoliza.isEmpty();
		if (polizaContieneAnexo && anexoContienePoliza) {
			logger.debug("No hay cambios en los grupos de negocio de la póliza");
		} else {
			//comprobación para ver si se han añadido
			logger.debug("Hay cambios en los grupos de negocio de la poliza");
			gruposNegocioAnexo.removeAll(gruposNegocioPoliza);
			if(gruposNegocioAnexo.size() > 0 ){
				//se han añadido grupos en el anexo;	
				logger.debug("Se han añadido grupos en el anexo");
				//añadimos gastoss
				etiquetaGastos(poliza, anexo, gruposNegocioAnexo, genericDao, polizaSinGastos);
				
				//añadimos los grupos de negocio que tenemos en el xml a una lista 
				//para luego comprobar que si ya lo tenemos y si es asi no lo volvemos a insertar
				List<String> gruposNegocioCostesPoliza = gruposNegocioCostesPoliza(poliza.getCostePoliza());
				//añadimos costes
				etiquetaCostes(poliza, anexo, gruposNegocioAnexo, gruposNegocioCostesPoliza);
			}		 
			//comprobación para ver si se han eliminado.
			gruposNegocioPoliza.removeAll(gruposNegocioAnexoHelper);
			if(gruposNegocioPoliza.size() > 0){
				//se han eliminado grupos en el anexo;							 
				//gastos
				eliminarGastos(poliza, gruposNegocioPoliza);
				//costes
				eliminarCostes(poliza, gruposNegocioPoliza);
			}
		}
	}

	private static boolean puedeTenerEtiquetaGastos(ObjetosAsegurados objetosAsegurados, 
			Long idAnexo, GenericDao genericDao) {
		boolean resultado = false;
		try {
			if(genericDao instanceof IAnexoModificacionDao){
				if(((IAnexoModificacionDao) genericDao).checkExplotacionesTCRyD(idAnexo)){
					List<Integer> tipos = tiposCapitalAseguradoPoliza(objetosAsegurados);
					resultado = !((IAnexoModificacionDao) genericDao).checkTCRyD(tipos);
				}
			}
		} catch (DAOException e) {
			logger.error("Excepcion : PolizaActualizadaGanadoTranformer - puedeTenerEtiquetaGastos", e);
		} catch (XmlException e) {
			logger.error("Excepcion : PolizaActualizadaGanadoTranformer - puedeTenerEtiquetaGastos", e);
		}
		return resultado;
	}

	private static List<Integer> tiposCapitalAseguradoPoliza(ObjetosAsegurados objetosAsegurados) 
			throws XmlException {
		List<Integer> tipos = new ArrayList<Integer>();
		Node currNode = objetosAsegurados.getDomNode().getFirstChild();
		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				ExplotacionDocument xmlExplotacion = null;
				xmlExplotacion = ExplotacionDocument.Factory.parse(currNode);				
				if (xmlExplotacion != null){
					for(GrupoRaza gr : xmlExplotacion.getExplotacion().getGrupoRazaArray()){
						for(CapitalAsegurado ca : gr.getCapitalAseguradoArray()){
							tipos.add(new Integer(ca.getTipo()));
						}
					}
					break;
				}
			}
			currNode = currNode.getNextSibling();
		}
		return tipos;
	}
	
	private static List<String> gruposNegocioCostesPoliza(CostePoliza costePoliza) {
		List<String> gruposNegocioCostesPoliza = new ArrayList<String>();
		if (costePoliza !=  null){
			for (CosteGrupoNegocio cgn : costePoliza.getCosteGrupoNegocioArray()){
				gruposNegocioCostesPoliza.add(cgn.getGrupoNegocio().toString());
			}
		} else {
			logger.debug("objeto poliza sin costePoliza");
		}
		return gruposNegocioCostesPoliza;
	}

	private static void eliminarCostes(Poliza poliza, List<String> gruposDePoliza) {
		List<CosteGrupoNegocio> listaCostes = new ArrayList<CosteGrupoNegocio>(Arrays.asList(poliza.getCostePoliza().getCosteGrupoNegocioArray()));
		for(int i = 0; i < listaCostes.size(); i++){
			String grupoNegocio = listaCostes.get(i).getGrupoNegocio();
			if(gruposDePoliza.contains(grupoNegocio)){
				listaCostes.remove(i);
			}
		}
		poliza.getCostePoliza().setCosteGrupoNegocioArray(listaCostes.toArray(new CosteGrupoNegocio[listaCostes.size()]));
	}

	private static void eliminarGastos(Poliza poliza, List<String> gruposDePoliza) {
		List<Gastos> listaGastos = new ArrayList<Gastos>(Arrays.asList(poliza.getEntidad().getGastosArray()));
				
		for(int i = 0; i < listaGastos.size(); i++){
			String grupoNegocio = listaGastos.get(i).getGrupoNegocio();
			if(gruposDePoliza.contains(grupoNegocio)){
				listaGastos.remove(i);
			}
		}
		poliza.getEntidad().setGastosArray(listaGastos.toArray(new Gastos[listaGastos.size()]));
	}


	private static void etiquetaCostes(Poliza poliza, AnexoModificacion anexo, List<String> gruposDelAnexo, List<String> gruposNegocioCostesPoliza) {
		logger.debug("TMR - añadimos costes ");
		
		for(AnexoModDistribucionCostes adc : anexo.getAnexoModDistribucionCosteses()){
			logger.debug("TMR - GRUPO DE NEGOCIO DEL ANEXO " + adc.getId().getGrupoNegocio().toString());
			logger.debug("TMR - TIPO DC DEL ANEXO " + adc.getId().getTipoDc());
			AnexoModDistribucionCostesId adcId = adc.getId();
			if(gruposDelAnexo.contains(adcId.getGrupoNegocio().toString()) && adcId.getTipoDc() ==0
					&& !(gruposNegocioCostesPoliza.contains(adcId.getGrupoNegocio().toString()))){
								 
				CosteGrupoNegocio cgn = CosteGrupoNegocio.Factory.newInstance();
				cgn.setGrupoNegocio(adc.getId().getGrupoNegocio().toString());
				cgn.setPrimaComercial(adc.getPrimaComercial());
				cgn.setPrimaComercialNeta(adc.getPrimaComercialNeta());
				cgn.setRecargoConsorcio(adc.getRecargoConsorcio());
				cgn.setReciboPrima(adc.getReciboPrima());
				cgn.setCosteTomador(adc.getCosteTomador());
					 
				// falta recorrer los 3 iterarator y completar.
				
				//recargos				
				List <BonificacionRecargo> recargosList = new ArrayList<BonificacionRecargo>();
				for(AnexoModBonifRecargos anexoModBonifRecargos : adc.getAnexoModBonifRecargoses()){
					BonificacionRecargo br=BonificacionRecargo.Factory.newInstance();
					br.setImporte(anexoModBonifRecargos.getImporte());
					br.setCodigo(anexoModBonifRecargos.getId().getCodigo());
					recargosList.add(br);
				}
				cgn.setBonificacionRecargoArray(recargosList.toArray(new BonificacionRecargo[recargosList.size()]));
				
				//CCAA
				List <SubvencionCCAA> ccaaList = new ArrayList<SubvencionCCAA>();
				for(AnexoModSubvCCAA anexoModSubvCCAA : adc.getAnexoModSubvCCAAs()){
					SubvencionCCAA ccaa = SubvencionCCAA.Factory.newInstance();
					ccaa.setCodigoOrganismo(anexoModSubvCCAA.getId().getCodOrganismo().toString());
					ccaa.setImporte(anexoModSubvCCAA.getImporte());
					ccaaList.add(ccaa);
				}
				cgn.setSubvencionCCAAArray(ccaaList.toArray(new SubvencionCCAA[ccaaList.size()]));
				
				//enesa
				List <SubvencionEnesa> enesaList = new ArrayList<SubvencionEnesa>();
				for(AnexoModSubvEnesa anexoModSubvEnesas : adc.getAnexoModSubvEnesas()){
					SubvencionEnesa subvencionEnesa = SubvencionEnesa.Factory.newInstance();
					subvencionEnesa.setImporte(anexoModSubvEnesas.getImporte());
				}
				cgn.setSubvencionEnesaArray(enesaList.toArray(new SubvencionEnesa[enesaList.size()]));
									 
				//añadimos el coste
				poliza.getCostePoliza().insertNewCosteGrupoNegocio(poliza.getCostePoliza().getCosteGrupoNegocioArray().length);
				poliza.getCostePoliza().setCosteGrupoNegocioArray(poliza.getCostePoliza().getCosteGrupoNegocioArray().length-1, cgn);
				logger.debug("TMR - ARRAY COSTE POLIZA " + poliza.getCostePoliza().getCosteGrupoNegocioArray().toString());
			}
		}
	}
	
	private static void etiquetaGastos(Poliza poliza, AnexoModificacion anexo, 
			List<String> gruposDelAnexo, GenericDao genericDao, boolean polizaSinGastos) {
		boolean puedeTenerEtiquetaGastosRyD = puedeTenerEtiquetaGastos(poliza.getObjetosAsegurados(), anexo.getId(), genericDao);
		Set<PolizaPctComisiones> comisionesPoliza = anexo.getPoliza().getSetPolizaPctComisiones();		
		for(PolizaPctComisiones pctCom : comisionesPoliza){
			boolean esComisionRyD = pctCom.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_RYD);
			boolean esComisionVida = pctCom.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_VIDA);
			if(polizaSinGastos && esComisionVida){
				ponerGastos(poliza, anexo, pctCom);
			}
			if(puedeTenerEtiquetaGastosRyD && esComisionRyD){
				ponerGastos(poliza, anexo, pctCom);
				break;
			}
		}
	}
	
	private static void ponerGastos(Poliza poliza, AnexoModificacion anexo,
			PolizaPctComisiones pctCom) {
		PolizaPctComisiones pc = generarPolizaPctComisiones(pctCom, anexo.getPctdescelegido(), anexo.getPctrecarelegido());
		BigDecimal comMediador = obtenerComisionMediador(pc);
		Gastos gas = generarEtiquetaGastos(pctCom, comMediador);
		anexo.setPctadministracion(pctCom.getPctadministracion());
		anexo.setPctadquisicion(pctCom.getPctadquisicion());
		anexo.setPctcomisionmediador(comMediador);
		poliza.getEntidad().insertNewGastos(poliza.getEntidad().getGastosArray().length);
		poliza.getEntidad().setGastosArray(poliza.getEntidad().getGastosArray().length - 1, gas);
	}

	private static Gastos generarEtiquetaGastos(PolizaPctComisiones pctCom, BigDecimal comMediador) {
		Gastos gas = Gastos.Factory.newInstance();	
		if (pctCom.getPctadministracion() != null) {
			gas.setAdministracion(pctCom.getPctadministracion().setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			gas.setAdministracion(Constants.CERO);
		}
		if (pctCom.getPctadquisicion() != null) {
			gas.setAdquisicion(pctCom.getPctadquisicion().setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			gas.setAdquisicion(Constants.CERO);
		}
		if (comMediador != null) {
			gas.setComisionMediador(comMediador);

		} else {
			gas.setComisionMediador(Constants.CERO);
		}
		gas.setGrupoNegocio(pctCom.getGrupoNegocio().toString());
		return gas;
	}

	private static PolizaPctComisiones generarPolizaPctComisiones(PolizaPctComisiones pctCom, BigDecimal pctDescElegido, BigDecimal pctRecarElegido) {
		PolizaPctComisiones pc = new PolizaPctComisiones(); 		
		if(pctCom.getId() != null){
			pc.setId(pctCom.getId());
		}
		if(pctCom.getDescGrupoNegocio() != null){
			pc.setDescGrupoNegocio(pctCom.getDescGrupoNegocio());
		}
		if(pctCom.getEstado() != null){
			pc.setEstado(pctCom.getEstado());
		}
		if(pctCom.getGrupoNegocio() != null){
			pc.setGrupoNegocio(pctCom.getGrupoNegocio());
		}
		if(pctCom.getPctadministracion() != null){
			pc.setPctadministracion(pctCom.getPctadministracion());
		}
		if(pctCom.getPctadquisicion() != null){
			pc.setPctadquisicion(pctCom.getPctadquisicion());
		}
		if(pctCom.getPctcommax() != null){
			pc.setPctcommax(pctCom.getPctcommax());
		}
		// si tiene de pctdescuentos o pctrecargos el anexo, metemos esos datos para el recálculo de la comisionmediadora.
		if(pctDescElegido != null){
			pc.setPctdescelegido(pctDescElegido);
		} else if (pctCom.getPctdescelegido() != null){
			pc.setPctdescelegido(pctCom.getPctdescelegido());
		}
		if(pctRecarElegido != null){
			pc.setPctdescelegido(pctRecarElegido);
		} else if (pctCom.getPctrecarelegido() != null){
			pc.setPctdescelegido(pctCom.getPctrecarelegido());
		}
		if(pctCom.getPctdescmax() != null){
			pc.setPctdescmax(pctCom.getPctdescmax());
		}
		if(pctCom.getPctentidad() != null){
			pc.setPctentidad(pctCom.getPctentidad());
		}
		if(pctCom.getPctesmediadora() != null){
			pc.setPctesmediadora(pctCom.getPctesmediadora());
		}
		if(pctCom.getPoliza() != null){
			pc.setPoliza(pctCom.getPoliza());
		}
		return pc;
	}
	
	private static List<String> gruposNegocioAnexo(Set<ExplotacionAnexo> explotaciones, GenericDao genericDao) {
		List<String> gruposAnexo = new ArrayList<String>();
		for(ExplotacionAnexo exps : explotaciones){
			for(GrupoRazaAnexo gruposRazas : exps.getGrupoRazaAnexos()){
				long codTipoCapital = gruposRazas.getCodtipocapital().longValue();
				TipoCapitalConGrupoNegocio tccg = (TipoCapitalConGrupoNegocio) genericDao.getObject(TipoCapitalConGrupoNegocio.class,"codtipocapital", codTipoCapital);
				String grupoAnexo = tccg.getGruposNegocio().getGrupoNegocio().toString();
				if(!gruposAnexo.contains(grupoAnexo)){
					gruposAnexo.add(grupoAnexo);
				}
			}
		}
		logger.debug(new StringBuilder("gruposNegocioAnexo ").append(gruposAnexo.toString()));
		return gruposAnexo;
	}


	private static List<String> gruposNegocioGastosPoliza(Gastos[] gastos) {
		List<String> gruposPoliza = new ArrayList<String>();
		for (Gastos gasto : gastos) {
			String grupoNegocio = gasto.getGrupoNegocio();
			if(!gruposPoliza.contains(grupoNegocio)){
				gruposPoliza.add(grupoNegocio);
			}
		}
		logger.debug(new StringBuilder("gruposNegocioAnexo ").append(gruposPoliza.toString()));
		return gruposPoliza;
	}
	
	/**
	 * Devuelve los objetos asegurados de la póliza a partir del anexo
	 * @param am
	 * @return
	 * @throws ValidacionPolizaException 
	 */
	private static ObjetosAsegurados getObjetosAsegurados(final AnexoModificacion am, final Map<BigDecimal, List<String>> listaDatosVariables,
			final GenericDao polizaDao) throws ValidacionPolizaException {
		// IPolizaDao polizaDao
		// Objetos asegurados de la situación actualizada de la póliza		
		ObjetosAsegurados newOa = ObjetosAsegurados.Factory.newInstance();

		// Objetos asegurados
		List<ExplotacionDocument> listaExplotaciones = new ArrayList<ExplotacionDocument>(am.getExplotacionAnexos().size());
		for (ExplotacionAnexo explotacionAnexo : am.getExplotacionAnexos()) {
			// Si la parcela no se ha dado de baja se copia
			if (!Constants.BAJA.equals(explotacionAnexo.getTipoModificacion())) {
				Long lineaSeguroId = am.getPoliza().getLinea().getLineaseguroid();
				listaExplotaciones.add(explotacionAnexoToExplotacionAgr(explotacionAnexo, listaDatosVariables, lineaSeguroId, polizaDao));
			}
		}
		org.w3c.dom.Node importedNode;
		for (ExplotacionDocument explotacionDoc : listaExplotaciones) {
			importedNode = newOa.getDomNode().getOwnerDocument()
					.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
			newOa.getDomNode().appendChild(importedNode);
		}
		return newOa;
	}

	/**
	 * Transforma una explotación de anexo en una explotación de contratación
	 * 
	 * @param parcelaAnexo
	 * @return
	 * @throws ValidacionPolizaException 
	 */
	public static ExplotacionDocument explotacionAnexoToExplotacionAgr(
			final ExplotacionAnexo explotacionAnexo,
			final Map<BigDecimal, List<String>> listaDatosVariables,
			final Long lineaseguroId,
			final GenericDao polizaDao) throws ValidacionPolizaException {
		
		ExplotacionDocument explotDoc = ExplotacionDocument.Factory.newInstance();
		Explotacion explot = Explotacion.Factory.newInstance();
		explot.setRega(explotacionAnexo.getRega());
		explot.setSigla(explotacionAnexo.getSigla());
		explot.setEspecie(explotacionAnexo.getEspecie().intValue());
		explot.setRegimen(explotacionAnexo.getRegimen().intValue());
		
		if(explotacionAnexo.getNumero()!=null){
			explot.setNumero(explotacionAnexo.getNumero());
		}
		
		if (explotacionAnexo.getSubexplotacion() != null) {
			explot.setSubexplotacion(explotacionAnexo.getSubexplotacion());
		}
		
		// INICIO UBICACION
		Ambito ambito = Ambito.Factory.newInstance();
		ambito.setProvincia(explotacionAnexo.getTermino().getId().getCodprovincia().intValue());
		ambito.setComarca(explotacionAnexo.getTermino().getId().getCodcomarca().intValue());
		ambito.setTermino(explotacionAnexo.getTermino().getId().getCodtermino().intValue());
		ambito.setSubtermino(explotacionAnexo.getTermino().getId().getSubtermino().toString());
		
		explot.setUbicacion(ambito);
		// FIN UBICACION
		
		// INICIO COORDENADAS
		if (explotacionAnexo.getLatitud() != null
				&& explotacionAnexo.getLongitud() != null) {
			Coordenadas coordenadas = Coordenadas.Factory.newInstance();
			coordenadas.setLatitud(explotacionAnexo.getLatitud());
			coordenadas.setLongitud(explotacionAnexo.getLongitud());
			explot.setCoordenadas(coordenadas);
		}
		// FIN COORDENADAS
		
		Set<com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo> grupoRazasSet;
		List<GrupoRaza> grupoRazasCol;
		es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAsegurado;
		BigDecimal precioMax;
		Map<BigDecimal, DatosVariables> datosVarXML = null;
		
		// INICIO GRUPOS RAZA
		if (explotacionAnexo.getGrupoRazaAnexos() != null) {
			grupoRazasSet = explotacionAnexo.getGrupoRazaAnexos();
			grupoRazasCol = new ArrayList<GrupoRaza>(grupoRazasSet.size());
			
			Map<String, es.agroseguro.contratacion.explotacion.GrupoRaza> mapaGr = new HashMap<String, es.agroseguro.contratacion.explotacion.GrupoRaza>();
			Map<String, es.agroseguro.contratacion.explotacion.CapitalAsegurado> mapaTc = new HashMap<String, es.agroseguro.contratacion.explotacion.CapitalAsegurado>();
			Map<String, Set<String>> mapaGrVsTcClaves = new HashMap<String, Set<String>>();
			Map<String, es.agroseguro.contratacion.explotacion.Animales> mapaAnim = new HashMap<String, es.agroseguro.contratacion.explotacion.Animales>();
			Map<String, Set<String>> mapaTcVsAnimClaves = new HashMap<String, Set<String>>();
			
			//Preparación de los objetos
			for (com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo grupoRazaAplicacion : grupoRazasSet) {
				
				//Datos variables
				datosVarXML = getDatosVariablesExplotacionAnexo(lineaseguroId, grupoRazaAplicacion.getDatosVarExplotacionAnexos(), polizaDao);
						//getDatosVariablesExplotacion(lineaseguroId, grupoRazaAplicacion.getDatosVarExplotacionAnexos(), genericDao);
				
				//GRUPO RAZA
				Long codGrupoRaza = grupoRazaAplicacion.getCodgruporaza();
				
				if(mapaGr.get(codGrupoRaza.toString())==null){
					GrupoRaza g = GrupoRaza.Factory.newInstance();
					g.setGrupoRaza(codGrupoRaza.intValue());
					
					if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_GRUPO_RAZA) != null) {
						g.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_GRUPO_RAZA));
					}					
					
					mapaGr.put(codGrupoRaza.toString(), g);
				}
				
				//TIPO CAPITAL
				BigDecimal codTipoCapital = grupoRazaAplicacion.getCodtipocapital();
				String keyMapaCapital = codGrupoRaza + "-" + codTipoCapital;
				
				if(mapaTc.get(keyMapaCapital)==null){
					capitalAsegurado = es.agroseguro.contratacion.explotacion.CapitalAsegurado.Factory.newInstance();
					capitalAsegurado.setTipo(codTipoCapital.intValue());
					
					if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_CAP_ASEG) != null) {
						capitalAsegurado.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_CAP_ASEG));
					}			
					
					mapaTc.put(keyMapaCapital, capitalAsegurado);
					
					if(mapaGrVsTcClaves.get(codGrupoRaza+"")==null){
						Set<String> colClaves = new HashSet<String>();
						colClaves.add(codTipoCapital.toString());
						mapaGrVsTcClaves.put(codGrupoRaza+"", colClaves);
					}else{
						Set<String> colClaves = mapaGrVsTcClaves.get(codGrupoRaza+"");
						colClaves.add(codTipoCapital.toString());
						mapaGrVsTcClaves.put(codGrupoRaza+"", colClaves);
					}
				}

				//TIPO ANIMAL
				Long codTipoAnimal = grupoRazaAplicacion.getCodtipoanimal();
				String keyMapaAnimal = codGrupoRaza + "-" + codTipoCapital + "-" + codTipoAnimal;
				
				if(mapaAnim.get(keyMapaAnimal)==null){
					es.agroseguro.contratacion.explotacion.Animales animales = null;
					animales = es.agroseguro.contratacion.explotacion.Animales.Factory.newInstance();
					animales.setTipo(grupoRazaAplicacion.getCodtipoanimal().intValue());
					animales.setNumero(grupoRazaAplicacion.getNumanimales().intValue());
					
					precioMax = new BigDecimal(0);
					for (PrecioAnimalesModuloAnexo precioAnimalModulo : grupoRazaAplicacion.getPrecioAnimalesModuloAnexos()) {
						if (null != precioAnimalModulo && null != precioAnimalModulo.getPrecio()) {
							precioMax = precioMax.max(precioAnimalModulo.getPrecio());
						}
					}

					animales.setPrecio(precioMax);
					
					if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_ANIMALES) != null) {
						animales.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_ANIMALES));
					}
					
					mapaAnim.put(keyMapaAnimal, animales);
					
					if(mapaTcVsAnimClaves.get(keyMapaCapital)==null){
						Set<String> colClaves = new HashSet<String>();
						colClaves.add(codTipoAnimal.toString());
						mapaTcVsAnimClaves.put(keyMapaCapital, colClaves);
					}else{
						Set<String> colClaves = mapaTcVsAnimClaves.get(keyMapaCapital);
						colClaves.add(codTipoAnimal.toString());
						mapaTcVsAnimClaves.put(keyMapaCapital, colClaves);
					}
				}
			}

			//Para enlazarlos en el orden que exige la validación
			for (Map.Entry<String, es.agroseguro.contratacion.explotacion.GrupoRaza> entry : mapaGr.entrySet()) {
			    GrupoRaza gr = entry.getValue();
			    String codGrupoRaza = String.valueOf(gr.getGrupoRaza());
			    
			    Set<String> colClavesGrVsTc = mapaGrVsTcClaves.get(codGrupoRaza);
			    Iterator<String> it = colClavesGrVsTc.iterator();
			    String claveDobleTc = null;
			    
			    List<CapitalAsegurado> listaCapitalAsegurado = new ArrayList<CapitalAsegurado>();
			    
			    while(it.hasNext()){
			    	claveDobleTc = codGrupoRaza + "-" + it.next();
			    	
			    	CapitalAsegurado capi = mapaTc.get(claveDobleTc);
			    	
			    	String claveTripleAnim = null;
			    	Set<String> colClavesTcVsAnim = mapaTcVsAnimClaves.get(claveDobleTc);
			    	Iterator<String> it2 = colClavesTcVsAnim.iterator();
			    	
			    	List<Animales> listaAnimales = new ArrayList<Animales>();
			    	while(it2.hasNext()){
			    		claveTripleAnim = claveDobleTc + "-" + it2.next();
			    		Animales ani = mapaAnim.get(claveTripleAnim);
			    		listaAnimales.add(ani);
			    	}
	    	
					Animales arrayAnimales[] = new Animales[listaAnimales.size()];
					arrayAnimales = listaAnimales.toArray(arrayAnimales);
					capi.setAnimalesArray(arrayAnimales);
							
			    	listaCapitalAsegurado.add(capi);
			    }
			    
			    CapitalAsegurado arrayCapitalAsegurado[] = new CapitalAsegurado[listaCapitalAsegurado.size()];
			    arrayCapitalAsegurado = listaCapitalAsegurado.toArray(arrayCapitalAsegurado);
			    gr.setCapitalAseguradoArray(arrayCapitalAsegurado);
			    
			    grupoRazasCol.add(gr);
			}
			
			if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_EXPLOTACION) != null) {
				explot.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_EXPLOTACION));
			}
			
			explot.setGrupoRazaArray(grupoRazasCol.toArray(new GrupoRaza[] {}));

			explotDoc.setExplotacion(explot);
		}
		// FIN GRUPOS RAZA
		RiesgoCubiertoElegido[] listaRce = getArrayDatoVariableRiesgoCbtoExplotacion(explotacionAnexo);
		if(listaRce != null){
			if(explot.getDatosVariables() == null){
				explot.setDatosVariables(DatosVariables.Factory.newInstance());
			}
			explot.getDatosVariables().setRiesgCbtoElegArray(listaRce);
		}
		
		// rellenamos los datos variables especiales
		rellenaDatosVariablesEspecialesAnexo(explot,explotacionAnexo);
		
		explotDoc.setExplotacion(explot);
		
		return explotDoc;
	}
	
	
	/*
	 * TRATAMIENTO PARA DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
	 */
	private static void rellenaDatosVariablesEspecialesAnexo(Explotacion explot, com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo explotacionAnexo){
		if(explotacionAnexo.getExplotacionCoberturasAnexo()!=null && explotacionAnexo.getExplotacionCoberturasAnexo().size()>0) {
			List<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> listaCalc=new ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion>();
			List<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> listaMin=new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable>();
			List<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> listaCap=new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado>();
			List<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> listaFr=new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia>();
			List<es.agroseguro.contratacion.datosVariables.Garantizado> listaGar=new ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado>();
			for (ExplotacionCoberturaAnexo expCob : explotacionAnexo.getExplotacionCoberturasAnexo()) {
				logger.debug("--> CPM: " + expCob.getCpm() + " RC: " + expCob.getRiesgoCubierto() + " elegible: " + expCob.getElegible()+ " elegida: " + expCob.getElegida()+ " - DVcodCpto: " + expCob.getDvCodConcepto()+ " DVvalor: " + expCob.getDvValor()+ " DVelegido: "+ expCob.getDvElegido());
				if((expCob.getElegible().equals('S') && expCob.getElegida().equals('S')) || (expCob.getElegible().equals('N'))){
					if (expCob.getDvCodConcepto() != null && expCob.getDvElegido().equals('S')){
						switch (expCob.getDvCodConcepto().intValue()) {
						// INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
						case 121: // minimo indemnizable
							es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable  min= es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable.Factory.newInstance();
							min.setCPMod(expCob.getCpm());
							min.setCodRCub(expCob.getRiesgoCubierto());					
							min.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaMin.add(min);
							logger.debug("--> CoberturaDV 121 insertada: CPM: " + expCob.getCpm() + " RC: " + expCob.getRiesgoCubierto() + " eleg: " + expCob.getElegible()+ " - DVcodCpto: " + expCob.getDvCodConcepto()+ " DVvalor: " + expCob.getDvValor()+ " DVelegido: "+ expCob.getDvElegido());
							break;
						case 174: //calculo indemnizacion
							es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion ca= es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion.Factory.newInstance();
							ca.setCPMod(expCob.getCpm());
							ca.setCodRCub(expCob.getRiesgoCubierto());						
							ca.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaCalc.add(ca);
							logger.debug("--> CoberturaDV 174 insertada: CPM: " + expCob.getCpm() + " RC: " + expCob.getRiesgoCubierto() + " eleg: " + expCob.getElegible()+ " - DVcodCpto: " + expCob.getDvCodConcepto()+ " DVvalor: " + expCob.getDvValor()+ " DVelegido: "+ expCob.getDvElegido());
							break;					
						case 362: // % CAPITAL ASEGURADO						
							es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado cap = es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado.Factory.newInstance();
							cap.setCPMod(expCob.getCpm());
							cap.setCodRCub(expCob.getRiesgoCubierto());						
							cap.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaCap.add(cap);
							logger.debug("--> CoberturaDV 362 insertada: CPM: " + expCob.getCpm() + " RC: " + expCob.getRiesgoCubierto() + " eleg: " + expCob.getElegible()+ " - DVcodCpto: " + expCob.getDvCodConcepto()+ " DVvalor: " + expCob.getDvValor()+ " DVelegido: "+ expCob.getDvElegido());
							break;
						case 120: // % FRANQUICIA
							es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia fr = es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia.Factory.newInstance();
							fr.setCPMod(expCob.getCpm());
							fr.setCodRCub(expCob.getRiesgoCubierto());						
							fr.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaFr.add(fr);
							logger.debug("--> CoberturaDV 120 insertada: CPM: " + expCob.getCpm() + " RC: " + expCob.getRiesgoCubierto() + " eleg: " + expCob.getElegible()+ " - DVcodCpto: " + expCob.getDvCodConcepto()+ " DVvalor: " + expCob.getDvValor()+ " DVelegido: "+ expCob.getDvElegido());
							break;	
						case 175: // GARANTIZADO
							es.agroseguro.contratacion.datosVariables.Garantizado gar = es.agroseguro.contratacion.datosVariables.Garantizado.Factory.newInstance();
							gar.setCPMod(expCob.getCpm());
							gar.setCodRCub(expCob.getRiesgoCubierto());						
							gar.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaGar.add(gar);
							logger.debug("--> CoberturaDV 175 insertada: CPM: " + expCob.getCpm() + " RC: " + expCob.getRiesgoCubierto() + " eleg: " + expCob.getElegible()+ " - DVcodCpto: " + expCob.getDvCodConcepto()+ " DVvalor: " + expCob.getDvValor()+ " DVelegido: "+ expCob.getDvElegido());
							break;		
						default:		
							break;
						}
					}
				}
			}
			if (listaMin.size()>0){
				es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[] arrayMin = listaMin.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[listaMin.size()]);		
				if(null==explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayMin.length>0)
					explot.getDatosVariables().setMinIndemArray(arrayMin);
			}
			if (listaCalc.size()>0){
				es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[] arrayCalc = listaCalc.toArray(new es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[listaCalc.size()]);		
				if(null==explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayCalc.length>0)
					explot.getDatosVariables().setCalcIndemArray(arrayCalc);
			}
			if (listaCap.size()>0){
				es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[] arrayCap = listaCap.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[listaCap.size()]);		
				if(null==explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayCap.length>0)
					explot.getDatosVariables().setCapAsegArray(arrayCap);
			}
			if (listaFr.size()>0){
				es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[] arrayFr = listaFr.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[listaFr.size()]);		
				if(null==explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayFr.length>0)
					explot.getDatosVariables().setFranqArray(arrayFr);
			}
			if (listaGar.size()>0){
				es.agroseguro.contratacion.datosVariables.Garantizado[] arrayGar = listaGar.toArray(new es.agroseguro.contratacion.datosVariables.Garantizado[listaGar.size()]);		
				if(null==explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayGar.length>0)
					explot.getDatosVariables().setGarantArray(arrayGar);
			}
		}
	}
	
	private static RiesgoCubiertoElegido[] getArrayDatoVariableRiesgoCbtoExplotacion(final ExplotacionAnexo tbExplotacion){
		RiesgoCubiertoElegido[] rCEArray = null;
		ArrayList<ExplotacionCoberturaAnexo> tempList = new ArrayList<ExplotacionCoberturaAnexo>();
		if(tbExplotacion.getExplotacionCoberturasAnexo() != null && tbExplotacion.getExplotacionCoberturasAnexo().size()>0){			
			int i=0;
			for (ExplotacionCoberturaAnexo expCob : tbExplotacion.getExplotacionCoberturasAnexo()) {
				// añadimos solo las coberturas elegidas
				if (expCob.getElegida().equals(Constants.CHARACTER_S) && expCob.getDvCodConcepto() == null){
					tempList.add(expCob);
				}
			}
			rCEArray = new RiesgoCubiertoElegido[tempList.size()];			
			for (ExplotacionCoberturaAnexo expCob : tempList) {
				RiesgoCubiertoElegido rc= RiesgoCubiertoElegido.Factory.newInstance();
				// añadimos solo las coberturas elegidas
				rc.setCodRCub(expCob.getRiesgoCubierto());
				rc.setCPMod(expCob.getCpm());
				rc.setValor(expCob.getElegida().toString());
				rCEArray[i] = rc; 
				i++;
			}
		}
		return rCEArray;
	}
	
	
	private static Map<BigDecimal, DatosVariables> getDatosVariablesExplotacionAnexo(
			final Long lineaseguroId,
			final Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo> datosVariables,
			final GenericDao genericDao) throws ValidacionPolizaException {
		
		BigDecimal codUbicacion;
		Method methodAdd;
		Method methodSet;
		Method methodValor;
		Class<?> valorClass;
		Object objValor;
		String etiquetaXML;
		BigDecimal codConcepto;
		XmlObject datoVar;
		Map<BigDecimal, DatosVariables> result = new HashMap<BigDecimal, DatosVariables>();
		try {
			// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA (31) Y A
			// LAS UBICACIONES DE EXPLOTACION
			Filter oiFilter = new Filter() {
				@Override
				public Criteria getCriteria(final Session sesion) {
					Criteria criteria = sesion
							.createCriteria(OrganizadorInformacion.class);
					criteria.add(Restrictions.eq("id.lineaseguroid",
							lineaseguroId));
					criteria.add(Restrictions.eq("id.coduso",
							OrganizadorInfoConstants.USO_POLIZA));
					criteria.add(Restrictions
							.in("id.codubicacion",
									new Object[] {
											OrganizadorInfoConstants.UBICACION_ANIMALES,
											OrganizadorInfoConstants.UBICACION_CAP_ASEG,
											OrganizadorInfoConstants.UBICACION_GRUPO_RAZA,
											OrganizadorInfoConstants.UBICACION_EXPLOTACION }));
					return criteria;
				}
			};
			@SuppressWarnings("unchecked")
			List<OrganizadorInformacion> oiList = (List<OrganizadorInformacion>) genericDao
					.getObjects(oiFilter);
			for (OrganizadorInformacion oi : oiList) {
				codConcepto = oi.getId().getCodconcepto();
				// LOCALIZAMOS EL CONCEPTO EN LOS DATOS VARIABLES DE LA
				// EXPLOTACION
				inner: for (com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo datoVariable : datosVariables) {
					if (codConcepto.equals(new BigDecimal(datoVariable
							.getCodconcepto()))) {
						codUbicacion = oi.getId().getCodubicacion();
						if (result.get(codUbicacion) == null) {
							result.put(codUbicacion,
									DatosVariables.Factory.newInstance());
						}
						etiquetaXML = oi.getDiccionarioDatos().getEtiquetaxml();
						// OBTENEMOS LOS METODOS NECESARIOS PARA AÑADIR LA
						// ETIQUETA XML DEL DATO VARIABLE
						methodAdd = DatosVariables.class.getMethod("addNew"
								+ etiquetaXML);
						methodSet = DatosVariables.class.getMethod("set"
								+ etiquetaXML, methodAdd.getReturnType());
						// OBTENEMOS EL OBJETO QUE GESTIONA EL DATO VARIABLE
						datoVar = (XmlObject) methodAdd.invoke(result
								.get(codUbicacion));
						// OBTENEMOS LOS METODOS NECESARIOS PARA GESTIONAR EL
						// CONTENIDO DEL DATO VARIABLE
						valorClass = datoVar.getClass().getMethod("getValor")
								.getReturnType();
						methodValor = datoVar.getClass().getMethod("setValor",
								valorClass);
						// CREAMOS EL VALOR (TIPOS CONOCIDOS AL MOMENTO DEL
						// DESARROLLO...ES POSIBLE QUE SE NECESITEN AÑADIR MAS
						// EN UN FUTURO)
						if (valorClass.equals(List.class)) {
							objValor = Arrays
									.asList(new String[] { datoVariable
											.getValor() });
						} else if (valorClass.equals(String.class)) {
							objValor = datoVariable.getValor();
						} else if (valorClass.equals(Integer.class)
								|| valorClass.equals(int.class)) {
							objValor = Integer.valueOf(datoVariable.getValor());
						} else if (valorClass.equals(Long.class)
								|| valorClass.equals(long.class)) {
							objValor = Long.valueOf(datoVariable.getValor());
						} else if (valorClass.equals(BigDecimal.class)) {
							objValor = new BigDecimal(datoVariable.getValor());
						} else {
							throw new ValidacionPolizaException(
									"Tipo de dato variable no esperado");
						}
						// INYECTAMOS EL VALOR DEL DATO VARIABLE
						methodValor.invoke(datoVar, objValor);
						// INYECTAMOS EL DATO VARIABLE
						methodSet.invoke(result.get(codUbicacion), datoVar);
						break inner;
					}
				}
			}
		} catch (Exception e) {
			throw new ValidacionPolizaException(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * Obtiene las subvenciones declaradas de la póliza actualizada
	 * @param am
	 * @param p
	 * @return
	 */
	private static SubvencionesDeclaradas getSubvencionesDeclaradas (final AnexoModificacion am, final Poliza p) {
		// Mapa con los códigos de subvenciones que no hay que copiar de la póliza ya que han sido tratadas desde el anexo
		Map<Integer, Boolean> mapa = new HashMap<Integer, Boolean>();
		SubvencionesDeclaradas subvencionesDeclaradas = SubvencionesDeclaradas.Factory.newInstance();
		// Listado de subvenciones que tendrá la póliza final
		ArrayList<SubvencionDeclarada> lstSubv = new ArrayList<SubvencionDeclarada>();
		boolean anadirSeguridadSocial = false;
		// Subvenciones del anexo
		for (SubvDeclarada sdAnexo : am.getSubvDeclaradas()) {
			if (!Constants.BAJA.equals(sdAnexo.getTipomodificacion())) {
				SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
				sd.setTipo(sdAnexo.getCodsubvencion().intValue());			
				lstSubv.add(sd);
				if (sdAnexo.getCodsubvencion().compareTo(Constants.SUBVENCION20) == 0 || 
						sdAnexo.getCodsubvencion().compareTo(Constants.SUBVENCION30) == 0){
					anadirSeguridadSocial = true;
				}
			}
			// Se marca la subvención como tratada
			mapa.put(sdAnexo.getCodsubvencion().intValue(), true);
		}
		// Subvenciones de la póliza
		SubvencionesDeclaradas sdPlz = p.getSubvencionesDeclaradas();
		if (sdPlz != null) {
		SubvencionDeclarada[] subvencionDeclaradaArray = sdPlz.getSubvencionDeclaradaArray();
		for (SubvencionDeclarada subvencionDeclarada : subvencionDeclaradaArray) {
			// Si la subvención no ha sido ya incluida en la póliza
			if (!mapa.containsKey(subvencionDeclarada.getTipo())) {
				lstSubv.add(subvencionDeclarada);
			}
		} }
		// Se establece el listado de subvenciones en la póliza
		
		subvencionesDeclaradas.setSubvencionDeclaradaArray(lstSubv.toArray(new SubvencionDeclarada[lstSubv.size()]));
		// Se comprueba si hay que añadir la seguridad social
		if (anadirSeguridadSocial && (p.getSubvencionesDeclaradas() != null && p.getSubvencionesDeclaradas().getSeguridadSocial() != null)) {
			SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
			// Si en la situación actualizada hay seguridad social se coge de ahÃ­
			if (p.getSubvencionesDeclaradas() != null && p.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
				segSocial.setProvincia(p.getSubvencionesDeclaradas().getSeguridadSocial().getProvincia());
				segSocial.setNumero(p.getSubvencionesDeclaradas().getSeguridadSocial().getNumero());
				segSocial.setCodigo(p.getSubvencionesDeclaradas().getSeguridadSocial().getCodigo());
				segSocial.setRegimen(p.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen());
			}
			else {
				segSocial.setProvincia(Integer.parseInt(am.getNumsegsocial().substring(0, 2)));
				segSocial.setNumero(Integer.parseInt(am.getNumsegsocial().substring(2, 10)));
				segSocial.setCodigo(am.getNumsegsocial().substring(10));
				segSocial.setRegimen(Short.parseShort(am.getRegimensegsocial()+""));
			}
			subvencionesDeclaradas.setSeguridadSocial(segSocial);
		}
		return subvencionesDeclaradas;
	}
	
	public static BigDecimal obtenerComisionMediador(
			final PolizaPctComisiones ppc) {
		BigDecimal comMediador = Constants.CERO;
		if (ppc.getPctcommax() != null && ppc.getPctentidad() != null
				&& ppc.getPctesmediadora() != null) {
			BigDecimal comisionMax = ppc.getPctcommax();
			BigDecimal comisionEntidad = ppc.getPctentidad();
			BigDecimal comisionE_S = ppc.getPctesmediadora();
			BigDecimal dtoElegido = null;
			BigDecimal recElegido = null;
			if (ppc.getPctdescelegido() != null){
				dtoElegido = ppc.getPctdescelegido();
			}
			if (ppc.getPctrecarelegido() != null){
				recElegido = ppc.getPctrecarelegido();
			}
			BigDecimal descuento = Constants.CERO;
			BigDecimal recargo = Constants.CERO;
			BigDecimal pctComision = Constants.CERO;

			if (dtoElegido != null && dtoElegido.compareTo(Constants.CERO) != 0) {
				descuento = dtoElegido.divide(Constants.CIEN);
				pctComision = (comisionE_S.multiply(new BigDecimal(1)
						.subtract(descuento))).add(comisionEntidad);
			}
			if (recElegido != null && recElegido.compareTo(Constants.CERO) != 0) {
				recargo = recElegido.divide(Constants.CIEN);
				pctComision = (comisionE_S.multiply(new BigDecimal(1)
						.add(recargo))).add(comisionEntidad);
			}
			if ((dtoElegido == null || dtoElegido.compareTo(Constants.CERO) == 0)
					&& (recElegido == null || recElegido.compareTo(Constants.CERO) == 0)) {
				pctComision = (comisionE_S.multiply(new BigDecimal(1)))
						.add(comisionEntidad);
			}
			if (comisionMax != null && comisionMax.compareTo(Constants.CERO) != 0)
				comMediador = (comisionMax.multiply(pctComision)).divide(Constants.CIEN);
		}
		
		return comMediador.setScale(2, BigDecimal.ROUND_DOWN);
	}
	
}