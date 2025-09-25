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
import com.rsi.agp.core.managers.impl.ReglamentoHistoricoManager;
import com.rsi.agp.core.managers.impl.ReglamentoManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.core.webapp.util.BigDecimalEditor;
import com.rsi.agp.core.webapp.util.LongEditor;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Reglamento;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ReglamentoController extends BaseMultiActionController{

	private static final Log logger = LogFactory.getLog(ReglamentoController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private ReglamentoHistoricoManager reglamentoHistoricoManager;
	private ReglamentoManager reglamentoManager;
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(BigDecimal.class, null, new BigDecimalEditor());
		binder.registerCustomEditor(Long.class, null, new LongEditor());
	}
	
	public ModelAndView doConsulta (HttpServletRequest request, HttpServletResponse response,Reglamento reglamentoBean) throws Exception{
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<Reglamento> listReglamentos = null;
		try {		
			logger.debug("se comprueban si existen los parametros que se envian desde la pantalla de incidencias");	
			if (request.getParameter("entidadmediadora") != null){
				reglamentoBean.getEntidad().setCodentidad(new BigDecimal(request.getParameter("entidadmediadora")));				
			}			
							
			if (request.getParameter("planIncidencias") != null){
				reglamentoBean.setPlan(new Long(request.getParameter("planIncidencias")));
			}	
			
			listReglamentos = reglamentoManager.listReglamentos(reglamentoBean);
			logger.debug("listReglamentos. Size=" + listReglamentos.size());
			
			parametros = reglamentoManager.getPlanesReplicar(listReglamentos);
			
			parametros.put("listReglamentos", listReglamentos);
			
			
			// Al entrar recogemos los valore id fichero y tipo ya que los necesitaremos para poder volver a pagina anterior 
			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null )
			{
				parametros.put("idFichero",request.getParameter("idFichero"));
				parametros.put("tipoFichero",request.getParameter("tipoFichero"));	
			} else {
				parametros.put("idFichero","");
				parametros.put("tipoFichero","");	
			}
			request.getSession().removeAttribute("filtroReglamentos");
			if (reglamentoBean !=  null) {
				request.getSession().setAttribute("filtroReglamentos", reglamentoBean);
			}
			
			mv = new ModelAndView("moduloComisiones/reglamentos","reglamentoBean",reglamentoBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new Reglamento());
		}
		
		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}

	
	public ModelAndView doAlta (HttpServletRequest request, HttpServletResponse response,Reglamento reglamentoBean) throws Exception{
		logger.debug("init - doAlta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = null;
		ArrayList<Integer> errList = null;
		try {
			logger.debug("comprobamos que no haya registros duplicados");
			if(!reglamentoManager.existeRegistro(reglamentoBean)){
				logger.debug("alta del registro reglamento");
				if(reglamentoBean.getId() == null){
						
						usuario = (Usuario) request.getSession().getAttribute("usuario");
						reglamentoBean.setUsuario(usuario);
						
						logger.debug("alta del registro de reglamento");
						errList = reglamentoManager.guardarReglamento(reglamentoBean);
						logger.debug("Errores Validacion=" + errList.size());
						
						if(errList.size() > 0)
							reglamentoBean = new Reglamento();
						else{
							logger.debug("creamos registro en el historico");
							reglamentoHistoricoManager.addResgitroHist(reglamentoBean,ComisionesConstantes.AccionesHistComisionCte.ALTA);
						}
							
						parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.reglamento.alta.OK"));
					
				}else{
					logger.debug("modificacion del registro de reglamento. id:" + reglamentoBean.getId());
					errList = reglamentoManager.guardarReglamento(reglamentoBean);
					logger.debug("Errores Validacion=" + errList.size());
					
					if(errList.size() > 0)
						reglamentoBean = reglamentoManager.getReglamento(reglamentoBean.getId());
					else{
						logger.debug("creamos registro en el historico");
						reglamentoHistoricoManager.addResgitroHist(reglamentoBean,ComisionesConstantes.AccionesHistComisionCte.MODIFICACION);
					}
					
					parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.reglamento.modificacion.OK"));
				}
			}else{
					logger.debug("registro de reglamento duplicado");
					parametros.put("alerta", bundle.getString("mensaje.comisiones.reglamento.alta.KO"));
					reglamentoBean = new Reglamento();
			}
			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null )
			{
				parametros.put("idFichero",request.getParameter("idFichero"));
				parametros.put("tipoFichero",request.getParameter("tipoFichero"));	
			}
			
			mv = doConsulta(request, response, reglamentoBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de alta/editar un reglamentos: " + be.getMessage());			
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new Reglamento());
		}
		
		logger.debug("end - doAlta");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doBaja (HttpServletRequest request, HttpServletResponse response,Reglamento reglamentoBean) throws Exception{
		logger.debug("init - doBaja");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		try {
			reglamentoBean = reglamentoManager.getReglamento(reglamentoBean.getId());
			
			reglamentoManager.borrarReglamento(reglamentoBean);
			parametros.put("mensaje", bundle.getString("mensaje.comisiones.reglamento.baja.OK"));
			
			logger.debug("alta del registro en el historico. id:" + reglamentoBean.getId());
			reglamentoHistoricoManager.addResgitroHist(reglamentoBean,ComisionesConstantes.AccionesHistComisionCte.BAJA);
			
			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null )
			{
				parametros.put("idFichero",request.getParameter("idFichero"));
				parametros.put("tipoFichero",request.getParameter("tipoFichero"));	
			}
			
			mv = doConsulta(request, response, new Reglamento());
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de baja unreglamento: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new Reglamento());
		}
		
		logger.debug("end - doBaja");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doReplicar (HttpServletRequest request, HttpServletResponse response,Reglamento reglamentoBean) throws Exception{
		logger.debug("init - doReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<Reglamento> listgeeSubentidadesBean = null;
		ModelAndView mv = null;
		ArrayList<Integer> errList = null;
		String planOrigen = "";
		String planDestino = "";
		try {
			planOrigen = StringUtils.nullToString(request.getParameter("planorigen"));
			planDestino = StringUtils.nullToString(request.getParameter("plannuevo"));
			logger.debug("Paremtros para la replicacion. PlanOrigen:"+planOrigen + "||PlanDestno:"+ planDestino);
			
			logger.debug("Replicamos el plan Origen");
			errList = reglamentoManager.replicarPlan(planOrigen,planDestino);
			logger.debug("Errores de validacion: " + errList.size());
			
			if(errList.size() > 0)
				reglamentoBean = new Reglamento();	
			else{

				reglamentoBean.setPlan(new Long(planDestino));
				listgeeSubentidadesBean = reglamentoManager.listReglamentos(reglamentoBean);
				logger.debug("listado de distribuciones gge replicadas. Size: " +listgeeSubentidadesBean.size());
				logger.debug("generamos el registro de historico");
				reglamentoHistoricoManager.addResgitroHistReplicar(listgeeSubentidadesBean);
			}
			
			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null )
			{
				parametros.put("idFichero",request.getParameter("idFichero"));
				parametros.put("tipoFichero",request.getParameter("tipoFichero"));	
			}
			Reglamento filtroReglamentos = (Reglamento) request.getSession().getAttribute("filtroReglamentos");
			if (filtroReglamentos != null) {
				reglamentoBean = filtroReglamentos;
			}
			parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.distMed.replicar.OK"));
			
			mv = doConsulta(request, response, reglamentoBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al replicar distribucion de mediadores para un plan",be);
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new Reglamento());
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
						erroresWeb.add(bundle.getString("mensaje.comisiones.reglamento.validacion.plan"));
						break;
				case 2:
						erroresWeb.add(bundle.getString("mensaje.comisiones.reglamento.validacion.ent"));
						break;
				case 3:
						erroresWeb.add(bundle.getString("mensaje.comisiones.reglamento.validacion.replicar.plan.origen"));
						break;
				case 4:
						erroresWeb.add(bundle.getString("mensaje.comisiones.reglamento.validacion.replicar.plan.destino"));
						break;
				case 5:
						erroresWeb.add(bundle.getString("mensaje.comisiones.reglamento.validacion.replicar.plan.destino.duplicado"));
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
	

	public void setReglamentoHistoricoManager(ReglamentoHistoricoManager reglamentoHistoricoManager) {
		this.reglamentoHistoricoManager = reglamentoHistoricoManager;
	}

	public void setReglamentoManager(ReglamentoManager reglamentoManager) {
		this.reglamentoManager = reglamentoManager;
	}
	
	
}
