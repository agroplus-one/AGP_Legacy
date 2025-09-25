package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.DatosRCFilter;
import com.rsi.agp.core.jmesa.sort.DatosRCSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.rc.DatosRC;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;
import com.rsi.agp.dao.tables.rc.SumaAseguradaRC;

public class DatosRCDao extends BaseDaoHibernate implements IDatosRCDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<EspeciesRC> getEspeciesRC() throws DAOException {

		try {

			logger.debug("init - [DatosRCDao] getEspeciesRC");

			return this.findAll(EspeciesRC.class);

		} catch (Exception e) {

			logger.error("Error: getEspeciesRC : " + e);
			throw new DAOException(
					"getEspeciesRC : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<RegimenRC> getRegimenesRC() throws DAOException {

		try {

			logger.debug("init - [DatosRCDao] getRegimenesRC");

			return this.findAll(RegimenRC.class);

		} catch (Exception e) {

			logger.error("Error: getRegimenesRC : " + e);
			throw new DAOException(
					"getEspeciesRC : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<SumaAseguradaRC> getSumasAseguradasRC()
			throws DAOException {

		try {

			logger.debug("init - [DatosRCDao] getSumasAseguradasRC");

			return this.findAll(SumaAseguradaRC.class);

		} catch (Exception e) {

			logger.error("Error: getSumasAseguradasRC : " + e);
			throw new DAOException(
					"getEspeciesRC : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public String getlistaIdsTodos(final DatosRCFilter consultaFilter)
			throws DAOException {

		String listaids = "";

		try {

			logger.debug("init - [DatosRCDao] getlistaIdsTodos");

			Session session = obtenerSession();

			String sql = "SELECT DRC.ID FROM TB_RC_DATOS DRC, TB_LINEAS L, TB_SUBENTIDADES_MEDIADORAS E "
					+ consultaFilter.getSqlWhere();

			@SuppressWarnings("unchecked")
			List<BigDecimal> lista = session.createSQLQuery(sql).list();

			for (int i = 0; i < lista.size(); i++) {
				listaids += lista.get(i).toString() + ",";
			}

			return listaids;

		} catch (Exception e) {

			logger.error("Error: getlistaIdsTodos : " + e);
			throw new DAOException(
					"getlistaIdsTodos : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public int getDatosRCCountWithFilter(final DatosRCFilter filter)
			throws DAOException {

		logger.debug("init - [DatosRCDao] getDatosRCCountWithFilter");

		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Criteria criteria = session
								.createCriteria(DatosRC.class);
						criteria.createAlias("linea", "lineaseguro");
						criteria.createAlias("especiesRC", "especiesRC");
						criteria.createAlias("regimenRC", "regimenRC");
						criteria = filter.execute(criteria);

						return criteria.setProjection(Projections.rowCount())
								.uniqueResult();
					}
				});

		logger.debug("end - [DatosRCDao] getDatosRCCountWithFilter");

		return count.intValue();
	}

	@Override
	public Collection<DatosRC> getDatosRCWithFilterAndSort(
			final DatosRCFilter filter, final DatosRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException {

		try {

			logger.debug("init - [DatosRCDao] getDatosRCWithFilterAndSort");

			@SuppressWarnings("unchecked")
			List<DatosRC> datos = (List<DatosRC>) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(DatosRC.class);
							criteria.createAlias("linea", "lineaseguro");
							criteria.createAlias("especiesRC", "especiesRC");
							criteria.createAlias("regimenRC", "regimenRC");
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<DatosRC> lista = criteria.list();
							return lista;
						}
					});

			logger.debug("end - [DatosRCDao] getDatosRCWithFilterAndSort");

			return datos;

		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public void replicaDatosRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws DAOException {

		try {
			// Procedimiento de réplica
			String procedimiento = "PQ_REPLICAR.replicarDatosRC (CODPLAN_ORIGEN IN NUMBER, CODLINEA_ORIGEN IN NUMBER, CODPLAN_DESTINO IN NUMBER, CODLINEA_DESTINO IN NUMBER)";

			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros
																			// PL
			parametros.put("CODPLAN_ORIGEN", planOrig);
			parametros.put("CODLINEA_ORIGEN", lineaOrig);
			parametros.put("CODPLAN_DESTINO", planDest);
			parametros.put("CODLINEA_DESTINO", lineaDest);

			this.databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos
																				// PL

		} catch (Exception e) {

			logger.error("Error al replicar datos para RC", e);
			throw new DAOException("Error al replicar datos para RC", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<DatosRC> getDatosRC(final BigDecimal plan,
			final BigDecimal linea, final BigDecimal codentidad,
			final BigDecimal codsubentidad, final String codespecieRC,
			final BigDecimal codregimenRC) throws DAOException {

		Criteria criteria;
		List<DatosRC> datos = null;
		
		try {
			
			criteria = this.getSession().createCriteria(DatosRC.class);
			criteria.createAlias("linea", "lineaseguro");
			criteria.createAlias("especiesRC", "especiesRC");
			criteria.createAlias("regimenRC", "regimenRC");

			// BUSCAMOS DATOS ESPECIFICOS PARA LA E-S MED
			criteria.add(Restrictions.eq("lineaseguro.codplan",
					plan));
			criteria.add(Restrictions.eq(
					"lineaseguro.codlinea", linea));
			criteria.add(Restrictions.eq(
					"subentidadMediadora.id.codentidad",
					codentidad));
			criteria.add(Restrictions.eq(
					"subentidadMediadora.id.codsubentidad",
					codsubentidad));
			criteria.add(Restrictions.eq(
					"especiesRC.codespecie", codespecieRC));
			criteria.add(Restrictions.eq(
					"regimenRC.codregimen", codregimenRC));

			datos = criteria.list();
			
			// BUSCAMOS SIN E-S MED
			if (datos == null || (datos != null && datos.isEmpty())) {
				
				criteria = this.getSession().createCriteria(DatosRC.class);
				criteria.createAlias("linea", "lineaseguro");
				criteria.createAlias("especiesRC", "especiesRC");
				criteria.createAlias("regimenRC", "regimenRC");

				criteria.add(Restrictions.eq("lineaseguro.codplan",
						plan));
				criteria.add(Restrictions.eq(
						"lineaseguro.codlinea", linea));
				criteria.add(Restrictions
						.isNull("subentidadMediadora.id.codentidad"));
				criteria.add(Restrictions
						.isNull("subentidadMediadora.id.codsubentidad"));
				criteria.add(Restrictions.eq(
						"especiesRC.codespecie", codespecieRC));
				criteria.add(Restrictions.eq(
						"regimenRC.codregimen", codregimenRC));

				datos = criteria.list();
			}

			return datos;

		} catch (Exception e) {

			logger.error("Error al obtener datos para RC", e);
			throw new DAOException("Error al obtener datos para RC", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean existeDatosRC(final DatosRC datosRC) throws DAOException {
		
		Criteria criteria;
		List<DatosRC> datos = null;
		
		boolean existeDatosRC = Boolean.FALSE;
		boolean esAlta = datosRC.getId() == null;
		
		try {

			// BUSCAMOS POR PLAN/LINEA/ESPECIE/REGIMEN/SUMA ASEGURADA
			criteria = this.getSession().createCriteria(DatosRC.class);
			criteria.createAlias("linea", "lineaseguro");
			criteria.createAlias("especiesRC", "especiesRC");
			criteria.createAlias("regimenRC", "regimenRC");
			criteria.createAlias("sumaAseguradaRC", "sumaAseguradaRC");
			
			criteria.add(Restrictions.eq("lineaseguro.codplan", datosRC.getLinea().getCodplan()));
			criteria.add(Restrictions.eq("lineaseguro.codlinea", datosRC.getLinea().getCodlinea()));
			
			criteria.add(Restrictions.eq("especiesRC.codespecie", datosRC.getEspeciesRC().getCodespecie()));
			criteria.add(Restrictions.eq("regimenRC.codregimen", datosRC.getRegimenRC().getCodregimen()));
			criteria.add(Restrictions.eq("sumaAseguradaRC.codsuma", datosRC.getSumaAseguradaRC().getCodsuma()));
			
			datos = criteria.list();
			
			// SI HAY DATOS DEBEREMOS REVISAR LA E-S MEDIADORA
			if (datos != null && !datos.isEmpty()) {
				
				for (DatosRC dato : datos) {
					
					// SI ES ALTA O NO ES EL MISMO REGISTRO QUE ESTAMOS MODIFICANDO
					if (esAlta || !datosRC.getId().equals(dato.getId())) {
						if (datosRC.getSubentidadMediadora().getId()
								.getCodentidad() == null
								&& datosRC.getSubentidadMediadora().getId()
										.getCodsubentidad() == null
								&& dato.getSubentidadMediadora() == null) {
	
							existeDatosRC = Boolean.TRUE;
							break;
	
						} else if (datosRC.getSubentidadMediadora().getId()
								.getCodentidad() != null
								&& datosRC.getSubentidadMediadora().getId()
										.getCodsubentidad() != null
								&& dato.getSubentidadMediadora() != null) {
	
							if (datosRC
									.getSubentidadMediadora()
									.getId()
									.getCodentidad()
									.equals(dato.getSubentidadMediadora().getId()
											.getCodentidad())
									&& datosRC
											.getSubentidadMediadora()
											.getId()
											.getCodsubentidad()
											.equals(dato.getSubentidadMediadora()
													.getId().getCodsubentidad())) {
	
								existeDatosRC = Boolean.TRUE;
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
	
			logger.error("Error al revisar duplicidad de datos RC", e);
			throw new DAOException("Error al revisar duplicidad de datos RC", e);
		}
		return existeDatosRC;
	}
}