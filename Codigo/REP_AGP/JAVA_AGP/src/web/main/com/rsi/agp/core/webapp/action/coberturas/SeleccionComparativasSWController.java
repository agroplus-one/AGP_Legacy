package com.rsi.agp.core.webapp.action.coberturas;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.ISeleccionComparativasSWManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager.ListadoEspeciesRCNumAnimales;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;

public class SeleccionComparativasSWController extends BaseMultiActionController {
	
	private static final String STRING_VACIO = "";
	private static final String COD_REGIMEN_RC = "codRegimenRC";
	private static final String NUM_ANIMALES_RC = "numAnimalesRC";
	private static final String COD_ESPECIE_RC = "codEspecieRC";
	private static final String CALCULAR_RC = "calcularRC";
	private static final String USUARIO = "usuario";
	private static final String VIENE_DE_UTILIDADES = "vieneDeUtilidades";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String VIENE_DE_IMPORTES = "vieneDeImportes";
	private static final String TRUE = "true";
	private static final String POLIZA_KEY = "poliza";
	
	private ISeleccionComparativasSWManager seleccionComparativasSWManager;
	private PolizaRCManager polizaRCManager;
	private String successView;
	
	private Log logger = LogFactory.getLog(SeleccionComparativasSWController.class);
	
	public ModelAndView doMostrarComparativas (HttpServletRequest request, 	HttpServletResponse response, Poliza poliza) {
		
		logger.debug("Inicio de SeleccionComparativasSWController");
		
		// Comprueba si hay que obtener el xml de comparativas del SW o de BBDD
		// Se cargará de BBDD cuando se esté volviendo desde la pantalla de importes o cuando se acceda en modo lectura
		boolean cargaBBDD = StringUtils.nullToString(request.getParameter(VIENE_DE_IMPORTES)).equals(TRUE) ||
							!StringUtils.nullToString(request.getParameter(MODO_LECTURA)).equals(STRING_VACIO);
		
		// Obtiene el usuario cargado en sesión para registrar en el sistema la llamada al SW
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		
		Long polizaId = poliza.getIdpoliza();
		if(StringUtils.nullToString(request.getParameter(VIENE_DE_IMPORTES)).equals(TRUE) &&
		   StringUtils.nullToString(request.getParameter(MODO_LECTURA)).equals(STRING_VACIO)) {
			
			try {
				seleccionComparativasSWManager.actualizaEstadoPoliza(polizaId, Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
			} catch (Exception e) {
				logger.error("Error al actualizar el estado de la póliza.", e);
			}
		}
		
		
		/* Pet. 63485 ** MODIF TAM (16.07.2020) ** Inicio */
		Poliza polizaMapa = null;
		Map<String, Object> mapa = new HashMap<String, Object>();
		try {
			/* Obtenemos el objetoPoliza para saber si la poliza es de ganado o no */
			Poliza polizaAux = polizaRCManager.getPoliza(polizaId);
			Boolean esGanado = polizaRCManager.esPolizaGanado(polizaAux.getLinea().getLineaseguroid());
			
			// Obtiene la lista de comparativas por módulo asociado a la póliza
			if (esGanado) {
				mapa = seleccionComparativasSWManager.generarListaComparativas(polizaId, cargaBBDD, getRealPath(), usuario);
			} else {
				mapa = seleccionComparativasSWManager.generarListaComparativasAgri(polizaId, cargaBBDD, getRealPath(), usuario);
			}
			polizaMapa = (Poliza) mapa.get(POLIZA_KEY);
		} catch (BusinessException e) {
			logger.error("Error al obtener si la póliza es de ganado.", e);
		}
			
		Long lineaseguroid = polizaMapa.getLinea().getLineaseguroid();
		Boolean esPolizaGanado = null;
		try {
			esPolizaGanado = this.polizaRCManager.esPolizaGanado(lineaseguroid);
		} catch (BusinessException e) {
			logger.error("Error al obtener si la póliza es de ganado.", e);
		}	
		mapa.put("esPolizaGanado", esPolizaGanado);
		final String perfil = usuario.getPerfil().substring(4);
		mapa.put("perfil", perfil);		
		
		if (esPolizaGanado
				&& StringUtils.nullToString(
						request.getParameter(VIENE_DE_IMPORTES)).equals(TRUE)
				&& StringUtils.nullToString(request.getParameter(MODO_LECTURA))
						.equals(STRING_VACIO)) {
			try {
				this.polizaRCManager.cambiaEstadoPolizaRC(polizaId, ConstantsRC.ESTADO_RC_BORRADOR, usuario.getCodusuario());
			} catch (Exception e) {
				logger.error("Error al actualizar el estado de la póliza de RC.", e);
			}			
		}
		
		boolean esModoLectura = StringUtils.nullToString(request.getParameter(MODO_LECTURA)).equals(MODO_LECTURA);
		if (esModoLectura){
			mapa.put(MODO_LECTURA, MODO_LECTURA);
		}
		if (StringUtils.nullToString(request.getParameter(VIENE_DE_IMPORTES)).equals(TRUE)){
			mapa.put(VIENE_DE_IMPORTES, true);
		}
		if (StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)).equals(TRUE)){
			mapa.put(VIENE_DE_UTILIDADES, true);
		}
		if(esPolizaGanado) {
			Boolean puedeCalcular = Boolean.FALSE;
			
			BigDecimal codSubentidad = usuario.getSubentidadMediadora().getId().getCodsubentidad();
			BigDecimal codEntidad    = usuario.getSubentidadMediadora().getId().getCodentidad();
			logger.debug("VALOR codSubentidad: "+ codSubentidad + "; codEntidad: " + codEntidad);
			
			String codUsuario = usuario.getCodusuario();
			try {
				puedeCalcular = this.polizaRCManager.puedeCalcularRCGanado(
						codEntidad, codSubentidad);
			} catch (BusinessException e) {
				logger.error(
						"Error al obtener si puede calcular RC de ganado.", e);
			}
			mapa.put("puedeCalcular", puedeCalcular);
			if (puedeCalcular) {
				if(esModoLectura){
					mapa.putAll(this.datosPolizaRCConsulta(polizaId));
				} else {
					Set<Explotacion> explotaciones = polizaMapa.getExplotacions();
					mapa.putAll(this.datosContratacion(explotaciones, lineaseguroid, codSubentidad, codEntidad, polizaId, codUsuario));
				}
			}
		}
		// Redirige a la pantalla de 'Selección de comparativas'
		return new ModelAndView(successView).addAllObjects(mapa);
	}
	
	private Map<String, Object> datosPolizaRCConsulta(Long polizaId) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("consultaRC", Boolean.TRUE);
		try {
			final PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(polizaId);
			if (polizaRC != null) {
				params.put("especieRC", polizaRC.getEspeciesRC());
				params.put("numAnimales", polizaRC.getNumanimales());
				params.put("regimenRCSeleccionado", polizaRC.getRegimenRC());
				params.put("idPolizaRC", polizaRC.getId());
				return params;
			}
			
		} catch (BusinessException e) {
			logger.error("Excepcion : SeleccionComparativasSWController - datosPolizaRCConsulta", e);
		}
		return params;
	}
	
	private Map<String, Object> datosContratacion(Set<Explotacion> explotaciones, Long lineaSeguroId, BigDecimal codSubentidad, 
			BigDecimal codEntidad, Long polizaId, String codUsuario) {
		try {
			ListadoEspeciesRCNumAnimales resultadoEspecies = this.polizaRCManager.getListadoEspeciesRCNumAnimales(explotaciones, lineaSeguroId);
			PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(polizaId);
			if(polizaRC != null){
				if(!resultadoEspecies.hayRegistros()){
					this.polizaRCManager.deletePolizaRC(polizaRC);
					return this.explotacionesSinDatosRC();
				}
				if(resultadoEspecies.hayRegistros() && !resultadoEspecies.esEspecieUnica()) {
					this.polizaRCManager.deletePolizaRC(polizaRC);
					return this.noEsEspecieUnicaRC();
				}
				String codEspecie = polizaRC.getEspeciesRC().getCodespecie();
				RegimenRC regimenRCSeleccionado = polizaRC.getRegimenRC();
				List<RegimenRC> regimenesRC = this.polizaRCManager.getListadoRegimenesRC(lineaSeguroId, codSubentidad, codEntidad, codEspecie);
				regimenesRC.remove(regimenRCSeleccionado);
				if(resultadoEspecies.getTotalAnimales().compareTo(polizaRC.getNumanimales()) != 0){
					polizaRC.setNumanimales(resultadoEspecies.getTotalAnimales());
					this.polizaRCManager.guardarPolizaRC(polizaRC, codUsuario);
				}
				
				/*6 Nov 2018 DNF control para que si la especie ha cambiado se cambie en BBDD*/
				if(!resultadoEspecies.getEspecie().equals(polizaRC.getEspeciesRC())){
					polizaRC.setEspeciesRC(resultadoEspecies.getEspecie());
					this.polizaRCManager.guardarPolizaRC(polizaRC, codUsuario);
				}
				
				Map<String, Object> params = this.datosRCParaVista(polizaRC.getEspeciesRC(), resultadoEspecies.getTotalAnimales(), regimenesRC);
				params.put("regimenRCSeleccionado", regimenRCSeleccionado);
				params.put("idPolizaRC", polizaRC.getId());
				return params;
			}
			if(!resultadoEspecies.hayRegistros()){
				return this.explotacionesSinDatosRC();
			}
			if(resultadoEspecies.hayRegistros() && !resultadoEspecies.esEspecieUnica()) {
				return this.noEsEspecieUnicaRC();
			}
			String codEspecie = resultadoEspecies.getEspecie().getCodespecie();
			List<RegimenRC> regimenesRC = this.polizaRCManager.getListadoRegimenesRC(lineaSeguroId, codSubentidad, codEntidad, codEspecie);
			return this.datosRCParaVista(resultadoEspecies.getEspecie(), resultadoEspecies.getTotalAnimales(), regimenesRC);
		} catch (BusinessException e) {
			logger.error("Excepcion : SeleccionComparativasSWController - datosContratacion", e);
			return this.explotacionesSinDatosRC();
		}
	}
	
	private Map<String, Object> datosRCParaVista(final EspeciesRC especieRC,
			final Long numAnimales, final List<RegimenRC> regimenRC) {
		return new HashMap<String, Object>() {
			private static final long serialVersionUID = -4204325045424025427L;
			{
				put("esEspecieUnica", Boolean.TRUE);
				put("especieRC", especieRC);
				put("numAnimales", numAnimales);
				put("regimenesRC", regimenRC);
				put("explDatosRC", Boolean.TRUE);
			}
		};
	}

	private Map<String, Object> noEsEspecieUnicaRC() {
		return new HashMap<String, Object>() {
			private static final long serialVersionUID = -6093829403182414316L;
			{
				put("esEspecieUnica", Boolean.FALSE);
				put("explDatosRC", Boolean.TRUE);
			}
		};
	}

	private Map<String, Object> explotacionesSinDatosRC() {
		return new HashMap<String, Object>() {
			private static final long serialVersionUID = -1391632703652299701L;
			{
				put("explDatosRC", Boolean.FALSE);
			}
		};
	}
	
	public ModelAndView doGuardarComparativas (HttpServletRequest request, 	HttpServletResponse response, Poliza poliza) throws BusinessException  {
		
		Long idPoliza = poliza.getIdpoliza();
		Long lineaseguroid = poliza.getLinea().getLineaseguroid();
		/* Pet. 63485 ** MODIF TAM (23/07/2020) ** Inicio */
		
		Boolean esGanado = this.polizaRCManager.esPolizaGanado(lineaseguroid);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String[] infoCoberturas = getModulosCoberturasElegidos(request, "coberturasElegidas", "#");
		String[] infoModulos = getModulosCoberturasElegidos(request, "renovElegidas", ",");
				
		logger.debug("Ganado - Guarda comparativas de la poliza: " + idPoliza + " y lineaseguroid: " + lineaseguroid);
		parameters = seleccionComparativasSWManager.guardarComparativas(idPoliza, lineaseguroid, infoModulos, infoCoberturas);
		
		if(esGanado) {		
			try {
				this.guardarPolizaRC(request, lineaseguroid, idPoliza);
			} catch (BusinessException e) {
				logger.error("Error al guardar la póliza RC.", e);
			}
		}
		
		// Si el mapa de parámetros contiene algún valor significa que han ocurrido errores al guardar las comparativas
		// Se vuelve a la pantalla de selección de comparativas y se muestran los mensajes
		if (!parameters.isEmpty()) {
			parameters.put(VIENE_DE_IMPORTES, false);
			
			return doMostrarComparativas(request, response, poliza).addAllObjects(parameters);
		}
		// Si el mapa de parámetros es vacío se llama a los servicios de validación y cálculo
		else {
			parameters.put("operacion", "validar");
			parameters.put("idpoliza", idPoliza);
			parameters.put(MODO_LECTURA, StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
			return new ModelAndView("redirect:/webservices.html", parameters);
		}
	}

	private void guardarPolizaRC(HttpServletRequest request, Long lineaseguroid, Long idPoliza) throws BusinessException {
		if(this.polizaRCManager.esPolizaGanado(lineaseguroid)){
			boolean cacularRC = Boolean.parseBoolean(request.getParameter(CALCULAR_RC));
			String idPolizaRC = request.getParameter("idPolizaRC");
			if(cacularRC){
				String codUsuario = ((Usuario)request.getSession().getAttribute(USUARIO)).getCodusuario();
				PolizasRC polizaRC = null;
				if(STRING_VACIO.equals(idPolizaRC)){
					polizaRC = this.recogerDatosPolizaRC(request, idPoliza);
				} else {
					polizaRC = this.recogerDatosPolizaRC(request, idPoliza, idPolizaRC);
				}
				this.polizaRCManager.guardarPolizaRC(polizaRC, codUsuario);
			} else {
				if(!STRING_VACIO.equals(idPolizaRC)){
					PolizasRC polizaRC = polizaRCManager.getPolizaRC(idPoliza);
					polizaRCManager.deletePolizaRC(polizaRC);
				}
			}
		}
	}
	
	private PolizasRC recogerDatosPolizaRC(HttpServletRequest req, Long idPoliza){
		PolizasRC polizaRC = new PolizasRC();
		Poliza poliza = new Poliza();
		poliza.setIdpoliza(idPoliza);
		polizaRC.setPoliza(poliza);
		polizaRC.getEspeciesRC().setCodespecie(req.getParameter(COD_ESPECIE_RC));
		polizaRC.setNumanimales(Long.valueOf(req.getParameter(NUM_ANIMALES_RC)));
		polizaRC.getRegimenRC().setCodregimen(new BigDecimal(req.getParameter(COD_REGIMEN_RC)));
		polizaRC.getEstadosRC().setId(Constants.ESTADO_RC_SIMULACION);
		return polizaRC;
	}
	
	private PolizasRC recogerDatosPolizaRC(HttpServletRequest req, Long idPoliza, String idPolizaRC){
		PolizasRC polizaRC = new PolizasRC();
		Poliza poliza = new Poliza();
		poliza.setIdpoliza(idPoliza);
		polizaRC.setId(Long.valueOf(idPolizaRC));
		polizaRC.setPoliza(poliza);
		polizaRC.getEspeciesRC().setCodespecie(req.getParameter(COD_ESPECIE_RC));
		polizaRC.setNumanimales(Long.valueOf(req.getParameter(NUM_ANIMALES_RC)));
		polizaRC.getRegimenRC().setCodregimen(new BigDecimal(req.getParameter(COD_REGIMEN_RC)));
		polizaRC.getEstadosRC().setId(Constants.ESTADO_RC_SIMULACION);
		return polizaRC;
	}
	
	/**
	 * Devuelve un array de String con los módulos y opciones de renovación elegidas
	 * @param request
	 * @return Array de String que contiene valores del tipo "codModulo#opcRenovacion"
	 */
	protected String[] getModulosCoberturasElegidos(HttpServletRequest request, String param, String charSeparador){
		
		String[] seleccionado = null;
		
		// Si se ha enviado el parámetro indicado
		if (request.getParameter(param) != null) {
			try {
				seleccionado = StringUtils.nullToString(request.getParameter(param)).split(charSeparador);
			} catch (Exception e) {
				logger.error("Ha ocurrido un error al obtener el array asociado al parámetro " + param, e);
			}
		}
		
		return seleccionado;
	}
	
	/* Pet.63485 ** MODIF TAM (20/07/2020) ** Inicio */
	public ModelAndView doDescargarFichero(HttpServletRequest request,
			HttpServletResponse response, FormFicheroComisionesBean ffcb)
			throws Exception {
		
		logger.debug("SelecccionComparativasWSController - doDescargarFichero [INIT]");
		
		
		ModelAndView mv = null;
		try {
			logger.debug("Recuperamos el objeto fichero");
				
			String idPoliza = request.getParameter("idpoliza");
			String codModulo = request.getParameter("codModulo");
				
			Clob fichero = seleccionComparativasSWManager.getxmlSWModyCobert(Long.parseLong(idPoliza), codModulo);
				
			if (fichero!= null) {
					String contenido = WSUtils.convertClob2String(fichero);

					response.setContentType("text/xml");
					String nombreFichero = "DescargaModyCob_" +idPoliza;
					response.setHeader("Content-Disposition","attachment; filename=\""+ Arrays.toString(nombreFichero.split(".xml"))+"\"");
					
					response.setHeader("cache-control", "no-cache");
					byte[] fileBytes = contenido.getBytes();
					ServletOutputStream outs = response.getOutputStream();
					outs.write(fileBytes);
					outs.flush();
					outs.close();

			}

		} catch (Exception e) {
			logger.error("Se ha producido un error al descargar el fichero", e);
		}
		return mv;

	}
	/* Pet.63485 ** MODIF TAM (20/07/2020) ** Fin */  


	/**
	 * Setter para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	/**
	 * Setter para Spring
	 * @param seleccionComparativasSWManager
	 */
	public void setSeleccionComparativasSWManager(ISeleccionComparativasSWManager seleccionComparativasSWManager) {
		this.seleccionComparativasSWManager = seleccionComparativasSWManager;
	}
	
	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}
}
