package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.CargaCSVManager;
import com.rsi.agp.core.managers.impl.CargaPACManager;
import com.rsi.agp.core.managers.impl.ConfiguracionCamposManager;
import com.rsi.agp.core.managers.impl.DatosParcelaManager;
import com.rsi.agp.core.managers.impl.PolizaCopyManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.DatoVariableCargaParcela;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.ProduccionVO;

public class CargaParcelasController extends BaseMultiActionController {

	private static final String ID_POL_SELECCIONADA = "idPolSeleccionada";
	private static final String MENSAJE_CARGA_PARCELAS_BBDD = "mensaje.cargaParcelas.bbdd";
	private static final String MENSAJE_CARGA_PARCELAS_WS = "mensaje.cargaParcelas.ws";
	private static final String WEB_INF = "/WEB-INF/";
	private static final String MENSAJE = "mensaje";
	private static final String ALERTA = "alerta";
	private static final String SELECCION_POLIZA_HTML = "seleccionPoliza.html";
	private static final String LIST_PARCELAS = "listParcelas";
	private static final String OPERACION = "operacion";
	private static final String POLIZA_BEAN = "polizaBean";
	private static final String DATOS_VAR_COPY = "datosVarCopy";
	private static final String LST_DATOS_VAR = "lstDatosVar";
	private static final String RECALCULAR = "recalcular";
	private static final String USUARIO = "usuario";

	private Log logger = LogFactory.getLog(CargaParcelasController.class);

	private DatosParcelaManager datosParcelaManager;
	private PolizaCopyManager polizaCopyManager;
	private CargaPACManager cargaPACManager;
	private CargaCSVManager cargaCSVManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private ConfiguracionCamposManager configuracionCamposManager;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	
	private IPolizaDao polizaDao;

	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * ASF - 17/9/2012 - Ampliacion de la Mejora 79: preguntar si desea
	 * recalcular produccion al cargar la copy Comprueba el numero de copys que
	 * hay para una poliza. A este metodo se le llamara mediante ajax.
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return Redireccion a jsp correspondiente
	 */
	@SuppressWarnings("all")
	public ModelAndView doGetNumCopys(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		logger.debug("doGetNumCopys - inicio");
		try {
			Long idPoliza = new Long(request.getParameter("idpoliza"));
			String nifasegurado = request.getParameter("nifasegurado");
			BigDecimal codlinea = new BigDecimal(
					request.getParameter("codlinea"));
			boolean polAnterior = Boolean.parseBoolean(request
					.getParameter("polAnterior"));
			JSONObject objeto = new JSONObject();
			objeto.put("preguntarRecalculo", (polizaCopyManager.getNumCopys(
					new BigDecimal(request.getParameter("codplan")), codlinea,
					nifasegurado, polAnterior) == 1));
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(objeto.toString());
		} catch (JSONException e) {
			logger.debug("Error al rellenar el objeto json", e);
		} catch (IOException e) {
			logger.debug("Error al devolver el objeto json", e);
		} catch (DAOException e) {
			logger.debug("Error al obtener la poliza", e);
		}
		logger.debug("doGetNumCopys - fin");
		return null;
	}

	// ////////////////////////////////////// INICIO CARGA PAC
	// /////////////////////////////////////////////////////////////
	/**
	 * Metodo para cargar las parcelas de la PAC. Busca los datos variables
	 * asociados y redirecciona a pantalla para mostrarlos y permitir al usuario
	 * elegir los valores.
	 * 
	 * @param request
	 *            Peticion
	 * @param response
	 *            Respuesta
	 * @param polizaBean
	 *            Poliza de destino
	 * @return Redireccion
	 */
	public ModelAndView doParcelasPac(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		ModelAndView resultado = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		String recalcular = request.getParameter(RECALCULAR);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", polizaBean.getIdpoliza());
		
		

		try {
			// DAA 04/09/2013
			List<ConfiguracionCampo> lstDatosVarMarcados = configuracionCamposManager
					.getListaDatosVariablesCargaParcelasMarcados(polizaBean
							.getLinea().getLineaseguroid(), null);

			// TMR 30-05-2012.Facturacion. Al cargar polizas del ano anterior
			// facturamos
			seleccionPolizaManager.callFacturacion(usuario,
					Constants.FACTURA_CONSULTA);

			if (lstDatosVarMarcados.size() > 0) {
				params.put(RECALCULAR, recalcular);
				params.put(LST_DATOS_VAR, lstDatosVarMarcados);
				params.put(DATOS_VAR_COPY, false);
				params.put("listaIdAseguradoPac", request.getParameter("listaIdAseguradoPac"));
				
				resultado = new ModelAndView("moduloPolizas/polizas/verDatosVariablesPac", POLIZA_BEAN, polizaBean).addAllObjects(params);
			} 
			else {
				resultado = doGrabarDatosVariablesPac(request, response, polizaBean);
			}
			
		} catch (BusinessException e) {
			logger.error(
					"Error al obtener la lista de datos variables para la carga de la PAC",
					e);
			params.put(OPERACION, SELECCION_POLIZA_HTML);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		} catch (DAOException e) {
			logger.error(
					"Error al ejecutar la facturacion en la carga de la PAC", e);
			params.put(OPERACION, LIST_PARCELAS);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		}

		return resultado;
	}

	/**
	 * Metodo para grabar los datos de las parcelas cargadas de la PAC con los
	 * datos variables indicados en la pantalla previa
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doGrabarDatosVariablesPac(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {

		ModelAndView resultado;
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		Long idpoliza = Long.parseLong(request.getParameter("idpoliza"));

		logger.debug("doGrabarDatosVariablesPac - INI");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", idpoliza);
		params.put(OPERACION, LIST_PARCELAS);

		logger.debug("doGrabarDatosVariablesPac - Comprueba si la PAC ya se ha cargado o está cargando todavía");
		Character isCargada = cargaPACManager.isPacCargada(idpoliza);
		// Si la PAC ya está cargada se redirige al listado de parcelas
		if (Constants.PAC_CARGADA_SI.equals(isCargada)) {
			return new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean).addAllObjects(params);
		}
		// Si el proceso de carga de PAC no ha finalizado todavía se redirige a la pantalla de selección de póliza
		else if (Constants.PAC_PROCESO_CARGA.equals(isCargada)) {
			return new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean);
		}

		logger.debug("doGrabarDatosVariablesPac - Antes de obtener los datos variables de la PAC");

		// Obtiene la lista de valores introducida en la pantalla de 'Visualización de datos variables PAC'
		List<DatoVariableCargaParcela> lstDatosVar = getDatosVariablesCargaParcela(request);
		String lstDatosVarStr = (lstDatosVar == null || lstDatosVar.isEmpty()) ? null : StringUtils.toValoresSeparadosXComas(lstDatosVar, false, false);
		
		// Obtiene los Ids de los registros de PAC de asegurado cuyas parcelas hay que volcar a la póliza en cuestión
		String listaIdAseguradoPac = request.getParameter("listaIdAseguradoPac");
		

		try {
			logger.debug("doGrabarDatosVariablesPac - Obtenida una lista de " + lstDatosVar.size() + " datos variables de la PAC");
			logger.debug("doGrabarDatosVariablesPac - Ids de los registros de PAC de asegurado cuyas parcelas hay que volcar a la póliza en cuestión: " + listaIdAseguradoPac);

			logger.debug("doGrabarDatosVariablesPac - Llama al procedimiento que vuelca las parcelas de PAC en la póliza: " + listaIdAseguradoPac);
			String errorMsg = this.cargaPACManager.cargaParcelasPolizaDesdePAC(idpoliza, usuario.getClase().getId(), listaIdAseguradoPac, lstDatosVarStr);
			
			// Si hay algún mensaje de error se para como parámetro a la pantalla de listado de parcelas para que se muestre
			if (errorMsg != null && !"".equals(errorMsg)) {
				params.put(ALERTA, errorMsg);
			}
			else {
				params.put(MENSAJE, "Parcelas de PAC cargadas correctamente");
				
				// Se recalcula el precio y producción de las parcelas cargadas si así se ha indicado en la pantalla
				if ("si".equals(StringUtils.nullToString(request.getParameter(RECALCULAR)))) {
					logger.debug("doGrabarDatosVariablesPac - Recalcula el precio y la produccion para todas las parcelas");
					recalcular(polizaBean, this.getServletContext().getRealPath(WEB_INF), usuario);
				}
			}
			
			logger.debug("Redireccion a pagina de seleccion de poliza");
			resultado = new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean).addAllObjects(params);
		} 
		catch (Throwable e) {
			logger.error("Error al copiar las parcelas de PAC en la póliza",e);
			resultado = new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean).addAllObjects(params);
		}

		logger.debug("doGrabarDatosVariablesPac - FIN");
		return resultado;
	}

	// ////////////////////////////////////// FIN CARGA PAC
	// /////////////////////////////////////////////////////////////

	/**
	 * DAA 05/09/2013 Devuelve la lista de los datos variables que se muestran
	 * en la pantalla verDatosVariablesPac.jsp
	 * 
	 * @param request
	 * @return lstDatosVar
	 */
	@SuppressWarnings("unchecked")
	private List<DatoVariableCargaParcela> getDatosVariablesCargaParcela(
			HttpServletRequest request) {
		List<DatoVariableCargaParcela> lstDatosVar = new ArrayList<DatoVariableCargaParcela>();
		Enumeration<String> parameterNames = request.getParameterNames();
		String valor = "";

		while (parameterNames.hasMoreElements()) {
			String parametro = parameterNames.nextElement().toString();

			if (parametro.indexOf("codConcepto") >= 0
					&& parametro.indexOf(".") < 0) {
				valor = request.getParameter(parametro);
				String[] paramCodconcepto = parametro.split("_");
				
				if (!StringUtils.nullToString(valor).equals("")) {
					DatoVariableCargaParcela datVar = new DatoVariableCargaParcela(new BigDecimal(paramCodconcepto[1]), valor);
					lstDatosVar.add(datVar);
				}
			}
		}
		return lstDatosVar;
	}

	// ////////////////////////////////////// INICIO CARGA COPY
	// //////////////////////////////////////////////////////////
	/**
	 * Metodo para realizar la carga de parcelas desde la copy. En caso de que
	 * haya mas de una copy, se redireccionara a otra pagina donde el usuario
	 * podra elegir la copy que desea cargar.
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doSituacionAct(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		ModelAndView resultado;
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", polizaBean.getIdpoliza());
		params.put(OPERACION, LIST_PARCELAS);
		params.put("asegurado.nifcif", polizaBean.getAsegurado().getNifcif());
		params.put("linea.codplan", polizaBean.getLinea().getCodplan());
		params.put("linea.codlinea", polizaBean.getLinea().getCodlinea());
		params.put("codEntidad" ,polizaBean.getAsegurado().getEntidad().getCodentidad());
		
		
		logger.debug("**@@** CargaParcelasController - doSituacionAct [INIT]");
		
		String recalcular = request.getParameter(RECALCULAR);

		// Arrastra las Parcelas del Asegurado de la poliza de la linea del plan
		// anterior
		int parcelasCargadas;
		try {
			parcelasCargadas = this.polizaCopyManager.mueveParcelas(polizaBean,
					realPath, true, false);

			logger.debug("**@@** CargaParcelasController - valor de parcelasCargadas:"+parcelasCargadas);
	
			// AMG 22/08/2012. Si para un mismo asegurado y linea hay mas de una
			// poliza contratada para el plan anterior
			// permitir elegir la poliza a cargar.
			if (parcelasCargadas==0) {
				// Creamos una póliza para el filtro
				params.put("origenLlamada", "doSituacionAct");
				resultado = new ModelAndView(new RedirectView("polizaActualizada.run"), POLIZA_BEAN, polizaBean).addAllObjects(params);
			} else {
				
				logger.debug("**@@** CargaParcelasController - entramos en el else");
				// DAA 10/09/2013 obtengo la lista de codconceptos de los datos
				// variables de las parcelas de la COPY
				// MPM 09/03/2015 - No se pasa la lista de conceptos para que se
				// muestren todos los DV configurados para que aparezcan
				// List<BigDecimal> lstCodConceptos =
				// cargaPACManager.getDatosVariableParcelas(polizaBean.getIdpoliza());
				// recupero la lista de datos variables excepto para los
				// codconcepto que ya tengo
				List<ConfiguracionCampo> lstDatosVarMarcados = configuracionCamposManager
						.getListaDatosVariablesCargaParcelasMarcados(polizaBean
								.getLinea().getLineaseguroid(), null);

				if (!lstDatosVarMarcados.isEmpty()
						&& lstDatosVarMarcados != null) {
					logger.debug("**@@** CargaParcelasController - entramos en el if(1)");
					if(parcelasCargadas==1){
						params.put("mensajeOrigen", bundle.getObject(MENSAJE_CARGA_PARCELAS_WS));			
					}else if(parcelasCargadas==2){
						params.put("alertaOrigen", bundle.getObject(MENSAJE_CARGA_PARCELAS_BBDD));
					}
					params.put(LST_DATOS_VAR, lstDatosVarMarcados);
					params.put(RECALCULAR, recalcular);
					params.put(DATOS_VAR_COPY, true);
					resultado = new ModelAndView(
							"moduloPolizas/polizas/verDatosVariablesPac",
							POLIZA_BEAN, polizaBean).addAllObjects(params);
				} else {
					logger.debug("**@@** CargaParcelasController - entramos en el else(1)");
					if(parcelasCargadas==1){
						params.put(WEB_INF, bundle.getObject(MENSAJE_CARGA_PARCELAS_WS));			
					}else if(parcelasCargadas==2){
						params.put(ALERTA, bundle.getObject(MENSAJE_CARGA_PARCELAS_BBDD));
					}
					logger.debug("**@@** CargaParcelasController - Antes de doGrabarDatosVariablesCopy");
					
					resultado = doGrabarDatosVariablesCopy(request, response,
							polizaBean);
					resultado.addAllObjects(params);
				}
			}
			
			
			
		} catch (Exception e) {
			logger.error("Error al cargar las parcelas de COPY", e);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		}

		return resultado;
	}

	/**
	 * DAA 10/09/2013
	 * 
	 */
	public ModelAndView doGrabarDatosVariablesCopy(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {

		
		logger.debug("**@@**CargaParcelasController - doGrabarDatosVariablesCopy [INIT]");
		logger.debug("**@@**CargaParcelasController (N) - sizeParcelas:"+polizaBean.getParcelas().size());
		polizaBean = this.seleccionPolizaManager.getPolizaById(polizaBean.getIdpoliza());
		
		logger.debug("**@@**CargaParcelasController (N) - Después de volver a cargar la poliza");
		logger.debug("**@@**CargaParcelasController (N) - sizeParcelas:"+polizaBean.getParcelas().size());
		
		ModelAndView resultado;
		Usuario usuario = (Usuario) request.getSession()
				.getAttribute(USUARIO);
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		String recalcular = StringUtils.nullToString(request
				.getParameter(RECALCULAR));
		Long idpoliza = Long.parseLong(request.getParameter("idpoliza"));
		
		String mensajeOrigen=StringUtils.nullToString(request.getParameter("mensajeOrigen"));
		String alertaOrigen=StringUtils.nullToString(request.getParameter("alertaOrigen"));
		
		// recupero la lista de los datos variables que me quedan
		List<DatoVariableCargaParcela> lstDatosVar = getDatosVariablesCargaParcela(request);
		logger.debug("doGrabarDatosVariablesPac - Obtenida una lista de "
				+ lstDatosVar.size() + " datos variables de la COPY");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", idpoliza);
		params.put(OPERACION, LIST_PARCELAS);

		try {

			if (lstDatosVar.size() > 0) {
				logger.debug("doGrabarDatosVariablesCopy - Se guardan los datos variables en los capitales de las parcelas de la COPY");
				this.cargaPACManager.grabarDatosVariablesCopy(polizaBean,
						lstDatosVar);
			}
		} catch (Exception e) {
			logger.error(
					"Error al obtener la lista de datos variables para la carga de la COPY",
					e);
			params.put(ALERTA,
					"Error al guardar la lista de datos variables para la carga de la COPY");
			return new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML),
					POLIZA_BEAN, polizaBean).addAllObjects(params);
		}

		try {
			
			logger.debug("**@@**CargaParcelasController - doGrabarDatosVariablesCopy, entramos en el try");
			// TMR 30-05-2012.Facturacion. Al cargar polizas del ano anterior
			// facturamos
			seleccionPolizaManager.callFacturacion(usuario,
					Constants.FACTURA_CONSULTA);
			
			logger.debug("**@@**CargaParcelasController - doGrabarDatosVariablesCopy, despues de llamara a Facturacion");

			// Comprobamos si el usuario ha elegido recalcular precio y
			// produccion para las parcelas.
			
			logger.debug("**@@**CargaParcelasController - doGrabarDatosVariablesCopy, valor de recalcular:"+recalcular);

			if (StringUtils.nullToString(recalcular).equals("si")) {
				
				logger.debug("**@@**CargaParcelasController - doGrabarDatosVariablesCopy, entramos en el if");
				logger.debug("**@@**CargaParcelasController - size de parcelas:"+polizaBean.getParcelas().size());
				/* ESC-11722 ** MODIF TAM (16.12.2020) ** Inicio */
				/* Si no se han cargado Parcelas no se lanza la llamada al S.W de Recalculo */
				if (polizaBean.getParcelas().size() > 0){
					logger.debug("**@@**CargaParcelasController - doGrabarDatosVariablesCopy, Antes de recalcular");
					// recalcular precio y produccion
					recalcular(polizaBean, realPath, usuario);
				}
				/* ESC-11722 ** MODIF TAM (16.12.2020) ** Fin */	
			}
			
			
			if(!mensajeOrigen.isEmpty())params.put(MENSAJE, mensajeOrigen);
			if(!alertaOrigen.isEmpty())params.put(ALERTA, alertaOrigen);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		} catch (Throwable e) {
			params.put(ALERTA, bundle.getString("mensaje.cargaCOPY.recalculo.KO"));
			return new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML),POLIZA_BEAN, polizaBean).addAllObjects(params);
		}

		logger.debug("doGrabarDatosVariablesPac - FIN");
		return resultado;
	}

	/**
	 * Metodo para cargar las parcelas de la copy elegida por el usuario.
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doSituacionActElegida(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		
		logger.debug("**@@** CargaParcelasController - doSituacionActElegida [INIT]");

		ModelAndView resultado;
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Map<String, Object> params = new HashMap<String, Object>();

		String recalcular = request.getParameter(RECALCULAR);

		Long idPolSeleccionada = Long.parseLong(request
				.getParameter(ID_POL_SELECCIONADA));
		Poliza polizaOrigen = this.seleccionPolizaManager
				.getPolizaById(idPolSeleccionada);
		Long idPolDestino = Long.parseLong(request.getParameter("idpoliza"));
		polizaBean = this.seleccionPolizaManager.getPolizaById(idPolDestino);

		// Arrastra las Parcelas del Asegurado de la poliza de la linea del plan
		// anterior
		int parcelasCargadas= this.polizaCopyManager.mueveParcelas(polizaBean, realPath,
				polizaOrigen, true);
		
		if(parcelasCargadas==1){
			params.put("mensajeOrigen", bundle.getObject(MENSAJE_CARGA_PARCELAS_WS));			
		}else if(parcelasCargadas==2){
			params.put("alertaOrigen", bundle.getObject(MENSAJE_CARGA_PARCELAS_BBDD));
		}
		
		try {
			List<BigDecimal> lstCodConceptos = cargaPACManager
					.getDatosVariableParcelas(polizaBean.getIdpoliza());
			// recupero la lista de datos variables excepto para los codconcepto
			// que ya tengo
			List<ConfiguracionCampo> lstDatosVarMarcados = configuracionCamposManager
					.getListaDatosVariablesCargaParcelasMarcados(polizaBean
							.getLinea().getLineaseguroid(), lstCodConceptos);

			logger.debug("CargaParcelasController - Despues de haber obtenido la lista de Datos Variables de la Parcela");
			if (!lstDatosVarMarcados.isEmpty() && lstDatosVarMarcados != null) {
				logger.debug("CargaParcelasController - Entra en el if");
				params.put(LST_DATOS_VAR, lstDatosVarMarcados);
				params.put(RECALCULAR, recalcular);
				params.put(DATOS_VAR_COPY, true);
				resultado = new ModelAndView(
						"moduloPolizas/polizas/verDatosVariablesPac",
						POLIZA_BEAN, polizaBean).addAllObjects(params);
			} else {
				logger.debug("CargaParcelasController - Entra en el else");
				logger.debug("CargaParcelasController - Antes de ejecutar el doGrabarDatosVariablesCopy");
				
				resultado = doGrabarDatosVariablesCopy(request, response,
						polizaBean);
				resultado.addAllObjects(params);
			}
		} catch (Exception e) {
			logger.error(
					"Error al cargar las parcelas de la situacion elegida", e);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		}
		return resultado;
	}

	// ////////////////////////////////////// FIN CARGA COPY
	// /////////////////////////////////////////////////////////////

	// ////////////////////////////////////// INICIO OTRA POLIZA MISMO
	// PLAN/LINEA ///////////////////////////////////////////////////
	/**
	 * Metodo para mostrar un listado de polizas del mismo plan/linea para que
	 * el usuario elija de cual quiere cargar las parcelas.
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doMultiClase(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		ModelAndView resultado;
		Map<String, Object> params = new HashMap<String, Object>();

		// Muestra las polizas con mismo lineaseguroid, colectivo, asegurado y
		// distinta clase, con al menos una parcela
		List<Poliza> lstPolizas = new ArrayList<Poliza>();
		try {
			lstPolizas = seleccionPolizaManager.getListaPolizas(polizaBean);
		} catch (BusinessException e) {
			logger.error(
					"Error al cargar las polizas de las que cargar las parcelas",
					e);
		}
		params.put("listaPolizas", lstPolizas);

		// Si existe alguna poliza con al menos una parcela se indica por
		// parametro a la jsp
		params.put("tieneParcelas",
				(lstPolizas != null && lstPolizas.size() > 0) ? "si" : "no");

		// Redireccion
		resultado = new ModelAndView(
				"moduloPolizas/polizas/elegirPolizaACargar", POLIZA_BEAN,
				polizaBean).addAllObjects(params);
		return resultado;
	}

	/**
	 * Metodo para cargar las parcelas de una poliza de distinta clase pero el
	 * mismo plan/linea
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doCargarParcelasDistClase(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {

		Usuario usuario = (Usuario) request.getSession()
				.getAttribute(USUARIO);
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		ModelAndView resultado;
		Long idPolSeleccionada = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if (!request.getParameter(ID_POL_SELECCIONADA).equals("")) {
				Long idpoliza = Long
						.parseLong(request.getParameter("idpoliza"));
				polizaBean = seleccionPolizaManager.getPolizaById(idpoliza); // Necesaria
																				// la
																				// consulta
																				// para
																				// cargar
																				// todos
																				// los
																				// datos
				idPolSeleccionada = Long.parseLong(request
						.getParameter(ID_POL_SELECCIONADA));
				// Replicamos las Parcelas de la poliza seleccinada con distinta
				// clase
				Poliza polizaParaCopiar = seleccionPolizaManager
						.getPolizaById(idPolSeleccionada);
				seleccionPolizaManager.guardarParcelasPoliza(polizaBean,
						polizaParaCopiar);

				// COMPROBAMOS DE NUEVO QUE HAY PRECIO Y PRODUCCION PARA TODAS
				// LAS PARCELAS
				String recalcular = StringUtils.nullToString(request
						.getParameter(RECALCULAR));
				if (polizaBean.getParcelas().size() > 0) {
					parameters.put("tieneParcelas", "si");
				} else {
					parameters.put("tieneParcelas", "no");
				}

				if ("si".equals(recalcular)) {
					/* ESC-11722 ** MODIF TAM (16.12.2020) ** Inicio */
					/* Si no se han cargado Parcelas no se lanza la llamada al S.W de Recalculo */
					if (polizaBean.getParcelas().size() > 0){
						// recalcular precio y produccion
						this.recalcular(polizaBean, realPath, usuario);
					}
					/* ESC-11722 ** MODIF TAM (16.12.2020) ** Fin */
					
					
				} else {
					
					// copiar coberturas
					copiarCoberturasParcela(polizaParaCopiar, polizaBean, false);
					seleccionPolizaManager
							.setProduccionModulosNuevos(polizaBean);
				}

				parameters.put("idpoliza", polizaBean.getIdpoliza());
				parameters.put(OPERACION, LIST_PARCELAS);
				resultado = new ModelAndView(new RedirectView(
						SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
						.addAllObjects(parameters);
			} else {
				parameters.put(ALERTA,
						bundle.getString("cambiooficinaNoCheck.msgError"));
				resultado = new ModelAndView(
						"moduloPolizas/polizas/elegirPolizaACargar",
						POLIZA_BEAN, polizaBean);
			}
		} catch (Exception e) {
			logger.error(
					"Error al guardar las parcelas cargadas de otra poliza del mismo plan/linea",
					e);
			parameters.put(OPERACION, LIST_PARCELAS);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(parameters);
		} catch (Throwable e) {
			logger.error(
					"Error al guardar las parcelas cargadas de otra poliza del mismo plan/linea",
					e);
			parameters.put(OPERACION, LIST_PARCELAS);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(parameters);
		}
		return resultado;
	}
	
	
	
	
	private void copiarCoberturasParcela(Poliza polizaOrigen, Poliza polizaNueva, boolean isPolizaAnterior) throws BusinessException {
				
		
		logger.debug("init - copiarCoberturasParcela");

		try {
			for (Parcela parcela: polizaNueva.getParcelas()) {
				logger.info("nueva parcela: " + parcela.getHoja() + "," + parcela.getNumero());
				
				if (isPolizaAnterior) {
					// TODO copiar todas las coberturas elegibles
					datosParcelaManager.copyElegibleCoberturas(parcela.getIdparcela());
				}
				
				
				// obtener la parcela de origen
				for (Parcela origen: polizaOrigen.getParcelas()) {
					logger.info("Origen: " + origen.getHoja() + ", " + origen.getNumero());
					if (origen.getHoja().equals(parcela.getHoja()) && origen.getNumero().equals(parcela.getNumero())) {
						logger.debug("CargaParcelasController - encontrada la parcela");
						
						
						// copiar coberturas
						for (ParcelaCobertura cobertura: origen.getCoberturasParcela()) {
							
							if (isPolizaAnterior) {
								datosParcelaManager.actualizaParcelaCobertura(cobertura.getId(), parcela.getIdparcela(), polizaNueva.getLinea().getLineaseguroid());
								
							} else {
								datosParcelaManager.copyParcelaCobertura(cobertura.getId(), parcela.getIdparcela());
							}

						}
						
						
						break;
					}
				}
				
				
			}
		
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de datos " + ex.getMessage());
			throw new BusinessException("[ERROR] en SeleccionPolizaManager- metodo guardarParcelasPoliza.", ex);
		}

		logger.debug("Fin - copiarCoberturasParcela");
	}
	


	// ////////////////////////////////////// FIN OTRA POLIZA MISMO PLAN/LINEA
	// ///////////////////////////////////////////////////

	// //////////////////////////////////////INICIO POLIZA PLAN ANTERIOR
	// ///////////////////////////////////////////////////
	/**
	 * Metodo para realizar la carga de parcelas desde la poliza del plan
	 * anterior. En caso de que haya mas de una, se redireccionara a otra pagina
	 * donde el usuario podra elegir la poliza que desea cargar.
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doPolizaAnterior(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		ModelAndView resultado;
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Usuario usuario = (Usuario) request.getSession()
				.getAttribute(USUARIO);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", polizaBean.getIdpoliza());
		params.put(OPERACION, LIST_PARCELAS);
		params.put("asegurado.nifcif", polizaBean.getAsegurado().getNifcif());
		params.put("linea.codplan", polizaBean.getLinea().getCodplan());
		params.put("linea.codlinea", polizaBean.getLinea().getCodlinea());
		params.put("descargarCopy", false);
		logger.debug("CargaParcelasController - doPolizaAnterior [INIT]");

		// Arrastra las Parcelas del Asegurado de la poliza de la linea del plan
		// anterior
		int parcelasCargadas;
		try {
			parcelasCargadas = this.polizaCopyManager.mueveParcelas(polizaBean,
					realPath, false, true);

			if(parcelasCargadas==1){
				params.put(MENSAJE, bundle.getObject(MENSAJE_CARGA_PARCELAS_WS));			
			}else if(parcelasCargadas==2){
				params.put(MENSAJE, bundle.getObject(MENSAJE_CARGA_PARCELAS_BBDD));
			}
			
			logger.debug("parcelasCargadas: " + parcelasCargadas);

			// AMG 22/08/2012. Si para un mismo asegurado y linea hay mas de una
			// poliza contratada para el plan anterior
			// permitir elegir la poliza a cargar.
			if (parcelasCargadas==0) {
				logger.debug("No hay parcelas cargadas");

				resultado = new ModelAndView(new RedirectView(
						"polizaActualizada.run"), POLIZA_BEAN, polizaBean)
						.addAllObjects(params);
			} else {
				// TMR 30-05-2012.Facturacion. Al cargar polizas del ano
				// anterior facturamos
				seleccionPolizaManager.callFacturacion(usuario,
						Constants.FACTURA_CONSULTA);

				// Comprobamos si el usuario ha elegido recalcular precio y
				// produccion para las parcelas.
				String recalcular = request.getParameter(RECALCULAR);
				logger.debug("ESC-15240 --> recalcular: " + recalcular);
				if (StringUtils.nullToString(recalcular).equals("si")) {
					
					int numParcelas = this.seleccionPolizaManager.getPolizaById(polizaBean.getIdpoliza()).getParcelas().size();
					logger.debug("ESC-15240 --> num parcelas: " + numParcelas);
					
					/* ESC-11722 ** MODIF TAM (16.12.2020) ** Inicio */
					/* Si no se han cargado Parcelas no se lanza la llamada al S.W de Recalculo */
					if (numParcelas > 0){
						// recalcular precio y produccion
						recalcular(polizaBean, realPath, usuario);
					}
					/* ESC-11722 ** MODIF TAM (16.12.2020) ** Fin */
				} else {

					logger.debug("Iniciar la copia de coberturas");
					logger.debug("\tP_IDPOLIZA_DEST: " + polizaBean.getIdpoliza());
					Poliza poliza = this.seleccionPolizaManager
							.getPolizaById(polizaBean.getIdpoliza());
					logger.debug("\tP_IDPOLIZA_DEST PARCELAS:: " + polizaBean.getParcelas().size());

					// TODO Por cada registro del plan anterior, verificar si aplica mirando en la tabla tb_sc_c_riesgo_cbrto_mod para la línea, modulo, concepto principal, riesgo cubierto y nivel de elección 'D'... y si está en esa tabla es que aplica para el plan nuevo, por lo que lo copiaríamos.
					Poliza polizaAnterior = this.polizaDao.getPolizaContratada(polizaBean.getLinea().getCodplan(), 
							polizaBean.getLinea().getCodlinea(),polizaBean.getAsegurado().getNifcif(),true);
					logger.debug("\tP_IDPOLIZA_ORIG: " + polizaAnterior.getIdpoliza());


					copiarCoberturasParcela(polizaAnterior, poliza, true);

					
				}

				resultado = new ModelAndView(new RedirectView(
						SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
						.addAllObjects(params);
			}
		} catch (Exception e) {
			logger.error("Error al cargar las parcelas de COPY", e);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		} catch (Throwable e) {
			logger.error("Error al cargar las parcelas de COPY", e);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		}

		logger.debug("CargaParcelasController - doPolizaAnterior [END]");

		return resultado;
	}

	/**
	 * Metodo para cargar las parcelas de la poliza del plan anterior elegida
	 * por el usuario.
	 * 
	 * @param request
	 * @param response
	 * @param polizaBean
	 * @return
	 */
	public ModelAndView doPolizaAnteriorElegida(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {

		ModelAndView resultado;
		Usuario usuario = (Usuario) request.getSession()
				.getAttribute(USUARIO);
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Map<String, Object> params = new HashMap<String, Object>();

		Long idPolSeleccionada = Long.parseLong(request
				.getParameter(ID_POL_SELECCIONADA));
		Poliza polizaOrigen = this.seleccionPolizaManager
				.getPolizaById(idPolSeleccionada);
		Long idPolDestino = Long.parseLong(request.getParameter("idpoliza"));
		Poliza polizaDestino = this.seleccionPolizaManager
				.getPolizaById(idPolDestino);

		// Arrastra las Parcelas del Asegurado de la poliza de la linea del plan
		// anterior
		int cargaParcelas=this.polizaCopyManager.mueveParcelas(polizaDestino, realPath,
				polizaOrigen, false);
		if(cargaParcelas==1){
			params.put(MENSAJE, bundle.getObject(MENSAJE_CARGA_PARCELAS_WS));			
		}else if(cargaParcelas==2){
			params.put(MENSAJE, bundle.getObject(MENSAJE_CARGA_PARCELAS_BBDD));
		}
		// TMR 30-05-2012.Facturacion. Al cargar polizas del ano anterior
		// facturamos
		try {
			seleccionPolizaManager.callFacturacion(usuario,
					Constants.FACTURA_CONSULTA);
		} catch (DAOException e) {
			logger.error("Error al llamar a facturacion desde carga del copy",
					e);
		}

		// Comprobamos si el usuario ha elegido recalcular precio y produccion
		// para las parcelas.
		String recalcular = request.getParameter(RECALCULAR);
		logger.debug("ESC-15240 --> recalcular: " + recalcular);
		if (StringUtils.nullToString(recalcular).equals("si")) {
			// recalcular precio y produccion
			try {
				int numParcelas = polizaDestino.getParcelas().size();
				logger.debug("ESC-15240 --> num parcelas: " + numParcelas);
				/* ESC-11722 ** MODIF TAM (16.12.2020) ** Inicio */
				/* Si no se han cargado Parcelas no se lanza la llamada al S.W de Recalculo */
				if (numParcelas > 0){
					// recalcular precio y produccion
					recalcular(polizaBean, realPath, usuario);
				}
				/* ESC-11722 ** MODIF TAM (16.12.2020) ** Fin */
			} catch (Exception e) {
				logger.error(
						"Error al recalcular precio y produccion durante la carga del COPY",
						e);
			} catch (Throwable e) {
				logger.error(
						"Error al recalcular precio y produccion durante la carga del COPY",
						e);
			}
		} else {

			logger.debug("Iniciar la copia de coberturas");
			logger.debug("\tP_IDPOLIZA_DEST: " + polizaDestino.getIdpoliza());

			logger.debug("\tP_IDPOLIZA_ORIG: " + polizaOrigen.getIdpoliza());

			// Iterar las coberturas del plan anterior y mirar en la tabla tb_sc_c_riesgo_cbrto_mod, si está entonces se puede copiar la cobertura
			try {
				copiarCoberturasParcela(polizaOrigen, polizaDestino, true);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

		params.put("idpoliza", polizaBean.getIdpoliza());
		params.put(OPERACION, LIST_PARCELAS);
		resultado = new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML),
				POLIZA_BEAN, polizaDestino).addAllObjects(params);
		return resultado;
	}

	// //////////////////////////////////////FIN POLIZA PLAN ANTERIOR
	// ///////////////////////////////////////////////////

	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {
		return null;
	}

	/**
	 * Metodo para ejecutar el recalculo de precio y produccion para una poliza
	 * 
	 * @param polizaBean
	 * @throws Throwable
	 */
	private void recalcular(Poliza polizaBean, String realPath, Usuario usuario)
			throws Throwable {
		List<String> codsModuloPoliza = new ArrayList<String>();
		
		logger.debug("**@@** CargaParcelasController - recalcular [INIT] " );

		List<ModuloPoliza> comparativas = polizaDao.getLstModulosPoliza(polizaBean.getIdpoliza());

		for (ModuloPoliza comp : comparativas) {
			if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
				codsModuloPoliza.add(comp.getId().getCodmodulo());
		}
		Parcela parcela = new Parcela();
		parcela.getPoliza().setIdpoliza(polizaBean.getIdpoliza());
		List<Parcela> parcelas = this.seleccionPolizaManager.getParcelas(
				parcela, null, null);

		boolean recalcularRendimientoConSW = calculoPrecioProduccionManager
				.calcularRendimientoProdConSW();

		logger.debug("ESC-15240 --> comparativas: " + comparativas.size());
		logger.debug("ESC-15240 --> recalcularRendimientoConSW: " + recalcularRendimientoConSW);
		
		if (recalcularRendimientoConSW) {

			logger.debug("ESC-15240 --> antes de llamar a SW");
			Map<String, ProduccionVO> mapaRendimientosProd = calculoPrecioProduccionManager
					.calcularRendimientosPolizaWS(polizaBean.getIdpoliza(),
							null, realPath, usuario.getCodusuario(), 0);
			logger.debug("ESC-15240 --> tras llamar a SW");

			for (ComparativaPoliza comp : polizaBean.getComparativaPolizas()) {
				if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
					codsModuloPoliza.add(comp.getId().getCodmodulo());
			}

			seleccionPolizaManager.recalculoPrecioProduccion(parcelas, codsModuloPoliza, recalcularRendimientoConSW, mapaRendimientosProd);

		} else {
			seleccionPolizaManager.recalculoPrecioProduccion(parcelas,
					codsModuloPoliza);
		}
	}
	
	
	// ////////////////////////////////////// INICIO CARGA CSV
	// /////////////////////////////////////////////////////////////
	/**
	 * Metodo para cargar las parcelas de CSV. Busca los datos variables
	 * asociados y redirecciona a pantalla para mostrarlos y permitir al usuario
	 * elegir los valores.
	 * 
	 * @param request
	 *            Peticion
	 * @param response
	 *            Respuesta
	 * @param polizaBean
	 *            Poliza de destino
	 * @return Redireccion
	 */
	public ModelAndView doParcelasCsv(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) {
		ModelAndView resultado = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", polizaBean.getIdpoliza());
		
		//ESC-7421 DNF 21/10/2019 Creo la variable recalcular que la necesito para pasar el valor al jsp
		String recalcular = request.getParameter(RECALCULAR);

		try {
			
			//seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_CONSULTA);
			//resultado = doGrabarDatosVariablesCsv(request, response, polizaBean);

			/*ESC-7421 DNF 21/10/2019***************************/
			List<ConfiguracionCampo> lstDatosVarMarcados = configuracionCamposManager
					.getListaDatosVariablesCargaParcelasMarcados(polizaBean
							.getLinea().getLineaseguroid(), null);

			seleccionPolizaManager.callFacturacion(usuario, Constants.FACTURA_CONSULTA);

			if (lstDatosVarMarcados.size() > 0) {
				params.put(RECALCULAR, recalcular);
				params.put(LST_DATOS_VAR, lstDatosVarMarcados);
				params.put(DATOS_VAR_COPY, false);
				params.put("listaIdAseguradoCsv", request.getParameter("listaIdAseguradoCsv"));
				
				resultado = new ModelAndView("moduloPolizas/polizas/verDatosVariablesCsv", POLIZA_BEAN, polizaBean).addAllObjects(params);
			} 
			else {
				resultado = doGrabarDatosVariablesCsv(request, response, polizaBean);
			}
			/*FIN ESC-7421 DNF 21/10/2019***********************/
			
			
				
		} catch (DAOException e) {
			logger.error(
					"Error al ejecutar la facturacion en la carga de CSV", e);
			params.put(OPERACION, LIST_PARCELAS);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		}
		/*ESC-7421 DNF 21/10/2019 Añado BusinessException necesaria para la lista lstDatosVarMarcados********************/
		catch (BusinessException e) {
			logger.error(
					"Error al obtener la lista de datos variables para la carga del CSV",
					e);
			params.put(OPERACION, LIST_PARCELAS);
			resultado = new ModelAndView(new RedirectView(
					SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean)
					.addAllObjects(params);
		} 
		/*FIN ESC-7421 DNF 21/10/2019 Añado BusinessException necesaria para la lista lstDatosVarMarcados****************/
		

		return resultado;
	}
	
	public ModelAndView doGrabarDatosVariablesCsv(HttpServletRequest request,
			HttpServletResponse response, Poliza polizaBean) {

		ModelAndView resultado;
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		Long idpoliza = Long.parseLong(request.getParameter("idpoliza"));

		logger.debug("doGrabarDatosVariablesCsv - INI");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idpoliza", idpoliza);
		params.put(OPERACION, LIST_PARCELAS);

		logger.debug("doGrabarDatosVariablesCsv - Comprueba si el CSV ya se ha cargado o está cargando todavía");
		Character isCargada = cargaCSVManager.isCsvCargado(idpoliza);
		// Si el CSV ya está cargada se redirige al listado de parcelas
		if (Constants.PAC_CARGADA_SI.equals(isCargada)) {
			return new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean).addAllObjects(params);
		}
		// Si el proceso de carga de CSV no ha finalizado todavía se redirige a la pantalla de selección de póliza
		else if (Constants.PAC_PROCESO_CARGA.equals(isCargada)) {
			return new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean);
		}

		logger.debug("doGrabarDatosVariablesCsv - Antes de obtener los datos variables del CSV");

		// Obtiene la lista de valores introducida en la pantalla de 'Visualización de datos variables PAC'
		List<DatoVariableCargaParcela> lstDatosVar = getDatosVariablesCargaParcela(request);
		String lstDatosVarStr = (lstDatosVar == null || lstDatosVar.isEmpty()) ? null : StringUtils.toValoresSeparadosXComas(lstDatosVar, false, false);
		
		// Obtiene los Ids de los registros de PAC de asegurado cuyas parcelas hay que volcar a la póliza en cuestión
		String listaIdAseguradoCsv = request.getParameter("listaIdAseguradoCsv");
		

		try {
			logger.debug("doGrabarDatosVariablesCsv - Obtenida una lista de " + lstDatosVar.size() + " datos variables del CSV");
			logger.debug("doGrabarDatosVariablesCsv - Ids de los registros de CSV de asegurado cuyas parcelas hay que volcar a la póliza en cuestión: " + listaIdAseguradoCsv);

			logger.debug("doGrabarDatosVariablesCsv - Llama al procedimiento que vuelca las parcelas de CSV en la póliza: " + listaIdAseguradoCsv);
			String errorMsg = this.cargaCSVManager.cargaParcelasPolizaDesdeCSV(idpoliza, usuario.getClase().getId(), listaIdAseguradoCsv, lstDatosVarStr);
			
			// Si hay algún mensaje de error se para como parámetro a la pantalla de listado de parcelas para que se muestre
			if (errorMsg != null && !"".equals(errorMsg)) {
				params.put(ALERTA, errorMsg);
			}
			else {
				params.put(MENSAJE, "Parcelas de CSV cargadas correctamente");
				
				this.seleccionPolizaManager.obtenerHojaNumero(this.seleccionPolizaManager.getPolizaById(idpoliza));
				
				// Se recalcula el precio y producción de las parcelas cargadas si así se ha indicado en la pantalla
				if ("si".equals(StringUtils.nullToString(request.getParameter(RECALCULAR)))) {
					logger.debug("doGrabarDatosVariablesCsv - Recalcula el precio y la produccion para todas las parcelas");
					recalcular(polizaBean, this.getServletContext().getRealPath(WEB_INF), usuario);
				}
			}
			
			logger.debug("Redireccion a pagina de seleccion de poliza");
			resultado = new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean).addAllObjects(params);
		} 
		catch (Throwable e) {
			logger.error("Error al copiar las parcelas de CSV en la póliza",e);
			resultado = new ModelAndView(new RedirectView(SELECCION_POLIZA_HTML), POLIZA_BEAN, polizaBean).addAllObjects(params);
		}

		logger.debug("doGrabarDatosVariablesCsv - FIN");
		return resultado;
	}
	
	
	

	public void setPolizaCopyManager(PolizaCopyManager polizaCopyManager) {
		this.polizaCopyManager = polizaCopyManager;
	}

	public void setCargaPACManager(CargaPACManager cargaPACManager) {
		this.cargaPACManager = cargaPACManager;
	}
	
	public void setCargaCSVManager(CargaCSVManager cargaCSVManager) {
		this.cargaCSVManager = cargaCSVManager;
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setConfiguracionCamposManager(
			ConfiguracionCamposManager configuracionCamposManager) {
		this.configuracionCamposManager = configuracionCamposManager;
	}

	public void setCalculoPrecioProduccionManager(
			CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}

	public DatosParcelaManager getDatosParcelaManager() {
		return datosParcelaManager;
	}

	public void setDatosParcelaManager(DatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}
}