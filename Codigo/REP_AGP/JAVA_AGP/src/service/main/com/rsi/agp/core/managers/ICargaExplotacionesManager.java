package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;





public interface ICargaExplotacionesManager {
	public void actualizarIdCargaExplotaciones(Long idpoliza, Integer idCargaExplotaciones) throws Exception;
	
	public IsRes isSituacionActualizadaAgroseguro(Long idAsegurado, BigDecimal plan,
			BigDecimal linea,Long idpoliza) throws DAOException, Exception;
	
	public IsRes isPolizaOriginalUltimosPlanes(Long idAsegurado, BigDecimal plan, 
			BigDecimal linea, Long idpoliza) throws DAOException, Exception;
	
	public IsRes isPolizaPlanActual(Long idAsegurado, BigDecimal plan, 
			BigDecimal linea, Long idpoliza) throws DAOException, Exception;
	
	public Boolean isPolizaAnteriorSistemaTradicional(BigDecimal plan, BigDecimal linea);
	
	public void cargaSituacionActualizada(Long idPolizaAnterior, String realPath, final Poliza poliza) throws Exception ;
	
	public List<Poliza> listaPlzSituacionActualizada(Long idAsegurado, BigDecimal plan, BigDecimal linea,Long idPoliza) throws DAOException;
	public List<Poliza> listaPolizaOriginalUltimosPlanes(Long idAsegurado, BigDecimal plan, BigDecimal linea,Long idPoliza) throws DAOException;
	public List<Poliza> listaPolizaPlanActual(Long idAsegurado, BigDecimal plan, BigDecimal linea,Long idPoliza) throws DAOException;
	public void cargaExplotacionesPolizaExistente(Long idPolizaSeleccionada, final Poliza poliza) throws Exception;
	
	public String  cargaPolizaSistemaTradicional(BigDecimal plan, String referencia, String realPath,
			Long idPoliza, Usuario usuario, Long lineaseguroid, Poliza poliza) 
					throws SOAPFaultException,AgrException, Exception;
	/**
	 * Objeto creado para devolver los valores de las funciones isSituacionActualizadaAgroseguro, 
	 * isPolizaOriginalUltimosPlanes y isPolizaPlanActual
	 * @author U028787
	 *
	 */
	public class IsRes{
		private Boolean res;
		private Long idPoliza;
		
		public IsRes(Boolean res, Long idPoliza){			
			this.res = res;
			this.idPoliza = idPoliza;
		}
		
		public IsRes(){				
		}

		public Boolean getRes() {
			return res;
		}

		public void setRes(Boolean res) {
			this.res = res;
		}

		public Long getIdPoliza() {
			return idPoliza;
		}

		public void setIdPoliza(Long idPoliza) {
			this.idPoliza = idPoliza;
		}
		
		
	}
}
