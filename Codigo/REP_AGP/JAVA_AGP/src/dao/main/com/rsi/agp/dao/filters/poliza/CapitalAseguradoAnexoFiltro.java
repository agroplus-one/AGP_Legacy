

package com.rsi.agp.dao.filters.poliza;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.rsi.agp.core.webapp.util.StringUtils;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Parcela;

public class CapitalAseguradoAnexoFiltro implements Filter 
{	 
	private CapitalAsegurado capitalAsegurado;
	private String columna;
	private String orden;
	
	public CapitalAseguradoAnexoFiltro(){  }
	
	public CapitalAseguradoAnexoFiltro(CapitalAsegurado capitalAsegurado){
		this.capitalAsegurado = capitalAsegurado;
	}
	

	public CapitalAseguradoAnexoFiltro(CapitalAsegurado capitalAsegurado,String columna, String orden){
		this.capitalAsegurado=capitalAsegurado;
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

		/* DAA 11/04/12
		 * Modificamos criterios de busqueda
		*/
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
		if (capitalAsegurado.getParcela() != null) { 
			// Idanexo
			if(capitalAsegurado.getParcela().getAnexoModificacion().getId() != null){
				criteria.add(Restrictions.eq("anexoModificacion.id", capitalAsegurado.getParcela().getAnexoModificacion().getId()));
			}

			//tipo modificacion
			if(capitalAsegurado.getParcela().getTipomodificacion() != null){
				if("A".equals(capitalAsegurado.getParcela().getTipomodificacion().toString()) || 
						"M".equals(capitalAsegurado.getParcela().getTipomodificacion().toString()) || 
						"B".equals(capitalAsegurado.getParcela().getTipomodificacion().toString())){
					criteria.add(Restrictions.eq("tipomodificacion", capitalAsegurado.getParcela().getTipomodificacion()));
				}
			}

			// Provincia
			if(capitalAsegurado.getParcela().getCodprovincia() != null){
				criteria.add(Restrictions.eq("codprovincia", capitalAsegurado.getParcela().getCodprovincia()));
			}
			
			// Comarca
			if(capitalAsegurado.getParcela().getCodcomarca() != null){
				criteria.add(Restrictions.eq("codcomarca", capitalAsegurado.getParcela().getCodcomarca()));
			}
			
			// Termino
			if(capitalAsegurado.getParcela().getCodtermino() != null){
				criteria.add(Restrictions.eq("codtermino", capitalAsegurado.getParcela().getCodtermino()));
			}
			
			//Subtermino
			if(capitalAsegurado.getParcela().getSubtermino() != null){
				criteria.add(Restrictions.eq("subtermino", capitalAsegurado.getParcela().getSubtermino()));
			}
			
			//cultivo
			if(capitalAsegurado.getParcela().getCodcultivo()!=null){
				criteria.add(Restrictions.eq("codcultivo", capitalAsegurado.getParcela().getCodcultivo()));
			}
			
			//variedad
			if(capitalAsegurado.getParcela().getCodvariedad()!=null){
				criteria.add(Restrictions.eq("codvariedad", capitalAsegurado.getParcela().getCodvariedad()));
			}
			
			//nombre
			if(capitalAsegurado.getParcela().getNomparcela()!=null && !"".equals(capitalAsegurado.getParcela().getNomparcela())){
				criteria.add(Restrictions.like("nomparcela", "%" + capitalAsegurado.getParcela().getNomparcela() + "%"));
			}
			
			//***************** Ident Catastral *****************
			//Poligono
			if(capitalAsegurado.getParcela().getPoligono()!=null && !"".equals(capitalAsegurado.getParcela().getPoligono())){
				criteria.add(Restrictions.eq("poligono", capitalAsegurado.getParcela().getPoligono()));
			}
			
			//Parcela
			if(capitalAsegurado.getParcela().getParcelasigpac()!=null){
				criteria.add(Restrictions.eq("parcelasigpac", capitalAsegurado.getParcela().getParcelasigpac()));
			}
			
			//***************** SIGPAC *****************
			//Prov
			if(capitalAsegurado.getParcela().getCodprovsigpac()!=null){
				criteria.add(Restrictions.eq("codprovsigpac",capitalAsegurado.getParcela().getCodprovsigpac()));
			}
			//Term
			if(capitalAsegurado.getParcela().getCodtermsigpac()!=null){
				criteria.add(Restrictions.eq("codtermsigpac",capitalAsegurado.getParcela().getCodtermsigpac()));
			}
			// Agr
			if(capitalAsegurado.getParcela().getAgrsigpac()!=null){
				criteria.add(Restrictions.eq("agrsigpac",capitalAsegurado.getParcela().getAgrsigpac()));
			}
			// Zona
			if(capitalAsegurado.getParcela().getZonasigpac()!=null){
				criteria.add(Restrictions.eq("zonasigpac",capitalAsegurado.getParcela().getZonasigpac()));
			}
			// Poligono
			if(capitalAsegurado.getParcela().getPoligonosigpac()!=null){
				criteria.add(Restrictions.eq("poligonosigpac",capitalAsegurado.getParcela().getPoligonosigpac()));
			}
			//Parcela
			if(capitalAsegurado.getParcela().getParcelasigpac()!=null){
				criteria.add(Restrictions.eq("parcelasigpac",capitalAsegurado.getParcela().getParcelasigpac()));
			}
			// Recinto
			if(capitalAsegurado.getParcela().getRecintosigpac()!=null){
				criteria.add(Restrictions.eq("recintosigpac",capitalAsegurado.getParcela().getRecintosigpac()));
			}
			
			// Tipo parcela
			if(capitalAsegurado.getParcela().getTipoparcela()!=null && !"".equals(capitalAsegurado.getParcela().getTipoparcela().toString())){
				criteria.add(Restrictions.eq("tipoparcela",capitalAsegurado.getParcela().getTipoparcela()));
			}
			
			// Tipo capital
			if (capitalAsegurado.getTipoCapital() != null && capitalAsegurado.getTipoCapital().getCodtipocapital() != null)
				criteria.add(Restrictions.eq("ca.tipoCapital.codtipocapital", capitalAsegurado.getTipoCapital().getCodtipocapital()));
			
			// Produccion
			if(capitalAsegurado.getProduccion()!=null){
				criteria.add(Restrictions.eq("ca.produccion", capitalAsegurado.getProduccion()));
			}
			
			// Superficie
			if(capitalAsegurado.getSuperficie()!=null){
				criteria.add(Restrictions.eq("ca.superficie", capitalAsegurado.getSuperficie()));
			}
			
			//Hoja
			if(capitalAsegurado.getParcela().getHoja()!=null){
				criteria.add(Restrictions.eq("hoja",capitalAsegurado.getParcela().getHoja()));
			}
			//Numero
			if(capitalAsegurado.getParcela().getNumero()!=null){
				criteria.add(Restrictions.eq("numero",capitalAsegurado.getParcela().getNumero()));
			}
			
		}

		return criteria;
	}
}