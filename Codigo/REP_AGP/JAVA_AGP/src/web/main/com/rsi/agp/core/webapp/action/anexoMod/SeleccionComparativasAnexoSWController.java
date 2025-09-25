package com.rsi.agp.core.webapp.action.anexoMod; 
 
import java.util.HashMap; 
import java.util.Map; 
 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
 
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.anexoMod.ISeleccionComparativasAnexoSWManager; 
import com.rsi.agp.core.webapp.action.BaseMultiActionController; 
import com.rsi.agp.core.webapp.action.ganado.ListadoExplotacionesAnexoController; 
import com.rsi.agp.core.webapp.util.StringUtils; 
import com.rsi.agp.dao.tables.anexo.AnexoModificacion; 
import com.rsi.agp.dao.tables.commons.Usuario; 
 
public class SeleccionComparativasAnexoSWController extends BaseMultiActionController { 
	 
	private ISeleccionComparativasAnexoSWManager seleccionComparativasAnexoSWManager; 
	private ListadoExplotacionesAnexoController listadoExplotacionesAnexoController; 
	private ConfirmacionModificacionController confirmacionModificacionController; 
	private String successView; 
	
	/*** SONAR Q ** MODIF TAM(16.12.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/
	 
	private Log logger = LogFactory.getLog(SeleccionComparativasAnexoSWController.class); 
	/** CONSTANTES SONAR Q ** MODIF TAM (17.12.2021) ** Inicio **/
	private final static String VIENE_LISTANX = "vieneDeListadoAnexosMod";
	/** CONSTANTES SONAR Q ** MODIF TAM (17.12.2021) ** Fin **/
	 
	/** 
	 * Genera las comparativas del anexo y de la poliza asociada y las envia a la pantalla 
	 * @param request 
	 * @param response 
	 * @param a 
	 * @return 
	 */ 
	public ModelAndView doMostrarComparativasAnexo (HttpServletRequest request, 	HttpServletResponse response, AnexoModificacion a) { 
		 
		// Obtiene el usuario cargado en sesion para registrar en el sistema la llamada al SW 
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
		 
		// Comprueba si se accede en modo lectura ya que en ese caso se obtienen los xml de BBDD y no del SW 
		boolean modoLectura =  StringUtils.nullToString(request.getParameter("modoLectura")).equals("true"); 
		 
		// Obtiene las comparativas del anexo y de la poliza asociada 
		Map<String, Object> mapa = new HashMap<String, Object>(); 
		try { 
			mapa = seleccionComparativasAnexoSWManager.generarListaComparativas(a.getId(), getRealPath(), usuario, modoLectura); 
		} catch (Exception exc) { 
			logger.error("Error al generar las comparativas del anexo y de la póliza asociada", exc); 
			mapa.put("alerta", "Error al generar las comparativas del anexo"); 
		} 
		
		// Setea el hidden de cambios de asegurados para los volver
		mapa.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		 
		// Pasa a la pantalla los parametros necesarios para la visualizacion 
		mapa.put("modoLectura", modoLectura); 
		mapa.put(VIENE_LISTANX, request.getParameter(VIENE_LISTANX)); 
		 
		// Redirige a la pantalla de 'Seleccion de comparativas' 
		return new ModelAndView(successView).addAllObjects(mapa); 
	} 
	 
	/** 
	 * Guarda las coberturas elegidas en pantalla y redirige al listado de explotaciones del anexo 
	 * @param request 
	 * @param response 
	 * @param a 
	 * @return 
	 * @throws DAOException 
	 * @throws BusinessException 
	 */ 
	public ModelAndView doGuardarComparativasAnexo (HttpServletRequest request,	HttpServletResponse response, AnexoModificacion a) throws DAOException, BusinessException { 
		 
		logger.debug("Guarda las comparativas elegidas"); 
		
		String[] infoModulos = getModulosCoberturasElegidos(request, "renovElegidas", ",");
		String tipologia = "";
		
		if (infoModulos.length > 0) {
			tipologia = infoModulos[0].replace("#", "");
		}
		 
		Map<String, Object> mapa = seleccionComparativasAnexoSWManager.guardarComparativas(a.getId(), getModulosCoberturasElegidos (request, "coberturasElegidas", "#"), tipologia); 
		 
		mapa.put(VIENE_LISTANX, request.getParameter(VIENE_LISTANX)); 
		
		// Setea el hidden de cambios de asegurados para los volver
		mapa.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		 
		// Si ha ocurrido algun error al guardar las comparativas vuelve a la pantalla de seleccion 
		if (mapa != null && !mapa.isEmpty() && mapa.get("alerta") != null) { 
			return doMostrarComparativasAnexo(request, response, a).addAllObjects(mapa); 
		} 
		// Si el guardado ha sido correcto 
		else { 
			// Comprueba si hay que enviar a validacion el anexo o regresar a la pantalla de listado de explotaciones 
			if (StringUtils.nullToString(request.getParameter("validarAnexo")).equals("true")) { 
				return this.confirmacionModificacionController.doValidarAnexo(request, response, a); 
			} 
			else { 
				mapa.put("mensaje", "Las comparativas se han modificado correctamente"); 
				return this.listadoExplotacionesAnexoController.doPantallaListaExplotacionesAnexo(request, response, a).addAllObjects(mapa); 
			} 
		} 
	} 
	 
	/** 
	 * Devuelve un array de String con los modulos y opciones de renovacion elegidas 
	 * @param request 
	 * @return Array de String que contiene valores del tipo "codModulo#opcRenovacion" 
	 */ 
	protected String[] getModulosCoberturasElegidos(HttpServletRequest request, String param, String charSeparador){ 
		 
		String[] seleccionado = null; 
		 
		// Si se ha enviado el parametro indicado 
		if (request.getParameter(param) != null) { 
			try { 
				seleccionado = StringUtils.nullToString(request.getParameter(param)).split(charSeparador); 
			} catch (Exception e) { 
				logger.error("Ha ocurrido un error al obtener el array asociado al parámetro " + param, e); 
			} 
		} 
		 
		return seleccionado; 
	} 
 
	public void setSeleccionComparativasAnexoSWManager(ISeleccionComparativasAnexoSWManager seleccionComparativasAnexoSWManager) { 
		this.seleccionComparativasAnexoSWManager = seleccionComparativasAnexoSWManager; 
	} 
 
	public void setSuccessView(String successView) { 
		this.successView = successView; 
	} 
 
	public void setListadoExplotacionesAnexoController( 
			ListadoExplotacionesAnexoController listadoExplotacionesAnexoController) { 
		this.listadoExplotacionesAnexoController = listadoExplotacionesAnexoController; 
	} 
 
	public void setConfirmacionModificacionController( 
			ConfirmacionModificacionController confirmacionModificacionController) { 
		this.confirmacionModificacionController = confirmacionModificacionController; 
	} 
 
} 
