package com.rsi.agp.dao.models.cesp;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.filters.cesp.ConceptoPpalFiltro;
import com.rsi.agp.dao.filters.cesp.CultivoFiltro;
import com.rsi.agp.dao.filters.cesp.EstadoFenologicoFiltro;
import com.rsi.agp.dao.filters.cesp.RiesgoCubiertoFiltro;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.EstadoFenologico;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.poliza.Linea;

public class CondicionesEspecialesDao extends BaseDaoHibernate implements ICondicionesEspecialesDao {

	@SuppressWarnings("unchecked")
	@Override
	public final List<ConceptoPpalModulo> getAllConceptoPpal() {
		final ConceptoPpalFiltro filtro = new ConceptoPpalFiltro();
		return this.getObjects(filtro);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<EstadoFenologico> getAllEstadoFenologico() {
		final EstadoFenologicoFiltro filtro = new EstadoFenologicoFiltro();
		return this.getObjects(filtro);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List getCultivosByCodLinea(final String codLinea) {
		
		return this.getObjectsBySQLQuery("select distinct codcultivo,descultivo from tb_sc_c_cultivos t join tb_lineas l on t.lineaseguroid=l.lineaseguroid where l.codlinea="+codLinea);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<RiesgoCubierto> getRiesgosCubiertos(final Long idLinea, final String codModulo){
		final RiesgoCubiertoFiltro filtro = new RiesgoCubiertoFiltro(idLinea, codModulo);
		return this.getObjects(filtro);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Linea> getLineaByPlan(final BigDecimal idPlan) {
		final LineasFiltro filtro = new LineasFiltro();
		filtro.setCodPlan(idPlan);
		return this.getObjects(filtro);
	}
	
	public List getEstadosFenologicosByCodCultivo(BigDecimal codcultivo){
		return this.getObjectsBySQLQuery("select distinct codestadofenologico,desestadofenologico from tb_sc_c_estados_fenologicos t where codcultivo="+codcultivo);
	}

}
