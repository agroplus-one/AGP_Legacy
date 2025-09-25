package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaHistoricoDescuentos;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;

@SuppressWarnings("rawtypes")
public class PolizasPctComisionesDao extends BaseDaoHibernate implements IPolizasPctComisionesDao {

	private static final String PCT_DESC_MAX = "pctDescMax";
	private static final String FECHA_BAJA = "fechaBaja";
	private static final String DELEGACION = "delegacion";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String OFICINA_ID_CODOFICINA = "oficina.id.codoficina";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD = "subentidadMediadora.id.codsubentidad";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODENTIDAD = "subentidadMediadora.id.codentidad";
	private static final String SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD = "subentidadMediadora.entidad.codentidad";
	private static final String LINEA = "linea";
	private static final String SUBENTIDAD_MEDIADORA_ID = "subentidadMediadora.id";
	private static final String ENTIDAD = "entidad";
	private static final String SUBENTIDAD_MEDIADORA_ENTIDAD = "subentidadMediadora.entidad";
	private static final String SUBENTIDAD_MEDIADORA = "subentidadMediadora";
	private static final String WHERE_C_ID = " where c.ID = ";
	
	private Log logger = LogFactory.getLog(getClass());
	private ILineaDao lineaDao;
	
	public List getParamsGen(Long lineaseguroid, BigDecimal entMed,
			BigDecimal subentMed) throws Exception {
		return getParamsGen(lineaseguroid, entMed, subentMed, null);
	}
	
	@SuppressWarnings("unchecked")
	public List getParamsGen(Long lineaseguroid, BigDecimal entMed,
			BigDecimal subentMed,Date fechaRenovacion) throws Exception {
		
		logger.debug("init - getParamsGen");
		Session session = obtenerSession();
		List registros = new ArrayList();
		List resultado = new ArrayList();
		boolean comprobarConNulos = true;
		String fechaParaComparar="current_date";
		if(fechaRenovacion!=null){
			DateFormat df = new SimpleDateFormat("dd/MM/yy");
			fechaParaComparar= "'"+df.format(fechaRenovacion)+"'".trim();
		}
		try {
			
			String sql =" SELECT h.pctrga,h.pctadquisicion,h.pctgeneralentidad, e.GRUPO_NEGOCIO " +
						" FROM o02agpe0.tb_coms_cultivos_entidades e " + 
						"\tINNER JOIN o02agpe0.tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "+
						" WHERE e.ENTMEDIADORA = " + entMed + " and e.SUBENTMEDIADORA = " + subentMed +
						" and ((e.fec_baja is null or to_date("+fechaParaComparar+",'dd/mm/yy') " +
								" < to_date(e.fec_baja,'dd/mm/yy')) " +  
						" and to_date("+fechaParaComparar+",'dd/mm/yy') >= h.fecha_efecto) " +						
						" and e.lineaseguroid = " + lineaseguroid + 
						" order by h.fecha_efecto  desc ,h.fechamodificacion desc";						
			logger.info("sql: " + sql);
			
			registros = session.createSQLQuery(sql).list();
			// comprobamos si el grupo de negocio del primer registro es el genérico, si es así no busca más.
			if(registros.size()>0){
			    String gnPrimero;
			    Object[] paramsGen =null;
				paramsGen = (Object[]) registros.get(0);
				gnPrimero =((String) paramsGen[3]);
				if(gnPrimero.equals(Constants.GRUPO_NEGOCIO_GENERICO.toString())){
					comprobarConNulos = false;
				}								
			}

			if (comprobarConNulos){
				sql =" SELECT h.pctrga,h.pctadquisicion,h.pctgeneralentidad, e.GRUPO_NEGOCIO " +
						" FROM o02agpe0.tb_coms_cultivos_entidades e " + 
						"\tINNER JOIN o02agpe0.tb_coms_cultivos_ents_hist h  ON  e.id = h.idcomisionesent "+
						" WHERE e.ENTMEDIADORA is null and e.SUBENTMEDIADORA is null " + 
						" and ((e.fec_baja is null or to_date("+fechaParaComparar+",'dd/mm/yy') " +
								" < to_date(e.fec_baja,'dd/mm/yy')) " +  
						" and to_date("+fechaParaComparar+",'dd/mm/yy') >= h.fecha_efecto) " +						
						" and e.lineaseguroid = " + lineaseguroid + 
						" order by h.fecha_efecto  desc ,h.fechamodificacion desc";
				logger.info("sql con nulos: " + sql);
				registros.addAll(session.createSQLQuery(sql).list());
			}
			
			//Nos quedamos con el primer registro de cada grupo de negocio
			List gn=new ArrayList<String>();
			String gnActual;
			if(registros.size()>0){
				for (int i = 0; i < registros.size(); i++) {
					Object[] paramsGen =null;
					paramsGen = (Object[]) registros.get(i);
					gnActual =((String) paramsGen[3]);
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

	@Override
	public CultivosEntidadesHistorico getUltimoHistoricoComision(final int plan, final int linea, final int entMed,
			final int subEntMed, final Calendar fecha, final String gn) throws Exception {

		logger.debug("init - getUltimoHistoricoComision");
		Session session = obtenerSession();
		CultivosEntidadesHistorico cultivo = null;
		try {
			List resultado = new ArrayList();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String fecEfecto = df.format(fecha.getTime());
			StringBuilder sql = new StringBuilder(" Select hco.PCTRGA as pctAdministracion, hco.PCTADQUISICION ")
					.append("from O02AGPE0.TB_COMS_CULTIVOS_ENTS_HIST hco ")
					.append("inner join O02AGPE0.TB_LINEAS l on hco.LINEASEGUROID=l.LINEASEGUROID ")
					.append("where l.CODPLAN=").append(plan)
					.append(" and l.CODLINEA=").append(linea)
					.append(" and hco.ENTMEDIADORA=").append(entMed)
					.append(" and hco.SUBENTMEDIADORA=").append(subEntMed)
					.append(StringUtils.isNullOrEmpty(gn) ? "" : " and hco.grupo_negocio in (" + gn + ", 9)")
					.append(" and hco.FECHA_EFECTO<= TO_DATE('").append(fecEfecto)
					.append("', 'YYYY-MM-DD') order by hco.grupo_negocio asc, hco.ID desc");

			logger.info("sql " + sql.toString());

			resultado = session.createSQLQuery(sql.toString()).list();

			if (resultado != null && resultado.size() > 0) {
				BigDecimal pctAdmon = (BigDecimal) ((Object[]) resultado.get(0))[0];
				BigDecimal pctAdq = (BigDecimal) ((Object[]) resultado.get(0))[1];

				cultivo = new CultivosEntidadesHistorico();
				cultivo.setPctadministracion(pctAdmon);
				cultivo.setPctadquisicion(pctAdq);
			}
			logger.debug("fin - getUltimoHistoricoComision");
		} catch (Exception e) {
			logger.error("Error al acceder a bbdd - getUltimoHistoricoComision - PolizasPctComisionesDao");
			throw e;
		}
		return cultivo;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getComisionesESMed(Long lineaseguroid,BigDecimal entMed,
						BigDecimal subentMed,BigDecimal codLinea,BigDecimal codPlan
						,Date fechaRenovacion) throws Exception {
		
		logger.debug("init - getComisionesESMed");
		Session session = obtenerSession();
		List<Object[]> resultado = new ArrayList<Object[]>();
		try {
			String fecRe = "";
			// formateamos la fecha renovacion
			if (fechaRenovacion != null)
				fecRe = DateUtil.date2String(fechaRenovacion, "dd/MM/yy");
			
			String sql =" select sh.pctentidad,sh.pctmediador" +
						" from o02agpe0.tb_coms_cultivos_subentidades s, o02agpe0.tb_coms_cultivos_subs_hist sh"+
						"  where s.id = sh.idcomisionessubent";
						if (fechaRenovacion != null){
							sql += "  and ((fec_baja is null or to_date(current_date,'dd/mm/yy') < to_date(s.fec_baja,'dd/mm/yy'))  " +
								   "  and to_date( '" +fecRe + "','dd/mm/yy') >= to_date(sh.fec_efecto)) ";
						}else{
							sql += "  and ((fec_baja is null or to_date(current_date,'dd/mm/yy') < to_date(s.fec_baja,'dd/mm/yy'))  " +
								   "  and to_date( current_date ,'dd/mm/yy') >= to_date(sh.fec_efecto)) ";
						}
						sql += "  and s.lineaseguroid = " + lineaseguroid +
						"  and s.codentidad=" + entMed +
						"  and s.codsubentidad =" + subentMed +
						"  order by sh.fec_efecto desc ,sh.fechamodificacion desc";
			logger.info("sql " + sql);
			
			resultado = session.createSQLQuery(sql).list();
			
			if (resultado.size()>0) {
				return (Object[]) resultado.get(0);
			}else {
				// Si no hay resultados buscamos por linea generica (si existe)
				Linea linea = lineaDao.getLinea (new BigDecimal(999),codPlan);
				if (linea != null) {
					String sql2 =" select sh.pctentidad,sh.pctmediador" +
							"  from o02agpe0.tb_coms_cultivos_subentidades s, o02agpe0.tb_coms_cultivos_subs_hist sh"+
							"  where s.id = sh.idcomisionessubent";
							if (fechaRenovacion != null){
								sql2 +="  and ((fec_baja is null or to_date(current_date,'dd/mm/yy') < to_date(s.fec_baja,'dd/mm/yy'))" +
								"  and to_date('" +fecRe + "','dd/mm/yy') >= sh.fec_efecto) ";
							}else{
								sql2 +="  and ((fec_baja is null or to_date(current_date,'dd/mm/yy') < to_date(s.fec_baja,'dd/mm/yy'))" +
								"  and to_date(current_date,'dd/mm/yy') >= sh.fec_efecto) ";
							}
							sql2 +="  and s.lineaseguroid = " + linea.getLineaseguroid() +
							"  and s.codentidad=" + entMed +
							"  and s.codsubentidad =" + subentMed +
							"  order by sh.fec_efecto desc ,sh.fechamodificacion desc";
					logger.info("sql2 " + sql2);
					List<Object[]> resultado2 = new ArrayList<Object[]>();
					resultado2 = session.createSQLQuery(sql2).list();
					if (resultado2.size()>0) {
						return (Object[]) resultado2.get(0);
					}
				}
			}
			logger.debug("fin - getComisionesESMed");
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - getComisionesESMed- PolizasPctComisionesDao");
			throw e;
		}
		return null;
	}

	@Override
	public Descuentos getDescuentos(BigDecimal entidad,String oficina,
			BigDecimal entMed, BigDecimal subentMed,BigDecimal delegacion,BigDecimal codPlan, BigDecimal codLinea) throws Exception {
		List<BigDecimal> lista=new ArrayList<BigDecimal>();
		lista.add(new BigDecimal(oficina));
		return  getDescuentos(entidad,lista,entMed,  subentMed, delegacion, codPlan,  codLinea);
	}

	@Override @Deprecated
	public void updateDescuento(Long idpoliza,BigDecimal descuento)throws Exception {
		
			Session session = obtenerSession();
			
			try {
				String sql =" update tb_polizas_pct_comisiones c set c.PCTDESCELEGIDO ="+ descuento + 
						    " where c.IDPOLIZA = " + idpoliza;
				
				logger.info("sql " + sql);
				
				session.createSQLQuery(sql.toString()).executeUpdate();
				session.flush();
				
				// actualizar el historico
				
				
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - updateDescuento- PolizasPctComisionesDao");
			throw e;
		}	
	}
	
	@Override	
	public void updateDescuento(BigDecimal descuento, Long idPolizaPctComision)throws Exception {
		
		Session session = obtenerSession();
		
		try {
			String sql =" update tb_polizas_pct_comisiones c set c.PCTDESCELEGIDO ="+ descuento + 
					    WHERE_C_ID + idPolizaPctComision;
			
			logger.info("sql " + sql);
			
			session.createSQLQuery(sql.toString()).executeUpdate();
			session.flush();
			
			// actualizar el historico
			
			
	}catch (Exception e ) {
		logger.error("Error al acceder a bbdd - updateDescuento- PolizasPctComisionesDao");
		throw e;
	}	
}
	
	
	@Override
	public void updateRecargo(BigDecimal recargo, Long idPolizaPctComision)throws Exception {
		
			Session session = obtenerSession();
			
			try {
				String sql =" update tb_polizas_pct_comisiones c set c.PCTRECARELEGIDO ="+ recargo + 
						    WHERE_C_ID + idPolizaPctComision;
				
				logger.info("sql " + sql);
				
				session.createSQLQuery(sql.toString()).executeUpdate();
				session.flush();
						
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - updateRecargo- PolizasPctComisionesDao ");
			throw e;
		}	
	}
	
	@Override @Deprecated
	public void updateRecargo(Long idpoliza,BigDecimal recargo)throws Exception {
		
			Session session = obtenerSession();
			
			try {
				String sql =" update tb_polizas_pct_comisiones c set c.PCTRECARELEGIDO ="+ recargo + 
						    " where c.IDPOLIZA = " + idpoliza;
				
				logger.info("sql " + sql);
				
				session.createSQLQuery(sql.toString()).executeUpdate();
				session.flush();
						
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - updateRecargo-  PolizasPctComisionesDao");
			throw e;
		}	
	}
	
	@Override	
	public void updateDescuentoAnexo(BigDecimal descuento, Long idAnexo, Character grupoNegocio) throws Exception {
		
		Session session = obtenerSession();
		
		try {
			String sql =" update tb_anexo_mod c set c.PCTDESCELEGIDO" + (grupoNegocio.equals(Constants.GRUPO_NEGOCIO_VIDA) ? "_RESTO" : "") + " ="+ descuento + 
					    WHERE_C_ID + idAnexo;
			
			logger.info("sql: " + sql);
			
			session.createSQLQuery(sql.toString()).executeUpdate();
			//session.flush();	
			
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - updateDescuentoAnexo- PolizasPctComisionesDao",e);
			//throw e;
		}	
	}
	
	@Override	
	public void updateRecargoAnexo(BigDecimal recargo, Long idAnexo, Character grupoNegocio)throws Exception {
		
		Session session = obtenerSession();
		
		try {
			String sql =" update tb_anexo_mod c set c.PCTRECARELEGIDO" + (grupoNegocio.equals(Constants.GRUPO_NEGOCIO_VIDA) ? "_RESTO" : "") + " =" + recargo + 
					    WHERE_C_ID + idAnexo;
			
			logger.info("sql " + sql);
			
			session.createSQLQuery(sql.toString()).executeUpdate();
			//session.flush();	
			
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - updateRecargoAnexo- PolizasPctComisionesDao",e);
			//throw e;
		}	
	}
	
	@Override	
	public void updatePorcentajesAnexo(es.agroseguro.iTipos.Gastos gas, Long idAnexo)throws Exception {
		
		Session session = obtenerSession();
		
		boolean isResto = Constants.GRUPO_NEGOCIO_VIDA.equals(gas.getGrupoNegocio().charAt(0));
		
		try {
			String sql =" update tb_anexo_mod c set c.pctadministracion" + (isResto ? "_resto" : "") + " =" + gas.getAdministracion() + 
					    ", c.pctadquisicion" + (isResto ? "_resto" : "") + " =" + gas.getAdquisicion() +
					    ", c.pctcomisionmediador" + (isResto ? "_resto" : "") + " =" + gas.getComisionMediador() +
					    WHERE_C_ID + idAnexo;
			
			logger.info("sql " + sql);
			
			session.createSQLQuery(sql.toString()).executeUpdate();
			//session.flush();	
			
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - updateRecargoAnexo- PolizasPctComisionesDao",e);
			//throw e;
		}	
	}
	
	
	
	@Override
	public void insertHistoricoDescuento(Poliza poliza, BigDecimal pctDescuento,Usuario usu) throws Exception {
		try {
			PolizaHistoricoDescuentos phd = new PolizaHistoricoDescuentos();
			phd.setFecha(new Date());
			phd.setPctDescuento(pctDescuento);
			phd.setPoliza(poliza);
			phd.setUsuario(usu.getCodusuario());
			this.saveOrUpdate(phd);
			
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - insertHistoricoDescuento- PolizasPctComisionesDao");
			throw e;
		}	
		
	}
	
	public String getDescripcionGrupoNegocio(Character codGrupoNeg) throws DAOException{
		String sql=null;
		String res = null;
		try {
			sql ="SELECT DESCRIPCION from TB_SC_C_GRUPOS_NEGOCIO WHERE GRUPO_NEGOCIO= " + codGrupoNeg;

			logger.debug(sql);
			Session session = obtenerSession();			
			res = (String) session.createSQLQuery(sql).uniqueResult();
									
			return res;
		}catch (Exception ex) {
			logger.error("PolizasPctComisionesDao.getDescripcionGrupoNegocio. - ", ex);
			throw new DAOException("PolizasPctComisionesDao.getDescripcionGrupoNegocio. - ", ex);
		}	
		
	}
	
	public void updatePctComs(PolizaPctComisiones pctComs) throws Exception{
		Session session = obtenerSession();		
		try {
			String sql ="UPDATE TB_POLIZAS_PCT_COMISIONES "+
			"set PCTADMINISTRACION="+pctComs.getPctadministracion()+
			", PCTADQUISICION=" +pctComs.getPctadquisicion()+
			", PCTCOMMAX=" +pctComs.getPctcommax()+
			", PCTENTIDAD=" +pctComs.getPctentidad()+
			", PCTESMEDIADORA= " +pctComs.getPctesmediadora()+
			"WHERE IDPOLIZA="+pctComs.getPoliza().getIdpoliza()+" and grupo_negocio='"+pctComs.getGrupoNegocio()+"'";
			
			logger.info("sql " + sql);
			
			session.createSQLQuery(sql.toString()).executeUpdate();
			session.flush();
					
	}catch (Exception e ) {
		logger.error("Error al acceder a bbdd - updateRecargo- PolizasPctComisionesDao");
		throw e;
	}	
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}



	@SuppressWarnings("unchecked")
	@Override
	public Descuentos getDescuentos(BigDecimal entidad,
			List<BigDecimal> codOficinas, BigDecimal entMed, BigDecimal subentMed,
			BigDecimal delegacion, BigDecimal codPlan, BigDecimal codLinea)
					throws Exception {

		try {
			Date fechaActual = new Date();
			//Busqueda por plan y linea específicos
			Session session = obtenerSession();

			Criteria criteria = session.createCriteria(Descuentos.class);
			criteria.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
			criteria.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
			criteria.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
			criteria.createAlias(LINEA, LINEA);

			criteria.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
			criteria.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
			criteria.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
			criteria.add(Restrictions.in(OFICINA_ID_CODOFICINA,codOficinas));
			criteria.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
			criteria.add(Restrictions.eq(LINEA_CODLINEA, codLinea));
			criteria.add(Restrictions.eq(DELEGACION, delegacion));			
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.isNull(FECHA_BAJA))
					.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
			criteria.addOrder(Order.desc(PCT_DESC_MAX));
			List<Descuentos> listaD=criteria.list();
			if (null!=listaD && listaD.size()>0){
				logger.debug("obtiene datos en criteria");
				if(listaD.size()==1)
					return (Descuentos)listaD.get(0) ;
				else if(listaD.size()>1)
					return DescuentoConPermitirRecargo(listaD);	
			}else {
				// buscamos por delegacion null
				Criteria criteria2 = session.createCriteria(Descuentos.class);
				criteria2.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
				criteria2.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
				criteria2.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
				criteria2.createAlias(LINEA, LINEA);

				criteria2.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
				criteria2.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
				criteria2.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
				criteria2.add(Restrictions.in(OFICINA_ID_CODOFICINA, codOficinas));
				criteria2.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
				criteria2.add(Restrictions.eq(LINEA_CODLINEA, codLinea));
				criteria2.add(Restrictions.isNull(DELEGACION));
				criteria2.add(Restrictions.disjunction()
						.add(Restrictions.isNull(FECHA_BAJA))
						.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
				criteria2.addOrder(Order.desc(PCT_DESC_MAX));
				listaD=criteria2.list();
				if (null!=listaD && listaD.size()>0){
					logger.debug("obtiene datos en criteria 2");
					if(listaD.size()==1)
						return (Descuentos)listaD.get(0) ;
					else if(listaD.size()>1)
						return DescuentoConPermitirRecargo(listaD);	
				}else {
					// buscamos por oficina genérica 
					Criteria criteria3 = session.createCriteria(Descuentos.class);
					criteria3.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
					criteria3.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
					criteria3.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
					criteria3.createAlias(LINEA, LINEA);

					criteria3.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
					criteria3.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
					criteria3.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
					criteria3.add(Restrictions.eq(OFICINA_ID_CODOFICINA, new BigDecimal(-1)));
					criteria3.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
					criteria3.add(Restrictions.eq(LINEA_CODLINEA, codLinea));
					criteria3.add(Restrictions.eq(DELEGACION, delegacion));
					criteria3.add(Restrictions.disjunction()
							.add(Restrictions.isNull(FECHA_BAJA))
							.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
					criteria3.addOrder(Order.desc(PCT_DESC_MAX));
					listaD=criteria3.list();
					if (null!=listaD && listaD.size()>0){
						logger.debug("obtiene datos en criteria 3");
						if(listaD.size()==1)
							return (Descuentos)listaD.get(0) ;
						else if(listaD.size()>1)
							return DescuentoConPermitirRecargo(listaD);	
					}else{
						// buscamos por oficina genérica y delegacion null 
						Criteria criteria4 = session.createCriteria(Descuentos.class);
						criteria4.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
						criteria4.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
						criteria4.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
						criteria4.createAlias(LINEA, LINEA);

						criteria4.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
						criteria4.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
						criteria4.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
						criteria4.add(Restrictions.eq(OFICINA_ID_CODOFICINA, new BigDecimal(-1)));
						criteria4.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
						criteria4.add(Restrictions.eq(LINEA_CODLINEA, codLinea));
						criteria4.add(Restrictions.isNull(DELEGACION));
						criteria4.add(Restrictions.disjunction()
								.add(Restrictions.isNull(FECHA_BAJA))
								.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
						criteria4.addOrder(Order.desc(PCT_DESC_MAX));
						listaD=criteria4.list();
						if (null!=listaD && listaD.size()>0){
							logger.debug("obtiene datos en criteria 4");
							if(listaD.size()==1)
								return (Descuentos)listaD.get(0) ;
							else if(listaD.size()>1)
								return DescuentoConPermitirRecargo(listaD);	
						}else{// Busqueda por plan específico y linea genérica
							Criteria criteria5 = session.createCriteria(Descuentos.class);
							criteria5.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
							criteria5.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
							criteria5.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
							criteria5.createAlias(LINEA, LINEA);

							criteria5.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
							criteria5.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
							criteria5.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
							criteria5.add(Restrictions.in(OFICINA_ID_CODOFICINA, codOficinas));
							criteria5.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
							criteria5.add(Restrictions.eq(LINEA_CODLINEA, new BigDecimal(999)));
							criteria5.add(Restrictions.eq(DELEGACION, delegacion));
							criteria5.add(Restrictions.disjunction()
									.add(Restrictions.isNull(FECHA_BAJA))
									.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
							criteria5.addOrder(Order.desc(PCT_DESC_MAX));
							listaD=criteria5.list();
							if (null!=listaD && listaD.size()>0){
								logger.debug("obtiene datos en criteria 5");
								if(listaD.size()==1)
									return (Descuentos)listaD.get(0) ;
								else if(listaD.size()>1)
									return DescuentoConPermitirRecargo(listaD);	
							}else {
								// buscamos por delegacion null
								Criteria criteria6 = session.createCriteria(Descuentos.class);
								criteria6.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
								criteria6.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
								criteria6.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
								criteria6.createAlias(LINEA, LINEA);

								criteria6.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
								criteria6.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
								criteria6.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
								criteria6.add(Restrictions.in(OFICINA_ID_CODOFICINA,codOficinas));
								criteria6.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
								criteria6.add(Restrictions.eq(LINEA_CODLINEA, new BigDecimal(999)));
								criteria6.add(Restrictions.isNull(DELEGACION));
								criteria6.add(Restrictions.disjunction()
										.add(Restrictions.isNull(FECHA_BAJA))
										.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
								criteria6.addOrder(Order.desc(PCT_DESC_MAX));
								listaD=criteria6.list();
								if (null!=listaD && listaD.size()>0){
									logger.debug("obtiene datos en criteria 6");
									if(listaD.size()==1)
										return (Descuentos)listaD.get(0) ;
									else if(listaD.size()>1)
										return DescuentoConPermitirRecargo(listaD);										}else {
											// buscamos por oficina genérica 
											Criteria criteria7 = session.createCriteria(Descuentos.class);
											criteria7.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
											criteria7.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
											criteria7.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
											criteria7.createAlias(LINEA, LINEA);

											criteria7.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
											criteria7.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
											criteria7.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
											criteria7.add(Restrictions.eq(OFICINA_ID_CODOFICINA, new BigDecimal(-1)));
											criteria7.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
											criteria7.add(Restrictions.eq(LINEA_CODLINEA, new BigDecimal(999)));
											criteria7.add(Restrictions.eq(DELEGACION, delegacion));
											criteria7.add(Restrictions.disjunction()
													.add(Restrictions.isNull(FECHA_BAJA))
													.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
											criteria7.addOrder(Order.desc(PCT_DESC_MAX));
											listaD=criteria7.list();
											if (null!=listaD && listaD.size()>0){
												logger.debug("obtiene datos en criteria 7");
												if(listaD.size()==1)
													return (Descuentos)listaD.get(0) ;
												else if(listaD.size()>1)
													return DescuentoConPermitirRecargo(listaD);	
											}else{
												// buscamos por oficina genérica y delegacion null 
												Criteria criteria8 = session.createCriteria(Descuentos.class);
												criteria8.createAlias(SUBENTIDAD_MEDIADORA, SUBENTIDAD_MEDIADORA);
												criteria8.createAlias(SUBENTIDAD_MEDIADORA_ENTIDAD, ENTIDAD);
												criteria8.createAlias(SUBENTIDAD_MEDIADORA_ID, "id");
												criteria8.createAlias(LINEA, LINEA);

												criteria8.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidad));
												criteria8.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, entMed));
												criteria8.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, subentMed));
												criteria8.add(Restrictions.eq(OFICINA_ID_CODOFICINA, new BigDecimal(-1)));
												criteria8.add(Restrictions.eq(LINEA_CODPLAN, codPlan));
												criteria8.add(Restrictions.eq(LINEA_CODLINEA, new BigDecimal(999)));
												criteria8.add(Restrictions.isNull(DELEGACION));
												criteria8.add(Restrictions.disjunction()
														.add(Restrictions.isNull(FECHA_BAJA))
														.add(Restrictions.gt(FECHA_BAJA, fechaActual)));
												criteria8.addOrder(Order.desc(PCT_DESC_MAX));
												listaD=criteria8.list();
												if (null!=listaD && listaD.size()>0){
													logger.debug("obtiene datos en criteria 8");
													if(listaD.size()==1)
														return (Descuentos)listaD.get(0) ;
													else if(listaD.size()>1)
														return DescuentoConPermitirRecargo(listaD);	
												}
											}
										}
							}		
						}
					}

				}

			}
		}catch (Exception e ) {
			logger.error("Error al acceder a bbdd - getDescuentos- PolizasPctComisionesDao");
			throw e;
		}	
		return null;
	}

	private Descuentos DescuentoConPermitirRecargo(List<Descuentos> listaD){
		boolean uno=false;
		Descuentos d=null;
		if(listaD.size()>0)
			d=listaD.get(0);
		for(Descuentos descuento:listaD){
			if(descuento.getPermitirRecargo().equals(new Integer(1))){
				uno=true;
				break;
			}
			
		}
		if(uno)
			d.setPermitirRecargo(new Integer(1));
		return d;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updatePctComsMaxCalculada(final Poliza poliza) throws DAOException {
		Set<ModuloPoliza> mpLst = poliza.getModuloPolizas();
		List<PolizaPctComisiones> comisiones = poliza.getLstPolizaPctComisiones();	
		for (ModuloPoliza mp : mpLst) {
			// OBTENEMOS LOS POSIBLES % CALCULADOS POR CADA COMPARATIVA (DEBERIA HABER SOLO UNA EN ESTE PUNTO) 
			// PUEDEN SER VARIOS DISCRIMINANDO POR GRUPO DE NEGOCIO
			List<ComsPctCalculado> pctCalcLst = this.findFiltered(ComsPctCalculado.class, "id.idComparativa", mp.getId().getNumComparativa());
			if (pctCalcLst != null && !pctCalcLst.isEmpty()) {
				for (ComsPctCalculado pctCalc : pctCalcLst) {
					// ACTUALIZAMOS EL % DE COMISION EN EL REGISTRO DEL GRUPO DE NEGOCIO CORRESPONDIENTE
					for (PolizaPctComisiones comision : comisiones) {
						if (comision.getGrupoNegocio().equals(pctCalc.getId().getIdGrupo())) {
							comision.setPctcommax(pctCalc.getPctCalculado());
							this.saveOrUpdate(comision);
						}
					}
				}
			}
		}
	}
}
