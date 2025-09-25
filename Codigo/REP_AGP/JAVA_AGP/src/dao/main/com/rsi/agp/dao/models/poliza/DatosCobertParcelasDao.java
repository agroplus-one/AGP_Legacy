/**********************************************/
/** CREATE: 29/09/2020, T-SYSTEMS            **/
/** Fuente nuevo para Coberturas de Parcelas **/
/** PETICION: 63485 - FASE II                **/
/**********************************************/

package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class DatosCobertParcelasDao extends BaseDaoHibernate implements IDatosCobertParcelasDao {

	@SuppressWarnings("deprecation")
	public String getCobParcelas(String codModulo, Long idPoliza, Integer numParc, Integer hojaParc)
			throws DAOException {

		String xml = null;
		Session session = obtenerSession();

		try {

			Clob respuesta = null;
			String sql = "select mod.respuesta from o02agpe0.tb_sw_modulos_coberturas_parc mod"
					+ " WHERE mod.idpoliza = " + idPoliza + " AND mod.idparcela in (select parc.idparcela "
					+ " from o02agpe0.tb_parcelas parc " + " where parc.idpoliza = " + idPoliza + " and parc.numero = "
					+ numParc + " and parc.hoja = " + hojaParc + ")" + " AND mod.codmodulo = '" + codModulo
					+ "' order by mod.fecha asc";

			logger.info(sql);

			Statement stmt = session.connection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (null != rs) {
				while (rs.next()) {
					respuesta = (Clob) rs.getClob(1);
					xml = WSUtils.convertClob2String(respuesta);
				}
				rs.close();
				stmt.close();

				return xml;
			}
			return xml;
		} catch (Exception e) {
			throw new DAOException("Error al recuperar el campo respuesta del SW de la poliza: " + idPoliza
					+ " y el modulo:" + codModulo, e);
		}

	}

	@SuppressWarnings("deprecation")
	public String getCobParcelasAnexo(String codModulo, Long idAnexo, Long idParcAnexo, Integer numParc,
			Integer hojaParc) throws DAOException {

		logger.debug("DatosCobertParcelasDao - getCobParcelasAnexo [INIT]");

		String xml = null;
		Session session = obtenerSession();

		try {

			Clob respuesta = null;
			String sql = "select pAnex.respuesta from o02agpe0.tb_sw_modulos_cob_parc_anexo pAnex"
					+ " WHERE pAnex.Idanexo = " + idAnexo + " AND pAnex.Id_Parcela_Anexo  = " + idParcAnexo
					+ " AND pAnex.codmodulo = '" + codModulo + "'" + " order by pAnex.fecha asc";

			logger.info(sql);

			Statement stmt = session.connection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (null != rs) {
				while (rs.next()) {
					respuesta = (Clob) rs.getClob(1);
					xml = WSUtils.convertClob2String(respuesta);
				}
				rs.close();
				stmt.close();

				return xml;
			}
			return xml;
		} catch (Exception e) {
			throw new DAOException("Error al recuperar el campo respuesta del SW de la poliza: " + idAnexo
					+ " y el modulo:" + codModulo, e);
		}

	}

	@SuppressWarnings("rawtypes")
	public Boolean obtenerParcelaElegida(BigDecimal codConcepto, Long idParcela,
			String codModulo, BigDecimal codRiesgoCub) throws DAOException {

		Session session = obtenerSession();
		Boolean elegida = false;

		try {

			String sql = "select c.*" + " from o02agpe0.tb_parcelas_coberturas c" + " where c.idparcela = " + idParcela
					+ " and c.codmodulo ='" + codModulo + "'" + " and c.codconceptoppalmod = " + codConcepto
					+ " and c.codriesgocubierto = " + codRiesgoCub
					+ " and c.codvalor = -1"; /* No recuperamos las que no se hayan elegido */

			logger.info(sql);

			logger.debug(sql);
			List resultado = session.createSQLQuery(sql).list();
			if (null != resultado && resultado.size() > 0) {
				elegida = true;
			} else {
				elegida = false;
			}
		} catch (Exception e) {
			throw new DAOException("Error al recuperar el valor de la Parcela: " + idParcela, e);
		}

		return elegida;
	}

	@SuppressWarnings("rawtypes")
	public Boolean obtenerParcelaElegidaAnexo(BigDecimal codConcepto, Long idParcelaAnx,
			String codModulo, BigDecimal codRiesgoCub) throws DAOException {

		Session session = obtenerSession();
		Boolean elegida = false;

		try {

			String sql = "select dtsAnx.* from o02agpe0.tb_anexo_mod_capitales_dts_vbl dtsAnx"
					+ " where dtsAnx.Idcapitalasegurado in (select Cap.Id "
					+ " from o02agpe0.tb_anexo_mod_capitales_aseg Cap " + " where Cap.Idparcelaanexo = " + idParcelaAnx
					+ ")" + " and dtsAnx.codconceptoppalmod = " + codConcepto + " and dtsAnx.Codriesgocubierto = "
					+ codRiesgoCub + " and dtsAnx.Valor ='-1'";

			logger.debug(sql);
			List resultado = session.createSQLQuery(sql).list();
			if (null != resultado && resultado.size() > 0) {
				elegida = true;
			} else {
				elegida = false;
			}
		} catch (Exception e) {
			throw new DAOException("Error al recuperar el valor de la Parcela: " + idParcelaAnx, e);
		}

		return elegida;
	}

	public String obtenerParcValorElegido(BigDecimal codConcepto, Long idParcela, String codModulo)
			throws DAOException {

		String valor = "";

		Session session = obtenerSession();

		try {

			String sql = "select c.codvalor" + " from o02agpe0.tb_parcelas_coberturas c" + " where c.idparcela = "
					+ idParcela + " and c.codmodulo = '" + codModulo + "' and c.codconcepto = " + codConcepto;

			logger.debug(sql);

			@SuppressWarnings("rawtypes")
			List resultado = session.createSQLQuery(sql).list();
			if (null != resultado && resultado.size() > 0) {
				BigDecimal vAux = (BigDecimal) resultado.get(0);
				valor = vAux.toString();
			}
		} catch (Exception e) {
			throw new DAOException("Error al recuperar el valor de la cobertura de la parcela " + idParcela
					+ " y el concepto " + codConcepto, e);
		}

		return valor;
	}
	
	public String obtenerParcValorElegidoAnexo(BigDecimal codConcepto, Long idParcela) throws DAOException {
		 
		String valor = "";
		 
		 Session session = obtenerSession();
			
			try {
				
				String sql = "select cd.valor" + 
						" from o02agpe0.tb_anexo_mod_capitales_dts_vbl cd" + 
						" inner join o02agpe0.tb_anexo_mod_capitales_aseg ca on cd.idcapitalasegurado = ca.id " + 
						" inner join o02agpe0.tb_anexo_mod_parcelas pa on ca.idparcelaanexo = pa.id and pa.id = " + idParcela + 
						" where cd.codconcepto = " + codConcepto;
				
				logger.debug(sql);
				
				@SuppressWarnings("rawtypes")
				List resultado = session.createSQLQuery(sql).list();
				if(null!=resultado && resultado.size()>0){
					valor = (String) resultado.get(0);
				}
			}
			catch (Exception e) {
				throw new DAOException("Error al recuperar el valor de la cobertura de la parcela de anexo " 
						+ idParcela + " y el concepto " + codConcepto, e);
			}
		 
		 return valor;
	 }
}