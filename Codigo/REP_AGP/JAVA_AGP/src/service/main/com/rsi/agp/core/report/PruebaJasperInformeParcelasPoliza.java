package com.rsi.agp.core.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PruebaJasperInformeParcelasPoliza {
	public static List<BeanParcela> createBeanCollection() {
		List<BeanParcela> lista = new ArrayList<BeanParcela>();

		BeanParcela inf = new BeanParcela();
		inf.setNumero("1-1");
		inf.setCodProvincia(new BigDecimal("1"));
		inf.setNombre("Nombre de la 1-1");
		inf.setSuperm("12\n51\n");
		lista.add(inf);
		
		BeanParcela inf2 = new BeanParcela();
		inf2.setNumero("1-2");
		inf2.setCodProvincia(new BigDecimal("2"));
		inf2.setNombre("Nombre de la 1-2");
		lista.add(inf2);
		
		BeanParcela inf3 = new BeanParcela();
		inf3.setNumero("1-3");
		inf3.setCodProvincia(new BigDecimal("3"));
		inf3.setNombre("Nombre de la 1-3");
		lista.add(inf3);
		
		return lista;
	}
}
