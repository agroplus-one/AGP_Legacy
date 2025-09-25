package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.sort.PolizaActualizadaSort;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.poliza.CaracteristicaExplotacionFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.MedidaFranquicia;
import com.rsi.agp.dao.tables.cpl.MedidasAsociadasHistorialAsegurado;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.TablaBonus;
import com.rsi.agp.dao.tables.cpl.TablaExternaCultivo;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.pac.PacAsegurados;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaFija;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

@SuppressWarnings("unchecked")
public class PolizaDao extends BaseDaoHibernate implements IPolizaDao {

	private static final String QUERY_LINEA_POLIZA = "SELECT L.CODLINEA FROM TB_LINEAS L INNER JOIN TB_POLIZAS P ON L.LINEASEGUROID = P.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND P.TIPOREF = :tiporef AND L.CODPLAN = :codplan";
	private static final String QUERY_PLAN_POLIZA = "SELECT L.CODPLAN FROM TB_LINEAS L INNER JOIN TB_POLIZAS P ON L.LINEASEGUROID = P.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND P.TIPOREF = :tiporef AND L.CODPLAN = :codplan";
	private static final String QUERY_NIFCIF_POLIZA = "SELECT A.NIFCIF FROM TB_ASEGURADOS A, TB_LINEAS L, TB_POLIZAS P WHERE P.REFERENCIA = :referencia AND P.TIPOREF = :tiporef AND P.LINEASEGUROID = L.LINEASEGUROID AND A.ID = P.IDASEGURADO AND L.CODPLAN =:codplan";
	private static final String QUERY_VALOR_CONCEPTO_RIESGO = " as valor, p.codconceptoppalmod,  p.codriesgocubierto";
	private static final String QUERY_LINEA_SEGURO_ID = " AND r.lineaseguroid = ";
	private static final String QUERY_COD_MODULO = " AND r.codmodulo = '";
	private static final String QUERY_COD_TIPO_CAPITAL = "' AND p.codtipocapital IN (";
	private static final String QUERY_COD_CULTIVO = ", 999) AND p.codcultivo IN (";
	private static final String QUERY_COD_VARIEDAD = ", 999) AND p.codvariedad IN (";
	private static final String QUERY_COD_PRIVINCIA = ", 999) AND p.codprovincia IN (";
	private static final String QUERY_COD_MARCA = ", 99) AND p.codcomarca IN (";
	private static final String QUERY_COD_TERMINO = ", 99) AND p.codtermino IN (";
	private static final String QUERY_SUB_TERMINO = ", 999) AND p.subtermino IN ('";
	private static final String QUERY_COUNT_POLIZA = "select count(*) from com.rsi.agp.dao.tables.poliza.Poliza po ";
	
	private static final String ERROR_ACCESO_BD = "Se ha producido un error durante el acceso a la base de datos";
	private static final String ERROR_BBDD = "Se ha producido un error en la BBDD: ";
	
	private static final String ID_COD_OFICINA = "id.codoficina";
	private static final String ID_COD_ENTIDAD = "id.codentidad";
	private static final String NOM_OFICINA = "nomoficina";
	private static final String COD_MODULO = "codmodulo";
	private static final String NIF_CIF = "nifcif";
	private static final String REFERENCIA = "referencia";
	private static final String TIPO_REFERENCIA = "tipoReferencia";
	private static final String LINEA = "linea";
	private static final String ESTADO_POLIZA = "estadoPoliza";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String POLIZA = "poliza";
	private static final String ENTIDAD = "entidad";
	private static final String USUARIO = "usuario";
	private static final String ID_LINEA_SEGURO_ID = "id.lineaseguroid";
	private static final String ID_COD_MODULO = "id.codmodulo";
	private static final String LINEA_LINEA_SEGURO_ID = "linea.lineaseguroid";
	private static final String MODULO_ID_COD_MODULO = "modulo.id.codmodulo";
	private static final String CLASE = "clase";
	private static final String RIESGO_ELEGIDO = "riesgoelegido";
	private static final String ID_ID_POLIZA = "id.idpoliza";
	private static final String COLECTIVO = "colectivo";
	private static final String LIN_COD_PLAN = "lin.codplan";
	private static final String LIN_COD_LINEA = "lin.codlinea";
	private static final String ASEGURADO = "asegurado";
	private static final String ESTADO = "estado";
	private static final String ASEG_NIF_CIF = "aseg.nifcif";
	private static final String ESTADO_ID_ESTADO = "estado.idestado";
	private static final String ENTIDAD_COD_ENTIDAD = "entidad.codentidad";
	private static final String COLECTIVO_TOMADOR = "colectivo.tomador";
	private static final String TOMADOR = "tomador";
	private static final String TOMADOR_ENTIDAD = "tomador.entidad";
	
	private DataSource dataSource;
	private LobHandler lobHandler;

	public List<VistaComparativas> getComparativa(final Filter filter) {
		return this.getObjects(filter);
	}

	public void borrarComparativasSelec(Long idpoliza, String codmodulo,
			String filacomparativa) throws DAOException {
		Session session = obtenerSession();
		try {
			Query query = session
					.createSQLQuery(
							"delete from tb_comparativas_poliza where idpoliza = :idpoliza and (codmodulo != :codmodulo or filacomparativa != :filacomparativa)")
					.setLong("idpoliza", idpoliza).setString(COD_MODULO,
							codmodulo).setBigDecimal("filacomparativa",
							new BigDecimal(filacomparativa));

			query.executeUpdate();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante la eliminacion de las comparativas",
					ex);
		} finally {
		}
	}

	public List<PacAsegurados> listPacAseg(String nifcif, BigDecimal codlinea,
			BigDecimal codplan) throws DAOException {

		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(PacAsegurados.class);

			criteria.createAlias("pacCargas", "carga");

			criteria.add(Restrictions.eq(NIF_CIF, nifcif));
			criteria.add(Restrictions.eq("carga.codlinea", codlinea));
			criteria.add(Restrictions.eq("carga.codplan", codplan));

			return criteria.list();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR_ACCESO_BD,
					ex);
		} finally {
		}
	}

	@Override
	public String getRefPolizaById(Long idPoliza) throws DAOException {
		Poliza poliza;
		try {
			poliza = (Poliza) get(Poliza.class, idPoliza);

			return poliza.getReferencia();

		} catch (Exception e) {
			throw new DAOException(
					ERROR_ACCESO_BD,
					e);
		}
	}

	@Override
	public Poliza getPolizaById(Long idPoliza) throws DAOException {

		return (Poliza) get(Poliza.class, idPoliza);
	}

	@SuppressWarnings("rawtypes")
	public Poliza getPolizaByReferencia(String refPoliza,Character tipoRefPoliza) throws DAOException {
		logger.debug("init - [PolizaDao] getPolizaByReferencia");
		Session session = obtenerSession();
		List listaPolizas=null;
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.add(Restrictions.eq(REFERENCIA, refPoliza));
			criteria.add(Restrictions.eq("tipoReferencia", tipoRefPoliza));
			listaPolizas= criteria.list();
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getPolizaByReferencia");
		return listaPolizas.isEmpty() ? null : (Poliza) listaPolizas.get(0);
		
	}
	
	// Obtiene la póliza definitiva por referencia, tipo y plan
	public Poliza getPolizaByReferenciaPlan(String refPoliza, Character tipoRefPoliza, BigDecimal plan) throws DAOException {
		logger.debug("init - [PolizaDao] getPolizaByReferenciaPlan");
		Session session = obtenerSession();
		Poliza poliza = null;
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(ESTADO_POLIZA, ESTADO_POLIZA);
			criteria.add(Restrictions.eq(REFERENCIA, refPoliza));
			criteria.add(Restrictions.eq(TIPO_REFERENCIA, tipoRefPoliza));
			criteria.add(Restrictions.eq(LINEA_CODPLAN, plan));
			criteria.add(Restrictions.ge("estadoPoliza.idestado", Constants.ESTADO_POLIZA_DEFINITIVA));
			poliza= (Poliza)criteria.uniqueResult();
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getPolizaByReferenciaPlan");
		return poliza;		
	}
	
	/* Pet. 57627 ** MODIF TAM (07.11.2019) ** Inicio */
	// Obtiene la póliza definitiva por referencia, tipo y plan
		public Poliza getPolizaByRefPlanLin(String refPoliza, Character tipoRefPoliza, BigDecimal plan, BigDecimal linea) throws DAOException {
			logger.debug("init - [PolizaDao] getPolizaByRefPlanLin");
			Session session = obtenerSession();
			Poliza poliza = null;
			try {
				Criteria criteria = session.createCriteria(Poliza.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(ESTADO_POLIZA, ESTADO_POLIZA);
				criteria.add(Restrictions.eq(REFERENCIA, refPoliza));
				criteria.add(Restrictions.eq(TIPO_REFERENCIA, tipoRefPoliza));
				criteria.add(Restrictions.eq(LINEA_CODPLAN, plan));
				criteria.add(Restrictions.eq("linea.codlinea", linea));
				criteria.add(Restrictions.ge("estadoPoliza.idestado", Constants.ESTADO_POLIZA_DEFINITIVA));
				poliza= (Poliza)criteria.uniqueResult();
			} catch (Exception e) {
				logger.error(e);
				throw new DAOException(
						ERROR_ACCESO_BD,e);
			}
			logger.debug("end - [PolizaDao] getPolizaByRefPlanLin");
			return poliza;		
		}

	@Override
	public void updateComparativas(ComparativaPolizaId compPoriginal,
			ComparativaPolizaId compPupdate) throws DAOException {
		Session session = obtenerSession();
		try {

			Query update = session
					.createSQLQuery(
							"update TB_COMPARATIVAS_POLIZA cp set cp.codvalor = :valor "
									+ "where cp.codvalor = :valor2 and cp.idpoliza = :poliza and  cp.lineaseguroid = :linea and cp.codmodulo = :modulo and "
									+ "cp.filamodulo = :filaM and "
									+ "cp.codconceptoppalmod= :conceptoM and cp.codriesgocubierto = :riesgo and "
									+ "cp.codconcepto = :concepto and "
									+ "cp.filacomparativa = :filaC")
					.setBigDecimal("valor", compPupdate.getCodvalor())
					.setBigDecimal("valor2", compPoriginal.getCodvalor())
					.setLong(POLIZA, compPoriginal.getIdpoliza())
					.setLong(LINEA, compPoriginal.getLineaseguroid())
					.setString("modulo", compPoriginal.getCodmodulo())
					.setBigDecimal("filaM", compPoriginal.getFilamodulo())
					.setBigDecimal("conceptoM",
							compPoriginal.getCodconceptoppalmod())
					.setBigDecimal("riesgo",
							compPoriginal.getCodriesgocubierto())
					.setBigDecimal("concepto", compPoriginal.getCodconcepto())
					.setBigDecimal("filaC", compPoriginal.getFilacomparativa());
			update.executeUpdate();

		} catch (Exception e) {
			throw new DAOException(
					ERROR_ACCESO_BD,
					e);
		} finally {
		}

	}

	@Override
	public int getNumeroMaxComparativas() throws DAOException {

		List<Parametro> lista = findAll(Parametro.class);
		return lista.get(0).getMaxcomparativas().intValue();
	}

	/**
	 * Metodo para obtener los datos variables de parcela que dependen del
	 * concepto principal del modulo y del riesgo cubierto
	 * 
	 * @throws BusinessException
	 */
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgo(
			Poliza poliza, ComparativaPoliza cp) throws BusinessException {
		GregorianCalendar gcI = new GregorianCalendar();

		String procedure = "PQ_DATOS_VARIABLES_RIESGO.getDatosVariablesParcelaRiesgo (IDPOLIZAPARAM IN NUMBER, "
				+ "CODMODULOPARAM IN VARCHAR2, FILACOMPARATIVAPARAM IN NUMBER) RETURN VARCHAR2";
		// Establecemos los parametros para llamar al PL
		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("IDPOLIZAPARAM", poliza.getIdpoliza());
		parametros.put("CODMODULOPARAM", cp.getId().getCodmodulo());
		parametros.put("FILACOMPARATIVAPARAM", cp.getId().getFilacomparativa());

		logger
				.debug("Llamada al procedimiento PQ_DATOS_VARIABLES_RIESGO.getDatosVariablesParcelaRiesgo con los siguientes parametros: ");
		logger.debug("    IDPOLIZAPARAM: " + poliza.getIdpoliza());
		logger.debug("    CODMODULOPARAM: " + cp.getId().getCodmodulo());
		logger.debug("    FILACOMPARATIVAPARAM: "
				+ cp.getId().getFilacomparativa());

		// Ejecutamos el PL. El resultado esta en la Clave del Map RESULT
		Map<String, Object> resultado = databaseManager.executeStoreProc(
				procedure, parametros);

		// Montamos un mapa indexado por codigo de concepto y con una lista de
		// string con cada
		// combinacion CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		String strDatVar = (String) resultado.get("RESULT");
		Map<BigDecimal, List<String>> lstDatVar = new HashMap<BigDecimal, List<String>>();
		if (!StringUtils.nullToString(strDatVar).equals("")) {
			for (String concepto : strDatVar.split("\\|")) {
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2)
					lstDatVar.put(new BigDecimal(cod_valor[0]), Arrays
							.asList(cod_valor[1].split(";")));
			}
		}

		GregorianCalendar gcF = new GregorianCalendar();
		Long tiempo = gcF.getTimeInMillis() - gcI.getTimeInMillis();
		logger
				.debug("Tiempo de la llamada a PQ_DATOS_VARIABLES_RIESGO.getDatosVariablesParcelaRiesgo: "
						+ tiempo + " milisegundos");

		return lstDatVar;
	}
	
	/**
	 * Metodo para obtener los datos variables de parcela que dependen del
	 * concepto principal del modulo y del riesgo cubierto mediante JAVA
	 * 
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgoJavaImpl(
			Poliza poliza, ComparativaPoliza cp, String webServiceToCall) throws BusinessException {
		
		String resultado = "";
		
		Session session = obtenerSession();
		
		String sqlConcepto = "SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI WHERE OI.LINEASEGUROID = " + 
						poliza.getLinea().getLineaseguroid() + " AND OI.CODUSO = 31 AND OI.CODUBICACION = 16 " +
						"AND OI.CODCONCEPTO IN (134, 135, 136, 137, 138, 139, 140)";
		
		List listConcepto = session.createSQLQuery(sqlConcepto).list();
		for (int i = 0; i < listConcepto.size(); i++){
			
			BigDecimal codconcepto = (BigDecimal)listConcepto.get(i);
			
			resultado += codconcepto + ":";
			
			Set<Parcela> parcelas = poliza.getParcelas();
			for (Parcela parcela : parcelas) {	
				Set<CapitalAsegurado> capAsegs = parcela.getCapitalAsegurados();
				for (CapitalAsegurado capAseg : capAsegs) {
					StringBuffer parIdent = new StringBuffer();
					parIdent.append(parcela.getCodcultivo());
					parIdent.append("." + parcela.getCodvariedad());
					parIdent.append("." + parcela.getTermino().getId().getCodprovincia());
					parIdent.append("." + parcela.getTermino().getId().getCodcomarca());
					parIdent.append("." + parcela.getTermino().getId().getCodtermino());
					parIdent.append("." + parcela.getTermino().getId().getSubtermino());
					parIdent.append("." + capAseg.getTipoCapital().getCodtipocapital());
					
					/* ESC-12511 ** MODIF TAM (15.02.2021) ** Inicio */
					/* El Problema se reproduce en varias líneas de Agrícola, por lo tanto se incluye el idCapitalAsegurado para todas las líneas de Agrícola*/
					if (!poliza.getLinea().isLineaGanado() && capAseg.getIdcapitalasegurado() != null) {
						parIdent.append("." + capAseg.getIdcapitalasegurado());
					}
					/* ESC-12511 ** MODIF TAM (15.02.2021) ** Fin */	
						
					String dvValor = "";
					for (DatoVariableParcela dv : capAseg.getDatoVariableParcelas()) {
						if (dv.getDiccionarioDatos().getCodconcepto().equals(codconcepto)) {
							dvValor = dv.getValor();
							break;
						}
					}
					String consulta = "";
					String sqlfield = "";
					String sqlWhere = "";
					// Recorremos el resultado de la consulta y tratamos de manera particular cada
					// codigo de concepto
					if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_DIAS_INI_GARANT))) {
						sqlfield = "p.numdiasdesde";
						sqlWhere = "p.numdiasdesde = " + dvValor;
					} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_MES_DUR_MAX_GARANT))) {
						sqlfield = "p.nummeseshasta";
						sqlWhere = "p.nummeseshasta = " + dvValor;
					} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_DIAS_DUR_MAX_GARANT))) {
						sqlfield = "p.numdiashasta";
						sqlWhere = "p.numdiashasta = " + dvValor;
					} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_FEC_FIN_GARANT))) {
						sqlfield = "TO_CHAR(p.fgaranthasta, 'dd/mm/yyyy')";
						sqlWhere = "p.fgaranthasta = TO_DATE('" + dvValor + "', 'dd/mm/yyyy')";
					} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_FEC_INI_GARANT))) {
						sqlfield = "TO_CHAR(p.fgarantdesde, 'dd/mm/yyyy')";
						sqlWhere = "p.fgarantdesde = TO_DATE('" + dvValor + "', 'dd/mm/yyyy')";
					} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_EST_FENOLOGICO_F_GARANT))) {
						sqlfield = "p.codestfenhasta";
						sqlWhere = "p.codestfenhasta = '" + dvValor + "'";
					} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_EST_FENOLOGICO_I_GARANT))) {
						sqlfield = "p.codestfendesde";
						sqlWhere = "p.codestfendesde = '" + dvValor + "'";
					}
					
					if ( !poliza.getLinea().getCodlinea().equals(new BigDecimal(301))) {
						consulta = "select * from (select " + sqlfield
								+ QUERY_VALOR_CONCEPTO_RIESGO
								+ " FROM o02agpe0.tb_sc_c_riesgo_cbrto_mod r, o02agpe0.Tb_Sc_c_Fecha_Fin_Garantia p"
								+ " WHERE p.lineaseguroid = r.lineaseguroid" + " AND r.codmodulo = p.codmodulo"
								+ " AND r.codriesgocubierto = p.codriesgocubierto"
								+ " AND r.codconceptoppalmod = p.codconceptoppalmod" + QUERY_LINEA_SEGURO_ID
								+ poliza.getLinea().getLineaseguroid() + QUERY_COD_MODULO + cp.getId().getCodmodulo()
								+ QUERY_COD_TIPO_CAPITAL + capAseg.getTipoCapital().getCodtipocapital()
								+ QUERY_COD_CULTIVO + parcela.getCodcultivo() + QUERY_COD_VARIEDAD
								+ parcela.getCodvariedad() + QUERY_COD_PRIVINCIA
								+ parcela.getTermino().getId().getCodprovincia() + QUERY_COD_MARCA
								+ parcela.getTermino().getId().getCodcomarca() + QUERY_COD_TERMINO
								+ parcela.getTermino().getId().getCodtermino() + QUERY_SUB_TERMINO
								+ parcela.getTermino().getId().getSubtermino() + "', '9') AND r.elegible = 'N' AND "
								+ sqlWhere + " union select " + sqlfield
								+ QUERY_VALOR_CONCEPTO_RIESGO
								+ " FROM o02agpe0.tb_comparativas_poliza c, o02agpe0.tb_sc_c_riesgo_cbrto_mod r, o02agpe0.tb_sc_c_fecha_fin_garantia p"
								+ " WHERE p.lineaseguroid = r.lineaseguroid AND p.lineaseguroid = c.lineaseguroid "
								+ " AND r.codmodulo = p.codmodulo AND p.codmodulo = c.codmodulo "
								+ " AND r.codriesgocubierto = p.codriesgocubierto AND p.codriesgocubierto = c.codriesgocubierto "
								+ " AND r.codconceptoppalmod = p.codconceptoppalmod AND p.codconceptoppalmod = c.codconceptoppalmod "
								+ QUERY_LINEA_SEGURO_ID + poliza.getLinea().getLineaseguroid() + QUERY_COD_MODULO
								+ cp.getId().getCodmodulo() + QUERY_COD_TIPO_CAPITAL
								+ capAseg.getTipoCapital().getCodtipocapital() + QUERY_COD_CULTIVO
								+ parcela.getCodcultivo() + QUERY_COD_VARIEDAD + parcela.getCodvariedad()
								+ QUERY_COD_PRIVINCIA + parcela.getTermino().getId().getCodprovincia()
								+ QUERY_COD_MARCA + parcela.getTermino().getId().getCodcomarca()
								+ QUERY_COD_TERMINO + parcela.getTermino().getId().getCodtermino()
								+ QUERY_SUB_TERMINO + parcela.getTermino().getId().getSubtermino()
								+ "', '9') AND r.elegible = 'S' AND c.codconcepto = 363 "
								+ " AND c.codvalor = -1 "//AND c.filacomparativa = " + cp.getId().getFilacomparativa()
								+ " AND c.idmodulo = " + cp.getId().getIdComparativa()
								+ " AND " + sqlWhere
								+ ") group by valor, codconceptoppalmod, codriesgocubierto";
					}else {
						consulta = "select * from (select " + sqlfield
								+ QUERY_VALOR_CONCEPTO_RIESGO
								+ " FROM o02agpe0.tb_sc_c_riesgo_cbrto_mod r, o02agpe0.Tb_Sc_c_Fecha_Fin_Garantia p"
								+ " WHERE p.lineaseguroid = r.lineaseguroid" + " AND r.codmodulo = p.codmodulo"
								+ " AND r.codriesgocubierto = p.codriesgocubierto"
								+ " AND r.codconceptoppalmod = p.codconceptoppalmod" + QUERY_LINEA_SEGURO_ID
								+ poliza.getLinea().getLineaseguroid() + QUERY_COD_MODULO + cp.getId().getCodmodulo()
								+ QUERY_COD_TIPO_CAPITAL + capAseg.getTipoCapital().getCodtipocapital()
								+ QUERY_COD_CULTIVO + parcela.getCodcultivo() + QUERY_COD_VARIEDAD
								+ parcela.getCodvariedad() + QUERY_COD_PRIVINCIA
								+ parcela.getTermino().getId().getCodprovincia() + QUERY_COD_MARCA
								+ parcela.getTermino().getId().getCodcomarca() + QUERY_COD_TERMINO
								+ parcela.getTermino().getId().getCodtermino() + QUERY_SUB_TERMINO
								+ parcela.getTermino().getId().getSubtermino() + "', '9') AND r.elegible = 'N' AND "
								+ sqlWhere + " union select " + sqlfield
								+ QUERY_VALOR_CONCEPTO_RIESGO
								+ " FROM o02agpe0.tb_comparativas_poliza c, o02agpe0.tb_sc_c_riesgo_cbrto_mod r, o02agpe0.tb_sc_c_fecha_fin_garantia p"
								+ " WHERE p.lineaseguroid = r.lineaseguroid AND p.lineaseguroid = c.lineaseguroid "
								+ " AND r.codmodulo = p.codmodulo AND p.codmodulo = c.codmodulo "
								+ " AND r.codriesgocubierto = p.codriesgocubierto AND p.codriesgocubierto = c.codriesgocubierto "
								+ " AND r.codconceptoppalmod = p.codconceptoppalmod AND p.codconceptoppalmod = c.codconceptoppalmod "
								+ QUERY_LINEA_SEGURO_ID + poliza.getLinea().getLineaseguroid() + QUERY_COD_MODULO
								+ cp.getId().getCodmodulo() + QUERY_COD_TIPO_CAPITAL
								+ capAseg.getTipoCapital().getCodtipocapital() + QUERY_COD_CULTIVO
								+ parcela.getCodcultivo() + QUERY_COD_VARIEDAD + parcela.getCodvariedad()
								+ QUERY_COD_PRIVINCIA + parcela.getTermino().getId().getCodprovincia()
								+ QUERY_COD_MARCA + parcela.getTermino().getId().getCodcomarca()
								+ QUERY_COD_TERMINO + parcela.getTermino().getId().getCodtermino()
								+ QUERY_SUB_TERMINO + parcela.getTermino().getId().getSubtermino()
								+ "', '9') AND r.elegible = 'S' AND c.codconcepto = 363 "
								+ " AND c.codvalor = -1 "
								+ " AND c.idmodulo = " + cp.getId().getIdComparativa()
								+ " AND " + sqlWhere
	                            + " union "
								+ "select " + sqlfield  
								+ " as valor, p.codconceptoppalmod,  p.codriesgocubierto "
								+ "FROM  o02agpe0.tb_polizas po, o02agpe0.tb_parcelas par, o02agpe0.tb_capitales_asegurados ca, "
								+ "o02agpe0.TB_DATOS_VAR_PARCELA DV, o02agpe0.tb_parcelas_coberturas c, o02agpe0.tb_sc_c_fecha_fin_garantia p "
								+ "where  po.idpoliza = par.idpoliza and par.idparcela = ca.idparcela and ca.idcapitalasegurado = dv.idcapitalasegurado "
								+ "and c.idparcela = par.idparcela and c.codconceptoppalmod = p.codconceptoppalmod "
								+ "and c.codriesgocubierto = p.codriesgocubierto and c.codmodulo = p.codmodulo "
								+ "AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999) AND p.codcultivo = par.codcultivo "
								+ "and ca.codtipocapital = p.codtipocapital and c.codvalor = -1 and po.idpoliza = "
								+ poliza.getIdpoliza() + " and c.codmodulo = '" + cp.getId().getCodmodulo() + "' AND "
								+ sqlWhere
								+ ") group by valor, codconceptoppalmod, codriesgocubierto";
							
					}

					logger.debug("Consulta para obtener los datos variables que dependen del riesgo: " + consulta);
					
					List lista = session.createSQLQuery(consulta).list();
					
					for (int j = 0; j < lista.size(); j++){
						Object[] registro = (Object[]) lista.get(j);
						resultado += parIdent.toString() + "#" + registro[1] + "#" + registro[2] + "#" + registro[0] + ";"; 
					}
				}
			}
			
			resultado += "|";			
		}		
		
		// MPM - Sigpe 8045
		// Se llama al método que obtiene la combinación "CLAVE_PARCELA#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR"
		// para los códigos de concepto 120 (% FRANQUICIA) Y 121 (% MINIMO INDEMNIZABLE)
		resultado += getDatosVariablesParcelaRiesgoEspeciales(cp.getId().getCodmodulo(), poliza, session, webServiceToCall);
		
		logger.debug("resultado: " + resultado);
		
		// Montamos un mapa indexado por codigo de concepto y con una lista de
		// string con cada combinacion de
		// CLAVE_PARCELA#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		Map<BigDecimal, List<String>> lstDatVar = new HashMap<BigDecimal, List<String>>();
		if (!StringUtils.nullToString(resultado).equals("")) {
			for (String concepto : resultado.split("\\|")) {
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2)
					lstDatVar.put(new BigDecimal(cod_valor[0]), Arrays.asList(cod_valor[1].split(";")));
			}
		}
		return lstDatVar;
	}
		

	@SuppressWarnings("rawtypes")
	private String getDatosVariablesParcelaRiesgoEspeciales(String codModulo, Poliza poliza, Session session,
			String webServiceToCall) {		
		String resultado = "";		
		try {
			BigDecimal[] listConceptoEsp = new BigDecimal[] { new BigDecimal(120), new BigDecimal(121)};			
			for (BigDecimal codconcepto : listConceptoEsp) {				
				resultado += codconcepto + ":";				
				for (Parcela parcela : poliza.getParcelas()) {				
					for (CapitalAsegurado capAseg : parcela.getCapitalAsegurados()) {
						StringBuffer parIdent = new StringBuffer();
						parIdent.append(parcela.getCodcultivo());
						parIdent.append("." + parcela.getCodvariedad());
						parIdent.append("." + parcela.getTermino().getId().getCodprovincia());
						parIdent.append("." + parcela.getTermino().getId().getCodcomarca());
						parIdent.append("." + parcela.getTermino().getId().getCodtermino());
						parIdent.append("." + parcela.getTermino().getId().getSubtermino());
						parIdent.append("." + capAseg.getTipoCapital().getCodtipocapital());
						
						/* ESC-12511 ** MODIF TAM (15.02.2021) ** Inicio */
						/* El Problema se reproduce en varias líneas de Agrícola, por lo tanto se incluye el idCapitalAsegurado para todas las líneas de Agrícola*/
						/*if (poliza.getLinea().getCodlinea().equals(new BigDecimal(301)) || poliza.getLinea().getCodlinea().equals(new BigDecimal(311))) {*/
						if (!poliza.getLinea().isLineaGanado()) {
						/*if (poliza.getLinea().getCodlinea().equals(new BigDecimal(301)) || poliza.getLinea().getCodlinea().equals(new BigDecimal(311))) {*/
							parIdent.append("." + capAseg.getIdcapitalasegurado());
						}
						/* ESC-12511 ** MODIF TAM (15.02.2021) ** Fin */
						
						String dvValor = "";
						for (DatoVariableParcela dv : capAseg.getDatoVariableParcelas()) {
							if (dv.getDiccionarioDatos().getCodconcepto().equals(codconcepto)) {
								dvValor = dv.getValor();
								break;
							}
						}	
						if (!StringUtils.isNullOrEmpty(dvValor)) {
							/* Pet. 64397 (REQ.01) ** MODIF TAM (31/03/2020) ** Inicio 
							 * Incluimos validación para las polizas principales, cuyo modulo elegido sea 'P' y el 
							 * WebService desde el que se esté llamando sea validación o Calculo */
							String consulta = "";
							String sqlField = "";
							if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA))) {
								sqlField = "V.CODPCTFRANQUICIAELEG";
							} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE))) {
								sqlField = "V.PCTMININDEMNELEG";
							}
							if( (codModulo != null && codModulo.equals("P") ) && 
								(!Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(poliza.getTipoReferencia()))) {								
								consulta = "SELECT " + sqlField + ", R.CODCONCEPTOPPALMOD, R.CODRIESGOCUBIERTO "
										+ "FROM TB_SC_C_RIESGO_CBRTO_MOD R, "
										+ "TB_SC_C_VINC_VALORES_MOD V, "
										+ "TB_SC_C_CARACT_MODULO CM "
										+ "WHERE R.LINEASEGUROID = V.LINEASEGUROID "
										+ "AND R.CODMODULO = V.CODMODULO "
										+ "AND R.FILAMODULO = V.FILAMODULO "
										+ "AND CM.FILAMODULO = R.FILAMODULO " 
										+ "AND CM.LINEASEGUROID = R.LINEASEGUROID "
									    + "AND CM.CODMODULO = R.CODMODULO "
									    + "AND CM.COLUMNAMODULO = V.COLUMNAMODULO "
										+ "AND R.LINEASEGUROID = " + poliza.getLinea().getLineaseguroid() + " "
										+ "AND R.CODMODULO = '" + codModulo + "' "
										+ "AND R.ELEGIBLE = 'S' "
										+ "AND R.NIVELECCION = 'D' "
										+ "AND CM.TIPOVALOR = 'E' ";
							} else {
								/* Pet. 64397 (REQ.01) ** MODIF TAM (31/03/2020) ** Fin*/
								consulta = "SELECT " + sqlField + ", R.CODCONCEPTOPPALMOD, R.CODRIESGOCUBIERTO "
										+ "FROM TB_SC_C_RIESGO_CBRTO_MOD R, "
										+ "TB_SC_C_VINC_VALORES_MOD V "
										+ "WHERE AND R.LINEASEGUROID = V.LINEASEGUROID "
										+ "AND R.CODMODULO = V.CODMODULO "
										+ "AND R.FILAMODULO = V.FILAMODULO "
										+ "AND R.LINEASEGUROID = " + poliza.getLinea().getLineaseguroid() + " "
										+ "AND R.CODMODULO = '" + codModulo + "' "
										+ "AND R.ELEGIBLE = 'S' "
										+ "AND R.NIVELECCION = 'D' ";
							}						
							if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA))) {
								consulta += " AND v.CODPCTFRANQUICIAELEG = " + dvValor;
							} else if (codconcepto.equals(new BigDecimal(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE))) {
								consulta += " AND V.PCTMININDEMNELEG = " + dvValor;
							}		
							logger.debug("Consulta para obtener los datos variables que dependen del riesgo los conceptos 120 y 121: "
									+ consulta);		
							List lista = session.createSQLQuery(consulta).list();
									for (int j = 0; j < lista.size(); j++) {
								Object[] registro = (Object[]) lista.get(j);
								resultado += parIdent.toString() + "#" + registro[1] + "#"
										+ registro[2] + "#" + registro[0] + ";";
							}
						}
					}
				}
				if (!StringUtils.isNullOrEmpty(resultado))
					resultado += "|";
			}
		} catch (Exception e) {
			logger.error("Error al obtener los datos variables asociados a los conceptos 120 y 121", e);
		}
		
		return resultado;
	}

	public EnvioAgroseguro saveEnvioAgroseguro(EnvioAgroseguro envio)
			throws DAOException {
		Session session = obtenerSession();
		try {

			session.saveOrUpdate(envio);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el guardado de la entidad",
					ex);
		} finally {
		}

		return envio;
	}

	/**
	 * Metodo para actualizar el XML de un envio a Agroseguro.
	 */
	public void actualizaXmlEnvio(Long idEnvio, final String xml, final String calculo) {
		
		logger.debug("WebServicesManager - Dentro de actualizaXmlEnvio");
		logger.debug("Valor de idEnvio: "+idEnvio);
		logger.debug("Vaalor de xml: "+xml);
		
		if (!StringUtils.nullToString(xml).equals("")) {
			logger.debug("Entramos en el primer if");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update("UPDATE TB_ENVIOS_AGROSEGURO SET XML=? WHERE ID=" + idEnvio, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					lobHandler.getLobCreator().setClobAsString(ps, 1, xml);
				}
			});
		}

		if (!StringUtils.nullToString(calculo).equals("")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			logger.debug("Entramos en el segundo if");
			jdbcTemplate.update("UPDATE TB_ENVIOS_AGROSEGURO SET CALCULO=? WHERE ID=" + idEnvio, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					lobHandler.getLobCreator().setClobAsString(ps, 1, calculo);
				}
			});
		}
	}
	
	
	public void actualizaXmlCoberturas(Long idEnvio, final String xml, final String respuesta) {
		if (!StringUtils.nullToString(xml).equals("")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update("UPDATE TB_SW_MODULOS_COBERTURAS_EXP SET ENVIO=? WHERE ID=" + idEnvio, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					lobHandler.getLobCreator().setClobAsString(ps, 1, xml);
				}
			});
		}
		
		if (!StringUtils.nullToString(respuesta).equals("")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update("UPDATE TB_SW_MODULOS_COBERTURAS_EXP SET RESPUESTA=? WHERE ID=" + idEnvio, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					lobHandler.getLobCreator().setClobAsString(ps, 1, respuesta);
				}
			});
		}
		
	}
	
	public void actualizaXmlCoberturasAnexo(Long idEnvio, final String xml, final String respuesta) {
		if (!StringUtils.nullToString(xml).equals("")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update("UPDATE TB_SW_MODULOS_COB_EXP_ANEXO SET ENVIO=? WHERE ID=" + idEnvio, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					lobHandler.getLobCreator().setClobAsString(ps, 1, xml);
				}
			});
		}
		
		if (!StringUtils.nullToString(respuesta).equals("")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update("UPDATE TB_SW_MODULOS_COB_EXP_ANEXO SET RESPUESTA=? WHERE ID=" + idEnvio, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					lobHandler.getLobCreator().setClobAsString(ps, 1, respuesta);
				}
			});
		}
		
	}
	
	/**
	 * Metodo para guardar el fichero XML de envio a Agroseguro con los datos de
	 * la poliza en el campo 'XMLACUSECONTRATACION'
	 * 
	 * @param poliza
	 *            Poliza a generar
	 */
	public void actualizaXmlPoliza(Long idPoliza, final String xml) {
						
		if (!StringUtils.nullToString(xml).equals("")) {			
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(
					"UPDATE TB_POLIZAS SET XMLACUSECONTRATACION=? WHERE IDPOLIZA="
							+ idPoliza, new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							lobHandler.getLobCreator().setClobAsString(ps, 1,
									xml);
						}
					});			
		}
	}

	/**
	 * Elimina el objeto de la sesion de hibernate para recargarlo
	 */
	public void evictEnvio(EnvioAgroseguro envio) {
		Session session = obtenerSession();
		session.evict(envio);
	}

	public void evictPoliza(Poliza poliza) {
		Session session = obtenerSession();
		session.evict(poliza);
	}

	/**
	 * Metodo para comprobar si una poliza necesita la caracteristica de la
	 * explotacion en los datos variables de cobertura
	 * 
	 * @param idPoliza
	 * @param codModulo
	 * @return true en caso de que sea necesario
	 * @throws Exception
	 */
	public boolean aplicaCaractExplotacion(final Long lineaseguroid)
			throws Exception {
		// consultamos en la base de datos (Organizador de informacion) si es
		// necesario
		// incluir el codigo de concepto 106 para el uso poliza (31) y la
		// ubicacion cobertura datos variables (18)
		CaracteristicaExplotacionFiltro filter = new CaracteristicaExplotacionFiltro(
				lineaseguroid);
		Integer numObjects = this.getNumObjects(filter);

		return numObjects > 0;
	}

	public void deleteCaractExplotacion(final Long idpoliza)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Query query = session
					.createSQLQuery(
							"delete from tb_comparativas_poliza where idpoliza = :idpoliza and codconcepto = 106")
					.setLong("idpoliza", idpoliza);
			query.executeUpdate();
		} catch (Exception e) {
			throw new DAOException(
					"Error al borrar la caracteristica de la expotacion.", e);
		}
	}

	@Override
	public boolean tieneAccesoAPolizaByIdPoliza(Usuario usuario, String idPoliza) {
		Session session = obtenerSession();
		Query query = null;
		try {

			if ((usuario.getPerfil()
					.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					|| (usuario.getPerfil()
							.equals(Constants.PERFIL_USUARIO_OFICINA))
							|| (usuario.getPerfil()
									.equals(Constants.PERFIL_USUARIO_JEFE_ZONA))) {

				query = session
						.createQuery(
								QUERY_COUNT_POLIZA
										+ "where po.colectivo.tomador.entidad.codentidad= :entidad and  po.idpoliza = :id")
						.setLong("id", new Long(idPoliza)).setBigDecimal(
								ENTIDAD,
								usuario.getOficina().getEntidad()
										.getCodentidad());

			} else if (usuario.getPerfil().equals(
					Constants.PERFIL_USUARIO_OTROS)) {

				query = session
						.createQuery(
								QUERY_COUNT_POLIZA
										+ "where po.usuario.codusuario = :usuario and  po.idpoliza = :id")
						.setString(USUARIO, usuario.getCodusuario()).setLong(
								"id", new Long(idPoliza));

			}

			if (query != null){
				Long count = (Long) query.uniqueResult();
				if (count.compareTo(new Long(0)) == 0)
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			logger.error("Error al comprobar si el usuario tiene acceso a las polizas por id", e);
		} finally {
		}
		return false;
	}

	@Override
	public boolean tieneAccesoAPolizaByRefPoliza(Usuario usuario,
			String refPoliza) {
		Session session = obtenerSession();
		Query query = null;
		try {

			if ((usuario.getPerfil()
					.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					|| (usuario.getPerfil()
							.equals(Constants.PERFIL_USUARIO_OFICINA))
							|| (usuario.getPerfil()
									.equals(Constants.PERFIL_USUARIO_JEFE_ZONA))) {

				query = session
						.createQuery(
								QUERY_COUNT_POLIZA
										+ "where po.colectivo.tomador.entidad.codentidad= :entidad and  po.referencia = :ref")
						.setString("ref", refPoliza).setBigDecimal(
								ENTIDAD,
								usuario.getOficina().getEntidad()
										.getCodentidad());

			} else if (usuario.getPerfil().equals(
					Constants.PERFIL_USUARIO_OTROS)) {

				query = session
						.createQuery(
								QUERY_COUNT_POLIZA
										+ "where po.usuario.codusuario = :usuario and  po.referencia = :ref")
						.setString(USUARIO, usuario.getCodusuario())
						.setString("ref", refPoliza);

			}

			if (query != null){
				Long count = (Long) query.uniqueResult();
				if (count.compareTo(new Long(0)) == 0)
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			logger.error("Error al comprobar si el usuario tiene acceso a las polizas por referencia", e);
		} finally {
		}
		return false;
	}

	@Override
	public boolean tieneAccesoAAnexo(Usuario usuario, String idAnexo) {
		Session session = obtenerSession();
		Query query = null;
		try {

			if ((usuario.getPerfil()
					.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					|| (usuario.getPerfil()
							.equals(Constants.PERFIL_USUARIO_OFICINA))
							|| (usuario.getPerfil()
									.equals(Constants.PERFIL_USUARIO_JEFE_ZONA))) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.anexo.AnexoModificacion anx "
										+ "where anx.poliza.colectivo.tomador.entidad.codentidad= :entidad and  anx.id = :id")
						.setLong("id", new Long(idAnexo)).setBigDecimal(
								ENTIDAD,
								usuario.getOficina().getEntidad()
										.getCodentidad());

			} else if (usuario.getPerfil().equals(
					Constants.PERFIL_USUARIO_OTROS)) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.anexo.AnexoModificacion anx "
										+ "where anx.poliza.usuario.codusuario = :usuario and  anx.id = :id")
						.setString(USUARIO, usuario.getCodusuario()).setLong(
								"id", new Long(idAnexo));

			}

			if (query != null){
				Long count = (Long) query.uniqueResult();
				if (count.compareTo(new Long(0)) == 0)
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			logger.error("Error al comprobar si el usuario tiene acceso a los anexos de modificacon", e);
		} finally {
		}
		return false;
	}

	@Override
	public boolean tieneAccesoAAsegurado(Usuario usuario, String idAsegurado) {
		Session session = obtenerSession();
		Query query = null;
		try {

			if ((usuario.getPerfil()
					.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					|| (usuario.getPerfil()
							.equals(Constants.PERFIL_USUARIO_OFICINA))
							|| (usuario.getPerfil()
									.equals(Constants.PERFIL_USUARIO_JEFE_ZONA))) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.admin.Asegurado asg "
										+ "where asg.entidad.codentidad= :entidad and  asg.id = :id")
						.setLong("id", new Long(idAsegurado)).setBigDecimal(
								ENTIDAD,
								usuario.getOficina().getEntidad()
										.getCodentidad());

			} else if (usuario.getPerfil().equals(
					Constants.PERFIL_USUARIO_OTROS)) {
				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.admin.Asegurado asg "
										+ "where asg.usuario.codusuario = :usuario and  asg.id = :id")
						.setString(USUARIO, usuario.getCodusuario()).setLong(
								"id", new Long(idAsegurado));
			}

			if (query != null){
				Long count = (Long) query.uniqueResult();
				if (count.compareTo(new Long(0)) == 0)
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			logger.error("Error al comprobar si el usuario tiene acceso a la administracion de asegurados", e);
		} finally {
		}
		return false;
	}

	@Override
	public boolean tieneAccesoARedCapital(Usuario usuario, String idRedCapital) {
		Session session = obtenerSession();
		Query query = null;
		try {

			if ((usuario.getPerfil()
					.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					|| (usuario.getPerfil()
							.equals(Constants.PERFIL_USUARIO_OFICINA))
							|| (usuario.getPerfil()
									.equals(Constants.PERFIL_USUARIO_JEFE_ZONA))) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital rc "
										+ "where rc.poliza.colectivo.tomador.entidad.codentidad = :entidad and  rc.id = :id")
						.setLong("id", new Long(idRedCapital)).setBigDecimal(
								ENTIDAD,
								usuario.getOficina().getEntidad()
										.getCodentidad());

			} else if (usuario.getPerfil().equals(
					Constants.PERFIL_USUARIO_OTROS)) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital rc "
										+ "where rc.poliza.usuario.codusuario = :usuario and  rc.id = :id")
						.setString(USUARIO, usuario.getCodusuario()).setLong(
								"id", new Long(idRedCapital));

			}

			if (query != null){
				Long count = (Long) query.uniqueResult();
				if (count.compareTo(new Long(0)) == 0)
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			logger.error("Error al comprobar si el usuario tiene acceso a las reducciones de capital", e);
		} finally {
		}
		return false;
	}

	@Override
	public boolean tieneAccesoASiniestro(Usuario usuario, String idSiniestro) {
		Session session = obtenerSession();
		Query query = null;
		try {

			if ((usuario.getPerfil()
					.equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES))
					|| (usuario.getPerfil()
							.equals(Constants.PERFIL_USUARIO_OFICINA))
							|| (usuario.getPerfil()
									.equals(Constants.PERFIL_USUARIO_JEFE_ZONA))) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.siniestro.Siniestro s "
										+ "where s.poliza.colectivo.tomador.entidad.codentidad= :entidad and  s.id = :id")
						.setLong("id", new Long(idSiniestro)).setBigDecimal(
								ENTIDAD,
								usuario.getOficina().getEntidad()
										.getCodentidad());

			} else if (usuario.getPerfil().equals(
					Constants.PERFIL_USUARIO_OTROS)) {

				query = session
						.createQuery(
								"select count(*) from com.rsi.agp.dao.tables.siniestro.Siniestro s "
										+ "where s.poliza.usuario.codusuario = :usuario and  s.id = :id")
						.setString(USUARIO, usuario.getCodusuario()).setLong(
								"id", new Long(idSiniestro));

			}

			if (query != null){
				Long count = (Long) query.uniqueResult();
				if (count.compareTo(new Long(0)) == 0)
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			logger.error("Error al comprobar si el usuario tiene acceso a los siniestros", e);
		} finally {
		}
		return false;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BigDecimal getHistAsegBonus(Long lineaseguroid,
			BigDecimal codpctfranquiciaeleg) {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(TablaBonus.class);

			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			if (codpctfranquiciaeleg!=null)
				criteria.add(Restrictions.eq("id.codpctfranquiciaeleg", codpctfranquiciaeleg));

			criteria.setProjection(Projections.distinct(Projections
					.projectionList().add(
							Projections.property("id.codhistorialasegurado"))));

			List lstHistAseg = criteria.list();
			if (lstHistAseg.size() > 0)
				return (BigDecimal) lstHistAseg.get(0);

		} catch (Exception ex) {
			logger.error(ex);
			// throw new DAOException("Se ha producido un error durante el
			// acceso a la base de datos", ex);
		} finally {
		}
		return null;
	}

	@Override
	public List<BigDecimal> getMedAsocHistAseg(Long lineaseguroid,
			String codmodulo, BigDecimal codHistAseg) {
		// buscar la lista de porcentajes con los parametros pasados.
		Session session = obtenerSession();
		try {
			Criteria criteria = session
					.createCriteria(MedidasAsociadasHistorialAsegurado.class);

			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(Restrictions.eq(ID_COD_MODULO, codmodulo));
			criteria.add(Restrictions.eq("id.histaseg", codHistAseg));

			criteria.setProjection(Projections.distinct(Projections
					.projectionList().add(
							Projections.property("pctbonifrecargo"))));

			return criteria.list();

		} catch (Exception ex) {
			logger.error(ex);
			// throw new DAOException("Se ha producido un error durante el
			// acceso a la base de datos", ex);
		} finally {
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BigDecimal getPctBonifRecargo(Long lineaseguroid, String nifcif) {
		// buscar si para el nif del asegurado hay pdtbonifrecargo mayor o igual
		// que la del asegurado (con club=1)
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Medida.class);

			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(Restrictions.eq("id.nifasegurado", nifcif));
			criteria.add(Restrictions.eq("tipomedidaclub", new Character('1')));

			criteria.setProjection(Projections.distinct(Projections
					.projectionList().add(
							Projections.property("pctbonifrecargo"))));

			List lstMedidas = criteria.list();
			if (lstMedidas.size() > 0)
				return (BigDecimal) lstMedidas.get(0);

		} catch (Exception ex) {
			logger.error("Error al obtener las medidas", ex);
			// throw new DAOException("Se ha producido un error durante el
			// acceso a la base de datos", ex);
		} finally {
		}
		return new BigDecimal(0);
	}

	/**
	 * Metodo para obtener las 'VinculacionValoresModulo' para un determinado
	 * lineaseguroid, codmodulo, filamodulo y columnamodulo
	 */
	@Override
	public List<VinculacionValoresModulo> getVinculacionesValoresModulo(
			Long lineaseguroid, String codmodulo, BigDecimal codconcepto,
			BigDecimal valor, BigDecimal filamodulo, BigDecimal columnamodulo)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session
					.createCriteria(VinculacionValoresModulo.class);
			criteria.createAlias("caracteristicaModuloByFkVincValModCaracMod1",
					"carMod", CriteriaSpecification.LEFT_JOIN);

			if (!StringUtils.nullToString(lineaseguroid).equals("")) {
				criteria
						.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
				criteria.add(Restrictions.eq("carMod.id.lineaseguroid",
						lineaseguroid));
			}
			if (!StringUtils.nullToString(codmodulo).equals(""))
				criteria.add(Restrictions.eq("carMod.id.codmodulo", codmodulo));
			if (!StringUtils.nullToString(filamodulo).equals(""))
				criteria.add(Restrictions
						.eq("carMod.id.filamodulo", filamodulo));
			if (!StringUtils.nullToString(columnamodulo).equals(""))
				criteria.add(Restrictions.eq("carMod.id.columnamodulo",
						columnamodulo));

			switch (codconcepto.intValue()) {
			case 120:
				criteria.createAlias(
						"pctFranquiciaElegibleByCodpctfranquiciaeleg",
						"pctFranqEleg", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.eq(
						"pctFranqEleg.codpctfranquiciaeleg", valor));
				break;
			case 121:
				criteria.createAlias(
						"minimoIndemnizableElegibleByPctminindemneleg",
						"minIndem", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.eq("minIndem.pctminindem", valor));
				break;
			case 175:
				criteria.createAlias(
						"garantizadoByGarantizadoeleg",
						"garant", CriteriaSpecification.LEFT_JOIN);
				criteria.add(Restrictions.eq("garant.codgarantizado", valor));
				break;
			default:
				break;
			}

			return criteria.list();

		} catch (Exception ex) {
			logger
					.error(
							"Se ha producido un error al obtener las vinculaciones",
							ex);
			throw new DAOException(
					"Se ha producido un error al obtener las vinculaciones", ex);
		}
	}

	@Override
	public String getMSGAclaracionModulo(String codmodulo, Long lineaseguroid)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Modulo.class);
			criteria.add(Restrictions.eq(ID_COD_MODULO, codmodulo));
			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));

			return ((Modulo) criteria.uniqueResult()).getMsjaclaracion();

		} catch (Exception ex) {
			logger
					.error(
							"Se ha producido un error al obtener el mensaje aclaratorio",
							ex);
			throw new DAOException(
					"Se ha producido un error al obtener el mensaje aclaratorio",
					ex);
		}
	}

	@Override
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgoCPL(	Poliza poliza, String codModulo) throws BusinessException {
		GregorianCalendar gcI = new GregorianCalendar();

		String procedure = "PQ_DATOS_VARIABLES_RIESGO.getDatosVarParcelaRiesgoCPL (IDPOLIZAPARAM IN NUMBER, "
				+ "CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2";
		// Establecemos los parametros para llamar al PL
		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("IDPOLIZAPARAM", poliza.getIdpoliza());
		parametros.put("CODMODULOPARAM", codModulo);

		// Ejecutamos el PL. El resultado esta en la Clave del Map RESULT
		Map<String, Object> resultado = databaseManager.executeStoreProc(
				procedure, parametros);

		// Montamos un mapa indexado por codigo de concepto y con una lista de
		// string con cada
		// combinacion CAPITAL_ASEGURADO#CONCEPTO_PPAL_MOD#RIESGO_CUBIERTO#VALOR
		String strDatVar = (String) resultado.get("RESULT");
		Map<BigDecimal, List<String>> lstDatVar = new HashMap<BigDecimal, List<String>>();
		if (!StringUtils.nullToString(strDatVar).equals("")) {
			for (String concepto : strDatVar.split("\\|")) {
				String[] cod_valor = concepto.split(":");
				if (cod_valor.length == 2)
					lstDatVar.put(new BigDecimal(cod_valor[0]), Arrays
							.asList(cod_valor[1].split(";")));
			}
		}

		GregorianCalendar gcF = new GregorianCalendar();
		Long tiempo = gcF.getTimeInMillis() - gcI.getTimeInMillis();
		logger.debug("Tiempo de la llamada a PQ_DATOS_VARIABLES_RIESGO.getDatosVarParcelaRiesgoCPL: "+ tiempo + " milisegundos");

		return lstDatVar;
	}

	@Override
	public Comunicaciones getComunicaciones(BigDecimal idEnvio)	throws DAOException {
		try {

			return (Comunicaciones) get(Comunicaciones.class, idEnvio);

		} catch (Exception e) {
			throw new DAOException(	ERROR_ACCESO_BD,e);
		}
	}

	public List<TablaExternaCultivo> getTablaExtCultivo(Long lineaseguroid,	BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto,String codmodulo) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(TablaExternaCultivo.class);
		criteria.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
		criteria.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod",codconceptoppalmod));
		criteria.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto",	codriesgocubierto));
		criteria.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
		List<TablaExternaCultivo> tbExtCultivo = criteria.list();
		return tbExtCultivo;
	}

	public List<BigDecimal> getCultivosClase(BigDecimal clase,Long lineaseguroid) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ClaseDetalle.class);
		criteria.createAlias(CLASE, "aliasClase");
		if (clase != null) criteria.add(Restrictions.eq("aliasClase.clase", clase));
		criteria.add(Restrictions.eq("aliasClase.linea.lineaseguroid", lineaseguroid));
		criteria.setProjection(Projections.distinct(Projections.property("cultivo.id.codcultivo")));
		List<BigDecimal> listaCultivosClase = criteria.list();
		return listaCultivosClase;
	}
	
	public BigDecimal getSistCultivoClase(BigDecimal clase,Long lineaseguroid) {
		Session session = obtenerSession();
		BigDecimal sisCultivo=null;
		Criteria criteria = session.createCriteria(ClaseDetalle.class);
		criteria.createAlias(CLASE, "aliasClase");
		if (clase != null) criteria.add(Restrictions.eq("aliasClase.clase", clase));
		criteria.add(Restrictions.eq("aliasClase.linea.lineaseguroid", lineaseguroid));
		criteria.setProjection(Projections.distinct(Projections.property("sistemaCultivo.codsistemacultivo")));
		List<BigDecimal> listaSistCultivosClase = criteria.list();
		if (listaSistCultivosClase.size()>0){
			sisCultivo =(BigDecimal)(listaSistCultivosClase.get(0));
		}
		//BigDecimal sisCultivo = (BigDecimal) criteria.uniqueResult();

		return sisCultivo;
	}

	public List<BigDecimal> getCiclosCultivoClase(BigDecimal clase,Long lineaseguroid) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ClaseDetalle.class);
		criteria.createAlias(CLASE, "aliasClase");
		if (clase != null) criteria.add(Restrictions.eq("aliasClase.clase", clase));
		criteria.add(Restrictions.eq("aliasClase.linea.lineaseguroid", lineaseguroid));
		criteria.setProjection(Projections.distinct(Projections.property("cicloCultivo.codciclocultivo")));
		List<BigDecimal> listaCicloCultivos = criteria.list();
		return listaCicloCultivos;
		
	}
	
	/**
	 * Metodo que verifica si se encuentra en el plazo de pasar la poliza a definitiva.
	 * res: 0-> fecha actual en plazo. 1-> fecha actual en plazo y aviso al cliente. 2-> fecha actual fuera de plazo.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public int validarFecha(Poliza poliza) throws DAOException{
		logger.debug("init - [PolizaDao] validarFecha");
		Session session = obtenerSession();
		
		int res = 0;
		GregorianCalendar c = new GregorianCalendar();
		GregorianCalendar fechaIni= new GregorianCalendar();
		GregorianCalendar fechaFin= new GregorianCalendar();
		long miliDia = 86400000;  // un dia en milisegundos 
		long fecHoy= c.getTimeInMillis();
		
		List lstDates = new ArrayList();
		int horaHoy = c.get(Calendar.HOUR_OF_DAY);
		int minutosHoy = c.get(Calendar.MINUTE);
		int diaHoy = c.get(Calendar.DAY_OF_MONTH);
		int mesHoy = c.get(Calendar.MONTH);
		int anioHoy= c.get(Calendar.YEAR);
		
		Query query = null;
		try {
			query = session.createQuery("SELECT min(f.feciniciocontrata), max(f.ultimodiapago) from "
							+ "com.rsi.agp.dao.tables.cpl.FechaContratacionAgricola f where f.id.lineaseguroid ="
							+ poliza.getLinea().getLineaseguroid().toString()
							+ " and f.modulo.id.codmodulo='"
							+ poliza.getCodmodulo().toString() + "'");
			lstDates = query.list();
			Iterator a = lstDates.iterator();
			Object[] fechas = (Object[]) a.next();
			fechaIni.setTime((Date)fechas[0]);
			fechaFin.setTime((Date)fechas[1]);
			int diaFin = fechaFin.get(Calendar.DAY_OF_MONTH);
			int mesFin = fechaFin.get(Calendar.MONTH);
			int anioFin = fechaFin.get(Calendar.YEAR);
			long tiempoIni=fechaIni.getTimeInMillis();
			long tiempoFin=fechaFin.getTimeInMillis() + miliDia;
			
			if ((fecHoy >= tiempoIni) && (fecHoy <=tiempoFin)){
				if (diaHoy == diaFin && mesHoy == mesFin && anioHoy == anioFin){
					if (horaHoy > 16){
						res = 1;
					}else if ((horaHoy == 16) && (minutosHoy >= 40)) {
						res = 1;
					}
				}
			} else {
				res = 2;
			}
		} catch (Exception e) {
			logger.error(ERROR_BBDD + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		} 
		logger.debug("end - [PolizaDao] validarFecha");
		return res;
	}
	
	public BigDecimal getPctFranquicia(long lineaseguroid, String codmodulo,String nifCif, BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Character elegido){
		logger.debug("init - [PolizaDao] getPctFranquicia");
		Session session = obtenerSession();
		BigDecimal Pctfranquicia= null;
		
		Criteria criteria = session.createCriteria(MedidaFranquicia.class);
		criteria.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
		criteria.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
		criteria.add(Restrictions.eq(NIF_CIF, nifCif));
		criteria.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", codconceptoppalmod));
		criteria.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto", codriesgocubierto));
		criteria.add(Restrictions.eq(RIESGO_ELEGIDO, elegido));
		
		List<MedidaFranquicia> lstMedidaFranquicia = criteria.list();
		if (lstMedidaFranquicia.size()>0) {
			Pctfranquicia=lstMedidaFranquicia.get(0).getPctfranquicia();
		}else{
			Criteria criteria2 = session.createCriteria(MedidaFranquicia.class);
			criteria2.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
			criteria2.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
			criteria2.add(Restrictions.eq(NIF_CIF, nifCif));
			criteria2.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", codconceptoppalmod));
			criteria2.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto", codriesgocubierto));
			criteria2.add(Restrictions.isNull(RIESGO_ELEGIDO));
			
			List<MedidaFranquicia> lstMedidaFranquicia2 = criteria2.list();
			if (lstMedidaFranquicia2.size()>0) {
				Pctfranquicia=lstMedidaFranquicia2.get(0).getPctfranquicia();
			}
			else {
				Criteria criteria3 = session.createCriteria(MedidaFranquicia.class);
				criteria3.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
				criteria3.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
				criteria3.add(Restrictions.isNull(NIF_CIF));
				criteria3.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", codconceptoppalmod));
				criteria3.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto", codriesgocubierto));
				criteria3.add(Restrictions.eq(RIESGO_ELEGIDO, elegido));
				
				List<MedidaFranquicia> lstMedidaFranquicia3 = criteria3.list();
				if (lstMedidaFranquicia3.size()>0) {
					Pctfranquicia=lstMedidaFranquicia3.get(0).getPctfranquicia();
				}
				else {
					Criteria criteria4 = session.createCriteria(MedidaFranquicia.class);
					criteria4.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
					criteria4.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
					criteria4.add(Restrictions.isNull(NIF_CIF));
					criteria4.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", codconceptoppalmod));
					criteria4.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto", codriesgocubierto));
					criteria4.add(Restrictions.isNull(RIESGO_ELEGIDO));
					
					List<MedidaFranquicia> lstMedidaFranquicia4 = criteria4.list();
					if (lstMedidaFranquicia4.size()>0) {
						Pctfranquicia=lstMedidaFranquicia4.get(0).getPctfranquicia();
					}
				}
			}
		}
		logger.debug("end - [PolizaDao] getPctFranquicia");
		return Pctfranquicia;
	}
	
	public String getDescPctFranquiciaEleg(BigDecimal pctFranquicia){
		String descPctFranquiciaEleg="";
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(PctFranquiciaElegible.class);
		criteria.add(Restrictions.eq("codpctfranquiciaeleg", pctFranquicia));
		
		List<PctFranquiciaElegible> lstPctFranquiciaElegible = criteria.list();
		if (lstPctFranquiciaElegible.size()>0) {
			descPctFranquiciaEleg=lstPctFranquiciaElegible.get(0).getDespctfranquiciaeleg();
		}
		return descPctFranquiciaEleg;
	}
	
	public List<ModuloPoliza> getLstModulosPoliza(long idpoliza) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ModuloPoliza.class);
		criteria.add(Restrictions.eq(ID_ID_POLIZA, new Long(idpoliza)));		
		List<ModuloPoliza> lstCompPolizas= criteria.list();
		return lstCompPolizas;
	}
	
	public List<ComparativaPoliza> getLstCompPolizas(long idpoliza, String modulo) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ComparativaPoliza.class);
		criteria.add(Restrictions.eq(ID_ID_POLIZA, new Long(idpoliza)));
		criteria.add(Restrictions.eq(ID_COD_MODULO, modulo));
		criteria.add(Restrictions.eq("id.codvalor", new BigDecimal(-2)));
		List<BigDecimal> lstBig = new ArrayList<BigDecimal>();
		lstBig.add(new BigDecimal(363));
		lstBig.add(new BigDecimal(0));
		criteria.add(Restrictions.in("id.codconcepto", lstBig));
		List<ComparativaPoliza> lstCompPolizas= criteria.list();
		return lstCompPolizas;
	}
	
	/* Pet. 57626 ** MODIF TAM (12.06.2020) ** Inicio */
	/* Creamos un nuevo método para obtener la lista de comparativas por el idpoliza */
	public List<ComparativaPoliza> getLstCompPolizasByIdPol(long idpoliza){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ComparativaPoliza.class);
		criteria.add(Restrictions.eq(ID_ID_POLIZA, new Long(idpoliza)));		
		List<ComparativaPoliza> lstCompPolizas= criteria.list();
		return lstCompPolizas;
	}
	
	/**
	 * DEVUELVE UNA LISTA DE MEDIDAS FRANQUICIA DADO UNA LINEASEGUROID, MODULO Y RIESGOELEGIDO
	 * PRIMERO CONSULTA CON EL NIFNIF Y SI LA LISTA ESTA VACIA RECOGE LOS DATOS CON NIFCIF NULO
	 */
	
	public List<MedidaFranquicia> getLstMedidasFranquicia(String nifcif, long lineaseguroid, String modulo, Character elegido){
		logger.debug("init - [PolizaDao] getLstMedidasFranquicia");
		List<MedidaFranquicia> resultado = null;
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(MedidaFranquicia.class);
		criteria.add(Restrictions.eq(NIF_CIF, nifcif));
		criteria.add(Restrictions.eq(MODULO_ID_COD_MODULO, modulo));
		criteria.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
		criteria.add(Restrictions.eq(RIESGO_ELEGIDO, elegido));
		List<MedidaFranquicia> lstMedidasFranquicia= criteria.list();
		if (lstMedidasFranquicia.size()==0){
			List<MedidaFranquicia> lstMedidasFranquici2 = null;
			Criteria criteria2 = session.createCriteria(MedidaFranquicia.class);
			criteria.add(Restrictions.eq(NIF_CIF, nifcif));
			criteria2.add(Restrictions.eq(MODULO_ID_COD_MODULO, modulo));
			criteria2.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
			criteria2.add(Restrictions.isNull(RIESGO_ELEGIDO));
			lstMedidasFranquici2= criteria2.list();
			
			if (lstMedidasFranquici2.size()==0){
				List<MedidaFranquicia> lstMedidasFranquici3 = null;
				Criteria criteria3 = session.createCriteria(MedidaFranquicia.class);
				criteria3.add(Restrictions.isNull(NIF_CIF));
				criteria3.add(Restrictions.eq(MODULO_ID_COD_MODULO, modulo));
				criteria3.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
				criteria3.add(Restrictions.eq(RIESGO_ELEGIDO, elegido));
				lstMedidasFranquici3= criteria3.list();
				
				if (lstMedidasFranquici3.size()==0){
					List<MedidaFranquicia> lstMedidasFranquici4 = null;
					Criteria criteria4 = session.createCriteria(MedidaFranquicia.class);
					criteria4.add(Restrictions.isNull(NIF_CIF));
					criteria4.add(Restrictions.eq(MODULO_ID_COD_MODULO, modulo));
					criteria4.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
					criteria4.add(Restrictions.isNull(RIESGO_ELEGIDO));
					lstMedidasFranquici4= criteria4.list();
					resultado = lstMedidasFranquici4;
				}
				else {
					resultado = lstMedidasFranquici3;
				}
			}
			else {
				resultado = lstMedidasFranquici2;
			}
			
		}
		else {
			resultado = lstMedidasFranquicia;
		}
		
		
		logger.debug("end - [PolizaDao] getLstMedidasFranquicia");
		return resultado;
	}
	
	public List<Poliza> getListPolizasAseg(long idAseg,Poliza poliza, String OpTipoPol){
		logger.debug("init - [PolizaDao] getListPolizasAseg");
		List<Poliza> listPolizasAseg = null;
		
		Session session = obtenerSession();
		try{
			Criteria criteria =	session.createCriteria(Poliza.class);
			if(StringUtils.nullToString(idAseg)!=null){
				criteria.add(Restrictions.eq("asegurado.id", idAseg));
			}
			if(poliza.getOficina()!= null && !poliza.getOficina().equals("")){
				criteria.add(Restrictions.eq("oficina", poliza.getOficina()));
			}
			if(poliza.getUsuario() !=null && !poliza.getUsuario().getCodusuario().equals("")){
				criteria = criteria.createAlias(USUARIO, "u");
				criteria.add(Restrictions.eq("u.codusuario", poliza.getUsuario().getCodusuario()));
			}
			if(poliza.getColectivo().getIdcolectivo()!=null && !poliza.getColectivo().getIdcolectivo().equals("")){
				criteria = criteria.createAlias(COLECTIVO, "col");
				criteria.add(Restrictions.eq("col.idcolectivo", poliza.getColectivo().getIdcolectivo()));
			}
			criteria = criteria.createAlias(LINEA, "lin");
			if(poliza.getLinea().getCodplan()!=null){
				criteria.add(Restrictions.eq(LIN_COD_PLAN, poliza.getLinea().getCodplan()));
			}
			if(poliza.getLinea().getCodlinea()!=null){
				criteria.add(Restrictions.eq(LIN_COD_LINEA,poliza.getLinea().getCodlinea()));
			}
			if(poliza.getCodmodulo()!=null && !poliza.getCodmodulo().equals("")){
				criteria.add(Restrictions.eq(COD_MODULO,poliza.getCodmodulo()));
				//criteria.add(Restrictions.ilike(COD_MODULO, "%" + poliza.getCodmodulo() + "%"));
			}
			if(poliza.getReferencia()!=null && !poliza.getReferencia().equals("")){
				criteria.add(Restrictions.ilike(REFERENCIA, "%" + poliza.getReferencia() + "%"));
			}
			if (OpTipoPol.equals("1")){
				criteria.add(Restrictions.eq(TIPO_REFERENCIA, "P"));
			}else if (OpTipoPol.equals("0")){
				criteria.add(Restrictions.eq(TIPO_REFERENCIA, "C"));
			}
			listPolizasAseg=criteria.list();
			
		} catch (Exception ex) {
			logger.error(ERROR_BBDD + ex.getMessage());
		}
		logger.debug("end - [PolizaDao] getListPolizasAseg");
		return listPolizasAseg;
	}
	
	public String getTotalProdComparativa(String modulo,Poliza poliza){
		String resultado = "";
		BigDecimal resBig = null;
		logger.debug("init - [PolizaDao] getTotalProdComparativa");
		try{
			Session session = obtenerSession();
			Query query = null;
			query = session.createQuery("select sum(cap.produccion) from com.rsi.agp.dao.tables.poliza.CapAsegRelModulo cap where "
					+ "cap.capitalAsegurado.idcapitalasegurado in(select capi.idcapitalasegurado from com.rsi.agp.dao.tables.poliza.CapitalAsegurado capi "
					+ "where capi.parcela.idparcela in (select par.idparcela from com.rsi.agp.dao.tables.poliza.Parcela par "
					+ "where par.poliza.idpoliza=" + poliza.getIdpoliza() + ")) and cap.codmodulo='" + modulo + "'");
					
			resBig = (BigDecimal) query.uniqueResult();
			if (resBig !=null){
				resultado = resBig.toString();
			}
		} catch (Exception ex) {
			logger.error(ERROR_BBDD + ex.getMessage());
		}
		logger.debug("end - [PolizaDao] getTotalProdComparativa");
	return resultado;	
	}
	
	public List<ConceptoCubiertoModulo> getMapConceptoCubMod(Long lineaseguroid,String codmodulo){
		List<ConceptoCubiertoModulo> lstConCubMod = new ArrayList<ConceptoCubiertoModulo>();
		try{
			Session session = obtenerSession();
			//Query query = null;
			Criteria criteria = session.createCriteria(ConceptoCubiertoModulo.class);
			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
			lstConCubMod= criteria.list();
			
			/*query = session.createQuery("Select c.id.columnamodulo, c.diccionarioDatos.codconcepto, c.diccionarioDatos.desconcepto "
					+ "from com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo c where c.id.lineaseguroid=" + lineaseguroid 
					+ " and c.modulo.id.codmodulo=" + codmodulo + "'");*/
		
		} catch (Exception ex) {
			logger.error(ERROR_BBDD + ex.getMessage());
		}
		return lstConCubMod;
	}
	
	public BigDecimal getCodigoConceptoCubMod(Long lineaseguroid,String codmodulo, BigDecimal columnaVinc){
		List<ConceptoCubiertoModulo> lstConCubMod = new ArrayList<ConceptoCubiertoModulo>();
		BigDecimal codigo=null;
		try{
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(ConceptoCubiertoModulo.class);
			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria.add(Restrictions.eq(MODULO_ID_COD_MODULO, codmodulo));
			criteria.add(Restrictions.eq(MODULO_ID_COD_MODULO, columnaVinc));
			
			lstConCubMod = criteria.list();
			codigo = lstConCubMod.get(0).getDiccionarioDatos().getCodconcepto();
		} catch (Exception ex) {
			logger.error(ERROR_BBDD + ex.getMessage());
		}
		return codigo;
		
	}
	
	public BigDecimal getValorVincValMod(Long lineaseguroid,String codmodulo, BigDecimal filaModulo,BigDecimal colModulo, BigDecimal codigoVinc, BigDecimal codigo, BigDecimal valorcodigo){
		List<VinculacionValoresModulo> objetoresultado= null;
		BigDecimal resultado = null;
		logger.debug("getValorVincValMod("+lineaseguroid+","+codmodulo+","+filaModulo+","+colModulo+","+codigoVinc+","+codigo+","+valorcodigo+")");
		try{
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(VinculacionValoresModulo.class);
			criteria.add(Restrictions.eq(ID_LINEA_SEGURO_ID, lineaseguroid));
			criteria = criteria.createAlias("caracteristicaModuloByFkVincValModCaracMod1", "carMod");
			criteria.add(Restrictions.eq("carMod.id.codmodulo", codmodulo));
			criteria.add(Restrictions.eq("carMod.id.filamodulo", filaModulo));
			criteria.add(Restrictions.eq("carMod.id.columnamodulo", colModulo));
			switch (codigo.intValue()) {
			case 174:// CALCULO INDEMNIZACION 174
				criteria = criteria.createAlias("calculoIndemnizacionByCalcindemneleg", "calcInd");
				criteria.add(Restrictions.eq("calcInd.codcalculo", valorcodigo));
					break;
			case 362:// % CAPITAL ASEGURADO 362
				criteria = criteria.createAlias("capitalAseguradoElegibleByPctcapitalasegeleg", "capAsg");
				criteria.add(Restrictions.eq("capAsg.pctcapitalaseg", valorcodigo));
					break;
			case 121:// % MINIMO INDEMNIZABLE 121
				criteria = criteria.createAlias("minimoIndemnizableElegibleByPctminindemneleg", "minInd");
				criteria.add(Restrictions.eq("minInd.pctminindem", valorcodigo));
					break;
			case 170:// TIPO FRANQUICIA 170
				criteria = criteria.createAlias("tipoFranquiciaByTipofranquiciaeleg", "tipoFr");
				criteria.add(Restrictions.eq("tipoFr.codtipofranquicia", valorcodigo));
					break;
			case 120:// % FRANQUICIA 120
				criteria = criteria.createAlias("pctFranquiciaElegibleByCodpctfranquiciaeleg", "pctF");
				criteria.add(Restrictions.eq("pctF.codpctfranquiciaeleg", valorcodigo));
					break;
			case 175:// GARANTIZADO 175
				criteria = criteria.createAlias("garantizadoByGarantizadoeleg", "garant");
				criteria.add(Restrictions.eq("garant.codgarantizado", valorcodigo));
					break;
			default:
				break;
			}
			objetoresultado = criteria.list();
			if(!objetoresultado.isEmpty()){
				logger.debug("Numero de VinculacionesValoresModulo: " + objetoresultado.size());
				switch (codigoVinc.intValue()) {
				case 174:// CALCULO INDEMNIZACION 174
					resultado = objetoresultado.get(0).getCalculoIndemnizacionByCalcindemnvinc().getCodcalculo();
						break;
				case 362:// % CAPITAL ASEGURADO 362
					resultado = objetoresultado.get(0).getCapitalAseguradoElegibleByPctcapitalasegvinc().getPctcapitalaseg();
						break;
				case 121:// % MINIMO INDEMNIZABLE 121
					resultado = objetoresultado.get(0).getMinimoIndemnizableElegibleByPctminindemnvinc().getPctminindem();
						break;
				case 170:// TIPO FRANQUICIA 170
					Character resTemp=objetoresultado.get(0).getTipoFranquiciaByTipofranquiciavinc().getCodtipofranquicia();
					resultado = new BigDecimal(resTemp.toString());
						break;
				case 120:// % FRANQUICIA 120
					resultado = objetoresultado.get(0).getPctFranquiciaElegibleByPctfranquiciavinc().getCodpctfranquiciaeleg();
						break;
				case 175:// GARANTIZADO 175
					resultado = objetoresultado.get(0).getGarantizadoByGarantizadovinc().getCodgarantizado();
						break;
				default:
					break;
				}
				
			}
			
		} catch (Exception ex) {
			logger.error("[PolizaDao] getValorVincValMod - Se ha producido un error en la BBDD", ex);
		}
		return resultado;
	}
	
	public boolean checkModulosPoliza(Poliza poliza) {
		boolean hayModulos=false;
		try{
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(ModuloPoliza.class);
			criteria.add(Restrictions.eq(POLIZA, poliza));
			criteria.setProjection(Projections.rowCount());
			Integer count= (Integer) criteria.uniqueResult();
			if (count > 0) {
				hayModulos=true;
			}
		} catch (Exception ex) {
			logger.error(ERROR_BBDD + ex.getMessage());
		}
		return hayModulos;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean existeCultivoVariedad(Long lineaSeguroId, Short cultivo, Short variedad) {
		boolean existe = false;
		try{
			Session session = obtenerSession();
			
			String sql= "select count(*) from TB_SC_C_VARIEDADES WHERE LINEASEGUROID = " + lineaSeguroId + 
				" AND CODCULTIVO = " + cultivo + " AND CODVARIEDAD = " + variedad;
			List list = session.createSQLQuery(sql).list();
			
			int numElem = ( (BigDecimal)list.get(0) ).intValue();
			
			if (numElem > 0)
				existe = true;
			
		} catch (Exception ex) {
			logger.error(ERROR_BBDD + ex.getMessage());
		}
		return existe;
	}
	
	public List<ComparativaFija> getListComparativasFijas(Long lineaseguroid, String modulo, BigDecimal clase){
		List<ComparativaFija> listCompFijas = new ArrayList<ComparativaFija>();
		try{
			Session session = obtenerSession(); 
			
			// Si la clase se ha informado, se buscan las comparativas fijas para el plan/línea, módulo y clase indicados
			if (clase != null) {			
				Criteria criteria = getCriteriaCompFijas(session, lineaseguroid, modulo);
				criteria.add(Restrictions.eq(CLASE, clase));
				listCompFijas= criteria.list();
			}
			
			// Si no se ha encontrado nada, se buscan las comparativas fijas sin filtrar por la clase
			if (listCompFijas == null || listCompFijas.isEmpty()) {
				Criteria criteria = getCriteriaCompFijas(session, lineaseguroid, modulo);
				criteria.add(Restrictions.isNull(CLASE));
				listCompFijas= criteria.list();
			}
						
		} catch (Exception ex) {
			logger.error("[PolizaDao] getListComparativasFijas - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return listCompFijas;				
	}
	
	/**
	 * Genera el criteria genérico para la búsqueda de comparativas fijas
	 * @param lineaseguroid
	 * @param modulo
	 * @return
	 */
	private Criteria getCriteriaCompFijas (Session session, Long lineaseguroid, String modulo) {
		
		Criteria criteria = session.createCriteria(ComparativaFija.class);
		criteria.add(Restrictions.eq("lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq(COD_MODULO, modulo));
		
		criteria.addOrder(Order.asc("comparativa"));
		criteria.addOrder(Order.desc("fichvinculacionexterna"));
		criteria.addOrder(Order.asc("id"));
		
		return criteria;
	}	
	
	public List<String> getLstModulosElegidos(Long idPoliza) {
		Session session = obtenerSession();
		List<String> lstModulosElegidos=null;
		Criteria criteria = session.createCriteria(ModuloPoliza.class);
		criteria.add(Restrictions.eq(ID_ID_POLIZA, idPoliza));
		criteria.setProjection(Projections.distinct(Projections.property(ID_COD_MODULO)));
		lstModulosElegidos = criteria.list();
		
		return lstModulosElegidos;
	}
	
	public List<Parcela> getlistParcelas(Long idPoliza){
		List<Parcela> lstParcelas = new ArrayList<Parcela>();
		Session session = obtenerSession();
		
		Criteria criteria = session.createCriteria(Parcela.class);
		criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));
		criteria.addOrder(Order.asc("termino.id.codprovincia"));
		criteria.addOrder(Order.asc("termino.id.codcomarca"));
		criteria.addOrder(Order.asc("termino.id.codtermino"));
		criteria.addOrder(Order.asc("codcultivo"));
		
		lstParcelas= criteria.list();
		//if (lstParcelas !=null){
			return lstParcelas;
		
	}
	
	public List<BigDecimal> getHojasPoliza(Long idPoliza){
		Session session = obtenerSession();
		List<BigDecimal> list = null;
		
		String sql = "select distinct(hoja) from tb_parcelas p where p.idpoliza = "+ idPoliza;
		if (session.createSQLQuery(sql).list().size()>0){
			list =session.createSQLQuery(sql).list();
		}
		return list;
	}

	@Override
	public List<BigDecimal> getCodConceptoMod(Long linea, String codmodulo) {
		Session session = obtenerSession();
		List<BigDecimal> list = null;
		
		String sql = "SELECT CODCONCEPTOCBRTOMOD FROM TB_SC_C_CONCEPTO_CBRTO_MOD WHERE LINEASEGUROID = "+ linea +
					 " AND CODMODULO = '" + codmodulo + "'";
		if (session.createSQLQuery(sql).list().size()>0){
			list =session.createSQLQuery(sql).list();
		}
		return list;
		
	}
	
	public List<Poliza> dameListaPolizasCplByPpl(Long idpoliza){
		List<Poliza> lstPolizas = new ArrayList<Poliza>();
		Session session = obtenerSession();
		try{
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria = criteria.createAlias("polizaPpal", "ppal");
			
			criteria.add(Restrictions.eq("ppal.idpoliza", idpoliza));
			criteria.add(Restrictions.eq(TIPO_REFERENCIA, 'C'));
			
			lstPolizas= criteria.list();
		} catch (Exception ex) {
			logger.error("[PolizaDao] dameListaPolizasCplByPpl - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return lstPolizas;
	}

	public List<EnvioAgroseguro> getEnvioAgroseguro(Long idPoliza,String codModulo)
			throws DAOException {
		List<EnvioAgroseguro> lstEnvios = new ArrayList<EnvioAgroseguro>();
		Session session = obtenerSession();
		
		try{
			Criteria criteria = session.createCriteria(EnvioAgroseguro.class);
			criteria = criteria.createAlias(POLIZA, POLIZA);
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));
			criteria.add(Restrictions.eq("tipoenvio", "VL"));
			criteria.add(Restrictions.eq(COD_MODULO, codModulo));
			criteria.addOrder(Order.desc("fechaEnvio"));
			
			lstEnvios= criteria.list();
		
		} catch (Exception ex) {
			logger.error("[PolizaDao] getEnvioAgroseguro - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return lstEnvios;
	}
	
	/**
	 * Metodo para obtener un objeto "EnvioAgroseguro" a partir de su id.
	 */
	public EnvioAgroseguro getEnvioAgroseguro(Long idEnvio) throws DAOException {
		EnvioAgroseguro envio = null;
		Session session = obtenerSession();
		
		try{
			Criteria criteria = session.createCriteria(EnvioAgroseguro.class);
			criteria.add(Restrictions.eq("id", idEnvio));
			
			envio = (EnvioAgroseguro) criteria.list().get(0);
		
		} catch (Exception ex) {
			logger.error("[PolizaDao] getEnvioAgroseguro - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return envio;
	}
	
	/**
	 * Este método en realidad no tiene que buscar en pólizas de copy, sino en pólizas de base de datos, ya que puede que
	 * la copy no se haya descargado aun.
	 */
	public List<com.rsi.agp.dao.tables.poliza.Poliza> existePolizaPlanLinea(String nifCif, BigDecimal[] codPlan,
			BigDecimal codLinea, BigDecimal clase, BigDecimal entidad, boolean situacionAct) throws DAOException {
		
		Session session = obtenerSession();
		BigDecimal[] estadoPol;
		if (situacionAct) {
			estadoPol = new BigDecimal[1];
			estadoPol[0] = Constants.ESTADO_POLIZA_DEFINITIVA;
		}else {
			estadoPol = new BigDecimal[2];
			estadoPol[0] = Constants.ESTADO_POLIZA_DEFINITIVA;
			estadoPol[1] = Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL;
		}
		try {
			//Obtengo las polizas para ese asegurado del plan / línea indicados como parámetros
			Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.poliza.Poliza.class);

			criteria.createAlias("asegurado", "aseg");
			criteria.createAlias(LINEA, "lin");
			criteria.createAlias(ESTADO_POLIZA, ESTADO);
			criteria.createAlias("aseg.entidad", ENTIDAD);
			
			criteria.add(Restrictions.eq(ASEG_NIF_CIF, nifCif));
			criteria.add(Restrictions.in(LIN_COD_PLAN, codPlan));
			criteria.add(Restrictions.eq(LIN_COD_LINEA, codLinea));
			criteria.add(Restrictions.in(ESTADO_ID_ESTADO,estadoPol ));
			criteria.add(Restrictions.eq(ENTIDAD_COD_ENTIDAD, entidad));
			criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
			if (clase != null){
				criteria.add(Restrictions.eq(CLASE, clase));
			}
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR_ACCESO_BD,
					ex);
		} finally {
		}
	}
	
	/**
	 * ASF - 17/9/2012 - Ampliacion de la Mejora 79: preguntar si desea recalcular produccion al cargar la copy
	 * Metodo para obtener el nÃºmero de copys disponibles para una poliza.
	 * @param polizaBean
	 * @return
	 */
	public int getNumPolizasContratadas(BigDecimal codplan, final BigDecimal codlinea, final String nifasegurado,final boolean polAnterior){
		
		List<BigDecimal> planAnterior = new ArrayList<BigDecimal>();
		List<BigDecimal> estadosPol = new ArrayList<BigDecimal>();
		Session session = obtenerSession();
		String sql= "select count(*) " +
				"from TB_POLIZAS P, TB_ASEGURADOS A, TB_LINEAS L " +
				"where P.IDASEGURADO = A.ID AND P.LINEASEGUROID = L.LINEASEGUROID";
		
		if (polAnterior) {
			planAnterior.add(codplan.subtract(new BigDecimal (1)));
			planAnterior.add(codplan.subtract(new BigDecimal (2)));
			planAnterior.add(codplan.subtract(new BigDecimal (3)));
			sql+= " AND  L.codplan in " + StringUtils.toValoresSeparadosXComas(planAnterior, false, true) ; 
		
		}else {
			codplan = codplan.subtract(new BigDecimal(1));
			sql+= " AND L.codplan = " + codplan;
		}
		sql+= 	" AND A.nifcif = '" + nifasegurado + "'" +  
				" AND L.CODLINEA = " + codlinea +
				" AND P.tipoRef = '" +  Constants.MODULO_POLIZA_PRINCIPAL+"'";
		
		if (polAnterior) {
			estadosPol.add(Constants.ESTADO_POLIZA_DEFINITIVA);
			estadosPol.add(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
    		sql+= " AND P.idestado in " + StringUtils.toValoresSeparadosXComas(estadosPol, false, true);
		}else {
			sql+= " AND P.idestado = " + Constants.ESTADO_POLIZA_DEFINITIVA;
		}
		return ( (BigDecimal)session.createSQLQuery(sql).list().get(0) ).intValue();
	}
	
	public List<Poliza> getPolizasDefinitivas(final BigDecimal codPlan, final BigDecimal codLinea, 
			final String nifAsegurado, final BigDecimal codEntidad,
			final PolizaActualizadaSort sort, final int rowStart,
			final int rowEnd) throws BusinessException{
		List<Poliza> polizas = null;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.poliza.Poliza.class);

						
			criteria.createAlias(ASEGURADO, ASEGURADO);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(ESTADO_POLIZA,ESTADO);
			criteria.createAlias(COLECTIVO, COLECTIVO);
			criteria.createAlias(COLECTIVO_TOMADOR, TOMADOR);
			criteria.createAlias(TOMADOR_ENTIDAD, ENTIDAD);
			
			BigDecimal[] planAnterior = new BigDecimal[3];
    		planAnterior[0]= codPlan.subtract(new BigDecimal (1));
    		planAnterior[1]= codPlan.subtract(new BigDecimal (2));
    		planAnterior[2]= codPlan.subtract(new BigDecimal (3));
    		
    		BigDecimal estadoPol = Constants.ESTADO_POLIZA_DEFINITIVA;
    		
    		criteria.add(Restrictions.in(LINEA_CODPLAN, planAnterior));
    		criteria.add(Restrictions.eq("asegurado.nifcif", nifAsegurado));
			criteria.add(Restrictions.eq("linea.codlinea", codLinea));
			criteria.add(Restrictions.eq(ESTADO_ID_ESTADO, estadoPol));
			criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
			criteria.add(Restrictions.eq(ENTIDAD_COD_ENTIDAD, codEntidad));
    		
			criteria=sort.execute(criteria);
			// Primer registro
			criteria.setFirstResult(rowStart);
			criteria.setMaxResults(rowEnd - rowStart);
			polizas=criteria.list();
			
			
		} catch (Exception e) {
			logger.error("Error al obtener las pólizas definitivas para el nif-entidad " + nifAsegurado + " - " + codEntidad, e);
			throw new BusinessException(ERROR_ACCESO_BD + e.getMessage());
		}
		return polizas;
		
	}
	
	
	@Override
	public int getPolizasDefinitivasCount(final BigDecimal codPlan, final BigDecimal codLinea, final String nifAsegurado,
			final BigDecimal codEntidad) {
		logger
				.debug("init - [PolizaDao] getPolizasParaActualizarCount");
		Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						BigDecimal[] planAnterior = new BigDecimal[3];
		        		planAnterior[0]= codPlan.subtract(new BigDecimal (1));
		        		planAnterior[1]= codPlan.subtract(new BigDecimal (2));
		        		planAnterior[2]=codPlan.subtract(new BigDecimal (3));
		        		
		        		BigDecimal estadoPol =  Constants.ESTADO_POLIZA_DEFINITIVA;
						Criteria criteria = session.createCriteria(Poliza.class);
						
						// Alias
		                criteria.createAlias(ASEGURADO, "aseg");
						criteria.createAlias(LINEA, "lin");
						criteria.createAlias(ESTADO_POLIZA,ESTADO);
						criteria.createAlias(COLECTIVO, COLECTIVO);
						criteria.createAlias(COLECTIVO_TOMADOR, TOMADOR);
						criteria.createAlias(TOMADOR_ENTIDAD, ENTIDAD);
						
						// Filtro
						criteria.add(Restrictions.eq(ASEG_NIF_CIF, nifAsegurado));
						criteria.add(Restrictions.in(LIN_COD_PLAN, planAnterior));
						criteria.add(Restrictions.eq(LIN_COD_LINEA, codLinea));
						criteria.add(Restrictions.eq(ESTADO_ID_ESTADO, estadoPol));
						criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
						criteria.add(Restrictions.eq(ENTIDAD_COD_ENTIDAD, codEntidad));
						
						criteria.setProjection(Projections.rowCount())
								.uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger
				.debug("end - [SobrePrecioDao] getPolizasParaActualizarCount");
		return count.intValue();
		
	}
	
	/**
	 * Metodo para obtener una poliza para el plan, linea y nif de asegurado indicados como parametros
	 */
	public Poliza getPolizaContratada(BigDecimal codplan, final BigDecimal codlinea, final String nifasegurado,final boolean polAnterior){
		
		Session session = obtenerSession();
		try {
			
			//Obtengo las polizas para ese asegurado del plan / linea indicados como parametros
			Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.poliza.Poliza.class);

			criteria.createAlias(ASEGURADO, "aseg");
			criteria.createAlias(LINEA, "lin");
			criteria.createAlias(ESTADO_POLIZA, ESTADO);
			
			criteria.add(Restrictions.eq(ASEG_NIF_CIF, nifasegurado));
			criteria.add(Restrictions.eq(LIN_COD_LINEA, codlinea));
			
			if (polAnterior) {
				BigDecimal[] planAnterior = new BigDecimal[3];
	    		planAnterior[0]= codplan.subtract(new BigDecimal (1));
	    		planAnterior[1]= codplan.subtract(new BigDecimal (2));
	    		planAnterior[2]= codplan.subtract(new BigDecimal (3));
	    		
	    		BigDecimal[] estadoPol = new BigDecimal[2];
	    		estadoPol[0] = Constants.ESTADO_POLIZA_DEFINITIVA;
	    		estadoPol[1] = Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL;
	    		
				criteria.add(Restrictions.in(LIN_COD_PLAN, planAnterior));
				criteria.add(Restrictions.in(ESTADO_ID_ESTADO, estadoPol));
				criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
			}else {
				
				criteria.add(Restrictions.eq(LIN_COD_PLAN, codplan.subtract(new BigDecimal(1))));
				criteria.add(Restrictions.eq(ESTADO_ID_ESTADO, Constants.ESTADO_POLIZA_DEFINITIVA));
				criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
			}
			
			
			
			return (Poliza) criteria.list().get(0);
			
		} catch (Exception ex) {
			logger.error("Error al obtener la poliza contratada para el nif " + nifasegurado, ex);
		} finally {
		}
		return null;
	}
	
	/**
	 * Arrastre de Parcelas de la Copy/Poliza
	 * 
	 * @param lineaseguroid Identificador de plan/linea
	 * @param idPolizaDestino Identificador de la poliza destino
	 * @param idcopy Identificador de la copy
	 * @param codlinea Linea a la que pertenece la poliza
	 * @param codplan Plan correspondiente a la poliza origen
	 * @throws Exception
	 */
	public void arrastreParcelas(Long lineaseguroid, Long idPolizaDestino, Long idcopy, Long idPolizaOrigen, BigDecimal clase)throws Exception {
		// llamamos al PL para que arrastre las parcelas
		
		String procedure ="";
		Map<String, Object> parametros = new HashMap<String, Object>();
		if (idcopy != null){
			procedure = "PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy (P_LINEASEGUROID IN NUMBER, " +
					"P_IDCOPY        IN NUMBER, " +
					"P_IDPOLIZA_DEST IN NUMBER, " +
					"P_CLASE         IN NUMBER)";
			
			parametros.put("P_LINEASEGUROID", lineaseguroid);
			parametros.put("P_IDCOPY", idcopy);
			parametros.put("P_IDPOLIZA_DEST", idPolizaDestino);
			parametros.put("P_CLASE", clase);
			
			logger.debug("Llamada al procedimiento PQ_SITUACION_ACTUALIZADA.arrastre_parcelas_copy con los siguientes parametros: ");
			logger.debug("    P_LINEASEGUROID: " + lineaseguroid);
			logger.debug("    P_IDCOPY: " + idcopy);
			logger.debug("    P_IDPOLIZA_DEST: " + idPolizaDestino);
			logger.debug("    P_CLASE: " + clase);
		}else{
			procedure = "PQ_SITUACION_ACTUALIZADA.arrastre_parcelas (P_LINEASEGUROID IN NUMBER, " +
					"P_IDPOLIZA_ORIG IN NUMBER, " +
					"P_IDPOLIZA_DEST IN NUMBER, " +
					"P_CLASE         IN NUMBER)";
			
			parametros.put("P_LINEASEGUROID", lineaseguroid);
			parametros.put("P_IDPOLIZA_ORIG", idPolizaOrigen);
			parametros.put("P_IDPOLIZA_DEST", idPolizaDestino);
			parametros.put("P_CLASE", clase);
			
			logger.debug("Llamada al procedimiento PQ_SITUACION_ACTUALIZADA.arrastre_parcelas con los siguientes parametros: ");
			logger.debug("    P_LINEASEGUROID: " + lineaseguroid);
			logger.debug("    P_IDPOLIZA_ORIG: " + idPolizaOrigen);
			logger.debug("    P_IDPOLIZA_DEST: " + idPolizaDestino);
			logger.debug("    P_CLASE: " + clase);
		}

		// Ejecutamos el PL
		databaseManager.executeStoreProc(procedure, parametros);
	}

	/**
	 * Actualiza los datos de la poliza
	 */
	public Poliza savePoliza(Poliza poliza) throws DAOException {
		
		Session session = obtenerSession();
		try {
			session.saveOrUpdate(poliza);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad", ex);
		} finally {
		}

		return poliza;
	}
	
	@Override
	public int getPolizasParaActualizarCount(final Poliza poliza) {
		logger
				.debug("init - [PolizaDao] getPolizasParaActualizarCount");
		Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						BigDecimal[] planAnterior = new BigDecimal[3];
		        		planAnterior[0]= poliza.getLinea().getCodplan().subtract(new BigDecimal (1));
		        		planAnterior[1]= poliza.getLinea().getCodplan().subtract(new BigDecimal (2));
		        		planAnterior[2]= poliza.getLinea().getCodplan().subtract(new BigDecimal (3));
		        		
		        		BigDecimal[] estadoPol = new BigDecimal[2];
		        		estadoPol[0] = Constants.ESTADO_POLIZA_DEFINITIVA;
		        		estadoPol[1] = Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL;
						Criteria criteria = session.createCriteria(Poliza.class);
						
						// Alias
		                criteria.createAlias(ASEGURADO, "aseg");
						criteria.createAlias(LINEA, "lin");
						criteria.createAlias(ESTADO_POLIZA,ESTADO);
						criteria.createAlias(COLECTIVO, COLECTIVO);
						criteria.createAlias(COLECTIVO_TOMADOR, TOMADOR);
						criteria.createAlias(TOMADOR_ENTIDAD, ENTIDAD);
		        								
						// Filtro
						criteria.add(Restrictions.eq(ASEG_NIF_CIF, poliza.getAsegurado().getNifcif()));
						criteria.add(Restrictions.in(LIN_COD_PLAN, planAnterior));
						criteria.add(Restrictions.eq(LIN_COD_LINEA, poliza.getLinea().getCodlinea()));
						criteria.add(Restrictions.in(ESTADO_ID_ESTADO, estadoPol));
						criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
						criteria.add(Restrictions.eq(ENTIDAD_COD_ENTIDAD, poliza.getColectivo().getTomador().getEntidad().getCodentidad()));
						
						criteria.setProjection(Projections.rowCount())
								.uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger
				.debug("end - [SobrePrecioDao] getPolizasParaActualizarCount");
		return count.intValue();
		
	}
	
	@SuppressWarnings("all")
	public Collection<Poliza> getPolizasParaActualizar(final Poliza poliza, final int rowStart, final int rowEnd, final PolizaActualizadaSort sort) throws BusinessException {
		
		try{
		logger.debug("init - [PolizaDao] getPolizasParaActualizar");
			List<Poliza> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	
        		BigDecimal[] planAnterior = new BigDecimal[3];
        		planAnterior[0]= poliza.getLinea().getCodplan().subtract(new BigDecimal (1));
        		planAnterior[1]= poliza.getLinea().getCodplan().subtract(new BigDecimal (2));
        		planAnterior[2]= poliza.getLinea().getCodplan().subtract(new BigDecimal (3));
        		
        		BigDecimal[] estadoPol = new BigDecimal[2];
        		estadoPol[0] = Constants.ESTADO_POLIZA_DEFINITIVA;
        		estadoPol[1] = Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL;
        		
        		Criteria criteria = session.createCriteria(Poliza.class);     
                
                // Alias
                criteria.createAlias(ASEGURADO, ASEGURADO);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(ESTADO_POLIZA, ESTADO);
				criteria.createAlias(COLECTIVO, COLECTIVO);
				criteria.createAlias(COLECTIVO_TOMADOR, TOMADOR);
				criteria.createAlias(TOMADOR_ENTIDAD, ENTIDAD);
        		
				// Filtro
				criteria.add(Restrictions.eq("asegurado.nifcif", poliza.getAsegurado().getNifcif()));
				criteria.add(Restrictions.in(LINEA_CODPLAN, planAnterior));
				criteria.add(Restrictions.eq("linea.codlinea", poliza.getLinea().getCodlinea()));
				criteria.add(Restrictions.in(ESTADO_ID_ESTADO, estadoPol));
				criteria.add(Restrictions.eq(TIPO_REFERENCIA, Constants.MODULO_POLIZA_PRINCIPAL));
				criteria.add(Restrictions.eq(ENTIDAD_COD_ENTIDAD, poliza.getColectivo().getTomador().getEntidad().getCodentidad()));
				
				// Filtro
				
				// Ordenacion
				//sort.clearSorts();//eliminamos las ordenaciones anteriores
				criteria = sort.execute(criteria);
				
                // Primer registro
                criteria.setFirstResult(rowStart);
                // NÃºmero mÃ¡ximo de registros a mostrar
                criteria.setMaxResults(rowEnd - rowStart);
                // Devuelve el listado de polizas
                return criteria.list();
            }
        });
		logger.debug("end - [PolizaDao] getPolizasParaActualizar");
        return applications;
		}catch (Exception e) {
			throw new BusinessException(ERROR_ACCESO_BD + e.getMessage());
		}
		
	}

	/**
	 * @return List
	 * Recuperamos los codConceptos del organizador 
	 * para el uso 31 â€“ Poliza y ubicacion 16- Parcela Datos Variables
	 * 
	 */
	public List<BigDecimal> getCodsConceptoOrganizador(Long lineaseguroid)
			throws DAOException {
		
		List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
		Session session = this.obtenerSession();
		Criteria criteria = session.createCriteria (OrganizadorInformacion.class);
		
		criteria.createAlias(LINEA, LINEA);
		criteria.createAlias("uso", "uso");
		criteria.createAlias("ubicacion", "ubicacion");
		criteria.createAlias("diccionarioDatos", "diccionarioDatos");
		
		criteria.add(Restrictions.eq(LINEA_LINEA_SEGURO_ID, lineaseguroid));
		criteria.add(Restrictions.eq("uso.coduso", Constants.USO_POLIZA));
		criteria.add(Restrictions.eq("ubicacion.codubicacion", OrganizadorInfoConstants.UBICACION_PARCELA_DV));
		criteria.setProjection(Projections.distinct(Projections.property("diccionarioDatos.codconcepto")));
		
		codsConceptos = criteria.list();
		return codsConceptos;
	}

	/** DAA 15/01/2013
	 *  borra las subvenciones de los modulos no elegidos de la poliza actual
	 *  @param codModulo
	 * 	@param poliza 
	 *  @param lineaseguroid
	 * 	@throws DAOException
	 */
	public void deleteSubvsEnesaModsNoElec(String codModulo, Long idPoliza, Long lineaseguroid) throws DAOException {
		
		Session session = obtenerSession();
		String sql = "delete from tb_subvs_aseg_enesa su where su.idpoliza = " + idPoliza + 
					" and su.lineaseguroid = " + lineaseguroid +
					" and su.idsubvencion in (select s.id from tb_sc_c_subvs_enesa s where s.codmodulo != '"+ codModulo +
					"' and s.codmodulo != '99999' and s.lineaseguroid = su.lineaseguroid)";
		
		Query queryDelete = session.createSQLQuery(sql);
		queryDelete.executeUpdate();
	}
	
	/**
	 * Borra las subvenciones de la CCAA asociadas a la poliza que no corresponden con el modulo elegido
	 */
	public void deleteSubvsCCAAModsNoElec(String codModulo, Long idPoliza,	Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		String sql = "delete from tb_subvs_aseg_ccaa su where su.idpoliza = " + idPoliza + 
					" and su.lineaseguroid = " + lineaseguroid +
					" and su.idsubvencion in (select s.id from tb_sc_c_subvs_ccaa s where s.codmodulo != '"+ codModulo +
					"' and s.codmodulo != '99999' and s.lineaseguroid = su.lineaseguroid)";
		
		Query queryDelete = session.createSQLQuery(sql);
		queryDelete.executeUpdate();
		
	}
	
	/**
	 * Devuelve el caracter que indica si la pac ya está cargada (S), se está cargando (X) o no se ha cargado (N)
	 * @param idPoliza
	 * @return
	 */
	public Character isPacCargada (Long idPoliza) {
		
		Character isCargada = new Character ('S');

		try {
			// Obtiene la póliza asociada al id
			Poliza p = (Poliza) get(Poliza.class, idPoliza);
			// Comprueba si se ha cargado la pac previamente
			if (p != null) isCargada = p.getPacCargada();
			// Si no se había cargado se actualiza el indicador a en proceso
			if (isCargada == null || Constants.PAC_CARGADA_NO.equals(isCargada)) {
				p.setPacCargada(Constants.PAC_PROCESO_CARGA);
				saveOrUpdate(p);
				//En este caso, el valor de PAC_CARGADA es nulo o NO, por lo que sí puedo 
				// continuar con la carga de parcelas => Devolvemos que no está cargada para que pueda continuar
				isCargada = Constants.PAC_CARGADA_NO;
			}
			
		} catch (Exception e) {
			logger.error("Ocurrió un error al comprobar si la pac está cargada para la póliza " + idPoliza, e);
		}
		
		return isCargada;
	}

	/** DAA 11/07/2013
	 * 	Actualiza el estado de la poliza tras la carga de la pac
	 */
	public void actualizaPacCargadaPoliza(Long idPoliza, Character estado) {
		final Character[] estados_arr = new Character[] {
				Constants.PAC_CARGADA_SI, Constants.PAC_CARGADA_NO,
				Constants.PAC_PROCESO_CARGA, Constants.PAC_CARGADA_PDTE };
		try {
			// Obtiene la pï¿½liza asociada al id
			Poliza p = (Poliza) get(Poliza.class, idPoliza);

			if (Constants.PAC_PROCESO_CARGA.equals(p.getPacCargada())) {
				if (ArrayUtils.contains(estados_arr, estado)) {
					p.setPacCargada(estado);
					saveOrUpdate(p);
				} else {
					throw new Exception("Estado no esperado");
				}
			}

		} catch (Exception e) {
			logger.error(
					"Ocurriï¿½ un error al actualizar el estado a pac cargada para la pï¿½liza "
							+ idPoliza, e);
		}
	}
	
	public boolean isOficinaConPolizas(String codOficina,BigDecimal codEntidad) throws DAOException{
		Integer count=0;
		
		try {
			Session session = obtenerSession();
			// anadimos cero por la izquierda hasta llegar a 4 digitos
			if (codOficina.length()<4) {
				while (codOficina.length()<4) {
					codOficina= "0" + codOficina;
				}
			}
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias(COLECTIVO, "col");
			criteria.createAlias("col.subentidadMediadora", "sm");
			criteria.createAlias("sm.entidad", "ent");
			
			criteria.add(Restrictions.eq("oficina", codOficina));
			criteria.add(Restrictions.eq("ent.codentidad", codEntidad));
			count = (Integer)criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en isOficinaConPolizas", e);
			throw new DAOException("Se ha producido un error en isOficinaConPolizas", e);
		}
	}
	
	public PagoPoliza existePagoPoliza(Poliza polizaBean) throws DAOException {
			
		List<PagoPoliza> listPagos = new ArrayList<PagoPoliza>(); 
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(PagoPoliza.class);
			
			criteria.add(Restrictions.eq("poliza.idpoliza", polizaBean.getIdpoliza()));
			
			listPagos = criteria.list();
			
			if (listPagos.size()>0) { // existe pago en la poliza (ya se ha guardado)
				return listPagos.get(0);
			}
			return null;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en existePagoPoliza", e);
			throw new DAOException("Se ha producido un error en existePagoPoliza", e);
		}
	}

	///probamos a realizar el update de la columna por cada complementaria
	public void actualizarEstadoComplementaria(Poliza poliza) throws Exception{
		try {
		Session session = obtenerSession();
		String sql = "update tb_polizas set idestado = '0'" +
				" where idpoliza_ppal = " + poliza.getIdpoliza() ;
		
		Query query = session.createSQLQuery(sql);
		query.executeUpdate();
	}catch (Exception e) {
		logger.error("Se ha producido un error en actualizaImporte", e);
		throw e;
	}
	}
	
	
	
	
	
	@Override
	public void actualizaImporte(Poliza poliza) throws Exception {
		try {
			Session session = obtenerSession();
			String sql = "update tb_polizas set importe = " + poliza.getImporte() + 
						" where idpoliza = " + poliza.getIdpoliza();
			
			Query query = session.createSQLQuery(sql);
			query.executeUpdate();
			if (!poliza.getEsFinanciada().equals('S')){
				String sql2 = "update tb_pagos_poliza set importe = " + poliza.getImporte() + 
					" where idpoliza = " + poliza.getIdpoliza();
		
				Query query2 = session.createSQLQuery(sql2);
				query2.executeUpdate();
			}
		}catch (Exception e) {
			logger.error("Se ha producido un error en actualizaImporte", e);
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean esPolizaGanado(final String referencia, final BigDecimal plan)
			throws DAOException {
		boolean result = false;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.add(Restrictions.eq(REFERENCIA, referencia));
			if (plan != null) {
				criteria.add(Restrictions.eq(LINEA_CODPLAN, plan));
			}
			criteria.setProjection(Projections
					.property("linea.esLineaGanadoCount"));
			List temp=criteria.list();
			if(temp.size()>0){
				result = Long.valueOf(1).equals(temp.get(0)); // POR
																		// COMPLEMENTARIAS
																		// PUEDE
																		// VENIR
																		// MÁS
																		// DE UN
																		// RESULTADO...
																		// NOS
																		// VALE
																		// CUALQUIERA
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error en esPolizaGanado", e);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean esPolizaGanadoByIdPoliza(Long idPoliza) throws DAOException {
		boolean result = false;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.add(Restrictions.eq("idpoliza", idPoliza));
			criteria.setProjection(Projections.property("linea.esLineaGanadoCount"));
			List temp=criteria.list();
			if(temp.size()>0){
				result = Long.valueOf(1).equals(temp.get(0)); // POR
																		// COMPLEMENTARIAS
																		// PUEDE
																		// VENIR
																		// MÁS
																		// DE UN
																		// RESULTADO...
																		// NOS
																		// VALE
																		// CUALQUIERA
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error en esPolizaGanado", e);
		}
		return result;
	}
	
public List<GruposNegocio>getGruposNegocio(Long idPoliza)throws DAOException {
		
		Session session = obtenerSession();
		List<GruposNegocio> listaGn = null;
		try {
			
			String  sql= "from GruposNegocio gn where gn.grupoNegocio IN " +
					"( " +
						"select distinct gn.grupoNegocio from GruposNegocio gn,  " +
						"TipoCapitalConGrupoNegocio tc,  GrupoRaza gr, Explotacion ex, Poliza pol " +
						"where tc.gruposNegocio.grupoNegocio = gn.grupoNegocio and " +
				   		"gr.codtipocapital = tc.codtipocapital and "+
				   		"gr.explotacion.id= ex.id and "+
				   		"pol.idpoliza = ex.poliza.idpoliza and "+
						"pol.idpoliza= " + idPoliza +
					")";
			
			 
			logger.debug(sql);
			
			

			Query hql = session.createQuery(sql);
			listaGn= ((List<GruposNegocio>)hql.list());

		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return listaGn;
		
	}
	

public List<GruposNegocio>getGruposNegocio(Long lineaseguroid, Long codGrupoRaza, Long codtipocapital)throws DAOException {
	
	Session session = obtenerSession();
	List<GruposNegocio> listaGn = null;
	try {
		
		String  sql= "from GruposNegocio gn where gn.grupoNegocio IN " +
				"( " +
					"select distinct gn.grupoNegocio from GruposNegocio gn,  " +
					"TipoCapitalConGrupoNegocio tc,  GrupoRaza gr, LineaGrupoNegocio lin " +
					"where tc.gruposNegocio.grupoNegocio = gn.grupoNegocio and " +
			   		"gr.codtipocapital = tc.codtipocapital and "+
					"gn.grupoNegocio = lin.id.grupoNegocio and " +
			   		"gr.codgruporaza = " + codGrupoRaza + 
			   		" and lin.linea.lineaseguroid = " + lineaseguroid +
			   		" and tc.codtipocapital = " + codtipocapital +
				")";
		
		 
		logger.debug(sql);
		
		

		Query hql = session.createQuery(sql);
		listaGn= ((List<GruposNegocio>)hql.list());

	} catch (Exception ex) {
		logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
		throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
	}
	return listaGn;
	
}
	

@SuppressWarnings("rawtypes")
public List getDatosVariablesEspecialesExplotacion(Long idExplotacion, long codConcepto)throws DAOException{
	Session session = obtenerSession();
	List registros = new ArrayList();
	try {
		String sql="SELECT distinct exp.ID, cob.CPM,cob.RIESGO_CUBIERTO, cob.CODMODULO, dv.VALOR from o02agpe0.TB_EXPLOTACIONES_COBERTURAS cob " +
				"inner join o02agpe0.TB_EXPLOTACIONES exp ON cob.IDEXPLOTACION= exp.ID " + 
				"inner join O02AGPE0.TB_POLIZAS pol on pol.idpoliza=exp.IDPOLIZA " +
				"inner join O02AGPE0.TB_GRUPO_RAZA_EXPLOTACION gr ON gr.IDEXPLOTACION=exp.ID " +  
				"inner join O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv ON dv.IDGRUPORAZA=gr.ID " +
				"where cob.ELEGIDA='S' and exp.ID=" + idExplotacion + " and  dv.CODCONCEPTO=" + codConcepto;
		logger.debug(sql);
		registros.addAll(session.createSQLQuery(sql).list());
		
	} catch (Exception ex) {
		logger.error("getDatosVariablesEspecialesExplotacion - Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
		throw new DAOException("getDatosVariablesEspecialesExplotacion - Se ha producido un error en el acceso a la BBDD",ex);
	}
	 
	return registros;
}

/**
 * Actualiza el flag de tener siniestros o no de la póliza
 * @author U029114 21/06/2017
 * @param idPoliza
 * @param caracter
 * @throws DAOException
 */
public void actualizaFlagTieneSiniestrosPoliza(Long idpoliza, Character caracter) throws DAOException {

	final Session session = obtenerSession();

	try {
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append( "update tb_polizas pol set tienesiniestros = '" + caracter+"'");
		stringQuery.append(" where pol.idpoliza = " + idpoliza);

		session.createSQLQuery(stringQuery.toString()).executeUpdate();

	}catch (Exception ex) {
		logger.info("Se ha producido un error actualizando el campo tienesiniestros de la poliza: " + ex.getMessage());
		throw new DAOException("Se ha producido un error actualizando el campo tienesiniestros de la poliza", ex);
	}

}

/**
 * Actualiza el estado de la póliza
 * @author U029114 14/09/2017
 * @param idPoliza
 * @param idestado
 * @throws DAOException
 */
public void actualizaEstadoPoliza(Long idpoliza, BigDecimal idestado) throws DAOException {

	final Session session = obtenerSession();

	try {
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append( "update tb_polizas pol set idestado = '" + idestado+"'");
		stringQuery.append(" where pol.idpoliza = " + idpoliza);

		session.createSQLQuery(stringQuery.toString()).executeUpdate();

	}catch (Exception ex) {
		logger.info("Se ha producido un error actualizando el campo idestado de la poliza: " + ex.getMessage());
		throw new DAOException("Se ha producido un error actualizando el campo idestado de la poliza", ex);
	}

}

/**
 * Obtener el estado de la póliza
 * @author U029114 14/09/2017
 * @param idPoliza
 * @return BigDecimal
 * @throws DAOException
 */
public BigDecimal obtenerEstadoPoliza(Long idpoliza) throws DAOException {

	final Session session = obtenerSession();

	try {
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append( "select idestado from tb_polizas pol where 1=1" );
		stringQuery.append(" and pol.idpoliza = " + idpoliza);

		BigDecimal estadoPoliza = (BigDecimal) session.createSQLQuery(stringQuery.toString()).uniqueResult();
		return estadoPoliza;

	}catch (Exception ex) {
		logger.info("Se ha producido un error al obtener el campo idestado de la poliza: " + ex.getMessage());
		throw new DAOException("Se ha producido un error al obtener el campo idestado de la poliza", ex);
	}

}
	@Override
	public BigDecimal obtenerPlanPoliza(String referencia, Character tipoRef) throws DAOException {
		BigDecimal planPoliza = (BigDecimal)this.obtenerSession()
				.createSQLQuery(QUERY_PLAN_POLIZA)
				.setString(REFERENCIA, referencia)
				.setCharacter("tiporef", tipoRef)
				.uniqueResult();
		return planPoliza;
	}
	
	@Override
	public BigDecimal obtenerLineaPoliza(String referencia, Character tipoRef, BigDecimal codplan) throws DAOException {
		BigDecimal lineaPoliza = (BigDecimal)this.obtenerSession()
				.createSQLQuery(QUERY_LINEA_POLIZA)
				.setString(REFERENCIA, referencia)
				.setCharacter("tiporef", tipoRef)
				.setBigDecimal("codplan", codplan)
				.uniqueResult();
		return lineaPoliza;
	}

	@Override
	public String obtenerNifCifDesdeReferenciaPoliza(String referencia, Character tipoRef, BigDecimal codplan) throws DAOException {
		String nifCif = (String)this.obtenerSession()
				.createSQLQuery(QUERY_NIFCIF_POLIZA)
				.setString(REFERENCIA, referencia)
				.setCharacter("tiporef", tipoRef)
				.setBigDecimal("codplan", codplan)
				.uniqueResult();
		return nifCif;
	}
	
	@Override
	public String getNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) throws DAOException{
		return (String)this.obtenerSession()
				.createCriteria(Oficina.class)
				.add(Restrictions.eq(ID_COD_ENTIDAD, codEntidad))
				.add(Restrictions.eq(ID_COD_OFICINA, codOficina))
				.setProjection(Projections.property(NOM_OFICINA)).uniqueResult();
	}
	
	public void actualizarPolizaPagada(Long idpoliza, Date fechaEnvioAgro) throws DAOException {

		logger.debug("INIT: actualizarPolizaPagada");
		
		final Session session = obtenerSession();

		try {
			StringBuilder stringQuery = new StringBuilder();
			
			stringQuery.append("update TB_POLIZAS set pagada="+Constants.POLIZA_PAGADA +" ,fecha_pago = SYSDATE where idpoliza="+ idpoliza);
			logger.debug("SENTENCIA UPDATE: "+ stringQuery.toString());
			session.createSQLQuery(stringQuery.toString()).executeUpdate();
			
			logger.debug("FIN: actualizarPolizaPagada");
		}catch (Exception ex) {
			logger.info("Se ha producido un error actualizando el campo pagada y fecha_pago de la poliza: " + ex.getMessage());
			throw new DAOException("Se ha producido un error actualizando el campo pagada y fecha_pago de la poliza", ex);
		}
	}
	
	@Override 
	public void actualizaCsvCargadoPoliza(Long idpoliza, Character pacCargadaSi) { 
		// EMPTY METHOD
	} 
 
	@Override 
	public Character isCsvCargado(Long idPoliza) { 
		return null; 
	} 

	@Override
	public List<RiesgoCubiertoModuloGanado> getRiesgoCubiertosModuloGanado(Long lineaSeguroId, String codModulo) {
		Map<String, Object> crit = new HashMap<String, Object>();
		crit.put(ID_LINEA_SEGURO_ID, lineaSeguroId);
		crit.put(ID_COD_MODULO, codModulo);
		return this.obtenerSession().createCriteria(RiesgoCubiertoModuloGanado.class).add(Restrictions.allEq(crit)).list();
	}

	@Override
	public List<RiesgoCubiertoModulo> getRiesgoCubiertosModulo(Long lineaSeguroId, String codModulo) {
		Map<String, Object> crit = new HashMap<String, Object>();
		crit.put(ID_LINEA_SEGURO_ID, lineaSeguroId);
		crit.put(ID_COD_MODULO, codModulo);
		return this.obtenerSession().createCriteria(RiesgoCubiertoModulo.class).add(Restrictions.allEq(crit)).list();
	}	
	
	public Poliza getPolizaByRefPlanTipoRef(String refPoliza, BigDecimal plan, Character tipoRefPoliza) throws DAOException {
		logger.debug("init - [PolizaDao] getPolizaByRefPlanTipoRef");
		Session session = obtenerSession();
		Poliza poliza = null;
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(ESTADO_POLIZA, ESTADO_POLIZA);
			criteria.add(Restrictions.eq(REFERENCIA, refPoliza));
			criteria.add(Restrictions.eq(TIPO_REFERENCIA, tipoRefPoliza));
			criteria.add(Restrictions.eq(LINEA_CODPLAN, plan));
			criteria.add(Restrictions.ne("estadoPoliza.idestado", Constants.ESTADO_POLIZA_BAJA));
			
			poliza= (Poliza)criteria.uniqueResult();
			
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getPolizaByRefPlanTipoRef");
		return poliza;		
	}

	@Override
	public void saveCobertura(Long idparcela, Long lineaseguroid, BigDecimal elegible, Integer conceptoppalmod, Integer codconcepto,
			Integer codriesgo, String codmodulo) throws DAOException {
		logger.debug("PolizaDao - saveCobertura");
		try {
			String sql =  String.format("insert into o02agpe0.tb_parcelas_coberturas (id,idparcela,lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,codconcepto,codvalor) VALUES (o02agpe0.sq_parcela_cobertura.nextVal" +
		",%s, %s, '%s',%s, %s, %s,%s)",
			String.valueOf(idparcela),
			String.valueOf(lineaseguroid),
			String.valueOf(codmodulo),
			String.valueOf(conceptoppalmod),
			String.valueOf(codriesgo),
			String.valueOf(codconcepto),
			String.valueOf(elegible)
			);
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			session.createSQLQuery(sql).executeUpdate();

		}
		 catch (Exception e) {
				logger.error("PolizaDao.saveCobertura. - ", e);
				throw new DAOException(
						"Se ha producido un error durante el acceso a la base de datos",
						e);
		}
		logger.debug("PolizaDao - saveCobertura FIN");		
	}
	
	public List<Explotacion> getExplotacionesPoliza(final Long idPoliza) throws DAOException {
		logger.debug("init - [PolizaDao] getExplotacionesPoliza");
		Session session = obtenerSession();
		List<Explotacion> explotaciones = null;
		try {
			Criteria criteria = session.createCriteria(Explotacion.class);	
			criteria.createAlias("poliza", "poliza");
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));			
			explotaciones = (List<Explotacion>) criteria.list();			
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getExplotacionesPoliza");
		return explotaciones;
	}

	@Override
	public List<BonificacionRecargo2015> getDcBonifRecargos(final Long idDc2015) throws DAOException {
		logger.debug("init - [PolizaDao] getDcBonifRecargos");
		Session session = obtenerSession();
		List<BonificacionRecargo2015> bonifRecargos = null;
		try {
			Criteria criteria = session.createCriteria(BonificacionRecargo2015.class);	
			criteria.createAlias("distribucionCoste2015", "distribucionCoste2015");
			criteria.add(Restrictions.eq("distribucionCoste2015.id", idDc2015));			
			bonifRecargos = (List<BonificacionRecargo2015>) criteria.list();			
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getDcBonifRecargos");
		return bonifRecargos;
	}
	
	@Override
	public List<DistCosteSubvencion2015> getDcSubvs(final Long idDc2015) throws DAOException {
		logger.debug("init - [PolizaDao] getBonifRecargos");
		Session session = obtenerSession();
		List<DistCosteSubvencion2015> subvs = null;
		try {
			Criteria criteria = session.createCriteria(DistCosteSubvencion2015.class);	
			criteria.createAlias("distribucionCoste2015", "distribucionCoste2015");
			criteria.add(Restrictions.eq("distribucionCoste2015.id", idDc2015));			
			subvs = (List<DistCosteSubvencion2015>) criteria.list();			
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getBonifRecargos");
		return subvs;
	}
	
	public List<SubAseguradoENESAGanado> getSubAseguradoENESAGanados(final Long idPoliza) throws DAOException {
		logger.debug("init - [PolizaDao] getSubAseguradoENESAGanados");
		Session session = obtenerSession();
		List<SubAseguradoENESAGanado> subAseguradoENESAGanados = null;
		try {
			Criteria criteria = session.createCriteria(SubAseguradoENESAGanado.class);	
			criteria.createAlias("poliza", "poliza");
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));			
			subAseguradoENESAGanados = (List<SubAseguradoENESAGanado>) criteria.list();			
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getSubAseguradoENESAGanados");
		return subAseguradoENESAGanados;
	}
	
	public List<CapitalAsegurado> getListCapitalesAsegurados(Long idPoliza) throws DAOException{
    	try{
    		Session session = obtenerSession(); 
    		Criteria criteria = session.createCriteria(CapitalAsegurado.class);
    		criteria.createAlias("parcela", "par");
    		criteria.createAlias("par.poliza", "pol");
    		criteria.add(Restrictions.eq("pol.idpoliza", idPoliza));
    		List<CapitalAsegurado> lstCapAseg = criteria.list();
    		return lstCapAseg;
    		
	    } catch (Exception e) {
	    	throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
    }
    
	public List<SubAseguradoCCAAGanado> getSubAseguradoCCAAGanados(final Long idPoliza) throws DAOException {
		logger.debug("init - [PolizaDao] getSubAseguradoCCAAGanados");
		Session session = obtenerSession();
		List<SubAseguradoCCAAGanado> subAseguradoCCAAGanados = null;
		try {
			Criteria criteria = session.createCriteria(SubAseguradoCCAAGanado.class);	
			criteria.createAlias("poliza", "poliza");
			criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));			
			subAseguradoCCAAGanados = (List<SubAseguradoCCAAGanado>) criteria.list();			
		} catch (Exception e) {
			logger.error(e);
			throw new DAOException(
					ERROR_ACCESO_BD,e);
		}
		logger.debug("end - [PolizaDao] getSubAseguradoCCAAGanados");
		return subAseguradoCCAAGanados;
	}
}