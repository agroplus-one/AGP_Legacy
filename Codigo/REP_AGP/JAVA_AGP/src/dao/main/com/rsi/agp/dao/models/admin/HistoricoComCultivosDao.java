package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;


public class HistoricoComCultivosDao extends BaseDaoHibernate implements IHistoricoComCultivosDao{
	private static final Log LOGGER = LogFactory.getLog(HistoricoComCultivosDao.class);

	private HistoricoColectivos inicializarDatosColectivo(Colectivo colectivoBean, String codUsuario, String tipoOperacion){
		HistoricoColectivos historicoColectivo = new HistoricoColectivos();
		
		//Copiamos los atributos del colectivo
		historicoColectivo.setActivo(colectivoBean.getActivo());
		if (colectivoBean.getTomador() != null){
			historicoColectivo.setTomador(colectivoBean.getTomador());
		}
		historicoColectivo.setTomador(colectivoBean.getTomador());
		historicoColectivo.setDc(colectivoBean.getDc());
		//DAA 11/07/2013 si estoy dando de baja registramos en el historico la fecha de baja y la fecha actual para efecto y cambio.
		if (colectivoBean.getFechabaja() != null){
			historicoColectivo.setFechabaja(colectivoBean.getFechabaja());
			historicoColectivo.setFechacambio(new Date());
			historicoColectivo.setFechaefecto(new Date());
		}else{
			historicoColectivo.setFechacambio(colectivoBean.getFechacambio());
			historicoColectivo.setFechaefecto(colectivoBean.getFechaefecto());
		}
		
		historicoColectivo.setFechaprimerpago(colectivoBean.getFechaprimerpago());
		historicoColectivo.setFechasegundopago(colectivoBean.getFechasegundopago());
		//TMR 19/07/12 
		//historicoColectivo.setIdcolectivo(colectivoBean.getIdcolectivo());
		historicoColectivo.setReferencia(colectivoBean.getIdcolectivo());
		historicoColectivo.setColectivo(colectivoBean);
		
		if (colectivoBean.getLinea() != null){
			historicoColectivo.setLinea(colectivoBean.getLinea());
		}		
		historicoColectivo.setNomcolectivo(colectivoBean.getNomcolectivo());
		historicoColectivo.setPctdescuentocol(colectivoBean.getPctdescuentocol());
		historicoColectivo.setPctprimerpago(colectivoBean.getPctprimerpago());
		historicoColectivo.setPctsegundopago(colectivoBean.getPctsegundopago());
		if (colectivoBean.getSubentidadMediadora() != null){
			historicoColectivo.setSubentidadMediadora(colectivoBean.getSubentidadMediadora());
		}
				
		//Se inicializan los datos del usuario, tipo de operación y fecha de inserción
		historicoColectivo.setCodusuario(StringUtils.nullToString(codUsuario));
		historicoColectivo.setFechaoperacion(new Date());
		historicoColectivo.setTipooperacion(tipoOperacion.charAt(0));
		
		return historicoColectivo;
	}
	
	@Override
	public void saveHistoricoColectivo(Colectivo colectivoBean, Usuario usuario, String tipoOperacion)
			throws DAOException {		
		HistoricoColectivos historicoColectivo = inicializarDatosColectivo(colectivoBean, usuario.getCodusuario(), 
				tipoOperacion);
		try {
			this.saveOrUpdate(historicoColectivo);
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
	}
	
	/*
	@Override
	public List<HistoricoColectivos> listHistoricoColectivos(HistoricoColectivos historicoColectivo) throws DAOException{
		logger.debug("init - listHistoricoColectivos");
		List<HistoricoColectivos> listHistoricoColectivos = null;
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(HistoricoColectivos.class);
			criteria.createAlias("linea", "linea");			
			criteria.createAlias("tomador", "tomador");
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("colectivo", "colectivo");
			
			if ((historicoColectivo.getTomador() != null) && (historicoColectivo.getTomador().getId() != null) &&
				(FiltroUtils.noEstaVacio(historicoColectivo.getTomador().getId().getCodentidad()))){
				criteria.add(Restrictions.eq("tomador.id.codentidad", historicoColectivo.getTomador().getId().getCodentidad()));
			}
					
			if ((historicoColectivo.getTomador() != null) && (historicoColectivo.getTomador().getId() != null) &&
				(FiltroUtils.noEstaVacio(historicoColectivo.getTomador().getId().getCiftomador()))){
				criteria.add(Restrictions.eq("tomador.id.ciftomador", historicoColectivo.getTomador().getId().getCiftomador()));
			}

			if ((historicoColectivo.getLinea() != null) && (FiltroUtils.noEstaVacio(historicoColectivo.getLinea().getCodlinea()))) {
				criteria.add(Restrictions.eq("linea.codlinea", historicoColectivo.getLinea().getCodlinea()));
			}
			
			if ((historicoColectivo.getLinea() != null) && (FiltroUtils.noEstaVacio(historicoColectivo.getLinea().getCodplan()))) {
				criteria.add(Restrictions.eq("linea.codplan", historicoColectivo.getLinea().getCodplan()));
			}
			//TMR 19/07/12 
			if (FiltroUtils.noEstaVacio(historicoColectivo.getReferencia())) {
				criteria.add(Restrictions.eq("referencia", historicoColectivo.getReferencia()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getNomcolectivo())) {
				criteria.add(Restrictions.like("nomcolectivo", "%"+historicoColectivo.getNomcolectivo()+"%"));
			}

			if ((historicoColectivo.getSubentidadMediadora() != null) && (historicoColectivo.getSubentidadMediadora().getId() != null) &&
				(FiltroUtils.noEstaVacio(historicoColectivo.getSubentidadMediadora().getId().getCodentidad()))){
				criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", historicoColectivo.getSubentidadMediadora().getId().getCodentidad()));
			}
			
			if ((historicoColectivo.getSubentidadMediadora() != null) && (historicoColectivo.getSubentidadMediadora().getId() != null) &&
				(FiltroUtils.noEstaVacio(historicoColectivo.getSubentidadMediadora().getId().getCodsubentidad()))) {
				criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", historicoColectivo.getSubentidadMediadora().getId().getCodsubentidad()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getActivo())) {
				criteria.add(Restrictions.eq("activo", historicoColectivo.getActivo()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getPctdescuentocol())) {
				criteria.add(Restrictions.eq("pctdescuentocol", historicoColectivo.getPctdescuentocol()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getPctprimerpago())) {
				criteria.add(Restrictions.eq("pctprimerpago", historicoColectivo.getPctprimerpago()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getFechaprimerpago())) {
				criteria.add(Restrictions.eq("fechaprimerpago", historicoColectivo.getFechaprimerpago()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getPctsegundopago())) {
				criteria.add(Restrictions.eq("pctsegundopago", historicoColectivo.getPctsegundopago()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getFechasegundopago())) {
				criteria.add(Restrictions.eq("fechasegundopago", historicoColectivo.getFechasegundopago()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getDc())) {
				criteria.add(Restrictions.eq("dc", historicoColectivo.getDc()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getTipooperacion())) {
				criteria.add(Restrictions.eq("tipooperacion", historicoColectivo.getTipooperacion()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getFechacambio())) {
				criteria.add(Restrictions.eq("fechacambio", historicoColectivo.getFechacambio()));
			}
			
			if (FiltroUtils.noEstaVacio(historicoColectivo.getFechaefecto())) {
				criteria.add(Restrictions.eq("fechaefecto", historicoColectivo.getFechaefecto()));
			}		
			if (FiltroUtils.noEstaVacio(historicoColectivo.getColectivo())){
				if (FiltroUtils.noEstaVacio(historicoColectivo.getColectivo().getIdcolectivo()))
					criteria.add(Restrictions.eq("colectivo.id", Long.parseLong(historicoColectivo.getColectivo().getIdcolectivo())));
			}
			criteria.addOrder(Order.desc("fechaoperacion"));
			criteria.addOrder(Order.desc("id"));
			
			listHistoricoColectivos = criteria.list();			
			
		} catch (Exception e){
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		logger.debug("end - listHistoricoColectivos");
		return listHistoricoColectivos;
		
	}
	*/
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CultivosEntidadesHistorico> listHistoricoComCultivos(
			Long id)
			throws DAOException {
		
		logger.debug("init - listHistoricoColectivos");
		List<CultivosEntidadesHistorico> listCultivosEntidadesHistorico = null;
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(CultivosEntidadesHistorico.class);
			if (FiltroUtils.noEstaVacio(id)) {
				criteria.add(Restrictions.eq("cultivosEntidades", new BigDecimal(id.toString())));
			}
			criteria.addOrder(Order.desc("fechamodificacion"));
			listCultivosEntidadesHistorico = criteria.list();			
		} catch (Exception e){
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return listCultivosEntidadesHistorico;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean existeComision(int plan,
			int linea, Date fechaEfecto, BigDecimal gastosAdmon, BigDecimal gastosAdq) throws DAOException {
		logger.debug("init - listHistoricoColectivos");
		List<CultivosEntidadesHistorico> comisiones = null;
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(CultivosEntidadesHistorico.class);
			criteria.createAlias("linea","linea");
			criteria.add(Restrictions.eq("linea.codplan",new BigDecimal(plan)));
			criteria.add(Restrictions.eq("linea.codlinea",new BigDecimal(linea)));
			criteria.add(Restrictions.eq("fechaEfecto", fechaEfecto));
			criteria.add(Restrictions.eq("pctadquisicion", gastosAdq));
			criteria.add(Restrictions.eq("pctadministracion", gastosAdmon));
			comisiones = criteria.list();			
		} catch (Exception e){
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}				
		return (comisiones!=null && comisiones.size()>0);
	}
	
}
	

