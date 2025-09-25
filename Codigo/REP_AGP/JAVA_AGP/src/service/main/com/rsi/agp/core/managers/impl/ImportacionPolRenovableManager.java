/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  21/06/2010  Ernesto Laura     Manager que gestiona todo lo relacionado
* 											con las importaciones y sus historicos      
*
 **************************************************************************************************
*/
package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.core.jmesa.dao.impl.IImportacionPolRenovableDao;
import com.rsi.agp.core.jmesa.service.impl.PolizaRenBean;
import com.rsi.agp.core.jmesa.service.utilidades.IAltaPolizaRenovableService;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

import es.agroseguro.estadoRenovacion.Renovacion;

public class ImportacionPolRenovableManager implements IManager {
	private static final Log logger = LogFactory.getLog(ImportacionPolRenovableManager.class);

	private IImportacionPolRenovableDao importacionPolRenovableDao;
	private IPolizasPctComisionesDao polizasPctComisionesDao;
	private IAltaPolizaRenovableService altaPolizaRenovableService;

	public HashMap<String, Object> importaPolizaRen(Long codPlan, Long codLinea, String refPolRenovable,
			String realPath, String codUsuario, Session session) throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		Renovacion polRen = null;
		List<PolizaRenBean> lstRes = new ArrayList<PolizaRenBean>();
		PolizaRenovable polizaHbm;
		StringBuilder polizasOK = null;

		logger.debug("Dentro de ImportacionPolRenovableManager - importaPolizaRen [INIT]");

		try {
			// Guardamos la relacion fichero->tablas
			boolean existePol = importacionPolRenovableDao.existePolRenovable(codPlan, codLinea, refPolRenovable);
	
			if (!existePol) {
				
				logger.debug("No existe la póliza Renovable- Procedemos a buscar la póliza");
				polRen = new SWListaPolizasRenovablesHelper().getListaPolizaRenovables(codPlan, codLinea, refPolRenovable,
						realPath);
	
				if (polRen != null) {
					/* Actualizamos con la respuesta */
					String xml = polRen.xmlText();
					/* Grabamos en tabla de auditoria el envío */
					 importacionPolRenovableDao.grabaAuditoriaSWPolRenovable(codPlan, codLinea, refPolRenovable, codUsuario, xml);
					 polizaHbm = new PolizaRenovable();
					 boolean batch = false;
					 
					 ColectivosRenovacion colRen = altaPolizaRenovableService.ValidatePolizaRenColectivo(polRen, batch, session);
					 
					 if (colRen != null) {
						 
						 polizaHbm.setColectivoRenovacion(colRen);
					 	 
					 	 boolean polizaOK = altaPolizaRenovableService.populateAndValidatePolizaRen(lstRes, polizaHbm, polRen, session, 
							 			polizasOK, polizasPctComisionesDao, batch, codUsuario);
					 	 
					 	 if (polizaOK) {
					 		 parameters.put("mensaje", "Poliza Renovable dada de alta correctamente.");
					 	 }else {
					 		parameters.put("alerta", "Se ha producido un error al dar de alta la póliza Renovable. No se encuentran gastos");
					 	 }
					 }else {
						 /* retornamos error de colectivo inexistente */
						 parameters.put("alerta", "Colectivo de la póliza Renovable, No Existe");
					 }
					 
				}else {
					parameters.put("alerta", "No se encontró ninguna póliza a renovar");
				}
	
			}else {
				parameters.put("alerta", "Póliza Renovable ya existente");
			}

		} catch (Exception e) {
			logger.debug("Atención, Atención, Se ha producido un error en el Alta de la póliza Renovable");
			parameters.put("alert", "Atención, Se ha producido un error en el Alta de la póliza Renovable");
			parameters.put("resultado", "KO");
		}
		logger.debug("Dentro de ImportacionPolRenovableManager - importaPolizaRen [END]");

		return parameters;
	}
	
	/*
	 * @param importacionPolRenovableDao
	 */
	public void setImportacionPolRenovableDao(IImportacionPolRenovableDao importacionPolRenovableDao) {
		this.importacionPolRenovableDao = importacionPolRenovableDao;
	}
	
	public void setAltaPolizaRenovableService(IAltaPolizaRenovableService altaPolizaRenovableService) {
		this.altaPolizaRenovableService = altaPolizaRenovableService;
	}
	
	public void setPolizasPctComisionesDao(IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}
	
}
