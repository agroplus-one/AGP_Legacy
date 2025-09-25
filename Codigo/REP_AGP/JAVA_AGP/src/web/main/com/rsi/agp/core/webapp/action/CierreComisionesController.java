package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DatosConsultaComException;
import com.rsi.agp.core.jmesa.service.ICierreComisionesService;
import com.rsi.agp.core.managers.impl.CierreComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.ReportCierre;
import com.rsi.agp.dao.tables.commons.Usuario;
 
public class CierreComisionesController extends BaseMultiActionController{// implements Runnable{
	private static final Log logger = LogFactory.getLog(CierreComisionesController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private ICierreComisionesService cierreComisionesService;
	private CierreComisionesManager cierreComisionesManager;
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Cierre cierreBean)throws Exception{
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		GregorianCalendar gc = null;
		boolean periodoCerrado = false;
		int fasesSinCierre = 0;
		try {
			
			logger.debug("Recuperamos la fecha del ultimo dÌa del mes anterior");
			gc = new GregorianCalendar();
			gc.add(Calendar.MONTH, -1);
			gc.set(Calendar.DAY_OF_MONTH,gc.getActualMaximum(Calendar.DAY_OF_MONTH));
			logger.debug("fecha: " + gc.getTime().toString());
			if (cierreBean!= null) {
				if (cierreBean.getId()!= null) {
					parametros.put("idCierre", cierreBean.getId());
				}
			}
			
			// comprobamos si viene de ajax porque se ha cerrado el periodo ok
			// para mostrar los mensajes
			if (StringUtils.nullToString(request.getParameter("isPeriodoCerradoOK")).equals("true")) {
				parametros.put("mensaje", bundle.getString("mensaje.comisiones.cierre.OK"));
				parametros.put("mensaje2", bundle.getString("mensaje.informes.OK"));
			}
			
			cierreBean.setFechacierre(gc.getTime());
			
			fasesSinCierre = cierreComisionesManager.getFasesSinCierre(gc.getTime());
			logger.debug("Fases sin cerrar: " + fasesSinCierre);
			
			periodoCerrado = cierreComisionesManager.periodoCerrado(gc.getTime());
			logger.debug("Periodo cerrado:  " + periodoCerrado);
			
			parametros.put("fasesSinCierre", fasesSinCierre);
			parametros.put("periodoCerrado", periodoCerrado);
			
			// Mostrar listado de cierres.TMR.4-10-2012
			String origenLlamada = request.getParameter("origenLlamada");
			
			//idBorrable ser· el id de cierre que se pueda borrar (el del icono de la papelera)
			String idBorrableStr = null;
			if(fasesSinCierre==0){
				Long idBorrable = cierreComisionesManager.obtenerIdCierreMasReciente();
				if(idBorrable!=null){
					idBorrableStr = idBorrable.toString();
				}
			}
			request.setAttribute("idBorrable", idBorrableStr);

			logger.info("CierreComisionesController - doConsulta: Comienza la b√∫squeda cierres");
			String html = cierreComisionesService.getTablaCierre(request, response, cierreBean, origenLlamada);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents;
					try {
						contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
					} catch (UnsupportedEncodingException e) {
						logger.error("Error:" + e);
					} catch (IOException e) {
						logger.error("Error:" + e);
					}
		
					return null;
				} else
					// Pasa a la jsp el c√≥digo de la tabla a trav√©s de este atributo
					request.setAttribute("listadoCierres", html);
			}
			
			
			mv = new ModelAndView("/moduloComisiones/cierreComisiones","cierreBean",cierreBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error general: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new Cierre());
		}
		catch(Exception e){
			logger.error("Se ha producido un error al tramitar el proceso de cerrar ficheros:", e);
			mv = doConsulta(request, response, new Cierre());
		}
		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	@SuppressWarnings("rawtypes")
	public void doCerrarPeriodo(HttpServletRequest request, HttpServletResponse response, Cierre cierreBean)throws Exception{
		logger.debug("init - doCerrarPeriodo");
		
		String fecha = "";
		JSONObject resultado = new JSONObject();
		Boolean lanzarGenInfoExcel = true;
		String res = "";
		Boolean informesClasicos = false;
		Boolean informes2015 = false;
		HashMap params = new HashMap();
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			 
			fecha = StringUtils.nullToString(request.getParameter("fechaCierre"));
			
			if(!fecha.equals("")){
				try {
					params = cierreComisionesManager.cerrarPeriodo(sdf.parse(fecha), usuario);
				} catch (DatosConsultaComException be) {
					logger.error("Se ha producido un error al cerrar los datos para la consulta de Comisiones: " + be.getMessage());
					res = "KO_DATOS_CONSULTA_COMS.";
				} catch(Exception e){
					logger.error("Se ha producido un error al cerrar el periodo:", e);
					res = "KO";
					lanzarGenInfoExcel = false;
				} 
				if (res.equals("")){ // el cierre ha ido bien. generamos los informes
					boolean genInfomeClasico = (Boolean) params.get("cerradoClasico");
					boolean genInfomeUnif = (Boolean) params.get("cerradoUnificado");
					if (lanzarGenInfoExcel){
						
						Cierre cierre = (Cierre) params.get("cierre");
						
						if (genInfomeClasico){
							informesClasicos = cierreComisionesManager.generarInformeExcelClasico(cierre, usuario);
						}else{
							informesClasicos = true;
						}
						if (genInfomeUnif){
							informes2015     = cierreComisionesManager.generarInformeExcel2015(cierre.getId(), cierre.getFechacierre(), cierre.getPeriodo(), usuario);
						}else{
							informes2015 = true;
						}
							
						if (informesClasicos && informes2015){
							// si se ha cerrado el periodo y los informes se han generado bien:
							res = res + "OK";
						}else {
							logger.error("Se ha producido un error al generar los informes:");
							res = res + "ERRORINFORMES";
						}
					}
				}
			}else {
				res = "KO";
			}
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al cerrar el periodo: " + be.getMessage());
			res = "KO";
			
		} catch(Exception e){
			logger.error("Se ha producido un error al cerrar el periodo:", e);
			res = "KO";
		} finally {
			resultado.put("resul", res);
			this.getWriterJSON(response, resultado);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public ModelAndView doValidarCerrarPeriodo(HttpServletRequest request, HttpServletResponse response, Cierre cierreBean)throws Exception{
		logger.debug("init - doValidarCerrarPeriodo");
		ArrayList<String> erroresWeb = new ArrayList<String>();
		JSONObject objeto = new JSONObject();
		StringBuffer listaFicherosError = new StringBuffer();
		StringBuffer todasfasesAceptadas = new StringBuffer();
		//StringBuffer fasesUnifAceptadas = new StringBuffer();
		String fecha = "";
		boolean ficherosAceptados = false;
		Map<String,String> faseaux = new HashMap<String, String>();
		
		try {
			 fecha = StringUtils.nullToString(request.getParameter("fechaCierre"));
			 if(!fecha.equals("")){
				 
				 if(cierreComisionesManager.periodoCerrado(sdf.parse(fecha))){
					 logger.debug("periodo cerrado previamente");
					 erroresWeb.add(bundle.getString("mensaje.comisiones.cierre.cerrado.KO") + "<br><br>");
				 }
				 // Para los ficheros "antiguos" se comprueba si tiene comisiones(nuevas y antiguas) y reglamentos cargados
				if(!cierreComisionesManager.comisionesReglamentosEmitidosCargados(listaFicherosError, sdf.parse(fecha))){					
					logger.debug("comisiones y reglamentos no han sido cargados");
					erroresWeb.add(bundle.getString("mensaje.comisiones.cierre.comisionesReglamentos.KO") + "<br>" + listaFicherosError.toString() + "<br>");
				}
				// Para los ficheros UNIFICADOS se comprueba si se a cargado el de comisiones
				if(!cierreComisionesManager.unificadosComisionesCargados(listaFicherosError, sdf.parse(fecha))){					
					logger.debug("comisiones no han sido cargados");
					erroresWeb.add(bundle.getString("mensaje.comisiones.unificados.cierre.comcargados.KO") + "<br>" + listaFicherosError.toString() + "<br>");
				}
				// Para los ficheros "antiguos" se comprueba si tiene ficheros aceptados			
				if(cierreComisionesManager.ficherosNoAceptados(sdf.parse(fecha),listaFicherosError)){
					logger.debug("hay ficheros por fase no aceptados");
					erroresWeb.add(bundle.getString("mensaje.comisiones.cierre.ficherosAceptados.KO")+ "<br>" + listaFicherosError.toString()+ "<br>");
				}
				// Para los ficheros UNIFICADOS se comprueba si tiene ficheros aceptados	
				if(cierreComisionesManager.ficherosUnificadosNoAceptados(sdf.parse(fecha),listaFicherosError)){
					logger.debug("hay ficheros unificados por fase no aceptados");
					erroresWeb.add(bundle.getString("mensaje.comisiones.unificados.cierre.ficherosAceptados.KO")+ "<br>" + listaFicherosError.toString()+ "<br>");
				}
				// para ficheros "antiguos"
				int fases = cierreComisionesManager.obtenerFasesAceptadas(sdf.parse(fecha),faseaux).size();
				if( fases > 0){					
					ficherosAceptados = true;
					//objeto.put("fasesAceptadas", fasesAceptadas);
				}
				if (erroresWeb.size()< 1 && fases < 1){
					erroresWeb.add(bundle.getString("mensaje.comisiones.cierre.fases"));
				}
				// para ficheros unificados
				int fasesUnif = cierreComisionesManager.obtenerFasesUnificadasAceptadas(sdf.parse(fecha),null,faseaux).size();
				if( fasesUnif > 0){					
					ficherosAceptados = true;
					//if (fasesUnifAceptadas.length()>0)
					//	objeto.put("fasesAceptadasUnificadas", fasesUnifAceptadas);
				}
				
				Iterator it = faseaux.entrySet().iterator();
		    	while(it.hasNext()) {
		    		
		    		Map.Entry ent = (Map.Entry)it.next();
		    		todasfasesAceptadas.append(ent.getValue().toString());
		    	}
		    	objeto.put("todasfasesAceptadas",todasfasesAceptadas);
				
				if (erroresWeb.size()< 1 && fases < 1){
					erroresWeb.add(bundle.getString("mensaje.comisiones.unificados.cierre.fases"));
				}
			 }else{
				 logger.debug("error con la fecha de cierre");
				 erroresWeb.add(bundle.getString("mensaje.comisiones.cierre.KO"));
			 }
			
			objeto.put("errores", erroresWeb);			
			objeto.put("ok", ficherosAceptados);
			
			getWriterJSON(response, objeto);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al cerrar el periodo: " + be.getMessage());
			erroresWeb.add(bundle.getString("mensaje.comisiones.cierre.KO"));
		}
		catch(Exception e){
			logger.error("Se ha producido un error al cerrar el periodo:", e);
		}
		logger.debug("end - doValidarCerrarPeriodo");
		return null;
	}
	
	public ModelAndView doDetalleFasesSinCierre (HttpServletRequest request, HttpServletResponse response,Cierre cierreBean) throws Exception{
		logger.debug("init - doDetalleFasesSinCierre");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;	
		List<Fichero> listFicheros = new ArrayList<Fichero>();
		try {				
			
			listFicheros = cierreComisionesManager.getListaFicherosSinCierre();
			logger.debug("Se obtiene la lista de ficheros con fases sin cerrar");
			
			parametros.put("listFicherosSinCierre", listFicheros);
			
			mv = new ModelAndView("/moduloComisiones/detalleCierreComisiones");
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error general: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new Cierre());
		}
		catch(Exception e){
			logger.error("Se ha producido un error al obtener los detalles de las fases sin cierre:", e);
			mv = doConsulta(request, response, new Cierre());
		}
		logger.debug("end - doDetalleFasesSinCierre");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doGenerarInforme (HttpServletRequest request, HttpServletResponse response, Cierre cierreBean) throws Exception{
		logger.debug("init - doGenerarInforme");
		ModelAndView mv = null;	
		Map<String, Object> parametros = new HashMap<String, Object>();
		Cierre cierre = null;
		Boolean informesClasicos = false;
		Boolean informes2015 = false;
		try {				
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			cierre = cierreComisionesManager.getCierreByFecha(cierreBean.getFechacierre());
			
			if (cierre != null){			
				cierreComisionesManager.borrarInformesCierre(cierre.getId());
				informesClasicos   = cierreComisionesManager.generarInformeExcelClasico(cierre, usuario);
				informes2015 = cierreComisionesManager.generarInformeExcel2015(cierre.getId(), cierre.getFechacierre(), cierre.getPeriodo(), usuario);
				if ((informesClasicos) && (informes2015))	
					parametros.put("mensaje", bundle.getString("mensaje.comisiones.cierre.informes.OK"));
				else{
					parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.KO"));
				}
			} else {
				parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.noexistecierre"));
			}
			mv = doConsulta(request, response, cierreBean);
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al generar los informes de comisiones: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.KO"));
			mv = doConsulta(request, response, new Cierre());
		}
		catch(Exception e){
			logger.error("Se ha producido un error al generar los informes:", e);
			parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.KO"));
			mv = doConsulta(request, response, new Cierre());
		}
		logger.debug("end - doGenerarInforme");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doAbrirInformes (HttpServletRequest request, HttpServletResponse response, Cierre cierreBean) throws Exception{
		logger.debug("init - doAbrirInformes");
		ModelAndView mv = null;	
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<ReportCierre> listInformes = new ArrayList<ReportCierre>();
		Cierre cierre = null;
		try {				
			cierre = cierreComisionesManager.getCierreByFecha(cierreBean.getFechacierre());
			
			if (cierre != null){			
				listInformes = cierreComisionesManager.getListaInformesGenerados(cierre.getId());
				logger.debug("Se obtiene la lista de informes generados");
			}
				
			parametros.put("listInformesGenerados", listInformes);
			
			mv = new ModelAndView("/moduloComisiones/verListadoInformes");
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al generar los informes de comisiones: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.KO"));
			mv = doConsulta(request, response, new Cierre());
		}
		catch(Exception e){
			logger.error("Se ha producido un error al abrir el informe", e);
			mv = doConsulta(request, response, new Cierre());
		}
		
		logger.debug("end - doAbrirInformes");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doAbrirInformebyIdCierre (HttpServletRequest request, HttpServletResponse response,Cierre cierreBean) throws Exception{
		logger.debug("init - doAbrirInformes");
		ModelAndView mv = null;	
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<ReportCierre> listInformes = new ArrayList<ReportCierre>();
		String idcierre = StringUtils.nullToString(request.getParameter("idCierre"));
		try {				
			if (!idcierre.isEmpty()){
				cierreBean.setId(new Long(idcierre));
				listInformes = cierreComisionesManager.getListaInformesGenerados(cierreBean.getId());
				logger.debug("Se obtiene la lista de informes generados");
			}
				
			parametros.put("listInformesGenerados", listInformes);
			
			mv = new ModelAndView("/moduloComisiones/verListadoInformes");
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al generar los informes de comisiones: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.KO"));
			mv = doConsulta(request, response, new Cierre());
		}
		catch(Exception e){
			logger.error("Se ha producido un error al abrir el informe", e);
			mv = doConsulta(request, response, new Cierre());
		}
		
		logger.debug("end - doAbrirInformes");
		return mv.addAllObjects(parametros);
	}
	
	/**
	 * Borrado del cierre
	 * @param request
	 * @param response
	 * @param cierreBean
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doBorrarCierrePorId (HttpServletRequest request, HttpServletResponse response, Cierre cierreBean) throws Exception{
		logger.debug("init - doBorrarCierrePorId");
		ModelAndView mv = null;	
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idCierre = StringUtils.nullToString(request.getParameter("idCierre"));
		try{				
			if (!idCierre.isEmpty()){
				boolean borradoInformesOk = cierreComisionesManager.borrarCierrePorId(new Long(idCierre));
				logger.debug("Se borra el informe de cierre");
				if(borradoInformesOk){
					parametros.put("mensaje", bundle.getString("mensaje.comisiones.cierre.borrado.OK"));
				}else{
					parametros.put("mensaje", bundle.getString("mensaje.comisiones.cierre.borrado.error.borradoinformes"));
				}
			}
			
		}catch (BusinessException be) {
			logger.error("Se ha producido un error al borrar el informes de comisiones: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.borrado.KO"));
		
		}catch(Exception e){
			logger.error("Se ha producido un error al borrar el informe", e);
			parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.borrado.KO"));
		}
		finally{
			mv = doConsulta(request, response, new Cierre());
		}
		
		logger.debug("end - doBorrarCierrePorId");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doVerContenidoArchivo (HttpServletRequest request, HttpServletResponse response,Cierre cierreBean) throws Exception{
		logger.debug("init - doVerContenidoArchivo");		
		Map<String, Object> parametros = new HashMap<String, Object>();		
		ModelAndView mv = null;
		try{
			String idInforme = StringUtils.nullToString(request.getParameter("idInforme"));
		//	ReportCierre informeComisiones = cierreComisionesManager.getInformeById(new Long(idInforme));
			ReportCierre ficheroCierre = cierreComisionesManager.getContenidoInforme(new Long(idInforme));
			
			if (ficheroCierre!=null){
				
				// nombre de la ruta del fichero + nombre fichero
				
				//String pathname = System.getProperty("java.io.tmpdir")+ficheroCierre.getNombrefichero();
				
				// creamos el fichero
				Blob blob = ficheroCierre.getContenido(); 
				response.setHeader("Content-Disposition","attachment; filename=\""+ficheroCierre.getNombrefichero()+"\"");
				response.setHeader("cache-control", "no-cache");
				byte[] fileBytes = blob.getBytes(1,(int)blob.length());
				ServletOutputStream outs = response.getOutputStream();
				outs.write(fileBytes);
				outs.flush();
				outs.close();
	
				logger.debug("end - doVerContenidoArchivo");
				return mv;
			}else{
				parametros.put("alerta", bundle.getString("mensaje.comisiones.cierre.informes.noencontrado"));
				mv = doConsulta(request, response, cierreBean);				
			}					
		}catch(Exception e){
			logger.error("Se ha producido un error durante la lectura del archivo PAC", e);
			mv = doConsulta(request, response, cierreBean);
		}
		logger.debug("end - doVerContenidoArchivo");
		return mv.addAllObjects(parametros);
		
	}
		
	public void setCierreComisionesManager(CierreComisionesManager cierreComisionesManager) {
		this.cierreComisionesManager = cierreComisionesManager;
	}

	public void setCierreComisionesService(
			ICierreComisionesService cierreComisionesService) {
		this.cierreComisionesService = cierreComisionesService;
	}
}