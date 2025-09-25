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

import es.agroseguro.recibos.comisiones.Aplicacion;
import es.agroseguro.recibos.comisiones.CondicionesParticulares;
import es.agroseguro.recibos.comisiones.Recibo;

/**
 * @author t-systems
 * 
 * 
 */
public class ProcesamientoFicherosComisiones extends ProcesamientoFicherosBase {

	private static final Log logger = LogFactory.getLog(ProcesamientoFicherosComisiones.class);

	private static final String VALUES_STR = " VALUES (";

	public ProcesamientoFicherosComisiones(ImportacionConnectionPool icp) {
		this.icp = icp;
	}

	public void procesarFicheroComisiones(File ficheroImportacion) throws SQLException {
		es.agroseguro.recibos.comisiones.FaseDocument fase = null;

		Connection con = null;
		Statement st = null;
		Long idFichero = null;
		try {
			fase = (es.agroseguro.recibos.comisiones.FaseDocument) realizarValidacion(ficheroImportacion, fase,
					ConfigBuzonInfovia.FICHERO_COMISIONES);
			con = icp.getConnection();

			con.setAutoCommit(false);
			st = con.createStatement();
			// Se comrpueba que el fichero no exista
			String nombreFichero = ficheroImportacion.getName().toLowerCase();
			idFichero = existeFichero(nombreFichero);
			logger.info("idfichero: " + idFichero);
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
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String fechaCarga = df.format(new Date());
				idFichero = getNewId("sq_coms_ficheros");
				String nuevoFichero = "INSERT INTO o02agpe0.tb_coms_ficheros "
						+ " (id,nombrefichero,idfase,tipofichero,contenido,fechacarga,codusuario) " + VALUES_STR
						+ idFichero + ",'" + ficheroImportacion.getName().toLowerCase() + "'," + idFase
						+ ",'C',empty_clob(),TO_DATE('" + fechaCarga + "','YYYY-MM-DD'),'batch')";
				logger.info(nuevoFichero);
				st.addBatch(nuevoFichero);

				// Recuperamos los recibos incluidos en el fichero
				// cada uno es una entrada en tb_coms_recibos_comisiones
				Recibo[] recibos = fase.getFase().getReciboArray();
				Recibo reciboTemp = null;
				for (int x = 0; x < recibos.length; x++) {
					reciboTemp = recibos[x];
					Long idRecibo = getNewId("SQ_COMS_RECIBOS_COMISIONES");
					Long lineaSeguroid = obtenerLineaseguroid(reciboTemp.getLinea(), fase.getFase().getPlan());
					int codRecibo = reciboTemp.getRecibo();
					String localidad = reciboTemp.getLocalidad();
					int grupoNegocio = reciboTemp.getGrupoNegocio().intValue();

					String razonSocial = null;
					String tomadorNombre = null;
					String tomadorApellido1 = null;
					String tomadorApellido2 = null;
					if (reciboTemp.getNombreApellidos() != null) {
						tomadorNombre = reciboTemp.getNombreApellidos().getNombre();
						tomadorApellido1 = reciboTemp.getNombreApellidos().getApellido1();
						tomadorApellido2 = reciboTemp.getNombreApellidos().getApellido2();
					}
					if (reciboTemp.getRazonSocial() != null) {
						razonSocial = reciboTemp.getRazonSocial().getRazonSocial();
					}

					// datos nuevos. opcional
					BigDecimal daComs = null;
					BigDecimal daGee = null;
					BigDecimal daPbc = null;
					BigDecimal daTotal = null;
					if (reciboTemp.getDatosNuevos() != null) {
						daComs = reciboTemp.getDatosNuevos().getComisiones();
						daGee = reciboTemp.getDatosNuevos().getGastosExternosEntidad();
						daPbc = reciboTemp.getDatosNuevos().getPrimaBaseCalculo();
						daTotal = reciboTemp.getDatosNuevos().getTotal();
					}

					// datos regularizados. opcional
					BigDecimal drComs = null;
					BigDecimal drGee = null;
					BigDecimal drPbc = null;
					BigDecimal drTotal = null;
					if (reciboTemp.getDatosRegularizados() != null) {
						drComs = reciboTemp.getDatosRegularizados().getComisiones();
						drGee = reciboTemp.getDatosRegularizados().getGastosExternosEntidad();
						drPbc = reciboTemp.getDatosRegularizados().getPrimaBaseCalculo();
						drTotal = reciboTemp.getDatosRegularizados().getTotal();
					}

					// datos totales
					BigDecimal dtPBC = reciboTemp.getDatosTotales().getPrimaBaseCalculo();
					BigDecimal dtGee = reciboTemp.getDatosTotales().getGastosExternosEntidad();
					BigDecimal dtComs = reciboTemp.getDatosTotales().getComisiones();
					BigDecimal dtTotal = reciboTemp.getDatosTotales().getTotal();
					BigDecimal dtExtpagados = reciboTemp.getDatosTotales().getGastosPagados();
					BigDecimal dtExtpendientes = reciboTemp.getDatosTotales().getGastosPendientes();

					// colectivo
					String colectivoRef = null;
					String colectivoCodinterno = null;
					Integer colectivoDc = null;
					if (reciboTemp.getColectivo() != null) {
						colectivoDc = reciboTemp.getColectivo().getDigitoControl();
						colectivoRef = reciboTemp.getColectivo().getReferenciaColectivo();
						colectivoCodinterno = reciboTemp.getColectivo().getCodigoInterno();
					}
					// individual
					Integer anulRefun = null;
					String codInterno = null;
					Integer digControl = null;
					BigDecimal pgc = null;
					BigDecimal pge = null;
					String ref = null;
					Integer tipRef = null;
					if (reciboTemp.getIndividual() != null) {
						anulRefun = reciboTemp.getIndividual().getAnuladaRefundida().intValue();
						codInterno = reciboTemp.getIndividual().getCodigoInterno();
						digControl = reciboTemp.getIndividual().getDigitoControl();
						pgc = reciboTemp.getIndividual().getPorcentajeGtosComisAplic();
						ref = reciboTemp.getIndividual().getReferencia();
						tipRef = reciboTemp.getIndividual().getTipoReferencia().intValue();
						pge = reciboTemp.getIndividual().getPorcentajeGtosEntAplic();
					}
					// insertamos el registro del recibo
					String nuevoRegistro = "INSERT INTO o02agpe0.TB_COMS_RECIBOS_COMISIONES "
							+ "(ID, IDREGISTROFICHERO, LINEASEGUROID, CODRECIBO, LOCALIDAD, GRUPONEGOCIO, "
							+ " TOMADORNOMBRE, TOMADORAPELLIDO1, TOMADORAPELLIDO2, TOMADORRAZONSOCIAL, "
							+ " COLECTIVOREF, COLECTIVODC, COLECTIVOCODINTERNO, "
							+ " INDIVIDUALREFPOLIZA, INDIVIDUALDCPOLIZA, INDIVIDUALTIPOREF, INDIVIDUALCODINTERNO, "
							+ " INDIVIDUALANULADAREFUNDIDA, INDIVIDUALMARCACONDICIONES, INDIVIDUALPCTOGTOSENT, INDIVIDUALPCTOGTOSCOMIS, "
							+ " DATOSNUEVOSPRIMABASECALCULO, DATOSNUEVOSGTOSEXTENTIDAD, DATOSNUEVOSCOMISIONES, DATOSNUEVOSTOTAL, "
							+ " DATOSREGPRIMABASECALCULO, DATOSREGGTOSEXTENTIDAD, DATOSREGCOMISIONES, DATOSREGTOTAL, "
							+ " DATOSTOTPRIMABASECALCULO, DATOSTOTGTOSEXTENTIDAD, DATOSTOTCOMISIONES, DATOSTOTTOTAL, "
							+ " DATOSTOTEXTPAGADOS, DATOSTOTEXTPENDIENTES) " + VALUES_STR + idRecibo + "," + idFichero
							+ "," + lineaSeguroid + "," + codRecibo + ",'" + remplazarComillado(localidad) + "',"
							+ grupoNegocio + "," + "'" + nullToString(tomadorNombre) + "','"
							+ nullToString(tomadorApellido1) + "','" + nullToString(tomadorApellido2) + "','"
							+ nullToString(razonSocial) + "'," + "'" + nullToString(colectivoRef) + "'," + colectivoDc
							+ ",'" + nullToString(colectivoCodinterno) + "'," + "'" + nullToString(ref) + "',"
							+ digControl + "," + tipRef + ",'" + nullToString(codInterno) + "'," + anulRefun + ","
							+ null + "," + pge + "," + pgc + "," + "" + daPbc + "," + daGee + "," + daComs + ","
							+ daTotal + "," + "" + drPbc + "," + drGee + "," + drComs + "," + drTotal + "," + "" + dtPBC
							+ "," + dtGee + "," + dtComs + "," + dtTotal + "," + dtExtpagados + "," + dtExtpendientes
							+ ")";
					logger.info(nuevoRegistro);
					st.addBatch(nuevoRegistro);

					// marcas a tb_coms_recibos_coms_marcas
					if (reciboTemp.getIndividual() != null) {
						CondicionesParticulares[] cps = reciboTemp.getIndividual().getCondicionesParticularesArray();
						CondicionesParticulares tempCp = null;
						for (int y = 0; y < cps.length; y++) {
							tempCp = cps[x];
							Integer marca = Integer.valueOf(tempCp.getMarcaCondicionesParticulares());
							Long idMarca = getNewId("sq_coms_recibos_coms_marcas");
							String nuevaMarca = "INSERT INTO o02agpe0.tb_coms_recibos_coms_marcas "
									+ " (ID, IDRECIBOCOMISION, MARCACONDICIONESPARTCOMS) " + VALUES_STR + idMarca + ","
									+ idRecibo + "," + marca + ") ";
							logger.info(nuevaMarca);
							st.addBatch(nuevaMarca);

						}
					}

					// aplicaciones
					// entrada en tb_coms_recibos_comisiones_apl
					Aplicacion[] apps = reciboTemp.getAplicaciones().getAplicacionArray();
					Long idAplicacion = null;
					for (Aplicacion aplicacion : apps) {
						idAplicacion = getNewId("sq_coms_recibos_comisiones_apl");
						Integer apliAnulRefun = aplicacion.getAnuladaRefundida().intValue();
						String apliRef = aplicacion.getReferencia();
						Integer apliDC = aplicacion.getDigitoControl();
						Integer apliTipoRef = aplicacion.getTipoReferencia().intValue();
						String apliCodInt = aplicacion.getCodigoInterno();

						String aplinombre = null;
						String apliapellido1 = null;
						String apliapellido2 = null;
						String aplirazonSocial = null;
						if (aplicacion.getNombreApellidos() != null) {
							aplinombre = aplicacion.getNombreApellidos().getNombre();
							apliapellido1 = aplicacion.getNombreApellidos().getApellido1();
							apliapellido1 = aplicacion.getNombreApellidos().getApellido2();
						}
						if (aplicacion.getRazonSocial() != null) {
							aplirazonSocial = aplicacion.getRazonSocial().getRazonSocial();
						}

						// datos nuevos. opcional
						BigDecimal apdaComs = null;
						BigDecimal apdaGee = null;
						BigDecimal apdaPbc = null;
						BigDecimal apdaTotal = null;
						if (aplicacion.getDatosNuevos() != null) {
							apdaComs = aplicacion.getDatosNuevos().getComisiones();
							apdaGee = aplicacion.getDatosNuevos().getGastosExternosEntidad();
							apdaPbc = aplicacion.getDatosNuevos().getPrimaBaseCalculo();
							apdaTotal = aplicacion.getDatosNuevos().getTotal();
						}

						// datos regularizados. opcional
						BigDecimal apdrComs = null;
						BigDecimal apdrGee = null;
						BigDecimal apdrPbc = null;
						BigDecimal apdrTotal = null;
						if (aplicacion.getDatosRegularizados() != null) {
							apdrComs = aplicacion.getDatosRegularizados().getComisiones();
							apdrGee = aplicacion.getDatosRegularizados().getGastosExternosEntidad();
							apdrPbc = aplicacion.getDatosRegularizados().getPrimaBaseCalculo();
							apdrTotal = aplicacion.getDatosRegularizados().getTotal();
						}

						// datos totales
						BigDecimal apdtPBC = aplicacion.getDatosTotales().getPrimaBaseCalculo();
						BigDecimal apdtGee = aplicacion.getDatosTotales().getGastosExternosEntidad();
						BigDecimal apdtComs = aplicacion.getDatosTotales().getComisiones();
						BigDecimal apdtTotal = aplicacion.getDatosTotales().getTotal();

						BigDecimal apdtExtpagados = aplicacion.getDatosTotales().getPorcentajeGtosEntAplic();
						BigDecimal apdtExtpendientes = aplicacion.getDatosTotales().getPorcentajeGtosComisAplic();

						String nuevaApli = "INSERT INTO o02agpe0.tb_coms_recibos_comisiones_apl "
								+ "(IDRECIBOCOMISION,ID,REFERENCIA,DC,TIPOREFERENCIA,CODINTERNO,ANULADAREFUNDIDA,"
								+ " NOMBRE,APELLIDO1,APELLIDO2,RAZONSOCIAL,DATOSNUEVOSPRIMABASECALCULO,"
								+ " DATOSNUEVOSGTOSEXTENTIDAD,DATOSNUEVOSCOMISIONES,DATOSNUEVOSTOTAL,"
								+ " DATOSREGPRIMABASECALCULO,DATOSREGGTOSEXTENTIDAD,DATOSREGCOMISIONES,DATOSREGTOTAL,"
								+ " DATOSTOTPRIMABASECALCULO,DATOSTOTGTOEXTENTIDAD,DATOSTOTCOMISIONES,"
								+ " DATOSTOTTOTAL,DATOSTOTEXTPAGADOS,DATOSTOTEXTPENDIENTES) " + VALUES_STR + idRecibo
								+ "," + idAplicacion + ",'" + nullToString(apliRef) + "'," + apliDC + "," + apliTipoRef
								+ ",'" + nullToString(apliCodInt) + "'," + apliAnulRefun + "" + ",'"
								+ nullToString(aplinombre) + "','" + nullToString(apliapellido1) + "','"
								+ nullToString(apliapellido2) + "','" + nullToString(aplirazonSocial) + "'," + apdaPbc
								+ "," + apdaGee + "," + apdaComs + "," + apdaTotal + "," + apdrPbc + "" + "," + apdrGee
								+ "," + apdrComs + "," + apdrTotal + "," + apdtPBC + "," + apdtGee + "," + apdtComs + ""
								+ "," + apdtTotal + "," + apdtExtpagados + "," + apdtExtpendientes + ")";
						logger.info(nuevaApli);
						st.addBatch(nuevaApli);

						// marcas a tb_coms_recs_coms_apl_marcas
						CondicionesParticulares[] apcps = aplicacion.getCondicionesParticularesArray();
						for (CondicionesParticulares condicionesParticulares : apcps) {
							Integer marca = Integer.valueOf(condicionesParticulares.getMarcaCondicionesParticulares());
							Long idMarca = getNewId("sq_coms_recs_coms_apl_marcas");
							String nuevaMar = "INSERT INTO o02agpe0.tb_coms_recs_coms_apl_marcas "
									+ " (ID, IDRECIBOCOMISIONAPL, MARCACONDICIONESPARTSCOMS ) " + VALUES_STR + idMarca
									+ "," + idAplicacion + "," + marca + ")";
							logger.info(nuevaMar);
							st.addBatch(nuevaMar);

						}
					}

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
			logger.error("Error durante el procesamiento del fichero de comisiones " + ficheroImportacion.getName(), e);
			throw new SQLException(e.getMessage());
		} finally {
			if (st != null)
				st.close();
			if (con != null)
				con.close();
		}

	}

}
