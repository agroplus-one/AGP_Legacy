package com.rsi.agp.core.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import es.agroseguro.modulosYCoberturas.Modulo;

public class WSRUtils {

	// Fichero de Propiedades
	private static final ResourceBundle bundle = ResourceBundle.getBundle("webservices");
	private static final ResourceBundle bundle2 = ResourceBundle.getBundle("agp");

	// Logger
	private static final Log logger = LogFactory.getLog(WSRUtils.class);

	private static final String CLAIMS_IAT = "iat";
	private static final String CLAIMS_SUB = "sub";
	private static final String CLAIMS_USUARIO = "usuario";
	private static final String CLAIMS_DESCRIPCION = "descripcion";
	private static final String CLAIMS_ATT_ADC_UNIDAD = "atributosAdicionalesUnidad";
	private static final String CLAIMS_ATT_ADC_USUARIO = "atributosAdicionalesUsuario";
	private static final String CLAIMS_EXP = "exp";
	private static final String KEYSTORE_ENTRY = "{7792be17-064c-4562-b9e4-8fdecb06338c}";

	public static Modulo getModulosCoberturas(String xmlPoliza) throws RestWSException {

		return WSRUtils.getModulosCoberturas(xmlPoliza, WSRUtils.getSecurityToken());
	}

	public static Modulo getCoberturasContratadas(final String xmlPoliza) throws RestWSException {

		return WSRUtils.getCoberturasContratadas(xmlPoliza, WSRUtils.getSecurityToken());
	}
	
	/**
	 * 
	 * @param codigoRega
	 * @param plan
	 * @param linea
	 * @return
	 * @throws RestWSException
	 */
	public static String getInfoRega(final String codigoRega, final String plan, final String linea) throws RestWSException  {
		return WSRUtils.getInfoRega(codigoRega, plan, linea, WSRUtils.getSecurityToken());
	}

	public static String getSecurityToken() throws RestWSException {

		String token = null;
		ClientResponse response = null;

		logger.debug("getSecurityToken - [INIT]");

		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("security.rest.url"));

			String input = "{\"username\":\"" + bundle.getString("security.user") + "\",\"password\":\""
					+ bundle.getString("security.password") + "\"}";

			response = webResource.accept("application/json").type("application/json").post(ClientResponse.class,
					input);

			if (response == null) {

				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					String output = response.getEntity(String.class);
					logger.debug("respuesta SW -> " + output);

					JSONObject json = new JSONObject(output);

					token = json.getString("access_token");
				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 401:
						throw new RestWSException("401 - Usuario no autorizado al sistema");
					case 500:
						throw new RestWSException("500 - Internal server error");
					default:
						throw new RestWSException(response.getStatus() + " - Codigo de respuesta inesperado");
					}
				}
			}
		} catch (UniformInterfaceException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (JSONException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		}

		logger.debug("token -> " + token);

		logger.debug("getSecurityToken - [END]");

		return token;
	}

	public static Modulo getModulosCoberturas(final String xmlPoliza, final String token) throws RestWSException {

		Modulo modulo = null;
		ClientResponse response = null;

		logger.debug("getModulosCoberturas - [INIT]");

		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("ayudasContratacion.rest.url")
							+ bundle.getString("ayudasContratacion.rest.modulosCoberturas"));

			response = webResource.accept("application/xml").type("application/xml")
					.header("authorization", "Bearer " + token).post(ClientResponse.class, xmlPoliza);

			if (response == null) {

				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					String output = response.getEntity(String.class);
					logger.debug("respuesta SW -> " + output);

					modulo = Modulo.Factory.parse(output);
				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Petición incorrecta");
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

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (XmlException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		}

		logger.debug("getModulosCoberturas - [END]");

		return modulo;
	}

	public static Modulo getCoberturasContratadas(final String xmlPoliza, final String token) throws RestWSException {

		Modulo modulo = null;
		ClientResponse response = null;

		logger.debug("getCoberturasContratadas - [INIT]");

		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource(bundle.getString("rest.baseUrl") + bundle.getString("ayudasContratacion.rest.url")
							+ bundle.getString("ayudasContratacion.rest.coberturasContratadas"));

			response = webResource.accept("application/xml").type("application/xml")
					.header("authorization", "Bearer " + token).post(ClientResponse.class, xmlPoliza);

			if (response == null) {

				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					String output = response.getEntity(String.class);
					logger.debug("respuesta SW -> " + output);

					modulo = Modulo.Factory.parse(output);
				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("400 - Petición incorrecta");
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

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (ClientHandlerException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (XmlException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		}

		logger.debug("getCoberturasContratadas - [END]");

		return modulo;
	}

	public static String getAccesoMediador(final String codUsuario, final String descripcion, final String[] mediadores,
			final String[] referencias, final String[] colectivos, final boolean isCert) throws BusinessException {

		StringBuilder token = new StringBuilder();

		logger.debug("getAccesoMediador - [INIT]");

		String[] auxArr = new String[] {};

		if (mediadores != null && mediadores.length > 0) {
			auxArr = (String[]) ArrayUtils.add(auxArr, "COD_MEDIADOR=" + StringUtils.join(mediadores, ","));
		}
		if (referencias != null && referencias.length > 0) {
			auxArr = (String[]) ArrayUtils.add(auxArr, "REF_POLIZA=" + StringUtils.join(referencias, ","));
		}
		if (colectivos != null && colectivos.length > 0) {
			auxArr = (String[]) ArrayUtils.add(auxArr, "REF_COLECTIVO=" + StringUtils.join(colectivos, ","));
		}

		try {

			JSONArray attrAdicUnit = new JSONArray(new String[] { "IDENT_ENTIDAD=" + Constants.ENTIDAD_C616 });
			JSONArray attrAdicUser = new JSONArray(auxArr);

			JSONObject header = new JSONObject();
			JSONObject payload = new JSONObject();

			header.put("typ", "JWT");
			header.put("alg", isCert ? "RS256" : "HS256");

			Calendar now = Calendar.getInstance();

			payload.put(CLAIMS_IAT, now.getTimeInMillis() / 1000);
			payload.put(CLAIMS_SUB, codUsuario);
			payload.put(CLAIMS_USUARIO, codUsuario);
			payload.put(CLAIMS_DESCRIPCION, descripcion);
			payload.put(CLAIMS_ATT_ADC_UNIDAD, attrAdicUnit);
			payload.put(CLAIMS_ATT_ADC_USUARIO, attrAdicUser);

			now.add(Calendar.MINUTE, 30);
			payload.put(CLAIMS_EXP, now.getTimeInMillis() / 1000);

			String headerStr = Base64.encodeBase64URLSafeString(header.toString().getBytes());
			String payloadStr = Base64.encodeBase64URLSafeString(payload.toString().getBytes());

			token.append(headerStr);
			token.append(".");
			token.append(payloadStr);
			
			String signature = isCert ? getSignatureWithCert(token.toString()) : getSignatureWithSecret(token.toString());
			token.append(".");
			token.append(signature);
		} catch (JSONException e) {
			throw new BusinessException(e);
		} catch (InvalidKeyException e) {
			throw new BusinessException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException(e);
		} catch (CertificateException e) {
			throw new BusinessException(e);
		} catch (KeyStoreException e) {
			throw new BusinessException(e);
		} catch (UnrecoverableEntryException e) {
			throw new BusinessException(e);
		} catch (IOException e) {
			throw new BusinessException(e);
		}

		logger.debug("token -> " + token);

		logger.debug("getAccesoMediador - [END]");

		return token.toString();
	}

	private static String getSignatureWithSecret(final String sigtkn)
			throws InvalidKeyException, NoSuchAlgorithmException {

		return WSRUtils.getSignature(sigtkn, bundle2.getString("portalMediador.secret"));
	}

	private static String getSignatureWithCert(final String sigtkn)
			throws InvalidKeyException, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException,
			UnrecoverableEntryException {

		char[] keyStorePassword = bundle2.getString("portalMediador.keystore.cert.pass").toCharArray();

		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(WSRUtils.class.getResourceAsStream("AgroplusSegurosRGA.pfx"), keyStorePassword);

		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(KEYSTORE_ENTRY,
				new KeyStore.PasswordProtection(keyStorePassword));
		PrivateKey privKey = pkEntry.getPrivateKey();

		return WSRUtils.getSignature(sigtkn, privKey);
	}

	private static String getSignature(final String data, final Object key)
			throws NoSuchAlgorithmException, InvalidKeyException {
		Mac hmac = Mac.getInstance("HmacSHA256");
		byte[] encoded;
		if (key instanceof PrivateKey) {
			encoded = ((PrivateKey) key).getEncoded();
		} else {
			encoded = Base64.decodeBase64(key.toString().getBytes());
		}
		SecretKeySpec secretKey = new SecretKeySpec(encoded, "HmacSHA256");
		hmac.init(secretKey);
		byte[] signedBytes = hmac.doFinal(data.getBytes());
		return Base64.encodeBase64URLSafeString(signedBytes);
	}
	
	public static String getInfoRega(final String codigoRega, final String plan, final String linea, final String token) throws RestWSException {

		ClientResponse response = null;
		String output = "";

		logger.debug("WSRUtils - getInfoRega() - init");

		try {

			Client client = Client.create();

			WebResource webResource = client.resource(bundle.getString("rest.baseUrl")
					+ bundle.getString("rega.rest.url") + bundle.getString("rega.rest.informar") + "/" + codigoRega
					+ "?plan=" + plan + "&linea=" + linea + "&censo=true&ambitoEquivalente=true");

			response = webResource.accept("application/json").type("application/json")
					.header("authorization", "Bearer " + token).get(ClientResponse.class);

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
					case 500:
						throw new RestWSException("500 - Internal server error");
					default:
						throw new RestWSException(response.getStatus() + " - Codigo de respuesta inesperado");
					}
				}
			}
		} catch (UniformInterfaceException e) {

			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de autorización", e);
		} 

		logger.debug("WSRUtils - getInfoRega() - end");

		return output;
	}
}