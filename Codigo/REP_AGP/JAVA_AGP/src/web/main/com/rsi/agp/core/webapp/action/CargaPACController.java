package com.rsi.agp.core.webapp.action;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
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
import com.rsi.agp.core.managers.impl.CargaPACManager;
import com.rsi.agp.core.managers.impl.EntidadManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.pac.FormPacCargasBean;
import com.rsi.agp.dao.tables.pac.PacCargas;

public class CargaPACController extends BaseMultiActionController {

	private static final Log logger = LogFactory.getLog(CargaPACController.class);
	private CargaPACManager cargaPACManager;
	private EntidadManager entidadManager;
	
	/**
	 * Método para obtener un listado de cargas de PAC
	 * @param request
	 * @param response
	 * @param formPacCargasBean
	 * @return
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, PacCargas pacCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		FormPacCargasBean formPacCargasBean = new FormPacCargasBean();
		Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		
		try {
			
			// Comprueba si se accede desde el menú general
			boolean accesoMenu = esNulo (pacCargasBean);
			
			// Almacena el bean de búsqueda en sesión si se ha realizado una búsqueda
			if (!accesoMenu) request.getSession().setAttribute("pacCargasBean", pacCargasBean);
			else request.getSession().removeAttribute("pacCargasBean");
			if (pacCargasBean == null) pacCargasBean = new PacCargas();

			// Carga el grupo de entidades asociadas al usuario si es de perfil 5
			List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
			if (grupoEntidades != null && !grupoEntidades.isEmpty()) pacCargasBean.setGrupoEntidades(grupoEntidades);

			// Se añaden los filtros de búsqueda por defecto dependiendo del tipo de usuario conectado			
			filtrosObligatoriosPorUsuario(pacCargasBean, parameters, usuario, formPacCargasBean);
			
			// Si se accede desde el menú lateral no se realiza búsqueda de cargas
			List<PacCargas> listaCargas = accesoMenu ? null : cargaPACManager.listarCargas(pacCargasBean);
			parameters.put("listCargasPAC",  listaCargas);
			
			// Si el usuario es perfil 5, se compruebas si se ha filtrado por código de entidad para no sobreescribir el valor
			if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) && pacCargasBean.getEntidad() == null) {
				pacCargasBean.setEntidad(usuario.getOficina().getId().getCodentidad());
				// Establece el nombre de la entidad
				pacCargasBean.setNomentidad(this.entidadManager.getEntidad(pacCargasBean.getEntidad()).getNomentidad());
			}
			
			// Se pasa a la pantalla la lista de ids para implementar el 'Marcar todos'
			parameters.put ("idsRowsTodos", StringUtils.toValoresSeparadosXComas(listaCargas, false, false).replaceAll(",", ";"));
			
		}
		catch(Exception be){
			
			logger.error("Se ha producido un error durante la consulta de archivos PAC", be);
			parameters.put("alerta", bundle.getString("mensaje.error.general"));
		}   
		
		Map<String, Object> mapModels = new HashMap<String, Object>();
		mapModels.put ("formPacCargasBean", formPacCargasBean);
		mapModels.put ("pacCargasBean", pacCargasBean);
		mapModels.put ("idsRowsChecked", request.getParameter("idsRowsChecked"));
		    
		ModelAndView mv = new ModelAndView("moduloTaller/cargaPAC/cargaPAC", mapModels);
		mv.addAllObjects(parameters);
		    
		return mv;
	}
	
	public ModelAndView doPaginar(HttpServletRequest request, HttpServletResponse response, PacCargas pacCargasBean) {
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		PacCargas pacCargasBeanBusqueda = (PacCargas) request.getSession().getAttribute("pacCargasBean");
		// Redirección a la pantalla
		return this.doConsulta(request, response, pacCargasBeanBusqueda);
	}

	/**
	 * Método para cargar un fichero de PAC en el sistema
	 * @param request
	 * @param response
	 * @param formPacCargasBean
	 * @return
	 */
	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, FormPacCargasBean formPacCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String resultadoCargaPac;
		
		try{
			if(!cargaPACManager.existeArchivoCargado(formPacCargasBean.getFile().getOriginalFilename())){
				Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
				
				// Si el usuario no es perfil 0 se valida que la E-S Mediadora introducida esté asociada a la(s) entidad(es) del usuario
				if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR) ||
					cargaPACManager.existeESMedEntUsuario(formPacCargasBean.getEntMed(), formPacCargasBean.getSubentMed(), usuario)) {
					resultadoCargaPac = cargaPACManager.cargarArchivoPAC(formPacCargasBean, usuario.getCodusuario());
					
					if (resultadoCargaPac.equals("")){
						// Si la carga ha sido correcta
						parameters.put("mensaje", bundle.getString("mensaje.cargaPAC.OK"));	
					}
					else{
						// Si ha habido un error, se añade la descripción del error devuelto por el SW al mensaje genérico de error.
				    	parameters.put("alerta", bundle.getString("mensaje.cargaPAC.KO") + resultadoCargaPac);
					}
				}
				else {
					parameters.put("alerta", bundle.getString("mensaje.cargaPAC.ESMedEntidadKO"));
				}
			} 
			else {
				parameters.put("alerta", bundle.getString("mensaje.cargaPAC.archivoYaCargado"));
			}
		}
		catch (BusinessException be){
			logger.error("Se ha producido un error durante la carga del archivo PAC", be);
			parameters.put("alerta", bundle.getString("mensaje.cargaPAC.KO"));
		}
		
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		PacCargas pacCargasBeanBusqueda = (PacCargas) request.getSession().getAttribute("pacCargasBean");
		// Redirección a la pantalla
		return this.doConsulta(request, response, pacCargasBeanBusqueda).addAllObjects(parameters);
	}
	
	/**
	 * Método para mostrar el contenido de un fichero de PAC cargado en el sistema
	 * @param request
	 * @param response
	 * @param formPacCargasBean
	 * @return
	 */
	public ModelAndView doVerContenidoArchivo(HttpServletRequest request, HttpServletResponse response, PacCargas pacCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try{
			
			Reader reader = cargaPACManager.getContenidoArchivoCargaPAC(pacCargasBean.getId());
			if (reader!=null){
				BufferedReader bufferedReader = new BufferedReader(reader);
				response.setHeader("Content-Disposition","attachment; filename=\""+ pacCargasBean.getNombreFichero() + "\"");
				
				PrintWriter pw = response.getWriter();
				String linea;
				
				while((linea = bufferedReader.readLine()) != null)
					pw.println(linea);
				
				pw.flush();
				pw.close();
				return null;
			}else{
				parameters.put("alerta", ResourceBundle.getBundle("agp").getString("mensaje.cargaPAC.NoEncontrado"));
			}
		}catch(Exception e){
			logger.error("Se ha producido un error durante la lectura del archivo PAC", e);
			parameters.put("alerta", ResourceBundle.getBundle("agp").getString("mensaje.cargaPAC.KO"));
		}
		
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		PacCargas pacCargasBeanBusqueda = (PacCargas) request.getSession().getAttribute("pacCargasBean");
		parameters.put("origenLlamada", "consulta");
		return this.doConsulta(request, response, pacCargasBeanBusqueda == null ? new PacCargas() : pacCargasBeanBusqueda).addAllObjects(parameters);
	}
	
	/**
	 * Método para eliminar un fichero de PAC con todos sus asegurados y parcelas
	 * @param request
	 * @param response
	 * @param formPacCargasBean
	 * @return
	 */
	public ModelAndView doEliminar(HttpServletRequest request, HttpServletResponse response, FormPacCargasBean formPacCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		
		try{
			// Obtiene los ids de carga a borrar (separados por ';') y crea la lista correspondiente 
			List<BigDecimal> listaIdsCargaPAC = StringUtils.asListBigDecimal(StringUtils.nullToString(request.getParameter("idsRowsChecked")), ";");
			// Si la lista contiene datos se procede al borrado
			if (listaIdsCargaPAC != null && !listaIdsCargaPAC.isEmpty()) {
				cargaPACManager.eliminarCargaPac(listaIdsCargaPAC);
				parameters.put("mensaje", bundle.getString("mensaje.cargaPAC.eliminar.OK"));
			}
			else {
				logger.error("La lista de ids de PAC está vacía");
				parameters.put("alerta", bundle.getString("mensaje.cargaPAC.eliminar.KO"));
			}
		}
		catch(Exception e){
			logger.error("Se ha producido un error durante el borrado del archivo PAC", e);
			parameters.put("alerta", bundle.getString("mensaje.cargaPAC.eliminar.KO"));
		}
		
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		PacCargas pacCargasBeanBusqueda = (PacCargas) request.getSession().getAttribute("pacCargasBean");
		parameters.put("origenLlamada", "consulta");
		return this.doConsulta(request, response, pacCargasBeanBusqueda == null ? new PacCargas() : pacCargasBeanBusqueda).addAllObjects(parameters);
	}
	
	/**
	 * @param pacCargasBean
	 * @param parameters
	 * @param usuario
	 * @throws BusinessException
	 */
	private void filtrosObligatoriosPorUsuario(PacCargas pacCargasBean,	Map<String, Object> parameters, Usuario usuario, FormPacCargasBean formPacCargasBean) throws BusinessException {
		// Si el usuario no es perfil 0 se filtra por entidad
		if (!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
			
			// Si el usuario no es perfil 5, se establecen los valores de entidad y nombre de entidad asociados a él
			if (!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) ) {
				pacCargasBean.setEntidad(usuario.getOficina().getId().getCodentidad());
				
			}
			// Establece el nombre de la entidad
			pacCargasBean.setNomentidad(pacCargasBean.getEntidad() != null ? this.entidadManager.getEntidad(pacCargasBean.getEntidad()).getNomentidad() : "");
			
			
			// Se pasa un parámetro a la pantalla para indicar que el campo 'Entidad' no se debe poder modificar (excepto para perfil 5)
			parameters.put("filtroEntidad", !usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR));
			
			// Carga el grupo de entidades asociadas al usuario si es de perfil 5
			List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
			parameters.put("grupoEntidades",StringUtils.toValoresSeparadosXComas(grupoEntidades, false, false));
			
			// Si no es perfil 0 ni 5, en el panel de carga se filtra la lupa de E-S Mediadora por la entidad del usuaario
			if (!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)) 
				parameters.put("entidadCarga", usuario.getSubentidadMediadora().getId().getCodentidad());
			
			// Si además es externo, se filtra por E-S Mediadora (en filtro y en carga)
			if (usuario.isUsuarioExterno()) {
				pacCargasBean.setEntMed(usuario.getSubentidadMediadora().getId().getCodentidad().intValue());
				pacCargasBean.setSubentMed(usuario.getSubentidadMediadora().getId().getCodsubentidad().intValue());
				formPacCargasBean.setEntMed(usuario.getSubentidadMediadora().getId().getCodentidad());
				formPacCargasBean.setSubentMed(usuario.getSubentidadMediadora().getId().getCodsubentidad());
				// Se para un parámetro a la pantalla para indicar que el campo 'E-S Medi.' no se debe poder modificar
				parameters.put("filtroMediador", true);
			}
			else {
				parameters.put("filtroMediador", false);
			}
		}
		else {
			parameters.put("filtroEntidad", false);
			parameters.put("filtroMediador", false);
		}
	}
	
	/**
	 * Devuelve un boolean indicando si el parámetro informado es nulo o si todos sus atributos que se incluyen en la búsqueda lo son
	 * @param pc
	 * @return
	 */
	private boolean esNulo (PacCargas pc) {
		return pc == null ? true : ((pc.getEntidad() == null && pc.getEntMed() == null && pc.getSubentMed() == null && pc.getPlan() == null && pc.getLinea() == null &&
				pc.getNombreFichero() == null));
	}
	
	public void setCargaPACManager(CargaPACManager cargaPACManager) {
		this.cargaPACManager = cargaPACManager;
	}

	public void setEntidadManager(EntidadManager entidadManager) {
		this.entidadManager = entidadManager;
	}
	
}
