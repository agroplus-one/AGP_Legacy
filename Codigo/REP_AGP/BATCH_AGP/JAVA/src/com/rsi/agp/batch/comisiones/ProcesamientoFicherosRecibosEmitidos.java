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

import es.agroseguro.recibos.emitidos.DetalleCompensac;
import es.agroseguro.recibos.emitidos.IndividualAplicacion;
import es.agroseguro.recibos.emitidos.Recibo;
import es.agroseguro.recibos.emitidos.SubvencionCCAA;

/**
 * @author t-systems
 *
 */
public class ProcesamientoFicherosRecibosEmitidos extends ProcesamientoFicherosBase {

	private static final Log logger = LogFactory.getLog(ProcesamientoFicherosRecibosEmitidos.class);

	private static final String VALUES_STR = " VALUES (";

	public ProcesamientoFicherosRecibosEmitidos(ImportacionConnectionPool icp) {
		this.icp = icp;
	}

	public void procesarFicheroReciboEmitido(File ficheroImportacion) throws SQLException {
		logger.info("ENTRANDO A PROCESAR FICHERO DE RECIBOS EMITIDOS  " + ficheroImportacion.getName());
		es.agroseguro.recibos.emitidos.FaseDocument fase = null;

		Connection con = null;
		Statement st = null;
		Long idFichero = null;
		try {
			fase = (es.agroseguro.recibos.emitidos.FaseDocument) realizarValidacion(ficheroImportacion, fase,
					ConfigBuzonInfovia.FICHERO_EMITIDOS);
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
					String nuevaFase = "INSERT INTO o02agpe0.tb_coms_fase (ID,FASE,PLAN,FECHAEMISION) VALUES (" + ""
							+ idFase + "," + fase.getFase().getFase() + "," + fase.getFase().getPlan() + ",TO_DATE('"
							+ fase.getFase().getFechaEmisionRecibo() + "','YYYY-MM-DD'))";
					logger.info(nuevaFase);
					st.addBatch(nuevaFase);
				}
				logger.info("Fase: " + idFase);
				// insertamos una entrada de fichero nuevo
				idFichero = getNewId("sq_coms_ficheros");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String fechaCarga = df.format(new Date());
				String nuevfich = "INSERT INTO o02agpe0.tb_coms_ficheros "
						+ " (id,nombrefichero,idfase,tipofichero,contenido,fechacarga,fechaaceptacion,codusuario) "
						+ VALUES_STR + idFichero + ",'" + ficheroImportacion.getName().toLowerCase() + "'," + idFase
						+ ",'G',empty_clob(),TO_DATE('" + fechaCarga + "','YYYY-MM-DD')," + "TO_DATE('" + fechaCarga
						+ "','YYYY-MM-DD'),'batch')";
				logger.info(nuevfich);
				st.addBatch(nuevfich);

				// recibos
				Recibo[] arrayRecibos = fase.getFase().getReciboArray();

				for (Recibo recibo : arrayRecibos) {

					Long idRecibo = getNewId("sq_coms_recibos_emitidos");
					Long lineaSeguroid = obtenerLineaseguroid(recibo.getLinea(), fase.getFase().getPlan());
					Integer rec = recibo.getRecibo();
					String codigo = recibo.getCodigo();
					Integer deudor = recibo.getDeudor().intValue();
					Integer coldc = null;
					String colref = null;
					if (recibo.getColectivo() != null) {
						coldc = recibo.getColectivo().getDigitoControl();
						colref = recibo.getColectivo().getReferencia();
					}
					Integer indidc = null;
					String indiref = null;
					if (recibo.getIndividual() != null) {
						indidc = recibo.getIndividual().getDigitoControl();
						indiref = recibo.getIndividual().getReferencia();
					}
					String nombre = null;
					String ape1 = null;
					String ape2 = null;
					if (recibo.getNombreApellidos() != null) {
						nombre = recibo.getNombreApellidos().getNombre();
						ape1 = recibo.getNombreApellidos().getApellido1();
						ape2 = recibo.getNombreApellidos().getApellido2();
					}
					String razonSocial = null;
					if (recibo.getRazonSocial() != null) {
						razonSocial = recibo.getRazonSocial().getRazonSocial();
					}
					String via = recibo.getDireccion().getVia();
					String numero = recibo.getDireccion().getNumero();
					String piso = recibo.getDireccion().getPiso();
					String bloque = recibo.getDireccion().getBloque();
					String escalera = recibo.getDireccion().getEscalera();
					Integer provincia = recibo.getDireccion().getProvincia();
					String localidad = remplazarComillado(recibo.getDireccion().getLocalidad());
					String codPostal = recibo.getDireccion().getCp();
					// datos economicos
					BigDecimal cst = recibo.getDatosEconomicos().getCompensacionSaldoTomador();
					BigDecimal cri = recibo.getDatosEconomicos().getCompensacionRecibosImpagados();
					BigDecimal liquido = recibo.getDatosEconomicos().getLiquido();
					BigDecimal primaComercial = recibo.getDatosEconomicos().getPrimaComercial();
					BigDecimal bonSistProt = recibo.getDatosEconomicos().getBonificacionSistProteccion();
					BigDecimal bonificacion = recibo.getDatosEconomicos().getBonificacion();
					BigDecimal recargo = recibo.getDatosEconomicos().getRecargo();
					BigDecimal descColectivo = recibo.getDatosEconomicos().getDescuentoColectivo();
					BigDecimal descVentanilla = recibo.getDatosEconomicos().getDescuentoVentanilla();
					BigDecimal primaNeta = recibo.getDatosEconomicos().getPrimaNeta();
					BigDecimal consorcio = recibo.getDatosEconomicos().getConsorcio();
					BigDecimal clea = recibo.getDatosEconomicos().getClea();
					BigDecimal costeNeto = recibo.getDatosEconomicos().getCosteNeto();
					BigDecimal subEnesa = recibo.getDatosEconomicos().getSubvencionEnesa();
					BigDecimal costeTomador = recibo.getDatosEconomicos().getCosteTomador();
					BigDecimal pagos = recibo.getDatosEconomicos().getPagos();

					String nuevorec = "INSERT INTO o02agpe0.tb_coms_recibos_emitidos "
							+ " (ID, IDREGISTROFICHERO,LINEASEGUROID, RECIBO, CODIGO, DEUDOR, COLECTIVOREF,"
							+ " COLECTIVODC, INDIVIDUALREFERENCIA, INDIVIDUALDC, NOMBRE, APELLIDO1, APELLIDO2, "
							+ " RAZONSOCIAL, VIANOMBRE, VIANUMERO, PISO, BLOQUE, ESCALERA, PROVINCIA, LOCALIDAD,"
							+ " CODIGOPOSTAL, COMPSALDOTOMADOR, COMPRECIBOSIMPAGADOS, LIQUIDO, PRIMACOMERCIAL,"
							+ " BONSISTPROTECCION, BONIFICACION, RECARGO, DESCCOLECTIVO, DESCVENTANILLA, PRIMANETA,"
							+ " CONSORCIO, CLEA, COSTENETO, SUBENESA, COSTETOMADOR, PAGOS)" + VALUES_STR + idRecibo
							+ "," + idFichero + "," + lineaSeguroid + "," + rec + ",'" + nullToString(codigo) + "','"
							+ deudor + "'," + "'" + nullToString(colref) + "'," + coldc + ",'" + nullToString(indiref)
							+ "'," + indidc + ",'" + nullToString(nombre) + "','" + nullToString(ape1) + "','"
							+ nullToString(ape2) + "','" + "" + nullToString(razonSocial) + "','" + nullToString(via)
							+ "','" + nullToString(numero) + "','" + nullToString(piso) + "','" + nullToString(bloque)
							+ "','" + nullToString(escalera) + "'," + provincia + "," + "'" + nullToString(localidad)
							+ "','" + nullToString(codPostal) + "'," + cst + "," + cri + "," + liquido + ","
							+ primaComercial + "," + bonSistProt + "," + "" + bonificacion + "," + recargo + ","
							+ descColectivo + "," + descVentanilla + "," + primaNeta + "," + consorcio + "," + clea
							+ "," + "" + costeNeto + "," + subEnesa + "," + costeTomador + "," + pagos + ")";
					logger.info(nuevorec);
					st.addBatch(nuevorec);

					// subvenciones CCAA
					SubvencionCCAA[] subs = recibo.getSubvencionCCAAArray();
					for (SubvencionCCAA subvencionCCAA : subs) {
						Long idCCAA = getNewId("sq_coms_recs_emitidos_ccaa");
						String nuevasub = "INSERT INTO o02agpe0.tb_coms_recs_emitidos_ccaa "
								+ "(ID, IDRECIBOEMITIDO,CODIGO,SUBVCOMUNIDADES ) " + VALUES_STR + idCCAA + ","
								+ idRecibo + ",'" + nullToString(subvencionCCAA.getCodigo()) + "',"
								+ subvencionCCAA.getSubvencionComunidades() + ")";
						logger.info(nuevasub);
						st.addBatch(nuevasub);
					}

					// detalle compensacion
					DetalleCompensac[] detallesCompensac = recibo.getDetalleCompensacArray();
					for (DetalleCompensac detalle : detallesCompensac) {
						Long idDetalle = getNewId("sq_coms_recs_emitidos_det_comp");
						String nuevoDet = "INSERT INTO o02agpe0.tb_coms_recs_emitidos_det_comp"
								+ " (ID, IDRECIBOEMITIDO, PLAN, LINEA, RECIBO, COBRO) " + "VALUES (" + idDetalle + ", "
								+ idRecibo + ", " + detalle.getPlan() + ", " + detalle.getLinea() + ", "
								+ detalle.getRecibo() + ", " + detalle.getCobro() + ")";
						logger.info(nuevoDet);
						st.addBatch(nuevoDet);
					}

					// aplicaciones
					if (recibo.getPolizasAplicaciones() != null) {
						IndividualAplicacion[] indiApp = recibo.getPolizasAplicaciones().getIndividualAplicacionArray();

						for (IndividualAplicacion individualAplicacion : indiApp) {

							Long idApli = getNewId("sq_coms_recs_emitidos_apli");
							String apliRef = individualAplicacion.getReferencia();
							Integer apliDc = individualAplicacion.getDigitoControl();
							String aplinif = individualAplicacion.getNif();
							Integer apliTipRef = individualAplicacion.getTipoReferencia().intValue();
							Integer apliTipRecibo = individualAplicacion.getTipoRecibo().intValue();
							String apliNom = null;
							String apliApe1 = null;
							String apliApe2 = null;
							if (individualAplicacion.getNombreApellidos() != null) {
								apliNom = individualAplicacion.getNombreApellidos().getNombre();
								apliApe1 = individualAplicacion.getNombreApellidos().getApellido1();
								apliApe2 = individualAplicacion.getNombreApellidos().getApellido2();
							}
							String apliRazonSocial = null;
							if (individualAplicacion.getRazonSocial() != null) {
								apliRazonSocial = individualAplicacion.getRazonSocial().getRazonSocial();
							}

							BigDecimal apliSaldo = individualAplicacion.getDatosEconomicos().getSaldoPoliza();
							BigDecimal apliPrimaComercial = individualAplicacion.getDatosEconomicos()
									.getPrimaComercial();
							BigDecimal apliBonSistPro = individualAplicacion.getDatosEconomicos()
									.getBonificacionSistProteccion();
							BigDecimal apliBonificacion = individualAplicacion.getDatosEconomicos().getBonificacion();
							BigDecimal apliRecargo = individualAplicacion.getDatosEconomicos().getRecargo();
							BigDecimal apliDescColectivo = individualAplicacion.getDatosEconomicos()
									.getDescuentoColectivo();
							BigDecimal apliDescVentanilla = individualAplicacion.getDatosEconomicos()
									.getDescuentoVentanilla();
							BigDecimal apliPrimaNeta = individualAplicacion.getDatosEconomicos().getPrimaNeta();
							BigDecimal apliConsorcio = individualAplicacion.getDatosEconomicos().getConsorcio();
							BigDecimal apliClea = individualAplicacion.getDatosEconomicos().getClea();
							BigDecimal apliCosteNeto = individualAplicacion.getDatosEconomicos().getCosteNeto();
							BigDecimal apliSubEnesa = individualAplicacion.getDatosEconomicos().getSubvencionEnesa();
							BigDecimal apliCosteTomador = individualAplicacion.getDatosEconomicos().getCosteTomador();
							BigDecimal apliPagos = individualAplicacion.getDatosEconomicos().getPagos();

							String nuevaapli = "INSERT INTO o02agpe0.tb_coms_recs_emitidos_apli "
									+ "(ID, IDRECIBOEMITIDO, REFERENCIA, DIGITOCONTROL, NIFCIF, TIPOREFERENCIA, TIPORECIBO, "
									+ " NOMBRE, APELLIDO1, APELLIDO2, RAZONSOCIAL, SALDOPOLIZA, PRIMACOMERCIAL, BONSISTPROTECCION, "
									+ " BONIFICACION, RECARGO, DESCCOLECTIVO, DESCVENTANILLA, PRIMANETA, CONSORCIO, "
									+ " CLEA, COSTENETO, SUBENESA, COSTETOMADOR, PAGOS )" + "VALUES (" + idApli + ","
									+ idRecibo + ",'" + nullToString(apliRef) + "'," + apliDc + ",'"
									+ nullToString(aplinif) + "'," + apliTipRef + "," + "" + apliTipRecibo + ",'"
									+ nullToString(apliNom) + "','" + nullToString(apliApe1) + "','"
									+ nullToString(apliApe2) + "','" + nullToString(apliRazonSocial) + "'," + apliSaldo
									+ "," + apliPrimaComercial + "," + "" + apliBonSistPro + "," + apliBonificacion
									+ "," + apliRecargo + "," + apliDescColectivo + "," + apliDescVentanilla + ","
									+ apliPrimaNeta + "," + apliConsorcio + "," + "" + apliClea + "," + apliCosteNeto
									+ "," + apliSubEnesa + "," + apliCosteTomador + "," + apliPagos + ")";
							logger.info(nuevaapli);
							st.addBatch(nuevaapli);

							// ccaa de aplicaciones
							SubvencionCCAA[] subs2 = individualAplicacion.getSubvencionCCAAArray();

							for (SubvencionCCAA subvencionCCAA : subs2) {
								Long idCCAA = getNewId("sq_coms_recs_emi_apli_ccaa");
								String nuevsub2 = "INSERT INTO o02agpe0.tb_coms_recs_emi_apli_ccaa "
										+ "(ID, IDAPLI,CODIGO,SUBVCOMUNIDADES ) " + VALUES_STR + idCCAA + "," + idApli
										+ ",'" + nullToString(subvencionCCAA.getCodigo()) + "',"
										+ subvencionCCAA.getSubvencionComunidades() + ")";
								logger.info(nuevsub2);
								st.addBatch(nuevsub2);
							}
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
				if (fase == null)
					logger.info("El fichero " + nombreFichero + " no tiene el formato adecuado");
				else
					logger.info("El fichero " + nombreFichero + " esta duplicado");
			}
		} catch (Exception e) {
			if (con != null)
				con.rollback();
			logger.error("Error durante el procesamiento de recibos emitidos " + ficheroImportacion.getName(), e);

			throw new SQLException(e.getMessage());
		} finally {
			if (st != null)
				st.close();
			if (con != null)
				con.close();
		}
	}
}