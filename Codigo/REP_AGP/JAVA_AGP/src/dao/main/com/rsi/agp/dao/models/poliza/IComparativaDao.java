package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("rawtypes")
public interface IComparativaDao extends GenericDao {
	public ComparativaPoliza guardarComparatCaracExplot(ComparativaPoliza cp, Poliza poliza, BigDecimal caractExlp);
}
