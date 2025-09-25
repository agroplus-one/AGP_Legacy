package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.admin.TipoMediador;
import com.rsi.agp.dao.tables.admin.TipoMediadorAgro;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.commons.Usuario;

public class SubentidadMediadoraManager implements IManager {
	
	private static final Log logger = LogFactory.getLog(SubentidadMediadoraManager.class);

	private static final Character INFORME_MEDIADOR_BAJA = '2';
	private static final String VACIO = "";
	
	private ISubentidadMediadoraDao subentidadMediadoraDao;	
	
	/**
	 * Metodo que devuelve un listado de subentidades mediadoras
	 * @param subentidadMediadora
	 * @return
	 * @throws BusinessException
	 */
	public final List<SubentidadMediadora> listSubentidadesGrupoEntidad(final SubentidadMediadora subentidadMediadora) throws BusinessException {
		logger.debug("init - getSubentidadesGrupoEntidad");
		List<SubentidadMediadora> list = null;
		
		try {
				list =  subentidadMediadoraDao.listSubentidadesGrupoEntidad(subentidadMediadora);
				
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de subentidades mediadoras: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el listado de subentidades mediadoras",dao);
		}
		logger.debug("end - getSubentidadesGrupoEntidad");
		return list;
	}
	
	/**
	 * metodo que guarda o actualiza un registro de subentidades mediadoras en la BBDD
	 * @param subentidadMediadoraBean
	 * @throws BusinessException
	 */
	public List<Integer> guardarSubentidadMediadora(SubentidadMediadora subentidadMediadoraBean) throws BusinessException {
		logger.debug("init - guardarSubentidadMediadora");
		ArrayList<Integer> errList = null;
		
		try {
			subentidadMediadoraBean.setFechabaja(null);
			errList = this.validarSubentidadMediadora(subentidadMediadoraBean);
			
			if(errList.isEmpty()) {
				if(!Character.valueOf('1').equals(subentidadMediadoraBean.getPagodirecto())){
					subentidadMediadoraBean.setPagodirecto(Character.valueOf('0'));
				}
				subentidadMediadoraBean.setRetribucion(BigDecimal.ZERO);
				subentidadMediadoraBean.setTipoMediador(this.calcularTipoMediador(subentidadMediadoraBean));
				
				subentidadMediadoraBean.setTipoMediadorAgro(this.calcularTipoMediadorAgro(subentidadMediadoraBean));
				subentidadMediadoraDao.saveOrUpdate(subentidadMediadoraBean);
			}
				
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar la subentidad mediadora: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al guardar la subentidad mediadora",dao);
		}
		
		logger.debug("end - guardarSubentidadMediadora");
		return errList;
	}
	
	/**
	 * Obtiene el tipo de mediador asociado a la subentidad mediadora
	 * @param subentidadMediadora
	 * @return
	 * @throws BusinessException
	 */
	private TipoMediador calcularTipoMediador(SubentidadMediadora subentidadMediadora) throws BusinessException {
		logger.debug("init - calcularTipoMediador");
		TipoMediador tipoMediador = null;
		BigDecimal entidad = subentidadMediadora.getId().getCodentidad();
		BigDecimal subentidad = subentidadMediadora.getId().getCodsubentidad();
		try {
			tipoMediador = subentidadMediadoraDao.getTipoMediadorRGA(entidad, subentidad);
		} catch (Exception ex) {
			logger.error("Se ha producido un error al guardar obtener el tipo de mediador asociado.", ex);
		}
		
		logger.debug("end - calcularTipoMediador");
		return tipoMediador;
	}
	
	/**
	 * MÃ©todo para calcular el campo "TipoMedidas" de la subentidad mediadora. Se utiliza para enviarlo en el 
	 * campo "TipoMediador" de los xml de contratacion.
	 * @param subentidadMediadora
	 * @return
	 * @throws BusinessException
	 * @throws DAOException 
	 */
	private TipoMediadorAgro calcularTipoMediadorAgro(
			SubentidadMediadora subentidadMediadora) throws BusinessException,
			DAOException {
		logger.debug("init - calcularTipoMedidas");
		TipoMediadorAgro tipoMediadorAgro = null;
		BigDecimal entidad = subentidadMediadora.getId().getCodentidad();
		BigDecimal subentidad = subentidadMediadora.getId().getCodsubentidad();
		try {
			tipoMediadorAgro = subentidadMediadoraDao.getTipoMediadorAgro(entidad, subentidad);
		} catch (Exception ex) {
			logger.error("Se ha producido un error al guardar obtener el tipo de mediador asociado.", ex);
		}

		logger.debug("end - calcularTipoMedidas");
		return tipoMediadorAgro;
	}

	/**
	 * Metodo que borra un registro de subentidad mediadora de la BBDD
	 * @param subentidadMediadoraBean
	 * @throws BusinessException
	 */
	public void borrarSubentidadMediadora(SubentidadMediadora subentidadMediadoraBean) throws BusinessException {
		logger.debug("init - borrarSubentidadMediadora");
		
		try {
			
			Date fecha = new Date();
			subentidadMediadoraBean.setFechabaja(fecha);
			
			subentidadMediadoraDao.saveOrUpdate(subentidadMediadoraBean);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al borrar la subentidad mediadora: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al borrar la subentidad mediadora",dao);
		}
		
		logger.debug("end - borrarSubentidadMediadora");
	}
	
	public void modificarInformeMediadores(SubentidadMediadora subentidadMediadoraBean) throws BusinessException{
		logger.debug("init - modificarInformeMediadores");
		List<InformeMediadores> listInformeMediadores = null;
		try {
			listInformeMediadores = subentidadMediadoraDao.listInformeMediadoresBySubent(subentidadMediadoraBean);
			if (!listInformeMediadores.isEmpty()){
				for (InformeMediadores informeMediadores : listInformeMediadores){
					informeMediadores.setNuevo(INFORME_MEDIADOR_BAJA);
				}
				subentidadMediadoraDao.saveOrUpdateList(listInformeMediadores);
			}
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al borrar la subentidad mediadora: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al borrar la subentidad mediadora",dao);
		}
		
		logger.debug("end - modificarInformeMediadores");
		
	}

	
	/**
	 * Metodo que comprueba si existe ya el registros en la BBDD
	 * comprobando que no se dupliquen registros por la clave entidad, entidad mediadora y subentidad mediadora
	 * @param subentidadMediadoraBean
	 * @return
	 * @throws BusinessException 
	 */
	public boolean existeRegistro(	SubentidadMediadora subentidadMediadoraBean) throws BusinessException {
		logger.debug("init - existeRegistro");
		boolean isValid = false;
		Integer res = null;
		try {
			
			res = subentidadMediadoraDao.existeRegistro(subentidadMediadoraBean,false,subentidadMediadoraBean.getEntidad().getCodentidad());
			logger.debug("numero de registros: " + res);
			
			isValid = !Integer.valueOf(0).equals(res);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar los registros duplicados: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al comprobar los registros duplicados",dao);
		}
		
		logger.debug("end - existeRegistro");
		return isValid;
	}
	
	/**
	 * Metodo que valida si es posible dar de baja un subentidad Mediadora
	 * No se permitira eliminar un 3xxx-00 si hay algun registro con el codigo de entidad mediadora cuyos 3 ultimos digitos coincidan 
	 * con el indicado.
	 * @param subentidadMediadoraBean
	 * @return
	 * @throws BusinessException 
	 */
	public boolean isBajaOk(SubentidadMediadora subentidadMediadoraBean) throws BusinessException {
		logger.debug("init - isBajaOk");
		boolean isValid = true;
		List<SubentidadMediadora> list = null;
		try {
			
			logger.debug("comprobamos que no esta asociada a ningun colectivo");
			if(subentidadMediadoraBean.getColectivos().isEmpty()){
				
				logger.debug("comprobamos la condicion para ent med 3xxx- 00");
				if(subentidadMediadoraBean.getId().getCodentidad().compareTo(new BigDecimal(3000)) > 0 
						&& subentidadMediadoraBean.getId().getCodentidad().compareTo(new BigDecimal(3999)) < 0 ){
					
					if(subentidadMediadoraBean.getId().getCodsubentidad().compareTo(new BigDecimal(0)) == 0){

						list = this.listSubentidadesGrupoEntidad(new SubentidadMediadora());
						String last3 = subentidadMediadoraBean.getId().getCodentidad().toString().substring(1, subentidadMediadoraBean.getId().getCodentidad().toString().length());
						logger.debug("entidad 3xxx : "  + last3);
						for(SubentidadMediadora sm: list){
							
							if(sm.getEntidad() != null){
								String aux = sm.getEntidad().getCodentidad().toString().substring(1,sm.getEntidad().getCodentidad().toString().length());
								logger.debug("entidadAux 3xxx : "  + aux);
								if(last3.equals(aux)){
									isValid = false;
									break;
								}
							}
							
						}
						
					}
				}
				
			}else{
				isValid = false;
			}
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al comprobar los registros duplicados: " + be.getMessage());
			throw new BusinessException("Se ha producido un error al comprobar los registros duplicados",be);
		} 
		
		logger.debug("se permite la baja?: " + isValid);
		logger.debug("end - isBajaOk");
		return isValid;
	}
	
	/**
	 * Método que valida si tiene usuarios asociados la subentidad Mediadora
	 * @param subentidadMediadoraBean
	 * @return
	 */
	public boolean checkUsuariosSubentidad(SubentidadMediadora subentidadMediadoraBean) {
		logger.debug("init - checkUsuariosSubentidad");
		boolean tieneUsuarios = true;
		logger.debug("comprobamos si tiene asociados usuarios");
		Set<Usuario> usuarios = subentidadMediadoraBean.getUsuarios();
		if (usuarios != null && usuarios.isEmpty())
			tieneUsuarios = false;			
		logger.debug("Tiene usuarios asociados?: " + tieneUsuarios);
		logger.debug("end - checkUsuariosSubentidad");
		return tieneUsuarios;
	}
	
	public SubentidadMediadora obtenerSubentidadMediadora(String codEntidadMed, String codSubentidad) throws DAOException{

		SubentidadMediadora subentidadMediadora = null;
		
		if(codEntidadMed!=null && !VACIO.equals(codEntidadMed) && codSubentidad!=null && !VACIO.equals(codSubentidad)){
			SubentidadMediadoraId id = new SubentidadMediadoraId();
			id.setCodentidad(new BigDecimal(codEntidadMed));
			id.setCodsubentidad(new BigDecimal(codSubentidad));
			subentidadMediadora = (SubentidadMediadora)subentidadMediadoraDao.get(SubentidadMediadora.class, id);			
		}
		
		return subentidadMediadora;
	}
	
	/**
	 * Metodo de validacion de los datos introducidos 
	 * @param cultivosSubentidadesBean
	 * @return 
	 * @throws DAOException
	 * @throws BusinessException 
	 */
	private ArrayList<Integer> validarSubentidadMediadora(SubentidadMediadora subentidadMediadoraBean) throws BusinessException {
		logger.debug("init - validarSubentidadMediadora");
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
		Entidad entidad = null;
		try {
			logger.debug("validamos si la entidad no es nula");
			if (subentidadMediadoraBean.getEntidad().getCodentidad() == null){
				errList.add(1);
			} else {
				logger.debug("validamos entidad. ID: " + subentidadMediadoraBean.getEntidad().getCodentidad());
				entidad = (Entidad) subentidadMediadoraDao.get(Entidad.class, subentidadMediadoraBean.getEntidad().getCodentidad());
				if(entidad == null)
					errList.add(2);
			}		
			
			logger.debug("validamos entidad mediadora. ID: " + subentidadMediadoraBean.getId().getCodentidad());
			EntidadMediadora entidadMediadora = (EntidadMediadora) subentidadMediadoraDao.get(EntidadMediadora.class, subentidadMediadoraBean.getId().getCodentidad());
			// Si no existe la entidad mediadora, la damos de alta
			if(entidadMediadora == null) {
				entidadMediadora = new EntidadMediadora();
				entidadMediadora.setCodentidad(subentidadMediadoraBean.getId().getCodentidad());
				entidadMediadora.setNomentidad(entidad.getNomentidad());
				subentidadMediadoraDao.saveOrUpdate(entidadMediadora);
			}
			subentidadMediadoraBean.setEntidadMediadora(entidadMediadora);
			
			logger.debug("validamos si los tres ultimos digitos de entidad coinciden con los de entidad mediadora");		
			if (entidad != null){
				String codEntidadAux = entidad.getCodentidad().toString().substring(1);
				String codEntidadMediadoraAux = entidadMediadora.getCodentidad().toString().substring(1);
				if (!codEntidadAux.equals(codEntidadMediadoraAux)){
					errList.add(4);	
				}
			}
				
			
			logger.debug("validamos entidad mediadora. ID: " + subentidadMediadoraBean.getId().getCodentidad());
		
		}catch(DAOException dao) {
			logger.error("Se ha producido un error al validarSubentidadMediadora : " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al validarSubentidadMediadora",dao);
		}
		return errList;
	}
	
	public SubentidadMediadora getSubentidadMediadora(SubentidadMediadoraId id) throws BusinessException {
		logger.debug("init - getSubentidadMediadora");
		SubentidadMediadora sm = null;
		
		try {
			
			sm = (SubentidadMediadora) subentidadMediadoraDao.get(SubentidadMediadora.class, id);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar la subentidad mediadora: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar la subentidad mediadora",dao);
		}
		
		logger.debug("end - getSubentidadMediadora");
		return sm;
	}

	public String[] getColectivosUltPlanes(final BigDecimal codentidad, final BigDecimal codsubentidad)
			throws BusinessException {
		try {
			return this.subentidadMediadoraDao.getColectivosUltPlanes(codentidad, codsubentidad);
		} catch (DAOException e) {
			logger.error(
					"Se ha producido un error al recuperar los colectivos de los ultimos 2 planes: " + e.getMessage());
			throw new BusinessException(e);
		}
	}
	
	public void setSubentidadMediadoraDao(ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}
}
