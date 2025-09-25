package com.rsi.agp.core.managers.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ICambioMasivoPolizasManager;
import com.rsi.agp.core.managers.IPasarADefinitivaPlzManager;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.dao.models.poliza.ICambioMasivoPolizasDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class CambioMasivoPolizasManager implements ICambioMasivoPolizasManager{
	
	private ICambioMasivoPolizasDao cambioMasivoPolizasDao;
	private IPasarADefinitivaPlzManager pasarADefinitivaPlzManager;
	private IPolizaDao polizaDao;
	private Log logger = LogFactory.getLog(CambioMasivoPolizasManager.class);
	
	@Override
	/**
	 * llama al dao para insertar en TB_PAGO_HISTORICO_PLZ un registro por cada poliza
	 * 06/08/2013 U029769
	 * @param fechapago
	 * @param listaIds
	 * @param marcar_desmarcar
	 * @param codUsuario
	 * @throws DAOException
	 */
	public void pagoMasivo(String fechapago, String listaIds, String marcar_desmarcar,String codUsuario) throws DAOException {
		String fecha = "";
		try {
			//si fechapago es vacio pongo la fecha actual 
			if(("").equals(fechapago)){
				fecha = DateUtil.date2String(DateUtil.getFechaActual(), DateUtil.FORMAT_DATE_DEFAULT);
			}else{
				fecha = fechapago;
			}	
			cambioMasivoPolizasDao.pagoMasivo(fecha, listaIds, marcar_desmarcar,codUsuario);
			
		} catch (DAOException e) {
			logger.error("Se ha producido un error al efectuar el pago masivo: ", e);
			throw new DAOException("e ha producido un error al efectuar el pago masivo: ", e);
		}
			// DAA 21/06/2013 si todo ha ido bien se genera de nuevo el xml de paso a def con la nueva situacion del pago
			String idsPolizas[] = listaIds.split(",");
			for(int i=0; i< idsPolizas.length; i++){
				Poliza p = polizaDao.getPolizaById(new Long(idsPolizas[i]));
				pasarADefinitivaPlzManager.generarXMLDefinitivo(p, null);
			}
			
	}
	
	public void setCambioMasivoPolizasDao(ICambioMasivoPolizasDao cambioMasivoPolizasDao) {
		this.cambioMasivoPolizasDao = cambioMasivoPolizasDao;
	}

	public void setPasarADefinitivaPlzManager(
			IPasarADefinitivaPlzManager pasarADefinitivaPlzManager) {
		this.pasarADefinitivaPlzManager = pasarADefinitivaPlzManager;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	
	
	
}
