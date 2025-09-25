package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;

import com.rsi.agp.dao.models.BaseDaoHibernate;

public class DatoAseguradoDao extends BaseDaoHibernate implements IDatoAseguradoDao {
	
	public boolean existeDatoAsegurado (Long idAsegurado, BigDecimal codLinea){
		try {		

			BigDecimal resultado;
			Session session = obtenerSession();
			
	 		String sql = "select count(*) from tb_datos_asegurados d " +
	 				"where d.idasegurado = " + idAsegurado + " and d.codlinea = " + codLinea;
			
	 		List list = session.createSQLQuery(sql).list();
			resultado = (BigDecimal) list.get(0);
			
			if ( resultado.intValue() ==0) 
				return false;
			else
				return true;
		
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean duplicaLinea (Long idasegurado, Long iddatoasegurado, BigDecimal codLinea){
		try {		

			BigDecimal resultado;
			Session session = obtenerSession();
			
	 		String sql = "select count(*) from tb_datos_asegurados d " +
	 				"where d.idasegurado="+idasegurado+" and d.id != " + iddatoasegurado + " and d.codlinea = " + codLinea;
			
	 		List list = session.createSQLQuery(sql).list();
			resultado = (BigDecimal) list.get(0);
			
			if ( resultado.intValue() ==0) 
				return false;
			else
				return true;
		
		} catch (Exception e) {
			return false;
		}
	}
	
	// devuelve la n linea de un asegurado
	public BigDecimal getCodLinea (Long idDatoAsegurado){
		try {		

			BigDecimal resultado;
			Session session = obtenerSession();
			
	 		String sql = "select codLinea from tb_datos_asegurados d " +
	 				"where d.id="+idDatoAsegurado;
			
	 		List list = session.createSQLQuery(sql).list();
			resultado = (BigDecimal) list.get(0);
			
			return resultado;
		
		} catch (Exception e) {
			return null;
		}
	}
	
}
