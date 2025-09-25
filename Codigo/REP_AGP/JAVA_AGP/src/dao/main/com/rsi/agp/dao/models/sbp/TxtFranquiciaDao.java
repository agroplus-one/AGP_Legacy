package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import org.hibernate.Session;

import java.sql.Clob;
import java.util.List;
import java.util.ResourceBundle;

public class TxtFranquiciaDao extends BaseDaoHibernate implements ITxtFranquiciaDao {

	@Override
	public String getTxtFranquicia(BigDecimal codPlan) {

		String franquicia = ResourceBundle.getBundle("agp_sbp").getString("registro.franquicia.no.encontrado");
		
		try {
			Session session = obtenerSession();
			String sql = "select nvl(texto,'"+franquicia+"') from TB_SBP_TXT_FRANQUICIA where codplan =  " + codPlan;
			List list = session.createSQLQuery(sql).list();
			if (list.size()>0)
				franquicia = WSUtils.convertClob2String((Clob) list.get(0));
			
		} catch (Exception e) {
			logger.error("Error en getTxtFranquicia", e);
		}
		return franquicia;
	}
}
