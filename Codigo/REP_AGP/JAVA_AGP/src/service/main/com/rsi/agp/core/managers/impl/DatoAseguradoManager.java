package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.admin.impl.DatoAseguradoFiltro;
import com.rsi.agp.dao.filters.poliza.SeleccionPolizaFiltro;
import com.rsi.agp.dao.models.admin.IDatoAseguradoDao;
import com.rsi.agp.dao.models.poliza.IPagoPolizaDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.cgen.LineaCondicionado;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class DatoAseguradoManager implements IManager {
	
	private static final Log LOGGER = LogFactory.getLog(DatoAseguradoManager.class);
	
	private IDatoAseguradoDao datoAseguradoDao;
	private IPagoPolizaDao pagoPolizaDao;
	
	final String tituloListadoPolizas = "Listado de pólizas del asegurado para actualizar el numero de cuenta";

	@SuppressWarnings("unchecked")
	public final List<DatoAsegurado> getDatosAsegurado(final DatoAsegurado datoAseguradoBean) {
		
		final DatoAseguradoFiltro filter = new DatoAseguradoFiltro(datoAseguradoBean);
		return datoAseguradoDao.getObjects(filter);
	}

	public final DatoAsegurado getDatoAsegurado(Long id) {
		return (DatoAsegurado) datoAseguradoDao.getObject(DatoAsegurado.class, id);
	}
	
	public boolean existeDatoAsegurado (Long idAsegurado, BigDecimal codLinea){
		boolean existe = false;
		existe = this.datoAseguradoDao.existeDatoAsegurado(idAsegurado, codLinea);
		return existe;
	}
	
	public boolean duplicaLinea (Long idasegurado,Long id, BigDecimal codLinea){
		boolean existe = false;
		existe = this.datoAseguradoDao.duplicaLinea(idasegurado,id, codLinea);
		return existe;
	}

	public final void saveDatoAsegurado(final DatoAsegurado aseguradoBean) throws Exception{
		try {
			datoAseguradoDao.saveOrUpdate(aseguradoBean);
			datoAseguradoDao.evict(aseguradoBean);
		} catch (Exception e){
			LOGGER.error("Error al guardar los datos del asegurado", e);
			throw e;
			
		}
	}
	
	
	
	public final void dropDatoAsegurado(final Long id) throws DAOException {
		datoAseguradoDao.delete(DatoAsegurado.class, id);
	}

	public final LineaCondicionado getLineaComun() {
		return (LineaCondicionado) datoAseguradoDao.getObject(LineaCondicionado.class, new BigDecimal("999"));
	}

	@SuppressWarnings("unchecked")
	public final List<Poliza> getPolizasByIdAsegurado(final Long id,BigDecimal codLinea,BigDecimal[] estados) {
		final Poliza poliza = new Poliza();
		poliza.getAsegurado().setId(id);
		if (null!=codLinea && !codLinea.equals(Constants.CODLINEA_GENERICA)){ // si es la 999 no la añadimos en la consulta. Nos traemos todas
			poliza.getLinea().setCodlinea(codLinea);
		}
		final SeleccionPolizaFiltro filter = new SeleccionPolizaFiltro(poliza);
		
		if (null != estados){
			filter.setEstadosPolizaIncluir(estados);
		}
		
		return datoAseguradoDao.getObjects(filter);
	}
	
	public void actualizaIbanPolizasAseg(Long idAsegurado, BigDecimal codLinea, HttpServletRequest request,
			DatoAsegurado datoAseguradoBean) throws Exception {

		LOGGER.debug("actualizamos los datos de pago de las polizas del popup");
		try {
			String listIdPolizasMod = request.getParameter("listIdPolizas");
			String iban = request.getParameter("iban");
			String cuenta1 = request.getParameter("cuenta1");
			String cuenta2 = request.getParameter("cuenta2");
			String cuenta3 = request.getParameter("cuenta3");
			String cuenta4 = request.getParameter("cuenta4");
			String cuenta5 = request.getParameter("cuenta5");
			String cccbanco = cuenta1 + cuenta2 + cuenta3 + cuenta4 + cuenta5;
			String iban2 = request.getParameter("iban2");
			String cuenta6 = request.getParameter("cuenta6");
			String cuenta7 = request.getParameter("cuenta7");
			String cuenta8 = request.getParameter("cuenta8");
			String cuenta9 = request.getParameter("cuenta9");
			String cuenta10 = request.getParameter("cuenta10");
			String cccbanco2 = cuenta6 + cuenta7 + cuenta8 + cuenta9 + cuenta10;
			pagoPolizaDao.updateIbanbyPoliza(listIdPolizasMod, iban, cccbanco,
					datoAseguradoBean.getDestinatarioDomiciliacion(), datoAseguradoBean.getTitularCuenta(), iban2,
					cccbanco2);
			datoAseguradoBean.setCcc(cccbanco);
			datoAseguradoBean.setCcc2(cccbanco2);
		} catch (Exception e) {
			LOGGER.error("Error al actualizar el iban en las polizas del asegurado", e);
			throw e;

		}
	}

	public boolean existeLineaCondicionado(BigDecimal codlinea) {
		if (datoAseguradoDao.getObject(LineaCondicionado.class, codlinea) != null){
			return true;
		}
		
		return false;
	}
	
	public void showPopupDatosAsegurados(BigDecimal codLinea,Long idAsegurado, Map<String, Object> parameters)throws Exception{
		LOGGER.debug("init - showPopupDatosAsegurados - DatoAseguradoController");
		
		List<Poliza> polizasAsegurado = new LinkedList<Poliza>();
		String listIdPolizasMod = "";
		BigDecimal[] estados = new BigDecimal[2];	
		
		try{
			LOGGER.debug ("INIT - showPopupDatosAsegurados - comprobamos si el asegurado tiene alguna poliza en estado " +
					"pendiente de validacion o grabacion provisional");
			
			
			//estados poliza grabacion provisional y pendiente de validacion
			estados[0] = Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION;
			estados[1] = Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL;
			
			polizasAsegurado = this.getPolizasByIdAsegurado
						(idAsegurado,codLinea,estados);
			
			
			if (polizasAsegurado.size()>0){
				parameters.put("polizasAsegurado", polizasAsegurado);
				parameters.put("showPopupPolAsegurados", "true");
				parameters.put("tituloListadoPolizasAseg",tituloListadoPolizas);
				//guardamos los ids de las polizas para luego no tener que volver
				//a consultarlas en caso de que le de actualizar del popup
				
				for (int i=0; i< polizasAsegurado.size();i++){
					Poliza p = polizasAsegurado.get(i);
					if (i==0)
						listIdPolizasMod+=p.getIdpoliza() ;
					else
						listIdPolizasMod+= "," + p.getIdpoliza();
				}
				parameters.put("listIdPolizas",listIdPolizasMod);
			}
		
		}catch (Exception e) {
			LOGGER.debug ("ERROR al mostrar el popup de las polizas de los asegurados",e);
	
		}
	}
	public final Asegurado getAseguradoById(final Long idAsegurado) {
		return (Asegurado) datoAseguradoDao.getObject(Asegurado.class, idAsegurado);
	}

	public final Entidad getEntidadById(final BigDecimal codentidad) {
		return (Entidad) datoAseguradoDao.getObject(Entidad.class, codentidad);
	}

	public final void setDatoAseguradoDao(final IDatoAseguradoDao datoAseguradoDao) {
		this.datoAseguradoDao = datoAseguradoDao;
	}
	
	public void setPagoPolizaDao(IPagoPolizaDao pagoPolizaDao) {
		this.pagoPolizaDao = pagoPolizaDao;
	}

	public BigDecimal getLineaAsegurado(Long idDatoAsegurado) {		
		return datoAseguradoDao.getCodLinea(idDatoAsegurado);
	}

	
}
