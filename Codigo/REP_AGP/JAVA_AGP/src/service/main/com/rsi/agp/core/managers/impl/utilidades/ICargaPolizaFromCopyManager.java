package com.rsi.agp.core.managers.impl.utilidades;

import com.rsi.agp.dao.tables.poliza.Poliza;

public interface ICargaPolizaFromCopyManager {
	public Long doCargar(Poliza poliza, String realPath);
}
