package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.service.utilidades.IClaseDetalleGanadoService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ClaseDetalleGanadoController extends BaseMultiActionController {

	private IClaseDetalleGanadoService claseDetalleGanadoService;
	private IGenericoDao claseDetalleGanadoDao;
	
	@SuppressWarnings("unused")
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private final static String VACIO = "";
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, ClaseDetalleGanado claseDetalleGanadoBean) {
		logger.debug("init - ClaseDetalleController");
		// Obtiene el usuario de la sesión y su sesión
				final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
				// Map para guardar los parámetros que se pasarán a la jsp
				final Map<String, Object> parameters = new HashMap<String, Object>();

				ClaseDetalleGanado claseDetalleBusqueda = (ClaseDetalleGanado) claseDetalleGanadoBean;		
				String origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));			
				
				String vieneDeCargaClases = StringUtils.nullToString(request.getParameter("vieneDeCargaClases"));
				if(VACIO.equals(vieneDeCargaClases)){
					vieneDeCargaClases = StringUtils.nullToString((String)request.getAttribute("vieneDeCargaClases"));
				}
				
				parameters.put("vieneDeCargaClases", vieneDeCargaClases);
				request.setAttribute("vieneDeCargaClases", vieneDeCargaClases);	
				
				if (null==claseDetalleBusqueda.getClase().getId()) {
					if(null!=request.getParameter("detalleid")) {
						claseDetalleBusqueda.getClase().setId(new Long(request.getParameter("detalleid")));
					}
				}
				
				parameters.put("codlinea",
						request.getParameter("codlinea") == null ? claseDetalleBusqueda.getClase().getLinea().getCodlinea()
								: request.getParameter("codlinea"));
				parameters.put("fechaInicioContratacion", request.getParameter("fechaInicioContratacionGan"));
				
				// ---------------------------------------------------------------------------------
				ModelAndView mv = null;

				try {
					String tablaHTML = getTablaHtml(request, response,
							claseDetalleBusqueda, usuario, origenLlamada, vieneDeCargaClases);

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
							request.setAttribute("consultaClaseDetalleGanado", tablaHTML);
					}

					mv = new ModelAndView("moduloTaller/claseMto/claseDetalleGanado",
							"claseDetalleGanadoBean", claseDetalleBusqueda);
					mv.addAllObjects(parameters);
				} catch (Exception e) {
					logger.error("Error en doConsulta de ClaseDetalleGanadoController", e);
				}

				return mv;
	}
				
	private String getTablaHtml(HttpServletRequest request,
			HttpServletResponse response,
			ClaseDetalleGanado claseGanadoDetalleBean, Usuario usuario,
			String origenLlamada, String vieneDeCargaClases) {

		List<BigDecimal> listaGrupoEntidades = usuario.getListaCodEntidadesGrupo();
		
		String tabla = claseDetalleGanadoService.getTabla(request, response,
				claseGanadoDetalleBean, origenLlamada, vieneDeCargaClases, listaGrupoEntidades,
				claseDetalleGanadoDao);
		return tabla;
	}		
	
	
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, ClaseDetalleGanado claseDetalleGanadoBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String origenLlamada=null;
		try{	
			origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
			
			if (claseDetalleGanadoBean!=null){
				
				if(null!=claseDetalleGanadoBean.getId())claseDetalleGanadoBean.setId(null);
				
				parameters = claseDetalleGanadoService.insertOrUpdate(claseDetalleGanadoBean);
				if (!parameters.containsKey("alerta")){
					//Ha ido todo bien
					parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.alta.OK"));
				}
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
			}
		}
		catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
			
		}
		catch (Exception e){
			logger.debug("Error inesperado en el alta de la claseDetalleGanado", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
		}
		parameters.put("origenLlamada", origenLlamada);
		return doConsulta(request, response, claseDetalleGanadoBean).addAllObjects(parameters);   
}
	
	
public ModelAndView doModificar(HttpServletRequest request, HttpServletResponse response, ClaseDetalleGanado claseDetalleGanadoBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String origenLlamada=null;
		try{	
			origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
			
			if (claseDetalleGanadoBean!=null && claseDetalleGanadoBean.getId()!=null){
				parameters = claseDetalleGanadoService.insertOrUpdate(claseDetalleGanadoBean);
				if (!parameters.containsKey("alerta")){
					//Ha ido todo bien
					parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.edicion.OK"));
					
				}				
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
			}
		}
		catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
			
		}
		catch (Exception e){
			logger.debug("Error inesperado en el alta de la claseDetalleGanado", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
		}
		parameters.put("origenLlamada", origenLlamada);
		return doConsulta(request, response, claseDetalleGanadoBean).addAllObjects(parameters);   
}
	
	
	
	
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, ClaseDetalleGanado claseDetalleGanadoBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(claseDetalleGanadoBean.getId()).equals("")){
				
				claseDetalleGanadoService.bajaClaseDetalle(claseDetalleGanadoBean);
				parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.borrado.OK"));
				//para que no busque por el Id del registro eliminado, lo quitamos del bean antes de llamar a doconsulta
				claseDetalleGanadoBean.setId(null);
				//Modificamos también el módulo si viene inicalizado a ""
				if(VACIO.equals(claseDetalleGanadoBean.getCodmodulo())) {
					claseDetalleGanadoBean.setCodmodulo(null);
				}
			
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.borrado.KO"));
			}

			mv = doConsulta(request, response, claseDetalleGanadoBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.borrado.KO"));
			mv = doConsulta(request, response, claseDetalleGanadoBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de la clase de ganado", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.borrado.KO"));
			mv = doConsulta(request, response, claseDetalleGanadoBean).addAllObjects(parameters);
		}
		return mv;     
	}
	
		
	public ModelAndView doCambioMasivo(HttpServletRequest request, HttpServletResponse response, ClaseDetalleGanado claseDetalleGanadoBean) {
		logger.debug("init - ClaseDetalleController - doCambioMasivo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		String listaIdsMarcados_cm = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
		
		String tipoCapitalCheck = StringUtils.nullToString(request.getParameter("tipoCapitalCheck"));
		//Limit limit =(Limit)request.getSession().getAttribute("consultaClaseDetalleGanado_LIMIT");
		
		try{
			parameters = claseDetalleGanadoService.cambioMasivo(listaIdsMarcados_cm, claseDetalleGanadoBean, tipoCapitalCheck); 
			
		}catch (Exception e){
			logger.debug("Error inesperado en el Cambio Masivo de ClaseDetalle ", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
		
		}	
		//recuperamos el filtro de sesion para pasarlo al nuevo Bean del Cambio Masivo y 
		//no perder el filtro al volver
		ClaseDetalleGanado filtroClaseDetalleGanadoBean = claseDetalleGanadoService.getBeanFromLimit((Limit) request.getSession().getAttribute("consultaClaseDetalleGanado_LIMIT"));
		mv = doConsulta(request, response, filtroClaseDetalleGanadoBean).addAllObjects(parameters);
		
		logger.debug("end - ClaseDetalleController - doCambioMasivo");
		return mv;
	}
	
	
public ModelAndView doImportar(HttpServletRequest request, HttpServletResponse response, ClaseDetalleGanado claseDetalleGanadoBean) throws Exception{
		
		logger.info("init - doImportar");
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		 MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest)request;
			
		try{
			
			 MultipartFile file=multipartRequest.getFile("file");
			 
			 logger.debug("Nombre Fichero: " + file.getOriginalFilename());	
			  if (file == null || file.getSize() == 0) {
				  parametros.put("alerta", bundle.getString("mensaje.clase.detalleGanado.importacion.KO") + ". Fichero vacío");
			  }else {
				  this.claseDetalleGanadoService.importaFichero(parametros, file);				  			  
			  }
			  

		}catch (IOException ioe) {
			logger.error("Error al tratar el archivo de clases de detalle de ganado ", ioe);
			parametros.put("alerta", bundle.getString("mensaje.clase.detalleGanado.importacion.KO"));
		}catch (Exception ex) {
			logger.error("Error al tratar el archivo de clases de detalle de ganado ", ex);
			parametros.put("alerta", bundle.getString("mensaje.clase.detalleGanado.importacion.KO"));
		}
		
		mv = doConsulta(request, response, claseDetalleGanadoBean).addAllObjects(parametros);
		return mv;
	}

	
	
	public void setClaseDetalleGanadoService(
			IClaseDetalleGanadoService claseDetalleGanadoService) {
		this.claseDetalleGanadoService = claseDetalleGanadoService;
	}

	public void setClaseDetalleGanadoDao(IGenericoDao claseDetalleGanadoDao) {
		this.claseDetalleGanadoDao = claseDetalleGanadoDao;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
}