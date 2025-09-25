package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.config.DatoVariableDefault;
import com.rsi.agp.dao.tables.copy.CoberturaPoliza;
import com.rsi.agp.dao.tables.copy.DatoVariableParcela;
import com.rsi.agp.dao.tables.copy.PagoPoliza;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.iTipos.DatosContacto;
import es.agroseguro.iTipos.Direccion;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.iTipos.SIGPAC;
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
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroAniosDesdePoda;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroUnidades;
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
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoPlantacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoRendimiento;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoSubvencionDeclaradaParcela;
import es.agroseguro.seguroAgrario.recibos.AmbitoAgro;
import es.agroseguro.seguroAgrario.recibos.Asegurado;
import es.agroseguro.seguroAgrario.recibos.CapitalAsegurado;
import es.agroseguro.seguroAgrario.recibos.Cobertura;
import es.agroseguro.seguroAgrario.recibos.Colectivo;
import es.agroseguro.seguroAgrario.recibos.Cosecha;
import es.agroseguro.seguroAgrario.recibos.DistribucionCoste;
import es.agroseguro.seguroAgrario.recibos.Entidad;
import es.agroseguro.seguroAgrario.recibos.Fase;
import es.agroseguro.seguroAgrario.recibos.FaseDocument;
import es.agroseguro.seguroAgrario.recibos.IdentificacionCatastral;
import es.agroseguro.seguroAgrario.recibos.Individual;
import es.agroseguro.seguroAgrario.recibos.ObjetosAsegurados;
import es.agroseguro.seguroAgrario.recibos.Pago;
import es.agroseguro.seguroAgrario.recibos.Parcela;
import es.agroseguro.seguroAgrario.recibos.SubvencionDeclarada;
import es.agroseguro.seguroAgrario.recibos.SubvencionEnesa;
import es.agroseguro.seguroAgrario.recibos.SubvencionOrganismo;
import es.agroseguro.seguroAgrario.recibos.SubvencionesDeclaradas;
import es.agroseguro.seguroAgrario.recibos.SubvencionesEnesa;
import es.agroseguro.seguroAgrario.recibos.SubvencionesOrganismos;


public class PolizaCopyManager implements IManager {

	private static final Log logger = LogFactory.getLog(PolizaCopyManager.class);
	private IPolizaCopyDao polizaCopyDao;
	private IPolizaDao polizaDao;
	private WebServicesManager webServicesManager;
	private IDiccionarioDatosDao  diccionarioDatosDao;
	
	public Long descargarPolizaCopyWS(String tipoReferencia, BigDecimal codplan, String refPoliza,String realPath) throws BusinessException{
		try{
			
			return descargarPolizaCopyWS(tipoReferencia, codplan, refPoliza, null, null, realPath);
			
		}catch(Exception e){
			throw new BusinessException("Error al descargar la copy de la poliza " +tipoReferencia + " "+ refPoliza, e); 
		}
	}

	public Long descargarPolizaCopyWS(String tipoReferencia, BigDecimal codPlan, String refPoliza, BigDecimal codRecibo, Date fechaEmisionRecibo, String realPath) throws BusinessException{

		FaseDocument situacionActualizadaPoliza = null;
		
		try{
		
			situacionActualizadaPoliza = webServicesManager.consultaEstado(tipoReferencia, codPlan, refPoliza, codRecibo, fechaEmisionRecibo, realPath, null);
			
			
		}catch(SeguimientoServiceException sse){
			logger.error("El WebService de Seguimiento no ha devuelto la situación actualizada de la póliza. Esta excepción no es necesariamente un error"/*,sse*/);
		}

		try {
		
			if(situacionActualizadaPoliza != null){
				logger.debug("COPY: " + situacionActualizadaPoliza.toString());
				fechaEmisionRecibo = situacionActualizadaPoliza.getFase().getFechaEmisionRecibo().getTime();
				
				// MPM - 08/05/12
				// Se controla que el método que carga la copy no genere excepciones producidas porque haya varias copys para una misma fecha y póliza
				com.rsi.agp.dao.tables.copy.Poliza polizaCopyOrigen = null;
				
				try {
					polizaCopyOrigen = polizaCopyDao.existeCopyPolizaByReferenciaAndFecha(tipoReferencia, refPoliza, fechaEmisionRecibo);
				}
				catch (Exception e) {
					logger.error("PolizaCopyManager.descargarPolizaCopyWS - Ocurrió un error al obtener la copy por referencia y fecha", e);
				}
				
				if(polizaCopyOrigen == null){
					
					com.rsi.agp.dao.tables.copy.Poliza polizaCopy = generarCopyPoliza(tipoReferencia, situacionActualizadaPoliza);
					polizaCopyDao.saveOrUpdate(polizaCopy);
					return polizaCopy.getId();
					
				}else { //Si existe y tiene la misma fecha se actualizan los datos variables
					if (polizaCopyOrigen.getFecemisionrecibo().equals(fechaEmisionRecibo)){
						
						// MPM - 31/07/2012
						// Optimización del proceso de actualización de datos variables desde la copy
						GregorianCalendar gcIni = new GregorianCalendar();
						
						actualizaDatosVariables(tipoReferencia,situacionActualizadaPoliza,polizaCopyOrigen);
						
						GregorianCalendar gcFin = new GregorianCalendar();
						
						Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
						logger.debug("Tiempo empleado en actualizar la copy: " + tiempo + " milisegundos");
						
						return polizaCopyOrigen.getId();
					}
				}
			}
			
			return null;

		}catch (DAOException daoe) {
			throw new BusinessException("Error al guardar la copy de la póliza " + refPoliza, daoe);
		}catch (Exception daoe) {
			throw new BusinessException("Error inesperado al guardar la copy de la póliza " + refPoliza, daoe);
		}
		
		
		
	}
	

	
	private void actualizaDatosVariables (String tipoReferencia,FaseDocument situacionActualizadaPoliza,com.rsi.agp.dao.tables.copy.Poliza polizaCopyOrigen){
		
		Fase fase = situacionActualizadaPoliza.getFase();
		Colectivo colectivoWS = null;
		Individual individualWS = null;
		es.agroseguro.seguroAgrario.recibos.Poliza polizaWS = null;
		
		// DAA 24/01/2013 hay que tener en cuenta que no venga el colectivo en el XML
		if(fase.getColectivoArray().length != 0){
			colectivoWS = fase.getColectivoArray()[0];
			polizaWS = colectivoWS.getPolizaArray()[0];
			
		}else {
			individualWS = fase.getIndividualArray()[0];
			polizaWS = individualWS.getPoliza();
		}
		
		Cobertura coberturaWS;
		if (tipoReferencia.equals("P")){
			coberturaWS = polizaWS.getCoberturaPrincipal();
		}else{
			coberturaWS = polizaWS.getCoberturaComplementario();
		}
		
		// MPM - 30/07/2012. Optimización en la actualización de la copy
		// Obtiene todos los datos variables que aparecen a nivel de cobertura en la copy descargada
		DatosVariables datosVariablesCoberturaWS = coberturaWS.getDatosVariables();
		if (datosVariablesCoberturaWS != null){
			// Construye la cadena correspondiente a los datos variables que hay actualizar en las parcelas de la póliza
			logger.debug("Compone la cadena de datos variables que se actualizarán desde la copy");
			String cadena = componerCadenaDV(datosVariablesCoberturaWS);
			logger.debug("Cadena de DV: " + cadena);
			
			// Si hay algún DV para actualizar, se llama al PL correspondiente  
			if (!"".equals(cadena)) {
				int numParcelas = polizaWS.getObjetosAsegurados().sizeOfParcelaArray();					
				logger.debug("Numero de parcelas que vienen en la copy: " + numParcelas);
				
				// Se lanza el PL encargado de actualizar los DV de la copy		
				logger.debug("Se lanza el PL encargado de actualizar los DV de la copy");
				polizaCopyDao.actualizaDVCopy(cadena, numParcelas, polizaCopyOrigen.getId());
			}
		}
	}

	/**
	 * Devuelve una cadena de texto con todos los códigos de concepto y valores correspondientes a los datos variables
	 * de la copy descargada
	 * @param datosVariablesCoberturaWS
	 * @return
	 */
	private String componerCadenaDV(DatosVariables datosVariablesCoberturaWS) {
		
		String cadena = "";
		
		// DV
		if (datosVariablesCoberturaWS.getRot() != null) cadena += componerCadena(datosVariablesCoberturaWS.getRot());
		if (datosVariablesCoberturaWS.getCiCul() != null) cadena += componerCadena(datosVariablesCoberturaWS.getCiCul());
		if (datosVariablesCoberturaWS.getCodDO() != null) cadena += componerCadena(datosVariablesCoberturaWS.getCodDO());
		if (datosVariablesCoberturaWS.getCodRedRdto() != null) cadena += componerCadena(datosVariablesCoberturaWS.getCodRedRdto());
		if (datosVariablesCoberturaWS.getDens() != null) cadena += componerCadena(datosVariablesCoberturaWS.getDens());
		if (datosVariablesCoberturaWS.getDest() != null) cadena += componerCadena(datosVariablesCoberturaWS.getDest());
		if (datosVariablesCoberturaWS.getEdad() != null) cadena += componerCadena(datosVariablesCoberturaWS.getEdad());
		if (datosVariablesCoberturaWS.getFecRecol() != null) cadena += componerCadena(datosVariablesCoberturaWS.getFecRecol());
		if (datosVariablesCoberturaWS.getFecSiemTrasp() != null) cadena += componerCadena(datosVariablesCoberturaWS.getFecSiemTrasp());
		if (datosVariablesCoberturaWS.getIndGastSalv() != null) cadena += componerCadena(datosVariablesCoberturaWS.getIndGastSalv());
		if (datosVariablesCoberturaWS.getMedPrev() != null) cadena += componerCadena(datosVariablesCoberturaWS.getMedPrev());
		if (datosVariablesCoberturaWS.getNadp() != null) cadena += componerCadena(datosVariablesCoberturaWS.getNadp());
		if (datosVariablesCoberturaWS.getNumUnid() != null) cadena += componerCadena(datosVariablesCoberturaWS.getNumUnid());
		if (datosVariablesCoberturaWS.getPraCult() != null) cadena += componerCadena(datosVariablesCoberturaWS.getPraCult());
		if (datosVariablesCoberturaWS.getSisCond() != null) cadena += componerCadena(datosVariablesCoberturaWS.getSisCond());
		if (datosVariablesCoberturaWS.getSisCult() != null) cadena += componerCadena(datosVariablesCoberturaWS.getSisCult());
		if (datosVariablesCoberturaWS.getSisProd() != null) cadena += componerCadena(datosVariablesCoberturaWS.getSisProd());
		if (datosVariablesCoberturaWS.getSisProt() != null) cadena += componerCadena(datosVariablesCoberturaWS.getSisProt());
		if (datosVariablesCoberturaWS.getTipMcoPlant() != null) cadena += componerCadena(datosVariablesCoberturaWS.getTipMcoPlant());
		if (datosVariablesCoberturaWS.getTipPlant() != null) cadena += componerCadena(datosVariablesCoberturaWS.getTipPlant());
		if (datosVariablesCoberturaWS.getTipSubDecPar() != null) cadena += componerCadena(datosVariablesCoberturaWS.getTipSubDecPar());
		
		return cadena;
	}
	
	/**
	 * Devuelve la cadena con formato 'codconcepto=valor;' correspondiente a los datos variables de la copy
	 * @param datoVariableParcelaWS
	 * @return
	 */
	private String componerCadena (Object datoVariableParcelaWS) {
		return (getCodigoConcepto (datoVariableParcelaWS) + "=" + HTMLUtils.getProperty(datoVariableParcelaWS, "valor").toString().replaceAll("-", "/") + ";");
	}
	
	
	private com.rsi.agp.dao.tables.copy.Poliza generarCopyPoliza(String tipoReferencia, FaseDocument situacionActualizadaPoliza){
		logger.debug("-----------generarCopyPoliza------------");
		logger.debug("FaseDocument->situacionActualizadaPoliza");
		logger.debug("----------------------------------------");
		logger.debug(situacionActualizadaPoliza.toString());
		
		Fase fase = situacionActualizadaPoliza.getFase();
		Colectivo colectivoWS = null;
		Individual individualWS = null;
		es.agroseguro.seguroAgrario.recibos.Poliza polizaWS = null;
		RazonSocial razonSocialWS = null;
		Direccion direccionWS = null;
		DatosContacto datosContactoWS = null;
		
		// DAA 24/01/2013 hay que tener en cuenta que no venga el colectivo en el XML
		if(fase.getColectivoArray().length != 0){
			colectivoWS = fase.getColectivoArray()[0];
			
			polizaWS = colectivoWS.getPolizaArray()[0];
			
			razonSocialWS = colectivoWS.getRazonSocial();
			direccionWS = colectivoWS.getDireccion();
			datosContactoWS = colectivoWS.getDatosContacto();
			
		}else{ //Individual
			individualWS = fase.getIndividualArray()[0];
			
			polizaWS = individualWS.getPoliza();
			
			razonSocialWS = individualWS.getRazonSocial();
			direccionWS = individualWS.getDireccion();
			datosContactoWS = individualWS.getDatosContacto();
		}

		Asegurado aseguradoWS = polizaWS.getAsegurado();
		
		Entidad entidadWS = polizaWS.getEntidad();
		
		
		DistribucionCoste distribucionCosteWS;
		Pago[] pagosBancoWS;
		Cobertura coberturaWS;
		if (tipoReferencia.equals(Constants.MODULO_POLIZA_PRINCIPAL+"")){
			distribucionCosteWS = polizaWS.getDistribucionCostePrincipal();
			pagosBancoWS = polizaWS.getPagoPrincipalArray();
			coberturaWS = polizaWS.getCoberturaPrincipal();
		}
		else{
			distribucionCosteWS = polizaWS.getDistribucionCosteComplementario();
			pagosBancoWS = polizaWS.getPagoComplementarioArray();
			coberturaWS = polizaWS.getCoberturaComplementario();
		}
		DatosVariables datosVariablesCoberturaWS = coberturaWS.getDatosVariables();
		
		SubvencionesEnesa subvencionesEnesaWS = polizaWS.getSubvencionesEnesa();
		SubvencionesDeclaradas subvencionesDeclaradasWS = polizaWS.getSubvencionesDeclaradas();
		SubvencionesOrganismos subvencionesOrganismosWS = polizaWS.getSubvencionesOrganismos();
		
		ObjetosAsegurados objetosAseguradosWS = polizaWS.getObjetosAsegurados();
		Parcela[] parcelasWS = objetosAseguradosWS.getParcelaArray();
		
		// Póliza
		
		com.rsi.agp.dao.tables.copy.Poliza polizaCOPY = new com.rsi.agp.dao.tables.copy.Poliza();
		
		polizaCOPY.setCodfase(new Integer(fase.getFase()).toString());
		polizaCOPY.setCodplan(new Integer(fase.getPlan()).toString());
		polizaCOPY.setCodrecibo(new BigDecimal(polizaWS.getRecibo()));
		polizaCOPY.setFecemisionrecibo(fase.getFechaEmisionRecibo().getTime());
		polizaCOPY.setPorcmodulacion(polizaWS.getPorcentajeModulacion());
		polizaCOPY.setCodlinea(polizaWS.getLinea());
		polizaCOPY.setTiporef(new Character(tipoReferencia.charAt(0)));
		polizaCOPY.setDcpoliza(new BigDecimal(polizaWS.getDigitoControl()));
		polizaCOPY.setRefpoliza(polizaWS.getReferencia());
		polizaCOPY.setCodentidadaseg(entidadWS.getCodigo());
		polizaCOPY.setCodmodulo(coberturaWS.getModulo());
		
		if (tipoReferencia.equals(Constants.MODULO_POLIZA_PRINCIPAL+"")){
			//poliza principal
			polizaCOPY.setFechavigor(polizaWS.getFechaEntradaVigorPrincipal().getTime());
			polizaCOPY.setFechafirma(polizaWS.getFechaFirmaSeguroPrincipal().getTime());
			polizaCOPY.setIndividualrefundida(polizaWS.getAnuladaRefundidaPrincipal().toString().charAt(0));
			polizaCOPY.setCodinternoentidad(entidadWS.getMediadorPrincipal().getCodigoInterno());
			polizaCOPY.setTipomediador(new BigDecimal(entidadWS.getMediadorPrincipal().getTipo()));
			polizaCOPY.setImporteretribucion(entidadWS.getMediadorPrincipal().getImporteRetribucion());
			polizaCOPY.setReciberetribucion(entidadWS.getMediadorPrincipal().getRetribucionAsegurado().toString().charAt(0));
		}
		else{
			//poliza complementaria
			polizaCOPY.setFechavigor(polizaWS.getFechaEntradaVigorPrincipal().getTime());
			polizaCOPY.setFechafirma(polizaWS.getFechaFirmaSeguroComplementario().getTime());
			polizaCOPY.setIndividualrefundida(polizaWS.getAnuladaRefundidaComplementario().toString().charAt(0));
			polizaCOPY.setCodinternoentidad(entidadWS.getMediadorComplementario().getCodigoInterno());
			polizaCOPY.setTipomediador(new BigDecimal(entidadWS.getMediadorComplementario().getTipo()));
			polizaCOPY.setImporteretribucion(entidadWS.getMediadorComplementario().getImporteRetribucion());
			polizaCOPY.setReciberetribucion(entidadWS.getMediadorComplementario().getRetribucionAsegurado().toString().charAt(0));
		}
		
		// Para polizas < 2015
		if (new BigDecimal(polizaCOPY.getCodplan()).compareTo(Constants.PLAN_2015)== -1){
			
			if (distribucionCosteWS != null){
				polizaCOPY.setBonificacion(distribucionCosteWS.getBonificacion());
				polizaCOPY.setBonifsistproteccion(distribucionCosteWS.getBonificacionSistProteccion());
				polizaCOPY.setClea(distribucionCosteWS.getClea());
				polizaCOPY.setConsorcio(distribucionCosteWS.getConsorcio());
				polizaCOPY.setCosteneto(distribucionCosteWS.getCosteNeto());
				polizaCOPY.setCostetomador(distribucionCosteWS.getCosteTomador());
				polizaCOPY.setDctocolectivo(distribucionCosteWS.getDescuentoColectivo());
				polizaCOPY.setDctoventanilla(distribucionCosteWS.getDescuentoVentanilla());
				polizaCOPY.setPrimacomercial(distribucionCosteWS.getPrimaComercial());
				polizaCOPY.setPrimaneta(distribucionCosteWS.getPrimaNeta());
				polizaCOPY.setRecargo(distribucionCosteWS.getRecargo());
				polizaCOPY.setSubvenesa(distribucionCosteWS.getSubvencionEnesa());
				//polizaCOPY.setId(pCopy.getId()!=null?pCopy.getId():null);
			}
		// polizas >2015	
		}else{
			es.agroseguro.seguroAgrario.recibos.DistribucionCoste1 distribucionCosteWS1 = polizaWS.getDistribucionCostePrincipal1();
			if (distribucionCosteWS1 != null){
				
				polizaCOPY.setCosteneto(distribucionCosteWS1.getCosteTomador());
				polizaCOPY.setCostetomador(distribucionCosteWS1.getTotalCosteTomador());
				polizaCOPY.setPrimacomercial(distribucionCosteWS1.getPrimaComercial());
				polizaCOPY.setPrimaneta(distribucionCosteWS1.getPrimaComercialNeta());
				polizaCOPY.setRecargo(distribucionCosteWS1.getRecargoAval());
				polizaCOPY.setSubvenesa(distribucionCosteWS1.getSubvencionEnesa());
				polizaCOPY.setConsorcio(distribucionCosteWS1.getRecargoConsorcio());				
				
			}
		}
		
		//Colectivos
		
		com.rsi.agp.dao.tables.copy.Colectivo colectivoCOPY = new com.rsi.agp.dao.tables.copy.Colectivo();
		Set<com.rsi.agp.dao.tables.copy.Colectivo> colectivosCOPY = new HashSet<com.rsi.agp.dao.tables.copy.Colectivo>();
		
		// DAA 24/01/2013
		if(colectivoWS != null){
			colectivoCOPY.setCifniftomador(colectivoWS.getCif());
			colectivoCOPY.setDccolectivo(new Integer(colectivoWS.getDigitoControl()).shortValue());
			colectivoCOPY.setRefcolectivo(colectivoWS.getReferencia());
		}else{
			colectivoCOPY.setCifniftomador(individualWS.getNif());
			colectivoCOPY.setDccolectivo(new Integer(individualWS.getDigitoControl()).shortValue());
			colectivoCOPY.setRefcolectivo(individualWS.getReferencia());
		}
		
		colectivoCOPY.setRazonsocialtomador(razonSocialWS.getRazonSocial());
		colectivoCOPY.setCptomador(direccionWS.getCp());
		colectivoCOPY.setLocalidadtomador(direccionWS.getLocalidad());
		colectivoCOPY.setProvinciatomador(new BigDecimal(direccionWS.getProvincia()));
		colectivoCOPY.setNumeroviatomador(direccionWS.getNumero());
		colectivoCOPY.setViatomador(direccionWS.getVia());
		colectivoCOPY.setEmail(datosContactoWS.getEmail());
		colectivoCOPY.setTelefonofijo(((Integer)datosContactoWS.getTelefonoFijo()).toString());
		colectivoCOPY.setTelefonomovil(((Integer)datosContactoWS.getTelefonoMovil()).toString()); 
		colectivoCOPY.setPoliza(polizaCOPY);
		
		colectivosCOPY.add(colectivoCOPY);
		
		polizaCOPY.setColectivos(colectivosCOPY);
		
		// Asegurados
		
		com.rsi.agp.dao.tables.copy.Asegurado aseguradoCOPY = new com.rsi.agp.dao.tables.copy.Asegurado();
		Set<com.rsi.agp.dao.tables.copy.Asegurado> aseguradosCOPY = new HashSet<com.rsi.agp.dao.tables.copy.Asegurado>();

		
		aseguradoCOPY.setNifcif(aseguradoWS.getNif());
		if (aseguradoWS.getNombreApellidos() != null){
			aseguradoCOPY.setNombreaseg(StringUtils.nullToString(aseguradoWS.getNombreApellidos().getNombre()).trim());
			aseguradoCOPY.setApell1aseg(StringUtils.nullToString(aseguradoWS.getNombreApellidos().getApellido1()).trim());
			aseguradoCOPY.setApell2aseg(StringUtils.nullToString(aseguradoWS.getNombreApellidos().getApellido2()).trim());
		}
		else{
			aseguradoCOPY.setRazonsocialaseg(aseguradoWS.getRazonSocial().getRazonSocial().trim());
		}
		aseguradoCOPY.setCpaseg(aseguradoWS.getDireccion().getCp());
		aseguradoCOPY.setLocalidadaseg(aseguradoWS.getDireccion().getLocalidad());
		aseguradoCOPY.setProvinciaaseg(new BigDecimal(aseguradoWS.getDireccion().getProvincia()));
		aseguradoCOPY.setNumeroviaaseg(aseguradoWS.getDireccion().getNumero());
		aseguradoCOPY.setViaaseg(aseguradoWS.getDireccion().getVia());
		
		if(aseguradoWS.getDatosContacto() != null){
		
			aseguradoCOPY.setTelefonofijo(new Integer(aseguradoWS.getDatosContacto().getTelefonoFijo()).toString());
			aseguradoCOPY.setTelefonomovil(new Integer(aseguradoWS.getDatosContacto().getTelefonoMovil()).toString());
			aseguradoCOPY.setEmail(aseguradoWS.getDatosContacto().getEmail());
		
		}
		
		aseguradoCOPY.setPoliza(polizaCOPY);
		
		aseguradosCOPY.add(aseguradoCOPY);
		
		polizaCOPY.setAsegurados(aseguradosCOPY);
		
		if(tipoReferencia.equals(Constants.MODULO_POLIZA_PRINCIPAL+"") && entidadWS.getMediadorPrincipal().getNombreApellidos() != null){
			polizaCOPY.setNombremediador(entidadWS.getMediadorPrincipal().getNombreApellidos().getNombre().trim());
			polizaCOPY.setApellido1mediador(entidadWS.getMediadorPrincipal().getNombreApellidos().getApellido1().trim());
			polizaCOPY.setApellido2mediador(entidadWS.getMediadorPrincipal().getNombreApellidos().getApellido2().trim());
		
		}
		else if(tipoReferencia.equals(Constants.MODULO_POLIZA_COMPLEMENTARIO+"") && entidadWS.getMediadorComplementario().getNombreApellidos() != null){
			polizaCOPY.setNombremediador(entidadWS.getMediadorComplementario().getNombreApellidos().getNombre().trim());
			polizaCOPY.setApellido1mediador(entidadWS.getMediadorComplementario().getNombreApellidos().getApellido1().trim());
			polizaCOPY.setApellido2mediador(entidadWS.getMediadorComplementario().getNombreApellidos().getApellido2().trim());
		}
		
		// Pagos
		
		Set<PagoPoliza> pagosPolizaCOPY = new HashSet<PagoPoliza>(); 
		
		for(Pago pagoWS : pagosBancoWS){
			
			com.rsi.agp.dao.tables.copy.PagoPoliza pagoPolizaCOPY = new PagoPoliza();
			
			pagoPolizaCOPY.setBancopago(pagoWS.getBanco());
			pagoPolizaCOPY.setFectransferencia(pagoWS.getFecha().getTime());
			pagoPolizaCOPY.setFormapago(pagoWS.getForma().toString().charAt(0));
			pagoPolizaCOPY.setImportepago(pagoWS.getImporte());

			pagoPolizaCOPY.setPoliza(polizaCOPY);
			
			pagosPolizaCOPY.add(pagoPolizaCOPY);
		
		}
		
		polizaCOPY.setPagoPolizas(pagosPolizaCOPY);
		
		
		// Datos Coberturas
		
		if (datosVariablesCoberturaWS != null){ 
			Set<com.rsi.agp.dao.tables.copy.CoberturaPoliza> datosVariablesCoberturaPoliza = getDatosVariablesCoberturaCopy(
					datosVariablesCoberturaWS, polizaCOPY);
			
			polizaCOPY.setCoberturaPolizas(datosVariablesCoberturaPoliza);
		}
		
		// Subvenciones Declaradas
		
		if(subvencionesDeclaradasWS != null){

			Set<com.rsi.agp.dao.tables.copy.SubvencionDeclarada> subvencionesDeclaradasCOPY = getSubvencionesDeclaradas(
					subvencionesDeclaradasWS.getSubvencionDeclaradaArray(), polizaCOPY);
			
			polizaCOPY.setSubvencionDeclaradas(subvencionesDeclaradasCOPY);
		}
		
		//Subvenciones ENESA
		
		if(subvencionesEnesaWS != null){
		
			Set<com.rsi.agp.dao.tables.copy.SubvencionEnesa> subvencionesEnesaCOPY;
			if (tipoReferencia.equals(Constants.MODULO_POLIZA_PRINCIPAL+"")){
				subvencionesEnesaCOPY = getSubvencionesEnesa(subvencionesEnesaWS.getSubvencionEnesaPrincipalArray(), polizaCOPY);
			}
			else{
				subvencionesEnesaCOPY = getSubvencionesEnesa(subvencionesEnesaWS.getSubvencionEnesaComplementarioArray(), polizaCOPY);
			}
			
			polizaCOPY.setSubvencionEnesas(subvencionesEnesaCOPY);

		}
		
		// Subvenciones Organismos
		
		if(subvencionesOrganismosWS != null){

			Set<com.rsi.agp.dao.tables.copy.SubvencionOrganismo> subvencionesOrganismosCOPY;
			if (tipoReferencia.equals(Constants.MODULO_POLIZA_PRINCIPAL+"")){
				subvencionesOrganismosCOPY = getSubvencionesOrganismo(
						subvencionesOrganismosWS.getSubvencionOrganismoPrincipalArray(), polizaCOPY);
			}
			else{
				subvencionesOrganismosCOPY = getSubvencionesOrganismo(
						subvencionesOrganismosWS.getSubvencionOrganismoComplementarioArray(), polizaCOPY);
			}
			
			polizaCOPY.setSubvencionOrganismos(subvencionesOrganismosCOPY);	
		}
		
		// Parcelas
		
		Set<com.rsi.agp.dao.tables.copy.Parcela> parcelasCOPY = new HashSet<com.rsi.agp.dao.tables.copy.Parcela>();
		
		for(Parcela parcelaWS : parcelasWS){
			
			AmbitoAgro ambitoAgroWS = parcelaWS.getUbicacion();
			SIGPAC sigpacWS = parcelaWS.getSIGPAC();
			IdentificacionCatastral identificacionCatastralWS = parcelaWS.getIdentificacionCatastral();
			Cosecha cosechaWS = parcelaWS.getCosecha();
			CapitalAsegurado[] capitalesAseguradosWS = cosechaWS.getCapitalesAsegurados().getCapitalAseguradoArray();
			
			com.rsi.agp.dao.tables.copy.Parcela parcelaCOPY = new com.rsi.agp.dao.tables.copy.Parcela();
			
			parcelaCOPY.setPoliza(polizaCOPY);
			parcelaCOPY.setNomparcela(parcelaWS.getNombre());
			parcelaCOPY.setNumero(parcelaWS.getNumero());
			parcelaCOPY.setHoja(parcelaWS.getHoja());
			parcelaCOPY.setCodprovincia(new BigDecimal(ambitoAgroWS.getProvincia()));
			parcelaCOPY.setCodcomarca(new BigDecimal(ambitoAgroWS.getComarca()));
			parcelaCOPY.setCodtermino(new BigDecimal(ambitoAgroWS.getTermino()));
			//DAA 24/01/2013 si no tiene sigpac no la cargo en la copy
			if (sigpacWS != null){
				parcelaCOPY.setParcelasigpac(new BigDecimal(sigpacWS.getParcela()));
				parcelaCOPY.setPoligonosigpac(new BigDecimal(sigpacWS.getPoligono()));
				parcelaCOPY.setCodprovsigpac(new BigDecimal(sigpacWS.getProvincia()));
				parcelaCOPY.setRecintosigpac(new BigDecimal(sigpacWS.getRecinto()));
				parcelaCOPY.setCodtermsigpac(new BigDecimal(sigpacWS.getTermino()));
				parcelaCOPY.setZonasigpac(new BigDecimal(sigpacWS.getZona()));
				parcelaCOPY.setAgrsigpac(new BigDecimal(sigpacWS.getAgregado()));
			}
			parcelaCOPY.setCodcultivo(new BigDecimal(cosechaWS.getCultivo()));
			parcelaCOPY.setCodvariedad(new BigDecimal(cosechaWS.getVariedad()));
			
			if(ambitoAgroWS.getSubtermino() != null && ambitoAgroWS.getSubtermino().length() > 0)
				parcelaCOPY.setSubtermino(ambitoAgroWS.getSubtermino().charAt(0));
			
			if(identificacionCatastralWS != null){
				parcelaCOPY.setPoligono(identificacionCatastralWS.getPoligono());
				parcelaCOPY.setParcela(identificacionCatastralWS.getParcela());
			}
			
			
			// Capitales Asegurados

			Set<com.rsi.agp.dao.tables.copy.CapitalAsegurado> capitalesAseguradosCOPY = new HashSet<com.rsi.agp.dao.tables.copy.CapitalAsegurado>();

			for(CapitalAsegurado capitalAseguradoWS : capitalesAseguradosWS){
				
				com.rsi.agp.dao.tables.copy.CapitalAsegurado capitalAseguradoCOPY = new com.rsi.agp.dao.tables.copy.CapitalAsegurado();
				
				capitalAseguradoCOPY.setPrecio(capitalAseguradoWS.getPrecio());
				capitalAseguradoCOPY.setProduccion(new BigDecimal(capitalAseguradoWS.getProduccionPrincipal()));
				if (!StringUtils.nullToString(capitalAseguradoWS.getProduccionComplementario()).equals("")){
					capitalAseguradoCOPY.setIncrementoproduccion(new BigDecimal(capitalAseguradoWS.getProduccionComplementario()));
				}
				capitalAseguradoCOPY.setSuperficie(capitalAseguradoWS.getSuperficie());
				capitalAseguradoCOPY.setCodtipocapital(new BigDecimal(capitalAseguradoWS.getTipo()));
				
				Set<com.rsi.agp.dao.tables.copy.DatoVariableParcela> datosVariablesParcela = new HashSet<com.rsi.agp.dao.tables.copy.DatoVariableParcela>();
				
				DatosVariables datosVariablesCapitalAsegurado = capitalAseguradoWS.getDatosVariables();
				//convertir datosVariablesCapitalAsegurado
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getCarExpl(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getCiCul(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getCodCert(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getCodDO(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getCodIGP(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getCodRedRdto(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getDens(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getDest(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getEdad(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getEdadCubi(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getEdadEstr(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getFecRecol(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getFecSiemTrasp(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getIndGastSalv(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getMatCubi(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getMatEstr(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getMedPrev(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getMet(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getMet2(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getNadp(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getNumUnid(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getPraCult(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getRot(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getSisCond(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getSisCult(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getSisProd(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getSisProt(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getTipInst(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getTipMcoPlant(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getTipPlant(), capitalAseguradoCOPY));
				datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCapitalAsegurado.getTipSubDecPar(), capitalAseguradoCOPY));
				
				//Mejora Angel 12/03/2012 convertir datosVariablesCoberturaWS
				if (datosVariablesCoberturaWS !=null){
					/*
					 * ASF: El dato variable "Característica de la explotación" no aplica a nivel de parcela.
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getCarExpl(), capitalAseguradoCOPY));
					*/
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getCiCul(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getCodCert(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getCodDO(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getCodIGP(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getCodRedRdto(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getDens(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getDest(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getEdad(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getEdadCubi(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getEdadEstr(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getFecRecol(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getFecSiemTrasp(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getIndGastSalv(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getMatCubi(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getMatEstr(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getMedPrev(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getMet(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getMet2(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getNadp(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getNumUnid(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getPraCult(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getRot(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getSisCond(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getSisCult(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getSisProd(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getSisProt(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getTipInst(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getTipMcoPlant(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getTipPlant(), capitalAseguradoCOPY));
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datosVariablesCoberturaWS.getTipSubDecPar(), capitalAseguradoCOPY));
				}
				//Mejora Angel 12/03/2012
				
				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getCalcIndemArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));
				
				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getCapAsegArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getDIGarantArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));
				
				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getDnCbtosArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getDurMaxGarantDiasArray())//??
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getDurMaxGarantMesesArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getEFFGarantArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getEFIGarantArray())//??
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getFecFGarantArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getFecIGarantArray())//??
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getFranqArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getGarantArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getMIGarantArray())//??
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getMinIndemArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getPerGarantArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getRiesgCbtoElegArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getTipFranqArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				for(Object datoVariableWS : datosVariablesCapitalAsegurado.getTipRdtoArray())
					datosVariablesParcela.add(convertDatoVariableWStoCopy(datoVariableWS, capitalAseguradoCOPY));

				
				capitalAseguradoCOPY.setDatoVariableParcelas(datosVariablesParcela);
				capitalAseguradoCOPY.setParcela(parcelaCOPY);
				capitalesAseguradosCOPY.add(capitalAseguradoCOPY);
			}
			
			parcelaCOPY.setCapitalAsegurados(capitalesAseguradosCOPY);
			parcelasCOPY.add(parcelaCOPY);
			
		}
		
		polizaCOPY.setParcelas(parcelasCOPY);
	
		return polizaCOPY;
	
	}

	private Set<com.rsi.agp.dao.tables.copy.SubvencionOrganismo> getSubvencionesOrganismo(
			SubvencionOrganismo[] subvencionesOrganismosWS,
			com.rsi.agp.dao.tables.copy.Poliza polizaCOPY) {
		Set<com.rsi.agp.dao.tables.copy.SubvencionOrganismo> subvencionesOrganismosCOPY = new HashSet<com.rsi.agp.dao.tables.copy.SubvencionOrganismo>();
		
		for(SubvencionOrganismo subvencionOrganismoWS : subvencionesOrganismosWS){
			
			com.rsi.agp.dao.tables.copy.SubvencionOrganismo subvencionOrganismoCOPY = new com.rsi.agp.dao.tables.copy.SubvencionOrganismo();
			subvencionOrganismoCOPY.setCodorganismo(subvencionOrganismoWS.getCodigo().charAt(0));
			subvencionOrganismoCOPY.setImporte(subvencionOrganismoWS.getImporte());
			subvencionOrganismoCOPY.setPoliza(polizaCOPY);
			subvencionesOrganismosCOPY.add(subvencionOrganismoCOPY);
			
		}
		return subvencionesOrganismosCOPY;
	}

	private Set<com.rsi.agp.dao.tables.copy.SubvencionEnesa> getSubvencionesEnesa(
			SubvencionEnesa[] subvencionesEnesaWS,
			com.rsi.agp.dao.tables.copy.Poliza polizaCOPY) {
		Set<com.rsi.agp.dao.tables.copy.SubvencionEnesa> subvencionesEnesaCOPY = new HashSet<com.rsi.agp.dao.tables.copy.SubvencionEnesa>();
		
		for(SubvencionEnesa subvencionEnesaWS : subvencionesEnesaWS){
			
			com.rsi.agp.dao.tables.copy.SubvencionEnesa subvencionEnesaCOPY = new com.rsi.agp.dao.tables.copy.SubvencionEnesa();
			subvencionEnesaCOPY.setImportesubv(subvencionEnesaWS.getImporte());
			subvencionEnesaCOPY.setCodsubvencion(new BigDecimal(subvencionEnesaWS.getCodigoSubvencion()));
			subvencionEnesaCOPY.setPoliza(polizaCOPY);
			subvencionesEnesaCOPY.add(subvencionEnesaCOPY);
			
		}
		return subvencionesEnesaCOPY;
	}

	private Set<com.rsi.agp.dao.tables.copy.SubvencionDeclarada> getSubvencionesDeclaradas(
			SubvencionDeclarada[] subvencionesDeclaradasWS, com.rsi.agp.dao.tables.copy.Poliza polizaCOPY) {
		
		Set<com.rsi.agp.dao.tables.copy.SubvencionDeclarada> subvencionesDeclaradasCOPY = new HashSet<com.rsi.agp.dao.tables.copy.SubvencionDeclarada>();
		
		for(SubvencionDeclarada subvencionDeclaradaWS : subvencionesDeclaradasWS){
			
			com.rsi.agp.dao.tables.copy.SubvencionDeclarada subvencionDeclaradaCOPY = new com.rsi.agp.dao.tables.copy.SubvencionDeclarada();
			subvencionDeclaradaCOPY.setCodsubvencion(new Integer(subvencionDeclaradaWS.getTipo()).shortValue());
			subvencionDeclaradaCOPY.setPoliza(polizaCOPY);
			subvencionesDeclaradasCOPY.add(subvencionDeclaradaCOPY);
			
		}
		return subvencionesDeclaradasCOPY;
	}

	private Set<com.rsi.agp.dao.tables.copy.CoberturaPoliza> getDatosVariablesCoberturaCopy(
			DatosVariables datosVariablesCoberturaWS,
			com.rsi.agp.dao.tables.copy.Poliza polizaCOPY) {
		Set<com.rsi.agp.dao.tables.copy.CoberturaPoliza> datosVariablesCoberturaPoliza = new HashSet<com.rsi.agp.dao.tables.copy.CoberturaPoliza>();
		
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getRot(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getCarExpl(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getCiCul(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getCodDO(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getCodRedRdto(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getDens(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getDest(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getEdad(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getFecRecol(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getFecSiemTrasp(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getIndGastSalv(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getMedPrev(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getNadp(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getNumUnid(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getPraCult(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getSisCond(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getSisCult(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getSisProd(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getSisProt(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getTipMcoPlant(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getTipPlant(), polizaCOPY));
		datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datosVariablesCoberturaWS.getTipSubDecPar(), polizaCOPY));
		
		for(Object datoVariableWS : datosVariablesCoberturaWS.getCalcIndemArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));
		
		for(Object datoVariableWS : datosVariablesCoberturaWS.getCapAsegArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getDIGarantArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));
		
		for(Object datoVariableWS : datosVariablesCoberturaWS.getDnCbtosArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getDurMaxGarantDiasArray())//??
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getDurMaxGarantMesesArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getEFFGarantArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getEFIGarantArray())//??
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getFecFGarantArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getFecIGarantArray())//??
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getFranqArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getGarantArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getMIGarantArray())//??
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getMinIndemArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getPerGarantArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getRiesgCbtoElegArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getTipFranqArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));

		for(Object datoVariableWS : datosVariablesCoberturaWS.getTipRdtoArray())
			datosVariablesCoberturaPoliza.add(convertDatoVariableCoberturaWStoCopy(datoVariableWS, polizaCOPY));
		return datosVariablesCoberturaPoliza;
	}
	
	
	
	private com.rsi.agp.dao.tables.copy.DatoVariableParcela convertDatoVariableWStoCopy(Object datoVariableParcelaWS, com.rsi.agp.dao.tables.copy.CapitalAsegurado capitalAseguradoCopy){
		
		if(datoVariableParcelaWS == null)
			return null;
		
		DatoVariableParcela datoVariableParcelaCopy = new DatoVariableParcela();
		datoVariableParcelaCopy.setCapitalAsegurado(capitalAseguradoCopy);
		datoVariableParcelaCopy.setCodconcepto(getCodigoConcepto(datoVariableParcelaWS));
		datoVariableParcelaCopy.setValor(HTMLUtils.getProperty(datoVariableParcelaWS, "valor").toString().replaceAll("-", "/"));
		datoVariableParcelaCopy.setCodriesgocubierto((Integer)HTMLUtils.getProperty(datoVariableParcelaWS, "codRCub"));
		datoVariableParcelaCopy.setCodconceptoppalmod((Integer)HTMLUtils.getProperty(datoVariableParcelaWS, "CPMod"));
		
		return datoVariableParcelaCopy;
		
	}
	
	
	private com.rsi.agp.dao.tables.copy.CoberturaPoliza convertDatoVariableCoberturaWStoCopy(Object datoVariableCoberturaWS, com.rsi.agp.dao.tables.copy.Poliza polizaCopy){
		
		if(datoVariableCoberturaWS == null)
			return null;
		
		CoberturaPoliza coberturaPoliza = new CoberturaPoliza();
		coberturaPoliza.setPoliza(polizaCopy);
		
		coberturaPoliza.setCodconcepto(new BigDecimal(getCodigoConcepto(datoVariableCoberturaWS)));
		coberturaPoliza.setCodvalor(""+HTMLUtils.getProperty(datoVariableCoberturaWS, "valor"));
		
		Integer codRCub = (Integer)HTMLUtils.getProperty(datoVariableCoberturaWS, "codRCub");
		Integer cPMod = (Integer)HTMLUtils.getProperty(datoVariableCoberturaWS, "CPMod");
		
		if(codRCub != null)
			coberturaPoliza.setCodriesgocubierto(new BigDecimal(codRCub));
		
		if(cPMod != null )
			coberturaPoliza.setCodconceptoppalmod(new BigDecimal(cPMod));
		
		return coberturaPoliza;
		
	}
	
	private Integer getCodigoConcepto(Object datoVariableParcela){
		
		if(datoVariableParcela instanceof Rotacion)
			return 144;
		if(datoVariableParcela instanceof CaracteristicasExplotacion)
			return 106;
		if(datoVariableParcela instanceof CicloCultivo)
			return 618;
		if(datoVariableParcela instanceof DenominacionOrigen)
			return 107;
		if(datoVariableParcela instanceof CodigoReduccionRdtos)
			return 620;
		if(datoVariableParcela instanceof Densidad)
			return 109;
		if(datoVariableParcela instanceof Destino)
			return 110;
		if(datoVariableParcela instanceof Edad)
			return 111;
		if(datoVariableParcela instanceof FechaRecoleccion)
			return 112;
		if(datoVariableParcela instanceof CodigoReduccionRdtos)
			return 620;
		if(datoVariableParcela instanceof FechaSiembraTrasplante)
			return 113;
		if(datoVariableParcela instanceof IndicadorGastosSalvamento)
			return 114;
		if(datoVariableParcela instanceof MedidaPreventiva)
			return 124;
		if(datoVariableParcela instanceof NumeroAniosDesdePoda)
			return 617;
		if(datoVariableParcela instanceof NumeroUnidades)
			return 117;
		if(datoVariableParcela instanceof PracticaCultural)
			return 133;
		if(datoVariableParcela instanceof SistemaConduccion)
			return 131;
		if(datoVariableParcela instanceof SistemaCultivo)
			return 123;
		if(datoVariableParcela instanceof SistemaProduccion)
			return 616;
		if(datoVariableParcela instanceof SistemaProteccion)
			return 621;
		if(datoVariableParcela instanceof TipoMarcoPlantacion)
			return 116;
		if(datoVariableParcela instanceof TipoPlantacion)
			return 173;
		if(datoVariableParcela instanceof TipoSubvencionDeclaradaParcela)
			return 132;
		if(datoVariableParcela instanceof CalculoIndemnizacion)
			return 174;
		if(datoVariableParcela instanceof PorcentajeCapitalAsegurado)
			return 362;
		if(datoVariableParcela instanceof DiasLimiteGarantias)
			return -1;
		if(datoVariableParcela instanceof DaniosCubiertos)
			return 169;
		if(datoVariableParcela instanceof MesesLimiteGarantias)
			return -1;
		if(datoVariableParcela instanceof EstadoFenologicoLimiteGarantias)
			return 234;
		if(datoVariableParcela instanceof FechaLimiteGarantias)
			return 134;
		if(datoVariableParcela instanceof PorcentajeFranquicia)
			return 120;
		if(datoVariableParcela instanceof Garantizado)
			return 175;
		if(datoVariableParcela instanceof PorcentajeMinimoIndemnizable)
			return 121;
		if(datoVariableParcela instanceof PeriodoGarantias)
			return 157;
		if(datoVariableParcela instanceof RiesgoCubiertoElegido)
			return 363;
		if(datoVariableParcela instanceof TipoFranquicia)
			return 170;
		if(datoVariableParcela instanceof TipoRendimiento)
			return 502;
		if(datoVariableParcela instanceof CodigoCertificado)
			return 879;
		if(datoVariableParcela instanceof EdadCubierta)
			return 874;
		if(datoVariableParcela instanceof EdadEstructura)
			return 876;
		if(datoVariableParcela instanceof MaterialCubierta)
			return 873;
		if(datoVariableParcela instanceof MaterialEstructura)
			return 875;
		if(datoVariableParcela instanceof MetrosCuadrados)
			return 767;
		if(datoVariableParcela instanceof MetrosLineales)
			return 766;
		if(datoVariableParcela instanceof TipoInstalacion)
			return 778;
		if(datoVariableParcela instanceof CodigoIGP)
			return 765;
		
		return -1;
		
	}
	
	
	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
	
	public List<com.rsi.agp.dao.tables.copy.Poliza> getListaPolizasCopy(com.rsi.agp.dao.tables.copy.Poliza polizaBean) throws DAOException{
		try{
			return polizaCopyDao.getListaPolizas(polizaBean);
		
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/**
	 * ASF - 17/9/2012 - Ampliación de la Mejora 79: preguntar si desea recalcular producción al cargar la copy
	 * Método para obtener el número de copys (polizas contratadas) disponibles para una póliza.
	 * @param polizaBean
	 * @return
	 */
	public int getNumCopys(BigDecimal codplan, final BigDecimal codlinea, final String nifasegurado,final boolean polAnterior) throws DAOException{
		return polizaDao.getNumPolizasContratadas(codplan, codlinea, nifasegurado, polAnterior);
	}
	
	/**
	 * Método para copiar parcelas de un copy o una póliza a otra póliza
	 * @param poliza Póliza destino para copiar las parcelas
	 * @param realPath Ruta física de la aplicación en el servidor
	 * @return true cuando la póliza haya sido cargada bien de copy o bien de póliza anterior y false en caso
	 * de que haya más de una póliza para cargar. Antes de llamar a este método se habrá comprobado que hay pólizas para cargar
	 * @throws Exception
	 */
	public int mueveParcelas(Poliza poliza,String realPath, boolean descargarCopy,boolean polAnterior) throws Exception {
		int res=0;//Para determinar si la carga de las parcelas se hizo desde la situaci�n actualizada o desde la bbdd
		int numPolizas;
		try {
			//TMR 10/09/2012 Mejora 198 al buscar la copy año anterior, NO se filtra por clase
			numPolizas = this.getNumCopys(poliza.getLinea().getCodplan(), poliza.getLinea().getCodlinea(), 
					poliza.getAsegurado().getNifcif(),polAnterior);
			
			if (numPolizas == 1){
				Poliza polizaAnterior = this.polizaDao.getPolizaContratada(poliza.getLinea().getCodplan(), 
						poliza.getLinea().getCodlinea(),poliza.getAsegurado().getNifcif(),polAnterior);
				
				res= this.mueveParcelas(poliza, realPath, polizaAnterior, descargarCopy);
			}
		}catch (DAOException dao) {
			logger.error("Se ha producido al obtener la poliza copy: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al descargar la poliza copy",dao);
		}

		//AMG 22/08/2012. Si para un mismo asegurado y linea hay más de una poliza contratada para el plan anterior
		// permitir elegir la póliza a cargar.
		return res;
		
	}
	
	/**
	 * AMG 22/08/2012. Si para un mismo asegurado y linea hay más de una poliza contratada para el plan anterior
	 * permitir elegir la póliza a cargar.
	 * @param polizaDestino
	 * @param realPath
	 * @param idpolizaAnterior -> idPoliza de la cual se copiarán las parcelas de su copy, si existe.
	 * @throws Exception
	 */
	public int mueveParcelas(Poliza polizaDestino,String realPath, Poliza polizaOrigen, boolean descargarCopy) {
		com.rsi.agp.dao.tables.copy.Poliza polizaCopy = null;
		int res=0;//Para determinar si la carga de las parcelas se hizo desde la situaci�n actualizada o desde la bbdd
		try {
			//ASF - 29/10/2012 Mejora 216
			if (descargarCopy){
				//comprobamos si existe la polizaCopy
				Long idCopy = this.descargarPolizaCopyWS(polizaOrigen.getTipoReferencia() + "", 
						polizaOrigen.getLinea().getCodplan(),polizaOrigen.getReferencia(),realPath);
				
				if (idCopy != null) {
					logger.debug("Se descargó la copy de Agroseguro para los datos introducidos. Id de copy de BD: " + idCopy);
					polizaCopy =  polizaCopyDao.getPolizaCopyById(idCopy);
				}
			}
					
			if (polizaCopy != null){ //tenemos copy
				polizaDao.arrastreParcelas(polizaDestino.getLinea().getLineaseguroid(),polizaDestino.getIdpoliza(), polizaCopy.getId(),
						null,polizaDestino.getClase());
				
				// volvermos a recuperar la poliza con las parcelas 
				polizaDestino = polizaDao.getPolizaById(polizaDestino.getIdpoliza());
				

				for (com.rsi.agp.dao.tables.copy.Parcela parcela: polizaCopy.getParcelas()) {
						
					Integer hoja = parcela.getHoja();
					Integer numero = parcela.getNumero();
					
					for (com.rsi.agp.dao.tables.copy.CapitalAsegurado capital: parcela.getCapitalAsegurados()) {
						
						if (capital == null) {
							continue;
						}
						
						for (DatoVariableParcela dato: capital.getDatoVariableParcelas()) {
							
						
							if (dato == null) {
								continue;
							}
							
							
							if (
									dato.getCodconcepto() == ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO
									||
									dato.getCodconcepto() == ConstantsConceptos.CODCPTO_PCT_FRANQUICIA
									||
									dato.getCodconcepto() == ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE
								) {
								
								BigDecimal valor;
								

								if (dato.getCodconcepto() == ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO) {
								
									if ("N".equals(dato.getValor())) {
										valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
									} else {
										valor =  new BigDecimal(Constants.RIESGO_ELEGIDO_SI);
									}
								} else {
									valor = new BigDecimal(dato.getValor());
								}

								Integer conceptoppalmod = dato.getCodconceptoppalmod();
								Integer codconcepto = dato.getCodconcepto();
								Integer codriesgo = dato.getCodriesgocubierto();
								String codmodulo = "P";

								
								for (com.rsi.agp.dao.tables.poliza.Parcela origen: polizaDestino.getParcelas()) {
									if (hoja.equals(origen.getHoja()) && numero.equals(origen.getNumero())) {
										


										polizaDao.saveCobertura(origen.getIdparcela(), polizaDestino.getLinea().getLineaseguroid(), valor, conceptoppalmod, codconcepto, codriesgo, codmodulo);
										
										
										break;
									}
								}
								
							}
							
							
						}
						
					}
						
					
					
				}
				
				
				
				res=1;
			}else{
				polizaDao.arrastreParcelas(polizaDestino.getLinea().getLineaseguroid(),polizaDestino.getIdpoliza(), null,
						polizaOrigen.getIdpoliza(),polizaDestino.getClase());
				res=2;
			}
			return res;
		}catch (DAOException dao) {
			logger.error("Se ha producido al obtener la copy.", dao);
		} catch (BusinessException e) {
			logger.error("Se ha producido al descargar la copy.", e);
		} catch (Exception e) {
			logger.error("Se ha producido al copiar las parcelas de la copy.", e);
		}
		return res;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	/**
	 * Obtiene los datos variables que No estan en la copy
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public List<DatoVariableDefault> getDatosVariablesCopy(Poliza poliza)throws  BusinessException {
		logger.debug("init - getDatosVariablesCopy");

		List<DatoVariableDefault> lstDatosVarSinFiltrar= new ArrayList<DatoVariableDefault>();
		try {
			lstDatosVarSinFiltrar = (List<DatoVariableDefault>) polizaCopyDao.getDatosVariablesCopy(poliza);
			for (DatoVariableDefault dat: lstDatosVarSinFiltrar){
				String planLineaConcepto=dat.getPlanLineaConcepto();
				String[] plan_Linea_Concepto = planLineaConcepto.split("_");
				BigDecimal CodConcepto= new BigDecimal(plan_Linea_Concepto[2]);
				//con el codConcepto obtenemos la naturaleza del campo y el numtabla (para saber si es lupa)
				DiccionarioDatos dicDatos = (DiccionarioDatos)diccionarioDatosDao.get(DiccionarioDatos.class,CodConcepto);
				dat.setTipoCampo(dicDatos.getTipoNaturaleza().getDestiponaturaleza());
				dat.setNumTabla(dicDatos.getNumtabla().toString());
			}
		} 
		catch (DAOException ex){
			logger.error("Se ha producido un error durante el acceso a la base de datos", ex);
			throw new BusinessException ("[ERROR] al obtener los datos variables de la tabla DatoVariableDefault - en PolizaCopyManager, método getDatosVariablesCopy]",ex);
		}
		catch (Exception ex){
			logger.error("Se ha producido un error inesperado al cargar la lista de datos variables", ex);
			throw new BusinessException ("[ERROR] al obtener los datos variables de la tabla DatoVariableDefault - en PolizaCopyManager, método getDatosVariablesCopy]",ex);
		}
		
		logger.debug("Fin - getDatosVariablesCopy");
		return lstDatosVarSinFiltrar;
	}
	/**
	 * Guarda los datos variables en la nueva poliza, que No estan en la copy
	 * @param polizaBean
	 * @param lstDatosVarfinal
	 * @throws BusinessException 
	 */
	public void guardaDatosVariablesCopy(Poliza polizaBean,
			List<DatoVariableDefault> lstDatosVarfinal) throws BusinessException {
		
		com.rsi.agp.dao.tables.poliza.DatoVariableParcela dVarParcela= null;
		try{
			for (com.rsi.agp.dao.tables.poliza.Parcela p : polizaBean.getParcelas()){
				for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado capAseg :p.getCapitalAsegurados()){
					
					
					for (DatoVariableDefault dat: lstDatosVarfinal){
						dVarParcela =  new com.rsi.agp.dao.tables.poliza.DatoVariableParcela();
						dVarParcela.setCapitalAsegurado(capAseg);
						
						String planLineaConcepto=dat.getPlanLineaConcepto();
						String[] plan_Linea_Concepto = planLineaConcepto.split("_");
						BigDecimal CodConcepto= new BigDecimal(plan_Linea_Concepto[2]);
						
						dVarParcela.getDiccionarioDatos().setCodconcepto(CodConcepto);
						dVarParcela.setCapitalAsegurado(capAseg);
						dVarParcela.setValor(dat.getValor());
						this.polizaCopyDao.saveDatoVarParcela(dVarParcela);
						this.polizaCopyDao.evict(dVarParcela);
					}
				}
			}
		}catch (DAOException ex){
			logger.error("Se ha producido un error durante el acceso a la base de datos " + ex.getMessage());
			throw new BusinessException ("[ERROR] en PolizaCopyManager.java método guardaDatosVariablesCopy.", ex);
		}
		catch (Exception ex){
			logger.error("Se ha producido un error inesperado al guardar los datos variables de la PAC",ex);
			throw new BusinessException ("[ERROR] en PolizaCopyManager.java método guardaDatosVariablesCopy.", ex);
		}
			
			logger.debug("Fin - guardaDatosVariablesCopy");
	}
	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	//ASF - Mejora para crear una póliza a partir de los datos de una copy
	/**
	 * Método para crear una póliza a partir de una copy
	 * @param polizaOrigen Poliza con los datos del formulario de entrada
	 * @param idcopy Identificador de la copy origen
	 * @param realPath Ruta real de ejecución
	 * @return
	 */
	public Long crearPolizaFromCopy(Poliza polizaOrigen, Long idcopy, String realPath) {
		
		//Llamo al pl que crea la póliza a partir de la copy
		Long idpoliza = this.polizaCopyDao.crearPolizaFromCopy(polizaOrigen.getReferencia(), polizaOrigen.getTipoReferencia()+"", 
				polizaOrigen.getClase(), idcopy);
		
		//Calculo la comparativa elegida
		
		//Llamar a los servicios de validación y cálculo una vez. Lo más probable es que de problemas porque la 
		// distribución de costes puede haber cambiado.

		return idpoliza;
	}

	
	
}
