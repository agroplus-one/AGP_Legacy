package com.rsi.agp.core.ws;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza;
/**
 * Encapsula la respuesta de los servicios Web, 
 * compuesta de un AcuseRecibo y de una Poliza
 * en caso de que el servicio sea el de calculo
 *  
 * @author T-Systems
 *
 */
public class ResultadoWS {
	
	/**
	 * Acuse de recibo obtenido de la llamada al servicio Web
	 */
	private AcuseRecibo acuseRecibo = null;
	
	/**
	 * Resultado del Calculo de la Poliza 
	 */
	private Poliza polizaCalculo = null;
	
	private es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado = null;
	
	/**
	 * Indica si se puede llamar al servicio de Calculo o no
	 */
	private boolean calculable = false;

	/**
	 * @return the acuseRecibo
	 */
	public AcuseRecibo getAcuseRecibo() {
		return acuseRecibo;
	}

	/**
	 * @param acuseRecibo the acuseRecibo to set
	 */
	public void setAcuseRecibo(AcuseRecibo acuseRecibo) {
		this.acuseRecibo = acuseRecibo;
	}

	/**
	 * @return the polizaCalculo
	 */
	public Poliza getPolizaCalculo() {
		return polizaCalculo;
	}

	/**
	 * @param polizaCalculo the polizaCalculo to set
	 */
	public void setPolizaCalculo(Poliza polizaCalculo) {
		this.polizaCalculo = polizaCalculo;
	}

	/**
	 * @return the calculable
	 */
	public boolean isCalculable() {
		return calculable;
	}

	/**
	 * @param calculable the calculable to set
	 */
	public void setCalculable(boolean calculable) {
		this.calculable = calculable;
	}

	public es.agroseguro.distribucionCostesSeguro.Poliza getPlzCalculoUnificado() {
		return plzCalculoUnificado;
	}

	public void setPlzCalculoUnificado(
			es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado) {
		this.plzCalculoUnificado = plzCalculoUnificado;
	}
	
	
	

}
