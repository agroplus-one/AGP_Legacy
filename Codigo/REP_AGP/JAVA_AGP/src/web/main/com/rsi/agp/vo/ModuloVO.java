package com.rsi.agp.vo;

import java.util.ArrayList;
import java.util.List;

public class ModuloVO {

	private String codModulo;
	private String desModulo;
	private List<RiesgoVO> riesgos = null;

	public ModuloVO() {
		this.codModulo = new String("");
		this.desModulo = new String("");
		this.riesgos = new ArrayList<RiesgoVO>();
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public String getDesModulo() {
		return desModulo;
	}

	public void setDesModulo(String desModulo) {
		this.desModulo = desModulo;
	}

	public List<RiesgoVO> getRiesgos() {
		return riesgos;
	}

	public void setRiesgos(List<RiesgoVO> riesgos) {
		this.riesgos = riesgos;
	}
}
