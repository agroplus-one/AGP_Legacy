package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.DeclaracionesReduccionCapitalManager;
import com.rsi.agp.core.managers.impl.ParcelasReduccionCapitalManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.BigDecimalEditor;
import com.rsi.agp.core.webapp.util.ShortEditor;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.poliza.ListaCapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

public class ParcelasReduccionCapitalController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(ParcelasReduccionCapitalController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private ParcelasReduccionCapitalManager parcelasReduccionCapitalManager; 
	private DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager;
	private ValidacionesRCAjaxController validacionesRCAjaxController;
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		/* Evitar que informacion introducida por pantalla, cuyo tipo no se corresponde con el de
		 * su propiedad en el bean, se asocie a ella, es decir, evita asociar, a un BigDecimal, 
		 * una letra introducida en el formulario. */ 
		binder.registerCustomEditor(BigDecimal.class, null, new BigDecimalEditor());
		
		binder.registerCustomEditor(Short.class, null, new ShortEditor());
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAsegurado) throws Exception{
		
		List<CapitalAsegurado>  listCapitalesAsegurados = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		StringBuilder capitalesAlta = new StringBuilder();
		StringBuilder capitalesProdPost = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		
		try{
			
			informarCapitalAsegurado(request, capitalAsegurado, parametros);
			
			//Listado de capitales asegurados para el grid		
			listCapitalesAsegurados = parcelasReduccionCapitalManager.buscarCapitalesAsegurados(capitalAsegurado);
			ordenarListasCapitalesAsegurados(listCapitalesAsegurados);
			
			if (params.get("mensaje")!= null) {
				parametros.put("mensaje", params.get("mensaje"));
			}else if (params.get("alerta")!= null) {
				parametros.put("alerta", params.get("alerta"));
			}
			parcelasReduccionCapitalManager.getCapitalesProdPost(listCapitalesAsegurados, capitalesAlta, capitalesProdPost);
			
			parametros.put("listCapitalesAsegurados", listCapitalesAsegurados);			
			parametros.put("capitalesAlta", capitalesAlta);			
			parametros.put("capitalesProdPost", capitalesProdPost);
			parametros.put("idRC", capitalAsegurado.getParcela().getReduccionCapital().getId());
			
			//DAA 31/08/2012
			String vieneDeListadoRedCap = request.getParameter("vieneDeListadoRedCap");
			parametros.put("vieneDeListadoRedCap", vieneDeListadoRedCap);
			// MPM 04/09/2012
			// Indica si la visualizacion de las parcelas de la reduccion es en modo lectura
			boolean modoLectura = "true".equals(request.getParameter("modoLectura")) ? true : false;
			parametros.put("modoLectura", modoLectura);
			
			com.rsi.agp.dao.tables.poliza.Linea linea;
			
			if (!modoLectura) {
				
				// Se recupera una instancia espec韋ica de la entidad "Linea" a trav閟 del DAO a partir del lineaseguroid
				linea = lineaDao.getLinea(capitalAsegurado.getParcela().getReduccionCapital().getPoliza().getLinea().getLineaseguroid().toString());
				
			}
			else {
				
				// Se obtiene la poliza
				
				Long idPoliza = request.getParameter("idPoliza") == null ? new Long (request.getParameter("parcela.reduccionCapital.poliza.idpoliza")) : new Long(request.getParameter("idPoliza"));	
				Poliza poliza = declaracionesReduccionCapitalManager.getPoliza(idPoliza);
				// Se recupera una instancia espec韋ica de la entidad "Linea" a trav閟 del DAO a partir del lineaseguroid
				linea = lineaDao.getLinea(poliza.getLinea().getLineaseguroid().toString());
				
			}
			
			// Obtenemos la fecha de fin de contrataci髇.
			Date fechaInicioContratacion = linea.getFechaInicioContratacion();
			parametros.put("fechaInicioContratacion", fechaInicioContratacion);
		
			cargarDescripcionesLupas(request, capitalAsegurado, parametros); 
			
		}catch(Exception be){
			logger.error("Se ha producido un error durante la consulta de capitales asegurados", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			
		}
		
		return new ModelAndView("/moduloUtilidades/reduccionCapital/parcelasReduccionCapital", "capitalAseguradoBean", capitalAsegurado).addAllObjects(parametros);
	
	}
	/**
	 * carga las parcelas del WS o si este falla, de bbdd y las muestra en la jsp
	 * 24/04/2014 U029769
	 * @param request
	 * @param response
	 * @param capitalAsegSiniestradoDV
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView doCargaParcelas(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAsegurado) {
		
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<CapitalAsegurado>  listCapitalesAsegurados = null;
		StringBuilder capitalesAlta = new StringBuilder();
		StringBuilder capitalesProdPost = new StringBuilder();
		Poliza poliza = null;
		String idPoliza = request.getParameter("poliza.idpoliza") != null?request.getParameter("poliza.idpoliza"):request.getParameter("idPoliza");
		String idReduccionCapital = request.getParameter("id")!= null?request.getParameter("id"):request.getParameter("idReduccionCapital");
		
		parametros.put("idRC", idReduccionCapital);
		
		try {	
			params = parcelasReduccionCapitalManager.cargarParcelas(capitalAsegurado,
					capitalAsegurado.getParcela().getReduccionCapital().getPoliza().getReferencia(),
					capitalAsegurado.getParcela().getReduccionCapital().getPoliza().getLinea(),realPath);	
			
			listCapitalesAsegurados = (List<CapitalAsegurado>) params.get("listCapitalesAsegurados");
				
			
			if (params.get("mensaje")!= null) {
				parametros.put("mensaje", params.get("mensaje"));
			}else if (params.get("alerta")!= null) {
				parametros.put("alerta", params.get("alerta"));
			}
				
			parcelasReduccionCapitalManager.getCapitalesProdPost(listCapitalesAsegurados, capitalesAlta, capitalesProdPost);
			
			parametros.put("listCapitalesAsegurados", listCapitalesAsegurados);			
			parametros.put("capitalesAlta", capitalesAlta);			
			parametros.put("capitalesProdPost", capitalesProdPost);
			
			//DAA 31/08/2012
			String vieneDeListadoRedCap = request.getParameter("vieneDeListadoRedCap");
			parametros.put("vieneDeListadoRedCap", vieneDeListadoRedCap);
			// MPM 04/09/2012
			// Indica si la visualizacion de las parcelas de la reduccion es en modo lectura
			boolean modoLectura = "true".equals(request.getParameter("modoLectura")) ? true : false;
			parametros.put("modoLectura", modoLectura);
			//Recuperar el id de la poliza y el id de la reduccion de capital
			
			if(idPoliza != null && idReduccionCapital != null){
				
				capitalAsegurado.getParcela().getReduccionCapital().setId(new Long(idReduccionCapital));
				capitalAsegurado.getParcela().getReduccionCapital().getPoliza().setIdpoliza(new Long(idPoliza));
			}else{
				idReduccionCapital = capitalAsegurado.getParcela().getReduccionCapital().getId().toString();
				idPoliza = capitalAsegurado.getParcela().getReduccionCapital().getPoliza().getIdpoliza().toString();
			}
			poliza = declaracionesReduccionCapitalManager.getPoliza(new Long(idPoliza));
			parametros.put("poliza", poliza);
			
			
			cargarDescripcionesLupas(request, capitalAsegurado, parametros); 
		
	}catch(Exception be){
		logger.error("Se ha producido un error durante la consulta de capitales asegurados", be);
		parametros.put("alerta", bundle.getString("mensaje.error.general"));
	}
	return new ModelAndView("/moduloUtilidades/reduccionCapital/parcelasReduccionCapital", "capitalAseguradoBean", capitalAsegurado).addAllObjects(parametros);
	
	}
	
	
	
	
	public void informarCapitalAsegurado (HttpServletRequest request, CapitalAsegurado capitalAsegurado, Map<String, Object> parametros) throws Exception {
		String idPoliza = request.getParameter("poliza.idpoliza") != null?request.getParameter("poliza.idpoliza"):request.getParameter("idPoliza");
		String idReduccionCapital = request.getParameter("id")!= null?request.getParameter("id"):request.getParameter("idReduccionCapital");
		if (idPoliza == null) {
			idPoliza = request.getParameter("parcela.reduccionCapital.poliza.idpoliza");
		}
		if (idReduccionCapital == null) {
			idReduccionCapital = request.getParameter("parcela.reduccionCapital.id");
		}
		if (idReduccionCapital.isEmpty()) {
			idReduccionCapital= request.getAttribute("idReduccionCapital").toString();
		}
		Poliza poliza = null;
		Parcela parcela = new Parcela();
		ReduccionCapital reduccionCapital = null;
		
		if (capitalAsegurado.getParcela() == null) {
			parcela.setReduccionCapital(new ReduccionCapital());
			capitalAsegurado.setParcela(parcela);			
		}
		
		//Recuperar el id de la poliza y el id de la reduccion de capital
		if(idPoliza != null && idReduccionCapital != null){
			
			capitalAsegurado.getParcela().getReduccionCapital().setId(new Long(idReduccionCapital));
			capitalAsegurado.getParcela().getReduccionCapital().getPoliza().setIdpoliza(new Long(idPoliza));
		}else{
			idReduccionCapital = capitalAsegurado.getParcela().getReduccionCapital().getId().toString();
			idPoliza = capitalAsegurado.getParcela().getReduccionCapital().getPoliza().getIdpoliza().toString();
		}
		
		try{
			reduccionCapital = declaracionesReduccionCapitalManager.buscarReduccionCapital(new Long(idReduccionCapital));			
			
			if(reduccionCapital.getEstado().getIdestado().intValue()!= Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR.intValue() &&
			   reduccionCapital.getEstado().getIdestado().intValue()!= Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO_ERRONEO.intValue() &&
			   reduccionCapital.getEstado().getIdestado().intValue()!= Constants.REDUCCION_CAPITAL_ESTADO_DEFINITIVO.intValue()){
				parametros.put("modoLectura", true);
			}
			poliza = declaracionesReduccionCapitalManager.getPoliza(new Long(idPoliza));
			String linea = poliza.getLinea().getLineaseguroid().toString();
			parametros.put("poliza", poliza);
			
			capitalAsegurado.getParcela().getParcela().setPoliza(poliza);
			capitalAsegurado.getParcela().getReduccionCapital().setPoliza(poliza);
			
		}catch(BusinessException be){
			logger.error("Se ha producido un error durante la consulta de capitales asegurados", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			
			throw new Exception();
		}
	}

	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, CapitalAsegurado capitalAsegurado) throws Exception{

		
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<CapitalAsegurado> listaCapitalesAsegurados = null;
		
		try {
			String checksAlta = StringUtils.nullToString(request.getParameter("altaSel"));
			//String idreduccionCapital = request.getParameter("idReduccionCapital");
			//ReduccionCapital reduccionCapital = parcelasReduccionCapitalManager.getReduccionCapitalById(Long.parseLong(idreduccionCapital));
			
			//Montamos la lista que contendr谩 los capitales dados de alta con los checkbox
			List<CapitalAsegurado> listaCapitalesAseguradosAlta = construirListaCapitalesAsegurados(request);
			
			List<CapitalAsegurado> listaCapitalesAseguradosCompleta = 
				parcelasReduccionCapitalManager.getParcelaReduccionCapitalDao().listReduccionCapitalParcelas(capitalAsegurado);
			
			listaCapitalesAsegurados = mezclarListasCapitalesAsegurados(listaCapitalesAseguradosAlta, listaCapitalesAseguradosCompleta);
			
			if(!request.getParameter("operacion").equals("pasarDedinitivo") || (
				request.getParameter("operacion").equals("pasarDedinitivo") && parcelasReduccionCapitalManager.isParcelasAlta(checksAlta))){
			    //Realizamos el alta
			    parcelasReduccionCapitalManager.guardarCapitalesAsegurados(listaCapitalesAsegurados);
			
			    //Alta correcta			
			    parametros.put("mensaje", bundle.getString("mensaje.reduccionCapital.OK"));
			}else{
				parametros.put("alerta", bundle.getString("mensaje.reduccionCapital.KO.noParcelasAlta"));
				return this.doConsulta(request, response, capitalAsegurado).addAllObjects(parametros);
			}
			
			
			parametros.put("idReduccionCapital", capitalAsegurado.getParcela().getReduccionCapital().getId());
			return validacionesRCAjaxController.doValidacionesPreviasEnvio(request, response).addAllObjects(parametros);
		
		} catch (BusinessException be) {
			
			logger.error("Se ha producido un error durante el alta de parcelas siniestradas", be);
			//Alta incorrecta
			parametros.put("alerta", bundle.getString("mensaje.reduccionCapital.KO"));
			return doConsulta(request, response, capitalAsegurado).addAllObjects(parametros);
			
		}			
				
	}


	private List<CapitalAsegurado> construirListaCapitalesAsegurados(HttpServletRequest request) throws Exception {		
		
		//Recorrer los atributos del request para buscar los elementos alta_X y prodPost_X		
		List<CapitalAsegurado> lstCapitalAsegurado = new ArrayList<CapitalAsegurado>();
		CapitalAsegurado capitalAsegurado = null;
					
		final String alta = "alta_";
		
		List<String> capitalesAlta = new ArrayList<String>();				
		Map<String,BigDecimal> capitalesProdPost = new HashMap<String, BigDecimal>();
		
		try {
			
			String checksAlta = StringUtils.nullToString(request.getParameter("altaSel"));
			String prodPost = StringUtils.nullToString(request.getParameter("prodSel"));
			
			if(!checksAlta.equals("")){
				for(String capital: checksAlta.split("\\|")){
					if(capital.indexOf(alta) != -1){
						String id = capital.split("_")[1];
						capitalesAlta.add(id);
					}
				}
			}
			
			if(!prodPost.equals("")){
				for(String capitalProd : prodPost.split("\\|")){
					String[] aux= capitalProd.split("#");
					String id = aux[0];
					BigDecimal fecha = new BigDecimal(aux[1]);
					capitalesProdPost.put(id, fecha);				
				}
			}
			
			//	Con los idCapital que han sido de alta, busco en la request	si hay 
			//	producciones posteriores y se dan de alta
			for(String s : capitalesAlta){
				capitalAsegurado = new CapitalAsegurado();
				capitalAsegurado.setAltaenanexo('S');
				capitalAsegurado.setId(new Long(s));			
				
				if(capitalesProdPost.containsKey(s)){
					capitalAsegurado.setProdred(capitalesProdPost.get(s));
				}
					
				lstCapitalAsegurado.add(capitalAsegurado);
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error general", e);
		}	
			
		
		return lstCapitalAsegurado;				
	}
	/**
	 * Ordenar capitales asegurados por hoja y numero.
	 * @param listaCapitalesAsegurados Lista con los capitales asegurados desde el Dao
	 * @return Una lista en la que aparecen los capitales asegurados ordenados
	 * @throws Exception en caso de error
	 */
	public List<CapitalAsegurado> ordenarListasCapitalesAsegurados(List<CapitalAsegurado> listaCapitalesAsegurados){
        Collections.sort(listaCapitalesAsegurados, new Comparator<CapitalAsegurado>() {
            @Override
            public int compare(CapitalAsegurado c1, CapitalAsegurado c2) {
                // Comparar por hoja
                int hojaComparison = Integer.compare(c1.getParcela().getHoja().intValue(), c2.getParcela().getHoja().intValue());
                if (hojaComparison != 0) {
                    return hojaComparison;
                }
                // Si hoja es igual, comparar por numero
                return Integer.compare(c1.getParcela().getNumero().intValue(), c2.getParcela().getNumero().intValue());
            }
        });
		
		return listaCapitalesAsegurados;
	}
	

	/**
	 * Mezcla las dos listas que recibe, tomando por defecto los valores de la lista completa, cambiando el valor de los campos Altaenanexo
	 * y ProdPost para aquellos capitales asegurados que se encuentre en la lista de capitales asegurado de alta.
	 * @param listaCapitalesAseguradosAlta Lista con los capitales asegurados a dar de alta
	 * @param listaCapitalesAseguradosCompleta Lista con todos los capitales asegurados de la reducci贸n de capital correspondiente
	 * @return Una lista en la que aparecen los capitales asegurados que se quieren dar de alta y los que no
	 * @throws Exception en caso de error
	 */
	public List<CapitalAsegurado> mezclarListasCapitalesAsegurados(
			List<CapitalAsegurado> listaCapitalesAseguradosAlta, 
			List<CapitalAsegurado> listaCapitalesAseguradosCompleta) throws Exception {
		
		List<CapitalAsegurado> resultado = new ArrayList<CapitalAsegurado>();
		
		try {
			for (CapitalAsegurado cap : listaCapitalesAseguradosCompleta) {
				CapitalAsegurado nuevo = new CapitalAsegurado();
				
				nuevo.getParcela().setId(cap.getParcela().getId());
				nuevo.getParcela().setHoja(cap.getParcela().getHoja());
				nuevo.getParcela().setNumero(cap.getParcela().getNumero());
				nuevo.getParcela().setCodprovincia(cap.getParcela().getCodprovincia());
				nuevo.getParcela().setCodcomarca(cap.getParcela().getCodcomarca());
				nuevo.getParcela().setCodtermino(cap.getParcela().getCodtermino());
				nuevo.getParcela().setSubtermino(cap.getParcela().getSubtermino());
				nuevo.getParcela().setCodcultivo(cap.getParcela().getCodcultivo());
				nuevo.getParcela().setCodvariedad(cap.getParcela().getCodvariedad());
				nuevo.getParcela().setPoligono(cap.getParcela().getPoligono());
				nuevo.getParcela().setParcela_1(cap.getParcela().getParcela_1());
				nuevo.getParcela().setCodprovsigpac(cap.getParcela().getCodprovsigpac());
				nuevo.getParcela().setCodtermsigpac(cap.getParcela().getCodtermsigpac());
				nuevo.getParcela().setAgrsigpac(cap.getParcela().getAgrsigpac());
				nuevo.getParcela().setZonasigpac(cap.getParcela().getZonasigpac());
				nuevo.getParcela().setPoligonosigpac(cap.getParcela().getPoligonosigpac());
				nuevo.getParcela().setParcelasigpac(cap.getParcela().getParcelasigpac());
				nuevo.getParcela().setRecintosigpac(cap.getParcela().getRecintosigpac());
				nuevo.setCodtipocapital(cap.getCodtipocapital());
				nuevo.setSuperficie(cap.getSuperficie());
				nuevo.setPrecio(cap.getPrecio());
				nuevo.setProd(cap.getProd());
				
				nuevo.setId(cap.getId());
				nuevo.setAltaenanexo('N');
				nuevo.setProdred(null);
				
				Iterator<CapitalAsegurado> iter = listaCapitalesAseguradosAlta.iterator();
				while (iter.hasNext()) { 
					CapitalAsegurado cap2 = iter.next();
					BigDecimal prodRed = cap2.getProdred();
					//DAA 26/07/2012 prodRed.compareTo(BigDecimal.ZERO) > 0			
					if (cap.getId().equals(cap2.getId()) && 
							prodRed != null && prodRed.compareTo(BigDecimal.ZERO) >= 0) {
						nuevo.setAltaenanexo('S');
						nuevo.setProdred(prodRed);
						break;
					} 
				}
				resultado.add(nuevo);
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error general", e);
			throw new Exception();
		}
		
		return resultado;
	}
	
	/**
	 * Recupera las descripciones de los c贸digos de los campos con lupa para que se muestren cuando se recarga la pantalla, ya que los c贸digos 
	 * s铆 que existen en el bean, pero no as铆 sus descripciones.
	 * @param capitalAsegurado Contiene la informaci贸n necesaria para recuperar la informaci贸n
	 * @param parametros Almacena la informaci贸n que ser谩 enviada por request al jsp
	 * @throws Exception en caso de error
	 * 
	 */
	public void cargarDescripcionesLupas (HttpServletRequest request, CapitalAsegurado capitalAsegurado, Map<String, Object> parametros) throws Exception {

		try {
			String descProvincia = request.getParameter("descProvincia");
			String descComarca = request.getParameter("descComarca");
			String descTermino = request.getParameter("descTermino");
			String descCultivo = request.getParameter("descCultivo");
			String descVariedad = request.getParameter("descVariedad");
			String descTipoCapital = request.getParameter("descTipoCapital");
			
			if (descProvincia != null) {
				parametros.put("descProvincia", descProvincia);
			}
			if (descComarca != null) {
				parametros.put("descComarca", descComarca);
			}
			if (descTermino != null) {
				parametros.put("descTermino", descTermino);
			}
			if (descCultivo != null) {
				parametros.put("descCultivo", descCultivo);
			}
			if (descVariedad != null) {
				parametros.put("descVariedad", descVariedad);
			}
			if (descTipoCapital != null) {
				parametros.put("descTipoCapital", descTipoCapital);
			}
			
		} catch (Exception e) {			
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			logger.error("Excepcion : ParcelasReduccionCapitalController - cargarDescripcionesLupas", e);
			throw new Exception();
		}
	}
	
	public ParcelasReduccionCapitalManager getParcelasReduccionCapitalManager() {
		return parcelasReduccionCapitalManager;
	}

	public void setParcelasReduccionCapitalManager(ParcelasReduccionCapitalManager parcelasReduccionCapitalManager) {
		this.parcelasReduccionCapitalManager = parcelasReduccionCapitalManager;
	}

	public DeclaracionesReduccionCapitalManager getDeclaracionesReduccionCapitalManager() {
		return declaracionesReduccionCapitalManager;
	}

	public void setDeclaracionesReduccionCapitalManager(
			DeclaracionesReduccionCapitalManager reduccionCapitalManager) {
		this.declaracionesReduccionCapitalManager = reduccionCapitalManager;
	}	
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setValidacionesRCAjaxController(ValidacionesRCAjaxController validacionesRCAjaxController) {
		this.validacionesRCAjaxController = validacionesRCAjaxController;
	}


}
