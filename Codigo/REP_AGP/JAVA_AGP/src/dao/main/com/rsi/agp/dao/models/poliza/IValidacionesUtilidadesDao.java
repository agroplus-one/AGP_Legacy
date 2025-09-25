package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IValidacionesUtilidadesDao extends GenericDao {
	
	/**
	 * Devuelve el numero de polizas cuyos id estan dentro de 'idspoliza' y no pueden ser borradas ya que su estado es diferente a 
	 * 'Pendiente Validacion' y 'Grabacion Provisional'
	 * @param idsPoliza 
	 * @return
	 * @throws DAOException
	 */
	public int getCountPlzBorradoMasivo (List<String> idsPoliza) throws DAOException;
	
	
	/** Obtiene un String que puede ser: o bien la entidad por la que se haria el Cambio Oficina 
	 * o bien "false" si las entidades de las polizas seleccionadas son diferentes. 
	 * @parm idsPoliza
	 * @return String 
	 * @throws DAOException
	 */
	public String getEntidadCambioOficinaMasivo(List<String> idsPoliza) throws DAOException;


	/**
	 * Devuelve si en las polizas cuyos id estan dentro de 'idspoliza' hay alguna que no puede
	 * ser cambiada de oficina ya que su estado es 'Anulada'
	 * @param idsPoliza 
	 * @return
	 * @throws DAOException
	 */
	public boolean hayPolizasAnuladas(List<String> idsPoliza) throws DAOException;

}
