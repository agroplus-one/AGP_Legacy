package com.rsi.agp.core.webapp.action.sbp;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IConsultaPolizaSbpService;
import com.rsi.agp.core.managers.IConsultaSbpManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.PolizaCopyManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public class ConsultaSbpController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(ConsultaSbpController.class);
	private IConsultaPolizaSbpService consultaPolizaSbpService;
	private SeleccionPolizaManager seleccionPolizaManager;
	private PolizaManager polizaManager;
	private PolizaCopyManager polizaCopyManager;
	private IConsultaSbpManager consultaSbpManager;
	private ISimulacionSbpManager simulacionSbpManager;
	private String successView;    
	
   

	/**
     * Realiza la consulta de polizas para sobreprecio
     * @param request
     * @param response
     * @param polizaBean Objeto que encapsula el filtro de la busqueda
     * @return Devuelve la redireccion a la jsp con los parametros necesarios
     */
    public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) throws Exception{
    	
    	logger.debug("init - ConsultaSbpController");
    	
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	// Map para guardar los parametros que se pasaran a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	// Variable que almacena el codigo de la tabla de polizas
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	Poliza polizaBusqueda = (Poliza) polizaBean;
    	
    	/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
        String entMediadora = request.getParameter("entMediadora");
        String subentMediadora = request.getParameter("subEntmediadora");
        String deleg = request.getParameter("deleg");
        parameters.put("externo",usuario.getExterno());
        
        /* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
    	
    	// ---------------------------------------------------------------------------
    	// -- Busqueda de las cultivos por lineaseguroid que cumplen el sobreprecio --
        // ---------------------------------------------------------------------------
		BigDecimal codPlan = simulacionSbpManager.getPlanSbp();
		Map<Long, List<BigDecimal>> cultivosPorLinea = consultaSbpManager.getCultivosPorLineaseguroid(codPlan);
		
		// --------------------------------------------------------
    	// -- Busqueda de las lineas que cumplen el sobreprecio --
        // --------------------------------------------------------
		//List<Sobreprecio> lineas = consultaSbpManager.getLineasSobrePrecio();
		List<Long> lstLineasSbp = new ArrayList<Long>();
		String listaLineasSbp = "";
		Set <Long> lstLin= cultivosPorLinea.keySet();
		for (Long lineaSeguroId: lstLin){
			if (!lstLineasSbp.contains(lineaSeguroId)){
				lstLineasSbp.add(lineaSeguroId);
			}
		}
		if(!lstLineasSbp.isEmpty()){
			for(Long li : lstLineasSbp){
				listaLineasSbp += li.toString() + ",";
			}
			listaLineasSbp = listaLineasSbp.substring(0,listaLineasSbp.length()-1);
		}

        // DAA 12/08/2013
		// Se busca en la tabla entidades el grupo al que pertenece, a partir de la entidad del usuario
		List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
		//SFM P21050
		List<BigDecimal> grupoOficinas=usuario.getListaCodOficinasGrupo();
		
    	// ---------------------------------------------------------------------------------
    	// -- Busqueda de polizas para sobreprecio y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
		if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
			logger.debug("Comienza la busqueda de polizas de sobreprecio");
			html = consultaPolizaSbpService.getTablaPolizasParaSbp(request, response, polizaBusqueda, lstLineasSbp,
					origenLlamada, grupoEntidades, cultivosPorLinea, grupoOficinas);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else {
					// Pasa a la jsp el codigo de la tabla a traves de este atributo
					request.setAttribute("consultaPolizasSbp", html);
				}
			}
		}  	    	    
        // --------------------------------------------------------
    	// -- Busqueda de datos necesarias para cargar el filtro --
        // --------------------------------------------------------        
    	logger.debug("init - carga de datos para el filtro de busqueda");   	
    	// Carga los estados
    	List <EstadoPoliza> estadosPoliza = new ArrayList<EstadoPoliza>();
    	final List <EstadoPoliza> estadosPolizaTemp = seleccionPolizaManager.getEstadosPoliza(new BigDecimal[]{});
    	
    	// elimino el estado 'Anulada' de los estados de la poliza
    	for (EstadoPoliza est: estadosPolizaTemp){
    		if (est.getIdestado().compareTo(Constants.ESTADO_POLIZA_ANULADA) != 0){
    			estadosPoliza.add(est);
    		}
    	}
    	
    	// Carga de lineaseguroid
		//polizaBusqueda.getLinea().setLineaseguroid(seleccionPolizaManager.getLineaseguroId(polizaBusqueda.getColectivo().getLinea().getCodplan(), polizaBusqueda.getColectivo().getLinea().getCodlinea()));
    	
    	parameters.put("perfil", perfil);
		
		switch (new Integer(perfil).intValue()){
		case 4: 
			parameters.put("filtroUsuario" , usuario.getCodusuario());
			break;
		default:
			break;
		}
    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
		
		// ----------------------------------
    	// -- Carga del mapa de parametros --
        // ----------------------------------
		
		PolizaSbp polizaSbp = new PolizaSbp();
		polizaSbp.setIncSbpComp('N');
		parameters.put("polizaSbp", polizaSbp);
		AnexoModificacion anexMod = new AnexoModificacion();
		Poliza poliza = new Poliza();
		anexMod.setPoliza(poliza);
		parameters.put("anexMod", anexMod);
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
		parameters.put("perfil", perfil);
		logger.debug("Establecemos perfil");
		parameters.put("listaLineasSbp", listaLineasSbp);
		parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas(grupoEntidades, false, false));
		parameters.put("grupoOficinas", StringUtils.toValoresSeparadosXComas(grupoOficinas, false, false));
		logger.debug("Establecemos grupo de Entidades");
		parameters.put("estados", estadosPoliza);
		logger.debug("Establecemos estados de la Poliza");
    	
		
		
		// -- FILTROS POR DEFECTO --
		// filtro por entidad del usuario
		if (StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
	    	switch (new Integer(perfil).intValue()){
				case 1: polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
					break;
				case 3:	polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
						polizaBusqueda.setOficina(usuario.getOficina().getId().getCodoficina().toString());
						polizaBusqueda.setNombreOfi(usuario.getOficina().getNomoficina());
						break;				
				case 2: 
						polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
						polizaBusqueda.setOficina(usuario.getOficina().getId().getCodoficina().toString());
						polizaBusqueda.setNombreOfi(usuario.getOficina().getNomoficina());
						break;
				case 4: polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
				        polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
						break;
				case 0: case 5:
						polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
						break;
				default:
						break;
			}
	    	// Anadimos el filtro por ultimo plan
	    	if (codPlan!= null){
	    		polizaBusqueda.getLinea().setCodplan(codPlan);
	    	}
		}else{
			Poliza pol = (Poliza)request.getSession().getAttribute("polBusqueda");
	    	if (pol != null){
	    		parameters.put("filtroPlan" , pol.getLinea().getCodplan());
	    	}
		}
		
    	/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
		String entmediadora = "";
		String subentmediadora = "";
		String delegacion = ""; 
    	//campos por defecto en caso de usuario externo
        if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
        	if (usuario.getTipousuario().compareTo(Constants.PERFIL_1)==0) {
        		entmediadora = usuario.getSubentidadMediadora().getId().getCodentidad().toString();
        		subentmediadora = usuario.getSubentidadMediadora().getId().getCodsubentidad().toString();
        		parameters.put("entMediadora", entmediadora);
        		parameters.put("subEntmediadora", subentmediadora);
        	}else if (usuario.getTipousuario().compareTo(Constants.PERFIL_3)==0) {
        		entmediadora = usuario.getSubentidadMediadora().getId().getCodentidad().toString();
        		subentmediadora = usuario.getSubentidadMediadora().getId().getCodsubentidad().toString();
				delegacion = usuario.getDelegacion().toString();
				parameters.put("entMediadora", entmediadora);
				parameters.put("subEntmediadora", subentmediadora);
				parameters.put("deleg", delegacion);
        	}
		}
        
        if (entMediadora != null &&  !entMediadora.equals("")) {
        	parameters.put("entMediadora", entMediadora);
        }
        if (subentMediadora != null &&  !subentMediadora.equals("")) {
        	parameters.put("subEntmediadora", subentMediadora);
        }

        if (deleg != null &&  !deleg.equals("")) {
        	parameters.put("subEntmediadora", deleg);
        }

	    /* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
		
		
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
       	mv = new ModelAndView(successView, "polizaBean", polizaBusqueda);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - ConsultaSbpController");
    	
    	return mv;
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
    
    /**
     * Metodo que imprime el ultimo Copy por referencia de la poliza. Si no se dispone de Copy todavia, se mostarra
     * la sitaucion origen de Agroseguro
     * @param request
     * @param response
     * @param polizaBean
     * @return
     * @throws Exception
     */
    public ModelAndView doImprimirCopyOSituacionOrigen(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) throws Exception{
    	logger.debug("Init - doImprimirCopyOSituacionOrigen");
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	String realPath = this.getServletContext().getRealPath("/WEB-INF/");
    	String idPoliza= (request.getParameter("idPoliza"));
    	Poliza poliza = polizaManager.getPoliza(Long.parseLong(idPoliza));
    	try{
    	
	    	if (poliza.getReferencia() != null){
	    		Long idCopy = polizaCopyManager.descargarPolizaCopyWS("C", poliza.getLinea().getCodplan(), poliza.getReferencia(), realPath);
	    		if (idCopy !=null){
	    			// Hay copy con principal y complementaria
	    			parameters.put("tipoRefPoliza", "C");
					parameters.put("plan2", poliza.getLinea().getCodplan());
					parameters.put("poliza", poliza.getReferencia());
					return new ModelAndView("redirect:/recibosPoliza.html").addObject("idPoliza", idPoliza).addObject("method", "doVerPDFPolizaCopy").addAllObjects(parameters);
	    		}else{
	    			idCopy = polizaCopyManager.descargarPolizaCopyWS("P", poliza.getLinea().getCodplan(), poliza.getReferencia(), realPath);
	    			if (idCopy !=null){
	    				// Hay copy con principal
	    				parameters.put("tipoRefPoliza", "P");
	    				parameters.put("plan2", poliza.getLinea().getCodplan());
	    				parameters.put("poliza", poliza.getReferencia());
	    				logger.debug("end - doImprimirCopyOSituacionOrigen");
	    				return new ModelAndView("redirect:/recibosPoliza.html").addObject("idPoliza", idPoliza).addObject("method", "doVerPDFPolizaCopy").addAllObjects(parameters);
	    			}else{
	    				// mostrar situacion origen de Agroseguro
	    				logger.debug("end - doImprimirCopyOSituacionOrigen");
	    				return new ModelAndView("redirect:/recibosPoliza.html").addObject("idPoliza", idPoliza).addObject("method", "doVerPDFPolizaOrigen");
	    			}
	    		}
	    	}else{// sin referencia, mostrar borrador de la poliza
	    		logger.debug("end - doImprimirCopyOSituacionOrigen");
	    		return new ModelAndView("redirect:/recibosPoliza.html").addObject("idPoliza", idPoliza).addObject("method", "doVerPDFPolizaOrigen");
	    	}
    	}catch(Exception e){
			throw new BusinessException("Error al descargar la copy de la poliza: "+ poliza.getReferencia(), e); 
		}
    }        
	        
    
    // Set de managers y propiedades
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

    public void setConsultaPolizaSbpService(IConsultaPolizaSbpService consultaPolizaSbpService) {
		this.consultaPolizaSbpService = consultaPolizaSbpService;
	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setPolizaCopyManager(PolizaCopyManager polizaCopyManager) {
		this.polizaCopyManager = polizaCopyManager;
	}

	public void setConsultaSbpManager(IConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}
	
	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

}
