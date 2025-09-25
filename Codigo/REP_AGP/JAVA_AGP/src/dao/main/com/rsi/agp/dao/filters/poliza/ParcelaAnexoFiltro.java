
package com.rsi.agp.dao.filters.poliza;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import static com.rsi.agp.core.util.Constants.*;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.anexo.Parcela;


public class ParcelaAnexoFiltro implements Filter 
{	
	private Parcela parcela;
	private String columna;
	private String orden;
	
	public ParcelaAnexoFiltro(){  }
	
	
	public ParcelaAnexoFiltro(Parcela parcela){
		this.parcela=parcela;
	}
	
	public ParcelaAnexoFiltro(Parcela parcela,String columna, String orden){
		this.parcela=parcela;
		this.columna = columna;
		this.orden = orden;
	}
		

	@Override
	public final Criteria getCriteria(final Session sesion) 
	{
		//generamos un mapa con todas las columnas del listado para pasarlas al criteria.
			Map<String, String> columnas = new HashMap<String, String>();
			columnas.put("2","codprovincia");
			columnas.put("3","codcomarca");
			columnas.put("4","codtermino");
			columnas.put("5","subtermino");
			columnas.put("6","codcultivo");
			columnas.put("7","codvariedad");
			columnas.put("8","codprovsigpac#codtermsigpac#agrsigpac#zonasigpac#poligonosigpac#parcelasigpac#recintosigpac#tipoparcela");
			columnas.put("9","ca.superficie");
			columnas.put("10","ca.precio");
			columnas.put("11","ca.produccion");
			
		
		final Criteria criteria = sesion.createCriteria(Parcela.class);
		criteria.createAlias("capitalAsegurados", "ca", CriteriaSpecification.LEFT_JOIN);
			
		if(StringUtils.nullToString(columna).equals("")){
			criteria.addOrder(Order.asc("codprovincia"));
			criteria.addOrder(Order.asc("codcomarca"));
			criteria.addOrder(Order.asc("codtermino"));
			criteria.addOrder(Order.asc("subtermino"));
			criteria.addOrder(Order.asc("codcultivo"));
			criteria.addOrder(Order.asc("codvariedad"));
			 
			criteria.addOrder(Order.asc("codprovsigpac"));
			criteria.addOrder(Order.asc("codtermsigpac"));		
			criteria.addOrder(Order.asc("agrsigpac"));
			criteria.addOrder(Order.asc("zonasigpac"));
			criteria.addOrder(Order.asc("poligonosigpac"));
			criteria.addOrder(Order.asc("parcelasigpac"));
			criteria.addOrder(Order.asc("recintosigpac"));
			criteria.addOrder(Order.desc("tipoparcela"));
			
			// por ultimo ordenamos las parcelas por hoja y numero
			criteria.addOrder(Order.asc("hoja"));
			criteria.addOrder(Order.asc("numero"));
		}else{
			if (columnas.containsKey(columna)){
				columna = columnas.get(columna);
			}
			String[] columnaOrden = columna.split("#");
			
			if(("desc").equals(orden)){
				for(int i=0; i<columnaOrden.length; i++){
					criteria.addOrder(Order.desc(columnaOrden[i]));
				}
			}
			else
				for(int i=0; i<columnaOrden.length; i++){
					criteria.addOrder(Order.asc(columnaOrden[i]));
				}
		}
		
		
		
		if (parcela != null)
		{
			// Idanexo
			if(parcela.getAnexoModificacion().getId() != null){
				Criterion crit1 = Restrictions.eq("anexoModificacion.id", parcela.getAnexoModificacion().getId());
		        criteria.add(crit1);
			}
			//***************** Ubicacion *****************
			
			
			
	        // Provincia
	        if(parcela.getCodprovincia() != null){
		        Criterion crit2 = Restrictions.eq("codprovincia", parcela.getCodprovincia());
		        criteria.add(crit2);
	        }
	        // Comarca
	        if(parcela.getCodcomarca() != null){
		        Criterion crit12 = Restrictions.eq("codcomarca", parcela.getCodcomarca());
		        criteria.add(crit12);
	        }
	        // Termino
	        if(parcela.getCodtermino() != null){
		        Criterion crit3 = Restrictions.eq("codtermino", parcela.getCodtermino());
		        criteria.add(crit3);
		        
	        }
	        //Subtermino
	        if(parcela.getSubtermino() != null){		        
		        Criterion crit4 = Restrictions.eq("subtermino", parcela.getSubtermino());
		        criteria.add(crit4);
		        
	        }
	      //cultivo
	        if(parcela.getCodcultivo()!=null){
	        	 Criterion crit5 = Restrictions.eq("codcultivo", parcela.getCodcultivo());
			      criteria.add(crit5);
			      
	        }
	        //variedad
	        if(parcela.getCodvariedad()!=null){
	        	 Criterion crit5 = Restrictions.eq("codvariedad", parcela.getCodvariedad());
			      criteria.add(crit5);
			     
	        }
	        //nombre
	        if(parcela.getNomparcela()!=null && !"".equals(parcela.getNomparcela())){
	        	Criterion crit6 = Restrictions.like("nomparcela", "%" + parcela.getNomparcela() + "%");
	        	criteria.add(crit6);	        	
	        }
	        //***************** Ident Catastral *****************
	        //Poligono
	        if(parcela.getPoligono()!=null && !"".equals(parcela.getPoligono())){
	        	Criterion crit7 = Restrictions.eq("poligono", parcela.getPoligono());
	        	criteria.add(crit7);
	        }
	        /*
			//Parcela
	        if(parcela.getParcela()!=null && !"".equals(parcela.getParcela())){
	        	Criterion crit8 = Restrictions.eq("parcela", parcela.getParcela());
	        	criteria.add(crit8);
	        }
	        */
	        //***************** SIGPAC *****************
	        //Prov
	        if(parcela.getCodprovsigpac()!=null){
		        Criterion crit9 = Restrictions.eq("codprovsigpac",parcela.getCodprovsigpac());
		        criteria.add(crit9);
	        }
	        //Term
	        if(parcela.getCodtermsigpac()!=null){
		        Criterion crit10 = Restrictions.eq("codtermsigpac",parcela.getCodtermsigpac());
		        criteria.add(crit10);
	        }
	        // Agr
	        if(parcela.getAgrsigpac()!=null){
		        Criterion crit11 = Restrictions.eq("agrsigpac",parcela.getAgrsigpac());
		        criteria.add(crit11);
	        }
	        // Zona
	        if(parcela.getZonasigpac()!=null){
		        Criterion crit12 = Restrictions.eq("zonasigpac",parcela.getZonasigpac());
		        criteria.add(crit12);
	        }
	        // Poligono
	        if(parcela.getPoligonosigpac()!=null){
		        Criterion crit13 = Restrictions.eq("poligonosigpac",parcela.getPoligonosigpac());
		        criteria.add(crit13);
	        }
	        //Parcela
	        if(parcela.getParcelasigpac()!=null){
		        Criterion crit14 = Restrictions.eq("parcelasigpac",parcela.getParcelasigpac());
		        criteria.add(crit14);
	        }
	        // Recinto
	        if(parcela.getRecintosigpac()!=null){
		        Criterion crit15 = Restrictions.eq("recintosigpac",parcela.getRecintosigpac());
		        criteria.add(crit15);
	        }
	        // Tipo parcela
	        if(parcela.getTipoparcela()!=null && !"".equals(parcela.getTipoparcela().toString())){
		        Criterion crit16 = Restrictions.eq("tipoparcela",parcela.getTipoparcela());
		        criteria.add(crit16);
	        }
	        
	        if(parcela.getTipomodificacion()!= null && parcela.getTipomodificacion().equals(TODOSMENOSBAJA)){ 
 		        criteria.add(
 		        		   Restrictions.disjunction()
 		        		   .add(Restrictions.isNull("tipomodificacion"))
 		        		   .add(Restrictions.ne("tipomodificacion",BAJA))
 		        				  );
	        }else if(parcela.getTipomodificacion() != null){ //tipo modificacion
					Criterion critAA = Restrictions.eq("tipomodificacion", parcela.getTipomodificacion());
			        criteria.add(critAA);
			}
	        
	        // Hoja
	        if (parcela.getHoja() != null) {
		        criteria.add(Restrictions.eq("hoja",parcela.getHoja()));
	        }
	        // Numero
	        if (parcela.getNumero() != null) {
		        criteria.add(Restrictions.eq("numero",parcela.getNumero()));
	        }
	        
	        // Produccion
	        
	        
	        // Superficie
	       
	        
	}

		return criteria;
	}
}