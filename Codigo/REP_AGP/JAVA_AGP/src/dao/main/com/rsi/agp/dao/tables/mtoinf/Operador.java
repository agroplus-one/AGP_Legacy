package com.rsi.agp.dao.tables.mtoinf;

public class Operador {
		
		private int idOperador;
		private int value;
		private String property;
		
		
		
		
		public int getIdOperador() {
			return idOperador;
		}
		public void setIdOperador(int idOperador) {
			this.idOperador = idOperador;
		}
		public String getProperty() {
			return property;
		}
		public void setProperty(String property) {
			this.property = property;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		public Operador(int idOperador,int value,String property){
			this.idOperador = idOperador;
			this.property = property;
			this.value = value;
			
		}
		

		public Operador(int value,String property){
			this.property = property;
			this.value = value;
			
		}
		
}