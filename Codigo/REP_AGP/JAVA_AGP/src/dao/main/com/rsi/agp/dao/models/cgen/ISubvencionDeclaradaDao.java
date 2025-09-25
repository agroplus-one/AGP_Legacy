package com.rsi.agp.dao.models.cgen;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.SubvencionDeclarada;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;


public interface ISubvencionDeclaradaDao {
	public Map<BigDecimal, SubvencionDeclarada> getSubvencionesDeclaradas(
			es.agroseguro.seguroAgrario.contratacion.SubvencionDeclarada[] subvenciones);
	
	public Map<BigDecimal, SubvencionDeclarada> getSubvencionesDeclaradasGanado(
			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvenciones);

	public TipoSubvencionEnesa getSubvencionENESA(BigDecimal codtiposubv);
	public Organismo getSubvencionCCAA(Character character);
}
