package com.rsi.agp.core.jmesa.service;

import java.util.Map;

public interface IInformesRCGanadoService {
	
	Map<String, Object> getRellenarInformacion(Long idPolizaRC) throws Exception;
}
