package com.rsi.agp.dao.models.config;

import java.util.List;

import com.rsi.agp.dao.filters.org.PantallaConfigurableFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class PantallaConfigurableDao extends BaseDaoHibernate implements IPantallaConfigurableDao {

	/**
	 * Metodo para obtener la pantalla configuracion de la seccion de datos
	 * variables de poliza para montar la pantalla de parcelas
	 * @param polizaBean Poliza a gestionar.
	 * @return PantallaConfigurable: la pantalla configurable.
	 */
	@SuppressWarnings("unchecked")
	public PantallaConfigurable getPantallaVarPoliza(Long idpantallaconfigurable, Long idpantalla, Long lineaseguroid) {
		PantallaConfigurableFiltro filtro = new PantallaConfigurableFiltro(idpantallaconfigurable, idpantalla, lineaseguroid);

		List<PantallaConfigurable> pantallas = this.getObjects(filtro);
		if (pantallas.size() > 0)
			return (PantallaConfigurable) pantallas.get(0);
		else
			return null;
	}

}
