package com.rsi.agp.core.jmesa.filter.gan;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.filter.GenericoFilter;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.cpl.gan.PrecioGanado;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;

public class PrecioGanadoFilter extends GenericoFilter {

	public Criteria execute(Criteria criteria) {
		for (Filter filter : this.filters) {
			logger.debug("Filtro añadido - " + filter.getProperty() + " = " + filter.getValue());
			if ("numAnimalesAcumDesde".equals(filter.getProperty())) {
				criteria.add(Restrictions.le("numAnimalesAcumDesde", filter.getValue()));
			} else if ("numAnimalesAcumHasta".equals(filter.getProperty())) {
				criteria.add(Restrictions.ge("numAnimalesAcumHasta", filter.getValue()));
			} else {
				buildCriteria(criteria, filter.getProperty(), filter.getValue(), filter.getTipo(), filter.getOperador());
			}
        }
		
        return criteria;
	}
	
	/**
	 * 
	 * @param ex
	 * @param codModulo
	 * @param prvGen
	 * @param comGen
	 * @param terGen
	 * @param subGen
	 * @param dvBean
	 * @param esMascara Determina si el filtro se va a utilizar para recoger la lista de máscara de precios o precios
	 * @return
	 */
	public static PrecioGanadoFilter getFilter(Explotacion ex, String codModulo, boolean prvGen, boolean comGen,
			boolean terGen, boolean subGen, PrecioGanado dvBean, Boolean esMascara) {
		
		PrecioGanadoFilter pgf = new PrecioGanadoFilter();
		
		//Filtro fijo
		if(!esMascara){//esta propiedad no está en la máscara de precios
			Calendar c2 = new GregorianCalendar();
			pgf.addFilter("fecValidezHasta",c2.getTime(),"ge");
		}
		
		// Datos de póliza
		pgf.addFilter("id.lineaseguroid", ex.getPoliza().getLinea().getLineaseguroid());
		pgf.addFilter("modulo.id.codmodulo", codModulo);
		
		// Datos de la explotación 
		pgf.addFilter("termino.id.codprovincia", prvGen ? Constants.PROVINCIA_GENERICA :ex.getTermino().getId().getCodprovincia());
		pgf.addFilter("termino.id.codcomarca", comGen ? Constants.COMARCA_GENERICA : ex.getTermino().getId().getCodcomarca());
		pgf.addFilter("termino.id.codtermino", terGen ? Constants.TERMINO_GENERICO : ex.getTermino().getId().getCodtermino());
		pgf.addFilter("termino.id.subtermino", subGen ? Constants.SUBTERMINO_GENERICO : ex.getTermino().getId().getSubtermino());
		pgf.addFilter("especie.id.codespecie", ex.getEspecie());
		pgf.addFilter("regimenManejo.id.codRegimen", ex.getRegimen());
		
		for (GrupoRaza grupoRaza :  ex.getGrupoRazas()) {
			pgf.addFilter("gruposRazas.id.CodGrupoRaza", new Long (grupoRaza.getCodgruporaza()));
			pgf.addFilter("tipoCapital.codtipocapital", new Long(grupoRaza.getCodtipocapital().longValue()));
			pgf.addFilter("tiposAnimalGanado.id.codTipoAnimal", new Long (grupoRaza.getCodtipoanimal()));
			break;
		}		

		// Para filtrar el precio por los valores de los datos variables
		if (dvBean != null) {
			if (dvBean.getCodControlOficialLechero() != null) pgf.addFilter("codControlOficialLechero", dvBean.getCodControlOficialLechero());
			if (dvBean.getCodPureza() != null) pgf.addFilter("codPureza", dvBean.getCodPureza());
			if (dvBean.getSistemaProduccion() != null) pgf.addFilter("sistemaProduccion.codsistemaproduccion", dvBean.getSistemaProduccion().getCodsistemaproduccion());
			if (dvBean.getCodIgpdoGanado() != null) pgf.addFilter("codIgpdoGanado", dvBean.getCodIgpdoGanado());
			if (dvBean.getCodGestora() != null) pgf.addFilter("codGestora", dvBean.getCodGestora());
			if (dvBean.getNumAnimalesAcumDesde() != null) pgf.addFilter("numAnimalesAcumDesde", dvBean.getNumAnimalesAcumDesde());
			if (dvBean.getNumAnimalesAcumHasta() != null) pgf.addFilter("numAnimalesAcumHasta", dvBean.getNumAnimalesAcumHasta());
			if (dvBean.getCodSistAlmacena() != null) pgf.addFilter("codSistAlmacena", dvBean.getCodSistAlmacena());
			if (dvBean.getCodExcepContrExc() != null) pgf.addFilter("codExcepContrExc", dvBean.getCodExcepContrExc());
			if (dvBean.getCodExcepContrPol() != null) pgf.addFilter("codExcepContrPol", dvBean.getCodExcepContrPol());
			if (dvBean.getCodTipoGanaderia() != null) pgf.addFilter("codTipoGanaderia", dvBean.getCodTipoGanaderia());
			if (dvBean.getCodAlojamiento() != null) pgf.addFilter("codAlojamiento", dvBean.getCodAlojamiento());
			if (dvBean.getCodDestino() != null) pgf.addFilter("codDestino", dvBean.getCodDestino());
			if (dvBean.getProdAnualMedia()!= null) pgf.addFilter("prodAnualMedia", dvBean.getProdAnualMedia());
		}
		
		return pgf;
	}
	
	/**
	 * 
	 * @param ex
	 * @param codModulo
	 * @param prvGen
	 * @param comGen
	 * @param terGen
	 * @param subGen
	 * @param dvBean
	 * @param esMascara Determina si el filtro se va a utilizar para recoger la lista de máscara de precios o precios
	 * @return
	 */
	public static PrecioGanadoFilter getFilter(ExplotacionAnexo ex, String codModulo, boolean prvGen, boolean comGen,
			boolean terGen, boolean subGen, PrecioGanado dvBean, Boolean esMascara) {

		PrecioGanadoFilter pgf = new PrecioGanadoFilter();
		//Filtro fijo
		if(!esMascara){//esta propiedad no está en la máscara de precios
			Calendar c2 = new GregorianCalendar();
			pgf.addFilter("fecValidezHasta",c2.getTime(),"ge");
		}
				
		// Datos de póliza
		pgf.addFilter("id.lineaseguroid", ex.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid());
		pgf.addFilter("modulo.id.codmodulo", codModulo);

		// Datos de la explotación
		pgf.addFilter("termino.id.codprovincia", prvGen ? Constants.PROVINCIA_GENERICA : ex.getTermino().getId().getCodprovincia());
		pgf.addFilter("termino.id.codcomarca", comGen ? Constants.COMARCA_GENERICA : ex.getTermino().getId().getCodcomarca());
		pgf.addFilter("termino.id.codtermino", terGen ? Constants.TERMINO_GENERICO : ex.getTermino().getId().getCodtermino());
		pgf.addFilter("termino.id.subtermino", subGen ? Constants.SUBTERMINO_GENERICO : ex.getTermino().getId().getSubtermino());
		pgf.addFilter("especie.id.codespecie", ex.getEspecie());
		pgf.addFilter("regimenManejo.id.codRegimen", ex.getRegimen());

		for (GrupoRazaAnexo grupoRaza : ex.getGrupoRazaAnexos()) {
			pgf.addFilter("gruposRazas.id.CodGrupoRaza", new Long(grupoRaza.getCodgruporaza()));
			pgf.addFilter("tipoCapital.codtipocapital", new Long(grupoRaza.getCodtipocapital().longValue()));
			pgf.addFilter("tiposAnimalGanado.id.codTipoAnimal", new Long(grupoRaza.getCodtipoanimal()));
			break;
		}

		// Para filtrar el precio por los valores de los datos variables
		if (dvBean != null) {
			if (dvBean.getCodControlOficialLechero() != null)
				pgf.addFilter("codControlOficialLechero", dvBean.getCodControlOficialLechero());
			if (dvBean.getCodPureza() != null)
				pgf.addFilter("codPureza", dvBean.getCodPureza());
			if (dvBean.getSistemaProduccion() != null)
				pgf.addFilter("sistemaProduccion.codsistemaproduccion", dvBean.getSistemaProduccion().getCodsistemaproduccion());
			if (dvBean.getCodIgpdoGanado() != null)
				pgf.addFilter("codIgpdoGanado", dvBean.getCodIgpdoGanado());
			if (dvBean.getCodGestora() != null)
				pgf.addFilter("codGestora", dvBean.getCodGestora());
			if (dvBean.getNumAnimalesAcumDesde() != null)
				pgf.addFilter("numAnimalesAcumDesde", dvBean.getNumAnimalesAcumDesde());
			if (dvBean.getNumAnimalesAcumHasta() != null)
				pgf.addFilter("numAnimalesAcumHasta", dvBean.getNumAnimalesAcumHasta());
			if (dvBean.getCodSistAlmacena() != null)
				pgf.addFilter("codSistAlmacena", dvBean.getCodSistAlmacena());
			if (dvBean.getCodExcepContrExc() != null)
				pgf.addFilter("codExcepContrExc", dvBean.getCodExcepContrExc());
			if (dvBean.getCodExcepContrPol() != null)
				pgf.addFilter("codExcepContrPol", dvBean.getCodExcepContrPol());
			if (dvBean.getCodTipoGanaderia() != null)
				pgf.addFilter("codTipoGanaderia", dvBean.getCodTipoGanaderia());
			if (dvBean.getCodAlojamiento() != null)
				pgf.addFilter("codAlojamiento", dvBean.getCodAlojamiento());
			if (dvBean.getCodDestino() != null)
				pgf.addFilter("codDestino", dvBean.getCodDestino());
			if (dvBean.getProdAnualMedia() != null)
				pgf.addFilter("prodAnualMedia", dvBean.getProdAnualMedia());
		}

	return pgf;
	}	
}
