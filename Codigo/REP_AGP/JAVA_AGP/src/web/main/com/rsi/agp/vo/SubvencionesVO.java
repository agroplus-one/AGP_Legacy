package com.rsi.agp.vo;

import java.util.ArrayList;

public class SubvencionesVO {

	private ArrayList<ItemSubvencionVO> subvencionesEnesa = new ArrayList<ItemSubvencionVO>();
	private ArrayList<ItemSubvencionVO> subvencionesCCAA = new ArrayList<ItemSubvencionVO>();

	public SubvencionesVO() {
	}

	public SubvencionesVO(ArrayList<ItemSubvencionVO> subvencionesEnesa, ArrayList<ItemSubvencionVO> subvencionesCCAA) {
		this.subvencionesCCAA = subvencionesCCAA;
		this.subvencionesEnesa = subvencionesEnesa;
	}

	public ArrayList<ItemSubvencionVO> getSubvencionesEnesa() {
		return subvencionesEnesa;
	}

	public void setSubvencionesEnesa(ArrayList<ItemSubvencionVO> subvencionesEnesa) {
		this.subvencionesEnesa = subvencionesEnesa;
	}

	public ArrayList<ItemSubvencionVO> getSubvencionesCCAA() {
		return subvencionesCCAA;
	}

	public void setSubvencionesCCAA(ArrayList<ItemSubvencionVO> subvencionesCCAA) {
		this.subvencionesCCAA = subvencionesCCAA;
	}

}
