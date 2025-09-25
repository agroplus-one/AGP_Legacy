package com.rsi.agp.dao.models.cesp;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.EstadoFenologico;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("rawtypes")
public interface ICondicionesEspecialesDao extends GenericDao {
	List<Linea> getLineaByPlan(final BigDecimal idPlan);
	List<ConceptoPpalModulo> getAllConceptoPpal();
	List<EstadoFenologico> getAllEstadoFenologico();
	List getCultivosByCodLinea(final String codLinea);
	List<RiesgoCubierto> getRiesgosCubiertos(final Long idLinea, final String codModulo);
	List getEstadosFenologicosByCodCultivo(BigDecimal codcultivo);
}
