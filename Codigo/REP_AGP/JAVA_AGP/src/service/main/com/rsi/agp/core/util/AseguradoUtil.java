package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;


public class AseguradoUtil {
	
	private static final Log logger = LogFactory.getLog(AseguradoUtil.class);
	
	/**
	 * Obtiene la cuenta 'IBAN + CCC' del asegurado asociado a la póliza que se está contratando, correspondiente a la línea de la póliza
	 * y si ésta no existe, la correspondiente a la línea 999 
	 * @param poliza Póliza que se está contratando
	 * @param isPagoPrima indica si se quiere la cuenta para el pago prima o para el cobro siniestros
	 * @return String correspondiente a 'IBAN + CCC'
	 */
	public static String obtenerCcc(Poliza poliza, boolean isPagoPrima){		
		
		logger.debug("Inicio - AseguradoUtil.obtenerCcc");
		
		DatoAsegurado da = obtenerDatoAsegurado(poliza);
		
		return da != null
				? (isPagoPrima ? da.getIban() + da.getCcc()
						: (StringUtils.isNullOrEmpty(da.getIban2()) ? "" : da.getIban2())
								+ (StringUtils.isNullOrEmpty(da.getCcc2()) ? "" : da.getCcc2()))
				: "";
	}
	
	/**
	 * Obtiene el registro 'DatoAsegurado' del asegurado asociado a la póliza que se está contratando, correspondiente a la línea de la póliza
	 * y si ésta no existe, la correspondiente a la línea 999 
	 * @param poliza Póliza que se está contratando
	 * @return DatoAsegurado correspondiente al asegurado indicado
	 */
	public static DatoAsegurado obtenerDatoAsegurado (Poliza poliza){		
		logger.debug("Inicio - AseguradoUtil.obtenerDatoAsegurado");
		
		try {
			Set<DatoAsegurado> datoAseguradoSet = poliza.getAsegurado().getDatoAsegurados();
			for (DatoAsegurado datoAsegurado : datoAseguradoSet) {
				if (poliza.getLinea().getCodlinea().compareTo(datoAsegurado.getLineaCondicionado().getCodlinea()) == 0) {
					logger.debug("Datos de los asegurados, linea especifica: " + datoAsegurado);
					return datoAsegurado;
				}
			}
			
			for (DatoAsegurado datoAsegurado : datoAseguradoSet) {
				if ((new BigDecimal(999)).compareTo(datoAsegurado.getLineaCondicionado().getCodlinea()) == 0) {
					logger.debug("Datos de los asegurados, linea generica: " + datoAsegurado);
					return datoAsegurado;
				}
			}
		} catch (Exception e) {
			logger.error("Error al obtener los datos del asegurado asociado a la poliza", e);
		}
		
		logger.debug("Fin - AseguradoUtil.obtenerDatoAsegurado - No se han encontrado datos para el asegurado");
		
		return null;
	}
	

	/** DAA 03/05/2012
	 * Metodo encargado de realizar la validacion que indica que se mostrara la 
	 * cuenta del asegurado CON FORMATO asociada a la linea que se esta contratando, o si no
	 * existe, la asociada a la linea 999 (equivalente a todas las lineas).
	 * @param asegurado
	 * @param poliza
	 * @return La cuenta asociada.
	 */
	public static String obtenerCccFormateado(Poliza poliza, boolean isPagoPrima){
		String ccc = AseguradoUtil.obtenerCcc(poliza, isPagoPrima);		
		String iban= "";
		String cuenta1= "";
		String cuenta2= "";
		String cuenta3= "";
		String cuenta4= "";
		String cuenta5= "";
			
		if (!StringUtils.nullToString(ccc).equals("") && ccc.length()>23){
			iban=ccc.substring(0, 4);
			cuenta1=ccc.substring(4, 8);
			cuenta2=ccc.substring(8, 12);
			cuenta3=ccc.substring(12,16);
			cuenta4=ccc.substring(16,20);
			cuenta5=ccc.substring(20,24);
		}

		ccc=iban+" "+cuenta1+" "+cuenta2+" "+cuenta3+" " +cuenta4+" "+cuenta5;
		return ccc;
	}
	
	/**
	 * Método estático que obtiene los datos de pago para un póliza dada.
	 * Primero busca en Pagos Poliza, si ahí no encuentra nada, va a la cuenta bancaria específica para la linea de la póliza,
	 * y por úlitmo, si no ha encontrado nada, devuelve la cuenta genérica del cliente.
	 * El flag de entrada isCobroPrima indica si se quiere obtener el IBAN para el pago de la prima o el IBAN para el cobro
	 * de siniestros.
	 * @return String con la cuenta formateada
	 */
	public static String getFormattedBankAccount(Poliza poliza, boolean isPagoPrima){
		String bankAccount = getRawBankAccount(poliza, isPagoPrima);
		return formatAccountNumber(bankAccount);
	}

	private static String getRawBankAccount(Poliza policy, boolean isPagoPrima) {
		BigDecimal policyLineCode = policy.getLinea().getCodlinea();
		Set<PagoPoliza> policyPaymentsSet = policy.getPagoPolizas();
		String policyId = policy.getIdpoliza().toString();
		String rawBankAccount = "";
		if (policyPaymentsSet == null || (policyPaymentsSet != null && policyPaymentsSet.isEmpty())) {
			rawBankAccount = obtenerCcc(policy, isPagoPrima);
		} else {
			Iterator<PagoPoliza> policyPaymentsIterator = policyPaymentsSet.iterator();
			logger.debug(new StringBuilder("Buscamos los datos de pago para la poliza ").append(policyId).toString());
			if(policyPaymentsIterator.hasNext()){
				logger.debug(new StringBuilder("La poliza ").append(policyId).append(" tiene datos previos de pago.").toString());
				PagoPoliza payment = policyPaymentsSet.iterator().next();
				if (isPagoPrima) {
					rawBankAccount = new StringBuilder(payment.getIban()).append(payment.getCccbanco()).toString();
				} else {
					rawBankAccount = new StringBuilder(
							StringUtils.isNullOrEmpty(payment.getIban2()) ? "" : payment.getIban2()).append(
									StringUtils.isNullOrEmpty(payment.getCccbanco2()) ? "" : payment.getCccbanco2())
									.toString();
				}
			} else {
				logger.debug(new StringBuilder("La poliza ").append(policyId).append(" no tiene datos previos de pago.").toString());
				Set<DatoAsegurado> insuredPersonDataSet = policy.getAsegurado().getDatoAsegurados();
				// Buscamos si hay cuenta bancaria especifica para esta linea
				rawBankAccount = getRawBankAccountByLine(insuredPersonDataSet, policyLineCode, isPagoPrima);
				if(rawBankAccount.isEmpty()){
					// como no hay cuenta bancaria especifica para la linea, buscamos la generica.
					// Si tampoco hay generica devolvemos un string vacio
					rawBankAccount = getRawBankAccountByLine(insuredPersonDataSet, Constants.CODLINEA_GENERICA, isPagoPrima);
				}
			}
		}
		return rawBankAccount;
	}
	
	private static String getRawBankAccountByLine(Set<DatoAsegurado> insuredPersonDataSet, BigDecimal lineCode, boolean isPagoPrima){
		String rawBankAccount = "";
		for (DatoAsegurado insuredPersonDatum : insuredPersonDataSet) {
			BigDecimal conditionedLineCode = insuredPersonDatum.getLineaCondicionado().getCodlinea();
			if (lineCode.compareTo(conditionedLineCode) == 0) {
				if (isPagoPrima) {
					rawBankAccount = new StringBuilder(insuredPersonDatum.getIban()).append(insuredPersonDatum.getCcc()).toString();
				} else {
					rawBankAccount = new StringBuilder(StringUtils.isNullOrEmpty(insuredPersonDatum.getIban2()) ? ""
							: insuredPersonDatum.getIban2())
									.append(StringUtils.isNullOrEmpty(insuredPersonDatum.getCcc2()) ? ""
											: insuredPersonDatum.getCcc2())
									.toString();
				}
				break;
			}
		}
		return rawBankAccount;
	}

	private static String formatAccountNumber(String rawBankAccount) {
		String formattedBankAccount = "";
		if(!rawBankAccount.equals("") && rawBankAccount.length() > 23){
			StringBuilder sb = new StringBuilder();
			sb.append(rawBankAccount.substring(0, 4));
			sb.append(" ");
			sb.append(rawBankAccount.substring(4, 8));
			sb.append(" ");
			sb.append(rawBankAccount.substring(8, 12));
			sb.append(" ");
			sb.append(rawBankAccount.substring(12, 16));
			sb.append(" ");
			sb.append(rawBankAccount.substring(16, 20));
			sb.append(" ");
			sb.append(rawBankAccount.substring(20, 24));
			formattedBankAccount = sb.toString();
		}
		return formattedBankAccount;
	}
}
