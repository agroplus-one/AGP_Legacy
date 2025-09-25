package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.decorators.ModelTableDecoratorParcelasModificadas;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaComplementariaManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.PolizaComplementariaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.report.anexoMod.BeanParcelaAnexo;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class DeclaracionesModificacionPolizaComplementariaController extends BaseMultiActionController{
	
	// Constantes
	private static final String PARCELAS_ANEXO_CPL_INFO = "parcelasAnexoCplInfo";
	private static final String MENSAJE_POLIZA_COMPLEMENTARIA_MODIFICACION_KO = "mensaje.poliza.complementaria.modificacion.KO";
	private static final String MENSAJE_MODIFICACION_DEFINITIVO_KO = "mensaje.modificacion.definitivo.KO";
	private static final String MENSAJE_ALTA_KO = "mensaje.alta.KO";
	private static final String MENSAJE_ERROR_GENERAL = "mensaje.error.general";
	private static final String ALERTA = "alerta";
	private static final String CAPITAL_ASEGURADO_BEAN = "capitalAseguradoBean";
	private static final String VIENE_DE_LISTADO_ANEXOS_MOD = "vieneDeListadoAnexosMod";
	private static final String INCREMENTO_OK = "incrementoOK";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String ID_POLIZA = "idPoliza";
	private static final String MENSAJE = "mensaje";
	private static final String ID_ANEXO2 = "idAnexo: ";
	private static final String ID_ANEXO = "idAnexo";
	private static final String USUARIO = "usuario";
	private static final String FEC_INICIO_CONTRAT = "fechaInicioContratacion";
	
	private static final Log logger = LogFactory.getLog(DeclaracionesModificacionPolizaComplementariaController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private DeclaracionesModificacionPolizaComplementariaManager modificacionPolizaComplementariaManager;
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private PolizaComplementariaManager polizaComplementariaManager;
	private PolizaManager polizaManager;
	
	private DeclaracionesModificacionPolizaController declaracionesModificacionPolizaController;
	private AnexoModificacionManager anexoModificacionManager;
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean) {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<CapitalAsegurado> listCapAseg = null;
		
		List<CapitalAsegurado> listCapAsegFinal = null;
		
		AnexoModificacion anexo = null;
		Long idAnexo = null;
		StringBuilder capitales = new StringBuilder();
		StringBuilder incrementos = new StringBuilder();
		//DAA 12/09/2013
		boolean tieneParcelas = false;
		
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
			if(capitalAseguradoBean.getParcela().getAnexoModificacion().getId() != null){
				idAnexo = capitalAseguradoBean.getParcela().getAnexoModificacion().getId();
			}else if(!StringUtils.nullToString(request.getParameter(ID_ANEXO)).equals("")){
				idAnexo = new Long(request.getParameter(ID_ANEXO));
			}else if(!StringUtils.nullToString(request.getAttribute(ID_ANEXO)).equals("")){
				idAnexo = (Long)(request.getAttribute(ID_ANEXO));
			}
			logger.debug(ID_ANEXO2 + idAnexo);
			anexo = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);
			
			/* Pet. 78691 ** MODIF TAM (17.12.2021) ** Inicio */
			/* Obtenemos el parametro nuevo de busqueda de S. Cultivo */
			String sistCultivo = StringUtils.nullToString(request.getParameter("sistemaCultivo"));
			/* Pet. 78691 ** MODIF TAM (17.12.2021) ** Inicio */
			
			
			if (anexo.getCupon() != null && anexo.getCupon().getId() != null){
				// Si el anexo es de SW, no se hace el cambio de estado 
				// se comprueba si esta caducado, si es asi se cambia el estado a caducado
				estado = null;
				Boolean isCaducado = declaracionesModificacionPolizaManager.isAnexoCaducado(anexo);
				if (isCaducado) {							
					anexo.getCupon().setEstadoCupon(declaracionesModificacionPolizaManager.getEstadoCupon());
					anexo = declaracionesModificacionPolizaManager.saveAnexoModificacion(anexo,usuario.getCodusuario(),estado,false);
					anexo.setCupon(null);
					parametros.put(MENSAJE, bundle.getString("mensaje.anexo.cupon.caducado"));
					parametros.put(ID_POLIZA, anexo.getPoliza().getIdpoliza());
					//Redireccionar
					return new ModelAndView("redirect:/declaracionesModificacionPoliza.html").addAllObjects(parametros);
				}
			}
			
			if(anexo.getParcelas().size() > 0){
				tieneParcelas = true;
			}

			BigDecimal idEstado = anexo.getEstado().getIdestado();
			if(idEstado.equals(Constants.ANEXO_MODIF_ESTADO_ENVIADO) || 
					idEstado.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)) { 
				parametros.put(MODO_LECTURA, "true");
			}
			else{
				parametros.put(MODO_LECTURA, "false");
				
				// Si el anexo es de SW, no se hace el cambio de estado
				if (anexo.getCupon() != null && anexo.getCupon().getId() != null){
					estado = null;
				}
				
				declaracionesModificacionPolizaManager.saveAnexoModificacion(anexo,usuario.getCodusuario(),estado,false);
			}
			
			capitalAseguradoBean.getParcela().setAnexoModificacion(anexo);
			
			String idCupon = "";
			if (anexo.getCupon() != null){
				idCupon = StringUtils.nullToString(anexo.getCupon().getId());
				if (StringUtils.nullToString(idCupon).equals("")){
					idCupon = StringUtils.nullToString(request.getParameter("idCupon"));
				}
			}
			
//			COPIAMOS LAS PARCELAS DE LA POLIZA COMPLEMENTARIA A LAS TABLAS DE ANEXO SI NO FUERON CARGADAS ANTES
			if(!tieneParcelas){
				if (!StringUtils.nullToString(idCupon).equals("")){
					modificacionPolizaComplementariaManager.copiarParcelasFromPolizaActualizada(anexo.getId(), 
							anexo.getCupon().getIdcupon(), anexo.getPoliza().getLinea().getLineaseguroid());
				}
				else{
					modificacionPolizaComplementariaManager.copiarParcelasFromPolizaOrCopy(anexo.getId());
				}
			}
			
//			LISTADO DE CAPITALES ASEGURADOS DE LA POLIZA COMPLEMENTARIA
			listCapAsegFinal = modificacionPolizaComplementariaManager.getCapitalesAsegPolCpl(capitalAseguradoBean);
			
			/* Pet. 78691 ** MODIF TAM (21/12/2021) ** Inicio*/
			listCapAseg= modificacionPolizaComplementariaManager.getParcelasAnxCplFiltradas(listCapAsegFinal, sistCultivo);

			logger.debug("listado de capitales asegurados. Size: " + listCapAseg.size());
//			LISTADO DE CHECKS QUE VIENE CHECKEADO DE LA BBDD
			modificacionPolizaComplementariaManager.getListas(listCapAseg,capitales,incrementos);
			//TMR 13-11-2012 Guarda la lista completa de ids de capitales asegurados para los checks
			CapitalAsegurado capital = null;
			String listaIdCapAseg = "";
			for(int i = 0; i < listCapAseg.size(); i++) {
				capital = listCapAseg.get(i); 
			
				listaIdCapAseg += (listaIdCapAseg.length() == 0) ? (capital.getId()) : ("," + capital.getId());
			}
			
			/* Pet. 78691 ** MODIF TAM (22.12.2021) ** Inicio */
			parametros.put("sist_cultivo", 	  sistCultivo);
			if (!"".equals(StringUtils.nullToString(sistCultivo))) {
				/* obtenemos la descripciÛn del cultivo*/
				String desc_sistCultivo = modificacionPolizaComplementariaManager.obtenerDescSistCultivo(sistCultivo);
				parametros.put("des_sist_cultivo", desc_sistCultivo);
			}
			
			parametros.put("listaIdCapAseg", listaIdCapAseg );
			parametros.put(INCREMENTO_OK,StringUtils.nullToString(request.getParameter(INCREMENTO_OK)));
			
			String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
			parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
			
			parametros.put("listCapAseg", listCapAseg);
			parametros.put("capitalesAlta", capitales.toString());
			parametros.put("incrementosAlta", incrementos.toString());
			parametros.put(ID_ANEXO, idAnexo);
			
			String fechaInicioContratacion = request.getParameter(FEC_INICIO_CONTRAT);
			if (StringUtils.isNullOrEmpty(fechaInicioContratacion)) {
				parametros.put(FEC_INICIO_CONTRAT, capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getLinea().getFechaInicioContratacion());
			} else {
				parametros.put(FEC_INICIO_CONTRAT, fechaInicioContratacion);
			}
			
			if (!StringUtils.nullToString(idCupon).equals("")){
				mv = new ModelAndView("moduloUtilidades/modificacionesPolizaComplementaria/parcelasAnexoPolizaComplementariaSw",CAPITAL_ASEGURADO_BEAN, capitalAseguradoBean).addAllObjects(parametros);
			}
			else{
				mv = new ModelAndView("moduloUtilidades/modificacionesPolizaComplementaria/parcelasAnexoPolizaComplementaria",CAPITAL_ASEGURADO_BEAN, capitalAseguradoBean).addAllObjects(parametros);
			}
		
		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante la consulta del anexo de la poliza complementaria", be);
			request.setAttribute(ID_POLIZA, capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getIdpoliza());
			request.setAttribute(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexo);
		} catch (DAOException e) {
			logger.error("Se ha producido un error al copiar las parcelas de la copy en la poliza complementaria", e);
			request.setAttribute(ID_POLIZA, capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getIdpoliza());
			request.setAttribute(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexo);
		} catch (Exception e) {
			request.setAttribute(ID_POLIZA, capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getIdpoliza());
			request.setAttribute(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexo);
		}	
		logger.debug("end - doConsulta");
		return mv;
	}
	/**
	 * Alta de un Anexo de Modificacion de una poliza cpl
	 * TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico (historicoEstadosManager.insertaEstado ) 
	 * @author U029769 28/06/2013
	 * @param request
	 * @param response
	 * @param anexoModificacion
	 * @return ModelAndView
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion)throws Exception{
		logger.debug("init - doAlta");
		ModelAndView mv = null;
		
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
		Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
		String mensaje = "";
		
		try {
			logger.debug("idPoliza: " + idPoliza);
			//ALTA DEL ANEXO DE MODIFICACION DE LA POLIZA COMPLEMENTARIA
			anexoModificacion.getPoliza().setIdpoliza(new Long(idPoliza));
			
			//Establezco los valores fijos en el anexo
			if (anexoModificacion.getCupon() != null && !StringUtils.nullToString(anexoModificacion.getCupon().getIdcupon()).equals("")){
				anexoModificacion.setTipoEnvio(Constants.ANEXO_MODIF_TIPO_ENVIO_SW);
			}
			else{
				anexoModificacion.setTipoEnvio(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP);
				anexoModificacion.setCupon(null);
			}
			anexoModificacion.setFechaAlta(new Date());
			anexoModificacion.setUsuarioAlta(usuario.getCodusuario());

			anexoModificacion = declaracionesModificacionPolizaManager.altaAnexoModificacion(anexoModificacion,realPath,usuario.getCodusuario(),estado,true);
			logger.debug("idAnexo: " + anexoModificacion.getId());
			
			if(anexoModificacion.getId() == null){
				mensaje = bundle.getString("mensaje.anexo.complementario.estado.borradorODefinitivo.KO");
				mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexoModificacion);
			}else{
				CapitalAsegurado capital = new CapitalAsegurado();
				capital.getParcela().setAnexoModificacion(anexoModificacion);
				
				mv = new ModelAndView("forward:/declaracionesModificacionPolizaComplementaria.html?method=doConsulta&idAnexo="+anexoModificacion.getId());
			}
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al dar de alta el anexo de la poliza complementaria", be);
			mensaje = bundle.getString(MENSAJE_ALTA_KO);
			mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexoModificacion);
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Se ha producido un error al validar el xml del anexo de modificaci√≥n");
			mensaje = bundle.getString(MENSAJE_ALTA_KO+" "+ e.getMessage());
			mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexoModificacion);
		} catch (Exception e) {
			logger.error("Se ha producido un error al validar el xml del anexo de modificaci√≥n");
			mensaje = bundle.getString(MENSAJE_ALTA_KO+" "+ e.getMessage());
			mv = this.declaracionesModificacionPolizaController.doConsulta(request, response, anexoModificacion);
		}
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put(ID_POLIZA, idPoliza);
		parametros.put(ALERTA, mensaje);
		
		logger.debug("end - doAlta");
		return mv.addAllObjects(parametros);
	}
	
	
	public ModelAndView doPasarDefinitiva(HttpServletRequest request,HttpServletResponse response, AnexoModificacion anexoModificacion) {
		Long idAnexo = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
		
		if(anexoModificacion.getId() != null){
			idAnexo = anexoModificacion.getId();
		}else if(!StringUtils.nullToString(request.getParameter(ID_ANEXO)).equals("")){
			idAnexo = new Long(request.getParameter(ID_ANEXO));
		}
		logger.debug(ID_ANEXO2 + idAnexo);
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		logger.debug("idPoliza: " + idPoliza);
		parametros.put(ID_POLIZA, idPoliza);

		try {
			modificacionPolizaComplementariaManager.pasarDefinitiva(idAnexo, usuario.getCodusuario());
			parametros.put(MENSAJE, bundle.getString("mensaje.modificacion.definitivo.OK"));
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al pasar a definitivo el anexo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_MODIFICACION_DEFINITIVO_KO));
		} catch (DAOException e) {
			logger.error("Se ha producido un error de acceso a datos al pasar a definitivo el anexo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_MODIFICACION_DEFINITIVO_KO));
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Se ha producido un error de validaci√≥n al pasar a definitivo el anexo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_MODIFICACION_DEFINITIVO_KO));
		} catch (Exception e) {
			logger.error("Se ha producido un error de validacion al pasar a definitivo el anexo", e);
			parametros.put(ALERTA, bundle.getString(MENSAJE_MODIFICACION_DEFINITIVO_KO));
		}
		
		
		String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
		if ("true".equals(vieneDeListadoAnexosMod)) {
			parametros.put("volver", true);
			return new ModelAndView("redirect:/anexoModificacionUtilidades.run").addAllObjects(parametros);
		}else{
			return new ModelAndView("redirect:/declaracionesModificacionPoliza.html").addAllObjects(parametros);
		}
	}
	
	public ModelAndView doVisualiza(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean) {
		logger.debug("init - doVisualiza");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<CapitalAsegurado> listCapAseg = null;
		List<CapitalAsegurado> listCapAsegFinal = null;
		
		AnexoModificacion anexo = null;
		Long idAnexo = null;
		StringBuilder capitales = new StringBuilder();
		StringBuilder incrementos = new StringBuilder();
		boolean mostrarImportes = false;
		
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
			
			if(capitalAseguradoBean.getParcela().getAnexoModificacion().getId() != null){
				idAnexo = capitalAseguradoBean.getParcela().getAnexoModificacion().getId();
			}
			else if(!StringUtils.nullToString(request.getParameter(ID_ANEXO)).equals("")){
				idAnexo = new Long(request.getParameter(ID_ANEXO));
			}
			else if(!StringUtils.nullToString(request.getAttribute(ID_ANEXO)).equals("")){
				idAnexo = (Long)(request.getAttribute(ID_ANEXO));
			}
			
			/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto N∫ 2 */
			/* Obtenemos el parametro nuevo de busqueda de S. Cultivo */
			String sistCultivo = StringUtils.nullToString(request.getParameter("sistemaCultivo"));
			/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto N∫ 2 */

			
			logger.debug(ID_ANEXO2 + idAnexo);
			anexo = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);
			
			int idEstado = anexo.getEstado().getIdestado().intValue();
			
			if (anexo.getCupon()!=null) {
				if(idEstado == 2 || idEstado == 3) { // si no es editable				
					parametros.put(MODO_LECTURA, "true");
				}
				if(idEstado == 1 || idEstado == 4 || idEstado == 5){ // si es editable
					parametros.put(MODO_LECTURA, "false");
					Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);					
					declaracionesModificacionPolizaManager.saveAnexoModificacion(anexo,usuario.getCodusuario(),estado,false);
				}
			}
			if (anexo.getCupon()!=null && anexo.getCupon().getEstadoCupon()!= null) {
				if (Constants.AM_CUPON_ESTADO_CADUCADO.equals(anexo.getCupon().getEstadoCupon().getId())){
					parametros.put(MODO_LECTURA, "true");
				}
				
				// Si el cupÛn est· en estado 'Confirmado-Aplicado' se muestra en la pantalla del botÛn 'Importes'
				mostrarImportes = Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(anexo.getCupon().getEstadoCupon().getId()) ||
								  Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(anexo.getCupon().getEstadoCupon().getId());
			}
			capitalAseguradoBean.getParcela().setAnexoModificacion(anexo);
			
//			LISTADO DE CAPITALES ASEGURADOS DE LA POLIZA COMPLEMENTARIA
			listCapAsegFinal = modificacionPolizaComplementariaManager.getCapitalesAsegPolCpl(capitalAseguradoBean);
			
			/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto N∫ 2 */
			listCapAseg= modificacionPolizaComplementariaManager.getParcelasAnxCplFiltradas(listCapAsegFinal, sistCultivo);

			logger.debug("listado de capitales asegurados. Size: " + listCapAseg.size());
//			LISTADO DE CHECKS QUE VIENE CHECKEADO DE LA BBDD
			modificacionPolizaComplementariaManager.getListas(listCapAseg,capitales,incrementos);
			
			/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto N∫ 2 */
			parametros.put("sist_cultivo", 	  sistCultivo);
			if (!"".equals(StringUtils.nullToString(sistCultivo))) {
				/* obtenemos la descripciÛn del cultivo*/
				String desc_sistCultivo = modificacionPolizaComplementariaManager.obtenerDescSistCultivo(sistCultivo);
				parametros.put("des_sist_cultivo", desc_sistCultivo);
			}
			/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto N∫ 2 */
			
			String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
			parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
			
			parametros.put("listCapAseg", listCapAseg);
			parametros.put("capitalesAlta", capitales.toString());
			parametros.put("incrementosAlta", incrementos.toString());
			parametros.put(ID_ANEXO, idAnexo);
			parametros.put("anexoModificacionBean", anexo);
			parametros.put("mostrarImportes", mostrarImportes);
			
			mv = new ModelAndView("moduloUtilidades/modificacionesPolizaComplementaria/parcelasAnexoPolizaCplInfo",CAPITAL_ASEGURADO_BEAN, capitalAseguradoBean).addAllObjects(parametros);
		
		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante la consulta del anexo de la poliza complementaria", be);
			request.setAttribute(ID_POLIZA, capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getIdpoliza());
			request.setAttribute(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = new ModelAndView("forward:/declaracionesModificacionPoliza.html?method=doConsulta");
		}catch (Exception e) {
			logger.error("Se ha producido un error al copiar las parcelas de la copy en la poliza complementaria", e);
			request.setAttribute(ID_POLIZA, capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getIdpoliza());
			request.setAttribute(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
			mv = new ModelAndView("forward:/declaracionesModificacionPoliza.html?method=doConsulta");
		}
		logger.debug("end - doVisualiza");
		return mv;
	}
	
	public ModelAndView doGuardar(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean) {
		logger.debug("init - doGuardar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {
//			RECUPERAMOS LAS OPERACIONES REALIZADAS SOBRE LOS CAPITALES ASEGURADOS Y ACTUALIZAMOS LA BBDD
			modificacionPolizaComplementariaManager.guardarIncrementoParcelas(request,capitalAseguradoBean);
			
			String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
			parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
			
			parametros.put(MENSAJE, bundle.getString("mensaje.poliza.complementaria.modificacion.OK"));
			mv = doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
			
		} catch (BusinessException be) {
			parametros.put(ALERTA, bundle.getString(MENSAJE_POLIZA_COMPLEMENTARIA_MODIFICACION_KO));
			mv = doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		} catch (Exception be) {
			parametros.put(ALERTA, bundle.getString(MENSAJE_POLIZA_COMPLEMENTARIA_MODIFICACION_KO));
			mv = doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		}
		
		logger.debug("end - doGuardar");
		return mv;
	}
	
	public ModelAndView doGuardarAndEnviar(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean) {
		logger.debug("init - doGuardar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {
			// RECUPERAMOS LAS OPERACIONES REALIZADAS SOBRE LOS CAPITALES ASEGURADOS Y ACTUALIZAMOS LA BBDD
			modificacionPolizaComplementariaManager.guardarIncrementoParcelas(request,capitalAseguradoBean);
			
			String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
			parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
			
			AnexoModificacion am = this.declaracionesModificacionPolizaManager
					.getAnexoModifById(capitalAseguradoBean.getParcela().getAnexoModificacion().getId());
			
			// verificamos si tiene modificaciones el anexo complementario
			boolean esValido = anexoModificacionManager.tieneModificacionesAnexo(am, null,
					Constants.MODULO_POLIZA_COMPLEMENTARIO);

			parametros.put("redireccion", request.getParameter("redireccion"));
			
			if(esValido) {
				parametros.put(MENSAJE, bundle.getString("mensaje.poliza.complementaria.modificacion.OK"));
				return new ModelAndView("redirect:/confirmacionModificacion.html?method=doValidarAnexo&idCuponValidar="+am.getCupon().getId()).addAllObjects(parametros);
			}else {
				parametros.put(ALERTA, bundle.getString("alerta.anexo.NoModificaciones"));
				mv = doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
			}
		} catch (BusinessException be) {
			parametros.put(ALERTA, bundle.getString(MENSAJE_POLIZA_COMPLEMENTARIA_MODIFICACION_KO));
			mv = doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		} catch (Exception be) {
			parametros.put(ALERTA, bundle.getString(MENSAJE_POLIZA_COMPLEMENTARIA_MODIFICACION_KO));
			mv = doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		}
		
		logger.debug("end - doGuardar");
		return mv;
	}
	
	public ModelAndView doCoberturas (HttpServletRequest request, HttpServletResponse response,CapitalAsegurado capitalAseguradoBean) throws Exception{
		logger.debug("init - doCoberturas");
		Map<String, Object> parametros = new HashMap<String, Object>();
	
		try {
//			POLIZA COMPLEMENTARIA
			Poliza polizaCpl = polizaManager.getPoliza(capitalAseguradoBean.getParcela().getAnexoModificacion().getPoliza().getIdpoliza());
			logger.debug("idPolizaCPL: " + polizaCpl.getIdpoliza());
			//TMR 02/04/2013
			parametros.put("polizaPpal", polizaCpl.getPolizaPpal());
			parametros.put("polizaCpl", polizaCpl);
			parametros.put(ID_ANEXO, capitalAseguradoBean.getParcela().getAnexoModificacion().getId());
			String parcelasAnexoCplInfo = StringUtils.nullToString(request.getParameter(PARCELAS_ANEXO_CPL_INFO));
			if ("true".equals(parcelasAnexoCplInfo))
				parametros.put(PARCELAS_ANEXO_CPL_INFO, "true");
			
		} catch (Exception be) {
			logger.error("Se ha producido un error inesperado al obtener las coberturas de la poliza ",be);
			parametros.put(ALERTA, bundle.getString("mensaje.poliza.complementaria.coberturas.KO"));
			String parcelasAnexoCplInfo = StringUtils.nullToString(request.getParameter(PARCELAS_ANEXO_CPL_INFO));
			if ("true".equals(parcelasAnexoCplInfo))
				return doVisualiza(request, response, capitalAseguradoBean).addAllObjects(parametros);
			else
				return doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		}
		logger.debug("end - doCoberturas");
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD));
		parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
		
		return new ModelAndView("moduloUtilidades/modificacionesPolizaComplementaria/coberturasAnexoPolComplementaria",CAPITAL_ASEGURADO_BEAN,capitalAseguradoBean).addAllObjects(parametros);
	}
	
	public ModelAndView doIncrementar(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean)throws Exception{
		//TMR 12/11/2012
		logger.debug("init - doIncrementar");
		
		ModelAndView mv = null;
		// Obtiene el listado de ids de capitales asegurados a incrementar
		String listaIdsSel = StringUtils.nullToString(request.getParameter("listaIds"));
		
		// Obtiene el tipo de incremento que se aplicar√°
		String tipoInc = StringUtils.nullToString(request.getParameter("tipoInc"));
		// Obtiene el incremento que se aplicar√°
		String incremento = StringUtils.nullToString(request.getParameter("incrGen"));
		
		// Realiza el incremento de todos los capitales asegurados indicados en la lista de ids si los tres par√°metros est√°n informados
		if (!"".equals(listaIdsSel) && !"".equals(tipoInc) && !"".equals(incremento)){
			polizaComplementariaManager.capitalesAsegAnexoModificadosLista(listaIdsSel, tipoInc, incremento);
		}
		request.setAttribute(INCREMENTO_OK, "true");
		mv =  doConsulta(request, response, capitalAseguradoBean);
		
		
		return mv;
	}
	public ModelAndView doImprimirInformeListadoParcelasAnexo(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {

		logger.debug("doImprimirInformeListadoParcelasAnexo - Inicio");
		Long idAnexo = null;
		ModelAndView mv = null;

		try {
			
			if(capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getId() != null){
				idAnexo = capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getId();
			}
			else if(!StringUtils.nullToString(request.getParameter(ID_ANEXO)).equals("")){
				idAnexo = new Long(request.getParameter(ID_ANEXO));
			}
			else if(!StringUtils.nullToString(request.getAttribute(ID_ANEXO)).equals("")){
				idAnexo = (Long)(request.getAttribute(ID_ANEXO));
			}
			logger.debug(ID_ANEXO2 + idAnexo);

			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().setId(idAnexo);
			
//			LISTADO DE CAPITALES ASEGURADOS DE LA POLIZA COMPLEMENTARIA
			List<CapitalAsegurado> listCapAseg = modificacionPolizaComplementariaManager.getCapitalesAsegPolCpl(capitalAseguradoModificadaBean);
		
			List<BeanParcelaAnexo> listParcelasAnexo = obtenerListaBeanParcelaAnexo(listCapAseg);
			request.setAttribute("listaParcelasAnexo", listParcelasAnexo);
			request.setAttribute("esPrincipal", false);
			mv = new ModelAndView("forward:/informes.html?method=doInformeListadoParcelasAnexo");
		} catch (Exception e) {
			logger.error("Error");
		}
		return mv;
	}
	private List<BeanParcelaAnexo> obtenerListaBeanParcelaAnexo(List<CapitalAsegurado> listaCapitalesAseguradosAnexo) {
		List<BeanParcelaAnexo> listParcelasAnexo = new ArrayList<BeanParcelaAnexo>();
		ModelTableDecoratorParcelasModificadas decorator = new ModelTableDecoratorParcelasModificadas();

		Iterator<CapitalAsegurado> it = listaCapitalesAseguradosAnexo.iterator();
		while (it.hasNext()) {
			CapitalAsegurado capitalAsegurado = it.next();
			BeanParcelaAnexo bpa = new BeanParcelaAnexo();

			// NÔøΩ
			if (capitalAsegurado.getParcela().getHoja() != null && capitalAsegurado.getParcela().getNumero() != null) {
				bpa.setNumero(
						capitalAsegurado.getParcela().getHoja() + "-" + capitalAsegurado.getParcela().getNumero());
			}

			// PRV, CMC, TRM, SBT
			bpa.setCodProvincia(capitalAsegurado.getParcela().getCodprovincia());
			bpa.setCodComarca(capitalAsegurado.getParcela().getCodcomarca());
			bpa.setCodTermino(capitalAsegurado.getParcela().getCodtermino());
			bpa.setSubtermino(capitalAsegurado.getParcela().getSubtermino() != null
					? capitalAsegurado.getParcela().getSubtermino().toString()
					: null);

			// CUL
			bpa.setCodCultivo(capitalAsegurado.getParcela().getCodcultivo());

			// VAR
			bpa.setCodVariedad(capitalAsegurado.getParcela().getCodvariedad());

			// Id Cat/SIGPAC
			if (capitalAsegurado.getParcela().getPoligono() != null
					&& capitalAsegurado.getParcela().getParcela_1() != null) {
				bpa.setIdCatSigpac(capitalAsegurado.getParcela().getPoligono() + "-"
						+ capitalAsegurado.getParcela().getParcela_1());
			} else {
				String sigPac = "";
				if (capitalAsegurado.getParcela().getCodprovsigpac() != null)
					sigPac = capitalAsegurado.getParcela().getCodprovsigpac().toString();
				if (capitalAsegurado.getParcela().getCodtermsigpac() != null)
					sigPac += "-" + capitalAsegurado.getParcela().getCodtermsigpac().toString();
				if (capitalAsegurado.getParcela().getAgrsigpac() != null)
					sigPac += "-" + capitalAsegurado.getParcela().getAgrsigpac().toString();
				if (capitalAsegurado.getParcela().getZonasigpac() != null)
					sigPac += "-" + capitalAsegurado.getParcela().getZonasigpac().toString();
				if (capitalAsegurado.getParcela().getPoligonosigpac() != null)
					sigPac += "-" + capitalAsegurado.getParcela().getPoligonosigpac().toString();
				if (capitalAsegurado.getParcela().getParcelasigpac() != null)
					sigPac += "-" + capitalAsegurado.getParcela().getParcelasigpac().toString();
				if (capitalAsegurado.getParcela().getRecintosigpac() != null)
					sigPac += "-" + capitalAsegurado.getParcela().getRecintosigpac().toString();
				bpa.setIdCatSigpac(sigPac);
			}

			// Para informe Excel
			if (capitalAsegurado.getParcela().getPoligono() != null
					&& capitalAsegurado.getParcela().getParcela_1() != null) {
				bpa.setParcela(capitalAsegurado.getParcela().getParcela_1());
				bpa.setPoligono(capitalAsegurado.getParcela().getPoligono());
			} else {
				bpa.setCodprovsigpac(capitalAsegurado.getParcela().getCodprovsigpac());
				bpa.setCodtermsigpac(capitalAsegurado.getParcela().getCodtermsigpac());
				bpa.setAgrsigpac(capitalAsegurado.getParcela().getAgrsigpac());
				bpa.setZonasigpac(capitalAsegurado.getParcela().getZonasigpac());
				bpa.setPoligonosigpac(capitalAsegurado.getParcela().getPoligonosigpac());
				bpa.setParcelasigpac(capitalAsegurado.getParcela().getParcelasigpac());
				bpa.setRecintosigpac(capitalAsegurado.getParcela().getRecintosigpac());
			}

			// Nombre
			bpa.setNombre(capitalAsegurado.getParcela().getNomparcela());

			// Super./m
			bpa.setSuperm(decorator.getSuperf(capitalAsegurado));

			// Precio
			bpa.setPrecio(capitalAsegurado.getPrecio());

			// Prod
			bpa.setProduccion(capitalAsegurado.getProduccion());

			// T.Capital
			bpa.setTipoCapital(capitalAsegurado.getTipoCapital().getDestipocapital());

			// Estado
			bpa.setEstado(capitalAsegurado.getParcela().getTipomodificacion() != null
					? capitalAsegurado.getParcela().getTipomodificacion().toString()
					: null);
			
			for(CapitalDTSVariable captDTSVariable:capitalAsegurado.getCapitalDTSVariables()) {
				if(captDTSVariable.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {		
					//Sistema Cultivo
					bpa.setSistemaCultivo(captDTSVariable.getValor());
				}
				if(captDTSVariable.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCOND))) {
					bpa.setSistemaConduccion(captDTSVariable.getValor().toString());
				}
			}
			//Incremento Produccion
			if(capitalAsegurado.getIncrementoproduccionanterior()!=null) {
			bpa.setIncrementoProduccion(capitalAsegurado.getIncrementoproduccionanterior().toString());
			}
			
			//Incremento Modificado
			if(capitalAsegurado.getIncrementoproduccion()!=null) {
			bpa.setIncrementoModificado(capitalAsegurado.getIncrementoproduccion().toString());
			}
			listParcelasAnexo.add(bpa);
		}
		return listParcelasAnexo;
	}
	
	public ModelAndView doInformeListadoParcelas(HttpServletRequest request,HttpServletResponse response,com.rsi.agp.dao.tables.poliza.CapitalAsegurado capitalAseguradoBean) {
		
		List<com.rsi.agp.dao.tables.poliza.CapitalAsegurado> listCapAseg = null;
		List<Parcela> listParcela=new ArrayList<Parcela>();		
		
		try {
			listCapAseg = polizaComplementariaManager.getCapitalesAsegPolCpl(capitalAseguradoBean);
			for(com.rsi.agp.dao.tables.poliza.CapitalAsegurado capitalAsegurado:listCapAseg) {
				if(capitalAsegurado.getIncrementoproduccion() !=null) {
				capitalAsegurado.getParcela().setIncrProduccion(capitalAsegurado.getIncrementoproduccion().toString());
				}
				listParcela.add(capitalAsegurado.getParcela());
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	
		ModelAndView resultado = new ModelAndView("forward:/informes.html?method=doInformeListadoParcelasPoliza");
		
		return resultado;
	}
	
	public void setModificacionPolizaComplementariaManager(DeclaracionesModificacionPolizaComplementariaManager modificacionPolizaComplementariaManager) {
		this.modificacionPolizaComplementariaManager = modificacionPolizaComplementariaManager;
	}

	public void setDeclaracionesModificacionPolizaManager(DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public void setPolizaComplementariaManager(PolizaComplementariaManager polizaComplementariaManager) {
		this.polizaComplementariaManager = polizaComplementariaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	public void setDeclaracionesModificacionPolizaController(
			DeclaracionesModificacionPolizaController declaracionesModificacionPolizaController) {
		this.declaracionesModificacionPolizaController = declaracionesModificacionPolizaController;
	}
	public void setAnexoModificacionManager(
			AnexoModificacionManager anexoModificacionManager) {
		this.anexoModificacionManager = anexoModificacionManager;
	}
}