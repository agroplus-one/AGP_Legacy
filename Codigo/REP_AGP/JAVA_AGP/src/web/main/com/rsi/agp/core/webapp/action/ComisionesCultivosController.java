package com.rsi.agp.core.webapp.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.CultivosEntidadesHistoricoManager;
import com.rsi.agp.core.managers.impl.CultivosEntidadesManager;
import com.rsi.agp.core.managers.impl.CultivosSubEntidadesHistoricoManager;
import com.rsi.agp.core.managers.impl.CultivosSubEntidadesManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ExcelUtils;
import com.rsi.agp.core.webapp.util.ParametrizacionHelper;
import com.rsi.agp.core.webapp.util.ParametrizacionPolizaHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidadesHistorico;
import com.rsi.agp.dao.tables.comisiones.DetalleComisionEsMediadora;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;

public class ComisionesCultivosController extends BaseMultiActionController{
	
	private static final Log logger = LogFactory.getLog(ComisionesCultivosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private CultivosEntidadesManager cultivosEntidadesManager;
	private CultivosSubEntidadesManager cultivosSubEntidadesManager;
	private CultivosSubEntidadesHistoricoManager cultivosSubEntidadesHistoricoManager;
	private CultivosEntidadesHistoricoManager cultivosEntidadesHistoricoManager;
	private PolizaManager polizaManager;
	
	// Constantes
	private static final String ERROR = "ERROR";
	private static final String IDPLAN = "idplan";
	private static final String MENSAJE = "mensaje";
	private static final String MENSAJE_COMISIONES_OK = "mensaje.comisiones.reglamento.replicar.OK";
	private static final String MENSAJE_COMISIONES_KO = "mensaje.comisiones.KO";
	private static final String ACTIVAR_MODO_MODIFICAR = "activarModoModificar";
	private static final String ALTA_DEL_REGISTRO_EN_EL_HISTORICO = "alta del registro en el historico";
	private static final String USUARIO = "usuario";
	private static final String ALERTA = "alerta";
	private static final String ID_FICHERO = "idFichero";
	private static final String TIPO_FICHERO = "tipoFichero";
	private static final String PROCEDENCIA = "procedencia";
	private static final String FILTRO_CULTIVOS_SUBENT = "filtroCultivosSubent";
	private static final String PLAN = "Plan";
	private static final String LINEA = "Línea";
	private static final String E_S_MED = "E-S Med.";
	private static final String G_N = "G.N.";
	private static final String COMISION_MAXIMO = "% Comisión máximo";
	private static final String ADMINISTRACION = "% Administración";
	private static final String ADQUISICION = "% Adquisición";
	private static final String FEC_EFECTO = "Fec. Efecto";
	private static final String FEC_BAJA = "Fec. Baja";
	private static final String LIMPIAR_REPLICAR = "limpiarReplicar";
	
	private static final HashMap<Integer, String> titulosColumnas = new HashMap<>();

    static {
    	
    	titulosColumnas.put(0, PLAN);
    	titulosColumnas.put(1, LINEA);
    	titulosColumnas.put(2, E_S_MED);
    	titulosColumnas.put(3, G_N);
    	titulosColumnas.put(4, COMISION_MAXIMO);
    	titulosColumnas.put(5, ADMINISTRACION);
    	titulosColumnas.put(6, ADQUISICION);
    	titulosColumnas.put(7, FEC_EFECTO);
    	titulosColumnas.put(8, FEC_BAJA);
    }
    	
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String LISTADO_CULTIVO_ENTIDADES = "LISTADO CULTIVO ENTIDADES";
	private static final String ATTACHMENT_FILENAME_LISTADO_CULTIVOS_ENTIDADES_XLS = "attachment; filename=listadoCultivosEntidades.xls";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
	
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat(DD_MM_YYYY);
		// True indica que se aceptan fechas vacias
		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
		binder.registerCustomEditor(Date.class, editor);
}
	
	
	public ModelAndView doConsulta (HttpServletRequest request, 
			HttpServletResponse response,CultivosSubentidades cultivosSubentidadesBean) {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		CultivosEntidades cultivosEntidades = null;
		List<CultivosSubentidades> listCultivosSubentidades = null;		
		ModelAndView mv = null;
		Linea linea = new Linea();
		
		try {			
			if (StringUtils.nullToString(request.getParameter("limpiarFiltro")).equals("false")){
				CultivosSubentidades filtroCultivosSubent = (CultivosSubentidades) request.getSession().getAttribute(FILTRO_CULTIVOS_SUBENT);
				if (filtroCultivosSubent != null) {
					filtroCultivosSubent.setId(null);
					cultivosSubentidadesBean = filtroCultivosSubent;
				}
			}else {
				request.getSession().removeAttribute(FILTRO_CULTIVOS_SUBENT);
				request.getSession().setAttribute(FILTRO_CULTIVOS_SUBENT, cultivosSubentidadesBean);
			}
			logger.debug("se comprueban si existen los parametros que se envian desde la pantalla de incidencias");
			if (request.getParameter("entidadmediadora") != null){
				cultivosSubentidadesBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal(request.getParameter("entidadmediadora")));				
			}	
			
			if (request.getParameter("subentidad") != null){
				cultivosSubentidadesBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(request.getParameter("subentidad")));
				if (request.getParameter("nomsubentidad") != null){
					cultivosSubentidadesBean.getSubentidadMediadora().setNomsubentidad(request.getParameter("nomsubentidad"));
				}
			}	
				
			if (request.getParameter("planIncidencias") != null){
				cultivosSubentidadesBean.getLinea().setCodplan(new BigDecimal(request.getParameter("planIncidencias")));
				cultivosEntidades = cultivosEntidadesManager.getLastCultivosEntidades(cultivosSubentidadesBean.getLinea().getCodplan());
			} else {
				if (StringUtils.nullToString(request.getParameter("primeraConsulta")).equals("true")) {
					if (cultivosSubentidadesBean.getLinea().getCodplan() != null){
						cultivosEntidades = cultivosEntidadesManager.getLastCultivosEntidades(cultivosSubentidadesBean.getLinea().getCodplan());
						if (cultivosEntidades == null){
							cultivosEntidades = cultivosEntidadesManager.getLastCultivosEntidades();
							cultivosSubentidadesBean.getLinea().setCodplan(cultivosEntidades.getLinea().getCodplan());
						}
					} else {
						cultivosEntidades = cultivosEntidadesManager.getLastCultivosEntidades();					
					}
					cultivosSubentidadesBean.getLinea().setCodplan(cultivosEntidades.getLinea().getCodplan());
				}
			}
			
			
			String procedencia = request.getParameter(PROCEDENCIA);
			if (("incidenciasComisiones".equalsIgnoreCase(procedencia) ||
					"incidenciasComisionesUnificadas".equalsIgnoreCase(procedencia))
				&& StringUtils.nullToString(request.getParameter("consultando")).equals("") ) {
				parametros.put(PROCEDENCIA, procedencia);
				String lin =request.getParameter("codLineaCom");
				String plan=request.getParameter("planLineaCom");
				if (lin !=null) {
					linea.setCodlinea(new BigDecimal(lin));
					if(null!=plan) {
						linea.setCodplan(new BigDecimal(plan));
					}					
					cultivosSubentidadesBean.setLinea(linea);	
					if(null!=cultivosEntidades && null!=cultivosEntidades.getLinea()) {
						cultivosEntidades.getLinea().setCodlinea(new BigDecimal(lin));
					}
				}
			}
			parametros.put(PROCEDENCIA, procedencia);
			
			listCultivosSubentidades = cultivosSubEntidadesManager.listCultivosSubentidades(cultivosSubentidadesBean);
			logger.debug("listCultivosSubentidades. Size=" + listCultivosSubentidades.size());
			
			Map<String, Object> paramsReplicar= null;
			paramsReplicar = cultivosSubEntidadesManager.getPlanesReplicar(listCultivosSubentidades);
			parametros.putAll(paramsReplicar);
			
			//SI NO ESTA DADA DE ALTA PARA LA LINEA GENERICA, MOSTRAMOS UN ALERTA Y PREPARAMOS LOS DATOS NECESARIOS PARA EL ALTA
			/*logger.debug("Comprobamos si esta dada de alta la linea generica");
			if(!cultivosSubEntidadesManager.compruebaLinea999()){
				cultivosSubentidadesBean = cultivosSubEntidadesManager.generarDistMedLinea999();
				parametros.put("altaLinea999",true);
			}else{
				parametros.put("altaLinea999",false);
			}*/
			
			
			if (request.getParameter(TIPO_FICHERO) != null && request.getParameter(ID_FICHERO) != null )
			{
				parametros.put(ID_FICHERO,request.getParameter(ID_FICHERO));
				parametros.put(TIPO_FICHERO,request.getParameter(TIPO_FICHERO));	
			} else {
				parametros.put(ID_FICHERO,"");
				parametros.put(TIPO_FICHERO,"");	
			}
			parametros.put("listCultivosSubentidades", listCultivosSubentidades);
			parametros.put("cultivosEntidades", cultivosEntidades);
			
			mv = new ModelAndView("moduloComisiones/comisionesCultivos","cultivosSubentidadesBean",cultivosSubentidadesBean);
			
		}catch (Exception be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			parametros.put(ALERTA, bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new CultivosSubentidades());
		}
		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doGuardarDistribucionMediadores (HttpServletRequest request,
			HttpServletResponse response,CultivosSubentidades cultivosSubentidadesBean){
		logger.debug("init - doGuardarDistribucionMediadores");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);	
		ArrayList<Integer> errList = null;
		
		try {
				logger.debug("comprabamos que no esta duplicado el registro");
				if(!cultivosSubEntidadesManager.existeRegistro(cultivosSubentidadesBean)){
					logger.debug("proceso de alta/modificacion de una distribucion de mediadores");
					if(cultivosSubentidadesBean.getId() == null){
							
							cultivosSubentidadesBean.setUsuario(usuario);
							
							logger.debug("alta del registro");
							errList = cultivosSubEntidadesManager.guardarDistribucionMediadores(cultivosSubentidadesBean);
							logger.debug("Errores Validacion=" + errList.size());
							
							if(errList.size() == 0){							
								logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
								cultivosSubEntidadesHistoricoManager.addResgitroHist(cultivosSubentidadesBean,
										ComisionesConstantes.AccionesHistComisionCte.ALTA,usuario);
							}
							
							parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.distMed.alta.OK"));
							
						
					}else{
						
						logger.debug("modificacion del registro");
						errList = cultivosSubEntidadesManager.guardarDistribucionMediadores(cultivosSubentidadesBean);
						logger.debug("Errores Validacion=" + errList.size());
						
						if(errList.size() > 0)
							cultivosSubentidadesBean = cultivosSubEntidadesManager.getCultivosSubEntidades(cultivosSubentidadesBean.getId());
						else{
							logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
							cultivosSubEntidadesHistoricoManager.addResgitroHist(cultivosSubentidadesBean,
									ComisionesConstantes.AccionesHistComisionCte.MODIFICACION,usuario);
						}						
						
						parametros = this.gestionMensajes(errList,bundle.getString("mensaje.comisiones.distMed.modificacion.OK"));
						parametros.put(ACTIVAR_MODO_MODIFICAR, true);
					}
				}else{
					parametros.put(ALERTA, bundle.getString("mensaje.comisiones.distMed.alta.KO"));					
				}
				
			mv = doConsulta(request, response, cultivosSubentidadesBean);
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al dar de alta/editar una distribucion de mediadores: ", be);			
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO));
			mv = doConsulta(request, response, cultivosSubentidadesBean);
		}
		logger.debug("end - doGuardarDistribucionMediadores");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doBorrarDistribucionMediadores (HttpServletRequest request, HttpServletResponse response,
			CultivosSubentidades cultivosSubentidadesBean){
		logger.debug("init - doBorrarDistribucionMediadores");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Long id = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);	
		try {
			
			id = new Long(StringUtils.nullToString(request.getParameter("id")));
			logger.debug("Recuperamos la distribucion de mediadores que se va a eliminar. ID=" + id);
			CultivosSubentidades cultivosSubentidades = cultivosSubEntidadesManager.getCultivosSubEntidades(id);
				
			logger.debug("Borramos el registro");
			cultivosSubEntidadesManager.borrarParametrosComisiones(cultivosSubentidades);
			parametros.put(MENSAJE, bundle.getString("mensaje.comisiones.distMed.baja.OK"));
			
//			INTRODUCIMOS UN REGISTRO NUEVO EN EL HISTORICO POR CADA OPERACION
			logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
			cultivosSubEntidadesHistoricoManager.addResgitroHist(cultivosSubentidades,
					ComisionesConstantes.AccionesHistComisionCte.BAJA,usuario);
			
			mv = doConsulta(request, response, cultivosSubentidadesBean);
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al dar de baja una distribucion de mediadores: " + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO));
			mv = doConsulta(request, response,cultivosSubentidadesBean);
		}
		logger.debug("end - doBorrarDistribucionMediadores");
		return mv.addAllObjects(parametros);
	}
	
	
	
	/**
	 * Comprueba que al dar de alta o mdificar una comision exista '% de comision' en parametros generales
	 * para ese plan y esa linea
	 */
	public ModelAndView doExisteComisionMaxima(HttpServletRequest request, HttpServletResponse response,
			CultivosSubentidades cultivosSubentidadesBean){
		logger.debug("init - doexisteComisionMaxima");
		String idplan = StringUtils.nullToString(request.getParameter(IDPLAN));
		String idlinea = StringUtils.nullToString(request.getParameter("idlinea"));
		JSONObject resultado = new JSONObject();
		String result = "";
		try {		
			
			if (cultivosEntidadesManager.existeComisionMaxima (idplan,idlinea)) {
				result = "OK";
			}else {
				result = "KO";
			}
			
		} catch (Exception be) {
			result = ERROR;
			logger.error("Se ha producido un error al doExisteComisionMaxima: " + be.getMessage());			
		}
		try {
			resultado.put("resultado",result);
		} catch (JSONException e) {
			logger.error("Se ha producido un error al doExisteComisionMaxima: " + e.getMessage());		
		}
		logger.debug("fin - doExisteComisionMaxima");
		getWriterJSON(response,resultado);
		return null;
	}
	
	public ModelAndView doReplicar (HttpServletRequest request, HttpServletResponse response,CultivosSubentidades cultivosSubentidadesBean) throws Exception{
		logger.debug("init - doReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<CultivosSubentidades> listCultivosSubentidades = new ArrayList<CultivosSubentidades>();
		List<CultivosSubentidades> listCultivosSubentidadesAux = null;
		List<CultivosEntidades> listCultivosEntidades = null;
		CultivosEntidades cultivosEntidadesBean = new CultivosEntidades();
		ModelAndView mv = null;
		ArrayList<Integer> errList = new ArrayList<Integer>();
		String planOrigen = "";
		String planDestino = "";
		String lineaOrigen = "";
		String lineaDestino = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");	
		try {
			planOrigen = StringUtils.nullToString(request.getParameter("plan_orig"));
			planDestino = StringUtils.nullToString(request.getParameter("plan_dest"));
			lineaOrigen = StringUtils.nullToString(request.getParameter("linea_orig"));
			lineaDestino = StringUtils.nullToString(request.getParameter("linea_dest"));
			
			/* ESC-17100 ** MODIF TAM (08/02/2022) ** Inicio */
			String planFiltro =  StringUtils.nullToString(request.getParameter("plan_filtro"));
			String lineaFiltro = StringUtils.nullToString(request.getParameter("linea_filtro"));
			String entidadFiltro = StringUtils.nullToString(request.getParameter("entidad_filtro"));
			String subentFiltro = StringUtils.nullToString(request.getParameter("subentidad_filtro"));
			/* ESC-17100 ** MODIF TAM (08/02/2022) ** Fin */
			
			
			if (Integer.parseInt(planDestino) <= Integer.parseInt(planOrigen)){
				errList.add(7);
			} else {				
				logger.debug("Parametros para la replicacion. PlanOrigen: " + planOrigen + " || PlanDestino: " + planDestino + 
						" || LineaOrigen: " + lineaOrigen + " || LineaDestino: " + lineaDestino );
				
				logger.debug("Replicamos el plan Origen");
				errList = cultivosSubEntidadesManager.replicarPlanLinea(planOrigen, planDestino, lineaOrigen, lineaDestino);
				logger.debug("Errores de validacion: " + errList.size());
				
				if(errList.size() == 0){					
					Linea objlinea = new Linea();				
					objlinea.setCodplan(new BigDecimal(planDestino));
					objlinea.setCodlinea(new BigDecimal(lineaDestino));
					cultivosSubentidadesBean.setLinea(objlinea);
					listCultivosSubentidadesAux = cultivosSubEntidadesManager.listCultivosSubentidades(cultivosSubentidadesBean);
					logger.debug("listCultivosSubentidades replicados. Size=" + listCultivosSubentidadesAux.size());
					
					/* ESC-17100 - Parte VI - MODIF TAM (22/02/2022) * INICIO */
					/* quitamos de la lista los registros que estén dados de Baja */
					for(int i = 0; i < listCultivosSubentidadesAux.size(); i++){
						logger.debug ("Valor de i:"+i);
						CultivosSubentidades cultSubEnt = listCultivosSubentidadesAux.get(i);
						logger.debug("Valor de FecBaja:"+cultSubEnt.getFecBaja() );
						if (cultSubEnt.getFecBaja() == null) {
							listCultivosSubentidades.add(cultSubEnt);
						}
					}
					/* ESC-17100 - Parte VI - MODIF TAM (22/02/2022) * FIN */
					
					logger.debug("generamos el registro de historico de subentidades");
					cultivosSubEntidadesHistoricoManager.addResgitroHistReplicar(listCultivosSubentidades,usuario);
					
					cultivosEntidadesBean.setLinea(objlinea);
					listCultivosEntidades = cultivosEntidadesManager.listComisionesCultivosEntidades(cultivosEntidadesBean);
					logger.debug("listCultivosEntidades replicados. Size=" + listCultivosEntidades.size());
					
					/* ESC-17100 ** MODIF TAM (21.02.2022) ** Inicio */
					/* Como ya no se inserta registro en los parametros generales al replicar tampoco se deben dar de alta registros en el */ 
					/* historico de Parametros generales */
					/*logger.debug("generamos el registro de historico de entidades");
					/*cultivosEntidadesHistoricoManager.addResgitroHistReplicar(listCultivosEntidades);*/
					/* ESC-17100 ** MODIF TAM (21.02.2022) ** Fin */
				}
			}					
			
			parametros = this.gestionMensajes(errList,  bundle.getString("mensaje.comisiones.distMed.replicar.OK"));
			
			logger.debug("si ha habido algun error, se filtra por el plan y la linea actuales");
			if (cultivosSubentidadesBean.getLinea().getCodplan() == null){
				/* ESC-17100 ** MODIF TAM (08.02.2021) ** Inicio */
				/* Informamos con los datos del filtro al ejecutar la replica */
				/*CultivosEntidades cultivosEntidades = cultivosEntidadesManager.getLastCultivosEntidades();
				/*cultivosSubentidadesBean.getLinea().setCodplan(cultivosEntidades.getLinea().getCodplan());
				/*cultivosSubentidadesBean.getLinea().setCodlinea(cultivosEntidades.getLinea().getCodlinea());*/
				cultivosSubentidadesBean.getLinea().setCodplan(new BigDecimal(planFiltro));
				cultivosSubentidadesBean.getLinea().setCodlinea(new BigDecimal(lineaFiltro));
				
			}
			
			mv = doConsulta(request, response, cultivosSubentidadesBean);
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al replicar distribucion de mediadores para un plan",be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO));
			mv = doConsulta(request, response, new CultivosSubentidades());
		}
		logger.debug("end - doReplicar");
		return mv.addAllObjects(parametros);
	}
	
	//
	public ModelAndView doReplicarCultivo (HttpServletRequest request, HttpServletResponse response,  CultivosEntidades cultivosEntidadesBean) throws Exception{
		
		logger.debug("init - doReplicarCultivo");
		
		Map<String, Object> parametros = new HashMap<String, Object>();

		ModelAndView mv = null;
				
		try {
			
			BigDecimal planOrigen = new BigDecimal(request.getParameter("plan_origen"));
			BigDecimal planDestino = new BigDecimal(request.getParameter("plan_destino"));
			BigDecimal lineaOrigen = new BigDecimal(request.getParameter("linea_origen"));
			BigDecimal lineaDestino = new BigDecimal(request.getParameter("linea_destino"));
			
			parametros.put("planOrigen", planOrigen);
			parametros.put("planDestino", planDestino);
			parametros.put("lineaOrigen", lineaOrigen);
			parametros.put("lineaOrigenDesc", request.getParameter("linea_origen_desc"));
			parametros.put("lineaDestino", lineaDestino);
			parametros.put("lineaDestinoDesc", request.getParameter("linea_destino_desc"));

			
			logger.debug("Parametros para la replicacion. PlanOrigen: " + planOrigen + " || PlanDestino: " + planDestino + 
					" || LineaOrigen: " + lineaOrigen + " || LineaDestino: " + lineaDestino );
			
			cultivosEntidadesManager.replicarPlanLineaCultivos(planOrigen, lineaOrigen, planDestino, lineaDestino);
												
			////// Insertar en el historico
			
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

			// obtene una lista de cultivos entidades creada, ya que solo se crean cuando no existe el plan/linea destino, las creadas serán precisamente esas.
			CultivosEntidades searchObj = new CultivosEntidades();
			searchObj.getLinea().setCodplan(planDestino);
			searchObj.getLinea().setCodlinea(lineaDestino);
			List<CultivosEntidades> listcultivosEntidades = cultivosEntidadesManager.listComisionesCultivosEntidades(searchObj);

			// registro el alta en el historico de cada una de ellas
			for (CultivosEntidades p: listcultivosEntidades) {
				p.setUsuario(usuario);	
				logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
				cultivosEntidadesHistoricoManager.addResgitroHist(p,ComisionesConstantes.AccionesHistComisionCte.ALTA);
			}
			
			parametros.put(MENSAJE, bundle.getString(MENSAJE_COMISIONES_OK));
			mv = doConsultaParam(request, response, cultivosEntidadesBean);
			
			
		}
		catch (NumberFormatException ignored) {
			logger.error("Los parametros enviados no son válidos.");
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO) + ": Los parametros enviados no son válidos.");
			mv = doConsultaParam(request, response, cultivosEntidadesBean);

		}
		catch (Exception be) {
			logger.error("Se ha producido un error al replicar",be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO) + ": " + be.getMessage());
			mv = doConsultaParam(request, response, cultivosEntidadesBean);
		}
		
		logger.debug("end - doReplicarCultivo");
		
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doConsultarHistorico (HttpServletRequest request, HttpServletResponse response,
			CultivosSubentidades cultivosSubentidadesBean){
		logger.debug("init - doConsultarHistorico");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		ArrayList<CultivosSubentidadesHistorico> listadoHistorico = new ArrayList<CultivosSubentidadesHistorico>();
		try {
			request.getSession().setAttribute(FILTRO_CULTIVOS_SUBENT, cultivosSubentidadesBean);
			
		    listadoHistorico = cultivosSubEntidadesManager.consultaHistorico (cultivosSubentidadesBean.getId());
			parametros.put("listadoHistorico", listadoHistorico);			
			
			parametros.put("entmediadoraH",StringUtils.nullToString(request.getParameter("entmediadoraH")));
			parametros.put("subentmediadoraH",StringUtils.nullToString(request.getParameter("subentmediadoraH")));
			parametros.put("desc_subentmediadoraH",StringUtils.nullToString(request.getParameter("desc_subentmediadoraH")));
			parametros.put("planH",StringUtils.nullToString(request.getParameter("planH")));
			parametros.put("lineaH",StringUtils.nullToString(request.getParameter("lineaH")));
			parametros.put("txt_porcentajeMediadorH",StringUtils.nullToString(request.getParameter("txt_porcentajeMediadorH")));
			
		}catch (Exception be) {
			logger.error("Se ha producido un error al consultar el historico ",be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO));
			mv = doConsulta(request, response, new CultivosSubentidades());
			return mv.addAllObjects(parametros);
		}
		logger.debug("end - doConsultarHistorico");
		return mv = new ModelAndView("moduloComisiones/historicoComisionesCultivos",
				"cultivosSubentidadesBean", cultivosSubentidadesBean).addAllObjects(parametros);
	}
	

	
	public ModelAndView doVerDetalle (HttpServletRequest request, HttpServletResponse response,
			CultivosSubentidades cultivosSubentidadesBean){
		logger.debug("init - doVerDetalle");
		
		List<DetalleComisionEsMediadora> listDetallePct = new ArrayList<DetalleComisionEsMediadora>();
		String codPlan = StringUtils.nullToString(request.getParameter(IDPLAN));
		String ent = StringUtils.nullToString(request.getParameter("ent"));
		String subEnt = StringUtils.nullToString(request.getParameter("subEnt"));
		String lineaseguoId = StringUtils.nullToString(request.getParameter("lineaseguoId"));
		String codLin = StringUtils.nullToString(request.getParameter("codLin"));
		JSONObject resultado = new JSONObject();
		
		try {		
			if (!codPlan.equals("") && !ent.equals("") && !subEnt.equals("") && !lineaseguoId.equals("")
					&& !codLin.equals("")) {
				// Si la línea es la genérica buscamos por todas las lineseguroid para ese plan en parametros generales
				if (codLin.equals(Constants.CODLINEA_GENERICA.toString())){
					List<Linea> lstLineas = cultivosEntidadesManager.getListLineasParamsGenByPlan(codPlan);
					for (Linea lin:lstLineas){
						cultivosEntidadesManager.getListDetallePct (codPlan,new BigDecimal(ent),
								new BigDecimal(subEnt),new BigDecimal(lin.getLineaseguroid().toString()),new BigDecimal(codLin),listDetallePct,lin.getCodlinea());
					}
				}else {	
					cultivosEntidadesManager.getListDetallePct (codPlan,new BigDecimal(ent),
							new BigDecimal(subEnt),new BigDecimal(lineaseguoId),new BigDecimal(codLin),listDetallePct,null);
				}
				resultado.put("listDetallePct", listDetallePct);
			}else {
				resultado.put(ERROR, "Error");
			}
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al doVerDetalle: " + be.getMessage());			
		}
		getWriterJSON(response,resultado);
		
		return null;
	}
	
	
	/**
	 * Método al que se llama para recuperar datos de comisión genéricos de:
	 * - Porcentaje administración (pctrga)
	 * - Porcentaje adquisición (pctadquisicion)
	 * - Porcentaje comisión máxima	(pctgeneralentidad)
	 * - Porcentaje entidad 
	 * - Porcentaje E-S mediadora
	 * @param request
	 * @param response
	 * @param cultivosEntidadesBean
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ModelAndView doParamsGeneralesByIdPolizaYLinea (HttpServletRequest request, HttpServletResponse response){
		
		logger.debug("init - doVerDetalle");
		List<List> listParamSGen 		= new ArrayList<List>();
		String lineaSeguroId 			= StringUtils.nullToString(request.getParameter("lineaSeguroId"));
		String idPoliza 				= StringUtils.nullToString(request.getParameter("idPoliza"));
		
		JSONObject resultado = new JSONObject();
		
		try {		
			if (!lineaSeguroId.equals("")) {
				Poliza pol = polizaManager.getPoliza(new Long(idPoliza));
				
				listParamSGen = cultivosEntidadesManager.getParamsGeneralesByLineaseguroId 
						(Long.parseLong(lineaSeguroId), 
								pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
								pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad());				
				
				
				List<String> listaPorcentajes = cultivosSubEntidadesManager.obtenerPorcentajesEntMedYSubEntMed(pol);
				
					for(int x=0;x<listParamSGen.size();x++){
						if ((listaPorcentajes != null) && listaPorcentajes.size()>1) {
							listParamSGen.get(x).add(listaPorcentajes.get(0));//El de entidad
							listParamSGen.get(x).add(listaPorcentajes.get(1));//El de E-S mediadora
						}else{
							listParamSGen.get(x).add("");//El de entidad
							listParamSGen.get(x).add("");//El de E-S mediadora
						}
						
					}

					calcularPorcentajeRecargo(listParamSGen,pol);
					
					
					JSONArray jsonParamGen = new JSONArray(getJsonParamsGen(listParamSGen));
					JSONArray jsonParamPol = new JSONArray(getJsonPol(pol));					
					String res = jsonParamGen.toString();
					String res2 = jsonParamPol.toString();
					res=res.substring(0, res.length()-1);
					res2=res2.substring(1, res2.length());					
					String resTotal = res +(listParamSGen.size()>0?",":"")+res2;			
					this.getWriterJSON(response, resTotal);				
			}else {
				resultado.put(ERROR, "Error");
				logger.error("lineaSeguroId es vacío");
				getWriterJSON(response,resultado);
			}
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al doParamsGeneralesByIdPolizaYLinea: " + be.getMessage());			
		}
		
		
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void calcularPorcentajeRecargo(List<List> listParamSGen, Poliza pol){
		
		//mismo descuento para todo el colectivo
		//podemos cogerlo de cualquier registro.
		String porcentajeRecargo="0";
		String descuentoRecargoTipo=(pol.getColectivo().gettipoDescRecarg()!=null)?pol.getColectivo().gettipoDescRecarg().toString():"-1";
		String descuentoRecargoPct 	= (pol.getColectivo().getpctDescRecarg()!=null)?pol.getColectivo().getpctDescRecarg().toString():"0";
		//PCT_DESC_RECARG (0 - Descuento, 1 - Recargo)
		if(("0".equals(descuentoRecargoTipo)) || ("1".equals(descuentoRecargoTipo))){
			//Descuento			
			porcentajeRecargo = descuentoRecargoPct;			
		}
		for(int x=0;x<listParamSGen.size();x++){
				listParamSGen.get(x).add(porcentajeRecargo);
				listParamSGen.get(x).add(descuentoRecargoTipo);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List getJsonPol(Poliza pol) throws Exception{
		Set<PolizaPctComisiones> pctComs=pol.getSetPolizaPctComisiones();
		Iterator<PolizaPctComisiones> it=pctComs.iterator();
		ParametrizacionPolizaHelper pph=null;
		List<ParametrizacionPolizaHelper> comisionesPolizas=new ArrayList<ParametrizacionPolizaHelper>();
		while(it.hasNext()){
			pph=new ParametrizacionPolizaHelper();
			PolizaPctComisiones pct=it.next();
			
			
			String descuentoRecargoTipo = (pol.getColectivo().gettipoDescRecarg()!=null)?pol.getColectivo().gettipoDescRecarg().toString():"-1";
			String descuentoRecargoPct 	= (pol.getColectivo().getpctDescRecarg()!=null)?pol.getColectivo().getpctDescRecarg().toString():"0";
			String pctEntidadCom 		= (pct.getPctentidad()!=null)?pct.getPctentidad().toString():"-1";
			String pctESMedCom			= (pct.getPctesmediadora()!=null)?pct.getPctesmediadora().toString():"-1";					
			BigDecimal pctDescuentoElegido	= pct.getPctdescelegido();
			BigDecimal pctRecargoElegido 	= pct.getPctrecarelegido();
			BigDecimal pctElegido			= new BigDecimal("0.00");
			BigDecimal ceroBigDecimal		= new BigDecimal("0.00");
			
			//Suponemos que no pueden ser los dos diferentes de null o cero
			if(pctDescuentoElegido!=null && pctDescuentoElegido.compareTo(ceroBigDecimal)!=0){
				pctElegido = pctDescuentoElegido.negate();
				
			}else{
				if(pctRecargoElegido!=null && pctRecargoElegido.compareTo(ceroBigDecimal)!=0){
					pctElegido = pctRecargoElegido;
				}
			}
			
			pph.setId(pol.getIdpoliza()+"");
			pph.setEstado(pol.getEstadoPoliza().getIdestado().toString());
			pph.setTipo(pol.getTipoReferencia()+"");
			pph.setPlan(pol.getLinea().getCodplan().toString());
			pph.setPctComMax(pct.getPctcommax().toString());
			pph.setPctAdm(pct.getPctadministracion().toString());
			pph.setPctAdq(pct.getPctadquisicion().toString());
			pph.setLinsegId(pol.getLinea().getLineaseguroid().toString());
			pph.setPctEnt(pctEntidadCom);
			pph.setPctESMed(pctESMedCom);
			pph.setDescuentoRecargoTipo(descuentoRecargoTipo);
			pph.setDescuentoRecargoPct(descuentoRecargoPct);
			pph.setDescuentoElegidoPct(pctElegido.toString());
			pph.setGrupoNegocio(pct.getGrupoNegocio());
			pph.setDescGrupoNegocio(cultivosEntidadesManager.getGruposNegocioPorCodigo(pct.getGrupoNegocio()+""));
			comisionesPolizas.add(pph);			
		}
		Collections.sort(comisionesPolizas);
		return comisionesPolizas;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getJsonParamsGen(List<List> listParamSGen){
		ParametrizacionHelper phTemp=null;	
		List listParaJson = new ArrayList();
		for(int x=0;x<listParamSGen.size();x++){
			phTemp=new ParametrizacionHelper();
			phTemp.setPorAdmin((String)(listParamSGen.get(x)).get(0));
			phTemp.setPorAdq((String)(listParamSGen.get(x)).get(1));
			phTemp.setPorComisionMax((String)(listParamSGen.get(x)).get(2));
			phTemp.setCodGrupoNegocio((String)(listParamSGen.get(x)).get(3));
			phTemp.setDescGrupoNecio((String)(listParamSGen.get(x)).get(4));
			phTemp.setPorEnt((String)(listParamSGen.get(x)).get(5));
			phTemp.setPorESMed((String)(listParamSGen.get(x)).get(6));	
			phTemp.setPorcentajeRecargo((String)(listParamSGen.get(x)).get(7));//Puede ser Dcto. o recargo
			phTemp.setTipoDctoRecargo((String)(listParamSGen.get(x)).get(8));
			
			listParaJson.add(phTemp);
		}
		Collections.sort(listParaJson);
		return listParaJson;
	}
	
	public ModelAndView doConsultaParam (HttpServletRequest request, HttpServletResponse response, CultivosEntidades cultivosEntidadesBean) throws Exception{
		logger.debug("init - doConsultaParam");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<CultivosEntidades> listcultivosEntidades = null;
		List<GruposNegocio>listGruposNegocio=null;
		if (cultivosEntidadesBean.getLinea() == null){
			Linea lin = new Linea();
			cultivosEntidadesBean.setLinea(lin);
		}
		String procedencia = StringUtils.nullToString(request.getParameter(PROCEDENCIA));
		if (procedencia.equals("")) {
			procedencia = StringUtils.nullToString(request.getAttribute(PROCEDENCIA));
		}
		if ("historicoComisiones".equalsIgnoreCase(procedencia) || "borrar".equalsIgnoreCase(procedencia)){
			cultivosEntidadesBean = (CultivosEntidades) request.getSession().getAttribute("CultEntFiltro");
			procedencia = "";
		}
		else if (("incidenciasComisiones".equalsIgnoreCase(procedencia) 
				||"incidenciasComisionesUnificadas".equalsIgnoreCase(procedencia)) 
				&& StringUtils.nullToString(request.getParameter("consultando")).equals("") ) {
			//cultivosEntidadesBean = (CultivosEntidades) request.getSession().getAttribute("CultEntFiltro");
			parametros.put(PROCEDENCIA, procedencia);
			cultivosEntidadesBean = new CultivosEntidades();
			String lin =request.getParameter("codLineaCom");
			String plan=request.getParameter("planLineaCom");
			
			Linea linea = new Linea();
			if (lin !=null) 	
				linea.setCodlinea(new BigDecimal(lin));				
			if (plan !=null) 
				linea.setCodplan(new BigDecimal(plan));				
			cultivosEntidadesBean.setLinea(linea);				
			
		}else{
			// guardamos filtro en sesión
			request.getSession().setAttribute("CultEntFiltro", cultivosEntidadesBean);
		}
		
		try {
			listcultivosEntidades = cultivosEntidadesManager.listComisionesCultivosEntidades(cultivosEntidadesBean);
			logger.debug("listcultivosEntidades. Size:" + listcultivosEntidades.size());
			listGruposNegocio=cultivosEntidadesManager.getListGruposDeNegocio();
			parametros.put("listGruposNegocio", listGruposNegocio);
			/*
//			SI NO ESTA DADA DE ALTA PARA LA LINEA GENERICA, MOSTRAMOS UN ALERTA Y PREPARAMOS LOS DATOS NECESARIOS PARA EL ALTA
			logger.debug("Comprobamos si esta dada de alta la linea generica");
			if(!cultivosEntidadesManager.compruebaLinea999()){
				cultivosEntidadesBean = cultivosEntidadesManager.generarComisionLinea999();
				parametros.put("altaLinea999",true);
			}else{
				parametros.put("altaLinea999",false);
			}
			*/
			//marcar todos
			String polizasString = "";
	        if (listcultivosEntidades != null && listcultivosEntidades.size() > 0) {
		    	polizasString = cultivosEntidadesManager.getListPolizasString(listcultivosEntidades);
	        }
	        parametros.put("polizasString", polizasString);
	        parametros.put(PROCEDENCIA, procedencia);
			parametros.put("altaLinea999",false);
			if (request.getParameter(TIPO_FICHERO) != null && request.getParameter(ID_FICHERO) != null )
			{
				parametros.put(ID_FICHERO,request.getParameter(ID_FICHERO));
				parametros.put(TIPO_FICHERO,request.getParameter(TIPO_FICHERO));	
			}
			
			parametros.put("listCE", listcultivosEntidades);
			
			// limpiamos el popup de replicar plan/linea
			parametros.put(LIMPIAR_REPLICAR, "true");
			
			mv = new ModelAndView("moduloComisiones/parametrosComisionesCultivos","CultivosEntidadesBean",cultivosEntidadesBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error",be);
			parametros.put(ALERTA, bundle.getString("mensaje.error.general"));
			mv = doConsultaParam(request, response, new CultivosEntidades());
		}
		logger.debug("end - doConsultaParam");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doGuardarParametrosComisiones (HttpServletRequest request, HttpServletResponse response,CultivosEntidades cultivosEntidadesBean) throws Exception{
		logger.debug("init - doAltaParametrosComisiones");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = null;
		ArrayList<Integer> errList = null;
		try {
			logger.debug("proceso de alta/modificacion de un parametro de comision cultivo");
			errList= cultivosEntidadesManager.validaRegistro(cultivosEntidadesBean);
			if(errList.size()==0){
				usuario = (Usuario) request.getSession().getAttribute(USUARIO);
				cultivosEntidadesBean.setUsuario(usuario);			
				if(cultivosEntidadesBean.getId() == null){
					logger.debug("alta del registro");
					cultivosEntidadesManager.guardarParametrosComisiones(cultivosEntidadesBean);
					logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
					cultivosEntidadesHistoricoManager.addResgitroHist(cultivosEntidadesBean,ComisionesConstantes.AccionesHistComisionCte.ALTA);
					parametros = this.gestionMensajes(errList, bundle.getString("mensaje.comisiones.parametros.alta.OK"));
				}else{
					logger.debug("modificacion del registro");
					cultivosEntidadesManager.guardarParametrosComisiones(cultivosEntidadesBean);
					logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
					cultivosEntidadesHistoricoManager.addResgitroHist(cultivosEntidadesBean,ComisionesConstantes.AccionesHistComisionCte.MODIFICACION);
					parametros = this.gestionMensajes(errList, bundle.getString("mensaje.comisiones.parametros.modificacion.OK"));
					
				}
				parametros.put(ACTIVAR_MODO_MODIFICAR, true);
				
			}else{
				
				parametros = this.gestionMensajes(errList, bundle.getString("mensaje.comisiones.parametros.alta.OK"));
				if(errList.size() > 0 && null!=cultivosEntidadesBean.getId()) {//modificacion
					cultivosEntidadesBean = cultivosEntidadesManager.getCultivosEntidades(cultivosEntidadesBean.getId());
					parametros.put(ACTIVAR_MODO_MODIFICAR, true);
				}
				parametros.put(ALERTA, bundle.getString("mensaje.comisiones.distMed.validacion.KO"));				
			}
			if (null == cultivosEntidadesBean.getSubentidadMediadora()){
				cultivosEntidadesBean.setSubentidadMediadora(new SubentidadMediadora());
			}
			mv = doConsultaParam(request, response, cultivosEntidadesBean);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de alta/editar un parametro de comisiones por cultivo: " + be.getMessage());
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO));
			mv = doConsultaParam(request, response, new CultivosEntidades());
		}
		logger.debug("end - doAltaParametrosComisiones");
		return mv.addAllObjects(parametros);
	}
	
	
	
	
	@SuppressWarnings("deprecation")
	public ModelAndView doCambioMasivo (HttpServletRequest request, HttpServletResponse response,CultivosEntidades cultivosEntidadesBean) throws Exception{
		logger.debug("init - doCambioMasivo");
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);	
		Map<String, Object> parametros = new HashMap<String, Object>();
		logger.debug("init - ComisionesCultivosController - cambioMasivo");
		String listaIds = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm")); 
		ModelAndView mv = null;
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
		try {
			if (listaIds.length()>0 && listaIds.substring(0,1).equals(";"))				
				listaIds = listaIds.substring(1,listaIds.length());
			
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			parametros = cultivosEntidadesManager.cambioMasivo(listaIds,cultivosEntidadesBean,usuario);
			// recogemos los parametros del filtro inicial
						String planFiltro = StringUtils.nullToString(request.getParameter("planFiltro")); 
						String lineaFiltro = StringUtils.nullToString(request.getParameter("lineaFiltro")); 
						String desc_lineaFiltro = StringUtils.nullToString(request.getParameter("desc_lineaFiltro")); 
						String pctComFiltro = StringUtils.nullToString(request.getParameter("pctComFiltro")); 
						String pctAdqFiltro = StringUtils.nullToString(request.getParameter("pctAdqFiltro")); 
						String pctAdmFiltro = StringUtils.nullToString(request.getParameter("pctAdmFiltro"));
						String fecEfectoFiltro = StringUtils.nullToString(request.getParameter("fecEfectoFiltro"));
						String entmediadoraFiltro = StringUtils.nullToString(request.getParameter("entmediadoraFiltro"));
						String subentmediadoraFiltro = StringUtils.nullToString(request.getParameter("subentmediadoraFiltro"));
						String grupoNegocioFiltro = StringUtils.nullToString(request.getParameter("grupoNegocioFiltro"));
						if (planFiltro != null && !planFiltro.equals("")){
							cultivosEntidadesBean.getLinea().setCodplan(new BigDecimal(planFiltro));
						}
						if (lineaFiltro != null && !lineaFiltro.equals("")){
							cultivosEntidadesBean.getLinea().setCodlinea(new BigDecimal(lineaFiltro));
						}
						if (desc_lineaFiltro != null && !desc_lineaFiltro.equals("")){
							cultivosEntidadesBean.getLinea().setNomlinea(desc_lineaFiltro);
						}
						cultivosEntidadesBean.setPctgeneralentidad(null);
						if (pctComFiltro != null && !pctComFiltro.equals("")){
							cultivosEntidadesBean.setPctgeneralentidad(new BigDecimal(pctComFiltro));
						}
						cultivosEntidadesBean.setPctadquisicion(null);
						if (pctAdqFiltro != null && !pctAdqFiltro.equals("")){
							cultivosEntidadesBean.setPctadquisicion(new BigDecimal(pctAdqFiltro));
						}
						cultivosEntidadesBean.setPctadministracion(null);
						if (pctAdmFiltro != null && !pctAdmFiltro.equals("")){
							cultivosEntidadesBean.setPctadministracion(new BigDecimal(pctAdmFiltro));
						}
						cultivosEntidadesBean.setFechaEfecto(null);
						if (fecEfectoFiltro != null && !fecEfectoFiltro.equals("")){
							cultivosEntidadesBean.setFechaEfecto((new Date (fecEfectoFiltro)));
						}
						if (entmediadoraFiltro != null && !entmediadoraFiltro.equals("")){
							cultivosEntidadesBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal(entmediadoraFiltro));
						}
						if (subentmediadoraFiltro != null && !subentmediadoraFiltro.equals("")){
							cultivosEntidadesBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(subentmediadoraFiltro));
						}
						if (grupoNegocioFiltro != null && !grupoNegocioFiltro.equals("")){
							cultivosEntidadesBean.getGrupoNegocio().setGrupoNegocio(grupoNegocioFiltro.charAt(0));
						}
			
			if (parametros.get(ALERTA)!= null) {
				parametros.put(ALERTA, "Error en el cambio masivo."+" "+
						parametros.get(ALERTA));
			}else{
				parametros = this.gestionMensajes(errList, bundle.getString("mensaje.comisiones.parametros.modificacion.OK"));
			}
			
			
	
			mv = doConsultaParam(request, response, cultivosEntidadesBean).addAllObjects(parametros);
			
			logger.debug("end - ComisionesCultivosController - doCambioMasivo");
			
		} catch (Exception e) {
			logger.debug("Error al ejecutar el Cambio Masivo ", e);
		}
		return mv;
	}
	
	public ModelAndView doBorrarParametrosComisiones(HttpServletRequest request, HttpServletResponse response,CultivosEntidades cultivosEntidadesBean) throws Exception{
		logger.debug("init - doBorrarParametrosComisiones");
		Map<String, Object> parametros = new HashMap<String, Object>();
		Usuario usuario = new Usuario();
		ModelAndView mv = null;
		Long id = null;
		Date fEfecto = new Date();
		try {
			id = new Long(StringUtils.nullToString(request.getParameter("idBaja")));
			String fecEfecto = StringUtils.nullToString(request.getParameter("fFec"));
			if (!fecEfecto.equals("")){
				SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
				fEfecto = formatter.parse(fecEfecto);	
			}
			logger.debug("Recuperamos el parametro de comisiones cultivo que se va a eliminar. ID=" + id);
			CultivosEntidades cultivosEntidades = cultivosEntidadesManager.getCultivosEntidades(id);
			cultivosEntidades.setFechaEfecto(fEfecto);
			logger.debug("Comprobamos si no hay comisiones asociadas a algun mediador");
			if(!cultivosEntidadesManager.hayComisionesAsociadas(cultivosEntidades)){
				logger.debug("borrado del registro");
				cultivosEntidadesManager.borrarParametrosComisiones(cultivosEntidades);
				parametros.put(MENSAJE, bundle.getString("mensaje.comisiones.parametros.baja.OK"));
			}else{
				parametros.put(ALERTA, bundle.getString("mensaje.comisiones.parametros.baja.KO"));
			}
			
//			INTRODUCIMOS UN REGISTRO NUEVO EN EL HISTORICO
			logger.debug(ALTA_DEL_REGISTRO_EN_EL_HISTORICO);
			usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			cultivosEntidades.setUsuario(usuario);
			cultivosEntidadesHistoricoManager.addResgitroHist(cultivosEntidades,ComisionesConstantes.AccionesHistComisionCte.BAJA);
			
			request.setAttribute(PROCEDENCIA, "borrar");
			mv = doConsultaParam(request, response, new CultivosEntidades());
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de baja un parametro de comisiones por cultivo",be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_COMISIONES_KO));
			mv = doConsultaParam(request, response, new CultivosEntidades());
		}
		logger.debug("end - doBorrarParametrosComisiones");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doActualizaPlanLinea(HttpServletRequest request, HttpServletResponse response,CultivosSubentidades cultivosSubentidadesBean){
		logger.debug("init - doActualizaPlan");
		String idplan = StringUtils.nullToString(request.getParameter(IDPLAN));
		String idlinea = StringUtils.nullToString(request.getParameter("idlinea"));
		JSONObject resultado = new JSONObject();
		CultivosEntidades ent=null;
		try {		
			// Valores por defecto
			if (StringUtils.nullToString(idlinea).equals("")){
				idlinea = "999";
			}
			ent=cultivosEntidadesManager.getCultivoEntidadByPlanLinea(new BigDecimal(idplan), new BigDecimal(idlinea));
			if(ent!=null){
				resultado.put("plan",ent.getLinea().getCodplan().toString());				
				resultado.put("linea",ent.getLinea().getCodlinea().toString());	
				resultado.put("pctentidades",ent.getPctgeneralentidad().toString());		
				resultado.put("pctrga",ent.getPctrga().toString());		
			}
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al doActualizaPlan: " + be.getMessage());			
		} catch (JSONException e) {
			logger.error("Se ha producido un error al doActualizaPlan: " + e.getMessage());		
		}	
		logger.debug("fin - doActualizaPlan");
		getWriterJSON(response,resultado);
		return null;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws FileNotFoundException 
	 */
	public ModelAndView doExportarExcel(HttpServletRequest request, HttpServletResponse response, CultivosEntidades cultivosEntidadesBean) throws FileNotFoundException {
		
		logger.debug("ComisionesCultivosController - doExportarExcel - init");
				
		List<CultivosEntidades> listcultivosEntidades = null;
		
		try {
			
			listcultivosEntidades = cultivosEntidadesManager.listComisionesCultivosEntidades(cultivosEntidadesBean);
			generarFicheroExcel(response, listcultivosEntidades);
			
		} catch (BusinessException e) {
			e.printStackTrace();
		} 
		
		logger.debug("ComisionesCultivosController - doExportarExcel - end");

		return null;
		
	}
	
	private void generarFicheroExcel(HttpServletResponse response, List<CultivosEntidades> listCultivosEntidades) {
		
		logger.debug("ComisionesCultivosController - generarFicheroExcel - init");

		response.setContentType(APPLICATION_VND_MS_EXCEL);
        response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_LISTADO_CULTIVOS_ENTIDADES_XLS);
        
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(LISTADO_CULTIVO_ENTIDADES); 
		sheet = wb.getSheetAt(0);								
		
		insertarCabeceras(wb);
		insertarFilas(wb, listCultivosEntidades);
		
		ExcelUtils.autoajustarColumnas(sheet, 10);	
		
		try {
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.debug("ComisionesCultivosController - generarFicheroExcel - end");

	}

	private void insertarFilas(HSSFWorkbook wb, List<CultivosEntidades> listCultivosEntidades ) {
				
		logger.debug("ComisionesCultivosController - insertarFilas - init");

		HSSFCellStyle estiloFila = ExcelUtils.getEstiloFila(wb);
		HSSFCellStyle estiloFilaNumeroEntero = ExcelUtils.getEstiloFilaNumeroEntero(wb);
		HSSFCellStyle estiloFilaPorcentaje = ExcelUtils.getEstiloFilaPorcentaje(wb);

		HSSFCell cell;
		
		for (int i=0; i<listCultivosEntidades.size(); i++){
			
			HSSFRow row = wb.getSheetAt(wb.getActiveSheetIndex()).createRow(i+1); 

			cell = row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(estiloFilaNumeroEntero);
			if ((null!=listCultivosEntidades.get(i).getLinea().getCodplan())) {			
				cell.setCellValue(listCultivosEntidades.get(i).getLinea().getCodplan().doubleValue());					
			}
			
			cell = row.createCell(1);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(estiloFilaNumeroEntero);
			if ((null!=listCultivosEntidades.get(i).getLinea().getCodlinea())) {			
				cell.setCellValue(listCultivosEntidades.get(i).getLinea().getCodlinea().doubleValue());					
			}
			
			cell = row.createCell(2);
			cell.setCellStyle(estiloFila);
			
			String entMed = "";
			String subEntMed = "";
			if ( (null!=listCultivosEntidades.get(i).getSubentidadMediadora()) && (null!=listCultivosEntidades.get(i).getSubentidadMediadora().getId() ) ) {
				if(listCultivosEntidades.get(i).getSubentidadMediadora().getId().getCodentidad()!=null){
					if(listCultivosEntidades.get(i).getSubentidadMediadora().getId().getCodsubentidad()!=null){
						entMed= listCultivosEntidades.get(i).getSubentidadMediadora().getId().getCodentidad().toString();
						subEntMed=listCultivosEntidades.get(i).getSubentidadMediadora().getId().getCodsubentidad().toString();
						cell.setCellValue(entMed + "-" + subEntMed);
					}
				}		
				
			} 
			
			cell = row.createCell(3);
			cell.setCellStyle(estiloFila); 
			if ( null!=listCultivosEntidades.get(i).getGrupoNegocio()&& null!=listCultivosEntidades.get(i).getGrupoNegocio().getGrupoNegocio()) { 
				String grupoNegocio=listCultivosEntidades.get(i).getGrupoNegocio().getGrupoNegocio().toString();
				cell.setCellValue(listCultivosEntidades.get(i).getGrupoNegocio().getDescripcion());					
			}
			
			cell = row.createCell(4);
			cell.setCellStyle(estiloFilaPorcentaje); 
			if ( null!=listCultivosEntidades.get(i).getPctgeneralentidad()) { 
				cell.setCellValue(listCultivosEntidades.get(i).getPctgeneralentidad().setScale(2,BigDecimal.ROUND_DOWN)+"%");					
			}
			
			cell = row.createCell(5);
			cell.setCellStyle(estiloFilaPorcentaje); 
			if ( null!=listCultivosEntidades.get(i).getPctadministracion()) { 
				cell.setCellValue(listCultivosEntidades.get(i).getPctadministracion().setScale(2,BigDecimal.ROUND_DOWN)+"%");					
			}
			
			cell = row.createCell(6);
			cell.setCellStyle(estiloFilaPorcentaje); 
			if ( null!=listCultivosEntidades.get(i).getPctadquisicion() ) { 
				cell.setCellValue(listCultivosEntidades.get(i).getPctadquisicion().setScale(2,BigDecimal.ROUND_DOWN)+"%");					
			}
			
			cell = row.createCell(7);
			cell.setCellStyle(estiloFila); 
			if ( null!=listCultivosEntidades.get(i).getFechaEfecto() ) { 
				SimpleDateFormat formatoFecha = new SimpleDateFormat(DD_MM_YYYY);
				String strFecha = formatoFecha.format(listCultivosEntidades.get(i).getFechaEfecto());
				cell.setCellValue(strFecha);		
			}
			
			cell = row.createCell(8);
			cell.setCellStyle(estiloFila); 
			if ( null!=listCultivosEntidades.get(i).getFechaBaja() ) { 
				SimpleDateFormat formatoFecha = new SimpleDateFormat(DD_MM_YYYY);
				String strFecha = formatoFecha.format(listCultivosEntidades.get(i).getFechaBaja());
				cell.setCellValue(strFecha);	
			}
			
		}		
		
		logger.debug("ComisionesCultivosController - insertarFilas - end");

	}

	private void insertarCabeceras(HSSFWorkbook wb) {
		
		logger.debug("ComisionesCultivosController - insertarCabeceras - init");

		HSSFCellStyle estiloCabecera = ExcelUtils.getEstiloCabecera(wb);
		HSSFRow cabecera = wb.getSheetAt(wb.getActiveSheetIndex()).createRow(0);
		HSSFCell cell;
		
		for (int i=0;i<titulosColumnas.size();i++) {
			cell = cabecera.createCell(i);
			cell.setCellStyle(estiloCabecera);
			cell.setCellValue(new HSSFRichTextString(titulosColumnas.get(i)));
		}
		
		logger.debug("ComisionesCultivosController - insertarCabeceras - end");
	}


	/**
	 * Metodo que genera los mensajes de errores de validacion que se mostrarÃ¡n en la jsp o el mensaje de todo correcto
	 * @param errList
	 * @param msjOK
	 * @return
	 */
	private Map<String, Object> gestionMensajes(ArrayList<Integer> errList,String msjOK){
		Map<String, Object> parametros = new HashMap<String, Object>();
		ArrayList<String> erroresWeb = new ArrayList<String>();
		
//		MENSAJES DE ERRORES DE VALIDACION
		if(errList.size() > 0){
			for(Integer error : errList){
				switch (error) {
				case 1:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.linea"));
						break;
				case 2:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.entSubent"));
						break;
				case 3:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.replicar.plan.origen"));
						break;
				case 4:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.replicar.plan.destino"));
						break;
				case 5:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.replicar.plan.destino.duplicado"));
						break;				
				case 6:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.replicar.plan.noreplicado"));
						break;
				case 7:
						erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.replicar.Destino"));
						break;
				case 8:
					erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.existeReg"));
					break;
				case 9:
					erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.grupoNegocio"));
					break;
				case 10:
					erroresWeb.add(bundle.getString("mensaje.comisiones.distMed.validacion.Exception"));
					break;
				default: 
					break;
				}
			}
			parametros.put("alerta2", erroresWeb);
		}else{
			parametros.put(MENSAJE, msjOK);
		}
		
		return parametros;
	}
	
	
	
	public void setCultivosEntidadesManager(CultivosEntidadesManager cultivosEntidadesManager) {
		this.cultivosEntidadesManager = cultivosEntidadesManager;
	}

	public void setCultivosSubEntidadesManager(CultivosSubEntidadesManager cultivosSubEntidadesManager) {
		this.cultivosSubEntidadesManager = cultivosSubEntidadesManager;
	}

	public void setCultivosSubEntidadesHistoricoManager(CultivosSubEntidadesHistoricoManager cultivosSubEntidadesHistoricoManager) {
		this.cultivosSubEntidadesHistoricoManager = cultivosSubEntidadesHistoricoManager;
	}

	public void setCultivosEntidadesHistoricoManager(CultivosEntidadesHistoricoManager cultivosEntidadesHistoricoManager) {
		this.cultivosEntidadesHistoricoManager = cultivosEntidadesHistoricoManager;
	}
	
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
}