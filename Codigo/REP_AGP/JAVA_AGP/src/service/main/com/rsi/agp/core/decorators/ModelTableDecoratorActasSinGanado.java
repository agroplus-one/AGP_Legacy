package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoActas;

public class ModelTableDecoratorActasSinGanado extends TableDecorator {

	public String getColumnaAccActas() {

		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		String acciones = "";

		String serieActa = siniestroGanadoActas.getSerieActa().toString();
		String numeroActa = siniestroGanadoActas.getNumActa().toString();
		String letra = "";

		if (siniestroGanadoActas.getLetra() != null) {
			letra.equals(siniestroGanadoActas.getLetra());
		}

		acciones += "<a href=\"javascript:imprimirPdfActa('" + serieActa + "','" + numeroActa + "','" + letra;
		acciones += "')\"><img src=\"jsp/img/jmesa/pdf.gif\" alt=\"Pdf - Acta de tasación\" title=\"Pdf - Acta de tasación\"/></a>";

		acciones += "<a href=\"javascript:imprimirCartaPago('" + serieActa + "','" + numeroActa + "','" + letra;
		acciones += "')\"><img src=\"jsp/img/displaytag/reduccionCapital.png\" alt=\"Carta de Pago\" title=\"Carta de Pago\"/></a>";

		return acciones;
	}

	public String getColumnaProv() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getProvincia() != null) {
			return siniestroGanadoActas.getProvincia();
		} else {
			return vacio;
		}
	}

	public String getColumnaTerm() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getTermino() != null) {
			return siniestroGanadoActas.getTermino();
		} else {
			return vacio;
		}
	}

	public String getColumnaSerie() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getSerieActa() != null) {
			return siniestroGanadoActas.getSerieActa().toString();
		} else {
			return vacio;
		}
	}

	public String getColumnaNum() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getNumActa() != null) {
			return siniestroGanadoActas.getNumActa().toString();
		} else {
			return vacio;
		}
	}

	public String getColumnaLetra() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getLetra() != null) {
			return siniestroGanadoActas.getLetra();
		} else {
			return vacio;
		}
	}

	public String getColumnaEstado() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getEstado() != null) {
			return siniestroGanadoActas.getEstado();
		} else {
			return vacio;
		}
	}

	public String getColumnaLibro() {

		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getLibro() != null) {
			return siniestroGanadoActas.getLibro();
		} else {
			return vacio;
		}
	}

	public String getColumnaIdAnim() {
		String vacio = "";
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		if (siniestroGanadoActas.getIdAnimal() != null) {
			return siniestroGanadoActas.getIdAnimal();
		} else {
			return vacio;
		}
	}

	public Date getColumnaFActa() {
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		Date fecha = null;
		if (siniestroGanadoActas.getFechaActa() != null) {
			fecha = siniestroGanadoActas.getFechaActa();
		}

		return fecha;
	}

	public Date getColumnaFPago() {
		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();

		Date fecha = null;
		if (siniestroGanadoActas.getFechaPago() != null) {
			fecha = siniestroGanadoActas.getFechaPago();
		}
		return fecha;
	}

	public String getColumnaImpActa() {
		String vacio = "";

		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();
		if (siniestroGanadoActas.getImporteActa() != null) {
			return siniestroGanadoActas.getImporteActa().toString();
		} else {
			return vacio;
		}
	}

	public String getColumnaImpDev() {
		String vacio = "";

		SiniestroGanadoActas siniestroGanadoActas = (SiniestroGanadoActas) getCurrentRowObject();
		if (siniestroGanadoActas.getImporteDevolver() != null) {
			return siniestroGanadoActas.getImporteDevolver().toString();
		} else {
			return vacio;
		}
	}

}
