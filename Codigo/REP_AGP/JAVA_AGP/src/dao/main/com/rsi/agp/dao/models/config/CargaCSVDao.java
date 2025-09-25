package com.rsi.agp.dao.models.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.LobHandler;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cvs.CvsAsegurado;
import com.rsi.agp.dao.tables.cvs.CvsCapAseg;
import com.rsi.agp.dao.tables.cvs.CvsCapAsegId;
import com.rsi.agp.dao.tables.cvs.CvsCarga;
import com.rsi.agp.dao.tables.cvs.CvsCargasFichero;
import com.rsi.agp.dao.tables.cvs.CvsDatoVariable;
import com.rsi.agp.dao.tables.cvs.CvsDatoVariableId;
import com.rsi.agp.dao.tables.cvs.CvsParcela;
import com.rsi.agp.dao.tables.cvs.FormCsvCargasBean;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
public class CargaCSVDao extends BaseDaoHibernate implements ICargaCSVDao {

	private static final Log logger = LogFactory.getLog(CargaCSVDao.class);
	private DataSource dataSource;
	private LobHandler lobHandler;
	private ILineaDao lineaDao;
	
	private final String PROVINCIA_FIELD_STR = "provincia";
	private final String COMARCA_FIELD_STR = "comarca";
	private final String TERMINO_FIELD_STR = "termino";
	private final String SUBTERMINO_FIELD_STR = "subtermino";
	private final String CSVASEG_FIELD_STR = "cvsAsegurado";

	public void saveDatosCargaCSV(List<CvsParcela> listCsvAsegParcelas, Reader contenidoFicheroCSV)
			throws DAOException {

		Session session = obtenerSession();

		try {

			for (CvsParcela csvParcelas : listCsvAsegParcelas) {

				// Si existe algun AsegParcela con Entidad, plan, linea, Num_identificacion
				// Asegurado, Provincia, Municipio, Submunicipio,
				// Comarca, Cultivo, Variedad, Poligono, Parcela que coincida con
				// csvAsegParcelas,
				// el anterior se elimina de la base de datos y se inserta el nuevo

				Criteria criteria = session.createCriteria(CvsParcela.class);
				criteria.add(Restrictions.eq(PROVINCIA_FIELD_STR, csvParcelas.getProvincia()));
				criteria.add(Restrictions.eq(COMARCA_FIELD_STR, csvParcelas.getComarca()));
				criteria.add(Restrictions.eq(TERMINO_FIELD_STR, csvParcelas.getTermino()));
				criteria.add(Restrictions.eq(SUBTERMINO_FIELD_STR, csvParcelas.getSubtermino()));

				criteria.add(Restrictions.eq("cultivo", csvParcelas.getCultivo()));
				criteria.add(Restrictions.eq("variedad", csvParcelas.getVariedad()));

				criteria.createAlias(CSVASEG_FIELD_STR, "csvAseg");

				criteria.add(Restrictions.eq("csvAseg.nifAsegurado", csvParcelas.getCvsAsegurado().getNifAsegurado()));

				criteria.createAlias("csvAseg.cvsCarga", "car");
				criteria.add(
						Restrictions.eq("car.entidad", csvParcelas.getCvsAsegurado().getCvsCarga().getEntidad()));
				criteria.add(Restrictions.eq("car.linea", csvParcelas.getCvsAsegurado().getCvsCarga().getLinea()));
				criteria.add(Restrictions.eq("car.plan", csvParcelas.getCvsAsegurado().getCvsCarga().getPlan()));

				List<CvsParcela> listaAsegParcelasExistentes = criteria.list();

				for (CvsParcela csvAsegParcelasAux : listaAsegParcelasExistentes)
					session.delete(csvAsegParcelasAux);

				session.saveOrUpdate(csvParcelas);

			}

			CvsCarga csvCargas = listCsvAsegParcelas.get(0).getCvsAsegurado().getCvsCarga();

			PreparedStatement ps = session.connection()
					.prepareStatement("UPDATE TB_CVS_CARGAS SET CONTENIDO_FICHERO = ? WHERE ID = " + csvCargas.getId());
			lobHandler.getLobCreator().setClobAsCharacterStream(ps, 1, contenidoFicheroCSV, 80000);
			ps.executeUpdate();

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error salvando los datos de carga del CSV", ex);
		}

	}

	/**
	 * Método para obtener el contenido del clob en el que se guarda el fichero de
	 * CSV.
	 * 
	 * @param idCargaCSV
	 *            Identificador de la carga.
	 * @return Reader apuntando al contenido del clob.
	 */
	public Reader getContenidoArchivoCargaCSV(Long idCargaCSV) throws DAOException {

		Session session = obtenerSession();
		Reader reader = null;

		try {
			PreparedStatement ps = session.connection()
					.prepareStatement("SELECT FICHERO FROM TB_CVS_CARGAS_FICHERO WHERE ID_CARGA = " + idCargaCSV);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				reader = lobHandler.getClobAsCharacterStream(rs, 1);
			rs.close();

			if (reader == null)
				throw new DAOException("No se han encontrado datos para el idCargaCSV " + idCargaCSV);
			return reader;
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error leyendo el archivo de carga de la CSV", ex);
		}

	}

	public List<Long> existeParcelasCSVAsegurado(BigDecimal codlinea, BigDecimal codplan, String cifnifAsegurado,
			BigDecimal codentidad, BigDecimal codentidadMed, BigDecimal codsubentidadMed) throws DAOException {

		Session session = obtenerSession();

		try {
			Criteria criteria = session.createCriteria(CvsAsegurado.class);

			criteria.createAlias("cvsCarga", "carga");

			criteria.add(Restrictions.eq("carga.linea", codlinea.intValue()));
			criteria.add(Restrictions.eq("carga.plan", codplan.intValue()));
			criteria.add(Restrictions.eq("carga.entidad", codentidad));
			criteria.add(Restrictions.eq("carga.entMed", codentidadMed.intValue()));
			criteria.add(Restrictions.eq("carga.subentMed", codsubentidadMed.intValue()));
			criteria.add(Restrictions.eq("nifAsegurado", cifnifAsegurado));

			criteria.setProjection(Projections.property("id"));

			return (criteria.list());

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CargaCSV.existeParcelasCSVAseg] Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	public List<CvsCarga> listarCargas(CvsCarga csvCarga) throws DAOException {

		Session session = obtenerSession();

		try {

			Criteria criteria = session.createCriteria(CvsCarga.class);

			// Entidad
			if (csvCarga.getEntidad() != null) {
				criteria.add(Restrictions.eq("entidad", csvCarga.getEntidad()));
			}
			// Si no se ha filtrado por 'Entidad' y el usuario es perfil 5, se filtra por
			// todas las entidades del grupo
			else if (csvCarga.getGrupoEntidades() != null && !csvCarga.getGrupoEntidades().isEmpty()) {
				criteria.add(Restrictions.in("entidad", csvCarga.getGrupoEntidades()));
			}

			// Entidad mediadora
			if (csvCarga.getEntMed() != null)
				criteria.add(Restrictions.eq("entMed", csvCarga.getEntMed()));

			// Entidad mediadora
			if (csvCarga.getSubentMed() != null)
				criteria.add(Restrictions.eq("subentMed", csvCarga.getSubentMed()));

			// Plan
			if (csvCarga.getPlan() != null)
				criteria.add(Restrictions.eq("plan", csvCarga.getPlan()));

			// Línea
			if (csvCarga.getLinea() != null)
				criteria.add(Restrictions.eq("linea", csvCarga.getLinea()));

			// Fichero PAC
			if (csvCarga.getNombreFichero() != null && csvCarga.getNombreFichero().length() > 0)
				criteria.add(
						Restrictions.ilike("nombreFichero", csvCarga.getNombreFichero().trim(), MatchMode.ANYWHERE));

			return criteria.list();

		} catch (Exception ex) {
			throw new DAOException("[CargaCSV.listarCargas] Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	public List<RelEspeciesSCEspeciesST> buscarST(BigDecimal lineaSeguroId, BigDecimal codLinea, BigDecimal codPlan,
			BigDecimal codCultST, BigDecimal codVarST) throws DAOException {
		Session session = obtenerSession();

		try {
			Criteria criteria = session.createCriteria(RelEspeciesSCEspeciesST.class);

			criteria.add(Restrictions.eq("id.lineaseguroid", lineaSeguroId));
			criteria.add(Restrictions.eq("id.codlinea", codLinea));
			criteria.add(Restrictions.eq("id.codplan", codPlan));
			criteria.add(Restrictions.eq("id.codcultivost", codCultST));
			criteria.add(Restrictions.eq("id.codvariedadst", codVarST));

			return criteria.list();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CargaCSV.buscarST] Se ha producido un error durante el acceso a la base de datos", ex);
		}

	}

	@SuppressWarnings("unlikely-arg-type")
	public List<CvsParcela> filtrarPacProvComTerSubterm(Map<String, Object> filtro, List<CvsParcela> listParcelasCsv)
			throws DAOException {
		logger.debug("--- Init filtrarPacProvComTerSubterm");
		List<CvsParcela> filtrado = new ArrayList<CvsParcela>(0);
		ArrayList<String> listProv = new ArrayList<String>();
		ArrayList<String> listCom = new ArrayList<String>(0);
		ArrayList<String> listTerm = new ArrayList<String>(0);
		ArrayList<Character> listSubTer = new ArrayList<Character>(0);

		try {
			if (!filtro.isEmpty()) {
				for (Iterator<String> recorrofiltro = filtro.keySet().iterator(); recorrofiltro.hasNext();) {
					String propiedad = recorrofiltro.next();
					if (SUBTERMINO_FIELD_STR.equals(propiedad)) {
						ArrayList<Character> valorSubTer = (ArrayList<Character>) filtro.get(propiedad);
						listSubTer = valorSubTer;
					} else {
						ArrayList<String> valor = (ArrayList<String>) filtro.get(propiedad);
						if (PROVINCIA_FIELD_STR.equals(propiedad)) {
							listProv = valor;
						} else if (COMARCA_FIELD_STR.equals(propiedad)) {
							listCom = valor;
						} else if (TERMINO_FIELD_STR.equals(propiedad)) {
							listTerm = valor;
						}
					}
				}

				for (CvsParcela csvParcela : listParcelasCsv) {
					boolean existe = true;

					if (!listProv.isEmpty() && listProv.indexOf(csvParcela.getProvincia()) < 0) {
						existe = false;
					}
					if (!listCom.isEmpty() && existe && listCom.indexOf(csvParcela.getComarca()) < 0) {
						existe = false;
					}
					if (!listTerm.isEmpty() && existe && listTerm.indexOf(csvParcela.getTermino()) < 0) {
						existe = false;						
					}
					if (!listSubTer.isEmpty() && existe && listSubTer.indexOf(csvParcela.getSubtermino()) < 0) {
						existe = false;
					}

					if (existe) {
						filtrado.add(csvParcela);
					}
				}
			} else {
				filtrado = listParcelasCsv;
			}
			logger.debug("--- Exit filtrarCsvProvComTerSubterm");
			return filtrado;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CargaCSV.filtrarPacProvComTerSubterm] Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	// ---------------------------------------------
	// [INICIO] Listados filtrados por clase
	// ---------------------------------------------
	public List getParcelasCsv(String nifcif, BigDecimal codlinea, BigDecimal codplan,
			BigDecimal codentidad) throws DAOException {
		List<BigDecimal> provincias = new ArrayList<BigDecimal>();
		List<BigDecimal> comarcas = new ArrayList<BigDecimal>();
		List<BigDecimal> terminos = new ArrayList<BigDecimal>();
		List<Character> subterminos = new ArrayList<Character>();

		Long lineaseguroid = null;

		try {

			lineaseguroid = this.lineaDao.getLineaSeguroId(codlinea, codplan);

			// elementos por los que filtro
			provincias = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.codprovincia"); // provincias validas
			comarcas = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.codcomarca"); // comarcas "
			terminos = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.codtermino"); // terminos "
			subterminos = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.subtermino"); // subterminos "

			// cultivos = getCampoClaseDetalle(null, lineaseguroid,
			// "claseDetalle.cultivo.id.codcultivo"); // cultivos "
			// variedades = getCampoClaseDetalle(null, lineaseguroid,
			// "claseDetalle.variedad.id.codvariedad"); // variedades "

			Session session = obtenerSession();
			Criteria criteria = getCriteriaParcelasCsv(session, provincias, comarcas, terminos, subterminos, null, null,
					nifcif, codlinea, codplan, codentidad);

			return criteria.list();

		} catch (Exception ex) {
			logger.error("[CargaCSV.getParcelasCsv] Se ha producido un error durante el acceso a la base de datos: ", ex);
			throw new DAOException("[ERROR] en CargaCSVDao.java metodo getParcelasCsv]");
		}
	}

	public Criteria getCriteriaParcelasCsv(Session session, List provincias, List comarcas, List terminos,
			List subterminos, List cultivos, List variedades, String nifcif, BigDecimal codlinea, BigDecimal codplan,
			BigDecimal codentidad) {

		String cad = "";
		Criteria criteria = session.createCriteria(CvsParcela.class);
		criteria.createAlias(CSVASEG_FIELD_STR, "paseg");
		criteria.createAlias("paseg.csvCargas", "papc");

		if (nifcif != null && !"".equals(nifcif)) {
			criteria.add(Restrictions.eq("paseg.nifcif", nifcif));
		}

		if (codlinea != null) {
			criteria.add(Restrictions.eq("papc.codlinea", codlinea));
		}

		if (codplan != null) {
			criteria.add(Restrictions.eq("papc.codplan", codplan));
		}

		if (codentidad != null) {
			criteria.add(Restrictions.eq("papc.codEntidad", codentidad));
		}

		// provincias
		if (provincias != null && !provincias.contains(new BigDecimal("99"))) {
			if (!provincias.isEmpty()) {
				provincias.add(new BigDecimal("99"));
			}
			// cambiar la lista de BigDecimal a String
			// Rellenar por la izquierda con '0' hasta el maximo de caracteres por campo
			List<String> listProv = new ArrayList<String>();
			Iterator iter = provincias.iterator();
			while (iter.hasNext()) {
				cad = (iter.next().toString());
				Integer temp = Integer.valueOf(cad);
				listProv.add((String.format("%02d", temp)));
			}
			criteria.add(Restrictions.in(PROVINCIA_FIELD_STR, listProv));
		}

		// comarcas
		if (comarcas != null && !comarcas.contains(new BigDecimal("99"))) {
			if (!comarcas.isEmpty()) {
				comarcas.add(new BigDecimal("99"));
			}
			// cambiar la lista de BigDecimal a String
			List<String> listCom = new ArrayList<String>();
			Iterator iter = comarcas.iterator();
			while (iter.hasNext()) {
				cad = (iter.next().toString());
				Integer temp = Integer.valueOf(cad);
				listCom.add((String.format("%02d", temp)));
			}
			criteria.add(Restrictions.in(COMARCA_FIELD_STR, listCom));
		}

		// teminos
		if (terminos != null && !terminos.contains(new BigDecimal("999"))) {
			if (!terminos.isEmpty()) {
				terminos.add(new BigDecimal("999"));
			}
			// cambiar la lista de BigDecimal a String
			List<String> listTerm = new ArrayList<String>();
			Iterator iter = terminos.iterator();
			while (iter.hasNext()) {
				cad = (iter.next().toString());
				Integer temp = Integer.valueOf(cad);
				listTerm.add((String.format("%03d", temp)));
			}
			criteria.add(Restrictions.in(TERMINO_FIELD_STR, listTerm));
		}

		// subterminos
		if (subterminos != null && !subterminos.contains('9')) {
			if (!subterminos.isEmpty()) {
				subterminos.add(new Character('9'));
			}
			criteria.add(Restrictions.in(SUBTERMINO_FIELD_STR, subterminos));
		}

		// cultivos
		if (cultivos != null && !cultivos.contains(new BigDecimal("999"))) {
			if (!cultivos.isEmpty()) {
				cultivos.add(new BigDecimal("999"));
			}
			// cambiar la lista de BigDecimal a String
			List<String> listCult = new ArrayList<String>();
			Iterator iter = cultivos.iterator();
			while (iter.hasNext()) {
				cad = (iter.next().toString());
				Integer temp = Integer.valueOf(cad);
				listCult.add((String.format("%03d", temp)));
			}
			criteria.add(Restrictions.in("cultivo", listCult));
		}
		// variedades
		if (variedades != null && !variedades.contains(new BigDecimal("999"))) {
			if (!cultivos.isEmpty()) {
				variedades.add(new BigDecimal("999"));
			}
			// cambiar la lista de BigDecimal a String
			List<String> listVar = new ArrayList<String>();
			Iterator iter = variedades.iterator();
			while (iter.hasNext()) {
				cad = (iter.next().toString());
				Integer temp = Integer.valueOf(cad);
				listVar.add((String.format("%03d", temp)));
			}
			criteria.add(Restrictions.in("variedad", listVar));
		}
		return criteria;
	}

	/**
	 * METODO GENERICO
	 * 
	 * @param campo
	 * @param clase
	 */
	public List getCampoClaseDetalle(Long clase, String campo) {
		Session session = obtenerSession();
		String query = "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle WHERE claseDetalle.clase.id =:clase_";
		Query hql = session.createQuery(query);
		hql.setParameter("clase_", clase);
		return hql.list();
	}

	/**
	 * METODO GENERICO
	 * 
	 * @param campo
	 * @param clase
	 * @param linea
	 *            seguro id
	 */
	public List getCampoClaseDetalle(Long clase, Long lineaseguroid, String campo) {
		Session session = obtenerSession();
		String query = "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle";
		if (clase != null) {
			query += " WHERE claseDetalle.clase.id =:_clase_ AND claseDetalle.clase.linea.lineaseguroid =:_lineaseguroid_";
		} else {
			query += " WHERE claseDetalle.clase.linea.lineaseguroid =:_lineaseguroid_";
		}

		Query hql = session.createQuery(query);
		if (clase != null) {
			hql.setParameter("_clase_", clase);
		}
		hql.setParameter("_lineaseguroid_", lineaseguroid);
		return hql.list();
	}

	public List getlstSisCultClaseDetalle(BigDecimal clase, Long lineaseguroid, String campo) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Clase.class);
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("clase", clase));
		Clase claseAux = (Clase) criteria.uniqueResult();

		// MPM 26-09 - Se controla que la clase no sea nula
		String query = "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle WHERE "
				+ (claseAux != null && claseAux.getId() != null ? " claseDetalle.clase.id =:_clase_ AND " : "")
				+ " claseDetalle.clase.linea.lineaseguroid =:_lineaseguroid_";

		Query hql = session.createQuery(query);
		if (claseAux != null && claseAux.getId() != null)
			hql.setParameter("_clase_", claseAux.getId());
		hql.setParameter("_lineaseguroid_", lineaseguroid);

		return hql.list();
	}

	// ---------------------------------------------
	// [FIN] Listados filtrados por clase
	// ---------------------------------------------

	public void saveDatoVarParcela(DatoVariableParcela nDat) throws DAOException {
		Session session = obtenerSession();
		try {
			session.save(nDat);
		} catch (Exception ex) {
			logger.error("[CargaCSV.saveDatoVarParcela] Se ha producido un error durante el acceso a la base de datos ", ex);
			throw new DAOException("[ERROR] al acceder a la BBDD.", ex);
		}
	}

	public void actualizaCLOBCsvCargas(List<CvsParcela> listCsvAsegParcelas, final String clob) throws DAOException {
		logger.info("init - actualizaCLOBCsvCargas");
		try {
			CvsCarga csvCargas = listCsvAsegParcelas.get(0).getCvsAsegurado().getCvsCarga();
			if (!StringUtils.nullToString(clob).equals("")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				jdbcTemplate.update("UPDATE TB_CVS_CARGAS SET CONTENIDO_FICHERO=? WHERE ID=" + csvCargas.getId(),
						new PreparedStatementSetter() {
							public void setValues(PreparedStatement ps) throws SQLException {
								lobHandler.getLobCreator().setClobAsString(ps, 1, clob);
							}
						});
			}
		} catch (Exception ex) {
			logger.error("[CargaCSV.actualizaCLOB] Se ha producido un error durante el acceso a la base de datos ", ex);
			throw new DAOException("Se ha producido un error en acceso a la BBDD", ex);
		}
		logger.info("end - actualizaCLOBCsvCargas");
	}

	/**
	 * Llama al procedimiento almacenado encargado de copiar las parcelas de PAC
	 * asocidas a la lista de ids de asegurados de PAC indicada como parámetro en la
	 * póliza asociada al id 'idPoliza'
	 * 
	 * @param idPoliza
	 * @param idClase
	 * @param listaIdPacAseg
	 * @param listaDVDefecto
	 * @return
	 * @throws DAOException
	 */
	public String cargaParcelasPolizaDesdeCSV(Long idPoliza, Long idClase, String listaIdCsvAseg, String listaDVDefecto)
			throws DAOException {

		Session session = null;

		try {
			session = obtenerSession();

			String query = "call PQ_CARGA_PARCELAS_CSV.cargaParcelasPolizaDesdeCSV (?,?,?,?,?)";
			CallableStatement statement = session.connection().prepareCall("{" + query + "}");
			statement.setLong(1, idPoliza);
			statement.setLong(2, idClase);
			statement.setString(3, listaIdCsvAseg);
			statement.setString(4, listaDVDefecto);
			statement.registerOutParameter(5, Types.VARCHAR);

			logger.info("[cargaParcelasPolizaDesdeCSV] Llamada al PL: " + query + " con los parametros " + idPoliza
					+ ", " + idClase + ", '" + listaIdCsvAseg + "', '" + listaDVDefecto + "' ");

			statement.execute();

			String resultado = statement.getString(5);
			logger.info(
					"[cargaParcelasPolizaDesdeCSV] Resultado carga CSV: '" + StringUtils.nullToString(resultado) + "'");

			return (StringUtils.nullToString(resultado));

		} catch (Exception ex) {
			logger.error("[cargaParcelasPolizaDesdeCSV] Se produjo el siguiente error al ejecutar el procedimiento '",
					ex);
			throw new DAOException(ex);
		}

	}

	public boolean existeESMedEnt(BigDecimal entMed, BigDecimal subentMed) {
		Session session = this.obtenerSession();

		String sql = "SELECT COUNT(*) from TB_SUBENTIDADES_MEDIADORAS sm where sm.codentidad=" + entMed + " and "
				+ "sm.codsubentidad=" + subentMed;

		List list = session.createSQLQuery(sql).list();

		return ((BigDecimal) list.get(0)).intValue() > 0;
	}

	public boolean existeArchivoCargado(final String filename) {
		Session session = this.obtenerSession();

		String sql = "select count(*) from TB_CVS_CARGAS c where UPPER(NOMBRE_FICHERO) = '" + filename.toUpperCase()
				+ "'";
		List list = session.createSQLQuery(sql).list();

		return ((BigDecimal) list.get(0)).intValue() > 0;
	}

	public void dropCargaCSV(BigDecimal idCargaCsv) throws DAOException {
		Session session = obtenerSession();
		try {
			String sql = "DELETE TB_CVS_CARGAS WHERE ID = " + idCargaCsv;
			session.createSQLQuery(sql).executeUpdate();
		} catch (Exception excepcion) {
			logger.error("Error al borrar el CSV por id " + idCargaCsv, excepcion);
			throw new DAOException("Error al borrar el CSV por id " + idCargaCsv, excepcion);
		}
	}

	public Map<BigDecimal, BigDecimal> getDatosVarPantalla(Long lineaSeguroId) throws DAOException {

		List<BigDecimal> list = new ArrayList<BigDecimal>();
		Map<BigDecimal, BigDecimal> codConceptos = new HashMap<BigDecimal, BigDecimal>();
		try {
			Session session = this.obtenerSession();

			String sql = " select distinct codconcepto from tb_pantallas_configurables p, tb_configuracion_campos c "
					+ " where p.idpantallaconfigurable = c.idpantallaconfigurable " + " and c.lineaseguroid = "
					+ lineaSeguroId + " and p.idpantalla = 7";

			list = session.createSQLQuery(sql).list();
			for (int i = 0; i < list.size(); i++) {
				codConceptos.put(list.get(i), list.get(i));
			}

		} catch (Exception excepcion) {
			logger.error("Error al recuperar los datos variables", excepcion);
			throw new DAOException("Error al recuperar los datos variables", excepcion);
		}
		return codConceptos;
	}

	@Override
	public List<ModuloPoliza> getModulosPoliza(Long idpoliza, Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ModuloPoliza.class);
			criteria.add(Restrictions.eq("id.idpoliza", idpoliza));
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));

			return criteria.list();
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CargaCSV.getModulosPoliza] Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	@Override
	public String executeStoreProcCargarCSV(String fichero, String codUsuario, FormCsvCargasBean form)
			throws DAOException, IOException {

		Session session = obtenerSession();

		logger.info("** executeStoreProcCargarCargar: Inicio");

		String linea = "";
		String cvsSplitBy = ";";
		String[] l;
		Character subtermino;

		CvsCarga cargaCsv = new CvsCarga();
		CvsAsegurado aseguradoCsv = new CvsAsegurado();
		CvsCapAseg capAseguradoCsv = new CvsCapAseg();
		CvsCargasFichero cargasFicheroCsv = new CvsCargasFichero();
		CvsParcela parcelaCsv = new CvsParcela();

		/*ESC-10106 DNF necesito obtener la entidad a partir de la entidad mediadora y subentidad mediadora*/
		BigDecimal entidad = getEntidad(form.getEntMed(), form.getSubentMed());
		
		cargaCsv.setPlan(form.getPlan().intValue());
		cargaCsv.setLinea(form.getLinea().intValue());
		
		/*Seteo la entidad que he obtenido antes ESC-10106 DNF 06/Julio/2020*/
		cargaCsv.setEntidad(entidad);
		
		cargaCsv.setEntMed(form.getEntMed().intValue());
		cargaCsv.setSubentMed(form.getSubentMed().intValue());
		cargaCsv.setNombreFichero(fichero);
		cargaCsv.setUsuario(codUsuario);
		cargaCsv.setFechaCarga(new Date());
		cargaCsv.setCvsCargasFichero(cargasFicheroCsv);

		cargasFicheroCsv.setCvsCarga(cargaCsv);
		// Guardamos el fichero en tb_cargas_fichero

		String bytesFichero = new String(form.getFile().getBytes());
		cargasFicheroCsv.setFichero(Hibernate.createClob(bytesFichero));

		try {

			logger.info("** executeStoreProcCargarCargar: Comenzamos la lectura del fichero");
			ArrayList<CvsAsegurado> listaAseguradosProcesados = new ArrayList<CvsAsegurado>();

			// Abrimos el fichero en la ruta donde se ha guardado
			logger.info("** executeStoreProcCargarCargar: Abrimos el fichero csv");
			ResourceBundle bundle = ResourceBundle.getBundle("agp");
			String serverLocation = bundle.getString("ruta.defecto.fichero.PAC2");

			try (BufferedReader br = new BufferedReader(new FileReader(serverLocation + "/" + fichero))) {
				linea = br.readLine();
				l = linea.split(cvsSplitBy);
				// Comprobamos de la linea 1 que estan todos los campos y en el orden adecuado
				if (!compruebaCabeceraCSV(l)) {
					logger.error("Error: Los titulos de la cabecera no son correctos o no estan en el orden correcto");
					throw new IOException(
							"Los titulos de la cabecera no son correctos o no estan en el orden correcto");
				}

				List<CvsAsegurado> listAsegurados = new ArrayList<CvsAsegurado>();
				List<CvsParcela> listParcelas = new ArrayList<CvsParcela>();
				List<CvsCapAseg> listCapAsegurados = new ArrayList<CvsCapAseg>();
				List<CvsDatoVariable> listDatosVariables = new ArrayList<CvsDatoVariable>();

				while ((linea = br.readLine()) != null) {

					l = linea.split(cvsSplitBy);

					/* Si la linea no esta vacia, procedemos */
					if (l.length != 0) {
						if ((l[15] == null) || (l[15].equals(""))) {
							subtermino = ' ';
						} else {
							subtermino = l[15].charAt(0);
						}

						// Comprobamos si la parcela tiene los datos obligatorios
						if ((l[1].equals("")) || (l[3].equals("")) || (l[4].equals("")) || (l[5].equals(""))
								|| (l[6].equals("")) || (l[7].equals("")) || (l[8].equals("")) || (l[9].equals(""))
								|| (l[10].equals("")) || (l[11].equals("")) || (l[12].equals("")) || (l[13].equals(""))
								|| (l[14].equals("")) || (l[17].equals("")) || (l[18].equals("")) || (l[19].equals(""))
								|| (l[20].equals(""))) {

							logger.error("Error: Los datos de la parcela obligatorios no estan completos");
							br.close();
							throw new IOException("Los datos de la parcela obligatorios no estan completos");

						} else if (!(existeTermino(Byte.parseByte(l[12]), Byte.parseByte(l[13]),
								Short.parseShort(l[14]), subtermino))) {
							logger.error("Error: Termino inexistente");
							br.close();

							throw new IOException("Termino inexistente");
						} else {
							Integer tipoCapital = Integer.parseInt(l[17]);
							Integer numeroParcela = Integer.parseInt(l[1]);
							CvsParcela parcelaCapAseg = buscaParcelaCorrespondienteCapAseg(listParcelas, numeroParcela);

							if ((parcelaCapAseg != null) && (tipoCapital < 100)) {
								// Parcela ya guardada, es un capital asegurado nuevo asignado a esta

								capAseguradoCsv = new CvsCapAseg();
								capAseguradoCsv.setId(new CvsCapAsegId());

								// Rellenamos los datos del capital asegurado
								capAseguradoCsv.setSuperficie(new BigDecimal(l[18].replace(",", ".")));
								capAseguradoCsv.setProduccion(Long.parseLong(l[19]));
								capAseguradoCsv.setPrecio(new BigDecimal(l[20].replace(",", ".")));

								// Rellenamos y guardamos los capitales asegurados. Buscamos la parcela al que
								// corresponde
								capAseguradoCsv.setCvsParcela(parcelaCapAseg);
								capAseguradoCsv.setCvsAsegurado(parcelaCapAseg.getCvsAsegurado());
								capAseguradoCsv.getId().setCodtipocapital(Integer.parseInt(l[17]));
								listCapAsegurados.add(capAseguradoCsv);
								// Rellenamos los datos variables
								listDatosVariables.addAll(guardaDatosVariables(capAseguradoCsv, l));

							} else {
								// Es parcela o intalacion nueva
								// Creamos nuevos objetos
								capAseguradoCsv = new CvsCapAseg();
								capAseguradoCsv.setId(new CvsCapAsegId());
								parcelaCsv = new CvsParcela();

								// Si el asegurado ya ha sido guardado, lo cogemos de la lista de asegurados
								// almacenados
								// si no, creamos uno nuevo
								aseguradoCsv = getAseguradoYaProcesado(listaAseguradosProcesados, l[0]);
								if (aseguradoCsv == null) {
									aseguradoCsv = new CvsAsegurado();
								}

								// Rellenamos los datos de la parcela
								parcelaCsv.setNumero(Integer.parseInt(l[1]));
								parcelaCsv.setNombre(l[2]);
								parcelaCsv.setCultivo(Short.parseShort(l[3]));
								parcelaCsv.setVariedad(Short.parseShort(l[4]));
								parcelaCsv.setProvinciaSigpac(Byte.parseByte(l[5]));
								parcelaCsv.setTerminoSigpac(Short.parseShort(l[6]));
								parcelaCsv.setAgregadoSigpac(Short.parseShort(l[7]));
								parcelaCsv.setZonaSigpac(Byte.parseByte(l[8]));
								parcelaCsv.setPoligonoSigpac(Short.parseShort(l[9]));
								parcelaCsv.setParcelaSigpac(Integer.parseInt(l[10]));
								parcelaCsv.setRecintoSigpac(Integer.parseInt(l[11]));
								parcelaCsv.setProvincia(Byte.parseByte(l[12]));
								parcelaCsv.setComarca(Byte.parseByte(l[13]));
								parcelaCsv.setTermino(Short.parseShort(l[14]));

								if (StringUtils.isNullOrEmpty(l[15])) {
									parcelaCsv.setSubtermino(' ');
								} else {
									parcelaCsv.setSubtermino(l[15].charAt(0));
								}

								if (!StringUtils.isNullOrEmpty(l[16])) {
									parcelaCsv.setParcAgricola(Integer.parseInt(l[16]));
								}

								// Rellenamos los datos del capital asegurado
								capAseguradoCsv.setSuperficie(new BigDecimal(l[18].replace(",", ".")));
								capAseguradoCsv.setProduccion(Long.parseLong(l[19]));
								capAseguradoCsv.setPrecio(new BigDecimal(l[20].replace(",", ".")));

								// Rellenamos los datos del asegurado y lo guardamos
								aseguradoCsv.setNifAsegurado(l[0]);
								aseguradoCsv.setCvsCarga(cargaCsv);
								listaAseguradosProcesados.add(aseguradoCsv);

								listAsegurados.add(aseguradoCsv);

								// Guardamos la parcela
								parcelaCsv.setCvsAsegurado(aseguradoCsv);
								listParcelas.add(parcelaCsv);

								// Rellenamos y guardamos los capitales asegurados
								capAseguradoCsv.setCvsParcela(parcelaCsv);
								capAseguradoCsv.setCvsAsegurado(aseguradoCsv);
								capAseguradoCsv.getId().setCodtipocapital(Integer.parseInt(l[17]));
								listCapAsegurados.add(capAseguradoCsv);
								// session.save(capAseguradoCsv);
								// Rellenamos los datos variables
								listDatosVariables.addAll(guardaDatosVariables(capAseguradoCsv, l));
							}
						}
					}
				}

				logger.info("** executeStoreProcCargarCargar: Guardamos los registros de la carga");
				session.save(cargasFicheroCsv);
				session.save(cargaCsv);

				for (CvsAsegurado aseg : listAsegurados) {
					session.save(aseg);
				}
				for (CvsParcela parc : listParcelas) {
					session.save(parc);
				}
				for (CvsCapAseg capAseg : listCapAsegurados) {
					capAseg.getId().setIdCvsParcela(capAseg.getCvsParcela().getId());
					capAseg.getId().setIdCvsAseg(capAseg.getCvsAsegurado().getId());
					session.save(capAseg);
				}
				for (CvsDatoVariable dv : listDatosVariables) {
					dv.getId().setIdCvsAseg(dv.getCvsCapAseg().getId().getIdCvsAseg());
					dv.getId().setIdCvsParcela(dv.getCvsCapAseg().getId().getIdCvsParcela());
					dv.getId().setCodtipocapital(dv.getCvsCapAseg().getId().getCodtipocapital());
					session.save(dv);
				}
			}
		} catch (FileNotFoundException e1) {
			logger.error(e1);
			throw new FileNotFoundException("Se ha producido un error al acceder al fichero");
		} catch (IOException e) {
			logger.error(e);
			throw new IOException(e.getMessage(), e);
		}
		
		return "";

	}

	private boolean compruebaCabeceraCSV(String[] linea) {

		boolean coincidencia = true;
		Collator c = Collator.getInstance(new Locale("es"));
		c.setStrength(Collator.PRIMARY);

		// Creamos el array con los nombres de los campos
		String[] listNombres = { "DNI Asegurado", "Nº Parcela", "Nombre Parcela", "Cod Cultivo", "Cod Variedad",
				"Provincia SIGPAC", "Termino SIGPAC", "Agregado SIGPAC", "Zona SIGPAC", "Poligono SIGPAC",
				"Parcela SIGPAC", "Recinto SIGPAC", "Provincia", "Comarca", "Termino", "Subtermino", "Parcela agricola",
				"Tipo capital",	"Superficie", "Produccion", "Precio", "Denominacion origen", "Destino", "Edad", 
				"Fecha recoleccion", "Fecha siembra/trasplante", "Tipo marco plantacion", "Numero unidades", "Sistema cultivo",
				"Sistema Conduccion", "Practica cultural", "Fecha fin garantias", "Rotacion", "Tipo plantacion",
				"Sistema produccion", "Nº años desde poda", "Ciclo cultivo", "Codigo reduccion rdtos",
				"Sistema proteccion", "Tipo terreno", "Tipo masa", "Pendiente", "Codigo IGP", "Metros lineales",
				"Metros cuadrados", "Tipo instalacion", "Material cubierta", "Edad cubierta", "Material estructura",
				"Edad estructura", "Nº años desde descorche", "NIF socio" };

		for (int i = 0; i < listNombres.length; i++) {
			if (!(c.equals(listNombres[i], linea[i]))) {
				coincidencia = false;
			}
		}

		return coincidencia;
	}

	private List<CvsDatoVariable> guardaDatosVariables(CvsCapAseg cvsCapAseg, String[] linea) {

		CvsDatoVariable datoVariable;
		List<CvsDatoVariable> listDatosVariables = new ArrayList<CvsDatoVariable>();

		// Creamos el array con los conceptos
		final short[] listConceptos = { ConstantsConceptos.CODCPTO_DENOMORIGEN, ConstantsConceptos.CODCPTO_DESTINO,
				ConstantsConceptos.CODCPTO_EDAD, ConstantsConceptos.CODCPTO_FECHA_RECOLEC,
				ConstantsConceptos.CODCPTO_FECSIEMBRA, ConstantsConceptos.CODCPTO_TIPMARCOPLANT,
				ConstantsConceptos.CODCPTO_UNIDADES, ConstantsConceptos.CODCPTO_SISTCULTIVO,
				ConstantsConceptos.CODCPTO_SISTCOND, ConstantsConceptos.CODCPTO_PRACTCULT,
				ConstantsConceptos.CODCPTO_FEC_FIN_GARANT, ConstantsConceptos.CODCPTO_ROTACION,
				ConstantsConceptos.CODCPTO_TIPO_PLANTACION, ConstantsConceptos.CODCPTO_SISTEMA_PRODUCCION,
				ConstantsConceptos.CODCPTO_NUMANIOSPODA, ConstantsConceptos.CODCPTO_CICLOCULTIVO,
				ConstantsConceptos.CODCPTO_REDUCCION_RDTOS, ConstantsConceptos.CODCPTO_SISTEMA_PROTECCION,
				ConstantsConceptos.CODCPTO_TIPOTERRENO, ConstantsConceptos.CODCPTO_TIPOMASA,
				ConstantsConceptos.CODCPTO_PENDIENTE, ConstantsConceptos.CODCPTO_IGP,
				ConstantsConceptos.CODCPTO_METROS_LINEALES, ConstantsConceptos.CODCPTO_METROS_CUADRADOS,
				ConstantsConceptos.CODCPTO_TIPOINSTAL, ConstantsConceptos.CODCPTO_MATCUBIERTA,
				ConstantsConceptos.CODCPTO_EDAD_CUBIERTA, ConstantsConceptos.CODCPTO_MATESTRUCTURA,
				ConstantsConceptos.CODCPTO_EDAD_ESTRUCTURA, ConstantsConceptos.CODCPTO_ANHOS_DESCORCHE,
				ConstantsConceptos.CODCPTO_NIF_SOCIO };

		final int NUM_COLS_BEFORE_DVS = 21;
		
		// Comenzamos a guardar los datos variables
		for (int i = NUM_COLS_BEFORE_DVS; i < linea.length; i++) {

			datoVariable = new CvsDatoVariable();
			datoVariable.setCvsCapAseg(cvsCapAseg);
			datoVariable.setId(new CvsDatoVariableId());

			if ((linea[i] != null) && !(linea[i].equals(""))) {

				datoVariable.getId().setCodconcepto(listConceptos[i - NUM_COLS_BEFORE_DVS]);
				datoVariable.setValor(linea[i]);

				listDatosVariables.add(datoVariable);
			}
		}
		return listDatosVariables;
	}

	private CvsAsegurado getAseguradoYaProcesado(ArrayList<CvsAsegurado> listaAseguradosProcesados, String nifAseg) {

		for (CvsAsegurado aseg : listaAseguradosProcesados) {
			if (aseg.getNifAsegurado().equals(nifAseg)) {
				return aseg;
			}
		}
		return null;
	}

	public boolean existeTermino(Byte provincia, Byte comarca, Short termino, Character subtermino)
			throws DAOException {

		Session session = obtenerSession();

		try {
			Criteria criteria = session.createCriteria(Termino.class);

			criteria.add(Restrictions.eq("id.codprovincia", BigDecimal.valueOf(provincia)));
			criteria.add(Restrictions.eq("id.codcomarca", BigDecimal.valueOf(comarca)));
			criteria.add(Restrictions.eq("id.codtermino", BigDecimal.valueOf(termino)));
			criteria.add(Restrictions.eq("id.subtermino", subtermino));

			return !criteria.list().isEmpty();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CargaCSV.existeTermino] Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	public boolean existeCultivoVariedad(short cultivo, short variedad, Integer linea, Integer plan)
			throws DAOException {

		Session session = obtenerSession();

		try {
			Criteria criteria = session.createCriteria(Variedad.class);

			criteria.add(Restrictions.eq("linea.codplan", BigDecimal.valueOf(plan)));
			criteria.add(Restrictions.eq("linea.codlinea", BigDecimal.valueOf(linea)));
			criteria.add(Restrictions.eq("id.codcultivo", BigDecimal.valueOf(cultivo)));
			criteria.add(Restrictions.eq("id.codvariedad", BigDecimal.valueOf(variedad)));

			return !criteria.list().isEmpty();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CargaCSV.existeCultivoVariedad] Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	@Override
	public boolean existePlanLinea(BigDecimal plan, BigDecimal linea) {
		Session session = this.obtenerSession();

		String sql = "SELECT COUNT(*) from TB_LINEAS lin where lin.codlinea =" + linea + " and " + "lin.codplan ="
				+ plan;

		List list = session.createSQLQuery(sql).list();

		return ((BigDecimal) list.get(0)).intValue() > 0;
	}

	// Metodo que busca la parcela correspondiente al capital asegurado en la lista
	// de parcelas ya tratadas
	private CvsParcela buscaParcelaCorrespondienteCapAseg(List<CvsParcela> listParcelas, Integer numeroParcela) {

		for (CvsParcela parcela : listParcelas) {
			if (parcela.getNumero().equals(numeroParcela)) {
				return parcela;
			}
		}

		return null;
	}

	@Override
	public List<CvsParcela> filtrarCsvProvComTerSubterm(Map<String, Object> filtro, List<CvsParcela> listParcelasCsv)
			throws DAOException {
		// TODO: PDTE DE DESARROLLO????
		return null;
	}
	
	/**
	 * ESC-10106 DNF 06/Julio/2020
	 * Devuelve la Entidad asociado a la entidad mediadora y subentidad mediadora indicadas
	 */
	private BigDecimal getEntidad(BigDecimal codEntMed, BigDecimal codSubEntMed) {
		Session sesion = obtenerSession();
		BigDecimal entidad = null;
		try {			
			
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			criteria.add(Restrictions.eq("id.codentidad", codEntMed));
			criteria.add(Restrictions.eq("id.codsubentidad", codSubEntMed));
			
			List<SubentidadMediadora> lista1 = criteria.list();
			
			entidad = (lista1.get(0).getEntidad().getCodentidad());
			
		} catch (Exception ex) {
			logger.error("Error al obtener la Entidad asociada a la entidad mediadora y subentidad mediadora indicadas", ex);
		}
		
		return entidad;
	}
}
