package com.rsi.agp.dao.tables.poliza.dc2015;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpcionesFinanciacion {

	private final Log logger = LogFactory.getLog(OpcionesFinanciacion.class);

	private Integer opcionFraccionamiento = null;
	private Integer condicionFracionamiento = null;
	private BigDecimal valorOpcionFraccionamiento = null;

	public OpcionesFinanciacion(Map<String, Object> frmCalculoFinanciacion) {
		try {
			this.opcionFraccionamiento = new Integer(((String[]) frmCalculoFinanciacion.get("opcion_cf"))[0]);
			this.condicionFracionamiento = new Integer(
					((String[]) frmCalculoFinanciacion.get("condicionesFraccionamiento"))[0]);

			String valorOpcionFraccStr = null;

			switch (this.opcionFraccionamiento) {
			case 0:
				valorOpcionFraccStr = ((String[]) frmCalculoFinanciacion.get("porcentajeCosteTomador_txt"))[0];
				break;
			case 1:
				valorOpcionFraccStr = ((String[]) frmCalculoFinanciacion.get("importeFinanciar_txt"))[0];
				break;
			case 2:
				valorOpcionFraccStr = ((String[]) frmCalculoFinanciacion.get("importeAval_txt"))[0];
				break;
			default:
				break;
			}
			valorOpcionFraccStr = valorOpcionFraccStr.replace(".", "");
			valorOpcionFraccStr = valorOpcionFraccStr.replace(",", ".");
			this.valorOpcionFraccionamiento = new BigDecimal(valorOpcionFraccStr);

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public OpcionesFinanciacion() {

	}

	public Integer getOpcionFraccionamiento() {
		return opcionFraccionamiento;
	}

	public void setOpcionFraccionamiento(Integer opcionFraccionamiento) {
		this.opcionFraccionamiento = opcionFraccionamiento;
	}

	public Integer getCondicionFracionamiento() {
		return condicionFracionamiento;
	}

	public void setCondicionFracionamiento(Integer condicionFracionamiento) {
		this.condicionFracionamiento = condicionFracionamiento;
	}

	public BigDecimal getValorOpcionFraccionamiento() {
		return valorOpcionFraccionamiento;
	}

	public void setValorOpcionFraccionamiento(BigDecimal valorOpcionFraccionamiento) {
		this.valorOpcionFraccionamiento = valorOpcionFraccionamiento;
	}

}
