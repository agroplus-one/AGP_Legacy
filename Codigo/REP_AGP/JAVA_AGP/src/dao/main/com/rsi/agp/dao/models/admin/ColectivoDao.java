package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.admin.impl.Colectivo2Filtro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;


public class ColectivoDao extends BaseDaoHibernate implements IColectivoDao{
	private static final Log LOGGER = LogFactory.getLog(ColectivoDao.class);
	@Override
	public List<Colectivo> getColectivosGrupoEntidad(Colectivo colectivoBean, 
			List<BigDecimal> entidades, boolean addFiltroBaja, List<BigDecimal> planesFiltroInicial) throws DAOException{
		Session session = obtenerSession();		
		
		try {
			Criteria criteria = session.createCriteria(Colectivo.class);
			
			criteria.createAlias("linea", "lin");
			criteria.createAlias("subentidadMediadora", "SM");
			criteria.createAlias("tomador", "tom");
			criteria.addOrder(Order.asc("tom.id.codentidad"));
			criteria.addOrder(Order.asc("SM.entidadMediadora.codentidad"));
			criteria.addOrder(Order.desc("lin.codplan"));
			criteria.addOrder(Order.asc("lin.codlinea"));
			
			criteria.add(Restrictions.isNull("SM.fechabaja"));
			if (FiltroUtils.noEstaVacio(colectivoBean)) {
				criteria.add(Restrictions.allEq(getMapaColectivo(colectivoBean)));
				
				final String nomColectivo = colectivoBean.getNomcolectivo();
				if (FiltroUtils.noEstaVacio(nomColectivo)) {
					criteria.add(Restrictions.ilike("nomcolectivo", "%".concat(nomColectivo).concat("%")));
				}
			}
//			Si el perfil pertenece a algun grupo de Entidades
			if(entidades.size() > 0){
				if(colectivoBean.getTomador().getId().getCodentidad() != null){
					criteria.add(Restrictions.eq("tom.id.codentidad", colectivoBean.getTomador().getId().getCodentidad()));
				}else{
					criteria.add(Restrictions.in("tom.id.codentidad", entidades));
				}
				
			}else{
				final BigDecimal codEntidad = colectivoBean.getTomador().getId().getCodentidad();
				if (FiltroUtils.noEstaVacio(codEntidad)) {
					criteria.add(Restrictions.eq("tom.id.codentidad", codEntidad));
				}
			}
			
			//criteria.add(Restrictions.isNull("fechabaja"));
			
			if (addFiltroBaja){
				criteria.add(Restrictions.isNull("fechabaja"));
			}
			if (planesFiltroInicial.size()>0){
				criteria.add(Restrictions.in("lin.codplan", planesFiltroInicial));
			}
			return criteria.list();
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	
	
	
	/* -------------------------------------------------------- */
	/*                       DISPLAYTAG PAGINATION              */
	/* -------------------------------------------------------- */
	public PaginatedListImpl<Colectivo> getPaginatedListColectivosGrupoEntidad(Colectivo colectivoBean, PageProperties pageProperties, Colectivo2Filtro colectivo2Filtro) throws DAOException {
		PaginatedListImpl<Colectivo> paginatedListImpl = new PaginatedListImpl<Colectivo>();
		
		List<Colectivo> listColectivos = null;
		
		try {

			listColectivos = this.getPageColectivos(colectivoBean, colectivo2Filtro, pageProperties);

			paginatedListImpl.setFullListSize(pageProperties.getFullListSize());
			paginatedListImpl.setObjectsPerPage(pageProperties.getPageSize());
			paginatedListImpl.setPageNumber(pageProperties.getPageNumber());
			paginatedListImpl.setList(listColectivos);
			paginatedListImpl.setSortCriterion(pageProperties.getSort());
			if(pageProperties.getDir().equals("asc")){
				paginatedListImpl.setSortDirection(SortOrderEnum.ASCENDING);
			}else if(pageProperties.getDir().equals("desc")){
				paginatedListImpl.setSortDirection(SortOrderEnum.DESCENDING);
			}
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
		}

		return paginatedListImpl;
	}
	
	public List<Colectivo> getPageColectivos(Colectivo polizaBean, Colectivo2Filtro colectivo2Filtro, PageProperties pageProperties) {
		Session session = obtenerSession(); 

		Criteria criteria = colectivo2Filtro.getCriteria(session);
		//JANV 31/03/2016
		//se añade ordenación por subentidad cuando se elige la columna entidad/subentidad
		if(pageProperties.getDir().equals("asc")){
			criteria.addOrder(Order.asc(pageProperties.getSort()));
			if("SM.id.codentidad".equals(pageProperties.getSort())){
			criteria.addOrder(Order.asc("SM.id.codsubentidad"));
			}
		}else if(pageProperties.getDir().equals("desc")){
			criteria.addOrder(Order.desc(pageProperties.getSort()));
			if("SM.id.codentidad".equals(pageProperties.getSort())){
				criteria.addOrder(Order.desc("SM.id.codsubentidad"));
				}
		}
		
		//ASF: BUG DE ORACLE + HIBERNATE. ES NECESARIO AÑADIR SIEMPRE LA ORDENACIÓN POR UN ID 
		//PARA QUE FUNCIONE CORRECTAMENTE EL LISTADO PAGINADO CUANDO ORDENO POR OTRO CAMPO
		//CON ESTO SE CORRIGE EL PROBLEMA DE QUE AL PAGINAR MUESTRE SIEMPRE LOS MISMOS REGISTROS.
		criteria.addOrder(Order.asc("id"));
		
		criteria.setFirstResult(pageProperties.getIndexRowMin());
		criteria.setMaxResults(pageProperties.getPageSize());
		
		return criteria.list();
	}
	
	/* -------------- fin displaytag pagination -----------------*/
	
	
	
	
	
	public Map<String, Object> getMapaColectivo(Colectivo colectivo) {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final Long id = colectivo.getId();
		if (FiltroUtils.noEstaVacio(id)) {
			mapa.put("id", id);
		}

		final BigDecimal codPlan = colectivo.getLinea().getCodplan();
		if (FiltroUtils.noEstaVacio(codPlan)) {
			mapa.put("lin.codplan", codPlan);
		}
		final BigDecimal codLinea = colectivo.getLinea().getCodlinea();
		if(FiltroUtils.noEstaVacio(codLinea)){
			mapa.put("lin.codlinea", codLinea);
		}

		final Long lineaSeguroId = colectivo.getLinea().getLineaseguroid();
		if (FiltroUtils.noEstaVacio(lineaSeguroId)) {
			mapa.put("lin.lineaseguroid", lineaSeguroId);
		}

		final String cifTomador = colectivo.getTomador().getId().getCiftomador();
		if (FiltroUtils.noEstaVacio(cifTomador)) {
			mapa.put("tom.id.ciftomador", cifTomador);
		}

		final String idColectivo = colectivo.getIdcolectivo();
		if (FiltroUtils.noEstaVacio(idColectivo)) {
			mapa.put("idcolectivo", idColectivo);
		}
		
		final String dc = colectivo.getDc();
		if (FiltroUtils.noEstaVacio(dc)) {
			mapa.put("dc", dc);
		}

		final BigDecimal entMediadora = colectivo.getSubentidadMediadora().getId().getCodentidad();
		if (FiltroUtils.noEstaVacio(entMediadora)) {
			mapa.put("subentidadMediadora.id.codentidad", entMediadora);
		}

		final BigDecimal subEntMediadora = colectivo.getSubentidadMediadora().getId().getCodsubentidad();
		//DAA 13/08/2013
		if (subEntMediadora != null) {
			mapa.put("subentidadMediadora.id.codsubentidad", subEntMediadora);
		}

		final Character activo = colectivo.getActivo();
		if (FiltroUtils.noEstaVacio(activo)) {
			mapa.put("activo", activo);
		}

		final BigDecimal pctPrimerPago= colectivo.getPctprimerpago();
		if (FiltroUtils.noEstaVacio(pctPrimerPago)) {
			mapa.put("pctprimerpago", pctPrimerPago);
		}

		final Date fechaPrimerPago = colectivo.getFechaprimerpago();
		if (FiltroUtils.noEstaVacio(fechaPrimerPago)) {
			mapa.put("fechaprimerpago", fechaPrimerPago);
		}
		
		final BigDecimal pctSegundoPago = colectivo.getPctsegundopago();
		if (FiltroUtils.noEstaVacio(pctSegundoPago)) {
			mapa.put("pctsegundopago", pctSegundoPago);
		}

		final Date fechaSegundoPago = colectivo.getFechasegundopago();
		if (FiltroUtils.noEstaVacio(fechaSegundoPago)) {
			mapa.put("fechasegundopago", fechaSegundoPago);
		}
		final String cccEntidad = colectivo.getCccEntidad();
		if (FiltroUtils.noEstaVacio(cccEntidad)) {
			mapa.put("cccEntidad", cccEntidad);
		}
		final String cccOficina = colectivo.getCccOficina();
		if (FiltroUtils.noEstaVacio(cccOficina)) {
			mapa.put("cccOficina", cccOficina);
		}
		final String cccDc = colectivo.getCccDc();
		if (FiltroUtils.noEstaVacio(cccDc)) {
			mapa.put("cccDc", cccDc);
		}
		final String cccCuenta = colectivo.getCccCuenta();
		if (FiltroUtils.noEstaVacio(cccCuenta)) {
			mapa.put("cccCuenta", cccCuenta);
		}
		final String observaciones = colectivo.getObservaciones();
		if (FiltroUtils.noEstaVacio(observaciones)) {
			mapa.put("observaciones", observaciones);
		}
		final Integer tipoDescRecarg = colectivo.gettipoDescRecarg();
		if (FiltroUtils.noEstaVacio(tipoDescRecarg)) {
			mapa.put("tipoDescRecarg", tipoDescRecarg);
		}
		final BigDecimal pctDescRecarg =colectivo.getpctDescRecarg();
		if (FiltroUtils.noEstaVacio(pctDescRecarg)) {
			mapa.put("pctDescRecarg", pctDescRecarg);
		}
		/* Pet. 5385 ** MODIF TAM (13.11.2018) ** Inicio */
		final Character envioIbanAgro = colectivo.getEnvioIbanAgro();
		if (FiltroUtils.noEstaVacio(envioIbanAgro)) {
			mapa.put("envioIbanAgro", envioIbanAgro);
		} 
		
		return mapa;
	}

	@Override
	public boolean existeOficina(Oficina oficina) throws DAOException {
		Session session = obtenerSession();
		try {
			
			Query query = session.createQuery("select count(*) from Oficina o where o.id.codentidad = :entidad and o.id.codoficina = :oficina")
																									.setBigDecimal("entidad", oficina.getId().getCodentidad())
																									.setBigDecimal("oficina", oficina.getId().getCodoficina());
			Long res = (Long) query.uniqueResult();
			
			if(res == 0)
				return false;
			else
				return true;
			
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos ",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Colectivo> getAll()throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(Colectivo.class);			
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	/**
	 * MÃ©todo para obtener los colectivos que cumplen que el id es distinto y la referencia y el digito de control son iguales
	 * @param id Identificador de colectivo
	 * @param referencia Referencia del colectivo
	 * @param dc DÃ­gito de control del colectivo
	 */
	public List<Colectivo> getColectivos(Long id, String referencia, String dc){
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(Colectivo.class);		
			criteria.add(Restrictions.eq("idcolectivo", referencia));
			criteria.add(Restrictions.eq("dc", dc));
			if (id != null)
				criteria.add(Restrictions.ne("id", id));
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error al obtener los colectivos para id = " + id + ", referencia = " + referencia + "-" + dc, ex);
		}
		return null;
	}
	
	/**
	 * obtiene la linea segun codlinea y codplan
	 * @param codLinea
	 * @param codPlan
	 * 
	 */
	public Linea getLineaColectivo(BigDecimal codLinea, BigDecimal codPlan){
		Session sesion = obtenerSession();
			
			List<Linea> lstLineas = new ArrayList<Linea>();
			Linea lin= null;
			Criteria criteria = sesion.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", codLinea));
			criteria.add(Restrictions.eq("codplan", codPlan));
	    		
			lstLineas=criteria.list();
    	
	    	if (lstLineas!=null)
	    		if (lstLineas.size()>0)
	    			lin= lstLineas.get(0);
	    			
	    	return lin;				
	}




	@Override
	public Colectivo activarColectivo(Long id) throws DAOException {
		
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(Colectivo.class);		
			criteria.add(Restrictions.eq("id", id));
			
			Colectivo c = (Colectivo) criteria.uniqueResult();
			
			c.setActivo(Constants.COLECTIVO_ACTIVO);
			sesion.saveOrUpdate(c);
			
			return c;
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al activar el colectivo", ex);
			throw new DAOException();
		}
		
	}




	@Override
	public ArrayList<BigDecimal> getPlanesFiltroInicial() throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(Linea.class);
			
			criteria.add(Restrictions.eq("estado", Constants.LINEA_IMPORTADA_OK));
			criteria.add(Restrictions.eq("activo", Constants.LINEA_ACTIVA_SI));
			criteria.addOrder(Order.desc("codplan"));
			criteria.setProjection(Projections.distinct(Projections.property("codplan")));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			return (ArrayList<BigDecimal>) criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al obtener los planes del filtro inicial el colectivo", ex);
			throw new DAOException();
		}
	}
	
	
	/**
	 * obtiene el digito de control del colectivo
	 * @param idColectivo
	 * 
	 */
	@Override
	public String getDcColectivo(String idColectivo){
		Session sesion = obtenerSession();
			
			Colectivo colectivo = new Colectivo();
			
			Criteria criteria = sesion.createCriteria(Colectivo.class);
			criteria.add(Restrictions.eq("idcolectivo", idColectivo));
			criteria.addOrder(Order.asc("dc"));

	    		
			colectivo = (Colectivo) criteria.list().get(0);
	    			
			colectivo.getDc();
	    	return colectivo.getDc();				
	}
	
}
