package com.rsi.agp.core.jmesa.service.mtoinf;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones2015;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos2015;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.Informe;

public interface IGeneracionInformeService {
	
	/**
	 * Genera el informe indicado por el parametro
	 * @param informe Objeto que encapsula la informacion del informe que se quiere generar
	 * @return Codigo del informe generado
	 * @throws BusinessException 
	 */
	public Map<String, Object> generarInforme(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos informeRecibos) throws BusinessException;

	/**
	 * Genera el informe indicado por el parametro (versión 2015+)
	 * @param informe Objeto que encapsula la informacion del informe que se quiere generar
	 * @return Codigo del informe generado
	 * @throws BusinessException 
	 */
	public Map<String, Object> generarInforme2015(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos2015 informeRecibos) throws BusinessException;
	
	public String getTablaInformes(HttpServletRequest request,
			HttpServletResponse response, Informe informe, String origenLlamada, final Usuario usuario);
	
	/** DAA 21/02/13 Obtiene el objeto que controlara el maximo de registros permitidos para el informe
	 * 
	 * @param numRegistros
	 * @param maxReg
	 * @param formatoInforme
	 * @return
	 */
	public JSONObject getControlErrorMaxReg(int numRegistros, int maxReg,
			int formatoInforme);

	@SuppressWarnings("rawtypes")
	public List setSumatorioToInforme(List totaliza, List listadoInforme);

	public Map<String, Object> generarInformeComisiones(Informe informe, Usuario usuario, String consultaYaGenerada,
			InformeComisiones informeComisionesBean) throws BusinessException;
	
	public Map<String, Object> generarInformeComisiones2015(Informe informe, Usuario usuario, String consultaYaGenerada,
			InformeComisiones2015 informeComisionesBean) throws BusinessException;

}