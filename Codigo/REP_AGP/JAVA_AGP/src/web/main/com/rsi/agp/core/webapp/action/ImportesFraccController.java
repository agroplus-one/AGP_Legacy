package com.rsi.agp.core.webapp.action;

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

import com.rsi.agp.core.jmesa.dao.IImportesFraccDao;
import com.rsi.agp.core.jmesa.service.IGetTablaService;
import com.rsi.agp.core.managers.impl.SubentidadMediadoraManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.commons.ImporteFraccFiltro;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ImportesFraccController extends BaseMultiActionController{
	
	// Constantes
	private static final String ALERTA_IMPORTEFRACC_LINEA_KO = "alerta.importefracc.linea.ko";
	private static final String ALERTA = "alerta";
	private static final String MENSAJE = "mensaje";
	private static final String PCT_RECARGO = "pctRecargo";
	private static final String IMPORTE = "importe";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD = "subentidadMediadora.id.codsubentidad";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODENTIDAD = "subentidadMediadora.id.codentidad";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	
	private static final Log logger = LogFactory.getLog(ImportesFraccController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp"); 
	 
	// Declaraciones
	private IGetTablaService importesFraccService;
	private IImportesFraccDao importesFraccDao;
	private SubentidadMediadoraManager subentidadMediadoraManager;
	
	public void setImportesFraccService(IGetTablaService importesFraccService) {
		this.importesFraccService = importesFraccService;
	}

	public void setImportesFraccDao(IImportesFraccDao importesFraccDao) {
		this.importesFraccDao = importesFraccDao;
	}
	

	// ----------------------------------------------------------------


	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, ImporteFraccionamiento importesFraccBean) {

		ModelAndView mv = null;

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		/*
		 * Recoge los parámetros según el perfil y
		 * guarda también éste
		 */		
		Map<String, Object> parameters = this.getParametrosEntidadUsuarioPorPerfil(usuario);
		asignaPorperfil(usuario, importesFraccBean);
		
		String origenLlamada = request.getParameter(ORIGEN_LLAMADA);

		parameters.put(ORIGEN_LLAMADA, origenLlamada);
		
		try {
			if(null!=origenLlamada && origenLlamada.equals(Constants.ORIGEN_LLAMADA_PAGINACION)) {
				if (request.getParameter("id") != null && request.getParameter("id").length() != 0){
					importesFraccBean.setId(new Long(request.getParameter("id")).longValue());
				}
				if (request.getParameter(LINEA_CODPLAN) != null && request.getParameter(LINEA_CODPLAN).length() != 0){
					importesFraccBean.getLinea().setCodplan(new BigDecimal(request.getParameter(LINEA_CODPLAN)));
				}
				if (request.getParameter(LINEA_CODLINEA) != null && request.getParameter(LINEA_CODLINEA).length() != 0){
					importesFraccBean.getLinea().setCodlinea(new BigDecimal(request.getParameter(LINEA_CODLINEA)));
				}			
			
				if (request.getParameter(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD)!=null && request.getParameter(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD).length()!=0){
					importesFraccBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal(request.getParameter(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD)));
				}
				if (request.getParameter(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD)!=null && request.getParameter(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD).length()!=0){
					importesFraccBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(request.getParameter(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD)));
				}
				
				if (request.getParameter(IMPORTE) != null && request.getParameter(IMPORTE).length() != 0){
					importesFraccBean.setImporte(new BigDecimal(request.getParameter(IMPORTE)));
				}
				if (request.getParameter("tipo") != null && request.getParameter("tipo").length() != 0){
					importesFraccBean.setTipo(new Integer(request.getParameter("tipo")));
				}
				if (request.getParameter(PCT_RECARGO) != null && request.getParameter(PCT_RECARGO).length() != 0){
					importesFraccBean.setPctRecargo(new BigDecimal(request.getParameter(PCT_RECARGO)));
				}
				
			}
			
			// Se incluye el filtro de busqueda en la sesion
			//request.getSession().setAttribute("importesFraccBean", importesFraccBean);
			
			//String listImportesFraccHTML = getTablaHtml(request, response, importesFraccBean, usuario, origenLlamada);
			
			if (null == origenLlamada || !origenLlamada.equalsIgnoreCase(Constants.ORIGEN_LLAMADA_MENU_GENERAL)) {
				String listImportesFraccHTML = getTablaHtml(request, response, importesFraccBean, usuario, origenLlamada);
				if (listImportesFraccHTML == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = listImportesFraccHTML.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este
						// atributo
						request.setAttribute("consultaImportesFracc", listImportesFraccHTML);
				}
				
				mv = new ModelAndView("moduloTaller/mtoImportesFrac/mtoImportesFrac",
						"importesFraccBean", importesFraccBean).addAllObjects(parameters);
			}else {
				mv = new ModelAndView("moduloTaller/mtoImportesFrac/mtoImportesFrac",
						"importesFraccBean", importesFraccBean)
						.addAllObjects(parameters);
			}

		} catch (Exception e) {
			logger.error("Error en doConsulta de ImportesFracc", e);
		}

		return mv;
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView doAlta(HttpServletRequest request,HttpServletResponse response, ImporteFraccionamiento importesFraccBean) throws Exception {
		logger.debug("init - doAlta");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		LineasFiltro filtro = new LineasFiltro(importesFraccBean.getLinea().getCodplan(), importesFraccBean.getLinea().getCodlinea());
		Integer existe = importesFraccDao.getNumObjects(filtro);
		
		if (existe == 1){
		
			//LineasFiltro filtro = new LineasFiltro(importesFraccBean.getLinea().getCodplan(), importesFraccBean.getLinea().getCodlinea());
			
			List <Linea> linea = importesFraccDao.getObjects(filtro);
			boolean SmInformada=null!=importesFraccBean.getSubentidadMediadora().getId().getCodentidad();
			ImporteFraccFiltro filter=null;
			if(SmInformada){
				 filter = new ImporteFraccFiltro(linea.get(0),importesFraccBean.getSubentidadMediadora());
			}else{
				 filter = new ImporteFraccFiltro(linea.get(0));
			}
			Integer count = importesFraccDao.getNumObjects(filter);
			
			if (count == 0){
				logger.debug("alta importeFraccionamiento: " + importesFraccBean.getTipo() + "," + importesFraccBean.getImporte()+ "," + importesFraccBean.getLinea().getLineaseguroid());
				
				if(linea.isEmpty() == false){
					logger.debug("alta importeFraccionamiento: " + importesFraccBean.getTipo() + "," + importesFraccBean.getImporte()+ "," + importesFraccBean.getLinea().getLineaseguroid());
					importesFraccBean.setLinea(linea.get(0));
					importesFraccBean.getLinea().setLineaseguroid(linea.get(0).getLineaseguroid());
					//Creamos un objeto nuevo porque Hibernate está haciendo un update cuando el objeto viene de editar
					ImporteFraccionamiento impF=new ImporteFraccionamiento(linea.get(0),importesFraccBean.getImporte(),importesFraccBean.getSubentidadMediadora(), 
							importesFraccBean.getTipo(), importesFraccBean.getPctRecargo());
					
					if(!SmInformada){
						importesFraccDao.saveOrUpdate(impF);
						parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.anadido.ok"));
					}else{
						if(subentidadMediadoraManager.existeRegistro(importesFraccBean.getSubentidadMediadora())){
							importesFraccDao.saveOrUpdate(impF);
							parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.anadido.ok"));
						}else{
							parameters.put(ALERTA, bundle.getString("mensaje.importefracc.entidad.noexiste"));
						}
					}
					
					
				
				}else{
					parameters.put(ALERTA, bundle.getString(ALERTA_IMPORTEFRACC_LINEA_KO));
				}
			}else{
				parameters.put(ALERTA, bundle.getString("mensaje.importeFracc.linea.existe"));
			}
		}else{
			parameters.put(ALERTA, bundle.getString(ALERTA_IMPORTEFRACC_LINEA_KO));
		}
		mv = doConsulta(request, response, importesFraccBean);
		logger.debug("end - doAlta");
		parameters.put(ORIGEN_LLAMADA, Constants.ORIGEN_LLAMADA_ALTA);
		return mv.addAllObjects(parameters);
	}
	
	
	@SuppressWarnings("unchecked")
	public ModelAndView doEdita(HttpServletRequest request,HttpServletResponse response, ImporteFraccionamiento importesFraccBean) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;

		LineasFiltro filtro = new LineasFiltro(importesFraccBean.getLinea().getCodplan(), importesFraccBean.getLinea().getCodlinea());

		Integer existe = importesFraccDao.getNumObjects(filtro);
		
		if (existe == 1){//la linea esta disponible
			List <Linea> linea = importesFraccDao.getObjects(filtro);
			importesFraccBean.setLinea(linea.get(0));//es necesario para poder guardar
			ImporteFraccFiltro filter = new ImporteFraccFiltro(linea.get(0),importesFraccBean.getSubentidadMediadora());

			Integer count = importesFraccDao.getNumObjects(filter);
			List<ImporteFraccionamiento> importe=importesFraccDao.getObjects(filter);
			try{
				if (count == 0){// no existe el registro linea-sm, comprobar que  la sm dada es buena
					boolean SmInformada=null!=importesFraccBean.getSubentidadMediadora().getId().getCodentidad();
						if(!SmInformada ||subentidadMediadoraManager.existeRegistro(importesFraccBean.getSubentidadMediadora())){//la sm informada es valida
							importesFraccDao.saveOrUpdate(importesFraccBean);
							parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.modificado.ok"));
							mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
						}
						if(SmInformada && !(subentidadMediadoraManager.existeRegistro(importesFraccBean.getSubentidadMediadora()))){
							parameters.put(ALERTA, bundle.getString("mensaje.importefracc.entidad.noexiste"));
							mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
						}

					
				}else{//si existe el registro linea-sm, por lo que no es necesario comprobar que la sm informada es valida
				  	 //pero debemos comprobar que no se este intentando modificar con un linea-sm repetido.
				  	if(importe.get(0).getId()==importesFraccBean.getId()){
				  		importesFraccDao.evict(importe.get(0));
				  		importe=null;
				  		importesFraccDao.saveOrUpdate(importesFraccBean);
				  		parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.modificado.ok"));
				  		mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
				  	}else{
				  		parameters.put(ALERTA, bundle.getString("mensaje.importeFracc.linea.existe"));
				  		mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
					}
				  	
				}
		
		}catch (Exception e){
			logger.debug("Error inesperado en la edicion de ImporteFraccionamiento", e);
			parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.modificado.ko"));
			mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
		}
		}else{
			parameters.put(ALERTA, bundle.getString(ALERTA_IMPORTEFRACC_LINEA_KO));
			mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
		}
		parameters.put(ORIGEN_LLAMADA, Constants.ORIGEN_LLAMADA_MODIFICAR);
		return mv.addAllObjects(parameters);       	   
	}
	
	/**
	 * Realiza la baja del importeFraccionado
	 * @param request
	 * @param response
	 * @param ImporteFraccionamiento Objeto que encapsula el importe a dar de baja
	 * @return ModelAndView que contiene la redireccion a la pagina de importes fraccionados
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, ImporteFraccionamiento importesFraccBean) {
		
		ModelAndView mv = null;
		logger.debug("init - doBaja en importesFraccController");
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		if (request.getParameter("id") != null && request.getParameter("id").length() != 0){
			importesFraccBean.setId(new Long(request.getParameter("id")));
		}
		
		try{
				if (importesFraccBean != null){
					//importesFraccDao.delete(importesFraccBean);
					importesFraccDao.delete(ImporteFraccionamiento.class, importesFraccBean.getId());
					parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.borrado.ok"));
					importesFraccBean.getLinea().setLineaseguroid(null);
				}	

				mv = doConsulta(request, response,importesFraccBean).addAllObjects(parameters);
	
		}
    	catch (Exception e) {
    		
			logger.error("doBaja : Se ha producido un error a dar de baja un importe fraccionado " + e);
			parameters.put(MENSAJE, bundle.getString("mensaje.importefracc.borrado.ko"));
			mv = doConsulta(request, response, importesFraccBean).addAllObjects(parameters);
		
    	}
		
    	logger.debug("end - doBaja en importesFraccController");
		return mv;
	}
	
	private String getTablaHtml(HttpServletRequest request,
			HttpServletResponse response,
			ImporteFraccionamiento importeFraccBean, Usuario usuario,
			String origenLlamada) {

		List<BigDecimal> listaGrupoEntidades = usuario.getListaCodEntidadesGrupo();
		
		String tabla = importesFraccService.getTabla(request, response,
				importeFraccBean, origenLlamada, listaGrupoEntidades,
				importesFraccDao);
		return tabla;
	}
	
	//Procedimiento para asignarle los valores al bean. La lista de entidades y resto de
	//parametros se asignan en el método del multiaction getParametrosEntidadUsuarioPorPerfil
	@SuppressWarnings("unused")
	private void asignaPorperfil(Usuario usuario, ImporteFraccionamiento impFrac) {
		String perfil = usuario.getPerfil();
	}

	public SubentidadMediadoraManager getSubentidadMediadoraManager() {
		return subentidadMediadoraManager;
	}

	public void setSubentidadMediadoraManager(SubentidadMediadoraManager subentidadMediadoraManager) {
		this.subentidadMediadoraManager = subentidadMediadoraManager;
	}
}
