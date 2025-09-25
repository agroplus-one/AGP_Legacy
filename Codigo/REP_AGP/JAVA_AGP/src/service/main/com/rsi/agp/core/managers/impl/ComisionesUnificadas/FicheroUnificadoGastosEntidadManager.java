package com.rsi.agp.core.managers.impl.ComisionesUnificadas;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.jmesa.dao.impl.IImportacionComisionesUnificadoDao;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.AseguradoUnif17;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.GrupoNegocioUnif17;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.PolizaUnif17;
import com.rsi.agp.dao.tables.comisiones.gastosentidad.ReciboUnif17;
import com.rsi.agp.dao.tables.comisiones.unificado.ColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class FicheroUnificadoGastosEntidadManager extends
		FicheroUnificadoGastosManager {

	private static final Log LOGGER = LogFactory
			.getLog(FicheroUnificadoGastosEntidadManager.class);
	
	private IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao;

	public FicheroUnificado getFicheroUnificado(XmlObject xml, Usuario usuario,
			Character tipoFichero, Long idFichero, String nombreFichero, Blob blob)
			throws Exception {

		LOGGER.debug("init - getFicheroUnificado");
		FicheroUnificado file = null;
		try {
			
			// Venimos de la recarga de fichero
			if (null!=idFichero) {
				file = (FicheroUnificado) importacionComisionesUnificadoDao.getObject(FicheroUnificado.class, idFichero);
			}
			else {
				// Datos generales del fichero
				file = new FicheroUnificado();
				this.llenaDatosGeneralesFichero(file, usuario == null ? "@BATCH" : usuario.getCodusuario(), tipoFichero,
						nombreFichero, new Date());
			}

			// FASES
			es.agroseguro.recibos.abonoGastosEntidad.Entidad entidad = ((es.agroseguro.recibos.abonoGastosEntidad.EntidadDocument) xml)
					.getEntidad();
			for (es.agroseguro.recibos.abonoGastosEntidad.Fase fase : entidad.getFaseArray()) {
				FaseUnificado faseUnificado = this.getFaseUnificado(
						fase.getFase(), fase.getFechaFase().getTime(),
						fase.getPlan(), file);

				// RECIBOS
				for (es.agroseguro.recibos.abonoGastosEntidad.Recibo re : fase
						.getReciboArray()) {
					ReciboUnif17 reciboUnificado = new ReciboUnif17();

					// Datos propios de recibo
					reciboUnificado.setLinea(new BigDecimal(re.getLinea()));
					reciboUnificado.setRecibo(new BigDecimal(re.getRecibo()));
					reciboUnificado.setTipo(new BigDecimal(re.getTipo()));
					reciboUnificado.setTotalcostetomador(re
							.getTotalCosteTomador());

					// POLIZA
					es.agroseguro.recibos.abonoGastosEntidad.Poliza po = re
							.getPoliza();
					es.agroseguro.recibos.abonoGastosEntidad.CobroAplicado ca = re
							.getCobroAplicado();
					PolizaUnif17 poliza = new PolizaUnif17();

					// Datos propios de poliza
					poliza.setCodigoInterno(po.getCodigoInterno());
					poliza.setDc(new BigDecimal(po.getDigitoControl()));
					poliza.setImpCobroRecibido(ca.getImporteCobroRecibido());
					poliza.setPlazoDomiciliacion(new BigDecimal(ca
							.getPlazoDomiciliacion()));
					poliza.setReferencia(po.getReferencia());
					poliza.setTipoReferencia(po.getTipoReferencia().toString()
							.charAt(0));
					poliza.setAnuladaRefundida(po.getAnuladaRefundida()
							.toString().charAt(0));
					if (po.getFechaEntradaVigor() != null) {
						poliza.setFechaVigor(po.getFechaEntradaVigor().getTime());
					}
					
					// ASEGURADO
					es.agroseguro.recibos.abonoGastosEntidad.Asegurado as = po
							.getAsegurado();
					AseguradoUnif17 asegurado = new AseguradoUnif17();

					// Datos propios de asegurado
					if (as.getNombreApellidos() != null) {
						asegurado
								.setNombre(as.getNombreApellidos().getNombre());
						asegurado.setApellido1(as.getNombreApellidos()
								.getApellido1());
						asegurado.setApellido2(as.getNombreApellidos()
								.getApellido2());
					}
					if (as.getRazonSocial() != null) {
						asegurado.setRazonsocial(as.getRazonSocial()
								.getRazonSocial());
					}
					asegurado.setNifcif(as.getNif());

					// join poliza - asegurado
					poliza.setAsegurado(asegurado);

					// INDIVIDUAL
					es.agroseguro.recibos.abonoGastosEntidad.Individual iu = po
							.getIndividual();
					if (iu != null) {
						IndividualUnificado individualUnificado = getIndividualUnificado(iu);

						// join poliza - individual
						poliza.setIndividualUnificado(individualUnificado);
					}

					// COLECTIVO
					es.agroseguro.recibos.abonoGastosEntidad.Colectivo co = po
							.getColectivo();
					if (co != null) {
						ColectivoUnificado colectivoUnificado = getColectivoUnificado(co);

						// join poliza - colectivo
						poliza.setColectivoUnificado(colectivoUnificado);
					}

					// GRUPOS NEGOCIO
					for (es.agroseguro.recibos.abonoGastosEntidad.GrupoNegocio gn : po
							.getGrupoNegocioArray()) {
						GrupoNegocioUnif17 grupoNegocio = new GrupoNegocioUnif17();

						// Datos propios de grupo de negocio
						grupoNegocio.setFracPriComNeta(gn.getFracPriComNeta());
						grupoNegocio.setGrupoNegocio(gn.getGrupoNegocio()
								.charAt(0));
						grupoNegocio.setPrimaComercialNeta(gn
								.getPrimaComercialNeta());
						es.agroseguro.recibos.abonoGastosEntidad.ComisionesGastos ga = gn
								.getGastosAbonarPorCobroActual();
						if (ga != null) {
							grupoNegocio.setGaAdmin(ga.getGastosAdminEntidad());
							grupoNegocio.setGaAdq(ga.getGastosAdqEntidad());
							grupoNegocio.setGaComisionMediador(ga
									.getComisionesMediador());
						}
						es.agroseguro.recibos.abonoGastosEntidad.GastosDevengados gd = gn
								.getGastosDevengados();
						if (gd != null) {
							grupoNegocio.setGdImpAdmin(gd
									.getGastosAdminEntidad());
							grupoNegocio.setGdImpAdq(gd.getGastosAdqEntidad());
							grupoNegocio.setGdImpComMediador(gd
									.getComisionesMediador());
							grupoNegocio.setGdPctAdmin(gd
									.getPorcGastosAdmEntidad());
							grupoNegocio.setGdPctAdq(gd
									.getPorcGastosAdqEntidad());
							grupoNegocio.setGdPctComMediador(gd
									.getPorcComisMediador());
						}
						es.agroseguro.recibos.abonoGastosEntidad.ComisionesGastos gp = gn
								.getGastosPendientesAbonar();
						if (gp != null) {
							grupoNegocio.setGpAdmin(gp.getGastosAdminEntidad());
							grupoNegocio.setGpAdq(gp.getGastosAdqEntidad());
							grupoNegocio.setGpComisionMediador(gp
									.getComisionesMediador());
						}

						// join poliza - grupo negocio
						grupoNegocio.setPoliza(poliza);
						poliza.getGrupoNegocios().add(grupoNegocio);
					}

					// join recibo - poliza
					poliza.setRecibo(reciboUnificado);
					reciboUnificado.getPolizas().add(poliza);

					// join fase - recibo
					reciboUnificado.setFaseUnificado(faseUnificado);
					faseUnificado.getRecibos().add(reciboUnificado);
				}

				// join fichero - fase
				faseUnificado.setFichero(file);
				file.getFases().add(faseUnificado);
			}
			
			// Si venimos de recarga fichero
			if (null!=idFichero) {
				file.setId(idFichero);
			} else {
				// Si no, almacenamos el xml del fichero
				file.setFicheroContenido(this.getFicheroContenidoUnificado(file,blob,idFichero));
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Se ha producido un error en la creacion del fichero unificado de gastos de entidad unificados",
					ex);
			throw ex;
		}

		return file;

	}

	private ColectivoUnificado getColectivoUnificado(
			es.agroseguro.recibos.abonoGastosEntidad.Colectivo co) {
		es.agroseguro.iTipos.NombreApellidos na = co.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz = co.getRazonSocial();
		ColectivoUnificado colectivoUnificado = this.getColectivoUnificado(na,
				rz, co.getReferencia(), co.getCodigoInterno(),
				co.getDigitoControl());
		return colectivoUnificado;
	}

	private IndividualUnificado getIndividualUnificado(
			es.agroseguro.recibos.abonoGastosEntidad.Individual iu) {
		es.agroseguro.iTipos.NombreApellidos na = iu.getNombreApellidos();
		es.agroseguro.iTipos.RazonSocial rz = iu.getRazonSocial();
		IndividualUnificado individualUnificado = this.getIndividualUnificado(
				na, rz);
		return individualUnificado;
	}
	
	public void setImportacionComisionesUnificadoDao(IImportacionComisionesUnificadoDao importacionComisionesUnificadoDao) {
		this.importacionComisionesUnificadoDao = importacionComisionesUnificadoDao;
	}
}