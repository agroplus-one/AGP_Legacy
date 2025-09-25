package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.anexo.IDeclaracionModificacionPolizaDao;
import com.rsi.agp.dao.models.ged.IDocumentacionGedDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Comarca;
import com.rsi.agp.dao.tables.commons.ComarcaId;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;
import com.rsi.agp.dao.tables.sbp.HistoricoEstado;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;
import com.rsi.agp.dao.tables.sbp.TasasSbp;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SimulacionSbpDao extends BaseDaoHibernate implements ISimulacionSbpDao {
	
	
	private static final String REFERENCIA = "referencia";
	/*** SONAR Q ** MODIF TAM(01.12.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/
	
	public static final BigDecimal PROVINCIA_GENERICA = new BigDecimal(99);
	public static final BigDecimal CULTIVO_GENERICO = new BigDecimal(999);
	
	
	/** CONSTANTES SONAR Q ** MODIF TAM (01.12.2021) ** Inicio **/
	private final static String LINEA = "linea";
	private final static String LIN_ID = "lin.lineaseguroid";
	private final static String ERROR = "Se ha producido un error durante el acceso a la base de datos";
	private final static String FEC_INI = "fechainicio";
	private final static String FEC_FIN = "fechafin";
	private final static String POLIZA = "poliza";
	private final static String POL_PPAL = "polizaPpal";
	private final static String POL_COMP = "polizaCpl";
	private final static String EST_POLSBP = "estadoPlzSbp";
	private final static String TIPOENV = "tipoEnvio";
	private final static String ESTADOSBP = "estadoSbp.idestado";
	private final static String IDPOL_PPAL = "polizaPpal.idpoliza";
	private final static String IDPOL_COMP = "polizaCpl.idpoliza";
	private final static String TIPOENV_ID = "tipoEnvio.id";
	private final static String PROV = "provincia";
	private final static String CULTIVO = "cultivo";
	private final static String LIN_LINEAID = "linea.lineaseguroid";
	private final static String PROV_CODPROV = "provincia.codprovincia";
	private final static String CULTIVO_ID = "cultivo.id.codcultivo";
	private final static String COMARCA = "comarca";
	private final static String COM_CODPROV = "comarca.id.codprovincia";
	private final static String COM_CODCOM = "comarca.id.codcomarca";
	private final static String TIPO_CAP = "tipoCapital";
	private final static String TIPOCAP_CODTIPOCAP = "tipoCapital.codtipocapital";
	private final static String UPDATE_SBPPOL = "update o02agpe0.tb_sbp_polizas p set gen_spl_cpl='";
	/** CONSTANTES SONAR Q ** MODIF TAM (01.12.2021) ** Fin **/

	private IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao;
	private IDocumentacionGedDao documentacionGedDao;

	
	public boolean isCargadaPrimaMinima(PolizaSbp polizaSbp)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(PrimaMinimaSbp.class);
			criteria.createAlias(LINEA, "lin");

			criteria.add(Restrictions.eq(LIN_ID, polizaSbp
					.getPolizaPpal().getLinea().getLineaseguroid()));
			if (criteria.list().size() > 0)
				return true;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		return false;
	}

	public boolean isCargadasTasas(PolizaSbp polizaSbp) throws DAOException {

		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(TasasSbp.class);
			criteria.createAlias(LINEA, "lin");

			criteria.add(Restrictions.eq(LIN_ID, polizaSbp
					.getPolizaPpal().getLinea().getLineaseguroid()));
			if (criteria.list().size() > 0)
				return true;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		return false;
	}

	public boolean isCargadosImpuestos(PolizaSbp polizaSbp) throws DAOException {

		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(MtoImpuestoSbp.class);

			criteria.add(Restrictions.eq("codplan", polizaSbp.getPolizaPpal().getLinea().getCodplan()));
			if (criteria.list().size() > 0)
				return true;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR, ex);
		}
		return false;
	}
	
	public boolean isCargadoPeriodoContratacion(PolizaSbp polizaSbp)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session
					.createCriteria(FechaContratacionSbp.class);
			criteria.createAlias(LINEA, "lin");

			criteria.add(Restrictions.eq(LIN_ID, polizaSbp
					.getPolizaPpal().getLinea().getLineaseguroid()));
			if (criteria.list().size() > 0)
				return true;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		return false;
	}

	
	public boolean isCargadoSbpCultivo(PolizaSbp polizaSbp) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias(LINEA, "lin");

			criteria.add(Restrictions.eq(LIN_ID, polizaSbp
					.getPolizaPpal().getLinea().getLineaseguroid()));
			if (criteria.list().size() > 0)
				return true;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		return false;
	}

	
	public boolean isLineaEnPeriodoContratacion(PolizaSbp polizaSbp)
			throws DAOException {
		Session session = obtenerSession();
		Date fechahoy = new Date();

		try {
			Criteria criteria = session
					.createCriteria(FechaContratacionSbp.class);
			criteria.createAlias(LINEA, "lin");

			criteria.add(Restrictions.eq(LIN_ID, polizaSbp
					.getPolizaPpal().getLinea().getLineaseguroid()));
			criteria.add(Restrictions.le(FEC_INI, fechahoy));
			criteria.add(Restrictions.ge(FEC_FIN, fechahoy));
			if (criteria.list().size() > 0)
				return true;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		return false;
	}

	
	@SuppressWarnings("unused")
	public boolean isCultivosEnPeriodoContratacion(PolizaSbp polizaSbp)
			throws DAOException {
		boolean enFecha = false;
		List<BigDecimal> lstCultivos = new ArrayList<BigDecimal>();
		
		for (ParcelaSbp parSbp : polizaSbp.getParcelaSbps()) {
			if (!lstCultivos.contains(parSbp.getCultivo().getId().getCodcultivo()))
				lstCultivos.add(parSbp.getCultivo().getId().getCodcultivo());
		}	
		if (lstCultivos != null) {
			for (int i = 0; i < lstCultivos.size(); i++) {			
				try{					
					enFecha = isCultivoSbpContratable(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(),new BigDecimal(Long.parseLong(lstCultivos.get(i).toString())));					
					if (!enFecha){
						return enFecha;
					}
				} catch (Exception ex) {
					logger.error("[SimulacionSbpDao] isCultivoEnPeriodoContratacion - Se ha producido un error en la BBDD: " + ex.getMessage());
				}
			}
			return enFecha;	
		}
		return false;
	}
	
	public boolean isPolizaConSiniestro(PolizaSbp polizaSbp)
			throws DAOException {

		Short pedrisco = 1;
		Short incendio = 2;
		Session session = obtenerSession();
		
		logger.debug("**@@** SimulacionSbpDao - isPolizaConSiniestro [INIT]");
		try {

			Criteria criteria = session.createCriteria(Siniestro.class);
			criteria.createAlias(POLIZA, POLIZA);
			criteria.add(Restrictions.eq("poliza.idpoliza", polizaSbp
					.getPolizaPpal().getIdpoliza()));
			/* Pet. 63473 ** MODIF TAM(01.12.2021) ** Inicio */
			/* Tendremos en cuenta aquellos Siniestros que no tengan 
			 * fecha de Baja y por tanto no estén dados de baja lógica */
			criteria.add(Restrictions.isNull("fechaBaja"));
			/* Pet. 63473 ** MODIF TAM(01.12.2021) ** Inicio */
			Criterion codriesgo = Restrictions.eq("codriesgo", pedrisco);
			Criterion codriesgo2 = Restrictions.eq("codriesgo", incendio);
		
			LogicalExpression expression = Restrictions.or(codriesgo,
					codriesgo2);
			criteria.add(expression);
			if (criteria.list().size() > 0) {
				logger.debug("**@@** SimulacionSbpDao - isPolizaConSiniestr. Retornamos true");
				return true;
			}

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		
		logger.debug("**@@** SimulacionSbpDao - isPolizaConSiniestr. Retornamos false");
		return false;
	}

	/**
	 * Primero comprueba si existe el sobreprecio en estado simulacion, si es
	 * asi lo borra. Comprueba si la poliza tiene sobreprecio
	 * @param polizaSbp
	 * @throws DAOException
	 * @return true si existe - false si no existe
	 */
	public PolizaSbp existePolizaSbp(PolizaSbp polizaSbp) throws DAOException{

		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(PolizaSbp.class);
			criteria.createAlias(POL_PPAL, POL_PPAL);
			criteria.createAlias(EST_POLSBP, "estadoSbp");
			criteria.createAlias(TIPOENV, TIPOENV);
			criteria.add(Restrictions.eq(ESTADOSBP,
					ConstantsSbp.ESTADO_SIMULACION));
			criteria.add(Restrictions.eq(IDPOL_PPAL, polizaSbp
					.getPolizaPpal().getIdpoliza()));

			if (criteria.list().size() > 0) { // Si existe en estado simulacion,
												// lo borramos

				List<PolizaSbp> listaborrar = criteria.list();

				for (int i = 0; i < listaborrar.size(); i++) {
					PolizaSbp p = listaborrar.get(i);
					this.deleteParcelas(p);
					this.delete(p);
				}
			}
			Criteria criteria2 = session.createCriteria(PolizaSbp.class);
			criteria2.createAlias(POL_PPAL, POL_PPAL);
			criteria2.createAlias(EST_POLSBP, "estadoSbp");
			criteria2.createAlias(TIPOENV, TIPOENV);
			criteria2.add(Restrictions.not(Restrictions.eq(
					ESTADOSBP, ConstantsSbp.ESTADO_ANULADA)));
			criteria2.add(Restrictions.not(Restrictions.eq(
					ESTADOSBP, ConstantsSbp.ESTADO_SIMULACION)));
			criteria2.add(Restrictions.eq(IDPOL_PPAL, polizaSbp
					.getPolizaPpal().getIdpoliza()));
			criteria2.add(Restrictions.eq(TIPOENV_ID, new BigDecimal(1)));
			if (criteria2.list().size() > 0)
				return (PolizaSbp) criteria2.uniqueResult();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		return null;
	}

	/**
	 * A partir de una lineaseguroId me devuelve su prima minima
	 * @param lineaseguroid
	 * @throws DAOException
	 * @return PrimaMinimaSbp
	 */
	public PrimaMinimaSbp getPrimaMinima(Long lineaseguroid)
			throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(PrimaMinimaSbp.class);
			criteria.createAlias(LINEA, "lin");

			criteria.add(Restrictions.eq(LIN_ID, lineaseguroid));
			return (PrimaMinimaSbp) criteria.uniqueResult();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
	}
	
	public List<MtoImpuestoSbp> getImpuestos(BigDecimal codPlan) throws DAOException {
		Session session = obtenerSession();
		
		List<MtoImpuestoSbp> listMtoImpuestos = new ArrayList<MtoImpuestoSbp>();
		try {
			Criteria criteria = session.createCriteria(MtoImpuestoSbp.class);
			logger.debug("SimulacionSbpDao - plan: "+codPlan);
			criteria.add(Restrictions.eq("codplan", codPlan));
			listMtoImpuestos = criteria.list();
			logger.debug("SimulacionSbpDao - total impuestos encontrados: "+listMtoImpuestos.size());
			return listMtoImpuestos;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
	}

	public List<ParcelaSbp> getParcelasParaSbp(PolizaSbp polizaSbp,
			String tipoPoliza, boolean incluirCplEnSbp, boolean filtroxComarca,
			List cultivos) throws DAOException {
		// AMG SBP fuerzo poliza a principal para q no falle pues viene de tipo Copy
		
		Session session = obtenerSession();
		List<Object> resultado = new ArrayList<Object>();
		String produccion = "";
		String tablas = "";
		String where = "";
		Long idpoliza;
		String filtroCom = "";
		String sql = "";
		String filtroCultivos = "";
		StringBuilder sb = new StringBuilder();
		String delim = "";
		String separator = ",";
		try {
			Long lineaseguroId = polizaSbp.getPolizaPpal().getLinea()
					.getLineaseguroid();
			if (filtroxComarca) {
				filtroCom = " ,pa.codcomarca ";
			}
			/* SONAR Q */
            filtroCultivos = obtenerFiltroCultivos(cultivos, sb, delim, separator);
			/* FIN SONAR Q */
			
			if (tipoPoliza.equals(ConstantsSbp.TIPO_PPAL)) {
				idpoliza = polizaSbp.getPolizaPpal().getIdpoliza();
				produccion = " sum(f.produccion) as produccion ";
				tablas = " tb_cap_aseg_rel_modulo  f,tb_capitales_asegurados c,tb_parcelas pa ";
				where = " f.idcapitalasegurado = c.idcapitalasegurado and c.idparcela = pa.idparcela"
						+ " and pa.idpoliza =" + idpoliza;

			} else if (tipoPoliza.equals(ConstantsSbp.TIPO_CPL)) {
				idpoliza = polizaSbp.getPolizaCpl().getIdpoliza();
				produccion = " sum(c.produccion + nvl(c.incrementoproduccion, 0)) as produccion ";
				tablas = " tb_capitales_asegurados c,tb_parcelas pa ";
				where = " c.idparcela = pa.idparcela and pa.idpoliza = "
						+ idpoliza;

			} else if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)) {
				idpoliza = polizaSbp.getPolizaCpl().getIdpoliza();
				produccion = " sum(0+nvl(c.incrementoproduccion, 0)) as produccion ";
				tablas = " tb_capitales_asegurados c,tb_parcelas pa ";
				where = " c.idparcela = pa.idparcela and pa.idpoliza = "
						+ idpoliza;
			}

			sql = " select pa.codcultivo as codcultivo,pa.codprovincia,"
					+ produccion + filtroCom + " from " + tablas + " where "
					+ where + filtroCultivos
					+ " group by pa.codcultivo,pa.codprovincia " + filtroCom
					+ " order by pa.codcultivo asc";

			logger.info("Consulta obtener parcelas: ********* "
					+ sql.toString());
			resultado = session.createSQLQuery(sql.toString()).list();

			return this.guardaParcelasList(resultado, lineaseguroId,
					filtroxComarca);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}

	}

	public Map<String, Object> getSobreprecios(PolizaSbp polizaSbp,
			String tipoPoliza, boolean incluirCplEnSbp, boolean filtroxComarca,List<ParcelaSbp> lstParSbp)
			throws DAOException {
		
		logger.debug("SimulacionSbpDao - getSobreprecios - init");
		
		List<Sobreprecio> sbpList = new ArrayList<Sobreprecio>();
		List<Sobreprecio> sbpListFinal = new ArrayList<Sobreprecio>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Session session = obtenerSession();
		BigDecimal[] provinciasPar = null;
		BigDecimal[] cultivosPar = null;
		List<ParcelaSbp> listaParcelas = new ArrayList<ParcelaSbp>();	
		try {
			Long lineaseguroId = polizaSbp.getPolizaPpal().getLinea()
					.getLineaseguroid();
			
			
			/* SONAR Q */
			listaParcelas = obtenerListaParc(tipoPoliza, lstParSbp, listaParcelas, polizaSbp, incluirCplEnSbp, filtroxComarca);
			/* FIN SONAR Q */

			parameters.put("listaParcelas", listaParcelas);

			provinciasPar = new BigDecimal[(listaParcelas).size() + 1];
			cultivosPar = new BigDecimal[(listaParcelas).size() + 1];
			int j = 0;
			for (int i = 0; i < new ArrayList<ParcelaSbp>(listaParcelas).size(); i++) {

				ParcelaSbp par = new ArrayList<ParcelaSbp>(listaParcelas)
						.get(i);
				provinciasPar[i] = par.getComarca().getProvincia()
						.getCodprovincia();
				cultivosPar[i] = par.getCultivo().getId().getCodcultivo();
				j = i;
			}
			provinciasPar[j + 1] = PROVINCIA_GENERICA;
			cultivosPar[j + 1] = CULTIVO_GENERICO;
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(PROV, PROV);
			criteria.createAlias(CULTIVO, CULTIVO);
			criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
			criteria.add(Restrictions.in(PROV_CODPROV,
					provinciasPar));
			criteria.add(Restrictions.in(CULTIVO_ID, cultivosPar));
			criteria.addOrder(Order.asc(CULTIVO_ID));
			criteria.addOrder(Order.asc(PROV_CODPROV));
			sbpList = (List<Sobreprecio>) criteria.list();
			String cultTemp = "";
			boolean revisar = false;
			// para un mismo cultivo nos quedamos con la provincia especifica primero y si no viene pues con la generica ->99
		
			logger.debug("ANTES DE LLAMAR AL obtenerSbpListFinal");

			
			/* SONAR Q */
			sbpListFinal = obtenerSbpListFinal(sbpListFinal, sbpList, revisar, cultTemp); 
			/* FIN SONAR Q */
			
			logger.debug("Lista SobrePrecios");
			for (Sobreprecio sbp : sbpListFinal) {
				logger.debug("cultivo: " + sbp.getCultivo().getId().getCodcultivo().toString() + " provincia: " + sbp.getProvincia().getCodprovincia().toString());
			}
			parameters.put("listaSobreprecios", sbpListFinal);
			
			logger.debug("SimulacionSbpDao - getSobreprecios - end");

			return parameters;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
	}

	/**
	 * Metodo que recorre la lista de objetos devueltos por la consulta y genera
	 * una lista de parcelasSBP solo con los datos necesarios para mostralas en
	 * la JSP
	 * @param resultado
	 * @param lineaseguroId
	 * @param filtroxComarca
	 * @return List<ParcelaSbp>
	 */
	private List<ParcelaSbp> guardaParcelasList(List<Object> resultado,
			Long lineaseguroId, boolean filtroxComarca) {
		ParcelaSbp parcelaSbp = null;
		Cultivo cultivo = null;
		Comarca comarca = null;
		List<ParcelaSbp> parcelaSbps = new ArrayList<ParcelaSbp>();

		for (int i = 0; i < resultado.size(); i++) {
			Object[] t = (Object[]) resultado.get(i);
			parcelaSbp = new ParcelaSbp();
			// 0 - cultivo
			CultivoId cultivoId = new CultivoId(lineaseguroId,
					(BigDecimal) t[0]);
			cultivo = (Cultivo) this.getObject(Cultivo.class, cultivoId);
			parcelaSbp.setCultivo(cultivo);
			// 2 - produccion
			parcelaSbp.setTotalProduccion((BigDecimal) t[2]);
			if (filtroxComarca) {
				// 3 comarca - 1 provincia

				ComarcaId comarcaId = new ComarcaId((BigDecimal) t[3],
						(BigDecimal) t[1]);
				comarca = (Comarca) this.getObject(Comarca.class, comarcaId);
				parcelaSbp.setComarca(comarca);

				// 1 provincia
				Provincia provincia = new Provincia();
				provincia = (Provincia) this.getObject(Provincia.class,
						(BigDecimal) t[1]);
				parcelaSbp.getComarca().setProvincia(provincia);
			} else {
				// 1 provincia
				Provincia provincia = new Provincia();
				provincia = (Provincia) this.getObject(Provincia.class,
						(BigDecimal) t[1]);
				parcelaSbp.setComarca(new Comarca());
				parcelaSbp.getComarca().setProvincia(provincia);
			}
			logger.debug(" idCultivo: "
					+ parcelaSbp.getCultivo().getId().getCodcultivo()
					+ " desCultivo: " + parcelaSbp.getCultivo().getDescultivo()
					+ " TotalProd: " + parcelaSbp.getTotalProduccion()
					+ " SBP: " + parcelaSbp.getSobreprecio() + " CodProv: "
					+ parcelaSbp.getComarca().getProvincia().getCodprovincia()
					+ " DescProv: "
					+ parcelaSbp.getComarca().getProvincia().getNomprovincia()
					+ " cocomarca: "
					+ parcelaSbp.getComarca().getId().getCodcomarca());

			parcelaSbps.add(parcelaSbp);
		}
		return parcelaSbps;

	}

	
	public List<ParcelaSbp> rellenaPrimas(List<ParcelaSbp> listParXcomarca,
			HttpServletRequest request, Long lineaseguroId) throws DAOException {
		
		logger.debug("SimulacionSbpDao - rellenarPrimas - init");
		
		logger.debug("Han llegado " + listParXcomarca.size() + " parcelas");
		
		for (ParcelaSbp parcelaSbp : listParXcomarca) {
			logger.debug("PRECIO PARCELA ANTES DE RELLENAR PRIMAS --> " + parcelaSbp.getSobreprecio());
		}
		
		BigDecimal sbp = null;
		BigDecimal primaNetaIncendio = null;
		BigDecimal primaNetaPedrisco = null;
		BigDecimal cero = new BigDecimal("0.00");
		List<ParcelaSbp> listParcelas = new ArrayList<ParcelaSbp>();
		try {
			for (int i = 0; i < listParXcomarca.size(); i++) {
				ParcelaSbp par = listParXcomarca.get(i);
				
				
				StringBuilder infoParcela = new StringBuilder(100);
				
				infoParcela.append("ID PARCELA --> " + par.getId());
				infoParcela.append(" - PRIMA NETA INCENDIO --> " + par.getPrimaNetaIncendio());
				infoParcela.append(" - PRIMA NETA PEDRISCO --> " + par.getPrimaNetaPedrisco());

				logger.debug("@ENRIQUE: INFO PARCELA ANTES DE RELLENAR PRIMAS:" + infoParcela.toString());
				
				String sbpEspecifico = "sbp_"
						+ par.getComarca().getProvincia().getCodprovincia()
						+ "_" + par.getCultivo().getId().getCodcultivo();
				String sbpProvGeneral = "sbp_99_"
						+ par.getCultivo().getId().getCodcultivo();
				String sbpCultGeneral = "sbp_"
						+ par.getComarca().getProvincia().getCodprovincia()
						+ "_999";
				String sbpTodoGeneral = "sbp_99_999";
				if (request.getParameter(sbpEspecifico) != null) {
					sbp = new BigDecimal(request.getParameter(sbpEspecifico));

				} else if (request.getParameter(sbpProvGeneral) != null) {
					sbp = new BigDecimal(request.getParameter(sbpProvGeneral));

				} else if (request.getParameter(sbpCultGeneral) != null) {
					sbp = new BigDecimal(request.getParameter(sbpProvGeneral));
				}else if (request.getParameter(sbpTodoGeneral) != null) {
					sbp = new BigDecimal(request.getParameter(sbpTodoGeneral));
				}
				if (sbp != null) {

					TasasSbp tasa = this.getTasa(lineaseguroId, par);
					par.setTasaIncendio(tasa.getTasaIncendio());
					par.setTasaPedrisco(tasa.getTasaPedrisco());
					par.setSobreprecio(sbp);
					// DAA 21/05/2013
					primaNetaIncendio = ((par.getTotalProduccion().multiply(par
							.getSobreprecio())).multiply(par.getTasaIncendio()))
							.divide(new BigDecimal(1000)).setScale(2,
									BigDecimal.ROUND_HALF_UP);
					primaNetaPedrisco = ((par.getTotalProduccion().multiply(par
							.getSobreprecio())).multiply(par.getTasaPedrisco()))
							.divide(new BigDecimal(1000)).setScale(2,
									BigDecimal.ROUND_HALF_UP);
					if (primaNetaIncendio.compareTo(cero) == 0) {
						primaNetaIncendio = new BigDecimal("0.01");
					}
					if (primaNetaPedrisco.compareTo(cero) == 0) {
						primaNetaPedrisco = new BigDecimal("0.01");
					}

					par.setPrimaNetaIncendio(primaNetaIncendio);
					par.setPrimaNetaPedrisco(primaNetaPedrisco);
					
					infoParcela.setLength(0);
					
					infoParcela.append("ID PARCELA --> " + par.getId());
					infoParcela.append(" - PRIMA NETA INCENDIO --> " + par.getPrimaNetaIncendio());
					infoParcela.append(" - PRIMA NETA PEDRISCO --> " + par.getPrimaNetaPedrisco());

					logger.debug("@ENRIQUE: INFO PARCELA DESPUES DE RELLENAR PRIMAS:" + infoParcela.toString());
					logger.debug("SimulacionSbpDao - rellenarPrimas - init");

					
					listParcelas.add(par);
					sbp = null;
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		
		logger.debug("SimulacionSbpDao - rellenarPrimas - end");
		
		for (ParcelaSbp parcelaSbp : listParcelas) {
			logger.debug("PRECIO PARCELA DESPUES DE RELLENAR PRIMAS --> " + parcelaSbp.getSobreprecio());
		}
		
		return listParcelas;
	}

	public TasasSbp getTasa(Long lineaseguroId, ParcelaSbp par)
			throws DAOException {

		Session session = obtenerSession();
		TasasSbp tasa = null;
		try {
			// Buscamos la tasa para los datos concretos de la parcela
			logger.debug("Buscamos la tasa para los datos concretos");
			Criteria criteria = session.createCriteria(TasasSbp.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(COMARCA, COMARCA);
			criteria.createAlias(CULTIVO, CULTIVO);

			criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
			criteria.add(Restrictions.eq(COM_CODPROV, par
					.getComarca().getProvincia().getCodprovincia()));
			criteria.add(Restrictions.eq(COM_CODCOM, par
					.getComarca().getId().getCodcomarca()));
			criteria.add(Restrictions.eq(CULTIVO_ID, par
					.getCultivo().getId().getCodcultivo()));
			tasa = (TasasSbp) criteria.uniqueResult();

			if (tasa == null || tasa.getId() == null) {
				logger.debug("Buscamos la tasa para la comarca genÃ©rica");
				// Buscamos la tasa para la comarca
				criteria = session.createCriteria(TasasSbp.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(COMARCA, COMARCA);
				criteria.createAlias(CULTIVO, CULTIVO);

				criteria.add(Restrictions.eq(LIN_LINEAID,
						lineaseguroId));
				criteria.add(Restrictions.eq(COM_CODPROV, par
						.getComarca().getProvincia().getCodprovincia()));
				criteria.add(Restrictions.eq(COM_CODCOM,
						new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, par
						.getCultivo().getId().getCodcultivo()));
				tasa = (TasasSbp) criteria.uniqueResult();
			}

			if (tasa == null || tasa.getId() == null) {
				logger.debug("Buscamos la tasa para la comarca y la provincia genÃ©rica");
				// Buscamos la tasa para la comarca y la provincia
				criteria = session.createCriteria(TasasSbp.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(COMARCA, COMARCA);
				criteria.createAlias(CULTIVO, CULTIVO);

				criteria.add(Restrictions.eq(LIN_LINEAID,
						lineaseguroId));
				criteria.add(Restrictions.eq(COM_CODPROV,
						new BigDecimal("99")));
				criteria.add(Restrictions.eq(COM_CODCOM,
						new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, par
						.getCultivo().getId().getCodcultivo()));
				tasa = (TasasSbp) criteria.uniqueResult();
			}

			if (tasa == null || tasa.getId() == null) {
				// Buscamos la tasa para la comarca, la provincia y el cultivo
				logger.debug("Buscamos la tasa para la comarca, la provincia y el cultivo genÃ©ricos");
				criteria = session.createCriteria(TasasSbp.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(COMARCA, COMARCA);
				criteria.createAlias(CULTIVO, CULTIVO);

				criteria.add(Restrictions.eq(LIN_LINEAID,
						lineaseguroId));
				criteria.add(Restrictions.eq(COM_CODPROV,
						new BigDecimal("99")));
				criteria.add(Restrictions.eq(COM_CODCOM,
						new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID,
						new BigDecimal("999")));
				tasa = (TasasSbp) criteria.uniqueResult();
			}

			return tasa;

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
	}

	
	
	public List<ParcelaSbp> getParcelasSbpbyIdPolizaPpal(Long idpolizaPpal,
			boolean filtroXestado) throws DAOException {
		
		Session session = obtenerSession();
		List<ParcelaSbp> oldParcSbp = null;
		BigDecimal id = null;
		try {
			logger.debug("INIT getParcelasSbpbyIdPolizaPpal()");
			logger.debug("idpolizaPpal: "+idpolizaPpal);
		    String sql =" select p.id from o02agpe0.tb_SBP_HISTORICO_ESTADOS e, o02agpe0.tb_sbp_polizas p"+ 
		    			" where p.idpoliza ="+idpolizaPpal+ " and p.idestado=5  and p.id= e.idpoliza_sbp " +
		    			" and e.estado=5 order by fecha desc";
		    logger.debug("sql: "+sql);
		    
		    List<BigDecimal> idsSbp = session.createSQLQuery(sql).list();
		    
		    if (idsSbp!= null && !idsSbp.isEmpty()) {
		    	logger.debug("Ha entrado porque idsSbp es distinto de null");
		    	id =  idsSbp.get(0);
		    	logger.debug("el valor de id es: " + id);
		    }
		    Criteria criteria = session.createCriteria(ParcelaSbp.class);
			criteria.createAlias("polizaSbp", "polizaSbp");
			criteria.createAlias("polizaSbp.estadoPlzSbp",
					"polizaSbp.estadoPlzSbp");
		    
			logger.debug("***id: " + id);
			criteria.add(Restrictions.eq("polizaSbp.id", id !=null ? id.longValue() : null));
			
			if (filtroXestado)
				criteria.add(Restrictions.eq("polizaSbp.estadoPlzSbp.idestado",
						ConstantsSbp.ESTADO_ENVIADA_CORRECTA));
		
			oldParcSbp = (List) criteria.list();
			
			if (oldParcSbp != null && !oldParcSbp.isEmpty()){
				logger.debug("oldParcSbp size: "+oldParcSbp.size());
				for (ParcelaSbp par:oldParcSbp){
					logger.debug("idParSbp: "+par.getId()+ " idPolSbp: "+par.getPolizaSbp().getId()+ "cult.: "+par.getCultivo().getId().getCodcultivo()+" totProd: "+par.getTotalProduccion());
				}
				
			}else{
				logger.debug("oldParSbp es null");
			}
			return oldParcSbp;
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos:  ",
					ex);
		}
	}

	
	public void deleteParcelas(PolizaSbp polizaSbp2) throws DAOException {

		Session session = obtenerSession();

		try {
			Query sql = session
					.createSQLQuery("delete from o02agpe0.tb_sbp_parcelas s where s.idpoliza_sbp ="
							+ polizaSbp2.getId());
			sql.executeUpdate();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}

	}

	
	public void cambiaEstado(String referencia) throws DAOException {
		Session session = obtenerSession();

		try {
			Query sql = session
					.createSQLQuery("update tb_sbp_polizas set IDESTADO= "
							+ ConstantsSbp.ESTADO_ANULADA
							+ " where referencia = '" + referencia + "'");
			sql.executeUpdate();

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}

	}

	
	public List<ParcelaSbp> getParcelasSimulacion(PolizaSbp polizaSbp)
			throws DAOException {
		
		logger.debug("SimulacionSbpDao - getParcelasSimulacion - init");
		
		Session session = obtenerSession();
		List<ParcelaSbp> listparcelas = new ArrayList<ParcelaSbp>();
		List<Object> resultado = new ArrayList<Object>();
		ParcelaSbp parcelaSbp = null;
		Cultivo cultivo = null;
		try {
			String sql = " select codprovincia,codcultivo,sobreprecio,sum(total_produccion) ,sum(prima_neta_incendio),sum(prima_neta_pedrisco),codcomarca"
					+ " from tb_sbp_parcelas pa where pa.idpoliza_sbp="
					+ polizaSbp.getId()
					+ " group by codprovincia,codcultivo,sobreprecio,codcomarca order by codcultivo";

			resultado = session.createSQLQuery(sql.toString()).list();
			for (int i = 0; i < resultado.size(); i++) {
				Object[] t = (Object[]) resultado.get(i);
				parcelaSbp = new ParcelaSbp();
				// 1 - cultivo
				CultivoId cultivoId = new CultivoId(polizaSbp.getPolizaPpal()
						.getLinea().getLineaseguroid(), (BigDecimal) t[1]);
				cultivo = (Cultivo) this.getObject(Cultivo.class, cultivoId);
				parcelaSbp.setCultivo(cultivo);

				//Provincia 0 y comarca 6
				Provincia provincia = (Provincia) this.getObject(Provincia.class, (BigDecimal) t[0]);
				BigDecimal codProvincia = (BigDecimal) t[0];

				// Crear un nuevo ComarcaId con codProvincia y posiblemente codComarca si está disponible
				ComarcaId comarcaId = new ComarcaId();
				comarcaId.setCodprovincia(codProvincia);
				if (t[6] != null) {
				    comarcaId.setCodcomarca((BigDecimal) t[6]);
				}

				// Crear y configurar Comarca
				Comarca comarca = new Comarca();
				comarca.setId(comarcaId);
				comarca.setProvincia(provincia);
				comarca.getProvincia().setCodprovincia(codProvincia);

				// Asociar la Comarca al ParcelaSbp
				parcelaSbp.setComarca(comarca);

				// 2 - produccion
				parcelaSbp.setTotalProduccion((BigDecimal) t[3]);
				// 3 - sbp
				parcelaSbp.setSobreprecio((BigDecimal) t[2]);
				// 4 PrecioMinimo
				parcelaSbp.setPrimaNetaIncendio((BigDecimal) t[4]);
				// 5 PrecioMaximo
				parcelaSbp.setPrimaNetaPedrisco((BigDecimal) t[5]);
				
				listparcelas.add(parcelaSbp);
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
		
		logger.debug("Se devuelven " + listparcelas.size() + " parcelas.");
		
		logger.debug("SimulacionSbpDao - getParcelasSimulacion - end");

		return listparcelas;
	}

	public void actualizarSobreprecio(BigDecimal sobreprecio, BigDecimal codCultivo, BigDecimal codProvincia, BigDecimal codComarca) throws DAOException {
	    logger.debug("SimulacionSbpDao - actualizarSobreprecio - init");

	    Session session = null;
	    
	    try {
	    	session = obtenerSession();

	        String sql = "UPDATE tb_sbp_parcelas " +
	                     "SET sobreprecio = :nuevoSobreprecio " +
	                     "WHERE codcultivo = :codCultivo " +
	                     "AND codprovincia = :codProvincia " +
	                     "AND codcomarca = :codComarca";

	        Query query = session.createSQLQuery(sql);
	        query.setParameter("nuevoSobreprecio", sobreprecio);
	        query.setParameter("codCultivo", codCultivo);
	        query.setParameter("codProvincia", codProvincia);
	        query.setParameter("codComarca", codComarca);

	        int rowsAffected = query.executeUpdate();
	        logger.debug("Actualización completada. Filas afectadas: " + rowsAffected);

	    } catch (Exception ex) {
	        logger.error("Error actualizando sobreprecio", ex);
	        throw new DAOException(ERROR, ex);
	    } finally {
	    	logger.debug("SimulacionSbpDao - actualizarSobreprecio - end");
	    }
	}
	
	@SuppressWarnings("unused")
	public List<String> getCultivosSbp(Long lineaseguroId) throws DAOException {
		Session session = obtenerSession();
		List<Sobreprecio> cultivos = new ArrayList<Sobreprecio>();
		List<Sobreprecio> cultivosFin = new ArrayList<Sobreprecio>();
		List<BigDecimal> cultivosBig = new ArrayList<BigDecimal>();
		//List cultivosFiltrados = null;
		List<String> cultivosFiltrados = new ArrayList<String>();
		
		boolean enFecha = false;
		List<FechaContratacionSbp> lstFechaContratacionSbp = new ArrayList<FechaContratacionSbp>();
		try {		
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(CULTIVO, "culti");			
			criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
			
			cultivos = criteria.list();
			
			// filtramos los cultivos que estï¿½n fuera de periodo de contrataciï¿½n	
			if (cultivos != null) {
				// eliminiamos cultivos duplicados:
				for (Sobreprecio sbp:cultivos) {
					if (!cultivosBig.contains(sbp.getCultivo().getId().getCodcultivo())){
						cultivosBig.add(sbp.getCultivo().getId().getCodcultivo());
						cultivosFin.add(sbp);
					}
				}
				
				for (Sobreprecio sbp:cultivosFin) {
					enFecha = false;
					try{					
						enFecha = isCultivoSbpContratable(lineaseguroId,sbp.getCultivo().getId().getCodcultivo());					
						if (enFecha){
							cultivosFiltrados.add(sbp.getCultivo().getId().getCodcultivo().toString()+"_"+sbp.getTipoCapital().getCodtipocapital());
						}						
					} catch (Exception ex) {
						logger.error("[SimulacionSbpDao] getCultivosSbp - Se ha producido un error en la BBDD: " + ex.getMessage());
					}
				}
			}
			
			return cultivosFiltrados;
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
	}
	
	/*
	 * Chequea si el cultivo de sobreprecio estï¿½ en fecha de contrataciï¿½n
	 * 
	 */
	
	public boolean isCultivoSbpContratable(Long lineaseguroid,BigDecimal codcultivo){
		List<FechaContratacionSbp> lstFechaContratacionSbp = new ArrayList<FechaContratacionSbp>();
		
		logger.debug("SimulacionSbpDao - isCultivoSbpContratable - init");
		
		Session session = obtenerSession();
		Date fechahoy = new Date();
		boolean enFecha = false;	
		try{
			Criteria crit = session.createCriteria(FechaContratacionSbp.class);
			crit.createAlias(LINEA, LINEA);		
			crit.add(Restrictions.eq(LIN_LINEAID, lineaseguroid));
			crit.add(Restrictions.eq(CULTIVO_ID, codcultivo));	
			lstFechaContratacionSbp = crit.list();
			if (!lstFechaContratacionSbp.isEmpty()){ //existe cultivo, verificar fecha
				crit = session.createCriteria(FechaContratacionSbp.class);
				crit.createAlias(LINEA, LINEA);		
				crit.add(Restrictions.eq(LIN_LINEAID, lineaseguroid));
				crit.add(Restrictions.le(FEC_INI, fechahoy));
				crit.add(Restrictions.ge(FEC_FIN, fechahoy));
				crit.add(Restrictions.eq(CULTIVO_ID, codcultivo));	
				lstFechaContratacionSbp = crit.list();							
			}else{ // busco por el cultivo generico y verifico fecha
				crit = session.createCriteria(FechaContratacionSbp.class);
				crit.createAlias(LINEA, LINEA);
				crit.add(Restrictions.eq(LIN_LINEAID, lineaseguroid));
				crit.add(Restrictions.le(FEC_INI, fechahoy));
				crit.add(Restrictions.ge(FEC_FIN, fechahoy));
				crit.add(Restrictions.eq(CULTIVO_ID, new BigDecimal(Long.parseLong("999"))));
				lstFechaContratacionSbp = crit.list();		
			}
			if (!lstFechaContratacionSbp.isEmpty()){
				enFecha = true;
				logger.info("El cultivo = " + codcultivo.toString() + ", lineaseguroid: " + lineaseguroid + " es contratable");
			}else{
				logger.info("cultivo " + codcultivo.toString() + ", lineaseguroid: " + lineaseguroid + " fuera de fecha");
			}
		} catch (Exception ex) {
			logger.error("[SimulacionSbpDao] isCultivoSbpContratable - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		
		logger.debug("SimulacionSbpDao - isCultivoSbpContratable - end");

		return enFecha;
	}
	
	
	
	/**
	 * comprueba si ya existe un suplemento
	 * @param idPolizappal
	 * @return boolean existe
	 */
	public List<PolizaSbp> existeSuplemento(Long idpolizaPpal)throws DAOException{
		try{
			final Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(PolizaSbp.class);
			criteria.add(Restrictions.eq(TIPOENV_ID, ConstantsSbp.TIPO_ENVIO_SUPLEMENTO));
			criteria.add(Restrictions.eq(IDPOL_PPAL, idpolizaPpal));
			
			List<PolizaSbp> suplementos = criteria.list();
			 
			return suplementos;
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}
	}
	
	public List<ParcelaSbp> getParcelasCPLconSbp(Long idPolizaCPL,List cultivos,Long lineaseguroId) throws DAOException {

		Session session = obtenerSession();
		List<Object> resultado = new ArrayList<Object>();
		try {
			String sql = " select pa.codcultivo as codcultivo,pa.codprovincia,"+
					     " sum(c.produccion + nvl(c.incrementoproduccion, 0)) as produccion,pa.codcomarca "+ 
					     " from tb_capitales_asegurados c,tb_parcelas pa "+
					     " where c.idparcela = pa.idparcela and pa.idpoliza = " + idPolizaCPL+
					     " and pa.codcultivo in "+ StringUtils.toValoresSeparadosXComas(cultivos,false)+
					     " and c.altaencomplementario='S'"+
					     " group by pa.codcultivo,pa.codprovincia, pa.codcomarca "+ 
					     " order by pa.codcultivo asc";

			logger.info("Consulta obtener parcelas: ********* "
					+ sql.toString());
			resultado = session.createSQLQuery(sql.toString()).list();

			return this.guardaParcelasList(resultado, lineaseguroId,
					true);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}

	}
	/**
	 * Devuelve el plan mas reciente de sbp
	 * @author U029769 26/06/2013
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getPlanSbp() throws DAOException{
		
		logger.debug("init - [ConsultaSbpDao] getPlanSbp");
		BigDecimal codPlan = null;
		try{
			final Session session = obtenerSession();
			
			final String sql = "select max (l.codplan) " +
					     " from tb_sbp_fechas_contratacion f,tb_lineas l " +
					     " where f.lineaseguroid = l.lineaseguroid ";
			
			List<BigDecimal> cods = session.createSQLQuery(sql).list();
			if (cods.size()>0){
				codPlan = cods.get(0);
			}
		return codPlan;
		
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
	}
	
	@Override
	public BigDecimal esParcelaConSbp(BigDecimal cultivo, BigDecimal provincia, Long lineaseguroId,boolean comprobarfecha, List<BigDecimal> lstTiposCapital) throws DAOException {
		BigDecimal codTipoCapital = null;
		try{
			final Session session = obtenerSession();
			boolean enFecha = false;
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias(PROV, PROV);
			criteria.createAlias(CULTIVO, CULTIVO);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(TIPO_CAP, TIPO_CAP);
			
			
			criteria.add(Restrictions.eq(PROV_CODPROV, provincia));
			criteria.add(Restrictions.eq(CULTIVO_ID, cultivo));
			criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
			criteria.add(Restrictions.in(TIPOCAP_CODTIPOCAP, lstTiposCapital));
			Sobreprecio sobreprecio = (Sobreprecio) criteria.uniqueResult();

			if (sobreprecio == null){
				// Buscamos el sobreprecio con provincia generica
				criteria = session.createCriteria(Sobreprecio.class);
				criteria.createAlias(PROV, PROV);
				criteria.createAlias(CULTIVO, CULTIVO);
				criteria.createAlias(LINEA, LINEA);
				
				criteria.add(Restrictions.eq(PROV_CODPROV, new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, cultivo));
				criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
				criteria.add(Restrictions.in(TIPOCAP_CODTIPOCAP, lstTiposCapital));
				sobreprecio = (Sobreprecio) criteria.uniqueResult();
			}
			if (sobreprecio == null){
				// Buscamos el sobreprecio con provincia generica y cultivo generico
				criteria = session.createCriteria(Sobreprecio.class);
				criteria.createAlias(PROV, PROV);
				criteria.createAlias(CULTIVO, CULTIVO);
				criteria.createAlias(LINEA, LINEA);
				
				criteria.add(Restrictions.eq(PROV_CODPROV, new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, new BigDecimal("999")));
				criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
				criteria.add(Restrictions.in(TIPOCAP_CODTIPOCAP, lstTiposCapital));
				sobreprecio = (Sobreprecio) criteria.uniqueResult();
			}
			if (comprobarfecha){
				if (sobreprecio!=null){
					try{					
						enFecha = isCultivoSbpContratable(lineaseguroId,cultivo);
						if (enFecha)
							codTipoCapital = sobreprecio.getTipoCapital().getCodtipocapital();
					} catch (Exception ex){
						logger.error("[SimulacionSbpDao] esParcelaConSbp - Se ha producido un error en la BBDD: " + ex.getMessage());
					}
				}
			}else {
				if (sobreprecio!= null){
					enFecha=true;
					codTipoCapital = sobreprecio.getTipoCapital().getCodtipocapital();
				}
			}
			return codTipoCapital;
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(
					ERROR,
					ex);
		}		
	}

@Override
	public List<Object> getSobreprecioYtasasFromParcelas(BigDecimal cultivo,BigDecimal codProvincia, 
			String referencia,BigDecimal comarca,boolean filtroComarca)
			throws DAOException {
		logger.debug("init - [SimulacionSbpDao] getSobreprecioYtasasFromParcelas");
		
		List<Object> importes = null;
		try{
			final Session session = obtenerSession();
			
			String sql = "select sobreprecio,tasa_incendio,tasa_pedrisco from o02agpe0.tb_sbp_parcelas p where p.idpoliza_sbp in " + 
					"(select id from o02agpe0.tb_sbp_polizas po where po.referencia ='"+referencia+"') and p.codprovincia ="+codProvincia+"" +
							" and p.codcultivo= "+cultivo ;
			if (filtroComarca) {
				sql = sql + " and p.codcomarca= "+comarca ;
			}
			logger.debug("getSobreprecioYtasasFromParcelas - sql: "+sql);
			 importes = session.createSQLQuery(sql).list();
			
			
		
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return importes;
	}

	@Override
	public Sobreprecio getMaxSobrepreciofromTabla(BigDecimal cultivo,BigDecimal codProvincia, Long lineaseguroId, List<BigDecimal> lstTiposCapital)
			throws DAOException {
		logger.debug("init - [SimulacionSbpDao] getSobrepreciofromTabla");
		
		Sobreprecio sobreprecio = null;
		try{
			final Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(Sobreprecio.class);
			criteria.createAlias(PROV, PROV);
			criteria.createAlias(CULTIVO, CULTIVO);
			criteria.createAlias(LINEA, LINEA);
			criteria.createAlias(TIPO_CAP, TIPO_CAP);
			
			criteria.add(Restrictions.eq(PROV_CODPROV, codProvincia));
			criteria.add(Restrictions.eq(CULTIVO_ID, cultivo));
			criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
			criteria.add(Restrictions.in(TIPOCAP_CODTIPOCAP, lstTiposCapital));
			
			sobreprecio = (Sobreprecio) criteria.uniqueResult();
			
			if (sobreprecio == null){
				// Buscamos la el sobreprecio con provincia generica
				criteria = session.createCriteria(Sobreprecio.class);
				criteria.createAlias(PROV, PROV);
				criteria.createAlias(CULTIVO, CULTIVO);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(TIPO_CAP, TIPO_CAP);
				
				criteria.add(Restrictions.eq(PROV_CODPROV, new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, cultivo));
				criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
				criteria.add(Restrictions.in(TIPOCAP_CODTIPOCAP, lstTiposCapital));
				
				sobreprecio = (Sobreprecio) criteria.uniqueResult();
			    
			}
			if (sobreprecio == null){
				// Buscamos la el sobreprecio con provincia generica y cultivo generico
				criteria = session.createCriteria(Sobreprecio.class);
				criteria.createAlias(PROV, PROV);
				criteria.createAlias(CULTIVO, CULTIVO);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(TIPO_CAP, TIPO_CAP);
				
				criteria.add(Restrictions.eq(PROV_CODPROV, new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, new BigDecimal("999")));
				criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
				criteria.add(Restrictions.in(TIPOCAP_CODTIPOCAP, lstTiposCapital));
				
				sobreprecio = (Sobreprecio) criteria.uniqueResult();
				if (sobreprecio != null)
					logger.debug("getMaxSobrepreciofromTabla - sobreprecio mainprecio: "+sobreprecio.getPrecioMinimo() + "sbp maxprecio: "+sobreprecio.getPrecioMaximo());
				else
					logger.debug("sobreprecio es null");
				return sobreprecio;
			}else{
				logger.debug("getMaxSobrepreciofromTabla - sobreprecio mainprecio: "+sobreprecio.getPrecioMinimo() + "sbp maxprecio: "+sobreprecio.getPrecioMaximo());
				return sobreprecio;
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
	}
	
	@Override
	public TasasSbp getMaxTasasfromTabla(BigDecimal cultivo,BigDecimal codProvincia, Long lineaseguroId,BigDecimal codcomarca)
			throws DAOException {
		logger.debug("init - [SimulacionSbpDao] getMaxTasasfromTabla");
		
		TasasSbp tasa = null;
		try{
			final Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(TasasSbp.class);
			criteria.createAlias(COMARCA, COMARCA);
			criteria.createAlias(CULTIVO, CULTIVO);
			criteria.createAlias(LINEA, LINEA);
			
			criteria.add(Restrictions.eq(COM_CODCOM, codcomarca));
			criteria.add(Restrictions.eq(COM_CODPROV, codProvincia));
			criteria.add(Restrictions.eq(CULTIVO_ID, cultivo));
			criteria.add(Restrictions.eq(LIN_LINEAID, lineaseguroId));
			
			tasa = (TasasSbp) criteria.uniqueResult();
			
			
			if (tasa == null || tasa.getId() == null) {
				logger.debug("Buscamos la tasa para la comarca genÃ©rica");
				// Buscamos la tasa para la comarca
				criteria = session.createCriteria(TasasSbp.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(COMARCA, COMARCA);
				criteria.createAlias(CULTIVO, CULTIVO);

				criteria.add(Restrictions.eq(LIN_LINEAID,
						lineaseguroId));
				criteria.add(Restrictions.eq(COM_CODPROV, codProvincia));
				criteria.add(Restrictions.eq(COM_CODCOM, new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, cultivo));
				tasa = (TasasSbp) criteria.uniqueResult();
			}

			if (tasa == null || tasa.getId() == null) {
				logger.debug("Buscamos la tasa para la comarca y la provincia genÃ©rica");
				// Buscamos la tasa para la comarca y la provincia
				criteria = session.createCriteria(TasasSbp.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(COMARCA, COMARCA);
				criteria.createAlias(CULTIVO, CULTIVO);

				criteria.add(Restrictions.eq(LIN_LINEAID,
						lineaseguroId));
				criteria.add(Restrictions.eq(COM_CODPROV,	new BigDecimal("99")));
				criteria.add(Restrictions.eq(COM_CODCOM,new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID,cultivo));
				tasa = (TasasSbp) criteria.uniqueResult();
			}

			if (tasa == null || tasa.getId() == null) {
				// Buscamos la tasa para la comarca, la provincia y el cultivo
				logger.debug("Buscamos la tasa para la comarca, la provincia y el cultivo genÃ©ricos");
				criteria = session.createCriteria(TasasSbp.class);
				criteria.createAlias(LINEA, LINEA);
				criteria.createAlias(COMARCA, COMARCA);
				criteria.createAlias(CULTIVO, CULTIVO);

				criteria.add(Restrictions.eq(LIN_LINEAID,
						lineaseguroId));
				criteria.add(Restrictions.eq(COM_CODPROV,	new BigDecimal("99")));
				criteria.add(Restrictions.eq(COM_CODCOM, new BigDecimal("99")));
				criteria.add(Restrictions.eq(CULTIVO_ID, new BigDecimal("999")));
				tasa = (TasasSbp) criteria.uniqueResult();
			}

		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return tasa;
	}
	
	/*
	 * Chequea si el cultivo de sobreprecio estï¿½ en fecha de contrataciï¿½n
	 * 
	*/
	@SuppressWarnings("unused")
	public boolean existeCultivosSbpContratables(PolizaSbp p) throws DAOException {
		
		logger.debug("SimulacionSbpDao - existeCultivosSbpContratables - init");
		
		boolean existeCultivoEnFecha = false;
		List<BigDecimal> lstCultivos = new ArrayList<BigDecimal>();
		
		for (Parcela par : p.getPolizaPpal().getParcelas()) {
			if (!lstCultivos.contains(par.getCodcultivo()))
				lstCultivos.add(par.getCodcultivo());
		}	
		if (lstCultivos != null) {
			for (int i = 0; i < lstCultivos.size(); i++) {			
				try{					
					existeCultivoEnFecha = isCultivoSbpContratable(p.getPolizaPpal().getLinea().getLineaseguroid(),new BigDecimal(Long.parseLong(lstCultivos.get(i).toString())));					
					if (existeCultivoEnFecha){
						return existeCultivoEnFecha;
					}
				} catch (Exception ex) {
					logger.error("[SimulacionSbpDao] existeCultivosSbpContratables - Se ha producido un error en la BBDD: " + ex.getMessage());
				}
			}
			return existeCultivoEnFecha;	
		}
		
		logger.debug("SimulacionSbpDao - existeCultivosSbpContratables - end");

		return false;
	}
	
	public ArrayList<Object[]> getPolConAnexosCupon () throws DAOException{
		
		ArrayList<Object[]> listPolConAnexosCupon = null;
		try{
			final Session session = obtenerSession();
			
			String sql ="select p.idpoliza,l.codlinea,l.codplan" + 
					"  from o02agpe0.tb_anexo_mod m, o02agpe0.tb_anexo_mod_cupon c,o02agpe0.tb_polizas p,o02agpe0.tb_lineas l" + 
					" where m.idcupon = c.id" + 
					"   and p.idpoliza = m.idpoliza" + 
					"   and p.lineaseguroid = l.lineaseguroid" + 
					"   and c.estado in (6, 7)" + 
					"   and m.revisar_sbp = 'S'" + 
					" group by p.idpoliza,l.codlinea,l.codplan";
			listPolConAnexosCupon = (ArrayList<Object[]>) session.createSQLQuery(sql).list();
		
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return listPolConAnexosCupon;
		
	}
	public ArrayList<AnexoModificacion> getAnexosCuponParaSbp(Long[] estadosCupon, Character revisarSbp) throws DAOException{
		ArrayList<AnexoModificacion> listaAnexosCupon = null;
		
		logger.info("getAnexosCuponByPol.estadosCupon - " + estadosCupon);
		logger.info("getAnexosCuponByPol.revisarSbp - " + revisarSbp);
		
		try{
			final Session session = obtenerSession();
			Criteria criteria = session.createCriteria(AnexoModificacion.class);
			criteria.createAlias("cupon", "cupon");
			criteria.createAlias(POLIZA, POLIZA);
			criteria.add(Restrictions.in("cupon.estadoCupon.id", estadosCupon));
			criteria.add(Restrictions.eq("revisarSbp", revisarSbp));
			criteria.addOrder(Order.desc("cupon.fecha"));
			listaAnexosCupon = (ArrayList<AnexoModificacion>) criteria.list();
			
			
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return listaAnexosCupon;
	}
	@Override
	public ArrayList<AnexoModificacion> getAnexosCuponParaSbp() throws DAOException {
		
		logger.debug("init- [SimulacionSbpDao] getAnexosParaSbp");
		ArrayList<AnexoModificacion> listAnexoSWSbp = new ArrayList<AnexoModificacion>();
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		AnexoModificacion am;
		try{
			final Session session = obtenerSession();
			String sql = getConsultaSqlFecha();
			
			Date inifechaContratacion = (Date) session.createSQLQuery(sql).uniqueResult();
			logger.debug("Fecha Minima de inicio de contratacion: " +  inifechaContratacion);
			
		    String sql2 = " select m.id from o02agpe0.TB_ANEXO_MOD m, o02agpe0.TB_ANEXO_MOD_HISTORICO_ESTADOS e, o02agpe0.TB_ANEXO_MOD_CUPON c"+          
            	" where m.id = e.idanexo "+    
            	 "  and c.id = m.idcupon "+    
            	 "  and m.tipo_envio = '"+ Constants.ANEXO_MODIF_TIPO_ENVIO_SW + "'"+      
            	 "  and m.estado =  "+  Constants.ANEXO_MODIF_ESTADO_CORRECTO +   
            	 "  and e.estado =  "+  Constants.ANEXO_MODIF_ESTADO_CORRECTO  +    
            	 "  and c.estado =  "+ Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE +
            	 "  and e.fecha >= to_date('"+formatoDeFecha.format(inifechaContratacion)+"','dd/MM/yyyy HH24:MI:ss')" + 
            	 "  and REVISAR_SBP is null";        
		    
		    List<BigDecimal> aux = (List<BigDecimal>)session.createSQLQuery(sql2).list();
			
			logger.debug("se han encontrado anexos SW para Sbp: " + aux.size());
			for (int i=0; i<aux.size(); i++ ) {
				BigDecimal id = aux.get(i);
				am = declaracionModificacionPolizaDao.getAnexoModifById(id.longValue());
				listAnexoSWSbp.add(am);
			}
						  
			
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return listAnexoSWSbp;
	}
	
	/**
	 * Devuelve la consulta para buscar la minima fecha de contratacion
	 * 26/03/2014
	 * @return
	 */
	private String getConsultaSqlFecha () {
		
		return "select min(fechainicio) from o02agpe0.tb_lineas l, o02agpe0.tb_sbp_fechas_contratacion f " + 
				" where l.lineaseguroid = f.lineaseguroid and l.codplan in " + 
				" (select max(codplan) from o02agpe0.tb_lineas l, o02agpe0.tb_sbp_fechas_contratacion f " +
				" where l.lineaseguroid = f.lineaseguroid)";
	}
	
	
	public BigDecimal esPolizaConSbp(Long idPoliza, BigDecimal codLinea, BigDecimal codPlan) throws DAOException {
		List<FechaContratacionSbp> listFechas = new ArrayList<FechaContratacionSbp>();
		Map<String, FechaContratacionSbp> fechasMap = new HashMap<String, FechaContratacionSbp>();
		boolean returnIdPoliza = false;
		try{
			final Session session = obtenerSession();
			String sql = " select max(fecha) as fechas_envio,p.id" +
						 " from o02agpe0.tb_SBP_HISTORICO_ESTADOS e, o02agpe0.tb_sbp_polizas p " +
					     " where (p.idpoliza="+idPoliza+" or p.idpolizacom = "+idPoliza+") and p.idestado=5" +
					     " and tipoenvio= 1 and p.id= e.idpoliza_sbp" +
					     " and e.estado=5 group by p.id" ;			
			Object[] sbp =  (Object[]) session.createSQLQuery(sql).uniqueResult();			
			logger.info("Consulta --> " + sql);			 
			String clave = codLinea + "-" + codPlan;		
			if (sbp!=null) { 
				
				if (fechasMap.containsKey(clave)) {
					FechaContratacionSbp f = fechasMap.get(clave);					
					if (estaDentroDeFecha(new Date(new Long(sbp[0].toString())), f.getFechainicio(), f.getFechaFinSuplementos())) {
						returnIdPoliza = true;
					}
				}else {
					// cargamos la fecha de contratacion para esa linea/plan
					Criteria criteria = session.createCriteria(FechaContratacionSbp.class);
					criteria.createAlias(LINEA, LINEA);
					criteria.add(Restrictions.eq("linea.codlinea", codLinea));
					criteria.add(Restrictions.eq("linea.codplan", codPlan));					
					listFechas = criteria.list();	
					
					/* SONAR Q */
					returnIdPoliza = obtenerValor(listFechas, clave, returnIdPoliza, fechasMap, idPoliza, sbp);
	                /* FIN SONAR Q */
				}
				if (returnIdPoliza) {
					return new BigDecimal(sbp[1].toString());// devolvemos el idpoliza
				}
			}else {
				logger.info("No ha polizas de sobreprecio para la poliza: " +idPoliza);
			}
			
			return null;
			
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
	}
	
	private boolean estaDentroDeFecha(Date date, Date fechainicio, Date fechafin) {
		if (date.compareTo(fechainicio) >= 0 && date.compareTo(fechafin) <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si el campo gen_spl_cpl de la poliza de sobreprecio esta relleno
	 * Si esta relleno con S no devolvemos false par que no actualice los anexos 
	 */
	public void actualizaFlagSbp(Long idSbp, Session sesion) throws DAOException {
		try {
			this.updateFlag(idSbp, Constants.CHARACTER_S, sesion);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR, ex);
		}
	}
	
	public void updateFlag(Long idSbp,Character flag, Session sesion) throws DAOException {
		try{
			logger.info("actualizamos el flag gen_spl_cpl de tb_sbp_polizas a :" +flag +" de la poliza de sbp: "+idSbp);
			String sql2 = UPDATE_SBPPOL+flag+"' where p.id=" +idSbp;
			sesion.createSQLQuery(sql2).executeUpdate();
		
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
	}
	
	public void updateFlagbyIdsPolSbp(List<Long> lstPolSbp,Character flag) throws DAOException {		
		StringBuilder sb = new StringBuilder(); 
		Transaction trans = null;
		boolean primera = true;
		for (Long id:lstPolSbp){
			if (primera){
				sb.append(id.toString());
				primera = false;
			}else{
				sb.append(","+id.toString());
			}
		}
		try{
			logger.info("polizasSbp a actualizar: "+sb.toString());
			final Session session = obtenerSession();
			trans = session.beginTransaction();
			
			String sql2 = UPDATE_SBPPOL+flag+"' where p.id in (" +sb.toString()+")";
			int resul = session.createSQLQuery(sql2).executeUpdate();
			
			trans.commit();
			
			logger.debug("resultado de la actualizacion de polizas sbp: " +resul);
		
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos.",ex);
		}
	}
	
	public void deleteParcela(ParcelaSbp parcela) throws DAOException {
		Session session = obtenerSession(); 
				
		try{
			session.delete(parcela);
			session.flush();
			session.evict(parcela);
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el borrado de la parcela", ex);
		}
		finally{
		}
	}	
	
	@Override
	public List<PolizaSbp> getPolizasSbpParaSuplementos() throws DAOException {
		
		logger.debug("init- [SimulacionSbpDao] getPolizasSbpParaSuplementos");
		ArrayList<PolizaSbp> listPolizasSbp = null;
		try{
			final Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(PolizaSbp.class);
			// MPM
			criteria.createAlias(POL_PPAL, "pp");
			criteria.createAlias("pp.estadoPoliza", "ppEst");			
			criteria.add(Restrictions.eq("genSplCpl", Constants.CHARACTER_S));
			criteria.add(Restrictions.eq("ppEst.idestado", Constants.ESTADO_POLIZA_DEFINITIVA));
					
			/* P79222_3 - REQ.20 */
			/* Inicio */
			List<String> refsPolizasExcluir = obtenerPolizasExcluir();
			
			if(refsPolizasExcluir != null){
				if(refsPolizasExcluir.size() > 0){
					criteria.add(Restrictions.not(Restrictions.in(REFERENCIA, refsPolizasExcluir)));
				}
			}
			/* Fin */
			
			listPolizasSbp = (ArrayList<PolizaSbp>) criteria.list();
			
			for (PolizaSbp polSbp : listPolizasSbp) {
				polSbp.getFechaEnvioSbp();
				logger.debug("Poliza sbp aplicable para suplemento" + polSbp.getReferencia());

			}
			
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return listPolizasSbp;
	}
	
	/**
	 * P79222_3 - REQ.20 Excluimos aquellas polizas que tengan un suplemento de sobreprecio 
	 * 			  ya generado sobre esa misma poliza y que este pendiente de envío/respuesta
	 * @return
	 * @throws DAOException 
	 */
	private List<String> obtenerPolizasExcluir() throws DAOException {
		
		logger.debug("SimulacionSbpDao - obtenerPolizasExcluir - init");

		List<String> refsPolizasExcluir = new ArrayList<>();
		
		try{
			final Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(PolizaSbp.class);
			
			criteria.createAlias(EST_POLSBP, "estadoSbp");
			criteria.add(Restrictions.eq(TIPOENV_ID, ConstantsSbp.TIPO_ENVIO_SUPLEMENTO));
			criteria.add(Restrictions.eq(ESTADOSBP, ConstantsSbp.ESTADO_PENDIENTE_ACEPTACION));
			criteria.setProjection(Projections.projectionList().add(Projections.property(REFERENCIA), REFERENCIA));
			
			refsPolizasExcluir = (ArrayList<String>) criteria.list();
			
			for (String refPoliza : refsPolizasExcluir) {
				logger.debug("Poliza excluida: " + refPoliza);

			}
			
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		
		logger.debug("SimulacionSbpDao - obtenerPolizasExcluir - end");

		return refsPolizasExcluir;
	}

	@SuppressWarnings("unused")
	@Override
	public void insertarHistoricoSuplemento(PolizaSbp psbp) throws DAOException {
		
		logger.debug("init- [SimulacionSbpDao] insertarHistoricoSuplemento");
		ArrayList<PolizaSbp> listPolizasSbp = null;
		try{
			final Session session = obtenerSession();
			
			
			HistoricoEstado he = new HistoricoEstado();
			he.setPolizaSbp(psbp);
			he.setCodusuario("BATCH");
			he.setEstado(ConstantsSbp.ESTADO_GRAB_DEF);
			he.setFecha(java.util.Calendar.getInstance().getTime());
			
			session.saveOrUpdate(he);
			session.flush();
			session.evict(he);
			
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error al guardar el histÃ³rico de estados del sobreprecio",ex);
		}
	}
	
	@Override
	public boolean esUltimoDiaEnvSupl() throws DAOException {		
		try{
			final Session session = obtenerSession();
			String sql = "select max(fecha_fin_supl) from o02agpe0.tb_sbp_fechas_contratacion s";
			Date fechaFinContratacion = (Date) session.createSQLQuery(sql).uniqueResult();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String fechaHoy = sdf.format(date);
			Date d = sdf.parse(fechaHoy);			
			if (fechaFinContratacion.compareTo(d) == 0) {
				return true;
			}
		}catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR,ex);
		}
		return false;
	}

	@Override
	public void guardaSuplemento(PolizaSbp polizaSbp, Usuario usuario,
			Long idPolizaPpal, Character characterN,Session session, BigDecimal estado) throws Exception {
		
		try {
			// grabamos la polizaSbp	
			EstadoPlzSbp estadoPlzSbp = new EstadoPlzSbp();
			estadoPlzSbp.setIdestado(estado);
			polizaSbp.setEstadoPlzSbp(estadoPlzSbp);
			
			if (null==session) {
				session = obtenerSession();
			}
			
			session.saveOrUpdate(polizaSbp);
			session.flush();
			session.evict(polizaSbp);
			
			logger.debug("PolizaSbp suplemento creada : " + polizaSbp.getId());
			
			// guardamos las parcelas
			for (ParcelaSbp parcelaSbp : polizaSbp.getParcelaSbps()) {
				parcelaSbp.setPolizaSbp(polizaSbp);
				session.saveOrUpdate(parcelaSbp);
				session.flush();
				session.evict(parcelaSbp);
			}
			// insertamos en el historico. No lo hacemos por pl, porque al ser ejecutado desde
			// el jar de suplementos no funciona						
			logger.debug("Introducir el registro en el historico correspondiente al sbp " + polizaSbp.getId());
			
			// Historico del suplemento
			String sql = " insert into o02agpe0.TB_SBP_HISTORICO_ESTADOS " +
					"(id,idpoliza_sbp,codusuario,fecha,estado) " +
					" values ( o02agpe0.sq_sbp_historico_estados.nextval,"
					+polizaSbp.getId()+",'"+usuario.getCodusuario()+"',to_date(sysdate),"+estado+ " )";
			
			logger.debug("--> " + sql);
			
			session.createSQLQuery(sql).executeUpdate();
			
			this.documentacionGedDao.saveNewGedDocPolizaSBP(polizaSbp.getId(), usuario.getCodusuario(), null);
			
		}catch (Exception ex){
			logger.error("Error al guardar el suplemento ",ex);

		}catch (Throwable a ) {
			logger.error("Error al guardar el suplemento ",a);
		}
	}

	@Override
	public void updateFlagAnexo(ArrayList<Long> listaAnexosActualizar) throws DAOException {
		logger.info("actualizamos el flag revisar_sbp de tb_anexo_mod a :N del id de los anexos: "+StringUtils.toValoresSeparadosXComas(listaAnexosActualizar, false, true));
		Transaction trans2 = null;
		String sql2 = "update o02agpe0.tb_anexo_mod  set revisar_sbp='N' where id in ";
		
		try{		
			final Session session = obtenerSession();
			trans2 = session.beginTransaction();
			int result = 0;
			// SE TROCEA EN BLOQUES DE 500 REGISTROS PARA EVITAR
			// LA LIMITACION DE 1000 CONDICIONES EN BLOQUE IN
			// Y AGILIZAR LAS SENTENCIAS
			List<List<Long>> listaQuerys = chopped(listaAnexosActualizar, 500);
			for (List<Long> listaAnexosQuerys : listaQuerys) {
				result += session.createSQLQuery(sql2 + StringUtils.toValoresSeparadosXComas(listaAnexosQuerys, false, true)).executeUpdate();
			}
			logger.debug("resultado de la actualizacion de anexos: " +result);
			trans2.commit();
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos..",ex);
		}		
	}
	
	private List<List<Long>> chopped(List<Long> list, final int L) {
	    List<List<Long>> parts = new ArrayList<List<Long>>();
	    final int N = list.size();
	    for (int i = 0; i < N; i += L) {
	        parts.add(new ArrayList<Long>(
	            list.subList(i, Math.min(N, i + L)))
	        );
	    }
	    return parts;
	}

	public void setDeclaracionModificacionPolizaDao(
			IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao) {
		this.declaracionModificacionPolizaDao = declaracionModificacionPolizaDao;
	}

	
	/** PET-63699 DNF 14/05/2020*/ 
	public Long getPolizaSbpId(final Long idpolizaPpal) throws DAOException {
		Long idSbp = null;
		try {
			logger.debug("init - hayPolizaSbp: idpolizaPpal= " + idpolizaPpal);
			Session session = obtenerSession();
			PolizaSbp polizaSbp = (PolizaSbp) session.createCriteria(PolizaSbp.class)
					.createAlias(POL_PPAL, POL_PPAL).createAlias(EST_POLSBP, EST_POLSBP)
					.createAlias(TIPOENV, TIPOENV).add(Restrictions.eq(TIPOENV_ID, BigDecimal.ONE))
					.add(Restrictions.eq(IDPOL_PPAL, idpolizaPpal))
					.add(Restrictions.eq("estadoPlzSbp.idestado", ConstantsSbp.ESTADO_ENVIADA_CORRECTA)).uniqueResult();
			if (polizaSbp != null) {
				idSbp = polizaSbp.getId();
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
		return idSbp;
	}
	 
	public void updateGenSplCpl(final Long idSbp) throws DAOException {
		Session session = obtenerSession();
		try {
			Query sql = session
					.createSQLQuery("UPDATE o02agpe0.TB_SBP_POLIZAS SET GEN_SPL_CPL = 'S' WHERE ID = " + idSbp);
			sql.executeUpdate();
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException(ERROR, ex);
		}
	}
	/** FIN PET-63699 DNF 14/05/2020*/
	
	
	/* MODIF TAM (23.11.2021) ** SONAR Q ** Inicio */
	/* Creamos nuevas funciones, para descargar las funciones principales de ifs/fors */
	private String obtenerFiltroCultivos(List cultivos, StringBuilder sb, String delim, String separator) {
		String filtroCultivos = "";
	
		if (cultivos != null && !cultivos.isEmpty()) {
			
			for (int i = 0; i < cultivos.size(); i++) {
				String combo = (String)cultivos.get(i);
				String[] arrCult = combo.split("_");					
				if (!"999".equals(arrCult[0])){
					sb.append("X");
					delim = separator;
					break;				
				}
			}
			if (!sb.toString().equals("")){
				boolean primera = true;
				for (int i = 0; i < cultivos.size(); i++) {
					String combo = (String)cultivos.get(i);
					String[] arrCult = combo.split("_");							
					if (primera){
						filtroCultivos = " and (";
						filtroCultivos = filtroCultivos + " (pa.codcultivo = "+arrCult[0] + " and c.codtipocapital = "+arrCult[1]+")";
						primera = false;
					}else{
						filtroCultivos = filtroCultivos + " or  (pa.codcultivo = "+arrCult[0] + " and c.codtipocapital = "+arrCult[1]+")";
					}
					
				}
				filtroCultivos = filtroCultivos + " )";
			}	
		}
		return filtroCultivos;
	}
	
	/* SONAR Q */
	private List<ParcelaSbp> obtenerListaParc(String tipoPoliza, List<ParcelaSbp> lstParSbp, List<ParcelaSbp> listaParcelas,
								              PolizaSbp polizaSbp,boolean incluirCplEnSbp, boolean filtroxComarca) throws DAOException{
	
		logger.debug("SimulacionSbpDao - obtenerListaParc - init");
		
		if ((tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL) || tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL))&& lstParSbp!=null){
			
			logger.debug("Usamos las parcelas que llegan por parametro en la cabecera de la funcion");
			
			for (ParcelaSbp pSbp : lstParSbp) {
				logger.debug("PARCELA: " + pSbp.getId() + " PRECIO --> " + pSbp.getSobreprecio());
				listaParcelas.add(pSbp);
			}			
		}else{
			
			logger.debug("Se recogen las parcelas llamando al getParcelasParaSbp");
			
			List listCultivos = this.getCultivosSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid());
			// Si es de la situacion actualizada cojo las parcelas sbp que vienen por parametro
			listaParcelas = getParcelasParaSbp(polizaSbp, tipoPoliza, incluirCplEnSbp, filtroxComarca, listCultivos);
		}
		
		logger.debug("SimulacionSbpDao - obtenerListaParc - end");

		return listaParcelas;
	}
	
	/* SONAR Q */
	private List<Sobreprecio> obtenerSbpListFinal(List<Sobreprecio> sbpListFinal, List<Sobreprecio>sbpList, 
												  boolean revisar, String cultTemp){ 
		
		logger.debug("SimulacionSbpDao - obtenerSbpListFinal - init");
				
		for (Sobreprecio sbp : sbpList) {
						
			if (revisar){ //cultivo antiguo o el primero
				if (sbp.getCultivo().getId().getCodcultivo().toString().equals(cultTemp)){
					if (sbp.getProvincia().getCodprovincia().toString().equals("99")){ 
						revisar = false;
					}else{
						sbpListFinal.add(sbp);
						cultTemp = sbp.getCultivo().getId().getCodcultivo().toString();					
						revisar = true;
					}
				}else{ // nuevo cultivo
					cultTemp = sbp.getCultivo().getId().getCodcultivo().toString();				
					if (sbp.getProvincia().getCodprovincia().toString().equals("99")){ // meto el 99
						sbpListFinal.add(sbp);
						revisar = false;
					}else{						
						sbpListFinal.add(sbp);					
						revisar = true;
					}
				}
			}else{ // nuevo cultivo
				cultTemp = sbp.getCultivo().getId().getCodcultivo().toString();				
				if (sbp.getProvincia().getCodprovincia().toString().equals("99")){ // meto el 99
					sbpListFinal.add(sbp);
					revisar = false;
				}else{						
					sbpListFinal.add(sbp);					
					revisar = true;
				}
			}
		}	
		
		logger.debug("SimulacionSbpDao - obtenerSbpListFinal - end");

		return sbpListFinal;
	}
	
	
	/* SONAR Q */
	private boolean obtenerValor(List<FechaContratacionSbp> listFechas, String clave, 
												boolean returnIdPoliza, Map<String, FechaContratacionSbp>fechasMap, 
												Long idPoliza, Object[] sbp) {
		
		if (listFechas.size()>0) {
			// guardamos las fechas/codlinesa-plan en el map
			FechaContratacionSbp fecSbp = listFechas.get(0);						
			fechasMap.put(clave, fecSbp);						
			Date fechaEnvio = (Date) sbp[0];
			if (estaDentroDeFecha(fechaEnvio, fecSbp.getFechainicio(), fecSbp.getFechaFinSuplementos())) {
				returnIdPoliza = true;
			}
		}else {
			logger.info("Hay polizas de sobreprecio para la poliza: " +idPoliza+ " " +
					" pero NO estan dentro del periodo de contratacion ");
		}
		
		return returnIdPoliza;
	}
	
	public void setDocumentacionGedDao(IDocumentacionGedDao documentacionGedDao) {
		this.documentacionGedDao = documentacionGedDao;
	}

	@Override
	public PolizaSbp getPolizaSbp(final Long idpoliza, final Character tipoRef) throws DAOException {
		PolizaSbp polizaSbp;
		try {
			logger.debug("[init] getPolizaSbp");
			Session session = obtenerSession();
			Criteria crit = session.createCriteria(PolizaSbp.class).createAlias(POL_PPAL, POL_PPAL)
					.createAlias(POL_COMP, POL_COMP).createAlias(TIPOENV, TIPOENV)
					.add(Restrictions.eq(TIPOENV_ID, BigDecimal.ONE));
			if (Constants.MODULO_POLIZA_PRINCIPAL.equals(tipoRef)) {
				crit.add(Restrictions.eq(IDPOL_PPAL, idpoliza));
			} else {
				crit.add(Restrictions.eq(IDPOL_COMP, idpoliza));
			}
			polizaSbp = (PolizaSbp) crit.uniqueResult();
			logger.debug("[end] getPolizaSbp");
			return polizaSbp;
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
	}
}	