package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.ImportacionPolizasManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class ImportacionPolizasController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(ImportacionPolizasController.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	private ImportacionPolizasManager importacionPolizasManager;

	public void doIniciarImportacionPoliza(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("ImportacionPolizasController - doIniciarImportacionPoliza [INIT]");

		JSONObject objeto = new JSONObject();

		HashMap<String, Object> resultadoImporta = new HashMap<String, Object>();

		try {

			// Recupero los valores introducidos por el usuario
			Integer plan = Integer.parseInt(StringUtils.nullToString(request.getParameter("plan")));
			String referencia = StringUtils.nullToString(request.getParameter("referencia")).toUpperCase();
			String usuario = StringUtils.nullToString(request.getParameter("usuario"));
			String tipoRef = StringUtils.nullToString(request.getParameter("tipoRefPoliza")).toUpperCase();
			Character tipoRefPoliza = tipoRef.toUpperCase().charAt(0);

			String realPath = this.getServletContext().getRealPath("/WEB-INF/");

			try {
				resultadoImporta = importacionPolizasManager.iniciarImportacion(plan, referencia, usuario,
						tipoRefPoliza, realPath);
				String alerta = (String) resultadoImporta.get("alert");
				objeto.put("dato", "KO");
				objeto.put("alert", alerta);

				String resultado_str = (String) resultadoImporta.get("resultado");
				if (resultado_str.equals("OK")) {
					objeto.put("dato", "OK");
				}

			} catch (BusinessException e) {
				e.printStackTrace();
				objeto.put("dato", "KO");
				objeto.put("alert", "Atención! Se ha producido un error en la importación de la póliza");
			}

			response.setCharacterEncoding("UTF-8");

			getWriterJSON(response, objeto);

			logger.debug("ImportacionPolizasController - doIniciarImportacionPoliza [END]");
		} catch (JSONException e) {
			logger.error("Excepcion : ImportacionPolizasController - doIniciarImportacionPoliza", e);
		} catch (Exception e) {
			logger.debug(
					"ImportacionPolizasController.doIniciarImportacionPoliza - Ocurrió un error importar la póliza");
			logger.error("Ocurrió un error al importar la Poliza.", e);
			try {
				String alerta_str = (String) resultadoImporta.get("alert");
				if (alerta_str.equals("")) {
					objeto.put("alert", "Ha ocurrido un error importar la póliza");
				} else {
					objeto.put("alert", alerta_str);
				}

				getWriterJSON(response, objeto);
			} catch (JSONException e1) {
				logger.error("Excepcion : ImportacionPolizasController - doIniciarImportacionPoliza", e1);
			}
		}

	}

	// setters para la inyeccion de dependencias
	public void setImportacionPolizasManager(ImportacionPolizasManager importacionPolizasManager) {
		this.importacionPolizasManager = importacionPolizasManager;
	}

}
