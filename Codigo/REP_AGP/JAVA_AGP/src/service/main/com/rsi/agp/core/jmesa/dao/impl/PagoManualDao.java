
package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IPagoManualDao;
import com.rsi.agp.core.jmesa.filter.PagoManualFilter;
import com.rsi.agp.core.jmesa.sort.PagoManualSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Zona;


/**
 * @author U029769
 *
 */
public class PagoManualDao extends BaseDaoHibernate implements IPagoManualDao {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<Oficina> getOficinasPagoManualWithFilterAndSort(
			final PagoManualFilter filter, final PagoManualSort sort, final int rowStart,
			final int rowEnd, final String codZona) throws BusinessException {
		try {
			logger.debug("init - [PagoManualDao] getOficinasPagoManualWithFilterAndSort");
			List<Oficina> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(Oficina.class);
							
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							if (rowStart != -1 && rowEnd != -1) {
						        // Primer registro
						        criteria.setFirstResult(rowStart);
						        // Número máximo de registros a mostrar
						        criteria.setMaxResults(rowEnd - rowStart);
						    }
							
							List<Oficina> lista = criteria.list();
							for (Oficina of : lista) {
								of.setCodZona(codZona);
							}
							return lista;
						}
					});
			logger.debug("end - [PagoManualDao] getOficinasPagoManualWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/* Pet. 63701 ** MODIF TAM (22/06/2021) * Inicio */
	@SuppressWarnings("unchecked")
	@Override
	public List<Zona> obtenerListaZonas(BigDecimal codentidad) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Zona.class);
		
		if (codentidad != null) {
			criteria.add(Restrictions.eq("id.codentidad", codentidad));
		}
			
		return (List<Zona>) criteria.list();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> obtenerListaNombZonasOficina(BigDecimal codentidad, BigDecimal codoficina) throws DAOException {
		logger.debug("init - [PagoManualDao] obtenerListaZonasOficina");
		Session session = obtenerSession();
		
		String sqlSel = "select zon.nomzona from o02agpe0.tb_zonas_entidad zon "  
				        + "  inner join o02agpe0.tb_oficinas_zonas ofZon on ofZon.Codentidad = zon.codentidad and ofZon.Codzona = zon.codzona "  
				        + " where ofZon.Codentidad = " + codentidad + "  and ofZon.Codoficina = " + codoficina;
		
		logger.debug("Valor de sqlSel:"+sqlSel);
		
		
		List<String> listZonas = session.createSQLQuery(sqlSel).list();
		
		logger.debug("end - [PagoManualDao] obtenerListaZonasOficina");
		return listZonas;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public List<Zona> obtenerListaZonasOficina(BigDecimal codentidad, BigDecimal codoficina) throws DAOException {
		logger.debug("init - [PagoManualDao] obtenerListaZonasOficina");
		Session session = obtenerSession();
		
		String sqlSel = "select zon.* from o02agpe0.tb_zonas_entidad zon "  
				        + "  inner join o02agpe0.tb_oficinas_zonas ofZon on ofZon.Codentidad = zon.codentidad and ofZon.Codzona = zon.codzona "  
				        + " where ofZon.Codentidad = " + codentidad + "  and ofZon.Codoficina = " + codoficina;
		
		logger.debug("Valor de sqlSel:"+sqlSel);
		
		List list = session.createSQLQuery(sqlSel).list();
		List<Zona> listZonas = new ArrayList<Zona>();
		if (list.size()>0) {
			for(int i=0;i<list.size();i++){
				Object[] registro = (Object[]) list.get(i);
				Zona zon = new Zona();
				
				BigDecimal codEntidad = (BigDecimal) registro[0];
				BigDecimal codZona = (BigDecimal) registro[1];
				String nombZona = (String) registro[2];
				
				zon.getId().setCodentidad(codEntidad);
				zon.getId().setCodzona(codZona);
				zon.setNomzona(nombZona);
				listZonas.add(zon);
			}
			
		}
		
		logger.debug("end - [PagoManualDao] obtenerListaZonasOficina");
		return listZonas;
	}

	
	public void guardarZonasOficina(Oficina oficinaBean, List<Zona> zonaListSel) throws DAOException {
		logger.debug("init - [PagoManualDao] guardarZonasOficina");
		
		String sql = null;
		Session session = obtenerSession();
		
		try {
			for (int i = 0; i < zonaListSel.size(); i++){
				// Compone el insert
				Zona zon = zonaListSel.get(i);
				sql = "INSERT INTO o02agpe0.TB_OFICINAS_ZONAS VALUES (" + zon.getId().getCodentidad() +", "+ oficinaBean.getId().getCodoficina() + "," + zon.getId().getCodzona() +")"; 
				
				//Insertamos las zonas asignadas a la oficina
				session.createSQLQuery(sql).executeUpdate();
			}
			
			
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al dar de alta las zonas de la oficina", ex);
		}
		logger.debug("Fin de guardarZonasOficina");
		
	}
	
	public void borrarZonasOficinas(Oficina oficinaBean) throws DAOException {
		logger.debug("init - [PagoManualDao] borrarZonasOficinas");
		
		String sqlDelete = null;
		Session session = obtenerSession();
		
		BigDecimal entidad = oficinaBean.getId().getCodentidad();
		BigDecimal oficina = oficinaBean.getId().getCodoficina();
		
		try {
			sqlDelete = "delete from o02agpe0.tb_oficinas_zonas ofZon " + 
					    " where ofZon.Codentidad = "+entidad + " and ofZon.Codoficina = " +oficina;
			 
			//Insertamos las zonas asignadas a la oficina
			session.createSQLQuery(sqlDelete).executeUpdate();
			
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al dar de alta las zonas de la oficina", ex);
		}
		logger.debug("Fin de guardarZonasOficina");
		
	}
	
	public void editaZonasOficina(Oficina oficinaBean, List<Zona>zonaListSel) throws DAOException {
		logger.debug("init - [PagoManualDao] editaZonasOficina");
		
		try {
			//1º Borramos las zonas que tenga la oficina
			borrarZonasOficinas(oficinaBean);
			
			//2º Insertamos las zonas seleccionadas en la ventana
			/* P0063701 ** MODIF TAM (26.08.2021) ** Defecto 9 * Inicio */
			if (zonaListSel != null) {
				guardarZonasOficina(oficinaBean, zonaListSel);
			}
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al modificar las zonas de la oficina", ex);
		}
		logger.debug("end - [PagoManualDao] editaZonasOficina");
		
	}
	
	
	@Override
	public void adiccionMasivaZonas(String listaIds,List<Zona> zonaListSel) throws DAOException {
		
		logger.debug("init - [PagoManualDao] adiccionMasivaZonas");
		Session session = obtenerSession();
		
		try {
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			int longitud =ids.length;
				
			for (int i=0; i<longitud;i++){
				String[] idOfi = ids[i].split("_");
					
				BigDecimal codentidad = new BigDecimal(idOfi[0]);
				BigDecimal codoficina = new BigDecimal(idOfi[1]);
				
				//1º Buscamos si las zonas existen para esa oficina
				for (int j = 0; j < zonaListSel.size(); j++){
					// Compone el insert
					Zona zon = zonaListSel.get(j);
					
					String sql = "SELECT COUNT(*) "+ 
								 " FROM o02agpe0.TB_OFICINAS_ZONAS ZON " +
								 " WHERE ZON.CODENTIDAD = "+codentidad + " AND ZON.CODOFICINA = "+codoficina + " AND ZON.CODZONA =" + zon.getId().getCodzona(); 
					
					int num = ( (BigDecimal)session.createSQLQuery(sql).list().get(0) ).intValue();
					
					/* 2º: Si no encuentra la zona para esa oficina y Entidad, se da de alta*/
					if (num <= 0) {
						
						sql = "INSERT INTO o02agpe0.TB_OFICINAS_ZONAS VALUES (" + codentidad+", "+ codoficina + "," + zon.getId().getCodzona() +")"; 
						
						//Insertamos las zonas asignadas a la oficina
						session.createSQLQuery(sql).executeUpdate();
					}
					
				}
					
			}
				
				
		}catch(Exception d) {
			logger.error("error al hacer la Adicción Masiva de zonas",d);
			throw new DAOException();
		}
			
	}
	/* Pet. 63701 ** MODIF TAM (22/06/2021) * Fin */

	@Override
	public int getOficinasPagoManualCountWithFilter(final PagoManualFilter filter) {
		logger.debug("init - [PagoManualDao] getOficinasPagoManualCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(Oficina.class);
						// Filtro
						criteria = filter.execute(criteria);
						
						return criteria.setProjection(Projections.rowCount()).uniqueResult();
					}
				});
		logger.debug("end - [PagoManualDao] getOficinasPagoManualCountWithFilter");
		return count.intValue();
		
	}

	/** Metodo para recuperar un String con  entidad_oficina de oficinas segun el filtro
	 * 
	 * @param PagoManualFilter
	 * @return listaids
	 */
	public String getlistaIdsTodos(PagoManualFilter consultaFilter) throws DAOException{
		try {
			
			StringBuilder listaids = null;
			Session session = obtenerSession();
			
			/* P0063701 ** MODIF TAM (26.08.2021) ** Defecto 11 ** MODIF TAM */
			String sql = "select CONCAT(o.codentidad||'_',o.codoficina) from tb_oficinas o "
					+ consultaFilter.getSqlWhere();
			@SuppressWarnings("rawtypes")
			List lista = session.createSQLQuery(sql).list();
			
			listaids = new StringBuilder(lista.size());
			for(int i=0;i<lista.size();i++){
				listaids.append(lista.get(i)).append(",");
				
			}
			return listaids.toString();
		}catch(Exception d) {
			logger.error("error al obtener la clave de la tabla oficinas para el cambio masivo",d);
			throw new DAOException();
		}
	}

	@Override
	public void cambioMasivo(String listaIds,Oficina oficinaBean) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		Session session = obtenerSession();
		try {
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			int longitud =ids.length;
			
			sql.append("update tb_oficinas set pago_manual = ").append(oficinaBean.getPagoManual()).append(" where ");
			for (int i=0; i<longitud;i++){
				String[] idOfi = ids[i].split("_");
				
				sql.append(" ( codentidad= ").append(new BigDecimal(idOfi[0])).
					   append(" and codoficina = ").append(new BigDecimal(idOfi[1])).append(")");
				
				if (i != longitud-1) // si es la ultima vez que  entro al for ya no lo anado
					sql.append(" or ");
			}
			
			session.createSQLQuery(sql.toString()).executeUpdate();
			
			
		}catch(Exception d) {
			logger.error("error al hacer el cambio masivo",d);
			throw new DAOException();
		}
		
	}
	
	@Override
	public void cambioMasivoZonas(String listaIds, List<Zona> zonaListSel) throws DAOException {
		
		try {
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(",");
			int longitud =ids.length;
			

			for (int i=0; i<longitud;i++){
				String[] idOfi = ids[i].split("_");
				
				Oficina oficinaBean = new Oficina();

				oficinaBean.getId().setCodentidad(new BigDecimal(idOfi[0]));
				oficinaBean.getId().setCodoficina(new BigDecimal(idOfi[1]));
				
				/* 1º : Borramos las zonas que tenga asignadas la oficina de esa entidad */
				borrarZonasOficinas(oficinaBean);
				
				//2º Insertamos las zonas seleccionadas en la ventana
				guardarZonasOficina(oficinaBean, zonaListSel);
			}
			
			
		}catch(Exception d) {
			logger.error("error al hacer el cambio masivo de zonas",d);
			throw new DAOException();
		}
		
	}

	

}
