package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.SiniestrosFilter;
import com.rsi.agp.core.jmesa.sort.SiniestrosSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.commons.Localidad;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.Via;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestrosAnteriores;
import com.rsi.agp.dao.tables.siniestro.SiniestrosUtilidades;

public class SiniestroDao extends BaseDaoHibernate implements ISiniestroDao {

	/*** SONAR Q ** MODIF TAM(26.11.2021) ***/
	/**
	 * - Se ha eliminado todo el código comentado - Se crean metodos nuevos para
	 * descargar de ifs/fors - Se crean constantes locales nuevas
	 **/

	private final Log logger = LogFactory.getLog(this.getClass());

	/** CONSTANTES SONAR Q ** MODIF TAM (26.11.2021) ** Inicio **/
	private final static String MSJ_ERROR = "Se ha producido un error durante el acceso a la base de datos";

	/** CONSTANTES SONAR Q ** MODIF TAM (26.11.2021) ** Fin **/

	@SuppressWarnings("unchecked")
	public List<Siniestro> list(Siniestro siniestro) throws DAOException {
		Session session = obtenerSession();
		try {

			Criteria criteria = session.createCriteria(Siniestro.class);

			if (siniestro.getPoliza() != null && siniestro.getPoliza().getIdpoliza() != null) {
				criteria.add(Restrictions.eq("poliza.idpoliza", siniestro.getPoliza().getIdpoliza()));
			}
			if (siniestro.getCodriesgo() != null) {
				criteria.add(Restrictions.eq("codriesgo", siniestro.getCodriesgo()));
			}
			if (siniestro.getFecfirmasiniestro() != null) {
				criteria.add(Restrictions.eq("fecfirmasiniestro", siniestro.getFecfirmasiniestro()));
			}
			if (siniestro.getComunicaciones() != null && siniestro.getComunicaciones().getFechaEnvio() != null) {
				// JANV 01/04/2016
				// modificamos para realizar la busqueda por fecha entre la seleccionada y +24
				// horas como en otros filtros
				GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
				fechaEnvioGrMas24.setTime(siniestro.getComunicaciones().getFechaEnvio());
				fechaEnvioGrMas24.add(java.util.Calendar.HOUR, 24);
				// JANV 01/04/2016
				// se ha añadido el campo a Siniestro desde vw_siniestros_utilidades
				Conjunction and = Restrictions.conjunction();
				and.add(Restrictions.ge("fechaEnvio", siniestro.getComunicaciones().getFechaEnvio()));
				and.add(Restrictions.lt("fechaEnvio", fechaEnvioGrMas24.getTime()));
				criteria.add(and);
			}
			if (siniestro.getFechaocurrencia() != null) {
				criteria.add(Restrictions.eq("fechaocurrencia", siniestro.getFechaocurrencia()));
			}
			if (siniestro.getEstadoSiniestro() != null && siniestro.getEstadoSiniestro().getIdestado() != null) {
				criteria.add(Restrictions.eq("estadoSiniestro.idestado", siniestro.getEstadoSiniestro().getIdestado()));
			}

			return criteria.list();

		} catch (Exception ex) {

			throw new DAOException(MSJ_ERROR, ex);

		}
	}

	@Override
	public Poliza getPoliza(Long idPoliza) throws DAOException {
		try {

			return (Poliza) get(Poliza.class, idPoliza);

		} catch (Exception e) {

			throw new DAOException(MSJ_ERROR, e);

		}

	}

	@Override
	public void eliminarSiniestro(Long idSiniestro) throws DAOException {

		try {

			delete(Siniestro.class, idSiniestro);

		} catch (Exception e) {
			throw new DAOException(MSJ_ERROR, e);
		}
	}

	/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
	@Override
	public void bajaSiniestro(Long idSiniestro) throws DAOException {

		logger.debug("SiniestroDao - bajaSiniestro [INIT] ");
		Session session = obtenerSession();
		try {
			String sqlUpdate = " update o02agpe0.tb_siniestros sin " + " set sin.fecha_baja  = SYSDATE "
					+ " where sin.id = " + idSiniestro;

			logger.debug("SiniestroDao - bajaSiniestro - sql: " + sqlUpdate);
			session.createSQLQuery(sqlUpdate).executeUpdate();
		} catch (Exception e) {
			logger.error("Se ha producido un error al realizar la baja lógica del Siniestro: " + idSiniestro, e);
		}
		logger.debug("SiniestroDao - bajaSiniestro [END] ");
		return;
	}

	@Override
	public Siniestro getSiniestro(Long idSiniestro) throws DAOException {
		try {

			return (Siniestro) get(Siniestro.class, idSiniestro);

		} catch (Exception e) {

			throw new DAOException(MSJ_ERROR, e);

		}
	}

	@Override
	public Siniestro guardarSiniestro(Siniestro siniestro, Usuario usuario, boolean facturacion) throws DAOException {
		try {
			// TMR 30-05-2012 Facturacion
			this.evict(siniestro);
			if (facturacion)
				return (Siniestro) saveOrUpdateFacturacion(siniestro, usuario);
			else {
				// indicando que el usuario es nulo no guarda registro de facturación.
				return (Siniestro) saveOrUpdateFacturacion(siniestro, null);
			}

		} catch (Exception e) {
			throw new DAOException(MSJ_ERROR, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EstadoSiniestro> getEstadosSiniestro() throws DAOException {
		try {

			return findAll(EstadoSiniestro.class);

		} catch (Exception e) {

			throw new DAOException(MSJ_ERROR, e);

		}
	}

	@Override
	public boolean tieneEstado(Long idSiniestro, Short estado) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Siniestro.class);

			criteria.add(Restrictions.eq("id", idSiniestro));
			criteria.add(Restrictions.eq("estadoSiniestro.idestado", estado));

			return (!criteria.list().isEmpty());

		} catch (Exception e) {
			throw new DAOException(MSJ_ERROR, e);
		}
	}

	public Map<String, Object> buscarDescripciones(Siniestro siniestro) throws DAOException {
		Session session = obtenerSession();
		Map<String, Object> resultado = new HashMap<String, Object>();
		try {
			Criteria criteria = session.createCriteria(Via.class);
			criteria.add(Restrictions.eq("clave", siniestro.getClavevia()));
			resultado.put("via", ((Via) criteria.uniqueResult()).getNombre());

			Criteria criteriaLoc = session.createCriteria(Localidad.class);

			if (siniestro.getCodprovincia() != null) {
				criteriaLoc.add(Restrictions.eq("provincia.codprovincia", siniestro.getCodprovincia()));
			}
			criteriaLoc.add(Restrictions.eq("id.codlocalidad", siniestro.getCodlocalidad()));
			criteriaLoc.add(Restrictions.eq("id.sublocalidad", siniestro.getSublocalidad()));
			resultado.put("localidad", ((Localidad) criteriaLoc.uniqueResult()).getNomlocalidad());
			resultado.put("provincia", ((Localidad) criteriaLoc.uniqueResult()).getProvincia().getNomprovincia());

			return resultado;

		} catch (Exception e) {
			throw new DAOException(MSJ_ERROR, e);

		}
	}

	@Override
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException {
		try {

			return (Comunicaciones) get(Comunicaciones.class, idEnvio);

		} catch (Exception e) {
			throw new DAOException(MSJ_ERROR, e);
		}
	}

	public int getSiniestrosCountWithFilter(final SiniestrosFilter filter) throws BusinessException {
		try {
			logger.debug("getSiniestrosCountWithFilter - Inicio");

			Session session = obtenerSession();

			String sql = filter.getSqlInnerJoin();
			sql += "WHERE 1=1 " + filter.getSqlWhere();
			logger.debug("getSinistrosCountWithFilter - sql = " + sql);
			return ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();
		}

		catch (Exception e) {
			logger.error("getSiniestrosCountWithFilter - Se ha producido un error durante el acceso a la base de datos",
					e);
			throw new BusinessException(MSJ_ERROR + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<SiniestrosUtilidades> getSiniestrosWithFilterAndSort(final SiniestrosFilter filter,
			final SiniestrosSort sort, final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger.debug("getSiniestrosWithFilterAndSort - Inicio");

			List<SiniestrosUtilidades> applications = (List<SiniestrosUtilidades>) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(SiniestrosUtilidades.class);
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							if (rowStart != -1 && rowEnd != -1) {
						        // Primer registro
						        criteria.setFirstResult(rowStart);
						        // Número máximo de registros a mostrar
						        criteria.setMaxResults(rowEnd - rowStart);
						    }
							// Devuelve el listado de siniestros
							return criteria.list();
						}
					});

			return applications;

		}

		catch (Exception e) {
			logger.error(
					"getSiniestrosWithFilterAndSort - Se ha producido un error durante el acceso a la base de datos",
					e);
			throw new BusinessException(MSJ_ERROR + e.getMessage());
		}
	}

	/**
	 * DAA 09/01/2013 Obtiene la descripcion del riesgo del siniestro enviado
	 * correcto anterior al siniestro seleccionado
	 * 
	 * @param idSiniestro
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public String getRiesgoSiniestroAnterior(Integer idSiniestro) throws Exception {
		try {
			Session session = obtenerSession();
			String sql = "select r.desriesgo from (o02agpe0.tb_siniestros s"
					+ " inner join o02agpe0.tb_polizas p on p.idpoliza = s.idpoliza"
					+ " inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid"
					+ " inner join o02agpe0.tb_sc_c_lineas lc on lc.codlinea = l.codlinea"
					+ " inner join o02agpe0.tb_sc_c_riesgos r on s.codriesgo = r.codriesgo and lc.codgruposeguro = r.codgruposeguro)"
					+ " where s.idpoliza in (select s1.idpoliza from o02agpe0.tb_siniestros s1 where id = " + idSiniestro + ")"
					+ " and s.fechaocurrencia < (select s2.fechaocurrencia from tb_siniestros s2 where id = " + idSiniestro + ")" 
					+ " and s.estado = 3";
			List list = session.createSQLQuery(sql).list();
			if (list.size() > 0)
				return (String) list.get(0);
			else
				return "";
		} catch (Exception e) {
			logger.error("getRiesgoSiniestroAnterior - Se ha producido un error durante el acceso a la base de datos",
					e);
			throw new Exception(
					"Se ha producido un error al obtener el riesgo del siniestro anterior - " + e.getMessage());

		}

	}
	
	/**
	 * Obtiene la descripcion de los siniestros enviados
	 * correcto anterior a la fecha de emisión de póliza
	 * 
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked" })
	public List<SiniestrosAnteriores> getRiesgoSiniestrosAnteriores(Integer idSiniestro) throws Exception {
		try {
			List<SiniestrosAnteriores>  returnList = new ArrayList<SiniestrosAnteriores>();
			Session session = obtenerSession();
			String sql = "select r.codriesgo, r.desriesgo,s.fechaocurrencia from (o02agpe0.tb_siniestros s"
					+ " inner join o02agpe0.tb_polizas p on p.idpoliza = s.idpoliza"
					+ " inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid"
					+ " inner join o02agpe0.tb_sc_c_lineas lc on lc.codlinea = l.codlinea"
					+ " inner join o02agpe0.tb_sc_c_riesgos r on s.codriesgo = r.codriesgo and lc.codgruposeguro = r.codgruposeguro)"
					+ " where s.idpoliza in (select s1.idpoliza from o02agpe0.tb_siniestros s1 where id = " + idSiniestro + ")"
					+ " and s.fechaocurrencia < (select s2.fechaocurrencia from tb_siniestros s2 where id = " + idSiniestro + ")" 
					+ " and s.estado = 3"
					+ " order by s.fechaocurrencia";
			
			List<Object[]> siniestros = session.createSQLQuery(sql).list();
			SimpleDateFormat sdfToFormat = new SimpleDateFormat("dd/MM/yyyy");
			if (siniestros != null) {
				for(Object[] siniestro : siniestros){
					SiniestrosAnteriores siniestrosAnteriores = new SiniestrosAnteriores(siniestro[0].toString(), siniestro[1].toString() , sdfToFormat.format(siniestro[2]));
					returnList.add(siniestrosAnteriores);
				}

				
			}
			return returnList;
		} catch (Exception e) {
			logger.error("getRiesgoSiniestroAnterior - Se ha producido un error durante el acceso a la base de datos",
					e);
			throw new Exception(
					"Se ha producido un error al obtener el riesgo del siniestro anterior - " + e.getMessage());

		}

	}

	@Override
	public boolean isSiniestroConParcelas(Long idSineistro) throws DAOException {
		Session session = obtenerSession();
		try {

			Criteria criteria = session.createCriteria(ParcelaSiniestro.class);

			criteria.add(Restrictions.eq("siniestro.id", idSineistro));
			if (criteria.list().size() > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("error al recuperar las parcelas del siniestro.- isSiniestroConParcelas()");
			throw new DAOException();
		}

	}

	/**
	 * Contamos el número total de siniestros.
	 * 
	 * @author U029114 21/06/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getNumTotalSiniestros(Long idPoliza) throws DAOException {

		final Session sesion = obtenerSession();

		try {
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append("select count(*) from tb_siniestros sini where 1=1 ");
			stringQuery.append(" and sini.idpoliza = " + idPoliza);

			BigDecimal totalSiniestros = (BigDecimal) sesion.createSQLQuery(stringQuery.toString()).uniqueResult();
			return totalSiniestros;

		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException(MSJ_ERROR, ex);
		}
	}

	/**
	 * Obtenemos un nuevo numsiniestro del siniestro.
	 * 
	 * @author U029114 27/06/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getNuevoNumSiniestro(Long idPoliza) throws DAOException {

		final Session sesion = obtenerSession();

		try {
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append("select max(TO_NUMBER(sini.numsiniestro)) + 1 from tb_siniestros sini where 1=1 ");
			stringQuery.append(" and sini.idpoliza = " + idPoliza);

			BigDecimal nuevoNumSiniestro = (BigDecimal) sesion.createSQLQuery(stringQuery.toString()).uniqueResult();
			return nuevoNumSiniestro;

		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException(MSJ_ERROR, ex);
		}
	}

}
