package com.rsi.agp.core.managers;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;

public interface IPagoEstadosPolizaManager {
	
	List<EstadosPoliza> getEstadosPagoPoliza () throws BusinessException;

}
