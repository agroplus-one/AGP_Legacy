package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.SocioManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;

public class SocioController  extends MultiActionController{

	private static final Log LOGGER = LogFactory.getLog(SocioController.class); 
	private SocioManager socioManager;
	private AseguradoManager aseguradoManager;
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public ModelAndView doAlta(HttpServletRequest request, 
			HttpServletResponse response, Socio  socioBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
		try{		
			LOGGER.debug("Inicio alta de Socios ");
			String displayPopUpPolizas = StringUtils.nullToString(request.getParameter("displayPopUpPolizas"));
			Socio socioBusqueda = socioManager.getSocio(socioBean.getId().getNif(), socioBean.getId().getIdasegurado());
			
			if (null == socioBusqueda){
				socioManager.altaSocio (displayPopUpPolizas, params, user, socioBean);
				params.put("idSocio", socioBean.getId().getIdasegurado());
				request.setAttribute("idSocio", socioBean.getId().getIdasegurado());
			} else {
				socioManager.saveSocio(socioBean);
				params.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
			}
			params.put("origenLlamada", "doAlta");
			mv = doConsulta(request, response, socioBean).addAllObjects(params);
			
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, new Socio()).addAllObjects(params); 
					
		}
		return mv;
		
	}
	public ModelAndView doConsulta(HttpServletRequest request, 
			HttpServletResponse response, Socio  socioBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		Long idAsegurado = null;
		try{		
			LOGGER.debug("Inicio consulta de Socios ");
			String idAseguradoAux = request.getParameter("idAsegurado");
			Long idSocio = (Long) request.getAttribute("idSocio");
			if (idSocio != null)
				params.put("idSocio", idSocio.toString());
			if (idAseguradoAux != null){
				 idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
				 socioBean.getId().setIdasegurado(idAsegurado);
			}
			final List<Socio> listaSocios = socioManager.getSocios(socioBean);
			params.put("listaSocios", listaSocios);
			params.put("asegurado", socioManager.getDatosAsegurado(socioBean.getId().getIdasegurado()));
			
			mv = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", 
					socioBean).addAllObjects(params);
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.error.grave"));
			mv = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", 
					new Socio()).addAllObjects(params);
		}
		return mv;
	}
	public ModelAndView doModificar(HttpServletRequest request, 
			HttpServletResponse response, Socio  socioBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
		try{
			socioManager.saveSocio(socioBean);
			params.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
			params.put("idSocio", socioBean.getId().getIdasegurado());
			request.setAttribute("idSocio", socioBean.getId().getIdasegurado());
			if (user.getAsegurado() != null){
				Set<Socio> listaSocios = user.getAsegurado().getSocios();
				List<Socio> listaSocios2 = new ArrayList<Socio>(user.getAsegurado().getSocios());
				
				//si el socio que estamos modificando es del asegurado de sesion, actualizamos el asegurado de sesion
				if (socioBean.getId().getIdasegurado().equals(user.getAsegurado().getId())){
					Iterator<Socio> it = listaSocios.iterator();
					while(it.hasNext()){
				    	Socio soc = (Socio) it.next();
				    	if (soc.getId().equals(socioBean.getId())){
							//si es este socio, lo elimino y lo vuelvo a poner
				    		listaSocios2.remove(soc);
							listaSocios2.add(socioBean);
						}
					}
				}
			}	
			mv = doConsulta(request, response, socioBean).addAllObjects(params);
		
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.modificacion.KO"));
			mv = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", 
					new Socio()).addAllObjects(params);
		}
		return mv;
	}
	public ModelAndView doImprimir(HttpServletRequest request, 
			HttpServletResponse response, Socio  socioBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		try{
			// se imprimen todos los socios independientemente del filtro
			Long idAsegurado = socioBean.getId().getIdasegurado();
			socioBean = new Socio();
			socioBean.getId().setIdasegurado(idAsegurado);
			//DAA 17/07/2013	no se imprimen en el informe aquellos socios dados de baja
			socioBean.setBaja('N');
			
			final List<Socio> listaSocios = socioManager.getSocios(socioBean);
			request.setAttribute("listaSocios", listaSocios);
			//DAA 17/07/2013	pasamos en la request el nombre del asegurado
			Asegurado asegurado = aseguradoManager.getAsegurado(socioBean.getId().getIdasegurado());
			request.setAttribute("nombreAsegurado", StringUtils.nullToString(asegurado.getFullName()));
			mv = new  ModelAndView("forward:/informes.html?method=doInformeSocios");
		
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.error.grave"));
			mv = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", 
					new Socio()).addAllObjects(params);
		}
		return mv;
	}
	public ModelAndView doBaja(HttpServletRequest request, 
			HttpServletResponse response, Socio  socioBean){
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
		try{
			String idAseguradoBorrar = StringUtils.nullToString(request.getParameter("idAseguradoBaja"));
			String cifSocioBorrar = StringUtils.nullToString(request.getParameter("nifcifBaja"));
			String displayPopUpPolizas = StringUtils.nullToString(request.getParameter("displayPopUpPolizas"));
			SocioId idSoc = new SocioId(cifSocioBorrar, new Long(idAseguradoBorrar));
			
			
			final List<PolizaSocio> listaSociosAsociados = socioManager.getPolizasByIdSocio(idSoc);
			
			if (null == listaSociosAsociados || listaSociosAsociados.isEmpty()){
				socioManager.dropDatoSocio(idSoc);	
				params.put("mensaje", bundle.getString("mensaje.baja.OK"));
			}else{
				socioManager.esSocioConSubvs(displayPopUpPolizas,params,listaSociosAsociados,
						idAseguradoBorrar,cifSocioBorrar);
			}
			socioManager.actualizaAsegSesion(user,socioBean);
			params.put("origenLlamada", "doBaja");
			Long idasegurado = socioBean.getId().getIdasegurado();
			socioBean = new Socio();
			socioBean.getId().setIdasegurado(idasegurado);
			mv = doConsulta(request, response, socioBean).addAllObjects(params);
			
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.baja.KO"));
			mv = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", 
					new Socio()).addAllObjects(params);
		}
		return mv;
		
	}
	public ModelAndView deshacerSocio(HttpServletRequest request, 
			HttpServletResponse response, Socio  socioBean){
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
		try{
			String idAseguradoDeshacer = StringUtils.nullToString(request.getParameter("idAseguradoBaja"));
			String cifSocioDeshacer = StringUtils.nullToString(request.getParameter("nifcifBaja"));
			Socio socioBaja = socioManager.getSocio(cifSocioDeshacer, new Long(idAseguradoDeshacer));
			String displayPopUpPolizas = StringUtils.nullToString(request.getParameter("displayPopUpPolizas"));
			
			if (socioBaja != null){
				
				socioManager.deshazSocio(displayPopUpPolizas,params,socioBean, user,
						idAseguradoDeshacer,cifSocioDeshacer,socioBaja);
			}else{
				params.put("alerta", bundle.getString("mensaje.deshacer.KO"));
			}
			params.put("origenLlamada", "deshacerSocio");
			mv = doConsulta(request, response, socioBean).addAllObjects(params);
			
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.deshacer.OK"));
			mv = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", 
					new Socio()).addAllObjects(params);
		}
		return mv;
		
	}
	
	/*protected final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object,
			final BindException exception) {
		
		final Map<String, Object> parameters = new HashMap<String, Object>();
		//final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		
		final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
		
		Socio socioBean = (Socio) object;
		Socio socioBusqueda = new Socio();
		
		final String codNifCif = socioBean.getId().getNif();
		final String tipoIdent = socioBean.getTipoidentificacion();	*/	
		
		//try {
			/*if (null != codNifCif && null != tipoIdent && !"".equals(codNifCif) && !"".equals(tipoIdent)
					&& !StringUtils.validaNifCif(tipoIdent, codNifCif)) {
				LOGGER.warn("El codigo NIF/CIF introducido no es correcto");
				return HTMLUtils.errorMessage("socioController", "El codigo NIF/CIF introducido no es correcto");
			}

			if (null != socioBean.getNumsegsocial() && !"".equals(socioBean.getNumsegsocial())) {
				socioBean.setAtp("SI");
			} else {
				socioBean.setAtp("NO");
			}*/

			/*// Parámetro fijo
			Long idAsegurado;
			try {
				idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));			
			} catch (final Exception e) {
				LOGGER.warn("No ha escogido ningún asegurado");
				return HTMLUtils.errorMessage("socioController", "No ha escogido ningún asegurado");
			}
		
			socioBean.getId().setIdasegurado(idAsegurado);
			socioBusqueda.getId().setIdasegurado(idAsegurado);
*/
			//parameters.put("idAsegurado", idAsegurado);
			// Fin parámetro fijo

			//final String operacion = request.getParameter("operacion");
			//if ("alta".equalsIgnoreCase(operacion)) 
			//{
				/*String displayPopUpPolizas = StringUtils.nullToString(request.getParameter("displayPopUpPolizas"));
				socioBusqueda = socioManager.getSocio(socioBean.getId().getNif(), idAsegurado);
				List<Poliza> listaPolizasSinGrabar = new ArrayList<Poliza>();
				boolean socioGrabado = false;
				if (null == socioBusqueda) 
				{
					listaPolizasSinGrabar = socioManager.getPolizasSinGrabarByIdAsegurado(idAsegurado);	
					
					if (listaPolizasSinGrabar != null && !listaPolizasSinGrabar.isEmpty()){
						if (displayPopUpPolizas.equals("true")){
							socioManager.saveSocio(socioBean);
							
							for (Poliza poliza : listaPolizasSinGrabar){
								PolizaSocio polizaSocio = new PolizaSocio();
								polizaSocio.setPoliza(poliza);
								polizaSocio.setSocio(socioBean);
								socioManager.savePolizaSocio(polizaSocio);
							}
							
							socioGrabado = true;												
						} else {
							String tableSociosSinGrabar = socioManager.getTablePolizasSinGrabar(listaPolizasSinGrabar, 
									bundle.getString("mensaje.poliza.socio.alta.polizasSinGrabar"));
							parameters.put("popUpPolizas", "true");
							parameters.put("tableInfoPolizasSinGrabar", tableSociosSinGrabar);
							parameters.put("operacion", operacion);
						}
					} else {
						socioManager.saveSocio(socioBean);							
						socioGrabado = true;
					}
					
					if (socioGrabado){
						parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
						//si el socio que estamos dando de alta es del asegurado de sesion, actualizamos el asegurado de sesion
						if ((user.getAsegurado() != null) && (idAsegurado.equals(user.getAsegurado().getId()))){
							user.setAsegurado(socioManager.getDatosAsegurado(idAsegurado));
						}	
					}					
				} else 
				{
					socioManager.saveSocio(socioBean);
					parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
				}
				socioBusqueda = new Socio();
				socioBusqueda.getId().setIdasegurado(idAsegurado);
				//socioBusqueda = new Socio();
*/			//} 
			//else if ("baja".equalsIgnoreCase(operacion)) 
			//{
				/*String idAseguradoBorrar = StringUtils.nullToString(request.getParameter("idAseguradoBaja"));
				String cifSocioBorrar = StringUtils.nullToString(request.getParameter("nifcifBaja"));
				String displayPopUpPolizas = StringUtils.nullToString(request.getParameter("displayPopUpPolizas"));
				SocioId idSoc = new SocioId(cifSocioBorrar, new Long(idAseguradoBorrar));
				List<Poliza> listaPolizasSinGrabar = new ArrayList<Poliza>();
				
				final List<PolizaSocio> listaSociosAsociados = socioManager.getPolizasByIdSocio(idSoc);
				
				if (null == listaSociosAsociados || listaSociosAsociados.isEmpty()) 
				{
					socioManager.dropDatoSocio(idSoc);	
					
					//si el socio que estamos dando de baja es del asegurado de sesion, actualizamos el asegurado de sesion
					if ((user.getAsegurado() != null) && (idAsegurado.equals(user.getAsegurado().getId()))){
						user.setAsegurado(socioManager.getDatosAsegurado(idAsegurado));
					}
					
					parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
				} 
				else 
				{
					//Si el socio tiene polizas con estado "Pendiente de validacion" y "grabacion provisional"
					//se muestra un pop-up con un listado de dichas polizas
					if (!displayPopUpPolizas.equals("true")){
						listaPolizasSinGrabar = socioManager.filtrarPolizasSinGrabarByIdAsegurado(listaSociosAsociados);
					}
						
					if ((!displayPopUpPolizas.equals("true")) && (listaPolizasSinGrabar.size() > 0)){
						String tableSociosSinGrabar = socioManager.getTablePolizasSinGrabar(listaPolizasSinGrabar, 
								bundle.getString("mensaje.poliza.socio.baja.polizasSinGrabar"));
						parameters.put("popUpPolizas", "true");
						parameters.put("tableInfoPolizasSinGrabar", tableSociosSinGrabar);
						parameters.put("idAseguradoBaja", idAseguradoBorrar);				
						parameters.put("nifcifBaja", cifSocioBorrar);
						parameters.put("operacion", operacion);
					} else {
						//Si el socio tiene polizas asociadas, no se borran físicamente los datos
						Socio socioBaja = socioManager.getSocio(cifSocioBorrar, new Long(idAseguradoBorrar));
						socioBaja.setBaja('S');
						
						//Si el socio tiene alguna subvencion asociada en un polizas con estado "Pendiente de validacion" y
						//"grabacion provisional", se borran dichas subvenciones y se muestra un aviso
						List<SubvencionSocio> listSubvencionSocios = new ArrayList<SubvencionSocio>();
						Set<SubvencionSocio> subvencionesAux = new HashSet<SubvencionSocio>(socioBaja.getSubvencionSocios());
						
						//Se recorre un set auxiliar y se copian a una lista las subvenciones que hay que borrar para evitar concurrencia
						Iterator<SubvencionSocio> it = subvencionesAux.iterator();
						while(it.hasNext()){
							SubvencionSocio subvSocio = it.next();
							if ((subvSocio.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)) ||
								(subvSocio.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL))){
								listSubvencionSocios.add(subvSocio);
								socioBaja.getSubvencionSocios().remove(subvSocio);
							}
						}
						
						for (SubvencionSocio subvAux:listSubvencionSocios){
							socioManager.dropSubvencionSocio(subvAux);
						}
						
						socioBaja.setSubvencionSocios(subvencionesAux);				
						socioManager.saveSocio(socioBaja);
						
						//Si el socio tiene alguna poliza asociada en estado "Pendiente de validacion" y
						//"grabacion provisional", se borra el socio de dichas polizas
						for (PolizaSocio polizaSocioAux: listaSociosAsociados){
							if ((polizaSocioAux.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)) ||
								(polizaSocioAux.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL))){
								socioManager.dropPolizaSocio(polizaSocioAux);
							}
						}					
						
						//si el socio que estamos dando de baja es del asegurado de sesion, actualizamos el asegurado de sesion
						if ((user.getAsegurado() != null) && (idAsegurado.equals(user.getAsegurado().getId()))){
							user.setAsegurado(socioManager.getDatosAsegurado(idAsegurado));
						}
						
						parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));	
					}		
					
				}				*/
			//}
			//else if ("deshacerSocio".equalsIgnoreCase(operacion)) 
			//{
				/*String idAseguradoDeshacer = StringUtils.nullToString(request.getParameter("idAseguradoBaja"));
				String cifSocioDeshacer = StringUtils.nullToString(request.getParameter("nifcifBaja"));
				Socio socioBaja = socioManager.getSocio(cifSocioDeshacer, new Long(idAseguradoDeshacer));
				List<Poliza> listaPolizasSinGrabar = new ArrayList<Poliza>();
				String displayPopUpPolizas = StringUtils.nullToString(request.getParameter("displayPopUpPolizas"));
				boolean socioRecuperado = false;
				
				if (socioBaja != null){
					listaPolizasSinGrabar = socioManager.getPolizasSinGrabarByIdAsegurado(new Long(idAseguradoDeshacer));	
					
					if (listaPolizasSinGrabar != null && !listaPolizasSinGrabar.isEmpty()){
						
						if (displayPopUpPolizas.equals("true")){
							for (Poliza poliza : listaPolizasSinGrabar){
								PolizaSocio polizaSocio = new PolizaSocio();
								polizaSocio.setPoliza(poliza);
								polizaSocio.setSocio(socioBaja);
								socioManager.savePolizaSocio(polizaSocio);
							}
							
							socioRecuperado = true;							
						} else {
							String tableSociosSinGrabar = socioManager.getTablePolizasSinGrabar(listaPolizasSinGrabar, 
									bundle.getString("mensaje.poliza.socio.alta.polizasSinGrabar"));
							parameters.put("popUpPolizas", "true");
							parameters.put("tableInfoPolizasSinGrabar", tableSociosSinGrabar);
							parameters.put("idAseguradoBaja", idAseguradoDeshacer);				
							parameters.put("nifcifBaja", cifSocioDeshacer);
							parameters.put("operacion", operacion);
						}
						
					} else {
						socioRecuperado = true;		
					}			
					
					if (socioRecuperado){
						socioBaja.setBaja('N');
						socioManager.saveSocio(socioBaja);
						parameters.put("mensaje", bundle.getString("mensaje.deshacer.OK"));
						
						//si el socio que se recupera es el asegurado de sesion, actualizamos el asegurado de sesion
						if ((user.getAsegurado() != null) && (idAsegurado.equals(user.getAsegurado().getId()))){
							user.setAsegurado(socioManager.getDatosAsegurado(idAsegurado));
						}
					}
					
				}
				else 
				{
					parameters.put("alerta", bundle.getString("mensaje.deshacer.KO"));
				}*/
			//} 
			//else if ("modificar".equalsIgnoreCase(operacion)) 
			/*{
				//idAsegurado = socioBean.getId().getIdasegurado();
				//socioBusqueda = socioManager.getSocio(socioBean.getId().getNif(), idAsegurado);
				
				if (socioBusqueda != null)
				{
					socioManager.saveSocio(socioBean);
					parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
					
					if (user.getAsegurado() != null){
						Set<Socio> listaSocios = user.getAsegurado().getSocios();
						List<Socio> listaSocios2 = new ArrayList<Socio>(user.getAsegurado().getSocios());
						
						//si el socio que estamos modificando es del asegurado de sesion, actualizamos el asegurado de sesion
						if (idAsegurado.equals(user.getAsegurado().getId())){
							Iterator<Socio> it = listaSocios.iterator();
							while(it.hasNext()){
						    	Socio soc = (Socio) it.next();
						    	if (soc.getId().equals(socioBean.getId())){
									//si es este socio, lo elimino y lo vuelvo a poner
						    		listaSocios2.remove(soc);
									listaSocios2.add(socioBean);
								}
							}
						}
					}					
				}
				//socioBusqueda = new Socio();			
			} 
			else if ("editar".equalsIgnoreCase(operacion)) 
			{
				final String nif = request.getParameter("nif");
				socioBean = socioManager.getSocio(nif, idAsegurado);
			}*/ 
			/*else if ("limpiar".equalsIgnoreCase(operacion)) 
			{
					
				socioBean = new Socio();
				socioBusqueda = new Socio();
				socioBusqueda.getId().setIdasegurado(idAsegurado);
				
			} else if ("imprimir".equalsIgnoreCase(operacion)) {
				//DAA 17/07/2013	no se imprimen en el informe aquellos socios dados de baja
				socioBean.setBaja('N');
				socioBean.setAtp("");
				final List<Socio> listaSocios = socioManager.getSocios(socioBean);
				request.setAttribute("listaSocios", listaSocios);
				//DAA 17/07/2013	pasamos en la request el nombre del asegurado
				Asegurado asegurado = aseguradoManager.getAsegurado(idAsegurado);
				request.setAttribute("nombreAsegurado", StringUtils.nullToString(asegurado.getFullName()));
				return new ModelAndView("forward:/informes.html?method=doInformeSocios");
				
			
			}else{
				socioBean.setAtp("");
				socioBusqueda = socioBean;			
			}*/

			//final List<Socio> listaSocios = socioManager.getSocios(socioBusqueda);
			//parameters.put("listaSocios", listaSocios);
			//parameters.put("asegurado", socioManager.getDatosAsegurado(idAsegurado));

		/*} catch (BusinessException e) {
			parameters.put("alerta", bundle.getString("mensaje.comisiones.KO"));
		}
		
		final ModelAndView resultado = new ModelAndView("moduloAdministracion/asegurados/socios", "socioBean", socioBean);
		resultado.addAllObjects(parameters);

		return resultado;
	}*/

	public final void setSocioManager(final SocioManager socioManager) {
		this.socioManager = socioManager;
	}

	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}
}
