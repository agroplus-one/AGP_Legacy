package com.rsi.agp.core.webapp.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.IncidenciasComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;

@RequestMapping("/incidencias.html")
public class IncidenciasComisionesMultController extends BaseMultiActionController{
	
	private static final Log logger = LogFactory.getLog(IncidenciasComisionesMultController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IncidenciasComisionesManager incidenciasComisionesManager;		
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, FicheroMultIncidencias ficheroMultIncidenciasBean) throws Exception{
		logger.debug("init - doConsultaDeuda");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<FicheroMultIncidencias> listIncidencias = null;
		String idFichero = "";
		FicheroMult fichero = null;
		try {
			// recuperamos el tipo ya que es necesario para filtrar por el mismo en la siguiente pantalla
			if (request.getParameter("tipo") != null && !request.getParameter("tipo").equals("")){
				Character tipo=  request.getParameter("tipo").charAt(0);
				if (ficheroMultIncidenciasBean.getFicheroMult() != null)
					ficheroMultIncidenciasBean.getFicheroMult().setTipoFichero(tipo);
			}
			
			if (request.getParameter("idFichero") != null){
				idFichero = StringUtils.nullToString(request.getParameter("idFichero"));
			}
			
			if (!"".equals(idFichero)){
				
				fichero = incidenciasComisionesManager.getFicheroMultIncidencias(idFichero);
				if (fichero != null){
					ficheroMultIncidenciasBean.setFicheroMult(fichero);
				}	
			} 
			else {
				if ((ficheroMultIncidenciasBean != null) && (ficheroMultIncidenciasBean.getFicheroMult() != null)){
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					//Obtenemos la fecha de carga del fichero desde el formulario
					if (ficheroMultIncidenciasBean.getFicheroMult().getFechaCarga() == null){
						String fechacarga = request.getParameter("fechaCarga");
						if (!StringUtils.nullToString(fechacarga).equals("")) {
							ficheroMultIncidenciasBean.getFicheroMult().setFechaCarga(df.parse(fechacarga));
						}
					}
				}
			}				
			
			logger.debug("recuperamos el listado de incidencias del fichero y su estado");
			
			if (ficheroMultIncidenciasBean != null && ficheroMultIncidenciasBean.getOficina() == null){
				ficheroMultIncidenciasBean.setOficina("");
			}
			//si lo tenemos en session es que hemos pulsado el boton volver y mantenemos el filtro
			if (request.getSession().getAttribute("ficheroMultIncidenciaBean")!=null){
				FicheroMultIncidencias ficheroSesion = (FicheroMultIncidencias)request.getSession().getAttribute("ficheroMultIncidenciaBean");
				listIncidencias = incidenciasComisionesManager.getListFicherosMultIncidencias(ficheroSesion);
				
				mv = new ModelAndView("moduloComisiones/incidenciasMult","ficheroMultIncidenciaBean", ficheroSesion);
				//eliminamos el filtro de la sesion
				request.getSession().removeAttribute("ficheroMultIncidenciaBean");
			}else{
				listIncidencias = incidenciasComisionesManager.getListFicherosMultIncidencias(ficheroMultIncidenciasBean);
				mv = new ModelAndView("moduloComisiones/incidenciasMult","ficheroMultIncidenciaBean",ficheroMultIncidenciasBean);
			}
			
			logger.debug("listIncidencias tamano: " + listIncidencias.size());			
			
			parametros.put("listIncidencias", listIncidencias);
			parametros.put("totalListSize", listIncidencias.size());
			if (!StringUtils.nullToString(request.getParameter("estadoFichero")).equals(""))
				parametros.put("estadoFichero", StringUtils.nullToString(request.getParameter("estadoFichero")));
			else if (fichero != null) 
				parametros.put("estadoFichero", this.incidenciasComisionesManager.getEstadoFicheroMult(fichero));

									
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new FicheroMultIncidencias());
		}
		logger.debug("fin - doConsultaDeuda");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, FicheroMultIncidencias ficheroMultIncidenciaBean) throws Exception{
		logger.debug("init - doCargarDeuda");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		boolean ficheroCargado = false;		
		FicheroMult fichero = new FicheroMult();
		
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String fechaAceptacionFicheroStr = request.getParameter("fechaAceptacionFichero");
		Date fechaAceptacionFichero = sdf.parse(fechaAceptacionFicheroStr);
		
		try {	
			logger.debug("se comprueba si el fichero no es nulo");
			if ((ficheroMultIncidenciaBean.getFicheroMult() != null) || (ficheroMultIncidenciaBean.getFicheroMult().getId() != null)){
				
				fichero = incidenciasComisionesManager.getFicheroMultIncidencias(ficheroMultIncidenciaBean.getFicheroMult().getId().toString());
				
				if (fichero != null){
						
					logger.debug("se acepta el fichero siempre que no tenga ninguna incidencia de tipo erroneo");
					ficheroCargado = incidenciasComisionesManager.cargarFicheroMult(fichero, fechaAceptacionFichero);
					if (ficheroCargado){
						parametros.put("mensaje", bundle.getString("mensaje.comisiones.incidencia.cargar.OK"));
					} else {
						parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
					}
	
				} else {
					parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
				}					
						
			} else {
				parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
			}			
			
			mv = doConsulta(request, response, ficheroMultIncidenciaBean);
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new FicheroMultIncidencias());
		}
			
		logger.debug("end - doCargar");
		return mv.addAllObjects(parametros);
	}
	
	/*public ModelAndView doVerificar(HttpServletRequest request, HttpServletResponse response, FicheroIncidencia ficheroIncidenciaBean) throws Exception{
		logger.debug("init - doVerificar");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		Fichero fichero = new Fichero(); 
			
		try {	
			if ((ficheroIncidenciaBean.getFichero() != null) || (ficheroIncidenciaBean.getFichero().getId() != null)){				
				
				fichero = incidenciasComisionesManager.getFicheroIncidencias(ficheroIncidenciaBean.getFichero().getId().toString());
				
				if (fichero != null){
					incidenciasComisionesManager.verificarTodos(fichero);
					parametros.put("mensaje", bundle.getString("mensaje.comisiones.incidencia.cargar.Verificado"));
				} else {
					parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
				}
			} else {
				parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
			}
			
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, ficheroIncidenciaBean);
		}
			
		logger.debug("end - doVerificar");
		return mv.addAllObjects(parametros);
	}*/
	
	public ModelAndView doVerificar(HttpServletRequest request, HttpServletResponse response, FicheroMultIncidencias ficheroMultIncidenciaBean) throws Exception{
		logger.debug("init - doVerificar");
		
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();	
		FicheroMult fichero = new FicheroMult(); 
			
		try {	
			if ((ficheroMultIncidenciaBean.getFicheroMult() != null) || (ficheroMultIncidenciaBean.getFicheroMult().getId() != null)){				
				
				fichero = incidenciasComisionesManager.getFicheroMultIncidencias(ficheroMultIncidenciaBean.getFicheroMult().getId().toString());
				
				if (fichero != null){
					incidenciasComisionesManager.verificarTodosMult(fichero);
					parametros.put("mensaje", bundle.getString("mensaje.comisiones.incidencia.cargar.Verificado"));
				} else {
					parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
				}
			} else {
				parametros.put("alerta", bundle.getString("mensaje.comisiones.incidencia.cargar.Erroneo"));
			}
			
			mv = doConsulta(request, response, ficheroMultIncidenciaBean);
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, ficheroMultIncidenciaBean);
		}
			
		logger.debug("end - doVerificar");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doImprimir(HttpServletRequest request, HttpServletResponse response, FicheroMultIncidencias ficheroMultIncidenciaBean) throws Exception{
		List<FicheroMultIncidencias> listIncidencias = null;
		
		logger.debug("recuperamos el listado de incidencias del fichero y su estado");		
		listIncidencias = incidenciasComisionesManager.getListFicherosMultIncidencias(ficheroMultIncidenciaBean);
		logger.debug("listIncidencias size: " + listIncidencias.size());			
		
		request.setAttribute("listIncidencias", listIncidencias);
		
		return new ModelAndView("forward:/informes.html?method=doInformeIncidencias");
	}
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. 
     * En MultiActionController no se hace este bind automáticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacías
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }

	public void setIncidenciasComisionesManager(IncidenciasComisionesManager incidenciasComisionesManager) {
		this.incidenciasComisionesManager = incidenciasComisionesManager;
	}
}