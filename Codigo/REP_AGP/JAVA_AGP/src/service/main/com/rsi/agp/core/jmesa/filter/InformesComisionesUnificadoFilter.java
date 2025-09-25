package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.service.impl.InformesComisionesUnificadoService;

public class InformesComisionesUnificadoFilter extends GenericoFilter 
{
	@SuppressWarnings("unchecked")
	@Override
	public Criteria execute(final Criteria criteria) {
		for (Filter filter : this.filters) {
			
			if (InformesComisionesUnificadoService.LISTA_ENTIDADES_USUARIO.equals( filter.getProperty())) {
				List<BigDecimal> l=(List<BigDecimal>) filter.getValue();
				ArrayList<BigDecimal> listEnt = new ArrayList<BigDecimal>();
				for (BigDecimal value : l) {
	 				listEnt.add(value);
	 			}
				criteria.add(Restrictions.in("codentidad", listEnt));	
			}else if(InformesComisionesUnificadoService.FECHA_CARGA_ENTRE.equals(filter.getProperty())) {
				criteria.add(this.getCriteriaBetween((String)filter.getValue(),InformesComisionesUnificadoService.FECHA_CARGA));				
			}else if(InformesComisionesUnificadoService.FECHA_EMISION_RECIBO_ENTRE.equals(filter.getProperty())) {
				criteria.add(this.getCriteriaBetween((String)filter.getValue(),InformesComisionesUnificadoService.FECHA_EMISION_RECIBO));
			}else if(InformesComisionesUnificadoService.FECHA_ACEPTACION_ENTRE.equals(filter.getProperty())) {
				criteria.add(this.getCriteriaBetween((String)filter.getValue(),InformesComisionesUnificadoService.FECHA_ACEPTACION));
			}else if(InformesComisionesUnificadoService.FECHA_CIERRE_ENTRE.equals(filter.getProperty())) {
				criteria.add(this.getCriteriaBetween((String)filter.getValue(),InformesComisionesUnificadoService.FECHA_CIERRE));
			}else if(InformesComisionesUnificadoService.FECHA_VIGOR_ENTRE.equals(filter.getProperty())) {
				criteria.add(this.getCriteriaBetween((String)filter.getValue(),InformesComisionesUnificadoService.FECHA_VIGOR));
			}else {
				this.buildCriteria(criteria, filter.getProperty(),
						filter.getValue(), filter.getTipo());
			}
			
		}
		

		
		return criteria;
	}
	
	private Criterion getCriteriaBetween(String valoresEntre, String nombrePropiedad) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Criterion res=null;
		String valor[]=null;
		valor=valoresEntre.split(",");
		Date fechaDesde=null;
		Date fechaHasta=null;
		
		try {
			fechaDesde = df.parse(valor[0].trim());
			fechaHasta = df.parse(valor[1].trim());
		} catch (ParseException e) {
			logger.error(e);
		}
		
		res=Restrictions.between(nombrePropiedad,fechaDesde,fechaHasta);
		
		return res;
	}
}
