package com.rsi.agp.dao.models.poliza;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class UtilidadesXMLDAO extends BaseDaoHibernate implements IUtilidadesXMLDAO{
	
	private static final String TIPO_ENVIO_VALIDACION = "VL";
	private static final String TIPO_ENVIO_CALCULO = "CL";

	/**
	 * Obtiene el fichero xml de una poliza enviado a Agroseguro para una fila y un tipo de envio
	 * @param idPoliza Identificador de la poliza
	 * @param filaComparativa Identifica la fila a comparar
	 * @param tipoEnvio Identifica el tipo de envío: Validacion (VL), Calculo (CL)
	 * @throws DAOException
	 * @return Cadena de texto con el xml del envio a Agroseguro
	 */
	@SuppressWarnings("deprecation")
	private String getXML(String idPoliza, String filaComparativa, String tipoEnvio) throws DAOException {
		
		logger.debug("UtilidadesXMLDao - getXML [INIT]");
		
		Session session = obtenerSession();
		Statement stmt = null;
		ResultSet rs = null;
		String stringClob = null;
		String sql = null;
		
		if(filaComparativa != null) {
			sql = "SELECT * FROM TB_ENVIOS_AGROSEGURO" +
					" WHERE IDPOLIZA = " + idPoliza +
						" AND TIPOENVIO = '"+ tipoEnvio +
						"' AND FILACOMPARATIVA = "+ filaComparativa +
						" ORDER BY FECHA_ENVIO DESC";
		} else {
			sql = "SELECT * FROM TB_ENVIOS_AGROSEGURO" +
					" WHERE IDPOLIZA = " + idPoliza +
						" AND TIPOENVIO = '"+ tipoEnvio +
						"' ORDER BY FECHA_ENVIO DESC";
		}
		logger.debug("Valor de sql ejecutar:"+sql);
		
		try {
			
			stmt = session.connection().createStatement();
			rs = stmt.executeQuery(sql);
			
			if (rs.next()){
				Clob clob = rs.getClob("XML");
				stringClob = WSUtils.convertClob2String(clob);
			}
			
			rs.close();
			stmt.close();
			
			logger.debug("UtilidadesXMLDao - getXML Valor de stringClob:"+stringClob);
			logger.debug("UtilidadesXMLDao - getXML [END]");
			
			return stringClob;
			
		} catch (SQLException e) {
			throw new DAOException("Error al recuperar el campo respuesta del SW de la poliza: " + idPoliza
					+ " con tipoEnvio:" + tipoEnvio + " y filaComparativa: " + filaComparativa, e);
		}
	}
	
	@Override
	public String getXMLCalculo(String idPoliza, String filaComparativa) throws DAOException {
		return getXML(idPoliza, filaComparativa, TIPO_ENVIO_CALCULO);
	}

	@Override
	public String getXMLValidacion(String idPoliza, String filaComparativa) throws DAOException {
		return getXML(idPoliza, filaComparativa, TIPO_ENVIO_VALIDACION);
	}

}
