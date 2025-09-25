package com.rsi.agp.core.managers.impl.ganado;

import java.util.ArrayList;


public class InformacionRega {
	
	
	private String explotacionRegistrada = "";
	private String fechaEfecto = "";
	private String fechaVersionCenso = "";
	private ArrayList<Linea> lineas = new ArrayList<Linea>();
	private ArrayList<AmbitoAgroseguro> ambitoAgroseguro = new ArrayList<AmbitoAgroseguro>();
	
	public String getExplotacionRegistrada() {
		return explotacionRegistrada;
	}

	public void setExplotacionRegistrada(String explotacionRegistrada) {
		this.explotacionRegistrada = explotacionRegistrada;
	}

	public String getFechaEfecto() {
		return fechaEfecto;
	}

	public void setFechaEfecto(String fechaEfecto) {
		this.fechaEfecto = fechaEfecto;
	}

	public String getFechaVersionCenso() {
		return fechaVersionCenso;
	}

	public void setFechaVersionCenso(String fechaVersionCenso) {
		this.fechaVersionCenso = fechaVersionCenso;
	}
	
	public ArrayList<AmbitoAgroseguro> getAmbitoAgroseguro() {
		return ambitoAgroseguro;
	}

	public void setAmbitoAgroseguro(ArrayList<AmbitoAgroseguro> elementos) {
		this.ambitoAgroseguro = elementos;
	}

	public ArrayList<Linea> getLineas() {
		return lineas;
	}

	public void setLineas(ArrayList<Linea> lineas) {
		this.lineas = lineas;
	}
	
	

	public class Linea  {
		
		private String id = "";
		private String descriptivo = "";
		private ArrayList<Especie> especies = new ArrayList<Especie>();
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
		public String getDescriptivo() {
			return descriptivo;
		}
		public void setDescriptivo(String descriptivo) {
			this.descriptivo = descriptivo;
		}
		
		public ArrayList<Especie> getEspecies() {
			return especies;
		}
		public void setEspecies(ArrayList<Especie> especies) {
			this.especies = especies;
		}

		public class Especie {
			
			private String especie = "";
			private String descriptivo = "";
			private ArrayList<Regimen> regimenes = new ArrayList<Regimen>();

			
			public String getEspecie() {
				return especie;
			}
			public void setEspecie(String especie) {
				this.especie = especie;
			}
			public String getDescriptivo() {
				return descriptivo;
			}
			public void setDescriptivo(String descriptivo) {
				this.descriptivo = descriptivo;
			}
			
			public ArrayList<Regimen> getRegimenes() {
				return regimenes;
			}
			public void setRegimenes(ArrayList<Regimen> regimenes) {
				this.regimenes = regimenes;
			}

			public class Regimen {
				
				private String regimen = "";
				private String descriptivo = "";
				private String censo = "";
				
				public String getRegimen() {
					return regimen;
				}
				public void setRegimen(String regimen) {
					this.regimen = regimen;
				}
				public String getDescriptivo() {
					return descriptivo;
				}
				public void setDescriptivo(String descriptivo) {
					this.descriptivo = descriptivo;
				}
				public String getCenso() {
					return censo;
				}
				public void setCenso(String censo) {
					this.censo = censo;
				}
			}
			
		}
	
	}

	

	public class AmbitoAgroseguro {
		
		
		private String zonificacionestimada;
		private Provincia provincia;
		private Comarca comarca;
		private Termino termino;
		private Subtermino subtermino;
		

		
		public String getZonificacionestimada() {
			return zonificacionestimada;
		}
		public void setZonificacionestimada(String zonificacionestimada) {
			this.zonificacionestimada = zonificacionestimada;
		}
		public Provincia getProvincia() {
			return provincia;
		}
		public void setProvincia(Provincia provincia) {
			this.provincia = provincia;
		}
		public Comarca getComarca() {
			return comarca;
		}
		public void setComarca(Comarca comarca) {
			this.comarca = comarca;
		}
		public Termino getTermino() {
			return termino;
		}
		public void setTermino(Termino termino) {
			this.termino = termino;
		}
		public Subtermino getSubtermino() {
			return subtermino;
		}
		public void setSubtermino(Subtermino subtermino) {
			this.subtermino = subtermino;
		}

		public class Provincia extends CodeDes{}
		public class Comarca extends CodeDes{}
		public class Termino extends CodeDes{}
		public class Subtermino extends CodeDes{}
	}
	
	
	public class CodeDes {
		private String codigo;
		private String descriptivo;
		


		public String getCodigo() {
			return codigo;
		}

		public void setCodigo(String codigo) {
			this.codigo = codigo;
		}

		public String getDescriptivo() {
			return descriptivo;
		}

		public void setDescriptivo(String descriptivo) {
			this.descriptivo = descriptivo;
		}
		
		
	}
	
	
}
