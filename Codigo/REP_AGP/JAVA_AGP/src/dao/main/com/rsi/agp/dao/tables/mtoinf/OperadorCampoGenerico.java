package com.rsi.agp.dao.tables.mtoinf;

import java.math.BigDecimal;

public class OperadorCampoGenerico {

	private Long idOpGen;
	private BigDecimal isOpCalcOPerm;
	private BigDecimal idOperadorCalcOPerm;
	private BigDecimal idVista;
	private String nombreVista;
	private BigDecimal idCampo;
	private String nombreCampo;
	private BigDecimal idoperador;
	private String nombreOperador;

	public OperadorCampoGenerico() {
		super();
	}

	public String getNombreOperador() {
		return nombreOperador;
	}

	public void setNombreOperador(String nombreOperador) {
		this.nombreOperador = nombreOperador;
	}

	public Long getIdOpGen() {
		return idOpGen;
	}

	public void setIdOpGen(Long idOpGen) {
		this.idOpGen = idOpGen;
	}

	public BigDecimal getIsOpCalcOPerm() {
		return isOpCalcOPerm;
	}

	public void setIsOpCalcOPerm(BigDecimal isOpCalcOPerm) {
		this.isOpCalcOPerm = isOpCalcOPerm;
	}

	public BigDecimal getIdOperadorCalcOPerm() {
		return idOperadorCalcOPerm;
	}

	public void setIdOperadorCalcOPerm(BigDecimal idOperadorCalcOPerm) {
		this.idOperadorCalcOPerm = idOperadorCalcOPerm;
	}

	public BigDecimal getIdVista() {
		return idVista;
	}

	public void setIdVista(BigDecimal idVista) {
		this.idVista = idVista;
	}

	public String getNombreVista() {
		return nombreVista;
	}

	public void setNombreVista(String nombreVista) {
		this.nombreVista = nombreVista;
	}

	public BigDecimal getIdVistaCampo() {
		return idCampo;
	}

	public void setIdVistaCampo(BigDecimal idVistaCampo) {
		this.idCampo = idVistaCampo;
	}

	public String getNombreVistaCampo() {
		return nombreCampo;
	}

	public void setNombreVistaCampo(String nombreVistaCampo) {
		this.nombreCampo = nombreVistaCampo;
	}

	public BigDecimal getIdoperador() {
		return idoperador;
	}

	public void setIdoperador(BigDecimal idoperador) {
		this.idoperador = idoperador;
	}
}