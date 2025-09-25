package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import org.hibernate.Session;

import java.sql.Clob;
import java.util.List;
import java.util.ResourceBundle;

public class TxtRiesgoGarantizadoDao extends BaseDaoHibernate implements ITxtRiesgoGarantizadoDao {

	@Override
	public String getTxtRiesgoGarantizado(BigDecimal codPlan) {

		String riesgoGarantizado = ResourceBundle.getBundle("agp_sbp").getString("registro.riesgoGarantizado.no.encontrado");
		
		try {
			Session session = obtenerSession();
			String sql = "select nvl(texto,'"+riesgoGarantizado+"') from TB_SBP_TXT_RIESGO_GARANTIZADO where codplan =  " + codPlan;
			List list = session.createSQLQuery(sql).list();
			if (list.size()>0)
				riesgoGarantizado = WSUtils.convertClob2String((Clob) list.get(0));
			
		} catch (Exception e) {
			logger.error("Error en getTxtRiesgoGarantizado", e);
		}
		return riesgoGarantizado;
	}
}
