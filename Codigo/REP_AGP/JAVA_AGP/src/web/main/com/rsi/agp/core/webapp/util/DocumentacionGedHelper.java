package com.rsi.agp.core.webapp.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class DocumentacionGedHelper {

	private static final Log LOGGER = LogFactory.getLog(DocumentacionGedHelper.class);

	// Fichero de Propiedades
	private static final ResourceBundle bundle = ResourceBundle.getBundle("webservices");

	private static final String DOC_AGRARIOS = "agrarios";
	private static final String DOC_GENERALES = "generales";

	private static final String EE_I_ALTA_GENERALES = "EE_I_AltaGenerales";
	private static final String EE_I_ALTA_AGRARIO = "EE_I_AltaAgrario";
	private static final String EE_I_MODIF_GENERALES = "EE_I_ModificacionGenerales";
	private static final String EE_I_MODIF_AGRARIO = "EE_I_ModificacionAgrario";
	private static final String BASE_DOCUMENTO = "baseDocumento";
	private static final String FIRMA_EN_TABLETA = "firmaEnTableta";
	private static final String IND_TABLETA = "indTableta";
	private static final String TIPO_MOVR = "tipoMovr";
	private static final String SEGUNDO_APELLIDO_TOMADOR = "segundoApellidoTomador";
	private static final String PRIMER_APELLIDO_TOMADOR = "primerApellidoTomador";
	private static final String NOMBRE_TOMADOR = "nombreTomador";
	private static final String DNI_TOMADOR = "dniTomador";
	private static final String SEGUNDO_APELLIDO_ASEGURADO = "segundoApellidoAsegurado";
	private static final String PRIMER_APELLIDO_ASEGURADO = "primerApellidoAsegurado";
	private static final String NOMBRE_ASEGURADO = "nombreAsegurado";
	private static final String DNI_ASEGURADO = "dniAsegurado";
	private static final String COD_OFICI = "codOfici";
	private static final String COD_MEDIA = "codMedia";
	private static final String FECH_EFEC = "fechEfec";
	private static final String TIPO_POL = "tipoPol";
	private static final String REFERENCIA_AGRO = "referenciaAgro";
	private static final String CODP_TEC = "codpTec";
	private static final String PLAN_AGRO = "planAgro";
	private static final String COD_CIA = "codCia";
	private static final String NUMERO_PROYECTO = "numeroProyecto";
	private static final String PRIMA_NETA = "primaNeta";
	private static final String PRIMA_TOTAL = "primaTotal";
	private static final String NUMERO_SOLICITUD = "numeroSolicitud";
	private static final String CODIGOENTIDAD = "codigoEntidad";
	private static final String CODIGOOFICINA = "codigoOficina";
	private static final String NUM_POLIZA = "numPoliz";
	private static final String NUM_CERTI = "numCerti";
	private static final String USUARIO = "usuario";
	private static final String BORRADO = "borrado";
	private static final String COD_BARRAS_RGA = "codBarraRga";
	private static final String _1 = "1";
	private static final String _3 = "3";
	private static final String _9 = "9";

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param usuario
	 * @param poliza
	 * @param tipoPoliza
	 * @param canalFirma
	 * @return
	 * @throws RestWSException
	 * @throws URISyntaxException
	 */
	public String uploadAltaDocumento(final byte[] documento, final String tipoDocumento,
			final String firmaTableta, final String indTableta, final String apiKey, final BigDecimal codentidad,
			final String codOficina, final BigDecimal codPlan, final Date fechaEfecto, final boolean esLineaGanado,
			final String referencia, final Character tipoReferencia, final String nifCif,
			final String tipoIdentificacion, final String nomAsegurado, final String apellido1, final String apellido2,
			final String nifCifTomador, final String nomTomador, final String codBarras) throws RestWSException {

		LOGGER.debug("DocumentacionGedHelper - uploadDocumento - init");

		ClientResponse response = null;
		String output = "";

		try {

			Client client = Client.create();

			String urlCalled = bundle.getString("rest.rsi.baseUrl") + bundle.getString("serviciosSeguros.rest.url")
					+ bundle.getString("serviciosSeguros.rest.documentos") + "/" + tipoDocumento;
			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled);

			String input = obtenerJsonDeEntrada(documento, tipoDocumento, firmaTableta, indTableta,
					codentidad, codOficina, codPlan, fechaEfecto, esLineaGanado, referencia, tipoReferencia, nifCif,
					tipoIdentificacion, nomAsegurado, apellido1, apellido2, nifCifTomador, nomTomador, codBarras)
							.toString();

			System.out.println("ENTRADA SW: " + input);

			response = webResource.accept("application/json").type("application/json").header("CODCorrelationId", "1")
					.header("API-KEY", apiKey).post(ClientResponse.class, input);

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {

				if (response.getStatus() == 200) {
					output = response.getEntity(String.class);
					LOGGER.debug("RESPUESTA SW: " + output);
				} else {
					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Peticion incorrecta");
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
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		} catch (JSONException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		} catch (IOException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		}

		LOGGER.debug("DocumentacionGedHelper - uploadDocumento - end");

		return output;
	}

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param usuario
	 * @param file
	 * @param poliza
	 * @param poliza
	 * @param string
	 */
	public String uploadModifDocumento(final byte[] documento, final String idDocumentum, final String tipoDocumento,
			final String apiKey) throws RestWSException {

		LOGGER.debug("DocumentacionGedHelper - uploadModifDocumento - init");

		ClientResponse response = null;
		String output = "";

		try {
			Client client = Client.create();

			String urlCalled = bundle.getString("rest.rsi.baseUrl") + bundle.getString("serviciosSeguros.rest.url")
					+ bundle.getString("serviciosSeguros.rest.documentos") + "/" + idDocumentum + "/" + tipoDocumento;
			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled);

			String input = obtenerJsonDeEntradaModif(documento, tipoDocumento).toString();

			System.out.println("ENTRADA SW: " + input);

			response = webResource.accept("application/json").type("application/json").header("CODCorrelationId", "1")
					.header("API-KEY", apiKey).put(ClientResponse.class, input);

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {

				if (response.getStatus() == 200) {
					output = response.getEntity(String.class);
					LOGGER.debug("RESPUESTA SW: " + output);
				} else {
					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Peticion incorrecta");
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
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		} catch (JSONException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		} catch (IOException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		}

		LOGGER.debug("DocumentacionGedHelper - uploadModifDocumento - end");

		return output;

	}

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param documento
	 * @param tipoDocumento
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONObject obtenerJsonDeEntradaModif(final byte[] documento, final String tipoDocumento)
			throws JSONException, IOException {

		LOGGER.debug("DocumentacionGedHelper - obtenerJsonDeEntradaModif - init");

		JSONObject child = new JSONObject();
		JSONObject json = new JSONObject();

		child.put(IND_TABLETA, _3);
		byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(documento);
		child.put(BASE_DOCUMENTO, encoded.toString());

		if (DOC_GENERALES.equals(tipoDocumento)) {
			json = new JSONObject().put(EE_I_MODIF_GENERALES, child);
		} else {
			json = new JSONObject().put(EE_I_MODIF_AGRARIO, child);
		}

		LOGGER.debug("json: " + json.toString());
		LOGGER.debug("DocumentacionGedHelper - obtenerJsonDeEntradaModif - end");

		return json;
	}

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param documento
	 * @param poliza
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONObject obtenerJsonDeEntrada(final byte[] documento, final String tipoDocumento,
			final String firmaTableta, final String indTableta, final BigDecimal codentidad, final String codOficina,
			final BigDecimal codPlan, final Date fechaEfecto, final boolean esLineaGanado, final String referencia,
			final Character tipoReferencia, final String nifCif, final String tipoIdentificacion,
			final String nomAsegurado, final String apellido1, final String apellido2, final String nifCifTomador,
			final String nomTomador, final String codBarras) throws JSONException, IOException {

		LOGGER.debug("DocumentacionGedHelper - obtenerJsonDeEntrada - init");

		JSONObject child = new JSONObject();
		JSONObject json = new JSONObject();

		child.put(CODIGOENTIDAD, codentidad);
		child.put(COD_CIA, "1"); // valor fijo
		child.put(PLAN_AGRO, codPlan);
		child.put(FECH_EFEC, new SimpleDateFormat("yyyy-MM-dd").format(fechaEfecto));
		child.put(COD_MEDIA, codentidad);
		child.put(COD_OFICI, codOficina);
		child.put(DNI_ASEGURADO, nifCif);
		if (Constants.TIPO_IDENTIFICACION_CIF.equals(tipoIdentificacion)) {
			child.put(NOMBRE_ASEGURADO, "");
			child.put(PRIMER_APELLIDO_ASEGURADO, nomAsegurado);
			child.put(SEGUNDO_APELLIDO_ASEGURADO, "");
		} else {
			child.put(NOMBRE_ASEGURADO, nomAsegurado);
			child.put(PRIMER_APELLIDO_ASEGURADO, apellido1);
			child.put(SEGUNDO_APELLIDO_ASEGURADO, apellido2);
		}
		child.put(DNI_TOMADOR, nifCifTomador);
		child.put(NOMBRE_TOMADOR, "");
		child.put(PRIMER_APELLIDO_TOMADOR, nomTomador); // es la razon social
		child.put(SEGUNDO_APELLIDO_TOMADOR, "");
		child.put(TIPO_MOVR, "PRC"); // valor fijo
		child.put(IND_TABLETA, indTableta);
		child.put(FIRMA_EN_TABLETA, firmaTableta);
		child.put(BASE_DOCUMENTO, org.apache.commons.codec.binary.Base64.encodeBase64String(documento));
		child.put(COD_BARRAS_RGA, codBarras);

		if (DOC_GENERALES.equals(tipoDocumento)) {
			child.put(CODP_TEC, "2009"); // valor fijo
			child.put(NUM_POLIZA, "0");
			child.put(NUM_CERTI, "0");
			child.put(PRIMA_TOTAL, "0");
			child.put(PRIMA_NETA, "0");
			child.put(NUMERO_PROYECTO, "0"); // Valor fijo 1			
			json = new JSONObject().put(EE_I_ALTA_GENERALES, child);
		} else {
			// campos especificos de documentos agrarios
			child.put(CODP_TEC, esLineaGanado ? "1001" : "1002"); // valor fijo
			if (StringUtils.isNullOrEmpty(referencia)) {
				// SI NO HAY REFERENCIA, GENERAMOS UNA REFERENCIA FALSA
				child.put(REFERENCIA_AGRO,
						org.apache.commons.lang.StringUtils.right(String.valueOf(new Date().getTime()), 6) + "F");
			} else {
				child.put(REFERENCIA_AGRO, referencia);
			}
			child.put(TIPO_POL, tipoReferencia);
			child.put(NUMERO_SOLICITUD, "0"); // Valor por defecto
			json = new JSONObject().put(EE_I_ALTA_AGRARIO, child);
		}

		LOGGER.debug("json: " + json.toString());
		LOGGER.debug("DocumentacionGedHelper - obtenerJsonDeEntrada - end");

		return json;
	}

	public byte[] getDocumentoGed(final String idDocumentum, final String codEntidad, final String codOficina,
			final String usuario, final String apiKey) throws RestWSException {
		return getDocumentoGed(idDocumentum, codEntidad, codOficina, usuario, 1, apiKey);
	}

	public byte[] getDocumentoSbpGed(final String idDocumentum, final String codEntidad, final String codOficina,
			final String usuario, final String apiKey) throws RestWSException {
		return getDocumentoGed(idDocumentum, codEntidad, codOficina, usuario, 2, apiKey);
	}

	private byte[] getDocumentoGed(final String idDocumentum, final String codEntidad, final String codOficina,
			final String usuario, final int tipoDoc, final String apiKey) throws RestWSException {

		LOGGER.debug("DocumentacionGedHelper - getDocumentoGed - init");

		ClientResponse response = null;

		byte[] doc = null;

		try {

			Client client = Client.create();

			StringBuilder urlCalled = new StringBuilder(bundle.getString("rest.rsi.baseUrl"));
			urlCalled.append(bundle.getString("serviciosSeguros.rest.url"));
			urlCalled.append(bundle.getString("serviciosSeguros.rest.documentos"));
			urlCalled.append("/");
			urlCalled.append(idDocumentum);
			urlCalled.append("/");
			urlCalled.append((tipoDoc == 1 ? DOC_AGRARIOS : DOC_GENERALES));
			urlCalled.append("?");
			urlCalled.append(CODIGOENTIDAD + "=" + codEntidad);
			urlCalled.append("&" + CODIGOOFICINA + "=" + codOficina);
			urlCalled.append("&" + USUARIO + "=" + usuario);
			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled.toString());

			response = webResource.accept("application/json").type("application/json").header("CODCorrelationId", "1")
					.header("API-KEY", apiKey).get(ClientResponse.class);

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {
					String output = response.getEntity(String.class);
					LOGGER.debug("RESPUESTA SW: " + output);
					JSONObject result = new JSONObject(output);					
					if ((Integer) ((JSONObject) result.get("EE_O_Consulta")).get("codigoRetorno") == 1) {
						doc = org.apache.commons.codec.binary.Base64
								.decodeBase64(((JSONObject) ((JSONObject) result.get("EE_O_Consulta")).get("Respuesta"))
										.getString("baseDocumento"));
					} else {
						throw new RestWSException(
								((JSONObject) ((JSONObject) result.get("EE_O_Consulta")).get("Errores"))
										.get("codigoMostrar") + " - "
										+ ((JSONObject) ((JSONObject) result.get("EE_O_Consulta")).get("Errores"))
												.get("mensajeMostrar"));
					}
				} else {
					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Peticion incorrecta");
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
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		} catch (JSONException e) {
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		}

		LOGGER.debug("DocumentacionGedHelper - getDocumentoGed - end");

		return doc;
	}

	public String borrarDocumento(final String idDocumentum, final BigDecimal codEntidad, final String tipoDocumento,
			final String apiKey) throws RestWSException {

		LOGGER.debug("DocumentacionGedHelper - borrarDocumento - end");

		ClientResponse response = null;
		String output = "";

		try {

			Client client = Client.create();

			String urlCalled = bundle.getString("rest.rsi.baseUrl") + bundle.getString("serviciosSeguros.rest.url")
					+ bundle.getString("serviciosSeguros.rest.documentos") + "/" + idDocumentum + "/" + tipoDocumento;
			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled);

			String input = obtenerJsonDeEntradaBorrar(codEntidad, tipoDocumento).toString();

			System.out.println("ENTRADA SW: " + input);

			response = webResource.accept("application/json").type("application/json").header("CODCorrelationId", "1")
					.header("API-KEY", apiKey).put(ClientResponse.class, input);

			if (response == null) {
				throw new RestWSException("Sin respuesta del servicio web");
			} else {

				if (response.getStatus() == 200) {
					output = response.getEntity(String.class);
					LOGGER.debug("RESPUESTA SW: " + output);
				} else {
					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Peticion incorrecta");
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
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de ServiciosSeguros", e);
		} catch (JSONException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		} catch (IOException e) {
			throw new RestWSException("Error inesperado en el SW de Modificacion de documentos ", e);
		}

		LOGGER.debug("DocumentacionGedHelper - borrarDocumento - end");

		return output;
	}

	private JSONObject obtenerJsonDeEntradaBorrar(final BigDecimal codEntidad, final String tipoDocumento)
			throws JSONException, IOException {

		LOGGER.debug("DocumentacionGedHelper - obtenerJsonDeEntradaBorrar - init");

		JSONObject child = new JSONObject();
		JSONObject json = new JSONObject();

		child.put(CODIGOENTIDAD, codEntidad);
		child.put(BORRADO, _1);
		child.put(IND_TABLETA, _9);

		if (DOC_GENERALES.equals(tipoDocumento)) {
			json = new JSONObject().put(EE_I_MODIF_GENERALES, child);
		} else {
			json = new JSONObject().put(EE_I_MODIF_AGRARIO, child);
		}

		LOGGER.debug("json: " + json.toString());
		LOGGER.debug("DocumentacionGedHelper - obtenerJsonDeEntradaBorrar - end");

		return json;
	}
}