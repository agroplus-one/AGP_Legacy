package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.DetalleComisionEsMediadora;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CultivosEntidadesDao extends BaseDaoHibernate implements ICultivosEntidadesDao {

	private static final String LINEA = "linea";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String ERROR_ACCESO_BBDD = "Se ha producido un error en el acceso a la BBDD";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	
	@Override
	public List<CultivosEntidades> getLastCultivosEntidades(boolean evitar2015)throws DAOException {
		Session session = obtenerSession();
		List<CultivosEntidades> aux = new ArrayList<CultivosEntidades>();
		try {
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias(LINEA, LINEA);
			if (evitar2015) criteria.add(Restrictions.lt(LINEA_CODPLAN, Constants.PLAN_2015));
			criteria.addOrder(Order.desc(LINEA_CODPLAN));
			criteria.addOrder(Order.desc("fechamodificacion"));	
			if (criteria.list().size() > 0)
				aux = criteria.list();

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		return aux;
	}
	
	@Override
	public List<CultivosEntidades> getLastCultivosEntidades(BigDecimal year) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias(LINEA, LINEA);
			
			if (!year.equals(new BigDecimal(0))){
				criteria.add(Restrictions.eq(LINEA_CODPLAN, year));
			}
			criteria.addOrder(Order.desc("fechamodificacion"));
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
	}

	@Override
	public List<CultivosEntidades> listComisionesCultivosEntidades(CultivosEntidades cultivosEntidadesBean) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.addOrder(Order.desc(LINEA_CODPLAN));
			criteria.addOrder(Order.asc(LINEA_CODLINEA));
			
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getLinea())){
				if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getLinea().getCodlinea())){
					criteria.add(Restrictions.eq(LINEA_CODLINEA, cultivosEntidadesBean.getLinea().getCodlinea()));
				}
				if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getLinea().getCodplan())){
					criteria.add(Restrictions.eq(LINEA_CODPLAN, cultivosEntidadesBean.getLinea().getCodplan()));
				}
			}
			
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getPctgeneralentidad())){
				criteria.add(Restrictions.eq("pctgeneralentidad", cultivosEntidadesBean.getPctgeneralentidad()));
			}
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getPctadquisicion())){
				criteria.add(Restrictions.eq("pctadquisicion", cultivosEntidadesBean.getPctadquisicion()));
			}
		
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getFechaEfecto())){
				criteria.add(Restrictions.eq("fechaEfecto", cultivosEntidadesBean.getFechaEfecto()));
			}
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getPctadministracion())){
				criteria.add(Restrictions.eq("pctadministracion", cultivosEntidadesBean.getPctadministracion()));
			}
			if(null!=cultivosEntidadesBean.getGrupoNegocio() && null!=cultivosEntidadesBean.getGrupoNegocio().getGrupoNegocio()){
				criteria.add(Restrictions.eq("grupoNegocio.grupoNegocio", cultivosEntidadesBean.getGrupoNegocio().getGrupoNegocio()));
			}
			
			if(null!=cultivosEntidadesBean.getSubentidadMediadora()&& null!=cultivosEntidadesBean.getSubentidadMediadora().getId()){
				if(null!=cultivosEntidadesBean.getSubentidadMediadora().getId().getCodentidad()){
					criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", cultivosEntidadesBean.getSubentidadMediadora().getId().getCodentidad()));					
				}
				if(null!=cultivosEntidadesBean.getSubentidadMediadora().getId().getCodsubentidad()){
					criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", cultivosEntidadesBean.getSubentidadMediadora().getId().getCodsubentidad()));
					
				}
			}
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
	}
	
	@Override
	public Linea getLineaseguroId(BigDecimal codlinea, BigDecimal codplan) throws DAOException {
		logger.debug("init - getLineaseguroId");
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", codlinea));
			criteria.add(Restrictions.eq("codplan", codplan));
			
			logger.debug("end -  getLineaseguroId");
			return (Linea)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
	}

	@Override
	public Long comisionesAsociadas(Linea linea) throws DAOException {
		logger.debug("init - comisionesAsociadas");
		Session session = obtenerSession();
		try {
			
			String query = "select count(*) from CultivosSubentidades c where c.linea.codlinea = :linea and c.linea.codplan = :plan";
			
			Query countQuery = session.createQuery(query).setBigDecimal(LINEA, linea.getCodlinea())
														 .setBigDecimal("plan", linea.getCodplan());
			
			logger.debug("end - comisionesAsociadas");
			return (Long)countQuery.uniqueResult();
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
	}
	
	@Override
	/**
	 * Comprueba si en la tabla tb_coms_cultivos_entidades existe comision para el 
	 * plan/ linea indicados	 
	 */
	public boolean existeComisionMaxima(String codplan, String codlinea) throws DAOException {
		
		List<CultivosEntidades> list = null;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias(LINEA, LINEA);
			
			criteria.add(Restrictions.eq(LINEA_CODPLAN, new BigDecimal (codplan)));		
			criteria.add(Restrictions.eq(LINEA_CODLINEA, new BigDecimal (codlinea)));
			criteria.add(Restrictions.isNull("fechaBaja"));
			list = criteria.list();
			
			if(list.size() > 0)
				return true;
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		return false;
	}
	
	@Override
	public Integer existeRegistro(CultivosEntidades cultivosEntidadesBean) throws DAOException {
		logger.debug("init - existeRegistro");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			
			criteria.createAlias(LINEA, LINEA);
			
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getLinea())){
				if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getLinea().getCodlinea()))
					criteria.add(Restrictions.eq(LINEA_CODLINEA, cultivosEntidadesBean.getLinea().getCodlinea()));
				if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getLinea().getCodplan()))
					criteria.add(Restrictions.eq(LINEA_CODPLAN, cultivosEntidadesBean.getLinea().getCodplan()));
			}
			criteria.add(Restrictions.isNull("fechaBaja"));
			if(FiltroUtils.noEstaVacio(cultivosEntidadesBean.getId()))
				criteria.add(Restrictions.ne("id", cultivosEntidadesBean.getId()));
			
			
			if(null!=cultivosEntidadesBean.getGrupoNegocio() && null!=cultivosEntidadesBean.getGrupoNegocio().getGrupoNegocio()){
				criteria.add(Restrictions.eq("grupoNegocio.grupoNegocio", cultivosEntidadesBean.getGrupoNegocio().getGrupoNegocio()));
			}else{
				criteria.add(Restrictions.isNull("grupoNegocio.grupoNegocio"));
			}
			
			if(null!=cultivosEntidadesBean.getSubentidadMediadora()&& null!=cultivosEntidadesBean.getSubentidadMediadora().getId()){
				if(null!=cultivosEntidadesBean.getSubentidadMediadora().getId().getCodentidad()){
					criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", cultivosEntidadesBean.getSubentidadMediadora().getId().getCodentidad()));					
				}else{
					criteria.add(Restrictions.isNull("subentidadMediadora.id.codentidad"));
				}
				if(null!=cultivosEntidadesBean.getSubentidadMediadora().getId().getCodsubentidad()){
					criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", cultivosEntidadesBean.getSubentidadMediadora().getId().getCodsubentidad()));
				}else{
					criteria.add(Restrictions.isNull("subentidadMediadora.id.codsubentidad"));
				}
			}
			
			
			
			
			criteria.setProjection(Projections.rowCount());
			
			logger.debug("end - existeRegistro");
			return (Integer)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
	}
	
	public List<DetalleComisionEsMediadora> getListDetallePct(String codPlan,BigDecimal ent, 
			BigDecimal subEnt, BigDecimal lineaseguoId,BigDecimal codLin, BigDecimal codLineaTemp)
			throws DAOException {
		logger.debug("init - getListDetallePct");
		List parametrosGenerales = new ArrayList();
		try {
			 List<CultivosSubentidades> cultSubEnt = getCultivosSubentidades(new BigDecimal(codPlan),ent,subEnt, codLin);
			 List<DetalleComisionEsMediadora> listDetallePct = new ArrayList<DetalleComisionEsMediadora>();
			 if (codLineaTemp != null)
				 parametrosGenerales=getParamsGen(lineaseguoId.longValue(), ent, subEnt,codLineaTemp, codPlan);
			 else
				 parametrosGenerales=getParamsGen(lineaseguoId.longValue(), ent, subEnt,codLin, codPlan);
			 
			if (parametrosGenerales.size()>0 && null!=cultSubEnt && cultSubEnt.size()>0){
				listDetallePct = getListaDetalleComisionEsMediadora(parametrosGenerales, cultSubEnt);
			}
			return listDetallePct;

		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:"
					+ e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}
	
	
	public List getParamsGen(Long lineaseguroid, BigDecimal entMed,
			BigDecimal subentMed,BigDecimal codLin,String codplan) throws Exception {
		
		logger.debug("init - getParamsGen");
		Session session = obtenerSession();
		List registros = new ArrayList();
		List resultado = new ArrayList();
		try {
			//l.codlinea,l.codplan, e.GRUPO_NEGOCIO, cgn.DESCRIPCION, e.pctgeneralentidad
			String sql =" SELECT "+codLin+","+codplan+", e.GRUPO_NEGOCIO,grn.descripcion,h.pctgeneralentidad " +
						" FROM tb_sc_c_grupos_negocio grn,tb_coms_cultivos_entidades e " + 
						" INNER JOIN tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "+
						" WHERE e.ENTMEDIADORA = " + entMed + " and e.SUBENTMEDIADORA = " + subentMed +
						" and e.grupo_negocio=grn.grupo_negocio "+
						" and ((e.fec_baja is null or to_date(current_date,'dd/mm/yyy') " +
								" < to_date(e.fec_baja,'dd/mm/yyy')) " +  
						" and to_date(current_date,'dd/mm/yyy') >= h.fecha_efecto) " +						
						" and e.lineaseguroid = " + lineaseguroid + 
						" order by h.fecha_efecto  desc ,h.fechamodificacion desc";						
			logger.info("sql " + sql);
			
			registros = session.createSQLQuery(sql).list();
			
			//if (registros.size()<1){
				sql =" SELECT "+codLin+","+codplan+", e.GRUPO_NEGOCIO,grn.descripcion,h.pctgeneralentidad " +
						" FROM tb_sc_c_grupos_negocio grn,tb_coms_cultivos_entidades e " + 
						" INNER JOIN tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "+
						" WHERE e.ENTMEDIADORA is null and e.SUBENTMEDIADORA is null " + 
						" and e.grupo_negocio=grn.grupo_negocio "+
						" and ((e.fec_baja is null or to_date(current_date,'dd/mm/yyy') " +
								" < to_date(e.fec_baja,'dd/mm/yyy')) " +  
						" and to_date(current_date,'dd/mm/yyy') >= h.fecha_efecto) " +						
						" and e.lineaseguroid = " + lineaseguroid + 
						" order by h.fecha_efecto  desc ,h.fechamodificacion desc";
				logger.info("sql " + sql);
			registros.addAll(session.createSQLQuery(sql).list());
			//}
			
			//Nos quedamos con el primer registro de cada grupo de negocio
			List gn=new ArrayList<String>();
			String gnActual;
			if(registros.size()>0){
				for (int i = 0; i < registros.size(); i++) {
					Object[] paramsGen =null;
					paramsGen = (Object[]) registros.get(i);
					gnActual =((String)paramsGen[2]);
					if(!gn.contains(gnActual)){
						resultado.add(registros.get(i));
						gn.add(gnActual);
					}					
				}
			}

			logger.debug("fin - getParamsGen");
			return resultado;					
			
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - getParamsGen- PolizasPctComisionesDao");
			throw e;
		}
		
		//return null;
	}
	
	
	private List<DetalleComisionEsMediadora> getListaDetalleComisionEsMediadora(List listaParamsGenerales,  List<CultivosSubentidades> culSubEnt){
		List<DetalleComisionEsMediadora> listDetalle = new ArrayList<DetalleComisionEsMediadora>();
		
// ahora		l.codlinea,  l.codplan, , e.GRUPO_NEGOCIO, cgn.DESCRIPCION, e.pctgeneralentidad
// antes 		SELECT l.codlinea,  l.codplan,  s.pctentidad,  s.pctmediador,
//		  			s.pctentidad  * e.pctgeneralentidad)/100 AS pctEntAux ,
//		  			(s.pctmediador * e.pctgeneralentidad)/100 AS pctEsMedAux
		CultivosSubentidades culSub=culSubEnt.get(0);
		
		for (int i = 0; i < listaParamsGenerales.size(); i++) {
			Object[] registro = (Object[]) listaParamsGenerales.get(i);
			DetalleComisionEsMediadora aux = new DetalleComisionEsMediadora();
			if (registro[0] != null) {
				aux.setCodlinea((BigDecimal) registro[0]);
			}
			if (registro[1] != null) {
				aux.setCodplan((BigDecimal) registro[1]);
			}
			if (registro[2] != null) {
				String gn=registro[2].toString();
				aux.setGrupoNegocio(gn.charAt(0));
			}
			if (registro[3] != null) {
				aux.setDescripcionGN(registro[3].toString());
			}
			if(culSub.getPctentidad() != null){
				aux.setPctEntidad(culSub.getPctentidad());
			}
			if(culSub.getPctmediador() != null){
				aux.setPctMediador(culSub.getPctmediador());
			}						
			if (culSub.getPctentidad() != null && registro[4] != null) {
				BigDecimal pctEntAux = ((BigDecimal) registro[4]).setScale(2, BigDecimal.ROUND_DOWN);
				BigDecimal resPctEntAux=pctEntAux.multiply(culSub.getPctentidad()).divide(new BigDecimal(100));
				aux.setPctEntAux(resPctEntAux);
			}
			if (culSub.getPctmediador() != null && registro[4] != null) {
				BigDecimal pctEsMedAux=((BigDecimal) registro[4]).setScale(2, BigDecimal.ROUND_DOWN);				
				BigDecimal resPctEsMedAux=pctEsMedAux.multiply(culSub.getPctmediador()).divide(new BigDecimal(100));;
				aux.setPctEsMedAux(resPctEsMedAux);
			}
			
			listDetalle.add(aux);
		}
		return listDetalle;
	}
	
	public List<CultivosSubentidades> getCultivosSubentidades(BigDecimal codPlan,BigDecimal entMed, 
			BigDecimal subEntMed, BigDecimal codLin)
			throws DAOException {
		
		Session session = obtenerSession();
		List<CultivosSubentidades> aux = new ArrayList<CultivosSubentidades>();
		try {
			Criteria criteria = session.createCriteria(CultivosSubentidades.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
			if(null!=codLin)
				criteria.add(Restrictions.eq(LINEA_CODLINEA, codLin));
			
			criteria.createAlias("subentidadMediadora", "esMed");
			criteria.add(Restrictions.eq("esMed.id.codentidad", entMed));
			criteria.add(Restrictions.eq("esMed.id.codsubentidad", subEntMed));
			criteria.add(Restrictions.isNull("fecBaja"));
			criteria.addOrder(Order.desc(LINEA_CODLINEA));
		
			
			aux = criteria.list();

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		return aux;
	}
	
	public List<GruposNegocio>getGruposNegocio()throws DAOException {
		
		Session session = obtenerSession();
		List<GruposNegocio> aux = new ArrayList<GruposNegocio>();
		try {
			Criteria criteria = session.createCriteria(GruposNegocio.class);
			
			criteria.add(Restrictions.ne("grupoNegocio", Constants.GRUPO_NEGOCIO_GENERICO));		
			aux = criteria.list();

		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		return aux;
		
	}
	
	public List<Linea> getListLineasParamsGenByPlan( String codPlan){
		Session session = obtenerSession();
		List<Linea> lstLineas=new ArrayList<Linea>();
		List registros = new ArrayList();
		try {			
			String sql =" select distinct lin.lineaseguroid,lin.codlinea from o02agpe0.tb_lineas lin, o02agpe0.tb_coms_cultivos_entidades e " +
						" where e.lineaseguroid = lin.lineaseguroid and lin.codplan=" +codPlan+ " order by lin.codlinea";						
			logger.info("sql " + sql);
			
			registros = session.createSQLQuery(sql).list();
			
			if(registros.size()>0){
				for (int i = 0; i < registros.size(); i++) {
					Linea lin = new Linea();
					Object[] reg = (Object[]) registros.get(i);
					if (reg[0] != null) {
						BigDecimal linId = (BigDecimal)reg[0];
						lin.setLineaseguroid(linId.longValue());
						
					}
					if (reg[1] != null) {
						lin.setCodlinea((BigDecimal)reg[1]);
					}
					lstLineas.add(lin);
				}
			}
			
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - getParamsGen - getListLineasParamsGenByPlan",e);
			
		}
		return lstLineas;
	}
	
	@Override
	public CultivosEntidades getCultivoEntidadByPlanLinea(BigDecimal codplan, BigDecimal codlinea) throws DAOException {
		logger.debug("init - getCultivoEntidadByPlanLinea");
		Session session = obtenerSession();
		CultivosEntidades ent = null;
		List<CultivosEntidades> list = null;
		try {
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias(LINEA, LINEA);
			
			criteria.add(Restrictions.eq(LINEA_CODPLAN, codplan));		
			criteria.add(Restrictions.eq(LINEA_CODLINEA, codlinea));
			
			list = criteria.list();
			
			if(list.size() > 0)
				ent = list.get(0);
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		
		return ent;
		
	}
	
	@Override
	public List<CultivosEntidades> getCultivosEntidadByPlanLinea(BigDecimal codplan, BigDecimal codlinea) throws DAOException {
		logger.debug("init - getCultivoEntidadByPlanLinea");
		Session session = obtenerSession();
		CultivosEntidades ent = null;
		List<CultivosEntidades> list = null;
		try {
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias(LINEA, LINEA);
			
			criteria.add(Restrictions.eq(LINEA_CODPLAN, codplan));		
			criteria.add(Restrictions.eq(LINEA_CODLINEA, codlinea));
			
			list = criteria.list();
			
			if(list.size() > 0)
				return list;
			
		} catch (Exception ex) {
			logger.error(ERROR_ACCESO_BBDD + ex.getMessage());
			throw new DAOException(ERROR_ACCESO_BBDD,ex);
		}
		
		return null;
		
	}
	
	@Override
	public Map<String, Object> cambioMasivo(String listaIds, CultivosEntidades cultivosEntidadesBean){
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			Session session = obtenerSession();
	
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			String[] ids = listaIds.split(";");
			boolean entro = false;
		
			List lstCom = Arrays.asList(ids);
			// Update para comisiones
			StringBuilder sql = new StringBuilder();
			StringBuilder sql2015 = new StringBuilder();
			
			// para el caso de plan > 2014
			sql2015.append("update TB_COMS_CULTIVOS_ENTIDADES set ");
			if (cultivosEntidadesBean.getPctgeneralentidad() !=null){
				sql2015.append(" pctgeneralentidad = ").append(cultivosEntidadesBean.getPctgeneralentidad().toString());
				entro = true;
			}
			if (cultivosEntidadesBean.getPctadquisicion() !=null){
				if (entro)
					sql2015.append(",");
				sql2015.append(" pctadquisicion = ").append(cultivosEntidadesBean.getPctadquisicion().toString());
				entro = true;
			}
			if (cultivosEntidadesBean.getPctadministracion() !=null){
				if (entro)
					sql2015.append(",");
				sql2015.append(" pctrga = ").append(cultivosEntidadesBean.getPctadministracion());
				entro = true;
			}
			if (cultivosEntidadesBean.getFechaEfecto() !=null){
				if (entro)
					sql2015.append(",");
				
				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
				String fecEfecto = "";				
				fecEfecto = StringUtils.forHTML(formato.format(cultivosEntidadesBean.getFechaEfecto()));
	
				sql2015.append(" fecha_efecto = ").append("TO_DATE('"+fecEfecto+"','dd/mm/yyyy')");
				entro = true;
			}
						
			sql2015.append(" where ");
			StringBuilder strWhere = CriteriaUtils.splitSql("id",lstCom);
			sql2015.append(strWhere.toString());
			sql2015.append(" and lineaseguroid in (select lin.lineaseguroid from tb_lineas lin where lin.codplan >2014)  ");			
			
			
			logger.debug("update comisiones > 2014: "+ sql2015.toString());
			session.createSQLQuery(sql2015.toString()).executeUpdate();
			
			// para el caso de plan < 2015
			//solo se actualiza los campos Pctgeneralentidad y Pctadministracion. Si estos vienen nulos,
			// no se ejecuta el update.
			if (cultivosEntidadesBean.getPctgeneralentidad()!= null || cultivosEntidadesBean.getPctadministracion() != null) {
				entro = false;
				sql.append("update TB_COMS_CULTIVOS_ENTIDADES set ");
				if (cultivosEntidadesBean.getPctgeneralentidad() !=null){
					sql.append(" pctgeneralentidad = ").append(cultivosEntidadesBean.getPctgeneralentidad().toString());
					entro = true;
				}
				
				if (cultivosEntidadesBean.getPctadministracion() !=null){
					if (entro)
						sql.append(",");
					sql.append(" pctrga = ").append(cultivosEntidadesBean.getPctadministracion());
					entro = true;
				}
				
				sql.append(" where ");
				sql.append(strWhere.toString());
				sql.append(" and lineaseguroid in (select lin.lineaseguroid from tb_lineas lin where lin.codplan <2015)  ");			
				
				logger.debug("update comisiones < 2015: "+ sql.toString());
				session.createSQLQuery(sql.toString()).executeUpdate();
			}
		}catch (Exception e ) {
			logger.error("Error en cambioMasivo de parametros generales de Comisiones: ", e);
			parametros.put("alerta", "Se ha producido un error en el cambio masivo");
			return parametros ;
		}
		return parametros;
	}
	
	
	public void replicarCultivos(final BigDecimal plan_origen, final BigDecimal linea_origen, final BigDecimal plan_destino, final BigDecimal linea_destino) throws DAOException{


		
		logger.debug("replicarCultivos # INIT");

		try {
			final Session session = this.obtenerSession();

			

			session.doWork(new Work() {
				public void execute(final Connection connection) throws SQLException {						

											
						try (CallableStatement call = connection.prepareCall("{ call O02AGPE0.PQ_REPLICAR.replicarDatosCultivos(?,?,?,?) }")) {
							call.setBigDecimal(1, plan_origen);
							call.setBigDecimal(2, linea_origen);
							call.setBigDecimal(3, plan_destino);
							call.setBigDecimal(4, linea_destino);
							call.execute();
						}
					
				}
			});	
			
			logger.debug("replicarCultivos # END");

		} catch (Exception e) {
			logger.error("Se ha producido un error al efectuar replicarCultivos",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		
		
	}
	
}
 