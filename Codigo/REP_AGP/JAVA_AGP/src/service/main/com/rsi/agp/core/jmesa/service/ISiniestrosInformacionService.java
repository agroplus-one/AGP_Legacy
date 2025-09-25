package com.rsi.agp.core.jmesa.service;

import com.rsi.agp.dao.tables.poliza.Poliza;

public interface ISiniestrosInformacionService extends IGetTablaService {
	public Poliza getPoliza(Long id) throws Exception;
}
