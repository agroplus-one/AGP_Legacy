package com.rsi.agp.dao.models.poliza;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado;
import com.rsi.agp.dao.tables.reduccionCap.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.siniestro.Siniestro;


public class ParcelaReduccionCapitalDao extends BaseDaoHibernate implements IParcelaReduccionCapitalDao {

	public List<CapitalAsegurado> listReduccionCapitalParcelas(CapitalAsegurado capitalAsegurado) throws DAOException {
		
		Session session = obtenerSession();
		Long idReduccionCapital = capitalAsegurado.getParcela().getReduccionCapital().getId();
		
		try {
			
			Criteria criteria = session.createCriteria(CapitalAsegurado.class);
				
			criteria.createAlias("parcela", "par");
			criteria.createAlias("par.parcela", "par2", CriteriaSpecification.LEFT_JOIN);
			//criteria.addOrder(Order.asc("par.hoja"));
			//criteria.addOrder(Order.asc("par.numero"));
			
			criteria.addOrder(Order.asc("par.codprovsigpac"));
			criteria.addOrder(Order.asc("par.codtermsigpac"));
			criteria.addOrder(Order.asc("par.agrsigpac"));
			criteria.addOrder(Order.asc("par.zonasigpac"));
			criteria.addOrder(Order.asc("par.poligonosigpac"));
			criteria.addOrder(Order.asc("par.parcelasigpac"));
			criteria.addOrder(Order.asc("par.recintosigpac"));
			
			criteria.addOrder(Order.asc("par.codcultivo"));
			criteria.addOrder(Order.asc("par.codvariedad"));
			
			criteria.addOrder(Order.desc("par2.tipoparcela"));
			
			
			if(idReduccionCapital!= null){				
				criteria.createAlias("parcela.reduccionCapital", "parRed");			
				criteria.add(Restrictions.eq("parRed.id", idReduccionCapital));
			}			
			if(null != capitalAsegurado.getParcela().getCodprovincia()){				
				criteria.add(Restrictions.eq("par.codprovincia",capitalAsegurado.getParcela().getCodprovincia()));
			}
			if(null != capitalAsegurado.getParcela().getHoja()){
				criteria.add(Restrictions.eq("par.hoja",capitalAsegurado.getParcela().getHoja()));
			}
			if(null != capitalAsegurado.getParcela().getNumero()){
				criteria.add(Restrictions.eq("par.numero", capitalAsegurado.getParcela().getNumero()));
			}
			if(null != capitalAsegurado.getParcela().getPoligono() && !"".equals(capitalAsegurado.getParcela().getPoligono())){
				criteria.add(Restrictions.eq("par.poligono",capitalAsegurado.getParcela().getPoligono()));			
			}
			if(null != capitalAsegurado.getParcela().getParcela_1() && !"".equals(capitalAsegurado.getParcela().getParcela_1())){
				criteria.add(Restrictions.eq("par.parcela_1",capitalAsegurado.getParcela().getParcela_1()));
			}
			if(null != capitalAsegurado.getParcela().getCodprovsigpac()){
				criteria.add(Restrictions.eq("par.codprovsigpac", capitalAsegurado.getParcela().getCodprovsigpac()));
			}
			if(null != capitalAsegurado.getParcela().getCodtermsigpac()){
				criteria.add(Restrictions.eq("par.codtermsigpac",capitalAsegurado.getParcela().getCodtermsigpac()));
			}
			if(null != capitalAsegurado.getParcela().getAgrsigpac()){
				criteria.add(Restrictions.eq("par.agrsigpac",capitalAsegurado.getParcela().getAgrsigpac()));
			}
			if(null != capitalAsegurado.getParcela().getZonasigpac()){
				criteria.add(Restrictions.eq("par.zonasigpac", capitalAsegurado.getParcela().getZonasigpac()));
			}
			if(null != capitalAsegurado.getParcela().getPoligonosigpac()){
				criteria.add(Restrictions.eq("par.poligonosigpac", capitalAsegurado.getParcela().getPoligonosigpac()));
			}
			if(null != capitalAsegurado.getParcela().getParcelasigpac()){
				criteria.add(Restrictions.eq("par.parcelasigpac",capitalAsegurado.getParcela().getParcelasigpac()));
			}
			if(null != capitalAsegurado.getParcela().getRecintosigpac()){
				criteria.add(Restrictions.eq("par.recintosigpac", capitalAsegurado.getParcela().getRecintosigpac()));
			}
			if(null != capitalAsegurado.getParcela().getNomparcela() && !"".equals(capitalAsegurado.getParcela().getNomparcela())){
				criteria.add(Restrictions.eq("par.nomparcela", capitalAsegurado.getParcela().getNomparcela()));
			}			
			if(null != capitalAsegurado.getParcela().getCodprovincia()){
				criteria.add(Restrictions.eq("par.codprovincia",capitalAsegurado.getParcela().getCodprovincia()));
			}
			if(null != capitalAsegurado.getParcela().getCodcomarca()){
				criteria.add(Restrictions.eq("par.codcomarca", capitalAsegurado.getParcela().getCodcomarca()));				
			}
			if(null != capitalAsegurado.getParcela().getCodtermino()){
				criteria.add(Restrictions.eq("par.codtermino", capitalAsegurado.getParcela().getCodtermino()));
			}
			if(null != capitalAsegurado.getParcela().getSubtermino()){
				criteria.add(Restrictions.eq("par.subtermino", capitalAsegurado.getParcela().getSubtermino()));
			}
			if(null != capitalAsegurado.getParcela().getCodcultivo()){
				criteria.add(Restrictions.eq("par.codcultivo",capitalAsegurado.getParcela().getCodcultivo()));
			}
			if(null != capitalAsegurado.getParcela().getCodvariedad()){
				criteria.add(Restrictions.eq("par.codvariedad", capitalAsegurado.getParcela().getCodvariedad()));
			}
			if(null != capitalAsegurado.getCodtipocapital()){
				criteria.add(Restrictions.eq("codtipocapital", capitalAsegurado.getCodtipocapital()));
			}
			if(null != capitalAsegurado.getSuperficie()){
				criteria.add(Restrictions.eq("superficie", capitalAsegurado.getSuperficie()));
			}
			if(null != capitalAsegurado.getProd()){
				criteria.add(Restrictions.eq("prod", capitalAsegurado.getProd()));
			}			

			return criteria.list();
			
		} catch (Exception e) {			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}finally{
		}
				
	}

	
	public List<CapitalAsegurado> list(Long idCapitalAsegurado) throws DAOException {
		
		try {
		
			return findFiltered(CapitalAsegurado.class,"id" ,idCapitalAsegurado);
			
		} catch (Exception e) {			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}finally{
		}

	}
		
	/**
	 * Guarda TODOS los capitales asegurados de la reducción de capital correspondiente
	 */
	@Override
	public void saveCapitalesAsegurados(List<CapitalAsegurado> listaCapitalesAsegurados)throws DAOException {
		Session sesion = obtenerSession();
		try {
			  for(CapitalAsegurado capital : listaCapitalesAsegurados) {
					  
				  Query queryUpdateAltaCapitalAsegurado = sesion.createSQLQuery(
						  "update TB_ANEXO_RED_CAP_ASEG " +
						  "set altaenanexo = :altaenanexo, prodred = :prodred " +
						  "where id = :id")
						  .setCharacter("altaenanexo", capital.getAltaenanexo())
						  .setBigDecimal("prodred", capital.getProdred())
						  .setLong("id", capital.getId());						
				  queryUpdateAltaCapitalAsegurado.executeUpdate();
				  
				  Query queryUpdateAltaParcela = sesion.createSQLQuery(
						  "update TB_ANEXO_RED_PARCELAS " +
						  "set altaenanexo = :altaenanexo " +
						  "where id = :id")							  
						  .setCharacter("altaenanexo", capital.getAltaenanexo())						  
						  .setLong("id", capital.getParcela().getId());						
				  queryUpdateAltaParcela.executeUpdate();
				  sesion.refresh(capital);
			  }  	
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error el alta de Capitales Asegurados", e);			
		}finally{
		}		
	}
	
	@Override
	public void saveReduccionCapital(ReduccionCapital reduccionCapital) throws DAOException {
		Session session = obtenerSession();
		try {
			
			session.saveOrUpdate(reduccionCapital);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}

	}
	/**
	 * Busca ReduccionCapital por id
	 */
	@Override
	public ReduccionCapital getReduccionCapitalById(Long id) throws DAOException {
		// Crear una nueva sesión para la lectura
		Session session = getSessionFactory().getCurrentSession();
        session.flush();
        session.clear(); // Limpia el contexto de persistencia
		ReduccionCapital reduccionCapital = new ReduccionCapital();
		reduccionCapital = (ReduccionCapital)session.get(ReduccionCapital.class, id);
		Criteria criteria = session.createCriteria(ReduccionCapital.class);
		criteria.add(Restrictions.idEq(id));
		reduccionCapital = (ReduccionCapital) criteria.uniqueResult();
		
		return reduccionCapital;
	}
	
	
}
