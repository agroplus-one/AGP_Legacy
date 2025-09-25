package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import com.rsi.agp.main.contratacionext.ContratacionExtBO;

public abstract class ContratacionExtConfirmacion implements  IContratacionExtConfirmacion {

	private ContratacionExtBO bo;

	public void setBo(final ContratacionExtBO bo) {
		this.bo = bo;
	}
	
	public ContratacionExtBO getBo() {
		return this.bo;
	}
}
