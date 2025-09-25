package com.rsi.agp.dao.models.cpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;

public class AseguradoAutorizadoDao extends BaseDaoHibernate implements IAseguradoAutorizadoDao{
	
	@SuppressWarnings("unchecked")
	public List<AseguradoAutorizado> getAseguradosAutorizados(Long lineaseguroid, String nifasegurado, String modulo, 
			String fechaFinGarantias,String garantizado, String codcultivo) throws DAOException {
		
        Session session = obtenerSession();
		List<AseguradoAutorizado> listaAseguradosAutorizados = null;

		try{
			Criteria c = session.createCriteria(AseguradoAutorizado.class);
			if (lineaseguroid != null){
				Criterion crit1 = Restrictions.eq("id.lineaseguroid", lineaseguroid);
				c.add(crit1);
			}
			if (nifasegurado != null && !"".equalsIgnoreCase(nifasegurado)){
				Criterion crit2 = Restrictions.eq("nifasegurado", nifasegurado);
				c.add(crit2);
			}else{
				Criterion crit2 = Restrictions.isNull("nifasegurado");
				c.add(crit2);
			}
			if(modulo != null && !"".equalsIgnoreCase(modulo)){
				Criterion crit3 = Restrictions.eq("modulo.id.codmodulo", modulo);
				c.add(crit3);
			}
			if(fechaFinGarantias != null && !"".equalsIgnoreCase(fechaFinGarantias)){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Criterion crit4 = Restrictions.eq("fecfgarant", sdf.parse(fechaFinGarantias));
				c.add(crit4);
			}
			if(garantizado != null && !"".equalsIgnoreCase(garantizado)){
				Criterion crit5 = Restrictions.eq("valorcg",garantizado);
				c.add(crit5);
			}
			
			if (!StringUtils.nullToString(codcultivo).equals("")){
				c.add(Restrictions.eq("variedad.id.codcultivo", new BigDecimal(codcultivo)));
			}

			listaAseguradosAutorizados = c.list();
		}
		catch(Exception e){
			logger.fatal("Error al obtener los asegurados autorizados de Base de Datos", e);
			throw new DAOException("Error al obtener los asegurados autorizados de Base de Datos", e);
		}
		
		return listaAseguradosAutorizados;
	}
	
	public boolean checkAseguradoAutorizadoGarantizado(long lineaseguroid){
		boolean checkGarantizado= false;
		try {
			Session session = obtenerSession();
			
			String sql= "select count(*) from o02agpe0.TB_ASEG_AUTORIZADOS_DIA a "
							+ "where a.lineaseguroid = " + lineaseguroid + " and a.codgarantizado is not null";
			
			int count = ((BigDecimal)(session.createSQLQuery(sql).list().get(0))).intValue();
			if (count > 0){
				checkGarantizado=true;
			}
		} catch (HibernateException e) {
			logger.error("Error en checkAseguradoAutorizadoGarantizado", e);
		}
		return checkGarantizado;
	}
	
	
	public boolean checkAseguradoAutorizadoNif(long lineaseguroid,String nifAsegurado){
		boolean checkGarantizadoNif= false;
		try {
			Session session = obtenerSession();
			String sql= "select count(*) from o02agpe0.TB_ASEG_AUTORIZADOS_DIA a "
					+ "where a.lineaseguroid = " + lineaseguroid + " and a.nifasegurado = '" + nifAsegurado + "'";

			int count = ((BigDecimal)(session.createSQLQuery(sql).list().get(0))).intValue();
			if (count > 0){
				checkGarantizadoNif=true;
			}
		} catch (HibernateException e) {
			logger.error("Error en checkAseguradoAutorizadoNif", e);
		}
		return checkGarantizadoNif;
	}
	
	@SuppressWarnings("unchecked")
	public List<AseguradoAutorizado> lstAsegGarantizadosAplicables(long lineaseguroid,String nifAsegurado){
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(AseguradoAutorizado.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			if (nifAsegurado !=null){
				criteria.add(Restrictions.eq("nifasegurado", nifAsegurado));
			}else{
				criteria.add(Restrictions.isNull("nifasegurado"));
			}
			List<AseguradoAutorizado> lstAsegGarantizados = criteria.list();
			return lstAsegGarantizados;
		} catch (HibernateException e) {
			logger.error("Error en lstAsegGarantizadosAplicables", e);
		}
		return new ArrayList<AseguradoAutorizado>();
	}

}
