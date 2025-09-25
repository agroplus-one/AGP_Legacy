package com.rsi.agp.core.webapp.action.utilidades;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IIncidenciasAgroService;
import com.rsi.agp.core.managers.impl.utilidades.AportarDocIncidenciaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Motivos;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;

public class IncidenciasAgroUtilidadesController extends MultiActionController {

	private static final Log LOGGER = LogFactory.getLog(IncidenciasAgroUtilidadesController.class);

	private IIncidenciasAgroService incidenciasAgroService;
	private String successView;
	private AportarDocIncidenciaManager aportarDocIncidenciaManager;

	
	public ModelAndView doConsulta(final HttpServletRequest request,
			final HttpServletResponse response,
			final VistaIncidenciasAgro vIncidenciasAgro) {

		LOGGER.debug("init - IncidenciasAgroUtilidadesController");

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String html = null;
		
		try {
			
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			String fechaEnvioDesde = (String) request.getParameter("fechaEnvioDesdeId");
			
			
			String fechaEnvioHasta = request.getParameter("fechaEnvioHastaId");
			String origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
			String origenLlamadaAttr = StringUtils.nullToString(request.getAttribute("origenLlamadaAttr"));

			// MODIF TAM (20.07.2018) ** Inicio //
			String codAsunto = vIncidenciasAgro.getCodasunto();
			if (codAsunto == null)
				codAsunto = request.getParameter("asunto");
			
			AsuntosInc asunto = this.aportarDocIncidenciaManager.getAsunto(codAsunto);
			parametros.put("asuntoInc", asunto);
			// MODIF TAM (20.07.2018) ** Fin //
			
			/* Pet. 57627 ** MODIF TAM (15.11.2019) ** Inicio */
			if (vIncidenciasAgro.getCodmotivo()!= null) {
				Integer codmotivo = vIncidenciasAgro.getCodmotivo();
			
				if (codmotivo !=0) {
					Motivos motivo = this.aportarDocIncidenciaManager.getMotivo(codmotivo);
					parametros.put("motivos", motivo);
				}
			}
			
			/* Incluimos validación del filtro de búsqueda, dependiendo de la tipología seleccionada */
			Character tipoInc = vIncidenciasAgro.getTipoinc();
			
			if (tipoInc !=null) {
				if (tipoInc =='I'){
					vIncidenciasAgro.setCodmotivo(0);
				}else {
					vIncidenciasAgro.setCodasunto("");
				}
			}	
			/* Pet. 57627 ** MODIF TAM (15.11.2019) ** Inicio */
			 
			if (fechaEnvioDesde != null && !fechaEnvioDesde.equals("")) {
				Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaEnvioDesde);
				vIncidenciasAgro.setFechaEnvioDesde(fecha);
			}
			if (fechaEnvioHasta != null && !fechaEnvioHasta.equals("")) {
				Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaEnvioHasta);
				vIncidenciasAgro.setFechaEnvioHasta(fecha);
			}
			
			cargaParametrosComunes(request, parametros, vIncidenciasAgro);

			if (!"menuGeneral".equals(origenLlamada) && "".equals(origenLlamadaAttr)) {
				logger.debug("Comienza la busqueda de incidencias");

				html = this.incidenciasAgroService.getTablaIncidenciasAgro(
						request, response,
						vIncidenciasAgro, origenLlamada);

				if (html == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este
						// atributo
						request.setAttribute("listaIncidenciasAgro", html);
				}
			} else {
			    parametros.putAll(this.valoresIniciales(vIncidenciasAgro, usuario));
			}

			parametros.putAll(this.entidadesOficinasGrupo(usuario));
			parametros.put("origenLlamada", origenLlamada);

			String mensaje = request.getParameter("mensaje") == null ? (String) request
					.getAttribute("mensaje") : request.getParameter("mensaje");
			String alerta = request.getParameter("alerta") == null ? (String) request
					.getAttribute("alerta") : request.getParameter("alerta");
			if (alerta != null) {
				parametros.put("alerta", alerta);
			}
			if (mensaje != null) {
				parametros.put("mensaje", mensaje);
			}
		} catch (Exception e) {

			logger.error("Error en doConsulta de IncidenciasAgroUtilidadesController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "VistaIncidenciasAgro", vIncidenciasAgro);
		mv.addAllObjects(parametros);

		return mv;
	}

	
	public ModelAndView doBorrar(final HttpServletRequest request,
			final HttpServletResponse response,
			final VistaIncidenciasAgro vIncidenciasAgro) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			Character tipo_inc = request.getParameter("tipoincBorrado").charAt(0);
			
			cargaParametrosComunes(request, parametros, vIncidenciasAgro);
			
			this.incidenciasAgroService.borrarIncidencia(vIncidenciasAgro.getIdincidencia());
			if (tipo_inc =='I'){
				request.setAttribute("mensaje", "Incidencia borrada correctamente.");
			}else {
				request.setAttribute("mensaje", "Anulación/Rescisión borrada correctamente.");
			}
			
			

			return doConsulta(request, response, vIncidenciasAgro);

		} catch (Exception e) {

			logger.error("Error en doBorrar de LineasRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "incidenciasAgro", vIncidenciasAgro);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	
	public IIncidenciasAgroService getIncidenciasAgroService() {
		return incidenciasAgroService;
	}

	public void setIncidenciasAgroService(IIncidenciasAgroService incidenciasAgroService) {
		this.incidenciasAgroService = incidenciasAgroService;
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
	private Map<String, Object> valoresIniciales(
			VistaIncidenciasAgro vIncidenciasAgro, Usuario usuario) {
		Map<String, Object> params = new HashMap<String, Object>();
		int tipoUsuario = usuario.getTipousuario().intValue();
		BigDecimal codEntidad = usuario.getOficina().getEntidad()
				.getCodentidad();
		switch (tipoUsuario) {
		case Constants.COD_PERFIL_1:
			vIncidenciasAgro.setCodentidad(codEntidad);
			params.putAll(this.cargarNombreEntidad(codEntidad));
			if (Constants.USUARIO_EXTERNO.equals(usuario.getExterno())) {
				vIncidenciasAgro.setSubentmediadora(usuario
						.getSubentidadMediadora().getId().getCodsubentidad());
				vIncidenciasAgro.setEntmediadora(usuario
						.getSubentidadMediadora().getId().getCodentidad());
			}
			break;
		case Constants.COD_PERFIL_2:
			vIncidenciasAgro.setCodentidad(codEntidad);
			params.putAll(this.cargarNombreEntidad(codEntidad));
			vIncidenciasAgro.setOficina(usuario.getOficina().getId()
					.getCodoficina().toString());
			params.putAll(this.cargarNombreOficina(codEntidad, usuario
					.getOficina().getId().getCodoficina()));
			break;
		case Constants.COD_PERFIL_3:
			vIncidenciasAgro.setCodentidad(codEntidad);
			params.putAll(this.cargarNombreEntidad(codEntidad));
			vIncidenciasAgro.setOficina(usuario.getOficina().getId()
					.getCodoficina().toString());
			params.putAll(this.cargarNombreOficina(codEntidad, usuario
					.getOficina().getId().getCodoficina()));
			if (Constants.USUARIO_EXTERNO.equals(usuario.getExterno())) {
				vIncidenciasAgro.setSubentmediadora(usuario
						.getSubentidadMediadora().getId().getCodsubentidad());
				vIncidenciasAgro.setEntmediadora(usuario
						.getSubentidadMediadora().getId().getCodentidad());
				vIncidenciasAgro.setDelegacion(usuario.getDelegacion());
			}
			break;
		case Constants.COD_PERFIL_4:
			vIncidenciasAgro.setCodusuario(usuario.getCodusuario());
			vIncidenciasAgro.setCodentidad(codEntidad);
			vIncidenciasAgro.setEntmediadora(usuario.getSubentidadMediadora()
					.getId().getCodentidad());
			vIncidenciasAgro.setSubentmediadora(usuario
					.getSubentidadMediadora().getId().getCodsubentidad());
			params.putAll(this.cargarNombreEntidad(codEntidad));
			vIncidenciasAgro.setDelegacion(usuario.getDelegacion());
			break;
		case Constants.COD_PERFIL_5:
			vIncidenciasAgro.setCodentidad(codEntidad);
			params.putAll(this.cargarNombreEntidad(codEntidad));
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
	
	private Map<String, Object> cargarNombreEntidad(BigDecimal codEntidad){
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String nomEntidad = this.incidenciasAgroService.cargarNombreEntidad(codEntidad);
			params.put("nomEntidad", nomEntidad);
		} catch (BusinessException e) {
			LOGGER.debug(e);
			params.put("nomEntidad", "");
		}
		return params;
	}
	
	private Map<String, Object> cargarNombreOficina(BigDecimal codEntidad, BigDecimal codOficina){
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String nomOficina = this.incidenciasAgroService.cargarNombreOficina(codOficina, codEntidad);
			params.put("nomOficina", nomOficina);
		} catch (BusinessException e) {
			LOGGER.debug(e);
			params.put("nomOficina", "");
		}
		return params;
	}
	
	private Map<String, Object> cargarNombreLinea(BigDecimal codLinea){
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String nomLinea = this.incidenciasAgroService.cargarNombreLinea(codLinea);
			params.put("nomLinea", nomLinea);
		} catch (BusinessException e) {
			LOGGER.debug(e);
			params.put("nomLinea", "");
		}
		return params;
	}


	private void cargaParametrosComunes(final HttpServletRequest request,
			final Map<String, Object> parametros,
			final VistaIncidenciasAgro vIncidenciasAgro) throws BusinessException {

		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil().substring(4);
		
		Collection<EstadosInc> estadosInc = this.incidenciasAgroService
				.getEstadosInc();
		parametros.put("listaEstadosInc", estadosInc);
		parametros.put("listaAsuntos", this.aportarDocIncidenciaManager.obtenerAsuntos());
		
		/* Pet. 57627 ** MODIF TAM (11.11.2019) ** Inicio */
		parametros.put("listaMotivos", this.aportarDocIncidenciaManager.obtenerMotivos());

		parametros.put("perfil", perfil);
		parametros.put("externo",usuario.getExterno());
		parametros.put("grupoOficinas",StringUtils.toValoresSeparadosXComas(usuario.getListaCodOficinasGrupo(),false,false));
		parametros.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));

		if(vIncidenciasAgro.getCodestado() != null) {
			parametros.put("codestadoSel", vIncidenciasAgro.getCodestado());
		}
		
		if(vIncidenciasAgro.getTiporef() != null) {
			parametros.put("tiporefSel", vIncidenciasAgro.getTiporef());
		}
		if(vIncidenciasAgro.getTipoinc() != null) {
			parametros.put("tipoincSel", vIncidenciasAgro.getTipoinc());
		}
		
		
		if(vIncidenciasAgro.getFechaEnvioDesde() != null) {
			parametros.put("fechaEnvioDesdeId", vIncidenciasAgro.getFechaEnvioDesde());
		}
		
		if(vIncidenciasAgro.getFechaEnvioHasta() != null) {
			parametros.put("fechaEnvioHastaId", vIncidenciasAgro.getFechaEnvioHasta());
		}
		
		if(vIncidenciasAgro.getCodentidad() != null) {
			parametros.putAll(this.cargarNombreEntidad(vIncidenciasAgro.getCodentidad()));

			if(vIncidenciasAgro.getOficina() != null && !vIncidenciasAgro.getOficina().isEmpty()) {
				parametros.putAll(this.cargarNombreOficina(vIncidenciasAgro
						.getCodentidad(), new BigDecimal(
						vIncidenciasAgro.getOficina())));
			}
		}
		
		if(vIncidenciasAgro.getTipoinc() != null) {
			parametros.put("tipoinc", vIncidenciasAgro.getTipoinc());
		}
			
		
		if(vIncidenciasAgro.getCodlinea() != null) {
			parametros.putAll(this.cargarNombreLinea(vIncidenciasAgro.getCodlinea()));
		}
		if (perfil != null && String.valueOf(Constants.COD_PERFIL_4).equals(perfil)) {
			vIncidenciasAgro.setDelegacion(usuario.getDelegacion());
		}
	}
	
	public void setAportarDocIncidenciaManager(AportarDocIncidenciaManager aportarDocIncidenciaManager) {
		this.aportarDocIncidenciaManager = aportarDocIncidenciaManager;
	}
	public void getAportarDocIncidenciaManager(AportarDocIncidenciaManager aportarDocIncidenciaManager) {
		this.aportarDocIncidenciaManager = aportarDocIncidenciaManager;
	}
	
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
	    List<VistaIncidenciasAgro> items;
	    // Obtener todos los registros filtrados y ordenados
		try {
			items = incidenciasAgroService.getAllFilteredAndSorted();
			
			if (items.size() != 0) {
			    request.setAttribute("listado", items);
			    request.setAttribute("nombreInforme", "ListadoIncidenciasAgro");
			    request.setAttribute("jasperPath", "informeJasper.listadoIncidenciasAgro");

			    // Redirigir a la vista de exportación a Excel
			    return new ModelAndView("forward:/informes.html?method=doInformeListado");
			}
			
		} catch (DAOException e) {
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
		}
		
	    // Si no hay registros o se produce un error, devolver null
	    return null;
	}


}
