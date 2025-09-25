package com.rsi.agp.batch.bbdd;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.rsi.agp.batch.common.ImportacionConstants;
import com.rsi.agp.core.webapp.util.StringUtils;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.CLOB;

@SuppressWarnings("deprecation")
public class Conexion {
	
	//Atributos para las conexiones a base de datos
	private String connectionUrl;
	private String userD;
	private String passD;
	
	public Conexion(){
		//Inicializamos los atributos necesarios para las conexiones con BBDD
		ResourceBundle bundle = ResourceBundle.getBundle("agp_conexion");
		connectionUrl = bundle.getString("CONNECTION_URL");
		userD = bundle.getString("USER_D");
		passD = bundle.getString("PASS_D");
	}
	
	public List<String> ejecutaQueryString(String sql) throws Exception {
		List<String> resultado = new ArrayList<String>();
		Connection conexion = null;
		try {
			conexion = this.getConnection();
			try (OraclePreparedStatement stmt = (OraclePreparedStatement) conexion.prepareStatement(sql);
					ResultSet rs = stmt.executeQuery()) {
				while (rs != null && rs.next()) {
					resultado.add(rs.getString(1));
				}
			}
			return resultado;
		} catch (SQLException e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		} catch (Exception e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		} finally {
			this.closeConnection(conexion);
		}
	}
	
	public List<Object> ejecutaQuery(String sql, int numArgumentos) throws Exception {
		List<Object> resultado = new ArrayList<Object>();
		Connection conexion = null;
		try {
			conexion = this.getConnection();
			try (OraclePreparedStatement stmt = (OraclePreparedStatement)conexion.prepareStatement(sql);
					ResultSet rs = stmt.executeQuery()) {			
			while (rs != null && rs.next()){
				Object[] registro = new Object[numArgumentos];
				//Recorro los argumentos de la select y los anhado al array de object que representa el registro
				for (int i = 0; i < numArgumentos; i++){
					registro[i] = rs.getObject(i+1);
				}
				//Anhado el registro al resultado
				resultado.add(registro);
			}
			}
			return resultado;			
		} catch (SQLException e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		} catch (Exception e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		} 
		finally{
			this.closeConnection(conexion);
		}
	}
	
	public void ejecutaUpdate(String sql) throws Exception {
		Connection conexion = null;		
		try {
			conexion = this.getConnection();			
			try (OraclePreparedStatement stmt = (OraclePreparedStatement)conexion.prepareStatement(sql)) {
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		} catch (Exception e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		}  finally{
			this.closeConnection(conexion);
		}
	}
	
	public void ejecutaUpdate(String sql, Connection conexion) throws Exception {
		try (OraclePreparedStatement stmt = (OraclePreparedStatement)conexion.prepareStatement(sql)) {			
			stmt.executeUpdate();			
		} catch (SQLException e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		} catch (Exception e) {
			System.out.println(sql + ": " + StringUtils.stack2string(e));
			throw e;
		}
	}
	
	/**
	 * Inserta en la tabla TB_COMUNICACIONES un registro asociado al nombre del fichero indicado en el parametro
	 * para que no se vuelva a procesar en las siguientes ejecuciones del proceso.
	 * Este tipo de registros se insertan con el tipo de fichero a X
	 * @param fichero Nombre del fichero (no perteneciente a Agroplus o no procesable) que se insertara en la tabla de comunicaciones
	 * @throws Exception 
	 */
	public void insertaRegComunicaciones (String fichero) throws Exception {
		
		Connection conexion = null;
		String sql = null;
		
		try {
			conexion = this.getConnection();
			
			//Desactivo el autocommit
			conexion.setAutoCommit(false);
		
			// Compone el insert
			sql = "INSERT INTO o02agpe0.TB_COMUNICACIONES VALUES (o02agpe0.sq_comunicaciones.nextval, sysdate, null, null, null, '" +
						fichero +
						"', null, 'X', null)";
			
			//Actualizamos la tabla de comunicaciones
			this.ejecutaUpdate(sql, conexion);
			
			//hago commit activo el autocommit de la conexion (por si fuera necesario)
			conexion.commit();
			conexion.setAutoCommit(true);
		} catch (Exception e) {
			try {
				if(conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			throw e;			
		} finally{			
			this.closeConnection(conexion);
		}
	}
	
	/**
	 * Metodo para actualizar el acuse de recibo obtenido de agroseguro para el envio de Agroplus indicado
	 * @param xml Contenido del acuse
	 * @param idenvio Identificador del envio
	 * @param resultado Resultado del procesamiento del acuse de recibo
	 * @param nomAcuseRecibo Nombre del fichero ZIP que contiene el acuse de recibo (con extension) 
	 */
	public void actualizaAcuse(String xml, String idenvio, String resultado, String nomAcuseRecibo) {
		Connection conexion = null;
		String sql = "";
		
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		CLOB clob = null;
		
		try {
			conexion = this.getConnection();
			
			//Desactivo el autocommit
			conexion.setAutoCommit(false);
			
			sql = "UPDATE o02agpe0.TB_COMUNICACIONES SET RESULTADO = '" + resultado + "', FICHERO_RECIBO = '" + 
					nomAcuseRecibo.substring(0, nomAcuseRecibo.indexOf(".ZIP")) + 
					"', FECHA_RECEPCION = SYSDATE, TIPO_MOV='RECEPCION', FICHERO_CONTENIDO = EMPTY_CLOB() WHERE IDENVIO = " + idenvio;
		
			//Actualizamos la tabla de comunicaciones
			this.ejecutaUpdate(sql, conexion);
			
			if(xml != null){
				sql = "SELECT FICHERO_CONTENIDO FROM o02agpe0.TB_COMUNICACIONES WHERE IDENVIO = " + idenvio + " FOR UPDATE";
			
				stmt = conexion.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()){
					clob = (CLOB) rs.getClob("FICHERO_CONTENIDO");
					
					OutputStream os = clob.setAsciiStream(1);
	
					byte[] b = xml.getBytes("ASCII");
	
					os.write(b);
					os.flush();
					os.close();
	
				}
			}
			//hago commit activo el autocommit de la conexion (por si fuera necesario)
			conexion.commit();
			conexion.setAutoCommit(true);
			
		} catch (SQLException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error SQLException al actualizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error UnsupportedEncodingException al actualizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} catch (IOException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error IOException al actualizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} catch (Exception e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error inesperado al acutalizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} finally{
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("No se pudo el Statement por " + StringUtils.stack2string(e));
			}
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar el Resulset por " + StringUtils.stack2string(e));
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar el PreparedStatement por " + StringUtils.stack2string(e));
			}
			this.closeConnection(conexion);
		}
	}
	
	/**
	 * Metodo para actualizar el acuse de recibo obtenido de agroseguro para el envio de Correduria externa indicado
	 * @param xml Contenido del acuse
	 * @param idenvio Identificador del envio
	 * @param resultado Resultado del procesamiento del acuse de recibo
	 * @param nomAcuseRecibo Nombre del fichero ZIP que contiene el acuse de recibo (con extension) 
	 */
	public void actualizaAcuseCorreduriaExterna(String xml, String idenvio, String resultado, String nomAcuseRecibo, String ficheroEnviado) {
		Connection conexion = null;
		String sql = "";
		
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		CLOB clob = null;
		
		try {
			conexion = this.getConnection();
			
			//Desactivo el autocommit
			conexion.setAutoCommit(false);
			
			sql = "UPDATE o02agpe0.TB_COMUNICACIONES_EXTERNAS SET RESULTADO_RECIBO = '" + resultado + "', FICHERO_RECIBO = '" + 
					nomAcuseRecibo.substring(0, nomAcuseRecibo.indexOf(".ZIP")) + 
					"', FECHA_RECIBO = SYSDATE, FICHERO_RECIBO_F = '" + ficheroEnviado + 
					"_AR', CONTENIDO_RECIBO = EMPTY_CLOB() WHERE ID = " + idenvio;
		
			//Actualizamos la tabla de comunicaciones
			this.ejecutaUpdate(sql, conexion);
			
			if(xml != null){
				sql = "SELECT CONTENIDO_RECIBO FROM o02agpe0.TB_COMUNICACIONES_EXTERNAS WHERE ID = " + idenvio + " FOR UPDATE";
			
				stmt = conexion.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()){
					clob = (CLOB) rs.getClob("CONTENIDO_RECIBO");
					
					OutputStream os = clob.setAsciiStream(1);
	
					byte[] b = xml.getBytes("ASCII");
	
					os.write(b);
					os.flush();
					os.close();
	
				}
			}
			//hago commit activo el autocommit de la conexion (por si fuera necesario)
			conexion.commit();
			conexion.setAutoCommit(true);
			
		} catch (SQLException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error SQLException al acutalizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error UnsupportedEncodingException al acutalizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} catch (IOException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error IOException al acutalizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} catch (Exception e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out.println("KO - Error inesperado al acutalizar el acuse: " + sql + ": " + StringUtils.stack2string(e));
			System.exit(-1);
		} finally{
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("Error SQLException al cerrar el Statement " + StringUtils.stack2string(e));
			}
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar el ResultSet " + StringUtils.stack2string(e));
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				System.out.println("No se pudo cerrar el PreparedStatement " + StringUtils.stack2string(e));
			}
			this.closeConnection(conexion);
		}
	}
	
	/**
	 * Metodo para llamar al pl de actualizacion del historico de estados
	 * @param idEnvio Identificador del envioo.
	 * @param tipoEnvio Tipo de envio
	 */
	public void actualizaHistoricoEstados(String idEnvio, String tipoEnvio){
		Connection conexion = null;
		CallableStatement vStatement = null;
		try {
			conexion = this.getConnection();
			vStatement = 
					conexion.prepareCall( "begin o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estados_recepcion( ?, ? ); end;" );
			vStatement.setString(1, idEnvio);
			vStatement.setString(2, tipoEnvio);
			
			vStatement.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Error al actualizar el historico de estados: " + StringUtils.stack2string(e));
		} finally {
			if (vStatement != null){
				try {
					vStatement.close();
				} catch (SQLException e) {
					System.out.println("No se pudo cerrar el CallableStatement " + StringUtils.stack2string(e));
				}
			}
			this.closeConnection(conexion);
		}
	}
	
	/**
	 * Metodo para llamar al pl actualizaAnexosDefNoEnviados
	 */
	public void actualizaAnexosDefNoEnviados(){
		Connection conexion = null;
		CallableStatement vStatement = null;
		try {
			conexion = this.getConnection();
			vStatement = 
					conexion.prepareCall( "begin o02agpe0.pq_genera_envios_agroseguro.actualizaAnexosDefNoEnviados; end;" );
			
			vStatement.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Error al actualizar los anexos definitivos no enviados: " + StringUtils.stack2string(e));
		} finally {
			if (vStatement != null){
				try {
					vStatement.close();
				} catch (SQLException e) {
					System.out.println("No se pudo cerrar el CallableStatement " + StringUtils.stack2string(e));
				}
			}
			this.closeConnection(conexion);
		}
	}
	
	public void guardaPolizaExtParaImportacion(final String codPlan,
			final String codLinea, final String referencia,
			final String idEnvio, final String tipoRef) {
		Connection conexion = null;
		String sql = "";

		try {
			conexion = this.getConnection();
			conexion.setAutoCommit(false);
			
			StringBuilder sb = new StringBuilder(
					"INSERT INTO o02agpe0.TB_IMPORTACION_PLZ_EXT ");
			sb.append("(ID, CODPLAN, CODLINEA, REFERENCIA, TIPOREF, ESTADO, FEC_IMPORTACION, DETALLE, IDENVIO) ");
			sb.append("VALUES (o02agpe0.SQ_IMPORTACION_PLZ_EXT.NEXTVAL, ");
			sb.append(codPlan);
			sb.append(", ");
			sb.append(codLinea);
			sb.append(", '");
			sb.append(referencia);
			sb.append("', '");
			sb.append(tipoRef);
			sb.append("', ");			
			sb.append(ImportacionConstants.ESTADO_IMPORTACION_PDTE);
			sb.append(", ");
			sb.append("SYSDATE, ");
			sb.append("'POLIZA PDTE DE IMPORTACION', ");
			sb.append(idEnvio);
			sb.append(")");

			sql = sb.toString();

			// Actualizamos la tabla de comunicaciones
			this.ejecutaUpdate(sql, conexion);
			conexion.commit();

		} catch (SQLException e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out
					.println("KO - Error al guardar la poliza para importacion: "
							+ sql + ": " + StringUtils.stack2string(e));
		} catch (Exception e) {
			try {
				if (conexion != null) conexion.rollback();
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
			System.out
					.println("KO - Error inesperado al guardar la poliza para importacion: "
							+ sql + ": " + StringUtils.stack2string(e));
		} finally {
			this.closeConnection(conexion);
		}
	}
	
	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(connectionUrl, userD, passD);
	}
	
	private void closeConnection(Connection conexion){
		try {
			if (conexion != null)
				conexion.close();
		} catch (SQLException e) {
			System.out.println("No se pudo cerrar la conexion " + StringUtils.stack2string(e));
		}
	}
}