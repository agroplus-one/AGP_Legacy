package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IDocumentacionAgroseguroService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.doc.DocAgroseguro;
import com.rsi.agp.dao.tables.doc.DocAgroseguroExtPerm;
import com.rsi.agp.dao.tables.doc.DocAgroseguroTipo;
import com.rsi.agp.dao.tables.doc.FormCargaDoc;

public class DocumentacionAgroseguroController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(DocumentacionAgroseguroController.class);

	private final static BigDecimal PLANGENERICO = new BigDecimal(9999);

	private IDocumentacionAgroseguroService docAgroseguroService;
	private String successView;

	public ModelAndView doConsulta(final HttpServletRequest request, final HttpServletResponse response,
			final DocAgroseguro docAgroseguroBean) throws DAOException {

		LOGGER.debug("DocumentacionAgroseguroController - doConsulta[INIT]");

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String html = null;
		String origenLlamada = request.getParameter("origenLlamada");

		/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio */
		String perfil = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		List<String> listPerfEdit = new ArrayList<String>();
		List<String> listPerfNull = new ArrayList<String>();

		if (StringUtils.nullToString(origenLlamada).equals("editar")) {
			listPerfEdit = docAgroseguroBean.getListaPerfiles();
			docAgroseguroBean.setListaPerfiles(listPerfNull);
		}

		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		parametros.put("externo", usuario.getExterno());
		parametros.put("grupoEntidades",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
		parametros.put("listaPerfil", this.docAgroseguroService.obtenerListaPerfiles());

		if (docAgroseguroBean.getCodplan() != null) {
			if (docAgroseguroBean.getCodplan().compareTo(PLANGENERICO) == 0) {
				parametros.put("esPlanGenerico", true);
			} else {
				parametros.put("esPlanGenerico", false);
			}
		} else {
			parametros.put("esPlanGenerico", false);
		}

		if (docAgroseguroBean.getCodlinea() == null) {
			String lineaCond = request.getParameter("lineaCondicionado");
			if (lineaCond != null) {
				BigDecimal linea = new BigDecimal(lineaCond);
				docAgroseguroBean.setCodlinea(linea);
			} else {
				String lineaCondA = request.getParameter("lineaGen");
				if (!StringUtils.isNullOrEmpty(lineaCondA)) {
					BigDecimal linea = new BigDecimal(lineaCondA);
					docAgroseguroBean.setCodlinea(linea);
				}
			}
		}

		if (docAgroseguroBean.getId() != null) {
			parametros.put("id", docAgroseguroBean.getId());
		}
		/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin */

		try {

			cargaParametrosComunes(request, parametros);

			/* Resoluci�n Defectos 19.04.2022 */
			if (StringUtils.nullToString(origenLlamada).equals("cargar")
					|| StringUtils.nullToString(origenLlamada).equals("modificar")) {
				if (docAgroseguroBean.getCodplan().compareTo(PLANGENERICO) == 0) {
					parametros.put("lineaGen", docAgroseguroBean.getCodlinea());
					String nombLineaGen = this.docAgroseguroService.getNombLinea(docAgroseguroBean.getCodlinea());
					parametros.put("nomblineaGen", nombLineaGen);
				} else {
					String nombLinea = this.docAgroseguroService.getNombLinea(docAgroseguroBean.getCodlinea());
					docAgroseguroBean.setNomlinea(nombLinea);
				}

				String nombEntidad = this.docAgroseguroService.getNombEntidad(docAgroseguroBean.getCodentidad());
				docAgroseguroBean.setNombreEntidad(nombEntidad);

			}
			/* Resoluci�n Defectos 19.04.2022 */

			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				logger.debug("Comienza la busqueda de documentos");

				html = this.docAgroseguroService.getTablaDocumentos(request, response, docAgroseguroBean, origenLlamada,
						(Boolean) parametros.get("esUsuarioAdmin"), usuario);

				if (html == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este
						// atributo
						request.setAttribute("docsAgroseguro", html);
				}
			}

			// al entrar desde menugeneral buscamos el nomEntidad
			if (StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {

				switch (new Integer(perfil).intValue()) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					docAgroseguroBean.setNombreEntidad(usuario.getOficina().getEntidad().getNomentidad());
					docAgroseguroBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
					break;
				}

			}

			if (StringUtils.nullToString(origenLlamada).equals("editar")) {
				if (docAgroseguroBean.getListaPerfiles().size() > 0)
					parametros.put("listPerSelD", docAgroseguroBean.getListaPerfiles());
			}

			parametros.put("origenLlamada", origenLlamada);

			if (StringUtils.nullToString(origenLlamada).equals("editar")) {
				docAgroseguroBean.setListaPerfiles(listPerfEdit);
			}

			BigDecimal codEntZero = new BigDecimal(0);
			if (docAgroseguroBean.getCodentidad() != null) {
				if (docAgroseguroBean.getCodentidad().compareTo(codEntZero) == 0) {
					docAgroseguroBean.setCodentidad(null);
				}
			}

			String mensaje = request.getParameter("mensaje") == null ? (String) request.getAttribute("mensaje")
					: request.getParameter("mensaje");
			String alerta = request.getParameter("alerta") == null ? (String) request.getAttribute("alerta")
					: request.getParameter("alerta");
			if (alerta != null) {
				parametros.put("alerta", alerta);
			}
			if (mensaje != null) {
				parametros.put("mensaje", mensaje);
			}
		} catch (Exception e) {

			logger.error("Error en doConsulta de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;
	}

	@SuppressWarnings("unchecked")
	public ModelAndView doCarga(final HttpServletRequest request, final HttpServletResponse response,
			final FormCargaDoc docAgroseguroBean) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		DocAgroseguro resultBean = new DocAgroseguro();

		LOGGER.debug("DocumentacionAgroseguroController - doCarga[INIT]");

		String origenLlamada = request.getParameter("origenLlamada");

		/** Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio **/
		String perfil = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		parametros.put("grupoEntidades",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));

		if (docAgroseguroBean.getCodplan() != null) {
			if (docAgroseguroBean.getCodplan().compareTo(PLANGENERICO) == 0) {
				parametros.put("esPlanGenerico", true);
			} else {
				parametros.put("esPlanGenerico", false);
			}
		} else {
			parametros.put("esPlanGenerico", false);
		}

		if (docAgroseguroBean.getCodlinea() == null) {
			String lineaCond = request.getParameter("lineaCondicionado");
			if (lineaCond != null) {
				BigDecimal linea = new BigDecimal(lineaCond);
				docAgroseguroBean.setCodlinea(linea);
			}
		}
		/** Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin */

		try {
			parametros.put("listaPerfil", this.docAgroseguroService.obtenerListaPerfiles());

			if (docAgroseguroBean.getCodlinea() == null) {
				String lineaCond = request.getParameter("lineaCondicionado");
				String lineaC = request.getParameter("lineaCondicionado.codlinea");

				if (!StringUtils.isNullOrEmpty(lineaCond)) {
					BigDecimal linea = new BigDecimal(lineaCond);
					docAgroseguroBean.setCodlinea(linea);
				} else {
					if (!StringUtils.isNullOrEmpty(lineaC)) {
						BigDecimal linea = new BigDecimal(lineaC);
						docAgroseguroBean.setCodlinea(linea);
					}
				}
			}

			/* Pet. 79014 ** MODIF TAM (25.03.2022) ** Inicio */
			docAgroseguroBean.setCodusuario(usuario.getCodusuario());
			docAgroseguroBean.setFecha(new Date());
			/* Pet. 79014 ** MODIF TAM (25.03.2022) ** Inicio */

			cargaParametrosComunes(request, parametros);

			parametros.put("origenLlamada", origenLlamada);

			// HACEMOS ESTO PARA TENER EL NUM_MAX_DOCS EN LA VALIDACION Y ASI NO
			// NECEISTAR LLAMADA A BBDD REDUNDANTE
			Collection<DocAgroseguroTipo> listaTiposDoc = (Collection<DocAgroseguroTipo>) parametros
					.get("listaTiposDoc");
			for (DocAgroseguroTipo docAgroseguroTipo : listaTiposDoc) {
				if (docAgroseguroTipo.getId().equals(docAgroseguroBean.getDocAgroseguroTipo().getId())) {
					docAgroseguroBean.getDocAgroseguroTipo().setNumMaxLinea(docAgroseguroTipo.getNumMaxLinea());
					break;
				}
			}

			String errorMsg = this.docAgroseguroService.validateDocAgroseguro(docAgroseguroBean, perfil);
			if (errorMsg != null && !"".equals(errorMsg)) {
				parametros.put("alerta", errorMsg);
			} else {
				resultBean = this.docAgroseguroService.grabarDocAgroseguro(docAgroseguroBean);
				this.docAgroseguroService.grabarPerfilesDocAgr(resultBean);
				request.setAttribute("mensaje", "Fichero cargado correctamente.");

				return doConsulta(request, response, resultBean);
			}
		} catch (Exception e) {

			logger.error("Error en doCarga de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;
	}

	public ModelAndView doDescargarFichero(final HttpServletRequest request, final HttpServletResponse response,
			DocAgroseguro docAgroseguroBean) throws Exception {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String origenLlamada = request.getParameter("origenLlamada");

		try {

			cargaParametrosComunes(request, parametros);

			parametros.put("origenLlamada", origenLlamada);

			DocAgroseguro docAgroseguro = this.docAgroseguroService.getDocAgroseguro(docAgroseguroBean.getId());

			if (docAgroseguro != null) {

				Blob fichero = docAgroseguro.getDocAgroseguroFichero().getFichero();
				if (fichero != null) {
					response.setContentType(docAgroseguro.getDocAgroseguroExtPerm().getMimeType());
					response.setHeader("Content-Disposition",
							"attachment; filename=\"" + docAgroseguro.getNombre() + "\"");
					response.setHeader("cache-control", "no-cache");
					byte[] fileBytes = docAgroseguro.getDocAgroseguroFichero().getFichero().getBytes(1,
							Integer.parseInt(String.valueOf(fichero.length())));
					ServletOutputStream outs = response.getOutputStream();
					outs.write(fileBytes);
					outs.flush();
					outs.close();
				}

				return null;
			}
		} catch (Exception e) {

			logger.error("Error en doDescargarFichero de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;
	}

	public ModelAndView doBorrar(final HttpServletRequest request, final HttpServletResponse response,
			final DocAgroseguro docAgroseguroBean) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {
			cargaParametrosComunes(request, parametros);

			this.docAgroseguroService.borrarDocsAgroseguro(new Long[] { docAgroseguroBean.getId() });

			request.setAttribute("mensaje", "Fichero borrado correctamente.");
			return doConsulta(request, response, docAgroseguroBean);

		} catch (Exception e) {

			logger.error("Error en doCarga de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;
	}

	/* Pet. 79014 ** MODIF TAM (21.03.2022) ** Inicio */
	public ModelAndView doEditar(final HttpServletRequest request, final HttpServletResponse response,
			DocAgroseguro docAgroseguroBean) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String origenLlamada = request.getParameter("origenLlamada");

		LOGGER.debug("DocumentacionAgroseguroController - doEditar[INIT]");
		LOGGER.debug("DocumentacionAgroseguroController - Valor de id:" + docAgroseguroBean.getId());

		try {

			logger.debug("Valor de origenLlamada:" + origenLlamada);
			parametros.put("origenLlamada", origenLlamada);

			String perfil = "";
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

			perfil = usuario.getPerfil().substring(4);
			parametros.put("perfil", perfil);
			parametros.put("grupoEntidades",
					StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
			parametros.put("listaPerfil", this.docAgroseguroService.obtenerListaPerfiles());

			cargaParametrosComunes(request, parametros);

			docAgroseguroBean = this.docAgroseguroService.getDocAgroseguro(docAgroseguroBean.getId());

			/* Obtenemos la lista de Perfiles asociados al documento */
			List<String> listPerfilesDoc = this.docAgroseguroService.obtenerListaPerfilesDoc(docAgroseguroBean.getId());
			docAgroseguroBean.setListaPerfiles(listPerfilesDoc);

			return doConsulta(request, response, docAgroseguroBean);

		} catch (Exception e) {

			logger.error("Error en doEditar de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;

	}

	public ModelAndView doModificar(final HttpServletRequest request, final HttpServletResponse response,
			DocAgroseguro docAgroseguroBean) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		LOGGER.debug("DocumentacionAgroseguroController - doModificar[INIT]");
		LOGGER.debug("DocumentacionAgroseguroController - Valor de id:" + docAgroseguroBean.getId());

		if (docAgroseguroBean.getCodlinea() == null) {
			String lineaCond = request.getParameter("lineaCondicionado");
			if (lineaCond != null) {
				BigDecimal linea = new BigDecimal(lineaCond);
				docAgroseguroBean.setCodlinea(linea);
			} else {
				String lineaCondA = request.getParameter("lineaGen");
				if (!StringUtils.isNullOrEmpty(lineaCondA)) {
					BigDecimal linea = new BigDecimal(lineaCondA);
					docAgroseguroBean.setCodlinea(linea);
				}
			}
		}

		String idStr = request.getParameter("id");
		Long id = Long.parseLong(idStr);

		try {

			String perfil = "";
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

			perfil = usuario.getPerfil().substring(4);
			parametros.put("perfil", perfil);
			parametros.put("grupoEntidades",
					StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
			parametros.put("listaPerfil", this.docAgroseguroService.obtenerListaPerfiles());

			cargaParametrosComunes(request, parametros);

			this.docAgroseguroService.modificarDocAgroseguro(id, docAgroseguroBean, usuario.getCodusuario());

			LOGGER.debug("**@@** Antes de modificar los Perfiles");
			this.docAgroseguroService.modificarPerfilesDocAgr(id, docAgroseguroBean);

			request.setAttribute("mensaje", "Documento Modificado correctamente.");
			parametros.put("origenLlamada", "editar");

			return doConsulta(request, response, docAgroseguroBean);

		} catch (Exception e) {

			logger.error("Error en doEditar de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;

	}
	/* Pet. 79014 ** MODIF TAM (21.03.2022) ** Fin */

	public ModelAndView doBorradoMasivo(final HttpServletRequest request, final HttpServletResponse response,
			final DocAgroseguro docAgroseguroBean) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			cargaParametrosComunes(request, parametros);

			String listaIdsMarcados = StringUtils.nullToString(request.getParameter("listaIdsMarcados"));
			String[] idsMarcadosStrArr = listaIdsMarcados.split(",");
			Long[] idsMarcadosLngArr = new Long[idsMarcadosStrArr.length];
			for (int i = 0; i < idsMarcadosStrArr.length; i++) {
				idsMarcadosLngArr[i] = Long.valueOf(idsMarcadosStrArr[i]);
			}

			this.docAgroseguroService.borrarDocsAgroseguro(idsMarcadosLngArr);

			request.setAttribute("mensaje", "Fichero/s borrado/s correctamente.");

			return doConsulta(request, response, docAgroseguroBean);

		} catch (Exception e) {

			logger.error("Error en doBorradoMasivo de DocumentacionAgroseguro", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "docAgroseguroBean", docAgroseguroBean);
		mv.addAllObjects(parametros);

		return mv;
	}

	private void cargaParametrosComunes(final HttpServletRequest request, final Map<String, Object> parametros)
			throws BusinessException {

		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		boolean esUsuarioAdmin = Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil());
		parametros.put("esUsuarioAdmin", esUsuarioAdmin);

		// TIPOS DE DOCUMENTO (PARA COMBO)
		Collection<DocAgroseguroTipo> listaTiposDoc = null;
		if (esUsuarioAdmin) {
			listaTiposDoc = this.docAgroseguroService.getTiposDocumento();
		} else {
			listaTiposDoc = this.docAgroseguroService.getTiposDocumentoNoAdmin();
		}
		parametros.put("listaTiposDoc", listaTiposDoc);

		// EXTENSIONES (PARA VALIDACION JQUERY DE EXTENSION PERMITIDA)
		Collection<DocAgroseguroExtPerm> listaExtensiones = this.docAgroseguroService.getExtensiones();
		StringBuilder extensionesPermitidas = new StringBuilder();
		for (DocAgroseguroExtPerm docAgroseguroExtPerm : listaExtensiones) {
			extensionesPermitidas.append(docAgroseguroExtPerm.getExtension());
			extensionesPermitidas.append('|');
		}
		if (extensionesPermitidas.indexOf("|") != -1) {
			extensionesPermitidas.replace(extensionesPermitidas.lastIndexOf("|"), extensionesPermitidas.length(), "");
		}
		parametros.put("extensionesPermitidas", extensionesPermitidas.toString());
	}

	public void doDescargarCondicionesRC(final HttpServletRequest req, final HttpServletResponse res) {
		try {
			BigDecimal plan = new BigDecimal(req.getParameter("plan"));
			String especieRC = req.getParameter("especieRC");
			DocAgroseguro docAgroseguro = docAgroseguroService.getDocumentoParaPolizasRC(especieRC, plan);
			if (docAgroseguro != null) {
				Blob fichero = docAgroseguro.getDocAgroseguroFichero().getFichero();
				if (fichero != null) {
					res.setContentType(docAgroseguro.getDocAgroseguroExtPerm().getMimeType());
					String fileName = new StringBuilder("attachment; filename=\"").append(docAgroseguro.getNombre())
							.append("\"").toString();
					res.setHeader("Content-Disposition", fileName);
					res.setHeader("cache-control", "no-cache");
					int fileLength = ((Long) fichero.length()).intValue();
					byte[] fileBytes = docAgroseguro.getDocAgroseguroFichero().getFichero().getBytes(1, fileLength);
					ServletOutputStream output = res.getOutputStream();
					output.write(fileBytes);
					output.flush();
					output.close();
				}
			}
		} catch (Exception e) {
			logger.error("Excepcion : DocumentacionAgroseguroController - doDescargarCondicionesRC", e);
		}
	}

	/** Pet. 79014 ** MODIF TAM (25.03.2022) ** Inicio **/
	public void doAltaModifTipoDoc(HttpServletRequest request, HttpServletResponse response,
			DocAgroseguro docAgroseguroBean) throws Exception {

		logger.debug("** DocumentacionAgroseguroController - doAltaModifTipoDoc [INIT]");
		JSONObject objeto = new JSONObject();

		String descripcion = request.getParameter("desc");
		String operacion = request.getParameter("operacion").toUpperCase();

		try {
			if (operacion.equals("A")) {

				/* Alta de un nuevo tipo Documento */
				DocAgroseguroTipo docAgroTipo = new DocAgroseguroTipo();

				docAgroTipo.setNumMaxLinea(99);
				docAgroTipo.setVisible(1);
				docAgroTipo.setDescripcion(descripcion);

				DocAgroseguroTipo docAgrTipo = this.docAgroseguroService.insertDescTipoDoc(docAgroTipo);
				logger.debug("Valor de docAgroTipo de salida:" + docAgrTipo.getId());
			} else {
				Long idTipoDoc = Long.parseLong(request.getParameter("idTipoDoc"));
				DocAgroseguroTipo docAgrTipo = this.docAgroseguroService.updateDescTipoDoc(idTipoDoc, descripcion);
				logger.debug("Valor de docAgroTipo de salida:" + docAgrTipo.getId());
			}

			/* Si el alta ha ido correctamente */
			objeto.put("result", "OK");

			response.setCharacterEncoding("UTF-8");
			getWriterJSON(response, objeto);

			logger.debug("DocumentacionAgroseguroController - doAltaModifTipoDoc [END]");

		} catch (JSONException e) {
			objeto.put("result", "KO");
			logger.error("Excepcion : DocumentacionAgroseguroController - doAltaModifTipoDoc", e);
		} catch (Exception e) {
			objeto.put("result", "KO");
			logger.debug("DocumentacionAgroseguroController.doAltaModifTipoDoc - Ocurri� un error");
			logger.error("Ocurri� un error en Alta/Modificaci�n Tipo Documento.", e);

			try {
				objeto.put("alert", "Ha ocurrido un error en Alta/Modificaci�n Tipo Documento.");
				objeto.put("result", "KO");

				getWriterJSON(response, objeto);
			} catch (JSONException e1) {
				logger.error("Excepcion : DocumentacionAgroseguroController - doAltaModifTipoDoc", e1);
			}
		}
	}

	public void doObtenerNombLinea(HttpServletRequest request, HttpServletResponse response,
			DocAgroseguro docAgroseguroBean) throws Exception {
		logger.debug("** DocumentacionAgroseguroController - doObtenerNombLinea [INIT]");

		JSONObject objeto = new JSONObject();
		String codLinea = request.getParameter("codLinea");

		try {
			String nomLinea = "";
			if (!StringUtils.isNullOrEmpty(codLinea)) {
				BigDecimal codLin = new BigDecimal(codLinea);
				nomLinea = this.docAgroseguroService.getNombLinea(codLin);
			} else {
				nomLinea = "";
			}

			/* Si el alta ha ido correctamente */
			objeto.put("result", "OK");
			objeto.put("descripcion", nomLinea);

			response.setCharacterEncoding("UTF-8");
			getWriterJSON(response, objeto);

			logger.debug("DocumentacionAgroseguroController - doObtenerNombLinea [END]");
		} catch (JSONException e) {
			objeto.put("result", "KO");
			logger.error("Excepcion : DocumentacionAgroseguroController - doObtenerNombLinea", e);
		} catch (Exception e) {
			objeto.put("result", "KO");
			try {
				objeto.put("alert", "Ha ocurrido un error en Alta/Modificaci�n Tipo Documento.");
				objeto.put("result", "KO");

				getWriterJSON(response, objeto);
			} catch (JSONException e1) {
				logger.error("Excepcion : DocumentacionAgroseguroController - doAltaModifTipoDoc", e1);
			}
		}
	}

	/** Pet. 79014 ** MODIF TAM (24.03.2022) ** Fin **/

	public void setDocAgroseguroService(IDocumentacionAgroseguroService docAgroseguroService) {
		this.docAgroseguroService = docAgroseguroService;
	}

	public void setSuccessView(final String successView) {
		this.successView = successView;
	}
}