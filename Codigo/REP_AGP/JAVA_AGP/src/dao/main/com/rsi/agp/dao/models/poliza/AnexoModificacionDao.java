package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.AnexoModificacionFilter;
import com.rsi.agp.core.jmesa.sort.AnexoModificacionSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.AnexoModificacionItem;
import com.rsi.agp.dao.tables.anexo.EnviosSWConfirmacion;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class AnexoModificacionDao extends BaseDaoHibernate implements IAnexoModificacionDao {
	
	/*** SONAR Q ** MODIF TAM(16.12.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/

	private final Log logger = LogFactory.getLog(this.getClass());
	
	/** CONSTANTES SONAR Q ** MODIF TAM (16.12.2021) ** Inicio **/
	private static final String ERROR = "Se ha producido un error durante el acceso a la base de datos";
	private static final String CUPON = "cupon";
	/** CONSTANTES SONAR Q ** MODIF TAM (16.12.2021) ** Fin **/	

	public int getAnexoModificacionCountWithFilter(final AnexoModificacionFilter filter) throws BusinessException {
		try {
			log("getAnexoModificacionCountWithFilter", "Inicio");
			Session session = obtenerSession();
			String sql = filter.getSqlInnerJoin();
			sql += filter.getSqlWhere();

			return ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();

		} catch (Exception e) {
			log("getAnexoModificacionCountWithFilter", ERROR,
					e);
			throw new BusinessException(
					ERROR + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<AnexoModificacion> getAnexoModificacionWithFilterAndSort(final AnexoModificacionFilter filter,
			final AnexoModificacionSort sort, final int rowStart, final int rowEnd) throws BusinessException {
		try {
			log("getAnexoModificacionWithFilterAndSort", "Inicio");

			List<AnexoModificacion> applications = (List<AnexoModificacion>) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {

							Criteria criteria = session.createCriteria(AnexoModificacion.class);
							// alias
							criteria.createAlias("poliza", "pol");
							criteria.createAlias("pol.linea", "lin");
							criteria.createAlias("pol.asegurado", "aseg");
							criteria.createAlias("pol.colectivo", "col");
							criteria.createAlias("pol.usuario", "usu");
							criteria.createAlias("col.tomador", "tom");
							criteria.createAlias("col.subentidadMediadora", "subent");
							criteria.createAlias(CUPON, CUPON, CriteriaSpecification.LEFT_JOIN);
							criteria.createAlias("estado", "estado", CriteriaSpecification.LEFT_JOIN);
							criteria.createAlias("estadoAgroseguro", "estadoAgroseguro",
									CriteriaSpecification.LEFT_JOIN);

							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);

							/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
							criteria.add(Restrictions.ne("aseg.isBloqueado", Integer.valueOf(1)));
							/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Fin */

							if (rowStart != -1 && rowEnd != -1) {
						        // Primer registro
						        criteria.setFirstResult(rowStart);
						        // Número máximo de registros a mostrar
						        criteria.setMaxResults(rowEnd - rowStart);
						    }
							// DAA 23/10/12
							List<AnexoModificacion> lista = criteria.list();
							List<AnexoModificacionItem> listaItem = new ArrayList<AnexoModificacionItem>();
							for (AnexoModificacion anexo : lista) {
								listaItem.add(new AnexoModificacionItem(anexo));
							}
							return listaItem;
						}
					});
			return applications;
		} catch (Exception e) {
			log("getAnexoModificacionWithFilterAndSort",
					ERROR, e);
			throw new BusinessException(
					ERROR + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AnexoModificacion getAnexoByIdCupon(Object id) throws DAOException {
		try {
			Criteria criteria = getSession().createCriteria(AnexoModificacion.class);
			criteria.createAlias(CUPON, CUPON);

			// Si se recibe el id numérico asociado al cupón
			if (id instanceof java.lang.Long)
				criteria.add(Restrictions.eq("cupon.id", id));
			// Si se recibe el identificador del cupón
			else if (id instanceof java.lang.String)
				criteria.add(Restrictions.eq("cupon.idcupon", id));
			// Si el id no es de ninguno de estos tipos se devuelve nulo
			else
				return null;

			List<AnexoModificacion> lista = criteria.list();
			if (lista != null && lista.size() > 0) {
				return lista.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("Error al obtener el anexo asociado al idcupon " + id, e);
			throw new DAOException(e);
		}
	}

	@Override
	public void actualizar(AnexoModificacion am) throws DAOException {
		try {
			this.saveOrUpdate(am);
		} catch (Exception e) {
			logger.error("Error al actualizar el anexo", e);
			throw new DAOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Clob getAcuseConfirmacion(Long idAnexo) throws DAOException {

		try {
			Criteria criteria = getSession().createCriteria(EnviosSWConfirmacion.class);
			criteria.createAlias("anexoModificacion", "anexoModificacion");
			criteria.add(Restrictions.eq("anexoModificacion.id", idAnexo));
			criteria.addOrder(Order.desc("fecha"));

			List<EnviosSWConfirmacion> listaEnvios = criteria.list();
			if (listaEnvios != null && listaEnvios.size() > 0) {
				return listaEnvios.get(0).getEnviosSWXMLByIdxmlAcuse().getXml();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return null;
	}

	/**
	 * Busca el ultimo XML enviado para ver el ultimo estado original.
	 */
	@SuppressWarnings("rawtypes")
	public Clob getXMLSituacionActualizada(Long idAnexo) throws DAOException {

		try {
			Session session = obtenerSession();
			String consulta = null;
			consulta = "select xml from vw_inf_anexos_mod_expl_XML_Org" + " where idAnexo=" + idAnexo;

			List EnviosSWXML = session.createSQLQuery(consulta).list();
			if (EnviosSWXML != null && EnviosSWXML.size() > 0) {

				return (Clob) EnviosSWXML.get(0);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return null;
	}

	/**
	 * Comprueba si existen explotaciones en el anexo
	 */
	@SuppressWarnings("rawtypes")
	public boolean existenExplotacionesEnAnexo(Long idAnexoModificacion) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from TB_ANEXO_MOD_EXPLOTACIONES aexp where aexp.id_anexo = "
					+ idAnexoModificacion;
			List list = session.createSQLQuery(sql).list();

			if (((BigDecimal) list.get(0)).intValue() > 0)
				result = true;
			else
				result = false;
		} catch (Exception excepcion) {
			logger.error("Error al comprobar si existen explotaciones en el anexo", excepcion);
			throw new DAOException("Error al copiar explotaciones al anexo", excepcion);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isAnexoExplotacionesConModificaciones(Long idAnexoModificacion) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from tb_anexo_mod_explotaciones exp where exp.id_anexo = "
					+ idAnexoModificacion + " and exp.tipo_modificacion is not null group by exp.id_anexo";
			List list = session.createSQLQuery(sql).list();

			if (list.size() > 0 && ((BigDecimal) list.get(0)).intValue() > 0) {
				result = true;
			}
		} catch (Exception excepcion) {
			logger.error("Error al comprobar si existen explotaciones de anexo con modificaciones", excepcion);
			throw new DAOException("Error al comprobar si existen explotaciones de anexo con modificaciones",
					excepcion);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isAnexoCoberturasConModificaciones(Long idAnexoModificacion) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from tb_anexo_mod_coberturas exp where exp.idanexo = "
					+ idAnexoModificacion + " and exp.tipomodificacion is not null group by exp.idanexo";
			List list = session.createSQLQuery(sql).list();

			if (list.size() > 0 && ((BigDecimal) list.get(0)).intValue() > 0) {
				result = true;
			}
		} catch (Exception excepcion) {
			logger.error("Error al comprobar si existen coberturas de anexo con modificaciones", excepcion);
			throw new DAOException("Error al comprobar si existen coberturas de anexo con modificaciones",
					excepcion);
		}

		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean isAnexoSubvencionesConModificaciones(Long idAnexoModificacion) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from tb_anexo_mod_subv_decl exp where exp.idanexo = "
					+ idAnexoModificacion + " and exp.tipomodificacion is not null group by exp.idanexo";
			List list = session.createSQLQuery(sql).list();

			if (list.size() > 0 && ((BigDecimal) list.get(0)).intValue() > 0) {
				result = true;
			}
		} catch (Exception excepcion) {
			logger.error("Error al comprobar si existen coberturas de anexo con modificaciones", excepcion);
			throw new DAOException("Error al comprobar si existen coberturas de anexo con modificaciones",
					excepcion);
		}

		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean isAnexoParcelasConModificaciones(Long idAnexoModificacion) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from tb_anexo_mod_parcelas par where par.idanexo = " + idAnexoModificacion
					+ " and par.tipomodificacion is not null group by par.idanexo";
			List list = session.createSQLQuery(sql).list();

			if (list.size() > 0 && ((BigDecimal) list.get(0)).intValue() > 0) {
				result = true;
			}
		} catch (Exception excepcion) {
			logger.error("Error al comprobar si existen parcelas de anexo con modificaciones", excepcion);
			throw new DAOException("Error al comprobar si existen parcelas de anexo con modificaciones", excepcion);
		}

		return result;
	}

	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * 
	 * @param method
	 * @param msg
	 */
	private void log(String method, String msg) {
		logger.debug(this.getClass().getName() + "." + method + " - " + msg);
	}

	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * 
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log(String method, String msg, Throwable e) {
		logger.error(this.getClass().getName() + "." + method + " - " + msg, e);
	}

	@SuppressWarnings("rawtypes")
	public boolean getCountgruposNegocio(Long lineaseguroId) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(*) from TB_LINEAS_GRUPO_NEGOCIO where lineaseguroid = " + lineaseguroId;
			List list = session.createSQLQuery(sql).list();

			if (((BigDecimal) list.get(0)).intValue() > 0)
				result = true;
			else
				result = false;
		} catch (Exception excepcion) {
			logger.error("Error al comprobar los grupos de negocio ", excepcion);
			throw new DAOException("comprobar los grupos de negocio", excepcion);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	public String getDescGrupoNegocio(Character grupoNegocio) {
		String descGr = "";
		Session session = obtenerSession();

		try {
			String sql = "select descripcion from TB_SC_C_GRUPOS_NEGOCIO where grupo_negocio = " + grupoNegocio;
			List list = session.createSQLQuery(sql).list();
			descGr = (String) list.get(0);

		} catch (Exception e) {
			logger.error("Error al obtener la descripcion del grupo de negocio ", e);

		}
		return descGr;
	}

	public Poliza getPolizaById(Long idPoliza) throws DAOException {
		Poliza poliza;
		try {
			poliza = (Poliza) get(Poliza.class, idPoliza);

			return poliza;

		} catch (Exception e) {
			throw new DAOException(ERROR, e);
		}
	}

	/**
	 * Devuelve un booleano indicando si alguno de los códigos de tipo de capital
	 * contenidos en la lista recibida como parámetro se corresponde con RyD
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean checkTCRyD(List<Integer> lista) throws DAOException {
		boolean resultado = false;
		Session session = obtenerSession();
		String listaStr = StringUtils.collectionToCommaDelimitedString(lista);
		try {
			String sql = "select count(1) from o02agpe0.tb_sc_c_tipo_capital_grupo_neg tc" + " where "
					+ "tc.codtipocapital in (" + listaStr + ") and" + " tc.grupo_negocio ='2'";

			List list = session.createSQLQuery(sql).list();
			if (list.size() > 0 && ((BigDecimal) list.get(0)).intValue() > 0) {
				resultado = true;
			}

		} catch (Exception e) {
			logger.error("Error al obtener si alguno de los códigos de tipo de capital se corresponde con RyD ", e);

		}

		return resultado;
	}

	/* Pet. 78691 ** MODIF TAM (15.12.2021) ** Inicio */
	/*
	 * Declaramos una nueva función para dar de alta el caracter de la explotación
	 * obtenido de la llamada al S. Web correspondiente
	 */
	@Override
	public void guardarCaractExplAnx(Long idAnexo, BigDecimal caractExplAnx) throws DAOException {

		try {
			logger.debug("**@@** AnexoModificacionDao - guardarCaractExplAnx [INIT]");
			Session session = obtenerSession();

			String sql = "update o02agpe0.tb_anexo_mod anx set anx.codcaractexplotacion = " + caractExplAnx
					+ " where anx.id = " + idAnexo;

			session.createSQLQuery(sql).executeUpdate();
			logger.debug("Fin: guardarCaractExplAnx - AnexoModificacionDao");

		} catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la caracteristica de la explotación del Anexo: ", e);
			throw new DAOException();
		}

		logger.debug("**@@** AnexoModificacionDao - guardarCaractExplAnx [END]");
	}

	/* Pet. 78691 ** MODIF TAM (15.12.2021) ** Fin */
	/**
	 * Devuelve un booleano indicando si la primera explotación modificada o dada de
	 * alta del anexo indicado por el id recibido como parámetro contiene algún tipo
	 * de capital de retirada.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean checkExplotacionesTCRyD(Long IdAnexo) throws DAOException {
		boolean resultado = false;
		Session session = obtenerSession();

		try {
			String sql = "select count(1) from o02agpe0.tb_anexo_mod_grupo_raza amgr,"
					+ " o02agpe0.tb_sc_c_tipo_capital_grupo_neg tc" + " where amgr.id_explotacion_anexo in ("
					+ " select min(e.id)" + " from o02agpe0.tb_anexo_mod_explotaciones e " + " where e.id_anexo ="
					+ IdAnexo + " and e.tipo_modificacion in ('A','M'))" + " and amgr.codtipocapital=tc.codtipocapital"
					+ " and tc.grupo_negocio ='2'";

			List list = session.createSQLQuery(sql).list();
			if (list.size() > 0 && ((BigDecimal) list.get(0)).intValue() > 0) {
				resultado = true;
			}

		} catch (Exception e) {
			logger.error(
					"Error al obtener si la primera explotación modificada o dada de alta del anexo contiene algún tipo de capital de retirada ",
					e);

		}
		return resultado;
	}
	
	public AnexoModificacion saveAnexoModificacion(AnexoModificacion anexo) {
		try {
			Session sesion = this.obtenerSession();
			this.evict(anexo);
			sesion.saveOrUpdate(anexo);
			return anexo;
			
		} catch (Exception e) {
			try {
				throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			} catch (DAOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return anexo;
	} 
}