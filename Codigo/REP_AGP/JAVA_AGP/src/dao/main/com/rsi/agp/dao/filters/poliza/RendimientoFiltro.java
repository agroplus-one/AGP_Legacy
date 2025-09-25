package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class RendimientoFiltro implements Filter{

	private Modulo modulo;
	private Variedad variedad;
	private Termino termino;
	
	public RendimientoFiltro() {
		super();
	}
	
	public RendimientoFiltro(final Modulo modulo, final Variedad variedad, final Termino termino){
		this.modulo = modulo;
		this.variedad = variedad;
		this.termino = termino;
	}
	
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(LimiteRendimiento.class);
		
		criteria.add(getRendimientos2());
        
        return criteria;
	}

	private final Map<String, Object> getRendimientos() {

		final Map<String, Object> mapa = new HashMap<String, Object>();

		/* PROPIEDADES DE MODULO */
		final Long lineaseguroid = modulo.getId().getLineaseguroid();
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			mapa.put("modulo.id.lineaseguroid", lineaseguroid);
			mapa.put("variedad.id.lineaseguroid", lineaseguroid);
		}
	
		final String codmodulo = modulo.getId().getCodmodulo();
		if (FiltroUtils.noEstaVacio(codmodulo)) {
			mapa.put("modulo.id.codmodulo", codmodulo);
		}
		
		/*final Long lineaseguroid =  variedad.getId().getLineaseguroid();
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			mapa.put("lineaseguroid", lineaseguroid);
		}*/

		/* PROPIEDADES DE VARIEDAD */
		final BigDecimal codcultivo = variedad.getId().getCodcultivo();
		if (FiltroUtils.noEstaVacio(codcultivo)) {
			mapa.put("variedad.id.codcultivo", codcultivo);
		}
		
		final BigDecimal codvariedad = variedad.getId().getCodvariedad();
		if (FiltroUtils.noEstaVacio(codvariedad)) {
			mapa.put("variedad.id.codvariedad", codvariedad);
		}
		
		/* PROPIEDADES DE TERMINO */
		final BigDecimal codprovincia = termino.getId().getCodprovincia();
		if (FiltroUtils.noEstaVacio(codprovincia)) {
			mapa.put("codprovincia", codprovincia);
		}
		
		final BigDecimal codtermino = termino.getId().getCodtermino();
		if (FiltroUtils.noEstaVacio(codtermino)) {
			mapa.put("codtermino", codtermino);
		}
		
		final Character subtermino = termino.getId().getSubtermino();
		if (FiltroUtils.noEstaVacio(subtermino)) {
			mapa.put("subtermino", subtermino);
		}

		final BigDecimal codComarca = termino.getId().getCodcomarca();
		if (FiltroUtils.noEstaVacio(codComarca)) {
			mapa.put("codcomarca", codComarca);
		}

		return mapa;
	}
	
	private final Conjunction getRendimientos2() {

		Conjunction c = Restrictions.conjunction();
		
		/* PROPIEDADES DE MODULO */
		final Long lineaseguroid = modulo.getId().getLineaseguroid();
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			c.add(Restrictions.eq("modulo.id.lineaseguroid", lineaseguroid));
			c.add(Restrictions.eq("variedad.id.lineaseguroid", lineaseguroid));
		}
	
		final String codmodulo = modulo.getId().getCodmodulo();
		if (FiltroUtils.noEstaVacio(codmodulo)) {
			c.add(Restrictions.eq("modulo.id.codmodulo", codmodulo));
		}
		

		/* PROPIEDADES DE VARIEDAD */
		
		c.add(
				
			Restrictions.disjunction()
			.add(Restrictions.eq("variedad.id.codcultivo", variedad.getId().getCodcultivo()))
			.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal("999")))

		).add (
				
			Restrictions.disjunction()
			.add(Restrictions.eq("variedad.id.codvariedad", variedad.getId().getCodvariedad()))
			.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal("999")))
		
		);

		/* PROPIEDADES DE TERMINO */
		
		c.add (
				
				Restrictions.disjunction()
				.add(Restrictions.eq("codprovincia", termino.getId().getCodprovincia()))
				.add(Restrictions.eq("codprovincia", new BigDecimal("99")))
		
		).add (
				
				Restrictions.disjunction()
				.add(Restrictions.eq("codtermino", termino.getId().getCodtermino()))
				.add(Restrictions.eq("codtermino", new BigDecimal("999")))
		
		).add (
				
				Restrictions.disjunction()
				.add(Restrictions.eq("subtermino", termino.getId().getSubtermino()))
				.add(Restrictions.eq("subtermino", new BigDecimal("9")))
		
		).add (
				
				Restrictions.disjunction()
				.add(Restrictions.eq("codcomarca", termino.getId().getCodcomarca()))
				.add(Restrictions.eq("codcomarca", new BigDecimal("99")))
		
		);

		return c;
	}
	
	
}
