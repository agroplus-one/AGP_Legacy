package com.rga.documentacion.srvmaestro.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@SuppressWarnings("unused")
public class ClausulaAplicable { 
	
	private String clausula;

	private String valor;
}
