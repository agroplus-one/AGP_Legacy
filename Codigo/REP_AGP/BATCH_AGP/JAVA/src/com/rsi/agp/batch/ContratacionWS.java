package com.rsi.agp.batch;

import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.DatosAsociados;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarRequest;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarResponse;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ContratacionSCConfirmacion;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ContratacionSCConfirmacion_Service;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OraclePreparedStatement;

public class ContratacionWS {

	private static final String CONNECTION_URL = "jdbc:oracle:thin:@172.22.3.207:1521:OA9D";
	private static final String CONNECTION_USR = "o02agpe0";
	private static final String CONNECTION_PWD = "o02agpe0";

	private static final Log logger = LogFactory.getLog(ContratacionWS.class);

	/**
	 * Metodo principal de la clase para realizar la contratacion definitiva de
	 * polizas en Agroseguro
	 */
	public static void main(String[] args) {
		// 1. Consulta en la base de datos aquellas polizas que estan en estado
		// 'Grabacion definitiva' del plan actual y del anterior.
		try {
			List<String> idsPoliza = getPolizasDefinitivas();
			logger.info("Numero de polizas definitivas: " + idsPoliza.size());
			// 2. Por cada resultado, consultar en TB_ENVIOS_POLIZA la ultima version de
			// fichero xml de validacion enviado a Agroseguro
			// asignarselo al objeto peticion del servicio web y enviarlo.
			for (int i = 0; i < idsPoliza.size(); i++) {
				String xml = getXmlEnvio(idsPoliza.get(i) + "");
				if (!WSUtils.isProxyFixed())
					WSUtils.setProxy();

				doInternalJob(xml, idsPoliza.get(i));
			}
		} catch (SQLException e1) {
			logger.error(e1);
		}
	}

	private static void doInternalJob(final String xml, final String idPoliza) {
		AcuseRecibo acuseRecibo = null;
		try {
			acuseRecibo = confirmarPoliza(xml);

			// 3. Tratar acuse de recibo de cada resultado.
			if (acuseRecibo != null) {
				String referencia = tratarAcuseRecibo(acuseRecibo);
				logger.info("Referencia del acuse de la poliza: " + referencia);
				String estado;
				if (referencia != null) {
					// Guardar la referencia de la poliza y el acuse y poner el estado a
					// 'Definitivo' => codigo 8
					estado = "8";
				} else {
					// Guardar el acuse y poner el estado a 'Enviada Error' => codigo 7
					estado = "7";
				}
				String acuseStr = acuseRecibo.toString();
				logger.info("Acuse de recibo: " + acuseStr);
				// actualizamos la poliza en la base de datos
				actualizaPoliza(idPoliza + "", referencia, estado, acuseStr);

				logger.info("Poliza " + idPoliza + " tratada.");
			}
		} catch (Exception e) {
			logger.error("Error tratando la poliza " + idPoliza + ". ERROR: " + e.getMessage());
		}
	}

	/**
	 * Metodo para actualizar una poliza con los resultados del servicio web de
	 * contratacion de Agroseguro
	 * 
	 * @param idpoliza
	 *            Identificador de la poliza a actualizar
	 * @param referencia
	 *            Referencia proporcionada por Agroseguro
	 * @param estado
	 *            Estado con el que actualizar la poliza
	 * @param acuseStr
	 *            Cadena de texto que representa el xml de respuesta de Agroseguro
	 * @throws SQLException
	 */
	private static void actualizaPoliza(String idpoliza, String referencia, String estado, String acuseStr)
			throws SQLException {
		// Primero actualizo el estado y la referencia (si es necesario)
		String sql = "UPDATE TB_POLIZAS SET IDESTADO = ?, XMLACUSECONTRATACION = ?";
		if (referencia != null)
			sql += ", REFERENCIA = ?";
		sql += " WHERE IDPOLIZA = ?";
		// Creamos la conexion a base de datos
		Properties props = new Properties();
		props.put("user", CONNECTION_USR);
		props.put("password", CONNECTION_PWD);
		props.put("SetBigStringTryClob", "true");
		DriverManager.registerDriver(new OracleDriver());
		try (Connection conexion = DriverManager.getConnection(CONNECTION_URL, props);
				OraclePreparedStatement stmt = (OraclePreparedStatement) conexion.prepareStatement(sql)) {
			stmt.setBigDecimal(1, new BigDecimal(estado));
			stmt.setStringForClob(2, acuseStr);
			if (referencia != null) {
				stmt.setString(3, referencia);
				stmt.setLong(4, Long.parseLong(idpoliza));
			} else {
				stmt.setLong(3, Long.parseLong(idpoliza));
			}
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	/**
	 * Metodo para tratar cada uno de los acuses de recibo del servicio web de
	 * contratacion.
	 * 
	 * @param acuseRecibo
	 *            Acuse de recibo a tratar
	 * @return referencia de la poliza, en caso de que no haya errores.
	 */
	private static String tratarAcuseRecibo(AcuseRecibo acuseRecibo) {
		String referencia = null;
		if (acuseRecibo.getDocumentoArray(0) != null) {
			Documento doc = acuseRecibo.getDocumentoArray(0);
			if (doc.getEstado() == 1) {
				DatosAsociados da = doc.getDatosAsociados();
				referencia = da.getDomNode().getAttributes().getNamedItem("referencia").getNodeValue();
			} else {
				// hay que cambiar el estado de la poliza a 'Enviada Erronea'
			}
		}
		return referencia;
	}

	private static AcuseRecibo confirmarPoliza(String xml) throws Exception {
		AcuseRecibo acuseRecibo = null;
		URL wsdlLocation = null;
		String url = "";
		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("confirmacion.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new Exception("Imposible recuperar el WSDL de Contratacion. Revise la Ruta: " + url, e1);
		} catch (NullPointerException e1) {
			throw new Exception("Imposible obtener el WSDL de Contratacion. Revise la Ruta: " + wsdlLocation, e1);
		}

		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("confirmacion.location");
		String wsPort = WSUtils.getBundleProp("confirmacion.port");
		String wsService = WSUtils.getBundleProp("confirmacion.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		// Crea el envoltorio para la llamada al servicio web de contratacion
		ContratacionSCConfirmacion_Service srv = new ContratacionSCConfirmacion_Service(wsdlLocation, serviceName);
		ContratacionSCConfirmacion srvConfirmacion = (ContratacionSCConfirmacion) srv.getPort(portName,
				ContratacionSCConfirmacion.class);

		// Anhade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvConfirmacion);

		// Se valida el XML antes de llamar al Servicio Web
		WSUtils.getXMLPoliza(xml);

		// Se convierte el String a Base64, para enviarlo al WS
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes("UTF-8");
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error(e2);
			throw new Exception("Se esperaba un XML en formato UTF-8.", e2);
		}
		// Parametros de envio al Servicio Web
		ConfirmarRequest parameters = new ConfirmarRequest();
		parameters.setPoliza(base64Binary);

		ConfirmarResponse response = null;
		try {
			response = srvConfirmacion.confirmar(parameters);
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				Base64Binary respuesta = response.getAcuseRecibo();
				byte[] arrayAcuse = respuesta.getValue();
				String acuse = new String(arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));

				acuseRecibo = acuseReciboDoc.getAcuseRecibo();
			}
		} catch (Exception e) {
			logger.error(e);
			throw new Exception("Error inesperado al llamar al servicio web de Validacion", e);
		}

		return acuseRecibo;
	}

	/**
	 * Metodo para consultar las polizas definitivas del plan actual y del anterior
	 * 
	 * @return Listado de identificadores de polizas definitivas
	 * @throws SQLException
	 */
	private static List<String> getPolizasDefinitivas() throws SQLException {
		List<String> lista = new ArrayList<String>(0);
		GregorianCalendar gc = new GregorianCalendar();
		int anioActual = gc.get(Calendar.YEAR);
		anioActual = anioActual - 1;
		String sql = "SELECT P.IDPOLIZA FROM TB_POLIZAS P, TB_LINEAS L "
				+ "WHERE P.LINEASEGUROID = L.LINEASEGUROID AND IDESTADO = 3 AND " + "(L.CODPLAN = " + anioActual
				+ " OR L.CODPLAN = " + (anioActual - 1) + ")";
		// Creamos la conexion a base de datos
		DriverManager.registerDriver(new OracleDriver());
		Properties props = new Properties();
		props.put("user", CONNECTION_USR);
		props.put("password", CONNECTION_PWD);
		props.put("SetBigStringTryClob", "true");
		try (Connection conexion = DriverManager.getConnection(CONNECTION_URL, props);
				Statement stmt = conexion.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// Tratamos los registros obtenidos de base de datos
			while (rs.next()) {
				lista.add(rs.getString("IDPOLIZA"));
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		return lista;
	}

	/**
	 * Metodo para obtener el ultimo fichero xml de validacion de una poliza enviado
	 * a Agroseguro
	 * 
	 * @param idpoliza
	 *            Identificador de la poliza
	 * @return Cadena de texto con el xml del ultimo envio a Agroseguro
	 * @throws SQLException
	 */
	private static String getXmlEnvio(String idpoliza) throws SQLException {
		String stringClob = null;
		String sql = "SELECT * FROM TB_ENVIOS_AGROSEGURO " + "WHERE IDPOLIZA = " + idpoliza
				+ " AND TIPOENVIO = 'VL' ORDER BY FECHA_ENVIO DESC";
		DriverManager.registerDriver(new OracleDriver());
		Properties props = new Properties();
		props.put("user", CONNECTION_USR);
		props.put("password", CONNECTION_PWD);
		props.put("SetBigStringTryClob", "true");
		try (Connection conexion = DriverManager.getConnection(CONNECTION_URL, props);
				Statement stmt = conexion.createStatement();
				ResultSet rs = stmt.executeQuery(sql);) {
			// Solo busco en el primer resultado. Siempre habra al menos un resultado.
			if (rs.next()) {
				Clob clob = rs.getClob("XML");
				stringClob = WSUtils.convertClob2String(clob);
			}
			return stringClob;
		} catch (SQLException e) {
			logger.error("Error al obtener el xml de envio a Agroseguro: " + e.getMessage());
		}
		return null;
	}
}
