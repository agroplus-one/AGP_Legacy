package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class CriteriaUtils {
	
	private static int PARAMETER_LIMIT = 999;

	public static Criterion splitHibernateIn(String propertyName, List values) {
        Criterion criterion = null;

        int listSize = values.size();
        for (int i = 0; i < listSize; i += PARAMETER_LIMIT) {
            List subList;
            if (listSize > i + PARAMETER_LIMIT) {
                subList = values.subList(i, (i + PARAMETER_LIMIT));
            } else {
                subList = values.subList(i, listSize);
            }
            if (criterion != null) {
                criterion = Restrictions.or(criterion, Restrictions.in(propertyName, subList));
            } else {
                criterion = Restrictions.in(propertyName, subList);
            }
        }
        return criterion;
    }
	
	public static List<String> getCodigosOficina (final String oficina) {
		
		String parsedOficina = oficina.replaceFirst ("^0*", "");
		List<String> codigosOficina = new ArrayList<String>();
		
		if (parsedOficina.length()==4) {
			codigosOficina.add(parsedOficina);
		}else {
			while (parsedOficina.length()<=4) {
				codigosOficina.add(parsedOficina);
				parsedOficina = 0 + parsedOficina;
			}
		}
		return codigosOficina;
	}
	
	public static List<String> getCodigosListaOficina (List<?> lista){
			List<String> codigosOficina = new ArrayList<String>();
		for(Object ofi:lista){
			String oficina=ofi.toString();
		if (oficina.length()==4) {
			codigosOficina.add(oficina);
		}else {
			while (oficina.length()<=4) {
				codigosOficina.add(oficina);
				oficina = 0+oficina;
			}
		}
		}
		return codigosOficina;
	}
	
	
	public static StringBuilder splitSql(String propertyName, List values) throws Exception {
		StringBuilder sql = new StringBuilder();
		try {
	        int listSize = values.size();
	        for (int i = 0; i < listSize; i += PARAMETER_LIMIT) {
	        	List<String> subList = new ArrayList<String>();
	            if (listSize > i + PARAMETER_LIMIT) {
	                subList = values.subList(i, (i + PARAMETER_LIMIT));
	               
	               
	            } else {
	                subList = values.subList(i, listSize);
	            }
	            String strList = "";
	        	
	            boolean entro = false;
	            for (String sList : subList){
	            	if (!entro){          		
	            		entro = true;
	            	}else{
	            		strList = strList + ",";
	            	}
	            	strList = strList + "'"+sList+ "'";
	            }
	            
	            if ((sql.length() >0)) {
	            	sql.append(" or " + propertyName+ " in ("+ strList+") ");
	            } else {
	            	sql.append(propertyName + " in ("+ strList+") ");
	            }
	        }
	        return sql;
		}catch (Exception e ) {
			throw e;
		}
    }
	
}
