package com.rga.documentacion.srvmaestro.beans;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class ProcesadoPaginaBean {

	private String textoLateralCopias;

	private boolean pintaLateraRegistroMercantil;

	private boolean pintaEmisionPiePagina;

	private boolean pintaNumeracionPaginaPie;

	private boolean ultimaPaginaSubDocumentoImpar;

	private boolean esCopia;

	private int paginasTotalSubDocumento;

	private int paginaActual;
}
