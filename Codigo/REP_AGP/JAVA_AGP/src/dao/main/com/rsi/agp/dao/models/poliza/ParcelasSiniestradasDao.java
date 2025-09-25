package com.rsi.agp.dao.models.poliza;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestradoDV;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;

public class ParcelasSiniestradasDao extends BaseDaoHibernate implements IParcelasSiniestradasDao{
	
	@Override
	public List<CapAsegSiniestro> list(Long idCapital)throws DAOException {
		
		try {
		
			return findFiltered(CapAsegSiniestro.class,"id" ,idCapital);
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
	}

	/**
	 * 
	 * Filtro del doConsulta
	 * 
	 */
	@Override
	public List<ParcelaSiniestro> listSiniestroParcelas(CapAsegSiniestradoDV capAsegSiniestradoDV) throws DAOException {
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(ParcelaSiniestro.class);
			// ordenamos las parcelas por hoja - numero 14-11-2012 TMR
			criteria.addOrder(Order.asc("hoja"));
			criteria.addOrder(Order.asc("numero"));
			
			
			criteria.createAlias("parcela", "par", CriteriaSpecification.LEFT_JOIN);
			
			
			if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().getId()!= null){
				
				criteria.add(Restrictions.eq("siniestro.id", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().getId()));
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getHoja()){
					criteria.add(Restrictions.eq("hoja",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getHoja()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNumero()){
					criteria.add(Restrictions.eq("numero", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNumero()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligono() && !"".equals(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligono())){
					criteria.add(Restrictions.eq("poligono",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligono()));			
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela_1() && !"".equals(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela_1())){
					criteria.add(Restrictions.eq("parcela_1",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela_1()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodprovsigpac()){
					criteria.add(Restrictions.eq("codprovsigpac", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodprovsigpac()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermsigpac()){
					criteria.add(Restrictions.eq("codtermsigpac",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermsigpac()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getAgrsigpac()){
					criteria.add(Restrictions.eq("agrsigpac",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getAgrsigpac()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getZonasigpac()){
					criteria.add(Restrictions.eq("zonasigpac", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getZonasigpac()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligonosigpac()){
					criteria.add(Restrictions.eq("poligonosigpac", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligonosigpac()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcelasigpac()){
					criteria.add(Restrictions.eq("parcelasigpac",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcelasigpac()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getRecintosigpac()){
					criteria.add(Restrictions.eq("recintosigpac", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getRecintosigpac()));
				}
				if(!StringUtils.nullToString(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNomparcela()).equals("")){
					criteria.add(Restrictions.like("nomparcela", "%" + capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNomparcela() + "%"));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodprovincia()){
					criteria.add(Restrictions.eq("codprovincia",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodprovincia()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodcomarca()){
					criteria.add(Restrictions.eq("codcomarca", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodcomarca()));				
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino()){
					criteria.add(Restrictions.eq("codtermino", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSubtermino()){
					criteria.add(Restrictions.eq("subtermino", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSubtermino()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodcultivo()){
					criteria.add(Restrictions.eq("codcultivo",capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodcultivo()));
				}
				if(null != capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodvariedad()){
					criteria.add(Restrictions.eq("codvariedad", capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodvariedad()));
				}	
				if(null!= capAsegSiniestradoDV.getCapAsegSiniestro().getCodtipocapital()){
					criteria.createAlias("capAsegSiniestros", "capital",CriteriaSpecification.LEFT_JOIN);
					criteria.add(Restrictions.eq("capital.codtipocapital", capAsegSiniestradoDV.getCapAsegSiniestro().getCodtipocapital()));
				}
			}			
					
			return criteria.list();
			
		} catch (Exception e) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}
	}

	public List<CapAsegSiniestro> getCapitalesAseguradosSiniestro(List<Long> idsCapitalesAseguradosSiniestro) throws DAOException {
		Session sesion = obtenerSession();
		
		try {
			Criteria criteria = sesion.createCriteria(CapAsegSiniestro.class);
			int size = idsCapitalesAseguradosSiniestro.size();
			if (size > 999) {
				Criterion criterion = CriteriaUtils.splitHibernateIn("id", idsCapitalesAseguradosSiniestro);
				criteria.add(criterion);
			}else {
				criteria.add(Restrictions.in("id", idsCapitalesAseguradosSiniestro));
			}
			return criteria.list();
		} catch (Exception e) {
			logger.error("Error al obtener los capitales asegurados del siniestro", e);
			throw new DAOException(e);
		}
	}
	
	public List<ParcelaSiniestro> getParcelasSiniestradas(List<Long> idsCapitalesAseguradosSiniestro) throws DAOException {
		Session sesion = obtenerSession();
		
		try {
			Criteria criteria = sesion.createCriteria(ParcelaSiniestro.class);
			criteria.createAlias("capAsegSiniestros", "cas");
			int size = idsCapitalesAseguradosSiniestro.size();
			if (size > 999) {
				Criterion criterion = CriteriaUtils.splitHibernateIn("cas.id", idsCapitalesAseguradosSiniestro);
				criteria.add(criterion);
			}else {
				criteria.add(Restrictions.in("cas.id", idsCapitalesAseguradosSiniestro));
			}
			return criteria.list();
		} catch (Exception e) {
			logger.error("Error al obtener los capitales asegurados del siniestro", e);
			throw new DAOException(e);
		}
	}

}
