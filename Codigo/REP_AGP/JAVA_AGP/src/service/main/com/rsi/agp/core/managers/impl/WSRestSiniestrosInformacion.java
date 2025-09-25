package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoVida;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class WSRestSiniestrosInformacion {

	// Fichero de Propiedades
	private static final ResourceBundle bundle = ResourceBundle.getBundle("webservices");

	// Logger
	private static final Log logger = LogFactory.getLog(WSRestSiniestrosInformacion.class);

	/*
	 * Recupera la información de los siniestros de una póliza para el Grupo de
	 * Negocio "Vida" lanzando llamada al SW de Información Siniestros por REST
	 */
	public static String getSiniestrosGanadoVida(final String referencia, final BigDecimal codplan, final String token)
			throws RestWSException, JSONException {

		ClientResponse response = null;
		String output = "";

		logger.debug("WSRestSiniestrosInformacion- getSiniestrosGanadoVida (REST) - [INIT]");

		try {
			Integer plan = codplan.intValue();

			Client client = Client.create();
			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("siniestrosInformacion.rest.url")
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanado") + "/" + plan + "/"
							+ referencia + bundle.getString("siniestrosInformacion.rest.siniestrosGanadoVida"));

			logger.debug("Valor de webResource:" + webResource.toString());

			response = webResource.accept("application/json").type("application/json")
					.header("authorization", "Bearer " + token).get(ClientResponse.class);

			logger.debug("Valor de response.getStatus():" + response.getStatus());

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					output = response.getEntity(String.class);
					logger.debug("respuesta SW -> " + output);

				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Petición incorrecta");
					case 401:
					case 403:
						throw new RestWSException(response.getStatus() + " - No autorizado");
					case 404:
						logger.debug("Se ha retornado: 404 - Recursos no encontrados ");
						return output;
					case 500:
						throw new RestWSException("500 - Internal server error");
					default:
						throw new RestWSException(response.getStatus() + " - Codigo de respuesta inesperado");
					}
				}
			}
		} catch (UniformInterfaceException e) {

			throw new RestWSException("Error inesperado en el SW REST de getSiniestrosGanadoRetirada", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW REST de getSiniestrosGanadoRetirada", e);
		}

		logger.debug("WSRestSiniestrosInformacion- getSiniestrosGanadoRetirada(REST) - [END]");

		return output;
	}

	/*
	 * Recupera la información de los siniestros de una póliza para el Grupo de
	 * Negocio "RyD"
	 */
	public static String getSiniestrosGanadoRetirada(final String referencia, final BigDecimal codplan,
			final String token) throws RestWSException, JSONException {

		ClientResponse response = null;
		String output = "";

		logger.debug("WSRestSiniestrosInformacion- getSiniestrosGanadoRetirada(REST) - [INIT]");

		try {
			Integer plan = codplan.intValue();

			Client client = Client.create();
			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("siniestrosInformacion.rest.url")
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanado") + "/" + plan + "/"
							+ referencia + bundle.getString("siniestrosInformacion.rest.siniestrosGanadoRyD"));

			logger.debug("Valor de webResource:" + webResource.toString());

			response = webResource.accept("application/json").type("application/json")
					.header("authorization", "Bearer " + token).get(ClientResponse.class);

			logger.debug("Valor de response.getStatus():" + response.getStatus());

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					output = response.getEntity(String.class);
					logger.debug("respuesta SW -> " + output);
				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Petición incorrecta");
					case 401:
					case 403:
						throw new RestWSException(response.getStatus() + " - No autorizado");
					case 404:
						logger.debug("S.Web de Información de Siniestros ha devuelto: 404 - Recursos no Encontrados");
						return output;
					case 500:
						throw new RestWSException("500 - Internal server error");
					default:
						throw new RestWSException(response.getStatus() + " - Codigo de respuesta inesperado");
					}
				}
			}
		} catch (UniformInterfaceException e) {

			throw new RestWSException("Error inesperado en el SW REST de getSiniestrosGanadoRetirada", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW REST de getSiniestrosGanadoRetirada", e);
		}

		logger.debug("WSRestSiniestrosInformacion- getSiniestrosGanadoRetirada(REST) - [END]");
		return output;
	}

	/*
	 * Recupera la información de los siniestros de una póliza para el Grupo de
	 * Negocio "RyD"
	 */
	public static String getActasGanado(final String referencia, final BigDecimal codplan, final String token)
			throws RestWSException {

		ClientResponse response = null;

		String output = "";

		logger.debug("WSRestSiniestrosInformacion- getActasGanado(REST) - [INIT]");

		try {

			Client client = Client.create();

			Integer plan = codplan.intValue();

			WebResource webResource1 = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("siniestrosInformacion.rest.url")
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanado") + "/" + plan + "/"
							+ referencia + bundle.getString("siniestrosInformacion.rest.siniestrosGanadoActa"));

			logger.debug("Valor de webResource1:" + webResource1.toString());

			response = webResource1.accept("application/json").type("application/json")
					.header("authorization", "Bearer " + token).get(ClientResponse.class);

			logger.debug("Valor de response.getStatus():" + response.getStatus());

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					output = response.getEntity(String.class);
					logger.debug("respuesta SW -> " + output);

				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Petición incorrecta");
					case 404:
						throw new RestWSException("404 - Recursos no encontrados");
					case 401:
					case 403:
						throw new RestWSException(response.getStatus() + " - No autorizado");
					case 500:
						throw new RestWSException("500 - Internal server error");
					default:
						throw new RestWSException(response.getStatus() + " - Codigo de respuesta inesperado");
					}
				}
			}
		} catch (UniformInterfaceException e) {
			throw new RestWSException("Error inesperado en el SW REST de getActasGanado", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW REST de getActasGanado", e);
		}

		logger.debug("WSRestSiniestrosInformacion- getActasGanado(REST) - [END]");

		return output;
	}

	/*
	 * Se lanza llamada al SW (REST) de Siniestros Informacion (Método
	 * getPDFActaGanado) para obtener el pdf con la información del Acta de Tasación
	 * seleccionado
	 */
	public static byte[] getPDFActaGanado(final Integer serie, final Integer numero, final String letra,
			final String token) throws RestWSException, IOException, JSONException {

		ClientResponse clientResponse = null;

		byte[] pdf = null;

		logger.debug("WSRestSiniestrosInformacion- getPDFActaGanado(REST) - [INIT]");

		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("siniestrosInformacion.rest.url")
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanadoActaPdf") + "/" + serie + "/"
							+ numero + "/" + letra
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanadoActaDocumento"));

			clientResponse = webResource.accept("application/json").header("authorization", "Bearer " + token)
					.get(ClientResponse.class);

			logger.debug("Valor de clientResponse.getStatus():" + clientResponse.getStatus());

			if (clientResponse == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (clientResponse.getStatus() == 200) {
					pdf = org.apache.commons.io.IOUtils.toByteArray(clientResponse.getEntityInputStream());
					logger.debug("LLAMADA CORRECTA (PDF de ACTA DE TASACIÓN) GANADO - DEVOLVEMOS 200-OK");
				
				} else {
					
					JSONArray jsonArray = new JSONArray(clientResponse.getEntity(String.class));
					
					HashMap<String, String> Error = obtenerError(jsonArray);

					String errorMsg = Error.get("codigo") +" - " + Error.get("mensaje");
					logger.debug("Se ha producido un error: "+errorMsg);
					
					throw new RestWSException(Error.get("codigo") +" - " + Error.get("mensaje"));
				}    
						
			}
		} catch (UniformInterfaceException e) {

			throw new RestWSException("Error inesperado en el SW REST de getPDFActaGanado", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW REST de getPDFActaGanado", e);
		}

		return pdf;
	}

	/*
	 * Se lanza llamada al SW (REST) de Siniestros Informacion (Método
	 * getPDFActaGanado) para obtener el pdf con la información del Acta de Tasación
	 * seleccionado
	 */

	public static byte[] getPDFCartaPagoGanado(final Integer serie, final Integer numero, final String letra,
			final String token) throws RestWSException, IOException, JSONException {

		byte[] pdf = null;

		logger.debug("WSRestSiniestrosInformacion- getPDFCartaPagoGanado(REST) - [INIT]");

		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("siniestrosInformacion.rest.url")
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanadoActaPdf") + "/" + serie + "/"
							+ numero + "/" + letra
							+ bundle.getString("siniestrosInformacion.rest.siniestrosGanadoActaCartaPago"));

			logger.debug("Valor de webResource:" + webResource);

			ClientResponse clientResponse = webResource.accept("application/json")
					.header("authorization", "Bearer " + token).get(ClientResponse.class);

			logger.debug("Valor de clientResponse.getStatus():" + clientResponse.getStatus());

			logger.debug("Valor de response:" + clientResponse.toString());

			if (clientResponse == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (clientResponse.getStatus() == 200) {
					pdf = org.apache.commons.io.IOUtils.toByteArray(clientResponse.getEntityInputStream());

					logger.debug("LLAMADA CORRECTA (PDF de CARTA DE PAGO) GANADO - DEVOLVEMOS 200-OK");
				} else {

					JSONArray jsonArray = new JSONArray(clientResponse.getEntity(String.class));
					
					HashMap<String, String> Error = obtenerError(jsonArray);

					String errorMsg = Error.get("codigo") +" - " + Error.get("mensaje");
					logger.debug("Se ha producido un error: "+errorMsg);
					
					throw new RestWSException(Error.get("codigo") +" - " + Error.get("mensaje"));

				}
			}

		} catch (UniformInterfaceException e) {

			throw new RestWSException("Error inesperado en el SW REST de getPDFCartaPagoGanado", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW REST de getPDFCartaPagoGanado", e);
		}

		logger.debug("WSRestSiniestrosInformacion- getPDFCartaPagoGanado(REST) - [END]");

		return pdf;

	}
	
	private static HashMap<String, String> obtenerError(final JSONArray jsonArray) throws JSONException{
		
		HashMap<String, String> error = new HashMap<String, String>();

		if (jsonArray != null) {

			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				
				String errorDs = jsonArray.get(i).toString();
				
				errorDs = errorDs.replace("[", "");
				errorDs = errorDs.replace("]", "");

				errorDs = errorDs.replace("{", "");
				errorDs = errorDs.replace("}", "");


				String[] errorR = errorDs.split(",");
				
				int numDatSin = errorR.length;
				for (int j = 0; j < numDatSin; j++) {
					String datoErr = errorR[j];
					datoErr = datoErr.replace("\"", "");
					String[] datError = datoErr.split(":");

					String dato = datError[0];
					String valor = "";
					if (datError.length > 1) {
						valor = datError[1];
					}

					// * Parseamos todos los datos recibidos en el SW por cada siniestro recibido */
					if (dato.equals("codigo")) {
						error.put("codigo", valor);
					}else if (dato.equals("mensaje")) {
						error.put("mensaje", valor);
					}
				}

			}
		}
		return error;
	}
	
}
