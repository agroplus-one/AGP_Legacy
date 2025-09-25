package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoComisionesRenovDao;
import com.rsi.agp.core.jmesa.filter.MtoComisionesRenovFilter;
import com.rsi.agp.core.jmesa.sort.MtoComisionesRenovSort;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.commons.ComisionesRenov;
import com.rsi.agp.dao.tables.poliza.Linea;

/**
 * @author   U028975 (Tatiana, T-Systems)
 * Peticion: 57624 (Mantenimiento de Comisioens en Renovables por E-S Mediadora)
 * Fecha:    (Enero/Febrero.2019)
 */
public class MtoComisionesRenovDao extends BaseDaoHibernate implements IMtoComisionesRenovDao {

	private static final String QUERY_ID_COMIS_RENOV = "SELECT MAX (C.ID) FROM o02agpe0.TB_COMS_RENOV_ESMED C";
	  
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<ComisionesRenov> getComisRenovWithFilterAndSort(final MtoComisionesRenovFilter filter,
																	  final MtoComisionesRenovSort sort, 
																	  final int rowStart,
																	  final int rowEnd) throws BusinessException{

		try {
			logger.debug("init - [MtoComisionesRenovDao] getComisRenovWithFilterAndSort");
			@SuppressWarnings("rawtypes")
			List<ComisionesRenov> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(ComisionesRenov.class);
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<ComisionesRenov> lista = criteria.list();
							return lista;
						}
					});
			logger.debug("end - [MtoComisionesRenovDao] getComisRenovWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		} 
	}

	@Override
	public int getComisionesRenovCountWithFilter(final MtoComisionesRenovFilter filter) {
		logger.debug("init - [MtoComisionesRenovDao] getComisionesRenovCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(ComisionesRenov.class);
						criteria = filter.execute(criteria);
						
						return criteria.setProjection(Projections.rowCount()).uniqueResult();
					}
				});
		logger.debug("end - [MtoComisionesRenovDao] getComisionesRenovCountWithFilter");
		return count.intValue();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ComisionesRenov> listTodasComisionesRenov(ComisionesRenov ComisionesRenov) throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(ComisionesRenov.class);
			
			criteria.addOrder(Order.asc("codplan"));		
			
			if(FiltroUtils.noEstaVacio(ComisionesRenov.getCodplan())){
				if (FiltroUtils.noEstaVacio(ComisionesRenov.getCodplan())) {			
					criteria.add(Restrictions.eq("codplan", ComisionesRenov.getCodplan()));
				}	
			}
			
			if(FiltroUtils.noEstaVacio(ComisionesRenov.getCodlinea())){
				if (FiltroUtils.noEstaVacio(ComisionesRenov.getCodlinea())) {			
					criteria.add(Restrictions.eq("codlinea", ComisionesRenov.getCodlinea()));
				}	
			}

			return criteria.list();	
		} catch (Exception e) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + e.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
	}
	
	/**
	 * obtiene la linea segun codlinea y codplan
	 * @param codLinea
	 * @param codPlan
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Linea getLinea(BigDecimal codLinea, BigDecimal codPlan){
		Session sesion = obtenerSession();
			
			List<Linea> lstLineas = new ArrayList<Linea>();
			Linea lin= null;
			Criteria criteria = sesion.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", codLinea));
			criteria.add(Restrictions.eq("codplan", codPlan));
	    		
			lstLineas=criteria.list();
    	
	    	if (lstLineas!=null)
	    		if (lstLineas.size()>0)
	    			lin= lstLineas.get(0);
	    			
	    	return lin;				
	}
	
	/**
	 * obtiene el nombre de la linea por CodLinea y codPlan
	 * @param codLinea
	 * @param codPlan
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String getDescLinea(BigDecimal codLinea, BigDecimal codPlan){
		Session sesion = obtenerSession();
			
			List<Linea> lstLineas = new ArrayList<Linea>();
			Linea lin= null;
			
			String descLinea = "";
			
			Criteria criteria = sesion.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", codLinea));
			criteria.add(Restrictions.eq("codplan", codPlan));
	    		
			lstLineas=criteria.list();
    	
	    	if (lstLineas!=null){
	    		if (lstLineas.size()>0){
	    			lin= lstLineas.get(0);
	    			descLinea = lin.getNomlinea();
	    		}
	    	}
	    	return descLinea;
	}
	
	/**
	 * obtiene la descripcion de la Entidad opr el codEntidad
	 * @param codEntidad
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String getNombEntidad(BigDecimal codEntidad){
		Session sesion = obtenerSession();
			
			List<Entidad> lstEntidad = new ArrayList<Entidad>();
			Entidad entidad= null;
			String nombEntidad = ""; 
			
			Criteria criteria = sesion.createCriteria(Entidad.class);
			criteria.add(Restrictions.eq("codentidad", codEntidad));
	    		
			lstEntidad=criteria.list();
    	
	    	if (lstEntidad!=null){
	    		if (lstEntidad.size()>0){
	    			entidad= lstEntidad.get(0);
	    			nombEntidad = entidad.getNomentidad();
	    		}
	    	}
	    		
	    	return nombEntidad; 
	}

	@Override
	public boolean existeComisionesRenov(ComisionesRenov comisRenovBean, boolean valEntidad) throws Exception {
		logger.debug("exiteComisionRenov (init) - MtoComisionesRenovDao");

		boolean existeComis = false;

		/*
		 * MODIF TAM (13.03.2019) * RGA Ha solicitado quitar de la validacion la
		 * referencia de importe de tal forma que Para un mismo plan/linea, E-S
		 * Mediadora (o nula) y Grupo de negocio no se podrán indicar distintos valores
		 * en importe de referencia, es decir, si ya tengo un registro para 2018/415 –
		 * 3008-0 – RyD – Coste Tomador, si se intenta dar de alta un registro para
		 * prima comercial, que no deje darlo de alta (o modificar).
		 */

		/*
		 * Primero comprobamos que no haya un registro para el Plan/Linea/G.N con el
		 * importe de Referencia contrario, en caso de haberlo no se permite la
		 * modificacion
		 */
		/* comprobamos el tipo de referencia que se intenta dar de alta */
		char tipoRefContrario = comisRenovBean.getRefimporte() == 'C' ? 'P' : 'C';
		
		Integer count1 = 0;
		try {

			/*
			 * Si se trata de una modificacion no se puede tener en cuenta el propio
			 * registro para la validacion de duplicidad
			 */
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(ComisionesRenov.class);

			// Si estamos modificando, no se puede tener en cuenta el propio registro.
			if (comisRenovBean.getId() != null) {
				criteria.add(Restrictions.ne("id", comisRenovBean.getId()));
			}

			/*
			 * VALIDAMOS EN ALTA Y MODIFICACIoN DEL REGISTRO, SI YA EXISTE UN REGISTRO CON
			 * LOS MISMOS DATOS EN BBDD
			 */
			// Validamos si existe el registro para ese plan, linea, Grupo Negocio, modulo
			criteria.add(Restrictions.eq("codplan", comisRenovBean.getCodplan()));
			criteria.add(Restrictions.eq("codlinea", comisRenovBean.getCodlinea()));
			criteria.add(Restrictions.eq("idgrupo", comisRenovBean.getIdgrupo()));
			criteria.add(Restrictions.eq("codmodulo", comisRenovBean.getCodmodulo()));
			criteria.add(Restrictions.eq("refimporte", tipoRefContrario));

			if (valEntidad) {
				// Incluimos la validacion de la Entidad, EntidadMediadora y
				// SubentidadMediadora.
				criteria.add(Restrictions.eq("codentidad", comisRenovBean.getCodentidad()));
				criteria.add(Restrictions.eq("codentmed", comisRenovBean.getCodentmed()));
				criteria.add(Restrictions.eq("codsubmed", comisRenovBean.getCodsubmed()));
			} else {
				criteria.add(Restrictions.isNull("codentidad"));
				criteria.add(Restrictions.isNull("codentmed"));
				criteria.add(Restrictions.isNull("codsubmed"));
			}

			count1 = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();

			if (count1 > 0) {
				logger.error("Ya existe registro con los mismos datos");
				existeComis = true;
				return existeComis;

			}

		} catch (Exception e) {
			logger.error("Ya existe registro con los mismos datos. Clave duplicada", e);
			throw new DAOException("Ya existe registro con los mismos datos. Clave duplicada", e);
		}

		Integer count = 0;
		try {

			/*
			 * Si se trata de una modificacion no se puede tener en cuenta el propio
			 * registro para la validacion de duplicidad
			 */
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(ComisionesRenov.class);

			// si el id viene a null es un alta y si viene relleno estamos editando
			// y comprobamos que el registro que vamos a editar no exista sin
			// tener en cuenta el id que le estamos pasando

			/*
			 * VALIDAMOS EN ALTA Y MODIFICACIoN DEL REGISTRO, SI YA EXISTE UN REGISTRO CON
			 * LOS MISMOS DATOS EN BBDD
			 */
			// Si estamos dando de alta añadimos el % de Comision para que
			// no se permita alta de un mismo registro con el mismo porcentaje
			if (comisRenovBean.getId() != null) {
				criteria.add(Restrictions.eq("comision", comisRenovBean.getComision()));
				criteria.add(Restrictions.ne("id", comisRenovBean.getId()));
			}
						
			// Validamos si existe el registro para ese plan, linea, Grupo Negocio, modulo
			criteria.add(Restrictions.eq("codplan", comisRenovBean.getCodplan()));
			criteria.add(Restrictions.eq("codlinea", comisRenovBean.getCodlinea()));
			criteria.add(Restrictions.eq("idgrupo", comisRenovBean.getIdgrupo()));
			criteria.add(Restrictions.eq("codmodulo", comisRenovBean.getCodmodulo()));
			criteria.add(Restrictions.eq("refimporte", comisRenovBean.getRefimporte()));

			criteria.add(Restrictions.eq("impDesde", comisRenovBean.getimpDesde()));
			criteria.add(Restrictions.eq("impHasta", comisRenovBean.getimpHasta()));

			if (valEntidad) {
				// Incluimos la validacion de la Entidad, EntidadMediadora y
				// SubentidadMediadora.
				criteria.add(Restrictions.eq("codentidad", comisRenovBean.getCodentidad()));
				criteria.add(Restrictions.eq("codentmed", comisRenovBean.getCodentmed()));
				criteria.add(Restrictions.eq("codsubmed", comisRenovBean.getCodsubmed()));
			} else {
				criteria.add(Restrictions.isNull("codentidad"));
				criteria.add(Restrictions.isNull("codentmed"));
				criteria.add(Restrictions.isNull("codsubmed"));
			}

			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();

			if (count > 0) {
				logger.error("Ya existe registro con los mismos datos");
				existeComis = true;

			}

			return existeComis;

		} catch (Exception e) {
			logger.error("Ya existee registro con los mismos datos. Clave duplicada", e);
			throw new DAOException("Ya existee registro con los mismos datos. Clave duplicada", e);
		}
	}	
	/* MODIF TAM (13.03.2019) * INICIO */
	
	@Override
	public boolean validarRangoImporte(ComisionesRenov comisRenovBean, boolean valEntidad) throws Exception{
		logger.debug("validarRangosImportes (init) - MtoComisionesRenovDao");
		
		boolean valRangos = false;
		
		try {
			
			BigDecimal codPlan = comisRenovBean.getCodplan();
			BigDecimal codLinea = comisRenovBean.getCodlinea();
			char idGrupo = comisRenovBean.getIdgrupo();
			
			String sqlQuery = "SELECT COUNT(*) FROM tb_coms_renov_esmed C ";
			sqlQuery += "WHERE C.CODPLAN = " + codPlan + " AND C.CODLINEA= " +codLinea + " AND C.IDGRUPO= " + idGrupo;
			
			if (valEntidad == true){
				BigDecimal entidad = comisRenovBean.getCodentidad();
				BigDecimal codEntMed = comisRenovBean.getCodentmed();
				BigDecimal codSubMed = comisRenovBean.getCodsubmed();
				
				sqlQuery += " AND C.CODENTIDAD= " + entidad +" AND C.CODENTMED= " + codEntMed + " AND C.CODSUBMED= " + codSubMed ;
			} else {
				
				sqlQuery += " AND C.CODENTIDAD IS NULL AND C.CODENTMED IS NULL AND C.CODSUBMED IS NULL";
			}
			
			sqlQuery += " AND C.CODMODULO= '" + comisRenovBean.getCodmodulo() + "'";
			
			// Si estamos modificando un registro no se tiene que tener en cuenta a si mismo 
			if (comisRenovBean.getId() != null){
				sqlQuery += " AND C.ID <> " + comisRenovBean.getId();
			}
			
			BigDecimal impDesde_insert = comisRenovBean.getimpDesde();
			BigDecimal impHasta_insert = comisRenovBean.getimpHasta();
			
			sqlQuery += " AND ((C.IMP_DESDE < " + impDesde_insert + " OR C.IMP_HASTA < " + impHasta_insert +" )";
			sqlQuery += " OR (C.IMP_DESDE > "+ impDesde_insert + " AND C.IMP_HASTA = " + impHasta_insert + " )";
		    sqlQuery += " OR (C.IMP_DESDE = "+impDesde_insert + " AND (C.IMP_HASTA > " + impHasta_insert + " OR C.IMP_HASTA < " + impHasta_insert + "))";
			sqlQuery += " OR (C.IMP_DESDE > " + impDesde_insert + " AND C.IMP_DESDE < " +impHasta_insert + " AND C.IMP_HASTA > "+impHasta_insert+" ) )";
			sqlQuery += " AND (C.IMP_HASTA > " + impDesde_insert +" )";   
			
			SQLQuery query = this.obtenerSession().createSQLQuery(sqlQuery);
			
			Integer result = new Integer((query.uniqueResult()).toString());
			
			if (result > 0){
				logger.error("Existe un rango de importes coincidente para la comision");
				valRangos = true;
			}

			return valRangos;
		} 
		catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	/* MODIF TAM (13.03.2019) * FIN */
	
	
	@Override
	public BigDecimal getMaxIdComisRenov() {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_ID_COMIS_RENOV);
		BigDecimal uniqueResult = (BigDecimal)query.uniqueResult();
		return uniqueResult;
	}
	
	@Override
	public Integer validarEntidad(BigDecimal entidadMed, BigDecimal subentidadMed ,BigDecimal codEntidad) throws DAOException {
		
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			
			criteria.add(Restrictions.eq("id.codentidad", entidadMed));
			criteria.add(Restrictions.eq("id.codsubentidad", subentidadMed));
			if(null!=codEntidad){
				criteria.add(Restrictions.eq("entidad.codentidad", codEntidad));
			}
			criteria.setProjection(Projections.rowCount());
			
			return (Integer) criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}
	
	public void replicarComisRenov(BigDecimal planOrig, BigDecimal lineaOrig,BigDecimal planDest, BigDecimal lineaDest, String codUsuario)
				throws DAOException {
		
		
		try {
			// Procedimiento de réplica
			String procedimiento = "PQ_REPLICAR.replicarComisRenov (PLAN_ORIGEN IN NUMBER, LINEA_ORIGEN IN NUMBER, PLAN_DESTINO IN NUMBER, LINEA_DESTINO IN NUMBER, USUARIO IN VARCHAR2)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("PLAN_ORIGEN", planOrig);
			parametros.put("LINEA_ORIGEN", lineaOrig);
			parametros.put("PLAN_DESTINO", planDest);
			parametros.put("LINEA_DESTINO", lineaDest);
			parametros.put("USUARIO", codUsuario);
			
			databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			
		} catch (Exception e) {
			logger.error("Error al replicar Comisiones de Renovables",e);
			throw new DAOException("Error al replicar Comisiones de Renovables", e);
		}        
		
	}

	@Override
	public List<ComisionesRenov> getComisRenovParaCalculo(final ComisionesRenov predicate) throws DAOException {
		return getComisRenovParaCalculo(predicate, false, false);		
	}
	
	private List<ComisionesRenov> getComisRenovParaCalculo(final ComisionesRenov predicate, final boolean moduloGenerico, final boolean entidadNula) throws DAOException {
		List<ComisionesRenov> result = null;
		Session sesion = obtenerSession();
		try {
			Criteria criteria = sesion.createCriteria(ComisionesRenov.class);
			criteria.add(Restrictions.eq("codplan", predicate.getCodplan()));
			criteria.add(Restrictions.eq("codlinea", predicate.getCodlinea()));
			criteria.add(Restrictions.eq("idgrupo", predicate.getIdgrupo()));
			if (moduloGenerico) {
				criteria.add(Restrictions.eq("codmodulo", "99999"));
			} else {
				criteria.add(Restrictions.eq("codmodulo", predicate.getCodmodulo()));
			}
			if (entidadNula) {
				criteria.add(Restrictions.isNull("codentidad"));
				criteria.add(Restrictions.isNull("codentmed"));
				criteria.add(Restrictions.isNull("codsubmed"));
			} else {
				criteria.add(Restrictions.eq("codentidad", predicate.getCodentidad()));
				criteria.add(Restrictions.eq("codentmed", predicate.getCodentmed()));
				criteria.add(Restrictions.eq("codsubmed", predicate.getCodsubmed()));
			}
			@SuppressWarnings("unchecked")
			List<ComisionesRenov> aux = (List<ComisionesRenov>) criteria.list();
			if (aux == null || aux.isEmpty()) {
				if (moduloGenerico == false && entidadNula == false) {
					// PRIMERA ITERACION, LLLAMAMOS CON MODULO GENERICO, ENTIDAD ESPECIFICA
					result = getComisRenovParaCalculo(predicate, true, false);
				} else if (moduloGenerico == true && entidadNula == false) {
					// SEGUNDA ITERACION, LLLAMAMOS CON MODULO ESPECIFICO, ENTIDAD GENERICA
					result = getComisRenovParaCalculo(predicate, false, true);
				} else if (moduloGenerico == false && entidadNula == true) {
					// TERCERA ITERACION, LLLAMAMOS CON MODULO GENERICO, ENTIDAD GENERICA
					result = getComisRenovParaCalculo(predicate, true, true);
				} else {
					// NO HAY PARAMETRIZACION
					result = null;
				}				
			} else {
				result = aux;
			}
		} catch (Exception e) {
			logger.error("Error al obtener Comisiones para calculo", e);
			throw new DAOException("Error al obtener Comisiones para calculo", e);
		}
		return result;
	}	
}