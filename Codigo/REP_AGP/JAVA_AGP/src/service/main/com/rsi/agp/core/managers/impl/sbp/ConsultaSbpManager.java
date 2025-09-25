package com.rsi.agp.core.managers.impl.sbp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IConsultaSbpManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.dao.models.sbp.IConsultaSbpDao;
import com.rsi.agp.dao.models.sbp.ISimulacionSbpDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ErrorSbp;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public class ConsultaSbpManager implements IConsultaSbpManager {
	
	protected final Log logger = LogFactory.getLog(getClass());
	private IConsultaSbpDao consultaSbpDao;
	private ISimulacionSbpManager simulacionSbpManager;
	private ISimulacionSbpDao simulacionSbpDao;
	private SeleccionPolizaManager seleccionPolizaManager;
	
	public Boolean getListaPolizasSbp (Poliza poliza, boolean complementaria){
		boolean res = false;
		logger.debug("init - [metodo] getListaPolizasSbp");
		List<PolizaSbp> lstPolizasSbp= null;
		try {
			lstPolizasSbp = consultaSbpDao.getListaPolizasSbp(poliza.getIdpoliza(),complementaria);
			if (lstPolizasSbp!=null && lstPolizasSbp.size() >0){
				for (PolizaSbp pSbp: lstPolizasSbp){
					if (!pSbp.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_ANULADA)){
						if (pSbp.getIncSbpComp().equals('S')){
							res = true;
						}
					}
				}
				
				
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error en el borrado de las poliza Sbp asociada "+ e.getMessage());
		}
		
		logger.debug("end - [metodo] getListaPolizasSbp");
		return res;
	}
	
	/**
	 * metodo para borrar la poliza de Sbp asociada a una principal o complementaria
	 */
	public String borrarPolizaSbpByPoliza (Poliza poliza, Usuario usuario, String realPath){
		logger.debug("init - [metodo] borrarPolizaSbpByPoliza");
		List<PolizaSbp> lstPolizasSbp= null;
		String accion = "";
		String msj = "";
		try {
			// buscamos la poliza de SBP asociada
			if (poliza.getTipoReferencia().equals('P')){
				lstPolizasSbp = consultaSbpDao.getListaPolizasSbp(poliza.getIdpoliza(),false);//devuelve la PPal
				accion = "borrar";
			}else{
				lstPolizasSbp = consultaSbpDao.getListaPolizasSbp(poliza.getIdpoliza(),true);//devuelve la CPl
				if (lstPolizasSbp!=null && lstPolizasSbp.size() >0){
					for (PolizaSbp pSbp: lstPolizasSbp){
						
						// si es de tipo suplemento la borramos
						if (pSbp.getTipoEnvio().getId().equals(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO)){
							simulacionSbpDao.deleteParcelas(pSbp);
							simulacionSbpDao.delete(pSbp);
						}else if (!pSbp.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_ANULADA)){
							if (pSbp.getIncSbpComp().equals('S'))
								accion = "recalcular";
							else
								accion = "actualizar";
						}
					}
				}
			}
			if (lstPolizasSbp!=null && lstPolizasSbp.size() >0){
				if (accion.equals("borrar")){
					for (PolizaSbp pSbp: lstPolizasSbp){
						logger.debug("Poliza de Sobreprecio a borrar: "+ pSbp.getId().toString());
						//primero borramos las parcelas de la polizaSbp y luego la polizaSbp
						for (ParcelaSbp parcelaSbp: pSbp.getParcelaSbps()){
							simulacionSbpDao.delete(parcelaSbp);
						}
						simulacionSbpDao.delete(pSbp);
						msj = "msjSbpBorrada";
					}
				}else if (accion.equals("recalcular")){
					
					for (PolizaSbp pSbp: lstPolizasSbp){
						if (!pSbp.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_ANULADA)){
							
							pSbp.setIncSbpComp('N');
							PolizaSbp polizaSbpActualizadaSbp = null;
							try {
								//TMR
								simulacionSbpManager.recalculaSbp(realPath, pSbp, usuario, false);
								logger.debug("idPolizaSbpSinCpl recalculada sin la Cpl: "	+ ((polizaSbpActualizadaSbp != null) ? polizaSbpActualizadaSbp.getId() : ""));
							} catch (BusinessException e) {
								logger.error("Se ha producido un error en la actualizacion de las poliza Sbp", e);
							}catch (Exception e) {
								logger.error("Se ha producido un error en la actualizacion de las poliza Sbp", e);
							}
							simulacionSbpDao.saveOrUpdate(polizaSbpActualizadaSbp);
							msj = "msjSbpRecalculada";
						}
					}
					
				}else if (accion.equals("actualizar")){
					//quitamos la Cpl en la Sbp
					for (PolizaSbp pSbp: lstPolizasSbp){
						pSbp.setPolizaCpl(null);
						pSbp.setPolizaCplCopy(null);
						simulacionSbpDao.saveOrUpdate(pSbp);
						logger.debug("Poliza de Sobreprecio actualizada sin la Cpl: "+ pSbp.getId());
					}
				}
				
			}else{
				logger.debug("no hay polizas de Sbp asociadas para la poliza: " + poliza.getIdpoliza());
				return msj;
			}
		} catch (DAOException e) {
			logger.error("Se ha producido un error en el borrado de las poliza Sbp asociada "+ e.getMessage());
		}
		logger.debug("end - [metodo] borrarPolizaSbpByPoliza");
		return msj;
	}
	
	/**
	 * metodo para conseguir las lineas permitidas en sobreprecio
	 * @return Listado de lineas permitidas
	 */
	public List<Sobreprecio> getLineasSobrePrecio() {
		List<Sobreprecio> lineas = new ArrayList<Sobreprecio>();
		try {
			lineas  = consultaSbpDao.getLineasSobrePrecio();
		} catch (Exception e) {
			logger.error("Excepcion : ConsultaSbpManager - getLineasSobrePrecio", e);
		}

		
		return lineas;
	}
	
	/**
	 * @param Objeto que encapsula el filtro para polizas de sobreprecio
	 * @return Listado de polizas de sobreprecio que se ajustan al filtro
	 */
	public List<PolizaSbp> consultarPolizasSbp(PolizaSbp polizaSbp) {
		List<PolizaSbp> lstPolizasSbp = new ArrayList<PolizaSbp>();
		try {
			lstPolizasSbp = consultaSbpDao.consultaPolizaSobreprecio(polizaSbp);
		} catch (Exception e) {
			logger.error("Excepcion : ConsultaSbpManager - consultarPolizasSbp", e);
		}

		
		return lstPolizasSbp;
	}

	/**
	 * @return Listado completo de polizas de sobreprecio
	 */
	public List<PolizaSbp> limpiar(HttpServletRequest request) {
		return null;
	}
	
	public List<EstadoPoliza> getEstadosPoliza(BigDecimal estadosPolizaExcluir[]) {
		List<EstadoPoliza> lstEstadosOriginal = new ArrayList<EstadoPoliza>();
		
		lstEstadosOriginal = seleccionPolizaManager.getEstadosPoliza(estadosPolizaExcluir);
		List<EstadoPoliza> lstEstadosTemp = new ArrayList<EstadoPoliza>();
		//creo una lista de estados a partir de los disponibles para una poliza
		for (EstadoPoliza estado:lstEstadosOriginal ){
			EstadoPoliza est = new EstadoPoliza();
			est.setIdestado(estado.getIdestado());
			est.setDescEstado(estado.getDescEstado());
			est.setPolizas(estado.getPolizas());
			lstEstadosTemp.add(est);
		}
		//añadimos las iniciales a cada decripcion del estado para el combo de la jsp
		for (EstadoPoliza estado:lstEstadosTemp ){
			String estadoTemp = "";
			if (!estado.getDescEstado().contains("-")){
				estadoTemp = estado.getDescEstado();
				if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL) == 0){
					estado.setDescEstado("P&nbsp;&nbsp;- " + estadoTemp); 
				}else if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION) == 0){
					estado.setDescEstado("V&nbsp;&nbsp;- " + estadoTemp); 
				}else if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA) == 0){
					estado.setDescEstado("D&nbsp;&nbsp;- " + estadoTemp); 
				}else if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_DEFINITIVA) == 0){
					estado.setDescEstado("E&nbsp;&nbsp;- " + estadoTemp); 
				}else if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_ENVIADA_ERRONEA) == 0){
					estado.setDescEstado("EE&nbsp;- " + estadoTemp); 
				}else if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR) == 0){
					estado.setDescEstado("EP&nbsp;- " + "Enviada pend. Confirmar");
				}else if (estado.getIdestado().compareTo(Constants.ESTADO_POLIZA_ANULADA) == 0){
					estado.setDescEstado("A&nbsp;&nbsp;- " + estadoTemp); 
				}
			}
		}
		return lstEstadosTemp;
	}
	
	public List<EstadoPoliza> getEstadosPolizaPpal(List<EstadoPoliza> lstEstados) {
		List<EstadoPoliza> lstEstadosPpal = new ArrayList<EstadoPoliza>();
		
		//excluyo el estado "pendiente de validacion"
		for(EstadoPoliza estado : lstEstados){
			if (estado.getIdestado().compareTo(new BigDecimal(1))==0){
				// no incluir la pendiente de validacion
			}else{
				lstEstadosPpal.add(estado);
			}
				
		}
		return lstEstadosPpal;
	}
	
	public List<EstadoPlzSbp> getEstadosPolizaSbp(BigDecimal estadosPolizaExcluir[]) {
		List<EstadoPlzSbp> lstEstadosPolSbp = new ArrayList<EstadoPlzSbp>();
		lstEstadosPolSbp = consultaSbpDao.getEstadosPolSbp(estadosPolizaExcluir);
		return lstEstadosPolSbp;
	}
	
	public List<ErrorSbp> getDetalleErroresSbp(BigDecimal detalleErroresExcluir[]) {
		List<ErrorSbp> lstdetalleErroresSbp = new ArrayList<ErrorSbp>();
		lstdetalleErroresSbp = consultaSbpDao.getDetalleErroresSbp(detalleErroresExcluir);
		return lstdetalleErroresSbp;
	}
	
	
	@Override	
	public List<Poliza> getListObjPolFromString(String listIdPolizas) throws Exception {
		
		Poliza poliza;
		List<Poliza> listPolizas = new ArrayList<Poliza>();
		String[] listaFin = listIdPolizas.split(";");	
		
		for (int j=0; j<listaFin.length; j++){
			String id = listaFin[j];
			if (!id.equals("")){
				poliza = seleccionPolizaManager.getPolizaById(new Long(id));
				listPolizas.add(poliza);
			}
		}
		
		return listPolizas;
	}
	
	public Map<Long, List<BigDecimal>>  getCultivosPorLineaseguroid(BigDecimal codPlan){
		Map<Long, List<BigDecimal>> cultivosPorLinea = new HashMap<Long, List<BigDecimal>>();
		cultivosPorLinea = consultaSbpDao.getCultivosPorLineaseguroid(codPlan);
		return cultivosPorLinea;
	}
	
	/**
	* P0073325 - RQ.19
	*/
	/**
	 * Obtiene un objeto poliza de sobreprecio de la BD
	 * 
	 * @param idPoliza
	 *            Identificador de la poliza de sobreprecio en la BD
	 */
	public final PolizaSbp getPolizaSbpById(final Long idPoliza) {
		return (PolizaSbp) consultaSbpDao.getObject(PolizaSbp.class, idPoliza);
	}
	
	
	/**
	 * Getter del Dao asociado
	 * @return Devuelve el objeto ConsultaSbpDao
	 */
	public IConsultaSbpDao getConsultaSbpDao() {
		return consultaSbpDao;
	}

	/**
	 * Setter de Dao asociado
	 * @param consultaSbpDao Objeto ConsultaSbpDao
	 */
	public void setConsultaSbpDao(IConsultaSbpDao consultaSbpDao) {
		this.consultaSbpDao = consultaSbpDao;
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}
	
	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}


	public void setSimulacionSbpDao(ISimulacionSbpDao simulacionSbpDao) {
		this.simulacionSbpDao = simulacionSbpDao;
	}
	
}
