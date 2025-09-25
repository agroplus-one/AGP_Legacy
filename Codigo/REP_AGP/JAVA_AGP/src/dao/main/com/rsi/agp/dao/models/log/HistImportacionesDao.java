package com.rsi.agp.dao.models.log;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class HistImportacionesDao extends BaseDaoHibernate implements IHistImportacionesDao {
	
	private int paramSeguroId;
	private int resultado;

	public int compruebaRegistrosPL(int lineaSeguroId, final Boolean forzarActivar) throws BusinessException {
		this.paramSeguroId = lineaSeguroId;
 
		Session session = this.getSessionFactory().openSession();
		if (session != null) {
			session.doWork(new Work() {
				public void execute(Connection connection) throws SQLException {
					try (CallableStatement call = connection.prepareCall("{ ? = call o02agpe0.PQ_ACTIVACION.FN_COMPRUEBAREGISTROSPL(?,?) }")) {
						call.registerOutParameter(1, Types.NUMERIC);
						call.setInt(2, paramSeguroId);
						call.setString(3, forzarActivar.toString());
						call.execute();					
						int result = call.getInt(1);
						resultado = result;
					}
				}
			});			
			session.close();
		}
		return resultado;
	}
	
	@Override
	public boolean mostrarCoberturas(Long lineaseguroId) throws DAOException {
		Map<String,Object> resultado = new HashMap<String, Object>();
		String procedure = "o02agpe0.pq_activacion.FN_MOSTRAR_COBERTURAS(P_LINEASEGUROID IN NUMBER) RETURN NUMBER";
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("P_LINEASEGUROID", lineaseguroId);
		try {
			resultado = this.databaseManager.executeStoreProc(procedure, inParameters);
			BigDecimal fn_resultado = (BigDecimal) resultado.get("RESULT");
			return fn_resultado.compareTo(new BigDecimal(0)) == 1;
		} catch (Exception e) {
			logger.error("Error al obtener el precio de la parcela.", e);
			throw new DAOException("Error en mostrarCoberturas: " + e.getMessage());
		}
	}
}
