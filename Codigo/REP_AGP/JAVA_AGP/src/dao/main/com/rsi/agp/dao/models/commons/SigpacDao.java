package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.cgen.ZonificacionGrupoCultivoDetalle;
import com.rsi.agp.dao.tables.cgen.ZonificacionSIGPAC;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoSigpacAgro;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.SigpacVO;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SigpacDao extends BaseDaoHibernate implements ISigpacDao {

	/**
	 * ------------------------------------------------------------------------
	 * METODOS PUBLICOS
	 * -----------------------------------------------------------------------
	 */
	public LocalCultVarVO getLocalCultVar(SigpacVO sigpacVO, Long claseId) throws DAOException {
		logger.debug("init - [SigpacDao] getLocalCultVar");
		LocalCultVarVO localCultVarVO = new LocalCultVarVO();
		ZonificacionSIGPAC zonificacionSIGPAC = new ZonificacionSIGPAC();
		List<ZonificacionSIGPAC> listZonificacionSIGPAC = null;

		try {

			listZonificacionSIGPAC = getZonificacionSIGPAC(sigpacVO);

			if (listZonificacionSIGPAC.size() > 0) {
				zonificacionSIGPAC = listZonificacionSIGPAC.get(0);

				localCultVarVO = setLocalCultVarVO(zonificacionSIGPAC.getCodprovincia(),
						zonificacionSIGPAC.getCodtermino(), zonificacionSIGPAC.getSubtermino(), claseId);

				// Si cultivo es único lo guardo en el objeto para enviarselo al cliente
				if (isUnicCultivo(listZonificacionSIGPAC)) {
					if (zonificacionSIGPAC.getZonificacionGrupoCultivo() != null && zonificacionSIGPAC
							.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles() != null) {
						if (zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles()
								.size() > 0) {
							BigDecimal codCultivoAux = zonificacionSIGPAC.getZonificacionGrupoCultivo()
									.getZonificacionGrupoCultivoDetalles().iterator().next().getId().getCodcultivo();
							CultivoId cultivoId = new CultivoId();
							cultivoId.setCodcultivo(codCultivoAux);
							cultivoId.setLineaseguroid(Long.parseLong(sigpacVO.getLineaseguroid()));

							Cultivo cultivoAux = (Cultivo) this.getObject(Cultivo.class, cultivoId);
							localCultVarVO.setCodCultivo(cultivoAux.getId().getCodcultivo().toString());
							localCultVarVO.setDesCultivo(cultivoAux.getDescultivo());
						} else {
							localCultVarVO.setCodCultivo("");
							localCultVarVO.setDesCultivo("");
						}
					}
				}
			}

		} catch (Exception e) {
			throw new DAOException("[SigpacDao][getLocalCultVar]error no controlado", e);
		}

		logger.debug("end - [SigpacDao] getLocalCultVar");
		return localCultVarVO;
	}

	/**
	 * ------------------------------------------------------------------------
	 * MÉTODOS PRIVADOS
	 * -----------------------------------------------------------------------
	 */

	public LocalCultVarVO getLocalFromTerminosSigpacAgro(SigpacVO sigpacVO, Long claseId) {

		LocalCultVarVO localCultVarVO = new LocalCultVarVO();

		List<TerminoSigpacAgro> listTerminosSigpacAgro = getTerminosSigpacAgro(new BigDecimal(sigpacVO.getProv()),
				new BigDecimal(sigpacVO.getTerm()));

		if (listTerminosSigpacAgro.size() > 0) {
			List<Termino> listTerminos = getTerminos(listTerminosSigpacAgro.get(0).getCodprovagr(),
					listTerminosSigpacAgro.get(0).getCodmunagr());

			if (listTerminos.size() > 0) {
				Termino termino = (Termino) listTerminos.get(0);
				TerminoSigpacAgro terminoSigpacAgro = listTerminosSigpacAgro.get(0);

				localCultVarVO = getLocalCultVarVO(termino, terminoSigpacAgro);
			}
		}

		return localCultVarVO;

	}

	/**
	 * 
	 * @param termino
	 * @param terminoSigpacAgro
	 * @return
	 */
	private LocalCultVarVO getLocalCultVarVO(Termino termino, TerminoSigpacAgro terminoSigpacAgro) {

		LocalCultVarVO localCultVarVO = new LocalCultVarVO();

		localCultVarVO.setCodComarca(termino.getComarca().getId().getCodcomarca().toString());
		localCultVarVO.setCodTermino(termino.getId().getCodtermino().toString());
		localCultVarVO.setCodProvincia(termino.getProvincia().getCodprovincia().toString());
		localCultVarVO.setNomComarca(termino.getComarca().getNomcomarca());
		localCultVarVO.setNomProvincia(termino.getProvincia().getNomprovincia());
		localCultVarVO.setNomTermino(termino.getNomtermino());
		localCultVarVO.setSubTermino(termino.getId().getSubtermino() + "");
		
		return localCultVarVO;
	}

	/**
	 * 
	 * @param provincia
	 * @param termino
	 * @return
	 */
	private List<TerminoSigpacAgro> getTerminosSigpacAgro(BigDecimal provincia, BigDecimal termino) {
		Session session = obtenerSession();

		String query = "SELECT FROM TerminoSigpacAgro terminoSigpacAgro "
				+ "WHERE terminoSigpacAgro.codprovincia =:provincia_ AND terminoSigpacAgro.codmunicipio =:termino_";

		Query hql = session.createQuery(query);
		hql.setParameter("provincia_", provincia);
		hql.setParameter("termino_", termino);
		return hql.list();
	}

	/**
	 * 
	 * @param provincia
	 * @param termino
	 * @return
	 */
	private List<Termino> getTerminos(BigDecimal provincia, BigDecimal termino) {
		Session session = obtenerSession();

		String query = "SELECT FROM Termino termino "
				+ "WHERE termino.id.codprovincia =:provincia_ AND termino.id.codtermino =:termino_";

		// Obtengo el objeto termino
		Query hql = session.createQuery(query);
		hql.setParameter("provincia_", provincia);
		hql.setParameter("termino_", termino);

		return hql.list();
	}

	/**
	 * 
	 * @param codProvincia
	 * @param codTermino
	 * @param subtermino
	 * @param claseId
	 * @return
	 */
	private LocalCultVarVO setLocalCultVarVO(BigDecimal codProvincia, BigDecimal codTermino, Character subtermino,
			Long claseId) {
		logger.debug("init - [SigpacDao] setLocalCultVarVO");

		LocalCultVarVO localCultVarVO = new LocalCultVarVO();
		List<VistaTerminosAsegurable> listVistaTerminosAsegurable = null;
		VistaTerminosAsegurable vistaTerminosAsegurable = null;

		Criterion critProvinc = null;
		Criterion critTermino = null;
		Criterion critSubterm = null;

		try {

			Session session = obtenerSession();

			critProvinc = getCriterionClase(claseId, "claseDetalle.codprovincia", "id.codprovincia", "99");
			critTermino = getCriterionClase(claseId, "claseDetalle.codtermino", "id.codtermino", "999");
			critSubterm = getCriterionClase(claseId, "claseDetalle.subtermino", "id.subtermino", "9");

			Criteria criteria = session.createCriteria(VistaTerminosAsegurable.class);

			criteria.add(Restrictions.eq("id.codprovincia", codProvincia));
			if (critProvinc != null) {
				criteria.add(critProvinc);
			}

			criteria.add(Restrictions.eq("id.codtermino", codTermino));
			if (critTermino != null) {
				criteria.add(critTermino);
			}

			if (Character.isLetterOrDigit(subtermino)) {
				criteria.add(Restrictions.eq("id.subtermino", subtermino));
				if (critSubterm != null) {
					getCriterionClase(claseId, "claseDetalle.subtermino", "id.subtermino", "9");
				}
			}

			listVistaTerminosAsegurable = criteria.list();

			if (listVistaTerminosAsegurable.size() > 0) {
				vistaTerminosAsegurable = listVistaTerminosAsegurable.get(0);
				localCultVarVO.setCodProvincia(vistaTerminosAsegurable.getId().getCodprovincia().toString());
				localCultVarVO.setNomProvincia(vistaTerminosAsegurable.getId().getNomprovincia());
				localCultVarVO.setCodComarca(vistaTerminosAsegurable.getId().getCodcomarca().toString());
				localCultVarVO.setNomComarca(vistaTerminosAsegurable.getId().getNomcomarca());
				localCultVarVO.setCodTermino(vistaTerminosAsegurable.getId().getCodtermino().toString());
				localCultVarVO.setNomTermino(vistaTerminosAsegurable.getId().getNomtermino());
				localCultVarVO.setSubTermino(vistaTerminosAsegurable.getId().getSubtermino().toString());
			}

		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][SigpacDao][setLocalCultVarVO]Error lectura BD", e);
		}
		logger.debug("end - [SigpacDao] setLocalCultVarVO");
		return localCultVarVO;
	}

	/**
	 * Método genérico para la obtención de un Criterion al que se le pasa una
	 * lista de valores
	 * 
	 * Parametros: id clase, ruta objeto clase, ruta objeto restrictions
	 */
	private Criterion getCriterionClase(Long claseId, String pathClase, String pathRestrictions, String todos) {
		List listClase = null;
		Criterion criterion = null;

		try {
			Clase clase = (Clase) this.get(Clase.class, claseId);
			if (clase != null) {
				listClase = this.getFieldFromClase(clase.getLinea().getLineaseguroid(),
						new Long(clase.getClase().toString()), this.getClaseQuery(pathClase));
			}

			if ("9".equals(todos)) {
				if (listClase != null && !listClase.contains(new Character('9'))) {
					if (listClase.size() > 0) {
						listClase.add(new Character('9'));
					}
					criterion = Restrictions.disjunction().add(Restrictions.in(pathRestrictions, listClase));
				}
			} else if ("99".equals(todos)) {
				if (listClase != null && !listClase.contains(new BigDecimal("99"))) {
					if (listClase.size() > 0) {
						listClase.add(new BigDecimal("99"));
					}
					criterion = Restrictions.disjunction().add(Restrictions.in(pathRestrictions, listClase));
				}
			} else if ("999".equals(todos)) {
				if (listClase != null && !listClase.contains(new BigDecimal("999"))) {
					if (listClase.size() > 0) {
						listClase.add(new BigDecimal("999"));
					}
					criterion = Restrictions.disjunction().add(Restrictions.in(pathRestrictions, listClase));
				}
			}

		} catch (Exception ex) {
			return null;
		}
		return criterion;
	}

	/**
	 * 
	 * @param sigpacVO
	 * @return
	 * @throws DAOException
	 */
	private List<ZonificacionSIGPAC> getZonificacionSIGPAC(SigpacVO sigpacVO) throws DAOException {
		logger.debug("init - [SigpacDao] getZonificacionSIGPAC");

		Session session = obtenerSession();
		List<ZonificacionSIGPAC> lst;
		try {

			List lstFamilia = this.getObjectsBySQLQuery(
					"select codfamilia from tb_sc_c_zonif_familias_det where codlinea = " + sigpacVO.getCodLinea());

			if (lstFamilia.size() > 0) {
				Criteria criteria = session.createCriteria(ZonificacionSIGPAC.class);

				criteria.add(Restrictions.eq("codprovsigpac", new BigDecimal(sigpacVO.getProv())));
				criteria.add(Restrictions.eq("codtermsigpac", new BigDecimal(sigpacVO.getTerm())));
				criteria.add(Restrictions.eq("agrsigpac", new BigDecimal(sigpacVO.getAgr())));
				criteria.add(Restrictions.eq("zonasigpac", new BigDecimal(sigpacVO.getZona())));
				criteria.add(Restrictions.eq("poligonosigpac", new BigDecimal(sigpacVO.getPol())));
				criteria.add(Restrictions.eq("parcelasigpac", new BigDecimal(sigpacVO.getParc())));

				criteria.createAlias("zonificacionFamilia", "zonificacionFamilia");
				criteria.add(Restrictions.eq("zonificacionFamilia.codfamilia", lstFamilia.get(0)));

				lst = criteria.list();

				logger.debug("end - [SigpacDao] getZonificacionSIGPAC");
				return lst;
			}

		} catch (Exception e) {
			throw new DAOException("[SigpacDao][getZonificacionSIGPAC]error al obtener la zonificación", e);
		}

		return null;
	}

	/**
	 * 
	 * @param campo
	 * @return
	 */
	private String getClaseQuery(String campo) {
		return "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle "
				+ "WHERE claseDetalle.clase.clase =:clase_ AND claseDetalle.clase.linea.lineaseguroid =:lineaseguroid_";
	}

	/**
	 * 
	 * @param lineaseguroid
	 * @param clase
	 * @param query
	 * @return
	 */
	private List getFieldFromClase(Long lineaseguroid, Long clase, String query) {
		Session session = obtenerSession();
		Query hql = session.createQuery(query);
		hql.setParameter("clase_", new BigDecimal(clase.toString()));
		hql.setParameter("lineaseguroid_", lineaseguroid);
		return hql.list();
	}

	/**
	 * Comprueba si en hay un solo cultivo
	 * 
	 * @param listZonificacionSIGPAC
	 * @return boolean true si es único
	 */
	private boolean isUnicCultivo(List<ZonificacionSIGPAC> listZonificacionSIGPAC) {
		logger.debug("init - [SigpacDao] isUnicCultivo");
		boolean result = false;
		List<BigDecimal> listCodCultivos = new ArrayList<BigDecimal>();

		// Recorro la lista obteniendo los cultivos
		for (ZonificacionSIGPAC zonificacionSIGPAC : listZonificacionSIGPAC) {
			if (zonificacionSIGPAC.getZonificacionGrupoCultivo() != null
					&& zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles() != null) {
				Iterator it = zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles()
						.iterator();
				while (it.hasNext()) {
					ZonificacionGrupoCultivoDetalle detalle = (ZonificacionGrupoCultivoDetalle) it.next();
					// Si no esta lo añado
					if (!estaInListBigDecimal(detalle.getId().getCodcultivo(), listCodCultivos))
						listCodCultivos.add(detalle.getId().getCodcultivo());
				}
			}
		}

		if (listCodCultivos.size() > 1)
			result = false;
		else
			result = true;

		logger.debug("end - [SigpacDao] isUnicCultivo");
		return result;
	}

	/**
	 * Busco si esta el elemento en la lista
	 * 
	 * @param codCultivo
	 * @param listCodCultivos
	 * @return si existe o no en la lista
	 */
	private boolean estaInListBigDecimal(BigDecimal elem, List<BigDecimal> listElems) {
		logger.debug("init - [SigpacDao] estaInListBigDecimal");

		boolean result = false;

		for (int i = 0; i < listElems.size(); i++) {
			if (listElems.get(i) == elem) {
				result = true;
				break;
			}
		}

		logger.debug("end - [SigpacDao] estaInListBigDecimal");
		return result;
	}
}
