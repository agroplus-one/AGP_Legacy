package com.rsi.agp.core.managers.impl.anexoMod.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.CollectionsAndMapsUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.PolizaUnificadaTransformer;
import com.rsi.agp.core.webapp.util.CoberturasUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;

import es.agroseguro.contratacion.Cuenta;
import es.agroseguro.contratacion.CuentaCobroSiniestros;
import es.agroseguro.contratacion.Pago;
import es.agroseguro.contratacion.parcela.ParcelaDocument;
import es.agroseguro.iTipos.Ambito;
import es.agroseguro.iTipos.DatosContacto;
import es.agroseguro.iTipos.Direccion;
import es.agroseguro.iTipos.IdentificacionCatastral;
import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.iTipos.SIGPAC;
import es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado;
import es.agroseguro.seguroAgrario.contratacion.CapitalesAsegurados;
import es.agroseguro.seguroAgrario.contratacion.Cobertura;
import es.agroseguro.seguroAgrario.contratacion.Cosecha;
import es.agroseguro.seguroAgrario.contratacion.ObjetosAsegurados;
import es.agroseguro.seguroAgrario.contratacion.Parcela;
import es.agroseguro.seguroAgrario.contratacion.Poliza;
import es.agroseguro.seguroAgrario.contratacion.SeguridadSocial;
import es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada;
import es.agroseguro.seguroAgrario.contratacion.SubvencionesDeclaradas;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CalculoIndemnizacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CaracteristicasExplotacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CicloCultivo;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CodigoCertificado;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CodigoIGP;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CodigoReduccionRdtos;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DaniosCubiertos;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DatosVariables;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DenominacionOrigen;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Densidad;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Destino;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DiasLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Edad;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.EdadCubierta;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.EdadEstructura;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.EstadoFenologicoLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaRecoleccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaSiembraTrasplante;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Garantizado;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.IndicadorGastosSalvamento;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MaterialCubierta;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MaterialEstructura;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MedidaPreventiva;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MesesLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MetrosCuadrados;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MetrosLineales;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroAniosDesdeDescorche;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroAniosDesdePoda;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroUnidades;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Pendiente;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PeriodoGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeCapitalAsegurado;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeFranquicia;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeMinimoIndemnizable;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PracticaCultural;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.RiesgoCubiertoElegido;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Rotacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaConduccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaCultivo;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaProduccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaProteccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoFranquicia;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoInstalacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoMarcoPlantacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoMasa;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoPlantacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoRendimiento;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoSubvencionDeclaradaParcela;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoTerreno;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.ValorFijo;

public class PolizaActualizadaTranformer {
	
	private static final Log logger = LogFactory.getLog(PolizaActualizadaTranformer.class);
	
	/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
	private static final ResourceBundle bundle_siniestros = ResourceBundle.getBundle("agp_cobro_siniestros");
	
	
	/**
	 * Modifica el objeto Poliza con los datos incluidos en el anexo de modificaciÃ³n
	 * @param polizaAgroseguro
	 * @param anexoModiicacion
	 */
	public static void generarPolizaSituacionFinalCompletaAgri (es.agroseguro.contratacion.Poliza polizaAgroseguro, AnexoModificacion anexoModificacion, 
			Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal>  listaCPM, boolean hayCambiosDatosAsegurado) {
		
		/* MODIF TAM -* COMENTAAMOS ESTA FUNCIÓN DE MOMENTO */
		// ASEGURADO
		if (!StringUtils.nullToString(anexoModificacion.getNomaseg()).equals("") || !StringUtils.nullToString(anexoModificacion.getRazsocaseg()).equals("") ){
			polizaAgroseguro.setAsegurado(getAsegurado(anexoModificacion));
			polizaAgroseguro.setAsegurado(getAsegurado(anexoModificacion));
		}
		
		// OBJETOS ASEGURADOS
		if (!CollectionsAndMapsUtil.isEmpty(anexoModificacion.getParcelas())){
			polizaAgroseguro.setObjetosAsegurados(getObjetosAsegurados(anexoModificacion, listaDatosVariables));
		}
		
		// SUBVENCIONES
		if (agregarNodoDeSubvenciones(anexoModificacion.getSubvDeclaradas())) {
			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvsDeclaradas = getSubvencionesDeclaradas(anexoModificacion, polizaAgroseguro);
			logger.debug(subvsDeclaradas);
			if (subvsDeclaradas == null || subvsDeclaradas.getSubvencionDeclaradaArray() == null
					|| subvsDeclaradas.getSubvencionDeclaradaArray().length == 0) {
				logger.debug("unset de subvenciones");
				if (polizaAgroseguro.getSubvencionesDeclaradas() != null) 
					polizaAgroseguro.unsetSubvencionesDeclaradas();
			} else {
				logger.debug("asignaci�n de subvenciones");
				polizaAgroseguro.setSubvencionesDeclaradas(subvsDeclaradas);
			}
		}
		
		// COBERTURAS
		boolean hayCoberturasAnex = !CollectionsAndMapsUtil.isEmpty(anexoModificacion.getCoberturas());
		boolean moduloCambiado = !anexoModificacion.getCodmodulo().trim()
				.equalsIgnoreCase(polizaAgroseguro.getCobertura().getModulo().trim());
		boolean coberturasDiferentes = AnexoModificacionUtils.tieneCambiosCoberturas(anexoModificacion,
				polizaAgroseguro);
		logger.debug("hayCoberturasAnex: " + hayCoberturasAnex);
		logger.debug("moduloCambiado: " + moduloCambiado);
		logger.debug("coberturasDiferentes: " + coberturasDiferentes);
		if (hayCoberturasAnex || moduloCambiado || coberturasDiferentes) {
			polizaAgroseguro.setCobertura(PolizaUnificadaTransformer.getCoberturasAnexo(anexoModificacion, listaCPM, null));
		}
		
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
		//PAGOS: Incluirmos el nuevo IBAN si se ha modificado si se ha modificado el IBAN
		if(ObjectUtils.equals(anexoModificacion.getEsIbanAsegModificado(),1)){
			Pago pago = polizaAgroseguro.getPago();
			Cuenta cuenta = Cuenta.Factory.newInstance();
			cuenta.setIban(anexoModificacion.getIbanAsegModificado());
			if (pago.getCuenta() != null) {
				if (pago.getCuenta().getDestinatario() != null) {
					cuenta.setDestinatario(pago.getCuenta().getDestinatario());
				}
				if (pago.getCuenta().getTitular() != null) {
					cuenta.setTitular(pago.getCuenta().getTitular());
				}
			}
			pago.setCuenta(cuenta);
			polizaAgroseguro.setPago(pago);
		}
				
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (26/02/2021) * Inicio */ 
		// PAGOS: Incluir si se ha modificado el IBAN de Cuenta Cobro siniestros si se ha modificado
		CuentaCobroSiniestros cuentacobro = getCuentaCobroSinAnexo(anexoModificacion);
		if(ObjectUtils.equals(anexoModificacion.getEsIban2AsegModificado(), 1)){
			cuentacobro.setIban(anexoModificacion.getIban2AsegModificado());
			polizaAgroseguro.setCuentaCobroSiniestros(cuentacobro);
		}else {
			/* Incluimos en la llamada al S.W de Validacion, calcular y Confirmación de anexos */
			if (!StringUtils.isNullOrEmpty(cuentacobro.getIban())){
				polizaAgroseguro.setCuentaCobroSiniestros(cuentacobro);
			}
		}	
		/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */
		
		if (hayCambiosDatosAsegurado) {
			logger.debug("Hay cambios en los datos del asegurado respecto a la situacion actualizada, se modifica el XML de envio al SW");
			actualizarDatosAseguradoSiHayCambios(anexoModificacion, polizaAgroseguro);
		}
		
	}
	
	
	/**
	 * Si hay diferencias entre los datos actuales del asegurado en agroplus y los datos de la situaci�n actualizada,
	 * los datos del asegurado en la situaci�n actualizada se modifican por los de Agroplus para su env�o al SW.
	 *
	 * @param anexo El objeto Anexo que contiene la informaci�n actual del asegurado en Agroplus.
	 * @param polizaAgroseguro El objeto Poliza que contiene la informaci�n del asegurado en la situaci�n actualizada.
	 */
	private static void actualizarDatosAseguradoSiHayCambios(AnexoModificacion anexo, es.agroseguro.contratacion.Poliza polizaAgroseguro) {
		
		Asegurado aseguradoAnexo = anexo.getPoliza().getAsegurado();

        if (aseguradoAnexo != null) {
        	polizaAgroseguro.getAsegurado().setNif(aseguradoAnexo.getNifcif() != null ? aseguradoAnexo.getNifcif() : "");
        	
        	if (polizaAgroseguro.getAsegurado().getNombreApellidos() != null) {
            	polizaAgroseguro.getAsegurado().getNombreApellidos().setNombre(aseguradoAnexo.getNombre() != null ? aseguradoAnexo.getNombre() : "");
            	polizaAgroseguro.getAsegurado().getNombreApellidos().setApellido1(aseguradoAnexo.getApellido1() != null ? aseguradoAnexo.getApellido1() : "");
            	polizaAgroseguro.getAsegurado().getNombreApellidos().setApellido2(aseguradoAnexo.getApellido2() != null ? aseguradoAnexo.getApellido2() : "");
        	}
        	else {
        		if (polizaAgroseguro.getAsegurado().getRazonSocial() != null) {
        			polizaAgroseguro.getAsegurado().getRazonSocial().setRazonSocial(aseguradoAnexo.getRazonsocial() != null ? aseguradoAnexo.getRazonsocial() : "");
        		}
        	}
            
            String via = aseguradoAnexo.getVia() != null && aseguradoAnexo.getVia().getAbreviatura() != null && aseguradoAnexo.getDireccion() != null ? aseguradoAnexo.getVia().getAbreviatura() + " " + aseguradoAnexo.getDireccion() : "";
            polizaAgroseguro.getAsegurado().getDireccion().setVia(via);

            polizaAgroseguro.getAsegurado().getDireccion().setNumero(aseguradoAnexo.getNumvia() != null ? aseguradoAnexo.getNumvia() : "");
            polizaAgroseguro.getAsegurado().getDireccion().setPiso(aseguradoAnexo.getPiso() != null ? aseguradoAnexo.getPiso() : "");
            polizaAgroseguro.getAsegurado().getDireccion().setBloque(aseguradoAnexo.getBloque() != null ? aseguradoAnexo.getBloque() : "");
            polizaAgroseguro.getAsegurado().getDireccion().setEscalera(aseguradoAnexo.getEscalera() != null ? aseguradoAnexo.getEscalera() : "");

            if (aseguradoAnexo.getLocalidad() != null && aseguradoAnexo.getLocalidad().getProvincia() != null && aseguradoAnexo.getLocalidad().getProvincia().getCodprovincia() != null) {
            	polizaAgroseguro.getAsegurado().getDireccion().setProvincia(aseguradoAnexo.getLocalidad().getProvincia().getCodprovincia().intValue());
            } else {
            	polizaAgroseguro.getAsegurado().getDireccion().setProvincia(0);
            }

            polizaAgroseguro.getAsegurado().getDireccion().setLocalidad(aseguradoAnexo.getLocalidad() != null && aseguradoAnexo.getLocalidad().getNomlocalidad() != null ? aseguradoAnexo.getLocalidad().getNomlocalidad() : "");

            if (aseguradoAnexo.getTelefono() != null) {
                try {
                	polizaAgroseguro.getAsegurado().getDatosContacto().setTelefonoFijo(Integer.parseInt(aseguradoAnexo.getTelefono()));
                } catch (NumberFormatException e) {
                    logger.error("Error al convertir el n�mero de tel�fono del asegurado a un entero. Se utilizar� el valor predeterminado 0. Valor original: ");
                    polizaAgroseguro.getAsegurado().getDatosContacto().setTelefonoFijo(0);
                }
            } else {
            	polizaAgroseguro.getAsegurado().getDatosContacto().setTelefonoFijo(0);
            }

            if (aseguradoAnexo.getMovil() != null) {
                try {
                	polizaAgroseguro.getAsegurado().getDatosContacto().setTelefonoMovil(Integer.parseInt(aseguradoAnexo.getMovil()));
                } catch (NumberFormatException e) {
                    logger.error("Error al convertir el n�mero de tel�fono del asegurado a un entero. Se utilizar� el valor predeterminado 0. Valor original: ");
                    polizaAgroseguro.getAsegurado().getDatosContacto().setTelefonoMovil(0);
                }
            } else {
            	polizaAgroseguro.getAsegurado().getDatosContacto().setTelefonoMovil(0);
            }
            
        	polizaAgroseguro.getAsegurado().getDatosContacto().setEmail(aseguradoAnexo.getEmail() != null ? aseguradoAnexo.getEmail() : "");
        }
	}


	/**
	 * Modifica el objeto Poliza con los datos incluidos en el anexo de modificaciÃ³n
	 * @param polizaAgroseguro
	 * @param anexoModiicacion
	 */
	public static void generarPolizaSitFinalCompletaAgricAnt (es.agroseguro.seguroAgrario.contratacion.Poliza polizaAgrosPpal, AnexoModificacion anexoModiicacion, 
			Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal>  listaCPM) {
		
		/* MODIF TAM -* COMENTAAMOS ESTA FUNCIÓN DE MOMENTO */
		// ASEGURADO
		if (!StringUtils.nullToString(anexoModiicacion.getNomaseg()).equals("") || !StringUtils.nullToString(anexoModiicacion.getRazsocaseg()).equals("") ){
			polizaAgrosPpal.setAsegurado(getAseguradoAnt(anexoModiicacion));
		}
		
		// OBJETOS ASEGURADOS
		if (!CollectionsAndMapsUtil.isEmpty(anexoModiicacion.getParcelas())){
			polizaAgrosPpal.setObjetosAsegurados(getObjetosAseguradosAnt(anexoModiicacion, listaDatosVariables));
		}
		
		// SUBVENCIONES
		if (agregarNodoDeSubvenciones(anexoModiicacion.getSubvDeclaradas())) {
			SubvencionesDeclaradas subvsDeclaradas = getSubvencionesDeclaradasAnt(anexoModiicacion, polizaAgrosPpal);
			logger.debug(subvsDeclaradas);
			if (subvsDeclaradas == null || subvsDeclaradas.getSubvencionDeclaradaArray() == null
					|| subvsDeclaradas.getSubvencionDeclaradaArray().length == 0) {
				logger.debug("unset de subvenciones");
				if (polizaAgrosPpal.getSubvencionesDeclaradas() != null)
					polizaAgrosPpal.unsetSubvencionesDeclaradas();
			} else {
				logger.debug("asignaci�n de subvenciones");
				polizaAgrosPpal.setSubvencionesDeclaradas(subvsDeclaradas);
			}
		}
		
		// COBERTURAS
		if (!CollectionsAndMapsUtil.isEmpty(anexoModiicacion.getCoberturas())){						
			polizaAgrosPpal.setCobertura(getCoberturas(anexoModiicacion,listaCPM));
		}
		
	}
	
	/**
	 * Método que retorna un booleano en función de si la lista de subvenciones está vacia 
	 * y se han agregado, o no, nuevas subvenciones
	 * @param subvDeclaradas
	 * @return retorna true cuando se debe agregar el nodo al xml de validación y false 
	 * 			cuando no se debe agregar 
	 */
	private static boolean agregarNodoDeSubvenciones(Set<SubvDeclarada> subvDeclaradas) {
		logger.debug("agregarNodoDeSubvenciones !CollectionsAndMapsUtil.isEmpty(subvDeclaradas): " + !CollectionsAndMapsUtil.isEmpty(subvDeclaradas));
		logger.debug("agregarNodoDeSubvenciones hayCambiosEnSubvs(subvDeclaradas): " + hayCambiosEnSubvs(subvDeclaradas));
		return !CollectionsAndMapsUtil.isEmpty(subvDeclaradas) && hayCambiosEnSubvs(subvDeclaradas);
	}
	
	/**
	 * Método que comprueba si hay cambios en la lista de subvenciones 
	 * declaradas en el anexo de modificación
	 * @param subvDeclaradas
	 * @return si hay altas o bajas de subvenciones true, en caso contrario, false
	 */
	private static boolean hayCambiosEnSubvs(Set<SubvDeclarada> subvDeclaradas) {
		for (SubvDeclarada subvencion : subvDeclaradas) {
			if (Constants.BAJA.equals(subvencion.getTipomodificacion())
					|| Constants.ALTA.equals(subvencion.getTipomodificacion())) {
				logger.debug(subvencion.getCodsubvencion() + " -> " + subvencion.getTipomodificacion());
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Devuelve las coberturas del anexo
	 * @param am
	 * @param listaDatosVariables 
	 * @return
	 */
	private static Cobertura getCoberturas (AnexoModificacion am, List<BigDecimal>  listaCPM) {
		
		Cobertura cobertura = Cobertura.Factory.newInstance();
		
		if (!StringUtils.nullToString(am.getCodmodulo().trim()).equals(""))
			cobertura.setModulo(String.format("%-5s", am.getCodmodulo().trim()));
		else
			cobertura.setModulo(String.format("%-5s", am.getPoliza().getCodmodulo().trim()));
		
				
		DatosVariables dv = DatosVariables.Factory.newInstance();
		
		List<CalculoIndemnizacion> listCalcIndem = new ArrayList<CalculoIndemnizacion>();
		List<Garantizado> listGarantizado = new ArrayList<Garantizado>();
		List<PorcentajeFranquicia> listPctFranquicia = new ArrayList<PorcentajeFranquicia>();
		List<PorcentajeMinimoIndemnizable> listPctMinIndem = new ArrayList<PorcentajeMinimoIndemnizable>();
		List<RiesgoCubiertoElegido> listRCubEleg = new ArrayList<RiesgoCubiertoElegido>();
		List<TipoFranquicia> listTipoFranq = new ArrayList<TipoFranquicia>();
		List<PorcentajeCapitalAsegurado> listPctCapAseg = new ArrayList<PorcentajeCapitalAsegurado>();
		
		for (com.rsi.agp.dao.tables.anexo.Cobertura coberturasAnexo : am.getCoberturas()) {
			
			if (CoberturasUtils.isCPMPermitido(coberturasAnexo.getCodconceptoppalmod(), coberturasAnexo.getCodconcepto(),listaCPM)) {
				// CALCULO INDEMNIZACION
				if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION).equals(coberturasAnexo.getCodconcepto())) {
					CalculoIndemnizacion cI = CalculoIndemnizacion.Factory.newInstance();
					cI.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					cI.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					cI.setValor(new Integer (coberturasAnexo.getCodvalor()).intValue());
					
					listCalcIndem.add(cI);
				}
				// GARANTIZADO
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO).equals(coberturasAnexo.getCodconcepto())) {
					Garantizado g = Garantizado.Factory.newInstance();
					g.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					g.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					g.setValor(new Integer (coberturasAnexo.getCodvalor()).intValue());
					
					listGarantizado.add(g);
				}
				//% FRANQUICIA
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA).equals(coberturasAnexo.getCodconcepto())) {
					PorcentajeFranquicia fran = PorcentajeFranquicia.Factory.newInstance();
					fran.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					fran.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					fran.setValor(new Integer (coberturasAnexo.getCodvalor()).intValue());
					
					listPctFranquicia.add(fran);
				}
				//% MINIMO INDEMNIZABLE
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE).equals(coberturasAnexo.getCodconcepto())) {
					PorcentajeMinimoIndemnizable minIndem = PorcentajeMinimoIndemnizable.Factory.newInstance();
					minIndem.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					minIndem.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					minIndem.setValor(new Integer (coberturasAnexo.getCodvalor()).intValue());
					
					listPctMinIndem.add(minIndem);
				}
				//RIESGO CUBIERTO ELEGIDO
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO).equals(coberturasAnexo.getCodconcepto())) {
					RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
					rCubEleg.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					rCubEleg.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());				
					rCubEleg.setValor(Constants.RIESGO_ELEGIDO_NO.equals(coberturasAnexo.getCodvalor()) ? "N" : "S");
					
					listRCubEleg.add(rCubEleg);
				}
				//TIPO FRANQUICIA
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA).equals(coberturasAnexo.getCodconcepto())) {
					TipoFranquicia tipoFranq = TipoFranquicia.Factory.newInstance();
					tipoFranq.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					tipoFranq.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());				
					tipoFranq.setValor(coberturasAnexo.getCodvalor());
					
					listTipoFranq.add(tipoFranq);
				}
				//% CAPITAL ASEGURADO
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO).equals(coberturasAnexo.getCodconcepto())) {
					PorcentajeCapitalAsegurado pctCapAseg = PorcentajeCapitalAsegurado.Factory.newInstance();
					pctCapAseg.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					pctCapAseg.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());				
					pctCapAseg.setValor(new Integer (coberturasAnexo.getCodvalor()).intValue());
					
					listPctCapAseg.add(pctCapAseg);
				}
				//CARACT. EXPLOTACION
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION).equals(coberturasAnexo.getCodconcepto())) {
					CaracteristicasExplotacion caracExpl = CaracteristicasExplotacion.Factory.newInstance();
					caracExpl.setValor(new Integer (coberturasAnexo.getCodvalor()).intValue());
					
					dv.setCarExpl(caracExpl);
				}
			}
		}
		
		// Se insertan las listas generadas en el objeto DatosVariables
		if (listCalcIndem.size() > 0) {
			dv.setCalcIndemArray(listCalcIndem.toArray(new CalculoIndemnizacion[listCalcIndem.size()]));
		}
		if (listGarantizado.size() > 0) {
			dv.setGarantArray(listGarantizado.toArray(new Garantizado[listGarantizado.size()]));
		}
		if (listPctFranquicia.size() > 0) {
			dv.setFranqArray(listPctFranquicia.toArray(new PorcentajeFranquicia[listPctFranquicia.size()]));
		}
		if (listPctMinIndem.size() > 0) {
			dv.setMinIndemArray(listPctMinIndem.toArray(new PorcentajeMinimoIndemnizable[listPctMinIndem.size()]));
		}
		if (listRCubEleg.size() > 0) {
			dv.setRiesgCbtoElegArray(listRCubEleg.toArray(new RiesgoCubiertoElegido[listRCubEleg.size()]));
		}
		if (listTipoFranq.size() > 0) {
			dv.setTipFranqArray(listTipoFranq.toArray(new TipoFranquicia[listTipoFranq.size()]));
		}
		if (listPctCapAseg.size() > 0) {
			dv.setCapAsegArray(listPctCapAseg.toArray(new PorcentajeCapitalAsegurado[listPctCapAseg.size()]));
		}
		
		// Se establecen las coberturas
		cobertura.setDatosVariables(dv);
	
		
		return cobertura;
	}
	
	/**
	 * Obtiene las subvenciones declaradas de la póliza actualizada
	 * @param anexoModificacion
	 * @param polizaAgroseguro
	 * @return
	 */
	private static es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas getSubvencionesDeclaradas (AnexoModificacion anexoModificacion, es.agroseguro.contratacion.Poliza polizaAgroseguro) {
		
		// Mapa con los códigos de subvenciones que no hay que copiar de la póliza ya que han sido tratadas desde el anexo
		Map<Integer, Boolean> mapaAltas = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> mapaBajas = new HashMap<Integer, Boolean>();
		
		es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvencionesDeclaradas = es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas.Factory.newInstance();
		
		// Listado de subvenciones que tendrá la póliza final
		ArrayList<es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada> listaSubvencionesDeclaradas = new ArrayList<es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada>();
		boolean agregarSeguridadSocial = false;
		
		// Subvenciones del anexo
		for (SubvDeclarada subvDeclaradaAnexo : anexoModificacion.getSubvDeclaradas()) {
			if (Constants.ALTA.equals(subvDeclaradaAnexo
							.getTipomodificacion()) || null == subvDeclaradaAnexo.getTipomodificacion() ) {
				BigDecimal codSubvencion = subvDeclaradaAnexo.getCodsubvencion();
				es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvencionDeclarada = es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada.Factory.newInstance();
				subvencionDeclarada.setTipo(codSubvencion.intValue());			
				listaSubvencionesDeclaradas.add(subvencionDeclarada);
				if (codSubvencion.compareTo(Constants.SUBVENCION20) == 0 || codSubvencion.compareTo(Constants.SUBVENCION30) == 0){
					agregarSeguridadSocial = true;
				}
				// Se marca la subvención como tratada
				mapaAltas.put(subvDeclaradaAnexo.getCodsubvencion().intValue(), true);
			} else if (Constants.BAJA.equals(subvDeclaradaAnexo.getTipomodificacion())) {
				// Se marca la subvención como tratada (no se incluye en el XML)
				mapaBajas.put(subvDeclaradaAnexo.getCodsubvencion().intValue(), true);
			}
		}
		
		// Subvenciones de la póliza
		
		//es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvDeclaradadPoliza = polizaAgroseguro.getSubvencionesDeclaradas();
		//if(subvDeclaradadPoliza != null){
		//	es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvencionDeclaradaArray = subvDeclaradadPoliza.getSubvencionDeclaradaArray();
		//	for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvencionDeclarada : subvencionDeclaradaArray) {
		//		// Si la subvención no ha sido ya incluida en el anexo
		//		if (!mapaAltas.containsKey(subvencionDeclarada.getTipo())
		//				&& !mapaBajas
		//						.containsKey(subvencionDeclarada.getTipo())) {
		//			listaSubvencionesDeclaradas.add(subvencionDeclarada);
		//			if (subvencionDeclarada.getTipo() == Constants.SUBVENCION20.intValue()
		//					|| subvencionDeclarada.getTipo() == Constants.SUBVENCION30.intValue()) {
		//				agregarSeguridadSocial = true;
		//			}
		//		}
		//	}
		//}
		
		// Se establece el listado de subvenciones en la póliza
		//subvencionesDeclaradas.setSubvencionDeclaradaArray(listaSubvencionesDeclaradas.toArray(new es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[listaSubvencionesDeclaradas.size()]));
		
		// Se comprueba si hay que agregar la seguridad social
		if (agregarSeguridadSocial) {
			subvencionesDeclaradas.setSeguridadSocial(getSeguridadSocial(anexoModificacion, polizaAgroseguro));
		}
		
		
		/*** MODIF TAM (22.06.2020) ** Inicio ***/
		// Subvenciones de la póliza
		es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvDeclaradadPoliza = polizaAgroseguro.getSubvencionesDeclaradas();
		if(subvDeclaradadPoliza != null){
			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvencionDeclaradaArray = subvDeclaradadPoliza.getSubvencionDeclaradaArray();
			for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvencionDeclarada : subvencionDeclaradaArray) {
				// Si la subvención no ha sido ya incluida en la póliza
				if (!mapaBajas.containsKey(subvencionDeclarada.getTipo())) {
					listaSubvencionesDeclaradas.add(subvencionDeclarada);
				}
			}
		}	
		// Se establece el listado de subvenciones en la póliza
		
		subvencionesDeclaradas.setSubvencionDeclaradaArray(listaSubvencionesDeclaradas.toArray(new es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[listaSubvencionesDeclaradas.size()]));
		// Se comprueba si hay que añadir la seguridad social
		
		/*** MODIF TAM (22.06.2020) ** Fin ***/
		
		return subvencionesDeclaradas;
		//return listaSubvencionesDeclaradas != null 
		//		&& listaSubvencionesDeclaradas.size() > 0 ? subvencionesDeclaradas
		//		: null;
	}

	/**
	 * Devuelve la seguridad social correspondiente al anexo
	 * @param am
	 * @return
	 */
	private static  es.agroseguro.contratacion.declaracionSubvenciones.SeguridadSocial getSeguridadSocial(AnexoModificacion am, es.agroseguro.contratacion.Poliza p) {
		
		es.agroseguro.contratacion.declaracionSubvenciones.SeguridadSocial segSocial = es.agroseguro.contratacion.declaracionSubvenciones.SeguridadSocial.Factory.newInstance();
		// Si en la situación actualizada hay seguridad social se coge de ahí
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
		
		return segSocial;
	}

	/**
	 * Devuelve el objeto Asegurado de la pÃ³liza a partir del anexo
	 * @param am
	 * @return
	 */
	protected static  es.agroseguro.contratacion.Asegurado getAsegurado(AnexoModificacion am) {

		es.agroseguro.contratacion.Asegurado a = es.agroseguro.contratacion.Asegurado.Factory.newInstance();
		
		a.setNif(am.getPoliza().getAsegurado().getNifcif());
		if (am.getPoliza().getAsegurado().getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(am.getRazsocaseg());
			a.setRazonSocial(rs);
		}
		else{
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(am.getNomaseg());
			nom.setApellido1(am.getApel1aseg());
			
			// Control de nulos del apellido 2
			if (!"".equals(StringUtils.nullToString(am.getApel2aseg()))) {
				nom.setApellido2(am.getApel2aseg());
			}
			
			a.setNombreApellidos(nom);
		}
		
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(am.getBloqueaseg());
		dir.setCp(am.getCodposasegstr());
		dir.setEscalera(am.getEscaseg());
		dir.setLocalidad(am.getNomlocalidad());
		dir.setNumero(am.getNumaseg());
		dir.setPiso(am.getPisoaseg());
		dir.setProvincia(am.getCodprovincia().intValue());
		dir.setVia(am.getCalleaseg());
		a.setDireccion(dir);
		
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(am.getEmail());
		if (!StringUtils.nullToString(am.getTelffijoaseg()).equals(""))
			dContacto.setTelefonoFijo(Integer.parseInt(am.getTelffijoaseg()));
		if (!StringUtils.nullToString(am.getTelfmovilaseg()).equals(""))
			dContacto.setTelefonoMovil(Integer.parseInt(am.getTelfmovilaseg()));
		a.setDatosContacto(dContacto);
		
		return a;
	}
	
	/* Pet. 57626 ** MODIF TAM (26.06.2020) */
	/**
	 * Devuelve el objeto Asegurado de la pÃ³liza a partir del anexo
	 * @param am
	 * @return
	 */
	protected static  es.agroseguro.seguroAgrario.contratacion.Asegurado getAseguradoAnt(AnexoModificacion am) {

		es.agroseguro.seguroAgrario.contratacion.Asegurado a = es.agroseguro.seguroAgrario.contratacion.Asegurado.Factory.newInstance();
		
		a.setNif(am.getPoliza().getAsegurado().getNifcif());
		if (am.getPoliza().getAsegurado().getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(am.getRazsocaseg());
			a.setRazonSocial(rs);
		}
		else{
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(am.getNomaseg());
			nom.setApellido1(am.getApel1aseg());
			
			// Control de nulos del apellido 2
			if (!"".equals(StringUtils.nullToString(am.getApel2aseg()))) {
				nom.setApellido2(am.getApel2aseg());
			}
			
			a.setNombreApellidos(nom);
		}
		
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(am.getBloqueaseg());
		dir.setCp(am.getCodposasegstr());
		dir.setEscalera(am.getEscaseg());
		dir.setLocalidad(am.getNomlocalidad());
		dir.setNumero(am.getNumaseg());
		dir.setPiso(am.getPisoaseg());
		dir.setProvincia(am.getCodprovincia().intValue());
		dir.setVia(am.getCalleaseg());
		a.setDireccion(dir);
		
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(am.getEmail());
		if (!StringUtils.nullToString(am.getTelffijoaseg()).equals(""))
			dContacto.setTelefonoFijo(Integer.parseInt(am.getTelffijoaseg()));
		if (!StringUtils.nullToString(am.getTelfmovilaseg()).equals(""))
			dContacto.setTelefonoMovil(Integer.parseInt(am.getTelfmovilaseg()));
		a.setDatosContacto(dContacto);
		
		return a;
	}
	

	/**
	 * Devuelve los objetos asegurados de la pÃ³liza a partir del anexo
	 * @param am
	 * @return
	 */
	private static  es.agroseguro.contratacion.ObjetosAsegurados getObjetosAsegurados(AnexoModificacion am, Map<BigDecimal, List<String>> listaDatosVariables) {
		
		/**** TAM ****/
		// IPolizaDao polizaDao
		// Objetos asegurados de la situación actualizada de la póliza		
		es.agroseguro.contratacion.ObjetosAsegurados objAseg = es.agroseguro.contratacion.ObjetosAsegurados.Factory.newInstance();
		
		/* Incidencia RGA Pet. 63485 - Fase II (04.12.2020) */
		BigDecimal codLinea = am.getPoliza().getLinea().getCodlinea();

		// Objetos asegurados
		List<ParcelaDocument> listaParcelas = new ArrayList<ParcelaDocument>(am.getParcelas().size());
		for (com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo : am.getParcelas()) {
			// Si la parcela no se ha dado de baja se copia
			if (!Constants.BAJA.equals(parcelaAnexo.getTipomodificacion())) {
					listaParcelas.add(parcelaAnexoToParcelaAgr(parcelaAnexo, listaDatosVariables, codLinea));
				}
			}
		
			org.w3c.dom.Node importedNode;
						
			for (ParcelaDocument parcelaDoc : listaParcelas) { 
				importedNode = objAseg.getDomNode().getOwnerDocument() 
						.importNode(parcelaDoc.getParcela().getDomNode(), true); 
				objAseg.getDomNode().appendChild(importedNode); 
			} 
			
			return objAseg;
			
		}
		 
		/**** TAM ****/

		// Objetos asegurados de la situación actualizada de la póliza		
		//es.agroseguro.contratacion.ObjetosAsegurados newOa = es.agroseguro.contratacion.ObjetosAsegurados.Factory.newInstance();
		
		// Objetos asegurados
		//List<Parcela> listaParcelas = new ArrayList<Parcela>();
		//for (com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo : am.getParcelas()) {
		//	// Si la parcela no se ha dado de baja se copia
		//	if (!Constants.BAJA.equals(parcelaAnexo.getTipomodificacion())) {
		//		listaParcelas.add(parcelaAnexoToParcelaAgr(parcelaAnexo, listaDatosVariables));
		//	}
		//}
		
		// Establece la lista de parcelas en 'ObjetosAsegurados'
		//newOa.setParcelaArray(listaParcelas.toArray(new Parcela[listaParcelas.size()]));
		
		//return newOa;
	
	/**
	 * Devuelve los objetos asegurados de la pÃ³liza a partir del anexo
	 * @param am
	 * @return
	 */
	private static  es.agroseguro.seguroAgrario.contratacion.ObjetosAsegurados getObjetosAseguradosAnt(AnexoModificacion am, Map<BigDecimal, List<String>> listaDatosVariables) {
		
			// Objetos asegurados de la situaciÃ³n actualizada de la pÃ³liza		
			ObjetosAsegurados newOa = ObjetosAsegurados.Factory.newInstance();
			
			// Objetos asegurados
			List<Parcela> listaParcelas = new ArrayList<Parcela>();
			for (com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo : am.getParcelas()) {
				// Si la parcela no se ha dado de baja se copia
				if (!Constants.BAJA.equals(parcelaAnexo.getTipomodificacion())) {
					listaParcelas.add(parcelaAnexoToParcelaAgrAnt(parcelaAnexo, listaDatosVariables));
				}
			}
			
			// Establece la lista de parcelas en 'ObjetosAsegurados'
			newOa.setParcelaArray(listaParcelas.toArray(new Parcela[listaParcelas.size()]));
			
			return newOa;
		}
	
	/**
	 * Transforma una parcela de anexo en una parcela de contrataciÃ³n
	 * @param parcelaAnexo
	 * @return
	 */
	private static  Parcela parcelaAnexoToParcelaAgrAnt(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables) {
		
		es.agroseguro.seguroAgrario.contratacion.Parcela newParcela = es.agroseguro.seguroAgrario.contratacion.Parcela.Factory.newInstance();
		newParcela.setHoja(parcelaAnexo.getHoja().intValue());
		newParcela.setNumero(parcelaAnexo.getNumero().intValue());
		newParcela.setNombre(parcelaAnexo.getNomparcela());
		
		
		/* P00077429 ** MODIF TAM (29/01/2021) ** Inicio */
		/* Incluir el nuevo atributo parcelaAgricola en la etiqueta Parcela */
		/*newParcela.setParcelaAgricola(parcelaAnexo.getParcAgricola());
		/* P00077429 ** MODIF TAM (29/01/2021) ** Fin */
		
		// Si la parcela tiene SIGPAC
		if (parcelaAnexo.getCodprovsigpac() != null) newParcela.setSIGPAC(getSIGPAC(parcelaAnexo));
		// Si tiene Identificacion Catastral
		//else newParcela.setIdentificacionCatastral(getIdentCatastral(parcelaAnexo));
		
		// Establece la ubicacion
		
		/* P00077429 ** MODIF TAM (27/01/2021) ** Inicio */
		/* Incluir solo la etiqueta "Ubicación"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
		Ambito ambito = Ambito.Factory.newInstance();
		
		ambito = getAmbito(parcelaAnexo);
		if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
			newParcela.setUbicacion(getAmbito(parcelaAnexo));
		}
		/* P00077429 ** MODIF TAM (27/01/2021) ** Fin */
		
		// Establece la cosecha de la parcela
		newParcela.setCosecha(getCosechaAnt(parcelaAnexo, listaDatosVariables));
		
		
		return newParcela;
	}
	
	/**
	 * Obtiene la cosecha asociada a la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	private static  Cosecha getCosechaAnt(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables) {
		Cosecha cosecha = Cosecha.Factory.newInstance();
		cosecha.setVariedad(parcelaAnexo.getCodvariedad().intValue());
		cosecha.setCultivo(parcelaAnexo.getCodcultivo().intValue());
		cosecha.setCapitalesAsegurados(getCapitalesAseguradosAnt(parcelaAnexo, listaDatosVariables));
		return cosecha;
	}

	/**
	 * Devuelve los capitales asegurados de la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	private static  CapitalesAsegurados getCapitalesAseguradosAnt(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables) {
		
		// Listado de capitales asegurados que se establecerÃ¡ en la pÃ³liza		 
		List<CapitalAsegurado> listaCA = new ArrayList<CapitalAsegurado>();
		// Recorre los capitales asegurados de la parcela del anexo
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado : parcelaAnexo.getCapitalAsegurados()) {
			// Lo transforma a CA de la pÃ³liza
			listaCA.add(caAnexoToCaPolizaAnt(capitalAsegurado, listaDatosVariables));
		}
		
		CapitalesAsegurados capitalesAsegurados = CapitalesAsegurados.Factory.newInstance();
		capitalesAsegurados.setCapitalAseguradoArray(listaCA.toArray(new CapitalAsegurado[listaCA.size()]));
		return capitalesAsegurados;
	}
	
	/**
	 * Transforma el Capital Asegurado del anexo en un Capital Asegurado de la PÃ³liza
	 * @param caAnexo
	 * @return
	 */
	private static  CapitalAsegurado caAnexoToCaPolizaAnt(com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo, Map<BigDecimal, List<String>> listaDatosVariables) {
		CapitalAsegurado capitalAsegurado = CapitalAsegurado.Factory.newInstance();
		capitalAsegurado.setPrecio(caAnexo.getPrecio());
		capitalAsegurado.setProduccion(caAnexo.getProduccion().intValue());
		capitalAsegurado.setSuperficie(caAnexo.getSuperficie());
		capitalAsegurado.setTipo(caAnexo.getTipoCapital().getCodtipocapital().intValue());
		capitalAsegurado.setDatosVariables(getDatosVariablesAnt(caAnexo, listaDatosVariables));
		return capitalAsegurado;
	}
	
	/**
	 * Transforma los datos variables del capital asegurado de la parcela del anexo en un objeto DatoVariables 
	 * de pÃ³liza
	 * @param caAnexo
	 * @return
	 */
	private static  DatosVariables getDatosVariablesAnt(com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo, Map<BigDecimal, List<String>> listaDatosVariables) {
		
		DatosVariables datosVariables = DatosVariables.Factory.newInstance();
		
		
		for (CapitalDTSVariable dvp : caAnexo.getCapitalDTSVariables()) {
			
			if (listaDatosVariables.containsKey(dvp.getCodconcepto())){
				//Es un dato variable "particular"
				List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());
				
				for (String cad: auxDatVar){
					String[] auxValores = cad.split("#");	
					
					// Id de capital asegurado asociado al dv
					Long auxIdCapAseg = new Long(auxValores[0]);
					if (auxIdCapAseg.equals(dvp.getCapitalAsegurado().getId())){
						setDatoVariableRiesgoAnt(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariables);
					}
				}
			}
			else {
				setDatosVariablesAnt(datosVariables, dvp);
			}
			
		}
		
		return datosVariables;
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setDatosVariablesAnt(DatosVariables datosVariables, CapitalDTSVariable dvp) {
		
		String valor = dvp.getValor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		// Dependiendo del cÃ³digo de concepto del dato variable se establece de una manera
		switch (dvp.getCodconcepto().intValue()) {
			case 362:
				//% CAPITAL ASEGURADO
				List<PorcentajeCapitalAsegurado> lstCA = Arrays.asList(datosVariables.getCapAsegArray());
				ArrayList<PorcentajeCapitalAsegurado> lstCAA = new ArrayList<es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeCapitalAsegurado>(lstCA);
				
				PorcentajeCapitalAsegurado capital = PorcentajeCapitalAsegurado.Factory.newInstance();
				capital.setCodRCub(dvp.getCodriesgocubierto().intValue()); 
				capital.setCPMod(dvp.getCodconceptoppalmod().intValue());
				capital.setValor(Integer.parseInt(valor.toString()));
				
				lstCAA.add(capital);
				
				datosVariables.setCapAsegArray(lstCAA.toArray(new PorcentajeCapitalAsegurado[lstCAA.size()]));
				break;
			case 120:
				//% FRANQUICIA
				// Si no vienen informados el riesgo cubierto y el CPM no se inserta
				if (dvp.getCodriesgocubierto() == null || dvp.getCodconceptoppalmod() == null) break;
				List<PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
				ArrayList<PorcentajeFranquicia> lstFA = new ArrayList<PorcentajeFranquicia>(lstF);
				
				PorcentajeFranquicia f = PorcentajeFranquicia.Factory.newInstance();
				f.setCodRCub(dvp.getCodriesgocubierto().intValue());
				f.setCPMod(dvp.getCodconceptoppalmod().intValue());
				f.setValor(Integer.parseInt(valor.toString()));
				
				lstFA.add(f);
				
				datosVariables.setFranqArray(lstFA.toArray(new PorcentajeFranquicia[lstFA.size()]));
				break;
			case 121:
				//% MINIMO INDEMNIZABLE
				// Si no vienen informados el riesgo cubierto y el CPM no se inserta
				if (dvp.getCodriesgocubierto() == null || dvp.getCodconceptoppalmod() == null) break;
				List<PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
				ArrayList<PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);
				
				PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
				m.setCodRCub(dvp.getCodriesgocubierto().intValue());
				m.setCPMod(dvp.getCodconceptoppalmod().intValue());
				m.setValor(Integer.parseInt(valor.toString()));
				
				lstMIA.add(m);
				
				datosVariables.setMinIndemArray(lstMIA.toArray(new PorcentajeMinimoIndemnizable[lstMIA.size()]));
				break;
			case 174:
				//CALCULO INDEMNIZACION
				List<CalculoIndemnizacion> lstCI = Arrays.asList(datosVariables.getCalcIndemArray());
				ArrayList<CalculoIndemnizacion> lstCIA = new ArrayList<CalculoIndemnizacion>(lstCI);
				
				CalculoIndemnizacion c = CalculoIndemnizacion.Factory.newInstance();
				c.setCodRCub(dvp.getCodriesgocubierto().intValue());
				c.setCPMod(dvp.getCodconceptoppalmod().intValue());
				c.setValor(Integer.parseInt(valor.toString()));
				
				lstCIA.add(c);
				
				datosVariables.setCalcIndemArray(lstCIA.toArray(new CalculoIndemnizacion[lstCIA.size()]));
				break;
			case 169:
				//DAï¿½OS CUBIERTOS
				List<DaniosCubiertos> lstDNC = Arrays.asList(datosVariables.getDnCbtosArray());
				ArrayList<DaniosCubiertos> lstDNCA = new ArrayList<DaniosCubiertos>(lstDNC);
				
				DaniosCubiertos dan = DaniosCubiertos.Factory.newInstance();
				dan.setCodRCub(dvp.getCodriesgocubierto().intValue());
				dan.setCPMod(dvp.getCodconceptoppalmod().intValue());
				dan.setValor(valor+"");
				
				lstDNCA.add(dan);
				
				datosVariables.setDnCbtosArray(lstDNCA.toArray(new DaniosCubiertos[lstDNCA.size()]));
				break;
			case 175:
				//GARANTIZADO
				List<Garantizado> lstG = Arrays.asList(datosVariables.getGarantArray());
				ArrayList<Garantizado> lstGA = new ArrayList<Garantizado>(lstG);

				Garantizado garant = Garantizado.Factory.newInstance();
				garant.setCodRCub(dvp.getCodriesgocubierto().intValue());
				garant.setCPMod(dvp.getCodconceptoppalmod().intValue());
				garant.setValor(Integer.parseInt(valor.toString()));
				lstGA.add(garant);
				
				datosVariables.setGarantArray(lstGA.toArray(new Garantizado[lstGA.size()]));
				break;
			case 363:
				//RIESGO CUBIERTO ELEGIDO
				List<RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
				ArrayList<RiesgoCubiertoElegido> lstRCEA = new ArrayList<RiesgoCubiertoElegido>(lstRCE);

				RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
				rCubEleg.setCodRCub(dvp.getCodriesgocubierto().intValue());
				rCubEleg.setCPMod(dvp.getCodconceptoppalmod().intValue());
				
				//Ponemos la S y la N tambien para cuando venga del copy
				if (valor.equals("-1") || valor.equals("S")) rCubEleg.setValor("S");
				else rCubEleg.setValor("N");
				
				lstRCEA.add(rCubEleg);
				
				datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new RiesgoCubiertoElegido[lstRCEA.size()]));
				break;
			case 170:
				//TIPO FRANQUICIA
				List<TipoFranquicia> lstTF = Arrays.asList(datosVariables.getTipFranqArray());
				ArrayList<TipoFranquicia> lstTFA = new ArrayList<TipoFranquicia>(lstTF);
				
				TipoFranquicia tipFranq = TipoFranquicia.Factory.newInstance();
				tipFranq.setCodRCub(dvp.getCodriesgocubierto().intValue());
				tipFranq.setCPMod(dvp.getCodconceptoppalmod().intValue());
				tipFranq.setValor(valor+"");
				
				lstTFA.add(tipFranq);
				
				datosVariables.setTipFranqArray(lstTFA.toArray(new TipoFranquicia[lstTFA.size()]));
				break;
			case 502:
				//TIPO RENDIMIENTO
				List<TipoRendimiento> lstTR = Arrays.asList(datosVariables.getTipRdtoArray());
				ArrayList<TipoRendimiento> lstTRA = new ArrayList<TipoRendimiento>(lstTR);
				
				TipoRendimiento tipRdto = TipoRendimiento.Factory.newInstance();
				tipRdto.setCodRCub(dvp.getCodriesgocubierto().intValue());
				tipRdto.setCPMod(dvp.getCodconceptoppalmod().intValue());
				tipRdto.setValor(Integer.parseInt(valor.toString()));
				
				lstTRA.add(tipRdto);
				
				datosVariables.setTipRdtoArray(lstTRA.toArray(new TipoRendimiento[lstTRA.size()]));
				break;
			//FIN DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MÃ?DULO Y DEL RIESGO CUBIERTO
				
			//INICIO DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 144:
				//ALTERNATIVA
				Rotacion alt = Rotacion.Factory.newInstance();
				alt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setRot(alt);
				break;
			case 106:
				//CARACT. EXPLOTACION
				CaracteristicasExplotacion carExlp = CaracteristicasExplotacion.Factory.newInstance();
				carExlp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCarExpl(carExlp);
				break;
			case 618:
				//CICLO CULTIVO
				CicloCultivo ciCul = CicloCultivo.Factory.newInstance();
				ciCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCiCul(ciCul);
				break;
			case 765:
				//CODIGO IGP
				CodigoIGP igp = CodigoIGP.Factory.newInstance();
				igp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodIGP(igp);
				break;
			case 620:
				//CODIGO REDUCCION RDTOS.
				List lista = new ArrayList();
				lista.add(valor);
				CodigoReduccionRdtos redRdto = CodigoReduccionRdtos.Factory.newInstance();
				redRdto.setValor(lista);
				
				datosVariables.setCodRedRdto(redRdto);
				break;
			case 107:
				//DENOMINACION ORIGEN
				DenominacionOrigen denOrig = DenominacionOrigen.Factory.newInstance();
				denOrig.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodDO(denOrig);
				break;
			case 109:
				//DENSIDAD
				Densidad dens = Densidad.Factory.newInstance();
				dens.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDens(dens);
				break;
			case 110:
				//DESTINO
				Destino dest = Destino.Factory.newInstance();
				dest.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDest(dest);
				break;
			case 111:
				//EDAD
				Edad edad = Edad.Factory.newInstance();
				edad.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdad(edad);
				break;
			case 112:
				//FECHA RECOLECCION
				FechaRecoleccion fRecol = FechaRecoleccion.Factory.newInstance();
				GregorianCalendar gcFRecol = new GregorianCalendar();
				
				try {
					gcFRecol.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e1) {
					logger.error("Error al parsear la FechaRecoleccion", e1);
				}
				
				fRecol.setValor(gcFRecol);
				
				datosVariables.setFecRecol(fRecol);
				break;
			case 113:
				//FECHA SIEMBRA/TRASPLANTE
				FechaSiembraTrasplante fSiembraTransplante = FechaSiembraTrasplante.Factory.newInstance();
				GregorianCalendar gcFSiemb = new GregorianCalendar();
				
				try {
					gcFSiemb.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e) {
					logger.error("Error al parsear la FechaSiembraTrasplante", e);
				}
				
				fSiembraTransplante.setValor(gcFSiemb);
				
				datosVariables.setFecSiemTrasp(fSiembraTransplante);
				break;
				
			case 114:
				//INDIC.GASTOS SALVAMENTO
				IndicadorGastosSalvamento gastSalv = IndicadorGastosSalvamento.Factory.newInstance();
				gastSalv.setValor(valor.toString());
				
				datosVariables.setIndGastSalv(gastSalv);
				break;
			case 124:
				//MEDIDA PREVENTIVA
				List<Integer> listaMedPrev = new ArrayList<Integer>();
				
				for (String val : valor.toString().split(" ")){
					listaMedPrev.add(Integer.parseInt(val));
				}
				
				
				MedidaPreventiva medPrev = MedidaPreventiva.Factory.newInstance();
				medPrev.setValor(listaMedPrev);
				
				datosVariables.setMedPrev(medPrev);
				break;
			case 767:
				//METROS CUADRADOS
				MetrosCuadrados met2 = MetrosCuadrados.Factory.newInstance();
				met2.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet2(met2);
				break;
			case 766:
				//METROS LINEALES
				MetrosLineales met = MetrosLineales.Factory.newInstance();
				met.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet(met);
				break;
			case 617:
				//NÂº AÃ?OS DESDE PODA
				NumeroAniosDesdePoda nadp = NumeroAniosDesdePoda.Factory.newInstance();
				nadp.setValor((new BigDecimal(valor.toString())).intValue());
				
				datosVariables.setNadp(nadp);
				break;
			case 117:
				//NUMERO UNIDADES
				NumeroUnidades numUds = NumeroUnidades.Factory.newInstance();
				numUds.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNumUnid(numUds);
				break;
			case 133:
				//PRACTICA CULTURAL
				PracticaCultural praCul = PracticaCultural.Factory.newInstance();
				praCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPraCult(praCul);
				break;
			case 131:
				//SISTEMA CONDUCCION
				SistemaConduccion sCond = SistemaConduccion.Factory.newInstance();
				sCond.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCond(sCond);
				break;
			case 123:
				//SISTEMA CULTIVO
				SistemaCultivo sCul = SistemaCultivo.Factory.newInstance();
				sCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCult(sCul);
				break;
			case 616:
				//SISTEMA PRODUCCION
				SistemaProduccion sProd = SistemaProduccion.Factory.newInstance();
				sProd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProd(sProd);
				break;
			case 621:
				//SISTEMA PROTECCION
				SistemaProteccion sProt = SistemaProteccion.Factory.newInstance();
				sProt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProt(sProt);
				break;
			case 116:
				//TIPO MARCO PLANTACION
				TipoMarcoPlantacion tmp = TipoMarcoPlantacion.Factory.newInstance();
				tmp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMcoPlant(tmp);
				break;
			case 173:
				//TIPO PLANTACION
				TipoPlantacion tp = TipoPlantacion.Factory.newInstance();
				tp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipPlant(tp);
				break;
			case 132:
				//TIPO SUB.DECLARADA PARCEL
				List listaTsdp = new ArrayList();
				listaTsdp.add(valor);
				TipoSubvencionDeclaradaParcela tsdp = TipoSubvencionDeclaradaParcela.Factory.newInstance();
				tsdp.setValor(listaTsdp);
				
				datosVariables.setTipSubDecPar(tsdp);
				break;
			case 768:
				//VALOR FIJO
				ValorFijo vf = ValorFijo.Factory.newInstance();
				vf.setValor(new BigDecimal(valor.toString()));
				
				datosVariables.setValFij(vf);
				break;
			case 778:
				//Tipo instalacion
				TipoInstalacion tipInst = TipoInstalacion.Factory.newInstance();
				tipInst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipInst(tipInst);
				break;
			case 873:
				// Material Cubierta
				MaterialCubierta mCub = MaterialCubierta.Factory.newInstance();
				mCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatCubi(mCub);
				break;
			case 874:
				// Edad Cubierta
				EdadCubierta eCub = EdadCubierta.Factory.newInstance();
				eCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadCubi(eCub);
				break;
			case 875:
				// Material Estructuras
				MaterialEstructura mEst = MaterialEstructura.Factory.newInstance();
				mEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatEstr(mEst);
				break;
			case 876:
				// Edad Estrucutra
				EdadEstructura eEst = EdadEstructura.Factory.newInstance();
				eEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadEstr(eEst);
				break;
			case 879:
				// Codigo Certificado
				CodigoCertificado cc = CodigoCertificado.Factory.newInstance();
				cc.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodCert(cc);
				break;
			case 752:
				// Tipo terreno
				TipoTerreno tt = TipoTerreno.Factory.newInstance();
				tt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipTer(tt);
				break;
			case 753:
				// Tipo masa
				TipoMasa tm = TipoMasa.Factory.newInstance();
				tm.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMas(tm);
				break;
			case 754:
				// Pendiente
				Pendiente p = Pendiente.Factory.newInstance();
				p.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPend(p);
				break;
			case 944:
				// Nï¿½ aï¿½os desde descorche
				NumeroAniosDesdeDescorche nadd = NumeroAniosDesdeDescorche.Factory.newInstance();
				nadd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNadd(nadd);
				break;					
			//FIN DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			
			default:
				//No hacemos nada
				break;
			
		}
	}
	
	private static void setDatoVariableRiesgoAnt(BigDecimal codconcepto,
			BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Object valor,
			DatosVariables datosVariables)  {
		
		//Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		//Comienzo del tratamiento del dato variable: 157OK, 140OK, 137OK, 136OK, 134OK, 138OK, 135OK, 139OK

		switch (codconcepto.intValue()){
			//INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 140:
				//DIAS INICIO GARANTIAS
				List<DiasLimiteGarantias> lstDIG = Arrays.asList(datosVariables.getDIGarantArray());
				ArrayList<DiasLimiteGarantias> lstDIGA = new ArrayList<DiasLimiteGarantias>(lstDIG);

				DiasLimiteGarantias dLim = DiasLimiteGarantias.Factory.newInstance();
				dLim.setCodRCub(codriesgocubierto.intValue());
				dLim.setCPMod(codconceptoppalmod.intValue());
				dLim.setValor(Integer.parseInt(valor.toString()));
				lstDIGA.add(dLim);
				
				datosVariables.setDIGarantArray(lstDIGA.toArray(new DiasLimiteGarantias[lstDIGA.size()]));
				break;
			case 136:
				//DURACION MAX.GARANT(DIAS)
				List<DiasLimiteGarantias> lstDMGD = Arrays.asList(datosVariables.getDurMaxGarantDiasArray());
				ArrayList<DiasLimiteGarantias> lstDMGDA = new ArrayList<DiasLimiteGarantias>(lstDMGD);
				
				DiasLimiteGarantias durMaxD = DiasLimiteGarantias.Factory.newInstance();
				durMaxD.setCodRCub(codriesgocubierto.intValue());
				durMaxD.setCPMod(codconceptoppalmod.intValue());
				durMaxD.setValor(Integer.parseInt(valor.toString()));
				lstDMGDA.add(durMaxD);
				
				datosVariables.setDurMaxGarantDiasArray(lstDMGDA.toArray(new DiasLimiteGarantias[lstDMGDA.size()]));
				break;
			case 137:
				//DURACION MAX.GARAN(MESES)
				List<MesesLimiteGarantias> lstDMGM = Arrays.asList(datosVariables.getDurMaxGarantMesesArray());
				ArrayList<MesesLimiteGarantias> lstDMGMA = new ArrayList<MesesLimiteGarantias>(lstDMGM);
				
				MesesLimiteGarantias durMaxM = MesesLimiteGarantias.Factory.newInstance();
				durMaxM.setCodRCub(codriesgocubierto.intValue());
				durMaxM.setCPMod(codconceptoppalmod.intValue());
				durMaxM.setValor(new BigDecimal(valor.toString()));
				lstDMGMA.add(durMaxM);
				
				datosVariables.setDurMaxGarantMesesArray(lstDMGMA.toArray(new MesesLimiteGarantias[lstDMGMA.size()]));
				break;
			case 135:
				//EST.FEN.FIN GARANTIAS
				List<EstadoFenologicoLimiteGarantias> lstEFFG = Arrays.asList(datosVariables.getEFFGarantArray());
				ArrayList<EstadoFenologicoLimiteGarantias> lstEFFGA = new ArrayList<EstadoFenologicoLimiteGarantias>(lstEFFG);
				
				EstadoFenologicoLimiteGarantias estFenFin = EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenFin.setCodRCub(codriesgocubierto.intValue());
				estFenFin.setCPMod(codconceptoppalmod.intValue());
				estFenFin.setValor(valor+"");
				lstEFFGA.add(estFenFin);
				
				datosVariables.setEFFGarantArray(lstEFFGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFFGA.size()]));
				break;
			case 139:
				//EST.FEN.INICIO GARANTIAS
				List<EstadoFenologicoLimiteGarantias> lstEFIG = Arrays.asList(datosVariables.getEFIGarantArray());
				ArrayList<EstadoFenologicoLimiteGarantias> lstEFIGA = new ArrayList<EstadoFenologicoLimiteGarantias>(lstEFIG);

				EstadoFenologicoLimiteGarantias estFenIni = EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenIni.setCodRCub(codriesgocubierto.intValue());
				estFenIni.setCPMod(codconceptoppalmod.intValue());
				estFenIni.setValor(valor+"");
				lstEFIGA.add(estFenIni);
				
				datosVariables.setEFIGarantArray(lstEFIGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFIGA.size()]));
				break;
			case 134:
				//FECHA FIN GARANTIAS
				List<FechaLimiteGarantias> lstFLG = Arrays.asList(datosVariables.getFecFGarantArray());
				ArrayList<FechaLimiteGarantias> lstFLGA = new ArrayList<FechaLimiteGarantias>(lstFLG);

				FechaLimiteGarantias fLim = FechaLimiteGarantias.Factory.newInstance();
				fLim.setCodRCub(codriesgocubierto.intValue());
				fLim.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFLG = new GregorianCalendar();
				try {
					gcFLG.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha fin de garantias", e);
				}
				fLim.setValor(gcFLG);
				//ASF - 5/9/2012 - Antes de aï¿½adir la fecha de fin de garantÃ­as, comprobamos que no tengamos ya esa combinacion.
				boolean existe = false;
				for (FechaLimiteGarantias flg: lstFLGA){
					logger.debug("Elemento a aï¿½adir: " + fLim.getCodRCub() + ", " + fLim.getCPMod() + ", " + fLim.getValor());
					logger.debug("Elemento de la lista: " + flg.getCodRCub() + ", " + flg.getCPMod() + ", " + flg.getValor());
					logger.debug("1. " + (flg.getCodRCub() == fLim.getCodRCub()));
					logger.debug("2. " + (flg.getCPMod() == fLim.getCPMod()));
					logger.debug("3. " + (flg.getValor().equals(fLim.getValor())));
					if (flg.getCodRCub() == fLim.getCodRCub() && flg.getCPMod() == fLim.getCPMod() && flg.getValor().equals(fLim.getValor())){
						existe = true;
						break;
					}
				}
				if (!existe){
					lstFLGA.add(fLim);
					datosVariables.setFecFGarantArray(lstFLGA.toArray(new FechaLimiteGarantias[lstFLGA.size()]));
				}
				break;
			case 138:
				//FECHA INICIO GARANTIAS
				List<FechaLimiteGarantias> lstFIG = Arrays.asList(datosVariables.getFecIGarantArray());
				ArrayList<FechaLimiteGarantias> lstFIGA = new ArrayList<FechaLimiteGarantias>(lstFIG);

				FechaLimiteGarantias fIni = FechaLimiteGarantias.Factory.newInstance();
				fIni.setCodRCub(codriesgocubierto.intValue());
				fIni.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFIG = new GregorianCalendar();
				try {
					gcFIG.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha de inicio de garantias", e);
				}
				fIni.setValor(gcFIG);
				lstFIGA.add(fIni);
				
				datosVariables.setFecIGarantArray(lstFIGA.toArray(new FechaLimiteGarantias[lstFIGA.size()]));
				break;
			case 141:
				//MESES INICIO GARANTIAS
				List<MesesLimiteGarantias> lstMIG = Arrays.asList(datosVariables.getMIGarantArray());
				ArrayList<MesesLimiteGarantias> lstMIGA = new ArrayList<MesesLimiteGarantias>(lstMIG);

				MesesLimiteGarantias migarant = MesesLimiteGarantias.Factory.newInstance();
				migarant.setCodRCub(codriesgocubierto.intValue());
				migarant.setCPMod(codconceptoppalmod.intValue());
				migarant.setValor(new BigDecimal(valor.toString()));
				lstMIGA.add(migarant);
				
				datosVariables.setMIGarantArray(lstMIGA.toArray(new MesesLimiteGarantias[lstMIGA.size()]));
				break;
			case 157:
				//PERIODO GARANTIAS
				List<PeriodoGarantias> lstPG = Arrays.asList(datosVariables.getPerGarantArray());
				ArrayList<PeriodoGarantias> lstPGA = new ArrayList<PeriodoGarantias>(lstPG);

				PeriodoGarantias perGarant = PeriodoGarantias.Factory.newInstance();
				perGarant.setCodRCub(codriesgocubierto.intValue());
				perGarant.setCPMod(codconceptoppalmod.intValue());
				perGarant.setValor(Integer.parseInt(valor.toString()));
				lstPGA.add(perGarant);
				
				datosVariables.setPerGarantArray(lstPGA.toArray(new PeriodoGarantias[lstPGA.size()]));
				break;
			case 120:
				//% FRANQUICIA				
				List<PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
				ArrayList<PorcentajeFranquicia> lstFA = new ArrayList<PorcentajeFranquicia>(lstF);
				
				PorcentajeFranquicia f = PorcentajeFranquicia.Factory.newInstance();
				f.setCodRCub(codriesgocubierto.intValue());
				f.setCPMod(codconceptoppalmod.intValue());
				f.setValor(Integer.parseInt(valor.toString()));
				
				lstFA.add(f);
				
				datosVariables.setFranqArray(lstFA.toArray(new PorcentajeFranquicia[lstFA.size()]));
				break;
			case 121:
				//% MINIMO INDEMNIZABLE
				List<PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
				ArrayList<PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);
				
				PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
				m.setCodRCub(codriesgocubierto.intValue());
				m.setCPMod(codconceptoppalmod.intValue());
				m.setValor(Integer.parseInt(valor.toString()));
				
				lstMIA.add(m);
				
				datosVariables.setMinIndemArray(lstMIA.toArray(new PorcentajeMinimoIndemnizable[lstMIA.size()]));
				break;
			default:
				//No hacemos nada
				break;
			
		}
	}
	
	/**
	 * Obtiene las subvenciones declaradas de la póliza actualizada
	 * @param anexoModificacion
	 * @param polizaAgroseguro
	 * @return
	 */
	private static  SubvencionesDeclaradas getSubvencionesDeclaradasAnt (AnexoModificacion anexoModificacion, Poliza polizaAgroseguro) {
		
		// Mapa con los códigos de subvenciones que no hay que copiar de la póliza ya que han sido tratadas desde el anexo
		Map<Integer, Boolean> mapaAltas = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> mapaBajas = new HashMap<Integer, Boolean>();
		SubvencionesDeclaradas subvencionesDeclaradas = SubvencionesDeclaradas.Factory.newInstance();
		// Listado de subvenciones que tendrÃ¡ la póliza final
		ArrayList<SubvencionDeclarada> listaSubvencionesDeclaradas = new ArrayList<SubvencionDeclarada>();
		boolean agregarSeguridadSocial = false;
		
		// Subvenciones del anexo
		for (SubvDeclarada subvDeclaradaAnexo : anexoModificacion.getSubvDeclaradas()) {
			if (Constants.ALTA.equals(subvDeclaradaAnexo
							.getTipomodificacion())) {
				BigDecimal codSubvencion = subvDeclaradaAnexo.getCodsubvencion();
				SubvencionDeclarada subvencionDeclarada = SubvencionDeclarada.Factory.newInstance();
				subvencionDeclarada.setTipo(codSubvencion.intValue());			
				listaSubvencionesDeclaradas.add(subvencionDeclarada);
				if (codSubvencion.compareTo(Constants.SUBVENCION20) == 0 || codSubvencion.compareTo(Constants.SUBVENCION30) == 0){
					agregarSeguridadSocial = true;
				}
				// Se marca la subvención como tratada
				mapaAltas.put(subvDeclaradaAnexo.getCodsubvencion().intValue(), true);
			} else if (Constants.BAJA.equals(subvDeclaradaAnexo.getTipomodificacion())) {
				// Se marca la subvención como tratada (no se incluye en el XML)
				mapaBajas.put(subvDeclaradaAnexo.getCodsubvencion().intValue(), true);
			}
		}
		
		// Subvenciones de la póliza
		SubvencionesDeclaradas subvDeclaradadPoliza = polizaAgroseguro.getSubvencionesDeclaradas();
		if(subvDeclaradadPoliza != null){
			SubvencionDeclarada[] subvencionDeclaradaArray = subvDeclaradadPoliza.getSubvencionDeclaradaArray();
			for (SubvencionDeclarada subvencionDeclarada : subvencionDeclaradaArray) {
				// Si la subvención no ha sido ya incluida en el anexo
				if (!mapaAltas.containsKey(subvencionDeclarada.getTipo())
						&& !mapaBajas
								.containsKey(subvencionDeclarada.getTipo())) {
					listaSubvencionesDeclaradas.add(subvencionDeclarada);
					if (subvencionDeclarada.getTipo() == Constants.SUBVENCION20.intValue()
							|| subvencionDeclarada.getTipo() == Constants.SUBVENCION30.intValue()) {
						agregarSeguridadSocial = true;
					}
				}
			}
		}
		
		// Se establece el listado de subvenciones en la póliza
		subvencionesDeclaradas.setSubvencionDeclaradaArray(listaSubvencionesDeclaradas.toArray(new SubvencionDeclarada[listaSubvencionesDeclaradas.size()]));
		
		// Se comprueba si hay que agregar la seguridad social
		if (agregarSeguridadSocial) {
			subvencionesDeclaradas.setSeguridadSocial(getSeguridadSocialAnt(anexoModificacion, polizaAgroseguro));
		}
		
		return listaSubvencionesDeclaradas != null
				&& listaSubvencionesDeclaradas.size() > 0 ? subvencionesDeclaradas
				: null;
	}
	
	
	/**
	 * Devuelve la seguridad social correspondiente al anexo
	 * @param am
	 * @return
	 */
	private static  SeguridadSocial getSeguridadSocialAnt(AnexoModificacion am, Poliza p) {
		
		SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
		// Si en la situaciÃ³n actualizada hay seguridad social se coge de ahÃ­
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
		
		return segSocial;
	}
	
	/* Taty Fin (26.06.2020) ** */
	
	
	
	
	/**
	 * Transforma una parcela de anexo en una parcela de contratación
	 * @param parcelaAnexo
	 * @return
	 */
	private static  es.agroseguro.contratacion.parcela.ParcelaDocument parcelaAnexoToParcelaAgr (com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables, BigDecimal codLinea) {
		
		es.agroseguro.contratacion.parcela.ParcelaDocument newParcela = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.newInstance();
		es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela p = es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela.Factory.newInstance();
		
		p.setHoja(parcelaAnexo.getHoja().intValue());
		p.setNumero(parcelaAnexo.getNumero().intValue());
		p.setNombre(parcelaAnexo.getNomparcela());
		
		/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
		/* Incluir el nuevo atributo parcelaAgricola en la etiqueta Parcela*/
		/* DE MOMENTO Y POR PETICI�N DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENV�A EN NING�N XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
		/*if (parcelaAnexo.getParcAgricola() != null) {
			p.setParcelaAgricola(parcelaAnexo.getParcAgricola());	
		}*/
		/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
		
		// Si la parcela tiene SIGPAC
		if (parcelaAnexo.getCodprovsigpac() != null) p.setSIGPAC(getSIGPAC(parcelaAnexo));
		// Si tiene Identificación Catastral
		//else newParcela.setIdentificacionCatastral(getIdentCatastral(parcelaAnexo));
		
		// Establece la ubicación
		/* P00077429 ** MODIF TAM (27/01/2021) ** Inicio */
		/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
		Ambito ambito = Ambito.Factory.newInstance();
		
		ambito = getAmbito(parcelaAnexo);
		if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
			p.setUbicacion(getAmbito(parcelaAnexo));
		}
		/* P00077429 ** MODIF TAM (27/01/2021) ** Fin */
		
		
		// Establece la cosecha de la parcela
		p.setCosecha(getCosecha(parcelaAnexo, listaDatosVariables, codLinea));
		
		newParcela.setParcela(p);
		
		return newParcela;
	}
	
	/**
	 * Obtiene la cosecha asociada a la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	private static  es.agroseguro.contratacion.parcela.Cosecha getCosecha(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables, BigDecimal codLinea) {
		es.agroseguro.contratacion.parcela.Cosecha cosecha = es.agroseguro.contratacion.parcela.Cosecha.Factory.newInstance();
		cosecha.setVariedad(parcelaAnexo.getCodvariedad().intValue());
		cosecha.setCultivo(parcelaAnexo.getCodcultivo().intValue());
		cosecha.setCapitalesAsegurados(getCapitalesAsegurados(parcelaAnexo, listaDatosVariables, codLinea));
		return cosecha;
	}

	/**
	 * Devuelve los capitales asegurados de la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	private static es.agroseguro.contratacion.parcela.CapitalesAsegurados getCapitalesAsegurados(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, 
			Map<BigDecimal, List<String>> listaDatosVariables, BigDecimal codLinea) {
		
		// Listado de capitales asegurados que se establecerÃ¡ en la pÃ³liza		 
		List<es.agroseguro.contratacion.parcela.CapitalAsegurado> listaCA = new ArrayList<es.agroseguro.contratacion.parcela.CapitalAsegurado>();
		es.agroseguro.contratacion.parcela.CapitalesAsegurados capitalesAsegurados = es.agroseguro.contratacion.parcela.CapitalesAsegurados.Factory.newInstance();
		
		

		// Recorre los capitales asegurados de la parcela del anexo
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado : parcelaAnexo.getCapitalAsegurados()) {
			// Lo transforma a CA de la póliza
			
			listaCA.add(caAnexoToCaPoliza(capitalAsegurado, listaDatosVariables, codLinea));
		}
		
		capitalesAsegurados.setCapitalAseguradoArray((es.agroseguro.contratacion.parcela.CapitalAsegurado[]) listaCA.toArray(new es.agroseguro.contratacion.parcela.CapitalAsegurado[listaCA.size()]));
		
		return capitalesAsegurados;
	}
	
	/**
	 * Transforma el Capital Asegurado del anexo en un Capital Asegurado de la Póliza
	 * @param caAnexo
	 * @return
	 */
	private static  es.agroseguro.contratacion.parcela.CapitalAsegurado caAnexoToCaPoliza (com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo, 
				Map<BigDecimal, List<String>> listaDatosVariables, BigDecimal codLinea) {
		es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory.newInstance();
		capitalAsegurado.setPrecio(caAnexo.getPrecio());
		capitalAsegurado.setProduccion(caAnexo.getProduccion().intValue());
		capitalAsegurado.setSuperficie(caAnexo.getSuperficie());
		capitalAsegurado.setTipo(caAnexo.getTipoCapital().getCodtipocapital().intValue());
		capitalAsegurado.setDatosVariables(getDatosVariables (caAnexo, listaDatosVariables, codLinea));
		return capitalAsegurado;
	}
	
	/**
	 * Transforma los datos variables del capital asegurado de la parcela del anexo en un objeto DatoVariables 
	 * de pÃ³liza
	 * @param caAnexo
	 * @return
	 */
	private static  es.agroseguro.contratacion.datosVariables.DatosVariables getDatosVariables (com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo, Map<BigDecimal, List<String>> listaDatosVariables, BigDecimal codLinea) {
		
		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();
		
		
		for (CapitalDTSVariable dvp : caAnexo.getCapitalDTSVariables()) {
			
			if (listaDatosVariables.containsKey(dvp.getCodconcepto())){
				//Es un dato variable "particular"
				List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());
				
				for (String cad: auxDatVar){
					String[] auxValores = cad.split("#");	
					
					// Id de capital asegurado asociado al dv
					Long auxIdCapAseg = new Long(auxValores[0]);
					if (auxIdCapAseg.equals(dvp.getCapitalAsegurado().getId())){
						
						/* Resol. Incidencia RGA Pet. 63485-Fase II (04.12.2020) */
						/* Para el tipo de Capital 1 de la línea 301 no hay que enviar los datos
						 * del riesgo (indicado por RGA)
						 */
						
						if (codLinea.compareTo(new BigDecimal (301))==0){
							if (caAnexo.getTipoCapital().getCodtipocapital().compareTo(new BigDecimal(0))==0) {
								setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariables);
							}
						}else {
							setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariables);
						}
						
					}
				}
			}
			else {
				
				/* Resol. Incidencia RGA Pet. 63485-Fase II (04.12.2020) */
				/* Para el tipo de Capital 1 Plantones)de la línea 301 no hay que enviar los datos
				 * del riesgo (indicado por RGA)
				 */
				if (codLinea.compareTo(new BigDecimal (301))==0){
					if (caAnexo.getTipoCapital().getCodtipocapital().compareTo(new BigDecimal(0))==0) {
						setDatosVariables(datosVariables, dvp);												
					}else{
						if (dvp.getCodconcepto().compareTo(new BigDecimal(363))!=0) {
							setDatosVariables(datosVariables, dvp);
						}	
					}
						
				}else {	
					setDatosVariables(datosVariables, dvp);
				}
			}
			
		}
		
		return datosVariables;
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setDatosVariables(es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, CapitalDTSVariable dvp) {
		
		String valor = dvp.getValor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		// Dependiendo del cÃ³digo de concepto del dato variable se establece de una manera
		switch (dvp.getCodconcepto().intValue()) {
			case 362:
				//% CAPITAL ASEGURADO
				List<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> lstCA = Arrays.asList(datosVariables.getCapAsegArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> lstCAA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado>(lstCA);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado capital = es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado.Factory.newInstance();
				capital.setCodRCub(dvp.getCodriesgocubierto().intValue()); 
				capital.setCPMod(dvp.getCodconceptoppalmod().intValue());
				capital.setValor(Integer.parseInt(valor.toString()));
				
				lstCAA.add(capital);
				
				datosVariables.setCapAsegArray(lstCAA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[lstCAA.size()]));
				break;
			case 120:
				//% FRANQUICIA
				// Si no vienen informados el riesgo cubierto y el CPM no se inserta
				if (dvp.getCodriesgocubierto() == null || dvp.getCodconceptoppalmod() == null) break;
				List<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstFA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia>(lstF);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia f = es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia.Factory.newInstance();
				f.setCodRCub(dvp.getCodriesgocubierto().intValue());
				f.setCPMod(dvp.getCodconceptoppalmod().intValue());
				f.setValor(Integer.parseInt(valor.toString()));
				
				lstFA.add(f);
				
				datosVariables.setFranqArray(lstFA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[lstFA.size()]));
				break;
			case 121:
				//% MINIMO INDEMNIZABLE
				// Si no vienen informados el riesgo cubierto y el CPM no se inserta
				if (dvp.getCodriesgocubierto() == null || dvp.getCodconceptoppalmod() == null) break;
				List<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable>(lstMI);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable m = es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable.Factory.newInstance();
				m.setCodRCub(dvp.getCodriesgocubierto().intValue());
				m.setCPMod(dvp.getCodconceptoppalmod().intValue());
				m.setValor(Integer.parseInt(valor.toString()));
				
				lstMIA.add(m);
				
				datosVariables.setMinIndemArray(lstMIA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[lstMIA.size()]));
				break;
			case 174:
				//CALCULO INDEMNIZACION
				List<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> lstCI = Arrays.asList(datosVariables.getCalcIndemArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> lstCIA = new ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion>(lstCI);
				
				es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c = es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion.Factory.newInstance();
				c.setCodRCub(dvp.getCodriesgocubierto().intValue());
				c.setCPMod(dvp.getCodconceptoppalmod().intValue());
				c.setValor(Integer.parseInt(valor.toString()));
				
				lstCIA.add(c);
				
				datosVariables.setCalcIndemArray(lstCIA.toArray(new es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[lstCIA.size()]));
				break;
			case 169:
				//DAï¿½OS CUBIERTOS
				List<es.agroseguro.contratacion.datosVariables.DaniosCubiertos> lstDNC = Arrays.asList(datosVariables.getDnCbtosArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.DaniosCubiertos> lstDNCA = new ArrayList<es.agroseguro.contratacion.datosVariables.DaniosCubiertos>(lstDNC);
				
				es.agroseguro.contratacion.datosVariables.DaniosCubiertos dan = es.agroseguro.contratacion.datosVariables.DaniosCubiertos.Factory.newInstance();
				dan.setCodRCub(dvp.getCodriesgocubierto().intValue());
				dan.setCPMod(dvp.getCodconceptoppalmod().intValue());
				dan.setValor(valor+"");
				
				lstDNCA.add(dan);
				
				datosVariables.setDnCbtosArray(lstDNCA.toArray(new es.agroseguro.contratacion.datosVariables.DaniosCubiertos[lstDNCA.size()]));
				break;
			case 175:
				//GARANTIZADO
				List<es.agroseguro.contratacion.datosVariables.Garantizado> lstG = Arrays.asList(datosVariables.getGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado> lstGA = new ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado>(lstG);

				es.agroseguro.contratacion.datosVariables.Garantizado garant = es.agroseguro.contratacion.datosVariables.Garantizado.Factory.newInstance();
				garant.setCodRCub(dvp.getCodriesgocubierto().intValue());
				garant.setCPMod(dvp.getCodconceptoppalmod().intValue());
				garant.setValor(Integer.parseInt(valor.toString()));
				lstGA.add(garant);
				
				datosVariables.setGarantArray(lstGA.toArray(new es.agroseguro.contratacion.datosVariables.Garantizado[lstGA.size()]));
				break;
			case 363:
				//RIESGO CUBIERTO ELEGIDO
				List<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCEA = new ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido>(lstRCE);

				es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCubEleg = es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido.Factory.newInstance();
				rCubEleg.setCodRCub(dvp.getCodriesgocubierto().intValue());
				rCubEleg.setCPMod(dvp.getCodconceptoppalmod().intValue());
				
				//Ponemos la S y la N tambien para cuando venga del copy
				if (valor.equals("-1") || valor.equals("S")) rCubEleg.setValor("S");
				else rCubEleg.setValor("N");
				
				lstRCEA.add(rCubEleg);
				
				datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[lstRCEA.size()]));
				break;
			case 170:
				//TIPO FRANQUICIA
				List<es.agroseguro.contratacion.datosVariables.TipoFranquicia> lstTF = Arrays.asList(datosVariables.getTipFranqArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.TipoFranquicia> lstTFA = new ArrayList<es.agroseguro.contratacion.datosVariables.TipoFranquicia>(lstTF);
				
				es.agroseguro.contratacion.datosVariables.TipoFranquicia tipFranq = es.agroseguro.contratacion.datosVariables.TipoFranquicia.Factory.newInstance();
				tipFranq.setCodRCub(dvp.getCodriesgocubierto().intValue());
				tipFranq.setCPMod(dvp.getCodconceptoppalmod().intValue());
				tipFranq.setValor(valor+"");
				
				lstTFA.add(tipFranq);
				
				datosVariables.setTipFranqArray(lstTFA.toArray(new es.agroseguro.contratacion.datosVariables.TipoFranquicia[lstTFA.size()]));
				break;
			case 502:
				//TIPO RENDIMIENTO
				List<es.agroseguro.contratacion.datosVariables.TipoRendimiento> lstTR = Arrays.asList(datosVariables.getTipRdtoArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.TipoRendimiento> lstTRA = new ArrayList<es.agroseguro.contratacion.datosVariables.TipoRendimiento>(lstTR);
				
				es.agroseguro.contratacion.datosVariables.TipoRendimiento tipRdto = es.agroseguro.contratacion.datosVariables.TipoRendimiento.Factory.newInstance();
				tipRdto.setCodRCub(dvp.getCodriesgocubierto().intValue());
				tipRdto.setCPMod(dvp.getCodconceptoppalmod().intValue());
				tipRdto.setValor(Integer.parseInt(valor.toString()));
				
				lstTRA.add(tipRdto);
				
				datosVariables.setTipRdtoArray(lstTRA.toArray(new es.agroseguro.contratacion.datosVariables.TipoRendimiento[lstTRA.size()]));
				break;
			//FIN DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MÃDULO Y DEL RIESGO CUBIERTO
				
			//INICIO DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 144:
				//ALTERNATIVA
				es.agroseguro.contratacion.datosVariables.Rotacion alt = es.agroseguro.contratacion.datosVariables.Rotacion.Factory.newInstance();
				alt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setRot(alt);
				break;
			case 106:
				//CARACT. EXPLOTACION
				es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion carExlp = es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion.Factory.newInstance();
				carExlp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCarExpl(carExlp);
				break;
			case 618:
				//CICLO CULTIVO
				es.agroseguro.contratacion.datosVariables.CicloCultivo ciCul = es.agroseguro.contratacion.datosVariables.CicloCultivo.Factory.newInstance();
				ciCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCiCul(ciCul);
				break;
			case 765:
				//CODIGO IGP
				es.agroseguro.contratacion.datosVariables.CodigoIGP igp = es.agroseguro.contratacion.datosVariables.CodigoIGP.Factory.newInstance();
				igp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodIGP(igp);
				break;
			case 620:
				//CODIGO REDUCCION RDTOS.
				List lista = new ArrayList();
				lista.add(valor);
				es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos redRdto = es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos.Factory.newInstance();
				redRdto.setValor(lista);
				
				datosVariables.setCodRedRdto(redRdto);
				break;
			case 107:
				//DENOMINACION ORIGEN
				es.agroseguro.contratacion.datosVariables.DenominacionOrigen denOrig = es.agroseguro.contratacion.datosVariables.DenominacionOrigen.Factory.newInstance();
				denOrig.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodDO(denOrig);
				break;
			case 109:
				//DENSIDAD
				es.agroseguro.contratacion.datosVariables.Densidad dens = es.agroseguro.contratacion.datosVariables.Densidad.Factory.newInstance();
				dens.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDens(dens);
				break;
			case 110:
				//DESTINO
				es.agroseguro.contratacion.datosVariables.Destino dest = es.agroseguro.contratacion.datosVariables.Destino.Factory.newInstance();
				dest.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDest(dest);
				break;
			case 111:
				//EDAD
				es.agroseguro.contratacion.datosVariables.Edad edad = es.agroseguro.contratacion.datosVariables.Edad.Factory.newInstance();
				edad.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdad(edad);
				break;
			case 112:
				//FECHA RECOLECCION
				es.agroseguro.contratacion.datosVariables.FechaRecoleccion fRecol = es.agroseguro.contratacion.datosVariables.FechaRecoleccion.Factory.newInstance();
				GregorianCalendar gcFRecol = new GregorianCalendar();
				
				try {
					gcFRecol.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e1) {
					logger.error("Error al parsear la FechaRecoleccion", e1);
				}
				
				fRecol.setValor(gcFRecol);
				
				datosVariables.setFecRecol(fRecol);
				break;
			case 113:
				//FECHA SIEMBRA/TRASPLANTE
				es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante fSiembraTransplante = es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante.Factory.newInstance();
				GregorianCalendar gcFSiemb = new GregorianCalendar();
				
				try {
					gcFSiemb.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e) {
					logger.error("Error al parsear la FechaSiembraTrasplante", e);
				}
				
				fSiembraTransplante.setValor(gcFSiemb);
				
				datosVariables.setFecSiemTrasp(fSiembraTransplante);
				break;
				
			case 114:
				//INDIC.GASTOS SALVAMENTO
				es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento gastSalv = es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento.Factory.newInstance();
				gastSalv.setValor(valor.toString());
				
				datosVariables.setIndGastSalv(gastSalv);
				break;
			case 124:
				//MEDIDA PREVENTIVA
				List<Integer> listaMedPrev = new ArrayList<Integer>();
				
				for (String val : valor.toString().split(" ")){
					listaMedPrev.add(Integer.parseInt(val));
				}
				
				es.agroseguro.contratacion.datosVariables.MedidaPreventiva medPrev = es.agroseguro.contratacion.datosVariables.MedidaPreventiva.Factory.newInstance();
				medPrev.setValor(listaMedPrev);
				
				datosVariables.setMedPrev(medPrev);
				break;
			case 767:
				//METROS CUADRADOS
				es.agroseguro.contratacion.datosVariables.MetrosCuadrados met2 = es.agroseguro.contratacion.datosVariables.MetrosCuadrados.Factory.newInstance();
				met2.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet2(met2);
				break;
			case 766:
				//METROS LINEALES
				es.agroseguro.contratacion.datosVariables.MetrosLineales met = es.agroseguro.contratacion.datosVariables.MetrosLineales.Factory.newInstance();
				met.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet(met);
				break;
			case 617:
				//NÂº AÃOS DESDE PODA
				es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda nadp = es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda.Factory.newInstance();
				nadp.setValor((new BigDecimal(valor.toString())).intValue());
				
				datosVariables.setNadp(nadp);
				break;
			case 117:
				//NUMERO UNIDADES
				es.agroseguro.contratacion.datosVariables.NumeroUnidades numUds = es.agroseguro.contratacion.datosVariables.NumeroUnidades.Factory.newInstance();
				numUds.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNumUnid(numUds);
				break;
			case 133:
				//PRACTICA CULTURAL
				es.agroseguro.contratacion.datosVariables.PracticaCultural praCul = es.agroseguro.contratacion.datosVariables.PracticaCultural.Factory.newInstance();
				praCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPraCult(praCul);
				break;
			case 131:
				//SISTEMA CONDUCCION
				es.agroseguro.contratacion.datosVariables.SistemaConduccion sCond = es.agroseguro.contratacion.datosVariables.SistemaConduccion.Factory.newInstance();
				sCond.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCond(sCond);
				break;
			case 123:
				//SISTEMA CULTIVO
				es.agroseguro.contratacion.datosVariables.SistemaCultivo sCul = es.agroseguro.contratacion.datosVariables.SistemaCultivo.Factory.newInstance();
				sCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCult(sCul);
				break;
			case 616:
				//SISTEMA PRODUCCION
				es.agroseguro.contratacion.datosVariables.SistemaProduccion sProd = es.agroseguro.contratacion.datosVariables.SistemaProduccion.Factory.newInstance();
				sProd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProd(sProd);
				break;
			case 621:
				//SISTEMA PROTECCION
				es.agroseguro.contratacion.datosVariables.SistemaProteccion sProt = es.agroseguro.contratacion.datosVariables.SistemaProteccion.Factory.newInstance();
				sProt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProt(sProt);
				break;
			case 116:
				//TIPO MARCO PLANTACION
				es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion tmp = es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion.Factory.newInstance();
				tmp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMcoPlant(tmp);
				break;
			case 173:
				//TIPO PLANTACION
				es.agroseguro.contratacion.datosVariables.TipoPlantacion tp = es.agroseguro.contratacion.datosVariables.TipoPlantacion.Factory.newInstance();
				tp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipPlant(tp);
				break;
			case 132:
				//TIPO SUB.DECLARADA PARCEL
				List listaTsdp = new ArrayList();
				listaTsdp.add(valor);
				es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela tsdp = es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela.Factory.newInstance();
				tsdp.setValor(listaTsdp);
				
				datosVariables.setTipSubDecPar(tsdp);
				break;
			case 768:
				//VALOR FIJO
				es.agroseguro.contratacion.datosVariables.ValorFijo vf = es.agroseguro.contratacion.datosVariables.ValorFijo.Factory.newInstance();
				vf.setValor(new BigDecimal(valor.toString()));
				
				datosVariables.setValFij(vf);
				break;
			case 778:
				//Tipo instalacion
				es.agroseguro.contratacion.datosVariables.TipoInstalacion tipInst = es.agroseguro.contratacion.datosVariables.TipoInstalacion.Factory.newInstance();
				tipInst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipInst(tipInst);
				break;
			case 873:
				// Material Cubierta
				es.agroseguro.contratacion.datosVariables.MaterialCubierta mCub = es.agroseguro.contratacion.datosVariables.MaterialCubierta.Factory.newInstance();
				mCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatCubi(mCub);
				break;
			case 874:
				// Edad Cubierta
				es.agroseguro.contratacion.datosVariables.EdadCubierta eCub = es.agroseguro.contratacion.datosVariables.EdadCubierta.Factory.newInstance();
				eCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadCubi(eCub);
				break;
			case 875:
				// Material Estructuras
				es.agroseguro.contratacion.datosVariables.MaterialEstructura mEst = es.agroseguro.contratacion.datosVariables.MaterialEstructura.Factory.newInstance();
				mEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatEstr(mEst);
				break;
			case 876:
				// Edad Estrucutra
				es.agroseguro.contratacion.datosVariables.EdadEstructura eEst = es.agroseguro.contratacion.datosVariables.EdadEstructura.Factory.newInstance();
				eEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadEstr(eEst);
				break;
			case 879:
				// Codigo Certificado
				es.agroseguro.contratacion.datosVariables.CodigoCertificado cc = es.agroseguro.contratacion.datosVariables.CodigoCertificado.Factory.newInstance();
				cc.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodCert(cc);
				break;
			case 752:
				// Tipo terreno
				es.agroseguro.contratacion.datosVariables.TipoTerreno tt = es.agroseguro.contratacion.datosVariables.TipoTerreno.Factory.newInstance();
				tt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipTer(tt);
				break;
			case 753:
				// Tipo masa
				es.agroseguro.contratacion.datosVariables.TipoMasa tm = es.agroseguro.contratacion.datosVariables.TipoMasa.Factory.newInstance();
				tm.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMas(tm);
				break;
			case 754:
				// Pendiente
				es.agroseguro.contratacion.datosVariables.Pendiente p = es.agroseguro.contratacion.datosVariables.Pendiente.Factory.newInstance();
				p.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPend(p);
				break;
			case 944:
				// Nï¿½ aï¿½os desde descorche
				es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche nadd = es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche.Factory.newInstance();
				nadd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNadd(nadd);
				break;					
			//FIN DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			
			default:
				//No hacemos nada
				break;
			
		}
	}

	/**
	 * Devuelve la ubicaciÃ³n de la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	protected static  Ambito getAmbito(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		Ambito ambito = Ambito.Factory.newInstance();
		ambito.setComarca(parcelaAnexo.getCodcomarca().intValue());
		ambito.setProvincia(parcelaAnexo.getCodprovincia().intValue());
		ambito.setSubtermino(parcelaAnexo.getSubtermino()+ "");
		ambito.setTermino(parcelaAnexo.getCodtermino().intValue());
		return ambito;
	}

	/**
	 * Devuelve el objeto IdentificacionCatastral asociado a la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	protected static  IdentificacionCatastral getIdentCatastral(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		IdentificacionCatastral ic = IdentificacionCatastral.Factory.newInstance();
		ic.setParcela(parcelaAnexo.getParcela_1());
		ic.setParcela(parcelaAnexo.getPoligono());
		return ic;
	}

	/**
	 * Devuelve el objeto SIGPAC asociado a la parcela del anexo
	 * @param parcelaAnexo
	 * @return
	 */
	protected static  SIGPAC getSIGPAC(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		SIGPAC sigpac = SIGPAC.Factory.newInstance();
		sigpac.setAgregado(parcelaAnexo.getAgrsigpac().intValue());
		sigpac.setParcela(parcelaAnexo.getParcelasigpac().intValue());
		sigpac.setPoligono(parcelaAnexo.getPoligonosigpac().intValue());
		sigpac.setProvincia(parcelaAnexo.getCodprovsigpac().intValue());
		sigpac.setRecinto(parcelaAnexo.getRecintosigpac().intValue());
		sigpac.setTermino(parcelaAnexo.getCodtermsigpac().intValue());
		sigpac.setZona(parcelaAnexo.getZonasigpac().intValue());
		return sigpac;
	}
	
	private static void setDatoVariableRiesgo(BigDecimal codconcepto,
			BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Object valor,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables)  {
		
		//Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		//Comienzo del tratamiento del dato variable: 157OK, 140OK, 137OK, 136OK, 134OK, 138OK, 135OK, 139OK

		switch (codconcepto.intValue()){
			//INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 140:
				//DIAS INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDIG = Arrays.asList(datosVariables.getDIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias>(lstDIG);

				es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias dLim = es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias.Factory.newInstance();
				dLim.setCodRCub(codriesgocubierto.intValue());
				dLim.setCPMod(codconceptoppalmod.intValue());
				dLim.setValor(Integer.parseInt(valor.toString()));
				lstDIGA.add(dLim);
				
				datosVariables.setDIGarantArray(lstDIGA.toArray(new es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias[lstDIGA.size()]));
				break;
			case 136:
				//DURACION MAX.GARANT(DIAS)
				List<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDMGD = Arrays.asList(datosVariables.getDurMaxGarantDiasArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDMGDA = new ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias>(lstDMGD);
				
				es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias durMaxD = es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias.Factory.newInstance();
				durMaxD.setCodRCub(codriesgocubierto.intValue());
				durMaxD.setCPMod(codconceptoppalmod.intValue());
				durMaxD.setValor(Integer.parseInt(valor.toString()));
				lstDMGDA.add(durMaxD);
				
				datosVariables.setDurMaxGarantDiasArray(lstDMGDA.toArray(new es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias[lstDMGDA.size()]));
				break;
			case 137:
				//DURACION MAX.GARAN(MESES)
				List<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstDMGM = Arrays.asList(datosVariables.getDurMaxGarantMesesArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstDMGMA = new ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias>(lstDMGM);
				
				es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias durMaxM = es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias.Factory.newInstance();
				durMaxM.setCodRCub(codriesgocubierto.intValue());
				durMaxM.setCPMod(codconceptoppalmod.intValue());
				durMaxM.setValor(new BigDecimal(valor.toString()));
				lstDMGMA.add(durMaxM);
				
				datosVariables.setDurMaxGarantMesesArray(lstDMGMA.toArray(new es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias[lstDMGMA.size()]));
				break;
			case 135:
				//EST.FEN.FIN GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFFG = Arrays.asList(datosVariables.getEFFGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFFGA = new ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias>(lstEFFG);
				
				es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias estFenFin = es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenFin.setCodRCub(codriesgocubierto.intValue());
				estFenFin.setCPMod(codconceptoppalmod.intValue());
				estFenFin.setValor(valor+"");
				lstEFFGA.add(estFenFin);
				
				datosVariables.setEFFGarantArray(lstEFFGA.toArray(new es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias[lstEFFGA.size()]));
				break;
			case 139:
				//EST.FEN.INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFIG = Arrays.asList(datosVariables.getEFIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias>(lstEFIG);

				es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias estFenIni = es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenIni.setCodRCub(codriesgocubierto.intValue());
				estFenIni.setCPMod(codconceptoppalmod.intValue());
				estFenIni.setValor(valor+"");
				lstEFIGA.add(estFenIni);
				
				datosVariables.setEFIGarantArray(lstEFIGA.toArray(new es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias[lstEFIGA.size()]));
				break;
			case 134:
				//FECHA FIN GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFLG = Arrays.asList(datosVariables.getFecFGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFLGA = new ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias>(lstFLG);

				es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias fLim = es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias.Factory.newInstance();
				fLim.setCodRCub(codriesgocubierto.intValue());
				fLim.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFLG = new GregorianCalendar();
				try {
					gcFLG.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha fin de garantias", e);
				}
				fLim.setValor(gcFLG);
				//ASF - 5/9/2012 - Antes de añadir la fecha de fin de garantías, comprobamos que no tengamos ya esa combinacion.
				boolean existe = false;
				for (es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias flg: lstFLGA){
					logger.debug("Elemento a añadir: " + fLim.getCodRCub() + ", " + fLim.getCPMod() + ", " + fLim.getValor());
					logger.debug("Elemento de la lista: " + flg.getCodRCub() + ", " + flg.getCPMod() + ", " + flg.getValor());
					logger.debug("1. " + (flg.getCodRCub() == fLim.getCodRCub()));
					logger.debug("2. " + (flg.getCPMod() == fLim.getCPMod()));
					logger.debug("3. " + (flg.getValor().equals(fLim.getValor())));
					if (flg.getCodRCub() == fLim.getCodRCub() && flg.getCPMod() == fLim.getCPMod() && flg.getValor().equals(fLim.getValor())){
						existe = true;
						break;
					}
				}
				if (!existe){
					lstFLGA.add(fLim);
					datosVariables.setFecFGarantArray(lstFLGA.toArray(new es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias[lstFLGA.size()]));
				}
				break;
			case 138:
				//FECHA INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFIG = Arrays.asList(datosVariables.getFecIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias>(lstFIG);

				es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias fIni = es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias.Factory.newInstance();
				fIni.setCodRCub(codriesgocubierto.intValue());
				fIni.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFIG = new GregorianCalendar();
				try {
					gcFIG.setTime(sdf.parse(valor.toString()));
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha de inicio de garantias", e);
				}
				fIni.setValor(gcFIG);
				lstFIGA.add(fIni);
				
				datosVariables.setFecIGarantArray(lstFIGA.toArray(new es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias[lstFIGA.size()]));
				break;
			case 141:
				//MESES INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstMIG = Arrays.asList(datosVariables.getMIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstMIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias>(lstMIG);

				es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias migarant = es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias.Factory.newInstance();
				migarant.setCodRCub(codriesgocubierto.intValue());
				migarant.setCPMod(codconceptoppalmod.intValue());
				migarant.setValor(new BigDecimal(valor.toString()));
				lstMIGA.add(migarant);
				
				datosVariables.setMIGarantArray(lstMIGA.toArray(new es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias[lstMIGA.size()]));
				break;
			case 157:
				//PERIODO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.PeriodoGarantias> lstPG = Arrays.asList(datosVariables.getPerGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PeriodoGarantias> lstPGA = new ArrayList<es.agroseguro.contratacion.datosVariables.PeriodoGarantias>(lstPG);

				es.agroseguro.contratacion.datosVariables.PeriodoGarantias perGarant = es.agroseguro.contratacion.datosVariables.PeriodoGarantias.Factory.newInstance();
				perGarant.setCodRCub(codriesgocubierto.intValue());
				perGarant.setCPMod(codconceptoppalmod.intValue());
				perGarant.setValor(Integer.parseInt(valor.toString()));
				lstPGA.add(perGarant);
				
				datosVariables.setPerGarantArray(lstPGA.toArray(new es.agroseguro.contratacion.datosVariables.PeriodoGarantias[lstPGA.size()]));
				break;
			case 120:
				//% FRANQUICIA				
				List<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstFA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia>(lstF);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia f = es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia.Factory.newInstance();
				f.setCodRCub(codriesgocubierto.intValue());
				f.setCPMod(codconceptoppalmod.intValue());
				f.setValor(Integer.parseInt(valor.toString()));
				logger.debug("Elemento a añadir % FRANQUICIA: " + f.getCodRCub() + ", " + f.getCPMod() + ", " + f.getValor());
				
				boolean existeF = false;
				for(es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia concepto : lstF) {
					if(concepto.getCodRCub() == f.getCodRCub() && concepto.getCPMod() == f.getCPMod() && concepto.getValor() == f.getValor()) {
						existeF = true;
					}
				}
				
				if(!existeF) {
					lstFA.add(f);
				}
				
				datosVariables.setFranqArray(lstFA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[lstFA.size()]));
				break;
			case 121:
				//% MINIMO INDEMNIZABLE
				List<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable>(lstMI);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable m = es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable.Factory.newInstance();
				m.setCodRCub(codriesgocubierto.intValue());
				m.setCPMod(codconceptoppalmod.intValue());
				m.setValor(Integer.parseInt(valor.toString()));
				logger.debug("Elemento a añadir % MINIMO INDEMNIZABLE: " + m.getCodRCub() + ", " + m.getCPMod() + ", " + m.getValor());
				
				boolean existeM = false;
				for(es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable concepto : lstMI) {
					if(concepto.getCodRCub() == m.getCodRCub() && concepto.getCPMod() == m.getCPMod() && concepto.getValor() == m.getValor()) {
						existeM = true;
					}
				}
				
				if(!existeM) {
					lstMIA.add(m);
				}
												
				datosVariables.setMinIndemArray(lstMIA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[lstMIA.size()]));
				break;
			default:
				//No hacemos nada
				break;
			
		}
	}
	
	
	/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */ 

	public static CuentaCobroSiniestros getCuentaCobroSinAnexo(AnexoModificacion am) { 
		
		CuentaCobroSiniestros cuentaCobroSiniestros = CuentaCobroSiniestros.Factory.newInstance();
  
		boolean lineaSup2021 = false;
		lineaSup2021 = PolizaUnificadaTransformer.obtener_lineaSup2021(am.getPoliza().getLinea().getCodplan(),
					am.getPoliza().getLinea().getCodlinea(), am.getPoliza().getLinea().isLineaGanado());
		
		// COBRO SINIESTROS
		boolean modExentoCuentaCobroSiniestros = false;
		
		String[] auxArr = bundle_siniestros.getString("mods.exentos.envio").split(",");
		for (String aux : auxArr) {
			String auxStr = am.getPoliza().getLinea().getCodlinea() + "|" + am.getPoliza().getCodmodulo();
			if (auxStr.toUpperCase().equals(aux.trim().toUpperCase())) {
				modExentoCuentaCobroSiniestros = true;
				break;
			}
		}
		if (lineaSup2021 && !modExentoCuentaCobroSiniestros) {
			if (am.getPoliza().getPagoPolizas() != null && !am.getPoliza().getPagoPolizas().isEmpty()) {
				for (PagoPoliza pp : am.getPoliza().getPagoPolizas()) {
					if (pp != null) {
						// SE ENVIA SIEMPRE EN CARGO EN CUENTA O PAGO MANUAL
						// EN DOMICILIACION SE ENVIA SOLO SI EL DESTINATARIO ES DIFERENTE AL ASEGURADO
						if (ArrayUtils.contains(new BigDecimal[] { Constants.CARGO_EN_CUENTA, Constants.PAGO_MANUAL },
								pp.getTipoPago())
								|| !Character.valueOf('A').equals(pp.getDestinatarioDomiciliacion())) {
							if (!StringUtils.isNullOrEmpty(pp.getIban2())
									&& !StringUtils.isNullOrEmpty(pp.getCccbanco2())) {
								cuentaCobroSiniestros.setIban(pp.getIban2() + pp.getCccbanco2());
								break;
							}
						}
					}
				}				
			}
		}
		
		return cuentaCobroSiniestros;
		// FIN COBRO SINIESTROS
	}


}
