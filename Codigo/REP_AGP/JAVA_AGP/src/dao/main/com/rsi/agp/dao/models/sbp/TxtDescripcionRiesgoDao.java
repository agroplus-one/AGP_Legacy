package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;

import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class TxtDescripcionRiesgoDao extends BaseDaoHibernate implements ITxtDescripcionRiesgoDao {

	@Override
	public String getTxtDescRiesgo(BigDecimal codPlan) {
		
		String descRiesgo = ResourceBundle.getBundle("agp_sbp").getString("registro.descRiesgo.no.encontrado");
		
		try {
			Session session = obtenerSession();
			String sql = "select nvl(texto,'"+descRiesgo+"') from TB_SBP_TXT_DESC_RIESGO where codplan =  " + codPlan;
			List list = session.createSQLQuery(sql).list();
			if (list.size()>0)
				descRiesgo = WSUtils.convertClob2String((Clob) list.get(0));
			
		} catch (Exception e) {
			logger.error("Error en getTxtDescRiesgo", e);
		}
		return descRiesgo;
	}
}
