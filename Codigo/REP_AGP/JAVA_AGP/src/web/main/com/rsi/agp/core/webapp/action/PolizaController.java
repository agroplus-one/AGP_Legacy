package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ICuadroCoberturasGanadoManager;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.CapitalAseguradoManager;
import com.rsi.agp.core.managers.impl.CargaCSVManager;
import com.rsi.agp.core.managers.impl.CargaPACManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class PolizaController extends BaseSimpleController implements Controller {
	
	private static final String ALERTA = "alerta";
	private static final String REDIRECT_POLIZA_CONTROLLER_HTML = "redirect:/polizaController.html";
	private static final String RECALCULAR = "recalcular";
	private static final String LISTA_MODULOS = "listaModulos";
	private static final String MODULO_POLIZAS_MODULOS_SELECCION_COMPARATIVA_NUEVO = "moduloPolizas/modulos/seleccionComparativaNuevo";
	private static final String NUMERO_MAX_COMPARATIVAS = "numeroMaxComparativas";
	private static final String COMPARATIVAS_POLIZA = "comparativasPoliza";
	private static final String TIENE_PARCELAS = "tieneParcelas";
	private static final String ORIGENLLAMADA = "origenllamada";
	private static final String VIENE_DE_UTILIDADES = "vieneDeUtilidades";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String IDPOLIZA = "idpoliza";
	private static final String ACTION = "action";
	
	private static final Log LOGGER = LogFactory.getLog(PolizaController.class);
	private PolizaManager polizaManager;
	private ICuadroCoberturasGanadoManager cuadroCoberturasGanadoManager;
	private CapitalAseguradoManager capitalAseguradoManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private CargaPACManager cargaPACManager;
	private CargaCSVManager cargaCSVManager;

	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public PolizaController (){
		setCommandClass(String.class);
		setCommandName("string");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object,final	BindException exception) throws Exception {		
		ModelAndView mv = null;	
		HashMap<String, Object> comparativas = null;
		boolean hayComprativasElegibles = false;
		List<String> modulos = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String accion = StringUtils.nullToString(request.getParameter(ACTION));		
		final Long idPoliza = Long.parseLong(request.getParameter(IDPOLIZA));
		Poliza poliza = polizaManager.getPoliza(idPoliza);
		//poliza.getModuloPolizas();
		
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECTURA));
		String vieneDeUtilidades =StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES));
		boolean isLineaGanado = poliza.getLinea().isLineaGanado();
		
		if (accion.equalsIgnoreCase("comparativa")){
			// RECUPERAMOS LOS MODULOS SELECCIONADOS	
 			String[] modSelec = getModulosSeleccionados(request, poliza);
 			
 			
 			//Solo se puede editar la poliza si esta pendiente de validacion
 			if(poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)){ 	
 				
 				//ACTUALIZAMOS LOS MODULOS DE LA POLIZA
 				modulos = polizaManager.updateModulosPoliza(poliza, modSelec,  isLineaGanado ? getModulosRenovablesSeleccionados (request, poliza) : null);
 				
 				//COMPROBAMOS QUE LA PRODUCCION Y EL PRECIO HAN SIDO CALCULADOS PARA ALGUNO DE LOS MODULOS SELECCIONADOS
 				this.compruebaProduccionPrecio(poliza, modulos);
 			}
			
 			// MPM - Ganado
 			// Obtiene el parametro que indica si hay comparativas elegibles - no hay que volver a comprobarlo 			
 			hayComprativasElegibles = "true".equals(request.getParameter("hayComprativasElegibles"));
 			
 			LinkedHashMap<String, Object> comparativasModulo = null;
 			
 			// Obtiene el mapa de comparativas correspondiente al plan/linea y modulos seleccionados
 			// Lineas de Ganado
 			if (isLineaGanado ){
 				comparativas = this.cuadroCoberturasGanadoManager.crearComparativas(poliza, modSelec);
 				comparativasModulo = (LinkedHashMap<String, Object>) comparativas.get("comparativa");
 			}
	 		
	 		int numMaxComp = polizaManager.numeroMaxComparativas(); 			
 			
			// SI HAY COMPARATIVAS ELEGIBLES, MAS DE UNA POR MODULO, SEGUIMOS EL FLUJO NORMAL DE SELECCION 
			// DE COMPARATIVAS EN OTRO CASO, NOS SALTAMOS LA PANTALLA DE COMPARATIVAS Y SELECCIONAMOS POR 
			// DEFECTO LA UNICA COMPARATIVA QUE HAY POR MODULO.
 			if(!StringUtils.nullToString(request.getParameter(ORIGENLLAMADA)).equals("listparcelas") 
 					&& StringUtils.nullToString(request.getParameter(ORIGENLLAMADA)).equals("")) {
 				if(hayComprativasElegibles) {
 	 				parameters = comparativas;
 	 				
 	 				if(poliza.getParcelas().size() > 0){
 	 	 				parameters.put(TIENE_PARCELAS, "si");
 	 	 			}else{
 	 	 				parameters.put(TIENE_PARCELAS, "no");
 	 	 			}
 	 				
 	 				parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 				parameters.put(COMPARATIVAS_POLIZA, poliza.getComparativaPolizas());
 	 				parameters.put(NUMERO_MAX_COMPARATIVAS, numMaxComp);
 	 				parameters.put(MODO_LECTURA, modoLectura);
 	 				parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
 	 				
 	 				mv = new ModelAndView(MODULO_POLIZAS_MODULOS_SELECCION_COMPARATIVA_NUEVO, LISTA_MODULOS, parameters);
 	 			} else {  
 					
 	 				// COMPROBAMOS QUE EL NUMERO DE COMPARATIVAS ELEGIDAS POR DEFECTO NO SUPERA EL MAXIMO PERMITIDO
					if (numMaxComp >= modSelec.length && (comparativasModulo == null
							|| polizaManager.isNumComparativasNoElegidasOK(comparativasModulo, numMaxComp))) {
 	 					
 	 					String seleccionados = comparativasModulo == null ? "" : polizaManager.seleccionaComparativas(comparativasModulo);
 	 					
 	 					// [inicio]Miguel 2-2-2012
 	 					String recalcular = request.getParameter(RECALCULAR);
 	 					parameters.put(RECALCULAR, recalcular);
 	 					// [fin]Miguel 2-2-2012
 	 					
 	 					parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
 	 					parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 					// Se redirige dependiendo del tipo de linea asociada a la poliza
 	 					parameters.put(ACTION, isLineaGanado ? "seleccionCompGanado" : "seleccionComp");
 	 					parameters.put("seleccionados", seleccionados);
 	 					parameters.put(MODO_LECTURA, modoLectura);
 	 					mv=  new ModelAndView(REDIRECT_POLIZA_CONTROLLER_HTML).addAllObjects(parameters);	
 	 				} else {
 	 					parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 					parameters.put(ACTION, "");
 	 					parameters.put(ALERTA, bundle.getString("mensaje.poliza.comparativa.max.KO"));
 	 					parameters.put(MODO_LECTURA, modoLectura);
 	 					
 	 					mv=  new ModelAndView(REDIRECT_POLIZA_CONTROLLER_HTML).addAllObjects(parameters);	
 	 				}
 	 			}
			}
 			else {
//				PULSADO BOTON VOLVER DE PARCELAS
 				parameters.put(MODO_LECTURA, modoLectura);
	 			parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
				if(hayComprativasElegibles){
					parameters = comparativas;
 	 				parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 				parameters.put(COMPARATIVAS_POLIZA, poliza.getComparativaPolizas());
 	 				parameters.put(NUMERO_MAX_COMPARATIVAS, numMaxComp);
 	 				mv = new ModelAndView(MODULO_POLIZAS_MODULOS_SELECCION_COMPARATIVA_NUEVO, LISTA_MODULOS, parameters);
				}else{
					mv = this.inicio(poliza, modoLectura, vieneDeUtilidades);
				}
			}
				
		} else if (accion.equalsIgnoreCase("volverAOrigenDatos")) {
			Map<String, Object> params = new HashMap<String, Object>();
			Poliza pol = new Poliza();
			Long idpoliza = Long.parseLong(request.getParameter(IDPOLIZA));
			
			//String opcion = StringUtils.nullToString(request.getParameter("seleccionOrigen"));
			pol = seleccionPolizaManager.getPolizaById(idpoliza);
			String nifCif = pol.getAsegurado().getNifcif();
			 
			// Obtenemos si tiene parcelas PAC
			List<Long> listaIdAseguradoPac = cargaPACManager.existeParcelasPACAsegurado(
									pol.getColectivo().getLinea().getCodlinea(),  // L�nea
									pol.getColectivo().getLinea().getCodplan(),   // Plan
									nifCif, 									  // Nif/Cif asegurado
									pol.getAsegurado().getEntidad().getCodentidad(), // Entidad
									pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(), // Entidad mediadora
									pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad()); // Subentidad mediadora
			
			boolean hayParcelasPac = listaIdAseguradoPac != null && !listaIdAseguradoPac.isEmpty();
			
			// Obtenemos si tiene parcelas CSV
			List<Long> listaIdAseguradoCsv = cargaCSVManager.existeParcelasCSVAsegurado(pol.getColectivo().getLinea().getCodlinea(), 
					pol.getColectivo().getLinea().getCodplan(), nifCif, pol.getAsegurado().getEntidad().getCodentidad(),
					pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(), // Entidad mediadora
					pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad()); // Subentidad mediadora);
			
			boolean hayParcelasCsv = listaIdAseguradoCsv != null && !listaIdAseguradoCsv.isEmpty();
				
			// obtenemos si el asegurado tiene polizas de la situacion actualizada
			List polizaActualizada = new ArrayList();
			polizaActualizada = polizaManager.existePolizaPlanLinea(nifCif, pol.getColectivo().getLinea().getCodplan(), 
						pol.getColectivo().getLinea().getCodlinea(), pol.getClase(), pol.getAsegurado().getEntidad().getCodentidad(),
						true);
			
			// obtenemos si el asegurado tiene polizas de los 3 ultimos planes anteriores
			List hayPolAnterior = new ArrayList();
			hayPolAnterior = polizaManager.existePolizaPlanLinea(nifCif, pol.getColectivo().getLinea().getCodplan(), 
						pol.getColectivo().getLinea().getCodlinea(), pol.getClase(), pol.getAsegurado().getEntidad().getCodentidad(),
						false);
			//Obtenemos si tiene mas polizas con el mismo asegurado,plan, linea y distinta clase,
			//con al menos una parcela
			boolean hayMultiPoliza =seleccionPolizaManager.existenPolizasDistClase(pol);
			
			if (hayParcelasPac || hayParcelasCsv || polizaActualizada.size()>0 || hayMultiPoliza || hayPolAnterior.size()>0) {
					params.put("hayMultiPoliza", hayMultiPoliza);
					params.put("hayParcelasPac", hayParcelasPac); 
					params.put("hayParcelasCsv", hayParcelasCsv); 
					if (hayParcelasPac) params.put("listaIdAseguradoPac", StringUtils.toValoresSeparadosXComas(listaIdAseguradoPac, false, false));
					if (hayParcelasCsv) params.put("listaIdAseguradoCsv", StringUtils.toValoresSeparadosXComas(listaIdAseguradoCsv, false, false));
					params.put("haySitActualizada", polizaActualizada.size()>0?true:false);
					params.put("hayPolAnterior", hayPolAnterior.size()>0?true:false);
					params.put(IDPOLIZA, poliza.getIdpoliza());
					
					logger.debug("Hay ParcelasPAC -- redirige carga parcelas" );
					mv = new ModelAndView("moduloPolizas/polizas/cargaParcelas","polizaBean", pol).addAllObjects(params);
			}
		} else if (accion.equalsIgnoreCase("seleccionComp")) { 
			
			Map<String, Object> params = new HashMap<String, Object>();
			// SI LA POLIZA ES DEFINITIVA NO PODEMOS MODIFICAR SUS COMPARATIVAS
			/// mejora 112 Angel 01/02/2012 aÃ±adida la opcion de ver la poliza sin opcion a editarla tambiÃ©n con estado grabacion definitiva
			if(!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) && 
					!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)) {
				// guardamos en sesion el idpoliza
				request.getSession().setAttribute(IDPOLIZA, poliza.getIdpoliza());
				// COMPROBAMOS QUE HAY PRECIO Y PRODUCCION PARA TODAS LAS PARCELAS
				
				String recalcular = StringUtils.nullToString(request.getParameter(RECALCULAR));
				
				if("si".equals(recalcular)){
					Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
					String realPath = this.getServletContext().getRealPath("/WEB-INF/");
					try {
						//volvemos a recargar la poliza porque se cierra la sesion
						// y en el obtenerHojaNumero falla al inicializar las parcelas
						
						poliza = polizaManager.getPoliza(idPoliza);
						seleccionPolizaManager.obtenerHojaNumero(poliza);
						seleccionPolizaManager.savePoliza(poliza);
						polizaManager.checkPrecioProduccion(poliza.getIdpoliza(), realPath, usuario);
					} catch (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e){
						String mensajeError = CalculoPrecioProduccionManager.getMsgAgrExceptionMasivo(e);
						params.put(ALERTA, mensajeError);
					} catch (Throwable e) {
						logger.error(e.getMessage());
						params.put(ALERTA, e.getMessage());
					}
				}else{
					seleccionPolizaManager.setProduccionModulosNuevos(poliza);
				}
			}
			// Comprobar si la poliza  no tiene parcelas  y hay que recuperarlas
			Poliza pol = seleccionPolizaManager.getPolizaById(poliza.getIdpoliza());
			
			if (pol.getParcelas().size() <= 0) {
				
			  	pol.setLinea(pol.getColectivo().getLinea());
			  //antes de grabar,relleno la oficina con ceros a la izq. hasta completar el maximo permitido
			  	if (!StringUtils.nullToString(pol.getOficina()).equals("")){
			  		Integer ofi = new Integer(StringUtils.nullToString(pol.getOficina()));
			  		pol.setOficina(String.format("%04d", ofi.intValue()));
			  	}
				pol.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
				EstadoPoliza estadoPoliza = new EstadoPoliza();
				estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
				pol.setEstadoPoliza(estadoPoliza);
				pol.setClase(pol.getClase());
	
				Long id = seleccionPolizaManager.savePoliza(pol);
				pol.setIdpoliza(id);
				
				String nifCif = pol.getAsegurado().getNifcif();
				
				// Obtenemos si tiene parcelas PAC
				List<Long> listaIdAseguradoPac = cargaPACManager.existeParcelasPACAsegurado(pol.getColectivo().getLinea().getCodlinea(), 
						pol.getColectivo().getLinea().getCodplan(), nifCif, pol.getAsegurado().getEntidad().getCodentidad(),
						pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(), // Entidad mediadora
						pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad()); // Subentidad mediadora);
				
				boolean hayParcelasPac = listaIdAseguradoPac != null && !listaIdAseguradoPac.isEmpty();
				
				
				// Obtenemos si tiene parcelas CSV
				List<Long> listaIdAseguradoCsv = cargaCSVManager.existeParcelasCSVAsegurado(pol.getColectivo().getLinea().getCodlinea(), 
						pol.getColectivo().getLinea().getCodplan(), nifCif, pol.getAsegurado().getEntidad().getCodentidad(),
						pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(), // Entidad mediadora
						pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad()); // Subentidad mediadora);
				
				boolean hayParcelasCsv = listaIdAseguradoCsv != null && !listaIdAseguradoCsv.isEmpty();
					
				
				// obtenemos si el asegurado tiene polizas de la situacion actualizada
				List polizaActualizada = new ArrayList();
				polizaActualizada = polizaManager.existePolizaPlanLinea(nifCif, pol.getColectivo().getLinea().getCodplan(), 
							pol.getColectivo().getLinea().getCodlinea(), pol.getClase(), pol.getAsegurado().getEntidad().getCodentidad(),
							true);
				
				// obtenemos si el asegurado tiene polizas de los 3 ultimos planes anteriores
				List hayPolAnterior = new ArrayList();
				hayPolAnterior = polizaManager.existePolizaPlanLinea(nifCif, pol.getColectivo().getLinea().getCodplan(), 
							pol.getColectivo().getLinea().getCodlinea(), pol.getClase(), pol.getAsegurado().getEntidad().getCodentidad(),
							false);
				//Obtenemos si tiene mas polizas con el mismo asegurado,plan, linea y distinta clase
				//con al menos una parcela
				boolean hayMultiPoliza =seleccionPolizaManager.existenPolizasDistClase(pol);
				
				if (hayParcelasPac || hayParcelasCsv || polizaActualizada.size()>0 || hayMultiPoliza || hayPolAnterior.size()>0) {
						
						params.put("hayMultiPoliza", hayMultiPoliza);
						params.put("hayParcelasPac", hayParcelasPac);
						params.put("hayParcelasCsv", hayParcelasCsv); 
						if (hayParcelasPac) params.put("listaIdAseguradoPac", StringUtils.toValoresSeparadosXComas(listaIdAseguradoPac, false, false));
						if (hayParcelasCsv) params.put("listaIdAseguradoCsv", StringUtils.toValoresSeparadosXComas(listaIdAseguradoCsv, false, false));
						params.put("haySitActualizada", polizaActualizada.size()>0?true:false);
						params.put("hayPolAnterior", hayPolAnterior.size()>0?true:false);
						params.put(IDPOLIZA, poliza.getIdpoliza());
						
						logger.debug("Hay ParcelasPAC -- redirige carga parcelas" );
						mv = new ModelAndView("moduloPolizas/polizas/cargaParcelas","polizaBean", pol).addAllObjects(params);
				}
				else {
					params.put(IDPOLIZA, poliza.getIdpoliza());
					params.put(MODO_LECTURA, modoLectura);
					params.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
					params.put("operacion", "listParcelas");
					
					mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(params);
				}
			}
			else {
				params.put(IDPOLIZA, poliza.getIdpoliza());
				params.put(MODO_LECTURA, modoLectura);
				params.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
				params.put("operacion", "listParcelas");
				
				mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(params);
			}
				
		} else if (accion.equalsIgnoreCase("seleccionParc")) {
			
			/*Nueva implementaci�n para cambiar el flujo DNF 31 Julio 2020 PET-63485*/
			
			// RECUPERAMOS LOS MODULOS SELECCIONADOS	
 			String[] modSelec = getModulosSeleccionados(request, poliza);
 			
 			
 			//Solo se puede editar la poliza si esta pendiente de validacion
 			if(poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)){ 	
 				
 				//ACTUALIZAMOS LOS MODULOS DE LA POLIZA
 				modulos = polizaManager.updateModulosPoliza(poliza, modSelec,  isLineaGanado ? getModulosRenovablesSeleccionados (request, poliza) : null);
 				
 				//COMPROBAMOS QUE LA PRODUCCION Y EL PRECIO HAN SIDO CALCULADOS PARA ALGUNO DE LOS MODULOS SELECCIONADOS
 				this.compruebaProduccionPrecio(poliza, modulos);
 			}
			
 			// MPM - Ganado
 			// Obtiene el parametro que indica si hay comparativas elegibles - no hay que volver a comprobarlo 			
 			hayComprativasElegibles = "true".equals(request.getParameter("hayComprativasElegibles"));
 			
 			LinkedHashMap<String, Object> comparativasModulo = null;
 			
 			// Obtiene el mapa de comparativas correspondiente al plan/linea y modulos seleccionados
 			// Lineas de Ganado
 			if (isLineaGanado ){
 				comparativas = this.cuadroCoberturasGanadoManager.crearComparativas(poliza, modSelec);
 				comparativasModulo = (LinkedHashMap<String, Object>) comparativas.get("comparativa");
 			}

	 		int numMaxComp = polizaManager.numeroMaxComparativas();
 			
// 			SI HAY COMPARATIVAS ELEGIBLES, MAS DE UNA POR MODULO, SEGUIMOS EL FLUJO NORMAL DE SELECCION 
// 			DE COMPARATIVAS EN OTRO CASO, NOS SALTAMOS LA PANTALLA DE COMPARATIVAS Y SELECCIONAMOS POR 
//          DEFECTO LA UNICA COMPARATIVA QUE HAY POR MODULO.
 			if(!StringUtils.nullToString(request.getParameter(ORIGENLLAMADA)).equals("listparcelas") 
 					&& StringUtils.nullToString(request.getParameter(ORIGENLLAMADA)).equals("")) {
 				if(hayComprativasElegibles) {
 	 				parameters = comparativas;
 	 				
 	 				if(poliza.getParcelas().size() > 0){
 	 	 				parameters.put(TIENE_PARCELAS, "si");
 	 	 			}else{
 	 	 				parameters.put(TIENE_PARCELAS, "no");
 	 	 			}
 	 				
 	 				parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 				parameters.put(COMPARATIVAS_POLIZA, poliza.getComparativaPolizas());
 	 				parameters.put(NUMERO_MAX_COMPARATIVAS, numMaxComp);
 	 				parameters.put(MODO_LECTURA, modoLectura);
 	 				parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
 	 				
 	 				mv = new ModelAndView(MODULO_POLIZAS_MODULOS_SELECCION_COMPARATIVA_NUEVO, LISTA_MODULOS, parameters);
 	 			} else {  
 					
 	 				// COMPROBAMOS QUE EL NUMERO DE COMPARATIVAS ELEGIDAS POR DEFECTO NO SUPERA EL MAXIMO PERMITIDO
					if (numMaxComp >= modSelec.length && (comparativasModulo == null
							|| polizaManager.isNumComparativasNoElegidasOK(comparativasModulo, numMaxComp))) {
 	 					
 	 					String seleccionados = comparativasModulo == null ? "" : polizaManager.seleccionaComparativas(comparativasModulo);
 	 					
 	 					String recalcular = request.getParameter(RECALCULAR);
 	 					parameters.put(RECALCULAR, recalcular);
 	 					parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
 	 					parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 					parameters.put(ACTION, isLineaGanado ? "seleccionCompGanado" : "seleccionComp");
 	 					parameters.put("seleccionados", seleccionados);
 	 					parameters.put(MODO_LECTURA, modoLectura);
 	 					mv=  new ModelAndView(REDIRECT_POLIZA_CONTROLLER_HTML).addAllObjects(parameters);	
 	 				} else {
 	 					parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 					parameters.put(ACTION, "");
 	 					parameters.put(ALERTA, bundle.getString("mensaje.poliza.comparativa.max.KO"));
 	 					parameters.put(MODO_LECTURA, modoLectura);
 	 					
 	 					mv=  new ModelAndView(REDIRECT_POLIZA_CONTROLLER_HTML).addAllObjects(parameters);	
 	 				}
 	 			}
			} else {
//				PULSADO BOTON VOLVER DE PARCELAS
 				parameters.put(MODO_LECTURA, modoLectura);
	 			parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
				if(hayComprativasElegibles){
					parameters = comparativas;
 	 				parameters.put(IDPOLIZA, poliza.getIdpoliza());
 	 				parameters.put(COMPARATIVAS_POLIZA, poliza.getComparativaPolizas());
 	 				parameters.put(NUMERO_MAX_COMPARATIVAS, numMaxComp);
 	 				mv = new ModelAndView(MODULO_POLIZAS_MODULOS_SELECCION_COMPARATIVA_NUEVO, LISTA_MODULOS, parameters);
				}else{
					mv = this.inicio(poliza, modoLectura, vieneDeUtilidades);
				}
			}
			
			
		}
		// Seleccion y guardado de comparativas para polizas de Ganado + redireccion a listado de explotaciones
		else if (accion.equalsIgnoreCase("seleccionCompGanado")) { 
		
			mv = seleccionarComparativaGanado(request, poliza);
		
			
		} else {			
			
			if(isLineaGanado) {
				if(	poliza.getModuloPolizas()==null || poliza.getModuloPolizas().size()==0){
					polizaManager.asignacionModulosGanado(poliza);
				}
				// Si es MODO CONSULTA se redirige directamente a la pantalla Listado de explotaciones.
				if(!modoLectura.equals(MODO_LECTURA) && null==poliza.getIdCargaExplotaciones()) {
					//se redirigira a la nueva pantalla Carga de explotaciones (especificada en el mismo punto).
					//Esta redireccion se realizara invocando al metodo 
					//CargaExplotacionesController.doMenuCargaExplotaciones pasando como parametro 
					//el id de poliza en cuestion.
					parameters.put(IDPOLIZA, poliza.getIdpoliza());
					//parameters.put("method", "doMenuCargaExplotaciones");
					mv= new ModelAndView("redirect:/cargaExplotaciones.html").addObject(
							"method", "doMenuCargaExplotaciones").addAllObjects(parameters);
					
					return mv;
				} else {
					//Si la poliza es de linea de ganado y el nuevo campo ID_CARGA_EXPLOTACIONES esta informado se redirigira 
					//a la pantalla Listado de explotaciones.
					// Parametros para la pantalla de datos de la explotacion
					parameters.put(IDPOLIZA, poliza.getIdpoliza());
					parameters.put("linea.lineaseguroid", poliza.getLinea().getLineaseguroid());
					parameters.put("linea.codlinea", poliza.getLinea().getCodlinea());
					parameters.put("linea.codplan", poliza.getLinea().getCodplan());
					parameters.put(MODO_LECTURA, modoLectura);
					parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
					parameters.put(ORIGENLLAMADA, "seleccionPoliza");
					parameters.put("ajax", "false");
					
					return new ModelAndView("redirect:/listadoExplotaciones.html").addAllObjects(parameters);
				}
			} else {
			
				// Comprueba si ya hay comparativas guardadas para la poliza
				boolean hayModulos = polizaManager.checkModulosPoliza(poliza);
				
				//parcelasWeb es true solo si viene de la web de parcelas
				String parcelasWeb = StringUtils.nullToString(request.getParameter("parcelasWeb"));
				
				//Si vienen de utilidades es que ha pulsado editar poliza desde utilidades
				if ((hayModulos) && !parcelasWeb.equals("true")) {
				
					parameters.put(IDPOLIZA, poliza.getIdpoliza());
					parameters.put(MODO_LECTURA, modoLectura);
					parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
					parameters.put("operacion", "listParcelas");
					
					if(poliza.getParcelas().size() > 0) {
		 	 		    parameters.put(TIENE_PARCELAS, "si");
		 	 		} else {
		 	 			parameters.put(TIENE_PARCELAS, "no");
		 	 		}
					
					mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parameters);
					
				} else {
					
					LOGGER.info("Redireccionamos a la pantalla de eleccion de modulos para la poliza " + poliza.getIdpoliza());
					mv = this.inicio(poliza, modoLectura, vieneDeUtilidades);
					
					if(poliza.getParcelas().size() > 0){
						mv.addObject(TIENE_PARCELAS, "si");
		 	 		}else{
		 	 			mv.addObject(TIENE_PARCELAS, "no");
		 	 		}
					
					// MENSAJE DE ERROR EN CASO DE QUE SUEPEREMOS EL NUMERO MAXIMO DE COMPARATIVAS POSIBLES
					if(!StringUtils.nullToString(request.getParameter(ALERTA)).equals("")){
						mv.addObject(ALERTA, StringUtils.nullToString(request.getParameter(ALERTA)));
					}
				}
			}
		}
		
		return mv;
	}

	
	/**
	 * Polizas de Ganado - Guarda la comparativa elegida y redirige a la pantalla de listado de explotaciones
	 * @param request
	 * @param poliza
	 * @return
	 */
	private ModelAndView seleccionarComparativaGanado(final HttpServletRequest request, Poliza poliza) {
		
		// Si la poliza tiene estado 'Grabacion definitiva' o 'Enviada correcta' no se modifican las comparativas
		if(!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) && 
				!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)) {
			
			// Obtiene los datos de la comparativa seleccionada
			String[] seleccionados = StringUtils.nullToString(request.getParameter("seleccionados")).split(";");
			
			// Borra las anteriores comparativas asociadas a la poliza 
			try {
				polizaManager.borraComparativa(poliza);
			} catch (DAOException e) {
				logger.error("Error al borrar las comparativas",e);
			}
			
			// Guarda las comparativas asociadas a la poliza seleccionadas
			Set<ComparativaPoliza> comparativaPolizas = new HashSet<ComparativaPoliza>(seleccionados.length);
			comparativaPolizas = seleccionCompNUEVO(seleccionados, poliza);
			poliza.setComparativaPolizas((Set<ComparativaPoliza>) comparativaPolizas);
		}
		
		// Redirige a la pantalla de explotaciones
		logger.debug("Redirige a la pantalla de explotaciones" );
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(IDPOLIZA, poliza.getIdpoliza());
		parameters.put("linea.lineaseguroid", poliza.getLinea().getLineaseguroid());
		parameters.put("linea.codlinea", poliza.getLinea().getCodlinea());
		parameters.put("linea.codplan", poliza.getLinea().getCodplan());
		if(request.getParameter(MODO_LECTURA)!=null)
			parameters.put(MODO_LECTURA, request.getParameter(MODO_LECTURA));
		if(request.getParameter(VIENE_DE_UTILIDADES)!=null)
			parameters.put(VIENE_DE_UTILIDADES, request.getParameter(VIENE_DE_UTILIDADES));
		parameters.put(ORIGENLLAMADA, "seleccionPoliza");
		parameters.put("ajax", "false");
		
		return new ModelAndView("redirect:/listadoExplotaciones.html").addAllObjects(parameters);
		
		
		//return new ModelAndView("moduloExplotaciones/explotaciones/listadoExplotaciones", "polizaBean", poliza).addObject("idpoliza", request.getParameter("idpoliza"));
	}

	/**
	 * Metodo que devulve un String array con los modulos seleccionados o el modulo de la poliza
	 * @param request
	 * @param poliza
	 * @return
	 */
	private String[] getModulosSeleccionados(HttpServletRequest request,Poliza poliza){
		String[] modSelec = StringUtils.nullToString(request.getParameter("activados")).split(",");
		
		if(modSelec != null && modSelec.length > 0 && "".equals(modSelec[0])) {
			modSelec = new String[poliza.getModuloPolizas().size()];
			int i = 0;
			for(ModuloPoliza modPoliza : poliza.getModuloPolizas()){
				modSelec[i]=modPoliza.getId().getCodmodulo();
				i++;
			}
			modSelec = ordenarModulos(modSelec);
		}
		
		return modSelec;
	}
	
	/**
	 * Devuelve un array de String con los modulos y opciones de renovacion elegidas
	 * @param request
	 * @return Array de String que contiene valores del tipo "codModulo#opcRenovacion"
	 */
	private String[] getModulosRenovablesSeleccionados(HttpServletRequest request, Poliza p){
		
		String[] modSelec = null;
		
		// Si se ha enviado el parametro con las opciones elegidas
		if (request.getParameter("renovElegidas") != null) {
			try {
				modSelec = StringUtils.nullToString(request.getParameter("renovElegidas")).split(",");
			} catch (Exception e) {
				logger.error("Ocurrio un error al obtener el array de modulos renovables", e);
			}
		}
		// Si no, comprueba si ya se han elegido modulos previamente (en ese caso se esta volviendo a la pantalla de coberturas desde la de explotaciones)
		else {
			Set<ModuloPoliza> setmp = p.getModuloPolizas();
			if (setmp != null && !setmp.isEmpty()) {
				modSelec = new String[setmp.size()];
				
				int i = 0;
				for (ModuloPoliza mp : p.getModuloPolizas()) {
					modSelec[i] = mp.getId().getCodmodulo() + "#" + mp.getRenovable();
				}
			}
		}
		
		return modSelec;
	}
	
	/**
	 * Metodo que comprueba si han sido calculados producciones/precios para algun modulo seleccionado
	 * @param poliza
	 * @param modulos
	 * @throws BusinessException
	 */
	private void compruebaProduccionPrecio(Poliza poliza,List<String> modulos) throws BusinessException{
		for (Parcela parcela : poliza.getParcelas()) {
			for (CapitalAsegurado capAseg : parcela.getCapitalAsegurados()) {
				Set<CapAsegRelModulo> listCapAsegRelMod = capAseg.getCapAsegRelModulos();
				List<CapAsegRelModulo> auxCapAsegRelMod = new ArrayList<CapAsegRelModulo>();
				if (listCapAsegRelMod != null && listCapAsegRelMod.size() > 0) {
					//	Se copian todos los elementos del set a una lista para recorrerlos a partir de su indice
					//	Esto se hace para evitar ConcurrentModificationException
					for(CapAsegRelModulo capAsegRelMod : listCapAsegRelMod){
						auxCapAsegRelMod.add(capAsegRelMod);
					}
					//	Se recorre la lista auxiliar que se ha creado										
					for(int cont=0; cont<auxCapAsegRelMod.size(); cont++){
						if (!modulos.contains(auxCapAsegRelMod.get(cont).getCodmodulo())) {
							listCapAsegRelMod.remove(auxCapAsegRelMod.get(cont));
							capitalAseguradoManager.deleteCapAsegRelModById(auxCapAsegRelMod.get(cont).getId());	
						}
					}
				}
			}
		}
	}
	
	private ComparativaPoliza crearComparativaPoliza(String comparativaSeleccionada, Poliza poliza){
		
		String[] comparativaSeleccionadaTroceada = comparativaSeleccionada.split("\\|");
		
		ComparativaPoliza compP = new ComparativaPoliza();
		ComparativaPolizaId compID = new ComparativaPolizaId();
		Long auxIdcom= null;
		for (ModuloPoliza mp :poliza.getModuloPolizas()){
			if (mp.getId().getCodmodulo().equals(comparativaSeleccionadaTroceada[0])){
				auxIdcom = mp.getId().getNumComparativa();
				break;
			}
		}
		compID.setIdComparativa(auxIdcom);
		compID.setIdpoliza(poliza.getIdpoliza());
		compID.setLineaseguroid(poliza.getLinea().getLineaseguroid());
		compID.setCodmodulo(comparativaSeleccionadaTroceada[0]);
		compID.setFilamodulo(new BigDecimal(comparativaSeleccionadaTroceada[1]));
		compID.setCodconceptoppalmod(new BigDecimal(comparativaSeleccionadaTroceada[2]));
		compID.setCodriesgocubierto(new BigDecimal(comparativaSeleccionadaTroceada[3]));
		compID.setCodconcepto(new BigDecimal(comparativaSeleccionadaTroceada[4]));
		compID.setCodvalor(new BigDecimal(comparativaSeleccionadaTroceada[5]));
		compID.setFilacomparativa(new BigDecimal(comparativaSeleccionadaTroceada[6]));
		
		ConceptoPpalModulo ppalMod = new ConceptoPpalModulo();
		ppalMod.setCodconceptoppalmod(new BigDecimal(comparativaSeleccionadaTroceada[2]));
		compP.setConceptoPpalModulo(ppalMod);
		
		if(comparativaSeleccionadaTroceada.length > 7)
			compP.setDescvalor(comparativaSeleccionadaTroceada[7]);
		else
			compP.setDescvalor("");
			
		RiesgoCubierto riesgo = new RiesgoCubierto();
		RiesgoCubiertoId idRiesgo = new RiesgoCubiertoId();
		idRiesgo.setCodriesgocubierto(new BigDecimal(comparativaSeleccionadaTroceada[3]));
		riesgo.setId(idRiesgo);
		compP.setRiesgoCubierto(riesgo);												
		compP.setId(compID);
		compP.setPoliza(poliza);
		return compP;
		
	}
	
	private Set<ComparativaPoliza> seleccionCompNUEVO(String[] seleccionados, Poliza poliza){
		
		Set<ComparativaPoliza> comparativaPolizas = new HashSet<ComparativaPoliza>(seleccionados.length);

		for (int i=0; i<seleccionados.length; i++){

			ComparativaPoliza comparativa = crearComparativaPoliza(seleccionados[i], poliza);
			
			boolean existeComparativa = polizaManager.existeComparativa(comparativa.getId());
			if(!existeComparativa){
				polizaManager.guardaComparativasSeleccionadas(comparativa);
				comparativaPolizas.add(comparativa);
			}
		}
		return comparativaPolizas;		
	}
	
	private ModelAndView inicio (Poliza poliza,String modoLectura,String vieneDeUtilidades) {
		Map<String, Object> params = new HashMap<String, Object>();
				
		params = polizaManager.dameListaModulos(poliza, true);
		
		params.put(IDPOLIZA, poliza.getIdpoliza().toString());
		params.put("moduloPolizas", poliza.getModuloPolizas());
		params.put(MODO_LECTURA, modoLectura);
		params.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
		params.put("comparativasNoElegibles", false);
		params.put("esLineaGanado", poliza.getLinea().isLineaGanado());
		
		return new ModelAndView("moduloPolizas/modulos/elecmodulos", "detCoberturas", params);		
	}

	public String[] ordenarModulos(String[] mod){
		String [] modOrden = new String[mod.length];
		LinkedHashMap<String, String> hmMod = new LinkedHashMap<String, String>();
		
		for(String codmodulo : mod){
			if("1".equals(codmodulo)){
				hmMod.put(codmodulo, codmodulo);
				break;
			}
		}
		for(String codmodulo : mod){
			if("2".equals(codmodulo)){
				hmMod.put(codmodulo, codmodulo);
				break;
			}
		}
		for(String codmodulo : mod){
			if("3".equals(codmodulo)){
				hmMod.put(codmodulo, codmodulo);
				break;
			}
		}
		for(String codmodulo : mod){
			if("P".equals(codmodulo)){
				hmMod.put(codmodulo, codmodulo);
				break;
			}
		}
		int i=0;
		for(String key : hmMod.keySet()){
			modOrden[i] = hmMod.get(key);
			i++;
		}
		return modOrden;
	}
	
	/* SETTERS MANAGERS
	 ----------------------------------------------------------------- */
	
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setCapitalAseguradoManager(	CapitalAseguradoManager capitalAseguradoManager) {
		this.capitalAseguradoManager = capitalAseguradoManager;
	}

	public SeleccionPolizaManager getSeleccionPolizaManager() {
		return seleccionPolizaManager;
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public CargaPACManager getCargaPACManager() {
		return cargaPACManager;
	}

	public void setCargaPACManager(CargaPACManager cargaPACManager) {
		this.cargaPACManager = cargaPACManager;
	}

	public void setCuadroCoberturasGanadoManager(
			ICuadroCoberturasGanadoManager cuadroCoberturasGanadoManager) {
		this.cuadroCoberturasGanadoManager = cuadroCoberturasGanadoManager;
	}
	
	public CargaCSVManager getCargaCSVManager() {
		return cargaCSVManager;
	}

	public void setCargaCSVManager(CargaCSVManager cargaCSVManager) {
		this.cargaCSVManager = cargaCSVManager;
	}

	
}