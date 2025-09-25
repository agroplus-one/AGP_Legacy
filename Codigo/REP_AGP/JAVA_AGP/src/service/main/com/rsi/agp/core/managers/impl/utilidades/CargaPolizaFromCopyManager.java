package com.rsi.agp.core.managers.impl.utilidades;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.PolizaCopyManager;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

//ASF - Mejora para crear una póliza a partir de los datos de una copy

public class CargaPolizaFromCopyManager implements ICargaPolizaFromCopyManager {
	
	private static final Log logger = LogFactory.getLog(CargaPolizaFromCopyManager.class);
	
	private IPolizaDao polizaDao;
	private PolizaCopyManager polizaCopyManager;

	/**
	 * Método para crear una póliza a partir de una copy
	 * @param poliza Poliza con los datos del formulario de entrada
	 * @param realPath Ruta real de ejecución
	 * @return Identificador de la póliza cargada
	 */
	@Override
	public Long doCargar(Poliza poliza, String realPath) {
		
		try {
			//1. Comprobar que la póliza no existe
			Poliza auxPoliza = polizaDao.getPolizaByReferencia(poliza.getReferencia(), poliza.getTipoReferencia());
			
			if (auxPoliza == null){
				//2. Descargar la copy si la póliza no existe en la base de datos
				Long idcopy = polizaCopyManager.descargarPolizaCopyWS(poliza.getTipoReferencia()+"", poliza.getLinea().getCodplan(), poliza.getReferencia(), realPath);
				//3. Insertar los datos de la póliza a partir de la copy
				Long idpoliza = polizaCopyManager.crearPolizaFromCopy(poliza, idcopy, realPath);
				return idpoliza;
			}
		} catch (DAOException e) {
			logger.error("Excepcion : CargaPolizaFromCopyManager - doCargar", e);
		} catch (BusinessException e) {
			logger.error("Excepcion : CargaPolizaFromCopyManager - doCargar", e);
		}
		return null;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setPolizaCopyManager(PolizaCopyManager polizaCopyManager) {
		this.polizaCopyManager = polizaCopyManager;
	}

}
