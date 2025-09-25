package com.rsi.agp.core.webapp.action.sbp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IListadoPolizaSbpService;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.managers.IConsultaSbpManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.action.BaseSimpleController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.CanalFirma;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.sbp.ErrorSbp;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public class ListadoPolizaSbpController extends BaseSimpleController {

	private IListadoPolizaSbpService listadoPolizaSbpService;
	private IConsultaSbpManager consultaSbpManager;
	private ISimulacionSbpManager simulacionSbpManager;
	private IDocumentacionGedManager documentacionGedManager;

	private String successView;
	private String id;
	private TableDataFilter filtroJmesa;
	private TableDataSort sortJmesa;

	private static final String COD_ENTIDAD = "codEntidad";
	private static final String NOM_ENTIDAD = "nomEntidad";
	private static final String POLIZA_OFICINA = "polizaPpal.oficina";
	private static final String POLIZA_TOMADOR_ENTIDAD = "polizaPpal.colectivo.tomador.id.codentidad";
	private static final String USUARIO_PROVISIONAL = "usuarioProvisional";
	private static final String POLIZA_LINEA_LINEA = "polizaPpal.linea.codlinea";
	private static final String POLIZA_LINEA_PLAN = "polizaPpal.linea.codplan";
	private static final String REFERENCIA = "referencia";
	private static final String POLIZA_MODULO = "polizaPpal.codmodulo";
	private static final String N_SOLICITUD = "nSolicitud";
	private static final String FECHA_ENVIO_SBP = "fechaEnvioSbp";
	private static final String REF_PLZ_OMEGA = "refPlzOmega";

	/* Pet. 79014 ** MODIF TAM (16.03.2022) ** Inicio */
	private static final String CODENT_MEDIADORA = "polizaPpal.colectivo.subentidadMediadora.id.codentidad";
	private static final String CODSUBENT_MEDIADORA = "polizaPpal.colectivo.subentidadMediadora.id.codsubentidad";
	
	/**
	 * P0073325 - RQ.10, RQ.11 y RQ.12
	 */
	private static final String CANAL_FIRMA = "canalFirma";
	private static final String DOC_FIRMADA = "docFirmada";
	private static final String USUARIO_FIRMA = "usuarioFirma";
	private static final String FECHA_FIRMA = "fechaFirma";


	/**
	 * Maneja las peticiones que recibe del listado de JMesa
	 * 
	 * @param request
	 *            Objeto HttpServletRequest
	 * @param response
	 *            Objeto HttpServletResponse
	 * @return Objeto ModelAndView con la redireccion
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		// Comprueba si el parámetro "exportToExcel" es igual a "true"
		// Si es así, llama a la función doExportToExcel() y devuelve su resultado
		// Esto evita la necesidad de renderizar la vista si estamos exportando a Excel
		if ("true".equals(request.getParameter("exportToExcel"))) {
		    return doExportToExcel(request);
		}

		
		ModelAndView mv = new ModelAndView(successView);
		String html = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		String origenLlamada = request.getParameter("origenLlamada");
		if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
			html = render(request, response, origenLlamada);
			if (html == null) {
				return null; // export
			} else {
				String ajax = request.getParameter("ajax");
				if (ajax != null && ajax.equals("true")) {// llamada desde ajax
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					request.setAttribute("listadoPolizasSbp", html);
			}
		}
		String perfil = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");

		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
		String entMediadora = request.getParameter("entmediadora");
		String subentMediadora = request.getParameter("subentmediadora");
		String deleg = request.getParameter("delegacion");
		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */

		if (alerta != null) {
			parametros.put("alerta", alerta);
		}
		if (mensaje != null) {
			parametros.put("mensaje", mensaje);
		}
		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);

		// Pet. 63473 ** MODIF TAM (20/12/2021)
		parametros.put("externo", usuario.getExterno());

		// ---------------------------------------------------------------------------
		// -- Busqueda de las cultivos por lineaseguroid que cumplen el sobreprecio --
		// ---------------------------------------------------------------------------
		Map<Long, List<BigDecimal>> cultivosPorLinea = new HashMap<Long, List<BigDecimal>>();
		BigDecimal maxPlan = simulacionSbpManager.getPlanSbp();
		cultivosPorLinea = consultaSbpManager.getCultivosPorLineaseguroid(maxPlan);

		// --------------------------------------------------------
		// -- Busqueda de las lineas que cumplen el sobreprecio --
		// --------------------------------------------------------
		List<Long> lstLineasSbp = new ArrayList<Long>();
		String listaLineasSbp = "";
		Set<Long> lstLinSbp = cultivosPorLinea.keySet();
		for (Long lineaSeguroId : lstLinSbp) {
			if (!lstLineasSbp.contains(lineaSeguroId)) {
				lstLineasSbp.add(lineaSeguroId);
			}
		}
		if (!lstLineasSbp.isEmpty()) {
			for (Long li : lstLineasSbp) {
				listaLineasSbp += li.toString() + ",";
			}
			listaLineasSbp = listaLineasSbp.substring(0, listaLineasSbp.length() - 1);
		}

		List<EstadoPoliza> estadosPolizaCpl = consultaSbpManager.getEstadosPoliza(new BigDecimal[] {});
		List<EstadoPoliza> estadosPolizaPpal = consultaSbpManager.getEstadosPolizaPpal(estadosPolizaCpl);
		List<EstadoPlzSbp> estadosPolizaSbp = consultaSbpManager
				.getEstadosPolizaSbp(new BigDecimal[] { ConstantsSbp.ESTADO_SIMULACION });
		List<ErrorSbp> detalleErroresSbp = consultaSbpManager.getDetalleErroresSbp(new BigDecimal[] {});
		/**
		 * P0073325 - RQ.10, RQ.11 y RQ.12
		 */
		List<CanalFirma> canalesFirma = documentacionGedManager.getCanalesFirma();
		String idPolizaSbp = StringUtils.nullToString(request.getParameter("idPolizaSbp"));

		if (idPolizaSbp.equals("")) {
			idPolizaSbp = StringUtils.nullToString(request.getParameter("idPolSbp"));
			idPolizaSbp = StringUtils.nullToString(request.getAttribute("idPolSbp"));
		}

		PolizaSbp polSbp = new PolizaSbp();
		if (!idPolizaSbp.equals("")) {
			polSbp = simulacionSbpManager.getPolizaSbp(Long.valueOf(idPolizaSbp));
			parametros.put("nomLinea", polSbp.getPolizaPpal().getLinea().getNomlinea());
		}
		String idPolizaSeleccion = request.getParameter("idPolizaSeleccion");
		parametros.put("idPolizaSeleccion", idPolizaSeleccion);
		parametros.put("origenLlamada", origenLlamada);

		String nomEntidad = "";
		String codEntidad = "";
		String codOficina = "";
		String nomOficina = "";
		String entmediadora = "";
		String subentmediadora = "";
		String delegacion = "";

		// al entrar desde menugeneral buscamos el nomEntidad
		if (StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {

			switch (new Integer(perfil).intValue()) {
			case 0:
			case 1:
				nomEntidad = usuario.getOficina().getEntidad().getNomentidad();
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				parametros.put(COD_ENTIDAD, codEntidad);
				parametros.put(NOM_ENTIDAD, nomEntidad);
				break;
			case 2:
				nomEntidad = usuario.getOficina().getEntidad().getNomentidad();
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				parametros.put(COD_ENTIDAD, codEntidad);
				parametros.put(NOM_ENTIDAD, nomEntidad);
				codOficina = usuario.getOficina().getId().getCodoficina().toString();
				nomOficina = usuario.getOficina().getNomoficina();
				parametros.put("codOficina", codOficina);

				parametros.put("nomOficina", nomOficina);
				break;
			case 3:
				nomEntidad = usuario.getOficina().getEntidad().getNomentidad();
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				parametros.put(COD_ENTIDAD, codEntidad);
				parametros.put(NOM_ENTIDAD, nomEntidad);
				codOficina = usuario.getOficina().getId().getCodoficina().toString();
				nomOficina = usuario.getOficina().getNomoficina();
				parametros.put("codOficina", codOficina);
				parametros.put("nomOficina", nomOficina);
				break;
			case 4:
				nomEntidad = usuario.getOficina().getEntidad().getNomentidad();
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				parametros.put(COD_ENTIDAD, codEntidad);
				parametros.put(NOM_ENTIDAD, nomEntidad);
				parametros.put("filtroUsuario", usuario.getCodusuario());
				break;
			case 5:
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				parametros.put(COD_ENTIDAD, codEntidad);
				break;
			}

		}

		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
		// campos por defecto en caso de usuario externo
		if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
			if (usuario.getTipousuario().compareTo(Constants.PERFIL_1) == 0) {
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				nomEntidad = usuario.getSubentidadMediadora().getEntidad().getNomentidad();
				entmediadora = usuario.getSubentidadMediadora().getId().getCodentidad().toString();
				subentmediadora = usuario.getSubentidadMediadora().getId().getCodsubentidad().toString();
				parametros.put("codEntidad", codEntidad);
				parametros.put("nomEntidad", nomEntidad);
				parametros.put("entMediadora", entmediadora);
				parametros.put("subEntmediadora", subentmediadora);
				parametros.put("delegacion", delegacion);
			} else if (usuario.getTipousuario().compareTo(Constants.PERFIL_3) == 0) {
				codEntidad = usuario.getOficina().getEntidad().getCodentidad().toString();
				nomEntidad = usuario.getSubentidadMediadora().getEntidad().getNomentidad();
				entmediadora = usuario.getSubentidadMediadora().getId().getCodentidad().toString();
				subentmediadora = usuario.getSubentidadMediadora().getId().getCodsubentidad().toString();
				codOficina = usuario.getOficina().getId().getCodoficina().toString();
				nomOficina = usuario.getOficina().getNomoficina();
				delegacion = usuario.getDelegacion().toString();
				parametros.put("codEntidad", codEntidad);
				parametros.put("nomEntidad", nomEntidad);
				parametros.put("entMediadora", entmediadora);
				parametros.put("subEntmediadora", subentmediadora);
				parametros.put("codOficina", codOficina);
				parametros.put("nomOficina", nomOficina);
				parametros.put("deleg", delegacion);

			}
		}
		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */

		BigDecimal codPlan = simulacionSbpManager.getPlanSbp();
		if (codPlan != null) {
			parametros.put("filtroPlan", codPlan);
		}
		switch (new Integer(perfil).intValue()) {
		case 4:
			parametros.put("filtroUsuario", usuario.getCodusuario());
			break;
		default:
			break;
		}

		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
		if (entMediadora != null && !entMediadora.equals("")) {
			parametros.put("entMediadora", entMediadora);
		}
		if (subentMediadora != null && !subentMediadora.equals("")) {
			parametros.put("subEntmediadora", subentMediadora);
		}

		if (deleg != null && !deleg.equals("")) {
			parametros.put("subEntmediadora", deleg);
		}
		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */

		parametros.put("estadosPpal", estadosPolizaPpal);
		parametros.put("estadosCpl", estadosPolizaCpl);
		parametros.put("estadosSbp", estadosPolizaSbp);
		parametros.put("listaLineasSbp", listaLineasSbp);
		parametros.put("grupoEntidades",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
		parametros.put("grupoOficinas",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodOficinasGrupo(), false, false));
		parametros.put("detalleErroresSbp", detalleErroresSbp);
		/**
		 * P0073325 - RQ.10, RQ.11 y RQ.12
		 */
		parametros.put("canalesFirma", canalesFirma);
					
		return mv.addAllObjects(parametros);
	}

	@SuppressWarnings("deprecation")
	protected String render(HttpServletRequest request, HttpServletResponse response, String origenLlamada) {
		String filtrarDetalle = (String) request.getParameter("filtrarDetalle");
		TableFacade tableFacade = new TableFacade(id, request);

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		// DAA 12/08/2013
		List<BigDecimal> grupoEntidades = null;
		if (Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
			grupoEntidades = usuario.getListaCodEntidadesGrupo();
		}
		List<BigDecimal> grupoOficinas = null;
		if (Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil())) {
			grupoOficinas = usuario.getListaCodOficinasGrupo();
		}
		// Defino los campos a mostrar en la tabla
		if (("true").equals(request.getParameter("excel"))) {
			tableFacade.setColumnProperties("id", POLIZA_TOMADOR_ENTIDAD, CODENT_MEDIADORA, CODSUBENT_MEDIADORA,
					POLIZA_OFICINA, USUARIO_PROVISIONAL, POLIZA_LINEA_PLAN, POLIZA_LINEA_LINEA,
					"polizaPpal.colectivo.idcolectivo", REFERENCIA, "polizaPpal.asegurado.nifcif", "polizaPpal.clase",
					POLIZA_MODULO, "polizaPpal.estadoPoliza.idestado", "polizaCpl.estadoPoliza.idestado", "incSbpComp",
					"tipoEnvio.descripcion", FECHA_ENVIO_SBP, "importe", "estadoPlzSbp.idestado", "errorSbps",
					REF_PLZ_OMEGA, N_SOLICITUD, 
					/**
					 * P0073325 - RQ.10, RQ.11 y RQ.12
					 */CANAL_FIRMA, DOC_FIRMADA, USUARIO_FIRMA, FECHA_FIRMA,
					"fechaDefinitiva", "prodTotal", "sumAsegurada");
		} else {
			tableFacade.setColumnProperties("id", POLIZA_TOMADOR_ENTIDAD, CODENT_MEDIADORA, CODSUBENT_MEDIADORA,
					POLIZA_OFICINA, USUARIO_PROVISIONAL, POLIZA_LINEA_PLAN, POLIZA_LINEA_LINEA,
					"polizaPpal.colectivo.idcolectivo", REFERENCIA, "polizaPpal.asegurado.nifcif", "polizaPpal.clase",
					POLIZA_MODULO, "polizaPpal.estadoPoliza.idestado", "polizaCpl.estadoPoliza.idestado", "incSbpComp",
					"tipoEnvio.descripcion", FECHA_ENVIO_SBP, "importe", "estadoPlzSbp.idestado", "errorSbps",
					REF_PLZ_OMEGA, N_SOLICITUD);
		}

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.

		// Defino los tipos para los filtros. Habra que redefinir en el filter la forma
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		try {
			String perfil = usuario.getPerfil().substring(4);
			if (request.getParameter("ajax") == null) {
				if (origenLlamada == null || StringUtils.nullToString(origenLlamada).equals("deGrabacionDefinitiva")) {
					if (request.getSession().getAttribute("listadoPolizasSbp_LIMIT") != null) {
						// Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
						tableFacade.setLimit((Limit) request.getSession().getAttribute("listadoPolizasSbp_LIMIT"));
						String sbpRecalculada = StringUtils.nullToString(request.getParameter("sbpRecalculada"));
						if ("true".equals(sbpRecalculada)) {
							String idPolizaSbp = StringUtils.nullToString(request.getParameter("idPolizaSbp"));
							Filter filterId = new Filter("id", idPolizaSbp);
							tableFacade.getLimit().getFilterSet().addFilter(filterId);
						}
					}
				} else {
					if (StringUtils.nullToString(origenLlamada).equals("menuGeneral")
							|| StringUtils.nullToString(origenLlamada).equals("consultaPolizasParaSbp")
							|| StringUtils.nullToString(origenLlamada).equals("utilidadesPoliza")
							|| StringUtils.nullToString(origenLlamada).equals("seleccionPoliza")) {
						// -- FILTROS POR DEFECTO --
						String idPolizaSbp = StringUtils.nullToString(request.getParameter("idPolizaSbp"));
						PolizaSbp polSbp = new PolizaSbp();

						BigDecimal codEntidadUsuario = null;
	    	        	BigDecimal codOficinaUsuario = null;	    	        	
	    	        	
						switch (new Integer(perfil).intValue()) {
						case 4:
							Filter filterUsuario = new Filter("polizaPpal.usuario.codusuario", usuario.getCodusuario());
							tableFacade.getLimit().getFilterSet().addFilter(filterUsuario);
							break;
						default:
							break;
						}

						// filtros viniendo desde seleccionPoliza o desde utilidadesPoliza
						if (!idPolizaSbp.equals("")) {
							polSbp = simulacionSbpManager.getPolizaSbp(Long.valueOf(idPolizaSbp));
							Filter filterId = new Filter("id", idPolizaSbp);
							tableFacade.getLimit().getFilterSet().addFilter(filterId);
							if (polSbp != null) {
								if (polSbp.getPolizaPpal().getLinea().getCodplan() != null) {
									Filter filterPlan = new Filter(POLIZA_LINEA_PLAN,
											polSbp.getPolizaPpal().getLinea().getCodplan().toString());
									tableFacade.getLimit().getFilterSet().addFilter(filterPlan);
								}
								if (polSbp.getPolizaPpal().getLinea().getCodlinea() != null) {
									Filter filterLinea = new Filter(POLIZA_LINEA_LINEA,
											polSbp.getPolizaPpal().getLinea().getCodlinea().toString());
									tableFacade.getLimit().getFilterSet().addFilter(filterLinea);
								}
								if (polSbp.getReferencia() != null) {
									Filter filterReferencia = new Filter(REFERENCIA, polSbp.getReferencia());
									tableFacade.getLimit().getFilterSet().addFilter(filterReferencia);
								}

								switch (new Integer(perfil).intValue()) {
								case 1:
									codEntidadUsuario = usuario.getOficina().getEntidad().getCodentidad();
									break;
								case 3:
									codEntidadUsuario = usuario.getOficina().getEntidad().getCodentidad();
									codOficinaUsuario = usuario.getOficina().getId().getCodoficina();
									break;
								}
							}
						}

						else {// filtro por entidad del usuario
		    	        	
							switch (new Integer(perfil).intValue()) {
							case 1:
								codEntidadUsuario = usuario.getOficina().getEntidad().getCodentidad();
								break;
							case 3:
								codEntidadUsuario = usuario.getOficina().getEntidad().getCodentidad();
								codOficinaUsuario = usuario.getOficina().getId().getCodoficina();
								break;
							case 0:
							case 5:
								codEntidadUsuario = usuario.getOficina().getEntidad().getCodentidad();
								break;
							default:
								break;
							}
		    	        }
						// Anadimos el filtro por max plan
						BigDecimal maxPlan = simulacionSbpManager.getPlanSbp();
						if (maxPlan != null) {
							Filter filterPlan = new Filter(POLIZA_LINEA_PLAN, maxPlan.toString());
							tableFacade.getLimit().getFilterSet().addFilter(filterPlan);
						}

						if (codEntidadUsuario != null) {
							Filter filterEntidad = new Filter(POLIZA_TOMADOR_ENTIDAD, codEntidadUsuario.toString());
							tableFacade.getLimit().getFilterSet().addFilter(filterEntidad);
						}
						if (codOficinaUsuario != null) {
							Filter filterOficina = new Filter(POLIZA_OFICINA, codOficinaUsuario.toString());
							tableFacade.getLimit().getFilterSet().addFilter(filterOficina);
						}

						// -- ORDENACION POR DEFECTO --> Entidad,oficina,poliza y modulo
						Sort sortEntidad = new Sort(1, POLIZA_TOMADOR_ENTIDAD, Order.ASC);
						Sort sortOficina = new Sort(2, POLIZA_OFICINA, Order.ASC);
						Sort sortReferencia = new Sort(7, REFERENCIA, Order.ASC);
						Sort sortModulo = new Sort(10, POLIZA_MODULO, Order.ASC);
						tableFacade.getLimit().getSortSet().addSort(sortEntidad);
						tableFacade.getLimit().getSortSet().addSort(sortOficina);
						tableFacade.getLimit().getSortSet().addSort(sortReferencia);
						tableFacade.getLimit().getSortSet().addSort(sortModulo);
					}
				}
			}
			if (StringUtils.nullToString(origenLlamada).equals("primeraBusqueda")
					|| StringUtils.nullToString(origenLlamada).equals("deGrabacionDefinitiva")) {
				cargarFiltrosBusqueda(request, tableFacade);
				//
			}
			switch (new Integer(perfil).intValue()) {
			case 4:
				Filter filterUsuario = new Filter(USUARIO_PROVISIONAL, usuario.getCodusuario());
				tableFacade.getLimit().getFilterSet().addFilter(filterUsuario);
				break;
			}
		} catch (BusinessException be) {
			logger.debug("render error. " + be.getMessage());
		}

		// Find the data to display and build the Limit.
		listadoPolizaSbpService.setDataAndLimitVariables(tableFacade, filtrarDetalle, grupoEntidades, grupoOficinas); 
		String ajax = request.getParameter("ajax");
		if (!"false".equals(ajax)) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		}

		String html = listadoPolizaSbpService.html(tableFacade, usuario.getPerfil());
		return html;

	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HttpServletRequest request, TableFacade tableFacade) {
		// Entidad
		String entidad = request.getParameter("entidad");
		if (!StringUtils.nullToString(entidad).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(POLIZA_TOMADOR_ENTIDAD, entidad));
		}
		// Oficina
		String oficina = request.getParameter("oficina");
		if (!StringUtils.nullToString(oficina).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(POLIZA_OFICINA, oficina));
		}
		/* Pet. 63473 ** MODIF TAM (20/12/2021) ** inicio */
		// Entidad Mediadora
		String entmediadora = request.getParameter("entmediadora");
		if (!StringUtils.nullToString(entmediadora).equals("")) {
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter("polizaPpal.colectivo.subentidadMediadora.id.codentidad", entmediadora));
		}
		String subentmediadora = request.getParameter("subentmediadora");
		if (!StringUtils.nullToString(subentmediadora).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter("polizaPpal.colectivo.subentidadMediadora.id.codsubentidad", subentmediadora));
		}
		String delegacion = request.getParameter("delegacion");
		if (!StringUtils.nullToString(delegacion).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("polizaPpal.usuario.delegacion", delegacion));
		}
		/* Pet. 63473 ** MODIF TAM (20/12/2021) ** fin */

		// Usuario
		String codusuario = request.getParameter("codusuario");
		if (!StringUtils.nullToString(codusuario).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(USUARIO_PROVISIONAL, codusuario));
		}

		// Plan
		String plan = request.getParameter("plan");
		if (!StringUtils.nullToString(plan).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(POLIZA_LINEA_PLAN, plan));
		}

		// Linea
		String linea = request.getParameter("linea");
		if (!StringUtils.nullToString(linea).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(POLIZA_LINEA_LINEA, linea));
		}
		// Clase
		String clase = request.getParameter("clase");
		if (!StringUtils.nullToString(clase).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("polizaPpal.clase", clase));
		}
		// Referencia
		String referencia = request.getParameter(REFERENCIA);
		if (!StringUtils.nullToString(referencia).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(REFERENCIA, referencia));
		}
		// estado Ppal
		String estadoPpal = request.getParameter("estadoPpal");
		if (!StringUtils.nullToString(estadoPpal).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("polizaPpal.estadoPoliza.idestado", estadoPpal));
		}
		// Modulo
		String modulo = request.getParameter("modulo");
		if (!StringUtils.nullToString(modulo).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(POLIZA_MODULO, modulo));
		}
		// CIF/NIF
		String nifCif = request.getParameter("nifCif");
		if (!StringUtils.nullToString(nifCif).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("polizaPpal.asegurado.nifcif", nifCif));
		}
		// incSbpCpl
		String incSbpCpl = request.getParameter("incSbpCpl");
		if (!StringUtils.nullToString(incSbpCpl).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("incSbpComp", incSbpCpl));
		}
		// estadoCpl
		String estadoCpl = request.getParameter("estadoCpl");
		if (!StringUtils.nullToString(estadoCpl).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("polizaCpl.estadoPoliza.idestado", estadoCpl));
		}
		// FecEnvioId
		String fechaEnvioSbp = request.getParameter(FECHA_ENVIO_SBP);
		if (!StringUtils.nullToString(fechaEnvioSbp).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(FECHA_ENVIO_SBP, fechaEnvioSbp));
		}
		// Estado Sbp
		String estadoSbp = request.getParameter("estadoSbp");
		if (!StringUtils.nullToString(estadoSbp).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("estadoPlzSbp.idestado", estadoSbp));
		}
		// tipoEnvio
		String tipoEnvio = request.getParameter("tipoEnvio");
		if (!StringUtils.nullToString(tipoEnvio).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("tipoEnvio.descripcion", tipoEnvio));
		}
		// detalle
		String detalle = request.getParameter("detalle");
		if (!StringUtils.nullToString(detalle).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("errorSbps", detalle));
		}
		// refPlzOmega
		String refPlzOmega = request.getParameter(REF_PLZ_OMEGA);
		if (!StringUtils.nullToString(refPlzOmega).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(REF_PLZ_OMEGA, refPlzOmega));
		}
		// nsol
		String nSol = request.getParameter(N_SOLICITUD);
		if (!StringUtils.nullToString(nSol).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(/* "polizaPpal.idpoliza" */N_SOLICITUD, nSol));
		}
		/**
		 * P0073325 - RQ.10, RQ.11 y RQ.12
		 */
		// canalFirma
		String canalFirma = request.getParameter(CANAL_FIRMA);
		if (!StringUtils.nullToString(canalFirma).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("gedDocPolizaSbp.canalFirma.idCanal", canalFirma));
		}
		// docFirmada
		String docFirmada = request.getParameter(DOC_FIRMADA);
		if (!StringUtils.nullToString(docFirmada).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter("gedDocPolizaSbp.docFirmada", docFirmada));
		}
		

	}

	public void setListadoPolizaSbpService(IListadoPolizaSbpService listadoPolizaSbpService) {
		this.listadoPolizaSbpService = listadoPolizaSbpService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setConsultaSbpManager(IConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}

	public TableDataFilter getFiltroJmesa() {
		return filtroJmesa;
	}

	public void setFiltroJmesa(TableDataFilter filtroJmesa) {
		this.filtroJmesa = filtroJmesa;
	}

	public TableDataSort getSortJmesa() {
		return sortJmesa;
	}

	public void setSortJmesa(TableDataSort sortJmesa) {
		this.sortJmesa = sortJmesa;
	}

	public String getSuccessView() {
		return successView;
	}

	public String getId() {
		return id;
	}

	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}
	
	/**
	 * P0073325 - RQ.10, RQ.11 y RQ.12
	 */
	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
	
	
	public ModelAndView doExportToExcel(HttpServletRequest request) {
	    List<PolizaSbp> items;
	    try {
	        // Obtener todos los registros filtrados y ordenados
	        items = listadoPolizaSbpService.getAllFilteredAndSorted();

	        // Si hay registros, preparar los datos para la exportación a Excel
	        if (items.size() != 0) {
	            request.setAttribute("listado", items);
	            request.setAttribute("nombreInforme", "ListadoPolizasSobreprecio");
	            request.setAttribute("jasperPath", "informeJasper.listadoPolizaSbp");

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

}
