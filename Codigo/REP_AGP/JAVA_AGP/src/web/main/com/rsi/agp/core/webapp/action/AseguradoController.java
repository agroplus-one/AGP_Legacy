package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.AseguradoSubvencionManager;
import com.rsi.agp.core.managers.impl.CargaAseguradoManager;
import com.rsi.agp.core.managers.impl.DatoAseguradoManager;
import com.rsi.agp.core.managers.impl.SWAsegDatosYMedidasHelper;
import com.rsi.agp.core.managers.impl.WSResponse;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.Via;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.pagination.PaginatedListImpl;

import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidas;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidasDocument;
import es.agroseguro.serviciosweb.contratacionscasegurado.AgrException;

public class AseguradoController extends BaseMultiActionController {

	private static final String FECHA_REVISION_N = "fechaRevisionN";
	private static final String ASEGURADO_POPUP = "aseguradoPopup";
	private static final String MENSAJE = "mensaje";
	private static final String MENSAJE2 = "mensaje2";

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String JOVENAGRICULTOR_OLD = "jovenagricultorOLD";
	private static final String ATP_OLD = "atpOLD";
	private static final String MENU_GENERAL = "menuGeneral";
	private static final String ERROR = "error";
	private static final String MENSAJE_ERROR_GRAVE = "mensaje.error.grave";
	private static final String ALERTA = "alerta";
	private static final String ID_ASEGURADO = "idAsegurado";
	private static final String CARGA_ASEG = "cargaAseg";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	private static final String USUARIO = "usuario";
	private static final Log LOGGER = LogFactory.getLog(AseguradoController.class);
	private static final String LIMPIAR = "limpiar";
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private AseguradoManager aseguradoManager;
	private AseguradoSubvencionManager aseguradoSubvencionManager;
	private CargaAseguradoManager cargaAseguradoManager;
	private DatoAseguradoManager datoAseguradoManager;
	private IAseguradoDao aseguradoDao;

	private Long numPageRequest = new Long("0");
	private String sort;
	private String dir;
	
	
	
	public ModelAndView doConsulta(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{		
			LOGGER.debug("Inicio consulta de asegurados");
			
			final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
			String origenLlamada = StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA));
			String cargaAseg =  StringUtils.nullToString(request.getParameter(CARGA_ASEG));
			
			String idAsegurado = StringUtils.nullToString(request.getParameter(ID_ASEGURADO));
			if (!idAsegurado.equalsIgnoreCase("")) {
				aseguradoBean = aseguradoManager.getAsegurado(new Long(idAsegurado));
			}
			this.asignaFiltroDefectoBean(origenLlamada,aseguradoBean,user,cargaAseg);
			
			this.getDatosDisplayTag(request);
			
			if ("".equals(cargaAseg)) {
				
				List<Asegurado> listAsegurados = null;
				if (!"menuGeneral".equals(origenLlamada) && !"limpiar".equals(origenLlamada)) {
					listAsegurados = aseguradoManager.getAseguradosGrupoEntidad(aseguradoBean, user.getListaCodEntidadesGrupo());
				}
				
				String aseguradosString = "";
				if (listAsegurados != null && listAsegurados.size() > 0) 
					aseguradosString = aseguradoManager.getListAseguradosString(listAsegurados);
			
				params.put("aseguradosString",aseguradosString);
				params.put("numElem", listAsegurados == null ? 0 : listAsegurados.size());
	        
				String idsRowsChecked = StringUtils.nullToString(request.getParameter("idsRowsChecked"));
				params.put("idsRowsChecked",idsRowsChecked);
			}
	        			
			PaginatedListImpl<Asegurado> listaAsegurados = new PaginatedListImpl<Asegurado>();
			if (!"menuGeneral".equals(origenLlamada) && !"limpiar".equals(origenLlamada)) {
				listaAsegurados = aseguradoManager.getPaginatedListAsegurados(aseguradoBean,
						user.getListaCodEntidadesGrupo(), numPageRequest.intValue(), sort, dir);
			} else {
				listaAsegurados.setFullListSize(0);
				listaAsegurados.setList(new ArrayList<Asegurado>());
				listaAsegurados.setObjectsPerPage(10);
				listaAsegurados.setPageNumber(1);
			}
		      
			this.cargaParameters(params,listaAsegurados,user,request,aseguradoBean,
					cargaAseg,idAsegurado);
			params.put("impresionnumRegAseg", bundle.getString("impresionnumRegAseg"));
			params.put("listMsgError", bundle.getString("listados.msgError"));
			if (null == aseguradoBean.getVia()){
				aseguradoBean.setVia(new Via());
			}
			mv = new ModelAndView("moduloAdministracion/asegurados/asegurados",
					"aseguradoBean", aseguradoBean);
			mv.addAllObjects(params);
			LOGGER.debug("FIN consulta de asegurados");
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e);
			params.put(ALERTA, bundle.getString(MENSAJE_ERROR_GRAVE));
			params.put(ERROR, "true");
			mv = new ModelAndView("moduloAdministracion/asegurados/asegurados",
					"aseguradoBean", new Asegurado()).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doAlta(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		LOGGER.debug("Inicio doAlta de asegurados");
		Map<String, Object> params = new HashMap<String, Object>();
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		ModelAndView mv = null;
		try{
			aseguradoBean.setRazonsocial(StringUtils.normalizaRS(aseguradoBean
					.getRazonsocial()));
			aseguradoBean.setFechaRevision(null);
			
			this.asignaDatosBean(aseguradoBean,Constants.CHARACTER_S);
	
			// Nos aseguramos que NO exista un asegurado con esos datos
			Asegurado aseguradoBusqueda = aseguradoManager.getAseguradoUnico(
					aseguradoBean.getEntidad().getCodentidad(), aseguradoBean
							.getUsuario().getSubentidadMediadora().getId()
							.getCodentidad(), aseguradoBean.getUsuario()
							.getSubentidadMediadora().getId()
							.getCodsubentidad(), aseguradoBean.getNifcif(),
					aseguradoBean.getDiscriminante(), null);
			// no existe el asegurado-> damos de alta
			if (null == aseguradoBusqueda) {
				aseguradoBean.setFechaModificacion(new Date());
				aseguradoBean.setUsuarioModificacion(user.getCodusuario());
				ArrayList<Integer> errorAsegurado = aseguradoManager
						.saveAsegurado(aseguradoBean, user, false, false);
				ArrayList<String> erroresWeb = new ArrayList<String>();
				Long idAsegurado = aseguradoBean.getId();
				
				pintaErrores (errorAsegurado,params,erroresWeb,aseguradoBean,true);
				
				// si hay errores al dar el alta
				if (erroresWeb.size() > 0) {
					params.put("alerta2", erroresWeb);
					return doConsulta(request, response, aseguradoBean).addAllObjects(params);
				} else {
					// Si todo va bien, vamos a la pantalla de los
					// datosAsegurado
					return new ModelAndView("redirect:/datoAsegurado.html")
							.addObject(ID_ASEGURADO, idAsegurado)
							.addObject(CARGA_ASEG,StringUtils.nullToString(request.getParameter(CARGA_ASEG)));
				}
			// el asegurado ya existe
			} else {
				params.put(ALERTA,bundle.getString("mensaje.alta.duplicado.KO"));
				mv=  doConsulta(request, response, aseguradoBean).addAllObjects(params);
			}
			LOGGER.debug("Fin doAlta de asegurados");
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error en el alta: " + e);
			params.put(ALERTA,  bundle.getString("mensaje.alta.generico.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			return doConsulta(request, response, aseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doActualizaAseguradosSW(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;		
		LOGGER.debug("Inicio doActualizaAseguradosSW de asegurados");

		List<Asegurado> asegurados = new ArrayList<Asegurado>();
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		String inicio = (String) request.getSession().getAttribute("inicio");
		String fin = (String) request.getSession().getAttribute("fin");
		
		inicio = request.getParameter("inicio");
		fin = request.getParameter("fin");
	
		request.getSession().setAttribute("inicio", inicio);
		request.getSession().setAttribute("fin", fin);
		
		WSResponse<AseguradoDatosYMedidasDocument> respuesta = null;
		
		try {
			 asegurados = (List<Asegurado>) aseguradoDao.getAsegurados(inicio,fin);
			
			for(Asegurado asegurado : asegurados) {
				respuesta = new SWAsegDatosYMedidasHelper().getSolicitudAseguradoActualizado(asegurado.getNifcif(), null, null, realPath);
				
				if(respuesta != null) {
					AseguradoDatosYMedidasDocument data = respuesta.getData();
					
					if(asegurado.getId() != null && !"".equals(asegurado.getId().toString())){
						AseguradoDatosYMedidas aseguradoDatosYMedidas = data.getAseguradoDatosYMedidas();
						Asegurado aseg = aseguradoManager.solicitudAseguradoToBean(aseguradoDatosYMedidas);
						
						aseguradoManager.actualizaDatosAseguradoWS(aseg, asegurado.getId().toString(),Constants.CHARACTER_S.toString());
					}
				}
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AgrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mv = new ModelAndView("moduloAdministracion/asegurados/asegurados",
				"aseguradoBean", aseguradoBean);
		mv.addAllObjects(params);
		LOGGER.debug("FIN doActualizaAseguradosSW de asegurados");
		return mv;
	}
	
	public ModelAndView doModificar(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		ModelAndView mv = null;
		try{
			LOGGER.debug("Inicio doModificar de asegurados");
			this.asignaDatosBean(aseguradoBean,Constants.CHARACTER_S);
			
			params.put(ATP_OLD, aseguradoBean.getAtp());
			params.put(JOVENAGRICULTOR_OLD, aseguradoBean.getJovenagricultor());
			
			// Nos aseguramos que NO exista un asegurado con esos datos
			Asegurado aseguradoBusqueda = aseguradoManager.getAseguradoUnico(
					aseguradoBean.getEntidad().getCodentidad(), aseguradoBean
							.getUsuario().getSubentidadMediadora().getId()
							.getCodentidad(), aseguradoBean.getUsuario()
							.getSubentidadMediadora().getId()
							.getCodsubentidad(), aseguradoBean.getNifcif(),	
							aseguradoBean.getDiscriminante(), aseguradoBean.getId());
	
			if (null == aseguradoBusqueda) {
				String fechaRevision = request.getParameter("fechaRevision");
				params.put("fechaRevision", fechaRevision);
				if (!StringUtils.nullToString(fechaRevision).equals("")){
					
					aseguradoBean.setFechaRevision(DateUtil.string2Date(fechaRevision,DD_MM_YYYY));
				}
				aseguradoBean.setFechaModificacion(new Date());
				aseguradoBean.setUsuarioModificacion(user.getCodusuario());
				ArrayList<Integer> errorAsegurado = aseguradoManager.saveAsegurado(
						aseguradoBean, user, false, false);
				ArrayList<String> erroresWeb = new ArrayList<String>();
				LOGGER.debug("Usuario guardado. Miramos si hay errores y buscamos los mensajes a mostrar");
				pintaErrores(errorAsegurado, params, erroresWeb, aseguradoBean,false);
				
				if (erroresWeb.size() > 0) {
					params.put("alerta2", erroresWeb);
				}else{
					aseguradoManager.showPopupSubv (request,aseguradoBean,params);
				}
				LOGGER.debug("Comprobamos si hay que acutalizar el asegurado de la sesion");
				cargaAseguradoManager.actualizaAseguradoSesion (user,aseguradoBean);
			}else {
				params.put(ALERTA, bundle.getString("mensaje.modificacion.duplicado.KO"));
								
			}
			LOGGER.debug("Fin doModificar de asegurados");
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
		
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error en la modificacion: " + e);
			params.put(ALERTA,  bundle.getString("mensaje.modificacion.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doControlSubvenciones(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String aseguradosSeleccionados = request.getParameter("aseguradoSeleccionado");
		String plan = request.getParameter("plan");
		String linea = request.getParameter("linea");
		
		JSONObject json = new JSONObject();
		
		logger.debug("Asegurados: " + aseguradosSeleccionados);
		logger.debug("Plan: " + plan);
		logger.debug("Linea: " + linea);
		if (StringUtils.isNullOrEmpty(aseguradosSeleccionados) || StringUtils.isNullOrEmpty(plan)
				|| StringUtils.isNullOrEmpty(linea)) {
			json.put("errorMsg", "No se han recibido todos los par\u00E1metros de entrada.");
		} else {
			
			JSONArray datosAsegs = new JSONArray();
			String[] asegurados = aseguradosSeleccionados.split(";");
			List<String> aseguradosLista = Arrays.asList(asegurados);
			
			 // De cada elemento nos quedamos solo con el nif
			for (int i = 0; i < aseguradosLista.size(); i++) {
				String nifCif = aseguradosLista.get(i).toString().substring(aseguradosLista.get(i).indexOf(":")+1);
				aseguradosLista.set(i, nifCif);
			}
	        
	        // Eliminamos elemntos duplicados
	        List<String> aseguradosListaSinDuplicados = removeDuplicates(aseguradosLista);
        
	        final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
	        // PARA PERFILES DISTINTOS DE 0 Y 1 LIMITAMOS LA CONSULTA
			if (!ArrayUtils.contains(new String[] { Constants.PERFIL_USUARIO_ADMINISTRADOR,
					Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES, Constants.PERFIL_USUARIO_SEMIADMINISTRADOR },
					user.getPerfil()) && aseguradosListaSinDuplicados.size() > 5000) {
				json.put("errorMsg",
						"L\u00EDmite m\u00E1ximo de asegurados permitidos para esta operaci\u00F3n excedido (5.000). Por favor, introduzca alg\u00FAn filtro adicional que reduzca el resultado la consulta o seleccione menos elementos.");
			} else {
	        	// Dividimos la lista en porciones de 1000 para consultas con muchos registros
				List<List<String>> partes = new LinkedList<List<String>>();
				for (int i = 0; i < aseguradosListaSinDuplicados.size(); i += 1000) {
					partes.add(aseguradosListaSinDuplicados.subList(i,
				            i + Math.min(1000, aseguradosListaSinDuplicados.size() - i)));
				}
				
				JSONObject aseg;
				
				for (int i = 0; i < partes.size(); i++) {
					
					// Llamamos al servicio
					List<String> subvencionables = this.aseguradoSubvencionManager.getControlSubvenciones(partes.get(i), new BigDecimal(plan),
							new BigDecimal(linea));
					
					// Recorremos la lista de subvencionables y comparamos con la de de seleccionados
					for (String asegurado : partes.get(i)) {
						
						aseg = new JSONObject();
						aseg.put("nifcif", asegurado);
						aseg.put("subvencionable", false);
						
						if (subvencionables.contains(asegurado)) {
							aseg.put("subvencionable", true);
						}
						
						datosAsegs.put(aseg);
					}
				}
				
				json.put("datosAsegs", datosAsegs);
	        }
		}
		this.getWriterJSON(response, json);
		return null;
	}
	
	public ModelAndView doActualizarSubvsAseg(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		ModelAndView mv = null;
		try{
			LOGGER.debug("Inicio doActualizarSubvsAseg de asegurados");
			
			String listIdPolizas =  request.getParameter("listIdPolizas");
			String subv10 = StringUtils.nullToString(request.getParameter("subv10"));
			String subv20 = StringUtils.nullToString(request.getParameter("subv20"));
			aseguradoManager.asociarSubvsPolizaAseg (aseguradoBean.getId(),listIdPolizas,subv10,subv20, user);
			params.put(MENSAJE,bundle.getString("mensaje.asegurado.actualizarSubvs.OK"));
			params.put(ATP_OLD, aseguradoBean.getAtp());
			params.put(JOVENAGRICULTOR_OLD, aseguradoBean.getJovenagricultor());
		
			mv =  doConsulta(request, response, aseguradoBean).addAllObjects(params);
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error al actualizar las subvenciones del asegurado: " + e);
			params.put(ALERTA,  bundle.getString("mensaje.asegurado.actualizarSubvs.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			mv =  doConsulta(request, response, aseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	
	public ModelAndView doBaja(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		ModelAndView mv = null;
		try{
			LOGGER.debug("Inicio doBaja de asegurados");
			
			final Long idAsegurado = aseguradoBean.getId();
			final List<Poliza> listaPolizasAsociadas = aseguradoManager
					.getPolizasByIdAsegurado(idAsegurado);
			// objeto asegurado para comprobar que el asegurado que se va a
			// borrar existe en la BD
			final Asegurado asegurado = aseguradoManager
					.getAsegurado(idAsegurado);
			if ((null == listaPolizasAsociadas || listaPolizasAsociadas
					.isEmpty()) && null != asegurado) {
				aseguradoManager.dropAsegurado(asegurado, user);
				params.put(MENSAJE,bundle.getString("mensaje.baja.OK"));
			} else {
				params.put(ATP_OLD, aseguradoBean.getAtp());
				params.put(JOVENAGRICULTOR_OLD, aseguradoBean.getJovenagricultor());
				// si el asegurado ya no esta en la BD no se puede borrar
				if (null == asegurado) {
					params
							.put(ALERTA,
									bundle.getString("mensaje.asegurado.incorrecto.KO"));
				} else {
					params.put(ALERTA,
							bundle.getString("mensaje.baja.con.polizas.KO"));
				}
				return doConsulta(request, response, aseguradoBean).addAllObjects(params);
			}
			LOGGER.debug("Fin doBaja de asegurados");
			mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
			
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error al dar de baja un asegurado: " + e);
			params.put(ALERTA,  bundle.getString("mensaje.asegurado.borrar.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			mv =  doConsulta(request, response, aseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doDesbloquearUsuario(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		LOGGER.debug("Inicio doDesbloquearUsuario de asegurados");
		
		try {
			String usuarioAsegurado = StringUtils.nullToString(request
					.getParameter("usuarioAsegurado"));
		
			aseguradoManager.desbloqueaAsegurado(usuarioAsegurado);
			params.put(MENSAJE,bundle.getString("mensaje.desbloquearUsuario.OK"));
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
			LOGGER.debug("Fin doDesbloquearUsuario de asegurados");
			
				
		} catch (DAOException e) {
			LOGGER.error("Error al desbloquear un asegurado" + e);
			params.put(ALERTA,bundle.getString("mensaje.desbloquearUsuario.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView getDatosAseguradoWS(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		LOGGER.debug("Inicio getDatosAseguradoWS de asegurados");
		try {
			String id = StringUtils.nullToString(request.getParameter("idAseguradoP"));
			String nifcif = StringUtils.nullToString(request.getParameter("nifcifBusqueda"));
			Linea linea = user.getColectivo().getLinea();
			String codLinea = linea.getCodlinea().toString();
			String codPlan = linea.getCodplan().toString();
			if (!nifcif.equals("")) {
				if(!id.equals("")){
					params = this.aseguradoManager
							.getDatosAseguradoWS(Long.valueOf(id), nifcif, realPath, user.getCodusuario(), id, codLinea, codPlan);
				}else{
					params = this.aseguradoManager
							.getDatosAseguradoWS(null, nifcif, realPath, user.getCodusuario(), id, codLinea, codPlan);
				}
				
				if (params.containsKey(ASEGURADO_POPUP)) {
					
					params.put("showPopupAsegurados", true);
					Asegurado aseguradoPopup = new Asegurado();
					aseguradoPopup = (Asegurado) params.get(ASEGURADO_POPUP);
					params.put(ASEGURADO_POPUP, aseguradoPopup);					
				}else if (!params.containsKey("aseguradoBean")){
					if (params.containsKey(ERROR)) {
						params.put(ALERTA, params.get(ERROR));
					}
				}
				
			}else {
				LOGGER.error("Error al obtener los datos actualizados del asegurado");
				params.put(ALERTA,bundle.getString("mensaje.asegurado.obtenerDatos.KO"));
			}
			
			params.put("idAseguradoP",id);
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
			LOGGER.debug("Fin getDatosAseguradoWS de asegurados");
			
				
		} catch (Exception e) {
			LOGGER.error("Error al getDatosAseguradoWS de un asegurado" + e);
			params.put(ALERTA,bundle.getString("mensaje.getDatosAseguradoWS.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView actualizaDatosAseguradoWS(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		LOGGER.debug("Inicio actualizaDatosAseguradoWS de asegurados");
		try {
			String id = StringUtils.nullToString(request.getParameter("idAseg"));
			aseguradoBean.setFechaModificacion(new Date());
			final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
			aseguradoBean.setUsuarioModificacion(user.getCodusuario());
			aseguradoManager.actualizaDatosAseguradoWS (aseguradoBean,id,Constants.CHARACTER_S.toString());
			params.put(MENSAJE,bundle.getString("mensaje.asegurado.actualizado.OK"));
			aseguradoBean = aseguradoManager.getAseguradobyId(aseguradoBean,id);
			
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
			LOGGER.debug("Fin actualizaDatosAseguradoWS de asegurados");
			
				
		} catch (Exception e) {
			LOGGER.error("Error al actualizaDatosAseguradoWS de un asegurado" + e);
			params.put(ALERTA,bundle.getString("mensaje.asegurado.actualizado.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defectos
			mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
		}
		return mv;
	}
	 
	
	public ModelAndView actualizaFechaRevision(HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		LOGGER.debug("Inicio actualizaFechaRevision de asegurados");
		try {
			String id = StringUtils.nullToString(request.getParameter("idAseg"));
			aseguradoManager.actualizaFechaRevision(id, aseguradoBean,Constants.CHARACTER_S.toString());
			aseguradoBean = aseguradoManager.getAsegurado(new Long(id));
			params.put(MENSAJE,
					bundle.getString("mensaje.asegurado.fechaRevision.OK"));
			
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
			LOGGER.debug("Fin actualizaFechaRevision de asegurados");
			
				
		} catch (Exception e) {
			LOGGER.error("Error al actualizaFechaRevision de un asegurado" + e);
			params.put(ALERTA,bundle.getString("mensaje.asegurado.actualizado.KO"));
			params.put(ORIGEN_LLAMADA, MENU_GENERAL); // para que ponga los filtros por defecto
			mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
		}
		return mv;
	}
	 
	public ModelAndView imprimir (HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		LOGGER.debug("Inicio imprimir de asegurados");
		try {
			String formato = request.getParameter("formato");
			this.getDatosDisplayTag(request);			
			
			List<Asegurado> listaAseg = aseguradoManager
						.getAseguradosGrupoEntidad(aseguradoBean,
								user.getListaCodEntidadesGrupo());
			
			LOGGER.debug("@@@@@@TOTAL ASEGURADOS:" + listaAseg.size());
			
			if (listaAseg.size() > Integer.parseInt(bundle.getString("impresionnumRegAseg"))) {
				params.put(ALERTA,bundle.getString("listados.msgError"));
				mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
			}
			else {
				
				List<DatoAsegurado> listaDatosAsegs = getListaDatosAsegs(listaAseg);
								
				request.setAttribute("listaAseg", listaDatosAsegs);
				return new ModelAndView(
						"forward:/informes.html?method=doInformeAsegurados").addObject("formato",formato);
			}
		} catch (Exception e) {
			LOGGER.error("Error al imprimir el listado de asegurados" + e);
			params.put(ALERTA,bundle.getString("listados.msgError"));
			mv = doConsulta(request, response, new Asegurado()).addAllObjects(params);
		}
		return mv;
	}
	
	private List<DatoAsegurado> getListaDatosAsegs(List<Asegurado> listaAseg) {
		
		List<DatoAsegurado> listaDatoAsegurado = new ArrayList<DatoAsegurado>();
				
		// por cada asegurado
		for (Asegurado asegurado : listaAseg) {
			
			int n = asegurado.getDatoAsegurados().size();
						
			if (n!=0) {
				
				DatoAsegurado arr[] = new DatoAsegurado[n];
				
				int i = 0;
				
				// por cada dato asegurado que corresponde a una linea
				for (DatoAsegurado x : asegurado.getDatoAsegurados()) { 
					
					arr[i] = x;
					
					DatoAsegurado datoAsegurado = new DatoAsegurado();
					
					datoAsegurado.setAsegurado(arr[i].getAsegurado());
				    datoAsegurado.setIban(arr[i].getIban());
				    datoAsegurado.setCcc(arr[i].getCcc());
				    datoAsegurado.setIban2(arr[i].getIban2());
				    datoAsegurado.setCcc2(arr[i].getCcc2());
				    Long idDatoAsegurado = arr[i].getId();
				    datoAsegurado.getLineaCondicionado().setCodlinea(datoAseguradoManager.getLineaAsegurado(idDatoAsegurado));
	   				listaDatoAsegurado.add(datoAsegurado);
				    i++;
				}
				
			} else {
			
					// Asegurado sin cuenta, solo mostramos los datos del asegurado y dejamos los campos de cuenta vacios
					DatoAsegurado datoAsegurado = new DatoAsegurado();
					datoAsegurado.setAsegurado(asegurado);
					datoAsegurado.setIban("");
				    datoAsegurado.setCcc("");
				    datoAsegurado.setIban2("");
				    datoAsegurado.setCcc2("");
				    listaDatoAsegurado.add(datoAsegurado);	
			}
			
		}
				
		return listaDatoAsegurado;
	}

	public ModelAndView doCarga (HttpServletRequest request, 
			HttpServletResponse response, Asegurado aseguradoBean){
		
		LOGGER.debug("Inicio cargar asegurado");
		ModelAndView mv = null;
		final Usuario user = (Usuario) request.getSession().getAttribute(USUARIO);
		Map<String, Object> params = new HashMap<String, Object>();
		String realPath = this.getRealPath();
		try {	
			LOGGER.debug("Cargar el asegurado " + aseguradoBean.getId());
			
			Asegurado aseg = aseguradoManager.getAseguradoFacturacion(
					aseguradoBean.getId(), user,Constants.FACTURA_CONSULTA);
			
			params = cargaAseguradoManager.cargaAsegurado(aseg,request, user);
			Long idAsegurado = aseg.getId();
			String nifCif = aseg.getNifcif();
			
			// Se obtiene la línea del colectivo cargado previamente para las llamadas al SW de Datos y Medidas
			Linea linea = user.getColectivo().getLinea();
			String codLinea = linea.getCodlinea().toString();
			String codPlan = linea.getCodplan().toString();
			String idAseguradoStr = idAsegurado.toString();
			String codUsuario = user.getCodusuario();
			HashMap<String,Object> subvencionabilidadAsegurado = aseguradoManager
					.subvencionabilidadAsegurado(idAsegurado, nifCif, realPath, codUsuario, idAseguradoStr, codLinea, codPlan);
			params.putAll(subvencionabilidadAsegurado);
			params.put(CARGA_ASEG, CARGA_ASEG);
			mv = doConsulta(request, response, aseg).addAllObjects(params);
		} catch (Exception e) {
			LOGGER.error("Error al cargar el asegurado: ", e);
			params.put(ALERTA,bundle.getString(MENSAJE_ERROR_GRAVE));
			mv = doConsulta(request, response, aseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doImportarCsv(HttpServletRequest request,
			HttpServletResponse response, Asegurado aseguradoBean)
			throws Exception {

		logger.info("init - doImportarCsv");
		JSONObject parameters = new JSONObject();

		try {
			logger.debug("Nombre Fichero: "
					+ aseguradoBean.getFile().getOriginalFilename());
			parameters = aseguradoManager.importarCSV(aseguradoBean.getFile(),
					request);
			
			
		} catch (Exception e) {
			logger.error("Error al importar el fichero de asegurados" + e);
			parameters.put(ALERTA,
					bundle.getString("mensaje.importarAsegurados.fichero.KO"));
		}
		getWriterJSON(response, parameters);
		return null;
	}

	
	/**
	 * Asigna datos al bean
	 * @param aseguradoBean
	 */
	private void asignaDatosBean(Asegurado aseguradoBean,Character revisado) {

		if (null == aseguradoBean.getDiscriminante()
				|| "".equals(aseguradoBean.getDiscriminante())) {
			aseguradoBean.setDiscriminante("0");
		}
		if (null == aseguradoBean.getApellido2()
				|| "".equals(aseguradoBean.getApellido2())) {
			aseguradoBean.setApellido2("");
		}
		aseguradoBean.setRevisado(revisado);
		
	}

	/**
	 * En funcion del perfil establecemos campos por defecto
	 * @param origenLlamada
	 * @param aseguradoBean
	 * @param user
	 * @param cargaAseg 
	 */
	private void asignaFiltroDefectoBean(String origenLlamada,
			Asegurado aseg, Usuario user, String cargaAseg) {
		
		if (origenLlamada.equals(MENU_GENERAL) || origenLlamada.equals("limpiar")|| origenLlamada.equals("baja")){
			
			// si estamos en carga de usuarios rellenamos por defecto Entidad, Entidad Mediadora y subentidad mediadora
			// para todos los perfiles y ademas si es perfil 4 rellenamos usuario
			if (cargaAseg.equals(CARGA_ASEG)){
				//
				final BigDecimal entidad = user.getColectivo().getTomador().getId().getCodentidad();

				aseg.getEntidad().setCodentidad(entidad);
				aseg.getEntidad().setNomentidad(user.getColectivo().getSubentidadMediadora().getEntidad().getNomentidad());
				aseg.getUsuario().getSubentidadMediadora().getId().setCodentidad(user.getColectivo().getSubentidadMediadora().getId().getCodentidad());
				aseg.getUsuario().getSubentidadMediadora().getId().setCodsubentidad(user.getColectivo().getSubentidadMediadora().getId().getCodsubentidad());
				
				if (Constants. PERFIL_USUARIO_OTROS.equals(user.getPerfil())){
					aseg.getUsuario().setCodusuario(user.getCodusuario());
				}
				
			// Administración de asegurados	
			}else{
				// Para todos los perfiles se rellena la entidad
				aseg.getEntidad().setCodentidad(
						user.getOficina().getEntidad().getCodentidad());
				aseg.getEntidad().setNomentidad(
						user.getSubentidadMediadora().getEntidad().getNomentidad());

				// INTERNOS - perfil 2,3 y 4: Entidad,E-S mediadora y usuario
				if (Constants. PERFIL_USUARIO_OTROS.equals(user.getPerfil()) || 
						Constants. PERFIL_USUARIO_OFICINA.equals(user.getPerfil())
						|| Constants.PERFIL_USUARIO_JEFE_ZONA.equals(user.getPerfil()) && !user.isUsuarioExterno()){
					if (Constants. PERFIL_USUARIO_OTROS.equals(user.getPerfil()) || 
							Constants. PERFIL_USUARIO_OFICINA.equals(user.getPerfil())
							|| Constants.PERFIL_USUARIO_JEFE_ZONA.equals(user.getPerfil())&& !user.isUsuarioExterno()){
						aseg.getUsuario().getSubentidadMediadora().getId().setCodentidad(
								user.getSubentidadMediadora().getId().getCodentidad());
						aseg.getUsuario().getSubentidadMediadora().getId().setCodsubentidad(
								user.getSubentidadMediadora().getId().getCodsubentidad());
						aseg.getUsuario().setCodusuario(user.getCodusuario());

						// perfil 1 externo: Entidad,E-S mediadora
					}else if ((Constants. PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(user.getPerfil())&& user.isUsuarioExterno())
							|| Constants. PERFIL_USUARIO_OFICINA.equals(user.getPerfil())
							&& user.isUsuarioExterno()){
						aseg.getUsuario().getSubentidadMediadora().getId().setCodentidad(
								user.getSubentidadMediadora().getId().getCodentidad());
						aseg.getUsuario().getSubentidadMediadora().getId().setCodsubentidad(
								user.getSubentidadMediadora().getId().getCodsubentidad());
					}
				}
				// EXTERNOS - perfil 1 y 3 - E-S Mediadora
				else if ((Constants. PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(user.getPerfil())&& user.isUsuarioExterno())
						|| Constants. PERFIL_USUARIO_OFICINA.equals(user.getPerfil())
						&& user.isUsuarioExterno()){
					aseg.getUsuario().getSubentidadMediadora().getId().setCodentidad(
							user.getSubentidadMediadora().getId().getCodentidad());
					aseg.getUsuario().getSubentidadMediadora().getId().setCodsubentidad(
							user.getSubentidadMediadora().getId().getCodsubentidad());
				}
				
			}

			}
		
	}

	/**
	 * rellena el map con los parameteros necesarios 
	 * @param params
	 * @param listaAsegurados 
	 * @param idAsegurado 
	 */
	private void cargaParameters(Map<String, Object> params,
			PaginatedListImpl<Asegurado> listaAsegurados,Usuario user,HttpServletRequest request,
			Asegurado aseguradoBean,String cargaAseg, String idAsegurado ) {

		String fechaRevision = request.getParameter(FECHA_REVISION_N);
		if (!StringUtils.nullToString(fechaRevision).equals("")){
			
			try {
				Date d = DateUtil.string2Date(fechaRevision,"yyyy-MM-dd");
				SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
				String d2 = sdf.format(d);
				aseguradoBean.setFechaRevision(DateUtil.string2Date(d2,DD_MM_YYYY));
				params.put(FECHA_REVISION_N, request.getParameter(FECHA_REVISION_N));
			} catch (Exception e) {
				LOGGER.debug("Error al formatear la fecha Revision: cargaParameters() - AseguradoController ", e);
			}
		}
		if (!idAsegurado.equalsIgnoreCase("")) {
			if (!aseguradoBean.getTipoidentificacion().equals("CIF")) {
				params.put("comeFrom", 1);
			} else {
				params.put("comeFrom", 2);
			}
		}
		params.put(CARGA_ASEG, cargaAseg);
		params.put(ASEGURADO_POPUP, new Asegurado());
		params.put("totalListSize", listaAsegurados.getFullListSize());
		params.put("perfil", user.getPerfil().substring(4));
		params.put("externo", user.getExterno());
		params.put("listaAsegurados", listaAsegurados);
		
		LOGGER.debug("Buscamos grupo de entidades...");
		String grupoEntidades = "";
		if (!StringUtils.nullToString(user.getListaCodEntidadesGrupo()).equals(
				"")) {
			grupoEntidades = StringUtils.toValoresSeparadosXComas(
					user.getListaCodEntidadesGrupo(), false, false);
		}
		LOGGER.debug("Grupo de entidades: '" + grupoEntidades + "'");
		params.put("grupoEntidades", grupoEntidades);
		
	}
	/**
	 * Inicializa datos del displayTag para pasarlos y 
	 * hacer la busqueda
	 * @param request
	 */
	private void getDatosDisplayTag(HttpServletRequest request) {

		if (MENU_GENERAL.equals(StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA))) || 
				LIMPIAR.equals(StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)))) {
			numPageRequest = Long.parseLong("-1");
		}
		else {
			
			if (request.getParameter("page") == null) {
				numPageRequest = Long.parseLong("1");
			}else{
				numPageRequest = Long.parseLong(request.getParameter("page"));
			}
		}
		
		sort = StringUtils.nullToString(request.getParameter("sort"));
		dir = StringUtils.nullToString(request.getParameter("dir"));
	}

	private void pintaErrores(ArrayList<Integer> errorAsegurado,
			Map<String, Object> parameters, ArrayList<String> erroresWeb,
			Asegurado aseguradoBean,boolean esAlta) {
		for (Integer error : errorAsegurado) {
			switch (error.intValue()) {
			case 0:
				if (esAlta)
					parameters.put(MENSAJE, bundle.getString("mensaje.alta.OK")
							+ (parameters.get(MENSAJE) == null ? "" : ". " + parameters.get(MENSAJE)));
				else
					parameters.put(MENSAJE, bundle.getString("mensaje.modificacion.OK")
							+ (parameters.get(MENSAJE) == null ? "" : ". " + parameters.get(MENSAJE)));
				break;
			case 1:
				erroresWeb
						.add(bundle
								.getString("mensaje.asegurado.entidad.inexistente"));
				break;
			case 2:
				erroresWeb.add("El "
						+ aseguradoBean.getTipoidentificacion()
						+ " introducido no es correcto.");
				break;
			case 3:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.via.KO"));
				break;
			case 4:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.provincia.KO"));
				break;
			case 5:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.localidad.KO"));
				break;
			case 6:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.sublocalidad.KO"));
				break;
			case 7:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.relacion.KO"));
				break;
			case 8:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.codusuario.KO"));
				break;
			case 9:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.subentmed.KO"));
				break;	
			case 10:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.ESMedBaja.KO"));
				break;	
			case 11:
				erroresWeb.add(bundle
						.getString("mensaje.asegurado.ESMed.KO"));
				break;
			case 69:
				parameters.put(MENSAJE2, bundle.getString("mensaje.asegurado.codigoPostal.KO")
						+ (parameters.get(MENSAJE2) == null ? "" : ". " + parameters.get(MENSAJE2)));
				break;
			default:
				erroresWeb.add(bundle.getString(MENSAJE_ERROR_GRAVE));
			}
		}
	}
	
	public void getDetalleAsegurado(HttpServletRequest req, HttpServletResponse res) {
		String nifCif = req.getParameter("nifCif");
		String origen = req.getParameter("origen");
		JSONObject json = new JSONObject();
		try {
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			LOGGER.info("VOY A INTENTAR LLAMAR AL SERVICIO WEB");
			String tablaHtml = null;
			if(origen.equals(Constants.ORIGEN_CARGA)){
				final Usuario usuario = (Usuario) req.getSession().getAttribute(USUARIO);
				Linea linea = usuario.getColectivo().getLinea();
				String codLinea = linea.getCodlinea().toString();
				String codPlan = linea.getCodplan().toString();
				tablaHtml = this.aseguradoManager.getControlSubvsAsegurado(nifCif, codPlan, codLinea, realPath);
			}
			if(origen.equals(Constants.ORIGEN_ADMIN)){
				String fechaEstudio = StringUtils.nullToString(req.getParameter("fechaEstudio"));
				tablaHtml = this.aseguradoManager.getControlSubvsAsegurado(nifCif, fechaEstudio, realPath);
			}
			json.put("tablaHtml", tablaHtml);
		} catch (Exception e) {
			logger.error("Hubo un error al llamar el servicio web de Agroseguro", e);
			try {
				json.put("agroMsg", e.getMessage());
			} catch (JSONException e1) {
				LOGGER.debug(e1.getMessage());
			}
		}
		LOGGER.info("MANDO LOS DATOS A LA VISTA");
		this.getWriterJSON(res, json);
	}
	
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat(DD_MM_YYYY), true));
	}
	
	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}
	
	public void setAseguradoSubvencionManager(AseguradoSubvencionManager aseguradoSubvencionManager) {
		this.aseguradoSubvencionManager = aseguradoSubvencionManager;
	}

	public void setCargaAseguradoManager(CargaAseguradoManager cargaAseguradoManager) {
		this.cargaAseguradoManager = cargaAseguradoManager;
	}
	
	 // Funcion para eliminar duplicados de una lista
    public static <T> List<T> removeDuplicates(List<T> list)
    {
  
        List<T> newList = new ArrayList<T>();
  
        for (T element : list) {
  
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
  
        return newList;
    }

	public DatoAseguradoManager getDatoAseguradoManager() {
		return datoAseguradoManager;
	}

	public void setDatoAseguradoManager(DatoAseguradoManager datoAseguradoManager) {
		this.datoAseguradoManager = datoAseguradoManager;
	}

	public IAseguradoDao getAseguradoDao() {
		return aseguradoDao;
	}

	public void setAseguradoDao(IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}
}
