/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  16/07/2010  Ernesto Laura     Dao para consultas de Modulos 
* 											
*
**************************************************************************************************/
package com.rsi.agp.dao.models.cpl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.Modulo;

public class ModulosDao extends BaseDaoHibernate implements IModulosDao{
	
	private static final Log logger = LogFactory.getLog(ModulosDao.class);
	
	@Override
	public List<Modulo> getModulosPoliza(Long idPoliza, Long lineaseguroid) throws BusinessException {
		Session session = obtenerSession(); 
		List<Modulo> lista = null;
		
		try {
			String hql = "from Modulo as m where m.id.lineaseguroid = " + lineaseguroid + " and m.id.codmodulo in (" +
					"select mp.id.codmodulo from ModuloPoliza mp " +
					"where mp.id.idpoliza = " + idPoliza + " and mp.id.lineaseguroid = " + lineaseguroid + ")";
			Query q = session.createQuery(hql);
			
			lista = q.list();
		}
		catch(Exception e){
			logger.error("Error al obtener los módulos de la póliza " + idPoliza, e);
			throw new BusinessException ("Error al obtener los módulos de la póliza " + idPoliza, e);
		}
		
		return lista;
	}
	
	@Override
	public List<Modulo> getModuloAnexo(Long idAnexo, Long idPoliza, Long lineaseguroid) throws BusinessException {
		Session session = obtenerSession(); 
		List<Modulo> lista = null;
		
		try {
			//Buscamos el módulo en el anexo
			String hql = "from Modulo as m where m.id.lineaseguroid = " + lineaseguroid + " and m.id.codmodulo in (" +
					"select an.codmodulo from AnexoModificacion an " +
					"where an.id = " + idAnexo + ")";
			Query q = session.createQuery(hql);
			
			lista = q.list();
			
			//Si no hay elementos para el anexo, buscamos para la póliza asociada
			if (lista.size() == 0){
				hql = "from Modulo as m where m.id.lineaseguroid = " + lineaseguroid + " and m.id.codmodulo in (" +
				"select p.codmodulo from Poliza p " +
				"where p.idpoliza = " + idPoliza + " and p.linea.lineaseguroid = " + lineaseguroid + ")";
				
				q = session.createQuery(hql);
				
				lista = q.list();
			}
		}
		catch(Exception e){
			logger.error("Error al obtener el módulo del anexo " + idAnexo, e);
			throw new BusinessException ("Error al obtener el módulo del anexo " + idAnexo, e);
		}
		
		return lista;
	}

	@Override
	public Modulo getModulo(final Long lineaseguroid, final String codmodulo) {
		Modulo modulo = (Modulo) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(Modulo.class);
						criteria.add(Restrictions.eq ("id.lineaseguroid",lineaseguroid));
						criteria.add(Restrictions.eq ("id.codmodulo",codmodulo));
						
						List<Modulo> lista = criteria.list();
						
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return modulo;
	}
}
