package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.impl.TallerConfiguradorPantallasManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.vo.CampoPantallaConfigurableVO;
import com.rsi.agp.vo.PantallaConfigurableVO;

public class TallerConfiguradorPantallasController extends BaseMultiActionController {
	
	private TallerConfiguradorPantallasManager tallerConfiguradorPantallasManager;
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unused")
	private String successView;
	
	public ModelAndView doArbolComponentes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doArbolComponentes [INIT]");
		String lsIdStr = request.getParameter("lineaseguroid");
		String usoStr = request.getParameter("uso");
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(usoStr)) {
			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
		} else {
			JSONArray jsonArr = this.tallerConfiguradorPantallasManager.getEstructuraCampos(Long.valueOf(lsIdStr),
					new BigDecimal(usoStr));
			result.put("estructuraUso", jsonArr);
			errorMsgs = new String[] {};
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doArbolComponentes [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doControlesPantalla(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doControlesPantalla [INIT]");
		String idPantallaStr = request.getParameter("idPantalla");
		if (StringUtils.isNullOrEmpty(idPantallaStr)) {
			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
		} else {
			PantallaConfigurableVO pantallaConfigurableVO = this.tallerConfiguradorPantallasManager
					.getPantallaConfigurada(Long.valueOf(idPantallaStr));
			result.put("pantallaConfigurableVO", new JSONObject(pantallaConfigurableVO));
			errorMsgs = new String[] {};
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doControlesPantalla [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doAnhadirControlPantalla(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doAnhadirControlPantalla [INIT]");
		String lsIdStr = request.getParameter("lineaseguroid");
		String codConceptoStr = request.getParameter("codConcepto");
		String codUbicacionStr = request.getParameter("codUbicacion");
		String codUsoStr = request.getParameter("codUso");
		String topStr = request.getParameter("top");
		String leftStr = request.getParameter("left");
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(codConceptoStr)
				|| StringUtils.isNullOrEmpty(codUbicacionStr) || StringUtils.isNullOrEmpty(codUsoStr)
				|| StringUtils.isNullOrEmpty(topStr) || StringUtils.isNullOrEmpty(leftStr)) {
			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
		} else {
			CampoPantallaConfigurableVO campoPantallaConfigurableVO = this.tallerConfiguradorPantallasManager
					.getNuevoCampoPantalla(Long.valueOf(lsIdStr), new BigDecimal(codConceptoStr),
							new BigDecimal(codUbicacionStr), new BigDecimal(codUsoStr));
			campoPantallaConfigurableVO.setIdtipo(1);
			campoPantallaConfigurableVO.setAlto(22);
			campoPantallaConfigurableVO.setAncho(150);
			campoPantallaConfigurableVO.setX(Math.round(Float.parseFloat(leftStr)));
			campoPantallaConfigurableVO.setY(Math.round(Float.parseFloat(topStr)));
			campoPantallaConfigurableVO.setMostrar("N");
			campoPantallaConfigurableVO.setMostrarCarga("N");
			campoPantallaConfigurableVO.setDeshabilitado("N");
			campoPantallaConfigurableVO.setValorCargaPac("");
			result.put("campoPantallaConfigurableVO", new JSONObject(campoPantallaConfigurableVO));
			errorMsgs = new String[] {};
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doAnhadirControlPantalla [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doGuardarPantalla(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doGuardarPantalla [INIT]");
		String lsIdStr = request.getParameter("lineaseguroid");
		String idPantallaStr = request.getParameter("idPantalla");
		String jsonStr = request.getParameter("jsonCampos");
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(idPantallaStr)
				|| StringUtils.isNullOrEmpty(jsonStr)) {
			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
		} else {
			JSONArray jsonCamposArr = new JSONArray(jsonStr);
			List<CampoPantallaConfigurableVO> camposPantallaVO = new ArrayList<CampoPantallaConfigurableVO>(
					jsonCamposArr.length());
			for (int i = 0; i < jsonCamposArr.length(); i++) {
				JSONObject jsonCampo = jsonCamposArr.getJSONObject(i);
				CampoPantallaConfigurableVO campoVO = new CampoPantallaConfigurableVO();
				campoVO.setCodConcepto(jsonCampo.getInt("codConcepto"));
				campoVO.setUbicacion_codigo(jsonCampo.getInt("ubicacion_codigo"));
				campoVO.setCodUso(jsonCampo.getInt("codUso"));
				campoVO.setIdorigendedatos(jsonCampo.getInt("idorigendedatos"));
				campoVO.setIdtipo(jsonCampo.getInt("idtipo"));
				campoVO.setX(jsonCampo.getInt("x"));
				campoVO.setY(jsonCampo.getInt("y"));
				campoVO.setEtiqueta(jsonCampo.getString("etiqueta"));
				campoVO.setAlto(jsonCampo.getInt("alto"));
				campoVO.setAncho(jsonCampo.getInt("ancho"));
				campoVO.setMostrar(jsonCampo.getString("mostrar"));
				campoVO.setMostrarCarga(jsonCampo.getString("mostrarCarga"));
				campoVO.setDeshabilitado(jsonCampo.getString("deshabilitado"));
				campoVO.setValorCargaPac(jsonCampo.getString("valorCargaPac"));
				camposPantallaVO.add(campoVO);
			}
			this.tallerConfiguradorPantallasManager.savePantallaconfigurada(Long.valueOf(lsIdStr),
					Long.valueOf(idPantallaStr), camposPantallaVO);
			errorMsgs = new String[] {};
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doGuardarPantalla [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doPreview(HttpServletRequest request, HttpServletResponse response, Object bean)
			throws Exception {
		logger.debug("DatosParcelaController.doPreview [INIT]");
		Map<String, Object> parameters = new HashMap<String, Object>();
		String idPantallaStr = request.getParameter("idPantalla");
		if (StringUtils.isNullOrEmpty(idPantallaStr)) {
			parameters.put("alerta", "No se han recibido todos los datos de entrada.");
		} else {
			String alturaPanelDV = PantallaConfigurable.MAX_ALTURA_PANEL_DV_DEFECTO.toString();
			List<CampoPantallaConfigurableVO> listCampos = this.tallerConfiguradorPantallasManager
					.getListConfigCampos(new BigDecimal(idPantallaStr));
			parameters.put("listaDV", listCampos);
			parameters.put("alturaPanelDV", alturaPanelDV);
		}
		logger.debug("DatosParcelaController.doPreview [END]");
		return new ModelAndView("moduloTaller/pantallasConfigurables/previewConfPantalla").addAllObjects(parameters);
	}
	
	public void setTallerConfiguradorPantallasManager(
			TallerConfiguradorPantallasManager tallerConfiguradorPantallasManager) {
		this.tallerConfiguradorPantallasManager = tallerConfiguradorPantallasManager;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
}
