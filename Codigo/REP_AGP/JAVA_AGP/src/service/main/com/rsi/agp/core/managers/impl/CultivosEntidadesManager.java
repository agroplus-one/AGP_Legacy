 package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.models.comisiones.ICultivosEntidadesDao;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.DetalleComisionEsMediadora;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;

public class CultivosEntidadesManager implements IManager {
	private static final Log logger = LogFactory.getLog(CultivosEntidadesManager.class);
	private ICultivosEntidadesDao cultivosEntidadesDao;
	private CultivosEntidadesHistoricoManager cultivosEntidadesHistoricoManager;
	private ILineaDao lineaDao;
	private IPolizasPctComisionesDao polizasPctComisionesDao;
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	/**
	 * Metodo que devuelve la ultima comision cultivo entidad
	 * @param year 
	 * @return
	 * @throws BusinessException
	 */
	public CultivosEntidades getLastCultivosEntidades() throws BusinessException{
		logger.debug("init - getLastCultivosEntidades");
		List<CultivosEntidades> listCultivosEntidades = null;
		try {
			
			listCultivosEntidades = cultivosEntidadesDao.getLastCultivosEntidades(false);			
			logger.debug("end - getLastCultivosEntidades");
						
			return listCultivosEntidades.get(0);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar la ultima comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar la ultima comision por cultivo entidad",dao);
		}
	}
	
	/**
	 * Metodo que devuelve la ultima comision cultivo entidad
	 * @param year 
	 * @return
	 * @throws BusinessException
	 */
	public CultivosEntidades getLastCultivosEntidades(BigDecimal year) throws BusinessException{
		logger.debug("init - getLastCultivosEntidades");
		List<CultivosEntidades> listCultivosEntidades = null;
		try {
			
			listCultivosEntidades = cultivosEntidadesDao.getLastCultivosEntidades(year);
			
			logger.debug("end - getLastCultivosEntidades");
			if (listCultivosEntidades.size() > 0){
				return listCultivosEntidades.get(0);
			} else {
				return null;
			}
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar la ultima comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar la ultima comision por cultivo entidad",dao);
		}
	}
	
	/**
	 * Metodo que devuelve una comision cultivo entidad
	 * @param idCultivosEntidades
	 * @return
	 * @throws BusinessException
	 */
	public CultivosEntidades getCultivosEntidades(Long idCultivosEntidades) throws BusinessException {
		logger.debug("init - getCultivosEntidades");
		CultivosEntidades cultivosEntidades = null;
		try {
			
			cultivosEntidades = (CultivosEntidades) cultivosEntidadesDao.get(CultivosEntidades.class, idCultivosEntidades);
			
			logger.debug("end - getCultivosEntidades");
			return cultivosEntidades;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar la comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar la comision por cultivo entidad",dao);
		}
	}
	
	/**
	 * Metodo que devuelve un listado de todas las comisiones por cultivo
	 * @param cultivosEntidadesBean
	 * @return
	 * @throws BusinessException
	 */
	public List<CultivosEntidades> listComisionesCultivosEntidades(CultivosEntidades cultivosEntidadesBean) throws BusinessException {
		logger.debug("init - listComisionesCultivosEntidades");
		List<CultivosEntidades> listCultivosEntidades = null;
		try {
			
			listCultivosEntidades = cultivosEntidadesDao.listComisionesCultivosEntidades(cultivosEntidadesBean);
				
			logger.debug("init - listComisionesCultivosEntidades");
			return listCultivosEntidades;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de comisiones por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar el listado de comisiones por cultivo entidad",dao);
		}
	}
	
	/**
	 * Metodo que da de alta/update un nuevo parametro de comisiones
	 * @param cultivosEntidadesBean
	 * @throws Exception 
	 * @throws Exception 
	 */
	public 	void guardarParametrosComisiones(CultivosEntidades cultivosEntidadesBean) throws  BusinessException {
		logger.debug("init - guardarParametrosComisiones");
		try {
		//		FECHA DE MODIFICACION ACTUAL
				Date fecha = new Date();
				cultivosEntidadesBean.setFechamodificacion(fecha);
				cultivosEntidadesDao.saveOrUpdate(cultivosEntidadesBean);	
			logger.debug("end - guardarParametrosComisiones");
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar la comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar la comision por cultivo entidad",dao);
		}		
	}
	
	
	
	/**
	 * Método que borra un registro 
	 * @param id
	 * @throws BusinessException
	 */
	public void borrarParametrosComisiones(CultivosEntidades cultivosEntidades) throws BusinessException {
		logger.debug("init - borrarParametrosComisiones");		
		try {
			logger.debug("end - borrarParametrosComisiones");
			if (cultivosEntidades.getLinea().getCodplan() != null && cultivosEntidades.getLinea().getCodplan().intValue()<2015){				
				cultivosEntidadesDao.delete(cultivosEntidades);
				logger.debug("borrado físico del registro");
			}else{
				//FECHA DE BAJA ACTUAL
				Date fechaBaja = new Date();
				cultivosEntidades.setFechaBaja(fechaBaja);
				cultivosEntidadesDao.saveOrUpdate(cultivosEntidades);
				logger.debug("borrado lógico del registro");
			}
			
			logger.debug("end - borrarParametrosComisiones");
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al borrar la comision por cultivo entidad",dao);
			throw new BusinessException ("Se ha producido un error al borrar la comision por cultivo entidad",dao);
		}
		
	}
	
	/**
	 * Metodo que valida si para un determinado parametro de comision, esta asociado con algun mediador, 
	 * a traves de la comparacion de la linea del parametro de comision que se va a borrar
	 * @param cultivosEntidades
	 * @return
	 * @throws BusinessException
	 */
	public boolean hayComisionesAsociadas(CultivosEntidades cultivosEntidades) throws BusinessException {
		logger.debug("init - hayComisionesAsociadas");
		Long count = new Long(0);
		boolean resultado = false;
		try {
			if (cultivosEntidades.getLinea().getCodplan().intValue()<2015)
				count = cultivosEntidadesDao.comisionesAsociadas(cultivosEntidades.getLinea());
			
			if(count == 0)
				resultado = false;
			else
				resultado = true;
		
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar si hay comisiones asociadas: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar si hay comisiones asociadas",dao);
		}
		logger.debug("end - hayComisionesAsociadas");
		return resultado;
	}

	/**
	 * Metodo que comprueba si para el plan /linea introducidos existe algun registro
	 * @param cultivosEntidadesBean
	 * @return
	 * @throws BusinessException
	 */
	private boolean existeRegistro(CultivosEntidades cultivosEntidadesBean) throws BusinessException {
		logger.debug("init - existeRegistro");
		Integer count = null;
		boolean resultado = false;
		try {
			logger.debug("Validando si existe el registro en la base de datos. ");
			count = cultivosEntidadesDao.existeRegistro(cultivosEntidadesBean);
			
			if(count == 0)
				resultado = false;
			else
				resultado = true;
		
		} catch (DAOException dao) {
			logger.error("Se ha producido un error comprobar si existe el registro en la BBDD: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error comprobar si existe el registro en la BBDD",dao);
		}
		logger.debug("end - existeRegistro");
		return resultado;
	}
	
	/**
	 * Metodo que genera una comision cultivo por defecto para la linea 999
	 * @return
	 * @throws DAOException
	 * @throws BusinessException 
	 */
	public CultivosEntidades generarComisionLinea999() throws BusinessException {
		logger.debug("init - generarComisionLinea999");
		CultivosEntidades cultivosEntidades = new CultivosEntidades();		
		CultivosEntidades cultivosEntidadesAux = new CultivosEntidades();
		try {	
			cultivosEntidadesAux = this.getLastCultivosEntidades();		
			
			Linea linea = (Linea) cultivosEntidadesDao.getLineaseguroId(new BigDecimal(999), cultivosEntidadesAux.getLinea().getCodplan());
				
			cultivosEntidades.setPctgeneralentidad(new BigDecimal(100));
			cultivosEntidades.setPctrga(new BigDecimal(0));
			cultivosEntidades.setLinea(linea);
			
			logger.debug("end - generarComisionLinea999");
		}catch (DAOException dao) {
			logger.error("Se ha producido un error al generar los datos para insertar la linea generica 999: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al generar los datos para insertar la linea generica 999",dao);
		}
		return cultivosEntidades;
	}

	/**
	 * Metodo que comprueba si esta dada de alta, una comision para la lÃ­nea 999
	 * @param listcultivosEntidades
	 * @return
	 * @throws DAOException 
	 */
	public boolean compruebaLinea999() throws BusinessException {
		logger.debug("init - compruebaLinea999");
		boolean encontrado = false;		
		CultivosEntidades cultivosEntidades = new CultivosEntidades();
		CultivosEntidades cultivosEntidadesAux = new CultivosEntidades();
		
		try {
			logger.info("Se obtiene el ultimo parametro general para saber cual es el aÃ±o actual");
			cultivosEntidadesAux = this.getLastCultivosEntidades();
			
			List<CultivosEntidades>listcultivosEntidades = cultivosEntidadesDao.listComisionesCultivosEntidades(cultivosEntidades);
			
			Linea linea = (Linea) cultivosEntidadesDao.getLineaseguroId(new BigDecimal(999), cultivosEntidadesAux.getLinea().getCodplan());
			
			for(CultivosEntidades ce:listcultivosEntidades){
				if(ce.getLinea().equals(linea)){
					encontrado = true;
					break;
				}
			}
			logger.debug("end - compruebaLinea999");
		} catch (DAOException dao) {
			logger.error("Se ha producido un error comprobar si existe la linea generica 999 en BBDD: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error comprobar si existe la linea generica 999 en BBDD",dao);
		}
		return encontrado;
	}
	
	
	public CultivosEntidades getCultivoEntidadByPlanLinea(BigDecimal codplan, BigDecimal codlinea) throws BusinessException {
		logger.debug("init - getCultivoEntidadByPlanLinea");
		CultivosEntidades cultivosEntidades = new CultivosEntidades();
		
		try {
			
			cultivosEntidades = cultivosEntidadesDao.getCultivoEntidadByPlanLinea(codplan, codlinea);
				
			logger.debug("init - getCultivoEntidadByPlanLinea");
			return cultivosEntidades;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de comisiones por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar el listado de comisiones por cultivo entidad",dao);
		}	
	}
	
	
	public boolean existeComisionMaxima(String idplan, String idlinea) throws BusinessException {
		try {	
			return cultivosEntidadesDao.existeComisionMaxima (idplan,idlinea);
		
		} catch (DAOException dao) {
			logger.error("Se ha producido un error en existeComisionMaxima: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error en existeComisionMaxima",dao);
		}	
	
	}
	
	public List<DetalleComisionEsMediadora> getListDetallePct(String codPlan,BigDecimal ent, BigDecimal subEnt,
			BigDecimal lineaseguoId,BigDecimal codLin,List<DetalleComisionEsMediadora>listaDetPorGN, BigDecimal codLineaTemp) throws BusinessException {
		try {			
			List<DetalleComisionEsMediadora> listaDetalle= cultivosEntidadesDao.getListDetallePct (codPlan,ent,subEnt,lineaseguoId,codLin,codLineaTemp);
			if(listaDetalle.size()>0){
				for (DetalleComisionEsMediadora detalle : listaDetalle) {
					//Si el registro de detalle es de un grupo de negocio genérico, hay que quitar éste y desdoblarlo por todos
					// los grupos de negocio
					
					//JANV - 06-05-2016  Hay que comprobar que no esta ya paremetrizado cuando añadimos los desdoblados.
					if(detalle.getGrupoNegocio().equals(Constants.GRUPO_NEGOCIO_GENERICO)){
						List<GruposNegocio> listGn=cultivosEntidadesDao.getGruposNegocio();					
						
						for (GruposNegocio grNe: listGn) {
								if(noEstaParametrizado(listaDetalle,grNe)){
								DetalleComisionEsMediadora detGn=new DetalleComisionEsMediadora(detalle);
								detGn.setGrupoNegocio(grNe.getGrupoNegocio());
								detGn.setDescripcionGN(grNe.getDescripcion());
								listaDetPorGN.add(detGn);
							}
						}						
					}else{
						listaDetPorGN.add(detalle);						
					}
				}
			}	
			if(codLin.equals(Constants.CODLINEA_GENERICA)){
				limpiaListaDetalleComisiones(new BigDecimal(codPlan),ent, subEnt,codLin,listaDetPorGN);
			}
			return listaDetPorGN;
		} catch (DAOException dao) {
			logger.error("Se ha producido un error en getListDetallePct: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error en getListDetallePct",dao);
		}	
	}
	
	public List<Linea> getListLineasParamsGenByPlan (String codPlan){
		List<Linea> lstLineaseguroids=new ArrayList<Linea>();
		lstLineaseguroids = cultivosEntidadesDao.getListLineasParamsGenByPlan(codPlan);		
		return lstLineaseguroids;
	}
	
	private boolean noEstaParametrizado(
			List<DetalleComisionEsMediadora> listaDetalle, GruposNegocio grNe) {
		for (DetalleComisionEsMediadora detalle : listaDetalle) {
			if(detalle.getGrupoNegocio().charValue()==grNe.getGrupoNegocio().charValue()){
				return false;
			}
		}
		return true;
	}

	private void limpiaListaDetalleComisiones(BigDecimal codPlan,BigDecimal entMed, 
			BigDecimal subEntMed, BigDecimal codLin, 
			final List<DetalleComisionEsMediadora>listaDetPorGN) throws DAOException{
			//Comprobamos si existen registros específicos de subentidades y los quitamos de la lista
			List<CultivosSubentidades> culSubConcretos=cultivosEntidadesDao.getCultivosSubentidades(
				codPlan,entMed, subEntMed, null);
			List<Integer> indice = new ArrayList<Integer>();
			//int[] indice;
			//List<CultivosSubentidades> aux = new ArrayList<CultivosSubentidades>();
			//int ii=0;
			for (CultivosSubentidades cultSu : culSubConcretos) {
				for (int i = 0; i < listaDetPorGN.size(); i++) {
					if(listaDetPorGN.get(i).getCodlinea().equals(cultSu.getLinea().getCodlinea())){
						indice.add(i);
						//ii+=1;
					}
				}
			}
			
			for (int i = indice.size()-1; i >= 0; i--) {
				listaDetPorGN.remove(indice.get(i).intValue());
			}
	}
	
	
	@SuppressWarnings("rawtypes")
	public List<List> getParamsGeneralesByLineaseguroId(Long lineaseguoId, BigDecimal entMed,
			BigDecimal subentMed) throws BusinessException, Exception {
		
		List<List> lstParamsGen = new ArrayList<List>();
		List<String> temp = null;		
		try {
			List resultado=polizasPctComisionesDao.getParamsGen (lineaseguoId, entMed, subentMed);
			if(resultado!=null && resultado.size()>0){
				for(int x=0;x<resultado.size();x++){
					temp=new ArrayList<String>();
					temp.add((String) ((Object[]) resultado.get(x))[0].toString());
					temp.add((String) ((Object[]) resultado.get(x))[1].toString());
					temp.add((String) ((Object[]) resultado.get(x))[2].toString());
					temp.add((String) ((Object[]) resultado.get(x))[3].toString());
					temp.add(getGruposNegocioPorCodigo((String) ((Object[]) resultado.get(x))[3].toString()));
					lstParamsGen.add(temp);
				}	
			}
				return lstParamsGen;

		} catch (DAOException dao) {
			logger.error("Se ha producido un error en getParamsGeneralesByLineaseguroId: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error en getParamsGeneralesByLineaseguroId",dao);
		} catch (Exception ex) {
			logger.error("Error en getParamsGeneralesByLineaseguroId: " + ex.getMessage());
			throw new BusinessException ("Error en getParamsGeneralesByLineaseguroId",ex);
		}	
	}
	
	/** Metodo para actualizar un String con todos los Ids de comisiones segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param errorWsAccionBean
	 */
	public Map<String, Object> cambioMasivo(String listaIds, CultivosEntidades cultivosEntidadesBean, Usuario usuario) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			//recorro la lista de ids y cargo los objetos de bbdd y los voy modificando y si existe no lo guardo
			parameters = cultivosEntidadesDao.cambioMasivo(listaIds, cultivosEntidadesBean);

		if (parameters.get("alerta")!= null) {
			parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.edicion.KO"));
		}else{
			logger.debug("alta del registro en el historico");
			String[] ids = listaIds.split(";");
			CultivosEntidades cultEnt = new CultivosEntidades();
			for (int i=0; i<ids.length;i++){
				cultEnt = (CultivosEntidades) cultivosEntidadesDao.getObject(CultivosEntidades.class, new Long(ids[i]));
				if (cultEnt != null) {
					cultEnt.setUsuario(usuario);
					cultivosEntidadesDao.evict(cultEnt);
					cultivosEntidadesHistoricoManager.addResgitroHist(cultEnt,ComisionesConstantes.AccionesHistComisionCte.MODIFICACION);
				}else {
					logger.debug(" AVISO! No se ha encontrado ningun cultivoEntidad con id: "+ids[i]);
				}
			}	
		}
		return parameters;
		
		} catch (Exception e) {
			logger.error("Error al ejecutar el Cambio Masivo ", e);
		}
		return parameters;
	}
	
	//Utilizada para los checks de marcar/desmarcar Todo
	public String getListPolizasString(
			List<CultivosEntidades> listcultivosEntidades) {
		String listaids="";
		for(int i=0;i<listcultivosEntidades.size();i++){
			
			CultivosEntidades c = listcultivosEntidades.get(i);
			if(c.getFechaBaja()==null)
				listaids += c.getId()+";";
			
		}
		return listaids;
	}
	
	@SuppressWarnings("unchecked")
	public List<GruposNegocio> getListGruposDeNegocio() throws Exception {
		List<GruposNegocio>grNeg=null;
		try {
			grNeg = cultivosEntidadesDao.getObjects(GruposNegocio.class, null, null);
		} catch (Exception e) {
			throw e;
		}
		return grNeg;
	}
	
	public String getGruposNegocioPorCodigo(String codigo) throws Exception {
		String grNeg=null;
		try {
			grNeg = polizasPctComisionesDao.getDescripcionGrupoNegocio(codigo.charAt(0));
		} catch (Exception e) {
			throw e;
		}
		return grNeg;
	}
	
	// PROCEDIMIENTOS DE VALIDACIÓN DE LA COMISIÓN
	public ArrayList<Integer>  validaRegistro(CultivosEntidades cul){
		ArrayList<Integer> errList = new ArrayList<Integer>();
		try {
			logger.debug("Validación del registro.");
			if(!validaLineaCultivo(cul))
				errList.add(1);
			if(!grupoNegocioOk(cul)){
				errList.add(9);
				//res="Grupo de negocio erróneo para una línea agrícola. ";
			}
			if(!existeSubEntidadMediadora(cul)){
				errList.add(2);
				//res= res + "La subentidad mediadora no existe en el mantenimiento de subentidades mediadoras. ";
			}
			if(this.existeRegistro(cul)){
				//res= res + "El registro actual ya existe en la base de datos.";
				errList.add(8);
			}
			
		} catch (Exception e) {
			errList.add(10);
			//res="Error durante la validación de la comisión.";
			logger.debug("Error durante la validación de la comisión. - CultivosEntidadesManager.validaRegistro" , e);
		}
		return errList;
	}
	
	private Boolean grupoNegocioOk(CultivosEntidades cul) throws Exception{
		//las validaciones de obligatoriedad del campo se realizan en la jsp
		Boolean res=true;
		try {
			logger.debug("Validación del grupo de negocio del registro. ");
			if (null != cul.getLinea().getEsLineaGanadoCount()){
				if(cul.getLinea().getEsLineaGanadoCount()==0 && 
						!cul.getGrupoNegocio().getGrupoNegocio().toString().equals(new String("1"))){
					res=false;
				}
			}
		} catch (Exception e) {
			res=false;
			logger.debug("Error validando el grupo de negocio - CultivosEntidadesManager.grupoNegocioOk" , e);
			throw e;
		}
		return res;
	}
	
	private Boolean existeSubEntidadMediadora(CultivosEntidades cul) throws Exception{
		Boolean res=true;
		try {
			logger.debug("Validación de la subentidad mediadora del registro. ");
			if(null!=cul.getSubentidadMediadora().getId() && null!=cul.getSubentidadMediadora().getId().getCodentidad()){
				Integer numRegs= subentidadMediadoraDao.existeRegistro(cul.getSubentidadMediadora().getId().getCodentidad(), 
						cul.getSubentidadMediadora().getId().getCodsubentidad(),true, null);
				res=(numRegs>0);
			}
			
		} catch (Exception e) {
			res=false;
			logger.debug("Error validando la subentidad mediadora - CultivosEntidadesManager.existeSubEntidadMediadora" , e);
			throw e;
		}
		return res;
	}
	
	
	public void replicarPlanLineaCultivos(BigDecimal plan_origen, BigDecimal linea_origen, BigDecimal plan_destino, BigDecimal linea_destino) throws BusinessException {
		
		logger.debug("replicarPlanLineaCultivos  - init ");

		// TODo verificar que existe la linea de origen y la linea destino?

		if (!lineaDao.existeLinea(plan_origen, linea_origen)) {
			throw new BusinessException("El plan/linea de origen no existe.");
		}
		
		if (!lineaDao.existeLinea(plan_destino, linea_destino)) {
			throw new BusinessException("El plan/linea de destino no existe.");
		}
		
		
		try {
			
			// comprobar que no existen parametros para el plan/linea destino
			CultivosEntidades cultivoEntidad = cultivosEntidadesDao.getCultivoEntidadByPlanLinea(plan_destino, linea_destino);
			
			if (cultivoEntidad != null) {
				
				List<CultivosEntidades> listaCultivos = cultivosEntidadesDao.getCultivosEntidadByPlanLinea(plan_destino, linea_destino);
				
				for (CultivosEntidades c : listaCultivos) {
					if (c.getFechaBaja() == null) {
						logger.debug("ya existen parametros para el plan/linea destino ");
						throw new BusinessException("Ya existen parametros para el plan/linea destino");
					}
				}
				
				cultivosEntidadesDao.replicarCultivos(plan_origen, linea_origen, plan_destino, linea_destino);
				
			} else {
				// 	Si no existen entonces proceder a replicar
				cultivosEntidadesDao.replicarCultivos(plan_origen, linea_origen, plan_destino, linea_destino);
			}
		
		} catch (DAOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
		
		
		logger.debug("replicarPlanLineaCultivos  - end ");

		
	}
	
	/**
	 * Metodo de validacion de los datos introducidos de linea y entidad/subentidad
	 * @param cultivosSubentidadesBean
	 * @return 
	 * @throws DAOException
	 */
	private Boolean validaLineaCultivo(CultivosEntidades cultivosEntidades) throws DAOException {
		logger.debug("init - validacionComisionesCultivo");
		Boolean res=true;
//		CALCULAMOS LA LINEA INTRODUCIDA
		Linea linea = cultivosEntidadesDao.getLineaseguroId(cultivosEntidades.getLinea().getCodlinea(), cultivosEntidades.getLinea().getCodplan());
		
		if(linea == null){ 
			//DAA 30/10/2013  si no existe la linea y la linea a dar de alta es la linea generica se da de alta previamente
			if(cultivosEntidades.getLinea().getCodlinea().compareTo(Constants.CODLINEA_GENERICA)==0){
				linea = lineaDao.insertaLineaGenerica (cultivosEntidades.getLinea().getCodplan());
				if(linea != null){
					cultivosEntidades.setLinea(linea);
				}else{
					res=false;
				}
			}else{
				res=false;
			}
		}else{
			cultivosEntidades.setLinea(linea);
		}
		logger.debug("end - validacionComisionesCultivo");
		return res;
	}
	
	//FIN DE LOS PROCEDIMIENTOS DE VALIDACIÓN
	
	
	
	
	public ICultivosEntidadesDao getCultivosEntidadesDao() {
		return cultivosEntidadesDao;
	}

	public void setCultivosEntidadesDao(ICultivosEntidadesDao cultivosEntidadesDao) {
		this.cultivosEntidadesDao = cultivosEntidadesDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
	
	public void setCultivosEntidadesHistoricoManager(CultivosEntidadesHistoricoManager cultivosEntidadesHistoricoManager) {
		this.cultivosEntidadesHistoricoManager = cultivosEntidadesHistoricoManager;
	}

	public void setPolizasPctComisionesDao(
			IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}

	public ISubentidadMediadoraDao getSubentidadMediadoraDao() {
		return subentidadMediadoraDao;
	}

	public void setSubentidadMediadoraDao(
			ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}


	
}
