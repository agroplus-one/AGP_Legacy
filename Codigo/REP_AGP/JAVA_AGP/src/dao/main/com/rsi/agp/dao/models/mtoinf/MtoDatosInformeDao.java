package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DatosInformeFilter;
import com.rsi.agp.core.jmesa.sort.DatosInformeSort;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.InformeUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.Informe;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfCondiciones;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfDatosInformes;

public class MtoDatosInformeDao extends BaseDaoHibernate implements IMtoDatosInformeDao {


	/**
	 * Devuelve el numero de elemento de datos informe segun el informe id 
	 * @param filter Filtro para la búsqueda de datos del informe
	 * @param informeId id del informe
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public int getDatosInformeCountWithFilter(final DatosInformeFilter filter, final BigDecimal informeId)
			throws DAOException {
		logger.debug("init - [MtoDatosInformeDao] getDatosInformeCountWithFilter");
		try {
			Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Criteria criteria = session.createCriteria(VistaMtoinfDatosInformes.class);
					if (informeId != null) {
						criteria.add(Restrictions.eq("idinforme", informeId));
					}
					if (filter != null) {
						criteria = filter.execute(criteria);
					}

					return ((Integer) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
				}
			});
			logger.debug("end - [MtoDatosInformeDao] getDatosInformeCountWithFilter");
			return count.intValue();
		} catch (Exception e) {
			logger.error("Error: getDatosInformeCountWithFilter : " + e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	
	/**
	 * Devuelve el listado de datos del informe ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de datos del informe
	 * @param informeId id del informe
	 * @param sort Ordenación para la búsqueda datos del informe
	 * @param rowStart Primer registro que se datos del informe
	 * @param rowEnd Último registro que se datos del informe
	 * @return
	 * @throws DAOException
	 */

	@Override
	@SuppressWarnings("all")
	public Collection<VistaMtoinfDatosInformes> getDatosInformeWithFilterAndSort(
			final DatosInformeFilter filter,final DatosInformeSort sort,final BigDecimal informeId,  final int rowStart,
			final int rowEnd) throws  DAOException  {
	
		logger
		.debug("init - [MtoDatosInformeDao] getDatosInformeWithFilterAndSort");
		try {
			List<VistaMtoinfDatosInformes> applications = (List) getHibernateTemplate()
		.execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session
						.createCriteria(VistaMtoinfDatosInformes.class);
				// Alias
				
				if(informeId != null){
					criteria.add(Restrictions.eq("idinforme", informeId));
				}
				// Filtro
				criteria = filter.execute(criteria);
				// Ordenación
				criteria = sort.execute(criteria);
				// Primer registro
				criteria.setFirstResult(rowStart);
				// Número máximo de registros a mostrar
				criteria.setMaxResults(rowEnd - rowStart);
				//DAA 06/06/2012 Devuelve el listado de pólizas
				List<VistaMtoinfCondiciones> lista = criteria.list();
				
				return lista;
				//return criteria.list();
			}
		});
			logger.debug("end - [MtoDatosInformeDao] getDatosInformeWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			logger.error("Error: getDatosInformeWithFilterAndSort : " + e);	
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
}
	
	/**
	 * Verifica si existe el datos informe para el informe
	 * @param VistaMtoinfDatosInformes vista de datos de informe
	  * @return
	 * @throws DAOException
	 */	
	
public boolean existeDatosInforme(final VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws  DAOException  {
	boolean exist = false;	
	logger
	.debug("init - [MtoDatosInformeDao] existeDatosInforme");
	try {			
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							
							Criteria criteria = session.createCriteria(DatoInformes.class);
							
							// Añade al filtro el id del campo permitido o calculado
							if(vistaMtoinfDatosInformes.getId().getPermitidocalculado().intValue() == 1)
								criteria.add(Restrictions.eq("camposCalculados.id", vistaMtoinfDatosInformes.getIdcampo().longValue()));
							else if(vistaMtoinfDatosInformes.getId().getPermitidocalculado().intValue() == 2)
								criteria.add(Restrictions.eq("camposPermitidos.id", vistaMtoinfDatosInformes.getIdcampo().longValue()));
							
							// Añade al filtro el id del informe con el que se está trabajando
							criteria.add(Restrictions.eq("informe.id", vistaMtoinfDatosInformes.getIdinforme().longValue()));
							
							if (vistaMtoinfDatosInformes.getId() != null && vistaMtoinfDatosInformes.getId().getId() != null) {
								// Añade al filtro el id del dato que se está modificando para que la validación no lo detecte a él
								criteria.add(Restrictions.ne("id", vistaMtoinfDatosInformes.getId().getId().longValue()));
							}
							
							return ((Integer) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
					}
					});
				
		
			if (count > 0) 	exist	= true;
			
			logger.debug("end - [MtoDatosInformeDao] existeDatosInforme");			
		} catch (Exception e) {			
			logger.error("Error: existeDatosInforme : " + e);	
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
		}
	
		return exist;	
	}


	/**
	 * recupera el numero del orden para el dato del informe  
	 * @param vistaMtoinfDatosInformes 
	 * @throws DAOException
	 */
	@Override
	@SuppressWarnings("all")
	public int getOrden(VistaMtoinfDatosInformes vistaMtoinfDatosInformes)
			throws  DAOException {
		int orden = 1;
		int count = 0;
		
		try{
			logger.debug("init - [MtoDatosInformeDao] getOrden");	
		Session session = obtenerSession();
		String queryMax = "SELECT count(*) FROM TB_MTOINF_DATOS_INFORMES where idinforme = " + vistaMtoinfDatosInformes.getIdinforme() +"";
		count = ((BigDecimal)session.createSQLQuery(queryMax).uniqueResult()).intValue();
			if(count > 0){
				String queryOrden = "SELECT MAX(orden)+ 1 FROM TB_MTOINF_DATOS_INFORMES where idinforme = " + vistaMtoinfDatosInformes.getIdinforme() +"";
				orden= ((BigDecimal)session.createSQLQuery(queryOrden).uniqueResult()).intValue();
			}		
				
			logger.debug("end - [MtoDatosInformeDao] getOrden");
			
			}catch (Exception e) {
				logger.error("Error: getOrden : " + e);
				throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			}
			return orden;
		}
	
	
	
	
	/**
	 * devuelve una lista de datos del informe  
	 * @param informeId 
	 * @throws DAOException
	 */
	@Override
	public List<DatoInformes> getListaDatoInformes(Long informeId) throws DAOException {
		
		logger.debug("init - [MtoDatosInformeDao] getListaDatoInformes");
		List<DatoInformes> listaDatoInformes = new ArrayList<DatoInformes>(); 
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(DatoInformes.class);
			criteria.add(Restrictions.eq("informe.id", informeId));
			listaDatoInformes = criteria.list();
			logger.debug("end - [MtoDatosInformeDao] getListaDatoInformes");
			
		} catch (Exception e) {	
				logger.error("Error: getListaDatoInformes : " + e);
				throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
			}
			
			return listaDatoInformes;
	}
	
	
	
	/**
	 * devuelve una lista de campos de informe 
	 * @param informeId 
	 * @throws DAOException
	 */

	public List<CampoInforme> getListaCampos(final BigDecimal informeId) throws DAOException {		
			
			logger.debug("init - [MtoDatosInformeDao] getListaCampos");
			List<CampoInforme> listaCampoInforme = new ArrayList<CampoInforme>();
			Informe informe = null;
					try {
						
						informe = (Informe) getHibernateTemplate()
						.execute(new HibernateCallback() {

							public Object doInHibernate(Session session)
									throws HibernateException, SQLException {
								Criteria criteria = session
										.createCriteria(Informe.class);
								criteria.add(Restrictions.eq("id", informeId.longValue()));
								Informe informeRec = (Informe)criteria.uniqueResult();	
								return informeRec;
								}
						});
					
						for(DatoInformes datoInformes :informe.getDatoInformeses() ){
							
							if(datoInformes.getCamposCalculados() != null){
								
								CampoInforme campoInforme = new CampoInforme(); 
								campoInforme.setId(datoInformes.getCamposCalculados().getId());
								campoInforme.setDatoInformeId(datoInformes.getId());
								campoInforme.setNombre(datoInformes.getCamposCalculados().getNombre());
								campoInforme.setTipo(new BigDecimal(Integer.toString(ConstantsInf.CAMPO_TIPO_NUMERICO)));
								campoInforme.setNombreVista("Campo Calculado");
								if(datoInformes.getFormato() != null){
									campoInforme.setFormato(InformeUtils.getValueFormato(datoInformes.getFormato().toString()));
								}
								campoInforme.setPermitidoOCalculado(new BigDecimal(1));
								// Si es un campo calculado no tiene ningún origen de datos asociado
								campoInforme.setOrigen_datos(new BigDecimal (0));
								listaCampoInforme.add(campoInforme);
							}
							
							if(datoInformes.getCamposPermitidos() != null){
							
								CampoInforme campoInforme = new CampoInforme(); 
								campoInforme.setId(datoInformes.getCamposPermitidos().getId());
								campoInforme.setDatoInformeId(datoInformes.getId());
								campoInforme.setNombre(datoInformes.getAbreviado()); 
								campoInforme.setTipo(datoInformes.getCamposPermitidos().getVistaCampo().getVistaCampoTipo().getIdtipo());
								campoInforme.setNombreVista(datoInformes.getCamposPermitidos().getVistaCampo().getVista().getNombre());
								if(datoInformes.getFormato() != null){
									campoInforme.setFormato(InformeUtils.getValueFormato(datoInformes.getFormato().toString()));
								}
								campoInforme.setOrigen_datos(datoInformes.getCamposPermitidos().getVistaCampo().getOrigen_datos());
								campoInforme.setPermitidoOCalculado(new BigDecimal(2));
								listaCampoInforme.add(campoInforme);
							
							}
					
						}
					}
						catch(Exception ex){
							logger.error("Error: getListaCampos : " + ex);
							throw new DAOException("Se ha producido un error", ex);
							 
						}
					Collections.sort(listaCampoInforme);  
					logger.debug("init - [MtoDatosInformeDao] getListaCampos");
					return listaCampoInforme;
					
		}
	
	
	}
