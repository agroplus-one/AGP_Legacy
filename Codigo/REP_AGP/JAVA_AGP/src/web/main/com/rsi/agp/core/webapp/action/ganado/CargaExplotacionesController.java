package com.rsi.agp.core.webapp.action.ganado;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.ICargaExplotacionesManager;
import com.rsi.agp.core.managers.ICargaExplotacionesManager.IsRes;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.ganado.ICargaExplotacionesDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;



public class CargaExplotacionesController extends BaseMultiActionController {
	
	// Constantes
	private static final String MENSAJE_CARGA_EXPLOTACIONES_SITUACION_ACTUALIZADA_KO = "mensaje.CargaExplotaciones.SituacionActualizada.KO";
	private static final String DO_CARGA_SITUACION_ACTUALIZADA = "doCargaSituacionActualizada";
	private static final String MODULO_POLIZAS_POLIZAS_ELEGIR_POLIZA_A_CARGAR = "moduloPolizas/polizas/elegirPolizaACargar";
	private static final String DO_METHOD_CARGA_EXPLO = "doMethodCargaExplo";
	private static final String CARGA_EXPLOTACIONES = "cargaExplotaciones";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	private static final String LISTA_POLIZAS = "listaPolizas";
	private static final String MENSAJE = "mensaje";
	private static final String ALERTA = "alerta";
	private static final String REDIRECT_LISTADO_EXPLOTACIONES_HTML = "redirect:/listadoExplotaciones.html";
	private static final String POLIZA_BEAN = "polizaBean";
	private static final String IDPOLIZA = "idpoliza";
	
	private ICargaExplotacionesManager cargaExplotacionesManager;
	private ICargaExplotacionesDao cargaExplotacionesDao;
	private static final Log logger = LogFactory.getLog(CargaExplotacionesController.class);
	
	public ModelAndView doMenuCargaExplotaciones(HttpServletRequest request, HttpServletResponse response) {
		//Definicion de variables
		ModelAndView mv=null;
		String strIdPoliza=null;
		Long idPoliza=null;
		Poliza poliza=null; 	
		BigDecimal plan=null;	
		BigDecimal linea=null;	
		Long idAsegurado=null;
		
		Boolean haySistemaTradicional= null; 
		Boolean haySitActualizada=null;
		Boolean hayPolAnterior=null;		
		Boolean hayPlanActualPoliza=null;
		
		Long idPolizaPlanActual=null;	
		Long idPolizaSitActualizada=null;	
		Long idPolizaAnterior=null;
	
		Map<String, Object> parameters = new HashMap<String, Object>();
		IsRes isres=null;
		
		try {
			//Variables de entrada
			strIdPoliza = request.getParameter(IDPOLIZA);		
			if(strIdPoliza != null && !strIdPoliza.isEmpty()) idPoliza = new Long(strIdPoliza);		
			//seleccionOrigen=StringUtils.nullToString(request.getParameter("seleccionOrigen"));
			
			
			//variables de proceso
			poliza = (Poliza) cargaExplotacionesDao.getObject(Poliza.class, idPoliza);
			plan = poliza.getLinea().getCodplan();
			linea = poliza.getLinea().getCodlinea();
			idAsegurado = poliza.getAsegurado().getId();		
			
			//Parametros de salida		
			haySistemaTradicional = cargaExplotacionesManager.isPolizaAnteriorSistemaTradicional(plan, linea);		
			
			isres = cargaExplotacionesManager.isPolizaPlanActual(idAsegurado, plan, linea, poliza.getIdpoliza());
			hayPlanActualPoliza = isres.getRes(); 		
			idPolizaPlanActual = isres.getIdPoliza();
			
			isres = cargaExplotacionesManager.isSituacionActualizadaAgroseguro(idAsegurado, plan, linea, poliza.getIdpoliza());
			haySitActualizada = isres.getRes(); 	
			idPolizaSitActualizada = isres.getIdPoliza();
			
			if(!haySitActualizada){
				isres = cargaExplotacionesManager.isPolizaOriginalUltimosPlanes(idAsegurado, plan, linea, poliza.getIdpoliza());
				hayPolAnterior = isres.getRes();	
				idPolizaAnterior = isres.getIdPoliza();
			}else{
				hayPolAnterior = true;
			}
			
		} catch (Exception e) {
			logger.error("### CargaExplotacionesController.doMenuCargaExplotaciones ", e);
		}
		
		parameters.put(IDPOLIZA, idPoliza);
		parameters.put("haySistemaTradicional", haySistemaTradicional);
		parameters.put("hayPlanActualPoliza", hayPlanActualPoliza);
		parameters.put("idPolizaPlanActual",idPolizaPlanActual);
		parameters.put("haySitActualizada", haySitActualizada);
		parameters.put("idPolizaSitActualizada",idPolizaSitActualizada);
		parameters.put("hayPolAnterior", hayPolAnterior);
		parameters.put("idPolizaAnterior",idPolizaAnterior);
		parameters.put("planAnterior",plan.subtract(new BigDecimal(1)));
		
		
		//Salida
		mv = new ModelAndView("moduloExplotaciones/explotaciones/cargaExplotaciones", POLIZA_BEAN, poliza).addAllObjects(parameters);
		
		return mv;
	}
	
	public ModelAndView doNoCargarExplotaciones(HttpServletRequest request, HttpServletResponse response) {
		
		//DefiniciÃ³n de variables
		ModelAndView mv=null;
		String strIdPoliza=null;	Long idPoliza=null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		//******************************************************************************		
		try {
			//Variables de entrada
			strIdPoliza=request.getParameter(IDPOLIZA);		
			if(strIdPoliza!=null && !strIdPoliza.isEmpty()) idPoliza= new Long(strIdPoliza);
			//******************************************************************************
			
			//Proceso
			cargaExplotacionesManager.actualizarIdCargaExplotaciones(idPoliza, Constants.ID_CARGA_EXPLOT_NO_CARGAR_NINGUNA);
			//******************************************************************************
			
			//Salida
			parameters.put(IDPOLIZA, idPoliza);
			//parameters.put("linea.lineaseguroid", explotacionBean.getPoliza().getLinea().getLineaseguroid());
			mv = new ModelAndView(REDIRECT_LISTADO_EXPLOTACIONES_HTML).addAllObjects(parameters);
			return mv;
			//******************************************************************************
		} catch (Exception e) {
			logger.error("### CargaExplotacionesController.doNoCargarExplotaciones ", e);
		}
		return mv;
	}
	
	@SuppressWarnings("finally")
	public ModelAndView doCargaPolizaSistemaTradicional(HttpServletRequest request, HttpServletResponse response) {
		//Variables de entrada
		String strIdPoliza=request.getParameter("idpoliza_SistTrad");
		String strPlan=request.getParameter("plan_SistTrad");
		String referencia= StringUtils.nullToString(request.getParameter("referencia_SistTrad"));
		Long idPoliza=null;		Poliza poliza=null;		Usuario usuario=null;
		BigDecimal plan=null;
		String realPath = null;
		if(strPlan!=null && !strPlan.isEmpty()) plan= new BigDecimal(strPlan);
		if(strIdPoliza!=null && !strIdPoliza.isEmpty()) idPoliza= new Long(strIdPoliza);
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String mensaje=null;
		String mensajeErrorSW=null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv=null;
		//******************************************************************************
		try {
			//Proceso
			poliza=(Poliza) cargaExplotacionesDao.get(Poliza.class, idPoliza);
			usuario = (Usuario) request.getSession().getAttribute("usuario");
				//Ejecutará el método "actualizarIdCargaExplotacionesâ" del manager indicando el valor 1 (por constante).
			cargaExplotacionesManager.actualizarIdCargaExplotaciones(idPoliza, Constants.ID_CARGA_EXPLOT_SISTEMA_TRADICIONAL);
			poliza.setIdCargaExplotaciones(Constants.ID_CARGA_EXPLOT_SISTEMA_TRADICIONAL);
				//Ejecutará el método "cargaPolizaSistemaTradicional" de "CargaExplotacionesManager" pasando los parámetros recibidos de la pantalla.
			realPath = this.getServletContext().getRealPath("/WEB-INF/");
			mensajeErrorSW= cargaExplotacionesManager.cargaPolizaSistemaTradicional(plan, referencia, realPath, 
					idPoliza, usuario, poliza.getLinea().getLineaseguroid(), poliza);
				//Si la ejecución del método anterior ha sido correcta, redirigirá a la pantalla de listado de explotaciones mostrando el mensaje Explotaciones cargadas de la poliza de sistema tradicional <plan> - <referencia> (resaltado en amarillo)
			if (null==mensajeErrorSW){
				mensaje=bundle.getString("mensaje.CargaExplotaciones.SistemaTradicional.OK");
				mensaje= mensaje + " "  + strPlan + " - " + referencia; 
				parameters.put(MENSAJE, mensaje);
			}else{
				logger.error("### CargaExplotacionesController.doCargaPolizaSistemaTradicional " + mensajeErrorSW);
				mensaje=bundle.getString("mensaje.CargaExplotaciones.SistemaTradicional.KO");
				mensaje= mensaje + " "  + strPlan + " - " + referencia + ". " + mensajeErrorSW; 
				parameters.put(ALERTA, mensaje);
			}			
			//******************************************************************************		

			}catch (Exception e) {
				logger.error("### CargaExplotacionesController.doCargaPolizaSistemaTradicional ", e);
				mensaje=bundle.getString("mensaje.CargaExplotaciones.SistemaTradicional.KO");
				mensaje= mensaje + " "  + strPlan + " - " + referencia; 
				parameters.put(ALERTA, mensaje);
			}finally{
				//Salida
				parameters.put(IDPOLIZA, idPoliza);				
				mv = new ModelAndView(REDIRECT_LISTADO_EXPLOTACIONES_HTML).addAllObjects(parameters);
				return mv;
				//******************************************************************************
			}		
		
	}

	@SuppressWarnings("finally")
	public ModelAndView doCargaSituacionActualizada(HttpServletRequest request, HttpServletResponse response) {
		//Variables de entrada
		String strIdPoliza=null;		
		String strIdPolizaSitActualizada= null;
		Long idPolizaSitActualizada=null;				Long idAsegurado=null;
		Long idPoliza=null;		Poliza poliza=null; 	BigDecimal codplan=null; BigDecimal codlinea=null;
		String realPath = null;
		
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String mensaje=null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv=null;			
		
		try {
			strIdPoliza= request.getParameter(IDPOLIZA);
			if(strIdPoliza!=null && !strIdPoliza.isEmpty()) idPoliza= new Long(strIdPoliza);
			
			strIdPolizaSitActualizada= StringUtils.nullToString(request.getParameter("idPolizaSitActualizada"));
			if(strIdPolizaSitActualizada!=null && !strIdPolizaSitActualizada.isEmpty())
				idPolizaSitActualizada=new Long(strIdPolizaSitActualizada);
			//******************************************************************************
			//Proceso
			poliza=(Poliza) cargaExplotacionesDao.get(Poliza.class, idPoliza);			
			realPath = this.getServletContext().getRealPath("/WEB-INF/");
			cargaExplotacionesManager.actualizarIdCargaExplotaciones(idPoliza, Constants.ID_CARGA_EXPLOT_SITUACION_ACTUALIZADA);
			poliza.setIdCargaExplotaciones(Constants.ID_CARGA_EXPLOT_SITUACION_ACTUALIZADA);
			
			if (null!=strIdPolizaSitActualizada && !strIdPolizaSitActualizada.isEmpty()){
				cargaExplotacionesManager.cargaSituacionActualizada(idPolizaSitActualizada,realPath, poliza);
				mensaje=bundle.getString("mensaje.CargaExplotaciones.SituacionActualizada.OK");
				parameters.put(MENSAJE, mensaje);				
			}else{
				idAsegurado=poliza.getAsegurado().getId();
				codplan=poliza.getLinea().getCodplan();
				codlinea=poliza.getLinea().getCodlinea();
				List<Poliza>lstPolizas=cargaExplotacionesManager.listaPlzSituacionActualizada(idAsegurado, codplan, 
						codlinea,poliza.getIdpoliza());
				parameters.put(IDPOLIZA, idPoliza);
				parameters.put(LISTA_POLIZAS, lstPolizas);
				parameters.put(ORIGEN_LLAMADA,CARGA_EXPLOTACIONES);
				parameters.put(DO_METHOD_CARGA_EXPLO,DO_CARGA_SITUACION_ACTUALIZADA);
				mv = new ModelAndView(MODULO_POLIZAS_POLIZAS_ELEGIR_POLIZA_A_CARGAR,POLIZA_BEAN, poliza).addAllObjects(parameters);
			}
			//******************************************************************************
		}catch(SOAPFaultException e){
			logger.error("### CargaExplotacionesController.doCargaSituacionActualizada ", e);
			mensaje=bundle.getString(MENSAJE_CARGA_EXPLOTACIONES_SITUACION_ACTUALIZADA_KO);
			String mensajeErrorSW = WSUtils.debugAgrException(e);
			mensaje= mensaje + ". " + mensajeErrorSW; 
			parameters.put(ALERTA, mensaje);
		}catch(AgrException e){
			logger.error("### CargaExplotacionesController.doCargaSituacionActualizada ", e);
			mensaje=bundle.getString(MENSAJE_CARGA_EXPLOTACIONES_SITUACION_ACTUALIZADA_KO);
			String mensajeErrorSW = WSUtils.debugAgrException(e);
			mensaje= mensaje + ". " + mensajeErrorSW; 
			parameters.put(ALERTA, mensaje);
		} catch (Exception e) {
			logger.error("### CargaExplotacionesController.doCargaSituacionActualizada ", e);
			mensaje=bundle.getString(MENSAJE_CARGA_EXPLOTACIONES_SITUACION_ACTUALIZADA_KO);
			parameters.put(ALERTA, mensaje);
		}finally{
			//Salida
			if(null==mv){
				parameters.put(IDPOLIZA, idPoliza);
				mv = new ModelAndView(REDIRECT_LISTADO_EXPLOTACIONES_HTML).addAllObjects(parameters);
			}
			return mv;
			//******************************************************************************
		}
	}
	
	
	@SuppressWarnings("finally")
	public ModelAndView doCargaPolizaOriginalUltimosPlanes(HttpServletRequest request, HttpServletResponse response) {
		//Variables de entrada
		String strIdPoliza=null;		
		String strIdPolizaOriginalUP= null;// para pÃ³liza seleccionada
		Long idPolizaOriginalUP=null;		Long idAsegurado=null;
		Long idPoliza=null;					Poliza poliza=null; 	
		BigDecimal codplan=null; 			BigDecimal codlinea=null;
		
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String mensaje=null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv=null;			
		
		try {
			strIdPoliza= request.getParameter(IDPOLIZA);
			if(strIdPoliza!=null && !strIdPoliza.isEmpty()) idPoliza= new Long(strIdPoliza);
			
			strIdPolizaOriginalUP= StringUtils.nullToString(request.getParameter("idPolizaAnterior"));
			if(strIdPolizaOriginalUP!=null && !strIdPolizaOriginalUP.isEmpty())
				idPolizaOriginalUP=new Long(strIdPolizaOriginalUP);
			//******************************************************************************
			//Proceso
			poliza=(Poliza) cargaExplotacionesDao.get(Poliza.class, idPoliza);			
			//realPath = this.getServletContext().getRealPath("/WEB-INF/");
			cargaExplotacionesManager.actualizarIdCargaExplotaciones(idPoliza, Constants.ID_CARGA_EXPLOT_ORIGINAL);
			poliza.setIdCargaExplotaciones(Constants.ID_CARGA_EXPLOT_ORIGINAL);
			
			if (null!=strIdPolizaOriginalUP && !strIdPolizaOriginalUP.isEmpty()){
				cargaExplotacionesManager.cargaExplotacionesPolizaExistente(idPolizaOriginalUP, poliza);
				mensaje=bundle.getString("mensaje.CargaExplotaciones.OriginalUltimosPlanes.OK");
				parameters.put(MENSAJE, mensaje);				
			}else{
				idAsegurado=poliza.getAsegurado().getId();
				codplan=poliza.getLinea().getCodplan();
				codlinea=poliza.getLinea().getCodlinea();
				List<Poliza>lstPolizas=cargaExplotacionesManager.listaPolizaOriginalUltimosPlanes(idAsegurado, codplan, 
						codlinea,poliza.getIdpoliza());
				
				if(lstPolizas.size()==1){
					cargaExplotacionesManager.cargaExplotacionesPolizaExistente(lstPolizas.get(0).getIdpoliza(), poliza);
					mensaje=bundle.getString("mensaje.CargaExplotaciones.OriginalUltimosPlanes.OK");
					parameters.put(MENSAJE, mensaje);
				}else{				
					parameters.put(IDPOLIZA, idPoliza);
					parameters.put(LISTA_POLIZAS, lstPolizas);
					parameters.put(ORIGEN_LLAMADA,CARGA_EXPLOTACIONES);
					parameters.put(DO_METHOD_CARGA_EXPLO,"doCargaPolizaOriginalUltimosPlanes");
					mv = new ModelAndView(MODULO_POLIZAS_POLIZAS_ELEGIR_POLIZA_A_CARGAR,POLIZA_BEAN, poliza).addAllObjects(parameters);
				}
			}
			//******************************************************************************
		} catch (Exception e) {
			logger.error("### CargaExplotacionesController.doCargaPolizaOriginalUltimosPlanes ", e);
			mensaje=bundle.getString("mensaje.CargaExplotaciones.OriginalUltimosPlanes.KO");
			parameters.put(ALERTA, mensaje);
		}finally{
			//Salida
			if(null==mv){
				parameters.put(IDPOLIZA, idPoliza);
				mv = new ModelAndView(REDIRECT_LISTADO_EXPLOTACIONES_HTML).addAllObjects(parameters);
			}
			return mv;
			//******************************************************************************
		}
	}
	
	public ModelAndView doCargaPolizaPlanActual(HttpServletRequest request, HttpServletResponse response) {
		//Variables de entrada
				String strIdPoliza=null;		
				String strIdPolizaPlanActual= null;//para pÃ³liza seleccionada
				Long idPolizaPlanActual=null;		Long idAsegurado=null;
				Long idPoliza=null;					Poliza poliza=null; 	
				BigDecimal codplan=null; 			BigDecimal codlinea=null;
				
				ResourceBundle bundle = ResourceBundle.getBundle("agp");
				String mensaje=null;
				Map<String, Object> parameters = new HashMap<String, Object>();
				ModelAndView mv=null;			
				
				try {
					strIdPoliza= request.getParameter(IDPOLIZA);
					if(strIdPoliza!=null && !strIdPoliza.isEmpty()) idPoliza= new Long(strIdPoliza);
					
					strIdPolizaPlanActual= StringUtils.nullToString(request.getParameter("idPolizaPlanActual"));
					if(strIdPolizaPlanActual!=null && !strIdPolizaPlanActual.isEmpty())
						idPolizaPlanActual=new Long(strIdPolizaPlanActual);
					//******************************************************************************
					//Proceso
					poliza=(Poliza) cargaExplotacionesDao.get(Poliza.class, idPoliza);
					cargaExplotacionesManager.actualizarIdCargaExplotaciones(idPoliza, Constants.ID_CARGA_EXPLOT_EXISTENTE);
					poliza.setIdCargaExplotaciones(Constants.ID_CARGA_EXPLOT_EXISTENTE);
					
					if (null!=strIdPolizaPlanActual && !strIdPolizaPlanActual.isEmpty()){
						cargaExplotacionesManager.cargaExplotacionesPolizaExistente(idPolizaPlanActual, poliza);
						mensaje=bundle.getString("mensaje.CargaExplotaciones.PlanActual.OK");
						parameters.put(MENSAJE, mensaje);				
					}else{
						idAsegurado=poliza.getAsegurado().getId();
						codplan=poliza.getLinea().getCodplan();
						codlinea=poliza.getLinea().getCodlinea();
						List<Poliza>lstPolizas=cargaExplotacionesManager.
								listaPolizaPlanActual(idAsegurado, codplan, codlinea,poliza.getIdpoliza());
						parameters.put(IDPOLIZA, idPoliza);
						parameters.put(LISTA_POLIZAS, lstPolizas);
						parameters.put(ORIGEN_LLAMADA,CARGA_EXPLOTACIONES);
						parameters.put(DO_METHOD_CARGA_EXPLO,"doCargaPolizaPlanActual");
						mv = new ModelAndView(MODULO_POLIZAS_POLIZAS_ELEGIR_POLIZA_A_CARGAR,POLIZA_BEAN, poliza).addAllObjects(parameters);
					}
					//******************************************************************************
				} catch (Exception e) {
					logger.error("### CargaExplotacionesController.doCargaPolizaPlanActual ", e);
					mensaje=bundle.getString("mensaje.CargaExplotaciones.PlanActual.KO");
					parameters.put(ALERTA, mensaje);
				}finally{
					//Salida
					if(null==mv){
						parameters.put(IDPOLIZA, idPoliza);
						mv = new ModelAndView(REDIRECT_LISTADO_EXPLOTACIONES_HTML).addAllObjects(parameters);
					}
					//******************************************************************************
				}
				
				return mv;
	}
	
	
	public void setCargaExplotacionesManager(
			ICargaExplotacionesManager cargaExplotacionesManager) {
		this.cargaExplotacionesManager = cargaExplotacionesManager;
	}
	public void setCargaExplotacionesDao(
			ICargaExplotacionesDao cargaExplotacionesDao) {
		this.cargaExplotacionesDao = cargaExplotacionesDao;
	}


	
	
}
