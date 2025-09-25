package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.impl.IImportacionComisionesUnificadoDao;
import com.rsi.agp.core.jmesa.service.IFicheroUnificadoService;
import com.rsi.agp.core.managers.impl.ComisionesUnificadas.IImportacionComisionesUnificadoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroContenidoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ImportacionComisionesUnificadoController extends
		BaseMultiActionController {
	private static final Log LOGGER = LogFactory.getLog(ImportacionComisionesController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");	
	
	private IImportacionComisionesUnificadoManager importacionComisionesUnificadoManager;
	private IFicheroUnificadoService ficheroUnificadoService;//para jmesa
	private IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao;
	
	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, FicheroUnificado fichero)
			throws Exception {
		LOGGER.debug("init - doConsulta");
		Character tipo = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {
			if (!StringUtils.nullToString(request.getParameter("tipo")).equals(""))
				tipo = request.getParameter("tipo").charAt(0);
			String origenLlamada = request.getParameter("origenLlamada");
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			parametros.put("tipo", tipo);
			parametros.put("origenLlamada", origenLlamada);

			if(origenLlamada!=null && origenLlamada.compareTo(Constants.ORIGEN_LLAMADA_MENU_GENERAL)==0){
				if(request.getSession().getAttribute("filtroFichero")!=null){
					request.getSession().removeAttribute("filtroFichero");
				}
			}else if(origenLlamada!=null && origenLlamada.compareTo(Constants.ORIGEN_LLAMADA_CONSULTAR)==0){
				request.getSession().setAttribute("filtroFichero",fichero);
			}else {
				if(request.getSession().getAttribute("filtroFichero")!=null){
					fichero = (FicheroUnificado)request.getSession().getAttribute("filtroFichero");
				}
			}
			
			if(null==fichero.getTipoFichero()) {
				fichero.setTipoFichero(tipo);
			}
			if(fichero.getNombreFichero()!=null && fichero.getNombreFichero().equals("")) {
				fichero.setNombreFichero(null);
			}
			
			
				String tablaHTML = getTablaHtml(request, response, fichero, usuario, origenLlamada);

				if (tablaHTML == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = tablaHTML.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este
						// atributo
						request.setAttribute("consultaFicheroUnificadoTabla", tablaHTML);
				}
				
				
				
				mv = new ModelAndView("moduloComisionesUnificado/importacionComisionesUnificado", "fichero", fichero); //.addAllObjects(parametros);
	
		} catch (Exception be) {
			logger.error("Se ha producido un error", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
		}
		return mv.addAllObjects(parametros);
	}
		
	public ModelAndView doCargar(HttpServletRequest request,
			HttpServletResponse response,FicheroUnificado fichero)
			throws Exception {
		JSONObject resultado = new JSONObject();
		Map<String,Long> mapaIdAndError = new HashMap<String,Long>();
		Usuario usuario = null;
		Character tipo = null;
		Long idFichero = null;
		try {
			request.getSession().setAttribute("progressStatus", "UPLOADING");
			request.getSession().setAttribute("progress", 5);
			
			 MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest)request;
			 MultipartFile file=multipartRequest.getFile("file");
			 usuario = (Usuario) request.getSession().getAttribute("usuario");
			 tipo = request.getParameter("tipo").charAt(0);
			 
			 if(!importacionComisionesUnificadoManager.esFicheroYaImportado(file.getOriginalFilename())) {
				 mapaIdAndError = importacionComisionesUnificadoManager.procesaFichero(file, tipo, usuario, request);
					idFichero = mapaIdAndError.get(Constants.CLAVE_ID_FICHERO);
					
					if(mapaIdAndError.get(Constants.CLAVE_ID_ERROR)!=null){
						LOGGER.debug("Fichero importado con errores");
						request.getSession().setAttribute("progressStatus", Constants.ESTADO_AJAX_WARN);
						resultado.put("progressStatus", Constants.ESTADO_AJAX_WARN);				
					}else{
						LOGGER.debug("fichero importado correctamente");
						request.getSession().setAttribute("progressStatus", Constants.ESTADO_AJAX_DONE);
						resultado.put("progressStatus", Constants.ESTADO_AJAX_DONE);
					}
			 } else {
				 request.getSession().setAttribute("progressStatus", Constants.ESTADO_AJAX_ERROR_DUPLICADO);
				 resultado.put("progressStatus", Constants.ESTADO_AJAX_ERROR_DUPLICADO);
			}

			resultado.put("id", idFichero);
			this.getWriterJSON(response, resultado);

		
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al cargar el fichero", e);
			request.getSession().setAttribute("progressStatus", Constants.ESTADO_AJAX_ERROR_GENERICO);
		}
		
		return null;
	}

	public ModelAndView doBorrarFichero(HttpServletRequest request,	HttpServletResponse response, FicheroUnificado ficheroBean)throws Exception {
		LOGGER.debug("init - doBorrarFichero");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		String idFichero = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {

			idFichero = StringUtils.nullToString(request.getParameter("idFichero"));
			LOGGER.debug("Fichero que se va a eliminar: " + idFichero);
			parametros.put("origenLlamada", Constants.ORIGEN_LLAMADA_BORRAR);
			
			if (!idFichero.equals("")) {
				LOGGER.debug("Recuperamos el objeto fichero");
				FicheroUnificado fichero = importacionComisionesUnificadoManager.getFichero(new Long(idFichero));
				if (fichero != null) {
					LOGGER.debug("Comprobamos que el fichero no tenga fecha de cierre");
					if (fichero.getFechaCierre()==null) {
						LOGGER.debug("Damos de baja el fichero: " + fichero.getId());
						//TMR 30-05-2012 Facturacion
						importacionComisionesUnificadoManager.borrarFichero(fichero,usuario);
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
			mv = doConsulta(request, response, ficheroBean);

		} catch (BusinessException be) {
			logger.error("Se ha producido un error al borrar el fichero", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, ficheroBean);
		} catch (Exception be) {
			logger.error("Se ha producido un error al borrar el fichero", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, ficheroBean);
		}

		LOGGER.debug("end - doBorrarFichero");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doDescargarFichero(HttpServletRequest request,
			HttpServletResponse response, FicheroUnificado fichero)
			throws Exception {
		
		LOGGER.debug("init - doDescargarFichero");
		ModelAndView mv = null;
		String idFichero = "";
		try {
			idFichero = StringUtils.nullToString(request.getParameter("idFichero"));
			if (!idFichero.equals("")) {
				LOGGER.debug("Recuperamos el objeto ficheroUnificado");
				FicheroContenidoUnificado ficheroContenido = importacionComisionesUnificadoManager.getFicheroContenido(new Long(idFichero));
				if (ficheroContenido.getContenido() != null) {
						response.setContentType("text/xml");
						response.setHeader("Content-Disposition","attachment; filename=\""+ Arrays.toString(ficheroContenido.getFichero().getNombreFichero().split(".zip"))+"\"");
						response.setHeader("cache-control", "no-cache");
						byte[] fileBytes = ficheroContenido.getContenido().getBytes(1,  (int)ficheroContenido.getContenido().length());
						ServletOutputStream outs = response.getOutputStream();
						outs.write(fileBytes);
						outs.flush();
						outs.close();
	
				}

			}
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al cargar el fichero", e);
		}
		
		LOGGER.debug("final - doDescargarFichero");
		return mv;

	}
	
	private String getTablaHtml(HttpServletRequest request,
			HttpServletResponse response,
			FicheroUnificado ficheroUnificado, Usuario usuario,
			String origenLlamada) {

		List<BigDecimal> listaGrupoEntidades = usuario
				.getListaCodEntidadesGrupo();
		
		String tabla = ficheroUnificadoService.getTabla(request, response,
				ficheroUnificado, origenLlamada, listaGrupoEntidades,
				importacionComisionesUnificadoDao);
		return tabla;
	}
	
	public void setImportacionComisionesUnificadoManager(
			IImportacionComisionesUnificadoManager importacionComisionesUnificadoManager) {
		this.importacionComisionesUnificadoManager = importacionComisionesUnificadoManager;
	}

	public void setFicheroUnificadoService(IFicheroUnificadoService ficheroUnificadoService) {
		this.ficheroUnificadoService = ficheroUnificadoService;
	}

	public void setImportacionComisionesUnificadoDao(
			IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao) {
		this.importacionComisionesUnificadoDao = importacionComisionesUnificadoDao;
	}	
}