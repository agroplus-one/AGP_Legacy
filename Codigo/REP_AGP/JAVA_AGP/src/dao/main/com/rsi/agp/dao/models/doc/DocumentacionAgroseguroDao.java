package com.rsi.agp.dao.models.doc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.DocsAgroseguroFilter;
import com.rsi.agp.core.jmesa.sort.DocAgroseguroSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Perfil;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.doc.DocAgroseguro;
import com.rsi.agp.dao.tables.doc.DocAgroseguroExtPerm;
import com.rsi.agp.dao.tables.doc.DocAgroseguroPerfiles;
import com.rsi.agp.dao.tables.doc.DocAgroseguroTipo;
import com.rsi.agp.dao.tables.poliza.Linea;

import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("unchecked")
public class DocumentacionAgroseguroDao extends BaseDaoHibernate implements IDocumentacionAgroseguroDao {

	private static final String VISIBLE = "visible";
	private static final String COD_ENTIDAD = "codentidad";
	private static final String NOM_ENTIDAD = "nomentidad";

	@Override
	public Collection<DocAgroseguroTipo> getTiposDocumento() throws DAOException {

		try {
			logger.debug("init - [DocumentacionAgroseguroDao] getTiposDocumento");
			return this.findAll(DocAgroseguroTipo.class);
		} catch (Exception e) {
			logger.error("Error: getTiposDocumento : " + e);
			throw new DAOException("getTiposDocumento : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public Collection<DocAgroseguroTipo> getTiposDocumentoNoAdmin() throws DAOException {

		try {
			logger.debug("init - [DocumentacionAgroseguroDao] getTiposDocumentoNoAdmin");
			return this.getObjects(DocAgroseguroTipo.class, VISIBLE, Constants.DOC_VISIBLE);
		} catch (Exception e) {
			logger.error(new StringBuilder("Error: getTiposDocumento : ").append(e).toString());
			throw new DAOException("getTiposDocumento : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public Collection<DocAgroseguroExtPerm> getExtensiones() throws DAOException {

		try {
			logger.debug("init - [DocumentacionAgroseguroDao] getExtensiones");
			return this.findAll(DocAgroseguroExtPerm.class);
		} catch (Exception e) {

			logger.error("Error: getExtensiones : " + e);
			throw new DAOException("getExtensiones : Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public String getlistaIdsTodos(final DocsAgroseguroFilter consultaFilter, final Usuario usuario)
			throws DAOException {

		String listaids = "";

		try {

			logger.debug("init - [DocumentacionAgroseguroDao] getlistaIdsTodos");
			Session session = obtenerSession();

			String sql = "SELECT  DISTINCT D.ID FROM o02agpe0.TB_DOC_AGROSEGURO D, "
					+ " o02agpe0.TB_DOC_AGROSEGURO_TIPO T " + consultaFilter.getSqlWhere(usuario);

			logger.debug("Valor de sql:" + sql);

			List<BigDecimal> lista = session.createSQLQuery(sql).list();

			for (int i = 0; i < lista.size(); i++) {
				listaids += lista.get(i).toString() + ",";
			}

			return listaids;

		} catch (Exception e) {

			logger.error("Error: getlistaIdsTodos : " + e);
			throw new DAOException("getlistaIdsTodos : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public int getDocsAgroseguroCountWithFilter(final DocsAgroseguroFilter filter, final boolean incluirPerfil,
			final String perfil, final BigDecimal codEntidad) throws DAOException {

		logger.debug("init - [DocumentacionAgroseguroDao] getDocsAgroseguroCountWithFilter");
		logger.debug("**@@** Valor de perfil:" + perfil);
		logger.debug("**@@** Valor de codEntidad:" + codEntidad);

		Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				Criteria criteria = session.createCriteria(DocAgroseguro.class);
				criteria.createAlias("docAgroseguroTipo", "tipo");
				if (incluirPerfil) {
					criteria.createAlias("codDocAgroseguroPerfiles", "docPerfil", Criteria.LEFT_JOIN);
				}

				criteria = filter.execute(criteria);

				/* P0079014 ** MODIF TAM (21.04.2022) * Defecto N�13 ** Inicio */
				/* Incluir Entidad dependiendo del perfil del usuario */
				if (!perfil.equals("AGR-0")) {
					logger.debug("**@@** Entramos en perfil <> 0");
					if (codEntidad != null) {
						logger.debug("**@@** Entramos en codEntidad <> null");
						BigDecimal codEntZero = new BigDecimal(0);
						criteria.add(Restrictions.or(Restrictions.eq("codentidad", codEntidad),
								Restrictions.eq("codentidad", codEntZero)));
						logger.debug("**@@** asignamos la restricci�n de codEntidad para perfil <> 0");
					}
				}
				/* P0079014 ** MODIF TAM (21.04.2022) * Defecto N�13 ** Fin */

				criteria.setProjection(Projections.countDistinct("id")).uniqueResult();
				return criteria.uniqueResult();

			}
		});

		logger.debug("end - [DocumentacionAgroseguroDao] getDocsAgroseguroCountWithFilter");

		return count.intValue();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection<DocAgroseguro> getDocsAgroseguroWithFilterAndSort(final DocsAgroseguroFilter filter,
			final DocAgroseguroSort sort, final int rowStart, final int rowEnd, final boolean incluirPerfil,
			final String perfil, final BigDecimal codEntidad) throws DAOException {

		try {

			logger.debug("init - [DocumentacionAgroseguroDao] getDocsAgroseguroWithFilterAndSort");

			List<DocAgroseguro> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {

				public Object doInHibernate(final Session session) throws HibernateException, SQLException {
					Criteria criteria = session.createCriteria(DocAgroseguro.class);
					criteria.createAlias("docAgroseguroTipo", "tipo");
					if (incluirPerfil) {
						criteria.createAlias("codDocAgroseguroPerfiles", "docPerfil", Criteria.LEFT_JOIN);
					}
					criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
					// Filtro
					criteria = filter.execute(criteria);

					/* P0079014 ** MODIF TAM (21.04.2022) * Defecto N�13 ** Inicio */
					/* Incluir Entidad dependiendo del perfil del usuario */
					if (!perfil.equals("AGR-0")) {
						if (codEntidad != null) {
							BigDecimal codEntZero = new BigDecimal(0);
							criteria.add(Restrictions.or(Restrictions.eq("codentidad", codEntidad),
									Restrictions.eq("codentidad", codEntZero)));
						}
					} else {
						if (codEntidad != null) {
							criteria.add(Restrictions.eq("codentidad", codEntidad));
						}
					}
					/* P0079014 ** MODIF TAM (21.04.2022) * Defecto N�13 ** Fin */

					// Ordenaci�n
					criteria = sort.execute(criteria);
					List<?> aux = criteria.list();
					return Arrays.asList(Arrays.copyOfRange(aux.toArray(), rowStart, rowEnd));
				}
			});

			logger.debug("end - [DocumentacionAgroseguroDao] getDocsAgroseguroWithFilterAndSort");

			return applications;

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public DocAgroseguro getDocsPolizasRC(String especieRC, BigDecimal plan) throws DAOException {
		return (DocAgroseguro) this.obtenerSession().createCriteria(DocAgroseguro.class)
				.createAlias("docAgroseguroTipo", "tipo").add(Restrictions.eq("tipo.descripcion", "Condiciones RC"))
				.add(Restrictions.eq("codplan", plan)).add(Restrictions.eq("codlinea", Constants.CODLINEA_GENERICA))
				.add(Restrictions.eq("descripcion", especieRC)).uniqueResult();
	}

	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio */
	@Override
	public List<Perfil> obtenerListaPerfiles() throws DAOException {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Perfil.class).addOrder(Order.asc("descripcion"));
		return (List<Perfil>) criteria.list();
	}

	@Override
	public void modificarDocAgroseguro(Long id, DocAgroseguro docAgroModif, String codusuario) throws DAOException {
		Session sesion = obtenerSession();

		try {

			Criteria criteria = sesion.createCriteria(DocAgroseguro.class);
			criteria.add(Restrictions.eq("id", id));

			DocAgroseguro docAgro = (DocAgroseguro) criteria.uniqueResult();

			/*** A�adimos las modificaciones */
			BigDecimal codPlan = docAgroModif.getCodplan();

			docAgro.setCodplan(codPlan);
			docAgro.setCodlinea(docAgroModif.getCodlinea());

			/* Pet. 79014 ** MODIF TAM (26.04.2022) ** Defecto N�13 ** Inicio */
			if (docAgroModif.getCodentidad() == null) {
				docAgro.setCodentidad(new BigDecimal(0));
			} else {
				docAgro.setCodentidad(docAgroModif.getCodentidad());
			}
			/* Pet. 79014 ** MODIF TAM (26.04.2022) ** Defecto N�13 ** Fin */

			/* Datos del Tipo Documento */
			Criteria criteria2 = sesion.createCriteria(DocAgroseguroTipo.class);
			criteria2.add(Restrictions.eq("id", docAgroModif.getDocAgroseguroTipo().getId()));

			DocAgroseguroTipo docTipo = (DocAgroseguroTipo) criteria2.uniqueResult();
			docAgro.setDocAgroseguroTipo(docTipo);
			/* Datos del Tipo Documento */

			docAgro.setDescripcion(docAgroModif.getDescripcion());
			docAgro.setFechavalidez(docAgroModif.getFechavalidez());
			docAgro.setFecha(new Date());
			docAgro.setCodusuario(codusuario);

			sesion.saveOrUpdate(docAgro);

		} catch (Exception e) {
			logger.error("Se ha producido un error durante la modificacion del Documento ", e);
			throw new DAOException("Se ha producido un error durante la modificaci�n del Documento", e);
		}

	}

	@Override
	public DocAgroseguroTipo modifTipoDocumento(Long id, String descripcion) throws DAOException {
		Session sesion = obtenerSession();

		try {
			Criteria criteria = sesion.createCriteria(DocAgroseguroTipo.class);
			criteria.add(Restrictions.eq("id", id));

			DocAgroseguroTipo docTipo = (DocAgroseguroTipo) criteria.uniqueResult();

			docTipo.setDescripcion(descripcion);
			sesion.saveOrUpdate(docTipo);

			return docTipo;

		} catch (Exception e) {
			logger.error("Se ha producido un error durante la modificacion del Tipo Documento ", e);
			throw new DAOException("Se ha producido un error durante la modificaci�n del Tipo Documento", e);
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public DocAgroseguroTipo altaTipoDocumento(DocAgroseguroTipo docAgrTipo) throws DAOException {
		Session sesion = obtenerSession();
		List list;

		try {
			String sql = "select max(id)+1 from o02agpe0.tb_doc_agroseguro_tipo ";
			list = sesion.createSQLQuery(sql).list();
			int id = ((BigDecimal) list.get(0)).intValue();

			docAgrTipo.setId(new Long(id));
			sesion.saveOrUpdate(docAgrTipo);

			return docAgrTipo;
		} catch (Exception e) {
			logger.error("Se ha producido un error durante el alta  del Tipo Documento ", e);
			throw new DAOException("Se ha producido un error durante el alta del Tipo Documento", e);
		}
	}

	@Override
	public void grabarPerfilesDocAgr(Set<DocAgroseguroPerfiles> docAgrPerfiles) throws DAOException {
		Session sesion = obtenerSession();

		logger.debug("DocumentacionAgroseguroDao - Dentro de grabarPerfilesDocAgr [INIT] v1.0");
		try {

			for (DocAgroseguroPerfiles docAgro : docAgrPerfiles) {
				sesion.saveOrUpdate(docAgro);
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error durante la modificacion del Tipo Documento ", e);
			throw new DAOException("Se ha producido un error durante la modificaci�n del Tipo Documento", e);
		}

	}

	public void ModifPerfilesDocAgr(Long id, Set<DocAgroseguroPerfiles> docAgrPerfiles) throws DAOException {
		logger.debug("init - [PagoManualDao] editaZonasOficina");

		try {
			logger.debug("Antes de borrrarPerfiles");
			// 1� Borramos los perfiles que tenga el documento
			borrarPerfilesDocAgro(id);

			logger.debug("Antes de Insertar");
			// 2� Insertamos los perfiles del documento.
			if (docAgrPerfiles != null) {
				grabarPerfilesDocAgr(docAgrPerfiles);
			}
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al modificar las zonas de la oficina", ex);
		}
		logger.debug("end - [PagoManualDao] editaZonasOficina");

	}

	public void borrarPerfilesDocAgro(Long id) throws DAOException {
		logger.debug("init - [DocumentacionAgroseguroDao] borrarPerfilesDocAgro");

		String sqlDelete = null;
		Session session = obtenerSession();

		try {
			sqlDelete = " Delete from o02agpe0.tb_doc_agroseguro_perfil docP " + " where docP.Id_Doc_Agro = " + id;

			// Eliminamos los perfiles que haya para ese documento
			session.createSQLQuery(sqlDelete).executeUpdate();

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al dar de alta las zonas de la oficina", ex);
		}
		logger.debug("Fin de guardarZonasOficina");

	}

	@Override
	public String getNombLinea(BigDecimal codLinea) throws DAOException {

		Session session = obtenerSession();
		List<Linea> lstLinea = new ArrayList<Linea>();
		String nombLinea = "";

		Criteria criteria = session.createCriteria(Linea.class);
		criteria.add(Restrictions.eq("codlinea", codLinea));

		lstLinea = criteria.list();

		if (!lstLinea.isEmpty()) {
			nombLinea = lstLinea.get(0).getNomlinea();
		}
		return nombLinea;
	}

	@Override
	public String getNombreEntidad(BigDecimal codEntidad) throws DAOException {
		return (String) this.obtenerSession().createCriteria(Entidad.class)
				.add(Restrictions.eq(COD_ENTIDAD, codEntidad)).setProjection(Projections.property(NOM_ENTIDAD))
				.uniqueResult();
	}

	@Override
	public List<String> obtenerListaPerfilesDoc(Long id) throws DAOException {
		logger.debug("init - [DocumentacionAgroseguroDao] obtenerListaPerfiles");
		Session session = obtenerSession();

		String sql = "select docP.Idperfil from o02agpe0.tb_doc_agroseguro_perfil docP " + " where docP.Id_Doc_Agro = "
				+ id;

		logger.debug("Valor de sqlSel:" + sql);

		List<String> listPerfiles = session.createSQLQuery(sql).list();

		logger.debug("end - [DocumentacionAgroseguroDao] obtenerListaPerfiles");
		return listPerfiles;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String obtenerStringListPerf(Long id) throws DAOException {
		logger.debug("init - [DocumentacionAgroseguroDao] obtenerListaPerfiles");
		Session session = obtenerSession();
		List resultado = new ArrayList();
		String listPerfiles = "";

		String sql = "select docP.Idperfil from o02agpe0.tb_doc_agroseguro_perfil docP " + " where docP.Id_Doc_Agro = "
				+ id;

		logger.debug("Valor de sqlSel:" + sql);

		resultado = session.createSQLQuery(sql).list();

		if (resultado.size() > 0) {
			for (int i = 0; i < resultado.size(); i++) {
				BigDecimal registro = (BigDecimal) resultado.get(i);

				listPerfiles = listPerfiles + registro.toString() + ";";
			}
		}

		int longitud = listPerfiles.length();

		if (longitud > 0) {
			listPerfiles = listPerfiles.substring(0, listPerfiles.length() - 1);
		}

		logger.debug("end - [DocumentacionAgroseguroDao] obtenerListaPerfiles");
		return listPerfiles;
	}

	@Override
	public List<Perfil> obtenerListaTodosPerfiles() throws DAOException {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Perfil.class).addOrder(Order.asc("descripcion"));
		return (List<Perfil>) criteria.list();
	}
	/* Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin */
}