package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;

public class ModelTableDecoratorDatosAsegurados extends TableDecorator {
	
	public String getDatosAseguradoSelec() {

		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();

		String cadenaDev = "<a href=\"javascript:modificar('" + co.getId() + "','" + co.getAsegurado().getId() + "','"
				+ co.getLineaCondicionado().getCodlinea() + "','" + co.getCcc() + "','" + co.getIban() + "','"
				+ co.getDestinatarioDomiciliacion() + "','"
				+ (StringUtils.isNullOrEmpty(co.getTitularCuenta()) ? "" : co.getTitularCuenta()) + "','"
				+ (StringUtils.isNullOrEmpty(co.getCcc2()) ? "" : co.getCcc2()) + "','"
				+ (StringUtils.isNullOrEmpty(co.getIban2()) ? "" : co.getIban2()) + "')\">";
		
		cadenaDev += "<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";
		
		if (co.getLineaCondicionado().getCodlinea().intValue() != 999) {
			cadenaDev += "<a href=\"javascript:baja('" + co.getId() + "','" + co.getLineaCondicionado().getCodlinea()
					+ "')\">";
			cadenaDev += "<img src=\"jsp/img/displaytag/delete.png\" alt=\"Baja\" title=\"Baja\"/></a>";
		} else {
			cadenaDev += "<img src=\"jsp/img/displaytag/transparente.gif\"/>";
		}

		return cadenaDev;
	}

	public String getDatosAseguradoLinea() {
		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();
		return co.getLineaCondicionado().getCodlinea().toString();
	}

	public String getDatosAseguradosccc() {
		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();
		return co.getIban() + " " + co.getCcc().substring(0, 4) + " " + co.getCcc().substring(4, 8) + " "
				+ co.getCcc().substring(8, 12) + " " + co.getCcc().substring(12, 16) + " "
				+ co.getCcc().substring(16, 20);
	}
	
	public String getDatosAseguradosccc2() {
		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();
		String iban2 = StringUtils.isNullOrEmpty(co.getIban2()) ? "" : co.getIban2();
		String ccc2 = StringUtils.isNullOrEmpty(co.getCcc2()) ? ""
				: co.getCcc2().substring(0, 4) + " " + co.getCcc2().substring(4, 8) + " "
						+ co.getCcc2().substring(8, 12) + " " + co.getCcc2().substring(12, 16) + " "
						+ co.getCcc2().substring(16, 20);
		return iban2 + " " + ccc2;
	}

	public String getFechaAlta() {

		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();
		String res = "";
		if (co.getFechaAlta() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaAlta = co.getFechaAlta();
			res = sdf.format(fechaAlta);
		}
		return res;
	}

	public String getFechaModificacion() {
		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();
		String res = "";
		if (co.getFechaModificacion() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaMod = co.getFechaModificacion();
			res = sdf.format(fechaMod);
		}
		return res;
	}

	public String getDestinatario() {
		DatoAsegurado co = (DatoAsegurado) getCurrentRowObject();
		String res = "";
		if (co.getDestinatarioDomiciliacion() != null) {
			if (co.getDestinatarioDomiciliacion().equals("A")) {
				res = "Asegurado";
			} else if (co.getDestinatarioDomiciliacion().equals("T")) {
				res = "Tomador";
			} else if (co.getDestinatarioDomiciliacion().equals("O")) {
				res = "Otros";
			}
		}
		return res;

	}
}
