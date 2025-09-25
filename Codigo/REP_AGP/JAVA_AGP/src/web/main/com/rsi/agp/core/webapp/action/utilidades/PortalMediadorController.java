package com.rsi.agp.core.webapp.action.utilidades;

import java.math.BigDecimal;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.SubentidadMediadoraManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.ConfigAgp;

public class PortalMediadorController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(UtilidadesXMLController.class);

	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private SubentidadMediadoraManager subentidadMediadoraManager;
	private ICommonDao commonDao;

	public String doPortalMediador(final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		String[] errorMsgs;
		JSONObject result = new JSONObject();
		String jwtToken = null;
		String portalMedUrl = "";

		LOGGER.debug("init - PortalMediadorController - doPortalMediador");

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		String[] mediadores = getMediadoresUsuario(usuario);
		String[] colectivos = getColectivosUsuario(usuario);

		jwtToken = WSRUtils.getAccesoMediador(usuario.getCodusuario(), usuario.getNombreusu(), mediadores, null,
				colectivos, isJwtCertificado());

		if (StringUtils.isNullOrEmpty(jwtToken)) {

			errorMsgs = new String[] { "No se ha podido montar la URL de acceso." };
		} else {

			portalMedUrl = bundle.getString("portalMediador.url") + jwtToken + "/RURALE";
			errorMsgs = new String[] {};
		}
		
		LOGGER.debug("portalMedUrl -> " + portalMedUrl);

		result.put("portalMedUrl", portalMedUrl);
		result.put("errorMsgs", new JSONArray(errorMsgs));

		getWriterJSON(response, result);

		LOGGER.debug("end - PortalMediadorController - doPortalMediador");
		return null;
	}

	public String doPortalMediadorPoliza(final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		String[] errorMsgs;
		JSONObject result = new JSONObject();
		String jwtToken = null;
		String portalMedUrl = "";
		
		LOGGER.debug("init - PortalMediadorController - doPortalMediadorPoliza");

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String referencia = request.getParameter("referencia");

		if (StringUtils.isNullOrEmpty(referencia)) {

			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };

		} else {

			String[] mediadores = getMediadoresUsuario(usuario);
			String[] colectivos = getColectivosUsuario(usuario);

			jwtToken = WSRUtils.getAccesoMediador(usuario.getCodusuario(), usuario.getNombreusu(), mediadores,
					new String[] { referencia }, colectivos, isJwtCertificado());

			if (StringUtils.isNullOrEmpty(jwtToken)) {

				errorMsgs = new String[] { "No se ha podido montar la URL de acceso." };
			} else {

				portalMedUrl = bundle.getString("portalMediador.url") + jwtToken + "/RURALE";
				errorMsgs = new String[] {};
			}
		}

		LOGGER.debug("portalMedUrl -> " + portalMedUrl);
		
		result.put("portalMedUrl", portalMedUrl);
		result.put("errorMsgs", new JSONArray(errorMsgs));

		getWriterJSON(response, result);

		LOGGER.debug("end - PortalMediadorController - doPortalMediadorPoliza");
		return null;
	}

	private String[] getColectivosUsuario(final Usuario usuario) throws BusinessException {
		String[] colectivos = null;
		if (Constants.PERFIL_1.equals(usuario.getTipousuario()) && usuario.isUsuarioExterno()) {
			colectivos = this.subentidadMediadoraManager.getColectivosUltPlanes(
					usuario.getSubentidadMediadora().getId().getCodentidad(),
					usuario.getSubentidadMediadora().getId().getCodsubentidad());
		}
		return colectivos;
	}
	
	private String[] getMediadoresUsuario(final Usuario usuario) {
		String[] mediadores = null;
		if (Constants.PERFIL_0.equals(usuario.getTipousuario())) {
			mediadores = new String[] {"3%", "6%", "8%"};
		} else {
			if (!usuario.isUsuarioExterno()) {
				if (Constants.PERFIL_1.equals(usuario.getTipousuario())) {
					mediadores = new String[] {};
					mediadores = (String[]) ArrayUtils.add(mediadores,
							usuario.getSubentidadMediadora().getEntidad().getCodentidad() + "%");
				} else if (Constants.PERFIL_5.equals(usuario.getTipousuario())) {
					mediadores = new String[] {};
					List<BigDecimal> listaCodEntidadesGrupo = usuario.getListaCodEntidadesGrupo();
					if (listaCodEntidadesGrupo != null) {
						for (BigDecimal codEntidad : listaCodEntidadesGrupo) {
							mediadores = (String[]) ArrayUtils.add(mediadores, codEntidad + "%");
						}
					}
				}
			}
		}
		return mediadores;
	}
	
	private boolean isJwtCertificado() {
		ConfigAgp configAgp = (ConfigAgp) this.commonDao.getObject(ConfigAgp.class, "agpNemo", "PM_ACCESO_CERT");
		return configAgp != null && Constants.VALOR_SI.equals(configAgp.getAgpValor());
	}
	
	public void setSubentidadMediadoraManager(final SubentidadMediadoraManager subentidadMediadoraManager) {
		this.subentidadMediadoraManager = subentidadMediadoraManager;
	}

	public void setCommonDao(final ICommonDao commonDao) {
		this.commonDao = commonDao;
	}
}