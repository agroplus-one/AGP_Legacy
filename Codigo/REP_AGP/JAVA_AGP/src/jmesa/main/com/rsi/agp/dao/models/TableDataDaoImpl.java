package com.rsi.agp.dao.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.TableDataFilter;
import com.rsi.agp.dao.filters.TableDataSort;
import com.rsi.agp.dao.filters.TableDataSort.Sort;

/**
 * @author T-Systems
 */
@SuppressWarnings("unchecked")
public class TableDataDaoImpl extends HibernateDaoSupport implements TableDataDao {
	
	private final Log  logger = LogFactory.getLog(TableDataDaoImpl.class);
	
    public List<Object> getTableData(String objeto, HashMap<String, Object> filtros) {
    	
    	String lineaSeguroId = (String)filtros.get("lineaSeguroId");
    	String codPlan = (String)filtros.get("codPlan");
    	String criterio = (String)filtros.get("criterio");    	
    	StringBuffer queryString = new StringBuffer("from " + objeto);

    	if (criterio.equals("lineaseguroid")) {
    		queryString.append(" where lineaseguroid = " + lineaSeguroId);
    	} else if (criterio.equals("codplan")) {
    		queryString.append(" where codplan = " + codPlan);
    	} else if (criterio.equals("plan")) {
    		queryString.append(" where plan = " + codPlan);
    	}
    	
        return getHibernateTemplate().find(queryString.toString());
    }

    public int getTableDataCountWithFilter(final TableDataFilter filter, final FilterSet filtroColumna) {
    	
    	 try {
    		 
    		 Long count = (Long) getHibernateTemplate().execute(new HibernateCallback() {
    	            @SuppressWarnings("rawtypes")
					public Object doInHibernate(Session session)throws HibernateException, SQLException {
    	            	
    	            	String lineaSeguroId = (String)filter.getFiltros().get("lineaSeguroId");
    	            	String codPlan = (String)filter.getFiltros().get("codPlan");
    	            	String codLinea = (String)filter.getFiltros().get("codLinea");
    	            	String criterio = (String)filter.getFiltros().get("criterio"); 
    	            	String clase = (String)filter.getFiltros().get("clase"); 
    	            	String campos = (String)filter.getFiltros().get("fields");
    	            	String tipos = (String)filter.getFiltros().get("types");
    	            	
    	            	
    	            	String query = "select count(*) from ";
    	            	if (clase.indexOf(" x") < 0){
    	            		query += clase  + " x";
    	            	}
    	            	else{
    	            		query += clase;
    	            	}
    	            	
    	            	query += " where 1=1 ";
    	            	
    	            	if (criterio.indexOf("plan")>=0) {
    	            		query += " and x."+criterio+" = " + codPlan;
    	            	} else if (criterio.indexOf("codlinea") >= 0) {  //codlinea
    	            		query += " and "+criterio+" = " + codLinea ;
    	            	} else if (!criterio.equals("")) {  //lineaseguroid
    	            		query += " and x."+criterio+" = " + lineaSeguroId ;
    	            	}
    	            	
    	            	// Recorremos el filtro y añadimos a la query
    	            	Set<Filter>  filCol = (Set<Filter>) filtroColumna.getFilters();
    	            	           	
    	            	Iterator iter = filCol.iterator();
    	            	while (iter.hasNext()) { // Para cada elemento que venga en el filtro
    	            		Filter fc = (Filter) iter.next();
    	            		String propiedad = fc.getProperty();
    	            		String valor = fc.getValue();
    	            		String tipocampo = "";
    	            		// Comprobamos el tipo de la propiedad 
    	            		if (FiltroUtils.noEstaVacio(campos)&& 
    	            				FiltroUtils.noEstaVacio(tipos)) {
    	            			String[] campo = campos.split("\\|");
    	            			String[] tipo = tipos.split("\\|");
    	            			int posicion =-1;
    	            			for (int i=0; i<campo.length; i++){
    	            				if (propiedad.equals(campo[i])){ 
    	            					posicion = i;
    	            					break;
    	            				}
    	            			}
    	            			if (posicion != -1) {tipocampo = tipo[posicion]; }
    	            			
    	            			// Completamos la query
        	            		
        	            		if (tipocampo.equals("null")){ //Si el tipocampo es un null-> string
        	            			query+=" and x."+ propiedad +" like " + "'"+ valor+"%'";
        	            		}else if (tipocampo.trim().equals("char")){ //Si el tipocampo es char
        	            			query+=" and x."+ propiedad +" = " + "'"+ valor+"'";
        	            		}else if (tipocampo.trim().equals("number")){ //Si el tipocampo es number
        	            		  query+=" and x."+ propiedad +" = " + valor;
    	            			}else if (tipocampo.trim().equals("date")){ //Si el tipocampo es date
    	            				query+=" and x."+ propiedad +" = " + "'"+ valor+"'";
    	            			}
        	            		
    	            		}
    	            		
    	            	}
    	            	
    	            	Query hql= session.createQuery(query);
    	    			return hql.uniqueResult();
    	            }
    	        });

    	        return count.intValue();
            
         } catch (Exception e) { 
        	 logger.error("Error al obtener el número de elementos del listado genérico", e);
        	 return 0;
         } 

    	

    }

    @SuppressWarnings("rawtypes")
	public List<Object> getTableDataWithFilterAndSort(final TableDataFilter filter,final FilterSet filtroColumna, final TableDataSort sort, final int rowStart, final int rowEnd) {
     	 List applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
     		 
             public Object doInHibernate(Session session)throws HibernateException, SQLException {
            	 
              try {	 
            	String lineaSeguroId = (String)filter.getFiltros().get("lineaSeguroId");
             	String codPlan = (String)filter.getFiltros().get("codPlan");
             	String codLinea = (String)filter.getFiltros().get("codLinea");
             	String criterio = (String)filter.getFiltros().get("criterio"); 
             	String clase = (String)filter.getFiltros().get("clase"); 
             	String claseItem = (String)filter.getFiltros().get("claseItem");
             	String campos = (String)filter.getFiltros().get("fields");
            	String tipos = (String)filter.getFiltros().get("types");
             	
            	String query = "select x from ";
            	if (clase.indexOf(" x") < 0){
            		query += clase  + " x";
            	}
            	else{
            		query += clase;
            	}
            	
            	query += " where 1=1 ";
            	            	 
            	if (criterio.indexOf("plan")>=0) {
            		query += " and x."+criterio+" = " + codPlan;
            	} else if (criterio.indexOf("codlinea") >= 0) {  //codlinea
            		query += " and "+criterio+" = " + codLinea ;
            	} else if (!criterio.equals("")) {
             		query += " and x."+criterio+" = " + lineaSeguroId ;
             	} 
            	
            	//  Recorremos el filtro y añadimos a la query
            	Set<Filter>  filCol = (Set<Filter>) filtroColumna.getFilters();
            	
            	Iterator iter = filCol.iterator();
            	while (iter.hasNext()) { // Para cada elemento que venga en el filtro
            		Filter fc = (Filter) iter.next();
            		String propiedad = fc.getProperty();
            		String valor = fc.getValue();
            		String tipocampo = "";
            		// Comprobamos el tipo de la propiedad 
            		if (FiltroUtils.noEstaVacio(campos)&& 
            				FiltroUtils.noEstaVacio(tipos)) {
            			String[] campo = campos.split("\\|");
            			String[] tipo = tipos.split("\\|");
            			int posicion =-1;
            			for (int i=0; i<campo.length; i++){
            				if (propiedad.equals(campo[i])){ 
            					posicion = i;
            					break;
            				}
            			}
            			if (posicion != -1) { tipocampo = tipo[posicion]; }
            			
            			// Completamos la query
	            		
	            		if (tipocampo.equals("null")){ //Si el tipocampo es un null-> string
	            			query+=" and x."+ propiedad +" like " + "'"+ valor+"%'";
	            		}else if (tipocampo.trim().equals("char")){ //Si el tipocampo es char
	            			query+=" and x."+ propiedad +" = " + "'"+ valor+"'";
	            		}else if (tipocampo.trim().equals("number")){ //Si el tipocampo es number
	            		    query+=" and x."+ propiedad +" = " + valor;
	            		}else if (tipocampo.trim().equals("date")){ //Si el tipocampo es date
            			    query+=" and x."+ propiedad +" = " + "'"+ valor+"'";
            			}
            		}
            	}
            	List<Sort> sorts = sort.getSorts();
            	String property = null;
            	String order = null;
            	String ordenacion = "";
            	int i = 0;
            	for (Sort s : sorts) {
            		property = s.getProperty();
                    order = s.getOrder().toString();
                    ordenacion += "x."+ property +" "+order;
                    if (i < sorts.size() -1){
                    	ordenacion += ", ";
                    }
                    i++;
            	}

            	if (!StringUtils.nullToString(ordenacion).equals(""))
            		query+=" order by "+ ordenacion;
            	
            	logger.debug("Consulta TableDataDaoImpl: " + query);
            	
         		Query hql= session.createQuery(query);

         		// Paginación
    			hql.setFirstResult(rowStart);
    			hql.setMaxResults(rowEnd - rowStart);
    			
    			List<Object> lista = hql.list();
    			List listaItems = new ArrayList();
    			if (!StringUtils.nullToString(claseItem).equals("")){
    				logger.debug("Con claseItem. Devolvemos la lista modificada");
    				Class c = Class.forName(claseItem);
    				for (Object obj:lista){
    					Object objItem = c.getConstructor(obj.getClass()).newInstance(obj); 
    					listaItems.add(objItem);
    					//logger.debug(objItem.toString());
    				}
    			}
    			else{
    				logger.debug("No hay claseItem. Devolvemos la lista original");
    				listaItems = lista;
    			}
    			return listaItems;
    			//return hql.list();
             
             }catch (Exception e) {
            	 logger.error("Error al obtener el listado genérico", e);
            	 return null;
      	     }
            }  
         });
     	 
         return applications;

    }
   
    public Map<String, Object> getTableDataByUniqueIds(String property, List<String> uniqueIds) {
    	//De momento no es necesario
        return null;
    }
}
