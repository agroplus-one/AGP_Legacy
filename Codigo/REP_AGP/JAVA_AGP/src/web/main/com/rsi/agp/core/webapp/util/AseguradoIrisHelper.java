package com.rsi.agp.core.webapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public final class AseguradoIrisHelper {

	private static final Log LOGGER = LogFactory.getLog(AseguradoIrisHelper.class);

	// Fichero de Propiedades
	private static final ResourceBundle bundle = ResourceBundle.getBundle("webservices");

	public List<AseguradoIrisBean> getAseguradoIris(final String codigoEntidad, final String idExternoPersona,
			final String tipoPersona, final String codUsuario, final String codTerminal, final String apiKey)
			throws RestWSException, JSONException {

		List<AseguradoIrisBean> result = null;

		ClientResponse response = null;
		String output = "";

		LOGGER.debug("getAseguradoIris [init]");

		try {

			Client client = Client.create();

			String urlCalled = bundle.getString("rest.rsi.baseUrl") + bundle.getString("serviciosSeguros.rest.url")
					+ "/" + idExternoPersona + bundle.getString("serviciosSeguros.rest.personas") + "?codigoEntidad="
					+ codigoEntidad + "&idExternoPersona=" + idExternoPersona + "&tipoPersona=" + tipoPersona;

			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled);

			response = webResource.accept("application/json").type("application/json").header("CODSecUser", codUsuario)
					.header("CODTerminal", codTerminal).header("CODSecEnt", 9996).header("API-KEY", apiKey)
					.header("CODCanal", "TF").get(ClientResponse.class);

			if (response == null) {

				throw new RestWSException("Sin respuesta del servicio web");
			} else {
				if (response.getStatus() == 200) {

					output = response.getEntity(String.class);
					LOGGER.debug("respuesta SW -> " + output);

					JSONObject json = new JSONObject(output);
					JSONObject respuesta = json.getJSONObject("EE_O_ConsultaPersonasExternas")
							.getJSONObject("Respuesta");
					int numRegistros = respuesta.getInt("numeroRegistros");
					result = new ArrayList<AseguradoIrisBean>(numRegistros);
					AseguradoIrisBean aseg;
					JSONArray listaPersonas = respuesta.getJSONArray("ListaPersonas");
					for (int i = 0; i < numRegistros; i++) {
						aseg = this.new AseguradoIrisBean();
						JSONObject persona = listaPersonas.getJSONObject(i);
						aseg.setTipoIdentificacion(persona.getString("tipoIdentificacion"));
						aseg.setCodigoOficina(persona.getString("codigoOficina"));
						aseg.setFechaNacimiento(persona.getString("fechaNacimiento"));
						aseg.setIdExterno(persona.getString("idExterno"));
						aseg.setIdInternoPe(persona.getString("idInternoPe"));
						aseg.setIndicadorAcuerdoRuralvia(persona.getString("indicadorAcuerdoRuralvia"));
						aseg.setNombre(persona.getString("nombre"));
						aseg.setPrimerApellido(persona.getString("primerApellido"));
						aseg.setSegundoApellido(persona.getString("segundoApellido"));
						aseg.setRazonSocial(persona.getString("razonSocial"));
						result.add(aseg);
					}
				} else {

					// Errores especificados por Agroseguro
					switch (response.getStatus()) {
					case 400:
						throw new RestWSException("Firma en tableta no disponible.");
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
			throw new RestWSException("Error inesperado en el SW de consulta asegurado IRIS", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de consulta asegurado IRIS", e);
		}

		LOGGER.debug("getAseguradoIris [end]");
		return result;
	}
	
	public Boolean isAseguradoVulnerable(final String codigoEntidad, final String idExternoPersona,
			final String tipoPersona, final String apiKey, final String codTerminal, final String usuario) throws RestWSException, JSONException {
		Boolean resultado = Boolean.FALSE;
		
		if (StringUtils.isNullOrEmpty(codTerminal)) {
			resultado = isAseguradoVulnerableNoTF(codigoEntidad, idExternoPersona, tipoPersona, apiKey);
		}
		else {
			resultado = isAseguradoVulnerableTF(codigoEntidad, idExternoPersona, tipoPersona, apiKey, codTerminal, usuario);
		}
		
		return resultado;
	}

	private Boolean isAseguradoVulnerableNoTF(final String codigoEntidad, final String idExternoPersona,
			final String tipoPersona, final String apiKey) throws RestWSException, JSONException {

		Boolean result = Boolean.FALSE;
		ClientResponse response = null;

		LOGGER.debug("isAseguradoVulnerable [init]");

		try {

			Client client = Client.create();

			String urlCalled = bundle.getString("rest.rsi.baseUrl3") + bundle.getString("serviciosSeguros2.rest.url")
					+ "?codigoEntidad=" + codigoEntidad + "&idExterno=" + idExternoPersona + "&codTipoPersona="
					+ tipoPersona;

			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled);

			response = webResource.accept("application/json").type("application/json").header("apikey", apiKey)
					.get(ClientResponse.class);

			result = procesarResponseAsegVulner(response);

		} catch (UniformInterfaceException e) {
			throw new RestWSException("Error inesperado en el SW de marca de vulnerabilidad", e);
		} catch (ClientHandlerException e) {
			LOGGER.debug("Error inesperado en el SW de marca de vulnerabilidad" + e.getMessage());
			return false;
		}

		LOGGER.debug("isAseguradoVulnerable [end]");
		return result;
	}

	private Boolean isAseguradoVulnerableTF(final String codigoEntidad, final String idExternoPersona,
			final String tipoPersona, final String apiKey, final String codTerminal, final String codUsuario) throws RestWSException, JSONException {

		Boolean result = Boolean.FALSE;
		ClientResponse response = null;

		LOGGER.debug("isAseguradoVulnerableTF [init]");

		try {

			Client client = Client.create();

			String urlCalled = bundle.getString("rest.rsi.baseUrl3") + bundle.getString("serviciosSeguros3.rest.url")
					+ "/" + idExternoPersona + "?codigoEntidad=" + codigoEntidad + "&codTipoPersona=" + tipoPersona;

			LOGGER.debug("urlCalled: " + urlCalled);

			WebResource webResource = client.resource(urlCalled);

			response = webResource.accept("application/json").type("application/json")
					.header("apikey", apiKey).header("X-CODSecUser", codUsuario).header("X-CodTerminal", codTerminal)
					.get(ClientResponse.class);

			result = procesarResponseAsegVulnerTF(response);

		} catch (UniformInterfaceException e) {
			throw new RestWSException("Error inesperado en el SW de marca de vulnerabilidad", e);
		} catch (ClientHandlerException e) {
			throw new RestWSException("Error inesperado en el SW de marca de vulnerabilidad", e);
		}

		LOGGER.debug("isAseguradoVulnerableTF [end]");
		return result;
	}

	private Boolean procesarResponseAsegVulner(final ClientResponse response) throws RestWSException, JSONException {

		Boolean result = Boolean.FALSE;
		String output = "";

		if (response == null) {
			LOGGER.debug("Sin respuesta del servicio web.");
			return false;
		} else {
			if (response.getStatus() == 200) {

				output = response.getEntity(String.class);
				LOGGER.debug("respuesta SW -> " + output);

				JSONObject json = new JSONObject(output);
				
				
				if (!json.isNull("EE_O_Consulta")) {
					JSONObject respuesta = json.getJSONObject("EE_O_Consulta").getJSONObject("Respuesta");
					int numRegistros = respuesta.getInt("numeroRegistros") != 0 ? respuesta.getInt("numeroRegistros") : 1;
					JSONArray listaPersonas = respuesta.getJSONArray("ListaPersonas");
					
					if (listaPersonas == null) {
						numRegistros = 0;
					}
					
					for (int i = 0; i < numRegistros; i++) {
						JSONObject persona = listaPersonas.getJSONObject(i);
						if (com.rsi.agp.core.util.Constants.CHARACTER_S.equals(persona.getString("vulnerable").charAt(0))) {
							result = Boolean.TRUE;
							break;
						}
					}
				}
			} else {

				// Errores especificados por Agroseguro
				switch (response.getStatus()) {
				case 400:
					LOGGER.debug("ERROR 404");
					return false;
					//throw new RestWSException("400 - Peticion incorrecta");
				case 401:
					LOGGER.debug("ERROR 401");
					return false;
				case 403:
					LOGGER.debug("ERROR 403 - No autorizado");
					return false;
				case 500:
					LOGGER.debug("ERROR 500 - Error de servidor");
					return false;
				default:
					LOGGER.debug("ERROR - Codigo de respuesta inesperado");
					return false;
				}
			}
		}
		return result;
	}
	
	private Boolean procesarResponseAsegVulnerTF(final ClientResponse response) throws RestWSException, JSONException {

		Boolean result = Boolean.FALSE;
		String output = "";

		if (response == null) {
			LOGGER.debug("Sin respuesta del servicio web.");
			return false;
		} else {
			if (response.getStatus() == 200) {

				output = response.getEntity(String.class);
				LOGGER.debug("respuesta SW -> " + output);

				JSONObject json = new JSONObject(output);
				
				
				if (!json.isNull("respuesta")) {
					JSONObject respuesta = json.getJSONObject("respuesta");
					int numRegistros = respuesta.getInt("numeroRegistros") != 0 ? respuesta.getInt("numeroRegistros") : 1;
					JSONArray listaPersonas = respuesta.getJSONArray("listaPersonas");
					
					if (listaPersonas == null) {
						numRegistros = 0;
					}
					for (int i = 0; i < numRegistros; i++) {
						JSONObject persona = listaPersonas.getJSONObject(i);
						if (com.rsi.agp.core.util.Constants.CHARACTER_S.equals(persona.getString("vulnerable").charAt(0))) {
							result = Boolean.TRUE;
							break;
						}
					}
				}
			} else {

				// Errores especificados por Agroseguro
				switch (response.getStatus()) {
				case 400:
					LOGGER.debug("ERROR 404");
					return false;
					//throw new RestWSException("400 - Peticion incorrecta");
				case 401:
					LOGGER.debug("ERROR 401");
					return false;
				case 403:
					LOGGER.debug("ERROR 403 - No autorizado");
					return false;
				case 500:
					LOGGER.debug("ERROR 500 - Error de servidor");
					return false;
				default:
					LOGGER.debug("ERROR - Codigo de respuesta inesperado");
					return false;
				}
			}
		}
		return result;
	}
	
	

	public class AseguradoIrisBean {

		private String tipoIdentificacion;
		private String idExterno;
		private String idInternoPe;
		private String razonSocial;
		private String nombre;
		private String primerApellido;
		private String segundoApellido;
		private String codigoOficina;
		private String fechaNacimiento;
		private String indicadorAcuerdoRuralvia;

		public AseguradoIrisBean() {
			super();
		}

		public String getTipoIdentificacion() {
			return tipoIdentificacion;
		}

		public void setTipoIdentificacion(String tipoIdentificacion) {
			this.tipoIdentificacion = tipoIdentificacion;
		}

		public String getIdExterno() {
			return idExterno;
		}

		public void setIdExterno(String idExterno) {
			this.idExterno = idExterno;
		}

		public String getIdInternoPe() {
			return idInternoPe;
		}

		public void setIdInternoPe(String idInternoPe) {
			this.idInternoPe = idInternoPe;
		}

		public String getRazonSocial() {
			return razonSocial;
		}

		public void setRazonSocial(String razonSocial) {
			this.razonSocial = razonSocial;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getPrimerApellido() {
			return primerApellido;
		}

		public void setPrimerApellido(String primerApellido) {
			this.primerApellido = primerApellido;
		}

		public String getSegundoApellido() {
			return segundoApellido;
		}

		public void setSegundoApellido(String segundoApellido) {
			this.segundoApellido = segundoApellido;
		}

		public String getCodigoOficina() {
			return codigoOficina;
		}

		public void setCodigoOficina(String codigoOficina) {
			this.codigoOficina = codigoOficina;
		}

		public String getFechaNacimiento() {
			return fechaNacimiento;
		}

		public void setFechaNacimiento(String fechaNacimiento) {
			this.fechaNacimiento = fechaNacimiento;
		}

		public String getIndicadorAcuerdoRuralvia() {
			return indicadorAcuerdoRuralvia;
		}

		public void setIndicadorAcuerdoRuralvia(String indicadorAcuerdoRuralvia) {
			this.indicadorAcuerdoRuralvia = indicadorAcuerdoRuralvia;
		}
	}
}
