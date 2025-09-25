package com.rsi.agp.batch.comisiones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.batch.comisiones.util.ImportacionConnectionPool;
import com.rsi.agp.batch.comisiones.util.XMLValidationException;
import com.rsi.agp.batch.comisiones.util.XmlComisionesValidationUtil;
import com.rsi.agp.batch.comisiones.util.ZipUtil;
import com.rsi.agp.core.exception.BusinessException;

import oracle.sql.CLOB;

/**
 * @author t-systems
 * 
 *         Clase para procesar los ficheros. Al no poder apoyarnos en hibernate
 *         hay que generar las queries manualmente. Como es un proceso interno,
 *         evitamos parametrizaciones en la queries para comodidad.
 * 
 */
@SuppressWarnings("deprecation")
public class ProcesamientoFicherosBase {

	private static final Log logger = LogFactory.getLog(ProcesamientoFicherosBase.class);

	protected static final String SQLINSERTFASE = "INSERT INTO o02agpe0.tb_coms_fase (ID,FASE,PLAN,FECHAEMISION) VALUES (?,?,?,?)";
	protected static final String SQLINSERTFICHERO = " INSERT INTO o02agpe0.tb_coms_ficheros "
			+ " (id,nombrefichero,idfase,tipofichero,fechacarga,codusuario) " + " values (?,?,?,?,?,?)";

	ImportacionConnectionPool icp = null;

	/**
	 * Metodo que trata el fichero zip y valida su contenido
	 * 
	 * @param ficheroImportacion
	 * @param fase
	 * @param tipoImportacion
	 * @return
	 * @throws BusinessException
	 */
	protected XmlObject realizarValidacion(File ficheroImportacion, XmlObject fase, int tipoImportacion) {
		XmlObject xmlobj = null;
		logger.info("variables:" + ficheroImportacion.exists() + " ," + tipoImportacion);

		try {
			File temp = ZipUtil.getFirstFileInZip(ficheroImportacion);
			xmlobj = XmlComisionesValidationUtil.getXMLBeanValidado(temp, fase, tipoImportacion);
		} catch (XMLValidationException xmle) {
			logger.error("Error al validar el xml", xmle);
		} catch (Exception be) {
			logger.error("Error inesperado al validar el xml", be);
		}
		return xmlobj;
	}

	/**
	 * Comprueba si existe una fase para un plan devolviendo su id o null si no
	 * existe.
	 * 
	 * @param fase
	 * @param plan
	 * @return
	 * @throws SQLException
	 */
	protected Long existeFase(int fase, int plan) throws SQLException {
		String sqlGetFase = "select * from o02agpe0.tb_coms_fase where fase=" + fase + " and plan=" + plan + " ";
		return icp.executeQueryGetId(sqlGetFase);

	}

	protected Long existeFichero(String nombre) throws SQLException {
		String sqlComprobarFichero = "select * from o02agpe0.tb_coms_ficheros where " + "nombrefichero='" + nombre
				+ "'";
		return icp.executeQueryGetId(sqlComprobarFichero);
	}

	/**
	 * Obtiene el codigo lineaseguroid para una fase y plan
	 * 
	 * @param fase
	 * @param plan
	 * @return
	 */
	protected Long obtenerLineaseguroid(int fase, int plan) {
		String sqlGetFase = "select lineaseguroid from o02agpe0.tb_lineas where codlinea=" + fase + " and codplan="
				+ plan + " ";
		try {
			return icp.executeQueryGetId(sqlGetFase);
		} catch (SQLException e) {
			logger.error("Error al obtener el identificador de plan/linea", e);
		}
		return null;
	}

	protected Long getNewId(String nombreSecuencia) {
		String sqlGetId = "select o02agpe0." + nombreSecuencia + ".nextval from dual ";
		try {
			return icp.executeQueryGetId(sqlGetId);
		} catch (SQLException e) {
			logger.error("Error al obtener el nuevo identificador de " + nombreSecuencia, e);
		}
		return null;
	}

	/**
	 * Inserta una fase nueva con los datos enviados como parámetros.
	 * 
	 * @param idFase
	 * @param fase
	 * @param plan
	 * @throws SQLException
	 */
	protected void insertarFaseNuevo(Long idFase, int fase, int plan) throws SQLException {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(idFase);
		params.add(fase + "");
		params.add(Integer.valueOf(plan));
		params.add(new Date());
		icp.executeQueryInsertParams(ProcesamientoFicherosBase.SQLINSERTFASE, params);
	}

	protected void insertarFicheroNuevo(Long idFichero, String nombreFichero, Long idFase, String tipoFichero,
			Date fechaCarga, String codUsuario) throws SQLException {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(idFichero);
		params.add(nombreFichero.substring(0, 8));
		params.add(idFase);
		params.add(tipoFichero);
		params.add(fechaCarga);
		params.add(codUsuario);
		icp.executeQueryInsertParams(ProcesamientoFicherosBase.SQLINSERTFICHERO, params);
	}

	protected void insertarTextoFichero(File zipFile, Long idFichero, Connection con) throws IOException, SQLException {
		try {
			File temp = ZipUtil.getFirstFileInZip(zipFile);
			try (FileReader bodyIn = new FileReader(temp);
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT contenido FROM o02agpe0.tb_coms_ficheros " + "where id="
							+ idFichero + " for update");) {
				while (rs.next()) {
					String linea;
					String contenido = "";
					int pos = 1;
					CLOB clob = (CLOB) rs.getClob(1);
					try (BufferedReader br = new BufferedReader(bodyIn);
							OutputStream os = clob.setAsciiStream(1)) {
						while ((linea = br.readLine()) != null) {
							clob.setString(pos, linea);
							contenido = contenido.concat(linea);
							pos = pos + linea.getBytes().length + 1;
						}
						byte[] b = contenido.getBytes("ASCII");
						os.write(b);
						os.flush();
					}
				}
			}
			con.commit();
			logger.info("Contenido del fichero " + idFichero + " guardado");
		} catch (Exception e) {
			logger.error("Error al insertar el texto del fichero " + idFichero, e);
		}
	}

	protected String nullToString(Object cad) {
		if (cad == null || cad.equals("null")) {
			cad = "";
		}
		return remplazarComillado(cad.toString());
	}

	protected String remplazarComillado(String nombre) {
		return nombre.replace("'", "''");
	}
}