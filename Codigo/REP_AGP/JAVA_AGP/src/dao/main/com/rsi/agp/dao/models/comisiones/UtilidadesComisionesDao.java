package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

@SuppressWarnings("rawtypes")
public class UtilidadesComisionesDao extends BaseDaoHibernate implements IUtilidadesComisionesDao {

	private static final String DD_MM_YYYY = "', 'dd/MM/yyyy')";
	private static final String AND_COM_LINEASEGUROID = "AND com.LINEASEGUROID = ";
	private static final String FROM_TB_COMS_CULTIVOS_SUBS_HIST_COM = "FROM TB_COMS_CULTIVOS_SUBS_HIST com ";
	private static final String CLAVE_COL_PORCENTAJE_MAXIMO = "Clave colPorcentajeMaximo: ";
	private static final String REGISTROS = "Registros: ";
	private static final String CLAVE_COL_PORCENTAJES_APLICAR = "Clave colPorcentajesAplicar: ";
	private static final String WHERE_1_1 = "WHERE 1=1 ";
	private static final String REFERENCIA = "referencia";
	
	private static final Log LOG = LogFactory.getLog(UtilidadesComisionesDao.class);

	/**
	 * Devuelve los porcentajes de entidad y E-S mediadora encapsulados en un array de tipo BigDecimal
	 * resultado[0] = porcentaje de la entidad
	 * resultado[1] = porcentaje de la E-S mediadora
	 * @param refPoliza Referencia de la póliza
	 * @return
	 * @throws DAOException
	 */
	public BigDecimal[] obtenerPorcentajesComision(String refPoliza, Date fechaEmision) throws DAOException{
		BigDecimal resultado[] = null;
		
		try{
			resultado = this.getPorcentajesComision(refPoliza, null, null, null, fechaEmision);		
		}catch (Exception excepcion) {
			LOG.error("Se ha producido un error al obtener los porcentajes de comisiones de la póliza: " + refPoliza + ". ", excepcion);
		}		
		return resultado;
	}
	
	/**
	 * Método creado para las comisiones unificadas
	 * @param refPoliza
	 * @param colColectivos
	 * @param colPorcentajesAplicar
	 * @param colPorcentajeMaximo
	 * @param colLineas
	 * @return
	 * @throws DAOException
	 */
	public PorcentajesAplicar obtenerPorcentajesComision(String refPoliza, Character tipoRef, Long codPlan, Character grupoNegocio,
			Map<String, ColectivosRenovacion> colColectivos,Map<String, PorcentajesAplicar> colPorcentajesAplicar,
			Map<String, BigDecimal> colPorcentajeMaximo, Map<String, Linea> colLineas, AplicacionUnificado aplicacion, Date fechaEmision) throws DAOException{
		BigDecimal resultado[] = null;
		PorcentajesAplicar porcentajes=null;
		try{
			PolizaRenovable polizaRenovable=this.getPolizaRenovable(refPoliza, codPlan);
			
			if (null==polizaRenovable){
				 resultado = this.getPorcentajesComision(refPoliza, tipoRef, codPlan, grupoNegocio, fechaEmision);
				 if (null!=resultado && resultado.length>0) {
					 porcentajes= new PorcentajesAplicar(resultado[0], resultado[1], resultado[2], resultado[3].intValue());
				 }
			}else {
				porcentajes=this.getPorcentajesComisionRenovables(polizaRenovable,
						colColectivos, colPorcentajesAplicar,colPorcentajeMaximo, colLineas);
			}
			if(null!= porcentajes && null!=porcentajes.getSinPorcentajes()){
				aplicacion.setErrSinComisiones(porcentajes.getSinPorcentajes());
			}else{
				aplicacion.setErrSinComisiones(new Integer(1));
			}
			
			
		}catch (Exception excepcion) {
			LOG.error("Se ha producido un error al obtener los porcentajes de comisiones de la póliza: " + refPoliza + ". ", excepcion);
		}
		
		return porcentajes;
	}	
	
	
	@SuppressWarnings("unchecked")
	private PolizaRenovable getPolizaRenovable(String refPoliza, Long codPlan) {
		// Las pólizaas renovables se encuentran en la tabla
		// TB_POLIZAS_RENOVABLES pero también pueden estar en TB_POLIZAS aunque
		// no a la inversa. Por esta razón iremos primero a ver si existen en la
		// tabla de renovables y si no daremos por hecho
		// que es una póliza normal NO RENOVABLE
		PolizaRenovable res = null;
		List<PolizaRenovable> aux = (List<PolizaRenovable>) this.getObjects(
				PolizaRenovable.class, REFERENCIA, refPoliza);
		if (aux != null) {
			for (PolizaRenovable resAux : aux) {
				if (codPlan.equals(resAux.getPlan())) {
					res = resAux;
					break;
				}
			}
		}
		return res;
	}
	
	private ColectivosRenovacion getColectivo(String referencia, Map<String, ColectivosRenovacion> colColectivos) {
		ColectivosRenovacion res=null;
		if(colColectivos.containsKey(referencia)) {
			res=colColectivos.get(referencia);
		}else {
			res=(ColectivosRenovacion) this.getObject(ColectivosRenovacion.class, REFERENCIA, referencia);			
			colColectivos.put(referencia, res);			
		}
 		return (ColectivosRenovacion)res;
	}
	
	@SuppressWarnings("unchecked")
	private Linea getLinea(BigDecimal codPlan, BigDecimal codLinea, Map<String, Linea> colLinea) {
		Linea res=null;
		
		String key=codPlan.toString() + codLinea.toString();
		if(colLinea.containsKey(key)) {
			res=colLinea.get(key);
		}else {
			LineasFiltro filtro=new LineasFiltro(codPlan, codLinea);
			List<Linea> lista=null;
			lista=(List<Linea>)this.getObjects(filtro);
			if(lista!=null && lista.size()>0) {
				res=lista.get(0);
				colLinea.put(key, res);
			}
		}
		
		return res;
	}
	
	private BigDecimal[] getPorcentajesComisionUnif17(String refPoliza, Character tipoRef, Long codPlan,
			Character grupoNegocio, Date fechaEmision) throws DAOException {

		final Pattern entidadPattern = Pattern.compile("[38][0-9][0-9][0-9]"); // 3xxx-0 ó 8xxx-0
		final BigDecimal cien = new BigDecimal("100.00");
		
		BigDecimal resultado[] = new BigDecimal[6];

		BigDecimal pctEntidad = BigDecimal.ZERO;
		BigDecimal pctESmediadora = BigDecimal.ZERO;
		BigDecimal pctComMax = BigDecimal.ZERO;
		BigDecimal pctDescelegido =  BigDecimal.ZERO;
		BigDecimal pctrRecarelegido =  BigDecimal.ZERO;

		if (refPoliza != null && !"".equals(refPoliza)) {

			boolean ajustar = false;

			try {
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT nvl(hc.entmediadora, col.entmediadora) as entmediadora, nvl(hc.subentmediadora, col.subentmediadora) as subentmediadora, ");
				sql.append("com.pctentidad, com.pctesmediadora, com.pctcommax, com.pctdescelegido, com.pctrecarelegido ");
				sql.append("FROM O02AGPE0.TB_POLIZAS_PCT_COMISIONES com ");
				sql.append("inner join O02AGPE0.TB_POLIZAS pol on pol.referencia = :referencia and pol.tiporef = :tipoRef and pol.idestado >= 8 and pol.idpoliza = com.idpoliza ");
				sql.append("inner join O02AGPE0.TB_LINEAS lin on lin.codplan = :plan and pol.lineaseguroid = lin.lineaseguroid ");
				sql.append("inner join O02AGPE0.TB_COLECTIVOS col on pol.idcolectivo = col.id ");
				sql.append("left join (select aux.idcolectivo, max(aux.FECHAOPERACION) as fecha from O02AGPE0.TB_HISTORICO_COLECTIVOS aux where aux.fechaefecto <= :fechaEmision group by aux.idcolectivo) q on q.idcolectivo = col.id ");
				sql.append("left join O02AGPE0.TB_HISTORICO_COLECTIVOS hc on hc.idcolectivo = col.id and hc.fechaefecto <= :fechaEmision and hc.FECHAOPERACION = q.fecha ");
				sql.append(WHERE_1_1); 
				if (grupoNegocio != null) { 
					sql.append(" AND com.grupo_negocio = TO_CHAR(:grupoNegocio) "); 
				} 
				sql.append("AND ROWNUM = 1"); 

				SQLQuery query = obtenerSession()
						.createSQLQuery(sql.toString());
				
				query.setParameter(REFERENCIA, refPoliza);
				query.setParameter("tipoRef", tipoRef);
				query.setParameter("plan", codPlan);
				query.setParameter("fechaEmision", fechaEmision == null ? new Date() : fechaEmision);
				if (grupoNegocio != null) {					
					query.setParameter("grupoNegocio", grupoNegocio);
				}
				query.addScalar("entmediadora", Hibernate.STRING);
				query.addScalar("subentmediadora", Hibernate.STRING);
				query.addScalar("pctentidad", Hibernate.BIG_DECIMAL);
				query.addScalar("pctesmediadora", Hibernate.BIG_DECIMAL);
				query.addScalar("pctcommax", Hibernate.BIG_DECIMAL);
				query.addScalar("pctdescelegido", Hibernate.BIG_DECIMAL);
				query.addScalar("pctrecarelegido", Hibernate.BIG_DECIMAL);

				Object[] resultset = (Object[]) query.uniqueResult();

				if (resultset != null) {

					if ("0".equals((String) resultset[1])) { // El código de la E-S es 0

						if (entidadPattern.matcher((String) resultset[0]).matches()) { // 3xxx-0 ó 8xxx-0

							// En este caso, PCTENTIDAD=100 y PCTESMEDIADORA=0
							ajustar = true;
						}
					}

					if (resultset[4] != null)  pctComMax = new BigDecimal(resultset[4].toString());
					
					if (ajustar) {

						// 3xxx-0 ó 8xxx-0
						pctEntidad = cien;
						pctESmediadora = BigDecimal.ZERO;
						
					} else {

						if (resultset[2] != null) pctEntidad = new BigDecimal(resultset[2].toString());
						if (resultset[3] != null) pctESmediadora = new BigDecimal(resultset[3].toString());
						if (resultset[5] != null) pctDescelegido = new BigDecimal(resultset[5].toString());
						if (resultset[6] != null) pctrRecarelegido = new BigDecimal(resultset[6].toString());						
					}
					
					resultado[3] = new BigDecimal(0); // poliza con comisiones
					
				} else {

					resultado[3] = new BigDecimal(1); // poliza sin comisiones
				}

				resultado[0] = pctEntidad;
				resultado[1] = pctESmediadora;
				resultado[2] = pctComMax;
				resultado[4] = pctDescelegido;
				resultado[5] = pctrRecarelegido;
				
			} catch (Exception excepcion) {
				LOG.error(
						"Se ha producido un error al obtener los porcentajes de la tabla TB_POLIZAS_PCT_COMISIONES ",
						excepcion);
			}
		}

		return resultado;
	}
	
	private BigDecimal[] getPorcentajesComision(String refPoliza, Character tipoRef, Long codPlan, Character grupoNegocio, Date fechaEmision) throws DAOException {
		Session session = obtenerSession();
		BigDecimal resultado[] = new BigDecimal[4];
		BigDecimal pctEntidad = BigDecimal.ZERO;
		BigDecimal pctESmediadora = BigDecimal.ZERO;
		BigDecimal pctComMax = BigDecimal.ZERO;
		BigDecimal bigDecimalCien = new BigDecimal(100);
		Integer descuentoRecargo= null;
		BigDecimal pctDescuentoRecargo=null;
		final Pattern entidadPattern = Pattern.compile("[38][0-9][0-9][0-9]");//3xxx-0 o 8xxx-0
		boolean ajustar = false;
		
		if(refPoliza!=null && !"".equals(refPoliza)){
			try{
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT nvl(hc.entmediadora, col.entmediadora) as codentidad, nvl(hc.subentmediadora, col.subentmediadora) as subentmediadora, ");
				sql.append("com.pctentidad, com.pctesmediadora, com.pctcommax, col.TIPO_DESC_RECARG, col.PCT_DESC_RECARG ");
				sql.append("FROM O02AGPE0.TB_POLIZAS_PCT_COMISIONES com ");
				sql.append("inner join O02AGPE0.TB_POLIZAS pol on pol.referencia = :referencia and pol.tiporef = :tipoRef and pol.idestado >= 8 and pol.idpoliza = com.idpoliza ");
				sql.append("inner join O02AGPE0.TB_LINEAS lin on lin.codplan = :plan and pol.lineaseguroid = lin.lineaseguroid ");
				sql.append("inner join O02AGPE0.TB_COLECTIVOS col on pol.idcolectivo = col.id ");
				sql.append("left join (select aux.idcolectivo, max(aux.FECHAOPERACION) as fecha from O02AGPE0.TB_HISTORICO_COLECTIVOS aux where aux.fechaefecto <= :fechaEmision group by aux.idcolectivo) q on q.idcolectivo = col.id ");
				sql.append("left join O02AGPE0.TB_HISTORICO_COLECTIVOS hc on hc.idcolectivo = col.id and hc.fechaefecto <= :fechaEmision and hc.FECHAOPERACION = q.fecha ");
				sql.append(WHERE_1_1); 
				if (grupoNegocio != null) { 
					sql.append(" AND com.grupo_negocio = TO_CHAR(:grupoNegocio) "); 
				} 
				sql.append("AND ROWNUM = 1");

				SQLQuery query = session.createSQLQuery(sql.toString());
				
				query.setParameter(REFERENCIA, refPoliza);
				query.setParameter("tipoRef", tipoRef);
				query.setParameter("plan", codPlan);
				query.setParameter("fechaEmision", fechaEmision == null ? new Date() : fechaEmision);
				if (grupoNegocio != null) {
					query.setParameter("grupoNegocio", grupoNegocio);
				}
				query.addScalar("codentidad", Hibernate.STRING);
				query.addScalar("subentmediadora", Hibernate.STRING);
				query.addScalar("pctentidad", Hibernate.BIG_DECIMAL);
				query.addScalar("pctesmediadora", Hibernate.BIG_DECIMAL);
				query.addScalar("pctcommax", Hibernate.BIG_DECIMAL);
				query.addScalar("TIPO_DESC_RECARG", Hibernate.INTEGER);
				query.addScalar("PCT_DESC_RECARG", Hibernate.BIG_DECIMAL);
				
				Object[] aux = (Object[])query.uniqueResult();
				
				if (aux!=null){	
					if(aux[2] != null){//pctentidad
						pctEntidad = new BigDecimal(aux[2].toString());
					}
					
					
					//Descuento/Recargo
					if(aux[5] != null)descuentoRecargo=(Integer)aux[5];
					if(aux[6] != null)pctDescuentoRecargo=(BigDecimal)aux[6];					
					//*************************************************
					
					
					if(aux[3] != null){//pctesmediadora
						pctESmediadora = new BigDecimal(aux[3].toString());
						
						//Corregimos si el colectivo tiene descuentos o recargos. Como guardamos los porcentajes cambiados
						//si hay recargos o descuentos, el porcentaje del mediador será 100 - el porcentaje de la entidad
						if(null!=descuentoRecargo && null!=pctDescuentoRecargo && 
								pctDescuentoRecargo.compareTo(BigDecimal.ZERO)==1){
							pctESmediadora=BigDecimal.ZERO.add(bigDecimalCien).subtract(pctEntidad);
//							if(descuentoRecargo.compareTo(new Integer(0))==0){// descuentos
//								pctESmediadora=new BigDecimal(0.00).add(pctESmediadora.add(pctDescuentoRecargo));
//							}else{//Recargos
//								pctESmediadora=new BigDecimal(0.00).add(pctESmediadora.subtract(pctDescuentoRecargo));
//							}
						}
						
						if(bigDecimalCien.compareTo(pctESmediadora)==0){//Si es igual a 100
							
							String codESMediadora = (String)aux[1];
							
							if("0".equals(codESMediadora)){//El código de la E-S es 0 
								
								String codEntidad = (String)aux[0].toString();
								
								if(entidadPattern.matcher(codEntidad).matches()){//3xxx-0 o 8xxx-0
									//En este caso, PCTENTIDAD=100 y PCTESMEDIADORA=0
									ajustar = true;
								}
							}
						}
					}
					
					
					
					if(ajustar){
						pctEntidad = new BigDecimal("100.00");
						pctESmediadora = BigDecimal.ZERO;
					}
				
					if(aux[4] != null){ //pctcommax
						pctComMax = new BigDecimal(aux[4].toString());
					}
					resultado[3]= new BigDecimal(0);//polizqa con comisiones
					
				}else{
					resultado[3]= new BigDecimal(1);//polizqa sin comisiones
				}

				resultado[0]= pctEntidad;
				resultado[1]= pctESmediadora;
				resultado[2]= pctComMax;

			}catch (Exception excepcion) {
				LOG.error("Se ha producido un error al obtener los porcentajes de la tabla TB_POLIZAS_PCT_COMISIONES ", excepcion);
			}
		}
		return resultado;
	}
	
	private PorcentajesAplicar getPorcentajesComisionRenovables(PolizaRenovable polizaRenovable,  
			Map<String, ColectivosRenovacion> colColectivos,Map<String, PorcentajesAplicar> colPorcentajesAplicar,
			Map<String, BigDecimal> colPorcentajeMaximo, Map<String, Linea> colLineas)throws DAOException{
		//Definición de variables
		PorcentajesAplicar porcentajes = null;		Session session=null;				Long lineaseguroid=null;		
		String fechaEfecto=null;					BigDecimal codEntidad=null;			BigDecimal codSubEntidad=null;
		BigDecimal codPlan=null;					ColectivosRenovacion colectivo=null;Linea linea=null;
		//-----------------------------
		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			
			codPlan=new BigDecimal(polizaRenovable.getPlan());
			colectivo=this.getColectivo(polizaRenovable.getColectivoRenovacion().getReferencia(), colColectivos);
			linea=this.getLinea(codPlan, new BigDecimal(polizaRenovable.getLinea()), colLineas);
			fechaEfecto= df.format(polizaRenovable.getFechaRenovacion());
			LOG.info("getPorcentajesComisionRenovables - colectivo - linea ");
			if(null!= colectivo && null!=linea) {
				LOG.info("getPorcentajesComisionRenovables - colectivo - linea no nulo");
				lineaseguroid=linea.getLineaseguroid();
				codEntidad=new BigDecimal(colectivo.getCodentidadmed());
				codSubEntidad=new BigDecimal(colectivo.getCodsubentmed());
				
				session= obtenerSession();
				porcentajes=this.getPorcentajesSubentidad(session, lineaseguroid, fechaEfecto, codEntidad, codSubEntidad,colPorcentajesAplicar);
				if(null==porcentajes)porcentajes=this.getPorcentajesSubentidadFechaEfectoMinima(session, lineaseguroid,codEntidad, codSubEntidad, colPorcentajesAplicar);
				if(null==porcentajes)porcentajes=this.getPorcentajesSubentidadLineaGenerica(session,fechaEfecto, codEntidad, codSubEntidad, codPlan, colPorcentajesAplicar);
				if(null==porcentajes)porcentajes=this.getPorcentajesSubentidadLineaGenericaFechaEfectoMinima(session, codEntidad, codSubEntidad, codPlan, colPorcentajesAplicar);
				if(null!=porcentajes) {
					this.getPorcentajeMaxEntidad(session, porcentajes, lineaseguroid, fechaEfecto, colPorcentajeMaximo);
					if(porcentajes.getPctComMax()==null)this.getPorcentajeMaxEntidadFechaEfectoMinima(session, porcentajes, lineaseguroid, colPorcentajeMaximo);
					if(porcentajes.getPctComMax()==null)this.getPorcentajeMaxEntidadLineaGenerica(session, porcentajes, fechaEfecto, codPlan, colPorcentajeMaximo);
					if(porcentajes.getPctComMax()==null)this.getPorcentajeMaxEntidadLineaGenericaFechaEfectoMinima(session, porcentajes, codPlan, colPorcentajeMaximo);
				}
		}
			
		}catch (Exception excepcion) {
			LOG.error("Se ha producido un error al obtener los porcentajes de la tabla TB_POLIZAS_PCT_COMISIONES ", excepcion);
		}
		if(null!=porcentajes) {
			inicializaPorcentajes(porcentajes);
			porcentajes.setSinPorcentajes(new Integer(0));
		}else {
			LOG.info("getPorcentajesComisionRenovables - porcentajes no encontrados");
			porcentajes	= new PorcentajesAplicar(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new Integer(1));	
		}
		;
		return porcentajes;
	}
	
	/**
	 * Inicializa los valores a 0 de las propiedades nulas, incluido el propio objeto
	 * @param porcentajes
	 */
	private void inicializaPorcentajes(PorcentajesAplicar porcentajes) {	
			if(null==porcentajes.getPctEntidad())porcentajes.setPctEntidad(BigDecimal.ZERO);
			if(null==porcentajes.getPctESmediadora())porcentajes.setPctESmediadora(BigDecimal.ZERO);
			if(null==porcentajes.getPctComMax())porcentajes.setPctComMax(BigDecimal.ZERO);
	}

	private PorcentajesAplicar getPorcentajesSubentidad(Session session, Long lineaseguroid, 
			String fechaEfecto, BigDecimal codEntidad, BigDecimal codSubEntidad,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar) {
		final String  indiceBusqueda="1";
		PorcentajesAplicar porcentajes=null;
		String key=indiceBusqueda + lineaseguroid.toString() + fechaEfecto.toString()+codEntidad.toString() + codSubEntidad.toString();
		LOG.info(CLAVE_COL_PORCENTAJES_APLICAR + key);
		if(colPorcentajesAplicar.containsKey(key)) {
			porcentajes=colPorcentajesAplicar.get(key);
		}else {
			String sql =this.getSqlPorcentajesSubentidad(lineaseguroid, fechaEfecto, codEntidad, codSubEntidad);
			
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajes=new PorcentajesAplicar((BigDecimal)porc[0], (BigDecimal)porc[1],(BigDecimal)porc[2],(BigDecimal)porc[3], null );
				
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajesAplicar.put(key, porcentajes);
		}
		
		return porcentajes;
	}
	
	private void getPorcentajeMaxEntidad(Session session, PorcentajesAplicar porcentajes, Long lineaseguroid, 
			String fechaEfecto,Map<String, BigDecimal> colPorcentajeMaximo) {
		final String  indiceBusqueda="5";
		BigDecimal porcentajeMax=null;
		String key=indiceBusqueda + lineaseguroid.toString() + fechaEfecto.toString();
		LOG.info(CLAVE_COL_PORCENTAJE_MAXIMO + key);
		if(colPorcentajeMaximo.containsKey(key)) {
			porcentajeMax=colPorcentajeMaximo.get(key);
		}else {
			String sql =this.getSqlPorcentajeMaxEntidad(lineaseguroid, fechaEfecto);
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajeMax=(BigDecimal)porc[0];
				porcentajes.setPctComMax(porcentajeMax);
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajeMaximo.put(key, porcentajeMax);
		}		
	}
	  
	private PorcentajesAplicar getPorcentajesSubentidadFechaEfectoMinima(Session session, Long lineaseguroid, 
			BigDecimal codEntidad, BigDecimal codSubEntidad, Map<String, PorcentajesAplicar> colPorcentajesAplicar) {
		PorcentajesAplicar porcentajes=null;
		final String  indiceBusqueda="2";
		String key=indiceBusqueda + lineaseguroid.toString() + codEntidad.toString() + codSubEntidad.toString();
		LOG.info(CLAVE_COL_PORCENTAJES_APLICAR + key);
		if(colPorcentajesAplicar.containsKey(key)) {
			porcentajes=colPorcentajesAplicar.get(key);
		}else {
			String sql = this.getSqlPorcentajesSubentidadFechaEfectoMinima(lineaseguroid, codEntidad, codSubEntidad);
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajes=new PorcentajesAplicar((BigDecimal)porc[0], (BigDecimal)porc[1],(BigDecimal)porc[2],(BigDecimal)porc[3], null );
			}	
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajesAplicar.put(key, porcentajes);
		}
		
		return porcentajes;
	}

	private void getPorcentajeMaxEntidadFechaEfectoMinima(Session session, PorcentajesAplicar porcentajes, 
			Long lineaseguroid, Map<String, BigDecimal> colPorcentajeMaximo) {
		final String  indiceBusqueda="6";
		
		BigDecimal porcentajeMax=null;
		String key=indiceBusqueda + lineaseguroid.toString();
		LOG.info(CLAVE_COL_PORCENTAJE_MAXIMO + key);
		if(colPorcentajeMaximo.containsKey(key)) {
			porcentajeMax=colPorcentajeMaximo.get(key);
		}else {
			String sql = this.getSqlPorcentajeMaxEntidadFechaEfectoMinima(lineaseguroid);
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajeMax=(BigDecimal)porc[0];
				porcentajes.setPctComMax(porcentajeMax);
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajeMaximo.put(key, porcentajeMax);
		}
	}

	private PorcentajesAplicar getPorcentajesSubentidadLineaGenerica(Session session,String fechaEfecto, BigDecimal codEntidad,
			BigDecimal codSubEntidad, BigDecimal codPlan, Map<String, PorcentajesAplicar> colPorcentajesAplicar) {
		PorcentajesAplicar porcentajes=null;
		final String  indiceBusqueda="3";
		String key=indiceBusqueda + fechaEfecto.toString() + codEntidad.toString() + codSubEntidad.toString() + codPlan.toString();
		LOG.info(CLAVE_COL_PORCENTAJES_APLICAR + key);
		if(colPorcentajesAplicar.containsKey(key)) {
			porcentajes=colPorcentajesAplicar.get(key);
		}else {
			String sql = this.getSqlPorcentajesSubentidadLineaGenerica(fechaEfecto, codEntidad, codSubEntidad, codPlan);
			
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajes=new PorcentajesAplicar((BigDecimal)porc[0], (BigDecimal)porc[1],(BigDecimal)porc[2],(BigDecimal)porc[3], null );
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajesAplicar.put(key, porcentajes);
		}
				
		return porcentajes;
	}

	private void getPorcentajeMaxEntidadLineaGenerica(Session session, PorcentajesAplicar porcentajes,
			String fechaEfecto, BigDecimal codPlan, Map<String, BigDecimal> colPorcentajeMaximo) {
		final String  indiceBusqueda="7";
		BigDecimal porcentajeMax=null;
		String key=indiceBusqueda + fechaEfecto.toString() + codPlan.toString();
		LOG.info(CLAVE_COL_PORCENTAJE_MAXIMO + key);
		if(colPorcentajeMaximo.containsKey(key)) {
			porcentajeMax=colPorcentajeMaximo.get(key);
		}else {
			String sql =this.getSqlPorcentajeMaxEntidadLineaGenerica(fechaEfecto, codPlan);
			
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajeMax=(BigDecimal)porc[0];
				porcentajes.setPctComMax(porcentajeMax);
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajeMaximo.put(key, porcentajeMax);
		}
		
	}
	
	private PorcentajesAplicar getPorcentajesSubentidadLineaGenericaFechaEfectoMinima(Session session,
			BigDecimal codEntidad,	BigDecimal codSubEntidad, BigDecimal codPlan, Map<String, PorcentajesAplicar> colPorcentajesAplicar) {
		PorcentajesAplicar porcentajes=null;
		final String  indiceBusqueda="4";
		String key= indiceBusqueda + codEntidad.toString() + codSubEntidad.toString() + codPlan.toString();
		LOG.info(CLAVE_COL_PORCENTAJES_APLICAR + key);
		if(colPorcentajesAplicar.containsKey(key)) {
			porcentajes=colPorcentajesAplicar.get(key);
		}else {
			String sql = this.getSqlPorcentajesSubentidadLineaGenericaFechaEfectoMinima(codEntidad, codSubEntidad, codPlan);
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajes=new PorcentajesAplicar((BigDecimal)porc[0], (BigDecimal)porc[1],(BigDecimal)porc[2],(BigDecimal)porc[3], null );
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajesAplicar.put(key, porcentajes);
		}
		
		
		return porcentajes;
		
		
	}

	private void getPorcentajeMaxEntidadLineaGenericaFechaEfectoMinima(Session session, PorcentajesAplicar porcentajes,
			BigDecimal codPlan, Map<String, BigDecimal> colPorcentajeMaximo) {
		final String  indiceBusqueda="8";
		BigDecimal porcentajeMax=null;
		String key=indiceBusqueda + codPlan.toString();
		LOG.info(CLAVE_COL_PORCENTAJE_MAXIMO + key);
		if(colPorcentajeMaximo.containsKey(key)) {
			porcentajeMax=colPorcentajeMaximo.get(key);
		}else {
			String sql = this.getSqlPorcentajeMaxEntidadLineaGenericaFechaEfectoMinima(codPlan);
			List lista = session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0) {
				Object[] porc = (Object[]) lista.get(0);
				porcentajes.setPctComMax((BigDecimal)porc[0]);
			}
			if (lista!=null)LOG.info(REGISTROS + lista.size());
			colPorcentajeMaximo.put(key, porcentajeMax);
		}
				
	}
	
	//**************** SENTENCIAS SQL DE SELECCIÓN *********************************************************************
	private String getSqlPorcentajesSubentidad(Long lineaseguroid, String fechaEfecto, BigDecimal codEntidad, BigDecimal codSubEntidad) {
		StringBuilder sql = new StringBuilder();
		sql.append("select com.CODENTIDAD, com.CODSUBENTIDAD,  com.PCTENTIDAD, com.PCTMEDIADOR, com.FEC_EFECTO, com.ID ");
		sql.append(FROM_TB_COMS_CULTIVOS_SUBS_HIST_COM);	
		sql.append(WHERE_1_1);
		sql.append(AND_COM_LINEASEGUROID);
		sql.append(lineaseguroid);
		sql.append(" AND com.FEC_EFECTO>= TO_DATE('");
		sql.append(fechaEfecto);
		sql.append(DD_MM_YYYY);
		sql.append(" AND com.CODENTIDAD= ");
		sql.append(codEntidad);
		sql.append(" AND com.CODSUBENTIDAD=  ");
		sql.append(codSubEntidad);
		sql.append(" order by com.FEC_EFECTO asc, com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
	}

	private String getSqlPorcentajeMaxEntidad(Long lineaseguroid, String fechaEfecto) {		
		StringBuilder sql = new StringBuilder();
		sql.append("select com.PCTGENERALENTIDAD  as pctcommax, com.FECHA_EFECTO, com.ID ");
		sql.append("FROM TB_COMS_CULTIVOS_ENTS_HIST com ");	
		sql.append(WHERE_1_1);
		sql.append(AND_COM_LINEASEGUROID);
		sql.append(lineaseguroid);
		sql.append(" AND com.FECHA_EFECTO>= TO_DATE('");
		sql.append(fechaEfecto);
		sql.append(DD_MM_YYYY);
		sql.append(" order by com.FECHA_EFECTO asc, com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
		}
		
	private String getSqlPorcentajesSubentidadFechaEfectoMinima(Long lineaseguroid, BigDecimal codEntidad, BigDecimal codSubEntidad) {
		StringBuilder sql = new StringBuilder();
		sql.append("select com.CODENTIDAD, com.CODSUBENTIDAD, com.PCTENTIDAD,  com.PCTMEDIADOR, com.ID ");
		sql.append(FROM_TB_COMS_CULTIVOS_SUBS_HIST_COM);	
		sql.append(WHERE_1_1);
		sql.append(AND_COM_LINEASEGUROID);
		sql.append(lineaseguroid);
		sql.append(" AND com.CODENTIDAD= ");
		sql.append(codEntidad);
		sql.append(" AND com.CODSUBENTIDAD= ");
		sql.append(codSubEntidad);
		sql.append(" AND com.FEC_EFECTO = ");
		//Subconsulta
		sql.append("(select min(subC.FEC_EFECTO) from TB_COMS_CULTIVOS_SUBS_HIST subC ");
		sql.append("where subC.LINEASEGUROID = ");
		sql.append(lineaseguroid);
		sql.append(" and subC.CODENTIDAD= "); 
		sql.append(codEntidad);
		sql.append(" and subC.CODSUBENTIDAD= ");
		sql.append(codSubEntidad);
		sql.append(")");
		//Ordenación
		sql.append("order by com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
	}
	
	private String getSqlPorcentajeMaxEntidadFechaEfectoMinima(Long lineaseguroid) {
		StringBuilder sql = new StringBuilder();
		sql.append("select com.PCTGENERALENTIDAD  as pctcommax,com.FECHA_EFECTO, com.ID ");
		sql.append("FROM  TB_COMS_CULTIVOS_ENTS_HIST com ");	
		sql.append(WHERE_1_1);
		sql.append(AND_COM_LINEASEGUROID);
		sql.append(lineaseguroid);
		sql.append(" AND com.FECHA_EFECTO= ");
		//Subconsulta
		sql.append("(select min(subC.FECHA_EFECTO) from TB_COMS_CULTIVOS_ENTS_HIST subC ");
		sql.append("where subC.LINEASEGUROID = ");
		sql.append(lineaseguroid);
		sql.append(")");
		//Ordenación
		sql.append(" order by com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
	}
	
	private String getSqlPorcentajesSubentidadLineaGenerica(String fechaEfecto, BigDecimal codEntidad,
			BigDecimal codSubEntidad, BigDecimal codPlan) {
		StringBuilder sql = new StringBuilder();
		sql.append("select  com.CODENTIDAD, com.CODSUBENTIDAD, com.PCTENTIDAD, com.PCTMEDIADOR, com.FEC_EFECTO, com.ID ");
		sql.append(FROM_TB_COMS_CULTIVOS_SUBS_HIST_COM);	
		sql.append("INNER JOIN TB_LINEAS l on com.LINEASEGUROID = l.LINEASEGUROID ");
		sql.append(WHERE_1_1);
		sql.append("AND com.FEC_EFECTO>= TO_DATE('");
		sql.append(fechaEfecto);
		sql.append(DD_MM_YYYY);
		sql.append(" AND com.CODENTIDAD= ");
		sql.append(codEntidad);
		sql.append(" AND com.CODSUBENTIDAD= ");
		sql.append(codSubEntidad);
		sql.append(" AND l.CODPLAN= ");
		sql.append(codPlan);
		sql.append(" AND l.CODLINEA=999 ");
		sql.append("order by com.FEC_EFECTO asc, com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
	}
	
	private String getSqlPorcentajeMaxEntidadLineaGenerica(String fechaEfecto, BigDecimal codPlan) {
		StringBuilder sql = new StringBuilder();
		sql.append("select com.PCTGENERALENTIDAD  as pctcommax, com.FECHA_EFECTO, com.ID ");
		sql.append("FROM TB_COMS_CULTIVOS_ENTS_HIST com ");
		sql.append("INNER JOIN TB_LINEAS l on com.LINEASEGUROID = l.LINEASEGUROID ");
		sql.append(WHERE_1_1);
		sql.append("AND com.FECHA_EFECTO>= TO_DATE('");
		sql.append(fechaEfecto);
		sql.append(DD_MM_YYYY);
		sql.append(" and l.CODPLAN= ");
		sql.append(codPlan);
		sql.append(" AND l.CODLINEA=999 ");
		sql.append("order by com.FECHA_EFECTO asc, com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
	}
	
	private String getSqlPorcentajesSubentidadLineaGenericaFechaEfectoMinima(BigDecimal codEntidad,	BigDecimal codSubEntidad, 
			BigDecimal codPlan) {
		StringBuilder sql = new StringBuilder();
		sql.append("select com.CODENTIDAD, com.CODSUBENTIDAD, com.PCTENTIDAD, com.PCTMEDIADOR, com.FEC_EFECTO, com.ID  ");
		sql.append(FROM_TB_COMS_CULTIVOS_SUBS_HIST_COM);
		sql.append("INNER JOIN  TB_LINEAS l on com.LINEASEGUROID=l.LINEASEGUROID ");
		sql.append(WHERE_1_1);
		sql.append("AND com.CODENTIDAD= ");
		sql.append(codEntidad);
		sql.append(" AND com.CODSUBENTIDAD= ");
		sql.append(codSubEntidad);
		sql.append(" AND l.CODPLAN= ");
		sql.append(codPlan);
		sql.append(" AND l.CODLINEA=999 ");
		sql.append("AND com.FEC_EFECTO = ");
		//Subconsulta
		sql.append("(select min(subC.FEC_EFECTO) from TB_COMS_CULTIVOS_SUBS_HIST subC  ");
		sql.append("INNER JOIN TB_LINEAS li on subC.LINEASEGUROID=li.LINEASEGUROID ");
		sql.append("where li.CODPLAN= ");
		sql.append(codPlan);
		sql.append(" AND li.CODLINEA= 999 ");
		sql.append("AND subC.CODENTIDAD= ");
		sql.append(codEntidad);
		sql.append(" AND subC.CODSUBENTIDAD= ");
		sql.append(codSubEntidad);
		sql.append(")");
		//order by
		sql.append(" order by com.ID desc ");	
		LOG.info(sql.toString());
		return sql.toString();
	}
	
	private String getSqlPorcentajeMaxEntidadLineaGenericaFechaEfectoMinima(
			BigDecimal codPlan) {
		StringBuilder sql = new StringBuilder();
		sql.append("select com.PCTGENERALENTIDAD  as pctcommax, com.FECHA_EFECTO, com.ID ");
		sql.append("FROM TB_COMS_CULTIVOS_ENTS_HIST com ");
		sql.append("INNER JOIN TB_LINEAS l on com.LINEASEGUROID= l.LINEASEGUROID ");
		sql.append(WHERE_1_1);
		sql.append("AND l.CODPLAN= ");
		sql.append(codPlan);
		sql.append(" and l.CODLINEA=999");
		sql.append(" AND com.FECHA_EFECTO = ");
		//Subconsulta
		sql.append("(select min(subC.FECHA_EFECTO) from TB_COMS_CULTIVOS_ENTS_HIST subC  ");
		sql.append("INNER JOIN TB_LINEAS li on subC.LINEASEGUROID = li.LINEASEGUROID ");
		sql.append("where li.CODPLAN= ");
		sql.append(codPlan);
		sql.append(" and li.CODLINEA=999)");
		//order by
		sql.append("order by com.ID desc");
		LOG.info(sql.toString());
		return sql.toString();
	}
	
	// ******************************************************************************************************************
	/**
	 * Comprueba que los porcentajes de comisión no valgan cero los dos.
	 * @param arrayPorcentajes
	 * @return
	 */
	public boolean validarArrayPorcentajesComision(BigDecimal[] arrayPorcentajes){
		
		boolean esPorcentajesValidos = false;
		
		if(arrayPorcentajes!=null && arrayPorcentajes.length==3){
			
			BigDecimal bigDecimalCero 	= BigDecimal.ZERO;
			BigDecimal porComEntidad 	= arrayPorcentajes[0];
			BigDecimal porComES			= arrayPorcentajes[1];
			esPorcentajesValidos 		= (bigDecimalCero.compareTo(porComEntidad)!=0 || bigDecimalCero.compareTo(porComES)!=0);		
		}
		return esPorcentajesValidos;
	}
	
public boolean validarPorcentajesComision(PorcentajesAplicar porcentajes){
		
		boolean esPorcentajesValidos = false;
		
		if(porcentajes!=null && null!=porcentajes.getPctEntidad() && null!=porcentajes.getPctESmediadora()){
			BigDecimal bigDecimalCero 	= BigDecimal.ZERO;
			BigDecimal porComEntidad 	= porcentajes.getPctEntidad();
			BigDecimal porComES			= porcentajes.getPctESmediadora();
			esPorcentajesValidos 		= (bigDecimalCero.compareTo(porComEntidad)!=0 || bigDecimalCero.compareTo(porComES)!=0);		
		}
		return esPorcentajesValidos;
	}
	
	
	public class PorcentajesAplicar {

		private BigDecimal pctEntidad = null;
		private BigDecimal pctESmediadora = null;
		private BigDecimal pctComMax = null;
		private BigDecimal pctDescelegido = null;
		private BigDecimal pctrRecarelegido = null;
		private Integer sinPorcentajes;
		final Pattern entidadPattern = Pattern.compile("[38][0-9][0-9][0-9]"); // 3xxx-0 ó 8xxx-0

		public PorcentajesAplicar() {
			super();
		}

		public PorcentajesAplicar(BigDecimal codEntidad,
				BigDecimal codESMediadora, BigDecimal pctEntidad,
				BigDecimal pctESmediadora, BigDecimal pctComMax) {

			super();

			boolean ajustar = false;

			if ("0".equals(codESMediadora.toString())) {// El código de la E-S es 0
				String strCodEntidad = codEntidad.toString();
				if (entidadPattern.matcher(strCodEntidad).matches()) {// 3xxx-0 ó 8xxx-0
					// En este caso, PCTENTIDAD=100 y PCTESMEDIADORA=0
					ajustar = true;
				}
			}

			if (ajustar) {
				this.pctEntidad = new BigDecimal("100.00");
				this.pctESmediadora = BigDecimal.ZERO;
			} else {
				this.pctEntidad = pctEntidad;
				this.pctESmediadora = pctESmediadora;
				this.pctComMax = pctComMax;
			}
		}

		public PorcentajesAplicar(BigDecimal pctEntidad,
				BigDecimal pctESmediadora, BigDecimal pctComMax) {
			super();
			this.pctEntidad = pctEntidad;
			this.pctESmediadora = pctESmediadora;
			this.pctComMax = pctComMax;
		}

		public PorcentajesAplicar(BigDecimal pctEntidad,
				BigDecimal pctESmediadora, BigDecimal pctComMax,
				Integer sinPorcentaje) {
			this(pctEntidad, pctESmediadora, pctComMax);
			this.sinPorcentajes = sinPorcentaje;
		}

		public PorcentajesAplicar(BigDecimal pctEntidad,
				BigDecimal pctESmediadora, BigDecimal pctComMax,
				Integer sinPorcentaje, BigDecimal pctDescelegido,
				BigDecimal pctrRecarelegido) {
			this(pctEntidad, pctESmediadora, pctComMax, sinPorcentaje);
			this.pctDescelegido = pctDescelegido;
			this.pctrRecarelegido = pctrRecarelegido;
		}

		public BigDecimal getPctEntidad() {
			return pctEntidad;
		}

		public void setPctEntidad(BigDecimal pctEntidad) {
			this.pctEntidad = pctEntidad;
		}

		public BigDecimal getPctESmediadora() {
			return pctESmediadora;
		}

		public void setPctESmediadora(BigDecimal pctESmediadora) {
			this.pctESmediadora = pctESmediadora;
		}

		public BigDecimal getPctComMax() {
			return pctComMax;
		}

		public void setPctComMax(BigDecimal pctComMax) {
			this.pctComMax = pctComMax;
		}

		public Integer getSinPorcentajes() {
			return sinPorcentajes;
		}

		public void setSinPorcentajes(Integer sinPorcentaje) {
			this.sinPorcentajes = sinPorcentaje;
		}

		public BigDecimal getPctDescelegido() {
			return pctDescelegido;
		}

		public void setPctDescelegido(BigDecimal pctDescelegido) {
			this.pctDescelegido = pctDescelegido;
		}

		public BigDecimal getPctrRecarelegido() {
			return pctrRecarelegido;
		}

		public void setPctrRecarelegido(BigDecimal pctrRecarelegido) {
			this.pctrRecarelegido = pctrRecarelegido;
		}
	}
	
	public PorcentajesAplicar obtenerPorcentajesComisionUnif17(
			String refPoliza, Character tipoRef, Long codPlan,
			Character grupoNegId, Date fechaEmision) throws DAOException {
		BigDecimal resultado[] = null;
		PorcentajesAplicar porcentajes = null;
		try {
			resultado = this.getPorcentajesComisionUnif17(refPoliza, tipoRef,
					codPlan, grupoNegId, fechaEmision);
			if (null != resultado && resultado.length > 0) {
				porcentajes = new PorcentajesAplicar(resultado[0],
						resultado[1], resultado[2], resultado[3].intValue(),
						resultado[4], resultado[5]);
			}
		} catch (Exception excepcion) {
			LOG.error(
					"Se ha producido un error al obtener los porcentajes de comisiones de la póliza: "
							+ refPoliza + ". ", excepcion);
		}
		return porcentajes;
	}
}

