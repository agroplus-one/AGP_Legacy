package com.rsi.agp.core.report.anexoMod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PruebaJasperInformeParcelasAnexo {
	public static List<BeanParcelaAnexo> createBeanCollection() {
		List<BeanParcelaAnexo> lista = new ArrayList<BeanParcelaAnexo>();

		BeanParcelaAnexo inf = new BeanParcelaAnexo();
		inf.setNumero("1-1");
		inf.setCodProvincia(new BigDecimal("1"));
		inf.setNombre("Nombre de la 1-1");
		lista.add(inf);
		
		BeanParcelaAnexo inf2 = new BeanParcelaAnexo();
		inf2.setNumero("1-2");
		inf2.setCodProvincia(new BigDecimal("2"));
		inf2.setNombre("Nombre de la 1-2");
		lista.add(inf2);
		
		BeanParcelaAnexo inf3 = new BeanParcelaAnexo();
		inf3.setNumero("1-3");
		inf3.setCodProvincia(new BigDecimal("3"));
		inf3.setNombre("Nombre de la 1-3");
		lista.add(inf3);
		
		return lista;
	}
}
