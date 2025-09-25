package com.rsi.agp.core.jmesa.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.filter.FicheroUnificadoFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.comisiones.IUtilidadesComisionesDao;
import com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao.PorcentajesAplicar;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.AseguradoUnif17;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.GrupoNegocioUnif17;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.PolizaUnif17;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.ReciboUnif17;
import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GastosAbonadosDeudaAplazadaUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GrupoNegocioUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ReciboUnificado;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

public class ImportacionComisionesUnificadoDao extends BaseDaoHibernate
		implements IImportacionComisionesUnificadoDao {
	
	private static final Log LOGGER = LogFactory.getLog(ImportacionComisionesUnificadoDao.class);
	
	private IUtilidadesComisionesDao utilidadesComisionesDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort,final int rowStart,
			final int rowEnd) throws BusinessException {
		
		try {
			
			
			LOGGER.debug("init - [ImportacionComisionesUnificadoDao] getWithFilterAndSort");
			List<Serializable> informes =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {							
							final FicheroUnificadoFilter filtro= (FicheroUnificadoFilter)filter;	
							//filtro.execute();
						
							Criteria criteria=null;
							criteria = session.createCriteria(FicheroUnificado.class);
							// Filtro
							criteria = filtro.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<FicheroUnificado> lista = criteria.list();
							return lista;
							
						}
					});
			LOGGER.debug("end - [ImportacionComisionesUnificadoDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		LOGGER.debug("init - [ImportacionComisionesUnificadoDao] getCountWithFilter");
		final FicheroUnificadoFilter filtro= (FicheroUnificadoFilter)filter;	
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							
							Criteria criteria=null;
							criteria = session.createCriteria(FicheroUnificado.class);
							criteria = filtro.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			LOGGER.debug("end - [ImportacionComisionesUnificadoDao] getCountWithFilter");
			return count.intValue();	
		
	}

	@Override
	public Boolean existeFicheroImportado(String nombreFichero) throws DAOException {
		LOGGER.debug("init - ficheroImportado");
		Session session = obtenerSession();
		Integer res=null;
		try {
			
			Criteria criteria = session.createCriteria(FicheroUnificado.class);
			
			criteria.add(Restrictions.ilike("nombreFichero", nombreFichero));
			criteria.setProjection(Projections.rowCount());

			LOGGER.debug("end - ficheroImportado");
			res = (Integer) criteria.uniqueResult();
			return (res > 0);
			
		} catch (Exception ex) {
			LOGGER.error("existeFicheroImportado - Se ha producido un error en el acceso a la BBDD", ex);
			throw new DAOException("existeFicheroImportado - Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	
	public void saveFicheroUnificado(FicheroUnificado fichero) throws DAOException {
		Session session = obtenerSession();
		Transaction transaccion=null;
		try {
			//Paradigma Memoization **********************************************************************
			//Creamos los mapas que va a utilizar utilidadesComisionesDao para gtuaradr las búsquedas
			//Los creamos aqui porque necesitamos que se destruyan los objetos una vez finalizado el proceso
			//y no podemos declararlos a nivel de clase en utilidadesComisionesDao por el despliegue de Spring
			Map<String, ColectivosRenovacion> colColectivos = new HashMap<String, ColectivosRenovacion>(); 
			Map<String, Colectivo> colectivos = new HashMap<String, Colectivo>();
			Map<String, Linea> colLineas=new HashMap<String, Linea>();
			Map<String, PorcentajesAplicar> colPorcentajesAplicar = new HashMap<String, PorcentajesAplicar>();
			Map<String, BigDecimal> colPorcentajeMaximo = new HashMap<String, BigDecimal>(); 			
			
			
			// ******************************************************************************************
			transaccion=session.beginTransaction();
			
			//Guardamos la tabla fichero
			session.saveOrUpdate(fichero);
			
			//Solo si venimos de carga de fichero
			if (null==fichero.getFicheroContenido().getIdfichero()) { 
				//Asignamos id al objeto ficheroContenido y guardamos
				fichero.getFicheroContenido().setIdfichero(fichero.getId());
				session.save(fichero.getFicheroContenido());
			}
			
			for(FaseUnificado fase: fichero.getFases()) {
				fase.getFichero().setId(fichero.getId());
				saveFase(fase, session, fichero.getTipoFichero(), colColectivos, colLineas,
						colPorcentajesAplicar,colPorcentajeMaximo, colectivos );
			}

		} catch (Exception ex) {
			LOGGER.error("Error al grabar el fichero de comisiones unificado en la base de datos", ex);
			if(transaccion !=null) {
				transaccion.rollback();
			}
			throw new DAOException("Se ha producido un error guardando el fichero de comisiones unificado en la base de datos", ex);			
		}
	}
	
	private void saveFase(FaseUnificado fase, Session session, Character tipoFichero, 
			Map<String, ColectivosRenovacion> colColectivos,Map<String, Linea> colLineas,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar, 
			Map<String, BigDecimal> colPorcentajeMaximo, Map<String, Colectivo> colectivos) throws Exception {
			
		try {
			session.saveOrUpdate(fase);
		} catch (Exception ex) {
			LOGGER.error("Error al grabar una fase del fichero de comisiones unificado en la base de datos.");
			throw ex;
		}
			
		// Tres formas de grabar según la procedencia
		if(tipoFichero.equals(new Character('D'))){//Deuda aplazada
			for(IndividualColectivoUnificado indCol:fase.getIndividualColectivos()) {
				indCol.setFase(fase);
				saveIndividualColectivoUnificado(indCol, fase, session, colColectivos, 
						colLineas, colPorcentajesAplicar,colPorcentajeMaximo );
			}
		}else if(tipoFichero.equals(new Character('U'))){//Gastos entidad
			for(ReciboUnif17 rec: fase.getRecibos()) {
				saveReciboUnif17(rec,session);
				for(PolizaUnif17 pol: rec.getPolizas()) {
					pol.setRecibo(rec);
					savePolizaUnif17(fase.getPlan().longValue(),
							rec.getLinea().longValue(), pol, session, colColectivos,
							colLineas, colPorcentajesAplicar,
							colPorcentajeMaximo);
				}
			}
		}else {//Deuda aplazada
			for(ReciboUnificado rec: fase.getReciboUnificados()) {
				//IndividualColectivoUnificado
				if (null!=rec.getIndividual()) {
					this.saveIndividualUnificado(rec.getIndividual(), session);
				}
				if(null!=rec.getColectivo()) {
					this.saveColectivoUnificado(rec.getColectivo(), session);			
				}					
				saveIndividualColectivoUnificado(rec.getIndividual(), rec.getColectivo(), session);	
				saveReciboUnificado(rec,session);	
				rec.getAplicacion().setRecibo(rec);
				saveAplicacionUnificado(Long.valueOf(fase.getPlan()
						.longValue()), rec.getAplicacion()
						.getGrupoNegocios(), rec.getAplicacion(), session,
						colColectivos, colLineas, colPorcentajesAplicar,
						colPorcentajeMaximo, fase.getFechaEmisionRecibo());					
			}
		}
	}

	//Métodos para guardar datos de Deuda aplazada
	private void saveIndividualColectivoUnificado(IndividualColectivoUnificado indCol, FaseUnificado fase, Session session,
			Map<String, ColectivosRenovacion> colColectivos,Map<String, Linea> colLineas,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar, Map<String, BigDecimal> colPorcentajeMaximo)throws DAOException {
	
		try {
				if (indCol.getColectivo()!=null) {
					saveColectivoUnificado(indCol.getColectivo(), session);					
				}		
				if (indCol.getIndividual()!=null) {
					saveIndividualUnificado(indCol.getIndividual(), session);					
				}
				//COMPROBAR QUE COLECTIVO E INDIVIDUAL TRAEN SUS IDS Y SI NO TRATAR!!!!!!!!!!!!
				session.saveOrUpdate(indCol);
				for(AplicacionUnificado apl:indCol.getAplicacions()) {
					apl.setIndividualColectivo(indCol);
				saveAplicacionUnificado(
						Long.valueOf(fase.getPlan().longValue()), apl,
						apl.getRecibos(), fase, session, colColectivos,
						colLineas, colPorcentajesAplicar, colPorcentajeMaximo);
				}
				
		}catch(Exception ex) {
			LOGGER.error("Error al grabar los datos Individual/Colectivo unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando los datos Individual/Colectivo unificado del fichero de comisiones unificado en la base de datos", ex);
			
		}
	}
	
	private void saveAplicacionUnificado(Long codPlan, AplicacionUnificado apl, Set<ReciboUnificado> rec, FaseUnificado fase, Session session,
			Map<String, ColectivosRenovacion> colColectivos,Map<String, Linea> colLineas,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar, Map<String, BigDecimal> colPorcentajeMaximo)throws DAOException {
		session.saveOrUpdate(apl);
		for(ReciboUnificado recUnif:rec) {
			recUnif.setAplicacion(apl);
			recUnif.setFase(fase);
			if(apl.getIndividualColectivo().getIndividual()!=null) {
				recUnif.setIndividual(apl.getIndividualColectivo().getIndividual());
			}else {//Colectivo
				recUnif.setColectivo(apl.getIndividualColectivo().getColectivo());
			}
			saveReciboUnificado(recUnif, session);
			for(GrupoNegocioUnificado gn:recUnif.getGrupoNegocios()) {
				gn.setRecibo(recUnif);
				gn.setAplicacion(apl);
				
				//Cálculo de los porcentajes de entidad subentidad mediadora para los gastos del grupo de negocio
				com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao.PorcentajesAplicar porcentajesAplicar = utilidadesComisionesDao
						.obtenerPorcentajesComision(apl.getReferencia(), apl.getTipoReferencia(), codPlan,
								gn.getGrupoNegocio(), colColectivos,
								colPorcentajesAplicar, colPorcentajeMaximo,
								colLineas, apl, fase.getFechaEmisionRecibo());				
				this.calculoComisionMediadora(gn, porcentajesAplicar);
				
				// -------------------------------------------------------------------------------------------------
				
				saveGrupoNegocioUnificado(gn, session);
				for(GastosAbonadosDeudaAplazadaUnificado gaDa:gn.getGastosAbonadosDeudaAplazadaUnificados()) {
					gaDa.setGrupoNegocioUnificado(gn);
					
					//Cálculo de los porcentajes de entidad subentidad mediadora para los gastos específicos de deuda aplazada
					this.calculoComisionMediadora(gaDa, porcentajesAplicar);					
					//-------------------------------------------------------------------------------
					
					saveGastosAbonadosDeudaAplazada(gaDa, session);
				}
			}
		}
	}
//----------------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------------
	//Métodos para el resto de fichero
	private void saveIndividualColectivoUnificado(IndividualUnificado ind, ColectivoUnificado col, Session session)throws DAOException {
		try {
			
				if (col!=null) {
					saveColectivoUnificado(col, session);
				}		
				if (ind!=null) {
					saveIndividualUnificado(ind, session);
				}
		}catch(Exception ex) {
			LOGGER.error("Error al grabar los datos Individual/Colectivo unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando los datos Individual/Colectivo unificado del fichero de comisiones unificado en la base de datos", ex);
			
		}
	}
	
	private void saveAplicacionUnificado(Long codPlan, Set<GrupoNegocioUnificado> gn, AplicacionUnificado apl,
			Session session, Map<String, ColectivosRenovacion> colColectivos, Map<String, Linea> colLineas,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar, Map<String, BigDecimal> colPorcentajeMaximo,
			Date fechaEmision) throws DAOException {
		session.saveOrUpdate(apl);
		for (GrupoNegocioUnificado gnu : gn) {
			gnu.setAplicacion(apl);

			// Cálculo de los porcentajes de entidad subentidad mediadora
			com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao.PorcentajesAplicar porcentajesAplicar = utilidadesComisionesDao
					.obtenerPorcentajesComision(apl.getReferencia(), apl.getTipoReferencia(), codPlan,
							gnu.getGrupoNegocio(), colColectivos, colPorcentajesAplicar, colPorcentajeMaximo, colLineas,
							apl, fechaEmision);
			this.calculoComisionMediadora(gnu, porcentajesAplicar);
			// -------------------------------------------------------------------------------

			saveGrupoNegocioUnificado(gnu, session);
		}
	}
	
	private void saveGastosAbonadosDeudaAplazada(GastosAbonadosDeudaAplazadaUnificado gaDa, Session session)throws DAOException{
		try {
			session.saveOrUpdate(gaDa);			
		}catch(Exception ex) {
			LOGGER.error("Error al grabar los gastos abonados de deuda aplazada del fichero de comisiones unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando los gastos abonados de deuda aplazada del fichero de comisiones unificado en la base de datos", ex);
			
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------
	
	private void calculoComisionMediadora(GrupoNegocioUnificado gnu,com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao.PorcentajesAplicar porcentajesAplicar) {
		if(null!= gnu.getGaComisionMediador() && gnu.getGaComisionMediador().compareTo(new BigDecimal(0.00))!=0) {
			gnu.setGaCommedEntidad(this.getImporteComision( gnu.getGaComisionMediador(), porcentajesAplicar.getPctEntidad()));
			gnu.setGaCommedEsmed(this.getImporteComision(gnu.getGaComisionMediador(), porcentajesAplicar.getPctESmediadora()));
		}else{
			gnu.setGaCommedEntidad(this.getImporteComision( gnu.getGaComisionMediador(), null));
			gnu.setGaCommedEsmed(this.getImporteComision(null, null));
		}
		if(null!=gnu.getGdComisionMediador() && gnu.getGdComisionMediador().compareTo(new BigDecimal(0.00))!=0) {
			gnu.setGdCommedEntidad(this.getImporteComision(gnu.getGdComisionMediador(), porcentajesAplicar.getPctEntidad()));
			gnu.setGdCommedEsmed(this.getImporteComision(gnu.getGdComisionMediador(),porcentajesAplicar.getPctESmediadora()));
		}else{
			gnu.setGdCommedEntidad(this.getImporteComision(gnu.getGdComisionMediador(), null));
			gnu.setGdCommedEsmed(this.getImporteComision(null, null));		
		}
		
		if(null!= gnu.getGpComisionMediador()&& gnu.getGpComisionMediador().compareTo(new BigDecimal(0.00))!=0) {
			gnu.setGpCommedEntidad(this.getImporteComision(gnu.getGpComisionMediador(), porcentajesAplicar.getPctEntidad()));
			gnu.setGpCommedEsmed(this.getImporteComision(gnu.getGpComisionMediador(), porcentajesAplicar.getPctESmediadora()));
		}else{
			gnu.setGpCommedEntidad(this.getImporteComision(gnu.getGpComisionMediador(), null));
			gnu.setGpCommedEsmed(this.getImporteComision(null, null));	
		}
		
		if(null!= gnu.getGpiComisionMediador() && gnu.getGpiComisionMediador().compareTo(new BigDecimal(0.00))!=0) {
			gnu.setGpiCommedEntidad(this.getImporteComision(gnu.getGpiComisionMediador(), porcentajesAplicar.getPctEntidad()));
			gnu.setGpiCommedEsmed(this.getImporteComision(gnu.getGpiComisionMediador(), porcentajesAplicar.getPctESmediadora()));
		}else{
			gnu.setGpiCommedEntidad(this.getImporteComision(gnu.getGpiComisionMediador(), null));
			gnu.setGpiCommedEsmed(this.getImporteComision(null, null));
		}
		
	}
	
	private void calculoComisionMediadora(GastosAbonadosDeudaAplazadaUnificado gaDa,com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao.PorcentajesAplicar porcentajesAplicar) {
		if(null!=gaDa.getGaComisionMediador() && gaDa.getGaComisionMediador().compareTo(new BigDecimal(0.00))!=0) {
			gaDa.setGaCommedEntidad(this.getImporteComision(gaDa.getGaComisionMediador(), porcentajesAplicar.getPctEntidad()));
			gaDa.setGaCommedEsmed(this.getImporteComision(gaDa.getGaComisionMediador(), porcentajesAplicar.getPctESmediadora()));
		}else{
			gaDa.setGaCommedEntidad(this.getImporteComision(gaDa.getGaComisionMediador(), null));
			gaDa.setGaCommedEsmed(this.getImporteComision(null, null));	
		}
	}
	
	private BigDecimal getImporteComision(final BigDecimal comision, final BigDecimal porcentaje) {
		
		BigDecimal importeComision = BigDecimal.ZERO;
		final BigDecimal bigDecimalCien = new BigDecimal("100.00");
		
		if (null != comision && null!= porcentaje){
			importeComision = comision.multiply(porcentaje).divide(bigDecimalCien, 2, RoundingMode.HALF_UP);
		}
		
		return importeComision.setScale(2, RoundingMode.HALF_UP);
	}
	
	private void saveReciboUnificado(ReciboUnificado rec, Session session)throws DAOException {
		try {
			session.saveOrUpdate(rec);			
		}catch(Exception ex) {
			LOGGER.error("Error al grabar el Recibo unificado del fichero de comisiones unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando Recibo unificado del fichero de comisiones unificado en la base de datos", ex);
			
		}
	}
	
	
	private void saveIndividualUnificado(IndividualUnificado ind, Session session)throws DAOException {
		try {
			session.saveOrUpdate(ind);
		}catch(Exception ex) {
			LOGGER.error("Error al grabar el objeto Individual unificado del fichero de comisiones unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando objeto Individual unificado del fichero de comisiones unificado en la base de datos", ex);
			
		}
	}
	
	private void saveColectivoUnificado(ColectivoUnificado col, Session session)throws DAOException {
		try {
			session.saveOrUpdate(col);
		}catch(Exception ex) {
			LOGGER.error("Error al grabar el objeto Colectivo unificado del fichero de comisiones unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando objeto Colectivo unificado del fichero de comisiones unificado en la base de datos", ex);
			
		}
	}
	

	private void saveGrupoNegocioUnificado(GrupoNegocioUnificado  gn, Session session)throws DAOException {
		try {
			session.saveOrUpdate(gn);			
		}catch(Exception ex) {
			LOGGER.error("Error al grabar el grupo de negocio unificado del fichero de comisiones unificado en la base de datos", ex);			
			throw new DAOException("Se ha producido un error guardando el grupo de negocio unificado del fichero de comisiones unificado en la base de datos", ex);			
		}
	}
	
	public void setUtilidadesComisionesDao(
			IUtilidadesComisionesDao utilidadesComisionesDao) {
		this.utilidadesComisionesDao = utilidadesComisionesDao;
	}

	@Override
	public void validarFicheroComisiones(Long idFichero, Character tipoFichero) throws DAOException {
		LOGGER.debug("init - validarFicheroComisiones");
		String procedure =null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			if(tipoFichero.equals(new Character('C'))){				
				procedure ="o02agpe0.PQ_VALIDAR_COMS_UNIF.doValidarFichComs2015(IDFICHERO IN NUMBER)";
			}else if (tipoFichero.equals(new Character('I'))) {
				procedure ="o02agpe0.PQ_VALIDAR_IMPAGADOS_UNIF.doValidarFichImpagados2015(idFichero IN NUMBER)";				
			}else if (tipoFichero.equals(new Character('D'))) {
				procedure ="o02agpe0.PQ_VALIDAR_DEUDA_APLAZADA.doValidarFicheroDeudaAplazada(IDFICHERO IN NUMBER)";
			}else if (tipoFichero.equals(new Character('U'))) {
				procedure ="o02agpe0.PQ_VALIDAR_COMS_UNIF17.doValidarFichGastosEntidad(IDFICHERO IN NUMBER)";
			}  			
			parametros.put("IDFICHERO", idFichero);
			LOGGER.debug("Llamada al procedimiento " + procedure + " con los siguientes parámetros: ");
			LOGGER.debug("    IDFICHERO: " + idFichero);
			databaseManager.executeStoreProc(procedure, parametros);
		}catch(Exception ex) {
			throw new DAOException ("Error validando el fichero de comisiones unificadas", ex);
		}
		LOGGER.debug("end - validarFicheroComisiones");
	}
	
	private void saveReciboUnif17(ReciboUnif17 rec, Session session)throws DAOException {
		try {
			session.saveOrUpdate(rec);			
		}catch(Exception ex) {
			LOGGER.error("Error al grabar el Recibo unificado del fichero de comisiones unificado en la base de datos", ex);
			LOGGER.error("############################");
			LOGGER.error("");
			LOGGER.error("");
			LOGGER.error("Error al grabar el Recibo unificado del fichero de comisiones unificado en la base de datos");
			LOGGER.error("");
			LOGGER.error("");
			LOGGER.error("############################");
			throw new DAOException("Se ha producido un error guardando Recibo unificado del fichero de comisiones unificado en la base de datos", ex);			
		}
	}
	
	private void savePolizaUnif17(Long codPlan, Long codLinea, PolizaUnif17 pol,
			Session session, Map<String, ColectivosRenovacion> colColectivos,
			Map<String, Linea> colLineas,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar,
			Map<String, BigDecimal> colPorcentajeMaximo) throws Exception {
		try {
			AseguradoUnif17 aseg = pol.getAsegurado();
			session.saveOrUpdate(aseg);
			pol.setAsegurado(aseg);
		} catch (Exception ex) {
			LOGGER.error(
					"Error al grabar el asegurado de la póliza del fichero de comisiones unificado en la base de datos.");
			throw ex;
		}
		try {
			if (pol.getIndividualUnificado() != null) {
				IndividualUnificado individualUnificado = pol
						.getIndividualUnificado();
				session.saveOrUpdate(individualUnificado);
				pol.setIndividualUnificado(individualUnificado);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error al grabar el individual de la póliza del fichero de comisiones unificado en la base de datos.");
			throw ex;
		}
		try {
			if (pol.getColectivoUnificado() != null) {
				ColectivoUnificado colectivoUnificado = pol
						.getColectivoUnificado();
				session.saveOrUpdate(colectivoUnificado);
				pol.setColectivoUnificado(colectivoUnificado);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error al grabar el colectivo de la póliza del fichero de comisiones unificado en la base de datos.");
			throw ex;
		}
		try {
			session.saveOrUpdate(pol);
		} catch (Exception ex) {
			LOGGER.error(
					"Error al grabar la póliza del fichero de comisiones unificado en la base de datos.");
			throw ex;
		}
		
		final BigDecimal CIEN = new BigDecimal("100.00");
		BigDecimal pctEntidad = null;
		BigDecimal pctESmediadora = null;
		BigDecimal gdImpCommedEntidad = null;
		BigDecimal gdImpCommedEsmed = null;
		BigDecimal gaImpCommedEntidad = null;
		BigDecimal gaImpCommedEsmed = null;
		BigDecimal gpImpCommedEntidad = null;
		BigDecimal gpImpCommedEsmed = null;
		
		try {
			
			for (GrupoNegocioUnif17 gn : pol.getGrupoNegocios()) {
		
				pctEntidad = BigDecimal.ZERO;
				pctESmediadora = BigDecimal.ZERO;
				
				// Cálculo de los porcentajes de entidad subentidad mediadora para los gastos del grupo de negocio
				PorcentajesAplicar porcentajes = utilidadesComisionesDao.obtenerPorcentajesComisionUnif17(
						pol.getReferencia(), pol.getTipoReferencia(), codPlan, gn.getGrupoNegocio(),
						pol.getRecibo().getFaseUnificado().getFechaEmisionRecibo());
				
				if (porcentajes != null) {
					
					// SOLO CALCULAMOS EL PORCENTAJE DE LA ENTIDAD SI EL ASIGNADO ES DISTINTO DE 0 Y 100
					if (BigDecimal.ZERO.compareTo(porcentajes.getPctEntidad()) != 0 && CIEN.compareTo(porcentajes.getPctEntidad()) != 0 && BigDecimal.ZERO.compareTo(gn.getGdPctComMediador()) != 0) {
						if (aplicaCalcPorcComis(pol.getReferencia(), codPlan, codLinea, session)) {
							pctEntidad = porcentajes.getPctComMax().multiply(porcentajes.getPctEntidad()).divide(gn.getGdPctComMediador(), 2, RoundingMode.HALF_UP);
						} else {
							pctEntidad = porcentajes.getPctEntidad();
						}
					} else {
						pctEntidad = porcentajes.getPctEntidad();
					}
					// SOLO CALCULAMOS POR DIFERENCIA EL PORCENTAJE DE LA E-S MED SI EL ASIGNADO ES DISTINTO DE 0 Y 100
					if (BigDecimal.ZERO.compareTo(porcentajes.getPctESmediadora()) != 0 && CIEN.compareTo(porcentajes.getPctESmediadora()) != 0) {
						pctESmediadora = CIEN.subtract(pctEntidad);
					} else {
						pctESmediadora = porcentajes.getPctESmediadora();
					}					
					// SOLO CALCULAMOS DESCUENTO Y RECARGO SI EL PORCENTAJE DE LA E-S MED ES DISTINTO DE 0 Y 100
					if (BigDecimal.ZERO.compareTo(pctESmediadora) != 0 && CIEN.compareTo(pctESmediadora) != 0) {
						if (BigDecimal.ZERO.compareTo(porcentajes.getPctDescelegido()) != 0) {
							pctESmediadora = pctESmediadora.multiply(BigDecimal.ONE.subtract(porcentajes.getPctDescelegido().divide(CIEN, 2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP);
						} else if (BigDecimal.ZERO.compareTo(porcentajes.getPctrRecarelegido()) != 0) {
							pctESmediadora = pctESmediadora.multiply(BigDecimal.ONE.add(porcentajes.getPctrRecarelegido().divide(CIEN, 2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP);
						} 
					}
					
					gn.setGdPctComMedEnt(pctEntidad);
					gn.setGdPctComMedEsmed(pctESmediadora);
					
					gdImpCommedEntidad = this.getImporteComision(gn.getGdImpComMediador(), pctEntidad);
					gdImpCommedEsmed = gn.getGdImpComMediador().subtract(gdImpCommedEntidad);
					gn.setGdImpCommedEntidad(gdImpCommedEntidad);
					gn.setGdImpCommedEsmed(gdImpCommedEsmed);
	
					gaImpCommedEntidad = this.getImporteComision(gn.getGaComisionMediador(), pctEntidad);
					gaImpCommedEsmed = gn.getGaComisionMediador().subtract(gaImpCommedEntidad);
					gn.setGaCommedEntidad(gaImpCommedEntidad);
					gn.setGaCommedEsmed(gaImpCommedEsmed);
	
					gpImpCommedEntidad = this.getImporteComision(gn.getGpComisionMediador(), pctEntidad);
					gpImpCommedEsmed = gn.getGpComisionMediador().subtract(gpImpCommedEntidad);
					gn.setGpCommedEntidad(gpImpCommedEntidad);
					gn.setGpCommedEsmed(gpImpCommedEsmed);
				}
				
				gn.setPoliza(pol);
				
				session.saveOrUpdate(gn);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error al grabar el grupo de negocio del fichero de comisiones unificado en la base de datos.");
			LOGGER.error("referencia -> " + pol.getReferencia());
			LOGGER.error("pctEntidad -> " + pctEntidad);
			LOGGER.error("pctESmediadora -> " + pctESmediadora);
			LOGGER.error("gdImpCommedEntidad -> " + gdImpCommedEntidad);
			LOGGER.error("gdImpCommedEsmed -> " + gdImpCommedEsmed);
			LOGGER.error("gaImpCommedEntidad -> " + gaImpCommedEntidad);
			LOGGER.error("gaImpCommedEsmed -> " + gaImpCommedEsmed);
			LOGGER.error("gpImpCommedEntidad -> " + gpImpCommedEntidad);
			LOGGER.error("gpImpCommedEsmed -> " + gpImpCommedEsmed);
			throw ex;
		}
	}
	
	private Boolean aplicaCalcPorcComis(final String referencia,
			final Long codPlan, final Long codLinea, final Session session) {
		Boolean result = Boolean.TRUE;
		// UNICAMENTE CALCULAMOS EN POLIZAS NO RENOVABLES
		// Y SOLO EN LINEAS DE GANADO
		if (codLinea.compareTo(Long.valueOf(400)) >= 0) {
			Criteria criteria = session.createCriteria(PolizaRenovable.class);
			criteria.add(Restrictions.eq("referencia", referencia));
			criteria.add(Restrictions.eq("plan", codPlan));
			Integer count = (Integer) criteria.setProjection(
					Projections.rowCount()).uniqueResult();
			result = count <= 0;
		}
		return result;
	}
	
	// Metodo que devuelve el password del buzon Infovia
	public String getPasswordBuzonInfovia() {
		try {
			Parametro param = (Parametro) findAll(Parametro.class).get(0);
			return param.getPasswordBuzonInfovia();
		} catch (Exception ex) {
			logger.error(" Error al recoger el password del buzon Infovia en BBDD : ", ex);
			return null;
		}
	}
}
