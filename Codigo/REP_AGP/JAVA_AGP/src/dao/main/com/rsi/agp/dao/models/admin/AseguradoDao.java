package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.admin.impl.Asegurado2Filtro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.BloqueosAsegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizadoSC;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.cpl.gan.AseguradoAutorizadoGanado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AseguradoDao extends BaseDaoHibernate implements IAseguradoDao {

	final DateFormat df = new SimpleDateFormat("yyyy");
	private static final Log LOGGER = LogFactory.getLog(AseguradoDao.class);

	public int getCountAsegurados(Asegurado2Filtro aseguradoFiltro) {

		Session session = obtenerSession();
		String sql = "SELECT AGP_VALOR FROM TB_CONFIG_AGP WHERE AGP_NEMO = 'SQL_COUNT_ASEGURADOS'";
		List list = session.createSQLQuery(sql).list();

		sql = list.get(0).toString();

		sql += aseguradoFiltro.getSqlWhere();

		return ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();
	}

	/* -------------------------------------------------------- */
	/* DISPLAYTAG PAGINATION */
	/* -------------------------------------------------------- */
	public PaginatedListImpl<Asegurado> getPaginatedListAsegurados(PageProperties pageProperties,
			Asegurado2Filtro aseguradoFiltro) throws DAOException {

		PaginatedListImpl<Asegurado> paginatedListImpl = new PaginatedListImpl<Asegurado>();
		List<Asegurado> listAsegurados = null;

		try {

			if (pageProperties.getFullListSize() > 0) {
				listAsegurados = getPageAsegurados(pageProperties, aseguradoFiltro);
			} else {
				listAsegurados = new ArrayList<Asegurado>();
			}
			paginatedListImpl.setFullListSize(pageProperties.getFullListSize());
			paginatedListImpl.setObjectsPerPage(pageProperties.getPageSize());
			paginatedListImpl.setPageNumber(pageProperties.getPageNumber());
			paginatedListImpl.setList(listAsegurados);
			paginatedListImpl.setSortCriterion(pageProperties.getSort());
			if (pageProperties.getDir().equals("asc")) {
				paginatedListImpl.setSortDirection(SortOrderEnum.ASCENDING);
			} else if (pageProperties.getDir().equals("desc")) {
				paginatedListImpl.setSortDirection(SortOrderEnum.DESCENDING);
			}

		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ", e);
		}
		return paginatedListImpl;
	}

	public List<Asegurado> getPageAsegurados(PageProperties pageProperties, Asegurado2Filtro aseguradoFiltro) {
		Session session = obtenerSession();

		Criteria criteria = aseguradoFiltro.getCriteria(session);

		if (pageProperties.getDir().equals("asc")) {
			criteria.addOrder(Order.asc(pageProperties.getSort()));
		} else if (pageProperties.getDir().equals("desc")) {
			criteria.addOrder(Order.desc(pageProperties.getSort()));
		}
		criteria.setFirstResult(pageProperties.getIndexRowMin());
		criteria.setMaxResults(pageProperties.getPageSize());

		return criteria.list();
	}

	/* -------------- fin displaytag pagination ----------------- */

	@Override
	public List<Poliza> tienePolizasVigentes(Asegurado aseg, BigDecimal[] estados) throws DAOException {
		Session session = obtenerSession();

		try {

			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias("linea", "l");

			criteria.add(Restrictions.eq("asegurado.id", aseg.getId()));
			criteria.add(Restrictions.in("estadoPoliza.idestado", estados));

			String planVigente = df.format(new Date());

			criteria.add(Restrictions.between("l.codplan", new BigDecimal(planVigente).subtract(BigDecimal.ONE),
					new BigDecimal(planVigente)));

			return criteria.list();

		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}

	@Override
	public List<Asegurado> getAseguradosGrupoEntidad(Asegurado aseguradoBean, List<BigDecimal> entidades)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Asegurado.class);
			criteria.createAlias("entidad", "ent");
			criteria.addOrder(Order.asc("ent.codentidad"));
			criteria.createAlias("usuario", "usu");
			criteria.createAlias("usu.subentidadMediadora", "esMed", CriteriaSpecification.LEFT_JOIN);
			if (FiltroUtils.noEstaVacio(aseguradoBean)) {
				criteria.add(Restrictions.allEq(getMapaAsegurado(aseguradoBean)));
			}

			// Si el perfil pertenece a algun grupo de Entidades
			if (entidades.size() > 0) {
				if (aseguradoBean.getEntidad().getCodentidad() != null) {
					criteria.add(Restrictions.eq("ent.codentidad", aseguradoBean.getEntidad().getCodentidad()));
				} else {
					criteria.add(Restrictions.in("ent.codentidad", entidades));
				}

			} else {
				final BigDecimal codEntidad = aseguradoBean.getEntidad().getCodentidad();
				if (FiltroUtils.noEstaVacio(codEntidad)) {
					criteria.add(Restrictions.eq("ent.codentidad", codEntidad));
				}
			}

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}

	}

	public Map<String, Object> getMapaAsegurado(Asegurado asegurado) {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final String nifCif = asegurado.getNifcif();
		if (FiltroUtils.noEstaVacio(nifCif)) {
			mapa.put("nifcif", nifCif);
		}

		final String tipoIdent = asegurado.getTipoidentificacion();
		if (FiltroUtils.noEstaVacio(tipoIdent)) {
			mapa.put("tipoidentificacion", tipoIdent);
		}

		final String discriminante = asegurado.getDiscriminante();
		if (FiltroUtils.noEstaVacio(discriminante)) {
			mapa.put("discriminante", discriminante);
		}

		if (asegurado.getUsuario() != null) {
			String codusuario = asegurado.getUsuario().getCodusuario();
			if (FiltroUtils.noEstaVacio(codusuario)) {
				mapa.put("usuario.codusuario", codusuario);
			}
			BigDecimal codEntidad = asegurado.getUsuario().getSubentidadMediadora().getId().getCodentidad();
			BigDecimal codSubEntidad = asegurado.getUsuario().getSubentidadMediadora().getId().getCodsubentidad();
			if (null != codEntidad) {
				mapa.put("esMed.id.codentidad", codEntidad);
			}
			if (null != codSubEntidad) {
				mapa.put("esMed.id.codsubentidad", codSubEntidad);
			}
		}

		// Filtros del bloque direccion
		
		// Via
		if (FiltroUtils.noEstaVacio(asegurado.getVia().getClave())) {
			mapa.put("via.clave", asegurado.getVia().getClave());
		}
		
		// Domicilio 
		if (FiltroUtils.noEstaVacio(asegurado.getDireccion())) {
			mapa.put("direccion", asegurado.getDireccion());
		}
		
		// Numero
		if (FiltroUtils.noEstaVacio(asegurado.getNumvia())) {
			mapa.put("numvia", asegurado.getNumvia());
		}
		
		// Piso
		if (FiltroUtils.noEstaVacio(asegurado.getPiso())) {
			mapa.put("piso", asegurado.getPiso());
		}
		
		// Bloque
		if (FiltroUtils.noEstaVacio(asegurado.getBloque())) {
			mapa.put("bloque", asegurado.getBloque());
		}
		
		// Escalera
		if (FiltroUtils.noEstaVacio(asegurado.getEscalera())) {
			mapa.put("escalera", asegurado.getEscalera());
		}
		
		// Provincia
		if (FiltroUtils.noEstaVacio(asegurado.getLocalidad().getId().getCodprovincia())) {
			mapa.put("localidad.id.codprovincia", asegurado.getLocalidad().getId().getCodprovincia());
		}
		
		// Localidad
		if (FiltroUtils.noEstaVacio(asegurado.getLocalidad().getId().getCodlocalidad())) {
			mapa.put("localidad.id.codlocalidad", asegurado.getLocalidad().getId().getCodlocalidad());
		}
		
		// Sublocalidad
		if (FiltroUtils.noEstaVacio(asegurado.getLocalidad().getId().getSublocalidad())) {
			mapa.put("localidad.id.sublocalidad", asegurado.getLocalidad().getId().getSublocalidad());
		}
		
		// Codpostal
		if (FiltroUtils.noEstaVacio(asegurado.getCodpostal())) {
			mapa.put("codpostal", asegurado.getCodpostal());
		}
		
		// Filtros del bloque contacto
		
		// Telefono
		if (FiltroUtils.noEstaVacio(asegurado.getTelefono())) {
			mapa.put("telefono", asegurado.getTelefono());
		}
		
		// Movil
		if (FiltroUtils.noEstaVacio(asegurado.getMovil())) {
			mapa.put("movil", asegurado.getMovil());
		}
		
		// Email
		if (FiltroUtils.noEstaVacio(asegurado.getEmail())) {
			mapa.put("email", asegurado.getEmail());
		}
		
		// SS
		if (FiltroUtils.noEstaVacio(asegurado.getNumsegsocial())) {
			mapa.put("numsegsocial", asegurado.getNumsegsocial());
		}
		
		// Regimen social
		if (FiltroUtils.noEstaVacio(asegurado.getRegimensegsocial())) {
			mapa.put("regimensegsocial", asegurado.getRegimensegsocial());
		}
		
		// ATP
		if (FiltroUtils.noEstaVacio(asegurado.getAtp())) {
			mapa.put("atp", asegurado.getAtp());
		}
		
		// Young farmer 
		if (FiltroUtils.noEstaVacio(asegurado.getJovenagricultor())) {
			mapa.put("jovenagricultor", asegurado.getJovenagricultor());
		}

		return mapa;
	}

	@Override
	public Usuario aseguradoCargadoUsuario(Asegurado aseg, Long lineaseguroid, String usuario) throws DAOException {
		Session session = obtenerSession();
		Usuario usuarioP = new Usuario();
		try {
			Query query = session
					.createQuery("from Usuario u " + "where u.asegurado.id = :aseg "
							+ "and u.colectivo.linea.lineaseguroid = :linea " + "and u.codusuario != :usuario ")
					.setLong("aseg", aseg.getId()).setLong("linea", lineaseguroid).setString("usuario", usuario);

			usuarioP = (Usuario) query.uniqueResult();

			return usuarioP;

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public List<SubvencionesGrupo> getGruposSubv(BigDecimal codplan) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(SubvencionesGrupo.class);

			if (codplan != null) {
				criteria.add(Restrictions.eq("id.plan", codplan));
			}

			return criteria.list();

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	public Integer getCountAseguradosAutorizados(final Long lineaseguroid, final String nifcif) throws DAOException {
		return getCountAseguradosAutorizados(lineaseguroid, nifcif, Boolean.FALSE);
	}
	
	public Integer getCountAseguradosAutorizadosG(final Long lineaseguroid, final String nifcif) throws DAOException {
		return getCountAseguradosAutorizados(lineaseguroid, nifcif, Boolean.TRUE);
	}
	
	private Integer getCountAseguradosAutorizados(final Long lineaseguroid, final String nifcif, final Boolean esGanado) throws DAOException {
		Session session = obtenerSession();
		try {
			StringBuilder stringQuery = new StringBuilder();
			if (esGanado) {
				stringQuery.append("SELECT count(*) from TB_SC_C_ASEG_AUTORIZADOS_G where lineaseguroid = :lineaseguroid");
				if (FiltroUtils.noEstaVacio(nifcif)) {
					stringQuery.append(" and nif_asegurado = :nifasegurado");
				} else {
					stringQuery.append(" and nif_asegurado is null");
				}
			} else {
				stringQuery.append("SELECT count(*) from TB_SC_C_ASEG_AUTORIZADOS where lineaseguroid = :lineaseguroid");
				if (FiltroUtils.noEstaVacio(nifcif)) {
					stringQuery.append(" and nifasegurado = :nifasegurado");
				} else {
					stringQuery.append(" and nifasegurado is null");
				}
			}

			SQLQuery query = session.createSQLQuery(stringQuery.toString());
			query.setParameter("lineaseguroid", new Long(lineaseguroid));

			if (FiltroUtils.noEstaVacio(nifcif)) {
				query.setParameter("nifasegurado", nifcif);
			}

			Integer result = new Integer((query.uniqueResult()).toString());
			return result;

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	@Override
	public AseguradoAutorizadoSC[] getAAC(final Long lineaseguroid, final String nifcif) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria crit = session.createCriteria(AseguradoAutorizadoSC.class);
			crit.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			if (StringUtils.isNullOrEmpty(nifcif)) {
				crit.add(Restrictions.isNull("nifasegurado"));
			} else {
				crit.add(Restrictions.eq("nifasegurado", nifcif));
			}
			List<?> resLst = crit.list();
			if (CollectionUtils.isEmpty(resLst)) {
				if (StringUtils.isNullOrEmpty(nifcif)) {
					return new AseguradoAutorizadoSC[] {};
				} else {
					return getAAC(lineaseguroid, null);
				}
			} else {
				return resLst.toArray(new AseguradoAutorizadoSC[] {});
			}
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public AseguradoAutorizadoGanado[] getAACGan(final Long lineaseguroid, final String nifcif) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria crit = session.createCriteria(AseguradoAutorizadoGanado.class);
			crit.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			if (StringUtils.isNullOrEmpty(nifcif)) {
				crit.add(Restrictions.isNull("nifasegurado"));
			} else {
				crit.add(Restrictions.eq("nifasegurado", nifcif));
			}
			List<?> resLst = crit.list();
			if (CollectionUtils.isEmpty(resLst)) {
				if (StringUtils.isNullOrEmpty(nifcif)) {
					return new AseguradoAutorizadoGanado[] {};
				} else {
					return getAACGan(lineaseguroid, null);
				}
			} else {
				return resLst.toArray(new AseguradoAutorizadoGanado[] {});
			}
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	@Override
	public Integer getcountOrigenInfo(Long lineaseguroid, BigDecimal usoAutorizContrat) throws DAOException {
		Session session = obtenerSession();
		try {
			String stringQuery = "SELECT count(*) from tb_sc_oi_org_info where lineaseguroid = :lineaseguroid and coduso = :coduso";
			SQLQuery query = session.createSQLQuery(stringQuery);
			query.setParameter("lineaseguroid", new Long(lineaseguroid));
			query.setParameter("coduso", new Long(usoAutorizContrat.longValue()));

			Integer result = new Integer((query.uniqueResult()).toString());
			return result;

		} catch (Exception e) {
			logger.debug("Se ha producido un error en la BBDD: " + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
	}

	public BigDecimal[] obtenerIntervaloCoefReduccionRdtos(String tipo, Long lineaseguroid, String nifcifAsegurado,
			List<String> lstCodModulos, List<BigDecimal> lstCodCultivos, List<BigDecimal> lstCodVariedades)
			throws DAOException {
		boolean filtrarPorModulo = true;
		boolean filtrarPorCultivo = true;
		boolean filtrarPorVariedad = true;
		Session session = obtenerSession();
		BigDecimal resultado[] = new BigDecimal[2];

		try {

			Criteria criteria = session.createCriteria(AseguradoAutorizadoSC.class);

			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));

			if (lstCodModulos.isEmpty() || lstCodModulos.contains("99999"))
				filtrarPorModulo = false;

			if (lstCodCultivos.isEmpty() || lstCodCultivos.contains(new BigDecimal(999)))
				filtrarPorCultivo = false;

			if (lstCodVariedades.isEmpty() || lstCodVariedades.contains(new BigDecimal(999)))
				filtrarPorVariedad = false;

			if (filtrarPorModulo) {
				criteria.add(Restrictions.in("modulo.id.codmodulo", lstCodModulos));
			}
			if (filtrarPorCultivo) {
				criteria.add(Restrictions.in("variedad.id.codcultivo", lstCodCultivos));
			}
			if (filtrarPorVariedad) {
				criteria.add(Restrictions.in("variedad.id.codvariedad", lstCodVariedades));
			}

			if (FiltroUtils.noEstaVacio(nifcifAsegurado))
				criteria.add(Restrictions.eq("nifasegurado", nifcifAsegurado));
			else
				criteria.add(Restrictions.isNull("nifasegurado"));

			if (tipo.equals("coefsobrerdtos")) {
				criteria.setProjection(Projections.projectionList().add(Projections.min("coefsobrerdtos"))
						.add(Projections.max("coefsobrerdtos")));
			} else if (tipo.equals("rdtopermitido")) {
				criteria.setProjection(Projections.projectionList().add(Projections.min("rdtopermitido"))
						.add(Projections.max("rdtopermitido")));
			} else {
				criteria.setProjection(Projections.projectionList().add(Projections.min("coefsobrerdtos"))
						.add(Projections.max("coefsobrerdtos")));
			}

			Object[] aux = (Object[]) criteria.uniqueResult();

			if (aux != null) {
				if (aux[0] != null)
					resultado[0] = new BigDecimal(aux[0].toString());
				if (aux[1] != null)
					resultado[1] = new BigDecimal(aux[1].toString());
			}
			return resultado;

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	/**
	 * Método que comprueba si existe otro usuario distinto al que le llega como
	 * parámetro, que tiene cargado el mismo asegurado.
	 */
	public boolean chekAseguradoDisponible(String codusuario, Long idAsegurado) {
		Session session = obtenerSession();

		String sql = "select count(*) from TB_USUARIOS where idasegurado = " + idAsegurado + " and codusuario <> '"
				+ codusuario + "'";

		int num = ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();
		return num <= 0;
	}

	public List getCodsOrganismos(List<BigDecimal> codsProv) throws DAOException {
		List codsOrganismos = new ArrayList();
		try {
			Session session = obtenerSession();
			String sql = "select distinct(codorganismo) from tb_sc_c_provs_organismo po where po.codprovincia in "
					+ StringUtils.toValoresSeparadosXComas(codsProv, false);

			codsOrganismos = session.createSQLQuery(sql).list();

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}

		return codsOrganismos;
	}

	@Override
	public void desbloqueaAsegurado(String usuarioAsegurado) throws DAOException {

		Session session = obtenerSession();
		try {
			String sql = "update tb_usuarios u set idclase=null,idasegurado=null where u.codusuario ='"
					+ usuarioAsegurado + "'";

			session.createSQLQuery(sql).executeUpdate();

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}

	}

	@Override
	public void actualizaDatosAseguradoWS(Asegurado aseguradoBean, String idAseg) throws DAOException {

		Session session = obtenerSession();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("update tb_asegurados u set nifcif= '" + aseguradoBean.getNifcif() + "'");

			if (!StringUtils.nullToString(aseguradoBean.getRazonsocial()).equals("")) {
				sql.append(" , razonsocial = '" + aseguradoBean.getRazonsocial() + "'");
				sql.append(" , nombre = null");
				sql.append(" , apellido1 = null");
				sql.append(" , apellido2 = null ");
			} else {
				sql.append(" , nombre = '" + aseguradoBean.getNombre() + "'");
				sql.append(" , apellido1 = '" + aseguradoBean.getApellido1() + "'");
				sql.append(" , apellido2 = '" + aseguradoBean.getApellido2() + "'");
				sql.append(" , razonsocial = null");
			}
			if (!StringUtils.nullToString(aseguradoBean.getVia().getClave()).equals(""))
				sql.append(" , clavevia = '" + aseguradoBean.getVia().getClave() + "'");
			else
				sql.append(" , clavevia = null ");
			if (!StringUtils.nullToString(aseguradoBean.getDireccion()).equals(""))
				sql.append(" , direccion = '" + aseguradoBean.getDireccion() + "'");
			else
				sql.append(" , direccion = null ");

			if (!StringUtils.nullToString(aseguradoBean.getNumvia()).equals(""))
				sql.append(" , numvia = '" + aseguradoBean.getNumvia() + "'");
			else
				sql.append(" , numvia = null ");

			if (!StringUtils.nullToString(aseguradoBean.getPiso()).equals(""))
				sql.append(" , piso = '" + aseguradoBean.getPiso() + "'");
			else
				sql.append(" , piso = null");

			if (!StringUtils.nullToString(aseguradoBean.getBloque()).equals(""))
				sql.append(", bloque = '" + aseguradoBean.getBloque() + "'");
			else
				sql.append(", bloque = null");

			if (!StringUtils.nullToString(aseguradoBean.getEscalera()).equals(""))
				sql.append(", escalera = '" + aseguradoBean.getEscalera() + "'");
			else
				sql.append(", escalera = null");

			if (aseguradoBean.getLocalidad().getId().getCodprovincia() != null)
				sql.append(", codprovincia = " + aseguradoBean.getLocalidad().getId().getCodprovincia());
			else
				sql.append(", codprovincia = null");

			if (aseguradoBean.getLocalidad().getId().getCodlocalidad() != null)
				sql.append(", codlocalidad = " + aseguradoBean.getLocalidad().getId().getCodlocalidad());
			else
				sql.append(", codlocalidad = null");

			if (aseguradoBean.getLocalidad().getId().getSublocalidad() != null)
				sql.append(", sublocalidad = " + aseguradoBean.getLocalidad().getId().getSublocalidad());
			else
				sql.append(", sublocalidad = null");

			if (!StringUtils.nullToString(aseguradoBean.getCodpostal()).equals(""))
				sql.append(", codpostal = '" + aseguradoBean.getCodpostal() + "'");
			else
				sql.append(", codpostal = null");

			if (!StringUtils.nullToString(aseguradoBean.getTelefono()).equals(""))
				sql.append(", telefono = '" + aseguradoBean.getTelefono() + "'");
			else
				sql.append(", telefono = null");

			if (!StringUtils.nullToString(aseguradoBean.getMovil()).equals(""))
				sql.append(", movil = '" + aseguradoBean.getMovil() + "'");
			else
				sql.append(", movil = null");

			if (!StringUtils.nullToString(aseguradoBean.getEmail()).equals(""))
				sql.append(", email = '" + aseguradoBean.getEmail() + "'");
			else
				sql.append(", email = null");

			if (!StringUtils.nullToString(aseguradoBean.getUsuarioModificacion()).equals(""))
				sql.append(", usuario_modificacion = '" + aseguradoBean.getUsuarioModificacion() + "'");
			else
				sql.append(", usuario_modificacion = null");

			if (aseguradoBean.getFechaModificacion() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
				String fecha = sdf.format(aseguradoBean.getFechaModificacion());
				sql.append(", fecha_modificacion = to_date('" + fecha + "','DD/MM/YYYY HH24:mi:ss') ");
			} else
				sql.append(", fecha_modificacion = null");

			sql.append(" where id = " + new Long(idAseg));

			LOGGER.debug(sql.toString());

			session.createSQLQuery(sql.toString()).executeUpdate();

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}

	}

	/**
	 * Dado un codprovincia y el nombre de la localidad obtiene el codlocalidad, la
	 * sublocalidad y el nombre de la provincia
	 * 
	 * @param codProvincia,
	 *            localidad
	 * @return Object[]
	 * @throws DAOException
	 */
	@Override
	public Object[] getDatosProvincia(BigDecimal codProvincia, String localidad) throws DAOException {

		Session session = obtenerSession();
		try {
			StringBuilder querySb = new StringBuilder();
			querySb.append("select l.codlocalidad, l.sublocalidad, p.nomprovincia from tb_localidades l")
					.append(" inner join o02agpe0.tb_provincias p on l.codprovincia = p.codprovincia")
					.append(" where l.codprovincia = :codProvincia and nomlocalidad = :nomlocalidad");

			SQLQuery query = session.createSQLQuery(querySb.toString());
			query.setBigDecimal("codProvincia", codProvincia);
			query.setString("nomlocalidad", localidad);

			List<Object[]> datos = (List<Object[]>) query.list();

			return datos.get(0);

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}

	/**
	 * Actualiza la fecha revision de un asegurado
	 * 
	 * @param nifcif
	 * @throws DAOException
	 */
	@Override
	public void actualizaFechaRevision(String idAseg, String revisado) throws DAOException {
		Session session = obtenerSession();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechahoy = sdf.format(new Date());

		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" update tb_asegurados u ");
			sql.append(" set fecha_revision= TO_DATE('" + fechahoy + "','DD/MM/YYYY') ");
			if (revisado != null) {
				sql.append(" , revisado='" + revisado + "' ");
			}
			sql.append(" where id = '" + idAseg + "' ");

			LOGGER.debug(sql.toString());
			session.createSQLQuery(sql.toString()).executeUpdate();

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}

	/**
	 * Coje la propiedad VALIDA_CARGA_ASEGURADO de la tabla TB_CONFIG_AGP si es
	 * true: comprobamos la fecha de revision del asegurado si es false: No
	 * comprobamos nada y continuamos con la carga
	 */
	@Override
	public String validaCargaASegurado() throws DAOException {
		Session session = obtenerSession();
		String sql = "SELECT AGP_VALOR FROM TB_CONFIG_AGP WHERE AGP_NEMO = 'VALIDA_CARGA_ASEGURADO'";
		String valor = (String) session.createSQLQuery(sql).uniqueResult();

		return valor;
	}

	@Override
	public Asegurado getAseguradoById(String id) throws DAOException {
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Asegurado.class);

			criteria.add(Restrictions.eq("id", Long.parseLong(id)));
			List<Asegurado> asegs = criteria.list();
			if (asegs.size() > 0)
				return (Asegurado) asegs.get(0);
			else
				return null;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}

	/* Pet. 62719 ** MODIF TAM (24.02.2021) ** Inicio */
	/* Resoluci�n de Incidencia pasada por RGA */
	@Override
	public BloqueosAsegurado consultarAsegBloqueado(String nifcif) throws DAOException {
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(BloqueosAsegurado.class);

			criteria.add(Restrictions.eq("nifcif", nifcif));
			criteria.add(Restrictions.eq("idEstadoAseg", "B"));
			List<BloqueosAsegurado> asegs = criteria.list();
			if (asegs.size() > 0)
				return (BloqueosAsegurado) asegs.get(0);
			else
				return null;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}
	@Override
	public List<Asegurado> getAsegurados(String inicio,String fin)
			throws DAOException {
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(Asegurado.class);
			return criteria.setFirstResult(Integer.parseInt(inicio)) // offset
			         .setMaxResults(Integer.parseInt(fin)) // limit
			         .list();
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/* Pet. 62719 ** MODIF TAM (24.02.2021) ** Fin */
}