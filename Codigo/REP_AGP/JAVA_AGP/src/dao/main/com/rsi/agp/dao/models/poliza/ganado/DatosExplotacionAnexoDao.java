package com.rsi.agp.dao.models.poliza.ganado;

import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCobExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCoberturasExplotacion;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;

public class DatosExplotacionAnexoDao extends BaseDaoHibernate implements IDatosExplotacionAnexoDao {

	@Override
	public Integer calcularNuevoNumeroExplotacion(Long idAnexoExplotacion) throws DAOException {
		Integer numeroExplotacion = null;
		
		Session session = obtenerSession(); 
		Criteria criteria = session.createCriteria(ExplotacionAnexo.class)
				.add(Restrictions.eq("anexoModificacion.id", idAnexoExplotacion))
				.setProjection(Projections.max("numero"));
		numeroExplotacion = (Integer)criteria.uniqueResult();
		
		if(numeroExplotacion!=null)
		{
			numeroExplotacion = numeroExplotacion + 1;
		}
		return numeroExplotacion;
	}
	
	public void deleteCoberturasById(final Long idExplotacionAnexo) throws DAOException {
		Session session = obtenerSession();
		try {
			Query query = session.createSQLQuery(
							"delete from tb_explotaciones_cob_anexo where id_explot_anexo = :idExplotacionAnexo")
					.setLong("idExplotacionAnexo", idExplotacionAnexo);
			query.executeUpdate();
		} catch (Exception e) {
			throw new DAOException(
					"Error al borrar las coberturas de la explotacion de anexo:  "+idExplotacionAnexo, e);
		}
	}
	
	public void deleteCoberturasByIdsCob(final Long idExplotacionAnexo, List<String> idsABorrar) throws DAOException {
		Session session = obtenerSession();
		try {
			for (String idCob:idsABorrar) {
				Query query = session.createSQLQuery(
								"delete from tb_explotaciones_cob_anexo where idexplotacion = :idExplotacion and id = :id")
						.setLong("idExplotacionAnexo", idExplotacionAnexo)
						.setLong("id",new Long(idCob));
				query.executeUpdate();
			}
		} catch (Exception e) {
			throw new DAOException(
					"Error al borrar las coberturas de la explotacion de anexo: "+idExplotacionAnexo, e);
		}
	}
	
	public SWModulosCobExplotacionAnexo saveEnvioCobExplotacion(SWModulosCobExplotacionAnexo envio)	throws DAOException {
		Session session = obtenerSession();
		try {

			session.saveOrUpdate(envio);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad",ex);
		} finally {
		}

		return envio;
	}
	
}
