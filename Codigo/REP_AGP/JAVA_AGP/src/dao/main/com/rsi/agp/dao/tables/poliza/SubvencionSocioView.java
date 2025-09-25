package com.rsi.agp.dao.tables.poliza;

import java.io.Serializable;
import java.math.BigDecimal;

import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesaId;

@SuppressWarnings("serial")
public class SubvencionSocioView extends SubvencionSocio implements
		Serializable {

	private String codigo;

	public SubvencionSocioView() {
		super();
	}

	public SubvencionSocioView(final String codigo) {
		this.codigo = codigo;
		if (null == getSubvencionEnesa()) {
			setSubvencionEnesa(new SubvencionEnesa());
		}
		getSubvencionEnesa().setId(construyeId(codigo));
	}

	public SubvencionSocioView(final Long id, final Poliza poliza, final SubvencionEnesa subvencionEnesa, final Socio socio) {
		super(id, poliza, subvencionEnesa, socio);
		final SubvencionEnesaId enesaId = subvencionEnesa.getId();
		this.codigo = new String()
				.concat(enesaId.getId().toString()).concat(";")
				.concat(enesaId.getLineaseguroid().toString()).concat(";");
	}

	public SubvencionSocioView(final String codigo, final Long id, final Poliza poliza, final SubvencionEnesa subvencionEnesa,
			final Socio socio) {
		super(id, poliza, subvencionEnesa, socio);
		this.codigo = codigo;
		if (null == getSubvencionEnesa()) {
			setSubvencionEnesa(new SubvencionEnesa());
		}
		getSubvencionEnesa().setId(construyeId(codigo));
	}

	// Este método, junto con el constructor arriba indicado, establecen el orden de los campos del código
	public final SubvencionEnesaId construyeId(final String codigo) {
		final SubvencionEnesaId enesaId = new SubvencionEnesaId();
		final String[] listaCodigos = codigo.split(";");
		if (null != listaCodigos && listaCodigos.length == 2) {
			enesaId.setId(new BigDecimal(listaCodigos[0]));
			enesaId.setLineaseguroid(new Long(listaCodigos[1]));
		}
		return enesaId;
	}

	public final SubvencionSocio getSubvencionSocio() {
		return new SubvencionSocio(getId(), getPoliza(), getSubvencionEnesa(), getSocio());
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
