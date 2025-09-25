package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.poliza.AseguradoAutorizadoFiltro;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizadoSC;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.MedidaId;
import com.rsi.agp.dao.tables.cpl.gan.AseguradoAutorizadoGanado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.models.admin.IAseguradoDao;

@SuppressWarnings("unchecked")
public class CargaAseguradoManager implements IManager {
	
	private static final Log LOGGER = LogFactory.getLog(CargaAseguradoManager.class);
	
	private SeleccionPolizaManager seleccionPolizaManager;
	private UserManager userManager;
	final ResourceBundle bundle_agp = ResourceBundle.getBundle("agp");
	private IAseguradoDao aseguradoDao;
	
	public final Medida getMedida(final Long lineaSeguroId, final String nifAsegurado) {
		final MedidaId medidaId = new MedidaId(lineaSeguroId, nifAsegurado); 
		return (Medida) aseguradoDao.getObject(Medida.class, medidaId);
	}
	
	/**
	 * Nos devuelve una lista con los usuarios autorizados para los datos pasados.
	 * @param asegurado 
	 * @param lineaseguroid, codmodulo, nifasegurado, codcultivo, codvariedad, codgarantizado Lista de parametros para la búsqueda.
	 * @return Lista de usuarios autorizados.
	 */
	public List<AseguradoAutorizado> getUsuariosAutorizados (Asegurado asegurado, Long lineaSeguroId){
		LOGGER.debug("init - getUsuariosAutorizados");
		final AseguradoAutorizadoFiltro aseguradoAutorizadoFiltro = new AseguradoAutorizadoFiltro(lineaSeguroId, null, asegurado.getNifcif(), null, null, null);
		
		List<AseguradoAutorizado>  aseguradosAutorizados = aseguradoDao.getObjects(aseguradoAutorizadoFiltro);
		
		LOGGER.debug("end - getUsuariosAutorizados");
		return aseguradosAutorizados;		
	}
	
	public Usuario aseguradoCargadoUsuario(Asegurado aseg,Long lineaseguroid,String usuario) {
		try {
			
			return aseguradoDao.aseguradoCargadoUsuario(aseg,lineaseguroid,usuario);
			
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al comprobar que el asegurado no esta cargado por otro usuario",dao);
		}
		return null;
	}
	
	/**
	 * Metodo que desbloquea las polizas que han sido bloqueadas por un usuario
	 * @param aseguradoAnterior
	 * @param codUsuario
	 */
	public void DesbloquearPolizasUsuario(String codUsuario) {
		try {
			Poliza poliza = new Poliza(); 
			poliza.setBloqueadopor(codUsuario);
			List<Poliza> polizas = seleccionPolizaManager.getPolizas(poliza);
			
			for(Poliza pol : polizas){
				if(pol.getBloqueadopor() != null && pol.getBloqueadopor().equals(codUsuario)){
					pol.setBloqueadopor(null);
					pol.setFechabloqueo(null);
					
					seleccionPolizaManager.savePoliza(pol);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error al desbloquear las polizas",ex);
		}
	}
	
	public Integer getCountAseguradoAutorizado(final String nifcif, final Long lineaseguroid) {
		LOGGER.debug("init - getCountAseguradoAutorizadoNIF");
		Integer res = null;
		try {			
			if(nifcif != null){
				res = aseguradoDao.getCountAseguradosAutorizados(lineaseguroid,nifcif);
			}else{
				res = aseguradoDao.getCountAseguradosAutorizados(lineaseguroid,null);
			}
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al obterner el numero de asegurados autorizados; "  + dao);
		}
		LOGGER.debug("end - getCountAseguradoAutorizadoNIF");
		return res;
	}
	
	public Integer getCountAseguradoAutorizadoG(final String nifcif, final Long lineaseguroid) {
		LOGGER.debug("init - getCountAseguradoAutorizadoNIF");
		Integer res = null;
		try {			
			if(nifcif != null){
				res = aseguradoDao.getCountAseguradosAutorizadosG(lineaseguroid, nifcif);
			}else{
				res = aseguradoDao.getCountAseguradosAutorizadosG(lineaseguroid, null);
			}
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al obterner el numero de asegurados autorizados; "  + dao);
		}
		LOGGER.debug("end - getCountAseguradoAutorizadoNIF");
		return res;
	}
	
	public AseguradoAutorizadoGanado[] getAACGan(final String nifcif, final Long lineaseguroid) {
		AseguradoAutorizadoGanado[] res = null;
		LOGGER.debug("init - getAACGan");
		try {
			res = aseguradoDao.getAACGan(lineaseguroid, nifcif);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al obterner los datos del AAC: "  + dao);
		}
		LOGGER.debug("end - getAACGan");
		return res;
	}
	
	public AseguradoAutorizadoSC[] getAAC(final String nifcif, final Long lineaseguroid) {
		AseguradoAutorizadoSC[] res = null;
		LOGGER.debug("init - getAAC");
		try {
			res = aseguradoDao.getAAC(lineaseguroid, nifcif);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al obterner los datos del AAC: "  + dao);
		}
		LOGGER.debug("end - getAAC");
		return res;
	}
	
	public Boolean getcountOrigenInfo(Long lineaseguroid,BigDecimal usoAutorizContrat) {
		LOGGER.debug("init - getcountOrigenInfo");
		Integer count = null;
		Boolean resultado = false;
		try {
			count = aseguradoDao.getcountOrigenInfo(lineaseguroid,usoAutorizContrat);
			
		if(count != null && count > 0)
			resultado = true;	
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al obterner el numero de asegurados autorizados; "  + dao);
		}
		LOGGER.debug("end - getcountOrigenInfo");
		return resultado;
	}
	
	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	/**
	 * metodo para comprobar que el asegurado no lo tiene cargado otro usuario 
	 * @param idColectivo
	 * @param asegurado
	 * @return
	 */
	
	public boolean isAseguradoDisponible(String codusuario, Long idAsegurado){
		return aseguradoDao.chekAseguradoDisponible(codusuario, idAsegurado);
	}

	/**
	 * Metodo para obtener el usuario que tiene cargado el asegurado indicado como parametro
	 * @param id Identificador del asegurado
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getUsuarioAseguradoCargado(Long id) {
		List usuarios = aseguradoDao.getObjects(Usuario.class, "asegurado.id", id);
		if (usuarios.size() == 1){
			return ((Usuario)usuarios.get(0)).getCodusuario();
		}
		return "";
	}
	public Map<String, Object> cargaAsegurado(Asegurado aseg,HttpServletRequest request,Usuario usuario) throws Exception{
	Map<String, Object> parameters = new HashMap<String, Object>();
		
		try{ 
			LOGGER.debug("Asegurado obtenido de BD correctamente. Comprobamos la integridad del asegurado " + (aseg !=null ? aseg.getId() : ""));
			// comprobamos si el asegurado no esta cargado previamente por otro usuario
			boolean idAseguradoDisponible = (aseg != null) ? isAseguradoDisponible(usuario.getCodusuario(), aseg.getId()) : false;
			
			if (!idAseguradoDisponible){
				String codusuario = (aseg != null) ? getUsuarioAseguradoCargado(aseg.getId()) : null;
				LOGGER.debug("El asegurado " + (aseg !=null ? aseg.getId() : "") + " ya se encuentra cargado por el usuairo " + codusuario);
				parameters.put("alerta", "El asegurado ya se encuentra cargado por el usuario: " + codusuario);
			}
			else {
				// Comprobamos si hay que mirar la ultima fecha de revision del asegurado
				String validaAseg = aseguradoDao.validaCargaASegurado ();
				if (StringUtils.nullToString(validaAseg).equalsIgnoreCase("true")) {
					boolean cargaUsuario = validaFechaRevisionAseg (aseg);
					if (!cargaUsuario) {
						LOGGER.debug("El asegurado no ha sido revisado => mostrar mensaje");
						parameters.put("alerta", bundle_agp.getString("mensaje.asegurado.cargado.fechaRevision.KO"));
						return parameters;
					}
				}
				// Comprobamos la integridad de los datos del asegurado antes de cargarlo
				if((aseg != null) ? isAseguradoValidated(aseg) : false){
					
					LOGGER.debug("El asegurado " + (aseg !=null ? aseg.getId() : "") + " es valido");
					
					//Si el asegurado no ha sido revisado no se puede cargar y si el asegurado
					if(aseg != null && aseg.getRevisado() == 'S'){

						LOGGER.debug("Asegurado " + aseg.getId() + " revisado");
							
						//DESBLOQUEAMOS LAS POLIZAS DEL USUARIO AL CAMBIAR DE ASEGURADO
						DesbloquearPolizasUsuario(usuario.getCodusuario());
						LOGGER.debug("Se desbloquearon las polizas del asegurado " + aseg.getId());
							
						// asociamos el asegurado al usuario
						usuario.setAsegurado(aseg);
						usuario.setClase(null);
						// guardamos el usuario con el asegurado cargado
						guardarUsuario(usuario);
						LOGGER.debug("Guardamos usuario con el asegurado cargado");
							
						// P19876
						Long esGanado = usuario.getColectivo().getLinea().getEsLineaGanadoCount();									
						if (esGanado == 0) {
							// guardamos la nueva medida en sesion para lineas agricolas, al cambiar de asegurado
							Medida medida = new Medida();
							if(usuario.getColectivo() != null){
								Long lineaseguroid = usuario.getColectivo().getLinea().getLineaseguroid();
								if(usuario.getAsegurado() != null){
									String nifcif = aseg.getNifcif();
									
									medida = userManager.getMedida(lineaseguroid, nifcif);
								}
							}
							if (medida != null){
								LOGGER.debug("Medida del asegurado " + aseg.getId() + ": " + StringUtils.nullToString(medida.getTipomedidaclub()) + 
										", " + StringUtils.nullToString(medida.getPctbonifrecargo()));
							}
							else{
								medida = new Medida();
								LOGGER.debug("Sin Medida");
							}
							request.getSession().setAttribute("medida", medida);
						}else {// al ser de ganado la medida se carga en la clase. Eliminamos de la sesi�n la medida anterior
							request.getSession().removeAttribute("medida");
						}
						LOGGER.debug("Asegurado cargado OK");
						parameters.put("mensaje", bundle_agp.getString("mensaje.asegurado.cargado.OK"));
						
						// eliminamos de la sesion el coeficiente de reduccion de rendimientos
						request.getSession().removeAttribute("intervaloCoefReduccionRdto");
						
					}else{
						LOGGER.debug("El asegurado no ha sido revisado => mostrar mensaje");
						parameters.put("alerta", bundle_agp.getString("mensaje.asegurado.cargado.revisado.KO"));
					}
				}else{
					LOGGER.debug("El asegurado NO es valido => mostrar mensaje");
					parameters.put("alerta", bundle_agp.getString("mensaje.asegurado.cargado.valido.KO"));
				}
			}
			return parameters;
		}catch (BusinessException e) {
			LOGGER.error("Error en la carga de Asegurado" + e);
			throw new BusinessException();
		}catch (DAOException e) {
			LOGGER.error("Error de bbdd en la carga de Asegurado" + e);
			throw e;
		}catch (Exception e) {
			LOGGER.error("Error generico en la carga de Asegurado" + e);
			throw e;
		}
		
	}
	
	private void guardarUsuario(Usuario usuario) {
		try {
			aseguradoDao.saveOrUpdate(usuario);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al guardar el usuario", e);
		}
	}
	
	/**
	 * Validamos la fecha de revision del asegurado. Si esta vacia 
	 * o ha pasado mas de un a�o no dejamos cargar el asegurado
	 * 16/12/2014 U029769
	 * @param aseg
	 * @return boolean
	 */
	private boolean validaFechaRevisionAseg(Asegurado aseg) {
		
		if (aseg.getFechaRevision()== null) {
			return false;
		}
		
		int anios = DateUtil.getNumAnosEntreFechas(aseg.getFechaRevision());
		if (anios >0) {
			return false;
		}
		return true;
	}

	/**
	 * Funcion que valida la integridad de los datos del Asegurado segun el esquema
	 * @param asegurado
	 * @return true o false
	 */
	public boolean isAseguradoValidated(Asegurado asegurado){
		boolean isValidNombre = false;
		boolean isValidDireccion = false;
		boolean isValidDatosContacto = false;
		boolean isValidIdentificacion = false;
		
		boolean cumplePatron = false;
		boolean cumpleObligatorio = false;
		String patron = "";
		
//		Validacion de datos Personales - razon Social
		if(asegurado.getTipoidentificacion().equals("NIF") || asegurado.getTipoidentificacion().equals("NIE")){
			patron = "\\D{1,20}";
			cumpleObligatorio = !asegurado.getNombre().isEmpty();
			if(cumpleObligatorio){				
				cumplePatron = Pattern.matches(patron, asegurado.getNombre());
				if(cumplePatron){ 
					cumpleObligatorio = !asegurado.getApellido1().isEmpty();
					if(cumpleObligatorio){
						patron = "\\D{1,40}";
						cumplePatron = Pattern.matches(patron, asegurado.getApellido1());
					}
				}
			}
			
			isValidNombre = cumplePatron && cumpleObligatorio;
			
//			Validamos el NIF
			patron = "[LMKXY0-9]{1}[0-9]{0,7}[A-Z]{1}";
			cumpleObligatorio = !asegurado.getNifcif().isEmpty();
			cumplePatron = Pattern.matches(patron, asegurado.getNifcif());			
			isValidIdentificacion = cumplePatron && cumpleObligatorio;
			
		}else if(asegurado.getTipoidentificacion().equals("CIF")){
			
			patron = "[a-zA-Z|\u00e1-\u00fa|\u00c1-\u00da|\u00d1|\u00f1|\\s|0-9|.|,|ç| Ç|-]{1,50}";
			cumpleObligatorio = !asegurado.getRazonsocial().isEmpty();
			if(cumpleObligatorio){
				cumplePatron = Pattern.matches(patron, asegurado.getRazonsocial());				
			}			
			isValidNombre = cumplePatron && cumpleObligatorio;
			
//			Validamos el CIF
			patron = "([A-JN-WYZ]{1}[0-9]{1,7}[A-Z0-9]{1})";
			cumpleObligatorio = !asegurado.getNifcif().isEmpty();
			cumplePatron = Pattern.matches(patron, asegurado.getNifcif());				
			isValidIdentificacion = cumplePatron && cumpleObligatorio;
			
		}
			

//		Validacion de los datos de  direccion
		cumpleObligatorio = !asegurado.getDireccion().isEmpty();
		if(cumpleObligatorio){
			//patron = "\\D{1,25}";	
			//Los acentos y las ñ se escriben mediante su codigo unicode Ç|ç|,|-
			patron = "[a-zA-Z|\u00e1-\u00fa|\u00c1-\u00da|\u00d1|\u00f1|\u0020|\\s|0-9|,|ç| Ç|-]{1,22}"; 
			cumplePatron = Pattern.matches(patron,asegurado.getDireccion());
			if(cumplePatron){
				patron = "\\D{1,4}";
				cumpleObligatorio = asegurado.getVia() != null && asegurado.getVia().getClave() != null && !asegurado.getVia().getClave().isEmpty();
				if(cumpleObligatorio){
					cumplePatron = Pattern.matches(patron, asegurado.getVia().getClave());
					if(cumplePatron){
						cumpleObligatorio = asegurado.getNumvia() != null && !asegurado.getNumvia().isEmpty();
						if(cumpleObligatorio){
							patron = "[\u0020|0-9|a-z|A-Z|º/|ª/|,]{1,5}";
							cumplePatron = Pattern.matches(patron,asegurado.getNumvia());
							if(cumplePatron){		
								patron = "[\\w|\\s]{1,10}";
								String bloque = StringUtils.nullToString(asegurado.getBloque());
								if(!bloque.equals(""))
									cumplePatron = Pattern.matches(patron,bloque);
								else
									cumplePatron = true;
								if(cumplePatron){
									String escalera = StringUtils.nullToString(asegurado.getEscalera());
									if(!escalera.equals(""))								
										cumplePatron = Pattern.matches(patron,escalera);
									else
										cumplePatron = true;
									if(cumplePatron){
										cumpleObligatorio = FiltroUtils.noEstaVacio(asegurado.getLocalidad().getId().getCodprovincia());
										if(cumpleObligatorio){ 
											cumplePatron = (asegurado.getLocalidad().getId().getCodprovincia().precision() == 2 || asegurado.getLocalidad().getId().getCodprovincia().precision() == 1) && asegurado.getLocalidad().getId().getCodprovincia().intValue() >= 1 && asegurado.getLocalidad().getId().getCodprovincia().intValue() <= 50;
											if(cumplePatron){
												cumpleObligatorio = FiltroUtils.noEstaVacio(asegurado.getLocalidad().getId().getCodlocalidad());
												if(cumpleObligatorio){
													patron = "\\D{1,50}";
//													cumplePatron = Pattern.matches(patron, asegurado.getLocalidad().getNomlocalidad());
													cumplePatron = true;
													if(cumplePatron){
														cumpleObligatorio = !asegurado.getCodpostalstr().isEmpty();
														if(cumpleObligatorio){
															patron = "\\d{5}";
															cumplePatron = Pattern.matches(patron,asegurado.getCodpostalstr());
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		isValidDireccion = cumplePatron && cumpleObligatorio;
		
//		Validacion datos de contacto
		patron = "\\d{9}";
		String telefono = StringUtils.nullToString(asegurado.getTelefono());
		if(!telefono.equals(""))
			cumplePatron = Pattern.matches(patron, telefono);
		else
			cumplePatron = true;
		
		if(cumplePatron){		
			
			String movil = StringUtils.nullToString(asegurado.getMovil());
			if(!movil.equals(""))
				cumplePatron = Pattern.matches(patron, movil);
			else
				cumplePatron = true;
		}
		isValidDatosContacto = cumplePatron;
		
		
		return isValidNombre && isValidDireccion && isValidDatosContacto && isValidIdentificacion;
	}


	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * Metodo que refresca el asegurado de la sesion
	 * @param user
	 * @param aseguradoBean
	 */
	public void actualizaAseguradoSesion(Usuario user, Asegurado aseguradoBean) {
		
		if (user.getAsegurado() != null) {
			Asegurado AsegSession = user.getAsegurado();
			if (AsegSession.getId().equals(aseguradoBean.getId())) {
				LOGGER.debug("Actualizamos el asegurado de la sesion");
				user.setAsegurado(aseguradoBean);
				this.guardarUsuario(user);
			}
		}
		
	}
	
	public void setAseguradoDao(final IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}
}