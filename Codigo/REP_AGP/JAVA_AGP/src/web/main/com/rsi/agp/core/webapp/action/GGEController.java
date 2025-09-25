package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.GGEEntidadesHistoricoManager;
import com.rsi.agp.core.managers.impl.GGEEntidadesManager;
import com.rsi.agp.core.managers.impl.GGESubEntidadesHistoricoManager;
import com.rsi.agp.core.managers.impl.GGESubEntidadesManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.core.webapp.util.BigDecimalEditor;
import com.rsi.agp.core.webapp.util.LongEditor;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;
import com.rsi.agp.dao.tables.comisiones.GGESubentidades;
import com.rsi.agp.dao.tables.commons.Usuario;

public class GGEController extends BaseMultiActionController  {
	private static final Log LOGGER = LogFactory.getLog(GGEController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private GGEEntidadesManager GGEEntidadesManager;
	private GGESubEntidadesManager GGESubEntidadesManager;
	private GGEEntidadesHistoricoManager GGEEntidadesHistoricoManager;
	private GGESubEntidadesHistoricoManager GGESubEntidadesHistoricoManager;
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(BigDecimal.class, null, new BigDecimalEditor());
		binder.registerCustomEditor(Long.class, null, new LongEditor());
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, GGESubentidades geeSubentidadesBean)throws Exception{
		LOGGER.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<GGESubentidades> listgeeSubentidades = null;
		GGEEntidades ggeEntidade = null;
		boolean alertaCargarDatos = false; 
	
		try {
			
			if (StringUtils.nullToString(request.getParameter("limpiar")).equals("")){
				GGESubentidades filtroSubentidades = (GGESubentidades) request.getSession().getAttribute("filtroSubentidades");
				if (filtroSubentidades != null){
					geeSubentidadesBean = filtroSubentidades;
				}
			}
			else{
				request.getSession().removeAttribute("filtroSubentidades");
			}
			
			LOGGER.debug("se comprueban si existen los parametros que se envian desde la pantalla de incidencias");	
			if (request.getParameter("entidadmediadora") != null){
				geeSubentidadesBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal(request.getParameter("entidadmediadora")));				
			}	
			
			if (request.getParameter("subentidad") != null){
				geeSubentidadesBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(request.getParameter("subentidad")));
				if (request.getParameter("nomsubentidad") != null){
					geeSubentidadesBean.getSubentidadMediadora().setNomsubentidad(request.getParameter("nomsubentidad"));
				}
			}				
							
			//Se carga el plan actual cuando se viene a la pantalla a través del menú
			//Si se viene de incidencias, se cargaran los correspondiente al plan del mediador sin desglose de GGE
			if (request.getParameter("planIncidencias") != null){
				geeSubentidadesBean.setPlan(new Long(request.getParameter("planIncidencias")));
				ggeEntidade = GGEEntidadesManager.getLastGGEPlan(geeSubentidadesBean.getPlan());
				if (ggeEntidade == null){
					ggeEntidade = GGEEntidadesManager.getLastGGEPlan();
				}
			} else {				
				//Si se filtra por algún plan hay que modificar los porcentajes de ese plan
				if (geeSubentidadesBean.getPlan() != null){
					ggeEntidade = GGEEntidadesManager.getLastGGEPlan(geeSubentidadesBean.getPlan());
					if (ggeEntidade == null){
						ggeEntidade = GGEEntidadesManager.getLastGGEPlan();
					}
				} else { //Si no se filtra por ninguno se muestra el plan actual
					ggeEntidade = GGEEntidadesManager.getLastGGEPlan();
				}			
			}				
			
			if(ggeEntidade != null){
				LOGGER.debug("registro de porcentajes. id: " +ggeEntidade.getId());				
					
				if((request.getParameter("planIncidencias") == null) && (Boolean.parseBoolean(request.getParameter("primeraConsulta")))){
					geeSubentidadesBean.setPlan(ggeEntidade.getPlan());
				}
				listgeeSubentidades = GGESubEntidadesManager.getListGGESubentidades(geeSubentidadesBean);				
							
				GGEEntidades ggeEntidadeReplicar = GGEEntidadesManager.getLastGGEPlan();
				parametros.put("planorigen", ggeEntidadeReplicar.getPlan());
				parametros.put("plannuevo", ggeEntidadeReplicar.getPlan() + 1);
				//parametros.putAll(GGEEntidadesManager.getPlanesReplicar(ggeEntidade));
				LOGGER.debug("listado de distribuciones gge. Size: " +listgeeSubentidades.size());
				 	
			}else{
				alertaCargarDatos = true;
			}
			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null )
			{
				parametros.put("idFichero",request.getParameter("idFichero"));
				parametros.put("tipoFichero",request.getParameter("tipoFichero"));	
			} else {
				parametros.put("idFichero","");
				parametros.put("tipoFichero","");	
			}
			parametros.put("alertaCargarDatos", alertaCargarDatos);
			
			parametros.put("listCultivosSubentidades", listgeeSubentidades);
			parametros.put("ggeEntidad", ggeEntidade);
			parametros.put("mensaje", request.getParameter("mensaje"));
				
			parametros.put("planesJSON", obtenerPlanesJSON());	
			
			//ASF - Guardo el filtro en sesión para poder mantenerlo
			request.getSession().setAttribute("filtroSubentidades", geeSubentidadesBean);
				
			mv = new ModelAndView("/moduloComisiones/distribucionGEE", "geeSubentidadesBean", geeSubentidadesBean);				
				
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new GGESubentidades());
		}
		LOGGER.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	/**
	 * Obtenemos los planes para saber que porcentajes corresponden a cada uno.
	 * Necesario ya que en la tabla de subentidades pueden aparecer valores para diferentes planes
	 * y es necesario calcular los porcentajes en función de cada plan. Se manda com json al recalcular
	 * en javascript.
	 * */

	private Object obtenerPlanesJSON() throws BusinessException{
		
			List<GGEEntidades> list = GGEEntidadesManager.getAll();
			JSONArray objeto = new JSONArray();
			for (GGEEntidades object : list) {				
					objeto.put(object.getPlan().toString());
					objeto.put(object.getPctentidades().toString());
			}

		return objeto;
	}

	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, GGESubentidades geeSubentidadesBean) throws Exception {
		LOGGER.debug("init - doBaja");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
	    try {
	    	LOGGER.debug("Registro GGE que se va a eliminar. ID=" + geeSubentidadesBean.getId());
	    	GGESubentidades gge = GGESubEntidadesManager.getGge(geeSubentidadesBean.getId());
	    	
	        GGESubEntidadesManager.baja(geeSubentidadesBean.getId());
	    	LOGGER.debug("registro eliminado correctamente");
	    	
	    	parametros.put("mensaje", bundle.getString("mensaje.comisiones.GGE.baja.OK"));
	    	 
	    	LOGGER.debug("generamos el registro de historico");
        	GGESubEntidadesHistoricoManager.addResgitroHist(gge,ComisionesConstantes.AccionesHistComisionCte.BAJA);
        	 
	    	mv = doConsulta(request, response, new GGESubentidades());
	    	 
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de baja una distribucion de GGE: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.GGE.baja.KO"));
			mv = doConsulta(request, response, new GGESubentidades());
		}
		LOGGER.debug("end - doBaja");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, GGESubentidades geeSubentidadesBean) throws Exception{
		LOGGER.debug("init - doEdita");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = null;
        ArrayList<Integer> errList = null;
        try {
         	LOGGER.debug("Registro GGE que se va a modificar. ID=" + geeSubentidadesBean.getId());
        	usuario = (Usuario) request.getSession().getAttribute("usuario");
        	
        	  if(geeSubentidadesBean.getId() != null){
        		  errList = GGESubEntidadesManager.saveUpdateGGE(geeSubentidadesBean,usuario);
        		 
        		  if(errList.size() > 0) {
  					geeSubentidadesBean = GGESubEntidadesManager.getGge(geeSubentidadesBean.getId());
        		  } else {
        			  LOGGER.debug("generamos el registro de historico");
                	  GGESubEntidadesHistoricoManager.addResgitroHist(geeSubentidadesBean,ComisionesConstantes.AccionesHistComisionCte.MODIFICACION);
        		  }
        		  
        		  parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.GGE.modificacion.OK"));
        	  }
				
        	  mv = doConsulta(request, response, geeSubentidadesBean);
        	  
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de modificar una distribucion de GGE: " + be.getMessage());			
			parametros.put("alerta", bundle.getString("mensaje.comisiones.GGE.modificacion.KO"));
			mv = doConsulta(request, response, new GGESubentidades());
		}
		LOGGER.debug("end - doEdita");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doModificarPorcentajes(HttpServletRequest request, HttpServletResponse response, GGESubentidades geeSubentidadesBean) throws Exception{
		LOGGER.debug("init - doModificarPorcentajes");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = null;
		String pRGA = "";
		String pga = "";
		String pe = "";
		String plan = "";
		GGEEntidades ggeEntidades = null;
		
		try {
			usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			pRGA = StringUtils.nullToString(request.getParameter("txt_porcentajeRga"));
			pga = StringUtils.nullToString(request.getParameter("porcentajeGeneral"));
			pe = StringUtils.nullToString(request.getParameter("pctentidadnuevo")).replace(",", ".");
			plan = StringUtils.nullToString(request.getParameter("planPorcentajes"));
			
			LOGGER.debug("se comprueba si hay que coger el plan actual, o el que se manda desde la pantalla de incidencias");
			if (plan != null){
				ggeEntidades = GGEEntidadesManager.getLastGGEPlan(new Long(plan));
				geeSubentidadesBean.setPlan(new Long(plan));
			} else {				
				ggeEntidades = GGEEntidadesManager.getLastGGEPlan();
			}			
			
			LOGGER.debug("Porcentajes: ");
			LOGGER.debug("porcentaje RGA: " + pRGA );
			LOGGER.debug("porcentaje GENERAL: " + pga );
			LOGGER.debug("nuevo pct entidad: " + pe );
			LOGGER.debug("plan Porcentajes: " + plan );
			
			ggeEntidades = GGEEntidadesManager.Modificar(pga,pe,pRGA,plan,usuario,ggeEntidades);
			LOGGER.debug("porcentajes modificados correctamente");
			
			parametros.put("mensaje", bundle.getString("mensaje.comisiones.GGE.modificacion.OK"));
			
			LOGGER.debug("insertamos el registro en el historico");
			GGEEntidadesHistoricoManager.addResgitroHist(ggeEntidades,ComisionesConstantes.AccionesHistComisionCte.MODIFICACION);
			
			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null )
			{
				parametros.put("idFichero",request.getParameter("idFichero"));
				parametros.put("tipoFichero",request.getParameter("tipoFichero"));	
			}		
			GGESubentidades filtroSubentidades = (GGESubentidades) request.getSession().getAttribute("filtroSubentidades");
			if (filtroSubentidades != null) {
				geeSubentidadesBean = filtroSubentidades;
			}
			mv = doConsulta(request, response, geeSubentidadesBean);
			//mv = new ModelAndView("redirect:/gge.html",parametros);		
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de modificar los porcentajes de GGE: " + be.getMessage());			
			parametros.put("alerta", bundle.getString("mensaje.comisiones.GGE.modificacion.KO"));
			mv = doConsulta(request, response, new GGESubentidades());
		}
		LOGGER.debug("end - doModificarPorcentajes");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, GGESubentidades geeSubentidadesBean) throws Exception{
		LOGGER.debug("init - doAlta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = null;
		ArrayList<Integer> errList = null;
		try {
			usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			LOGGER.debug("comprobamos que el registro no esta duplicado");
			if(GGESubEntidadesManager.existePlan(geeSubentidadesBean)){			
				LOGGER.debug("comprobamos que el registro no esta duplicado");
				if(!GGESubEntidadesManager.existeRegistro(geeSubentidadesBean)){
						errList = GGESubEntidadesManager.saveUpdateGGE(geeSubentidadesBean,usuario);
						
						if (errList.size() == 0){
							LOGGER.debug("generamos el registro de historico");
		 					GGESubEntidadesHistoricoManager.addResgitroHist(geeSubentidadesBean,ComisionesConstantes.AccionesHistComisionCte.ALTA);
						}
						
						parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.GGE.alta.OK"));
		 				
				}else{
						parametros.put("alerta", bundle.getString("mensaje.comisiones.GGE.alta.KO"));
				}
			} else {				
				parametros.put("alerta", bundle.getString("mensaje.comisiones.reglamento.validacion.plan"));
			}
			
			mv = doConsulta(request, response, geeSubentidadesBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de alta una distribucion de GGE: " + be.getMessage());			
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new GGESubentidades());
		}
		LOGGER.debug("end - doAlta");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doActualizaPlan(HttpServletRequest request, HttpServletResponse response,GGESubentidades gGESubentidadesBean){
		logger.debug("init - doActualizaPlan");
		String idplan = StringUtils.nullToString(request.getParameter("idplan"));
		JSONObject resultado =  new JSONObject();
		GGEEntidades ent=null;
		try {			
			ent=GGEEntidadesManager.getEntidadByIdPlan(new Long(idplan));
			if(ent!=null){
				resultado.put("gsa",ent.getPctsectoragricola().toString());				
				resultado.put("ge",ent.getPctentidades().toString());
				resultado.put("rga",ent.getPctrga().toString());
				resultado.put("plan",ent.getPlan().toString());
			}
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al doActualizaPlan: " + be.getMessage());			
		} catch (JSONException e) {
			logger.error("Se ha producido un error al doActualizaPlan: " + e.getMessage());		
		}	
			logger.debug("fin - doActualizaPlan");
			getWriterJSON(response,resultado);
			return null;
	}
	
	public ModelAndView doReplicar (HttpServletRequest request, HttpServletResponse response,GGESubentidades gGESubentidadesBean) throws Exception{
		logger.debug("init - doReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<GGESubentidades> listgeeSubentidadesBean = null;
		GGEEntidades GGEEntidades = null;
		ModelAndView mv = null;
		ArrayList<Integer> errList = null;
		String planOrigen = "";
		String planDestino = "";
		try {
			planOrigen = StringUtils.nullToString(request.getParameter("planorigen"));
			planDestino = StringUtils.nullToString(request.getParameter("plannuevo"));
			logger.debug("Paremtros para la replicacion. PlanOrigen:"+planOrigen + "||PlanDestno:"+ planDestino);
			
			logger.debug("Replicamos el plan Origen");
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			errList = GGESubEntidadesManager.replicarPlan(planOrigen,planDestino, usuario);
			logger.debug("Errores de validacion: " + errList.size());
			
			if(errList.size() > 0)
				gGESubentidadesBean = new GGESubentidades();	
			else{
				GGEEntidades = GGEEntidadesManager.getLastGGEPlan(new Long(planDestino)); 				
				GGEEntidadesHistoricoManager.addResgitroHist(GGEEntidades, ComisionesConstantes.AccionesHistComisionCte.ALTA);	
				LOGGER.debug("generamos el registro de historico del nuevo plan");	
				
				gGESubentidadesBean.setPlan(new Long(planDestino));
				listgeeSubentidadesBean = GGESubEntidadesManager.getListGGESubentidades(gGESubentidadesBean);
				LOGGER.debug("listado de distribuciones gge replicadas. Size: " +listgeeSubentidadesBean.size());
				LOGGER.debug("generamos el registro de historico de las nuevas distribuciones gge replicadas");				
				GGESubEntidadesHistoricoManager.addResgitroHistReplicar(listgeeSubentidadesBean);
			}
			GGESubentidades filtroSubentidades = (GGESubentidades) request.getSession().getAttribute("filtroSubentidades");
			if (filtroSubentidades != null) {
				gGESubentidadesBean = filtroSubentidades;
			}
			parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.distMed.replicar.OK"));
			
			mv = doConsulta(request, response, gGESubentidadesBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al replicar distribucion de mediadores para un plan",be);
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new GGESubentidades());
		}
		logger.debug("end - doReplicar");
		return mv.addAllObjects(parametros);
	}
	
	
	/**
	 * Metodo que genera los mensajes de errores de validacion que se mostrarán en la jsp o el mensaje de todo correcto
	 * @param errList
	 * @param msjOK
	 * @return
	 */
	private Map<String, Object> gestionMensajes(ArrayList<Integer> errList,String msjOK){
		Map<String, Object> parametros = new HashMap<String, Object>();
		ArrayList<String> erroresWeb = new ArrayList<String>();
		
//		MENSAJES DE ERRORES DE VALIDACION
		if(errList.size() > 0){
			for(Integer error : errList){
				switch (error) {
				case 1:
						erroresWeb.add(bundle.getString("mensaje.comisiones.GGE.validacion.plan"));
						break;
				case 2:
						erroresWeb.add(bundle.getString("mensaje.comisiones.GGE.validacion.entSubent"));
						break;
				case 3:
						erroresWeb.add(bundle.getString("mensaje.comisiones.GGE.validacion.replicar.plan.origen"));
						break;
				case 4:
						erroresWeb.add(bundle.getString("mensaje.comisiones.GGE.validacion.replicar.plan.destino"));
						break;
				case 5:
						erroresWeb.add(bundle.getString("mensaje.comisiones.GGE.validacion.replicar.plan.destino.duplicado"));
						break;
				default:
						break;
				}
			}
			parametros.put("alerta2", erroresWeb);
		}else{
			parametros.put("mensaje", msjOK);
		}
		
		return parametros;
	}
	
	public void setGGEEntidadesManager(GGEEntidadesManager entidadesManager) {
		GGEEntidadesManager = entidadesManager;
	}

	public void setGGESubEntidadesManager(GGESubEntidadesManager subEntidadesManager) {
		GGESubEntidadesManager = subEntidadesManager;
	}

	public void setGGEEntidadesHistoricoManager(GGEEntidadesHistoricoManager entidadesHistoricoManager) {
		GGEEntidadesHistoricoManager = entidadesHistoricoManager;
	}

	public void setGGESubEntidadesHistoricoManager(	GGESubEntidadesHistoricoManager subEntidadesHistoricoManager) {
		GGESubEntidadesHistoricoManager = subEntidadesHistoricoManager;
	}
}

