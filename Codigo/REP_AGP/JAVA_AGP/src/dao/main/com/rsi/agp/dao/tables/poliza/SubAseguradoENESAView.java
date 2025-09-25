package com.rsi.agp.dao.tables.poliza;

import java.io.Serializable;
import java.math.BigDecimal;

import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesaId;

@SuppressWarnings("serial")
public class SubAseguradoENESAView extends SubAseguradoENESA implements
		Serializable {

	private String codigo;

	public SubAseguradoENESAView() {
		super();
	}

	public SubAseguradoENESAView(final String codigo) {
		this.codigo = codigo;
		if (null == getSubvencionEnesa()) {
			setSubvencionEnesa(new SubvencionEnesa());
		}
		getSubvencionEnesa().setId(construyeId(codigo));
	}

	// Este método, junto con el construyeId, establecen el orden de los campos del código
	public SubAseguradoENESAView(final Long id, final Asegurado asegurado, final Poliza poliza, final SubvencionEnesa subvencionEnesa) {
		super(id, asegurado, poliza, subvencionEnesa);
		final SubvencionEnesaId enesaId = subvencionEnesa.getId();
		this.codigo = new String()
				.concat(enesaId.getId().toString()).concat(";")
				.concat(enesaId.getLineaseguroid().toString()).concat(";");
	}

	public SubAseguradoENESAView(final String codigo, final Long id, final Asegurado asegurado, final Poliza poliza,
			final SubvencionEnesa subvencionEnesa) {
		super(id, asegurado, poliza, subvencionEnesa);
		this.codigo = codigo;
		if (null == subvencionEnesa) {
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

	public final SubAseguradoENESA getSubAseguradoENESA() {
		return new SubAseguradoENESA(getId(), getAsegurado(), getPoliza(), getSubvencionEnesa());
	}

	public final String getCodigo() {
		return codigo;
	}

	public final void setCodigo(final String codigo) {
		this.codigo = codigo;
		getSubvencionEnesa().setId(construyeId(codigo));
	}

}
