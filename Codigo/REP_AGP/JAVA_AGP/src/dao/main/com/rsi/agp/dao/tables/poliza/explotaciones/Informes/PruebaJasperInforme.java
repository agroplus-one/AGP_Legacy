package com.rsi.agp.dao.tables.poliza.explotaciones.Informes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;

public class PruebaJasperInforme {
	public static List<InformeAnexModExplotacion> getModificacionAnexosExplotacion() {
		List<InformeAnexModExplotacion> lista = new ArrayList<InformeAnexModExplotacion>();
		//solo para pruebas - FALTA IMPLEMENTAR
		InformeAnexModExplotacion inf = new InformeAnexModExplotacion();
		//inf.setAutorizacionEspecial("??");
		//inf.setComarca("1 - MANCHA");
		//inf.setCondicionParticular("??");
		inf.setCosteTomador(new BigDecimal(1525.36));
		inf.setEspecie("1 - ??");
		inf.setGrupoListado("1152");
		inf.setGrupoRaza("1 - LACTEAS");
		inf.setId(new BigDecimal(49));
		inf.setIdpoliza(new BigDecimal(39054));
		//inf.setLatitud(new BigDecimal(1));
		//inf.setLongitud(new BigDecimal(1));
		inf.setNumanimales(new BigDecimal(1024));
		//inf.setNumero(numero);
		inf.setPrecio(new BigDecimal(12698.56));
		inf.setProvincia("47 - Valladolid");
		inf.setRega("2");
		inf.setRegimen("1??");
		inf.setSigla("2");
		inf.setSubexplotacion("1");
		inf.setTasaComercial(new BigDecimal(0));
		inf.setTermino("19 - BONILLO");
		inf.setTipoAnimal("2 - CRIA");
		inf.setTipoCapital("15-RETIRADA Y DESTRUCCION");
		inf.setTipoModificacion("M");
		
		//inf.setAutorizacionEspecialM("??");
		//inf.setComarcaM("1 - MANCHA");
		//inf.setCondicionParticularM("??");
		//inf.setCosteTomadorM(new BigDecimal(0));
		//inf.setEspecieM("1 - ??");
		//inf.setGrupoListadoM("1152");
		//inf.setGrupoRazaM("1 - LACTEAS");
		inf.setIdM(new BigDecimal(100));
		//inf.setIdpolizaM(new BigDecimal(39054));
		//inf.setLatitudM(new BigDecimal(1));
		//inf.setLongitudM(new BigDecimal(1));
		//inf.setNumAnimalesM(new BigDecimal(45));
		inf.setNumeroM(new BigDecimal(11111));
		//inf.setPrecioM(new BigDecimal(0));
		//inf.setProvinciaM("47 - Valladolid");
		inf.setRegaM("5");
		inf.setRegimenM("2??");
		inf.setSiglaM("3");
		//inf.setSubexplotacionM(new BigDecimal(2));
		//inf.setTasaComercialM(new BigDecimal(0));
		//inf.setTerminoM("19 - BONILLO");
		inf.setTipoAnimalM("3 - yo que se");
		inf.setTipoCapitalM("17- pppppp");
		inf.setTipoModificacionM("M");
	
		       
		
		InformeDatosVariables inf1 = new InformeDatosVariables();
		inf1.setNombreConcepto("Nombre Concepto 1");inf1.setDescripcion("Valor 1"); inf1.setDescripcion("descripcion1");
		InformeDatosVariables inf2 = new InformeDatosVariables();
		inf2.setNombreConcepto("Nombre Concepto 2");inf2.setDescripcion("Valor 2"); inf2.setDescripcion("descripcion2");
		InformeDatosVariables inf3 = new InformeDatosVariables();
		inf3.setNombreConcepto("Nombre Concepto 3");inf3.setDescripcion("Valor 3"); inf3.setDescripcion("descripcion3");
		inf.getDatosVariables().add(inf1);inf.getDatosVariables().add(inf2);inf.getDatosVariables().add(inf3);
		
		
				
		lista.add(inf);
		
		
		
		
		return lista;
	}
}
