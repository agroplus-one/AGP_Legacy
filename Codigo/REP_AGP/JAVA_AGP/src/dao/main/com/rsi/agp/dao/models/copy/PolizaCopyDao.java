package com.rsi.agp.dao.models.copy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.config.DatoVariableDefault;
import com.rsi.agp.dao.tables.copy.Poliza;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;

public class PolizaCopyDao extends BaseDaoHibernate implements IPolizaCopyDao {

	private static final Log logger = LogFactory.getLog(PolizaCopyDao.class);
	private IPolizaDao polizaDao ;

	@Override
	public Poliza getPolizaCopyById(Long idPoliza) throws DAOException {
		
		return (Poliza) get(Poliza.class, idPoliza);
	}
	
	@Override
	public List<Poliza> getByRefPoliza(String refpoliza) throws DAOException {
		
		return findFiltered(Poliza.class,"refpoliza" ,refpoliza);
	}
	
	public com.rsi.agp.dao.tables.copy.Poliza existeCopyPolizaByReferenciaAndFecha(String tipoReferencia, String refpoliza, Date fechaEmisionRecibo) throws DAOException {
		
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.add(Restrictions.eq("refpoliza", refpoliza));
			criteria.add(Restrictions.eq("tiporef", new Character(tipoReferencia.charAt(0))));
			criteria.add(Restrictions.eq("fecemisionrecibo", fechaEmisionRecibo));
			//criteria.setProjection(Projections.rowCount());
			
			return ((com.rsi.agp.dao.tables.copy.Poliza)criteria.uniqueResult());
			
		}catch(Exception e){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",e);
		}finally{
		}
	}
	
	public Poliza getPolizaCopyMasRecienteByReferencia(Character tipoReferencia, String refpoliza) throws DAOException {
		
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
		
			criteria.add(Restrictions.eq("refpoliza", refpoliza));
			criteria.add(Restrictions.eq("tiporef", tipoReferencia));
			criteria.addOrder(Order.desc("fecemisionrecibo"));
			
			List resultado = criteria.list();
			
			if(resultado.isEmpty())
				return null;
			
			return (Poliza)resultado.get(0);
			
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",	ex);
		} finally {
		}
	}
	
	public List<Poliza> getListaPolizas(Poliza polizaBean) throws DAOException{
		
		Session session = obtenerSession();
		try{
			Criteria criteria = session.createCriteria(Poliza.class);
			
			if (FiltroUtils.noEstaVacio(polizaBean.getCodlinea())) {
				criteria.add(Restrictions.eq("codlinea", polizaBean.getCodlinea()));
			}
			if (FiltroUtils.noEstaVacio(polizaBean.getCodplan())) {
				criteria.add(Restrictions.eq("codplan", polizaBean.getCodplan()));
			}
			/*final BigDecimal codEntidad = polizaBean.getColectivo().getTomador().getId().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidad)) {
				criteria.add(Restrictions.eq("col.tomador.id.codentidad", codEntidad));
			}*/
			if (polizaBean.getTiporef()!=null){
				if (!polizaBean.getTiporef().equals('T')){
					criteria.add(Restrictions.eq("tiporef", polizaBean.getTiporef()));
				}
			}
			if (polizaBean.getRefpoliza()!=null){
				criteria.add(Restrictions.eq("tiporef", polizaBean.getTiporef()));
			}
			
			if((polizaBean.getRefpoliza()!=null) && !polizaBean.getRefpoliza().equals("")){
				criteria.add(Restrictions.ilike("refpoliza", "%" + polizaBean.getRefpoliza() + "%"));
			}
			
			
			List<Poliza> lstPolizas = criteria.list();
			
			return lstPolizas;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public void actualizaDVCopy(String cadena, int numParcelas, Long idCopy) {
		
		try {
			String procedure = "PQ_ACTUALIZA_COPY.actualiza_datos_variables_copy (cadena IN varchar2, numParcelas IN number, idcopy IN number)";
			
			Map<String, Object> parametros = new HashMap<String, Object>();// parámetros PL
			parametros.put("CADENA", cadena);
			parametros.put("NUMPARCELAS", numParcelas);
			parametros.put("IDCOPY", idCopy);
			
			databaseManager.executeStoreProc(procedure, parametros); // ejecutamos PL
		}
		catch(Exception excepcion) {
			logger.error("Se ha producido un error al lanzar el PL de actualización de DV de la copy",excepcion);
		}
		
	}
	//TMR 10/12/2012
	/**
	 * @return List
	 * Recuperamos los codConceptos del organizador 
	 * para el uso 31 – Póliza y ubicación 16- Parcela Datos Variables
	 * 
	 */
	public List<BigDecimal>  getCodsConceptoOrganizador(Long lineaseguroid) throws DAOException{
		
		try {
			
			return polizaDao.getCodsConceptoOrganizador(lineaseguroid);
			
		} catch (DAOException e) {
			logger.error("error al obtener los codigos concepto del organizador de base de datos",e);
			throw e;
		}
		
	}

	public List<DatoVariableDefault> getDatosVariablesCopy(com.rsi.agp.dao.tables.poliza.Poliza polizaBean)
			throws DAOException {
		logger.info("init - getDatosVariablesCopy");
    	List<DatoVariableDefault> lstDatosVar = null;
    	Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(DatoVariableDefault.class);
			criteria.add(Restrictions.ilike("planLineaConcepto", "".concat(polizaBean.getLinea().getCodplan().toString()).concat("_").concat(polizaBean.getLinea().getCodlinea().toString()).concat("%")));
			lstDatosVar = criteria.list();
			
		} catch(Exception ex){
			logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
    		throw new DAOException("[ERROR] al acceder a la BBDD.",ex);
    	}
			logger.info("end - getDatosVariablesCopy");
			return lstDatosVar;
	}

	@Override
	public void saveDatoVarParcela(DatoVariableParcela varParcela)throws  DAOException{
		Session session = obtenerSession();
    	try {
    	session.save(varParcela);
    	} catch (Exception ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
    		throw new DAOException("[ERROR] al acceder a la BBDD.",ex);
		}
		
	}
	
	//ASF - Mejora para crear una póliza a partir de los datos de una copy
	/**
	 * Método que lee los datos de una copy y crea una póliza en estado contratada basandose en ella.
	 * @param cadena
	 * @param numParcelas
	 * @param idCopy
	 */
	public Long crearPolizaFromCopy(String referencia, String tipoReferencia, BigDecimal clase, Long idCopy) {
		
		Long idpoliza = null;
		try {
			String procedure = "PQ_SITUACION_ACTUALIZADA.generarPolizaFromCopy (" +
					"P_REFERENCIA IN VARCHAR2, P_TIPOREFERENCIA IN VARCHAR2, P_CLASE IN NUMBER, " +
					"P_IDCOPY IN NUMBER, P_IDPOLIZA OUT NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>();// parámetros PL
			parametros.put("P_REFERENCIA", referencia);
			parametros.put("P_TIPOREFERENCIA", tipoReferencia);
			parametros.put("P_CLASE", clase);
			parametros.put("P_IDCOPY", idCopy);
			parametros.put("P_IDPOLIZA", idpoliza);
			
			logger.debug("Llamada al procedimiento " + procedure);
			logger.debug("Con los siguientes parametros: ");
			logger.debug("   - P_REFERENCIA: " + referencia);
			logger.debug("   - P_TIPOREFERENCIA: " + tipoReferencia);
			logger.debug("   - P_CLASE: " + clase);
			logger.debug("   - P_IDCOPY: " + idCopy);
			logger.debug("   - P_IDPOLIZA: " + idpoliza);
			
			Map<String,Object> resultado = new HashMap<String, Object>();
			resultado = databaseManager.executeStoreProc(procedure, parametros); // ejecutamos PL
			if (resultado.get("P_IDPOLIZA") != null){
				idpoliza = ((BigDecimal) resultado.get("P_IDPOLIZA")).longValue();
			}
		}
		catch(Exception e) {
			logger.error("Se ha producido un error al crear la póliza desde la copy " + idCopy, e);
		}
		return idpoliza;
	}
	
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
}
