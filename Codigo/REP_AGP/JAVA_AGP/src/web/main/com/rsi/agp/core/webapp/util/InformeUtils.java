package com.rsi.agp.core.webapp.util;

import com.rsi.agp.core.util.ConstantsInf;

public class InformeUtils {

	/**
	 * Devuelve el nombre del operador correspondiente al codigo que se pasa por parametro
	 * @param operadorValue
	 * @return
	 */
	public static String getValueOperador(int operadorValue) {
		String strOprerator = "";
		
		switch (operadorValue) {
			case ConstantsInf.COD_OPERADOR_CAD_CONTIENEN: strOprerator = ConstantsInf.OPERADOR_BD_CAD_CONTIENEN; break;
			case ConstantsInf.COD_OPERADOR_BD_CAD_EMPIEZAN_POR: strOprerator = ConstantsInf.OPERADOR_BD_CAD_EMPIEZAN_POR; break;
			case ConstantsInf.COD_OPERADOR_BD_CAD_TERMINAN_POR: strOprerator = ConstantsInf.OPERADOR_BD_CAD_TERMINAN_POR; break;
			case ConstantsInf.COD_OPERADOR_BD_CONTENIDO_EN: strOprerator = ConstantsInf.OPERADOR_BD_CONTENIDO_EN; break;
			case ConstantsInf.COD_OPERADOR_BD_ENTRE: strOprerator = ConstantsInf.OPERADOR_BD_ENTRE; break;
			case ConstantsInf.COD_OPERADOR_BD_IGUAL: strOprerator = ConstantsInf.OPERADOR_BD_IGUAL; break;
			case ConstantsInf.COD_OPERADOR_BD_MAYOR_IGUAL_QUE: strOprerator = ConstantsInf.OPERADOR_BD_MAYOR_IGUAL_QUE; break;
			case ConstantsInf.COD_OPERADOR_BD_MAYOR_QUE: strOprerator = ConstantsInf.OPERADOR_BD_MAYOR_QUE; break;
			case ConstantsInf.COD_OPERADOR_BD_MENOR_IGUAL_QUE: strOprerator = ConstantsInf.OPERADOR_BD_MENOR_IGUAL_QUE; break;
			case ConstantsInf.COD_OPERADOR_BD_MENOR_QUE: strOprerator = ConstantsInf.OPERADOR_BD_MENOR_QUE; break;
			default: break;
		}		
		return strOprerator;
	}
	
	/**
	 * Devuelve el nombre del operador para campos calculados correspondiente al codigo que se pasa por parametro
	 * @param operadorValue
	 * @return
	 */
	public static String getOperador(int operadorValue) {
		String strOprerator = "";
		
		switch (operadorValue) {
			case ConstantsInf.COD_OPERADOR_ARIT_SUMA: strOprerator = ConstantsInf.OPERADOR_ARIT_SUMA; break;
			case ConstantsInf.COD_OPERADOR_ARIT_RESTA: strOprerator = ConstantsInf.OPERADOR_ARIT_RESTA; break;
			case ConstantsInf.COD_OPERADOR_ARIT_MULT: strOprerator = ConstantsInf.OPERADOR_ARIT_MULT; break;
			case ConstantsInf.COD_OPERADOR_ARIT_DIV: strOprerator = ConstantsInf.OPERADOR_ARIT_DIV; break;
			default: break;
		}		
		return strOprerator;
	}
	
	
	/**
	 * Devuelve el numbore del formato correspondiente al codigo que se pasa por parametro
	 * @param formatoValue
	 * @return
	 */
	public static String getValueFormato(String formatoValue) {
		String strFormato = "";
		
		switch (new Integer(formatoValue)) {
			case ConstantsInf.COD_FORMATO_FECHA_DDMMYYYY: strFormato = ConstantsInf.FORMATO_FECHA_DDMMYYYY; break;
			case ConstantsInf.COD_FORMATO_FECHA_YYYYMMDD: strFormato = ConstantsInf.FORMATO_FECHA_YYYYMMDD; break;
			case ConstantsInf.COD_FORMATO_NUM_NNNN: strFormato = ConstantsInf.FORMATO_NUM_NNNN; break;
			case ConstantsInf.COD_FORMATO_NUM_N_NNN: strFormato = ConstantsInf.FORMATO_NUM_N_NNN; break;
			case ConstantsInf.COD_FORMATO_NUM_NNNN_DD: strFormato = ConstantsInf.FORMATO_NUM_NNNN_DD; break;
			case ConstantsInf.COD_FORMATO_NUM_N_NNN_DD: strFormato = ConstantsInf.FORMATO_NUM_N_NNN_DD; break;
			default: break;
		}		
		return strFormato;
	}
	
	
	public static String getValueSentido(String sentidoValue) {
		String strSentido = "";
		
		switch (new Integer(sentidoValue)) {
			case 0:	strSentido = ConstantsInf.ORDENACION_ASC; break;
			case 1:	strSentido = ConstantsInf.ORDENACION_DESC; break;
			default: break;
		}		
		return strSentido;
	}
}