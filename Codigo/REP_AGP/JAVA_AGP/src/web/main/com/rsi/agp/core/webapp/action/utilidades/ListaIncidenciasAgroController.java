package com.rsi.agp.core.webapp.action.utilidades;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.anexoMod.impresion.ImpresionIncidenciasModManager;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;

public class ListaIncidenciasAgroController extends MultiActionController {

	private static final String FECHA_ENVIO_HASTA_ID = "fechaEnvioHastaId";

	private static final String FECHA_ENVIO_DESDE_ID = "fechaEnvioDesdeId";

	private static final String ASUNTO2 = "asunto";

	private static final String IDCUPON = "idcupon";

	private static final String TIPOREF = "tiporef";

	private static final String CODESTADOAGRO = "codestadoagro";

	private static final String CODESTADO = "codestado";

	private static final String CODLINEA = "codlinea";

	private static final String DELEGACION = "delegacion";

	private static final String SUBENTMEDIADORA = "subentmediadora";

	private static final String ENTMEDIADORA = "entmediadora";

	private static final String OFICINA = "oficina";

	private static final String CODENTIDAD = "codentidad";

	private static final String IDINCIDENCIA = "idincidencia";

	private static final String REFERENCIA = "referencia";

	private static final String NIFCIF = "nifcif";

	private static final String CODPLAN = "codplan";

	private ImpresionIncidenciasModManager impresionIncidenciasModManager;

	private String listaIncidenciasAgroVista;
	@SuppressWarnings("unused")
	private String aportarDocIncidenciaVista;

	private static final Log LOGGER = LogFactory.getLog(ListaIncidenciasAgroController.class);

	public ModelAndView doCargar(HttpServletRequest req, HttpServletResponse res) {

		LOGGER.debug("Accediendo a la vista de busqueda de incidencias de agroseguro");
		Map<String, Object> model = new HashMap<String, Object>();
		try {

			// Modif tam (25.07.2018)
			// model.put("vuelta", this.parametrosVueltaConsListIncidencia(req));
			VistaIncidenciasAgro VistaIncAgro = this.parametrosVueltaConsListIncidencia(req);
			model.put("vuelta", VistaIncAgro);
			model.put("fechaEnvioDesdeStr", VistaIncAgro.getFechaEnvioDesdeStr());
			model.put("fechaEnvioHastaStr", VistaIncAgro.getFechaEnvioHastaStr());

			model.put("cargaPagina", "cargaPagina");
		} catch (Exception e) {
			LOGGER.error(e);
			model.put("alerta", "Error, póngase en contacto con el administrador");
		}
		return new ModelAndView(listaIncidenciasAgroVista, model);
	}

	public ModelAndView doConsultar(HttpServletRequest req, HttpServletResponse res) {
		final Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
		String codUsuario = usuario.getCodusuario();
		Map<String, Object> params = null;
		boolean esBusquedaPorPoliza = StringUtils.equals(req.getParameter("opcionBusqueda"), "p");

		if (esBusquedaPorPoliza) {
			LOGGER.debug("La busqueda de incidencias es plan y póliza");
			params = this.consultaPorPoliza(req, codUsuario);
		} else {
			LOGGER.debug("La busqueda de incidencias es plan, linea y asegurado");
			params = this.consultaPorAsegurado(req, codUsuario);
		}

		// (05.07.2018) Guardamos los datos de la vuelta.
		try {
			// Modif tam (25.07.2018)
			// params.put("vuelta", this.parametrosVueltaConsListIncidencia(req));
			VistaIncidenciasAgro VistaIncAgro = this.parametrosVueltaConsListIncidencia(req);
			params.put("vuelta", VistaIncAgro);
			params.put("fechaEnvioDesdeStr", VistaIncAgro.getFechaEnvioDesdeStr());
			params.put("fechaEnvioHastaStr", VistaIncAgro.getFechaEnvioHastaStr());

			params.put("entidadConsList", req.getParameter("entidadConsulta"));
			params.put(CODPLAN, req.getParameter("planConsulta"));
			// params.put("vuelta", req.getParameter("vuelta"));

		} catch (Exception e) {
			LOGGER.error(e);
		}
		//

		ModelAndView mav = new ModelAndView(listaIncidenciasAgroVista, params);
		return mav;
	}

	private Map<String, Object> consultaPorAsegurado(HttpServletRequest req, String codUsuario) {
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		BigDecimal plan = new BigDecimal(req.getParameter("plan"));
		BigDecimal linea = new BigDecimal(req.getParameter("linea"));
		String nifCif = req.getParameter("asegurado_nifcif");

		logger.debug("CONSULTA INCIDENCIAS POR ASEGURADO");
		logger.debug("Valor de getParameter(plan):" + plan);
		logger.debug("Valor de getParameter(linea):" + linea);
		logger.debug("Valor de getParameter(nifcif):" + nifCif);

		if (nifCif == null) {
			nifCif = req.getParameter(NIFCIF);
		}

		/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */
		/*
		 * Previo a lanzar la llamada a la consulta de la relación de Incidicias se
		 * comprueba si el Asegurado de la póliza se encuentra en estado bloqueado
		 */
		Map<String, Object> datos = new HashMap<String, Object>();
		boolean AseguradoBloqueado = true;
		String referencia = "";

		AseguradoBloqueado = this.impresionIncidenciasModManager.consultaAseguradoBloqueado(plan, linea, referencia,
				nifCif);

		if (!AseguradoBloqueado) {
			datos = this.impresionIncidenciasModManager.solicitarRelacionIncidencias(nifCif, plan, linea, realPath,
					codUsuario);
		}
		/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */

		// MODIF TAM (11.05.2018) ** Inicio //
		datos.put("plan", plan);
		// MODIF TAM (11.05.2018) ** Fin //
		datos.put("planAsegurado", plan);
		datos.put("linea", linea);
		datos.put(NIFCIF, nifCif);
		return datos;
	}

	private Map<String, Object> consultaPorPoliza(HttpServletRequest req, String codUsuario) {
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		logger.debug("Valor de Parameter(poliza_plan)" + (req.getParameter("poliza_plan")));
		BigDecimal plan = new BigDecimal(req.getParameter("poliza_plan"));
		String referencia = req.getParameter("poliza_referencia");

		if (referencia == null) {
			referencia = req.getParameter(REFERENCIA);
		}

		logger.debug("CONSULTA INCIDENCIAS POR POLIZA");
		logger.debug("Valor de getParameter(plan):" + plan);
		logger.debug("Valor de getParameter(referencia):" + referencia);

		/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */
		/*
		 * Previo a lanzar la llamada a la consulta de la relación de Incidicias se
		 * comprueba si el Asegurado de la póliza se encuentra en estado bloqueado
		 */
		Map<String, Object> datos = new HashMap<String, Object>();
		boolean AseguradoBloqueado = true;
		BigDecimal linea = new BigDecimal(0);
		String nifcif = "";

		AseguradoBloqueado = this.impresionIncidenciasModManager.consultaAseguradoBloqueado(plan, linea, referencia,
				nifcif);

		if (!AseguradoBloqueado) {
			datos = this.impresionIncidenciasModManager.solicitarRelacionIncidencias(referencia, plan, realPath,
					codUsuario);
		}
		/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */

		datos.put("planPoliza", plan);
		datos.put(REFERENCIA, referencia);
		return datos;
	}

	public ModelAndView doAportarDocumentacion(HttpServletRequest req, HttpServletResponse res) {
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			AsuntosInc asunto = IncidenciasAgroHelper.obtenerAsuntoVista(req);
			DocsAfectadosInc docAfectados = IncidenciasAgroHelper.obtenerDocAfectadosVista(req);
			Incidencias incidencia = IncidenciasAgroHelper.obtenerIncidenciaVista(req);
			parametros = this.impresionIncidenciasModManager.guardarIncidencia(incidencia, asunto, docAfectados);
			parametros = IncidenciasAgroHelper.agregarParametrosDeVuelta(parametros, req);

			parametros.put(IDINCIDENCIA, req.getParameter(IDINCIDENCIA));
			parametros.put(CODENTIDAD, req.getParameter(CODENTIDAD));
			parametros.put(OFICINA, req.getParameter(OFICINA));
			parametros.put(REFERENCIA, req.getParameter(REFERENCIA));
			parametros.put(ENTMEDIADORA, req.getParameter(ENTMEDIADORA));
			parametros.put(SUBENTMEDIADORA, req.getParameter(SUBENTMEDIADORA));
			parametros.put(DELEGACION, req.getParameter(DELEGACION));
			parametros.put(CODPLAN, req.getParameter(CODPLAN));
			parametros.put(CODLINEA, req.getParameter(CODLINEA));
			parametros.put(CODESTADO, req.getParameter(CODESTADO));
			parametros.put(CODESTADOAGRO, req.getParameter(CODESTADOAGRO));
			parametros.put(NIFCIF, req.getParameter(NIFCIF));
			parametros.put(TIPOREF, req.getParameter(TIPOREF));
			parametros.put(IDCUPON, req.getParameter(IDCUPON));
			parametros.put(ASUNTO2, req.getParameter(ASUNTO2));
			parametros.put(FECHA_ENVIO_DESDE_ID, req.getParameter(FECHA_ENVIO_DESDE_ID));
			parametros.put(FECHA_ENVIO_HASTA_ID, req.getParameter(FECHA_ENVIO_HASTA_ID));

			parametros.put("method", "doCargar");
			parametros.put("origenEnvio", "agroseguro");
			mv = new ModelAndView("redirect:/aportarDocIncidencia.run").addAllObjects(parametros);
		} catch (BusinessException e) {
			LOGGER.error("Avisamos al usuario del error");
			mv = this.doConsultar(req, res);
		}

		return mv;
	}

	private VistaIncidenciasAgro parametrosVueltaConsListIncidencia(HttpServletRequest req) throws ParseException {
		VistaIncidenciasAgro vista = new VistaIncidenciasAgro();
		String idIncidencia = req.getParameter(IDINCIDENCIA);
		String codEntidad = req.getParameter(CODENTIDAD);
		String oficina = req.getParameter(OFICINA);
		String referencia = req.getParameter(REFERENCIA);
		String entMediadora = req.getParameter(ENTMEDIADORA);
		String subentMediadora = req.getParameter(SUBENTMEDIADORA);
		String delegacion = req.getParameter(DELEGACION);
		String codPlan = req.getParameter(CODPLAN);
		String codLinea = req.getParameter(CODLINEA);
		String codEstado = req.getParameter(CODESTADO);
		String codEstadoAgro = req.getParameter(CODESTADOAGRO);
		String nifCif = req.getParameter(NIFCIF);
		String tipoReferencia = req.getParameter(TIPOREF);
		String idCupon = req.getParameter(IDCUPON);
		String asunto = req.getParameter(ASUNTO2);
		String fechaEnvioDesde = req.getParameter(FECHA_ENVIO_DESDE_ID);
		String fechaEnvioHasta = req.getParameter(FECHA_ENVIO_HASTA_ID);

		String numIncidencia = req.getParameter("numero");
		String codUsuario = req.getParameter("codUsuarioVolver");

		if (StringUtils.isNotBlank(idIncidencia)) {
			vista.setIdincidencia(Long.parseLong(idIncidencia));
		}
		if (StringUtils.isNotBlank(codEntidad)) {
			vista.setCodentidad(new BigDecimal(codEntidad));
		}
		if (StringUtils.isNotBlank(oficina)) {
			vista.setOficina(oficina);
		}
		if (StringUtils.isNotBlank(entMediadora)) {
			vista.setEntmediadora(new BigDecimal(entMediadora));
		}
		if (StringUtils.isNotBlank(subentMediadora)) {
			vista.setSubentmediadora(new BigDecimal(subentMediadora));
		}
		if (StringUtils.isNotBlank(delegacion)) {
			vista.setDelegacion(new BigDecimal(delegacion));
		}
		if (StringUtils.isNotBlank(codPlan)) {
			vista.setCodplan(new BigDecimal(codPlan));
		}
		if (StringUtils.isNotBlank(codLinea)) {
			vista.setCodlinea(new BigDecimal(codLinea));
		}
		if (StringUtils.isNotBlank(codEstado)) {
			vista.setCodestado(new BigDecimal(codEstado));
		}
		if (StringUtils.isNotBlank(codEstadoAgro)) {
			vista.setCodestadoagro(codEstadoAgro.charAt(0));
		}
		if (StringUtils.isNotBlank(nifCif)) {
			vista.setNifcif(nifCif);
		}
		if (StringUtils.isNotBlank(tipoReferencia)) {
			vista.setTiporef(tipoReferencia.charAt(0));
		}
		if (StringUtils.isNotBlank(idCupon)) {
			vista.setIdcupon(idCupon);
		}
		if (StringUtils.isNotBlank(asunto)) {
			vista.setAsunto(asunto);
		}

		if (StringUtils.isNotBlank(referencia)) {
			vista.setReferencia(referencia);
		}

		if (StringUtils.isNotBlank(fechaEnvioDesde)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			vista.setFechaEnvioDesde(sdf.parse(fechaEnvioDesde));
		}
		if (StringUtils.isNotBlank(fechaEnvioHasta)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			vista.setFechaEnvioHasta(sdf.parse(fechaEnvioHasta));
		}

		if (StringUtils.isNotBlank(numIncidencia)) {
			vista.setNumero(new BigDecimal(numIncidencia));
		}
		if (StringUtils.isNotBlank(codUsuario)) {
			vista.setCodusuario(codUsuario);
		}

		return vista;
	}

	public void setImpresionIncidenciasModManager(ImpresionIncidenciasModManager impresionIncidenciasModManager) {
		this.impresionIncidenciasModManager = impresionIncidenciasModManager;
	}

	public void setListaIncidenciasAgroVista(String listaIncidenciasAgroVista) {
		this.listaIncidenciasAgroVista = listaIncidenciasAgroVista;
	}

	public void setAportarDocIncidenciaVista(String aportarDocIncidenciaVista) {
		this.aportarDocIncidenciaVista = aportarDocIncidenciaVista;
	}
}
