package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.CapitalAseguradoManager;
import com.rsi.agp.core.managers.impl.RevProduccionPrecioManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ListaCapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.DatoVariableParcelaVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.PreciosProduccionesVO;
import com.rsi.agp.vo.ProduccionVO;
import com.rsi.agp.pagination.PaginatedListImpl;

public class RevProduccionPrecioController extends BaseSimpleController implements Controller {

	private static final Log logger = LogFactory.getLog(RevProduccionPrecioController.class);
	private RevProduccionPrecioManager revProduccionPrecioManager;
	private CapitalAseguradoManager capitalAseguradoManager;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	
	public RevProduccionPrecioController() {
		super();
		setCommandClass(ListaCapitalAsegurado.class);
		setCommandName("listaCapitalAseguradoBean");
	}

	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object object, BindException exception) throws Exception {
		
		final String operacion = request.getParameter("operacion");
		final Map<String, Object> parameters = new HashMap<String, Object>();
		
		ListaCapitalAsegurado capitalAseguradoBean = (ListaCapitalAsegurado) object;
		ModelAndView resultado;
		
		if (null != operacion && operacion.equalsIgnoreCase("getLimitesProduccion_ajax")){
	    	getLimitesProduccion_ajax(response, new Long(request.getParameter("idcapitalasegurado")));
	    	return null;
	    }

		final String idpoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		
		Long idEnvio= null;
		String idEnvioStr="";
		if (request.getParameter("idEnvio") != null){
			idEnvioStr= request.getParameter("idEnvio");
			if (!idEnvioStr.equals("") && !idEnvioStr.equals("N/D"))
				idEnvio = Long.parseLong(idEnvioStr);
		}
		parameters.put("idEnvio", idEnvio);
		if("cambiar".equalsIgnoreCase(operacion)) {
			Boolean isChange = false;
			String idCapital = "";
			String newProduccion = "";
			String newPrecio = "";
			String capModif= request.getParameter("capModif");
			if(capModif != null && !"".equals(capModif)){
				capModif = capModif.substring(0, capModif.length()-1);
				
				String[] capitales = capModif.split(";");
				for(int i=0; i< capitales.length; i++){
					String[] auxString = capitales[i].split("\\|");
					
					idCapital = auxString[0];
					newProduccion = auxString[1];
					newPrecio = auxString[2];
					
					if((newProduccion != null && !"".equals(newProduccion)) && (newPrecio != null && !"".equals(newPrecio))){
						List<CapAsegRelModulo> listCapAsegRelMod = (List<CapAsegRelModulo>) capitalAseguradoManager.listCapAsegRelModuloByIdCapitalAsegurado(new Long(idCapital));
						if (listCapAsegRelMod != null && listCapAsegRelMod.size() > 0) {
							CapAsegRelModulo capAsegRelMod = listCapAsegRelMod.get(0);
							capAsegRelMod.setProduccion(new BigDecimal(newProduccion));
							capAsegRelMod.setPrecio(new BigDecimal(newPrecio));
			
							capitalAseguradoManager.saveCapAsegRelMod(capAsegRelMod);
			
							isChange = true;
						}
					}
				}
			}
			parameters.put("idpoliza", idpoliza);
			if (isChange){
				//Si ha habido cambios serinvocarán los servicios remotos de validación y cálculo.
				parameters.put("operacion", "validar");
				return new ModelAndView("redirect:/webservices.html", parameters);
			}else{
				//String revPagos= StringUtils.nullToString((request.getParameter("revPagos")));
				//if (revPagos.equals("true")){
					//return new ModelAndView("redirect:/pagoPoliza.html", parameters);
				//}else{
					parameters.put("operacion", "importes");
					return new ModelAndView("redirect:/seleccionPoliza.html", parameters);
				}	 
				
		}
		/*else {
			//CapitalAsegurado capAseg = listaCapitalAsegurado.get(0);
			
			capitalAseguradoBean.setTipoCapital(capAseg.getTipoCapital());
			capitalAseguradoBean.setSuperficie(capAseg.getSuperficie());
			
		}*/
		/*************************************************/
		/*************Paginacion*************************/
		/************************************************/
		Long numPageRequest = new Long("0");		
		if(request.getParameter("page") == null)
			numPageRequest = Long.parseLong("1");
		else
		    numPageRequest = Long.parseLong(request.getParameter("page"));
		
		/************* DAA 04/12/2012 Ordenacion*************************/
		String sort = StringUtils.nullToString(request.getParameter("sort"));		
		String dir = StringUtils.nullToString(request.getParameter("dir"));
		
		PaginatedListImpl<CapitalAsegurado> listaCapitalAsegurado = revProduccionPrecioManager.getPaginatedListCapitalesAsegurados(idpoliza,numPageRequest.intValue(),sort,dir);
		parameters.put("totalListSize", listaCapitalAsegurado.getFullListSize());
		parameters.put("listaCapitalAsegurado", listaCapitalAsegurado);
		/*************************************************/
		/*************Fin Paginacion**********************/
		/************************************************/
		parameters.put("idpoliza", idpoliza);
		resultado = new ModelAndView("/moduloPolizas/polizas/revision/revProduccionPrecios", "capitalAseguradoBean", capitalAseguradoBean);
		resultado.addAllObjects(parameters);

		return resultado;
	}
	
	private void getLimitesProduccion_ajax(HttpServletResponse response, Long idcapitalasegurado){
		try{
			JSONObject prodPrecio = new JSONObject();
			JSONObject limitesProduccion = new JSONObject();
			JSONObject limitesPrecio = new JSONObject();
			JSONArray list     = new JSONArray();
			CapitalAsegurado capitalAsegurado = revProduccionPrecioManager.getCapitalAseguradoById(idcapitalasegurado);
			Variedad variedad = revProduccionPrecioManager.getVariedad(capitalAsegurado.getParcela().getCodcultivo());
			Parcela parcela = capitalAsegurado.getParcela();
			
			if (!capitalAsegurado.getCapAsegRelModulos().isEmpty()) {
				CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo)capitalAsegurado.getCapAsegRelModulos().toArray()[0];
				if(capAsegRelMod.getProduccionmodif() != null)
					prodPrecio.put("produccion", capAsegRelMod.getProduccionmodif());
				else
					prodPrecio.put("produccion", capAsegRelMod.getProduccion());
				
				if(capAsegRelMod.getPreciomodif() != null)
					prodPrecio.put("precio", capAsegRelMod.getPreciomodif());
				else
					prodPrecio.put("precio", capAsegRelMod.getPrecio());
			}
			
			list.put(prodPrecio);
			
			List <PrecioVO> listPreciosVO = null;
			List <ProduccionVO> listProduccionesVO = null;
			
			if (null != variedad){
				Modulo modulo = new Modulo();
				modulo.getId().setLineaseguroid(capitalAsegurado.getParcela().getPoliza().getLinea().getLineaseguroid());
				modulo.getId().setCodmodulo(capitalAsegurado.getParcela().getPoliza().getCodmodulo());
				
				
				ParcelaVO parcelaVO = new ParcelaVO();
				parcelaVO.setCodProvincia(parcela.getTermino().getProvincia().getCodprovincia().toString());
				parcelaVO.setCodComarca(parcela.getTermino().getComarca().getId().getCodcomarca().toString());
				parcelaVO.setCodTermino(parcela.getTermino().getId().getCodtermino().toString());
				parcelaVO.setCodSubTermino(parcela.getTermino().getId().getSubtermino().toString());								
				parcelaVO.setCultivo(parcela.getCodcultivo().toString());
				parcelaVO.setVariedad(parcela.getCodvariedad().toString());
				parcelaVO.setCodPoliza(parcela.getPoliza().getIdpoliza().toString());
				CapitalAseguradoVO capitalAseguradoVO = new CapitalAseguradoVO();
				capitalAseguradoVO.setSuperficie(capitalAsegurado.getSuperficie().toString());
				capitalAseguradoVO.setCodtipoCapital(capitalAsegurado.getTipoCapital().getCodtipocapital().toString());
				
				List<CapitalAseguradoVO> listCapitalesAsegurados = new ArrayList<CapitalAseguradoVO>();
				listCapitalesAsegurados.add(capitalAseguradoVO);
				
				parcelaVO.setCapitalesAsegurados(listCapitalesAsegurados); 
				
				List<DatoVariableParcelaVO> datosVariablesParcelaVO = new ArrayList<DatoVariableParcelaVO>();
				
				Set<DatoVariableParcela> datosVariablesParcela = capitalAsegurado.getDatoVariableParcelas();
				
				for (DatoVariableParcela datoVariableParcela : datosVariablesParcela) {
					datosVariablesParcelaVO.add(new DatoVariableParcelaVO(
							datoVariableParcela.getDiccionarioDatos().getCodconcepto().intValue(),
							datoVariableParcela.getValor(), datoVariableParcela.getIddatovariable()));
				}
				
				capitalAseguradoVO.setDatosVariablesParcela(datosVariablesParcelaVO);
				
				BigDecimal codConceptoProduccion = new BigDecimal(ResourceBundle.getBundle("agp").getString("codConceptoRendimiento"));
				PreciosProduccionesVO precProd = this.calculoPrecioProduccionManager.getProduccionPrecio(parcelaVO, 0, codConceptoProduccion);
				
				listPreciosVO = precProd.getListPrecios();
				listProduccionesVO = precProd.getListProducciones();
				
			}
			
			if (listProduccionesVO != null && listProduccionesVO.size() > 0){
				
				ProduccionVO produccionoVO = listProduccionesVO.get(0);
				
				limitesProduccion.put("produccionMin", produccionoVO.getLimMin());
				limitesProduccion.put("produccionMax", produccionoVO.getLimMax());
			}else{
				
				limitesProduccion.put("produccionMin", "");
				limitesProduccion.put("produccionMax", "");				
			
			}
			
			list.put(limitesProduccion);
			
			if (listPreciosVO != null && listPreciosVO.size() > 0){
				
				PrecioVO precioVO = listPreciosVO.get(0);
				
				if (!precioVO.getLimMax().equals(precioVO.getLimMin())){
				
					limitesPrecio.put("precioMin", precioVO.getLimMin());
					limitesPrecio.put("precioMax", precioVO.getLimMax());
				
				}else{
					limitesPrecio.put("precioMin", "Precio Fijo");
					limitesPrecio.put("precioMax",  precioVO.getLimMax());
				}
			
			}else{
				limitesPrecio.put("precioMin", "Precio Fijo");
				limitesPrecio.put("precioMax", "");
			}
			
			list.put(limitesPrecio);
			
			//List<CapitalAsegurado> listaCapitalAsegurado = new ArrayList<CapitalAsegurado>();
			getWriterJSON(response, list); 
		}catch(Exception excepcion){
			getWriterJSON(response, new JSONArray().put(""));
			logger.error("Excepcion : RevProduccionPrecioController - getLimitesProduccion_ajax", excepcion);
		}
	}

	public final void setRevProduccionPrecioManager(final RevProduccionPrecioManager revProduccionPrecioManager) {
		this.revProduccionPrecioManager = revProduccionPrecioManager;
	}

	public void setCapitalAseguradoManager(CapitalAseguradoManager capitalAseguradoManager) {
		this.capitalAseguradoManager = capitalAseguradoManager;
	}

	public void setCalculoPrecioProduccionManager(
			CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
	
}
