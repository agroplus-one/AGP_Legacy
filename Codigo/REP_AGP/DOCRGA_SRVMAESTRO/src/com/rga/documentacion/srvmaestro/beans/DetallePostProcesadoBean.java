package com.rga.documentacion.srvmaestro.beans;

import java.util.List;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class DetallePostProcesadoBean {

	private int paginas;

	private String textoLateral;

	private boolean pintaEmitidoPiePagina;
	
	private boolean pintaLateralRegistroMercantil;

	private boolean pintaNumeracionPaginaPie;

	private List<FirmaXmpBaseBean> listaFirmas;

	private boolean esCopia;

	private boolean eliminarEnGed;
}
