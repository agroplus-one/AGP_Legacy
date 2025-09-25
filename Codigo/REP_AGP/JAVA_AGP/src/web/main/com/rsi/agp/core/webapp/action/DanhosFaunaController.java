package com.rsi.agp.core.webapp.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.rsi.agp.core.managers.impl.DanhosFaunaManager;
import com.rsi.agp.core.managers.impl.DatosParcelaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.DanhosFaunaVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.SigpacVO;

import es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFauna;
import es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.ReduccionProduccion;

public class DanhosFaunaController extends BaseMultiActionController {

	ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private static final Log logger = LogFactory.getLog(DanhosFaunaController.class);
	private SeleccionPolizaManager seleccionPolizaManager;
	private DanhosFaunaManager danhosFaunaManager;
	private DatosParcelaManager datosParcelaManager;

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)
			throws Exception {

		logger.debug("init: DanhosFaunaController - doConsulta");
		ModelAndView mv = null;
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		String tipoListadoGrid = request.getParameter("tipoListadoGridDF");
		Long idPoliza = null;

		if (!"".equals(StringUtils.nullToString(request.getParameter("idPolizaDF")))) {
			idPoliza = Long.parseLong(request.getParameter("idPolizaDF"));
		}

		polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);

		// Recupero los checks marcados de las parcelas que se verán afectadas al precio
		// masivo
		String idsRowsChecked = StringUtils.nullToString(request.getParameter("idsRowsCheckedDF"));

		Map<String, DanhosFaunaVO> mapaSigpacDanhosFauna = new HashMap<String, DanhosFaunaVO>();

		for (Long idRowSeleccionado : getListParcelas(idsRowsChecked)) {

			for (Parcela parcela : polizaBean.getParcelas()) {
				Long idPar = parcela.getIdparcela();

				if (idPar.equals(idRowSeleccionado)) {
					SigpacVO sigpacVO = new SigpacVO();

					ParcelaVO parVO = this.datosParcelaManager.getParcela(parcela.getIdparcela());

					sigpacVO.setProv(parVO.getProvinciaSigpac());
					sigpacVO.setTerm(parVO.getTerminoSigpac());
					sigpacVO.setAgr(parVO.getAgregadoSigpac());
					sigpacVO.setZona(parVO.getZonaSigpac());
					sigpacVO.setPol(parVO.getPoligonoSigpac());
					sigpacVO.setParc(parVO.getParcelaSigpac());
					sigpacVO.setRecinto(parVO.getRecintoSigpac());
					sigpacVO.setCodPlan(request.getParameter("codplanDF"));
					sigpacVO.setCodLinea(request.getParameter("codlineaDF"));

					String sigPac = "";
					if (parVO.getProvinciaSigpac() != null) {
						sigPac = parVO.getProvinciaSigpac().toString();
					}
					if (parVO.getTerminoSigpac() != null) {
						sigPac += "-" + parVO.getTerminoSigpac().toString();
					}
					if (parVO.getAgregadoSigpac() != null) {
						sigPac += "-" + parVO.getAgregadoSigpac().toString();
					}
					if (parVO.getZonaSigpac() != null) {
						sigPac += "-" + parVO.getZonaSigpac().toString();
					}
					if (parVO.getPoligonoSigpac() != null) {
						sigPac += "-" + parVO.getPoligonoSigpac().toString();
					}
					if (parVO.getParcelaSigpac() != null) {
						sigPac += "-" + parVO.getParcelaSigpac().toString();
					}
					if (parVO.getRecintoSigpac() != null) {
						sigPac += "-" + parVO.getRecintoSigpac().toString();
					}

					if (!mapaSigpacDanhosFauna.containsKey(sigPac)) {
						DanhosFaunaVO danhosFaunaVO = new DanhosFaunaVO();
						InfoReduccionParcelaFauna danhosFaunaDatos = danhosFaunaManager.obtenerDanhosFauna(sigpacVO,
								realPath);
						if (danhosFaunaDatos != null) {
							ReduccionProduccion redProd = danhosFaunaDatos.getReduccionProduccionArray(0);
							if (redProd != null) {
								danhosFaunaVO.setDescripcion(redProd.getDescripcion());
								danhosFaunaVO.setEnVigor(redProd.getEnVigor());
								danhosFaunaVO.setFechaVigor(
										new SimpleDateFormat("dd/MM/yyyy").format(redProd.getFechaVigor().getTime()));
								danhosFaunaVO.setReduccionProducion(redProd.getReduccionProduccion());
								danhosFaunaVO.setTipoReduccion(redProd.getTipoReduccion());

								mapaSigpacDanhosFauna.put(sigPac, danhosFaunaVO);
							}
						}
					}
				}
			}
		}

		mv = new ModelAndView(new RedirectView("seleccionPoliza.html"));
		mv.addObject("idpoliza", idPoliza);
		mv.addObject("mostrarPopupDanhosFauna", "true");
		mv.addObject("operacion", "listParcelas");
		mv.addObject("tipoListadoGrid", tipoListadoGrid);

		if (mapaSigpacDanhosFauna != null) {
			request.getSession().setAttribute("mapDanhoFauna", mapaSigpacDanhosFauna);
		}

		logger.debug("end: DanhosFaunaController - doConsulta");

		return mv;

	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setDatosParcelaManager(DatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}

	private List<Long> getListParcelas(String cadenaParcelas) {
		List<Long> listaParcelas = new ArrayList<Long>();
		StringTokenizer token = new StringTokenizer(cadenaParcelas, ";");

		while (token.hasMoreTokens()) {
			listaParcelas.add(new Long(token.nextToken()));
		}

		return listaParcelas;
	}

	public DanhosFaunaManager getDanhosFaunaManager() {
		return danhosFaunaManager;
	}

	public void setDanhosFaunaManager(DanhosFaunaManager danhosFaunaManager) {
		this.danhosFaunaManager = danhosFaunaManager;
	}

}