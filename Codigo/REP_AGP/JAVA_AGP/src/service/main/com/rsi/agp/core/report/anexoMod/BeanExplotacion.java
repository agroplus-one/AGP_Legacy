package com.rsi.agp.core.report.anexoMod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BeanExplotacion {

	private static final String SEPARATOR = " - ";
	private static final String ESPACIO = " ";

	private String codProvincia;
	private String nomProvincia;
	private String codComarca;
	private String nomComarca;
	private String codTermino;
	private String nomTermino;
	private String subtermino;
	private String codEspecie;
	private String nomEspecie;
	private String codRegimen;
	private String nomRegimen;
	private String rega;
	private String sigla;
	private String subexplotacion;
	private String latitud;
	private String longitud;
	private int numExplotacion;

	private List<BeanGrupoRaza> gruposRaza = new ArrayList<BeanGrupoRaza>();
	private List<BeanRiesgoCubiertoElegido> riesgosCubiertos = new ArrayList<BeanRiesgoCubiertoElegido>();

	public List<BeanRiesgoCubiertoElegido> getRiesgosCubiertos() {
		return riesgosCubiertos;
	}

	public void setRiesgosCubiertos(List<BeanRiesgoCubiertoElegido> riesgosCubiertos) {
		this.riesgosCubiertos = riesgosCubiertos;
	}

	// Constructor
	public BeanExplotacion() {
		super();
	}

	// Provincia
	public void setCodProvincia(final String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public void setNomProvincia(final String nomProvincia) {
		this.nomProvincia = nomProvincia;
	}

	public String getProvincia() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.codProvincia);
		sb.append(ESPACIO);
		sb.append(this.nomProvincia);
		return sb.toString();
	}

	// Comarca
	public void setCodComarca(final String codComarca) {
		this.codComarca = codComarca;
	}

	public void setNomComarca(final String nomComarca) {
		this.nomComarca = nomComarca;
	}

	public String getComarca() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.codComarca);
		sb.append(ESPACIO);
		sb.append(this.nomComarca);
		return sb.toString();
	}

	// Termino
	public void setCodTermino(final String codTermino) {
		this.codTermino = codTermino;
	}

	public void setNomTermino(final String nomTermino) {
		this.nomTermino = nomTermino;
	}

	public void setSubtermino(final String subtermino) {
		this.subtermino = subtermino;
	}

	public String getTermino() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.codTermino);
		sb.append(ESPACIO);
		sb.append(this.nomTermino);
		sb.append(ESPACIO);
		sb.append(this.subtermino);
		return sb.toString();
	}

	public void setCodEspecie(final String codEspecie) {
		this.codEspecie = codEspecie;
	}

	public void setNomEspecie(final String nomEspecie) {
		this.nomEspecie = nomEspecie;
	}

	public String getEspecie() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.codEspecie);
		sb.append(SEPARATOR);
		sb.append(this.nomEspecie);
		return sb.toString();
	}

	public void setCodRegimen(final String codRegimen) {
		this.codRegimen = codRegimen;
	}

	public void setNomRegimen(final String nomRegimen) {
		this.nomRegimen = nomRegimen;
	}

	public String getRegimen() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.codRegimen);
		sb.append(SEPARATOR);
		sb.append(this.nomRegimen);
		return sb.toString();
	}

	public String getRega() {
		return this.rega;
	}

	public void setRega(final String rega) {
		this.rega = rega;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(final String sigla) {
		this.sigla = sigla;
	}

	public String getSubexplotacion() {
		return this.subexplotacion;
	}

	public void setSubexplotacion(final String subexplotacion) {
		this.subexplotacion = subexplotacion;
	}

	public String getLatitud() {
		return this.latitud;
	}

	public void setLatitud(final String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return this.longitud;
	}

	public void setLongitud(final String longitud) {
		this.longitud = longitud;
	}

	// Lista grupo raza
	public List<BeanGrupoRaza> getGruposRaza() {
		return this.gruposRaza;
	}

	public void setGruposRaza(final List<BeanGrupoRaza> gruposRaza) {
		this.gruposRaza = gruposRaza;
	}

	public int getNumExplotacion() {
		return numExplotacion;
	}

	public void setNumExplotacion(int numExplotacion) {
		this.numExplotacion = numExplotacion;
	}

	public class BeanRiesgoCubiertoElegido {
		String nombreCptoPpal;
		String nombreRiesgoCub;
		String nombreCpto;
		String valor;
		int fila;
		int columna;

		public String getNombreCptoPpal() {
			return nombreCptoPpal;
		}

		public void setNombreCptoPpal(String nombreCptoPpal) {
			this.nombreCptoPpal = nombreCptoPpal;
		}

		public String getNombreRiesgoCub() {
			return nombreRiesgoCub;
		}

		public void setNombreRiesgoCub(String nombreRiesgoCub) {
			this.nombreRiesgoCub = nombreRiesgoCub;
		}

		public String getNombreCpto() {
			return nombreCpto;
		}

		public void setNombreCpto(String nombreCpto) {
			this.nombreCpto = nombreCpto;
		}

		public String getValor() {
			return valor;
		}

		public void setValor(String valor) {
			this.valor = valor;
		}

		public int getFila() {
			return fila;
		}

		public void setFila(int fila) {
			this.fila = fila;
		}

		public int getColumna() {
			return columna;
		}

		public void setColumna(int columna) {
			this.columna = columna;
		}

	}

	public class BeanGrupoRaza {

		private String codGrupoRaza;
		private String nomGrupoRaza;

		private List<BeanTipoCapital> beanTipoCapital = new ArrayList<BeanTipoCapital>();

		// Grupo raza
		public void setCodGrupoRaza(final String codGrupoRaza) {
			this.codGrupoRaza = codGrupoRaza;
		}

		public void setNomGrupoRaza(final String nomGrupoRaza) {
			this.nomGrupoRaza = nomGrupoRaza;
		}

		public String getGrupoRaza() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.codGrupoRaza);
			sb.append(SEPARATOR);
			sb.append(this.nomGrupoRaza);
			return sb.toString();
		}

		public List<BeanTipoCapital> getBeanTipoCapital() {
			return beanTipoCapital;
		}

		public void setBeanTipoCapital(List<BeanTipoCapital> beanTipoCapital) {
			this.beanTipoCapital = beanTipoCapital;
		}

		public class BeanTipoCapital {
			private String codTipoCapital;
			private String nomTipoCapital;
			private List<BeanTipoAnimal> beanTipoAnimal = new ArrayList<BeanTipoAnimal>();
			private List<DatoVariable> datosVariable = new ArrayList<DatoVariable>();

			// Tipo capital
			public void setCodTipoCapital(final String codTipoCapital) {
				this.codTipoCapital = codTipoCapital;
			}

			public void setNomTipoCapital(final String nomTipoCapital) {
				this.nomTipoCapital = nomTipoCapital;
			}

			public String getTipoCapital() {
				StringBuffer sb = new StringBuffer();
				sb.append(this.codTipoCapital);
				sb.append(SEPARATOR);
				sb.append(this.nomTipoCapital);
				return sb.toString();
			}

			public List<BeanTipoAnimal> getBeanTipoAnimal() {
				return beanTipoAnimal;
			}

			public void setBeanTipoAnimal(List<BeanTipoAnimal> beanTipoAnimal) {
				this.beanTipoAnimal = beanTipoAnimal;
			}

			public String getCodTipoCapital() {
				return codTipoCapital;
			}

			public String getNomTipoCapital() {
				return nomTipoCapital;
			}

			public List<DatoVariable> getDatosVariable() {
				return this.datosVariable;
			}

			public void setDatosVariable(final List<DatoVariable> datosVariable) {
				this.datosVariable = datosVariable;
			}

			public class BeanTipoAnimal {
				private String codTipoAnimal;
				private String nomTipoAnimal;
				private String numAnimales;
				private BigDecimal precio;
				private String etiquetaConcepto;

				// Tipo animal
				public void setCodTipoAnimal(final String codTipoAnimal) {
					this.codTipoAnimal = codTipoAnimal;
				}

				public void setNomTipoAnimal(final String nomTipoAnimal) {
					this.nomTipoAnimal = nomTipoAnimal;
				}

				public String getTipoAnimal() {
					StringBuffer sb = new StringBuffer();
					sb.append(this.codTipoAnimal);
					sb.append(SEPARATOR);
					sb.append(this.nomTipoAnimal);
					return sb.toString();
				}

				public String getNumAnimales() {
					return this.numAnimales;
				}

				public void setNumAnimales(final String numAnimales) {
					this.numAnimales = numAnimales;
				}

				public BigDecimal getPrecio() {
					return this.precio;
				}

				public void setPrecio(final BigDecimal precio) {
					this.precio = precio;
				}

				public String getEtiquetaConcepto() {
					return etiquetaConcepto;
				}

				public void setEtiquetaConcepto(String etiquetaConcepto) {
					this.etiquetaConcepto = etiquetaConcepto;
				}
			}
		}
	}
}