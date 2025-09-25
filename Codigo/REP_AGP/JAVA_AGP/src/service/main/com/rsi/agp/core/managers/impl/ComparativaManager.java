package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.dao.models.poliza.IComparativaDao;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ComparativaManager {

	
	private IComparativaDao comparativaDao;
	
	public ComparativaPoliza  guardarComparatCaracExplot(ComparativaPoliza cp, Poliza poliza , 
			BigDecimal caractExlp)
	{
		
		return comparativaDao.guardarComparatCaracExplot(cp, poliza , caractExlp);
		
	}

	public void setComparativaDao(IComparativaDao comparativaDao) {
		this.comparativaDao = comparativaDao;
	}
	
	public List<ComparativaPoliza> getComparativas(Long idpoliza){
		List<ComparativaPoliza> comparativas = new ArrayList<ComparativaPoliza>();
		comparativas = this.comparativaDao.getObjects(ComparativaPoliza.class, "id.idpoliza", idpoliza);
		return comparativas;
	}
}
