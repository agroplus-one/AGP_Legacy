package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.copy.CoberturaPoliza;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

public class CoberturasModificacionPolizaDao  extends BaseDaoHibernate implements ICoberturasModificacionPolizaDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Modulo> getModulosPoliza(Long lineaseguroid) throws DAOException {
		
		Session session = obtenerSession();
		
		try {
			Criteria criteria =	session.createCriteria(Modulo.class);
		
			criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			String principal = "P";
			criteria.add(Restrictions.eq("ppalcomplementario", principal.charAt(0)));
			criteria.add(Restrictions.ne("id.codmodulo","99999"));
			
			criteria.addOrder(Order.asc("desmodulo"));
			
			return criteria.list();
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error listando las declaraciones de modificación de póliza", ex);
		} finally{
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ComparativaPoliza> getCoberturasPoliza(Long idPoliza) throws DAOException {
		
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(ComparativaPoliza.class);
		
			criteria.add(Restrictions.eq("id.idpoliza", idPoliza));
			
			return criteria.list();
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error listando las declaraciones de modificación de póliza", ex);
		} finally{
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CoberturaPoliza> getCoberturasCopy(Long idCopy) throws DAOException {
		
		Session session = obtenerSession();
		
		try {
			Criteria criteria =	session.createCriteria(CoberturaPoliza.class);
		
			criteria.add(Restrictions.eq("poliza.id", idCopy));
			
			return criteria.list();
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error listando las declaraciones de modificación de póliza", ex);
		} finally{
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isUsuarioAutorizado(Long lineaseguroid, String nifcif) throws DAOException {
		List<Medida> medidaAseg = new ArrayList<Medida>();
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(Medida.class);
		
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.nifasegurado", nifcif));
			
			medidaAseg = criteria.list();
			
			if (medidaAseg != null
					&& medidaAseg.size() > 0) {
				for(Medida medida : medidaAseg){
					Double pctbonifrecargo = medida.getPctbonifrecargo().doubleValue();
					if(medida.getTipomedidaclub().toString().equals("1")
						&& pctbonifrecargo >= 15){
						return true;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
			
			return false;
			
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error listando las declaraciones de modificación de póliza", ex);
		} finally{
		}
	}
	
	@Override
	public Modulo getModulo(Long lineaseguroid, String codmodulo) throws DAOException{
		
		ModuloId id = new ModuloId(lineaseguroid, codmodulo);
		
		return (Modulo) get(Modulo.class, id);
		
	}
	
	@Override
	public boolean saveCoberturasAnexo(Long idAnexo, List<Cobertura> listCoberturas) throws DAOException {
		boolean isGrabado = false;
		Session session = obtenerSession();
		try {
			// Eliminamos todas las coberturas
			Query queryDelete = session.createSQLQuery("delete from tb_anexo_mod_coberturas where idanexo = :idanexo").setLong("idanexo", idAnexo);
			queryDelete.executeUpdate();
	
			if(listCoberturas != null){
				// Insertamos las nuevas coberturas
				for (Cobertura cobertura : listCoberturas) {	
					logger.debug("cobertura: "+ cobertura.getCodconcepto());
					logger.debug("cobertura: "+ cobertura.getCodconceptoppalmod());
					logger.debug("cobertura: "+ cobertura.getCodriesgocubierto());
					logger.debug("cobertura: "+ cobertura.getCodvalor());
					session.saveOrUpdate(cobertura);
				}
				isGrabado = true;
			}
			
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error al guardar las Coberturas del Anexo de Modificación", ex);
		} finally{
		}
		logger.debug("Fin saveCoberturasAnexo()");
		return isGrabado;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Metodo que genera una lista con las vinculacionesValorModulo filtrando por módulo y lineaseguroid
	 * @param lineaseguroid
	 * @param codModulo
	 * @return lstVincValMod
	 * @throws DAOException
	 */
	public List<VinculacionValoresModulo> getLstVincValMod(Long lineaseguroid, String codModulo) throws DAOException {
		logger.debug("init - [CoberturasModificacionPolizaDao] getLstVincValMod");
		List<VinculacionValoresModulo> lstVincValMod = new ArrayList<VinculacionValoresModulo>();
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(VinculacionValoresModulo.class);
			
			criteria = criteria.createAlias("caracteristicaModuloByFkVincValModCaracMod1", "mod1");
			criteria = criteria.createAlias("caracteristicaModuloByFkVincValModCaracMod2", "mod2");
			
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("mod1.id.codmodulo", codModulo));
			criteria.add(Restrictions.isNotNull("mod2.id.filamodulo"));
			criteria.add(Restrictions.isNotNull("mod2.id.columnamodulo"));
			lstVincValMod = criteria.list();
			
			logger.debug("end - [CoberturasModificacionPolizaDao] getLstVincValMod");
			return lstVincValMod;
			
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error en la llamada a la BBDD", ex);
		} finally{
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<VinculacionValoresModulo> getLstVincValMod2(Long lineaseguroid, String codModulo) throws DAOException {
		logger.debug("init - [CoberturasModificacionPolizaDao] getLstVincValMod");
		List<VinculacionValoresModulo> lstVincValMod = new ArrayList<VinculacionValoresModulo>();
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(VinculacionValoresModulo.class);
			
			criteria = criteria.createAlias("caracteristicaModuloByFkVincValModCaracMod1", "mod1");
			
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("mod1.id.codmodulo", codModulo));
			lstVincValMod = criteria.list();
			
			logger.debug("end - [CoberturasModificacionPolizaDao] getLstVincValMod");
			return lstVincValMod;
			
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error en la llamada a la BBDD", ex);
		} finally{
		}
	}
	
	@SuppressWarnings("rawtypes")
	public String getDesValorByCodConcepto(String sql) throws DAOException  {
		Session session = obtenerSession();
		String desValor="";
		try {
			List list = session.createSQLQuery(sql).list();
			desValor = (String) list.get(0);
		
		return desValor;
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error en la llamada a la BBDD", ex);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public BigDecimal getFilaModuloByCodConcepto(String sqlFila) throws DAOException  {
		Session session = obtenerSession();
		BigDecimal fila=new BigDecimal(0);
		try {
			List list = session.createSQLQuery(sqlFila).list();
			fila = (BigDecimal) list.get(0);
		
		return fila;
		} catch(Exception ex){
			throw new DAOException("Se ha producido un error en la llamada a la BBDD", ex);
		}
	}
}