package com.rsi.agp.dao.tables.commons;

import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public class PermisosInformes implements java.io.Serializable{
	
	private boolean accesoDisenador = true, accesoGenerador = true;
	
	public PermisosInformes () {
	}
	
	public PermisosInformes (EntidadAccesoRestringido e) {
		this.accesoDisenador = !(e != null && e.getAccesoDisenador() != null && e.getAccesoDisenador().equals(ConstantsInf.ACCESO_DENEGADO));
		this.accesoGenerador = !(e != null && e.getAccesoGenerador() != null && e.getAccesoGenerador().equals(ConstantsInf.ACCESO_DENEGADO));
	}

	public boolean isAccesoDisenador() {
		return accesoDisenador;
	}

	public void setAccesoDisenador(boolean accesoDisenador) {
		this.accesoDisenador = accesoDisenador;
	}

	public boolean isAccesoGenerador() {
		return accesoGenerador;
	}

	public void setAccesoGenerador(boolean accesoGenerador) {
		this.accesoGenerador = accesoGenerador;
	}

}
