package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.NumAniosDesdePoda;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaConduccion;
import com.rsi.agp.dao.tables.cpl.Factor;
import com.rsi.agp.dao.tables.orgDat.VistaIGPFactorAmbito;
import com.rsi.agp.dao.tables.orgDat.VistaPorFactores;
import com.rsi.agp.dao.tables.orgDat.VistaSistemaCultivo312;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class OrigenDatosDao extends BaseDaoHibernate implements IOrigenDatosDao {

	private static final Log logger = LogFactory.getLog(OrigenDatosDao.class);

	@Override
	public List<ComparativaPoliza> getRiesgosCubiertos(String codPoliza, BigDecimal codRiesgo, String valor)
			throws DAOException {
		logger.debug("init - [DatosParcelaFLDao] getRiesgosCubiertos");

		Session session = obtenerSession();

		try {

			Criteria criteria = session.createCriteria(ComparativaPoliza.class);
			criteria.add(Restrictions.eq("poliza.idpoliza", new Long(codPoliza)));
			criteria.add(Restrictions.eq("id.codconcepto", codRiesgo));
			criteria.add(Restrictions.eq("id.codvalor", new BigDecimal(valor)));

			logger.debug("end - [DatosParcelaFLDao] getRiesgosCubiertos");
			return criteria.list();

		} catch (Exception e) {
			throw new DAOException("[DatosParcelaFLDao][getRiesgosCubiertos]error lectura BD", e);
		}
	}

	public List getFieldFromClase(Long lineaseguroid, Long clase, String query) {
		Session session = obtenerSession();
		Query hql = session.createQuery(query);
		hql.setParameter("clase_", new BigDecimal(clase.toString()));
		hql.setParameter("lineaseguroid_", lineaseguroid);
		return hql.list();
	}

	public String getClaseQuery(String campo) {
		return "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle "
				+ "WHERE claseDetalle.clase.clase =:clase_ AND claseDetalle.clase.linea.lineaseguroid =:lineaseguroid_";
	}

	public List getCampoClaseDetalle(String campo, BigDecimal clase) {
		Session session = obtenerSession();
		String query = "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle "
				+ "WHERE claseDetalle.clase.id =:clase_ and " + campo + " is not null";
		logger.debug("getCampoClaseDetalle - query: " + query);
		Query hql = session.createQuery(query);
		hql.setParameter("clase_", clase);
		return hql.list();
	}

	@Override
	public List<BigDecimal> dameListaValoresConceptoFactor(BigDecimal lineaseguroid, String lstModulos,
			BigDecimal codConcepto) {

		Session session = obtenerSession();
		String cad = "";
		cad = lstModulos + ";99999";
		String[] modulos = cad.split(";");

		Criteria criteria = session.createCriteria(Factor.class);
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid.longValue()));
		criteria.add(Restrictions.eq("id.codconcepto", codConcepto));
		criteria.add(Restrictions.in("modulo.id.codmodulo", modulos));
		criteria.setProjection(Projections.distinct(Projections.property("id.valorconcepto")));
		List<BigDecimal> lstValoresConcepto = criteria.list();

		return lstValoresConcepto;
	}

	public List<BigDecimal> dameListaTotalValoresConceptoFactor(BigDecimal lineaseguroid, BigDecimal codConcepto) {

		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Factor.class);
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid.longValue()));
		criteria.add(Restrictions.eq("id.codconcepto", codConcepto));
		criteria.setProjection(Projections.distinct(Projections.property("id.valorconcepto")));
		List<BigDecimal> lstValoresConcepto = criteria.list();

		return lstValoresConcepto;
	}

	public List<SistemaConduccion> getListSistemaConduccion(BigDecimal lineaseguroid, BigDecimal codConcepto)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;

		try {
			criteria = session.createCriteria(SistemaConduccion.class);
			List<BigDecimal> lstValoresConceptoFactor = this.dameListaTotalValoresConceptoFactor(lineaseguroid,
					codConcepto);
			criteria.add(Restrictions.in("codsistemaconduccion", lstValoresConceptoFactor));

		} catch (Exception e) {
			throw new DAOException("[OrigenDatosDao][getListSistemaConduccion]error lectura BD", e);
		}

		return criteria.list();
	}

	public List<NumAniosDesdePoda> getListNumAniosPoda() throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;

		try {
			criteria = session.createCriteria(NumAniosDesdePoda.class);

		} catch (Exception e) {
			throw new DAOException("[OrigenDatosDao][getListNumAniosPoda]error lectura BD", e);
		}

		return criteria.list();
	}

	public List<PracticaCultural> getListPracticaCultural314(BigDecimal lineaseguroid, BigDecimal codConcepto)
			throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;

		try {
			criteria = session.createCriteria(PracticaCultural.class);
			List<BigDecimal> lstValoresConceptoFactor = this.dameListaTotalValoresConceptoFactor(lineaseguroid,
					codConcepto);
			criteria.add(Restrictions.in("codpracticacultural", lstValoresConceptoFactor));

		} catch (Exception e) {
			throw new DAOException("[OrigenDatosDao][getListPracticaCultural314]error lectura BD", e);
		}

		return criteria.list();
	}

	public List<VistaSistemaCultivo312> getVistaSistemaCultivo312(List<BigDecimal> listProvincias,
			List<BigDecimal> listComarcas, List<BigDecimal> listTerminos, List<BigDecimal> listCultivos,
			List<BigDecimal> listVariedades, List<Character> listSubterminos, List<BigDecimal> listSistemaCultivos,
			Long lineaSeguroId, String codmodulo, BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino, Character subtermino,
			BigDecimal codtipocapital) {
		List<VistaSistemaCultivo312> lstVistaSisCult312 = new ArrayList<VistaSistemaCultivo312>();

		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(VistaSistemaCultivo312.class);
			Conjunction c = Restrictions.conjunction();

			c.add(Restrictions.disjunction().add(Restrictions.eq("id.codtipocapital", codtipocapital))
					.add(Restrictions.isNull("id.codtipocapital")));

			/* PROPIEDADES DE MODULO */
			if (FiltroUtils.noEstaVacio(lineaSeguroId)) {
				c.add(Restrictions.eq("id.lineaseguroid", new BigDecimal(lineaSeguroId)));
			}
			if (FiltroUtils.noEstaVacio(codmodulo)) {
				StringTokenizer tokens = new StringTokenizer(codmodulo, ";");

				Disjunction dd = Restrictions.disjunction();

				while (tokens.hasMoreTokens()) {
					dd.add(Restrictions.eq("id.codmodulo", new String(tokens.nextToken())));
				}
				c.add(dd);
			}
			if (listCultivos != null && !listCultivos.contains(new BigDecimal("999"))) {
				if (listCultivos.size() > 0) {
					listCultivos.add(new BigDecimal("999"));
				}
				c.add(Restrictions.in("id.codcultivo", listCultivos));
			}
			if (FiltroUtils.noEstaVacio(codcultivo) && !listCultivos.contains(new BigDecimal("999"))) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codcultivo", codcultivo))
						.add(Restrictions.eq("id.codcultivo", new BigDecimal("999"))));
			}
			if (listVariedades != null && !listVariedades.contains(new BigDecimal("999"))) {
				if (listVariedades.size() > 0) {
					listVariedades.add(new BigDecimal("999"));
				}
				c.add(Restrictions.in("id.codvariedad", listVariedades));
			}
			if (FiltroUtils.noEstaVacio(codvariedad)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codvariedad", codvariedad))
						.add(Restrictions.eq("id.codvariedad", new BigDecimal("999")))

				);
			}
			if (listProvincias != null && !listProvincias.contains(new BigDecimal("99"))) {
				if (listProvincias.size() > 0) {
					listProvincias.add(new BigDecimal("99"));
				}
				c.add(Restrictions.in("id.codprovincia", listProvincias));
			}
			if (FiltroUtils.noEstaVacio(codprovincia)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codprovincia", codprovincia))
						.add(Restrictions.eq("id.codprovincia", new BigDecimal("99"))));
			}
			if (listComarcas != null && !listComarcas.contains(new BigDecimal("99"))) {
				if (listComarcas.size() > 0) {
					listComarcas.add(new BigDecimal("99"));
				}
				c.add(Restrictions.in("id.codcomarca", listComarcas));
			}
			if (FiltroUtils.noEstaVacio(codcomarca)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codcomarca", codcomarca))
						.add(Restrictions.eq("id.codcomarca", new BigDecimal("99"))));
			}

			if (listTerminos != null && !listTerminos.contains(new BigDecimal("999"))) {
				if (listTerminos.size() > 0) {
					listTerminos.add(new BigDecimal("999"));
				}
				c.add(Restrictions.in("id.codtermino", listTerminos));
			}
			if (FiltroUtils.noEstaVacio(codtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codtermino", codtermino))
						.add(Restrictions.eq("id.codtermino", new BigDecimal("999"))));
			}

			if (listSubterminos != null && !listSubterminos.contains(new Character('9'))) {
				if (listSubterminos.size() > 0) {
					listSubterminos.add(new Character('9'));
				}
				c.add(Restrictions.in("id.subtermino", listSubterminos));
			}
			if (FiltroUtils.noEstaVacio(subtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.subtermino", subtermino))
						.add(Restrictions.eq("id.subtermino", new Character('9'))));
			}
			// codsistemacultivos
			// if(listSistemaCultivos != null){
			// c.add(Restrictions.in("id.codsistemacultivo", listSistemaCultivos));
			// }
			criteria.add(c);
			lstVistaSisCult312 = criteria.list();
		} catch (Exception e) {
			logger.debug("[OrigenDatosDao][getVistaSistemaCultivo312] Se ha producido un error en la BBDD: "
					+ e.getMessage());
		}
		return lstVistaSisCult312;
	}

	public List<VistaIGPFactorAmbito> getVistaIGP307FacAmb(BigDecimal lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad, BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino) {

		List<VistaIGPFactorAmbito> lstVistaIGP307FacAmb = new ArrayList<VistaIGPFactorAmbito>();
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(VistaIGPFactorAmbito.class);
			Conjunction c = Restrictions.conjunction();

			/* PROPIEDADES DE MODULO */
			if (FiltroUtils.noEstaVacio(lineaSeguroId)) {
				c.add(Restrictions.eq("id.lineaseguroid", lineaSeguroId));
			}
			if (FiltroUtils.noEstaVacio(codmodulo)) {
				StringTokenizer tokens = new StringTokenizer(codmodulo, ";");
				Disjunction dd = Restrictions.disjunction();
				while (tokens.hasMoreTokens()) {
					dd.add(Restrictions.eq("id.codmodulo", new String(tokens.nextToken())));
				}
				// Añadimos modulo generico
				dd.add(Restrictions.eq("id.codmodulo", "99999"));
				c.add(dd);
			}
			if (FiltroUtils.noEstaVacio(codcultivo)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codcultivo", codcultivo))
						.add(Restrictions.eq("id.codcultivo", new BigDecimal("999"))));
			}
			if (FiltroUtils.noEstaVacio(codvariedad)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codvariedad", codvariedad))
						.add(Restrictions.eq("id.codvariedad", new BigDecimal("999"))));
			}
			if (FiltroUtils.noEstaVacio(codprovincia)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codprovincia", codprovincia))
						.add(Restrictions.eq("id.codprovincia", new BigDecimal("99")))
						.add(Restrictions.isNull("id.codprovincia")));
			}
			if (FiltroUtils.noEstaVacio(codcomarca)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codcomarca", codcomarca))
						.add(Restrictions.eq("id.codcomarca", new BigDecimal("99"))));
			}
			if (FiltroUtils.noEstaVacio(codtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codtermino", codtermino))
						.add(Restrictions.eq("id.codtermino", new BigDecimal("999"))));
			}
			if (FiltroUtils.noEstaVacio(subtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq("id.codsubtermino", subtermino))
						.add(Restrictions.eq("id.codsubtermino", new Character('9'))));
			}
			criteria.add(c);
			lstVistaIGP307FacAmb = criteria.list();
		} catch (Exception e) {
			logger.debug(
					"[OrigenDatosDao][getVistaIGP307FacAmb] Se ha producido un error en la BBDD: " + e.getMessage());
		}
		return lstVistaIGP307FacAmb;
	}

	public List<VistaPorFactores> getVistaPorFactores(BigDecimal lineaSeguroId, String codmodulo,
			BigDecimal codConcepto, BigDecimal idClase) {

		List<VistaPorFactores> lstVistaPorFactores = new ArrayList<VistaPorFactores>();
		List<BigDecimal> ciclosCultivo = new ArrayList<BigDecimal>();
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(VistaPorFactores.class);
			Conjunction c = Restrictions.conjunction();

			if (FiltroUtils.noEstaVacio(lineaSeguroId)) {
				c.add(Restrictions.eq("id.lineaseguroid", lineaSeguroId));
			}
			if (FiltroUtils.noEstaVacio(codmodulo)) {
				c.add(Restrictions.in("id.codmodulo", codmodulo.split(";")));
			}
			if (FiltroUtils.noEstaVacio(codConcepto)) {
				c.add(Restrictions.eq("id.codconcepto", codConcepto));
			}
			// DAA 23/07/2012
			logger.debug("getVistaPofFactores: lineaseguroid=" + lineaSeguroId + ", codmodulo=" + codmodulo
					+ ", codconcepto=" + codConcepto + ", idclase=" + idClase);
			if (codConcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_CICLOCULTIVO)) && idClase != null) {
				ciclosCultivo = this.getCampoClaseDetalle("claseDetalle.cicloCultivo.codciclocultivo", idClase);
				if (ciclosCultivo.size() > 0)
					c.add(Restrictions.in("id.codvalor", ciclosCultivo));
			}

			criteria.add(c);
			lstVistaPorFactores = criteria.list();

		} catch (Exception e) {
			logger.debug(
					"[OrigenDatosDao][getVistaPorFactores] Se ha producido un error en la BBDD: " + e.getMessage());
		}
		return lstVistaPorFactores;
	}
}