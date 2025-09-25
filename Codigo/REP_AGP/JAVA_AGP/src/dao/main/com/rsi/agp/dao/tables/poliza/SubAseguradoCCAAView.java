package com.rsi.agp.dao.tables.poliza;

import java.io.Serializable;
import java.math.BigDecimal;

import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAAId;

@SuppressWarnings("serial")
public class SubAseguradoCCAAView extends SubAseguradoCCAA implements
		Serializable {

	private String codigo;

	public SubAseguradoCCAAView() {
		super();
	}

	public SubAseguradoCCAAView(final String codigo) {
		this.codigo = codigo;
		if (null == getSubvencionCCAA()) {
			setSubvencionCCAA(new SubvencionCCAA());
		}
		getSubvencionCCAA().setId(construyeId(codigo));
	}

	// Este método, junto con el construyeId, establecen el orden de los campos del código
	public SubAseguradoCCAAView(final Long id, final SubvencionCCAA subvencionCCAA, final Asegurado asegurado, final Poliza poliza) {
		super(id, subvencionCCAA, asegurado, poliza);
		final SubvencionCCAAId subvencionCCAAId = subvencionCCAA.getId();
		this.codigo = new String()
				.concat(subvencionCCAAId.getId().toString()).concat(";")
				.concat(subvencionCCAAId.getLineaseguroid().toString()).concat(";");
	}

	public SubAseguradoCCAAView(final String codigo, final Long id, final SubvencionCCAA subvencionCCAA, final Asegurado asegurado,
			final Poliza poliza){
		super(id, subvencionCCAA, asegurado, poliza);
		this.codigo = codigo;
		if (null == getSubvencionCCAA()) {
			setSubvencionCCAA(new SubvencionCCAA());
		}
		getSubvencionCCAA().setId(construyeId(codigo));
	}

	// Este método, junto con el constructor arriba indicado, establecen el orden de los campos del código
	public final SubvencionCCAAId construyeId(final String codigo) {
		final SubvencionCCAAId subvencionCCAAId = new SubvencionCCAAId();
		final String[] listaCodigos = codigo.split(";");
		if (null != listaCodigos && listaCodigos.length == 2) {
			subvencionCCAAId.setId(new BigDecimal(listaCodigos[0]));
			subvencionCCAAId.setLineaseguroid(new Long(listaCodigos[1]));
		}
		return subvencionCCAAId;
	}

	public final String getCodigo() {
		return codigo;
	}

	public final void setCodigo(final String codigo) {
		this.codigo = codigo;
	}

}
