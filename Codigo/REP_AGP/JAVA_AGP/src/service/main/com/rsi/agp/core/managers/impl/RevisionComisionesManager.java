package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.IRevisionComisionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;


/**
 * @author U028982
 */
public class RevisionComisionesManager implements IRevisionComisionesManager {
	
	private static final Log LOGGER = LogFactory.getLog(RevisionComisionesManager.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IPolizasPctComisionesDao polizasPctComisionesDao;
	private IPolizaDao polizaDao;	
	private IPolizasPctComisionesManager polizasPctComisionesManager;

	
	/**
	 * Cambia los parametros de comisiones de una póliza
	 * @author U028982 31/10/2014
	 * @param clase
	 * @param listaIds
	 * @return Map<String, String>
	 * @throws DAOException 
	 */
	@Override
	public Map<String, String> cambiaParametrosComisiones(Usuario usuario, String comMaxP, String pctadministracionP, String pctadquisicionP,
															String pctEntidadP, String pctESMedP, String idPlz, String grupoNegocio) throws DAOException {

		Map<String, String> params = new HashMap<String, String>();
		try {
			boolean actualizarPoliza = false;
			Poliza pol = (Poliza) polizaDao.getObject(Poliza.class, Long.valueOf(idPlz));

			if (pol != null){
				EstadoPoliza estadoPoliza = new EstadoPoliza();
				estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
				pol.setEstadoPoliza(estadoPoliza);
				PolizaPctComisiones pctCom = null;
				PolizaPctComisiones tempPct = null;
				Set<PolizaPctComisiones> pctComs=pol.getSetPolizaPctComisiones();
				Iterator<PolizaPctComisiones> it=pctComs.iterator();
				while(it.hasNext()){
					tempPct=it.next();
					if(Character.toString(tempPct.getGrupoNegocio()).equals(grupoNegocio)){
						pctCom=tempPct;
					}
				}
				if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
					// perfil 0
					if (!comMaxP.equalsIgnoreCase("")){
						pctCom.setPctcommax(new BigDecimal(comMaxP));
					}
					if (!pctadministracionP.equalsIgnoreCase("")){
						pctCom.setPctadministracion(new BigDecimal(pctadministracionP));
					}
					if (!pctadquisicionP.equalsIgnoreCase("")){
						pctCom.setPctadquisicion(new BigDecimal(pctadquisicionP));
					}
					if (!pctEntidadP.equalsIgnoreCase("")){
						pctCom.setPctentidad(new BigDecimal(pctEntidadP));
					}
					if (!pctESMedP.equalsIgnoreCase("")){
						pctCom.setPctesmediadora(new BigDecimal(pctESMedP));
					}
					pctCom.setEstado(Constants.ESTADO_POL_PCT_COMISINOES_REVISADA);
					actualizarPoliza = true;
				}else if ((usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES) && (usuario.getExterno().compareTo(Constants.USUARIO_INTERNO)==0)) ||
						 (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR))){
					// perfiles 1 interno y 5
					try {	
						
						
						Map a=polizasPctComisionesManager.validaComisiones(pol, usuario);
						List<PolizaPctComisiones> pct=(List)a.get("polizaPctComisiones");						
						for (PolizaPctComisiones  pctList : pct) {								
							polizasPctComisionesDao.updatePctComs(pctList);
						}			
						actualizarPoliza = true;
						/* recogemos los datos del mto de parametros generales*/
						/*
						List<List> lstParamsGen = new ArrayList<List>();
						List<String> temp = null;	
						
						List resultado=polizasPctComisionesDao.getParamsGen (pol.getLinea().getLineaseguroid(),
								pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
								pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad());
						
						if(resultado!=null && resultado.size()>0){
							for(int x=0;x<resultado.size();x++){
								temp=new ArrayList<String>();
								temp.add((String) ((Object[]) resultado.get(x))[0].toString());
								temp.add((String) ((Object[]) resultado.get(x))[1].toString());
								temp.add((String) ((Object[]) resultado.get(x))[2].toString());
								temp.add((String) ((Object[]) resultado.get(x))[3].toString());
								temp.add(getGruposNegocioPorCodigo((String) ((Object[]) resultado.get(x))[3].toString()));
								lstParamsGen.add(temp);
							}	
						}
						
						//if (paramsGen != null) {
						if (lstParamsGen != null) {
							// recogemos los datos del mto de comisiones por E-S Mediadora 
							Object[] comisionesESMed = polizasPctComisionesDao.getComisionesESMed (pol.getLinea().getLineaseguroid(),
									pol.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
									pol.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
									pol.getLinea().getCodlinea(),pol.getLinea().getCodplan());
							
							if (comisionesESMed != null) {
								// grabamos datos en póliza
								pctCom.setPctcommax((BigDecimal) lstParamsGen.get(0).get(2));
								pctCom.setPctentidad((BigDecimal) comisionesESMed[0]);
								
								BigDecimal pctEsMediadoraAjuste = (BigDecimal) comisionesESMed[1];
								
								Integer descuentoRecargoTipo = pol.getColectivo().gettipoDescRecarg();
								BigDecimal descuentoRecargoValor = pol.getColectivo().getpctDescRecarg();
								
								//Hay que ajustar
								if(descuentoRecargoTipo!=null && descuentoRecargoValor!=null){

									LOGGER.debug("descuentoRecargoTipo = " + descuentoRecargoTipo + " | descuentoRecargoValor = " + descuentoRecargoValor);
									BigDecimal bigDecimalCien = new BigDecimal("100.00");
									
									//PCT_DESC_RECARG (0 - Descuento, 1 - Recargo)
									if(descuentoRecargoTipo.compareTo(new Integer("0"))==0){
										//Descuento -> restamos...
										pctEsMediadoraAjuste = (pctEsMediadoraAjuste.subtract(pctEsMediadoraAjuste.multiply(descuentoRecargoValor).divide(bigDecimalCien))).setScale(2, BigDecimal.ROUND_FLOOR);
												
									}else if(descuentoRecargoTipo.compareTo(new Integer("1"))==0){
										//Recargo -> sumamos...
										pctEsMediadoraAjuste = (pctEsMediadoraAjuste.add(pctEsMediadoraAjuste.multiply(descuentoRecargoValor).divide(bigDecimalCien))).setScale(2, BigDecimal.ROUND_FLOOR);

									}else{
										LOGGER.error("descuentoRecargoTipo con valor no válido:" + descuentoRecargoTipo);
									}
								}
								
								LOGGER.debug("pctEsMediadoraAjuste = " + pctEsMediadoraAjuste);
								pctCom.setPctesmediadora(pctEsMediadoraAjuste);
								
								pctCom.setEstado(Constants.ESTADO_POL_PCT_COMISINOES_RESTAURADA);
								actualizarPoliza = true;
							}else {
								params.put("alerta", bundle.getString("comisiones.alta.poliza.comisionesESMed.KO"));
							}
						}else {
							params.put("alerta", bundle.getString("comisiones.alta.poliza.parametrosGenerales.KO"));
						}	*/						
					} catch (Exception e) {
						LOGGER.info("Se ha producido un error al modificar los parametros de comisiones: " + e.getMessage());
						params.put("alerta",bundle.getString("mensaje.comisiones.parametros.modificacion.KO"));
					}
				}
				if (actualizarPoliza){
					pol.setImporte(null);
					for (PagoPoliza pago : pol.getPagoPolizas()) {
						pago.setImporte(null);
					}					
					polizaDao.saveOrUpdate(pol);
					params.put("mensaje", bundle.getString("mensaje.comisiones.parametros.modificacion.OK"));
				}else{
					LOGGER.info("ERROR -- al modificar los parametros de comisiones: ");
					params.put("alerta",bundle.getString("mensaje.comisiones.parametros.modificacion.KO"));
				}
			}

			return params;
		} catch (DAOException e) {
			LOGGER.info("Se ha producido un error al cambiar los parametros de comisiones de la poliza: " + e.getMessage());
			throw new DAOException("Se ha producido un error al cambiar cambiar los parametros de comisiones de de la poliza:", e);
		}	
	}
	
	public void setPolizasPctComisionesDao(
			IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}


	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	public void setPolizasPctComisionesManager(IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}
}