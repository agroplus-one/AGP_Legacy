/*
 **************************************************************************************************
 *
 *  CReACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034              Miguel Granadino  Controlador para pantallasConfigurables.jsp
 *
 **************************************************************************************************
 */
package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.LineaManager;
import com.rsi.agp.core.managers.impl.PantallasConfigurablesManager;
import com.rsi.agp.core.managers.impl.TallerConfiguradorPantallasManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.config.Pantalla;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.poliza.Linea;

public class PantallasConfigurablesController extends BaseSimpleController
		implements Controller {
	
	private static final Log logger = LogFactory.getLog(PantallasConfigurablesController.class);
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private PantallasConfigurablesManager pantallasConfigurablesManager;
	private TallerConfiguradorPantallasManager tallerConfiguradorPantallasManager;
	private LineaManager lineaManager;

	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public PantallasConfigurablesController() {
		setCommandClass(PantallaConfigurable.class);
		setCommandName("PantallaConfigurableBean");
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object object, BindException exception) throws Exception {
		
		ModelAndView mv = null;
		PantallaConfigurable pantallaConfigurableBean = (PantallaConfigurable) object;

		String op = StringUtils.nullToString(request.getParameter("operacion"));
		
		if (op.equals("")){
			op="consulta";
		}
		
		if (parameters != null) parameters.clear();
		
		if (op.equals("alta")) {
			mv = alta(request, pantallaConfigurableBean);
		} else if (op.equals("baja")) {
			mv = baja(request, pantallaConfigurableBean);
		} else if (op.equals("modificacion")) {
			mv = modificacion(request, pantallaConfigurableBean);
		} else if (op.equals("consulta")) {
			mv = consulta(request, pantallaConfigurableBean);
		} else if (op.equals("editar")) {
			mv = editar(request, response, pantallaConfigurableBean);

		} else if (op.equals("ajax_replicar")) {
			mv = ajax_replicar(request, response);
		} else if (op.equals("ajax_existPlanLinea")) {
			ajax_existPlanLinea(request, response);
		} else if (op.equals("ajax_getLineas")) {
			ajax_getLineas(request, response);
			return null;
		} else if (op.equals("redirect_tallerPantallasConfigurables")) {
			request.setAttribute("tallerPantalla", true);
			mv = redirect_tallerPantallasConfigurables(request);
		} else if (op.equals("inicializar")) {
			@SuppressWarnings("rawtypes")
			List listPantallas = pantallasConfigurablesManager.getPantallas();
			parameters.put("listPantallas", listPantallas);
			request.setAttribute("tablaConfigurables", "vacio");
			mv = inicializar(request, pantallaConfigurableBean);
		}
		else if (op.equals("volver")) {
			mv = volver(request);
		}
		else {
			mv = loadDefaultData("allRegisters", pantallaConfigurableBean);
		}
		
		String replicarOK = StringUtils.nullToString(request.getParameter("replicarOK"));
		if (replicarOK.equals("true")){
			parameters.put("mensaje", bundle.getString("mensaje.replica.OK"));
		}
		if (parameters != null && parameters.size() > 0) {
			mv.addAllObjects(parameters);
		}
		return mv;
	}
	
	
	/**
	 * Vuelve al listado de configuracion de pantallas filtrando por la busqueda anterior
	 * @param request
	 * @return
	 */
	private ModelAndView volver (HttpServletRequest request) {
		
		PantallaConfigurable pc = new PantallaConfigurable();
		// Comprueba si se ha guardado en sesion la busqueda anterior
		if (request.getSession().getAttribute("pantallaConfigurableBean") != null) {
			pc = (PantallaConfigurable) request.getSession().getAttribute("pantallaConfigurableBean");
		}
		
		return consulta(request, pc);
	}

	/*
	 * Alta 
	 */
	private ModelAndView alta(HttpServletRequest request, PantallaConfigurable pantallaConfigurableBean) {
		try {

			Linea linea = lineaManager.obtenerLinea(pantallaConfigurableBean.getLinea().getCodlinea(), pantallaConfigurableBean.getLinea().getCodplan());
			
			if(linea!=null){
				pantallaConfigurableBean.setLinea(linea);
				
				boolean existePantallaConfigurada = pantallasConfigurablesManager.existePantalla(
						pantallaConfigurableBean.getLinea().getLineaseguroid(),
						pantallaConfigurableBean.getPantalla().getIdpantalla(), null);
	
				if (!existePantallaConfigurada) {
					String grupoSeguro=this.obtieneGrupoSeguro(pantallaConfigurableBean.getLinea().getCodlinea());
					if (!grupoSeguro.equals("")){
						pantallaConfigurableBean.setGruposeguro(grupoSeguro);
						//control de obligatoria
						pantallaConfigurableBean.setObligatoria(0);
						if(new Long(7).equals(pantallaConfigurableBean.getPantalla().getIdpantalla()) && !(linea.isLineaGanado())){
							pantallaConfigurableBean.setObligatoria(1);
						}
						if(new Long(101).equals(pantallaConfigurableBean.getPantalla().getIdpantalla()) && (linea.isLineaGanado())){
							pantallaConfigurableBean.setObligatoria(1);
						}
						pantallasConfigurablesManager.savePantallaConfigurable(pantallaConfigurableBean);
						parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
					}else{// grupoSeguro no encontrado en tabla TB_SC_C_LINEAS
						parameters.put("alerta", bundle.getString("mensaje.configurables.linea.grupoSeguro.KO"));						
					}
				} else {
					parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
				}
			}else{
				parameters.put("alerta", bundle.getString("mensaje.configurables.linea.KO"));
			}
		} catch (Exception excepcion) {
			logger.error("Error al dar de alta el registro", excepcion);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
		}
		return loadDefaultData("noneAllRegisters", pantallaConfigurableBean);
	}

	/*
	 * Modificacion 
	 */
	private ModelAndView modificacion(HttpServletRequest request, PantallaConfigurable pantallaConfigurableBean) {
		
		try {
			Linea linea = lineaManager.obtenerLinea(pantallaConfigurableBean.getLinea().getCodlinea(), pantallaConfigurableBean.getLinea().getCodplan());
			
			if(linea!=null){
				pantallaConfigurableBean.setLinea(linea);
				
				boolean existePantallaConfigurada = false;
				
				if (pantallaConfigurableBean != null) {
					existePantallaConfigurada = pantallasConfigurablesManager.existePantalla(
						pantallaConfigurableBean.getLinea().getLineaseguroid(),
						pantallaConfigurableBean.getPantalla().getIdpantalla(), pantallaConfigurableBean.getIdpantallaconfigurable());
				}
				
				if (pantallaConfigurableBean != null && !existePantallaConfigurada) {
					String grupoSeguro=this.obtieneGrupoSeguro(pantallaConfigurableBean.getLinea().getCodlinea());
					pantallaConfigurableBean.setGruposeguro(grupoSeguro);
					//control de obligatoria
					pantallaConfigurableBean.setObligatoria(0);
					if(new Long(7).equals(pantallaConfigurableBean.getPantalla().getIdpantalla()) && !(linea.isLineaGanado())){
						pantallaConfigurableBean.setObligatoria(1);
					}
					if(new Long(101).equals(pantallaConfigurableBean.getPantalla().getIdpantalla()) && (linea.isLineaGanado())){
						pantallaConfigurableBean.setObligatoria(1);
					}
					pantallasConfigurablesManager.savePantallaConfigurable(pantallaConfigurableBean);
					parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
				}
				else if (existePantallaConfigurada){
					parameters.put("alerta", bundle.getString("mensaje.modificacion.duplicado.KO"));
				}
			}else{
				parameters.put("alerta", bundle.getString("mensaje.configurables.linea.KO"));
			}
		} catch (Exception excepcion) {
			logger.error("Error al modificar el registro" ,excepcion);
			parameters.put("alerta", bundle.getString("mensaje.modificacion.KO"));
		}
		return ((pantallaConfigurableBean != null) ? loadDefaultData("noneAllRegisters", pantallaConfigurableBean) : null);
	}

	/*
	 * Baja
	 */
	private ModelAndView baja(HttpServletRequest request,
			PantallaConfigurable pantallaConfigurableBean) {

		try {
			pantallasConfigurablesManager.delete(pantallaConfigurableBean
					.getIdpantallaconfigurable());
			parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
		} catch (Exception excepcion) {
			logger.error("Error al dar de baja el registro" ,excepcion);
			parameters.put("alerta", bundle.getString("mensaje.baja.KO"));
		}
		return loadDefaultData("noneAllRegisters", pantallaConfigurableBean);
	}

	/*
	 * Consulta
	 */
	private ModelAndView inicializar(HttpServletRequest request,
			PantallaConfigurable pantallaConfigurableBean) {
		
		// Pantalla de busqueda sin resultados
		return new ModelAndView(
				"moduloTaller/pantallasConfigurables/pantallasConfigurables",
				"PantallaConfigurableBean", pantallaConfigurableBean);
	}
	
	/*
	 * Consulta
	 */
	private ModelAndView consulta(HttpServletRequest request,
			PantallaConfigurable pantallaConfigurableBean) {
		
		// Guarda en sesion el filtro de busqueda utilizado
		request.getSession().setAttribute("pantallaConfigurableBean", pantallaConfigurableBean);
		
		// Busca los registros que se ajustan al filtro de busqueda
		return loadDefaultData("noneAllRegisters", pantallaConfigurableBean);
	}

	/*
	 * Editar
	 */
	private ModelAndView editar(HttpServletRequest request,
			HttpServletResponse response,
			PantallaConfigurable pantallaConfigurableBean) {
		
		if(request.getParameter("idPantallaConfigurable")!=null){
			pantallaConfigurableBean = pantallasConfigurablesManager.getPantallaConfigurable(new Long(request.getParameter("idPantallaConfigurable")));

			JSONObject pantallaConfigurableJSON = new JSONObject();
			try{
				pantallaConfigurableJSON.put("idpantallaconfigurable",	pantallaConfigurableBean.getIdpantallaconfigurable());
				pantallaConfigurableJSON.put("plan", 					pantallaConfigurableBean.getLinea().getCodplan());
				pantallaConfigurableJSON.put("codlinea", 				pantallaConfigurableBean.getLinea().getCodlinea());
				pantallaConfigurableJSON.put("nomlinea", 				pantallaConfigurableBean.getLinea().getNomlinea());
				pantallaConfigurableJSON.put("pantalla", 				pantallaConfigurableBean.getPantalla().getIdpantalla());

				getWriterJSON(response, pantallaConfigurableJSON);
				return null;
				
			}catch (JSONException e) {
				logger.error("Error al editar la pantalla configurable", e);
			}
		}	

		return loadDefaultData("allRegisters", pantallaConfigurableBean);
	}

	/*
	 * Replicar
	 */
	private ModelAndView ajax_replicar(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = null;

		String idLineaOrigen = StringUtils.nullToString(request.getParameter("idLineaOrigen"));
		String idLineaDestino = StringUtils.nullToString(request.getParameter("idLineaDestino"));
		JSONArray list = new JSONArray();
		try {
			pantallasConfigurablesManager.replicar(new Long(idLineaOrigen),new Long(idLineaDestino));
		} catch (NumberFormatException e) {
			logger.error("Error durante la creacion de pantallas configurables", e);
		} catch (Exception e) {
			logger.error("Error indefinido durante la creacion de pantallas configurables", e);
		}
		getWriterJSON(response, list);
		return mv;
	}

	@SuppressWarnings("rawtypes")
	private ModelAndView loadDefaultData(String rows,
			PantallaConfigurable pantallaConfigurableBean) {

		List listPlanes = pantallasConfigurablesManager.getPlanes();
		parameters.put("listPlanes", listPlanes);

		List listPantallas = pantallasConfigurablesManager.getPantallas();
		
		parameters.put("listPantallas", listPantallas);
		if (pantallaConfigurableBean.getPantalla().getIdpantalla()!= null){
			for (int i=0;i<listPantallas.size();i++){
				Pantalla p = (Pantalla)listPantallas.get(i);
				if (pantallaConfigurableBean.getPantalla().getIdpantalla().equals(p.getIdpantalla())){
					pantallaConfigurableBean.getPantalla().setDescpantalla(p.getDescpantalla());
				}
			}
		}
		
		if (rows.equals("allRegisters")) {
			putAllPantallasConfigurables();
		}else{
			putPantallaConfigurable(pantallasConfigurablesManager.consulta(pantallaConfigurableBean));
		}

		return new ModelAndView(
				"moduloTaller/pantallasConfigurables/pantallasConfigurables",
				"PantallaConfigurableBean", pantallaConfigurableBean);
	}

	@SuppressWarnings("rawtypes")
	private void putAllPantallasConfigurables() {
		List listPantallasConfigurables = pantallasConfigurablesManager
				.getPantallasConfigurables();
		parameters.put("listPantallasConfigurables", listPantallasConfigurables);
	}

	@SuppressWarnings("rawtypes")
	private void putPantallaConfigurable(List consulta) {
		parameters.put("listPantallasConfigurables", consulta);
	}

	@SuppressWarnings("unchecked")
	private void ajax_getLineas(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String idPlan	= StringUtils.nullToString(request.getParameter("idPlan"));
			String codLinea = StringUtils.nullToString(request.getParameter("codLinea"));
			
			JSONObject element = null;
			JSONArray list = new JSONArray();
			List<Linea> listLineas = pantallasConfigurablesManager.getLineas(new BigDecimal(idPlan));

			for (Linea linea : listLineas) {
				
				String indiceSeleccionado = "";
				
				element = new JSONObject();
				element.put("value", linea.getLineaseguroid());
				element.put("nodeText", linea.getCodlinea() + " - "	+ linea.getNomlinea());
				
				if(codLinea.equals(linea.getCodlinea().toString())){
					indiceSeleccionado = linea.getLineaseguroid().toString();
					element.put("indiceSeleccionado", indiceSeleccionado);
				}
				
				list.put(element);
			}
			getWriterJSON(response, list);

		} catch (Exception excepcion) {
			logger.error("Error al obtener las líneas", excepcion);
		}
	}

	@SuppressWarnings("rawtypes")
	private void ajax_existPlanLinea(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String control = "false";
			String lineaOrigen = StringUtils.nullToString(request
					.getParameter("LineaOrigen"));
			String lineaDestino = StringUtils.nullToString(request
					.getParameter("LineaDestino"));
			JSONObject element = new JSONObject();
			JSONArray list = new JSONArray();
			List listLineaDestino = null;
			List listLineaOrigen = null;

			// Comprobacion que el plan/linea ORIGEN esta configurada,
			// si no lo esta no permitir replicacion
			listLineaOrigen = pantallasConfigurablesManager
					.getPantallasConfigurables(new Long(lineaOrigen));
			if (listLineaOrigen != null) {
				if (listLineaOrigen.size() == 0) {
					control = "noConfiguracion";
				}
			}

			// Comprobacion de que la plan/linea DESTINO esta ya configurada,
			// si ya lom esta preguntar al user si quiere replicar
			if (!control.equals("noConfiguracion")) {
				listLineaDestino = pantallasConfigurablesManager
						.getPantallasConfigurables(new Long(lineaDestino));
				if (listLineaDestino != null) {
					if (listLineaDestino.size() > 0) {
						control = "true";
					}
				}
			}

			element.put("result", control);
			list.put(element);
			logger.info(list.toString());
			getWriterJSON(response, list);

		} catch (Exception excepcion) {
			logger.error("Error al comprobar si existe el plan/línea", excepcion);
		}
	}

	private ModelAndView redirect_tallerPantallasConfigurables(
			HttpServletRequest request) {

		PantallaConfigurable pantallaConfigurable = pantallasConfigurablesManager
				.getPantallaConfigurable(new Long(request.getParameter("ROW")));
		
		parameters.put("codPlan", pantallaConfigurable.getLinea().getCodplan());
		parameters.put("idLinea", pantallaConfigurable.getLinea()
				.getLineaseguroid());
		parameters.put("codLinea", pantallaConfigurable.getLinea()
				.getCodlinea());
		parameters.put("descPantalla", pantallaConfigurable.getPantalla()
				.getDescpantalla());
		parameters.put("idPantalla", pantallaConfigurable
				.getIdpantallaconfigurable());
		parameters.put("idRowConfigurar", request.getParameter("ROW"));
		
		parameters.put("usosLst",
				this.tallerConfiguradorPantallasManager.getUsos(pantallaConfigurable.getLinea().getLineaseguroid()));
		parameters.put("origenDatosLst", this.tallerConfiguradorPantallasManager.getOrigenDatos());
		parameters.put("tipoCampoLst", this.tallerConfiguradorPantallasManager.getTiposCampo());

		return new ModelAndView(
				"moduloTaller/pantallasConfigurables/tallerConfiguracionPantallas",
				"pantallasConfigurables", parameters);
	}
	
	public String obtieneGrupoSeguro(BigDecimal codLinea){
		return pantallasConfigurablesManager.obtieneGrupoSeguro(codLinea);
	}
	
	public void setPantallasConfigurablesManager(
			PantallasConfigurablesManager pantallasConfigurablesManager) {
		this.pantallasConfigurablesManager = pantallasConfigurablesManager;
	}

	public void setTallerConfiguradorPantallasManager(
			TallerConfiguradorPantallasManager tallerConfiguradorPantallasManager) {
		this.tallerConfiguradorPantallasManager = tallerConfiguradorPantallasManager;
	}

	public void setLineaManager(LineaManager lineaManager) {
		this.lineaManager = lineaManager;
	}
}