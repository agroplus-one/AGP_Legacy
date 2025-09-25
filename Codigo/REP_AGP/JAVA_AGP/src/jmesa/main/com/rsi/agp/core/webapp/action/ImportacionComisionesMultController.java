package com.rsi.agp.core.webapp.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.ImportacionComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultContenido;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ImportacionComisionesMultController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(ImportacionComisionesMultController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	private ImportacionComisionesManager importacionComisionesManager;
	
	public static final String CLAVE_ID_FICHERO = "idFichero";
	public static final String CLAVE_ID_ERROR = "error";
	public static final Long WARN_ID_VALIDACION = -1L; //En validación
	public static final Long WARN_ID_TABLA_INFORMES = -2L; //En inserción tabla de informes
	
	private static final String ESTADO_AJAX_DONE = "DONE";
	private static final String ESTADO_AJAX_WARN = "WARN";
	private static final String ESTADO_AJAX_ERROR_DUPLICADO = "DUPLICADO";
	private static final String ESTADO_AJAX_ERROR_GENERICO = "FAILED";
	

	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, FormFicheroComisionesBean ffcb)
			throws Exception {
		LOGGER.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<FicheroMult> listimportaciones = null;
		String idFichero = "";
		String accion = "";
		FicheroMult fichero = null;
		Character tipo = null;
		String estado = "";
		/*
		try {
			if (StringUtils.nullToString(request.getParameter("limpiar")).equals("")){
				FormFicheroComisionesBean filtroFicheroCom = (FormFicheroComisionesBean) request.getSession().getAttribute("filtroFicheroCom");
				if (filtroFicheroCom != null) {
					ffcb = filtroFicheroCom;
				}
				
			}else {
				request.getSession().removeAttribute("filtroFicheroCom");
				
			}
			if (!StringUtils.nullToString(request.getParameter("tipo")).equals(""))
				tipo = request.getParameter("tipo").charAt(0);

			accion = StringUtils.nullToString(request.getParameter("method"));
			// PARA PODER FILTRAR EL GRID POR EL FICHERO IMPORTADO
			if (!accion.equals("doBorrarFichero")) {
				idFichero = StringUtils.nullToString(request.getParameter("idFichero"));
				if (!idFichero.equals("")) {
					fichero = importacionComisionesManager.getFicheroMult(new Long(idFichero));
					ffcb.setFechaCarga(fichero.getFechaCarga());
					ffcb.setNombreFichero(fichero.getNombreFichero());
				}
			}

			if (request.getParameter("estado") != null ){
				estado = request.getParameter("estado");
				parametros.put("estado", estado);
			}
			if (ffcb.getEstado() != null ){
				estado =ffcb.getEstado();
				parametros.put("estado", ffcb.getEstado());
			}
			
			listimportaciones = importacionComisionesManager.listImportacionesMult(ffcb, tipo, estado);
			
			// En el caso de que se filtre por codigo de situacion se halla los
			// diferentes ficheros por dicho codigo
			if (ffcb.getReglamentoProduccionEmitidaSituacion().getCodigo() != null
					&& listimportaciones.size() > 0) {
				listimportaciones = hallarListadoImportacionesPocCodigoSit(
						listimportaciones, ffcb.getReglamentoProduccionEmitidaSituacion().getCodigo());
			}
			
			parametros.put("listImportaciones", listimportaciones);
			parametros.put("tipo", tipo);
			
			request.getSession().setAttribute("filtroFicheroCom", ffcb);
			
			mv = new ModelAndView("moduloComisiones/importacionComisionesMult", "ffcb", ffcb);

		} catch (BusinessException be) {
			logger.error("Se ha producido un error", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new FormFicheroComisionesBean());
		} catch (Exception be) {
			logger.error("Se ha producido un error", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new FormFicheroComisionesBean());
		}
*/
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doBorrarFichero(HttpServletRequest request,	HttpServletResponse response, FormFicheroComisionesBean ffcb)throws Exception {
		LOGGER.debug("init - doBorrarFichero");
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		String idFichero = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");/*
		try {

			idFichero = StringUtils.nullToString(request.getParameter("idFichero"));
			LOGGER.debug("Fichero que se va a eliminar: " + idFichero);

			if (!idFichero.equals("")) {
				LOGGER.debug("Recuperamos el objeto fichero");
				FicheroMult fichero = importacionComisionesManager.getFicheroMult(new Long(idFichero));
				if (fichero != null) {
					LOGGER.debug("Comprobamos que el fichero no tenga fecha de cierre");
					// hallamos la fase para comprobar si esta cerrada
					if (!importacionComisionesManager.ficheroCierre(fichero)) {
						LOGGER.debug("Damos de baja el fichero: " + fichero.getId());
						//TMR 30-05-2012 Facturacion
						importacionComisionesManager.borrarFicheroMult(fichero,usuario);
						parametros.put("mensaje",bundle.getString("mensaje.comisiones.importacion.baja.OK"));
						LOGGER.debug("Baja completada");
					} else {
						parametros.put("alerta",bundle.getString("mensaje.comisiones.importacion.baja.fecha.KO"));
					}

				} else
					parametros.put("alerta", bundle.getString("mensaje.comisiones.importacion.baja.KO"));

			} else {
				LOGGER.error("no hemos recuperado el id del fichero");
				parametros.put("alerta", bundle.getString("mensaje.comisiones.importacion.baja.KO"));
			}
			mv = doConsulta(request, response, new FormFicheroComisionesBean());

		} catch (BusinessException be) {
			logger.error("Se ha producido un error al borrar el fichero", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new FormFicheroComisionesBean());
		} catch (Exception be) {
			logger.error("Se ha producido un error al borrar el fichero", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new FormFicheroComisionesBean());
		}
*/
		LOGGER.debug("end - doBorrarFichero");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doCargar(HttpServletRequest request,
			HttpServletResponse response, FormFicheroComisionesBean ffcb)
			throws Exception {
		LOGGER.debug("init - doCargar");
		Usuario usuario = null;
		Character tipo = null;
		JSONObject resultado = new JSONObject();
		Long idFichero = null;
		Map<String,Long> mapaIdAndError = new HashMap<String,Long>();
		
		try {
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress", 5);

			usuario = (Usuario) request.getSession().getAttribute("usuario");

			tipo = request.getParameter("tipo").charAt(0);

			LOGGER.debug("comprobamos si el fichero no esta importado");
			if (!importacionComisionesManager.ficheroImportado(ffcb)) {
				mapaIdAndError = importacionComisionesManager.crearFicheroImportado(ffcb, tipo, usuario, request);
				idFichero = mapaIdAndError.get(CLAVE_ID_FICHERO);
				
				if(mapaIdAndError.get(CLAVE_ID_ERROR)!=null){
					LOGGER.debug("Fichero importado con errores");
					request.getSession().setAttribute("progressStatus", ESTADO_AJAX_WARN);
					resultado.put("progressStatus", ESTADO_AJAX_WARN);					
				}else{
					LOGGER.debug("fichero importado correctamente");
					request.getSession().setAttribute("progressStatus", ESTADO_AJAX_DONE);
					resultado.put("progressStatus", ESTADO_AJAX_DONE);
				}
			} else {
				request.getSession().setAttribute("progressStatus", ESTADO_AJAX_ERROR_DUPLICADO);
				resultado.put("progressStatus", ESTADO_AJAX_ERROR_DUPLICADO);
			}

			resultado.put("id", idFichero);
			this.getWriterJSON(response, resultado);

		} catch (BusinessException be) {
			LOGGER.error("Se ha producido un error al cargar el fichero", be);
			request.getSession().setAttribute("progressStatus", ESTADO_AJAX_ERROR_GENERICO);
			this.getWriterJSON(response, resultado);
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al cargar el fichero", e);
			request.getSession().setAttribute("progressStatus", ESTADO_AJAX_ERROR_GENERICO);
		}

		LOGGER.debug("end - doCargar");
		return null;
	}

	public ModelAndView doDescargarFichero(HttpServletRequest request,
			HttpServletResponse response, FormFicheroComisionesBean ffcb)
			throws Exception {
		
		LOGGER.debug("init - doVerFichero");
		ModelAndView mv = null;
		String idFichero = "";
		/*
		try {
			idFichero = StringUtils.nullToString(request.getParameter("idFichero"));
			if (!idFichero.equals("")) {
				LOGGER.debug("Recuperamos el objeto fichero");
				FicheroMultContenido fichero = importacionComisionesManager.getFicheroMultContenido(new Long(idFichero));
				if (fichero.getContenido() != null) {
						String contenido = fichero.getContenido().toString();
						response.setContentType("text/xml");
						response.setHeader("Content-Disposition","attachment; filename=\""+ Arrays.toString(fichero.getFicheroMult().getNombreFichero().split(".zip"))+"\"");
						response.setHeader("cache-control", "no-cache");
						byte[] fileBytes = contenido.getBytes();
						ServletOutputStream outs = response.getOutputStream();
						outs.write(fileBytes);
						outs.flush();
						outs.close();
	
				}

			}
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al cargar el fichero", e);
		}*/
		return mv;

	}
	public ModelAndView doVerFichero(HttpServletRequest request,
			HttpServletResponse response, FormFicheroComisionesBean ffcb) throws Exception {
		
		Character tipo;
		ModelAndView mv = null;
		HashMap<String, String> parameters = new HashMap<String, String>();
		tipo = request.getParameter("tipo").charAt(0);
		parameters.put("idFichero", request.getParameter("idFichero"));
		parameters.put("origenLlamada", "importacionComisiones");
		parameters.put("nombreFichero", request.getParameter("nombreF"));
		parameters.put("estado", request.getParameter("estadoF"));
		parameters.put("fase", request.getParameter("numFase"));
		
		if (tipo.equals(new Character('D'))){
			mv= new ModelAndView("redirect:/ficheroDeudaAplazada.run").addObject("method", "doConsulta").addAllObjects(parameters);
		}
		request.getSession().removeAttribute("filtroFicheroCom");
		if (ffcb !=  null) {
			request.getSession().setAttribute("filtroFicheroCom", ffcb);
		}
		return mv;
	}
	
	/**
	 * Metodo que va a hallar una lista sin repeticiones de fichero filtrando
	 * por el codigoSituacion indicado
	 */
	/*
	private List<FicheroMult> hallarListadoImportacionesPocCodigoSit(
			List<FicheroMult> listimportaciones, Character codigoSituacion) {
		
		List<FicheroMult> listaimportaciones = new ArrayList<FicheroMult>();
		String nombreFicheroAnterior = "";
		String nombreSiguiente = "";
		for (int i = 0; i < listimportaciones.size(); i++) {
			FicheroMult fich = listimportaciones.get(i);
			if (i == 0) {
				nombreFicheroAnterior = listimportaciones.get(0).getNombreFichero();
			} else {
				nombreSiguiente = listimportaciones.get(i).getNombreFichero();
			}
			Set<ReglamentoProduccionEmitida> reglaProdEmitidaList = fich.getFicheroReglamentos();
			if (reglaProdEmitidaList != null && reglaProdEmitidaList.size() > 0) {
				
				Iterator<ReglamentoProduccionEmitida> reglaProdEmitidaIterator = reglaProdEmitidaList.iterator();
					
					while (reglaProdEmitidaIterator.hasNext()) {
						ReglamentoProduccionEmitida reglaProdEmitidaBean = reglaProdEmitidaIterator.next();
						
						Set<ReglamentoProduccionEmitidaSituacion> fichRegAplSit = reglaProdEmitidaBean.getReglamentoProduccionEmitidaSituacions();
						
						if (fichRegAplSit != null && fichRegAplSit.size() > 0) {
							Iterator<ReglamentoProduccionEmitidaSituacion> fiSiIterator = fichRegAplSit.iterator();
							ReglamentoProduccionEmitidaSituacion fichsi = fiSiIterator.next();
							if (fichsi.getCodigo() != null && fichsi.getCodigo().equals(codigoSituacion)
									&& (!nombreFicheroAnterior.equals(nombreSiguiente))) {
								
								Set<ReglamentoProduccionEmitidaSituacion> ficheroReglamentoAplicacionSituacions = new HashSet<ReglamentoProduccionEmitidaSituacion>(0);
								Set<ReglamentoProduccionEmitida> ficheroReglamentoAplicacion = new HashSet<ReglamentoProduccionEmitida>(0);
								
								ficheroReglamentoAplicacionSituacions.add(fichsi);
								reglaProdEmitidaBean.setReglamentoProduccionEmitidaSituacions(ficheroReglamentoAplicacionSituacions);
								ficheroReglamentoAplicacion.add(reglaProdEmitidaBean);
								
								fich.setFicheroReglamentos(ficheroReglamentoAplicacion);
								listaimportaciones.add(fich);
								break;
							}
						}
					}
				}
				nombreFicheroAnterior = fich.getNombrefichero();
			}
		if (listaimportaciones != null) {
			listimportaciones = listaimportaciones;

		}

		return listimportaciones;
	}*/

	public void setImportacionComisionesManager(ImportacionComisionesManager importacionComisionesManager) {
		this.importacionComisionesManager = importacionComisionesManager;
	}
}