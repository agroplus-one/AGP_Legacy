package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.admin.impl.Colectivo2Filtro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cpl.gan.MedidaG;
import com.rsi.agp.dao.tables.orgDat.VistaTipoCapitalGanado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.pagination.PageProperties;

public class ClaseDao extends BaseDaoHibernate implements IClaseDao {
	private Log logger = LogFactory.getLog(ClaseDao.class);
	
	@SuppressWarnings("unchecked")
	public List<Clase> getPageClases(Clase polizaBean,Colectivo2Filtro colectivo2Filtro, PageProperties pageProperties) {
		Session session = obtenerSession();

		Criteria criteria = session.createCriteria(Clase.class);

		if (pageProperties.getDir().equals("asc")) {
			criteria.addOrder(Order.asc(pageProperties.getSort()));
		} else if (pageProperties.getDir().equals("desc")) {
			criteria.addOrder(Order.desc(pageProperties.getSort()));
		}
		criteria.setFirstResult(pageProperties.getIndexRowMin());
		criteria.setMaxResults(pageProperties.getPageSize());

		return criteria.list();
	}

	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Clase.class);

		criteria.createAlias("linea", "lin");
		criteria.createAlias("subentidadMediadora", "SM");
		criteria.createAlias("tomador", "tom");
		criteria.addOrder(Order.asc("tom.entidad.codentidad"));
		criteria.addOrder(Order.asc("SM.entidadMediadora.codentidad"));
		// criteria.addOrder(Order.asc("SM.id.codentidad"));
		criteria.addOrder(Order.desc("lin.codplan"));
		criteria.addOrder(Order.asc("lin.codlinea"));
		criteria.add(Restrictions.isNull("SM.fechabaja"));
		return criteria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> dameListaModulosClase (long lineaseguroid, BigDecimal clase){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ClaseDetalle.class);
		criteria.createAlias("clase", "aliasClase");
		criteria.add(Restrictions.eq("aliasClase.clase", clase));
		criteria.add(Restrictions.eq("aliasClase.linea.lineaseguroid", lineaseguroid));
		//criteria.add(Restrictions.ne("modulo.id.codmodulo", "99999"));
		criteria.setProjection(Projections.distinct(Projections.property("codmodulo")));
		List<String> lstModulos = criteria.list();
		
		return lstModulos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Clase> getListaClases(Clase claseBean) throws DAOException{
		
		Session session = obtenerSession();
		try{
			Criteria criteria = session.createCriteria(Clase.class);
			long lineaseguroid= claseBean.getLinea().getLineaseguroid();
			criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			BigDecimal clase=claseBean.getClase();
			String descripcion=claseBean.getDescripcion();
			if (FiltroUtils.noEstaVacio(clase)) {
				criteria.add(Restrictions.eq("clase", clase));
			}		
			if (FiltroUtils.noEstaVacio(descripcion)) {
				criteria.add(Restrictions.ilike("descripcion", "%".concat(descripcion).concat("%")));
			}
			
			List<Clase> lstClase = criteria.list();
			String strClase = "";
			
			//eliminar de lstClase las clases repetidas
			List<Clase> lstDef = new ArrayList<Clase>();
			for (Clase c: lstClase){
				if (strClase.indexOf("|"+c.getClase()+"|") < 0){
					lstDef.add(c);
					strClase += "|"+c.getClase()+"|";
				}
			}
			return lstDef;
		} catch (Exception e) {
			logger.error("Error al obtener la lista de clases.", e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ClaseDetalle> getListaDetalleClases(ClaseDetalle claseDetalleBean) throws NullPointerException {

		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ClaseDetalle.class);
		Long lineaseguroid = claseDetalleBean.getClase().getLinea().getLineaseguroid();
		criteria.createAlias("clase", "aliasClase");
		criteria.add(Restrictions.eq("aliasClase.linea.lineaseguroid", lineaseguroid));
		Long idclase = claseDetalleBean.getClase().getId();
		String descripcion = claseDetalleBean.getClase().getDescripcion();
		String modulo = claseDetalleBean.getCodmodulo();
		String ciclocultivo = "";
		try {
			ciclocultivo = claseDetalleBean.getCicloCultivo().getCodciclocultivo().toString();
		} catch (NullPointerException e) {

		}

		String cultivo = "";
		try {
			cultivo = claseDetalleBean.getCultivo().getId().getCodcultivo().toString();
		} catch (NullPointerException e) {
			// cultivo = "";
		}
		String variedad = "";
		try {
			variedad = claseDetalleBean.getVariedad().getId().getCodvariedad().toString();
		} catch (NullPointerException e) {
			// variedad = "";
		}
		String codprovincia = "";
		try {
			codprovincia = claseDetalleBean.getCodprovincia().toString();
		} catch (NullPointerException e) {
			// codprovincia = "";
		}
		String codcomarca = "";
		try {
			codcomarca = claseDetalleBean.getCodcomarca().toString();
		} catch (NullPointerException e) {
			// codcomarca = "";
		}
		String codtermino = "";
		try {
			codtermino = claseDetalleBean.getCodtermino().toString();
		} catch (NullPointerException e) {
			// codtermino = "";
		}
		String subtermino = "";
		try {
			subtermino = claseDetalleBean.getSubtermino().toString();
		} catch (NullPointerException e) {
			// subtermino = "";
		}

		if (FiltroUtils.noEstaVacio(idclase)) {
			criteria.add(Restrictions.eq("aliasClase.id", idclase));
		}
		if (FiltroUtils.noEstaVacio(descripcion)) {
			criteria.add(Restrictions.ilike("aliasClase.descripcion", "%".concat(descripcion).concat("%")));
		}
		if (FiltroUtils.noEstaVacio(modulo)) {
			criteria.add(Restrictions.ilike("codmodulo", "%".concat(modulo).concat("%")));
		}
		if (FiltroUtils.noEstaVacio(ciclocultivo)) {
			criteria.add(Restrictions.eq("cicloCultivo.codciclocultivo", new BigDecimal(ciclocultivo)));
		}
		if (FiltroUtils.noEstaVacio(cultivo)) {
			criteria.add(Restrictions.eq("cultivo.id.codcultivo", new BigDecimal(cultivo)));
		}
		if (FiltroUtils.noEstaVacio(variedad)) {
			criteria.add(Restrictions.eq("variedad.id.codvariedad", new BigDecimal(variedad)));
		}
		if (FiltroUtils.noEstaVacio(codprovincia)) {
			criteria.add(Restrictions.eq("codprovincia", new BigDecimal(codprovincia)));
		}
		if (FiltroUtils.noEstaVacio(codcomarca)) {
			criteria.add(Restrictions.eq("codcomarca", new BigDecimal(codcomarca)));
		}
		if (FiltroUtils.noEstaVacio(codtermino)) {
			criteria.add(Restrictions.eq("codtermino", new BigDecimal(codtermino)));
		}
		if (FiltroUtils.noEstaVacio(subtermino)) {
			criteria.add(Restrictions.ilike("subtermino", "%".concat(subtermino).concat("%")));

		}

		List<ClaseDetalle> lstClase = criteria.list();
		return lstClase;
	}
	
	@SuppressWarnings("unchecked")
	public List<Clase> getClase(long lineaseguroid) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Clase.class);
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		List<Clase> claseFiltro = criteria.list();
		
		return claseFiltro;
	}
	
	/**
	 * Obtiene la clase asociada a la póliza
	 * @param p
	 * @return
	 * @throws DAOException 
	 */
	public Clase getClase (Poliza p) throws DAOException {
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(Clase.class);
			criteria.add(Restrictions.eq("linea.lineaseguroid", p.getLinea().getLineaseguroid()));
			criteria.add(Restrictions.eq("clase", p.getClase()));
			
			return (Clase) criteria.uniqueResult();
		}
		
		catch (Exception e) {
			logger.error("Error al obtener la clase de la póliza" , e);
			throw new DAOException("ClaseDao - Se ha producido un error al obtener la clase asociada a la póliza", e);
		}				
	}
	
	
	public Clase getClaseById(long idClase) {
		
		Session session = obtenerSession();
		
		Criteria criteria = session.createCriteria(Clase.class);
		criteria.add(Restrictions.eq("id", idClase));
		 
		
		return (Clase) criteria.uniqueResult();
	}
	
	
	@SuppressWarnings("rawtypes")
	public Set getClaseDetalle (long lineaseguroid, BigDecimal clase) {
		
		Session session = obtenerSession();

		Criteria criteria = session.createCriteria(Clase.class);
			
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("clase", clase));
				
		Clase claseAux = (Clase )criteria.uniqueResult();
		Set detalle = claseAux.getClaseDetalles();
		
		return  detalle;
	}
	
@SuppressWarnings("rawtypes")
public Set getClaseDetalleGanado (long lineaseguroid, BigDecimal clase) {
		
		Session session = obtenerSession();

		Criteria criteria = session.createCriteria(Clase.class);
			
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("clase", clase));
				
		Clase claseAux = (Clase )criteria.uniqueResult();
		Set detalle = claseAux.getClaseDetallesGanado();
		
		return  detalle;
	}
	
	
	public Clase getClase (long lineaseguroid, BigDecimal clase) {
		Clase cl = null;
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Clase.class);
			
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("clase", clase));
				
		cl = (Clase )criteria.uniqueResult();
		
		return  cl;
	}
	
	// ASF - 17/10/2012 - Adaptaciones 314
	@SuppressWarnings("rawtypes")
	public String getComprobarAac (Long lineaseguroid, BigDecimal clase){
		try {
			Session session = obtenerSession();
			String sql = "select COMPROBAR_AAC from O02AGPE0.TB_CLASE where LINEASEGUROID = " + lineaseguroid + " and CLASE = " + clase;
			List list = session.createSQLQuery(sql).list();
			return (String) list.get(0);
		} catch (Exception e) {
			logger.error("Error en getComprobarAac", e);
		}
		return "";
	}
	@SuppressWarnings("unchecked")
	public List<Character> obtenerGruposNegocio(Map<String,List<Long>> mapListas,List<String> listMod,List<Character> listSubterminos){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(VistaTipoCapitalGanado.class);
		List<Character> lstGruposNeg = new ArrayList<Character>();
		if (listMod != null) criteria.add(Restrictions.in("id.codmodulo",listMod));
		if (mapListas.get("listEspecies")   != null) criteria.add(Restrictions.in("id.codespecie", mapListas.get("listEspecies")));
		if (mapListas.get("listRegimen")    != null) criteria.add(Restrictions.in("id.codregimen", mapListas.get("listRegimen")));
		if (mapListas.get("listGrupoRaza")  != null) criteria.add(Restrictions.in("id.codgruporaza", mapListas.get("listGrupoRaza")));
		if (mapListas.get("listTipAnimal")  != null) criteria.add(Restrictions.in("id.codtipoanimal", mapListas.get("listTipAnimal")));
		if (mapListas.get("listTipCapital") != null) criteria.add(Restrictions.in("id.codtipocapital", mapListas.get("listTipCapital")));
		if (mapListas.get("codprovincia")   != null) criteria.add(Restrictions.in("id.codprovincia", mapListas.get("codprovincia")));
		if (mapListas.get("listComarcas")   != null) criteria.add(Restrictions.in("id.codcomarca", mapListas.get("listComarcas")));
		if (mapListas.get("listTerminos")   != null) criteria.add(Restrictions.in("id.codtermino", mapListas.get("listTerminos")));
		if (listSubterminos != null) criteria.add(Restrictions.in("id.subtermino", listSubterminos));
		
		List<VistaTipoCapitalGanado> lstVistaT = criteria.list();
		
		for (VistaTipoCapitalGanado vt:lstVistaT) {
			if (!lstGruposNeg.contains(vt.getId().getGrupoNegocio())) {
				lstGruposNeg.add(vt.getId().getGrupoNegocio());
			}
		}
		return lstGruposNeg;
			
		
	}
	
	@SuppressWarnings("unchecked")
	public List<MedidaG> getMedidasGanado(Long lineaseguroid,List<Character> lstGrNeg,List<String> lstMods,
			List<Long> lstEspecies, String nifCif){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(MedidaG.class);
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));	
		criteria.add(Restrictions.eq("nifcif", nifCif));
		if (lstGrNeg != null && lstGrNeg.size()>0){
			criteria.createAlias("gruposNegocio", "gruposNegocio");
			criteria.add(Restrictions.in("gruposNegocio.grupoNegocio", lstGrNeg));
		}
		if (lstMods != null && lstMods.size()>0){
			criteria.createAlias("modulo", "aliasModulo");
			criteria.add(Restrictions.in("aliasModulo.id.codmodulo", lstMods));
		}
		if (lstEspecies != null && lstEspecies.size()>0){
			criteria.createAlias("especie", "aliasEspecie");
			criteria.add(Restrictions.in("aliasEspecie.id.codespecie", lstEspecies));
			criteria.add(Restrictions.eq("especie.id.lineaseguroid", lineaseguroid));
		}
		List <MedidaG> lstMedidasG = criteria.list();
		return lstMedidasG;
	}
	
}
