package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.ged.impl.DocumentacionGedManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.sbp.ConsultaSbpManager;
import com.rsi.agp.core.webapp.util.BigDecimalEditor;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

/**
 * P0073325 - RQ. 16
 * 
 * - Realiza la llamada a la logica de negocio del manager
 * DocumentacionGedManager.uploadDocumento y gestiona los mensajes a mostrar en
 * la pantalla de carga documentacion firmada de polizas
 *
 */
public class CargaDocFirmadaController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(CargaDocFirmadaController.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	private DocumentacionGedManager documentacionGedManager;
	private PolizaManager polizaManager;
	private ConsultaSbpManager consultaSbpManager;

	private static final String ALERTA = "alerta";
	private static final String MENSAJE = "mensaje";
	private static final String ORIGEN_POLIZA_GENERAL = "1";
	private static final String ID_POLIZA = "idPoliza";
	private static final String ORIGEN_POLIZA = "origenPoliza";
	private static final String USUARIO = "usuario";

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doCargaDocFirmada(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info("CargaDocFirmadaController - doCargaDocFirmada - init");

		JSONObject resultado = new JSONObject();

		// Obtenemos el tipo de poliza
		String origenPoliza = request.getParameter(ORIGEN_POLIZA);

		if (ORIGEN_POLIZA_GENERAL.equals(origenPoliza)) {
			resultado = gestionarSubidaDocPolizaSbp(request, response);
		} else {
			resultado = gestionarSubidaDocPoliza(request, response);
		}

		this.getWriterJSON(response, resultado);

		logger.info("CargaDocFirmadaController - doCargaDocFirmada - end");

		return null;
	}

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	private JSONObject gestionarSubidaDocPoliza(HttpServletRequest request, HttpServletResponse response)
			throws JSONException {

		logger.info("CargaDocFirmadaController - gestionarSubidaDocPoliza - init");

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		MultipartFile file = obtenerArchivoRequest(request);
		Poliza poliza;

		JSONObject parameters = new JSONObject();

		try {

			poliza = (Poliza) polizaManager.getPoliza(Long.parseLong(request.getParameter(ID_POLIZA)));

			documentacionGedManager.uploadDocumentoPoliza(usuario.getCodusuario(), file, poliza);
			parameters.put(MENSAJE, bundle.getString("mensaje.cargaDocFirmada.OK"));
			LOGGER.debug("Documento firma subido a GED correctamente.");

		} catch (Exception e) {
			LOGGER.error("Error durante la subida de la documentacion a GED: ", e);
			parameters.put(ALERTA, bundle.getString("mensaje.cargaDocFirmada.KO"));
		}

		logger.info("CargaDocFirmadaController - gestionarSubidaDocPoliza - end");

		return parameters;
	}

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 */
	private JSONObject gestionarSubidaDocPolizaSbp(HttpServletRequest request, HttpServletResponse response)
			throws JSONException {

		logger.info("CargaDocFirmadaController - gestionarSubidaDocPolizaSbp - init");

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		MultipartFile file = obtenerArchivoRequest(request);

		JSONObject parameters = new JSONObject();

		Long idPolizaSbp = Long.parseLong(request.getParameter(ID_POLIZA));

		PolizaSbp polizaSbp = consultaSbpManager.getPolizaSbpById(idPolizaSbp);

		try {
			documentacionGedManager.uploadDocumentoPolizaSbp(usuario.getCodusuario(), file, polizaSbp);
			parameters.put(MENSAJE, bundle.getString("mensaje.cargaDocFirmada.OK"));
			LOGGER.debug("Documento firma subido a GED correctamente.");

		} catch (Exception e) {
			LOGGER.error("Error durante la subida de la documentacion a GED: ", e);
			parameters.put(ALERTA, bundle.getString("mensaje.cargaDocFirmada.KO"));
		}

		logger.info("CargaDocFirmadaController - gestionarSubidaDocPolizaSbp - end");

		return parameters;
	}

	/**
	 * P0073325 - REQ. 19
	 * 
	 * @param req
	 * @return
	 */
	private MultipartFile obtenerArchivoRequest(HttpServletRequest req) {
		final MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) req;
		return (MultipartFile) multiReq.getFileMap().get("file");
	}

	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(BigDecimal.class, null, new BigDecimalEditor());
	}

	public void setDocumentacionGedManager(DocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setConsultaSbpManager(ConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}
}