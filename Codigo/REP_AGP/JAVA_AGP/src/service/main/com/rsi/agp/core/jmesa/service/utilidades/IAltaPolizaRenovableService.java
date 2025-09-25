package com.rsi.agp.core.jmesa.service.utilidades;

import java.util.List;

import org.hibernate.Session;

import com.rsi.agp.core.jmesa.service.impl.PolizaRenBean;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

import es.agroseguro.estadoRenovacion.Renovacion;


public interface IAltaPolizaRenovableService {
	
	public ColectivosRenovacion ValidatePolizaRenColectivo(Renovacion polRen, boolean batch, final Session session) throws Exception;
	
	public boolean populateAndValidatePolizaRen(List<PolizaRenBean> lstRes,
			final PolizaRenovable polizaHbm, final Renovacion polizaRen, final Session session,
			final StringBuilder polizasOK, final IPolizasPctComisionesDao polizasPctComisionesDao, 
			final boolean batch, final String codUsuario) throws Exception;
		
	public void guardaXml(final PolizaRenovable polizaHbm, final String xmlText, final Session session);

}
