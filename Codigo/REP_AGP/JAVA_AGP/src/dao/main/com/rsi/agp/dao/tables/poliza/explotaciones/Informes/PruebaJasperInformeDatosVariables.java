package com.rsi.agp.dao.tables.poliza.explotaciones.Informes;

import java.util.ArrayList;
import java.util.List;

public class PruebaJasperInformeDatosVariables {
	public static List<InformeDatosVariables> getDatosVariables() {
		List<InformeDatosVariables> lista = new ArrayList<InformeDatosVariables>();
		//solo para pruebas - FALTA IMPLEMENTAR
		InformeDatosVariables inf = new InformeDatosVariables();
		inf.setNombreConcepto("Nombre concepto 1:");
		inf.setValor("Valor 1");
		inf.setDescripcion("Descripcion 1");
		lista.add(inf);
		
		InformeDatosVariables inf2 = new InformeDatosVariables();
		inf2.setNombreConcepto("Nombre concepto 2:");
		inf2.setValor("Valor 2");
		inf2.setDescripcion("Descripcion 2");
		lista.add(inf2);
		
		InformeDatosVariables inf3 = new InformeDatosVariables();
		inf3.setNombreConcepto("Nombre concepto 3:");
		inf3.setValor("Valor 3");
		inf3.setDescripcion("Descripcion 3");
		lista.add(inf3);
		
		return lista;
	}
}
