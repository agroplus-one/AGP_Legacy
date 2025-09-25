package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.Termino;

public class TerminoDao extends BaseDaoHibernate implements ITerminoDao {

	/**
	 * Metodo para obtener los datos de un termino
	 */
	@SuppressWarnings("unchecked")
	public Termino getTermino(BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino,
			Character subtermino) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Termino.class);

			criteria.add(Restrictions.eq("id.codprovincia", codprovincia));
			criteria.add(Restrictions.eq("id.codcomarca", codcomarca));
			criteria.add(Restrictions.eq("id.codtermino", codtermino));
			criteria.add(Restrictions.eq("id.subtermino", subtermino));

			List<Termino> terminos = criteria.list();
			if (terminos.size() > 0)
				return terminos.get(0);

		} catch (Exception ex) {
			logger.error("Error al obtener los datos del termino " + codprovincia + ", " + codcomarca + ", "
					+ codtermino + ", '" + subtermino + "'", ex);
			throw new DAOException("Error al obtener los datos del termino " + codprovincia + ", " + codcomarca + ", "
					+ codtermino + ", '" + subtermino + "'", ex);
		}
		return null;
	}
}