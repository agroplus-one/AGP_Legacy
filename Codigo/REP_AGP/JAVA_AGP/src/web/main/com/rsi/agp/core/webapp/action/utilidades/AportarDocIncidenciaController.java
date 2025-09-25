package com.rsi.agp.core.webapp.action.utilidades;

import java.math.BigDecimal;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.utilidades.AportarDocIncidenciaManager;
import com.rsi.agp.core.webapp.action.anexoMod.ImpresionIncidenciasModController;
import com.rsi.agp.dao.models.inc.IAportarDocIncidenciaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.DocumentacionIncForm;
import com.rsi.agp.dao.tables.inc.DocumentosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.TiposDocInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error;

public class AportarDocIncidenciaController extends MultiActionController {

	private static final String ORIGEN_BD = "baseDatos";
	private static final String ORIGEN_AGRO = "agroseguro";
	private static final String ENVIO_POLIZA = "p";
	// private static final String ENVIO_INCIDENCIA = "i";
	private static final String ENVIO_ANEXO = "am";
	private static final String ENVIO_ASEGURADO = "aseg";
	private static final String TIPO_ENV_ASEG = "a";
	private static final String TIPO_ENV_CUP = "c";
	
	private static final String ORIGEN = "origen";
	private static final String EDITAR_INC = "editarInc";
	private static final String INCIDENCIAS_AGRO = "incidenciasAgro";
	private static final String FECHA_ENVIO_DESDE = "fechaEnvioDesdeStr";
	private static final String FECHA_ENVIO_HASTA = "fechaEnvioHastaStr";
	private static final String VUELTA = "vuelta";
	private static final String LISTA_ASUNTOS = "listaAsuntos";
	private static final String ORIGEN_ENVIO = "origenEnvio";
	private static final String EXTENSIONES = "extensiones";
	private static final String TIPO_BUSQUEDA = "tipoBusqueda";
	private static final String REFERENCIA = "referencia";
	private static final String LINEA = "linea";
	private static final String NIF_CIF = "nifcif";
	private static final String IMPRESION_INCIDENCIAS = "impresionIncidencias";
	private static final String REFERENCIA_VOLVER = "referenciaVolver";
	private static final String LINEA_VOLVER = "lineaVolver";
	private static final String FECHA_ENV_VOLVER = "fechaEnvVolver";
	private static final String NOM_LINEA_VOLER = "nomLineaVolver";
	private static final String ID_POLIZA_VOLVER = "idPolizaVolver";
	private static final String NOMBRE_COMPLETO = "nombreCompleto";
	private static final String MODULO_VOLVER = "moduloVolver";
	private static final String COD_PLAN_VOLVER = "codPlanVolver";
	private static final String CONSULTA = "consulta";
	private static final String ID_INC = "idInc";
	private static final String TIPO_ENVIO = "tipoEnvio";
	private static final String ASUNTO_INC = "asuntoInc";
	private static final String DOCUMENTOS = "documentos";
	private static final String ALERTA = "alerta";
	private static final String ERROR_CONTACTO = "Error, póngase en contacto con el administrador";
	private static final String DOC_INC_FORM = "docIncForm";
	private static final String OPCION_BUSQUEDA = "opcionBusqueda";
	private static final String COD_ASUNTO = "codasunto";
	private static final String TIPO_ENVIO_BOR = "tipoEnvioBor";

	private static final Log LOGGER = LogFactory.getLog(AportarDocIncidenciaController.class);

	private AportarDocIncidenciaManager aportarDocIncidenciaManager;
	private String aportarDocIncidenciaVista;
	private IncidenciasAgroUtilidadesController incidenciasUtilidadesController;
	private ImpresionIncidenciasModController impresionIncidenciasModController;

	private IAportarDocIncidenciaDao aportarDocIncidenciaDao;

	public ModelAndView doCargar(HttpServletRequest req, HttpServletResponse res) {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		DocumentacionIncForm docIncForm = new DocumentacionIncForm();
		try {
			String idIncString = req.getParameter("incidenciaId");
			if (req.getParameter(ORIGEN).equals(EDITAR_INC)
					|| req.getParameter(ORIGEN).equals(INCIDENCIAS_AGRO)) {
				VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
				this.parametrosVueltaConsultaIncidencia(req, vistaIncAgro);

				parametros.put(FECHA_ENVIO_DESDE, vistaIncAgro.getFechaEnvioDesdeStr());
				parametros.put(FECHA_ENVIO_HASTA, vistaIncAgro.getFechaEnvioHastaStr());
				parametros.put(VUELTA, vistaIncAgro);
			}
			parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
			parametros.put(ORIGEN_ENVIO, req.getParameter(ORIGEN_ENVIO));
			parametros.put(EXTENSIONES, this.listaExtensionesPermitidas());
			// Pet. 50775 ** MODIF TAM (06.06.2018) ** Inicio //
			parametros.put(ORIGEN, req.getParameter(ORIGEN));
			parametros.put(TIPO_BUSQUEDA, req.getParameter(TIPO_BUSQUEDA));
			parametros.put("plan", req.getParameter("plan"));
			if (req.getParameter(ORIGEN).equals(INCIDENCIAS_AGRO)) {
				parametros.put(REFERENCIA, req.getParameter("referencia_pol"));
			} else {
				parametros.put(REFERENCIA, req.getParameter(REFERENCIA));
			}

			parametros.put(LINEA, req.getParameter(LINEA));
			parametros.put(NIF_CIF, req.getParameter(NIF_CIF));
			// Pet. 50775 ** MODIF TAM (06.06.2018) ** Fin //

			if (req.getParameter(ORIGEN).equals(IMPRESION_INCIDENCIAS)) {
				parametros.put(REFERENCIA_VOLVER, req.getParameter(REFERENCIA_VOLVER));
				parametros.put(LINEA_VOLVER, req.getParameter(LINEA_VOLVER));
				parametros.put(FECHA_ENV_VOLVER, req.getParameter(FECHA_ENV_VOLVER));
				parametros.put(NOM_LINEA_VOLER, req.getParameter(NOM_LINEA_VOLER));
				parametros.put(ID_POLIZA_VOLVER, req.getParameter(ID_POLIZA_VOLVER));
				parametros.put(NOMBRE_COMPLETO, req.getParameter("nombreAseVolver"));
				parametros.put(MODULO_VOLVER, req.getParameter(MODULO_VOLVER));
				parametros.put("tipoincVolver", req.getParameter("tipoincVolver"));

				Poliza poliza = null;
				Long idpoliza = null;

				idpoliza = new Long(req.getParameter(ID_POLIZA_VOLVER));

				poliza = aportarDocIncidenciaDao.getPolizaById(idpoliza);
				parametros.put(FECHA_ENV_VOLVER, poliza.getFechaenvio());
				parametros.put(COD_PLAN_VOLVER, poliza.getLinea().getCodplan());
			}

			parametros.put(CONSULTA, false);
			if (idIncString != null) {
				long idIncidencia = Long.parseLong(idIncString);
				parametros.put(ID_INC, idIncidencia);
				docIncForm = this.aportarDocIncidenciaManager.generarDocumentacionIncForm(idIncidencia);
				// MODIF TAM (12.06.2018): Recuperamos el valor insertado en la incidencia
				String tipoAlta = docIncForm.getIncidencias().getTipoalta();
				if (tipoAlta == null || tipoAlta.equals("null")) {
					tipoAlta = "";
				} else {
					String tipoEnvio = "";
					if (tipoAlta.equals(TIPO_ENV_ASEG)) {
						tipoEnvio = ENVIO_ASEGURADO;
					} else if (tipoAlta.equals(TIPO_ENV_CUP)) {
						tipoEnvio = ENVIO_ANEXO;
					} else {
						tipoEnvio = tipoAlta;
					}
					parametros.put(TIPO_ENVIO, tipoEnvio);
				}

				// MODIF TAM (15.06.2018) - Enviar todos los datos si estamos Editando una
				// incidenica//
				String origen = req.getParameter(ORIGEN);
				if (origen.equals(EDITAR_INC)) {

					parametros.put(TIPO_BUSQUEDA, parametros.get(TIPO_ENVIO));
					parametros.put("plan", docIncForm.getIncidencias().getCodplan());
					parametros.put(REFERENCIA, docIncForm.getIncidencias().getReferencia());
					parametros.put(LINEA, docIncForm.getIncidencias().getCodlinea());
					parametros.put(NIF_CIF, docIncForm.getIncidencias().getNifaseg());
					parametros.put("anhoincidencia", docIncForm.getIncidencias().getAnhoincidencia());
					parametros.put("numincidencia", docIncForm.getIncidencias().getNumincidencia());
					parametros.put("idenvio", docIncForm.getIncidencias().getIdenvio());
					// Pet. 50775 ** MODIF TAM (06.06.2018) ** Fin //
				}

				String nomlinea = this.aportarDocIncidenciaDao.getNombLinea(docIncForm.getIncidencias().getCodlinea());
				parametros.put("nomLinea", nomlinea);

				// 18.07.2018 ** Inicio //
				if (origen.equals(EDITAR_INC)) {
					String codAsunto = docIncForm.getIncidencias().getAsuntosInc().getId().getCodasunto();

					AsuntosInc asunto = (AsuntosInc) this.aportarDocIncidenciaManager.getAsunto(codAsunto);
					parametros.put(ASUNTO_INC, asunto);
				}
				// 18.07.2018 ** Fin //

				parametros.put(DOCUMENTOS, this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia));
			}
		} catch (Exception e) {
			LOGGER.error(e);
			parametros.put(ALERTA, ERROR_CONTACTO);
		}

		VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
		this.parametrosVueltaConsultaIncidencia(req, vistaIncAgro);
		parametros.put(VUELTA, vistaIncAgro);
		parametros.put(FECHA_ENVIO_DESDE, vistaIncAgro.getFechaEnvioDesdeStr());
		parametros.put(FECHA_ENVIO_HASTA, vistaIncAgro.getFechaEnvioHastaStr());

		return new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
	}

	private String listaExtensionesPermitidas() throws BusinessException {
		List<TiposDocInc> tiposDocIncs = this.aportarDocIncidenciaManager.obtenerExtensionesValidas();
		StringBuilder sb = new StringBuilder();
		ListIterator<TiposDocInc> iterator = tiposDocIncs.listIterator();
		while (iterator.hasNext()) {
			TiposDocInc tipo = iterator.next();
			sb.append(tipo.getExtension());
			if (iterator.hasNext()) {
				sb.append("|");
			}
		}
		return sb.toString();
	}

	public ModelAndView doConsultar(HttpServletRequest req, HttpServletResponse res) {
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		DocumentacionIncForm docIncForm = new DocumentacionIncForm();
		try {
			long idIncidencia = Long.parseLong(req.getParameter("idincidencia"));

			// String codAsunto = form.getIncidencias().getAsuntosInc().getCodasunto();
			// AsuntosInc asunto =
			// (AsuntosInc)this.aportarDocIncidenciaDao.get(AsuntosInc.class, codAsunto);
			// incidencia.setAsuntosInc(asunto);
			//
			docIncForm = this.aportarDocIncidenciaManager.generarDocumentacionIncForm(idIncidencia);
			// String codAsunto =
			// docIncForm.getIncidencias().getAsuntosInc().getCodasunto();
			String descAsunto = docIncForm.getIncidencias().getAsuntosInc().getDescripcion();

			// AsuntosInc asunto =
			// (AsuntosInc)this.aportarDocIncidenciaDao.get(AsuntosInc.class, codAsunto);

			// parametros.put("asuntoInc.codasunto", codAsunto);
			// parametros.put("asuntoInc.descripcion", descAsunto);
			// parametros.put("asunto.codasunto", codAsunto);
			docIncForm.getIncidencias().getAsuntosInc().setDescripcion(descAsunto);
			parametros.put("docIncForm.incidencias.asuntosInc.descripcion", descAsunto);
			parametros.put("descAsunto", descAsunto);

			// Modif tam (24.07.2018)
			VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
			this.parametrosVueltaConsultaIncidencia(req, vistaIncAgro);

			parametros.put(VUELTA, vistaIncAgro);
			parametros.put(FECHA_ENVIO_DESDE, vistaIncAgro.getFechaEnvioDesdeStr());
			parametros.put(FECHA_ENVIO_HASTA, vistaIncAgro.getFechaEnvioHastaStr());

			parametros.put(DOCUMENTOS, this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia));
			parametros.put(ORIGEN_ENVIO, req.getParameter(ORIGEN_ENVIO));
			// Pet. 50775 ** MODIF TAM (06.06.2018) ** Inicio //
			parametros.put(ORIGEN, req.getParameter(ORIGEN));

			// Cargamos el tipoalta con el que se dió de alta la incidencia.
			String tipoAlta = docIncForm.getIncidencias().getTipoalta();
			if (tipoAlta == null || tipoAlta.equals("null")) {
				tipoAlta = "";
			} else {
				if (tipoAlta.equals(TIPO_ENV_ASEG)) {
					parametros.put(TIPO_ENVIO, ENVIO_ASEGURADO);
					parametros.put(TIPO_BUSQUEDA, ENVIO_ASEGURADO);
				} else if (tipoAlta.equals(TIPO_ENV_CUP)) {
					parametros.put(TIPO_ENVIO, ENVIO_ANEXO);
					parametros.put(TIPO_BUSQUEDA, ENVIO_ANEXO);
				} else {
					parametros.put(TIPO_ENVIO, tipoAlta);
					parametros.put(TIPO_BUSQUEDA, tipoAlta);
				}
			}

			// Pet. 50775 ** MODIF TAM (06.06.2018) ** Fin //
			parametros.put(CONSULTA, true);
		} catch (Exception e) {
			LOGGER.error(e);
			parametros.put(ALERTA, ERROR_CONTACTO);
		}
		return new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
	}

	private boolean parametrosVueltaConsultaIncidencia(final HttpServletRequest req, final VistaIncidenciasAgro vista) {
		boolean hasFilters = false;
		String idIncidencia = req.getParameter("idincidencia");
		String codEntidad = req.getParameter("codentidad");
		String oficina = req.getParameter("oficina");
		String referencia = req.getParameter(REFERENCIA);
		String entMediadora = req.getParameter("entmediadora");
		String subentMediadora = req.getParameter("subentmediadora");
		String delegacion = req.getParameter("delegacion");
		String codPlan = req.getParameter("codplan");
		String codLinea = req.getParameter("codlinea");
		String codEstado = req.getParameter("codestado");
		String codEstadoAgro = req.getParameter("codestadoagro");
		String nifCif = req.getParameter(NIF_CIF);
		String tipoReferencia = req.getParameter("tiporef");
		String idCupon = req.getParameter("idcupon");
		String asunto = req.getParameter("asunto");
		String fechaEnvioDesde = req.getParameter("fechaEnvioDesdeId");
		String fechaEnvioHasta = req.getParameter("fechaEnvioHastaId");
		String numIncidencia = req.getParameter("numIncidencia");
		String codUsuario = req.getParameter("codusuario");
		String tipoinc = req.getParameter("tipoinc");

		String origen_aux = req.getParameter(ORIGEN);
		if (origen_aux == null || (origen_aux != null && "null".equals(origen_aux))) {
			origen_aux = "";
		}

		if ("consultaInc".equals(origen_aux) || "VolvConsLisInc".equals(origen_aux)) {
			referencia = req.getParameter(REFERENCIA);
			numIncidencia = req.getParameter("numero");
			nifCif = req.getParameter(NIF_CIF);
		}

		if (StringUtils.isNotBlank(idIncidencia)) {
			hasFilters = true;
			vista.setIdincidencia(Long.parseLong(idIncidencia));
		}
		if (StringUtils.isNotBlank(codEntidad)) {
			hasFilters = true;
			vista.setCodentidad(new BigDecimal(codEntidad));
		}
		if (StringUtils.isNotBlank(oficina)) {
			hasFilters = true;
			vista.setOficina(oficina);
		}
		if (StringUtils.isNotBlank(entMediadora)) {
			hasFilters = true;
			vista.setEntmediadora(new BigDecimal(entMediadora));
		}
		if (StringUtils.isNotBlank(subentMediadora)) {
			hasFilters = true;
			vista.setSubentmediadora(new BigDecimal(subentMediadora));
		}
		if (StringUtils.isNotBlank(delegacion)) {
			hasFilters = true;
			vista.setDelegacion(new BigDecimal(delegacion));
		}
		if (StringUtils.isNotBlank(codPlan)) {
			hasFilters = true;
			vista.setCodplan(new BigDecimal(codPlan));
		}
		if (StringUtils.isNotBlank(codLinea)) {
			hasFilters = true;
			vista.setCodlinea(new BigDecimal(codLinea));
		}
		if (StringUtils.isNotBlank(codEstado)) {
			hasFilters = true;
			vista.setCodestado(new BigDecimal(codEstado));
		}
		if (StringUtils.isNotBlank(codEstadoAgro)) {
			hasFilters = true;
			vista.setCodestadoagro(codEstadoAgro.charAt(0));
		}
		if (StringUtils.isNotBlank(nifCif)) {
			hasFilters = true;
			vista.setNifcif(nifCif);
		}
		if (StringUtils.isNotBlank(tipoReferencia)) {
			hasFilters = true;
			vista.setTiporef(tipoReferencia.charAt(0));
		}
		if (StringUtils.isNotBlank(idCupon)) {
			hasFilters = true;
			vista.setIdcupon(idCupon);
		}
		if (StringUtils.isNotBlank(asunto)) {
			hasFilters = true;
			vista.setAsunto(asunto);
			vista.setCodasunto(asunto);
		}

		if (StringUtils.isNotBlank(referencia)) {
			hasFilters = true;
			vista.setReferencia(referencia);
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (StringUtils.isNotBlank(fechaEnvioDesde)) {
				hasFilters = true;
				vista.setFechaEnvioDesde(sdf.parse(fechaEnvioDesde));
			}
			if (StringUtils.isNotBlank(fechaEnvioHasta)) {
				hasFilters = true;
				vista.setFechaEnvioHasta(sdf.parse(fechaEnvioHasta));
			}
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		if (StringUtils.isNotBlank(numIncidencia)) {
			hasFilters = true;
			vista.setNumero(new BigDecimal(numIncidencia));
		}
		if (StringUtils.isNotBlank(codUsuario)) {
			hasFilters = true;
			vista.setCodusuario(codUsuario);
		}

		if (StringUtils.isNotBlank(tipoinc)) {
			hasFilters = true;
			vista.setTipoinc(tipoinc.charAt(0));
		}

		return hasFilters;
	}

	public ModelAndView doAgregarDoc(HttpServletRequest req, HttpServletResponse res, DocumentacionIncForm docIncForm) {
		ModelAndView mv = null;
		LOGGER.debug("**AportarDocIncidenciaController-doAgregarDoc");
		Map<String, Object> parametros = new HashMap<String, Object>();
		String tipoEnvio = req.getParameter(TIPO_ENVIO);
		String origenEnvio = req.getParameter(ORIGEN_ENVIO);

		parametros.put(ORIGEN_ENVIO, origenEnvio);
		parametros.put(TIPO_ENVIO, tipoEnvio);
		parametros.put(EXTENSIONES, req.getParameter(EXTENSIONES));
		parametros.put(CONSULTA, false);
		// Pet. 50775 ** MODIF TAM (06.06.2018) ** Inicio //
		parametros.put(ORIGEN, req.getParameter(ORIGEN));
		parametros.put(TIPO_BUSQUEDA, req.getParameter(OPCION_BUSQUEDA));
		if (req.getParameter(OPCION_BUSQUEDA) == "") {
			parametros.put(TIPO_BUSQUEDA, tipoEnvio);
		} else {
			if (tipoEnvio == "" || tipoEnvio == null) {
				tipoEnvio = req.getParameter(OPCION_BUSQUEDA);
				parametros.put(TIPO_ENVIO, tipoEnvio);
			}
		}
		parametros.put("plan", req.getParameter("plan"));
		parametros.put(REFERENCIA, req.getParameter(REFERENCIA));
		parametros.put(LINEA, req.getParameter(LINEA));
		parametros.put(NIF_CIF, req.getParameter(NIF_CIF));

		if (req.getParameter(ORIGEN).equals(INCIDENCIAS_AGRO)) {
			parametros.put("plan", req.getParameter("plan_aseg"));
			parametros.put(LINEA, req.getParameter("linea_aseg"));

		}

		// (06.07.2018)
		if (req.getParameter(ORIGEN).equals(IMPRESION_INCIDENCIAS)) {
			// Para no perder los datos y poder volver después de haber agregado un
			// documento
			parametros.put(LINEA_VOLVER, req.getParameter(LINEA_VOLVER));
			parametros.put(REFERENCIA_VOLVER, req.getParameter(REFERENCIA_VOLVER));
			parametros.put(FECHA_ENV_VOLVER, req.getParameter(FECHA_ENV_VOLVER));
			parametros.put(NOM_LINEA_VOLER, req.getParameter(NOM_LINEA_VOLER));
			parametros.put(ID_POLIZA_VOLVER, req.getParameter(ID_POLIZA_VOLVER));
			parametros.put(NOMBRE_COMPLETO, req.getParameter(NOMBRE_COMPLETO));
			parametros.put(MODULO_VOLVER, req.getParameter(MODULO_VOLVER));
			parametros.put(COD_PLAN_VOLVER, req.getParameter(COD_PLAN_VOLVER));

		}
		// TAM (14.06.2018) ** Fin //

		// MODIF TAM (20.07.2018)
		// Modif tam (24.07.2018)
		VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
		this.parametrosVueltaConsultaIncidencia(req, vistaIncAgro);
		parametros.put(VUELTA, vistaIncAgro);
		parametros.put(FECHA_ENVIO_DESDE, vistaIncAgro.getFechaEnvioDesdeStr());
		parametros.put(FECHA_ENVIO_HASTA, vistaIncAgro.getFechaEnvioHastaStr());

		/* 11.06.2018 */
		if (ENVIO_ASEGURADO.equals(tipoEnvio)) {
			docIncForm.getIncidencias().setTipoalta(TIPO_ENV_ASEG);
		} else if (ENVIO_ANEXO.equals(tipoEnvio)) {
			docIncForm.getIncidencias().setTipoalta(TIPO_ENV_CUP);
		} else {
			docIncForm.getIncidencias().setTipoalta(tipoEnvio);
		}

		/** Resolución Incidencia RGA V.09 (14.09.2018) ** Inicio **/
		/**
		 * Incluimos una validación de la poliza y del plan para comprobar antes de
		 * hacer nada que /** existe esa póliza para dicho plan.
		 */
		if (ENVIO_POLIZA.equals(tipoEnvio) && (req.getParameter(ORIGEN).equals("altaInc"))) {
			try {

				String referencia = docIncForm.getIncidencias().getReferencia();
				Character tipoReferencia = docIncForm.getIncidencias().getTiporef();
				BigDecimal codPlan = docIncForm.getIncidencias().getCodplan();

				if (referencia != null) {

					/* obtenemos la línea de la poliza por referencia, plan y tipoReferencia */
					BigDecimal codLinea = this.aportarDocIncidenciaDao.getLineaPoliza(referencia, codPlan,
							tipoReferencia);

					/*
					 * obtenemos el id de la poliza, si no recupera nada es que no existe para esa
					 * línea, plan, tipoReferencia
					 */
					BigDecimal idPoliza = this.aportarDocIncidenciaDao.getIdPoliza(referencia, tipoReferencia, codPlan,
							codLinea);

					if (idPoliza == null) {
						parametros.put(ALERTA, "No existe ninguna póliza contratada para los parámetros enviados.");
						AsuntosInc asunto = this.obtenerAsuntoRequest(req);
						docIncForm.getIncidencias().setAsuntosInc(asunto);
						parametros.put(COD_ASUNTO, asunto.getId().getCodasunto());
						parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
						parametros.put(ASUNTO_INC, this.aportarDocIncidenciaManager.getAsunto(asunto.getId().getCodasunto()));

						mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm)
								.addAllObjects(parametros);
						/* No queremos que continue si la validación no ha sido correcta */
						return mv;
					} else {
						/*
						 * Si la póliza Existe comprobamos si el asegurado de dicha póliza está
						 * bloqueado
						 */
						/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */
						/*
						 * Previo a lanzar la llamada a la consulta de la relación de Incidicias se
						 * comprueba si el Asegurado de la póliza se encuentra en estado bloqueado
						 */
						boolean AseguradoBloqueado = true;
						String nifCif = "";
						AseguradoBloqueado = this.aportarDocIncidenciaManager.consultaAseguradoBloqueado(codPlan,
								codLinea, referencia, nifCif);

						if (AseguradoBloqueado) {
							parametros.put(ALERTA, "El Asegurado de la póliza se encuentra en estado Bloqueado.");
							AsuntosInc asunto = this.obtenerAsuntoRequest(req);
							docIncForm.getIncidencias().setAsuntosInc(asunto);
							parametros.put(COD_ASUNTO, asunto.getId().getCodasunto());
							parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
							parametros.put(ASUNTO_INC,
									this.aportarDocIncidenciaManager.getAsunto(asunto.getId().getCodasunto()));

							mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm)
									.addAllObjects(parametros);
							/* No queremos que continue si la validación no ha sido correcta */
							return mv;
						}
						/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
				parametros.put(ALERTA, "No existe ninguna póliza contratada para los parámetros enviados.");
				mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
				/* No queremos que continue si la validación no ha sido correcta */
				return mv;
			}
		} else {
			if (ENVIO_ASEGURADO.equals(tipoEnvio)) {

				/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */
				/*
				 * Previo a lanzar la llamada a la consulta de la relación de Incidicias se
				 * comprueba si el Asegurado de la póliza se encuentra en estado bloqueado
				 */
				try {
					boolean AseguradoBloqueado = true;
					String nifCif = docIncForm.getIncidencias().getNifaseg();
					String referencia = "";
					BigDecimal codPlan = new BigDecimal(0);
					BigDecimal codLinea = new BigDecimal(0);

					AseguradoBloqueado = this.aportarDocIncidenciaManager.consultaAseguradoBloqueado(codPlan, codLinea,
							referencia, nifCif);

					if (AseguradoBloqueado) {
						parametros.put(ALERTA, "El Asegurado informado se encuentra en estado Bloqueado.");
						AsuntosInc asunto = this.obtenerAsuntoRequest(req);
						docIncForm.getIncidencias().setAsuntosInc(asunto);
						parametros.put(COD_ASUNTO, asunto.getId().getCodasunto());
						parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
						parametros.put(ASUNTO_INC, this.aportarDocIncidenciaManager.getAsunto(asunto.getId().getCodasunto()));

						mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm)
								.addAllObjects(parametros);
						/* No queremos que continue si la validación no ha sido correcta */
						return mv;
					}
				} catch (Exception e) {
					logger.error("Excepcion : AportarDocIncidenciaController - doAgregarDoc", e);
					parametros.put(ALERTA, ERROR_CONTACTO);
					mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm)
							.addAllObjects(parametros);
					/* No queremos que continue si la validación no ha sido correcta */
					return mv;
				}
				/* Pet. 62719 ** MODIF TAM (27.01.2021) ** Inicio) */
			}
		}

		/** Resolución Incidencia RGA V.09 (14.09.2018) ** Inicio **/

		// Pet. 50775 ** MODIF TAM (06.06.2018) ** Fin //
		try {
			docIncForm.setFile(this.obtenerArchivoRequest(req));
			AsuntosInc asunto = this.obtenerAsuntoRequest(req);
			docIncForm.getIncidencias().setAsuntosInc(asunto);
			parametros.put(COD_ASUNTO, asunto.getId().getCodasunto());
			parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
			parametros.put(ASUNTO_INC, this.aportarDocIncidenciaManager.getAsunto(asunto.getId().getCodasunto()));

			Long idIncidencia = this.aportarDocIncidenciaManager.guardarDocumento(docIncForm);
			parametros.put(DOCUMENTOS, this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia));
			parametros.put(ID_INC, idIncidencia);

			DocumentacionIncForm nuevoDocIncForm = this.modificarCamposCamposFormulario(docIncForm, tipoEnvio, origenEnvio);
			// TAM (14.06.2018) ** Obtenemos los datos de la incidencia de la cabecera//
			if (req.getParameter(ORIGEN).equals(EDITAR_INC)) {

				Incidencias incidencia = new Incidencias();
				incidencia.setIdincidencia(idIncidencia);

				int i = this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).size();

				if (i >= 1) {
					incidencia.setCodplan(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getCodplan());
					incidencia.setReferencia(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getReferencia());
					incidencia.setTiporef(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getTiporef());
					incidencia.setNifaseg(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getNifaseg());
					incidencia.setCodlinea(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getCodlinea());
					incidencia.setAnhoincidencia(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia)
							.get(i - 1).getIncidencias().getAnhoincidencia());
					incidencia.setTipoalta(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getTipoalta());
					incidencia.setNumincidencia(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia)
							.get(i - 1).getIncidencias().getNumincidencia());
					incidencia.setIdenvio(this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia).get(i - 1)
							.getIncidencias().getIdenvio());
				}

				nuevoDocIncForm.setIncidencias(incidencia);
			}

			// TAM (14.06.2018) ** Fin //
			try {
				BigDecimal codlinea = nuevoDocIncForm.getIncidencias().getCodlinea();
				if (codlinea != null) {
					String nomlinea = this.aportarDocIncidenciaDao.getNombLinea(codlinea);
					parametros.put("nomLinea", nomlinea);
				}
			} catch (Exception e) {
				LOGGER.error(e);
				parametros.put(ALERTA, "Error al recuperar línea, póngase en contacto con el administrador");
			}

			parametros.put("mensaje", "Documento agregado correctamente");
			mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, nuevoDocIncForm).addAllObjects(parametros);
		} catch (Exception e) {
			logger.error("Excepcion : AportarDocIncidenciaController - doAgregarDoc", e);
			parametros.put(ALERTA, ERROR_CONTACTO);
			mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
		}
		return mv;
	}

	private DocumentacionIncForm modificarCamposCamposFormulario(DocumentacionIncForm viejo, String tipoEnvio,
			String origenEnvio) {
		DocumentacionIncForm nuevo = new DocumentacionIncForm();
		Incidencias incidencia = new Incidencias();
		incidencia.setIdincidencia(viejo.getIncidencias().getIdincidencia());

		// MODIF TAM (11.06.2018)//
		incidencia.setTipoalta(viejo.getIncidencias().getTipoalta());
		incidencia.setObservaciones(viejo.getIncidencias().getObservaciones());
		if (ORIGEN_AGRO.equals(origenEnvio)) {
			if (ENVIO_POLIZA.equals(tipoEnvio)) {
				incidencia.setReferencia(viejo.getIncidencias().getReferencia());
				incidencia.setTiporef(viejo.getIncidencias().getTiporef());
				incidencia.setCodplan(viejo.getIncidencias().getCodplan());
			} else if (ENVIO_ANEXO.equals(tipoEnvio)) {
				incidencia.setIdenvio(viejo.getIncidencias().getIdenvio());
			} else if (ENVIO_ASEGURADO.equals(tipoEnvio)) {
				incidencia.setCodplan(viejo.getIncidencias().getCodplan());
				incidencia.setCodlinea(viejo.getIncidencias().getCodlinea());
				incidencia.setNifaseg(viejo.getIncidencias().getNifaseg());
			} else {
				incidencia.setAnhoincidencia(viejo.getIncidencias().getAnhoincidencia());
				incidencia.setNumincidencia(viejo.getIncidencias().getNumincidencia());
			}
		} else {
			AsuntosInc asuntosInc = new AsuntosInc();
			asuntosInc.setId(viejo.getIncidencias().getAsuntosInc().getId());
			incidencia.setAsuntosInc(asuntosInc);
			if (ENVIO_ASEGURADO.equals(tipoEnvio)) {
				incidencia.setCodplan(viejo.getIncidencias().getCodplan());
				incidencia.setCodlinea(viejo.getIncidencias().getCodlinea());
				incidencia.setNifaseg(viejo.getIncidencias().getNifaseg());
			} else {
				incidencia.setReferencia(viejo.getIncidencias().getReferencia());
				incidencia.setTiporef(viejo.getIncidencias().getTiporef());
				incidencia.setCodplan(viejo.getIncidencias().getCodplan());
			}
		}
		nuevo.setIncidencias(incidencia);
		return nuevo;
	}

	private AsuntosInc obtenerAsuntoRequest(HttpServletRequest req) {
		String codAsunto = req.getParameter(COD_ASUNTO);
		return this.aportarDocIncidenciaManager.getAsunto(codAsunto);
	}

	private MultipartFile obtenerArchivoRequest(HttpServletRequest req) {
		final MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) req;
		return (MultipartFile) multiReq.getFileMap().get("fichero");
	}

	public ModelAndView doEliminarDoc(HttpServletRequest req, HttpServletResponse res) {
		DocumentacionIncForm docIncForm = new DocumentacionIncForm();
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			Long idIncidencia = Long.parseLong(req.getParameter(ID_INC));
			Long idDoc = Long.parseLong(req.getParameter("idDoc"));

			// Modif TAM (13.06.2018) //
			parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
			parametros.put(EXTENSIONES, req.getParameter(EXTENSIONES));
			parametros.put(TIPO_ENVIO, req.getParameter(TIPO_ENVIO_BOR));
			parametros.put(OPCION_BUSQUEDA, req.getParameter("opcionBusquedaBor"));
			parametros.put(TIPO_BUSQUEDA, req.getParameter("opcionBusquedaBor"));
			parametros.put(ORIGEN_ENVIO, req.getParameter(ORIGEN_ENVIO));
			parametros.put(ORIGEN, req.getParameter(ORIGEN));

			parametros.put(CONSULTA, false);

			// Añadido DNF (13.08.2108) //
			parametros.put(REFERENCIA_VOLVER, req.getParameter(REFERENCIA_VOLVER));
			parametros.put(LINEA_VOLVER, req.getParameter(LINEA_VOLVER));
			parametros.put(COD_PLAN_VOLVER, req.getParameter(COD_PLAN_VOLVER));
			parametros.put(ID_POLIZA_VOLVER, req.getParameter(ID_POLIZA_VOLVER));
			parametros.put(NOM_LINEA_VOLER, req.getParameter(NOM_LINEA_VOLER));
			parametros.put(MODULO_VOLVER, req.getParameter(MODULO_VOLVER));

			String codAsunto = req.getParameter("codAsuntoBor");
			AsuntosInc asunto = null;

			if (codAsunto != null) {
				asunto = this.aportarDocIncidenciaManager.getAsunto(codAsunto);
				parametros.put(ASUNTO_INC, asunto);
			}

			// Modif TAM (13.06.2018) //
			this.aportarDocIncidenciaManager.eliminarDocumento(idDoc);
			docIncForm = this.aportarDocIncidenciaManager.generarDocumentacionIncForm(idIncidencia);

			parametros.put(DOCUMENTOS, this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia));

			this.formatearCamposBorrado(req, docIncForm);

			if (docIncForm.getIncidencias().getCodlinea() != null) {
				BigDecimal codlinea = docIncForm.getIncidencias().getCodlinea();
				String nomlinea = this.aportarDocIncidenciaDao.getNombLinea(codlinea);
				parametros.put("nomLinea", nomlinea);
			}

			// Modif tam (24.07.2018)
			// parametros.put(VUELTA, this.parametrosVueltaConsultaIncidencia(req));
			VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
			this.parametrosVueltaConsultaIncidencia(req, vistaIncAgro);
			parametros.put(VUELTA, vistaIncAgro);
			parametros.put(FECHA_ENVIO_DESDE, vistaIncAgro.getFechaEnvioDesdeStr());
			parametros.put(FECHA_ENVIO_HASTA, vistaIncAgro.getFechaEnvioHastaStr());
			parametros.put(ID_INC, idIncidencia);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
	}

	public DocumentacionIncForm formatearCamposBorrado(HttpServletRequest req, DocumentacionIncForm docIncForm) {
		BigDecimal BigDec_null = null;

		if (!req.getParameter(ORIGEN).equals(EDITAR_INC)) {
			if (req.getParameter(ORIGEN_ENVIO).equals(ORIGEN_BD)) {
				if (req.getParameter(TIPO_ENVIO_BOR).equals("p")) {
					docIncForm.getIncidencias().setNifaseg("");
					docIncForm.getIncidencias().setCodlinea(BigDec_null);
				} else {
					docIncForm.getIncidencias().setReferencia("");
				}
			} else {
				if (req.getParameter(TIPO_ENVIO_BOR).equals("p")) {
					// Asegurado
					docIncForm.getIncidencias().setNifaseg("");
					docIncForm.getIncidencias().setCodlinea(BigDec_null);
					// Incidencia
					docIncForm.getIncidencias().setAnhoincidencia(BigDec_null);
					docIncForm.getIncidencias().setNumincidencia(BigDec_null);
					// Cupón
					docIncForm.getIncidencias().setIdenvio("");
				} else if (req.getParameter(TIPO_ENVIO_BOR).equals("aseg")) {
					// Póliza
					docIncForm.getIncidencias().setReferencia("");
					// Incidencia
					docIncForm.getIncidencias().setAnhoincidencia(BigDec_null);
					docIncForm.getIncidencias().setNumincidencia(BigDec_null);
					// Cupón
					docIncForm.getIncidencias().setIdenvio("");
				} else if (req.getParameter(TIPO_ENVIO_BOR).equals("i")) {
					// Póliza
					docIncForm.getIncidencias().setReferencia("");
					// Asegurado
					docIncForm.getIncidencias().setNifaseg("");
					docIncForm.getIncidencias().setCodlinea(BigDec_null);
					// Cupón
					docIncForm.getIncidencias().setIdenvio("");
					// Cupón
				} else if (req.getParameter(TIPO_ENVIO_BOR).equals("am")) {
					// Póliza
					docIncForm.getIncidencias().setReferencia("");
					// Asegurado
					docIncForm.getIncidencias().setNifaseg("");
					docIncForm.getIncidencias().setCodlinea(BigDec_null);
					// Incidencia
					docIncForm.getIncidencias().setAnhoincidencia(BigDec_null);
					docIncForm.getIncidencias().setNumincidencia(BigDec_null);
				}
			}
		}
		return docIncForm;
	}

	public ModelAndView doDescargarDoc(HttpServletRequest req, HttpServletResponse res) {
		Long idIncidencia = Long.parseLong(req.getParameter(ID_INC));
		Long idDoc = Long.parseLong(req.getParameter("idDoc"));
		try {
			DocumentosInc documento = this.aportarDocIncidenciaManager.obtenerDocumento(idDoc);
			if (documento != null) {
				Blob fichero = documento.getFichero();
				if (fichero != null) {
					res.setContentType(documento.getTiposDocInc().getMimeType());

					String extension = documento.getTiposDocInc().getExtension();
					String nombre = documento.getNombre();

					String nombreExtension = new StringBuilder("attachment; filename=\"").append(nombre).append(".")
							.append(extension).append("\"").toString();
					// res.setHeader("Content-Disposition", "attachment; filename=\"" +
					// nombreExtension + "\"");

					res.setHeader("Content-Disposition", nombreExtension);
					res.setHeader("cache-control", "no-cache");
					byte[] fileBytes = fichero.getBytes(1, Integer.parseInt(String.valueOf(fichero.length())));
					ServletOutputStream outs = res.getOutputStream();
					outs.write(fileBytes);
					outs.flush();
					outs.close();
				}
				return null;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		Map<String, Object> parametros = new HashMap<String, Object>();
		DocumentacionIncForm docIncForm = null;
		try {
			docIncForm = this.aportarDocIncidenciaManager.generarDocumentacionIncForm(idIncidencia);
			parametros.put(DOCUMENTOS, this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
	}

	public ModelAndView doCancelar(HttpServletRequest req, HttpServletResponse res) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		logger.debug("**@@** AportarDocIncidenciaController - doCancelar");
		try {
			String idIncString = req.getParameter(ID_INC);
			logger.debug("Valor de idIncString:" + idIncString);
			if (idIncString != null && !idIncString.equals("") && !idIncString.equals("null")) {
				this.aportarDocIncidenciaManager.borrarIncidencia(Long.parseLong(idIncString));
				parametros.put("mensaje", "Incidencia dada de baja satisfactoriamente");
			}
		} catch (Exception e) {
			parametros.put(ALERTA,
					"Se ha producido un error. Por favor, póngase en contacto con el administrador...");
			LOGGER.error(e.getMessage());
		}
		parametros.put("origenLlamada", "menuGeneral");
		parametros.put(CONSULTA, false);
		return new ModelAndView("redirect:/utilidadesIncidencias.run").addAllObjects(parametros);
	}

	public ModelAndView doEnviar(HttpServletRequest req, HttpServletResponse res, DocumentacionIncForm docIncForm) {

		logger.debug("**@@** Entramos en doEnviar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		/* 30.08.2018 */
		Map<String, Object> parametros2 = new HashMap<String, Object>();
		ModelAndView mv = null;
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		String tipoEnvio = req.getParameter(TIPO_ENVIO);

		try {
			parametros.put(EXTENSIONES, this.listaExtensionesPermitidas());
		} catch (Exception e) {
			LOGGER.error("No se pudo obtener el listado de extensiones", e);
		}

		String origenEnvio = req.getParameter(ORIGEN_ENVIO);
		try {
			parametros.put(LISTA_ASUNTOS, this.aportarDocIncidenciaManager.obtenerAsuntos());
		} catch (Exception e) {
			LOGGER.error("No se pudo obtener el listado de asuntos", e);
		}

		logger.debug("**@@** AportarDocIncidenciaController.doEnviar, valor de origen:" + req.getParameter(ORIGEN));

		/* MODIF TAM (06.06.2018) * Resolución Incidencias */
		parametros.put(ORIGEN, req.getParameter(ORIGEN));
		if (req.getParameter(ORIGEN).equals(EDITAR_INC)) {
			if (tipoEnvio == "" || tipoEnvio == null) {
				tipoEnvio = req.getParameter(OPCION_BUSQUEDA);
				parametros.put(TIPO_ENVIO, tipoEnvio);
			}
		}
		parametros.put(TIPO_BUSQUEDA, req.getParameter(OPCION_BUSQUEDA));
		parametros.put("plan", req.getParameter("plan"));
		parametros.put(REFERENCIA, req.getParameter(REFERENCIA));
		parametros.put(LINEA, req.getParameter(LINEA));
		parametros.put(NIF_CIF, req.getParameter(NIF_CIF));
		// Pet. 50775 ** MODIF TAM (06.06.2018) ** Fin //

		if (req.getParameter(ORIGEN).equals(INCIDENCIAS_AGRO)) {
			parametros.put("plan", req.getParameter("plan_aseg"));
			parametros.put("poliza_plan", req.getParameter("poliza_plan"));
			parametros.put(LINEA, req.getParameter("linea_aseg"));
		}

		// (09.07.2018)
		if (req.getParameter(ORIGEN).equals(IMPRESION_INCIDENCIAS)) {
			// Para no perder los datos y poder volver después de haber agregado un
			// documento
			parametros.put(LINEA_VOLVER, req.getParameter(LINEA_VOLVER));
			parametros.put(REFERENCIA_VOLVER, req.getParameter(REFERENCIA_VOLVER));
			parametros.put(FECHA_ENV_VOLVER, req.getParameter(FECHA_ENV_VOLVER));
			parametros.put(NOM_LINEA_VOLER, req.getParameter(NOM_LINEA_VOLER));
			parametros.put(ID_POLIZA_VOLVER, req.getParameter(ID_POLIZA_VOLVER));
			parametros.put(NOMBRE_COMPLETO, req.getParameter(NOMBRE_COMPLETO));
			parametros.put(MODULO_VOLVER, req.getParameter(MODULO_VOLVER));
			parametros.put(COD_PLAN_VOLVER, req.getParameter(COD_PLAN_VOLVER));
		}
		// (09.07.2018)

		if (req.getParameter(ORIGEN).equals(EDITAR_INC)) {
			try {
				long idIncidencia = docIncForm.getIncidencias().getIdincidencia();
				docIncForm = this.aportarDocIncidenciaManager.generarDocumentacionIncForm(idIncidencia);

			} catch (Exception e) {
				LOGGER.error(e);
				parametros.put(ALERTA, ERROR_CONTACTO);
			}
		}

		AsuntosInc asuntosInc = this.obtenerAsuntoRequest(req);

		String codAsunto = asuntosInc.getId().getCodasunto();

		if (codAsunto != null) {
			parametros.put(ASUNTO_INC, asuntosInc);
		}
		docIncForm.getIncidencias().setAsuntosInc(asuntosInc);
		String codUsuario = ((Usuario) req.getSession().getAttribute("usuario")).getCodusuario();
		BigDecimal numincidencia = docIncForm.getIncidencias().getNumincidencia();

		Long idIncidencia = docIncForm.getIncidencias().getIdincidencia();

		logger.debug("**@@** AportarDocIncidenciaController.doEnviar, valor de idIncidencia:" + idIncidencia);

		try {

			/* ESC-9244 ** MODIF TAM (15/04/2020) ** Inicio */
			/*
			 * Comprobamos la referencia, plan y tiporef únicamente cuanto el tipo de Envio
			 * sea "poliza" ya que en cualquier otro caso esos datos no están informados y
			 * por lo tanto no hay que comprobarlos (Añadimos el if de tipoEnvio).
			 */
			if (tipoEnvio != "" && tipoEnvio != null && tipoEnvio.equals(ENVIO_POLIZA)) {

				/* ESC-8399 DNF 18/02/2020 */
				VistaIncidenciasAgro via = this.aportarDocIncidenciaDao
						.getPlanRefTipoRefFromIncidenciaById(idIncidencia);
				logger.debug("doEnviar.referencia: " + via.getReferencia());
				logger.debug("doEnviar.plan: " + via.getCodplan());
				logger.debug("doEnviar.tiporef: " + via.getTiporef());

				// validamos que no sean null
				if (null != via.getCodplan() && null != via.getReferencia()
						&& null != via.getTiporef()) {

					logger.debug("**@@** AportarDocIncidenciaController.doEnviar, Entramos en el if");

					// Comprobamos que lo que hay en BBDD y pantalla NO ha cambiado, en caso de que
					// sean distintos actualizo el valor
					// en BBDD:
					if (!(docIncForm.getIncidencias().getCodplan().compareTo(via.getCodplan()) == 0)
							|| !docIncForm.getIncidencias().getReferencia().equals(via.getReferencia())
							|| !docIncForm.getIncidencias().getTiporef().equals(via.getTiporef())) {

						logger.debug(
								"**@@** AportarDocIncidenciaController.doEnviar, Entramos en el segundo if, distintos valores");

						// update plan, referencia y tipoRef. por idIncidencia
						this.aportarDocIncidenciaDao.actualizarIncidenciaPlanRefYTipo(
								docIncForm.getIncidencias().getCodplan(), docIncForm.getIncidencias().getReferencia(),
								docIncForm.getIncidencias().getTiporef(), idIncidencia);

						logger.debug("doEnviar datos actualizados en BBDD.");

					}
				}
				/* FIN ESC-8399 DNF 18/02/2020 */
			}

			parametros.put(DOCUMENTOS, this.aportarDocIncidenciaManager.obtenerDocumentos(idIncidencia));

			/* 30.08.2018 */
			parametros2 = parametros;

			if ((ORIGEN_BD.equals(origenEnvio) && !req.getParameter(ORIGEN).equals(EDITAR_INC)
					&& numincidencia == null) || (numincidencia == BigDecimal.ZERO)) {
				parametros = this.aportarDocIncidenciaManager.enviarDocAgroseguroNuevo(realPath, docIncForm, tipoEnvio,
						codUsuario);
			} else {
				parametros = this.aportarDocIncidenciaManager.enviarDocAgroseguro(realPath, docIncForm, tipoEnvio,
						codUsuario);
			}
			parametros.put("mensaje", "Incidencia enviada correctamente");
			VistaIncidenciasAgro vistaIncidenciasAgro = new VistaIncidenciasAgro();
			boolean hasfilters = this.parametrosVueltaConsultaIncidencia(req, vistaIncidenciasAgro);

			if (hasfilters) {
				mv = this.incidenciasUtilidadesController.doConsulta(req, res, vistaIncidenciasAgro);
			} else {
				/* 30.08.2018 */
				/* Si ha ido correcto volvemos a la ventana de Relación de Modificaciones */
				if (ORIGEN_AGRO.equals(origenEnvio) && req.getParameter(ORIGEN).equals(IMPRESION_INCIDENCIAS)) {

					parametros.putAll(parametros2);

					mv = this.doVolverImpresionIncidencias(parametros, req, res);
					parametros.put(LINEA, req.getParameter(LINEA_VOLVER));
					parametros.put(REFERENCIA, req.getParameter(REFERENCIA_VOLVER));
					parametros.put("plan", req.getParameter(COD_PLAN_VOLVER));
					parametros.put("idPoliza", req.getParameter(ID_POLIZA_VOLVER));

					mv.addAllObjects(parametros);
					return mv;
				} else {
					mv = new ModelAndView("redirect:/utilidadesIncidencias.run?origenLlamada=menuGeneral",
							"VistaIncidenciasAgro", new VistaIncidenciasAgro());
				}
			}
		} catch (AgrException e) {
			parametros.put(ALERTA, procesarAgrException(e));
			LOGGER.debug(procesarAgrException(e));
			LOGGER.error("Avisamos al usuario", e);
			mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
		} catch (Exception e) {
			parametros.put(ALERTA, "Se ha producido un error, póngase en contacto con el administrador");
			LOGGER.error("Avisamos al usuario", e);
			mv = new ModelAndView(aportarDocIncidenciaVista, DOC_INC_FORM, docIncForm).addAllObjects(parametros);
		}

		// Modif tam (24.07.2018)
		// parametros.put(VUELTA, this.parametrosVueltaConsultaIncidencia(req));
		VistaIncidenciasAgro vistaVuelta = new VistaIncidenciasAgro();
		this.parametrosVueltaConsultaIncidencia(req, vistaVuelta);
		parametros.put(VUELTA, vistaVuelta);
		parametros.put(FECHA_ENVIO_DESDE, vistaVuelta.getFechaEnvioDesdeStr());
		parametros.put(FECHA_ENVIO_HASTA, vistaVuelta.getFechaEnvioHastaStr());

		parametros.put(TIPO_ENVIO, tipoEnvio);
		parametros.put(ORIGEN_ENVIO, origenEnvio);
		parametros.put(CONSULTA, false);
		mv.addAllObjects(parametros);
		return mv;
	}

	// Pet. 50775 ** MODIF TAM (31.08.2018) * Resolución de Incidencias ** FIN
	public ModelAndView doVolverImpresionIncidencias(Map<String, Object> parametros, HttpServletRequest req,
			HttpServletResponse res) {

		Poliza poliza = null;
		Long idpoliza = null;

		try {

			idpoliza = new Long(req.getParameter(ID_POLIZA_VOLVER));
			poliza = aportarDocIncidenciaDao.getPolizaById(idpoliza);
		} catch (Exception e) {
			LOGGER.error(e);
			parametros.put(ALERTA, ERROR_CONTACTO);
		}

		ModelAndView mv = null;

		AnexoModificacion anexoModificacion = new AnexoModificacion();
		BigDecimal codLinea = new BigDecimal(req.getParameter(LINEA_VOLVER));
		String nombreComplAsegurado = req.getParameter(NOMBRE_COMPLETO);

		anexoModificacion.setPoliza(poliza);

		anexoModificacion.getPoliza().setReferencia(req.getParameter(REFERENCIA_VOLVER));
		anexoModificacion.getPoliza().getLinea().setCodlinea(codLinea);
		anexoModificacion.getPoliza().getLinea().setCodplan(poliza.getLinea().getCodplan());
		anexoModificacion.getPoliza().getLinea().setNomlinea(poliza.getLinea().getNomlinea());
		anexoModificacion.getPoliza().setModuloPolizas(poliza.getModuloPolizas());
		anexoModificacion.getPoliza().getAsegurado().setNombreCompleto(nombreComplAsegurado);
		anexoModificacion.getPoliza().setFechaenvio(poliza.getFechaenvio());

		req.setAttribute("fechaEnvio", poliza.getFechaenvio());
		req.setAttribute(REFERENCIA, req.getParameter(REFERENCIA_VOLVER));
		req.setAttribute(LINEA, codLinea);
		req.setAttribute("plan", poliza.getLinea().getCodplan());

		mv = impresionIncidenciasModController.doImprimirIncidencias(req, res, anexoModificacion);
		mv.addObject("poliza", poliza);

		return mv;
	}
	// Pet. 50775 ** MODIF TAM (31.08.2018) * Resolución de Incidencias ** FIN

	private static String procesarAgrException(AgrException e) {
		StringBuilder msg = new StringBuilder();
		if (e.getFaultInfo() != null && e.getFaultInfo().getError() != null) {
			List<Error> errores = e.getFaultInfo().getError();
			for (Error error : errores) {
				msg.append(error.getMensaje()).append("\n");
			}
		}
		return msg.toString();
	}

	public ModelAndView doVolver(HttpServletRequest req, HttpServletResponse res) throws ParseException {
		VistaIncidenciasAgro vista = new VistaIncidenciasAgro();
		boolean hasFilters = this.parametrosVueltaConsultaIncidencia(req, vista);

		if (!hasFilters) {
			req.setAttribute("origenLlamadaAttr", "menuGeneral");
		}
		return this.incidenciasUtilidadesController.doConsulta(req, res, vista);
	}

	public void setAportarDocIncidenciaManager(AportarDocIncidenciaManager aportarDocIncidenciaManager) {
		this.aportarDocIncidenciaManager = aportarDocIncidenciaManager;
	}

	public void setAportarDocIncidenciaVista(String aportarDocIncidenciaVista) {
		this.aportarDocIncidenciaVista = aportarDocIncidenciaVista;
	}

	public void setIncidenciasUtilidadesController(
			IncidenciasAgroUtilidadesController incidenciasUtilidadesController) {
		this.incidenciasUtilidadesController = incidenciasUtilidadesController;
	}

	public void setAportarDocIncidenciaDao(IAportarDocIncidenciaDao aportarDocIncidenciaDao) {
		this.aportarDocIncidenciaDao = aportarDocIncidenciaDao;
	}

	public void setImpresionIncidenciasModController(
			ImpresionIncidenciasModController impresionIncidenciasModController) {
		this.impresionIncidenciasModController = impresionIncidenciasModController;
	}

}
