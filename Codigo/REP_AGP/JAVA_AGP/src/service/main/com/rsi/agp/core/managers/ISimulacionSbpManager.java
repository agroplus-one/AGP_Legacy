package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public interface ISimulacionSbpManager {
	/**
	 * Alta de poliza de sobreprecio
	 * @param polizaSbp Objeto que encapsula los datos de la poliza de sobreprecio
	 * @throws BusinessException 
	 * @throws Exception 
	 */
	public Map<String, Object> altaPolizaSbp (PolizaSbp polizaSbp,String realPath,HttpServletRequest request) throws BusinessException, Exception;
	
	/**
	 * Edicion de poliza de sobreprecio
	 * @param polizaSbp Objeto que encapsula los datos de la poliza de sobreprecio
	 * @throws BusinessException 
	 */
	public List<String> editarPolizaSbp (PolizaSbp polizaSbp) throws BusinessException;
	
	/**
	 * Baja de poliza de sobreprecio
	 * @param polizaSbp Objeto que encapsula los datos de la poliza de sobreprecio
	 * @throws BusinessException 
	 */
	public void bajaPolizaSbp (PolizaSbp polizaSbp) throws BusinessException;
	/**
	 * Comprueba si la poliza ya tiene sobreprecio
	 * @param polizaSbp
	 * @return boolean 
	 * @throws BusinessException 
	 */
	public PolizaSbp existePolizaSbp(PolizaSbp polizaSbp)throws  BusinessException;
	/**
	 * Valida la poliza antes de calcular el sobreprecio y antes de pasarla a definitiva
	 * @param polizaSbp
	 * @return List errores
	 * @throws BusinessException
	 */
	public Map<String, Object> validaPoliza(PolizaSbp polizaSbp,boolean isValidacionDef,String realPath) throws BusinessException;
	/**
	 * graba una poliza de sobreprecio y la pone en estado provisional
	 * @param polizaSbp2
	 * @throws BusinessException
	 */
	public void grabacionProvisionalSbp(PolizaSbp polizaSbp2,Usuario usuario) throws BusinessException;
	/**
	 * Pasa una poliza de estado provisional a definitiva
	 * @param polizaSbp2
	 * @throws BusinessException
	 */
	public void grabacionDefinitivaSbp(PolizaSbp polizaSbp2,Usuario usuario) throws Exception;
	/**
	 * Obtiene una polizaSbp
	 * @param Long idPolizaSbp
	 * @return PolizaSbp
	 * @throws BusinessException
	 */
	public PolizaSbp getPolizaSbp(Long idPolizaSbp)throws BusinessException;
	
	public List<String> validarPolizaPpalParaSbp(Poliza poliza) throws BusinessException;
	
	/**
	 * Método que actualiza la poliza de sobreprecio con la Complementaria
	 * @param poliza
	 * @param realPath
	 * @throws Exception 
	 */
	
	public Map<String, Object> recalculaPolizaSbpConCpl(Poliza polPpal, Poliza polCpl, PolizaSbp polSbp, String realPath,Usuario usuario,HttpServletRequest request) throws Exception;
	
	public boolean actualizarPolizaSbp(Poliza p);
	
	/**
	 * Comprueba la compatibilidad con linea para Sbp
	 * @return true = valida
	 * @throws BusinessException
	 */
	public boolean validarLineaParaSbp(String linea, String plan) throws BusinessException;
	
	public Map<String, Object> getSeleccionPreciosSbp(PolizaSbp polizaSbp,String realPath,List<ParcelaSbp> lstParSbp) throws BusinessException;
	
	public Map<String, Object> recalculaSbp(String realPath, PolizaSbp polizaSbp, Usuario usuario,boolean definitiva )throws Exception  ;

	public Long calculaSuplemento(final Poliza poliza, final Date fechaVigorPoliza, final Usuario usuario,
			final String realPath, final Session session, final boolean esBatch, HttpServletRequest request) throws Exception;
	
	public boolean validaPolizaParaSuplemento(final Long idPoliza, final Date fechaVigorPoliza, final Usuario usuario,
			final String realPath, final AnexoModificacion anexo, final boolean actualizaAnexo, final Session session, final boolean esBatch)
			throws Exception;

	void anulaPoliza(String referencia) throws BusinessException;

	public List<Sobreprecio> generaSobreprecios(List<Sobreprecio> listSobreprecios, List<ParcelaSbp> parcelaSbpsMostrar, Map<String, Object> parameters)throws BusinessException;
	/**
	 * Devuelve las parcelas a mostrar en el informe
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public List<ParcelaSbp> getParcelasSimulacion(PolizaSbp polizaSbp)throws BusinessException;
	
	public BigDecimal getPlanSbp()throws BusinessException ;
	public boolean isCultivosEnPeriodoContratacion(PolizaSbp polizaSbp)throws BusinessException;
	public boolean isLineaEnPeriodoContratacion(PolizaSbp polizaSbp)
			throws BusinessException;
	public boolean existeCultivosSbpContratables(PolizaSbp polizaSbp)
			throws BusinessException;
	public List<PolizaSbp> getPolizasSbpParaSuplementos()throws BusinessException;

	public ArrayList<Long> checkAnexosCuponParaSbp(Session sesion) throws BusinessException;
	
	public void updateFlagbyIdPolSbp(List<Long> lstPolSbp) throws BusinessException;
	
	public void saveOrUpdate(PolizaSbp polizaSbp);

	/**
	 * P79222_3
	 * @param idPolizaSbp
	 * @return
	 * @throws BusinessException 
	 */
	public boolean validarSuplemento(PolizaSbp pSbp) throws BusinessException;
	
	/**
	 * 
	 * @param poliza
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public List<ParcelaSbp> obtenerParcelasSituacionActualizadaParaSuplemento(Poliza poliza, String realPath) throws Exception;
	
	/**
	 * 
	 */
	public void recalcularImporteSuplemento(Poliza poliza, PolizaSbp polizaSbp, HttpServletRequest request) throws BusinessException;

	void actualizaSobreprecio(BigDecimal sobreprecio, BigDecimal codCultivo, BigDecimal codProvincia,
			BigDecimal codComarca) throws BusinessException;

	
	public PolizaSbp getPolizaSbp(final Long idpoliza, final Character tipoRef) throws BusinessException;
}
