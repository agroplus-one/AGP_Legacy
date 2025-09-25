package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.EleccionFormaPagoController;
import com.rsi.agp.core.webapp.action.MetodoPagoController;
import com.rsi.agp.core.webapp.util.StringUtils;
/* Pet. 22208 ** MODIF TAM (27.02.2018) ** Inicio */
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.models.poliza.IPagoPolizaDao;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

public class EleccionFormaPagoManager implements IManager {

	private EleccionFormaPagoController eleccionFormaPagoController;
	private IPagoPolizaDao pagoPolizaDao;
	/* Pet. 22208 ** MODIF TAM (27.02.2018) ** Inicio */
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private MetodoPagoController metodoPagoController;
	private PolizaManager polizaManager;

	private static final Log logger = LogFactory.getLog(EleccionFormaPagoManager.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public Map<String, Object> obtenerDatosFormaPago(final Usuario usuario, final Long idPoliza,
			HttpServletRequest request) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		boolean pagoPolPorDefecto = false;

		boolean permiteEnvIban = false;
		try {
			Poliza poliza = (Poliza) pagoPolizaDao.get(Poliza.class, idPoliza);
			PagoPoliza pagoPoliza = pagoPolizaDao.getFormaPago(idPoliza);
			boolean lineaContrSup2019 = pagoPolizaDao.lineaContratacion2019(poliza.getLinea().getCodplan(),
					poliza.getLinea().getCodlinea(), poliza.getLinea().isLineaGanado());
			boolean lineaContrSup2021 = lineaContrSup2019 && pagoPolizaDao.lineaContratacion2021(
					poliza.getLinea().getCodplan(), poliza.getLinea().getCodlinea(), poliza.getLinea().isLineaGanado());

			parameters.put("lineaContrataSup2021", lineaContrSup2021);

			String ibanCCC = "";
			String ibanCCC2 = "";
			if (pagoPoliza != null) {
				if (null != pagoPoliza.getTipoPago() && pagoPoliza.getTipoPago().equals(Constants.DOMICILIACION_AGRO)
						&& lineaContrSup2019 == true) {
					pagoPoliza.setEnvioIbanAgro('S');
				}

				if (pagoPoliza.getTipoPago() != null) {
					if (lineaContrSup2019 != true) {
						if (pagoPoliza.getTipoPago().equals(Constants.PAGO_MANUAL)) {
							parameters.put("tipoPagoGuardado", "manual");
						} else {
							parameters.put("tipoPagoGuardado", "cargoEnCuenta");
						}
					} else {
						if (pagoPoliza.getTipoPago().equals(Constants.PAGO_MANUAL)) {
							parameters.put("tipoPagoGuardado", "manual");
						} else if (pagoPoliza.getTipoPago().equals(Constants.CARGO_EN_CUENTA)) {
							parameters.put("tipoPagoGuardado", "cargoEnCuenta");
						} else {
							parameters.put("tipoPagoGuardado", "domiciliacionAgro");
						}
					}
				}

				// Si existe datos de pago en la tabla TB_PAGOS_POLIZA lo recuperamos antes
				// que los datos de pago procedentes del mantenimiento de usuario.
				if (pagoPoliza.getCccbanco() != null && pagoPoliza.getIban().length() == 4
						&& pagoPoliza.getCccbanco().length() == 20) {
					ibanCCC = formatearIbanYCuentaDePagosPoliza(pagoPoliza.getIban(), pagoPoliza.getCccbanco());
				} else {
					ibanCCC = AseguradoUtil.obtenerCccFormateado(poliza, true);
				}
				if (!StringUtils.isNullOrEmpty(pagoPoliza.getIban2())
						&& !StringUtils.isNullOrEmpty(pagoPoliza.getCccbanco2()) && pagoPoliza.getIban2().length() == 4
						&& pagoPoliza.getCccbanco2().length() == 20) {
					ibanCCC2 = formatearIbanYCuentaDePagosPoliza(pagoPoliza.getIban2(), pagoPoliza.getCccbanco2());
				} else {
					ibanCCC2 = AseguradoUtil.obtenerCccFormateado(poliza, false);
				}

				// Recuperamos la informacion de destinatario domiciliacion y titular cuenta
				// para que no se pierda
				if (pagoPoliza.getDestinatarioDomiciliacion() == null
						|| pagoPoliza.getDestinatarioDomiciliacion().toString().equals("")) {
					DatoAsegurado da = AseguradoUtil.obtenerDatoAsegurado(poliza);
					pagoPoliza.setDestinatarioDomiciliacion(
							da.getDestinatarioDomiciliacion() != null ? da.getDestinatarioDomiciliacion().charAt(0)
									: null);
					pagoPoliza.setTitularCuenta(da.getTitularCuenta());
				}

				if (pagoPoliza.getFecha() == null) {

					Date fechaHoy = new Date();

					// Si la fecha actual es menor que la del primer pago del colectivo, se asigna
					// la fecha del primer pago del colectivo.
					if ((poliza.getColectivo().getFechaprimerpago() != null
							&& fechaHoy.after(poliza.getColectivo().getFechaprimerpago())))
						pagoPoliza.setFecha(fechaHoy);
					else if (poliza.getColectivo().getFechaprimerpago() != null)
						pagoPoliza.setFecha(poliza.getColectivo().getFechaprimerpago());
					else
						pagoPoliza.setFecha(null);
				}
			} else {
				ibanCCC = AseguradoUtil.obtenerCccFormateado(poliza, true);
				ibanCCC2 = AseguradoUtil.obtenerCccFormateado(poliza, false);

				pagoPolPorDefecto = true;
				pagoPoliza = new PagoPoliza();
				pagoPoliza.setPoliza(poliza);
				asignaValoresPagoPolizaPorDefecto(pagoPoliza, poliza);

				logger.debug("Despues de asignaValoresPagoPolizaPorDefecto");
				logger.debug("poliza: " + poliza);
				logger.debug("isLineaGanado: " + poliza.getLinea().isLineaGanado());
				logger.debug("esFinanciada: " + poliza.getEsFinanciada());
				logger.debug("codPlan: " + poliza.getLinea().getCodplan());
				logger.debug("codLinea: " + poliza.getLinea().getCodlinea());

				if (poliza.getLinea().isLineaGanado()) {
					if (poliza.getEsFinanciada().equals('S'))
						pagoPoliza.setEnvioIbanAgro('S');
				} else {
					// Pet. 54046 ** MODIF TAM (16.07.2018) ** Inicio //
					BigDecimal codPlan_pol = poliza.getLinea().getCodplan();
					BigDecimal codLinea_pol = poliza.getLinea().getCodlinea();

					permiteEnvIban = pagoPolizaDao.polizaAgrPermiteEnvioIban(codPlan_pol, codLinea_pol);

					logger.debug("no es de ganado");
					logger.debug("permiteEnvIban: " + permiteEnvIban);
					logger.debug("lineaContrSup2019: " + lineaContrSup2019);

					if (permiteEnvIban == true) {
						if (poliza.getEsFinanciada().equals('S'))
							pagoPoliza.setEnvioIbanAgro('S');
					} else if (lineaContrSup2019 == true) {
						if (poliza.getEsFinanciada().equals('S'))
							pagoPoliza.setTipoPago(new BigDecimal(2));

					} else {
						// Pet. 54046 ** MODIF TAM (16.07.2018) ** Fin //
						pagoPoliza.setEnvioIbanAgro(null);
						pagoPoliza.setDestinatarioDomiciliacion(null);
						pagoPoliza.setTitularCuenta(null);
					}
				}
			}
			logger.debug("asignamos valor de los dos Iban");
			parameters.put("numeroCuenta", ibanCCC);
			parameters.put("numeroCuenta2", ibanCCC2);

			boolean mpPagoM = metodoPagoController.isPagoMAllowed(usuario, poliza);
			boolean mpPagoC = metodoPagoController.isPagoCCAllowed(usuario, poliza);
			boolean mpDomiAgro = metodoPagoController.isDomiciAgroAllowed(usuario, poliza, lineaContrSup2019);
			parameters.put("mpPagoM", mpPagoM);
			parameters.put("mpPagoC", mpPagoC);
			parameters.put("mpDomiAgro", mpDomiAgro);
			parameters.put("destinatarioDomiciliacion2019", pagoPoliza.getDestinatarioDomiciliacion());
			parameters.put("titularCuenta2019", pagoPoliza.getTitularCuenta());
			parameters.put("caracterEnvioIbanAgroseguro", poliza.getColectivo().getEnvioIbanAgro());

			// se añade el costetomador y la oficina a los parametros
			parameters.put("oficina", poliza.getOficina());
			parameters.put("entidad", poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad());
			if (null != poliza.getDistribucionCoste2015s() && poliza.getDistribucionCoste2015s().size() > 0) {
				Iterator<DistribucionCoste2015> it = poliza.getDistribucionCoste2015s().iterator();
				BigDecimal importePago = new BigDecimal(0.00);
				while (it.hasNext()) {
					DistribucionCoste2015 dc = it.next();
					parameters.put("costeTomador",
							poliza.getLinea().isLineaGanado() ? dc.getTotalcostetomador() : dc.getCostetomador());
					if (dc.getImportePagoFracc() != null) {
						importePago = dc.getImportePagoFracc();
						pagoPoliza.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
					} else if (dc.getImportePagoFraccAgr() != null) {
						importePago = dc.getImportePagoFraccAgr();
						pagoPoliza.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
					} else {
						importePago = importePago.add(
								poliza.getLinea().isLineaGanado() ? dc.getTotalcostetomador() : dc.getCostetomador());
						pagoPoliza.setFormapago(Constants.FORMA_PAGO_ALCONTADO);
					}
					pagoPoliza.setImporte(importePago);
					//Contiene el importe pagado por el cliente. Por defecto, el importe de la poliza.
					pagoPoliza.setImportePago(importePago);
					break;
				}
			}
			logger.debug("## financiada: " + poliza.getEsFinanciada() + " costeTomador: "
					+ parameters.get("costeTomador") + " oficina: " + poliza.getOficina());

			poliza.setImporte(pagoPoliza.getImporte());

			if (request.getParameter("netoTomadorFinanciadoAgr") != null
					&& !request.getParameter("netoTomadorFinanciadoAgr").equals("")) {
				parameters.put("importePrimerPagoCliente", request.getParameter("netoTomadorFinanciadoAgr"));
				parameters.put("importePoliza", request.getParameter("netoTomadorFinanciadoAgr"));
			} else if (request.getParameter("modoLectura") != null
					&& request.getParameter("modoLectura").equals("modoLectura")) {
				if (!Constants.FORMA_PAGO_FINANCIADO.equals(pagoPoliza.getFormapago())) {
					if (request.getParameter("importePrimerPagoCliente") != null) {
						parameters.put("importePrimerPagoCliente", request.getParameter("importePrimerPagoCliente"));
						parameters.put("importePoliza", pagoPoliza.getImporte());
					} else {
						parameters.put("importePrimerPagoCliente", pagoPoliza.getImporte());
						parameters.put("importePoliza", pagoPoliza.getImporte());
					}
				} else {
					parameters.put("importePrimerPagoCliente", getPrimerPago(poliza));
					parameters.put("importePoliza", pagoPoliza.getImporte());
				}
			} else {
				if (request.getParameter("esSaecaVal") != null && !request.getParameter("esSaecaVal").equals("")) {
					parameters.put("importePrimerPagoCliente", request.getParameter("esSaecaVal"));
					parameters.put("importePoliza", request.getParameter("esSaecaVal"));
				} else {
					parameters.put("importePrimerPagoCliente", getPrimerPago(poliza));
					parameters.put("importePoliza", pagoPoliza.getImporte());
				}
			}

			parameters.put("importeSegundoPagoCliente", getSegundoPago(poliza));

			if (pagoPolPorDefecto)
				asignaValorTipoPagoPorDefecto(pagoPoliza, mpPagoM, mpPagoC, mpDomiAgro);

			if (poliza.getPolizaPpal() == null) {
				parameters.put("esPolPrincipal", true);
			} else {
				parameters.put("esPolPrincipal", false);
			}

			// Para saber si la poliza es financiada por SAECA
			Boolean esFinaciadaSaeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),
					poliza.getColectivo().getSubentidadMediadora());
			parameters.put("esSaeca", esFinaciadaSaeca);
			parameters.put("lineaContratSuperior2019", lineaContrSup2019);
			if (lineaContrSup2019) {
				if (!mpPagoM && !mpPagoC && !mpDomiAgro) {
					parameters.put("alerta2", bundle.getString("mensaje.formaPago.inexistente"));
				}
			} else {
				if (!mpPagoM && !mpPagoC) {
					parameters.put("alerta2", bundle.getString("mensaje.formaPago.inexistente"));
				}
			}
			parameters.put("isLineaGanado", poliza.getLinea().isLineaGanado());

			// Pet. 54046 ** MODIF TAM (29.06.2018) ** Inicio//
			// Si no es línea de ganado consultamos si se permite el envío del Iban
			// Si es ganado... siempre se permite el envío del Iban
			if (poliza.getLinea().isLineaGanado() == true) {
				parameters.put("permiteEnvIban", true);
			} else {
				BigDecimal codPlan_pol = poliza.getLinea().getCodplan();
				BigDecimal codLinea_pol = poliza.getLinea().getCodlinea();
				logger.debug("**@@** EleccionFormaPagomanager - Antes de obtener el Indicador permiteEnvIban");
				permiteEnvIban = pagoPolizaDao.polizaAgrPermiteEnvioIban(codPlan_pol, codLinea_pol);

				// Si es poliza complementaria, se marcan los datos de la poliza principal
				if ((poliza.getPolizaPpal() != null && permiteEnvIban == true)
						|| (poliza.getPolizaPpal() != null && (lineaContrSup2019 == true && mpDomiAgro == true))) {

					logger.debug("**@@** ANTES DE CONSULTAR DATOS DE LA POLIZA COMPLEMENTARIA **@@**");

					long idPolPpal = poliza.getPolizaPpal().getIdpoliza();
					logger.debug("**@@** Valor de idPoliza:" + idPolPpal);

					PagoPoliza pagoPol_Ppal = pagoPolizaDao.getFormaPago(idPolPpal);
					
					logger.debug("**@@** Hemos obtenido el PagoPoliza");
					
					if ( null==pagoPoliza.getDestinatarioDomiciliacion() || pagoPoliza.getDestinatarioDomiciliacion() == ' ') {
						pagoPoliza.setDestinatarioDomiciliacion(new Character(pagoPol_Ppal.getDestinatarioDomiciliacion()));
					}
					
					if ( null==pagoPoliza.getTitularCuenta() || pagoPoliza.getTitularCuenta().length() == 0 ) {
						pagoPoliza.setTitularCuenta(StringUtils.isNullOrEmpty(pagoPol_Ppal.getTitularCuenta()) ? "" : new String(pagoPol_Ppal.getTitularCuenta()));
					}
					
					pagoPoliza.setEnvioIbanAgro(pagoPol_Ppal.getEnvioIbanAgro());
					parameters.put("envioIbanAgro", pagoPol_Ppal.getEnvioIbanAgro());
					parameters.put("indEnvIbanCpl", pagoPol_Ppal.getEnvioIbanAgro());
				}

				logger.debug("**@@** EleccionFormaPagomanager - Valor de permiteEnvIban:" + permiteEnvIban);
				parameters.put("permiteEnvIban", permiteEnvIban);
			}
			logger.debug("**@@** ## obtenerDatosFormaPago. Valor de permiteIEnvIban:" + permiteEnvIban);

			parameters.put("perfilUsu", eleccionFormaPagoController.getCodigoPerfilUsuarioEI(usuario));

			Character esPolFinanciada = poliza.getEsFinanciada();

			logger.debug("**@@** EleccionFormaPagomanager (ObtenerDatosFormaPago) - Valor de esFinanciada:"
					+ esPolFinanciada);

			// Pet. 54046 ** MODIF TAM (29.06.2018) ** Fin//
			parameters.put("isPolizaFinanciada", poliza.getEsFinanciada().equals('S'));
			
			// Pet. 22208 ** MODIF TAM (19.02.2018) ** Inicio //
			logger.debug("## pagada(antes): " + poliza.getEstadoPagoAgp().getId());
			if (poliza.getEstadoPagoAgp().getId() == 1) {
				parameters.put("pagada", 'S');
			} else {
				parameters.put("pagada", 'N');
			}
			logger.debug("## pagada(despues): " + parameters.get("pagada"));
			// Pet. 22208 ** MODIF TAM (27.02.2018) ** Inicio **//
			// Incluimos una nueva funcion para obtener el swConfirmacion //
			/*
			 * obtenemos el valor guardado en la tabla de SubentidadMediadora y lo pasamos
			 * por parametro
			 */

			boolean swConfirm = puedeSWConfirmacion(usuario);
			logger.debug("*** swConfirm: " + swConfirm);
			if (swConfirm == true)
				parameters.put("swConfirmacion", 'S');
			else
				parameters.put("swConfirmacion", 'N');
			// Pet. 22208 ** MODIF TAM (27.02.2018) ** Fin **//

			/* Guardamos datos necesarios para el mensaje de confirmacion */
			parameters.put("codUsuario", usuario.getCodusuario());
			parameters.put("nomUsuario", usuario.getNombreusu());
			parameters.put("nifcifAsegurado", poliza.getAsegurado().getNifcif());
			parameters.put("nomAsegurado", poliza.getAsegurado().getNombreCompleto());

			// Comprobar si la forma pago cliente es visible, si cumple lo siguiente
			// el usuario puede financiar
			// fechalimite usuario nula o mayor o igual que la fecha actual
			// importe 1qfracciond el pago este dentro del rango establecido entre los
			// importes mín. y max del usuario
			logger.debug("*** usuario.getFinanciar: " + usuario.getFinanciar());
			logger.debug("*** usuario.getFechaLimite: " + usuario.getFechaLimite());
			boolean financiarOK = BigDecimal.ONE.equals(usuario.getFinanciar());
			boolean fechaLimiteOK = false;
			boolean importeMinFinanciacionOK = false;
			boolean importeMaxFinanciacionOK = false;
			
			if (usuario.getFechaLimite() != null) {
				Date fechaActual = new Date();
				SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
				String fechaSistema = formateador.format(fechaActual);
				Date fSystema = formateador.parse(fechaSistema);
				String fechaUsuario = formateador.format(usuario.getFechaLimite());
				Date fUsuario = formateador.parse(fechaUsuario);
				logger.debug(" fUsuario: " + fechaUsuario + " fechaSistema: " + fechaSistema);
				if (fUsuario == null || (fUsuario.compareTo(fSystema) > 0 || fUsuario.compareTo(fSystema) == 0)) {
					fechaLimiteOK = true; // fechalimite usuario nula o mayor o igual que la fecha actual
				}
			} else {
				fechaLimiteOK = true;
			}
			BigDecimal impPrimeraFraccPago = pagoPoliza.getImporte();
			logger.debug(" impPrimeraFraccPago: " + impPrimeraFraccPago + " usu minFinanciacion: "
					+ usuario.getImpMinFinanciacion() + " usu maxFinanciacion: " + usuario.getImpMaxFinanciacion());
			if (usuario.getImpMinFinanciacion() != null || usuario.getImpMaxFinanciacion() != null) {
				// importeMinFinanciacionOK
				if (usuario.getImpMinFinanciacion() != null) {
					if ((impPrimeraFraccPago.compareTo(usuario.getImpMinFinanciacion()) == 0
							|| impPrimeraFraccPago.compareTo(usuario.getImpMinFinanciacion()) > 0)) {
						importeMinFinanciacionOK = true;
					}
				} else {
					importeMinFinanciacionOK = true;
				}

				// importeMaxFinanciacionOK
				if (usuario.getImpMaxFinanciacion() != null) {
					if (impPrimeraFraccPago.compareTo(usuario.getImpMaxFinanciacion()) == 0
							|| impPrimeraFraccPago.compareTo(usuario.getImpMaxFinanciacion()) < 0) {
						importeMaxFinanciacionOK = true;
					}
				} else {
					importeMaxFinanciacionOK = true;
				}
			} else {
				importeMinFinanciacionOK = true;
				importeMaxFinanciacionOK = true;
			}

			logger.debug(
					" financiarOK: " + financiarOK + " fechaLimiteOK: " + fechaLimiteOK + " importeMinFinanciacionOK: "
							+ importeMinFinanciacionOK + " importeMaxFinanciacionOK: " + importeMaxFinanciacionOK);
			parameters.put("verFormaPagoCliente", financiarOK && fechaLimiteOK && importeMinFinanciacionOK && importeMaxFinanciacionOK);
			// Fin Comprobar si la forma pago cliente es visible
			parameters.put("financiarOK", financiarOK);
			parameters.put("pagoPoliza", pagoPoliza);

		} catch (DAOException e) {
			logger.error("obtenerDatosFormaPago - Se ha producido un error" + e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		} catch (Exception e) {
			logger.error("obtenerDatosFormaPago - Se ha producido un error" + e);
			throw e;
		}

		return parameters;
	}
	
	/**/
	/* Pet. 22208 ** MODIF TAM (26.02.2018) ** Inicio */
	public Boolean puedeSWConfirmacion(final Usuario usuario) throws BusinessException {
		logger.debug("*** init puedeSWConfirmacion....");
		Boolean result = Boolean.FALSE;
		logger.debug("usuario: " + usuario);
		logger.debug("usuario.getSubentidadMediadora: " + usuario.getSubentidadMediadora());
		logger.debug("usuario.getSubentidadMediadora.getSubentidadMediadora().getId(): " + usuario.getSubentidadMediadora().getId());
		logger.debug("usuario.getSubentidadMediadora.getSubentidadMediadora().getId().getCodentidad: " + usuario.getSubentidadMediadora().getId().getCodentidad());
		logger.debug("usuario.getSubentidadMediadora.getSubentidadMediadora().getId().getCodsubentidad: " + usuario.getSubentidadMediadora().getId().getCodsubentidad());
		try{
			
   		   BigDecimal entidad = usuario.getSubentidadMediadora().getId().getCodentidad();
		   BigDecimal subEntidad = usuario.getSubentidadMediadora().getId().getCodsubentidad();
		   logger.debug("*** antes de result ....");
		   result = BigDecimal.ONE.equals(((SubentidadMediadora) this.subentidadMediadoraDao.get(SubentidadMediadora.class, new SubentidadMediadoraId(entidad, subEntidad))).getSwConfirmacion()); 
		   logger.debug("*** result : " + result);
		}catch (DAOException e) {
			logger.error ("puedesSwConfirmacion - Se ha producido un error"+ e);
			throw new BusinessException(e);
		}
		
		return result;
	}
	
	private String formatearIbanYCuentaDePagosPoliza(String iban, String ccc){
		StringBuilder sb = new StringBuilder();
		sb.append(iban.toUpperCase());
		sb.append(" ");
		sb.append(ccc.substring(0, 4));
		sb.append(" ");
		sb.append(ccc.substring(4,8));
		sb.append(" ");
		sb.append(ccc.substring(8,12));
		sb.append(" ");
		sb.append(ccc.substring(12,16));
		sb.append(" ");
		sb.append(ccc.substring(16));
		return sb.toString();
	}
	
	
	/**
	 * Visualiza el importe del primer pago, que se calcula aplicando
	 * el % del primer pago al importe total de la poliza.
	 * @param poliza
	 * @return
	 */
	private BigDecimal getPrimerPago(Poliza poliza){
		logger.debug("init - getPrimerPago");
		BigDecimal importe = poliza.getImporte(), pct = null;		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctprimerpago() != null){
					pct = pp.getPctprimerpago();
				}
			}
		}
		else{
			pct = poliza.getColectivo().getPctprimerpago();
		}
		logger.debug("end - getPrimerPago");
		return (pct != null && importe != null) ? pct.multiply(importe).divide(new BigDecimal(100)) : null;
	}
	
	/**
	 * Visualiza el importe del segundo pago, que se calcula aplicando
	 * el % del segundo pago al importe total de la poliza.
	 * @param poliza
	 * @return
	 */	
	private BigDecimal getSegundoPago(Poliza poliza){
		logger.debug("init - getSegundoPago");
		BigDecimal importe = poliza.getImporte(), pct = null;
		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctsegundopago() != null){
					pct = pp.getPctsegundopago();
				}
			}
		}
		else{
			if (poliza.getColectivo().getPctsegundopago() != null){
				pct = poliza.getColectivo().getPctsegundopago();
			}
		}
		logger.debug("end - getSegundoPago");
		if (importe != null && pct != null)
			return pct.multiply(importe).divide(new BigDecimal(100));
		else
			return null;
	}
	
	private void asignaValoresPagoPolizaPorDefecto(PagoPoliza pagoPoliza, Poliza poliza){

		// Pet. 54046 ** MODIF TAM (14.11.2018) ** Inicio //
		// Si la poliza es Complementaria los datos de pago, los debe obtener de la poliza 
		// principal  y no de los datos del Asegurado.
		
		boolean esPolPrincipal = false;
		if(poliza.getPolizaPpal()==null){
			esPolPrincipal = true;
		}else{
			esPolPrincipal = false;
		}
		
		if(esPolPrincipal == true){
			// Obtiene los datos del asegurado asociados a la poliza y a la línea específica/generica
			DatoAsegurado da = AseguradoUtil.obtenerDatoAsegurado(poliza);

			// Pet. 54046 ** MODIF TAM (14.11.2018) ** Fin //	
			if (da != null) {
				pagoPoliza.setIban(da.getIban());
				pagoPoliza.setCccbanco(da.getCcc());
				pagoPoliza.setDestinatarioDomiciliacion(da.getDestinatarioDomiciliacion() != null ? da.getDestinatarioDomiciliacion().charAt(0) : null);
				pagoPoliza.setTitularCuenta(da.getTitularCuenta());
				pagoPoliza.setIban2(da.getIban2());
				pagoPoliza.setCccbanco2(da.getCcc2());
			}
		}else{
			// obtenemos los datos de la poliza principal.
			for(PagoPoliza ppPpal :poliza.getPolizaPpal().getPagoPolizas()) {
				logger.debug("**@@** EleccionFormaPagoManager -Valor de envioIbanAgro:"+ppPpal.getEnvioIbanAgro()); 
				if (ppPpal.getEnvioIbanAgro() != null && ppPpal.getEnvioIbanAgro().equals('S')){
					logger.debug("**@@** EleccionFormaPagoManager - Obtenemos los datos de la Poliza Principal");
					pagoPoliza.setIban(ppPpal.getIban());
					pagoPoliza.setCccbanco(ppPpal.getCccbanco());
					pagoPoliza.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
					pagoPoliza.setTitularCuenta(ppPpal.getTitularCuenta());
					pagoPoliza.setIban2(ppPpal.getIban2());
					pagoPoliza.setCccbanco2(ppPpal.getCccbanco2());
				}else{
					// MODIF TAM (27.11.2018) ESC-4552 //
					// Si no se ha marcado 'Envío de IBAN', los dos valores se guardan vacíos
					
					/*ESC-9332 DNF 30/04/2020 Comento la siguiente linea, dado que quieren que el destinatario domiciliacion se guarde independientemente
					 * de la forma de pago elegida para las polizas complementarias y añado el dato para que lo guarde*/
					//pagoPoliza.setDestinatarioDomiciliacion(null);
					if(null != ppPpal.getDestinatarioDomiciliacion()) {
						pagoPoliza.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
					}
					/*FIN ESC-9332 DNF 30/04/2020*/
					
					pagoPoliza.setTitularCuenta(null);
					// Pero se informan el IBan y el banco de la Poliza Princiapl
					pagoPoliza.setIban(ppPpal.getIban());
					pagoPoliza.setCccbanco(ppPpal.getCccbanco());
					pagoPoliza.setIban2(ppPpal.getIban2());
					pagoPoliza.setCccbanco2(ppPpal.getCccbanco2());

				}
				break;
			}
		}
		
		pagoPoliza.setBanco(null);
		
		if (pagoPoliza.getPctprimerpago() == null){
			//si es nulo, asignamos pctprimerpago, fechaprimerpago, pctsegundopago, a pagos polizas
			pagoPoliza.setFechasegundopago(getFechaSegundoPago(poliza));
			//Introducimos el porcentaje del primer y segundo pago
			pagoPoliza.setPctprimerpago(getPctPrimerPago(poliza));
			pagoPoliza.setPctsegundopago(getPctSegundoPago(poliza));
	
			pagoPoliza.setFecha(getFechaPrimerPago(poliza));
		}
		
		logger.debug("*** fin asignacion fechas pagos.");
		//si tiene ya un pagoPoliza previo, se mantiene la cuenta, la fecha de pago y el banco
		Set<PagoPoliza> pagoPolizas = poliza.getPagoPolizas();
		logger.debug("poliza : " + poliza);
		logger.debug("poliza.getPagoPolizas() : " + poliza.getPagoPolizas());
		// si no tiene pagospoliza comprobamos si es Saeca y metemos de la distrib. de costes el importepagoFracc, si no es Saeca ponemos el importe de la poliza
		if (pagoPolizas == null || pagoPolizas.size()==0){
			logger.debug("poliza.getLinea().getLineaseguroid() : " + poliza.getLinea().getLineaseguroid());
			logger.debug("poliza.getColectivo().getSubentidadMediadora() : " + poliza.getColectivo().getSubentidadMediadora());
			Boolean esSaeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
			logger.debug("esSaeca : " + esSaeca);
			logger.debug("codmodulo : " + poliza.getCodmodulo());
			logger.debug("poliza.getDistribucionCoste2015s(): " + poliza.getDistribucionCoste2015s());
			if(esSaeca){
				DistribucionCoste2015 dcte = polizaManager.getDistCosteSaeca(poliza);
				logger.debug("dcte : " + dcte);
				if (dcte.getImportePagoFracc() != null)
					pagoPoliza.setImporte(dcte.getImportePagoFracc());
			}
		}
		// Fin asignamos pctprimerpago...
	}
	
	private void asignaValorTipoPagoPorDefecto(PagoPoliza pagoPoliza, boolean mpPagoM, boolean mpPagoC, boolean mpDomiAgro){
	
		//if(mpPagoM!=mpPagoC){
			if(mpPagoM){
				pagoPoliza.setTipoPago(new BigDecimal("1"));
			}else if (mpPagoC){
				pagoPoliza.setTipoPago(new BigDecimal("0"));
			}else{
				pagoPoliza.setTipoPago(new BigDecimal("2"));
			}
		//}
		
	}
	
//	Recibe como parametro el bean PagosPoliza con los datos de pago y los almacena en BBDD. 
//	Ademas se actualizara el campo TB_POLIZAS.PAGADA a N de la poliza asociada.
	public void  guardarDatosFormaPago(PagoPoliza pagoPoliza, Long idpoliza,String oficina)throws Exception{
		logger.debug("**@@** EleccionFormaPagoManager - Dentro de guardarDatosFormaPago");
		Poliza poliza = (Poliza) pagoPolizaDao.getObject(Poliza.class, idpoliza);
		if (oficina != null && !oficina.equals(""))
			poliza.setOficina(String.format("%04d", Integer.parseInt(oficina)));
		// Pet. 22208 ** MODIF TAM (13.03.2018) ** Inicio //
		// Si la poliza esta pagada, no debe lanzarse llamada al metodo guardarDatosFormaPago
		if (poliza.getEstadoPagoAgp().getId()!=1){
		// Pet. 22208 ** MODIF TAM (13.03.2018) ** fin //			
		   if(poliza.getPagoPolizas()==null || poliza.getPagoPolizas().size()==0){			
			   guardarDatosFormaPago (pagoPoliza, poliza, false);
		   }else{
			  guardarDatosFormaPago (pagoPoliza, poliza.getPagoPolizas().iterator().next(), poliza);
			   pagoPolizaDao.saveOrUpdate(poliza);
		   }
		}
	}
	
	public void  guardarDatosFormaPago(PagoPoliza pagoPoliza, Poliza pol, boolean cambioMasivoEstPol)throws Exception{

		logger.debug("**@@** EleccionFormaPagoManager - Dentro de guardarDatosFormaPago (1)");
		
		EstadoPagoAgp estadoPagoAgp = new EstadoPagoAgp(Constants.POLIZA_NO_PAGADA, null, null);
		pol.setEstadoPagoAgp(estadoPagoAgp);
		pagoPolizaDao.saveOrUpdate(pol);		
		
		if(Constants.CARGO_EN_CUENTA.equals(pagoPoliza.getTipoPago())){
			asignaValoresPagoPolizaPorDefecto(pagoPoliza, pol);
			
			if(cambioMasivoEstPol) {
				pagoPoliza.setFecha(getFechaPrimerPagoCambioMasivoEstPol(pol));
			}
			else {
				if (pagoPoliza.getFecha() == null)
					pagoPoliza.setFecha(getFechaPrimerPago(pol));
			}
						
		}
		if(cambioMasivoEstPol) {
			pagoPoliza.setFechasegundopago(getFechaSegundoPagoCambioMasivoEstPol(pol));
			pagoPoliza.setPctprimerpago(getPctPrimerPagoCambioMasivoEstPol(pol));
			pagoPoliza.setPctsegundopago(getPctSegundoPagoCambioMasivoEstPol(pol));
		}
		else {
			if (pagoPoliza.getPctprimerpago()== null){
				pagoPoliza.setFechasegundopago(getFechaSegundoPago(pol));
				pagoPoliza.setPctprimerpago(getPctPrimerPago(pol));
				pagoPoliza.setPctsegundopago(getPctSegundoPago(pol));
			}
		}
		
		pagoPoliza.setPoliza(pol);		
		
		if(pagoPoliza.getEnvioIbanAgro()==null || !pagoPoliza.getEnvioIbanAgro().equals('S')){
			pagoPoliza.setEnvioIbanAgro(null);
			pagoPoliza.setTitularCuenta(null);
		}
		
		/* ESC-13542 ** MODIF TAM (28/04/2021) ** Inicio */
		if (null != pol.getDistribucionCoste2015s() && pol.getDistribucionCoste2015s().size() > 0) {
			Iterator<DistribucionCoste2015> it = pol.getDistribucionCoste2015s().iterator();
			BigDecimal importePago = new BigDecimal(0.00);
			while (it.hasNext()) {
				DistribucionCoste2015 dc = it.next();
				
				if (dc.getImportePagoFracc() != null) {
					importePago = dc.getImportePagoFracc();
					pagoPoliza.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
				} else if (dc.getImportePagoFraccAgr() != null) {
					importePago = dc.getImportePagoFraccAgr();
					pagoPoliza.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
				} else {
					importePago = importePago.add(
							pol.getLinea().isLineaGanado() ? dc.getTotalcostetomador() : dc.getCostetomador());
					pagoPoliza.setFormapago(Constants.FORMA_PAGO_ALCONTADO);
				}
				pagoPoliza.setImporte(importePago);
				//Contiene el importe pagado por el cliente. Por defecto, el importe de la poliza.
				pagoPoliza.setImportePago(importePago);
				break;
			}
		}
		/* ESC-13542 ** MODIF TAM (28/04/2021) ** Fin */
			
		pagoPolizaDao.saveOrUpdate(pagoPoliza);
		pol.getPagoPolizas().add(pagoPoliza);
	}
	
	public void  guardarDatosFormaPago(PagoPoliza pagoPolizaBean, 
		PagoPoliza pagoPolizaActualizar,  Poliza pol)throws Exception{
		pagoPolizaActualizar.setTipoPago(pagoPolizaBean.getTipoPago());
		logger.debug("**@@** EleccionFormaPagoManager - Dentro de guardarDatosFormaPago (2)");
		logger.debug("Valor de tipoPago:"+pagoPolizaBean.getTipoPago());
		if(pagoPolizaBean.getTipoPago().compareTo(Constants.CARGO_EN_CUENTA)==0){//Cargo en cuenta
			if(null!=pagoPolizaBean.getPctprimerpago())
				pagoPolizaActualizar.setPctprimerpago(pagoPolizaBean.getPctprimerpago());		
			if(null!=pagoPolizaBean.getPctsegundopago()){
				pagoPolizaActualizar.setPctsegundopago(pagoPolizaBean.getPctsegundopago());
			}else{
				pagoPolizaActualizar.setPctsegundopago(null);
			}
			if(null!=pagoPolizaBean.getFechasegundopago()){
				pagoPolizaActualizar.setFechasegundopago(pagoPolizaBean.getFechasegundopago());
			}else{
				pagoPolizaActualizar.setFechasegundopago(null);
			}
			if(null!=pagoPolizaBean.getFecha()){
				pagoPolizaActualizar.setFecha(pagoPolizaBean.getFecha());
			}
			//En este caso, se rellena con el importe
			pagoPolizaActualizar.setImportePago(pagoPolizaBean.getImportePago());
			logger.debug ("Valor de ImportePago:"+pagoPolizaActualizar.getImportePago());
			
		}else if (pagoPolizaBean.getTipoPago().compareTo(Constants.PAGO_MANUAL)==0){//Pago manual
			
			pagoPolizaActualizar.setBanco(pagoPolizaBean.getBanco());
			pagoPolizaActualizar.setCccbanco(pagoPolizaBean.getCccbanco());
			pagoPolizaActualizar.setCccbanco2(pagoPolizaBean.getCccbanco2());
			pagoPolizaActualizar.setPctprimerpago(new BigDecimal(100));
			pagoPolizaActualizar.setPctsegundopago(null);
			pagoPolizaActualizar.setFechasegundopago(null);
			if(null!=pagoPolizaBean.getFecha()){
				pagoPolizaActualizar.setFecha(pagoPolizaBean.getFecha());
			}
			BigDecimal importePago = pagoPolizaBean.getImportePago();
			if (importePago != null){
				pagoPolizaActualizar.setImportePago(importePago);
			}
			
		}
		
		pagoPolizaActualizar.setEnvioIbanAgro(pagoPolizaBean.getEnvioIbanAgro());
		
		// Pet. 54046 ** MODIF TAM (15.11.2018) ** Inicio //
		// Si la poliza es Complementaria los datos de pago, los debe obtener de la poliza 
		// principal  y no de los datos del Asegurado.
		boolean esPolPrincipal = false;
		if(pol.getPolizaPpal()==null){
			esPolPrincipal = true;
		}else{
			esPolPrincipal = false;
		}
		// Pet. 54046 ** MODIF TAM (15.11.2018) ** Fin //
		
		logger.debug("Valor de EnvioIbanAgro:"+pagoPolizaBean.getEnvioIbanAgro());

				
		// Se ha marcado 'Envío de IBAN'
		if(pagoPolizaBean.getEnvioIbanAgro() != null && pagoPolizaBean.getEnvioIbanAgro().equals('S')){
			
			logger.debug("Entramos en el if");
			// Si se ha elegido 'Cargo en cuenta' se guardan los valores configurados en el aseguarado
			if (pagoPolizaBean.getTipoPago().compareTo(Constants.CARGO_EN_CUENTA)==0){
				// Pet. 54046 ** MODIF TAM (15.11.2018) ** Inicio //
				// Incluimos la condicion, para las pol.principales y el Else para las complementarias.
				if(esPolPrincipal == true){
					DatoAsegurado da = AseguradoUtil.obtenerDatoAsegurado(pol);
					pagoPolizaActualizar.setDestinatarioDomiciliacion(da.getDestinatarioDomiciliacion() != null ? da.getDestinatarioDomiciliacion().charAt(0) : null);
					pagoPolizaActualizar.setTitularCuenta(da.getTitularCuenta());
				}else{
					// obtenemos los datos de la poliza principal.
					for(PagoPoliza ppPpal :pol.getPolizaPpal().getPagoPolizas()) {
						if (ppPpal.getEnvioIbanAgro() != null && ppPpal.getEnvioIbanAgro().equals('S')){
							pagoPolizaActualizar.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
							pagoPolizaActualizar.setTitularCuenta(ppPpal.getTitularCuenta());
						}
						break;
					}
				}
			}
			// Si se ha elegido 'Pago manual' se guardan los valores informados en la pantalla
			else {
				pagoPolizaActualizar.setDestinatarioDomiciliacion(pagoPolizaBean.getDestinatarioDomiciliacion());
				pagoPolizaActualizar.setTitularCuenta(pagoPolizaBean.getTitularCuenta());
			}
		}else{
			
			logger.debug("Entramos en el else");
			// Si no se ha marcado 'Envío de IBAN', los dos valores se guardan vacíos
			
			/*ESC-7612 DNF 06/04/2020 ahora quieren que el destinatario domiciliacion siempre se guarde en BBDD con lo cual
			 * comento la siquiente linea: pagoPolizaActualizar.setDestinatarioDomiciliacion(null);*/
			//pagoPolizaActualizar.setDestinatarioDomiciliacion(null);
			/*FIN ESC-7612 DNF 06/04/2020*/
			
			/* ESC-13211 ** MODIF TAM (29.03.2021) ** Inicio */
			/* Si estamos enviando un Cargo en Cuenta, con datos de pago ya guardamos actualizamos valores */
			if (pagoPolizaBean.getTipoPago().compareTo(Constants.CARGO_EN_CUENTA)==0){
				pagoPolizaActualizar.setEnvioIbanAgro(null);
				logger.debug("Valor de polizaActualizar.envioIbanAgro:"+pagoPolizaActualizar.getEnvioIbanAgro());
			}
			/* ESC-13211 ** MODIF TAM (29.03.2021) ** Inicio */
			
			pagoPolizaActualizar.setTitularCuenta(null);
		}
		
		if(pol.getEsFinanciada().equals('S')){
			pagoPolizaActualizar.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
		}else{
			pagoPolizaActualizar.setFormapago(Constants.FORMA_PAGO_ALCONTADO);
		}

		pagoPolizaActualizar.setPoliza(pol);
		pagoPolizaDao.saveOrUpdate(pagoPolizaActualizar);
		
	}

	private BigDecimal getPctPrimerPago(Poliza poliza){
		logger.debug("init - getPctPrimerPago");
		BigDecimal res = null;
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctprimerpago() != null)
					res = pp.getPctprimerpago();
			}
		}
		else{
			res = poliza.getColectivo().getPctprimerpago();
		}
		logger.debug("end - getPctPrimerPago");
		return res;
	}
	
	private BigDecimal getPctPrimerPagoCambioMasivoEstPol(Poliza poliza) {
		logger.debug("init - getPctPrimerPagoCambioMasivoEstPol");
		BigDecimal res = null;
				
		res = poliza.getColectivo().getPctprimerpago();
		
		logger.debug("end - getPctPrimerPagoCambioMasivoEstPol");
		return res;
	}
	
	private BigDecimal getPctSegundoPago(Poliza poliza){
		logger.debug("init - getPctSegundoPago");
		BigDecimal res = null;
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctsegundopago() != null)
					res = pp.getPctsegundopago();
			}
		}
		else{
			res = poliza.getColectivo().getPctsegundopago();
		}
		logger.debug("end - getPctSegundoPago");
		return res;
	}
	
	private BigDecimal getPctSegundoPagoCambioMasivoEstPol(Poliza poliza) {
		logger.debug("init - getPctSegundoPagoCambioMasivoEstPol");
		BigDecimal res = null;
				
		res = poliza.getColectivo().getPctsegundopago();
		
		logger.debug("end - getPctSegundoPagoCambioMasivoEstPol");
		return res;
	}
	
	private Date getFechaPrimerPago(Poliza poliza){
		logger.debug("init - getFechaPrimerPago");
		Date fechaToday = new Date();
		Date fecha = null;
		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getFecha() != null)
					fecha = pp.getFecha();
			}
		}else{
			Date fechaPrimerPagoColectivo = poliza.getColectivo().getFechaprimerpago();
			//Si la fecha actual es menor que la del primer pago del colectivo, se asigna la fecha del primer pago del colectivo.	
			if ((fechaPrimerPagoColectivo != null && fechaToday.after(fechaPrimerPagoColectivo)))
				fecha =  fechaToday;
			else if (fechaPrimerPagoColectivo != null)
				fecha =  fechaPrimerPagoColectivo;
			else
				fecha = null;
		}
		logger.debug("end - getFechaPrimerPago");
		return fecha;
	}
	
	private Date getFechaPrimerPagoCambioMasivoEstPol(Poliza poliza) {
		logger.debug("init - getFechaPrimerPagoCambioMasivoEstPol");
		Date fechaToday = new Date();
		Date fecha = null;
			Date fechaPrimerPagoColectivo = poliza.getColectivo().getFechaprimerpago();
			//Si la fecha actual es menor que la del primer pago del colectivo, se asigna la fecha del primer pago del colectivo.
			if ((fechaPrimerPagoColectivo != null && fechaToday.after(fechaPrimerPagoColectivo)))
				fecha =  fechaToday;
			else if (fechaPrimerPagoColectivo != null)
				fecha =  fechaPrimerPagoColectivo;
			else
				fecha = null;
		logger.debug("end - getFechaPrimerPagoCambioMasivoEstPol");
		return fecha;
	}
	
	
	
	private Date getFechaSegundoPago(Poliza poliza){
		logger.debug("init - getFechaSegundoPago");
		long miliDia = 86400000;  // un dÃ­a en milisegundos 
		Date fechaToday = new Date();
		Date fechaTomorrow = new Date(fechaToday.getTime() + miliDia);
		Date fecha = null;
		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getFechasegundopago() != null)
					fecha = pp.getFechasegundopago();
			}
		}
		else{
			Date fechaSegundoPagoColectivo = poliza.getColectivo().getFechasegundopago();
			//Si la fecha actual es mayor que la de segundo pago del colectivo, se asigna la fecha de maÃ±ana.	
			if ((fechaSegundoPagoColectivo != null && fechaTomorrow.after(fechaSegundoPagoColectivo)))
				fecha =  fechaTomorrow;
			else if (fechaSegundoPagoColectivo != null)
				fecha =  fechaSegundoPagoColectivo;
			else
				fecha = null;
		}
		logger.debug("end - getFechaSegundoPago");
		return fecha;
	}
	
	private Date getFechaSegundoPagoCambioMasivoEstPol(Poliza poliza) {
		logger.debug("init - getFechaSegundoPagoCambioMasivoEstPol");
		long miliDia = 86400000;  // un dÃ­a en milisegundos 
		Date fechaToday = new Date();
		Date fechaTomorrow = new Date(fechaToday.getTime() + miliDia);
		Date fecha = null;
		
			Date fechaSegundoPagoColectivo = poliza.getColectivo().getFechasegundopago();
			//Si la fecha actual es mayor que la de segundo pago del colectivo, se asigna la fecha de maÃ±ana.	
			if ((fechaSegundoPagoColectivo != null && fechaTomorrow.after(fechaSegundoPagoColectivo)))
				fecha =  fechaTomorrow;
			else if (fechaSegundoPagoColectivo != null)
				fecha =  fechaSegundoPagoColectivo;
			else
				fecha = null;
		
		logger.debug("end - getFechaSegundoPagoCambioMasivoEstPol");
		return fecha;
	}
	
	public void setPagoPolizaDao(IPagoPolizaDao pagoPolizaDao) {
		this.pagoPolizaDao = pagoPolizaDao;
	}

	public EleccionFormaPagoController getEleccionFormaPagoController() {
		return eleccionFormaPagoController;
	}

	public void setEleccionFormaPagoController(
			EleccionFormaPagoController eleccionFormaPagoController) {
		this.eleccionFormaPagoController = eleccionFormaPagoController;
	}

	public void setMetodoPagoController(MetodoPagoController metodoPagoController) {
		this.metodoPagoController = metodoPagoController;
	}
	
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setSubentidadMediadoraDao(
			ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}
}