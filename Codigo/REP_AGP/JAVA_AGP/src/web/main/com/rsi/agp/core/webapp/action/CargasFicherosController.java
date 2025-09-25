package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.ICargasCondicionadoService;
import com.rsi.agp.core.jmesa.service.ICargasFicherosService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;

public class CargasFicherosController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(CargasFicherosController.class);
	private ICargasFicherosService cargasFicherosService;
	private ICargasCondicionadoService cargasCondicionadoService;
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza la consulta de cargas de condicionado
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CargasFicheros cargasFicherosBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = StringUtils.nullToString(request.getAttribute("origenLlamada"));

		if (origenLlamada.equals("")) {
			origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
		}
		// Es la primera vez que entra en el alta. no mostramos datos en la
		// tabla y borramos el idCondicionado de session
		if (origenLlamada.equals("cargasCondicionado")) {
			request.getSession().removeAttribute("idCondicionado");
			return new ModelAndView(successView, "cargasFicherosBean", cargasFicherosBean).addAllObjects(parameters);
		}

		if (origenLlamada.equals("cargasTablas")) {
			parameters.put("mensaje", bundle.getString(Constants.MENSAJE_FICHEROS_OK));
		}

		if (!StringUtils.nullToString(request.getAttribute("alerta")).equals(""))
			parameters.put("alerta", (String) request.getAttribute("alerta"));
		
		// Si tenemos idCondicionado filtramos los ficheros por el id, si no no
		// mostramos nada
		if (!StringUtils.nullToString(request.getSession().getAttribute("idCondicionado")).equals("")) {
			cargasFicherosBean.setCargasCondicionado(new CargasCondicionado());
			cargasFicherosBean.getCargasCondicionado().setId((Long) request.getSession().getAttribute("idCondicionado"));

			String html = cargasFicherosService.getTablaCargasFicheros(request, response, cargasFicherosBean, origenLlamada);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents;
					try {
						contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
					} catch (UnsupportedEncodingException e) {
						logger.error("Error:" + e);
					} catch (IOException e) {
						logger.error("Error:" + e);
					}

					return null;
				} else
					// Pasa a la jsp el código de la tabla a través de este
					// atributo
					request.setAttribute("listadoCargasFicheros", html);
			}
		}
		if (origenLlamada.equals("consulta")){
			parameters.put("modoConsulta", "true");
			parameters.put("origenLlamada", "consulta");
		}
		return new ModelAndView(successView, "cargasFicherosBean", cargasFicherosBean).addAllObjects(parameters);

	}

	/**
	 * Carga los ficheros en bbdd y los sube al servidor sftp
	 * 
	 * @param request
	 * @param response
	 * @param cargasFicherosBean
	 * @return
	 */
	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, CargasFicheros cargasFicherosBean) {

		logger.info("init - doCargar");

		List<String> errores = new ArrayList<String>();
		Long idFichero = null;
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		CargasCondicionado cargasCondicionado = null;

		Long idCondicionado = null;

		logger.info("Comprobamos que los campos linea,plan y tipo son correctos");
		errores = cargasFicherosService.validaCampos(cargasFicherosBean);

		if (errores.size() == 0) {

			// Si son correctos los subimos por ftp, si no error
			logger.info("guardamos los ficheros en el directorio /aplicaciones/AGP_AGROPLUS/INTERFACES/cargas_batch por ftp");
			try {
				// Subimos el txt
				if (null != cargasFicherosBean.getFile()) {
					logger.debug("Nombre Fichero: " + cargasFicherosBean.getFile().getOriginalFilename());
					cargasFicherosService.subeFicherosFTP(cargasFicherosBean.getFile(), request);
					request.getSession().setAttribute("fichtxt", cargasFicherosBean.getFile());
				}
				// subimos el zip
				if (null != cargasFicherosBean.getFile2()) { // zip
					logger.debug("Nombre Fichero: " + cargasFicherosBean.getFile2().getOriginalFilename());
					cargasFicherosService.subeFicherosFTP(cargasFicherosBean.getFile2(), request);
				}

				// si el idCondicionado es vacio creamos un objeto condicionado
				// para guardar los ficheros
				if (StringUtils.nullToString(request.getSession().getAttribute("idCondicionado")).equals("")) {

					cargasCondicionado = cargasCondicionadoService.saveCondicionado();
					idCondicionado = cargasCondicionado.getId();
					request.getSession().setAttribute("idCondicionado", idCondicionado);
				} else {
					idCondicionado = (Long) request.getSession().getAttribute("idCondicionado");
				}

				idFichero = cargasFicherosService.saveFichero(cargasFicherosBean, idCondicionado);
				parameters.put("idFichero", idFichero);

				mv = new ModelAndView("redirect:/cargasTablas.html").addObject("method", "doGetTablas").addAllObjects(parameters);

			} catch (BusinessException e) {
				logger.error("Error al guardar en bbdd" + e);
				request.setAttribute("origenLlamada", "errorValidaFicheros");
				request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_FTP));
				mv = doConsulta(request, response, new CargasFicheros());
			} catch (Exception e) {
				logger.error("Error subir los ficheros por FTP " + e);
				request.setAttribute("origenLlamada", "errorValidaFicheros");
				request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_FTP));
				mv = doConsulta(request, response, new CargasFicheros());
			}

		} else {
			request.setAttribute("origenLlamada", "errorValidaFicheros");
			request.setAttribute("alerta", errores.toString().substring(1, errores.toString().length() - 1));

			mv = doConsulta(request, response, cargasFicherosBean);
		}
		return mv;

	}

	/**
	 * Borra los ficheros de bbdd y del servidor por ftp
	 * 
	 * @param request
	 * @param response
	 * @param cargasFicherosBean
	 * @return
	 */
	public ModelAndView doBorrarFichero(HttpServletRequest request, HttpServletResponse response, CargasFicheros cargasFicherosBean) {

		ModelAndView mv = null;
		String idFichero = request.getParameter("idFichero");
		String nombreFichero = request.getParameter("nombreFichero");
		try {

			// Borra el fichero y sus tablas de bbdd. Tb borra el fichero del
			// servidor por ftp
			cargasFicherosService.borrarFichero(Long.parseLong(idFichero), nombreFichero);

		} catch (BusinessException e) {
			logger.error("Error al borrar el fichero " + e);
			request.setAttribute("origenLlamada", "errorValidaFicheros");
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_BORRADO_FICHERO_KO));
			mv = doConsulta(request, response, new CargasFicheros());
		} catch (Exception e) {
			logger.error("Error al borrar el fichero " + e);
			request.setAttribute("origenLlamada", "errorValidaFicheros");
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_BORRADO_FICHERO_KO));
			mv = doConsulta(request, response, new CargasFicheros());
		}

		request.setAttribute("mensaje", bundle.getObject(Constants.MENSAJE_BORRADO_FICHERO_OK));
		mv = doConsulta(request, response, new CargasFicheros());
		return mv;
	}

	/**
	 * Vuelve a la pantalla de cargas de condicionado
	 * 
	 * @param request
	 * @param response
	 * @param cargasFicherosBean
	 * @return
	 */
	public ModelAndView doSalir(HttpServletRequest request, HttpServletResponse response, CargasFicheros cargasFicherosBean) {

		// Al salir borramos de session el idCondicionado
		request.getSession().removeAttribute("idCondicionado");
		return new ModelAndView("redirect:/cargasCondicionado.run").addObject("method", "doConsulta");

	}

	/* Inyeccion de spring */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setCargasFicherosService(
			ICargasFicherosService cargasFicherosService) {
		this.cargasFicherosService = cargasFicherosService;
	}

	public void setCargasCondicionadoService(
			ICargasCondicionadoService cargasCondicionadoService) {
		this.cargasCondicionadoService = cargasCondicionadoService;
	}

}
