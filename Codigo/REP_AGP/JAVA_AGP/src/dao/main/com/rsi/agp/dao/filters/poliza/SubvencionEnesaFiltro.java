package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class SubvencionEnesaFiltro implements Filter 
{
	private Poliza poliza;
	private List<String> listCodigosModulos = null;
	private boolean dameMax = false;
	private BigDecimal tipoSubvencion;
	private Parcela parcela;
	
	public SubvencionEnesaFiltro() 
	{
	
	}
	
	public SubvencionEnesaFiltro(Poliza poliza) 
	{
		this.poliza = poliza;
		listCodigosModulos = new ArrayList<String>();
	}
	
	@Override
	public final Criteria getCriteria(final Session sesion) 
	{
		final Criteria criteria = sesion.createCriteria(SubvencionEnesa.class);
		Set<Parcela> parcelas = null;
		
		if (parcela != null)
		{
			parcelas = new HashSet<Parcela>();
			parcelas.add(parcela);
		}
		else
		{
			parcelas = poliza.getParcelas();
		}

		String moduloPoliza = poliza.getCodmodulo();
		
//		if (moduloPoliza != null)
//		{			
//			Criterion crit = Restrictions.eq("id.codmodulo", moduloPoliza);
//			criteria.add(crit);
//		}

		if (parcelas != null)
		{
			if(listCodigosModulos.size() == 0){
				listCodigosModulos.add("99999");
			}
			
			Disjunction dis = Restrictions.disjunction();//OR
			
			for (Parcela p: parcelas)
			{
				Criterion c = Restrictions.conjunction()
				
				.add(
						Restrictions.in("modulo.id.codmodulo", listCodigosModulos)
			    )
			    .add(
						Restrictions.disjunction() //OR
							.add(Restrictions.eq("variedad.id.codcultivo", p.getCodcultivo()))//Le decimos que nos devuelva el cod. igual a 999, y el que le pasamos
							.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal("999")))
				)//AND
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("variedad.id.codvariedad", p.getCodvariedad()))
							.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal("999")))
				)
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("codprovincia", p.getTermino().getId().getCodprovincia()))
							.add(Restrictions.eq("codprovincia", new BigDecimal("99")))
				)
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("codtermino", p.getTermino().getId().getCodtermino()))
							.add(Restrictions.eq("codtermino", new BigDecimal("999")))
				)
				.add(
						Restrictions.disjunction()
							.add(Restrictions.eq("subtermino", p.getTermino().getId().getSubtermino()))	
							.add(Restrictions.eq("subtermino", new Character('9')))			
				);				
				//por cada grupo de AND añadimos un OR
				
				
				
				dis.add(c);				
			}			
			criteria.add(dis);
			if (this.tipoSubvencion != null)
			{
				Criterion c = Restrictions.eq("tipoSubvencionEnesa.codtiposubvenesa", this.getTipoSubvencion());
				criteria.add(c);
			}
		}
		
		Criterion crit = Restrictions.eq("id.lineaseguroid", poliza.getLinea().getLineaseguroid());		
		criteria.add(crit);
		if (dameMax)
		{
			criteria.setProjection(Projections.max("pctsubvindividual"));
		}
		else
		{
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//distinct en lugar de un id, de todo el modulo
		}
		
		return criteria;
	}

	public Poliza getPoliza() {
		return poliza;
	}

	public void setPoliza(Poliza poliza) {
		this.poliza = poliza;
	}

	public boolean isDameMax() {
		return dameMax;
	}

	public void setDameMax(boolean dameMax) {
		this.dameMax = dameMax;
	}

	public BigDecimal getTipoSubvencion() {
		return tipoSubvencion;
	}

	public void setTipoSubvencion(BigDecimal tipoSubvencion) {
		this.tipoSubvencion = tipoSubvencion;
	}

	public Parcela getParcela() {
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}

	public List<String> getListCodigosModulos() {
		return listCodigosModulos;
	}

	public void setListCodigosModulos(List<String> listCodigosModulos) {
		this.listCodigosModulos = listCodigosModulos;
	}

}
