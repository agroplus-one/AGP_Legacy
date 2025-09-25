package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.util.CollectionUtils;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IImportacionPolizasService;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.config.IClaseDetalleDao;
import com.rsi.agp.dao.models.poliza.ganado.ISeleccionComparativaSWDao;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

public class CambioModuloDao extends BaseDaoHibernate implements ICambioModuloDao {

	private static final Log LOGGER = LogFactory.getLog(CambioModuloDao.class);

	private ISeleccionComparativaSWDao seleccionComparativaSWDao;
	private IImportacionPolizasService importacionPolizasService;
	private IClaseDetalleDao claseDetalleDao;

	@Override
	public void cambiarModulo(final Poliza poliza, final es.agroseguro.contratacion.Poliza sitAct) throws DAOException {

		LOGGER.debug("[cambiarModulo] init");
		LOGGER.debug(sitAct.toString());

		Session session = this.getSession();

		String moduloDestino = sitAct.getCobertura().getModulo().trim();
		es.agroseguro.contratacion.datosVariables.DatosVariables dvs = sitAct.getCobertura().getDatosVariables();

		poliza.setCodmodulo(moduloDestino);

		// TB_MODULOS_POLIZA
		// por los estados en donde es posible el cambio, solo deberia haber uno
		Integer renovable = CollectionUtils.isEmpty(poliza.getModuloPolizas()) ? 0
				: poliza.getModuloPolizas().iterator().next().getRenovable();
		poliza.getModuloPolizas().clear();
		ModuloPoliza modPol = new ModuloPoliza();
		ModuloPolizaId moduloPolizaId = new ModuloPolizaId();
		moduloPolizaId.setIdpoliza(poliza.getIdpoliza());
		moduloPolizaId.setLineaseguroid(poliza.getLinea().getLineaseguroid());
		Long numComparativa = this.seleccionComparativaSWDao.getSecuenciaComparativa();
		moduloPolizaId.setNumComparativa(numComparativa);
		modPol.setId(moduloPolizaId);
		modPol.setRenovable(renovable);
		modPol.setPoliza(poliza);
		modPol.getId().setCodmodulo(moduloDestino);
		if (dvs != null && dvs.getTAseGan() != null) {
			modPol.setTipoAsegGanado(dvs.getTAseGan().getValor());
		}
		poliza.getModuloPolizas().add(modPol);
		this.saveOrUpdate(modPol);

		// TB_COMPARATIVAS_POLIZA		
		// limpiamos los existentes y los creamos de nuevo
		poliza.getComparativaPolizas().clear();
		List<ComparativaPoliza> comparativas;
		
		// Anyadimos coberturas de riesgos
		if (poliza.getLinea().isLineaGanado()) {
			comparativas = this.importacionPolizasService.getComparativasRiesgCubElegGanado(poliza, dvs, session,
					numComparativa, false, poliza.getLinea().getLineaseguroid());
		} else {
			comparativas = this.importacionPolizasService.getComparativasRiesgCubEleg(poliza,
					dvs == null ? null : dvs.getRiesgCbtoElegArray(), session, numComparativa, false);
		}
		
		// Anyadimos el resto de coberturas
		
		List<ComparativaPoliza> restoComparativas;
		restoComparativas = this.importacionPolizasService.getComparativas(poliza, session, numComparativa,
				sitAct.getCobertura(), false);
		
		if (restoComparativas.size()!=0) {
			comparativas.addAll(restoComparativas);
		}
		
		logger.debug("Hay " + comparativas.size() + " comparativas para anyadir a la poliza");
		
		for (ComparativaPoliza comp : comparativas) {

			logger.debug("Id Comparativa: " + comp.getId());
			
			poliza.getComparativaPolizas().add(comp);
			this.saveOrUpdate(comp);
		}

		// TB_DISTRIBUCION_COSTES_2015
		// se mantienen... unicamente se actualiza la asociacion con 
		// la comparativa y el modulo
		Set<DistribucionCoste2015> distCosteCol = poliza.getDistribucionCoste2015s();
		for (DistribucionCoste2015 distCoste : distCosteCol) {
			distCoste.setCodmodulo(moduloDestino);
			distCoste.setIdcomparativa(BigDecimal.valueOf(numComparativa));
		}

		// clase (solo agricolas)
		if (!poliza.getLinea().isLineaGanado()) {
			BigDecimal clase = this.claseDetalleDao.getClaseSitAct(sitAct, poliza.getLinea().getLineaseguroid());
			if (!poliza.getClase().equals(clase)) {
				poliza.setClase(clase);
			}
		}

		this.saveOrUpdate(poliza);
		LOGGER.debug("[cambiarModulo] end");
	}

	public void setSeleccionComparativaSWDao(final ISeleccionComparativaSWDao seleccionComparativaSWDao) {
		this.seleccionComparativaSWDao = seleccionComparativaSWDao;
	}

	public void setImportacionPolizasService(final IImportacionPolizasService importacionPolizasService) {
		this.importacionPolizasService = importacionPolizasService;
	}

	public void setClaseDetalleDao(final IClaseDetalleDao claseDetalleDao) {
		this.claseDetalleDao = claseDetalleDao;
	}
}