package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;

@SuppressWarnings("rawtypes")
public interface IPagoPolizaDao extends GenericDao {

	boolean existeBancoDestino(String bancoDestino) throws Exception;

	JSONObject validaFormaPago(String idpoliza) throws Exception;

	PagoPoliza getFormaPago(Long idpoliza) throws Exception;

	public boolean polizaAgrPermiteEnvioIban(BigDecimal codPlan_pol, BigDecimal codLinea_pol) throws Exception;

	public void updateIbanbyPoliza(String listIdPolizasMod, String iban, String cccbanco,
			String destinatarioDomiciliacion, String titularCuenta, String iban2, String cccbanco2) throws Exception;

	BigDecimal getPctMinimoFinanciacion(BigDecimal codPlan, BigDecimal codLinea, String codModulo) throws Exception;

	boolean guardaDatosCuenta(Long idpoliza, Character envioIBANAgr) throws Exception;

	boolean polizaEsFinanciada(Long idpoliza) throws Exception;

	JSONObject validaEntidadPermitida(String idpoliza, String entidad) throws Exception;

	boolean lineaContratacion2019(BigDecimal codPlan_pol, BigDecimal codLinea_pol, boolean isLineaGan) throws Exception;
	
	boolean lineaContratacion2021(BigDecimal codPlan_pol, BigDecimal codLinea_pol, boolean isLineaGan) throws Exception;
}
