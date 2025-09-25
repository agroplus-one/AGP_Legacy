package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.tables.poliza.Poliza;

public interface IRefPolizaDao {

	List getReferenciasAgricolas();

	public Poliza getPoliza(String refpoliza,Character tipo, BigDecimal codPlan);
	public void sendMail(String mensaje);

}
