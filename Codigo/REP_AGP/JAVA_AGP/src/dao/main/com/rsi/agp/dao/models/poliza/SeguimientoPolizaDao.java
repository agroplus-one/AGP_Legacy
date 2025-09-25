package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.SeguimientoPolizaBean;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.AsuntosIncId;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.IncidenciasHist;
import com.rsi.agp.dao.tables.inc.Motivos;
import com.rsi.agp.dao.tables.seguimiento.SwSeguimientoContrat;
import com.rsi.agp.dao.tables.seguimiento.TmpBatchSeguimiento;
import com.rsi.agp.dao.tables.seguimiento.TmpBatchSeguimientoId;

import es.agroseguro.seguimientoContratacion.Incidencia;

public class SeguimientoPolizaDao extends BaseDaoHibernate implements ISeguimientoPolizaDao {

	private static final BigDecimal ESTADO_CONFIRMADO_TRAMITE = new BigDecimal(6);
	private static final char REVISAR_SBP = 'S';
	private static final String ESTADO_PARCIALEMNTE_ACEPTADO = "P";
	private static final String ESTADO_ACEPTADO = "A";
	private static final Character CATALOGO_POLIZA = 'P';
	
	private IHistoricoEstadosManager iHistoricoEstadosManager;
	private Session session;
	
	private final String IDPOLIZA_FIELD = "idPoliza";
	
	/**
	 * M�todo que actualiza el estado de una poliza
	 * @author DANUNEZ
	 * @since 17/01/2019
	 * @param idPoliza Identificador �nico de la una p�liza
	 * @param costeTomador Indicador del coste Tomador de la p�liza en Agroseguro
	 * @param idEstadoAgro Indicador �nico del Estado de la p�liza en Agroseguro
	 * @param fechaCamEstado Indicador de la fecha en la que se produjo el cambio de Estado.
	 * @return void
	 **/
	
	public void actualizarPoliza(Long idPoliza, BigDecimal costeTomador, BigDecimal idEstadoAgro, Date fechaCambioEstado, String codUsuario, Integer plan) throws DAOException {

		session = obtenerSession();
		BigDecimal estadoAgroplusDeseado;
		BigDecimal idEstadoActual;
		SQLQuery queryEstPol;
		SQLQuery queryIdPol;
		Query update;
		Query updateIdEstado;		
		boolean esRenovable = false;
		
		esRenovable = "S".equals((String) session.createSQLQuery(
				"select decode(count(*), 0, 'N', 'S') from o02agpe0.TB_MODULOS_POLIZA mp where mp.renovable = 1 and mp.idpoliza = "
						+ idPoliza)
				.uniqueResult());
		
		//1.-El Estado Agroplus actual de la poliza lo recibo por parametros: idEstadoAgro		
		try{
			
			//2.-En Tb_polizas tengo que actualizar los tres campos de la tabla con los parametros recibidos
			update = session
					.createSQLQuery(
							"update ( select pol.idestado_agro, pol.fecha_seguimiento, pol.coste_tomador_agro "
							+ "from o02agpe0.TB_POLIZAS pol "
							+ "inner join o02agpe0.TB_LINEAS lin "
							+ "on pol.lineaseguroid = lin.lineaseguroid and pol.idpoliza = :idPoliza and lin.codplan = :plan) t "
							+ "set t.idestado_agro = :idEstadoAgro, t.fecha_seguimiento = :fechaSeguimiento, t.coste_tomador_agro = :costeTomadorAgro")
					.setBigDecimal("idEstadoAgro", idEstadoAgro)
					.setDate("fechaSeguimiento", fechaCambioEstado)
					.setBigDecimal("costeTomadorAgro", costeTomador)
					.setLong(IDPOLIZA_FIELD, idPoliza)
					.setInteger("plan", plan);
			

			update.executeUpdate();
			
			String sqlEstadoDeseado = "SELECT IDESTADO_POL FROM o02agpe0.tb_relacion_estados where IDESTADO_AGRO = "
					+ idEstadoAgro;
			
			//3.-Saber el Estado Agroplus que deber�a tener la poliza			
			queryEstPol = session.createSQLQuery(sqlEstadoDeseado);
			estadoAgroplusDeseado = (BigDecimal) queryEstPol.uniqueResult();
		
			
			//4.-Si el Estado Agroplus es diferente se actualiza
			//obtengo el idestado actual de la tabla tb_polizas
			if (null != estadoAgroplusDeseado) {
				
				String sqlIdEstado = "SELECT IDESTADO FROM o02agpe0.TB_POLIZAS where IDPOLIZA = "+ idPoliza;
				queryIdPol = session.createSQLQuery(sqlIdEstado);
				idEstadoActual = (BigDecimal) queryIdPol.uniqueResult();
				
				boolean debeActualizar = estadoAgroplusDeseado.compareTo(idEstadoActual) != 0;
				// Las polizas en estado 8-Enviada Correcta solo se actualizan al estado 16-Anulada 
				if (Constants.ESTADO_POLIZA_DEFINITIVA.equals(idEstadoActual) && !Constants.ESTADO_POLIZA_ANULADA.equals(estadoAgroplusDeseado)) {
					debeActualizar = false;
				}
				
				if((Constants.ESTADO_POLIZA_ANULADA.equals(idEstadoActual) || Constants.ESTADO_POLIZA_RESCINDIDA.equals(idEstadoActual))
						&& (new BigDecimal(3)).equals(idEstadoAgro) && (!esRenovable || iHistoricoEstadosManager.esNuevaContratacion(idPoliza))) {
					estadoAgroplusDeseado = Constants.ESTADO_POLIZA_DEFINITIVA;
				}
				
				if((Constants.ESTADO_POLIZA_ANULADA.equals(idEstadoActual) || Constants.ESTADO_POLIZA_RESCINDIDA.equals(idEstadoActual))
						&& (new BigDecimal(3)).equals(idEstadoAgro) && esRenovable) {
					estadoAgroplusDeseado = Constants.ESTADO_POLIZA_EMITIDA;
				}
				
				if(debeActualizar) {
					//actualizamos el estado
					updateIdEstado = session
							.createSQLQuery(
									"update o02agpe0.TB_POLIZAS p set p.IDESTADO = :estadoAgroplusDes "
											+ "where p.IDPOLIZA = :idPoliza")
							.setBigDecimal("estadoAgroplusDes", estadoAgroplusDeseado)
							.setLong(IDPOLIZA_FIELD, idPoliza);
	
					updateIdEstado.executeUpdate();
					
					//actualizamos el historico TB_POLIZAS_HISTORICO_ESTADOS
					iHistoricoEstadosManager.insertaEstado(Tabla.POLIZAS, idPoliza, codUsuario, estadoAgroplusDeseado);
				}
			}			
		} catch (Exception e) {
			throw new DAOException("Error durante el acceso a la base de datos", e);
		}	
		
		if (esRenovable) {

			actualizaRenovable(idPoliza, null, idEstadoAgro, costeTomador, plan, codUsuario);
		}
	}
	
	/* ESC-10193 ** MODIF TAM (18.03.2021) ** Inicio */
	
	public void actualizaRenovable(Long idPoliza, Long idRenov, BigDecimal idEstadoAgro, BigDecimal costeTomador,
			Integer plan, String codUsuario) throws DAOException {
		
		logger.debug("Entramos dentro de actualizaRenovable.v1.1");

		BigDecimal estadoRenovDeseado;
		SQLQuery queryEstPolRenov;

		session = obtenerSession();
		
		try {

			String sqlEstadoRenov = "SELECT IDESTADO_RENOV FROM o02agpe0.tb_relacion_estados where IDESTADO_AGRO = "
					+ idEstadoAgro;
			queryEstPolRenov = session.createSQLQuery(sqlEstadoRenov);
			estadoRenovDeseado = (BigDecimal) queryEstPolRenov.uniqueResult();

			if (null != estadoRenovDeseado) {

				List<Object[]> resultado = execGetRenovable(idPoliza, idRenov, plan);			

				if (resultado != null && !resultado.isEmpty()) {
				
					execUpdateRenovable(resultado, estadoRenovDeseado, costeTomador, codUsuario);
				}
			}
		} catch (Exception e) {
			throw new DAOException("Error durante el acceso a la base de datos", e);
		}
	}
	/* ESC-10193 ** MODIF TAM (18.03.2021) ** Fin */
	
	@SuppressWarnings("unchecked")
	private List<Object[]> execGetRenovable(final Long idPoliza, final Long idRenov, final Integer plan) {
		
		List<Object[]> resultado = null;
		
		if (idPoliza == null && idRenov != null) {

			resultado = session
					.createSQLQuery("SELECT p.id, p.estado_agroseguro, gr.estado_agroplus, gr.grupo_negocio "
							+ "FROM o02agpe0.TB_POLIZAS_RENOVABLES p "
							+ "INNER JOIN o02agpe0.TB_GASTOS_RENOVACION gr ON gr.IDPOLIZARENOVABLE = p.ID "
							+ "WHERE p.ID = :idRenov")
					.setLong("idRenov", idRenov).list();

		} else if (idPoliza != null) {

			resultado = session
					.createSQLQuery("SELECT p.id, p.estado_agroseguro, gr.estado_agroplus, gr.grupo_negocio "
							+ "FROM o02agpe0.TB_POLIZAS_RENOVABLES p "
							+ "INNER JOIN o02agpe0.TB_GASTOS_RENOVACION gr ON gr.IDPOLIZARENOVABLE = p.ID "
							+ "INNER JOIN o02agpe0.TB_POLIZAS p2 ON p2.IDPOLIZA = :idPoliza AND p.referencia = p2.referencia "
							+ "INNER JOIN o02agpe0.TB_LINEAS l ON l.codplan = :plan and "
							+ "l.lineaseguroid = p2.lineaseguroid and p.linea = l.codlinea and l.codplan = p.plan")
					.setLong(IDPOLIZA_FIELD, idPoliza).setLong("plan", plan).list();
		}
		
		return resultado;
	}
	
	private void execUpdateRenovable(final List<Object[]> datosRenov, final BigDecimal estadoRenovDeseado,
			final BigDecimal costeTomador, final String codUsuario) {

		String auxStr = StringUtils.nullToString(datosRenov.get(0)[1]);
		BigDecimal auxEstadoAg = new BigDecimal("".equals(auxStr) ? "-1" : auxStr);
		if (auxEstadoAg.compareTo(estadoRenovDeseado) != 0) {

			session.createSQLQuery(
					"UPDATE o02agpe0.TB_POLIZAS_RENOVABLES p " + "SET p.ESTADO_AGROSEGURO = :estadoAgroseguro, "
							+ "COSTE_TOTAL_TOMADOR = :costeTomador " + "WHERE p.ID = :idRenov")
					.setBigDecimal("estadoAgroseguro", estadoRenovDeseado).setBigDecimal("costeTomador", costeTomador)
					.setLong("idRenov", Long.valueOf(datosRenov.get(0)[0].toString())).executeUpdate();

			if (!"".equals(StringUtils.nullToString(datosRenov.get(0)[2]))) {
				// Creamos un historico de estado por grupo de negocio
				for (Object[] resultset : datosRenov) {
					session.createSQLQuery("insert into o02agpe0.tb_plz_renov_hist_estados values "
							+ "(o02agpe0.sq_plz_renov_hist_estados.nextval, " + resultset[0] + ", "
							+ estadoRenovDeseado.toString() + "," + resultset[2] + ", sysdate, '" + codUsuario
							+ "', null, null, null " + ", " + costeTomador + ", null,'" + resultset[3] + "')")
							.executeUpdate();
				}
			}

		}
	}
	
	public void actualizarAnexos(List<Incidencia> lista, String codUsuario, SeguimientoPolizaBean seguimientoPolizaBean)
			throws DAOException {
		
		Query update;
		session = obtenerSession();
		
		logger.debug("SeguimientoPolizaDao - actualizarAnexos [INIT]");
		
		SQLQuery queryEstCupon;
		
		for (Incidencia inc : lista) {		
			
			Criteria criteria = session.createCriteria(AnexoModificacion.class);
			criteria.createAlias("cupon", "cup");
			criteria.add(Restrictions.eq("cup.idcupon", inc.getIdEnvio())); //identificador del cupon
			
			@SuppressWarnings("unchecked")
			List<AnexoModificacion> result = criteria.list();
			
			if (result != null && !result.isEmpty()) {
			
				if(result.size() > 1){
					//log("mas de un anexo recuperado para el cupon = ")
					logger.error("M�s de un anexo recuperado para el cup�n = " + inc.getIdEnvio());
				}
				//Creo la variable anex
				AnexoModificacion anex = result.get(0);
				
				/* MODIF TAM (08.07.2021) - Resoluci�n Incidencia Prueba 04 */
				/* Lanzamos consulta para obtener el estado del cupon*/
				logger.debug("SeguimientoPolizaDao - entramos a obtener el estado del cupon");
					
				String sqlEstadoCupon = "select AnxCup.Estado from o02agpe0.tb_anexo_mod_cupon AnxCup " + 
										  " where AnxCup.Id = "+anex.getCupon().getId();
					
				//3.-Saber el Estado Agroplus que deber�a tener la poliza			
				queryEstCupon = session.createSQLQuery(sqlEstadoCupon);
				BigDecimal estadoCuponBig =  (BigDecimal) queryEstCupon.uniqueResult();
					
				logger.debug("Seteamos el valor del estado del cupon: "+estadoCuponBig);
				/* MODIF TAM (08.07.2021) - Resoluci�n Incidencia Prueba 04 * Fin */
				
				if ((ESTADO_CONFIRMADO_TRAMITE).compareTo(estadoCuponBig) == 0) { 
					if(ESTADO_ACEPTADO.equals(inc.getEstado()) || ESTADO_PARCIALEMNTE_ACEPTADO.equals(inc.getEstado())) {
						anex.setRevisarSbp(REVISAR_SBP);
					}
				} else {
					anex.setRevisarSbp(null);
				}
				
				
				/* MODIF TAM (08.07.2021) - Resoluci�n Incidencia Prueba 04 */
				if (anex.getRevisarSbp() == null) {
					update = session.createSQLQuery(
				  			"update o02agpe0.tb_anexo_mod a set "
				  					+ "a.idestado_agro = :codEstadoAgro, a.fecha_seguimiento = :fechaEstadoAgro, a.revisar_sbp = :revisarSbp "
				  					+ "where a.id = :idAnexo")
				  	.setCharacter("codEstadoAgro", inc.getEstado().charAt(0))
					.setDate("fechaEstadoAgro", inc.getFechaHoraEstado().getTime())
					.setString("revisarSbp", null)
					.setLong("idAnexo", anex.getId());
				}else {
					// Actualiza anexos en la tabla TB_ANEXO_MOD. (IDESTADO_AGRO y FECHA_SEGUIMIENTO) 
					update = session.createSQLQuery(
				  			"update o02agpe0.tb_anexo_mod a set "
				  					+ "a.idestado_agro = :codEstadoAgro, a.fecha_seguimiento = :fechaEstadoAgro, a.revisar_sbp = :revisarSbp "
				  					+ "where a.id = :idAnexo")
				  	.setCharacter("codEstadoAgro", inc.getEstado().charAt(0))
					.setDate("fechaEstadoAgro", inc.getFechaHoraEstado().getTime())
					.setCharacter("revisarSbp", anex.getRevisarSbp())
					.setLong("idAnexo", anex.getId());
				}
					
				update.executeUpdate();
			  	
				String estadoAgroAnex = StringUtils.nullToString(anex
						.getEstadoAgroseguro() == null ? null : anex
						.getEstadoAgroseguro().getCodestado());
				String estadoAnexIncid = StringUtils.nullToString(inc
						.getEstado());

				// comprobaci�n para el hist�rico
				if (!"".equals(estadoAnexIncid)
						&& !estadoAgroAnex.equals(estadoAnexIncid)) {
					//TB_ANEXO_MOD_HIST
					iHistoricoEstadosManager.insertaEstado(Tabla.ANEXO_MOD, anex.getId(), codUsuario, anex.getEstado().getIdestado(), inc.getEstado().charAt(0));
					
				}	
			}
		}
	}
	 
	public void actualizarIncidencias(List<Incidencia> lista, String codUsuario,
			SeguimientoPolizaBean seguimientoPolizaBean) throws DAOException {
		
	    logger.error("Dentro de actualizarIncidencias");
		Query update;
		session = obtenerSession();
		Long idIncidencia;
		String codEstadoAgro;
		// Actualiza incidencias en la tabla TB_INC_INCIDENCIAS. (CODESTADOAGRO: estado recibido como par�metro. en bbdd es un char
		//														  FECHAESTADOAGRO: fecha de cambio de estado recibida como par�metro. bbdd es un date
		//														  FECHA_SEGUIMIENTO: fecha de cambio de estado recibida como par�metro. bbdd es un date
 
		//1� comprobar que existen incidencias que actualizar
		for(Incidencia inc : lista){
		
			// Obtenemos las incidencias afectadas
			String sql = "SELECT IDINCIDENCIA, CODESTADOAGRO"
					+ " FROM o02agpe0.TB_INC_INCIDENCIAS"
					+ " WHERE codestado = 1" // NI BORRADORES NI ERRONEAS
					+ " AND numincidencia = " + inc.getNumero()
					+ " AND anhoincidencia = " + inc.getAnio();
			
			@SuppressWarnings("unchecked")
			List<Object[]> listIncids = (List<Object[]>) session.createSQLQuery(sql).list();
			if (listIncids != null && listIncids.size() > 0){
				for (Object[] incid : listIncids) {
					
					idIncidencia = ((BigDecimal)incid[0]).longValue();
					codEstadoAgro = (String)incid[1];
					
					update = session.createSQLQuery(
							"update o02agpe0.TB_INC_INCIDENCIAS set CODESTADOAGRO = :codEstadoAgro ,"
							+ "FECHA_SEGUIMIENTO = :fechaSeguimiento ,"
							+ "FECHAESTADOAGRO   =  :fechaEstadoAgro "
							+ "WHERE IDINCIDENCIA = :idIncidencia")						
							.setString("codEstadoAgro", inc.getEstado())
							.setDate("fechaSeguimiento", inc.getFechaHoraEstado().getTime())
							.setDate("fechaEstadoAgro", inc.getFechaHoraEstado().getTime())
							.setLong("idIncidencia", idIncidencia);
				  
				  	update.executeUpdate(); 
								
					if(null != codEstadoAgro && inc.getEstado() != null && !codEstadoAgro.equals(inc.getEstado())){
						IncidenciasHist hist = populateIncHist( inc, codUsuario, seguimientoPolizaBean, idIncidencia);
						this.saveOrUpdate(hist);
					}	
				}
			}
		}
	}	 
	
	public void auditarLlamadaSW(final BigDecimal plan,
			final String referencia, final Date fechaDesde,
			final Date fechaHasta, final String usuario, final String xml)
			throws DAOException {
		SwSeguimientoContrat obj = new SwSeguimientoContrat();
		obj.setPlan(plan);
		obj.setReferencia(referencia);
		obj.setFechaDesde(fechaDesde);
		obj.setFechaHasta(fechaHasta);
		obj.setUsuario(usuario);
		obj.setXml(xml);
		obj.setFechaPet(new Date());
		this.saveOrUpdate(obj);
	}
	
	public IncidenciasHist populateIncHist(Incidencia inc, String codUsuario,
			SeguimientoPolizaBean seguimientPB, Long idIncidencia) throws DAOException {
		logger.error("Dentro de actualizar Historico de Incidencias");

		IncidenciasHist hist = new IncidenciasHist();
		
		/* Incidencia Pet. 57627 */
		int codmotivo = 0;
		/* Incidencia Pet. 57627 Fin */

		hist.setCodplan(new BigDecimal(seguimientPB.getPlan()));
		hist.setCodlinea(new BigDecimal(seguimientPB.getLinea()));
		hist.setReferencia(seguimientPB.getReferenciaPoliza());
		hist.setDc(new BigDecimal(seguimientPB.getDigitoPoliza()));
		hist.setTiporef(seguimientPB.getReferenciaPoliza().charAt(0));
		hist.setNifaseg(seguimientPB.getNifAsegurado());
		hist.setNumincidencia(new BigDecimal(inc.getNumero()));
		hist.setAnhoincidencia(new BigDecimal(inc.getAnio()));
		hist.setFechaestadoagro(inc.getFechaHoraEstado().getTime());
		hist.setCodestado(seguimientPB.getEstado());
		hist.setFechaestado(seguimientPB.getFechaEstado());
		hist.setIdenvio(inc.getIdEnvio());
		hist.setObservaciones(null);
		hist.setNumdocumentos(null);
		hist.setTimestamp(new Date());
		hist.setUsuario(codUsuario);
		
		/* Incidencia Pet. 57627 */
		logger.error("Insertamos el motivo nulo");
		hist.setmotivos((Motivos) this.getObject(Motivos.class, codmotivo));
		/* Incidencia Pet. 57627 * Fin*/
		
		// AsuntosInc
		AsuntosInc asuntoAux = (AsuntosInc) this.getObject(AsuntosInc.class,
				new AsuntosIncId(inc.getAsunto(), CATALOGO_POLIZA));
		// si no existe asunto lo creamos y lo guardamos en la tabla de asuntos
				if(asuntoAux == null) {
					asuntoAux = new AsuntosInc(new AsuntosIncId(inc.getAsunto(), CATALOGO_POLIZA), Constants.STRING_NA, new BigDecimal(0));
					this.saveOrUpdate(asuntoAux);
				}
		
		hist.setAsuntosInc(asuntoAux);

		// EstadosInc
		hist.setEstadosInc((EstadosInc) this.getObject(EstadosInc.class, inc
				.getEstado().charAt(0)));

		// DocsAfectadosInc
		hist.setDocsAfectadosInc((DocsAfectadosInc) this
				.getObject(DocsAfectadosInc.class, inc
						.getCodDocumentoAfectado().charAt(0)));

		// Incidencias
		hist.setIncidencias((Incidencias) this.getObject(Incidencias.class,
				idIncidencia));

		return hist;
	}
	
	@SuppressWarnings("rawtypes")
	public Date getFechaParamDesde() {
		
		Calendar calendarDesde = Calendar.getInstance();
		/* MODIF TAM (25.03.2019) */
		/* Le restamos un d�a al calendar */
		calendarDesde.add(Calendar.DAY_OF_MONTH, -1);
		/* MODIF TAM (25.03.2019) - Fin */

		calendarDesde.set(Calendar.SECOND, 0);
        calendarDesde.set(Calendar.MINUTE, 0);
        calendarDesde.set(Calendar.HOUR_OF_DAY, 0);
		
		Calendar calendarHasta = Calendar.getInstance();
		/* MODIF TAM (25.03.2019) */
		/* Le restamos un d�a al calendar */
		calendarHasta.add(Calendar.DAY_OF_MONTH, -1);
		/* MODIF TAM (25.03.2019) - Fin */

		calendarHasta.set(Calendar.SECOND, 59);
        calendarHasta.set(Calendar.MINUTE, 59);
        calendarHasta.set(Calendar.HOUR_OF_DAY, 23);
		
        Date dateRetorno;
        
		session = obtenerSession();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		
		String sql= "select AGP_VALOR from O02AGPE0.TB_CONFIG_AGP where AGP_NEMO = 'FECHA_CAM_EST_DESDE'";
		List list = session.createSQLQuery(sql).list();
		
		if( list.get(0) == null || "".equals(list.get(0).toString().trim())){
			dateRetorno = calendarDesde.getTime();
			return dateRetorno;
		}
		
		Date fechaRecup;
		try {
			fechaRecup = sdf.parse(list.get(0).toString().trim());

			if (fechaRecup.equals(calendarHasta.getTime()) || fechaRecup.after(calendarHasta.getTime())) {
				dateRetorno = calendarDesde.getTime();
			} else {
				dateRetorno = sdf.parse(list.get(0).toString());
			}
		} catch (ParseException e) {
			logger.error("Excepcion : SeguimientoPolizaDao - getFechaParamDesde", e);
			dateRetorno = calendarDesde.getTime();
		}
		//Si la fecha parametrizable es nula o vacia
		
			
		return dateRetorno;
	}
	
	public void setHistoricoEstadosManager(IHistoricoEstadosManager historicoEstadosM) {
		this.iHistoricoEstadosManager = historicoEstadosM;
	}
	public IHistoricoEstadosManager getHistoricoEstadosManager() {
		return this.iHistoricoEstadosManager;
	}


	@Override
	public void createTmpBatchSeguimiento(final String nifAsegurado,
			final String nifTomador, final BigDecimal plan,
			final BigDecimal linea, final String referencia,
			final Character tipoRef, final BigDecimal entidad,
			final String oficina, final BigDecimal tipo, final String detalle,
			final String estado, final String colectivo) throws DAOException {
		TmpBatchSeguimiento tempTb = new TmpBatchSeguimiento();
		TmpBatchSeguimientoId compId = new TmpBatchSeguimientoId();
		compId.setAsegurado(nifAsegurado);
		compId.setCifTomador(nifTomador);
		compId.setPlan(plan);
		compId.setLinea(linea);
		compId.setReferencia(referencia);
		compId.setTipoReferencia(tipoRef);
		compId.setEntidad(entidad);
		compId.setOficina(oficina);
		compId.setTipo(tipo);
		compId.setDetalle(detalle);
		compId.setEstado(estado);
		compId.setColectivo(colectivo);
		tempTb.setId(compId);
		this.saveOrUpdate(tempTb);
	}
}