package com.rsi.agp.core.webapp.action.anexoMod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.IAnexoModCambioMasivoSWManager;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModSWCambioMasivo;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.vo.ProduccionVO;

public class AnexoModCambioMasivoSWController extends BaseMultiActionController {

	private static final Log logger = LogFactory
			.getLog(AnexoModCambioMasivoSWController.class);
	private IAnexoModCambioMasivoSWManager anexoModCambioMasivoSWManager;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public ModelAndView doCambioMasivoSW(HttpServletRequest request,
			HttpServletResponse response,
			AnexoModSWCambioMasivo anexoModSWCambioMasivo) {

		logger.debug("Init:doCambioMasivoSW - AnexoModCambioMasivoSWController");
		ModelAndView mv = null;
		String mensajeError = "";
		Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();
		HashMap<String, String> mensajesError = new HashMap<String, String>();

		try {
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			boolean recalcularRendimientoConSW = calculoPrecioProduccionManager
					.calcularRendimientoProdConSW();
			Usuario usuario = (Usuario) request.getSession().getAttribute(
					"usuario");
			Long idAnexoMod = anexoModSWCambioMasivo.getIdAnexoMod();

			String tipoListadoGrid = request.getParameter("tipoListadoGridCM");
			String idsRowsCheckedCM = StringUtils.nullToString(request
					.getParameter("idsRowsCheckedCM"));

			// En caso de que se modificaquen datos variables que afecten al
			// precio o produccion
			// (todos menos los campos para precio,produccion y tipoplantacion)
			// se le pregunta al usuario si quiere recalcular
			String recalcularPrecioProd = StringUtils.nullToString(request
					.getParameter("recalcularPrecioProd"));

			mv = new ModelAndView(new RedirectView(
					"parcelasAnexoModificacion.html"));

			colIdParcelasParaRecalculo = anexoModCambioMasivoSWManager
					.cambioMasivoSW(anexoModSWCambioMasivo, idsRowsCheckedCM,
							mensajesError,false);

			// si la ubicacion es incorrecta no recalculamos, ni hacemos nada
			// mas.
			if (!mensajesError.containsKey("alerta")) {
				try {
					if (recalcularPrecioProd.equals("si")) {

						logger.debug("doCambioMasivoSW -Recalculamos precio y produccion");
						Map<String, ProduccionVO> mapaRendimientosProd = null;
						Set<com.rsi.agp.dao.tables.anexo.Parcela> colParcelasRecalculo = new HashSet<com.rsi.agp.dao.tables.anexo.Parcela>();
						AnexoModificacion anexoMod = declaracionesModificacionPolizaManager
								.getAnexoModifById(idAnexoMod);
						
						logger.debug("TAMARA -recalcularRendimientoConSW: " +recalcularRendimientoConSW);
						if (recalcularRendimientoConSW) {
							logger.debug("TAMARA - entro por aqui ");
							// Preparar datos con servicio web
							// es.agroseguro.seguroAgrario.modificacion.Poliza
							// pol =
							// anexoModCambioMasivoSWManager.obtenerPolizaAnexo(anexoMod,
							// usuario);
							mapaRendimientosProd = calculoPrecioProduccionManager
									.calcularRendimientosAnexoWS(anexoMod,
											colIdParcelasParaRecalculo,
											realPath, usuario, 0);

						}// else{

						// }
						for (com.rsi.agp.dao.tables.anexo.Parcela p : anexoMod
								.getParcelas()) {
							if (colIdParcelasParaRecalculo.contains(p.getId())) {
								colParcelasRecalculo.add(p);
							}
						}
						logger.debug("**@@** Valor de colParcelasRecalculo:"+colParcelasRecalculo);
						anexoModCambioMasivoSWManager
								.recalculaPrecioProduccion(
										colParcelasRecalculo,
										recalcularRendimientoConSW,
										mapaRendimientosProd);
					}
					
					// cambio masivo OK
					//mv.addObject("mensaje",
						//	bundle.getString("mensaje.parcela.cambioMasivo.OK"));
					mv.addObject("mensaje","Se ha realizado el cambio masivo correctamente");

				} catch (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e) {
					mensajeError = "Aviso: No se ha podido realizar el recálculo de precio/producción:\n"
							+ CalculoPrecioProduccionManager
									.getMsgAgrExceptionMasivo(e);
				} catch (Throwable e) {
					mensajeError = e.getMessage();
				}
				colIdParcelasParaRecalculo = anexoModCambioMasivoSWManager
						.cambioMasivoSW(anexoModSWCambioMasivo, idsRowsCheckedCM,
								mensajesError,true);
			} else {
				mv.addObject("alerta2", mensajesError.get("alerta"));
				//mv.addObject("alerta",
					//	bundle.getString("mensaje.parcela.cambioMasivo.KO"));
				mv.addObject("alerta","Error al realizar el cambio masivo ");
			}

			String vieneDeListadoAnexosMod = StringUtils.nullToString(request
					.getParameter("vieneDeListadoAnexosMod"));
			if ("true".equals(vieneDeListadoAnexosMod)) {
				mv.addObject("vieneDeListadoAnexosMod", vieneDeListadoAnexosMod);
			}
			mv.addObject("idPoliza", anexoModSWCambioMasivo.getIdPoliza());
			mv.addObject("tipoListadoGrid", tipoListadoGrid);
			mv.addObject("idAnexo", anexoModSWCambioMasivo.getIdAnexoMod());
			mv.addObject("vieneCambioMasivo", "true");
			mv.addObject("idCupon", anexoModSWCambioMasivo.getIdCupon());
			mv.addObject("idCuponStr", StringUtils.nullToString(request
					.getParameter("idCuponStr")));

			if (mensajeError != null && !"".equals(mensajeError)) {
				//mv.addObject("alerta",
					//	bundle.getString("mensaje.parcela.cambioMasivo.KO"));
				mv.addObject("alerta","Error al realizar el cambio masivo ");
				mv.addObject("alerta2", mensajeError);
			}

			logger.debug("end: doCambioMasivoSW - AnexoModCambioMasivoSWController");

		} catch (Exception e) {
			logger.error(
					"Error al hacer le cambio masivo en las parcelas de anexo de modificacion",
					e);

		}
		return mv;
	}

	public void setAnexoModCambioMasivoSWManager(
			IAnexoModCambioMasivoSWManager anexoModCambioMasivoSWManager) {
		this.anexoModCambioMasivoSWManager = anexoModCambioMasivoSWManager;
	}

	public void setCalculoPrecioProduccionManager(
			CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}
}