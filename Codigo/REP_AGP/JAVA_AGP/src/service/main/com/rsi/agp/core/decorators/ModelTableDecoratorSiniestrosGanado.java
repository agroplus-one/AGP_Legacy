package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.siniestro.SiniestroGanado;

public class ModelTableDecoratorSiniestrosGanado extends TableDecorator {

	public String getcolumnaGrNegocio() {
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		return siniestroGanado.getGrupoNegocio();
	}

	public String getColumnaProv() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getProvincia() != null) {
			return siniestroGanado.getProvincia();
		} else {
			return vacio;
		}
	}

	public String getColumnaTerm() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getTermino() != null) {
			return siniestroGanado.getTermino();
		} else {
			return vacio;
		}
	}

	public String getColumnaSerie() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getSerieSiniestro() != null) {
			return siniestroGanado.getSerieSiniestro().toString();
		} else {
			return vacio;
		}
	}

	public String getColumnaNum() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getNumeroSiniestro() != null) {
			return siniestroGanado.getNumeroSiniestro().toString();
		} else {
			return vacio;
		}
	}

	public Date getColumnaFCom() {
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		Date fecha = null;
		if (siniestroGanado.getFechaComunicacion() != null) {
			fecha = siniestroGanado.getFechaComunicacion();
		}

		return fecha;
	}

	public String getColumnaLibro() {

		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getLibro() != null) {
			return siniestroGanado.getLibro();
		} else {
			return vacio;
		}
	}

	public String getColumnaIdAnim() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getIdAnimal() != null) {
			return siniestroGanado.getIdAnimal();
		} else {
			return vacio;
		}
	}

	public String getColumnaPer() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getPerito() != null) {
			return siniestroGanado.getPerito();
		} else {
			return vacio;
		}
	}

	public String getColumnaTlfPer() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();

		if (siniestroGanado.getTlfPerito() != null) {
			return siniestroGanado.getTlfPerito();
		} else {
			return vacio;
		}
	}

	public String getColumnaTas() {
		String TasadoSi = "Sí";
		String TasadoNo = "No";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();
		if (siniestroGanado.getTasado()) {
			return TasadoSi;
		} else {
			return TasadoNo;
		}
	}

	public Date getColumnaFRet() {

		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();
		if (siniestroGanado.getFechaRetirada() != null) {
			return siniestroGanado.getFechaRetirada();
		} else {
			return null;
		}
	}

	public String getColumnaKg() {
		String vacio = "";
		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();
		if (siniestroGanado.getKilos() != null) {
			return siniestroGanado.getKilos().toString();
		} else {
			return vacio;
		}
	}

	public String getColumnaCosRet() {
		String vacio = "";

		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();
		if (siniestroGanado.getCosteRetirada() != null) {
			return siniestroGanado.getCosteRetirada().toString();
		} else {
			return vacio;
		}
	}

	public String getColumnaPagGest() {
		String vacio = "";

		SiniestroGanado siniestroGanado = (SiniestroGanado) getCurrentRowObject();
		if (siniestroGanado.getPagoGestora() != null) {
			return siniestroGanado.getPagoGestora().toString();
		} else {
			return vacio;
		}
	}
}
