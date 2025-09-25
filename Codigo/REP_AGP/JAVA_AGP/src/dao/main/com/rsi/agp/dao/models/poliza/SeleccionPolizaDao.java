package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.TransactionSystemException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.util.ParcelaUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.poliza.ParcelaFiltro;
import com.rsi.agp.dao.filters.poliza.PolizaFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaHistoricoBorrado;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.PreciosProduccionesVO;
import com.rsi.agp.vo.ProduccionVO;

import edu.emory.mathcs.backport.java.util.Arrays;

import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

@SuppressWarnings("unchecked")
public class SeleccionPolizaDao extends BaseDaoHibernate implements ISeleccionPolizaDao {

	private final Log logger = LogFactory.getLog(getClass());
	
	private static final String POLIZA_ID_POLIZA = "poliza.idpoliza";
	private static final String ID_LINEA_SEGURO_ID = "id.lineaseguroid";
	private static final String ERROR_ACCESO_BD = "Se ha producido un error durante el acceso a la base de datos";
	private static final String ERROR_ACCESO_BBDD = "[ERROR] al acceder a la BBDD.";
	private static final String P_RESULT = "P_RESULT";
	private static final String COD_CONCEPTO_REND = "codConceptoRendimiento";
	private static final String CLASE_ID = "clase.id";
	private static final String ID_PARCELA = "idparcela";

	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	private IDatosParcelaDao datosParcelaDao;

	public void setDatosParcelaDao(IDatosParcelaDao datosParcelaDao) {
		this.datosParcelaDao = datosParcelaDao;
	}

	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}

	/**
	 * DAA 18/05/2012 Recoge todos los ids de las polizas segun el filtro de
	 * busqueda y los convierte a un String
	 */
	public String getIdsPolizas(PolizaFiltro polizaFiltro) {
		String listaids = "";
		Session session = obtenerSession();
		/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
		String sql = "select P.idpoliza from TB_POLIZAS P inner join TB_COLECTIVOS C on P.IDCOLECTIVO = C.ID "
				+ "inner join TB_LINEAS L on P.LINEASEGUROID = L.LINEASEGUROID "
				+ "inner join Tb_ASEGURADOS A on P.IDASEGURADO = A.ID "
				+ "inner join TB_ESTADOS_POLIZA E on P.IDESTADO = E.IDESTADO "
				+ "inner join TB_USUARIOS U on P.Codusuario = U.CODUSUARIO " 
				+ "inner join TB_GED_DOC_POLIZA D on P.IDPOLIZA= D.IDPOLIZA" 
		/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */				
				+ polizaFiltro.getSqlWhere();
		// String sql = "SELECT IDPOLIZA FROM TB_POLIZAS P,TB_COLECTIVOS C,
		// TB_ASEGURADOS A, TB_LINEAS L"+polizaFiltro.getSqlWhere()+"AND P.
		// IDCOLECTIVO=C.ID";
		List<?> lista = session.createSQLQuery(sql).list();

		for (int i = 0; i < lista.size(); i++) {
			listaids += lista.get(i) + ";";
		}
				
		return listaids;
	}

	public HashMap<BigDecimal, BigDecimal> getLineasRecalculo() {

		Session session = obtenerSession();
		HashMap<BigDecimal, BigDecimal> lineas = new HashMap<BigDecimal, BigDecimal>();

		String sql = "SELECT AGP_VALOR FROM TB_CONFIG_AGP WHERE AGP_NEMO = 'recalculoParcelas'";

		List<String> list = session.createSQLQuery(sql).list();

		if (list != null && list.size() > 0) {
			String l = (String) list.get(0);
			String[] aux = l.split(",");
			for (int i = 0; i < aux.length; i++) {
				lineas.put(new BigDecimal(aux[i]), new BigDecimal(aux[i]));
			}
		}
		return lineas;
	}

	public int getCountPolizas(PolizaFiltro polizaFiltro) {

		Session session = obtenerSession();
		String sql = "SELECT AGP_VALOR FROM TB_CONFIG_AGP WHERE AGP_NEMO = 'SQL_COUNT_POLIZAS_UTILIDADES'";
		List<?> list = session.createSQLQuery(sql).list();

		sql = list.get(0).toString();

		sql += polizaFiltro.getSqlWhere();
		
		return ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();
	}

	/**
	 * Elimina una parcela de la BD
	 * 
	 * @param idParcela:
	 *            id parcela a borrar
	 */
	public void deleteParcela(Long idParcela) throws DAOException {
		Session session = obtenerSession();
		try {
			String sql = "DELETE TB_PARCELAS WHERE IDPARCELA = " + idParcela;
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception excepcion) {
			logger.error("Error al borrar la parcela por id " + idParcela, excepcion);
			throw new DAOException("Error al borrar la parcela por id " + idParcela, excepcion);
		}
	}

	/**
	 * Elimina varias parcelas
	 * 
	 * @param listaParcelas:
	 *            listado de parcelas a eliminar
	 */
	public void deleteParcelas(List<Long> listaParcelas) throws DAOException {
		Session session = obtenerSession();

		String listaParcelasString = "";

		for (Long parcela : listaParcelas) {
			listaParcelasString = listaParcelasString + parcela.toString() + ",";
		}
		// Eliminamos la ultima coma
		listaParcelasString = listaParcelasString.substring(0, listaParcelasString.length() - 1);

		try {
			String sql = "DELETE TB_PARCELAS WHERE IDPARCELA IN (" + listaParcelasString + ")";
			logger.debug("Query: " + sql);
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception excepcion) {
			logger.error("Error al borrar las parcelas", excepcion);
			throw new DAOException("Error al borrar las parcelas", excepcion);
		}
	}

	/**
	 * Elimina un objeto CapAsegRelModulo
	 * 
	 * @param parcela:
	 *            parcela a borrar
	 */
	public void deleteCapAsegRelModulo(CapAsegRelModulo carm) {
		Session session = obtenerSession();

		try {
			session.delete(carm);
			session.flush();
		} catch (Exception excepcion) {
			logger.error("Error al borrar el CapAsegRelModulo " + carm.getId(), excepcion);
		} finally {
		}
	}

	/**
	 * DAA 12/06/2012 Obtiene un listado de parcelas filtrado por los valores del
	 * parametro. Listado de parcelas
	 * 
	 * @param Parcela
	 *            parcela de búsqueda
	 * @return listparcelas
	 */
	public List<Parcela> getParcelas(Parcela parcela, String columna, String orden) {
		final ParcelaFiltro filter = new ParcelaFiltro(parcela, columna, orden);
		return this.getObjects(filter);
	}

	/**
	 * Duplica una parcela en la BD (en profundidad)
	 * 
	 * @param idParcela
	 */
	public void cloneParcela(Long idParcela) {
		Session session = obtenerSession();
		Parcela parcela = null;
		Parcela copyOfParcela = null;
		
		try{
            parcela = getParcela(idParcela);
            copyOfParcela = cloneDeepParcela(parcela);
            session.persist(copyOfParcela);
		}
		catch(Exception excepcion){
			logger.error("Excepcion : SeleccionPolizaDao - cloneParcela", excepcion);
		}
	}

	/**
	 * Obtiene una parcela de la BD
	 * 
	 * @param idParcela:
	 *            PK en la BD
	 */
	public Parcela getParcela(Long idParcela) {
		Session session = obtenerSession();
		Parcela parcela = null;
		
		try{
			parcela  = (Parcela)session.get(Parcela.class,idParcela);
		}
		catch(Exception excepcion){
			logger.error("Excepcion : SeleccionPolizaDao - getParcela", excepcion);
		}
		return parcela;
	}

	public Long savePoliza(Poliza polizaBean) {
		Session session = obtenerSession();
		Long id = null;

		try {
			session.saveOrUpdate(polizaBean);
			id = polizaBean.getIdpoliza();
		}
		catch(Exception excepcion){
			logger.error("Excepcion : SeleccionPolizaDao - savePoliza", excepcion);
		}
		return id;
	}

	public Long saveParcela(Parcela parcela) {
		Session session = obtenerSession();
		Long idParcela = parcela.getIdparcela();
		try {
			idParcela = (Long) session.save(parcela);
		} catch (Exception ex) {
		} finally {
		}
		return idParcela;
	}

	public Long saveCapAseg(CapitalAsegurado capAseg) {
		Session session = obtenerSession();
		Long idCapAseg = capAseg.getIdcapitalasegurado();
		try {
			idCapAseg = (Long) session.save(capAseg);
		} catch (Exception ex) {
		} finally {
		}
		return idCapAseg;
	}

	/**
	 * Obtiene una copia en profundidad de una parcela (todos deben ser objetos
	 * nuevos -- deep copy no shadow copy)
	 * 
	 * @param parcela
	 *            --> parcela a copiar
	 */
	private Parcela cloneDeepParcela(Parcela parcela) {

		Parcela copyOfParcela = new Parcela();
		return copyOfParcela;
	}

	public List<ModuloPoliza> getModulosPoliza(Long idPoliza) throws BusinessException {
		List<ModuloPoliza> lista = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ModuloPoliza.class);
			criteria.add(Restrictions.eq(POLIZA_ID_POLIZA, idPoliza));
			lista = criteria.list();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(e);
		} 
		return lista;
	}

	public List<ComparativaPoliza> getModulosPolizaWithComparativa(Long idPoliza) throws BusinessException {
		List<ComparativaPoliza> listComparativasPoliza = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ComparativaPoliza.class);
			// idpoliza y codvalor = -1(seleccionada)
			criteria.add(Restrictions.eq(POLIZA_ID_POLIZA, idPoliza));
			listComparativasPoliza = criteria.list();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(e);
		} 
		return listComparativasPoliza;
	}

	public List<TipoCapital> getTiposCapitales(Long idlinea) throws DAOException {
		Session session = obtenerSession();
		List<TipoCapital> tipCapitales = null;

		try {
			Criteria c = session.createCriteria(Precio.class);
			c.setProjection(
					Projections.distinct(Projections.projectionList().add(Projections.property("tipoCapital"))));

			Criterion crit1 = Restrictions.eq(ID_LINEA_SEGURO_ID, idlinea);
			c.add(crit1);

			tipCapitales = c.list();
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error recuperando los Tipos de Capital", ex);
		} finally {
		}

		return tipCapitales;
	}

	public TipoCapital getTipoCapitalById(Long codtipocapital) throws DAOException {
		try {

			return (TipoCapital) get(TipoCapital.class, new BigDecimal(codtipocapital));

		} catch (Exception e) {
			throw new DAOException(ERROR_ACCESO_BD, e);
		}
	}

	@Override
	public void borrarPoliza(Poliza poliza, Usuario usuario) throws DAOException {
		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;
		logger.debug("SeleccionPolizaDao - Inicia la transaccion de borradoP�liza");
		tx = session.beginTransaction();
		try {
			logger.info("SeleccionPolizaDao - borrarPoliza - idpoliza = " + poliza.getIdpoliza() + ". Inicio ");

			String sql = "UPDATE TB_POLIZAS SET IDESTADO = " + Constants.ESTADO_POLIZA_BAJA + " WHERE IDPOLIZA = "
					+ poliza.getIdpoliza();
			session.createSQLQuery(sql).executeUpdate();

			// Insertamos en p�lizas_historico_borrado;
			PolizaHistoricoBorrado polBorrado = new PolizaHistoricoBorrado(poliza, usuario.getCodusuario());
			String insert = polBorrado.getInsertPolizaHistoricoBorrado();
			logger.info("SeleccionPolizaDao - InsertPolizaHistorico = " + insert);
			session.createSQLQuery(insert).executeUpdate();
			logger.debug("SeleccionPolizaDao - Commit de la transacci�n de borradoP�liza");
			tx.commit();

		} catch (Exception ex) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw new DAOException(
					"Se ha producido un error al intentar borrar la poliza con idPoliza = " + poliza.getIdpoliza(), ex);
		} finally {
			logger.debug("SeleccionPolizaDao - Cierra la sesion");
			if (session != null)
				session.close();
			// TMR Facturacion. Al borrar una poliza (desde ciclo de poliza o desde
			// utilidades)facturamos.
			this.callFacturacion(usuario, "A");
		}
	}

	@Override
	public List<Poliza> getPolizasButEstadosGrupoEnt(Poliza polizaBean, PolizaFiltro polizaFiltro) throws DAOException {

		List<Poliza> listPolizas = null;

		try {
			listPolizas = getPagePolizas(polizaBean, polizaFiltro);

		} catch (Exception e) {
			logger.error("Error al obtener el listado de polizas", e);
		}

		return listPolizas;
	}

	/* -------------------------------------------------------- */
	/* DISPLAYTAG PAGINATION */
	/* -------------------------------------------------------- */
	public PaginatedListImpl<Poliza> getPaginatedListPolizasButEstadosGrupoEnt(Poliza polizaBean,
			PageProperties pageProperties, PolizaFiltro polizaFiltro, Usuario usuario) throws DAOException {
		PaginatedListImpl<Poliza> paginatedListImpl = new PaginatedListImpl<Poliza>();

		List<Poliza> listPolizas = null;

		try {
			logger.info("pageProperties.getFullListSize()" + pageProperties.getFullListSize());
			logger.info("pageProperties.getIndexRowMax()" + pageProperties.getIndexRowMax());
			logger.info("pageProperties.getIndexRowMin()" + pageProperties.getIndexRowMin());
			logger.info("pageProperties.getPageNumber()" + pageProperties.getPageNumber());
			logger.info("pageProperties.getPageSize()" + pageProperties.getPageSize());

			listPolizas = getPagePolizas(polizaBean, polizaFiltro, pageProperties, usuario);

			paginatedListImpl.setFullListSize(pageProperties.getFullListSize());
			paginatedListImpl.setObjectsPerPage(pageProperties.getPageSize());
			paginatedListImpl.setPageNumber(pageProperties.getPageNumber());
			paginatedListImpl.setList(listPolizas);
			paginatedListImpl.setSortCriterion(pageProperties.getSort());
			if (pageProperties.getDir().equals("asc")) {
				paginatedListImpl.setSortDirection(SortOrderEnum.ASCENDING);
			} else if (pageProperties.getDir().equals("desc")) {
				paginatedListImpl.setSortDirection(SortOrderEnum.DESCENDING);
			}

		} catch (Exception e) {
			logger.error("Excepcion : SeleccionPolizaDao - getPaginatedListPolizasButEstadosGrupoEnt", e);
		}

		return paginatedListImpl;
	}

	public List<Poliza> getPagePolizas(Poliza polizaBean, PolizaFiltro polizaFilter, PageProperties pageProperties,
			Usuario usuario) {
		List<Poliza> listPolizas = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = polizaFilter.getCriteria(session);			
			if (pageProperties.getDir().equals("asc")) {
				criteria.addOrder(Order.asc(pageProperties.getSort()));
			} else if (pageProperties.getDir().equals("desc")) {
				criteria.addOrder(Order.desc(pageProperties.getSort()));
			}
			criteria.setFirstResult(pageProperties.getIndexRowMin());
			criteria.setMaxResults(pageProperties.getPageSize());
			listPolizas = criteria.list();
			// TMR Facturacion. Al consultar las polizas facturamos.
			callFacturacion(usuario, Constants.FACTURA_CONSULTA);
		} catch (DAOException e) {
			logger.error("Error en el acceso a base de datos", e);
		}

		return listPolizas;
	}

	public List<Poliza> getPagePolizas(Poliza polizaBean, PolizaFiltro polizaFilter) {
		Session session = obtenerSession();

		return polizaFilter.getCriteria(session).list();
	}

	/* -------------- fin displaytag pagination ----------------- */

	public Map<String, Object> getMapaPoliza(Poliza poliza) {

		final Map<String, Object> mapa = new HashMap<String, Object>();

		final String usuario = poliza.getUsuario() != null ? poliza.getUsuario().getCodusuario() : null;
		if (FiltroUtils.noEstaVacio(usuario)) {
			mapa.put("usuario.codusuario", usuario);
		}
		final BigDecimal usuDelegacion = poliza.getUsuario().getDelegacion();
		if (FiltroUtils.noEstaVacio(usuDelegacion)) {
			mapa.put("u.delegacion", usuDelegacion);
		}

		/*
		 * DATOS DEL COLECTIVO
		 */
		final String cifTomador = poliza.getColectivo().getTomador().getId().getCiftomador();

		if (FiltroUtils.noEstaVacio(cifTomador)) {
			mapa.put("col.tomador.id.ciftomador", cifTomador);
		}

		final BigDecimal codEntidad = poliza.getColectivo().getTomador().getId().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			mapa.put("col.tomador.id.codentidad", codEntidad);
		}

		final BigDecimal codPlan = poliza.getLinea().getCodplan();
		if (FiltroUtils.noEstaVacio(codPlan)) {
			mapa.put("lin.codplan", codPlan);
		}

		final BigDecimal codlinea = poliza.getLinea().getCodlinea();
		if (FiltroUtils.noEstaVacio(codlinea)) {
			mapa.put("lin.codlinea", codlinea);
		}

		final Long lineaseguro = poliza.getColectivo().getLinea().getLineaseguroid();
		if (FiltroUtils.noEstaVacio(lineaseguro)) {
			mapa.put("col.linea.lineaseguroid", lineaseguro);
		}

		final Long idColectivo = poliza.getColectivo().getId();
		if (FiltroUtils.noEstaVacio(idColectivo)) {
			mapa.put("col.id", idColectivo);
		}
		final String colectivo = poliza.getColectivo().getIdcolectivo();
		if (FiltroUtils.noEstaVacio(colectivo)) {
			mapa.put("col.idcolectivo", colectivo);
		}

		final String dcColectivo = poliza.getColectivo().getDc();
		if (FiltroUtils.noEstaVacio(dcColectivo)) {
			mapa.put("col.dc", dcColectivo);
		}
		final BigDecimal codEntidadES = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidadES)) {
			mapa.put("subent.id.codentidad", codEntidadES);
		}
		final BigDecimal codSubEntidadES = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();
		if (FiltroUtils.noEstaVacio(codSubEntidadES)) {
			mapa.put("subent.id.codsubentidad", codSubEntidadES);
		}

		/*
		 * DATOS DEL ASEGURADO
		 */
		final Long idAsegurado = poliza.getAsegurado().getId();
		if (FiltroUtils.noEstaVacio(idAsegurado)) {
			mapa.put("ase.id", idAsegurado);
		}

		final String nifCif = poliza.getAsegurado().getNifcif();
		if (FiltroUtils.noEstaVacio(nifCif)) {
			mapa.put("ase.nifcif", nifCif);
		}

		final BigDecimal codEntidadAseg = poliza.getAsegurado().getEntidad().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidadAseg)) {
			mapa.put("ase.entidad.codentidad", codEntidadAseg);
		}

		final String discriminante = poliza.getAsegurado().getDiscriminante();
		if (FiltroUtils.noEstaVacio(discriminante)) {
			mapa.put("ase.discriminante", discriminante);
		}

		/*
		 * DATOS DE LA POLIZA
		 */

		final String modulo = poliza.getCodmodulo();
		if (FiltroUtils.noEstaVacio(modulo)) {
			mapa.put("codmodulo", modulo);
		}

		final Long idPoliza = poliza.getIdpoliza();
		if (FiltroUtils.noEstaVacio(idPoliza)) {
			mapa.put("idpoliza", idPoliza);
		}

		final BigDecimal idestado = poliza.getEstadoPoliza().getIdestado();
		if (FiltroUtils.noEstaVacio(idestado)) {
			mapa.put("estadoPoliza.idestado", idestado);
		}

		final String refPol = poliza.getReferencia();
		if (FiltroUtils.noEstaVacio(refPol)) {
			mapa.put("referencia", refPol);
		}
		// filtramos por clase
		final BigDecimal tipoClase = poliza.getClase();
		if (FiltroUtils.noEstaVacio(tipoClase)) {
			mapa.put("clase", tipoClase);
		}

		final Character tieneSiniestros = poliza.getTienesiniestros();
		if (FiltroUtils.noEstaVacio(tieneSiniestros))
			mapa.put("tienesiniestros", tieneSiniestros);

		final Character tieneanexomp = poliza.getTieneanexomp();
		if (FiltroUtils.noEstaVacio(tieneanexomp))
			mapa.put("tieneanexomp", tieneanexomp);

		final Character tieneanexorc = poliza.getTieneanexorc();
		if (FiltroUtils.noEstaVacio(tieneanexorc))
			mapa.put("tieneanexorc", tieneanexorc);

		// final Character esFinanciada=poliza.getEsFinanciada();
		// if(FiltroUtils.noEstaVacio(esFinanciada))mapa.put("esFinanciada",
		// esFinanciada);
		//
		// final Character esRyD =poliza.getEsRyD();
		// if(FiltroUtils.noEstaVacio(esRyD))mapa.put("esRyD", esRyD);

		// final Date fechavigor=poliza.getFechavigor();
		// if(FiltroUtils.noEstaVacio(fechavigor))mapa.put("fechavigor", fechavigor);
		
		return mapa;
	}

	@Override
	public Usuario aseguradoCargadoUsuario(Asegurado aseg, Long lineaseguroid, String usuario) throws DAOException {
		Session session = obtenerSession();
		Usuario usuarioP = new Usuario();
		try {
			Query query = session
					.createQuery("from Usuario u " + "where u.asegurado.id = :aseg "
							+ "and u.colectivo.linea.lineaseguroid = :linea " + "and u.codusuario != :usuario ")
					.setLong("aseg", aseg.getId()).setLong("linea", lineaseguroid).setString("usuario", usuario);

			usuarioP = (Usuario) query.uniqueResult();

			return usuarioP;

		} catch (Exception e) {
			throw new DAOException(ERROR_ACCESO_BD, e);
		}
	}

	@Override
	public List<BigDecimal> getIdsPolizaSiniestros() throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria c = session.createCriteria(Siniestro.class);
			c.setProjection(
					Projections.distinct(Projections.projectionList().add(Projections.property(POLIZA_ID_POLIZA))));
			c.add(Restrictions.isNotNull(POLIZA_ID_POLIZA));

			return c.list();

		} catch (Exception e) {
			throw new DAOException(ERROR_ACCESO_BD, e);
		}
	}

	@Override
	public List<BigDecimal> getIdsPolizaRedCapital() throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria c = session.createCriteria(ReduccionCapital.class);
			c.setProjection(
					Projections.distinct(Projections.projectionList().add(Projections.property(POLIZA_ID_POLIZA))));
			c.add(Restrictions.isNotNull(POLIZA_ID_POLIZA));

			return c.list();

		} catch (Exception e) {
			throw new DAOException(ERROR_ACCESO_BD, e);
		}

	}

	public List<BigDecimal> getIdsAnexoMod() throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria c = session.createCriteria(AnexoModificacion.class);
			c.setProjection(
					Projections.distinct(Projections.projectionList().add(Projections.property(POLIZA_ID_POLIZA))));
			c.add(Restrictions.isNotNull(POLIZA_ID_POLIZA));

			return c.list();

		} catch (Exception e) {
			throw new DAOException(ERROR_ACCESO_BD, e);
		}

	}

	@Override
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException {
		try {

			return (Comunicaciones) get(Comunicaciones.class, idEnvio);

		} catch (Exception e) {
			throw new DAOException(ERROR_ACCESO_BD, e);
		}
	}

	/**
	 * filtra las polizas con mismo lineaseguroid y asegurado pero distinta clase
	 */
	public List<Poliza> getVerificarPolizas(Poliza polizaBean) throws DAOException {
		logger.info("Init - getVerificarPolizas");
		List<Poliza> lstPolizas = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.add(Restrictions.eq("linea.lineaseguroid", polizaBean.getLinea().getLineaseguroid()));
			criteria.add(Restrictions.eq("asegurado.id", polizaBean.getAsegurado().getId()));
			criteria.add(Restrictions.eq("colectivo.id", polizaBean.getColectivo().getId()));
			criteria.add(Restrictions.ne("estadoPoliza.idestado", Constants.ESTADO_POLIZA_BAJA));
			// Deshabilitamos la restricion de que filtre las polizas por clase
			// DAA 18/04/2012
			// criteria.add(Restrictions.ne("clase", polizaBean.getClase()));
			lstPolizas = criteria.list();

		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de datos ", ex);
			throw new DAOException(ERROR_ACCESO_BBDD, ex);
		}
		logger.info("end - getVerificarPolizas");
		return lstPolizas;
	}

	public void saveParcela2(Parcela nPar) throws DAOException {
		Session session = obtenerSession();
		try {
			session.save(nPar);
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de  datos ", ex);
			throw new DAOException(ERROR_ACCESO_BBDD, ex);
		}
	}

	public void saveCapAsegurado(CapitalAsegurado nCap) throws DAOException {
		Session session = obtenerSession();
		try {
			session.save(nCap);
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la  base de datos ", ex);
			throw new DAOException(ERROR_ACCESO_BBDD, ex);
		}
	}

	public void saveDatoVarParcela(DatoVariableParcela nDat) throws DAOException {
		Session session = obtenerSession();
		try {
			session.save(nDat);
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a  la base de datos ", ex);
			throw new DAOException(ERROR_ACCESO_BBDD, ex);
		}
	}

	/**
	 * [inicio]FUNCIONES CAMBIO/BORRADO MASIVO
	 * 
	 * @throws SQLIntegrityConstraintViolationException,TransactionSystemException
	 */
	// TMR Facturacion. Le anadimos el parametro Usuario para la facturacion al
	// hacer un cambio masivo
	public void cambioMasivo(CambioMasivoVO cambioMasivoVO, Poliza poliza, Usuario usuario, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd, String recalcular,
			Map<Long, List<Long>> mapaParcelasInstalaciones, boolean guardaSoloPrecioYProd)
			throws DAOException, SQLIntegrityConstraintViolationException, TransactionSystemException {
		try {
			// modifico todas las parcelas checked (y sus instalaciones)
			boolean tieneEstructuras = false;
			// for(int i = 0;i < cambioMasivoVO.getListaParcelas().size();i++){
			List<Parcela> parcelasModificadas = new ArrayList<Parcela>();
			int i = 0;
			if (mapaParcelasInstalaciones != null && mapaParcelasInstalaciones.size() > 0)
				tieneEstructuras = true;
			for (Parcela parcela : poliza.getParcelas()) {
				if (cambioMasivoVO.getListaParcelas().contains(parcela.getIdparcela())) {
					parcelasModificadas.add(parcela);
					traceCambioMasivo(cambioMasivoVO, i);

					modificarParcelaCambioMasivo(cambioMasivoVO, i, parcela, cambioMasivoVO.getMaxProduccion(),
							cambioMasivoVO.getMinProduccion(), usuario, guardaSoloPrecioYProd);

					if (tieneEstructuras && mapaParcelasInstalaciones.containsKey(parcela.getIdparcela())) {
						List<Parcela> lstInstalaciones = new ArrayList<Parcela>();
						List<Long> lstIdsInstall = (List<Long>) mapaParcelasInstalaciones.get(parcela.getIdparcela());
						if (lstIdsInstall != null && lstIdsInstall.size() > 0) {
							lstInstalaciones = getInstalaciones(lstIdsInstall);
							modificarInstalacionesCambioMasivo(cambioMasivoVO, lstInstalaciones, usuario);
						}
					}

					i++;
				}
			}

			// recalculo precio produccion de todas las parcelas si el usuario ha pulsado
			// aceptar en el
			// popup de recalculo
			if (recalcular.equalsIgnoreCase("true")) {
				List<String> codsModuloPoliza = new ArrayList<String>();
				for (ComparativaPoliza comp : poliza.getComparativaPolizas()) {
					if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
						codsModuloPoliza.add(comp.getId().getCodmodulo());
				}

				reCalculoPrecioProduccion(parcelasModificadas, codsModuloPoliza, esConWS, mapaRendimientosProd);
			}
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			logger.error("Error de integridad ", e);
			throw new SQLIntegrityConstraintViolationException(
					"Error de integridad al actualizar los datos del cambio masivo", e);
		} catch (TransactionSystemException t) {
			logger.error("Clave principal no encontrada ", t);
			throw new TransactionSystemException("Clave pricipal no encontrada");
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso  a la base de datos ", ex);
			throw new DAOException(ERROR_ACCESO_BBDD, ex);
		}
	}

	/**
	 * BORRADO MASIVO
	 */
	public String borradoMasivo(String strCodsParcelaEliminar) throws DAOException {

		String resultado = "";
		try {

			String procedure = "PQ_BORRAR_PARCELAS.PR_BORRADO_MASIVO (P_IDSPARCELA IN VARCHAR2, P_RESULT OUT VARCHAR2)";
			Map<String, Object> inParameters = new HashMap<String, Object>();
			inParameters.put("P_IDSPARCELA", strCodsParcelaEliminar);
			inParameters.put(P_RESULT, resultado);

			logger.info("Llamada al procedimiento " + procedure);
			logger.info("Con parametros:");
			logger.info("   P_IDSPARCELA: " + strCodsParcelaEliminar);
			logger.info("   P_RESULT: " + resultado);

			Map<String, Object> res = this.databaseManager.executeStoreProc(procedure, inParameters);

			logger.debug("Termino la llamada al pl " + res.get(P_RESULT));
			return (String) res.get(P_RESULT);

		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el borrado masivo de parcelas ", ex);
			throw new DAOException("Se ha producido un error durante el borrado masivo de parcelas.", ex);
		}
	}

	/**
	 * DUPLICADO MASIVO PARCELAS
	 */
	public void duplicadoMasivo(String idParcela, Poliza poliza, Long cantDuplicar) throws DAOException {
		String resultado = "";

		try {
			String procedure = "PQ_DUPLICAR_PARCELA.PR_DUPLICAR_PARCELA (P_IDPARCELA IN VARCHAR2, P_IDPOLIZA IN VARCHAR2,P_VECES IN VARCHAR2, P_RESULT OUT VARCHAR2)";
			Map<String, Object> inParameters = new HashMap<String, Object>();
			inParameters.put("P_IDPARCELA", idParcela);
			inParameters.put("P_IDPOLIZA", poliza.getIdpoliza());
			inParameters.put("P_VECES", cantDuplicar.toString());
			inParameters.put(P_RESULT, resultado);

			logger.info("Llamada al procedimiento " + procedure);
			logger.info("Con parametros:");
			logger.info("   P_IDPARCELA: " + idParcela);
			logger.info("   P_IDPOLIZA: " + poliza.getIdpoliza());
			logger.info("   P_VECES: " + cantDuplicar.toString());
			logger.info("   P_RESULT: " + resultado);

			this.databaseManager.executeStoreProc(procedure, inParameters);
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el duplicado masivo de parcelas ", ex);
			throw new DAOException("Se ha producido un error durante el duplicado masivo de parcelas.", ex);
		}
	}

	/**
	 * modifica los datos de la parcela. Cambio masivo
	 */
	public void modificarParcelaCambioMasivo(CambioMasivoVO cambioMasivoVO, int i, Parcela parcela,
			BigDecimal maxProduccion, BigDecimal minProduccion, Usuario usuario, boolean guardaSoloPrecioYProd)
			throws DAOException, SQLIntegrityConstraintViolationException {

		try {
			if (!guardaSoloPrecioYProd) {
				// Ubicacion
				setUbicacion(cambioMasivoVO, parcela);

				// Sigpac
				setSigpac(cambioMasivoVO, parcela);

				// Cultivo y Variedad
				if (!"".equals(cambioMasivoVO.getCultivo_cm())) {
					parcela.setCodcultivo(new BigDecimal(cambioMasivoVO.getCultivo_cm()));
				}
				if (!"".equals(cambioMasivoVO.getVarieda_cm())) {
					parcela.setCodvariedad(new BigDecimal(cambioMasivoVO.getVarieda_cm()));
				}

				// superficie
				if (!"".equals(cambioMasivoVO.getSuperficie_cm())) {
					setSuperficie(cambioMasivoVO, parcela);
				}

				// Datos Variables
				// destino
				if (!"".equals(cambioMasivoVO.getDestino_cm())) {
					setDatoVariable(cambioMasivoVO.getDestino_cm(), parcela, ConstantsConceptos.CODCPTO_DESTINO);
				}
				// tipo plantacion
				if (!"".equals(cambioMasivoVO.getTplantacion())) {
					setDatoVariable(cambioMasivoVO.getTplantacion(), parcela, ConstantsConceptos.CODCPTO_TIPO_PLANTACION);
				}
				// sistema cultivo
				if (!"".equals(cambioMasivoVO.getSistcultivo())) {
					setDatoVariable(cambioMasivoVO.getSistcultivo(), parcela, ConstantsConceptos.CODCPTO_SISTCULTIVO);
				}
				// Tipo Marco plantacion
				if (!"".equals(cambioMasivoVO.getCodtipomarcoplantac_cm())) {
					setDatoVariable(cambioMasivoVO.getCodtipomarcoplantac_cm(), parcela,
							ConstantsConceptos.CODCPTO_TIPMARCOPLANT);
				}
				// practica cultural
				if (!"".equals(cambioMasivoVO.getCodpracticacultural_cm())) {
					setDatoVariable(cambioMasivoVO.getCodpracticacultural_cm(), parcela, ConstantsConceptos.CODCPTO_PRACTCULT);
				}
				// fechaSiembra
				if (!"".equals(cambioMasivoVO.getFechaSiembra())) {
					setDatoVariable(cambioMasivoVO.getFechaSiembra(), parcela, ConstantsConceptos.CODCPTO_FECSIEMBRA);
				}
				// fecha fin garantias
				if (!"".equals(cambioMasivoVO.getFechaFinGarantia_cm())) {
					setDatoVariable(cambioMasivoVO.getFechaFinGarantia_cm(), parcela, ConstantsConceptos.CODCPTO_FEC_FIN_GARANT);
				}
				// unidades
				if (!"".equals(cambioMasivoVO.getUnidades_cm())) {
					setDatoVariable(cambioMasivoVO.getUnidades_cm(), parcela, ConstantsConceptos.CODCPTO_NUMARBOLES);
				}
				// Sistema de Produccion
				if (!"".equals(cambioMasivoVO.getSistproduccion_cm())) {
					setDatoVariable(cambioMasivoVO.getSistproduccion_cm(), parcela, ConstantsConceptos.CODCPTO_SISTEMA_PRODUCCION);
				}

				// edad
				if (!"".equals(cambioMasivoVO.getEdad_cm())) {
					setEdad(cambioMasivoVO, parcela, false);
				}
				// incremento edad
				if (!"".equals(cambioMasivoVO.getIncEdad_cm())) {
					setEdad(cambioMasivoVO, parcela, true);
				}

				evict(parcela);
				// TMR.Facturacion. Por cada cambio que se haga en una parcela facturamos.
				logger.debug("TAMARA Y ANGEL 4: ID PArcela a grabar= " + parcela.getIdparcela());
				saveOrUpdateFacturacion(parcela, usuario);
				// Precio y superficie
				if (!"".equals(cambioMasivoVO.getPrecio_cm())) {
					setPrecio(cambioMasivoVO, parcela);
				}
				// set incremento unidades
				if (!"".equals(cambioMasivoVO.getInc_unidades_cm())) {
					setIncrementoUnidades(cambioMasivoVO, parcela);
				}
				// set incremento ha
				if (!"".equals(cambioMasivoVO.getIncrene_ha_cm())) {
					setIncrementoHa(cambioMasivoVO, parcela);
				}
				// set incremento parcela
				if (!"".equals(cambioMasivoVO.getIncreme_parcela_cm())) {
					setIncrementoParcela(cambioMasivoVO, parcela);
				}
			} else {
				// Precio y superficie
				if (!"".equals(cambioMasivoVO.getPrecio_cm())) {
					setPrecio(cambioMasivoVO, parcela);
				}
				// set incremento unidades
				if (!"".equals(cambioMasivoVO.getInc_unidades_cm())) {
					setIncrementoUnidades(cambioMasivoVO, parcela);
				}
				// set incremento ha
				if (!"".equals(cambioMasivoVO.getIncrene_ha_cm())) {
					setIncrementoHa(cambioMasivoVO, parcela);
				}
				// set incremento parcela
				if (!"".equals(cambioMasivoVO.getIncreme_parcela_cm())) {
					setIncrementoParcela(cambioMasivoVO, parcela);
				}
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el  acceso a la base de datos ", ex);
			throw new DAOException("[ERROR] al modificar la parcela.", ex);
		}
	}

	/**
	 * set incremento unidades Se asigna a los capitales asegurados de cada parcela
	 * la produccion indicada multiplicada por su superficie
	 */
	private void setIncrementoUnidades(CambioMasivoVO cambioMasivoVO, Parcela parcela) {
		Session session = obtenerSession();
		BigDecimal incremento = new BigDecimal(0);
		BigDecimal codConceptoProduccion = new BigDecimal(
				ResourceBundle.getBundle("agp").getString(COD_CONCEPTO_REND));
		Set<CapitalAsegurado> listCapitalesAsegurados = parcela.getCapitalAsegurados();// getListCapitalesAsegurados(parcela.getIdparcela());

		// bucle capital asegurado
		for (CapitalAsegurado capitalAsegurado : listCapitalesAsegurados) {
			logger.debug(capitalAsegurado.getTipoCapital().getCodtipocapital());

			if (capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
				// bucle modulos
				Set<CapAsegRelModulo> listModulos = capitalAsegurado.getCapAsegRelModulos();
				for (CapAsegRelModulo capAsegRelModulo : listModulos) {

					boolean enco = false;
					if (!cambioMasivoVO.getUnidades_cm().equals("")) {
						incremento = new BigDecimal(cambioMasivoVO.getUnidades_cm())
								.multiply(new BigDecimal(cambioMasivoVO.getInc_unidades_cm()));
						enco = true;
					} else {

						for (DatoVariableParcela datos : capitalAsegurado.getDatoVariableParcelas()) {

							if (datos.getDiccionarioDatos().getCodconcepto()
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_NUMARBOLES))) {
								incremento = new BigDecimal(datos.getValor())
										.multiply(new BigDecimal(cambioMasivoVO.getInc_unidades_cm()));

								enco = true;
							}
						}
					}
					if (!enco) {
						incremento = new BigDecimal(0);
					}

					BigDecimal newProduccion = incremento;

					// Si la nueva produccion es < 0 en base de datos guardamos 0
					if (newProduccion.compareTo(new BigDecimal(0)) < 0)
						capAsegRelModulo.setProduccion(new BigDecimal(0));
					else
						capAsegRelModulo.setProduccion(newProduccion);

					evict(capAsegRelModulo);
					session.saveOrUpdate(capAsegRelModulo);
				}
			}
		}
	}

	/**
	 * Actualiza o inserta el dato variable del cambio masivo para las parcelas
	 * seleccionadas
	 * 
	 * @param valor
	 * @param parcela
	 * @param destinoConcepto
	 * @throws DAOException
	 */

	private void setDatoVariable(String valor, Parcela parcela, int destinoConcepto) throws DAOException {
		boolean enco = false;

		List<CapitalAsegurado> listCapitalesAsegurados = getListCapitalesAsegurados(parcela.getIdparcela());

		for (CapitalAsegurado capital : listCapitalesAsegurados) {
			List<DatoVariableParcela> datos = getObjects(DatoVariableParcela.class,
					"capitalAsegurado.idcapitalasegurado", capital.getIdcapitalasegurado());
			for (int i = 0; i < datos.size(); i++) {
				if (datos.get(i).getDiccionarioDatos().getCodconcepto().equals(BigDecimal.valueOf(destinoConcepto))) {
					enco = true;
					datos.get(i).setValor(valor);
				}
			}
			if (!enco) {
				DatoVariableParcela dvp = new DatoVariableParcela();
				dvp.setCapitalAsegurado(capital);
				dvp.setValor(valor);

				DiccionarioDatos dd = new DiccionarioDatos();
				dd.setCodconcepto(BigDecimal.valueOf(destinoConcepto));
				dvp.setDiccionarioDatos(dd);

				capital.getDatoVariableParcelas().add(dvp);
				saveOrUpdate(dvp);
			}
		}
	}

	private void setEdad(CambioMasivoVO cambioMasivoVO, Parcela parcela, boolean incrementoEdad) throws DAOException {
		boolean enco = false;

		List<CapitalAsegurado> listCapitalesAsegurados = getListCapitalesAsegurados(parcela.getIdparcela());

		for (CapitalAsegurado capital : listCapitalesAsegurados) {
			List<DatoVariableParcela> datos = getObjects(DatoVariableParcela.class,
					"capitalAsegurado.idcapitalasegurado", capital.getIdcapitalasegurado());
			for (int i = 0; i < datos.size(); i++) {
				if (datos.get(i).getDiccionarioDatos().getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_EDAD))) {
					enco = true;
					if (incrementoEdad) {
						Integer sum = Integer.parseInt(datos.get(i).getValor())
								+ Integer.parseInt(cambioMasivoVO.getIncEdad_cm());
						datos.get(i).setValor(sum.toString());
					} else
						datos.get(i).setValor(cambioMasivoVO.getEdad_cm());
				}
			}

			if (!enco) {
				DatoVariableParcela dvp = new DatoVariableParcela();
				dvp.setCapitalAsegurado(capital);
				if (incrementoEdad)
					dvp.setValor(cambioMasivoVO.getIncEdad_cm());
				else
					dvp.setValor(cambioMasivoVO.getEdad_cm());
				DiccionarioDatos dd = new DiccionarioDatos();
				dd.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_EDAD));
				dvp.setDiccionarioDatos(dd);

				capital.getDatoVariableParcelas().add(dvp);
				saveOrUpdate(dvp);
			}
		}
	}

	private void setUbicacion(CambioMasivoVO cambioMasivoVO, Parcela parcela) {

		if (!cambioMasivoVO.getProvincia_cm().equals("")) {
			parcela.getTermino().getId().setCodprovincia(new BigDecimal(cambioMasivoVO.getProvincia_cm()));
		}
		if (!cambioMasivoVO.getComarca_cm().equals("")) {
			parcela.getTermino().getComarca().getId().setCodcomarca(new BigDecimal(cambioMasivoVO.getComarca_cm()));
			parcela.getTermino().getId().setCodprovincia(new BigDecimal(cambioMasivoVO.getProvincia_cm()));
		}
		if (!cambioMasivoVO.getTermino_cm().equals("")) {
			parcela.getTermino().getId().setCodtermino(new BigDecimal(cambioMasivoVO.getTermino_cm()));
			parcela.getTermino().getId().setCodcomarca(new BigDecimal(cambioMasivoVO.getComarca_cm()));
			parcela.getTermino().getId().setCodprovincia(new BigDecimal(cambioMasivoVO.getProvincia_cm()));

		}
		if (!cambioMasivoVO.getSubtermino_cm().equals("")) {
			parcela.getTermino().getId().setSubtermino(cambioMasivoVO.getSubtermino_cm().charAt(0));
			parcela.getTermino().getId().setCodtermino(new BigDecimal(cambioMasivoVO.getTermino_cm()));
			parcela.getTermino().getId().setCodcomarca(new BigDecimal(cambioMasivoVO.getComarca_cm()));
			parcela.getTermino().getId().setCodprovincia(new BigDecimal(cambioMasivoVO.getProvincia_cm()));
		}

	}

	private void setSigpac(CambioMasivoVO cambioMasivoVO, Parcela parcela) {

		if (!cambioMasivoVO.getProvSig_cm().equals("")) {
			parcela.setCodprovsigpac(new BigDecimal(cambioMasivoVO.getProvSig_cm()));
		}
		if (!cambioMasivoVO.getTermSig_cm().equals("")) {
			parcela.setCodtermsigpac(new BigDecimal(cambioMasivoVO.getTermSig_cm()));
		}
		if (!cambioMasivoVO.getAgrSig_cm().equals("")) {
			parcela.setAgrsigpac(new BigDecimal(cambioMasivoVO.getAgrSig_cm()));
		}
		if (!cambioMasivoVO.getZonaSig_cm().equals("")) {
			parcela.setZonasigpac(new BigDecimal(cambioMasivoVO.getZonaSig_cm()));
		}
		if (!cambioMasivoVO.getPolSig_cm().equals("")) {
			parcela.setPoligonosigpac(new BigDecimal(cambioMasivoVO.getPolSig_cm()));
		}
		if (!cambioMasivoVO.getParcSig_cm().equals("")) {
			parcela.setParcelasigpac(new BigDecimal(cambioMasivoVO.getParcSig_cm()));
		}
		if (!cambioMasivoVO.getRecSig_cm().equals("")) {
			parcela.setRecintosigpac(new BigDecimal(cambioMasivoVO.getRecSig_cm()));
		}
	}
	
    /**
     * Método para realizar el recalculo de precio y produccion.
     * @param parcelas Lista de parcelas a actualizar precio y produccion
     * @param codsModuloPoliza Codigos de modulo seleccionados para la poliza
     */
    public void reCalculoPrecioProduccion(Collection<Parcela> parcelas, List<String> codsModuloPoliza, boolean esConWS, Map<String, ProduccionVO> mapaRendimientosProd) throws Exception{
    	
    	Map<String, BigDecimal> rdtosGrp = new HashMap<String, BigDecimal>();
    	Map<String, BigDecimal> preciosGrp = new HashMap<String, BigDecimal>();
    	Map<BigDecimal, BigDecimal> mapLineas = new HashMap<BigDecimal, BigDecimal>();
    	boolean addIdentificador = true;
    	boolean aplica = false;
    	
        BigDecimal codConceptoProduccion = new BigDecimal(ResourceBundle.getBundle("agp").getString(COD_CONCEPTO_REND));
        
        
        //TMR 15/01/2014
        mapLineas = getLineasRecalculo();
        //DAA 11/12/2013 recupero el lineaseguroid para despues verificar si aplica sistema de cultivo a rendimiento
        if (parcelas != null && !parcelas.isEmpty()) {
	        Parcela parcela = parcelas.iterator().next();
	        aplica = aplicaSistemaCultivoARendimiento(parcela.getPoliza().getLinea().getLineaseguroid());
	        if (mapLineas.containsKey(parcela.getPoliza().getLinea().getCodlinea())) {
				addIdentificador = false;
			}
		}

		// -- PARCELAS --
		for (Parcela par : parcelas) {
			// Para evitar OutOfMemory al cargar las parcelas de la pac cuando no hay
			// cultivo o variedad
			if (!par.getCodcultivo().equals(new BigDecimal(999)) && !par.getCodvariedad().equals(new BigDecimal(999))) {
				ParcelaVO parcelaVO = ParcelaUtil.getParcelaVO(par, datosParcelaDao);
				Set<CapitalAsegurado> listCapitalesAsegurados = par.getCapitalAsegurados();
				int indiceCapital = 0;

				// -- CAPITALES ASEG. --
				for (CapitalAsegurado capitalAsegurado : listCapitalesAsegurados) {
					// DAA 11/12/2013
					String valor = "";
					if (aplica) {
						for (DatoVariableParcela datoVariable : capitalAsegurado.getDatoVariableParcelas()) {
							if ((datoVariable.getDiccionarioDatos().getCodconcepto())
									.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
								valor = "#" + datoVariable.getValor().toString();
								break;
							}
						}
					}
					// -- MODULOS --
					for (String modulo : codsModuloPoliza) {
						String identificador = modulo + "#" + par.getCodcultivo() + "#" + par.getCodvariedad() + "#"
								+ par.getTermino().getId().getCodprovincia() + "#"
								+ par.getTermino().getId().getCodcomarca() + "#"
								+ par.getTermino().getId().getCodtermino() + "#"
								+ par.getTermino().getId().getSubtermino() + "#"
								+ capitalAsegurado.getTipoCapital().getCodtipocapital() + valor;

						boolean existeModulo = false;
						// por cada cap_aseg_rel_modulo
						for (CapAsegRelModulo carm : capitalAsegurado.getCapAsegRelModulos()) {
							if (carm.getCodmodulo().equals(modulo)) {
								existeModulo = true;
								setPrecioProduccionCapAsegRelModulo(rdtosGrp, preciosGrp, codConceptoProduccion, par,
										parcelaVO, indiceCapital, capitalAsegurado, identificador, modulo, carm,
										addIdentificador, esConWS, mapaRendimientosProd);
							}

						}
						if (!existeModulo) {
							CapAsegRelModulo carm = new CapAsegRelModulo();
							carm.setCodmodulo(modulo);
							carm.setCapitalAsegurado(capitalAsegurado);

							setPrecioProduccionCapAsegRelModulo(rdtosGrp, preciosGrp, codConceptoProduccion, par,
									parcelaVO, indiceCapital, capitalAsegurado, identificador, modulo, carm,
									addIdentificador, esConWS, mapaRendimientosProd);

							capitalAsegurado.getCapAsegRelModulos().add(carm);
						}

					} // end for modulos

					// puede darse el caso de un modulo sin produccion, solo hacerlo si hay + de un
					// modulo
					if (codsModuloPoliza.size() > 1) {
						asignarProduccionesACero(capitalAsegurado);
					}

					indiceCapital++;
				} // end for capitales aseg.
			}
		} // end for parcelas
	}

	/**
	 * DAA 11/12/2013 Metodo para comprobar si se aplica el sistema de cultivo al
	 * sistema de rendimiento para un plan/linea
	 * 
	 * @param lineaseguroid
	 * @return aplica
	 * @throws DAOException
	 */
	public boolean aplicaSistemaCultivoARendimiento(Long lineaseguroid) {
		Session session = obtenerSession();
		try {
			String sql = "select * from o02agpe0.tb_sc_oi_org_info o where o.lineaseguroid = " + lineaseguroid
					+ " and o.coduso = " + Constants.USO_RENDIMIENTOS + " and o.codubicacion = "
					+ OrganizadorInfoConstants.UBICACION_PARCELA_DV + " and o.codconcepto = "
					+ ConstantsConceptos.CODCPTO_SISTCULTIVO;
			List<?> list = session.createSQLQuery(sql).list();
			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception excepcion) {
			logger.error("Error al comprobar en el Organizador si aplica Sistema de Cultivo a Rendimiento ", excepcion);
			return false;
		}
	}

	/**
	 * Puede darse el caso que no se quede con la produccion(rendimiento) mayor de
	 * los modulos, según el orden en el que vengan esos modulos, los recorro todos
	 * buscando el mayor y asignandoselo, este cambio se persiste en
	 * TB_CAP_ASEG_REL_MODULO Solo se asigna el mayor si para ese modulo la
	 * produccion es libre(0 o vacio)
	 */
	private void asignarProduccionesACero(CapitalAsegurado capitalAsegurado) {
		BigDecimal produccionMayor = new BigDecimal(0);

		// selecciono la produccion mayor de todos los modulos
		for (CapAsegRelModulo carm : capitalAsegurado.getCapAsegRelModulos()) {
			if (produccionMayor.compareTo(carm.getProduccion()) == -1) {
				produccionMayor = carm.getProduccion();
			}
		}
		// los recorro todos buscando alguno a 0
		for (CapAsegRelModulo carm : capitalAsegurado.getCapAsegRelModulos()) {
			if (carm.getProduccion().toString().equals("0") || carm.getProduccion().toString().equals("")) {
				carm.setProduccion(produccionMayor);
			}
		}
	}

	/**
	 * Metodo para hacer el recalculo de precio y produccion para un modulo en
	 * concreto.
	 * 
	 * @param rdtosGrp
	 *            Mapa con los rendimientos por grupo que ya hay calculados de antes
	 * @param preciosGrp
	 *            Mapa con los precios por grupo que ya hay calculados de antes
	 * @param codConceptoProduccion
	 *            Codigo de concepto para el cual no se calcula la produccion
	 * @param par
	 *            Parcela
	 * @param parcelaVO
	 *            Parcela de pantalla
	 * @param indiceCapital
	 *            Indice del capital asegurado para el que estamos calculando precio
	 *            y produccion
	 * @param capitalAsegurado
	 *            Capital asegurado
	 * @param identificador
	 *            Identificador para los grupos de rendimiento y precio
	 * @param modulo
	 *            Modulo para el que estamos calculando
	 * @param carm
	 *            Objeto CapAsegRelModulo a actualizar
	 * @param addIdentificador
	 * @throws Exception
	 * @throws BusinessException
	 */
	private void setPrecioProduccionCapAsegRelModulo(Map<String, BigDecimal> rdtosGrp,
			Map<String, BigDecimal> preciosGrp, BigDecimal codConceptoProduccion, Parcela par, ParcelaVO parcelaVO,
			int indiceCapital, CapitalAsegurado capitalAsegurado, String identificador, String modulo,
			CapAsegRelModulo carm, boolean addIdentificador, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd) throws Exception, BusinessException {

		List<PrecioVO> precios;
		BigDecimal auxProduccion = new BigDecimal(0);
		BigDecimal nuevaProduccion;
		BigDecimal nuevoRdto;
		BigDecimal nuevoPrecio;
		String[] producciones = new String[2];

		ProduccionVO prodVO = null;
		String clave = modulo + "-" + par.getHoja() + "-" + par.getNumero() + "-"
				+ capitalAsegurado.getTipoCapital().getCodtipocapital();

		if (!rdtosGrp.containsKey(identificador)
				&& capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
			PreciosProduccionesVO precProd = calculoPrecioProduccionManager.getProduccionPrecio(parcelaVO,
					indiceCapital, codConceptoProduccion, esConWS);

			if (esConWS) {

				List<ProduccionVO> listaProdVO = new ArrayList<ProduccionVO>();
				prodVO = mapaRendimientosProd.get(clave);

				if (prodVO == null) {
					prodVO = new ProduccionVO();
					prodVO.setCodModulo(modulo);
					prodVO.setLimMin("0");
					prodVO.setLimMax("");
					prodVO.setRdtoMin("0");
					prodVO.setRdtoMax("");
					prodVO.setProduccion("");
				} else {

					// Asignamos los valores redondeando los decimales
					String limMin = prodVO.getLimMin();
					prodVO.setLimMin(
							new BigDecimal(limMin.replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR).toString());

					String limMax = prodVO.getLimMax();
					if (!StringUtils.nullToString(limMax).equals(""))
						prodVO.setLimMax(new BigDecimal(limMax.replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR)
								.toString());
					else
						prodVO.setLimMax("");

					String rdtoMin = prodVO.getRdtoMin();
					prodVO.setRdtoMin(new BigDecimal(rdtoMin.replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR)
							.toString());

					String rdtoMax = prodVO.getRdtoMax();
					if (!StringUtils.nullToString(rdtoMax.trim()).equals(""))
						prodVO.setRdtoMax(new BigDecimal(rdtoMax.replace(",", "."))
								.setScale(0, BigDecimal.ROUND_FLOOR).toString());
					else
						prodVO.setRdtoMax("");
				}

				listaProdVO.add(prodVO);
				precProd.setListProducciones(listaProdVO);
			}

			precios = precProd.getListPrecios();

			int i = 0;
			for (ProduccionVO pvo : precProd.getListProducciones()) {
				if (pvo.getCodModulo().equals(modulo)) {
					break;
				}
				i++;
			}

			producciones[0] = precProd.getListProducciones().get(i).getRdtoMin().replace(",", ".");
			producciones[1] = precProd.getListProducciones().get(i).getRdtoMax().replace(",", ".");

			nuevoRdto = getProduccionAGuardar(producciones, carm);
			nuevoPrecio = getPrecioAGuardar(precios, i, carm);

			if (addIdentificador) {
				rdtosGrp.put(identificador, nuevoRdto);
				preciosGrp.put(identificador, nuevoPrecio);
			} else {
				// ASF - 317 - Para el caso de que en la linea se busquen producciones para
				// todas las parcelas, me quedo tambien con la producción.
				try {
					auxProduccion = new BigDecimal(
							precProd.getListProducciones().get(i).getLimMax().replace(",", "."));
				} catch (Exception e) {
					auxProduccion = new BigDecimal(0);
				}
			}
		} else if (!rdtosGrp.containsKey(identificador)
				&& !capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
			nuevoRdto = new BigDecimal(0);
			nuevoPrecio = new BigDecimal(
					calculoPrecioProduccionManager.getPrecio(par.getPoliza().getLinea().getLineaseguroid(),
							par.getPoliza().getIdpoliza(), par, modulo, indiceCapital));

			if (addIdentificador) {
				rdtosGrp.put(identificador, nuevoRdto);
				preciosGrp.put(identificador, nuevoPrecio);
			}
		} else {
			// cogemos precio y produccion de los mapas por cultivo, variedad y ubicacion
			nuevoRdto = rdtosGrp.get(identificador);
			nuevoPrecio = preciosGrp.get(identificador);
		}

		// Se obtiene el campo aplicacion del rendimiento correspondiente al capital
		// asegurado
		String apprdto = getAplRdto(capitalAsegurado, modulo);
		logger.debug("setPrecioProduccionCapAsegRelModulo - Aplicacion del rendimiento obtenida: '" + apprdto + "'");

		BigDecimal superficie_unidades = new BigDecimal(0);
		if (par.getPoliza().getLinea().getCodlinea().equals(new BigDecimal(314))
				&& ("U".equals(apprdto) || "P".equals(apprdto) || "".equals(apprdto))) {
			// por unidades
			for (DatoVariableParcela dvp : capitalAsegurado.getDatoVariableParcelas()) {
				if (dvp.getDiccionarioDatos().getCodconcepto().equals(new BigDecimal(117))) {
					superficie_unidades = new BigDecimal(dvp.getValor());
					break;
				}
			}
		} else {
			// por superficie
			if (capitalAsegurado.getSuperficie() != null) {
				superficie_unidades = capitalAsegurado.getSuperficie();
			}
		}

		if (!addIdentificador) {
			// ASF - 317
			nuevaProduccion = auxProduccion.setScale(0, BigDecimal.ROUND_FLOOR);
		} else {
			nuevaProduccion = nuevoRdto.multiply(superficie_unidades).setScale(0, BigDecimal.ROUND_FLOOR);
		}

		if (esConWS) {
			try {
				if (prodVO == null) {
					prodVO = mapaRendimientosProd.get(clave);
				}
				nuevaProduccion = new BigDecimal(prodVO.getProduccion()).setScale(0, BigDecimal.ROUND_FLOOR);
			} catch (Exception e) {
				nuevaProduccion = new BigDecimal(0);

			}
		}

		// establecemos los valores
		carm.setProduccion(nuevaProduccion);
		carm.setPrecio(nuevoPrecio);
		if (nuevaProduccion.equals(new BigDecimal(0))) {
			carm.setTipoRdto(Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO);
		} else {
			carm.setTipoRdto(Constants.TIPO_RDTO_MAXIMO);
		}

	}

	/**
	 * Obtiene el campo "Aplicacion del rendimiento" para un capital asegurado y un
	 * modulo
	 * 
	 * @param ca
	 * @param codModulo
	 * @return
	 */
	public String getAplRdto(CapitalAsegurado ca, String codModulo) {

		logger.debug("SeleccionPolizaDao.getAplRdto - Inicio");

		// Carga los datos variables que se usaran para filtrar en la consulta
		Map<BigDecimal, String> mapa = new HashMap<BigDecimal, String>();
		// Sistema de cultivo
		mapa.put(new BigDecimal(123), null);
		// Practica cultural
		mapa.put(new BigDecimal(133), null);
		// Tipo de plantacion
		mapa.put(new BigDecimal(173), null);
		// Nº de anios desde poda
		mapa.put(new BigDecimal(617), null);
		// Edad
		mapa.put(new BigDecimal(111), null);
		// Tipo Marco Plantación
		mapa.put(new BigDecimal(116), null);

		for (DatoVariableParcela dvp : ca.getDatoVariableParcelas()) {
			if (mapa.containsKey(dvp.getDiccionarioDatos().getCodconcepto())) {
				mapa.put(dvp.getDiccionarioDatos().getCodconcepto(), dvp.getValor());
			}
		}

		String select = " select apprdto from tb_sc_c_limites_rdtos ";

		// Monta el where con los datos identificativos de la parcela
		String where = " WHERE lineaseguroid =" + ca.getParcela().getPoliza().getLinea().getLineaseguroid();
		where += " AND codmodulo = '" + codModulo + "'";
		where += " AND codcultivo in (" + ca.getParcela().getCodcultivo() + ",999)";
		where += " AND codvariedad in (" + ca.getParcela().getCodvariedad() + ",999)";
		where += " AND codprovincia in (" + ca.getParcela().getTermino().getId().getCodprovincia() + ",99)";
		where += " AND codcomarca in (" + ca.getParcela().getTermino().getId().getCodcomarca() + ",99)";
		where += " AND codtermino in (" + ca.getParcela().getTermino().getId().getCodtermino() + ",999)";
		where += " AND subtermino in ('" + ca.getParcela().getTermino().getId().getSubtermino() + "','9') ";

		// Monta el where con los datos variables que vengan informados
		where += (mapa.get(new BigDecimal(123)) != null) ? (" AND codsistemacultivo = " + mapa.get(new BigDecimal(123)))
				: ("");
		where += (mapa.get(new BigDecimal(133)) != null)
				? (" AND codpracticacultural = " + mapa.get(new BigDecimal(133)))
				: ("");
		where += (mapa.get(new BigDecimal(173)) != null) ? (" AND codtipoplantacion = " + mapa.get(new BigDecimal(173)))
				: ("");
		where += (mapa.get(new BigDecimal(617)) != null) ? (" AND numaniospoda = " + mapa.get(new BigDecimal(617)))
				: ("");
		where += (mapa.get(new BigDecimal(111)) != null) ? (" AND edaddesde <= " + mapa.get(new BigDecimal(111))
				+ " AND edadhasta >= " + mapa.get(new BigDecimal(111))) : ("");
		where += (mapa.get(new BigDecimal(116)) != null)
				? (" AND CODTIPOMARCOPLANTAC = " + mapa.get(new BigDecimal(116)))
				: ("");

		String order = "order by codcultivo asc, codvariedad  asc, codprovincia asc, codtermino   asc, subtermino   desc";

		Session session = obtenerSession();

		String sql = select + where + order;

		logger.debug("Consulta: " + sql);

		List<?> list = session.createSQLQuery(sql).list();

		return ((list.size() > 0) ? ((String) list.get(0)).toString() : (""));

	}

	private BigDecimal getProduccionAGuardar(String[] producciones, CapAsegRelModulo capAsegRelModulo) {
		BigDecimal nuevaProduccion = new BigDecimal(0);
		if (!"".equals(producciones[1].trim())) {
			// Tengo limite superior
			nuevaProduccion = new BigDecimal(producciones[1]);
		} else if ("".equals(producciones[1].trim()) && !"".equals(producciones[0].trim())) {
			// 1. Sin limite superior y con limite inferior
			// MPM - 27/08/12
			// Si el rendimiento es libre, la produccion es 0
			nuevaProduccion = new BigDecimal(0);
		} else if ("0".equals(producciones[0].trim()) && "0".equals(producciones[1].trim())
				&& capAsegRelModulo.getProduccion() == null) {
			// 2. no asegurable (max y min = 0) y NO lo tengo de antes
			nuevaProduccion = new BigDecimal("0");
		} else if ("0".equals(producciones[0].trim()) && "0".equals(producciones[1].trim())
				&& capAsegRelModulo.getProduccion() != null) {
			// 2. no asegurable (max y min = 0) y SI lo tengo de antes
			nuevaProduccion = capAsegRelModulo.getProduccion();
		} else if (producciones[0].equals(producciones[1])) {
			// 3.fijo (los dos iguales)
			nuevaProduccion = new BigDecimal(producciones[1]);
		} else if (producciones.length == 0 && capAsegRelModulo.getProduccion() != null) {
			// 4. array sin datos
			nuevaProduccion = capAsegRelModulo.getProduccion();
		} else if ("".equals(producciones[0].trim()) && "".equals(producciones[0].trim())
				&& capAsegRelModulo.getProduccion() != null) {
			// 5. array con las dos ""
			nuevaProduccion = capAsegRelModulo.getProduccion();
		} else {
			nuevaProduccion = new BigDecimal(0);
		}

		return nuevaProduccion;
	}

	private BigDecimal getPrecioAGuardar(List<PrecioVO> precios, int indice, CapAsegRelModulo capAsegRelModulo) {
		BigDecimal nuevoPrecio = new BigDecimal(0);

		if (precios.size() > 0 && indice < precios.size()) {
			PrecioVO precio = precios.get(indice);
			// Si el limite superior es igual al inferior es que el precio es fijo y si no,
			// nos quedamos
			// con el maximo, por lo que no es necesario hacer distingcion => nos quedaremos
			// siempre con el maximo.
			nuevoPrecio = new BigDecimal(precio.getLimMax());
		} else {
			nuevoPrecio = new BigDecimal(0);
		}

		return nuevoPrecio;
	}

	/**
	 * set incremento ha Se asigna a los capitales asegurados de cada parcela la
	 * produccion indicada multiplicada por su superficie
	 */
	private void setIncrementoHa(CambioMasivoVO cambioMasivoVO, Parcela parcela) {
		Session session = obtenerSession();
		BigDecimal codConceptoProduccion = new BigDecimal(
				ResourceBundle.getBundle("agp").getString(COD_CONCEPTO_REND));
		Set<CapitalAsegurado> listCapitalesAsegurados = parcela.getCapitalAsegurados();// getListCapitalesAsegurados(parcela.getIdparcela());

		// bucle capital asegurado
		for (CapitalAsegurado capitalAsegurado : listCapitalesAsegurados) {
			logger.debug(capitalAsegurado.getTipoCapital().getCodtipocapital());
			if (capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
				// bucle modulos
				Set<CapAsegRelModulo> listModulos = capitalAsegurado.getCapAsegRelModulos();// getModulosCapitaqlAsegurado(capitalAsegurado.getIdcapitalasegurado());
				for (CapAsegRelModulo capAsegRelModulo : listModulos) {
					BigDecimal newProduccion = capitalAsegurado.getSuperficie()
							.multiply(new BigDecimal(cambioMasivoVO.getIncrene_ha_cm()));
					/*
					 * BigDecimal incremento = capitalAsegurado.getSuperficie().multiply(new
					 * BigDecimal(cambioMasivoVO.getIncrene_ha_cm())); BigDecimal newProduccion =
					 * incremento.add(capAsegRelModulo.getProduccion());
					 */

					// Si la nueva produccion es < 0 en base de datos guardamos 0
					if (newProduccion.compareTo(new BigDecimal(0)) < 0)
						capAsegRelModulo.setProduccion(new BigDecimal(0));
					else
						capAsegRelModulo.setProduccion(newProduccion);

					evict(capAsegRelModulo);
					session.saveOrUpdate(capAsegRelModulo);
				}
			}
		}
	}

	/**
	 * set incremento parcela Se asigna a los capitales asegurados de cada parcela
	 * la produccion indicada en el campo
	 */
	private void setIncrementoParcela(CambioMasivoVO cambioMasivoVO, Parcela parcela) {
		Session session = obtenerSession();
		BigDecimal codConceptoProduccion = new BigDecimal(
				ResourceBundle.getBundle("agp").getString(COD_CONCEPTO_REND));
		Set<CapitalAsegurado> listCapitalesAsegurados = parcela.getCapitalAsegurados();// getListCapitalesAsegurados(parcela.getIdparcela());

		for (CapitalAsegurado capitalAsegurado : listCapitalesAsegurados) {
			// bucle modulos
			if (capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)) {
				Set<CapAsegRelModulo> listModulos = capitalAsegurado.getCapAsegRelModulos();// getModulosCapitaqlAsegurado(capitalAsegurado.getIdcapitalasegurado());
				for (CapAsegRelModulo capAsegRelModulo : listModulos) {
					BigDecimal newProduccion = new BigDecimal(cambioMasivoVO.getIncreme_parcela_cm());
					/*
					 * BigDecimal incremento = new
					 * BigDecimal(cambioMasivoVO.getIncreme_parcela_cm()); BigDecimal newProduccion
					 * = incremento.add(capAsegRelModulo.getProduccion());
					 */

					// Si la nueva produccion es < 0 en base de datos guardamos 0
					if (newProduccion.compareTo(new BigDecimal(0)) < 0)
						capAsegRelModulo.setProduccion(new BigDecimal(0));
					else
						capAsegRelModulo.setProduccion(newProduccion);

					evict(capAsegRelModulo);
					session.saveOrUpdate(capAsegRelModulo);
				}
			}
		}
	}

	/**
	 * Set precio en todos los capitales asegurados
	 */
	private void setPrecio(CambioMasivoVO cambioMasivoVO, Parcela parcela) {
		Session session = obtenerSession();

		List<CapitalAsegurado> listCapitalesAsegurados = getListCapitalesAsegurados(parcela.getIdparcela());

		for (int e = 0; e < listCapitalesAsegurados.size(); e++) {
			CapitalAsegurado capitalAsegurado = listCapitalesAsegurados.get(e);
			capitalAsegurado.setPrecio(new BigDecimal(cambioMasivoVO.getPrecio_cm()));

			Set<CapAsegRelModulo> listModulos = capitalAsegurado.getCapAsegRelModulos();// getModulosCapitaqlAsegurado(capitalAsegurado.getIdcapitalasegurado());
			for (CapAsegRelModulo capAsegRelModulo : listModulos) {
				capAsegRelModulo.setPrecio(new BigDecimal(cambioMasivoVO.getPrecio_cm()));

				evict(capAsegRelModulo);
				session.saveOrUpdate(capAsegRelModulo);
			}

		}
	}

	/**
	 * Set superficie en todos los capitales asegurados
	 */
	private void setSuperficie(CambioMasivoVO cambioMasivoVO, Parcela parcela) {
		List<CapitalAsegurado> listCapitalesAsegurados = getListCapitalesAsegurados(parcela.getIdparcela());

		for (int e = 0; e < listCapitalesAsegurados.size(); e++) {
			CapitalAsegurado capitalAsegurado = listCapitalesAsegurados.get(e);
			capitalAsegurado.setSuperficie(new BigDecimal(cambioMasivoVO.getSuperficie_cm()));

		}
	}

	private List<CapitalAsegurado> getListCapitalesAsegurados(Long idParcela) {
		List<CapitalAsegurado> entidades = getObjects(CapitalAsegurado.class, "parcela.idparcela", idParcela);
		return entidades;
	}

	/**
	 * 
	 */
	public void modificarInstalacionesCambioMasivo(CambioMasivoVO cambioMasivoVO, List<Parcela> listInstalaciones,
			Usuario usuario) {
		logger.debug("init - [SeleccionPolizaDao] modificarInstalacionesCambioMasivo");
		Session session = obtenerSession();

		try {
			for (Parcela instalacion : listInstalaciones) {

				if (!"".equals(cambioMasivoVO.getCultivo_cm())) {
					instalacion.setCodcultivo(new BigDecimal(cambioMasivoVO.getCultivo_cm()));
				}
				if (!"".equals(cambioMasivoVO.getVarieda_cm())) {
					instalacion.setCodvariedad(new BigDecimal(cambioMasivoVO.getVarieda_cm()));
				}
				if (!"".equals(cambioMasivoVO.getSuperficie_cm())) {

					Set<CapitalAsegurado> listCapitalesAsegurados = instalacion.getCapitalAsegurados();

					for (CapitalAsegurado capitalAsegurado : listCapitalesAsegurados) {
						capitalAsegurado.setSuperficie(new BigDecimal(cambioMasivoVO.getSuperficie_cm()));
						evict(capitalAsegurado);
						session.saveOrUpdate(capitalAsegurado);
					}
				}

				this.evict(instalacion);
				// TMR.Facturacion. Por cada cambio que se haga en una instalacion facturamos.
				saveOrUpdateFacturacion(instalacion, usuario);

			} // for
		} catch (Exception e) {
			logger.fatal("[DAOException sin throw][DatosParcelaFLDao][getNumInstalaciones]Error lectura BD", e);
		}

		logger.debug("end - [SeleccionPolizaDao] modificarInstalacionesCambioMasivo");
	}

	// muestra por consala una traza con los datos que vienen del cliente web
	private void traceCambioMasivo(CambioMasivoVO cambioMasivoVO, int i) {
		logger.debug("[Cambio masivo - DAO] -  idparcela: " + cambioMasivoVO.getListaParcelas().get(i) + "   cultivo:"
				+ cambioMasivoVO.getCultivo_cm() + "   variedad:" + cambioMasivoVO.getVarieda_cm() + "   inc.ha:"
				+ cambioMasivoVO.getIncrene_ha_cm() + "   inc.parcela:" + cambioMasivoVO.getIncreme_parcela_cm()
				+ "   inc.unidades:" + cambioMasivoVO.getInc_unidades_cm() + "   sist prod:"
				+ cambioMasivoVO.getSistproduccion_cm());
	}

	/**
	 * [fin] FUNCIONES CAMBIO MASIVO
	 */

	public List<ClaseDetalle> getClaseDetalle(long lineaseguroid, BigDecimal clase) {
		logger.debug("init - getClaseDetalle");
		List<ClaseDetalle> lstClaseDetalles = new ArrayList<ClaseDetalle>();
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Clase.class);
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("clase", clase));
		Clase claseAux = (Clase) criteria.uniqueResult();

		criteria = session.createCriteria(ClaseDetalle.class);
		criteria.add(Restrictions.eq(CLASE_ID, claseAux.getId()));
		lstClaseDetalles = criteria.list();
		logger.debug("end - getClaseDetalle");
		return lstClaseDetalles;
	}

	public List<Modulo> getCoberturasNivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;

		try {
			criteria = session.createCriteria(Modulo.class);
			if (listCodigosModulos.size() == 0) {
				listCodigosModulos.add("99999");
			}

			Criterion criterion = Restrictions.conjunction()// and
					.add(Restrictions.in("id.codmodulo", listCodigosModulos))
					.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			criteria.add(criterion);

		} catch (Exception e) {
			throw new DAOException("[DatosParcelaFLDao][getSubvencionesCCAANivelParcela]error lectura BD", e);
		}

		return criteria.list();

	}

	public List<SubvencionCCAA> getSubvencionesCCAANivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;

		try {
			criteria = session.createCriteria(SubvencionCCAA.class);
			if (listCodigosModulos.size() == 0) {
				listCodigosModulos.add("99999");
			}

			Criterion criterion = Restrictions.conjunction()// and
					.add(Restrictions.in("modulo.id.codmodulo", listCodigosModulos))
					.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(criterion);

		} catch (Exception e) {
			throw new DAOException("[DatosParcelaFLDao][getSubvencionesCCAANivelParcela]error lectura BD", e);
		}

		return criteria.list();
	}

	public List<SubvencionEnesa> getSubencionesEnesaNivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;

		try {
			criteria = session.createCriteria(SubvencionEnesa.class);
			if (listCodigosModulos.size() == 0) {
				listCodigosModulos.add("99999");
			}

			Criterion criterion = Restrictions.conjunction()// and
					.add(Restrictions.in("modulo.id.codmodulo", listCodigosModulos))
					.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(criterion);

		} catch (Exception e) {
			throw new DAOException("[DatosParcelaFLDao][getSubencionesEnesaNivelParcela]error lectura BD", e);
		}

		return criteria.list();
	}

	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(Long lineaseguroid, String codmodulo) {
		logger.debug("init - [DatosParcelaFLDao] getRiesgosCubiertosModulo");

		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();

		try {

			Criteria criteria = session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			lista = criteria.list();

		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][DatosParcelaFLDao][getRiesgosCubiertosModulo]Error lectura BD", e);
		}

		logger.debug("end - [DatosParcelaFLDao] getRiesgosCubiertosModulo");
		return lista;
	}

	public List<String> getListCodModulosClase(Long idClase) {
		logger.debug("init - getListCodModulosClase");
		List<String> lstCodModulosClases = new ArrayList<String>();
		Session session = obtenerSession();

		Criteria c = session.createCriteria(ClaseDetalle.class);
		c.add(Restrictions.eq(CLASE_ID, idClase));

		c.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("codmodulo"))));

		lstCodModulosClases = c.list();
		logger.debug("end - getListCodModulosClase");
		return lstCodModulosClases;
	}

	public List<BigDecimal> getListCodCultivosClase(Long idClase) {
		logger.debug("init - getListCodCultivosClase");
		List<BigDecimal> lstCodCultivosClase = new ArrayList<BigDecimal>();
		Session session = obtenerSession();

		Criteria c = session.createCriteria(ClaseDetalle.class);
		c.add(Restrictions.eq(CLASE_ID, idClase));

		c.setProjection(
				Projections.distinct(Projections.projectionList().add(Projections.property("cultivo.id.codcultivo"))));

		lstCodCultivosClase = c.list();
		logger.debug("end - getListCodCultivosClase");
		return lstCodCultivosClase;
	}

	public List<BigDecimal> getListCodVariedadesClase(Long idClase) {
		logger.debug("init - getListCodCultivosClase");
		List<BigDecimal> lstCodVariedadesClase = new ArrayList<BigDecimal>();
		Session session = obtenerSession();

		Criteria c = session.createCriteria(ClaseDetalle.class);
		c.add(Restrictions.eq(CLASE_ID, idClase));

		c.setProjection(Projections
				.distinct(Projections.projectionList().add(Projections.property("variedad.id.codvariedad"))));

		lstCodVariedadesClase = c.list();
		logger.debug("end - getListCodVariedadesClase");
		return lstCodVariedadesClase;
	}

	/**
	 * DAA 28/07/2012 Metodo para actualizar el TotalSuperficie a partir de un
	 * String de id de poliza.
	 * 
	 * @throws DAOException
	 */
	public void actualizaTotSuperficie(String stringIds) throws DAOException {
		Session session = obtenerSession();

		try {
			String sql = "UPDATE TB_POLIZAS P SET P.TOTALSUPERFICIE = (SELECT SUM(SUPERFICIE) FROM O02AGPE0.TB_CAPITALES_ASEGURADOS CA WHERE "
					+ "CA.IDPARCELA IN ( SELECT PAR.IDPARCELA FROM O02AGPE0.TB_PARCELAS PAR WHERE PAR.IDPOLIZA = P.IDPOLIZA )) WHERE "
					+ "P.IDPOLIZA IN (" + stringIds + ")";
			logger.debug("actualizaTotSuperficie - sql: " + sql);
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception e) {
			logger.error("Se ha producido un error al actualizar el Total de Superficie", e);
			throw new DAOException("Se ha producido un error al actualizar el Total de Superficie", e);
		}
		return;

	}

	/**
	 * Dada una lista de identificadores de parcela, obtengo otra lista de
	 * identificadores de parcela que tienen asociada alguna instalacion y que no se
	 * encuentra en la lista inicial de identificadores de parcela.
	 * 
	 * Este metodo se utilizara en el borrado de parcelas para comprobar si una
	 * parcela tiene instalaciones y, en caso de tenerlas, que la instalacion no
	 * haya sido marcada para su borrado.
	 */
	public List<Long> getParcelasDeInstalaciones(Long idPoliza, List<Long> listaIdsParcelas) {

		// Partir la lista en listas de 1000 elementos o menos
		List<List<Long>> listaIdsPartida = new ArrayList<List<Long>>();
		int numListas = (int) Math
				.ceil(new Double(listaIdsParcelas.size()) / new Double(Constants.MAX_NUM_ELEM_OPERATOR_IN));
		for (int i = 0; i < numListas; i++) {
			int ini = (i * Constants.MAX_NUM_ELEM_OPERATOR_IN);
			int fin = ((i + 1) * Constants.MAX_NUM_ELEM_OPERATOR_IN);
			if (fin > listaIdsParcelas.size()) {
				fin = listaIdsParcelas.size();
			}
			listaIdsPartida.add(new ArrayList<Long>(listaIdsParcelas.subList(ini, fin)));
		}
		logger.debug("Número total de elementos: " + listaIdsParcelas.size() + ". Listas partidas: "
				+ listaIdsPartida.size());

		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Parcela.class);
		criteria.createAlias("poliza", "pol");
		criteria.add(Restrictions.isNotNull("idparcelaestructura"));
		criteria.add(Restrictions.eq("pol.idpoliza", idPoliza));

		// añado las listas para buscar las instalaciones
		Disjunction orInstalaciones = Restrictions.disjunction();
		for (List<Long> lista1 : listaIdsPartida) {
			orInstalaciones.add(Restrictions.in("idparcelaestructura", lista1));
		}
		criteria.add(orInstalaciones);

		// añado las listas para no tener en cuenta las parcelas que vamos a borrar
		Disjunction orParcelas = Restrictions.disjunction();
		for (List<Long> lista1 : listaIdsPartida) {
			orParcelas.add(Restrictions.not(Restrictions.in(ID_PARCELA, lista1)));
		}
		criteria.add(orParcelas);

		criteria.setProjection(
				Projections.distinct(Projections.projectionList().add(Projections.property(ID_PARCELA))));
		return criteria.list();
	}

	/**
	 * DAA 19/12/2012 Guarda las instalaciones en la nueva parcela antes del borrado
	 * 
	 */
	public void actualizaInstalacion(Long idInstalacion, Long idNuevaParcela) throws DAOException {

		if (!StringUtils.nullToString(idInstalacion).equals("")) {
			Session session = obtenerSession();
			try {
				String sql = "UPDATE TB_PARCELAS P SET P.IDPARCELAESTRUCTURA = " + idNuevaParcela
						+ " WHERE P.IDPARCELA = " + idInstalacion;
				logger.debug("actualizaInstalaciones - sql: " + sql);
				session.createSQLQuery(sql).executeUpdate();
			} catch (Exception e) {
				logger.error("Se ha producido un error al actualizar la Instalacion con id: " + idInstalacion, e);
				throw new DAOException("Se ha producido un error al actualizar las Instalaciones", e);
			}
		}
	}

	/**
	 * DAA 19/12/2012 recupero todas las parcelas con el mismo sigpac, que no sea
	 * otra instalacion y que no sea la que quiero borrar
	 * 
	 * @param parcela
	 *            Parcela origen (la que sera eliminada)
	 * @param idPoliza
	 *            Poliza de la parcela
	 * @param listaIdsParcelas
	 *            parcelas que no se pueden utilizar para la reasignacion porque van
	 *            a ser eliminadas.
	 * @throws DaoException
	 */
	public List<Parcela> getParcelasMismoSigpac(Parcela parcela, Long idPoliza, List<Long> listaIdsParcelas) {

		logger.debug("init - [SeleccionPolizaDao] validarEliminarParcela");

		Session session = obtenerSession();
		List<Parcela> lista = new ArrayList<Parcela>();

		try {

			Criteria criteria = session.createCriteria(Parcela.class);

			criteria.createAlias("poliza", "poliza");

			criteria.add(Restrictions.eq("codprovsigpac", parcela.getCodprovsigpac()));
			criteria.add(Restrictions.eq("codtermsigpac", parcela.getCodtermsigpac()));
			criteria.add(Restrictions.eq("agrsigpac", parcela.getAgrsigpac()));
			criteria.add(Restrictions.eq("zonasigpac", parcela.getZonasigpac()));
			criteria.add(Restrictions.eq("poligonosigpac", parcela.getPoligonosigpac()));
			criteria.add(Restrictions.eq("parcelasigpac", parcela.getParcelasigpac()));
			criteria.add(Restrictions.eq("recintosigpac", parcela.getRecintosigpac()));
			criteria.add(Restrictions.eq("codcultivo", parcela.getCodcultivo()));
			criteria.add(Restrictions.eq("codvariedad", parcela.getCodvariedad()));

			criteria.add(Restrictions.eq(POLIZA_ID_POLIZA, idPoliza));
			criteria.add(Restrictions.eq("tipoparcela", 'P'));
			criteria.add(Restrictions.ne(ID_PARCELA, parcela.getIdparcela()));

			criteria.add(Restrictions.not(Restrictions.in(ID_PARCELA, listaIdsParcelas)));

			lista = criteria.list();

		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][SeleccionPolizaDao][validarEliminarParcela]Error lectura BD", e);
		}

		logger.debug("end - [SeleccionPolizaDao] validarEliminarParcela");
		return lista;
	}

	@Override
	public boolean isOficinaPagoManual(BigDecimal oficina, BigDecimal codEntidad) throws DAOException {

		logger.debug("init - [SeleccionPolizaDao] isOficinaPagoManual");

		Session session = obtenerSession();
		try {

			Criteria criteria = session.createCriteria(Oficina.class);

			criteria.add(Restrictions.eq("id.codoficina", oficina));
			criteria.add(Restrictions.eq("id.codentidad", codEntidad));
			Oficina ofi = (Oficina) criteria.uniqueResult();
			if (ofi != null) {
				if (ofi.getPagoManual().compareTo(Constants.PAGO_MANUAL) == 0) {
					return true;
				}
			}

		} catch (Exception e) {
			logger.fatal("Error generico al acceder a base de datos", e);
			throw new DAOException();
		}
		logger.debug("end - [SeleccionPolizaDao] isOficinaPagoManual");
		return false;
	}

	public void reCalculoPrecioProduccion(Collection<Parcela> parcelas, List<String> codsModuloPoliza)
			throws Exception {
		this.reCalculoPrecioProduccion(parcelas, codsModuloPoliza, false, null);
	}

	@Override
	public void actualizaHojaNumero(int hoja, int num, Long idparcela) throws DAOException {
		try {

			Session session = obtenerSession();
			// Connection con = session.connection();
			String sql = "update tb_parcelas a set hoja= " + hoja + ", numero = " + num + " where idparcela= "
					+ idparcela;

			session.createSQLQuery(sql).executeUpdate();
			// con.commit();

		} catch (Exception e) {
			logger.error("Se ha producido un error al actualizar las parcela: " + idparcela, e);
			throw new DAOException();
		}

	}

	/**
	 * Metodo para comprobar que para todas las parcelas hay produccion y precio
	 * para el/los modulo/s seleccionado/s
	 * 
	 * @param idPoliza
	 * @return boolean
	 */
	public boolean isParcelasCorrectas(Long idPoliza) {
		Session session = obtenerSession();
		String sql = "select  0 as cuenta, mod.IDCAPITALASEGURADO, 'Valores nulos' as descr  from o02agpe0.tb_cap_aseg_rel_modulo mod"
				+ " inner join o02agpe0.TB_CAPITALES_ASEGURADOS cap on cap.IDCAPITALASEGURADO = mod.IDCAPITALASEGURADO"
				+ " inner join o02agpe0.TB_PARCELAS par on cap.IDPARCELA = par.IDPARCELA "
				+ " inner join o02agpe0.tb_sc_c_tipo_capital c on c.codtipocapital = cap.codtipocapital "
				+ " where par.idpoliza=" + idPoliza + " and "
				+ "((mod.PRECIO is null or mod.PRODUCCION is null)or (mod.PRECIO = 0 or mod.PRODUCCION = 0)) and c.codconcepto = 68"
				+ " UNION select xx.cuenta, xx.IDCAPITALASEGURADO, xx.descr from"
				+ " (select distinct 0 as cuenta, mod.IDCAPITALASEGURADO, 'modulo distinto' as descr, mod.CODMODULO from o02agpe0.tb_cap_aseg_rel_modulo mod"
				+ " inner join o02agpe0.TB_CAPITALES_ASEGURADOS cap on cap.IDCAPITALASEGURADO = mod.IDCAPITALASEGURADO"
				+ " inner join o02agpe0.TB_PARCELAS par on cap.IDPARCELA = par.IDPARCELA where par.idpoliza=" + idPoliza
				+ ")  xx where "
				+ " (xx.CODMODULO not in ( select DISTINCT mp.CODMODULO from o02agpe0.tb_modulos_poliza mp where mp.IDPOLIZA ="
				+ idPoliza + "))";
		logger.debug(sql);
		List<?> list = session.createSQLQuery(sql).list();

		if (list != null && list.size() > 0) {
			logger.debug("isParcelasCorrectas -> false");
			return false;
		}
		logger.debug("isParcelasCorrectas -> true");
		return true;
	}

	public Map<Long, List<Long>> getMapaParcelasInstalaciones(List<String> lstCadenasIds) throws DAOException {
		Session session = obtenerSession();
		Map<Long, List<Long>> mapPolizasInstalaciones = new HashMap<Long, List<Long>>();
		StringBuilder stringQuery = new StringBuilder();
		boolean primera = true;
		try {
			stringQuery.append("select par.idparcela,par.idparcelaestructura from o02agpe0.tb_parcelas par where 1=1"); // par.idparcelaestructura
																														// in(5506823,5506824)
																														// order
																														// by
																														// idparcela";
			if (lstCadenasIds.size() > 0) {
				primera = true;
				for (String cadenaIds : lstCadenasIds) {
					if (primera) {
						stringQuery.append(" and (par.idparcelaestructura in (" + cadenaIds + ")");
						primera = false;
					} else
						stringQuery.append(" or par.idparcelaestructura in (" + cadenaIds + ")");
				}
				stringQuery.append(") order by par.idparcelaestructura");
			}

			logger.info("Consulta parcelas_instalaciones: ********* " + stringQuery.toString());
			List<?> lista = session.createSQLQuery(stringQuery.toString()).list();
			if (lista != null && lista.size() > 0) {
				BigDecimal parcelaAnterior = null;
				List<Long> lstInstalaciones = new ArrayList<Long>();
				for (int j = 0; j < lista.size(); j++) {
					Object[] registro = (Object[]) lista.get(j);
					BigDecimal idEstructura = (BigDecimal) registro[0];
					BigDecimal idParcela = (BigDecimal) registro[1];
					logger.debug("idEstructura: " + idEstructura + " idParcela de la estructura: " + idParcela);
					if (parcelaAnterior == null) {
						lstInstalaciones.add(idEstructura.longValue());
					} else {
						if (parcelaAnterior.compareTo(idParcela) == 0) {
							lstInstalaciones.add(idEstructura.longValue());
						} else {
							mapPolizasInstalaciones.put(parcelaAnterior.longValue(), lstInstalaciones);
							lstInstalaciones = new ArrayList<Long>();
							lstInstalaciones.add(idEstructura.longValue());
						}
					}
					parcelaAnterior = idParcela;
				}
				mapPolizasInstalaciones.put(parcelaAnterior.longValue(), lstInstalaciones);
			} else {
				logger.info("parcelas seleccionadas sin instalaciones");
			}

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error al recoger las parcelas_instalaciones de bbdd", e);
		}
		return mapPolizasInstalaciones;
	}

	public List<Parcela> getInstalaciones(List<Long> lstIdsInstall) {
		logger.debug("init - [SeleccionPolizaDao] getInstalaciones");
		Session session = obtenerSession();
		List<Parcela> lista = new ArrayList<Parcela>();

		try {

			Criteria criteria = session.createCriteria(Parcela.class);
			criteria.add(Restrictions.in(ID_PARCELA, lstIdsInstall));
			lista = criteria.list();

		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][SeleccionPolizaDao][getInstalaciones]Error lectura BD", e);
		}

		logger.debug("end - [SeleccionPolizaDao] getInstalaciones");
		return lista;
	}

	public void marcarRecalculoHojaYNum(Long idPoliza) {
		try {
			Session session = obtenerSession();
			String sql = "update tb_polizas pol set pol.ind_recalculo_hoja_numero= 1 where pol.idpoliza =" + idPoliza;
			logger.debug(sql);
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception e) {
			logger.error(
					"[SeleccionPolizaDao]marcarRecalculoHojaYNum - Error al marcar la poliza para recalcular hoja y numero",
					e);
		}
	}

	public boolean checkRecalculoHojaYNumPoliza(Long idPoliza) {
		Session session = obtenerSession();
		try {
			String sqlQuery = "select count(*) from tb_polizas pol, tb_parcelas par"
					+ " where pol.idpoliza = par.idpoliza and (pol.ind_recalculo_hoja_numero=1 or par.ind_recalculo_hoja_numero=1)"
					+ " and  pol.idpoliza= " + idPoliza;
			SQLQuery query = session.createSQLQuery(sqlQuery);
			Integer count = new Integer((query.uniqueResult()).toString());
			if (count.compareTo(new Integer(0)) == 0)
				return false;
			else
				return true;

		} catch (Exception e) {
			logger.error("Error al comprobar el recalculo de hoja y num en la poliza" + idPoliza, e);
		}
		return false;
	}

	public void inicializarRecalculoHojaYNumPoliza(Long idPoliza) {
		try {
			Session session = obtenerSession();

			String sql = "update tb_polizas pol set pol.ind_recalculo_hoja_numero = '' where idpoliza = " + idPoliza;
			Query query = session.createSQLQuery(sql);
			query.executeUpdate();

			String sql2 = "update tb_parcelas pol set pol.ind_recalculo_hoja_numero = '' where idpoliza = " + idPoliza;
			Query query2 = session.createSQLQuery(sql2);
			query2.executeUpdate();

		} catch (Exception e) {
			logger.error(
					"Se ha producido un error al inicializar el recalculo de hoja y numero en la poliza" + idPoliza, e);
		}
	}

	@Override
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulos(final Long lineaseguroid, final String listCodModulos) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(RiesgoCubiertoModulo.class);
		criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
		if (!StringUtils.isNullOrEmpty(listCodModulos)) {
			criteria.add(Restrictions.in("modulo.id.codmodulo", listCodModulos.split("\\,")));
		}
		return criteria.list();
	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM 01.12.2020) ** Inicio */
	/**
	 * Método para obtener los datos variables de parcela que dependen del concepto
	 * principal del modulo y del riesgo cubierto
	 * 
	 * @throws BusinessException
	 */
	public Map<String, List<String>> getCoberturasCapAseg301(Long idPoliza, String codmodulo) throws BusinessException {
		GregorianCalendar gcI = new GregorianCalendar();

		String procedure = "PQ_DATOS_VARIABLES_RIESGO.getDatVarCoberturaParcela (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2";
		// Establecemos los parámetros para llamar al PL
		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("IDPOLIZAPARAM", idPoliza);
		parametros.put("CODMODULOPARAM", codmodulo);

		logger.debug(
				"Llamada al procedimiento PQ_DATOS_VARIABLES_RIESGO.getDatVarCoberturaParcela con los siguientes parámetros: ");
		logger.debug("    IDPOLIZAPARAM: " + idPoliza);
		logger.debug("    CODMODULOPARAM: " + codmodulo);

		// Ejecutamos el PL. El resultado está en la Clave del Map RESULT
		Map<String, Object> resultado = databaseManager.executeStoreProc(procedure, parametros);

		// Montamos un mapa indexado por codigo de concepto y con una lista de
		// string con cada
		// combinacion CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		String strDatVar = (String) resultado.get("RESULT");
		Map<String, List<String>> lstDatVar = new HashMap<String, List<String>>();
		Map<String, Integer> mapaIndicesPorCapAseg = new HashMap<String, Integer>();

		if (!StringUtils.nullToString(strDatVar).equals("")) {

			// MPM - Sigpe 8045
			// Modificaci�n para gestionar varios riesgos elegibles a nivel de parcela
			int index = 0;

			for (String concepto : strDatVar.split("\\|")) {
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2) {
					// cod_valor[0] = idcapitalasegurado; cod_valor[1] =
					// codmodulo#codconceptoppalmod#codriesgocubierto#aux_codvalor
					// NOTA: EL CAMPO codmodulo QUE VIENE DEL PL NO ES NECESARIO, PERO LO DEJO
					// PORQUE ESTABA ASI EN LA VERSION INICIAL

					// Obtiene el �ndice para el capital asegurado que se est� tratando, ya que
					// pueden venir riesgos elegibles a nivel de parcela
					// Si es la primera vez que se procesa el CA
					if (!mapaIndicesPorCapAseg.containsKey(cod_valor[0])) {
						index = 0;
					}
					// Si ya se ha procesado el CA antes
					else {
						index = mapaIndicesPorCapAseg.get(cod_valor[0]) + 1;
					}

					mapaIndicesPorCapAseg.put(cod_valor[0], index);

					lstDatVar.put(cod_valor[0] + "#" + index, Arrays.asList(cod_valor[1].split("#")));
				}
			}
		}

		GregorianCalendar gcF = new GregorianCalendar();
		Long tiempo = gcF.getTimeInMillis() - gcI.getTimeInMillis();
		logger.debug("Tiempo de la llamada a PQ_DATOS_VARIABLES_RIESGO.getDatVarCoberturaParcela: " + tiempo
				+ " milisegundos");

		return lstDatVar;
	}

	@Override
	public List<Explotacion> getExplotaciones(final Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Explotacion.class);
		criteria.createAlias("poliza", "poliza");
		criteria.add(Restrictions.eq("poliza.id", idPoliza));
		return criteria.list();
	}

	@Override
	public List<PolizaSocio> getSociosPoliza(final Long idPoliza) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(PolizaSocio.class);
		criteria.createAlias("poliza", "poliza");
		criteria.add(Restrictions.eq("poliza.id", idPoliza));
		return criteria.list();
	}

}