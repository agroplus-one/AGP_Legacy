package com.rsi.agp.dao.models.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.IDatabaseManager;
import com.rsi.agp.dao.tables.log.HistImportaciones;
import com.rsi.agp.dao.tables.log.TipoImportacion;
import com.rsi.agp.dao.tables.poliza.Linea;

public class DatabaseManager implements IDatabaseManager {

	SessionFactory sessionFactory;
	SessionFactory sessionFactoryImportacion;

	protected final Log log = LogFactory.getLog(getClass());
	
	public void createBackupImportacion(final String tablas){
		log.debug("Llamada al procedimiento o02agpe1.PQ_IMPORTACION_CSV.PR_CREATE_BACKUP('" + tablas + "')");
		Session session = sessionFactoryImportacion.openSession();		
		if (session != null) {
			session.doWork(new Work() {
				public void execute(Connection connection) throws SQLException {
					try (CallableStatement call = connection.prepareCall("{ call o02agpe1.PQ_IMPORTACION_CSV.PR_CREATE_BACKUP(?) }")) {
						call.setString(1, tablas);
						call.execute();
					}
				}
			});			
			session.close();
		}
	}

	@SuppressWarnings("deprecation")
	public Map<String, Object> executeStoreProc(String spName, Map<String, Object> inParameters) throws BusinessException {
		
		Session session = null;

		Map<String, Object> result = new HashMap<String, Object>();
		
		try {

			session = sessionFactory.openSession();
			StringBuffer query = new StringBuffer("call ");
			int parametersStart = spName.indexOf("(") + 1;
			ArrayList<String> outParameters;
			query.append(spName.substring(0, parametersStart));
			String parameters = spName.substring(parametersStart, spName.lastIndexOf(")"));
			StringTokenizer st = new StringTokenizer(parameters, ",");
			int resultSize = st.countTokens();

			for (int numParams = 0; numParams < resultSize; numParams++) {
				query.append("?,");
			}
			query.deleteCharAt(query.lastIndexOf(","));
			query.append(")");

			boolean isFunction = false;
			if (spName.indexOf("RETURN", spName.lastIndexOf(")")) != -1) {
				query.insert(0, "? = ");
				isFunction = true;
				resultSize++;
			}

			log.debug("Se va a aejecutar " + query.toString());
			CallableStatement statement = session.connection().prepareCall("{" + query.toString() + "}");
			
			int parametersIndex = 1;
			
			outParameters = new ArrayList<String>(resultSize);
			
			if (isFunction) {
				String type = spName.substring(spName.lastIndexOf("RETURN") + 7);
				outParameters.add(parametersIndex - 1, FUNCTION_RESULT);
				statement.registerOutParameter(parametersIndex++, getSQLType(type));
			}
			while (st.hasMoreElements()) {
				String parameter = (String) st.nextElement();
				StringTokenizer stParameter = new StringTokenizer(parameter, " ");
				String name = (String) stParameter.nextElement();
				String inOut = (String) stParameter.nextElement();
				String type = (String) stParameter.nextElement();
				if (inOut.equalsIgnoreCase(IDatabaseManager.PARAMETER_IN)) {
					statement.setObject(parametersIndex, inParameters.get(name.toUpperCase()));
					outParameters.add(parametersIndex - 1, null);
				} else if (inOut.equalsIgnoreCase(IDatabaseManager.PARAMETER_OUT)) {
					statement.registerOutParameter(parametersIndex, getSQLType(type));
					outParameters.add(parametersIndex - 1, name);
				}
				parametersIndex++;
			}

			statement.execute();
			log.debug("Procedimiento ejecutado correctamente.");
			
			for (int outParametersIndex = 0; outParametersIndex < outParameters.size(); outParametersIndex++) {
				if (outParameters.get(outParametersIndex) == null)
					continue;
				String name = outParameters.get(outParametersIndex);
				Object value = statement.getObject(outParametersIndex + 1);
				result.put(name, value);
			}
			
		} catch (HibernateException e) {
			log.error("Se produjo el siguiente error al ejecutar el procedimiento '" + spName + "'", e);
			throw new BusinessException(e);
		} catch (SQLException e) {
			log.error("Se produjo el siguiente error al ejecutar el procedimiento '" + spName + "'", e);
			throw new BusinessException(e);
		} catch (Exception e) {
			log.error("Se produjo el siguiente error al ejecutar el procedimiento '" + spName + "'", e);
			throw new BusinessException(e);
		} finally {
			if (session != null)
				session.close();
		}
		
		return result;

	}
	
	
	
	public int executeStatementErrorImportacion(String error, int tipoImportacion, Long lineaseguroid) throws BusinessException {
		Session session = null;
		Transaction tx = null;
		int result = 0;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			HistImportaciones hist = new HistImportaciones();
			TipoImportacion tipo = new TipoImportacion();
			tipo.setIdtipoimportacion(new Long(tipoImportacion));
			hist.setTipoImportacion(tipo);
			hist.setFechaimport(new Date());
			hist.setEstado("Error");
			Linea linea = new Linea();
			linea.setLineaseguroid(lineaseguroid);
			hist.setLinea(linea);
			hist.setDescerror(error);
			
			session.save(hist);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			log.error("Se produjo el siguiente error al ejecutar la inserción: " + e.getMessage());
			throw new BusinessException(e);
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			log.error("Se produjo el siguiente error al ejecutar la inserción: " + e.getMessage());
			throw new BusinessException(e);
		} finally {
			if (session != null)
				session.close();
		}
		return result;

	}

	private int getSQLType(String type) {
		if (type.equals(IDatabaseManager.DB_TYPE_NUMBER))
			return Types.NUMERIC;
		else if (type.equals(IDatabaseManager.DB_TYPE_VARCHAR2) || type.equals(IDatabaseManager.DB_TYPE_VARCHAR))
			return Types.VARCHAR;
		else if (type.equals(IDatabaseManager.DB_TYPE_DATE))
			return Types.DATE;
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rsi.tec.core.dao.impl.IDatabaseManager#setSessionFactory(org.hibernate.SessionFactory)
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}



	public void setSessionFactoryImportacion(SessionFactory sessionFactoryImportacion) {
		this.sessionFactoryImportacion = sessionFactoryImportacion;
	}

}
