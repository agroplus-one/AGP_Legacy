package com.rsi.agp.batch.comisiones;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.batch.comisiones.util.ConfigBuzonInfovia;
import com.rsi.agp.batch.comisiones.util.ImportacionConnectionPool;
import es.agroseguro.recibos.reglamentoProduccionEmitida.IndividualAplicacion;
import es.agroseguro.recibos.reglamentoProduccionEmitida.Situacion;

/**
 * @author t-systems
 *
 */
public class ProcesamientoFicherosReglamento extends ProcesamientoFicherosBase {

	private static final Log logger = LogFactory.getLog(ProcesamientoFicherosReglamento.class);

	private static final String TODATE1_STR = "TO_DATE('";
	private static final String TODATE21_STR = "','YYYY-MM-DD'),";

	public ProcesamientoFicherosReglamento(ImportacionConnectionPool icp) {
		this.icp = icp;
	}

	public void procesarFicheroReglamento(File ficheroImportacion) throws SQLException {
		logger.info("ENTRANDO A PROCESAR FICHERO DE REGLAMENTO " + ficheroImportacion.getName());
		es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument fase = null;

		Connection con = null;
		Statement st = null;
		Long idFichero = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fase = (es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument) realizarValidacion(
					ficheroImportacion, fase, ConfigBuzonInfovia.FICHERO_REGLAMENTO);
			con = icp.getConnection();

			con.setAutoCommit(false);

			st = con.createStatement();

			// Se comrpueba que el fichero no exista
			String nombreFichero = ficheroImportacion.getName().toLowerCase();
			logger.info("Se comprueba que el fichero " + nombreFichero + "no esté importado");
			idFichero = existeFichero(nombreFichero);

			if (idFichero == null && fase != null) {
				// Datos de la fase
				Long idFase = null;
				idFase = existeFase(fase.getFase().getFase(), fase.getFase().getPlan());
				if (idFase == null) {
					idFase = getNewId("sq_coms_fase");
					// si no hay fase, insertamos nueva
					String faseNueva = "INSERT INTO o02agpe0.tb_coms_fase (ID,FASE,PLAN,FECHAEMISION) VALUES (" + ""
							+ idFase + "," + fase.getFase().getFase() + "," + fase.getFase().getPlan() + ",TO_DATE('"
							+ fase.getFase().getFechaEmisionRecibo() + "','YYYY-MM-DD'))";
					logger.info(faseNueva);
					st.addBatch(faseNueva);

				}
				logger.info("Fase: " + idFase);
				// insertamos una entrada de fichero nuevo
				idFichero = getNewId("sq_coms_ficheros");
				String fechaCarga = df.format(new Date());
				String ficheroNuevo = "INSERT INTO o02agpe0.tb_coms_ficheros "
						+ " (id,nombrefichero,idfase,tipofichero,contenido,fechacarga,codusuario) " + " values ("
						+ idFichero + ",'" + ficheroImportacion.getName().toLowerCase() + "'," + idFase
						+ ",'R',empty_clob(),TO_DATE('" + fechaCarga + "','YYYY-MM-DD'),'batch')";
				logger.info(ficheroNuevo);
				st.addBatch(ficheroNuevo);

				// fichero reglamento
				Long idFicheroReg = getNewId("sq_coms_ficheros_reglamento");
				String nuevoReg = "INSERT INTO o02agpe0.TB_COMS_FICHEROS_REGLAMENTO (ID,IDREGISTROFICHERO) "
						+ " values(" + idFicheroReg + "," + idFichero + ")";
				logger.info(nuevoReg);
				st.addBatch(nuevoReg);

				// aplicaciones
				IndividualAplicacion[] individualAplicacion = fase.getFase().getIndividualAplicacionArray();
				for (IndividualAplicacion individualApli : individualAplicacion) {
					String referencia = "";
					Integer dc = null;
					String codigoInternno = "";
					Long lineaSeguroid = obtenerLineaseguroid(individualApli.getLinea(), fase.getFase().getPlan());
					if (individualApli.getColectivo() != null) {
						referencia = individualApli.getColectivo().getReferencia();
						dc = individualApli.getColectivo().getDigitoControl();
						codigoInternno = individualApli.getColectivo().getCodigoInterno();
					}

					Long idAplicacion = getNewId("sq_coms_fics_reglamento_apli");

					String fechaVigor = df.format(individualApli.getDatosReglamento().getFechaEntradaVigor().getTime());
					String fechaPago = df.format(individualApli.getDatosReglamento().getFechaPago().getTime());
					String fechaRecep = df.format(individualApli.getDatosReglamento().getFechaRecepcion().getTime());

					String nuevoapli = "INSERT INTO o02agpe0.TB_COMS_FICS_REGLAMENTO_APLI "
							+ " (ID, IDREGLAMENTO, CODIGOINTERNO, GASTOSCOMISIONES, DC, GASTOSEXTERNOSENTIDAD, "
							+ "GRUPONEGOCIO, LINEASEGUROID, REFERENCIA, TIPORECIBO, TIPOREFERENCIA,"
							+ "COLECTIVOREFERENCIA, COLECTIVODC, COLECTIVOCODIGOINTERNO,"
							+ "CODFECHACOMPUTODIAS, FECHAENTRADAVIGOR, FECHAPAGO, FECHARECEPCION, LIMITE) " + "values("
							+ idAplicacion + "," + idFicheroReg + ",'" + nullToString(individualApli.getCodigoInterno())
							+ "'," + individualApli.getComisiones() + "," + individualApli.getDigitoControl() + "," + ""
							+ individualApli.getGastosExternosEntidad() + ","
							+ individualApli.getGrupoNegocio().intValue() + "," + "" + lineaSeguroid + ",'"
							+ nullToString(individualApli.getReferencia()) + "'," + ""
							+ individualApli.getTipoRecibo().intValue() + ","
							+ individualApli.getTipoReferencia().intValue() + ",'" + nullToString(referencia) + "',"
							+ "" + dc + ",'" + nullToString(codigoInternno) + "'," + "'"
							+ nullToString(individualApli.getDatosReglamento().getCodigoFechaComputoDias()) + "',"
							+ TODATE1_STR + fechaVigor + TODATE21_STR + TODATE1_STR + fechaPago + TODATE21_STR
							+ TODATE1_STR + fechaRecep + TODATE21_STR + ""
							+ individualApli.getDatosReglamento().getLimite() + ")";
					logger.info(nuevoapli);
					st.addBatch(nuevoapli);

					// Situaciones
					Situacion[] situacionArray = individualApli.getSituaciones().getSituacionArray();

					for (Situacion situacion : situacionArray) {
						String medReg1 = "";
						String codMedidad1 = "";
						BigDecimal porc1 = null;
						BigDecimal impoApli1 = null;
						BigDecimal imporSin1 = null;
						Character codSituacion = situacion.getCodigo().toString().charAt(0);

						if (situacion.getDatosTramitacion() != null) {
							medReg1 = situacion.getDatosTramitacion().getMedidaReglamento();
							porc1 = situacion.getDatosTramitacion().getPorcentaje();
							codMedidad1 = situacion.getDatosTramitacion().getCodigoMedida();
							impoApli1 = situacion.getDatosTramitacion().getImporteAplicado();
							imporSin1 = situacion.getDatosTramitacion().getImporteSinReduccion();
						}

						String medReg2 = "";
						String codMedidad2 = "";
						BigDecimal porc2 = null;
						BigDecimal impoApli2 = null;
						BigDecimal imporSin2 = null;
						if (situacion.getDatosCalidad() != null) {
							medReg2 = situacion.getDatosCalidad().getMedidaReglamento();
							porc2 = situacion.getDatosCalidad().getPorcentaje();
							codMedidad2 = situacion.getDatosCalidad().getCodigoMedida();
							impoApli2 = situacion.getDatosCalidad().getImporteAplicado();
							imporSin2 = situacion.getDatosCalidad().getImporteSinReduccion();
						}
						Long idSituacion = getNewId("sq_coms_fics_reglamento_apli");
						String nuevaSit = "INSERT INTO o02agpe0.TB_COMS_FICS_REGLTO_APLI_SIT "
								+ " (ID, IDREGLAMENTOAPLI,CODIGOSITUACION,DTTIPOMEDIDAREGLAMENTO, DTPCTOREGLAMENTO, DTCODIGOMEDIDA, "
								+ "DTIMPORTEAPLICADO, DTIMPORTESINREDUCCION, DCTIPOMEDIDAREGLAMENTO, DCPCTOREGLAMENTO, "
								+ "DCCODIGOMEDIDA, DCIMPORTEAPLICADO, DCIMPORTESINREDUCCION )" + "VALUES ("
								+ idSituacion + "," + idAplicacion + ",'" + nullToString(codSituacion) + "','"
								+ nullToString(medReg1) + "'," + porc1 + "," + "'" + nullToString(codMedidad1) + "',"
								+ impoApli1 + "," + imporSin1 + ",'" + nullToString(medReg2) + "'," + porc2 + "," + "'"
								+ nullToString(codMedidad2) + "'," + impoApli2 + "," + imporSin2 + ")";
						logger.info(nuevaSit);
						st.addBatch(nuevaSit);

					}
				}
				int[] queries = st.executeBatch();
				logger.info(queries.length
						+ " insert generados.----------------------------------------------------------");
				con.commit();

				insertarTextoFichero(ficheroImportacion, idFichero, con);

				// ya no validamos. cargamos el fichero y al verificar se valida
				con.commit();

			} else {
				logger.info("El fichero " + nombreFichero + " esta duplicado");
			}
		} catch (Exception e) {
			if (con != null)
				con.rollback();
			logger.error("Error durante el procesamiento del fichero de reglamento " + ficheroImportacion.getName(), e);
			throw new SQLException(e.getMessage());
		} finally {
			if (st != null)
				st.close();
			if (con != null)
				con.close();			
		}
	}
}
