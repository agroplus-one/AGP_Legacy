package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;

@SuppressWarnings("rawtypes")
public interface IParcelaDao extends GenericDao {

	public List<Parcela> listParcelas(Parcela parcela) throws DAOException;

	public List<CapAsegSiniestro> listCapitales(CapAsegSiniestro capitalSiniestro) throws DAOException;

	public Parcela getParcelaPoliza(Long idParcela);

	public com.rsi.agp.dao.tables.copy.Parcela getParcelaCopy(Long idParcelaCopy);
}
