package com.rsi.agp.core.webapp.action.ganado;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.jmesa.service.impl.ganado.ExplotacionesAnexoService;
import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.anexoMod.ISeleccionComparativasAnexoSWManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.ganado.DatosExplotacionesAnexoManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesAnexoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;


public class ListadoExplotacionesAnexoController extends BaseMultiActionController {
	
	// Constantes
	private static final String TIPO_MODIFICACION = "tipoModificacion";
	private static final String REGIMEN = "regimen";
	private static final String ESPECIE = "especie";
	private static final String SUBEXPLOTACION = "subexplotacion";
	private static final String SIGLA = "sigla";
	private static final String REGA = "rega";
	private static final String LONGITUD = "longitud";
	private static final String LATITUD = "latitud";
	private static final String SUBTERMINO = "subtermino";
	private static final String TERMINO = "termino";
	private static final String COMARCA = "comarca";
	private static final String PROVINCIA = "provincia";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String ALERTA = "alerta";
	private static final String MENSAJE = "mensaje";
	
	private Log logger = LogFactory.getLog(ListadoExplotacionesAnexoController.class);
	private ExplotacionesAnexoService explotacionesAnexoService;
	private ExplotacionesAnexoManager explotacionesAnexoManager;
	private DatosExplotacionesAnexoManager datosExplotacionesAnexoManager;
	private AnexoModificacionManager anexoModificacionManager;
	private ISeleccionComparativasAnexoSWManager seleccionComparativasAnexoSWManager;
	private SolicitudModificacionManager solicitudModificacionManager;
	
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private final String VACIO = "";
	public static final String ORIGEN_PANTALLA_DATOS_EXPLOTACION_ANEXO = "datosExplotacionAnexo";
	public static final String ORIGEN_PANTALLA_VALIDACION_ANEXO = "validacionAnexo";

	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;

	/**
	 * Redirige a la pantalla de listado de explotaciones y realiza la primera
	 * búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param anexoModificacionBean
	 * @return
	 */
	public ModelAndView doPantallaListaExplotacionesAnexo(HttpServletRequest request, HttpServletResponse response,	AnexoModificacion anexoModificacionBean) {
		String tablaExplotacionesAnexo;
		Map<String, Object> parameters = new HashMap<String, Object>();
		logger.debug("doPantallaListaExplotacionesAnexo - Redirección a pantalla de listado de explotaciones anexo");
		
		ModelAndView result = new ModelAndView("moduloExplotaciones/explotaciones/listadoExplotacionesAnexo", "anexoModificacionBean", anexoModificacionBean);
		
		boolean hayQueRecargarBean = false;
		try {
			String origenLlamada = request.getParameter("origenLlamada");
			if(origenLlamada==null){
				origenLlamada = (String)request.getAttribute("origenLlamada");
			}
			
			String vieneDeListadoAnexosMod = request.getParameter("vieneDeListadoAnexosMod");
			parameters.put("vieneDeListadoAnexosMod", vieneDeListadoAnexosMod);
			
			if(ORIGEN_PANTALLA_DATOS_EXPLOTACION_ANEXO.equals(origenLlamada) || ORIGEN_PANTALLA_VALIDACION_ANEXO.equals(origenLlamada)){
				//Si viene de la pantalla de datos de explotación de anexo, hay que recargar el bean
				Long anexoModificacionId = Long.valueOf(request.getParameter("anexoModificacionId"));
				anexoModificacionBean = anexoModificacionManager.obtenerAnexoModificacionById(anexoModificacionId);
				result = new ModelAndView(
						"moduloExplotaciones/explotaciones/listadoExplotacionesAnexo",
						"anexoModificacionBean", anexoModificacionBean);
			}
			
			String operacion = request.getParameter("operacion");
			
			if (operacion != null && !VACIO.equals(operacion)) {
				String explotacionAnexoId = request.getParameter("explotacionAnexoId");
				
				if("borrarExplotacionAnexo".equals(operacion)){
					
					if (explotacionAnexoId != null && !VACIO.equals(explotacionAnexoId)) {
						try {
							explotacionesAnexoManager.bajaExplotacionAnexo(anexoModificacionBean.getId(), new Long(explotacionAnexoId));
							parameters.put(MENSAJE, bundle.getString("mensaje.listadoExplotaciones.borrar.OK"));
						} catch (Exception e) {
							parameters.put(ALERTA, bundle.getString("mensaje.listadoExplotaciones.borrar.KO"));
						}
					} else {
						throw new Exception("No se ha recibido el identificador de la explotación sobre la que realizar la acción.");
					}
					hayQueRecargarBean = true;
				
				}else if("deshacerExplotacionAnexo".equals(operacion)){
					
					if (explotacionAnexoId != null && !VACIO.equals(explotacionAnexoId)) {
						try {
							Long anexoModificacionId = anexoModificacionBean.getId();
							explotacionesAnexoManager.deshacerCambiosExplotacionAnexo(anexoModificacionId, new Long(explotacionAnexoId));
							anexoModificacionBean = anexoModificacionManager.obtenerAnexoModificacionById(anexoModificacionId);
							parameters.put(MENSAJE, bundle.getString("mensaje.listadoExplotaciones.deshacer.OK"));
						} catch (Exception e) {
							parameters.put(ALERTA, bundle.getString("mensaje.listadoExplotaciones.deshacer.KO"));
						}
					} else {
						throw new Exception("No se ha recibido el identificador de la explotación sobre la que realizar la acción.");
					}
					hayQueRecargarBean = true;
					
				}else if("recalcularPrecios".equals(operacion)){
					try {
						anexoModificacionBean = anexoModificacionManager.obtenerAnexoModificacionById(anexoModificacionBean.getId());
						recalcularPrecioExplotaciones(anexoModificacionBean);
						parameters.put(MENSAJE, bundle.getString("mensaje.listadoExplotaciones.precios.OK"));
					}catch (Exception e) {
						parameters.put(ALERTA, bundle.getString("mensaje.listadoExplotaciones.precios.KO"));
					}
					hayQueRecargarBean = true;
				}
			}else{
				//Desde otro controller
				hayQueRecargarBean = true;
				parameters.put(ALERTA, request.getParameter(ALERTA));
				parameters.put(MENSAJE, request.getParameter(MENSAJE));
			}
			
			String ajax = request.getParameter("ajax");
			//En caso de que la petición sea de ajax, no recargar
			if(hayQueRecargarBean && !"true".equalsIgnoreCase(ajax)){
				anexoModificacionBean = anexoModificacionManager.obtenerAnexoModificacionById(anexoModificacionBean.getId());
				logger.debug("Recargado");
			}
			
			
			
			//Antes de llamar a la parte de jMesa hay que comprobar el estado por si debe aparecer en modo lectura
			// Si el anexo está en estado 'Enviado Correcto' se visualiza el listado en modo lectura sí o sí
			String modoLectura = request.getParameter(MODO_LECTURA);
			if (Constants.ANEXO_MODIF_ESTADO_CORRECTO.equals(anexoModificacionBean.getEstado().getIdestado())){
				modoLectura = "true";
				request.setAttribute(MODO_LECTURA, modoLectura);
			}
			parameters.put(MODO_LECTURA, modoLectura);
			
			ExplotacionAnexo predicate = new ExplotacionAnexo();
			predicate.setAnexoModificacion(anexoModificacionBean);
			putFilterParametersInPredicate(request, predicate);
			tablaExplotacionesAnexo = explotacionesAnexoService.getTabla(request,
					response, predicate, origenLlamada, null, null);
			if (tablaExplotacionesAnexo == null) {
				return null;
			} else {
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = tablaExplotacionesAnexo.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					// Pasa a la jsp el codigo de la tabla a traves de este
					// atributo
					request.setAttribute("consultaExplotacionesAnexo", tablaExplotacionesAnexo);
			}
			putFilterParametersInResult(request, parameters);
			parameters.put("idpoliza", anexoModificacionBean.getPoliza().getIdpoliza());
			
			parameters.put("idpoliza", anexoModificacionBean.getPoliza().getIdpoliza());
			parameters.put("linea", anexoModificacionBean.getPoliza().getLinea().getCodlinea() + " - " + anexoModificacionBean.getPoliza().getLinea().getNomlinea());
			parameters.put("refPoliza", anexoModificacionBean.getPoliza().getReferencia());
			parameters.put("nombreAsegurado", anexoModificacionBean.getPoliza().getAsegurado().getFullName());
			parameters.put("codModulo", anexoModificacionBean.getPoliza().getCodmodulo());
			parameters.put("fechaEnvio", anexoModificacionBean.getPoliza().getFechaenvio());
			parameters.put("vieneDeUtilidades", request.getParameter("vieneDeUtilidades"));
			parameters.put("idAnexo", anexoModificacionBean.getId());
			
			// Comprueba si hay que mostrar el botón 'Importes' dependiendo del estado del cupón
			Long estadoCupon = null;
			if (anexoModificacionBean.getCupon() != null && anexoModificacionBean.getCupon().getEstadoCupon()!= null) {
				// Guarda el estado del cupón para comprobar si hay que mostrar el botón de 'Importes' en la pantalla
				estadoCupon = anexoModificacionBean.getCupon().getEstadoCupon().getId();
			}
			
			// Comprueba si hay que mostrar el botón 'Importes'
			parameters.put("mostrarImportes", Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(estadoCupon) || Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(estadoCupon));
			// Comprueba si hay que mostrar el botón 'Coberturas'
			parameters.put("mostrarBotonCoberturas", mostrarBotonCoberturas(request.getSession().getAttribute("usuario"), anexoModificacionBean.getId(), anexoModificacionBean.getPoliza().getEstadoPoliza().getIdestado()));
			// Comprueba si hay que mostrar el botón 'Cambiar datos asegurados'
			parameters.put("mostrarBotonCambiarDatosAsegurado", mostrarBotonCambiarDatosAsegurado(anexoModificacionBean));
			
			// Se recupera una instancia especifica de la entidad "Linea" a traves del DAO a partir del lineaseguroid
			com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(anexoModificacionBean.getPoliza().getLinea().getLineaseguroid().toString());
			// Obtenemos la fecha de fin de contratacion.
			Date fechaInicioContratacion = linea.getFechaInicioContratacion();
			parameters.put("fechaInicioContratacion", fechaInicioContratacion);
			
			// Setea el hidden de cambios de asegurados para los volver
			parameters.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
			
			result.addAllObjects(parameters);
		} catch (PrecioGanadoException e) {
			logger.error("Problemas en el precio del ganado", e);
		} catch (Exception e) {
			logger.error("Error en doPantallaListaExplotacionesAnexo de ListadoExplotacionesAnexoController", e);
		}
		return result;
	}


	private void recalcularPrecioExplotaciones(AnexoModificacion anexoModificacionBean) throws PrecioGanadoException {

		Set<ExplotacionAnexo> colExplotacionesAnexo = anexoModificacionBean.getExplotacionAnexos();
		Iterator<ExplotacionAnexo> itExplotacionesAnexo = colExplotacionesAnexo.iterator();
		
		while(itExplotacionesAnexo.hasNext()){
			ExplotacionAnexo explotacionAnexo = itExplotacionesAnexo.next();
			if(!Constants.BAJA.equals(explotacionAnexo.getTipoModificacion())){
				datosExplotacionesAnexoManager.calcularPrecio(explotacionAnexo);
			}
		}
	}

	private void putFilterParametersInPredicate(
			final HttpServletRequest request, final ExplotacionAnexo explotacionAnexoBean) {
		String provincia = request.getParameter(PROVINCIA);
		if (provincia != null && !VACIO.equals(provincia)) {
			explotacionAnexoBean.getTermino().getId()
					.setCodprovincia(new BigDecimal(provincia));
		}
		String comarca = request.getParameter(COMARCA);
		if (comarca != null && !VACIO.equals(comarca)) {
			explotacionAnexoBean.getTermino().getId()
					.setCodcomarca(new BigDecimal(comarca));
		}
		String termino = request.getParameter(TERMINO);
		if (termino != null && !VACIO.equals(termino)) {
			explotacionAnexoBean.getTermino().getId()
					.setCodtermino(new BigDecimal(termino));
		}
		String subtermino = request.getParameter(SUBTERMINO);
		if (subtermino != null && !VACIO.equals(subtermino)) {
			explotacionAnexoBean.getTermino().getId()
					.setSubtermino(subtermino.charAt(0));
		}
		String latitud = request.getParameter(LATITUD);
		if (latitud != null && !VACIO.equals(latitud)) {
			explotacionAnexoBean.setLatitud(Integer.valueOf(latitud));
		}
		String longitud = request.getParameter(LONGITUD);
		if (longitud != null && !VACIO.equals(longitud)) {
			explotacionAnexoBean.setLongitud(Integer.valueOf(longitud));
		}
		String rega = request.getParameter(REGA);
		if (rega != null && !VACIO.equals(rega)) {
			explotacionAnexoBean.setRega(rega);
		}
		String sigla = request.getParameter(SIGLA);
		if (sigla != null && !VACIO.equals(sigla)) {
			explotacionAnexoBean.setSigla(sigla);
		}
		String subexplotacion = request.getParameter(SUBEXPLOTACION);
		if (subexplotacion != null && !VACIO.equals(subexplotacion)) {
			explotacionAnexoBean.setSubexplotacion(Integer.valueOf(subexplotacion));
		}
		String especie = request.getParameter(ESPECIE);
		if (especie != null && !VACIO.equals(especie)) {
			explotacionAnexoBean.setEspecie(Long.valueOf(especie));
		}
		String regimen = request.getParameter(REGIMEN);
		if (regimen != null && !VACIO.equals(regimen)) {
			explotacionAnexoBean.setRegimen(Long.valueOf(regimen));
		}
		String tipoModificacion = request.getParameter(TIPO_MODIFICACION);
		if (tipoModificacion != null && !VACIO.equals(tipoModificacion)) {
			explotacionAnexoBean.setTipoModificacion(tipoModificacion.charAt(0));
		}
	}

	private void putFilterParametersInResult(final HttpServletRequest request,
			final Map<String, Object> parameters) {
		parameters.put(PROVINCIA, request.getParameter(PROVINCIA));
		parameters.put("desc_provincia", request.getParameter("desc_provincia"));
		parameters.put(COMARCA, request.getParameter(COMARCA));
		parameters.put("desc_comarca", request.getParameter("desc_comarca"));
		parameters.put(TERMINO, request.getParameter(TERMINO));
		parameters.put("desc_termino", request.getParameter("desc_termino"));
		parameters.put(SUBTERMINO, request.getParameter(SUBTERMINO));
		parameters.put(LATITUD, request.getParameter(LATITUD));
		parameters.put(LONGITUD, request.getParameter(LONGITUD));
		parameters.put(REGA, request.getParameter(REGA));
		parameters.put(SIGLA, request.getParameter(SIGLA));
		parameters.put(SUBEXPLOTACION, request.getParameter(SUBEXPLOTACION));
		parameters.put(ESPECIE, request.getParameter(ESPECIE));
		parameters.put("desc_especie", request.getParameter("desc_especie"));
		parameters.put(REGIMEN, request.getParameter(REGIMEN));
		parameters.put("desc_regimen", request.getParameter("desc_regimen"));
		parameters.put(TIPO_MODIFICACION, request.getParameter(TIPO_MODIFICACION));
	}
	
	/**
	 * Comprueba si se va a mostrar el botón 'Coberturas' para el anexo de modificación en cuestión
	 * @param usuario Usuario conectado
	 * @param idAnexo Id del anexo en cuestión
	 * @param idEstadoPlz Estado de la póliza asociada al anexo
	 * @return true: si el estado de la póliza asociada al anexo es 'Precartera generada', 'Precartera precalculada' o 'Primera comunicación' o el perfil del usuario es 0 o 
	 * 			     el anexo indicado contiene alguna explotación que tenga algún tipo de capital de retirada
	 * 		   false: en caso contrario
	 */
	private boolean mostrarBotonCoberturas(Object usuario, Long idAnexo, BigDecimal idEstadoPlz) {
		try {
			// Si el estado de la póliza asociada al anexo es 'Precartera generada', 'Precartera precalculada' o 'Primera comunicación' o el perfil del usuario es 0 o 
			// el anexo indicado contiene alguna explotación que tenga algún tipo de capital de retirada hay que mostrar el botón de 'Coberturas'
			return Constants.ESTADO_POLIZA_PRECARTERA_GENERADA.equals(idEstadoPlz) || 
				   Constants.ESTADO_POLIZA_PRECARTERA_PRECALCULADA.equals(idEstadoPlz) ||
				   Constants.ESTADO_POLIZA_PRIMERA_COMUNICACION.equals(idEstadoPlz) ||
				   Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(((Usuario) usuario).getPerfil()) || 
				   seleccionComparativasAnexoSWManager.isAnexoTCRetirada(idAnexo);
		}
		catch (Exception e) {
			logger.error("Error al comprobar si hay que mostrar el botón de comparativas", e);
		}
		
		return false;
	}
	
	private boolean mostrarBotonCambiarDatosAsegurado(AnexoModificacion anexoModificacion) {
		
		XmlObject polizaDoc = this.solicitudModificacionManager
				.getPolizaActualizadaFromCupon(anexoModificacion.getCupon().getIdcupon());
		es.agroseguro.contratacion.Poliza sitAct = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc)
				.getPoliza();
		
		return anexoModificacionManager.isAnexoAseguradoConModificaciones(anexoModificacion.getPoliza(), sitAct);
		
	}

	public void setExplotacionesAnexoService(
			final ExplotacionesAnexoService explotacionesAnexoService) {
		this.explotacionesAnexoService = explotacionesAnexoService;
	}
	
	public void setExplotacionesAnexoManager(
			ExplotacionesAnexoManager explotacionesAnexoManager) {
		this.explotacionesAnexoManager = explotacionesAnexoManager;
	}

	public void setAnexoModificacionManager(
			AnexoModificacionManager anexoModificacionManager) {
		this.anexoModificacionManager = anexoModificacionManager;
	}

	public void setDatosExplotacionesAnexoManager(DatosExplotacionesAnexoManager datosExplotacionesAnexoManager) {
		this.datosExplotacionesAnexoManager = datosExplotacionesAnexoManager;
	}


	public void setSeleccionComparativasAnexoSWManager(
			ISeleccionComparativasAnexoSWManager seleccionComparativasAnexoSWManager) {
		this.seleccionComparativasAnexoSWManager = seleccionComparativasAnexoSWManager;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
	
	public void setSolicitudModificacionManager(SolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}
	
	
}