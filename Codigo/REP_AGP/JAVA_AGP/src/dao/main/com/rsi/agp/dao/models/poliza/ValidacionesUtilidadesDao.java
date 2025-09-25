package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class ValidacionesUtilidadesDao extends BaseDaoHibernate implements IValidacionesUtilidadesDao {

	@Override
	public int getCountPlzBorradoMasivo (List<String> idsPoliza) throws DAOException {
		Session sesion = obtenerSession();
		try {
			String sqlQuery = "SELECT COUNT(*) FROM TB_POLIZAS P WHERE";			
			
			// Anhade los criterios de busqueda
			sqlQuery += " P.IDPOLIZA IN " + StringUtils.toValoresSeparadosXComas(idsPoliza,false);
			sqlQuery += " AND P.IDESTADO NOT IN (" + Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION + "," 
						+ Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL + ")";
			
			// Lanza la consulta y devuelve el valor del count
			return ((BigDecimal)sesion.createSQLQuery (sqlQuery).list().get(0)).intValue();			
			
		} 
		catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	/**DAA Obtiene un String que puede ser: o bien la entidad por la que se haria el Cambio Oficina 
	 * o bien "false" si las entidades de las polizas seleccionadas son diferentes. 
	 * @parm idsPoliza
	 * @return String 
	 */
	
	@Override
	public String getEntidadCambioOficinaMasivo (List<String> idsPoliza) throws DAOException {
		Session sesion = obtenerSession();
		try {
			String sqlQuery = "SELECT DISTINCT CODENTIDAD FROM TB_COLECTIVOS C WHERE ID IN( "; 			
			
			// Anhade los criterios de busqueda
			sqlQuery += "SELECT IDCOLECTIVO FROM TB_POLIZAS P WHERE P.IDPOLIZA IN " + StringUtils.toValoresSeparadosXComas(idsPoliza,false) + ")";
			
			// Lanza la consulta y devuelve el valor del count
			@SuppressWarnings("unchecked")
			List<BigDecimal> listaEntidades = sesion.createSQLQuery (sqlQuery).list();
			if (listaEntidades.size() == 1){
				//La entidad no esta repetida
				return listaEntidades.get(0).toString();
			}
			else{
				//hay entidades repetidas o no se ha encontrado entidad
				return "false";
			}
			
		} 
		catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	@Override
	public boolean hayPolizasAnuladas(List<String> idsPoliza) throws DAOException {
		Session sesion = obtenerSession();
		try {
			String sqlQuery = "SELECT COUNT(*) FROM TB_POLIZAS P WHERE";			
			
			// Anhade los criterios de busqueda
			sqlQuery += " P.IDPOLIZA IN " + StringUtils.toValoresSeparadosXComas(idsPoliza,false);
			sqlQuery += " AND P.IDESTADO = " + Constants.ESTADO_POLIZA_ANULADA;
			
			// Lanza la consulta y devuelve el valor del count
			int numPolsAnuladas = ((BigDecimal)sesion.createSQLQuery (sqlQuery).list().get(0)).intValue();			
			return numPolsAnuladas > 0;
		} 
		catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

}
