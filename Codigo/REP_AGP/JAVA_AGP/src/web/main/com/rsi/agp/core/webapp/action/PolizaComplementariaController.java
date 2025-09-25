package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelasTodas;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.PolizaComplementariaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.report.BeanParcela;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.BigDecimalEditor;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class PolizaComplementariaController extends BaseMultiActionController{
	
	private Log logger = LogFactory.getLog(PolizaComplementariaController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private PolizaComplementariaManager polizaComplementariaManager;
	private PolizaManager polizaManager;
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;
		
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(BigDecimal.class, null, new BigDecimalEditor());
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean){
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<CapitalAsegurado> listCapAseg = null;
		Long idPoliza = null;
		StringBuilder capitalesAlta = new StringBuilder();
		StringBuilder incrementosAlta = new StringBuilder();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try {
			
			if (capitalAseguradoBean.getParcela().getPoliza().getIdpoliza() == null && 
					!StringUtils.nullToString(request.getParameter("idpolizaCpl")).equals("")){
				capitalAseguradoBean.getParcela().getPoliza().setIdpoliza(
						new Long(StringUtils.nullToString(request.getParameter("idpolizaCpl"))));
			}
			
			//BOTON EDITAR DE LA POLIZA
			if(StringUtils.nullToString(request.getParameter("idpolizaCpl")).equals("")){
				idPoliza = capitalAseguradoBean.getParcela().getPoliza().getIdpoliza();
				if(idPoliza == null)
					idPoliza = polizaComplementariaManager.getIdPolizaPpalByRef(request.getParameter("refPol"), null);
			}else{
				idPoliza = Long.parseLong(request.getParameter("idpolizaCpl"));
			}
			
			logger.debug("idpoliza: " + idPoliza);
			//TMR
			Poliza poliza = polizaManager.getPoliza(idPoliza);
			
			capitalAseguradoBean.getParcela().setPoliza(poliza);
			
			listCapAseg = polizaComplementariaManager.getCapitalesAsegPolCpl(capitalAseguradoBean);
			logger.debug("listado capitales asegurados. size: " + listCapAseg.size());
			polizaComplementariaManager.getlistaAltas(listCapAseg,capitalesAlta,incrementosAlta);
			
			double superficieTotal = 0;
			CapitalAsegurado capital = null;
			
			
			String listaIdCapAseg = "";
			
			// Guarda la lista completa de ids de capitales asegurados
			for(int i = 0; i < listCapAseg.size(); i++) {
				capital = listCapAseg.get(i); 
			
				listaIdCapAseg += (listaIdCapAseg.length() == 0) ? (capital.getIdcapitalasegurado()) : ("," + capital.getIdcapitalasegurado());
							
				if(capital.getSuperficie() != null) {
					superficieTotal += capital.getSuperficie().doubleValue();
				}
			}
			parametros.put("listaIdCapAseg", listaIdCapAseg );
			
			DecimalFormat df = new DecimalFormat("0.00");
			request.getSession().setAttribute("superficieTotalComp", df.format(superficieTotal));
			/// mejora 112 Angel 01/02/2012 anadida la opcion de ver la poliza sin opcion a editarla con estado grabacion definitiva
			String modoLectura = request.getParameter("modoLecturaCpl");
			String deCoberturas = request.getParameter("deCoberturas");
			
			logger.debug("ESTADO POLIZA COMPLEMENTARIA:" + poliza.getEstadoPoliza().getIdestado());
			
			if(!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) && 
					!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION) &&
					!"modoLectura".equals(modoLectura) && !"deCoberturas".equals(deCoberturas)){

				// ACTUALIZAMOS EL ESTADO DE LA POLIZA A PENDIENTE DE VALIDACION CADA VEZ QUE SE EDITA
				logger.debug("actualizamos el estado de la poliza complementario a pendiente de validacion");
				
				if (poliza.getGedDocPoliza().toString() == null) {
					logger.debug("DOCUMENTACION NULA");
				}
				else {
					logger.debug(poliza.getGedDocPoliza().toString());
				}
				polizaComplementariaManager.polizaComplementariaPendienteValidacion(poliza, 
						Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION, usuario.getCodusuario());
			}
			if (request.getParameter("mensaje")!= null) {
				parametros.put("mensaje", request.getParameter("mensaje"));
			}else if (request.getParameter("alerta")!= null) {
				parametros.put("alerta", request.getParameter("alerta"));
			}
			parametros.put("vieneDeUtilidades", StringUtils.nullToString(request.getParameter("vieneDeUtilidades")));
			parametros.put("idPoliza",idPoliza);
			parametros.put("listCapAseg", listCapAseg );
			parametros.put("capitalesAlta", capitalesAlta.toString());
			parametros.put("incrementosAlta", incrementosAlta.toString());
			parametros.put("modoLectura", modoLectura );
			parametros.put("incrementoOK",StringUtils.nullToString(request.getParameter("incrementoOK")));
			
			// Se recupera una instancia específica de la entidad "Linea" a través del DAO a partir del lineaseguroid
			com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(poliza.getLinea().getLineaseguroid().toString());
			// Obtenemos la fecha de fin de contratación.
			Date fechaInicioContratacion = linea.getFechaInicioContratacion();
			parametros.put("fechaInicioContratacion", fechaInicioContratacion);
			
			mv = new ModelAndView("moduloPolizas/polizas/polComplementaria/parcelasComplementario","capitalAseguradoBean",capitalAseguradoBean).addAllObjects(parametros);
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al editar la pï¿½liza complementaria ", be);
			parametros.put("alerta",bundle.getString("mensaje.poliza.complementaria.consulta.KO") );
			mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parametros);
		}
		catch (Exception be) {
			logger.error("Se ha producido un error al editar la pï¿½liza complementaria ", be);
			parametros.put("alerta",bundle.getString("mensaje.poliza.complementaria.consulta.KO") );
			mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parametros);
		}
		logger.debug("end - doConsulta");
		return mv;
	}
	
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)throws Exception{
		logger.debug("init - doAlta");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		String idPoliza = StringUtils.nullToString(request.getParameter("idPol"));
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		try {
			Poliza polizPpal = polizaManager.getPoliza(Long.parseLong(idPoliza));
			
			logger.debug ("ANTES DE ALTAMODULOCPL");
			params  = polizaComplementariaManager.altaModuloCpl(usuario,realPath,polizPpal);
			
			Poliza polCpl = (Poliza) params.get("polizaCpl");
			
			if(null!=polCpl) {
				logger.debug("idpoliza complementaria: " + polCpl.getIdpoliza());
				parametros = gestionMensajes(polCpl.getIdpoliza());
				if (params.get("mensaje")!= null) {
					parametros.put("mensaje", params.get("mensaje"));
				}else if (params.get("alerta")!= null) {
					parametros.put("alerta", params.get("alerta"));
				}
				if(polCpl.getIdpoliza() != null && polCpl.getIdpoliza() > 0){
					parametros.put("idpolizaCpl", polCpl.getIdpoliza());
					
					CapitalAsegurado cap= new CapitalAsegurado();
					cap.getParcela().getPoliza().setIdpoliza(polCpl.getIdpoliza());
					
					mv = new ModelAndView("redirect:/polizaComplementaria.html").addAllObjects(parametros);
				}else{
					mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parametros);
				}
			}else {
				 if (params.get("alerta")!= null) { 
					parametros.put("alerta", params.get("alerta"));
				 }else{
					 parametros.put("alerta",bundle.getString("mensaje.poliza.complementaria.KO"));
				 }
				mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parametros);
			}
			
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error inesperado al dar de alta la poliza complementaria", be);
			parametros.put("alerta",bundle.getString("mensaje.poliza.complementaria.KO"));
			mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parametros);
		}
		catch (Exception be) {
			logger.error("Se ha producido un error inesperado al dar de alta la poliza complementaria", be);
			parametros.put("alerta",bundle.getString("mensaje.poliza.complementaria.KO"));
			mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parametros);
		}
		
		logger.debug("end - doAlta");
		return mv;
	}
	
	public ModelAndView doGuardar(HttpServletRequest request, HttpServletResponse response,CapitalAsegurado capitalAseguradoBean ) throws Exception{
		logger.debug("init - doGuardar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Poliza poliza = null;
		
		boolean hayCapitalesConIncremento = false;
		
		try {
			logger.debug("idpoliza complementaria: " + capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			poliza = polizaManager.getPoliza(capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			
			//A este punto o llegas en estado provisional o vienes en modo consulta: solo acutalizamos cuando estï¿½ en provisional
			if(poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)){
				
				//Buscamos los capitales asegurados que tienen incremento y los anadimos a listCapAseg
				hayCapitalesConIncremento = polizaComplementariaManager.getCapitalesConIncremento(poliza);
				
				if(hayCapitalesConIncremento){
					logger.debug("comprobamos que almenos hay un cap aseg con incremento de produccion para continuar con el proceso de contratacion");
					/* Si el grabado fue realizado con exito, nos vamos a los servicios de validacion y calculo
					   si la poliza principal no tiene referencia, ira directamente al servicio de calculo*/
					
					if (poliza.getPolizaPpal().getReferencia()!=null){
						parametros.put("method", "doValidar");
					}else{
						parametros.put("method", "doCalcular");
					}
					parametros.put("idpoliza", capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
					parametros.put("origenllamada", "polizaComplementaria");
					
					logger.debug("continuamos con los procesos de validacion y calculo");
					mv =  new ModelAndView("redirect:/webservicesCpl.html", parametros);
				}else{
					parametros.put("alerta", bundle.getString("mensaje.poliza.complementaria.seleccionar.check.KO"));
					mv =  doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
				}
			}else{
				parametros.put("method", "doValidar");
				parametros.put("idpoliza", capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
				parametros.put("origenllamada", "polizaComplementaria");
				mv =  new ModelAndView("redirect:/webservicesCpl.html", parametros);
			}

			
		} catch (Exception be) {
			logger.error("Se ha producido un error al guardar los cambios de los capitales asegurados del modulo complementario", be);
			parametros.put("alerta", bundle.getString("mensaje.poliza.complementaria.modificacion.KO"));
			mv =  doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		}
		logger.debug("end - doGuardar");
		return mv;
	}
	
	public ModelAndView doCoberturas (HttpServletRequest request, HttpServletResponse response,CapitalAsegurado capitalAseguradoBean) throws Exception{
		logger.debug("init - doCoberturas");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String modoLectura = StringUtils.nullToString(request.getParameter("modoLectura"));
		String vieneDeUtilidades = StringUtils.nullToString(request.getParameter("vieneDeUtilidades"));
		try {
			//Poliza complementaria
			logger.debug("id poliza complementaria : " + capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			Poliza polizaCpl = polizaManager.getPoliza(capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			
			parametros.put("polizaPpal", polizaCpl.getPolizaPpal());
			parametros.put("polizaCpl", polizaCpl);
			parametros.put("modoLectura", modoLectura);
			parametros.put("vieneDeUtilidades", vieneDeUtilidades);
			
		} catch (Exception be) {
			logger.error("Se ha producido un error inesperado al obtener las coberturas de la poliza", be);
			parametros.put("alerta", bundle.getString("mensaje.poliza.complementaria.coberturas.KO"));
			return doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		}
		logger.debug("end - doCoberturas");
		return new ModelAndView("moduloPolizas/polizas/polComplementaria/coberturasPolComplementaria","capitalAseguradoBean",capitalAseguradoBean).addAllObjects(parametros);
	}
	
	public ModelAndView doImprimir(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean){
		logger.debug("init - doImprimir");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Poliza poliza = null;
		boolean hayCapitalesConIncremento = false;
		try {
			logger.debug("idpoliza complementaria: " + capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			poliza = polizaManager.getPoliza(capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			
			//Buscamos los capitales asegurados que tienen incremento y los anadimos a listCapAseg
			hayCapitalesConIncremento = polizaComplementariaManager.getCapitalesConIncremento(poliza);

			if(hayCapitalesConIncremento){
				//Si el grabado fue realizado con exito, imprimimos		
				mv = new ModelAndView("redirect: /informes.html").addObject("method", "doInformePolizaParcelasComplementaria")
																	 .addObject("idPoliza", capitalAseguradoBean.getParcela().getPoliza().getIdpoliza());
			}else{
				parametros.put("alerta", "No hay Capitales Asegurados con incremento");
				mv =  doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
			}
			
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al imprimir", be);
			parametros.put("alerta", "Se ha producido un error al Imprimir");
			mv =  doConsulta(request, response, capitalAseguradoBean).addAllObjects(parametros);
		}
		
		logger.debug("init - doImprimir");
		return mv.addAllObjects(parametros);
	}
	
	public ModelAndView doIncrementar(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAseguradoBean)throws Exception{
		//TMR 28-05-12
		logger.debug("init - doIncrementar");
		ModelAndView mv = null;
		// Obtiene el listado de ids de capitales asegurados a incrementar
		String listaIds = StringUtils.nullToString(request.getParameter("listaIds"));
		
		// Obtiene el tipo de incremento que se aplicara
		String tipoInc = StringUtils.nullToString(request.getParameter("tipoInc"));
		// Obtiene el incremento que se aplicara
		String incremento = StringUtils.nullToString(request.getParameter("incrGen"));
		
		// Realiza el incremento de todos los capitales asegurados indicados en la lista de ids si los tres parametros estan informados
		if (!"".equals(listaIds) && !"".equals(tipoInc) && !"".equals(incremento)){
			polizaComplementariaManager.capitalesAsegModificadosLista(listaIds, tipoInc, incremento);
		}
		request.setAttribute("incrementoOK", "true");
		mv =  doConsulta(request, response, capitalAseguradoBean);
		
		
		return mv;
	}
	
	private List<BeanParcela> obtenerListaBeanParcela(List<Parcela> listaParcelas) {
		List<BeanParcela> listParcelasAnexo = new ArrayList<BeanParcela>();
		ModelTableDecoratorListaParcelasTodas decorator = new ModelTableDecoratorListaParcelasTodas();
		
		Iterator<Parcela> it = listaParcelas.iterator();
		while(it.hasNext()){
			Parcela parcela = it.next();
			
			
			for (CapitalAsegurado capitalAsegurado : parcela.getCapitalAsegurados()) {
				
				BeanParcela bp = new BeanParcela();
				
				// Datos dependientes de la parcela
				//PRV, CMC, TRM, SBT
				bp.setCodProvincia(parcela.getTermino().getId().getCodprovincia());
				bp.setCodComarca(parcela.getTermino().getId().getCodcomarca());
				bp.setCodTermino(parcela.getTermino().getId().getCodtermino());
				bp.setSubtermino(parcela.getTermino().getId().getSubtermino()!=null?parcela.getTermino().getId().getSubtermino().toString():null);

				//CUL
				bp.setCodCultivo(parcela.getCodcultivo());
				
				//VAR
				bp.setCodVariedad(parcela.getCodvariedad());

				//NUM
				if(parcela.getHoja() != null && parcela.getNumero() != null){
					bp.setNumero(parcela.getHoja() + "-" + parcela.getNumero());
				}
				
				//Id Cat/SIGPAC
				bp.setIdCatSigpac(decorator.getIdCat(parcela));
				//Para informe Excel
				if (parcela.getPoligono() != null && parcela.getParcela() != null) {
					bp.setParcela(parcela.getParcela());
					bp.setPoligono(parcela.getPoligono());
				} else {
					bp.setCodprovsigpac(parcela.getCodprovsigpac());
					bp.setCodtermsigpac(parcela.getCodtermsigpac());
					bp.setAgrsigpac(parcela.getAgrsigpac());
					bp.setZonasigpac(parcela.getZonasigpac());
					bp.setPoligonosigpac(parcela.getPoligonosigpac());
					bp.setParcelasigpac(parcela.getParcelasigpac());
					bp.setRecintosigpac(parcela.getRecintosigpac());	
				}
				
				//Nombre
				bp.setNombre(parcela.getNomparcela());

				
				// Datos dependientes del capital asegurado
				// Super./m - Si es una instalacion la superficie siempre es 0
				bp.setSuperm(new Character ('E').equals(parcela.getTipoparcela()) ? "0" : decorator.getSuperf(capitalAsegurado));

				// Prod
				bp.setProduccion(decorator.getProduccion(capitalAsegurado));
				
				// Precio
				bp.setPrecio(decorator.getPrecio(capitalAsegurado));
		
				// T.Capital
				bp.setTipoCapital(decorator.getTcapital(capitalAsegurado));
				// Fecha Garantia
				bp.setFechaGarantia(parcela.getTipoparcela().equals('P') ? decorator.getFechaFin(capitalAsegurado) : "");
				
				// Numero unidades
				bp.setNumUnidades(decorator.getNumUnidades(capitalAsegurado));
				
				//Sistema Cultivo
				bp.setSistemaCultivo(decorator.getSistemaCultivo(capitalAsegurado));
				
				//Sistema Conduccion
				bp.setSistemaConduccion(decorator.getSistemaConduccion(capitalAsegurado));
				//Incremento Produccion
				bp.setIncrementoProduccion(parcela.getIncrProduccion());
				listParcelasAnexo.add(bp);

			}
			
			
		}
		return listParcelasAnexo;
	}

public ModelAndView doInformeListadoParcelas(HttpServletRequest request,HttpServletResponse response,CapitalAsegurado capitalAseguradoBean) {
		
		List<CapitalAsegurado> listCapAseg = null;
		List<Parcela> listParcela=new ArrayList<Parcela>();
		
		try {
			listCapAseg = polizaComplementariaManager.getCapitalesAsegPolCpl(capitalAseguradoBean);
			for(CapitalAsegurado capitalAsegurado:listCapAseg) {
				if(capitalAsegurado.getIncrementoproduccion() !=null) {
				capitalAsegurado.getParcela().setIncrProduccion(capitalAsegurado.getIncrementoproduccion().toString());
				}
				listParcela.add(capitalAsegurado.getParcela());
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		List<BeanParcela> listadoParcelasPoliza = obtenerListaBeanParcela(listParcela);
		request.setAttribute("esPrincipal", false);
		request.setAttribute("listaParcelasPoliza", listadoParcelasPoliza);
	
		ModelAndView resultado = new ModelAndView("forward:/informes.html?method=doInformeListadoParcelasPoliza");
		
	return resultado;
	}

	
	/**
	 * Metodo que genera los mensajes de errores de validacion que se mostraran en la jsp 
	 * @param errList
	 * @param msjOK
	 * @return
	 */
	private Map<String, Object> gestionMensajes(Long error){
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		// MENSAJES DE ERRORES DE VALIDACION
		if(error < 0){
			switch (error.intValue()) {
				case -2:
						parametros.put("alerta", bundle.getString("mensaje.poliza.complementaria.existe.KO"));
						break;
				case -3:
						parametros.put("alerta", bundle.getString("mensaje.poliza.complementaria.duplicado.KO"));
						break;
				default:
						break;
			}
		}
		
		return parametros;
	}

	public void setPolizaComplementariaManager(	PolizaComplementariaManager polizaComplementariaManager) {
		this.polizaComplementariaManager = polizaComplementariaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}
