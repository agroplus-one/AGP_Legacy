package com.rsi.agp.core.managers.impl.anexoMod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.anexo.AnexoModSWCambioMasivo;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.vo.ProduccionVO;

public interface IAnexoModCambioMasivoSWManager {

	public Set<Long> cambioMasivoSW(AnexoModSWCambioMasivo anexoModSWCambioMasivo, String idsRowsCheckedCM,
			HashMap<String, String> mensajesError, boolean guardarSoloPrecioYProd) throws BusinessException;

	public void recalculaPrecioProduccion(Collection<Parcela> parcelas, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd) throws Exception;
}