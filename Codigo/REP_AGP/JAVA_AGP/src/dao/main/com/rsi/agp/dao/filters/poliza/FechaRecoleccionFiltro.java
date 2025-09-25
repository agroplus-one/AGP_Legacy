package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.FechaRecoleccion;

public class FechaRecoleccionFiltro implements Filter {

	private String codCultivo;
	private String codVariedad;
	private String codTipoCapital;
	private String codprovincia;
	private String codcomarca;
	private String codtermino;
	private String subtermino;
	private String frecolhasta;
	private String codModulo;
	private Long lineaseguroid;

	public FechaRecoleccionFiltro() {
		super();
	}

	public FechaRecoleccionFiltro(String codCultivo, String codVariedad, String codTipoCapital, String codprovincia,
			String codcomarca, String codtermino, String subtermino, String frecolhasta, String codModulo,
			Long lineaseguroid) {

		this.codCultivo = codCultivo;
		this.codVariedad = codVariedad;
		this.codTipoCapital = codTipoCapital;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.frecolhasta = frecolhasta;
		this.codModulo = codModulo;
		this.lineaseguroid = lineaseguroid;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(FechaRecoleccion.class);

		// Modulo
		if (!StringUtils.nullToString(codModulo).equals("")) {
			criteria.add(Restrictions.eq("modulo.id.codmodulo", codModulo));
		}

		// Lineaseguroid
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		}

		// Fecha fin de garantias
		if (FiltroUtils.noEstaVacio(frecolhasta)) {
			criteria.add(Restrictions.eq("frecolhasta", FiltroUtils.getDDMMYYYYDate(frecolhasta)));
		}

		// Tipo capital
		if (!StringUtils.nullToString(codTipoCapital).equals("")) {
			criteria.add(Restrictions.eq("tipoCapital.codtipocapital", new BigDecimal(codTipoCapital)));
		}

		// Provincia
		if (FiltroUtils.noEstaVacio(codprovincia)) {
			criteria.add(
					Restrictions.disjunction().add(Restrictions.eq("codprovincia", new BigDecimal(this.codprovincia)))
							.add(Restrictions.eq("codprovincia", new BigDecimal("99"))));
		}

		// Comarca
		if (FiltroUtils.noEstaVacio(codcomarca)) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("codcomarca", new BigDecimal(this.codcomarca)))
					.add(Restrictions.eq("codcomarca", new BigDecimal("99"))));
		}

		// Termino
		if (FiltroUtils.noEstaVacio(codtermino)) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("codtermino", new BigDecimal(this.codtermino)))
					.add(Restrictions.eq("codtermino", new BigDecimal("999"))));
		}

		// Subtermino
		criteria.add(Restrictions.disjunction().add(Restrictions.eq("subtermino", this.subtermino))
				.add(Restrictions.eq("subtermino", new Character('9'))));

		// Cultivo, variedad
		if (codCultivo != null) {
			if (FiltroUtils.noEstaVacio(codCultivo)) {
				// Cultivo
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal((codCultivo))))
						.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal("999"))));
			}

			if (!StringUtils.nullToString(codVariedad).equals("")) {
				// variedad
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal((codVariedad))))
						.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal("999"))));
			}
		}

		return criteria;
	}

	public String getCodCultivo() {
		return codCultivo;
	}

	public void setCodCultivo(String codCultivo) {
		this.codCultivo = codCultivo;
	}

	public String getCodVariedad() {
		return codVariedad;
	}

	public void setCodVariedad(String codVariedad) {
		this.codVariedad = codVariedad;
	}

	public String getCodTipoCapital() {
		return codTipoCapital;
	}

	public void setCodTipoCapital(String codTipoCapital) {
		this.codTipoCapital = codTipoCapital;
	}

	public String getCodprovincia() {
		return codprovincia;
	}

	public void setCodprovincia(String codprovincia) {
		this.codprovincia = codprovincia;
	}

	public String getCodcomarca() {
		return codcomarca;
	}

	public void setCodcomarca(String codcomarca) {
		this.codcomarca = codcomarca;
	}

	public String getCodtermino() {
		return codtermino;
	}

	public void setCodtermino(String codtermino) {
		this.codtermino = codtermino;
	}

	public String getSubtermino() {
		return subtermino;
	}

	public void setSubtermino(String subtermino) {
		this.subtermino = subtermino;
	}

	public String getFrecolhasta() {
		return frecolhasta;
	}

	public void setFrecolhasta(String frecolhasta) {
		this.frecolhasta = frecolhasta;
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}
}