package com.rsi.agp.core.jmesa.service.impl.rc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.InformesManager;
import com.rsi.agp.core.report.rcganado.ExplotacionInforme;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.dao.models.rc.IImpuestosRCDao;
import com.rsi.agp.dao.models.rc.IPolizasRCDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.commons.Localidad;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;
import com.rsi.agp.dao.tables.rc.PolizasRC;

public class InformesGanadoRCService {
	private static final Log LOGGER = LogFactory.getLog(InformesGanadoRCService.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp_informes_jasper");
	
	private static final String CIUDAD_EMISION = bundle.getString("informes.ciudad.Madrid");
	private static final String CODIGO_PRODUCTO = bundle.getString("informes.codigoProducto.ganandoRC");
	private static final String DURACION_ANUAL = bundle.getString("informes.duracion.anual");
	private static final String FORMA_PAGO = bundle.getString("informes.formaPago.unico");
	private static final String ASEGURADOR = bundle.getString("informes.asegurador");
	
	private static final String ESPACIO = " ";
	private static final BigDecimal CERO = new BigDecimal("0");
	private static final String ASEGURADO = "A";
	private static final String TOMADOR = "T";
	
	private IPolizasRCDao polizasRCDao;
	private IImpuestosRCDao impuestosRCDao;
	private InformesManager informesManager;
	
	public Map<String, Object> getRellenarInformacion(Long idPolizaRC) throws Exception {
		LOGGER.debug("Iniciamos la obtención de los datos para el informe");
		
		PolizasRC polizaRC = (PolizasRC)this.polizasRCDao.get(PolizasRC.class, idPolizaRC);
		
		Poliza poliza = polizaRC.getPoliza();
		Long idPolizaPpl = poliza.getIdpoliza();

		Map<String, Object> datos = new HashMap<String, Object>();

		Asegurado asegurado = poliza.getAsegurado();
		Colectivo colectivo = poliza.getColectivo();
		
		BigDecimal entidad = colectivo.getSubentidadMediadora().getId().getCodentidad();
		BigDecimal subEntidad = colectivo.getSubentidadMediadora().getId().getCodsubentidad();

		datos.put("numSolicitud", String.valueOf(idPolizaRC));
		
		datos.put("entMed", String.valueOf(entidad));
		datos.put("subEntMed", String.valueOf(subEntidad));
		datos.put("tipoIdentificacion", asegurado.getTipoidentificacion());
		
		datos.putAll(this.getDatosCondicionesParticulares(poliza, colectivo.getSubentidadMediadora()));
		datos.putAll(this.getDatosPersonales(asegurado, true));
		datos.putAll(this.getDatosPersonales(asegurado, false));
		datos.putAll(this.getRiesgos(polizaRC));
		
		datos.putAll(this.getDatosDePago(polizaRC));
		
		datos.putAll(this.informesManager.getNotaInformativa(entidad, idPolizaPpl, ConstantsInf.NOTA_INF_RC));
		
		final Pattern entidadPattern = Pattern.compile("[3][0-9][0-9][0-9]"); // 3xxx-x 
		if (entidadPattern.matcher(String.valueOf(entidad)).matches() && BigDecimal.ZERO.equals(subEntidad)) {
			datos.put("SHOW_ENT_SUBENT_MED", Boolean.FALSE);
		} else {
			datos.put("SHOW_ENT_SUBENT_MED", Boolean.TRUE);
		}

		datos.put("PLAN", poliza.getLinea().getCodplan().toString());
		
		datos.putAll(getFooter(CIUDAD_EMISION, polizaRC));
		
		LOGGER.debug("Datos para el informe obtenidos");
		return datos;
	}
		
	private HashMap<String,Object> getDatosDePago(PolizasRC polizaRC) throws DAOException{
		LOGGER.debug("Obtenemos datos de pago");
		HashMap<String, Object> datos = new HashMap<String, Object>();
		
		datos.putAll(this.obtenerImpuestos(polizaRC));
		Poliza poliza = polizaRC.getPoliza();
		datos.put(ConstantsRC.PARAMETRO_ENTIDAD_CUENTA_CORRIENTE, nombreEntidad(poliza));
		datos.put(ConstantsRC.PARAMETRO_IBAN_CUENTA_CORRIENTE, AseguradoUtil.getFormattedBankAccount(poliza, true));
		datos.put(ConstantsRC.PARAMETRO_PRIMA_NETA, NumberUtils.formatearMoneda(polizaRC.getPrimaNeta()));
		datos.put(ConstantsRC.PARAMETRO_FORMA_PAGO, FORMA_PAGO);
		datos.put(ConstantsRC.PARAMETRO_TOTAL_RECIBO, NumberUtils.formatearMoneda(polizaRC.getImporte()));
		datos.put(ConstantsRC.PARAMETRO_TITULAR_CUENTA_CORRIENTE,  this.titularCuenta(poliza));
		datos.put(ConstantsRC.PARAMETRO_PRIMA_NETA_ANUAL,  NumberUtils.formatearMoneda(polizaRC.getPrimaNeta()));
		
		return datos;
	}
	
	private static String nombreEntidad(Poliza poliza){
		String nombreEntidad = "";
		Asegurado asegurado = poliza.getAsegurado();
		if(asegurado != null){
			Entidad entidad = asegurado.getEntidad();
			if(entidad != null){
				nombreEntidad = entidad.getNomentidad();
			}
		}
		return nombreEntidad;
	}
	
	private String titularCuenta(Poliza poliza) {
		DatoAsegurado datoAsegurado = AseguradoUtil.obtenerDatoAsegurado(poliza);
		String titularCuenta = "";
		String destinatarioDomiciliacion = datoAsegurado.getDestinatarioDomiciliacion();		
		if(ASEGURADO.equals(destinatarioDomiciliacion)){
			titularCuenta = this.nombreAsegurado(poliza.getAsegurado());
		} else if(TOMADOR.equals(destinatarioDomiciliacion)){
			titularCuenta = poliza.getColectivo().getTomador().getRazonsocial();
		} else {
			titularCuenta = datoAsegurado.getTitularCuenta();
		}
		return titularCuenta;
	}

	private Map<String, Object> obtenerImpuestos(PolizasRC polizaRC) throws DAOException {
		LOGGER.debug("Calculando los impuestos de la poliza RC");
		Map<String, Object> datos = new HashMap<String, Object>();
		BigDecimal plan = polizaRC.getPoliza().getLinea().getCodplan();
		List<ImpuestosRC> impuestosRC = (List<ImpuestosRC>)this.impuestosRCDao.getImpuestosRC(plan);
		BigDecimal primaNeta = obtenerPrimaNeta(polizaRC);	
		for(int i = 0; i < impuestosRC.size(); i++){
			ImpuestosRC impuestoRC = impuestosRC.get(i);
			BigDecimal importeImpuesto;
			BigDecimal porcentaje = impuestoRC.getValor().divide(Constants.CIEN);
			if(ConstantsRC.BASE_IMP_SUMA_ASEGURADA.equals(impuestoRC.getBaseSbp().getId())) {
				importeImpuesto = polizaRC.getSumaAsegurada().multiply(porcentaje);	
			} else {
				// POR DEFECTO BASE = PRIMA NETA
				importeImpuesto = primaNeta.multiply(porcentaje);		
			}
			String vuelta = String.valueOf(i);
			datos.put(ConstantsRC.PARAMETRO_DESC_IMP + vuelta, impuestoRC.getImpuestoSbp().getDescripcion());
			datos.put(ConstantsRC.PARAMETRO_VALOR_IMP + vuelta, NumberUtils.formatearMoneda(importeImpuesto));
		}
		return datos;
	}
	
	private static BigDecimal obtenerPrimaNeta(PolizasRC polizaRC){
		BigDecimal primaNeta = polizaRC.getPrimaNeta();
		BigDecimal primaMinima = polizaRC.getPrimaMinima();
		
		if(primaNeta.compareTo(CERO) == 0 && primaMinima.compareTo(CERO) == 0){
			primaNeta = CERO;
		}
		if(primaNeta.compareTo(primaMinima) < 0){
			primaNeta = polizaRC.getPrimaMinima();
		}
		return primaNeta;
	}
	
	private HashMap<String,Object> getRiesgos(PolizasRC polizaRC) throws DAOException{
		LOGGER.debug("Obtenemos los riesgos");
		HashMap<String, Object> datos = new HashMap<String, Object>();

		Long lineaseguroid = polizaRC.getPoliza().getLinea().getLineaseguroid();
		Set<Explotacion> explotaciones = polizaRC.getPoliza().getExplotacions();

		Map<String, Long> explotacionesProcesadas = this.procesarExplotacionesPoliza(explotaciones, lineaseguroid);
		
		String especieRC = polizaRC.getEspeciesRC().getDescripcion();
		String regimenRC = polizaRC.getRegimenRC().getDescripcion();
		
		JRBeanCollectionDataSource explotacionesInforme = this.crearExplotacionesInforme(explotacionesProcesadas, especieRC, regimenRC);
		datos.put(ConstantsRC.PARAMETRO_EXPLOTACIONES_DATASOURCE, explotacionesInforme);
		
		datos.put(ConstantsRC.PARAMETRO_FRANQUICIA, NumberUtils.formatearMoneda(polizaRC.getFranquicia()));
		datos.put(ConstantsRC.PARAMETRO_SUMA_ASEGURADA, NumberUtils.formatearMoneda(polizaRC.getSumaAsegurada()));
		return datos;
	}
	
	private JRBeanCollectionDataSource crearExplotacionesInforme(Map<String, Long> explotacionesProcesadas, String especieRC, String regimenRC){
		List<ExplotacionInforme> explotaciones = new ArrayList<ExplotacionInforme>();
		for(Map.Entry<String, Long> entry : explotacionesProcesadas.entrySet()){
			String rega = entry.getKey();
			String animales = entry.getValue().toString();
			ExplotacionInforme explotacionInfoRC = new ExplotacionInforme(rega, especieRC, regimenRC, animales);
			explotaciones.add(explotacionInfoRC);
		}
		return new JRBeanCollectionDataSource(explotaciones);
	}
	
	private Map<String,Long> procesarExplotacionesPoliza(Set<Explotacion> explotaciones, Long lineaSeguroId) throws DAOException{
		Map<String,Long> resultado = new HashMap<String, Long>();
		for(Explotacion exp : explotaciones){
			Long codEspecie = exp.getEspecie();
			String rega = exp.getRega();
			Long codRegimen = exp.getRegimen();
			for(GrupoRaza gr : exp.getGrupoRazas()){
				BigDecimal codTipoCapital = gr.getCodtipocapital();
				Long numAnimales = gr.getNumanimales();
				Boolean tieneRegimen = false;
				tieneRegimen = this.polizasRCDao.grupoRazaTieneRegimen(lineaSeguroId, codEspecie, codRegimen, codTipoCapital);
				if(tieneRegimen){
					if(resultado.containsKey(rega)){
						numAnimales += resultado.get(rega);
					}
					resultado.put(rega, numAnimales);
				}
			}
		}
		return resultado;
	}
	
	private Map<String, Object> getDatosCondicionesParticulares(Poliza poliza, SubentidadMediadora subEntidadMediadora){
		LOGGER.debug("Obtenemos las condiciones particulares");
		HashMap<String,Object> datos = new HashMap<String, Object>();
		Entidad entidad = subEntidadMediadora.getEntidad();
		datos.put(ConstantsRC.PARAMETRO_ASEGURADOR, ASEGURADOR);
		datos.put(ConstantsRC.PARAMETRO_POLIZA, StringUtils.defaultString(poliza.getReferencia()));
		datos.put(ConstantsRC.PARAMETRO_NUMERO, poliza.getDc() != null ? poliza.getDc().toString() : "");
		datos.put(ConstantsRC.PARAMETRO_PRODUCTO, CODIGO_PRODUCTO);
		datos.put(ConstantsRC.PARAMETRO_CAJA, entidad.getCodentidad() != null ?entidad.getCodentidad().toString() : "");
		datos.put(ConstantsRC.PARAMETRO_OFICINA, StringUtils.defaultString(poliza.getOficina()));
		datos.put(ConstantsRC.PARAMETRO_MEDIADOR, StringUtils.defaultString(entidad.getNomentidad()));
		datos.put(ConstantsRC.PARAMETRO_DURACION, DURACION_ANUAL);
		return datos;
	}
	
	private Map<String, Object> getDatosPersonales(Asegurado asegurado, boolean esTomador){
		Map<String, Object> datos = new HashMap<String, Object>();
		
		String prefijo = esTomador ? ConstantsRC.PARAMETRO_PREFIJO_TOMADOR : ConstantsRC.PARAMETRO_PREFIJO_ASEGURADO;
				
		datos.put(prefijo + ConstantsRC.PARAMETRO_NOMBRE_COMPLETO, this.nombreAsegurado(asegurado));
		datos.put(prefijo + ConstantsRC.PARAMETRO_TELEFONO, asegurado.getTelefono());
		datos.put(prefijo + ConstantsRC.PARAMETRO_EMAIL, StringUtils.defaultString(asegurado.getEmail()));
		datos.put(prefijo + ConstantsRC.PARAMETRO_IDENTIFICADOR, asegurado.getNifcif());
		datos.put(prefijo + ConstantsRC.PARAMETRO_DOMICILIO, this.domicilioAsegurado(asegurado));
		datos.putAll(this.getPoblacionYProvincia(prefijo, asegurado.getLocalidad()));
		datos.put(prefijo + ConstantsRC.PARAMETRO_CODIGO_POSTAL, asegurado.getCodpostalstr());
		
		return datos;
	}
	
	private String nombreAsegurado(Asegurado asegurado){
		String nombre = "";
		if(StringUtils.isNotBlank(asegurado.getRazonsocial())){
			nombre = asegurado.getRazonsocial();
		} else if(StringUtils.isNotBlank(asegurado.getNombreCompleto())) {
			nombre = asegurado.getNombreCompleto();
		} else if(StringUtils.isNotBlank(asegurado.getFullName())){
			nombre = asegurado.getFullName();
		} else {
			nombre = this.componerNombre(asegurado);
		}
		return nombre;
	}
	
	private String componerNombre(Asegurado asegurado){
		String nombre = StringUtils.defaultString(asegurado.getNombre());
		String apellido1 = StringUtils.defaultString(asegurado.getApellido1());
		String apellido2 = StringUtils.defaultString(asegurado.getApellido2());
		return new StringBuilder(nombre).append(ESPACIO).append(apellido1).append(ESPACIO).append(apellido2).toString();
	}
		
	private String domicilioAsegurado(Asegurado asegurado){
		String via = StringUtils.defaultString(asegurado.getVia().getAbreviatura());
		String direccion  = StringUtils.defaultString(asegurado.getDireccion());
		String numVia = StringUtils.defaultString(asegurado.getNumvia());
		String piso = StringUtils.defaultString(asegurado.getPiso());
		String bloque = StringUtils.defaultString(asegurado.getBloque());
		String escalera = StringUtils.defaultString(asegurado.getEscalera());
		return new StringBuilder(via).append(ESPACIO)
				.append(direccion).append(ESPACIO)
				.append(numVia).append(ESPACIO)
				.append(piso).append(ESPACIO)
				.append(bloque).append(ESPACIO)
				.append(escalera).toString();
	}
	
	private static Map<String,Object> getFooter(String ciudad, PolizasRC polizaRC) throws Exception{
		LOGGER.debug("Obtenemos el pie de pagina");
		Map<String, Object> datos = new HashMap<String, Object>();
		datos.put(ConstantsRC.PARAMETRO_CIUDAD_EMISION, ciudad);
		int compare = polizaRC.getEstadosRC().getId().compareTo(ConstantsRC.ESTADO_RC_PDTE_ACEPTACION);
		switch (compare) {
		case 0:
		case 1:
			datos.put(ConstantsRC.PARAMETRO_FECHA_EMISION, polizaRC.getFechaEnvio());
			break;
		case -1:
			datos.put(ConstantsRC.PARAMETRO_FECHA_EMISION, Calendar.getInstance().getTime());
			break;
		default:
			break;
		}
		return datos;
	}
	
	private Map<String, String> getPoblacionYProvincia(String prefijo, Localidad localidad){
		Map<String, String> datos = new HashMap<String, String>();
		String poblacion = "";
		String nombreProvincia = "";
		if (localidad != null){
			poblacion = localidad.getNomlocalidad();
			Provincia provincia = localidad.getProvincia();
			if (provincia != null){
				nombreProvincia = provincia.getNomprovincia();
			}
		}
		datos.put(prefijo + ConstantsRC.PARAMETRO_POBLACION, poblacion);
		datos.put(prefijo + ConstantsRC.PARAMETRO_PROVINCIA, nombreProvincia);
		return datos;
	}
	
	public void setPolizasRCDao(IPolizasRCDao polizasRCDao){
		this.polizasRCDao = polizasRCDao;
	}

	public void setInformesManager(InformesManager informesManager) {
		this.informesManager = informesManager;
	}
	
	public void setImpuestosRCDao(IImpuestosRCDao impuestosRCDao){
		this.impuestosRCDao = impuestosRCDao;
	}
}
