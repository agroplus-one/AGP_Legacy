package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCoberturasExplotacion;

public class DatosExplotacionDao extends BaseDaoHibernate implements IDatosExplotacionDao {

	@SuppressWarnings("rawtypes")
	public boolean isCoberturasElegiblesNivelExplotacion(Long lineaSeguroId, String codModulos){
		boolean isCoberturas = false;
		try {
			String sql ="select rc.FILAMODULO from o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD_G rc " +
			  "inner join o02agpe0.TB_LINEAS lin ON rc.LINEASEGUROID = lin.LINEASEGUROID " + 
			  "WHERE lin.LINEASEGUROID = " + lineaSeguroId + 
			  " AND rc.CODMODULO in (" + codModulos + ")  AND rc.NIVELECCION='D'";
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			List resultado = session.createSQLQuery(sql).list();
			if(null!=resultado && resultado.size()>0)
				return true;
		
			return isCoberturas;
		}catch (Exception ex) {
			logger.error("DatosExplotacionDao.isCoberturasElegiblesNivelExplotacion. - ", ex);
			return false;
		}	
	}
	
	@SuppressWarnings("unchecked")
	public List<Object>getTipoCapitalConGrupoNegocio(Boolean dependenNumAnimales)throws DAOException{
		List<Object> tc=null;
		if(dependenNumAnimales){
			//P20328
			String sql ="select CODTIPOCAPITAL, CODCONCEPTO from o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG where CODCONCEPTO IN (1097, 1065, 1071, 1072 ,1076)";
			logger.debug(sql);
			Session session = obtenerSession();
			tc = session.createSQLQuery(sql).list();			
		}else{
			tc=this.getObjects(TipoCapitalConGrupoNegocio.class, null, null);
		}
		return tc;
	}
	
	public SWModulosCoberturasExplotacion saveEnvioCobExplotacion(SWModulosCoberturasExplotacion envio)	throws DAOException {
		Session session = obtenerSession();
		try {

			session.saveOrUpdate(envio);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad",ex);
		} finally {
		}

		return envio;
	}
	
	/* Pet. 57622-REQ.01 ** MODIF TAM (11.07.2019) INICIO */
	@SuppressWarnings("deprecation")
	public String getCobExplotacion(final String codModulo, final Long idExpl)	throws DAOException {
		
		String xml = null;
		Session session = obtenerSession();
		
		try {
			
			Clob respuesta=null;
			String sql ="select mod.respuesta from o02agpe0.tb_sw_modulos_coberturas_exp mod" +
				    " WHERE mod.idexplotacion = " + idExpl + 
				  " AND mod.codmodulo = '" + codModulo +
				  " ' order by mod.fecha desc"; 
			
			logger.info(sql);
			
			Statement stmt = session.connection().createStatement();
		    ResultSet rs = stmt.executeQuery(sql);	
		    
		    if(null!=rs ) {
	           while (rs.next()) {		        	 
	        	  respuesta = (Clob) rs.getClob(1);
	        	  xml = WSUtils.convertClob2String(respuesta);
	           }  
	           rs.close();
	           stmt.close();
	          		       		 		
	           return xml;
		    }
		    return xml;
		}
		catch (Exception e) {
			throw new DAOException("Error al recuperar el campo respuesta del SW de la explotacion: " 
					+ idExpl + " y el modulo:" + codModulo, e);
		}	
	}
	
	 @SuppressWarnings("rawtypes")
	public String obtenerExploDescvalorElegido(BigDecimal codConcepto, BigDecimal fila, Long idPoliza, Integer numeroExpl) throws DAOException {
		 String desc_valor = "";
		 
		 Session session = obtenerSession();
			
			try {
				
				String sql = "select c.dv_valor_descripcion" + 
						     " from o02agpe0.tb_explotaciones_coberturas c" + 
						     " where c.idexplotacion in (select exp.id " + 
						     					        " from o02agpe0.tb_explotaciones exp " + 
						                                " where exp.idpoliza = " + idPoliza +
						                                " and exp.numero = " + numeroExpl + " ) " +
						     " and c.fila = " + fila + 
						     " and c.dv_cod_concepto = " + codConcepto + 
						     " and dv_elegido ='S'";
				
				logger.info(sql);
				
				logger.debug(sql);		
				List resultado = session.createSQLQuery(sql).list();
				if(null!=resultado && resultado.size()>0){
					desc_valor = (String) resultado.get(0);
				}
			}
			catch (Exception e) {
				throw new DAOException("Error al recuperar el valor de la Explotacion, para la poliza: " 
						+ idPoliza + " y la explotacion: " + numeroExpl, e);
			}
		 
		 return desc_valor;
	 }
	 
	 @SuppressWarnings("rawtypes")
	public Boolean obtenerExploElegida(BigDecimal codConcepto, BigDecimal fila, Long idPoliza, Integer numeroExpl) throws DAOException {
		 
		 Session session = obtenerSession();
		 Boolean elegida = false;
			
			try {
				
				String sql = "select c.*" + 
						     " from o02agpe0.tb_explotaciones_coberturas c" + 
						     " where c.idexplotacion in (select exp.id " + 
						     					        " from o02agpe0.tb_explotaciones exp " + 
						                                " where exp.idpoliza = " + idPoliza +
						                                " and exp.numero = " + numeroExpl + " ) " +
						     " and c.elegida ='S' and c.fila = "  + fila ; 
				
				logger.debug(sql);		
				List resultado = session.createSQLQuery(sql).list();
				if(null!=resultado && resultado.size()>0){
					elegida = true;
				}else{
					elegida =  false;
				}
			}
			catch (Exception e) {
				throw new DAOException("Error al recuperar el valor de la Explotacion, para la poliza: " 
						+ idPoliza + " y la explotacion: " + numeroExpl, e);
			}
		 
			return elegida;
	 }
	 
	/* Pet. 57622-REQ.01 ** MODIF TAM (11.07.2019) * FIN */
	
	
	/**
	 * Elimina el objeto de la sesion de hibernate para recargarlo
	 */
	public void evictEnvio(SWModulosCoberturasExplotacion envio) {
		Session session = obtenerSession();
		session.evict(envio);
	}
	
	public void deleteCoberturasById(final Long idExplotacion) throws DAOException {
		Session session = obtenerSession();
		try {
			Query query = session.createSQLQuery(
							"delete from o02agpe0.TB_EXPLOTACIONES_COBERTURAS where idexplotacion = :idExplotacion")
					.setLong("idExplotacion", idExplotacion);
			query.executeUpdate();
		} catch (Exception e) {
			throw new DAOException(
					"Error al borrar las coberturas de la explotacion "+idExplotacion, e);
		}
	}
	
	public void deleteCoberturasByIdsCob(final Long idExplotacion, List<String> idsABorrar) throws DAOException {
		Session session = obtenerSession();
		try {
			for (String idCob:idsABorrar) {
				Query query = session.createSQLQuery(
								"delete from o02agpe0.TB_EXPLOTACIONES_COBERTURAS where idexplotacion = :idExplotacion and id = :id")
						.setLong("idExplotacion", idExplotacion)
						.setLong("id",new Long(idCob));
				query.executeUpdate();
			}
		} catch (Exception e) {
			throw new DAOException(
					"Error al borrar las coberturas de la explotacion "+idExplotacion, e);
		}
	}

}
