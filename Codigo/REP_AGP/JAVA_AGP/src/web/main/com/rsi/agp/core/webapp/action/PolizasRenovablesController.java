package com.rsi.agp.core.webapp.action;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.jmesa.limit.Limit;
import org.springframework.web.servlet.ModelAndView;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IPolizasRenovablesService;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.managers.impl.ImportacionPolRenovableManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableEstadoEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableValidacionEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.VistaPolizaRenovable;

import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;
import javax.xml.ws.soap.SOAPFaultException;

public class PolizasRenovablesController extends BaseMultiActionController{
	
	private static final String ERROR = "error";
	private static final String TITULO_ERROR = "TituloError";
	private static final String MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE = "moduloUtilidadesGanado/renovables/errorImpresionPolizaRenovable";
	private static final String SEL_ENVIO_IBAN = "selEnvioIBAN";
	private static final String FECHA_ENVIO_IBAN_FIN = "fechaEnvioIBANFin";
	private static final String FECHA_ENVIO_IBAN_INI = "fechaEnvioIBANIni";
	private static final String FECHA_RENO_FIN = "fechaRenoFin";
	private static final String FECHA_RENO_INI = "fechaRenoIni";
	private static final String FECHA_CARGA_FIN = "fechaCargaFin";
	private static final String FECHA_CARGA_INI = "fechaCargaIni";
	private static final String GRUPO_NEGOCIO = "grupoNegocio";
	private static final String EST_AGROPLUS = "estAgroplus";
	private static final String ENVIO_IBAN = "envioIBAN";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	private static final String USUARIO = "usuario";
	private static final Log logger = LogFactory.getLog(PolizasRenovablesController.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IPolizasRenovablesService polizasRenovablesService;
	private WebServicesManager webServicesManager;
	private String successView;
	/* Pet. 63482 ** MODIF TAM (20/04/2021) */
	private ImportacionPolRenovableManager importacionPolRenovableManager;
	
	String realPath = "";
	
	public ModelAndView doConsulta (HttpServletRequest request, 
			HttpServletResponse response,VistaPolizaRenovable polizaRenovableBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		String perfil = "";
		String html = null;
		ModelAndView mv = new ModelAndView(successView);
		final Usuario usuario = (Usuario) request.getSession().getAttribute(PolizasRenovablesController.USUARIO);
		
		String origenLlamada = request.getParameter(PolizasRenovablesController.ORIGEN_LLAMADA);
		if (null==origenLlamada) {
			origenLlamada = (String) request.getAttribute(PolizasRenovablesController.ORIGEN_LLAMADA);
		}
		
		if (StringUtils.nullToString(request.getParameter("limpiarFiltro")).equals("false")){
			VistaPolizaRenovable filtroPolRenovables = (VistaPolizaRenovable) request.getSession().getAttribute("filtroPolRenovables");
			if (filtroPolRenovables != null) {
				polizaRenovableBean = filtroPolRenovables;
			}
		}
		// Carga el grupo de entidades asociadas al usuario si es de perfil 5
		List<BigDecimal> grupoEntidadesBig = usuario.getListaCodEntidadesGrupo();
		List<Long> grupoEntidades = new ArrayList<Long>();
		for (BigDecimal ent: grupoEntidadesBig) {
			grupoEntidades.add(ent.longValue());
		}
		parametros.put("grupoEntidades",StringUtils.toValoresSeparadosXComas(grupoEntidades, false, false));
					
		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		String nomEntidad = "";
		String razonSocial="";
		if (new Integer(perfil).intValue() == Constants.COD_PERFIL_1) {
			polizaRenovableBean.setCodentidad(usuario.getSubentidadMediadora().getEntidad().getCodentidad().longValue());
			nomEntidad = usuario.getSubentidadMediadora().getEntidad().getNomentidad();
			parametros.put("nomEntidad", nomEntidad);
			if (usuario.isUsuarioExterno()) {
				polizaRenovableBean.setCodentidadmed(usuario.getSubentidadMediadora().getId().getCodentidad());
				polizaRenovableBean.setCodsubentmed(usuario.getSubentidadMediadora().getId().getCodsubentidad());
			}
		}else {
			if(null!= origenLlamada && origenLlamada.equals("primeraBusqueda") ) {
				//El bean no contienen la descripción de las lupas. Cuando la llamada no es de ajax, y las lupas forman parte
				// del filtro, debemos pasarle los datos a la página de laws descripciones de las lupas 
				if(polizaRenovableBean.getCodentidad()!=null) {
					nomEntidad =polizasRenovablesService.getNombreEntidad(polizaRenovableBean.getCodentidad().longValue());
					parametros.put("nomEntidad", nomEntidad);
					
					if(null!=polizaRenovableBean.getNifTomador()&& !polizaRenovableBean.getNifTomador().trim().equals("")) {
						razonSocial=polizasRenovablesService.getNombreTomador(polizaRenovableBean.getCodentidad().longValue(), polizaRenovableBean.getNifTomador());
						parametros.put("razonSocial", razonSocial);
					}
				}
			}			
		}
		// Carga los grupos de Negocio
    	List <GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();
    	gruposNegocio = polizasRenovablesService.getGruposNegocio();
		try{
			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				
				
				
				
				html = polizasRenovablesService.getTablaPolRenovables(request, response, polizaRenovableBean, origenLlamada, grupoEntidades,usuario,gruposNegocio);
				if (html == null) {
					return null; 
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este atributo
						request.setAttribute("polRenovables", html);						
				}
			}
				
			// --------------------------------------------------------
	    	// -- Busqueda de datos necesarias para cargar el filtro --
	        // --------------------------------------------------------        
	    	logger.debug("init - carga de datos para el filtro de busqueda");   	
	    	// Carga los estados Agroplus
	    	List <EstadoRenovacionAgroplus> estadosRenAgroplus = new ArrayList<EstadoRenovacionAgroplus>();
	    	estadosRenAgroplus = polizasRenovablesService.getEstadosRenAgroplus(new BigDecimal[]{});
	    	// Carga los estados Agroseguro
	    	List <EstadoRenovacionAgroseguro> estadosRenAgroseguro = new ArrayList<EstadoRenovacionAgroseguro>();
	    	estadosRenAgroseguro = polizasRenovablesService.getEstadosRenAgroseguro(new BigDecimal[]{});	    	
	    	// Carga los estados EnvioIBAN
	    	List <PolizaRenovableEstadoEnvioIBAN> estadosRenEnvioIBAN = new ArrayList<PolizaRenovableEstadoEnvioIBAN>();
	    	estadosRenEnvioIBAN = polizasRenovablesService.getEstadosRenEnvioIBAN(new BigDecimal[]{});
	    	
	    	if (null != origenLlamada && (origenLlamada.equals("primeraBusqueda") || origenLlamada.equals("cambioMasivo") || origenLlamada.equals(PolizasRenovablesController.ENVIO_IBAN) || origenLlamada.equals("altaPolRen"))) { 
				String estAgroplus   = (String) request.getParameter(PolizasRenovablesController.EST_AGROPLUS);
				String estAgroseguro = (String) request.getParameter("estAgroseguro");
				String estEnvioIBAN = (String) request.getParameter("estEnvioIBAN");
				String grupoNegocio = (String) request.getParameter(PolizasRenovablesController.GRUPO_NEGOCIO);
				if (estAgroplus !=null && !estAgroplus.equals("")) {
					parametros.put(PolizasRenovablesController.EST_AGROPLUS, estAgroplus);
				}//else if (polizaRenovableBean.getEstadoRenovacionAgroplus() != null && polizaRenovableBean.getEstadoRenovacionAgroplus().getCodigo() != null) {
//					parametros.put("estAgroplus", polizaRenovableBean.getEstadoRenovacionAgroplus().getCodigo().toString());
//				}
				if (estAgroseguro !=null && !estAgroseguro.equals("")) {
					parametros.put("estAgroseguro", estAgroseguro);
				}else if (polizaRenovableBean.getEstagroseguro() != null ) {
					parametros.put("estAgroseguro", polizaRenovableBean.getEstagroseguro().toString());
				}
				if (estEnvioIBAN !=null && !estEnvioIBAN.equals("")) {
					parametros.put("estEnvioIBAN", estEnvioIBAN);
				}else if (polizaRenovableBean.getEstadoIban() != null) {
					parametros.put("estEnvioIBAN", polizaRenovableBean.getEstadoIban().toString());
				}
				if (grupoNegocio !=null && !grupoNegocio.equals("")) {
					parametros.put(PolizasRenovablesController.GRUPO_NEGOCIO, grupoNegocio);
				}
				
				String fecCargaIni     = (String) request.getParameter(PolizasRenovablesController.FECHA_CARGA_INI);
				String fecCargaFin     = (String) request.getParameter(PolizasRenovablesController.FECHA_CARGA_FIN);
				String fecRenoIni      = (String) request.getParameter(PolizasRenovablesController.FECHA_RENO_INI);
				String fecRenoFin      = (String) request.getParameter(PolizasRenovablesController.FECHA_RENO_FIN);
				String fecEnvioIBANIni = (String) request.getParameter(PolizasRenovablesController.FECHA_ENVIO_IBAN_INI);
				String fecEnvioIBANFin = (String) request.getParameter(PolizasRenovablesController.FECHA_ENVIO_IBAN_FIN);
				
				
				if (fecCargaIni == null || fecCargaIni.equals("")) {
					fecCargaIni   = (String) request.getAttribute(PolizasRenovablesController.FECHA_CARGA_INI);
				}
				if (fecCargaFin == null || fecCargaFin.equals("")) {
					fecCargaFin   = (String) request.getAttribute(PolizasRenovablesController.FECHA_CARGA_FIN);
				}
				if (fecRenoIni == null || fecRenoIni.equals("")) {
					fecRenoIni    = (String) request.getAttribute(PolizasRenovablesController.FECHA_RENO_INI);
				}
				if (fecRenoFin == null || fecRenoFin.equals("")) {
					fecRenoFin    = (String) request.getAttribute(PolizasRenovablesController.FECHA_RENO_FIN);
				}
				if (fecEnvioIBANIni == null || fecEnvioIBANIni.equals("")) {
					fecEnvioIBANIni    = (String) request.getAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_INI);
				}
				if (fecEnvioIBANFin == null || fecEnvioIBANFin.equals("")) {
					fecEnvioIBANFin    = (String) request.getAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_FIN);
				}
				if (grupoNegocio == null || grupoNegocio.equals("")) {
					grupoNegocio    = (String) request.getAttribute(PolizasRenovablesController.GRUPO_NEGOCIO);
				}
				if (estAgroplus == null || estAgroplus.equals("")) {
					estAgroplus    = (String) request.getAttribute(PolizasRenovablesController.EST_AGROPLUS);
				}
				if (fecCargaIni !=null && !fecCargaIni.equals("")) {
					parametros.put("fecCargaIni", fecCargaIni);
				}
				if (fecCargaFin !=null && !fecCargaFin.equals("")) {
					parametros.put("fecCargaFin", fecCargaFin);
				}
				if (fecRenoIni !=null && !fecRenoIni.equals("")) {
					parametros.put("fecRenoIni", fecRenoIni);
				}
				if (fecRenoFin !=null && !fecRenoFin.equals("")) {
					parametros.put("fecRenoFin", fecRenoFin);
				}
				if (fecEnvioIBANIni !=null && !fecEnvioIBANIni.equals("")) {
					parametros.put("fecEnvioIBANIni", fecEnvioIBANIni);
				}
				if (fecEnvioIBANFin !=null && !fecEnvioIBANFin.equals("")) {
					parametros.put("fecEnvioIBANFin", fecEnvioIBANFin);
				}
				if (estAgroplus !=null && !estAgroplus.equals("")) {
					parametros.put(PolizasRenovablesController.EST_AGROPLUS, estAgroplus);
				}
				if (grupoNegocio !=null && !grupoNegocio.equals("")) {
					parametros.put(PolizasRenovablesController.GRUPO_NEGOCIO, grupoNegocio);
				}
				
				if (origenLlamada.equals(PolizasRenovablesController.ENVIO_IBAN)) {
					String marcadasIBAN  = StringUtils.nullToString(request.getAttribute("marcadasIBAN"));
					String correctasIBAN = StringUtils.nullToString(request.getAttribute("correctasIBAN"));
					String erroneasIBAN  = StringUtils.nullToString(request.getAttribute("erroneasIBAN"));
					String idErroresIBAN = StringUtils.nullToString(request.getAttribute("idErroresIBAN"));
					parametros.put("marcadasIBAN",  marcadasIBAN);
					parametros.put("correctasIBAN", correctasIBAN);
					parametros.put("erroneasIBAN",  erroneasIBAN);
					parametros.put("idErroresIBAN", idErroresIBAN);
					String listaIds = StringUtils.nullToString(request.getAttribute("listaIds"));
					String selEnvioIBAN = StringUtils.nullToString(request.getAttribute(PolizasRenovablesController.SEL_ENVIO_IBAN));
					parametros.put("listaIds", listaIds);
					parametros.put(PolizasRenovablesController.SEL_ENVIO_IBAN, selEnvioIBAN);
				}
			}
	    	
			parametros.put(PolizasRenovablesController.ORIGEN_LLAMADA,        origenLlamada);
			parametros.put("estadosRenAgroplus",   estadosRenAgroplus);
			parametros.put("estadosRenAgroseguro", estadosRenAgroseguro);
			parametros.put("estadosRenEnvioIBAN",  estadosRenEnvioIBAN);
			parametros.put("gruposNegocio"		,  gruposNegocio);
			
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			if (alerta != null) {
				parametros.put("alerta", alerta);
			}
			if (mensaje != null) {
				parametros.put("mensaje", mensaje);
			}
			/*
			if (null == polizaRenovableBean.getEstagroplus()) {
				EstadoRenovacionAgroplus estRenAgrPlus = new EstadoRenovacionAgroplus();
				polizaRenovableBean.setEstadoRenovacionAgroplus(estRenAgrPlus);
			}
			if (null == polizaRenovableBean.getEstadoRenovacionAgroseguro()) {
				EstadoRenovacionAgroseguro estRenAgroseguro = new EstadoRenovacionAgroseguro();
				polizaRenovableBean.setEstadoRenovacionAgroseguro(estRenAgroseguro);
			}
			if (null == polizaRenovableBean.getPolizaRenovableEstadoEnvioIBAN()) {
				PolizaRenovableEstadoEnvioIBAN estEnvioIBAN = new PolizaRenovableEstadoEnvioIBAN();
				polizaRenovableBean.setPolizaRenovableEstadoEnvioIBAN(estEnvioIBAN);
			}
			*/
			parametros.put("externo", usuario.getExterno());
			mv = new ModelAndView(successView, "polizaRenovableBean", polizaRenovableBean);		
			mv.addAllObjects(parametros);			
			
		}catch (Exception e){
			logger.error("Error en doConsulta de PolizasRenovablesController",e);
		}
		return mv;
	}
	

	public ModelAndView doCambioMasivo (HttpServletRequest request, HttpServletResponse response,VistaPolizaRenovable polizaRenovableBean) {
		
		String isPerfil0 = StringUtils.nullToString(request.getParameter("isPerfil0"));	
		logger.debug("perfil 0: "+isPerfil0);
		
		// Listad de ids de pólizas renovables a actualizar
		String listaIdsGastos_cm = StringUtils.nullToString(request.getParameter("idPlz"));
		// Porcentaje de comisión que se asignará a las pólizas renovables seleccionadas
		String comisionMasiva = StringUtils.nullToString(request.getParameter("comisionMasivo"));
		// Usuario que realizar el cambio 
		final Usuario usuario = (Usuario) request.getSession().getAttribute(PolizasRenovablesController.USUARIO);
		Map<String, String> parameters = new HashMap<String, String>();
		
		try{
			parameters = polizasRenovablesService.cambioMasivo(listaIdsGastos_cm, comisionMasiva, usuario.getCodusuario(), isPerfil0); 
		}
		catch (Exception e){
			logger.error("Error inesperado en el Cambio Masivo de pólizas renovables ", e);
		}
		
		// Obtener el filtro almacenado previamente en sesión para mantenerlo al volver a la pantalla de consulta
		VistaPolizaRenovable cm_PolizaRenovable = polizasRenovablesService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("polRenovables_LIMIT"));
		request.setAttribute(PolizasRenovablesController.ORIGEN_LLAMADA, "cambioMasivo");
		String fecCargaIni     = (String) request.getParameter("fecCargaIni_cm");
		String fecCargaFin     = (String) request.getParameter("fecCargaFin_cm");
		String fecRenoIni      = (String) request.getParameter("fecRenoIni_cm");
		String fecRenoFin      = (String) request.getParameter("fecRenoFin_cm");
		String fecEnvioIBANIni = (String) request.getParameter("fecEnvioIBANIni_cm");
		String fecEnvioIBANFin = (String) request.getParameter("fecEnvioIBANFin_cm");
		String estadoRenovacionAgroplus = (String) request.getParameter("estadoRenovacionAgroplus_cm");
		String polRenGrupoNegocio = (String) request.getParameter("polRenGrupoNegocio_cm");	
		
		if (fecCargaIni !=null && !fecCargaIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_CARGA_INI, fecCargaIni);
		if (fecCargaFin !=null && !fecCargaFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_CARGA_FIN, fecCargaFin);
		if (fecRenoIni !=null && !fecRenoIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_RENO_INI, fecRenoIni);
		if (fecRenoFin !=null && !fecRenoFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_RENO_FIN, fecRenoFin);
		if (fecEnvioIBANIni !=null && !fecEnvioIBANIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_INI, fecEnvioIBANIni);
		if (fecEnvioIBANFin !=null && !fecEnvioIBANFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_FIN, fecEnvioIBANFin);
		if (estadoRenovacionAgroplus !=null && !estadoRenovacionAgroplus.equals(""))
			request.setAttribute(PolizasRenovablesController.EST_AGROPLUS, estadoRenovacionAgroplus);
		if (polRenGrupoNegocio !=null && !polRenGrupoNegocio.equals(""))
			request.setAttribute(PolizasRenovablesController.GRUPO_NEGOCIO, polRenGrupoNegocio);
		
		//VistaPolizaRenovable vistaPol = cargaVistaPolRen(cm_PolizaRenovable);

		return doConsulta(request, response, cm_PolizaRenovable).addAllObjects(parameters);
	}	
	 
public ModelAndView doValidarEnvioIBAN (HttpServletRequest request, HttpServletResponse response,PolizaRenovable polizaRenovableBean) {
		
		boolean marcar = false;
		String listaIdsGastos = StringUtils.nullToString(request.getParameter("idPlzEnvioIBAN"));
		String marcarS  = StringUtils.nullToString(request.getParameter(PolizasRenovablesController.SEL_ENVIO_IBAN));
		marcar = marcarS.equals("1")?true:false;

		List<String> lstGasRenov = Arrays.asList(listaIdsGastos.split(","));
		List<String> lstCadenasIdsG = getListasParaIN(lstGasRenov);
		
		
		List<String> lstPlzRenov = polizasRenovablesService.getListaIdsRenovables(lstCadenasIdsG);
		List<String> lstCadenasIdsP = getListasParaIN(lstPlzRenov);
		
		// Usuario que realiza el cambio 
		final Usuario usuario = (Usuario) request.getSession().getAttribute(PolizasRenovablesController.USUARIO);
		Map<String, String> parameters = new HashMap<String, String>();
		
		try{
			parameters = polizasRenovablesService.validarEnvioIBAN(lstCadenasIdsP, marcar, usuario.getCodusuario(),lstPlzRenov); 
		}
		catch (Exception e){
			logger.error("Error inesperado en el envío IBAN de pólizas renovables ", e);
		}
		
		// Obtener el filtro almacenado previamente en sesión para mantenerlo al volver a la pantalla de consulta
		VistaPolizaRenovable cm_PolizaRenovable = polizasRenovablesService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("polRenovables_LIMIT"));
		request.setAttribute(PolizasRenovablesController.ORIGEN_LLAMADA, PolizasRenovablesController.ENVIO_IBAN);
		String fecCargaIni     = (String) request.getParameter("fecCargaIni_e");
		String fecCargaFin     = (String) request.getParameter("fecCargaFin_e");
		String fecRenoIni      = (String) request.getParameter("fecRenoIni_e");
		String fecRenoFin      = (String) request.getParameter("fecRenoFin_e");
		String fecEnvioIBANIni = (String) request.getParameter("fecEnvioIBANIni_e");
		String fecEnvioIBANFin = (String) request.getParameter("fecEnvioIBANFin_e");
		String estadoRenovacionAgroplus = (String) request.getParameter("estadoRenovacionAgroplus_e");
		String polRenGrupoNegocio = (String) request.getParameter("polRenGrupoNegocio_e");
		
		
		if (fecCargaIni !=null && !fecCargaIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_CARGA_INI, fecCargaIni);
		if (fecCargaFin !=null && !fecCargaFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_CARGA_FIN, fecCargaFin);
		if (fecRenoIni !=null && !fecRenoIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_RENO_INI, fecRenoIni);
		if (fecRenoFin !=null && !fecRenoFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_RENO_FIN, fecRenoFin);
		if (fecEnvioIBANIni !=null && !fecEnvioIBANIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_INI, fecEnvioIBANIni);
		if (fecEnvioIBANFin !=null && !fecEnvioIBANFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_FIN, fecEnvioIBANFin);
		if (estadoRenovacionAgroplus !=null && !estadoRenovacionAgroplus.equals(""))
			request.setAttribute(PolizasRenovablesController.EST_AGROPLUS, estadoRenovacionAgroplus);
		if (polRenGrupoNegocio !=null && !polRenGrupoNegocio.equals(""))
			request.setAttribute(PolizasRenovablesController.GRUPO_NEGOCIO, polRenGrupoNegocio);
		
		
		request.setAttribute("listaIds",listaIdsGastos);
		request.setAttribute(PolizasRenovablesController.SEL_ENVIO_IBAN,marcarS);
		
		//VistaPolizaRenovable vistaPol = cargaVistaPolRen(cm_PolizaRenovable);
		
		return doConsulta(request, response, cm_PolizaRenovable).addAllObjects(parameters);
	}	
	
	public List<String> getListasParaIN(List<String> lstPlzRenov){
		List<String> lstCadenasIds = new ArrayList<String>();
		int contador = 0;
		String cadena = "";
		boolean primera = true;
		for (String id : lstPlzRenov) {
			if (contador<Constants.MAX_NUM_ELEM_OPERATOR_IN) {
				if (!primera)
					cadena = cadena + ",";
				else
					primera = false;
				cadena = cadena +id;
				contador++;			
			}else {
				if (cadena.length()>0)
					lstCadenasIds.add(cadena);
				cadena = id;
				contador = 1;
			}
		}
		lstCadenasIds.add(cadena);
		logger.debug("Numero total de elementos: " + lstPlzRenov.size() + ". Listas partidas: " + lstCadenasIds.size());		
		return lstCadenasIds;
	}
	
public ModelAndView doModificarEstadoEnvioIBAN (HttpServletRequest request, HttpServletResponse response,PolizaRenovable polizaRenovableBean) {
		boolean marcar = false;
		String listaIdsGastos = StringUtils.nullToString(request.getParameter("idsResPlzEnvioIBAN"));
		String marcarS  = StringUtils.nullToString(request.getParameter("seleccionEnvioIBAN"));
		marcar = marcarS.equals("true")?true:false;
		
		List<String> lstGasRenov = Arrays.asList(listaIdsGastos.split(","));
		List<String> lstCadenasIdsG = getListasParaIN(lstGasRenov);
		
		List<String> lstPlzRenov = polizasRenovablesService.getListaIdsRenovables(lstCadenasIdsG);
		List<String> lstCadenasIdsP = getListasParaIN(lstPlzRenov);
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute(PolizasRenovablesController.USUARIO);
		Map<String, String> parameters = new HashMap<String, String>();
		
		try{
			parameters = polizasRenovablesService.modificarEstadoEnvioIBAN(lstCadenasIdsP, marcar, usuario.getCodusuario(),lstPlzRenov); 
		}
		catch (Exception e){
			logger.error("Error inesperado en la modificación de los etados Envio IBAN de pólizas renovables ", e);
		}

		// Obtener el filtro almacenado previamente en sesión para mantenerlo al volver a la pantalla de consulta
		VistaPolizaRenovable cm_PolizaRenovable = polizasRenovablesService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("polRenovables_LIMIT"));
		request.setAttribute(PolizasRenovablesController.ORIGEN_LLAMADA, PolizasRenovablesController.ENVIO_IBAN);
		String fecCargaIni     = (String) request.getParameter("fecCargaIni_res");
		String fecCargaFin     = (String) request.getParameter("fecCargaFin_res");
		String fecRenoIni      = (String) request.getParameter("fecRenoIni_res");
		String fecRenoFin      = (String) request.getParameter("fecRenoFin_res");
		String fecEnvioIBANIni = (String) request.getParameter("fecEnvioIBANIni_res");
		String fecEnvioIBANFin = (String) request.getParameter("fecEnvioIBANFin_res");
		String estadoRenovacionAgroplus = (String) request.getParameter("estadoRenovacionAgroplus_res");
		String polRenGrupoNegocio = (String) request.getParameter("polRenGrupoNegocio_res");
		
		
		if (fecCargaIni !=null && !fecCargaIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_CARGA_INI, fecCargaIni);
		if (fecCargaFin !=null && !fecCargaFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_CARGA_FIN, fecCargaFin);
		if (fecRenoIni !=null && !fecRenoIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_RENO_INI, fecRenoIni);
		if (fecRenoFin !=null && !fecRenoFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_RENO_FIN, fecRenoFin);
		if (fecEnvioIBANIni !=null && !fecEnvioIBANIni.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_INI, fecEnvioIBANIni);
		if (fecEnvioIBANFin !=null && !fecEnvioIBANFin.equals(""))
			request.setAttribute(PolizasRenovablesController.FECHA_ENVIO_IBAN_FIN, fecEnvioIBANFin);
		if (estadoRenovacionAgroplus !=null && !estadoRenovacionAgroplus.equals(""))
			request.setAttribute(PolizasRenovablesController.EST_AGROPLUS, estadoRenovacionAgroplus);
		if (polRenGrupoNegocio !=null && !polRenGrupoNegocio.equals(""))
			request.setAttribute(PolizasRenovablesController.GRUPO_NEGOCIO, polRenGrupoNegocio);
		
		//VistaPolizaRenovable vistaPol = cargaVistaPolRen(cm_PolizaRenovable);
		
		return doConsulta(request, response, cm_PolizaRenovable).addAllObjects(parameters);
	}	
	
public ModelAndView doVerErroresValidacionEnvioIBAN (HttpServletRequest request, HttpServletResponse response,PolizaRenovable polizaRenovableBean) {
	ModelAndView mv = null;
	Map<String, Object> parameters = new HashMap<String, Object>();
	String idErroresIBAN = StringUtils.nullToString(request.getParameter("idErroresIBAN"));

	Map<String, List<PolizaRenovableValidacionEnvioIBAN>> mapaListas = polizasRenovablesService.getPolRenValidacionEnvioIBAN(idErroresIBAN);
	List<PolizaRenovableValidacionEnvioIBAN> noAsegurado = mapaListas.get("lstErroresNoAsegurado");
	if (noAsegurado != null && noAsegurado.size()>0)
		parameters.put("mostrarErrNoAsegurado",true);
	List<PolizaRenovableValidacionEnvioIBAN> noCuenta = mapaListas.get("lstErroresNoCuenta");
	if (noCuenta != null && noCuenta.size()>0)
		parameters.put("mostrarErrNoCuenta",true);
	parameters.put("mapaListas",mapaListas);
	//Mostramos la pagina de errores
	mv = new ModelAndView("moduloUtilidadesGanado/renovables/erroresValidacionEnvioIBAN", "datos",mapaListas).addAllObjects(parameters);
	return mv;
}	

public ModelAndView getImpresionProrroga (HttpServletRequest request, HttpServletResponse response,PolizaRenovable polizaRenovableBean) {
	String referenciaWs = StringUtils.nullToString(request.getParameter("referenciaWs"));
	String planWs = StringUtils.nullToString(request.getParameter("planWs"));
	String valorWs = StringUtils.nullToString(request.getParameter("valorWs"));
	logger.debug("llamando al Ws ImpresionProrroga con los parametros poliza: "+referenciaWs+ " plan: "+planWs+" valor: "+valorWs);
	setRealPath(this.getServletContext().getRealPath("/WEB-INF/"));
	Base64Binary pdf = null;
	try{
		pdf = polizasRenovablesService.imprimirProrroga(planWs, referenciaWs,valorWs, realPath);		
		byte[] content = pdf.getValue();				
		response.setContentType("application/pdf");
		response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "filename=Prorroga_" + referenciaWs + "_" + planWs+ ".pdf");
		response.setHeader("Cache-Control", "cache, must-revalidate");
		response.setHeader("Pragma", "public");
		try (ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
			bufferedOutputStream.write(content);
		}
	} catch (AgrException e) {
		logger.error("getImpresionProrroga - Se ha producido un error durante la impresion de la poliza renovable ", e);
		String mensaje = WSUtils.debugAgrException(e);
		return new ModelAndView(PolizasRenovablesController.MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE)
			.addObject(PolizasRenovablesController.ERROR, mensaje).addObject(PolizasRenovablesController.TITULO_ERROR, "Resultado de la llamada al servicio web de impresión de la póliza renovable");
	} catch (SOAPFaultException e) {
		logger.error("getImpresionProrroga - Se ha producido un error durante la impresion de la poliza  renovable", e);
		return new ModelAndView(PolizasRenovablesController.MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE)
			.addObject(PolizasRenovablesController.ERROR, e.getMessage()).addObject(PolizasRenovablesController.TITULO_ERROR, "Resultado de la llamada al servicio web de impresión de la póliza renovable");
	} catch (Exception e) {
		logger.error("getImpresionProrroga - Se ha producido un error durante la impresion de la  poliza renovable", e);
		return new ModelAndView(PolizasRenovablesController.MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE)
			.addObject(PolizasRenovablesController.ERROR, "Se ha producido un error durante la impresion de la poliza renovable").addObject(PolizasRenovablesController.TITULO_ERROR, "Error al llamar al servicio web para la impresión de la póliza renovable");
	}
	return null;
}	

	public ModelAndView doImprimirCopy (HttpServletRequest request, HttpServletResponse response,PolizaRenovable polizaRenovableBean) {
		Base64Binary pdf = null;
		String referenciaPol = StringUtils.nullToString(request.getParameter("referenciaPol"));
		String planPol = StringUtils.nullToString(request.getParameter("planPol"));
		logger.debug("llamando al Ws con los parametros poliza: "+referenciaPol+ " plan: "+planPol+" tipo: P");
		setRealPath(this.getServletContext().getRealPath("/WEB-INF/"));
		try{
			pdf = webServicesManager.consultarPolizaTradActualCopy(planPol, referenciaPol, "P", realPath);			
			byte[] content = pdf.getValue();			
			response.setContentType("application/pdf");
			response.setContentLength(content.length);
			response.setHeader("Content-Disposition", "filename=Copy_" + referenciaPol+ "_" + planPol+ ".pdf");
			response.setHeader("Cache-Control", "cache, must-revalidate");
			response.setHeader("Pragma", "public");
			try (ServletOutputStream out = response.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out)) {
				bufferedOutputStream.write(content);
			}
		} catch (es.agroseguro.serviciosweb.polizapdf.AgrException e) {
			logger.error("getImpresionProrroga - Se ha producido un error durante la impresion de la copy de poliza renovable", e);
			String mensaje = WSUtils.debugAgrException(e);
			return new ModelAndView(PolizasRenovablesController.MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE)
				.addObject(PolizasRenovablesController.ERROR, mensaje).addObject(PolizasRenovablesController.TITULO_ERROR, "Resultado de la llamada al servicio web de impresión de la copy de póliza renovable");
		} catch (SOAPFaultException e) {
			logger.error("getImpresionProrroga - Se ha producido un error durante la impresion de la copy de poliza renovable", e);
			return new ModelAndView(PolizasRenovablesController.MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE)
				.addObject(PolizasRenovablesController.ERROR, e.getMessage()).addObject(PolizasRenovablesController.TITULO_ERROR, "Resultado de la llamada al servicio web de impresión de la copy de póliza renovable");
		
		} catch (Exception e) {
			logger.error("getImpresionProrroga - Se ha producido un error durante la impresion la copy de la poliza renovable", e);
			return new ModelAndView(PolizasRenovablesController.MODULO_UTILIDADES_GANADO_RENOVABLES_ERROR_IMPRESION_POLIZA_RENOVABLE)
				.addObject(PolizasRenovablesController.ERROR, "Se ha producido un error durante la impresion de la copy de la póliza renovable")
				.addObject(PolizasRenovablesController.TITULO_ERROR, "Error al llamar al servicio web para la impresión de la copy de póliza renovable");
		}
		
		
		return null;
	}	
	
	/**
	 * Método para ver el acuse de recibo devuelto por el WS de Agroseguro al enviar los gastos de la póliza renovable
	 * @param request
	 * @param response
	 */
	public void doVerAcuseReciboGastos(HttpServletRequest request, HttpServletResponse response) {
		String acuseRecibo = "";
		try{
			String idPolRen  = StringUtils.nullToString(request.getParameter("idPolRen"));
			acuseRecibo = polizasRenovablesService.getAcuseReciboGastos(Long.parseLong(idPolRen));
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(acuseRecibo);
		}
		catch(Exception e){
			logger.warn("error al recoger el acuse de recibo devuelto por el WS al enviar los gastos de una póliza renovable", e);
    	}
	}
	
    public String getPermitirRecargoTxt(int permitirRecargo) {
    	String res;
		if(permitirRecargo == Constants.PERMITIR_RECARGO_NO) {
			res = Constants.PERMITIR_RECARGO_NO_TXT ;
		}else {
			res =Constants.PERMITIR_RECARGO_SI_TXT;
		}
			return res;		
    }
	
	/**
	 * Comprueba que todas las polizas cuyos ids se pasan por parámetro se pueden cambiar el gasto.
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("all")
	public void doAjaxCheckGastosMasivo (HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("doAjaxCheckGastosMasivo - inicio");
		
		// Se obtiene el listado de ids de poliza a comprobar
		String listIdPolizas = request.getParameter("idPlz");
		String checkIds = listIdPolizas.substring(0, listIdPolizas.length()-1);
		logger.debug("doAjaxCheckGastosMasivo - Lista ids de poliza a validar: " + listIdPolizas);
		
		// Hace la llamada al manager para comprobar si las polizas se pueden modificar
		logger.debug("doAjaxCheckGastosMasivo - Llamada a la validacion");
		boolean estado = polizasRenovablesService.validarPolizasGastosMasivo(listIdPolizas);
		
		// Se escribe en el response el resultado de la validacion
		response.setCharacterEncoding("UTF-8");
		logger.debug("doAjaxCheckGastosMasivo - Las polizas " + ((estado) ? "no " : "si") + "se pueden modificar.");
		try {
			response.getWriter().write(new Boolean(estado).toString());
		} catch (IOException e) {
			logger.debug("doAjaxCheckGastosMasivo - Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);
		}
		
		logger.debug("doAjaxCheckGastosMasivo - fin");
					
	}
	
	/* Pet. 63485 ** MODIF TAM (19.04.2021) ** Inicio */
	public void doAltaPolizaRenovable (HttpServletRequest request, 
				HttpServletResponse response,VistaPolizaRenovable polizaRenovableBean) throws Exception {

		logger.debug("** PolizarRenovablesController - doAltaPolizaRenovable [INIT]");
		
		JSONObject objeto = new JSONObject(); 
		
		HashMap<String, Object> resultadoImporta = new HashMap<String, Object>();
		
		Session session = null;

		final Usuario usuario = (Usuario) request.getSession().getAttribute(PolizasRenovablesController.USUARIO);
		
		request.getParameter("idPlz");
		Long codPlan = Long.parseLong(request.getParameter("plan_renov"));
		Long codLinea = Long.parseLong(request.getParameter("linea_renov"));
		String refPolRenovable = request.getParameter("referencia_renov").toUpperCase();
		
		try{
			
			if (codPlan != null && codLinea != null && !StringUtils.isNullOrEmpty(refPolRenovable)) {
				setRealPath(this.getServletContext().getRealPath("/WEB-INF/"));
				
				/* Lanzamos proceso de importación de pólizas Renovables */
				HashMap<String, Object> map= this.importacionPolRenovableManager.importaPolizaRen(codPlan, codLinea, refPolRenovable, realPath, usuario.getCodusuario(), session);
				
				String mensaje = (String) map.get("mensaje");
				String alerta = (String) map.get("alerta");
				
				if (alerta != null) {
					/* Si se ha producido algún fallo en el alta*/
					objeto.put("dato", "KO");
					objeto.put("alert", alerta);
				}
				if (mensaje != null) {
					/* Si el alta ha ido correctamente*/
					objeto.put("dato", "OK");
					objeto.put("mensaje", mensaje);
				}
				
				response.setCharacterEncoding("UTF-8");			  
				getWriterJSON(response, objeto);  
	 
				logger.debug ("ImportacionPolizasController - doIniciarImportacionPoliza [END]"); 
	
			}else {
				logger.debug ("ImportacionPolizasController - doIniciarImportacionPoliza [END]"); 
				
				objeto.put("dato", "KO");
				objeto.put("alert", "Los datos insertados no son correctos");
	
				response.setCharacterEncoding("UTF-8");			  
				getWriterJSON(response, objeto);  
	 
			}
		
		} catch (JSONException e) {
			logger.error("Excepcion : PolizasRenovablesController - doAltaPolizaRenovable", e);
		} catch (Exception e) {
			logger.debug(
					"PolizasRenovablesController.doAltaPolizaRenovable - Ocurrió un error importar la póliza Renovable");
			logger.error("Ocurrió un error al importar la Poliza.", e);
			
			try {
				String alerta_str = (String) resultadoImporta.get("alert");
				if (alerta_str.equals("")) {
					objeto.put("alert", "Ha ocurrido un error importar la póliza Renovable");
					objeto.put("dato", "KO");
				} else {
					objeto.put("alert", alerta_str);
					objeto.put("dato", "KO");
				}

				getWriterJSON(response, objeto);
			} catch (JSONException e1) {
				logger.error("Excepcion : PolizasRenovablesController - doAltaPolizaRenovable", e1);
			}
		}
	}
	
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
	    List<VistaPolizaRenovable> items;
	    try {
	        // Obtener todos los registros filtrados y ordenados
	        items = polizasRenovablesService.getAllFilteredAndSorted(request);

	        // Si hay registros, preparar los datos para la exportación a Excel
	        if (items.size() != 0) {
	            request.setAttribute("listado", items);
	            request.setAttribute("nombreInforme", "ListadoPolizasRenovables");
	            request.setAttribute("jasperPath", "informeJasper.listadoPolizasRenovables");

	            // Redirigir a la vista de exportación a Excel
	            return new ModelAndView("forward:/informes.html?method=doInformeListado");
	        }
	    } catch (BusinessException e) {
	        // Registrar el error si no se pudieron obtener los registros filtrados y ordenados
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
	    }

	    // Si no hay registros o se produce un error, devolver null
	    return null;
	}
	
	public void setImportacionPolRenovableManager(ImportacionPolRenovableManager importacionPolRenovableManager) {
		this.importacionPolRenovableManager = importacionPolRenovableManager;
	}
	/* Pet. 63485 ** MODIF TAM (19.04.2021) ** Fin */
    	
	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setPolizasRenovablesService(
			IPolizasRenovablesService polizasRenovablesService) {
		this.polizasRenovablesService = polizasRenovablesService;
	}
	
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}


	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
	
}