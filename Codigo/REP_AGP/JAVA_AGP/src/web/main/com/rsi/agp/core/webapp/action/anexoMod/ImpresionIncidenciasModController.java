package com.rsi.agp.core.webapp.action.anexoMod;

import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.anexoMod.impresion.ImpresionIncidenciasModBean;
import com.rsi.agp.core.managers.impl.anexoMod.impresion.ImpresionIncidenciasModManager;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.action.utilidades.IncidenciasAgroHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;

public class ImpresionIncidenciasModController extends MultiActionController{
	
	private ImpresionIncidenciasModManager impresionIncidenciasModManager;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	@SuppressWarnings("unused")
	private String aportarDocIncidenciaVista;
	
	private static final Log LOGGER = LogFactory.getLog(ImpresionIncidenciasModController.class);
	
	public ModelAndView doImprimirIncidencias(HttpServletRequest request, 
				HttpServletResponse response, AnexoModificacion anexoModificacion) {
		
		ModelAndView mv = null;
		Map<String,Object> parameters = new HashMap<String,Object> ();
		Map<String,Object> parameters2 = new HashMap<String,Object> ();
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try {
			String fechaEnvio = "";
			String fechaEnvioAuxP = StringUtils.nullToString(request.getParameter("fechaEnvio"));
			
			if (fechaEnvioAuxP == null || fechaEnvioAuxP.equals("")){
				String fechaEnvioAuxA = request.getAttribute("fechaEnvio").toString();
				fechaEnvio = fechaEnvioAuxA;
			}else{
				fechaEnvio = fechaEnvioAuxP;
			}
			
			parameters = rellenaDatosPoliza (anexoModificacion,
											 StringUtils.nullToString(request.getParameter("nombreCompleto")),
											 StringUtils.nullToString(fechaEnvio) );
			// Pet. 50775 ** MODIF TAM (09.05.2018) ** Fin **//
			
			/* Pet. 57627 * MODIF TAM (19.09.2019) ** Inicio */
			parameters.put("perfil", usuario.getTipousuario());
			/*Pet. 57627 * MODIF TAM (19.09.2019) ** Fin */
			
			if (anexoModificacion.getPoliza().getReferencia() != null && 
					anexoModificacion.getPoliza().getLinea().getCodplan() != null) {
				
					parameters2  = impresionIncidenciasModManager.
						solicitarRelacionIncidencias(anexoModificacion.getPoliza().getReferencia(),
								anexoModificacion.getPoliza().getLinea().getCodplan(),
								realPath, usuario != null ? usuario.getCodusuario() : "");
					
					if (parameters2.containsKey("listaIncidencias")) {        // OK
						parameters.put("listaIncidencias", parameters2.get("listaIncidencias"));
					
					}else if (parameters2.containsKey("errorAgrException")) { // AGREXCEPTION
						parameters.put("alerta", parameters2.get("errorAgrException"));
					
					}else if (parameters2.containsKey("errorGenerico")) {     // ERROR GENERICO
						parameters.put("alerta", bundle.getString("mensaje.swImpresion.llamadaWs.KO"));
					}
			}else {
				parameters.put("alerta", bundle.getString("mensaje.swImpresion.generico.KO"));
			}
			
		}catch(Exception e) {
			LOGGER.debug("Ha ocurrido un error generico - doImprimirIncidencias",e);
			parameters.put("alerta", bundle.getString("mensaje.swImpresion.generico.KO"));
		}
		
		parameters.put("anexoModificacion", anexoModificacion);
		
		mv = new ModelAndView("/moduloUtilidades/modificacionesPoliza/impresionIncidencias",
				"impresionIncidenciasModBean",new ImpresionIncidenciasModBean()).addAllObjects(parameters);
		return mv;
	}
	
	public ModelAndView doImprimirPdf(HttpServletRequest request, HttpServletResponse response,
			ImpresionIncidenciasModBean impresionIncidenciasModBean) {
		
		ModelAndView mv = null;
		Map<String,Object> parameters = new HashMap<String,Object> ();
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		try {
			String idCupon = request.getParameter("idCuponImpresion");
			String anio = request.getParameter("anio");
			String numero = request.getParameter("numero");
			
			parameters = impresionIncidenciasModManager.imprimirPdfIncidencia (realPath,idCupon,anio,numero);
			
			if (parameters.containsKey("docPdf")) {        // OK - Mostramos PDF Incidencias
				
				byte[] pdf = (byte[]) parameters.get("docPdf");
				if (pdf != null) {
			           
		            response.setContentType("application/pdf");
		            response.setHeader("Content-Disposition", "filename=Incidencia_" + idCupon+ ".pdf");
					try (BufferedOutputStream fos1 = new BufferedOutputStream(response.getOutputStream())) {
						fos1.write(pdf);
						fos1.flush();
					}
		            return null;
		        }
			
			}else if (parameters.containsKey("errorAgrException")) { // AGREXCEPTION - Mostramos jsp con error
				parameters.put("alerta", parameters.get("errorAgrException"));
			
			}else if (parameters.containsKey("errorGenerico")) {     // ERROR GENERICO - Mostramos Jsp error generico
				parameters.put("alerta", bundle.getString("mensaje.swImpresion.llamadaWs.KO"));
			}
			mv = new ModelAndView("/moduloUtilidades/modificacionesPoliza/impresionErroresPDF").addAllObjects(parameters);
			
			
		}catch(Exception e) {
			LOGGER.debug("Ha ocurrido un error. doImprimirPdf",e);
			parameters.put("alerta", bundle.getString("mensaje.swImpresion.generico.KO"));
		}
		return mv;
		
	}

	private Map<String, Object> rellenaDatosPoliza(AnexoModificacion am,String nombreCompleto, String fechaEnvio) throws Exception {
		
		Map<String,Object> parameters = new HashMap<String,Object> ();
		
		parameters.put("linea", am.getPoliza().getLinea().getCodlinea());
		parameters.put("nomLinea", am.getPoliza().getLinea().getNomlinea());
		parameters.put("nombreAsegurado", nombreCompleto);
		parameters.put("plan", am.getPoliza().getLinea().getCodplan());
		parameters.put("referencia", am.getPoliza().getReferencia());
		parameters.put("modulo", am.getPoliza().getCodmodulo());
		parameters.put("fechaEnvio", DateUtil.string2Date(fechaEnvio, "yyyy-MM-dd"));
		parameters.put("idPoliza", am.getPoliza().getIdpoliza());
		return parameters;
	}
	
	public ModelAndView doAportarDocumentacion(HttpServletRequest req, HttpServletResponse res){
		ModelAndView mv = null;
		try {
			
			LOGGER.debug("ImpresionIncidenciasModController - dentro de doAportarDocumentacion");
			AsuntosInc asunto = IncidenciasAgroHelper.obtenerAsuntoVista(req);
			DocsAfectadosInc docAfectados = IncidenciasAgroHelper.obtenerDocAfectadosVista(req);
			LOGGER.debug("ImpresionIncidenciasModController - despues de obtenerDocAfectadosVista");
 			Incidencias incidencia = IncidenciasAgroHelper.obtenerIncidenciaVista(req);
 			LOGGER.debug("ImpresionIncidenciasModController - despues de obtenerIncidenciasVista");
 			Map<String, Object> parametros = this.impresionIncidenciasModManager.guardarIncidencia(incidencia, asunto, docAfectados);
 			LOGGER.debug("ImpresionIncidenciasModController - despues de guardarIncidencia");
			parametros = IncidenciasAgroHelper.agregarParametrosDeVuelta(parametros, req);
			LOGGER.debug("Redirigiendo a la pantalla de aportación de documentacion");
			
			Long incidenciaId = (Long)parametros.get("incidenciaId");
			
			ModelMap modelo = new ModelMap().addAttribute("method", "doCargar")
					.addAttribute("incidenciaId", StringUtils.nullToString(incidenciaId))
					.addAttribute("origenEnvio", "agroseguro")
					.addAttribute("origen","impresionIncidencias")
					.addAttribute("fechaEnvio", parametros.get("fechaEnvio"))
					.addAttribute("fechaEnvVolver", parametros.get("fechaEnvio"))
					.addAttribute("referenciaVolver", req.getParameter("referencia"))
					.addAttribute("lineaVolver", req.getParameter("linea"))
					.addAttribute("nomLineaVolver", req.getParameter("nomblineaDocVuelta"))
					.addAttribute("idPolizaVolver", req.getParameter("idPolDocVuelta"))
					.addAttribute("nombreAseVolver", req.getParameter("nombreDocVuelta"))
					.addAttribute("moduloVolver", req.getParameter("moduloDocVuelta"));
			
			mv = new ModelAndView("redirect:/aportarDocIncidencia.run", modelo);
			
		} catch(BusinessException e) {
			LOGGER.error("Avisamos al usuario del error, BussinessException: ",e);
			AnexoModificacion anexoModificacion = this.anexoEnCasoDeError(req);
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("alerta", bundle.getString("mensaje.swImpresion.generico.KO"));
			mv = this.doImprimirIncidencias(req, res, anexoModificacion).addAllObjects(parametros);
		} catch (Exception exception) {
			LOGGER.error("Traza de error en doAportarDocumentacion ... Exception: ",exception);
			AnexoModificacion anexoModificacion = this.anexoEnCasoDeError(req);
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("alerta", "Error, póngase en contacto con el administrador");
			mv = this.doImprimirIncidencias(req, res, anexoModificacion).addAllObjects(parametros);
		}
		return mv;
	}
	
	public ModelAndView doVolverIncidencias(HttpServletRequest req, HttpServletResponse res) {
		ModelAndView mv = null;
		AnexoModificacion anexoModificacion = this.anexoVolver(req);
		Map<String, Object> parametros = new HashMap<String, Object>();
		req.setAttribute("fechaEnvio", req.getParameter("fechaEnvVolver"));
		//parametros.put("alerta", bundle.getString("mensaje.swImpresion.generico.KO"));
		mv = this.doImprimirIncidencias(req, res, anexoModificacion).addAllObjects(parametros);
		return mv;
	}
	
	private AnexoModificacion anexoVolver(HttpServletRequest req){
		String referencia = req.getParameter("referenciaVolver");
		BigDecimal codLinea = new BigDecimal(req.getParameter("lineaVolver"));
		BigDecimal codPlan = new BigDecimal(req.getParameter("codPlanVolver"));
		long Idpoliza = new Long (req.getParameter("idPolizaVolver"));
		
		AnexoModificacion anexoModificacion = new AnexoModificacion();
		anexoModificacion.getPoliza().setReferencia(referencia);
		anexoModificacion.getPoliza().getLinea().setCodlinea(codLinea);
		anexoModificacion.getPoliza().getLinea().setNomlinea(req.getParameter("nomLineaVolver"));
		anexoModificacion.getPoliza().setCodmodulo(req.getParameter("moduloVolver"));
		anexoModificacion.getPoliza().setIdpoliza(Idpoliza);
		
		anexoModificacion.getPoliza().getLinea().setCodplan(codPlan);
		return anexoModificacion;
	}
	
	private AnexoModificacion anexoEnCasoDeError(HttpServletRequest req){
		String referencia = req.getParameter("referencia");
		BigDecimal codLinea = new BigDecimal(req.getParameter("linea"));
		AnexoModificacion anexoModificacion = new AnexoModificacion();
		anexoModificacion.getPoliza().setReferencia(referencia);
		anexoModificacion.getPoliza().getLinea().setCodlinea(codLinea);
		return anexoModificacion;
	}

	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automaticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacias
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }
	
	public void setImpresionIncidenciasModManager(
			ImpresionIncidenciasModManager impresionIncidenciasModManager) {
		this.impresionIncidenciasModManager = impresionIncidenciasModManager;
	}

	public void setAportarDocIncidenciaVista(String aportarDocIncidenciaVista){
		this.aportarDocIncidenciaVista = aportarDocIncidenciaVista;
	}
}