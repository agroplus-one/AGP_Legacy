package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ComparativaPolizaComparator;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.dao.filters.poliza.CaracteristicaExplotacionFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.CaracteristicaExplotacion;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;


/**
 * 
 * DAO
 * Clase de utilidad para todas las operaciones relacionadas con la caracteristica de explotacion
 * @author 
 *
 */
public class CaracteristicaExplotacionDao extends BaseDaoHibernate implements ICaracteristicaExplotacionDao {
   
	
	private static final Log logger = LogFactory.getLog(CaracteristicaExplotacionDao.class);
	
	/**
	 * Validar caracteristica explotacion
	 */
	public boolean validarCaractExplotacion(Poliza poliza) throws DAOException {
		
		boolean isValid = false;
		
		try {

			for(ComparativaPoliza comparativaPoliza : poliza.getComparativaPolizas()) {
				if(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION).equals(comparativaPoliza.getId().getCodconcepto())) {
					isValid = true;
					break;
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CaracteristicaExplotacionDao] validarCaractExplotacion().", ex);
		}
		
		return isValid;
	}
	
	/**
	 * Aplica y borra carac. de explotacion, devuelve si aplica o no.
	 */
	public boolean aplicaYBorraCaractExplocion(Poliza poliza) throws DAOException {
		
		boolean aplicaCaractExpl = true;
		
		try {
		
			// Para cada comparativa, solo se llama al servicio web por cada comparativa distinta 
			List<ComparativaPoliza> listComparativasPoliza = poliza
					.getComparativaPolizas() != null ? Arrays.asList(poliza
					.getComparativaPolizas()
					.toArray(new ComparativaPoliza[] {}))
					: new ArrayList<ComparativaPoliza>();
			
			Collections.sort(listComparativasPoliza, new ComparativaPolizaComparator());
			aplicaCaractExpl = aplicaCaractExplotacion(poliza.getLinea().getLineaseguroid());
	
			if (aplicaCaractExpl && !poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)){
				//1. Borro la caracteristica de la explotacion anterior (el paso 2 esta mas adelante)
				for (ComparativaPoliza cp : listComparativasPoliza) {
					if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION).equals(cp.getId().getCodconcepto())) {
						poliza.getComparativaPolizas().remove(cp);
						deleteCaractExplotacion(poliza.getIdpoliza());						
						break;
					}
				}
			}
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CaracteristicaExplotacionDao] aplicaYBorraCaractExplocion().", ex);
		}
		
		return aplicaCaractExpl;
	}

	/**
	 * Metodo para comprobar si una poliza necesita la caracteristica de la
	 * explotacion en los datos variables de cobertura
	 * 
	 */
	public boolean aplicaCaractExplotacion(final Long lineaseguroid) throws DAOException {
		// consultamos en la base de datos (Organizador de informacion) si es necesario
		// incluir el codigo de concepto 106 para el uso poliza (31) y la ubicacion cobertura datos variables (18)
		
		Integer numObjects;
		
		try {
		
			CaracteristicaExplotacionFiltro filter = new CaracteristicaExplotacionFiltro(lineaseguroid);
			numObjects = this.getNumObjects(filter);
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CaracteristicaExplotacionDao] aplicaCaractExplotacion().", ex);
		}

		return numObjects > 0;
	}

	/**
	 * borra carac. explotacion
	 */
	public void deleteCaractExplotacion(final Long idpoliza) throws DAOException {
		Session session = obtenerSession();
		
		try {
			
			Query query = session
					.createSQLQuery("delete from tb_comparativas_poliza where idpoliza = :idpoliza and codconcepto = "
							+ ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION)
					.setLong("idpoliza", idpoliza);
			query.executeUpdate();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CaracteristicaExplotacionDao] deleteCaractExplotacion().", ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CaracteristicaExplotacion getCaracteristicaExplotacion(int codCaracteristicaExplotacion) throws DAOException {
		
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CaracteristicaExplotacion.class);
			criteria.add(Restrictions.eq("codcaractexplotacion", new BigDecimal(codCaracteristicaExplotacion)));
			
			List<CaracteristicaExplotacion> caracts = criteria.list();
			if (caracts.size() > 0)
				return caracts.get(0);

		} catch (Exception ex) {
			logger.error("Error al obtener los datos de la caracteristica de la explotacion " +  codCaracteristicaExplotacion, ex);
			throw new DAOException(
					"Error al obtener los datos de la caracteristica de la explotacion " +  codCaracteristicaExplotacion,
					ex);
		}
		return null;
	}
}
