package com.rsi.agp.core.managers.impl.anexoMod.calculo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.CalculoModificacionCuponActivoRequest;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.ContratacionSCCalculoModificaciones;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.ContratacionSCCalculoModificaciones_Service;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.ParametrosSalida;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public class CalculoModificacionHelper {
	
	private static final Log logger = LogFactory.getLog(CalculoModificacionHelper.class);
	
	public Map<String, Object> getCalculoModificacionCuponActivo (final String realPath, final String cupon, final Character tipoPoliza, final boolean calcularSituacionActual, final Base64Binary xml) throws CalculoModificacionException{
		
		Map<String, Object> resultado = new HashMap<String, Object>();
		
		// Crea el objeto para llamar al SW
		ContratacionSCCalculoModificaciones srvCalculoModificacion = this.getSrvCalculoModificacion(realPath);
		
		// Crea el objeto que encapsula los parametros para la llamada al metodo de calculo de modificacion con cupon activo
		CalculoModificacionCuponActivoRequest cuponActivoRequest = getCuponActivoRequest(cupon, tipoPoliza, calcularSituacionActual, xml);
		
		// Llama al metodo de calculo de modificacion con cupon activo
		ParametrosSalida respuesta = null;
		try {
			respuesta = srvCalculoModificacion.calculoModificacionCuponActivo(cuponActivoRequest);
			resultado.put("respuesta", respuesta);
		} catch (AgrException e) {
			logger.error("Error al llamar al SW de calculo de modificacion de cupon activo", e);
			resultado.put("alerta", WSUtils.debugAgrException(e));
		}			
		
		// Se devuelve la respuesta del servicio en el mapa de resultados
		if (respuesta != null) {
			if (respuesta.getCalculoModificacion() != null) resultado.put("calculoModificacion", getStringFromBase64Binary(respuesta.getCalculoModificacion().getValue()));
			if (respuesta.getCalculoOriginal() != null) resultado.put("calculoOriginal", getStringFromBase64Binary(respuesta.getCalculoOriginal().getValue()));
			if (respuesta.getDiferenciasCoste() != null) resultado.put("diferenciasCoste", getStringFromBase64Binary(respuesta.getDiferenciasCoste()));
		}
		
		return resultado;
	}
	
	/*
	 *  Metodo alternativo al getCalculoModificacionCuponActivo() para devolver la respuesta del servicio en un mapa de Base64Binaries
	 */
	public Map<String, Base64Binary> getCalculoModificacionCuponActivoACM (final String realPath, final String cupon, final Character tipoPoliza, final boolean calcularSituacionActual, final Base64Binary xml) throws CalculoModificacionException{
		
		
		logger.debug("CalculoModificacionHelper - getCalculoModificacionCuponActivoACM() - init");
		
		Map<String, Base64Binary> resultado = null;
		
		// Crea el objeto para llamar al SW
		ContratacionSCCalculoModificaciones srvCalculoModificacion = this.getSrvCalculoModificacion(realPath);
		
		// Crea el objeto que encapsula los parametros para la llamada al metodo de calculo de modificacion con cupon activo
		CalculoModificacionCuponActivoRequest cuponActivoRequest = getCuponActivoRequest(cupon, tipoPoliza, calcularSituacionActual, xml);
		
		// Llama al metodo de calculo de modificacion con cupon activo
		ParametrosSalida respuesta = null;
		
		try {
			respuesta = srvCalculoModificacion.calculoModificacionCuponActivo(cuponActivoRequest);		
		} catch (AgrException e) {
			logger.error("Error al llamar al SW de calculo de modificacion de cupon activo", e);
		}			

		
		// Se devuelve la respuesta del servicio en el mapa de resultados
		if (respuesta != null) {
			resultado = new HashMap<String, Base64Binary>();
			if (respuesta.getCalculoModificacion() != null) resultado.put("calculoModificacion", respuesta.getCalculoModificacion().getValue());
			if (respuesta.getCalculoOriginal() != null) resultado.put("calculoOriginal", respuesta.getCalculoOriginal().getValue());
			if (respuesta.getDiferenciasCoste() != null) resultado.put("diferenciasCoste", respuesta.getDiferenciasCoste());
		}
		
		
		logger.debug("CalculoModificacionHelper - getCalculoModificacionCuponActivoACM() - end");

		
		return resultado;
	}


	/**
	 * Obtiene la cadena asociada al parametro en Base64Binary
	 * @param respuesta
	 * @return
	 */
	private String getStringFromBase64Binary(Base64Binary base) {
		
		try {
			return new String (base.getValue(), "UTF-8");
		} 
		catch (Exception e) {
			logger.error("Error al obtener la cadena asociada a la respuesta del servicio", e);
		}
		
		return null;
	}
	
	
	/**
	 * Genera el objeto que encapsula los parametros de entrada del metodo de calculo de modificacion con cupon activo del SW
	 * @param cupon Identificador del cupon asociado a la modificacion
	 * @param tipoPoliza Tipo de poliza asociada a la modificacion (P o C)
	 * @param calcularSituacionActual Indica si se realizara el calculo con respecto a la situacion actual de la poliza (true) o con respecto
	 * al último recibo emitido (o poliza actual si no hay recibos)
	 * @param xml Xml de la poliza modificada
	 * @return
	 * @throws CalculoModificacionException
	 */
	private CalculoModificacionCuponActivoRequest getCuponActivoRequest
			(final String cupon, final Character tipoPoliza, final boolean calcularSituacionActual, 
			 final Base64Binary xml) throws CalculoModificacionException {
		
		CalculoModificacionCuponActivoRequest cuponActivoRequest = new CalculoModificacionCuponActivoRequest();
		
		try {
			logger.debug("Parametros para llamada a SW de calculo:");
			
			// Cupon asociado a la modificacion
			cuponActivoRequest.setCuponModificacion(cupon);
			logger.debug("cupon: " + cupon);
			
			// Indicador de calculo de situacion actual
			cuponActivoRequest.setCalcularSituacionActual(calcularSituacionActual);
			logger.debug("calcularSituacionActual: " + calcularSituacionActual);
			
			// Tipo de referencia de la poliza asociada a la modificacion
			if (PolizaReferenciaTipo.P.value().equals(tipoPoliza.toString()))
				cuponActivoRequest.setTipoPoliza(PolizaReferenciaTipo.P);
			else if (PolizaReferenciaTipo.C.value().equals(tipoPoliza.toString()))
				cuponActivoRequest.setTipoPoliza(PolizaReferenciaTipo.C);
			
			logger.debug("tipo de poliza: " + cuponActivoRequest.getTipoPoliza());
			
			// Xml de la modificacion
			cuponActivoRequest.setModificacionPoliza(xml);
			
		} 
		catch (Exception e) {
			throw new CalculoModificacionException ("Error al crear los parametros para la llamada a calculoModificacionCuponActivo", e);
		}
		
		return cuponActivoRequest;
	}
	
	/**
	 * Genera el objeto para llamar al SW de calculo de modificacion
	 * @param realPath
	 * @return
	 * @throws CalculoModificacionException
	 */
	private ContratacionSCCalculoModificaciones getSrvCalculoModificacion (final String realPath) throws CalculoModificacionException {
		
		// Establece el proxy si no se ha hecho anteriormente
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
		// Obtiene la ubicacion del .wsdl de calculo de modificacion
		URL wsdlLocation = getWsdlLocation(realPath);

		// Obtiene del fichero de propiedades los valores necesarios para generar el objeto para llamar al SW de calculo de modificacion
		ContratacionSCCalculoModificaciones srv = null;
		try {
			String wsLocation = WSUtils.getBundleProp("calculoAnexoModificacion.location");
			String wsPort = WSUtils.getBundleProp("calculoAnexoModificacion.port");
			String wsService = WSUtils.getBundleProp("calculoAnexoModificacion.service");
			QName serviceName = new QName(wsLocation, wsService);
			QName portName = new QName(wsLocation, wsPort);
			
			ContratacionSCCalculoModificaciones_Service srvCalculoAm = new ContratacionSCCalculoModificaciones_Service(wsdlLocation, serviceName);
			srv = srvCalculoAm.getPort(portName, ContratacionSCCalculoModificaciones.class);
			
			logger.debug(srv.toString());
			
			// Añade la cabecera de seguridad
			WSUtils.addSecurityHeader(srv);
		} 
		catch (Exception e) {
			throw new CalculoModificacionException ("Error al generar el objeto para llamar al SW de calculo de modificacion" , e);
		}
		
		return srv;
	}
	
	/**
	 * Devuelve la ubicacion del .wsdl de calculo de modificacion
	 * @param realPath
	 * @return
	 * @throws CalculoModificacionException
	 */
	private URL getWsdlLocation(final String realPath) throws CalculoModificacionException {
		
		URL wsdlLocation = null;
		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("calculoAnexoModificacion.wsdl"));
		} 
		catch (MalformedURLException e1) {
			throw new CalculoModificacionException ("Imposible recuperar el WSDL de calculo de modificacion. Revise la Ruta: " + ((wsdlLocation != null) ? wsdlLocation.toString(): ""), e1);
		}
		catch (Exception e2) {
			throw new CalculoModificacionException ("Error inesperado al recuperar el WSDL de calculo de modificacion" , e2);
		}
		
		return wsdlLocation;
	}

}
