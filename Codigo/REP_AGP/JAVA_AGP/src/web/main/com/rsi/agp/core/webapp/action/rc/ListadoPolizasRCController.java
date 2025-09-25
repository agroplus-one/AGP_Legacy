package com.rsi.agp.core.webapp.action.rc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IDocumentacionAgroseguroService;
import com.rsi.agp.core.jmesa.service.IListadoPolizasRCService;
import com.rsi.agp.core.jmesa.service.impl.rc.InformesGanadoRCService;
import com.rsi.agp.core.managers.impl.InformesManager;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.helper.InformesGanadoRCHelper;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper;
import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.doc.DocAgroseguro;
import com.rsi.agp.dao.tables.rc.VistaPolizasRC;

public class ListadoPolizasRCController extends MultiActionController {

	private static final Log LOGGER = LogFactory.getLog(ListadoPolizasRCController.class);
	private static final String ERROR_TOTAL = "Error no esperado. Por favor, contacte con su administrador.";
	private static final String PLANTILLAS = "plantillas";
	private static final String VISTA_POLIZAS_RC = "vistaPolizasRC";
	
	private IListadoPolizasRCService listadoPolizasRCService;
	private String successView;
	private IDocumentacionAgroseguroService docAgroseguroService;
	private InformesGanadoRCService informesGanadoRCService;
	private InformesGanadoRCHelper informesGanadoRCHelper;
    private TableDataFilter tableDataFilter;
	private TableDataSort tableDataSort;
	private ParametrizacionManager parametrizacionManager;
	private InformesManager informesManager;
	
	public void setTableDataFilter(TableDataFilter tableDataFilter){
		this.tableDataFilter = tableDataFilter;
	}
	
	public TableDataFilter getTableDataFilter(){
		return this.tableDataFilter;
	}
	
	public void  setTableDataSort(TableDataSort tableDataSort){
		this.tableDataSort = tableDataSort;
	}
	
	public TableDataSort getTableDataSort(){
		return this.tableDataSort;
	}
	
	public void setListadoPolizasRCService(
			IListadoPolizasRCService listadoPolizasRCService) {
		this.listadoPolizasRCService = listadoPolizasRCService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
	public void setInformesManager(InformesManager informesManager) {
		this.informesManager = informesManager;
	}

	public void setDocAgroseguroService(IDocumentacionAgroseguroService docAgroseguroService) {
		this.docAgroseguroService = docAgroseguroService;
	}
	
	public void setInformesGanadoRCHelper(InformesGanadoRCHelper informesGanadoRCHelper){
		this.informesGanadoRCHelper = informesGanadoRCHelper;
	}
	
	public void setInformesGanadoRCService(InformesGanadoRCService informesGanadoRCService){
		this.informesGanadoRCService = informesGanadoRCService;
	} 

	public ModelAndView doConsulta(final HttpServletRequest req,
			final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC) {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String origenLlamada = req.getParameter("origenLlamada");
			params.put("origenLlamada", origenLlamada);			
			final Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
		    params.putAll(this.cargarParametrosComunes(usuario));
		    
		    String fechaString = req.getParameter("fecenviorcId");
			if (fechaString != null && !fechaString.equals("")) {
				Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaString);
				vistaPolizasRC.setFecenviorc(fecha);
			}
			if(!StringUtils.equals(origenLlamada, Constants.ORIGEN_LLAMADA_MENU_GENERAL)){
				params.putAll(this.cargarNombreEntidad(vistaPolizasRC.getEntidad()));
				String html = this.listadoPolizasRCService.getTablaPolizasRC(req, res, vistaPolizasRC, usuario.getTipousuario());
				if(html == null){
					return null;
				} else {
					Boolean ajax = Boolean.valueOf(req.getParameter("ajax"));
					if(ajax){
						byte[] bytes = html.getBytes(Constants.DEFAULT_ENCODING);
						res.getOutputStream().write(bytes);
						return null;
					} else {
						req.setAttribute("listaPolizasRC", html);
					}
				}
			} else {
			    params.putAll(this.valoresIniciales(vistaPolizasRC, usuario));
			}
			params.putAll(this.entidadesOficinasGrupo(usuario));
			String mensaje = req.getParameter(Constants.KEY_MENSAJE) == null ? 
					(String) req.getAttribute(Constants.KEY_MENSAJE) : 
						req.getParameter(Constants.KEY_MENSAJE);
			String alerta = req.getParameter(Constants.KEY_ALERTA) == null ? 
					(String) req.getAttribute(Constants.KEY_ALERTA) : 
						req.getParameter(Constants.KEY_ALERTA);
			if (alerta != null) {
				params.put(Constants.KEY_ALERTA, alerta);
			}
			if (mensaje != null) {
				params.put(Constants.KEY_MENSAJE, mensaje);
			}
		} catch (Exception e) {
			LOGGER.error("No se ha podido realizar la consulta", e);
			params.put(Constants.KEY_ALERTA, ERROR_TOTAL);
		}
		return new ModelAndView(successView, VISTA_POLIZAS_RC, vistaPolizasRC).addAllObjects(params);
	}
	
	public ModelAndView doBorrar(final HttpServletRequest req,
			final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC){
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			this.listadoPolizasRCService.borrarPoliza(vistaPolizasRC.getIdpoliza().longValue());
			req.setAttribute(Constants.KEY_MENSAJE, "Póliza de RC borrada correctamente");
			vistaPolizasRC.setIdpoliza(null);
			vistaPolizasRC.setNsolicitud(null);
			return this.doConsulta(req, res, vistaPolizasRC);
		} catch (BusinessException e) {
			params.put(Constants.KEY_ALERTA, ERROR_TOTAL);
			LOGGER.error("No se ha podido borrar la póliza RC", e);
		}
		new ModelAndView().addAllObjects(params);
		return new ModelAndView(successView, VISTA_POLIZAS_RC, vistaPolizasRC).addAllObjects(params);
	}
	
	public ModelAndView doAnular(final HttpServletRequest req,
			final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC){
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			this.listadoPolizasRCService.anular(vistaPolizasRC.getIdpoliza(), vistaPolizasRC.getUsuario());
			req.setAttribute(Constants.KEY_MENSAJE, "Póliza de RC anulada correctamente");
			vistaPolizasRC.setUsuario(null);
			return this.doConsulta(req, res, vistaPolizasRC);
		} catch (BusinessException e) {
			params.put(Constants.KEY_ALERTA, ERROR_TOTAL);
			LOGGER.error("No se ha podido anular la póliza RC", e);
		}
		return new ModelAndView(successView, VISTA_POLIZAS_RC, vistaPolizasRC).addAllObjects(params);
	}
	
	public ModelAndView imprimirCondiciones(final HttpServletRequest req,
			final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC){

		String especieRC = vistaPolizasRC.getCodespecierc();
		BigDecimal plan = vistaPolizasRC.getPlan();
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mav = null;
		String origenLlamada = req.getParameter("origenLlamada");

		try {
			params.put("origenLlamada", origenLlamada);
			DocAgroseguro docAgroseguro = this.docAgroseguroService.getDocumentoParaPolizasRC(especieRC, plan);
			if (docAgroseguro != null) {
				Blob fichero = docAgroseguro.getDocAgroseguroFichero().getFichero();
				if (fichero != null) {
					res.setContentType(docAgroseguro.getDocAgroseguroExtPerm().getMimeType());
					String fileName = new StringBuilder("attachment; filename=\"").append(docAgroseguro.getNombre()).append("\"").toString();
					res.setHeader("Content-Disposition",fileName);
					res.setHeader("cache-control", "no-cache");
					byte[] fileBytes = docAgroseguro.getDocAgroseguroFichero()
							.getFichero().getBytes(1, Integer.parseInt(String.valueOf(fichero.length())));
					ServletOutputStream output = res.getOutputStream();
					output.write(fileBytes);
					output.flush();
					output.close();
				};
			}
		} catch (Exception e) {
			logger.error("No se ha podido descargar la documentación", e);
			params.put("alerta", ERROR_TOTAL);
			mav = new ModelAndView(successView, VISTA_POLIZAS_RC, vistaPolizasRC).addAllObjects(params);
		}
		return mav;
	}
	
	public ModelAndView doPasoDefinitiva(final HttpServletRequest req,
			final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC){
		
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			this.listadoPolizasRCService.pasarDefinitiva(vistaPolizasRC.getIdpoliza(), vistaPolizasRC.getUsuario());
			req.setAttribute(Constants.KEY_MENSAJE, "Póliza de RC pasada a definitiva correctamente");
			vistaPolizasRC.setUsuario(null);
			return this.doConsulta(req, res, vistaPolizasRC);
		} catch (Exception e) {
			params.put(Constants.KEY_ALERTA, ERROR_TOTAL);
			LOGGER.error("No se ha podido pasar a definitiva la póliza RC");
		}
		return new ModelAndView(successView, VISTA_POLIZAS_RC, vistaPolizasRC).addAllObjects(params);
	}
	
	public ModelAndView doImprimir(final HttpServletRequest req, final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC) {
		ModelAndView mav = null;;
		
		try {
			Long idPolizaRC = vistaPolizasRC.getIdpoliza().longValue();
			Map<String, Object> datosInforme = this.informesGanadoRCService.getRellenarInformacion(idPolizaRC);
			ResourceBundle bundle = ResourceBundle.getBundle("agp");
			Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
			
			String codTerminal = "";
			
			if (req.getSession().getAttribute("codTerminal") != null)
				codTerminal = req.getSession().getAttribute("codTerminal").toString();
			
			
			String tipoIdentificador = datosInforme.get("tipoIdentificacion").toString();
			Boolean aseguradoVulnerable = Boolean.FALSE;
			
			if ("NIF".equals(tipoIdentificador)) {
				String key = "";
				if (codTerminal.isEmpty()) {
					key = bundle.getString("aseguradoVulnerable.secret.NoTF");
				}else {
					key = bundle.getString("aseguradoVulnerable.secret.TF");
				}
				
				AseguradoIrisHelper helper = new AseguradoIrisHelper();				
				
				aseguradoVulnerable = helper.isAseguradoVulnerable(datosInforme.get("entMed").toString(), 
				datosInforme.get("asegurado_identificador").toString(), "F", key, codTerminal, usuario.getCodusuario());
				LOGGER.debug("Fin aseguradoVulnerable: " + aseguradoVulnerable);
			}
			
			if (aseguradoVulnerable)
				datosInforme.putAll(this.informesManager.getNotaInformativa(new BigDecimal(datosInforme.get("entMed").toString()), idPolizaRC, ConstantsInf.NOTA_INF_RC_AV));
			String rutaInformes = this.getServletContext().getRealPath(PLANTILLAS);
			
			
			this.informesGanadoRCHelper.generarInforme(res, datosInforme, rutaInformes, aseguradoVulnerable);
		} catch (Exception e) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.KEY_ALERTA, ERROR_TOTAL);
			LOGGER.error(e.getMessage(), e);
			mav = new ModelAndView(successView, VISTA_POLIZAS_RC, vistaPolizasRC).addAllObjects(params);
		}
		return mav;
	}
	
	private Map<String,Object> valoresIniciales(VistaPolizasRC vistaPolizasRC, Usuario usuario){
		Map<String,Object> params = new HashMap<String, Object>();
		int tipoUsuario = usuario.getTipousuario().intValue();
		BigDecimal codEntidad = usuario.getOficina().getEntidad().getCodentidad();
		vistaPolizasRC.setEntidad(codEntidad);
		String codOficina = usuario.getOficina().getId().getCodoficina().toPlainString();
		params.putAll(this.cargarNombreEntidad(codEntidad));
		switch (tipoUsuario) {
			case Constants.COD_PERFIL_2:
			case Constants.COD_PERFIL_3:
				vistaPolizasRC.setOficina(codOficina);
				break;
			case Constants.COD_PERFIL_4:
				vistaPolizasRC.setUsuario(usuario.getCodusuario());
				break;
			default:
				break;
		}	
		return params;
	}
	
	private Map<String, Object> entidadesOficinasGrupo(Usuario usuario){
		Map<String, Object> datos = new HashMap<String, Object>();
		String entidadesGrupo = 
				com.rsi.agp.core.webapp.util.StringUtils.
				toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false);
		String oficinasGrupo = 
				com.rsi.agp.core.webapp.util.StringUtils.
				toValoresSeparadosXComas(usuario.getListaCodOficinasGrupo(), false, false);
		datos.put("grupoEntidades", entidadesGrupo);
		datos.put("grupoOficinas", oficinasGrupo);
		return datos;
	}
	
	private Map<String, Object> cargarParametrosComunes(Usuario usuario) throws BusinessException {
		return new HashMap<String, Object>(3) {
			private static final long serialVersionUID = 5147996543922001645L;
			{
				put("erroresRC", listadoPolizasRCService.getErroresRC());
				put("estadoPoliza", listadoPolizasRCService.getEstadoPoliza());
				put("estadosRC", listadoPolizasRCService.getEstadosRC());
			}
		};
	}
	
	private Map<String, Object> cargarNombreEntidad(BigDecimal codEntidad){
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String nomEntidad = this.listadoPolizasRCService.cargarNombreEntidad(codEntidad);
			params.put("nomEntidad", nomEntidad);
		} catch (BusinessException e) {
			LOGGER.debug(e);
			params.put("nomEntidad", "");
		}
		return params;
	}
	
	public void setParametrizacionManager(
			ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
}
