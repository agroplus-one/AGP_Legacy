package com.rsi.agp.dao.tables.log;

import java.util.ArrayList;

public class ActivacionLineasView 
{
	private ArrayList<Long> idsHistorico;
	private HistImportaciones registroHistorico;
		
	public ArrayList<Long> getIdsHistorico() {
		return idsHistorico;
	}
	public void setIdsHistorico(ArrayList<Long> idsHistorico) {
		this.idsHistorico = idsHistorico;
	}
	public HistImportaciones getRegistroHistorico() {
		return registroHistorico;
	}
	public void setRegistroHistorico(HistImportaciones registroHistorico) {
		this.registroHistorico = registroHistorico;
	}
}
