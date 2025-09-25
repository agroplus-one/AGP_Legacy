package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.model.user.User;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.SecureRequest;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.models.commons.EntidadDao;
import com.rsi.agp.dao.models.commons.IUserDao;
import com.rsi.agp.dao.models.mtoinf.IMtoEntidadesAccesoRestringidoDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinaId;
import com.rsi.agp.dao.tables.commons.PermisosInformes;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public class UserManager implements IManager, AuthenticationManager, UserDetailsService, AuthenticationProvider {

	public boolean supports(@SuppressWarnings("rawtypes") Class arg0) {
		return false;
	}

	/** The log. */
	protected final Log log = LogFactory.getLog(getClass());

	private IUserDao usuarioDao;
	private GenericDao<EntidadDao> entidadDao;
	private IMtoEntidadesAccesoRestringidoDao mtoEntidadesAccesoRestringidoDao;
	
	public boolean checkLogged() {
		return false;
	}
	
	public Authentication authenticate(Authentication arg0)	throws AuthenticationException {
		return null;
	}

	public Usuario getUser(String idUsuario){
		return (Usuario) usuarioDao.getObject(Usuario.class, idUsuario);	
	}
	
	/**
	 * Metodo para comprobar si un usuario tiene acceso a la aplicacion.
	 * @param user Usuario.
	 * @return true en caso de que el usuario tenga permiso. En cualquier otro caso devolvera false o lanzara una excepcion.
	 * @throws BusinessException
	 * @throws NumberFormatException
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public boolean hasAccess(User user) throws BusinessException, NumberFormatException, HibernateException, SQLException {
		User loggedUser = null;
		try{
			loggedUser = usuarioDao.login(user);
			if (loggedUser != null){
				log.debug("Usuario logeado");
				return true;
			}
		}
		catch (HibernateException e) {
			log.fatal("Error al cargar los datos del usuario", e);
			throw new HibernateException("Error al cargar los datos del usuario");
		} catch (SQLException e) {
			log.fatal("Error al cargar los datos del usuario", e);
			throw new SQLException("Error al cargar los datos del usuario");
		}catch(Exception e){
			log.fatal("Error durante la comprobacion de acceso del usuario", e);
			throw new BusinessException("El usuario no existe o no tiene acceso en Agroplus");
		}
		return false;
	}
	

	/**
	 * Devuelve un objeto Usuario si el usuario es externo y valido
	 * @param usuario
	 * @return
	 * @throws BusinessException 
	 */
	private Usuario isUsuarioExterno (String idUser) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioDao.getUsuarioExterno(idUser);
		
		// Si el codigo de usuario existe en la BD como externo continua con las validaciones para comprobar que es apto para el acceso
		if (usuario != null) {
			log.info("El usuario externo " + usuario.getCodusuario() + " existe en el sistema");
			
			// Comprueba si tiene perfil 1 o 3 
			if (!new BigDecimal (Constants.COD_PERFIL_1).equals(usuario.getTipousuario()) && 
				!new BigDecimal (Constants.COD_PERFIL_3).equals(usuario.getTipousuario())) {
				log.error("El usuario " + usuario.getCodusuario() + " no tiene un perfil permitido para usuario externo, no se permite el acceso.");
				throw new UsernameNotFoundException("El usuario no tiene un perfil permitido para usuario externo");
			}
			
			// Comprueba si tiene asignada la entidad, entidad mediadora y subentidad mediadora 
			if (usuario.getOficina() != null && usuario.getSubentidadMediadora() != null && usuario.getOficina().getEntidad() != null &&
				usuario.getSubentidadMediadora().getId() != null && usuario.getSubentidadMediadora().getId().getCodentidad() != null &&
				usuario.getSubentidadMediadora().getId().getCodsubentidad() != null) {
				// IGT 26/06/2014
				// Comprueba que la subentidad mediadora este activa
				if (usuario.getSubentidadMediadora().getFechabaja() != null) {					
					log.error("El usuario externo "
							+ usuario.getCodusuario()
							+ " está asociado a una subentidad dada de baja, no se permite el acceso.");
					throw new UsernameNotFoundException(
							"El usuario externo está asociado a una subentidad dada de baja, no se permite el acceso.");
				}
				// Comprobamos que el usuario, si es de tipo 3, 
				// tenga delegación asociada
				if (new BigDecimal(Constants.COD_PERFIL_3).equals(usuario
						.getTipousuario()) && usuario.getDelegacion() == null) {
					log.error("El usuario externo "
							+ usuario.getCodusuario()
							+ " no está asociado a una delegación, no se permite el acceso.");
					throw new UsernameNotFoundException(
							"El usuario externo no está asociado a una delegación, no se permite el acceso.");
				}			
				// El usuario externo es correcto, se devuelve el objeto
				// para permitir su acceso
				return usuario;
			} else {
				log.error("El usuario externo " + usuario.getCodusuario() + " no tiene correctamente asignadas la entidad, entidad mediadora" +
						  "y subentidad mediadora, no se permite el acceso.");
				
				throw new UsernameNotFoundException("El usuario externo no tiene correctamente asignadas la entidad, entidad mediadora" +
						  							"y subentidad mediadora, no se permite el acceso.");
			}
		}
		else {
			log.info("El usuario " + idUser + " no existe en el sistema o no es externo, se procede a su validacion contra Guia");
		}
		
		return null;
	}
	
	public Usuario login(User user,Boolean newLogin) throws BusinessException, NumberFormatException, HibernateException, SQLException {
		log.info("UserManager (login) Version 1.1");
		
		log.info("Conectando el usuario " + user.getIdUsuario() + " desde la ip " + user.getIpUsuario());		
		
		// Comprueba si el usuario es externo (las validaciones se han realizado previamente en el metodo loadUserByUsername)
		// En caso afirmativo no se realizaran validaciones contra Guia y se permitira el acceso
		if (newLogin) {
			Usuario uNew = usuarioDao.getUsuarionNewLogin(user.getIdUsuario());
			if (uNew != null)
				return uNew;
			else
				return null;
		}
		
		Usuario uExt = usuarioDao.getUsuarioExterno(user.getIdUsuario());
		if (uExt != null) return uExt;
		
		// Se valida el usuario contra Guia
		Usuario usuario = new Usuario();
		try{
			usuarioDao.load(user);
			// recuperamos de BD el usuario
			usuario = (Usuario) usuarioDao.getObject(Usuario.class, user.getIdUsuario());
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setExterno(Constants.USUARIO_INTERNO);
				// Si el usuario no existe en la tabla por defecto se establece el indicador de PAC dependiendo del perfil
				usuario.setCargaPac(Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(user.getPerfil()) ? new BigDecimal (1) : new BigDecimal (0));
				usuario.setFinanciar(new BigDecimal(Constants.COD_PERFIL_0));
			}
			
			// Si el usuario tiene acceso a trave de Guia, pero pertenece a la entidad 9996 y el perfil es diferente de 0
    		// (usuarios externos) no puede acceder
    		if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(user.getPerfil()) &&
    			 (usuario.getOficina() != null && Constants.CODENT_RGA.equals(usuario.getOficina().getEntidad().getCodentidad()))) {
    			throw new BusinessException("El usuario no existe o no tiene acceso en Agroplus");
    		}
    		
    		/* Pet. 63701 ** MODIF TAM (17.06.2021) */
    		/* solo en el caso de que el usuario sea del perfil 2 */
   			/* Pasamos el codzona recuperado del módulo de seguridad */
    		if (Constants.PERFIL_USUARIO_JEFE_ZONA.equals(user.getPerfil())){
       			if (user.getCodzona() != null && !user.getCodzona().equals("")) {
       				BigDecimal codZona = new BigDecimal(user.getCodzona());
       				usuario.setCodzona(codZona);
       			}
    		}
    		/* Pet. 63701 ** MODIF TAM (17.06.2021) Fin */
			
		}
		catch (HibernateException e) {
			log.error("Error en el login de usuario", e);
			throw new HibernateException("Error al cargar los datos del usuario");
		} catch (SQLException e) {
			log.error("Error en el login de usuario", e);
			throw new SQLException("Error al cargar los datos del usuario");
		}catch(Exception e){
			log.error("Error en el login de usuario", e);
			throw new BusinessException("El usuario no existe o no tiene acceso en Agroplus");
		}
		
		//En caso de tener acceso la entidad del usuario logado hacemos save or update
		Entidad entidad = new Entidad();
		entidad = (Entidad) entidadDao.getObject(Entidad.class, new BigDecimal(user.getIdEntidad()));
		
		if(entidad!=null){
			usuario.setPerfil(user.getPerfil());
			usuario.setCodusuario(user.getIdUsuario());
			usuario.setNombreusu(user.getNombre() + " " + user.getApellidos());
			if (user.getPerfil() != null && !user.getPerfil().equals(""))
				usuario.setTipousuario(new BigDecimal(user.getPerfil().substring(4)));
			
			// DAA 27/02/2013	Obtenemos el email
			usuario.setEmail(user.getEmail());
			
			Oficina oficina = new Oficina();
			try {
				oficina = new Oficina(new OficinaId(new BigDecimal(
						user.getIdEntidad()), new BigDecimal(
								user.getIdOficina())), new Entidad(
						new BigDecimal(user.getIdEntidad()),
						user.getEntidad()), user.getOficina(), new BigDecimal (0));
				usuario.setOficina(oficina);
			} catch (Exception e) {
				throw new NumberFormatException("La oficina a la que pertenece el usuario no existe en Agroplus");
			}
			try{ 
				if(!usuarioDao.existeOficina(oficina)){
					oficina.setNomoficina("Oficina " + oficina.getId().getCodentidad().toString() + "-" +  oficina.getId().getCodoficina());
					usuarioDao.saveOrUpdate(oficina);
				}
				usuarioDao.saveOrUpdate(usuario);
			}catch(Exception e){
				this.log.error("Error al guardar los datos de la oficina", e);
			}
			
		}else{
			throw new BusinessException("La entidad a la que pertenece el usuario no existe o no tiene acceso en Agroplus");
		}
		
		return usuario;

	}

	
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException, DataAccessException {
    	
    	log.debug("loadUserByUsername");
		
    	String encriptedUser = SecureRequest.secureString(username, false, true, 50);
		
    	if ((encriptedUser != null && !encriptedUser.equals(""))) {
		    
    		User user = new User();		    
		    user.setIdUsuario(encriptedUser);   		    		    	

		    try {
		    	// Si es un usuario externo valido
		    	Usuario u = isUsuarioExterno (encriptedUser);
		    	if (u != null) {
		    		GrantedAuthority extIntAuth;
					if (u.getCodusuario().startsWith("E") && BigDecimal.valueOf(3081)
							.equals(u.getSubentidadMediadora().getEntidad().getCodentidad())) {
						extIntAuth = new GrantedAuthorityImpl("INTERNO");
					} else {
						extIntAuth = new GrantedAuthorityImpl("EXTERNO");
					}
					return new org.springframework.security.userdetails.User(user.getIdUsuario(), "", true, true, true,
							true,
							new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_" + u.getTipousuario()), extIntAuth,
									// Comprueba si el usuario externo tiene permiso para cargar la PAC
									new GrantedAuthorityImpl(
											new BigDecimal(1).equals(u.getCargaPac()) ? "ROLE_PAC" : "ROLE_NOPAC"), });
		    	}
		    	// Si es un usuario interno que tiene acceso por Guia
		    	else if (this.hasAccess(user)) {
		    		log.debug("Entra por tiene permisos");
		    		return  new org.springframework.security.userdetails.User(user.getIdUsuario(), 
				    			"", true, true, true, true, 
				    			new GrantedAuthority[]{
		    						new GrantedAuthorityImpl("ROLE_"+user.getPerfil().substring(4)), 
		    						new GrantedAuthorityImpl("INTERNO"),
		    						// Comprueba si el usuario interno tiene permiso para cargar la PAC
		    						new GrantedAuthorityImpl(getPermisoPAC(encriptedUser, user))
		    						});
				}else {
					log.error("El Usuario no tiene acceso a Agroplus");
					throw new UsernameNotFoundException("El Usuario no tiene acceso a Agroplus");
				}
		    
		    }catch (BusinessException e) {
		    	log.error("Se ha producido un error al recuperar los datos del usuario", e);
		    	throw new UsernameNotFoundException("Se ha producido un error al recuperar los datos del usuario: " + e.getMessage());
		    } catch (NumberFormatException e) {
		    	log.error("Se ha producido un error al recuperar los datos del usuario", e);
		    	throw new DataIntegrityViolationException("Se ha producido un error al recuperar los datos del usuario: La oficina a la que pertenece el usuario no es correcta no existe en Agroplus.");
		    } catch (HibernateException e) {
		    	throw new DataIntegrityViolationException("Se ha producido un error al recuperar los datos del usuario " + e.getMessage());
			} catch (SQLException e) {
		    	throw new DataIntegrityViolationException("Se ha producido un error al recuperar los datos del usuario " + e.getMessage());
			}
		
		} else {
			log.debug("## NUEVO ACCESO LOGIN ##");  
			return new org.springframework.security.userdetails.User(" ", "", true, true, true, true,
					new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_" + "0"),
							new GrantedAuthorityImpl("EXTERNO") });
		}
		
    }
	/**
	 * @param encriptedUser
	 * @param user
	 * @return
	 */
	private String getPermisoPAC(String encriptedUser, User user) {
		try {
			String grantPAC;
			if ("0".equals(user.getPerfil().substring(4))) {
				grantPAC = "ROLE_PAC";
			}
			else {
				log.debug("Se busca el usuario " + encriptedUser + " para comprobar si puede cargar PAC");
				Usuario usuInt = this.usuarioDao.getUsuarionNewLogin(encriptedUser);
				grantPAC = usuInt == null ? "ROLE_NOPAC" : (new BigDecimal (1).equals(usuInt.getCargaPac()) ? "ROLE_PAC" : "ROLE_NOPAC");
			}
			return grantPAC;
		} 
		catch (Exception e) {
			log.error("Error al obtener el indicador de la PAC"	, e);
			return "ROLE_NOPAC";
		}
	}
    
    public Medida getMedida(Long lineaseguroid, String nifcif) throws BusinessException {
    	try {
    	    return usuarioDao.getMedida(lineaseguroid, nifcif);
    	}catch(Exception ex){
    		log.error("Se ha producido un error al recuperar los datos del usuario", ex);
    		throw new BusinessException("Se ha producido un error al obtener la medida del usuario", ex);
    	}
    }

	public String encodeUser(String user) throws BusinessException {
		return this.usuarioDao.encodeUser(user);
	}
	
	public String decodeUser(String user) throws BusinessException {
		return this.usuarioDao.decodeUser(user);
	}

	public void setEntidadDao(GenericDao<EntidadDao> entidadDao) {
		this.entidadDao = entidadDao;
	}

	public void setUsuarioDao(IUserDao usuarioDao) {
		this.usuarioDao = usuarioDao;
	}
	
	/**
	 * funcion que comprueba si la aplicacion esta abierta, en caso de que no lo este, se comprueba si el
	 * usuario introducido tiene privilegios de login.
	 * @param Usuario usuario introducido
	 * @return permisoLogin
	 */
	public boolean checkPermisosLogin(Usuario usuario){
		boolean permisoLogin= false;
		ConfigAgp checkUsuario = null;
		ConfigAgp checkAplicacion = null;
		HashMap<String, Object> permisos = this.usuarioDao.checkPermisosLogin();
		if (permisos!=null){
			checkUsuario = (ConfigAgp) permisos.get("checkUsuario");
			checkAplicacion = (ConfigAgp) permisos.get("checkAplicacion");
			if (checkAplicacion!=null && checkAplicacion.getAgpValor()!=null){
				if (checkAplicacion.getAgpValor().equals("NO")){
					permisoLogin=true;
				}else{
					if (checkUsuario!=null && checkUsuario.getAgpValor()!=null){
						String[] usuariosPermitidos = checkUsuario.getAgpValor().split(",");
						for (String usuPerm : usuariosPermitidos){
							if (usuario.getCodusuario().equals(usuPerm)){
								permisoLogin=true;
								break;
							}
						}
					}
				}
			}
		}
		return permisoLogin;
	}

	/**
	 * Carga en el usuario las posibles restricciones de acceso al modulo de informes por entidad
	 * @param u
	 */
	public void cargaPermisosPerfiles (Usuario u) {
		
		EntidadAccesoRestringido entidadAccesoRestringidos = null;
		
		// Carga el acceso restringido de la entidad asociada al usuario
		try {
			@SuppressWarnings("unchecked")
			List<EntidadAccesoRestringido> lista = (List<EntidadAccesoRestringido>) mtoEntidadesAccesoRestringidoDao.getObjects(EntidadAccesoRestringido.class, "entidad.codentidad", u.getOficina().getEntidad().getCodentidad());
			
			if (lista != null && lista.size()>0) entidadAccesoRestringidos = lista.get(0);
		}
		catch (Exception e) {
			log.error("Ocurrio un error al cargar el acceso restringido de la entidad asociada al usuario", e);
		}
		
		// Si existe el acceso restringido, lo carga en el usuario
		if (entidadAccesoRestringidos != null) {
			u.setPermisosInformes(new PermisosInformes (entidadAccesoRestringidos));
		}
		
	}
	
	public String getNombreUsuario() {
		String nombreUsuario = "";
		nombreUsuario = usuarioDao.getNombreUsuario();
		return nombreUsuario;
	}
	
	public void setMtoEntidadesAccesoRestringidoDao(IMtoEntidadesAccesoRestringidoDao mtoEntidadesAccesoRestringidoDao) {
		this.mtoEntidadesAccesoRestringidoDao = mtoEntidadesAccesoRestringidoDao;
	}
}