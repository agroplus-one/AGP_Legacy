package com.rsi.agp.dao.models.poliza;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;

public class ParcelaDao extends BaseDaoHibernate implements IParcelaDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Parcela> listParcelas(Parcela parcela) throws DAOException {

		try {

			String[] parametros = new String[] { "poliza.idpoliza", "hoja", "numero", "poligono", "parcela",
					"codprovsigpac", "codtermsigpac", "agrsigpac", "zonasigpac", "poligonosigpac", "parcelasigpac",
					"recintosigpac", "termino.id.codprovincia", "termino.id.codcomarca", "termino.id.codtermino",
					"termino.id.subtermino", "codcultivo", "codvariedad" };

			Object[] valores = new Object[] { parcela.getPoliza().getIdpoliza(), parcela.getHoja(), parcela.getNumero(),
					parcela.getPoligono(), parcela.getParcela(), parcela.getCodprovsigpac(), parcela.getCodtermsigpac(),
					parcela.getAgrsigpac(), parcela.getZonasigpac(), parcela.getPoligonosigpac(),
					parcela.getParcelasigpac(), parcela.getRecintosigpac(),
					parcela.getTermino().getId().getCodprovincia(), parcela.getTermino().getId().getCodcomarca(),
					parcela.getTermino().getId().getCodtermino(), parcela.getTermino().getId().getSubtermino(),
					parcela.getCodcultivo(), parcela.getCodvariedad() };
			return findFiltered(Parcela.class, parametros, valores, null);

		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CapAsegSiniestro> listCapitales(CapAsegSiniestro capitalSiniestro) throws DAOException {
		Session session = obtenerSession();
		try {

			Criteria criteria = session.createCriteria(CapAsegSiniestro.class);

			if (capitalSiniestro.getParcelaSiniestro().getSiniestro().getId() != null) {
				criteria.createAlias("parcelaSiniestro", "ps");
				criteria.add(Restrictions.eq("ps.siniestro.id",
						capitalSiniestro.getParcelaSiniestro().getSiniestro().getId()));
				if (null != capitalSiniestro.getParcelaSiniestro().getHoja()) {
					criteria.add(Restrictions.eq("ps.hoja", capitalSiniestro.getParcelaSiniestro().getHoja()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getNumero()) {
					criteria.add(Restrictions.eq("ps.numero", capitalSiniestro.getParcelaSiniestro().getNumero()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getPoligono()
						&& !"".equals(capitalSiniestro.getParcelaSiniestro().getPoligono())) {
					criteria.add(Restrictions.eq("pa.poligono", capitalSiniestro.getParcelaSiniestro().getPoligono()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getParcela_1()
						&& !"".equals(capitalSiniestro.getParcelaSiniestro().getParcela_1())) {
					criteria.add(
							Restrictions.eq("ps.parcela_1", capitalSiniestro.getParcelaSiniestro().getParcela_1()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodprovsigpac()) {
					criteria.add(Restrictions.eq("ps.codprovsigpac",
							capitalSiniestro.getParcelaSiniestro().getCodprovsigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodtermsigpac()) {
					criteria.add(Restrictions.eq("ps.codtermsigpac",
							capitalSiniestro.getParcelaSiniestro().getCodtermsigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getAgrsigpac()) {
					criteria.add(
							Restrictions.eq("ps.agrsigpac", capitalSiniestro.getParcelaSiniestro().getAgrsigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getZonasigpac()) {
					criteria.add(
							Restrictions.eq("ps.zonasigpac", capitalSiniestro.getParcelaSiniestro().getZonasigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getPoligonosigpac()) {
					criteria.add(Restrictions.eq("ps.poligonosigpac",
							capitalSiniestro.getParcelaSiniestro().getPoligonosigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getParcelasigpac()) {
					criteria.add(Restrictions.eq("ps.parcelasigpac",
							capitalSiniestro.getParcelaSiniestro().getParcelasigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getRecintosigpac()) {
					criteria.add(Restrictions.eq("ps.recintosigpac",
							capitalSiniestro.getParcelaSiniestro().getRecintosigpac()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodprovincia()) {
					criteria.add(Restrictions.eq("ps.codprovincia",
							capitalSiniestro.getParcelaSiniestro().getCodprovincia()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodcomarca()) {
					criteria.add(
							Restrictions.eq("ps.codcomarca", capitalSiniestro.getParcelaSiniestro().getCodcomarca()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodtermino()) {
					criteria.add(
							Restrictions.eq("ps.codtermino", capitalSiniestro.getParcelaSiniestro().getCodtermino()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getSubtermino()) {
					criteria.add(
							Restrictions.eq("ps.subtermino", capitalSiniestro.getParcelaSiniestro().getSubtermino()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodcultivo()) {
					criteria.add(
							Restrictions.eq("ps.codcultivo", capitalSiniestro.getParcelaSiniestro().getCodcultivo()));
				}
				if (null != capitalSiniestro.getParcelaSiniestro().getCodvariedad()) {
					criteria.add(
							Restrictions.eq("ps.codvariedad", capitalSiniestro.getParcelaSiniestro().getCodvariedad()));
				}
			}

			return criteria.list();

		} catch (Exception e) {

			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);

		}
	}

	@Override
	public Parcela getParcelaPoliza(Long idParcela) {
		try {
			return (Parcela) this.get(Parcela.class, idParcela);
		} catch (Exception e) {
			logger.error("Error al obtener la parcela de póliza asociada al id " + idParcela, e);
		}

		return null;
	}

	@Override
	public com.rsi.agp.dao.tables.copy.Parcela getParcelaCopy(Long idParcelaCopy) {
		try {
			return (com.rsi.agp.dao.tables.copy.Parcela) this.get(com.rsi.agp.dao.tables.copy.Parcela.class,
					idParcelaCopy);
		} catch (Exception e) {
			logger.error("Error al obtener la parcela de copy asociada al id " + idParcelaCopy, e);
		}

		return null;
	}
}