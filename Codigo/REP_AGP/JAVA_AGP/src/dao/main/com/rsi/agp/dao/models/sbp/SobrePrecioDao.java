package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.SobreprecioSbpFilter;
import com.rsi.agp.core.jmesa.sort.SobreprecioSbpSort;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;
import com.rsi.agp.dao.tables.sbp.SobreprecioItem;

public class SobrePrecioDao extends BaseDaoHibernate implements ISobrePrecioDao {

	@SuppressWarnings("unchecked")
	public List<Sobreprecio> getLineaSobrePrecio(Long lineaseguroid)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias("linea", "lin");
			criteria.add(Restrictions.eq("lin.lineaseguroid", lineaseguroid));
			return criteria.list();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Sobreprecio> getLineaSbpFromLineaPlan(String linea, String plan)
	throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias("linea", "lin");
			if (!linea.equals("")){
				criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(linea)));
			}
			if (!plan.equals("")){
				criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(plan)));
			}
			return criteria.list();
		
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					ex);
		}
}

	@Override
	public int getConsultaPolizaSbpCountWithFilter(final SobreprecioSbpFilter filter) {
		logger.debug("init - [SobrePrecioDao] getConsultaPolizaSbpCountWithFilter");
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(Sobreprecio.class);
						// Alias
						criteria.createAlias("linea", "lin");
						criteria.createAlias("provincia", "prov", CriteriaSpecification.LEFT_JOIN);
						criteria.createAlias("cultivo", "cultivo");
						criteria.createAlias("tipoCapital", "tipoCapital");
						// Filtro
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.rowCount())
								.uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger
				.debug("end - [SobrePrecioDao] getConsultaPolizaSbpCountWithFilter");
		return count.intValue();
		
	}

	@Override
	@SuppressWarnings("all")
	public Collection<Sobreprecio> getSobreprecioSbpWithFilterAndSort(
			final SobreprecioSbpFilter filter,final SobreprecioSbpSort sort,final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger
					.debug("init - [SobrePrecioDao] getSobreprecioSbpWithFilterAndSort");
			List<Sobreprecio> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(Sobreprecio.class);
							// Alias
							criteria.createAlias("linea", "lin");
							criteria.createAlias("provincia", "prov", CriteriaSpecification.LEFT_JOIN);
							criteria.createAlias("cultivo", "cultivo");
							criteria.createAlias("tipoCapital", "tipoCapital");
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							//DAA 06/06/2012 Devuelve el listado de pólizas
							List<Sobreprecio> lista = criteria.list();
							List<SobreprecioItem> listaItem = new ArrayList<SobreprecioItem>();
							for (Sobreprecio s : lista){
								listaItem.add(new SobreprecioItem(s));
							}
							return listaItem;
							//return criteria.list();
						}
					});
			logger.debug("end - [SobrePrecioDao] getSobreprecioSbpWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	public void actualizaSobreprecio(Sobreprecio sobreprecio) throws ConstraintViolationException,DataIntegrityViolationException,Exception{
		
		Session session = obtenerSession();
		
		try {
			
			
			StringBuilder sql = new StringBuilder();
			sql.append("update tb_sbp_sobreprecio s set ");
			
			sql.append(" lineaseguroid = ").append(sobreprecio.getLinea().getLineaseguroid()).append(", ");
			
			sql.append(" codcultivo = ").append(sobreprecio.getCultivo().getId().getCodcultivo()).append(", ");
			
			sql.append(" codtipocapital = ").append(sobreprecio.getTipoCapital().getCodtipocapital()).append(", ");
			
			sql.append(" codprovincia = ").append(sobreprecio.getProvincia().getCodprovincia()).append(", ");
			
			sql.append(" precio_minimo = ").append(sobreprecio.getPrecioMinimo()).append(", ");
			
			sql.append(" precio_maximo = ").append(sobreprecio.getPrecioMaximo());
			
			sql.append(" where id = ").append(sobreprecio.getId());
			
			Query update = session.createSQLQuery(sql.toString());
			update.executeUpdate();
		
		} catch (Exception ex) {
			logger.error(ex);
			throw ex;
		}
	}
	
	
	/**
	 * Chequea si existe el cultivo en tabla "TB_SC_C_CULTIVOS"
	 */
	@SuppressWarnings("unchecked")
	public boolean existeCultivo(Long lineaseguroid, BigDecimal codCultivo){
		
		List<Cultivo> lstCultivo = new ArrayList<Cultivo>();
		Session session = obtenerSession();
		boolean existeCultivo = false;
		
		try{
				Criteria criteria = session.createCriteria(Cultivo.class);
				// Alias
				criteria.createAlias("linea", "linea");
				criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
				criteria.add(Restrictions.eq("id.codcultivo", codCultivo));
				
				lstCultivo = criteria.list();
				
				if (!lstCultivo.isEmpty()){
					existeCultivo = true;
				}
			
		} catch (Exception ex) {
			logger.error("[SobreprecioDao] existeCultivo - Se ha producido un error en la BBDD: " + ex.getMessage());
		}

		return existeCultivo;
	}
	
	/**
	 * Chequea si existe el tipo de capital en la tabla "TB_SC_C_TIPO_CAPITAL"
	 */
	@SuppressWarnings("unchecked")
	public boolean existeTipoCapital(BigDecimal codTipoCapital){
		
		List<TipoCapital> lstTiposCapitales = new ArrayList<TipoCapital>();
		Session session = obtenerSession();
		boolean existeTipoCapital = false;
		
		try{
				Criteria criteria = session.createCriteria(TipoCapital.class);
				criteria.add(Restrictions.eq("codtipocapital", codTipoCapital));
				
				lstTiposCapitales = criteria.list();
				
				if (!lstTiposCapitales.isEmpty()){
					existeTipoCapital = true;
				}
			
		} catch (Exception ex) {
			logger.error("[SobreprecioDao] existeTipoCapital - Se ha producido un error en la BBDD: " + ex.getMessage());
		}

		return existeTipoCapital;
	}
	
	public String replicar(BigDecimal origen, BigDecimal destino) throws DAOException {
		
		int resultado=0;
		
		try {
			String procedimiento = "PQ_REPLICAR.replicarSbp (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, P_RESULT OUT NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("LINEASEGUROID_DESTINO", destino);
			parametros.put("LINEASEGUROID_ORIGEN", origen);
			parametros.put("P_RESULT", resultado);
		
			parametros = databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			return parametros.get("P_RESULT").toString();
			
		} catch (Exception e) {
			logger.error("Error al replicar los Sbp ",e);
			throw new DAOException("Error al replicar los Sbp ", e);
		}        
		
	}

	@SuppressWarnings("rawtypes")
	public boolean numRegDestinoIgualNumRegOrigen(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen) {
		
		boolean numregIguales = true;
		Session session = obtenerSession();
		
		String sql = "select count(*) from TB_SBP_SOBREPRECIO s where s.lineaseguroid in ("+
					lineaSeguroIdDestino+","+lineaSeguroIdOrigen+ ") group by s.lineaseguroid";
		List list = session.createSQLQuery(sql).list();
		
		BigDecimal reg1 = new BigDecimal(list.get(0).toString());
		BigDecimal reg2 = new BigDecimal(list.get(1).toString());
		
		if(reg1.compareTo(reg2)!= 0)
			numregIguales = false;
		
		return numregIguales;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * Dada una poliza con A.M busca si estos anexos tienen cultivos de sbp
	 * para mostrar el boton de sobreprecio
	 * @param List cultivosSbp
	 * @param Long idpoliza
	 * @return BigDecimal 
	 */
	public BigDecimal buscaParcelasAnexoSBP(List cultivosSbp, Long idPoliza){
		
		Session session = obtenerSession();
		BigDecimal count ;
		
		String sql = " select ad.id from o02agpe0.tb_anexo_mod ad,o02agpe0.TB_COMUNICACIONES c " +
					 " where ad.estado=3 and ad.idenvio =c.idenvio "+ 
					 " and ad.idpoliza= "+ idPoliza +" order by c.fecha_envio desc";
		
		List anexos = session.createSQLQuery(sql).list();
		if (anexos.size()>0){
			BigDecimal id = (BigDecimal) anexos.get(0);
			String sql2 = "select count(*) from " +
							" tb_anexo_mod_parcelas mp left join tb_parcelas par on (mp.idparcela = par.idparcela)"+
							" left join o02agpe0.tb_copy_parcelas cp on (mp.idcopyparcela = cp.id)"+
							" where mp.idanexo = "+id.longValue()+" and mp.codcultivo in " +
							  StringUtils.toValoresSeparadosXComas(cultivosSbp, false) +
							" and mp.tipomodificacion <> 'B' order by mp.hoja, mp.numero";
			
			count = (BigDecimal) session.createSQLQuery(sql2).uniqueResult();
		}else{
			count = new BigDecimal(0);
		}
		
		
		return count;
	}

	public Date getFechaFinGarantiasSbp(final Long lineaseguroid, final BigDecimal[] codcultivos) throws Exception {

		Session session = obtenerSession();

		try {

			Criteria criteria = session.createCriteria(FechaContratacionSbp.class);
			criteria.createAlias("linea", "linea");
			criteria.createAlias("cultivo", "cultivo");
			criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			criteria.add(
					Restrictions.in("cultivo.id.codcultivo", ArrayUtils.add(codcultivos, BigDecimal.valueOf(999))));
			criteria.setProjection(Projections.max("fechaFinGarantia"));

			return (Date) criteria.uniqueResult();

		} catch (Exception ex) {
			logger.error("[SobreprecioDao] getFechaFinContratacionSbp - Se ha producido un error en la BBDD: "
					+ ex.getMessage());
			throw ex;
		}
	}
}
