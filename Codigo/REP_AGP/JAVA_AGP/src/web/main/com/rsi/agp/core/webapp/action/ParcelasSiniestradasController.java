package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.rsi.agp.core.managers.impl.ParcelasSiniestradasManager;
import com.rsi.agp.core.managers.impl.SiniestrosManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.BigDecimalEditor;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestradoDV;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

public class ParcelasSiniestradasController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(ParcelasSiniestradasController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private ParcelasSiniestradasManager parcelasSiniestradasManager;
	private SiniestrosManager siniestrosManager;
	
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(BigDecimal.class, null, new BigDecimalEditor());
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CapAsegSiniestradoDV capitalAsegSiniestradoDV) {
		
		String idPoliza = request.getParameter("idPoliza");
		String idSiniestro = request.getParameter("idSiniestro");	
		
		List<CapAsegSiniestradoDV>  capAsegSiniestrados = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Poliza poliza = null;
		
		try{
			if(idPoliza != null){
				capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().setIdpoliza(Long.parseLong(idPoliza));
				capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().setId(Long.parseLong(idSiniestro));
			}else{
				idPoliza = capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().getIdpoliza().toString();
				idSiniestro = capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().getId().toString();
			}
			poliza = siniestrosManager.getPoliza(new Long(idPoliza));
			capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().setPoliza(poliza);
			
			capAsegSiniestrados = parcelasSiniestradasManager.buscarCapAsegSiniestradoDV(capitalAsegSiniestradoDV);		
		
			parametros = cargaParametros(capAsegSiniestrados,request,idSiniestro, poliza.getCodmodulo());
			
		}catch(BusinessException be){
			logger.error("Se ha producido un error durante la consulta de parcelas siniestradas", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
		}catch(Exception be){
			logger.error("Se ha producido un error indefinido durante la consulta de parcelas siniestradas", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
		}
		return new ModelAndView("/moduloUtilidades/siniestros/parcelasSiniestro", "capitalAsegSiniestradoDV", capitalAsegSiniestradoDV).addAllObjects(parametros);
	}
	
	/**
	 * carga las parcelas del WS o si este falla, de bbdd y las muestra en la jsp
	 * 24/04/2014 U029769
	 * @param request
	 * @param response
	 * @param capitalAsegSiniestradoDV
	 * @return
	 */
	public ModelAndView doCargaParcelas(HttpServletRequest request, HttpServletResponse response, CapAsegSiniestradoDV capitalAsegSiniestradoDV) {
		
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<CapAsegSiniestradoDV>  capAsegSiniestrados = null;
		String idPoliza = request.getParameter("idPoliza");
		String idSiniestro = request.getParameter("idSiniestro");	
		Poliza poliza = null;
		String altaWs = "";
		try{
			
			altaWs = StringUtils.nullToString(request.getParameter("idPoliza"));
			parametros.put("altaWs", altaWs);
			if(idPoliza != null){
				capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().setIdpoliza(Long.parseLong(idPoliza));
				capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().setId(Long.parseLong(idSiniestro));
			}else{
				idPoliza = capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().getIdpoliza().toString();
				idSiniestro = capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().getId().toString();
			}
			
			poliza = siniestrosManager.getPoliza(new Long(idPoliza));
			capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().setPoliza(poliza);
		
		
			params = parcelasSiniestradasManager.cargarParcelas(capitalAsegSiniestradoDV,
				poliza.getReferencia(),poliza.getLinea(),realPath);	
		
			capAsegSiniestrados = (List<CapAsegSiniestradoDV>) params.get("capitalesAsegSiniestrados");
			
			parametros = cargaParametros(capAsegSiniestrados,request,idSiniestro, poliza.getCodmodulo());
			
			
			//------------------------------------------------------
			if (params.get("mensaje")!= null) {
				parametros.put("mensaje", params.get("mensaje"));
			}else if (params.get("alerta")!= null) {
				parametros.put("alerta", params.get("alerta"));
			}
			
		}catch(BusinessException be){
			logger.error("Se ha producido un error al cargar las parcelas siniestradas:doCargaParcelas", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
		}catch(Exception be){
			logger.error("Se ha producido un error al cargar las parcelas siniestradas:doCargaParcelas", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
		}
		return new ModelAndView("/moduloUtilidades/siniestros/parcelasSiniestro", "capitalAsegSiniestradoDV", capitalAsegSiniestradoDV).addAllObjects(parametros);
	}
	
	private Map<String, Object> cargaParametros(
			List<CapAsegSiniestradoDV> capAsegSiniestrados,
			HttpServletRequest request, String idSiniestro, String codModulo) throws Exception {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		Siniestro siniestro = null;
		boolean modoLectura = "true".equals(request.getParameter("modoLectura")) ? true : false;
		boolean fromUtilidades = "true".equals(request.getParameter("fromUtilidades")) ? true : false;
		//Descripciones de las lupas
		String desc_provincia = StringUtils.nullToString(request.getParameter("desc_provincia"));
		String desc_comarca = StringUtils.nullToString(request.getParameter("desc_comarca"));
		String desc_termino = StringUtils.nullToString(request.getParameter("desc_termino"));
		String desc_cultivo = StringUtils.nullToString(request.getParameter("desc_cultivo"));
		String desc_variedad = StringUtils.nullToString(request.getParameter("desc_variedad"));
		String desc_capital = StringUtils.nullToString(request.getParameter("desc_capital"));
		// -----------------------------------------------------------------------------------
		String listCodModulos="";
		try{
			siniestro = siniestrosManager.buscarSiniestro(new Long(idSiniestro));
			if(siniestro.getEstadoSiniestro().getIdestado().intValue()!= Constants.SINIESTRO_ESTADO_PROVISIONAL.intValue() &&
			   siniestro.getEstadoSiniestro().getIdestado().intValue()!= Constants.SINIESTRO_ESTADO_ENVIADO_ERROR.intValue() &&
			   siniestro.getEstadoSiniestro().getIdestado().intValue()!= Constants.SINIESTRO_ESTADO_DEFINITIVO.intValue()){
				parametros.put("modoLectura", true);
			}
			//DAA 15/06/2012 Recojo los ids de todos los capitales siniestrados.
			String listaIdsCap = parcelasSiniestradasManager.getIdsCap(capAsegSiniestrados);
			
			parametros.put("listaIdsCap", listaIdsCap);
			parametros.put("idSiniestro", siniestro.getId());
			parametros.put("capAsegSiniestrados", capAsegSiniestrados);
			parametros.put("modoLectura", modoLectura);
			parametros.put("fromUtilidades", fromUtilidades);
			
			parametros.put("desc_provincia",desc_provincia);
			parametros.put("desc_comarca", desc_comarca);
			parametros.put("desc_termino", desc_termino);
			parametros.put("desc_cultivo",desc_cultivo);
			parametros.put("desc_variedad", desc_variedad);
			parametros.put("desc_capital", desc_capital);
			
			//para la lupa de tipo de capital			
			if(null!=codModulo){
				listCodModulos=codModulo;				
			}
			parametros.put("listCodModulos",listCodModulos);
			//---------------------------------------------------
		}catch(BusinessException be){
			logger.error("Se ha producido un error al cargar los parametros", be);
			throw be;
		}catch(Exception be){
			logger.error("Se ha producido un error al cargar los parametros", be);
			throw be;
		}
		return parametros;
	}
	
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, CapAsegSiniestradoDV capitalAsegSiniestradoDV) throws Exception{
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {
			
			//Compruebo que todas las parcelas dadas de alta en el siniestro tienen fecha de recoleccion
			if (this.parcelasSiniestradasManager.validarFechasRecoleccion(request.getParameter("idSin"))){
			
				// si viene del boton enviar redirijo a grabacion definitiva
				String pasarADefinitiva = request.getParameter("pasarADefinitiva");
				String altaWs = request.getParameter("altaWs");
				String idPoliza = "";
				String idSiniestro = "";
				if ("true".equals(pasarADefinitiva)){
					idPoliza = request.getParameter("idPol");
					parametros.put("idPoliza", idPoliza);
					parametros.put("altaWs", altaWs);
					idSiniestro = (request.getParameter("idSin"));
					parametros.put("idSiniestro", idSiniestro);
					
					return new ModelAndView("redirect:/siniestros.html")
					.addObject("method", "doPasarDefinitiva")
					.addObject("idSiniestro", "idSiniestro")
					.addObject("idSin", idSiniestro)
					.addObject("idPol", capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().getIdpoliza())
					.addObject("fromUtilidades", request.getParameter("fromUtilidades"))
					.addAllObjects(parametros);
	
				}else{
					parametros.put("mensaje", bundle.getString("mensaje.parcelaSiniestro.OK"));
					// Se indica a la jsp que la llamada se ha hecho desde el listado de utilidades
					if (!StringUtils.nullToString(request.getParameter("fromUtilidades")).equals("true")) {
						parametros.put("volver", true);
						return new ModelAndView("redirect:/utilidadesSiniestros.run").addAllObjects(parametros);
					}
					else{
						return new ModelAndView("redirect:/siniestros.html")
								.addObject("idPoliza", capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().getIdpoliza())
								.addAllObjects(parametros);
					}
				}
			}
			else{
				//Redirijo al listado con el mensaje de que faltan fechas de recoleccion por rellenar
				parametros.put("alerta", bundle.getString("mensaje.parcelaSiniestro.KO.TodasConFechaRecoleccion"));
				return doConsulta(request, response, capitalAsegSiniestradoDV).addAllObjects(parametros);
			}
		} 
		catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante el alta de parcelas siniestradas", be);
			parametros.put("alerta", bundle.getString("mensaje.parcelaSiniestro.KO"));
			return doConsulta(request, response, capitalAsegSiniestradoDV).addAllObjects(parametros);
		}		
	}
	
	/**
	 * Método para guardar los cambios realizados en el listado de parcelas siniestradas y volver al listado para seguir con las modificaciones
	 * @param request
	 * @param response
	 * @param capitalAsegSiniestradoDV
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doAplicarCambios(HttpServletRequest request, HttpServletResponse response, CapAsegSiniestradoDV capitalAsegSiniestradoDV){
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
			String idsSeleccionados = request.getParameter("idsRowsChecked");
			String frutosSeleccionados = request.getParameter("valorFrutosCM");
			String fechaRecolSelect = request.getParameter("inputFechaRec");
			
			try {
				Siniestro siniestro = this.siniestrosManager.buscarSiniestro(
						capitalAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().getId());
				
				if (this.comprobarFechaRecoleccion(siniestro.getFechaocurrencia(), fechaRecolSelect)){
					//Inicio - modifico los valores de los capitales seleccionados con el valor de frutos y fecha.
					this.parcelasSiniestradasManager.generateAndSaveListaCapitalesSiniestrados(
							idsSeleccionados, frutosSeleccionados, fechaRecolSelect);
					//Fin - modificacion.
				}
				else{
					//La fecha de recolección no es valida
					parametros.put("alerta", bundle.getString("mensaje.parcelaSiniestro.KO.fechaRecoleccion"));
				}
			} catch (BusinessException e) {
				parametros.put("alerta", bundle.getString("mensaje.parcelaSiniestro.KO"));
			} catch (ParseException e) {
				parametros.put("alerta", bundle.getString("mensaje.parcelaSiniestro.KO"));
			}
			
			//Volvemos al listado de parcelas siniestradas
			return doConsulta(request, response, capitalAsegSiniestradoDV).addAllObjects(parametros);
	}
	
	// Comprueba si las fechas de recoleccion son posteriores a la fecha de ocurrencia del siniestro
	private boolean comprobarFechaRecoleccion(Date fechaOcurrencia, String fechaRecoleccion) throws ParseException{
		boolean fechaPosterior = true;
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		if (!StringUtils.nullToString(fechaRecoleccion).equals("") && df.parse(fechaRecoleccion).compareTo(fechaOcurrencia) <= 0){
			fechaPosterior = false;
		}
		return fechaPosterior;
	}
	
	public void setParcelasSiniestradasManager(ParcelasSiniestradasManager parcelasSiniestradasManager) {
		this.parcelasSiniestradasManager = parcelasSiniestradasManager;
	}

	public void setSiniestrosManager(SiniestrosManager siniestrosManager) {
		this.siniestrosManager = siniestrosManager;
	}

	
	

}
