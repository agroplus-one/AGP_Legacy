package com.rsi.agp.batch.comisiones.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.batch.bbdd.Conexion;

public class ImportacionConnectionPool {

	private static final Log logger = LogFactory.getLog(ImportacionConnectionPool.class);

	public Connection getConnection() throws SQLException {
		Connection conexion = null;
		conexion = DriverManager.getConnection(ConfigBuzonInfovia.getProperty(ConfigBuzonInfovia.URL_BBDD),
				ConfigBuzonInfovia.getProperty(ConfigBuzonInfovia.BBDD_USER),
				ConfigBuzonInfovia.getProperty(ConfigBuzonInfovia.BBDD_PASS));
		return conexion;
	}

	public boolean existeEnHistorico(String filename) {
		boolean existe = false;
		Conexion c = new Conexion();
		String sqlQuery = "SELECT NOMBREFICHERO FROM o02agpe0.TB_COMS_HIST_FICHS_IMPORTS WHERE NOMBREFICHERO='"
				+ filename + "'";
		List<Object> lista;
		try {
			lista = c.ejecutaQuery(sqlQuery, 1);
			if (lista != null && !lista.isEmpty()) {
				existe = true;
			}
		} catch (Exception e) {
			logger.error("Error en la consulta exiteEnHistorico fichero: " + filename, e);
		}
		return existe;
	}

	public boolean existeEnMovidos(String filename) {
		boolean existe = false;
		Conexion c = new Conexion();
		String query = "SELECT FICHERO FROM o02agpe0.tb_buzon_agro_ficheros_movidos WHERE FICHERO='" + filename + "'";
		List<Object> lista;
		try {
			lista = c.ejecutaQuery(query, 1);
			if (lista != null && !lista.isEmpty()) {
				existe = true;
			}
		} catch (Exception e) {
			logger.error("## ERROR en la consulta existeEnMovidos fichero: " + filename, e);
		}
		return existe;
	}

	public Long executeQueryGetId(String query) throws SQLException {		
		try (Connection con = getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)) {
			if (rs.next()) {
				return Long.valueOf(rs.getString(1));
			}
		} catch (NumberFormatException e) {
			logger.error(e);
			return null;
		} 
		return null;
	}

	public void executeQueryInsertParams(String sqlQuery, List<Object> params) throws SQLException {
		logger.info("********************** INSERT***********************");
		logger.info(sqlQuery);
		logger.info("params->");
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = getConnection();
			pst = con.prepareStatement(sqlQuery);
			for (int x = 0; x < params.size(); x++) {
				Object temp = params.get(x);
				if (temp != null) {
					logger.info(temp.toString());
				} else {
					logger.info("null");
				}
				// miramos los tipos para hacer el set adecuado
				if (temp instanceof Integer) {
					pst.setInt((x + 1), (Integer) temp);
				} else if (temp instanceof Long) {
					pst.setLong((x + 1), (Long) temp);
				} else if (temp instanceof String) {
					pst.setString((x + 1), (String) temp);
				} else if (temp instanceof Date) {
					pst.setDate((x + 1), new java.sql.Date(((Date) temp).getTime()));
				} else if (temp instanceof BigDecimal) {
					pst.setBigDecimal((x + 1), (BigDecimal) temp);
				} else
					pst.setObject((x + 1), temp);
			}
			pst.executeUpdate();
		} finally {
			if (pst != null)
				pst.close();
			if (con != null)
				con.close();
		}
		logger.info("FIN params->");
	}

	public String[] executeQueryGetDirectorio(String query) throws SQLException {
		String[] dirYTipoFichero = new String[2];
		try (Connection con = getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)) {
			while (rs.next()) {
				dirYTipoFichero[0] = rs.getString("directorio");
				dirYTipoFichero[1] = rs.getString("id");
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return dirYTipoFichero;
	}

	public void executeQueryInsert(String sqlQuery) throws SQLException {
		Connection con = null;
		Statement st = null;
		try {
			con = getConnection();
			st = con.createStatement();
			st.executeUpdate(sqlQuery);
			con.commit();
		} catch (SQLException e) {
			logger.error(e);
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
		} finally {
			if (st != null)
				st.close();
			if (con != null)
				con.close();
		}

	}

	public long executeQueryMaxId(String query) throws SQLException {
		long maxId = 0;
		try (Connection con = getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)) {
			while (rs.next()) {
				maxId = rs.getLong(1);
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		return maxId;
	}

	public String[] executeQuerySelectLabel(String query) throws SQLException {
		String[] listaEtiquetas = null;
		List<String> listaS = new ArrayList<String>();
		try (Connection con = getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)) {
			while (rs.next()) {
				listaS.add(rs.getString("etiqueta"));
			}
			listaEtiquetas = new String[listaS.size()];
			for (int i = 0; i < listaS.size(); i++) {
				listaEtiquetas[i] = listaS.get(i);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return listaEtiquetas;
	}
}