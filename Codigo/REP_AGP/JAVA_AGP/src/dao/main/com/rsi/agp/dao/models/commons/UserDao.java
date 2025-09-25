package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.model.user.User;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinaId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.MedidaId;

@SuppressWarnings("rawtypes")
public class UserDao extends BaseDaoHibernate implements IUserDao {
	
	private static final Log log = LogFactory.getLog(UserDao.class);

	// Atributos para las llamadas a los procedimientos almacenados
	private User usuario;

	@Override
	public User login(User userFinal) throws BusinessException, HibernateException, SQLException {
		
		usuario = userFinal;

		log.debug("Comprobando acceso ... ");
		log.debug("Seguridad controlada por usuario cifrado ");

		try {
			this.setPerfil(usuario);
		} catch (HibernateException e) {
			log.fatal("Error al cargar los datos del usuario", e);
			throw e;
		} catch (SQLException e) {
			log.fatal("Error al cargar los datos del usuario", e);
			throw e;
		}

		boolean access = this.hasPermission();

		log.debug("access: " + access);
		if (!access) {
			usuario = null;
		}

		log.debug("Fin seguridad ");
		log.debug(usuario.toString());
		return usuario;
	}

	/**
	 * Metodo para comprobar si un usuario tiene permiso para acceder a la aplicacion.
	 * @return
	 */
	private boolean hasPermission() {
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						String queryEncriptar = "select PQ_SEGURIDAD_AGP.FN_USUARIO_APLICACION_VALID('"
								+ usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + 
								"') FROM dual";
						Query query = session.createSQLQuery(queryEncriptar);
						return query.list();
					}
				});
		
		boolean access = false;
		if (result.size() == 1) {
			access = Boolean.parseBoolean((String) result.get(0));
		}
		return access;
	}

	/**
	 * Metodo para codificar un codigo de usuario.
	 * @param user Usuario a codificar
	 * @return Usuario codificado
	 * @throws BusinessException
	 */
	public String encodeUser(final String user) throws BusinessException {
		String ret = null;

		List result = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						String queryEncriptar = "select PK_MOD_SEGURIDAD.FT_ENCRIPTAR ('"
								+ user + "') FROM dual";
						Query query = session.createSQLQuery(queryEncriptar);
						return query.list();
					}
				});

		if (result.size() == 1) {
			ret = (String) result.get(0);
		}
		
		return ret;
	}
	
	/**
	 * Metodo para decodificar un codigo de usuario.
	 * @param user Usuario a decodificar
	 * @return Usuario decodificado
	 * @throws BusinessException
	 */
	public String decodeUser(final String user) throws BusinessException {
		log.debug("[UserDao.decodeUser] Usuario encriptado: " + user);
		List resultDesencriptar = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						String queryDesencriptar = "select PK_MOD_SEGURIDAD.FT_DESENCRIPTAR('"
								+ user + "') FROM dual";
						Query query = session.createSQLQuery(queryDesencriptar);
						return query.list();
					}
				});

		String strUsuario = null;
		if (resultDesencriptar.size() == 1) {
			strUsuario = (String) resultDesencriptar.get(0);
		}
		
		return strUsuario;
	}

	@Override
	public void load(User userFinal) throws BusinessException, HibernateException, SQLException {
		// cargar los datos del usuario
		// usando modulos PL/SQL
		
		log.info("UserDao (load) Versión 1.1");

		usuario = userFinal;

		if (usuario.getIdEntidad() == null || usuario.getIdEntidad().equals("")) {
			List resultEntidad = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							String queryEntidad = "select PK_MOD_SEGURIDAD.FT_ENTIDAD('"
									+ usuario.getIdUsuario() + "') from dual";
							Query query = session.createSQLQuery(queryEntidad);
							return query.list();
						}
					});

			if (resultEntidad != null && resultEntidad.size() == 1) {
				String idEntidad = (String) resultEntidad.get(0);
				usuario.setIdEntidad(idEntidad);
			}
		}
		/* P0063701 ** MODIF TAM (21.07.2021) ** Inicio */
		/* Por petición de RGA, solo se llama al modulo de seguridad para usuarios de perfil 2 */
		List resultUser = new ArrayList();
		
		/* Obtenemos primero el perfil del usuario */
		List resultPerfil = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String queryPerfil = "select PK_MOD_SEGURIDAD.FT_PERFIL('" + usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + "') from dual";
						Query query = session.createSQLQuery(queryPerfil);
						return query.list();
					}
				});
		
		if (resultPerfil != null && resultPerfil.size() == 1) {
			String perfil = (String) resultPerfil.get(0);
			usuario.setPerfil(perfil);
		}
		
		if(Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil()) ){
			resultUser = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							StringBuffer templateQuery = new StringBuffer();
							templateQuery.append("select PK_MOD_SEGURIDAD.FT_NOMBRE('" + usuario.getIdUsuario() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_APELLIDOS('" + usuario.getIdUsuario() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_PERFIL('" + usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_NOMBRE_ENTIDAD('" + usuario.getIdEntidad() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_ZONAS_OFICINAS('" + usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + "', '" + usuario.getIdEntidad() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_ENCRIPTAR ('" + usuario.getIdUsuario() + "'),");
							/* Pet. 63701 ** MODIF TAM (08.06.2021) ** Inicio */
							templateQuery.append("PK_MOD_SEGURIDAD.FT_EMAIL ('" + usuario.getIdUsuario() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_ZONA ('" + usuario.getIdUsuario() + "', 'AGR')");
							/* Pet. 63701 ** MODIF TAM (08.06.2021) ** Fin */
							templateQuery.append(" FROM dual");
							log.info(templateQuery);
							Query query = session.createSQLQuery(templateQuery.toString());
							return query.list();
						}
					});
		}else {
			resultUser = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							StringBuffer templateQuery = new StringBuffer();
							templateQuery.append("select PK_MOD_SEGURIDAD.FT_NOMBRE('" + usuario.getIdUsuario() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_APELLIDOS('" + usuario.getIdUsuario() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_PERFIL('" + usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_NOMBRE_ENTIDAD('" + usuario.getIdEntidad() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_ZONAS_OFICINAS('" + usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + "', '" + usuario.getIdEntidad() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_ENCRIPTAR ('" + usuario.getIdUsuario() + "'),");
							templateQuery.append("PK_MOD_SEGURIDAD.FT_EMAIL ('" + usuario.getIdUsuario() + "')");
							templateQuery.append(" FROM dual");
							log.info(templateQuery);
							Query query = session.createSQLQuery(templateQuery.toString());
							return query.list();
						}
					});
		}
		

		if (resultUser.size() == 1) {
			log.info("Correct size");
			Object[] datosUsuario = (Object[]) resultUser.get(0);
			usuario.setNombre(((String) datosUsuario[0]).trim());
			usuario.setApellidos(((String) datosUsuario[1]).trim());
			String perfil = "";
			if (datosUsuario[2] != null) {
				perfil = ((String) datosUsuario[2]).trim();
			}
			usuario.setPerfil(perfil);
			usuario.setEntidad(((String) datosUsuario[3]).trim());

			/* Pet. 63701 ** MODIF TAM (08.06.2021) ** Inicio */
			if(Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil()) ){
				/* Guardamos el codigo de Zona recuperado en el modulo de Seguridad */
				String codZona = ((String) datosUsuario[7]).trim();
				usuario.setCodzona(codZona);
			}
			/* Pet. 63701 ** MODIF TAM (08.06.2021) ** Inicio */
			
			BigDecimal auxcodoficina = null;
			try{
				auxcodoficina = new BigDecimal(((String) datosUsuario[4]).trim());
			}catch(Exception e){
				auxcodoficina = new BigDecimal(9999);
			}
			//compruebo que existe la oficina en TB_OFICINAS y, si no existe, la insertamos
			OficinaId ofId = new OficinaId(new BigDecimal(usuario.getIdEntidad()), auxcodoficina);
			Oficina ofi = null;
			try {
				ofi = (Oficina) this.getObject(Oficina.class, ofId);
			}catch (Exception e) {
				log.error("Error al obtener la oficina " + auxcodoficina + " de la entidad " + (String) datosUsuario[4], e);
			}
			if (ofi == null){
				Oficina ofiInsertar = new Oficina();
				ofiInsertar.setId(ofId);
				ofiInsertar.setNomoficina("-");
				ofiInsertar.setPagoManual(Constants.CARGO_EN_CUENTA);
				
				try {
					ofi = (Oficina) this.saveOrUpdate(ofiInsertar);
				} catch (DAOException e) {
					log.error("Error al insertar la oficina " + auxcodoficina + " de la entidad " + (String) datosUsuario[4], e);
				}
			}
			
			usuario.setIdOficina(auxcodoficina+"");
			
			usuario.setUsuarioEncriptado(((String) datosUsuario[5]).trim());
			usuario.setEmail(((String) datosUsuario[6]).trim());
			usuario.setOficina(ofi.getNomoficina());
		}
	}
	
	/**
	 * Metodo para cargar el perfil de un usuario
	 * @param userFinal
	 * @throws BusinessException
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public void setPerfil(User userFinal) throws BusinessException, HibernateException, SQLException {
		// cargar los datos del usuario
		// usando modulos PL/SQL

		usuario = userFinal;

		List resultUser = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer templateQuery = new StringBuffer();
						templateQuery.append("select PK_MOD_SEGURIDAD.FT_PERFIL('" + 
								usuario.getIdUsuario() + "','" + ResourceBundle.getBundle("agp").getString("usuario.perfil") + 
								"') FROM dual");
						
						log.info(templateQuery);
						
						Query query = session.createSQLQuery(templateQuery.toString());
						return query.list();
					}
				});

		if (resultUser.size() == 1) {
			log.info("Correct size");
			String perfil = (String) resultUser.get(0);
			usuario.setPerfil(perfil);
		}
	}
	
	public boolean existeOficina(Oficina oficina) throws DAOException {
		Session session = obtenerSession();
		try {
			
			Query query = session.createQuery("select count(*) from Oficina o where o.id.codentidad = :entidad and o.id.codoficina = :oficina")
																									.setBigDecimal("entidad", oficina.getId().getCodentidad())
																									.setBigDecimal("oficina", oficina.getId().getCodoficina());
			Long res = (Long) query.uniqueResult();
			
			if(res == 0)
				return false;
			else
				return true;
			
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	public Medida getMedida(Long lineaseguroid, String nifcif)throws DAOException {
		Medida medida = null;
		try {

			MedidaId medidaId = new MedidaId(lineaseguroid, nifcif);
			medida = (Medida)this.getObject(Medida.class, medidaId);
			if (medida ==null){
				medidaId = new MedidaId(lineaseguroid, "-");
				medida = (Medida)this.getObject(Medida.class, medidaId);
			}
			return medida;
			
				
			
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error al obtener la medida del usuario", ex);
		}
	}
	
	public HashMap<String, Object> checkPermisosLogin(){
		HashMap<String, Object> permisos = new HashMap<String, Object>();
		ConfigAgp checkUsuario = null;
		ConfigAgp checkAplicacion = null;
		Session session = obtenerSession();
		
		Criteria criteria = session.createCriteria(ConfigAgp.class);
		criteria.add(Restrictions.eq("agpNemo", "USUARIO_ACCESO"));
		checkUsuario = (ConfigAgp)criteria.uniqueResult();
		permisos.put("checkUsuario", checkUsuario);
		
		Criteria criteria2 = session.createCriteria(ConfigAgp.class);
		criteria2.add(Restrictions.eq("agpNemo", "APLICACION_CERRADA"));
		checkAplicacion = (ConfigAgp) criteria2.uniqueResult();
		permisos.put("checkAplicacion", checkAplicacion);
		
		return permisos;
	}
	
	/**
	 * Devuelve el objeto Usuario asociado al codUsuario indicado si es externo
	 * @param codUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioExterno (String codUsuario) {
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Usuario.class);			

			criteria.createAlias("oficina", "oficina");
			criteria.createAlias("oficina.entidad", "entidad");		
			
			criteria.add(Restrictions.eq("codusuario", codUsuario));
			
			if (codUsuario.startsWith("E")) { 
				criteria.add(Restrictions.or(
						Restrictions.eq("entidad.codentidad", BigDecimal.valueOf(3081)),
						Restrictions.eq("externo", Constants.USUARIO_EXTERNO)
						));
			} else {
				criteria.add(Restrictions.eq("externo", Constants.USUARIO_EXTERNO));
			}
			
			List<Usuario> list = criteria.list();
			if (list != null && list.size() >0) {
				Usuario u = list.get(0);
				u.setPerfil("AGR-" + u.getTipousuario());
				return u;
			}
			
		}catch (Exception e) {
			logger.error("Se ha producido un error al obtener el usuario externo", e);
		}
		
		return null;
	}
	
	/**
	 * Devuelve el objeto Usuario asociado al codUsuario (NEW LOGIN)
	 * @param codUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Usuario getUsuarionNewLogin (String codUsuario) {
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Usuario.class);
			criteria.add(Restrictions.eq("codusuario", codUsuario));
			List<Usuario> list = criteria.list();
			if (list != null && list.size() >0) {
				Usuario u = list.get(0);
				u.setPerfil("AGR-" + u.getTipousuario());
				return u;
			}
			
		}catch (Exception e) {
			logger.error("getUsuarionNewLogin - Se ha producido un error al obtener el usuario ", e);
		}
		
		return null;
	}
	
	/**
	 * Devuelve el nombre que usaremos para recuperar el usuario de la cabecera HTTP (NEW LOGIN)
	 * @param nombreUsuario
	 * @return
	 */
			public String getNombreUsuario() {
				Session session = obtenerSession();
				try {
					String strCont = "select agp_valor from o02agpe0.Tb_Config_Agp where agp_nemo='NOMBRE_USUARIO'";
					String nombreUser = (String) session.createSQLQuery(strCont).uniqueResult();				
					if (nombreUser != null)
						return nombreUser;
					else
						return null;
				} catch (Exception ex) {
					logger.debug(" Error al recoger el nombreUsuario para el nuevo login : ",ex );
					return null;
				}
			}
}