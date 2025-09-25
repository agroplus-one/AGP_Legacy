package com.rsi.agp.core.webapp.action;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.rsi.agp.core.managers.impl.CargaCSVManager;
import com.rsi.agp.core.managers.impl.EntidadManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cvs.CvsCarga;
import com.rsi.agp.dao.tables.cvs.FormCsvCargasBean;

public class CargaCSVController extends BaseMultiActionController {

	private static final Log logger = LogFactory.getLog(CargaCSVController.class);
	private CargaCSVManager cargaCSVManager;
	private EntidadManager entidadManager;
	
	/**
	 * Método para obtener un listado de cargas CSV
	 * @param request
	 * @param response
	 * @param formCsvCargasBean
	 * @return
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CvsCarga csvCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		FormCsvCargasBean formCsvCargasBean = new FormCsvCargasBean();
		Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		
		try {
			
			// Comprueba si se accede desde el menú general
			boolean accesoMenu = esNulo (csvCargasBean);
			
			// Almacena el bean de búsqueda en sesión si se ha realizado una búsqueda
			if (!accesoMenu) request.getSession().setAttribute("csvCargasBean", csvCargasBean);
			else request.getSession().removeAttribute("csvCargasBean");
			if (csvCargasBean == null) csvCargasBean = new CvsCarga();

			// Carga el grupo de entidades asociadas al usuario si es de perfil 5
			List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
			if (grupoEntidades != null && !grupoEntidades.isEmpty()) csvCargasBean.setGrupoEntidades(grupoEntidades);

			// Se añaden los filtros de búsqueda por defecto dependiendo del tipo de usuario conectado			
			filtrosObligatoriosPorUsuario(csvCargasBean, parameters, usuario, formCsvCargasBean);
			
			// Si se accede desde el menú lateral no se realiza búsqueda de cargas
			List<CvsCarga> listaCargas = accesoMenu ? null : cargaCSVManager.listarCargas(csvCargasBean);
			parameters.put("listCargasCSV",  listaCargas);
			
			// Si el usuario es perfil 5, se compruebas si se ha filtrado por código de entidad para no sobreescribir el valor
			if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) && csvCargasBean.getEntidad() == null) {
				csvCargasBean.setEntidad(usuario.getOficina().getId().getCodentidad());
				// Establece el nombre de la entidad
				csvCargasBean.setNomentidad(this.entidadManager.getEntidad(csvCargasBean.getEntidad()).getNomentidad());
			}
			
			// Se pasa a la pantalla la lista de ids para implementar el 'Marcar todos'
			parameters.put ("idsRowsTodos", StringUtils.toValoresSeparadosXComas(listaCargas, false, false).replaceAll(",", ";"));
			
		}
		catch(Exception be){
			
			logger.error("Se ha producido un error durante la consulta de archivos CSV", be);
			parameters.put("alerta", bundle.getString("mensaje.error.general"));
		}   
		
		Map<String, Object> mapModels = new HashMap<String, Object>();
		mapModels.put ("formCsvCargasBean", formCsvCargasBean);
		mapModels.put ("csvCargasBean", csvCargasBean);
		mapModels.put ("idsRowsChecked", request.getParameter("idsRowsChecked"));
		    
		ModelAndView mv = new ModelAndView("moduloTaller/cargaCSV/cargaCSV", mapModels);
		mv.addAllObjects(parameters);
		    
		return mv;
	}
	
	public ModelAndView doPaginar(HttpServletRequest request, HttpServletResponse response, CvsCarga csvCargasBean) {
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		CvsCarga csvCargasBeanBusqueda = (CvsCarga) request.getSession().getAttribute("csvCargasBean");
		// Redirección a la pantalla
		return this.doConsulta(request, response, csvCargasBeanBusqueda);
	}

	/**
	 * Método para cargar un fichero CSV en el sistema
	 * @param request
	 * @param response
	 * @param formCsvCargasBean
	 * @return
	 */
	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, FormCsvCargasBean formCsvCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String resultadoCargaCsv;
		
		try{
			if(null != formCsvCargasBean && null != formCsvCargasBean.getFile()) {
				if(!cargaCSVManager.existeArchivoCargado(formCsvCargasBean.getFile().getOriginalFilename())){
					Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
					
					// Se valida si la Entidad subentidad mediadora existe
					if (cargaCSVManager.existeESMedEnt(formCsvCargasBean.getEntMed(), formCsvCargasBean.getSubentMed())) {
						if (cargaCSVManager.existePlanLinea(formCsvCargasBean.getPlan(), formCsvCargasBean.getLinea())) {
							resultadoCargaCsv = cargaCSVManager.cargarArchivoCSV(formCsvCargasBean, usuario.getCodusuario());
							
							if (resultadoCargaCsv.equals("")){
								// Si la carga ha sido correcta
								parameters.put("mensaje", bundle.getString("mensaje.cargaPAC.OK"));	
							}
							else{
								// Si ha habido un error, se añade la descripción del error devuelto por el SW al mensaje genérico de error.
						    	parameters.put("alerta", bundle.getString("mensaje.cargaPAC.KO") + resultadoCargaCsv);
							}
						} else {
					    	parameters.put("alerta", bundle.getString("mensaje.cargaCSV.PlanLineaKO"));
						}
					}
					else {
						parameters.put("alerta", bundle.getString("mensaje.cargaCSV.ESMedEntidadKO"));
					}
				} 
				else {
					parameters.put("alerta", bundle.getString("mensaje.cargaPAC.archivoYaCargado"));
				}
			}	
		}
		catch (BusinessException be){
			logger.error(be.getMessage(), be);
			parameters.put("alerta", bundle.getString("mensaje.cargaPAC.KO") + ". " + be.getMessage());
		}
		
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		CvsCarga csvCargasBeanBusqueda = (CvsCarga) request.getSession().getAttribute("csvCargasBean");
		// Redirección a la pantalla
		return this.doConsulta(request, response, csvCargasBeanBusqueda).addAllObjects(parameters);
	}
	
	/**
	 * Método para mostrar el contenido de un fichero CSV cargado en el sistema
	 * @param request
	 * @param response
	 * @param formCsvCargasBean
	 * @return
	 */
	public ModelAndView doVerContenidoArchivo(HttpServletRequest request, HttpServletResponse response, CvsCarga csvCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try{
			
			Reader reader = cargaCSVManager.getContenidoArchivoCargaCSV(csvCargasBean.getId());
			if (reader!=null){
				BufferedReader bufferedReader = new BufferedReader(reader);
				response.setHeader("Content-Disposition","attachment; filename=\""+ csvCargasBean.getNombreFichero() + "\"");
				
				PrintWriter pw = response.getWriter();
				String linea;
				
				while((linea = bufferedReader.readLine()) != null)
					pw.println(linea);
				
				pw.flush();
				pw.close();
				return null;
			}else{
				parameters.put("alerta", ResourceBundle.getBundle("agp").getString("mensaje.cargaCSV.NoEncontrado"));
			}
		}catch(Exception e){
			logger.error("Se ha producido un error durante la lectura del archivo CSV", e);
			parameters.put("alerta", ResourceBundle.getBundle("agp").getString("mensaje.cargaPAC.KO"));
		}
		
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		CvsCarga csvCargasBeanBusqueda = (CvsCarga) request.getSession().getAttribute("csvCargasBean");
		parameters.put("origenLlamada", "consulta");
		return this.doConsulta(request, response, csvCargasBeanBusqueda == null ? new CvsCarga() : csvCargasBeanBusqueda).addAllObjects(parameters);
	}
	
	/**
	 * Método para eliminar un fichero CSV con todos sus asegurados y parcelas
	 * @param request
	 * @param response
	 * @param formCsvCargasBean
	 * @return
	 */
	public ModelAndView doEliminar(HttpServletRequest request, HttpServletResponse response, FormCsvCargasBean formCsvCargasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		List<BigDecimal> listaIdsCargaCSV;
		
		try{
			// Obtiene los ids de carga a borrar (separados por ';') y crea la lista correspondiente 
			listaIdsCargaCSV = StringUtils.asListBigDecimal(StringUtils.nullToString(request.getParameter("idsRowsChecked")), ";");
			// Si la lista contiene datos se procede al borrado
			if (listaIdsCargaCSV != null && !listaIdsCargaCSV.isEmpty()) {
				cargaCSVManager.eliminarCargaCsv(listaIdsCargaCSV);
				parameters.put("mensaje", bundle.getString("mensaje.cargaPAC.eliminar.OK"));
			}
			else {
				logger.error("La lista de ids de CSV está vacía");
				parameters.put("alerta", bundle.getString("mensaje.cargaPAC.eliminar.KO"));
			}
		}
		catch(Exception e){
			logger.error("Se ha producido un error durante el borrado del archivo CSV", e);
			parameters.put("alerta", bundle.getString("mensaje.cargaPAC.eliminar.KO"));
		}
		
		// Obtiene el bean de búsqueda de sesión para que al volver a la pantalla se mantenga el filtro de búsqueda anterior
		CvsCarga csvCargasBeanBusqueda = (CvsCarga) request.getSession().getAttribute("csvCargasBean");
		parameters.put("origenLlamada", "consulta");
		
		listaIdsCargaCSV = null;
		parameters.put("idsRowsChecked", listaIdsCargaCSV);
		
		return this.doConsulta(request, response, csvCargasBeanBusqueda == null ? new CvsCarga() : csvCargasBeanBusqueda).addAllObjects(parameters);
	}
	
	/**
	 * @param csvCargasBean
	 * @param parameters
	 * @param usuario
	 * @throws BusinessException
	 */
	private void filtrosObligatoriosPorUsuario(CvsCarga csvCargasBean,	Map<String, Object> parameters, Usuario usuario, FormCsvCargasBean formCsvCargasBean) throws BusinessException {
		// Si el usuario no es perfil 0 se filtra por entidad
		if (!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
			
			// Si el usuario no es perfil 5, se establecen los valores de entidad y nombre de entidad asociados a él
			if (!usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) ) {
				csvCargasBean.setEntidad(usuario.getOficina().getId().getCodentidad());
				
			}
			// Establece el nombre de la entidad
			csvCargasBean.setNomentidad(csvCargasBean.getEntidad() != null ? this.entidadManager.getEntidad(csvCargasBean.getEntidad()).getNomentidad() : "");
			
			
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
				csvCargasBean.setEntMed(usuario.getSubentidadMediadora().getId().getCodentidad().intValue());
				csvCargasBean.setSubentMed(usuario.getSubentidadMediadora().getId().getCodsubentidad().intValue());
				formCsvCargasBean.setEntMed(usuario.getSubentidadMediadora().getId().getCodentidad());
				formCsvCargasBean.setSubentMed(usuario.getSubentidadMediadora().getId().getCodsubentidad());
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
	
	public void doDescargarArchivo(HttpServletRequest request, HttpServletResponse response) {
		String tipoArchivo = (String) request.getParameter("tipoArchivo");
		String filename;
		if (tipoArchivo.equals("plantilla")) {
			logger.info("Descarga de la plantilla CSV");
			filename = "PlantillaCSV.csv";
		} else {
			logger.info("Descarga del manual de instrucciones");
			filename = "Manual.pdf";
		}
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String ruta = bundle.getString("ruta.defecto.fichero.plantillaCSV");
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
		try (InputStream in = new FileInputStream(ruta + filename);
				OutputStream outputStream = response.getOutputStream()) {
			while (0 < (bytesRead = in.read(buffer))) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		} catch (FileNotFoundException e) {
			logger.error("*** doDescargarArchivo Error: No se encuentra el fichero " + filename, e);
		} catch (IOException e) {
			logger.error("Excepcion : CargaCSVController - doDescargarArchivo", e);
		}
		logger.info("CargaCSVController -- doDescargarArchivo");
	}
	
	/**
	 * Devuelve un boolean indicando si el parámetro informado es nulo o si todos sus atributos que se incluyen en la búsqueda lo son
	 * @param csv
	 * @return
	 */
	private boolean esNulo (CvsCarga cvs) {
		return cvs == null ? true : ((cvs.getEntidad() == null && cvs.getEntMed() == null && cvs.getSubentMed() == null && cvs.getPlan() == null && cvs.getLinea() == null &&
				cvs.getNombreFichero() == null));
	}
	
	public void setCargaCSVManager(CargaCSVManager cargaCSVManager) {
		this.cargaCSVManager = cargaCSVManager;
	}

	public void setEntidadManager(EntidadManager entidadManager) {
		this.entidadManager = entidadManager;
	}
	
}
