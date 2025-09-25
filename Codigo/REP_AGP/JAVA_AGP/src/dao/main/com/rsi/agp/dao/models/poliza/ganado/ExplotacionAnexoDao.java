package com.rsi.agp.dao.models.poliza.ganado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.filter.gan.ExplotacionesAnexoFilter;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;

public class ExplotacionAnexoDao extends BaseDaoHibernate implements IExplotacionAnexoDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort,
			final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [ExplotacionAnexoDAO] getWithFilterAndSort");
			List<Serializable> explotacionesAnexo = (List<Serializable>) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							final ExplotacionesAnexoFilter expFilter = (ExplotacionesAnexoFilter) filter;
							expFilter.execute();
							Criteria criteria = session
									.createCriteria(ExplotacionAnexo.class);
							// Filtro
							criteria = expFilter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<ExplotacionAnexo> lista = criteria.list();
							HashMap<String, List<String>> grupoRazasCols;
							Set<GrupoRazaAnexo> grupoRazas;
							Long lineaseguroId;
							for (ExplotacionAnexo explotacionAnexo : lista) {
								lineaseguroId = explotacionAnexo.getAnexoModificacion().getPoliza()
										.getLinea().getLineaseguroid();
								grupoRazasCols = explotacionAnexo
										.getGrupoRazasCols();
								grupoRazas = explotacionAnexo.getGrupoRazaAnexos();
								for (GrupoRazaAnexo grupo : grupoRazas) {
									grupoRazasCols
											.get(Explotacion.GR_RAZA)
											.add(grupo.getCodgruporaza()
													+ "-"
													+ grupo.getNomgruporaza());
									grupoRazasCols
											.get(Explotacion.GR_TIPOCAPITAL)
											.add(grupo.getCodtipocapital()
													+ "-"
													+ grupo.getNomtipocapital());
									grupoRazasCols
											.get(Explotacion.GR_TIPOANIMAL)
											.add(grupo.getCodtipoanimal()
													+ "-"
													+ grupo.getNomtipoanimal());
									grupoRazasCols.get(Explotacion.GR_NUMERO)
											.add("" + grupo.getNumanimales());
									BigDecimal precioMax = new BigDecimal(0);
									for (PrecioAnimalesModuloAnexo precioAnimalModulo : grupo
											.getPrecioAnimalesModuloAnexos()) {
										precioMax = precioMax
												.max(precioAnimalModulo
														.getPrecio());
									}
									grupoRazasCols.get(Explotacion.GR_PRECIO)
											.add(NumberUtils.formatear(
													precioMax, 2));
								}
								explotacionAnexo.setGrupoRazasCols(grupoRazasCols);
							}
							return lista;
						}
					});
			logger.debug("end - [ExplotacionAnexoDao] getWithFilterAndSort");
			return explotacionesAnexo;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	public int getCountWithFilter(final CriteriaCommand filter) {
		logger.debug("init - [ExplotacionAnexoDao] getCountWithFilter");
		final ExplotacionesAnexoFilter expFilter = (ExplotacionesAnexoFilter) filter;
		expFilter.execute();
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(ExplotacionAnexo.class);
						criteria = expFilter.execute(criteria);
						return criteria.setProjection(Projections.rowCount())
								.uniqueResult();
					}
				});
		logger.debug("end - [ExplotacionAnexoDao] getCountWithFilter");
		return count.intValue();
	}
	
	public  List getExplotaciones(Long idPoliza, String tipoModificacion, Long idAnexo) throws BusinessException {
		
		 try {
			 logger.debug("init - [ExplotacionAnexoDao] getExplotaciones");		 
			 Session session = obtenerSession();
			
			 String consulta=null;
			 consulta = "Select  id, idpoliza, TIPO_MODIFICACION, NUMERO, Provincia,Comarca," + 
			 		"Termino,rega, sigla, subexplotacion, latitud, longitud," + 
			 		"especie, regimen, GrupoRaza, TipoCapital," + 
			 		"TipoAnimal,grupoListado," + 
			 		"numanimales,precio," + 
			 		"TasaComercial,CosteTomador, idgruporaza, ID_ANEXO, lineaSeguroId from vw_inf_modexplotaciones_SD WHERE IDPOLIZA =" + 
						idPoliza + " AND TIPO_MODIFICACION = 'M' AND ID_ANEXO=" + idAnexo +" order by ID";
				List anexosMod = session.createSQLQuery(consulta).list();
			
   			 logger.debug("end - [ExplotacionAnexoDao] getExplotaciones");	
   			 return anexosMod;	    
   							
	        }catch (Exception e) {
	        	logger.debug ("getExplotaciones - Se ha producido un error buscando los anexos de modificaci�n de la p�liza " + idPoliza , e);
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
		
	}
	
	public List getDatosVariables(BigDecimal idExplotacion, BigDecimal idGrupoRaza) throws BusinessException {		
		List datosVariables =null;
		 try {
			 logger.debug("init - [ExplotacionAnexoDao] getDatosVariables");		 
			 Session session = obtenerSession();
			
			 String consulta=null;
			 consulta = "Select * from vw_inf_modexplotaciones_variab where idgruporaza = " + 
					 idGrupoRaza + "and idexplotacion= " + idExplotacion;
			 datosVariables	= session.createSQLQuery(consulta).list();
			
   			 logger.debug("end - [ExplotacionAnexoDao] getDatosVariables");	
   			 return datosVariables;	    
   							
	        }catch (Exception e) {
	        	logger.debug ("getDatosVariables - Se ha producido un error buscando los datos variables del anexo de modificación " , e);
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
		
	}
	
	public List getDatosVariables(BigDecimal idExplotacionAnexo) throws BusinessException {		
		List datosVariables =null;
		 try {
			 logger.debug("init - [ExplotacionAnexoDao] getDatosVariables");		 
			 Session session = obtenerSession();
			
			 String consulta=null;
			 consulta = "Select * from vw_inf_modexplotaciones_variab where idexplotacion= " + idExplotacionAnexo;
			 datosVariables	= session.createSQLQuery(consulta).list();
			
   			 logger.debug("end - [ExplotacionAnexoDao] getDatosVariables");	
   			 return datosVariables;	    
   							
	        }catch (Exception e) {
	        	logger.debug ("getDatosVariables - Se ha producido un error buscando los datos variables del anexo de modificación " , e);
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
		
	}
	
	
	public List<Long> obtenerIdsExplotacionesAnexoConVariosGruposRaza(final Long idAnexo) throws DAOException{
		
		Session session = obtenerSession();
		List<Long> listaIdsExplotacionAnexo = null;
		
		try {
			
			Query query = session.createSQLQuery("select exp.id id " +
												"from tb_anexo_mod_explotaciones exp, tb_anexo_mod_grupo_raza gr " +
												"where gr.id_explotacion_anexo = exp.id and exp.id_anexo = " + idAnexo +
												"group by exp.id " +
												"having count(*)>1").addScalar("id", Hibernate.LONG);

			listaIdsExplotacionAnexo = query.list();
		
		} catch (Exception e) {
			logger.error("Se ha producido un error durante el acceso a la base de datos ", e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return listaIdsExplotacionAnexo;
	}
}