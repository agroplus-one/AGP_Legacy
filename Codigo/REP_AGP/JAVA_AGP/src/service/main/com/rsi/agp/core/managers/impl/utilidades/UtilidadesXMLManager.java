package com.rsi.agp.core.managers.impl.utilidades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.util.ComparativaPolizaComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ModuloPolizaComparator;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IUtilidadesXMLDAO;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class UtilidadesXMLManager implements IManager {

	private static final Log LOGGER = LogFactory.getLog(UtilidadesXMLManager.class);

	private WebServicesManager webServicesManager;
	private IPolizaDao polizaDao;
	private IUtilidadesXMLDAO utilidadesXMLDAO;

	public String generarXML(final Long idPoliza, final String servicio) throws BusinessException {
		StringBuilder xml = new StringBuilder();
		LOGGER.debug("init - UtilidadesXMLManager - generarXML");
		try {
			Poliza poliza = this.polizaDao.getPolizaById(idPoliza);
			if (poliza == null) {
				throw new BusinessException("La p\u00F3liza no existe en el sistema.");
			} else {
				Boolean esGanado = poliza.getLinea().isLineaGanado();
				List<ComparativaPoliza> listComparativasPoliza = poliza.getComparativaPolizas() != null
						? Arrays.asList(poliza.getComparativaPolizas().toArray(new ComparativaPoliza[] {}))
						: new ArrayList<ComparativaPoliza>();
				Collections.sort(listComparativasPoliza, new ComparativaPolizaComparator());
				List<ModuloPoliza> modulosPoliza = polizaDao.getLstModulosPoliza(poliza.getIdpoliza());
				Collections.sort(modulosPoliza, new ModuloPolizaComparator());
				for (ModuloPoliza mp : modulosPoliza) {
					xml.append("################################################################");
					xml.append(System.getProperty("line.separator"));
					xml.append("M\u00F3dulo: " + mp.getId().getCodmodulo());
					xml.append(System.getProperty("line.separator"));
					xml.append("################################################################");
					xml.append(System.getProperty("line.separator"));
					ComparativaPoliza cp = null;
					for (ComparativaPoliza cpAux : listComparativasPoliza) {
						if (mp.getId().getNumComparativa().equals(cpAux.getId().getIdComparativa())) {
							cp = cpAux;
							break;
						}
					}
					if (cp == null) {
						cp = new ComparativaPoliza();
						ComparativaPolizaId cpId = new ComparativaPolizaId();
						cpId.setIdpoliza(poliza.getIdpoliza());
						cpId.setLineaseguroid(poliza.getLinea().getLineaseguroid());
						cpId.setCodmodulo(mp.getId().getCodmodulo());
						cpId.setIdComparativa(mp.getId().getNumComparativa());
						if (esGanado) {
							List<RiesgoCubiertoModuloGanado> lstrcm = polizaDao.getRiesgoCubiertosModuloGanado(
									poliza.getLinea().getLineaseguroid(), mp.getId().getCodmodulo());
							if (lstrcm != null && !lstrcm.isEmpty()) {
								RiesgoCubiertoModuloGanado rcm = lstrcm.get(0);
								cp.setRiesgoCubiertoModuloGanado(rcm);
								cpId.setFilamodulo(BigDecimal.valueOf(rcm.getId().getFilamodulo()));
								cpId.setCodconceptoppalmod(rcm.getConceptoPpalModulo().getCodconceptoppalmod());
								cpId.setCodriesgocubierto(rcm.getRiesgoCubierto().getId().getCodriesgocubierto());
							} else {
								cp.setRiesgoCubiertoModuloGanado(new RiesgoCubiertoModuloGanado());
								cpId.setFilamodulo(new BigDecimal(999));
								cpId.setCodconceptoppalmod(new BigDecimal(999));
								cpId.setCodriesgocubierto(new BigDecimal(999));
							}
							cpId.setCodconcepto(new BigDecimal(999));
							cpId.setFilacomparativa(new BigDecimal(99));
							cpId.setCodvalor(new BigDecimal(-2));
						} else {
							List<RiesgoCubiertoModulo> lstrcm = polizaDao.getRiesgoCubiertosModulo(
									poliza.getLinea().getLineaseguroid(), mp.getId().getCodmodulo());
							if (lstrcm != null && !lstrcm.isEmpty()) {
								RiesgoCubiertoModulo rcm = lstrcm.get(0);
								cp.setRiesgoCubiertoModulo(rcm);
								cpId.setFilamodulo(rcm.getId().getFilamodulo());
								cpId.setCodconceptoppalmod(rcm.getConceptoPpalModulo().getCodconceptoppalmod());
								cpId.setCodriesgocubierto(rcm.getRiesgoCubierto().getId().getCodriesgocubierto());
							} else {
								cp.setRiesgoCubiertoModulo(new RiesgoCubiertoModulo());
								cpId.setFilamodulo(new BigDecimal(999));
								cpId.setCodconceptoppalmod(new BigDecimal(999));
								cpId.setCodriesgocubierto(new BigDecimal(999));
							}
							cpId.setCodconcepto(new BigDecimal(999));
							cpId.setFilacomparativa(new BigDecimal(99));
							cpId.setCodvalor(new BigDecimal(-2));
						}
						cp.setId(cpId);
					}
					if ("CL".equals(servicio)) {
						Map<Character, ComsPctCalculado> comsPctCalculado = this.webServicesManager
								.getComsPctCalculadoComp(cp.getId().getIdComparativa());
						xml.append(WSUtils.generateXMLPoliza(poliza, cp, Constants.WS_CALCULO, polizaDao, null, null,
								true, comsPctCalculado));
					} else if ("PD".equals(servicio)) {
						Map<Character, ComsPctCalculado> comsPctCalculado = this.webServicesManager
								.getComsPctCalculadoComp(cp.getId().getIdComparativa());
						xml.append(WSUtils.generateXMLPoliza(poliza, cp, Constants.WS_CONFIRMACION, polizaDao, null,
								null, true, comsPctCalculado));
					} else if ("MC".equals(servicio)) {
						xml.append(WSUtils.generateXMLPolizaModulosCoberturas(poliza, null, null, null,
								mp.getId().getCodmodulo(), polizaDao));
					} else if ("VL".equals(servicio)) {
						xml.append(WSUtils.generateXMLPoliza(poliza, cp, Constants.WS_VALIDACION, polizaDao, null, null,
								false, null));
					} else {
						xml.append("");
					}
					xml.append(System.getProperty("line.separator"));
					xml.append("################################################################");
					xml.append(System.getProperty("line.separator"));
					xml.append("################################################################");
					xml.append(System.getProperty("line.separator"));
					xml.append(System.getProperty("line.separator"));
				}
			}
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		LOGGER.debug("end - UtilidadesXMLManager - generarXML");
		return xml.toString();
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	public String getXMLCalculo(String idPoliza, String filaComparativa) throws DAOException {
		return this.utilidadesXMLDAO.getXMLCalculo(idPoliza, filaComparativa);
	}
	
	public String getXMLValidacion(String idPoliza, String filaComparativa) throws DAOException {
		return this.utilidadesXMLDAO.getXMLValidacion(idPoliza, filaComparativa);
	}
	
	public void setUtilidadesXMLDAO(final IUtilidadesXMLDAO utilidadesXMLDAO) {
		this.utilidadesXMLDAO = utilidadesXMLDAO;
	}
}
