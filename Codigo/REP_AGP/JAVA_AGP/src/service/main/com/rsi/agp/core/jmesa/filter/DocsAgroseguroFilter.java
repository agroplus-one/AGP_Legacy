package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;

public class DocsAgroseguroFilter extends GenericoFilter implements IGenericoFilter {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log logger = LogFactory.getLog(getClass());

	private boolean descripccionExacta = Boolean.FALSE;

	public void addFilter(final String property, final Object value) {
		filters.add(new Filter(property, value));
	}

	@Override
	public Criteria execute(final Criteria criteria) {

		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		return criteria;
	}

	private void buildCriteria(final Criteria criteria, final String property, final Object value) {
		if (value != null) {
			try {
				// PLAN
				if (property.equals("codplan") && value != null) {
					criteria.add(Restrictions.eq("codplan", new BigDecimal((String) value)));
				}
				// LINEA
				else if (property.equals("codlinea") && value != null) {
					criteria.add(Restrictions.eq("codlinea", new BigDecimal((String) value)));
				}
				// CODENTIDAD
				else if (property.equals("codentidad") && value != null) {
					criteria.add(Restrictions.eq("codentidad", new BigDecimal((String) value)));
				}
				// TIPO DOCUMENTO
				else if (property.equals("docAgroseguroTipo.id") && value != null) {
					criteria.add(Restrictions.eq("tipo.id", Long.valueOf((String) value)));
				} else if (property.equals("docAgroseguroTipo.visible") && value != null) {
					criteria.add(Restrictions.eq("tipo.visible", (Integer) value));
				} else if (property.equals("docAgroseguroTipo.descripcion") && value != null) {
					criteria.add(Restrictions.like("tipo.descripcion", (String) value, MatchMode.EXACT));
				}
				// DESCRIPCION
				else if (property.equals("descripcion") && value != null) {
					if (this.descripccionExacta) {
						criteria.add(Restrictions.eq("descripcion", value.toString()));
					} else {
						criteria.add(Restrictions.ilike("descripcion", value.toString().trim(), MatchMode.ANYWHERE));
					}
				}

				/* Pet. 79014 ** MODIF TAM (28.03.2022) ** Inicio */
				/*
				 * FECHA VALIDEZ (Si el perfil del usuario es <> 0, se deben buscar los
				 * registros /*cuya fecha de Validez no esté informada o sea igual o superior a
				 * la fecha del día)
				 */
				else if (property.equals("fechavalidez") && value != null) {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					if (value.equals("perfil0")) {
						Date fec = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						String fecha = sdf.format(fec);
						Date fechaVal = df.parse(fecha);
						// Anade los anos a la condicion de busqueda
						criteria.add(Restrictions.disjunction().add(Restrictions.ge("fechavalidez", fechaVal))
								.add(Restrictions.isNull("fechavalidez")));
					} else {
						Date fechaVal = df.parse(value.toString());
						criteria.add(Restrictions.eq("fechavalidez", fechaVal));
					}

				}
				// Perfiles
				else if (property.equals("codDocAgroseguroPerfiles")) {
					/* Pet. 79014 ** MODIF TAM (28.04.2022) ** Defecto Nº 26 ** Inicio */
					/*
					 * Si para el filtro ha seleccionado el valor todos, es decir un vacío,
					 * independientemente de lo demás que haya seleccionado, se meten todos los
					 * valores
					 */

					if (value.toString().startsWith("[") && value.toString().endsWith("]")) {
						String val = value.toString().replace("[", "").replace("]", "").replace(" ", "");
						if (!StringUtils.isNullOrEmpty(val)) {
							criteria.add(
									Restrictions.in("docPerfil.perfil.id", StringUtils.asListBigDecimal(val, ",")));
						}
					} else {
						criteria.add(Restrictions.in("docPerfil.perfil.id",
								StringUtils.asListBigDecimal(value.toString(), ",")));
					}
				}
				/* Pet. 79014 ** MODIF TAM (28.03.2022) ** Fin */

			} catch (Exception e) {
				logger.error("DocsAgroseguroFilter - " + e.getMessage());
			}
		}
	}

	public String getSqlWhere(Usuario usuario) {
		logger.debug("Dentro de getsqlWhere - INIT");
		boolean filtroPerfil = false;
		String sqlWhere = "";

		/* Pet. 79014 ** MODIF TAM (27.04.2022) ** Defecto Nº13 ** Inicio */
		String perfil = "";
		perfil = usuario.getPerfil();
		BigDecimal codEntidad = usuario.getOficina().getEntidad().getCodentidad();

		logger.debug("**@@** Valor de perfil:" + perfil);
		logger.debug("**@@** Valor de codEntidad:" + codEntidad);
		/* Pet. 79014 ** MODIF TAM (27.04.2022) ** Defecto Nº13 ** Fin */

		/* comprobamos si se filtra por perfil */
		for (Filter filter : filters) {
			String property = filter.getProperty();

			if (property.equals("codDocAgroseguroPerfiles")) {
				String val = filter.getValue().toString().replace("[", "").replace("]", "").replace(" ", "");
				String[] tablasPerfil = val.split(",");
				if (!StringUtils.isNullOrEmpty(val)) {
					filtroPerfil = true;
				}
			}
		}

		if (filtroPerfil) {
			sqlWhere = ", o02agpe0.TB_DOC_AGROSEGURO_PERFIL DP ";
			sqlWhere += " WHERE D.ID_TIPO = T.ID ";
			sqlWhere += " AND D.ID = DP.ID_DOC_AGRO (+) ";
		} else {
			sqlWhere += " WHERE D.ID_TIPO = T.ID ";
		}

		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();
				// PLAN
				if (property.equals("codplan")) {
					sqlWhere += " AND CODPLAN = " + filter.getValue();
					continue;
				}
				// PLAN
				if (property.equals("codlinea")) {

					sqlWhere += " AND CODLINEA = " + filter.getValue();
					continue;
				}
				// PLAN
				if (property.equals("docAgroseguroTipo.id")) {

					sqlWhere += " AND D.ID_TIPO = " + filter.getValue();
					continue;
				}
				if (property.equals("docAgroseguroTipo.visible")) {

					sqlWhere += " AND T.VISIBLE = " + filter.getValue();
					continue;
				}
				// DESCRIPCION
				if (property.equals("descripcion")) {
					if (this.descripccionExacta) {
						sqlWhere += " AND D.DESCRIPCION = '" + filter.getValue() + "'";
						continue;
					} else {
						sqlWhere += " AND D.DESCRIPCION LIKE '%" + filter.getValue() + "%'";
						continue;
					}
				}

				// FECHA VALIDEZ
				if (property.equals("fechavalidez")) {
					if (!perfil.equals("AGR-0")) {
						Date fecha = new Date();
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						String fechaStr = df.format(fecha);
						sqlWhere += " AND (TO_DATE(FECHA_VALIDEZ) >= TO_DATE('" + fechaStr
								+ "', 'dd/MM/yyyy') OR FECHA_VALIDEZ is null)";
						continue;
					} else {
						sqlWhere += " AND TO_DATE(FECHA_VALIDEZ) = TO_DATE('" + filter.getValue() + "', 'dd/MM/yyyy')";
						continue;
					}
				}
				//
				if (property.equals("codentidad")) {
					sqlWhere += " AND CODENTIDAD = '" + filter.getValue() + "'";
					continue;
				}

				// Codigos de Perfil
				if ("codDocAgroseguroPerfiles".equals(property)) {
					if (perfil.equals("AGR-0")) {
						String val = filter.getValue().toString().replace("[", "").replace("]", "").replace(" ", "");
						if (!StringUtils.isNullOrEmpty(val)) {
							sqlWhere += " AND DP.IDPERFIL IN (" + val + ")";
							continue;
						}
					} else {
						String perf = "";
						if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
							perf = "1" + perfil.substring(4);
						} else {
							perf = perfil.substring(4);
						}

						sqlWhere += " AND DP.IDPERFIL IN (" + perf + ")";
						continue;
					}
				}

			}

			// CODENTIDAD
			if (!perfil.equals("AGR-0")) {
				sqlWhere += " AND (CODENTIDAD = " + codEntidad + " OR CODENTIDAD = 0 )";
			}

		} catch (Exception e) {
			logger.error("DocsAgroseguroFilter - Error al recuperar la lista de todos los ids -" + e.getMessage());
		}
		logger.debug("Valor de la sqlWhere:" + sqlWhere);
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

	public void setDescripccionExacta(final boolean descripccionExacta) {
		this.descripccionExacta = descripccionExacta;
	}
}
