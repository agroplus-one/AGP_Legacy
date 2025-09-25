package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

public interface IContratacionExtConfirmacion {

	public ConfirmarResponse confirmar(ConfirmarRequest parameters);

	public SolicitudCuponResponse solicitarCupon(SolicitudCuponRequest parameters);
	
	public AnularCuponResponse anularCupon(AnularCuponRequest parameters);
	
	public ConfirmarCuponResponse confirmarCupon(ConfirmarCuponRequest parameters);
	
	public ConfirmarSiniestroResponse confirmarSiniestro(ConfirmarSiniestroRequest parameters);
	
	public CalcularResponse calcular(CalcularRequest parameters);
	
	public CalcularAnexoResponse calcularAnexo(CalcularAnexoRequest parameters);
	
}
