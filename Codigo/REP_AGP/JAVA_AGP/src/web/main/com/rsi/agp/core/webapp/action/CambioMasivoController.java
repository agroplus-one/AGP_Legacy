package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.DatosParcelaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.CambioMasivoVO;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.PreciosProduccionesVO;
import com.rsi.agp.vo.ProduccionVO;

public class CambioMasivoController extends BaseMultiActionController {

	ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private static final Log logger = LogFactory.getLog(CambioMasivoController.class);
	private SeleccionPolizaManager seleccionPolizaManager;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	private DatosParcelaManager datosParcelaManager;

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)
			throws Exception {

		logger.debug("init: CambioMasivoController - doConsulta");
		ModelAndView mv = null;
		String mensajeError = "";
		Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();

		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		boolean recalcularRendimientoConSW = calculoPrecioProduccionManager.calcularRendimientoProdConSW();

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String tipoListadoGrid = request.getParameter("tipoListadoGridCM");
		// recuperamos el idpoliza
		Long idPoliza = null;// new Long(-1);
		if (!"".equals(StringUtils.nullToString(request.getParameter("idpolizaCM")))) {
			idPoliza = Long.parseLong(request.getParameter("idpolizaCM"));
		}
		polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
		CambioMasivoVO cambioMasivoVO = getCambioMasivoVO(request, polizaBean);

		// recogemos campo recalcular
		String recalcular = request.getParameter("recalcular");

		// [inicio] cambio masivo
		List<String> lstIdsParTemp = new ArrayList<String>();
		for (Long idP : cambioMasivoVO.getListaParcelas()) {
			lstIdsParTemp.add(idP.toString());
		}
		List<String> lstCadenasIds = getListasParaIN(lstIdsParTemp);
		try {
			colIdParcelasParaRecalculo = seleccionPolizaManager.cambioMasivo(cambioMasivoVO, polizaBean, usuario,
					recalcularRendimientoConSW, recalcular, lstCadenasIds, false);

		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			mensajeError = "La ubicaciï¿½n resultante es incorrecta";
		} catch (TransactionSystemException t) {
			mensajeError = "La ubicaciï¿½n resultante es incorrecta";
		} catch (Throwable e) {
			mensajeError = e.getMessage();
		}
		if ("".equals(mensajeError)) {
			Set<Parcela> colParcelasRecalculo = new HashSet<Parcela>();
			try {
				// Si es con web service y hay parcelas para recï¿½lculo
				if (recalcularRendimientoConSW && colIdParcelasParaRecalculo.size() > 0) {

					polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
					// Mapa codModulo-hoja-numero
					Map<String, ProduccionVO> mapaRendimientosProd = calculoPrecioProduccionManager
							.calcularRendimientosPolizaWS(polizaBean.getIdpoliza(), colIdParcelasParaRecalculo,
									realPath, usuario.getCodusuario(), 0);
					List<String> codsModuloPoliza = new ArrayList<String>();

					Set<ModuloPoliza> comparativas = polizaBean.getModuloPolizas();
					for (ModuloPoliza comp : comparativas) {
						if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
							codsModuloPoliza.add(comp.getId().getCodmodulo());
					}

					// Para recargarlo con los hoja-numero y obtener la colecciï¿½n de parcelas
					// polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
					Set<Parcela> colParcela = polizaBean.getParcelas();

					Iterator<Parcela> it = colParcela.iterator();
					while (it.hasNext()) {
						Parcela par = it.next();
						if (colIdParcelasParaRecalculo.contains(par.getIdparcela())) {
							colParcelasRecalculo.add(par);
						}
					}
					seleccionPolizaManager.recalculoPrecioProduccion(colParcelasRecalculo, codsModuloPoliza,
							recalcularRendimientoConSW, mapaRendimientosProd);
				}

			} catch (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e) {
				mensajeError = "Aviso: No se ha podido realizar el recï¿½lculo de precio/producciï¿½n:\n"
						+ CalculoPrecioProduccionManager.getMsgAgrExceptionMasivo(e);
			} catch (Throwable e) {
				mensajeError = e.getMessage();
			}
			try {
				colIdParcelasParaRecalculo = seleccionPolizaManager.cambioMasivo(cambioMasivoVO, polizaBean, usuario,
						recalcularRendimientoConSW, "true", lstCadenasIds, true);
				// el ultimo boolean indica si se quiere guardar solo precio y produccion

			} catch (java.sql.SQLIntegrityConstraintViolationException e) {
				mensajeError = "La ubicaciï¿½n resultante es incorrecta";
			} catch (TransactionSystemException t) {
				mensajeError = "La ubicaciï¿½n resultante es incorrecta";
			} catch (Throwable e) {
				mensajeError = e.getMessage();
			}
		}
		// [fin] cambio masivo

		mv = new ModelAndView(new RedirectView("seleccionPoliza.html"));

		mv.addObject("idpoliza", idPoliza);
		mv.addObject("operacion", "listParcelas");
		mv.addObject("tipoListadoGrid", tipoListadoGrid);
		mv.addObject("polizaBean", polizaBean);

		if (mensajeError != null && !"".equals(mensajeError)) {
			mv.addObject("alerta2", mensajeError);
			// mv.addObject("alerta",bundle.getString("mensaje.parcela.cambioMasivo.KO"));
			mv.addObject("alerta", "Error al realizar el cambio masivo ");
		} else {
			// mv.addObject("mensaje",bundle.getString("mensaje.parcela.cambioMasivo.OK"));
			mv.addObject("mensaje", "Se ha realizado el cambio masivo correctamente");
		}
		logger.debug("end: CambiomasivoController - doConsulta");
		return mv;
	}

	public ModelAndView doBorrarMasivo(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)
			throws Exception {
		logger.debug("init: CambioMasivoController - doBorrarMasivo");
		ModelAndView mv = null;

		String tipoListadoGrid = request.getParameter("tipoListadoGridCM");
		// recuperamos el idpoliza
		Long idPoliza = null;// new Long(-1);
		if (!"".equals(StringUtils.nullToString(request.getParameter("idpolizaCM")))) {
			idPoliza = Long.parseLong(request.getParameter("idpolizaCM"));
		}

		polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
		CambioMasivoVO cambioMasivoVO = getCambioMasivoVO(request, polizaBean);

		// [inicio] borrado masivo
		boolean correcto = true;
		try {
			correcto = seleccionPolizaManager.borradoMasivo(cambioMasivoVO, polizaBean);
		} catch (BusinessException e) {
			correcto = false;
		}
		// [fin] borrado masivo

		mv = new ModelAndView(new RedirectView("seleccionPoliza.html"));

		mv.addObject("idpoliza", idPoliza);
		mv.addObject("operacion", "listParcelas");
		mv.addObject("tipoListadoGrid", tipoListadoGrid);
		mv.addObject("polizaBean", polizaBean);
		if (correcto) {
			seleccionPolizaManager.marcarRecalculoHojaYNum(idPoliza);
			mv.addObject("mensaje", bundle.getString("mensaje.parcela.borradoMasivo.OK"));
		} else {
			mv.addObject("alerta", bundle.getString("mensaje.parcela.borradoMasivo.KO"));
		}

		logger.debug("end: CambiomasivoController - doBorrarMasivo");
		return mv;
	}
	
	/* Pet. 643485 - FASE III DNF 23/12/2020 */
	/* Método para calcular el precio masivo de las parcelas seleccionadas en listado parcelas*/
	public ModelAndView doPrecioMasivo(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) throws Exception {
		logger.debug("init: CambioMasivoController - doPrecioMasivo");
		ModelAndView mv = null;
		PreciosProduccionesVO preciosProduccionesVO = new PreciosProduccionesVO();
		
		String tipoListadoGrid   = request.getParameter("tipoListadoGridCM");
		
		Long idPoliza = null;
		if (!"".equals(StringUtils.nullToString(request.getParameter("idPolizaCM")))) {
			idPoliza = Long.parseLong(request.getParameter("idPolizaCM"));
		}
		
		polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
		
		//Recupero los checks marcados de las parcelas que se verán afectadas al precio masivo
		String idsRowsChecked    = StringUtils.nullToString(request.getParameter("idsRowsCheckedCM"));
		
        //recorro las parcelas y contrasto el id con el id de las parcelas checkeadas
		for (Long idRowSeleccionado : getListParcelas(idsRowsChecked)) {
			for (Parcela parcela : polizaBean.getParcelas()) {
				
				Long idPar = parcela.getIdparcela();
				if (idPar.equals(idRowSeleccionado)) {
					
					ParcelaVO parVO =  this.datosParcelaManager.getParcela(parcela.getIdparcela()); 
					for(CapitalAseguradoVO ca : parVO.getCapitalesAsegurados()) { 
 
						parVO.setCapitalAsegurado(ca);
						logger.debug("Parcela con id: "+idRowSeleccionado+", seleccionada para el precio masivo");
						/*** PET.63485.FIII DNF 09/02/2021 recupero el precio sin llamar al WS*/
						//preciosProduccionesVO = this.calculoPrecioProduccionManager
						//		.getProduccionPrecioPolizaWS(parVO, 
						//				this.getServletContext().getRealPath("/WEB-INF/"), usuario.getCodusuario());
						
						preciosProduccionesVO = this.calculoPrecioProduccionManager
								.getPrecioPolizaCalculoPrecio(parVO);
						/*** fin PET.63485.FIII DNF 09/02/2021 recupero el precio sin llamar al WS*/
						if (!preciosProduccionesVO.getListPrecios().isEmpty()) {
							String precioMax = null;
							for(PrecioVO valor : preciosProduccionesVO.getListPrecios()) {
									
								precioMax = valor.getLimMax() == null ? "0" : valor.getLimMax();
								
								parVO.getCapitalAsegurado().setPrecio(precioMax);
								parVO.getCapitalAsegurado().setListPrecios(preciosProduccionesVO.getListPrecios());
							}
							//Guardamos la parcela con el precio
							datosParcelaManager.guardarParcela(parVO);
						}	
					}	
				}
			}
        }
			
		mv = new ModelAndView(new RedirectView("seleccionPoliza.html"));
		
		mv.addObject("idpoliza", idPoliza);
		mv.addObject("operacion", "listParcelas");	
		mv.addObject("tipoListadoGrid", tipoListadoGrid);
		mv.addObject("polizaBean", polizaBean);
		
		logger.debug("end: CambioMasivoController - doPrecioMasivo");
		return mv;
	}
	/* fin Pet. 643485 - FASE III DNF 23/12/2020 */
	
	public ModelAndView doDuplicarMasivo(HttpServletRequest request,HttpServletResponse response, Poliza polizaBean) throws Exception {
		logger.debug("init: CambioMasivoController - doDuplicarMasivo");
		ModelAndView mv = null;

		String tipoListadoGrid = request.getParameter("tipoListadoGridDM");
		// recuperamos el idpoliza
		Long idPoliza = null;// new Long(-1);
		if (!"".equals(StringUtils.nullToString(request.getParameter("idpolizaDM")))) {
			idPoliza = Long.parseLong(request.getParameter("idpolizaDM"));
		}
		String cantDuplicar = request.getParameter("cantDuplicar");
		polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
		String idParcela = request.getParameter("idsRowsCheckedDM");
		List<Long> lstIdsParcelas = getListParcelas(idParcela);
		if (lstIdsParcelas.size() > 0) {
			idParcela = lstIdsParcelas.get(0).toString();
			// [inicio] duplicar masivo
			seleccionPolizaManager.duplicadoMasivo(idParcela, polizaBean, Long.parseLong(cantDuplicar));
			// [fin] duplicar masivo
		}
		mv = new ModelAndView(new RedirectView("seleccionPoliza.html"));

		mv.addObject("idpoliza", idPoliza);
		mv.addObject("operacion", "listParcelas");
		mv.addObject("tipoListadoGrid", tipoListadoGrid);
		mv.addObject("polizaBean", polizaBean);

		logger.debug("end: CambiomasivoController - doDuplicarMasivo");
		return mv;

	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
	/* Pet. 643485 - FASE III DNF 23/12/2020 */
	public void setDatosParcelaManager (DatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}
	/* fin Pet. 643485 - FASE III DNF 23/12/2020 */
	private CambioMasivoVO getCambioMasivoVO(HttpServletRequest request, Poliza polizaBean) {
		CambioMasivoVO cambioMasivoVO = new CambioMasivoVO();

		// cambioMasivoVO.setListaParcelas(getListParcelas(request.getParameter("checked_form_parcela_cm")));
		cambioMasivoVO.setListaParcelas(getListParcelas(request.getParameter("idsRowsCheckedCM")));
		cambioMasivoVO.setPolizaId(polizaBean.getIdpoliza());

		// ubicacion
		cambioMasivoVO.setProvincia_cm(request.getParameter("provincia_form_cm"));
		cambioMasivoVO.setComarca_cm(request.getParameter("comarca_form_cm"));
		cambioMasivoVO.setTermino_cm(request.getParameter("termino_form_cm"));
		cambioMasivoVO.setSubtermino_cm(request.getParameter("subtermino_form_cm"));
		// SIGPAC
		cambioMasivoVO.setProvSig_cm(request.getParameter("provSig_form_cm"));
		cambioMasivoVO.setTermSig_cm(request.getParameter("termSig_form_cm"));
		cambioMasivoVO.setAgrSig_cm(request.getParameter("agrSig_form_cm"));
		cambioMasivoVO.setZonaSig_cm(request.getParameter("zonaSig_form_cm"));
		cambioMasivoVO.setPolSig_cm(request.getParameter("polSig_form_cm"));
		cambioMasivoVO.setParcSig_cm(request.getParameter("parcSig_form_cm"));
		cambioMasivoVO.setRecSig_cm(request.getParameter("recSig_form_cm"));
		// cultivo y variedad
		cambioMasivoVO.setCultivo_cm(request.getParameter("cultivo_form_cm"));
		cambioMasivoVO.setVarieda_cm(request.getParameter("variedad_form_cm"));

		// produccion,superficie y precio
		cambioMasivoVO.setIncrene_ha_cm(request.getParameter("incremento_form_ha_cm"));
		cambioMasivoVO.setIncreme_parcela_cm(request.getParameter("incremento_form_parcela_cm"));
		cambioMasivoVO.setInc_unidades_cm(request.getParameter("incremento_form_unidades_cm"));
		cambioMasivoVO.setSuperficie_cm(request.getParameter("superficie_form_cm"));
		cambioMasivoVO.setPrecio_cm(request.getParameter("precio_form_cm"));

		// datos variables
		cambioMasivoVO.setDestino_cm(request.getParameter("destino_form_cm"));
		cambioMasivoVO.setTplantacion(request.getParameter("tipoPlant_form_cm"));
		cambioMasivoVO.setSistcultivo(request.getParameter("sistemaCultivo_form_cm"));
		cambioMasivoVO.setCodtipomarcoplantac_cm(request.getParameter("marcoPlant_form_cm"));
		cambioMasivoVO.setCodpracticacultural_cm(request.getParameter("practicaCul_form_cm"));
		cambioMasivoVO.setFechaFinGarantia_cm(request.getParameter("fechaFinGarantia_form_cm"));
		cambioMasivoVO.setFechaSiembra(request.getParameter("fechaSiembra_form_cm"));
		cambioMasivoVO.setEdad_cm(request.getParameter("edad_form_cm"));
		cambioMasivoVO.setIncEdad_cm(request.getParameter("incremento_edad_form_cm"));
		cambioMasivoVO.setUnidades_cm(request.getParameter("unidades_form_cm"));
		cambioMasivoVO.setSistproduccion_cm(request.getParameter("sistemaProduccion_form_cm"));
		return cambioMasivoVO;
	}

	private List<Long> getListParcelas(String cadenaParcelas) {
		List<Long> listaParcelas = new ArrayList<Long>();
		StringTokenizer token = new StringTokenizer(cadenaParcelas, ";");

		while (token.hasMoreTokens()) {
			listaParcelas.add(new Long(token.nextToken()));
		}

		return listaParcelas;
	}

	public List<String> getListasParaIN(List<String> lstPlzRenov) {
		List<String> lstCadenasIds = new ArrayList<String>();
		int contador = 0;
		String cadena = "";
		boolean primera = true;
		for (String id : lstPlzRenov) {
			if (contador < Constants.MAX_NUM_ELEM_OPERATOR_IN) {
				if (!primera)
					cadena = cadena + ",";
				else
					primera = false;
				cadena = cadena + id;
				contador++;
			} else {
				if (cadena.length() > 0)
					lstCadenasIds.add(cadena);
				cadena = id;
				contador = 1;
			}
		}
		lstCadenasIds.add(cadena);
		logger.debug("Numero total de elementos: " + lstPlzRenov.size() + ". Listas partidas: " + lstCadenasIds.size());
		return lstCadenasIds;
	}

}