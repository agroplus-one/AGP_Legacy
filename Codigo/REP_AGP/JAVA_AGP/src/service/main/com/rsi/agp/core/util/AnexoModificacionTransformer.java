package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.managers.ICuadroCoberturasManager;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.webapp.util.CoberturasUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.IParcelaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;

import es.agroseguro.contratacion.parcela.ParcelaDocument;
import es.agroseguro.iTipos.Ambito;
import es.agroseguro.iTipos.DatosContacto;
import es.agroseguro.iTipos.Direccion;
import es.agroseguro.iTipos.IdentificacionCatastral;
import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.iTipos.SIGPAC;
import es.agroseguro.iTipos.SiNo;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CalculoIndemnizacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CaracteristicasExplotacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CicloCultivo;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CodigoCertificado;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CodigoIGP;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.CodigoReduccionRdtos;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DaniosCubiertos;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DenominacionOrigen;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Densidad;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Destino;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.DiasLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Edad;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.EdadCubierta;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.EdadEstructura;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.EstadoFenologicoLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaRecoleccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.FechaSiembraTrasplante;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Garantizado;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.IndicadorGastosSalvamento;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MaterialCubierta;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MaterialEstructura;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MedidaPreventiva;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MesesLimiteGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MetrosCuadrados;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.MetrosLineales;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroAniosDesdeDescorche;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroAniosDesdePoda;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.NumeroUnidades;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Pendiente;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PeriodoGarantias;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeCapitalAsegurado;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeFranquicia;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeMinimoIndemnizable;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.PracticaCultural;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.RiesgoCubiertoElegido;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.Rotacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaConduccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaCultivo;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaProduccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.SistemaProteccion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoFranquicia;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoInstalacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoMarcoPlantacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoMasa;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoPlantacion;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoRendimiento;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoSubvencionDeclaradaParcela;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.TipoTerreno;
import es.agroseguro.seguroAgrario.contratacion.datosVariables.ValorFijo;
import es.agroseguro.seguroAgrario.modificacion.Asegurado;
import es.agroseguro.seguroAgrario.modificacion.CapitalAsegurado;
import es.agroseguro.seguroAgrario.modificacion.CapitalesAsegurados;
import es.agroseguro.seguroAgrario.modificacion.Cobertura;
import es.agroseguro.seguroAgrario.modificacion.Colectivo;
import es.agroseguro.seguroAgrario.modificacion.Cosecha;
import es.agroseguro.seguroAgrario.modificacion.DatosAntesModificacion;
import es.agroseguro.seguroAgrario.modificacion.DatosVariables;
import es.agroseguro.seguroAgrario.modificacion.Entidad;
import es.agroseguro.seguroAgrario.modificacion.Gastos;
import es.agroseguro.seguroAgrario.modificacion.Mediador;
import es.agroseguro.seguroAgrario.modificacion.ObjetosAsegurados;
import es.agroseguro.seguroAgrario.modificacion.Parcela;
import es.agroseguro.seguroAgrario.modificacion.Poliza;
import es.agroseguro.seguroAgrario.modificacion.SeguridadSocial;
import es.agroseguro.seguroAgrario.modificacion.SubvencionDeclarada;
import es.agroseguro.seguroAgrario.modificacion.SubvencionesDeclaradas;

/**
 * Clase para transformar una poliza de base de datos en una poliza para enviar a Agroseguro
 * @author U028783
 *
 */
public class AnexoModificacionTransformer {
	
	private static Log logger = LogFactory.getLog(AnexoModificacionTransformer.class);
	
	private static IParcelaDao parcelaDao;
	
	/*** SONAR Q ** MODIF TAM(14.11.2021) ***/
	/** - Se ha eliminado todo el c骴igo comentado
	 * - Se crean metodos nuevos para descargar de ifs/fors
	 * - Se crean constantes locales nuevas
	 **/
	
	/** P00078877 ** MODIF TAM (05.11.2021) ** Inicio */
	private static ClaseManager claseManager;
	private static CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	private static ICuadroCoberturasManager cuadroCoberturasManager;
	private static ISeleccionPolizaDao seleccionPolizaDao;
	/** P00078877 ** MODIF TAM (05.11.2021) ** Fin */
	
	/** CONSTANTES SONAR Q ** MODIF TAM (12.11.2021) ** Inicio **/
	private final static String FORMAT_DATE = "dd/MM/yyyy"; 
	private final static String FECHA = "La fecha ";
	private final static String NO_VALIDA = " no es valida para el dato ";
	private final static String NO_VALIDO = " no es valido para el dato ";
	private final static String NUMERO = "El numero ";
	private final static String ERR_ASIG = "Error al asignar los datos variables de A.M. para generar el xml";
	/** CONSTANTES SONAR Q ** MODIF TAM (12.11.2021) ** Fin **/

	
	public static Poliza transformar(AnexoModificacion anexo, com.rsi.agp.dao.tables.copy.Poliza copy, Map<BigDecimal, 
									 List<String>> listaDatosVariables, List<BigDecimal> listaCPM, 
									 List<BigDecimal> codsConceptos,Usuario usuario) throws Exception{
		Poliza polizaAS = Poliza.Factory.newInstance();
		
		GregorianCalendar gc = new GregorianCalendar();
		List<String> lstAsunto = Arrays.asList(StringUtils.nullToString(anexo.getAsunto()).split(";"));

		Colectivo c = Colectivo.Factory.newInstance();
		Entidad entidad = Entidad.Factory.newInstance();
		
		polizaAS.setNifAsegurado(anexo.getPoliza().getAsegurado().getNifcif());
		
		if (copy != null){
			//Rellenamos el anexo con los datos de la copy
			polizaAS.setPlan(Integer.parseInt(copy.getCodplan()));
			polizaAS.setLinea(copy.getCodlinea().intValue());
			polizaAS.setReferencia(copy.getRefpoliza());
			polizaAS.setDigitoControl(copy.getDcpoliza().intValue());
			polizaAS.setFechaFirmaDocumento(gc);
			polizaAS.setAsunto(lstAsunto);
			polizaAS.setModulo(String.format("%-5s", copy.getCodmodulo().trim()));
			
			//COLECTIVO
			for (com.rsi.agp.dao.tables.copy.Colectivo col : copy.getColectivos()){
				c.setDigitoControl(col.getDccolectivo().intValue());
				c.setNif(col.getCifniftomador());
				c.setReferencia(col.getRefcolectivo());
				polizaAS.setColectivo(c);
				break;
			}
			//FIN COLECTIVO
			
			//ENTIDAD
			entidad.setCodigo(copy.getCodentidadaseg());
			
			/* SONAR Q */
			String codInterno = obtenerCodInterno(copy, anexo);
			entidad = informarEntidad(entidad, anexo, codInterno);
			/* FIN SONAR Q */
			
			polizaAS.setEntidad(entidad);
			//FIN ENTIDAD
			
		}else{
			//Rellenamos el anexo con los datos de la poliza
			polizaAS.setPlan(anexo.getPoliza().getLinea().getCodplan().intValue());
			polizaAS.setLinea(anexo.getPoliza().getLinea().getCodlinea().intValue());
			polizaAS.setReferencia(anexo.getPoliza().getReferencia());
			if (anexo.getPoliza().getDc() != null)
				polizaAS.setDigitoControl(anexo.getPoliza().getDc().intValue());
			polizaAS.setFechaFirmaDocumento(gc);

			polizaAS.setAsunto(lstAsunto);
			polizaAS.setModulo(String.format("%-5s", anexo.getPoliza().getCodmodulo().trim()));
			
			//COLECTIVO
			c.setDigitoControl(Integer.parseInt(anexo.getPoliza().getColectivo().getDc()));
			c.setNif(anexo.getPoliza().getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(anexo.getPoliza().getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			//FIN COLECTIVO
			
			//ENTIDAD
			entidad.setCodigo(Constants.ENTIDAD_C616);
			
			String codInterno = String.format("%4s", anexo.getPoliza().getColectivo().getSubentidadMediadora().getId().getCodentidad()) +
					// Esta linea es para el caso de que no puedan venir espacios en blanco	
					//String.format("%04d", Integer.parseInt(poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad().toString()));
					String.format("%4s", anexo.getPoliza().getColectivo().getSubentidadMediadora().getId().getCodsubentidad());
			entidad.setCodigoInterno(String.format("%12s", codInterno));
			/* SONAR Q */
			entidad = informarEnt(anexo, entidad);

			/* FIN SONAR Q */
			polizaAS.setEntidad(entidad);
			//FIN ENTIDAD
		}
		
		//INICIO DATOS QUE PUEDEN SER MODIFICADOS EN EL ANEXO
		
		//ASEGURADO
		if (!StringUtils.nullToString(anexo.getNomaseg()).equals("") || !StringUtils.nullToString(anexo.getRazsocaseg()).equals("") ){
			//Si se ha modificado el asegurado, lo anhado al anexo de modificacion
			Asegurado a = Asegurado.Factory.newInstance();
			
			/* SONAR Q */
			a = informarAsegurado (a, anexo);
			/* FIN SONAR Q */
			
			Direccion dir = Direccion.Factory.newInstance();
			dir.setBloque(anexo.getBloqueaseg());
			dir.setCp(anexo.getCodposasegstr());
			dir.setEscalera(anexo.getEscaseg());
			dir.setLocalidad(anexo.getNomlocalidad());
			dir.setNumero(anexo.getNumaseg());
			dir.setPiso(anexo.getPisoaseg());
			dir.setProvincia(anexo.getCodprovincia().intValue());
			dir.setVia(anexo.getCalleaseg());
			a.setDireccion(dir);
			
			
			DatosContacto dContacto = DatosContacto.Factory.newInstance();
			/* SONAR Q */
			dContacto = informarDatContacto(anexo, dContacto);
			/* FIN SONAR Q */
			a.setDatosContacto(dContacto);
			
			polizaAS.setAsegurado(a);
		}
		//FIN ASEGURADO
			
		//COBERTURAS	
		
		Cobertura cobertura = Cobertura.Factory.newInstance();
		/* SONAR Q */
		cobertura = informarCobertura(cobertura, anexo, copy);
		/* FIN SONAR Q */
		
		if (anexo.getCoberturas() != null && anexo.getCoberturas().size() > 0) {	
			DatosVariables[] datosVariables = getDatosVariablesCobertura(anexo.getCoberturas(), listaCPM);
			cobertura.setDatosVariablesArray(datosVariables);
			polizaAS.setCobertura(cobertura);
		}
		//FIN COBERTURAS
		
		//OBJETOS ASEGURADOS
		if (anexo.getParcelas() != null && anexo.getParcelas().size() > 0){
			ObjetosAsegurados objAseg = ObjetosAsegurados.Factory.newInstance();
			Parcela[] parcelasArr = getParcelasArray(anexo, listaDatosVariables, listaCPM,  codsConceptos);
			if (parcelasArr != null){
				objAseg.setParcelaArray(parcelasArr);
				polizaAS.setObjetosAsegurados(objAseg);
			}
		}
		//FIN OBJETOS ASEGURADOS
		
		/* SONAR Q */
		polizaAS = informarSubvencionesDecl(anexo, polizaAS);
		/* FIN SONAR Q */
		
		//FIN DATOS QUE PUEDEN SER MODIFICADOS EN EL ANEXO

		return polizaAS;
	}
	
	
	//Version "light" para env锟給 a web service de recalculo de produccion
	public static es.agroseguro.contratacion.Poliza transformarParaEnvioWSRecalculo(AnexoModificacion anexo,
			com.rsi.agp.dao.tables.copy.Poliza copy,
			Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos,
			Usuario usuario, Set<Long> colIdParcelasFiltro,
			boolean calcRendOriHist) throws Exception {
		
		logger.debug("AnexoModificacionTransformer - transformarParaEnvioWSRecalculo [INIT]");
		
		
		es.agroseguro.contratacion.Poliza polizaAS = es.agroseguro.contratacion.Poliza.Factory.newInstance();

		es.agroseguro.contratacion.Colectivo c = es.agroseguro.contratacion.Colectivo.Factory.newInstance();
		es.agroseguro.contratacion.Entidad entidad = es.agroseguro.contratacion.Entidad.Factory.newInstance();

		// Rellenamos el anexo con los datos de la poliza
		polizaAS.setPlan(anexo.getPoliza().getLinea().getCodplan().intValue());
		polizaAS.setLinea(anexo.getPoliza().getLinea().getCodlinea().intValue());
		polizaAS.setReferencia(anexo.getPoliza().getReferencia());

		// COLECTIVO
		c.setDigitoControl(Integer.parseInt(anexo.getPoliza().getColectivo().getDc()));
		c.setNif(anexo.getPoliza().getColectivo().getTomador().getId().getCiftomador());
		c.setReferencia(anexo.getPoliza().getColectivo().getIdcolectivo());
		polizaAS.setColectivo(c);
		// FIN COLECTIVO

		// ENTIDAD		
		entidad = getEntidad(anexo.getPoliza()); 
		polizaAS.setEntidad(entidad);
		// FIN ENTIDAD

		// INICIO DATOS QUE PUEDEN SER MODIFICADOS EN EL ANEXO

		
		/* SONAR Q */
		polizaAS = informarAsegenPolizaAs(anexo, polizaAS);
		/* FIN SONAR Q */
		
		// COBERTURAS
		es.agroseguro.contratacion.Cobertura cobertura = es.agroseguro.contratacion.Cobertura.Factory.newInstance();

		String codModuloAnexo	= anexo.getCodmodulo().trim();
		String codModuloPoliza 	= anexo.getPoliza().getCodmodulo().trim();
		
		if (!StringUtils.nullToString(codModuloAnexo).equals("")){
			cobertura.setModulo(String.format("%-5s", codModuloAnexo));
		
		}else{
			cobertura.setModulo(String.format("%-5s", codModuloPoliza));
			codModuloAnexo = codModuloPoliza;
		}
		
		/* Pet. 78877 ** MODIF TAM (05.11.2020) ** Inicio */
		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();
		
		try {
			datosVariables = getDatosVarCobertRendimientos(anexo.getPoliza(), anexo.getCodmodulo(), listaCPM);
		} catch (Exception e) {
			throw new ValidacionPolizaException(
					"error al obtener los Datos Variables de Coberturas en Calculo de Rendimientos de Anexos");
		}
		/* Pet. 78877 ** MODIF TAM (05.11.2020) ** Inicio */
		cobertura.setDatosVariables(datosVariables);

		//}	
		
		polizaAS.setCobertura(cobertura);
		// FIN COBERTURAS

		// OBJETOS ASEGURADOS
		
		org.w3c.dom.Node importedNode;
		if (anexo.getParcelas() != null && anexo.getParcelas().size() > 0) {
			es.agroseguro.contratacion.ObjetosAsegurados objAseg = es.agroseguro.contratacion.ObjetosAsegurados.Factory.newInstance();
			
			List<es.agroseguro.contratacion.parcela.ParcelaDocument> parcelasArr =getParcelasArrayParaEnvioWSRecalculo(anexo,
					listaDatosVariables, listaCPM, codsConceptos, colIdParcelasFiltro, calcRendOriHist);
			
			for (ParcelaDocument parcelaDoc : parcelasArr) { 
				importedNode = objAseg.getDomNode().getOwnerDocument() 
						.importNode(parcelaDoc.getParcela().getDomNode(), true); 
				objAseg.getDomNode().appendChild(importedNode); 
			} 
			
			if (parcelasArr != null) {
				polizaAS.setObjetosAsegurados(objAseg);
			}
		}
		// FIN OBJETOS ASEGURADOS
		
		return polizaAS;
	}
	
	
	
	/**
	 * Metodo para establecer cada dato variable en el objeto 'DatosVariables'. Sirve tanto para Coberturas como para Capitales Asegurados
	 * @param codconcepto Codigo del concepto a anhadir
	 * @param codconceptoppalmod Concepto principal del modulo del cual depende el codconcepto (si procede). Puede ser null.
	 * @param codriesgocubierto Riesgo cubierto del cual depende el codconcepto (si procede). Puede ser null.
	 * @param valor Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String (Capital Asegurado)
	 * @param datosVariables Estructura donde se van anhadiendo los datos variables.
	 * @throws ParseException 
	 * @throws ParseException Error al parsear fechas.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setDatoVariableUnif(BigDecimal codconcepto,
			BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Object valor,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables) throws ParseException, NumberFormatException {
		
		//Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		
		//Comienzo del tratamiento del dato variable
		switch (codconcepto.intValue()){
			//INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 362:
				//% CAPITAL ASEGURADO
				List<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> lstCA = Arrays.asList(datosVariables.getCapAsegArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado> lstCAA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado>(lstCA);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado capital = es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado.Factory.newInstance();
				capital.setCodRCub(codriesgocubierto.intValue());
				capital.setCPMod(codconceptoppalmod.intValue());
				capital.setValor(Integer.parseInt(valor.toString()));
				
				lstCAA.add(capital);
				
				datosVariables.setCapAsegArray(lstCAA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado[lstCAA.size()]));
				break;
			case 120:
				//% FRANQUICIA
				List<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia> lstFA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia>(lstF);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia f = es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia.Factory.newInstance();
				f.setCodRCub(codriesgocubierto.intValue());
				f.setCPMod(codconceptoppalmod.intValue());
				f.setValor(Integer.parseInt(valor.toString()));
				
				lstFA.add(f);
				
				datosVariables.setFranqArray(lstFA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia[lstFA.size()]));
				break;
			case 121:
				//% MINIMO INDEMNIZABLE
				List<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable>(lstMI);
				
				es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable m = es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable.Factory.newInstance();
				m.setCodRCub(codriesgocubierto.intValue());
				m.setCPMod(codconceptoppalmod.intValue());
				m.setValor(Integer.parseInt(valor.toString()));
				
				lstMIA.add(m);
				
				datosVariables.setMinIndemArray(lstMIA.toArray(new es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable[lstMIA.size()]));
				break;
			case 174:
				//CALCULO INDEMNIZACION
				List<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> lstCI = Arrays.asList(datosVariables.getCalcIndemArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion> lstCIA = new ArrayList<es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion>(lstCI);
				
				es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c = es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion.Factory.newInstance();
				c.setCodRCub(codriesgocubierto.intValue());
				c.setCPMod(codconceptoppalmod.intValue());
				c.setValor(Integer.parseInt(valor.toString()));
				
				lstCIA.add(c);
				
				datosVariables.setCalcIndemArray(lstCIA.toArray(new es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion[lstCIA.size()]));
				break;
			case 169:
				//DANHOS CUBIERTOS
				List<es.agroseguro.contratacion.datosVariables.DaniosCubiertos> lstDNC = Arrays.asList(datosVariables.getDnCbtosArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.DaniosCubiertos> lstDNCA = new ArrayList<es.agroseguro.contratacion.datosVariables.DaniosCubiertos>(lstDNC);
				
				es.agroseguro.contratacion.datosVariables.DaniosCubiertos dan = es.agroseguro.contratacion.datosVariables.DaniosCubiertos.Factory.newInstance();
				dan.setCodRCub(codriesgocubierto.intValue());
				dan.setCPMod(codconceptoppalmod.intValue());
				dan.setValor(valor+"");
				
				lstDNCA.add(dan);
				
				datosVariables.setDnCbtosArray(lstDNCA.toArray(new es.agroseguro.contratacion.datosVariables.DaniosCubiertos[lstDNCA.size()]));
				break;
			case 175:
				//GARANTIZADO
				List<es.agroseguro.contratacion.datosVariables.Garantizado> lstG = Arrays.asList(datosVariables.getGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado> lstGA = new ArrayList<es.agroseguro.contratacion.datosVariables.Garantizado>(lstG);

				es.agroseguro.contratacion.datosVariables.Garantizado garant = es.agroseguro.contratacion.datosVariables.Garantizado.Factory.newInstance();
				garant.setCodRCub(codriesgocubierto.intValue());
				garant.setCPMod(codconceptoppalmod.intValue());
				garant.setValor(Integer.parseInt(valor.toString()));
				lstGA.add(garant);
				
				datosVariables.setGarantArray(lstGA.toArray(new es.agroseguro.contratacion.datosVariables.Garantizado[lstGA.size()]));
				break;
			case 363:
				//RIESGO CUBIERTO ELEGIDO
				List<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCEA = new ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido>(lstRCE);

				es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCubEleg = es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido.Factory.newInstance();
				rCubEleg.setCodRCub(codriesgocubierto.intValue());
				rCubEleg.setCPMod(codconceptoppalmod.intValue());
				
				//Ponemos la S y la N tambien para cuando venga del copy
				if (valor.equals("-1") || valor.equals("S")) rCubEleg.setValor("S");
				else rCubEleg.setValor("N");
				
				lstRCEA.add(rCubEleg);
				
				datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[lstRCEA.size()]));
				break;
			case 170:
				//TIPO FRANQUICIA
				List<es.agroseguro.contratacion.datosVariables.TipoFranquicia> lstTF = Arrays.asList(datosVariables.getTipFranqArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.TipoFranquicia> lstTFA = new ArrayList<es.agroseguro.contratacion.datosVariables.TipoFranquicia>(lstTF);
				
				es.agroseguro.contratacion.datosVariables.TipoFranquicia tipFranq = es.agroseguro.contratacion.datosVariables.TipoFranquicia.Factory.newInstance();
				tipFranq.setCodRCub(codriesgocubierto.intValue());
				tipFranq.setCPMod(codconceptoppalmod.intValue());
				tipFranq.setValor(valor+"");
				
				lstTFA.add(tipFranq);
				
				datosVariables.setTipFranqArray(lstTFA.toArray(new es.agroseguro.contratacion.datosVariables.TipoFranquicia[lstTFA.size()]));
				break;
			case 502:
				//TIPO RENDIMIENTO
				List<es.agroseguro.contratacion.datosVariables.TipoRendimiento> lstTR = Arrays.asList(datosVariables.getTipRdtoArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.TipoRendimiento> lstTRA = new ArrayList<es.agroseguro.contratacion.datosVariables.TipoRendimiento>(lstTR);
				
				es.agroseguro.contratacion.datosVariables.TipoRendimiento tipRdto = es.agroseguro.contratacion.datosVariables.TipoRendimiento.Factory.newInstance();
				tipRdto.setCodRCub(codriesgocubierto.intValue());
				tipRdto.setCPMod(codconceptoppalmod.intValue());
				tipRdto.setValor(Integer.parseInt(valor.toString()));
				
				lstTRA.add(tipRdto);
				
				datosVariables.setTipRdtoArray(lstTRA.toArray(new es.agroseguro.contratacion.datosVariables.TipoRendimiento[lstTRA.size()]));
				break;
			//FIN DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
				
			//INICIO DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 144:
				//ALTERNATIVA
				es.agroseguro.contratacion.datosVariables.Rotacion alt = es.agroseguro.contratacion.datosVariables.Rotacion.Factory.newInstance();
				alt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setRot(alt);
				break;
			case 106:
				//CARACT. EXPLOTACION
				es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion carExlp = es.agroseguro.contratacion.datosVariables.CaracteristicasExplotacion.Factory.newInstance();
				carExlp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCarExpl(carExlp);
				break;
			case 618:
				//CICLO CULTIVO
				es.agroseguro.contratacion.datosVariables.CicloCultivo ciCul = es.agroseguro.contratacion.datosVariables.CicloCultivo.Factory.newInstance();
				ciCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCiCul(ciCul);
				break;
			case 765:
				//CODIGO IGP
				es.agroseguro.contratacion.datosVariables.CodigoIGP igp = es.agroseguro.contratacion.datosVariables.CodigoIGP.Factory.newInstance();
				igp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodIGP(igp);
				break;
			case 620:
				//CODIGO REDUCCION RDTOS.
				List lista = new ArrayList();
				lista.add(valor);
				es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos redRdto = es.agroseguro.contratacion.datosVariables.CodigoReduccionRdtos.Factory.newInstance();
				redRdto.setValor(lista);
				
				datosVariables.setCodRedRdto(redRdto);
				break;
			case 107:
				//DENOMINACION ORIGEN
				es.agroseguro.contratacion.datosVariables.DenominacionOrigen denOrig = es.agroseguro.contratacion.datosVariables.DenominacionOrigen.Factory.newInstance();
				denOrig.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodDO(denOrig);
				break;
			case 109:
				//DENSIDAD
				es.agroseguro.contratacion.datosVariables.Densidad dens = es.agroseguro.contratacion.datosVariables.Densidad.Factory.newInstance();
				dens.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDens(dens);
				break;
			case 110:
				//DESTINO
				es.agroseguro.contratacion.datosVariables.Destino dest = es.agroseguro.contratacion.datosVariables.Destino.Factory.newInstance();
				dest.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDest(dest);
				break;
			case 111:
				//EDAD
				es.agroseguro.contratacion.datosVariables.Edad edad = es.agroseguro.contratacion.datosVariables.Edad.Factory.newInstance();
				edad.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdad(edad);
				break;
			case 112:
				//FECHA RECOLECCION
				es.agroseguro.contratacion.datosVariables.FechaRecoleccion fRecol = es.agroseguro.contratacion.datosVariables.FechaRecoleccion.Factory.newInstance();
				GregorianCalendar gcFRecol = new GregorianCalendar();
				gcFRecol.setTime(sdf.parse(valor.toString()));
				fRecol.setValor(gcFRecol);
				
				datosVariables.setFecRecol(fRecol);
				break;
			case 113:
				//FECHA SIEMBRA/TRASPLANTE
				es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante fSiembraTransplante = es.agroseguro.contratacion.datosVariables.FechaSiembraTrasplante.Factory.newInstance();
				GregorianCalendar gcFSiemb = new GregorianCalendar();
				gcFSiemb.setTime(sdf.parse(valor.toString()));
				fSiembraTransplante.setValor(gcFSiemb);
				
				datosVariables.setFecSiemTrasp(fSiembraTransplante);
				break;
			case 114:
				//INDIC.GASTOS SALVAMENTO
				es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento gastSalv = es.agroseguro.contratacion.datosVariables.IndicadorGastosSalvamento.Factory.newInstance();
				gastSalv.setValor(valor.toString());
				
				datosVariables.setIndGastSalv(gastSalv);
				break;
			case 124:
				//MEDIDA PREVENTIVA
				List<Integer> listaMedPrev = new ArrayList<Integer>();
				
				for (String val : valor.toString().split(" ")){
					listaMedPrev.add(Integer.parseInt(val));
				}
				
				
				es.agroseguro.contratacion.datosVariables.MedidaPreventiva medPrev = es.agroseguro.contratacion.datosVariables.MedidaPreventiva.Factory.newInstance();
				medPrev.setValor(listaMedPrev);
				
				datosVariables.setMedPrev(medPrev);
				break;
			case 767:
				//METROS CUADRADOS
				es.agroseguro.contratacion.datosVariables.MetrosCuadrados met2 = es.agroseguro.contratacion.datosVariables.MetrosCuadrados.Factory.newInstance();
				met2.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet2(met2);
				break;
			case 766:
				//METROS LINEALES
				es.agroseguro.contratacion.datosVariables.MetrosLineales met = es.agroseguro.contratacion.datosVariables.MetrosLineales.Factory.newInstance();
				met.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet(met);
				break;
			case 617:
				//NUM ANHOS DESDE PODA
				es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda nadp = es.agroseguro.contratacion.datosVariables.NumeroAniosDesdePoda.Factory.newInstance();
				nadp.setValor((new BigDecimal(valor.toString())).intValue());
				
				datosVariables.setNadp(nadp);
				break;
			case 117:
				//NUMERO UNIDADES
				es.agroseguro.contratacion.datosVariables.NumeroUnidades numUds = es.agroseguro.contratacion.datosVariables.NumeroUnidades.Factory.newInstance();
				numUds.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNumUnid(numUds);
				break;
			case 133:
				//PRACTICA CULTURAL
				es.agroseguro.contratacion.datosVariables.PracticaCultural praCul = es.agroseguro.contratacion.datosVariables.PracticaCultural.Factory.newInstance();
				praCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPraCult(praCul);
				break;
			case 131:
				//SISTEMA CONDUCCION
				es.agroseguro.contratacion.datosVariables.SistemaConduccion sCond = es.agroseguro.contratacion.datosVariables.SistemaConduccion.Factory.newInstance();
				sCond.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCond(sCond);
				break;
			case 123:
				//SISTEMA CULTIVO
				es.agroseguro.contratacion.datosVariables.SistemaCultivo sCul = es.agroseguro.contratacion.datosVariables.SistemaCultivo.Factory.newInstance();
				sCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCult(sCul);
				break;
			case 616:
				//SISTEMA PRODUCCION
				es.agroseguro.contratacion.datosVariables.SistemaProduccion sProd = es.agroseguro.contratacion.datosVariables.SistemaProduccion.Factory.newInstance();
				sProd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProd(sProd);
				break;
			case 621:
				//SISTEMA PROTECCION
				es.agroseguro.contratacion.datosVariables.SistemaProteccion sProt = es.agroseguro.contratacion.datosVariables.SistemaProteccion.Factory.newInstance();
				sProt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProt(sProt);
				break;
			case 116:
				//TIPO MARCO PLANTACION
				es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion tmp = es.agroseguro.contratacion.datosVariables.TipoMarcoPlantacion.Factory.newInstance();
				tmp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMcoPlant(tmp);
				break;
			case 173:
				//TIPO PLANTACION
				es.agroseguro.contratacion.datosVariables.TipoPlantacion tp = es.agroseguro.contratacion.datosVariables.TipoPlantacion.Factory.newInstance();
				tp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipPlant(tp);
				break;
			case 132:
				//TIPO SUB.DECLARADA PARCEL
				List listaTsdp = new ArrayList();
				listaTsdp.add(valor);
				es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela tsdp = es.agroseguro.contratacion.datosVariables.TipoSubvencionDeclaradaParcela.Factory.newInstance();
				tsdp.setValor(listaTsdp);
				
				datosVariables.setTipSubDecPar(tsdp);
				break;
			case 768:
				//VALOR FIJO
				es.agroseguro.contratacion.datosVariables.ValorFijo vf = es.agroseguro.contratacion.datosVariables.ValorFijo.Factory.newInstance();
				vf.setValor(new BigDecimal(valor.toString()));
				
				datosVariables.setValFij(vf);
				break;
			case 778:
				//Tipo instalacion
				es.agroseguro.contratacion.datosVariables.TipoInstalacion tipInst = es.agroseguro.contratacion.datosVariables.TipoInstalacion.Factory.newInstance();
				tipInst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipInst(tipInst);
				break;
			case 873:
				// Material Cubierta
				es.agroseguro.contratacion.datosVariables.MaterialCubierta mCub = es.agroseguro.contratacion.datosVariables.MaterialCubierta.Factory.newInstance();
				mCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatCubi(mCub);
				break;
			case 874:
				// Edad Cubierta
				es.agroseguro.contratacion.datosVariables.EdadCubierta eCub = es.agroseguro.contratacion.datosVariables.EdadCubierta.Factory.newInstance();
				eCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadCubi(eCub);
				break;
			case 875:
				// Material Estructuras
				es.agroseguro.contratacion.datosVariables.MaterialEstructura mEst = es.agroseguro.contratacion.datosVariables.MaterialEstructura.Factory.newInstance();
				mEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatEstr(mEst);
				break;
			case 876:
				// Edad Estrucutra
				es.agroseguro.contratacion.datosVariables.EdadEstructura eEst = es.agroseguro.contratacion.datosVariables.EdadEstructura.Factory.newInstance();
				eEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadEstr(eEst);
				break;
			case 879:
				// Codigo Certificado
				es.agroseguro.contratacion.datosVariables.CodigoCertificado cc = es.agroseguro.contratacion.datosVariables.CodigoCertificado.Factory.newInstance();
				cc.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodCert(cc);
				break;
			case 752:
				// Tipo terreno
				es.agroseguro.contratacion.datosVariables.TipoTerreno tt = es.agroseguro.contratacion.datosVariables.TipoTerreno.Factory.newInstance();
				tt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipTer(tt);
				break;
			case 753:
				// Tipo masa
				es.agroseguro.contratacion.datosVariables.TipoMasa tm = es.agroseguro.contratacion.datosVariables.TipoMasa.Factory.newInstance();
				tm.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMas(tm);
				break;
			case 754:
				// Pendiente
				es.agroseguro.contratacion.datosVariables.Pendiente p = es.agroseguro.contratacion.datosVariables.Pendiente.Factory.newInstance();
				p.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPend(p);
				break;
			case 944:
				// Num anhos desde descorche
				es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche nadd = es.agroseguro.contratacion.datosVariables.NumeroAniosDesdeDescorche.Factory.newInstance();
				nadd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNadd(nadd);
				break;
			//FIN DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			default:
				//No hacemos nada
				break;
			
		}
		
	}
	/***** Tatiana (16.06.2020) **** Fin  *****/
	
	
	public static Poliza transformarCPL(AnexoModificacion anexo,com.rsi.agp.dao.tables.copy.Poliza copy,
						Map<BigDecimal,List<String>> listaDatosVariables, List<BigDecimal> listaCPM, 
						List<BigDecimal> codsConceptos,Usuario usuario) throws ValidacionAnexoModificacionException {
		
		Poliza polizaAS = Poliza.Factory.newInstance();
		
		GregorianCalendar gc = new GregorianCalendar();
		List<String> lstAsunto = Arrays.asList(anexo.getAsunto().split(";"));

		Colectivo c = Colectivo.Factory.newInstance();
		Entidad entidad = Entidad.Factory.newInstance();
		
		polizaAS.setNifAsegurado(anexo.getPoliza().getAsegurado().getNifcif());
		if (copy != null){
			/* SONAR Q */
			polizaAS = informarPolizaDeCopy(copy, polizaAS, entidad, lstAsunto,c, gc, anexo, usuario);
			// FIN SONAR Q */
		}
		else{
			//Rellenamos el anexo con los datos de la poliza
			polizaAS.setPlan(anexo.getPoliza().getLinea().getCodplan().intValue());
			polizaAS.setLinea(anexo.getPoliza().getLinea().getCodlinea().intValue());
			polizaAS.setReferencia(anexo.getPoliza().getReferencia());
			if (anexo.getPoliza().getDc() != null)
				polizaAS.setDigitoControl(anexo.getPoliza().getDc().intValue());
			polizaAS.setFechaFirmaDocumento(gc);
			polizaAS.setAsunto(lstAsunto);
			polizaAS.setModulo(String.format("%-5s", anexo.getPoliza().getCodmodulo().trim()));
			
			//COLECTIVO
			c.setDigitoControl(Integer.parseInt(anexo.getPoliza().getColectivo().getDc()));
			c.setNif(anexo.getPoliza().getColectivo().getTomador().getId().getCiftomador());
			c.setReferencia(anexo.getPoliza().getColectivo().getIdcolectivo());
			polizaAS.setColectivo(c);
			//FIN COLECTIVO
			
			//ENTIDAD
			entidad.setCodigo(Constants.ENTIDAD_C616);
			String codInterno = String.format("%4s", anexo.getPoliza().getAsegurado().getEntidad().getCodentidad().toString()) 
								+ String.format("%4s", usuario.getOficina().getId().getCodoficina().toString());
			entidad.setCodigoInterno(String.format("%12s", codInterno));
			//GASTOS
			if (anexo.getPoliza().isPlanMayorIgual2015()) {
				es.agroseguro.seguroAgrario.modificacion.Gastos gastos = setGastos (anexo);
				entidad.setGastos(gastos);
			}
			polizaAS.setEntidad(entidad);
			//FIN ENTIDAD
		}
		
		//INICIO DATOS QUE PUEDEN SER MODIFICADOS EN EL ANEXO
		
		//ASEGURADO
		if (!StringUtils.nullToString(anexo.getNomaseg()).equals("") || !StringUtils.nullToString(anexo.getRazsocaseg()).equals("") ){
			//Si se ha modificado el asegurado, lo anhado al anexo de modificacion
			Asegurado a = Asegurado.Factory.newInstance();
			a.setNif(anexo.getPoliza().getAsegurado().getNifcif());
			if (anexo.getPoliza().getAsegurado().getTipoidentificacion().equals("CIF")){
				RazonSocial rs = RazonSocial.Factory.newInstance();
				rs.setRazonSocial(anexo.getRazsocaseg());
				a.setRazonSocial(rs);
			}else{
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
				
				nom.setNombre(anexo.getNomaseg());
				nom.setApellido1(anexo.getApel1aseg());
				if (!"".equals(StringUtils.nullToString(anexo.getApel2aseg()).trim()))
					nom.setApellido2(anexo.getApel2aseg());
				
				a.setNombreApellidos(nom);
			}
			
			
			/* SONAR Q */
			Direccion dir = informarDireccion(anexo);
			DatosContacto dContacto = informardContacto(anexo);
			/* FIN SONAR Q */
			
			a.setDireccion(dir);
			a.setDatosContacto(dContacto);
			
			polizaAS.setAsegurado(a);
		}
		//FIN ASEGURADO
		
		//OBJETOS ASEGURADOS
		if (anexo.getParcelas() != null && anexo.getParcelas().size() > 0){
			ObjetosAsegurados objAseg = ObjetosAsegurados.Factory.newInstance();
			Parcela[] parcelasArr = getParcelasCPLArray(anexo, copy, listaDatosVariables, listaCPM,codsConceptos);
			if (parcelasArr != null){
				objAseg.setParcelaArray(parcelasArr);
				polizaAS.setObjetosAsegurados(objAseg);
			}
		}
		//FIN OBJETOS ASEGURADOS
		
		
		//FIN DATOS QUE PUEDEN SER MODIFICADOS EN EL ANEXO
		
		return polizaAS;
	}

	
	private static Gastos setGastos(AnexoModificacion anexo) {
		es.agroseguro.seguroAgrario.modificacion.Gastos gastos = 
				es.agroseguro.seguroAgrario.modificacion.Gastos.Factory.newInstance();
		gastos.setAdministracion(anexo.getPctadministracion());
		gastos.setAdquisicion(anexo.getPctadquisicion());
		gastos.setComisionMediador(anexo.getPctcomisionmediador());
		return gastos;
	}

	/**
	 * Metodo para obtener un array de parcelas para enviar a Agroseguro a partir de una coleccion de parcelas de poliza
	 * @param poliza Poliza de la aplicacion
	 * @param cp Comparativa actual.
	 * @param listaCPM Lista de CPM permitidos para este AM
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionAnexoModificacionException 
	 */
	private static Parcela[] getParcelasArray(AnexoModificacion anexo, Map<BigDecimal, List<String>> listaDatosVariables, 
											  List<BigDecimal> listaCPM,  
											  List<BigDecimal> codsConceptos) throws Exception {
		List<Parcela> lstParcelas = new ArrayList<Parcela>();
		
		for (com.rsi.agp.dao.tables.anexo.Parcela parcela : anexo.getParcelas()){
			
				if (parcela.getTipomodificacion() != null){
					Parcela p = Parcela.Factory.newInstance();
					
					p.setHoja(parcela.getHoja().intValue());
					p.setNumero(parcela.getNumero().intValue());
					p.setNombre(parcela.getNomparcela());
					
					/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
					
					/* Incluir el nuevo atributo parcelaAgricola en la etiqueta Parcela */ 
					/* en el objeto es.agroseguro.SeguroAgrario.*.Parcela no existe el objeto Parcela. */
					/*p.setParcelaAgricola(StringUtils.nullToString(parcela.getParcAgricola()));*/
					/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */

					p.setTipoModificacion(parcela.getTipomodificacion()+"");
					
					if (parcela.getCodprovsigpac() != null){
						//rellenamos el sigpac
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
					else if (!StringUtils.nullToString(parcela.getParcela_1()).equals("")){
						//rellenamos la identificacion catastral
						IdentificacionCatastral idCat = IdentificacionCatastral.Factory.newInstance();
						idCat.setParcela(parcela.getParcela_1());
						idCat.setPoligono(parcela.getPoligono());
						p.setIdentificacionCatastral(idCat);
					}
					
					Ambito ambito = Ambito.Factory.newInstance();
					ambito.setComarca(parcela.getCodcomarca().intValue());
					ambito.setProvincia(parcela.getCodprovincia().intValue());
					ambito.setSubtermino(parcela.getSubtermino() + "");
					ambito.setTermino(parcela.getCodtermino().intValue());
					
					/* P00077429 ** MODIF TAM (27/01/2021) ** Inicio */
					/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
					if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
						p.setUbicacion(ambito);
					}
					/* P00077429 ** MODIF TAM (27/01/2021) ** Fin */

					
					//Cosecha
					Cosecha cosecha = Cosecha.Factory.newInstance();
					cosecha.setCultivo(parcela.getCodcultivo().intValue());
					cosecha.setVariedad(parcela.getCodvariedad().intValue());
					
					CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
					CapitalAsegurado[] capitales = new CapitalAsegurado[parcela.getCapitalAsegurados().size()];
					int cntCapAseg = 0;
					for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado ca : parcela.getCapitalAsegurados()){
						CapitalAsegurado c = CapitalAsegurado.Factory.newInstance();
						c.setPrecio(ca.getPrecio());
						c.setProduccion(ca.getProduccion().intValue());
						c.setSuperficie(ca.getSuperficie());
						c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
						//Obtenemos los datos variables de parcela y de coberturas a nivel de parcela
						c.setDatosVariablesArray(getDatosVariablesParcela(ca.getCapitalDTSVariables(), ca.getId(), 
								listaDatosVariables, listaCPM, codsConceptos, parcela));
						capitales[cntCapAseg] = c;
						cntCapAseg++;
					}
					
					capAseg.setCapitalAseguradoArray(capitales);
					cosecha.setCapitalesAsegurados(capAseg);
					p.setCosecha(cosecha);
					
					//SIGPE 7975 No es necesario montar los datos anteriores cuando el anexo se tramita por cupon en lugar de FTP.
					//Actualmente todo es por cupon 
					// Si la parcela corresponde a una modificacion o a una baja, se cargan los datos antes de la modificacion
//					if (parcela.getTipomodificacion() != null && !parcela.getTipomodificacion().equals('A')) {
//											
//						if (copy == null) {
//							// Se obtiene la parcela de p贸liza asociada a la del anexo
//							com.rsi.agp.dao.tables.poliza.Parcela parPoliza = parcelaDao.getParcelaPoliza(parcela.getParcela().getIdparcela());
//							// Se monta el objeto de DatosAntesModif a partir de dicha parcela de p贸liza
//							p.setDatosAntesModificacion (cargaDatosAntesModifPoliza (parPoliza));
//						}
//						else {
//							// Se obtiene la parcela de copy asociada a la del anexo
//							com.rsi.agp.dao.tables.copy.Parcela parCopy = parcelaDao.getParcelaCopy(parcela.getIdcopyparcela());
//							// Se monta el objeto de DatosAntesModif a partir de dicha parcela de copy
//							p.setDatosAntesModificacion (cargaDatosAntesModifCopy (parCopy));	
//						}
//						
//					}
					
					lstParcelas.add(p);
				}
		}
		
		Parcela[] parc = new Parcela[lstParcelas.size()];
		if (lstParcelas.size() > 0){
			int cntParcelas = 0;
			for (Parcela par : lstParcelas){
				parc[cntParcelas] = par;
				cntParcelas++;
			}
		}else{
			parc = null;
		}
		
		return parc;
	}

	
	//Version light
	private static List<es.agroseguro.contratacion.parcela.ParcelaDocument> getParcelasArrayParaEnvioWSRecalculo(AnexoModificacion anexo,
			Map<BigDecimal, List<String>> listaDatosVariables,
			List<BigDecimal> listaCPM,
			List<BigDecimal> codsConceptos, Set<Long> colIdParcelasAnexoFiltro,
			boolean calcRendOriHist)
			throws Exception {
		
		logger.debug("AnexoModificacionTransformer - getParcelasArrayParaEnvioWSRecalculo [INIT]");

		
		List<es.agroseguro.contratacion.parcela.ParcelaDocument> lstParcelas = new ArrayList<es.agroseguro.contratacion.parcela.ParcelaDocument>();
		
		for (com.rsi.agp.dao.tables.anexo.Parcela parcela : anexo.getParcelas()) {

			logger.warn("Id parcela: + " + parcela.getId());
			
			if (colIdParcelasAnexoFiltro == null
					|| (colIdParcelasAnexoFiltro != null && colIdParcelasAnexoFiltro
							.contains(parcela.getId()))) {

					es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela p = es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela.Factory.newInstance();
					es.agroseguro.contratacion.parcela.ParcelaDocument pd = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.newInstance(); 
					 

					p.setHoja(parcela.getHoja().intValue());
					p.setNumero(parcela.getNumero().intValue());
					p.setNombre(parcela.getNomparcela());
					
					/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
					/* Incluir el nuevo atributo parcelaAgricola en la etiqueta Parcela */
					/* DE MOMENTO Y POR PETICI覰 DE RGA (correo de Antonio del 01/02/2022) DE MOMENTO NO SE ENV虯 EN NING贜 XML EL NUEVO CAMPO DE PARCELAAGRICOLA */
					/*if (parcela.getParcAgricola() != null) {
						p.setParcelaAgricola(parcela.getParcAgricola());	
					}*/
					/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */					

					if (parcela.getCodprovsigpac() != null) {
						// rellenamos el sigpac
						SIGPAC sigpac = SIGPAC.Factory.newInstance();
						sigpac.setAgregado(parcela.getAgrsigpac().intValue());
						sigpac.setParcela(parcela.getParcelasigpac().intValue());
						sigpac.setPoligono(parcela.getPoligonosigpac()
								.intValue());
						sigpac.setProvincia(parcela.getCodprovsigpac()
								.intValue());
						sigpac.setRecinto(parcela.getRecintosigpac().intValue());
						sigpac.setTermino(parcela.getCodtermsigpac().intValue());
						sigpac.setZona(parcela.getZonasigpac().intValue());
						p.setSIGPAC(sigpac);
					} else if (!StringUtils
							.nullToString(parcela.getParcela_1()).equals("")) {
						// rellenamos la identificacion catastral
						IdentificacionCatastral idCat = IdentificacionCatastral.Factory
								.newInstance();
						idCat.setParcela(parcela.getParcela_1());
						idCat.setPoligono(parcela.getPoligono());
						// TAM (16.06.2020)
						//p.setIdentificacionCatastral(idCat);
					}

					Ambito ambito = Ambito.Factory.newInstance();
					ambito.setComarca(parcela.getCodcomarca().intValue());
					ambito.setProvincia(parcela.getCodprovincia().intValue());
					ambito.setSubtermino(parcela.getSubtermino() + "");
					ambito.setTermino(parcela.getCodtermino().intValue());
					
					/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
					/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
					if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
						p.setUbicacion(ambito);
					}
					/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */		
					

					// Cosecha
					es.agroseguro.contratacion.parcela.Cosecha cosecha = es.agroseguro.contratacion.parcela.Cosecha.Factory.newInstance();
					cosecha.setCultivo(parcela.getCodcultivo().intValue());
					cosecha.setVariedad(parcela.getCodvariedad().intValue());

					es.agroseguro.contratacion.parcela.CapitalesAsegurados capAseg = es.agroseguro.contratacion.parcela.CapitalesAsegurados.Factory.newInstance();
					
					
					/* SONAR Q */
					es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = informarCapitales(parcela, listaDatosVariables, listaCPM, codsConceptos, calcRendOriHist);
					/* FIN SONAR Q */
					capAseg.setCapitalAseguradoArray(capitales);
					cosecha.setCapitalesAsegurados(capAseg);
					p.setCosecha(cosecha);
					
					pd.setParcela(p); 
					lstParcelas.add(pd); 
					
			}else{
				logger.warn("else");
			}
		}
		
		logger.warn("Tamanho lista parcelas" + lstParcelas.size());
		
		return lstParcelas;
	}
	
	
	/**
	 * Carga los datos de la parcela anteriores a la modificacion obteniendolos de la copy
	 * @param parCopy Parcela de copy asociada a la del anexo
	 * @return Objeto que encapsula los datos antes de la modificacion
	 */
	private static DatosAntesModificacion cargaDatosAntesModifCopy(com.rsi.agp.dao.tables.copy.Parcela parCopy) {
		
		DatosAntesModificacion dam = DatosAntesModificacion.Factory.newInstance();
		
		if (parCopy == null) return dam;
		
		// Se cargan los datos de la parcela original en el objeto 'DatosAntesModificacion'
		// SIGPAC o Identificacion Catastral
		if (parCopy.getCodprovsigpac() != null){
			//rellenamos el sigpac
			SIGPAC sigpac = SIGPAC.Factory.newInstance();
			sigpac.setAgregado(parCopy.getAgrsigpac().intValue());
			sigpac.setParcela(parCopy.getParcelasigpac().intValue());
			sigpac.setPoligono(parCopy.getPoligonosigpac().intValue());
			sigpac.setProvincia(parCopy.getCodprovsigpac().intValue());
			sigpac.setRecinto(parCopy.getRecintosigpac().intValue());
			sigpac.setTermino(parCopy.getCodtermsigpac().intValue());
			sigpac.setZona(parCopy.getZonasigpac().intValue());								
			dam.setSIGPAC(sigpac);
		}
		else if (!StringUtils.nullToString(parCopy.getParcela()).equals("")){
			//rellenamos la identificacion catastral
			IdentificacionCatastral idCat = IdentificacionCatastral.Factory.newInstance();
			idCat.setParcela(parCopy.getParcela());
			idCat.setPoligono(parCopy.getPoligono());
			dam.setIdentificacionCatastral(idCat);
		}
		// Cultivo
		dam.setCultivo(parCopy.getCodcultivo().intValue());
		
		// Superficie
		// Se suman las superficies de los capitales asegurados de la parcela y se guarda en el objeto
		BigDecimal capAsegTot = new BigDecimal(0);
		for (com.rsi.agp.dao.tables.copy.CapitalAsegurado capitalAsegurado : parCopy.getCapitalAsegurados()) {								
			capAsegTot = capAsegTot.add(capitalAsegurado.getSuperficie());
		}
		
		dam.setSuperficie(capAsegTot);
				
		
		return dam;
	}
	
	
	/**
	 * Carga los datos de la parcela anteriores a la modificacion obteniendolos de la poliza del anexo
	 * @param copy Poliza asociada al anexo
	 * @param parcela Parcela que se esta tratando
	 * @return Objeto que encapsula los datos antes de la modificacion
	 */
	private static DatosAntesModificacion cargaDatosAntesModifPoliza(com.rsi.agp.dao.tables.poliza.Parcela parPoliza) {
		
		DatosAntesModificacion dam = DatosAntesModificacion.Factory.newInstance();
		
		if (parPoliza == null) return dam;

		// Se cargan los datos de la parcela original en el objeto 'DatosAntesModificacion'
		// SIGPAC o Identificacion Catastral
		if (parPoliza.getCodprovsigpac() != null){
			//rellenamos el sigpac
			SIGPAC sigpac = SIGPAC.Factory.newInstance();
			sigpac.setAgregado(parPoliza.getAgrsigpac().intValue());
			sigpac.setParcela(parPoliza.getParcelasigpac().intValue());
			sigpac.setPoligono(parPoliza.getPoligonosigpac().intValue());
			sigpac.setProvincia(parPoliza.getCodprovsigpac().intValue());
			sigpac.setRecinto(parPoliza.getRecintosigpac().intValue());
			sigpac.setTermino(parPoliza.getCodtermsigpac().intValue());
			sigpac.setZona(parPoliza.getZonasigpac().intValue());								
			dam.setSIGPAC(sigpac);
		}
		else if (!StringUtils.nullToString(parPoliza.getParcela()).equals("")){
			//rellenamos la identificacion catastral
			IdentificacionCatastral idCat = IdentificacionCatastral.Factory.newInstance();
			idCat.setParcela(parPoliza.getParcela());
			idCat.setPoligono(parPoliza.getPoligono());
			dam.setIdentificacionCatastral(idCat);
		}
		// Cultivo
		dam.setCultivo(parPoliza.getCodcultivo().intValue());
		
		// Superficie
		// Se suman las superficies de los capitales asegurados de la parcela y se guarda en el objeto
		BigDecimal capAsegTot = new BigDecimal(0);
		for (com.rsi.agp.dao.tables.poliza.CapitalAsegurado capitalAsegurado : parPoliza.getCapitalAsegurados()) {								
			capAsegTot = capAsegTot.add(capitalAsegurado.getSuperficie());
		}
		
		dam.setSuperficie(capAsegTot);
		
		return dam;
	}
	
	/**
	 * Metodo para obtener un array de parcelas de la complementaria para enviar a Agroseguro a partir de una coleccion de parcelas de poliza
	 * @param poliza Poliza de la aplicacion
	 * @param cp Comparativa actual.
	 * @return Array de parcelas para enviar a Agroseguro
	 * @throws ValidacionAnexoModificacionException 
	 */
	private static Parcela[] getParcelasCPLArray(AnexoModificacion anexo, com.rsi.agp.dao.tables.copy.Poliza copy, 
			Map<BigDecimal, List<String>> listaDatosVariables, List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos) throws ValidacionAnexoModificacionException {
		
		List<Parcela> lstParcelas = new ArrayList<Parcela>();
		
		for (com.rsi.agp.dao.tables.anexo.Parcela parcela : anexo.getParcelas()){
			
			if (parcela.getTipomodificacion() != null){
				Parcela p = Parcela.Factory.newInstance();
				
				p.setHoja(parcela.getHoja().intValue());
				p.setNumero(parcela.getNumero().intValue());
				p.setNombre(parcela.getNomparcela());
				p.setTipoModificacion(parcela.getTipomodificacion()+"");
				
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir el nuevo atributo parcelaAgricola en la etiqueta Parcela */ 
				/* en el objeto es.agroseguro.seguroAgrario.*.Parcela no existe el objeto Parcela. */
				/*p.setParcelaAgricola(StringUtils.nullToString(parcela.getParcAgricola()));*/
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
				
				if (parcela.getCodprovsigpac() != null){
					//rellenamos el sigpac
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
				else if (!StringUtils.nullToString(parcela.getParcela_1()).equals("")){
					//rellenamos la identificacion catastral
					IdentificacionCatastral idCat = IdentificacionCatastral.Factory.newInstance();
					idCat.setParcela(parcela.getParcela_1());
					idCat.setPoligono(parcela.getPoligono());
					p.setIdentificacionCatastral(idCat);
				}
				
				Ambito ambito = Ambito.Factory.newInstance();
				ambito.setComarca(parcela.getCodcomarca().intValue());
				ambito.setProvincia(parcela.getCodprovincia().intValue());
				ambito.setSubtermino(parcela.getSubtermino() + "");
				ambito.setTermino(parcela.getCodtermino().intValue());
				
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
				if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
					p.setUbicacion(ambito);
				}
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */		
				
				
				//Cosecha
				Cosecha cosecha = Cosecha.Factory.newInstance();
				cosecha.setCultivo(parcela.getCodcultivo().intValue());
				cosecha.setVariedad(parcela.getCodvariedad().intValue());
				
				CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
				/* SONAR Q */
				List<CapitalAsegurado> capitales = informarCapAsegCPL(parcela,listaDatosVariables, listaCPM, codsConceptos );
				/* FIN SONAR Q */
				
				capAseg.setCapitalAseguradoArray(capitales.toArray(new CapitalAsegurado[capitales.size()]));
				cosecha.setCapitalesAsegurados(capAseg);
				p.setCosecha(cosecha);
				
				// Si la parcela corresponde a una modificacion o a una baja, se cargan los datos antes de la modificacion
				if (parcela.getTipomodificacion() != null && !parcela.getTipomodificacion().equals('A')) {
					if (copy == null) {
						// Se obtiene la parcela de p贸liza asociada a la del anexo
						com.rsi.agp.dao.tables.poliza.Parcela parPoliza = parcelaDao.getParcelaPoliza(parcela.getParcela().getIdparcela());
						// Se monta el objeto de DatosAntesModif a partir de dicha parcela de p贸liza
						p.setDatosAntesModificacion (cargaDatosAntesModifPoliza (parPoliza));
					}
					else {
						// Se obtiene la parcela de copy asociada a la del anexo
						com.rsi.agp.dao.tables.copy.Parcela parCopy = parcelaDao.getParcelaCopy(parcela.getIdcopyparcela());
						// Se monta el objeto de DatosAntesModif a partir de dicha parcela de copy
						p.setDatosAntesModificacion (cargaDatosAntesModifCopy (parCopy));	
					}
				}
				
				lstParcelas.add(p);
			}
			
		}
		/* SONAR Q */
		Parcela[] parc = obtenerParc(lstParcelas);
		/* FIN SONAR Q */
		
		return parc;
	}

	/**
	 * Metodo para obtener los datos variables asociados a cada capital asegurado de una parcela
	 * @param datoVariableParcelas Conjunto de datos variables asociados a un capital asegurado
	 * @return
	 */
	private static DatosVariables[] getDatosVariablesParcela(Set<CapitalDTSVariable> datoVariableParcelas, 
			Long idCapitalAsegurado, Map<BigDecimal, List<String>> listaDatosVariables, List<BigDecimal> listaCPM, 
			List<BigDecimal> codsConceptos, com.rsi.agp.dao.tables.anexo.Parcela parcela) throws Exception{
		
		
		DatosVariables datosVariablesAlta = null;
		DatosVariables datosVariablesModif = null;
		DatosVariables datosVariablesBaja = null;
		Map<String, Object> params = new HashMap<String, Object>();
		int contador = 0;
		
		for (CapitalDTSVariable dvp : datoVariableParcelas){
			if (codsConceptos.contains(dvp.getCodconcepto())){
				if (listaDatosVariables.containsKey(dvp.getCodconcepto())){
					//Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());
					for (String cad: auxDatVar){
						String[] auxValores = cad.split("#");
						Long auxIdCapAseg = new Long(auxValores[0]);
						if (auxIdCapAseg.equals(idCapitalAsegurado)){
							try {

								/*SONAR Q */
								params = setDatoVariableRiesgAltModBaj(dvp, auxValores, datosVariablesAlta, datosVariablesModif, datosVariablesBaja, contador, listaCPM, parcela);
								
								datosVariablesAlta = (DatosVariables) params.get("datosVariablesAlta");
								datosVariablesModif = (DatosVariables) params.get("datosVariablesModif");
								datosVariablesBaja = (DatosVariables) params.get("datosVariablesBaja");
								
								contador = (Integer) params.get("contador");
								/*SONAR Q */

							} catch (ParseException e) {
								throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
							} catch (NumberFormatException e) {
								throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
							} catch (Exception e) {
								throw new Exception(ERR_ASIG);
							}
						}
					}
				}else{
					
					try {
						/* SONAR Q */
						params = setDatoVariableAltModBaj(dvp, datosVariablesAlta, datosVariablesModif, datosVariablesBaja, contador, listaCPM, parcela);
						
						datosVariablesAlta = (DatosVariables) params.get("datosVariablesAlta");
						datosVariablesModif = (DatosVariables) params.get("datosVariablesModif");
						datosVariablesBaja = (DatosVariables) params.get("datosVariablesBaja");
						
						contador = (Integer) params.get("contador");
						/*SONAR Q */
						
						/* FIN SONAR Q */
					} catch (ParseException e) {
						logger.error(ERR_ASIG, e);
						throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
					} catch (NumberFormatException e) {
						logger.error(ERR_ASIG, e);
						throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
					} catch (Exception e) {
						logger.error(ERR_ASIG, e);
						throw new Exception(ERR_ASIG);
					}
				}
			}
		}
		/* SONAR Q */
		DatosVariables[] arrDatVar = obtenerArrDatVar(datosVariablesAlta, datosVariablesModif, datosVariablesBaja, contador);
		/* FIN SONAR Q */
		
		return arrDatVar;
	}
	
	
	/**
	 * Metodo para obtener los datos variables asociados a cada capital asegurado de una parcela complementaria
	 * @param datoVariableParcelas Conjunto de datos variables asociados a un capital asegurado
	 * @param character 
	 * @return
	 * @throws ValidacionAnexoModificacionException 
	 */
	private static DatosVariables[] getDatosVariablesParcelaCPL(Set<CapitalDTSVariable> datoVariableParcelas, Long idCapitalAsegurado, 
			Character tipoModif, Map<BigDecimal, List<String>> listaDatosVariables, List<BigDecimal> listaCPM, 
			List<BigDecimal> codsConceptos) throws ValidacionAnexoModificacionException {
		
		DatosVariables datosVariablesAlta = null;
		DatosVariables datosVariablesModif = null;
		DatosVariables datosVariablesBaja = null;
		int contador = 0;
		
		for (CapitalDTSVariable dvp : datoVariableParcelas){
			if (codsConceptos.contains(dvp.getCodconcepto())){
				if (listaDatosVariables.containsKey(dvp.getCodconcepto())){
					//Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());
					for (String cad: auxDatVar){
						String[] auxValores = cad.split("#");
						Long auxIdCapAseg = new Long(auxValores[0]);
						if (auxIdCapAseg.equals(idCapitalAsegurado)){
							try {
								if (tipoModif.equals(new Character('A'))){
									if(datosVariablesAlta == null){
										datosVariablesAlta = DatosVariables.Factory.newInstance();
										datosVariablesAlta.setTipoModificacion("A");
										contador++;
									}
	
									// Comprueba si hay que insertar el CPM como dato variable				
									if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(),listaCPM)) {								
										setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariablesAlta);
									}
								}
								else if (tipoModif.equals(new Character('M'))){
									if(datosVariablesModif == null){
										datosVariablesModif = DatosVariables.Factory.newInstance();
										datosVariablesModif.setTipoModificacion("M");
										contador++;
									}
									// Comprueba si hay que insertar el CPM como dato variable				
									if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(),listaCPM)) {								
										setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariablesModif);
									}
								}
								else if (tipoModif.equals(new Character('B'))){
									if(datosVariablesBaja == null){
										datosVariablesBaja = DatosVariables.Factory.newInstance();
										datosVariablesBaja.setTipoModificacion("B");
										contador++;
									}
									// Comprueba si hay que insertar el CPM como dato variable				
									if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(),listaCPM)) {								
										setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]), auxValores[3], datosVariablesBaja);
									}
								}
							} catch (ParseException e) {
								throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
							} catch (NumberFormatException e) {
								throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
							} catch (Exception e) {
								throw new ValidacionAnexoModificacionException("El valor " + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
							}
						}
					}
				}
				else{
					try {
						if (tipoModif.equals(new Character('A'))){
							if(datosVariablesAlta == null){
								datosVariablesAlta = DatosVariables.Factory.newInstance();
								datosVariablesAlta.setTipoModificacion("A");
								contador++;
							}
							
							BigDecimal codConceptoPpalMod = null;
							if(dvp.getCodconceptoppalmod()!=null){
								codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
							}
							BigDecimal codRiesgoCubierto = null;
							if(dvp.getCodriesgocubierto()!=null){
								codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
							}
							
							// Comprueba si hay que insertar el CPM como dato variable				
							if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(),listaCPM)) {								
								setDatoVariable(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariablesAlta);
							}
						}
						else if (tipoModif.equals(new Character('M'))){
							if(datosVariablesModif == null){
								datosVariablesModif = DatosVariables.Factory.newInstance();
								datosVariablesModif.setTipoModificacion("M");
								contador++;
							}
							
							BigDecimal codConceptoPpalMod = null;
							if(dvp.getCodconceptoppalmod()!=null){
								codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
							}
							BigDecimal codRiesgoCubierto = null;
							if(dvp.getCodriesgocubierto()!=null){
								codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
							}
							
							// Comprueba si hay que insertar el CPM como dato variable				
							if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(),listaCPM)) {								
								setDatoVariable(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariablesModif);
							}
						}
						else if (tipoModif.equals(new Character('B'))){
							if(datosVariablesBaja == null){
								datosVariablesBaja = DatosVariables.Factory.newInstance();
								datosVariablesBaja.setTipoModificacion("B");
								contador++;
							}
							
							BigDecimal codConceptoPpalMod = null;
							if(dvp.getCodconceptoppalmod()!=null){
								codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
							}
							BigDecimal codRiesgoCubierto = null;
							if(dvp.getCodriesgocubierto()!=null){
								codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
							}
							
							// Comprueba si hay que insertar el CPM como dato variable				
							if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(),listaCPM)) {								
								setDatoVariable(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariablesBaja);
							}
						}
					} catch (ParseException e) {
						throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
					} catch (NumberFormatException e) {
						throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
					} catch (Exception e) {
						throw new ValidacionAnexoModificacionException("El valor " + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
					}
				}
			}
		}
		
		DatosVariables[] arrDatVar = new DatosVariables[contador];
		contador = 0;
		if(datosVariablesAlta!= null){
			arrDatVar[contador] = datosVariablesAlta;
			contador++;
		}
		if(datosVariablesModif!= null){
			arrDatVar[contador] = datosVariablesModif;
			contador++;
		}
		if(datosVariablesBaja!= null){
			arrDatVar[contador] = datosVariablesBaja;
			contador++;
		}
		return arrDatVar;
	}
	
	/**
	 * Aunque tiene forma de array, solo debe tener un elemento como maximo o dara error la validacion de la estructura de poliza
	 * para el WS de calculo de rendimientos
	 * @param datoVariableParcelas
	 * @param idCapitalAsegurado
	 * @param listaDatosVariables
	 * @param listaCPM
	 * @param codsConceptos
	 * @param parcela
	 * @return
	 * @throws Exception
	 */
	private static es.agroseguro.contratacion.datosVariables.DatosVariables getDatosVariablesParcelaParaEnvioWSRecalculo(Set<CapitalDTSVariable> datoVariableParcelas, 
			Long idCapitalAsegurado, Map<BigDecimal, List<String>> listaDatosVariables, List<BigDecimal> listaCPM, 
			List<BigDecimal> codsConceptos, com.rsi.agp.dao.tables.anexo.Parcela parcela) throws Exception{
		
		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();

		for (CapitalDTSVariable dvp : datoVariableParcelas){
			if (codsConceptos.contains(dvp.getCodconcepto())){
				if (listaDatosVariables.containsKey(dvp.getCodconcepto())){
					//Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());
					for (String cad: auxDatVar){
						String[] auxValores = cad.split("#");
						Long auxIdCapAseg = new Long(auxValores[0]);
						if (auxIdCapAseg.equals(idCapitalAsegurado)){
							try {
								if (new Character('A').equals(dvp.getTipomodificacion()) || new Character('M').equals(dvp.getTipomodificacion())){
									if(datosVariables == null){
										datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();
									}
									// Comprueba si hay que insertar el CPM como dato variable				
									if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(), listaCPM)) {								
										setDatoVariableRiesgoUnif(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariables);
									}
								}
							} catch (ParseException e) {
								throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
							} catch (NumberFormatException e) {
								throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
							} catch (Exception e) {
								throw new Exception(ERR_ASIG);
							}
						}
					}
				}else{
					try {
						if (new Character('A').equals(dvp.getTipomodificacion()) || new Character('M').equals(dvp.getTipomodificacion())){
							if(datosVariables == null){
								datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();
							}
							
							BigDecimal codConceptoPpalMod = null;
							if(dvp.getCodconceptoppalmod()!=null){
								codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
							}
							BigDecimal codRiesgoCubierto = null;
							if(dvp.getCodriesgocubierto()!=null){
								codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
							}
							
							// Comprueba si hay que insertar el CPM como dato variable				
							if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(), listaCPM)) {								
								setDatoVariableUnif(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariables);
							}
						}
					} catch (ParseException e) {
						logger.error(ERR_ASIG, e);
						throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
					} catch (NumberFormatException e) {
						logger.error(ERR_ASIG, e);
						throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
					} catch (Exception e) {
						logger.error(ERR_ASIG, e);
						throw new Exception(ERR_ASIG);
					}
				}
			}
		}
		
		return datosVariables;
	}
	
	/* ESC-16025 ** MODIF TAM (23.11.2021) ** Inicio */
	/**
	 * Aunque tiene forma de array, solo debe tener un elemento como maximo o dara error la validacion de la estructura de poliza
	 * para el WS de calculo de rendimientos
	 * @param datoVariableParcelas
	 * @param idCapitalAsegurado
	 * @param listaDatosVariables
	 * @param listaCPM
	 * @param codsConceptos
	 * @param parcela
	 * @return
	 * @throws Exception
	 */
	private static es.agroseguro.contratacion.datosVariables.DatosVariables getDatosVariablesParcelaParaEnvioWSRecOrientHist(Set<CapitalDTSVariable> datoVariableParcelas, 
			Long idCapitalAsegurado, Map<BigDecimal, List<String>> listaDatosVariables, List<BigDecimal> listaCPM, 
			List<BigDecimal> codsConceptos, com.rsi.agp.dao.tables.anexo.Parcela parcela) throws Exception{
		
		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();

		for (CapitalDTSVariable dvp : datoVariableParcelas){
			if (codsConceptos.contains(dvp.getCodconcepto())){
				if (listaDatosVariables.containsKey(dvp.getCodconcepto())){
					//Es un dato variable "particular"
					List<String> auxDatVar = listaDatosVariables.get(dvp.getCodconcepto());
					for (String cad: auxDatVar){
						String[] auxValores = cad.split("#");
						Long auxIdCapAseg = new Long(auxValores[0]);
						if (auxIdCapAseg.equals(idCapitalAsegurado)){
							try {
								if(datosVariables == null){
									datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();
								}
								// Comprueba si hay que insertar el CPM como dato variable				
								if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(), listaCPM)) {								
									setDatoVariableRiesgoUnif(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariables);
								}
							} catch (ParseException e) {
								throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
							} catch (NumberFormatException e) {
								throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
							} catch (Exception e) {
								throw new Exception(ERR_ASIG);
							}
						}
					}
				}
				else{
					try {
						if(datosVariables == null){
							datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();
						}
						
						BigDecimal codConceptoPpalMod = null;
						if(dvp.getCodconceptoppalmod()!=null){
							codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
						}
						BigDecimal codRiesgoCubierto = null;
						if(dvp.getCodriesgocubierto()!=null){
							codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
						}
						
						// Comprueba si hay que insertar el CPM como dato variable				
						if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(), listaCPM)) {								
							setDatoVariableUnif(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariables);
						}
					} catch (ParseException e) {
						logger.error(ERR_ASIG, e);
						throw new ValidacionAnexoModificacionException(FECHA + dvp.getValor() + NO_VALIDA + dvp.getCodconcepto());
					} catch (NumberFormatException e) {
						logger.error(ERR_ASIG, e);
						throw new ValidacionAnexoModificacionException(NUMERO + dvp.getValor() + NO_VALIDO + dvp.getCodconcepto());
					} catch (Exception e) {
						logger.error(ERR_ASIG, e);
						throw new Exception(ERR_ASIG);
					}
				}
			}
		}
		
		return datosVariables;
	}
	
	/**
	 * Metodo para obtener los datos variables asociados a las coberturas de un anexo de modificacion
	 * @param poliza Poliza
	 * @param cp Comparativa
	 * @return
	 */
	private static DatosVariables[] getDatosVariablesCobertura(Set<com.rsi.agp.dao.tables.anexo.Cobertura> coberturas, List<BigDecimal> listaCPM) {
		
		int contador = 0;
		DatosVariables datosVariablesAlta = null; 
		DatosVariables datosVariablesModificacion = null;
		DatosVariables datosVariablesBaja = null;
		
		for (com.rsi.agp.dao.tables.anexo.Cobertura cob : coberturas){
			try {
				logger.debug("Dentro de coberturas");
				logger.debug("Valor de codconcepto:"+cob.getCodconcepto());
				/* ESC-14490 ** MODIF TAM (02.07.2021) ** Inicio */
				if ( cob.getTipomodificacion() != null) {
					
					if (cob.getTipomodificacion().equals(new Character('A'))){
						if(datosVariablesAlta==null){
							datosVariablesAlta = DatosVariables.Factory.newInstance();
							datosVariablesAlta.setTipoModificacion("A");
							contador++;
						}					
						
						// Comprueba si hay que insertar el CPM como dato variable				
						if (CoberturasUtils.isCPMPermitido(cob.getCodconceptoppalmod(), cob.getCodconcepto(),listaCPM)) {								
							setDatoVariable(cob.getCodconcepto(), cob.getCodconceptoppalmod(),cob.getCodriesgocubierto(),cob.getCodvalor(), datosVariablesAlta);
						}
					}else if (cob.getTipomodificacion().equals(new Character('M'))){
						if(datosVariablesModificacion==null){
							datosVariablesModificacion = DatosVariables.Factory.newInstance();
							datosVariablesModificacion.setTipoModificacion("M");
							contador++;
						}
						// Comprueba si hay que insertar el CPM como dato variable				
						if (CoberturasUtils.isCPMPermitido(cob.getCodconceptoppalmod(), cob.getCodconcepto(),listaCPM)) {								
							setDatoVariable(cob.getCodconcepto(), cob.getCodconceptoppalmod(),cob.getCodriesgocubierto(),cob.getCodvalor(), datosVariablesModificacion);
						}
					}else if (cob.getTipomodificacion().equals(new Character('B'))){
						if(datosVariablesBaja==null){
							datosVariablesBaja = DatosVariables.Factory.newInstance();
							datosVariablesBaja.setTipoModificacion("B");
							contador++;
						}
						// Comprueba si hay que insertar el CPM como dato variable				
						if (CoberturasUtils.isCPMPermitido(cob.getCodconceptoppalmod(), cob.getCodconcepto(),listaCPM)) {								
							setDatoVariable(cob.getCodconcepto(), cob.getCodconceptoppalmod(),cob.getCodriesgocubierto(), cob.getCodvalor(), datosVariablesBaja);
						}
					}
				}	
			} catch (ParseException e) {
				//Si no se puede parsear alguna de las fechas, pasamos al siguiente dato variable.
			}
		}
		
		DatosVariables[] arrDatVar = new DatosVariables[contador];
		contador = 0;
		if(datosVariablesAlta!= null){
			arrDatVar[contador] = datosVariablesAlta;
			contador++;
		}
		if(datosVariablesModificacion!= null){
			arrDatVar[contador] = datosVariablesModificacion;;
			contador++;
		}
		if(datosVariablesBaja!= null){
			arrDatVar[contador] = datosVariablesBaja;
			contador++;
		}
		
		return arrDatVar;
	}

	/**
	 * Metodo para establecer cada dato variable en el objeto 'DatosVariables'. Sirve tanto para Coberturas como para Capitales Asegurados
	 * @param codconcepto Codigo del concepto a anhadir
	 * @param codconceptoppalmod Concepto principal del modulo del cual depende el codconcepto (si procede). Puede ser null.
	 * @param codriesgocubierto Riesgo cubierto del cual depende el codconcepto (si procede). Puede ser null.
	 * @param valor Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String (Capital Asegurado)
	 * @param datosVariables Estructura donde se van anhadiendo los datos variables.
	 * @throws ParseException 
	 * @throws ParseException Error al parsear fechas.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setDatoVariable(BigDecimal codconcepto,
			BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Object valor,
			DatosVariables datosVariables) throws ParseException, NumberFormatException {
		
		//Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		
		//Comienzo del tratamiento del dato variable
		switch (codconcepto.intValue()){
			//INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 362:
				//% CAPITAL ASEGURADO
				List<PorcentajeCapitalAsegurado> lstCA = Arrays.asList(datosVariables.getCapAsegArray());
				ArrayList<PorcentajeCapitalAsegurado> lstCAA = new ArrayList<es.agroseguro.seguroAgrario.contratacion.datosVariables.PorcentajeCapitalAsegurado>(lstCA);
				
				PorcentajeCapitalAsegurado capital = PorcentajeCapitalAsegurado.Factory.newInstance();
				capital.setCodRCub(codriesgocubierto.intValue());
				capital.setCPMod(codconceptoppalmod.intValue());
				capital.setValor(Integer.parseInt(valor.toString()));
				
				lstCAA.add(capital);
				
				datosVariables.setCapAsegArray(lstCAA.toArray(new PorcentajeCapitalAsegurado[lstCAA.size()]));
				break;
			case 120:
				//% FRANQUICIA
				List<PorcentajeFranquicia> lstF = Arrays.asList(datosVariables.getFranqArray());
				ArrayList<PorcentajeFranquicia> lstFA = new ArrayList<PorcentajeFranquicia>(lstF);
				
				PorcentajeFranquicia f = PorcentajeFranquicia.Factory.newInstance();
				f.setCodRCub(codriesgocubierto.intValue());
				f.setCPMod(codconceptoppalmod.intValue());
				f.setValor(Integer.parseInt(valor.toString()));
				
				lstFA.add(f);
				
				datosVariables.setFranqArray(lstFA.toArray(new PorcentajeFranquicia[lstFA.size()]));
				break;
			case 121:
				//% MINIMO INDEMNIZABLE
				List<PorcentajeMinimoIndemnizable> lstMI = Arrays.asList(datosVariables.getMinIndemArray());
				ArrayList<PorcentajeMinimoIndemnizable> lstMIA = new ArrayList<PorcentajeMinimoIndemnizable>(lstMI);
				
				PorcentajeMinimoIndemnizable m = PorcentajeMinimoIndemnizable.Factory.newInstance();
				m.setCodRCub(codriesgocubierto.intValue());
				m.setCPMod(codconceptoppalmod.intValue());
				m.setValor(Integer.parseInt(valor.toString()));
				
				lstMIA.add(m);
				
				datosVariables.setMinIndemArray(lstMIA.toArray(new PorcentajeMinimoIndemnizable[lstMIA.size()]));
				break;
			case 174:
				//CALCULO INDEMNIZACION
				List<CalculoIndemnizacion> lstCI = Arrays.asList(datosVariables.getCalcIndemArray());
				ArrayList<CalculoIndemnizacion> lstCIA = new ArrayList<CalculoIndemnizacion>(lstCI);
				
				CalculoIndemnizacion c = CalculoIndemnizacion.Factory.newInstance();
				c.setCodRCub(codriesgocubierto.intValue());
				c.setCPMod(codconceptoppalmod.intValue());
				c.setValor(Integer.parseInt(valor.toString()));
				
				lstCIA.add(c);
				
				datosVariables.setCalcIndemArray(lstCIA.toArray(new CalculoIndemnizacion[lstCIA.size()]));
				break;
			case 169:
				//DANHOS CUBIERTOS
				List<DaniosCubiertos> lstDNC = Arrays.asList(datosVariables.getDnCbtosArray());
				ArrayList<DaniosCubiertos> lstDNCA = new ArrayList<DaniosCubiertos>(lstDNC);
				
				DaniosCubiertos dan = DaniosCubiertos.Factory.newInstance();
				dan.setCodRCub(codriesgocubierto.intValue());
				dan.setCPMod(codconceptoppalmod.intValue());
				dan.setValor(valor+"");
				
				lstDNCA.add(dan);
				
				datosVariables.setDnCbtosArray(lstDNCA.toArray(new DaniosCubiertos[lstDNCA.size()]));
				break;
			case 175:
				//GARANTIZADO
				List<Garantizado> lstG = Arrays.asList(datosVariables.getGarantArray());
				ArrayList<Garantizado> lstGA = new ArrayList<Garantizado>(lstG);

				Garantizado garant = Garantizado.Factory.newInstance();
				garant.setCodRCub(codriesgocubierto.intValue());
				garant.setCPMod(codconceptoppalmod.intValue());
				garant.setValor(Integer.parseInt(valor.toString()));
				lstGA.add(garant);
				
				datosVariables.setGarantArray(lstGA.toArray(new Garantizado[lstGA.size()]));
				break;
			case 363:
				//RIESGO CUBIERTO ELEGIDO
				List<RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
				ArrayList<RiesgoCubiertoElegido> lstRCEA = new ArrayList<RiesgoCubiertoElegido>(lstRCE);

				RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
				rCubEleg.setCodRCub(codriesgocubierto.intValue());
				rCubEleg.setCPMod(codconceptoppalmod.intValue());
				
				//Ponemos la S y la N tambien para cuando venga del copy
				if (valor.equals("-1") || valor.equals("S")) rCubEleg.setValor("S");
				else rCubEleg.setValor("N");
				
				lstRCEA.add(rCubEleg);
				
				datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new RiesgoCubiertoElegido[lstRCEA.size()]));
				break;
			case 170:
				//TIPO FRANQUICIA
				List<TipoFranquicia> lstTF = Arrays.asList(datosVariables.getTipFranqArray());
				ArrayList<TipoFranquicia> lstTFA = new ArrayList<TipoFranquicia>(lstTF);
				
				TipoFranquicia tipFranq = TipoFranquicia.Factory.newInstance();
				tipFranq.setCodRCub(codriesgocubierto.intValue());
				tipFranq.setCPMod(codconceptoppalmod.intValue());
				tipFranq.setValor(valor+"");
				
				lstTFA.add(tipFranq);
				
				datosVariables.setTipFranqArray(lstTFA.toArray(new TipoFranquicia[lstTFA.size()]));
				break;
			case 502:
				//TIPO RENDIMIENTO
				List<TipoRendimiento> lstTR = Arrays.asList(datosVariables.getTipRdtoArray());
				ArrayList<TipoRendimiento> lstTRA = new ArrayList<TipoRendimiento>(lstTR);
				
				TipoRendimiento tipRdto = TipoRendimiento.Factory.newInstance();
				tipRdto.setCodRCub(codriesgocubierto.intValue());
				tipRdto.setCPMod(codconceptoppalmod.intValue());
				tipRdto.setValor(Integer.parseInt(valor.toString()));
				
				lstTRA.add(tipRdto);
				
				datosVariables.setTipRdtoArray(lstTRA.toArray(new TipoRendimiento[lstTRA.size()]));
				break;
			//FIN DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
				
			//INICIO DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 144:
				//ALTERNATIVA
				Rotacion alt = Rotacion.Factory.newInstance();
				alt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setRot(alt);
				break;
			case 106:
				//CARACT. EXPLOTACION
				CaracteristicasExplotacion carExlp = CaracteristicasExplotacion.Factory.newInstance();
				carExlp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCarExpl(carExlp);
				break;
			case 618:
				//CICLO CULTIVO
				CicloCultivo ciCul = CicloCultivo.Factory.newInstance();
				ciCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCiCul(ciCul);
				break;
			case 765:
				//CODIGO IGP
				CodigoIGP igp = CodigoIGP.Factory.newInstance();
				igp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodIGP(igp);
				break;
			case 620:
				//CODIGO REDUCCION RDTOS.
				List lista = new ArrayList();
				lista.add(valor);
				CodigoReduccionRdtos redRdto = CodigoReduccionRdtos.Factory.newInstance();
				redRdto.setValor(lista);
				
				datosVariables.setCodRedRdto(redRdto);
				break;
			case 107:
				//DENOMINACION ORIGEN
				DenominacionOrigen denOrig = DenominacionOrigen.Factory.newInstance();
				denOrig.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodDO(denOrig);
				break;
			case 109:
				//DENSIDAD
				Densidad dens = Densidad.Factory.newInstance();
				dens.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDens(dens);
				break;
			case 110:
				//DESTINO
				Destino dest = Destino.Factory.newInstance();
				dest.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setDest(dest);
				break;
			case 111:
				//EDAD
				Edad edad = Edad.Factory.newInstance();
				edad.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdad(edad);
				break;
			case 112:
				//FECHA RECOLECCION
				FechaRecoleccion fRecol = FechaRecoleccion.Factory.newInstance();
				GregorianCalendar gcFRecol = new GregorianCalendar();
				gcFRecol.setTime(sdf.parse(valor.toString()));
				fRecol.setValor(gcFRecol);
				
				datosVariables.setFecRecol(fRecol);
				break;
			case 113:
				//FECHA SIEMBRA/TRASPLANTE
				FechaSiembraTrasplante fSiembraTransplante = FechaSiembraTrasplante.Factory.newInstance();
				GregorianCalendar gcFSiemb = new GregorianCalendar();
				gcFSiemb.setTime(sdf.parse(valor.toString()));
				fSiembraTransplante.setValor(gcFSiemb);
				
				datosVariables.setFecSiemTrasp(fSiembraTransplante);
				break;
			case 114:
				//INDIC.GASTOS SALVAMENTO
				IndicadorGastosSalvamento gastSalv = IndicadorGastosSalvamento.Factory.newInstance();
				gastSalv.setValor(valor.toString());
				
				datosVariables.setIndGastSalv(gastSalv);
				break;
			case 124:
				//MEDIDA PREVENTIVA
				List<Integer> listaMedPrev = new ArrayList<Integer>();
				
				for (String val : valor.toString().split(" ")){
					listaMedPrev.add(Integer.parseInt(val));
				}
				
				
				MedidaPreventiva medPrev = MedidaPreventiva.Factory.newInstance();
				medPrev.setValor(listaMedPrev);
				
				datosVariables.setMedPrev(medPrev);
				break;
			case 767:
				//METROS CUADRADOS
				MetrosCuadrados met2 = MetrosCuadrados.Factory.newInstance();
				met2.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet2(met2);
				break;
			case 766:
				//METROS LINEALES
				MetrosLineales met = MetrosLineales.Factory.newInstance();
				met.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMet(met);
				break;
			case 617:
				//NUM ANHOS DESDE PODA
				NumeroAniosDesdePoda nadp = NumeroAniosDesdePoda.Factory.newInstance();
				nadp.setValor((new BigDecimal(valor.toString())).intValue());
				
				datosVariables.setNadp(nadp);
				break;
			case 117:
				//NUMERO UNIDADES
				NumeroUnidades numUds = NumeroUnidades.Factory.newInstance();
				numUds.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNumUnid(numUds);
				break;
			case 133:
				//PRACTICA CULTURAL
				PracticaCultural praCul = PracticaCultural.Factory.newInstance();
				praCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPraCult(praCul);
				break;
			case 131:
				//SISTEMA CONDUCCION
				SistemaConduccion sCond = SistemaConduccion.Factory.newInstance();
				sCond.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCond(sCond);
				break;
			case 123:
				//SISTEMA CULTIVO
				SistemaCultivo sCul = SistemaCultivo.Factory.newInstance();
				sCul.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisCult(sCul);
				break;
			case 616:
				//SISTEMA PRODUCCION
				SistemaProduccion sProd = SistemaProduccion.Factory.newInstance();
				sProd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProd(sProd);
				break;
			case 621:
				//SISTEMA PROTECCION
				SistemaProteccion sProt = SistemaProteccion.Factory.newInstance();
				sProt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setSisProt(sProt);
				break;
			case 116:
				//TIPO MARCO PLANTACION
				TipoMarcoPlantacion tmp = TipoMarcoPlantacion.Factory.newInstance();
				tmp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMcoPlant(tmp);
				break;
			case 173:
				//TIPO PLANTACION
				TipoPlantacion tp = TipoPlantacion.Factory.newInstance();
				tp.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipPlant(tp);
				break;
			case 132:
				//TIPO SUB.DECLARADA PARCEL
				List listaTsdp = new ArrayList();
				listaTsdp.add(valor);
				TipoSubvencionDeclaradaParcela tsdp = TipoSubvencionDeclaradaParcela.Factory.newInstance();
				tsdp.setValor(listaTsdp);
				
				datosVariables.setTipSubDecPar(tsdp);
				break;
			case 768:
				//VALOR FIJO
				ValorFijo vf = ValorFijo.Factory.newInstance();
				vf.setValor(new BigDecimal(valor.toString()));
				
				datosVariables.setValFij(vf);
				break;
			case 778:
				//Tipo instalacion
				TipoInstalacion tipInst = TipoInstalacion.Factory.newInstance();
				tipInst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipInst(tipInst);
				break;
			case 873:
				// Material Cubierta
				MaterialCubierta mCub = MaterialCubierta.Factory.newInstance();
				mCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatCubi(mCub);
				break;
			case 874:
				// Edad Cubierta
				EdadCubierta eCub = EdadCubierta.Factory.newInstance();
				eCub.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadCubi(eCub);
				break;
			case 875:
				// Material Estructuras
				MaterialEstructura mEst = MaterialEstructura.Factory.newInstance();
				mEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setMatEstr(mEst);
				break;
			case 876:
				// Edad Estrucutra
				EdadEstructura eEst = EdadEstructura.Factory.newInstance();
				eEst.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setEdadEstr(eEst);
				break;
			case 879:
				// Codigo Certificado
				CodigoCertificado cc = CodigoCertificado.Factory.newInstance();
				cc.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setCodCert(cc);
				break;
			case 752:
				// Tipo terreno
				TipoTerreno tt = TipoTerreno.Factory.newInstance();
				tt.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipTer(tt);
				break;
			case 753:
				// Tipo masa
				TipoMasa tm = TipoMasa.Factory.newInstance();
				tm.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setTipMas(tm);
				break;
			case 754:
				// Pendiente
				Pendiente p = Pendiente.Factory.newInstance();
				p.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setPend(p);
				break;
			case 944:
				// Num anhos desde descorche
				NumeroAniosDesdeDescorche nadd = NumeroAniosDesdeDescorche.Factory.newInstance();
				nadd.setValor(Integer.parseInt(valor.toString()));
				
				datosVariables.setNadd(nadd);
				break;
			//FIN DATOS VARIABLES QUE NO DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			default:
				//No hacemos nada
				break;
			
		}
		
	}
	
	/**
	 * Metodo para establecer los datos variables que dependen del concepto principal del modulo y del riesgo cubierto
	 * en el objeto 'DatosVariables'.
	 * @param codconcepto Codigo del concepto a anhadir
	 * @param codconceptoppalmod Concepto principal del modulo del cual depende el codconcepto (si procede). Puede ser null.
	 * @param codriesgocubierto Riesgo cubierto del cual depende el codconcepto (si procede). Puede ser null.
	 * @param valor Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String (Capital Asegurado)
	 * @param datosVariables Estructura donde se van anhadiendo los datos variables.
	 * @throws ParseException Error al parsear fechas.
	 */
	private static void setDatoVariableRiesgo(BigDecimal codconcepto,
			BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Object valor,
			DatosVariables datosVariables) throws ParseException, NumberFormatException {
		
		//Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		
		//Comienzo del tratamiento del dato variable: 157OK, 140OK, 137OK, 136OK, 134OK, 138OK, 135OK, 139OK

		switch (codconcepto.intValue()){
			//INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 140:
				//DIAS INICIO GARANTIAS
				List<DiasLimiteGarantias> lstDIG = Arrays.asList(datosVariables.getDIGarantArray());
				ArrayList<DiasLimiteGarantias> lstDIGA = new ArrayList<DiasLimiteGarantias>(lstDIG);

				DiasLimiteGarantias dLim = DiasLimiteGarantias.Factory.newInstance();
				dLim.setCodRCub(codriesgocubierto.intValue());
				dLim.setCPMod(codconceptoppalmod.intValue());
				dLim.setValor(Integer.parseInt(valor.toString()));
				lstDIGA.add(dLim);
				
				datosVariables.setDIGarantArray(lstDIGA.toArray(new DiasLimiteGarantias[lstDIGA.size()]));
				break;
			case 136:
				//DURACION MAX.GARANT(DIAS)
				List<DiasLimiteGarantias> lstDMGD = Arrays.asList(datosVariables.getDurMaxGarantDiasArray());
				ArrayList<DiasLimiteGarantias> lstDMGDA = new ArrayList<DiasLimiteGarantias>(lstDMGD);
				
				DiasLimiteGarantias durMaxD = DiasLimiteGarantias.Factory.newInstance();
				durMaxD.setCodRCub(codriesgocubierto.intValue());
				durMaxD.setCPMod(codconceptoppalmod.intValue());
				durMaxD.setValor(Integer.parseInt(valor.toString()));
				lstDMGDA.add(durMaxD);
				
				datosVariables.setDurMaxGarantDiasArray(lstDMGDA.toArray(new DiasLimiteGarantias[lstDMGDA.size()]));
				break;
			case 137:
				//DURACION MAX.GARAN(MESES)
				List<MesesLimiteGarantias> lstDMGM = Arrays.asList(datosVariables.getDurMaxGarantMesesArray());
				ArrayList<MesesLimiteGarantias> lstDMGMA = new ArrayList<MesesLimiteGarantias>(lstDMGM);
				
				MesesLimiteGarantias durMaxM = MesesLimiteGarantias.Factory.newInstance();
				durMaxM.setCodRCub(codriesgocubierto.intValue());
				durMaxM.setCPMod(codconceptoppalmod.intValue());
				durMaxM.setValor(new BigDecimal(valor.toString()));
				lstDMGMA.add(durMaxM);
				
				datosVariables.setDurMaxGarantMesesArray(lstDMGMA.toArray(new MesesLimiteGarantias[lstDMGMA.size()]));
				break;
			case 135:
				//EST.FEN.FIN GARANTIAS
				List<EstadoFenologicoLimiteGarantias> lstEFFG = Arrays.asList(datosVariables.getEFFGarantArray());
				ArrayList<EstadoFenologicoLimiteGarantias> lstEFFGA = new ArrayList<EstadoFenologicoLimiteGarantias>(lstEFFG);
				
				EstadoFenologicoLimiteGarantias estFenFin = EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenFin.setCodRCub(codriesgocubierto.intValue());
				estFenFin.setCPMod(codconceptoppalmod.intValue());
				estFenFin.setValor(valor+"");
				lstEFFGA.add(estFenFin);
				
				datosVariables.setEFFGarantArray(lstEFFGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFFGA.size()]));
				break;
			case 139:
				//EST.FEN.INICIO GARANTIAS
				List<EstadoFenologicoLimiteGarantias> lstEFIG = Arrays.asList(datosVariables.getEFIGarantArray());
				ArrayList<EstadoFenologicoLimiteGarantias> lstEFIGA = new ArrayList<EstadoFenologicoLimiteGarantias>(lstEFIG);

				EstadoFenologicoLimiteGarantias estFenIni = EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenIni.setCodRCub(codriesgocubierto.intValue());
				estFenIni.setCPMod(codconceptoppalmod.intValue());
				estFenIni.setValor(valor+"");
				lstEFIGA.add(estFenIni);
				
				datosVariables.setEFIGarantArray(lstEFIGA.toArray(new EstadoFenologicoLimiteGarantias[lstEFIGA.size()]));
				break;
			case 134:
				//FECHA FIN GARANTIAS
				List<FechaLimiteGarantias> lstFLG = Arrays.asList(datosVariables.getFecFGarantArray());
				ArrayList<FechaLimiteGarantias> lstFLGA = new ArrayList<FechaLimiteGarantias>(lstFLG);

				FechaLimiteGarantias fLim = FechaLimiteGarantias.Factory.newInstance();
				fLim.setCodRCub(codriesgocubierto.intValue());
				fLim.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFLG = new GregorianCalendar();
				gcFLG.setTime(sdf.parse(valor.toString()));
				fLim.setValor(gcFLG);
				//ASF - 5/9/2012 - Antes de anhadir la fecha de fin de garant铆as, comprobamos que no tengamos ya esa combinacion.
				boolean existe = false;
				for (FechaLimiteGarantias flg: lstFLGA){
					logger.debug("Elemento a anhadir: " + fLim.getCodRCub() + ", " + fLim.getCPMod() + ", " + fLim.getValor());
					logger.debug("Elemento de la lista: " + flg.getCodRCub() + ", " + flg.getCPMod() + ", " + flg.getValor());
					logger.debug("1. " + (flg.getCodRCub() == fLim.getCodRCub()));
					logger.debug("2. " + (flg.getCPMod() == fLim.getCPMod()));
					logger.debug("3. " + (flg.getValor().equals(fLim.getValor())));
					if (flg.getCodRCub() == fLim.getCodRCub() && flg.getCPMod() == fLim.getCPMod() && flg.getValor().equals(fLim.getValor())){
						existe = true;
						break;
					}
				}
				if (!existe){
					lstFLGA.add(fLim);
					datosVariables.setFecFGarantArray(lstFLGA.toArray(new FechaLimiteGarantias[lstFLGA.size()]));
				}
				break;
			case 138:
				//FECHA INICIO GARANTIAS
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
			case 141:
				//MESES INICIO GARANTIAS
				List<MesesLimiteGarantias> lstMIG = Arrays.asList(datosVariables.getMIGarantArray());
				ArrayList<MesesLimiteGarantias> lstMIGA = new ArrayList<MesesLimiteGarantias>(lstMIG);

				MesesLimiteGarantias migarant = MesesLimiteGarantias.Factory.newInstance();
				migarant.setCodRCub(codriesgocubierto.intValue());
				migarant.setCPMod(codconceptoppalmod.intValue());
				migarant.setValor(new BigDecimal(valor.toString()));
				lstMIGA.add(migarant);
				
				datosVariables.setMIGarantArray(lstMIGA.toArray(new MesesLimiteGarantias[lstMIGA.size()]));
				break;
			case 157:
				//PERIODO GARANTIAS
				List<PeriodoGarantias> lstPG = Arrays.asList(datosVariables.getPerGarantArray());
				ArrayList<PeriodoGarantias> lstPGA = new ArrayList<PeriodoGarantias>(lstPG);

				PeriodoGarantias perGarant = PeriodoGarantias.Factory.newInstance();
				perGarant.setCodRCub(codriesgocubierto.intValue());
				perGarant.setCPMod(codconceptoppalmod.intValue());
				perGarant.setValor(Integer.parseInt(valor.toString()));
				lstPGA.add(perGarant);
				
				datosVariables.setPerGarantArray(lstPGA.toArray(new PeriodoGarantias[lstPGA.size()]));
				break;
			default:
				//No hacemos nada
				break;
			
		}
		
	}
	
	/* Pet. 57626 ** MODIF TAM (16.06.2020) ** Inicio */
	/**
	 * Metodo para establecer los datos variables que dependen del concepto principal del modulo y del riesgo cubierto
	 * en el objeto 'DatosVariables'.
	 * @param codconcepto Codigo del concepto a anhadir
	 * @param codconceptoppalmod Concepto principal del modulo del cual depende el codconcepto (si procede). Puede ser null.
	 * @param codriesgocubierto Riesgo cubierto del cual depende el codconcepto (si procede). Puede ser null.
	 * @param valor Valor para codconcepto. Puede ser BigDecimal (Cobertura) o String (Capital Asegurado)
	 * @param datosVariables Estructura donde se van anhadiendo los datos variables.
	 * @throws ParseException Error al parsear fechas.
	 */
	private static void setDatoVariableRiesgoUnif(BigDecimal codconcepto,
			BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, Object valor,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables) throws ParseException, NumberFormatException {
		
		//Para el parseo de fechas
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		
		//Comienzo del tratamiento del dato variable: 157OK, 140OK, 137OK, 136OK, 134OK, 138OK, 135OK, 139OK

		switch (codconcepto.intValue()){
			//INICIO DATOS VARIABLES QUE DEPENDEN DEL CONCEPTO PRINCIPAL DEL MODULO Y DEL RIESGO CUBIERTO
			case 140:
				//DIAS INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDIG = Arrays.asList(datosVariables.getDIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias>(lstDIG);

				es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias dLim = es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias.Factory.newInstance();
				dLim.setCodRCub(codriesgocubierto.intValue());
				dLim.setCPMod(codconceptoppalmod.intValue());
				dLim.setValor(Integer.parseInt(valor.toString()));
				lstDIGA.add(dLim);
				
				datosVariables.setDIGarantArray(lstDIGA.toArray(new es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias[lstDIGA.size()]));
				break;
			case 136:
				//DURACION MAX.GARANT(DIAS)
				List<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDMGD = Arrays.asList(datosVariables.getDurMaxGarantDiasArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias> lstDMGDA = new ArrayList<es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias>(lstDMGD);
				
				es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias durMaxD = es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias.Factory.newInstance();
				durMaxD.setCodRCub(codriesgocubierto.intValue());
				durMaxD.setCPMod(codconceptoppalmod.intValue());
				durMaxD.setValor(Integer.parseInt(valor.toString()));
				lstDMGDA.add(durMaxD);
				
				datosVariables.setDurMaxGarantDiasArray(lstDMGDA.toArray(new es.agroseguro.contratacion.datosVariables.DiasLimiteGarantias[lstDMGDA.size()]));
				break;
			case 137:
				//DURACION MAX.GARAN(MESES)
				List<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstDMGM = Arrays.asList(datosVariables.getDurMaxGarantMesesArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstDMGMA = new ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias>(lstDMGM);
				
				es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias durMaxM = es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias.Factory.newInstance();
				durMaxM.setCodRCub(codriesgocubierto.intValue());
				durMaxM.setCPMod(codconceptoppalmod.intValue());
				durMaxM.setValor(new BigDecimal(valor.toString()));
				lstDMGMA.add(durMaxM);
				
				datosVariables.setDurMaxGarantMesesArray(lstDMGMA.toArray(new es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias[lstDMGMA.size()]));
				break;
			case 135:
				//EST.FEN.FIN GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFFG = Arrays.asList(datosVariables.getEFFGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFFGA = new ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias>(lstEFFG);
				
				es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias estFenFin = es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenFin.setCodRCub(codriesgocubierto.intValue());
				estFenFin.setCPMod(codconceptoppalmod.intValue());
				estFenFin.setValor(valor+"");
				lstEFFGA.add(estFenFin);
				
				datosVariables.setEFFGarantArray(lstEFFGA.toArray(new es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias[lstEFFGA.size()]));
				break;
			case 139:
				//EST.FEN.INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFIG = Arrays.asList(datosVariables.getEFIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias> lstEFIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias>(lstEFIG);

				es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias estFenIni = es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias.Factory.newInstance();
				estFenIni.setCodRCub(codriesgocubierto.intValue());
				estFenIni.setCPMod(codconceptoppalmod.intValue());
				estFenIni.setValor(valor+"");
				lstEFIGA.add(estFenIni);
				
				datosVariables.setEFIGarantArray(lstEFIGA.toArray(new es.agroseguro.contratacion.datosVariables.EstadoFenologicoLimiteGarantias[lstEFIGA.size()]));
				break;
			case 134:
				//FECHA FIN GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFLG = Arrays.asList(datosVariables.getFecFGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFLGA = new ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias>(lstFLG);

				es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias fLim = es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias.Factory.newInstance();
				fLim.setCodRCub(codriesgocubierto.intValue());
				fLim.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFLG = new GregorianCalendar();
				gcFLG.setTime(sdf.parse(valor.toString()));
				fLim.setValor(gcFLG);
				//ASF - 5/9/2012 - Antes de anhadir la fecha de fin de garant铆as, comprobamos que no tengamos ya esa combinacion.
				boolean existe = false;
				for (es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias flg: lstFLGA){
					logger.debug("Elemento a anhadir: " + fLim.getCodRCub() + ", " + fLim.getCPMod() + ", " + fLim.getValor());
					logger.debug("Elemento de la lista: " + flg.getCodRCub() + ", " + flg.getCPMod() + ", " + flg.getValor());
					logger.debug("1. " + (flg.getCodRCub() == fLim.getCodRCub()));
					logger.debug("2. " + (flg.getCPMod() == fLim.getCPMod()));
					logger.debug("3. " + (flg.getValor().equals(fLim.getValor())));
					if (flg.getCodRCub() == fLim.getCodRCub() && flg.getCPMod() == fLim.getCPMod() && flg.getValor().equals(fLim.getValor())){
						existe = true;
						break;
					}
				}
				if (!existe){
					lstFLGA.add(fLim);
					datosVariables.setFecFGarantArray(lstFLGA.toArray(new es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias[lstFLGA.size()]));
				}
				break;
			case 138:
				//FECHA INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFIG = Arrays.asList(datosVariables.getFecIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias> lstFIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias>(lstFIG);

				es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias fIni = es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias.Factory.newInstance();
				fIni.setCodRCub(codriesgocubierto.intValue());
				fIni.setCPMod(codconceptoppalmod.intValue());
				GregorianCalendar gcFIG = new GregorianCalendar();
				gcFIG.setTime(sdf.parse(valor.toString()));
				fIni.setValor(gcFIG);
				lstFIGA.add(fIni);
				
				datosVariables.setFecIGarantArray(lstFIGA.toArray(new es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias[lstFIGA.size()]));
				break;
			case 141:
				//MESES INICIO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstMIG = Arrays.asList(datosVariables.getMIGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias> lstMIGA = new ArrayList<es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias>(lstMIG);

				es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias migarant = es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias.Factory.newInstance();
				migarant.setCodRCub(codriesgocubierto.intValue());
				migarant.setCPMod(codconceptoppalmod.intValue());
				migarant.setValor(new BigDecimal(valor.toString()));
				lstMIGA.add(migarant);
				
				datosVariables.setMIGarantArray(lstMIGA.toArray(new es.agroseguro.contratacion.datosVariables.MesesLimiteGarantias[lstMIGA.size()]));
				break;
			case 157:
				//PERIODO GARANTIAS
				List<es.agroseguro.contratacion.datosVariables.PeriodoGarantias> lstPG = Arrays.asList(datosVariables.getPerGarantArray());
				ArrayList<es.agroseguro.contratacion.datosVariables.PeriodoGarantias> lstPGA = new ArrayList<es.agroseguro.contratacion.datosVariables.PeriodoGarantias>(lstPG);

				es.agroseguro.contratacion.datosVariables.PeriodoGarantias perGarant = es.agroseguro.contratacion.datosVariables.PeriodoGarantias.Factory.newInstance();
				perGarant.setCodRCub(codriesgocubierto.intValue());
				perGarant.setCPMod(codconceptoppalmod.intValue());
				perGarant.setValor(Integer.parseInt(valor.toString()));
				lstPGA.add(perGarant);
				
				datosVariables.setPerGarantArray(lstPGA.toArray(new es.agroseguro.contratacion.datosVariables.PeriodoGarantias[lstPGA.size()]));
				break;
			default:
				//No hacemos nada
				break;
			
		}
		
	}
	
	private static es.agroseguro.contratacion.Entidad getEntidad(final com.rsi.agp.dao.tables.poliza.Poliza poliza){ 
		 
		es.agroseguro.contratacion.Entidad entidad = es.agroseguro.contratacion.Entidad.Factory.newInstance(); 
		
		entidad.setCodigo(Constants.ENTIDAD_C616); 
		entidad.setCodigoInterno(PolizaTransformer.getCodigoInterno (poliza)); 
		 
		if (poliza.getColectivo().getSubentidadMediadora() != null) { 
			es.agroseguro.contratacion.Mediador mediador = es.agroseguro.contratacion.Mediador.Factory.newInstance(); 
			// DAA 18/12/2013 TIPO MEDIADOR 
			SubentidadMediadora subentidad = poliza.getColectivo() 
					.getSubentidadMediadora(); 
			try { 
				if (subentidad != null && subentidad.getTipoMediadorAgro() != null && subentidad.getTipoMediadorAgro().getId() != null) { 
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
			
			} else if(subentidad.getTipoidentificacion().equals("CIF")) { 
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
	
	/* Pet. 57626 ** MODIF TAM (16.06.2020) ** Fin */
	
	
	/*****/
	/* Pet. 78877 ** MODIF TAM (05.11.2021) ** Inicio */
	private static es.agroseguro.contratacion.datosVariables.DatosVariables getDatosVarCobertRendimientos(com.rsi.agp.dao.tables.poliza.Poliza poliza,
			String codModulo, List<BigDecimal> listaCPM) throws Exception {

		logger.debug("AnexoModificacionTransformer - getDatosVarCobertRendimientos [INIT]");
		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = es.agroseguro.contratacion.datosVariables.DatosVariables.Factory.newInstance();

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

					List<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCE = Arrays.asList(datosVariables.getRiesgCbtoElegArray());
					ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido> lstRCEA = new ArrayList<es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido>(lstRCE);

					es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCubEleg = es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido.Factory.newInstance();
					rCubEleg.setCodRCub(codRiesgoCubierto.intValue());
					rCubEleg.setCPMod(codconceptoppalmod.intValue());
					rCubEleg.setValor(valor);
					lstRCEA.add(rCubEleg);
					datosVariables.setRiesgCbtoElegArray(lstRCEA.toArray(new es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[lstRCEA.size()]));

				}

			}

		}
		logger.debug("AnexoModificacionTransformer - getDatosVarCobertRendimientos [END]");
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
	
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	public synchronized void setClaseManager(ClaseManager claseManager) {
		AnexoModificacionTransformer.claseManager = claseManager;
	}

	public synchronized void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		AnexoModificacionTransformer.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
	
	public synchronized void setCuadroCoberturasManager(ICuadroCoberturasManager cuadroCoberturasManager) {
		AnexoModificacionTransformer.cuadroCoberturasManager = cuadroCoberturasManager;
	}

	public synchronized final void setSeleccionPolizaDao(final ISeleccionPolizaDao seleccionPolizaDao) {
		AnexoModificacionTransformer.seleccionPolizaDao = seleccionPolizaDao;
	}
	/* Pet. 78877 ** MODIF TAM (05.11.2021) ** Fin */
	
	
	/** SONAR Q ** MODIF TAM(12.11.2021) ** INICIO **/
	/* Se declaran nuevos metodos para descargar otros metodos de if y fors */
	private static String obtenerCodInterno( com.rsi.agp.dao.tables.copy.Poliza copy, AnexoModificacion anexo){
		String codInterno = "";
		//Cogemos el codigo interno de la copy, y si no lo tiene, lo sacamos de la poliza
		if (!("").equals(StringUtils.nullToString(copy.getCodinternoentidad())))
			codInterno = copy.getCodinternoentidad();
		else{
			//De la poliza
			codInterno = String.format("%4s", anexo.getPoliza().getColectivo().getSubentidadMediadora().getId().getCodentidad()) +
				// Esta linea es para el caso de que no puedan venir espacios en blanco	
				//String.format("%04d", Integer.parseInt(poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad().toString()));
				String.format("%4s", anexo.getPoliza().getColectivo().getSubentidadMediadora().getId().getCodsubentidad());
		}
		return codInterno;	
	}
	
	private static Entidad informarEntidad(Entidad entidad, AnexoModificacion anexo, String codInterno) {
		
		entidad.setCodigoInterno(String.format("%12s", codInterno));
		
		if (anexo.getPoliza().getColectivo().getSubentidadMediadora() != null){
			Mediador mediador = Mediador.Factory.newInstance();
			//DAA 18/12/2013 TIPO MEDIADOR
			TransformerUtil.setAttrTipoMediadorAnexo(anexo.getPoliza().getColectivo().getSubentidadMediadora(), mediador);
			//DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			TransformerUtil.setAttrNombreRSMediadorAnexo(anexo.getPoliza().getColectivo().getSubentidadMediadora(), mediador);
			entidad.setMediador(mediador);
		}
		
		if (anexo.getPoliza().isPlanMayorIgual2015()) {
			es.agroseguro.seguroAgrario.modificacion.Gastos gastos = setGastos (anexo);
			entidad.setGastos(gastos);
		}
		return entidad;
		
	}
	
	private static Entidad informarEnt(AnexoModificacion anexo, Entidad entidad) {

		if (anexo.getPoliza().getColectivo().getSubentidadMediadora() != null){
			Mediador mediador = Mediador.Factory.newInstance();
			//DAA 18/12/2013 TIPO MEDIADOR
			TransformerUtil.setAttrTipoMediadorAnexo(anexo.getPoliza().getColectivo().getSubentidadMediadora(), mediador);
			//DAA 18/12/2013 NOMBRE O RAZON SOCIAL MEDIADOR
			TransformerUtil.setAttrNombreRSMediadorAnexo(anexo.getPoliza().getColectivo().getSubentidadMediadora(), mediador);
			entidad.setMediador(mediador);
		}
		if (anexo.getPoliza().isPlanMayorIgual2015()) {
			es.agroseguro.seguroAgrario.modificacion.Gastos gastos = setGastos (anexo);
			entidad.setGastos(gastos);
		}
		return entidad;
	}
	
	private static Asegurado informarAsegurado (Asegurado a, AnexoModificacion anexo) {
		a.setNif(anexo.getPoliza().getAsegurado().getNifcif());
		if (anexo.getPoliza().getAsegurado().getTipoidentificacion().equals("CIF")){
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(anexo.getRazsocaseg());
			a.setRazonSocial(rs);
		}else{
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(anexo.getNomaseg()); 
			nom.setApellido1(anexo.getApel1aseg());
		
			// Control de nulos del apellido 2
			if (!"".equals(StringUtils.nullToString(anexo.getApel2aseg()).trim())) {
				nom.setApellido2(anexo.getApel2aseg());
			}
		
			a.setNombreApellidos(nom);
		}
		return a;
	}
	
	private static Cobertura informarCobertura(Cobertura cobertura, AnexoModificacion anexo, com.rsi.agp.dao.tables.copy.Poliza copy){

		if (!StringUtils.isNullOrEmpty(anexo.getCodmodulo()) && !StringUtils.nullToString(anexo.getCodmodulo().trim()).equals("")) {
			cobertura.setModulo(String.format("%-5s", anexo.getCodmodulo().trim()));
		}else if (copy != null) {
			cobertura.setModulo(String.format("%-5s", copy.getCodmodulo().trim()));
		}else {
			cobertura.setModulo(String.format("%-5s", anexo.getPoliza().getCodmodulo().trim()));
		}
		return cobertura;
	}
	
	private static Poliza informarSubvencionesDecl(AnexoModificacion anexo, Poliza polizaAS) {
		
		//SUBVENCIONES DECLARADAS
		boolean anadirSeguridadSocial = false;
		
		if (anexo.getSubvDeclaradas() != null && anexo.getSubvDeclaradas().size() > 0){
			SubvencionesDeclaradas subvDecl = SubvencionesDeclaradas.Factory.newInstance();
			
			ArrayList<SubvencionDeclarada> lstSubv = new ArrayList<SubvencionDeclarada>();
			for(SubvDeclarada subv: anexo.getSubvDeclaradas()) {
				if (!StringUtils.nullToString(subv.getTipomodificacion()).equals("B")) {
					SubvencionDeclarada sd = SubvencionDeclarada.Factory.newInstance();
					sd.setTipo(subv.getCodsubvencion().intValue());
					sd.setTipoModificacion(StringUtils.nullToString(subv.getTipomodificacion()) + "");
					lstSubv.add(sd);
					if (subv.getCodsubvencion().compareTo(Constants.SUBVENCION20) == 0 || 
						subv.getCodsubvencion().compareTo(Constants.SUBVENCION30) == 0) {
						anadirSeguridadSocial = true;
						// En el caso de que la poliza no tuviera esas subvenciones, es necesario generar en el anexo la SS
						if (StringUtils.nullToString(anexo.getNumsegsocial()).equals("") && StringUtils.nullToString(subv.getTipomodificacion()).equals("A")) {
							anexo.setNumsegsocial(StringUtils.nullToString(anexo.getPoliza().getAsegurado().getNumsegsocial()));
							anexo.setRegimensegsocial(StringUtils.nullToString(anexo.getPoliza().getAsegurado().getRegimensegsocial()));
						}
					}
				}
			}
			if (anadirSeguridadSocial) {				
				if (!StringUtils.nullToString(anexo.getNumsegsocial()).equals("")){
					SeguridadSocial segSocial = SeguridadSocial.Factory.newInstance();
					segSocial.setProvincia(Integer.parseInt(anexo.getNumsegsocial().substring(0, 2)));
					segSocial.setNumero(Integer.parseInt(anexo.getNumsegsocial().substring(2, 10)));
					segSocial.setCodigo(anexo.getNumsegsocial().substring(10));
					segSocial.setRegimen(Short.parseShort(anexo.getRegimensegsocial()+""));
					subvDecl.setSeguridadSocial(segSocial);
				}
			}
			
			if (lstSubv.size() > 0){
				subvDecl.setSubvencionDeclaradaArray(lstSubv.toArray(new SubvencionDeclarada[lstSubv.size()]));
				polizaAS.setSubvencionesDeclaradas(subvDecl);
			} else if (polizaAS.getSubvencionesDeclaradas() != null) {
				polizaAS.unsetSubvencionesDeclaradas();
			}
		}
		
		//FIN SUBVENCIONES DECLARADAS
		
		return polizaAS;
	}
	
	private static DatosContacto informarDatContacto(AnexoModificacion anexo, DatosContacto dContacto) {
		dContacto.setEmail(anexo.getEmail());
		if (!StringUtils.nullToString(anexo.getTelffijoaseg()).equals("")) {
			dContacto.setTelefonoFijo(Integer.parseInt(anexo.getTelffijoaseg()));
		}
			
		if (!StringUtils.nullToString(anexo.getTelfmovilaseg()).equals("")) {
			dContacto.setTelefonoMovil(Integer.parseInt(anexo.getTelfmovilaseg()));
		}
		return dContacto;
	}
	
	/* SONAR Q */
	private static es.agroseguro.contratacion.Poliza informarAsegenPolizaAs(AnexoModificacion anexo, 
														es.agroseguro.contratacion.Poliza polizaAS) {
		
		// ASEGURADO
		if (!StringUtils.nullToString(anexo.getNomaseg()).equals("")
				|| !StringUtils.nullToString(anexo.getRazsocaseg()).equals("")) {
			// Si se ha modificado el asegurado, lo anhado al anexo de
			// modificacion
			es.agroseguro.contratacion.Asegurado a = es.agroseguro.contratacion.Asegurado.Factory.newInstance();
			a.setNif(anexo.getPoliza().getAsegurado().getNifcif());
			if (anexo.getPoliza().getAsegurado().getTipoidentificacion()
					.equals("CIF")) {
				RazonSocial rs = RazonSocial.Factory.newInstance();
				rs.setRazonSocial(anexo.getRazsocaseg());
				a.setRazonSocial(rs);
			} else {
				NombreApellidos nom = NombreApellidos.Factory.newInstance();
				nom.setNombre(anexo.getNomaseg());
				nom.setApellido1(anexo.getApel1aseg());
	
				// Control de nulos del apellido 2
				if (!"".equals(StringUtils.nullToString(anexo.getApel2aseg()).trim())) {
					nom.setApellido2(anexo.getApel2aseg());
				}
	
				a.setNombreApellidos(nom);
			}
	
			Direccion dir = Direccion.Factory.newInstance();
			dir.setBloque(anexo.getBloqueaseg());
			dir.setCp(anexo.getCodposasegstr());
			dir.setEscalera(anexo.getEscaseg());
			dir.setLocalidad(anexo.getNomlocalidad());
			dir.setNumero(anexo.getNumaseg());
			dir.setPiso(anexo.getPisoaseg());
			dir.setProvincia(anexo.getCodprovincia().intValue());
			dir.setVia(anexo.getCalleaseg());
			a.setDireccion(dir);
	
			DatosContacto dContacto = DatosContacto.Factory.newInstance();
			dContacto.setEmail(anexo.getEmail());
			if (!StringUtils.nullToString(anexo.getTelffijoaseg()).equals(""))
				dContacto.setTelefonoFijo(Integer.parseInt(anexo
						.getTelffijoaseg()));
			if (!StringUtils.nullToString(anexo.getTelfmovilaseg()).equals(""))
				dContacto.setTelefonoMovil(Integer.parseInt(anexo
						.getTelfmovilaseg()));
			a.setDatosContacto(dContacto);
	
			polizaAS.setAsegurado(a);
		}
		// FIN ASEGURADO
		
		return polizaAS;
	}
	/* SONAR Q */
	private static Poliza informarPolizaDeCopy(com.rsi.agp.dao.tables.copy.Poliza copy, Poliza polizaAS,
											Entidad entidad, List<String> lstAsunto, Colectivo c, GregorianCalendar gc,
											AnexoModificacion anexo, Usuario usuario) {
		//Rellenamos el anexo con los datos de la copy
		polizaAS.setPlan(Integer.parseInt(copy.getCodplan()));
		polizaAS.setLinea(copy.getCodlinea().intValue());
		polizaAS.setReferencia(copy.getRefpoliza());
		polizaAS.setDigitoControl(copy.getDcpoliza().intValue());
		polizaAS.setFechaFirmaDocumento(gc);
		polizaAS.setAsunto(lstAsunto);
		polizaAS.setModulo(getModuloCopy (copy.getCodmodulo().trim()));
		
		//COLECTIVO
		for (com.rsi.agp.dao.tables.copy.Colectivo col : copy.getColectivos()){
			c.setDigitoControl(col.getDccolectivo().intValue());
			c.setNif(col.getCifniftomador());
			c.setReferencia(col.getRefcolectivo());
			polizaAS.setColectivo(c);
			break;
		}
		//FIN COLECTIVO
		
		//ENTIDAD
		entidad.setCodigo(copy.getCodentidadaseg());
		String codInterno = copy.getCodinternoentidad();
		//Cogemos el codigo interno de la copy, y si no lo tiene, lo sacamos de la poliza
		if (!StringUtils.nullToString(codInterno).equals(""))
			entidad.setCodigoInterno(String.format("%12s", codInterno));
		else{
			//De la poliza
			codInterno = String.format("%4s", anexo.getPoliza().getAsegurado().getEntidad().getCodentidad().toString()) 
					+ String.format("%4s", usuario.getOficina().getId().getCodoficina().toString());
			entidad.setCodigoInterno(String.format("%12s", codInterno));
		}
		//GASTOS
		if (anexo.getPoliza().isPlanMayorIgual2015()) {
			es.agroseguro.seguroAgrario.modificacion.Gastos gastos = setGastos (anexo);
			entidad.setGastos(gastos);
		}
		polizaAS.setEntidad(entidad);
		//FIN ENTIDAD
		
		return polizaAS;
	}
	
	/* SONAR Q */
	private static Direccion informarDireccion(AnexoModificacion anexo) {
	
		Direccion dir = Direccion.Factory.newInstance();
		if(anexo.getBloqueaseg() != null)
			dir.setBloque(anexo.getBloqueaseg());
		
		if(anexo.getEscaseg() != null)
			dir.setEscalera(anexo.getEscaseg());
		
		dir.setCp(anexo.getCodposasegstr());
		dir.setLocalidad(anexo.getNomlocalidad());
		dir.setNumero(anexo.getNumaseg());
		dir.setPiso(anexo.getPisoaseg());
		dir.setProvincia(anexo.getCodprovincia().intValue());
		dir.setVia(anexo.getCalleaseg());
		
		return dir;
	}

	private static DatosContacto informardContacto(AnexoModificacion anexo) {
		DatosContacto dContacto = DatosContacto.Factory.newInstance();
		if(anexo.getEmail() != null) {
			dContacto.setEmail(anexo.getEmail());
		}
			
		dContacto.setTelefonoFijo(Integer.parseInt(anexo.getTelffijoaseg()));
			
		if (!StringUtils.nullToString(anexo.getTelfmovilaseg()).equals("")) {
				dContacto.setTelefonoMovil(Integer.parseInt(anexo.getTelfmovilaseg()));
		}
		return dContacto;
	}
	
	private static es.agroseguro.contratacion.parcela.CapitalAsegurado[] informarCapitales(com.rsi.agp.dao.tables.anexo.Parcela parcela, 
																		Map<BigDecimal, List<String>> listaDatosVariables,List<BigDecimal> listaCPM,	
																		List<BigDecimal> codsConceptos, boolean calcRendOriHist) throws Exception{
		
		
		es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitales = new es.agroseguro.contratacion.parcela.CapitalAsegurado[parcela.getCapitalAsegurados().size()];
		int cntCapAseg = 0;
		
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado ca : parcela.getCapitalAsegurados()) {
			
			es.agroseguro.contratacion.parcela.CapitalAsegurado c = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory.newInstance();
			
			if(ca.getPrecio()!=null){
				c.setPrecio(ca.getPrecio());
			}else{
				c.setPrecio(new BigDecimal(0));
			}
			
			if(ca.getProduccion()!=null){
				c.setProduccion(ca.getProduccion().intValue());
			}else{
				c.setProduccion(0);
			}
			c.setSuperficie(ca.getSuperficie());
			c.setTipo(ca.getTipoCapital().getCodtipocapital()
					.intValue());
			// Obtenemos los datos variables de parcela y de
			// coberturas a nivel de parcela
			if (calcRendOriHist) {
				c.setDatosVariables(getDatosVariablesParcelaParaEnvioWSRecOrientHist(
						ca.getCapitalDTSVariables(), ca.getId(),
						listaDatosVariables, listaCPM, codsConceptos,
						parcela ));

			}else {
				c.setDatosVariables(getDatosVariablesParcelaParaEnvioWSRecalculo(
						ca.getCapitalDTSVariables(), ca.getId(),
						listaDatosVariables, listaCPM, codsConceptos,
						parcela ));
				
			}
	
									
			capitales[cntCapAseg] = c;
			cntCapAseg++;
		}
		return capitales;
	}
	
	/* SONAR Q */
	private static List<CapitalAsegurado> informarCapAsegCPL(com.rsi.agp.dao.tables.anexo.Parcela parcela, 
						Map<BigDecimal, List<String>> listaDatosVariables, 
						List<BigDecimal> listaCPM, List<BigDecimal> codsConceptos) throws ValidacionAnexoModificacionException{
	
		List<CapitalAsegurado> capitales = new ArrayList<CapitalAsegurado>();
	
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado ca : parcela.getCapitalAsegurados()){
			
			if(ca.getTipomodificacion() != null){
				CapitalAsegurado c = CapitalAsegurado.Factory.newInstance();
				c.setPrecio(ca.getPrecio());
				if(ca.getIncrementoproduccion() != null)
					c.setProduccion(ca.getIncrementoproduccion().intValue());
				else
					c.setProduccion(0);
				c.setSuperficie(ca.getSuperficie());
				c.setTipo(ca.getTipoCapital().getCodtipocapital().intValue());
				//Obtenemos los datos variables de parcela y de coberturas a nivel de parcela
				c.setDatosVariablesArray(getDatosVariablesParcelaCPL(ca.getCapitalDTSVariables(), 
						ca.getId(),ca.getTipomodificacion(),listaDatosVariables, listaCPM, codsConceptos));
				capitales.add(c);
			}
			
		}
		return capitales;
	}

	private static Parcela[] obtenerParc(List<Parcela> lstParcelas) {
		Parcela[] parc = new Parcela[lstParcelas.size()];
		if (lstParcelas.size() > 0){
			int cntParcelas = 0;
			for (Parcela par : lstParcelas){
				parc[cntParcelas] = par;
				cntParcelas++;
			}
		}else{
			parc = null;
		}
		return parc;
	}
	
	private static DatosVariables[] obtenerArrDatVar(DatosVariables datosVariablesAlta, 
													 DatosVariables datosVariablesModif, 
													 DatosVariables datosVariablesBaja, 
													 int contador) {
		DatosVariables[] arrDatVar = new DatosVariables[contador];
		contador = 0;
		if(datosVariablesAlta!= null){
			arrDatVar[contador] = datosVariablesAlta;
			contador++;
		}
		if(datosVariablesModif!= null){
			arrDatVar[contador] = datosVariablesModif;
			contador++;
		}
		if(datosVariablesBaja!= null){
			arrDatVar[contador] = datosVariablesBaja;
			contador++;
		}
		return arrDatVar;
	}
	
	/*SONAR Q */
	private static Map<String, Object> setDatoVariableRiesgAltModBaj(CapitalDTSVariable dvp, String[] auxValores, DatosVariables datosVariablesAlta, 
											   DatosVariables datosVariablesModif, DatosVariables datosVariablesBaja, 
											   int contador, List<BigDecimal> listaCPM,
											   com.rsi.agp.dao.tables.anexo.Parcela parcela) throws NumberFormatException, ParseException {
		
		Map<String, Object> parametros = new HashMap<String, Object>(); 
		
		if (new Character('A').equals(dvp.getTipomodificacion())){
			if(datosVariablesAlta == null){
				datosVariablesAlta = DatosVariables.Factory.newInstance();
				datosVariablesAlta.setTipoModificacion("A");
				contador++;
			}
			// Comprueba si hay que insertar el CPM como dato variable				
			if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(), listaCPM)) {								
				setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariablesAlta);
			}
		 	
		}else if (new Character('M').equals(dvp.getTipomodificacion()) ||
			 new Character('M').equals(parcela.getTipomodificacion())){
			// Si el dato variable o la parcela correspondiente se ha modificado
	
			if(datosVariablesModif == null){
				datosVariablesModif = DatosVariables.Factory.newInstance();
				datosVariablesModif.setTipoModificacion("M");
				contador++;
			}
			// Comprueba si hay que insertar el CPM como dato variable				
			if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(), listaCPM)) {								
				setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]),new BigDecimal(auxValores[2]), auxValores[3], datosVariablesModif);
			}
		}else if (new Character('B').equals(dvp.getTipomodificacion())){
			if(datosVariablesBaja == null){
				datosVariablesBaja = DatosVariables.Factory.newInstance();
				datosVariablesBaja.setTipoModificacion("B");
				contador++;
			}
			// Comprueba si hay que insertar el CPM como dato variable				
			if (CoberturasUtils.isCPMPermitido(new BigDecimal(auxValores[1]), dvp.getCodconcepto(), listaCPM)) {								
				setDatoVariableRiesgo(dvp.getCodconcepto(), new BigDecimal(auxValores[1]), new BigDecimal(auxValores[2]), auxValores[3], datosVariablesBaja);
			}
		}
		
		parametros.put("datosVariablesAlta", datosVariablesAlta);
		parametros.put("datosVariablesModif", datosVariablesModif);
		parametros.put("datosVariablesBaja", datosVariablesBaja);
		parametros.put("contador", contador);
		
		return parametros;
	}
	
	/* SONAR Q */
	private static Map<String, Object> setDatoVariableAltModBaj(CapitalDTSVariable dvp, DatosVariables datosVariablesAlta, 
			   DatosVariables datosVariablesModif, DatosVariables datosVariablesBaja, 
			   int contador, List<BigDecimal> listaCPM,
			   com.rsi.agp.dao.tables.anexo.Parcela parcela) throws NumberFormatException, ParseException {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		if (new Character('A').equals(dvp.getTipomodificacion())){
			
			if(datosVariablesAlta == null){
				datosVariablesAlta = DatosVariables.Factory.newInstance();
				datosVariablesAlta.setTipoModificacion("A");
				contador++;
			}
			
			BigDecimal codConceptoPpalMod = null;
			if(dvp.getCodconceptoppalmod()!=null){
				codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
			}
			BigDecimal codRiesgoCubierto = null;
			if(dvp.getCodriesgocubierto()!=null){
				codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
			}
			
			// Comprueba si hay que insertar el CPM como dato variable				
			if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(), listaCPM)) {								
				setDatoVariable(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariablesAlta);
			}
		}
		else if (new Character('M').equals(dvp.getTipomodificacion()) || 
				 new Character('M').equals(parcela.getTipomodificacion())){
			if(datosVariablesModif == null){
				datosVariablesModif = DatosVariables.Factory.newInstance();
				datosVariablesModif.setTipoModificacion("M");
				contador++;
			}
			
			BigDecimal codConceptoPpalMod = null;
			if(dvp.getCodconceptoppalmod()!=null){
				codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
			}
			BigDecimal codRiesgoCubierto = null;
			if(dvp.getCodriesgocubierto()!=null){
				codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
			}
			
			// Comprueba si hay que insertar el CPM como dato variable				
			if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(),listaCPM)) {								
				setDatoVariable(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariablesModif);
			}
		}
		else if (new Character('B').equals(dvp.getTipomodificacion())){
			if(datosVariablesBaja == null){
				datosVariablesBaja = DatosVariables.Factory.newInstance();
				datosVariablesBaja.setTipoModificacion("B");
				contador++;
			}
			
			BigDecimal codConceptoPpalMod = null;
			if(dvp.getCodconceptoppalmod()!=null){
				codConceptoPpalMod = new BigDecimal(dvp.getCodconceptoppalmod());
			}
			BigDecimal codRiesgoCubierto = null;
			if(dvp.getCodriesgocubierto()!=null){
				codRiesgoCubierto = new BigDecimal(dvp.getCodriesgocubierto());
			}
			
			// Comprueba si hay que insertar el CPM como dato variable				
			if (CoberturasUtils.isCPMPermitido(codConceptoPpalMod, dvp.getCodconcepto(),listaCPM)) {								
				setDatoVariable(dvp.getCodconcepto(), codConceptoPpalMod, codRiesgoCubierto, dvp.getValor(), datosVariablesBaja);
			}
		}
		
		parametros.put("datosVariablesAlta", datosVariablesAlta);
		parametros.put("datosVariablesModif", datosVariablesModif);
		parametros.put("datosVariablesBaja", datosVariablesBaja);
		parametros.put("contador", contador);
		
		return parametros;
	}
	
	/** SONAR Q ** MODIF TAM(12.11.2021)  ** FIN **/
	
	/**
	 * Anhade la 'C' al modulo de la copy en el caso de que esta no lo tenga
	 * @param codModulo
	 * @return
	 */
	private static String getModuloCopy (String codModulo) {
		return String.format("%-5s", codModulo.contains("C") ? (codModulo.trim()) : ("C" + codModulo.trim()));
	}

	public synchronized void setParcelaDao(IParcelaDao parcelaDao) {
		AnexoModificacionTransformer.parcelaDao = parcelaDao;
	}
}