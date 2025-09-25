package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import org.hibernate.Session;

import java.sql.Clob;
import java.util.List;
import java.util.ResourceBundle;

public class TxtPrimaTotalDao extends BaseDaoHibernate implements ITxtPrimaTotalDao {

	@Override
	public String getTxtPrimaTotal(BigDecimal codPlan) {

		String primaTotal = ResourceBundle.getBundle("agp_sbp").getString("registro.primaTotal.no.encontrado");
		
		try {
			Session session = obtenerSession();
			String sql = "select nvl(texto,'" + primaTotal + "') from TB_SBP_TXT_PRIMA_TOTAL where codplan =  " + codPlan;
			List list = session.createSQLQuery(sql).list();
			if (list.size()>0)
				primaTotal = WSUtils.convertClob2String((Clob) list.get(0));
			
		} catch (Exception e) {
			logger.error("Error en getTxtPrimaTotal", e);
		}
		return primaTotal;
	}
}
