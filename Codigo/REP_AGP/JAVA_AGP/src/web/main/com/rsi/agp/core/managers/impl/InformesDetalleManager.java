package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.contratacion.PolizaDocument;
import es.agroseguro.contratacion.impl.PolizaDocumentImpl;

public class InformesDetalleManager implements IManager {

	private static final Log logger = LogFactory.getLog(InformesDetalleManager.class);

	private InformesExcelManager informesExcelManager;
	private IPolizaDao polizaDao;
	private WebServicesManager webServicesManager;

	public HSSFWorkbook informesDetallePoliza(Long idPoliza) throws BusinessException {
		logger.debug("InformesDetalleManager - informesDetallePoliza # Init");

		try {
			Poliza poliza = polizaDao.getPolizaById(idPoliza);

			// Solo polizas agricolas en estado definitiva
			if (!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
				throw new BusinessException("La poliza no esta en un estado que permita generar el informe");
			} 
			else if (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0) {
				throw new BusinessException("Solo se puede generar informes de polizas de agricola");
			}

			// Obtener detalle de las parcelas desde la poliza
			return informesExcelManager.generarDetallePolizaParcelas(poliza,
					new ArrayList<Parcela>(poliza.getParcelas()));

		} catch (DAOException e) {
			logger.error("Error al generar el informesDetalle" + e.getMessage());
			throw new BusinessException();
		}

	}

	public HSSFWorkbook informesDetalleSitAct(Long idPoliza, String realPath) throws BusinessException {
		logger.debug("InformesDetalleManager - informesDetalleSitAct # Init");

		try {
			Poliza poliza = polizaDao.getPolizaById(idPoliza);

			// Solo polizas agricolas en estado definitiva
			if (!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
				throw new BusinessException("La poliza no esta en un estado que permita generar el informe");
			} else if (poliza.getLinea().getEsLineaGanadoCount().compareTo(new Long(1)) == 0
				) {
				throw new BusinessException("Solo se puede generar informes de polizas de agricola");
			}
			
			PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
			SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
			
			

			respuesta = helper.getPolizaActualizada(poliza.getReferencia(), poliza.getLinea().getCodplan(), realPath);
			
			es.agroseguro.contratacion.Poliza polizaSWAct = null;
			
			if (poliza.getCodmodulo().contains("C")) {
				polizaSWAct = respuesta.getPolizaComplementariaUnif().getPoliza();
			}
			else {
				polizaSWAct = respuesta.getPolizaPrincipalUnif().getPoliza();
			}

			// Obtener poliza desde SW situacion actualizada
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaSW = webServicesManager
					.consultaCostesRecibo(poliza.getTipoReferencia() + "", poliza.getLinea().getCodplan(),
							poliza.getReferencia(), null, null, realPath);

			return informesExcelManager.generarDetallePolizaParcelasSitAct(polizaSW, poliza, polizaSWAct);

		} catch (DAOException e) {
			logger.error("Error al generar el informesDetalle" + e.getMessage());
			throw new BusinessException();
		} catch (Exception e) {
			logger.error("Error al generar el informesDetalle" + e.getMessage());
			throw new BusinessException();
		} 
		
	}

	public void setInformesExcelManager(InformesExcelManager informesExcelManager) {
		this.informesExcelManager = informesExcelManager;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
}
