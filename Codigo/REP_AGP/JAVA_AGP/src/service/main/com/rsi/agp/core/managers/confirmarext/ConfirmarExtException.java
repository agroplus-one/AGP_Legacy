package com.rsi.agp.core.managers.confirmarext;

public class ConfirmarExtException  {

	 public static class AgrWSException extends Exception {

		private static final long serialVersionUID = -75101492874926183L;

		public AgrWSException(final String exMsg) {
			 super("Error en la llamada al SW de confirmación de Agroseguro: " + exMsg);
		 }
	 }
}
