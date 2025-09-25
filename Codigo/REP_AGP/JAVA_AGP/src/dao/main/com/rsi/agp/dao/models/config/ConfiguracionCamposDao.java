package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;

public class ConfiguracionCamposDao extends BaseDaoHibernate implements IConfiguracionCamposDao {
	
	/** DAA 04/09/2013
     * 	Devuelve una lista con los datos variables marcados segun lineaseguroid de la tabla ConfiguracionCampo
     */
    public List<ConfiguracionCampo> getDatosVariablesCargaParcelasMarcados(Long lineaseguroid, List<BigDecimal> lstCodConceptos, String grupoSeguro) throws DAOException {
    	logger.info("init - getDatosVariablesCargaParcelasMarcados");
    	List<ConfiguracionCampo> lstDatosVar = null;
    	Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ConfiguracionCampo.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("mostrarcargapac", 'S'));
			criteria.createAlias("pantallaConfigurable","pc");
			criteria.add(Restrictions.eq("pc.gruposeguro", grupoSeguro));
			criteria.add(Restrictions.eq("pc.obligatoria", 1));
			//criteria.add(Restrictions.eq("pantallaConfigurable.", value))
			if(lstCodConceptos != null && !lstCodConceptos.isEmpty()){
				criteria.add(Restrictions.not(Restrictions.in("id.codconcepto", lstCodConceptos)));
			}
			
			lstDatosVar = criteria.list();
			
		} catch(Exception ex){
			logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
    		throw new DAOException("[ERROR] al acceder a la BBDD.",ex);
    	}
		logger.info("end - getDatosVariablesCargaParcelasMarcados");
		return lstDatosVar;
	}
	
}
