package com.rsi.agp.batch.comisiones;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.batch.comisiones.util.ConfigBuzonInfovia;
import com.rsi.agp.batch.comisiones.util.ImportacionConnectionPool;
import es.agroseguro.recibos.gastos.Recibo;

/**
 * @author t-systems
 * 
 */
public class ProcesamientoFicherosImpagados extends ProcesamientoFicherosBase {

	private static final Log logger = LogFactory.getLog(ProcesamientoFicherosImpagados.class);

	public ProcesamientoFicherosImpagados(ImportacionConnectionPool icp) {
		this.icp = icp;
	}

	public void procesarFicheroImpagado(File ficheroImportacion) throws SQLException {
		logger.info("ENTRANDO A PROCESAR FICHERO DE IMPAGADOS " + ficheroImportacion.getName());
		es.agroseguro.recibos.gastos.FaseDocument fase = null;

		Connection con = null;
		Statement st = null;
		Long idFichero = null;
		try {
			con = icp.getConnection();
			fase = (es.agroseguro.recibos.gastos.FaseDocument) realizarValidacion(ficheroImportacion, fase,
					ConfigBuzonInfovia.FICHERO_IMPAGADOS);

			con.setAutoCommit(false);
			st = con.createStatement();

			// Se comrpueba que el fichero no exista
			String nombreFichero = ficheroImportacion.getName().toLowerCase();
			logger.info("Se comprueba que el fichero " + nombreFichero + "no esté importado");
			idFichero = existeFichero(nombreFichero);

			if (idFichero == null && fase != null) {

				// Datos de la fase
				Long idFase = null;
				idFase = existeFase(fase.getFase().getFase(), /* fase.getFase().getPlan() */0);
				if (idFase == null) {
					idFase = getNewId("sq_coms_fase");
					// si no hay fase, insertamos nueva
					String nuevafse = "INSERT INTO o02agpe0.tb_coms_fase (ID,FASE,PLAN,FECHAEMISION) VALUES (" + ""
							+ idFase + "," + fase.getFase().getFase() + ","
							// + fase.getFase().getPlan()
							+ ",TO_DATE('" + fase.getFase().getFechaFaseCobros().getTime() + "','YYYY-MM-DD'))";
					logger.info(nuevafse);
					st.addBatch(nuevafse);
				}
				logger.info("Fase: " + idFase);
				// insertamos una entrada de fichero nuevo
				idFichero = getNewId("sq_coms_ficheros");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String fechaCarga = df.format(new Date());
				String nuevofic = "INSERT INTO o02agpe0.tb_coms_ficheros "
						+ " (id,nombrefichero,idfase,tipofichero,contenido,fechacarga,codusuario) " + " values ("
						+ idFichero + ",'" + ficheroImportacion.getName().toLowerCase() + "'," + idFase
						+ ",'I',empty_clob(),TO_DATE('" + fechaCarga + "','YYYY-MM-DD'),'batch')";

				logger.info(nuevofic);
				st.addBatch(nuevofic);

				// recibos
				Recibo[] arrayRecibos = fase.getFase().getReciboArray();

				for (Recibo recibo : arrayRecibos) {
					String indiCodInterno = null;
					Integer indiDigControl = null;
					String indiLocalidad = null;
					String indiRef = null;
					if (recibo.getIndividual() != null) {
						indiCodInterno = recibo.getIndividual().getCodigoInterno();
						indiDigControl = Integer.valueOf(recibo.getIndividual().getDigitoControl());
						indiLocalidad = recibo.getIndividual().getLocalidad();
						indiRef = recibo.getIndividual().getReferencia();
					}
					String coldiCodInterno = null;
					Integer coldiDigControl = null;
					String colLocalidad = null;
					String colRef = null;
					if (recibo.getColectivo() != null) {
						coldiCodInterno = recibo.getColectivo().getCodigoInterno();
						coldiDigControl = recibo.getColectivo().getDigitoControl();
						colLocalidad = recibo.getColectivo().getLocalidad();
						colRef = recibo.getColectivo().getReferencia();
					}

					Long idRecibo = getNewId("sq_coms_recibos_comisiones_imp");
					String nuevoImp = " INSERT INTO o02agpe0.TB_COMS_RECIBOS_COMISIONES_IMP"
							+ "  (ID, IDREGISTROFICHERO, EJERCICIOPAGO, GRUPONEGOCIO, LINEASEGUROID, RECIBO, "
							+ " TOMADORAPELLIDO1, TOMADORAPELLIDO2, TOMADORNOMBRE, INDIVIDUALCODINTERNO, INDIVIDUALDC, "
							+ " INDIVIDUALLOCALIDAD, INDIVIDUALREFERENCIA,COLECTIVOCODINTERNO, COLECTIVODC, COLECTIVOLOCALIDAD, COLECTIVOREF, "
							+ " PARIGASTOSCOMISIONES, PARIGASTOSENTIDAD, "
							+ " PARIIMPORTECOBROACTUAL, PARITOTALGASTOS, CAGASTOSCOMISIONES, CAGASTOSENTIDAD, "
							+ " CAIMPORTESALDOPENDIENTE, CATOTALGASTOS, PAGASTOSCOMISIONES,  PAGASTOSENTIDAD, PATOTALGASTOS)  "
							+ " values (" + idRecibo + "," + idFichero + ",NULL," + recibo.getGrupoNegocio().intValue()
							+ "," + recibo.getLinea() + "," + recibo.getRecibo() + ","
							+ nullToString(recibo.getNombreApellidos().getApellido1()) + "" + ","
							+ nullToString(recibo.getNombreApellidos().getApellido2()) + ","
							+ nullToString(recibo.getNombreApellidos().getNombre()) + ",'"
							+ nullToString(indiCodInterno) + "'," + indiDigControl + ",'" + nullToString(indiLocalidad)
							+ "','" + nullToString(indiRef) + "','" + nullToString(coldiCodInterno) + "'" + ","
							+ coldiDigControl + ",'" + nullToString(colLocalidad) + "','" + nullToString(colRef) + "',"
							+ recibo.getGastosExternos().getPendienteAbonoReciboImpagado().getGastosComisiones() + ","
							+ recibo.getGastosExternos().getPendienteAbonoReciboImpagado().getGastosEntidad() + ",NULL,"
							+ "" + recibo.getGastosExternos().getPendienteAbonoReciboImpagado().getTotalGastos() + ","
							+ recibo.getGastosExternos().getCobroActual().getGastosComisiones() + ","
							+ recibo.getGastosExternos().getCobroActual().getGastosEntidad() + ",NULL,"
							+ recibo.getGastosExternos().getCobroActual().getTotalGastos() + ","
							+ recibo.getGastosExternos().getPendienteAbono().getGastosComisiones() + ","
							+ recibo.getGastosExternos().getPendienteAbono().getGastosEntidad() + ","
							+ recibo.getGastosExternos().getPendienteAbono().getTotalGastos() + ")";
					logger.info(nuevoImp);
					st.addBatch(nuevoImp);
				}
				int[] queries = st.executeBatch();
				logger.info(queries.length
						+ " insert generados.----------------------------------------------------------");
				con.commit();
				insertarTextoFichero(ficheroImportacion, idFichero, con);
				con.commit();
			} else {
				logger.info("El fichero " + nombreFichero + " esta duplicado");
			}
		} catch (Exception e) {
			if (con != null)
				con.rollback();
			logger.error("Error durante el procesamiento del fichero de impagados" + ficheroImportacion.getName(), e);

			throw new SQLException(e.getMessage());
		} finally {
			if (st != null)
				st.close();
			if (con != null)
				con.close();
		}

	}

}
