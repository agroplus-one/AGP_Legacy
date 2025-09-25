package com.rsi.agp.core.managers.impl.ganado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IExplotacionDAO;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaVinculacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

public class ExplotacionesManager implements IManager {

	private static final Log logger = LogFactory.getLog(ExplotacionesManager.class);
			
	private IExplotacionDAO explotacionDAO;

	public List<Long> obtenerExplotacionesConVariosGruposRaza(Long idPoliza) throws BusinessException{
		List<Long> listaIdsExplotaciones = null;
		try {
			listaIdsExplotaciones = explotacionDAO.obtenerExplotacionesConVariosGruposRaza(idPoliza);
		} catch (DAOException e) {
			logger.error("Error al calcular las explotaciones con varios grupos raza");
			throw new BusinessException("Error al calcular las explotaciones con varios grupos raza");
		}
		return listaIdsExplotaciones;
	}

	public Explotacion obtenerExplotacionById(Long idExplotacion) throws BusinessException{
		Explotacion exp = null;
		try {
			exp = (Explotacion)explotacionDAO.get(Explotacion.class, idExplotacion);
		} catch (DAOException e) {
			logger.error("Error al obtener la explotación con id = " + idExplotacion);
			throw new BusinessException("Error al obtener la explotación con id = " + idExplotacion);
		}
		return exp;
	}
	
	
	public void borrarExplotacion(final Long idExplotacion) throws Exception {
		try {
			Explotacion explotacion = (Explotacion) explotacionDAO.get(
					Explotacion.class, idExplotacion);
			if (explotacion != null) {
				explotacionDAO.delete(explotacion);
			}
		} catch (Exception ex) {
			logger.debug("Error al borrar la explotación.", ex);
			throw ex;
		}
	}
	
	public List<Explotacion> asignaNumeroExplotacion(Set<Explotacion> listadoExplotaciones){
		//Ordenamos la lista
		List<Explotacion> explotaciones = new ArrayList<Explotacion>(listadoExplotaciones);
		Collections.sort(explotaciones);
		//Set<Explotacion> explotaciones = new HashSet<Explotacion>(0);
		for (int i = 0; i < explotaciones.size(); i++) {
			Explotacion exp=(Explotacion) explotaciones.get(i);
			exp.setNumero(i+1);
			
		}
		
		return explotaciones;
	}
	
	public void guardaExplotaciones(List<Explotacion> listadoExplotaciones) throws DAOException{
		explotacionDAO.saveOrUpdateList(listadoExplotaciones);
	}
	
	/**
	 * Duplica una explotación
	 * @param idExplotacion
	 * @return
	 * @throws DAOException
	 */
	public Explotacion duplicarExplotacion(final Long idExplotacion, final Set<ExplotacionCobertura> setExpCoberturas) throws DAOException {
		try {
			Explotacion explotacionOrigen = (Explotacion) explotacionDAO.get(Explotacion.class, idExplotacion);
			Explotacion explotacionDestino = new Explotacion();
			
			// DATOS BASICOS
			explotacionDestino.setEspecie(explotacionOrigen.getEspecie());
			explotacionDestino.setLatitud(explotacionOrigen.getLatitud());
			explotacionDestino.setLongitud(explotacionOrigen.getLongitud());
			explotacionDestino.getPoliza().setIdpoliza(explotacionOrigen.getPoliza().getIdpoliza());
			explotacionDestino.getPoliza().getLinea().setLineaseguroid(explotacionOrigen.getPoliza().getLinea().getLineaseguroid());
			explotacionDestino.setRega(explotacionOrigen.getRega());
			explotacionDestino.setRegimen(explotacionOrigen.getRegimen());
			explotacionDestino.setSigla(explotacionOrigen.getSigla());
			explotacionDestino.setSubexplotacion(explotacionOrigen.getSubexplotacion());
			explotacionDestino.setTermino(explotacionOrigen.getTermino());
			
			// GRUPO RAZA EXPLOTACION
			Set<GrupoRaza> grupoRazasOrigen = explotacionOrigen.getGrupoRazas();
			Set<GrupoRaza> grupoRazasDestino = new HashSet<GrupoRaza>(grupoRazasOrigen.size());
			GrupoRaza grupoRaza;
			Set<PrecioAnimalesModulo> precAnimModOrigen;
			Set<PrecioAnimalesModulo> precAnimModDestino;
			Set<DatosVariable> datosVarOrigen;
			Set<DatosVariable> datosVarDestino;
			PrecioAnimalesModulo precio;
			DatosVariable datoVar;

			for (GrupoRaza grupoRazaOrigen : grupoRazasOrigen) {
				grupoRaza = new GrupoRaza();
				grupoRaza.setCodgruporaza(grupoRazaOrigen.getCodgruporaza());
				grupoRaza.setCodtipoanimal(grupoRazaOrigen.getCodtipoanimal());
				grupoRaza.setCodtipocapital(grupoRazaOrigen.getCodtipocapital());
				grupoRaza.setNumanimales(grupoRazaOrigen.getNumanimales());
				grupoRaza.setExplotacion(explotacionDestino);
				precAnimModOrigen = grupoRazaOrigen.getPrecioAnimalesModulos();
				precAnimModDestino = new HashSet<PrecioAnimalesModulo>(precAnimModOrigen.size());
				
				for (PrecioAnimalesModulo precioOrigen : precAnimModOrigen) {
					precio = new PrecioAnimalesModulo();
					precio.setCodmodulo(precioOrigen.getCodmodulo());
					precio.setFilamodulo(precioOrigen.getFilamodulo());
					precio.setPrecio(precioOrigen.getPrecio());
					precio.setPrecioMax(precioOrigen.getPrecioMax());
					precio.setPrecioMin(precioOrigen.getPrecioMin());
					precio.setGrupoRaza(grupoRaza);
					precAnimModDestino.add(precio);
				}
				
				grupoRaza.setPrecioAnimalesModulos(precAnimModDestino);
				datosVarOrigen = grupoRazaOrigen.getDatosVariables();
				datosVarDestino = new HashSet<DatosVariable>(datosVarOrigen.size());
				
				for (DatosVariable datoVarOrigen : datosVarOrigen) {
					datoVar = new DatosVariable();
					datoVar.setCodconcepto(datoVarOrigen.getCodconcepto());
					datoVar.setCodrcub(datoVarOrigen.getCodrcub());
					datoVar.setCpm(datoVarOrigen.getCpm());
					datoVar.setValor(datoVarOrigen.getValor());
					datoVar.setGrupoRaza(grupoRaza);
					datosVarDestino.add(datoVar);
				}
				grupoRaza.setDatosVariables(datosVarDestino);
				grupoRazasDestino.add(grupoRaza);
			}
			explotacionDestino.setGrupoRazas(grupoRazasDestino);
			
			// grabamos las coberturas
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				replicarCoberturasExplotacion(setExpCoberturas,explotacionDestino);	
			}else {
				replicarCoberturasExplotacion(explotacionOrigen.getExplotacionCoberturas(),explotacionDestino);		
			}
			
			return explotacionDestino;
		} catch (Exception ex) {
			logger.debug(" Error al duplicar la explotación ", ex);
			throw new DAOException(ex);
		}
	}
	
	public void replicarCoberturasExplotacion(Set<ExplotacionCobertura> setExpCobOr,Explotacion explotacionDestino) {
		List<ExplotacionCobertura> lstCobsVincular = new ArrayList<ExplotacionCobertura>();
		Set<ExplotacionCobertura> setExpCobDestino = new HashSet<ExplotacionCobertura>();
		try {
			for (ExplotacionCobertura cobOrigen:setExpCobOr) {
				ExplotacionCobertura expCobDestino = new ExplotacionCobertura();
				expCobDestino.setCodmodulo(cobOrigen.getCodmodulo());			
				expCobDestino.setExplotacion(explotacionDestino);
				expCobDestino.setFila(cobOrigen.getFila());
				expCobDestino.setCpm(cobOrigen.getCpm());
				expCobDestino.setCpmDescripcion(cobOrigen.getCpmDescripcion());
				expCobDestino.setRiesgoCubierto(cobOrigen.getRiesgoCubierto());
				expCobDestino.setRcDescripcion(cobOrigen.getRcDescripcion());
				expCobDestino.setElegible(cobOrigen.getElegible());
				expCobDestino.setElegida(cobOrigen.getElegida());
				expCobDestino.setTipoCobertura(cobOrigen.getTipoCobertura());
				expCobDestino.setDvCodConcepto(cobOrigen.getDvCodConcepto());
				expCobDestino.setDvDescripcion(cobOrigen.getDvDescripcion());
				expCobDestino.setDvValor(cobOrigen.getDvValor());
				expCobDestino.setDvValorDescripcion(cobOrigen.getDvValorDescripcion());
				expCobDestino.setDvElegido(cobOrigen.getDvElegido());
				expCobDestino.setDvColumna(cobOrigen.getDvColumna());
				Set<ExplotacionCoberturaVinculacion> setCobVincOrigen = cobOrigen.getExplotacionCoberturaVinculacions();
				Set<ExplotacionCoberturaVinculacion> setCobVincDestino = new HashSet<ExplotacionCoberturaVinculacion>(); 
				for (@SuppressWarnings("unused") ExplotacionCoberturaVinculacion vincOrigen : setCobVincOrigen) {
					lstCobsVincular.add(cobOrigen);
				}
				if (setCobVincDestino.size() >0)
					expCobDestino.setExplotacionCoberturaVinculacions(setCobVincDestino);
				setExpCobDestino.add(expCobDestino);
			}	
			explotacionDestino.setExplotacionCoberturas(setExpCobDestino);
			explotacionDAO.saveOrUpdate(explotacionDestino);
			explotacionDAO.evict(explotacionDestino);
			if (lstCobsVincular.size()>0) {
				for (ExplotacionCobertura cobVinculada:lstCobsVincular) {
					for (ExplotacionCobertura cobVincular:setExpCobDestino) {
						if (cobVinculada.getCodmodulo() == cobVincular.getCodmodulo() && cobVinculada.getCpm() == cobVincular.getCpm() && 
								cobVinculada.getFila() == cobVincular.getFila() && cobVinculada.getElegible() == cobVincular.getElegible()) {
							Set<ExplotacionCoberturaVinculacion> setVinFinal = new HashSet<ExplotacionCoberturaVinculacion>();
							for (ExplotacionCoberturaVinculacion vin:cobVinculada.getExplotacionCoberturaVinculacions()) {
								// creamos la nueva explotacion vinculada
								ExplotacionCoberturaVinculacion expVincDestino = new ExplotacionCoberturaVinculacion();								
								expVincDestino.setExplotacionCobertura(cobVincular);
								expVincDestino.setFila(vin.getFila());
								expVincDestino.setVinculacion(vin.getVinculacion());												
								expVincDestino.setDvColumna(vin.getDvColumna());
								expVincDestino.setDvValor(vin.getDvValor());
								
								expVincDestino.setVinculacionElegida(vin.getVinculacionElegida());
								explotacionDAO.saveOrUpdate(expVincDestino);
								setVinFinal.add(expVincDestino);
							}
							cobVincular.setExplotacionCoberturaVinculacions(setVinFinal);						
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.debug(" Error al duplicar las coberturas de la explotacion ", ex);
			//throw new DAOException(ex);
		}
	}
	
	//SETTERS
	public void setExplotacionDAO(IExplotacionDAO explotacionDAO) {
		this.explotacionDAO = explotacionDAO;
	}
}