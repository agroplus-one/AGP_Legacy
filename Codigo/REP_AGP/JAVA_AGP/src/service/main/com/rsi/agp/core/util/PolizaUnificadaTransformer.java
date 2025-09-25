package com.rsi.agp.core.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.managers.ICuadroCoberturasManager;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaGanadoTranformer;
import com.rsi.agp.core.webapp.util.CoberturasUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.models.poliza.IPagoPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocioGanado;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

import es.agroseguro.contratacion.Asegurado;
import es.agroseguro.contratacion.Cobertura;
import es.agroseguro.contratacion.Colectivo;
import es.agroseguro.contratacion.Cuenta;
import es.agroseguro.contratacion.Entidad;
import es.agroseguro.contratacion.Mediador;
import es.agroseguro.contratacion.ObjetosAsegurados;
import es.agroseguro.contratacion.Pago;
import es.agroseguro.contratacion.Poliza;
import es.agroseguro.contratacion.PolizaDocument;
import es.agroseguro.contratacion.Tomador;
import es.agroseguro.contratacion.costePoliza.BonificacionRecargo;
import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion;
import es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion;
import es.agroseguro.contratacion.datosVariables.CicloCultivo;
import es.agroseguro.contratacion.datosVariables.CodigoCertificado;
import es.agroseguro.contratacion.datosVariables.CodigoIGP;
import es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos;
import es.agroseguro.contratacion.datosVariables.DaniosCubiertos;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.datosVariables.DenominacionOrigen;
import es.agroseguro.contratacion.datosVariables.Densidad;
import es.agroseguro.contratacion.datosVariables.Destino;
import es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias;
import es.agroseguro.contratacion.datosVariables.Edad;
import es.agroseguro.contratacion.datosVariables.EdadCubierta;
import es.agroseguro.contratacion.datosVariables.EdadEstructura;
import es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias;
import es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias;
import es.agroseguro.contratacion.datosVariables.FechaRecoleccion;
import es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante;
import es.agroseguro.contratacion.datosVariables.Garantizado;
import es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento;
import es.agroseguro.contratacion.datosVariables.MaterialCubierta;
import es.agroseguro.contratacion.datosVariables.MaterialEstructura;
import es.agroseguro.contratacion.datosVariables.MedidaPreventiva;
import es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias;
import es.agroseguro.contratacion.datosVariables.MetrosCuadrados;
import es.agroseguro.contratacion.datosVariables.MetrosLineales;
import es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche;
import es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda;
import es.agroseguro.contratacion.datosVariables.NumeroUnidades;
import es.agroseguro.contratacion.datosVariables.Pendiente;
import es.agroseguro.contratacion.datosVariables.PeriodoGarantias;
import es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado;
import es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia;
import es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable;
import es.agroseguro.contratacion.datosVariables.PracticaCultural;
import es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido;
import es.agroseguro.contratacion.datosVariables.Rotacion;
import es.agroseguro.contratacion.datosVariables.SistemaConduccion;
import es.agroseguro.contratacion.datosVariables.SistemaCultivo;
import es.agroseguro.contratacion.datosVariables.SistemaProduccion;
import es.agroseguro.contratacion.datosVariables.SistemaProteccion;
import es.agroseguro.contratacion.datosVariables.TipoAseguradoGanado;
import es.agroseguro.contratacion.datosVariables.TipoFranquicia;
import es.agroseguro.contratacion.datosVariables.TipoInstalacion;
import es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion;
import es.agroseguro.contratacion.datosVariables.TipoMasa;
import es.agroseguro.contratacion.datosVariables.TipoPlantacion;
import es.agroseguro.contratacion.datosVariables.TipoRendimiento;
import es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela;
import es.agroseguro.contratacion.datosVariables.TipoTerreno;
import es.agroseguro.contratacion.datosVariables.ValorFijo;
import es.agroseguro.contratacion.declaracionSubvenciones.RelacionSocios;
import es.agroseguro.contratacion.declaracionSubvenciones.SeguridadSocial;
import es.agroseguro.contratacion.declaracionSubvenciones.Socio;
import es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada;
import es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas;
import es.agroseguro.contratacion.explotacion.Animales;
import es.agroseguro.contratacion.explotacion.CapitalAsegurado;
import es.agroseguro.contratacion.explotacion.Coordenadas;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion;
import es.agroseguro.contratacion.explotacion.GrupoRaza;
import es.agroseguro.contratacion.parcela.CapitalesAsegurados;
import es.agroseguro.contratacion.parcela.Cosecha;
import es.agroseguro.contratacion.parcela.ParcelaDocument;
import es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela;
import es.agroseguro.iTipos.Ambito;
import es.agroseguro.iTipos.DatosContacto;
import es.agroseguro.iTipos.Direccion;
import es.agroseguro.iTipos.Gastos;
import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.iTipos.SIGPAC;
import es.agroseguro.iTipos.SiNo;

/**
 * Clase para transformar una poliza de base de datos en una poliza para enviar
 * a Agroseguro
 * 
 * @author U028783
 * 
 */
public class PolizaUnificadaTransformer {

	private static final Log logger = LogFactory.getLog(PolizaUnificadaTransformer.class);

	// Fichero de Propiedades
	private static final ResourceBundle bundle_siniestros = ResourceBundle.getBundle("agp_cobro_siniestros");

	/* MODIF TAM (03.06.2019) */
	private static IPagoPolizaDao pagoPolizaDao;
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	private static ClaseManager claseManager;
	private static CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	private static ICuadroCoberturasManager cuadroCoberturasManager;
	private static ISeleccionPolizaDao seleccionPolizaDao;

	private static final String VACIO = "";

	private static final String MODALIDAD_SEGURO_RENOVABLE = "R";
	private static final String MODALIDAD_SEGURO_NO_RENOVABLE = "N";
	
	private static final String DEFAULT_IBAN = "ES0000000000000000000000";
	private static final String FEC_DATA_ERROR_MSG = "La fecha {valor} no es valida para el dato {dato}";
	private static final String NUM_DATA_ERROR_MSG = "El numero {valor} no es valida para el dato {dato}";
	private static final String VAL_DATA_ERROR_MSG = "El valor {valor} no es valida para el dato {dato}";
	private static final String ELEG = " eleg: ";
	private static final String DV_COD_CPTO = " - DVcodCpto: ";
	private static final String DV_VALOR = " DVvalor: ";
	private static final String DV_ELEGIDO = " DVelegido: ";
	private static final String RC = " RC: ";
	private static final String FECHA = "dd/MM/yyyy";

	@SuppressWarnings("unchecked")
	public static PolizaDocument transformar(final com.rsi.agp.dao.tables.poliza.Poliza poliza,
			final com.rsi.agp.dao.tables.poliza.Poliza polizaPpl, final ComparativaPoliza cp,
			final Map<BigDecimal, List<String>> listaDatosVariables, final boolean aplicaReduccionRdto,
			final List<BigDecimal> listaCPM, final List<BigDecimal> codsConceptos, final GenericDao<?> genericDao,
			final List<GruposNegocio> gruposNegocio, final Map<Long, DatosVariables> dvEspecialesExplot,
			final boolean esGanado, final String webServiceToCall, final boolean aplicaDtoRec,
			final Map<Character, ComsPctCalculado> comsPctCalculado) throws ValidacionPolizaException {
		PolizaDocument polizaDoc = PolizaDocument.Factory.newInstance();
		Poliza polizaAS = Poliza.Factory.newInstance();
		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		boolean isRenovable = false;
		boolean esFinanciada = false;

		logger.debug("**@@** PolizaUnificadaTransformer - transformar");

		// Indicar si el modulo se ha elegido como renovable o no renovable
		if (esGanado) {
			if (cp != null && cp.getId() != null && cp.getId().getCodmodulo() != null) {
				for (ModuloPoliza mp : poliza.getModuloPolizas()) {
					if (cp.getId().getCodmodulo().equals(mp.getId().getCodmodulo())
							&& cp.getId().getIdComparativa().equals(mp.getId().getNumComparativa())) {
						if (Integer.valueOf(1).equals(mp.getRenovable())) {
							polizaAS.setModalidadSeguro(MODALIDAD_SEGURO_RENOVABLE);
							isRenovable = true;
						} else {
							polizaAS.setModalidadSeguro(MODALIDAD_SEGURO_NO_RENOVABLE);
							isRenovable = false;
						}
					}
				}
			}
		} else {
			isRenovable = false;
		}

		if (!StringUtils.nullToString(poliza.getReferencia()).equals(VACIO)) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!poliza.getLinea().getCodlinea().toString().equals("303")) {
			// COLECTIVO
			Colectivo c = Colectivo.Factory.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			Tomador tomador = Tomador.Factory.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);
			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);
			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(VACIO)) {
				dc.setEmail(tomadorPoliza.getEmail());
			}
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(VACIO)) {
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			}
			tomador.setDatosContacto(dc);
			polizaAS.setTomador(tomador);
		}
		// ASEGURADO
		Asegurado a = Asegurado.Factory.newInstance();
		a.setNif(poliza.getAsegurado().getNifcif());
		if (poliza.getAsegurado().getTipoidentificacion().equals("CIF")) {
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(poliza.getAsegurado().getRazonsocial());
			a.setRazonSocial(rs);
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(poliza.getAsegurado().getNombre());
			nom.setApellido1(poliza.getAsegurado().getApellido1());
			if (!"".equals(StringUtils.nullToString(poliza.getAsegurado().getApellido2()).trim()))
				nom.setApellido2(poliza.getAsegurado().getApellido2());
			a.setNombreApellidos(nom);
		}
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(poliza.getAsegurado().getBloque());
		dir.setCp(poliza.getAsegurado().getCodpostalstr());
		dir.setEscalera(poliza.getAsegurado().getEscalera());
		if (poliza.getAsegurado().getLocalidad().getNomlocalidad().length() > 30) {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad().substring(0, 30));
		} else {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad());
		}
		dir.setNumero(poliza.getAsegurado().getNumvia());
		dir.setPiso(poliza.getAsegurado().getPiso());
		dir.setProvincia(poliza.getAsegurado().getLocalidad().getProvincia().getCodprovincia().intValue());
		dir.setVia(
				poliza.getAsegurado().getVia().getClave() + " " + poliza.getAsegurado().getDireccion().toUpperCase());
		a.setDireccion(dir);
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(poliza.getAsegurado().getEmail());
		dContacto.setTelefonoFijo(Integer.parseInt(poliza.getAsegurado().getTelefono()));
		if (!StringUtils.nullToString(poliza.getAsegurado().getMovil()).equals(VACIO)) {
			dContacto.setTelefonoMovil(Integer.parseInt(poliza.getAsegurado().getMovil()));
		}
		a.setDatosContacto(dContacto);
		polizaAS.setAsegurado(a);
		// FIN ASEGURADO

		// ENTIDAD
		Entidad entidad = getEntidad(poliza);

		// AMG GASTOS 28/10/2014
		Gastos[] gastos = getGastos(gruposNegocio, poliza.getSetPolizaPctComisiones(), esGanado, poliza, aplicaDtoRec,
				comsPctCalculado);

		entidad.setGastosArray(gastos);
		// FIN GASTOS

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD
		// COBERTURAS
		Cobertura cobertura = Cobertura.Factory.newInstance();

		/*** Pet. 57626 ** MODIF TAM (29.05.2020) *** Inicio ***/
		if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
			cobertura.setModulo(String.format("%-5s", cp.getId().getCodmodulo()));

			DatosVariables datosVariables = getDatosVariablesCobertura(poliza, cp, aplicaReduccionRdto, listaCPM);
			cobertura.setDatosVariables(datosVariables);
		} else {
			cobertura.setModulo(String.format("%-5s", poliza.getCodmodulo()));

			DatosVariables datosVariables = getDatosVariablesCobertCpl(poliza, poliza.getPolizaPpal(), cp,
					aplicaReduccionRdto, listaCPM);
			cobertura.setDatosVariables(datosVariables);
		}

		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS
		// OBJETOS ASEGURADOS
		ObjetosAsegurados objAseg = ObjetosAsegurados.Factory.newInstance();

		/* Pet. 57626 ** MODIF TAM (29/0472020) ** Inicio **/
		/*
		 * Incluimos validacivalidacionn para guardar en el objeto asegurado las
		 * explotaciones o las Parcelas dependiendo de si se trata de una poliza de
		 * Ganado o Agricola
		 */
		org.w3c.dom.Node importedNode;
		if (esGanado) {

			List<ExplotacionDocument> explotacionesCol = getExplotaciones(poliza.getExplotacions(),
					poliza.getLinea().getLineaseguroid(), genericDao, dvEspecialesExplot);

			/* Pet. 70105 ** MODIF TAM (03.03.2021) ** Inicio */
			/* Creamos un nuevo comparator para ordenar las parcelas para el envio al xml */
			Collections.sort(explotacionesCol, new ExplotacionesComparator());

			for (ExplotacionDocument explotacionDoc : explotacionesCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument()
						.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
				objAseg.getDomNode().appendChild(importedNode);
			}
		} else {

			/*** Pet. 57626 ** MODIF TAM (29.05.2020) *** Inicio ***/

			logger.debug("**@@** Antes de obtener Parcelas");

			if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
				/*************************************/
				/** PARCELAS DE POLIZAS PRINCIPALES **/

				List<ParcelaDocument> parcelasCol = getParcelas(poliza, cp, listaDatosVariables, aplicaReduccionRdto,
						codsConceptos, webServiceToCall);

				/* Pet. 70105 ** MODIF TAM (03.03.2021) ** Inicio */
				/* Creamos un nuevo comparator para ordenar las parcelas para el envio al xml */
				Collections.sort(parcelasCol, new ParcelasComparator());

				for (ParcelaDocument parcelaDoc : parcelasCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(parcelaDoc.getParcela().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);

				}
			} else {
				/*****************************************/
				/** PARCELAS DE POLIZAS COMPLEMENTARIAS **/

				logger.debug("**@@** PolizaUnificadaTransformer - Obtenemos las Parcelas de Complementarias");
				Boolean esValid = false;

				Boolean esCalcular = false;
				if (webServiceToCall.equals(Constants.WS_CALCULO)) {
					esCalcular = true;
				}
				List<ParcelaDocument> parcelasCol = getParcelasCpl(poliza, cp, listaDatosVariables, aplicaReduccionRdto,
						codsConceptos, esValid, esCalcular);

				/* Pet. 70105 ** MODIF TAM (03.03.2021) ** Inicio */
				/* Creamos un nuevo comparator para ordenar las parcelas para el envio al xml */
				Collections.sort(parcelasCol, new ParcelasComparator());
				/* Pet. 70105 ** MODIF TAM (03.03.2021) ** Fin */

				for (ParcelaDocument parcelaDoc : parcelasCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(parcelaDoc.getParcela().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}

			}

		}
		polizaAS.setObjetosAsegurados(objAseg);

		// FIN OBJETOS ASEGURADOS

		// INICIO COSTE POLIZA
		Long idComparativa = null;
		if (cp != null && cp.getId() != null && cp.getId().getIdComparativa() != null)
			idComparativa = cp.getId().getIdComparativa();

		List<DistribucionCoste2015> distCostes = (List<DistribucionCoste2015>) genericDao
				.getObjects(DistribucionCoste2015.class, "poliza.idpoliza", poliza.getIdpoliza());

		/* Pet. 57626 ** MODIF TAM (13/05/2020) ** Inicio */
		/* Recuperamos la Distribucion de Coste para las agricolas */

		if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
			/* Obtenemos costes para polizas principales */
			CostePoliza coste = getCoste(poliza, gruposNegocio, idComparativa, cp);
			polizaAS.setCostePoliza(coste);
		} else {
			/* Obtenemos costes para polizas Complementarias */
			CostePoliza costeCpl = getCosteCpl(poliza, gruposNegocio, idComparativa, cp);
			polizaAS.setCostePoliza(costeCpl);
		}

		// coste.setDistribucionCoste2015(distCoste1);
		/* Pet. 57626 ** MODIF TAM (21.07.2020) ** Resolucion Incidencia - Inicio */
		/*
		 * Solo obtenemos el valor de esFinanciada para las polizas Principales, para
		 * las complementarias, aunque lo enviamos en getPagoCpl, dentro del metodo no
		 * se utiliza
		 */
		if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
			esFinanciada = esComparativaFinanciada(cp, distCostes);
		}
		// FIN COSTE POLIZA

		boolean lineaSup2021 = false;
		try {
			lineaSup2021 = pagoPolizaDao.lineaContratacion2021(poliza.getLinea().getCodplan(),
					poliza.getLinea().getCodlinea(), poliza.getLinea().isLineaGanado());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		// PAGO
		if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
			es.agroseguro.contratacion.Pago pago = lineaSup2021
					? getPago2021(poliza, cp, esFinanciada, webServiceToCall)
					: getPago(poliza, cp, isRenovable, esFinanciada, webServiceToCall);
			polizaAS.setPago(pago);
		} else {
			es.agroseguro.contratacion.Pago pagoCpl = lineaSup2021
					? getPago2021(poliza, cp, esFinanciada, webServiceToCall)
					: getPagoCpl(poliza, cp, esFinanciada, webServiceToCall);
			polizaAS.setPago(pagoCpl);
		}
		// FIN PAGO

		// COBRO SINIESTROS
		boolean modExentoCuentaCobroSiniestros = false;
		String[] auxArr = bundle_siniestros.getString("mods.exentos.envio").split(",");
		String auxStr = poliza.getLinea().getCodlinea() + "|"
				+ ((poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) ? cp.getId().getCodmodulo()
						: poliza.getCodmodulo());
		logger.debug("Verificando si la poliza esta exenta envio siniestros: " + auxStr);
		for (String aux : auxArr) {
			logger.debug("Verificando contra linea exenta envio siniestros: " + aux);
			if (auxStr.equalsIgnoreCase(aux.trim())) {
				logger.debug("Poliza exenta!!!!");
				modExentoCuentaCobroSiniestros = true;
				break;
			}
		}
		logger.debug("lineaSup2021: " + lineaSup2021);
		logger.debug("modExentoCuentaCobroSiniestros: " + modExentoCuentaCobroSiniestros);
		logger.debug(
				"Se debe incluir la cuenta cobro siniestros: " + (lineaSup2021 && !modExentoCuentaCobroSiniestros));
		if (lineaSup2021 && !modExentoCuentaCobroSiniestros
				&& (poliza.getPagoPolizas() != null && !poliza.getPagoPolizas().isEmpty())) {
			for (PagoPoliza pp : poliza.getPagoPolizas()) {				
				// SE ENVIA SIEMPRE EN CARGO EN CUENTA O PAGO MANUAL
				// EN DOMICILIACION SE ENVIA SOLO SI EL DESTINATARIO ES DIFERENTE AL ASEGURADO
				if (pp != null
						&& (ArrayUtils.contains(new BigDecimal[] { Constants.CARGO_EN_CUENTA, Constants.PAGO_MANUAL },
								pp.getTipoPago()) || !Character.valueOf('A').equals(pp.getDestinatarioDomiciliacion()))
						&& (!StringUtils.isNullOrEmpty(pp.getIban2())
								&& !StringUtils.isNullOrEmpty(pp.getCccbanco2()))) {
					es.agroseguro.contratacion.CuentaCobroSiniestros cuentaCobroSiniestros = es.agroseguro.contratacion.CuentaCobroSiniestros.Factory
							.newInstance();
					cuentaCobroSiniestros.setIban(pp.getIban2() + pp.getCccbanco2());
					polizaAS.setCuentaCobroSiniestros(cuentaCobroSiniestros);
					break;
				}
			}
		}
		// FIN COBRO SINIESTROS

		// SUBVENCIONES DECLARADAS
		SubvencionesDeclaradas subvDecl = SubvencionesDeclaradas.Factory.newInstance();
		boolean aniadirSeguridadSocial = false;

		ArrayList<SubvencionDeclarada> lstSubv = new ArrayList<SubvencionDeclarada>();
		List<BigDecimal> codSubvsEnesa = new ArrayList<BigDecimal>();
		List<BigDecimal> codSubvsCCAA = new ArrayList<BigDecimal>();

		boolean tieneSubv03 = false;
		
		/* Pet. 57626 ** MODIF TAM (29/04/2020) ** Inicio */
		if (!esGanado) {

			if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {

				/* POLIZAS agricolaS */
				for (SubAseguradoENESA senesa : poliza.getSubAseguradoENESAs()) {
					
					logger.debug("Procesando subvencion " + senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa());
					tieneSubv03 |= Constants.CARACT_ASEGURADO_PERSONA_JURIDICA.equals(senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa());
					
					if (!codSubvsEnesa
							.contains(senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
						SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
						sd.setTipo(
								senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().intValue());
						lstSubv.add(sd);
						if (senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
								.compareTo(Constants.SUBVENCION20) == 0
								|| senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
										.compareTo(Constants.SUBVENCION30) == 0) {
							aniadirSeguridadSocial = true;
						}
						codSubvsEnesa.add(senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa());
					}
				}

				for (SubAseguradoCCAA sccaa : poliza.getSubAseguradoCCAAs()) {
					if (!codSubvsCCAA
							.contains(sccaa.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa())) {
						SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
						sd.setTipo(sccaa.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa().intValue());
						lstSubv.add(sd);
						if (sccaa.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa()
								.compareTo(Constants.SUBVENCION20) == 0
								|| sccaa.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa()
										.compareTo(Constants.SUBVENCION30) == 0) {
							aniadirSeguridadSocial = true;
						}
						codSubvsCCAA.add(sccaa.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa());
					}
				}

			} else {

				/*****/
				// SUBVENCIONES DECLARADAS
				com.rsi.agp.dao.tables.poliza.Poliza polizaPpal = poliza.getPolizaPpal();

				if (!StringUtils.nullToString(polizaPpal.getAsegurado().getNumsegsocial()).equals("")) {
					SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
					segSocial.setProvincia(
							Integer.parseInt(polizaPpal.getAsegurado().getNumsegsocial().substring(0, 2)));
					segSocial.setNumero(Integer.parseInt(polizaPpal.getAsegurado().getNumsegsocial().substring(2, 10)));
					segSocial.setCodigo(polizaPpal.getAsegurado().getNumsegsocial().substring(10));
					if (!StringUtils.nullToString(polizaPpal.getAsegurado().getRegimensegsocial()).equals(""))
						segSocial.setRegimen(Short.parseShort(polizaPpal.getAsegurado().getRegimensegsocial() + ""));
					subvDecl.setSeguridadSocial(segSocial);
				}

				for (SubAseguradoENESA senesa : polizaPpal.getSubAseguradoENESAs()) {
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(senesa.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().intValue());
					lstSubv.add(sd);
				}
				for (SubAseguradoCCAA sccaa : polizaPpal.getSubAseguradoCCAAs()) {
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(sccaa.getSubvencionCCAA().getTipoSubvencionCCAA().getCodtiposubvccaa().intValue());

					lstSubv.add(sd);
				}
				if (lstSubv.size() > 0) {
					subvDecl.setSubvencionDeclaradaArray(lstSubv.toArray(new SubvencionDeclarada[lstSubv.size()]));
					polizaAS.setSubvencionesDeclaradas(subvDecl);
				}
				// FIN SUBVENCIONES DECLARADAS

			}

		} else {

			/* POLIZAS GANADO */
			for (SubAseguradoENESAGanado senesag : poliza.getSubAseguradoENESAGanados()) {
				if (!codSubvsEnesa
						.contains(senesag.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(senesag.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
							.intValue());
					lstSubv.add(sd);
					if (senesag.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
							.compareTo(Constants.SUBVENCION20) == 0
							|| senesag.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
									.compareTo(Constants.SUBVENCION30) == 0) {
						aniadirSeguridadSocial = true;
					}
					codSubvsEnesa
							.add(senesag.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa());
				}
			}
			for (SubAseguradoCCAAGanado sccaag : poliza.getSubAseguradoCCAAGanados()) {
				if (!codSubvsCCAA
						.contains(sccaag.getSubvencionCCAAGanado().getTipoSubvencionCCAA().getCodtiposubvccaa())) {
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(
							sccaag.getSubvencionCCAAGanado().getTipoSubvencionCCAA().getCodtiposubvccaa().intValue());
					lstSubv.add(sd);
					if (sccaag.getSubvencionCCAAGanado().getTipoSubvencionCCAA().getCodtiposubvccaa()
							.compareTo(Constants.SUBVENCION20) == 0
							|| sccaag.getSubvencionCCAAGanado().getTipoSubvencionCCAA().getCodtiposubvccaa()
									.compareTo(Constants.SUBVENCION30) == 0) {
						aniadirSeguridadSocial = true;
					}
					codSubvsCCAA.add(sccaag.getSubvencionCCAAGanado().getTipoSubvencionCCAA().getCodtiposubvccaa());
				}
			}
		}
		/* Pet. 57626 ** MODIF TAM (29/04/2020) ** Fin */

		if (!StringUtils.nullToString(poliza.getAsegurado().getNumsegsocial()).equals(VACIO)
				&& aniadirSeguridadSocial) {
			SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
			segSocial.setProvincia(Integer.parseInt(poliza.getAsegurado().getNumsegsocial().substring(0, 2)));
			segSocial.setNumero(Integer.parseInt(poliza.getAsegurado().getNumsegsocial().substring(2, 10)));
			segSocial.setCodigo(poliza.getAsegurado().getNumsegsocial().substring(10));
			if (!StringUtils.nullToString(poliza.getAsegurado().getRegimensegsocial()).equals(VACIO))
				segSocial.setRegimen(Short.parseShort(poliza.getAsegurado().getRegimensegsocial() + VACIO));
			subvDecl.setSeguridadSocial(segSocial);
		}
		if (lstSubv.size() > 0) {
			subvDecl.setSubvencionDeclaradaArray(lstSubv.toArray(new SubvencionDeclarada[lstSubv.size()]));
			polizaAS.setSubvencionesDeclaradas(subvDecl);
		}
		// FIN SUBVENCIONES DECLARADAS
		
		logger.debug("tieneSubv03 --> " + tieneSubv03);

		List<SocioId> lstSociosConSubvencion = new ArrayList<SocioId>();
		// SOCIOS
		// Solo se anahden si el asegurado tiene el tipo de subvencion "3 - Caract. Asegurado Persona Juridica"
		if (tieneSubv03 && (poliza.getSubvencionSocios().size() > 0 || (polizaPpl != null && polizaPpl.getSubvencionSocios().size() > 0))) {
			RelacionSocios rs = RelacionSocios.Factory.newInstance();
			List<Socio> lstSocios = new ArrayList<Socio>();

			SubvencionesDeclaradas sds = SubvencionesDeclaradas.Factory.newInstance();
			List<SubvencionDeclarada> lstSds = new ArrayList<SubvencionDeclarada>();

			Socio s = Socio.Factory.newInstance();
			boolean tieneATP = false;
			Set<SubvencionSocio> subvencionSocios = polizaPpl == null ? poliza.getSubvencionSocios()
					: polizaPpl.getSubvencionSocios();
			for (SubvencionSocio ss : subvencionSocios) {
				tieneATP = false;
				if (s.getNif() == null || (s.getNif() != null && !s.getNif().equals(ss.getSocio().getId().getNif()))) {
					lstSociosConSubvencion.add(ss.getSocio().getId());
					// Vuelco las subvenciones, se las asigno al socio, Anado el
					// socio a la lista y "resetear el socio"
					if (s.getNif() != null) {
						sds.setSubvencionDeclaradaArray(lstSds.toArray(new SubvencionDeclarada[lstSds.size()]));
						s.setSubvencionesDeclaradas(sds);
						lstSocios.add(s);
					}

					s = Socio.Factory.newInstance();

					s.setNif(ss.getSocio().getId().getNif());
					// El numero sera el orden que ocupa el socio en la tabla de
					// polizas socios
					s.setNumero(getOrdenPolizaSocio(polizaPpl == null ? poliza : polizaPpl, ss));

					if (!ss.getSocio().getTipoidentificacion().equals("CIF")) {
						NombreApellidos na = NombreApellidos.Factory.newInstance();
						na.setNombre(ss.getSocio().getNombre());
						na.setApellido1(ss.getSocio().getApellido1());
						if (!"".equals(StringUtils.nullToString(ss.getSocio().getApellido2()).trim()))
							na.setApellido2(ss.getSocio().getApellido2());
						s.setNombreApellidos(na);
					} else {
						RazonSocial rsoc = RazonSocial.Factory.newInstance();
						rsoc.setRazonSocial(ss.getSocio().getRazonsocial());
						s.setRazonSocial(rsoc);
					}

					sds = SubvencionesDeclaradas.Factory.newInstance();
					lstSds = new ArrayList<SubvencionDeclarada>();
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(Integer
							.parseInt(ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa() + VACIO));
					lstSds.add(sd);
					if (ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
							.compareTo(Constants.SUBVENCION20) == 0)
						tieneATP = true;
				} else {
					// le Anado las subvenciones
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(Integer
							.parseInt(ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa() + VACIO));
					lstSds.add(sd);
					if (ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
							.compareTo(Constants.SUBVENCION20) == 0)
						tieneATP = true;
				}
				if (!StringUtils.nullToString(ss.getSocio().getNumsegsocial()).equals(VACIO) && tieneATP) {
					SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
					segSocial.setProvincia(Integer.parseInt(ss.getSocio().getNumsegsocial().substring(0, 2)));
					segSocial.setNumero(Integer.parseInt(ss.getSocio().getNumsegsocial().substring(2, 10)));
					segSocial.setCodigo(ss.getSocio().getNumsegsocial().substring(10));
					if (!StringUtils.nullToString(ss.getSocio().getRegimensegsocial()).equals(VACIO)) {
						segSocial.setRegimen(Short.parseShort(ss.getSocio().getRegimensegsocial() + VACIO));
					}
					sds.setSeguridadSocial(segSocial);
				}
			}

			// Anado el de la ultima iteracion
			sds.setSubvencionDeclaradaArray(lstSds.toArray(new SubvencionDeclarada[lstSds.size()]));
			s.setSubvencionesDeclaradas(sds);
			lstSocios.add(s);

			rs.setSocioArray(lstSocios.toArray(new Socio[lstSocios.size()]));

			polizaAS.setRelacionSocios(rs);

		}

		// Anadimos los socios ganado si el asegurado tiene el tipo de subvencion "3 -
		// Caract. Asegurado Persona Juridica"
		if (poliza.getSubAseguradoENESAGanados().size() > 0) {
			for (SubAseguradoENESAGanado saeg : poliza.getSubAseguradoENESAGanados()) {
				if (saeg.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
						.equals(Constants.CARACT_ASEGURADO_PERSONA_JURIDICA)) {
					// sociosGanado
					if (poliza.getPolizaSocios().size() > 0) {
						for (com.rsi.agp.dao.tables.poliza.PolizaSocio ps : poliza.getPolizaSocios()) {
							if (!lstSociosConSubvencion.contains(ps.getSocio().getId())) {
								Socio soc = Socio.Factory.newInstance();
								soc.setNif(ps.getSocio().getId().getNif());
								if (!ps.getSocio().getTipoidentificacion().equals("CIF")) {
									NombreApellidos na = NombreApellidos.Factory.newInstance();
									na.setNombre(ps.getSocio().getNombre());
									na.setApellido1(ps.getSocio().getApellido1());
									if (!"".equals(StringUtils.nullToString(ps.getSocio().getApellido2()).trim()))
										na.setApellido2(ps.getSocio().getApellido2());
									soc.setNombreApellidos(na);
								} else {
									RazonSocial rsoc = RazonSocial.Factory.newInstance();
									rsoc.setRazonSocial(ps.getSocio().getRazonsocial());
									soc.setRazonSocial(rsoc);
								}

								SubvencionesDeclaradas sds = rellenaSubvencionesDeclaradas(ps);
								if (sds != null)
									soc.setSubvencionesDeclaradas(sds);

								List<Socio> lstSoc = new ArrayList<Socio>();
								if (polizaAS.getRelacionSocios() != null) {
									lstSoc = Arrays.asList(polizaAS.getRelacionSocios().getSocioArray());
								} else {
									polizaAS.setRelacionSocios(RelacionSocios.Factory.newInstance());
								}
								ArrayList<Socio> arrSoc = new ArrayList<Socio>(lstSoc);

								// El numero sera el orden que ocupa el socio en la tabla de
								// polizas socios
								soc.setNumero(ps.getOrden() != null ? ps.getOrden().intValue() : 1);
								arrSoc.add(soc);

								polizaAS.getRelacionSocios().setSocioArray(arrSoc.toArray(new Socio[arrSoc.size()]));

							}
						}
					}
					break;
					// FIN sociosGanado
				}
			}
		}

		/* ESC-11775 DNF 21/12/2020 */
		/* Se anhaden los socios aunque no tengan subvenciones, para agricolas */
		// Solo si el asegurado tiene el tipo de subvencion "3 - Caract. Asegurado Persona Juridica"
		if (!esGanado && tieneSubv03) {
			
			// si la poliza es principal ...
			logger.debug("REferencia de la poliza : " + poliza.getTipoReferencia());
			if (poliza.getTipoReferencia().equals('P')) {

				// anhadimos los socios sin subvenciones
				if (poliza.getPolizaSocios().size() > 0) {
					for (com.rsi.agp.dao.tables.poliza.PolizaSocio ps : poliza.getPolizaSocios()) {
						if (!lstSociosConSubvencion.contains(ps.getSocio().getId())) {
							Socio soc = Socio.Factory.newInstance();
							soc.setNif(ps.getSocio().getId().getNif());
							if (!ps.getSocio().getTipoidentificacion().equals("CIF")) {
								NombreApellidos na = NombreApellidos.Factory.newInstance();
								na.setNombre(ps.getSocio().getNombre());
								na.setApellido1(ps.getSocio().getApellido1());
								if (!"".equals(StringUtils.nullToString(ps.getSocio().getApellido2()).trim()))
									na.setApellido2(ps.getSocio().getApellido2());
								soc.setNombreApellidos(na);
							} else {
								RazonSocial rsoc = RazonSocial.Factory.newInstance();
								rsoc.setRazonSocial(ps.getSocio().getRazonsocial());
								soc.setRazonSocial(rsoc);
							}

							List<Socio> lstSoc = new ArrayList<Socio>();
							if (polizaAS.getRelacionSocios() != null) {
								lstSoc = Arrays.asList(polizaAS.getRelacionSocios().getSocioArray());
							} else {
								polizaAS.setRelacionSocios(RelacionSocios.Factory.newInstance());
							}
							ArrayList<Socio> arrSoc = new ArrayList<Socio>(lstSoc);

							// El numero sera el orden que ocupa el socio en la tabla de
							// polizas socios
							soc.setNumero(ps.getOrden() != null ? ps.getOrden().intValue() : 1);
							arrSoc.add(soc);

							polizaAS.getRelacionSocios().setSocioArray(arrSoc.toArray(new Socio[arrSoc.size()]));

						}
					}
				}

			} else {
				// si es complementaria, tengo que coger los datos de la poliza principal
				// polizaPpl

				// anhadimos los socios sin subvenciones
				if (polizaPpl.getPolizaSocios().size() > 0) {
					for (com.rsi.agp.dao.tables.poliza.PolizaSocio ps : polizaPpl.getPolizaSocios()) {
						if (!lstSociosConSubvencion.contains(ps.getSocio().getId())) {
							Socio soc = Socio.Factory.newInstance();
							soc.setNif(ps.getSocio().getId().getNif());
							if (!ps.getSocio().getTipoidentificacion().equals("CIF")) {
								NombreApellidos na = NombreApellidos.Factory.newInstance();
								na.setNombre(ps.getSocio().getNombre());
								na.setApellido1(ps.getSocio().getApellido1());
								if (!"".equals(StringUtils.nullToString(ps.getSocio().getApellido2()).trim()))
									na.setApellido2(ps.getSocio().getApellido2());
								soc.setNombreApellidos(na);
							} else {
								RazonSocial rsoc = RazonSocial.Factory.newInstance();
								rsoc.setRazonSocial(ps.getSocio().getRazonsocial());
								soc.setRazonSocial(rsoc);
							}

							List<Socio> lstSoc = new ArrayList<Socio>();
							if (polizaAS.getRelacionSocios() != null) {
								lstSoc = Arrays.asList(polizaAS.getRelacionSocios().getSocioArray());
							} else {
								polizaAS.setRelacionSocios(RelacionSocios.Factory.newInstance());
							}
							ArrayList<Socio> arrSoc = new ArrayList<Socio>(lstSoc);

							// El numero sera el orden que ocupa el socio en la tabla de
							// polizas socios
							soc.setNumero(ps.getOrden() != null ? ps.getOrden().intValue() : 1);
							arrSoc.add(soc);

							polizaAS.getRelacionSocios().setSocioArray(arrSoc.toArray(new Socio[arrSoc.size()]));

						}
					}
				}

			}

		}
		/* fin ESC-11775 DNF 21/12/2020 */

		// FIN DE SOCIOS
		polizaDoc.setPoliza(polizaAS);
		return polizaDoc;
	}

	private static es.agroseguro.contratacion.Pago getPago2021(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, boolean esFinanciada, String webServiceToCall) {
		es.agroseguro.contratacion.Pago pago = es.agroseguro.contratacion.Pago.Factory.newInstance();
		logger.debug("PolizaUnificadaTransformer - getPago2021");
		logger.debug("PolizaUnificadaTransformer - valor de webServiceToCall:" + webServiceToCall);
		if (poliza.getPagoPolizas() != null && !poliza.getPagoPolizas().isEmpty()) {
			pago.setForma(
					String.valueOf(esFinanciada ? Constants.FORMA_PAGO_FINANCIADO : Constants.FORMA_PAGO_ALCONTADO));
			PagoPoliza pp = poliza.getPagoPolizas().iterator().next();
			if (Constants.DOMICILIACION_AGRO.equals(pp.getTipoPago())) {
				pago.setDomiciliado("T");
				es.agroseguro.contratacion.Cuenta cuenta = es.agroseguro.contratacion.Cuenta.Factory.newInstance();
				cuenta.setIban(pp.getIban() + pp.getCccbanco());
				cuenta.setDestinatario(String.valueOf(pp.getDestinatarioDomiciliacion()));
				if (Character.valueOf('O').equals(pp.getDestinatarioDomiciliacion())) {
					cuenta.setTitular(pp.getTitularCuenta());
				}
				pago.setCuenta(cuenta);
			} else {
				pago.setDomiciliado("N");
			}
			pago.setImporte(pp.getImporte() == null ? new BigDecimal(0) : pp.getImporte());
		}
		
		if (poliza.getDatosAval() != null) {
			pago.setFraccionamiento(getFraccionamiento(poliza, cp, poliza.getDatosAval().getImporteAval(),
					poliza.getDatosAval().getNumeroAval()));
		
		/* ESC-13542 ** MODIF TAM (26.04.2021) ** Inicio */	
		} else {
			if (esFinanciada) {
				BigDecimal importeAval = new BigDecimal(1);
				BigDecimal numAval = new BigDecimal(1);
				es.agroseguro.contratacion.Fraccionamiento fraccionamiento;
				fraccionamiento = getFraccionamiento(poliza, cp, importeAval, numAval);
				pago.setFraccionamiento(fraccionamiento);
				if ((new BigDecimal(1).equals(importeAval) && new BigDecimal(1).equals(numAval))
						&& (null != pago.getCuenta() && "".equals(pago.getCuenta().getIban()))) {
					pago.getCuenta().setIban(DEFAULT_IBAN);
				}
			}
		}
		/* ESC-13542 ** MODIF TAM (26.04.2021) ** Fin */
		
		return pago;
	}

	private static es.agroseguro.contratacion.Pago getPago(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, boolean isRenovable, boolean esFinanciada, String webServiceToCall) {

		logger.debug("PolizaUnificadaTransformer - getPago");
		logger.debug("PolizaUnificadaTransformer - valor de webServiceToCall:" + webServiceToCall);

		boolean esGanado = poliza.getLinea().isLineaGanado();

		Pago pago = Pago.Factory.newInstance();
		if (esGanado) {

			logger.debug("PolizaUnificadaTransformer - Formateamos el Pago de Ganado");
			boolean fechaPagoInsertada = false;

			/* MODIF TAM (03.06.2019) ** Inicio */
			boolean lineaContrSup2019 = false;
			boolean pagoDomiciliado = false;

			BigDecimal codPlanPol = poliza.getLinea().getCodplan();
			BigDecimal codLineaPol = poliza.getLinea().getCodlinea();
			/* MODIF TAM (03.06.2019) ** Fin */

			if (esFinanciada) {
				pago.setCuenta(obtenerCuenta(poliza, true));
			}

			/* MODIF TAM (03.06.2019) */
			try {
				lineaContrSup2019 = pagoPolizaDao.lineaContratacion2019(codPlanPol, codLineaPol,
						poliza.getLinea().isLineaGanado());
				logger.debug("Valor de isLineaGanado:" + poliza.getLinea().isLineaGanado());
				logger.debug("var lineaContrSup2019: " + lineaContrSup2019);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			/* MODIF TAM (03.06.2019) */

			boolean pagoManual = false;
			// DNF 17 Junio 2019
			boolean tienePago = false;

			BigDecimal importePago = new BigDecimal(0);

			for (PagoPoliza pp : poliza.getPagoPolizas()) {
				if (pp.getTipoPago().compareTo(Constants.PAGO_MANUAL) == 0 && pp.getBanco() != null) {
					StringBuilder banco = new StringBuilder(pp.getBanco().toString());
					if (banco.length() < 4) {
						while (banco.length() < 4) {
							banco.insert(0, "0");
						}
					}
					pago.setBanco(banco.toString());
					pagoManual = true;
					// si es pago manual se inserta la fecha que nos venga en el pago
					if (pp.getFecha() != null) {
						pago.setFecha(DateUtil.date2Calendar(pp.getFecha()));
						fechaPagoInsertada = true;
					}
				}

				logger.debug("Valor de TipoPago:" + pp.getTipoPago());

				/* Pet. 57625 ** MODIF (22.02.2019) ** Inicio */
				if (pp.getTipoPago().compareTo(Constants.DOMICILIACION_AGRO) == 0 && lineaContrSup2019) {
					pagoDomiciliado = true;
				} else {
					pagoDomiciliado = false;
				}

				logger.debug("Valor de pagoDomiciliado:" + pagoDomiciliado);
				/* Pet. 57625 ** MODIF (22.02.2019) ** Fin */

				if (pp.getEnvioIbanAgro() != null && pp.getEnvioIbanAgro().equals('S')) {
					// pago.setDomiciliado("S"); //COMENTADO POR DNF 17 JUNIO 2019
					Cuenta cuenta = Cuenta.Factory.newInstance();
					cuenta.setIban(pp.getIban() + pp.getCccbanco());
					cuenta.setDestinatario(pp.getDestinatarioDomiciliacion().toString());
					if (pp.getDestinatarioDomiciliacion().equals('O'))
						cuenta.setTitular(pp.getTitularCuenta());
					pago.setCuenta(cuenta);
				}
				importePago = pp.getImporte() != null ? pp.getImporte() : new BigDecimal(0);

				// DNF 17 Junio 2019
				tienePago = true;
			}
			if (!pagoManual && !pagoDomiciliado) {
				if (poliza.getColectivo().getIsCRM() != null && poliza.getColectivo().getIsCRM() > 0) {
					pago.setBanco("3058");
				} else {
					pago.setBanco("0198");
				}
			}

			// Se indica la forma de pago dependiendo si se fracciona la poliza o no
			pago.setForma(
					String.valueOf(esFinanciada ? Constants.FORMA_PAGO_FINANCIADO : Constants.FORMA_PAGO_ALCONTADO));
			pago.setImporte(importePago);

			if (null != poliza.getDatosAval()) {
				es.agroseguro.contratacion.Fraccionamiento fraccionamiento;
				fraccionamiento = getFraccionamiento(poliza, cp, poliza.getDatosAval().getImporteAval(),
						poliza.getDatosAval().getNumeroAval());
				pago.setFraccionamiento(fraccionamiento);
				if ((new BigDecimal(1).equals(poliza.getDatosAval().getImporteAval())
						&& new BigDecimal(1).equals(poliza.getDatosAval().getNumeroAval()))
						&& (null != pago.getCuenta() && "".equals(pago.getCuenta().getIban()))) {
					pago.getCuenta().setIban(DEFAULT_IBAN);
				}
			} else {
				if (esFinanciada) {
					BigDecimal importeAval = new BigDecimal(1);
					BigDecimal numAval = new BigDecimal(1);
					es.agroseguro.contratacion.Fraccionamiento fraccionamiento;
					fraccionamiento = getFraccionamiento(poliza, cp, importeAval, numAval);
					pago.setFraccionamiento(fraccionamiento);
					if ((new BigDecimal(1).equals(importeAval) && new BigDecimal(1).equals(numAval))
							&& (null != pago.getCuenta() && "".equals(pago.getCuenta().getIban()))) {
						pago.getCuenta().setIban(DEFAULT_IBAN);
					}
				}
			}

			if (!pagoDomiciliado && !fechaPagoInsertada) {
				if (poliza.getFechaPago() != null) {
					pago.setFecha(DateUtil.date2Calendar(poliza.getFechaPago()));
				} else {
					pago.setFecha(new GregorianCalendar());
				}
			}

			logger.debug("**@@** Antes de asignar valor de Domiciliado");
			logger.debug("Valor de tienePago: " + tienePago + " Valor de isRenovable: " + isRenovable
					+ " Valor de esFinanciada:" + esFinanciada);
			logger.debug("Valor de pagoDomiciliado: " + pagoDomiciliado);

			if (tienePago) {
				if (isRenovable) {
					pago.setDomiciliado("S");
				} else {
					if (esFinanciada) {
						pago.setDomiciliado("S");
					} else {
						pago.setDomiciliado("N");
					}
				}
				for (PagoPoliza pp : poliza.getPagoPolizas()) {

					if (pp.getEnvioIbanAgro() != null && pp.getEnvioIbanAgro().equals('S')) {
						pago.setDomiciliado("S");
					}
				}
				if (pagoDomiciliado) {
					logger.debug("Comprobamos que se ha marcado la forma de Pago Domiciliacion en Agro en Ganado");
					pago.setDomiciliado("T");
				}

			} else {
				/* Pet. 57626 ** MODIF TAM (05.06.2020) */
				/*
				 * Si es principal y renovable se enviara la etiqueta 'domiciliado' con valor T
				 */
				if (isRenovable && poliza.getTipoReferencia().equals('P')) {
					logger.debug("Entrampor por Ganado Renovable y Poliza Principal");
					logger.debug("Asignamos Domiciliado T y quitamos Banco y Fecha");

					pago.setDomiciliado("T");
					pago.unsetBanco();
					pago.unsetFecha();

					/*
					 * Si es renovable y poliza Principal de Ganado se envia por solicitud de RGA el
					 * valor de la cuenta y el destinatario
					 */
					logger.debug("GANADO RENOVABLE Y PRINCIPAL");

					if (webServiceToCall.equals(Constants.WS_VALIDACION)) {

						logger.debug("Entramos por Sw Validacion");
						logger.debug("Obtenemos datos de la cuenta del asegurado");
						pago.setCuenta(obtenerDatCuentaAseg(poliza));

						/* Si tiene datos de pago que asigne los datos de la cuenta de pago */
						for (PagoPoliza pp : poliza.getPagoPolizas()) {
							Cuenta cuentaG = pago.getCuenta();
							if (pp.getDestinatarioDomiciliacion() != null) {
								cuentaG.setDestinatario(pp.getDestinatarioDomiciliacion().toString());
								if (pp.getDestinatarioDomiciliacion().equals('O'))
									cuentaG.setTitular(pp.getTitularCuenta());
							}
							pago.setCuenta(cuentaG);
							logger.debug("Asignamos destinatario: " + pago.getCuenta().getDestinatario());
							logger.debug("Asignamos titula: " + pago.getCuenta().getTitular());
						}

					}
				} else {

					pago.setDomiciliado("N");
				}

			}
			/* Pet. 57626 ** MODIF TAM (29.06.2020) ** Inicio */
		} else {
			/* FORMATEAMOS EL PAGO PARA LAS polizaS agricolaS */
			logger.debug("PolizaUnificadaTransformer - Formateamos el Pago de Agricolas");

			/////////////////////////// PAGO
			/////////////////////////// ////////////////////////////////////////////////////////////////////////////

			boolean permiteEnvIban = false;
			boolean lineaContrSup2019 = false;

			BigDecimal codPlan_pol = poliza.getLinea().getCodplan();
			BigDecimal codLinea_pol = poliza.getLinea().getCodlinea();

			try {
				permiteEnvIban = pagoPolizaDao.polizaAgrPermiteEnvioIban(codPlan_pol, codLinea_pol);
			} catch (Exception e) {
				logger.debug("PolizaTransformar.java - Se ha producido un error" + e);
			}

			try {
				lineaContrSup2019 = pagoPolizaDao.lineaContratacion2019(codPlan_pol, codLinea_pol,
						poliza.getLinea().isLineaGanado());
				logger.debug("valor de lineaContrSup2019: " + lineaContrSup2019);
			} catch (Exception e) {
				logger.debug(
						"PolizaUnificadaTransformar.java - Se ha producido un erroral recuperar la lineacontratacion"
								+ e);
			}

			boolean fechaPagoInsertada = false;

			// Si se ha elegido pago fraccionado se a la cuenta
			if (permiteEnvIban && poliza.getEsFinanciada().equals('S')) {
				logger.debug("**@@** Antes de obtenerCuenta con true(1)");
				pago.setCuenta(obtenerCuenta(poliza, true));
			}

			boolean pagoManual = false;
			boolean pagoDomiciliado = false;
			boolean tienePago = false;

			BigDecimal importePago = new BigDecimal(0);
			for (PagoPoliza pp : poliza.getPagoPolizas()) {
				if (pp.getTipoPago().compareTo(Constants.PAGO_MANUAL) == 0 && pp.getBanco() != null) {
					String banco = pp.getBanco().toString();
					if (banco.length() < 4) {
						while (banco.length() < 4) {
							banco = "0" + banco;
						}
					}
					pago.setBanco(banco);
					pagoManual = true;
					// si es pago manual se inserta la fecha que nos venga en el pago
					if (pp.getFecha() != null) {
						pago.setFecha(DateUtil.date2Calendar(pp.getFecha()));
						fechaPagoInsertada = true;
					}
				}

				/* Pet. 57625 ** MODIF (22.02.2019) ** Inicio */
				if (pp.getTipoPago().compareTo(Constants.DOMICILIACION_AGRO) == 0 && lineaContrSup2019) {
					pagoDomiciliado = true;
				} else {
					pagoDomiciliado = false;
				}
				/* Pet. 57625 ** MODIF (22.02.2019) ** Fin */

				if (pp.getEnvioIbanAgro() != null && pp.getEnvioIbanAgro().equals('S')) {
					Cuenta cuenta = Cuenta.Factory.newInstance();
					cuenta.setIban(pp.getIban() + pp.getCccbanco());
					if (pp.getDestinatarioDomiciliacion() != null) {
						cuenta.setDestinatario(pp.getDestinatarioDomiciliacion().toString());
						if (pp.getDestinatarioDomiciliacion().equals('O'))
							cuenta.setTitular(pp.getTitularCuenta());
					}
					pago.setCuenta(cuenta);
				}
				// MODIF TAM (29.06.2020) * Pet. 57626 */
				importePago = pp.getImporte() != null ? pp.getImporte() : new BigDecimal(0);
				// Fin

				tienePago = true;
			} /* Fin del For */

			if (!pagoManual && !pagoDomiciliado) {
				if (poliza.getColectivo().getIsCRM() != null && poliza.getColectivo().getIsCRM() > 0) {
					pago.setBanco("3058");
				} else {
					pago.setBanco("0198");
				}
			}

			// Se indica la forma de pago dependiendo si se fracciona la poliza o no
			pago.setForma(String.valueOf(poliza.getEsFinanciada().equals('S') ? Constants.FORMA_PAGO_FINANCIADO
					: Constants.FORMA_PAGO_ALCONTADO));
			pago.setImporte(importePago);
			logger.debug("Asignamos el importe: " + importePago);

			if (!pagoDomiciliado && !fechaPagoInsertada) {
				if (poliza.getFechaPago() != null)
					pago.setFecha(DateUtil.date2Calendar(poliza.getFechaPago()));
				else
					pago.setFecha(new GregorianCalendar());				
			}

			/* Nuevos DAtos Aval */
			if (null != poliza.getDatosAval()) {
				es.agroseguro.contratacion.Fraccionamiento fraccionamiento;
				fraccionamiento = getFraccionamiento(poliza, cp, poliza.getDatosAval().getImporteAval(),
						poliza.getDatosAval().getNumeroAval());
				pago.setFraccionamiento(fraccionamiento);
				if ((new BigDecimal(1).equals(poliza.getDatosAval().getImporteAval())
						&& new BigDecimal(1).equals(poliza.getDatosAval().getNumeroAval()))
						&& (null != pago.getCuenta() && "".equals(pago.getCuenta().getIban()))) {
					pago.getCuenta().setIban(DEFAULT_IBAN);
				}

				logger.debug("**@@** Antes de obtenerCuenta con true(2)");
				pago.setCuenta(obtenerCuenta(poliza, true));
			} else {
				if (esFinanciada) {
					BigDecimal importeAval = new BigDecimal(1);
					BigDecimal numAval = new BigDecimal(1);
					es.agroseguro.contratacion.Fraccionamiento fraccionamiento;
					fraccionamiento = getFraccionamiento(poliza, cp, importeAval, numAval);
					pago.setFraccionamiento(fraccionamiento);
					if ((new BigDecimal(1).equals(importeAval) && new BigDecimal(1).equals(numAval))
							&& (null != pago.getCuenta() && "".equals(pago.getCuenta().getIban()))) {
						pago.getCuenta().setIban(DEFAULT_IBAN);
					}
				}
			}

			/* Fin Nuevos Datos Aval */
			/* Pet.57626 MODIF TAAM (07.07.2020) *** Fin */

			// Si la poliza es de la linea 303 se Anade la cuenta a los datos del pago

			boolean esFinanc = false;
			if (poliza.getEsFinanciada().equals('S')) {
				esFinanc = true;

				/* Pet. 57626 ** MODIF TAM (09.07.2020) * Resolucion incidencias */
				/* Aunque esta financiada se envia la cuenta y el destinatario */
				Cuenta cuentaF = pago.getCuenta();

				for (PagoPoliza pp : poliza.getPagoPolizas()) {

					if (pp.getDestinatarioDomiciliacion() != null) {
						cuentaF.setDestinatario(pp.getDestinatarioDomiciliacion().toString());
						if (pp.getDestinatarioDomiciliacion().equals('O'))
							cuentaF.setTitular(pp.getTitularCuenta());
						pago.setCuenta(cuentaF);
					}

				}
			}

			logger.debug("**@@** Antes de obtenerCuenta con valor esFinanc:" + esFinanc);
			if ("303".equals(poliza.getLinea().getCodlinea().toString()))
				pago.setCuenta(obtenerCuenta(poliza, esFinanc));

			/******* SETDOMICILIADO PARA EL PAGO DE AgricolaS *********/

			/*
			 * Estamos viendo que al validar las polizas de ganado, independientemente de lo
			 * que se haya elegido en el modulo y lo que tenga en pagos (o no tenga), se
			 * esta enviando el indicador de poliza domiciliada, lo que provoca errores de
			 * rechazo. ?Es un valor que se envia por defecto asi? ?Se puede cambiar a N?
			 */

			/*
			 * Lo que queremos es que, mientras no tenga datos previos de pago, se envie el
			 * valor ?N? para que no de errores de validacion sin motivo. Ojo con el paso a
			 * definitiva y la confirmacion!! En cuanto al SW Calculo, debe ir siempre con
			 * la N, porque en caso contrario, al no llevar fecha de pago, da problemas con
			 * el calculo de las subvenciones.
			 */

			/*
			 * Con lo que estoy revisando... al lanzar la llamada al SW de Calculo no
			 * siempre va con 'N'.... con lo cual tendriamos que incluir una nueva
			 * validacion para que en este caso, el metodo que genera el XML correspondiente
			 * ahora mismo no esta teniendo en cuenta que WebService lo esta llamando, con
			 * lo cual tenemos que incluir la validacion por WS que llama.
			 * 
			 * RESPUESTA: [ASF] Tras comentarlo con Negocio, mientras no haya datos de pago,
			 * independientemente del servicio, siempre se envia la N. En caso de tener
			 * datos de pago, que se envie lo que corresponda segun la forma de pago, para
			 * todos los servicios. Habra que tener cuidado con el tema SAECA, que se vuelve
			 * a calcular, en ese caso revisad lo que indique la norma.
			 */

			if (tienePago) {
				if (permiteEnvIban) {
					if (poliza.getEsFinanciada().equals('S')) {
						pago.setDomiciliado("S");
					} else {
						pago.setDomiciliado("N");
					}
				}

				for (PagoPoliza pp : poliza.getPagoPolizas()) {
					if (pp.getEnvioIbanAgro() != null && pp.getEnvioIbanAgro().equals('S')) {
						pago.setDomiciliado("S");
					}
				}
				if (pagoDomiciliado) {
					pago.setDomiciliado("T");
				}
			} else {
				pago.setDomiciliado("N");
			}
		} /* Fin del IF de esGanado */

		logger.error("Antes de salir de getPago. Valor de pago.getDomiciliado despues de asignar valor: "
				+ pago.getDomiciliado());

		return pago;
	}

	private static es.agroseguro.contratacion.Fraccionamiento getFraccionamiento(
			com.rsi.agp.dao.tables.poliza.Poliza poliza, ComparativaPoliza cp, BigDecimal importeAval,
			BigDecimal numAval) {
		es.agroseguro.contratacion.Fraccionamiento fraccionamiento = es.agroseguro.contratacion.Fraccionamiento.Factory
				.newInstance();
		es.agroseguro.contratacion.Aval aval = es.agroseguro.contratacion.Aval.Factory.newInstance();
		Iterator<DistribucionCoste2015> itDistCostes = poliza.getDistribucionCoste2015s().iterator();
		boolean distCostesEncontrada = false;
		while (itDistCostes.hasNext() && !distCostesEncontrada) {
			DistribucionCoste2015 distCostes = itDistCostes.next();
			if (distCostes.getCodmodulo().equals(cp.getId().getCodmodulo()) && distCostes.getIdcomparativa().equals(BigDecimal.valueOf(cp.getId().getIdComparativa()))) {
				if (distCostes.getPeriodoFracc() != null) {
					aval.setImporte(importeAval);
					aval.setNumero(numAval.intValue());
					fraccionamiento.addNewAval();
					fraccionamiento.setAval(aval);
					fraccionamiento.setPeriodo(distCostes.getPeriodoFracc());
				}
				distCostesEncontrada = true;
			}
		}
		// Si no esta, es la que se va a financiar
		if (!distCostesEncontrada) {
			aval.setImporte(importeAval);
			aval.setNumero(numAval.intValue());
			fraccionamiento.addNewAval();
			fraccionamiento.setAval(aval);

		}
		if (new BigDecimal(1).equals(importeAval) && new BigDecimal(1).equals(numAval)
				&& null == fraccionamiento.getAval()) {
			aval.setImporte(importeAval);
			aval.setNumero(numAval.intValue());
			fraccionamiento.addNewAval();
			fraccionamiento.setAval(aval);
			if (null != cp.getOpcionesFinanciacion()
					&& null != cp.getOpcionesFinanciacion().getCondicionFracionamiento()) {
				fraccionamiento.setPeriodo(cp.getOpcionesFinanciacion().getCondicionFracionamiento().intValue());
			}
		}
		return fraccionamiento;
	}

	private static boolean esComparativaFinanciada(ComparativaPoliza cp, List<DistribucionCoste2015> dcs) {
		// Es posible que la comparativa sea la que hemos selecionado en la jsp para
		// financiar o bien
		// que sea una comparativa seleccionada con anterioridad para financiar. En el
		// primer caso lo
		// sabemos por la jsp y en el segundo porque la distribucion de costes de la
		// comparativa tiene
		// el importe fraccionado con datos
		boolean res = false;
		res = cp.getEsFinanciada();
		if (!res) {
			for (DistribucionCoste2015 dc : dcs) {
				if (dc.getIdcomparativa().compareTo(new BigDecimal(cp.getId().getIdComparativa())) == 0 && dc.getImportePagoFracc() != null) {
					res = true;
					break;					
				}
			}
		}

		return res;
	}

	public static es.agroseguro.presupuestoContratacion.PolizaDocument transformarPolizaModulosYCoberturas(
			final com.rsi.agp.dao.tables.poliza.Poliza poliza, final List<BigDecimal> codsConceptos,
			final GenericDao<?> genericDao, final com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion exp,
			final com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo expAnexo,
			final Set<com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo> listaExpAnexo,
			final String codModulo, final List<GruposNegocio> gruposNegocio,
			final Map<Long, DatosVariables> dvEspecialesExplot) throws ValidacionPolizaException {

		es.agroseguro.presupuestoContratacion.PolizaDocument polizaDoc = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();
		es.agroseguro.presupuestoContratacion.Poliza polizaAS = es.agroseguro.presupuestoContratacion.Poliza.Factory
				.newInstance();
		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		/* Pet. 57626 ** MODIF TAM (14.05.2020) ** Inicio */
		boolean esGanado = poliza.getLinea().isLineaGanado();
		/* Pet. 57626 ** MODIF TAM (14.05.2020) ** Fin */

		if (!StringUtils.nullToString(poliza.getReferencia()).equals(VACIO)) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!"303".equals(poliza.getLinea().getCodlinea().toString())) {
			// COLECTIVO
			es.agroseguro.presupuestoContratacion.Colectivo c = es.agroseguro.presupuestoContratacion.Colectivo.Factory
					.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			es.agroseguro.presupuestoContratacion.Tomador tomador = es.agroseguro.presupuestoContratacion.Tomador.Factory
					.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);
			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);
			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(VACIO)) {
				dc.setEmail(tomadorPoliza.getEmail());
			}
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(VACIO)) {
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			}
			tomador.setDatosContacto(dc);
			polizaAS.setTomador(tomador);
		}

		// ASEGURADO
		es.agroseguro.presupuestoContratacion.Asegurado a = es.agroseguro.presupuestoContratacion.Asegurado.Factory
				.newInstance();
		a.setNif(poliza.getAsegurado().getNifcif());
		if (poliza.getAsegurado().getTipoidentificacion().equals("CIF")) {
			RazonSocial rs1 = RazonSocial.Factory.newInstance();
			rs1.setRazonSocial(poliza.getAsegurado().getRazonsocial());
			a.setRazonSocial(rs1);
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(poliza.getAsegurado().getNombre());
			nom.setApellido1(poliza.getAsegurado().getApellido1());
			if (!"".equals(StringUtils.nullToString(poliza.getAsegurado().getApellido2()).trim()))
				nom.setApellido2(poliza.getAsegurado().getApellido2());
			a.setNombreApellidos(nom);
		}
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(poliza.getAsegurado().getBloque());
		dir.setCp(poliza.getAsegurado().getCodpostalstr());
		dir.setEscalera(poliza.getAsegurado().getEscalera());
		if (poliza.getAsegurado().getLocalidad().getNomlocalidad().length() > 30) {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad().substring(0, 30));
		} else {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad());
		}
		dir.setNumero(poliza.getAsegurado().getNumvia());
		dir.setPiso(poliza.getAsegurado().getPiso());
		dir.setProvincia(poliza.getAsegurado().getLocalidad().getProvincia().getCodprovincia().intValue());
		dir.setVia(
				poliza.getAsegurado().getVia().getClave() + " " + poliza.getAsegurado().getDireccion().toUpperCase());
		a.setDireccion(dir);
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(poliza.getAsegurado().getEmail());
		dContacto.setTelefonoFijo(Integer.parseInt(poliza.getAsegurado().getTelefono()));
		if (!StringUtils.nullToString(poliza.getAsegurado().getMovil()).equals(VACIO)) {
			dContacto.setTelefonoMovil(Integer.parseInt(poliza.getAsegurado().getMovil()));
		}
		a.setDatosContacto(dContacto);
		polizaAS.setAsegurado(a);
		// FIN ASEGURADO

		// ENTIDAD
		es.agroseguro.presupuestoContratacion.Entidad entidad = es.agroseguro.presupuestoContratacion.Entidad.Factory
				.newInstance();
		entidad.setCodigo(Constants.ENTIDAD_C616);
		entidad.setCodigoInterno(PolizaTransformer.getCodigoInterno(poliza));

		if (poliza.getColectivo().getSubentidadMediadora() != null) {
			es.agroseguro.presupuestoContratacion.Mediador mediador = es.agroseguro.presupuestoContratacion.Mediador.Factory
					.newInstance();
			// DAA 18/12/2013 TIPO MEDIADOR
			SubentidadMediadora subentidad = poliza.getColectivo().getSubentidadMediadora();
			try {
				if (subentidad != null && subentidad.getTipoMediadorAgro() != null
						&& subentidad.getTipoMediadorAgro().getId() != null) {
					mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
				}
			} catch (Exception e) {
				mediador.setTipo(1);
			}
			mediador.setRetribucionAsegurado(SiNo.NO);
			mediador.setImporteRetribucion(new BigDecimal(0));
			// DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			if (subentidad.getTipoidentificacion().equals("CIF")) {
				RazonSocial rs2 = RazonSocial.Factory.newInstance();
				rs2.setRazonSocial(subentidad.getNomsubentidad());
				mediador.setRazonSocial(rs2);
			} else {
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
				nom.setNombre(subentidad.getNombre());
				nom.setApellido1(subentidad.getApellido1());
				nom.setApellido2(subentidad.getApellido2());
				mediador.setNombreApellidos(nom);
			}
			entidad.setMediador(mediador);
		}

		// ********************** //
		// AMG GASTOS 28/10/2014 //
		// ********************** //
		Gastos[] gastos = getGastos(gruposNegocio, poliza.getSetPolizaPctComisiones(), esGanado, poliza, false, null);
		entidad.setGastosArray(gastos);
		// FIN GASTOS

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// COBERTURAS
		es.agroseguro.presupuestoContratacion.Cobertura cobertura = es.agroseguro.presupuestoContratacion.Cobertura.Factory
				.newInstance();
		cobertura.setModulo(String.format("%-5s", codModulo));
		polizaAS.setCobertura(cobertura);

		// FIN COBERTURAS

		// ********************//
		// OBJETOS ASEGURADOS //
		// ********************//
		es.agroseguro.presupuestoContratacion.ObjetosAsegurados objAseg = es.agroseguro.presupuestoContratacion.ObjetosAsegurados.Factory
				.newInstance();
		org.w3c.dom.Node importedNode;
		if (esGanado) {
			// EXPLOTACIONES
			if (null != exp) {
				Set<com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion> setExp = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion>();
				setExp.add(exp);
				List<ExplotacionDocument> explotacionesCol = getExplotaciones(setExp,
						poliza.getLinea().getLineaseguroid(), genericDao, dvEspecialesExplot);

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}

			} else if (null != expAnexo) {
				Set<com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo> setExpAnexo = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo>();
				setExpAnexo.add(expAnexo);

				List<ExplotacionDocument> explotacionesCol = new ArrayList<ExplotacionDocument>();
				ExplotacionDocument expDoc = PolizaActualizadaGanadoTranformer.explotacionAnexoToExplotacionAgr(
						expAnexo, null, poliza.getLinea().getLineaseguroid(), genericDao);
				explotacionesCol.add(expDoc);

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}
			} else if (listaExpAnexo != null && !listaExpAnexo.isEmpty()) {

				List<ExplotacionDocument> explotacionesCol = new ArrayList<ExplotacionDocument>();

				for (ExplotacionAnexo eA : listaExpAnexo) {
					ExplotacionDocument expDoc = PolizaActualizadaGanadoTranformer.explotacionAnexoToExplotacionAgr(eA,
							null, poliza.getLinea().getLineaseguroid(), genericDao);
					explotacionesCol.add(expDoc);
				}

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}

			} else {

				List<ExplotacionDocument> explotacionesCol = getExplotaciones(poliza.getExplotacions(),
						poliza.getLinea().getLineaseguroid(), genericDao, dvEspecialesExplot);

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}
			}
		} else {
			// PARCELAS
			List<ParcelaDocument> parcelasCol = getParcelasModyCob(poliza, codModulo, codsConceptos);

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);
			}
		}

		polizaAS.setObjetosAsegurados(objAseg);
		// FIN OBJETOS ASEGURADOS

		polizaDoc.setPoliza(polizaAS);
		return polizaDoc;
	}
	/* Pet. 63485 ** MODIF TAM (16.07.2020) ** Fin */

	/* ESC-12909 ** MODIF TAM (19.04.2021 ** Inicio */
	/*
	 * Damos de alta un nuevo metodo para generar el xml de polizas antes de llamar
	 * al S.W de Coberturas Contratadas
	 */
	public static es.agroseguro.presupuestoContratacion.PolizaDocument transformarPolizaCoberturasContratadas(
			final com.rsi.agp.dao.tables.poliza.Poliza poliza, final List<BigDecimal> codsConceptos,
			final GenericDao<?> genericDao, final com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion exp,
			final com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo expAnexo,
			final Set<com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo> listaExpAnexo,
			final String codModulo, final List<GruposNegocio> gruposNegocio,
			final Map<Long, DatosVariables> dvEspecialesExplot, final ComparativaPoliza cp,
			final boolean aplicaReduccionRdto, final List<BigDecimal> listaCPM) throws ValidacionPolizaException {

		logger.debug("PolizaUnificadaTransformer - transformarPolizaCoberturasContratadas [INIT]");

		es.agroseguro.presupuestoContratacion.PolizaDocument polizaDoc = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();
		es.agroseguro.presupuestoContratacion.Poliza polizaAS = es.agroseguro.presupuestoContratacion.Poliza.Factory
				.newInstance();
		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		/* Pet. 57626 ** MODIF TAM (14.05.2020) ** Inicio */
		boolean esGanado = poliza.getLinea().isLineaGanado();
		/* Pet. 57626 ** MODIF TAM (14.05.2020) ** Fin */

		if (!StringUtils.nullToString(poliza.getReferencia()).equals(VACIO)) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!"303".equals(poliza.getLinea().getCodlinea().toString())) {
			// COLECTIVO
			es.agroseguro.presupuestoContratacion.Colectivo c = es.agroseguro.presupuestoContratacion.Colectivo.Factory
					.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			es.agroseguro.presupuestoContratacion.Tomador tomador = es.agroseguro.presupuestoContratacion.Tomador.Factory
					.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);
			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);
			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(VACIO)) {
				dc.setEmail(tomadorPoliza.getEmail());
			}
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(VACIO)) {
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			}
			tomador.setDatosContacto(dc);
			polizaAS.setTomador(tomador);
		}

		// ASEGURADO
		es.agroseguro.presupuestoContratacion.Asegurado a = es.agroseguro.presupuestoContratacion.Asegurado.Factory
				.newInstance();
		a.setNif(poliza.getAsegurado().getNifcif());
		if (poliza.getAsegurado().getTipoidentificacion().equals("CIF")) {
			RazonSocial rs1 = RazonSocial.Factory.newInstance();
			rs1.setRazonSocial(poliza.getAsegurado().getRazonsocial());
			a.setRazonSocial(rs1);
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(poliza.getAsegurado().getNombre());
			nom.setApellido1(poliza.getAsegurado().getApellido1());
			if (!"".equals(StringUtils.nullToString(poliza.getAsegurado().getApellido2()).trim()))
				nom.setApellido2(poliza.getAsegurado().getApellido2());
			a.setNombreApellidos(nom);
		}
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(poliza.getAsegurado().getBloque());
		dir.setCp(poliza.getAsegurado().getCodpostalstr());
		dir.setEscalera(poliza.getAsegurado().getEscalera());
		if (poliza.getAsegurado().getLocalidad().getNomlocalidad().length() > 30) {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad().substring(0, 30));
		} else {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad());
		}
		dir.setNumero(poliza.getAsegurado().getNumvia());
		dir.setPiso(poliza.getAsegurado().getPiso());
		dir.setProvincia(poliza.getAsegurado().getLocalidad().getProvincia().getCodprovincia().intValue());
		dir.setVia(
				poliza.getAsegurado().getVia().getClave() + " " + poliza.getAsegurado().getDireccion().toUpperCase());
		a.setDireccion(dir);
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(poliza.getAsegurado().getEmail());
		dContacto.setTelefonoFijo(Integer.parseInt(poliza.getAsegurado().getTelefono()));
		if (!StringUtils.nullToString(poliza.getAsegurado().getMovil()).equals(VACIO)) {
			dContacto.setTelefonoMovil(Integer.parseInt(poliza.getAsegurado().getMovil()));
		}
		a.setDatosContacto(dContacto);
		polizaAS.setAsegurado(a);
		// FIN ASEGURADO

		// ENTIDAD
		es.agroseguro.presupuestoContratacion.Entidad entidad = es.agroseguro.presupuestoContratacion.Entidad.Factory
				.newInstance();
		entidad.setCodigo(Constants.ENTIDAD_C616);
		entidad.setCodigoInterno(PolizaTransformer.getCodigoInterno(poliza));

		if (poliza.getColectivo().getSubentidadMediadora() != null) {
			es.agroseguro.presupuestoContratacion.Mediador mediador = es.agroseguro.presupuestoContratacion.Mediador.Factory
					.newInstance();
			// DAA 18/12/2013 TIPO MEDIADOR
			SubentidadMediadora subentidad = poliza.getColectivo().getSubentidadMediadora();
			try {
				if (subentidad != null && subentidad.getTipoMediadorAgro() != null
						&& subentidad.getTipoMediadorAgro().getId() != null) {
					mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
				}
			} catch (Exception e) {
				mediador.setTipo(1);
			}
			mediador.setRetribucionAsegurado(SiNo.NO);
			mediador.setImporteRetribucion(new BigDecimal(0));
			// DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			if (subentidad.getTipoidentificacion().equals("CIF")) {
				RazonSocial rs2 = RazonSocial.Factory.newInstance();
				rs2.setRazonSocial(subentidad.getNomsubentidad());
				mediador.setRazonSocial(rs2);
			} else {
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
				nom.setNombre(subentidad.getNombre());
				nom.setApellido1(subentidad.getApellido1());
				nom.setApellido2(subentidad.getApellido2());
				mediador.setNombreApellidos(nom);
			}
			entidad.setMediador(mediador);
		}

		// ********************** //
		// AMG GASTOS 28/10/2014 //
		// ********************** //
		Gastos[] gastos = getGastos(gruposNegocio, poliza.getSetPolizaPctComisiones(), esGanado, poliza, false, null);
		entidad.setGastosArray(gastos);
		// FIN GASTOS

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// COBERTURAS
		es.agroseguro.presupuestoContratacion.Cobertura cobertura = es.agroseguro.presupuestoContratacion.Cobertura.Factory
				.newInstance();
		cobertura.setModulo(String.format("%-5s", codModulo));

		if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
			cobertura.setModulo(String.format("%-5s", cp.getId().getCodmodulo()));

			DatosVariables datosVariables = getDatosVariablesCobertura(poliza, cp, aplicaReduccionRdto, listaCPM);
			cobertura.setDatosVariables(datosVariables);
		} else {
			cobertura.setModulo(String.format("%-5s", poliza.getCodmodulo()));

			DatosVariables datosVariables = getDatosVariablesCobertCpl(poliza, poliza.getPolizaPpal(), cp,
					aplicaReduccionRdto, listaCPM);
			cobertura.setDatosVariables(datosVariables);
		}
		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS

		// ********************//
		// OBJETOS ASEGURADOS //
		// ********************//
		es.agroseguro.presupuestoContratacion.ObjetosAsegurados objAseg = es.agroseguro.presupuestoContratacion.ObjetosAsegurados.Factory
				.newInstance();
		org.w3c.dom.Node importedNode;
		if (esGanado) {
			// EXPLOTACIONES
			if (null != exp) {
				Set<com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion> setExp = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion>();
				setExp.add(exp);
				List<ExplotacionDocument> explotacionesCol = getExplotaciones(setExp,
						poliza.getLinea().getLineaseguroid(), genericDao, dvEspecialesExplot);

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}

			} else if (null != expAnexo) {
				Set<com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo> setExpAnexo = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo>();
				setExpAnexo.add(expAnexo);

				List<ExplotacionDocument> explotacionesCol = new ArrayList<ExplotacionDocument>();
				ExplotacionDocument expDoc = PolizaActualizadaGanadoTranformer.explotacionAnexoToExplotacionAgr(
						expAnexo, null, poliza.getLinea().getLineaseguroid(), genericDao);
				explotacionesCol.add(expDoc);

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}
			} else if (listaExpAnexo != null && !listaExpAnexo.isEmpty()) {

				List<ExplotacionDocument> explotacionesCol = new ArrayList<ExplotacionDocument>();

				for (ExplotacionAnexo eA : listaExpAnexo) {
					ExplotacionDocument expDoc = PolizaActualizadaGanadoTranformer.explotacionAnexoToExplotacionAgr(eA,
							null, poliza.getLinea().getLineaseguroid(), genericDao);
					explotacionesCol.add(expDoc);
				}

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}

			} else {

				List<ExplotacionDocument> explotacionesCol = getExplotaciones(poliza.getExplotacions(),
						poliza.getLinea().getLineaseguroid(), genericDao, dvEspecialesExplot);

				for (ExplotacionDocument explotacionDoc : explotacionesCol) {
					importedNode = objAseg.getDomNode().getOwnerDocument()
							.importNode(explotacionDoc.getExplotacion().getDomNode(), true);
					objAseg.getDomNode().appendChild(importedNode);
				}
			}
		} else {
			// PARCELAS
			List<ParcelaDocument> parcelasCol = getParcelasModyCob(poliza, codModulo, codsConceptos);

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);
			}
		}

		polizaAS.setObjetosAsegurados(objAseg);
		// FIN OBJETOS ASEGURADOS

		polizaDoc.setPoliza(polizaAS);
		logger.debug("PolizaUnificadaTransformer - transformarPolizaCoberturasContratadas [END]");
		return polizaDoc;
	}
	/* ESC-12909 ** MODIF TAM (19.04.2021 ** Fin */

	public static SubvencionesDeclaradas rellenaSubvencionesDeclaradas(com.rsi.agp.dao.tables.poliza.PolizaSocio ps) {
		SubvencionesDeclaradas sds = SubvencionesDeclaradas.Factory.newInstance();
		boolean tieneATP = false;
		boolean NumSegSocial = false;
		ArrayList<SubvencionDeclarada> lstSubvSoc = new ArrayList<SubvencionDeclarada>();
		Map<String, String> mapSocios = new HashMap<String, String>();
		if (!StringUtils.nullToString(ps.getSocio().getNumsegsocial()).equals(""))
			NumSegSocial = true;
		for (SubvencionSocioGanado sSociG : ps.getSocio().getSubvencionSocioGanados()) {

			SubvencionDeclarada subd = SubvencionDeclarada.Factory.newInstance();
			subd.setTipo(sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa().intValue());
			if (sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
					.compareTo(Constants.SUBVENCION20) == 0)
				tieneATP = true;
			if (!StringUtils.nullToString(ps.getSocio().getNumsegsocial()).equals("") && tieneATP) {
				SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
				segSocial.setProvincia(Integer.parseInt(ps.getSocio().getNumsegsocial().substring(0, 2)));
				segSocial.setNumero(Integer.parseInt(ps.getSocio().getNumsegsocial().substring(2, 10)));
				segSocial.setCodigo(ps.getSocio().getNumsegsocial().substring(10));
				if (!StringUtils.nullToString(ps.getSocio().getRegimensegsocial()).equals("")) {
					segSocial.setRegimen(Short.parseShort(ps.getSocio().getRegimensegsocial() + ""));
				}
				sds.setSeguridadSocial(segSocial);
				tieneATP = false;
			}

			if (sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
					.compareTo(Constants.SUBVENCION20) == 0) {
				if (!mapSocios.containsKey(sSociG.getSocio().getId().getNif() + "-"
						+ sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
					if (NumSegSocial) {
						lstSubvSoc.add(subd);
						mapSocios.put(sSociG.getSocio().getId().getNif() + "-"
								+ sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa(),
								"OK");
					}
				}
			} else {
				if (!mapSocios.containsKey(sSociG.getSocio().getId().getNif() + "-"
						+ sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa())) {
					lstSubvSoc.add(subd);
					mapSocios.put(
							sSociG.getSocio().getId().getNif() + "-"
									+ sSociG.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa(),
							"OK");
				}
			}

		}
		if (lstSubvSoc.size() > 0) {
			sds.setSubvencionDeclaradaArray(lstSubvSoc.toArray(new SubvencionDeclarada[lstSubvSoc.size()]));
			return sds;
		} else {
			return null;
		}

	}

	/**
	 * Devuelve las coberturas del anexo
	 * 
	 * @param am
	 * @param listaDatosVariables
	 * @return
	 */
	public static Cobertura getCoberturasAnexo(AnexoModificacion am, List<BigDecimal> listaCPM, TipoAseguradoGanado tAseGan) {

		Cobertura cobertura = Cobertura.Factory.newInstance();

		if (!StringUtils.nullToString(am.getCodmodulo().trim()).equals(""))
			cobertura.setModulo(String.format("%-5s", am.getCodmodulo().trim()));
		else
			cobertura.setModulo(String.format("%-5s", am.getPoliza().getCodmodulo().trim()));

		DatosVariables dv = DatosVariables.Factory.newInstance();

		List<CalculoIndemnizacion> listCalcIndem = new ArrayList<CalculoIndemnizacion>();
		List<Garantizado> listGarantizado = new ArrayList<Garantizado>();
		List<PorcentajeFranquicia> listPctFranquicia = new ArrayList<PorcentajeFranquicia>();
		List<PorcentajeMinimoIndemnizable> listPctMinIndem = new ArrayList<PorcentajeMinimoIndemnizable>();
		List<RiesgoCubiertoElegido> listRCubEleg = new ArrayList<RiesgoCubiertoElegido>();
		List<TipoFranquicia> listTipoFranq = new ArrayList<TipoFranquicia>();
		List<PorcentajeCapitalAsegurado> listPctCapAseg = new ArrayList<PorcentajeCapitalAsegurado>();

		for (com.rsi.agp.dao.tables.anexo.Cobertura coberturasAnexo : am.getCoberturas()) {
			
			logger.debug("Iterando coberturas... concepto " + coberturasAnexo.getCodconcepto());
			boolean isCPMPermitido = CoberturasUtils.isCPMPermitido(coberturasAnexo.getCodconceptoppalmod(), coberturasAnexo.getCodconcepto(), listaCPM);
			logger.debug("isCPMPermitido: " + isCPMPermitido);
			
			if (isCPMPermitido) {
				// CALCULO INDEMNIZACION
				if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION)
						.equals(coberturasAnexo.getCodconcepto())) {
					CalculoIndemnizacion cI = CalculoIndemnizacion.Factory.newInstance();
					cI.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					cI.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					cI.setValor(Integer.parseInt(coberturasAnexo.getCodvalor()));

					listCalcIndem.add(cI);
				}
				// GARANTIZADO
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO)
						.equals(coberturasAnexo.getCodconcepto())) {
					Garantizado g = Garantizado.Factory.newInstance();
					g.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					g.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					g.setValor(Integer.parseInt(coberturasAnexo.getCodvalor()));

					listGarantizado.add(g);
				}
				// % FRANQUICIA
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA)
						.equals(coberturasAnexo.getCodconcepto())) {
					PorcentajeFranquicia fran = PorcentajeFranquicia.Factory.newInstance();
					fran.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					fran.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					fran.setValor(Integer.parseInt(coberturasAnexo.getCodvalor()));

					listPctFranquicia.add(fran);
				}
				// % MINIMO INDEMNIZABLE
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE)
						.equals(coberturasAnexo.getCodconcepto())) {
					PorcentajeMinimoIndemnizable minIndem = PorcentajeMinimoIndemnizable.Factory.newInstance();
					minIndem.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					minIndem.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					minIndem.setValor(Integer.parseInt(coberturasAnexo.getCodvalor()));

					listPctMinIndem.add(minIndem);
				}
				// RIESGO CUBIERTO ELEGIDO
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO)
						.equals(coberturasAnexo.getCodconcepto())) {

					// MPM - Solo se envian los riesgos elegidos
					if (!Constants.RIESGO_ELEGIDO_NO.equals(coberturasAnexo.getCodvalor())) {
						RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
						rCubEleg.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
						rCubEleg.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
						rCubEleg.setValor("S");

						listRCubEleg.add(rCubEleg);
					}
					/*DNF 07/05/2021 ESC-13514 rce no elegido para que lo envie en el xml*/
					else {
						RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
						rCubEleg.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
						rCubEleg.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
						rCubEleg.setValor("N");

						listRCubEleg.add(rCubEleg);
					}
					/*fin DNF ESC-13514 07/05/2021*/ 
					
					
					
				}
				// TIPO FRANQUICIA
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA)
						.equals(coberturasAnexo.getCodconcepto())) {
					TipoFranquicia tipoFranq = TipoFranquicia.Factory.newInstance();
					tipoFranq.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					tipoFranq.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					tipoFranq.setValor(coberturasAnexo.getCodvalor());

					listTipoFranq.add(tipoFranq);
				}
				// % CAPITAL ASEGURADO
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO)
						.equals(coberturasAnexo.getCodconcepto())) {
					PorcentajeCapitalAsegurado pctCapAseg = PorcentajeCapitalAsegurado.Factory.newInstance();
					pctCapAseg.setCodRCub(coberturasAnexo.getCodriesgocubierto().intValue());
					pctCapAseg.setCPMod(coberturasAnexo.getCodconceptoppalmod().intValue());
					pctCapAseg.setValor(Integer.parseInt(coberturasAnexo.getCodvalor()));

					listPctCapAseg.add(pctCapAseg);
				}
				// CARACT. EXPLOTACION
				else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION)
						.equals(coberturasAnexo.getCodconcepto())) {
					/* Pet. 78691 ** MODIF TAM (15.12.2021) ** Inicio */
					/* Comprobamos si se ha lanzado el S.Web de Caracateristicas de la explotación para el Anexo previamente
					 * y en ese caso se obtiene dicho valor */
					CaracteristicasExplotacion caracExpl = CaracteristicasExplotacion.Factory.newInstance();
					if (am.getCodCaractExplotacion() == null) {
						caracExpl.setValor(Integer.parseInt(coberturasAnexo.getCodvalor()));
					}else {
						caracExpl.setValor(am.getCodCaractExplotacion().intValue());
					}
					dv.setCarExpl(caracExpl);
				}
			}
		}

		// Se insertan las listas generadas en el objeto DatosVariables
		if (listCalcIndem.size() > 0) {
			dv.setCalcIndemArray(listCalcIndem.toArray(new CalculoIndemnizacion[listCalcIndem.size()]));
		}
		if (listGarantizado.size() > 0) {
			dv.setGarantArray(listGarantizado.toArray(new Garantizado[listGarantizado.size()]));
		}
		if (listPctFranquicia.size() > 0) {
			dv.setFranqArray(listPctFranquicia.toArray(new PorcentajeFranquicia[listPctFranquicia.size()]));
		}
		if (listPctMinIndem.size() > 0) {
			dv.setMinIndemArray(listPctMinIndem.toArray(new PorcentajeMinimoIndemnizable[listPctMinIndem.size()]));
		}
		if (listRCubEleg.size() > 0) {
			dv.setRiesgCbtoElegArray(listRCubEleg.toArray(new RiesgoCubiertoElegido[listRCubEleg.size()]));
		}
		if (listTipoFranq.size() > 0) {
			dv.setTipFranqArray(listTipoFranq.toArray(new TipoFranquicia[listTipoFranq.size()]));
		}
		if (listPctCapAseg.size() > 0) {
			dv.setCapAsegArray(listPctCapAseg.toArray(new PorcentajeCapitalAsegurado[listPctCapAseg.size()]));
		}

		if (tAseGan != null) {
			dv.setTAseGan(tAseGan);
		}
		
		// Se establecen las coberturas
		cobertura.setDatosVariables(dv);

		return cobertura;
	}

	/**
	 * Calcula la comision del mediador a partir de los porcentajes de comisiones de
	 * la poliza o la comision de recalculo
	 * 
	 * @param pctCom
	 *            pctComisiones de la poliza
	 * @return
	 */
	public static BigDecimal obtenerComisionMediador(final PolizaPctComisiones ppc, final boolean aplicaDtoRec,
			final BigDecimal comisionRecalculo) {
		BigDecimal comMediador = new BigDecimal(0);
		logger.debug("CALCULANDO COMISION CON % FORZADO: " + comisionRecalculo);
		if ((ppc.getPctcommax() != null  || comisionRecalculo != null) && ppc.getPctentidad() != null
				&& ppc.getPctesmediadora() != null) {
			BigDecimal comisionMax = comisionRecalculo == null ? ppc.getPctcommax() : comisionRecalculo;
			BigDecimal comisionEntidad = ppc.getPctentidad();
			BigDecimal comisionE_S = ppc.getPctesmediadora();
			BigDecimal dtoElegido = null;
			BigDecimal recElegido = null;
			BigDecimal cien = new BigDecimal(100);
			if (ppc.getPctdescelegido() != null)
				dtoElegido = ppc.getPctdescelegido();
			if (ppc.getPctrecarelegido() != null)
				recElegido = ppc.getPctrecarelegido();
			BigDecimal descuento = BigDecimal.ZERO;
			BigDecimal recargo = BigDecimal.ZERO;
			BigDecimal pctComision = BigDecimal.ZERO;
			if (aplicaDtoRec) {
				logger.debug("DATOS PARA COMISION MEDIADOR: comisionMax: " + comisionMax + " comisionEnt: "
						+ comisionEntidad + " comisionE_ S: " + comisionE_S + " dtoElegido: " + dtoElegido
						+ " recElegido: " + recElegido);
				if (dtoElegido != null && dtoElegido.compareTo(BigDecimal.ZERO) != 0) {
					descuento = dtoElegido.divide(cien);
					pctComision = (comisionE_S.multiply(BigDecimal.ONE.subtract(descuento))).add(comisionEntidad);
				}
				if (recElegido != null && recElegido.compareTo(BigDecimal.ZERO) != 0) {
					recargo = recElegido.divide(cien);
					pctComision = (comisionE_S.multiply(BigDecimal.ONE.add(recargo))).add(comisionEntidad);
				}
				if ((dtoElegido == null || dtoElegido.compareTo(BigDecimal.ZERO) == 0)
						&& (recElegido == null || recElegido.compareTo(BigDecimal.ZERO) == 0)) {
					pctComision = (comisionE_S.multiply(BigDecimal.ONE)).add(comisionEntidad);
				}
			} else {
				logger.debug("DATOS PARA COMISION MEDIADOR: comisionMax: " + comisionMax + " comisionEnt: "
						+ comisionEntidad + " comisionE_ S: " + comisionE_S);
				pctComision = (comisionE_S.multiply(BigDecimal.ONE)).add(comisionEntidad);
			}
			if (comisionMax != null && comisionMax.compareTo(BigDecimal.ZERO) != 0) {
				comMediador = (comisionMax.multiply(pctComision)).divide(cien);
			}
		}
		logger.debug("COM MEDIADOR CALCULADO: " + comMediador.setScale(2, BigDecimal.ROUND_DOWN));
		return comMediador.setScale(2, BigDecimal.ROUND_DOWN);
	}

	/**
	 * Obtiene el orden del socio para la poliza indicada
	 * 
	 * @param poliza
	 * @param ss
	 * @return
	 */
	private static int getOrdenPolizaSocio(com.rsi.agp.dao.tables.poliza.Poliza poliza, SubvencionSocio ss) {
		// Recorre todos los registros de polizas socios correspondiente al
		// socio actual
		int orden = 1;

		try {
			for (PolizaSocio ps : ss.getSocio().getPolizaSocios()) {
				// Se obtiene el orden que ocupa el socio para la poliza actual
				if (ps.getPoliza().getIdpoliza().equals(poliza.getIdpoliza())) {
					orden = ps.getOrden().intValue();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return orden;
	}

	/**
	 * metodo para obtener un array de parcelas para enviar a Agroseguro a partir de
	 * una coleccion de parcelas de poliza
	 * 
	 * @param poliza
	 *            Poliza de la aplicacion
	 * @param cp
	 *            Comparativa actual.
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionPolizaException
	 */
	private static List<ParcelaDocument> getParcelas(com.rsi.agp.dao.tables.poliza.Poliza poliza, ComparativaPoliza cp,
			Map<BigDecimal, List<String>> listaDatosVariables, boolean aplicaReduccionRdto,
			List<BigDecimal> codsConceptos, final String webServiceToCall) throws ValidacionPolizaException {
		List<ParcelaDocument> parc = new ArrayList<ParcelaDocument>(poliza.getParcelas().size());
		ParcelaDocument pd;
		Parcela p;
		Set<com.rsi.agp.dao.tables.poliza.Parcela> parcelas = poliza.getParcelas();
		for (com.rsi.agp.dao.tables.poliza.Parcela parcela : parcelas) {
			if (parcela == null) {
				logger.error("parcela NULA: esto no deberia pasar.");
			} else {
				pd = ParcelaDocument.Factory.newInstance();
				p = Parcela.Factory.newInstance();		
				
				p.setHoja(parcela.getHoja().intValue());
				p.setNumero(parcela.getNumero().intValue());
				p.setNombre(parcela.getNomparcela());
				
				if (parcela.getCodprovsigpac() != null) {
					// rellenamos el sigpac
					SIGPAC sigpac = SIGPAC.Factory.newInstance();
					sigpac.setAgregado(parcela.getAgrsigpac().intValue());
					sigpac.setParcela(parcela.getParcelasigpac().intValue());
					sigpac.setPoligono(parcela.getPoligonosigpac().intValue());
					sigpac.setProvincia(parcela.getCodprovsigpac().intValue());
					sigpac.setRecinto(parcela.getRecintosigpac().intValue());
					sigpac.setTermino(parcela.getCodtermsigpac().intValue());
					sigpac.setZona(parcela.getZonasigpac().intValue());
					p.setSIGPAC(sigpac);
				}
				
				Ambito ambito = Ambito.Factory.newInstance();
				ambito.setComarca(parcela.getTermino().getId().getCodcomarca().intValue());
				ambito.setProvincia(parcela.getTermino().getId().getCodprovincia().intValue());
				ambito.setSubtermino(parcela.getTermino().getId().getSubtermino() + VACIO);
				ambito.setTermino(parcela.getTermino().getId().getCodtermino().intValue());
				
				/* P00077429 ** MODIF TAM (27/01/2021) ** Inicio */
				/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
				if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
					p.setUbicacion(ambito);
				}
				/* P00077429 ** MODIF TAM (27/01/2021) ** Fin */
				
				
				// Cosecha
				Cosecha cosecha = Cosecha.Factory.newInstance();
				cosecha.setCultivo(parcela.getCodcultivo().intValue());
				cosecha.setVariedad(parcela.getCodvariedad().intValue());
				CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
				Set<com.rsi.agp.dao.tables.poliza.CapitalAsegurado> capitalesAsegurados = parcela.getCapitalAsegurados();
				es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = new es.agroseguro.contratacion.parcela.CapitalAsegurado[capitalesAsegurados
						.size()];
				int cntCapAseg = 0;
				for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado ca : capitalesAsegurados) {
					StringBuffer parIdent = new StringBuffer();
					parIdent.append(parcela.getCodcultivo());
					parIdent.append("." + parcela.getCodvariedad());
					parIdent.append("." + parcela.getTermino().getId().getCodprovincia());
					parIdent.append("." + parcela.getTermino().getId().getCodcomarca());
					parIdent.append("." + parcela.getTermino().getId().getCodtermino());
					parIdent.append("." + parcela.getTermino().getId().getSubtermino());
					parIdent.append("." + ca.getTipoCapital().getCodtipocapital());
					es.agroseguro.contratacion.parcela.CapitalAsegurado c = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
							.newInstance();
					// DE MOMENTO EN TB_CAP_ASEG_REL_MODULO SOLO SE ESTA
					// GUARDANDO UN VALOR INDEPENDIENTEMENTE
					// DE LOS POSIBLES MODULOS SELECCIONADOS, ASI QUE ME QUEDO
					// CON EL PRIMERO Y LISTO
					// Hace un set del precio y produccion aun cuando la
					// coleccion no tiene nada
					Set<CapAsegRelModulo> colCapAsegRelModulo = ca.getCapAsegRelModulos();
					if (ca.getCapAsegRelModulos().size() > 0) {
						for (CapAsegRelModulo care : colCapAsegRelModulo) {
							logger.debug("P000018845: " + care.getCodmodulo() + " - " + cp.getId().getCodmodulo());
							if (care.getCodmodulo().equals(cp.getId().getCodmodulo())) {
								if (care.getPreciomodif() != null) {
									c.setPrecio(care.getPreciomodif());
								} else {
									if (care.getPrecio() != null) {
										c.setPrecio(care.getPrecio());
									} else {
										c.setPrecio(new BigDecimal(0));
									}
								}
								if (care.getProduccionmodif() != null) {
									c.setProduccion(care.getProduccionmodif().intValue());
								} else {
									c.setProduccion(care.getProduccion().intValue());
								}
								logger.debug("P000018845:  BREAK");
								break;
							} else {
								// P000018845: Si con todo lo anterior no se ha
								// incluido el precio, lo pondremos a cero para
								// que no falle la validacion para el envio
								logger.debug("P000018845: Precio/produccion = 0");
								c.setPrecio(new BigDecimal(0));
								c.setProduccion(0);
							}
						}
					} else {
						logger.debug("P000018845: getCapAsegRelModulos size = 0");
						if (ca.getPrecio() != null) {
							c.setPrecio(ca.getPrecio());
						} else {
							c.setPrecio(new BigDecimal(0));
						}
						if (ca.getProduccion() != null) {
							c.setProduccion(ca.getProduccion().intValue());
						} else {
							c.setProduccion(0);
						}
					}
					
					c.setSuperficie(ca.getSuperficie());
					c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
					// Obtenemos los datos variables de parcela y de coberturas
					// a nivel de parcela
					c.setDatosVariables(getDatosVariablesParcela(ca.getDatoVariableParcelas(),
							parcela.getCoberturasParcela(), parIdent.toString(), listaDatosVariables,
							cp.getId().getCodmodulo(), poliza.getLinea().getCodlinea(), aplicaReduccionRdto, codsConceptos,
							poliza.getIdpoliza(), ca.getIdcapitalasegurado(),
							ca.getTipoCapital().getCodtipocapital().intValue()));
	
					capitales[cntCapAseg] = c;
					cntCapAseg++;
				}
				capAseg.setCapitalAseguradoArray(capitales);
				cosecha.setCapitalesAsegurados(capAseg);
				p.setCosecha(cosecha);
				pd.setParcela(p);
				parc.add(pd);
			}
		}
		return parc;
	}

	/* Pet. 57626 ** MODIF TAM (15.07.2020) ** Inicio */
	/*
	 * Resolucion de Incidencias: Declaramos un nuevo metodo para cargar las
	 * parcelas para el SW de Rendimientos
	 */
	/**
	 * MÃ©todo para obtener un array de parcelas para enviar a Agroseguro a partir
	 * de una coleccion de parcelas de poliza
	 * 
	 * @param poliza
	 *            Poliza de la aplicacion
	 * @param cp
	 *            Comparativa actual.
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionPolizaException
	 */
	private static List<ParcelaDocument> getParcelasArray(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, Map<BigDecimal, List<String>> listaDatosVariables, boolean aplicaReduccionRdto,
			List<BigDecimal> codsConceptos, Set<Long> colIdParcelasFiltro, final String webServiceToCall) throws ValidacionPolizaException {

		List<ParcelaDocument> parc = new ArrayList<ParcelaDocument>();

		if (colIdParcelasFiltro != null) {
			parc = new ArrayList<ParcelaDocument>(colIdParcelasFiltro.size());
		} else {
			parc = new ArrayList<ParcelaDocument>(poliza.getParcelas().size());
		}

		Set<com.rsi.agp.dao.tables.poliza.Parcela> parcelas = poliza.getParcelas();
		for (com.rsi.agp.dao.tables.poliza.Parcela parcela : parcelas) {

			if (colIdParcelasFiltro == null
					|| (colIdParcelasFiltro != null && colIdParcelasFiltro.contains(parcela.getIdparcela()))) {

				Parcela p = Parcela.Factory.newInstance();
				ParcelaDocument pd = ParcelaDocument.Factory.newInstance();

				p.setHoja(parcela.getHoja().intValue());
				p.setNumero(parcela.getNumero().intValue());
				p.setNombre(parcela.getNomparcela());
				
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir el nuevo atributo 'parcelaAgricola' en la etiqueta 'Parcela' */
				/* DE MOMENTO Y POR PETICIÓN DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENVÍA EN NINGÚN XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
				/*if (parcela.getParcAgricola() != null) {
					p.setParcelaAgricola(parcela.getParcAgricola());	
				}*/
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */

				if (parcela.getCodprovsigpac() != null) {
					// rellenamos el sigpac
					SIGPAC sigpac = SIGPAC.Factory.newInstance();
					sigpac.setAgregado(parcela.getAgrsigpac().intValue());
					sigpac.setParcela(parcela.getParcelasigpac().intValue());
					sigpac.setPoligono(parcela.getPoligonosigpac().intValue());
					sigpac.setProvincia(parcela.getCodprovsigpac().intValue());
					// Por peticion de Judit del dia 25/05/2011 enviaremos un 0 en el recinto en
					// todas las lineas menos en la 309
					// ASF - Segun conversacion telefonica con Judit el 1/8/2012, volvemos a enviar
					// el recinto que se ponga en la pantalla
					// para todas las lineas
					sigpac.setRecinto(parcela.getRecintosigpac().intValue());

					sigpac.setTermino(parcela.getCodtermsigpac().intValue());
					// 17/4/2012. Incidencia 34 de polizas. ASF
					sigpac.setZona(parcela.getZonasigpac().intValue());
					// FIN Incidencia 34 de polizas. ASF
					p.setSIGPAC(sigpac);
				}

				Ambito ambito = Ambito.Factory.newInstance();
				ambito.setComarca(parcela.getTermino().getId().getCodcomarca().intValue());
				ambito.setProvincia(parcela.getTermino().getId().getCodprovincia().intValue());
				ambito.setSubtermino(parcela.getTermino().getId().getSubtermino() + "");
				ambito.setTermino(parcela.getTermino().getId().getCodtermino().intValue());
				
				/* P00077429 ** MODIF TAM (27/01/2021) ** Inicio */
				/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
				if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
					p.setUbicacion(ambito);
				}
				/* P00077429 ** MODIF TAM (27/01/2021) ** Fin */

				// Cosecha
				Cosecha cosecha = Cosecha.Factory.newInstance();
				cosecha.setCultivo(parcela.getCodcultivo().intValue());
				cosecha.setVariedad(parcela.getCodvariedad().intValue());

				Set<com.rsi.agp.dao.tables.poliza.CapitalAsegurado> capitalesAsegurados = parcela
						.getCapitalAsegurados();
				CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
				es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = new es.agroseguro.contratacion.parcela.CapitalAsegurado[capitalesAsegurados
						.size()];
				int cntCapAseg = 0;
				for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado ca : capitalesAsegurados) {

					StringBuffer parIdent = new StringBuffer();
					parIdent.append(parcela.getCodcultivo());
					parIdent.append("." + parcela.getCodvariedad());
					parIdent.append("." + parcela.getTermino().getId().getCodprovincia());
					parIdent.append("." + parcela.getTermino().getId().getCodcomarca());
					parIdent.append("." + parcela.getTermino().getId().getCodtermino());
					parIdent.append("." + parcela.getTermino().getId().getSubtermino());
					parIdent.append("." + ca.getTipoCapital().getCodtipocapital());

					es.agroseguro.contratacion.parcela.CapitalAsegurado c = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
							.newInstance();
					// Hace un set del precio y produccion aun cuando la coleccion no tiene nada
					Set<CapAsegRelModulo> colCapAsegRelModulo = ca.getCapAsegRelModulos();
					if (colCapAsegRelModulo.size() > 0) {

						for (CapAsegRelModulo care : colCapAsegRelModulo) {
							if (care.getCodmodulo().equals(cp.getId().getCodmodulo())) {
								if (care.getPreciomodif() != null) {
									c.setPrecio(care.getPreciomodif());
								} else {
									if (care.getPrecio() != null) {
										c.setPrecio(care.getPrecio());
									} else {
										c.setPrecio(new BigDecimal(0));
									}
								}
								if (care.getProduccionmodif() != null) {
									c.setProduccion(care.getProduccionmodif().intValue());
								} else {
									c.setProduccion(care.getProduccion() != null ? care.getProduccion().intValue() : 0);
								}
								if (poliza.getLinea().getCodlinea().equals(new BigDecimal(314)) && webServiceToCall.equals(Constants.WS_RENDIMIENTO)) {
									care.setProduccion(new BigDecimal(0));
								}
								break;
							} else {
								// P000018845: Si con todo lo anterior no se ha incluido el precio, lo pondremos
								// a cero para que no falle la validacion para el envio
								c.setPrecio(new BigDecimal(0));
								c.setProduccion(0);
							}
						}
					} else {
						if (ca.getPrecio() != null) {
							c.setPrecio(ca.getPrecio());
						} else {
							c.setPrecio(new BigDecimal(0));
						}
						if (ca.getProduccion() != null) {
							c.setProduccion(ca.getProduccion().intValue());
						} else {
							c.setProduccion(0);
						}
					}
					
					if (poliza.getLinea().getCodlinea().equals(new BigDecimal(314)) && webServiceToCall.equals(Constants.WS_RENDIMIENTO)) {
						ca.setProduccion(new BigDecimal(0));
						c.setProduccion(0);
					}

					c.setSuperficie(ca.getSuperficie());
					c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
					// Obtenemos los datos variables de parcela y de coberturas a nivel de parcela

					c.setDatosVariables(getDatosVariablesParcela(ca.getDatoVariableParcelas(),
							parcela.getCoberturasParcela(), parIdent.toString(), listaDatosVariables,
							cp.getId().getCodmodulo(), poliza.getLinea().getCodlinea(), aplicaReduccionRdto,
							codsConceptos, poliza.getIdpoliza(), ca.getIdcapitalasegurado(),
							ca.getTipoCapital().getCodtipocapital().intValue()));

					capitales[cntCapAseg] = c;
					cntCapAseg++;
				}

				capAseg.setCapitalAseguradoArray(capitales);
				cosecha.setCapitalesAsegurados(capAseg);
				p.setCosecha(cosecha);

				pd.setParcela(p);
				parc.add(pd);
			}
		}
		return parc;
	}
	/* Pet. 57626 ** MODIF TAM (15.07.2020) ** Fin */

	/* Pet. 63485 ** MODIF TAM (16.07.2020) ** Inicio */
	/**
	 * Metodo para obtener un array de parcelas para enviar a Agroseguro a partir de
	 * una coleccion de parcelas de poliza
	 * 
	 * @param poliza
	 *            Poliza de la aplicacion
	 * @param cp
	 *            Comparativa actual.
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionPolizaException
	 */
	private static List<ParcelaDocument> getParcelasModyCob(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			String codModulo, List<BigDecimal> codsConceptos) throws ValidacionPolizaException {
		List<ParcelaDocument> parc = new ArrayList<ParcelaDocument>(poliza.getParcelas().size());
		ParcelaDocument pd;
		Parcela p;
		for (com.rsi.agp.dao.tables.poliza.Parcela parcela : poliza.getParcelas()) {
			pd = ParcelaDocument.Factory.newInstance();
			p = Parcela.Factory.newInstance();

			/* Pet.50776_63485-Fase II ** MODIF TAM (03.11.2020) ** Inicio */
			/* Si no tiene asignado todavia la hoja y el numero se asigna el valor 1 */

			// El envio ha de hacerse con hoja y numero rellenos
			if (parcela.getHoja() == null) {
				p.setHoja(1);
			} else {
				p.setHoja(parcela.getHoja().intValue());
			}

			if (parcela.getNumero() == null) {
				p.setNumero(1);
			} else {
				p.setNumero(parcela.getNumero().intValue());
			}
			/* Pet.50776_63485-Fase II ** MODIF TAM (03.11.2020) ** Fin */

			p.setNombre(parcela.getNomparcela());
			
			/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
			/* Incluir el nuevo atributo 'parcelaAgricola' en la etiqueta 'Parcela' */
			/* DE MOMENTO Y POR PETICIÓN DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENVÍA EN NINGÚN XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
			/*if (parcela.getParcAgricola() != null) {
				p.setParcelaAgricola(parcela.getParcAgricola());	
			}*/
			/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
			
			if (parcela.getCodprovsigpac() != null) {
				// rellenamos el sigpac
				SIGPAC sigpac = SIGPAC.Factory.newInstance();
				sigpac.setAgregado(parcela.getAgrsigpac().intValue());
				sigpac.setParcela(parcela.getParcelasigpac().intValue());
				sigpac.setPoligono(parcela.getPoligonosigpac().intValue());
				sigpac.setProvincia(parcela.getCodprovsigpac().intValue());
				sigpac.setRecinto(parcela.getRecintosigpac().intValue());
				sigpac.setTermino(parcela.getCodtermsigpac().intValue());
				sigpac.setZona(parcela.getZonasigpac().intValue());
				p.setSIGPAC(sigpac);
			}
			Ambito ambito = Ambito.Factory.newInstance();
			ambito.setComarca(parcela.getTermino().getId().getCodcomarca().intValue());
			ambito.setProvincia(parcela.getTermino().getId().getCodprovincia().intValue());
			ambito.setSubtermino(parcela.getTermino().getId().getSubtermino() + VACIO);
			ambito.setTermino(parcela.getTermino().getId().getCodtermino().intValue());
			
			/* P00077429 ** MODIF TAM (27/01/2021) ** Inicio */
			/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
			if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
				p.setUbicacion(ambito);
			}
			/* P00077429 ** MODIF TAM (27/01/2021) ** Fin */
			
			// Cosecha
			Cosecha cosecha = Cosecha.Factory.newInstance();
			cosecha.setCultivo(parcela.getCodcultivo().intValue());
			cosecha.setVariedad(parcela.getCodvariedad().intValue());
			CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
			es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = new es.agroseguro.contratacion.parcela.CapitalAsegurado[parcela
					.getCapitalAsegurados().size()];
			int cntCapAseg = 0;
			for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
				es.agroseguro.contratacion.parcela.CapitalAsegurado c = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
						.newInstance();
				// DE MOMENTO EN TB_CAP_ASEG_REL_MODULO SOLO SE ESTA
				// GUARDANDO UN VALOR INDEPENDIENTEMENTE
				// DE LOS POSIBLES MODULOS SELECCIONADOS, ASI QUE ME QUEDO
				// CON EL PRIMERO Y LISTO
				// Hace un set del precio y producci?n aun cuando la
				// coleccion no tiene nada
				Set<CapAsegRelModulo> colCapAsegRelModulo = ca.getCapAsegRelModulos();
				if (ca.getCapAsegRelModulos().size() > 0) {
					for (CapAsegRelModulo care : colCapAsegRelModulo) {
						if (care.getCodmodulo().equals(codModulo)) {
							if (care.getPreciomodif() != null) {
								c.setPrecio(care.getPreciomodif());
							} else {
								if (care.getPrecio() != null) {
									c.setPrecio(care.getPrecio());
								} else {
									c.setPrecio(new BigDecimal(0));
								}
							}
							if (care.getProduccionmodif() != null) {
								c.setProduccion(care.getProduccionmodif().intValue());
							} else {
								c.setProduccion(care.getProduccion().intValue());
							}
							logger.debug("P000018845: BREAK ");
							break;
						} else {
							// P000018845: Si con todo lo anterior no se ha
							// incluido el precio, lo pondremos a cero para
							// que no falle la validaci?n para el env?o
							logger.debug("P000018845: Precio/producci?n = 0");
							c.setPrecio(new BigDecimal(0));
							c.setProduccion(0);
						}
					}
				} else {
					if (ca.getPrecio() != null) {
						c.setPrecio(ca.getPrecio());
					} else {
						c.setPrecio(new BigDecimal(0));
					}
					if (ca.getProduccion() != null) {
						c.setProduccion(ca.getProduccion().intValue());
					} else {
						c.setProduccion(0);
					}
				}
				c.setSuperficie(ca.getSuperficie());
				c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
				// Obtenemos los datos variables de parcela
				DatosVariables datosVariables = DatosVariables.Factory.newInstance();
				logger.debug("codsConceptos: " + codsConceptos);
				Set<DatoVariableParcela> dvSet = ca.getDatoVariableParcelas();
				for (DatoVariableParcela dvp : dvSet) {
					logger.debug("Tratando dv: " + dvp.getDiccionarioDatos().getCodconcepto());
					if (codsConceptos.contains(dvp.getDiccionarioDatos().getCodconcepto())) {
						if (dvp.getValor() != null) {
							try {
								/* ESC-14981 ** MODIF TAM (31/08/2021) ** Inicio */
								BigDecimal RiesgoCub = new BigDecimal(363);
								String valorLimpio = dvp.getValor().replace("[", "").replace("]", "").replace("\"", "").replace(",", " ");
								if (dvp.getDiccionarioDatos().getCodconcepto().compareTo(RiesgoCub)==0){
									BigDecimal codconceptoppalmod = new BigDecimal(dvp.getCodconceptoppalmod());
									BigDecimal codriesgocubierto = new BigDecimal(dvp.getCodriesgocubierto());
									setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(), codconceptoppalmod, codriesgocubierto, valorLimpio,
											datosVariables, false);
								}else {
									
									setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(), null, null, valorLimpio,
											datosVariables, false);
								}	
								/* ESC-14981 ** MODIF TAM (31/08/2021) ** Fin */
								
							} catch (NumberFormatException e) {
								throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							} catch (ParseException e) {
								throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							}
						}
					}
				}
				c.setDatosVariables(datosVariables);
				capitales[cntCapAseg] = c;
				cntCapAseg++;
			}
			capAseg.setCapitalAseguradoArray(capitales);
			cosecha.setCapitalesAsegurados(capAseg);
			p.setCosecha(cosecha);
			pd.setParcela(p);
			parc.add(pd);
		}
		return parc;
	}
	/* Pet. 63485 ** MODIF TAM (16.07.2020) ** Fin */

	/**
	 * metodo para obtener un array de explotaciones para enviar a Agroseguro a
	 * partir de una coleccion de explotaciones de poliza
	 * 
	 * @param explotaciones
	 * @param lineaseguroId
	 * @param genericDao
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static List<ExplotacionDocument> getExplotaciones(
			final Set<com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion> explotaciones, final Long lineaseguroId,
			final GenericDao<?> genericDao, Map<Long, DatosVariables> dvEspecialesExplot)
			throws ValidacionPolizaException {

		List<ExplotacionDocument> expls = new ArrayList<ExplotacionDocument>(explotaciones.size());
		Ambito ambito;
		Coordenadas coordenadas;
		Set<com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza> grupoRazasSet;
		List<GrupoRaza> grupoRazasCol;
		es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAsegurado;
		BigDecimal precioMax;
		Map<BigDecimal, DatosVariables> datosVarXML = null;
		// INICIO EXPLOTACIONES
		ExplotacionDocument explotDoc;
		Explotacion explot;

		// MPM - Temporalmente se genera el numero de la explotacion
		// secuencialmente
		int numExpl = 1;

		for (com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion explotacion : explotaciones) {
			explotDoc = ExplotacionDocument.Factory.newInstance();
			explot = Explotacion.Factory.newInstance();

			if (explotacion.getNumero() != null) {
				explot.setNumero(explotacion.getNumero());
			} else {// No deberia irse nunca por el else
				explot.setNumero(numExpl++);
			}

			explot.setRega(explotacion.getRega());
			explot.setSigla(explotacion.getSigla());
			explot.setEspecie(explotacion.getEspecie().intValue());
			explot.setRegimen(explotacion.getRegimen().intValue());

			if (explotacion.getSubexplotacion() != null)
				explot.setSubexplotacion(explotacion.getSubexplotacion());

			// INICIO UBICACION
			ambito = Ambito.Factory.newInstance();
			ambito.setProvincia(explotacion.getTermino().getId().getCodprovincia().intValue());
			ambito.setComarca(explotacion.getTermino().getId().getCodcomarca().intValue());
			ambito.setTermino(explotacion.getTermino().getId().getCodtermino().intValue());
			ambito.setSubtermino(explotacion.getTermino().getId().getSubtermino().toString());
			
			explot.setUbicacion(ambito);
			// FIN UBICACION

			// INICIO COORDENADAS
			if (explotacion.getLatitud() != null && explotacion.getLongitud() != null) {
				coordenadas = Coordenadas.Factory.newInstance();
				coordenadas.setLatitud(explotacion.getLatitud());
				coordenadas.setLongitud(explotacion.getLongitud());
				explot.setCoordenadas(coordenadas);
			}
			// FIN COORDENADAS

			// INICIO GRUPOS RAZA
			grupoRazasSet = explotacion.getGrupoRazas();
			grupoRazasCol = new ArrayList<GrupoRaza>(grupoRazasSet.size());

			Map<String, es.agroseguro.contratacion.explotacion.GrupoRaza> mapaGr = new HashMap<String, es.agroseguro.contratacion.explotacion.GrupoRaza>();
			Map<String, es.agroseguro.contratacion.explotacion.CapitalAsegurado> mapaTc = new HashMap<String, es.agroseguro.contratacion.explotacion.CapitalAsegurado>();
			Map<String, Set<String>> mapaGrVsTcClaves = new HashMap<String, Set<String>>();
			Map<String, es.agroseguro.contratacion.explotacion.Animales> mapaAnim = new HashMap<String, es.agroseguro.contratacion.explotacion.Animales>();
			Map<String, Set<String>> mapaTcVsAnimClaves = new HashMap<String, Set<String>>();

			Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> setDatosVar = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable>();
			List<String> lstKeysGRazasTCap = new ArrayList<String>();
			List<String> lstKeysGRazasTCapAni = new ArrayList<String>();
			// Preparacion de los objetos
			for (com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza grupoRazaAplicacion : grupoRazasSet) {
				datosVarXML = null;
				Long codGrupoRaza = grupoRazaAplicacion.getCodgruporaza();
				BigDecimal codTipoCapital = grupoRazaAplicacion.getCodtipocapital();
				Long codTipoAni = grupoRazaAplicacion.getCodtipoanimal();
				String keyMapaCapital = codGrupoRaza + "-" + codTipoCapital;
				String keyMapaCapAnim = codGrupoRaza + "-" + codTipoCapital + "-" + codTipoAni;
				if (!lstKeysGRazasTCap.contains(keyMapaCapital)) {
					lstKeysGRazasTCap.add(keyMapaCapital);
					setDatosVar = dameDatosVarPorGrupoRazaYTipoCap(grupoRazasSet,
							grupoRazaAplicacion.getCodgruporaza().longValue(), grupoRazaAplicacion.getCodtipocapital());

					// Datos variables
					datosVarXML = getDatosVariablesExplotacion(lineaseguroId, setDatosVar, genericDao);

				}
				// Para los casos en los que tengo un dato variable que va a nivel de tipo de
				// animal y dentro de una explotacion tengo
				// varios tipos de animal para los que aplica un dato variable
				if (!lstKeysGRazasTCapAni.contains(keyMapaCapAnim)) {
					lstKeysGRazasTCapAni.add(keyMapaCapAnim);
					setDatosVar = dameDatosVarPorGrRazaYTipCapYTipAni(grupoRazasSet,
							grupoRazaAplicacion.getCodgruporaza().longValue(), grupoRazaAplicacion.getCodtipocapital(),
							grupoRazaAplicacion.getCodtipoanimal());

					// Datos variables
					datosVarXML = getDatosVariablesExplotacion(lineaseguroId, setDatosVar, genericDao);

				}
				// EXPLOTACION

				// GRUPO RAZA
				if (mapaGr.get(codGrupoRaza.toString()) == null) {
					GrupoRaza g = GrupoRaza.Factory.newInstance();
					g.setGrupoRaza(codGrupoRaza.intValue());

					if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_GRUPO_RAZA) != null) {
						g.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_GRUPO_RAZA));
					}

					mapaGr.put(codGrupoRaza.toString(), g);
				}

				// TIPO CAPITAL
				if (mapaTc.get(keyMapaCapital) == null) {
					capitalAsegurado = es.agroseguro.contratacion.explotacion.CapitalAsegurado.Factory.newInstance();
					capitalAsegurado.setTipo(codTipoCapital.intValue());

					if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_CAP_ASEG) != null) {
						capitalAsegurado
								.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_CAP_ASEG));
					}

					mapaTc.put(keyMapaCapital, capitalAsegurado);

					if (mapaGrVsTcClaves.get(codGrupoRaza.toString()) == null) {
						Set<String> colClaves = new HashSet<String>();
						colClaves.add(codTipoCapital.toString());
						mapaGrVsTcClaves.put(codGrupoRaza.toString(), colClaves);
					} else {
						Set<String> colClaves = mapaGrVsTcClaves.get(codGrupoRaza.toString());
						colClaves.add(codTipoCapital.toString());
						mapaGrVsTcClaves.put(codGrupoRaza.toString(), colClaves);
					}
				}

				// TIPO ANIMAL
				Long codTipoAnimal = grupoRazaAplicacion.getCodtipoanimal();
				String keyMapaAnimal = codGrupoRaza + "-" + codTipoCapital + "-" + codTipoAnimal;

				if (mapaAnim.get(keyMapaAnimal) == null) {
					es.agroseguro.contratacion.explotacion.Animales animales = null;
					animales = es.agroseguro.contratacion.explotacion.Animales.Factory.newInstance();
					animales.setTipo(grupoRazaAplicacion.getCodtipoanimal().intValue());
					animales.setNumero(grupoRazaAplicacion.getNumanimales().intValue());

					precioMax = new BigDecimal(0);
					for (PrecioAnimalesModulo precioAnimalModulo : grupoRazaAplicacion.getPrecioAnimalesModulos()) {
						if (null != precioAnimalModulo.getPrecio())
							precioMax = precioMax.max(precioAnimalModulo.getPrecio());
						else if (null != precioAnimalModulo.getPrecioMax())
							precioMax = precioMax.max(precioAnimalModulo.getPrecioMax());
					}

					animales.setPrecio(precioMax);

					if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_ANIMALES) != null) {
						animales.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_ANIMALES));
					}

					mapaAnim.put(keyMapaAnimal, animales);

					if (mapaTcVsAnimClaves.get(keyMapaCapital) == null) {
						Set<String> colClaves = new HashSet<String>();
						colClaves.add(codTipoAnimal.toString());
						mapaTcVsAnimClaves.put(keyMapaCapital, colClaves);
					} else {
						Set<String> colClaves = mapaTcVsAnimClaves.get(keyMapaCapital);
						colClaves.add(codTipoAnimal.toString());
						mapaTcVsAnimClaves.put(keyMapaCapital, colClaves);
					}
				}
			}

			// Para enlazarlos en el orden que exige la validacion
			for (Map.Entry<String, es.agroseguro.contratacion.explotacion.GrupoRaza> entry : mapaGr.entrySet()) {
				GrupoRaza gr = entry.getValue();
				String codGrupoRaza = String.valueOf(gr.getGrupoRaza());

				Set<String> colClavesGrVsTc = mapaGrVsTcClaves.get(codGrupoRaza);
				Iterator<String> it = colClavesGrVsTc.iterator();
				String claveDobleTc = null;

				List<CapitalAsegurado> listaCapitalAsegurado = new ArrayList<CapitalAsegurado>();

				while (it.hasNext()) {
					claveDobleTc = codGrupoRaza + "-" + it.next();

					CapitalAsegurado capi = mapaTc.get(claveDobleTc);

					String claveTripleAnim = null;
					Set<String> colClavesTcVsAnim = mapaTcVsAnimClaves.get(claveDobleTc);
					Iterator<String> it2 = colClavesTcVsAnim.iterator();

					List<Animales> listaAnimales = new ArrayList<Animales>();
					while (it2.hasNext()) {
						claveTripleAnim = claveDobleTc + "-" + it2.next();
						Animales ani = mapaAnim.get(claveTripleAnim);
						listaAnimales.add(ani);
					}

					Animales arrayAnimales[] = new Animales[listaAnimales.size()];
					arrayAnimales = listaAnimales.toArray(arrayAnimales);
					capi.setAnimalesArray(arrayAnimales);

					listaCapitalAsegurado.add(capi);
				}

				CapitalAsegurado arrayCapitalAsegurado[] = new CapitalAsegurado[listaCapitalAsegurado.size()];
				arrayCapitalAsegurado = listaCapitalAsegurado.toArray(arrayCapitalAsegurado);
				gr.setCapitalAseguradoArray(arrayCapitalAsegurado);

				grupoRazasCol.add(gr);
			}

			if (datosVarXML != null && datosVarXML.get(OrganizadorInfoConstants.UBICACION_EXPLOTACION) != null) {
				explot.setDatosVariables(datosVarXML.get(OrganizadorInfoConstants.UBICACION_EXPLOTACION));
			}

			explot.setGrupoRazaArray(grupoRazasCol.toArray(new GrupoRaza[] {}));
			RiesgoCubiertoElegido[] listaRce = getArrayDatoVariableRiesgoCbtoExplotacion(explotacion);
			if (null != listaRce) {
				if (null == explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());

				explot.getDatosVariables().setRiesgCbtoElegArray(listaRce);

			}

			// rellenamos los datos variables especiales
			rellenaDatosVariablesEspeciales(explot, explotacion);

			if (explotacion.getId() != null) {
				logger.debug("1955 - Explotacion:" + explotacion.getId().toString());
				DatosVariables dv = dvEspecialesExplot.get(explotacion.getId());
				if (dv != null) {
					logger.debug("1998: " + dv.toString());
					if (null == explot.getDatosVariables()) {
						explot.setDatosVariables(dv);
					} else {
						if (dv.getMinIndemArray() != null && dv.getMinIndemArray().length > 0)
							explot.getDatosVariables().setMinIndemArray(dv.getMinIndemArray());
						if (dv.getCalcIndemArray() != null && dv.getCalcIndemArray().length > 0)
							explot.getDatosVariables().setCalcIndemArray(dv.getCalcIndemArray());
					}
				}
			}

			explotDoc.setExplotacion(explot);
			expls.add(explotDoc);

		}
		// FIN EXPLOTACIONES
		return expls;
	}

	private static Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> dameDatosVarPorGrupoRazaYTipoCap(
			Set<com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza> grupoRazasSet, Long codgruporaza,
			BigDecimal codtipocapital) {
		Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> setDatosVarFinal = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable>();

		for (com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza grupoRazaAplicacion : grupoRazasSet) {
			if ((grupoRazaAplicacion != null) && (grupoRazaAplicacion.getCodgruporaza().compareTo(codgruporaza) == 0)
					&& (grupoRazaAplicacion.getCodtipocapital().compareTo(codtipocapital) == 0)) {
				if (grupoRazaAplicacion.getDatosVariables() != null
						&& grupoRazaAplicacion.getDatosVariables().size() > 0) {
					Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> datosVariables = grupoRazaAplicacion
							.getDatosVariables();
					for (com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable datoVariable : datosVariables) {
						if (!setDatosVarFinal.contains(datoVariable))
							setDatosVarFinal.add(datoVariable);
					}
				}
			}
		}
		return setDatosVarFinal;
	}

	private static Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> dameDatosVarPorGrRazaYTipCapYTipAni(
			Set<com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza> grupoRazasSet, Long codgruporaza,
			BigDecimal codtipocapital, Long codtipoanimal) {
		Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> setDatosVarFinal = new HashSet<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable>();

		for (com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza grupoRazaAplicacion : grupoRazasSet) {
			if ((grupoRazaAplicacion != null) && (grupoRazaAplicacion.getCodgruporaza().compareTo(codgruporaza) == 0)
					&& (grupoRazaAplicacion.getCodtipocapital().compareTo(codtipocapital) == 0)
					&& (grupoRazaAplicacion.getCodtipoanimal().compareTo(codtipoanimal) == 0)) {
				if (grupoRazaAplicacion.getDatosVariables() != null
						&& grupoRazaAplicacion.getDatosVariables().size() > 0) {
					Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> datosVariables = grupoRazaAplicacion
							.getDatosVariables();
					for (com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable datoVariable : datosVariables) {
						if (!setDatosVarFinal.contains(datoVariable))
							setDatosVarFinal.add(datoVariable);
					}
				}
			}
		}
		return setDatosVarFinal;
	}

	private static RiesgoCubiertoElegido[] getArrayDatoVariableRiesgoCbtoExplotacion(
			final com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion tbExplotacion) {
		List<RiesgoCubiertoElegido> listaRce = new ArrayList<RiesgoCubiertoElegido>();
		if (tbExplotacion.getExplotacionCoberturas() != null && tbExplotacion.getExplotacionCoberturas().size() > 0) {

			for (ExplotacionCobertura expCob : tbExplotacion.getExplotacionCoberturas()) {
				logger.debug("Cobertura: CPM" + expCob.getCpm() + " descCPM: " + expCob.getCpmDescripcion() + " RC "
						+ expCob.getRiesgoCubierto() + " descRC: " + expCob.getCpmDescripcion() + ELEG
						+ expCob.getElegible() + DV_COD_CPTO + expCob.getDvCodConcepto() + DV_VALOR
						+ expCob.getDvValor() + DV_ELEGIDO + expCob.getDvElegido());
				if (expCob.getElegible().equals('S') && null == expCob.getDvCodConcepto()) {
					RiesgoCubiertoElegido rc = RiesgoCubiertoElegido.Factory.newInstance();
					rc.setCPMod(expCob.getCpm());
					rc.setCodRCub(expCob.getRiesgoCubierto());
					rc.setValor(expCob.getElegida().toString());
					listaRce.add(rc);
					logger.debug("--> Riesgo cubierto insertado: CPM: " + expCob.getCpm() + RC
							+ expCob.getRiesgoCubierto() + ELEG + expCob.getElegible());
				}
			}
		}
		logger.debug("Tamaï¿½o de lista: " + listaRce.size());
		RiesgoCubiertoElegido[] arrayRC = listaRce.toArray(new RiesgoCubiertoElegido[listaRce.size()]);
		return arrayRC;

	}

	/*
	 * TRATAMIENTO PARA DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL
	 * MODULO Y DEL RIESGO CUBIERTO
	 */
	private static void rellenaDatosVariablesEspeciales(Explotacion explot,
			com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion explotacion) {
		if (explotacion.getExplotacionCoberturas() != null && explotacion.getExplotacionCoberturas().size() > 0) {
			List<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> listaCalc = new ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion>();
			List<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> listaMin = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable>();
			List<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> listaCap = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado>();
			List<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> listaFr = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia>();
			List<es.agroseguro.contratacion.datosVariables.Garantizado> listaGar = new ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado>();
			for (ExplotacionCobertura expCob : explotacion.getExplotacionCoberturas()) {
				logger.debug("--> CPM: " + expCob.getCpm() + RC + expCob.getRiesgoCubierto() + " elegible: "
						+ expCob.getElegible() + " elegida: " + expCob.getElegida() + DV_COD_CPTO
						+ expCob.getDvCodConcepto() + DV_VALOR + expCob.getDvValor() + DV_ELEGIDO
						+ expCob.getDvElegido());
				if ((expCob.getElegible().equals('S') && expCob.getElegida().equals('S'))
						|| (expCob.getElegible().equals('N'))) {
					if (expCob.getDvCodConcepto() != null && expCob.getDvElegido().equals('S')) {
						switch (expCob.getDvCodConcepto().intValue()) {
						// INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL
						// RIESGO CUBIERTO
						case 121: // minimo indemnizable
							es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable min = es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable.Factory
									.newInstance();
							min.setCPMod(expCob.getCpm());
							min.setCodRCub(expCob.getRiesgoCubierto());
							min.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaMin.add(min);
							logger.debug("--> CoberturaDV 121 insertada: CPM: " + expCob.getCpm() + RC
									+ expCob.getRiesgoCubierto() + ELEG + expCob.getElegible() + DV_COD_CPTO
									+ expCob.getDvCodConcepto() + DV_VALOR + expCob.getDvValor() + DV_ELEGIDO
									+ expCob.getDvElegido());
							break;
						case 174: // calculo indemnizacion
							es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion ca = es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion.Factory
									.newInstance();
							ca.setCPMod(expCob.getCpm());
							ca.setCodRCub(expCob.getRiesgoCubierto());
							ca.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaCalc.add(ca);
							logger.debug("--> CoberturaDV 174 insertada: CPM: " + expCob.getCpm() + RC
									+ expCob.getRiesgoCubierto() + ELEG + expCob.getElegible() + DV_COD_CPTO
									+ expCob.getDvCodConcepto() + DV_VALOR + expCob.getDvValor() + DV_ELEGIDO
									+ expCob.getDvElegido());
							break;
						case 362: // % CAPITAL ASEGURADO
							es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado cap = es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado.Factory
									.newInstance();
							cap.setCPMod(expCob.getCpm());
							cap.setCodRCub(expCob.getRiesgoCubierto());
							cap.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaCap.add(cap);
							logger.debug("--> CoberturaDV 362 insertada: CPM: " + expCob.getCpm() + RC
									+ expCob.getRiesgoCubierto() + ELEG + expCob.getElegible() + DV_COD_CPTO
									+ expCob.getDvCodConcepto() + DV_VALOR + expCob.getDvValor() + DV_ELEGIDO
									+ expCob.getDvElegido());
							break;
						case 120: // % FRANQUICIA
							es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia fr = es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia.Factory
									.newInstance();
							fr.setCPMod(expCob.getCpm());
							fr.setCodRCub(expCob.getRiesgoCubierto());
							fr.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaFr.add(fr);
							logger.debug("--> CoberturaDV 120 insertada: CPM: " + expCob.getCpm() + RC
									+ expCob.getRiesgoCubierto() + ELEG + expCob.getElegible() + DV_COD_CPTO
									+ expCob.getDvCodConcepto() + DV_VALOR + expCob.getDvValor() + DV_ELEGIDO
									+ expCob.getDvElegido());
							break;
						case 175: // GARANTIZADO
							es.agroseguro.contratacion.datosVariables.Garantizado gar = es.agroseguro.contratacion.datosVariables.Garantizado.Factory
									.newInstance();
							gar.setCPMod(expCob.getCpm());
							gar.setCodRCub(expCob.getRiesgoCubierto());
							gar.setValor(Integer.parseInt(expCob.getDvValor().toString()));
							listaGar.add(gar);
							logger.debug("--> CoberturaDV 175 insertada: CPM: " + expCob.getCpm() + RC
									+ expCob.getRiesgoCubierto() + ELEG + expCob.getElegible() + DV_COD_CPTO
									+ expCob.getDvCodConcepto() + DV_VALOR + expCob.getDvValor() + DV_ELEGIDO
									+ expCob.getDvElegido());
							break;
						default:
							break;
						}
					}
				}
			}
			if (listaMin.size() > 0) {
				es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[] arrayMin = listaMin.toArray(
						new es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[listaMin.size()]);
				if (null == explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayMin.length > 0)
					explot.getDatosVariables().setMinIndemArray(arrayMin);
			}
			if (listaCalc.size() > 0) {
				es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[] arrayCalc = listaCalc
						.toArray(new es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[listaCalc.size()]);
				if (null == explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayCalc.length > 0)
					explot.getDatosVariables().setCalcIndemArray(arrayCalc);
			}
			if (listaCap.size() > 0) {
				es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[] arrayCap = listaCap.toArray(
						new es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[listaCap.size()]);
				if (null == explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayCap.length > 0)
					explot.getDatosVariables().setCapAsegArray(arrayCap);
			}
			if (listaFr.size() > 0) {
				es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[] arrayFr = listaFr
						.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[listaFr.size()]);
				if (null == explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayFr.length > 0)
					explot.getDatosVariables().setFranqArray(arrayFr);
			}
			if (listaGar.size() > 0) {
				es.agroseguro.contratacion.datosVariables.Garantizado[] arrayGar = listaGar
						.toArray(new es.agroseguro.contratacion.datosVariables.Garantizado[listaGar.size()]);
				if (null == explot.getDatosVariables())
					explot.setDatosVariables(DatosVariables.Factory.newInstance());
				if (arrayGar.length > 0)
					explot.getDatosVariables().setGarantArray(arrayGar);
			}
		}
	}

	private static Map<BigDecimal, DatosVariables> getDatosVariablesExplotacion(final Long lineaseguroId,
			final Set<com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable> datosVariables,
			final GenericDao<?> genericDao) throws ValidacionPolizaException {
		BigDecimal codUbicacion;
		Method methodAdd;
		Method methodSet;
		Method methodValor;
		Class<?> valorClass;
		Object objValor;
		String etiquetaXML;
		BigDecimal codConcepto;
		XmlObject datoVar;
		Map<BigDecimal, DatosVariables> result = new HashMap<BigDecimal, DatosVariables>();
		try {
			// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA (31) Y A
			// LAS UBICACIONES DE EXPLOTACION
			Filter oiFilter = new Filter() {
				@Override
				public Criteria getCriteria(final Session sesion) {
					Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class);
					criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroId));
					criteria.add(Restrictions.eq("id.coduso", OrganizadorInfoConstants.USO_POLIZA));
					criteria.add(Restrictions.in("id.codubicacion",
							new Object[] { OrganizadorInfoConstants.UBICACION_ANIMALES,
									OrganizadorInfoConstants.UBICACION_CAP_ASEG,
									OrganizadorInfoConstants.UBICACION_GRUPO_RAZA,
									OrganizadorInfoConstants.UBICACION_EXPLOTACION }));
					return criteria;
				}
			};
			@SuppressWarnings("unchecked")
			List<OrganizadorInformacion> oiList = (List<OrganizadorInformacion>) genericDao.getObjects(oiFilter);
			for (OrganizadorInformacion oi : oiList) {
				codConcepto = oi.getId().getCodconcepto();
				// LOCALIZAMOS EL CONCEPTO EN LOS DATOS VARIABLES DE LA
				// EXPLOTACION
				inner: for (com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable datoVariable : datosVariables) {
					if (datoVariable.getCodconcepto() != null && datoVariable.getValor() != null) {
						if (codConcepto.equals(new BigDecimal(datoVariable.getCodconcepto()))) {
							codUbicacion = oi.getId().getCodubicacion();
							if (result.get(codUbicacion) == null) {
								result.put(codUbicacion, DatosVariables.Factory.newInstance());
							}

							switch (codConcepto.intValue()) {
							// INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO
							// Y DEL RIESGO CUBIERTO
							case 121:// No los tratamos aqui. Son dv de explotacion
							case 174:
							case 361:
							case 363:
								break;
							default:

								etiquetaXML = oi.getDiccionarioDatos().getEtiquetaxml();
								logger.debug("### - 2084 " + etiquetaXML);
								// OBTENEMOS LOS METODOS NECESARIOS PARA AnaDIR LA
								// ETIQUETA XML DEL DATO VARIABLE
								methodAdd = DatosVariables.class.getMethod("addNew" + etiquetaXML);
								logger.debug("### - 2090 " + methodAdd);

								// OBTENEMOS EL OBJETO QUE GESTIONA EL DATO VARIABLE
								datoVar = (XmlObject) methodAdd.invoke(result.get(codUbicacion));
								logger.debug("### - 2097 " + datoVar.getClass().toString());
								// OBTENEMOS LOS METODOS NECESARIOS PARA GESTIONAR EL
								// CONTENIDO DEL DATO VARIABLE
								valorClass = datoVar.getClass().getMethod("getValor").getReturnType();
								logger.debug("### - 2102 " + valorClass.getName());
								methodValor = datoVar.getClass().getMethod("setValor", valorClass);
								logger.debug("### - 2105 " + methodValor.getName());
								// CREAMOS EL VALOR (TIPOS CONOCIDOS AL MOMENTO DEL
								// DESARROLLO...ES POSIBLE QUE SE NECESITEN AnaDIR MAS
								// EN UN FUTURO)
								if (valorClass.equals(List.class)) {
									logger.debug("### - 2110");
									objValor = Arrays.asList(new String[] { datoVariable.getValor() });
								} else if (valorClass.equals(String.class)) {
									logger.debug("### - 2115");
									objValor = datoVariable.getValor();
								} else if (valorClass.equals(Integer.class) || valorClass.equals(int.class)) {
									logger.debug("### - 2119");
									objValor = Integer.valueOf(datoVariable.getValor());
								} else if (valorClass.equals(Long.class) || valorClass.equals(long.class)) {
									logger.debug("### - 2123");
									objValor = Long.valueOf(datoVariable.getValor());
								} else if (valorClass.equals(BigDecimal.class)) {
									logger.debug("### - 2126");
									objValor = new BigDecimal(datoVariable.getValor());
								} else {
									throw new ValidacionPolizaException("Tipo de dato variable no esperado");
								}
								// INYECTAMOS EL VALOR DEL DATO VARIABLE
								logger.debug("### - 2132 " + datoVar.toString());
								logger.debug("### - 2133 " + objValor.toString());
								methodValor.invoke(datoVar, objValor);
								// INYECTAMOS EL DATO VARIABLE
								logger.debug("### - 2136");
								methodSet = DatosVariables.class.getMethod("set" + etiquetaXML,
										methodAdd.getReturnType());
								logger.debug("### - 2073 " + methodSet);
								methodSet.invoke(result.get(codUbicacion), datoVar);
								break inner;
							}

						}
					} else {
						logger.error(
								"Encontrado dato variable con codigo de concepto nulo. Id = " + datoVariable.getId());
					}
				}
			}
		} catch (Exception e) {
			throw new ValidacionPolizaException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Metodo para obtener los datos variables asociados a cada capital asegurado de
	 * una parcela
	 * 
	 * @param datoVariableParcelas
	 *            Conjunto de datos variables asociados a un capital asegurado
	 * @param codLinea
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static DatosVariables getDatosVariablesParcela(Set<DatoVariableParcela> datoVariableParcelas,
			Set<ParcelaCobertura> datoVariableCoberturas, String parIdent,
			Map<BigDecimal, List<String>> listaDatosVariables, String codmodulo, BigDecimal codLinea,
			boolean aplicaReduccionRdto, List<BigDecimal> codsConceptos, Long idPoliza, Long idCapitalAsegurado,
			int tipoCapital) throws ValidacionPolizaException {

		DatosVariables datosVariables = DatosVariables.Factory.newInstance();
		logger.debug("codsConceptos          : " + codsConceptos);
		logger.debug("listaDatosVariables    : " + listaDatosVariables);
		logger.debug("datoVariableCoberturas : " + datoVariableCoberturas);

		/* Incidencia Pasada por RGA 03.12.2020 **/
		/* Revisamos si hay coberturas de Parcela contratadas */
		boolean incluirRiesgos = true;

		for (DatoVariableParcela dvp : datoVariableParcelas) {
			logger.debug("Tratando dv : " + dvp.getDiccionarioDatos().getCodconcepto());
			if (codsConceptos.contains(dvp.getDiccionarioDatos().getCodconcepto())) {
				if (listaDatosVariables.containsKey(dvp.getDiccionarioDatos().getCodconcepto())) {
					// Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getDiccionarioDatos().getCodconcepto());

					for (String cad : auxDatVar) {
						String[] auxValores = cad.split("#");
						incluirRiesgos = true;

						if (codLinea.equals(new BigDecimal(301))) {

							if (idCapitalAsegurado == null) {

								/*
								 * Si se trata de la linea 301 tendremos que disgregar tambien por capital
								 * Asegurado
								 */
								if (auxValores[0].equals(parIdent) && auxValores[3].equals(dvp.getValor())) {
									try {

										for (ParcelaCobertura ParcCob : datoVariableCoberturas) {
											BigDecimal codRiesg = new BigDecimal(363);
											BigDecimal noContratado = new BigDecimal(-2);

											if (ParcCob.getDiccionarioDatos().getCodconcepto()
													.compareTo(codRiesg) == 0) {
												BigDecimal cptoPpal = ParcCob.getConceptoPpalModulo()
														.getCodconceptoppalmod();
												BigDecimal riesgoCub = ParcCob.getRiesgoCubierto().getId()
														.getCodriesgocubierto();
												logger.debug("Valor de cptoPpal: " + cptoPpal
														+ " y valor de auxValores[1] " + auxValores[1]);
												logger.debug("Valor de riesgoCub: " + riesgoCub
														+ " y valor de auxValores[2] " + auxValores[2]);
												if (riesgoCub.compareTo(new BigDecimal(auxValores[2])) == 0
														&& cptoPpal.compareTo(new BigDecimal(auxValores[1])) == 0) {
													if (ParcCob.getCodvalor().compareTo(noContratado) == 0) {
														incluirRiesgos = false;
													}
												}
											}
										}

										if (incluirRiesgos) {
											setDatoVariableRiesgo(dvp.getDiccionarioDatos().getCodconcepto(),
													new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]),
													auxValores[3], datosVariables);
										}
									} catch (ParseException e) {
										throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (NumberFormatException e) {
										throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (Exception e) {
										throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									}
								}
							} else {

								/*
								 * Si se trata de la linea 301 tendremos que disgregar tambien por capital
								 * Asegurado
								 */
								String cadAux = auxValores[0];
								String[] auxidenParc = cadAux.split("\\.");

								Long idCapAsegAux = Long.valueOf(auxidenParc[7]);
								String parIdentAux = auxidenParc[0] + "." + auxidenParc[1] + "." + auxidenParc[2] + "."
										+ auxidenParc[3] + "." + auxidenParc[4] + "." + auxidenParc[5] + "."
										+ auxidenParc[6];
								if (parIdentAux.equals(parIdent) && auxValores[3].equals(dvp.getValor())
										&& idCapAsegAux.compareTo(idCapitalAsegurado) == 0) {
									try {

										/* Modif 01.12.2020 */
										/*
										 * Por solicitud de RGA los tipo de Capital = '1' de la 301 no deben enviar
										 * datos del riesgo
										 */
										if (tipoCapital == 0) {

											for (ParcelaCobertura ParcCob : datoVariableCoberturas) {
												BigDecimal codRiesg = new BigDecimal(363);
												BigDecimal noContratado = new BigDecimal(-2);

												if (ParcCob.getDiccionarioDatos().getCodconcepto()
														.compareTo(codRiesg) == 0) {
													BigDecimal cptoPpal = ParcCob.getConceptoPpalModulo()
															.getCodconceptoppalmod();
													BigDecimal riesgoCub = ParcCob.getRiesgoCubierto().getId()
															.getCodriesgocubierto();
													if (riesgoCub.compareTo(new BigDecimal(auxValores[2])) == 0
															&& cptoPpal.compareTo(new BigDecimal(auxValores[1])) == 0) {
														if (ParcCob.getCodvalor().compareTo(noContratado) == 0) {
															incluirRiesgos = false;
														}
													}
												}
											}
											if (incluirRiesgos) {
												setDatoVariableRiesgo(dvp.getDiccionarioDatos().getCodconcepto(),
														new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]),
														auxValores[3], datosVariables);
											}
										}
									} catch (ParseException e) {
										throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (NumberFormatException e) {
										throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (Exception e) {
										throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									}
								}
							}

						} else {
							/* ESC-12511 ** MODIF TAM (15.02.2021) ** Inicio */
							/*
							 * Si se trata de una linea de Agricola tambien tenemos que disgregar por el
							 * idCapitalAsegurado
							 */
							if (idCapitalAsegurado != null) {

								String cadAux = auxValores[0];
								String[] auxidenParc = cadAux.split("\\.");

								Long idCapAsegAux = Long.valueOf(auxidenParc[7]);
								String parIdentAux = auxidenParc[0] + "." + auxidenParc[1] + "." + auxidenParc[2] + "."
										+ auxidenParc[3] + "." + auxidenParc[4] + "." + auxidenParc[5] + "."
										+ auxidenParc[6];
								if (parIdentAux.equals(parIdent) && auxValores[3].equals(dvp.getValor())
										&& idCapAsegAux.compareTo(idCapitalAsegurado) == 0) {
									try {

										setDatoVariableRiesgo(dvp.getDiccionarioDatos().getCodconcepto(),
												new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]),
												auxValores[3], datosVariables);

									} catch (ParseException e) {
										throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (NumberFormatException e) {
										throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (Exception e) {
										throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									}
								}

							} else {
								/* ESC-11828 ** MODIF TAM (21.12.2020) ** Fin */
								if (auxValores[0].equals(parIdent) && auxValores[3].equals(dvp.getValor())) {
									try {
										setDatoVariableRiesgo(dvp.getDiccionarioDatos().getCodconcepto(),
												new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]),
												auxValores[3], datosVariables);
									} catch (ParseException e) {
										throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (NumberFormatException e) {
										throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									} catch (Exception e) {
										throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
									}
								}
							}
						}
					}
				} else {
					try {
						// MPM - 20/11/2012
						// Si el valor del dato variables es nulo no se inserta
						// SE EXCLUYEN LOS VALORES 0 POR ORDEN EXPRESA DE RGA
						// CORREO 'RE: GDLD-50776 - PTC-6488 - PP linea 300/2020' DEL 19/11/2020
						if (dvp.getValor() != null && !"0".equals(dvp.getValor()) && !"null".equals(dvp.getValor())) {
							String valorLimpio = dvp.getValor().replace("[", "").replace("]", "").replace("\"", "").replace(",", " ");
							setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(), null, null, valorLimpio,
									datosVariables, aplicaReduccionRdto);
						}
					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					} catch (Exception e) {
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					}
				}
			}
		}

		/* Modif TAM 30.11.2020 */
		if (codLinea.equals(new BigDecimal(301))) {

			if (tipoCapital == 0) {

				Map<String, List<String>> lstCoberturas = new HashMap<String, List<String>>();
				List<String> lista = new ArrayList<String>();
				// Sacamos las coberturas del pl
				int i = 0;
				do {

					try {

						lstCoberturas = seleccionPolizaDao.getCoberturasCapAseg301(idPoliza, codmodulo);

						lista = lstCoberturas.get(idCapitalAsegurado + "#" + i);
						// MPM - 20/11/2012
						// Si el valor del dato variables es nulo no se inserta
						if (lista != null && lista.size() > 0 && lista.get(3) != null)
							setDatoVariable(new BigDecimal(363), new BigDecimal(lista.get(1)),
									new BigDecimal(lista.get(2)), new BigDecimal(lista.get(3)), datosVariables,
									aplicaReduccionRdto);
					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
					} catch (Exception e) {
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
					}
				} while (lstCoberturas.get(idCapitalAsegurado + "#" + (++i)) != null);

				/* Para los datos variables de las coberturas de parcelas */
				if (incluirRiesgos) {
					for (ParcelaCobertura pc : datoVariableCoberturas) {
						if (pc.getRiesgoCubierto().getId().getCodmodulo().equals(codmodulo)) {
							try {
								// MPM - 20/11/2012
								// Si el valor del dato variables es nulo no se inserta
								if (pc.getCodvalor() != null && pc.getDiccionarioDatos().getCodconcepto()
										.compareTo(new BigDecimal(363)) != 0) {
									setDatoVariable(pc.getDiccionarioDatos().getCodconcepto(),
											pc.getConceptoPpalModulo().getCodconceptoppalmod(),
											pc.getRiesgoCubierto().getId().getCodriesgocubierto(), pc.getCodvalor(),
											datosVariables, aplicaReduccionRdto);
								}
							} catch (ParseException e) {
								throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
							} catch (NumberFormatException e) {
								throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
							} catch (Exception e) {
								throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
							}
						}
					} /* Fin del For */
				} /* Fin del if */
			}
		} else {
			for (ParcelaCobertura pc : datoVariableCoberturas) {
				if (pc.getRiesgoCubierto().getId().getCodmodulo().equals(codmodulo)) {
					try {
						// MPM - 20/11/2012
						// Si el valor del dato variables es nulo no se inserta
						if (pc.getCodvalor() != null) {
							setDatoVariable(pc.getDiccionarioDatos().getCodconcepto(),
									pc.getConceptoPpalModulo().getCodconceptoppalmod(),
									pc.getRiesgoCubierto().getId().getCodriesgocubierto(), pc.getCodvalor(),
									datosVariables, aplicaReduccionRdto);
						}
					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					} catch (Exception e) {
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					}
				}
			}
		}
		return datosVariables;
	}

	/****
	 * Pet. 57626 ** MODIF TAM (21.07.2020) ** Resolucion de Incidencias ** Inicio
	 **/
	/**
	 * Metodo para obtener los datos variables asociados a cada capital asegurado de
	 * una parcela
	 * 
	 * @param datoVariableParcelas
	 *            Conjunto de datos variables asociados a un capital asegurado
	 * @param codLinea
	 * @return
	 * @throws ParseException
	 */
	private static DatosVariables getDatosVariablesParcelaCpl(Set<DatoVariableParcela> datoVariableParcelas,
			Set<ParcelaCobertura> datoVariableCoberturas, Long idCapitalAsegurado,
			Map<BigDecimal, List<String>> listaDatosVariables, String codmodulo, BigDecimal codLinea,
			boolean aplicaReduccionRdto, List<BigDecimal> codsConceptos, Long idPoliza)
			throws ValidacionPolizaException {

		DatosVariables datosVariables = DatosVariables.Factory.newInstance();
		
		if (listaDatosVariables != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < listaDatosVariables.size(); i++) {
				sb.append(listaDatosVariables.get(i) + ", ");
			}
		}
		for (DatoVariableParcela dvp : datoVariableParcelas) {
			if (codsConceptos.contains(dvp.getDiccionarioDatos().getCodconcepto())) {
				if (listaDatosVariables.containsKey(dvp.getDiccionarioDatos().getCodconcepto())) {
					// Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getDiccionarioDatos().getCodconcepto());

					for (String cad : auxDatVar) {
						String[] auxValores = cad.split("#");
						Long auxIdCapAseg = new Long(auxValores[0]);

						if (auxIdCapAseg.equals(idCapitalAsegurado)) {
							try {

								setDatoVariableRiesgoCpl(dvp.getDiccionarioDatos().getCodconcepto(),
										new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]), auxValores[3],
										datosVariables);

							} catch (ParseException e) {
								throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							} catch (NumberFormatException e) {
								throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							} catch (Exception e) {
								logger.error(e);
								throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							}
						}
					}

				} else {

					try {
						String valorLimpio = dvp.getValor().replace("[", "").replace("]", "").replace("\"", "").replace(",", " ");
						if (dvp.getCodconceptoppalmod() != null && dvp.getCodriesgocubierto() != null) {
							setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(),
									new BigDecimal(dvp.getCodconceptoppalmod()),
									new BigDecimal(dvp.getCodriesgocubierto()), valorLimpio, datosVariables,
									aplicaReduccionRdto);
						} else {
							setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(), null, null, valorLimpio,
									datosVariables, aplicaReduccionRdto);
						}

					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					} catch (Exception e) {
						logger.error(e);
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					}

				}
			}
		}

		if (codLinea.equals(new BigDecimal(301))) {

			Map<String, List<String>> lstCoberturas = new HashMap<String, List<String>>();
			List<String> lista = new ArrayList<String>();
			// Sacamos las coberturas del pl
			int i = 0;
			do {

				try {
					lstCoberturas = seleccionPolizaDao.getCoberturasCapAseg301(idPoliza, codmodulo);

					lista = lstCoberturas.get(idCapitalAsegurado + "#" + i);

					if (lista != null && lista.size() > 0)
						setDatoVariable(new BigDecimal(363), new BigDecimal(lista.get(1)), new BigDecimal(lista.get(2)),
								new BigDecimal(lista.get(3)), datosVariables, aplicaReduccionRdto);
				} catch (ParseException e) {
					throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
				} catch (NumberFormatException e) {
					throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
				} catch (Exception e) {
					logger.error(e);
					throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
				}
			} while (lstCoberturas.get(idCapitalAsegurado + "#" + (++i)) != null);

		} else {
			for (ParcelaCobertura pc : datoVariableCoberturas) {
				if (pc.getRiesgoCubierto().getId().getCodmodulo().equals(codmodulo)) {
					try {
						setDatoVariable(pc.getDiccionarioDatos().getCodconcepto(),
								pc.getConceptoPpalModulo().getCodconceptoppalmod(),
								pc.getRiesgoCubierto().getId().getCodriesgocubierto(), pc.getCodvalor(), datosVariables,
								aplicaReduccionRdto);
					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					} catch (Exception e) {
						logger.error(e);
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					}
				}
			}
		}
		return datosVariables;
	}
	/****
	 * Pet. 57626 ** MODIF TAM (21.07.2020) ** Resolucion de Incidencias ** Fin
	 **/

	/*
	 * Incidencia RGA en Complementarias (01.12.2020) - Pet. 63485 y 50776 * Inicio
	 */
	/**
	 * Metodo para obtener los datos variables asociados a cada capital asegurado de
	 * una parcela
	 * 
	 * @param datoVariableParcelas
	 *            Conjunto de datos variables asociados a un capital asegurado
	 * @param codLinea
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static DatosVariables getDatosVarParcComple(Set<DatoVariableParcela> datoVariableParcelas,
			Set<ParcelaCobertura> datoVariableCoberturas, Long idCapitalAsegurado,
			Map<BigDecimal, List<String>> listaDatosVariables, String codmodulo, BigDecimal codLinea,
			boolean aplicaReduccionRdto, List<BigDecimal> codsConceptos, Long idPoliza)
			throws ValidacionPolizaException {

		DatosVariables datosVariables = DatosVariables.Factory.newInstance();
		logger.debug("codsConceptos           : " + codsConceptos);
		logger.debug("listaDatosVariables     : " + listaDatosVariables);
		logger.debug("datoVariableCoberturas  : " + datoVariableCoberturas);
		for (DatoVariableParcela dvp : datoVariableParcelas) {
			logger.debug("Tratando dv             : " + dvp.getDiccionarioDatos().getCodconcepto());
			if (codsConceptos.contains(dvp.getDiccionarioDatos().getCodconcepto())) {
				if (listaDatosVariables.containsKey(dvp.getDiccionarioDatos().getCodconcepto())) {
					// Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getDiccionarioDatos().getCodconcepto());
					for (String cad : auxDatVar) {
						String[] auxValores = cad.split("#");
						Long auxIdCapAseg = new Long(auxValores[0]);
						if (auxIdCapAseg.equals(idCapitalAsegurado)) {
							try {
								setDatoVariableRiesgo(dvp.getDiccionarioDatos().getCodconcepto(),
										new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]), auxValores[3],
										datosVariables);
							} catch (ParseException e) {
								throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							} catch (NumberFormatException e) {
								throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							} catch (Exception e) {
								throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
							}
						}
					}
				} else {
					try {
						// MPM - 20/11/2012
						// Si el valor del dato variables es nulo no se inserta
						if (dvp.getValor() != null) {
							String valorLimpio = dvp.getValor().replace("[", "").replace("]", "").replace("\"", "").replace(",", " ");
							if (dvp.getCodconceptoppalmod() != null && dvp.getCodriesgocubierto() != null) {
								setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(),
										new BigDecimal(dvp.getCodconceptoppalmod()),
										new BigDecimal(dvp.getCodriesgocubierto()), valorLimpio, datosVariables,
										aplicaReduccionRdto);
							} else {
								setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(), null, null, valorLimpio,
										datosVariables, aplicaReduccionRdto);
							}

						}
					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					} catch (Exception e) {
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
					}
				}
			}
		}

		// Sacamos las coberturas del pl
		if (codLinea.equals(new BigDecimal(301))) {

			Map<String, List<String>> lstCoberturas = new HashMap<String, List<String>>();
			List<String> lista = new ArrayList<String>();
			// Sacamos las coberturas del pl
			int i = 0;
			do {

				try {
					lstCoberturas = seleccionPolizaDao.getCoberturasCapAseg301(idPoliza, codmodulo);
					lista = lstCoberturas.get(idCapitalAsegurado + "#" + i);
					// MPM - 20/11/2012
					// Si el valor del dato variables es nulo no se inserta
					if (lista != null && lista.size() > 0 && lista.get(3) != null)
						setDatoVariable(new BigDecimal(363), new BigDecimal(lista.get(1)), new BigDecimal(lista.get(2)),
								new BigDecimal(lista.get(3)), datosVariables, aplicaReduccionRdto);
				} catch (ParseException e) {
					throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
				} catch (NumberFormatException e) {
					throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
				} catch (Exception e) {
					throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", lista.get(3)).replace("{dato}", "363"));
				}
			} while (lstCoberturas.get(idCapitalAsegurado + "#" + (++i)) != null);
		} else {
			for (ParcelaCobertura pc : datoVariableCoberturas) {
				if (pc.getRiesgoCubierto().getId().getCodmodulo().equals(codmodulo)) {
					try {
						// MPM - 20/11/2012
						// Si el valor del dato variables es nulo no se inserta
						if (pc.getCodvalor() != null) {
							setDatoVariable(pc.getDiccionarioDatos().getCodconcepto(),
									pc.getConceptoPpalModulo().getCodconceptoppalmod(),
									pc.getRiesgoCubierto().getId().getCodriesgocubierto(), pc.getCodvalor(),
									datosVariables, aplicaReduccionRdto);
						}
					} catch (ParseException e) {
						throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					} catch (NumberFormatException e) {
						throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					} catch (Exception e) {
						throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", pc.getCodvalor().toString()).replace("{dato}", pc.getDiccionarioDatos().getNomconcepto()));
					}
				}
			}
		}
		return datosVariables;
	}

	/* Incidencia RGA en Complementarias (01.12.2020) - Pet. 63485 y 50776 * Fin */

	/****
	 * Pet. 57626 ** MODIF TAM (21.07.2020) ** Resolucion de Incidencias ** Fin
	 **/

	/**
	 * metodo para obtener los datos variables asociados a las coberturas de una
	 * comparativa
	 * 
	 * @param poliza
	 *            Poliza
	 * @param cp
	 *            Comparativa
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static DatosVariables getDatosVariablesCobertura(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, boolean aplicaReduccionRdto, List<BigDecimal> listaCPM)
			throws ValidacionPolizaException {

		Set<ModuloPoliza> moduloPolizas = poliza.getModuloPolizas();

		DatosVariables datosVariables = DatosVariables.Factory.newInstance();
		for (ComparativaPoliza comp : poliza.getComparativaPolizas()) {

			// MPM - 20/11/2012
			// Si el valor del dato variables es nulo no se inserta
			if (null != comp.getId().getIdComparativa() && null != cp.getId().getIdComparativa()) {
				if (comp.getId().getCodmodulo().equals(cp.getId().getCodmodulo())
						&& comp.getId().getIdComparativa().equals(cp.getId().getIdComparativa())
						&& comp.getId().getCodvalor() != null) {

					// Comprueba si hay que insertar el CPM como dato variable
					if (CoberturasUtils.isCPMPermitido(comp.getId().getCodconceptoppalmod(),
							comp.getId().getCodconcepto(), listaCPM)) {
						try {
							setDatoVariable(comp.getId().getCodconcepto(), comp.getId().getCodconceptoppalmod(),
									comp.getId().getCodriesgocubierto(), comp.getId().getCodvalor(), datosVariables,
									aplicaReduccionRdto);
						} catch (ParseException e) {
							throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", comp.getId().getCodvalor().toString()).replace("{dato}", comp.getId().getCodconcepto().toString()));
						} catch (NumberFormatException e) {
							throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", comp.getId().getCodvalor().toString()).replace("{dato}", comp.getId().getCodconcepto().toString()));
						} catch (Exception e) {
							throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", comp.getId().getCodvalor().toString()).replace("{dato}", comp.getId().getCodconcepto().toString()));
						}
					}
				}
			} else {
				if (comp.getId().getCodmodulo().equals(cp.getId().getCodmodulo())
						&& comp.getId().getFilacomparativa().equals(cp.getId().getFilacomparativa())
						&& comp.getId().getCodvalor() != null) {

					// Comprueba si hay que insertar el CPM como dato variable
					if (CoberturasUtils.isCPMPermitido(comp.getId().getCodconceptoppalmod(),
							comp.getId().getCodconcepto(), listaCPM)) {
						try {
							setDatoVariable(comp.getId().getCodconcepto(), comp.getId().getCodconceptoppalmod(),
									comp.getId().getCodriesgocubierto(), comp.getId().getCodvalor(), datosVariables,
									aplicaReduccionRdto);
						} catch (ParseException e) {
							throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", comp.getId().getCodvalor().toString()).replace("{dato}", comp.getId().getCodconcepto().toString()));
						} catch (NumberFormatException e) {
							throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", comp.getId().getCodvalor().toString()).replace("{dato}", comp.getId().getCodconcepto().toString()));
						} catch (Exception e) {
							throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", comp.getId().getCodvalor().toString()).replace("{dato}", comp.getId().getCodconcepto().toString()));
						}
					}
				}
			}

			try {
				// MPM - 19/05/2016
				// Inclusion del dato variable de cobertura 'Tipo de asegurado de ganado'
				for (ModuloPoliza mp : moduloPolizas) {
					if (null != mp.getId().getNumComparativa() && null != cp.getId().getIdComparativa()) {
						if (mp.getId().getCodmodulo().equals(cp.getId().getCodmodulo()) && mp.getId()
								.getNumComparativa().toString().equals(cp.getId().getIdComparativa().toString())) {
							if (mp.getTipoAsegGanado() != null) {
								TipoAseguradoGanado tag = TipoAseguradoGanado.Factory.newInstance();
								tag.setValor(mp.getTipoAsegGanado());
								datosVariables.setTAseGan(tag);
							}
						}
					} else {
						if (mp.getId().getCodmodulo().equals(cp.getId().getCodmodulo())) {
							if (mp.getTipoAsegGanado() != null) {
								TipoAseguradoGanado tag = TipoAseguradoGanado.Factory.newInstance();
								tag.setValor(mp.getTipoAsegGanado());
								datosVariables.setTAseGan(tag);
							}
						}
					}
				}
			} catch (Exception e) {
				throw new ValidacionPolizaException(
						"Error al insertar el dato variable de cobertura TipoAseguradoGanado");
			}

		}

		return datosVariables;
	}

	/**
	 * metodo para establecer cada dato variable en el objeto 'DatosVariables'.
	 * Sirve tanto para Coberturas como para Capitales Asegurados
	 * 
	 * @param codconcepto
	 *            Codigo del concepto a Anadir
	 * @param codconceptoppalmod
	 *            Concepto principal del modulo del cual depende el codconcepto (si
	 *            procede). Puede ser null.
	 * @param codriesgocubierto
	 *            Riesgo cubierto del cual depende el codconcepto (si procede).
	 *            Puede ser null.
	 * @param valor
	 *            Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String
	 *            (Capital Asegurado)
	 * @param datosVariables
	 *            Estructura donde se van Anadiendo los datos variables.
	 * @throws ParseException
	 *             Error al parsear fechas.
	 */
	@SuppressWarnings("unchecked")
	private static void setDatoVariable(BigDecimal codconcepto, BigDecimal codconceptoppalmod,
			BigDecimal codriesgocubierto, Object valor, DatosVariables datosVariables, boolean aplicaReduccionRdto)
			throws ParseException, NumberFormatException {

		// Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FECHA);
		// Comienzo del tratamiento del dato variable
		switch (codconcepto.intValue()) {
		// INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO
		// Y DEL RIESGO CUBIERTO
		case ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO:
			List<PorcentajeCapitalAsegurado> lstCA = Arrays.asList(datosVariables.getCapAsegArray());
			ArrayList<PorcentajeCapitalAsegurado> lstCAA = new ArrayList<PorcentajeCapitalAsegurado>(lstCA);
			PorcentajeCapitalAsegurado capital = PorcentajeCapitalAsegurado.Factory.newInstance();
			capital.setCodRCub(codriesgocubierto.intValue());
			capital.setCPMod(codconceptoppalmod.intValue());
			capital.setValor(Integer.parseInt(valor.toString()));
			lstCAA.add(capital);
			datosVariables.setCapAsegArray(lstCAA.toArray(new PorcentajeCapitalAsegurado[lstCAA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_PCT_FRANQUICIA:
			/* Pet. 57626 ** MODIF TAM (09.06.2020) ** Inicio */
			if (codriesgocubierto == null || codconceptoppalmod == null)
				break;
			List<PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
			ArrayList<PorcentajeFranquicia> lstFA = new ArrayList<PorcentajeFranquicia>(lstF);
			PorcentajeFranquicia f = PorcentajeFranquicia.Factory.newInstance();
			f.setCodRCub(codriesgocubierto.intValue());
			f.setCPMod(codconceptoppalmod.intValue());
			f.setValor(Integer.parseInt(valor.toString()));
			lstFA.add(f);
			datosVariables.setFranqArray(lstFA.toArray(new PorcentajeFranquicia[lstFA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE:
			/* Pet. 57626 ** MODIF TAM (09.06.2020) ** Inicio */
			if (codriesgocubierto == null || codconceptoppalmod == null)
				break;
			List<PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
			ArrayList<PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);
			PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
			if (null != codriesgocubierto)
				m.setCodRCub(codriesgocubierto.intValue());
			if (null != codconceptoppalmod)
				m.setCPMod(codconceptoppalmod.intValue());
			m.setValor(Integer.parseInt(valor.toString()));
			lstMIA.add(m);
			datosVariables.setMinIndemArray(lstMIA.toArray(new PorcentajeMinimoIndemnizable[lstMIA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION:
			List<CalculoIndemnizacion> lstCI = Arrays.asList(datosVariables.getCalcIndemArray());
			ArrayList<CalculoIndemnizacion> lstCIA = new ArrayList<CalculoIndemnizacion>(lstCI);
			CalculoIndemnizacion c = CalculoIndemnizacion.Factory.newInstance();
			c.setCodRCub(codriesgocubierto.intValue());
			c.setCPMod(codconceptoppalmod.intValue());
			c.setValor(Integer.parseInt(valor.toString()));
			lstCIA.add(c);
			datosVariables.setCalcIndemArray(lstCIA.toArray(new CalculoIndemnizacion[lstCIA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_DANHOS_CUBIERTOS:
			List<DaniosCubiertos> lstDNC = Arrays.asList(datosVariables.getDnCbtosArray());
			ArrayList<DaniosCubiertos> lstDNCA = new ArrayList<DaniosCubiertos>(lstDNC);
			DaniosCubiertos dan = DaniosCubiertos.Factory.newInstance();
			dan.setCodRCub(codriesgocubierto.intValue());
			dan.setCPMod(codconceptoppalmod.intValue());
			dan.setValor(valor + VACIO);
			lstDNCA.add(dan);
			datosVariables.setDnCbtosArray(lstDNCA.toArray(new DaniosCubiertos[lstDNCA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_GARANTIZADO:
			List<Garantizado> lstG = Arrays.asList(datosVariables.getGarantArray());
			ArrayList<Garantizado> lstGA = new ArrayList<Garantizado>(lstG);
			Garantizado garant = Garantizado.Factory.newInstance();
			garant.setCodRCub(codriesgocubierto.intValue());
			garant.setCPMod(codconceptoppalmod.intValue());
			garant.setValor(Integer.parseInt(valor.toString()));
			lstGA.add(garant);
			datosVariables.setGarantArray(lstGA.toArray(new Garantizado[lstGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO:
		case 0:
			BigDecimal valDec = new BigDecimal(valor.toString());
			BigDecimal contratada = new BigDecimal(-1);
			BigDecimal noContratada = new BigDecimal(-2);
			if (valDec.compareTo(contratada) == 0 || valDec.compareTo(noContratada) == 0) {
				List<RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
				ArrayList<RiesgoCubiertoElegido> lstRCEA = new ArrayList<RiesgoCubiertoElegido>(lstRCE);
				RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
				rCubEleg.setCodRCub(codriesgocubierto.intValue());
				rCubEleg.setCPMod(codconceptoppalmod.intValue());
				if (valDec.compareTo(contratada) == 0)
					rCubEleg.setValor("S");
				else if (valDec.compareTo(noContratada) == 0)
					rCubEleg.setValor("N");
				lstRCEA.add(rCubEleg);
				datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new RiesgoCubiertoElegido[lstRCEA.size()]));
			}
			break;
		case ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA:
			List<TipoFranquicia> lstTF = Arrays.asList(datosVariables.getTipFranqArray());
			ArrayList<TipoFranquicia> lstTFA = new ArrayList<TipoFranquicia>(lstTF);
			TipoFranquicia tipFranq = TipoFranquicia.Factory.newInstance();
			tipFranq.setCodRCub(codriesgocubierto.intValue());
			tipFranq.setCPMod(codconceptoppalmod.intValue());
			tipFranq.setValor(valor + VACIO);
			lstTFA.add(tipFranq);
			datosVariables.setTipFranqArray(lstTFA.toArray(new TipoFranquicia[lstTFA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_TIPO_RENDIMIENTO:
			List<TipoRendimiento> lstTR = Arrays.asList(datosVariables.getTipRdtoArray());
			ArrayList<TipoRendimiento> lstTRA = new ArrayList<TipoRendimiento>(lstTR);
			TipoRendimiento tipRdto = TipoRendimiento.Factory.newInstance();
			tipRdto.setCodRCub(codriesgocubierto.intValue());
			tipRdto.setCPMod(codconceptoppalmod.intValue());
			tipRdto.setValor(Integer.parseInt(valor.toString()));
			lstTRA.add(tipRdto);
			datosVariables.setTipRdtoArray(lstTRA.toArray(new TipoRendimiento[lstTRA.size()]));
			break;
		// FIN DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y
		// DEL RIESGO CUBIERTO
		// INICIO DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL
		// MODULO Y DEL RIESGO CUBIERTO
		case ConstantsConceptos.CODCPTO_ROTACION:
			Rotacion alt = Rotacion.Factory.newInstance();
			alt.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setRot(alt);
			break;
		case ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION:
			CaracteristicasExplotacion carExlp = CaracteristicasExplotacion.Factory.newInstance();
			carExlp.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setCarExpl(carExlp);
			break;
		case ConstantsConceptos.CODCPTO_CICLOCULTIVO:
			CicloCultivo ciCul = CicloCultivo.Factory.newInstance();
			ciCul.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setCiCul(ciCul);
			break;
		case ConstantsConceptos.CODCPTO_IGP:
			// SI EL VALOR DE IGP ES 0 NO LO ANHADIMOS
			if (!valor.toString().equals("0")) {
				CodigoIGP igp = CodigoIGP.Factory.newInstance();
				igp.setValor(Integer.parseInt(valor.toString()));
				datosVariables.setCodIGP(igp);
			}
			break;
		case ConstantsConceptos.CODCPTO_REDUCCION_RDTOS:
			if (aplicaReduccionRdto) {
				List<Integer> lstRedRdto = new ArrayList<Integer>();
				CodigoReduccionRdtos redRdto = datosVariables.getCodRedRdto();
				if (redRdto == null) {
					redRdto = CodigoReduccionRdtos.Factory.newInstance();
				} else {
					lstRedRdto.addAll((List<Integer>) redRdto.getValor());
				}
				lstRedRdto.add(Integer.parseInt(valor.toString()));
				redRdto.setValor(lstRedRdto);
				datosVariables.setCodRedRdto(redRdto);
			}
			break;
		case ConstantsConceptos.CODCPTO_DENOMORIGEN:
			DenominacionOrigen denOrig = DenominacionOrigen.Factory.newInstance();
			denOrig.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setCodDO(denOrig);
			break;
		case ConstantsConceptos.CODCPTO_DENSIDAD:
			Densidad dens = Densidad.Factory.newInstance();
			dens.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setDens(dens);
			break;
		case ConstantsConceptos.CODCPTO_DESTINO:
			Destino dest = Destino.Factory.newInstance();
			dest.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setDest(dest);
			break;
		case ConstantsConceptos.CODCPTO_EDAD:
			Edad edad = Edad.Factory.newInstance();
			edad.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setEdad(edad);
			break;
		case ConstantsConceptos.CODCPTO_FECHA_RECOLEC:
			FechaRecoleccion fRecol = FechaRecoleccion.Factory.newInstance();
			GregorianCalendar gcFRecol = new GregorianCalendar();
			gcFRecol.setTime(sdf.parse(valor.toString()));
			fRecol.setValor(gcFRecol);
			datosVariables.setFecRecol(fRecol);
			break;
		case ConstantsConceptos.CODCPTO_FECSIEMBRA:
			FechaSiembraTrasplante fSiembraTransplante = FechaSiembraTrasplante.Factory.newInstance();
			GregorianCalendar gcFSiemb = new GregorianCalendar();
			gcFSiemb.setTime(sdf.parse(valor.toString()));
			fSiembraTransplante.setValor(gcFSiemb);
			datosVariables.setFecSiemTrasp(fSiembraTransplante);
			break;
		case ConstantsConceptos.CODCPTO_GASTOS_SALVAMENTO:
			IndicadorGastosSalvamento gastSalv = IndicadorGastosSalvamento.Factory.newInstance();
			gastSalv.setValor(valor.toString());
			datosVariables.setIndGastSalv(gastSalv);
			break;
		case ConstantsConceptos.CODCPTO_MEDIDA_PREVENTIVA:
			List<Integer> lstValores = new ArrayList<Integer>();
			String[] valores = valor.toString().split(" ");
			for (String val : valores) {
				lstValores.add(Integer.parseInt(val));
			}
			MedidaPreventiva medPrev = MedidaPreventiva.Factory.newInstance();
			medPrev.setValor(lstValores);
			datosVariables.setMedPrev(medPrev);
			break;
		case ConstantsConceptos.CODCPTO_METROS_CUADRADOS:
			MetrosCuadrados met2 = MetrosCuadrados.Factory.newInstance();
			met2.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setMet2(met2);
			break;
		case ConstantsConceptos.CODCPTO_METROS_LINEALES:
			MetrosLineales met = MetrosLineales.Factory.newInstance();
			met.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setMet(met);
			break;
		case ConstantsConceptos.CODCPTO_NUMANIOSPODA:
			NumeroAniosDesdePoda nadp = NumeroAniosDesdePoda.Factory.newInstance();
			nadp.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setNadp(nadp);
			break;
		case ConstantsConceptos.CODCPTO_UNIDADES:
			NumeroUnidades numUds = NumeroUnidades.Factory.newInstance();
			numUds.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setNumUnid(numUds);
			break;
		case ConstantsConceptos.CODCPTO_PRACTCULT:
			PracticaCultural praCul = PracticaCultural.Factory.newInstance();
			praCul.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setPraCult(praCul);
			break;
		case ConstantsConceptos.CODCPTO_SISTCOND:
			SistemaConduccion sCond = SistemaConduccion.Factory.newInstance();
			sCond.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setSisCond(sCond);
			break;
		case ConstantsConceptos.CODCPTO_SISTCULTIVO:
			SistemaCultivo sCul = SistemaCultivo.Factory.newInstance();
			sCul.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setSisCult(sCul);
			break;
		case ConstantsConceptos.CODCPTO_SISTEMA_PRODUCCION:
			SistemaProduccion sProd = SistemaProduccion.Factory.newInstance();
			sProd.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setSisProd(sProd);
			break;
		case ConstantsConceptos.CODCPTO_SISTEMA_PROTECCION:
			SistemaProteccion sProt = SistemaProteccion.Factory.newInstance();
			sProt.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setSisProt(sProt);
			break;
		case ConstantsConceptos.CODCPTO_TIPMARCOPLANT:
			TipoMarcoPlantacion tmp = TipoMarcoPlantacion.Factory.newInstance();
			tmp.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setTipMcoPlant(tmp);
			break;
		case ConstantsConceptos.CODCPTO_TIPO_PLANTACION:
			TipoPlantacion tp = TipoPlantacion.Factory.newInstance();
			tp.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setTipPlant(tp);
			break;
		case ConstantsConceptos.CODCPTO_SUBV_DEC_PARC:
			TipoSubvencionDeclaradaParcela tsdp = datosVariables.getTipSubDecPar();
			List<Integer> aux = new ArrayList<Integer>();
			if (tsdp == null) {
				tsdp = TipoSubvencionDeclaradaParcela.Factory.newInstance();
			}
			if (tsdp.getValor() == null) {
				aux.add(Integer.valueOf(valor.toString()));
			}
			tsdp.setValor(aux);
			datosVariables.setTipSubDecPar(tsdp);
			break;
		case ConstantsConceptos.CODCPTO_VALOR_FIJO:
			ValorFijo vf = ValorFijo.Factory.newInstance();
			vf.setValor(new BigDecimal(valor.toString()));
			datosVariables.setValFij(vf);
			break;
		case ConstantsConceptos.CODCPTO_TIPOINSTAL:
			TipoInstalacion tipInst = TipoInstalacion.Factory.newInstance();
			tipInst.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setTipInst(tipInst);
			break;
		case ConstantsConceptos.CODCPTO_MATCUBIERTA:
			// Material Cubierta
			MaterialCubierta mCub = MaterialCubierta.Factory.newInstance();
			mCub.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setMatCubi(mCub);
			break;
		case ConstantsConceptos.CODCPTO_EDAD_CUBIERTA:
			EdadCubierta eCub = EdadCubierta.Factory.newInstance();
			eCub.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setEdadCubi(eCub);
			break;
		case ConstantsConceptos.CODCPTO_MATESTRUCTURA:
			MaterialEstructura mEst = MaterialEstructura.Factory.newInstance();
			mEst.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setMatEstr(mEst);
			break;
		case ConstantsConceptos.CODCPTO_EDAD_ESTRUCTURA:
			EdadEstructura eEst = EdadEstructura.Factory.newInstance();
			eEst.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setEdadEstr(eEst);
			break;
		case ConstantsConceptos.CODCPTO_COD_CERTIFICADO:
			CodigoCertificado cc = CodigoCertificado.Factory.newInstance();
			cc.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setCodCert(cc);
			break;
		case ConstantsConceptos.CODCPTO_TIPOTERRENO:
			TipoTerreno tt = TipoTerreno.Factory.newInstance();
			tt.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setTipTer(tt);
			break;
		case ConstantsConceptos.CODCPTO_TIPOMASA:
			TipoMasa tm = TipoMasa.Factory.newInstance();
			tm.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setTipMas(tm);
			break;
		case ConstantsConceptos.CODCPTO_PENDIENTE:
			Pendiente p = Pendiente.Factory.newInstance();
			p.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setPend(p);
			break;
		case ConstantsConceptos.CODCPTO_ANHOS_DESCORCHE:
			// Anhos desde descorche
			NumeroAniosDesdeDescorche nadd = NumeroAniosDesdeDescorche.Factory.newInstance();
			nadd.setValor(Integer.parseInt(valor.toString()));
			datosVariables.setNadd(nadd);
			break;
		// FIN DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO
		// Y DEL RIESGO CUBIERTO
		default:
			// No hacemos nada
			break;
		}
	}

	/**
	 * metodo para establecer los datos variables que dependen del concepto
	 * principal del modulo y del riesgo cubierto en el objeto 'DatosVariables'.
	 * 
	 * @param codconcepto
	 *            Codigo del concepto a Anadir
	 * @param codconceptoppalmod
	 *            Concepto principal del modulo del cual depende el codconcepto (si
	 *            procede). Puede ser null.
	 * @param codriesgocubierto
	 *            Riesgo cubierto del cual depende el codconcepto (si procede).
	 *            Puede ser null.
	 * @param valor
	 *            Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String
	 *            (Capital Asegurado)
	 * @param datosVariables
	 *            Estructura donde se van Anadiendo los datos variables.
	 * @throws ParseException
	 *             Error al parsear fechas.
	 */
	private static void setDatoVariableRiesgo(BigDecimal codconcepto, BigDecimal codconceptoppalmod,
			BigDecimal codriesgocubierto, Object valor, DatosVariables datosVariables)
			throws ParseException, NumberFormatException {

		// Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FECHA);

		switch (codconcepto.intValue()) {
		// INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO
		// Y DEL RIESGO CUBIERTO
		case ConstantsConceptos.CODCPTO_DIAS_INI_GARANT:
			List<DiasLimiteGarantias> lstDIG = Arrays.asList(datosVariables.getDIGarantArray());
			ArrayList<DiasLimiteGarantias> lstDIGA = new ArrayList<DiasLimiteGarantias>(lstDIG);

			DiasLimiteGarantias dLim = DiasLimiteGarantias.Factory.newInstance();
			dLim.setCodRCub(codriesgocubierto.intValue());
			dLim.setCPMod(codconceptoppalmod.intValue());
			dLim.setValor(Integer.parseInt(valor.toString()));
			lstDIGA.add(dLim);

			datosVariables.setDIGarantArray(lstDIGA.toArray(new DiasLimiteGarantias[lstDIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_DIAS_DUR_MAX_GARANT:
			List<DiasLimiteGarantias> lstDMGD = Arrays.asList(datosVariables.getDurMaxGarantDiasArray());
			ArrayList<DiasLimiteGarantias> lstDMGDA = new ArrayList<DiasLimiteGarantias>(lstDMGD);

			DiasLimiteGarantias durMaxD = DiasLimiteGarantias.Factory.newInstance();
			durMaxD.setCodRCub(codriesgocubierto.intValue());
			durMaxD.setCPMod(codconceptoppalmod.intValue());
			durMaxD.setValor(Integer.parseInt(valor.toString()));
			lstDMGDA.add(durMaxD);

			datosVariables.setDurMaxGarantDiasArray(lstDMGDA.toArray(new DiasLimiteGarantias[lstDMGDA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MES_DUR_MAX_GARANT:
			List<MesesLimiteGarantias> lstDMGM = Arrays.asList(datosVariables.getDurMaxGarantMesesArray());
			ArrayList<MesesLimiteGarantias> lstDMGMA = new ArrayList<MesesLimiteGarantias>(lstDMGM);

			MesesLimiteGarantias durMaxM = MesesLimiteGarantias.Factory.newInstance();
			durMaxM.setCodRCub(codriesgocubierto.intValue());
			durMaxM.setCPMod(codconceptoppalmod.intValue());
			durMaxM.setValor(new BigDecimal(valor.toString()));
			lstDMGMA.add(durMaxM);

			datosVariables.setDurMaxGarantMesesArray(lstDMGMA.toArray(new MesesLimiteGarantias[lstDMGMA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_EST_FENOLOGICO_F_GARANT:
			List<EstadoFenologicoLimiteGarantias> lstEFFG = Arrays.asList(datosVariables.getEFFGarantArray());
			ArrayList<EstadoFenologicoLimiteGarantias> lstEFFGA = new ArrayList<EstadoFenologicoLimiteGarantias>(
					lstEFFG);

			EstadoFenologicoLimiteGarantias estFenFin = EstadoFenologicoLimiteGarantias.Factory.newInstance();
			estFenFin.setCodRCub(codriesgocubierto.intValue());
			estFenFin.setCPMod(codconceptoppalmod.intValue());
			estFenFin.setValor(valor + VACIO);
			lstEFFGA.add(estFenFin);

			datosVariables.setEFFGarantArray(lstEFFGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFFGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_EST_FENOLOGICO_I_GARANT:
			List<EstadoFenologicoLimiteGarantias> lstEFIG = Arrays.asList(datosVariables.getEFIGarantArray());
			ArrayList<EstadoFenologicoLimiteGarantias> lstEFIGA = new ArrayList<EstadoFenologicoLimiteGarantias>(
					lstEFIG);

			EstadoFenologicoLimiteGarantias estFenIni = EstadoFenologicoLimiteGarantias.Factory.newInstance();
			estFenIni.setCodRCub(codriesgocubierto.intValue());
			estFenIni.setCPMod(codconceptoppalmod.intValue());
			estFenIni.setValor(valor + VACIO);
			lstEFIGA.add(estFenIni);

			datosVariables.setEFIGarantArray(lstEFIGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_FEC_FIN_GARANT:
			List<FechaLimiteGarantias> lstFLG = Arrays.asList(datosVariables.getFecFGarantArray());
			ArrayList<FechaLimiteGarantias> lstFLGA = new ArrayList<FechaLimiteGarantias>(lstFLG);

			FechaLimiteGarantias fLim = FechaLimiteGarantias.Factory.newInstance();
			fLim.setCodRCub(codriesgocubierto.intValue());
			fLim.setCPMod(codconceptoppalmod.intValue());
			GregorianCalendar gcFLG = new GregorianCalendar();
			gcFLG.setTime(sdf.parse(valor.toString()));
			fLim.setValor(gcFLG);
			lstFLGA.add(fLim);

			datosVariables.setFecFGarantArray(lstFLGA.toArray(new FechaLimiteGarantias[lstFLGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_FEC_INI_GARANT:
			List<FechaLimiteGarantias> lstFIG = Arrays.asList(datosVariables.getFecIGarantArray());
			ArrayList<FechaLimiteGarantias> lstFIGA = new ArrayList<FechaLimiteGarantias>(lstFIG);

			FechaLimiteGarantias fIni = FechaLimiteGarantias.Factory.newInstance();
			fIni.setCodRCub(codriesgocubierto.intValue());
			fIni.setCPMod(codconceptoppalmod.intValue());
			GregorianCalendar gcFIG = new GregorianCalendar();
			gcFIG.setTime(sdf.parse(valor.toString()));
			fIni.setValor(gcFIG);
			lstFIGA.add(fIni);

			datosVariables.setFecIGarantArray(lstFIGA.toArray(new FechaLimiteGarantias[lstFIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MES_INI_GARANT:
			List<MesesLimiteGarantias> lstMIG = Arrays.asList(datosVariables.getMIGarantArray());
			ArrayList<MesesLimiteGarantias> lstMIGA = new ArrayList<MesesLimiteGarantias>(lstMIG);

			MesesLimiteGarantias migarant = MesesLimiteGarantias.Factory.newInstance();
			migarant.setCodRCub(codriesgocubierto.intValue());
			migarant.setCPMod(codconceptoppalmod.intValue());
			migarant.setValor(new BigDecimal(valor.toString()));
			lstMIGA.add(migarant);

			datosVariables.setMIGarantArray(lstMIGA.toArray(new MesesLimiteGarantias[lstMIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_PERIODO_GARANT:
			List<PeriodoGarantias> lstPG = Arrays.asList(datosVariables.getPerGarantArray());
			ArrayList<PeriodoGarantias> lstPGA = new ArrayList<PeriodoGarantias>(lstPG);

			PeriodoGarantias perGarant = PeriodoGarantias.Factory.newInstance();
			perGarant.setCodRCub(codriesgocubierto.intValue());
			perGarant.setCPMod(codconceptoppalmod.intValue());
			perGarant.setValor(Integer.parseInt(valor.toString()));
			lstPGA.add(perGarant);

			datosVariables.setPerGarantArray(lstPGA.toArray(new PeriodoGarantias[lstPGA.size()]));
			break;
		/* Pet. 57626 ** MODIF TAM (27.05.2020) ** Inicio */
		case ConstantsConceptos.CODCPTO_PCT_FRANQUICIA:
			List<PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
			ArrayList<PorcentajeFranquicia> lstFA = new ArrayList<PorcentajeFranquicia>(lstF);

			PorcentajeFranquicia f = PorcentajeFranquicia.Factory.newInstance();
			f.setCodRCub(codriesgocubierto.intValue());
			f.setCPMod(codconceptoppalmod.intValue());
			f.setValor(Integer.parseInt(valor.toString()));

			lstFA.add(f);

			datosVariables.setFranqArray(lstFA.toArray(new PorcentajeFranquicia[lstFA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE:
			// % MINIMO INDEMNIZABLE
			List<PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
			ArrayList<PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);

			PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
			m.setCodRCub(codriesgocubierto.intValue());
			m.setCPMod(codconceptoppalmod.intValue());
			m.setValor(Integer.parseInt(valor.toString()));

			lstMIA.add(m);

			datosVariables.setMinIndemArray(lstMIA.toArray(new PorcentajeMinimoIndemnizable[lstMIA.size()]));
			break;
		default:
			// No hacemos nada
			break;
		}
	}

	/***
	 * Pet. 57626 ** MODIF TAM (21/07/2020) ** Resolucion Incidencias ** Inicio
	 ***/
	/**
	 * Metodo para establecer los datos variables que dependen del concepto
	 * principal del modulo y del riesgo cubierto en el objeto 'DatosVariables'.
	 * 
	 * @param codconcepto
	 *            Codigo del concepto a Anadir
	 * @param codconceptoppalmod
	 *            Concepto principal del modulo del cual depende el codconcepto (si
	 *            procede). Puede ser null.
	 * @param codriesgocubierto
	 *            Riesgo cubierto del cual depende el codconcepto (si procede).
	 *            Puede ser null.
	 * @param valor
	 *            Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String
	 *            (Capital Asegurado)
	 * @param datosVariables
	 *            Estructura donde se van Anadiendo los datos variables.
	 * @throws ParseException
	 *             Error al parsear fechas.
	 */
	private static void setDatoVariableRiesgoCpl(BigDecimal codconcepto, BigDecimal codconceptoppalmod,
			BigDecimal codriesgocubierto, Object valor, DatosVariables datosVariables)
			throws ParseException, NumberFormatException {

		// Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FECHA);

		// Comienzo del tratamiento del dato variable: 157OK, 140OK, 137OK, 136OK,
		// 134OK, 138OK, 135OK, 139OK

		switch (codconcepto.intValue()) {
		// INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL
		// RIESGO CUBIERTO
		case ConstantsConceptos.CODCPTO_DIAS_INI_GARANT:
			// DIAS INICIO GARANTIAS
			List<DiasLimiteGarantias> lstDIG = Arrays.asList(datosVariables.getDIGarantArray());
			ArrayList<DiasLimiteGarantias> lstDIGA = new ArrayList<DiasLimiteGarantias>(lstDIG);

			DiasLimiteGarantias dLim = DiasLimiteGarantias.Factory.newInstance();
			dLim.setCodRCub(codriesgocubierto.intValue());
			dLim.setCPMod(codconceptoppalmod.intValue());
			dLim.setValor(Integer.parseInt(valor.toString()));
			lstDIGA.add(dLim);

			datosVariables.setDIGarantArray(lstDIGA.toArray(new DiasLimiteGarantias[lstDIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_DIAS_DUR_MAX_GARANT:
			// DURACION MAX.GARANT(DIAS)
			List<DiasLimiteGarantias> lstDMGD = Arrays.asList(datosVariables.getDurMaxGarantDiasArray());
			ArrayList<DiasLimiteGarantias> lstDMGDA = new ArrayList<DiasLimiteGarantias>(lstDMGD);

			DiasLimiteGarantias durMaxD = DiasLimiteGarantias.Factory.newInstance();
			durMaxD.setCodRCub(codriesgocubierto.intValue());
			durMaxD.setCPMod(codconceptoppalmod.intValue());
			durMaxD.setValor(Integer.parseInt(valor.toString()));
			lstDMGDA.add(durMaxD);

			datosVariables.setDurMaxGarantDiasArray(lstDMGDA.toArray(new DiasLimiteGarantias[lstDMGDA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MES_DUR_MAX_GARANT:
			// DURACION MAX.GARAN(MESES)
			List<MesesLimiteGarantias> lstDMGM = Arrays.asList(datosVariables.getDurMaxGarantMesesArray());
			ArrayList<MesesLimiteGarantias> lstDMGMA = new ArrayList<MesesLimiteGarantias>(lstDMGM);

			MesesLimiteGarantias durMaxM = MesesLimiteGarantias.Factory.newInstance();
			durMaxM.setCodRCub(codriesgocubierto.intValue());
			durMaxM.setCPMod(codconceptoppalmod.intValue());
			durMaxM.setValor(new BigDecimal(valor.toString()));
			lstDMGMA.add(durMaxM);

			datosVariables.setDurMaxGarantMesesArray(lstDMGMA.toArray(new MesesLimiteGarantias[lstDMGMA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_EST_FENOLOGICO_F_GARANT:
			// EST.FEN.FIN GARANTIAS
			List<EstadoFenologicoLimiteGarantias> lstEFFG = Arrays.asList(datosVariables.getEFFGarantArray());
			ArrayList<EstadoFenologicoLimiteGarantias> lstEFFGA = new ArrayList<EstadoFenologicoLimiteGarantias>(
					lstEFFG);

			EstadoFenologicoLimiteGarantias estFenFin = EstadoFenologicoLimiteGarantias.Factory.newInstance();
			estFenFin.setCodRCub(codriesgocubierto.intValue());
			estFenFin.setCPMod(codconceptoppalmod.intValue());
			estFenFin.setValor(valor + "");
			lstEFFGA.add(estFenFin);

			datosVariables.setEFFGarantArray(lstEFFGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFFGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_EST_FENOLOGICO_I_GARANT:
			// EST.FEN.INICIO GARANTIAS
			List<EstadoFenologicoLimiteGarantias> lstEFIG = Arrays.asList(datosVariables.getEFIGarantArray());
			ArrayList<EstadoFenologicoLimiteGarantias> lstEFIGA = new ArrayList<EstadoFenologicoLimiteGarantias>(
					lstEFIG);

			EstadoFenologicoLimiteGarantias estFenIni = EstadoFenologicoLimiteGarantias.Factory.newInstance();
			estFenIni.setCodRCub(codriesgocubierto.intValue());
			estFenIni.setCPMod(codconceptoppalmod.intValue());
			estFenIni.setValor(valor + "");
			lstEFIGA.add(estFenIni);

			datosVariables.setEFIGarantArray(lstEFIGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_FEC_FIN_GARANT:
			// FECHA FIN GARANTIAS
			List<FechaLimiteGarantias> lstFLG = Arrays.asList(datosVariables.getFecFGarantArray());
			ArrayList<FechaLimiteGarantias> lstFLGA = new ArrayList<FechaLimiteGarantias>(lstFLG);

			FechaLimiteGarantias fLim = FechaLimiteGarantias.Factory.newInstance();
			if (codriesgocubierto != null)
				fLim.setCodRCub(codriesgocubierto.intValue());
			if (codconceptoppalmod != null)
				fLim.setCPMod(codconceptoppalmod.intValue());
			GregorianCalendar gcFLG = new GregorianCalendar();
			gcFLG.setTime(sdf.parse(valor.toString()));
			fLim.setValor(gcFLG);
			lstFLGA.add(fLim);

			datosVariables.setFecFGarantArray(lstFLGA.toArray(new FechaLimiteGarantias[lstFLGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_FEC_INI_GARANT:
			// FECHA INICIO GARANTIAS
			List<FechaLimiteGarantias> lstFIG = Arrays.asList(datosVariables.getFecIGarantArray());
			ArrayList<FechaLimiteGarantias> lstFIGA = new ArrayList<FechaLimiteGarantias>(lstFIG);

			FechaLimiteGarantias fIni = FechaLimiteGarantias.Factory.newInstance();
			fIni.setCodRCub(codriesgocubierto.intValue());
			fIni.setCPMod(codconceptoppalmod.intValue());
			GregorianCalendar gcFIG = new GregorianCalendar();
			gcFIG.setTime(sdf.parse(valor.toString()));
			fIni.setValor(gcFIG);
			lstFIGA.add(fIni);

			datosVariables.setFecIGarantArray(lstFIGA.toArray(new FechaLimiteGarantias[lstFIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MES_INI_GARANT:
			// MESES INICIO GARANTIAS
			List<MesesLimiteGarantias> lstMIG = Arrays.asList(datosVariables.getMIGarantArray());
			ArrayList<MesesLimiteGarantias> lstMIGA = new ArrayList<MesesLimiteGarantias>(lstMIG);

			MesesLimiteGarantias migarant = MesesLimiteGarantias.Factory.newInstance();
			migarant.setCodRCub(codriesgocubierto.intValue());
			migarant.setCPMod(codconceptoppalmod.intValue());
			migarant.setValor(new BigDecimal(valor.toString()));
			lstMIGA.add(migarant);

			datosVariables.setMIGarantArray(lstMIGA.toArray(new MesesLimiteGarantias[lstMIGA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_PERIODO_GARANT:
			// PERIODO GARANTIAS
			List<PeriodoGarantias> lstPG = Arrays.asList(datosVariables.getPerGarantArray());
			ArrayList<PeriodoGarantias> lstPGA = new ArrayList<PeriodoGarantias>(lstPG);

			PeriodoGarantias perGarant = PeriodoGarantias.Factory.newInstance();
			perGarant.setCodRCub(codriesgocubierto.intValue());
			perGarant.setCPMod(codconceptoppalmod.intValue());
			perGarant.setValor(Integer.parseInt(valor.toString()));
			lstPGA.add(perGarant);

			datosVariables.setPerGarantArray(lstPGA.toArray(new PeriodoGarantias[lstPGA.size()]));
			break;
		/* Pet. 57626 ** MODIF TAM (27.05.2020) ** Inicio */
		case ConstantsConceptos.CODCPTO_PCT_FRANQUICIA:
			List<PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
			ArrayList<PorcentajeFranquicia> lstFA = new ArrayList<PorcentajeFranquicia>(lstF);

			PorcentajeFranquicia f = PorcentajeFranquicia.Factory.newInstance();
			f.setCodRCub(codriesgocubierto.intValue());
			f.setCPMod(codconceptoppalmod.intValue());
			f.setValor(Integer.parseInt(valor.toString()));

			lstFA.add(f);

			datosVariables.setFranqArray(lstFA.toArray(new PorcentajeFranquicia[lstFA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE:
			// % MINIMO INDEMNIZABLE
			List<PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
			ArrayList<PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);

			PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
			m.setCodRCub(codriesgocubierto.intValue());
			m.setCPMod(codconceptoppalmod.intValue());
			m.setValor(Integer.parseInt(valor.toString()));

			lstMIA.add(m);

			datosVariables.setMinIndemArray(lstMIA.toArray(new PorcentajeMinimoIndemnizable[lstMIA.size()]));
			break;
		case ConstantsConceptos.CODCPTO_GARANTIZADO:
			// % GARANTIZADO
			List<Garantizado> lstGar = Arrays.asList(datosVariables.getGarantArray());
			ArrayList<Garantizado> lstGarA = new ArrayList<Garantizado>(lstGar);
			
			Garantizado g = Garantizado.Factory.newInstance();
			g.setCodRCub(codriesgocubierto.intValue());
			g.setCPMod(codconceptoppalmod.intValue());
			g.setValor(Integer.parseInt(valor.toString()));
			
			lstGarA.add(g);
			
			datosVariables.setGarantArray(lstGar.toArray(new Garantizado[lstGarA.size()]));
			break;
		default:
			// No hacemos nada
			break;
		}
	}

	/***
	 * Pet. 57626 ** MODIF TAM (21/07/2020) ** Resolucion Incidencias ** Fin
	 ***/

	/**
	 * Devuelve el objeto Cuenta correspondiente a la poliza indicada como parametro
	 * 
	 * @param poliza
	 * @return
	 */
	private static Cuenta obtenerCuenta(com.rsi.agp.dao.tables.poliza.Poliza poliza, boolean financiada) {
		Cuenta cuenta = Cuenta.Factory.newInstance();
		try {
			cuenta.setIban(AseguradoUtil.obtenerCcc(poliza, true));
			if (financiada)
				cuenta.setDestinatario("A");
		} catch (Exception e) {
			logger.error("Ocurrio un error al obtener el objeto Cuenta de la poliza", e);
		}
		return cuenta;
	}

	/**
	 * Devuelve el objeto Cuenta correspondiente a la poliza indicada como parametro
	 * 
	 * @param poliza
	 * @return
	 */
	private static Cuenta obtenerDatCuentaAseg(com.rsi.agp.dao.tables.poliza.Poliza poliza) {
		Cuenta cuenta = Cuenta.Factory.newInstance();

		try {
			logger.debug("PolizaUnificadaTransformer - obtenerDatCuentaAseg");

			DatoAsegurado datoAsegurado = AseguradoUtil.obtenerDatoAsegurado(poliza);
			String ibanAux = datoAsegurado.getIban() + datoAsegurado.getCcc();

			logger.debug("Valor de ibanAux: " + ibanAux);
			cuenta.setIban(ibanAux);
			cuenta.setDestinatario(datoAsegurado.getDestinatarioDomiciliacion());

			if (datoAsegurado.getDestinatarioDomiciliacion().equals("O"))
				cuenta.setTitular(datoAsegurado.getTitularCuenta());

		} catch (Exception e) {
			logger.error("Ocurrio un error al obtener el objeto Cuenta de la poliza", e);
		}
		return cuenta;
	}

	/**
	 * Carga en el objeto CosteGrupoNegocio las subvenciones Enesa y CCAA
	 * almacenadas en la DC2015
	 * 
	 * @param dc
	 * @param costeGrupo
	 */
	private static void cargarSubvencionesDC2015(DistribucionCoste2015 dc, CosteGrupoNegocio costeGrupo) {
		// Se crea el mapa para agrupar el importe de las subvenciones
		// por codigo de organismo
		Map<Character, BigDecimal> mapaCCAA = new HashMap<Character, BigDecimal>();

		for (DistCosteSubvencion2015 dcs : dc.getDistCosteSubvencion2015s()) {

			if (dcs.getCodorganismo().equals(new Character('0'))) {
				es.agroseguro.contratacion.costePoliza.SubvencionEnesa se = es.agroseguro.contratacion.costePoliza.SubvencionEnesa.Factory
						.newInstance();
				se.setTipo(dcs.getCodtiposubv().intValue());
				if (dcs.getImportesubv() != null)
					se.setImporte(dcs.getImportesubv());
				else
					se.setImporte(new BigDecimal(0));

				List<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstSE = Arrays
						.asList(costeGrupo.getSubvencionEnesaArray());
				ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstSEA = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa>(
						lstSE);
				lstSEA.add(se);

				costeGrupo.setSubvencionEnesaArray((es.agroseguro.contratacion.costePoliza.SubvencionEnesa[]) lstSEA
						.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionEnesa[lstSEA.size()]));
			} else {
				// Comprueba si ya existe subvencion para el organismo
				// en el mapa
				if (mapaCCAA.containsKey(dcs.getCodorganismo())) {
					// Se suma el importe de la subvencion actual al
					// guardado para este organismo
					mapaCCAA.put(dcs.getCodorganismo(), mapaCCAA.get(dcs.getCodorganismo())
							.add((dcs.getImportesubv() != null ? dcs.getImportesubv() : new BigDecimal(0))));
				} else {
					// Organismo nuevo, se insertan los datos en el mapa
					// Si el importe de la subvencion es nulo se inserta
					// un 0
					mapaCCAA.put(dcs.getCodorganismo(),
							(dcs.getImportesubv() != null ? dcs.getImportesubv() : new BigDecimal(0)));
				}

			}
		}

		// Se Anade el listado de subvenciones CCAA a la distribucion de
		// costes
		// Recorre las claves del mapa, crea el objeto SubvencionCCAA
		// correpondiente y lo Anade al listado
		List<es.agroseguro.contratacion.costePoliza.SubvencionCCAA> listSubvAux = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionCCAA>();
		for (Character codOrg : mapaCCAA.keySet()) {
			es.agroseguro.contratacion.costePoliza.SubvencionCCAA s = es.agroseguro.contratacion.costePoliza.SubvencionCCAA.Factory
					.newInstance();
			s.setCodigoOrganismo(codOrg.toString());
			s.setImporte(mapaCCAA.get(codOrg));

			listSubvAux.add(s);
		}

		// Convierte el listado en un array y lo Anade al objeto de
		// distribucion de costes
		costeGrupo.setSubvencionCCAAArray(
				listSubvAux.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionCCAA[listSubvAux.size()]));
	}

	private static es.agroseguro.iTipos.Gastos[] getGastos(final List<GruposNegocio> gruposNegocio,
			final Set<PolizaPctComisiones> listaPctComisiones, final Boolean esGanado,
			final com.rsi.agp.dao.tables.poliza.Poliza poliza, final boolean aplicaDtoRec,
			final Map<Character, ComsPctCalculado> comsPctCalculado) {
		BigDecimal comisionRecalculo;
		if (esGanado) {
			es.agroseguro.iTipos.Gastos gastos[] = new es.agroseguro.iTipos.Gastos[gruposNegocio.size()];
			/* GASTOS PARA GANADO */
			for (int i = 0; i < gruposNegocio.size(); i++) {
				Character idGrupo = gruposNegocio.get(i).getGrupoNegocio();
				Gastos gas = Gastos.Factory.newInstance();
				PolizaPctComisiones pctCom = getPctComisionesPorGrupoNegocio(listaPctComisiones, idGrupo);
				comisionRecalculo = comsPctCalculado != null && comsPctCalculado.containsKey(idGrupo)
						? comsPctCalculado.get(idGrupo).getPctCalculado()
						: null;
				if (pctCom != null) {
					if (pctCom.getPctadministracion() != null) {
						gas.setAdministracion(pctCom.getPctadministracion());
					} else {
						gas.setAdministracion(BigDecimal.ZERO);
					}
					if (pctCom.getPctadquisicion() != null) {
						gas.setAdquisicion(pctCom.getPctadquisicion());
					} else {
						gas.setAdquisicion(BigDecimal.ZERO);
					}
					BigDecimal comMediador = BigDecimal.ZERO;
					comMediador = obtenerComisionMediador(pctCom, aplicaDtoRec, comisionRecalculo);
					if (comMediador != null) {
						gas.setComisionMediador(comMediador);
					} else {
						gas.setComisionMediador(BigDecimal.ZERO);
					}					
				} else {
					gas.setAdministracion(BigDecimal.ZERO);
					gas.setAdquisicion(BigDecimal.ZERO);
					gas.setComisionMediador(comisionRecalculo == null ? BigDecimal.ZERO : comisionRecalculo);
				}
				// Grupo de negocio - 1 (vida) o 2 (muerte)
				gas.setGrupoNegocio(gruposNegocio.get(i).getGrupoNegocio().toString());
				gastos[i] = gas;
			}
			return gastos;
		} else {
			es.agroseguro.iTipos.Gastos gastos[] = new es.agroseguro.iTipos.Gastos[1];
			// LAS AGRICOLAS UNICAMENTE TIENEN UN GRUPO
			comisionRecalculo = comsPctCalculado != null && comsPctCalculado.containsKey('1')
					? comsPctCalculado.get('1').getPctCalculado()
					: null;
			/* GASTOS PARA AGRICOLAS */
			if (poliza.isPlanMayorIgual2015()) {
				int j = 0;
				Gastos gas = Gastos.Factory.newInstance();
				PolizaPctComisiones pctCom = poliza.getPolizaPctComisiones();
				if (pctCom != null) {
					if (pctCom.getPctadministracion() != null) {
						gas.setAdministracion(pctCom.getPctadministracion());
					} else {
						gas.setAdministracion(BigDecimal.ZERO);
					}
					if (pctCom.getPctadquisicion() != null) {
						gas.setAdquisicion(pctCom.getPctadquisicion());
					} else {
						gas.setAdquisicion(BigDecimal.ZERO);
					}
					BigDecimal comMediador = BigDecimal.ZERO;
					comMediador = obtenerComisionMediador(pctCom, aplicaDtoRec, comisionRecalculo);
					if (comMediador != null) {
						gas.setComisionMediador(comMediador);
					} else {
						gas.setComisionMediador(BigDecimal.ZERO);
					}
				} else {
					gas.setAdministracion(BigDecimal.ZERO);
					gas.setAdquisicion(BigDecimal.ZERO);
					gas.setComisionMediador(comisionRecalculo == null ? BigDecimal.ZERO : comisionRecalculo);
				}
				gastos[j] = gas;
			}
			return gastos;
		}
		// FIN GASTOS
	}

	private static PolizaPctComisiones getPctComisionesPorGrupoNegocio(
			final Set<PolizaPctComisiones> listaPctComisiones, Character codGrupoNeg) {
		PolizaPctComisiones res = null;
		for (PolizaPctComisiones pctCom : listaPctComisiones) {
			if (codGrupoNeg.compareTo(pctCom.getGrupoNegocio()) == 0) {
				res = pctCom;
				break;
			}
		}
		return res;
	}

	private static es.agroseguro.contratacion.costePoliza.CostePoliza getCoste(
			com.rsi.agp.dao.tables.poliza.Poliza poliza, List<GruposNegocio> gruposNegocio, Long idComparativa,
			ComparativaPoliza cp) {
		CostePoliza coste = CostePoliza.Factory.newInstance();
		Set<DistribucionCoste2015> distCostesSet = poliza.getDistribucionCoste2015s();

		List<CosteGrupoNegocio> costeGrupoCol = new ArrayList<CosteGrupoNegocio>(distCostesSet.size());
		BigDecimal totalCosteTomador = BigDecimal.valueOf(0);

		boolean moduloCorrecto = false;

		/* Pet. 57626 ** MODIF TAM (13.05.2020 ** Inicio */
		/*
		 * Se recuperan los Costes para ganado como se estaba haciendo hasta ahora y se
		 * incluye el nuevo tratamiento para recuperar los costes para agricolas
		 */
		BigDecimal recargoAval = BigDecimal.valueOf(0);
		BigDecimal recargoFraccionamiento = BigDecimal.valueOf(0);

		if (poliza.getLinea().isLineaGanado()) {
			/************************/
			/** COSTES PARA GANADO **/
			/************************/
			for (GruposNegocio gn : gruposNegocio) {
				CosteGrupoNegocio costeGrupo;
				BonificacionRecargo bonifRecargo;
				Set<BonificacionRecargo2015> bonifRecargoSet;
				List<BonificacionRecargo> bonifRecargoCol;

				if (distCostesSet.isEmpty()) {
					costeGrupo = getCosteGrupoNegocioDefault(gn.getGrupoNegocio().toString());
					costeGrupoCol.add(costeGrupo);
				} else {
					Set<DistribucionCoste2015> distCosteComparativa = idComparativa != null
							? getDistribucionCostes(new BigDecimal(idComparativa), distCostesSet)
							: null;
					if (null == distCosteComparativa || distCosteComparativa.size() == 0) {
						costeGrupo = getCosteGrupoNegocioDefault(gn.getGrupoNegocio().toString());
						costeGrupoCol.add(costeGrupo);
					} else {
						for (DistribucionCoste2015 distCoste : distCosteComparativa) {
							if (distCoste.getGrupoNegocio().equals(gn.getGrupoNegocio())
									&& distCoste.getIdcomparativa().equals(new BigDecimal(idComparativa))) {
								costeGrupo = CosteGrupoNegocio.Factory.newInstance();
								costeGrupo.setCosteTomador(distCoste.getCostetomador());

								if (poliza.getEsFinanciada().equals('S'))
									totalCosteTomador = distCoste.getTotalcostetomador();
								else
									totalCosteTomador = totalCosteTomador.add(distCoste.getCostetomador());

								costeGrupo.setPrimaComercial(distCoste.getPrimacomercial());
								costeGrupo.setPrimaComercialNeta(distCoste.getPrimacomercialneta());
								if (distCoste.getRecargoconsorcio() != null) {
									costeGrupo.setRecargoConsorcio(distCoste.getRecargoconsorcio());
								}
								costeGrupo.setReciboPrima(distCoste.getReciboprima());

								bonifRecargoSet = distCoste.getBonificacionRecargo2015s();
								bonifRecargoCol = new ArrayList<BonificacionRecargo>(bonifRecargoSet.size());
								for (BonificacionRecargo2015 bonifRecargo2015 : bonifRecargoSet) {
									bonifRecargo = BonificacionRecargo.Factory.newInstance();
									bonifRecargo.setCodigo(bonifRecargo2015.getCodigo().intValue());
									bonifRecargo.setImporte(bonifRecargo2015.getImporte());
									bonifRecargoCol.add(bonifRecargo);
								}
								costeGrupo.setBonificacionRecargoArray(
										bonifRecargoCol.toArray(new BonificacionRecargo[] {}));

								costeGrupo.setGrupoNegocio(gn.getGrupoNegocio().toString());

								// DISTRIBUCION DE COSTE DE LAS SUBVENCIONES
								cargarSubvencionesDC2015(distCoste, costeGrupo);
								costeGrupoCol.add(costeGrupo);
							}
						}
					}

				}
			}
			coste.setCosteGrupoNegocioArray(costeGrupoCol.toArray(new CosteGrupoNegocio[] {}));
		} else {
			/***************************/
			/** COSTES PARA AGRICOLAS **/
			/***************************/
			CosteGrupoNegocio costeGrupo = CosteGrupoNegocio.Factory.newInstance();

			BonificacionRecargo bonifRecargo;
			Set<BonificacionRecargo2015> bonifRecargoSet;
			List<BonificacionRecargo> bonifRecargoCol;

			for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
				// si el mio de la comparativa es diferente al que viene en la distrib. de
				// costes se pone una distrib. por defecto.
				if (cp.getId().getCodmodulo().equals(dc.getCodmodulo())
						&& dc.getIdcomparativa().equals(BigDecimal.valueOf(cp.getId().getIdComparativa()))) {
					moduloCorrecto = true;
					costeGrupo.setPrimaComercial(dc.getPrimacomercial());
					costeGrupo.setPrimaComercialNeta(dc.getPrimacomercialneta());
					costeGrupo.setCosteTomador(dc.getCostetomador());
					costeGrupo.setReciboPrima(dc.getReciboprima());
					costeGrupo.setRecargoConsorcio(dc.getRecargoconsorcio());
					costeGrupo.setGrupoNegocio(dc.getGrupoNegocio().toString());

					if (dc.getPeriodoFracc() != null) {
						totalCosteTomador = dc.getTotalcostetomador();
					} else {
						totalCosteTomador = dc.getCostetomador();

					}
					// Lo comentamos por ahora.
					if (dc.getRecargoaval() != null)
						recargoAval = dc.getRecargoaval();
					if (dc.getRecargofraccionamiento() != null)
						recargoFraccionamiento = dc.getRecargofraccionamiento();

					if (dc.getBonificacionRecargo2015s() != null && dc.getBonificacionRecargo2015s().size() > 0) {

						/* 57626 */
						/*
						 * Comentamos esta parte por que la hemos implementado abajo
						 * /*es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo[]
						 * arrBonRec = new
						 * es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo[dc
						 * .getBonificacionRecargo2015s().size()]; int i = 0; for
						 * (BonificacionRecargo2015 dcs : dc.getBonificacionRecargo2015s()) {
						 * es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo bonRec =
						 * es.agroseguro.seguroAgrario.distribucionCoste.BonificacionRecargo.Factory
						 * .newInstance(); bonRec.setCodigo(dcs.getCodigo().intValue());
						 * bonRec.setImporte(dcs.getImporte()); arrBonRec[i] = bonRec; i++; }
						 * 
						 * costeGrupo.setBonificacionRecargoArray(arrBonRec); /* 57626
						 */

						bonifRecargoSet = dc.getBonificacionRecargo2015s();
						bonifRecargoCol = new ArrayList<BonificacionRecargo>(bonifRecargoSet.size());
						for (BonificacionRecargo2015 bonifRecargo2015 : bonifRecargoSet) {
							bonifRecargo = BonificacionRecargo.Factory.newInstance();
							bonifRecargo.setCodigo(bonifRecargo2015.getCodigo().intValue());
							bonifRecargo.setImporte(bonifRecargo2015.getImporte());
							bonifRecargoCol.add(bonifRecargo);
						}
						costeGrupo.setBonificacionRecargoArray(bonifRecargoCol.toArray(new BonificacionRecargo[] {}));

					}

					// Se crea el mapa para agrupar el importe de las subvenciones
					// por codigo de organismo
					Map<Character, BigDecimal> mapaCCAA = new HashMap<Character, BigDecimal>();

					for (DistCosteSubvencion2015 dcs : dc.getDistCosteSubvencion2015s()) {
						if (dcs.getCodorganismo().equals(new Character('0'))) {
							es.agroseguro.contratacion.costePoliza.SubvencionEnesa se = es.agroseguro.contratacion.costePoliza.SubvencionEnesa.Factory
									.newInstance();
							se.setTipo(dcs.getCodtiposubv().intValue());
							if (dcs.getImportesubv() != null)
								se.setImporte(dcs.getImportesubv());
							else
								se.setImporte(new BigDecimal(0));

							List<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstSE = Arrays
									.asList(costeGrupo.getSubvencionEnesaArray());
							ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstSEA = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa>(
									lstSE);
							lstSEA.add(se);

							costeGrupo.setSubvencionEnesaArray(
									(es.agroseguro.contratacion.costePoliza.SubvencionEnesa[]) lstSEA.toArray(
											new es.agroseguro.contratacion.costePoliza.SubvencionEnesa[lstSEA.size()]));
						} else {
							// Comprueba si ya existe subvencion para el organismo
							// en el mapa
							if (mapaCCAA.containsKey(dcs.getCodorganismo())) {
								// Se suma el importe de la subvencion actual al
								// guardado para este organismo
								mapaCCAA.put(dcs.getCodorganismo(), mapaCCAA.get(dcs.getCodorganismo()).add(
										(dcs.getImportesubv() != null ? dcs.getImportesubv() : new BigDecimal(0))));
							} else {
								// Organismo nuevo, se insertan los datos en el mapa
								// Si el importe de la subvencion es nulo se inserta
								// un 0
								mapaCCAA.put(dcs.getCodorganismo(),
										(dcs.getImportesubv() != null ? dcs.getImportesubv() : new BigDecimal(0)));
							}

						}
					}

					// Se Anade el listado de subvenciones CCAA a la distribucion de
					// costes
					// Recorre las claves del mapa, crea el objeto SubvencionCCAA
					// correpondiente y lo Anade al listado
					List<es.agroseguro.contratacion.costePoliza.SubvencionCCAA> listSubvAux = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionCCAA>();
					for (Character codOrg : mapaCCAA.keySet()) {
						es.agroseguro.contratacion.costePoliza.SubvencionCCAA s = es.agroseguro.contratacion.costePoliza.SubvencionCCAA.Factory
								.newInstance();
						s.setCodigoOrganismo(codOrg.toString());
						s.setImporte(mapaCCAA.get(codOrg));

						listSubvAux.add(s);
					}

					// Convierte el listado en un array y lo Anade al objeto de
					// distribucion de costes
					costeGrupo.setSubvencionCCAAArray(listSubvAux
							.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionCCAA[listSubvAux.size()]));

					// Paramos porque solo deberia haber un registro en la base de
					// datos
					break;
				}
			} // fin bucle for distrb2015

			// En caso de que la poliza no tenga distribucion de costes, hay que
			// poner una por defecto
			if (!moduloCorrecto
					|| (poliza.getDistribucionCoste2015s() == null || poliza.getDistribucionCoste2015s().size() == 0)) {
				costeGrupo = cargaDistribucionCoste1Default_old();

				costeGrupoCol.add(costeGrupo);
				coste.setCosteGrupoNegocioArray(costeGrupoCol.toArray(new CosteGrupoNegocio[] {}));

			} else {

				costeGrupoCol.add(costeGrupo);
				coste.setCosteGrupoNegocioArray(costeGrupoCol.toArray(new CosteGrupoNegocio[] {}));

				es.agroseguro.contratacion.costePoliza.Financiacion fin_agr = es.agroseguro.contratacion.costePoliza.Financiacion.Factory
						.newInstance();

				fin_agr.setRecargoAval(recargoAval);
				fin_agr.setRecargoFraccionamiento(recargoFraccionamiento);
				BigDecimal nullBigDecimal = new BigDecimal(0);

				if (fin_agr != null && fin_agr.getRecargoAval() != null && fin_agr.getRecargoAval() != nullBigDecimal
						&& fin_agr.getRecargoFraccionamiento() != null
						&& fin_agr.getRecargoFraccionamiento() != nullBigDecimal) {
					coste.setFinanciacion(fin_agr);
				}
			}
		}

		// Financiacion
		if (poliza.getEsFinanciada().equals('S')) {
			es.agroseguro.contratacion.costePoliza.Financiacion financiacion = es.agroseguro.contratacion.costePoliza.Financiacion.Factory
					.newInstance();
			getFinanciacion(financiacion, distCostesSet);
			if (financiacion != null && financiacion.getRecargoAval() != null
					&& financiacion.getRecargoFraccionamiento() != null)
				coste.setFinanciacion(financiacion);
		}

		coste.setTotalCosteTomador(totalCosteTomador);
		return coste;
	}

	private static CosteGrupoNegocio getCosteGrupoNegocioDefault(String grupoNegocio) {
		CosteGrupoNegocio costeGrupo = null;
		costeGrupo = CosteGrupoNegocio.Factory.newInstance();
		costeGrupo.setCosteTomador(BigDecimal.valueOf(0));
		costeGrupo.setPrimaComercial(BigDecimal.valueOf(0));
		costeGrupo.setPrimaComercialNeta(BigDecimal.valueOf(0));
		costeGrupo.setRecargoConsorcio(BigDecimal.valueOf(0));
		costeGrupo.setReciboPrima(BigDecimal.valueOf(0));
		costeGrupo.setGrupoNegocio(grupoNegocio);
		return costeGrupo;
	}

	/*
	 * Selecciona las distribuciones de coste de una comparativa determinada
	 */
	private static Set<DistribucionCoste2015> getDistribucionCostes(BigDecimal idComparativa,
			Set<DistribucionCoste2015> distCostes) {
		Set<DistribucionCoste2015> distribucionCoste2015s = new HashSet<DistribucionCoste2015>();
		for (DistribucionCoste2015 distCoste : distCostes) {
			if (distCoste.getIdcomparativa().compareTo(idComparativa) == 0) {
				distribucionCoste2015s.add(distCoste);
			}
		}
		return distribucionCoste2015s;
	}

	private static es.agroseguro.contratacion.costePoliza.Financiacion getFinanciacion(
			es.agroseguro.contratacion.costePoliza.Financiacion financiacion,
			Set<DistribucionCoste2015> distCostesSet) {
		for (DistribucionCoste2015 distCoste : distCostesSet) {
			if (distCoste.getRecargoaval() != null)
				financiacion.setRecargoAval(distCoste.getRecargoaval());
			if (distCoste.getRecargofraccionamiento() != null)
				financiacion.setRecargoFraccionamiento(distCoste.getRecargofraccionamiento());
		}
		return financiacion;
	}

	private static es.agroseguro.contratacion.Entidad getEntidad(final com.rsi.agp.dao.tables.poliza.Poliza poliza) {

		es.agroseguro.contratacion.Entidad entidad = Entidad.Factory.newInstance();
		entidad.setCodigo(Constants.ENTIDAD_C616);
		entidad.setCodigoInterno(PolizaTransformer.getCodigoInterno(poliza));

		if (poliza.getColectivo().getSubentidadMediadora() != null) {
			Mediador mediador = Mediador.Factory.newInstance();
			// DAA 18/12/2013 TIPO MEDIADOR
			SubentidadMediadora subentidad = poliza.getColectivo().getSubentidadMediadora();
			try {
				if (subentidad != null && subentidad.getTipoMediadorAgro() != null
						&& subentidad.getTipoMediadorAgro().getId() != null) {
					mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
				}
			} catch (Exception e) {
				mediador.setTipo(1);
			}
			mediador.setRetribucionAsegurado(SiNo.NO);
			mediador.setImporteRetribucion(new BigDecimal(0));
			// DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			if (subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_OPERADOR_BANCA_SEGURO) ||
					subentidad.getTipoMediador().getId().equals(Constants.TIPO_MEDIADOR_COLABORADOR_EXTERNO)) {
				
				RazonSocial rs = RazonSocial.Factory.newInstance();
				rs.setRazonSocial(Constants.RAZON_SOCIAL_RGA_MEDIACION);
				mediador.setRazonSocial(rs);
				
			} else if (subentidad.getTipoidentificacion().equals("CIF")) {
				RazonSocial rs = RazonSocial.Factory.newInstance();
				rs.setRazonSocial(subentidad.getNomsubentidad());
				mediador.setRazonSocial(rs);

			} else {

				
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
		
				nom.setNombre(subentidad.getNombre());
				nom.setApellido1(subentidad.getApellido1());
				nom.setApellido2(subentidad.getApellido2());
				mediador.setNombreApellidos(nom);

				

			}
			entidad.setMediador(mediador);
		}

		return entidad;
	}

	/* Pet. 57626 ** MODIF TAM (29/04/2020) ** Inicio */
	/*
	 * Anadimos un nuevo metodo para el Calculo de Rendimiento de las agricolas
	 */
	/**
	 * Crea un objeto poliza preparado para su envio al servicio web de
	 * rendimientos. Es una version simplificada del normal.
	 * 
	 * @param poliza
	 * @param cp
	 * @param listaDatosVariables
	 * @param lstCoberturas
	 * @param aplicaReduccionRdto
	 * @param listaCPM
	 * @param codsConceptos
	 * @param usuario
	 * @param colIdParcelasFiltro
	 * @return
	 * @throws ValidacionPolizaException
	 * @throws DAOException
	 */

	public static Poliza transformarParaEnvioWSRecalculo(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, Map<BigDecimal, List<String>> listaDatosVariables, boolean aplicaReduccionRdto,
			List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos, Usuario usuario, Set<Long> colIdParcelasFiltro, final String webServiceToCall)
			throws ValidacionPolizaException, DAOException {
		Poliza polizaAS = Poliza.Factory.newInstance();

		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		if (!StringUtils.nullToString(poliza.getReferencia()).equals("")) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		logger.debug("Generando XML colectivo/tomador");
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!poliza.getLinea().getCodlinea().toString().equals("303")) {
			// COLECTIVO
			Colectivo c = Colectivo.Factory.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			Tomador tomador = Tomador.Factory.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());

			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);

			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);

			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(""))
				dc.setEmail(tomadorPoliza.getEmail());
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(""))
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			tomador.setDatosContacto(dc);

			polizaAS.setTomador(tomador);
		}

		// ASEGURADO
		logger.debug("Generando XML segurado");
		Asegurado a = Asegurado.Factory.newInstance();
		a.setNif(poliza.getAsegurado().getNifcif());
		if (poliza.getAsegurado().getTipoidentificacion().equals("CIF")) {
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(poliza.getAsegurado().getRazonsocial());
			a.setRazonSocial(rs);
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(poliza.getAsegurado().getNombre());
			nom.setApellido1(poliza.getAsegurado().getApellido1());
			if (!"".equals(StringUtils.nullToString(poliza.getAsegurado().getApellido2()).trim()))
				nom.setApellido2(poliza.getAsegurado().getApellido2());
			a.setNombreApellidos(nom);
		}

		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(poliza.getAsegurado().getBloque());
		dir.setCp(poliza.getAsegurado().getCodpostalstr());
		dir.setEscalera(poliza.getAsegurado().getEscalera());
		if (poliza.getAsegurado().getLocalidad().getNomlocalidad().length() > 30)
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad().substring(0, 30));
		else
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad());
		dir.setNumero(poliza.getAsegurado().getNumvia());
		dir.setPiso(poliza.getAsegurado().getPiso());
		dir.setProvincia(poliza.getAsegurado().getLocalidad().getProvincia().getCodprovincia().intValue());
		dir.setVia(
				poliza.getAsegurado().getVia().getClave() + " " + poliza.getAsegurado().getDireccion().toUpperCase());
		a.setDireccion(dir);

		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(poliza.getAsegurado().getEmail());
		dContacto.setTelefonoFijo(Integer.parseInt(poliza.getAsegurado().getTelefono()));
		if (!StringUtils.nullToString(poliza.getAsegurado().getMovil()).equals(""))
			dContacto.setTelefonoMovil(Integer.parseInt(poliza.getAsegurado().getMovil()));
		a.setDatosContacto(dContacto);

		polizaAS.setAsegurado(a);
		// FIN ASEGURADO

		/* Pet. 57626 ** MODIF TAM (08/05/2020) ** Inicio */
		// ENTIDAD
		logger.debug("Generando XML entidad");
		Entidad entidad = getEntidad(poliza);
		/* Pet. 57626 ** MODIF TAM (08/05/2020) ** Fin */

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// COBERTURAS
		logger.debug("Generando XML coberturas");
		Cobertura cobertura = Cobertura.Factory.newInstance();
		cobertura.setModulo(String.format("%-5s", cp.getId().getCodmodulo()));

		/* Pet. 63485-FASE II ** MODIF TAM (21.09.2020) ** Inicio */
		/*
		 * DatosVariables datosVariables = getDatosVariablesCobert(poliza, cp,
		 * aplicaReduccionRdto, listaCPM);
		 */
		DatosVariables datosVariables = DatosVariables.Factory.newInstance();
		try {
			datosVariables = getDatosVarCobertRendimientos(poliza, cp, aplicaReduccionRdto, listaCPM);
		} catch (Exception e) {
			throw new ValidacionPolizaException(
					"error al obtener los Datos Variables de Coberturas en Calculo de Rendimientos");
		}
		/* Pet. 63485-FASE II ** MODIF TAM (21.09.2020) ** Fin */

		cobertura.setDatosVariables(datosVariables);
		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS

		// OBJETOS ASEGURADOS
		logger.debug("Objetos asegurados");
		ObjetosAsegurados objAseg = ObjetosAsegurados.Factory.newInstance();

		org.w3c.dom.Node importedNode;

		List<ParcelaDocument> parcelasCol = getParcelasArray(poliza, cp, listaDatosVariables, aplicaReduccionRdto,
				codsConceptos, colIdParcelasFiltro, webServiceToCall);

		for (ParcelaDocument parcelaDoc : parcelasCol) {
			importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
					true);
			objAseg.getDomNode().appendChild(importedNode);
		}

		polizaAS.setObjetosAsegurados(objAseg);
		// FIN OBJETOS ASEGURADOS

		return polizaAS;
	}

	/* Pet. 63485-Fase II ** MODIF TAM (23.09.2020) ** Inicio */
	private static DatosVariables getDatosVarCobertRendimientos(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, boolean aplicaReduccionRdto, List<BigDecimal> listaCPM) throws Exception {

		logger.debug("PolizaUnificadaTransformer - getDatosVarCobertRendimientos [INIT]");
		DatosVariables datosVariables = DatosVariables.Factory.newInstance();

		/* Comprobamos el valor insertado a nivel de clase para el riesgo cubierto */
		Boolean incluirDatVariables = false;
		List<RiesgoCubiertoModulo> lstRcmodulo = new ArrayList<RiesgoCubiertoModulo>();
		Clase clase = claseManager.getClase(poliza);

		incluirDatVariables = consultaClaseRiesgoCubierto(poliza);

		logger.debug("Se incluyen DatosVariables por especificacion en clase:" + incluirDatVariables);

		/*
		 * NO SE ENVIAN DATOS VARIABLES DE COBERTURA, EXCEPTO SI SE HA INDICADO EN LA
		 * CALSE QUE SE INCLUYAN EL RIESGO CUBIERTO ELEGIDO
		 */
		if (incluirDatVariables) {

			/*
			 * Por cada modulo incluido en la poliza, se comprueba si hay registro en la
			 * tabla de "MascaraLimitesRdto" para el codconcepto 363 y los cultivos del
			 * modulo.
			 */
			Long lineaseguroid = poliza.getLinea().getLineaseguroid();
			String codModulo = cp.getId().getCodmodulo();

			List<BigDecimal> lstCodCultivos = seleccionPolizaDao.getListCodCultivosClase(clase.getId());
			boolean DatosVarRiesgCub = false;

			DatosVarRiesgCub = calculoPrecioProduccionManager.getRiesgoCubElegCalculoRendi(lineaseguroid, codModulo,
					lstCodCultivos);

			logger.debug("Valor de DatosVarRiesgCub (1) :" + DatosVarRiesgCub);
			if (!DatosVarRiesgCub) {
				// anadimos el cultivo 999
				if (!lstCodCultivos.contains(new BigDecimal(999))) {
					lstCodCultivos.add(new BigDecimal(999));

					// Lanzamos consulta con el cultivo 999
					DatosVarRiesgCub = calculoPrecioProduccionManager.getRiesgoCubElegCalculoRendi(lineaseguroid,
							codModulo, lstCodCultivos);
				}
			}
			logger.debug("Valor de DatosVarRiesgCub (2) :" + DatosVarRiesgCub);

			if (DatosVarRiesgCub) {
				lstRcmodulo = cuadroCoberturasManager.getRiesgosCubModuloCalcRendimiento(lineaseguroid, codModulo);
			}

			/*
			 * Si se obtienen Riesgos Cubiertos se envian en los datos variables de la
			 * cobertura
			 */
			if (lstRcmodulo.size() > 0) {
				logger.debug("Se han obtenido RiesgosCubiertos, se insertan en DAtos VAriables de la cobertura");

				for (RiesgoCubiertoModulo riesgoCMod : lstRcmodulo) {

					BigDecimal codconceptoppalmod = riesgoCMod.getConceptoPpalModulo().getCodconceptoppalmod();
					BigDecimal codRiesgoCubierto = riesgoCMod.getRiesgoCubierto().getId().getCodriesgocubierto();
					String valor = riesgoCMod.getElegible().toString();

					List<RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
					ArrayList<RiesgoCubiertoElegido> lstRCEA = new ArrayList<RiesgoCubiertoElegido>(lstRCE);

					RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
					rCubEleg.setCodRCub(codRiesgoCubierto.intValue());
					rCubEleg.setCPMod(codconceptoppalmod.intValue());
					rCubEleg.setValor(valor);
					lstRCEA.add(rCubEleg);
					datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new RiesgoCubiertoElegido[lstRCEA.size()]));

				}

			}

		}
		logger.debug("PolizaUnificadaTransformer - getDatosVarCobertRendimientos [END]");
		return datosVariables;
	}

	public static boolean consultaClaseRiesgoCubierto(com.rsi.agp.dao.tables.poliza.Poliza p) {

		Clase clase = new Clase();
		clase = claseManager.getClase(p);
		boolean datosVar = false;

		// calculamos el intervalo de CoefReduccRdto
		if (clase != null) {

			if (clase.getComprobarRce() != null) {
				Character Rce = clase.getComprobarRce();

				if (Rce.equals('S')) {
					datosVar = true;
				} else {
					datosVar = false;
				}

			} else {
				datosVar = false;
			}

		}
		return datosVar;
	}
	/* Pet. 63485-Fase II ** MODIF TAM (23.09.2020) ** Fin */

	public static CosteGrupoNegocio cargaDistribucionCoste1Default_old() {

		CosteGrupoNegocio costeGrupo = CosteGrupoNegocio.Factory.newInstance();

		costeGrupo.setPrimaComercial(new BigDecimal(0));
		costeGrupo.setPrimaComercialNeta(new BigDecimal(0));
		costeGrupo.setCosteTomador(new BigDecimal(0));
		costeGrupo.setRecargoConsorcio(new BigDecimal(0));
		costeGrupo.setReciboPrima(new BigDecimal(0));
		costeGrupo.setGrupoNegocio("1");

		es.agroseguro.contratacion.costePoliza.SubvencionEnesa se = es.agroseguro.contratacion.costePoliza.SubvencionEnesa.Factory
				.newInstance();
		ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstEnesa = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa>();
		// Le pongo el codigo de la subvencion "Poliza exenta de Subvencion"
		se.setTipo(Integer.parseInt("1"));
		se.setImporte(new BigDecimal(0));
		lstEnesa.add(se);

		costeGrupo.setSubvencionEnesaArray(
				lstEnesa.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionEnesa[1]));

		es.agroseguro.contratacion.costePoliza.SubvencionCCAA sc = es.agroseguro.contratacion.costePoliza.SubvencionCCAA.Factory
				.newInstance();
		ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionCCAA> lstCa = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionCCAA>();
		sc.setCodigoOrganismo("0");
		sc.setImporte(new BigDecimal(0));
		lstCa.add(sc);
		costeGrupo.setSubvencionCCAAArray(lstCa.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionCCAA[1]));

		return costeGrupo;
	}

	/**
	 * Metodo para obtener los datos variables asociados a las coberturas de una
	 * comparativa
	 * 
	 * @param polizaCpl
	 * @param poliza
	 *            Poliza
	 * @param cp
	 *            Comparativa
	 * @return
	 */

	private static DatosVariables getDatosVariablesCobertCpl(com.rsi.agp.dao.tables.poliza.Poliza polizaPpal,
			com.rsi.agp.dao.tables.poliza.Poliza polizaCpl, ComparativaPoliza cp, boolean aplicaReduccionRdto,
			List<BigDecimal> listaCPM) throws ValidacionPolizaException {
		
		DatosVariables datosVariables = DatosVariables.Factory.newInstance();		

		// primero obtenemos las comparativas de la poliza cpl y sacamos los datos
		// variables
		logger.debug("PolizaUnificadaTransformer - getDatosVariablesCobertCpl");
		logger.debug("Valor de listaCPM:" + StringUtils.nullToString(listaCPM));

		for (ComparativaPoliza compCpl : polizaCpl.getComparativaPolizas()) {

			logger.debug("Dentro del bucle de Comparativas de las Complementarias");

			logger.debug("Valor de Codconceptoppalmod: " + compCpl.getId().getCodconceptoppalmod());
			logger.debug("Valor de Codconcepto: " + compCpl.getId().getCodconcepto());
			logger.debug("Valor de Codriesgocubierto:" + compCpl.getId().getCodriesgocubierto());

			// Comprueba si hay que insertar el CPM como dato variable
			if (CoberturasUtils.isCPMPermitido(compCpl.getId().getCodconceptoppalmod(),
					compCpl.getId().getCodconcepto(), listaCPM)) {
				try {

					logger.debug("Entra en el if");
					setDatoVariable(compCpl.getId().getCodconcepto(), compCpl.getId().getCodconceptoppalmod(),
							compCpl.getId().getCodriesgocubierto(), compCpl.getId().getCodvalor(), datosVariables,
							aplicaReduccionRdto);

				} catch (ParseException e) {
					throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", compCpl.getId().getCodvalor().toString()).replace("{dato}", compCpl.getId().getCodconcepto().toString()));
				} catch (NumberFormatException e) {
					throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", compCpl.getId().getCodvalor().toString()).replace("{dato}", compCpl.getId().getCodconcepto().toString()));
				} catch (Exception e) {
					throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", compCpl.getId().getCodvalor().toString()).replace("{dato}", compCpl.getId().getCodconcepto().toString()));
				}
			}
		}
		if (null == polizaCpl.getComparativaPolizas() || polizaCpl.getComparativaPolizas().size() == 0) {
			logger.debug("Entramos en el if de comparativas");
			for (ComparativaPoliza compPpal : polizaPpal.getComparativaPolizas()) {

				logger.debug("Dentro del bucle de Comparativas de las Principal");

				if (compPpal.getId().getCodmodulo().equals(polizaPpal.getCodmodulo())) {

					logger.debug("Valor de Codconcepto:" + compPpal.getId().getCodconcepto());

					// Comprueba si hay que insertar el CPM como dato variable
					if (CoberturasUtils.isCPMPermitido(compPpal.getId().getCodconceptoppalmod(),
							compPpal.getId().getCodconcepto(), listaCPM)) {
						try {
							if (compPpal.getId().getCodconcepto().compareTo(new BigDecimal(106)) != 0) {

								setDatoVariable(compPpal.getId().getCodconcepto(),
										compPpal.getId().getCodconceptoppalmod(),
										compPpal.getId().getCodriesgocubierto(), compPpal.getId().getCodvalor(),
										datosVariables, aplicaReduccionRdto);
							}
						} catch (ParseException e) {
							throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", compPpal.getId().getCodvalor().toString()).replace("{dato}", compPpal.getId().getCodconcepto().toString()));
						} catch (NumberFormatException e) {
							throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", compPpal.getId().getCodvalor().toString()).replace("{dato}", compPpal.getId().getCodconcepto().toString()));
						} catch (Exception e) {
							throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", compPpal.getId().getCodvalor().toString()).replace("{dato}", compPpal.getId().getCodconcepto().toString()));
						}
					}
				}
			}
		}
		return datosVariables;
	}

	/**
	 * metodo para obtener un array de parcelas para enviar a Agroseguro a partir de
	 * una coleccion de parcelas de poliza
	 * 
	 * @param poliza
	 *            Poliza de la aplicacion
	 * @param cp
	 *            Comparativa actual.
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionPolizaException
	 */
	private static List<ParcelaDocument> getParcelasCpl(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, Map<BigDecimal, List<String>> listaDatosVariables, boolean aplicaReduccionRdto,
			List<BigDecimal> codsConceptos, Boolean esValid, Boolean esCalcular) throws ValidacionPolizaException {
		List<ParcelaDocument> parc = new ArrayList<ParcelaDocument>(poliza.getParcelas().size());
		ParcelaDocument pd;
		Parcela p;

		logger.debug("**@@** PolizaUnificadaTransformer -getParcelasCpl");

		for (com.rsi.agp.dao.tables.poliza.Parcela parcela : poliza.getParcelas()) {

			logger.debug("**@@** PolizaUnificadaTransformer- Parcela Modificada:" + parcela.getAltaencomplementario());

			if (parcela.getAltaencomplementario().equals('S')) {

				pd = ParcelaDocument.Factory.newInstance();
				p = Parcela.Factory.newInstance();
				p.setHoja(parcela.getHoja().intValue());
				p.setNumero(parcela.getNumero().intValue());
				p.setNombre(parcela.getNomparcela());
				
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir el nuevo atributo 'parcelaAgricola' en la etiqueta 'Parcela' */
				/* DE MOMENTO Y POR PETICIÓN DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENVÍA EN NINGÚN XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
				/*if (parcela.getParcAgricola() != null) {
					p.setParcelaAgricola(parcela.getParcAgricola());	
				}*/
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */

				if (parcela.getCodprovsigpac() != null) {
					// rellenamos el sigpac
					SIGPAC sigpac = SIGPAC.Factory.newInstance();
					sigpac.setAgregado(parcela.getAgrsigpac().intValue());
					sigpac.setParcela(parcela.getParcelasigpac().intValue());
					sigpac.setPoligono(parcela.getPoligonosigpac().intValue());
					sigpac.setProvincia(parcela.getCodprovsigpac().intValue());
					sigpac.setRecinto(parcela.getRecintosigpac().intValue());
					sigpac.setTermino(parcela.getCodtermsigpac().intValue());
					sigpac.setZona(parcela.getZonasigpac().intValue());
					p.setSIGPAC(sigpac);
				}
				Ambito ambito = Ambito.Factory.newInstance();
				ambito.setComarca(parcela.getTermino().getId().getCodcomarca().intValue());
				ambito.setProvincia(parcela.getTermino().getId().getCodprovincia().intValue());
				ambito.setSubtermino(parcela.getTermino().getId().getSubtermino() + VACIO);
				ambito.setTermino(parcela.getTermino().getId().getCodtermino().intValue());
				
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
				if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
					p.setUbicacion(ambito);
				}
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
				
				// Cosecha
				Cosecha cosecha = Cosecha.Factory.newInstance();
				cosecha.setCultivo(parcela.getCodcultivo().intValue());
				cosecha.setVariedad(parcela.getCodvariedad().intValue());
				CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
				es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = new es.agroseguro.contratacion.parcela.CapitalAsegurado[parcela
						.getCapitalAsegurados().size()];
				int cntCapAseg = 0;
				for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado ca : parcela.getCapitalAsegurados()) {

					StringBuffer parIdent = new StringBuffer();
					parIdent.append(parcela.getCodcultivo());
					parIdent.append("." + parcela.getCodvariedad());
					parIdent.append("." + parcela.getTermino().getId().getCodprovincia());
					parIdent.append("." + parcela.getTermino().getId().getCodcomarca());
					parIdent.append("." + parcela.getTermino().getId().getCodtermino());
					parIdent.append("." + parcela.getTermino().getId().getSubtermino());
					parIdent.append("." + ca.getTipoCapital().getCodtipocapital());

					es.agroseguro.contratacion.parcela.CapitalAsegurado c = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
							.newInstance();

					// DE MOMENTO EN TB_CAP_ASEG_REL_MODULO SOLO SE ESTA
					// GUARDANDO UN VALOR INDEPENDIENTEMENTE
					// DE LOS POSIBLES MODULOS SELECCIONADOS, ASI QUE ME QUEDO
					// CON EL PRIMERO Y LISTO
					// Hace un set del precio y produccion aun cuando la
					// coleccion no tiene nada

					Set<CapAsegRelModulo> colCapAsegRelModulo = ca.getCapAsegRelModulos();

					if (ca.getCapAsegRelModulos().size() > 0) {
						for (CapAsegRelModulo care : colCapAsegRelModulo) {
							logger.error("P000018845: " + care.getCodmodulo() + " - " + cp.getId().getCodmodulo());
							if (!esValid) {

								/*
								 * TAM (10.06.2020) - Comento esta parte por que para complementarias no hay que
								 * realizar la comparacion
								 */
								/* if (care.getCodmodulo().equals(cp.getId().getCodmodulo())) { */
								if (care.getPreciomodif() != null) {
									c.setPrecio(care.getPreciomodif());
								} else {
									if (care.getPrecio() != null) {
										c.setPrecio(care.getPrecio());
									} else {
										c.setPrecio(new BigDecimal(0));
									}
								}
								if (care.getProduccionmodif() != null) {
									c.setProduccion(ca.getIncrementoproduccion().intValue());
								} else {
									c.setProduccion(ca.getIncrementoproduccion().intValue());
								}
								break;
							} else {
								/*
								 * TAM (10.06.2020) - Comento esta parte por que para complementarias no hay que
								 * realizar la comparacion
								 */
								if (care.getProduccionmodif() != null) {
									c.setProduccion(ca.getIncrementoproduccion().intValue());
								} else {
									c.setProduccion(ca.getIncrementoproduccion().intValue());
								}
								break;
							}
						}
					} else {
						logger.debug("getCapAsegRelModulos size = 0");
						if (!esValid) {
							if (ca.getPrecio() != null) {
								c.setPrecio(ca.getPrecio());
							} else {
								c.setPrecio(new BigDecimal(0));
							}
						}

						if (ca.getProduccion() != null) {
							c.setProduccion(ca.getIncrementoproduccion().intValue());
						} else {
							c.setProduccion(0);
						}
					}
					c.setSuperficie(ca.getSuperficie());
					c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());

					// **** Tatiana (02.09.2020) *** Para las complementarias no se envian los
					// datos variables en la validacion
					// Obtenemos los datos variables de parcela y de coberturas a nivel de parcela
					if (!esValid) {
						if (!esCalcular) {
							c.setDatosVariables(getDatosVarParcComple(ca.getDatoVariableParcelas(),
									parcela.getCoberturasParcela(), ca.getIdcapitalasegurado(), listaDatosVariables,
									cp.getId().getCodmodulo(), poliza.getLinea().getCodlinea(), aplicaReduccionRdto,
									codsConceptos, poliza.getIdpoliza()));
						} else {
							if (ca.getAltaencomplementario().equals('S')) {

								c.setProduccion(ca.getIncrementoproduccion().intValue());
								c.setSuperficie(ca.getSuperficie());
								c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
								c.setPrecio(ca.getPrecio());

								c.setDatosVariables(getDatosVariablesParcelaCpl(ca.getDatoVariableParcelas(),
										parcela.getCoberturasParcela(), ca.getIdcapitalasegurado(), listaDatosVariables,
										poliza.getCodmodulo(), poliza.getLinea().getCodlinea(), aplicaReduccionRdto,
										codsConceptos, poliza.getIdpoliza()));
							}

						}

					}

					capitales[cntCapAseg] = c;
					cntCapAseg++;
				}
				capAseg.setCapitalAseguradoArray(capitales);
				cosecha.setCapitalesAsegurados(capAseg);
				p.setCosecha(cosecha);
				pd.setParcela(p);
				parc.add(pd);
			} /* Fin del if */
		} /* Fin del For */

		return parc;
	}

	public static PolizaDocument transformarCplValidar(final com.rsi.agp.dao.tables.poliza.Poliza poliza,
			final ComparativaPoliza cp, final Map<BigDecimal, List<String>> listaDatosVariables,
			final boolean aplicaReduccionRdto, final List<BigDecimal> listaCPM, final List<BigDecimal> codsConceptos,
			final GenericDao<?> genericDao, List<GruposNegocio> gruposNegocio,
			final Map<Long, DatosVariables> dvEspecialesExplot, final boolean esGanado, final String webServiceToCall,
			final boolean aplicaDtoRec, final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws ValidacionPolizaException {

		PolizaDocument polizaDoc = PolizaDocument.Factory.newInstance();
		Poliza polizaAS = Poliza.Factory.newInstance();
		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		boolean esFinanciada = false;

		if (!StringUtils.nullToString(poliza.getReferencia()).equals(VACIO)) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!poliza.getLinea().getCodlinea().toString().equals("303")) {
			// COLECTIVO
			Colectivo c = Colectivo.Factory.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			Tomador tomador = Tomador.Factory.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);
			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);
			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(VACIO)) {
				dc.setEmail(tomadorPoliza.getEmail());
			}
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(VACIO)) {
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			}
			tomador.setDatosContacto(dc);
			polizaAS.setTomador(tomador);
		}

		// ASEGURADO

		Asegurado asegurado = Asegurado.Factory.newInstance();
		asegurado.setNif(poliza.getAsegurado().getNifcif());
		polizaAS.setAsegurado(asegurado);

		polizaAS.setAsegurado(asegurado);
		// FIN ASEGURADO

		// ENTIDAD
		Entidad entidad = getEntidad(poliza);

		// GASTOS
		Gastos[] gastos = getGastos(gruposNegocio, poliza.getSetPolizaPctComisiones(), esGanado, poliza, aplicaDtoRec,
				comsPctCalculado);
		entidad.setGastosArray(gastos);
		// FIN GASTOS

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// COBERTURAS
		Cobertura cobertura = Cobertura.Factory.newInstance();
		cobertura.setModulo(String.format("%-5s", poliza.getCodmodulo()));
		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS

		// OBJETOS ASEGURADOS
		ObjetosAsegurados objAseg = ObjetosAsegurados.Factory.newInstance();

		/* Pet. 57626 ** MODIF TAM (29/0472020) ** Inicio **/
		/*
		 * Incluimos validacion para guardar en el objeto asegurado las explotaciones o
		 * las Parcelas dependiendo de si se trata de una poliza de Ganado o agricola
		 */
		org.w3c.dom.Node importedNode;

		/* PARCELAS DE POLIZAS COMPLEMENTARIAS */

		Boolean esValid = true;

		List<ParcelaDocument> parcelasCol = getParcelasCpl(poliza, cp, listaDatosVariables, aplicaReduccionRdto,
				codsConceptos, esValid, false);

		/* Pet. 70105 ** MODIF TAM (03.03.2021) ** Inicio */
		/* Creamos un nuevo comparator para ordenar las parcelas para el envio al xml */
		Collections.sort(parcelasCol, new ParcelasComparator());

		for (ParcelaDocument parcelaDoc : parcelasCol) {
			importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
					true);
			objAseg.getDomNode().appendChild(importedNode);
		}

		polizaAS.setObjetosAsegurados(objAseg);

		// FIN OBJETOS ASEGURADOS

		// INICIO COSTE POLIZA
		Long idComparativa = null;
		if (cp != null && cp.getId() != null && cp.getId().getIdComparativa() != null)
			idComparativa = cp.getId().getIdComparativa();

		/* Recuperamos la Distribucion de Coste para las agricolas */
		CostePoliza coste = getCosteCpl(poliza, gruposNegocio, idComparativa, cp);

		polizaAS.setCostePoliza(coste);
		// FIN COSTE POLIZA

		// PAGO
		boolean lineaSup2021 = false;
		try {
			lineaSup2021 = pagoPolizaDao.lineaContratacion2021(poliza.getLinea().getCodplan(),
					poliza.getLinea().getCodlinea(), false);
		} catch (Exception e) {
			logger.error(
					"PolizaUnificadTransformer.java - Se ha producido un error al recuperar la lineacontratacion " + e);
		}
		es.agroseguro.contratacion.Pago pago = lineaSup2021
				? getPago2021(poliza, cp, esFinanciada, webServiceToCall)
				: getPagoCpl(poliza, cp, esFinanciada, webServiceToCall);

		polizaAS.setPago(pago);
		// FIN PAGO

		@SuppressWarnings("unchecked")
		List<SubvencionSocio> subvencionSocios = seleccionPolizaDao.getObjects(SubvencionSocio.class, "poliza.idpoliza",
				poliza.getIdpoliza());
		
		List<SocioId> lstSociosConSubvencion = new ArrayList<SocioId>();
		// SOCIOS
		if (subvencionSocios != null && !subvencionSocios.isEmpty()) {
			RelacionSocios rs = RelacionSocios.Factory.newInstance();
			List<Socio> lstSocios = new ArrayList<Socio>();

			SubvencionesDeclaradas sds = SubvencionesDeclaradas.Factory.newInstance();
			List<SubvencionDeclarada> lstSds = new ArrayList<SubvencionDeclarada>();

			Socio s = Socio.Factory.newInstance();
			boolean tieneATP = false;
			boolean tieneSubv03 = false;
			for (SubvencionSocio ss : subvencionSocios) {
				
				logger.debug("Procesando subvencion " + ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa());
				tieneSubv03 |= Constants.CARACT_ASEGURADO_PERSONA_JURIDICA.equals(ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa());
				
				tieneATP = false;
				
				if (s.getNif() == null || (s.getNif() != null && !s.getNif().equals(ss.getSocio().getId().getNif()))) {
					lstSociosConSubvencion.add(ss.getSocio().getId());
					// Vuelco las subvenciones, se las asigno al socio, Anado el
					// socio a la lista y "resetear el socio"
					if (s.getNif() != null) {
						sds.setSubvencionDeclaradaArray(lstSds.toArray(new SubvencionDeclarada[lstSds.size()]));
						s.setSubvencionesDeclaradas(sds);
						lstSocios.add(s);
					}

					s = Socio.Factory.newInstance();

					s.setNif(ss.getSocio().getId().getNif());
					// El numero sera el orden que ocupa el socio en la tabla de
					// polizas socios
					s.setNumero(getOrdenPolizaSocio(poliza, ss));

					if (!ss.getSocio().getTipoidentificacion().equals("CIF")) {
						NombreApellidos na = NombreApellidos.Factory.newInstance();
						na.setNombre(ss.getSocio().getNombre());
						na.setApellido1(ss.getSocio().getApellido1());
						if (!"".equals(StringUtils.nullToString(ss.getSocio().getApellido2()).trim()))
							na.setApellido2(ss.getSocio().getApellido2());
						s.setNombreApellidos(na);
					} else {
						RazonSocial rsoc = RazonSocial.Factory.newInstance();
						rsoc.setRazonSocial(ss.getSocio().getRazonsocial());
						s.setRazonSocial(rsoc);
					}

					sds = SubvencionesDeclaradas.Factory.newInstance();
					lstSds = new ArrayList<SubvencionDeclarada>();
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(Integer
							.parseInt(ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa() + VACIO));
					lstSds.add(sd);
					if (ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
							.compareTo(Constants.SUBVENCION20) == 0)
						tieneATP = true;
				} else {
					// le Anado las subvenciones
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(Integer
							.parseInt(ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa() + VACIO));
					lstSds.add(sd);
					if (ss.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()
							.compareTo(Constants.SUBVENCION20) == 0)
						tieneATP = true;
				}
				if (!StringUtils.nullToString(ss.getSocio().getNumsegsocial()).equals(VACIO) && tieneATP) {
					SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
					segSocial.setProvincia(Integer.parseInt(ss.getSocio().getNumsegsocial().substring(0, 2)));
					segSocial.setNumero(Integer.parseInt(ss.getSocio().getNumsegsocial().substring(2, 10)));
					segSocial.setCodigo(ss.getSocio().getNumsegsocial().substring(10));
					if (!StringUtils.nullToString(ss.getSocio().getRegimensegsocial()).equals(VACIO)) {
						segSocial.setRegimen(Short.parseShort(ss.getSocio().getRegimensegsocial() + VACIO));
					}
					sds.setSeguridadSocial(segSocial);
				}
			}

			// Anado el de la ultima iteracion
			
			sds.setSubvencionDeclaradaArray(lstSds.toArray(new SubvencionDeclarada[lstSds.size()]));
			s.setSubvencionesDeclaradas(sds);
			lstSocios.add(s);
				
			// Solo se anahden si el asegurado tiene el tipo de subvencion "3 - Caract. Asegurado Persona Juridica"
			if (tieneSubv03) {
				rs.setSocioArray(lstSocios.toArray(new Socio[lstSocios.size()]));
	
				polizaAS.setRelacionSocios(rs);
			}
		}
		
		@SuppressWarnings("unchecked")
		List<SubAseguradoENESAGanado> subAseguradoENESAGanados = seleccionPolizaDao
				.getObjects(SubAseguradoENESAGanado.class, "poliza.idpoliza", poliza.getIdpoliza());
		
		// Anadimos los socios ganado si el asegurado tiene el tipo de subvencion "3 -
		// Caract. Asegurado Persona Juridica"
		if (subAseguradoENESAGanados != null && !subAseguradoENESAGanados.isEmpty()) {
			for (SubAseguradoENESAGanado saeg : subAseguradoENESAGanados) {
				if (saeg.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()
						.equals(Constants.CARACT_ASEGURADO_PERSONA_JURIDICA)) {
					// sociosGanado
					if (poliza.getPolizaSocios().size() > 0) {
						for (com.rsi.agp.dao.tables.poliza.PolizaSocio ps : poliza.getPolizaSocios()) {
							if (!lstSociosConSubvencion.contains(ps.getSocio().getId())) {
								Socio soc = Socio.Factory.newInstance();
								soc.setNif(ps.getSocio().getId().getNif());
								if (!ps.getSocio().getTipoidentificacion().equals("CIF")) {
									NombreApellidos na = NombreApellidos.Factory.newInstance();
									na.setNombre(ps.getSocio().getNombre());
									na.setApellido1(ps.getSocio().getApellido1());
									if (!"".equals(StringUtils.nullToString(ps.getSocio().getApellido2()).trim()))
										na.setApellido2(ps.getSocio().getApellido2());
									soc.setNombreApellidos(na);
								} else {
									RazonSocial rsoc = RazonSocial.Factory.newInstance();
									rsoc.setRazonSocial(ps.getSocio().getRazonsocial());
									soc.setRazonSocial(rsoc);
								}

								SubvencionesDeclaradas sds = rellenaSubvencionesDeclaradas(ps);
								if (sds != null)
									soc.setSubvencionesDeclaradas(sds);

								List<Socio> lstSoc = new ArrayList<Socio>();
								if (polizaAS.getRelacionSocios() != null) {
									lstSoc = Arrays.asList(polizaAS.getRelacionSocios().getSocioArray());
								} else {
									polizaAS.setRelacionSocios(RelacionSocios.Factory.newInstance());
								}
								ArrayList<Socio> arrSoc = new ArrayList<Socio>(lstSoc);

								// El numero sera el orden que ocupa el socio en la tabla de
								// polizas socios
								soc.setNumero(ps.getOrden() != null ? ps.getOrden().intValue() : 1);
								arrSoc.add(soc);

								polizaAS.getRelacionSocios().setSocioArray(arrSoc.toArray(new Socio[arrSoc.size()]));

							}
						}
					}
					break;
					// FIN sociosGanado
				}
			}
		}

		// FIN DE SOCIOS
		polizaDoc.setPoliza(polizaAS);
		return polizaDoc;
	}

	private static es.agroseguro.contratacion.Pago getPagoCpl(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			ComparativaPoliza cp, Boolean esFinanciada, String webServiceToCall) {
		Pago pago = Pago.Factory.newInstance();
		boolean pagoManual = false;
		boolean pagoDomiciliado = false;

		boolean fechaPagoInsertada = false;

		logger.debug("**@@** PolizaUnificadaTransformer - Dentro de getPagoCpl");
		logger.debug("**@@** Entramos por WebService: " + webServiceToCall);

		// Pet. 54046 ** MODIF TAM (31/10/2018) ** Inicio //
		boolean permiteEnvIban = false;
		boolean lineaContrSup2019 = false;

		BigDecimal codPlanPol = poliza.getLinea().getCodplan();
		BigDecimal codLineaPol = poliza.getLinea().getCodlinea();

		try {
			permiteEnvIban = pagoPolizaDao.polizaAgrPermiteEnvioIban(codPlanPol, codLineaPol);
		} catch (Exception e) {
			logger.error("PolizaUnificadaTransformar.java - Se ha producido un error" + e);
		}

		try {
			lineaContrSup2019 = pagoPolizaDao.lineaContratacion2019(codPlanPol, codLineaPol,
					poliza.getLinea().isLineaGanado());
			logger.debug("lineaContrSup2019: " + lineaContrSup2019);
		} catch (Exception e) {
			logger.error(
					"PolizaUnificadaTransformer.java - Se ha producido un erroral recuperar la lineacontratacion" + e);
		}

		// Si la linea de la poliza tiene fecha de contratacion anterior al
		// 01/06/2018, no se debe incluir la
		// etiqueta de "domiciliado en el pago)
		logger.debug("**@@** MODIFICAMOS PAGOS DE COMPLEMENTARIAS, Valor de permiteEnvIban:" + permiteEnvIban);

		if (permiteEnvIban == true) {
			if (poliza.getEsFinanciada().equals('S')) {
				pago.setDomiciliado("S");
				logger.debug("**@@** Antes de obtenerCuenta");
				pago.setCuenta(obtenerCuenta(poliza, true));
				pago.getCuenta().setDestinatario("A");
			} else {
				pago.setDomiciliado("N");
			}
		}

		if (poliza.getSubvencionSocios() != null && !poliza.getSubvencionSocios().isEmpty()) {

			logger.debug("**@@** Contiene datos de pago");
			for (PagoPoliza pp : poliza.getPagoPolizas()) {
				if (pp.getTipoPago().compareTo(Constants.PAGO_MANUAL) == 0) {
					if (pp.getBanco() != null) {
						StringBuilder banco = new StringBuilder(pp.getBanco().toString());
						if (banco.length() < 4) {
							while (banco.length() < 4) {
								banco.insert(0, "0");
							}
						}
						pago.setBanco(banco.toString());
						pagoManual = true;
						// si es pago manual se inserta la fecha que nos venga en el pago
						if (pp.getFecha() != null) {
							pago.setFecha(DateUtil.date2Calendar(pp.getFecha()));
							fechaPagoInsertada = true;
						}

					}
				}

				if (pp.getTipoPago().compareTo(Constants.DOMICILIACION_AGRO) == 0 && lineaContrSup2019 == true) {
					pagoDomiciliado = true;
				} else {
					pagoDomiciliado = false;
				}

				// Anadimos los datos de la domiciliacion en las polizas complementarias //
				logger.debug("Valor de pp.getEnvioIbanAgro():" + pp.getEnvioIbanAgro());

				if (pp.getEnvioIbanAgro() != null && pp.getEnvioIbanAgro().equals('S')) {
					logger.debug("Entramos y acutalizamos el XML de Pagos");
					pago.setDomiciliado("S");
					logger.debug("Domiciliado = S");

					Cuenta cuenta = Cuenta.Factory.newInstance();
					cuenta.setIban(pp.getIban() + pp.getCccbanco());
					cuenta.setDestinatario(pp.getDestinatarioDomiciliacion().toString());
					logger.debug("Destinatario =  " + pp.getDestinatarioDomiciliacion().toString());
					if (pp.getDestinatarioDomiciliacion().equals('O')) {
						cuenta.setTitular(pp.getTitularCuenta());
					}
					pago.setCuenta(cuenta);
					pago.setCuenta(cuenta);
				}

				// Pet. 54046 ** MODIF TAM (31.10.2018) ** Fin //
				break;
			}
		} else {

			logger.debug("Entramos en el else, valor de webServiceToCall: " + webServiceToCall);
			/*
			 * Al lanzar la llamada al S.W de validacion y en Complementarias, se enviara la
			 * informacion que nos haya devuelto la consulta de la situacion actualizada de
			 * la Ppal, en caso de que no se dispongan de datos.
			 */
			if (webServiceToCall.equals(Constants.WS_VALIDACION)) {

				logger.debug("**@@** Estamos en validacion y Recuperamos datos de Pago de la principal");
				for (PagoPoliza pp : poliza.getPolizaPpal().getPagoPolizas()) {
					logger.debug("**@@** Valor de tipoPago:" + pp.getTipoPago());

					/** PAGO MANUAL **/
					if (pp.getTipoPago().compareTo(Constants.PAGO_MANUAL) == 0) {
						if (pp.getBanco() != null) {
							String banco = pp.getBanco().toString();
							if (banco.length() < 4) {
								while (banco.length() < 4) {
									banco = "0" + banco;
								}
							}
							pago.setBanco(banco);
							pagoManual = true;
							// si es pago manual se inserta la fecha que nos venga en el pago
							if (pp.getFecha() != null) {
								pago.setFecha(DateUtil.date2Calendar(pp.getFecha()));
								fechaPagoInsertada = true;
							}

						}
						pago.setForma(String.valueOf(Constants.FORMA_PAGO_ALCONTADO));
						pago.setImporte(pp.getImporte());
					}

					/** CARGO EN CUENTA **/
					if (pp.getTipoPago().compareTo(Constants.CARGO_EN_CUENTA) == 0) {
						pago.setForma(String.valueOf(Constants.FORMA_PAGO_ALCONTADO));
						pago.setImporte(pp.getImporte());
					}

					/** DOMICILIADO CON AGROSEGURO **/
					if (pp.getTipoPago().compareTo(Constants.DOMICILIACION_AGRO) == 0) {
						logger.debug("La forma de Pago de la principal es Domiciliada");
						pago.setForma(pp.getFormapago().toString());
						logger.debug("Asignamos Forma de Pago:" + pago.getForma());
						pago.setImporte(pp.getImporte());
						logger.debug("Asignamos Importe:" + pago.getImporte());
						pago.setDomiciliado("T");

						Cuenta cuentaD = Cuenta.Factory.newInstance();
						cuentaD.setIban(pp.getIban() + pp.getCccbanco());
						cuentaD.setDestinatario(pp.getDestinatarioDomiciliacion().toString());

						logger.debug("Destinatario  = " + cuentaD.getDestinatario() + " y cuenta:" + cuentaD.getIban());

						if (pp.getDestinatarioDomiciliacion().equals('O')) {
							cuentaD.setTitular(pp.getTitularCuenta());
						}
						/*
						 * (13/07/2020) : Como estamos en F. de Pago Domiciliado, el banco y la fecha no
						 * se envian
						 */
						pago.setCuenta(cuentaD);
					}
				}

			} else {

				logger.debug("**@@** No estamos en validacion, informamos datos de pago");
				for (PagoPoliza pp : poliza.getPagoPolizas()) {
					if (pp.getTipoPago().compareTo(Constants.PAGO_MANUAL) == 0) {
						if (pp.getBanco() != null) {
							String banco = pp.getBanco().toString();
							if (banco.length() < 4) {
								while (banco.length() < 4) {
									banco = "0" + banco;
								}
							}
							pago.setBanco(banco);
							pagoManual = true;
							// si es pago manual se inserta la fecha que nos venga en el pago
							if (pp.getFecha() != null) {
								pago.setFecha(DateUtil.date2Calendar(pp.getFecha()));
								fechaPagoInsertada = true;
							}

						}
					}

					if (pp.getTipoPago().compareTo(Constants.DOMICILIACION_AGRO) == 0 && lineaContrSup2019 == true) {
						pagoDomiciliado = true;
					} else {
						pagoDomiciliado = false;
					}

					// Anadimos los datos de la domiciliacion en las polizas complementarias //
					logger.debug("Valor de pp.getEnvioIbanAgro():" + pp.getEnvioIbanAgro());

					if (pp.getEnvioIbanAgro() != null && pp.getEnvioIbanAgro().equals('S')) {
						logger.debug("Entramos y acutalizamos el XML de Pagos");
						pago.setDomiciliado("S");
						logger.debug("Domiciliado = S");

						Cuenta cuenta = Cuenta.Factory.newInstance();
						cuenta.setIban(pp.getIban() + pp.getCccbanco());
						cuenta.setDestinatario(pp.getDestinatarioDomiciliacion().toString());
						logger.debug("Destinatario = " + pp.getDestinatarioDomiciliacion().toString());
						if (pp.getDestinatarioDomiciliacion().equals('O')) {
							cuenta.setTitular(pp.getTitularCuenta());
						}
						pago.setCuenta(cuenta);
						pago.setCuenta(cuenta);
					}

					// Pet. 54046 ** MODIF TAM (31.10.2018) ** Fin //
					break;
				}

			}
		}

		if (!pagoManual) {
			if (pagoDomiciliado != true) {
				if (poliza.getColectivo().getIsCRM() != null && poliza.getColectivo().getIsCRM() > 0) {
					pago.setBanco("3058");
				} else {
					pago.setBanco("0198");
				}
			}
		}

		/*** Taty ****/
		BigDecimal cargoTomador = new BigDecimal(0);
		Set<DistribucionCoste2015> distCostesSet = poliza.getDistribucionCoste2015s();
		for (DistribucionCoste2015 dc : distCostesSet) {
			cargoTomador = dc.getCostetomador();
		}
		/*** Taty Fin ****/

		if (!webServiceToCall.equals(Constants.WS_VALIDACION)) {
			pago.setForma(String.valueOf(Constants.FORMA_PAGO_ALCONTADO));
			pago.setImporte(cargoTomador);
		}

		if (poliza.getEsFinanciada().equals('S')) {
			pago.setForma(String.valueOf(Constants.FORMA_PAGO_FINANCIADO));
			pago.setImporte(poliza.getImporte());
			/*
			 * if (saeca && null!=pagoPoliza){ pago.setImporte(pagoPoliza.getImportePago());
			 * }
			 */
		} else {
			/* pago.setForma(Constants.FORMA_PAGO_CONTADO); */
			/* pago.setImporte(cargoTomador); */
		}

		/** Pet. 57626 ** MODIF (07.07.2019) ** Inicio **/
		/*
		 * Si el Pago de la complementaria es Domiciliado no sse envia ni la fecha ni el
		 * banco
		 */
		if (pagoDomiciliado != true) {
			if (!fechaPagoInsertada) {
				if (poliza.getFechaPago() != null)
					pago.setFecha(DateUtil.date2Calendar(poliza.getFechaPago()));
				else
					pago.setFecha(new GregorianCalendar());
			}
		}

		/** Pet. 57625 ** MODIF (22.02.2019) ** Inicio **/
		/*
		 * Si se ha marcado la nueva forma de Pago "Domiciliacion en Agroseguro hay que
		 * enviar estos valores
		 */
		if (pagoDomiciliado) {
			logger.debug("Comprobamos que se ha marcado la forma de Pago Domiciliacion en Agro");
			pago.setDomiciliado("T");
		}

		/** Pet. 57625 ** MODIF (22.02.2019) ** Fin **/

		// Si la poliza se ha financiado
		if (poliza.getDatosAval() != null) {
			pago.setForma(String.valueOf(Constants.FORMA_PAGO_FINANCIADO));
			es.agroseguro.seguroAgrario.contratacion.complementario.Fraccionamiento fr = es.agroseguro.seguroAgrario.contratacion.complementario.Fraccionamiento.Factory
					.newInstance();
			es.agroseguro.seguroAgrario.contratacion.complementario.Aval av = es.agroseguro.seguroAgrario.contratacion.complementario.Aval.Factory
					.newInstance();
			av.setImporte(poliza.getDatosAval().getImporteAval());
			av.setNumero(poliza.getDatosAval().getNumeroAval().intValue());
			fr.addNewAval();
			fr.setAval(av);
			for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
				if (dc.getPeriodoFracc() != null) {
					fr.setPeriodo(dc.getPeriodoFracc());
				}
			}
			pago.addNewFraccionamiento();

			if (null != poliza.getDatosAval()) {
				es.agroseguro.contratacion.Fraccionamiento fraccionamiento;
				fraccionamiento = getFraccionamiento(poliza, cp, poliza.getDatosAval().getImporteAval(),
						poliza.getDatosAval().getNumeroAval());

				pago.setFraccionamiento(fraccionamiento);
				pago.setCuenta(obtenerCuenta(poliza, true));// En pago fraccionado nos devuelve error el servicio si no
															// informamos de la cuenta
				/* MODIF TAM (31.10.2018) */
				pago.getCuenta().setDestinatario("A");
			}

		}

		/* (13.07.2020) */
		logger.debug("Valor de pago.domiciliado: " + pago.getDomiciliado());
		logger.debug("Valor de pago.banco: " + pago.getBanco());
		logger.debug("Valor de pago.fecha: " + pago.getFecha());

		if (pago.getDomiciliado() != null) {
			if (pago.getDomiciliado().equals("T")) {
				logger.debug("Entramos en el if");
				if (pago.getBanco() != null) {
					logger.debug("Entramos en el unsetBanco");
					pago.unsetBanco();
				}
				if (pago.getFecha() != null) {
					logger.debug("Entramos en el unsetFecha");
					pago.unsetFecha();
				}

			}
		}
		/* (13.07.2020) Fin */
		return pago;
	}

	private static es.agroseguro.contratacion.costePoliza.CostePoliza getCosteCpl(
			com.rsi.agp.dao.tables.poliza.Poliza poliza, List<GruposNegocio> gruposNegocio, Long idComparativa,
			ComparativaPoliza cp) {
		CostePoliza coste = CostePoliza.Factory.newInstance();
		Set<DistribucionCoste2015> distCostesSet = poliza.getDistribucionCoste2015s();

		List<CosteGrupoNegocio> costeGrupoCol = new ArrayList<CosteGrupoNegocio>(distCostesSet.size());
		BigDecimal totalCosteTomador = BigDecimal.valueOf(0);

		boolean moduloCorrecto = false;

		BigDecimal recargoAval = BigDecimal.valueOf(0);
		BigDecimal recargoFraccionamiento = BigDecimal.valueOf(0);

		/** COSTES PARA COMPLEMENTARIAS DE AGRICOLAS **/
		CosteGrupoNegocio costeGrupo = CosteGrupoNegocio.Factory.newInstance();

		BonificacionRecargo bonifRecargo;
		Set<BonificacionRecargo2015> bonifRecargoSet;
		List<BonificacionRecargo> bonifRecargoCol;

		for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
			// si el mio de la comparativa es diferente al que viene en la distrib. de
			// costes se pone una distrib. por defecto.
			moduloCorrecto = true;
			costeGrupo.setPrimaComercial(dc.getPrimacomercial());
			costeGrupo.setPrimaComercialNeta(dc.getPrimacomercialneta());
			costeGrupo.setCosteTomador(dc.getCostetomador());
			costeGrupo.setReciboPrima(dc.getReciboprima());
			costeGrupo.setRecargoConsorcio(dc.getRecargoconsorcio());

			/* Pet. 57626 ** MODIF TAM (10/07/2020) ** Inicio ** Resolucion Incidencia **/
			costeGrupo.setGrupoNegocio(dc.getGrupoNegocio().toString());

			if (dc.getPeriodoFracc() != null) {
				totalCosteTomador = dc.getTotalcostetomador();
			} else {
				totalCosteTomador = dc.getCostetomador();

			}
			// Lo comentamos por ahora.
			if (dc.getRecargoaval() != null)
				recargoAval = dc.getRecargoaval();
			if (dc.getRecargofraccionamiento() != null)
				recargoFraccionamiento = dc.getRecargofraccionamiento();

			if (dc.getBonificacionRecargo2015s() != null && dc.getBonificacionRecargo2015s().size() > 0) {

				bonifRecargoSet = dc.getBonificacionRecargo2015s();
				bonifRecargoCol = new ArrayList<BonificacionRecargo>(bonifRecargoSet.size());
				for (BonificacionRecargo2015 bonifRecargo2015 : bonifRecargoSet) {
					bonifRecargo = BonificacionRecargo.Factory.newInstance();
					bonifRecargo.setCodigo(bonifRecargo2015.getCodigo().intValue());
					bonifRecargo.setImporte(bonifRecargo2015.getImporte());
					bonifRecargoCol.add(bonifRecargo);
				}
				costeGrupo.setBonificacionRecargoArray(bonifRecargoCol.toArray(new BonificacionRecargo[] {}));

			}

			// Se crea el mapa para agrupar el importe de las subvenciones por codigo de
			// organismo
			Map<Character, BigDecimal> mapaCCAA = new HashMap<Character, BigDecimal>();

			for (DistCosteSubvencion2015 dcs : dc.getDistCosteSubvencion2015s()) {
				if (dcs.getCodorganismo().equals(new Character('0'))) {
					es.agroseguro.contratacion.costePoliza.SubvencionEnesa se = es.agroseguro.contratacion.costePoliza.SubvencionEnesa.Factory
							.newInstance();
					se.setTipo(dcs.getCodtiposubv().intValue());
					if (dcs.getImportesubv() != null)
						se.setImporte(dcs.getImportesubv());
					else
						se.setImporte(new BigDecimal(0));

					List<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstSE = Arrays
							.asList(costeGrupo.getSubvencionEnesaArray());
					ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa> lstSEA = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionEnesa>(
							lstSE);
					lstSEA.add(se);

					costeGrupo.setSubvencionEnesaArray((es.agroseguro.contratacion.costePoliza.SubvencionEnesa[]) lstSEA
							.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionEnesa[lstSEA.size()]));
				} else {
					// Comprueba si ya existe subvencion para el organismo
					// en el mapa
					if (mapaCCAA.containsKey(dcs.getCodorganismo())) {
						// Se suma el importe de la subvencion actual al
						// guardado para este organismo
						mapaCCAA.put(dcs.getCodorganismo(), mapaCCAA.get(dcs.getCodorganismo())
								.add((dcs.getImportesubv() != null ? dcs.getImportesubv() : new BigDecimal(0))));
					} else {
						// Organismo nuevo, se insertan los datos en el mapa
						// Si el importe de la subvencion es nulo se inserta
						// un 0
						mapaCCAA.put(dcs.getCodorganismo(),
								(dcs.getImportesubv() != null ? dcs.getImportesubv() : new BigDecimal(0)));
					}

				}
			}

			// Se Anade el listado de subvenciones CCAA a la distribucion de
			// costes
			// Recorre las claves del mapa, crea el objeto SubvencionCCAA
			// correpondiente y lo Anade al listado
			List<es.agroseguro.contratacion.costePoliza.SubvencionCCAA> listSubvAux = new ArrayList<es.agroseguro.contratacion.costePoliza.SubvencionCCAA>();
			for (Character codOrg : mapaCCAA.keySet()) {
				es.agroseguro.contratacion.costePoliza.SubvencionCCAA s = es.agroseguro.contratacion.costePoliza.SubvencionCCAA.Factory
						.newInstance();
				s.setCodigoOrganismo(codOrg.toString());
				s.setImporte(mapaCCAA.get(codOrg));

				listSubvAux.add(s);
			}

			// Convierte el listado en un array y lo Anade al objeto de distribucion de
			// costes
			costeGrupo.setSubvencionCCAAArray(
					listSubvAux.toArray(new es.agroseguro.contratacion.costePoliza.SubvencionCCAA[listSubvAux.size()]));

			// Paramos porque solo deberia haber un registro en la base de datos
			break;
		} // fin bucle for distrb2015

		// En caso de que la poliza no tenga distribucion de costes, hay que
		// poner una por defecto
		if (!moduloCorrecto
				|| (poliza.getDistribucionCoste2015s() == null || poliza.getDistribucionCoste2015s().size() == 0)) {
			costeGrupo = cargaDistribucionCoste1Default_old();

			costeGrupoCol.add(costeGrupo);
			coste.setCosteGrupoNegocioArray(costeGrupoCol.toArray(new CosteGrupoNegocio[] {}));

		} else {

			costeGrupoCol.add(costeGrupo);
			coste.setCosteGrupoNegocioArray(costeGrupoCol.toArray(new CosteGrupoNegocio[] {}));

			es.agroseguro.contratacion.costePoliza.Financiacion fin_agr = es.agroseguro.contratacion.costePoliza.Financiacion.Factory
					.newInstance();

			fin_agr.setRecargoAval(recargoAval);
			fin_agr.setRecargoFraccionamiento(recargoFraccionamiento);
			BigDecimal nullBigDecimal = new BigDecimal(0);

			if (fin_agr != null && fin_agr.getRecargoAval() != null && fin_agr.getRecargoAval() != nullBigDecimal
					&& fin_agr.getRecargoFraccionamiento() != null
					&& fin_agr.getRecargoFraccionamiento() != nullBigDecimal) {
				coste.setFinanciacion(fin_agr);
			}

		}

		// Financiacion
		if (poliza.getEsFinanciada().equals('S')) {
			es.agroseguro.contratacion.costePoliza.Financiacion financiacion = es.agroseguro.contratacion.costePoliza.Financiacion.Factory
					.newInstance();
			getFinanciacion(financiacion, distCostesSet);
			if (financiacion != null && financiacion.getRecargoAval() != null
					&& financiacion.getRecargoFraccionamiento() != null)
				coste.setFinanciacion(financiacion);
		}

		coste.setTotalCosteTomador(totalCosteTomador);
		return coste;
	}

	/* Pet. 57626 ** MODIF TAM (29/04/2020) ** Fin */

	/* Pet.50776_63485-Fase II ** MODIF TAM (20.10.2020) ** Inicio */
	public static es.agroseguro.presupuestoContratacion.PolizaDocument transformarPolizaModulosYCoberturasAgri(
			final com.rsi.agp.dao.tables.poliza.Poliza poliza, final List<BigDecimal> codsConceptos,
			final GenericDao<?> genericDao, final com.rsi.agp.dao.tables.poliza.Parcela parc, final String codModulo,
			final List<GruposNegocio> gruposNegocio, final Map<Long, DatosVariables> dvEspecialesExplot)
			throws ValidacionPolizaException {

		es.agroseguro.presupuestoContratacion.PolizaDocument polizaDoc = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();
		es.agroseguro.presupuestoContratacion.Poliza polizaAS = es.agroseguro.presupuestoContratacion.Poliza.Factory
				.newInstance();
		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		boolean esGanado = poliza.getLinea().isLineaGanado();

		if (!StringUtils.nullToString(poliza.getReferencia()).equals(VACIO)) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!"303".equals(poliza.getLinea().getCodlinea().toString())) {
			// COLECTIVO
			es.agroseguro.presupuestoContratacion.Colectivo c = es.agroseguro.presupuestoContratacion.Colectivo.Factory
					.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			es.agroseguro.presupuestoContratacion.Tomador tomador = es.agroseguro.presupuestoContratacion.Tomador.Factory
					.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);
			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);
			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(VACIO)) {
				dc.setEmail(tomadorPoliza.getEmail());
			}
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(VACIO)) {
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			}
			tomador.setDatosContacto(dc);
			polizaAS.setTomador(tomador);
		}

		// ASEGURADO
		es.agroseguro.presupuestoContratacion.Asegurado a = es.agroseguro.presupuestoContratacion.Asegurado.Factory
				.newInstance();
		a.setNif(poliza.getAsegurado().getNifcif());
		if (poliza.getAsegurado().getTipoidentificacion().equals("CIF")) {
			RazonSocial rs1 = RazonSocial.Factory.newInstance();
			rs1.setRazonSocial(poliza.getAsegurado().getRazonsocial());
			a.setRazonSocial(rs1);
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(poliza.getAsegurado().getNombre());
			nom.setApellido1(poliza.getAsegurado().getApellido1());
			if (!"".equals(StringUtils.nullToString(poliza.getAsegurado().getApellido2()).trim()))
				nom.setApellido2(poliza.getAsegurado().getApellido2());
			a.setNombreApellidos(nom);
		}
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(poliza.getAsegurado().getBloque());
		dir.setCp(poliza.getAsegurado().getCodpostalstr());
		dir.setEscalera(poliza.getAsegurado().getEscalera());
		if (poliza.getAsegurado().getLocalidad().getNomlocalidad().length() > 30) {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad().substring(0, 30));
		} else {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad());
		}
		dir.setNumero(poliza.getAsegurado().getNumvia());
		dir.setPiso(poliza.getAsegurado().getPiso());
		dir.setProvincia(poliza.getAsegurado().getLocalidad().getProvincia().getCodprovincia().intValue());
		dir.setVia(
				poliza.getAsegurado().getVia().getClave() + " " + poliza.getAsegurado().getDireccion().toUpperCase());
		a.setDireccion(dir);
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(poliza.getAsegurado().getEmail());
		dContacto.setTelefonoFijo(Integer.parseInt(poliza.getAsegurado().getTelefono()));
		if (!StringUtils.nullToString(poliza.getAsegurado().getMovil()).equals(VACIO)) {
			dContacto.setTelefonoMovil(Integer.parseInt(poliza.getAsegurado().getMovil()));
		}
		a.setDatosContacto(dContacto);
		polizaAS.setAsegurado(a);
		// FIN ASEGURADO

		// ENTIDAD
		es.agroseguro.presupuestoContratacion.Entidad entidad = es.agroseguro.presupuestoContratacion.Entidad.Factory
				.newInstance();
		entidad.setCodigo(Constants.ENTIDAD_C616);
		entidad.setCodigoInterno(PolizaTransformer.getCodigoInterno(poliza));

		if (poliza.getColectivo().getSubentidadMediadora() != null) {
			es.agroseguro.presupuestoContratacion.Mediador mediador = es.agroseguro.presupuestoContratacion.Mediador.Factory
					.newInstance();
			// DAA 18/12/2013 TIPO MEDIADOR
			SubentidadMediadora subentidad = poliza.getColectivo().getSubentidadMediadora();
			try {
				if (subentidad != null && subentidad.getTipoMediadorAgro() != null
						&& subentidad.getTipoMediadorAgro().getId() != null) {
					mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
				}
			} catch (Exception e) {
				mediador.setTipo(1);
			}
			mediador.setRetribucionAsegurado(SiNo.NO);
			mediador.setImporteRetribucion(new BigDecimal(0));
			// DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			if (subentidad.getTipoidentificacion().equals("CIF")) {
				RazonSocial rs2 = RazonSocial.Factory.newInstance();
				rs2.setRazonSocial(subentidad.getNomsubentidad());
				mediador.setRazonSocial(rs2);
			} else {
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
				nom.setNombre(subentidad.getNombre());
				nom.setApellido1(subentidad.getApellido1());
				nom.setApellido2(subentidad.getApellido2());
				mediador.setNombreApellidos(nom);
			}
			entidad.setMediador(mediador);
		}

		// ********************** //
		// AMG GASTOS 28/10/2014 //
		// ********************** //
		Gastos[] gastos = getGastos(gruposNegocio, poliza.getSetPolizaPctComisiones(), esGanado, poliza, false, null);
		entidad.setGastosArray(gastos);
		// FIN GASTOS

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// COBERTURAS
		es.agroseguro.presupuestoContratacion.Cobertura cobertura = es.agroseguro.presupuestoContratacion.Cobertura.Factory
				.newInstance();
		cobertura.setModulo(String.format("%-5s", codModulo));
		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS

		// ********************//
		// OBJETOS ASEGURADOS //
		// ********************//
		es.agroseguro.presupuestoContratacion.ObjetosAsegurados objAseg = es.agroseguro.presupuestoContratacion.ObjetosAsegurados.Factory
				.newInstance();
		org.w3c.dom.Node importedNode;

		if (null != parc) {

			List<ParcelaDocument> parcelasCol = getParcelasDatos(parc, codModulo, codsConceptos);

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);
			}
		} else {
			// PARCELAS
			List<ParcelaDocument> parcelasCol = getParcelasModyCob(poliza, codModulo, codsConceptos);

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);

			}
		}

		polizaAS.setObjetosAsegurados(objAseg);
		// FIN OBJETOS ASEGURADOS

		polizaDoc.setPoliza(polizaAS);
		return polizaDoc;
	}

	/**
	 * Metodo para obtener un array de parcelas para enviar a Agroseguro a partir de
	 * una coleccion de parcelas de poliza
	 * 
	 * @param poliza
	 *            Poliza de la aplicacion
	 * @param cp
	 *            Comparativa actual.
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionPolizaException
	 */
	private static List<ParcelaDocument> getParcelasDatos(com.rsi.agp.dao.tables.poliza.Parcela parcela,
			String codModulo, List<BigDecimal> codsConceptos) throws ValidacionPolizaException {

		ParcelaDocument pd;
		Parcela p;
		List<ParcelaDocument> parc = new ArrayList<ParcelaDocument>();

		/* Tratamos la parcela que nos llega por parametro */
		pd = ParcelaDocument.Factory.newInstance();
		p = Parcela.Factory.newInstance();

		// El envio ha de hacerse con hoja y numero rellenos
		if (parcela.getHoja() == null || parcela.getNumero() == null) {
			p.setHoja(1);
			p.setNumero(1);
		} else {
			p.setHoja(parcela.getHoja().intValue());
			p.setNumero(parcela.getNumero().intValue());
		}

		p.setNombre(parcela.getNomparcela());
		
		/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
		/* Incluir el nuevo atributo 'parcelaAgricola' en la etiqueta 'Parcela' */
		/* DE MOMENTO Y POR PETICIÓN DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENVÍA EN NINGÚN XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
		/*if (parcela.getParcAgricola() != null) {
			p.setParcelaAgricola(parcela.getParcAgricola());	
		}*/
		/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
		
		if (parcela.getCodprovsigpac() != null) {
			// rellenamos el sigpac
			SIGPAC sigpac = SIGPAC.Factory.newInstance();
			sigpac.setAgregado(parcela.getAgrsigpac().intValue());
			sigpac.setParcela(parcela.getParcelasigpac().intValue());
			sigpac.setPoligono(parcela.getPoligonosigpac().intValue());
			sigpac.setProvincia(parcela.getCodprovsigpac().intValue());
			sigpac.setRecinto(parcela.getRecintosigpac().intValue());
			sigpac.setTermino(parcela.getCodtermsigpac().intValue());
			sigpac.setZona(parcela.getZonasigpac().intValue());
			p.setSIGPAC(sigpac);
		}
		Ambito ambito = Ambito.Factory.newInstance();
		ambito.setComarca(parcela.getTermino().getId().getCodcomarca().intValue());
		ambito.setProvincia(parcela.getTermino().getId().getCodprovincia().intValue());
		ambito.setSubtermino(parcela.getTermino().getId().getSubtermino() + VACIO);
		ambito.setTermino(parcela.getTermino().getId().getCodtermino().intValue());
		
		/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
		/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
		if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
			p.setUbicacion(ambito);
		}
		/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
		
		// Cosecha
		Cosecha cosecha = Cosecha.Factory.newInstance();
		cosecha.setCultivo(parcela.getCodcultivo().intValue());
		cosecha.setVariedad(parcela.getCodvariedad().intValue());
		CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
		es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = new es.agroseguro.contratacion.parcela.CapitalAsegurado[parcela
				.getCapitalAsegurados().size()];
		int cntCapAseg = 0;
		for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
			es.agroseguro.contratacion.parcela.CapitalAsegurado c = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
					.newInstance();
			// DE MOMENTO EN TB_CAP_ASEG_REL_MODULO SOLO SE ESTA
			// GUARDANDO UN VALOR INDEPENDIENTEMENTE
			// DE LOS POSIBLES MODULOS SELECCIONADOS, ASI QUE ME QUEDO
			// CON EL PRIMERO Y LISTO
			// Hace un set del precio y producci?n aun cuando la
			// coleccion no tiene nada
			Set<CapAsegRelModulo> colCapAsegRelModulo = ca.getCapAsegRelModulos();
			if (ca.getCapAsegRelModulos().size() > 0) {
				for (CapAsegRelModulo care : colCapAsegRelModulo) {
					if (care.getCodmodulo().equals(codModulo)) {
						if (care.getPreciomodif() != null) {
							c.setPrecio(care.getPreciomodif());
						} else {
							if (care.getPrecio() != null) {
								c.setPrecio(care.getPrecio());
							} else {
								c.setPrecio(new BigDecimal(0));
							}
						}
						if (care.getProduccionmodif() != null) {
							c.setProduccion(care.getProduccionmodif().intValue());
						} else {
							c.setProduccion(care.getProduccion().intValue());
						}
						logger.debug("P000018845: BREAK");
						break;
					} else {
						// P000018845: Si con todo lo anterior no se ha
						// incluido el precio, lo pondremos a cero para
						// que no falle la validaci?n para el env?o
						logger.debug("P000018845: Precio/producci?n = 0");
						c.setPrecio(new BigDecimal(0));
						c.setProduccion(0);
					}
				}
			} else {
				if (ca.getPrecio() != null) {
					c.setPrecio(ca.getPrecio());
				} else {
					c.setPrecio(new BigDecimal(0));
				}
				if (ca.getProduccion() != null) {
					c.setProduccion(ca.getProduccion().intValue());
				} else {
					c.setProduccion(0);
				}
			}
			c.setSuperficie(ca.getSuperficie());
			c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
			// Obtenemos los datos variables de parcela
			DatosVariables datosVariables = DatosVariables.Factory.newInstance();
			logger.debug("codsConceptos: " + codsConceptos);
			Set<DatoVariableParcela> dvSet = ca.getDatoVariableParcelas();
			for (DatoVariableParcela dvp : dvSet) {
				logger.debug("Tratando dv: " + dvp.getDiccionarioDatos().getCodconcepto());
				if (codsConceptos.contains(dvp.getDiccionarioDatos().getCodconcepto())) {
					logger.debug("valor de getValor(): -" + dvp.getValor() + "-");
					if (dvp.getValor() != null && dvp.getValor() != "") {
						try {
							String valorLimpio = dvp.getValor().replace("[", "").replace("]", "").replace("\"", "").replace(",", " ");
							setDatoVariable(dvp.getDiccionarioDatos().getCodconcepto(), null, null, valorLimpio,
									datosVariables, false);
						} catch (NumberFormatException e) {
							throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
						} catch (ParseException e) {
							throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getDiccionarioDatos().getNomconcepto()));
						}
					}
				}
			}
			c.setDatosVariables(datosVariables);
			capitales[cntCapAseg] = c;
			cntCapAseg++;
		}
		capAseg.setCapitalAseguradoArray(capitales);
		cosecha.setCapitalesAsegurados(capAseg);
		p.setCosecha(cosecha);
		pd.setParcela(p);
		parc.add(pd);

		return parc;
	}

	public static es.agroseguro.presupuestoContratacion.PolizaDocument transformarPolizaModulosYCoberturasAgriAnx(
			final com.rsi.agp.dao.tables.poliza.Poliza poliza, final List<BigDecimal> codsConceptos,
			final GenericDao<?> genericDao, final com.rsi.agp.dao.tables.poliza.Parcela parc,
			final com.rsi.agp.dao.tables.anexo.Parcela parcAnexo,
			final Set<com.rsi.agp.dao.tables.anexo.Parcela> listaparcAnexo, final String codModulo,
			final List<GruposNegocio> gruposNegocio, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException {

		es.agroseguro.presupuestoContratacion.PolizaDocument polizaDoc = es.agroseguro.presupuestoContratacion.PolizaDocument.Factory
				.newInstance();
		es.agroseguro.presupuestoContratacion.Poliza polizaAS = es.agroseguro.presupuestoContratacion.Poliza.Factory
				.newInstance();
		polizaAS.setPlan(poliza.getLinea().getCodplan().intValue());
		polizaAS.setLinea(poliza.getLinea().getCodlinea().intValue());
		polizaAS.setFechaFirmaSeguro(new GregorianCalendar());

		boolean esGanado = poliza.getLinea().isLineaGanado();

		if (!StringUtils.nullToString(poliza.getReferencia()).equals(VACIO)) {
			polizaAS.setReferencia(poliza.getReferencia());
			polizaAS.setDigitoControl(poliza.getDc().intValue());
		}
		// En la linea 303 en lugar de los datos del colectivo se pone la CCC.
		if (!"303".equals(poliza.getLinea().getCodlinea().toString())) {
			// COLECTIVO
			es.agroseguro.presupuestoContratacion.Colectivo c = es.agroseguro.presupuestoContratacion.Colectivo.Factory
					.newInstance();
			c.setDigitoControl(Integer.parseInt(poliza.getColectivo().getDc()));
			c.setNif(poliza.getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(poliza.getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			// FIN COLECTIVO
		} else {
			// TOMADOR
			es.agroseguro.presupuestoContratacion.Tomador tomador = es.agroseguro.presupuestoContratacion.Tomador.Factory
					.newInstance();
			com.rsi.agp.dao.tables.admin.Tomador tomadorPoliza = poliza.getColectivo().getTomador();
			tomador.setNif(tomadorPoliza.getId().getCiftomador());
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(tomadorPoliza.getRazonsocial());
			tomador.setRazonSocial(rs);
			Direccion direccion = Direccion.Factory.newInstance();
			direccion.setBloque(tomadorPoliza.getBloque());
			direccion.setCp(tomadorPoliza.getCodpostalstr());
			direccion.setEscalera(tomadorPoliza.getEscalera());
			direccion.setLocalidad(tomadorPoliza.getLocalidad().getNomlocalidad());
			direccion.setNumero(tomadorPoliza.getNumvia());
			direccion.setPiso(tomadorPoliza.getPiso());
			direccion.setProvincia(tomadorPoliza.getLocalidad().getId().getCodprovincia().intValue());
			direccion.setVia(tomadorPoliza.getVia().getClave() + " " + tomadorPoliza.getDomicilio().toUpperCase());
			tomador.setDireccion(direccion);
			DatosContacto dc = DatosContacto.Factory.newInstance();
			dc.setTelefonoFijo(Integer.parseInt(tomadorPoliza.getTelefono()));
			if (!StringUtils.nullToString(tomadorPoliza.getEmail()).equals(VACIO)) {
				dc.setEmail(tomadorPoliza.getEmail());
			}
			if (!StringUtils.nullToString(tomadorPoliza.getMovil()).equals(VACIO)) {
				dc.setTelefonoMovil(Integer.parseInt(tomadorPoliza.getMovil()));
			}
			tomador.setDatosContacto(dc);
			polizaAS.setTomador(tomador);
		}

		// ASEGURADO
		es.agroseguro.presupuestoContratacion.Asegurado a = es.agroseguro.presupuestoContratacion.Asegurado.Factory
				.newInstance();
		a.setNif(poliza.getAsegurado().getNifcif());
		if (poliza.getAsegurado().getTipoidentificacion().equals("CIF")) {
			RazonSocial rs1 = RazonSocial.Factory.newInstance();
			rs1.setRazonSocial(poliza.getAsegurado().getRazonsocial());
			a.setRazonSocial(rs1);
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(poliza.getAsegurado().getNombre());
			nom.setApellido1(poliza.getAsegurado().getApellido1());
			if (!"".equals(StringUtils.nullToString(poliza.getAsegurado().getApellido2()).trim()))
				nom.setApellido2(poliza.getAsegurado().getApellido2());
			a.setNombreApellidos(nom);
		}
		Direccion dir = Direccion.Factory.newInstance();
		dir.setBloque(poliza.getAsegurado().getBloque());
		dir.setCp(poliza.getAsegurado().getCodpostalstr());
		dir.setEscalera(poliza.getAsegurado().getEscalera());
		if (poliza.getAsegurado().getLocalidad().getNomlocalidad().length() > 30) {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad().substring(0, 30));
		} else {
			dir.setLocalidad(poliza.getAsegurado().getLocalidad().getNomlocalidad());
		}
		dir.setNumero(poliza.getAsegurado().getNumvia());
		dir.setPiso(poliza.getAsegurado().getPiso());
		dir.setProvincia(poliza.getAsegurado().getLocalidad().getProvincia().getCodprovincia().intValue());
		dir.setVia(
				poliza.getAsegurado().getVia().getClave() + " " + poliza.getAsegurado().getDireccion().toUpperCase());
		a.setDireccion(dir);
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		dContacto.setEmail(poliza.getAsegurado().getEmail());
		dContacto.setTelefonoFijo(Integer.parseInt(poliza.getAsegurado().getTelefono()));
		if (!StringUtils.nullToString(poliza.getAsegurado().getMovil()).equals(VACIO)) {
			dContacto.setTelefonoMovil(Integer.parseInt(poliza.getAsegurado().getMovil()));
		}
		a.setDatosContacto(dContacto);
		polizaAS.setAsegurado(a);
		// FIN ASEGURADO

		// ENTIDAD
		es.agroseguro.presupuestoContratacion.Entidad entidad = es.agroseguro.presupuestoContratacion.Entidad.Factory
				.newInstance();
		entidad.setCodigo(Constants.ENTIDAD_C616);
		entidad.setCodigoInterno(PolizaTransformer.getCodigoInterno(poliza));

		if (poliza.getColectivo().getSubentidadMediadora() != null) {
			es.agroseguro.presupuestoContratacion.Mediador mediador = es.agroseguro.presupuestoContratacion.Mediador.Factory
					.newInstance();
			// DAA 18/12/2013 TIPO MEDIADOR
			SubentidadMediadora subentidad = poliza.getColectivo().getSubentidadMediadora();
			try {
				if (subentidad != null && subentidad.getTipoMediadorAgro() != null
						&& subentidad.getTipoMediadorAgro().getId() != null) {
					mediador.setTipo(subentidad.getTipoMediadorAgro().getId().intValue());
				}
			} catch (Exception e) {
				mediador.setTipo(1);
			}
			mediador.setRetribucionAsegurado(SiNo.NO);
			mediador.setImporteRetribucion(new BigDecimal(0));
			// DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			if (subentidad.getTipoidentificacion().equals("CIF")) {
				RazonSocial rs2 = RazonSocial.Factory.newInstance();
				rs2.setRazonSocial(subentidad.getNomsubentidad());
				mediador.setRazonSocial(rs2);
			} else {
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
				nom.setNombre(subentidad.getNombre());
				nom.setApellido1(subentidad.getApellido1());
				nom.setApellido2(subentidad.getApellido2());
				mediador.setNombreApellidos(nom);
			}
			entidad.setMediador(mediador);
		}

		// ********************** //
		// AMG GASTOS 28/10/2014 //
		// ********************** //
		Gastos[] gastos = getGastos(gruposNegocio, poliza.getSetPolizaPctComisiones(), esGanado, poliza, false, null);
		entidad.setGastosArray(gastos);
		// FIN GASTOS

		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// COBERTURAS
		es.agroseguro.presupuestoContratacion.Cobertura cobertura = es.agroseguro.presupuestoContratacion.Cobertura.Factory
				.newInstance();
		cobertura.setModulo(String.format("%-5s", codModulo));
		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS

		// ********************//
		// OBJETOS ASEGURADOS //
		// ********************//
		es.agroseguro.presupuestoContratacion.ObjetosAsegurados objAseg = es.agroseguro.presupuestoContratacion.ObjetosAsegurados.Factory
				.newInstance();
		org.w3c.dom.Node importedNode;

		if (null != parc) {
			Set<com.rsi.agp.dao.tables.poliza.Parcela> setParc = new HashSet<com.rsi.agp.dao.tables.poliza.Parcela>();
			setParc.add(parc);
			List<ParcelaDocument> parcelasCol = getParcelasModyCob(poliza, codModulo, codsConceptos);

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);
			}

		} else if (null != parcAnexo) {
			Set<com.rsi.agp.dao.tables.anexo.Parcela> setParcAnexo = new HashSet<com.rsi.agp.dao.tables.anexo.Parcela>();
			setParcAnexo.add(parcAnexo);

			List<ParcelaDocument> parcelasCol = new ArrayList<ParcelaDocument>();
			ParcelaDocument parcel = parcelaAnexoToParcelaAgr(parcAnexo, listaDatosVariables);
			parcelasCol.add(parcel);

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);
			}
		} else if (listaparcAnexo != null && !listaparcAnexo.isEmpty()) {

			List<ParcelaDocument> parcelasCol = new ArrayList<ParcelaDocument>();

			for (com.rsi.agp.dao.tables.anexo.Parcela ParcA : listaparcAnexo) {
				
				/* ESC-14587 ** MODIF TAM (15.07.2021) ** Inicio */
				/* no se incluyen las parcelas que estï¿½n dadas de baja */
				if (ParcA.getTipomodificacion()!= null) {
					if (!ParcA.getTipomodificacion().equals('B')){
						ParcelaDocument parcel = parcelaAnexoToParcelaAgr(ParcA, listaDatosVariables);
						parcelasCol.add(parcel);
					}
				}else {
					ParcelaDocument parcel = parcelaAnexoToParcelaAgr(ParcA, listaDatosVariables);
					parcelasCol.add(parcel);
				}
				/* ESC-14587 ** MODIF TAM (15.07.2021) ** Fin */
			}

			for (ParcelaDocument parcelaDoc : parcelasCol) {
				importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
						true);
				objAseg.getDomNode().appendChild(importedNode);
			}
		}

		polizaAS.setObjetosAsegurados(objAseg);
		// FIN OBJETOS ASEGURADOS

		polizaDoc.setPoliza(polizaAS);
		return polizaDoc;
	}

	/**
	 * Transforma una parcela de anexo en una parcela de contrataciï¿½n
	 * 
	 * @param parcelaAnexo
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static es.agroseguro.contratacion.parcela.ParcelaDocument parcelaAnexoToParcelaAgr(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException {

		es.agroseguro.contratacion.parcela.ParcelaDocument newParcela = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory
				.newInstance();
		es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela p = es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela.Factory
				.newInstance();

		if (parcelaAnexo.getHoja() == null) {
			p.setHoja(1);
		} else {
			p.setHoja(parcelaAnexo.getHoja().intValue());
		}

		// El envio ha de hacerse con hoja y numero rellenos
		if (parcelaAnexo.getNumero() == null) {
			p.setNumero(1);
		} else {
			p.setNumero(parcelaAnexo.getNumero().intValue());
		}

		p.setNombre(parcelaAnexo.getNomparcela());
		
		/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
		/* Incluir el nuevo atributo 'parcelaAgricola' en la etiqueta 'Parcela' */
		/* DE MOMENTO Y POR PETICIÓN DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENVÍA EN NINGÚN XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
		/*if (parcelaAnexo.getParcAgricola() != null) {
			p.setParcelaAgricola(parcelaAnexo.getParcAgricola());	
		}*/
		/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */

		// Si la parcela tiene SIGPAC
		if (parcelaAnexo.getCodprovsigpac() != null)
			p.setSIGPAC(getSIGPAC(parcelaAnexo));
		// Si tiene Identificaciï¿½n Catastral
		// else newParcela.setIdentificacionCatastral(getIdentCatastral(parcelaAnexo));

		// Establece la ubicacion
		
		/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
		/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
		Ambito ambito = Ambito.Factory.newInstance();
		
		ambito = getAmbito(parcelaAnexo);
		if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
			p.setUbicacion(ambito);
		}
		/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */

		// Establece la cosecha de la parcela
		p.setCosecha(getCosecha(parcelaAnexo, listaDatosVariables));
		newParcela.setParcela(p);

		return newParcela;
	}

	/**
	 * Devuelve el objeto SIGPAC asociado a la parcela del anexo
	 * 
	 * @param parcelaAnexo
	 * @return
	 */
	protected static SIGPAC getSIGPAC(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		SIGPAC sigpac = SIGPAC.Factory.newInstance();
		sigpac.setAgregado(parcelaAnexo.getAgrsigpac().intValue());
		sigpac.setParcela(parcelaAnexo.getParcelasigpac().intValue());
		sigpac.setPoligono(parcelaAnexo.getPoligonosigpac().intValue());
		sigpac.setProvincia(parcelaAnexo.getCodprovsigpac().intValue());
		sigpac.setRecinto(parcelaAnexo.getRecintosigpac().intValue());
		sigpac.setTermino(parcelaAnexo.getCodtermsigpac().intValue());
		sigpac.setZona(parcelaAnexo.getZonasigpac().intValue());
		return sigpac;
	}

	/**
	 * Devuelve la ubicaciÃ³n de la parcela del anexo
	 * 
	 * @param parcelaAnexo
	 * @return
	 */
	protected static Ambito getAmbito(com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		Ambito ambito = Ambito.Factory.newInstance();
		ambito.setComarca(parcelaAnexo.getCodcomarca().intValue());
		ambito.setProvincia(parcelaAnexo.getCodprovincia().intValue());
		ambito.setSubtermino(parcelaAnexo.getSubtermino() + "");
		ambito.setTermino(parcelaAnexo.getCodtermino().intValue());
		return ambito;
	}

	/**
	 * Obtiene la cosecha asociada a la parcela del anexo
	 * 
	 * @param parcelaAnexo
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static es.agroseguro.contratacion.parcela.Cosecha getCosecha(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException {
		es.agroseguro.contratacion.parcela.Cosecha cosecha = es.agroseguro.contratacion.parcela.Cosecha.Factory
				.newInstance();
		cosecha.setVariedad(parcelaAnexo.getCodvariedad().intValue());
		cosecha.setCultivo(parcelaAnexo.getCodcultivo().intValue());
		cosecha.setCapitalesAsegurados(getCapitalesAsegurados(parcelaAnexo, listaDatosVariables));
		return cosecha;
	}

	/**
	 * Devuelve los capitales asegurados de la parcela del anexo
	 * 
	 * @param parcelaAnexo
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static es.agroseguro.contratacion.parcela.CapitalesAsegurados getCapitalesAsegurados(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException {

		// Listado de capitales asegurados que se establecera en la poliza
		List<es.agroseguro.contratacion.parcela.CapitalAsegurado> listaCA = new ArrayList<es.agroseguro.contratacion.parcela.CapitalAsegurado>();
		es.agroseguro.contratacion.parcela.CapitalesAsegurados capitalesAsegurados = es.agroseguro.contratacion.parcela.CapitalesAsegurados.Factory
				.newInstance();

		// Recorre los capitales asegurados de la parcela del anexo
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado : parcelaAnexo.getCapitalAsegurados()) {
			// Lo transforma a CA de la poliza
			listaCA.add(caAnexoToCaPoliza(capitalAsegurado, listaDatosVariables));
		}

		capitalesAsegurados.setCapitalAseguradoArray((es.agroseguro.contratacion.parcela.CapitalAsegurado[]) listaCA
				.toArray(new es.agroseguro.contratacion.parcela.CapitalAsegurado[listaCA.size()]));

		return capitalesAsegurados;
	}

	/**
	 * Transforma el Capital Asegurado del anexo en un Capital Asegurado de la
	 * Pï¿½liza
	 * 
	 * @param caAnexo
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static es.agroseguro.contratacion.parcela.CapitalAsegurado caAnexoToCaPoliza(
			com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException {
		es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
				.newInstance();
		capitalAsegurado.setPrecio(caAnexo.getPrecio());
		capitalAsegurado.setProduccion(caAnexo.getProduccion().intValue());
		capitalAsegurado.setSuperficie(caAnexo.getSuperficie());
		capitalAsegurado.setTipo(caAnexo.getTipoCapital().getCodtipocapital().intValue());
		capitalAsegurado.setDatosVariables(getDatosVariables(caAnexo, listaDatosVariables));
		return capitalAsegurado;
	}

	/**
	 * Transforma los datos variables del capital asegurado de la parcela del anexo
	 * en un objeto DatoVariables de poliza
	 * 
	 * @param caAnexo
	 * @return
	 * @throws ValidacionPolizaException
	 */
	private static es.agroseguro.contratacion.datosVariables.DatosVariables getDatosVariables(
			com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo, Map<BigDecimal, List<String>> listaDatosVariables)
			throws ValidacionPolizaException {

		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory
				.newInstance();

		for (CapitalDTSVariable dvp : caAnexo.getCapitalDTSVariables()) {

			if (listaDatosVariables.containsKey(dvp.getCodconcepto())) {
				// Es un dato variable "particular"
				List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());

				for (String cad : auxDatVar) {
					String[] auxValores = cad.split("#");

					// Id de capital asegurado asociado al dv
					Long auxIdCapAseg = new Long(auxValores[0]);
					if (auxIdCapAseg.equals(dvp.getCapitalAsegurado().getId())) {
						try {
							setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),
									new BigDecimal(auxValores[2]), auxValores[3], datosVariables);
						} catch (ParseException e) {
							throw new ValidacionPolizaException(FEC_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getCodconcepto().toString()));
						} catch (NumberFormatException e) {
							throw new ValidacionPolizaException(NUM_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getCodconcepto().toString()));
						} catch (Exception e) {
							throw new ValidacionPolizaException(VAL_DATA_ERROR_MSG.replace("{valor}", dvp.getValor()).replace("{dato}", dvp.getCodconcepto().toString()));
						}

					}
				}
			} else {
				setDatosVariables(datosVariables, dvp);
			}

		}

		return datosVariables;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setDatosVariables(es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables,
			CapitalDTSVariable dvp) {

		String valor = dvp.getValor();
		SimpleDateFormat sdf = new SimpleDateFormat(FECHA);

		// Dependiendo del codigo de concepto del dato variable se establece de una
		// manera
		switch (dvp.getCodconcepto().intValue()) {
		case 362:
			// % CAPITAL ASEGURADO
			List<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> lstCA = Arrays
					.asList(datosVariables.getCapAsegArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> lstCAA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado>(
					lstCA);

			es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado capital = es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado.Factory
					.newInstance();
			capital.setCodRCub(dvp.getCodriesgocubierto().intValue());
			capital.setCPMod(dvp.getCodconceptoppalmod().intValue());
			capital.setValor(Integer.parseInt(valor.toString()));

			lstCAA.add(capital);

			datosVariables.setCapAsegArray(lstCAA
					.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[lstCAA.size()]));
			break;
		case 120:
			// % FRANQUICIA
			// Si no vienen informados el riesgo cubierto y el CPM no se inserta
			if (dvp.getCodriesgocubierto() == null || dvp.getCodconceptoppalmod() == null)
				break;
			List<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstF = Arrays
					.asList(datosVariables.getFranqArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstFA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia>(
					lstF);

			es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia f = es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia.Factory
					.newInstance();
			f.setCodRCub(dvp.getCodriesgocubierto().intValue());
			f.setCPMod(dvp.getCodconceptoppalmod().intValue());
			f.setValor(Integer.parseInt(valor.toString()));

			lstFA.add(f);

			datosVariables.setFranqArray(
					lstFA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[lstFA.size()]));
			break;
		case 121:
			// % MINIMO INDEMNIZABLE
			// Si no vienen informados el riesgo cubierto y el CPM no se inserta
			if (dvp.getCodriesgocubierto() == null || dvp.getCodconceptoppalmod() == null)
				break;
			List<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMI = Arrays
					.asList(datosVariables.getMinIndemArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable>(
					lstMI);

			es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable m = es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable.Factory
					.newInstance();
			m.setCodRCub(dvp.getCodriesgocubierto().intValue());
			m.setCPMod(dvp.getCodconceptoppalmod().intValue());
			m.setValor(Integer.parseInt(valor.toString()));

			lstMIA.add(m);

			datosVariables.setMinIndemArray(lstMIA.toArray(
					new es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[lstMIA.size()]));
			break;
		case 174:
			// CALCULO INDEMNIZACION
			List<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> lstCI = Arrays
					.asList(datosVariables.getCalcIndemArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> lstCIA = new ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion>(
					lstCI);

			es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c = es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion.Factory
					.newInstance();
			c.setCodRCub(dvp.getCodriesgocubierto().intValue());
			c.setCPMod(dvp.getCodconceptoppalmod().intValue());
			c.setValor(Integer.parseInt(valor.toString()));

			lstCIA.add(c);

			datosVariables.setCalcIndemArray(
					lstCIA.toArray(new es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[lstCIA.size()]));
			break;
		case 169:
			// DAï¿½OS CUBIERTOS
			List<es.agroseguro.contratacion.datosVariables.DaniosCubiertos> lstDNC = Arrays
					.asList(datosVariables.getDnCbtosArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.DaniosCubiertos> lstDNCA = new ArrayList<es.agroseguro.contratacion.datosVariables.DaniosCubiertos>(
					lstDNC);

			es.agroseguro.contratacion.datosVariables.DaniosCubiertos dan = es.agroseguro.contratacion.datosVariables.DaniosCubiertos.Factory
					.newInstance();
			dan.setCodRCub(dvp.getCodriesgocubierto().intValue());
			dan.setCPMod(dvp.getCodconceptoppalmod().intValue());
			dan.setValor(valor + "");

			lstDNCA.add(dan);

			datosVariables.setDnCbtosArray(
					lstDNCA.toArray(new es.agroseguro.contratacion.datosVariables.DaniosCubiertos[lstDNCA.size()]));
			break;
		case 175:
			// GARANTIZADO
			List<es.agroseguro.contratacion.datosVariables.Garantizado> lstG = Arrays
					.asList(datosVariables.getGarantArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado> lstGA = new ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado>(
					lstG);

			es.agroseguro.contratacion.datosVariables.Garantizado garant = es.agroseguro.contratacion.datosVariables.Garantizado.Factory
					.newInstance();
			garant.setCodRCub(dvp.getCodriesgocubierto().intValue());
			garant.setCPMod(dvp.getCodconceptoppalmod().intValue());
			garant.setValor(Integer.parseInt(valor.toString()));
			lstGA.add(garant);

			datosVariables.setGarantArray(
					lstGA.toArray(new es.agroseguro.contratacion.datosVariables.Garantizado[lstGA.size()]));
			break;
		case 363:
			// RIESGO CUBIERTO ELEGIDO
			List<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCE = Arrays
					.asList(datosVariables.getRiesgCbtoElegArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCEA = new ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido>(
					lstRCE);

			es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCubEleg = es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido.Factory
					.newInstance();
			rCubEleg.setCodRCub(dvp.getCodriesgocubierto().intValue());
			rCubEleg.setCPMod(dvp.getCodconceptoppalmod().intValue());

			// Ponemos la S y la N tambien para cuando venga del copy
			if (valor.equals("-1") || valor.equals("S"))
				rCubEleg.setValor("S");
			else
				rCubEleg.setValor("N");

			lstRCEA.add(rCubEleg);

			datosVariables.setRiesgCbtoElegArray(lstRCEA
					.toArray(new es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[lstRCEA.size()]));
			break;
		case 170:
			// TIPO FRANQUICIA
			List<es.agroseguro.contratacion.datosVariables.TipoFranquicia> lstTF = Arrays
					.asList(datosVariables.getTipFranqArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.TipoFranquicia> lstTFA = new ArrayList<es.agroseguro.contratacion.datosVariables.TipoFranquicia>(
					lstTF);

			es.agroseguro.contratacion.datosVariables.TipoFranquicia tipFranq = es.agroseguro.contratacion.datosVariables.TipoFranquicia.Factory
					.newInstance();
			tipFranq.setCodRCub(dvp.getCodriesgocubierto().intValue());
			tipFranq.setCPMod(dvp.getCodconceptoppalmod().intValue());
			tipFranq.setValor(valor + "");

			lstTFA.add(tipFranq);

			datosVariables.setTipFranqArray(
					lstTFA.toArray(new es.agroseguro.contratacion.datosVariables.TipoFranquicia[lstTFA.size()]));
			break;
		case 502:
			// TIPO RENDIMIENTO
			List<es.agroseguro.contratacion.datosVariables.TipoRendimiento> lstTR = Arrays
					.asList(datosVariables.getTipRdtoArray());
			ArrayList<es.agroseguro.contratacion.datosVariables.TipoRendimiento> lstTRA = new ArrayList<es.agroseguro.contratacion.datosVariables.TipoRendimiento>(
					lstTR);

			es.agroseguro.contratacion.datosVariables.TipoRendimiento tipRdto = es.agroseguro.contratacion.datosVariables.TipoRendimiento.Factory
					.newInstance();
			tipRdto.setCodRCub(dvp.getCodriesgocubierto().intValue());
			tipRdto.setCPMod(dvp.getCodconceptoppalmod().intValue());
			tipRdto.setValor(Integer.parseInt(valor.toString()));

			lstTRA.add(tipRdto);

			datosVariables.setTipRdtoArray(
					lstTRA.toArray(new es.agroseguro.contratacion.datosVariables.TipoRendimiento[lstTRA.size()]));
			break;
		// FIN DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL
		// RIESGO CUBIERTO

		// INICIO DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y
		// DEL RIESGO CUBIERTO
		case 144:
			// ALTERNATIVA
			es.agroseguro.contratacion.datosVariables.Rotacion alt = es.agroseguro.contratacion.datosVariables.Rotacion.Factory
					.newInstance();
			alt.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setRot(alt);
			break;
		case 106:
			// CARACT. EXPLOTACION
			es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion carExlp = es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion.Factory
					.newInstance();
			carExlp.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setCarExpl(carExlp);
			break;
		case 618:
			// CICLO CULTIVO
			es.agroseguro.contratacion.datosVariables.CicloCultivo ciCul = es.agroseguro.contratacion.datosVariables.CicloCultivo.Factory
					.newInstance();
			ciCul.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setCiCul(ciCul);
			break;
		case 765:
			// CODIGO IGP
			es.agroseguro.contratacion.datosVariables.CodigoIGP igp = es.agroseguro.contratacion.datosVariables.CodigoIGP.Factory
					.newInstance();
			igp.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setCodIGP(igp);
			break;
		case 620:
			// CODIGO REDUCCION RDTOS.
			List lista = new ArrayList();
			lista.add(valor);
			es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos redRdto = es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos.Factory
					.newInstance();
			redRdto.setValor(lista);

			datosVariables.setCodRedRdto(redRdto);
			break;
		case 107:
			// DENOMINACION ORIGEN
			es.agroseguro.contratacion.datosVariables.DenominacionOrigen denOrig = es.agroseguro.contratacion.datosVariables.DenominacionOrigen.Factory
					.newInstance();
			denOrig.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setCodDO(denOrig);
			break;
		case 109:
			// DENSIDAD
			es.agroseguro.contratacion.datosVariables.Densidad dens = es.agroseguro.contratacion.datosVariables.Densidad.Factory
					.newInstance();
			dens.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setDens(dens);
			break;
		case 110:
			// DESTINO
			es.agroseguro.contratacion.datosVariables.Destino dest = es.agroseguro.contratacion.datosVariables.Destino.Factory
					.newInstance();
			dest.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setDest(dest);
			break;
		case 111:
			// EDAD
			es.agroseguro.contratacion.datosVariables.Edad edad = es.agroseguro.contratacion.datosVariables.Edad.Factory
					.newInstance();
			edad.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setEdad(edad);
			break;
		case 112:
			// FECHA RECOLECCION
			es.agroseguro.contratacion.datosVariables.FechaRecoleccion fRecol = es.agroseguro.contratacion.datosVariables.FechaRecoleccion.Factory
					.newInstance();
			GregorianCalendar gcFRecol = new GregorianCalendar();

			try {
				gcFRecol.setTime(sdf.parse(valor.toString()));
			} catch (ParseException e1) {
				logger.error("Error al parsear la FechaRecoleccion", e1);
			}

			fRecol.setValor(gcFRecol);

			datosVariables.setFecRecol(fRecol);
			break;
		case 113:
			// FECHA SIEMBRA/TRASPLANTE
			es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante fSiembraTransplante = es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante.Factory
					.newInstance();
			GregorianCalendar gcFSiemb = new GregorianCalendar();

			try {
				gcFSiemb.setTime(sdf.parse(valor.toString()));
			} catch (ParseException e) {
				logger.error("Error al parsear la FechaSiembraTrasplante", e);
			}

			fSiembraTransplante.setValor(gcFSiemb);

			datosVariables.setFecSiemTrasp(fSiembraTransplante);
			break;

		case 114:
			// INDIC.GASTOS SALVAMENTO
			es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento gastSalv = es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento.Factory
					.newInstance();
			gastSalv.setValor(valor.toString());

			datosVariables.setIndGastSalv(gastSalv);
			break;
		case 124:
			// MEDIDA PREVENTIVA
			List<Integer> listaMedPrev = new ArrayList<Integer>();

			for (String val : valor.toString().split(" ")) {
				listaMedPrev.add(Integer.parseInt(val));
			}

			es.agroseguro.contratacion.datosVariables.MedidaPreventiva medPrev = es.agroseguro.contratacion.datosVariables.MedidaPreventiva.Factory
					.newInstance();
			medPrev.setValor(listaMedPrev);

			datosVariables.setMedPrev(medPrev);
			break;
		case 767:
			// METROS CUADRADOS
			es.agroseguro.contratacion.datosVariables.MetrosCuadrados met2 = es.agroseguro.contratacion.datosVariables.MetrosCuadrados.Factory
					.newInstance();
			met2.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setMet2(met2);
			break;
		case 766:
			// METROS LINEALES
			es.agroseguro.contratacion.datosVariables.MetrosLineales met = es.agroseguro.contratacion.datosVariables.MetrosLineales.Factory
					.newInstance();
			met.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setMet(met);
			break;
		case 617:
			// NUMERO DE ANOS DESDE PODA
			es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda nadp = es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda.Factory
					.newInstance();
			nadp.setValor((new BigDecimal(valor.toString())).intValue());

			datosVariables.setNadp(nadp);
			break;
		case 117:
			// NUMERO UNIDADES
			es.agroseguro.contratacion.datosVariables.NumeroUnidades numUds = es.agroseguro.contratacion.datosVariables.NumeroUnidades.Factory
					.newInstance();
			numUds.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setNumUnid(numUds);
			break;
		case 133:
			// PRACTICA CULTURAL
			es.agroseguro.contratacion.datosVariables.PracticaCultural praCul = es.agroseguro.contratacion.datosVariables.PracticaCultural.Factory
					.newInstance();
			praCul.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setPraCult(praCul);
			break;
		case 131:
			// SISTEMA CONDUCCION
			es.agroseguro.contratacion.datosVariables.SistemaConduccion sCond = es.agroseguro.contratacion.datosVariables.SistemaConduccion.Factory
					.newInstance();
			sCond.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setSisCond(sCond);
			break;
		case 123:
			// SISTEMA CULTIVO
			es.agroseguro.contratacion.datosVariables.SistemaCultivo sCul = es.agroseguro.contratacion.datosVariables.SistemaCultivo.Factory
					.newInstance();
			sCul.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setSisCult(sCul);
			break;
		case 616:
			// SISTEMA PRODUCCION
			es.agroseguro.contratacion.datosVariables.SistemaProduccion sProd = es.agroseguro.contratacion.datosVariables.SistemaProduccion.Factory
					.newInstance();
			sProd.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setSisProd(sProd);
			break;
		case 621:
			// SISTEMA PROTECCION
			es.agroseguro.contratacion.datosVariables.SistemaProteccion sProt = es.agroseguro.contratacion.datosVariables.SistemaProteccion.Factory
					.newInstance();
			sProt.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setSisProt(sProt);
			break;
		case 116:
			// TIPO MARCO PLANTACION
			es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion tmp = es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion.Factory
					.newInstance();
			tmp.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setTipMcoPlant(tmp);
			break;
		case 173:
			// TIPO PLANTACION
			es.agroseguro.contratacion.datosVariables.TipoPlantacion tp = es.agroseguro.contratacion.datosVariables.TipoPlantacion.Factory
					.newInstance();
			tp.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setTipPlant(tp);
			break;
		case 132:
			// TIPO SUB.DECLARADA PARCEL
			List listaTsdp = new ArrayList();
			listaTsdp.add(valor);
			es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela tsdp = es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela.Factory
					.newInstance();
			tsdp.setValor(listaTsdp);

			datosVariables.setTipSubDecPar(tsdp);
			break;
		case 768:
			// VALOR FIJO
			es.agroseguro.contratacion.datosVariables.ValorFijo vf = es.agroseguro.contratacion.datosVariables.ValorFijo.Factory
					.newInstance();
			vf.setValor(new BigDecimal(valor.toString()));

			datosVariables.setValFij(vf);
			break;
		case 778:
			// Tipo instalacion
			es.agroseguro.contratacion.datosVariables.TipoInstalacion tipInst = es.agroseguro.contratacion.datosVariables.TipoInstalacion.Factory
					.newInstance();
			tipInst.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setTipInst(tipInst);
			break;
		case 873:
			// Material Cubierta
			es.agroseguro.contratacion.datosVariables.MaterialCubierta mCub = es.agroseguro.contratacion.datosVariables.MaterialCubierta.Factory
					.newInstance();
			mCub.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setMatCubi(mCub);
			break;
		case 874:
			// Edad Cubierta
			es.agroseguro.contratacion.datosVariables.EdadCubierta eCub = es.agroseguro.contratacion.datosVariables.EdadCubierta.Factory
					.newInstance();
			eCub.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setEdadCubi(eCub);
			break;
		case 875:
			// Material Estructuras
			es.agroseguro.contratacion.datosVariables.MaterialEstructura mEst = es.agroseguro.contratacion.datosVariables.MaterialEstructura.Factory
					.newInstance();
			mEst.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setMatEstr(mEst);
			break;
		case 876:
			// Edad Estrucutra
			es.agroseguro.contratacion.datosVariables.EdadEstructura eEst = es.agroseguro.contratacion.datosVariables.EdadEstructura.Factory
					.newInstance();
			eEst.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setEdadEstr(eEst);
			break;
		case 879:
			// Codigo Certificado
			es.agroseguro.contratacion.datosVariables.CodigoCertificado cc = es.agroseguro.contratacion.datosVariables.CodigoCertificado.Factory
					.newInstance();
			cc.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setCodCert(cc);
			break;
		case 752:
			// Tipo terreno
			es.agroseguro.contratacion.datosVariables.TipoTerreno tt = es.agroseguro.contratacion.datosVariables.TipoTerreno.Factory
					.newInstance();
			tt.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setTipTer(tt);
			break;
		case 753:
			// Tipo masa
			es.agroseguro.contratacion.datosVariables.TipoMasa tm = es.agroseguro.contratacion.datosVariables.TipoMasa.Factory
					.newInstance();
			tm.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setTipMas(tm);
			break;
		case 754:
			// Pendiente
			es.agroseguro.contratacion.datosVariables.Pendiente p = es.agroseguro.contratacion.datosVariables.Pendiente.Factory
					.newInstance();
			p.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setPend(p);
			break;
		case 944:
			// Numero aï¿½os desde descorche
			es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche nadd = es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche.Factory
					.newInstance();
			nadd.setValor(Integer.parseInt(valor.toString()));

			datosVariables.setNadd(nadd);
			break;
		// FIN DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL
		// RIESGO CUBIERTO

		default:
			// No hacemos nada
			break;

		}
	}
	/* Pet.50776_63485-Fase II ** MODIF TAM (20.10.2020) ** Fin */

	/* Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
	public static boolean obtener_lineaSup2021(BigDecimal codPlan, BigDecimal codLinea, boolean isLineaGanado) {
		boolean lineaSup2021 = false;
		try {
			lineaSup2021 = pagoPolizaDao.lineaContratacion2021(codPlan, codLinea, isLineaGanado);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return lineaSup2021;
	}
	/* Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */

	// MODIF TAM (23.08.2018) ** Inicio //
	public synchronized void setPagoPolizaDao(IPagoPolizaDao pagoPolizaDao) {
		PolizaUnificadaTransformer.pagoPolizaDao = pagoPolizaDao;
	}
	// MODIF TAM (23.08.2018) ** Fin //

	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	public synchronized void setClaseManager(ClaseManager claseManager) {
		PolizaUnificadaTransformer.claseManager = claseManager;
	}

	public synchronized void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		PolizaUnificadaTransformer.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}

	public synchronized void setCuadroCoberturasManager(ICuadroCoberturasManager cuadroCoberturasManager) {
		PolizaUnificadaTransformer.cuadroCoberturasManager = cuadroCoberturasManager;
	}

	public synchronized final void setSeleccionPolizaDao(final ISeleccionPolizaDao seleccionPolizaDao) {
		PolizaUnificadaTransformer.seleccionPolizaDao = seleccionPolizaDao;
	}
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Fin */
}