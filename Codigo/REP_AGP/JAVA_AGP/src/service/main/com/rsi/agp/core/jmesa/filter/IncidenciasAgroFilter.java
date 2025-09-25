package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;

/**
 * @author U029769
 *
 */
public class IncidenciasAgroFilter implements CriteriaCommand {

	private static final String OFICINA = "oficina";

	private List<Filter> filters = new ArrayList<Filter>();
	private VistaIncidenciasAgro vIncidenciasAgro;
	private final Log logger = LogFactory.getLog(getClass());

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public VistaIncidenciasAgro getvIncidenciasAgro() {
		return vIncidenciasAgro;
	}

	public void setvIncidenciasAgro(VistaIncidenciasAgro vIncidenciasAgro) {
		this.vIncidenciasAgro = vIncidenciasAgro;
	}

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}

		return criteria;
	}

	/**
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al filtro
	 * indicado por 'property,valor'
	 * 
	 * @param criteria
	 * @param property
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void buildCriteria(Criteria criteria, String property, Object value) {

		if (value != null && !value.toString().equals("")) {
			try {

				logger.debug("**@@**IncidenciasAgroFilter - buildCriteria. Valor del property:" + property);

				// entidad
				if (property.equals("codentidad") && value != null) {
					criteria.add(Restrictions.eq("codentidad", new BigDecimal(value.toString())));

				}
				// Modif tam (07.09.2018) ** Inicio **
				// oficina
				if (property.equals(OFICINA)) {
					criteria.add(Restrictions.in(OFICINA, CriteriaUtils.getCodigosOficina(value.toString())));
				}

				if (property.equals("listaGrupoOficinas")) {
					criteria.add(
							Restrictions.in(OFICINA, CriteriaUtils.getCodigosListaOficina((List<BigDecimal>) value)));
				}
				// MODIF TAM (07.09.2018) ** Fin **

				// entidad mediadora
				if (property.equals("entmediadora") && value != null) {
					criteria.add(Restrictions.eq("entmediadora", new BigDecimal(value.toString())));
				}
				// subentidad mediadora
				if (property.equals("subentmediadora") && value != null) {
					criteria.add(Restrictions.eq("subentmediadora", new BigDecimal(value.toString())));
				}
				// delegacion
				if (property.equals("delegacion") && value != null) {
					criteria.add(Restrictions.eq("delegacion", new BigDecimal(value.toString())));
				}
				// plan
				if (property.equals("codplan") && value != null) {
					criteria.add(Restrictions.eq("codplan", new BigDecimal(value.toString())));
				}
				// linea
				if (property.equals("codlinea") && value != null) {
					criteria.add(Restrictions.eq("codlinea", new BigDecimal(value.toString())));

				}
				// estado inc agroplus
				if (property.equals("codestado") && value != null) {
					criteria.add(Restrictions.eq("codestado", new BigDecimal(value.toString())));
				}
				// estado agro
				if (property.equals("codestadoagro") && value != null) {
					criteria.add(Restrictions.eq("codestadoagro", new Character(value.toString().charAt(0))));
				}
				// nif / cif
				if (property.equals("nifcif") && value != null) {
					criteria.add(Restrictions.eq("nifcif", value.toString()));
				}
				// asunto / asunto
				if (property.equals("codasunto") && value != null) {
					criteria.add(Restrictions.eq("codasunto", value.toString()));
				}

				// motivo
				if (property.equals("codmotivo") && value != null) {
					criteria.add(Restrictions.eq("codmotivo", new Integer(value.toString())));
				}

				// cupon
				if (property.equals("idcupon") && value != null) {
					criteria.add(Restrictions.eq("idcupon", value.toString()));
				}

				// tiporef
				if (property.equals("tiporef") && value != null) {
					criteria.add(Restrictions.eq("tiporef", value.toString()));
				}

				// MODIF TAM (04.06.2018) //
				// referencia (num de Poliza)
				if (property.equals("referencia") && value != null) {
					criteria.add(Restrictions.eq("referencia", value.toString()));
				}
				// numero (num Incidencia)
				if (property.equals("numero") && value != null) {
					criteria.add(Restrictions.eq("numero", new BigDecimal(value.toString())));
				}
				// Usuario
				if (property.equals("codusuario") && value != null) {
					criteria.add(Restrictions.eq("codusuario", value.toString()));
				}
				// MODIF TAM (04.06.2018) //

				// Pet. 57627 ** MODIF TAM (20.09.2019) ** Inicio //
				// Tipo Incidencia
				if (property.equals("tipoinc") && value != null) {
					criteria.add(Restrictions.eq("tipoinc", new Character(value.toString().charAt(0))));
				}
			} catch (Exception e) {
				logger.error("IncidenciasFilter - " + e.getMessage());
			}
		}
	}

	/**
	 * Anhade el filtro de Usuario para la lista de Ids.
	 * 
	 * @param MtoUsuariosFilter
	 * @return sqlWhere
	 */
	public String getSqlWhere() {
		String sqlWhere = " WHERE 1 = 1";

		logger.debug("**@@**IncidenciasAgroFilter - getSqlWhere");

		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();
				Object value = filter.getValue();
				if (!ObjectUtils.equals(value, "")) {
					// codentidad
					if (property.equals("codentidad")) {
						sqlWhere += " AND CODENTIDAD = " + value;
						continue;
					}
					if (property.equals(OFICINA)) {
						logger.debug("**@@** Valor de oficina:" + value.toString());
						logger.debug("**@@** Valor de Lista de Oficinas:"
								+ CriteriaUtils.getCodigosOficina(value.toString()));
						sqlWhere += " AND oficina in " + StringUtils.toValoresSeparadosXComas(
								CriteriaUtils.getCodigosOficina(value.toString()), false, true);
					}
					// entidad mediadora
					if (property.equals("entmediadora")) {
						sqlWhere += " AND entmediadora = " + value;
						continue;
					}
					// subentidad mediadora
					if (property.equals("subentmediadora")) {
						sqlWhere += " AND subentmediadora = " + value;
					}
					// delegacion
					if (property.equals("delegacion")) {
						sqlWhere += " AND delegacion = '" + value + "'";
						continue;
					}
					// plan
					if (property.equals("codplan")) {
						sqlWhere += " AND codplan = " + value;
						continue;
					}
					// linea
					if (property.equals("codlinea")) {
						sqlWhere += " AND codlinea = " + value;
						continue;
					}
					// estado incidencia agro
					if (property.equals("codestado")) {
						sqlWhere += " AND codestado = '" + value + "'";
						continue;
					}
					// estado agro
					if (property.equals("codestadoagro")) {
						sqlWhere += " AND codestadoagro = '" + value + "'";
						continue;
					}
					// nif/cif
					if (property.equals("nifcif")) {
						sqlWhere += " AND nifcif = '" + value + "'";
						continue;
					}
					// fecha
					if (property.equals("fecha")) {
						sqlWhere += " AND fecha = '" + value + "'";
						continue;
					}
					// cupon
					if (property.equals("idcupon")) {
						sqlWhere += " AND idcupon = '" + value + "'";
						continue;
					}
					// asunto
					if (property.equals("codasunto")) {
						sqlWhere += " AND CODASUNTO = '" + value + "'";
						continue;
					}
					// asunto
					if (property.equals("codmotivo")) {
						sqlWhere += " AND CODMOTIVO = " + value;
						continue;
					}
					// tipo ref
					if (property.equals("tiporef")) {
						sqlWhere += " AND tiporef = '" + value + "'";
						continue;
					}
					// referencia
					if (property.equals("referencia")) {
						sqlWhere += " AND referencia = '" + value + "'";
						continue;
					}
					// numero Incidencia
					if (property.equals("numero")) {
						sqlWhere += " AND numero = '" + value + "'";
						continue;
					}
					// usuario
					if (property.equals("codusuario")) {
						sqlWhere += " AND codusuario = '" + value + "'";
						continue;
					}

					// Tipologia
					if (property.equals("tipoinc")) {
						sqlWhere += " AND tipo_inc = '" + value + "'";
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("incidenciasFilter - Error al recuperar la lista de todos los ids -" + e.getMessage());
		}
		return sqlWhere;
	}

	private static class Filter {
		private final String property;
		private final Object value;

		public Filter(String property, Object value) {
			this.property = property;
			this.value = value;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}
	}
}