package com.rsi.agp.batch.importacionCondicionado.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;
import com.rsi.agp.dao.tables.cargas.CargasTablas;

public class ImportacionCondicionadoConnectionPool {

	private static final Log logger = LogFactory.getLog(ImportacionCondicionadoConnectionPool.class);

	public Connection getConnection() throws SQLException {

		Connection conexion = null;
		try {
			Class.forName(ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.DRIVER_BBDD));
			conexion = DriverManager.getConnection(
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.URL_BBDD),
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.BBDD_USER),
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.BBDD_PASS));
		} catch (ClassNotFoundException e) {
			logger.error("Error en la conexion", e);
			System.exit(1);
		}
		return conexion;
	}

	public List<Long> getCargasCerradas() {
		List<Long> listIds = new ArrayList<Long>();
		Connection con = null;
		ResultSet rs = null;
		Statement st = null;
		String query = "SELECT ID FROM o02agpe0.TB_CARGAS_CONDICIONADO WHERE ESTADO = 3 ORDER BY fecha_creacion ASC";
		logger.info(query);

		try {
			con = getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				Long s = rs.getLong("ID");
				listIds.add(s);
			}

		} catch (SQLException e) {
			logger.info("Error al obtener las cargas cerradas " + StringUtils.stack2string(e));
			logger.error("Error al obtener las cargas cerradas", e);
		} catch (Exception e) {
			logger.info("Error indefinido al obtener las cargas cerradas " + StringUtils.stack2string(e));
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return listIds;
	}

	public List<CargasFicheros> getFicherosbyID(Long idCarga) {
		List<CargasFicheros> listFicheros = new ArrayList<CargasFicheros>();
		CargasFicheros cf = null;
		Connection con = null;
		ResultSet rs = null;
		Statement st = null;
		String query = "SELECT * FROM o02agpe0.TB_CARGAS_FICHEROS WHERE ID_CARGA = '" + idCarga
				+ "' order by fecha asc";
		logger.info(query);

		try {
			con = getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				cf = new CargasFicheros();
				cf.setId(rs.getLong("ID"));
				cf.setFichero(rs.getString("FICHERO"));
				cf.setLinea(rs.getBigDecimal("LINEA"));
				cf.setPlan(rs.getBigDecimal("PLAN"));
				cf.setTipo(rs.getBigDecimal("TIPO"));
				listFicheros.add(cf);
			}

		} catch (SQLException e) {
			logger.error("Error al obtener los ficheros de la carga " + idCarga, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return listFicheros;
	}

	public List<CargasTablas> getTablas(Long idCargaFichero) {
		List<CargasTablas> listTablas = new ArrayList<CargasTablas>();
		CargasTablas cf = null;
		Connection con = null;
		ResultSet rs = null;
		Statement st = null;
		String query = "SELECT * FROM o02agpe0.TB_CARGAS_TABLAS WHERE ID_CARGA_FICHERO = '" + idCargaFichero
				+ "' AND ALTA='S'";
		logger.info(query);
		try {
			con = getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				cf = new CargasTablas();
				cf.setId(rs.getLong("ID"));
				cf.setFicheroxml(rs.getString("FICHEROXML"));
				cf.setNumtabla(rs.getBigDecimal("NUMTABLA"));
				listTablas.add(cf);
			}
		} catch (SQLException e) {
			logger.error("Error al obtener als tablas del fichero " + idCargaFichero, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return listTablas;
	}

	public void updateFechayEstado(long idcondicionado, int estadoCarga) {
		Connection con = null;
		Statement st = null;
		String query = "UPDATE o02agpe0.TB_CARGAS_CONDICIONADO SET FECHA_CARGA = SYSDATE,ESTADO=" + estadoCarga
				+ " WHERE ID=" + idcondicionado;
		logger.info(query);
		try {
			con = getConnection();
			con.setAutoCommit(false);
			st = con.createStatement();
			st.executeQuery(query);
			con.commit();
		} catch (SQLException e) {
			logger.error("Error al actualizar el estado de la carga " + idcondicionado, e);
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
}
