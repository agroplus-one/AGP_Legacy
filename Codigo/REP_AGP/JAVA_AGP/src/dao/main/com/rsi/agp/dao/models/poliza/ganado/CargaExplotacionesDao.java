package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.BaseDaoHibernate;

@SuppressWarnings("unchecked")
public class CargaExplotacionesDao extends BaseDaoHibernate implements ICargaExplotacionesDao {
	
	private static final Log logger = LogFactory.getLog(CargaExplotacionesDao.class);
	
	/**
	 * Filtrará según el parámetro tipoFiltro 
	 * tipoFiltro= 0 idAsegurado, plan anterior, código de línea y estados Enviada Correcta y Emitida
	 * tipoFiltro= 1 no filtrará por estados y buscará por los tres últimos planes
	 * tipoFiltro= 2 no filtrará por estados y buscará por el plan actual
	 * @throws DAOException 
	 */
	public List<BigDecimal>getIdsPolizas (int tipoFiltro, Long idAsegurado, 
			List<BigDecimal> listCodplan, BigDecimal codlinea, Long idpoliza) throws DAOException{
		List<BigDecimal>res=null;
		String sql=null;
			
		try {
			sql="SELECT pol.IDPOLIZA FROM TB_POLIZAS pol inner join TB_LINEAS lin ON pol.LINEASEGUROID=lin.LINEASEGUROID " +
					"WHERE pol.IDASEGURADO= " + idAsegurado + " and lin.CODLINEA= " + codlinea + 
					" AND  lin.CODPLAN IN ("; 
			
			//Creamos el conjunto de valores para la claúsula IN
			for (int i = 0; i < listCodplan.size(); i++) {
				sql = sql + listCodplan.get(i).toString();
				if(i+1<listCodplan.size())
					sql=sql + ",";
			}
			sql=sql + ")";// cerramos la claúsula IN
			
			if (tipoFiltro==0) {
				sql=sql + " and pol.IDESTADO IN (" + Constants.ESTADO_POLIZA_EMITIDA  + 
							", " + Constants.ESTADO_POLIZA_DEFINITIVA + ")";				
			}
			else {
				sql = sql + " and pol.IDESTADO NOT IN (" + Constants.ESTADO_POLIZA_BAJA + ")";
			}
			sql += " and pol.idpoliza != (" + idpoliza + ")";
			logger.debug(sql);
			res= this.getObjectsBySQLQuery(sql);
			return res;
		}catch (Exception ex) {
			logger.error("CargaExplotacionesDao.getIdsPolizas. - ", ex);
			throw new DAOException("CargaExplotacionesDao.getIdsPolizas. - ", ex);
		}	
	}
	
	public void actualizarIdCargaExplotaciones(Long idpoliza, Integer idCargaExplotaciones) 
			throws Exception{
		try {
			String sql="UPDATE TB_POLIZAS set ID_CARGA_EXPLOTACIONES=" + idCargaExplotaciones + 
					" WHERE IDPOLIZA=" + idpoliza;
			Session session = obtenerSession();
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception e) {
			logger.error("CargaExplotacionesDao.actualizarIdCargaExplotaciones. - ", e);
			throw new Exception("CargaExplotacionesDao.actualizarIdCargaExplotaciones. - " +
					"Error actualizando el campo ID_CARGA_EXPLOTACIONES de la póliza", e);
		}
		
	}
	
	
	public List<com.rsi.agp.dao.tables.poliza.Poliza> getPolizas(int tipoFiltro, 
			Long idAsegurado, List<BigDecimal> listCodplan, BigDecimal codlinea,
			Long idPoliza) throws DAOException {
		
		// situacion actualizada =idasegurado, línea, plan anterior y estados ‘Enviada correcta’ o ‘Emitida’
		// últimos planes        =idasegurado, línea y los tres planes anteriores al actual
		// plan actual           =idasegurado, línea y plan actual
		
		
		BigDecimal[] estadoPol=null;
		try {
			if (tipoFiltro==0) {
				estadoPol = new BigDecimal[2];
				estadoPol[0] = Constants.ESTADO_POLIZA_DEFINITIVA;
				estadoPol[1] = Constants.ESTADO_POLIZA_EMITIDA;
			}
			Session session = obtenerSession();			
			Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.poliza.Poliza.class);

			criteria.createAlias("asegurado", "aseg");
			criteria.createAlias("linea", "lin");
			criteria.createAlias("estadoPoliza", "estado");
			criteria.createAlias("aseg.entidad", "entidad");			
			criteria.add(Restrictions.eq("aseg.id", idAsegurado));
			criteria.add(Restrictions.in("lin.codplan", listCodplan));
			criteria.add(Restrictions.eq("lin.codlinea", codlinea));
			if (idPoliza != null){
				criteria.add(Restrictions.ne("idpoliza", idPoliza));
			}
			if (tipoFiltro==0) {
				criteria.add(Restrictions.in("estado.idestado",estadoPol ));
			}	
			else {
				criteria.add(Restrictions.ne("estado.idestado", Constants.ESTADO_POLIZA_BAJA));
			}
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					ex);
		} finally {
		}
	}
	
	public short getFilaExplotacionCobertura(Long lineaSeguroId, String modulo, int conceptoPpalMod, int riesgoCubierto) throws DAOException{
		
//		List<BigDecimal>lista=null;
		String sql=null;
		BigDecimal resBigD=null;
		short res = 0;
		try {
			sql ="select rc.FILAMODULO from TB_SC_C_RIESGO_CBRTO_MOD_G rc " +
					  "inner join TB_LINEAS lin ON rc.LINEASEGUROID = lin.LINEASEGUROID " + 
					  "WHERE lin.LINEASEGUROID = " + lineaSeguroId + 
					  " AND rc.CODMODULO = '" + modulo + "' AND rc.CODCONCEPTOPPALMOD = " + conceptoPpalMod +
					  " AND rc.CODRIESGOCUBIERTO = " + riesgoCubierto + "  AND rc.NIVELECCION='D'";

			logger.debug(sql);
			Session session = obtenerSession();
			
			resBigD = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			if(null!=resBigD)
				res=resBigD.shortValue();
			
//			lista= this.getObjectsBySQLQuery(sql);
//			if(lista!=null && lista.size()>0){
//				resBigD=lista.get(0);
//				res=resBigD.shortValue();				
//			}
									
			return res;
		}catch (Exception ex) {
			logger.error("CargaExplotacionesDao.getFilaExplotacionCobertura. - ", ex);
			throw new DAOException("CargaExplotacionesDao.getFilaExplotacionCobertura. - ", ex);
		}	
		
	}
	
	public String getDescripcionConceptoPpalMod(int conceptoPpalMod) throws DAOException{
		String sql=null;
		String res = null;
		try {
			sql ="SELECT DESCONCEPTOPPALMOD from TB_SC_C_CONCEPTO_PPAL_MOD " + 
					"WHERE CODCONCEPTOPPALMOD = " +  conceptoPpalMod;

			logger.debug(sql);
			Session session = obtenerSession();
			
			res = (String) session.createSQLQuery(sql).uniqueResult();
									
			return res;
		}catch (Exception ex) {
			logger.error("CargaExplotacionesDao.getDescripcionConceptoPpalMod. - ", ex);
			throw new DAOException("CargaExplotacionesDao.getDescripcionConceptoPpalMod. - ", ex);
		}	
		
	}
	
	public String getDescripcionRiesgoCubierto(Long lineaSeguroId,
			String modulo, int riesgoCubierto) throws DAOException{
		String sql=null;
		String res = null;		
		try {
			sql ="SELECT rc.DESRIESGOCUBIERTO FROM TB_SC_C_RIESGOS_CUBIERTOS  rc " +
					"INNER JOIN TB_LINEAS lin on rc.LINEASEGUROID = lin.LINEASEGUROID " +
					"WHERE lin.LINEASEGUROID = " + lineaSeguroId + 
					" AND rc.CODMODULO = '" + modulo + "' AND rc.CODRIESGOCUBIERTO = " + riesgoCubierto;

			logger.debug(sql);
			Session session = obtenerSession();
			
			res = (String) session.createSQLQuery(sql).uniqueResult();
									
			return res;
		}catch (Exception ex) {
			logger.error("CargaExplotacionesDao.getDescripcionRiesgoCubierto. - ", ex);
			throw new DAOException("CargaExplotacionesDao.getDescripcionRiesgoCubierto. - ", ex);
		}	
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public Object[]  getMediadoraColectivoGanadoweb(Integer refColectivo) throws DAOException{
		String sql=null;
		List registros = new ArrayList();
		Object[] campos =null;
		try {
			sql="select col.ENTMED, col.SUBMED from GANAWEB.COLECTIVOS col WHERE col.CODCOL=" + refColectivo;
			logger.debug(sql);
			Session session = obtenerSession();
			registros = session.createSQLQuery(sql).list();
			if(registros.size()>0){
				campos = (Object[]) registros.get(0);
			}
		} catch (Exception ex) {
			logger.error("CargaExplotacionesDao.getMediadoraColectivoGanadoweb. - ", ex);
			throw new DAOException("CargaExplotacionesDao.getMediadoraColectivoGanadoweb. - ", ex);
		}
		return campos;
	}
	
	
}
