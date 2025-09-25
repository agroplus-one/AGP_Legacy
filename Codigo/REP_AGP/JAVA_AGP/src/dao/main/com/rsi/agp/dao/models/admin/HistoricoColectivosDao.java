package com.rsi.agp.dao.models.admin;

import java.util.ArrayList;
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
import com.rsi.agp.dao.tables.commons.Usuario;


public class HistoricoColectivosDao extends BaseDaoHibernate implements IHistoricoColectivosDao{
	private static final Log LOGGER = LogFactory.getLog(HistoricoColectivosDao.class);

	private HistoricoColectivos inicializarDatosColectivo(Colectivo colectivoBean, String codUsuario, 
			String tipoOperacion,Date fechaEfectoHist,boolean activarCol){
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
			if (activarCol){
				historicoColectivo.setFechaefecto(fechaEfectoHist);
				historicoColectivo.setFechacambio(new Date());
			}else{	
				historicoColectivo.setFechaefecto(colectivoBean.getFechaefecto());
				historicoColectivo.setFechacambio(colectivoBean.getFechacambio());
			}
			
		}
		
		// PTC-5729 ** MODIF TAM (03.05.2019) //
		historicoColectivo.setEnvioIbanAgro(colectivoBean.getEnvioIbanAgro());
		historicoColectivo.setEnvioIbanAgro(colectivoBean.getEnvioIbanAgro().charValue());
		
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
	public void saveHistoricoColectivo(Colectivo colectivoBean, Usuario usuario, 
			String tipoOperacion,Date fechaEfectoHist,boolean activarCol)
			throws DAOException {		
		HistoricoColectivos historicoColectivo = inicializarDatosColectivo(colectivoBean, usuario.getCodusuario(), 
				tipoOperacion,fechaEfectoHist, activarCol);
		try {
			this.saveOrUpdate(historicoColectivo);
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
	}

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

	@Override
	public HistoricoColectivos getUltColectivoHistorico(Long id)
			throws DAOException {
		Session session = obtenerSession();
		List<HistoricoColectivos> listHistoricoColectivos = null;
		try {
			Criteria criteria = session.createCriteria(HistoricoColectivos.class);
			criteria.createAlias("colectivo", "colectivo");
			criteria.add(Restrictions.eq("colectivo.id", id));
			criteria.addOrder(Order.desc("fechaoperacion"));
			listHistoricoColectivos = criteria.list();
			if (listHistoricoColectivos.size()>0){
				return listHistoricoColectivos.get(0);
			}
		} catch (Exception e){
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return null;
	}

	@Override
	public void borrarHistoricoColectivo(Long idHistorico) throws DAOException {
		HistoricoColectivos hc = (HistoricoColectivos) get(HistoricoColectivos.class, idHistorico);
		
		/*Borramos la fecha de baja del colectivo en caso de tener operacion 'Baja'*/
		if (hc.getTipooperacion().equals('B')) {
			this.borrarFechaBajaColectivo(hc);
		}
		
		/*Borramos el historico*/
		this.delete(hc);
	}
	
	@Override
	public void borrarFechaBajaColectivo(HistoricoColectivos hc) throws DAOException {
		Session session = obtenerSession();
		Colectivo colectivo = null;
		try {
			Criteria criteria = session.createCriteria(Colectivo.class);
			//criteria.createAlias("colectivo", "colectivo");
			criteria.add(Restrictions.eq("idcolectivo", hc.getReferencia()));
			criteria.add(Restrictions.eq("linea.lineaseguroid", hc.getLinea().getLineaseguroid()));
			colectivo = (Colectivo) criteria.uniqueResult();
			if (colectivo != null){
				colectivo.setFechabaja(null);
				this.saveOrUpdate(colectivo);
				
			}
		} catch (Exception e){
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}

		
	}
	
}
