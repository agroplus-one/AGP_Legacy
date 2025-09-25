package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantia;

public class FechaFinGarantiasFiltro implements Filter {

	private String codCultivo;
	private String codVariedad;
	private String codTipoCapital;
	private String codprovincia;
	private String codcomarca;
	private String codtermino;
	private String subtermino;
	private String fgaranthasta;
	private String codconceptoppalmod;
	private String codriesgocubierto;
	private String codModulo;
	private Long lineaseguroid;

	public FechaFinGarantiasFiltro() {
		super();
	}

	public FechaFinGarantiasFiltro(String codCultivo, String codVariedad, String codTipoCapital, String codprovincia,
			String codcomarca, String codtermino, String subtermino, String fgaranthasta, String codconceptoppalmod,
			String codriesgocubierto, String codModulo, Long lineaseguroid) {

		this.codCultivo = codCultivo;
		this.codVariedad = codVariedad;
		this.codTipoCapital = codTipoCapital;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.fgaranthasta = fgaranthasta;
		this.codconceptoppalmod = codconceptoppalmod;
		this.codriesgocubierto = codriesgocubierto;
		this.codModulo = codModulo;
		this.lineaseguroid = lineaseguroid;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(FechaFinGarantia.class);

		// Modulo
		if (!StringUtils.nullToString(codModulo).equals("")) {
			criteria.add(Restrictions.eq("modulo.id.codmodulo", codModulo));
		}

		// Lineaseguroid
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		}

		// Fecha fin de garantias
		if (FiltroUtils.noEstaVacio(fgaranthasta)) {
			criteria.add(Restrictions.eq("fgaranthasta", FiltroUtils.getDDMMYYYYDate(fgaranthasta)));
		}

		// Tipo capital
		if (!StringUtils.nullToString(codTipoCapital).equals("")) {
			criteria.add(Restrictions.eq("tipoCapital.codtipocapital", new BigDecimal(codTipoCapital)));
		}

		// ----------------------- codconceptoppalmod = 4 and codriesgocubierto = 3
		// ------------------------
		// Concepto principal modulo
		if (!StringUtils.nullToString(codconceptoppalmod).equals("")) {
			criteria.add(Restrictions.eq("codconceptoppalmod", new BigDecimal(codconceptoppalmod)));
		}

		// Riesgo cubierto
		if (!StringUtils.nullToString(codriesgocubierto).equals("")) {
			criteria.add(Restrictions.eq("codriesgocubierto", new BigDecimal(codriesgocubierto)));
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

	public String getFgaranthasta() {
		return fgaranthasta;
	}

	public void setFgaranthasta(String fgaranthasta) {
		this.fgaranthasta = fgaranthasta;
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