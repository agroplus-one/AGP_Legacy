package com.rsi.agp.dao.models.config;

import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public interface IPantallaConfigurableDao {
	public PantallaConfigurable getPantallaVarPoliza(Long idpantallaconfigurable, Long idpantalla, Long lineaseguroid);
}
