package com.rsi.agp.core.webapp.action; 
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.impl.CoberturasModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesModificacionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.action.ganado.ListadoExplotacionesAnexoController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.CoberturaSeleccionada;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.modulosYCoberturas.ModulosYCoberturas; 
 
public class CoberturasModificacionPolizaController extends	BaseMultiActionController { 
 
	private Log logger = LogFactory.getLog(CoberturasModificacionPolizaController.class); 
	private CoberturasModificacionPolizaManager coberturasModificacionPolizaManager; 
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager; 
	private ExplotacionesModificacionPolizaManager explotacionesModificacionPolizaManager; 
	 
	private ParcelasModificacionPolizaController parcelasModificacionPolizaController; 
	private ListadoExplotacionesAnexoController listadoExplotacionesAnexoController; 
	 
	private ResourceBundle bundle = ResourceBundle.getBundle("agp"); 
 
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception { 
		 
		ModelAndView resultado = null; 
		Map<String, Object> parametros = new HashMap<String, Object>(); 
		Map<String, String> tablaPoliza = new HashMap<String, String>(); 
		Modulo modAnexo; 
		List<Modulo> modulosPoliza = null; 
		Poliza poliza = null; 
		List<CoberturaSeleccionada> lstCob = new ArrayList<CoberturaSeleccionada>(); 
		String codModuloAnexo = null;
		
		try { 
			Long idAnexo = anexoModificacion.getId(); 
			if (idAnexo == null || idAnexo.compareTo(0L) <= 0){ 
				//si el id no viene en el anexo, lo recupero del request 
				idAnexo = Long.parseLong(request.getParameter("idAnexo")); 
			}
			
			Long idPoliza = null;
			if(null != request.getParameter("idPoliza")) {
				idPoliza = Long.parseLong(request.getParameter("idPoliza")); 
			}
			if (null == idPoliza && null != request.getParameter("poliza.idpoliza")) {
				idPoliza = Long.parseLong(request.getParameter("poliza.idpoliza")); 
			}
			
			codModuloAnexo = request.getParameter("codModuloAnexo");
			
			String tipoModo = request.getParameter("tipoModo"); 
			String modoLectura = request.getParameter("modoLectura"); 
			poliza = declaracionesModificacionPolizaManager.getPoliza(idPoliza); 
			anexoModificacion = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);  
			modulosPoliza = coberturasModificacionPolizaManager.getModulosDisponibles(poliza.getLinea().getLineaseguroid()); 
			
			if(null == codModuloAnexo) {
				codModuloAnexo = anexoModificacion.getCodmodulo();
			}
			
			/*** DNF 04/02/2021 pet.63485.FIII*/ 
			String realPath = this.getServletContext().getRealPath("/WEB-INF/"); 
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
			tablaPoliza = coberturasModificacionPolizaManager.getCoberturasPoliza(poliza, anexoModificacion, lstCob, realPath, usuario);  
			/*** fin DNF 04/02/2021 pet.63485.FIII*/ 
			
			if (!StringUtils.nullToString(anexoModificacion.getCodmodulo()).equals("")){ 
				modAnexo = coberturasModificacionPolizaManager.getModulo(poliza.getLinea().getLineaseguroid(), anexoModificacion.getCodmodulo()); 
			} 
			else{ 
				modAnexo = coberturasModificacionPolizaManager.getModulo(poliza.getLinea().getLineaseguroid(), poliza.getCodmodulo()); 
			} 
			 
			String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter("vieneDeListadoAnexosMod")); 
			parametros.put("vieneDeListadoAnexosMod", vieneDeListadoAnexosMod); 
			 
			parametros.put("modulos", modulosPoliza); 
			parametros.put("tablaPoliza", tablaPoliza.get("tabla")); 
			 
			//DAA anadir parametro tablaAnexoCabecera con cod modulo y descripcion del modulo elegido del anexo en caso de que tenga  
			//y si no el de la poliza (tablaPoliza.get("cabecera")); 
			parametros.put("tablaAnexoCabecera",  modAnexo.getId().getCodmodulo() + " - " + modAnexo.getDesmodulo()); 
			 
			parametros.put("tablaPolizaCabecera", tablaPoliza.get("cabecera")); 
			parametros.put("modPol", modAnexo.getId().getCodmodulo()); 
			parametros.put("coberturas", lstCob); 
			parametros.put("tipoModo", tipoModo ); 
			parametros.put("modoLectura", modoLectura); 
			parametros.put("idAnexo", idAnexo); 
			parametros.put("codModuloAnexo", codModuloAnexo);
			parametros.put("codigoModuloPoliza", poliza.getCodmodulo());
			
			// Setea el hidden de cambios de asegurados para los volver
			parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
			
			if(modAnexo.getLinea().isLineaGanado()){ 
				//Para saltarse la pantalla de coberturas 
				//resultado = new ModelAndView(new RedirectView("coberturasModificacionPoliza.html"), "anexoModificacion", anexoModificacion).addAllObjects(parametros); 
				resultado = doContinua(request, response, anexoModificacion); 
			}else{ 
				resultado = new ModelAndView("/moduloUtilidades/modificacionesPoliza/coberturasAnexoModificacion","anexoModificacion", anexoModificacion).addAllObjects(parametros); 
			} 
			 
		} catch (BusinessException be) { 
			logger.error("Se ha producido un error al cargar las coberturas de la p&oacute;liza",be); 
		} catch (Exception be) { 
			logger.error("Se ha producido un error inesperado al cargar las coberturas de la p&oacute;liza",be); 
		} 
		 
		return resultado; 
	} 
     
	public ModelAndView doVolver(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception { 
		ModelAndView mv = null; 
		Long idPoliza = null; 
		 
		try { 
			String vieneDeListadoAnexosMod = request.getParameter("vieneDeListadoAnexosMod"); 
			if ("true".equals(vieneDeListadoAnexosMod)) { 
				Map<String, Object> parametros = new HashMap<String, Object>(); 
				parametros.put("volver", true); 
				 
				return new ModelAndView("redirect:/anexoModificacionUtilidades.run").addAllObjects(parametros); 
			}else{ 
				idPoliza= anexoModificacion.getPoliza().getIdpoliza(); 
				mv = new ModelAndView(new RedirectView("declaracionesModificacionPoliza.html")).addObject("idPoliza",idPoliza); 
			} 
			 
		} catch (Exception ex) { 
			logger.error("Se ha producido un error al volver a la pantalla de anexo",ex); 
		} 
		 
		return mv; 
	} 
	
	/*** PET63485.FIII DNF 29/01/2021 Metodo que guarda las coberturas del anexo */
	public void grabacionCoberturasAM(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception { 

		JSONObject resultado = new JSONObject();
		
		String modoLectura = StringUtils.nullToString(request.getParameter("modoLectura")); 
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String coberturasModificadas = StringUtils.nullToString(request.getParameter("coberturasModificadas"));
		String codModuloAnexo = StringUtils.nullToString(request.getParameter("codModuloAnexo"));
		Long idPoliza = Long.parseLong(request.getParameter("idPoliza"));
		
		Poliza poliza = declaracionesModificacionPolizaManager.getPoliza(idPoliza); 
		Long idAnexo = new Long(0); 
		if (!"".equals(StringUtils.nullToString(anexoModificacion.getId()))){ 
			idAnexo = anexoModificacion.getId(); 
		}
		if(idAnexo == 0) {
			idAnexo = Long.parseLong(request.getParameter("idAnexo"));
		}
		anexoModificacion = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo); 
		
		String idCupon = "";
		
		if(anexoModificacion.getCupon()!=null){ 
			idCupon = StringUtils.nullToString(anexoModificacion.getCupon().getId()); 
		} 
		resultado.put("idCupon", idCupon);
		
		request.setAttribute("vieneDeCoberturasAnexo", "true"); 
		HashMap<String,HashMap<String, Cobertura>> coberturas = this.getCoberturasAnexo(request);
		
		if(!"true".equals(modoLectura)){ 
			try{ 
				anexoModificacion.setAsunto(XmlTransformerUtil.generarAsuntoAnexo(anexoModificacion, false)); 
				coberturasModificacionPolizaManager.guardarCoberturas(anexoModificacion, poliza, usuario, coberturasModificadas, coberturas, codModuloAnexo);
				resultado.put("isGrabacionCorrecta", "true");
				
			}catch (ValidacionAnexoModificacionException ea){ 
				logger.error("Error al validar el xml de Anexos de Modificaci&oacute;n ", ea);  
				resultado.put("isGrabacionCorrecta", "false");
				resultado.put("alerta", "Error validando el xml de Anexos de Modificaci&oacute;n : " + ea.getMessage()); 
			} 
		}
		getWriterJSON(response, resultado);
	} 
	
	/** 
	 * Metodo para guardar la eleccion de coberturas del anexo y acceder a la pantalla de parcelas del anexo 
	 * @param request 
	 * @param response 
	 * @param anexoModificacion 
	 * @return 
	 * @throws Exception 
	 */ 
	public ModelAndView doContinua(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception { 
		 
		ModelAndView modelAndView = null; 
		Map<String, Object> parametros = new HashMap<String, Object>(); 
		String modoLectura = request.getParameter("modoLectura"); 
		 
		Long idAnexo = new Long(0); 
		 
		if (!"".equals(StringUtils.nullToString(anexoModificacion.getId()))){ 
			idAnexo = anexoModificacion.getId(); 
		} 
         
		anexoModificacion = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo); 
		//DAA 05/07/2013 si el modulo del anexo de la BBDD es nulo tengo que meter el de la poliza. 
		if(anexoModificacion.getCodmodulo()==null){ 
			anexoModificacion.setCodmodulo(anexoModificacion.getPoliza().getCodmodulo()); 
		} 
		parametros.put("idAnexo", anexoModificacion.getId()); 
		parametros.put("idPoliza", anexoModificacion.getPoliza().getIdpoliza()); 
		parametros.put("modoLectura",modoLectura); 
		String vieneDeListadoAnexosMod = request.getParameter("vieneDeListadoAnexosMod"); 
		parametros.put("vieneDeListadoAnexosMod", vieneDeListadoAnexosMod); 
		request.setAttribute("vieneDeCoberturasAnexo", "true"); 
		 
		boolean esLineaGanado = anexoModificacion.getPoliza().getLinea().isLineaGanado(); 
		 
		// si no esta visualizando lo guardo 
		if(!"true".equals(modoLectura)){ 
			Poliza poliza = declaracionesModificacionPolizaManager.getPoliza(anexoModificacion.getPoliza().getIdpoliza()); 
			try{ 
				Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
				String coberturasModificadas = request.getParameter("coberturasModificadas"); 
				HashMap<String,HashMap<String, Cobertura>> coberturas = this.getCoberturasAnexo(request); 
				//TMR. Mejora 230 actualizamos el campo asunto anadiendo las coberturas del xml de anexo de modificacion 
			    anexoModificacion.setAsunto(XmlTransformerUtil.generarAsuntoAnexo(anexoModificacion, false)); 
			    //Guardamos las coberturas y los datos del anexo 
			    
			    /***DNF PET.63485.FIII 15/01/2021 necesito saber el comboModulo seleccionado en el combo del anexo para modificar 
			     * las coberturas y se lo mando a la funcion guardarCoberturas ... */
			    
			    String comboModulo = request.getParameter("comboModulo"); 
			    boolean isGrabado = coberturasModificacionPolizaManager.guardarCoberturas(anexoModificacion, poliza, usuario, coberturasModificadas, coberturas, comboModulo); 
			    if (isGrabado) 
			    	parametros.put("mensaje", "Coberturas guardadas correctamente");
			    
			    /***fin DNF PET.63485.FIII 15/01/2021 */
			}catch (ValidacionAnexoModificacionException ea){ 
				logger.error("Error validando el xml de Anexos de Modificaci&oacute;n ", ea); 
				parametros.put("alerta", "Error validando el xml de Anexos de Modificaci&oacute;n : " + ea.getMessage()); 
				 
				if(esLineaGanado){ 
					modelAndView = new ModelAndView(new RedirectView("explotacionesAnexoModificacion.html"),"anexoModificacion", anexoModificacion).addAllObjects(parametros); 
				}else{ 
					modelAndView = new ModelAndView(new RedirectView("parcelasAnexoModificacion.html"),"anexoModificacion", anexoModificacion).addAllObjects(parametros);	 
				} 
				return modelAndView; 
			} 
		     
		    //declaracionesModificacionPolizaManager.saveAnexoModificacion(anexoModificacion,usuario.getCodusuario(),null,false); 
		} 
 
		if(esLineaGanado){ 
			//P00019224@015 El equivalente a parcelasModificacionPolizaController.doConsulta para obtener si procede las explotaciones de anexo actualizadas 
	        String idCupon = ""; 
	        String idCuponStr = ""; 
			request.setAttribute("origenLlamada", "cupones"); 
			 
			try { 
				if(!StringUtils.nullToString(idAnexo).equals("")){ 
					 
					if(anexoModificacion.getCupon()!=null){ 
						idCupon = StringUtils.nullToString(anexoModificacion.getCupon().getId()); 
						idCuponStr = StringUtils.nullToString(anexoModificacion.getCupon().getIdcupon()); 
						 
						if (StringUtils.nullToString(idCupon).equals("")){ 
							idCupon = StringUtils.nullToString(request.getAttribute("idCupon")); 
						} 
						if (StringUtils.nullToString(idCuponStr).equals("")){ 
							idCuponStr = StringUtils.nullToString(request.getParameter("idCuponStr")); 
						} 
					} 
					 
					if (!StringUtils.nullToString(idCupon).equals("")){ 
						explotacionesModificacionPolizaManager.copiarExplotacionesAnexoFromPolizaActualizada( 
								anexoModificacion.getPoliza().getIdpoliza(), 
								anexoModificacion.getId(), 
								idCupon, 
								anexoModificacion.getPoliza().getLinea().getLineaseguroid()); 
					} 
 
					parametros.put("idAnexo", idAnexo); 
				} 
			}  
			catch(Exception ex){ 
				logger.error("[doConsulta] Se ha producido un error durante la consulta de explotaciones de anexo", ex); 
				parametros.put("alerta", bundle.getString("mensaje.error.general")); 
			} 
 
			return listadoExplotacionesAnexoController.doPantallaListaExplotacionesAnexo(request, response, anexoModificacion).addAllObjects(parametros); 
			 
		}else{ 
			CapitalAsegurado capitalAseguradoModificadaBean = new CapitalAsegurado(); 
			Parcela parcelaAnexo = new Parcela(); 
			parcelaAnexo.setAnexoModificacion(anexoModificacion); 
			capitalAseguradoModificadaBean.setParcela(parcelaAnexo); 
		 
			request.setAttribute("idAnexo", idAnexo);
			
			return parcelasModificacionPolizaController.doConsulta(request, response, capitalAseguradoModificadaBean).addAllObjects(parametros); 
		} 
	} 
	
	 
	
	
	public void getValElegibles (HttpServletRequest request, HttpServletResponse response) throws Exception { 
		
		String valorSele = StringUtils.nullToString(request.getParameter("valorSele"));
		String idComboSeleccionado = StringUtils.nullToString(request.getParameter("idComboSeleccionado"));
		
		JSONObject resultado = new JSONObject();
		
		try { 
			
			ModulosYCoberturas myc = coberturasModificacionPolizaManager.getModulosYCoberturas();
			es.agroseguro.modulosYCoberturas.Cobertura[] cober = myc.getModuloArray(0).getCoberturaArray();
			
			String[] partes = idComboSeleccionado.split("_");
			int coberturaConceptoPpalModulo = Integer.parseInt(partes[1]);
			int coberturaRiesgoCubierto = Integer.parseInt(partes[2]);
			int datoVarCodigoConcepto = Integer.parseInt(partes[3]);
			
			String idComboSeleccionadoUbicacion = "";
			
			for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : cober) { //COBERTURA == FILA
				if (cobertura.getConceptoPrincipalModulo() == coberturaConceptoPpalModulo && cobertura.getRiesgoCubierto() == coberturaRiesgoCubierto) {
					
					int filaComboSel = cobertura.getFila();
					for (es.agroseguro.modulosYCoberturas.DatoVariable dv : cobertura.getDatoVariableArray()) {
						
						for(@SuppressWarnings("unused") es.agroseguro.modulosYCoberturas.Valor v : dv.getValorArray()) {
								
							if(dv.getCodigoConcepto() == datoVarCodigoConcepto) {
								int columnaComboSel = dv.getColumna();
								
								idComboSeleccionadoUbicacion = idComboSeleccionado + ":" + filaComboSel + ":" + columnaComboSel + ":" + valorSele;
								
							}
						}
					}
				}
			}
			
			String vinculacion = "";
			List<String> vinculaciones = new ArrayList<String>();
			
			for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : cober) { //COBERTURA == FILA
				int ubicacionFila = cobertura.getFila();
				for (es.agroseguro.modulosYCoberturas.DatoVariable dv : cobertura.getDatoVariableArray()) {
					int ubicacionColumna = dv.getColumna();	
					for(es.agroseguro.modulosYCoberturas.Valor v : dv.getValorArray()) {
							
							
						if(null != v.getVinculacionCelda()) {
							
							int filaMadre = v.getVinculacionCelda().getFilaMadre();
							int columnaMadre = v.getVinculacionCelda().getColumnaMadre();
							String valorMadre = v.getVinculacionCelda().getValorMadre();
							
							String datosMadre = filaMadre + ":" + columnaMadre + ":" + valorMadre;
							vinculacion = "selectAnexo_" + cobertura.getConceptoPrincipalModulo() + "_" + cobertura.getRiesgoCubierto() + "_" + dv.getCodigoConcepto() + ":" + ubicacionFila +":"+ ubicacionColumna + ":" + v.getValor() + ":" + v.getDescripcion() + ":" + datosMadre;
							
							vinculaciones.add(vinculacion);
							//listaDatosMadre.add(datosMadre);
						}	
					}
				}
			}
			
			String[] partesIdComboSeleccionadoUbicacion = idComboSeleccionadoUbicacion.split(":");
			int filaIdComboSel = Integer.parseInt(partesIdComboSeleccionadoUbicacion[1]);
			int columnaIdComboSel = Integer.parseInt(partesIdComboSeleccionadoUbicacion[2]);
			int valorSeleccionado = Integer.parseInt(valorSele);
			List<String> listaVinculacionesDirectasComboSeleccionado = new ArrayList<String>();
			
			for(String vinculacionDirStr : vinculaciones) {
				
				String[] partesVSDir = vinculacionDirStr.split(":");
				//String idVinculacionVS =  partesVS[0];
				//String descripcionVS = partesVS[1];
				//int valorVS = Integer.parseInt(partesVS[2]);
				int filaMadreVSDir = Integer.parseInt(partesVSDir[5]);
				int columnaMadreVSDir = Integer.parseInt(partesVSDir[6]);
				int valorMadreVSDir = Integer.parseInt(partesVSDir[7]);
				
				if(filaIdComboSel == filaMadreVSDir && columnaIdComboSel == columnaMadreVSDir && valorSeleccionado == valorMadreVSDir) {
					listaVinculacionesDirectasComboSeleccionado.add(vinculacionDirStr);
				}	
			}
			
			resultado.put("listaVinculacionesDirectasComboSeleccionado", listaVinculacionesDirectasComboSeleccionado);
			
			
			
			
			
			//CREO LA LLAMADA A MI METODO RECURSIVO:
			List<String> listaHerencia = new ArrayList<String>();
			
			List<String> listaVinculacionesComboSeleccionado = getlistaHerenciaVinculacionesComboSeleccionado(vinculaciones, idComboSeleccionadoUbicacion, listaHerencia);
			
			resultado.put("listaVinculacionesComboSeleccionado", listaVinculacionesComboSeleccionado);
			
			getWriterJSON(response, resultado);
			 
		} catch (Exception ex) { 
			logger.error("Se ha producido un error al recuperar los valores de los combos vinculados",ex); 
		} 
	}
	
	public List<String> getlistaHerenciaVinculacionesComboSeleccionado(List<String> listaVinculaciones, String idComboSeleccionadoUbicacionATratar, List<String> listaHer) {
		
		//List<String> listaHerencia = new ArrayList<String>();
		
		String[] partesIdComboSeleccionadoUbicacion = idComboSeleccionadoUbicacionATratar.split(":");
		int filaIdComboSel = Integer.parseInt(partesIdComboSeleccionadoUbicacion[1]);
		int columnaIdComboSel = Integer.parseInt(partesIdComboSeleccionadoUbicacion[2]);
		int valorSeleccionado = Integer.parseInt(partesIdComboSeleccionadoUbicacion[3]);
		
		for(String vinculacionStr : listaVinculaciones) {
			
			String[] partesVS = vinculacionStr.split(":");
			int filaMadreVS = Integer.parseInt(partesVS[5]);
			int columnaMadreVS = Integer.parseInt(partesVS[6]);
			int valorMadreVS = Integer.parseInt(partesVS[7]);
			
			if(filaIdComboSel == filaMadreVS && columnaIdComboSel == columnaMadreVS && valorSeleccionado == valorMadreVS) {
				listaHer.add(vinculacionStr);
				getlistaHerenciaVinculacionesComboSeleccionado(listaVinculaciones, vinculacionStr, listaHer);
			}	
		}
		
		
		return listaHer;
	}
	
	
	
	public ModelAndView getModulo(HttpServletRequest request, HttpServletResponse response) throws Exception { 
		try { 
			Long idPoliza = new Long(StringUtils.nullToString(request.getParameter("idpoliza"))); 
			Long idAnexo = new Long(StringUtils.nullToString(request.getParameter("idAnexo"))); 
			String codmodulo = StringUtils.nullToString(request.getParameter("codmodulo")); 
			Poliza poliza = declaracionesModificacionPolizaManager.getPoliza(idPoliza); 
			AnexoModificacion anexoModificacion = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo); 

			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			JSONObject datosModulo = coberturasModificacionPolizaManager.getCoberturasAnexoAgri(poliza, codmodulo, anexoModificacion, realPath, usuario); 
			
			getWriterJSON(response,datosModulo); 
			 
		} catch (Exception ex) { 
			logger.error("Se ha producido un error al recuperar la tabla de coberturas",ex); 
		} 
		return null; 
	} 
	 
	private HashMap<String,HashMap<String, Cobertura>> getCoberturasAnexo(HttpServletRequest request) { 
		HashMap<String,HashMap<String, Cobertura>> coberturas = new HashMap<String, HashMap<String,Cobertura>>(); 
		HashMap<String, Cobertura> conceptosAnexo = new HashMap<String, Cobertura>(); 
		HashMap<String, Cobertura> riesgosAnexo = new HashMap<String, Cobertura>(); 
		HashMap<String, Cobertura> conceptosPoliza = new HashMap<String, Cobertura>(); 
		HashMap<String, Cobertura> riesgosPoliza = new HashMap<String, Cobertura>(); 
		BigDecimal codconceptoppalmod; 
	    BigDecimal codriesgocubierto; 
	    BigDecimal codconcepto; 
	    int i = 0; 
	     
	    logger.debug("MPM# - En getCoberturasAnexo"); 
	     
		@SuppressWarnings("rawtypes")
		Enumeration enumeration = request.getParameterNames(); 
 
		 while(enumeration.hasMoreElements()) { 
			 StringTokenizer tokens = new StringTokenizer(enumeration.nextElement().toString(), "_"); 
			 String name = new String(tokens.nextToken()); 
			  
			 logger.debug("MPM# - Nombre del parametro: " + name); 
			  
			 if(name.equals("selectAnexo") || name.equals("checkAnexo")) { 
				  
				 logger.debug("MPM# - Dentro del if"); 
				  
				 i = 0; 
				 codconceptoppalmod = new BigDecimal(0); 
				 codriesgocubierto = new BigDecimal(0); 
				 codconcepto = new BigDecimal(0); 
				  
				 while(tokens.hasMoreTokens()) 
				 { 
					 String codName = new String(tokens.nextToken()); 
					 switch (i) { 
						case 0: 
							codconceptoppalmod = new BigDecimal(codName); 
							break; 
						case 1: 
							codriesgocubierto = new BigDecimal(codName); 
							break; 
						case 2: 
							codconcepto = new BigDecimal(codName); 
							break; 
						default: 
							break; 
					 } 
					 i++; 
				 } 
				  
				 String key = codconceptoppalmod.toString()+"_"+codriesgocubierto.toString(); 
				  
				 logger.debug("MPM# - key = " + key); 
				  
				 if(!codconcepto.toString().equals("0")) 
					 key = key + "_" + codconcepto.toString(); 
				  
				 logger.debug("MPM# - key = " + key); 
				  
				 String id = name+"_"+key; 
				 logger.debug("MPM# - id = " + id); 
				 String valor = request.getParameterValues(id)[0]; 
				 logger.debug("MPM# - valor = " + valor); 
				  
				 Cobertura cobertura = new Cobertura(); 
				  
				 cobertura.setCodconceptoppalmod(codconceptoppalmod); 
				 cobertura.setCodriesgocubierto(codriesgocubierto); 
				 cobertura.setCodconcepto(codconcepto); 
				  
				 if (name.equals("selectAnexo")) { 
					 cobertura.setCodvalor(valor); 
					  
					 conceptosAnexo.put(key, cobertura); 
				 } else if (name.equals("checkAnexo")) { 
					 if (valor.equals("1")) 
						 cobertura.setCodvalor(Constants.RIESGO_ELEGIDO_SI); 
					 else
						 cobertura.setCodvalor(Constants.RIESGO_ELEGIDO_NO); 
					 cobertura.setCodconcepto(new BigDecimal("363")); 
					  
					 riesgosAnexo.put(key, cobertura); 
				} 
			} 
		} 
		  
		if(!conceptosAnexo.isEmpty()) 
			coberturas.put("conceptosAnexo", conceptosAnexo); 
		if(!riesgosAnexo.isEmpty()) 
			coberturas.put("riesgosAnexo", riesgosAnexo); 
		if(!conceptosPoliza.isEmpty()) 
			coberturas.put("conceptosPoliza", conceptosPoliza); 
		if(!riesgosPoliza.isEmpty()) 
			coberturas.put("riesgosPoliza", riesgosPoliza); 
		 
		return coberturas; 
	} 
 
	public void setCoberturasModificacionPolizaManager(CoberturasModificacionPolizaManager coberturasModificacionPolizaManager) { 
		this.coberturasModificacionPolizaManager = coberturasModificacionPolizaManager; 
	} 
 
	public void setDeclaracionesModificacionPolizaManager(DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) { 
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager; 
	} 
 
	public void setParcelasModificacionPolizaController( 
			ParcelasModificacionPolizaController parcelasModificacionPolizaController) { 
		this.parcelasModificacionPolizaController = parcelasModificacionPolizaController; 
	} 
	 
	public void setListadoExplotacionesAnexoController( 
			ListadoExplotacionesAnexoController listadoExplotacionesAnexoController) { 
		this.listadoExplotacionesAnexoController = listadoExplotacionesAnexoController; 
	} 
 
	public void setExplotacionesModificacionPolizaManager(ExplotacionesModificacionPolizaManager explotacionesModificacionPolizaManager) { 
		this.explotacionesModificacionPolizaManager = explotacionesModificacionPolizaManager; 
	} 
}