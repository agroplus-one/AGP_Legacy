package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.ICPMTipoCapitalService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapital;
import com.rsi.agp.dao.tables.poliza.Linea;




public class CPMTipoCapitalController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(CPMTipoCapitalController.class);
	private ICPMTipoCapitalService cpmTipoCapitalService;
	
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza la consulta de cpmTipoCapital que se ajustan al filtro de búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param cpmTipoCapitalBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapitalBean) {
		logger.debug("init - CPMTipoCapitalController");
		
		// Obtiene el usuario de la sesión y su sesión
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		// Map para guardar los parámetros que se pasarán a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variable que almacena el código de la tabla de pólizas
		String html = null;
		CPMTipoCapital cpmTipoCapitalBusqueda = (CPMTipoCapital) cpmTipoCapitalBean;

		String origenLlamada = request.getParameter("origenLlamada");

		// ---------------------------------------------------------------------------------
		// -- Búsqueda de erroes WS y generación de la tabla de
		// presentación --
		// ---------------------------------------------------------------------------------
		logger.debug("Comienza la búsqueda de CPMTipoCapital");
		
		try{

			html = cpmTipoCapitalService.getTablaCPMTipoCapital(request, response, cpmTipoCapitalBusqueda, origenLlamada);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					// Pasa a la jsp el código de la tabla a través de este atributo
					request.setAttribute("consultaCPMTipoCapital", html);
			}
			
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			if (alerta != null) {
				parameters.put("alerta", alerta);
			}
			if (mensaje != null) {
				parameters.put("mensaje", mensaje);
			}
			parameters.put("perfil", perfil);
	
			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redirección y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);
			mv = new ModelAndView(successView, "cpmTipoCapitalBean", cpmTipoCapitalBean);
			mv.addAllObjects(parameters);
	
			logger.debug("end - CPMTipoCapitalController");
	
			return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : CPMTipoCapitalController - doConsulta", e);
		}
		return null;
	}

	/**
	 * Da de alta un registro de cpmTipoCapital
	 * 
	 * @param request
	 * @param response
	 * @param cpmTipoCapitalBean
	 *            Objeto que encapsula la información del cpmTipoCapital a dar de
	 *            alta
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapitalBean) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			ModelAndView mv = new ModelAndView(successView);
			try{
					if (cpmTipoCapitalBean!=null){
						parameters = cpmTipoCapitalService.altaCPMTipoCapital(cpmTipoCapitalBean);
						if(!parameters.containsKey("alerta")){
							parameters.put("mensaje", bundle.getString("mensaje.cpmTipoCapital.alta.OK"));
						}
						
					}else{
						parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.KO"));
					}
					if (cpmTipoCapitalBean.getSistemaCultivo()== null) {
						cpmTipoCapitalBean.setSistemaCultivo(new SistemaCultivo());
					}
					if (cpmTipoCapitalBean.getCicloCultivo()== null) {
						cpmTipoCapitalBean.setCicloCultivo(new CicloCultivo());
					}
					mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
					
			}catch (BusinessException e) {
				logger.error("Se ha producido un error: " + e.getMessage());
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.KO"));
				mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
				
			}catch (Exception e){
				logger.debug("Error inesperado en el alta de cpmTipoCapital", e);
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.alta.KO"));
				mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
			}
			return mv;   
	}

	/**
	 * Edita un registro de cpmTipoCapital
	 * 
	 * @param request
	 * @param response
	 * @param cpmTipoCapitalBean
	 *            Objeto que encapsula la información del cpmTipoCapital a editar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response, CPMTipoCapital cpmTipoCapitalBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			if (!StringUtils.nullToString(cpmTipoCapitalBean.getId()).equals("")){
				parameters = cpmTipoCapitalService.editaCPMTipoCapital(cpmTipoCapitalBean);
				
			}else{
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.edicion.KO"));
			}
			
			//ASF - 22/01/2014 - Reseteamos el valor del sistema de cultivo porque da problemas con hibernate al guardar
			if (cpmTipoCapitalBean.getSistemaCultivo() == null){
				cpmTipoCapitalBean.setSistemaCultivo(new SistemaCultivo());
			}
			if (cpmTipoCapitalBean.getCicloCultivo()== null) {
				cpmTipoCapitalBean.setCicloCultivo(new CicloCultivo());
			}
			mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.edicion.KO"));
			mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edición de cpmTipoCapital", e);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.edicion.KO"));
			mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
		}
		return mv;       	   
	}

	/**
	 * Borra un registro de cpmTipoCapital
	 * 
	 * @param request
	 * @param response
	 * @param cpmTipoCapitalBean
	 *            Objeto que encapsula la información del cpmTipoCapital a borrar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapitalBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(cpmTipoCapitalBean.getId()).equals("")){
				
				cpmTipoCapitalService.bajaCPMTipoCapital(cpmTipoCapitalBean.getId());
				parameters.put("mensaje", bundle.getString("mensaje.cpmTipoCapital.borrado.OK"));
				//le quitamos el id al objeto
				cpmTipoCapitalBean.setId(null);
			
			}else{
				parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.borrado.KO"));
			}
			mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.borrado.KO"));
			mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de cpmTipoCapital", e);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.borrado.KO"));
			mv = doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
		}
		return mv;     
	}
	
	/**
	 * Realiza la copia de todos los cpmTipoCapital de un plan/línea a otro
	 * @param request
	 * @param response
	 * @param cpmTipoCapitalBean
	 * @return
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapitalBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planDest = null;
		BigDecimal lineaDest = null;
		
		try {
			// Obtiene el plan/línea destino
			BigDecimal planOrig = cpmTipoCapitalBean.getCultivo().getLinea().getCodplan();
			BigDecimal lineaOrig = cpmTipoCapitalBean.getCultivo().getLinea().getCodlinea();
			planDest = new BigDecimal (request.getParameter("planreplica"));
			lineaDest = new BigDecimal (request.getParameter("lineareplica"));
			
			// Llamada al método que realiza la réplica
			logger.debug("Replicar los cpmTipoCapital del plan/linea " + planOrig + "/" + lineaOrig + " al" + planDest + "/" + lineaDest);
			parameters = cpmTipoCapitalService.replicar(planOrig, lineaOrig, planDest, lineaDest);
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar los cpmTipoCapital a otro plan/linea", e);
			parameters.put("alerta", bundle.getString("mensaje.cpmTipoCapital.replica.KO"));
		}
		
		// Redirección
		// Si ha ocurrido algún error, se vuelve a la pantalla con el filtro de búsqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan/línea destino
		if (!parameters.containsKey("alerta")) {
			cpmTipoCapitalBean = new CPMTipoCapital ();
			Linea linea = new Linea ();
			linea.setCodlinea(lineaDest);
			linea.setCodplan(planDest);
			cpmTipoCapitalBean.getCultivo().setLinea(linea);
		}
		
		return doConsulta(request, response, cpmTipoCapitalBean).addAllObjects(parameters);
	}
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automáticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacías
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }

	/**
	 * Setter de propiedad
	 * 
	 * @param cpmTipoCapitalService
	 */
	public void setCpmTipoCapitalService(ICPMTipoCapitalService cpmTipoCapitalService) {
		this.cpmTipoCapitalService = cpmTipoCapitalService;
	}

	/**
	 * Setter de propiedad
	 * 
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
}
