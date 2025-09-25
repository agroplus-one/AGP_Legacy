package com.rsi.agp.dao.models.poliza.ganado;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;

import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.jmesa.filter.gan.PrecioGanadoFilter;
import com.rsi.agp.core.util.CollectionsAndMapsUtil;
import com.rsi.agp.core.util.ListCastingHelper;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.MascaraPrecioGanado;
import com.rsi.agp.dao.tables.cpl.gan.PrecioGanado;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;

public class PrecioGanadoDao extends BaseDaoHibernate implements IPrecioGanadoDao {
	
	private static final Class<PrecioGanado> PRECIO_GANADO = PrecioGanado.class;
	private static final Class<MascaraPrecioGanado> MASCARA_PRECIO_GANADO = MascaraPrecioGanado.class;
	private static final Log logger = LogFactory.getLog(PrecioGanadoDao.class);
	
	@Override
	public PrecioGanado getPrecioExplotacion(Object explotacion,
			String codModulo, PrecioGanado dvBean) throws PrecioGanadoException {
		List<Object> lista = null;
		if(explotacion instanceof Explotacion){
			lista = getPrecioExplotacionUbicacion((Explotacion)explotacion, codModulo, PRECIO_GANADO, dvBean,false);
		} else {
			lista = getPrecioExplotacionUbicacion((ExplotacionAnexo)explotacion, codModulo, PRECIO_GANADO, dvBean, false);
		}
		List<PrecioGanado> precioGanadoLista = new ListCastingHelper<Object, PrecioGanado>(lista).cast();
		int dimensionPrecioGanadoLista = CollectionsAndMapsUtil.size(precioGanadoLista);
		PrecioGanado precioGanado = null;
		if(dimensionPrecioGanadoLista > 1){
			precioGanado = obtenerPrecio(precioGanadoLista);
			logger.debug(mensajeLogPrecioExplotacion(precioGanado));
		} else if(dimensionPrecioGanadoLista == 1) {
			precioGanado = precioGanadoLista.get(0);
			logger.debug(mensajeLogPrecioExplotacion(precioGanado));
		} else {
			logger.debug("getPrecioExplotacion - No se ha encontrado precio para los datos de explotación indicados");
		}
		return precioGanado;
	}

	@Override
	public List<MascaraPrecioGanado> getMascarasPrecioExplotacion(Object explotacion, String codModulo) {
		
		logger.debug("PrecioGanadoDao - getMascarasPrecioExplotacion - init");
		
		List<MascaraPrecioGanado> mascaraPrecioGanadoLista = new ArrayList<MascaraPrecioGanado>();
		try {
			List<Object> lista = null;
			if(explotacion instanceof Explotacion){
				
				logger.debug("Es una explotacion");
				lista = getPrecioExplotacionUbicacion((Explotacion)explotacion, codModulo, MASCARA_PRECIO_GANADO, null,true);
			} else {
				logger.debug("Es una explotacion de anexo");
				lista = getPrecioExplotacionUbicacion((ExplotacionAnexo)explotacion, codModulo, MASCARA_PRECIO_GANADO, null,true);
			}
			
			logger.debug("Num. elem. mascaraPrecioGanadoLista: " + lista.size());

			
			if(!CollectionsAndMapsUtil.isEmpty(lista)){
				mascaraPrecioGanadoLista.addAll(new ListCastingHelper<Object, MascaraPrecioGanado>(lista).cast());
			}
		} catch (Exception e) {
			logger.error("Error inesperado al obtener las mascaras de precio asociadas a la explotacion", e);
		}
		
		logger.debug("PrecioGanadoDao - getMascarasPrecioExplotacion - end");

		
		return mascaraPrecioGanadoLista;
	}
	
	/**
	 * Busca en la tabla de precios indicada los registros asociados a la explotación y al módulo indicados añadiendo valores genéricos
	 * de ubicación si no se obtienen resultados de los concretos
	 * @param explotacion
	 * @param codModulo
	 * @param clazz
	 * @param dvBean
	 * @param esMascara
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object> getPrecioExplotacionUbicacion(Object explotacion, String codModulo, 
			Class<?> clazz, PrecioGanado dvBean,Boolean esMascara) {
		boolean provinciaGen, comarcaGen, terminoGen, subterminoGen;
		provinciaGen = comarcaGen = terminoGen = subterminoGen = false;
		List<Object> lista = null;
		for(int i = 0; i < 5; i++){
			switch (i) {
				case 0:
					// Filtro con los valores exactos
					break;
				case 1:
					// Filtro con subtérmino genérico
					subterminoGen = true;
					break;
				case 2:
					// Filtro con término y subtérmino genéricos
					terminoGen = true;
					break;
				case 3:
					// Filtro con comarca, término y subtérmino genéricos
					comarcaGen = true;
					break;
				case 4:
					// Filtro con provincia, comarca, término y subtérmino genéricos
					provinciaGen = true;
					break;
				default:
					break;
			}
			Criteria criteria = this.obtenerSession().createCriteria(clazz);
			if(explotacion instanceof Explotacion){
				lista = PrecioGanadoFilter.getFilter((Explotacion)explotacion, codModulo, provinciaGen, 
						comarcaGen, terminoGen, subterminoGen, dvBean,esMascara).execute(criteria).list();
			} else {
				lista = PrecioGanadoFilter.getFilter((ExplotacionAnexo)explotacion, codModulo, provinciaGen, 
						comarcaGen, terminoGen, subterminoGen, dvBean,esMascara).execute(criteria).list();
			}
			if(!CollectionsAndMapsUtil.isEmpty(lista)){
				break;
			}
		}
		return lista;
	}
	
	/**
	 * Crea el mensaje del log cuando todo ha ido bien
	 * @param precioGanado
	 * @return mensaje que será mostrado en el log
	 */
	private String mensajeLogPrecioExplotacion(PrecioGanado precioGanado) {
		return new StringBuilder("getPrecioExplotacion - Precio encontrado: ")
			.append("Min = ").append(precioGanado.getPrecioGanadoDesde())
			.append(" - ")
			.append("Max = ").append(precioGanado.getPrecioGanadoHasta())
			.toString();
	}

	/**
	 * Este método devuelve el precio que, siendo igual al resto que ha devuelto la BD, 
	 * tiene una fecha más cercana al día de hoy
	 * @param precioGanadoLista
	 * @return PrecioGanado
	 * @throws PrecioGanadoException
	 * @throws FechaPrecioGanadoException
	 */
	private PrecioGanado obtenerPrecio(List<PrecioGanado> precioGanadoLista) 
			throws PrecioGanadoException {
		sonTodosLosPreciosIguales(precioGanadoLista);
		return obtenerPrecioConFechaMasCercana(precioGanadoLista);
	}
	
	/**
	 * Obtiene el precio que tiene la fecha más cercana al día de hoy
	 * @param precioGanadoLista
	 * @return PrecioGanado
	 * @throws FechaPrecioGanadoException
	 */
	private PrecioGanado obtenerPrecioConFechaMasCercana(List<PrecioGanado> precioGanadoLista) {
		PrecioGanado referencia = precioGanadoLista.get(0);
		for(int i = 1; i < precioGanadoLista.size(); i++){
			PrecioGanado prueba = precioGanadoLista.get(i);
			if(prueba.getFecValidezHasta().before(referencia.getFecValidezHasta())){
				referencia = prueba;
			}
		}
		return referencia;
	}

	/**
	 * Se considera que dos precios son iguales si, y solo si, todos sus campos son 
	 * iguales a excepción de id, fecValidezHasta, precioGanadoDesde y precioGanadoHasta
	 * @param precioGanadoLista
	 * @throws PrecioGanadoException 
	 */
	private void sonTodosLosPreciosIguales(List<PrecioGanado> precioGanadoLista) throws PrecioGanadoException{
		List<Boolean> resultadoComparacion = new ArrayList<Boolean>();
		PrecioGanado referencia = precioGanadoLista.get(0);
		for(int i = 1; i < precioGanadoLista.size(); i++){
			resultadoComparacion.add(this.sonPreciosIguales(referencia, precioGanadoLista.get(i)));
		}
		if(resultadoComparacion.contains(Boolean.FALSE)){
			throw new PrecioGanadoException("Los precios son distintos");
		}
	}
	
	/**
	 * Compara si dos precios son iguales. Dos precios son iguales si, y sólo si, 
	 * todos sus campos son iguales a excepción de id, fecValidezHasta, 
	 * precioGanadoDesde y precioGanadoHasta
	 * @param precio1
	 * @param precio2
	 * @return true o false, según sean los precios iguals o no
	 */
	private boolean sonPreciosIguales(PrecioGanado referencia, PrecioGanado prueba){
		if(!iguales(referencia.getTiposAnimalGanado().getId(),prueba.getTiposAnimalGanado().getId())){
			return false;
		}
		if(!iguales(referencia.getModulo().getId(),prueba.getModulo().getId())){
			return false;
		}
		if(!iguales(referencia.getRegimenManejo().getId(), prueba.getRegimenManejo().getId())){
			return false;
		}
		if(!iguales(referencia.getTiposAnimalGanado().getId(), prueba.getTiposAnimalGanado().getId())){
			return false;
		}
		if(!iguales(referencia.getTermino().getId(), prueba.getTermino().getId())){
			return false;
		}
		if(!iguales(referencia.getTipoCapital(), prueba.getTipoCapital())){
			return false;
		}
		if(!iguales(referencia.getGruposRazas().getId(), prueba.getGruposRazas().getId())){
			return false;
		}
		if(!iguales(referencia.getLinea().getLineaseguroid(), prueba.getLinea().getLineaseguroid())){
			return false;
		}
		
		if(referencia.getSistemaProduccion() != null && prueba.getSistemaProduccion() != null){
			SistemaProduccion sistProdRef = referencia.getSistemaProduccion();
			SistemaProduccion sistProdPrueba = prueba.getSistemaProduccion();
			if(!iguales(sistProdRef.getCodsistemaproduccion(), sistProdPrueba.getCodsistemaproduccion())){
				return false;
			}
		} else if (referencia.getSistemaProduccion() == null && prueba.getSistemaProduccion() == null) {
			// Pasamos de largo ya que null es igual a null
		} else {
			return false;
		}
		
		if(referencia.getEspecie() != null && prueba.getEspecie() != null){
			Especie especieRef = referencia.getEspecie();
			Especie especiePrueba = prueba.getEspecie();
			if(!iguales(especieRef.getId(), especiePrueba.getId())){
				return false;
			}
		} else if(referencia.getEspecie() == null && prueba.getEspecie() == null){
			// Pasamos de largo ya que null es igual a null			
		} else {
			return false;
		}
		
		if(!iguales(referencia.getCodControlOficialLechero(), prueba.getCodControlOficialLechero())){
			return false;
		}
		if(!iguales(referencia.getCodPureza(), prueba.getCodPureza())){
			return false;
		}
		if(!iguales(referencia.getCodIgpdoGanado(), prueba.getCodIgpdoGanado())){
			return false;
		}
		if(!iguales(referencia.getCodGestora(), prueba.getCodGestora())){
			return false;
		}
		if(!iguales(referencia.getNumAnimalesAcumDesde(), prueba.getNumAnimalesAcumDesde())){
			return false;
		}
		if(!iguales(referencia.getNumAnimalesAcumHasta(), prueba.getNumAnimalesAcumHasta())){
			return false;
		}
		if(!iguales(referencia.getCodSistAlmacena(), prueba.getCodSistAlmacena())){
			return false;
		}
		if(!iguales(referencia.getCodExcepContrExc(), prueba.getCodExcepContrExc())){
			return false;
		}
		if(!iguales(referencia.getCodExcepContrPol(), prueba.getCodExcepContrPol())){
			return false;
		}
		if(!iguales(referencia.getCodTipoGanaderia(), prueba.getCodTipoGanaderia())){
			return false;
		}
		if(!iguales(referencia.getCodAlojamiento(), prueba.getCodAlojamiento())){
			return false;
		}
		if(!iguales(referencia.getCodDestino(), prueba.getCodDestino())){
			return false;
		}
		if(!iguales(referencia.getProdAnualMedia(), prueba.getProdAnualMedia())){
			return false;
		}
		return true;
	}
	
	private boolean iguales(Object referencia, Object prueba){
		if(referencia != null && prueba != null){
			if(referencia.equals(prueba)){
				return true;
			} else {
				return false;
			}
		}
		if(referencia == null && prueba == null){
			return true;
		}
		return false;
	}
}
