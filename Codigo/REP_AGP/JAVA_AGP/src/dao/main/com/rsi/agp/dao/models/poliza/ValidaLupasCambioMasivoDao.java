package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.Destino;
import com.rsi.agp.dao.tables.cgen.MarcoPlantacion;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Variedad;

public class ValidaLupasCambioMasivoDao extends BaseDaoHibernate implements IValidaLupasCambioMasivoDao {

	@Override
	public boolean validaCultivoVariedadCM(String cultivo, String variedad,
			String lineaSeguroId) throws DAOException {
		
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(Variedad.class);
        	
        	criteria.add(Restrictions.eq("id.lineaseguroid", Long.valueOf(lineaSeguroId)));
            criteria.add(Restrictions.eq("id.codcultivo", new BigDecimal(cultivo)));
            criteria.add(Restrictions.eq("id.codvariedad", new BigDecimal(variedad)));
            
            if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaCultivoVariedadCM]error lectura BD",e);
		}
	}

	@Override
	public boolean validaDestinoCM(String destino) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(Destino.class);
        	criteria.add(Restrictions.eq("coddestino", new BigDecimal(destino)));
            
            if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaDestinoCM]error lectura BD",e);
		}
	}

	@Override
	public boolean validaSisCultivoCM(String sisCultivo) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(SistemaCultivo.class);
        	criteria.add(Restrictions.eq("codsistemacultivo", new BigDecimal(sisCultivo)));
            
            if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaSisCultivoCM]error lectura BD",e);
		}
	}

	@Override
	public boolean validaMarcoPlanCM(String tipoMarcoPlan) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(MarcoPlantacion.class);
        	criteria.add(Restrictions.eq("codtipomarcoplantac", new BigDecimal(tipoMarcoPlan)));
            
            if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaMarcoPlanCM]error lectura BD",e);
		}
	}

	@Override
	public boolean validaPracticaCulturalCM(String practicaCultural)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(PracticaCultural.class);
        	criteria.add(Restrictions.eq("codpracticacultural", new BigDecimal(practicaCultural)));
            
            if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaPracticaCulturalCM]error lectura BD",e);
		}
	}

	@Override
	public boolean validaTipoPlantacionCM(String tipoPlantacion)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(TipoPlantacion.class);
        	criteria.add(Restrictions.eq("codtipoplantacion", new BigDecimal(tipoPlantacion)));
            
            if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaTipoPlantacionCM]error lectura BD",e);
		}
	}

	@Override
	public boolean validaUbicacionCM(String provincia, String comarca,
			String termino, String subtermino) throws DAOException {
		
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(Termino.class);
        	if (!provincia.equals("")) {
        		criteria.add(Restrictions.eq("id.codprovincia", new BigDecimal(provincia)));
        	}
    		if (!comarca.equals("")) {
        		criteria.add(Restrictions.eq("id.codcomarca", new BigDecimal(comarca)));
    		}
    		if (!termino.equals("")) {
        		criteria.add(Restrictions.eq("id.codtermino", new BigDecimal(termino)));
    		}
            if (!subtermino.equals("")) {
        		criteria.add(Restrictions.eq("id.subtermino", subtermino.charAt(0)));            
            }
        		
        	if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaTipoPlantacionCM]error lectura BD",e);
		}
	}

		@Override
	public boolean validaSistemaProduccion(String sistProd)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try{
        	criteria = session.createCriteria(SistemaProduccion.class);
        	
        	if (!sistProd.equals(""))
        		criteria.add(Restrictions.eq("codsistemaproduccion", new BigDecimal(sistProd)));     	
        	
        	if (criteria.list().size()>0){
            	return true;
            }else{
            	return false;
            }
        } catch (Exception e) {
			throw new DAOException ("[ValidaLupasCambioMasivoDao][validaSistemaProduccionCM]error lectura BD",e);
		}	
	}

}
