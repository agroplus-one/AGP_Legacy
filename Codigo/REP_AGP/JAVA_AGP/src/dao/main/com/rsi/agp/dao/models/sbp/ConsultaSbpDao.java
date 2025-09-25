package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ConsultaPolizaSbpFilter;
import com.rsi.agp.core.jmesa.sort.ConsultaPolizaSbpSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ErrorSbp;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

@SuppressWarnings("unchecked")
public class ConsultaSbpDao extends BaseDaoHibernate implements IConsultaSbpDao  {

	public List<PolizaSbp> getListaPolizasSbp(Long idPoliza, boolean complementaria){
		List<PolizaSbp> lstPolizasSbp = new ArrayList<PolizaSbp>();
		Session session = obtenerSession();
		try{
			Criteria criteria = session.createCriteria(PolizaSbp.class);
			if (complementaria){
				criteria.add(Restrictions.eq("polizaCpl.idpoliza", idPoliza));
			}else{
				criteria.add(Restrictions.eq("polizaPpal.idpoliza", idPoliza));
			}
			lstPolizasSbp= criteria.list();
		} catch (Exception ex) {
			logger.error("[PolizaDao] dameListaPolizasSbpByPpl - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return lstPolizasSbp;
	}
	
	public List<Sobreprecio> getLineasSobrePrecio(){
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			return criteria.list();
		} catch (Exception ex) {
			logger.error(ex);
		}
		return null; 
	}
	
	@Override
	public int getConsultaPolizaSbpCountWithFilter(final ConsultaPolizaSbpFilter filter, final String nombreAseg,
			final List<Long> lstLineasSbp) {
		logger.debug("init - [ConsultaSbpDao] getConsultaPolizaSbpCountWithFilter");
		Session session = obtenerSession();
		String lineasStr = "";
		String sql = filter.getSqlInnerJoin(nombreAseg);
		sql += filter.getSqlWhere();
		if (nombreAseg != null && !nombreAseg.equals("")){
			sql += " AND ( upper(A.nombre ||A.Apellido1||A.Apellido2) like upper('%"+nombreAseg+"%')";
			sql += " OR UPPER(A.RAZONSOCIAL) LIKE UPPER('%"+nombreAseg+"%'))";	
        }
        // filtro por lineas de Sobreprecio
		for (Long lin: lstLineasSbp){
			if (lineasStr.length()>1){
				lineasStr += ","+lin.toString();
			}else{
				lineasStr += lin.toString();
			}
		} 	
		
		if (!"".equals(lineasStr)) {
			sql += " AND P.LINEASEGUROID IN(" + lineasStr + ")";
		}
		logger.debug("Valor de la sql:"+sql);
		
		logger.debug("end - [ConsultaSbpDao] getConsultaPolizaSbpCountWithFilter");
		return ( (BigDecimal)session.createSQLQuery(sql).list().get(0) ).intValue();
    }
	
	@Override
	@SuppressWarnings("all")
	public Collection<Poliza> getConsultaPolizasSbpWithFilterAndSort(final ConsultaPolizaSbpFilter filter,final  ConsultaPolizaSbpSort sort,
			final int rowStart, final int rowEnd, final String nombreAseg, final List<Long> lstLineasSbp) throws BusinessException {
		
		try{
		logger.debug("init - [ConsultaSbpDao] getConsultaPolizasSbpWithFilterAndSort");
			List<Poliza> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	
                Criteria criteria = session.createCriteria(Poliza.class);     
                // Alias
                criteria.createAlias("colectivo", "col");
				criteria.createAlias("asegurado", "ase");
				criteria.createAlias("linea", "lin");
				criteria.createAlias("usuario", "usu");				
				criteria.createAlias("estadoPoliza","est");
				
        		// Filtro
                criteria = filter.execute(criteria);
                // filtro por nombreAsegurado
                if (nombreAseg != null && !nombreAseg.equals("")){
                	criteria.add(
                	Restrictions.disjunction()
						.add(Restrictions.sqlRestriction("upper(nombre||' '||apellido1||' '||apellido2) like upper('%"+nombreAseg+"%')"))
						.add(Restrictions.sqlRestriction("upper(razonsocial) like upper('%"+nombreAseg+"%')"))
                	);
                }
                // filtro por lineas de Sobreprecio
                if (lstLineasSbp != null && !lstLineasSbp.isEmpty()) {
                	criteria.add(Restrictions.in("lin.lineaseguroid", lstLineasSbp));
                }
                
        		/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
        		criteria.add(Restrictions.ne("ase.isBloqueado", Integer.valueOf(1)));
        		/* Pet. 62719 ** MODIF TAM (19.01.2021) ** Fin */
                
                // Ordenacion
                criteria = sort.execute(criteria);
                // Primer registro
                criteria.setFirstResult(rowStart);
                // Numero maximo de registros a mostrar
                criteria.setMaxResults(rowEnd - rowStart);
                // Devuelve el listado de polizas
                return criteria.list();
            }
        });
		logger.debug("end - [ConsultaSbpDao] getConsultaPolizasSbpWithFilterAndSort");
        return applications;
		}catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
		
	}

	/**
	 * @param Objeto que encapsula el filtro para polizas de sobreprecio
	 * @return Listado de polizas de sobreprecio que se ajustan al filtro
	 */
	public List<PolizaSbp> consultaPolizaSobreprecio(PolizaSbp polizaSbp)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(PolizaSbp.class);
		
			Poliza polPpal = null;
			criteria.createAlias("polizaPpal", "polPpal");
			criteria.createAlias("polizaCpl", "polCpl");
			criteria.createAlias("polizaPpal.asegurado", "asegPpal");
			criteria.createAlias("polizaCpl.asegurado", "asegCpl");
			if (null != polizaSbp.getEstadoPlzSbp()){
				criteria.add(Restrictions.eq("estadoPlzSbp", polizaSbp.getEstadoPlzSbp()));
			}
			
			if (null != polizaSbp.getFechaEnvioSbp()){
				criteria.add(Restrictions.eq("fechaEnvioSbp", polizaSbp.getFechaEnvioSbp()));
			}
			
			if (null != polizaSbp.getEstadoPlzSbp()){
				criteria.add(Restrictions.eq("estadoPlzSbp", polizaSbp.getEstadoPlzSbp()));
			}
			if (null != polizaSbp.getPolizaPpal()){
				polPpal = polizaSbp.getPolizaPpal();
				if (polPpal != null){ 
					if (null!=polPpal.getAsegurado()){
						if (null!=polPpal.getAsegurado().getEntidad()){
							criteria.add(Restrictions.eq("asegPpal.entidad",polPpal.getAsegurado().getEntidad()));
						}
					}
					if (null != polPpal.getOficina()){
						criteria.add(Restrictions.eq("polPpal.oficina",polPpal.getOficina()));
					}
					if (null != polPpal.getClase()) {
						criteria.add(Restrictions.eq("polPpal.clase", polPpal.getClase()));
					}	
					
					if (null != polPpal.getLinea().getCodplan()) {
						criteria.add(Restrictions.eq("polPpal.lin.codplan", polPpal.getLinea().getCodplan()));
					}
					
					if (null != polPpal.getLinea().getCodlinea()) {
						criteria.add(Restrictions.eq("polPpal.lin.codlinea", polPpal.getLinea().getCodlinea()));
					}
					if (null != polPpal.getReferencia()) {
						
							criteria.add(Restrictions.eq("polPpal.referencia", polPpal.getReferencia()));
						}
				}
				if (polizaSbp.getFechaEnvioSbp()!= null){
					Date fechaMas24 = new Date();
					GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
					fechaEnvioGrMas24.setTime(polizaSbp.getFechaEnvioSbp());
					fechaEnvioGrMas24.add(Calendar.HOUR,24);
					fechaMas24 = fechaEnvioGrMas24.getTime();
					criteria.add(Restrictions.ge("fechaenvio", polizaSbp.getFechaEnvioSbp()));
					criteria.add(Restrictions.lt("fechaenvio", fechaMas24));
				}	
				if(polizaSbp.getEstadoPlzSbp()!=null){
					criteria.add(Restrictions.eq("estadoPlzSbp.idestado", polizaSbp.getEstadoPlzSbp()));
				}
			
			}
			return criteria.list();
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					ex);
		}
		
		
	}
	
	public List<EstadoPlzSbp> getEstadosPolSbp(BigDecimal[] estadosPolizaExcluir){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(EstadoPlzSbp.class);
		
		if(estadosPolizaExcluir != null){
			if(estadosPolizaExcluir.length > 0){
				criteria.add(Restrictions.not(Restrictions.in("idestado", estadosPolizaExcluir)));
			}
		}
			
		return criteria.list();
	}
	
	public List<ErrorSbp> getDetalleErroresSbp(BigDecimal[] detalleErroresExcluir){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ErrorSbp.class);
		
		if(detalleErroresExcluir != null){
			if(detalleErroresExcluir.length > 0){
				criteria.add(Restrictions.not(Restrictions.in("iderror", detalleErroresExcluir)));
			}
		}
			
		return criteria.list();
	}
	
	public Map<Long, List<BigDecimal>> getCultivosPorLineaseguroid(BigDecimal codPlan){
		Map<Long, List<BigDecimal>> cultivosPorLinea = new HashMap<Long, List<BigDecimal>>();
		
		Session session = obtenerSession();
		Date fechahoy = new Date();
			Criteria criteria = session.createCriteria(FechaContratacionSbp.class);
			criteria.createAlias("linea", "lin");
			criteria.createAlias("cultivo", "cult");
			criteria.add(Restrictions.eq("lin.codplan", codPlan));
			criteria.add(Restrictions.le("fechainicio", fechahoy));
			criteria.add(Restrictions.ge("fechafin", fechahoy));
			criteria.addOrder(Order.asc("lin.lineaseguroid"));
			criteria.addOrder(Order.asc("cult.id.codcultivo"));
			List<FechaContratacionSbp> lstFechasContrat = criteria.list();
			if (lstFechasContrat!=null){
				List<Long> lstLineaseguroid = new ArrayList<Long>(); // lista lineaseguroid
				for (FechaContratacionSbp fec: lstFechasContrat){
					Long lin = fec.getLinea().getLineaseguroid();
					if (!lstLineaseguroid.contains(lin)){ // inserto las lienaseguroid nuevas
						lstLineaseguroid.add(lin);
						//logger.debug("LineaSegId nueva: " + lin);
						List<BigDecimal> listCultivos = new ArrayList<BigDecimal>(); // lista cultivos en fecha
						for (FechaContratacionSbp fecTemp: lstFechasContrat){
							BigDecimal cult = fecTemp.getCultivo().getId().getCodcultivo();			
							if (lin.toString().equals(fecTemp.getLinea().getLineaseguroid().toString()) && !listCultivos.contains(cult)){
								listCultivos.add(cult);
								//logger.debug("LineaSegId: " + lin + " cult:" + cult);
							}
						}
						cultivosPorLinea.put(lin, listCultivos);
					}
					
				}									
			}						
		return cultivosPorLinea;
	}
	
}
