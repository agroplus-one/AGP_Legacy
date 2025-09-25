package com.rsi.agp.core.managers;

import java.util.ArrayList;

public interface IValidaLupasCambioMasivoManager {

	void validaCultivoVariedad(String cultivo, String variedad,
			String lineaSeguroId, ArrayList<String> erroresLupas)
			throws Exception;

	void validaDestino(String destino, ArrayList<String> erroresLupas)
			throws Exception;

	void validaTipoPlantacion(String tipoPlantacion,
			ArrayList<String> erroresLupas) throws Exception;

	void validaSisCultivo(String sisCultivo, ArrayList<String> erroresLupas)
			throws Exception;

	void validaMarcoPlan(String tipoMarcoPlan, ArrayList<String> erroresLupas)
			throws Exception;

	void validaPracticaCultural(String practicaCultural,
			ArrayList<String> erroresLupas) throws Exception;

	void validaUbicacion(String provincia, String comarca, String termino,
			String subtermino, ArrayList<String> erroresLupas) throws Exception;
	
	void validaSistemaProduccion(String sistProd, ArrayList<String> erroresLupas)
			throws Exception;
}
