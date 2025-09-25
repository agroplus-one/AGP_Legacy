/*
 **************************************************************************************************
 *
 *  CReACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034              Antonio Serrano  
 *
 **************************************************************************************************
 */
package com.rsi.agp.dao.filters.commons;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;



public class ErrorWsFiltro implements Filter {
	
	public static final String CAT_SINIESTRO = "S";
	public static final String CAT_POLIZA = "P";

	private BigDecimal codPlan;
	private BigDecimal codLinea;
	private BigDecimal codEntidad;
	private Character ocultar;
	private BigDecimal tipoUsuario;
	private String servicio;
	
	public BigDecimal getCodPlan() {
		return codPlan;
	}

	public void setCodPlan(BigDecimal codPlan) {
		this.codPlan = codPlan;
	}

	public BigDecimal getCodLinea() {
		return codLinea;
	}

	public void setCodLinea(BigDecimal codLinea) {
		this.codLinea = codLinea;
	}

	public BigDecimal getCodEntidad() {
		return codEntidad;
	}

	public void setCodEntidad(BigDecimal codEntidad) {
		this.codEntidad = codEntidad;
	}

	public Character getOcultar() {
		return ocultar;
	}

	public void setOcultar(Character ocultar) {
		this.ocultar = ocultar;
	}

	public BigDecimal getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(BigDecimal tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	/**
	 * Crea la consulta con el campo ocultar fijado a S
	 * @param codPlan
	 * @param codLinea
	 * @param codEntidad
	 * @param servicio
	 */
	public ErrorWsFiltro(BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntidad, String servicio) {
		super();
		this.codPlan = codPlan;
		this.codLinea = codLinea;
		this.tipoUsuario = null;
		this.ocultar = Constants.CHARACTER_S;
		this.servicio = servicio;
		this.codEntidad = codEntidad;
	}

	/**
	 * Crea la consulta con el campo ocultar fijado a N
	 * @param codPlan
	 * @param codLinea
	 * @param codEntidad
	 * @param tipoUsuario
	 * @param servicio
	 */
	public ErrorWsFiltro(BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntidad,
			BigDecimal tipoUsuario, String servicio) {
		super();
		this.codPlan = codPlan;
		this.codLinea = codLinea;
		this.tipoUsuario = tipoUsuario;
		this.ocultar = Constants.CHARACTER_N;
		this.servicio = servicio;
		this.codEntidad = codEntidad;
	}
	
	@Override
	public Criteria getCriteria(Session session) { 
		Criteria criteria = session.createCriteria(ErrorWsAccion.class);		
		
		/* Pet. 63481 ** MODIF TAM (18.05.2021) ** Inicio */
		/* Incluimos filtro para obtener únicamente los errores del catalógo 'P-Póliza' si el servicio no es SN-Siniestro */
		/* y en caso de que el servicio sea "SN-Siniestro" obtener los errores del catálogo de 'S-Siniestros' */
		if (!servicio.equals(Constants.SINIESTRO)) {
			criteria.add(Restrictions.eq("errorWs.id.catalogo", CAT_POLIZA));
		}else {
			criteria.add(Restrictions.eq("errorWs.id.catalogo", CAT_SINIESTRO));
		}
		/* Pet. 63481 ** MODIF TAM (18.05.2021) ** Fin */
			
		criteria.add(Restrictions.eq("servicio", servicio));
		criteria.add(Restrictions.eq("ocultar", ocultar));
		criteria.createAlias("linea", "linea");
		criteria.add(Restrictions.eq("linea.codplan", codPlan));
		criteria.add(Restrictions.or(Restrictions.eq("linea.codlinea", codLinea), Restrictions.eq("linea.codlinea", Constants.CODLINEA_GENERICA)));
		if(tipoUsuario != null) {
			criteria.createAlias("codErrorPerfiles", "errorPerfiles");
			criteria.add(Restrictions.eq("errorPerfiles.perfil.id", tipoUsuario));
		}
		criteria.createAlias("entidad", "entidad", CriteriaSpecification.LEFT_JOIN);
		criteria.add(Restrictions.or(Restrictions.eq("entidad.codentidad", codEntidad), Restrictions.isNull("entidad.codentidad")));
		return criteria;
	}
}
