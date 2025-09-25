package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.admin.impl.ColectivoFiltro;
import com.rsi.agp.dao.filters.admin.impl.EntidadFiltro;
import com.rsi.agp.dao.filters.admin.impl.TomadorFiltro;
import com.rsi.agp.dao.models.admin.IColectivoDao;
import com.rsi.agp.dao.models.admin.ITomadorDao;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.admin.TomadorId;
import com.rsi.agp.dao.tables.commons.Localidad;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.Via;

public class TomadorManager implements IManager {
	
	private static final Log LOGGER = LogFactory.getLog(TomadorManager.class);

	private ITomadorDao tomadorDao;
	private IColectivoDao colectivoDao;
	private IEntidadDao entidadDao;

	@SuppressWarnings("unchecked")
	public final List<Tomador> getTomadores(final Tomador tomadorBean) {
		final TomadorFiltro filter = new TomadorFiltro(tomadorBean);
		return tomadorDao.getObjects(filter);
	}
	
	public final List<Tomador> getTomadoresGrupoEntidad(final Tomador tomadorBean,List<BigDecimal> listaEnt) {
		try {
			return tomadorDao.getTomadoresGrupoEntidad(tomadorBean,listaEnt);
		} catch (DAOException dao) {
			LOGGER.error("Excepcion : TomadorManager - getTomadoresGrupoEntidad", dao);
		}
		return null;
	}

	public final Tomador getTomador(final BigDecimal codEntidad, final String cifTomador) {
		final TomadorId tomadorId = new TomadorId(codEntidad, cifTomador);
		return (Tomador)tomadorDao.getObject(Tomador.class, tomadorId);
	}
	
	/**
	 * Metodo que realiza el alta/modificacion de un tomador y hace las comprobaciones pertinentes
	 * @param tomadorBean
	 * @return un array de Integer con los codigos de errores detectados, en caso de que todo haya
	 * 			sido correcto devolvemos el mismo array con solo Int
	 */
	public final ArrayList<Integer> saveTomador(final Tomador tomadorBean,Usuario usuario) {
		ArrayList<Integer> error = null;
		try 
		{
			error = comprobarDatosGrabacion(tomadorBean);
			if (error.isEmpty()){
				//TMR 29-05-2012 facturacion
				tomadorBean.setFechaModificacion(new Date());
				tomadorBean.setUsuarioModificacion(usuario.getCodusuario());
				tomadorDao.saveOrUpdateFacturacion(tomadorBean,usuario);
				error.add(0);
			}
		} catch (Exception e) {
			LOGGER.error("Error al guardar el tomador: " + e.getMessage());
			if (error != null) error.add(10);
		}
		return error;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Integer> comprobarDatosGrabacion(final Tomador tomadorBean){
		final EntidadFiltro filtroEntidad = new EntidadFiltro(tomadorBean.getId().getCodentidad());
		List <Entidad> listaEntidades = entidadDao.getObjects(filtroEntidad);
		ArrayList<Integer> error = new ArrayList<>();
		 
		if (listaEntidades.isEmpty()){
		 error.add(1);
		}
		 List <Via>existeVia = tomadorDao.getObjects(Via.class, "clave", tomadorBean.getVia().getClave());
		 if (existeVia.isEmpty()){
			 error.add(2);
		 }	
		//Existe la provincia, localidad y la sublocalidad, comprobamos que la relacion de esos 3 campos sea correcta
		 Localidad existeRelacion = (Localidad) tomadorDao.getObject(Localidad.class, tomadorBean.getLocalidad().getId());
		 if (existeRelacion == null) {
			 error.add(6);
		 }
		 return error;		 
	}

	/**
	 * Esta funcion borrara el tomador referenciado, siempre y cuando este tomador NO tenga un colectivo 
	 * que le esta haciendo referencia, en ese caso NO borramos (daria una Excepcion) y devolvemos -1
	 * @param idTomador: este es el identificados del tomador a borrar
	 * @return 1: Si todo va  correcto y se borra el tomador
	 * 		  -1: Si NO borramos el tomador porque un colectivo le esta haciendo referencia
	 */	
	@SuppressWarnings("unchecked")
	public final int dropTomador(TomadorId idTomador, Usuario usuario) {
		int retorno = 1;
		try {
			ColectivoFiltro filtroColectivo = new ColectivoFiltro(idTomador.getCodentidad(),
					idTomador.getCiftomador().trim());
			List<Colectivo> colectivosAsociados = colectivoDao.getObjects(filtroColectivo);
			if (colectivosAsociados.size() > 0) {
				retorno = -1;
			} else {
				// TMR 30-05-2012 Facturacion. Añadimos el usuario
				tomadorDao.removeObjectFacturacion(Tomador.class, idTomador, usuario);
			}
		} catch (Exception e) {
			LOGGER.error("Excepcion : TomadorManager - dropTomador", e);
			retorno = -2;
		}
		return retorno;
	}
	
	public final void setTomadorDao(final ITomadorDao tomadorDao) {
		this.tomadorDao = tomadorDao;
	}

	public void setColectivoDao(IColectivoDao colectivoDao) {
		this.colectivoDao = colectivoDao;
	}

	public void setEntidadDao(IEntidadDao entidadDao) {
		this.entidadDao = entidadDao;
	}

}
