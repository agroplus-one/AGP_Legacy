package com.rsi.agp.core.jmesa.filter.gan;

import org.hibernate.Criteria;

import com.rsi.agp.core.jmesa.filter.GenericoFilter;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;

public class ClaseDetalleGanadoFilter extends GenericoFilter implements
		IGenericoFilter {

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : this.filters) {
			this.buildCriteria(criteria, filter.getProperty(), filter.getValue(),filter.getTipo());
        }   
        return criteria;
	}
	
	public String getSqlWhere() {
		String sqlWhere= "WHERE 1=1";
	
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty(); 
				
				//Id
				if (property.equals("clase.id")){
					sqlWhere += " AND D.IDCLASE = "+ filter.getValue();
				}
				//Modulo
				if (property.equals("codmodulo")){
					sqlWhere += " AND D.CODMODULO = '" + filter.getValue()+"'";
				}
				//lineaSeguroId
				if (property.equals("lineaseguroid")){
					sqlWhere += " AND D.LINEASEGUROID = "+ filter.getValue();
				}
				//CodEspecie
				if (property.equals("codespecie")) {
					sqlWhere += " AND D.CODESPECIE = "+ filter.getValue();
				}
				//CodRegimenManejo
				if (property.equals("codregimen")) {
					sqlWhere += " AND D.CODREGIMEN = "+ filter.getValue();
				}
				//CodGrupoRaza
				if (property.equals("codgruporaza")) {
					sqlWhere += " AND D.CODGRUPORAZA = "+ filter.getValue();
				}
				//codTipoAnimal
				if (property.equals("codtipoanimal")) {
					sqlWhere += " AND D.CODTIPOANIMAL = "+ filter.getValue();
				}
				//codTipoCapital
				if (property.equals("codtipocapital")) {
					sqlWhere += " AND D.CODTIPOCAPITAL = "+ filter.getValue();
				}
				
				//Provincia
				if (property.equals("codprovincia")){
					sqlWhere += " AND D.CODPROVINCIA = " + filter.getValue();
				}
				//Comarca
				if (property.equals("codcomarca")){
					sqlWhere += " AND D.CODCOMARCA = " + filter.getValue();
				}
				//Termino
				if (property.equals("codtermino")){
					sqlWhere += " AND D.CODTERMINO = " + filter.getValue();
				}
				//Subtermino
				if (property.equals("subtermino")){
					sqlWhere += " AND D.SUBTERMINO = '" + filter.getValue()+"'";
				}
				
			}
		}catch (Exception e) {
			logger.error("ClaseDetalleGanadoFilter - Error creando el filtro -"+e.getMessage());
		}  
		return sqlWhere;

	}
	
	public String getSqlWhere(Long id) {
		String sqlWhere= "";
		
		try {
			sqlWhere = getSqlWhere();
		
			
				if (null!=id){
					sqlWhere += " AND D.ID <> '" + id.toString()+"'";
				}
			
		}catch (Exception e) {
			logger.error("ClaseDetalleGanadoFilter - Error creando el filtro -"+e.getMessage());
		}  
		return sqlWhere;
	}
}
	

