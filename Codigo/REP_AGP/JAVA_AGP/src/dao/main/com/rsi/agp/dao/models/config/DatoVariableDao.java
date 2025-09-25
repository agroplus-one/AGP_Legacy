package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.TipoCapital;


public class DatoVariableDao extends BaseDaoHibernate implements IDatoVariableDao{
	
	private static final Log logger = LogFactory.getLog(DatoVariableDao.class);

	/** DAA 10/09/2013
	 *  Devuelve una lista de codConceptos de Datos variables de las parcelas de la poliza
	 * @param idpoliza
	 * @return
	 * @throws DAOException 
	 */
	public List<BigDecimal> getDatosVariableParcelas(Long idPoliza) throws DAOException {
	
		List<BigDecimal> lista = new ArrayList<BigDecimal>();
		Session session = this.obtenerSession();
		
		logger.debug("---  DatoVariableDao.getDatosVariableParcelas" );
		
		try{
			String sql = " select distinct(codconcepto) as COD_CONCEPTO from tb_datos_var_parcela dv where dv.idcapitalasegurado in" +
						 " (select ca.idcapitalasegurado from tb_capitales_asegurados ca where ca.idparcela in (select idparcela" +
						 "  from tb_parcelas pa where pa.idpoliza ="+idPoliza+ "))";
			
			lista = session.createSQLQuery(sql).list();
			return lista;	
			
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);	
		}		

	}
	
	/**
	 * Método para obtener la descripción para el valor de un dato variable
	 * @param codtipocapital Código del tipo de capital
	 * @return Tipo Capital con todos los datos
	 */
	public String getDescripcionDatoVariable (Long lineaseguroid, BigDecimal codconcepto, String codigo) throws DAOException{
		Session session = obtenerSession();
		try {
			String sql = "select descripcion from o02agpe0.vw_datos_variables_parcela " +
					"where lineaseguroid in (0, " + lineaseguroid + ") and codconcepto = " + codconcepto +
					" and codigo = " + codigo;
		
			List<String> descripciones = session.createSQLQuery(sql).list();
			if (descripciones.size() > 0)
				return descripciones.get(0);

		} catch (Exception ex) {
			logger.error("Error al obtener la descripcion del dato variable " +  codconcepto + ", " + codigo, ex);
			throw new DAOException(
					"Error al obtener la descripcion del dato variable " +  codconcepto + ", " + codigo,
					ex);
		} finally {
		}
		return null;
	}
	
	/**
	 * Método para obtener la descripción para el valor de un dato variable
	 * @return descripcion
	 */
	public String getDescDatoVariableGanado (BigDecimal codConcepto, String codValor) throws DAOException{
		Session session = obtenerSession();
		try {
			String sql = "select descripcion from o02agpe0.TB_SC_C_DATOS_BUZON_GENERAL where" +
		 " codcpto = " + codConcepto + " and valor_cpto = " + codValor;
		
			List<String> descripciones = session.createSQLQuery(sql).list();
			if (descripciones.size() > 0)
				return descripciones.get(0);

		} catch (Exception ex) {
			logger.error("Error al obtener la descripcion del dato variable codConcepto: " +  codConcepto + ", codValor:" + codValor, ex);
			throw new DAOException(
					"Error al obtener la descripcion del dato variable codConcepto:" +  codConcepto + ", codValor:" + codValor,
					ex);
		} finally {
		}
		return null;
	}
	
}
