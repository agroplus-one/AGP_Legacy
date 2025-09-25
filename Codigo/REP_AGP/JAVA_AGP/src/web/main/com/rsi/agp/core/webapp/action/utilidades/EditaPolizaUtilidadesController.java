package com.rsi.agp.core.webapp.action.utilidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.CargaAseguradoManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.ColectivoManager;
import com.rsi.agp.core.managers.impl.ConsultaDetallePolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.ProduccionVO;

public class EditaPolizaUtilidadesController extends MultiActionController{
	
	private AseguradoManager aseguradoManager;
	private CargaAseguradoManager cargaAseguradoManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private ClaseManager claseManager;
	private ColectivoManager colectivoManager;
	private ConsultaDetallePolizaManager consultaDetallePolizaManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	/*DNF 24/07/2020 PET.63485*/
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	/*FIN DNF 24/07/2020 PET.63485*/
	
	public ModelAndView doEditaPoliza(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		ModelAndView mv= null;
		try {
			final String operacion         = request.getParameter("operacion"); 
			final String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			Long idPoliza = null;
			if (!"".equals(StringUtils.nullToString(request.getParameter("idpoliza")))) {
				idPoliza = Long.parseLong(request.getParameter("idpoliza"));
			}
			polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
			
			// comprobamos que el asegurado no este bloqueado
			boolean idAseguradoDisponible = cargaAseguradoManager.isAseguradoDisponible(usuario.getCodusuario(), polizaBean.getAsegurado().getId());
			
			if (!idAseguradoDisponible){
				String codusuario = cargaAseguradoManager.getUsuarioAseguradoCargado(polizaBean.getAsegurado().getId());
				logger.debug("El asegurado " + polizaBean.getAsegurado().getId() + " ya se encuentra cargado por el usuario " + codusuario);
				parameters.put("alerta", "El asegurado ya se encuentra cargado por el usuario: " + codusuario);
				parameters.put("recogerPolizaSesion", "true");
				mv = new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parameters); 
			}else{
				
				
				
				// PET 63485 DNF 11/08/2020
				// ----------------------------------------------------------------
				// RECALCULAR PARCELAS
				// ----------------------------------------------------------------
				
				if ("recalcularParcela".equalsIgnoreCase(operacion)) {
					
					logger.debug("init: EditaPolizaUtilidadesController - recalcularParcela");
					
					Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();
				
					for (Parcela par : polizaBean.getParcelas()) {
						
				        colIdParcelasParaRecalculo.add(par.getIdparcela());
				    }
					
					List<String> codsModuloPoliza = new ArrayList<String>();

					Parcela parcela = new Parcela();
					parcela.getPoliza().setIdpoliza(polizaBean.getIdpoliza());
					List<Parcela> parcelas = seleccionPolizaManager.getParcelas(
							parcela, null, null);

					boolean recalcularRendimientoConSWprueba = calculoPrecioProduccionManager
							.calcularRendimientoProdConSW();

					try {
					if (recalcularRendimientoConSWprueba) {

						Map<String, ProduccionVO> mapaRendimientosProd;
						
						
							mapaRendimientosProd = calculoPrecioProduccionManager
										.calcularRendimientosPolizaWS(polizaBean.getIdpoliza(),
												null, realPath, usuario.getCodusuario(), 0);
						
						

						for (ComparativaPoliza comp : polizaBean.getComparativaPolizas()) {
							if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
								codsModuloPoliza.add(comp.getId().getCodmodulo());
						}

						seleccionPolizaManager.recalculoPrecioProduccion(parcelas,
								codsModuloPoliza, recalcularRendimientoConSWprueba,
								mapaRendimientosProd);

					} else {
						seleccionPolizaManager.recalculoPrecioProduccion(parcelas,
								codsModuloPoliza);
					}
					} catch (Throwable e) {
						logger.error("Excepcion : EditaPolizaUtilidadesController - doEditaPoliza", e);
					}
				}
				// ----------------------------------------------------------------
				// FIN RECALCULAR PARCELAS
				// ----------------------------------------------------------------
				
				
				//cargamos el colectivo
				Colectivo colectivo = colectivoManager.cargar(request,parameters,bundle,polizaBean.getColectivo().getId());
				if (colectivo != null){
					//cargamos el asegurado
					Asegurado aseg = aseguradoManager.getAseguradoFacturacion(polizaBean.getAsegurado().getId(), usuario,Constants.FACTURA_CONSULTA);
					parameters = cargaAseguradoManager.cargaAsegurado(aseg,request, usuario);
					
					if (StringUtils.nullToString(parameters.get("alerta")).equals("")){ 
						//cargamos la clase
						Long idClase = claseManager.getClase(polizaBean.getLinea().getLineaseguroid(), polizaBean.getClase());
						parameters = claseManager.cargaClase(request,idClase);
						parameters.remove("mensaje"); //Borramos el mensaje de clase cargada ok
						if (StringUtils.nullToString(parameters.get("alerta")).equals("")){
							parameters.put("vieneDeUtilidades", "true");
							parameters.put("operacion", "editar");
							parameters.put("idpoliza", polizaBean.getIdpoliza());
							if (polizaBean.getTipoReferencia().equals(new Character('P')))
								mv = new ModelAndView(new RedirectView("seleccionPoliza.html")).addAllObjects(parameters);
							else{
								consultaDetallePolizaManager.cargaCabecera (polizaBean,request);
								parameters.put("idpolizaCpl", idPoliza);
								mv = new ModelAndView(new RedirectView("polizaComplementaria.html")).addAllObjects(parameters);
							}
						
						}else{
							parameters.put("recogerPolizaSesion", "true");
							mv = new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parameters);
						}
					}else{
						parameters.put("recogerPolizaSesion", "true");
						mv = new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parameters); 
					}
					
				}else{
					parameters.put("alerta", "mensaje.edita.poliza.colectivo.KO");
					parameters.put("recogerPolizaSesion", "true");
					mv = new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parameters); 
				}
			}
			
		} catch (BusinessException e) {
			logger.error(bundle.getString("mensaje.edita.poliza.KO") + e.getMessage(), e);
			parameters.put("alerta",bundle.getString("mensaje.edita.poliza.KO"));
			parameters.put("recogerPolizaSesion", "true");
			mv = new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parameters);
			
		} catch (Exception e) {
			logger.error(bundle.getString("mensaje.edita.poliza.KO") + e.getMessage(), e);
			parameters.put("alerta",bundle.getString("mensaje.edita.poliza.KO"));
			parameters.put("recogerPolizaSesion", "true");
			mv = new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parameters);
		}
		
		
		return mv;
		
	}

	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}
	
	public void setCargaAseguradoManager(CargaAseguradoManager cargaAseguradoManager) {
		this.cargaAseguradoManager = cargaAseguradoManager;
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}

	public void setColectivoManager(ColectivoManager colectivoManager) {
		this.colectivoManager = colectivoManager;
	}

	public void setConsultaDetallePolizaManager(
			ConsultaDetallePolizaManager consultaDetallePolizaManager) {
		this.consultaDetallePolizaManager = consultaDetallePolizaManager;
	}

	
	/*DNF 24/07/2020 PET.63485*/
	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
	/*FIN DNF 24/07/2020 PET.63485*/
		

}
