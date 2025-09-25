package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import org.hibernate.Session;

import java.sql.Clob;
import java.util.List;
import java.util.ResourceBundle;

public class TxtPeriodoCarenciaDao extends BaseDaoHibernate implements ITxtPeriodoCarenciaDao {

	@Override
	public String getTxtPeriodoCarencia(BigDecimal codPlan) {

		String periodoCarencia = ResourceBundle.getBundle("agp_sbp").getString("registro.periodoCarencia.no.encontrado");
		
		try {
			Session session = obtenerSession();
			String sql = "select nvl(texto,'"+periodoCarencia+"') from TB_SBP_TXT_PERIODO_CARENCIA where codplan =  " + codPlan;
			List list = session.createSQLQuery(sql).list();
			if (list.size()>0)
				periodoCarencia = WSUtils.convertClob2String((Clob) list.get(0));
			
		} catch (Exception e) {
			logger.error("Error en getTxtPeriodoCarencia", e);
		}
		return periodoCarencia;
	}
}
