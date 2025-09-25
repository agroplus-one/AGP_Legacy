package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class ParcelaFiltro implements Filter {

	private final Log logger = LogFactory.getLog(this.getClass());

	private Parcela parcela;
	private String columna;
	private String orden;

	public ParcelaFiltro() {
	}

	public ParcelaFiltro(Parcela parcela) {
		this.parcela = parcela;
	}

	public ParcelaFiltro(Parcela parcela, String columna, String orden) {
		this.parcela = parcela;
		this.columna = columna;
		this.orden = orden;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {

		// generamos un mapa con todas las columnas del listado para pasarlas al
		// criteria.
		Map<String, String> columnas = new HashMap<String, String>();
		columnas.put("1", "termino.id.codprovincia");
		columnas.put("2", "termino.id.codcomarca");
		columnas.put("3", "termino.id.codtermino");
		columnas.put("4", "termino.id.subtermino");
		columnas.put("5", "codcultivo");
		columnas.put("6", "codvariedad");
		columnas.put("7", "hoja#numero");
		columnas.put("8",
				"codprovsigpac#codtermsigpac#agrsigpac#zonasigpac#poligonosigpac#parcelasigpac#recintosigpac#tipoparcela");
		columnas.put("9", "nomparcela");
		columnas.put("10", "capitalAsegurados.superficie");
		columnas.put("11", "capAsegRelModulos.produccion");
		columnas.put("12", "capAsegRelModulos.precio");
		columnas.put("13", "tipo.destipocapital");

		final Criteria criteria = sesion.createCriteria(Parcela.class);

		criteria.createAlias("termino", "term");
		criteria.createAlias("capitalAseguradosSinOrden", "capitalAsegurados", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("capitalAseguradosSinOrden.tipoCapital", "tipo", CriteriaSpecification.LEFT_JOIN);

		if (StringUtils.nullToString(columna).equals("")) {
			criteria.addOrder(Order.asc("term.id.codprovincia"));
			criteria.addOrder(Order.asc("term.id.codcomarca"));
			criteria.addOrder(Order.asc("term.id.codtermino"));
			criteria.addOrder(Order.asc("term.id.subtermino"));
			criteria.addOrder(Order.asc("codcultivo"));
			criteria.addOrder(Order.asc("codvariedad"));
			criteria.addOrder(Order.asc("codprovsigpac"));
			criteria.addOrder(Order.asc("agrsigpac"));
			criteria.addOrder(Order.asc("zonasigpac"));
			criteria.addOrder(Order.asc("poligonosigpac"));
			criteria.addOrder(Order.asc("parcelasigpac"));
			criteria.addOrder(Order.asc("recintosigpac"));
			criteria.addOrder(Order.asc("tipo.codtipocapital"));

		} else {

			// tratamiento especial para las columnas de Produccion y Precio
			if (("11").equals(columna) || ("12").equals(columna)) {
				criteria.createAlias("capitalAsegurados.capAsegRelModulos", "capAsegRelModulos",
						CriteriaSpecification.LEFT_JOIN);
			}

			// ASF: ÑAPA TEMPORAL PARA LA SUBIDA DEL 3/1/2013 HASTA QUE VEAMOS COMO
			// SOLUCIONARLO
			logger.debug("ParcelaFiltro - columna = " + columna);
			if (columnas.containsKey(columna)) {
				columna = columnas.get(columna);
			}
			String[] columnaOrden = columna.split("#");

			if (("desc").equals(orden)) {
				for (int i = 0; i < columnaOrden.length; i++) {
					criteria.addOrder(Order.desc(columnaOrden[i]));
				}
			} else
				for (int i = 0; i < columnaOrden.length; i++) {
					criteria.addOrder(Order.asc(columnaOrden[i]));
				}
		}

		if (parcela != null) {

			// Poliza
			if (parcela.getPoliza().getIdpoliza() != null) {
				Criterion crit1 = Restrictions.eq("poliza.idpoliza", parcela.getPoliza().getIdpoliza());
				criteria.add(crit1);
			}
			// ***************** Ubicacion *****************
			// Provincia
			if (parcela.getTermino().getId().getCodprovincia() != null) {
				Criterion crit2 = Restrictions.eq("termino.id.codprovincia",
						parcela.getTermino().getId().getCodprovincia());
				criteria.add(crit2);
			}
			// Comarca
			if (parcela.getTermino().getId().getCodcomarca() != null) {
				Criterion crit12 = Restrictions.eq("termino.id.codcomarca",
						parcela.getTermino().getId().getCodcomarca());
				criteria.add(crit12);
			}
			// Termino
			if (parcela.getTermino().getId().getCodtermino() != null) {
				Criterion crit3 = Restrictions.eq("termino.id.codtermino",
						parcela.getTermino().getId().getCodtermino());
				criteria.add(crit3);

			}
			// Subtermino
			if (parcela.getTermino().getId().getSubtermino() != null) {
				Criterion crit4 = Restrictions.eq("termino.id.subtermino",
						parcela.getTermino().getId().getSubtermino());
				criteria.add(crit4);

			}
			// cultivo
			if (parcela.getCodcultivo() != null) {
				Criterion crit5 = Restrictions.eq("codcultivo", parcela.getCodcultivo());
				criteria.add(crit5);

			}
			// variedad
			if (parcela.getCodvariedad() != null) {
				Criterion crit5 = Restrictions.eq("codvariedad", parcela.getCodvariedad());
				criteria.add(crit5);

			}
			// nombre
			if (parcela.getNomparcela() != null && !"".equals(parcela.getNomparcela())) {
				Criterion crit6 = Restrictions.like("nomparcela", "%" + parcela.getNomparcela() + "%");
				criteria.add(crit6);
			}
			// ***************** Ident Catastral *****************
			// Poligono
			if (parcela.getPoligono() != null && !"".equals(parcela.getPoligono())) {
				Criterion crit7 = Restrictions.eq("poligono", parcela.getPoligono());
				criteria.add(crit7);
			}
			// Parcela
			if (parcela.getParcela() != null && !"".equals(parcela.getParcela())) {
				Criterion crit8 = Restrictions.eq("parcela", parcela.getParcela());
				criteria.add(crit8);
			}
			// ***************** SIGPAC *****************
			// Prov
			if (parcela.getCodprovsigpac() != null) {
				Criterion crit9 = Restrictions.eq("codprovsigpac", parcela.getCodprovsigpac());
				criteria.add(crit9);
			}
			// Term
			if (parcela.getCodtermsigpac() != null) {
				Criterion crit10 = Restrictions.eq("codtermsigpac", parcela.getCodtermsigpac());
				criteria.add(crit10);
			}
			// Agr
			if (parcela.getAgrsigpac() != null) {
				Criterion crit11 = Restrictions.eq("agrsigpac", parcela.getAgrsigpac());
				criteria.add(crit11);
			}
			// Zona
			if (parcela.getZonasigpac() != null) {
				Criterion crit12 = Restrictions.eq("zonasigpac", parcela.getZonasigpac());
				criteria.add(crit12);
			}
			// Poligono
			if (parcela.getPoligonosigpac() != null) {
				Criterion crit13 = Restrictions.eq("poligonosigpac", parcela.getPoligonosigpac());
				criteria.add(crit13);
			}
			// Parcela
			if (parcela.getParcelasigpac() != null) {
				Criterion crit14 = Restrictions.eq("parcelasigpac", parcela.getParcelasigpac());
				criteria.add(crit14);
			}
			// Recinto
			if (parcela.getRecintosigpac() != null) {
				Criterion crit15 = Restrictions.eq("recintosigpac", parcela.getRecintosigpac());
				criteria.add(crit15);
			}
			// Tipo parcela
			if (parcela.getTipoparcela() != null && !"".equals(parcela.getTipoparcela().toString())) {
				Criterion crit16 = Restrictions.eq("tipoparcela", parcela.getTipoparcela());
				criteria.add(crit16);
			}
			// Hoja
			if (parcela.getHoja() != null) {
				criteria.add(Restrictions.eq("hoja", parcela.getHoja()));
			}
			// Numero
			if (parcela.getNumero() != null) {
				criteria.add(Restrictions.eq("numero", parcela.getNumero()));
			}
			// Tipo de Capital
			if (parcela.getCapitalAsegurados() != null && parcela.getCapitalAsegurados().size() > 0) {
				List<BigDecimal> listaCodCap = new ArrayList<BigDecimal>();
				Iterator<CapitalAsegurado> it = parcela.getCapitalAsegurados().iterator();
				while (it.hasNext()) {
					CapitalAsegurado ca = it.next();
					if (null != ca.getTipoCapital() && null != ca.getTipoCapital().getCodtipocapital()) {
						listaCodCap.add(ca.getTipoCapital().getCodtipocapital());

					}
				}
				if (listaCodCap.size() > 0)
					criteria.add(Restrictions.in("capitalAsegurados.tipoCapital.codtipocapital", listaCodCap));
			}
		}

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return criteria;
	}
}
