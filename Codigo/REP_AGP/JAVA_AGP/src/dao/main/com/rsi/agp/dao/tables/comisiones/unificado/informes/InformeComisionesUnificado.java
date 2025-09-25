package com.rsi.agp.dao.tables.comisiones.unificado.informes;

import java.math.BigDecimal;
import java.util.Date;

public class InformeComisionesUnificado implements java.io.Serializable {

	private static final long serialVersionUID = 4592136177256181391L;

	protected BigDecimal id;
	private BigDecimal codentidad;
	private String nomentidad;
	private String oficina;
	private String nomoficina;
	private Integer entmediadora;
	private Integer subentmediadora;
	private Integer plan;
	private Integer linea;
	private String nomlinea;
	private String referencia;
	private String idcolectivo;
	private String ciftomador;
	private String nifcif;
	private String nombreAsegurado;
	private Integer recibo;
	private Integer fase;
	private Date fechaCarga;
	private Date fechaEmisionRecibo;
	private Date fechaAceptacion;
	private Date fechaCierre;
	private Character grupoNegocio;
	private BigDecimal primaComercialNeta;
	private BigDecimal gdAdmin;
	private BigDecimal gdAdq;
	private BigDecimal gdComisionMediador;
	private BigDecimal gdCommedEntidad;
	private BigDecimal gdCommedEsmed;
	private BigDecimal gaAdmin;
	private BigDecimal gaAdq;
	private BigDecimal gaComisionMediador;
	private BigDecimal gaCommedEntidad;
	private BigDecimal gaCommedEsmed;
	private BigDecimal gpAdmin;
	private BigDecimal gpAdq;
	private BigDecimal gpComisionMediador;
	private BigDecimal gpCommedEntidad;
	private BigDecimal gpCommedEsmed;
	private Date fechaVigor;

	private String opcionfechaCarga;
	private String opcionfechaEmisionRecibo;
	private String opcionfechaAceptacion;
	private String opcionfechaCierre;
	private String entreFechaCarga;
	private String entreFechaEmisionRecibo;
	private String entreFechaAceptacion;
	private String entreFechaCierre;
	private String opcionfechaVigor;
	private String entreFechaVigor;

	public InformeComisionesUnificado() {
	}

	public InformeComisionesUnificado(BigDecimal codentidad, Integer plan,
			String ciftomador, Integer recibo, Integer fase, Date fechaCarga) {
		this.codentidad = codentidad;
		this.plan = plan;
		this.ciftomador = ciftomador;
		this.recibo = recibo;
		this.fase = fase;
		this.fechaCarga = fechaCarga;
	}

	public InformeComisionesUnificado(BigDecimal id, BigDecimal codentidad,
			String nomentidad, String oficina, String nomoficina,
			Integer entmediadora, Integer subentmediadora, Integer plan,
			Integer linea, String nomlinea, String referencia,
			String idcolectivo, String ciftomador, String nifcif,
			String nombreAsegurado, Integer recibo, Integer fase,
			Date fechaCarga, Date fechaEmisionRecibo, Date fechaAceptacion,
			Date fechaCierre, Date fechaVigor, Character grupoNegocio,
			BigDecimal primaComercialNeta, BigDecimal gdAdmin,
			BigDecimal gdAdq, BigDecimal gdComisionMediador,
			BigDecimal gdCommedEntidad, BigDecimal gdCommedEsmed,
			BigDecimal gaAdmin, BigDecimal gaAdq,
			BigDecimal gaComisionMediador, BigDecimal gaCommedEntidad,
			BigDecimal gaCommedEsmed, BigDecimal gpAdmin, BigDecimal gpAdq,
			BigDecimal gpComisionMediador, BigDecimal gpCommedEntidad,
			BigDecimal gpCommedEsmed, String opcionfechaCarga,
			String opcionfechaEmisionRecibo, String opcionfechaAceptacion,
			String opcionfechaCierre, String opcionfechaVigor,
			String entreFechaCarga, String entreFechaEmisionRecibo,
			String entreFechaAceptacion, String entreFechaCierre,
			String entreFechaVigor) {
		super();
		this.id = id;
		this.codentidad = codentidad;
		this.nomentidad = nomentidad;
		this.oficina = oficina;
		this.nomoficina = nomoficina;
		this.entmediadora = entmediadora;
		this.subentmediadora = subentmediadora;
		this.plan = plan;
		this.linea = linea;
		this.nomlinea = nomlinea;
		this.referencia = referencia;
		this.idcolectivo = idcolectivo;
		this.ciftomador = ciftomador;
		this.nifcif = nifcif;
		this.nombreAsegurado = nombreAsegurado;
		this.recibo = recibo;
		this.fase = fase;
		this.fechaCarga = fechaCarga;
		this.fechaEmisionRecibo = fechaEmisionRecibo;
		this.fechaAceptacion = fechaAceptacion;
		this.fechaCierre = fechaCierre;
		this.fechaVigor = fechaVigor;
		this.grupoNegocio = grupoNegocio;
		this.primaComercialNeta = primaComercialNeta;
		this.gdAdmin = gdAdmin;
		this.gdAdq = gdAdq;
		this.gdComisionMediador = gdComisionMediador;
		this.gdCommedEntidad = gdCommedEntidad;
		this.gdCommedEsmed = gdCommedEsmed;
		this.gaAdmin = gaAdmin;
		this.gaAdq = gaAdq;
		this.gaComisionMediador = gaComisionMediador;
		this.gaCommedEntidad = gaCommedEntidad;
		this.gaCommedEsmed = gaCommedEsmed;
		this.gpAdmin = gpAdmin;
		this.gpAdq = gpAdq;
		this.gpComisionMediador = gpComisionMediador;
		this.gpCommedEntidad = gpCommedEntidad;
		this.gpCommedEsmed = gpCommedEsmed;

		this.opcionfechaCarga = opcionfechaCarga;
		this.opcionfechaEmisionRecibo = opcionfechaEmisionRecibo;
		this.opcionfechaAceptacion = opcionfechaAceptacion;
		this.opcionfechaCierre = opcionfechaCierre;
		this.opcionfechaVigor = opcionfechaVigor;
		this.entreFechaCarga = entreFechaCarga;
		this.entreFechaEmisionRecibo = entreFechaEmisionRecibo;
		this.entreFechaAceptacion = entreFechaAceptacion;
		this.entreFechaCierre = entreFechaCierre;
		this.entreFechaVigor = entreFechaVigor;
	}

	public BigDecimal getCodentidad() {
		return this.codentidad;
	}

	public void setCodentidad(BigDecimal codentidad) {
		this.codentidad = codentidad;
	}

	public String getOficina() {
		return this.oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public Integer getEntmediadora() {
		return this.entmediadora;
	}

	public void setEntmediadora(Integer entmediadora) {
		this.entmediadora = entmediadora;
	}

	public Integer getSubentmediadora() {
		return this.subentmediadora;
	}

	public void setSubentmediadora(Integer subentmediadora) {
		this.subentmediadora = subentmediadora;
	}

	public Integer getPlan() {
		return this.plan;
	}

	public void setPlan(Integer plan) {
		this.plan = plan;
	}

	public Integer getLinea() {
		return this.linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}

	public String getReferencia() {
		return this.referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getIdcolectivo() {
		return this.idcolectivo;
	}

	public void setIdcolectivo(String idcolectivo) {
		this.idcolectivo = idcolectivo;
	}

	public String getCiftomador() {
		return this.ciftomador;
	}

	public void setCiftomador(String ciftomador) {
		this.ciftomador = ciftomador;
	}

	public String getNifcif() {
		return this.nifcif;
	}

	public void setNifcif(String nifcif) {
		this.nifcif = nifcif;
	}

	public String getNombreAsegurado() {
		return this.nombreAsegurado;
	}

	public void setNombreAsegurado(String nombreAsegurado) {
		this.nombreAsegurado = nombreAsegurado;
	}

	public Integer getRecibo() {
		return this.recibo;
	}

	public void setRecibo(Integer recibo) {
		this.recibo = recibo;
	}

	public Integer getFase() {
		return this.fase;
	}

	public void setFase(Integer fase) {
		this.fase = fase;
	}

	public Date getFechaCarga() {
		return this.fechaCarga;
	}

	public void setFechaCarga(Date fechaCarga) {
		this.fechaCarga = fechaCarga;
	}

	public Date getFechaEmisionRecibo() {
		return this.fechaEmisionRecibo;
	}

	public void setFechaEmisionRecibo(Date fechaEmisionRecibo) {
		this.fechaEmisionRecibo = fechaEmisionRecibo;
	}

	public Date getFechaAceptacion() {
		return this.fechaAceptacion;
	}

	public void setFechaAceptacion(Date fechaAceptacion) {
		this.fechaAceptacion = fechaAceptacion;
	}

	public Date getFechaCierre() {
		return this.fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public Character getGrupoNegocio() {
		return this.grupoNegocio;
	}

	public void setGrupoNegocio(Character grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

	public BigDecimal getPrimaComercialNeta() {
		return this.primaComercialNeta;
	}

	public void setPrimaComercialNeta(BigDecimal primaComercialNeta) {
		this.primaComercialNeta = primaComercialNeta;
	}

	public BigDecimal getGdAdmin() {
		return this.gdAdmin;
	}

	public void setGdAdmin(BigDecimal gdAdmin) {
		this.gdAdmin = gdAdmin;
	}

	public BigDecimal getGdAdq() {
		return this.gdAdq;
	}

	public void setGdAdq(BigDecimal gdAdq) {
		this.gdAdq = gdAdq;
	}

	public BigDecimal getGdComisionMediador() {
		return this.gdComisionMediador;
	}

	public void setGdComisionMediador(BigDecimal gdComisionMediador) {
		this.gdComisionMediador = gdComisionMediador;
	}

	public BigDecimal getGdCommedEntidad() {
		return this.gdCommedEntidad;
	}

	public void setGdCommedEntidad(BigDecimal gdCommedEntidad) {
		this.gdCommedEntidad = gdCommedEntidad;
	}

	public BigDecimal getGdCommedEsmed() {
		return this.gdCommedEsmed;
	}

	public void setGdCommedEsmed(BigDecimal gdCommedEsmed) {
		this.gdCommedEsmed = gdCommedEsmed;
	}

	public BigDecimal getGaAdmin() {
		return this.gaAdmin;
	}

	public void setGaAdmin(BigDecimal gaAdmin) {
		this.gaAdmin = gaAdmin;
	}

	public BigDecimal getGaAdq() {
		return this.gaAdq;
	}

	public void setGaAdq(BigDecimal gaAdq) {
		this.gaAdq = gaAdq;
	}

	public BigDecimal getGaComisionMediador() {
		return this.gaComisionMediador;
	}

	public void setGaComisionMediador(BigDecimal gaComisionMediador) {
		this.gaComisionMediador = gaComisionMediador;
	}

	public BigDecimal getGaCommedEntidad() {
		return this.gaCommedEntidad;
	}

	public void setGaCommedEntidad(BigDecimal gaCommedEntidad) {
		this.gaCommedEntidad = gaCommedEntidad;
	}

	public BigDecimal getGaCommedEsmed() {
		return this.gaCommedEsmed;
	}

	public void setGaCommedEsmed(BigDecimal gaCommedEsmed) {
		this.gaCommedEsmed = gaCommedEsmed;
	}

	public BigDecimal getGpAdmin() {
		return this.gpAdmin;
	}

	public void setGpAdmin(BigDecimal gpAdmin) {
		this.gpAdmin = gpAdmin;
	}

	public BigDecimal getGpAdq() {
		return this.gpAdq;
	}

	public void setGpAdq(BigDecimal gpAdq) {
		this.gpAdq = gpAdq;
	}

	public BigDecimal getGpComisionMediador() {
		return this.gpComisionMediador;
	}

	public void setGpComisionMediador(BigDecimal gpComisionMediador) {
		this.gpComisionMediador = gpComisionMediador;
	}

	public BigDecimal getGpCommedEntidad() {
		return this.gpCommedEntidad;
	}

	public void setGpCommedEntidad(BigDecimal gpCommedEntidad) {
		this.gpCommedEntidad = gpCommedEntidad;
	}

	public BigDecimal getGpCommedEsmed() {
		return this.gpCommedEsmed;
	}

	public void setGpCommedEsmed(BigDecimal gpCommedEsmed) {
		this.gpCommedEsmed = gpCommedEsmed;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getNomentidad() {
		return nomentidad;
	}

	public void setNomentidad(String nomentidad) {
		this.nomentidad = nomentidad;
	}

	public String getNomoficina() {
		return nomoficina;
	}

	public void setNomoficina(String nomoficina) {
		this.nomoficina = nomoficina;
	}

	public String getNomlinea() {
		return nomlinea;
	}

	public void setNomlinea(String nomlinea) {
		this.nomlinea = nomlinea;
	}

	public String getOpcionfechaCarga() {
		return opcionfechaCarga;
	}

	public void setOpcionfechaCarga(String opcionfechaCarga) {
		this.opcionfechaCarga = opcionfechaCarga;
	}

	public String getOpcionfechaEmisionRecibo() {
		return opcionfechaEmisionRecibo;
	}

	public void setOpcionfechaEmisionRecibo(String opcionfechaEmisionRecibo) {
		this.opcionfechaEmisionRecibo = opcionfechaEmisionRecibo;
	}

	public String getOpcionfechaAceptacion() {
		return opcionfechaAceptacion;
	}

	public void setOpcionfechaAceptacion(String opcionfechaAceptacion) {
		this.opcionfechaAceptacion = opcionfechaAceptacion;
	}

	public String getOpcionfechaCierre() {
		return opcionfechaCierre;
	}

	public void setOpcionfechaCierre(String opcionfechaCierre) {
		this.opcionfechaCierre = opcionfechaCierre;
	}

	public String getEntreFechaCarga() {
		return entreFechaCarga;
	}

	public void setEntreFechaCarga(String entreFechaCarga) {
		this.entreFechaCarga = entreFechaCarga;
	}

	public String getEntreFechaEmisionRecibo() {
		return entreFechaEmisionRecibo;
	}

	public void setEntreFechaEmisionRecibo(String entreFechaEmisionRecibo) {
		this.entreFechaEmisionRecibo = entreFechaEmisionRecibo;
	}

	public String getEntreFechaAceptacion() {
		return entreFechaAceptacion;
	}

	public void setEntreFechaAceptacion(String entreFechaAceptacion) {
		this.entreFechaAceptacion = entreFechaAceptacion;
	}

	public String getEntreFechaCierre() {
		return entreFechaCierre;
	}

	public void setEntreFechaCierre(String entreFechaCierre) {
		this.entreFechaCierre = entreFechaCierre;
	}

	public Date getFechaVigor() {
		return fechaVigor;
	}

	public void setFechaVigor(Date fechaVigor) {
		this.fechaVigor = fechaVigor;
	}

	public String getOpcionfechaVigor() {
		return opcionfechaVigor;
	}

	public void setOpcionfechaVigor(String opcionfechaVigor) {
		this.opcionfechaVigor = opcionfechaVigor;
	}

	public String getEntreFechaVigor() {
		return entreFechaVigor;
	}

	public void setEntreFechaVigor(String entreFechaVigor) {
		this.entreFechaVigor = entreFechaVigor;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InformeComisionesUnificado other = (InformeComisionesUnificado) obj;
		if (ciftomador == null) {
			if (other.ciftomador != null)
				return false;
		} else if (!ciftomador.equals(other.ciftomador))
			return false;
		if (codentidad == null) {
			if (other.codentidad != null)
				return false;
		} else if (!codentidad.equals(other.codentidad))
			return false;
		if (entmediadora == null) {
			if (other.entmediadora != null)
				return false;
		} else if (!entmediadora.equals(other.entmediadora))
			return false;
		if (entreFechaAceptacion == null) {
			if (other.entreFechaAceptacion != null)
				return false;
		} else if (!entreFechaAceptacion.equals(other.entreFechaAceptacion))
			return false;
		if (entreFechaCarga == null) {
			if (other.entreFechaCarga != null)
				return false;
		} else if (!entreFechaCarga.equals(other.entreFechaCarga))
			return false;
		if (entreFechaCierre == null) {
			if (other.entreFechaCierre != null)
				return false;
		} else if (!entreFechaCierre.equals(other.entreFechaCierre))
			return false;
		if (entreFechaVigor == null) {
			if (other.entreFechaVigor != null)
				return false;
		} else if (!entreFechaVigor.equals(other.entreFechaVigor))
			return false;
		if (entreFechaEmisionRecibo == null) {
			if (other.entreFechaEmisionRecibo != null)
				return false;
		} else if (!entreFechaEmisionRecibo
				.equals(other.entreFechaEmisionRecibo))
			return false;
		if (fase == null) {
			if (other.fase != null)
				return false;
		} else if (!fase.equals(other.fase))
			return false;
		if (fechaAceptacion == null) {
			if (other.fechaAceptacion != null)
				return false;
		} else if (!fechaAceptacion.equals(other.fechaAceptacion))
			return false;
		if (fechaCarga == null) {
			if (other.fechaCarga != null)
				return false;
		} else if (!fechaCarga.equals(other.fechaCarga))
			return false;
		if (fechaCierre == null) {
			if (other.fechaCierre != null)
				return false;
		} else if (!fechaCierre.equals(other.fechaCierre))
			return false;
		if (fechaVigor == null) {
			if (other.fechaVigor != null)
				return false;
		} else if (!fechaVigor.equals(other.fechaVigor))
			return false;
		if (fechaEmisionRecibo == null) {
			if (other.fechaEmisionRecibo != null)
				return false;
		} else if (!fechaEmisionRecibo.equals(other.fechaEmisionRecibo))
			return false;
		if (gaAdmin == null) {
			if (other.gaAdmin != null)
				return false;
		} else if (!gaAdmin.equals(other.gaAdmin))
			return false;
		if (gaAdq == null) {
			if (other.gaAdq != null)
				return false;
		} else if (!gaAdq.equals(other.gaAdq))
			return false;
		if (gaComisionMediador == null) {
			if (other.gaComisionMediador != null)
				return false;
		} else if (!gaComisionMediador.equals(other.gaComisionMediador))
			return false;
		if (gaCommedEntidad == null) {
			if (other.gaCommedEntidad != null)
				return false;
		} else if (!gaCommedEntidad.equals(other.gaCommedEntidad))
			return false;
		if (gaCommedEsmed == null) {
			if (other.gaCommedEsmed != null)
				return false;
		} else if (!gaCommedEsmed.equals(other.gaCommedEsmed))
			return false;
		if (gdAdmin == null) {
			if (other.gdAdmin != null)
				return false;
		} else if (!gdAdmin.equals(other.gdAdmin))
			return false;
		if (gdAdq == null) {
			if (other.gdAdq != null)
				return false;
		} else if (!gdAdq.equals(other.gdAdq))
			return false;
		if (gdComisionMediador == null) {
			if (other.gdComisionMediador != null)
				return false;
		} else if (!gdComisionMediador.equals(other.gdComisionMediador))
			return false;
		if (gdCommedEntidad == null) {
			if (other.gdCommedEntidad != null)
				return false;
		} else if (!gdCommedEntidad.equals(other.gdCommedEntidad))
			return false;
		if (gdCommedEsmed == null) {
			if (other.gdCommedEsmed != null)
				return false;
		} else if (!gdCommedEsmed.equals(other.gdCommedEsmed))
			return false;
		if (gpAdmin == null) {
			if (other.gpAdmin != null)
				return false;
		} else if (!gpAdmin.equals(other.gpAdmin))
			return false;
		if (gpAdq == null) {
			if (other.gpAdq != null)
				return false;
		} else if (!gpAdq.equals(other.gpAdq))
			return false;
		if (gpComisionMediador == null) {
			if (other.gpComisionMediador != null)
				return false;
		} else if (!gpComisionMediador.equals(other.gpComisionMediador))
			return false;
		if (gpCommedEntidad == null) {
			if (other.gpCommedEntidad != null)
				return false;
		} else if (!gpCommedEntidad.equals(other.gpCommedEntidad))
			return false;
		if (gpCommedEsmed == null) {
			if (other.gpCommedEsmed != null)
				return false;
		} else if (!gpCommedEsmed.equals(other.gpCommedEsmed))
			return false;
		if (grupoNegocio == null) {
			if (other.grupoNegocio != null)
				return false;
		} else if (!grupoNegocio.equals(other.grupoNegocio))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idcolectivo == null) {
			if (other.idcolectivo != null)
				return false;
		} else if (!idcolectivo.equals(other.idcolectivo))
			return false;
		if (linea == null) {
			if (other.linea != null)
				return false;
		} else if (!linea.equals(other.linea))
			return false;
		if (nifcif == null) {
			if (other.nifcif != null)
				return false;
		} else if (!nifcif.equals(other.nifcif))
			return false;
		if (nombreAsegurado == null) {
			if (other.nombreAsegurado != null)
				return false;
		} else if (!nombreAsegurado.equals(other.nombreAsegurado))
			return false;
		if (nomentidad == null) {
			if (other.nomentidad != null)
				return false;
		} else if (!nomentidad.equals(other.nomentidad))
			return false;
		if (nomlinea == null) {
			if (other.nomlinea != null)
				return false;
		} else if (!nomlinea.equals(other.nomlinea))
			return false;
		if (nomoficina == null) {
			if (other.nomoficina != null)
				return false;
		} else if (!nomoficina.equals(other.nomoficina))
			return false;
		if (oficina == null) {
			if (other.oficina != null)
				return false;
		} else if (!oficina.equals(other.oficina))
			return false;
		if (opcionfechaAceptacion == null) {
			if (other.opcionfechaAceptacion != null)
				return false;
		} else if (!opcionfechaAceptacion.equals(other.opcionfechaAceptacion))
			return false;
		if (opcionfechaCarga == null) {
			if (other.opcionfechaCarga != null)
				return false;
		} else if (!opcionfechaCarga.equals(other.opcionfechaCarga))
			return false;
		if (opcionfechaCierre == null) {
			if (other.opcionfechaCierre != null)
				return false;
		} else if (!opcionfechaCierre.equals(other.opcionfechaCierre))
			return false;
		if (opcionfechaVigor == null) {
			if (other.opcionfechaVigor != null)
				return false;
		} else if (!opcionfechaVigor.equals(other.opcionfechaVigor))
			return false;
		if (opcionfechaEmisionRecibo == null) {
			if (other.opcionfechaEmisionRecibo != null)
				return false;
		} else if (!opcionfechaEmisionRecibo
				.equals(other.opcionfechaEmisionRecibo))
			return false;
		if (plan == null) {
			if (other.plan != null)
				return false;
		} else if (!plan.equals(other.plan))
			return false;
		if (primaComercialNeta == null) {
			if (other.primaComercialNeta != null)
				return false;
		} else if (!primaComercialNeta.equals(other.primaComercialNeta))
			return false;
		if (recibo == null) {
			if (other.recibo != null)
				return false;
		} else if (!recibo.equals(other.recibo))
			return false;
		if (referencia == null) {
			if (other.referencia != null)
				return false;
		} else if (!referencia.equals(other.referencia))
			return false;
		if (subentmediadora == null) {
			if (other.subentmediadora != null)
				return false;
		} else if (!subentmediadora.equals(other.subentmediadora))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ciftomador == null) ? 0 : ciftomador.hashCode());
		result = prime * result
				+ ((codentidad == null) ? 0 : codentidad.hashCode());
		result = prime * result
				+ ((entmediadora == null) ? 0 : entmediadora.hashCode());
		result = prime
				* result
				+ ((entreFechaAceptacion == null) ? 0 : entreFechaAceptacion
						.hashCode());
		result = prime * result
				+ ((entreFechaCarga == null) ? 0 : entreFechaCarga.hashCode());
		result = prime
				* result
				+ ((entreFechaCierre == null) ? 0 : entreFechaCierre.hashCode());
		result = prime * result
				+ ((entreFechaVigor == null) ? 0 : entreFechaVigor.hashCode());
		result = prime
				* result
				+ ((entreFechaEmisionRecibo == null) ? 0
						: entreFechaEmisionRecibo.hashCode());
		result = prime * result + ((fase == null) ? 0 : fase.hashCode());
		result = prime * result
				+ ((fechaAceptacion == null) ? 0 : fechaAceptacion.hashCode());
		result = prime * result
				+ ((fechaCarga == null) ? 0 : fechaCarga.hashCode());
		result = prime * result
				+ ((fechaCierre == null) ? 0 : fechaCierre.hashCode());
		result = prime * result
				+ ((fechaVigor == null) ? 0 : fechaVigor.hashCode());
		result = prime
				* result
				+ ((fechaEmisionRecibo == null) ? 0 : fechaEmisionRecibo
						.hashCode());
		result = prime * result + ((gaAdmin == null) ? 0 : gaAdmin.hashCode());
		result = prime * result + ((gaAdq == null) ? 0 : gaAdq.hashCode());
		result = prime
				* result
				+ ((gaComisionMediador == null) ? 0 : gaComisionMediador
						.hashCode());
		result = prime * result
				+ ((gaCommedEntidad == null) ? 0 : gaCommedEntidad.hashCode());
		result = prime * result
				+ ((gaCommedEsmed == null) ? 0 : gaCommedEsmed.hashCode());
		result = prime * result + ((gdAdmin == null) ? 0 : gdAdmin.hashCode());
		result = prime * result + ((gdAdq == null) ? 0 : gdAdq.hashCode());
		result = prime
				* result
				+ ((gdComisionMediador == null) ? 0 : gdComisionMediador
						.hashCode());
		result = prime * result
				+ ((gdCommedEntidad == null) ? 0 : gdCommedEntidad.hashCode());
		result = prime * result
				+ ((gdCommedEsmed == null) ? 0 : gdCommedEsmed.hashCode());
		result = prime * result + ((gpAdmin == null) ? 0 : gpAdmin.hashCode());
		result = prime * result + ((gpAdq == null) ? 0 : gpAdq.hashCode());
		result = prime
				* result
				+ ((gpComisionMediador == null) ? 0 : gpComisionMediador
						.hashCode());
		result = prime * result
				+ ((gpCommedEntidad == null) ? 0 : gpCommedEntidad.hashCode());
		result = prime * result
				+ ((gpCommedEsmed == null) ? 0 : gpCommedEsmed.hashCode());
		result = prime * result
				+ ((grupoNegocio == null) ? 0 : grupoNegocio.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((idcolectivo == null) ? 0 : idcolectivo.hashCode());
		result = prime * result + ((linea == null) ? 0 : linea.hashCode());
		result = prime * result + ((nifcif == null) ? 0 : nifcif.hashCode());
		result = prime * result
				+ ((nombreAsegurado == null) ? 0 : nombreAsegurado.hashCode());
		result = prime * result
				+ ((nomentidad == null) ? 0 : nomentidad.hashCode());
		result = prime * result
				+ ((nomlinea == null) ? 0 : nomlinea.hashCode());
		result = prime * result
				+ ((nomoficina == null) ? 0 : nomoficina.hashCode());
		result = prime * result + ((oficina == null) ? 0 : oficina.hashCode());

		result = prime
				* result
				+ ((opcionfechaAceptacion == null) ? 0 : opcionfechaAceptacion
						.hashCode());
		result = prime
				* result
				+ ((opcionfechaCarga == null) ? 0 : opcionfechaCarga.hashCode());
		result = prime
				* result
				+ ((opcionfechaCierre == null) ? 0 : opcionfechaCierre
						.hashCode());
		result = prime
				* result
				+ ((opcionfechaVigor == null) ? 0 : opcionfechaVigor.hashCode());
		result = prime
				* result
				+ ((opcionfechaEmisionRecibo == null) ? 0
						: opcionfechaEmisionRecibo.hashCode());
		result = prime * result + ((plan == null) ? 0 : plan.hashCode());
		result = prime
				* result
				+ ((primaComercialNeta == null) ? 0 : primaComercialNeta
						.hashCode());
		result = prime * result + ((recibo == null) ? 0 : recibo.hashCode());
		result = prime * result
				+ ((referencia == null) ? 0 : referencia.hashCode());
		result = prime * result
				+ ((subentmediadora == null) ? 0 : subentmediadora.hashCode());
		return result;
	}
}