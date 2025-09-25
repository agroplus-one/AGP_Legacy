package com.rsi.agp.core.jmesa.service.mtoinf;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones;
import com.rsi.agp.dao.tables.comisiones.InformeComisiones2015;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos;
import com.rsi.agp.dao.tables.comisiones.InformeRecibos2015;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Informe;

public interface IMtoInformeService {

	/**
	 * Genera la tabla para mostrar el listado de informes que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param informe
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaInformes (HttpServletRequest request, HttpServletResponse response,
			Informe informe, String origenLlamada, String cadenaCodigosLupas, final Usuario usuario);
	
	/**
	 * Borra el objeto informe pasado como parámetro
	 * @param informe
	 * @return Boolean que indica si el borrado ha sido correcto
	 */
	public boolean bajaInforme(Informe informe) throws BusinessException;
	
	/**
	 * Obtiene el objeto informe correspondiente al id indicado en parámetro
	 * @param id
	 * @return
	 */
	public Informe getInforme(Long id) throws BusinessException;
	
	/**
	 * Realiza el alta del informe pasado como parámetro
	 * @param informe
	 * @return Devuelve un mapa con los errores producidos en el proceso de alta
	 */
	public Map<String, Object> altaInforme(Informe informe) throws BusinessException;
	
	public Map<String, Object> editarInforme(Informe informeEdit) throws BusinessException;
	
	public Map<String, Object> generarConsultaInforme(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos informeRecibosBean) throws BusinessException;
	
	public Map<String, Object> generarConsultaInforme2015(Informe informe, final Usuario usuario, String consultaYaGenerada, InformeRecibos2015 informeRecibosBean) throws BusinessException;
	
	public List<FormatoCampoGenerico> getFormatosInforme();
	
	public List<FormatoCampoGenerico> getOrientacionesInforme();
	
	public boolean checkRelTablas (Informe informe);

	public int getConstantMaxRegistros() throws BusinessException;

	public List<Object> getConsulta(String sql);

	public InformeRecibos setInformeRecibosBeanDetalle(InformeRecibos informeRecibosBean, String stringRegistro);
	public InformeRecibos2015 setInformeRecibosBeanDetalle2015(InformeRecibos2015 informeRecibosBean, String stringRegistro);

	public Map<String, Object> generarConsultaInformeComisiones(Informe informe, Usuario usuario, String consultaYaGenerada,
			InformeComisiones informeComisiones) throws BusinessException;
	
	public Map<String, Object> generarConsultaInformeComisiones2015(Informe informe, Usuario usuario, String consultaYaGenerada,
			InformeComisiones2015 informeComisiones) throws BusinessException;
	
	public InformeComisiones setInformeComisionesBeanDetalle(InformeComisiones informeComisionesBean, String stringRegistro);
	public InformeComisiones2015 setInformeComisionesBeanDetalle2015(InformeComisiones2015 informeComisionesBean, String stringRegistro);

	public Map<String, Object> duplicarInforme(Informe informeBean, String tituloInfoDuplicado)throws BusinessException;
	
}