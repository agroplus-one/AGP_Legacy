/**
 * 
 */
package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.MtoFamiliasManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.familias.LineaFamilia;

/**
 * @author U028210
 *
 */
public class MtoFamiliasController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoFamiliasController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	// Dependencies
	private MtoFamiliasManager mtoFamiliasManager;


	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, LineaFamilia lineaFamilia) throws Exception {
		
		logger.debug("init - doConsulta");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<LineaFamilia> listFamilias = null;

		try {


			// Se incluye el filtro de busqueda en la sesion
			request.getSession().setAttribute("familiaBean", lineaFamilia);

			listFamilias = mtoFamiliasManager.listLineaGrupoNegocioFamilia(lineaFamilia);
			
			logger.debug("Linea de familia encontradas: " + listFamilias.size());

			
			parametros.put("listFamilias", listFamilias);
			parametros.put("totalListSize", listFamilias.size());

			mv = new ModelAndView("moduloTaller/mtoFamilias/mtoFamilias", "familiaBean", lineaFamilia);
			

		} catch (BusinessException be) {
			logger.debug("Se ha producido un error general: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new LineaFamilia());
		}
		
		
    	parametros.put("grupos", mtoFamiliasManager.getGrupos());
    	parametros.put("gruposNegocio", mtoFamiliasManager.getGruposNegocio());

		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	
	
	
	public ModelAndView doBorrar(HttpServletRequest request,HttpServletResponse response,LineaFamilia lineaGrupoNegocio) throws Exception {
		
		logger.debug("init - doBorrar");

		
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv =null;
		
		try{
			
			if (
				!StringUtils.nullToString(lineaGrupoNegocio.getId().getCodFamilia()).equals("") && 
				!StringUtils.nullToString(lineaGrupoNegocio.getId().getGrupoNegocio()).equals("") &&
				!StringUtils.nullToString(lineaGrupoNegocio.getId().getCodGrupoFamilia()).equals("") &&
				!StringUtils.nullToString(lineaGrupoNegocio.getId().getCodLinea()).equals("")
				){ 

				mtoFamiliasManager.borrarLineaFamilia(lineaGrupoNegocio);
				parametros.put("mensaje", "Linea de Familia eliminada correctamente");

			} else {
					
				parametros.put("alerta", "Los datos requeridos no están rellenos");

			}
		
			request.setAttribute("origenLlamada", "doBorrar");
			
			// Se obtiene el bean que se enviarÃ¡ en la redirecciÃ³n
			LineaFamilia sm = request.getSession().getAttribute("familiaBean") != null
					? (LineaFamilia) request.getSession().getAttribute("familiaBean")
					: new LineaFamilia();
					
			mv = doConsulta(request, response, sm).addAllObjects(parametros);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parametros.put("alerta", "Error inesperado al borrar la linea de familia");
			mv = doConsulta(request, response, lineaGrupoNegocio).addAllObjects(parametros);
			
		}catch (Exception e){
			logger.debug("Error inesperado al borrar la linea de familia", e);
			parametros.put("alerta", "Error inesperado al borrar la linea de familia");
			mv = doConsulta(request, response, lineaGrupoNegocio).addAllObjects(parametros);
		}
		
		logger.debug("end - doBorrar");

		return mv;
	}
	
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response,LineaFamilia lineaFamilia) throws Exception {
		
		logger.debug("init - doEditar");

		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv =null;
		
		// Recuperar y contruir el objeto inicial de linea de familia
		String grupoNegocioinicial = request.getParameter("grupoNegocioInicial");
		String codFamiliaInicial = request.getParameter("codFamiliaInicial");
		String grupoInicial = request.getParameter("grupoInicial");
		String lineaInicial = request.getParameter("lineaInicial");
		
		// contruir el objeto inicial de linea de familia
		LineaFamilia lineaFamiliaInicial = new LineaFamilia();
		lineaFamiliaInicial.getId().setCodFamilia(Long.valueOf(codFamiliaInicial));
		lineaFamiliaInicial.getId().setCodGrupoFamilia(grupoInicial);
		lineaFamiliaInicial.getId().setCodLinea(Long.valueOf(lineaInicial));
		lineaFamiliaInicial.getId().setGrupoNegocio(grupoNegocioinicial.charAt(0));
		lineaFamiliaInicial.getFamilia().setNomFamilia(lineaFamilia.getFamilia().getNomFamilia());
		
		try{
			
		

			if ( estanRellenosCampos(lineaFamilia) && estanRellenosCampos(lineaFamiliaInicial) ){
				
				mtoFamiliasManager.actualizarLineaFamilia(lineaFamiliaInicial, lineaFamilia, request);
				parametros.put("mensaje", "Modificación realizada correctamente");

			}else{
				
				parametros.put("alerta", "Error en la validación de los campos");
				
			}
			
			parametros.put("showModificar", "true");
			request.setAttribute("origenLlamada", "doEditar");

			
			mv = doConsulta(request, response, lineaFamilia).addAllObjects(parametros);
			
			
		}catch (BusinessException e) {
		
			logger.error("Se ha producido un error: " + e.getMessage());
			parametros.put("alerta", "Error inesperado en la edicion de linea de familia");
			mv = doConsulta(request, response, lineaFamiliaInicial).addAllObjects(parametros);
		
		}catch (Exception e){
			logger.debug("Error inesperado en la edicion de linea de familia", e);
			parametros.put("alerta", "Error inesperado en la edicion de linea de familia");
			mv = doConsulta(request, response, lineaFamiliaInicial).addAllObjects(parametros);
		}
		
		logger.debug("end - doEditar");

		return mv;       	   
	}
	
	

	public ModelAndView doAlta(HttpServletRequest request,HttpServletResponse response,LineaFamilia lineaFamilia) throws Exception {
		
		
		logger.debug("init - doAlta");

		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			
			if (estanRellenosCampos(lineaFamilia)){ 
				
				
				mtoFamiliasManager.altaFamilia(lineaFamilia);
				parametros.put("showModificar", "true");
				parametros.put("mensaje", bundle.getString("mensaje.alta.OK"));

			}else{
				
				parametros.put("alerta", bundle.getString("mensaje.alta.generico.KO") + ": No todos los campos están rellenos.");
				
			}
			
			request.setAttribute("origenLlamada", "doAlta");
			
			mv = doConsulta(request, response, lineaFamilia).addAllObjects(parametros);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.alta.generico.KO") + ": " + e.getMessage());
			mv = doConsulta(request, response, lineaFamilia).addAllObjects(parametros);
			
		}catch (Exception e){
			logger.error("Error inesperado en la edicion de familias", e);
			parametros.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, lineaFamilia).addAllObjects(parametros);
		}
		
		logger.debug("end - doAlta");

		return mv;       	   
	}
	
	
	private boolean estanRellenosCampos(LineaFamilia lineaFamilia) {
		logger.debug("estanRellenosCampos");

		if (
			!StringUtils.nullToString(lineaFamilia.getId().getCodFamilia()).equals("") && 
			!StringUtils.nullToString(lineaFamilia.getId().getGrupoNegocio()).equals("") &&
			!StringUtils.nullToString(lineaFamilia.getId().getCodGrupoFamilia()).equals("") &&
			!StringUtils.nullToString(lineaFamilia.getId().getCodLinea()).equals("") &&
			!StringUtils.nullToString(lineaFamilia.getFamilia().getNomFamilia()).equals("") 
			)
		{ 
			logger.debug("estanRellenosCampos: SÍ");

			return true;
			}
		
		logger.debug("estanRellenosCampos: NO");
		return false;
	}


	
	

	public void setMtoFamiliasManager(MtoFamiliasManager mtoFamiliasManager) {
		this.mtoFamiliasManager = mtoFamiliasManager;
	}

	
}
