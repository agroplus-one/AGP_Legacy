/**
 * 
 */
package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.BaseDaoHibernate;

/**
 * @author U029769
 */
public class CambioClaseMasivoDao extends BaseDaoHibernate implements ICambioClaseMasivoDao{

	/**
	 * Dado una lista de ids de poliza cuenta las que
	 * no estan en estado enviada correcta.
	 * @author U029769 12/06/2013
	 * @param listaIds
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getNumPolizasNoEnvCorr(List<String> listaIds) throws DAOException{
		List<String> listaIdPolizasAgrupadas = getListaIdPolizasAgrupadas(listaIds);
		final Session sesion = obtenerSession();
		boolean primerGrupoIdPolizas = true;
		try {
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append( "select count(*) from tb_polizas p where 1=1 ");
			
			if(listaIdPolizasAgrupadas.size() > 0) {
			primerGrupoIdPolizas = true;
				for(String cadenaIdsPol : listaIdPolizasAgrupadas) {
					if(primerGrupoIdPolizas) {
						stringQuery.append(" and (p.idpoliza in (" + cadenaIdsPol + ")");
						primerGrupoIdPolizas = false;
					}
					else {
						stringQuery.append(" or p.idpoliza in (" + cadenaIdsPol + ")");
					}
				}
				stringQuery.append(")");
				stringQuery.append(" and p.idestado not in (" + Constants.ESTADO_POLIZA_DEFINITIVA + "," + Constants.ESTADO_POLIZA_EMITIDA + ")");
			}
			
			List<BigDecimal> list = sesion.createSQLQuery (stringQuery.toString()).list();
			return list.get(0);
		
		}catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	/**
	 * actualiza la clase de una lista de ids de polizas
	 * @author U029769 12/06/2013
	 * @param idsPolizas
	 * @param idclase
	 * @throws DAOException
	 */
	@Override
	public void actualizaClasePoliza(List<Long> idsPolizas, BigDecimal idclase) throws DAOException {
		
		List<String> listaIdPolizas = new ArrayList<String>();
		for(Long id : idsPolizas) {
			listaIdPolizas.add(String.valueOf(id));
		}
		
		List<String> listaIdPolizasAgrupadas = getListaIdPolizasAgrupadas(listaIdPolizas);
		
		final Session session = obtenerSession();
		boolean primerGrupoIdPolizas = true;
		
		try {

			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append( "update tb_polizas p set clase = " + idclase + " where 1=1 ");
			
			if(listaIdPolizasAgrupadas.size() > 0) {
				primerGrupoIdPolizas = true;
				   for(String cadenaIdsPol:listaIdPolizasAgrupadas) {
					   if(primerGrupoIdPolizas) {
						   stringQuery.append(" and (p.idpoliza in (" + cadenaIdsPol + ")");
						   primerGrupoIdPolizas = false;
				       }
					   else {
						   stringQuery.append(" or p.idpoliza in (" + cadenaIdsPol + ")");
				       }
				   }
				   stringQuery.append(")");
			}
			
			session.createSQLQuery(stringQuery.toString()).executeUpdate();
			
		}catch (Exception ex) {
			logger.info("Se ha producido un error al actualizar la clase de la poliza: " + ex.getMessage());
			throw new DAOException("Se ha producido un error al actualizar la clase de la poliza", ex);
		}
		
	}
	
	/**
	 * Dado el id de poliza, se intenta obtener el id de la linea de seguro de la poliza
	 * @author U029769 21/03/2017
	 * @param idPoliza
	 * @return Long
	 * @throws DAOException
	 */
	public Long getLineaSeguroIdFromPoliza(Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		Long idLineaSeguro = null;
		try {
			String sqlQuery = "select pol.lineaseguroid from tb_polizas pol where pol.idpoliza = " + idPoliza;
			SQLQuery query = session.createSQLQuery(sqlQuery);
			idLineaSeguro = new Long((query.uniqueResult()).toString());
			
		} catch (Exception e) {
			logger.error("Error al intentar obtener el id de la línea de seguro en función del id de póliza " + idPoliza , e);
		}
		return idLineaSeguro;
	}
	
	public List<String> getListaIdPolizasAgrupadas(List<String> listaIdPolizas) {
		List<String> listaCadenasIds = new ArrayList<String>();
		int contador = 0;
		String cadena = "";
		boolean primera = true;
		for (String id : listaIdPolizas) {
			if (contador < 1000) {
				if (!primera)
					cadena = cadena + ",";
				else
					primera = false;
				cadena = cadena + id;
				contador++;			
			}else {
				if (cadena.length() > 0)
					listaCadenasIds.add(cadena);
				cadena = id;
				contador = 1;
			}
		}
		listaCadenasIds.add(cadena);
		logger.debug("Numero total de elementos: " + listaIdPolizas.size() + ". Listas agrupadas: " + listaCadenasIds.size());		
		return listaCadenasIds;
	}

}
