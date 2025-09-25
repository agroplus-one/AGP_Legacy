package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.utilidades.AnexoModificacionUtilidadesService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Entidad;

public class AnexoModificacionFilter implements CriteriaCommand {

	// Constantes
	private static final String BUILD_CRITERIA = "buildCriteria";
	private static final String LIN_CODPLAN = "lin.codplan";
	
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log logger = LogFactory.getLog(getClass());
	private boolean filtroPlan = false;

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}

		// Si no se ha filtrado por plan, se introduce el filtro por defecto para este
		// campo
		if (!filtroPlan)
			buildCondicionPlan(criteria);

		return criteria;
	}

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	/**
	 * Anade el filtro por defecto para este campo (ano actual y ano pasado)
	 * 
	 * @param criteria
	 */
	private void buildCondicionPlan(Criteria criteria) {

		// Obtiene el ano actual y el pasado
		GregorianCalendar gc = new GregorianCalendar();
		BigDecimal anyoActual = new BigDecimal(gc.get(Calendar.YEAR));
		gc.add(Calendar.YEAR, -1);
		BigDecimal anyoPasado = new BigDecimal(gc.get(Calendar.YEAR));

		// Anade los anos a la condicion de busqueda
		criteria.add(Restrictions.disjunction().add(Restrictions.eq(LIN_CODPLAN, anyoActual))
				.add(Restrictions.eq(LIN_CODPLAN, anyoPasado)));

		log("buildCondicionPlan", "Anadidos el ano actual y el pasado al filtro de plan");
	}

	/**
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al
	 * filtro indicado por 'property,valor'
	 * 
	 * @param criteria
	 * @param property
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void buildCriteria(Criteria criteria, String property, Object value) {

		// Para comprobar que el valor tiene formato de fecha
		Pattern datePattern = Pattern
				.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
		Matcher dateMatcher = datePattern.matcher(value + "");

		if (value != null) {
			try {
				if (dateMatcher.find()) {
					// La propiedad es de tipo fecha
					GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)),
							Integer.parseInt(dateMatcher.group(2)) - 1, Integer.parseInt(dateMatcher.group(1)));
					Date fechaMas24 = new Date();
					GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
					fechaEnvioGrMas24.setTime(gc.getTime());
					fechaEnvioGrMas24.add(Calendar.HOUR, 24);
					fechaMas24 = fechaEnvioGrMas24.getTime();
					criteria.add(Restrictions.ge(property, gc.getTime()));
					criteria.add(Restrictions.lt(property, fechaMas24));
				} else {
					// Entidad
					if (AnexoModificacionUtilidadesService.CAMPO_ENTIDAD.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'entidad = " + value.toString() + "'");
						criteria.add(Restrictions.eq("tom.id.codentidad", new BigDecimal(value.toString())));
					}
					// Oficina
					else if (AnexoModificacionUtilidadesService.CAMPO_OFICINA.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'oficina = "
								+ CriteriaUtils.getCodigosOficina(value.toString()) + "'");
						criteria.add(Restrictions.in("pol.oficina", CriteriaUtils.getCodigosOficina(value.toString())));
					}
					// Usuario
					else if (AnexoModificacionUtilidadesService.CAMPO_USUARIO.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'usuario = " + value.toString() + "'");
						criteria.add(Restrictions.eq("pol.usuario.codusuario", value.toString()));
					}
					// Plan
					else if (AnexoModificacionUtilidadesService.CAMPO_PLAN.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'plan = " + value.toString() + "'");
						filtroPlan = true;
						criteria.add(Restrictions.eq(LIN_CODPLAN, new BigDecimal(value.toString())));
					}
					// Linea
					else if (AnexoModificacionUtilidadesService.CAMPO_LINEA.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'linea = " + value.toString() + "'");
						criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
					}
					// Poliza
					else if (AnexoModificacionUtilidadesService.CAMPO_POLIZA.equals(property)) {

						// Si la referencia de poliza tiene guion, se parte la cadena para buscar por
						// referencia y dc
						if (value.toString().indexOf("-") > -1) {
							String ref = value.toString().split("-")[0];
							String dc = value.toString().split("-")[1];
							// Establece los valores en el bean.
							log(BUILD_CRITERIA, "Anade el filtro 'poliza = " + ref + "'");
							criteria.add(Restrictions.eq("pol.referencia", value.toString()));
							log(BUILD_CRITERIA, "Anade el filtro 'dc = " + dc + "'");
							criteria.add(Restrictions.eq("pol.dc", new BigDecimal(dc)));
						}
						// Si no, solo se busca por el campo referencia
						else {
							log(BUILD_CRITERIA, "Anade el filtro 'poliza = " + value.toString() + "'");
							criteria.add(Restrictions.eq("pol.referencia", value.toString()));
						}
					}
					// Dc
					else if (AnexoModificacionUtilidadesService.CAMPO_DC.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'dc = " + value.toString() + "'");
						criteria.add(Restrictions.eq(AnexoModificacionUtilidadesService.CAMPO_DC, value.toString()));
					}
					// Tipo
					else if (AnexoModificacionUtilidadesService.CAMPO_TIPOREF.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'tipo ref. = " + value.toString() + "'");
						criteria.add(Restrictions.eq("pol.tipoReferencia", value.toString()));
					}
					// CIF/NIF
					else if (AnexoModificacionUtilidadesService.CAMPO_NIF.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'nif = " + value.toString() + "'");
						criteria.add(Restrictions.eq("aseg.nifcif", value.toString()));
					}
					// Asegurado
					else if (AnexoModificacionUtilidadesService.CAMPO_FULLNAME.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'asegurado = " + value.toString() + "'");
						criteria.add(Restrictions.like("aseg.fullName", "%" + value.toString().toUpperCase() + "%"));
					}
					// Estado
					else if (AnexoModificacionUtilidadesService.CAMPO_IDESTADO.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'idestado = " + value.toString() + "'");
						criteria.add(Restrictions.eq("estado.idestado", new BigDecimal(value.toString())));
					}
					if (AnexoModificacionUtilidadesService.CAMPO_LISTADOGRUPOOFI.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'listaGrupoOficinas = " + value.toString() + "'");
						criteria.add(Restrictions.in("pol.oficina",
								CriteriaUtils.getCodigosListaOficina((List<BigDecimal>) value)));
					}
					// Listado de grupo de entidades
					if (AnexoModificacionUtilidadesService.CAMPO_LISTADOGRUPOENT.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'listaGrupoEntidades = " + value.toString() + "'");
						criteria.add(Restrictions.in("tom.id.codentidad", (List<BigDecimal>) value));
					}
					// Tipo de envio
					if (AnexoModificacionUtilidadesService.CAMPO_TIPO_AM.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'tipoEnvio = " + value.toString() + "'");
						// Se comprueba si se filtra por FTP o por id de cupon
						if (Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(value.toString())) {
							criteria.add(Restrictions.eq(AnexoModificacionUtilidadesService.CAMPO_TIPO_AM,
									Constants.ANEXO_MODIF_TIPO_ENVIO_FTP));
						} else {
							criteria.add(Restrictions.eq(AnexoModificacionUtilidadesService.CAMPO_TIPO_AM,
									Constants.ANEXO_MODIF_TIPO_ENVIO_SW));
						}
					}
					// Tipo de envio
					if (AnexoModificacionUtilidadesService.CAMPO_IDCUPON.equals(property)) {
						criteria.add(
								Restrictions.eq(AnexoModificacionUtilidadesService.CAMPO_IDCUPON, value.toString()));
					}
					// Estado del cupon
					if (AnexoModificacionUtilidadesService.CAMPO_ESTADO_CUPON.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'estadoCupon = " + value.toString() + "'");
						criteria.add(Restrictions.eq(AnexoModificacionUtilidadesService.CAMPO_ESTADO_CUPON,
								new Long((String) value)));
					}
					// Delegacion
					if (AnexoModificacionUtilidadesService.CAMPO_DELEGACION.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'delegacion = " + value.toString() + "'");
						criteria.add(Restrictions.eq("usu.delegacion", new BigDecimal((String) value)));
					}
					// ent mediadora
					if (AnexoModificacionUtilidadesService.CAMPO_ENTMEDIADORA.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'entidad mediadora = " + value.toString() + "'");
						criteria.add(Restrictions.eq("subent.id.codentidad", new BigDecimal((String) value)));
					}
					// subent mediadora
					if (AnexoModificacionUtilidadesService.CAMPO_SUBENTMEDIADORA.equals(property)) {
						log(BUILD_CRITERIA, "Anade el filtro 'Subentidad Mediadora = " + value.toString() + "'");
						criteria.add(Restrictions.eq("subent.id.codsubentidad", new BigDecimal((String) value)));
					}
				}
			} catch (Exception e) {
				log(BUILD_CRITERIA, "Ocurrio un error al anadir el filtro", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String getSqlWhere() {
		String sqlWhere = " WHERE 1 = 1";
		for (Filter filter : filters) {
			if (filter.getValue() != null) {
				try {
					// entidad
					if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_ENTIDAD)) {
						sqlWhere += " AND t.codentidad = '" + filter.getValue() + "'";
					}
					// oficina
					if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_OFICINA)) {
						sqlWhere += " AND p.oficina in " + StringUtils.toValoresSeparadosXComas(
								CriteriaUtils.getCodigosOficina(filter.getValue().toString()), false, true);
					} else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_LISTADOGRUPOOFI)) {
						sqlWhere += " AND p.oficina IN " + StringUtils.toValoresSeparadosXComas(
								CriteriaUtils.getCodigosListaOficina((List<BigDecimal>) filter.getValue()), false,
								true);
					}
					// usuario
					if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_USUARIO)) {
						sqlWhere += " AND p.codusuario = '" + filter.getValue() + "'";
					}
					if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_DELEGACION)) {
						sqlWhere += " AND usu.delegacion = '" + filter.getValue() + "'";
					}
					// plan
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_PLAN)) {
						sqlWhere += " AND l.codplan = '" + filter.getValue() + "'";
						filtroPlan = true;
					}
					// linea
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_LINEA)) {
						sqlWhere += " AND l.codlinea = '" + filter.getValue() + "'";
					}
					// poliza
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_POLIZA)) {
						// Si la referencia de poliza tiene guion, se parte la cadena para buscar por
						// referencia y dc
						if (filter.getValue().toString().indexOf("-") > -1) {
							String ref = filter.getValue().toString().split("-")[0];
							String dc = filter.getValue().toString().split("-")[1];
							// Establece los valores en el bean.
							sqlWhere += "  AND p.referencia ='" + ref + "'";
							sqlWhere += "  AND p.dc =" + dc;
						}
						// Si no, solo se busca por el campo referencia
						else {
							sqlWhere += "  AND p.referencia ='" + filter.getValue() + "'";
						}
					}
					// tipo ref
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_TIPOREF)) {
						sqlWhere += " AND p.tiporef = '" + filter.getValue() + "'";
					}
					// nif
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_NIF)) {
						sqlWhere += " AND aseg.nifcif = '" + filter.getValue() + "'";
					}
					// idestado
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_IDESTADO)) {
						sqlWhere += " AND ams.idestado= " + filter.getValue();
					}
					// fecha envio del anexo
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_FEC_ENVIO_ANEXO)) {
						// sqlWhere += " and to_char(am.fecha_envio, 'DD/MM/YYYY') =
						// '"+filter.getValue()+ "'";
						sqlWhere += " AND ((am.TIPO_ENVIO = 'FTP' AND to_char(comusin.fecha_envio, 'DD/MM/YYYY') = '"
								+ filter.getValue() + "') "
								+ "OR (am.TIPO_ENVIO = 'SW' AND to_char(sw.FECHA, 'DD/MM/YYYY') = '" + filter.getValue()
								+ "'))";
					}
					// nombre
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_FULLNAME)) {
						sqlWhere += " AND (upper(trim(aseg.nombre) || ' ' || trim(aseg.apellido1) || ' ' || trim(aseg.apellido2)) like ('%"
								+ filter.getValue().toString().toUpperCase() + "%')"
								+ " OR upper(trim(aseg.razonsocial)) like ('%"
								+ filter.getValue().toString().toUpperCase() + "%'))";
					}
					// grupo de entidades
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_LISTADOGRUPOENT)) {
						sqlWhere += " AND t.codentidad IN "
								+ StringUtils.toValoresSeparadosXComas((List<Entidad>) filter.getValue(), false);
					}
					// Tipo de envio
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_TIPO_AM)) {
						// Se comprueba si se filtra por FTP o por id de cupon
						if (Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(filter.getValue().toString())) {
							sqlWhere += " AND am.TIPO_ENVIO = '" + Constants.ANEXO_MODIF_TIPO_ENVIO_FTP + "'";
						} else {
							sqlWhere += " AND am.TIPO_ENVIO = '" + Constants.ANEXO_MODIF_TIPO_ENVIO_SW + "'";
						}

					} else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_IDCUPON)) {
						sqlWhere += " AND cup.IDCUPON = '" + filter.getValue() + "'";
					}
					// Estado del cupon
					else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_ESTADO_CUPON)) {
						sqlWhere += " AND cup.ESTADO = " + filter.getValue().toString();
					} else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_ENTMEDIADORA)) {
						sqlWhere += " AND c.ENTMEDIADORA = " + filter.getValue().toString();
					} else if (filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_SUBENTMEDIADORA)) {
						sqlWhere += " AND c.SUBENTMEDIADORA = " + filter.getValue().toString();
					}

				} catch (Exception e) {
					logger.error("getSqlWhere - " + e.getMessage());
				}
			}
		}

		/* Pet. 62719 ** MODIF TAM (22.01.2021) ** Inicio */
		/*
		 * Incluimos validación para no recuperar aquellos Asegurados que estén
		 * bloqueados
		 */
		sqlWhere += " AND (aseg.id NOT IN (SELECT BLOQA.ID_ASEGURADO FROM o02agpe0.TB_BLOQUEOS_ASEGURADOS BLOQA WHERE BLOQA.IDESTADO_ASEG = 'B'))";
		/* Pet. 62719 ** MODIF TAM (22.01.2021) ** Fin */

		// Si no se ha filtrado por plan, se introduce el filtro por defecto para este
		// campo
		if (!filtroPlan) {
			// Obtiene el ano actual y el pasado
			GregorianCalendar gc = new GregorianCalendar();
			BigDecimal anyoActual = new BigDecimal(gc.get(Calendar.YEAR));
			gc.add(Calendar.YEAR, -1);
			BigDecimal anyoPasado = new BigDecimal(gc.get(Calendar.YEAR));
			// Anade los anos a la condicion de busqueda
			sqlWhere += " and (l.codplan='" + anyoActual + "' or l.codplan='" + anyoPasado + "')";
		}

		log("buildCondicionPlan", "Anadidos el ano actual y el pasado al filtro de plan");
		return sqlWhere;
	}

	public String getSqlInnerJoin() {
		String sqlInnerJoin = "select count (*) from TB_ANEXO_MOD am ";
		boolean col = false;
		boolean est = false;
		boolean com = false;
		boolean usu = false;
		boolean cup = false;
		boolean auxCol = false;

		sqlInnerJoin += " inner join TB_POLIZAS p on p.IDPOLIZA = am.IDPOLIZA";
		// el de lineas lo anadimos porque siempre filtra por ano
		sqlInnerJoin += " inner join TB_LINEAS l on p.LINEASEGUROID = l.LINEASEGUROID ";
		/* Pet. 62719 ** MODIF TAM (22.01.2021) ** Inicio */
		/*
		 * Independientemente de que se informe el filtro de asegurado o no hacemos fijo
		 * el join con la tabla de Asegurados para no sacar los anexos de los asegurados
		 * que estén bloqueados.
		 */
		sqlInnerJoin += " inner join Tb_ASEGURADOS aseg on p.IDASEGURADO = aseg.ID ";
		/* } */

		for (Filter filter : filters) {
			if (!col && (filter.getProperty().contains("poliza.colectivo.tomador.id.codentidad")
					|| filter.getProperty().equals(AnexoModificacionUtilidadesService.CAMPO_LISTADOGRUPOENT))) {
				if (!auxCol)
					sqlInnerJoin += " inner join TB_COLECTIVOS c on p.IDCOLECTIVO = c.ID";

				sqlInnerJoin += " inner join TB_TOMADORES t on (t.CIFTOMADOR = c.CIFTOMADOR and t.CODENTIDAD = c.CODENTIDAD)";
				col = true;
			}
			if (!col && filter.getProperty().contains("subentidadMediadora.")) {
				sqlInnerJoin += " inner join TB_COLECTIVOS c on p.IDCOLECTIVO = c.ID";
				auxCol = true;
			}

			if (!est && filter.getProperty().contains("estado.")) {
				sqlInnerJoin += " inner join Tb_ANEXO_MOD_ESTADOS ams on am.estado = ams.idestado ";
				est = true;
			}
			if (!com && filter.getProperty().contains("fechaEnvioAnexo")) {
				sqlInnerJoin += " left outer join TB_COMUNICACIONES comusin on comusin.idenvio = am.idenvio "
						+ " left outer join TB_ANEXO_MOD_SW_ENVIOS_CONF sw on sw.idanexo = am.id ";
				com = true;
			}
			if (!usu && filter.getProperty().contains("usuario.")) {
				sqlInnerJoin += " inner join TB_USUARIOS usu on usu.codusuario = p.codusuario ";
				usu = true;
			}
			if (!cup && (filter.getProperty().contains(AnexoModificacionUtilidadesService.CAMPO_ESTADO_CUPON)
					|| (filter.getProperty().contains(AnexoModificacionUtilidadesService.CAMPO_TIPO_AM)
							&& !filter.value.equals(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP)))) {
				sqlInnerJoin += " inner join TB_ANEXO_MOD_CUPON cup on cup.id = am.idcupon ";
				cup = true;
			}

		}

		return sqlInnerJoin;
	}

	/**
	 * Escribe en el log indicando la clase y el mÃ©todo.
	 * 
	 * @param method
	 * @param msg
	 */
	private void log(String method, String msg) {
		logger.debug("AnexoModificacionFilter." + method + " - " + msg);
	}

	/**
	 * Escribe en el log indicando la clase, el mÃ©todo y la excepcion.
	 * 
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log(String method, String msg, Throwable e) {
		logger.error("AnexoModificacionFilter." + method + " - " + msg, e);
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
