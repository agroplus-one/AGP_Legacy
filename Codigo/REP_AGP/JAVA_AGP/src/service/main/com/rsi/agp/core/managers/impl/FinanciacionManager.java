package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DistribucionCostesException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.dc2015.OpcionesFinanciacion;

import es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument;

public class FinanciacionManager implements IManager {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private WebServicesManager webServicesManager;
	private ParametrizacionManager parametrizacionManager;
	private IDistribucionCosteDAO distribucionCosteDAO;
	private ConsultaDetallePolizaManager consultaDetallePolizaManager;
	ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public String procesoFinanciacion(final String realPath, final String codModulo, final BigDecimal filaComp,
			final long idComparativa, final Long idPoliza, final Usuario usuario,
			final Map<String, Object> frmCalculoFinanciacion) {	
		String mensajeAcuseServicios = null;
		es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument financiacion=null;
		// Objeto que encapsula el resultado del cálculo para pólizas de Agrarios con el esquema antiguo 
		es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo = null;
		// Objeto que encapsula el resultado del cálculo para pólizas de Ganado y Agrario con el esquema unificado
		es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado = null;		
		try {			
			Poliza poliza= getPoliza( idPoliza); 			
			logger.debug("Llamada al SW de financiacion.");			
			//Llamamos al servicio de financiación
			Map<String, Object> respuestaServFinanciacion = this.webServicesManager.callWSFinanciacion(realPath, 
					frmCalculoFinanciacion, poliza, codModulo, filaComp , usuario.getCodusuario());			
			if (respuestaServFinanciacion != null && respuestaServFinanciacion.containsKey("financiacion")) {
				//Llamamos al servicio de cálculo
				logger.debug("Obtenemos la comparativa.");
				ComparativaPoliza cp = this.getComparativa(poliza.getComparativaPolizas(), codModulo, filaComp, idComparativa);
				logger.debug("Seteamos las opciones de financiacion.");
				OpcionesFinanciacion of = new OpcionesFinanciacion(frmCalculoFinanciacion);
				cp.setEsFinanciada(true);
				cp.setOpcionesFinanciacion(of);	
				// Obtenemos el % de comision usado en el calculo de esa comparativa para cada grupo de negocio
				Map<Character, ComsPctCalculado> comsPctCalculado = this.webServicesManager
						.getComsPctCalculadoComp(cp.getId().getIdComparativa());
				logger.debug("Generamos el XML de calculo.");
				Long idEnvio = this.webServicesManager.generateAndSaveXMLPoliza(poliza, cp, Constants.WS_CALCULO, true,
						comsPctCalculado);// OJO, asegurarse que el XML tiene construida la etiqueta de pago con el
											// fraccionamiento
				logger.debug("Llamada al SW de calculo.");
				Map<String, Object> mapRespuestaServCalculo = this.webServicesManager.callWSCalculo(idEnvio, poliza, cp, realPath);		
				Object resCalculo= mapRespuestaServCalculo.get("calculo");				
				if(mapRespuestaServCalculo != null && mapRespuestaServCalculo.containsKey("calculo")) {
					logger.debug("Respuesta correcta.");
					financiacion=(es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument)respuestaServFinanciacion.get("financiacion");
					if (resCalculo instanceof es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) {
						polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) resCalculo;	
						//es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DatosCalculo datosCalculo=polizaCalculo.getDatosCalculo();
					} else if (resCalculo instanceof es.agroseguro.distribucionCostesSeguro.Poliza) {
						plzCalculoUnificado = (es.agroseguro.distribucionCostesSeguro.Poliza) resCalculo;
						//es.agroseguro.distribucionCostesSeguro.DatosCalculo datosCalculo= plzCalculoUnificado.getDatosCalculo();
					}	
					logger.debug("Actualizamos la distribucion de costes.");
					this.actualizaDistribCostesConFinanciacion(poliza, codModulo, filaComp, idComparativa,
							financiacion, of, polizaCalculo, plzCalculoUnificado);
				} else {
					logger.debug("Respuesta erronea.");
					//Procesamos el acuse de recibo y el error del servicio de cálculo
					resCalculo = mapRespuestaServCalculo.get("acuse");
					mensajeAcuseServicios = resCalculo.toString();
				}				
			} else {
				//Procesamos el error del servicio de fiannciación
				mensajeAcuseServicios = (String) respuestaServFinanciacion.get("alerta");				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			mensajeAcuseServicios = bundle.getString("mensaje.swImpresion.llamadaWs.KO");			
		}
		return mensajeAcuseServicios;
	}
	
	
	public void actualizaDistribCostesConFinanciacion(Poliza poliza, String codModulo, BigDecimal filaComparativa,
			Long idComparativa, FinanciacionDocument financiacion, OpcionesFinanciacion of,
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo,
			es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado)
			throws DistribucionCostesException, Exception {

		try {

			Set<DistribucionCoste2015> setDistCoste = null;
			distribucionCosteDAO.deleteDistribucionCoste2015(poliza.getIdpoliza(), codModulo, idComparativa);

			Boolean esGanado = poliza.getLinea().isLineaGanado();
			setDistCoste = distribucionCosteDAO.saveDistribucionCoste2015Unificado(plzCalculoUnificado,
					poliza.getIdpoliza(), codModulo, filaComparativa, idComparativa, financiacion,
					of.getCondicionFracionamiento(), of.getOpcionFraccionamiento(), of.getValorOpcionFraccionamiento(),
					esGanado);
			poliza.getDistribucionCoste2015s().addAll(setDistCoste);
			poliza.setImporte(setDistCoste.iterator().next().getCostetomador());

			if (poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0) {
				Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
				if (it.hasNext()) {
					PagoPoliza pp = it.next();
					pp.setImporte(setDistCoste.iterator().next().getImportePagoFracc());
				}
			}
			/* } */

		} catch (Exception e) {
			throw new DistribucionCostesException(" Ocurrió un error al guardar la distribución de costes de la póliza",
					e);
		}
	}

	/*
	 *Selecciona la primera comparativa que encuentra con la clave de búsqueda por idComparativa 
	 */
	private ComparativaPoliza getComparativa(Set<ComparativaPoliza> comparativas, String codModulo, BigDecimal filaComp, long idComparativa){
		ComparativaPoliza comparativaPoliza=null;
		for (ComparativaPoliza  cp: comparativas) {
			if(cp.getId().getIdComparativa().compareTo(idComparativa) == 0) {
				comparativaPoliza=cp;
				break;
			}
		}
		return comparativaPoliza;
	}
	
	
	/*
	 * Genera la lista de comparativas de importes con las distribuciones de costes que hay en BBDD, para la jsp de importes
	 */
	public Set<VistaImportes> getComparativasImportes(long idPoliza, String realPath, final Usuario usuario){
		Set<VistaImportes> comparativasImportes = new LinkedHashSet<VistaImportes>();
		Poliza poliza= (Poliza) distribucionCosteDAO.getObject(Poliza.class, idPoliza);
		
		List<DistribucionCoste2015>listDcs = new ArrayList<DistribucionCoste2015>();		
		listDcs.addAll(poliza.getDistribucionCoste2015s());		
	
		comparativasImportes= consultaDetallePolizaManager.getDataImportes(listDcs, poliza, usuario, realPath);
		
		return comparativasImportes;
	}
	
	public Poliza getPoliza(Long idPoliza){
		Poliza poliza= (Poliza) distribucionCosteDAO.getObject(Poliza.class, idPoliza); 
		return poliza;
	}

	public WebServicesManager getWebServicesManager() {
		return webServicesManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public ParametrizacionManager getParametrizacionManager() {
		return parametrizacionManager;
	}

	public void setParametrizacionManager(
			ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
	
	public IDistribucionCosteDAO getDistribucionCosteDAO() {
		return distribucionCosteDAO;
	}


	public void setDistribucionCosteDAO(IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}
	
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	
	
	
	
}
