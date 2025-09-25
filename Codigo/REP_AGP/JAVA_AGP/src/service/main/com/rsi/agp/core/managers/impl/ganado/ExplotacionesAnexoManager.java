package com.rsi.agp.core.managers.impl.ganado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionManager;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.poliza.ganado.IDatosExplotacionAnexoDao;
import com.rsi.agp.dao.models.poliza.ganado.IExplotacionAnexoDao;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaVincAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;

public class ExplotacionesAnexoManager implements IManager {

	private static final Log logger = LogFactory.getLog(ExplotacionesAnexoManager.class);
	
	private SolicitudModificacionManager solicitudModificacionManager;
	private DatosExplotacionesManager datosExplotacionesManager;
	private ExplotacionesModificacionPolizaManager explotacionesModificacionPolizaManager;
	
	private IExplotacionAnexoDao explotacionAnexoDao;
	private IDatosExplotacionAnexoDao datosExplotacionAnexoDao;
	private IDiccionarioDatosDao diccionarioDatosDao;

	/**
	 * Da de baja una explotación de anexo. Se marca el tipo de modificación con una B (Baja).
	 * @param idAnexo
	 * @param idExplotacionAnexo
	 * @throws DAOException
	 */
	public void bajaExplotacionAnexo(Long idAnexo, Long idExplotacionAnexo) throws DAOException {
		
		logger.debug("bajaExplotacionAnexo - INICIO");
		//1. Obtener la explotación a poner de baja.
		ExplotacionAnexo explotacionAnexo = (ExplotacionAnexo)explotacionAnexoDao.get(ExplotacionAnexo.class, idExplotacionAnexo);
		Character tipoModificacion = explotacionAnexo.getTipoModificacion();
		
		try{
			if(tipoModificacion==null){
				//Si está sin flag, ponerle el de baja
				explotacionAnexo.setTipoModificacion(Constants.BAJA);
			}
		}catch (Exception e){
			throw new DAOException("Se ha producido un error al dar de baja una explotación de anexo", e);
		}
		
		logger.debug("bajaExplotacionAnexo - FIN");
	}
	
	
	/**
	 * Restaura una explotación de anexo a su situación actualizada
	 * @param idAnexo
	 * @param idExplotacionAnexo
	 * @throws DAOException
	 */
	public void deshacerCambiosExplotacionAnexo(Long idAnexo, Long idExplotacionAnexo) throws DAOException {
		
		logger.debug("deshacerCambiosExplotacionAnexo - INICIO");
		//1. Obtener la explotación a deshacer
		ExplotacionAnexo explotacionAnexo = (ExplotacionAnexo)explotacionAnexoDao.get(ExplotacionAnexo.class, idExplotacionAnexo);
		Character tipoModificacion = explotacionAnexo.getTipoModificacion();
		
		try{
			if(Constants.ALTA.equals(tipoModificacion)){
				//Si es alta, la borramos
				explotacionAnexoDao.delete(ExplotacionAnexo.class, idExplotacionAnexo);
				
			}else if(Constants.BAJA.equals(tipoModificacion)){
				//Si es baja, le quitamos el flag
				explotacionAnexo.setTipoModificacion(null);
				
			}else if(Constants.MODIFICACION.equals(tipoModificacion)){
				// Si es modificada, recuperamos su situación actualizada y fija el tipoModificación a null
				ExplotacionAnexo explotacionAnexoActualizada = recuperarExplotacionAnexoFromSituacionActualizada(explotacionAnexo);
				explotacionAnexoActualizada.setAnexoModificacion(explotacionAnexo.getAnexoModificacion());
				explotacionAnexoActualizada = (ExplotacionAnexo)explotacionAnexoDao.saveOrUpdate(explotacionAnexoActualizada);
				/*
				explotacionAnexo.setEspecie(explotacionAnexoActualizada.getEspecie());
				//explotacionAnexo.setGrupoRazaAnexos(explotacionAnexoActualizada.getGrupoRazaAnexos());
				explotacionAnexo.setLatitud(explotacionAnexoActualizada.getLatitud());
				explotacionAnexo.setLongitud(explotacionAnexoActualizada.getLongitud());
				explotacionAnexo.setRega(explotacionAnexoActualizada.getRega());
				explotacionAnexo.setRegimen(explotacionAnexoActualizada.getRegimen());
				explotacionAnexo.setSigla(explotacionAnexoActualizada.getSigla());
				explotacionAnexo.setSubexplotacion(explotacionAnexoActualizada.getSubexplotacion());
				explotacionAnexo.setTermino(explotacionAnexoActualizada.getTermino());
				explotacionAnexo.setTipoModificacion(null);
				*/
				explotacionAnexoDao.delete(explotacionAnexo);
				explotacionAnexoDao.evict(explotacionAnexoActualizada);
			}
		}catch (Exception e){
			throw new DAOException("Se ha producido un error al deshacer los cambios de una explotación de anexo", e);
		}
		
		logger.debug("deshacerCambiosExplotacionAnexo - FIN");
	}
	
	/**
	 * Obtiene una explotación de anexo dado un id
	 * @param id
	 * @return
	 */
	public ExplotacionAnexo getExplotacionAnexo(Long id) {
		try {
			ExplotacionAnexo exp = (ExplotacionAnexo) explotacionAnexoDao.get(ExplotacionAnexo.class, id);
			
			// Si tiene datos variables, se cargan las descripciones de los configurados por lupa
			for (GrupoRazaAnexo gr : exp.getGrupoRazaAnexos()) {
				for (DatosVarExplotacionAnexo dv : gr.getDatosVarExplotacionAnexos()) {
					dv.setDesValor(datosExplotacionesManager.getDescDatoVariable(dv.getCodconcepto(), dv.getValor()));
				}
			}
			
			return exp;
			
		} catch (Exception e1) {
			logger.error("Error al obtener la explotación de anexo con id " + id, e1);
			return null;
		}
	}
	
	
	public List<Long> obtenerIdsExplotacionesAnexoConVariosGruposRaza(Long idAnexo) throws BusinessException{
		List<Long> listaIdsExplotacionesAnexo = null;
		try {
			listaIdsExplotacionesAnexo = explotacionAnexoDao.obtenerIdsExplotacionesAnexoConVariosGruposRaza(idAnexo);
		} catch (DAOException e) {
			logger.error("Error al calcular las explotaciones de anexo con varios grupos raza");
			throw new BusinessException("Error al calcular las explotaciones de anexo con varios grupos raza");
		}
		return listaIdsExplotacionesAnexo;
	}
	
	
	/**
	 * Duplica una explotación de anexo
	 * @param idExplotacionAnexo
	 * @throws DAOException
	 */
	public ExplotacionAnexo duplicarExplotacionAnexo(final ExplotacionAnexo explotacionOrigen) throws DAOException {
		try {
			//ExplotacionAnexo explotacionOrigen = (ExplotacionAnexo) explotacionAnexoDao.get(ExplotacionAnexo.class, idExplotacionAnexo);
			ExplotacionAnexo explotacionDestino = new ExplotacionAnexo();
			
			//Es nuevo, por lo que calculamos el número que le corresponde y lo marcamos como ALTA 
			Integer numExplotacion = datosExplotacionAnexoDao.calcularNuevoNumeroExplotacion(explotacionOrigen.getId());
			explotacionDestino.setNumero(numExplotacion);
			explotacionDestino.setTipoModificacion(Constants.ALTA);
			
			// Datos básicos
			explotacionDestino.setEspecie(explotacionOrigen.getEspecie());
			explotacionDestino.setLatitud(explotacionOrigen.getLatitud());
			explotacionDestino.setLongitud(explotacionOrigen.getLongitud());
			explotacionDestino.getAnexoModificacion().setId(explotacionOrigen.getAnexoModificacion().getId());
			explotacionDestino.setRega(explotacionOrigen.getRega());
			explotacionDestino.setRegimen(explotacionOrigen.getRegimen());
			explotacionDestino.setSigla(explotacionOrigen.getSigla());
			explotacionDestino.setSubexplotacion(explotacionOrigen.getSubexplotacion());
			explotacionDestino.setTermino(explotacionOrigen.getTermino());

			// Grupo raza
			Set<GrupoRazaAnexo> grupoRazasOrigen = explotacionOrigen.getGrupoRazaAnexos();
			Set<GrupoRazaAnexo> grupoRazasDestino = new HashSet<GrupoRazaAnexo>(grupoRazasOrigen.size());
			GrupoRazaAnexo grupoRaza;
			Set<PrecioAnimalesModuloAnexo> precAnimModOrigen;
			Set<PrecioAnimalesModuloAnexo> precAnimModDestino;
			Set<DatosVarExplotacionAnexo> datosVarOrigen;
			Set<DatosVarExplotacionAnexo> datosVarDestino;
			PrecioAnimalesModuloAnexo precio;
			DatosVarExplotacionAnexo datoVar;
			
			for (GrupoRazaAnexo grupoRazaOrigen : grupoRazasOrigen) {
				grupoRaza = new GrupoRazaAnexo();
				grupoRaza.setCodgruporaza(grupoRazaOrigen.getCodgruporaza());
				grupoRaza.setCodtipoanimal(grupoRazaOrigen.getCodtipoanimal());
				grupoRaza.setCodtipocapital(grupoRazaOrigen.getCodtipocapital());
				grupoRaza.setNumanimales(grupoRazaOrigen.getNumanimales());
				grupoRaza.setExplotacionAnexo(explotacionDestino);
				precAnimModOrigen = grupoRazaOrigen.getPrecioAnimalesModuloAnexos();
				precAnimModDestino = new HashSet<PrecioAnimalesModuloAnexo>(precAnimModOrigen.size());
				
				for (PrecioAnimalesModuloAnexo precioOrigen : precAnimModOrigen) {
					precio = new PrecioAnimalesModuloAnexo();
					precio.setCodmodulo(precioOrigen.getCodmodulo());
					precio.setFilamodulo(precioOrigen.getFilamodulo());
					precio.setPrecio(precioOrigen.getPrecio());
					precio.setPrecioMax(precioOrigen.getPrecioMax());
					precio.setPrecioMin(precioOrigen.getPrecioMin());
					precio.setGrupoRazaAnexo(grupoRaza);
					precAnimModDestino.add(precio);
				}
				
				grupoRaza.setPrecioAnimalesModuloAnexos(precAnimModDestino);
				datosVarOrigen = grupoRazaOrigen.getDatosVarExplotacionAnexos();
				datosVarDestino = new HashSet<DatosVarExplotacionAnexo>(datosVarOrigen.size());
				
				for (DatosVarExplotacionAnexo datoVarOrigen : datosVarOrigen) {
					datoVar = new DatosVarExplotacionAnexo();
					datoVar.setCodconcepto(datoVarOrigen.getCodconcepto());
					datoVar.setCodrcub(datoVarOrigen.getCodrcub());
					datoVar.setCpm(datoVarOrigen.getCpm());
					datoVar.setValor(datoVarOrigen.getValor());
					datoVar.setGrupoRazaAnexo(grupoRaza);
					datosVarDestino.add(datoVar);
				}
				
				grupoRaza.setDatosVarExplotacionAnexos(datosVarDestino);
				grupoRazasDestino.add(grupoRaza);
			}
			
			explotacionDestino.setGrupoRazaAnexos(grupoRazasDestino);
			// grabamos las coberturas
			if (explotacionOrigen.getExplotacionCoberturasAnexo() != null)
				replicarCobExplotacionAnexo(explotacionOrigen.getExplotacionCoberturasAnexo(),explotacionDestino);		
			
			explotacionAnexoDao.saveOrUpdate(explotacionDestino);
			explotacionAnexoDao.evict(explotacionDestino);
			
			return explotacionDestino;
		} catch (Exception ex) {
			logger.debug("Error al duplicar la explotación de anexo", ex);
			throw new DAOException(ex);
		}
	}
	
	/**
	 * Duplica una explotacionCobertura de Anexo
	 * @param setExpCobOr
	 * @param explotacionDestino
	 */
	public void replicarCobExplotacionAnexo(Set<ExplotacionCoberturaAnexo> setExpCobOr,ExplotacionAnexo explotacionDestino) {
		List<ExplotacionCoberturaAnexo> lstCobsVincular = new ArrayList<ExplotacionCoberturaAnexo>();
		Set<ExplotacionCoberturaAnexo> setExpCobDestino = new HashSet<ExplotacionCoberturaAnexo>();
		try {
			for (ExplotacionCoberturaAnexo cobOrigen:setExpCobOr) {
				ExplotacionCoberturaAnexo expCobDestino = new ExplotacionCoberturaAnexo();
				expCobDestino.setCodmodulo(cobOrigen.getCodmodulo());			
				expCobDestino.setExplotacionAnexo(explotacionDestino);
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
				Set<ExplotacionCoberturaVincAnexo> setCobVincOrigen = cobOrigen.getExplotacionCoberturaVincAnexos();
				Set<ExplotacionCoberturaVincAnexo> setCobVincDestino = new HashSet<ExplotacionCoberturaVincAnexo>(); 
				for (@SuppressWarnings("unused") ExplotacionCoberturaVincAnexo vincOrigen :setCobVincOrigen) {
					lstCobsVincular.add(cobOrigen);
				}
				if (setCobVincDestino.size() >0)
					expCobDestino.setExplotacionCoberturaVincAnexos(setCobVincDestino);
				setExpCobDestino.add(expCobDestino);
			}	
			explotacionDestino.setExplotacionCoberturasAnexo(setExpCobDestino);
			explotacionAnexoDao.saveOrUpdate(explotacionDestino);
			explotacionAnexoDao.evict(explotacionDestino);
			if (lstCobsVincular.size()>0) {
				for (ExplotacionCoberturaAnexo cobVinculada:lstCobsVincular) {
					for (ExplotacionCoberturaAnexo cobVincular:setExpCobDestino) {
						if (cobVinculada.getCodmodulo() == cobVincular.getCodmodulo() && cobVinculada.getCpm() == cobVincular.getCpm() && 
								cobVinculada.getFila() == cobVincular.getFila() && cobVinculada.getElegible() == cobVincular.getElegible()) {
							Set<ExplotacionCoberturaVincAnexo> setVinFinal = new HashSet<ExplotacionCoberturaVincAnexo>();
							for (ExplotacionCoberturaVincAnexo vin:cobVinculada.getExplotacionCoberturaVincAnexos()) {
								// creamos la nueva explotacion vinculada
								ExplotacionCoberturaVincAnexo expVincDestino = new ExplotacionCoberturaVincAnexo();								
								expVincDestino.setExplotacionCoberturaAnexo(cobVincular);
								expVincDestino.setFila(vin.getFila());
								expVincDestino.setVinculacion(vin.getVinculacion());					
								expVincDestino.setDvColumna(vin.getDvColumna());
								expVincDestino.setDvValor(vin.getDvValor());
								
								expVincDestino.setVinculacionElegida(vin.getVinculacionElegida());
								explotacionAnexoDao.saveOrUpdate(expVincDestino);
								setVinFinal.add(expVincDestino);
							}
							cobVincular.setExplotacionCoberturaVincAnexos(setVinFinal);						
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.debug(" Error al duplicar las coberturas de la explotacion ", ex);
			//throw new DAOException(ex);
		}
	}
	
	/**
	 * Restaura una explotación de anexo con los datos de la situación actualizada del mismo 
	 * @param explotacionAnexo
	 * @throws DAOException
	 * @throws XmlException
	 */
	private ExplotacionAnexo recuperarExplotacionAnexoFromSituacionActualizada(ExplotacionAnexo explotacionAnexo) throws DAOException, XmlException {
		//P00019224@015
		es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
				.getPolizaActualizadaFromCupon(explotacionAnexo.getAnexoModificacion().getCupon().getIdcupon()))
						.getPoliza();
		
		Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta = 
				diccionarioDatosDao.getCodConceptoEtiquetaTablaExplotaciones(
						explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid());
		
		ExplotacionAnexo explotacionAnexoActualizada = explotacionesModificacionPolizaManager.getExplotacionAnexoFromPolizaActualizada(
				explotacionAnexo.getAnexoModificacion().getPoliza().getIdpoliza(),
				poliza,
				explotacionAnexo.getAnexoModificacion().getId(),
				dvCodConceptoEtiqueta,
				explotacionAnexo.getNumero());
		
		logger.info("Recuperada explotación de anexo actualizada");
		
		if(explotacionAnexoActualizada!=null){
			logger.info("Número= " + explotacionAnexo.getNumero());
		}
		
		return explotacionAnexoActualizada;
	}
	
	public void setExplotacionAnexoDao(IExplotacionAnexoDao explotacionAnexoDao) {
		this.explotacionAnexoDao = explotacionAnexoDao;
	}

	public void setDatosExplotacionAnexoDao(IDatosExplotacionAnexoDao datosExplotacionAnexoDao) {
		this.datosExplotacionAnexoDao = datosExplotacionAnexoDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setSolicitudModificacionManager(SolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setExplotacionesModificacionPolizaManager(ExplotacionesModificacionPolizaManager explotacionesModificacionPolizaManager) {
		this.explotacionesModificacionPolizaManager = explotacionesModificacionPolizaManager;
	}


	public void setDatosExplotacionesManager(
			DatosExplotacionesManager datosExplotacionesManager) {
		this.datosExplotacionesManager = datosExplotacionesManager;
	}
	
	
	
}