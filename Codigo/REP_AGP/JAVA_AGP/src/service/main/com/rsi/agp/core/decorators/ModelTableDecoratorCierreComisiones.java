package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;

public class ModelTableDecoratorCierreComisiones extends TableDecorator {

	public String getFase() {

		Fichero fich = (Fichero) getCurrentRowObject();
		return StringUtils.nullToString(fich.getFase().getFase());

	}

	public Date getFecEmision() {

		Fichero fich = (Fichero) getCurrentRowObject();
		return fich.getFase().getFechaemision();

	}

	public String getFichero() {

		Fichero fich = (Fichero) getCurrentRowObject();
		return StringUtils.nullToString(fich.getNombrefichero());
	}

	public String getTipoFichero() {

		String formatTipo = "";
		if (getCurrentRowObject() instanceof Fichero) {
			Fichero fich = (Fichero) getCurrentRowObject();

			switch (fich.getTipofichero()) {
			case 'C':
				formatTipo = "Comisiones";
				break;
			case 'R':
				formatTipo = "Reglamentos";
				break;
			case 'G':
				formatTipo = "Recibos Emitidos";
				break;
			case 'I':
				formatTipo = "Impagados";
				break;
			case 'D':
				formatTipo = "Deuda Aplazada";
				break;
			case '1':
				formatTipo = "Comisiones 2015+";
				break;
			case '2':
				formatTipo = "Impagados 2015+";
				break;
			case 'U':
				formatTipo = "Gastos Unificados";
				break;
			default:
				break;
			}
		} else if (getCurrentRowObject() instanceof FicheroUnificado) {
			FicheroUnificado fich = (FicheroUnificado) getCurrentRowObject();
			switch (fich.getTipoFichero()) {
			case 'C':
				formatTipo = "Emitidos 2015+";
				break;
			case 'I':
				formatTipo = "Impagados 2015+";
				break;
			case 'D':
				formatTipo = "Deuda Aplazada";
				break;
			case 'U':
				formatTipo = "Gastos Unificados";
				break;
			default:
				break;
			}
		}

		return formatTipo;
	}

}
